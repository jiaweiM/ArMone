/*
 ******************************************************************************
 * File: MascotEmbedPplCreatorTask.java * * * Created on 05-19-2010
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.group.MascotPercolatorDataReader;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.DatReader;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotDatParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.GlobalDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;

/**
 * For Mascot Dat file
 * 
 * @author Xinning
 * @version 0.1, 05-19-2010, 10:59:25
 */
public class MascotEmbedPplCreatorTask implements IEmbededPplCreationTask {

	private DatReader reader;

	private IPeptideWriter pwriter;

	private IPeptide peptide;
	private IScanDta curtPeaks;
	private IMS2PeakList[] peaklist;

	private boolean hasNext = true;
	private boolean closed = false;
	
	/**
	 * 
	 * @param output output file path
	 * @param input input dat file path
	 * @param topn how many top peptide to export
	 * @param database database file path 
	 * @param uniq_charge
	 * @param accession_regx
	 * @param judger
	 * @throws MascotDatParsingException
	 * @throws ModsReadingException
	 * @throws InvalidEnzymeCleavageSiteException
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 */
	public MascotEmbedPplCreatorTask(String output, String input, int topn, String database, boolean uniq_charge,
			String accession_regx, IDecoyReferenceJudger judger) throws MascotDatParsingException, ModsReadingException,
					InvalidEnzymeCleavageSiteException, ProteinNotFoundInFastaException,
					MoreThanOneRefFoundInFastaException, FastaDataBaseException, IOException {

		String per = input.replace("dat", "dat.tab.txt");
		if ((new File(per)).exists()) {
			reader = new DatReader(input, database, accession_regx, judger,
					MascotPercolatorDataReader.getQueryMap(per, 0.01f));
		} else {
			reader = new DatReader(input, database, accession_regx, judger);
		}
		reader.setTopN(topn);

		ISearchParameter parameter = reader.getSearchParameter();
		pwriter = new PeptideListWriter(output, reader.getPeptideFormat(), parameter, judger, uniq_charge,
				reader.getProNameAccesser());
	}

	public MascotEmbedPplCreatorTask(String output, String input, int topn, String database, boolean uniq_charge,
			String accession_regx, IDecoyReferenceJudger judger, HashMap<Integer, Integer> querymap)
					throws MascotDatParsingException, ModsReadingException, InvalidEnzymeCleavageSiteException,
					ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, FastaDataBaseException,
					IOException {

		reader = new DatReader(input, database, accession_regx, judger, querymap);
		reader.setTopN(topn);

		ISearchParameter parameter = reader.getSearchParameter();
		pwriter = new PeptideListWriter(output, reader.getPeptideFormat(), parameter, judger, uniq_charge,
				reader.getProNameAccesser());
	}

	@Override
	public float completedPercent() {
		return 0;
	}

	@Override
	public boolean hasNext() {

		if (!hasNext)
			return hasNext;

		try {
			hasNext = (peptide = reader.getPeptide()) != null;
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
		}

		if (!hasNext)
			this.dispose();

		return hasNext;
	}

	@Override
	public void processNext() {

		try {
			if (this.peptide != null) {

				IScanDta t = this.reader.getCurtDta(true);
				if (this.curtPeaks != t) {
					curtPeaks = t;
					IMS2PeakList peaks = t.getPeakList();
					peaks.sort();

					peaklist = new IMS2PeakList[] { peaks };

					PrecursePeak pp = peaks.getPrecursePeak();
					peptide.setInten(pp.getIntensity());
					peptide.setRetentionTime(pp.getRT());
				}

				pwriter.write(peptide, this.peaklist);

			} else {
				System.err.println("No next inner task need to be processed or use the hasNext() fist.");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean inDetermineable() {
		return true;
	}

	@Override
	public void dispose() {
		if (!this.closed) {

			if (this.reader != null)
				this.reader.close();

			if (this.pwriter != null)
				try {
					this.pwriter.close();
				} catch (ProWriterException e) {
					throw new RuntimeException(e);
				}

			this.closed = true;
		}
	}

	@Override
	public String toString() {
		return this.pwriter.toString();
	}

	/**
	 * 
	 * @param aDatFolder 
	 * @param database
	 * @param reg
	 * @param judger
	 * @throws MascotDatParsingException
	 * @throws ModsReadingException
	 * @throws InvalidEnzymeCleavageSiteException
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 */
	public static void batchProcess(String aDatFolder, String database, String reg, IDecoyReferenceJudger judger)
			throws MascotDatParsingException, ModsReadingException, InvalidEnzymeCleavageSiteException,
			ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, FastaDataBaseException, IOException {

		File[] files = (new File(aDatFolder)).listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				if (arg0.getName().endsWith("dat"))
					return true;

				return false;
			}

		});

		for (int i = 0; i < files.length; i++) {

			String ms2dat = files[i].getAbsolutePath();
			String ms2ppl = files[i].getAbsolutePath() + ".ppl";

			MascotEmbedPplCreatorTask task = new MascotEmbedPplCreatorTask(ms2ppl, ms2dat, 1, database, false, reg,
					judger);

			while (task.hasNext()) {
				task.processNext();
			}
			task.dispose();
		}
	}

	public static void batchProcess(String in, String database, String reg, IDecoyReferenceJudger judger,
			String percolator) throws MascotDatParsingException, ModsReadingException,
					InvalidEnzymeCleavageSiteException, ProteinNotFoundInFastaException,
					MoreThanOneRefFoundInFastaException, FastaDataBaseException, IOException {

		File[] files = (new File(in)).listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				if (arg0.getName().endsWith("dat"))
					return true;

				return false;
			}

		});

		for (int i = 0; i < files.length; i++) {

			String ms2dat = files[i].getAbsolutePath();
			String ms2ppl = percolator + "\\" + files[i].getName() + ".ppl";
			String per = percolator + "\\" + files[i].getName() + ".tab.txt";
			// String per = percolator+"\\"+files[i].getName()+"_decoy.tab.txt";
			System.out.println(ms2dat);
			HashMap<Integer, Integer> querymap = MascotPercolatorDataReader.getQueryMap(per, 0.01f);
			// System.out.println("~~~~~~~~~~\t"+querymap.get(8348));
			MascotEmbedPplCreatorTask task = new MascotEmbedPplCreatorTask(ms2ppl, ms2dat, 1, database, false, reg,
					judger, querymap);

			// MascotEmbedPplCreatorTask task = new
			// MascotEmbedPplCreatorTask(ms2ppl, ms2dat, 10, database,
			// false, reg, judger);

			while (task.hasNext()) {
				task.processNext();
			}
			task.dispose();
		}
	}

	public static void main(String args[])
			throws MascotDatParsingException, ModsReadingException, InvalidEnzymeCleavageSiteException,
			ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, FastaDataBaseException, IOException {

		String database = "F:\\DataBase\\o_glycan\\O-glycoprotein_0.fasta";
		// String database = "F:\\DataBase\\o_glycan\\final.fetuin.fasta";
		// String database = "F:\\DataBase\\o_glycan\\IGHA1.fasta";
		// String database =
		// "F:\\DataBase\\uniprot\\final.uniprot-human-20131211_0.fasta";
		// String database =
		// "F:\\DataBase\\uniprot\\uniprot-human-20131211_0.fasta";
		// String database =
		// "F:\\DataBase\\uniprot\\uniprot-mouse-20131211.fasta";
		// String database =
		// "D:\\ModDataBase\\v20141011.human\\final.human.jou.fasta";
		// String database =
		// "D:\\ModDataBase\\v20141002.mouse\\final.mouse.JOU.fasta";
		// String database =
		// "F:\\DataBase\\uniprot\\final.uniprot-mouse-20131211.fasta";
		// String accession_regex = ">IPI:([^| .]*)";
		// String accession_regex = ">\\([^|]*\\)";
		// String accession_regex = ">\\(gi\\|[0-9]*\\)";
		String accession_regex = "([^ ]*)";
		// String accession_regex = "..\\|\\([^|]*\\)";

		boolean uniq_charge = false;
		IDecoyReferenceJudger judger = new GlobalDecoyRefJudger("REV", false);
		// IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		// HashSet <Integer> queryset =
		// MascotPercolatorDataReader.getQuerySet("H:\\WFJ_mutiple_label\\2D_LysC\\2\\percolator\\"
		// +
		// "1000_F002497.dat.tab.txt", 0.05f);
		int topn = 1;
		MascotEmbedPplCreatorTask.batchProcess("H:\\OGLYCAN2\\20150116_ETHCD", database, accession_regex, judger);
	}

}
