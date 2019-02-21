/*
 * *****************************************************************************
 * File: UniquePeptide.java * * * Created on 12-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import java.util.Set;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;

/**
 * For computation of the count of spectra for this unique peptide identification
 * 
 * @author Xinning
 * @version 0.1, 12-08-2008, 13:31:56
 */
public class UniquePeptide {
	
	private int count;

	//Name of the peptide
	String sequence;
	String ref;
	
	double mw;
	double pi;
	
	
	public UniquePeptide(IPeptide pep){
		this.sequence = pep.getPeptideSequence().getSequence();//PeptideUtil.getUniqueSequenceWithTermine(pep.getSequence());
		String seq = PeptideUtil.getUniqueSequence(sequence);
		this.mw = new MwCalculator().getMonoIsotopeMh(seq);
		this.pi = pep.getPI();
		
		Set <ProteinReference> reference = pep.getProteinReferences();
		
		ref = new String();
		for(ProteinReference rf:reference){
			String str = rf.toString();
			ref +=str;
			ref +="$";
		}
		
		this.plus();
	}
	
	/**
	 * Equals ++;
	 * When this method is excuted, one spectrum count is added to the unqiue peptide
	 */
	public void plus(){
		this.count ++;
	}
	
	/**
	 * @return the number of spectrum for this peptide identification
	 */
	public int getCount(){
		return this.count;
	}
}
