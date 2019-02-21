/*
 ******************************************************************************
 * File: SequestChromosome.java * * * Created on 09-14-2010
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

import cn.ac.dicp.gp1809.ga.Chromosome;
import cn.ac.dicp.gp1809.ga.Gene;

/**
 * 继承自chromosome，没有增加任何方法，该类采用Xcorr deltaCn Sp ion等等的顺序规则, Rsp 必须位于最后。
 * 若要增加新的基因，则位于Rsp之前。 即，染色体中的基因采用上述规律排列,当有一个或多个上述基因未使用时，使用null代替，而规则不变
 * 即，染色体中的的总基因数不变,允许使用空基因
 * 
 * @author Xinning
 * @version 0.1, 09-14-2010, 20:07:02
 */
public class SequestChromosome extends Chromosome {

	private double[] nullchromosomevalues;

	public SequestChromosome(SequestConfiguration a_configuration,
	        int a_desiredSize) {
		super(a_configuration, a_desiredSize);
		this.nullchromosomevalues = a_configuration.getNullChromosomeValues()
		        .clone();
	}

	public SequestChromosome(SequestConfiguration a_configuration,
	        Gene a_sampleGene, int a_desiredSize) {
		super(a_configuration, a_sampleGene, a_desiredSize);
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
