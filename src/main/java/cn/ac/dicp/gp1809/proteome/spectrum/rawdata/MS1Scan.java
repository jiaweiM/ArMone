/* 
 ******************************************************************************
 * File:MS1Scan.java * * * Created on 2010-4-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;

/**
 * The class Scan refers to ms2 or ms3 scans. But for the purpose of quantitation 
 * the ms1 scan information is also needed.
 * @author ck
 *
 * @version 2010-4-23, 03:19:25
 */
public class MS1Scan implements IMS1Scan{

	private Description des;
	private IPeakList peaklist;
	private int index;

	/**
	 * @param des
	 *            description;
	 */
	public MS1Scan(Description des) {
		this(des, null);
	}

	/**
	 * @param des
	 *            description; (Null unpermited)
	 * @param PeakList
	 *            peaks in this scan. (Null permited, for the description of
	 *            this scan)
	 */
	public MS1Scan(Description des, IPeakList peaklist) {
		this.des = des;
		this.peaklist = peaklist;
	}
	
	/**
	 * @param des
	 *            description; (Null unpermited)
	 * @param PeakList
	 *            peaks in this scan. (Null permited, for the description of
	 *            this scan)
	 */
	public MS1Scan(Description des, IPeakList peaklist, int index) {
		this.des = des;
		this.peaklist = peaklist;
		this.index = index;
	}

	/**
	 * Get the description for this scan.
	 * 
	 * @return
	 */
	public Description getDescription() {
		return this.des;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan#getIndex()
	 */
	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return index;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan#getScanNum()
	 */
	@Override
	public int getScanNum() {
		// TODO Auto-generated method stub
		return des.getScanNum();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan#getScanNumInteger()
	 */
	@Override
	public Integer getScanNumInteger() {
		// TODO Auto-generated method stub
		return new Integer(des.getScanNum());
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum#getMSLev()
	 */
	@Override
	public int getMSLevel() {
		// TODO Auto-generated method stub
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum#getPeakList()
	 */
	@Override
	public IPeakList getPeakList() {
		return this.peaklist;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum#isContainPeaklist()
	 */
	@Override
	public boolean isContainPeaklist() {
		// TODO Auto-generated method stub
		return this.peaklist != null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan#getRetentionTimeSeconds()
	 */
	@Override
	public double getRTMinute() {
		// TODO Auto-generated method stub
		return des.getRenTimeMinute();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum#getTotIonCurrent()
	 */
	@Override
	public double getTotIonCurrent() {
		// TODO Auto-generated method stub
		return peaklist.getTotIonCurrent();
	}

}
