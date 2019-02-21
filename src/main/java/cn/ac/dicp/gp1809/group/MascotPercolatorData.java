/* 
 ******************************************************************************
 * File:MascotPercolatorData.java * * * Created on 2012-8-23
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.group;

/**
 * @author ck
 *
 * @version 2012-8-23, 10:26:50
 */
public class MascotPercolatorData {
	
	private int query;
	private int rank;
	private float score;
	private float qValue;
	private double PEP;
	private String seq;
	private String [] refs;
	
	public MascotPercolatorData(int query, int rank, float score, float qValue, 
			double PEP, String seq, String [] refs){
		
		this.query = query;
		this.rank = rank;
		this.score = score;
		this.qValue = qValue;
		this.PEP = PEP;
		this.seq = seq;
		this.refs = refs;
	}
	
	public int getQuery(){
		return query;
	}
	
	public int getRank(){
		return rank;
	}
	
	public float getScore(){
		return score;
	}
	
	public float getQValue(){
		return qValue;
	}
	
	public double getPEP(){
		return PEP;
	}
	
	public String getSeq(){
		return seq;
	}
	
	public String [] getRefs(){
		return refs;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
