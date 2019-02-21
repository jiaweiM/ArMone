/* 
 ******************************************************************************
 * File: EValueGene.java * * * Created on 2011-9-2
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
 * @version 2011-9-2, 13:58:23
 */
public class EValueGene extends MascotGene {

	/**
	 * @param config
	 */
	public EValueGene(MascotConfiguration config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.mascot.MascotGene#getActualLowerBound()
	 */
	@Override
	protected double getActualLowerBound() {
		// TODO Auto-generated method stub
		return this.getValueLimit().getEValueLowerlimit();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.mascot.MascotGene#getActualUpperBound()
	 */
	@Override
	protected double getActualUpperBound() {
		// TODO Auto-generated method stub
		return this.getValueLimit().getEValueUpperlimit();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.Gene#setLength()
	 */
	@Override
	protected void setLength() {
		// TODO Auto-generated method stub
		super.setLength(((MascotConfiguration)this.getConfiguration()).getEvalueGeneBit());
	}

}
