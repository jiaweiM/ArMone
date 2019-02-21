/* 
 ******************************************************************************
 * File: DefaultProteinFormat.java * * * Created on 09-11-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Formatter for protein identifications
 * 
 * @author Xinning
 * @version 0.1, 09-11-2008, 21:57:30
 */
public class DefaultProteinFormat implements IProteinFormat {

	protected final static String lineSeparator = IOConstant.lineSeparator;

	private IPeptideFormat pepformat;

	private IReferenceDetailFormat refformat;

	/**
	 * Create default protein format from the peptide title and protein title
	 * 
	 * @param reftitle
	 * @param peptitle
	 * @return
	 * @throws IllegalFormaterException
	 * @throws NullPointerException
	 */
	public static DefaultProteinFormat parseTitle(String reftitle,
	        String peptitle) throws IllegalFormaterException,
	        NullPointerException {

		PeptideType type = PeptideType.SEQUEST;
		/*
		 * Get the peptide type from the title of peptide. The format should be
		 * print at the begin of title with the format of [<i>peptidetype</i>]
		 * 
		 * @param peptitle @return
		 */
		int idx = peptitle.indexOf(']');
		if (idx != -1) {
			type = PeptideType.getTypebyName(peptitle.substring(1, idx));
		} else
			throw new IllegalArgumentException("Peptide type exception.");

		return new DefaultProteinFormat(DefaultReferenceDetailFormat
		        .parseTitle(reftitle), PeptideFormatFactory
		        .createPeptideFormat(peptitle, type));
	}
	

	public DefaultProteinFormat(IReferenceDetailFormat refformat,
	        IPeptideFormat pepformat) {
		this.pepformat = pepformat;
		this.refformat = refformat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IProteinFormat#format(cn.ac.dicp.gp1809.proteome.peptideIO.proteome.Protein)
	 */
	@Override
	public String format(Protein protein, String indexstr) {

		if (protein == null)
			throw new NullPointerException("Can not format a null protein.");

		StringBuilder sb = new StringBuilder(200);

		IReferenceDetail[] references = protein.getReferences();
		
		// The first reference is different with others, e.g. 
		sb.append('$').append(indexstr).append(" - ").append(1);
		sb.append(this.refformat.format1(references[0]));
		
		for (int i = 1; i < references.length;) {
			IReferenceDetail reference = references[i];
			sb.append(lineSeparator).append('$').append(indexstr).append(" - ").append(++i);
			sb.append(this.refformat.format(reference));
		}
		
		IPeptide[] peptides = protein.getAllPeptides();
		for (IPeptide peptide : peptides) {
			sb.append(lineSeparator).append(this.pepformat.format(peptide));
		}

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IProteinFormat#getTitle()
	 */
	@Override
	public String getTitle() {
		return this.refformat.getTitleString() + lineSeparator
		        + this.pepformat.getTitleString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .IProteinFormat#parseProtein(java.lang.String, java.lang.String)
	 */
	@Override
	public Protein parseProtein(String[] refDetails, String[] peptideStrings){

		int refcount = refDetails.length;
		IReferenceDetail[] refs = new IReferenceDetail[refcount];
		for (int i = 0; i < refcount; i++) {
			refs[i] = this.refformat.parse(refDetails[i]);
		}

		int pepcount = peptideStrings.length;
		IPeptide[] peptides = new IPeptide[pepcount];
		for (int i = 0; i < pepcount; i++) {
			peptides[i] = this.pepformat.parse(peptideStrings[i]);
		}

		try {
			return new Protein(refs, peptides);
		} catch (ProteinNotFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MoreThanOneRefFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Protein parseProtein(String [][] refDetails, String[][] peptideStrings){

		int refcount = refDetails.length;
		IReferenceDetail[] refs = new IReferenceDetail[refcount];
		for (int i = 0; i < refcount; i++) {
			refs[i] = this.refformat.parse(refDetails[i]);
		}

		int pepcount = peptideStrings.length;
		IPeptide[] peptides = new IPeptide[pepcount];
		for (int i = 0; i < pepcount; i++) {
			peptides[i] = this.pepformat.parse(peptideStrings[i]);
		}

		try {
			return new Protein(refs, peptides);
		} catch (ProteinNotFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MoreThanOneRefFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters
	 *      .IProteinFormat#getPeptideFormat()
	 */
	@Override
	public IPeptideFormat getPeptideFormat() {
		return this.pepformat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters
	 *      .IProteinFormat#getReferenceFormat()
	 */
	@Override
	public IReferenceDetailFormat getReferenceFormat() {
		return this.refformat;
	}
}
