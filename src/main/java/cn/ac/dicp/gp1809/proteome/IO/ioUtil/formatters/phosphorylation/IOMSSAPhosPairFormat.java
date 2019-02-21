/* 
 ******************************************************************************
 * File: IOMSSAPhosPairFormat.java * * * Created on 02-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IOMSSAPhosphoPeptidePair;

/**
 * The OMSSA phospho pair formatter
 * 
 * @author Xinning
 * @version 0.1, 02-27-2009, 09:28:00
 */
public interface IOMSSAPhosPairFormat extends
        IPhosPairFormat<IOMSSAPhosphoPeptidePair> {

	/**
	 * The MS2 pvalue
	 */
	public static final String PVALUE_MS2 = "PValue_MS2";
	
	/**
	 * The ms3 pvalue
	 */
	public static final String PVALUE_MS3 = "PValue_MS3";
	
	
	/**
	 * merged pvalue
	 */
	public static final String PVALUE_MERGE = "PValue";
	
	
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
