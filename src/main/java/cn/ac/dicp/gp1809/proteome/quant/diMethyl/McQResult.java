/* 
 ******************************************************************************
 * File:McQResult.java * * * Created on 2010-4-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.diMethyl;

/**
 * @author ck
 *
 * @version 2010-4-20, 10:03:31
 */
public class McQResult implements Comparable<McQResult>{

	private String ref;
	private double exp_mz;
	private double exp_mr;
	private int exp_z;
	private double cal_mr;
	private String pep_seq;
	private int isotope;
	private String des;
	private int scanNum;
	
	public McQResult(String ref, double exp_mz, int exp_z, double cal_mr, String pep_seq){
		this.ref = ref;
		this.exp_mz = exp_mz;
		this.exp_z = exp_z;
		this.cal_mr = cal_mr;
		this.pep_seq = pep_seq;
	}
	
	public McQResult(String ref, double exp_mz, int exp_z, double cal_mr, String pep_seq, String des, int scanNum){
		this(ref, exp_mz, exp_z, cal_mr, pep_seq);
		this.des = des;
		this.scanNum = scanNum;
	}
	
	public McQResult(String ref, double exp_mz, double exp_mr, int exp_z, double cal_mr, String pep_seq){
		this(ref, exp_mz, exp_z, cal_mr, pep_seq);
		this.exp_mr = exp_mr;
	}
	
	public String getRef(){
		return ref;
	}
	
	public double getExpMz(){
		return exp_mz;
	}
	
	public int getExpZ(){
		return exp_z;
	}
	
	public double getCalMr(){
		return cal_mr;
	}
	
	public void setIso(int iso){
		this.isotope = iso;
	}
	
	public int getIso(){
		return isotope;
	}
	
	public String getPepSeq(){
		return pep_seq;
	}
	
	public int getAveScanNum(){
		return scanNum;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(ref).append("\t");
		sb.append(exp_mz).append("\t");
		sb.append(exp_z).append("\t");
		sb.append(cal_mr).append("\t");
		sb.append(pep_seq).append("\t");
		sb.append(des).append("\t");
		sb.append(scanNum);
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(McQResult m1) {
		// TODO Auto-generated method stub
		double d = this.exp_mz;
		double d1 = m1.exp_mz;
		return d>d1 ? 1:-1;
	}

}
