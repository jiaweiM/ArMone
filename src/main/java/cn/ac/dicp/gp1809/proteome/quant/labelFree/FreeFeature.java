/* 
 ******************************************************************************
 * File: FreeFeature.java * * * Created on 2012-10-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree;

/**
 * @author ck
 *
 * @version 2012-10-16, 9:10:43
 */
public class FreeFeature {
	
	private int scannum;
	private int charge;
	private double mz;
	private double rt;
	private double intensity;

	/**
	 * @param scannum
	 * @param pepMr
	 * @param rt
	 * @param intens
	 */
	public FreeFeature(int scannum, double pepMr, double rt, double[] intens) {
		this.scannum = scannum;
	}

	/**
	 * @param scannum
	 * @param pepMr
	 * @param rt
	 * @param intens
	 */
	public FreeFeature(int scannum, double pepMr, double rt, double intensity) {
		// TODO Auto-generated constructor stub
		this.scannum = scannum;
		this.rt = rt;
		this.intensity = intensity;
	}
	
	/**
	 * @param scanNum
	 * @param value
	 */
	public FreeFeature(int scannum, int charge, double mz, double intensity) {
		// TODO Auto-generated constructor stub
		this.scannum = scannum;
		this.charge = charge;
		this.mz = mz;
		this.intensity = intensity;
	}

	
	/**
	 * @return
	 */
	public double getRT() {
		// TODO Auto-generated method stub
		return rt;
	}

	/**
	 * @return
	 */
	public double getIntensity() {
		// TODO Auto-generated method stub
		return intensity;
	}

	/**
	 * @param rt
	 */
	public void setRT(double rt) {
		// TODO Auto-generated method stub
		this.rt = rt;
	}

	/**
	 * 
	 */
	public void validate() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return
	 */
	public int getScanNum() {
		// TODO Auto-generated method stub
		return scannum;
	}


}
