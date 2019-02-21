/* 
 ******************************************************************************
 * File: MascotGene.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.mascot;

import cn.ac.dicp.gp1809.ga.Gene;

/**
 * @author ck
 *
 * @version 2011-8-31, 14:12:53
 */
public abstract class MascotGene extends Gene {

	//need not to be clone, all use the same instence;
	private final MascotValueLimit vlimit;
	
	/**
	 * Max value of this gene. When decoding gene to actual value, this max bound must be computed;
	 * this value equals 2^genelength;
	 * The observed value of the random sequest gene is ranged from 0-maxbitvalue.
	 */
	private final double maxBitValue;
	
	private MascotConfiguration sconfig;
	
	/**
	 * @param param
	 */
	public MascotGene(final MascotConfiguration config) {
		super(config);
		// TODO Auto-generated constructor stub
		this.sconfig = config;
		this.vlimit = config.getMascotValueLimit();
		
		this.maxBitValue = Math.pow(2d,this.length());
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.Gene#encode()
	 */
	@Override
	protected String encode() {
		String geneString = this.getRandomGenerator().generateBinString(this.length());
		return geneString;
	}

	public MascotValueLimit getValueLimit(){
		return this.vlimit;
	}
	
	protected MascotConfiguration getMascotConfiguration(){
		return this.sconfig;
	}
	

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.Gene#value()
	 */
	@Override
	public double value() {
		// TODO Auto-generated method stub
		double obvalue = Integer.parseInt(this.genestring,2);
		double acvalue = this.getActualLowerBound()+
		(getActualUpperBound()-getActualLowerBound())*obvalue/this.getMaxBitValue();
		
		return acvalue;
	}

	/**
	 * @return
	 */
	public final double getMaxBitValue() {
		// TODO Auto-generated method stub
		return this.maxBitValue;
	}

	/**
	 * @return
	 */
	protected abstract double getActualUpperBound();

	/**
	 * @return
	 */
	protected abstract double getActualLowerBound();

}
