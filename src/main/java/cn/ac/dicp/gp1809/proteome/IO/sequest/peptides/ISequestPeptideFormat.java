/* 
 ******************************************************************************
 * File: ISequestPeptideFormat.java * * * Created on 08-31-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * A peptide format which inherits of IPeptideFormat
 * 
 * @author Xinning
 * @version 0.1.1, 09-22-2008, 21:30:12
 */
public interface ISequestPeptideFormat<Pep extends ISequestPeptide> extends
        IPeptideFormat<Pep> {

	/**
	 * The name indicate the Xcorr
	 */
	public static final String XCORR = "Xcorr";

	/**
	 * The name indicate the DeltaCn
	 */
	public static final String DELTACN = "DeltaCn";

	/**
	 * The name indicate the Sp
	 */
	public static final String SP = "Sp";

	/**
	 * The name indicate the Rsp
	 */
	public static final String RSP = "Rsp";

}
