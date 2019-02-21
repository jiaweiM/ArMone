/* 
 ******************************************************************************
 * File: IXTandemPhosPairFormat.java * * * Created on 02-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IXTandemPhosphoPeptidePair;

/**
 * The Hyperscore phospho pair formatter
 * 
 * @author Xinning
 * @version 0.1, 02-27-2009, 09:28:00
 */
public interface IXTandemPhosPairFormat extends
        IPhosPairFormat<IXTandemPhosphoPeptidePair> {

	/**
	 * The MS2 Hyperscore
	 */
	public static final String Hyperscore_MS2 = "Hyperscore_MS2";
	
	/**
	 * The ms3 Hyperscore
	 */
	public static final String Hyperscore_MS3 = "Hyperscore_MS3";
	
	
	/**
	 * merged Hyperscore
	 */
	public static final String Hyperscore_MERGE = "Hyperscore";
	
	
	/**
	 * The merged evalue
	 */
	public static final String EVALUE_MERGE = "EValue";
	
	/**
	 * The MS2 evalue
	 */
	public static final String EVALUE_MS2 = "EValue_MS2";
	
	/**
	 * The MS2 evalue
	 */
	public static final String EVALUE_MS3 = "EValue_MS3";
	
	
}
