/* 
 ******************************************************************************
 * File:LabelStatInfo.java * * * Created on 2010-5-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author ck
 *
 * @version 2010-5-24, 21:53:09
 */
public class LabelStatInfo {

	private static final String[] colNames = new String[] {"Abundance Ratio","Peptide Number","Proportion"};
	private static DecimalFormat DF = new DecimalFormat("0.##%");
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		DF = new DecimalFormat("0.##%");
		
		Locale.setDefault(def);
	}
	
	private ArrayList <LabelCountInfo> infoList;
	
	public LabelStatInfo(){
		this.infoList = new ArrayList <LabelCountInfo>();
	}
	
	public LabelStatInfo(LabelCountInfo [] infos){
		this();
		if (infos != null) {
			for(LabelCountInfo info:infos){
				infoList.add(info);
			}
		}		
	}
	
	public static String[] getTableColNames() {
		return colNames;
	}
	
	public void addInfo(LabelCountInfo info){
		infoList.add(info);
	}
	
	public int size(){
		return infoList.size();
	}
	
	public LabelCountInfo [] getInfos(){
		return infoList.toArray(new LabelCountInfo[infoList.size()]);
	}
	
	public String [][] getString4Table(){
		int size = this.size();
		String[][] strss = new String[size][];
		
		for(int i=0; i< size; i++) {
			LabelCountInfo info = infoList.get(i);
			String[] strs = new String[3];
			
			strs[0] = info.getDescription();
			strs[1] = String.valueOf(info.getNum());
			if(info.total==0){
				strs[2] = "0";
			}else{
				strs[2] = DF.format((double)info.num/info.total);
			}
			strss[i] = strs;
		}
		return strss;
	}
	
	public class LabelCountInfo{
		
		private String des;
		private int num;
		private int total;
		
		public LabelCountInfo(String des, int num, int total){
			this.des = des;
			this.num = num;
			this.total = total;
		}
		
		public String getDescription(){
			return des;
		}
		
		public int getNum(){
			return num;
		}
		
		public double total(){
			return total;
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
