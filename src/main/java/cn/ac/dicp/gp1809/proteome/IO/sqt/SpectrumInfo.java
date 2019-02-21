/* 
 ******************************************************************************
 * File: SpectrumInfo.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

/**
 * The spectrum info in SQT file
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 21:02:41
 */
public class SpectrumInfo implements ISpectrumInfo {

	private short charge;
	private double expMH;
	private float lowestSp;
	private int numMatches;
	private float time;
	private String serverName;
	private float tic = -1;
	private int scanBeg, scanEnd;

	/**
	 * @param charge
	 * @param expMh
	 * @param lowestSp
	 * @param numMatches
	 * @param time
	 * @param serverName
	 * @param tic
	 * @param scanBeg
	 * @param scanEnd
	 */
	protected SpectrumInfo(int scanBeg, int scanEnd, short charge, float time,
	        String serverName, double expMh, float tic, float lowestSp,
	        int numMatches) {
		this.charge = charge;
		this.expMH = expMh;
		this.lowestSp = lowestSp;
		this.numMatches = numMatches;
		this.time = time;
		this.serverName = serverName;
		this.tic = tic;
		this.scanBeg = scanBeg;
		this.scanEnd = scanEnd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo#getCharge()
	 */
	@Override
	public short getCharge() {
		return this.charge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo#getExperimentalMH()
	 */
	@Override
	public double getExperimentalMH() {
		return this.expMH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo#getLowestSp()
	 */
	@Override
	public float getLowestSp() {
		return this.lowestSp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo#getNumMatches()
	 */
	@Override
	public int getNumMatches() {
		return this.numMatches;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo#getProcessTime()
	 */
	@Override
	public float getProcessTime() {
		return this.time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo#getScanNumBeg()
	 */
	@Override
	public int getScanNumBeg() {
		return this.scanBeg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo#getScanNumEnd()
	 */
	@Override
	public int getScanNumEnd() {
		return this.scanEnd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo#getSeverName()
	 */
	@Override
	public String getSeverName() {
		return this.serverName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo#getTic()
	 */
	@Override
	public float getTic() {
		return this.tic;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("S\t").append(this.scanBeg).append('\t').append(this.scanEnd)
		        .append('\t').append(this.charge).append('\t')
		        .append(this.time).append('\t').append(this.serverName).append(
		                '\t').append(this.tic).append('\t').append(
		                this.lowestSp).append('\t').append(this.numMatches);
		return sb.toString();
	}
}
