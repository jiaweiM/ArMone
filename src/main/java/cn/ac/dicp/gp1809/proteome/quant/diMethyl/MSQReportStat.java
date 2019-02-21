/* 
 ******************************************************************************
 * File:ReportStat.java * * * Created on 2010-4-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.diMethyl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.diMethyl.MSQReport.QInfo;

import jxl.JXLException;

/**
 * @author ck
 *
 * @version 2010-4-14, 14:38:49
 */
public class MSQReportStat {

	private String dir;
	private String [] fileName;
	private int fileNum;

	private HashMap <String,QInfo> [] infoMap;
	private HashSet <String> totalSet;
	
	public DecimalFormat dfPer= new DecimalFormat(".###%");
	public int [] summary;
	public int totalNum;
	
	public MSQReportStat(String dir) throws Exception{
		this.dir = dir;
		validate();
		fileNum = fileName.length;
		summary = new int [fileNum];
	}
	
	public void validate() throws Exception{
		File file = new File(dir);
		ArrayList <String> repoList = new ArrayList<String>();
		String [] txtList = null;
		if(file.isDirectory())
			txtList=file.list();
		
		for(int i=0;i<txtList.length;i++){
			if(txtList[i].endsWith(".xls")){
				repoList.add(txtList[i]);
			}
		}
		
		if(repoList.size()==0)
			throw new Exception("There is no .xls file in "+dir);
		
		fileName = repoList.toArray(new String[repoList.size()]);
	}
	
	public void getStatInfo() throws IOException, JXLException{
		totalSet = new HashSet <String>();
		infoMap = new HashMap [fileNum];
		for(int i=0;i<fileNum;i++){
			MSQReport r = new MSQReport(dir+"\\"+fileName[i]);
			infoMap[i] = r.getQInfoMap();
			totalSet.addAll(infoMap[i].keySet());
		}
		System.out.println(totalSet.size());
		totalNum = totalSet.size();
		StatInfo [] result = new StatInfo[totalNum];
		Iterator <String> it = totalSet.iterator();
		int count = 0;
		while(it.hasNext()){
			String refStr = it.next();
			int num = 0;
			float [] ratios = new float[fileNum];
			for(int j=0;j<fileNum;j++){
				if(infoMap[j].containsKey(refStr)){
					num++;
					ratios[j]=infoMap[j].get(refStr).getRatio();
				}else{
					ratios[j]=0;
				}
			}
//			System.out.println(refStr+"~"+num+"~"+fileNum);
			result[count]=new StatInfo(refStr,num,ratios);
			count++;
			summary[num-1]++;
		}
		Arrays.sort(result);
		PrintWriter pw = new PrintWriter(new BufferedWriter(
		        new FileWriter(new File(dir, "Report.stat"))));
		pw.println(getTitle());
		
		for(int k=0;k<result.length;k++){
			result[k].setID(k+1);
			pw.println(result[k]);
		}
		pw.println(getSummary());
		pw.close();
	}
	
	public void getStatInfo2() throws IOException, JXLException{
		totalSet = new HashSet <String>();
		infoMap = new HashMap [fileNum];
		for(int i=0;i<fileNum;i++){
			MSQReport r = new MSQReport(dir+"\\"+fileName[i]);
			infoMap[i] = r.getQInfoMap();
			totalSet.addAll(infoMap[i].keySet());
		}

		totalNum = totalSet.size();
		StatInfo [] result = new StatInfo[totalNum];
		Iterator <String> it = totalSet.iterator();
		int count = 0;
		while(it.hasNext()){
			String refStr = it.next();
			int num = 0;
			QInfo [] infos = new QInfo[fileNum];
			for(int j=0;j<fileNum;j++){
				if(infoMap[j].containsKey(refStr)){
					num++;
					infos[j] = infoMap[j].get(refStr);
				}
			}
//			System.out.println(refStr+"~"+num+"~"+fileNum);
			result[count]=new StatInfo(refStr,num,infos);
			count++;
			summary[num-1]++;
		}
		Arrays.sort(result);
		PrintWriter pw = new PrintWriter(new BufferedWriter(
		        new FileWriter(new File(dir, "Report.stat"))));
		pw.println(getTitle());
		
		for(int k=0;k<result.length;k++){
			result[k].setID(k+1);
			pw.println(result[k]);
		}
		pw.println(getSummary());
		pw.close();
	}
	
	public String getTitle(){
		StringBuilder sb = new StringBuilder();
		sb.append("id\t");
		sb.append("Reference\t");
		sb.append("Num\t");
		sb.append("Ave\t");
		sb.append("RSD\t");
		for(int i=0;i<fileNum;i++){
			sb.append(fileName[i].substring(0, fileName[i].length()-4)).append("_Ratio\t");
		}
		return sb.toString();
	}
	
	public String getSummary(){
		StringBuilder sb = new StringBuilder();
		sb.append("---------------------").append("\n");
		sb.append("Summary:\n");
		sb.append("\tTotal\tPercent\n");
		for(int i=fileNum;i>0;i--){
			sb.append("In "+i+" files:\t").append(summary[i-1]).append("\t").
				append(dfPer.format((float)summary[i-1]/(float)totalNum)).append("\n");
		}
		
		return sb.toString();
	}
	
	public class StatInfo implements Comparable<StatInfo>{
		private String ref;
		private int num;
		private float RSD;
		private float ave;
		private float [] ratios;
		private QInfo [] qinfo;
		private int id;
		
		public DecimalFormat df6= new DecimalFormat(".######");
		
		public StatInfo(String ref){
			this.ref = ref;
		}
		
		public StatInfo(String ref,int num,float [] ratios){

			this.ref = ref;
			this.num = num;
			this.ratios = ratios;
			float [] rsds = this.getRSD(ratios, num);
			this.ave = rsds[0];
			this.RSD = rsds[1];
		}

		public StatInfo(String ref, int num, QInfo [] qinfo){
			this.ref = ref;
			this.num = num;
			this.qinfo = qinfo;
			this.ratios = new float[qinfo.length];
			for(int i=0;i<qinfo.length;i++){
				if(qinfo[i]!=null)
					ratios[i] = qinfo[i].getRatio();
				else
					ratios[i] = 0;
			}
			float [] rsds = this.getRSD(ratios, num);
			this.ave = rsds[0];
			this.RSD = rsds[1];
		}
		
		public int getNum(){
			return this.num;
		}

		public HashMap <String, float[]> getPepRSD(){

			HashSet <String> pepSet = new HashSet<String>();
			for(int i=0;i<qinfo.length;i++){
				if(qinfo[i]!=null)
					pepSet.addAll(qinfo[i].getPepMap().keySet());
			}
			Iterator <String> it = pepSet.iterator();
			HashMap <String, float []> pepIntenMap = new HashMap <String, float []>();
			while(it.hasNext()){
				String seq = it.next();
				int num = 0;
				float [] ratios = new float[qinfo.length+3];
				for(int i=0;i<qinfo.length;i++){
					if(qinfo[i]!=null){
						if(qinfo[i].getPepMap().containsKey(seq)){
							float[] signals = qinfo[i].getPepMap().get(seq);
							ratios[i+3] = signals[1]/signals[0];
							num++;
						}else{
							ratios[i+3] = 0;
						}
					}else{
						ratios[i+3] = 0;
					}
				}
				float [] rsds = getRSD(ratios, num);
				ratios[0] = rsds[0];
				ratios[1] = rsds[1];
				ratios[2] = num;
				pepIntenMap.put(seq, ratios);
			}
			return pepIntenMap;
		}
		
		public float [] getRSD(float [] ratios, int num){
			float ave=0;
			for(int i=0;i<ratios.length;i++){
				ave+=ratios[i];
			}
			ave = ave/num;
			float rsd=0;
			for(int i=0;i<ratios.length;i++){
				if(ratios[i]!=0){
					rsd+=Math.pow(ratios[i]-ave, 2);
				}
			}
			if(num==1)
				rsd = 0;
			else
				rsd = Float.parseFloat(df6.format(Math.sqrt(rsd/(num-1))));
			
			float [] rsds = new float[2];
			rsds[0] = Float.parseFloat(df6.format(ave));
			rsds[1] = Float.parseFloat(df6.format(rsd/ave));
			return rsds;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(StatInfo s1) {
			// TODO Auto-generated method stub
			int i = this.num;
			int i1 = s1.num;
			if(i>i1)
				return -1;
			else if(i<i1)
				return 1;
			else
				return 0;
		}
		
		public void setID(int id){
			this.id = id;
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append(id).append("\t");
			sb.append(ref).append("\t");
			sb.append(num).append("\t");
			sb.append(ave).append("\t");
			sb.append(dfPer.format(RSD)).append("\t");
			for(int i=0;i<ratios.length;i++){
				sb.append(ratios[i]).append("\t");
			}
			sb.append("\n");
			HashMap <String, float[]> pepRSD = getPepRSD();
			Iterator <String> it = pepRSD.keySet().iterator();
			while(it.hasNext()){				
				String pep = it.next();
				float [] rsds = pepRSD.get(pep);
				sb.append("\t").append(pep);
				sb.append("\t").append((int)rsds[2]);
				sb.append("\t").append(rsds[0]);
				sb.append("\t").append(dfPer.format(rsds[1])).append("\t");
				for(int i=3;i<rsds.length;i++){
					sb.append(rsds[i]).append("\t");
				}
				sb.append("\n");
			}
			return sb.toString();
		}
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		MSQReportStat stat = new MSQReportStat("E:\\Data\\control-1-dimension-FINAL\\quantification");
		stat.getStatInfo2();
	}

}
