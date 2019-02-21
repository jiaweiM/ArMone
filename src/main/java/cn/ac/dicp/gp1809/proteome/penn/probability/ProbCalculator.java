/* 
 ******************************************************************************
 * File: ProbCalculator.java * * * Created on 11-30-2007
 *
 * Copyright (c) 2007 Xinning Jiang vext@163.com
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
package cn.ac.dicp.gp1809.proteome.penn.probability;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideProbListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideProbListWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.pepxml.SEQUESTPepxmlWriter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.SequestPeptideListReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.util.SmartTimer;
import cn.ac.dicp.gp1809.util.ioUtil.xml.XmlWritingException;

/**
 * A factory class for different probability calculation algorithm.
 * 
 * @author Xinning
 * @version 0.3.1, 05-25-2010, 15:38:30
 */
public class ProbCalculator {

	/**
	 * The Shepard algorithm. The common idea is that the nearer the peptides,
	 * the more similar they are. The weight for each peptide is the calculated
	 * by put 1 over the distance of peptide. Then the final value is computed
	 * by multiply the weight by the value of this peptide.
	 */
	public static final int ALGORITHM_SHEPARD = 0;

	/**
	 * The K_NEAR cluster algorithm.
	 * 
	 * The peptides are cluster into a group with K peptides by considering of
	 * the distance between the peptide for calculation of probability and
	 * others in the group. No weight of distance is used. The final probability
	 * for the peptide is calculated by derived the number of reversed peptides
	 * multiplied by 2 into the number fo total peptides in the group.
	 */
	public static final int ALGORITHM_K_NEAR = 1;

	/**
	 * Fist compute the basic probability using B_NEAR instances, then iterating
	 * the probability until a specific precise.
	 */
	public static final int ALGORITHM_K_NEAR_ITERATOR = 2;

	private IPeptideProbCalculator pcalor;

	/**
	 * For inherit;
	 */
	protected ProbCalculator() {
	}

	/**
	 * Create a ProbCalculator using the specific algorithm
	 * 
	 * @param algorithm
	 */
	public ProbCalculator(int algorithm) {
		if (algorithm == ALGORITHM_SHEPARD)
			pcalor = new Shepard_ProbCalculator();
		else if (algorithm == ALGORITHM_K_NEAR)
			pcalor = new KN_Cluster_ProbCalculator();
		else if (algorithm == ALGORITHM_K_NEAR_ITERATOR)
			pcalor = new KNIterationProbCalculator();
		else
			throw new IllegalArgumentException("The algorithm: " + algorithm
			        + " is a unknown algorithm");
	}

	/**
	 * Calculate the probability for peptides
	 * 
	 * @param peps
	 * @throws IOException
	 */
	public void calculate(PepNorm[] peps) {
		this.pcalor.calculate(peps);
	}

	/**
	 * Calculate the probabilities for peptides and write them into the ppls
	 * file.
	 * 
	 * @param pplsname
	 *            the outputted ppls file.
	 * @param inputfiles
	 *            : files containing peptide informations (@see
	 *            ProbReaderFactory)
	 * @param algrothm
	 *            : algorithm used for probability calculation (@see
	 *            ProbCalculator.ALGORITHM_XXXX)
	 * @param isUnique
	 *            : In low accuracy mass spectrometer, 2+ and 3+ peptide will
	 *            both match to a single spectrum, if only one peptide for a
	 *            spctrum is outputted, true should be selected, otherwise, both
	 *            2+ and 3+ peptides will be outputted.
	 *            <p>
	 *            <b>Currently, the peptide with highest probability was
	 *            retained. If the same, 2+ peptide was retained.</b>
	 * 
	 * @param minmumprob
	 *            the minimum probability for peptide which will be wrote into
	 *            the ppls file. Set -1 if all the peptides are outputted.
	 * @throws PeptideParsingException
	 * @throws ImpactReaderTypeException
	 * @throws ReaderGenerateException
	 * @throws ProWriterException
	 * @throws PeptideParsingException
	 * @throws IOException
	 * @throws FileDamageException
	 * @throws ProWriterException
	 * @throws ReaderGenerateException 
	 */
	public static void createPPLSFile(String pplsname, String inputpplfile,
	        int algorithm, boolean isUnique, float minimumprob)
	        throws PeptideParsingException, FileDamageException, IOException,
	        ProWriterException, ReaderGenerateException {

		if (algorithm < -1 || algorithm > 2) {
			throw new NullPointerException("Unknown algorithm: " + algorithm
			        + ". See also ProbCalculator.ALGORITHM_XXXX");
		}

		AttNormalizer norm1 = new AttNormalizer();
		AttNormalizer norm2 = new AttNormalizer();
		AttNormalizer norm3 = new AttNormalizer();
		AttNormalizer norm4 = new AttNormalizer();

		int idx = 0;

		SequestPeptideListReader sreader = new SequestPeptideListReader(inputpplfile);
		sreader.setTopN(1);

		IPeptideFormat<?> formatter = sreader.getPeptideFormat();
		ISearchParameter param = sreader.getSearchParameter();

		if (sreader.getPeptideType() != PeptideType.SEQUEST) {
			throw new IllegalArgumentException(
			        "Currently only support SEQUEST.");
		}


		ISequestPeptide speptide;
		while ((speptide = sreader.getPeptide()) != null) {
			short charge = speptide.getCharge();
			if (charge == 2)
				norm2.put(speptide, idx++);
			else if (charge == 3)
				norm3.put(speptide, idx++);
			else if (charge == 1)
				norm1.put(speptide, idx++);
			/*
			 * With charge bigger than 4 will be calculated together
			 */
			else if (charge >= 4)
				norm4.put(speptide, idx++);
		}

		sreader.close();

		ProbCalculator pcalor = new ProbCalculator(algorithm);

		PepNorm[] peps1 = norm1.getNormPep();
		pcalor.calculate(peps1);
		PepNorm[] peps2 = norm2.getNormPep();
		pcalor.calculate(peps2);
		PepNorm[] peps3 = norm3.getNormPep();
		pcalor.calculate(peps3);
		PepNorm[] peps4 = norm4.getNormPep();
		pcalor.calculate(peps4);

		int id1 = 0;
		int id2 = 0;
		int id3 = 0;
		int id4 = 0;

		PeptideProbListWriter pplsWriter = new PeptideProbListWriter(pplsname,
		        formatter, param, sreader.getDecoyJudger(), isUnique, minimumprob, sreader.getProNameAccesser());

		sreader = new SequestPeptideListReader(inputpplfile);
		sreader.setTopN(1);

		ISequestPeptide peptide;
		while ((peptide = sreader.getPeptide()) != null) {
			short charge = peptide.getCharge();
			PepNorm pep;
			if (charge == 2)
				pep = peps2[id2++];
			else if (charge == 3)
				pep = peps3[id3++];
			else if (charge == 1)
				pep = peps1[id1++];
			else 
				pep = peps4[id4++];

			peptide.setProbability(pep.probablity);
			pplsWriter.write(peptide, sreader.getPeakLists());
		}

		pplsWriter.close();
	}
	
	/**
	 * Calculate the probabilities for peptides and write them into the ppls
	 * file.
	 * 
	 * @param pplsname
	 *            the outputted ppls file.
	 * @param inputfiles
	 *            : files containing peptide informations (@see
	 *            ProbReaderFactory)
	 * @param algrothm
	 *            : algorithm used for probability calculation (@see
	 *            ProbCalculator.ALGORITHM_XXXX)
	 * @param isUnique
	 *            : In low accuracy mass spectrometer, 2+ and 3+ peptide will
	 *            both match to a single spectrum, if only one peptide for a
	 *            spctrum is outputted, true should be selected, otherwise, both
	 *            2+ and 3+ peptides will be outputted.
	 *            <p>
	 *            <b>Currently, the peptide with highest probability was
	 *            retained. If the same, 2+ peptide was retained.</b>
	 * 
	 * @param minmumprob
	 *            the minimum probability for peptide which will be wrote into
	 *            the ppls file. Set -1 if all the peptides are outputted.
	 * @throws PeptideParsingException
	 * @throws ImpactReaderTypeException
	 * @throws ReaderGenerateException
	 * @throws ProWriterException
	 * @throws PeptideParsingException
	 * @throws IOException
	 * @throws FileDamageException
	 * @throws ProWriterException
	 * @throws ReaderGenerateException 
	 */
	public static void createPPLSFileAllInOne(String pplsname, String inputpplfile,
	        int algorithm, boolean isUnique, float minimumprob)
	        throws PeptideParsingException, FileDamageException, IOException,
	        ProWriterException, ReaderGenerateException {

		if (algorithm < -1 || algorithm > 2) {
			throw new NullPointerException("Unknown algorithm: " + algorithm
			        + ". See also ProbCalculator.ALGORITHM_XXXX");
		}

		AttNormalizer norm1 = new AttNormalizer();

		int idx = 0;

		SequestPeptideListReader sreader = new SequestPeptideListReader(inputpplfile);
		sreader.setTopN(1);

		IPeptideFormat<?> formatter = sreader.getPeptideFormat();
		ISearchParameter param = sreader.getSearchParameter();

		if (sreader.getPeptideType() != PeptideType.SEQUEST) {
			throw new IllegalArgumentException(
			        "Currently only support SEQUEST.");
		}


		ISequestPeptide speptide;
		while ((speptide = sreader.getPeptide()) != null) {
			norm1.put(speptide, idx++);
		}

		sreader.close();

		ProbCalculator pcalor = new ProbCalculator(algorithm);

		PepNorm[] peps1 = norm1.getNormPep();
		pcalor.calculate(peps1);


		PeptideProbListWriter pplsWriter = new PeptideProbListWriter(pplsname,
		        formatter, param, sreader.getDecoyJudger(), isUnique, minimumprob, sreader.getProNameAccesser());

		sreader = new SequestPeptideListReader(inputpplfile);
		sreader.setTopN(1);

		int id1 = 0;
		ISequestPeptide peptide;
		while ((peptide = sreader.getPeptide()) != null) {
			PepNorm pep = peps1[id1++];
			peptide.setProbability(pep.probablity);
			pplsWriter.write(peptide, sreader.getPeakLists());
		}

		pplsWriter.close();
	}

	/**
	 * Calculate the probabilities for peptides and write them into the ppls
	 * file.
	 * 
	 * @param pepxml
	 *            the outputted ppls file.
	 * @param inputfiles
	 *            : files containing peptide informations (@see
	 *            ProbReaderFactory)
	 * @param algrothm
	 *            : algorithm used for probability calculation (@see
	 *            ProbCalculator.ALGORITHM_XXXX)
	 * @param isUnique
	 *            : In low accuracy mass spectrometer, 2+ and 3+ peptide will
	 *            both match to a single spectrum, if only one peptide for a
	 *            spctrum is outputted, true should be selected, otherwise, both
	 *            2+ and 3+ peptides will be outputted.
	 *            <p>
	 *            <b>Currently, the peptide with highest probability was
	 *            retained. If the same, 2+ peptide was retained.</b>
	 * 
	 * @param minmumprob
	 *            the minimum probability for peptide which will be wrote into
	 *            the ppls file. Set -1 if all the peptides are outputted.

	 * @throws ProWriterException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws ParamFileParseException
	 * @throws XmlWritingException
	 * @throws PeptideParsingException
	 * @throws FileDamageException
	 * @throws ReaderGenerateException 
	 */
	public static void createPepXml(String pepxml, String inputpplfile,
	        String database, int algorithm, boolean isUnique, float minimumprob)
	        throws ProWriterException, IOException, FastaDataBaseException,
	        XmlWritingException, PeptideParsingException, FileDamageException, ReaderGenerateException {

		String xml = pepxml+".xml";
		String ppls = pepxml;

		if (!new File(ppls).exists())
			createPPLSFile(ppls, inputpplfile, algorithm, isUnique, minimumprob);

		PeptideProbListReader preader = new PeptideProbListReader(ppls);

		SequestParameter param = (SequestParameter) preader
		        .getSearchParameter();

		param.setDatabase(database);

		SEQUESTPepxmlWriter pepxmlWriter = new SEQUESTPepxmlWriter(xml, param, preader.getDecoyJudger());
		
//		System.out.println(preader.getNumberofPeptides());
		ISequestPeptide peptide;
		while ((peptide = (ISequestPeptide) preader.getPeptide()) != null) {
			if (peptide.getProbabilty() >= minimumprob) {
//				System.out.println(peptide.getScanNum());
				pepxmlWriter.write(peptide, null);
			}
		}

		pepxmlWriter.close();
	}

	
	
	
	public static void main(String[] args) throws PeptideParsingException,
	        FileDamageException, ProWriterException, IOException,
	        XmlWritingException, FastaDataBaseException, ReaderGenerateException {

		SmartTimer timer = new SmartTimer();
		int len = args.length;
		if (len < 4) {
			System.out
			        .println("ProbCalculator inputppl outfile fasta_db_path outtype(0 for ppls, 1 for pepxml) ");
			return;
		}

		int type = Integer.parseInt(args[3]);
		if (type == 0)
			ProbCalculator.createPPLSFile(args[1], args[0],
			        ProbCalculator.ALGORITHM_K_NEAR_ITERATOR, true, 0.05f);
		else if (type == 1) {
			ProbCalculator.createPepXml(args[1], args[0], args[2],
			        ProbCalculator.ALGORITHM_K_NEAR_ITERATOR, true, 0.05f);
		} else {
			throw new IllegalArgumentException(
			        "Type of output must be ppls or pepxml");
		}

		System.out.println("Finished writing the file in: " + timer);
	}
}
