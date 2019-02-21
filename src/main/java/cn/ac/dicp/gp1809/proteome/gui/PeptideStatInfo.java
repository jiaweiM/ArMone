/* 
 ******************************************************************************
 * File: PeptideStatInfo.java * * * Created on 06-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * Peptide statistic information in peptide list viewer
 * 
 * @author Xinning
 * @version 0.1.1, 05-21-2010, 10:41:17
 */
public class PeptideStatInfo {

	private static DecimalFormat DF = DecimalFormats.DF_PRECENT0_2;
	private double totalSIn;
	
	private static final String[] colNames = new String[] {"Description", "Target", "Decoy", "FDR", "FPR"};
	
	private ArrayList<PeptideCountInfo> infolist;

	public PeptideStatInfo(PeptideCountInfo[] infos) {
		this();

		if (infos != null) {
			for (PeptideCountInfo info : infos) {
				infolist.add(info);
			}
		}
	}

	public PeptideStatInfo() {
		this.infolist = new ArrayList<PeptideCountInfo>();
	}

	public PeptideStatInfo(PeptideCountInfo[] infos, double totalSIn){
		this(infos);
		this.totalSIn = totalSIn;
	}
	
	/**
	 * Add an info
	 * 
	 * @param info
	 */
	public void addInfo(PeptideCountInfo info) {
		if (info != null)
			infolist.add(info);
	}

	/**
	 * The size of the statistic info
	 * 
	 * @return
	 */
	public int size() {
		return this.infolist.size();
	}

	/**
	 * The statistic infos
	 * 
	 * @return
	 */
	public PeptideCountInfo[] getInfos() {
		return this.infolist
		        .toArray(new PeptideCountInfo[this.infolist.size()]);
	}
	
	/**
	 * The values in the statistic table
	 * 
	 * @return
	 */
	String[][] getString4Table() {
		int size = this.size();
		String[][] strss = new String[size][];
		
		for(int i=0; i< size; i++) {
			
			PeptideCountInfo info = this.infolist.get(i);
			if(info==null)
				return null;
			
			String[] strs = new String[5];
			
			strs[0] = info.getDescription();
			strs[1] = String.valueOf(info.getTargetCount());
			strs[2] = String.valueOf(info.getDecoyCount());
			
			int total = info.getTargetCount()+info.getDecoyCount();
			double fdr = 0;
			double fpr = 0;
			if(total != 0) {
				fdr = info.getDecoyCount()*2/(double)total;
				fpr = info.getDecoyCount()/(double)info.getTargetCount();
			}
			
			strs[3] = DF.format(fdr);
			strs[4] = DF.format(fpr);
			
			strss[i] = strs;
		}
		
		return strss;
	}
	
	/**
	 * The names of each column
	 * 
	 * @return
	 */
	static String[] getTableColNames() {
		return colNames;
	}

	public void setTotalSIn(double inten){
		this.totalSIn = inten;
	}
	
	public double getTotalSIn(){
		return totalSIn;
	}
	
	public void setTotalSIns(double totalSIn){
		this.totalSIn = totalSIn;
	}
	
	/**
	 * The peptide count versus charge state information
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 06-19-2009, 10:41:14
	 */
	public static class PeptideCountInfo {

		private String description;
		private int targetCount;
		private int decoyCount;

		/**
		 * 
		 * 
		 * @param description
		 *            The description of this info (such as "1+" or ">4+")
		 * @param targetCount
		 *            the target peptide hit count
		 * @param decoyCount
		 *            the decoy peptide hit count
		 */
		public PeptideCountInfo(String description, int targetCount,
		        int decoyCount) {
			this.description = description;
			this.targetCount = targetCount;
			this.decoyCount = decoyCount;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @return the targetCount
		 */
		public int getTargetCount() {
			return targetCount;
		}

		/**
		 * @return the decoyCount
		 */
		public int getDecoyCount() {
			return decoyCount;
		}
	}
}
