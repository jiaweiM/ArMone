/* 
 ******************************************************************************
 * File: PhosPepCriteria.java * * * Created on 2010-11-22
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
 * @version 2010-11-22, 16:12:59
 */
public class PhosPepCriteria implements IPeptideCriteria<IPeptide> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private char[] symbols;
	private boolean phos;
	private final String name = "PhosCriteria";
	
	public PhosPepCriteria(boolean phos, char[] symbols){
		this.symbols = symbols;
		this.phos = phos;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IPeptide pep) {
		// TODO Auto-generated method stub
		if (pep.getPeptideSequence()
		        .getModificationNumber() == 0)
			return this.phos? false : true;

		IModifSite[] sites = pep.getPeptideSequence()
		        .getModifications();
		for (IModifSite site : sites) {
			for (char symbol : symbols)
				if (site.symbol() == symbol)
					return this.phos ? true : false;
		}

		return this.phos? false : true;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		// TODO Auto-generated method stub
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
	
	public boolean getUse(){
		return phos;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof PhosPepCriteria){
			PhosPepCriteria c = (PhosPepCriteria) obj;
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
