/*
 * *****************************************************************************
 * File: ZippedDtaOutReader.java * * * Created on 09-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.zipdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequestFileIllegalException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.ISequestReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.AbstractBatchOutReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFile;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFileReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFileReadingException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * Too many small files for sequest dta and out output will slow down the
 * reading and writing. Zip the sequest search directory may be a proper way.
 * 
 * <p>
 * This class map the zipped sequest search directory as a ISequestReader which
 * can be used for dta and out reading.
 * 
 * <p>
 * For zip and unzip the sequest search directory, see ZippedDtaOut.
 * 
 * @author Xinning
 * @version 0.2.1, 09-14-2010, 17:43:21
 */
public class ZippedDtaOutReader extends AbstractBatchOutReader implements ISequestReader {
	/**
	 * The zipped search search directory
	 */
	protected File zipdir;

	protected boolean isValid;

	private SequestParameter param;

	/**
	 * The zip file of zippedDtaOut
	 */
	protected ZipFile zipfile;
	/**
	 * List of dta files in this directory
	 */
	protected ZipEntry[] dtafiles;
	/**
	 * List of out files in this directory
	 */
	protected ZipEntry[] outfiles;

	/**
	 * The map of the name and dta entry
	 */
	protected HashMap<String, ZipEntry> dtaMap;

	/**
	 * The map of the name and out entry
	 */
	protected HashMap<String, ZipEntry> outMap;

	private int totalFileCount;

	private int curtOutFileIndex;// current file number
	// CurtFile which have been just readin and returned and outfile instance
	private ZipEntry curtOutFile;

	private int curtDtaFileIndex;// current file number
	// CurtFile which have been just readin and returned and outfile instance
	private ZipEntry curtDtaFile;
	
	private IFastaAccesser accesser;

	/**
	 * Create a reader for the sequest zipped dta out directory.
	 * 
	 * @param zippeddtaout
	 * @throws SequestFileIllegalException
	 * @throws ZipException
	 *             if some exception occurs while reading the zip file
	 */
	public ZippedDtaOutReader(String zippeddtaout)
	        throws SequestFileIllegalException, ZipException {
		this(zippeddtaout == null ? null : new File(zippeddtaout));
	}
	
	public ZippedDtaOutReader(String zippeddtaout, IFastaAccesser accesser)
		throws SequestFileIllegalException, ZipException {
		
		this(zippeddtaout == null ? null : new File(zippeddtaout), accesser);
	}

	/**
	 * Create a reader for the sequest zipped dta out directory.
	 * 
	 * @param zippeddtaout
	 * @throws SequestFileIllegalException
	 * @throws ZipException
	 *             if some exception occurs while reading the zip file
	 */
	public ZippedDtaOutReader(File zippeddtaout)
	        throws SequestFileIllegalException, ZipException {
		this.dtaMap = new HashMap<String, ZipEntry>();
		this.outMap = new HashMap<String, ZipEntry>();
		this.validate(zippeddtaout);
	}
	
	public ZippedDtaOutReader(File zippeddtaout, IFastaAccesser accesser)
		throws SequestFileIllegalException, ZipException {
		this.dtaMap = new HashMap<String, ZipEntry>();
		this.outMap = new HashMap<String, ZipEntry>();
		this.accesser = accesser;
		this.validate(zippeddtaout);
	}

	/**
	 * Valid the directory
	 * 
	 * @return always be TRUE. If not valid, exceptions will be threw.
	 * @throws SequestFileIllegalException
	 *             is the directory is not a valid sequest search directory: 1.
	 *             the number of dta and out file is not the same. 2. contains
	 *             no sequest search parameter or with more one params
	 * @throws ZipException
	 *             if some exception occurs while reading the zip file
	 * @throws NullPointerException
	 *             is the directory is null or not exist.
	 * @throws IllegalArgumentException
	 *             is the input file is not a directory file.
	 */
	protected boolean validate(File file) throws SequestFileIllegalException,
	        ZipException {
		boolean valid = false;
		ZipEntry paramfile = null;

		if (file == null || !file.exists())
			throw new NullPointerException("The zip file of sequest directory "
			        + "is Null or does not exist.");

		if (file.isDirectory())
			throw new IllegalArgumentException(
			        "The valid input should be a zip file of "
			                + "sequest search directory.");

		try {
			zipfile = new ZipFile(file);
		} catch (IOException e1) {
			throw new ZipException(
			        "Error in openning the zip file, may be not a sequest zip dta out file?");
		}

		Enumeration<? extends ZipEntry> emu = zipfile.entries();
		LinkedList<ZipEntry> dtas = new LinkedList<ZipEntry>();
		LinkedList<ZipEntry> outs = new LinkedList<ZipEntry>();
		LinkedList<ZipEntry> paramlist = new LinkedList<ZipEntry>();

		while (emu.hasMoreElements()) {
			ZipEntry entry = emu.nextElement();
			String name = entry.getName().toLowerCase();
			
			if (entry.isDirectory())
				continue;

			name = this.parseZipEntryName(name);
			
			if (name.endsWith(".dta")) {
				dtas.add(entry);
				this.dtaMap.put(name, entry);
			} else if (name.endsWith(".out")) {
				outs.add(entry);
				this.outMap.put(name, entry);
			} else if (name.endsWith(".params")) {
				paramlist.add(entry);
			} else {
				/*
				System.out.println("File: \"" + entry.getName()
				        + "\" is not a sequest search file, will"
				        + " be ignorated.");
				        */
				continue;
			}
		}

		int dtaNum = dtas.size();
		int outNum = outs.size();

		if (dtaNum == outNum) {
			this.dtafiles = dtas.toArray(new ZipEntry[dtaNum]);
			this.outfiles = outs.toArray(new ZipEntry[outNum]);

			ZipEntry[] params = paramlist
			        .toArray(new ZipEntry[paramlist.size()]);

			int len = params.length;
			if (len != 0) {
				if (len == 1) {
					valid = true;
					paramfile = params[0];
				} else {
					for (int i = 0; i < len; i++) {
						ZipEntry f = params[i];
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
						        .println("More than one .params files are contained in current"
						                + " directory, the default one \"sequest.params\" is selected");
					} else {
						throw new SequestFileIllegalException(
						        "There are more than one params files"
						                + " in current directory!");
					}
				}
			}
		}

		if (!valid) {
			throw new SequestFileIllegalException(
			        "There should be identifical number of"
			                + "dta and out files. Dta: " + this.dtafiles.length
			                + "; Out: " + this.outfiles.length);
		}

		this.zipdir = file;
		this.totalFileCount = this.dtafiles.length;

		try {
			this.param = new SequestParameter()
			        .readFromStream(new BufferedReader(new InputStreamReader(
			                zipfile.getInputStream(paramfile))));
		} catch (ParameterParseException e) {
			throw new SequestFileIllegalException(
			        "Exceptions occur while parsing the params file", e);
		} catch (IOException e) {
			throw new SequestFileIllegalException(
			        "Exceptions occur while generating the params stream from zip entry",
			        e);
		}

		return valid;
	}
	
	/**
	 * This method is used to remove the path name in the file name.
	 * 
	 * @param name
	 * @return
	 */
	private String parseZipEntryName(String name) {
		
		int idx = name.lastIndexOf('/');
		
		if(idx == -1) {
			idx = name.lastIndexOf('\\');
			if(idx == -1)
				return name;
		}
		
		return name.substring(idx + 1);
	}

	public boolean isValid() {
		return this.isValid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.dta.IBatchDtaReader#getNameofCurtDtaFile()
	 */
	@Override
	public String getNameofCurtDta() {
		if (this.curtDtaFile == null)
			return null;
		return this.parseZipEntryName(this.curtDtaFile.getName());
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
			dtareader = new SequestDtaReader(zipfile.getInputStream(curtDtaFile),
			        true, null, this.parseZipEntryName(curtDtaFile.getName()));
		} catch (Exception e) {
			throw new DtaFileParsingException(
			        "Error in reading the current dta entry: "
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
			reader = new OutFileReader(zipfile.getInputStream(curtOutFile),
			       this.getSearchParameters().getFastaAccesser(getDecoyJudger()), true);
		} catch (Exception e) {
			throw new OutFileReadingException(
			        "Error in reading the out entry: " + curtOutFile.getName(),
			        e);
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

		return this.parseZipEntryName(this.curtOutFile.getName());
	}

	@Override
	public void close() {
		try {
			this.zipfile.close();
		} catch (IOException e) {
			System.out
			        .println("Error while closing the zip file, but it doesn't matter :)");
		}
	}

	@Override
	public OutFile getOutFileForDta(SequestScanDta dta) throws OutFileReadingException {

		if (dta == null) {
			throw new OutFileReadingException("The input DtaFile is Null");
		}

		String dtaname = this.parseZipEntryName(dta.getFileName()).toLowerCase();
		ZipEntry out = this.outMap.get(dtaname.substring(0,
		        dtaname.length() - 3)
		        + "out");
		if (out == null) {
			throw new OutFileReadingException(
			        "The out file related to this dta file "+dtaname+" doesn't exist");
		}

		OutFileReader reader = null;
		try {
			reader = new OutFileReader(zipfile.getInputStream(out), this.getDecoyJudger(), true);
		} catch (Exception e) {
			throw new OutFileReadingException(
			        "Error in reading the out entry: " + out.getName(), e);
		}
		return reader.getOutFile();
	}

	@Override
	public SequestScanDta getDtaFileForOut(OutFile out, boolean isIncludePeakList)
	        throws DtaFileParsingException {
		if (out == null) {
			throw new DtaFileParsingException("The input OutFile is Null");
		}

		String outname = this.parseZipEntryName(out.getFilename()).toLowerCase();
		String dtaname = outname.substring(0,outname.length() - 3) + "dta";
		ZipEntry dta = this.dtaMap.get(dtaname);
		if (dta == null) {
			throw new DtaFileParsingException(
			        "The dta file related to this out file "+outname+" doesn't exist");
		}

		SequestDtaReader dtareader = null;
		try {
			dtareader = new SequestDtaReader(zipfile.getInputStream(dta), true, null,
			        dta.getName());
		} catch (Exception e) {
			throw new DtaFileParsingException(
			        "Error in reading the current dta entry: " + dta.getName(),
			        e);
		}
		return dtareader.getDtaFile(isIncludePeakList);
	}

	@Override
	public SequestParameter getSearchParameters() {
		return this.param;
	}

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.sequest.out.IBatchOutReader#getFile()
	 */
	@Override
    public File getFile() {
	    return this.zipdir;
    }

	@Override
    public DtaType getDtaType() {
	    return DtaType.DTA;
    }
}
