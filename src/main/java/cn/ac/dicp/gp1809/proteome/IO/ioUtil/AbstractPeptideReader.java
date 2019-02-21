/* 
 ******************************************************************************
 * File: AbstractPeptideReader.java * * * Created on 09-10-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReferenceUpdatingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * Abstract peptide reader for easy use.
 * 
 * @author Xinning
 * @version 0.1.5, 05-20-2010, 17:22:47
 */
public abstract class AbstractPeptideReader implements IPeptideReader {
	/**
	 * The maximum reported number of top matched peptides. The setting of topn
	 * by method {@link #setTopN(int)} will be limited by this number. Default
	 * 50.
	 */
	public static final int MAX_TOPN = 50;

	// only top # matched peptide can be selected, default 1.
	private int ntopscore = 1;

	private File file;

	private IFastaAccesser accesser;

	private IDecoyReferenceJudger judger;

	private IPeptideFormat<?> format;

	private boolean replace = false;

	private HashMap<Character, Character> replaceMap;

	protected AbstractPeptideReader(String filename) {
		this(new File(filename));
	}

	protected AbstractPeptideReader(File file) {
		this.file = file;
	}

	protected AbstractPeptideReader(String filename, IPeptideFormat<?> format) {
		this(new File(filename), format);
	}

	protected AbstractPeptideReader(File file, IPeptideFormat<?> format) {
		this.file = file;
		if (file == null || !file.exists()) {
			throw new NullPointerException("The input file is null.");
		}
		this.setPeptideFormat(format);
	}

	public void setReplace(HashMap<Character, Character> replaceMap) {
		if (replaceMap != null && replaceMap.size() > 0) {
			this.replace = true;
			this.replaceMap = replaceMap;
		}
	}

	@Override
	public IPeptide getPeptide() throws PeptideParsingException {

		IPeptide peptide = this.getPeptideImp();

		return peptide;
	}

	/**
	 * Because the generated peptides are used to renew the reference of peptide
	 * so that the output peptide is well formatted, therefore, the reading of
	 * peptides lay on this peptide.
	 * 
	 * @return
	 */
	protected abstract IPeptide getPeptideImp() throws PeptideParsingException;

	// @Override
	public File getFile() {
		return this.file;
	}

	/**
	 * Get the fasta accesser
	 * 
	 * @return
	 */
	public IFastaAccesser getFastaAccesser() {
		if (this.accesser == null) {

			this.accesser = this.getSearchParameter().getFastaAccesser(judger);

		}

		return this.accesser;
	}

	/**
	 * This method is used to renew the reference of peptide after reading in by
	 * a peptide reader. You may need this method for readers not a ppl peptide
	 * reader if the peptide list file is well formatted. However, currently all
	 * the readers should use this method to format the protein reference.
	 * 
	 * <p>
	 * If the input is a ProteinReference, and the index of the protein
	 * reference is not set (e.g. for SEQUEST xml or xls exported file), you may
	 * want to renew the index of protein reference for this protein database.
	 * Or you may want to format the name of protein so that the length is
	 * minimum. Use this method to do this.
	 * 
	 * <p>
	 * <b>Notice: only the index of ProteinReference with original index of 0
	 * (default value) will be renew.
	 * 
	 * <p>
	 * Compared with {@link #getSequence(ProteinReference)}, this method uses
	 * less time when the index of protein reference has been defined.
	 * 
	 * @param peptide
	 * @throws ReferenceUpdatingException
	 */
	protected final void renewReference(IPeptide peptide) throws ReferenceUpdatingException {

		if (peptide == null)
			return;

		if (replace) {
			String seq = peptide.getSequence();
			Iterator<Character> it = this.replaceMap.keySet().iterator();
			while (it.hasNext()) {
				Character c = it.next();
				seq = seq.replace(c, replaceMap.get(c));
			}
			peptide.updateSequence(seq);
		}

		if (this.accesser == null) {
			this.getFastaAccesser();
		}

		/*
		 * No database preset, the renew task will not be executed
		 */
		if (this.accesser == null)
			return;

		try {
			for (Iterator<ProteinReference> iterator = peptide.getProteinReferences().iterator(); iterator.hasNext();) {
				this.accesser.renewReference(iterator.next());
			}
		} catch (Exception e) {
			System.out.println(peptide);
			throw new ReferenceUpdatingException(e);
		}

	}

	@Override
	public IPeptideFormat<?> getPeptideFormat() {
		return this.format;
	}

	@Override
	public void setPeptideFormat(IPeptideFormat<?> format) {
		this.format = format;
	}

	@Override
	public void setTopN(int topn) {
		if (topn < 1) {
			System.out.println("Top n <1, set to 1");
			this.ntopscore = 1;
		} else if (topn > MAX_TOPN) {
			this.ntopscore = MAX_TOPN;
		} else
			this.ntopscore = topn;
	}

	@Override
	public int getTopN() {
		return this.ntopscore;
	}

	@Override
	public IDecoyReferenceJudger getDecoyJudger() {
		return this.judger;
	}

	@Override
	public void setDecoyJudger(IDecoyReferenceJudger judger) {
		this.judger = judger;
	}
}
