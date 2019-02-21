/* 
 ******************************************************************************
 * File: SFOERSequestCriteria.java * * * Created on 08-07-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * 
 * @author Xinning
 * @version 0.1.1, 07-28-2010, 19:25:29
 */
public class SFOERSequestCriteria implements IPeptideCriteria<ISequestPeptide> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private short maxcharge;
	private float[] xcorrs;
	private float[] deltaCns;
	private float[] sps;
	private short[] rsps;
	private float[] deltaMSppms;
	
	private final String name = "SFOERSequestCriteria";

	/**
	 * for convient, the index of each filter is the charge state. For example,
	 * if the filter is 2, 2.5, 3.8 for charge state of 1+, 2+ and 3+, the xcorr
	 * filter array should be float[]{0, 2, 2.5, 3.8} with length of 4. Peptides
	 * with other charge states will be removed.
	 * 
	 * @param xcorrs
	 *            use charge as index
	 * @param deltaCns
	 *            use charge as index
	 */
	public SFOERSequestCriteria(float[] xcorrs, float[] deltaCns, float[] sps,
	        short[] rsps) {
		this.maxcharge = 9;

		this.xcorrs = new float[maxcharge + 1];
		this.deltaCns = new float[maxcharge + 1];
		this.sps = new float[maxcharge + 1];
		this.rsps = new short[maxcharge + 1];

		Arrays.fill(this.rsps, (short) 500);

		if (xcorrs != null && xcorrs.length != 0) {
			int len = xcorrs.length;
			System.arraycopy(xcorrs, 0, this.xcorrs, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = xcorrs[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.xcorrs[i] = value;
			}
		}

		if (deltaCns != null && deltaCns.length != 0) {
			int len = deltaCns.length;
			System.arraycopy(deltaCns, 0, this.deltaCns, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = deltaCns[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.deltaCns[i] = value;
			}
		}

		if (sps != null && sps.length != 0) {
			int len = sps.length;
			System.arraycopy(sps, 0, this.sps, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = sps[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.sps[i] = value;
			}
		}

		if (rsps != null && rsps.length != 0) {
			int len = rsps.length;
			System.arraycopy(rsps, 0, this.rsps, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			short value = rsps[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.rsps[i] = value;
			}
		}
/*		
		if (deltaMSppms != null && deltaMSppms.length != 0) {
			int len = deltaMSppms.length;
			System.arraycopy(deltaMSppms, 0, this.deltaMSppms, 1, len);

			float value = deltaMSppms[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.deltaMSppms[i] = value;
			}
		}
*/	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(ISequestPeptide pep) {
		int charge = pep.getCharge();

		if (charge <= this.maxcharge && pep.getXcorr() >= this.xcorrs[charge]
		        && pep.getDeltaCn() >= this.deltaCns[charge]
		        && pep.getSp() >= this.sps[charge]
		        && pep.getRsp() <= this.rsps[charge] 
				){
			
			return pep.setUsed(true);
		}

		return pep.setUsed(false);
	}

	@Override
	public PeptideType getPeptideType() {
		return PeptideType.SEQUEST;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#getFilterName()
	 */
	@Override
	public String getFilterName() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof SFOERSequestCriteria){
			SFOERSequestCriteria c = (SFOERSequestCriteria) obj;
			String n0 = this.name;
			String n1 = c.name;
			return n0.equals(n1);
		}else{
			return false;
		}
	}

	@Override
	public int hashCode(){
		return this.name.hashCode();
	}

}
