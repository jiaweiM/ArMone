/*
 ******************************************************************************
 * File: IDataForInput.java * * * Created on 08-07-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;

/**
 * SFOER input
 * 
 * @author Xinning
 * @version 0.1, 08-07-2009, 17:58:13
 */
public interface IDataForInput {
	
	/**
	 * In the optimization of sequest filters, charge must be specified.
	 * In other word, the optimization is charge specified.
	 * 
	 * Currently, values of each peptide contains xcorr dcn sp rsp ions and 
	 * true or false value.
	 * 
	 * @param charge
	 * @return peptides with charge state specified;
	 */
	public float[][] getPeptide(IPeptideListReader reader, short charge) throws PeptideParsingException;
	
	/**
	 * In the optimization of sequest filters, charge must be specified.
	 * In other word, the optimization is charge specified.
	 * 
	 * Currently, values of each peptide contains xcorr dcn sp rsp ions and 
	 * true or false value.
	 * 
	 * @param charge charge state;
	 * @param ntt: trypsin cleavage type;(0 for no enzyme, 1 for partial tryptic 2 for full)
	 * @return peptides with charge and ntt specified;(full tryptic does not belong to no enzyme)
	 */
	public float[][] getPeptide(IPeptideListReader reader, short charge, short ntt) throws PeptideParsingException;
	
}
