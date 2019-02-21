/*
 * *****************************************************************************
 * File: IXTandemPeptideReader.java * * * Created on 10-06-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.readers;


import cn.ac.dicp.gp1809.proteome.IO.XTandem.XTandemParameter;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.XTandemPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader;

/**
 * Reader for OMSSA outputted peptides. This is also a PeptideReader
 * 
 * @author Xinning
 * @version 0.1, 10-06-2008, 11:04:16
 */
public interface IXTandemPeptideReader extends IPeptideReader{

	/**
	 * For easy handling, this method is added. However, this method returned
	 * the same instance as getPeptide(). And the call of these two methods will
	 * induce the same incident: an instance of IPeptide will be returned and
	 * the pointer of reader will move to the next IPeptide and being ready for
	 * next reading
	 * 
	 * @return the next XTandem peptide
	 * @throws PeptideParsingException
	 */
	public XTandemPeptide getPeptide() throws PeptideParsingException;
	
	
	/**
	 * The XTandemParameter used in database search
	 * 
	 * @return
	 */
	public XTandemParameter getSearchParameter();

}
