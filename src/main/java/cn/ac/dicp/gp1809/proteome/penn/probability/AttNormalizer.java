/*
 ******************************************************************************
 * File: AttNormalizer.java * * * Created on 10-10-2007
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn.probability;

import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide;

/**
 * 
 * Normalize the scores for sequest output
 * 
 * <p>Changes:
 * <li>0.4, 08-08-2009: Modified to use the ArCommon class library
 * 
 * @author Xinning
 * @version 0.4, 08-08-2009, 12:57:32
 */
class AttNormalizer {

	/**
	 * Normalization by line. The normalized value is between 0 and 1; All the
	 * normalized value are the bigger the better.
	 */
	public static final int NORM_LINE = 1;

	/**
	 * Normalization by z-score. The average of the normalized values is 0 and
	 * SD is 1;
	 */
	public static final int NORM_Z_SCORE = 2;

	private ArrayList<PepNorm> list = new ArrayList<PepNorm>(6000);

	private boolean has_value;

	/*
	 * When the normalized peptide has been called. this value changed to true
	 * and the put() method become closed.
	 */
	// private boolean isNormed = false;
	private float xcorrSum, dcnSum, spSum, rspSum, dmsSum, ionsSum, mpfSum,
	        simSum;
	private float xcorr2Sum, dcn2Sum, sp2Sum, rsp2Sum, dms2Sum, ions2Sum,
	        mpf2Sum, sim2Sum;

	private float xcorrMin, dcnMin, spMin, rspMin, dmsMin, ionsMin, mpfMin,
	        simMin;
	private float xcorrMax, dcnMax, spMax, rspMax, dmsMax, ionsMax, mpfMax,
	        simMax;

	public AttNormalizer() {
	}

	/**
	 * Add a peptide to the pool of normalized peptide. <b> When the
	 * getNormPep() has been excuted (peptide has been normalized), peptide
	 * cannot be added yet. Otherwise, throw RuntimeException</b> <b>Notice: as
	 * the distribution can only be normalized with a single charge state only
	 * the peptide with same charge state can be put together and be normalized
	 * </b>
	 * 
	 * @param peptide
	 *            a raw peptide
	 * @param idx,
	 *            the index of peptide in the file. Because the most information
	 *            is not contained in pepNorm, it is necessary to reRead the
	 *            file and merge the prob and normal infor together,
	 */
	public void put(ISequestPeptide peptide, int idx) {

		PepNorm pepnorm = new PepNorm();
		pepnorm.idx = idx;
		pepnorm.isRev = !peptide.isTP();

		int len = peptide.getPeptideSequence().getUniqueSequence().length();
		double lnlen = Math.log(len);
		float xcn = (float) (Math.log(peptide.getXcorr()) / lnlen);
		float dcn = (peptide.getDeltaCn() < 1 ? peptide.getDeltaCn() : 0f);
		// The bigger the better
		float rsp = -(float) Math.log(peptide.getRsp());
		float sp = peptide.getSp();
		// The bigger the better, in ppm as only used in high accuracy mass
		// spectrometer
		float dms = (float) -Math.abs((peptide.getDeltaMH() - 1.00782f)
		        / (peptide.getMH() - 1.00782f));
		float ions = peptide.getIonPercent();

		String seq = peptide.getPeptideSequence().getUniqueSequence();
		float mpf = 0f;
		for (int i = 0, n = seq.length(); i < n; i++) {
			char c = seq.charAt(i);
			if (c == 'R')
				mpf += 1f;
			else if (c == 'K')
				mpf += 0.8f;
			else if (c == 'H')
				mpf += 0.5f;
		}

		float sim = peptide.getSim();

		if (has_value) {
			// Get the minimum value and max value
			if (xcn < xcorrMin)
				xcorrMin = xcn;
			else if (xcn > xcorrMax)
				xcorrMax = xcn;

			if (dcn < dcnMin)
				dcnMin = dcn;
			else if (dcn > dcnMax)
				dcnMax = dcn;

			if (rsp < rspMin)
				rspMin = rsp;
			else if (rsp > rspMax)
				rspMax = rsp;

			if (sp < spMin)
				spMin = sp;
			else if (sp > spMax)
				spMax = sp;

			if (dms > dmsMax)
				dmsMax = dms;
			else if (dms < dmsMin)
				dmsMin = dms;

			if (ions < ionsMin)
				ionsMin = ions;
			else if (ions > ionsMax)
				ionsMax = ions;

			if (mpf < mpfMin)
				mpfMin = mpf;
			else if (mpf > mpfMax)
				mpfMax = mpf;

			if (sim < simMin)
				simMin = sim;
			else if (sim > simMax)
				simMax = sim;
		} else {
			xcorrMin = xcn;
			xcorrMax = xcn;
			dcnMin = dcn;
			dcnMax = dcn;
			rspMin = rsp;
			rspMax = rsp;
			spMin = sp;
			spMax = sp;
			dmsMax = dms;
			dmsMin = dms;
			ionsMin = ions;
			ionsMax = ions;
			mpfMin = mpf;
			mpfMax = mpf;
			simMin = sim;
			simMax = sim;

			has_value = true;
		}

		// prepare for normalization by z-score
		xcorrSum += (pepnorm.xcn = xcn);
		xcorr2Sum += xcn * xcn;
		dcnSum += (pepnorm.dcn = dcn);
		dcn2Sum += dcn * dcn;
		rspSum += (pepnorm.rspn = rsp);
		rsp2Sum += rsp * rsp;
		spSum += (pepnorm.spn = sp);
		sp2Sum += sp * sp;
		dmsSum += (pepnorm.dMS = dms);
		dms2Sum += dms * dms;
		ionsSum += (pepnorm.ions = ions);
		ions2Sum += ions * ions;
		mpfSum += (pepnorm.MPF = mpf);
		mpf2Sum += mpf * mpf;
		simSum += (pepnorm.sim = sim);
		sim2Sum += sim * sim;

		list.add(pepnorm);
	}

	/**
	 * ��ʱʹ�ã�ֻ���ڵ���getNormPep֮ǰ��Ч
	 * 
	 * @return
	 */
	public PepNorm[] getOriginal() {
		return this.list.toArray(new PepNorm[list.size()]);
	}

	/**
	 * Return the peptide with normalized scores using default line
	 * normalization strategy
	 */
	public PepNorm[] getNormPep() {
		return this.getNormPep(NORM_LINE);
	}

	/**
	 * Return the peptide with normalized scores.
	 * 
	 * @param normType :
	 *            (Norm_Z_SCORE or Norm_LINE)
	 */
	public PepNorm[] getNormPep(int normType) {
		if (normType == NORM_LINE)
			this.normalizeLine();
		else if (normType == NORM_Z_SCORE)
			this.normalizeZ();
		else
			throw new IllegalArgumentException(
			        "The specific normType can't be resolved: " + normType);

		return this.list.toArray(new PepNorm[this.list.size()]);
	}

	/*
	 * Normalization by line. The normalized value is between 0 and 1; All the
	 * normalized value are the bigger the better.
	 */
	private void normalizeLine() {
		int size = list.size();
		float xcorrRang = xcorrMax - xcorrMin;
		float dcnRang = dcnMax - dcnMin;
		float rspRang = rspMax - rspMin;
		float spRang = spMax - spMin;

		float dmsRang = dmsMax - dmsMin;
		float ionsRang = ionsMax - ionsMin;
		float mpfRang = mpfMax - mpfMin;
		float simRang = simMax - simMin;

		/*
		 * If the range is 0, set as 1f, and the normalized values are all 0f.
		 */
		if (xcorrRang == 0f)
			xcorrRang = 1f;
		if (dcnRang == 0f)
			dcnRang = 1f;
		if (rspRang == 0f)
			rspRang = 1f;
		if (spRang == 0f)
			spRang = 1f;
		if (dmsRang == 0f)
			dmsRang = 1f;
		if (ionsRang == 0f)
			ionsRang = 1f;
		if (mpfRang == 0f)
			mpfRang = 1f;
		if (simRang == 0f)
			simRang = 1f;

		for (int i = 0; i < size; i++) {
			PepNorm pepnorm = list.get(i);
			pepnorm.xcn = (pepnorm.xcn - xcorrMin) / xcorrRang;
			pepnorm.dcn = (pepnorm.dcn - dcnMin) / dcnRang;
			pepnorm.spn = (pepnorm.spn - spMin) / spRang;
			pepnorm.rspn = (pepnorm.rspn - rspMin) / rspRang;
			pepnorm.dMS = (pepnorm.dMS - dmsMin) / dmsRang;
			pepnorm.ions = (pepnorm.ions - ionsMin) / ionsRang;
			pepnorm.MPF = (pepnorm.MPF - mpfMin) / mpfRang;
			pepnorm.sim = (pepnorm.sim - simMin) / simRang;
		}
	}

	/*
	 * Normalization by z-score. The average of the normalized values is 0 and
	 * SD is 1;
	 */
	private void normalizeZ() {
		int size = list.size();

		float xcorrAvg = this.xcorrSum / size;
		float dcnAvg = this.dcnSum / size;
		float spAvg = this.spSum / size;
		float rspAvg = this.rspSum / size;
		float dmsAvg = this.dmsSum / size;
		float ionsAvg = this.ionsSum / size;
		float mpfAvg = this.mpfSum / size;
		float simAvg = this.simSum / size;

		float xcsd = (float) Math.pow((this.xcorr2Sum - this.xcorrSum
		        * this.xcorrSum / size)
		        / (size - 1), 0.5d);
		float dcnsd = (float) Math.pow((this.dcn2Sum - this.dcnSum
		        * this.dcnSum / size)
		        / (size - 1), 0.5d);
		float spsd = (float) Math.pow((this.sp2Sum - this.spSum * this.spSum
		        / size)
		        / (size - 1), 0.5d);
		float rspsd = (float) Math.pow((this.rsp2Sum - this.rspSum
		        * this.rspSum / size)
		        / (size - 1), 0.5d);
		float dmssd = (float) Math.pow((this.dms2Sum - this.dmsSum
		        * this.dmsSum / size)
		        / (size - 1), 0.5d);
		float ionssd = (float) Math.pow((this.ions2Sum - this.ionsSum
		        * this.ionsSum / size)
		        / (size - 1), 0.5d);
		float mpfsd = (float) Math.pow((this.mpf2Sum - this.mpfSum
		        * this.mpfSum / size)
		        / (size - 1), 0.5d);
		float simsd = (float) Math.pow((this.sim2Sum - this.simSum
		        * this.simSum / size)
		        / (size - 1), 0.5d);

		for (int i = 0; i < size; i++) {
			PepNorm pepnorm = list.get(i);
			pepnorm.xcn = (pepnorm.xcn - xcorrAvg) / xcsd;
			pepnorm.dcn = (pepnorm.dcn - dcnAvg) / dcnsd;
			pepnorm.spn = (pepnorm.spn - spAvg) / spsd;
			pepnorm.rspn = (pepnorm.rspn - rspAvg) / rspsd;
			pepnorm.dMS = (pepnorm.dMS - dmsAvg) / dmssd;
			pepnorm.ions = (pepnorm.ions - ionsAvg) / ionssd;
			pepnorm.MPF = (pepnorm.MPF - mpfAvg) / mpfsd;
			pepnorm.sim = (pepnorm.sim - simAvg) / simsd;
		}
	}
}
