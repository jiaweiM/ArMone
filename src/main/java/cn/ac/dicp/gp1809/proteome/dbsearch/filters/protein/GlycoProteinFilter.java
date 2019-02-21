/* 
 ******************************************************************************
 * File: GlycoProteinFilter.java * * * Created on 2010-11-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.protein;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;

/**
 * @author ck
 *
 * @version 2010-11-22, 15:58:39
 */
public class GlycoProteinFilter implements IProteinCriteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NGlycoPepCriteria pepFilter;
	private boolean glyco;
	
	public GlycoProteinFilter(boolean glyco){
		this.glyco = glyco;
		this.pepFilter = new NGlycoPepCriteria(glyco);
	}

	public GlycoProteinFilter(NGlycoPepCriteria glycoCriteria){
		this.glyco = glycoCriteria.getUse();
		this.pepFilter = glycoCriteria;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria#filter(cn.ac.dicp.gp1809.proteome.IO.proteome.Protein)
	 */
	@Override
	public boolean filter(Protein protein) {
		// TODO Auto-generated method stub
		IPeptide [] peps = protein.getAllPeptides();
		if(glyco){
			for(int i=0;i<peps.length;i++){
				if(pepFilter.filter(peps[i]))
					return true;
			}
			return false;
		}else{
			for(int i=0;i<peps.length;i++){
				if(!pepFilter.filter(peps[i]))
					return false;
			}
			return true;
		}
	}

}
