/*
 ******************************************************************************
 * File: CruxParameter.java * * * Created on 04-01-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.sqt.ISQTHeader;
import cn.ac.dicp.gp1809.proteome.IO.sqt.ISQTHeader.IAlgField;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.AbstractParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.proteome.dbsearch.VariableModSymbols;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Parameters used in Inspect database search.
 * 
 * @author Xinning
 * @version 0.1.1, 06-13-2009, 21:12:13
 */
public class CruxParameter extends AbstractParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Aminoacids aminoacids = null;
	private AminoacidModification aamodif = null;
	private transient VariableModSymbols modsymbol;

	// per peptide;
	private Enzyme enzyme = Enzyme.TRYPSIN;

	/**
	 * The parent ion tolerance, should corresponding to parentTol_ppm;
	 */
	private float parentTol;
	/**
	 * The fragment tolerance, should corresponding to fragmentTol_ppm;
	 */
	private float fragmentTol;

	/**
	 * Use the mono or average mass for the database search
	 */
	private boolean parentMono, fragmentMono;
	/**
	 * output n top matched peptides
	 */
	private int displayTopN;

	/**
	 * The symbol map for for sequest predefined symbols
	 */
	private transient HashMap<Character, CruxMod> varsymbolmap;

	/**
	 * The tile of each column in scan line (S)
	 */
	private transient String[] scanColumnTitle;

	/**
	 * The title of each column in match line(M)
	 */
	private transient String[] matchColumnTitle;

	/**
	 * A null parameter, must get the parameter from inspect parameter file
	 */
	public CruxParameter() {
		this.initial();
	}

	/**
	 * Get parameter instance from inspect parameter files;
	 * 
	 * @param param
	 * @throws ParameterParseException
	 */
	public CruxParameter(File paramfile) throws ParameterParseException {
		this.initial();
		this.loadFromFile(paramfile);
	}

	/**
	 * Get parameter instance from inspect parameter files;
	 * 
	 * @param param
	 * @throws ParameterParseException
	 */
	public CruxParameter(ISQTHeader header) throws ParameterParseException {
		this.initial();
		this.loadFromSQTHeader(header);
	}

	private void initial() {
		aminoacids = new Aminoacids();
		aamodif = new AminoacidModification();
		this.varsymbolmap = new HashMap<Character, CruxMod>();
	}

	/**
	 * Load from the configuration file.
	 * 
	 * @param paramfile
	 *            The parameter file.
	 * @return
	 * @throws ParameterParseException
	 */
	private void loadFromSQTHeader(ISQTHeader header)
	        throws ParameterParseException {
		try {

			if (header == null)
				throw new NullPointerException("The SQT header is null");

			else {

				if (!"crux".equalsIgnoreCase(header.getSQTGenerator())) {
					throw new IllegalArgumentException(
					        "The input is not a crux search result.");
				}

				System.out.println("Loading parameter ...");

				this.parseStaticMods(header.getStaticMod());

				this.parentMono = header.getPrecursorMasses().equalsIgnoreCase(
				        "mono");
				this.fragmentMono = header.getFragmentMassesString()
				        .equalsIgnoreCase("mono");

				ArrayList<IAlgField> fields = header.getAlgFields();
				for (IAlgField field : fields) {
					String name = field.field();
					String value = field.value();

					if (name.equals("PreMasTol")) {
						this.parentTol = Float.parseFloat(value);
						continue;
					}

					if (name.equals("FragMassTol")) {
						this.fragmentTol = Float.parseFloat(value);
						continue;
					}

					if (name.equals("DisplayTop")) {
						this.displayTopN = Integer.parseInt(value);
						continue;
					}
				}

				//	No useful informations	
				//	ArrayList<ISQTHeader.IComment> comments = header.getComments();
				//	for(ISQTHeader.IComment comment : comments) {
				//	}

				ArrayList<ISQTHeader.IOtherField> otherfields = header
				        .getOtherFields();
				ArrayList<CruxMod> vmods = new ArrayList<CruxMod>();
				for (ISQTHeader.IOtherField field : otherfields) {
					String value = field.value();

					if (value.startsWith("EnzymeSpec")) {
						this.enzyme = new CruxEnzymes().getEnzymeByName(value
						        .substring(11).trim());
						continue;
					}

					if (value.startsWith("DiffMod")) {
						String cap = value.substring(8).trim();
						int idx = cap.indexOf('=');
						char sym = cap.charAt(idx - 1);
						double add = Double
						        .parseDouble(cap.charAt(idx + 1) == '+' ? cap
						                .substring(idx + 2) : cap
						                .substring(idx + 1));

						HashSet<ModSite> set = new HashSet<ModSite>(idx - 1);
						for (int j = 0, n = idx - 1; j < n; j++) {
							set.add(ModSite.newInstance_aa(cap.charAt(j)));
						}

						CruxMod vmod = new CruxMod(set, add, add);
						this.varsymbolmap.put(sym, vmod);
						vmods.add(vmod);
						continue;
					}

					if (value.startsWith("Line fields: S")) {
						this.scanColumnTitle = StringUtil.splitAndTrim(value
						        .substring(15), ',');
						continue;
					}

					if (value.startsWith("Line fields: M")) {
						this.matchColumnTitle = StringUtil.splitAndTrim(value
						        .substring(15), ',');
						continue;
					}
				}

				CruxMod[] mods = vmods.toArray(new CruxMod[vmods.size()]);
				this.modsymbol = new VariableModSymbols(mods, this
				        .isMonoPeptideMass());

				for (CruxMod mod : mods) {
					this.aamodif.addModifications(mod.getModifiedAt().toArray(
					        new ModSite[0]), modsymbol.getModSymbol(mod), this
					        .isMonoPeptideMass() ? mod.getAddedMonoMass() : mod
					        .getAddedAvgMass(), mod.getName());
				}
			}

		} catch (Exception e) {
			throw new ParameterParseException(e);
		}
	}

	/**
	 * Parse the static mod
	 * 
	 * @param smod
	 */
	private void parseStaticMods(String smod) {

		System.out.println("More than one static mods have not be desinged.");

		if (smod != null && smod.length() > 0) {
			char aa = smod.charAt(0);
			double currentMass = Double.parseDouble(smod.substring(2).trim());

			this.aminoacids.setModifiedMassForAA(aa, currentMass);
		}
	}

	/**
	 * Load from the configuration file.
	 * 
	 * @param paramfile
	 *            The parameter file.
	 * @return
	 * @throws ParameterParseException
	 */
	private void loadFromFile(File paramfile) throws ParameterParseException {
		try {

			if (paramfile == null)
				throw new NullPointerException("Cann't find the parameter file");

			else {
				System.out.println("Loading parameter file: "
				        + paramfile.getName());

				System.out.println("Not designed");
			}

		} catch (Exception e) {
			throw new ParameterParseException(e);
		}
	}

	/**
	 * Parse the peptide with new defined modification symbols.
	 * 
	 * @param peptidestr
	 * @return
	 */
	public String parsePeptide(String peptidestr) {

		IModifiedPeptideSequence mseq = ModifiedPeptideSequence
		        .parseSequence(peptidestr);

		IModifSite[] sites = mseq.getModifications();
		if (sites == null || sites.length == 0)
			return mseq.getFormattedSequence();

		int len = sites.length;
		/*
		 * Test whether there are more than one variable modifications at the
		 * same site.
		 */
		if (len > 1) {
			HashSet<Integer> set = new HashSet<Integer>(len);

			for (IModifSite site : sites) {
				Integer s = site.modifLocation();
				if (set.contains(s)) {
					System.out
					        .println("Skip peptide with more than one variable modifs at same aminoacid : \""
					                + peptidestr + "\".");
					return null;
				}
				set.add(s);
			}
		}

		int[] loc = new int[len];
		CruxMod[] mods = new CruxMod[len];

		for (int i = 0; i < len; i++) {
			IModifSite site = sites[i];
			loc[i] = site.modifLocation();
			char sym = site.symbol();
			CruxMod mod = this.varsymbolmap.get(sym);

			if (mod == null) {
				throw new NullPointerException("The symbol of '" + sym
				        + "' isn't defined in variable modificiation.");
			}
			mods[i] = mod;
		}

		return this.modsymbol.parseSequence(mseq, mods, loc);
	}

	/**
	 * @return static information of the amino acids; the mass of each contains
	 *         static modificaiton which doesn't display from the sequence
	 *         outputed by sequest;
	 */
	public Aminoacids getStaticInfo() {
		return aminoacids;
	}

	/**
	 * @return the differential modification information, each char of symbol
	 *         indicated a modification
	 */
	public AminoacidModification getVariableInfo() {
		return aamodif;
	}

	/**
	 * Enzyme used for peptide digestion;
	 */
	public Enzyme getEnzyme() {
		return enzyme;
	}

	/**
	 * @return the parentTol
	 */
	public float getParentTol() {
		return parentTol;
	}

	/**
	 * @return the fragmentTol
	 */
	public float getFragmentTol() {
		return fragmentTol;
	}

	/**
	 * @return the parentMono
	 */
	public boolean isParentMono() {
		return parentMono;
	}

	/**
	 * @return the fragmentMono
	 */
	public boolean isFragmentMono() {
		return fragmentMono;
	}

	/**
	 * @return the displayTopN
	 */
	public int getDisplayTopN() {
		return displayTopN;
	}

	/**
	 * @return the scanColumnTitle
	 */
	public String[] getScanColumnTitle() {
		return scanColumnTitle;
	}

	/**
	 * @return the matchColumnTitle
	 */
	public String[] getMatchColumnTitle() {
		return matchColumnTitle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.INSPECT;
	}
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public CruxParameter clone() {
		try {
	        return (CruxParameter) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CruxParameter deepClone() {
		
		CruxParameter copy = this.clone();
		
		copy.aamodif = this.aamodif.deepClone();
		copy.aminoacids = this.aminoacids.deepClone();
		
		copy.enzyme = this.enzyme.deepClone();
		
		if(this.modsymbol != null) {
			System.err.println("Need to clone the mod symbol");
		}
		
		if(this.varsymbolmap != null) {
			System.err.println("Need to clone the map");
		}
		
		return copy;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
    public void setStaticInfo(Aminoacids aas) {
		this.aminoacids = aas;
    }

	/**
	 * {@inheritDoc}}
	 */
	@Override
    public void setVariableInfo(AminoacidModification aamodif) {
		this.aamodif = aamodif;
    }
	
	public static void main(String[] args) {
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#restore(java.util.HashMap)
	 */
	@Override
	public void restore(HashMap<Character, Character> replaceAA) {
		// TODO Auto-generated method stub
		char [] cs = this.aminoacids.getModifiedAAs();
		for(int i=0;i<cs.length;i++){
			char c = cs[i];
			if(replaceAA.containsKey(c)){
				double omass = Aminoacids.getAminoacid(c).getMonoMass();
				this.aminoacids.setModification(c, omass);
			}
		}
	}
}
