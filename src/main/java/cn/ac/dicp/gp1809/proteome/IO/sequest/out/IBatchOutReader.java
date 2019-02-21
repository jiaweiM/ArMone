/*
 * *****************************************************************************
 * File: IBatchOutReader.java * * * Created on 09-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.out;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;

/**
 * Batch out reader for the sequest output out directory which contains a bulk
 * of out files.
 * 
 * @author Xinning
 * @version 0.2.1, 09-14-2010, 18:52:53
 */
public interface IBatchOutReader {

	/**
	 * Get the container of out files for this batch out reader. These
	 * containers may be sequest dta_out search directory, srf file or zipped
	 * dta_out file.
	 * 
	 * @return the file behind this out reader
	 */
	public File getFile();

	/**
	 * The out files in the directory will be read one by one until all the out
	 * files have been read in. Then null will be returned.
	 * 
	 * @return
	 * @throws OutFileReadingException
	 */
	public OutFile getNextOut() throws OutFileReadingException;

	/**
	 * The total number of out files for reading
	 * 
	 * @return
	 */
	public int getNumberofOutFiles();

	/**
	 * Get the parameters used for database search.
	 * 
	 * 
	 * @return The parameter used for database search.
	 * @throws ParameterParseException
	 *             if some exception occurs while parsing the parameter
	 */
	public SequestParameter getSearchParameters();

	/**
	 * The name of current out file which have been reading to return an
	 * OutFile. This method should be called after the execution of getNext().
	 * <p>
	 * <b>If the out files are generated from srf file, the name should be
	 * transformed to the standard formatted name:
	 * "Basename.scanNumBeg.scanNumEnd.charge.out"</b>
	 * 
	 * @return
	 */
	public String getNameofCurtOutFile();
	
	/**
	 * The judger for target decoy system
	 * 
	 * @param judger
	 */
	public void setDecoyJudger(IDecoyReferenceJudger judger);
	
	/**
	 * The judger of target decoy system
	 * 
	 * @return
	 */
	public IDecoyReferenceJudger getDecoyJudger();

	/**
	 * Close the stream for reading. Mainly for SRF reader. Out reader can leave
	 * it blank
	 */
	public void close();
}
