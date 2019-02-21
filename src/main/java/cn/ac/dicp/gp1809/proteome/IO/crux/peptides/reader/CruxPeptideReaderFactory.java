/*
 ******************************************************************************
 * File: CruxPeptideReaderFactory.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * Factory to create Crux peptide reader.
 * 
 * @author Xinning
 * @version 0.3, 05-20-2010, 19:44:04
 */
public class CruxPeptideReaderFactory {

	/**
	 * Create reader for the file with name of "filename". 
	 * 
	 * @param file
	 * @param accesser
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ICruxPeptideReader createReader(File file,
	        FastaAccesser accesser, IDecoyReferenceJudger judger) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(file, accesser, false, judger);
	}

	/**
	 * Create reader for the file with name of "filename". 
	 * 
	 * @param file
	 * @param db
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 */
	public static ICruxPeptideReader createReader(File file, File db, IDecoyReferenceJudger judger)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FastaDataBaseException, IOException {
		return createReader(file, new FastaAccesser(db.getAbsolutePath(), judger), false, judger);
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
	private static ICruxPeptideReader createReader(File file,
	        IFastaAccesser accesser, boolean isCreateppl, IDecoyReferenceJudger judger)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {

		FileType type = FileType.typeofFile(file.getName());

		switch (type) {
		case SQTFILE: {
			if (!isCreateppl) {
				try {
					CruxSQTPeptideReader reader = new CruxSQTPeptideReader(
					        file, accesser);
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
				CruxPeptideListReader reader = new CruxPeptideListReader(file);
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
				CruxPeptideProbListReader reader = new CruxPeptideProbListReader(
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
		SQTFILE, PPLFILE, PPLSFILE;

		/**
		 * The file type is generated from the extension of the filename
		 * 
		 * @param filename
		 * @return
		 */
		public static FileType typeofFile(String filename) {
			String lowname = filename.toLowerCase();

			if (lowname.endsWith(".sqt"))
				return SQTFILE;

			if (lowname.endsWith(".ppl"))
				return PPLFILE;

			if (lowname.endsWith(".ppls"))
				return PPLSFILE;

			//All others are consider as the plain file
			throw new IllegalArgumentException("Unknown type for file \""
			        + filename + "\" by the extension.");
		}
	}
}
