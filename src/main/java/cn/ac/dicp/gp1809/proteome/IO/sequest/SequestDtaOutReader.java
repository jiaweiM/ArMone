/*
 * *****************************************************************************
 * File: SequestDtaOutReader.java * * * Created on 09-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequestFileIllegalException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.AbstractBatchOutReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFile;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFileReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFileReadingException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFilenameFilter;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaFilenameFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * Reader for Sequest database searching directory. The valid search directory
 * should contain equal number of dta and out files with the same base name
 * (except the extension)
 * 
 * 
 * @author Xinning
 * @version 0.2, 08-11-2009, 16:13:39
 */
public class SequestDtaOutReader extends AbstractBatchOutReader implements ISequestReader {

	protected File dir;

	protected boolean isValid;

	private SequestParameter param;

	/**
	 * List of dta files in this directory
	 */
	protected File[] dtafiles;
	/**
	 * List of out files in this directory
	 */
	protected File[] outfiles;

	private int totalFileCount;

	private int curtOutFileIndex;// current file number
	// CurtFile which have been just readin and returned and outfile instance
	private File curtOutFile;

	private int curtDtaFileIndex;// current file number
	// CurtFile which have been just readin and returned and outfile instance
	private File curtDtaFile;
	
	private IFastaAccesser accesser;

	/**
	 * Create a reader for the sequest search directory.
	 * 
	 * @param dir
	 * @throws SequestFileIllegalException
	 */
	public SequestDtaOutReader(String dir) throws SequestFileIllegalException {
		this(dir == null ? null : new File(dir));
	}

	public SequestDtaOutReader(File file) throws SequestFileIllegalException {
		this.validate(file);
	}
	
	public SequestDtaOutReader(String dir, IFastaAccesser accesser) throws SequestFileIllegalException {
		this(dir == null ? null : new File(dir), accesser);
	}

	public SequestDtaOutReader(File file, IFastaAccesser accesser) throws SequestFileIllegalException {
		this.validate(file);
		this.accesser = accesser;
	}

	/**
	 * Valid the directory
	 * 
	 * @return always be TRUE. If not valid, exceptions will be threw.
	 * @throws SequestFileIllegalException
	 *             is the directory is not a valid sequest search directory: 1.
	 *             the number of dta and out file is not the same. 2. contains
	 *             no sequest search parameter or with more one params
	 * @throws NullPointerException
	 *             is the directory is null or not exist.
	 * @throws IllegalArgumentException
	 *             is the input file is not a directory file.
	 */
	protected boolean validate(File file) throws SequestFileIllegalException {
		File paramfile = null;

		if (file == null || !file.exists())
			throw new NullPointerException(
			        "The sequest directory is Null or does not exist.");

		if (file.isFile())
			throw new IllegalArgumentException(
			        "The valid input should be a sequest search directory.");

		this.dtafiles = file.listFiles(new SequestDtaFilenameFilter());
		this.outfiles = file.listFiles(new OutFilenameFilter());

		
		/*
		 * Since 0.2, the number of dta and out will not be compared.
		 */
		if (this.dtafiles.length != this.outfiles.length) {

			System.err
			        .println("Warnning: The numbers of dta files and out files are not identical: Dta="
			                + this.dtafiles.length
			                + "; Out: "
			                + this.outfiles.length);

			/*
			 * if (!valid) {
			 * 
			 * throw new SequestFileIllegalException(
			 * "There should be identical number of" +
			 * " dta and out files. Dta: " + this.dtafiles.length + "; Out: " +
			 * this.outfiles.length);
			 * 
			 * }
			 */
		}
		
		
		boolean valid = false;
		File[] params = file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith("params"))
					return true;
				return false;
			}
		});

		int len = params.length;
		if (len != 0) {
			if (len == 1) {
				valid = true;
				paramfile = params[0];
			} else {
				for (int i = 0; i < len; i++) {
					File f = params[i];
					if (f.getName().equals("sequest.params")) {
						if (paramfile == null) {
							paramfile = f;
							valid = true;//
						} else {// Can't determine which one is used for
							// database seach.
							valid = false;
						}
					}
				}

				if (valid) {
					System.out
					        .println("Warnning: More than one .params files are contained in current"
					                + " directory, the default one \"sequest.params\" is selected");
				} else {
					
					System.err
			        .println("Warnning: More than one .params files are contained in current"
			                + " directory, \""+ params[0].getName()+"\" is selected");
					
					/*
					throw new SequestFileIllegalException(
					        "Error: There are more than one params files"
					                + " in current directory!");
					                */
				}
			}
		}
		

		this.dir = file;
		this.totalFileCount = this.outfiles.length;

		try {
			this.param = new SequestParameter().readFromFile(paramfile);
		} catch (ParameterParseException e) {
			throw new SequestFileIllegalException(
			        "Exceptions occur while parsing the params file", e);
		}

		return valid;
	}

	public boolean isValid() {
		return this.isValid;
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
	 * @see cn.ac.dicp.gp1809.proteome.out.IBatchOutReader#getNext()
	 */
	public OutFile getNextOut() throws OutFileReadingException {
		if (this.curtOutFileIndex >= this.totalFileCount) {
			this.curtOutFile = null;
			return null;
		}

		curtOutFile = this.outfiles[this.curtOutFileIndex++];
		OutFileReader reader = null;
		try {
			reader = new OutFileReader(curtOutFile, this.getDecoyJudger(), accesser);
		} catch (FileNotFoundException e) {
			throw new OutFileReadingException("The out file is unreachable: "
			        + curtOutFile.getName(), e);
		}
		return reader.getOutFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.out.IBatchOutReader#getNumberofFiles()
	 */
	public int getNumberofOutFiles() {
		return this.totalFileCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.out.IBatchOutReader#getNameofCurtFile()
	 */
	public String getNameofCurtOutFile() {
		if (this.curtOutFile == null)
			return null;

		return this.curtOutFile.getName();
	}

	@Override
	public void close() {
	}

	@Override
	public OutFile getOutFileForDta(SequestScanDta dta)
	        throws OutFileReadingException {

		if (dta == null) {
			throw new OutFileReadingException("The input DtaFile is Null");
		}

		String dtaname = dta.getFileName();
		File out = new File(this.dir, dtaname
		        .substring(0, dtaname.length() - 3)
		        + "out");
		if (!out.exists()) {
			throw new OutFileReadingException(
			        "The out file related to this dta file doesn't exist");
		}

		OutFileReader reader = null;
		try {
			reader = new OutFileReader(out, this.getSearchParameters().getFastaAccesser(getDecoyJudger()));
		} catch (FileNotFoundException e) {
			throw new OutFileReadingException("The out file is unreachable: "
			        + out.getName(), e);
		}
		return reader.getOutFile();
	}

	@Override
	public SequestScanDta getDtaFileForOut(OutFile out,
	        boolean isIncludePeakList) throws DtaFileParsingException {
		if (out == null) {
			throw new DtaFileParsingException("The input OutFile is Null");
		}

		String dtaname = out.getFilename();
		File dta = new File(this.dir, dtaname
		        .substring(0, dtaname.length() - 3)
		        + "dta");
		if (!dta.exists()) {
			throw new DtaFileParsingException(
			        "The dta file related to this out file doesn't exist");
		}

		SequestDtaReader dtareader = null;
		try {
			dtareader = new SequestDtaReader(dta);
		} catch (FileNotFoundException e) {
			throw new DtaFileParsingException("The dta file is unreachable: "
			        + dta.getName(), e);
		}

		return dtareader.getDtaFile(isIncludePeakList);
	}

	@Override
	public SequestParameter getSearchParameters() {
		return this.param;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.sequest.out.IBatchOutReader#getFile
	 * ()
	 */
	@Override
	public File getFile() {
		return this.dir;
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
}
