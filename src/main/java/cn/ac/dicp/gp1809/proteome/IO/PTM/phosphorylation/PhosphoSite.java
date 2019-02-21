/* 
 ******************************************************************************
 * File: PhosphoSite.java * * * Created on 02-17-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.PTM.ModifSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * The phosphorylated site
 * 
 * @author Xinning
 * @version 0.1.0.1, 06-09-2009, 21:09:26
 */
public class PhosphoSite extends ModifSite implements IPhosphoSite {

	//Is this site has neutral loss
	private boolean isNeutralLoss;
	//The tscore by APIVASE
	private float tscore;

	/**
	 * A phosphorylation site
	 * 
	 * @param aa
	 *            the aminoacid
	 * @param loc
	 *            the localization (from 1- n)
	 * @param isNeutralLoss
	 *            in MS3, if this site loses the phosphate
	 */
	public PhosphoSite(ModSite site, int loc, char symbol, boolean isNeutralLoss) {
		super(site, loc, symbol);
		this.isNeutralLoss = isNeutralLoss;
	}

	/**
	 * A phosphorylation site
	 * 
	 * @param aa
	 *            the aminoacid
	 * @param loc
	 *            the localization
	 * @param isNeutralLoss
	 *            in MS3, if this site loses the phosphate. In MS2, this value
	 *            always be false
	 */
	public PhosphoSite(ModSite site, int loc, char symbol) {
		super(site, loc, symbol);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite#isNeutralLoss
	 * ()
	 */
	@Override
	public boolean isNeutralLoss() {
		return isNeutralLoss;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite#setNeutralLoss
	 * (boolean)
	 */
	@Override
	public void setNeutralLoss(boolean isNeutralLoss) {
		this.isNeutralLoss = isNeutralLoss;
	}

	/**
	 * Return the tscore if this value has been calculated. Otherwise, return 0;
	 * 
	 * @return
	 */
	public float getTscore() {
		return this.tscore;
	}

	/**
	 * Set the tscore. (must >= 0)
	 */
	public void setTscore(float tscore) {

		if (tscore < 0f)
			this.tscore = 0f;
		else
			this.tscore = tscore;
	}

	@Override
	public String toString() {
		return super.toString() + ", "
		        + (this.isNeutralLoss ? "NeutralLost" : "");
	}
	
	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.PTM.ModifSite#deepClone()
	 */
	@Override
	public PhosphoSite deepClone() {
		return (PhosphoSite) super.deepClone();
	}
}
