/* 
 ******************************************************************************
 * File: MascotChromosome.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.mascot;

import cn.ac.dicp.gp1809.ga.Chromosome;
import cn.ac.dicp.gp1809.ga.Gene;

/**
 * @author ck
 *
 * @version 2011-8-31, 14:02:13
 */
public class MascotChromosome extends Chromosome {

	private double[] nullchromosomevalues;
	
	/**
	 * @param a_configuration
	 * @param gene
	 * @param size
	 */
	public MascotChromosome(MascotConfiguration a_configuration, int size) {
		super(a_configuration, size);
		// TODO Auto-generated constructor stub
		this.nullchromosomevalues = a_configuration.getNullChromosomeValues()
        	.clone();
	}
	
	/**
	 * @param a_configuration
	 * @param gene
	 * @param size
	 */
	public MascotChromosome(MascotConfiguration a_configuration, Gene gene, int size) {
		super(a_configuration, gene, size);
		// TODO Auto-generated constructor stub
		this.nullchromosomevalues = a_configuration.getNullChromosomeValues()
        	.clone();
	}

	@Override
	public double[] values() {
		int len = this.size();
		double[] values = this.nullchromosomevalues.clone();
		for (int i = 0; i < len; i++) {
			Gene temp = this.getGene(i);
			if (temp != null)
				values[i] = temp.value();
		}

		return values;
	}
	
}
