/*
 ******************************************************************************
 * File: SequestBatchDtaReader.java * * * Created on 05-30-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * Reader for the sequest output dta files in the directory. Compared with
 * BatchDtaOutReader in ioUtil package, this reander regardless the validation
 * of sequest directory (dta must equal to out). should with potential usage.
 * 
 * @author Xinning
 * @version 0.1.3, 03-30-2009, 20:49:26
 */
public class SequestBatchDtaReader implements IBatchDtaReader {
	/**
	 * List of dta files in this directory
	 */
	protected File[] dtafiles;

	/**
	 * Map for the names and files
	 */
	protected HashMap<String, File> nameMap;

	private int totalFileCount;

	private int curtDtaFileIndex;//current file number
	//CurtFile which have been just readin and returned and outfile instance
	private File curtDtaFile;
	/**
	 * The directory file
	 */
	private File dir;

	/**
	 * Create a BatchDtaReader for a director of sequest output
	 * 
	 * @param dir
	 */
	public SequestBatchDtaReader(String dir) {
		this(new File(dir));
	}

	/**
	 * Create a BatchDtaReader for a director of sequest output
	 * 
	 * @param dir
	 */
	public SequestBatchDtaReader(File dir) {
		this.dir = dir;
		this.dtafiles = dir.listFiles(new SequestDtaFilenameFilter());
		this.totalFileCount = this.dtafiles.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dta.IBatchDtaReader#getNameofCurtDtaFile()
	 */
	@Override
	public String getNameofCurtDta() {
		if (this.curtDtaFile == null)
			return null;
		return this.curtDtaFile.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.dta.IBatchDtaReader#getNextDta(boolean)
	 */
	@Override
	public SequestScanDta getNextDta(boolean isIncludePeakList)
	        throws DtaFileParsingException {
		if (this.curtDtaFileIndex >= this.totalFileCount) {
			this.curtDtaFile = null;
			return null;
		}

		curtDtaFile = this.dtafiles[this.curtDtaFileIndex++];
		SequestDtaReader dtareader = null;
		try {
			dtareader = new SequestDtaReader(curtDtaFile);
		} catch (FileNotFoundException e) {
			throw new DtaFileParsingException("The dta file is unreachable: "
			        + curtDtaFile.getName(), e);
		}
		return dtareader.getDtaFile(isIncludePeakList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.dta.IBatchDtaReader#getNumberofDtaFiles()
	 */
	@Override
	public int getNumberofDtas() {
		return this.dtafiles.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#close()
	 */
	@Override
	public void close() {
		//do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getDtaType()
	 */
	@Override
	public DtaType getDtaType() {
		return DtaType.DTA;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getFile()
	 */
	@Override
	public File getFile() {
		return dir;
	}
}
