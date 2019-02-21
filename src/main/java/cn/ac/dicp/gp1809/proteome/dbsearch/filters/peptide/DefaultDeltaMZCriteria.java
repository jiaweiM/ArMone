/*
 ******************************************************************************
 * File: DefaultDeltaMZCriteria.java * * * Created on 09-13-2010
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
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
 * The default m/z criteria for the filtering of generic peptide
 * 
 * @author Xinning
 * @version 0.1, 09-13-2010, 16:57:31
 */
public class DefaultDeltaMZCriteria implements IPeptideCriteria<IPeptide> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private double max_ppm = 0;
	private final String name = "DeltaMZCriteria";

	public DefaultDeltaMZCriteria(double max_ppm) {
		this.max_ppm = max_ppm;
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
		boolean used = pep.getAbsoluteDeltaMZppm() <= this.max_ppm;
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
		if(obj instanceof DefaultDeltaMZCriteria){
			DefaultDeltaMZCriteria c = (DefaultDeltaMZCriteria) obj;
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
