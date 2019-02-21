/* 
 ******************************************************************************
 * File: DefaultMascotPhosPairCriteria.java * * * Created on 04-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IMascotPhosphoPeptidePair;

/**
 * The default MascotPhosphoPeptidePair criteria for the filtering of
 * MascotPhosphoPeptidePair
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 14:25:29
 */
public class DefaultMascotPhosPairCriteria implements
        IPhosPeptidePairCriteria<IMascotPhosphoPeptidePair> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private float min_ionscore_sum;
	private final String name = "MascotPhosPairCriteria";

	public DefaultMascotPhosPairCriteria(float min_ionscore_sum) {
		this.min_ionscore_sum = min_ionscore_sum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IMascotPhosphoPeptidePair pep) {
		return pep.setUsed(pep.getIonscore() >= this.min_ionscore_sum);
	}

	@Override
	public PeptideType getPeptideType() {
		return PeptideType.APIVASE_MASCOT;
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
		if(obj instanceof DefaultMascotPhosPairCriteria){
			DefaultMascotPhosPairCriteria c = (DefaultMascotPhosPairCriteria) obj;
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
	 * Always be true. Just use {@link #filter(IMascotPhosphoPeptidePair)}
	 */
//	@Override
//	public boolean preFilter(IMascotPhosphoPeptidePair pep) {
//		return true;
//	}

}
