/* 
 ******************************************************************************
 * File: IonScoreGene.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.mascot;

/**
 * @author ck
 *
 * @version 2011-8-31, 13:30:42
 */
public class IonScoreGene extends MascotGene {

	/**
	 * @param config
	 */
	public IonScoreGene(MascotConfiguration config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.mascot.MascotGene#getActualLowerBound()
	 */
	@Override
	public double getActualLowerBound() {
		// TODO Auto-generated method stub
		return this.getValueLimit().getIonScoreLowerlimit();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.mascot.MascotGene#getActualUpperBound()
	 */
	@Override
	public double getActualUpperBound() {
		// TODO Auto-generated method stub
		return this.getValueLimit().getIonScoreUpperlimit();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.Gene#setLength()
	 */
	@Override
	public void setLength() {
		// TODO Auto-generated method stub
		super.setLength(((MascotConfiguration)this.getConfiguration()).getIonScoreGeneBit());
	}

}
