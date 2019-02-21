/* 
 ******************************************************************************
 * File:QModReader.java * * * Created on 2010-9-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import jxl.JXLException;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * @author ck
 *
 * @version 2010-9-7, 14:03:29
 */
public class QModReader {

	private ExcelReader reader;
	private int lineNumPro;
	private int lineNumSite;
	private int labelNum;
	private int ratioNum;
	
	public QModReader(String file) throws IOException, JXLException{
		this(new File(file));
	}
	
	public QModReader(File file) throws IOException, JXLException{
		this.reader = new ExcelReader(file);
		this.lineNumPro = reader.readLine().length;
		this.lineNumSite = reader.readLine().length;
		this.ratioNum = (lineNumSite-4)/2;
		this.labelNum = (int)(Math.sqrt(8*ratioNum+1)+1)/2;
	}
/*	
	public HashMap <String, HashMap<String, HashMap<Integer,QMInfo>>> getMResult(){
		String [] columns = null;
		String ref = "";
		String mod = "";
		HashMap <String, HashMap<String, HashMap<Integer,QMInfo>>> refMap = 
			new HashMap <String, HashMap<String, HashMap<Integer,QMInfo>>> ();
		
		while((columns = reader.readLine()) != null){
			int length = columns.length;
			if(length<=6){
				ref = columns[1];
				HashMap <String, HashMap<Integer,QMInfo>> modMap = 
					new HashMap <String, HashMap<Integer,QMInfo>> ();
				refMap.put(ref, modMap);				
			}else{
				if(columns[0].length()>0){
					mod = columns[0];
				}					
				
				int site = Integer.parseInt(columns[1]);
				String seq = columns[2];
				double [] abRatio = new double [labelNum];
				double [] reRatio = new double [labelNum];
				for(int i=0;i<labelNum;i++){
					abRatio[i] = Double.parseDouble(columns[i+3]);
					reRatio[i] = Double.parseDouble(columns[i+labelNum+3]);
				}
				QMInfo qm = new QMInfo(mod, ref, seq, site, abRatio, reRatio);
				HashMap <String, HashMap<Integer,QMInfo>> modMap = refMap.get(ref);
				if(modMap.containsKey(mod)){
					modMap.get(mod).put(site, qm);
				}else{
					HashMap <Integer,QMInfo> siteMap = new HashMap<Integer,QMInfo> ();
					siteMap.put(site, qm);
					modMap.put(mod, siteMap);
				}
			}
		}
		return refMap;
	}
*/	
	public HashMap <String, QMInfo> getMResult(){
		String [] columns = null;
		String ref = "";
		String mod = "";
		HashMap <String, QMInfo> qmMap = new HashMap <String, QMInfo> ();
		while((columns = reader.readLine()) != null && columns.length>1){
			int length = columns.length;
			if(length==lineNumPro){
				ref = columns[1];
			}else{
				if(columns[0].length()>0){
					mod = columns[0];
				}					
				
				int site = Integer.parseInt(columns[1]);
				String seq = columns[2];
				double [] abRatio = new double [ratioNum];
				double [] reRatio = new double [ratioNum];
				for(int i=0;i<ratioNum;i++){
					abRatio[i] = Double.parseDouble(columns[i*2+3]);
					reRatio[i] = Double.parseDouble(columns[i*2+4]);
				}
				QMInfo qm = new QMInfo(mod, ref, seq, site, abRatio, reRatio);
				String key = ref+"$$"+mod+"$$"+site+"$$"+seq;
				qmMap.put(key, qm);
			}	
		}
		return qmMap;
	}
	
	public int getLabelNum(){
		return labelNum;
	}
	
	public int getRatioNum(){
		return ratioNum;
	}
	
	public class QMInfo{
		
		private String mod;
		private String ref;
		private String seq;
		private int site;
		private double [] abRatio;
		private double [] reRatio;
		
		public QMInfo(String mod, String ref, String seq, int site, double [] abRatio, 
				double [] reRatio){
			this.mod = mod;
			this.ref = ref;
			this.seq = seq;
			this.site = site;
			this.abRatio = abRatio;
			this.reRatio = reRatio;
		}
		
		public String getRef(){
			return ref;
		}
		
		public String getMod(){
			return mod;
		}
		
		public String getSeq(){
			return seq;
		}
		
		public int getSite(){
			return site;
		}
		
		public double [] getAbRatio(){
			return abRatio;
		}
		
		public double [] getReRatio(){
			return reRatio;
		}
		
		public double getAbRUse(int i){
			return abRatio[i];
		}
		
		public double getReRUse(int i){
			return reRatio[i];
		}
		
		public String getInfo(){
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<ratioNum;i++){
				sb.append(abRatio[i]).append("\t").append(reRatio[i]).append("\t");
			}
			return sb.toString();
		}
		
	}
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

		QModReader reader = new QModReader("F:\\data\\Glyco\\peptide" +
				"\\comp_mod_quan.xls");
		System.out.println(reader.getMResult().size());
		
	}

}
