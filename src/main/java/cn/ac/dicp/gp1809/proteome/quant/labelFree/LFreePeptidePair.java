/* 
 ******************************************************************************
 * File: PeptidePair.java * * * Created on 2011-7-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;

/**
 * @author ck
 *
 * @version 2011-7-2, 09:44:56
 */
public class LFreePeptidePair {

	private IPeptide peptide;
	private FreeFeatures [] feas;
	private String [] srcs;

	public LFreePeptidePair(IPeptide peptide, FreeFeatures [] feas, String [] srcs){
		this.peptide = peptide;
		this.feas = feas;
		this.srcs = srcs;
	}

	public LFreePeptidePair(IPeptide pep, FreeFeatures[] feaslist,
			String[] srclist, double[] ratios) {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.PeptidePair#getSrc()
	 */
	public String getSrc() {
		// TODO Auto-generated method stub
		return srcs[0];
	}
	
	public String [] getSrcs(){
		return srcs;
	}
	
	public String getSequence(){
		return peptide.getSequence();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.PeptidePair#hasVariMod()
	 */
	public boolean hasVariMod() {
		// TODO Auto-generated method stub
		char [] chars = PeptideUtil.getSequence(this.getSequence()).toCharArray();
		for(int i=0;i<chars.length;i++){
			if(chars[i]<'A' || chars[i]>'Z'){
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.PeptidePair#getPairNames()
	 */
	public String[] getPairNames() {
		// TODO Auto-generated method stub
		return srcs;
	}
	
	public String toString(){
		
		StringBuilder sb = new StringBuilder();

		sb.append(this.getSequence()).append("\t");

		return sb.toString();
	}

	public boolean equals(Object o){
		
		if(o instanceof LFreePeptidePair){
			
			LFreePeptidePair p = (LFreePeptidePair) o;
			String k1 = this.getSequence();
			String k2 = p.getSequence();
			return k1.equals(k2);
			
		}else{
			return false;
		}		
	}
	
	public int hashCode(){
		String s = this.getSequence();
		return s.hashCode();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.PeptidePair#validate()
	 */
	public void validate() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.PeptidePair#getPairObjectString()
	 */
	public String getPairObjectString() {
		// TODO Auto-generated method stub
		return this.toString();
	}

	/**
	 * @param normal
	 */
	public void setNormalRatio(double[] normal) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return
	 */
	public double[] getSelectRatio() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param normal
	 */
	public void setNormal(boolean normal) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param realNormalFactor
	 */
	public void setNormalFactor(double[] realNormalFactor) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param outputRatio
	 */
	public void setSelectRatio(int[] outputRatio) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return
	 */
	public IPeptide getPeptide() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param name
	 */
	public void setDelegateRef(String name) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.PeptidePair#getRatioNum()
	 */
	public int getRatioNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.PeptidePair#getPairNum()
	 */
	public int getPairNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.PeptidePair#getTotalIntens()
	 */
	public double[] getTotalIntens() {
		// TODO Auto-generated method stub
		return null;
	}

}
