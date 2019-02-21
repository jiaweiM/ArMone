/*
 ******************************************************************************
 * File: DtaPrecursorIonMassChanger.java * * * Created on 12-29-2007
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.util;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtawriter;
import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;

/**
 * After generating dta files from raw by bioworks, we can use this class to
 * change the peak mz values.
 * 
 * @author Xinning
 * @version 0.1.1, 05-25-2010, 16:08:54
 */
public class DtaPrecursorIonMassChanger {

	private SequestDtawriter writer;

	public DtaPrecursorIonMassChanger() {
		writer = new SequestDtawriter();
	}

	/**
	 * Change the MZ value to the new value for the specific dta file
	 * 
	 * @param dtaFile
	 * @param newMZ
	 * @throws DtaFileParsingException
	 * @throws IOException
	 * @throws DtaWritingException 
	 */
	public void changeMZ(File dtaFile, double newMZ)
	        throws DtaFileParsingException, IOException, DtaWritingException {
		SequestDtaReader reader = new SequestDtaReader(dtaFile);
		IScanDta dta = reader.getDtaFile(true);
		dta.setPrecursorMZ(newMZ);

		this.reNewDtaFile(dtaFile, dta);
	}

	/**
	 * 
	 * 
	 * @param dtaFile
	 * @param dta
	 * @throws IOException
	 * @throws DtaWritingException 
	 */
	private void reNewDtaFile(File dtaFile, IScanDta dta) throws IOException, DtaWritingException {
		File tmp = FileUtil.getTempFile();

		this.writer.write(dta, tmp.getAbsolutePath());

		if (dtaFile.delete()) {
			tmp.renameTo(dtaFile);
		} else {
			throw new RuntimeException(
			        "The original dta file can not be rewritten!");
		}
	}

	/**
	 * Change the MH value to the new value for the specific dta file
	 * 
	 * @param dtaFile
	 * @param newMZ
	 * @throws DtaFileParsingException
	 * @throws IOException
	 * @throws DtaWritingException 
	 */
	public void changeMH(File dtaFile, double newMH)
	        throws DtaFileParsingException, IOException, DtaWritingException {
		SequestDtaReader reader = new SequestDtaReader(dtaFile);
		IScanDta dta = reader.getDtaFile(true);
		dta.setPrecursorMH(newMH);

		this.reNewDtaFile(dtaFile, dta);
	}

	/**
	 * Change the MH value to add a specific value for the specific dta file
	 * 
	 * @param dtaFile
	 * @param newMZ
	 */
	public void addMH(File dtaFile, double addMH) {

	}

	/**
	 * Change the MZ value to add a specific value for the specific dta file
	 * 
	 * @param dtaFile
	 * @param newMZ
	 */
	public void addMZ(File dtaFile, double addMz) {

	}
}
