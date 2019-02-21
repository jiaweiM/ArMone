/* 
 ******************************************************************************
 * File:QModStat.java * * * Created on 2010-9-8
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.profile.ResultStat;
import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.proteome.quant.modifQuan.QModReader.QMInfo;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2010-9-8, 09:03:47
 */
public class QModStat implements ResultStat
{

	private int fileNum;
	private String [] fileName;
	private File [] fileList;
	
	private int totalNum;
	private int ratioNum;
	private int [] summary;
	private int [][] abDis;
	private int [][] reDis;
	private ExcelWriter writer;
	
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;
	
	public QModStat(String file, String out) throws IOException{
		this(new File(file), out);
	}
	
	public QModStat(File file, String out) throws IOException{
		if(!file.isDirectory())
			throw new IOException("The file "+file+" is not seem as a directory.");
		
		FileFilter fileFilter = new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp = pathname.getName().toLowerCase();
	            if(tmp.endsWith(".xls")){
	                return true;
	            }
	            return false;
	        }
	    };
		
		File [] filelist = file.listFiles(fileFilter);
		if(filelist==null || filelist.length==0)
	    	throw new FileNotFoundException("There are no *.xls file in this directory : "+file);
	    
		this.fileNum = filelist.length;
		this.summary = new int [fileNum];
		this.fileList = filelist;
		this.fileName = new String [fileNum];
		for(int i=0;i<fileNum;i++){
			String name = filelist[i].getName();
			fileName[i] = name.substring(0,name.length()-4);
		}
		
		QModReader reader = null;
		try {
			reader = new QModReader(filelist[0]);
		} catch (JXLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ratioNum = reader.getRatioNum();
		this.abDis = new int [ratioNum][5];
		this.reDis = new int [ratioNum][5];
		this.writer = new ExcelWriter(out);
	}
/*	
	public void getStatInfo() throws IOException, JXLException{
		HashSet <String> totalSet = new HashSet <String>();
		HashMap <String, HashMap<String, HashMap<Integer,QMInfo>>> [] refMap = new HashMap [fileNum];		
		for(int i=0;i<fileNum;i++){
			QModReader reader = new QModReader(dir+"\\"+fileName[i]);
			refMap[i] = reader.getMResult();
			totalSet.addAll(refMap[i].keySet());
		}
		Iterator <String> refIt = totalSet.iterator();
		while(refIt.hasNext()){
			String ref = refIt.next();
			HashSet <String> modSet = new HashSet <String> ();
			for(int i=0;i<fileNum;i++){
				if(refMap[i].containsKey(ref)){
					modSet.addAll(refMap[i].get(ref).keySet());
				}
			}
			Iterator <String> modIt = totalSet.iterator();
			while(modIt.hasNext()){
				String mod = modIt.next();
				HashSet <Integer> siteSet = new HashSet <Integer>();
				for(int i=0;i<fileNum;i++){
					if(refMap[i].containsKey(ref)){
						HashMap<String, HashMap<Integer,QMInfo>> modMap = refMap[i].get(ref);
						if(modMap.containsKey(mod)){
							
						}
					}
				}
			}
		}
	}
*/
	
	private void getStatInfo() throws IOException, JXLException{
		
		HashSet <String> totalSet = new HashSet <String>();
		HashMap <String, QMInfo> [] refMap = new HashMap [fileNum];
		for(int i=0;i<fileNum;i++){
			System.out.println("Reading "+fileName[i]+" ...");
			QModReader reader = new QModReader(fileList[i]);
			refMap[i] = reader.getMResult();
			totalSet.addAll(refMap[i].keySet());
		}
		
		this.totalNum = totalSet.size();
		QMStatInfo [] infos = new QMStatInfo[totalNum];
		int count = 0;
		Iterator <String> refIt = totalSet.iterator();
		while(refIt.hasNext()){
			String refkey = refIt.next();
			int num = 0;
			QMInfo [] qms = new QMInfo[fileNum];
			for(int i=0;i<fileNum;i++){
				if(refMap[i].containsKey(refkey)){
					qms[i] = refMap[i].get(refkey);
					num++;
				}
			}
			
			String [] ss = refkey.split("\\$\\$");
			String ref = ss[0];
			String mod = ss[1];
			int site = Integer.parseInt(ss[2]);
			String seq = ss[3];
			infos[count] = new QMStatInfo(ref, mod, seq, site, qms, num);
			count++;
			summary[num-1]++;
		}
		
		Arrays.sort(infos);
		String ref = "";
		int proNum = 1;
		for(int i=0;i<infos.length;i++){
			ExcelFormat f = new ExcelFormat(true,0);
			this.distribute(infos[i]);
			if(infos[i].ref.equals(ref)){
				writer.addContent(" \t"+infos[i].toString(), 0, f);
			}else{
				writer.addContent(proNum+"\t"+infos[i].toString(), 0, f);
				proNum++;
			}
			ref = infos[i].ref;
		}
	}
	
	private void distribute(QMStatInfo info){
		double [] abAve = info.abAve;
		double [] reAve = info.reAve;
		for(int i=0;i<ratioNum;i++){
			if(abAve[i]>=2){
				this.abDis[i][4]++;
			}else if(abAve[i]<2 && abAve[i]>=1.2){
				this.abDis[i][3]++;
			}else if(abAve[i]<1.2 && abAve[i]>=0.8){
				this.abDis[i][2]++;
			}else if(abAve[i]<0.8 && abAve[i]>=0.5){
				this.abDis[i][1]++;
			}else if(abAve[i]<0.5 && abAve[i]>0){
				this.abDis[i][0]++;
			}
			
			if(reAve[i]>=2){
				this.reDis[i][4]++;
			}else if(reAve[i]<2 && reAve[i]>=1.2){
				this.reDis[i][3]++;
			}else if(reAve[i]<1.2 && reAve[i]>=0.8){
				this.reDis[i][2]++;
			}else if(reAve[i]<0.8 && reAve[i]>=0.5){
				this.reDis[i][1]++;
			}else if(reAve[i]<0.5 && reAve[i]>0){
				this.reDis[i][0]++;
			}
		}
	}
	
	private void addTitle() throws RowsExceededException, WriteException{
		StringBuilder sb = new StringBuilder();
		sb.append("Index\t").append("Replicate Num\t").append("Reference\t");
		sb.append("Modification\t").append("Mod Site\t").append("Sequence\t\t\t\t\t");
		for(int i=0;i<fileNum;i++){
			sb.append(fileName[i]).append("\t \t");
		}
		sb.append("\n");
		sb.append("\t\t\t\t\t\t");
		
		for(int i=0;i<ratioNum;i++){
			sb.append("Absolute Ave Ratio\t").append("RSD\t");
			sb.append("Relative Ave Ratio\t").append("RSD\t");
			for(int j=0;j<fileNum;j++){
				sb.append("Absolute Ratio\t").append("Relative Ratio\t");
			}
		}
		
		ExcelFormat f = new ExcelFormat(false,0);
		writer.addTitle(sb.toString(),0,f);
	}

	private void addSummary() throws RowsExceededException, WriteException{
		ExcelFormat f1 = new ExcelFormat(false,0);
		writer.addTitle("\n\n\n----------------Summary----------------\n", 0, f1);
		StringBuilder sb = new StringBuilder("\n");
		sb.append(" \t").append("Total\t").append("Percent\n");
		for(int i=fileNum;i>0;i--){
			sb.append("In "+i+" files:\t").append(summary[i-1]).append("\t").
				append(dfPer.format((float)summary[i-1]/(float)totalNum)).append("\n");
		}
		sb.append("Total\t").append(totalNum).append("\t").append("100%").append("\n");
		
		sb.append("\n\n");
		for(int i=0;i<ratioNum;i++){
			sb.append("Ratio\t").append("Absolute Ave Ratio\tPercent\t").append("Relative Ave Ratio\tPercent\n");
			sb.append(">=2\t").append(abDis[i][4]).append("\t")
				.append(dfPer.format((float)abDis[i][4]/totalNum)).append("\t")
				.append(reDis[i][4]).append("\t").append(dfPer.format((float)reDis[i][4]/totalNum)).append("\n");
			sb.append("1.2~2\t").append(abDis[i][3]).append("\t")
				.append(dfPer.format((float)abDis[i][3]/totalNum)).append("\t")
				.append(reDis[i][3]).append("\t").append(dfPer.format((float)reDis[i][3]/totalNum)).append("\n");
			sb.append("0.8~1.2\t").append(abDis[i][2]).append("\t")
				.append(dfPer.format((float)abDis[i][2]/totalNum)).append("\t")
				.append(reDis[i][2]).append("\t").append(dfPer.format((float)reDis[i][2]/totalNum)).append("\n");
			sb.append("0.5~0.8\t").append(abDis[i][1]).append("\t")
				.append(dfPer.format((float)abDis[i][1]/totalNum)).append("\t")
				.append(reDis[i][1]).append("\t").append(dfPer.format((float)reDis[i][1]/totalNum)).append("\n");
			sb.append("<0.5\t").append(abDis[i][0]).append("\t")
				.append(dfPer.format((float)abDis[i][0]/totalNum)).append("\t")
				.append(reDis[i][0]).append("\t").append(dfPer.format((float)reDis[i][0]/totalNum)).append("\n");
			sb.append("Total\t").append(totalNum).append("\t").append("100%").append("\n");
			sb.append("\n\n");
		}
		
		ExcelFormat f2 = new ExcelFormat(true,0);
		writer.addContent(sb.toString(), 0, f2);
	}
	
	public void write() throws IOException, JXLException{
		this.addTitle();
		this.getStatInfo();
		this.addSummary();
		this.writer.close();
		System.out.println("Finish!");
		System.gc();
	}
	
	public class QMStatInfo implements Comparable <QMStatInfo> {
		
		private String ref;
		private String mod;
		private String seq;
		private int site;
		
		private double [][] abRatio;
		private double [][] reRatio;
		private double [] abAve;
		private double [] reAve;
		private double [] abRsd;
		private double [] reRsd;
		private int fileNum;

		private QMInfo [] qms;
		private int num;
		
		public QMStatInfo(String ref, String mod, String seq, int site, QMInfo [] qms, int num){
			this.ref = ref;
			this.mod = mod;
			this.seq = seq;
			this.site = site;
			this.qms = qms;
			this.num = num;
			this.fileNum = qms.length;
			
			this.initial();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(QMStatInfo QM2) {
			// TODO Auto-generated method stub
			String ref1 = this.ref;
			String ref2 = QM2.ref;
			if(ref1.compareTo(ref2)==0){
				String mod1 = this.mod;
				String mod2 = QM2.mod;
				if(mod1.compareTo(mod2)==0){
					int site1 = this.site;
					int site2 = QM2.site;
					if(site1>site2)
						return 1;
					else if(site1<site2)
						return -1;
					else
						return 0;
				}else{
					return mod1.compareTo(mod2);
				}
			}else{
				return ref1.compareTo(ref2);
			}
		}		
		
		private void initial(){
			
			this.abRatio = new double[ratioNum][fileNum];
			this.reRatio = new double[ratioNum][fileNum];
			this.abAve = new double[ratioNum];
			this.reAve = new double[ratioNum];
			this.abRsd = new double[ratioNum];
			this.reRsd = new double[ratioNum];
			
			ArrayList <Double> [] ablist = new ArrayList [ratioNum];
			ArrayList <Double> [] relist = new ArrayList [ratioNum];
			
			for(int i=0;i<ratioNum;i++){
				ablist[i] = new ArrayList <Double>();
				relist[i] = new ArrayList <Double>();
			}
			for(int i=0;i<fileNum;i++){
				if(qms[i]!=null){
					double [] ab = qms[i].getAbRatio();
					double [] re = qms[i].getReRatio();
					for(int j=0;j<ratioNum;j++){
						if(ab[j]>0)
							ablist[j].add(ab[j]);
						if(re[j]>0)
							relist[j].add(re[j]);
						abRatio[j][i] = ab[j];
						reRatio[j][i] = re[j];
					}
				}else{
					for(int j=0;j<ratioNum;j++){
						abRatio[j][i] = 0;
						reRatio[j][i] = 0;
					}
				}
			}
			for(int i=0;i<ratioNum;i++){
				this.abAve[i] = MathTool.getAveInDouble(ablist[i]);
				this.reAve[i] = MathTool.getAveInDouble(relist[i]);
				this.abRsd[i] = MathTool.getRSDInDouble(ablist[i]);
				this.reRsd[i] = MathTool.getRSDInDouble(relist[i]);
			}
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append(num).append("\t");
			sb.append(ref).append("\t").append(mod).append("\t");
			sb.append(site).append("\t").append(seq).append("\t");

			for(int i=0;i<ratioNum;i++){
				sb.append(abAve[i]).append("\t").append(dfPer.format(abRsd[i])).append("\t")
					.append(reAve[i]).append("\t").append(dfPer.format(reRsd[i])).append("\t");
				
				for(int j=0;j<fileNum;j++){
					sb.append(abRatio[i][j]).append("\t").append(reRatio[i][j]).append("\t");
				}
			}
			return sb.toString();
		}
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws JXLException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

		QModStat stat = new QModStat("E:\\Data\\mouse-liver-W-X-FINAL\\mod", 
				"E:\\Data\\mouse-liver-W-X-FINAL\\Mod_stat.xls");
		stat.write();
	}

}
