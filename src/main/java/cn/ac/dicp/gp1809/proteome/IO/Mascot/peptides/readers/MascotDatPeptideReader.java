/* 
 ******************************************************************************
 * File: MascotDatPeptideReader.java * * * Created on 11-21-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotParameter;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.DatReader;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotDatParsingException;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;

/**
 * This class used inner DatReader to get the peptide. This class is mainly for
 * the consistency that PeptideReaders should localized at the reader package.
 * 
 * @author Xinning
 * @version 0.1.2, 05-20-2010, 20:05:26
 */
public class MascotDatPeptideReader implements IMascotPeptideReader {

	private DatReader reader;
	private IDecoyReferenceJudger judger;

	/**
	 * 
	 * construct a reader
	 * 
	 * @param localName
	 * @param localFasta
	 * @param accession_regex
	 *            the regular expression string in Mascot for parsing the
	 *            database. (The ORIGINAL regex string even through it may be an
	 *            illegal regex string)
	 * 
	 * 
	 * @throws MascotDatParsingException
	 * @throws ModsReadingException
	 * @throws InvalidEnzymeCleavageSiteException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	public MascotDatPeptideReader(String localName, String localFasta, String accession_regex,
			IDecoyReferenceJudger judger)
			throws MascotDatParsingException, ModsReadingException, InvalidEnzymeCleavageSiteException,
			FastaDataBaseException, IOException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {
		this(new File(localName), new File(localFasta), accession_regex, judger);
	}

	/**
	 * 
	 * @param localFile
	 * @param localFastaFile
	 * @param accession_regex
	 *            the regular expression string in mascot for parsing the
	 *            database. (The ORIGINAL regex string even through it may be an
	 *            illegal regex string)
	 * 
	 * @throws MascotDatParsingException
	 * @throws ModsReadingException
	 * @throws InvalidEnzymeCleavageSiteException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	public MascotDatPeptideReader(File localFile, File localFastaFile, String accession_regex,
			IDecoyReferenceJudger judger)
			throws MascotDatParsingException, ModsReadingException, InvalidEnzymeCleavageSiteException,
			FastaDataBaseException, IOException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {
		this.reader = new DatReader(localFile, localFastaFile, accession_regex, judger);
		this.judger = judger;

	}

	@Override
	public IMascotPeptide getPeptide() throws PeptideParsingException {
		IMascotPeptide pep = reader.getPeptide();
		return pep;
	}

	@Override
	public MascotParameter getSearchParameter() {
		return reader.getSearchParameter();
	}

	@Override
	public void close() {
		this.reader.close();
	}

	public File getFile() {
		return this.reader.getFile();
	}

	@Override
	public PeptideType getPeptideType() {
		return this.reader.getPeptideType();
	}

	@Override
	public int getTopN() {
		return this.reader.getTopN();
	}

	@Override
	public void setTopN(int topn) {
		this.reader.setTopN(topn);
	}

	@Override
	public IMascotPeptideFormat<?> getPeptideFormat() {
		return this.reader.getPeptideFormat();
	}

	@Override
	public void setPeptideFormat(IPeptideFormat<?> format) {
		this.reader.setPeptideFormat(format);
	}

	@Override
	public IDecoyReferenceJudger getDecoyJudger() {
		return this.judger;
	}

	@Override
	public void setDecoyJudger(IDecoyReferenceJudger judger) {
		throw new IllegalArgumentException("Cannot be set here");
	}

	@Override
	public void setReplace(HashMap<Character, Character> replaceMap) {
	}

	@Override
	public ProteinNameAccesser getProNameAccesser() {
		return reader.getProNameAccesser();
	}
}
