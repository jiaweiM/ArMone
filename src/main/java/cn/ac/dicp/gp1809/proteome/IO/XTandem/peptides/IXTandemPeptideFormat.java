/*
 * *****************************************************************************
 * File: IXTandemPeptideFormat.java * * * Created on 10-06-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * A peptide format which inherits of IPeptideFormat
 * 
 * @author Xinning
 * @version 0.2, 11-03-2008, 10:25:55
 */
public interface IXTandemPeptideFormat<Pep extends IXTandemPeptide> extends
        IPeptideFormat<Pep> {

	/**
	 * The name indicating expected value
	 */
	public static final String E_VALUE = "E-value";

	/**
	 * The name indicating Hyper score 
	 */
	public static final String HYPERSCORE = "Hyperscore";
	
	
	/**
	 * The name indicating the Hyper score of the next top matched peptide
	 */
	public static final String NEXTSCORE = "Nextscore";
	
	/**
	 * 
	 */
	public static final String YSCORE = "yscore";
	
	/**
	 * 
	 */
	public static final String BSCORE = "bscore";

}
