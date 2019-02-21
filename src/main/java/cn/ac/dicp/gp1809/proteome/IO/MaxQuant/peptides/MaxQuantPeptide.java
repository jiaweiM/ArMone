/* 
 ******************************************************************************
 * File: MaxQuantPeptide.java * * * Created on 2012-1-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

/**
 * @author ck
 *
 * @version 2012-1-4, 14:22:15
 */
public class MaxQuantPeptide extends AbstractPeptide implements IMaxQuantPeptide {

	private double score;
	
	private double PEP;
	
	/**
	 * @param pep
	 */
	public MaxQuantPeptide(IPeptide pep) {
		super(pep);
		// TODO Auto-generated constructor stub
	}
	
	public MaxQuantPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, double mh, double deltaMs,
	        short rank, double score, double PEP, HashSet<ProteinReference> refs, 
	        IMaxQuantPeptideFormat formatter){
		
		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, refs, formatter);
		
		this.setPrimaryScore((float)score);
		this.score = score;
		this.PEP = PEP;
	}

	/**
	 * @param scanNum
	 * @param sequence
	 * @param charge
	 * @param mh
	 * @param deltamh
	 * @param ntt
	 * @param rank
	 * @param score2
	 * @param pep2
	 * @param references
	 * @param pi
	 * @param defaultMaxQuantPeptideFormat
	 */
	public MaxQuantPeptide(String scanNum, String sequence, short charge,
			double mh, double deltamh, short ntt, short rank, double score,
			double PEP, HashSet<ProteinReference> references, float pi,
			IMaxQuantPeptideFormat formatter) {
		// TODO Auto-generated constructor stub
		
		super(scanNum, sequence, charge, mh, deltamh, rank, references, formatter);
		this.setNumberofTerm(ntt);
		this.setPI(pi);
		this.setPrimaryScore((float)score);
		
		this.score = score;
		this.PEP = PEP;
//		super(scanNum, sequence, charge, mh, deltamh, ntt, rank, references, pi, formatter);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		// TODO Auto-generated method stub
		return PeptideType.MaxQuant;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.IMaxQuantPeptide#getPEP()
	 */
	@Override
	public double getPEP() {
		// TODO Auto-generated method stub
		return PEP;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.IMaxQuantPeptide#getScore()
	 */
	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return score;
	}

}
