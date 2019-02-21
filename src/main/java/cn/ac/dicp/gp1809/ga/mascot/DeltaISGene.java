/* 
 ******************************************************************************
 * File: DeltaISGene.java * * * Created on 2011-8-31
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
 * @version 2011-8-31, 13:30:19
 */
public class DeltaISGene extends MascotGene {

	/**
	 * @param config
	 */
	public DeltaISGene(MascotConfiguration config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.mascot.MascotGene#getActualLowerBound()
	 */
	@Override
	protected double getActualLowerBound() {
		// TODO Auto-generated method stub
		return this.getValueLimit().getDeltaISLowerlimit();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.mascot.MascotGene#getActualUpperBound()
	 */
	@Override
	protected double getActualUpperBound() {
		// TODO Auto-generated method stub
		return this.getValueLimit().getDeltaISUpperlimit();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.Gene#setLength()
	 */
	@Override
	public void setLength() {
		// TODO Auto-generated method stub
		super.setLength(((MascotConfiguration)this.getConfiguration()).getDeltaISGeneBit());
	}

}
