/* 
 ******************************************************************************
 * File: PplCreateTask.java * * * Created on 03-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.gui;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.APIVASEII.APIVASETask;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.util.progress.ITaskDetails;

/**
 * The task for APIVASE
 * 
 * @author Xinning
 * @version 0.2, 04-30-2009, 21:10:55
 */
class APIVASETaskDetails implements ITaskDetails {

	private String ms2, ms3, mzdata, output;
	private DtaType type;
	private ISpectrumThreshold threshold;
	private int MSnCount;

	APIVASETaskDetails(String ms2, String ms3, String mzdata, DtaType type, String output,
	        ISpectrumThreshold threshold, int MSnCount) {

		this.ms2 = ms2;
		this.ms3 = ms3;
		this.mzdata = mzdata;
		this.type = type;
		this.output = output;
		this.threshold = threshold;
		this.MSnCount = MSnCount;

		this.validateNull();
	}

	private void validateNull() {
		if (this.ms2 == null || this.ms2.length() == 0
		        || !new File(ms2).exists())
			throw new NullPointerException(
			        "MS2 peptide list file is null or doesn't exist.");

		if (this.ms3 == null || this.ms3.length() == 0
		        || !new File(ms3).exists())
			throw new NullPointerException(
			        "MS3 peptide list file is null or doesn't exist.");

		if (this.mzdata == null || this.mzdata.length() == 0
		        || !new File(mzdata).exists())
			throw new NullPointerException(
			        "MzData file is null or doesn't exist.");

		if (this.threshold == null)
			throw new NullPointerException("Spectrum threshold is null.");

		if (this.MSnCount <= 0) {
			throw new IllegalArgumentException("MSn count is less than 0");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITaskDetails#getTask()
	 */
	@Override
	public APIVASETask getTask() {
		APIVASETask task = null;
		try {
			task = new APIVASETask(ms2, ms3, this.output, this.mzdata, type,
			        this.MSnCount, threshold);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return task;
	}

	/**
	 * The ms2 directory
	 */
	@Override
	public String toString() {
		return this.ms2;
	}
}
