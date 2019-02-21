/* 
 ******************************************************************************
 * File: SequestPhosPairParameter.java * * * Created on 04-30-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;

/**
 * The sequest PhosPair Parameter
 * 
 * @author Xinning
 * @version 0.1, 04-30-2009, 13:47:25
 */
public class SequestPhosPairParameter extends PhosPairParameter {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	/**
	 * @param aas
	 * @param aamodif
	 * @param enzyme
	 * @param database
	 * @param isMonoMass
	 */
	public SequestPhosPairParameter(Aminoacids aas,
	        AminoacidModification aamodif, Enzyme enzyme, String database,
	        boolean isMonoMass) {
		super(aas, aamodif, enzyme, database, isMonoMass);
	}

	/**
	 * @param aas
	 * @param aamodif
	 * @param enzyme
	 * @param accesser
	 * @param isMonoMass
	 */
	public SequestPhosPairParameter(Aminoacids aas,
	        AminoacidModification aamodif, Enzyme enzyme,
	        IFastaAccesser accesser, boolean isMonoMass) {
		super(aas, aamodif, enzyme, accesser, isMonoMass);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.APIVASE_SEQUEST;
	}

}
