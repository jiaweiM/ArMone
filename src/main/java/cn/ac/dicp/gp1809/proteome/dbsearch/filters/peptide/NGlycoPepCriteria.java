/* 
 ******************************************************************************
 * File: NGlycoPepCriteria.java * * * Created on 2010-11-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * @author ck
 *
 * @version 2010-11-22, 16:07:14
 */
public class NGlycoPepCriteria implements IPeptideCriteria<IPeptide> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean glyco;
	private final Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
	private final Pattern Non_N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-Z][^A-Z]{0,2}[^ST]");
	private final String name = "GlycoCriteria";

	/**
	 * 
	 * @param glyco true = searching glyco peptide; false = searching non-glyco peptide
	 */
	public NGlycoPepCriteria(boolean glyco){
		this.glyco = glyco;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IPeptide pep) {
		// TODO Auto-generated method stub
		String seq = pep.getSequence();
		Matcher m1 = N_GLYCO.matcher(seq);
//		Matcher m2 = Non_N_GLYCO.matcher(seq);
		boolean find1 = m1.find();
//		boolean find2 = m2.find();
		if(find1){
//			if(find2)
//				return glyco ? false : true;
//			else
				return glyco ? true : false;
		}else{
			return glyco ? false : true;
		}
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
		return glyco;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof NGlycoPepCriteria){
			NGlycoPepCriteria c = (NGlycoPepCriteria) obj;
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
