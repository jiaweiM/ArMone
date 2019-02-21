/* 
 ******************************************************************************
 * File: ISequestReader.java * * * Created on 05-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest;

import cn.ac.dicp.gp1809.proteome.IO.sequest.out.IBatchOutReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFile;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFileReadingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;

/**
 * Reader for SEQUEST output. Both dta and out files are read in.
 * 
 * <p>
 * 1. In the old version of sequest, raw fileswill first be extracted into bulk
 * of dta files containing peak informations, and then these dta files are
 * searched against fasta database and output these peptides into out files with
 * the same base name (except the extension is dta for dta file and out for out
 * file). Therefore, a valid directory after sequest should contain the same
 * number of dta files and out files with the exactly file name.
 * <p>
 * 2. Another new type of sequest output is the srf file, in which all the dta
 * files and out files are merged together into a single file, this file is
 * convenient for retain and move.
 * 
 * 
 * @author Xinning
 * @version 0.1.3, 11-14-2008, 14:29:14
 */
public interface ISequestReader extends IBatchOutReader, IBatchDtaReader {

	/**
	 * Validate the Sequest search directory or srf file. And generate some
	 * useful informations.
	 * 
	 * <p>
	 * A valid directory should contain equal number of dta and out files with
	 * the same base name. For SRF file, the dta and out entries should be with
	 * the same number
	 * 
	 * <p>
	 * This method need to be included in the constructor, and need not to be
	 * called else where
	 * 
	 * @return
	 */
	public boolean isValid();

	/**
	 * Get the OutFile instance for the specific DtaFile. Because the Sequest
	 * files has been validated, the OutFile can be generated.
	 * 
	 * <p>
	 * <b>One should be noted is that, the DtaFile must also be generated from
	 * this reader</b>
	 * 
	 * @param dta
	 * @return
	 */
	public OutFile getOutFileForDta(SequestScanDta dta) throws OutFileReadingException;

	/**
	 * Get the DtaFile instance for the specific OutFile. Because the Sequest
	 * files has been validated, the DtaFile can be generated.
	 * 
	 * <p>
	 * <b>One should be noted is that, the OutFile must also be generated from
	 * this reader</b>
	 * 
	 * @param out
	 * @param isIncludePeakList
	 * @return
	 */
	public SequestScanDta getDtaFileForOut(OutFile out, boolean isIncludePeakList)
	        throws DtaFileParsingException;

}
