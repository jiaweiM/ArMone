/* 
 ******************************************************************************
 * File: IFastaAccesser.java * * * Created on 08-18-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * This provides a simple way to access the protein sequence (and the full
 * reference name) in a fasta database by the index of protein or the partial
 * (or full) name.
 * 
 * @author Xinning
 * @version 0.3, 05-20-2010, 11:59:50
 */
public interface IFastaAccesser {

	/**
	 * The symbol of a decoy protein. This symbol is the start of the protein
	 * reference. Commonly this symbol is "REVERSED";
	 */
//	public final static String DECOY_SYM = "REV";

	/**
	 * Get the fasta file for this fasta accesser.
	 * 
	 * @return
	 */
	public File getFastaFile();

	/**
	 * Get sequence in fasta database from the protein name
	 * 
	 * @String proteinReference, partial name(front part of the name) or full
	 *         name
	 * @return ProteinSequnece of the protein
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 */
	public ProteinSequence getSequence(String proteinReference)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException;

	/**
	 * Get the protein sequences through the index number in the fasta database.
	 * <p>
	 * <b>Notice: the index is from 1 - n. That is, the index of first protein
	 * is 1. </b>
	 * 
	 * @see OutFile.Reference.
	 * @param idx
	 * @return
	 * @throws ProteinNotFoundInFastaException
	 */
	public ProteinSequence getSequence(int proteinIdx)
	        throws ProteinNotFoundInFastaException;

	/**
	 * Get the ProteinSequence instance for the protein reference. If the
	 * isRenewReference is set as true, the protein index and the name will be
	 * renewed so that the name is the mini name.
	 * 
	 * @param ref
	 * @return
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 */
	public ProteinSequence getSequence(ProteinReference ref)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException;

	/**
	 * As SEQUEST often output protein short names with uncertain length,
	 * therefore, the length of key is need to be determined. After split the
	 * protein reference into short, the short value can be used to generated
	 * protein informations from the index map. <b>Very important value.</b>
	 * 
	 * @return The point where the protein sequence should be split from the
	 *         beginning.
	 * @see getSplitRevLength() for split length of reversed sequences;
	 */
	public int getSplitLength();

	/**
	 * As SEQUEST often output protein short names with uncertain length,
	 * therefore, the length of key is need to be determined. After split the
	 * protein reference into short, the short value can be used to generated
	 * protein informations from the index map. <b>For reversed sequences. Very
	 * important value.</b>
	 * 
	 * @return The point where the protein sequence should be split from the
	 *         beginning.
	 * @see getSplitLength();
	 */
	public int getSplitRevLength();

	/**
	 * The total number of proteins in the fasta database.
	 * 
	 * @return
	 */
	public int getNumberofProteins();

	/**
	 * All the names of the proteins int the fasta database. The protein names
	 * are in the original order.
	 * 
	 * @since 0.2.1
	 * 
	 * @return the names of all the proteins in the fasta database.
	 */
	public String[] getNamesofProteins();

	/**
	 * Close the accesser;
	 */
	public void close();

	/**
	 * If the input is a ProteinReference, and the index of the protein
	 * reference is not set (e.g. for SEQUEST xml or xls exported file), you may
	 * want to renew the index of protein reference for this protein database.
	 * Or you may want to format the name of protein so that the length is
	 * minimum. Use this method to do this.
	 * 
	 * <p>
	 * <b><u>We assume that the reference in ProteinReference instance is either
	 * the full name of protein or the partial name at the start part.</u></b>
	 * 
	 * <p>
	 * <b>Notice: only if the index of ProteinReference with original index of 0
	 * (default value), the index of protein reference will be renew.</b>
	 * 
	 * <p>
	 * Compared with {@link #getSequence(ProteinReference)}, this method uses
	 * less time when the index of protein reference has been defined.
	 * 
	 * @param ref
	 *            the protein reference to be renewed.
	 * 
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	public void renewReference(ProteinReference ref)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException;

	/**
	 * The decoy reference judger
	 * 
	 * @return
	 */
	public IDecoyReferenceJudger getDecoyJudger() ;
	
	/**
	 * Create a shorter reference.
	 * @param ref
	 */
	public void setSubRef(IReferenceDetail ref);
}