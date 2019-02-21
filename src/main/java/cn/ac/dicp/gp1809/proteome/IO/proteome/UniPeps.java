/*
 * *****************************************************************************
 * File: UniPeps.java * * * Created on 09-23-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;

/**
 * UniPeps containing a list of UniPep.
 * 
 * @author Xinning
 * @version 0.2.1, 04-01-2010, 11:16:42
 */
public class UniPeps {

	private static final DecimalFormat DF20 = new DecimalFormat("0.00");

	/**
	 * Peptide with the same z_unisequence will be considered as the same unique
	 * peptide. That is peptide with same unique sequence and with same charge
	 * state will be considered as the same unique peptide and will be merged
	 * together.
	 * 
	 * <p>
	 * Key: e.g. 2_AERKEPNK
	 */
	public static final int UniType_z_unisequence = 1;

	/**
	 * Peptide with the same z_sequence will be considered as the same unique
	 * peptide. That is peptide with same sequence (and same modification) and
	 * with same charge state will be considered as the same unique peptide and
	 * will be merged together.
	 * 
	 * <p>
	 * Key: e.g. 2_AERK#EPNK
	 */
	public static final int UniType_z_sequence = 2;

	/**
	 * Peptide with the same unisequence_MH will be considered as the same
	 * unique peptide. That is peptide with same unique sequence and with same
	 * molecular weight will be considered as the same unique peptide and will
	 * be merged together. <b>In this condition, same peptide with different
	 * charge state will be considered as the same unique peptide, and (2)
	 * peptides with same unique sequence but with different modifications
	 * (variable modifications at different site are not included.) are
	 * considered as different unique peptide</b>
	 * 
	 * <p>
	 * Key: e.g. AERKEPNK_956.00
	 */
	public static final int UniType_unisequence_MH = 3;
	
	/**
	 * Peptide with same unisequence_mz will be considered as the same unique peptide. 
	 * That is peptide with same m/z value while acquisition, and with same unique sequence will be 
	 * considered as the same peptide. <b>In this condition, same peptide with different
	 * charge state will be considered as the different unique peptide, and (2)
	 * peptides with same unique sequence but with different modifications
	 * (variable modifications at different site are not included) are
	 * considered as different unique peptide</b>
	 * 
	 * <p>
	 * The mz value is theoretical mz value!
	 * Key: e.g. AERKEPNK_956.00
	 * 
	 */
	public static final int UniType_unisequence_MZ = 4;

	/**
	 * The type for identifing a unique peptide. Default is:
	 * UniType_unisequence_MH.
	 * 
	 * <p>
	 * <b></b>
	 */
	private int UniType = UniType_unisequence_MZ;

	/*
	 * key is the charge_unique sequence and the value is a uniPep; Contains all
	 * the unique peptides
	 */
	private HashMap<String, UniPep> unimap;

	public UniPeps() {
		this.unimap = new HashMap<String, UniPep>(2000);
	}

	public UniPeps(int unitype) {
		this.unimap = new HashMap<String, UniPep>(2000);

		if (unitype == UniType_z_unisequence || unitype == UniType_z_sequence
		        || unitype == UniType_unisequence_MH)

			this.UniType = unitype;
	}

	public UniPep addPeptide(IPeptide pep) {
		return addPeptide(pep, unimap, this.UniType);
	}

	/**
	 * Add a peptide to unipep map. And return the unipep instance. If there has
	 * been unipep for this peptide in the unipep map, unipep will be got from
	 * the map first. Otherwise, a new instance of unipep will be created and
	 * put into the map.
	 * 
	 * @param pep
	 * @param unimap
	 * @param unitype
	 *            type to define the unique peptide (see also
	 *            UniType_z_unisequence, UniType_z_sequence,
	 *            UniType_unisequence_MH) <b>Default: UniType_unisequence_MH</b>
	 * @return unipep
	 */
	public static UniPep addPeptide(IPeptide pep,
	        HashMap<String, UniPep> unimap, int unitype) {
		String key = getKey(pep, unitype);
		UniPep tmp;
		if ((tmp = unimap.get(key)) == null) {
//			tmp = new UniPep(key, pep.getPeptideSequence().getUniqueSequence(), pep
//			        .getProteinReferences());
			
			tmp = new UniPep(key, pep.getPeptideSequence().getUniqueSequence(), pep
			        .getProteinReferences(), pep.getPepLocAroundMap());
			unimap.put(key, tmp);
		}
		tmp.addPeptide(pep);
		return tmp;
	}

	/**
	 * Add a peptide to unipep map. And return the unipep instance. If there has
	 * been unipep for this peptide in the unipep map, unipep will be got from
	 * the map first. Otherwise, a new instance of unipep will be created and
	 * put into the map.
	 * 
	 * <p>
	 * <b>Default: UniType_unisequence_MH</b>
	 * 
	 * @param pep
	 * @param unimap
	 * @return unipep
	 */
	public static UniPep addPeptide(IPeptide pep, HashMap<String, UniPep> unimap) {
		String key = getKey(pep, UniType_unisequence_MH);
		UniPep tmp;
		if ((tmp = unimap.get(key)) == null) {
//			tmp = new UniPep(key, pep.getPeptideSequence().getUniqueSequence(), pep
//			        .getProteinReferences());
			
			tmp = new UniPep(key, pep.getPeptideSequence().getUniqueSequence(), pep
			        .getProteinReferences(), pep.getPepLocAroundMap());
			unimap.put(key, tmp);
		}
		tmp.addPeptide(pep);
		return tmp;
	}

	private static String getKey(IPeptide peptide, int unitype) {
		switch (unitype) {
		case UniType_z_unisequence:
			return getChargeUnisequence(peptide);
		case UniType_z_sequence:
			return getChargeSequence(peptide);
		case UniType_unisequence_MH:
			return getUnisequenceMH(peptide);
		case UniType_unisequence_MZ:
			return getUnisequenceMZ(peptide);

		}

		throw new IllegalArgumentException("Unknown key type: " + unitype);
	}

	/**
	 * It is defined that only peptide with the same sequence, the same
	 * modification and same charge state will be merged together into a UniPep
	 * under the consideration that PSMs for these kinds of peptides are the
	 * independent events.
	 * 
	 * @return the key for UniPep. e.g. 2_AERK#EPNK
	 */
	public static final String getChargeSequence(IPeptide pep) {
		String s = pep.getSequence();
		StringBuilder sb = new StringBuilder(s.length());
		sb.append(pep.getCharge()).append('_').append(
		        PeptideUtil.getSequence(s));
		return sb.toString();
	}

	/**
	 * It is defined that only peptide with the same sequence (regardless the
	 * variable modifications) and same charge state will be merged together
	 * into a UniPep under the consideration that PSMs for these kinds of
	 * peptides are the independent events.
	 * 
	 * @return the key for UniPep. e.g. 2_AERKEPNK
	 */
	public static final String getChargeUnisequence(IPeptide pep) {
		String s = pep.getPeptideSequence().getUniqueSequence();
		StringBuilder sb = new StringBuilder(s.length());
		sb.append(pep.getCharge()).append('_').append(s);
		return sb.toString();
	}

	/**
	 * It is defined that only peptide with the same unique sequence and same
	 * molecular weight will be merged together into a UniPep under the
	 * consideration that PSMs for these kinds of peptides are the independent
	 * events.
	 * 
	 * @return the key for UniPep. e.g. AERKEPNK_956.00
	 */
	public static final String getUnisequenceMH(IPeptide pep) {
		String s = pep.getPeptideSequence().getUniqueSequence();
		String mh = DF20.format(pep.getMH());
		StringBuilder sb = new StringBuilder(s.length() + 7);
		sb.append(s).append('_').append(mh);
		return sb.toString();
	}
	
	/**
	 * It is defined that only peptide with the same unique sequence and same
	 * theoretical mz value will be merged together into a UniPep under the
	 * consideration that PSMs for these kinds of peptides are the independent
	 * events.
	 * 
	 * @return the key for UniPep. e.g. AERKEPNK_956.00
	 */
	public static final String getUnisequenceMZ(IPeptide pep) {
		String s = pep.getPeptideSequence().getUniqueSequence();
		String mz = DF20.format(SpectrumUtil.getMZ(pep.getMH(), pep.getCharge()));
		StringBuilder sb = new StringBuilder(s.length() + 7);
		sb.append(s).append('_').append(mz);
		return sb.toString();
	}
	

	/**
	 * @return number of UniPep; <b>Peptides identified with different charge
	 *         will be considered as two different UniPep</b>
	 */
	public int getUniPepNumber() {
		return this.unimap.size();
	}

	/**
	 * @return The iterator of all the UniPep in the Proteins.
	 */
	public Iterator<UniPep> getUniPepIterator() {
		return this.unimap.values().iterator();
	}

	/**
	 * @return The collection of all the UniPeps.
	 */
	public Collection<UniPep> getUniPeps() {
		return this.unimap.values();
	}
}
