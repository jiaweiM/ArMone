/*
 * *****************************************************************************
 * File: SequestPepReaderFactory.java * * * Created on 08-01-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestDtaOutReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.export.ExcelProReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.export.XMLProReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.zipdata.ZippedDtaOutReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.sim.SimCalculator;

/**
 * A Factory for creating proReader.
 * 
 * <p>
 * Currently supported file type:
 * <ul>
 * 1. SEQUEST ".out" directory.
 * </ul>
 * <ul>
 * 2. SEQUEST ".srf" unified file
 * </ul>
 * <ul>
 * 3. Excel and XML files outputted from SRF with protein view
 * </ul>
 * <ul>
 * 4.pepxml (peptideprophet xml file)
 * </ul>
 * <ul>
 * 5. ".ppl" ".ppls" peptide list file by ArMone
 * </ul>
 * 
 * @author Xinning
 * @version 0.7, 05-04-2009, 09:42:04
 */
public class SequestPeptideReaderFactory {

	/**
	 * XML formatted file containing peptide informations. These contains: <li>
	 * exported file by bioworks in xml format. <li>pepxml file by
	 * PeptideProphet
	 */
	public static final FileType XMLFILE = FileType.XMLFILE;

	/**
	 * Exported file by bioworks in xls format
	 */
	public static final FileType XLSFILE = FileType.XLSFILE;
	/**
	 * Raw sequest search directory containing .dta and .out informations
	 */
	public static final FileType OUTFILE = FileType.OUTFILE;
	/**
	 * peptide list file
	 */
	public static final FileType PPLFILE = FileType.PPLFILE;

	/**
	 * Zipped dta out directory
	 */
	public static final FileType ZIPDATA = FileType.ZIPDATA;

	/**
	 * A PPLS file is similar as PPL file, but with a peptide probabaility
	 * attribute.
	 */
	public static final FileType PPLSFILE = FileType.PPLSFILE;

	/**
	 * Sequest new unified output file from bioworks 3.2 with extension of .srf
	 */
	public static final FileType SRFFILE = FileType.SRFFILE;

	/**
	 * Create a sequest peptide reader with specific parameters. 
	 * <p>
	 * The sequest parameter is always needed if the file for reading is
	 * XMLFILE, XLSFILE. Therefore, this method can <b>ONLY</b> used to create
	 * readers for <b>SRF, OUT, PPL and PPLS </b>file
	 * 
	 * @param file
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(String filename)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {

		return createReader(new File(filename));
	}

	/**
	 * Create a sequest peptide reader with specific parameters. 
	 * <p>
	 * The sequest parameter is always needed if the file for reading is
	 * XMLFILE, XLSFILE. Therefore, this method can <b>ONLY</b> used to create
	 * readers for <b>SRF, OUT, PPL and PPLS </b>file
	 * 
	 * @param file
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(File file)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {
		return createReader(file, FileType.typeofFile(file.getPath()));
	}

	/**
	 * Create a sequest peptide reader with specific parameters. 
	 * <p>
	 * The sequest parameter is always needed if the file for reading is
	 * XMLFILE, XLSFILE. Therefore, this method can <b>ONLY</b> used to create
	 * readers for <b>SRF, OUT, PPL and PPLS </b>file
	 * 
	 * @param file
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(String filename,
	        SequestParameter parameter) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {

		return createReader(new File(filename), parameter);
	}

	/**
	 * Create a sequest peptide reader with specific parameters. 
	 * <p>
	 * The sequest parameter is always needed if the file for reading is
	 * XMLFILE, XLSFILE. Therefore, this method can <b>ONLY</b> used to create
	 * readers for <b>SRF, OUT, PPL and PPLS </b>file
	 * 
	 * @param file
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(File file,
	        SequestParameter parameter) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(file, FileType.typeofFile(file.getPath()), false,
		        parameter);
	}

	/**
	 * Create a sequest peptide reader with specific parameters. 
	 * <p>
	 * The sequest parameter is always needed if the file for reading is
	 * XMLFILE, XLSFILE. Therefore, this method can <b>ONLY</b> used to create
	 * readers for <b>SRF, OUT, PPL and PPLS </b>file
	 * 
	 * @param file
	 * @param accesser
	 *            fasta accesser
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(File file,
	        IFastaAccesser accesser) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(file, FileType.typeofFile(file.getPath()),
		        accesser, false);
	}

	/**
	 * Create a sequest peptide reader with specific parameters. 
	 * <p>
	 * The sequest parameter is always needed if the file for reading is
	 * XMLFILE, XLSFILE. Therefore, this method can <b>ONLY</b> used to create
	 * readers for <b>SRF, OUT, PPL and PPLS </b>file
	 * 
	 * @param file
	 * @param type
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(String filename,
	        FileType type) throws ReaderGenerateException,
	        ImpactReaderTypeException, FileNotFoundException {
		return createReader(new File(filename), type);
	}

	/**
	 * Create a sequest peptide reader with specific parameters. 
	 * <p>
	 * The sequest parameter is always needed if the file for reading is
	 * XMLFILE, XLSFILE. Therefore, this method can <b>ONLY</b> used to create
	 * readers for <b>SRF, OUT, PPL and PPLS </b>file
	 * 
	 * @param file
	 * @param type
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(File file, FileType type)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {
		return createReader(file, type, false, null);
	}

	/**
	 * Create a sequest peptide reader with specific parameters. The sequest
	 * parameter is always needed if the file for reading is XMLFILE, XLSFILE.
	 * Otherwise, the parameters can be generated from the same directory (for
	 * out dir), in the same file (for srf, ppl and ppls file), and the
	 * parameter can be null in these conditions.
	 * 
	 * @param file
	 * @param type
	 * @param parameter
	 *            must be specified if the file for reading is XLS or XML file
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(String filename,
	        FileType type, SequestParameter parameter)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {

		return createReader(new File(filename), type, false, parameter);
	}

	/**
	 * Create a sequest peptide reader with specific parameters. The ppl
	 * temporary file will be automatically created while reading.
	 * <p>
	 * The sequest parameter will automatically create using default values
	 * (trypsin) by setting the specified accesser. This method can create
	 * reader for exported files.
	 * 
	 * @param file
	 * @param accesser
	 *            fasta accesser
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(File file, FileType type,
	        IFastaAccesser accesser, boolean isCreateppl)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {

		switch (type) {
		case PPLFILE: {
			try {
				SequestPeptideListReader reader = new SequestPeptideListReader(
				        file);

				reader.getSearchParameter().setFastaAccesser(accesser);

				return reader;
			} catch (FileDamageException e) {
				throw new ReaderGenerateException(
				        "PPL file damage Exception for: " + file.getName(), e);
			} catch (IOException e) {
				throw new ReaderGenerateException(
				        "Error in generating PPLFileReader for: "
				                + file.getName(), e);
			}
		}

		case PPLSFILE: {
			try {
				SequestPeptideProbListReader reader = new SequestPeptideProbListReader(
				        file);

				reader.getSearchParameter().setFastaAccesser(accesser);

				return reader;
			} catch (FileDamageException e) {
				throw new ReaderGenerateException(
				        "PPLS file damage Exception for: " + file.getName(), e);
			} catch (IOException e) {
				throw new ReaderGenerateException(
				        "Error in generating PPLSFileReader for: "
				                + file.getName(), e);
			}
		}

		case OUTFILE: {
			if (!isCreateppl) {
				try {

					OutFilePeptideReader reader = new OutFilePeptideReader(
					        new SequestDtaOutReader(file));

					reader.getSearchParameter().setFastaAccesser(accesser);

					return reader;
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create OutFileReader for the" + "file: "
					                + file.getName() + ". May be damaged?", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}
/*
		case SRFFILE: {
			if (!isCreateppl) {
				try {

					OutFilePeptideReader reader = new OutFilePeptideReader(
					        new SRFReader(file));

					reader.getSearchParameter().setFastaAccesser(accesser);

					return reader;
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create SRFReader for the" + "file: "
					                + file.getName() + ". May be damaged?", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}
*/
		case ZIPDATA: {
			if (!isCreateppl) {
				try {
					OutFilePeptideReader reader = new OutFilePeptideReader(
					        new ZippedDtaOutReader(file));
					reader.getSearchParameter().setFastaAccesser(accesser);
					return reader;
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create ZippedDataReader for the" + "file: "
					                + file.getName() + ". May be damaged?", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}

		case XLSFILE: {
			if (!isCreateppl) {

				if (accesser == null)
					throw new NullPointerException(
					        "SequestParameter must be specified for XLS file reader.");

				SequestParameter param = new SequestParameter();
				param.setFastaAccesser(accesser);
				param.setDefaultPara();
				try {
					return new ExcelProReader(file, param);
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create SRFReader for the" + "file: "
					                + file.getName() + ". May be damaged?", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}
		case XMLFILE: {
			if (!isCreateppl) {

				if (accesser == null)
					throw new NullPointerException(
					        "SequestParameter must be specified for XML file reader.");

				SequestParameter param = new SequestParameter();
				param.setFastaAccesser(accesser);
				param.setDefaultPara();
				try {
					return new XMLProReader(file, param);
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create SRFReader for the" + "file: "
					                + file.getName() + ". May be damaged?", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}

		default:
			throw new ReaderGenerateException(
			        "Currently unsupportted file type for file: "
			                + file.getName());

		}
	}

	/**
	 * Create a sequest peptide reader with specific parameters. The sequest
	 * parameter is always needed if the file for reading is XMLFILE, XLSFILE.
	 * Otherwise, the parameters can be generated from the same directory (for
	 * out dir), in the same file (for srf, ppl and ppls file), and the
	 * parameter can be null in these conditions.
	 * 
	 * <p>
	 * <b>Note: the parameter is useless if the input file is srf or out search
	 * directory</b>
	 * 
	 * @param file
	 * @param type
	 * @param isCreateppl
	 *            if use the temporary file ".ppl"
	 * @param parameter
	 *            must be specified if the file for reading is XLS or XML file
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static ISequestPeptideReader createReader(File file, FileType type,
	        boolean isCreateppl, SequestParameter parameter)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {

		switch (type) {
		case PPLFILE: {
			try {
				SequestPeptideListReader reader = new SequestPeptideListReader(
				        file);

				return reader;
			} catch (FileDamageException e) {
				throw new ReaderGenerateException(
				        "PPL file damage Exception for: " + file.getName(), e);
			} catch (IOException e) {
				throw new ReaderGenerateException(
				        "Error in generating PPLFileReader for: "
				                + file.getName(), e);
			}
		}

		case PPLSFILE: {
			try {
				return new SequestPeptideProbListReader(file);
			} catch (FileDamageException e) {
				throw new ReaderGenerateException(
				        "PPLS file damage Exception for: " + file.getName(), e);
			} catch (IOException e) {
				throw new ReaderGenerateException(
				        "Error in generating PPLSFileReader for: "
				                + file.getName(), e);
			}
		}

		case OUTFILE: {
			if (!isCreateppl) {
				try {
					return new OutFilePeptideReader(new SequestDtaOutReader(
					        file));
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create OutFileReader for the" + "file: "
					                + file.getName() + ". \n Caused by: \""
					                + e.getMessage() + "\".", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}
/*
		case SRFFILE: {
			if (!isCreateppl) {
				try {
					return new OutFilePeptideReader(new SRFReader(file));
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create SRFReader for the" + "file: "
					                + file.getName() + ". \n Caused by: \""
					                + e.getMessage() + "\".", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}
*/
		case ZIPDATA: {
			if (!isCreateppl) {
				try {
					return new OutFilePeptideReader(
					        new ZippedDtaOutReader(file));
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create ZippedDataReader for the" + "file: "
					                + file.getName() + ". \n Caused by: \""
					                + e.getMessage() + "\".", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}

		case XLSFILE: {
			if (!isCreateppl) {

				if (parameter == null)
					throw new NullPointerException(
					        "SequestParameter must be specified for XLS file reader.");

				try {
					return new ExcelProReader(file, parameter);
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create SRFReader for the" + "file: "
					                + file.getName() + ". May be damaged?", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}
		case XMLFILE: {
			if (!isCreateppl) {

				if (parameter == null)
					throw new NullPointerException(
					        "SequestParameter must be specified for XML file reader.");

				try {
					return new XMLProReader(file, parameter);
				} catch (Exception e) {
					throw new ImpactReaderTypeException(
					        "Can't create SRFReader for the" + "file: "
					                + file.getName() + ". \n Caused by: \""
					                + e.getMessage() + "\".", e);
				}
			} else {
				throw new IllegalArgumentException("Please use the BatchPplCreator for peptide list file creation.");
			}
		}

		default:
			throw new ReaderGenerateException(
			        "Currently unsupportted file type for file: "
			                + file.getName());

		}
	}

	/**
	 * Create a peptide reader which can generate the Sim socres into the
	 * peptide list file. The sim score is described as Zhang Z Q and see also
	 * SimCalculator.class
	 * 
	 * @param file
	 * @param filetype
	 * @param isCreatePPl
	 */
	public ISequestPeptideReader createSimPeptideReader(File file,
	        int filetype, SimCalculator calor) {

		throw new NullPointerException("This method has not fully completed!");
	}

	/**
	 * Test the file type.
	 * 
	 * @author Xinning
	 * @version 0.1, 09-07-2008, 09:43:01
	 */
	public static enum FileType {
		/** Raw sequest search directory containing .dta and .out informations */
		OUTFILE,
		/**
		 * Sequest new unified output file from bioworks with extension of .srf
		 */
		SRFFILE,
		/** Zipped dta out directory */
		ZIPDATA,
		/**
		 * XML formatted file containing peptide informations. These contains:
		 * <li>exported file by bioworks in xml format. <li>pepxml file by
		 * PeptideProphet
		 */
		XMLFILE,
		/** Exported file by bioworks in xls format */
		XLSFILE,
		/** A peptide list file */
		PPLFILE,
		/**
		 * A PPLS file is similar as PPL file, but with a peptide probabaility
		 * attribute.
		 */
		PPLSFILE;

		/**
		 * The file type is generated from the extension of the filename
		 * 
		 * @param filename
		 * @return
		 */
		public static FileType typeofFile(String filename) {

			if (new File(filename).isDirectory()) {
				return OUTFILE;
			}

			String lowname = filename.toLowerCase();
			if (lowname.endsWith(".srf"))
				return SRFFILE;

			if (lowname.endsWith(".zip"))
				return ZIPDATA;

			if (lowname.endsWith(".xml"))
				return XMLFILE;

			if (lowname.endsWith(".xls"))
				return XLSFILE;

			if (lowname.endsWith(".ppl"))
				return PPLFILE;

			if (lowname.endsWith(".ppls"))
				return PPLSFILE;

			throw new IllegalArgumentException("Unkown filetype for file/dir: "
			        + filename);

		}
	}
}
