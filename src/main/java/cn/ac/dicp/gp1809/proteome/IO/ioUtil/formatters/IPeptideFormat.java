/*
 * *****************************************************************************
 * File: IPeptideFormat.java * * * Created on 09-22-2008 Copyright (c) 2008
 * Xinning Jiang (vext@163.com) All right reserved. Use is subject to license
 * terms.
 * *****************************************************************************
 * *
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * Format a Peptide instance into a String for output to outputstream and parse
 * a input peptide string into the Peptide instance.
 * 
 * <p>
 * Changes:
 * <li>0.4.2, 02-27-2009: Add term of {@link #SOURCE}
 * 
 * @author Xinning
 * @version 0.4.3, 08-23-2009, 16:08:44
 */
public interface IPeptideFormat<Pep extends IPeptide> extends IFormat<Pep> {

	/**
	 * The term indicate the scan number
	 */
	public static final String SCAN = "Scan(s)";

	/**
	 * The term indicate the Sequence
	 */
	public static final String SEQUENCE = "Sequence";

	/**
	 * The term indicate the charge
	 */
	public static final String CHARGE = "Charge";

	/**
	 * The term indicate the delta MH+
	 */
	public static final String DELTAMH = "DeltaMH+";

	/**
	 * The term indicate the mass error (ppm)
	 */
	public static final String DELTAPPM = "Mass error (ppm)";

	/**
	 * The term indicate the rank of peptide by primary score
	 */
	public static final String RANK = "Rank";

	/**
	 * The term indicate the MH
	 */
	public static final String MH = "MH+";

	/**
	 * The term indicate the Ions
	 */
	public static final String IONS = "Ions";

	/**
	 * The term indicate the Sim
	 */
	public static final String SIM = "Sim";

	/**
	 * The term indicate the Ascore
	 */
	public static final String ASCORE = "Ascore";

	/**
	 * The term indicate the Protein references
	 */
	public static final String PROTEINS = "Proteins";

	/**
	 * The term indicate the Number of enzymatic terms
	 */
	public static final String NUM_TERMS = "NumofTerms";

	/**
	 * The term indicate the pi
	 */
	public static final String PI = "pI";

	/**
	 * The term indicate the probability
	 */
	public static final String PROB = "Prob";

	/**
	 * There may be more than one raw files, give this term to show which raw
	 * file identified this peptide.
	 */
	public static final String SOURCE = "Source";

	/**
	 * While the peptide can indicate more than one proteins, the name of each
	 * protein is splited by this char and return a String;
	 */
	public static final char ProteinNameSpliter = '$';

	/**
	 * The character used for the split of each score (or attribute) in peptide
	 */
	public static final char SEPARATOR = '\t';

	/**
	 * This intensity indicate the intensity of the precursor ion.
	 */
	public static final String inten = "PrecursorInten";

	/**
	 * This intensity indicate the total intensity of ms2 fragment ions.
	 */
	public static final String fragmentInten = "FragmentInten";

	/**
	 * The label information of the peptide.
	 */
	public static final String LabelInfo = "LabelInfo";

	/**
	 * The hydropathy score of the peptide/
	 */
	public static final String HydroScore = "HydroScore";

	/**
	 * The retention time of the peptide.
	 */
	public static final String retentionTime = "retentionTime";

	public static final String matchedIons = "matchedIons";

	public static final String peaksFromIons1 = "peaksFromIons1";

	public static final String peaksFromIons2 = "peaksFromIons2";

	public static final String peaksFromIons3 = "peaksFromIons3";

	/**
	 * 
	 */
	// public static final String peptideBeg = "Peptide begin";

	/**
	 * 
	 */
	// public static final String peptideEnd = "Peptide end";

	/**
	 * Use this PeptideFormat to format the peptide into a String for output.
	 * The separator between each score (or attribute) should be the tab
	 * character('\t');
	 * 
	 * @param peptide
	 * @return formatted String for this peptide instance
	 */
	public String format(Pep peptide);

	/**
	 * Use this PeptideFormat to format the peptide into a String with less
	 * information than <b>format(Pep peptide)</b> for output.
	 * 
	 * @param peptide
	 * @return
	 */
	public String simpleFormat(Pep peptide);

	/**
	 * Get the title for the simple format.
	 * 
	 * @return
	 */
	public String getSimpleFormatTitle();

	/**
	 * Parse a formatted peptide string into the Peptide instance.
	 * 
	 * @param pepstring
	 * @return
	 */
	public Pep parse(String pepstring);

	public Pep parse(String[] pepstring);

	/**
	 * The type of peptide of this peptide format.
	 * 
	 * @return the type of this peptide format
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType
	 */
	public PeptideType type();

	/**
	 * Get the index of column for the term.
	 * 
	 * @see PeptideFormat.Terms
	 * 
	 * @param term
	 * @return the index of column. If this term is not included, return -1
	 */
	public int getIndex(String term);

	public HashMap<String, Integer> getIndexMap();

}
