/*
 ******************************************************************************
 * File: IBatchDtaReader.java * * * Created on 05-01-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * Batch dta reader for the sequest out put dta directory which contains a bulk
 * of dta files.
 * 
 * @author Xinning
 * @version 0.2, 03-30-2009, 20:45:21
 */
public interface IBatchDtaReader {

	/**
	 * The type of Dta file.
	 * 
	 * @see DtaType
	 * @since 0.2
	 * @return
	 */
	public DtaType getDtaType();

	/**
	 * The file contains the dta. For sequest dta format, the file will be the
	 * directory file contains the dta. For mgf, ms2 and other files containing
	 * multiple dta in a single file, this file is the specific file.
	 * 
	 * @since 0.2
	 * @return
	 */
	public File getFile();

	/**
	 * The dta files in the directory will be read one by one until all the dta
	 * files have been read in. Then null will be returned.
	 * 
	 * @param isIncludePeakList
	 * @return
	 */
	public IScanDta getNextDta(boolean isIncludePeakList) throws DtaFileParsingException;

	/**
	 * The total number of dta files for reading
	 * 
	 * @return
	 */
	public int getNumberofDtas();

	/**
	 * The name of current dta file which have been reading to return an
	 * DtaFile. This method should be called after the execution of getNext().
	 * <p>
	 * <b>If the dta files are generated from srf file, the name should be
	 * transformed to the standard formatted name:
	 * "Basename.scanNumBeg.scanNumEnd.charge.dta"</b>
	 * 
	 * @return
	 */
	public String getNameofCurtDta();

	/**
	 * Close after reading
	 */
	public void close();

}
