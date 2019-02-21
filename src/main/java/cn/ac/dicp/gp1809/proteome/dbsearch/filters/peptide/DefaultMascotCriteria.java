/* 
 ******************************************************************************
 * File: DefaultMascotCriteria.java * * * Created on 04-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * The default mascot criteria for the filtering of mascot peptide
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 14:25:29
 */
public class DefaultMascotCriteria implements IPeptideCriteria<IMascotPeptide> {

	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private float min_ionscore;
	private double pep_expect;
	private float deltaIS;
	private float mht;
	private float mit;
	private float pValue;
// use[0]=ion score; use[1]=MHT; use[2]=MIT; use[3]=except; use[4]=delta ion score
	private boolean [] use;
	
	private final String name = "MascotCriteria";

	public DefaultMascotCriteria(float min_ionscore) {
		this.min_ionscore = min_ionscore;
		this.use = new boolean[5];
		use[0] = true;
		use[1] = false;
		use[2] = false;
		use[3] = false;
		use[4] = false;
	}
	
	public DefaultMascotCriteria(float min_ionscore, double pep_expect) {
		this.min_ionscore = min_ionscore;
		this.pep_expect = pep_expect;
		this.use = new boolean[5];
		use[0] = true;
		use[1] = false;
		use[2] = false;
		use[3] = true;
		use[4] = false;
	}
	
	public DefaultMascotCriteria(float min_ionscore, double pep_expect, float deltaIS, float mht, 
			float mit) {
		
		this.min_ionscore = min_ionscore;
		this.pep_expect = pep_expect;
		this.deltaIS = deltaIS;
		this.mht = mht;
		this.mit = mit;
		this.use = new boolean[5];
		Arrays.fill(use, true);
	}
	
	public DefaultMascotCriteria(float min_ionscore, double pep_expect, float deltaIS, float mht, 
			float mit, boolean [] use){
		
		this.min_ionscore = min_ionscore;
		this.pep_expect = pep_expect;
		this.deltaIS = deltaIS;
		this.mht = mht;
		this.mit = mit;
		
		this.use = use;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IMascotPeptide pep) {
		
		boolean use = true;
		if(this.use[0])
			if(pep.getIonscore() < this.min_ionscore){
				use = false;
			}
		
		if(this.use[3])
			if(pep.getEvalue() > pep_expect){
				use = false;
			}
		
		if(this.use[4])
			if(pep.getDeltaS()<this.deltaIS){
				use = false;
			}
		
		if(this.use[1])
			if((pep.getIonscore()-pep.getHomoThres())<this.mht){
				use = false;
			}
		
		if(this.use[2])
			if((pep.getIonscore()-pep.getIdenThres()<this.mit)){
				use = false;
			}
		
		return pep.setUsed(use);
	}

	@Override
	public PeptideType getPeptideType() {
		return PeptideType.MASCOT;
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
		if(obj instanceof DefaultMascotCriteria){
			DefaultMascotCriteria c = (DefaultMascotCriteria) obj;
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
//	public boolean preFilter(IMascotPeptide pep) {
//		return true;
//	}

}
