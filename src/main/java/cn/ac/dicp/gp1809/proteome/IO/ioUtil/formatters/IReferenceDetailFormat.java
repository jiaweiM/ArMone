/* 
 ******************************************************************************
 * File: IReferenceDetailFormat.java * * * Created on 08-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;

/**
 * Formatter of protein detail informations. These informations contains protein
 * name, mw, pI, identification coverage and so on.
 * <p>
 * Please use IProteinFormat if you want to format a identified protein with
 * peptide information
 * 
 * @author Xinning
 * @version 0.2, 08-28-2008, 16:10:32
 */
public interface IReferenceDetailFormat extends IFormat<IReferenceDetail>{
	
	/**
	 * The name indicate the protein reference name
	 */
	public static final String REFERENCE = "Reference";

	/**
	 * The name indicate the number of peptides (spectra) for this protein
	 * identification
	 */
	public static final String PEPCOUNT = "SpectraCount";

	/**
	 * The name indicate the number of unique peptides for this protein
	 * identification
	 */
	public static final String UNIQUEPEPCOUNT = "PeptideCount";

	/**
	 * The name indicate the percent of covered aminoacids
	 */
	public static final String COVERPERCENT = "CoverPercent";

	/**
	 * The name indicate the protein identification probability
	 */
	public static final String PROBABILITY = "ProteinProb";

	/**
	 * The name indicate the pI of protein
	 */
	public static final String PI = "pI";

	/**
	 * The name indicate the molecular weight of protein
	 */
	public static final String MW = "Mw";
	
	/**
	 * The name indicate the molecular weight of protein
	 */
	public static final String LENGTH = "Length";

	/**
	 * The name indicate is the protein reference is a target protein
	 */
	public static final String ISTARGET = "IsTarget?";

	/**
	 * The index of protein group in the created protein group collection
	 */
	public static final String GROUPIDX = "GroupIdx";

	/**
	 * How many proteins are crossed with this protein. The crossed proteins
	 * have same peptides for their identifications, but also have unique
	 * distinct peptides for each of their identifications. 
	 * 
	 */
	public static final String CROSSPROCOUNT = "CrossProCount";
	
	/**
	 * Spectral Index
	 */
	public static final String SIn = "SIn";

	/**
	 * The abundance ratio of label pairs.
	 */
	public static final String Ratio = "Ratio";

	/**
	 * The character used for the split of each score (or attribute) in peptide
	 */
	public static final char SEPARATOR = '\t';
	
	/**
	 * The hydropathy score of the protein.
	 */
	public static final String HydroScore = "HydroScore";

	/**
	 * Use this ReferenceDetailFormat to format the refDetail into a String for output.
	 * The separator between each score (or attribute) should be the tab
	 * character('\t');
	 * 
	 * @param IReferenceDetail
	 * @return formatted String for this protein instance
	 */
	public String format(IReferenceDetail refdetail);

	/**
	 * Format the first reference.
	 * @param refdetail
	 * @return
	 */
	public String format1(IReferenceDetail refdetail);
	/**
	 * Parse a formatted reference detail string into the ReferenceDetail instance.
	 * 
	 * @param refString
	 * @return
	 */
	public IReferenceDetail parse(String refString);
	
	public IReferenceDetail parse(String[] refString);
}
