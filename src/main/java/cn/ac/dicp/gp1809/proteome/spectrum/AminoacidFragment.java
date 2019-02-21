/*
 * *****************************************************************************
 * File: AminoacidFragment.java Created on 05-30-2008
 * 
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * *****************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aaproperties.Aminoacid;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;

/**
 * Theoretically fragment aminoacid sequences into b and y type ions
 * 
 * <p>
 * Changes:
 * <li>0.1.3, 02-26-2009: Modified for new AminoacidModification
 * <li>0.2, 05-30-2009: Major revision of the fragment strategy.
 * 
 * 
 * @author Xinning
 * @version 0.2, 05-30-2009, 14:37:44
 */
public class AminoacidFragment {

	private static final double B_ION_ADD_MONO = 1.00782;

	private static final double Y_ION_ADD_MONO = 19.01872;

	private static final double C_ION_ADD_MONO = 18.03438;

	private static final double Z_ION_ADD_MONO = 2.99962;

	private static final double B_ION_ADD_AVG = 1.00794;

	private static final double Y_ION_ADD_AVG = 19.02322;

	private static final double C_ION_ADD_AVG = 18.0385;

	private static final double Z_ION_ADD_AVG = 3.0006;

	private AminoacidModification aamodif = null;
	private Aminoacids aminoacids = null;

	private MwCalculator mwcalor;

	/**
	 * 
	 * @param _aminoacids
	 *            contains aminoacids informations
	 * @param _aamodif
	 *            the infor of modification
	 * 
	 */
	public AminoacidFragment(Aminoacids _aminoacids,
	        AminoacidModification _aamodif) {
		this.aminoacids = _aminoacids;
		this.aamodif = _aamodif;

		this.mwcalor = new MwCalculator(_aminoacids, _aamodif);
	}

	public AminoacidFragment(Aminoacids _aminoacids){
		this(_aminoacids, new AminoacidModification());
	}
	
	/**
	 * 
	 */
	public AminoacidFragment(){
		this(new Aminoacids(), new AminoacidModification());
	}
	
	/**
	 * The variable information for this fragment pattern
	 * 
	 * @return
	 */
	public AminoacidModification getVariableInfo() {
		return this.aamodif;
	}

	/**
	 * The static information for this fragment pattern.
	 * 
	 * @return
	 */
	public Aminoacids getStaticInfo() {
		return this.aminoacids;
	}

	/**
	 * Fragment peptide theoretically into ions of specific types.
	 * 
	 * @see Ion
	 * @param sequence
	 *            raw peptide from sequest output
	 * @param types
	 *            the types of ions to be generated
	 * @return ions of specified types
	 */
	public Ions fragment(String sequence, int[] types, boolean isMono) {

		IModifiedPeptideSequence mseq = ModifiedPeptideSequence
		        .parseSequence(sequence);

		return this.fragment(mseq, types, isMono);

	}

	/**
	 * Fragment peptide theoretically into ions of specific types.
	 * 
	 * @see Ion
	 * @param sequence
	 *            raw peptide from sequest output
	 * @param types
	 *            the types of ions to be generated
	 * @return ions of specified types
	 */
	public Ions fragment(IModifiedPeptideSequence mseq, int[] types,
	        boolean isMono) {

		Ions ions = new Ions(mseq, isMono ? this.mwcalor.getMonoIsotopeMh(mseq)
		        : this.mwcalor.getAverageMh(mseq));

		for (int type : types) {

			Ion[] ins = null;

			switch (type) {
			case Ion.TYPE_B:
				ins = this.fragment_b(mseq, isMono);
				break;
			case Ion.TYPE_Y:
				ins = this.fragment_y(mseq, isMono);
				break;
			case Ion.TYPE_C:
				ins = this.fragment_c(mseq, isMono);
				break;
			case Ion.TYPE_Z:
				ins = this.fragment_z(mseq, isMono);
				break;
			default:
				throw new IllegalArgumentException("Unsupported type: " + type);
			}

			ions.add(type, ins);
		}

		return ions;
	}

	/**
	 * Fragment and get the b theoretical ion
	 * 
	 * @param mseq
	 * @return
	 */
	public Ion[] fragment_b(IModifiedPeptideSequence mseq, boolean isMono) {

		int len = mseq.length() - 1;

		Ion[] ions = new Ion[len];
		int num = mseq.getModificationNumber();
		boolean hasModif = num > 0;
		int idx = 0;
		int curtloc = -1;
		IModifSite curtSite = null;
		
		double mass = isMono ? B_ION_ADD_MONO : B_ION_ADD_AVG;
		mass += this.aminoacids.getNterminalStaticModif();
		
		IModifSite[] sites = null;
		if (hasModif) {
			sites = mseq.getModifications();
			curtSite = sites[0];
			curtloc = curtSite.modifLocation();
			
			if(curtloc==0){
				mass += this.aamodif.getAddedMassForModif(curtSite.symbol());
			}
			
			if (++idx < num) {
				curtSite = sites[idx];
				curtloc = curtSite.modifLocation();
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= len; i++) {
			char aa = mseq.getAminoaicdAt(i);
			Aminoacid aac = this.aminoacids.get(aa);
			sb.append(aac.getOneLetter());
			mass += isMono ? aac.getMonoMass() : aac.getAverageMass();

			if (hasModif) {
				if (i == curtloc) {
					mass += this.aamodif
					        .getAddedMassForModif(curtSite.symbol());
					if (++idx < num) {
						curtSite = sites[idx];
						curtloc = curtSite.modifLocation();
					}
				}
				//small and big than the localization are all ignored
			}

			ions[i - 1] = new Ion(mass, Ion.TYPE_B, "b", i);
			ions[i - 1].setFragseq(sb.toString());
		}

		return ions;
	}

	/**
	 * Fragment and get the y theoretical ion
	 * 
	 * @param mseq
	 * @return
	 */
	public Ion[] fragment_y(IModifiedPeptideSequence mseq, boolean isMono) {

		int len = mseq.length();

		Ion[] ions = new Ion[len - 1];
		int num = mseq.getModificationNumber();
		boolean hasModif = num > 0;
		int idx = -1;
		int curtloc = -1;
		IModifSite curtSite = null;

		IModifSite[] sites = null;
		if (hasModif) {
			idx = num - 1;
			sites = mseq.getModifications();
			curtSite = sites[idx];
			curtloc = curtSite.modifLocation();
		}
		
		StringBuilder sb = new StringBuilder();
		double mass = isMono ? Y_ION_ADD_MONO : Y_ION_ADD_AVG;
		mass += this.aminoacids.getCterminalStaticModif();
		for (int i = 0, n=len - 1; i < n; i++) {
			int loc = len - i;
			char aa = mseq.getAminoaicdAt(loc);
			Aminoacid aac = this.aminoacids.get(aa);
			sb.insert(0, aac.getOneLetter());
			
			mass += isMono ? aac.getMonoMass() : aac.getAverageMass();
			if (hasModif) {
				if (loc == curtloc) {
					mass += this.aamodif
					        .getAddedMassForModif(curtSite.symbol());
					if (--idx >= 0) {
						curtSite = sites[idx];
						curtloc = curtSite.modifLocation();
					}
				}
				//small and big than the localization are all ignored
			}

			ions[i] = new Ion(mass, Ion.TYPE_Y, "y", i+1);
			ions[i].setFragseq(sb.toString());
		}

		return ions;
	}

	/**
	 * Fragment and get the c theoretical ion
	 * 
	 * @param mseq
	 * @return
	 */
	public Ion[] fragment_c(IModifiedPeptideSequence mseq, boolean isMono) {

		int len = mseq.length() - 1;

		Ion[] ions = new Ion[len];
		int num = mseq.getModificationNumber();
		boolean hasModif = num > 0;
		int idx = 0;
		int curtloc = -1;
		IModifSite curtSite = null;

		IModifSite[] sites = null;
		if (hasModif) {
			sites = mseq.getModifications();
			curtSite = sites[0];
			curtloc = curtSite.modifLocation();
		}

		double mass = isMono ? C_ION_ADD_MONO : C_ION_ADD_AVG;
		mass += this.aminoacids.getNterminalStaticModif();
		
		for (int i = 1; i <= len; i++) {
			char aa = mseq.getAminoaicdAt(i);
			Aminoacid aac = this.aminoacids.get(aa);

			mass += isMono ? aac.getMonoMass() : aac.getAverageMass();

			if (hasModif) {
				if (i == curtloc) {
					mass += this.aamodif
					        .getAddedMassForModif(curtSite.symbol());
					if (++idx < num) {
						curtSite = sites[idx];
						curtloc = curtSite.modifLocation();
					}
				}
				//small and big than the localization are all ignored
			}

			ions[i - 1] = new Ion(mass, Ion.TYPE_C, "c", i);
		}

		return ions;
	}

	/**
	 * Fragment and get the z theoretical ion
	 * 
	 * @param mseq
	 * @return
	 */
	public Ion[] fragment_z(IModifiedPeptideSequence mseq, boolean isMono) {

		int len = mseq.length();

		Ion[] ions = new Ion[len - 1];
		int num = mseq.getModificationNumber();
		boolean hasModif = num > 0;
		int idx = -1;
		int curtloc = -1;
		IModifSite curtSite = null;

		IModifSite[] sites = null;
		if (hasModif) {
			idx = num - 1;
			sites = mseq.getModifications();
			curtSite = sites[idx];
			curtloc = curtSite.modifLocation();
		}

		double mass = isMono ? Z_ION_ADD_MONO : Z_ION_ADD_AVG;
		mass += this.aminoacids.getCterminalStaticModif();
		for (int i = 0, n=len - 1; i < n; i++) {
			int loc = len - i;
			char aa = mseq.getAminoaicdAt(loc);
			Aminoacid aac = this.aminoacids.get(aa);

			mass += isMono ? aac.getMonoMass() : aac.getAverageMass();

			if (hasModif) {
				if (loc == curtloc) {
					mass += this.aamodif
					        .getAddedMassForModif(curtSite.symbol());
					if (--idx >= 0) {
						curtSite = sites[idx];
						curtloc = curtSite.modifLocation();
					}
				}
				//small and big than the localization are all ignored
			}

			ions[i] = new Ion(mass, Ion.TYPE_Z, "z", i+1);
		}

		return ions;
	}
	
	public MwCalculator getMwCalculator(){
		return this.mwcalor;
	}

	/**
	 * Fragment peptide theoritically into ions.
	 * 
	 * @param sequence
	 *            raw peptide from sequest output
	 * @return ions with b&y type ions from 1 to the end,(e.g contains 20 aa,
	 *         b1...b20) bions[0] = b1;
	 */
	/*
	public Ions fragment(String sequence) {
		String seqUnTermi = PeptideUtil.getSequence(sequence);
		int length = seqUnTermi.length();

		/*
		 * added mass for b and y type ions with 1+ charge state + for b is
		 * NH2-C-C=O for y is +NH3-C-COOH;
		 */
	/*
		float bincr1 = 1.00782f;
		float yincr1 = 19.01836f;

		//mh+ +1;
		float totalmass = bincr1
		        + yincr1
		        + (float) (aminoacids.getCterminalStaticModif() + aminoacids
		                .getNterminalStaticModif());
		int modifcount = 0;

		for (int i = 0; i < length; i++) {
			char c = seqUnTermi.charAt(i);

			if (c >= 'A' && c <= 'Z')
				totalmass += aminoacids.get(c).getMonoMass();
			else {
				totalmass += aamodif.getAddedMassForModif(c);
				modifcount++;
			}
		}

		int len = length - modifcount;
		Ion[] bions = new Ion[len];
		Ion[] yions = new Ion[len];

		bions[len - 1] = new Ion(totalmass, Ion.TYPE_B, "b", len);
		yions[len - 1] = new Ion(totalmass, Ion.TYPE_Y, "y", len);

		float tempmass = bincr1 + (float) aminoacids.getNterminalStaticModif();

		int p = 0;
		for (int i = 0; i < length; i++) {
			char c = seqUnTermi.charAt(i);

			if (++p == len)
				break;

			if (c >= 'A' && c <= 'Z') {
				tempmass += aminoacids.get(c).getMonoMass();
				bions[p - 1] = new Ion(tempmass, Ion.TYPE_B, "b", p);
				int y = len - p;
				yions[y - 1] = new Ion(totalmass - tempmass, Ion.TYPE_Y, "y", y);
			} else {//modif
				p--;
				tempmass += aamodif.getAddedMassForModif(c);
				bions[p - 1] = new Ion(tempmass, Ion.TYPE_B, "b", p);
				int y = len - p;
				yions[y - 1] = new Ion(totalmass - tempmass, Ion.TYPE_Y, "y", y);
			}
		}

		return new Ions(seqUnTermi, bions, yions, totalmass - 1f);
	}
*/
	/**
	 * Get the fragment after neutral loss. Note that the returned ions are only
	 * the ions that occured neutral loss. The ions with no neutral loss will be
	 * removed. <b> e.g. AAAA#AAAAA the returned neutral loss ions for b are
	 * from b4, there are only 6 ions for b series neutral loss ion. </b>
	 * 
	 * @param ions
	 *            the normal fragment
	 * @param neusymbol
	 *            symbol which indicating a modification with readily occured
	 *            neutral loss.
	 * @param loss
	 *            the mass of the neutral loss
	 */
	/*
	public static Ions getNeuIons(Ions ions, char neusymbol, float loss) {

		String parsedseq = ions.sequence();
		int len = parsedseq.length();

		//for mono phos peptide ,these two values are the same, for multi, be different
		//this point is the point in ions array, but not the index of sequence
		int bp = -1;
		int yp = -1;
		int othermodif = 0;
		//symbol must not in the first point
		for (int i = 1; i < len; i++) {
			char c = parsedseq.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == neusymbol) {
					bp = i - 1 - othermodif;
					break;
				} else
					othermodif++;
			}
		}

		if (bp == -1)//no modif with potential neutral loss
			return null;

		othermodif = 0;

		for (int i = 1;; i++) {//no need to end because there did exist a phso modif	
			char c = parsedseq.charAt(len - i);
			if (c < 'A' || c > 'Z') {
				if (c == neusymbol) {
					yp = i - 1 - othermodif;
					break;
				} else
					othermodif++;
			}
		}

		Ion[] bions = ions.bIons();
		Ion[] yions = ions.yIons();
		len = bions.length;
		Ion[] bnions = new Ion[len - bp];
		Ion[] ynions = new Ion[len - yp];

		int counter = 0;
		for (int i = bp; i < len; i++) {
			Ion ion = bions[i];
			bnions[counter++] = new Ion(ion.getMz() - loss, Ion.TYPE_B_NEU,
			        "b*", ion.getSeries());
		}
		counter = 0;
		for (int i = yp; i < len; i++) {
			Ion ion = yions[i];
			ynions[counter++] = new Ion(ion.getMz() - loss, Ion.TYPE_Y_NEU,
			        "y*", ion.getSeries());
		}

		return new Ions(ions.sequence(), bnions, ynions, ions.getMH());
	}
	
	*/
	
	public static void main(String[] args){
		
		Aminoacids aminoacids = new Aminoacids();
		aminoacids.setCysCarboxyamidomethylation();
		
		AminoacidModification aamodif = new AminoacidModification();
		aamodif.addModification('*', 0.984, "de");
		aamodif.addModification('#', 15.9949, "ox");
		aamodif.addModification('^', 1312.45522927, "g");
		
		MwCalculator mwc = new MwCalculator(aminoacids, aamodif);
		AminoacidFragment aaf = new AminoacidFragment(aminoacids, aamodif);
		String sequence = "AATVGSLAGQPLQER";

		double mh = mwc.getMonoIsotopeMh(sequence);
		System.out.println((mh-AminoAcidProperty.PROTON_W)+"\t"+mh+"\t"+
				(mh+AminoAcidProperty.PROTON_W)/2.0+"\t"+(mh+AminoAcidProperty.PROTON_W*2.0)/3.0);
		int[] types = new int[]{Ion.TYPE_B, Ion.TYPE_Y};
		Ions ions = aaf.fragment(sequence, types, true);
		Ion[] ion = ions.getTotalIons();
		for(int i=0;i<ion.length;i++){
			double mz1 = ion[i].getMzVsCharge(1);
			double mz2 = ion[i].getMzVsCharge(2);
			double mz3 = ion[i].getMzVsCharge(3);
			System.out.println(ion[i].getName()+"\t"+mz1+"\t"+mz2+"\t"+mz3+"\t"+(mz1-291));
		}
	}
	
}
