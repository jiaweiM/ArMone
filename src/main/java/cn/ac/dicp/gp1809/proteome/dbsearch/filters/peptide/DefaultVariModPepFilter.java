/* 
 ******************************************************************************
 * File: DefaultVariModPepFilter.java * * * Created on 2011-10-12
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * @author ck
 *
 * @version 2011-10-12, 14:56:29
 */
public class DefaultVariModPepFilter implements IPeptideCriteria<IPeptide> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8771305333976383216L;
	
	private String name;
	private char symbol;
	private String site;
	
	public DefaultVariModPepFilter(String name, char symbol, String site){
		this.name = name;
		this.symbol = symbol;
		this.site = site;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IPeptide pep) {
		// TODO Auto-generated method stub

		IModifSite [] sites = pep.getPeptideSequence().getModifications();
		if(sites!=null){
			for(int i=0;i<sites.length;i++){
				char symbol = sites[i].symbol();
				String modsite = sites[i].modifiedAt().toString();
				if(symbol==this.symbol && modsite.equals(this.site)){
					return true;
				}
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#getFilterName()
	 */
	@Override
	public String getFilterName() {
		// TODO Auto-generated method stub
		return name;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		// TODO Auto-generated method stub
		return PeptideType.GENERIC;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof DefaultVariModPepFilter){
			DefaultVariModPepFilter c = (DefaultVariModPepFilter) obj;
			String n0 = this.name;
			String s0 = this.site;
			String n1 = c.name;
			String s1 = c.site;
			return (n0+s0).equals(n1+s1);
		}else{
			return false;
		}
	}

	@Override
	public int hashCode(){
		return (this.name+this.site).hashCode();
	}
}
