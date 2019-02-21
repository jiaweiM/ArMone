/* 
 ******************************************************************************
 * File: IPeptideListAccesser.java * * * Created on 11-24-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;

/**
 * The random accesser of peptide ins
 * 
 * @author Xinning
 * @version 0.1.1, 04-10-2009, 15:25:24
 */
public interface IPeptideListAccesser {
	/**
	 * Reading peptide instance with specific index for various of sources of
	 * different search algorithms.
	 * <p>
	 * For sequest, these include out, xml, xls, srf, ppl and ppls files.
	 * <p>
	 * For OMSSA and so on ...
	 * 
	 * 
	 * @See ProReaderFactory.
	 * @param idx
	 *            the 0 based index of the target peptides (0-n).
	 * @return
	 * @throws PeptideParsingException
	 */
	public IPeptide getPeptide(int idx) throws PeptideParsingException;

	/**
	 * Get the peak lists for specific peptide with index of idx.
	 * 
	 * @param 0 based index
	 * @return
	 */
	public IMS2PeakList[] getPeakLists(int idx);

	/**
	 * Get the peptide type for this reader.
	 * 
	 * @see PeptideType
	 * @return
	 */
	public PeptideType getPeptideType();

	/**
	 * Get the database search parameter for this peptide reader.
	 * 
	 * @return
	 */
	public ISearchParameter getSearchParameter();

	/**
	 * Get the file behind this peptide reader, in which all peptides are
	 * included.
	 * 
	 * @return
	 */
	public File getFile();

	/**
	 * Close the source of input for peptide reading
	 */
	public void close();

	/**
	 * Get the peptide format used for the peptide list string formating
	 * 
	 * @return
	 */
	public IPeptideFormat<?> getPeptideFormat();

	/**
	 * Number of peptides in the list
	 * 
	 * @return
	 */
	public int getNumberofPeptides();
}
