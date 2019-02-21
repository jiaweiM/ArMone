/*
 * *****************************************************************************
 * File: IProteinDetailFormat.java * * * Created on 08-28-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;

/**
 * Formatter for protein
 * 
 * @author Xinning
 * @version 0.2.1, 09-22-2008, 21:25:58
 */
public interface IProteinFormat{

	/**
	 * Use this ProteinFormat to format the protein into a String for output.
	 * The separator between each score (or attribute) should be the tab
	 * character('\t');
	 * 
	 * @param protein
	 * @param indexstr the index string of this protein (e.g. "109" or "109a")
	 * @return formatted String for this protein instance
	 */
	public String format(Protein protein, String indexstr);

	/**
	 * Parse the formatted reference details and the peptide Strings into
	 * protein instance
	 * 
	 * @param refDetails
	 * @param peptideString
	 * @return
	 */
	public Protein parseProtein(String[] refDetails, String[] peptideStrings);
	
	public Protein parseProtein(String[][] refs, String[][] peps);
	
	/**
	 * The peptide format used to format the peptides in the protein
	 * 
	 * @return
	 */
	public IPeptideFormat<?> getPeptideFormat();
	
	
	/**
	 * The reference detail formatter used to format the referencedetail in the protein
	 * 
	 * @return
	 */
	public IReferenceDetailFormat getReferenceFormat();
	
	/**
	 * Title indicates name of each column. The title should be ouput first into
	 * a file for easy reading by users. Then the formatted reference string can
	 * be output.
	 * 
	 * @return
	 */
	public String getTitle();
}
