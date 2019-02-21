/*
 ******************************************************************************
 * File: InvalidSpectraRemoveTask.java * * * Created on 05-14-2009
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII;

import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * APVIASE task
 * 
 * @author Xinning
 * @version 0.1, 05-14-2009, 18:27:19
 */
public class APIVASETask implements ITask {

	private String ms2file, ms3file, mzdata;
	private DtaType type;
	private String output;
	private int MSnCount;
	private ISpectrumThreshold threshold;
	private boolean hasNext = true;

	public APIVASETask(String ms2file, String ms3file, String mzdata, DtaType type,
	        int MSnCount, ISpectrumThreshold threshold) {

		this(ms2file, ms3file, ms2file.substring(0, ms2file.length() - 3)
		        + ".apv.ppl", mzdata, type, MSnCount, threshold);
	}

	public APIVASETask(String ms2file, String ms3file, String output,
	        String mzdata, DtaType type, int MSnCount, ISpectrumThreshold threshold) {

		this.ms2file = ms2file;
		this.ms3file = ms3file;
		this.output = output;
		this.mzdata = mzdata;
		this.type = type;
		this.threshold = threshold;
		this.MSnCount = MSnCount;
	}

	@Override
	public float completedPercent() {
		return 0;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public boolean inDetermineable() {
		return true;
	}

	@Override
	public void processNext() {

		try {
			APIVASE apv = new APIVASE(this.ms2file, this.ms3file, this.mzdata, type,
			        this.output, this.MSnCount, this.threshold);
			apv.process();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			this.hasNext = false;
		}
	}
	
	@Override
	public String toString() {
		return this.mzdata;
	}
}
