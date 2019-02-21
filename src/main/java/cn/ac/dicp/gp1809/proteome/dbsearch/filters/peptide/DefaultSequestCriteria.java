/* 
 ******************************************************************************
 * File: DefaultSequestCriteria.java * * * Created on 04-12-2009
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
 * The default sequest criteria for the filtering of mascot peptide
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 14:25:29
 */
public class DefaultSequestCriteria implements
        IPeptideCriteria<ISequestPeptide> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private short maxcharge;
	private float[] xcorrs;
	private float[] deltaCns;
	private final String name = "SequestCriteria";
	
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
	public DefaultSequestCriteria(float[] xcorrs, float[] deltaCns) {
		this.xcorrs = new float[10];
		this.deltaCns = new float[10];

		Arrays.fill(this.xcorrs, Float.MAX_VALUE);
		Arrays.fill(this.deltaCns, 2f);

		if (xcorrs != null)
			System.arraycopy(xcorrs, 0, this.xcorrs, 0, xcorrs.length);

		if (deltaCns != null)
			System.arraycopy(deltaCns, 0, this.deltaCns, 0, deltaCns.length);

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
	public boolean filter(ISequestPeptide pep) {
		int charge = pep.getCharge();

		if (charge <= this.maxcharge && pep.getXcorr() >= this.xcorrs[charge]
		        && pep.getDeltaCn() >= this.deltaCns[charge]) {
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
		if(obj instanceof DefaultSequestCriteria){
			DefaultSequestCriteria c = (DefaultSequestCriteria) obj;
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
