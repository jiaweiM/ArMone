/* 
 ******************************************************************************
 * File: NoVairModFilter.java * * * Created on 2011-10-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * @author ck
 *
 * @version 2011-10-17, 09:24:10
 */
public class NoVairModFilter implements IPeptideCriteria<IPeptide> {

	private static String name = "NoVariMod";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2770522213338273373L;
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IPeptide pep) {
		// TODO Auto-generated method stub
		String seq = PeptideUtil.getSequence(pep.getSequence());
		char [] cs =seq.toCharArray();
		for(int i=0;i<cs.length;i++){
			if(cs[i]<'A' || cs[i]>'Z')
				return false;
		}
		return true;
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
		if(obj instanceof NoVairModFilter){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public int hashCode(){
		return name.hashCode();
	}

}
