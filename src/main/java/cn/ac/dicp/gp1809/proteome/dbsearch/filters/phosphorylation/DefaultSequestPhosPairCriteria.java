/* 
 ******************************************************************************
 * File: DefaultSequestPhosPairCriteria.java * * * Created on 04-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ISequestPhosphoPeptidePair;

/**
 * The default SequestPhosphoPeptidePair criteria for the filtering of
 * SequestPhosphoPeptidePair
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 14:25:29
 */
public class DefaultSequestPhosPairCriteria implements
        IPhosPeptidePairCriteria<ISequestPhosphoPeptidePair> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private float min_dcn;
	private float min_xcorr;
	private final String name = "SequestPhosPairCriteria";

	public DefaultSequestPhosPairCriteria(float min_xcorr, float min_dcn) {
		this.min_xcorr = min_xcorr;
		this.min_dcn = min_dcn;
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
		return pep.setUsed(pep.getXcorrSum() >= this.min_xcorr
		        && pep.getDeltaCn() >= this.min_dcn);
	}

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
		if(obj instanceof DefaultSequestPhosPairCriteria){
			DefaultSequestPhosPairCriteria c = (DefaultSequestPhosPairCriteria) obj;
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

	/**
	 * Always be true. Just use {@link #filter(ISequestPhosphoPeptidePair)}
	 */
//	@Override
//	public boolean preFilter(ISequestPhosphoPeptidePair pep) {
//		return true;
//	}

}
