/*
 * *****************************************************************************
 * File: IOMSSAPeptideReader.java * * * Created on 08-31-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.readers;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAParameter;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.OMSSAPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader;

/**
 * Reader for OMSSA outputted peptides. This is also a PeptideReader
 * 
 * @author Xinning
 * @version 0.1, 08-29-2008, 10:48:15
 */
public interface IOMSSAPeptideReader extends IPeptideReader{

	/**
	 * For easy handling, this method is added. However, this method returned
	 * the same instance as getPeptide(). And the call of these two methods will
	 * induce the same incident: an instance of IPeptide will be returned and
	 * the pointer of reader will move to the next IPeptide and being ready for
	 * next reading
	 * 
	 * @return the next OMSSA peptide
	 * @throws PeptideParsingException
	 */
	public OMSSAPeptide getPeptide() throws PeptideParsingException;
	
	
	/**
	 * The OMSSAParameter used in database search
	 * 
	 * @return
	 */
	public OMSSAParameter getSearchParameter();

}
