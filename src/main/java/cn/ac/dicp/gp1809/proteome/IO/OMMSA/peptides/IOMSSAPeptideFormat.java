/*
 * *****************************************************************************
 * File: IOMSSAPeptideFormat.java * * * Created on 08-31-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * A peptide format which inherits of IPeptideFormat
 * 
 * @author Xinning
 * @version 0.1.1, 09-22-2008, 21:45:32
 */
public interface IOMSSAPeptideFormat<Pep extends IOMSSAPeptide> extends
        IPeptideFormat<Pep> {

	/**
	 * The name indicating expected value
	 */
	public static final String E_VALUE = "E-value";

	/**
	 * The name indicating expected value
	 */
	public static final String P_VALUE = "P-value";

}
