/* 
 ******************************************************************************
 * File: BatchOutReader.java * * * Created on 05-01-2008
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
package cn.ac.dicp.gp1809.proteome.IO.sequest.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequestFileIllegalException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;

/**
 * Batch out reader for the sequest output out directory which contains a bulk
 * of out files.
 * 
 * @author Xinning
 * @version 0.1.3, 09-14-2010, 18:55:46
 */
public class BatchOutReader extends AbstractBatchOutReader implements IBatchOutReader {

	private File folder = null;
	private String[] filenames = null;
	private int totalFileCount;
	private int curtFileIndex;// current file number
	// CurtFile which have been just readin and returned and outfile instance
	private File curtFile;
	private SequestParameter parameter;

	/**
	 * @param foldername
	 *            the directory containing all the out files
	 *            
	 * @throws SequestFileIllegalException 
	 */
	public BatchOutReader(String foldername) throws SequestFileIllegalException {
		this(new File(foldername));
	}

	/**
	 * @param folder
	 *            the directory containing all the out files
	 *            
	 * @throws SequestFileIllegalException 
	 */
	public BatchOutReader(File folder) throws SequestFileIllegalException {
		this.folder = folder;
		this.listFiles();
	}

	/**
	 * @param folder
	 *            the out directory
	 * @param filenames
	 *            (not the path) and the out files containing in this directory
	 * @param parameter
	 *            parameter file
	 * @throws SequestFileIllegalException
	 */
	public BatchOutReader(File folder, String[] filenames, File parameter)
	        throws SequestFileIllegalException {
		this.folder = folder;
		this.filenames = filenames;
		if (filenames != null)
			this.totalFileCount = filenames.length;

		try {
			this.parameter = new SequestParameter().readFromFile(parameter);
		} catch (ParameterParseException e) {
			throw new SequestFileIllegalException(
			        "Exceptions occur while parsing the params file", e);
		}
	}

	/*
	 * List all the out files and validate
	 */
	private void listFiles() throws SequestFileIllegalException {
		this.filenames = this.folder.list(new OutFilenameFilter());
		this.totalFileCount = filenames.length;
		
		
		boolean valid = false;
		File paramfile = null;
		File[] params = folder.listFiles(new FilenameFilter() {
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
					//Select sequest.params for parameter file
					if (f.getName().equals("sequest.params")) {
						paramfile = f;
						valid = true;
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
			
			
			if(valid){
				try {
	                this.parameter = new SequestParameter().readFromFile(paramfile);
                } catch (ParameterParseException e) {
        			throw new SequestFileIllegalException(
        			        "Exceptions occur while parsing the params file", e);
                }
			}
		}
		else{
			throw new SequestFileIllegalException(
			        "The selected directory contains no params file for database search.");
		}
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.out.IBatchOutReader#getNext()
	 */
	public OutFile getNextOut() throws OutFileReadingException {
		if (this.curtFileIndex >= this.totalFileCount) {
			this.curtFile = null;
			return null;
		}

		curtFile = new File(this.folder, this.filenames[this.curtFileIndex++]);
		OutFileReader reader = null;
		try {
			reader = new OutFileReader(curtFile, this.getDecoyJudger());
		} catch (FileNotFoundException e) {
			throw new OutFileReadingException("The out file is unreachable: "
			        + curtFile.getName(), e);
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
		if (this.curtFile == null)
			return null;

		return this.curtFile.getName();
	}

	@Override
	public void close() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.sequest.out.IBatchOutReader#getFile()
	 */
	@Override
	public File getFile() {
		return this.folder;
	}

	@Override
	public SequestParameter getSearchParameters() {
		return this.parameter;
	}
}
