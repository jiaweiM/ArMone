/*
 * *****************************************************************************
 * File: IInspectPeptideFormat.java * * * Created on 03-24-2009
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * A peptide format which inherits of IPeptideFormat
 * 
 * @author Xinning
 * @version 0.1, 03-24-2009, 15:29:10
 */
public interface IInspectPeptideFormat<Pep extends IInspectPeptide> extends
        IPeptideFormat<Pep> {

	/**
	 * The name indicating pvalue
	 */
	public static final String P_VALUE = "Pvalue";

	/**
	 * The name indicating MQScore
	 */
	public static final String MQSCORE = "MQScore";
	
	/**
	 * The name indicating FScore
	 */
	public static final String FSCORE = "FScore";

}
