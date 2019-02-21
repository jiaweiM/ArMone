/* 
 ******************************************************************************
 * File: AscoreCalculationTask.java * * * Created on 06-14-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation;

import java.io.FileNotFoundException;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * Calculation of ascore for the peptides in the peptide list file
 * 
 * @author Xinning
 * @version 0.1.2, 05-25-2010, 14:47:43
 */
public class AscoreCalculationTask implements ITask {

	private IPeptideListReader reader;
	private PeptideListWriter writer;
	private AscorePhosPeptideConvertor converter;
	private ISpectrumThreshold threshold;
	private int[] types;

	private int total;
	private int curt = 1;
	private IPeptide curtPep;

	public AscoreCalculationTask(String pplfile, String output, boolean uniq_pep_charge, int[] types,
			ISpectrumThreshold threshold) throws FileDamageException, IOException {
		this(new PeptideListReader(pplfile), output, uniq_pep_charge, types, threshold);
	}

	/**
	 * 
	 * @param reader
	 * @throws FileNotFoundException
	 */
	public AscoreCalculationTask(IPeptideListReader reader, String output, boolean uniq_pep_charge, int[] types,
			ISpectrumThreshold threshold) throws FileNotFoundException {
		this.reader = reader;
		PeptideType type = reader.getPeptideType();
		if (type.isPeptidePair()) {
			throw new IllegalArgumentException(
					"The Ascore of APIVASE output paired phosphopeptide of MS2/MS3 needn't to be recalculate.");
		}

		this.types = types;
		this.converter = new AscorePhosPeptideConvertor(reader.getSearchParameter(), reader.getPeptideType(),
				reader.getPeptideFormat());
		this.writer = new PeptideListWriter(output, converter.getAscorePeptideFormat(), converter.getAscoreParameter(),
				reader.getDecoyJudger(), uniq_pep_charge, reader.getProNameAccesser());
		this.threshold = threshold;
		this.total = reader.getNumberofPeptides() + 1;
	}

	@Override
	public float completedPercent() {
		return this.curt / (float) this.total;
	}

	@Override
	public void dispose() {
		try {
			this.writer.close();
		} catch (ProWriterException e) {
			throw new RuntimeException(e);
		}
		this.reader.close();
	}

	@Override
	public boolean hasNext() {
		try {
			return (this.curtPep = this.reader.getPeptide()) != null;
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean inDetermineable() {
		return false;
	}

	@Override
	public void processNext() {

		if (this.curtPep != null) {
			IMS2PeakList[] peaklists = this.reader.getPeakLists();
			boolean converted = this.converter.convert(this.curtPep, peaklists[0], types, threshold);
			if (converted)
				this.writer.write(this.curtPep, peaklists);

			this.curt = this.reader.getCurtPeptideIndex() + 1;
		}
	}

	public static void main(String[] args) throws FileDamageException, IOException {

		ISpectrumThreshold threshold = new SpectrumThreshold(0.8, 0.5);

		String input = args[0];
		String output = args[1];
		int[] types = new int[] { Ion.TYPE_B, Ion.TYPE_Y };

		boolean uniqu = true;

		AscoreCalculationTask task = new AscoreCalculationTask(input, output, uniqu, types, threshold);

		while (task.hasNext()) {
			task.processNext();
		}

		task.dispose();
	}
}
