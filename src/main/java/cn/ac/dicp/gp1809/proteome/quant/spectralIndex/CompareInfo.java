/* 
 ******************************************************************************
 * File:CompareInfo.java * * * Created on 2010-4-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spectralIndex;

import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.util.math.MathTool;



/**
 * @author ck
 *
 * @version 2010-4-14, 09:00:34
 */
public class CompareInfo implements Comparable<CompareInfo>{

	private String ref;
	private double [] SIns;
	private int id;
	private String ratio;
	private double RSD;
	
	private DecimalFormat dfR = new DecimalFormat("0.###");
	private DecimalFormat dfE = new DecimalFormat("0.###E0");
	
	public CompareInfo(int id,String ref,double [] SIns){
		this(ref, SIns);
		this.id = id;
		this.RSD = MathTool.getStdDev(SIns);
	}
	
	public CompareInfo(String ref,double [] SIns){
		this.ref = ref;
		this.SIns = SIns;
		if(SIns.length==2){
			this.ratio = getRatio2();
		}else{
			this.ratio = getRatio();
		}
		this.RSD = MathTool.getStdDev(SIns);
	}
	
	public String getRatio(){
		StringBuilder sb = new StringBuilder();
		String [] ratio = new String [SIns.length];
		double d =0.0;
		for(int i=0;i<SIns.length;i++){
			if(SIns[i]!=0){
				d=SIns[i];
				break;
			}
		}
		
		for(int i=0;i<SIns.length;i++){
			if(SIns[i]==0){
				ratio[i]="0.000";
			}else{
				ratio[i]=dfR.format(SIns[i]/d);
			}
			sb.append(ratio[i]).append('/');
		}
		String str = sb.toString();
		return str.substring(0,str.length()-1);
	}

	public String getRatio2(){
		if(SIns[1]==0 || SIns[0]==0)
			return "0.000";
		else{
			return dfR.format(SIns[1]/SIns[0]);
		}
	}
	
	public double getRSD(){
		return MathTool.getStdDev(SIns);
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public double[] getSIns(){
		return SIns;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CompareInfo c1) {
		// TODO Auto-generated method stub
		double[] SIns = this.getSIns();
		double[] SIns1 = c1.getSIns();
		double d1 = 1;
		double d2 = 1;
		for(int i=0;i<SIns.length;i++){
			d1=d1*SIns[i];
		}
		for(int j=0;j<SIns1.length;j++){
			d2=d2*SIns1[j];
		}

		return d1>d2 ? -1:1;
	}

	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(id)).append("\t");
		sb.append(ref).append("\t");
		for(int i=0;i<SIns.length;i++){
			sb.append(SIns[i]).append("\t");
		}
		sb.append(ratio).append("\t");
		sb.append(dfE.format(RSD));
		
		return sb.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
