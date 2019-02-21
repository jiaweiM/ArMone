/*
 * *****************************************************************************
 * File: IMascotPeptideFormat.java * * * Created on 10-06-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * A peptide format which inherits of IPeptideFormat
 * 
 * @author Xinning
 * @version 0.1, 11-04-2008, 20:12:30
 */
public interface IMascotPeptideFormat<Pep extends IMascotPeptide> extends
        IPeptideFormat<Pep> {

	/**
	 * The name indicating expected value
	 */
	public static final String E_VALUE = "E-value";

	/**
	 * The name indicating ions score 
	 */
	public static final String IONSCORE = "Ions-score";
	
	/**
	 * Delta ion score
	 */
	public static final String deltaIS = "Delta-IS";
	
	/**
	 * The identity threshold of the mascot peptide.
	 */
	public static final String idenThres = "Identity Threshold";
	
	/**
	 * The homology threshold of the mascot peptide.
	 */
	public static final String homoThres = "Homology Threshold";
	
	/**
	 * The query identity number.
	 */
	public static final String queryIdenNum = "QueryIdenNum";

}
