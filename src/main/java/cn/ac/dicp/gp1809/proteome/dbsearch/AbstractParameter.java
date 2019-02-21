/* 
 ******************************************************************************
 * File: AbstractParameter.java * * * Created on 09-16-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import cn.ac.dicp.gp1809.exceptions.MyNullPointerException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;

/**
 * Abstract search parameter
 * 
 * @author Xinning
 * @version 0.2.2, 03-04-2009, 16:57:27
 */
public abstract class AbstractParameter implements ISearchParameter {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	private transient IFastaAccesser accesser;

	private String fastadb;

	private boolean isMonoMass;
	
	private LabelType labelType;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch
	 * .ISearchParameter#getFastaAccesser()
	 */
	@Override
	public IFastaAccesser getFastaAccesser(IDecoyReferenceJudger judger) {
		if (this.accesser == null) {
			try {
				String db = this.getDatabase();
				if(db != null && db.length() >0)
					this.accesser = new FastaAccesser(this.getDatabase(), judger);
			} catch (Exception e) {
				throw new MyNullPointerException(e);
			}
		}

		return this.accesser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter
	 * #setFastaAccesser
	 * (cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser)
	 */
	public void setFastaAccesser(IFastaAccesser accesser) {
		this.accesser = accesser;

		if (accesser != null) {
			this.fastadb = accesser.getFastaFile().getPath();
			System.out.println("Reset the sequence database: \"" + this.fastadb
			        + "\"");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch
	 * .ISearchParameter#setDatabase(java.lang.String)
	 */
	@Override
	public void setDatabase(String database) {
		if (database == null || !database.equals(this.fastadb)) {
			this.fastadb = database;
			this.setFastaAccesser(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.IParameter#getDatabase()
	 */
	@Override
	public String getDatabase() {
		return this.fastadb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#isMonoPeptideMass()
	 */
	@Override
	public boolean isMonoPeptideMass() {
		return this.isMonoMass;
	}

	
	/**
	 * Set the used peptide mass type.
	 * 
	 * @param isMonoPeptideMass
	 */
	protected void setPeptideMass(boolean isMonoPeptideMass) {
		this.isMonoMass = isMonoPeptideMass;
	}
	
	public void setLabelType(LabelType labelType){
		this.labelType = labelType;
/*		
		short [] used = labelType.getUsed();
		char [] symbols = labelType.getSymbols();
		double [] mass = labelType.getMass();
		AminoacidModification aamodif = this.getVariableInfo();
		Modif[] mods = aamodif.getModifications();
		for(int i=0;i<used.length;i++){
			int m = (int) mass[i];
			for(int j=0;j<mods.length;j++){
				int n = (int) mods[j].getMass();
				char s = mods[j].getSymbol();
				if(m==n){
					used[i] = 1;
					symbols[i] = s;
				}
			}
		}
*/		
	}
	
	public LabelType getLabelType(){
		return labelType;
	}

}
