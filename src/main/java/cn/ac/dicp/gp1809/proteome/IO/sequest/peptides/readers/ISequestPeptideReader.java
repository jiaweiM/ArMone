/* 
 ******************************************************************************
 * File: ISequestPeptideReader.java * * * Created on 08-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;

/**
 * Reader for Sequest outputted peptides. This is also a PeptideReader
 * 
 * @author Xinning
 * @version 0.1, 08-29-2008, 10:48:15
 */
public interface ISequestPeptideReader extends IPeptideReader {

	/**
	 * For easy handling, this method is added. However, this method returned
	 * the same instance as getPeptide(). And the call of these two methods will
	 * induce the same incident: an instance of IPeptide will be returned and
	 * the pointer of reader will move to the next IPeptide and being ready for
	 * next reading
	 * 
	 * @return
	 * @throws PeptideParsingException
	 */
	public SequestPeptide getPeptide() throws PeptideParsingException;
	
	
	/**
	 * Get the sequest search parameter.
	 * 
	 * @see IPeptideReader
	 *      #getSearchParameter()
	 */
	@Override
	public SequestParameter getSearchParameter();
}
