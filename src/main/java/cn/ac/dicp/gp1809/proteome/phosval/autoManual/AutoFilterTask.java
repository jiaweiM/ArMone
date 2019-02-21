/* 
 ******************************************************************************
 * File: AutoFilterTask.java * * * Created on 05-20-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval.autoManual;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.gui.PeptideListPagedRowGettor.PeptideRowReader;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * The auto filter task
 * 
 * @author Xinning
 * @version 0.1.1, 06-01-2009, 09:32:28
 */
public class AutoFilterTask implements ITask {

	private ISpectrumFilter spectrumfilter;
	private ISpectrumThreshold threshold;
	private int[] types;
	private boolean isMono;
	private int continuous;

	private PeptideRowReader reader;
	private IPeptide curtPeptide;
	private int total;
	private float totalf;
	private int curt;

	private AutoManualValidator filter;

	public AutoFilterTask(PeptideRowReader reader, ISpectrumFilter filter,
	        ISpectrumThreshold threshold, int[] types, boolean isMono,
	        int continuous) {
		this.reader = reader;
		this.spectrumfilter = filter;
		this.threshold = threshold;
		this.types = types;
		this.isMono = isMono;
		this.continuous = continuous;

		this.total = reader.getNumberofPeptides();
		this.totalf = total;

		AminoacidFragment aaf = new AminoacidFragment(reader
		        .getSearchParameter().getStaticInfo(), reader
		        .getSearchParameter().getVariableInfo());

		this.filter = new AutoManualValidator(aaf, this.continuous);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		return this.curt / this.totalf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		try {
			return (curtPeptide = this.reader.getPeptide()) != null;
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {

		if (this.curtPeptide == null)
			throw new NullPointerException("Null peptide. No more peptide?");

		IMS2PeakList peaklist = this.reader.getPeakLists()[0];
		short charge = this.curtPeptide.getCharge();
		peaklist.getPrecursePeak().setCharge(charge);

		boolean valid = this.filter.validate(peaklist, this.curtPeptide
		        .getPeptideSequence(), this.types, charge, this.spectrumfilter,
		        this.threshold, this.isMono);

		this.reader.setUsed4CurtPeptide(valid);

		this.curt = this.reader.getCurtPeptideIndex();
	}
}
