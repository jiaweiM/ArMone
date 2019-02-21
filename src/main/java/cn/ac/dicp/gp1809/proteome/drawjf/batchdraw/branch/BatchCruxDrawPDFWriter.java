/* 
 ******************************************************************************
 * File: BatchCruxDrawPDFWriter.java * * * Created on 05-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.branch;

import java.io.IOException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;

import com.itextpdf.text.DocumentException;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-18-2009, 15:56:24
 */
public class BatchCruxDrawPDFWriter extends BatchDrawPDFWriter {

	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_XCORR = "xcorr";

	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_PVALUE = "pvalue";

	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_PERCOLATOR = "percolator";

	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_IONS = "ions";

	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_QVALUE = "qvalue";

	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_RANK_XCORR = "rxc";
	
	/**
	 * Peptide index
	 */
	private int index = 1;

	protected BatchCruxDrawPDFWriter(String output, ISearchParameter parameter)
	        throws IOException, DocumentException {
		super(output, parameter, PeptideType.CRUX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.BatchDrawPDFWriter#getFieldMap
	 * (cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	protected HashMap<String, String> getFieldMap(IPeptide peptide) {

		ICruxPeptide cpep = (ICruxPeptide) peptide;

		HashMap<String, String> map = new HashMap<String, String>();

		map.put(FIELD_PAGEIDX, String.valueOf(index++));
		
		map.put(FIELD_SCAN, peptide.getScanNum());
		map.put(FIELD_CHARGE, String.valueOf(peptide.getCharge()));
		map.put(FIELD_DELTAMH, String.valueOf(peptide.getDeltaMH()));
		map.put(FIELD_Mr, String.valueOf(peptide.getMr()));
		map.put(FIELD_Score, String.valueOf(peptide.getPrimaryScore()));
		map.put(FIELD_NTT, String.valueOf(peptide.getNumberofTerm()));
		map.put(FIELD_MissCleaves, String.valueOf(peptide.getMissCleaveNum()));
		map.put(FIELD_PROTEINS, peptide.getProteinReferenceString());
		map.put(FIELD_RANK, String.valueOf(peptide.getRank()));
		map.put(FIELD_SEQUENCE, putPBefore(peptide.getSequence()));

		map.put(FIELD_IONS, cpep.getIons());
		map.put(FIELD_PERCOLATOR, String.valueOf(cpep.getPercolator_score()));
		map.put(FIELD_PVALUE, String.valueOf(cpep.getPValue()));
		map.put(FIELD_QVALUE, String.valueOf(cpep.getQValue()));
		map.put(FIELD_RANK_XCORR, String.valueOf(cpep.getRxc()));
		map.put(FIELD_XCORR, String.valueOf(cpep.getXcorr()));

		return map;
	}

}
