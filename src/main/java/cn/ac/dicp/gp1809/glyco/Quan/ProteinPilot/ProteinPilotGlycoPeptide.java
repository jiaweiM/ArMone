/* 
 ******************************************************************************
 * File: ProteinPilotGlycoPeptide.java * * * Created on 2013-6-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.ProteinPilot;

import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;

/**
 * @author ck
 * 
 * @version 2013-6-7, 9:37:11
 */
public class ProteinPilotGlycoPeptide {

	private ProteinPilotPeptide peptide;
	private NGlycoSSM ssm;
	private int rank;
	private double deltaMz;
	private double deltaMzPPM;

	/**
	 * @param peptide
	 * @param ssm
	 */
	public ProteinPilotGlycoPeptide(ProteinPilotPeptide peptide, NGlycoSSM ssm) {
		this.peptide = peptide;
		this.ssm = ssm;
	}

	/**
	 * @return the peptide
	 */
	public ProteinPilotPeptide getPeptide() {
		return peptide;
	}

	/**
	 * @param peptide
	 *            the peptide to set
	 */
	public void setPeptide(ProteinPilotPeptide peptide) {
		this.peptide = peptide;
	}

	/**
	 * @return the ssm
	 */
	public NGlycoSSM getSsm() {
		return ssm;
	}

	/**
	 * @param ssm
	 *            the ssm to set
	 */
	public void setSsm(NGlycoSSM ssm) {
		this.ssm = ssm;
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank
	 *            the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @return the deltaMz
	 */
	public double getDeltaMz() {
		return deltaMz;
	}

	/**
	 * @param deltaMz
	 *            the deltaMz to set
	 */
	public void setDeltaMz(double deltaMz) {
		this.deltaMz = deltaMz;
	}

	/**
	 * @return the deltaMzPPM
	 */
	public double getDeltaMzPPM() {
		return deltaMzPPM;
	}

	/**
	 * @param deltaMzPPM
	 *            the deltaMzPPM to set
	 */
	public void setDeltaMzPPM(double deltaMzPPM) {
		this.deltaMzPPM = deltaMzPPM;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
