/* 
 ******************************************************************************
 * File: SeqVsPScore.java * * * Created on 2013-2-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.Pscore;

import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.ISeqvsScore;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;

/**
 * @author ck
 *
 * @version 2013-2-28, 10:30:10
 */
public class SeqVsPScore implements ISeqvsScore {

	private String sequence;
	private String scoreSeq;
	private double [] scores;
	private char [] symbols;
	private Integer [] loc;
	private double deltaScore;
	
	

	public SeqVsPScore(String sequence, double [] scores){
		this.sequence = sequence;
		this.scores = scores;
	}
	
	public SeqVsPScore(String sequence, String scoreSeq, double [] scores, char [] symbols){
		this.sequence = sequence;
		this.scoreSeq = scoreSeq;
		this.scores = scores;
		this.symbols = symbols;
		
		ArrayList <Integer> loclist = new ArrayList <Integer>();
		for(int i=0;i<symbols.length;i++){
			if(symbols[i]!='\u0000'){
				loclist.add(i+1);
			}
		}
		this.loc = new Integer[loclist.size()];
		this.loc = loclist.toArray(loc);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.ISeqvsScore#getSequence()
	 */
	@Override
	public IModifiedPeptideSequence getSequence() {
		// TODO Auto-generated method stub
		return ModifiedPeptideSequence.parseSequence(sequence);
	}
	
	public String getSequenceString(){
		return sequence;
	}
	
	public double [] getPScore(){
		return scores;
	}
	
	public Integer [] getLocList(){
		return loc;
	}
	
	/**
	 * @return the deltaScore
	 */
	public double getDeltaScore() {
		return deltaScore;
	}

	/**
	 * @param deltaScore the deltaScore to set
	 */
	public void setDeltaScore(double deltaScore) {
		this.deltaScore = deltaScore;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.ISeqvsScore#getNLSite()
	 */
	@Override
	public int getNLSite() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
