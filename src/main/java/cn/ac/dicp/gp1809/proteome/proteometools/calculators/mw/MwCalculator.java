/*
 ******************************************************************************
 * File: MwCalculator.java * * * Created on 03-05-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aaproperties.Aminoacid;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;

/**
 * Calculator for peptide or protein sequences
 * 
 * <p>
 * Changes:
 * <li>0.1.7, 02-26-2009: Modified for new AminoacidModification
 * <li>
 * 0.1.8, 05-30-2009: Add method {@link #getAverageMh(IModifiedPeptideSequence)}, {@link #getMonoIsotopeMh(IModifiedPeptideSequence)}
 * <li>0.1.9, 06-10-2009: Improve the accuracy for calcuation
 * @author Xinning
 * @version 0.1.8, 05-30-2009, 14:28:26
 */
public class MwCalculator {

	/**
	 * The Mono mass (H3O+)
	 */
	private static final double TERMINAL_MASS_MONO = AminoAcidProperty.PROTON_W+AminoAcidProperty.MONOW_H*2+AminoAcidProperty.MONOW_O;
	
	/**
	 * The average mass (H3O+)
	 */
	private static final double TERMINAL_MASS_AVG = AminoAcidProperty.PROTON_W+AminoAcidProperty.AVERAGEW_H*2+AminoAcidProperty.AVERAGEW_O;
	
	private Aminoacids aacids;

	private AminoacidModification aamodif;

	/**
	 * Construct a calculator using default parameter. No modification will be
	 * included while calculating of molecular weight. To set modifications
	 * please us MwCalculator(Aminoacids, AminoacidModification)
	 */
	
	public MwCalculator() {
		this.aacids = new Aminoacids();
		this.aamodif = new AminoacidModification();
	}

	/**
	 * Construct a calculator using specific modifications for molecular weight.
	 * 
	 * @param aacids
	 *            : Aminoacids instance.
	 * @param aamodif
	 *            : AminoacidModification instance.
	 */
	public MwCalculator(Aminoacids aacids, AminoacidModification aamodif) {
		this.aacids = aacids;
		this.aamodif = aamodif;
	}

	/**
	 * @param aacids
	 *            the aacids to set
	 */
	public final void setAacids(Aminoacids aacids) {
		this.aacids = aacids;
	}

	/**
	 * @param aamodif
	 *            the aamodif to set
	 */
	public final void setAamodif(AminoacidModification aamodif) {
		this.aamodif = aamodif;
	}

	/**
	 * Calculate the monoisotope MH+ for the specific peptide sequence. Any
	 * modifications (static or variable) can be included when the related
	 * Aminoacids and AminoacidModification instance are transfered into the
	 * calculator through setAacids() and setAamodif();
	 * 
	 * @param sequence
	 *            can include variable modification symbols such as #* and so
	 *            on, but the added mass for these symbols must be defined in
	 *            aamodifcation. Otherwise, the masses added by these variable
	 *            modification will not be included.
	 * @return the mono isotope mass
	 */
	public final double getMonoIsotopeMh(String sequence) {
		if (sequence == null || sequence.length() == 0)
			return 0d;

		//19.01836d
		double mw = TERMINAL_MASS_MONO + aacids.getNterminalStaticModif()
		        + aacids.getCterminalStaticModif();
		for (int i = 0, n = sequence.length(); i < n; i++) {
			char aa = sequence.charAt(i);
			Aminoacid aacid = this.aacids.get(aa);
			if (aacid != null)
				mw += aacid.getMonoMass();
			else
				mw += this.aamodif.getAddedMassForModif(aa);
		}

		return mw;
	}
	
	public final double getMonoIsotopeMZ(String sequence) {
		if (sequence == null || sequence.length() == 0)
			return 0d;

		//19.01836d
		double mw = TERMINAL_MASS_MONO + aacids.getNterminalStaticModif()
		        + aacids.getCterminalStaticModif() - AminoAcidProperty.PROTON_W;
		for (int i = 0, n = sequence.length(); i < n; i++) {
			char aa = sequence.charAt(i);
			Aminoacid aacid = this.aacids.get(aa);
			if (aacid != null)
				mw += aacid.getMonoMass();
			else
				mw += this.aamodif.getAddedMassForModif(aa);
		}

		return mw;
	}

	/**
	 * Calculate the monoisotope mass for the specific peptide sequence. Any
	 * modifications (static or variable) can be included when the related
	 * Aminoacids and AminoacidModification instance are transfered into the
	 * calculator through setAacids() and setAamodif();
	 * 
	 * @param sequence
	 *            can include variable modification symbols such as #* and so
	 *            on, but the added mass for these symbols must be defined in
	 *            aamodifcation. Otherwise, the masses added by these variable
	 *            modification will not be included.
	 * @return the mono isotope mass
	 */
	public final double getAverageMh(String sequence) {
		if (sequence == null || sequence.length() == 0)
			return 0d;

		//19.02322d
		double mw = TERMINAL_MASS_AVG + aacids.getNterminalStaticModif()
		        + aacids.getCterminalStaticModif();
		for (int i = 0, n = sequence.length(); i < n; i++) {
			char aa = sequence.charAt(i);
			Aminoacid aacid = this.aacids.get(aa);
			if (aacid != null)
				mw += this.aacids.get(aa).getAverageMass();
			else
				mw += this.aamodif.getAddedMassForModif(aa);
		}

		return mw;
	}

	/**
	 * Calculate the monoisotope MH+ for the specific peptide sequence. Any
	 * modifications (static or variable) can be included when the related
	 * Aminoacids and AminoacidModification instance are transfered into the
	 * calculator through setAacids() and setAamodif();
	 * 
	 * @param sequence
	 *            the modified sequence
	 * @return the mono isotope mass
	 */
	public final double getMonoIsotopeMh(IModifiedPeptideSequence mseq) {
		if (mseq == null || mseq.length() == 0)
			return 0d;
		//19.01836d

		double mw = TERMINAL_MASS_MONO + aacids.getNterminalStaticModif()
		        + aacids.getCterminalStaticModif();
		for (int i = 1, n = mseq.length(); i <= n; i++) {
			char aa = mseq.getAminoaicdAt(i);
			Aminoacid aacid = this.aacids.get(aa);
			mw += aacid.getMonoMass();
		}

		if (mseq.getModificationNumber() > 0) {
			IModifSite[] sites = mseq.getModifications();

			for (IModifSite site : sites) {
				mw += this.aamodif.getAddedMassForModif(site.symbol());
			}
		}

		return mw;
	}

	/**
	 * Calculate the monoisotope mass for the specific peptide sequence. Any
	 * modifications (static or variable) can be included when the related
	 * Aminoacids and AminoacidModification instance are transfered into the
	 * calculator through setAacids() and setAamodif();
	 * 
	 * @param mseq
	 *            the modified sequence
	 * 
	 * @return the mono isotope mass
	 */
	public final double getAverageMh(IModifiedPeptideSequence mseq) {

		//19.02322d
		double mw = TERMINAL_MASS_AVG + aacids.getNterminalStaticModif()
		        + aacids.getCterminalStaticModif();
		for (int i = 1, n = mseq.length(); i <= n; i++) {
			char aa = mseq.getAminoaicdAt(i);
			Aminoacid aacid = this.aacids.get(aa);
			mw += aacid.getAverageMass();
		}

		if (mseq.getModificationNumber() > 0) {
			IModifSite[] sites = mseq.getModifications();

			for (IModifSite site : sites) {				
				mw += this.aamodif.getAddedMassForModif(site.symbol());
			}
		}

		return mw;
	}

	/**
	 * @return the AminoacidModification
	 */
	public AminoacidModification getAAModification() {
		return this.aamodif;
	}

	/**
	 * @return the Aminoacids
	 */
	public Aminoacids getAminoacids() {
		return this.aacids;
	}
	
	public static void main(String [] args){
		
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		AminoacidModification aam = new AminoacidModification();
		aam.addModification('#', 15.994915, "Oxidation");
		aam.addModification('@', 28.0313, "Dimethyl_CH3_K");
		aam.addModification('^', 36.07567, "Dimethyl_C13D3_K");
		aam.addModification('*', 0.984016, "Deamidated");
		System.out.println(aam.getModifications().length);
		Modif [] modifs = aam.getModifications();
		for(int i=0;i<modifs.length;i++){
			char symbol = modifs[i].getSymbol();System.out.println(symbol);
			HashSet <ModSite> sites = aam.getModifSites(symbol);
//			System.out.println(sites.size());
		}
		
		MwCalculator mw = new MwCalculator(aas, aam);
		double mh = mw.getMonoIsotopeMh("PPQPA");
		double m = mh- AminoAcidProperty.PROTON_W;
		double m2 = m/3.0+AminoAcidProperty.PROTON_W;
		System.out.println(mh+"\t"+m+"\t"+m2+"\t"+m*1E-5);
		
		double t14 =  656.227614635;
		double t25 = 1312.45522927;
		double ccc = (1106.6341-AminoAcidProperty.PROTON_W)*1;
		double noglyco = ccc-0;
//		System.out.println(ccc+"\t"+noglyco+"\t"+(noglyco+AminoAcidProperty.PROTON_W)+"\t"+Math.abs(noglyco-m)/m*1E6);
	}
	
}
