/*
 ******************************************************************************
 * File: DeltaMSGene.java * * * Created on 08-04-2010
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

/**
 * DeltaMS Gene
 * 
 * @author Xinning
 * @version 0.1, 08-04-2010, 10:46:30
 */
public class DeltaMSGene extends SequestGene {

	public DeltaMSGene(SequestConfiguration param) {
		super(param);
	}

	@Override
	protected double getActualUpperBound() {
		return this.getValueLimit().getDeltaMSUpperlimit();
	}


	@Override
	protected double getActualLowerBound() {
		return this.getValueLimit().getDeltaMSLowlimit();
	}

	@Override
	protected void setLength() {
		this.setLength(((SequestConfiguration)this.getConfiguration()).getDeltaMSGeneBit());
		
	}

}
