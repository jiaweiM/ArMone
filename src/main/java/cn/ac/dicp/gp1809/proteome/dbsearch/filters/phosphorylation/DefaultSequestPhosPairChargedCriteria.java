/* 
 ******************************************************************************
 * File: DefaultSequestPhosPairChargedCriteria.java * * * Created on 06-30-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.phosphorylation;

import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ISequestPhosphoPeptidePair;

/**
 * The default sequest criteria for the filtering of sequest apivase peptide
 * 
 * @author Xinning
 * @version 0.1, 06-30-2009, 14:44:51
 */
public class DefaultSequestPhosPairChargedCriteria implements
IPhosPeptidePairCriteria<ISequestPhosphoPeptidePair> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private short maxcharge;
	private float[] xcorrs;
	private float deltaCns;
	private final String name = "SequestPhosPairChargedCriteria";

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
	public DefaultSequestPhosPairChargedCriteria(float[] xcorrs, float deltaCns) {
		this.xcorrs = new float[10];

		Arrays.fill(this.xcorrs, Float.MAX_VALUE);

		if (xcorrs != null)
			System.arraycopy(xcorrs, 0, this.xcorrs, 0, xcorrs.length);
		
		this.deltaCns = deltaCns;

		this.maxcharge = 9;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(ISequestPhosphoPeptidePair pep) {
		int charge = pep.getCharge();

		if (charge <= this.maxcharge && pep.getXcorrSum() >= this.xcorrs[charge]
		        && pep.getDeltaCn() >= this.deltaCns) {
			return pep.setUsed(true);
		}

		return pep.setUsed(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#getPeptideType
	 * ()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.APIVASE_SEQUEST;
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
		if(obj instanceof DefaultSequestPhosPairChargedCriteria){
			DefaultSequestPhosPairChargedCriteria c = (DefaultSequestPhosPairChargedCriteria) obj;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#preFilter
	 * (cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
//	@Override
//	public boolean preFilter(ISequestPeptide pep) {
//		return true;
//	}

}
