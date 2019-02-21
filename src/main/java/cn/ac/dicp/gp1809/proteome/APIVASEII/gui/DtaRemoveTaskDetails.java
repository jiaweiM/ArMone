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

import cn.ac.dicp.gp1809.proteome.APIVASEII.dtadistill.IInvalidSpectraRemoveTask;
import cn.ac.dicp.gp1809.proteome.APIVASEII.dtadistill.ValidMS3XtractTask;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosConstants;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.util.progress.ITaskDetails;

/**
 * The task for ppl creation
 * 
 * @author Xinning
 * @version 0.2.1, 06-04-2009, 15:50:11
 */
class DtaRemoveTaskDetails implements ITaskDetails {

	private double loss = PhosConstants.PHOSPHATE_MASS;

	private String ms2, ms3, mzdata;
	private DtaType type;
	private ISpectrumThreshold threshold;
	private int MSnCount;
	private boolean renewMS3;

	DtaRemoveTaskDetails(String ms2, String ms3, String mzdata, DtaType type,
	        ISpectrumThreshold threshold, int MSnCount, boolean isRenewMS3) {

		this.ms2 = ms2;
		this.ms3 = ms3;
		this.mzdata = mzdata;
		this.type = type;
		this.threshold = threshold;
		this.MSnCount = MSnCount;
		this.renewMS3 = isRenewMS3;
		
		this.validateNull();
	}

	private void validateNull() {
		if (this.ms2 == null || this.ms2.length() == 0
		        || !new File(ms2).exists())
			throw new NullPointerException(
			        "MS2 directory is null or doesn't exist.");

		if (this.ms3 == null || this.ms3.length() == 0
		        || !new File(ms3).exists())
			throw new NullPointerException(
			        "MS3 directory is null or doesn't exist.");

		if (this.mzdata == null || this.mzdata.length() == 0
		        || !new File(mzdata).exists())
			throw new NullPointerException(
			        "MzData file is null or doesn't exist.");

		if (this.threshold == null)
			throw new NullPointerException("Spectrum threshold is null.");
		
		if (this.type == null)
			throw new NullPointerException("The DtaType is Null.");

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
	public IInvalidSpectraRemoveTask getTask() {
		IInvalidSpectraRemoveTask task = null;
		try {
			/*
			task = new InvalidSpectraRemoveTask(ms2, ms3, this.mzdata, this.type,
			        this.MSnCount, this.renewMS3, threshold, loss,
			        InvalidSpectraRemoveTask.TYPE_PAIRED_RETAIN);
			        */
			
			
			task = new ValidMS3XtractTask(ms2, ms3, this.mzdata, this.type,
			        this.MSnCount, this.renewMS3, threshold, loss);
			
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
