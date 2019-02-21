/* 
 ******************************************************************************
 * File: PepMatch.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * A matched peptide
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 20:37:04
 */
public class PepMatch implements IPepMatch {

	private String lineSeparator = IOConstant.lineSeparator;

	private double calculatedMH;
	private int matchedIons, totalIons;
	private short rankPre, rankPrim;
	private HashSet<ProMatch> references;
	private double[] scores;
	private String sequence;
	private char validationStatus;

	/**
	 * 
	 * @param rankPrim
	 * @param rankPre
	 * @param calculatedMh
	 * @param scores
	 * @param matchedIons
	 * @param totalIons
	 * @param sequence
	 * @param validationStatus
	 * @param references
	 */
	protected PepMatch(short rankPrim, short rankPre, double calculatedMh,
	        double[] scores, int matchedIons, int totalIons, String sequence,
	        char validationStatus, HashSet<ProMatch> references) {

		this.calculatedMH = calculatedMh;
		this.matchedIons = matchedIons;
		this.totalIons = totalIons;
		this.rankPre = rankPre;
		this.rankPrim = rankPrim;
		this.references = references;
		this.scores = scores;
		this.sequence = sequence;
		this.validationStatus = validationStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch#getCalculatedMH()
	 */
	@Override
	public double getCalculatedMH() {
		return calculatedMH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch#getMatchedIons()
	 */
	@Override
	public int getMatchedIons() {
		return matchedIons;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch#getTotalIons()
	 */
	@Override
	public int getTotalIons() {
		return totalIons;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch#getRankPre()
	 */
	@Override
	public short getRankPre() {
		return rankPre;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch#getRankPrim()
	 */
	@Override
	public short getRankPrim() {
		return rankPrim;
	}

	/**
	 * Add references for this peptide identification. There should be no
	 * duplicated protein reference if the hashCode is well designed.
	 * 
	 * @param ref
	 */
	public void addReferences(HashSet<ProMatch> refs) {
		this.references.addAll(refs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch#getReferences()
	 */
	@Override
	public HashSet<ProMatch> getReferences() {
		return references;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch#getScores()
	 */
	@Override
	public double[] getScores() {
		return scores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch#getSequence()
	 */
	@Override
	public String getSequence() {
		return sequence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch#getValidationStatus()
	 */
	@Override
	public char getValidationStatus() {
		return validationStatus;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("M\t").append(this.rankPrim).append('\t')
		        .append(this.rankPre).append('\t').append(this.calculatedMH);

		for (double score : this.scores) {
			sb.append('\t').append(score);
		}
		sb.append('\t').append(this.matchedIons).append('\t').append(
		        this.totalIons).append('\t').append(this.sequence).append('\t')
		        .append(this.validationStatus);
		
		for(Iterator<ProMatch> it = this.references.iterator(); it.hasNext();) {
			sb.append(lineSeparator).append(it.next());
		}
		
		return sb.toString();
	}
}
