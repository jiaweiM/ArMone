/*
 ******************************************************************************
 * File: DefaultProbabilityCriteria.java * * * Created on 08-08-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * The default probability criteria for the filtering of generic peptide
 * 
 * @author Xinning
 * @version 0.1, 08-08-2009, 12:25:46
 */
public class DefaultProbabilityCriteria implements IPeptideCriteria<IPeptide> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private float minprob = 0;
	private final String name = "OMSSACriteria";

	public DefaultProbabilityCriteria(float minprob) {
		this.minprob = minprob;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IPeptide pep) {
		
		boolean used = pep.getProbabilty() >= this.minprob;
		
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
		return PeptideType.GENERIC;
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
		if(obj instanceof DefaultProbabilityCriteria){
			DefaultProbabilityCriteria c = (DefaultProbabilityCriteria) obj;
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
