/*
 * *****************************************************************************
 * File: PeptideFormatFactory.java * * * Created on 08-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.DefaultInspectPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.DefaultMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.DefaultOMSSAPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.DefaultXTandemPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.DefaultCruxPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.CruxPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.InspectPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.MascotPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.OMSSAPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.SequestPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.XTandemPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.DefaultSequestPeptideFormat;

/**
 * Factory used for creation of PeptideFormat for different type of Peptides:
 * SequestPeptide, MascotPeptide .... or other specific used peptides:
 * PhosPeptidePair.
 * 
 * @author Xinning
 * @version 0.4, 04-02-2009, 20:33:53
 */
public class PeptideFormatFactory {

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
		case SEQUEST:
			return DefaultSequestPeptideFormat.parseTitle(title);

		case OMSSA:
			return DefaultOMSSAPeptideFormat.parseTitle(title);
			
		case XTANDEM:
			return DefaultXTandemPeptideFormat.parseTitle(title);
			
		case MASCOT:
			return DefaultMascotPeptideFormat.parseTitle(title);
			
		case INSPECT:
			return DefaultInspectPeptideFormat.parseTitle(title);
			
		case CRUX:
			return DefaultCruxPeptideFormat.parseTitle(title);
			
		case APIVASE:
			return DefaultSequestPeptideFormat.parseTitle(title);

		case APIVASE_SEQUEST:
			return SequestPhosPairFormat.parseTitle(title);
			
		case APIVASE_OMSSA:
			return OMSSAPhosPairFormat.parseTitle(title);
			
		case APIVASE_XTANDEM:
			return XTandemPhosPairFormat.parseTitle(title);
			
		case APIVASE_MASCOT:
			return MascotPhosPairFormat.parseTitle(title);
			
		case APIVASE_INSPECT:
			return InspectPhosPairFormat.parseTitle(title);
			
		case APIVASE_CRUX:
			return CruxPhosPairFormat.parseTitle(title);
		}

		throw new IllegalArgumentException("The PeptideFormat can not be"
		        + "created for unknown file type: " + peptide_type);
	}
	
	public static IPeptideFormat<?> createPeptideFormat(String [] title,
	        PeptideType peptide_type) throws IllegalFormaterException,
	        NullPointerException {

		switch (peptide_type) {
		case SEQUEST:
			return DefaultSequestPeptideFormat.parseTitle(title);

		case OMSSA:
			return DefaultOMSSAPeptideFormat.parseTitle(title);
			
		case XTANDEM:
			return DefaultXTandemPeptideFormat.parseTitle(title);
			
		case MASCOT:
			return DefaultMascotPeptideFormat.parseTitle(title);
			
		case INSPECT:
			return DefaultInspectPeptideFormat.parseTitle(title);
			
		case CRUX:
			return DefaultCruxPeptideFormat.parseTitle(title);
			
		case APIVASE:
			return DefaultSequestPeptideFormat.parseTitle(title);

		case APIVASE_SEQUEST:
			return SequestPhosPairFormat.parseTitle(title);
			
		case APIVASE_OMSSA:
			return OMSSAPhosPairFormat.parseTitle(title);
			
		case APIVASE_XTANDEM:
			return XTandemPhosPairFormat.parseTitle(title);
			
		case APIVASE_MASCOT:
			return MascotPhosPairFormat.parseTitle(title);
			
		case APIVASE_INSPECT:
			return InspectPhosPairFormat.parseTitle(title);
			
		case APIVASE_CRUX:
			return CruxPhosPairFormat.parseTitle(title);
		}

		throw new IllegalArgumentException("The PeptideFormat can not be"
		        + "created for unknown file type: " + peptide_type);
	}
	
}
