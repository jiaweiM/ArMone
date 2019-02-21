/* 
 ******************************************************************************
 * File: DefaultDecoyPeptideFilter.java * * * Created on 09-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
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
 * Remove the decoy peptides from the peptide list
 * 
 * @author Xinning
 * @version 0.1, 09-02-2009, 21:07:08
 */
public class DefaultDecoyPeptideFilter implements IPeptideCriteria<IPeptide> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	
	private final String name = "DecoyCriteria";

	/**
     * 
     * 
     */
	public DefaultDecoyPeptideFilter() {
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

		if (pep.isTP())
			return pep.setUsed(true);

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
		if(obj instanceof DefaultDecoyPeptideFilter){
			DefaultDecoyPeptideFilter c = (DefaultDecoyPeptideFilter) obj;
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
