/*
 ******************************************************************************
 * File: InspectPeptideReaderFactory.java * * * Created on 03-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * Factory to create Inspect peptide reader.
 * 
 * @author Xinning
 * @version 0.2.1, 05-20-2010, 16:30:42
 */
public class InspectPeptideReaderFactory {

	/**
	 * Create reader for the file with name of "filename". If isCreateppl is set
	 * as true, the temporary ppl file will be automatically created (if it is
	 * not exist or the original file has been renewed) while the parsing of
	 * reader.
	 * 
	 * @param file
	 * @param paramfile
	 * @param sourceFile
	 * @param accesser
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IInspectPeptideReader createReader(File file, File paramfile,
	        File sourceFile, FastaAccesser accesser, IDecoyReferenceJudger judger)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {
		return createReader(file, paramfile, sourceFile, accesser, judger, false);
	}

	/**
	 * Create reader for the file with name of "filename". If isCreateppl is set
	 * as true, the temporary ppl file will be automatically created (if it is
	 * not exist or the original file has been renewed) while the parsing of
	 * reader.
	 * 
	 * @param file
	 * @param paramfile
	 * @param sourceFile
	 * @param db
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 */
	public static IInspectPeptideReader createReader(File file, File paramfile,
	        File sourceFile, File db, IDecoyReferenceJudger judger) throws ReaderGenerateException,
	        ImpactReaderTypeException, FastaDataBaseException, IOException {
		return createReader(file, paramfile, sourceFile, new FastaAccesser(
		        db.getAbsolutePath(), judger), judger, false);
	}

	
	/**
	 * Create reader for the file with name of "filename". If isCreateppl is set
	 * as true, the temporary ppl file will be automatically created (if it is
	 * not exist or the original file has been renewed) while the parsing of
	 * reader.
	 * 
	 * @param file
	 * @param paramfile
	 * @param sourceFile
	 * @param accesser
	 * @param isCreateppl
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IInspectPeptideReader createReader(File file, File paramfile,
	        File sourceFile, FastaAccesser accesser, IDecoyReferenceJudger judger, boolean isCreateppl)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {

		FileType type = FileType.typeofFile(file.getName());

		switch (type) {
		case PLAINFILE: {
			if (!isCreateppl) {
				try {
					InspectPlainPeptideReader reader = new InspectPlainPeptideReader(
					        file, paramfile, sourceFile, accesser);
					reader.setDecoyJudger(judger);
					
					return reader;
				} catch (Exception e) {
					throw new ImpactReaderTypeException(e);
				}

			} else {
				throw new IllegalArgumentException(
				        "Please use the BatchPplCreator for peptide list file creation.");
			}
		}

		case PPLFILE: {
			try {
				InspectPeptideListReader reader = new InspectPeptideListReader(
				        file);
				reader.getSearchParameter().setFastaAccesser(accesser);

				return reader;
			} catch (FileDamageException e) {
				throw new ReaderGenerateException(e);
			} catch (IOException e) {
				throw new ReaderGenerateException(e);
			}
		}

		case PPLSFILE: {
			try {
				InspectPeptideProbListReader reader = new InspectPeptideProbListReader(
				        file);
				reader.getSearchParameter().setFastaAccesser(accesser);
				return reader;
			} catch (FileDamageException e) {
				throw new ReaderGenerateException(e);
			} catch (IOException e) {
				throw new ReaderGenerateException(e);
			}
		}
		}

		throw new IllegalArgumentException(
		        "Unknown filetype to create the peptide reader for file: "
		                + file.getName());

	}

	/**
	 * Test the file type.
	 * 
	 * @author Xinning
	 * @version 0.1, 03-24-2009, 15:53:12
	 */
	public static enum FileType {
		PLAINFILE, PPLFILE, PPLSFILE;

		/**
		 * The file type is generated from the extension of the filename
		 * 
		 * @param filename
		 * @return
		 */
		public static FileType typeofFile(String filename) {
			String lowname = filename.toLowerCase();

			if (lowname.endsWith(".ppl"))
				return PPLFILE;

			if (lowname.endsWith(".ppls"))
				return PPLSFILE;

			//All others are consider as the plain file
			return PLAINFILE;
		}
	}
}
