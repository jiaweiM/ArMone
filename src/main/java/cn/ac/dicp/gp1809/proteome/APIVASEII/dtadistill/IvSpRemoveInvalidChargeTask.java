/*
 ******************************************************************************
 * File: IvSpRemoveInvalidChargeTask.java * * * Created on 05-14-2009
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.dtadistill;

import java.io.File;
import java.util.HashSet;

/**
 * Only remove the spectra whose charge states can be calculated from the
 * neutral loss peaks
 * 
 * @author Xinning
 * @version 0.1, 05-14-2009, 18:27:19
 */
class IvSpRemoveInvalidChargeTask implements IInvalidSpectraRemoveTask {

	private File ms2file, ms3file;

	private String[] pairedMS2InvalidCharge;
	private String[] pairedMS3InvalidCharge;

	private boolean removeMS2 = true;

	private int curtTotal = -1;
	private int total;
	private int curt = -1;

	IvSpRemoveInvalidChargeTask(File ms2file, File ms3file,
	        HashSet<String> pairedMS2InvalidCharge,
	        HashSet<String> pairedMS3InvalidCharge) {

		this.ms2file = ms2file;
		this.ms3file = ms3file;

		this.pairedMS2InvalidCharge = pairedMS2InvalidCharge
		        .toArray(new String[pairedMS2InvalidCharge.size()]);
		this.pairedMS3InvalidCharge = pairedMS3InvalidCharge
		        .toArray(new String[pairedMS3InvalidCharge.size()]);

		this.total = pairedMS2InvalidCharge.size()
		        + pairedMS3InvalidCharge.size();
	}

	@Override
	public float completedPercent() {
		return (curtTotal + 1) / (float) total;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean hasNext() {
		return this.curt + 1 < this.total;
	}

	@Override
	public boolean inDetermineable() {
		return false;
	}

	@Override
	public void processNext() {

		if (removeMS2) {
			if (curt + 1 < this.pairedMS2InvalidCharge.length) {
				String name = pairedMS2InvalidCharge[++curt];

				this.remove(ms2file, name);

				this.curtTotal++;
			} else {
				this.removeMS2 = false;
				this.curt = -1;
			}
		} else {
			String name = pairedMS3InvalidCharge[++curt];
			this.remove(ms3file, name);
			this.curtTotal++;
		}
	}

	/**
	 * Remove dta file and its out file (if exists)
	 * 
	 * @param dir
	 * @param name
	 */
	private void remove(File dir, String name) {
		new File(dir, name).delete();
		File out = new File(dir, name.substring(0, name.length() - 3) + "out");
		if (out.exists())
			out.delete();
	}
}
