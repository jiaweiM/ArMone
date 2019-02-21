/*
 * *****************************************************************************
 * File: IInspectPeptideReader.java * * * Created on 03-24-2009
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.reader;

import cn.ac.dicp.gp1809.proteome.IO.Inspect.InspectParameter;
import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.IInspectPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader;

/**
 * Reader for Inspect outputted peptides. This is also a PeptideReader
 * 
 * @author Xinning
 * @version 0.1, 03-24-2009, 15:38:12
 */
public interface IInspectPeptideReader extends IPeptideReader{

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
	public IInspectPeptide getPeptide() throws PeptideParsingException;
	
	
	/**
	 * The InspectParameter used in database search
	 * 
	 * @return
	 */
	public InspectParameter getSearchParameter();

}
