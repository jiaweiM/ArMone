/* 
 ******************************************************************************
 * File: ISequestPhosPairFormat.java * * * Created on 02-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ISequestPhosphoPeptidePair;

/**
 * The sequest phospho pair formatter
 * 
 * @author Xinning
 * @version 0.1, 02-27-2009, 09:28:00
 */
public interface ISequestPhosPairFormat extends
        IPhosPairFormat<ISequestPhosphoPeptidePair> {

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
	
}
