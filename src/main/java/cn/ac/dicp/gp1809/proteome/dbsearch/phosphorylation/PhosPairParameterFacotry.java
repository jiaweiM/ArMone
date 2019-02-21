/* 
 ******************************************************************************
 * File: PhosPairParameterFacotry.java * * * Created on 04-30-2009
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
 * The phospair parameter factory
 * 
 * @author Xinning
 * @version 0.1, 04-30-2009, 13:49:23
 */
public class PhosPairParameterFacotry {

	/**
	 * Create the paired parameter for the specific peptide type
	 * 
	 * @param type
	 * @param aas
	 * @param aamodif
	 * @param enzyme
	 * @param database
	 * @param isMonoMass
	 * @return
	 */
	public static PhosPairParameter createPairedParameter(PeptideType type,
	        Aminoacids aas, AminoacidModification aamodif, Enzyme enzyme,
	        String database, boolean isMonoMass) {

		switch (type) {
		case SEQUEST:
			return new SequestPhosPairParameter(aas, aamodif, enzyme, database,
			        isMonoMass);
		case MASCOT:
			return new MascotPhosPairParameter(aas, aamodif, enzyme, database,
			        isMonoMass);
		case XTANDEM:
			return new XTandemPhosPairParameter(aas, aamodif, enzyme, database,
			        isMonoMass);
		case OMSSA:
			return new OMSSAPhosPairParameter(aas, aamodif, enzyme, database,
			        isMonoMass);
		case INSPECT:
			return new InspectPhosPairParameter(aas, aamodif, enzyme, database,
			        isMonoMass);
		case CRUX:
			return new CruxPhosPairParameter(aas, aamodif, enzyme, database,
			        isMonoMass);
		}

		throw new IllegalArgumentException("Unknown peptide type.");
	}

	/**
	 * Create the paired parameter for the specific peptide type
	 * 
	 * @param type
	 * @param aas
	 * @param aamodif
	 * @param enzyme
	 * @param accesser
	 * @param isMonoMass
	 * @return
	 */
	public static PhosPairParameter createPairedParameter(PeptideType type,
	        Aminoacids aas, AminoacidModification aamodif, Enzyme enzyme,
	        IFastaAccesser accesser, boolean isMonoMass) {

		switch (type) {
		case SEQUEST:
			return new SequestPhosPairParameter(aas, aamodif, enzyme, accesser,
			        isMonoMass);
		case MASCOT:
			return new MascotPhosPairParameter(aas, aamodif, enzyme, accesser,
			        isMonoMass);
		case XTANDEM:
			return new XTandemPhosPairParameter(aas, aamodif, enzyme, accesser,
			        isMonoMass);
		case OMSSA:
			return new OMSSAPhosPairParameter(aas, aamodif, enzyme, accesser,
			        isMonoMass);
		case INSPECT:
			return new InspectPhosPairParameter(aas, aamodif, enzyme, accesser,
			        isMonoMass);
		case CRUX:
			return new CruxPhosPairParameter(aas, aamodif, enzyme, accesser,
			        isMonoMass);
		}

		throw new IllegalArgumentException("Unkown peptide type");
	}
}
