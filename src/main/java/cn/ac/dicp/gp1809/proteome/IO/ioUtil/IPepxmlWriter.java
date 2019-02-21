/* 
 ******************************************************************************
 * File: IPepxmlWriter.java * * * Created on 08-28-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.util.ioUtil.xml.XmlWritingException;

/**
 * A writer for Pepxml with the format defined by ISB
 * 
 * @author Xinning
 * @version 0.3, 05-02-2010, 10:56:40
 */
public interface IPepxmlWriter extends IPeptideWriter {

	/**
	 * The Date formatter "yyyy-MM-dd'T'HH:mm:ss"
	 */
	public DateFormat DATF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	/**
	 * Write the header informations
	 * 
	 * 
	 * @throws XmlWritingException
	 */
	public void writeHeader() throws XmlWritingException;

	/**
	 * Write a peptide to the pepxml file. Equals to
	 * {@link #write(IPeptide, cn.ac.dicp.gp1809.proteome.spectrum.IPeakList[])}
	 * , peptide list is useless
	 * 
	 * @param pep
	 */
	public boolean write(IPeptide pep);

}