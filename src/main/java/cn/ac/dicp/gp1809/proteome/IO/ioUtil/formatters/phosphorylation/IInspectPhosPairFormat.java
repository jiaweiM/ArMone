/* 
 ******************************************************************************
 * File: IInspectPhosPairFormat.java * * * Created on 03-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IInspectPhosphoPeptidePair;

/**
 * The Inspect phospho pair formatter
 * 
 * @author Xinning
 * @version 0.1, 03-24-2009, 22:32:42
 */
public interface IInspectPhosPairFormat extends
        IPhosPairFormat<IInspectPhosphoPeptidePair> {

	/**
	 * The MS2 MQScore
	 */
	public static final String MQSCORE_MS2 = "MQScore_MS2";
	
	/**
	 * The ms3 MQScore
	 */
	public static final String MQSCORE_MS3 = "MQScore_MS3";
	
	
	/**
	 * merged MQScore
	 */
	public static final String MQSCORE_MERGE = "MQScores";
	
	
	/**
	 * The merged pvalue
	 */
	public static final String PVALUE_MERGE = "PValue";
	
	/**
	 * The MS2 pvalue
	 */
	public static final String PVALUE_MS2 = "PValue_MS2";
	
	/**
	 * The MS3 pvalue
	 */
	public static final String PVALUE_MS3 = "PValue_MS3";
	
	
}
