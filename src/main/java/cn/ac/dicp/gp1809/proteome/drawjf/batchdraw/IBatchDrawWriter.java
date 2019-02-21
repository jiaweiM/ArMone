/* 
 ******************************************************************************
 * File: IBatchWriter.java * * * Created on 04-16-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.batchdraw;

import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;

/**
 * The batch DrawWriter
 * 
 * @author Xinning
 * @version 0.2, 07-21-2009, 14:13:20
 */
public interface IBatchDrawWriter {

	/**
	 * Drawer for html
	 */
	public int HTML = 1;

	/**
	 * Drawer for PDF
	 */
	public int PDF = 0;

	/**
	 * Write the peptide and matched spectrum
	 * 
	 * @param peptide
	 * @param peaklists
	 * @param types
	 * @throws IOException
	 */
	public void write(IPeptide peptide, IMS2PeakList[] peaklists, int[] types)
	        throws IOException;

	/**
	 * Write the peptide and matched spectrum.
	 * 
	 * @param peptide
	 * @param threshold
	 * @throws IOException
	 */
	public void write(IPeptide peptide, IMS2PeakList[] peaklists, int[] types,
	        ISpectrumThreshold threshold) throws IOException;

	/**
	 * Write the peptide and matched spectrum
	 * 
	 * @param peptide
	 * @param losses
	 * @throws IOException
	 */
	public void write(IPeptide peptide, IMS2PeakList[] peaklists, int[] types,
	        NeutralLossInfo[] losses) throws IOException;

	/**
	 * Write the peptide and matched spectrum
	 * 
	 * @param peptide
	 * @param threshold
	 * @param losses
	 * @throws IOException
	 */
	public void write(IPeptide peptide, IMS2PeakList[] peaklists, int types[],
	        ISpectrumThreshold threshold, NeutralLossInfo[] losses)
	        throws IOException;

	/**
	 * Close
	 * 
	 */
	public void close();
}
