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

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IMascotPhosphoPeptidePair;

/**
 * The mascot phospho pair formatter
 * 
 * @author Xinning
 * @version 0.1, 02-27-2009, 09:28:00
 */
public interface IMascotPhosPairFormat extends
        IPhosPairFormat<IMascotPhosphoPeptidePair> {

	/**
	 * The MS2 ionscore
	 */
	public static final String IONSCORE_MS2 = "Ionscore_MS2";
	
	/**
	 * The ms3 ionscore
	 */
	public static final String IONSCORE_MS3 = "Ionscore_MS3";
	
	
	/**
	 * merged ionscore
	 */
	public static final String IONSCORE_MERGE = "Ionscores";
	
	
	/**
	 * The merged evalue
	 */
	public static final String EVALUE_MERGE = "EValue";
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String EVALUE_MS2 = "EValue_MS2";
	
	/**
	 * The MS2 Xcorr
	 */
	public static final String EVALUE_MS3 = "EValue_MS3";
	
	
}
