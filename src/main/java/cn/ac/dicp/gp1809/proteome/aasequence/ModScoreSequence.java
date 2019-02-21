/* 
 ******************************************************************************
 * File: ModScoreSequence.java * * * Created on 2010-12-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

/**
 * @author ck
 *
 * @version 2010-12-23, 09:48:46
 */
public class ModScoreSequence {

	private double peptidescore;
	private IModifiedPeptideSequence seq;
	private double [] ptmScores;
	
	ModScoreSequence(IModifiedPeptideSequence seq, double peptidescore, double [] ptmScores){
		this.seq = seq;
		this.peptidescore = peptidescore;
		this.ptmScores = ptmScores;
	}
	
	public IModifiedPeptideSequence getModSeq(){
		return seq;
	}
	
	public double getPepScore(){
		return peptidescore;
	}
	
	public double [] getPTMScores(){
		return ptmScores;
	}
	
	@Override
	public String toString() {
		return this.seq + ": " + this.peptidescore;
	}
	
}
