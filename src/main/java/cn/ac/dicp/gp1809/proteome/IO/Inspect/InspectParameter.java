/*
 * *****************************************************************************
 * File: InspectParameter.java * * * Created on 03-24-2009
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.AbstractParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.proteome.dbsearch.VariableModSymbols;
import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;

/**
 * Parameters used in Inspect database search.
 * 
 * @author Xinning
 * @version 0.1.1, 05-03-2009, 20:53:41
 */
public class InspectParameter extends AbstractParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The symbol of phosphorylation modification. Not the digit value
	 */
	private static final String PHOSSYMBOL = "phos";

	/**
	 * The symbol of phosphorylation modification. Not the digit value
	 */
	private static final Integer PHOSSYADD = 80;

	private transient HashMap<Integer, InspectMod> variablemassmap;

	private Aminoacids aminoacids = null;
	private AminoacidModification aamodif = null;

	private transient VariableModSymbols modsymbol;

	// per peptide;

	private Enzyme enzyme = Enzyme.TRYPSIN;

	/**
	 * The input file (mgf or mzxml)
	 */
	private transient String inputFile;

	/**
	 * The instrument: ESI-ION-TRAP, QTOF and FT-Hybrid
	 */
	private String instrument;

	/**
	 * The required terms for identified peptides. For example, for full
	 * enzymatically digest, this value is 2.
	 */
	private short require_terms;

	/**
	 * The parent ion tolerance, should corresponding to parentTol_ppm;
	 */
	private float parentTol;
	/**
	 * The fragment tolerance, should corresponding to fragmentTol_ppm;
	 */
	private float fragmentTol;
	/**
	 * If use the ppm as the magnitude of scal
	 */
	private boolean parentTol_ppm;

	/**
	 * If use the ppm as the magnitude of scale
	 */
	private boolean fragmentTol_ppm;

	/**
	 * The maximum number of modifications per peptide (don't sure only variable
	 * or all, think as variable)
	 */
	private int max_mods_per_pep;

	/**
	 * A null parameter, must get the parameter from inspect parameter file
	 */
	public InspectParameter() {
		this.initial();
	}

	/**
	 * Get parameter instance from inspect parameter files;
	 * 
	 * @param param
	 * @throws ParameterParseException
	 */
	public InspectParameter(File paramfile) throws ParameterParseException {
		this.initial();
		this.loadFromFile(paramfile);
	}

	private void initial() {
		aminoacids = new Aminoacids();
		aamodif = new AminoacidModification();

		variablemassmap = new HashMap<Integer, InspectMod>();
	}

	/**
	 * Load from the configuration file.
	 * 
	 * @param paramfile
	 *            The parameter file.
	 * @return
	 * @throws ParameterParseException
	 */
	public InspectParameter loadFromFile(File paramfile)
	        throws ParameterParseException {
		try {

			if (paramfile == null)
				throw new NullPointerException("Cann't find the parameter file");

			else {
				System.out.println("Loading parameter file: "
				        + paramfile.getName());
				return this.loadFromReader(new BufferedReader(new FileReader(
				        paramfile)));
			}

		} catch (Exception e) {
			throw new ParameterParseException(e);
		}
	}

	/**
	 * Get Parameter from stream containing inspect parameters.
	 * 
	 * 
	 * @param param
	 * @return
	 * @throws ParameterParseException
	 */
	public InspectParameter loadFromReader(BufferedReader breader)
	        throws ParameterParseException {

		String line = null;
		try {

			for (line = breader.readLine(); line != null; line = breader
			        .readLine()) {
				if (line.length() == 0)
					continue;

				//The note
				if (line.startsWith("#"))
					continue;

				int idx = line.indexOf(',');
				if (idx == -1)
					throw new IllegalArgumentException(
					        "The line is illegal: \"" + line + "\".");

				String key = line.substring(0, idx).trim();
				String value = line.substring(idx + 1).trim();

				if (key.equalsIgnoreCase("spectra")) {
					this.inputFile = value;
					continue;
				}

				if (key.equalsIgnoreCase("db")) {
					String fastadb = value.substring(0,
					        value.lastIndexOf('.') + 1)
					        + "fasta";
					this.setDatabase(fastadb);
					continue;
				}

				if (key.equalsIgnoreCase("pmtolerance")) {
					this.parentTol = Float.parseFloat(value);
					this.parentTol_ppm = false;
					continue;
				}

				if (key.equalsIgnoreCase("parentppm")) {
					this.parentTol = Float.parseFloat(value);
					this.parentTol_ppm = true;
					continue;
				}

				if (key.equalsIgnoreCase("iontolerance")) {
					this.fragmentTol = Float.parseFloat(value);
					this.fragmentTol_ppm = false;
					continue;
				}

				if (key.equalsIgnoreCase("peakppm")) {
					this.fragmentTol = Float.parseFloat(value);
					this.fragmentTol_ppm = true;
					continue;
				}

				if (key.equalsIgnoreCase("mods")) {
					this.max_mods_per_pep = Integer.parseInt(value);
					continue;
				}

				if (key.equalsIgnoreCase("requiretermini")) {
					this.require_terms = Short.parseShort(value);
					continue;
				}

				if (key.equalsIgnoreCase("protease")) {
					this.enzyme = new InspectEnzymes().getEnzymeByName(value);
					continue;
				}

				if (key.equalsIgnoreCase("mod")) {
					this.parseModif(value);
				}
			}
		} catch (final NumberFormatException ne) {
			throw new ParameterParseException(
			        "Inspect params has garbled number in this line: " + line,
			        ne.getCause());
		} catch (final IOException ioe) {
			throw new ParameterParseException(
			        "Errors occur when reading the params file, may be damaged?",
			        ioe.getCause());
		} catch (final RuntimeException re) {
			throw new ParameterParseException(re);
		}

		// ---process the variable modification

		InspectMod[] mods = this.variablemassmap.values().toArray(
		        new InspectMod[this.variablemassmap.size()]);

		this.modsymbol = new VariableModSymbols(mods, true);

		for (InspectMod mod : mods) {
			this.aamodif.addModifications(mod.getModifiedAt().toArray(
			        new ModSite[0]), modsymbol.getModSymbol(mod), this
			        .isMonoPeptideMass() ? mod.getAddedMonoMass() : mod
			        .getAddedAvgMass(), mod.getName());
		}

		return this;
	}

	/**
	 * Parse the modification
	 * 
	 * @param value
	 */
	private void parseModif(String value) {

		String[] vals = StringUtil.split(value, ',');
		int len = vals.length;
		double mass;
		String name = null;
		//"type" are "fix", "cterminal", "nterminal", and "opt"
		String type = null;
		String modifaa;
		/*
		 * mod,[MASS],[RESIDUES],[TYPE],[NAME]
		 */
		switch (len) {
		case 4:
			name = vals[3];
		case 3:
			type = vals[2];
		case 2:
			mass = Double.parseDouble(vals[0]);
			modifaa = vals[1];
			break;

		default:
			throw new IllegalArgumentException("Illegal modification: \""
			        + value + "\".");
		}

		boolean fix = false;

		HashSet<ModSite> sites = new HashSet<ModSite>();
		if (type == null || type.toLowerCase().equals("opt")) {
			for (int i = 0; i < modifaa.length(); i++) {
				sites.add(ModSite.newInstance_aa(modifaa.charAt(i)));
			}

		} else if (type.toLowerCase().equals("fix")) {
			fix = true;
			for (int i = 0; i < modifaa.length(); i++) {
				sites.add(ModSite.newInstance_aa(modifaa.charAt(i)));
			}
		} else if (type.toLowerCase().equals("cterminal")) {
			if ("*".equals(modifaa)) {
				sites.add(ModSite.newInstance_PepCterm());
			} else {
				for (int i = 0; i < modifaa.length(); i++) {
					sites.add(ModSite
					        .newInstance_PepCterm_aa(modifaa.charAt(i)));
				}
			}
		} else if (type.toLowerCase().equals("nterminal")) {
			if ("*".equals(modifaa)) {
				sites.add(ModSite.newInstance_PepNterm());
			} else {
				for (int i = 0; i < modifaa.length(); i++) {
					sites.add(ModSite
					        .newInstance_PepNterm_aa(modifaa.charAt(i)));
				}
			}
		}

		InspectMod mod = new InspectMod(name, sites, mass, mass);

		if (fix) {

			/*
			 * Currently the modification is not validated. For example,
			 * mistakenly set the fix modification twice.
			 */
			//validate

			for (Iterator<ModSite> it = sites.iterator(); it.hasNext();)
				this.aminoacids.setModification(it.next(), mass);
		} else {
			/*
			 * The symbol for Inspect sequence. for example, if the modification
			 * mass is 15.4 then the symbol is 15, but when the mass is 15.5,
			 * the symbol become 16.
			 */
			int symInSeq = (int) Math.round(mass);
			InspectMod tmod;
			if ((tmod = this.variablemassmap.get(symInSeq)) != null) {
				/*
				 * Only if the two modifications are with the exactly same mass,
				 * they are considered as the same and can be merged together.
				 * For example, the S T and Y modifications are defined
				 * separately.
				 */
				if (tmod.getAddedAvgMass() == mass) {
					tmod.merge(mod);
				} else {
					throw new IllegalArgumentException(
					        "Because the inspect use integer value as modification "
					                + "symbol in peptide sequence, there is duplicated integer value "
					                + "for two modfiications of " + symInSeq);
				}
			} else
				this.variablemassmap.put(symInSeq, mod);
		}
	}

	private static final Pattern SYMBOL_PATTERN = Pattern
	        .compile("([^A-Z\\.\\*]+)");

	private static final Pattern INTEGER_PATTERN = Pattern.compile("[-+]\\d+");

	/**
	 * Parse the peptide with new defined modification symbols. <b>Null Will be
	 * returned if the peptide is with more than one modifications on a single
	 * aminoacid</b>
	 * 
	 * 
	 * @see VariableModSymbols
	 * @param peptidestr
	 * @return the parsed peptide sequence with new designed modification
	 *         symbol.
	 */
	public String parsePeptide(String peptidestr) {

		if (peptidestr.length() < 2) {
			System.err.println("Warning: too short peptide string; "
			        + peptidestr);
			return peptidestr;
		}

		PeptideSequence pseq = PeptideSequence.parseSequence(peptidestr);

		Matcher matcher = SYMBOL_PATTERN.matcher(peptidestr);

		if (matcher.find()) {
			int term_len;
			if (peptidestr.charAt(1) == '.') {
				term_len = 2;
			} else if (peptidestr.charAt(0) == '.') {
				term_len = 1;
			} else {
				term_len = 0;
			}

			int modifs = 0;
			ArrayList<InspectMod> list = new ArrayList<InspectMod>();
			IntArrayList idxes = new IntArrayList();

			do {
				int idx = matcher.start();
				String sym = matcher.group();

				/*
				 * The digest
				 */
				if (INTEGER_PATTERN.matcher(sym).matches()) {
					//+16 or -18
					Integer integ = sym.charAt(0) == '+' ? Integer.valueOf(sym
					        .substring(1)) : Integer.valueOf(sym);

					InspectMod mod = this.variablemassmap.get(integ);

					if (mod == null) {
						//May be two or more modifications on a same aa
						System.out
						        .println("Skip peptide with unknown modif: \""
						                + peptidestr + "\".");

						return null;
					} else {
						list.add(mod);
					}
				} else if (PHOSSYMBOL.equals(sym)) {
					InspectMod mod = this.variablemassmap.get(PHOSSYADD);
					list.add(mod);
				} else {
					//two or more modifications

					System.out
					        .println("Skip peptide with more than one modif at a "
					                + "single aminoacid: \""
					                + peptidestr
					                + "\".");

					//				 this.splitMutilModification(sym);

					return null;
				}

				idx = idx - term_len - modifs;// The 1-based index
				idxes.add(idx);
				modifs += sym.length();
			} while (matcher.find());

			return this.modsymbol.parseSequence(pseq, list
			        .toArray(new InspectMod[list.size()]), idxes.toArray());
		} else
			return pseq.toString();
	}

	/**
	 * split multiple modifications on a single aminoacid, e.g. MSFDFAS#@SFSSS
	 * 
	 * @param mods
	 * @return
	 */
	private String[] splitMutilModification(String mods) {

		return null;
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
	 * @return the inputFile
	 */
	public String getInputFile() {
		return inputFile;
	}

	/**
	 * @return the instrument
	 */
	public String getInstrument() {
		return instrument;
	}

	/**
	 * @return the require_terms
	 */
	public short getRequire_terms() {
		return require_terms;
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
	 * @return the parentTol_ppm
	 */
	public boolean isParentTol_ppm() {
		return parentTol_ppm;
	}

	/**
	 * @return the fragmentTol_ppm
	 */
	public boolean isFragmentTol_ppm() {
		return fragmentTol_ppm;
	}

	/**
	 * @return the max_mods_per_pep
	 */
	public int getMax_mods_per_pep() {
		return max_mods_per_pep;
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
	public InspectParameter clone() {
		try {
	        return (InspectParameter) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public InspectParameter deepClone() {
		
		InspectParameter copy = this.clone();
		
		copy.aamodif = this.aamodif.deepClone();
		copy.aminoacids = this.aminoacids.deepClone();
		
		copy.enzyme = this.enzyme.deepClone();
		
		if(this.modsymbol != null) {
			System.err.println("Need to clone the mod symbol");
		}
		
		if(this.variablemassmap != null) {
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
