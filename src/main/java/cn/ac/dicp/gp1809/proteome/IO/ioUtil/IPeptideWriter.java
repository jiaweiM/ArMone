/* 
 ******************************************************************************
 * File: IPeptideWriter.java * * * Created on 01-03-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;

/**
 * Writer for peptides. All the peptide writer must implement this interface
 * 
 * @author Xinning
 * @version 0.3.1, 06-13-2009, 15:10:57
 */
public interface IPeptideWriter {

	/**
	 * Write peptide list information one by one; Note that, duplicated peptides
	 * identified from duplicated scans will be automatically removed. For
	 * example, in some cases, there may be duplicated spectra in mgf, and these
	 * duplicated spectra will identify duplicated peptides, only one of them
	 * will be output.
	 * <p>
	 * <b>In other word, at most, peptides with at least one different following
	 * informations, scannum, charge or sequence, can be output.</b>
	 * 
	 * @param peptide
	 */
	//	@Deprecated
	//	public void write(IPeptide peptide);

	/**
	 * Write peptide list information one by one; Note that, duplicated peptides
	 * identified from duplicated scans will be automatically removed. For
	 * example, in some cases, there may be duplicated spectra in mgf, and these
	 * duplicated spectra will identify duplicated peptides, only one of them
	 * will be output.
	 * <p>
	 * <b>In other word, at most, peptides with at least one different following
	 * informations, scannum, charge or sequence, can be output.</b>
	 * 
	 * @param peptide
	 *            the peptide
	 * @param PeakList
	 *            (can be null) peaklist array for this peptide identification.
	 *            For normal peptides, there should be only one peak list from a
	 *            spectrum, for MS2/MS3 peptides, the peak list number should be
	 *            two.
	 * @return whether the peptide is written by the writer
	 */
	public boolean write(IPeptide peptide, IMS2PeakList[] peaklist);

	/**
	 * The search parameter
	 * 
	 * @return
	 */
	public ISearchParameter getSearchParameter();

	public void setDataType(DataType dataType);
		
	/**
	 * Close the output file when writing is complete;
	 */
	public void close() throws ProWriterException;

}
