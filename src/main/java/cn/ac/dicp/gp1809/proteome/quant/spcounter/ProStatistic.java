/*
 * *****************************************************************************
 * File: ProStatistic.java * * * Created on 12-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import java.util.HashMap;
import java.util.Map;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.IProteinGroupSimplifier;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.MostLocusSimplifier;
import cn.ac.dicp.gp1809.util.math.Statisticer;

/**
 * A protein instance for statistic. Commonly for statistic, there are more than
 * 3 replicates. This pro instance contains all the the information for
 * spcounter at different replicates.
 * 
 * @author Xinning
 * @version 0.1, 12-08-2008, 13:32:19
 */
public class ProStatistic {
	
	private static final IProteinGroupSimplifier SIMPLIFIER = new MostLocusSimplifier();
	private boolean isstatispep;
	// Name of the protein
	private String refname;

	private double mw;
	private double pi;

	// index of this pro, for output;
	private int idx;

	// The number of replicate in this experiment
	private int replicateNum;

	// The spectrum count of this pro statistic.
	private int[] spcount;

	private double RSD = -1d;

	private boolean isAvg = true;

	private double avg = -1d;

	private HashMap<String, PepStatistic> pepMap;

	/**
	 * Create a pro statistic instence without compution of pep replicate
	 * information
	 * 
	 * @param how
	 *            many replicates in this experiment.
	 */
	public ProStatistic(int replicateNum) {
		this(replicateNum, false);
	}

	/**
	 * Create a pro statistic instance with or without computation of pep
	 * replicate information
	 * 
	 * @param how
	 *            many replicates in this experiment.
	 * @param isstatispep
	 *            statistic peptide information ?
	 */
	public ProStatistic(int replicateNum, boolean isstatispep) {
		this.replicateNum = replicateNum;
		this.spcount = new int[replicateNum];
		this.isstatispep = isstatispep;
		this.pepMap = new HashMap<String, PepStatistic>();
	}

	/**
	 * Create a pro statistic instance with or without computation of pep
	 * replicate information
	 * 
	 * @param how
	 *            many replicates in this experiment.
	 * @param isstatispep
	 *            statistic peptide information ?
	 * @param isAvg,
	 *            the computation format, the count of spectra is computed in
	 *            average form (true, default) or median form (false).
	 */
	public ProStatistic(int replicateNum, boolean isstatispep, boolean isAvg) {
		this(replicateNum, isstatispep);
		this.isAvg = isAvg;
	}

	/**
	 * Set a same protein as this pro. The added protein commonly comes from
	 * different replicate.
	 * 
	 * @param protein
	 * @param curtReplicate
	 *            the current replicate number.
	 */
	public void set(Protein protein, int curtReplicate) {

		if (curtReplicate >= this.replicateNum)
			throw new RuntimeException("");

		// The first time
		if (this.refname == null) {
			IReferenceDetail [] refs = protein.getReferences();
			IReferenceDetail ref = (IReferenceDetail) SIMPLIFIER.simplify(refs);
			this.refname = ref.getName();
			this.mw = ref.getMW();
			this.pi = ref.getPI();
		}

		this.spcount[curtReplicate] = protein.getSpectrumCount();

		this.setPep(protein, curtReplicate);
	}

	/**
	 * Get the peptide map containing all information in this protein
	 */
	public Map<String, PepStatistic> getPepMap() {
		return this.pepMap;
	}

	/**
	 * @return the relative sd of the spectra count <b> Compute only once at the
	 *         first excution of this method</b>
	 */
	public double getCV() {
		if (this.RSD < 0)
			this.RSD = Statisticer.getCV(this.spcount);

		return this.RSD;
	}

	/**
	 * @return the count of spectra for this proStats identification. <b>This is
	 *         a statistic value, so it may be a double value. The computation
	 *         of this value is the average count of all spectra or the median
	 *         value and this computation form can be assigned in conductor of
	 *         this prostatistic</b>
	 */
	public double getCount() {
		if (this.avg < 0)
			this.avg = Statisticer.getStatisticValue(this.spcount, isAvg);

		return this.avg;
	}

	/**
	 * The number of unique peptide in this protein identification. While for
	 * all the replicates in experiment, the number of unique peptide in this
	 * prostats identification is that for protein identification in all
	 * replicates. e.g. in rep 1, for protein 1 there are 2 peptide , 1 and 2,
	 * for identification, and in rep 2, there are 3 pep for the same protein
	 * identification, peptide 1, 3, and 4, thus, the number of unique peptide
	 * for prostats identification is 4 (1,2,3 and 4).
	 */
	public int getUniquePepCount() {
		return pepMap.size();
	}

	public void setIndex(int idx) {
		this.idx = idx;
	}

	private String getInfor() {
		StringBuilder sb = new StringBuilder(500);

		for (int i = 0; i < this.replicateNum; i++) {
			sb.append(this.spcount[i]);
			sb.append("\t");
		}

		sb.append(this.getCount());
		sb.append("\t");
		sb.append(this.getCV());
		sb.append("\t");
		sb.append(this.getUniquePepCount());
		sb.append("\t");
		sb.append(this.refname);
		sb.append("\t");
		sb.append(this.mw);
		sb.append("\t");
		sb.append(this.pi);
		sb.append("\r\n");

		if (this.isstatispep) {
			PepStatistic[] peps = this.pepMap.values().toArray(
			        new PepStatistic[0]);
			// sort??

			for (int i = 0; i < peps.length; i++) {
				sb.append(peps[i].toString());
				sb.append("\r\n");
			}
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return "$$-" + this.idx + "\t" + getInfor();
	}

	/**
	 * @return the refname
	 */
	public String getRefname() {
		return refname;
	}

	/**
	 * @return the mw
	 */
	public double getMw() {
		return mw;
	}

	/**
	 * @return the pi
	 */
	public double getPi() {
		return pi;
	}

	/*
	 * Add peptide information to statistic.
	 */
	private void setPep(Protein protein, int curtReplicate) {
		UniquePeptide[] upeps = this.getUniquePep(protein);
		for (int i = 0; i < upeps.length; i++) {
			UniquePeptide upep = upeps[i];
			String useq = upep.sequence;
			PepStatistic pep = null;
			if ((pep = pepMap.get(useq)) != null) {
				pep.set(upep, curtReplicate);
			} else {
				pep = new PepStatistic(this.replicateNum, this.isAvg);
				pep.set(upep, curtReplicate);
				pepMap.put(useq, pep);
			}
		}
	}

	private UniquePeptide[] getUniquePep(Protein protein) {
		IPeptide[] peptides = protein.getAllPeptides();
		int len = peptides.length;
		Map<String, UniquePeptide> map = new HashMap<String, UniquePeptide>(
		        protein.getPeptideCount());

		for (int i = 0; i < len; i++) {
			IPeptide pep = peptides[i];
			String useq = pep.getSequence();
			UniquePeptide upep = null;
			if ((upep = map.get(useq)) != null) {
				upep.plus();
			} else {
				upep = new UniquePeptide(pep);
				map.put(useq, upep);
			}
		}

		return map.values().toArray(new UniquePeptide[0]);
	}

}
