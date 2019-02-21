/* 
 ******************************************************************************
 * File: DefaultXTandemCriteria.java * * * Created on 04-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.IXTandemPeptide;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * The default X!Tandem criteria for the filtering of X!Tandem peptide
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 14:25:29
 */
public class DefaultXTandemCriteria implements
        IPeptideCriteria<IXTandemPeptide> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private double max_evalue = Double.MAX_VALUE;
	private float min_hyperscore = -Float.MAX_VALUE;
	private final String name = "XTandemCriteria";
	
	public DefaultXTandemCriteria(double max_evalue) {
		this.max_evalue = max_evalue;
	}

	public DefaultXTandemCriteria(double max_evalue, float min_hyperscore) {
		this.max_evalue = max_evalue;
		this.min_hyperscore = min_hyperscore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IXTandemPeptide pep) {
		boolean used = pep.getHyperscore() >= this.min_hyperscore
		        && pep.getEvalue() <= this.max_evalue;
		
		return pep.setUsed(used);
	}

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.XTANDEM;
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
		if(obj instanceof DefaultXTandemCriteria){
			DefaultXTandemCriteria c = (DefaultXTandemCriteria) obj;
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
	 * Always be true. Just use {@link #filter(IXTandemPeptide)}
	 */
//	@Override
//	public boolean preFilter(IXTandemPeptide pep) {
//		return true;
//	}

}
