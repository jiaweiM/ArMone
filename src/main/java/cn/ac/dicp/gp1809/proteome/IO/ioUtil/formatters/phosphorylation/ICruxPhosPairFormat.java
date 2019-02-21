/*
 ******************************************************************************
 * File: ICruxPhosPairFormat.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ICruxPhosphoPeptidePair;

/**
 * The crux phospho pair formatter
 * 
 * @author Xinning
 * @version 0.1, 04-02-2009, 22:31:18
 */
public interface ICruxPhosPairFormat extends
        IPhosPairFormat<ICruxPhosphoPeptidePair> {

	/**
	 * The MS2 Xcorr
	 */
	public static final String XCORR_MS2 = "Xcorr_MS2";
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String XCORR_MS3 = "Xcorr_MS3";
	
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String XCORR_MERGE = "Xcorr's";
	
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String DELTACAN_MS2 = "DeltaCn_MS2";
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String DELTACAN_MS3 = "DeltaCn_MS3";
	
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String DELTACAN_MERGE = "DeltaCn'm";
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String PVALUE_MS2 = "pvalue_MS2";
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String PVALUE_MS3 = "pvalue_MS3";
	
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String PVALUE_MERGE = "pvalue";
	
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String QVALUE_MS2 = "qvalue_MS2";
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String QVALUE_MS3 = "qvalue_MS3";
	
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String QVALUE_MERGE = "qvalue";
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String PERCOLATORSCORE_MS2 = "PercolatorScore_MS2";
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String PERCOLATORSCORE_MS3 = "PercolatorScore_MS3";
	
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String PERCOLATORSCORE_MERGE = "PercolatorScore";
	
}
