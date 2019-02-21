/* 
 ******************************************************************************
 * File: IBatchDtaWriter.java * * * Created on 09-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * Writer for Dta file. There are several dta file format for different search
 * algorithms such as dta, mgf, pkl and so one, all these writers should
 * implements this interface.
 * 
 * @author Xinning
 * @version 0.1.1, 08-10-2009, 19:24:36
 */
public interface IBatchDtaWriter {
	
	/**
	 * The type of the dta writer
	 * 
	 * @return
	 */
	public DtaType getDtaType();
	
	/**
	 * Print the Dta file to the different formatted files one by one. After all
	 * dta files have been printed, use the {@link #close()} to finish the
	 * writing.
	 * 
	 * @param dtafile
	 * @throws DtaWritingException
	 * @return
	 */
	public void write(IScanDta dtafile) throws DtaWritingException;

	
	/**
	 * Close the dta file writer to finish the writing.
	 */
	public void close();

}
