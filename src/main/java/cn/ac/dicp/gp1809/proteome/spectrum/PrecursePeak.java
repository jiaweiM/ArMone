/*
 ******************************************************************************
 * File: PrecursePeak.java * * * Created on 02-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;

/**
 * Peak for precursor ion. With charge state specified.
 * 
 * @author Xinning
 * @version 0.2.4, 06-03-2009, 21:36:44
 */
public class PrecursePeak extends Peak {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private int scanNum;
	
	private short charge;
	
	private double rt;

	public PrecursePeak() {
	}

	public PrecursePeak(double mz, double intens) {
		super(mz, intens);
	}
	
	public PrecursePeak(int scanNum, double mz, double intens) {
		super(mz, intens);
		this.scanNum = scanNum;
	}

	/**
	 * Set the charge state of the precursor peak.
	 * 
	 * @param charge
	 */
	public void setCharge(short charge) {
		this.charge = charge;
	}

	/**
	 * @return Charge state of this precursor ion. 0 for unassignment.
	 */
	public short getCharge() {
		return this.charge;
	}
	
	/**
	 * Set the retention time of the precursor peak.
	 * @param rt
	 */
	public void setRT(double rt){
		this.rt = rt;
	}
	
	/**
	 * 
	 * @return the retention time of the precursor peak.
	 */
	public double getRT(){
		return rt;
	}
	
	public void setScanNum(int scanNum){
		this.scanNum = scanNum;
	}
	
	public int getScanNum(){
		return scanNum;
	}

	/**
	 * The MH+ value
	 * 
	 * @since 0.2.4
	 * @return
	 */
	public double getMH() {
		return SpectrumUtil.getMH(this.getMz(), charge);
	}

	/**
	 * Set the MH+ value
	 * 
	 * @since 0.2.4
	 * @return
	 */
	public void setMH(double mh) {
		this.setMz(SpectrumUtil.getMZ(mh, charge));
	}

	@Override
	public PrecursePeak deepClone() {
		return this.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PrecursePeak clone() {
		return (PrecursePeak) super.clone();
	}
}
