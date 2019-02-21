/*
 ******************************************************************************
 * File: PhosPairParameter.java * * * Created on 02-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.AbstractParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;

/**
 * The parameter for Phosphopeptide pair.
 * 
 * @author Xinning
 * @version 0.1.2, 06-13-2009, 21:13:54
 */
public abstract class PhosPairParameter extends AbstractParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Enzyme enzyme;
	private Aminoacids aas;
	private AminoacidModification aamodif;

	protected PhosPairParameter(Aminoacids aas, AminoacidModification aamodif,
	        Enzyme enzyme, String database, boolean isMonoMass) {

		if (this.testNull(aas))
			this.aas = aas;
		if (this.testNull(aamodif))
			this.aamodif = aamodif;
		if (this.testNull(enzyme))
			this.enzyme = enzyme;
		if (this.testNull(database))
			this.setDatabase(database);

		this.setPeptideMass(isMonoMass);
	}

	protected PhosPairParameter(Aminoacids aas, AminoacidModification aamodif,
	        Enzyme enzyme, IFastaAccesser accesser, boolean isMonoMass) {

		if (this.testNull(aas))
			this.aas = aas;
		if (this.testNull(aamodif))
			this.aamodif = aamodif;
		if (this.testNull(enzyme))
			this.enzyme = enzyme;
		if (this.testNull(accesser))
			this.setFastaAccesser(accesser);

		this.setPeptideMass(isMonoMass);
	}

	/*
	 * @Throw NullPointerException if the obj is null.
	 */
	private boolean testNull(Object obj) {
		if (obj == null) {
			throw new NullPointerException("The input object is null: "
			        + obj.getClass().getName());
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#getEnzyme()
	 */
	@Override
	public Enzyme getEnzyme() {
		return this.enzyme;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#getStaticInfo()
	 */
	@Override
	public Aminoacids getStaticInfo() {
		return this.aas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#getVariableInfo()
	 */
	@Override
	public AminoacidModification getVariableInfo() {
		return this.aamodif;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#setStaticInfo(cn
	 * .ac.dicp.gp1809.proteome.dbsearch.Aminoacids)
	 */
	@Override
	public void setStaticInfo(Aminoacids aas) {
		this.aas = aas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#setVariableInfo(
	 * cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification)
	 */
	@Override
	public void setVariableInfo(AminoacidModification aamodif) {
		this.aamodif = aamodif;
	}
	
	public void restore(HashMap <Character, Character> replaceAA){
		char [] cs = this.aas.getModifiedAAs();
		for(int i=0;i<cs.length;i++){
			char c = cs[i];
			if(replaceAA.containsKey(c)){
				double omass = Aminoacids.getAminoacid(c).getMonoMass();
				this.aas.setModification(c, omass);
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public PhosPairParameter clone() {
		try {
	        return (PhosPairParameter) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PhosPairParameter deepClone() {
		
		PhosPairParameter copy = this.clone();
		
		copy.aamodif = this.aamodif.deepClone();
		copy.aas = this.aas.deepClone();
		
		copy.enzyme = this.enzyme.deepClone();
		
		return copy;
	}
}
