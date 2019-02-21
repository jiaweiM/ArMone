/* 
 ******************************************************************************
 * File: PeptideReaderFactory.java * * * Created on 08-31-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.reader.InspectPeptideReaderFactory;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers.MascotPeptideReaderFactory;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.readers.OMSSAPeptideReaderFactory;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.readers.XTandemPeptideReaderFactory;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.reader.CruxPeptideReaderFactory;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.SequestPeptideReaderFactory;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * Reader factory used for the generating of peptide reader
 * 
 * @author Xinning
 * @version 0.3.1, 05-20-2010, 21:08:58
 */
public class PeptideReaderFactory {

	/**
	 * Create the peptide reader for the specific input file. You can then use
	 * the global method setTopN(int) to specify the number of reported top
	 * matched peptides (if any). Default: top 1.
	 * 
	 * <p>
	 * <b>Only for mascot</b>
	 * 
	 * <p>
	 * peptide list file will not be automatically created.
	 * 
	 * @param output
	 * @param input
	 * @param database
	 * @param type
	 * @throws ImpactReaderTypeException
	 * @throws ReaderGenerateException
	 * @throws IOException
	 * @throws FastaDataBaseException
	 */
	public static IPeptideReader createRawReader(String input, String database,
	        PeptideType type, IDecoyReferenceJudger judger) throws ReaderGenerateException,
	        ImpactReaderTypeException, FastaDataBaseException, IOException {
		FastaAccesser accesser = new FastaAccesser(database, judger);
		switch (type) {

		case SEQUEST: {
			IPeptideReader reader = SequestPeptideReaderFactory.createReader(new File(input),
			        accesser);
			reader.setDecoyJudger(judger);
			return reader;
		}

		case OMSSA: {
			IPeptideReader reader = OMSSAPeptideReaderFactory.createReader(new File(input),
			        accesser);
			reader.setDecoyJudger(judger);
			return reader;
		}

		case MASCOT: {
			return createRawReader4Mascot(input,database, "", judger); //CSVPeptideReader don't need regex temporarily
/*			
			throw new RuntimeException(
			        "In order to parse the accession number, please use "
			                + "method-createPplReader4Mascot()- instead.");
*/
		}

		case XTANDEM: {
			IPeptideReader reader = XTandemPeptideReaderFactory.createReader(new File(input),
			        accesser);
			reader.setDecoyJudger(judger);

			return reader;
		}

		case CRUX: {
			IPeptideReader reader = CruxPeptideReaderFactory.createReader(new File(input),
			        accesser, judger);
			return reader;
		}

		}

		throw new ReaderGenerateException("Unknown reader type: " + type);

	}

	/**
	 * Create the peptide reader for the specific input file. You can then use
	 * the global method setTopN(int) to specify the number of reported top
	 * matched peptides (if any). Default: top 1.
	 * 
	 * <p>
	 * <b>Only for mascot</b>
	 * 
	 * <p>
	 * peptide list file will not be automatically created.
	 * 
	 * @param output
	 * @param input
	 * @param database
	 * @param access_regex
	 * @return
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws FileNotFoundException
	 */
	public static IPeptideReader createRawReader4Mascot(String input,
	        String database, String access_regex, IDecoyReferenceJudger judger)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FileNotFoundException {
		IPeptideReader reader = MascotPeptideReaderFactory.createReader(input, database,
		        access_regex, judger);
		return reader;
	}

	/**
	 * Create the peptide reader for the specific input file. You can then use
	 * the global method setTopN(int) to specify the number of reported top
	 * matched peptides (if any). Default: top 1.
	 * 
	 * <p>
	 * <b>Only for Inspect</b>
	 * 
	 * <p>
	 * peptide list file will not be automatically created.
	 * 
	 * @param output
	 * @param input
	 * @param database
	 * @param access_regex
	 * @return
	 */
	public static IPeptideReader createRawReader4Inspect(String input,
	        String paramfile, String sourcefile, String database, IDecoyReferenceJudger judger)
	        throws ReaderGenerateException, ImpactReaderTypeException,
	        FastaDataBaseException, IOException {
		
		IPeptideReader reader = InspectPeptideReaderFactory.createReader(new File(input),
		        new File(paramfile), new File(sourcefile), new File(database), judger);
		reader.setDecoyJudger(judger);
		return reader;
	}
}
