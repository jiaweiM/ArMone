/*
 ******************************************************************************
 * File: PhosSequestScanDta.java * * * Created on 07-15-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossTest.NeutralInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * inherits DtaFile. Add method for easy generation of whether there is neutral
 * loss peak in the dta file.
 * 
 * @author Xinning
 * @version 0.2.1, 03-27-2009, 15:13:52
 */
public class NeutralLossSequestScanDta extends SequestScanDta {

	private boolean hasNeutralLoss = false;
	//The top n for the neutral loss peak
	private int topn = 0;

	//The neutral loss peak
	private IPeak neutralPeak;
	//The threshold for spectrum
	private ISpectrumThreshold threshold;
	//The lost mass for neutral loss 
	private double lostmass;

	/**
	 * 
	 * @param scanDta
	 * @param threshold
	 *            the threshold for spectrum
	 * @param lostmass
	 *            the lost mass when the neutral loss occurred (e.g. for
	 *            phosphate, this value is 98)
	 */
	public NeutralLossSequestScanDta(SequestScanDta scanDta,
	        ISpectrumThreshold threshold, double lostmass) {
		super(scanDta.getFile(), (SequestScanName) scanDta.getScanName(),
		        scanDta.getPeakList());
		this.threshold = threshold;
		this.lostmass = lostmass;
		this.testNeutralLoss(scanDta.getCharge());
	}

	public NeutralLossSequestScanDta(File dtafile, SequestScanName scanName,
	       MS2PeakList peaks, ISpectrumThreshold threshold,
	        double lostmass) {
		super(dtafile, scanName, peaks);

		this.threshold = threshold;
		this.lostmass = lostmass;
		this.testNeutralLoss(scanName.getCharge());
	}

	/**
	 * The neutral loss peak is the top n peak in the peak list. Return 0 if
	 * there is no neutral loss peak within the threshold.
	 * 
	 * @return
	 */
	public int neutralTopN() {
		return this.topn;
	}

	/**
	 * Whether there is neural loss peak
	 * 
	 * @return
	 */
	public boolean isNeutralLoss() {
		return this.hasNeutralLoss;
	}

	/**
	 * If this PeakList contains neutral loss peaks, return this peak.
	 * Otherwise, return null.(commonly invoke {@link #isNeutralLoss()} first).
	 * 
	 * @return the peak
	 */
	public IPeak getNeutralPeak() {
		return this.neutralPeak;
	}

	/**
	 * Whether there is neutralloss;
	 */
	private void testNeutralLoss(short charge) {
		IMS2PeakList peaklist = this.getPeakList();

		NeutralInfo info = NeutralLossTest.testNeutralLoss(peaklist, charge,
		        threshold, lostmass);
		this.hasNeutralLoss = info.isNeutralLoss();

		if (this.hasNeutralLoss) {
			this.topn = info.getTopn();
			this.neutralPeak = info.getNeutralPeak();
		}
	}
}
