/* 
 ******************************************************************************
 * File: OMSSAPeptideReaderFactory.java * * * Created on 09-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.readers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAParameter;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;

/**
 * Factory to create OMSSA peptide reader.
 * 
 * @author Xinning
 * @version 0.2, 05-04-2009, 10:38:10
 */
public class OMSSAPeptideReaderFactory {

	/**
	 * The exported csv file
	 */
	public static final FileType CSVFILE = FileType.CSVFILE;

	/**
	 * The omx file
	 */
	public static final FileType OMXFILE = FileType.OMXFILE;

	/**
	 * Formated peptide list file
	 */
	public static final FileType PPLFILE = FileType.PPLFILE;

	/**
	 * A PPLS file is similar as PPL file, but with a peptide probability
	 * attribute.
	 */
	public static final FileType PPLSFILE = FileType.PPLSFILE;

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * <b>For CSV file, please set the search parameter by </b>
	 * {@link #createReader(String, FileType, OMSSAParameter)} or
	 * {@link #createReader(String, FileType, boolean, OMSSAParameter)}.
	 * <b>Otherwise, NullPointerException will be threw.</b>
	 * 
	 * @param filename
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(String filename)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {
		return createReader(new File(filename));
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * <b>For CSV file, please set the search parameter by </b>
	 * {@link #createReader(String, FileType, OMSSAParameter)} or
	 * {@link #createReader(String, FileType, boolean, OMSSAParameter)}.
	 * <b>Otherwise, NullPointerException will be threw.</b>
	 * 
	 * @param file
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(File file)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {
		return createReader(file, FileType.typeofFile(file.getName()));
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * For CSV file or other files which contains no search parameters, the
	 * parameter must be set and should not be null.
	 * </p>
	 * <p>
	 * <b>For OMX file or other files which contains search parameters in the
	 * output file, please set the search parameter as null or use the methods
	 * </b> {@link #createReader(String)} or
	 * {@link #createReader(String, FileType)}. <b>Please note: even though the
	 * parameters were set for these files, the parameters in the output files
	 * but not the set parameters will be used for the creation of readers.</b>
	 * 
	 * @param file
	 * @param parameter
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(String filename,
	        OMSSAParameter parameter) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(new File(filename), parameter);
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * For CSV file or other files which contains no search parameters, the
	 * parameter must be set and should not be null.
	 * </p>
	 * <p>
	 * <b>For OMX file or other files which contains search parameters in the
	 * output file, please set the search parameter as null or use the methods
	 * </b> {@link #createReader(String)} or
	 * {@link #createReader(String, FileType)}. <b>Please note: even though the
	 * parameters were set for these files, the parameters in the output files
	 * but not the set parameters will be used for the creation of readers.</b>
	 * 
	 * @param file
	 * @param parameter
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(File file,
	        OMSSAParameter parameter) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(file, FileType.typeofFile(file.getName()),
		        parameter);
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * This method may be useless for CSV reader whose database can be specified
	 * when the given of parameter file.
	 * </p>
	 * <p>
	 * <b>For OMX file or other files which contains search parameters in the
	 * output file, you need to copy the mods.xml file to the "mossa\\"
	 * directory in the same dir as the jar file. </b>
	 * 
	 * @param file
	 * @param accesser
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(String filename,
	        IFastaAccesser accesser) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(new File(filename), FileType.typeofFile(filename),
		        false, accesser);
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * This method may be useless for CSV reader whose database can be specified
	 * when the given of parameter file.
	 * </p>
	 * <p>
	 * <b>For OMX file or other files which contains search parameters in the
	 * output file, you need to copy the mods.xml file to the "mossa\\"
	 * directory in the same dir as the jar file. </b>
	 * 
	 * @param file
	 * @param accesser
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(File file,
	        IFastaAccesser accesser) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(file, FileType.typeofFile(file.getName()), true,
		        accesser);
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * <b>For CSV file, please set the search parameter by </b>
	 * {@link #createReader(String, FileType, OMSSAParameter)} or
	 * {@link #createReader(String, FileType, boolean, OMSSAParameter)}.
	 * <b>Otherwise, NullPointerException will be threw.</b>
	 * 
	 * @param filename
	 * @param type
	 *            FileType
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(String filename,
	        FileType type) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(new File(filename), type);
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * <b>For CSV file, please set the search parameter by </b>
	 * {@link #createReader(String, FileType, OMSSAParameter)} or
	 * {@link #createReader(String, FileType, boolean, OMSSAParameter)}.
	 * <b>Otherwise, NullPointerException will be threw.</b>
	 * 
	 * @param file
	 * @param type
	 *            FileType
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(File file, FileType type)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {
		return createReader(file, type, null);
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * For CSV file or other files which contains no search parameters, the
	 * parameter must be set and should not be null.
	 * </p>
	 * <p>
	 * <b>For OMX file or other files which contains search parameters in the
	 * output file, please set the search parameter as null or use the methods
	 * </b> {@link #createReader(String)} or
	 * {@link #createReader(String, FileType)}. <b>Please note: even though the
	 * parameters were set for these files, the parameters in the output files
	 * but not the set parameters will be used for the creation of readers.</b>
	 * 
	 * @param file
	 * @param type
	 *            FileType
	 * @param parameter
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(String filename,
	        FileType type, OMSSAParameter parameter)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {
		return createReader(new File(filename), type, parameter);
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * For CSV file or other files which contains no search parameters, the
	 * parameter must be set and should not be null.
	 * </p>
	 * <p>
	 * <b>For OMX file or other files which contains search parameters in the
	 * output file, please set the search parameter as null or use the methods
	 * </b> {@link #createReader(String)} or
	 * {@link #createReader(String, FileType)}. <b>Please note: even though the
	 * parameters were set for these files, the parameters in the output files
	 * but not the set parameters will be used for the creation of readers.</b>
	 * 
	 * @param file
	 * @param type
	 *            FileType
	 * @param parameter
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IOMSSAPeptideReader createReader(File file, FileType type,
	        OMSSAParameter parameter) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(file, type, false, parameter);
	}

	/**
	 * Create reader for the file with name of "filename".
	 * 
	 * <p>
	 * For CSV file or other files which contains no search parameters, the
	 * parameter must be set and should not be null.
	 * </p>
	 * <p>
	 * For OMX file or other files which contains search parameters in the
	 * output file, please set the search parameter as null. <b>Please note:
	 * even though the parameters were set for these files, the parameters in
	 * the output files but not the set parameters will be used for the creation
	 * of readers.</b>
	 * 
	 * @param file
	 * @param type
	 *            FileType
	 * @param isCreateppl
	 *            is create the temporary ppl file
	 * @param parameter
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	private static IOMSSAPeptideReader createReader(File file, FileType type,
	        boolean isCreateppl, OMSSAParameter parameter)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {

		switch (type) {
		case CSVFILE: {
			if (!isCreateppl) {

				if (parameter == null)
					throw new IllegalArgumentException(
					        "OMSSAParameter must be given prior the creation of CVSPeptideReader");
				else {
					try {
						return new CSVPeptideReader(file.getPath(), parameter);
					} catch (Exception e) {
						throw new ImpactReaderTypeException(e);
					}
				}
			} else {
				throw new IllegalArgumentException(
				        "Please use the BatchPplCreator for peptide list file creation.");
			}
		}

		case OMXFILE: {
			if (!isCreateppl) {
				try {
					return new OMXPeptideReader(file, parameter.getFastaAccesser(new DefaultDecoyRefJudger()));
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
				return new OMSSAPeptideListReader(file);
			} catch (FileDamageException e) {
				throw new ReaderGenerateException(e);
			} catch (IOException e) {
				throw new ReaderGenerateException(e);
			}
		}

		case PPLSFILE: {
			try {
				return new OMSSAPeptideProbListReader(file);
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
	 * Create reader for the file with name of "filename". The temporary ppl
	 * file will be created if the "isCreateppl" is true (if it is not exist or
	 * the original file has been renewed) while the parsing of reader.
	 * 
	 * <p>
	 * For CSV file or other files which contains no search parameters, the
	 * parameter must be set and should not be null.
	 * </p>
	 * <p>
	 * For OMX file or other files which contains search parameters in the
	 * output file, please set the search parameter as null. <b>Please note:
	 * even though the parameters were set for these files, the parameters in
	 * the output files but not the set parameters will be used for the creation
	 * of readers.</b>
	 * 
	 * @param file
	 * @param type
	 *            FileType
	 * @param isCreateppl
	 *            is create the temporary ppl file
	 * @param parameter
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	private static IOMSSAPeptideReader createReader(File file, FileType type,
	        boolean isCreateppl, IFastaAccesser accesser)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {

		switch (type) {
		case CSVFILE: {
			throw new ReaderGenerateException("The full parameter must be "
			        + "specified for CSV reader.");
		}

		case OMXFILE: {
			if (!isCreateppl) {
				try {
					OMXPeptideReader reader = new OMXPeptideReader(file, accesser);

					reader.getSearchParameter().setFastaAccesser(accesser);

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
				OMSSAPeptideListReader reader = new OMSSAPeptideListReader(file);

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
				OMSSAPeptideProbListReader reader = new OMSSAPeptideProbListReader(
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
	 * @version 0.1, 09-07-2008, 09:43:01
	 */
	public static enum FileType {
		CSVFILE, OMXFILE, PPLFILE, PPLSFILE;

		/**
		 * The file type is generated from the extension of the filename
		 * 
		 * @param filename
		 * @return
		 */
		public static FileType typeofFile(String filename) {
			String lowname = filename.toLowerCase();
			if (lowname.endsWith(".csv"))
				return CSVFILE;

			if (lowname.endsWith(".omx"))
				return OMXFILE;

			if (lowname.endsWith(".ppl"))
				return PPLFILE;

			if (lowname.endsWith(".ppls"))
				return PPLSFILE;

			throw new IllegalArgumentException("Unkown filetype for file: "
			        + filename);

		}
	}
}
