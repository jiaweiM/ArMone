/*
 * *****************************************************************************
 * File: IMascotPeptideReader.java * * * Created on 11-04-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotParameter;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader;

/**
 * Reader for Mascot outputted peptides. This is also a PeptideReader
 * 
 * @author Xinning
 * @version 0.1, 11-04-2008, 20:17:10
 */
public interface IMascotPeptideReader extends IPeptideReader{

	/**
	 * For easy handling, this method is added. However, this method returned
	 * the same instance as getPeptide(). And the call of these two methods will
	 * induce the same incident: an instance of IPeptide will be returned and
	 * the pointer of reader will move to the next IPeptide and being ready for
	 * next reading
	 * 
	 * @return the next Mascot peptide
	 * @throws PeptideParsingException
	 */
	public IMascotPeptide getPeptide() throws PeptideParsingException;
	
	
	/**
	 * The MascotParameter used in database search
	 * 
	 * @return
	 */
	public MascotParameter getSearchParameter();

}
