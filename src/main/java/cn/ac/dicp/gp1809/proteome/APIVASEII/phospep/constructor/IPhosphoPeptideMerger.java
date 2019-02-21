/* 
 ******************************************************************************
 * File: IPhosphoPeptideMerger.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.IPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;

/**
 * Merger used to merge the peptides from MS2 and MS3 into PhosphoPeptidePairs
 * 
 * @author Xinning
 * @version 0.1, 02-18-2009, 15:01:28
 */
public interface IPhosphoPeptideMerger {

	/**
	 * Merge the peptides from corresponding MS2 and MS3 into phosphopeptide
	 * pair. Only report the top matched phosphopeptide pair.
	 * 
	 * @param scanms2
	 *            the scan number of ms2 for this peptide pair
	 * @param scanms3
	 *            the scan number of ms3 for this peptide pair
	 *            @param charge charge states of the 
	 * @param pepsMS2
	 *            the peptide identifications of ms2
	 * @param pepsMS3
	 *            the peptide identifications of ms3
	 * @param peaksMS2
	 *            the peak list of ms2 spectrum
	 * @param peaksMS3
	 *            the peak list of ms3 spectrum
	 * @param source
	 *            the source of the raw file (can be null)
	 * @return
	 */
	public IPhosPeptidePair merge(int scanms2, int scanms3, short charge,
	        IPeptide[] pepsMS2, IPeptide[] pepsMS3, IMS2PeakList peaksMS2,
	        IMS2PeakList peaksMS3);

	/**
	 * Get the AminoacidModification with new phosphorylation and neutral loss
	 * symbols.
	 * 
	 * @return
	 */
	public AminoacidModification getModificationNewSymbol();

	/**
	 * The aminoacids instance.
	 * 
	 * @return
	 */
	public Aminoacids getAminoacids();

	/**
	 * The phosphopeptide pair formatter
	 * 
	 * @return
	 */
	public IPhosPairFormat getPeptideFormatter();
}
