/* 
 ******************************************************************************
 * File: PhosPairFormatFactory.java * * * Created on 02-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * The phosphopeptide pair format factory
 * 
 * @author Xinning
 * @version 0.1, 02-27-2009, 15:44:59
 */
public class PhosPairFormatFactory {

	
	
	/**
	 * Create a peptidefromat instance for peptide of specific type from the
	 * formatted title.
	 * 
	 * @param title
	 *            formatted title, the title should correspondent to the type of
	 *            peptides. This title commonly from the ppl or ppls file.
	 * @param peptide_type
	 *            type of peptide, see the final constant of
	 *            PeptideFormatFactory
	 * 
	 * @return the PeptideFromat.
	 * @throws NullPointerException
	 *             if the title is null or with length of 0
	 * @throws IllegalFormaterException
	 *             if the title is not valid or the peptide type is not valid
	 */
	public static IPeptideFormat<?> createPeptideFormat(String title,
	        PeptideType peptide_type) throws IllegalFormaterException,
	        NullPointerException {

		switch (peptide_type) {
		case APIVASE_SEQUEST:
			return SequestPhosPairFormat.parseTitle(title);

		case APIVASE_MASCOT:
			return MascotPhosPairFormat.parseTitle(title);
			
		case APIVASE_XTANDEM:
			return XTandemPhosPairFormat.parseTitle(title);
			
		case APIVASE_OMSSA:
			return OMSSAPhosPairFormat.parseTitle(title);
			
		case APIVASE_INSPECT:
			return InspectPhosPairFormat.parseTitle(title);
			
		case APIVASE_CRUX:
			return CruxPhosPairFormat.parseTitle(title);
		}

		throw new IllegalArgumentException("The PeptideFormat can not be"
		        + "created for unknown file type: " + peptide_type);
	}

}
