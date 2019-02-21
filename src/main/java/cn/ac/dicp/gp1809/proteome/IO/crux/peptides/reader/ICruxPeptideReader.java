/*
 ******************************************************************************
 * File: ICruxPeptideReader.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides.reader;

import cn.ac.dicp.gp1809.proteome.IO.crux.CruxParameter;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader;

/**
 * Reader for Crux outputted peptides. This is also a PeptideReader
 * 
 * @author Xinning
 * @version 0.1, 04-02-2009, 14:28:27
 */
public interface ICruxPeptideReader extends IPeptideReader{

	/**
	 * For easy handling, this method is added. However, this method returned
	 * the same instance as getPeptide(). And the call of these two methods will
	 * induce the same incident: an instance of IPeptide will be returned and
	 * the pointer of reader will move to the next IPeptide and being ready for
	 * next reading
	 * 
	 * @return the next Inspect peptide
	 * @throws PeptideParsingException
	 */
	public ICruxPeptide getPeptide() throws PeptideParsingException;
	
	
	/**
	 * The CruxParameter used in database search
	 * 
	 * @return
	 */
	public CruxParameter getSearchParameter();

}
