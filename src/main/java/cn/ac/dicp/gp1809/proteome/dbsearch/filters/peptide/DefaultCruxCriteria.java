/* 
 ******************************************************************************
 * File: DefaultCruxCriteria.java * * * Created on 04-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * The default crux criteria for the filtering of crux peptide
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 14:25:29
 */
public class DefaultCruxCriteria implements IPeptideCriteria<ICruxPeptide> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private float pvalue;
	private float percolator_score;
	private float qvalue;
	private final String name = "CruxCriteria";

	public DefaultCruxCriteria(float pvalue, float percolator_score,
	        float qvalue) {
		this.percolator_score = percolator_score;
		this.pvalue = pvalue;
		this.qvalue = qvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(ICruxPeptide pep) {

		boolean used = pep.getPValue() >= this.pvalue
		        && pep.getPercolator_score() >= this.percolator_score
		        && pep.getQValue() <= this.qvalue;
		return pep.setUsed(used);
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
		return PeptideType.CRUX;
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
		if(obj instanceof DefaultCruxCriteria){
			DefaultCruxCriteria c = (DefaultCruxCriteria) obj;
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
	 * Always be true. Just use {@link #filter(ICruxPeptide)}
	 */
//	@Override
//	public boolean preFilter(ICruxPeptide pep) {
//		return true;
//	}

}
