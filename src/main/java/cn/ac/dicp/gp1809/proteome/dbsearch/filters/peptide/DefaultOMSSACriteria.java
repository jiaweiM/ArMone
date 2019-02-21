/* 
 ******************************************************************************
 * File: DefaultOMSSACriteria.java * * * Created on 04-29-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.IOMSSAPeptide;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * The default omssa criteria for the filtering of omssa peptide
 * 
 * @author Xinning
 * @version 0.1, 04-29-2009, 14:17:31
 */
public class DefaultOMSSACriteria implements IPeptideCriteria<IOMSSAPeptide> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private double max_evalue = Double.MAX_VALUE;
	private double max_pvalue = Double.MAX_VALUE;
	
	private final String name = "OMSSACriteria";

	public DefaultOMSSACriteria(double max_evalue) {
		this.max_evalue = max_evalue;
	}

	public DefaultOMSSACriteria(double max_evalue, double max_pvalue) {
		this.max_pvalue = max_pvalue;
		this.max_evalue = max_evalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IOMSSAPeptide pep) {
		return pep.getEvalue() <= this.max_evalue
		        && pep.getPvalue() <= this.max_pvalue;
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
		return PeptideType.OMSSA;
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
		if(obj instanceof DefaultOMSSACriteria){
			DefaultOMSSACriteria c = (DefaultOMSSACriteria) obj;
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
	 * Always be true. Just use {@link #filter(IMascotPeptide)}
	 */
//	@Override
//	public boolean preFilter(IOMSSAPeptide pep) {
//		return true;
//	}

}
