/*
 ******************************************************************************
 * File: ICruxPeptideFormat.java * * * Created on 04-01-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * A peptide format which inherits of IPeptideFormat
 * 
 * @author Xinning
 * @version 0.1, 04-01-2009, 22:17:20
 */
public interface ICruxPeptideFormat<Pep extends ICruxPeptide> extends
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

	/**
	 * The name indicate the rank of xcorr
	 */
	public static final String RXC = "Rxc";

	/**
	 * The name indicate the pvalue
	 */
	public static final String P_VALUE = "Pvalue";

	/**
	 * The name indicate the qvalue
	 */
	public static final String Q_VALUE = "Qvalue";

	/**
	 * The name indicate the percalotar score
	 */
	public static final String PERCALOTAR_VALUE = "Percalotar_value";

	/**
	 * If the identification information contains xcorr score information. The
	 * crux has different score assignment, may be xcorr dcn sp, dcn pvalue
	 * xcorr or dcn percolatorscore qscore. The formatter contains methods to
	 * test whether these scores have been calculated.
	 * 
	 * @return
	 */
	public boolean isContainsXcorr();

	/**
	 * If the identification information contains Sp score information. The crux
	 * has different score assignment, may be xcorr dcn sp, dcn pvalue xcorr or
	 * dcn percolatorscore qscore. The formatter contains methods to test
	 * whether these scores have been calculated.
	 * 
	 * @return
	 */
	public boolean isContainsSp();

	/**
	 * If the identification information contains DeltaCn score information. The
	 * crux has different score assignment, may be xcorr dcn sp, dcn pvalue
	 * xcorr or dcn percolatorscore qscore. The formatter contains methods to
	 * test whether these scores have been calculated.
	 * 
	 * @return
	 */
	public boolean isContainsDeltaCn();

	/**
	 * If the identification information contains Pvalue score information. The
	 * crux has different score assignment, may be xcorr dcn sp, dcn pvalue
	 * xcorr or dcn percolatorscore qscore. The formatter contains methods to
	 * test whether these scores have been calculated.
	 * 
	 * @return
	 */
	public boolean isContainsPvalue();

	/**
	 * If the identification information contains Qvalue score information. The
	 * crux has different score assignment, may be xcorr dcn sp, dcn pvalue
	 * xcorr or dcn percolatorscore qscore. The formatter contains methods to
	 * test whether these scores have been calculated.
	 * 
	 * @return
	 */
	public boolean isContainsQvalue();

	/**
	 * If the identification information contains PercolatorScore information.
	 * The crux has different score assignment, may be xcorr dcn sp, dcn pvalue
	 * xcorr or dcn percolatorscore qscore. The formatter contains methods to
	 * test whether these scores have been calculated.
	 * 
	 * @return
	 */
	public boolean isContainsPercolatorScore();

}
