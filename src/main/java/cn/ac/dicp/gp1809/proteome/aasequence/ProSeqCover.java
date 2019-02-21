/*
 ******************************************************************************
 * File: ProSeqCover.java * * * Created on 03-19-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;

/**
 * In shotgun proteomics, peptides are identified for the evidence of the exists
 * of proteins. This class is used for the computation of protein coverage after
 * the identification of peptides.
 * 
 * @author Xinning
 * @version 0.2.0.1, 02-20-2009, 10:39:00
 */
public class ProSeqCover {
	private String reference;
	private String sequence;

	/*
	 * An array corresponding to the protein sequence to indicate which
	 * aminoacid is covered.
	 */
	private boolean covered[] = new boolean[0];

	/**
	 * Prepare the protein sequence for the computation of protein coverage
	 * 
	 * @param ProteinSequence
	 *            instance
	 */
	public ProSeqCover(ProteinSequence pseq) {
		if (pseq == null)
			throw new NullPointerException(
			        "The ProteinSequence must not be null");
		this.reference = pseq.getReference();
		this.sequence = pseq.getUniqueSequence();
		if (this.sequence == null)
			throw new NullPointerException("The protein with null sequences");
	}

	/**
	 * Prepare the protein sequence for the computation of protein coverage
	 * 
	 * @param sequence
	 *            aminoacid sequences;
	 */
	public ProSeqCover(String sequence) {
		this(null, sequence);
	}

	/**
	 * Prepare the protein sequence for the computation of protein coverage
	 * 
	 * @param reference
	 *            name of the protein
	 * @param sequence
	 *            aminoacid sequences;
	 */
	public ProSeqCover(String reference, String sequence) {
		this.reference = reference;
		this.sequence = sequence;
	}

	/**
	 * Input a peptide sequence array to computing the covered protein
	 * sequences. Then the coverage information can be called by getCoveredAAs()
	 * and getCoveredPoints();
	 * 
	 * @param peps
	 * @return current ProSeqCover after match.
	 */
	public ProSeqCover matches(String[] peps) {
		this.covered = new boolean[this.sequence.length()];
		for (String pep : peps) {
//			int idx = this.sequence.indexOf(pep);
			int idx = this.getLoc(pep)-1;
			if (idx == -1) {
				System.out.println("Warning: The peptide sequence "
				        + "is not found in the target protein, peptide: \""
				        + pep
				        + "\""
				        + (reference == null ? "."
				                : ("; protein: " + reference)));
				continue;
			}

			int end = idx + pep.length();
			for (int i = idx; i < end; i++) {
				covered[i] = true;
			}
		}
		return this;
	}

	/**
	 * The length of covered aminoacids. Must call matches(String[] peps) first.
	 * 
	 * @return the length of covered aminoacid.
	 */
	public int getCoveredAAs() {
		int covlen = 0;
		for (boolean cov : this.covered) {
			if (cov)
				covlen++;
		}
		return covlen;
	}

	/**
	 * The percentage of the covered amino acids. Must call matches(String[]
	 * peps) first.
	 * 
	 * @return the coverage
	 */
	public float getCoverage() {
		float covers = this.getCoveredAAs();
		return covers / this.sequence.length();
	}

	/**
	 * If an aminoacid in the protein sequence is covered by the peptides, then
	 * the related position in the boolean array will be true. This boolean
	 * array has the same number of elements as the protein sequence.
	 * 
	 * @return boolean array containing cover informations.
	 */
	public boolean[] getCoveredPoints() {
		return this.covered;
	}
	
	/**
	 * In database file, the char 'X' can be seemed as any aminoacid, so use this method to 
	 * replace indexOf();
	 * @param pep
	 * @return
	 */
	public int getLoc(String pep){
		char [] proChar = sequence.toCharArray();
		char [] pepChar = PeptideUtil.getUniqueSequence(pep).toCharArray();
		for(int i = 0;i <= proChar.length-pepChar.length;i++){
			int j;
			for(j=0;j<pepChar.length;j++){
				if(proChar[i+j] != pepChar[j] && proChar[i+j]!='X')
					break;
			}
			if(j==pepChar.length) 
				return i+1;
		}
		return 0;
	}
}
