/*
 * *****************************************************************************
 * File: OMSSAParameter.java * * * Created on 09-05-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.AbstractParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.IModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.dbsearch.VariableModSymbols;

/**
 * Configuration for OMSSA database search
 * 
 * @author Xinning
 * @version 0.2.6, 06-13-2009, 21:05:47
 */
public class OMSSAParameter extends AbstractParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Enzyme enzyme;
	private transient OMSSAMods mods;
	private Aminoacids aas;
	private AminoacidModification aamodif;
	private transient VariableModSymbols modsymbol;

	public OMSSAParameter(String modsfile, String usermodsfile,
			Enzyme enzyme, int[] staticModifs,
	        int[] variableModifs, boolean mono_precursor, boolean mono_fragment)
	        throws ModsReadingException {
		this.enzyme = enzyme;
		this.setPeptideMass(mono_precursor);
		this.mods = new OMSSAMods(modsfile, usermodsfile);
		this.parseModif(mods, staticModifs, variableModifs, mono_precursor);
	}

	public OMSSAParameter(String modsfile, String usermodsfile,
	        IFastaAccesser accesser, Enzyme enzyme, int[] staticModifs,
	        int[] variableModifs, boolean mono_precursor, boolean mono_fragment)
	        throws ModsReadingException {
		this.enzyme = enzyme;
		this.setPeptideMass(mono_precursor);
		this.mods = new OMSSAMods(modsfile, usermodsfile);
		this.parseModif(mods, staticModifs, variableModifs, mono_precursor);
	}

	/**
	 * Parse the sequence into formatted peptide sequence.
	 * 
	 * A.AAAAA#AAAA.A
	 * 
	 * @param raw
	 *            the raw PeptideSequence
	 * @param modifs
	 *            the modifications on this peptide
	 * @param modif_at
	 *            the modified aminoacid indexes according to the modifs.
	 * @return
	 */
	public String parseSequence(PeptideSequence raw, IModification[] modifs,
	        int[] modif_at) {

		return this.modsymbol.parseSequence(raw, modifs, modif_at);
	}

	/**
	 * Parse the sequence into formatted peptide sequence. Used only for reading
	 * of raw output
	 * 
	 * A.AAAAA#AAAA.A
	 * 
	 * @param raw
	 *            the raw PeptideSequence
	 * @param modifs
	 *            the modifications on this peptide
	 * @param modif_at
	 *            the modified aminoacid indexes according to the modifs.
	 * @return
	 */
	public String parseSequence(PeptideSequence raw, int[] modifs,
	        int[] modif_at) {
		IModification[] mods = null;
		if (modifs != null && modifs.length > 0) {
			int len = modifs.length;
			mods = new OMSSAMod[len];

			for (int i = 0; i < len; i++) {
				mods[i] = this.getMod(modifs[i]);
			}
		}

		return this.modsymbol.parseSequence(raw, mods, modif_at);
	}

	@Override
	public Enzyme getEnzyme() {
		return this.enzyme;
	}

	@Override
	public Aminoacids getStaticInfo() {
		return this.aas;
	}

	@Override
	public AminoacidModification getVariableInfo() {
		return this.aamodif;
	}

	/**
	 * Get the OMSSAMod through the modification index in the predefined mods
	 * file. Null will be returned if the OMSSAMod of current index is not
	 * predefined in current mods.
	 * 
	 * @param index
	 * @return the mod for this index (May be null).
	 */
	public IModification getMod(int index) {
		return this.mods.getMod(index);
	}

	/**
	 * Get the OMSSAMod through the description of the modification in
	 * predefined mods file (NOT the name).
	 * 
	 * @param description
	 * @return the mod for this description (May be null).
	 */
	public IModification getMod(String description) {
		return this.mods.getMod(description);
	}

	/**
	 * Parse the modification
	 * 
	 * @param mods
	 * @param staticModifs
	 * @param variableModifs
	 * @param mono_precursor
	 */
	private void parseModif(OMSSAMods mods, int[] staticModifs,
	        int[] variableModifs, boolean mono_precursor) {

		Aminoacids aas = new Aminoacids();
		AminoacidModification aamodif = new AminoacidModification();

		// The OMSSAMod used for database search
		HashSet<OMSSAMod> set = new HashSet<OMSSAMod>();

		if (staticModifs != null) {
			int[] sortedstatic = staticModifs.clone();
			Arrays.sort(sortedstatic);

			for (int fix : sortedstatic) {
				OMSSAMod mod = mods.getMod(fix);

				if (mod == null)
					throw new NullPointerException(
					        "The specific modification is null. Index: " + fix);

				set.add(mod);

				HashSet<ModSite> list = mod.getModifiedAt();
				for (Iterator<ModSite> iterator = list.iterator(); iterator
				        .hasNext();) {
					aas.setModification(iterator.next(), mono_precursor ? mod
					        .getAddedMonoMass() : mod.getAddedAvgMass());
				}
			}
		}

		if (variableModifs != null) {
			int len = variableModifs.length;
			OMSSAMod[] variablemods = new OMSSAMod[len];
			for (int i = 0; i < len; i++) {
				int variable = variableModifs[i];
				OMSSAMod mod = mods.getMod(variable);

				if (mod == null)
					throw new NullPointerException(
					        "The specific modification is null. Index: "
					                + variable);
				set.add(mod);

				variablemods[i] = mod;
			}

			this.modsymbol = new VariableModSymbols(variablemods,
			        mono_precursor);

			for (int i = 0; i < len; i++) {
				OMSSAMod mod = variablemods[i];

				ModSite [] modsites = new ModSite[mod.getModifiedAt().size()];
				modsites = mod.getModifiedAt().toArray(modsites);
				aamodif.addModifications(modsites, this.modsymbol.getModSymbol(mod),
				        mono_precursor ? mod.getAddedMonoMass() : mod
				                .getAddedAvgMass(), mod.getName());
			}
		}

		this.aas = aas;
		this.aamodif = aamodif;

		// Only used modifications are retained.

		OMSSAMod[] newmods = new OMSSAMod[this.mods.length()];
		for (Iterator<OMSSAMod> iterator = set.iterator(); iterator.hasNext();) {
			OMSSAMod mod = iterator.next();
			newmods[mod.getIndex()] = mod;
		}

		this.mods = new OMSSAMods(newmods);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.OMSSA;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OMSSAParameter clone() {
		try {
			return (OMSSAParameter) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OMSSAParameter deepClone() {

		OMSSAParameter copy = this.clone();

		copy.aamodif = this.aamodif.deepClone();
		copy.aas = this.aas.deepClone();

		copy.enzyme = this.enzyme.deepClone();

		if (this.modsymbol != null) {
			System.err.println("Need to clone the mod symbol");
		}

		if (this.mods != null) {
			System.err.println("Need to clone the map");
		}

		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStaticInfo(Aminoacids aas) {
		this.aas = aas;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVariableInfo(AminoacidModification aamodif) {
		this.aamodif = aamodif;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#restore(java.util.HashMap)
	 */
	@Override
	public void restore(HashMap<Character, Character> replaceAA) {
		// TODO Auto-generated method stub
		char [] cs = this.aas.getModifiedAAs();
		for(int i=0;i<cs.length;i++){
			char c = cs[i];
			if(replaceAA.containsKey(c)){
				double omass = Aminoacids.getAminoacid(c).getMonoMass();
				this.aas.setModification(c, omass);
			}
		}
	}
}
