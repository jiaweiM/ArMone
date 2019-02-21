/*
 * *****************************************************************************
 * File: SequestParameter.java * * * Created on 10-06-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.IndexDatabaseparser;
import cn.ac.dicp.gp1809.proteome.databasemanger.IndexedDatabaseException;
import cn.ac.dicp.gp1809.proteome.dbsearch.*;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Parameters used in SEQUEST database search.
 * 
 * @author Xinning
 * @version 0.6.4, 06-13-2009, 21:02:05
 */
public class SequestParameter extends AbstractParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Aminoacids aminoacids = null;
	private AminoacidModification aamodif = null;

	// private NeutralLoss neutralLoss = null;

	/*
	 * If indexed database is used for database search. However, the fasta name
	 * should always be generated.
	 */
	private String db_index_name;

	private String ionseries = "0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0";

	private int nucleotide_reading_frame;

	private int miss_cleavage_site = 2;

	private int outputPeptides = 10;

	/*
	 * Number of peptides for Sp calculation
	 */
	private int peptideForScore = 250;

	private boolean removePrecursorPeak;
	// masses
	private int mass_type_fragment = 1;// 0=average masses, 1=monoisotopic
	// masses

	private float mass_tolerance_pep = 2f;
	private float mass_tolerance_fragment = 1f;

	// 0=amu, 1=mmu, 2=ppm
	private int peptide_mass_unit;

	private int max_variable_modif = 3;// max differential modification number
	// per peptide;

	private Enzyme enzyme = Enzyme.TRYPSIN;
	private int enzymaticType = Enzyme.ENZYMATIC_FULL;
	private float ionPercentCutoff;
	private int matchPeakCount;
	private int matchPeakAllowedError = 1;
	private float matchTolerance = 1f;
	private float[] proteinMassFilter = { 0f, 0f };
	private boolean isProteinMassFilterUsed;
	private String sequenceHeaderFilter = "";

	private float[] digest_mass_range = { 600f, 3500f };

	// Only used for the old type of params file, in which the enzyme is
	// specified at the
	// end of the params file.
	private int enzyme_Num;

	/*
	 * The symbol map for for sequest predefined symbols
	 */
	private transient HashMap<Character, SequestVariableMod> varsymbolmap;

	private transient VariableModSymbols modsymbol;

	/**
	 * A null parameter, must get the parameter from params file
	 */
	public SequestParameter() {
		this.initial();

		// aminoacids.setCysCarboxyamidomethylation();
		// aamodif.setModification('*',15.99492);//oxidation
		// aamodif.setModification('#',79.96633);//phosphorylation
		// aamodif.setModification('@',-18.01056);//dewater

		// neutralLoss = new NeutralLoss('#',97.9763D);
	}

	/**
	 * Get parameter instance from sequest parameter files;
	 * 
	 * @param param
	 * @throws ParameterParseException
	 */
	public SequestParameter(String param) throws ParameterParseException {
		this(new File(param));
	}

	/**
	 * Get parameter instance from sequest parameter files;
	 * 
	 * @param param
	 * @throws ParameterParseException
	 */
	public SequestParameter(File param) throws ParameterParseException {
		this.initial();
		readFromFile(param);
	}

	private void initial() {
		aminoacids = new Aminoacids();
		aamodif = new AminoacidModification();

		this.varsymbolmap = new HashMap<Character, SequestVariableMod>();
	}

	/**
	 * Get Parameter from sequest parameter file;
	 * 
	 * @param param
	 * @return
	 * @throws ParameterParseException
	 * @throws IOException
	 */
	public SequestParameter readFromFile(File paramfile)
	        throws ParameterParseException {
		FileReader reader = null;

		try {
			reader = new FileReader(paramfile);
		} catch (NullPointerException e) {
			throw new ParameterParseException("The parameter file is Null.");
		} catch (Exception e) {
			throw new ParameterParseException(
			        "Cann't access the parameter file: " + paramfile.getName());
		}

		return this.readFromStream(reader);
	}

	/**
	 * Get Parameter from stream containing sequest parameters. This stream can
	 * from a parameter file , srf inner paramter or any other sources.
	 * <p>
	 * <b>However, the stream must start with [SEQUES] and with the same format
	 * as in a separate parameter file.</b>
	 * 
	 * 
	 * @param param
	 * @return
	 * @throws ParameterParseException
	 * @throws IOException
	 */
	public SequestParameter readFromStream(Reader paramReader)
	        throws ParameterParseException {

		BufferedReader breader = new BufferedReader(paramReader);
		String line = null;
		ArrayList<SequestVariableMod> vmods = new ArrayList<SequestVariableMod>();

		try {
			for (line = breader.readLine(); line != null
			        && !line.equals("[SEQUEST]"); line = breader.readLine())
				;
			if (line == null) {
				throw new ParameterParseException(
				        "Missing head tag [SEQUEST] in the params file!");
			}

			for (line = breader.readLine(); line != null; line = breader
			        .readLine()) {
				if (line.length() == 0)
					continue;

				if (line.startsWith("database_name")
				        || line.startsWith("first_database_name")
				/* || line.startsWith("second_database_name") */) {

					// if(this.dbName!=null)
					// System.out.println("Using last listed database rather
					// than "+this.dbName);

					final String db = line.substring(line.indexOf('=') + 2);
					if (db.endsWith(".hdr")) {
						this.db_index_name = db;

						try {
							String srcdb = new IndexDatabaseparser(db)
							        .getHeader().getOriginal_fasta_db();

							this.setDatabase(srcdb);
						} catch (IndexedDatabaseException e) {
							System.err
							        .println("Cann't find the indexed database in "
							                + "params file, need to be set.");
						}

					} else
						this.setDatabase(db);
					continue;
				}

				if (line.startsWith("peptide_mass_tolerance")) {
					final int vindex = line.indexOf('=') + 2;
					mass_tolerance_pep = Float.parseFloat(line
					        .substring(vindex));
					continue;
				}

				if (line.startsWith("peptide_mass_units")) {
					final int vindex = line.indexOf('=') + 2;
					int idx = line.indexOf(' ', vindex);
					if(idx == -1)
						idx = line.length();
//					System.out.println(idx);
					peptide_mass_unit = Integer.parseInt(line.substring(vindex,
					        idx).trim());
					continue;
				}

				if (line.startsWith("num_output_lines")) {
					final int vindex = line.indexOf('=') + 2;
					outputPeptides = Integer.parseInt(line.substring(vindex,
					        line.indexOf(' ', vindex)));

					continue;
				}

				if (line.startsWith("enzyme_info")) {
					/*
					 * Enzyme;
					 */
					final String lin = line.substring(14);
					final String[] enz = StringUtil.split(lin, ' ');
					if (enz.length != 5)
						throw new ParameterParseException(
						        "Some information lost for the enzyme: " + lin);

					final boolean offset_after = Integer.parseInt(enz[2]) == 1 ? true
					        : false;
					enzyme = new Enzyme(enz[0], offset_after, enz[3], enz[4]);
					this.enzymaticType = Integer.parseInt(enz[1]);

					continue;
				}

				// Only used in old type params
				if (line.startsWith("enzyme_number")) {
					final int vindex = line.indexOf('=') + 2;
					this.enzyme_Num = Integer.parseInt(line.substring(vindex)
					        .trim());
					continue;
				}

				if (line.startsWith("fragment_ion_tolerance")) {
					final int vindex = line.indexOf('=') + 2;
					mass_tolerance_fragment = Float.parseFloat(line.substring(
					        vindex, line.indexOf(' ', vindex)));
					continue;
				}

				if (line.startsWith("max_num_differential_")) {
					final int vindex = line.indexOf('=') + 2;
					max_variable_modif = Integer.parseInt(line.substring(
					        vindex, line.indexOf(' ', vindex)));
					continue;
				}

				if (line.startsWith("num_results")) {
					final int vindex = line.indexOf('=') + 2;
					peptideForScore = Integer.parseInt(line.substring(vindex,
					        line.indexOf(' ', vindex)));
					continue;
				}

				if (line.startsWith("remove_precursor_peak")) {
					final int vindex = line.indexOf('=') + 2;
					this.removePrecursorPeak = Integer.parseInt(line.substring(
					        vindex, line.indexOf(' ', vindex))) == 0 ? false
					        : true;
					continue;
				}

				if (line.startsWith("ion_cutoff_percentage")) {
					final int vindex = line.indexOf('=') + 2;
					this.ionPercentCutoff = Float.parseFloat(line.substring(
					        vindex, line.indexOf(' ', vindex)));
					continue;
				}

				// * # @ ^ ~ $ for 1-6 diff modif, ct[ and nt]
				if (line.startsWith("diff_search_options")) {
					int vindex = line.indexOf('=') + 2;
					String[] strings = StringUtil.split(line.substring(vindex),
					        ' ');

					if (strings.length != 12)
						throw new RuntimeException(
						        "Variable modification reading error: " + line);
					/*
					 * Symbols used in sequest database search for variable
					 * modification;
					 */
					final char[] symbol = new char[] { '*', '#', '@', '^', '~',
					        '$' };

					if (strings.length > 2 * symbol.length)
						throw new IllegalArgumentException(
						        "Currently, a maximum of " + symbol.length
						                + " modifications is allowed.");

					for (int i = 0; i < symbol.length; i++) {
						int idx = i << 1;
						double add = Double.parseDouble(strings[idx]);
						String aas = strings[idx + 1].trim();
						if (add != 0.0d) {
							HashSet<ModSite> list = new HashSet<ModSite>(aas
							        .length());
							for (int j = 0; j < aas.length(); j++) {
								list.add(ModSite.newInstance_aa(aas.charAt(j)));
							}
							SequestVariableMod vmod = new SequestVariableMod(
							        null, add, add, list, symbol[i]);

							vmods.add(vmod);
							this.varsymbolmap.put(vmod.getSymbol(), vmod);
						}
					}

					continue;
				}

				if (line.startsWith("ion_series")) {
					ionseries = line.substring(line.indexOf('=') + 1).trim();
					continue;
				}

				if (line.startsWith("nucleotide_reading_frame")) {
					nucleotide_reading_frame = Integer.parseInt(String
					        .valueOf(line.charAt(line.indexOf('=') + 2)));
					continue;
				}

				if (line.startsWith("term_diff_search_options")) {
					int vindex = line.indexOf('=') + 2;
					int idx2 = line.indexOf(' ', vindex);
					double ct = Double
					        .parseDouble(line.substring(vindex, idx2));
					if (ct != 0d) {
						HashSet<ModSite> list = new HashSet<ModSite>(1);
						list.add(ModSite.newInstance_PepCterm());
						SequestVariableMod vmod = new SequestVariableMod(null, 
								ct, ct, list, '[');
						
						vmods.add(vmod);
						this.varsymbolmap.put(vmod.getSymbol(), vmod);
					}
					double nt = Double.parseDouble(line.substring(idx2));
					if (nt != 0d) {
						HashSet<ModSite> list = new HashSet<ModSite>(1);
						list.add(ModSite.newInstance_PepNterm());
						SequestVariableMod vmod = new SequestVariableMod(null,
						        nt, nt, list, ']');

						vmods.add(vmod);
						this.varsymbolmap.put(vmod.getSymbol(), vmod);
					}
					continue;
				}

				if (line.startsWith("mass_type_parent")) {
					final int vindex = line.indexOf('=') + 2;
					int mass_type_parent = Integer.parseInt(line.substring(
					        vindex, line.indexOf(' ', vindex)));

					this.setPeptideMass(mass_type_parent == 1);
					continue;
				}

				if (line.startsWith("mass_type_fragment")) {
					final int vindex = line.indexOf('=') + 2;
					mass_type_fragment = Integer.parseInt(line.substring(
					        vindex, line.indexOf(' ', vindex)));
					continue;
				}

				if (line.startsWith("max_num_internal_cleavage_sites")) {
					final int vindex = line.indexOf('=') + 2;
					miss_cleavage_site = Integer.parseInt(line.substring(
					        vindex, line.indexOf(' ', vindex)));
					continue;
				}

				if (line.startsWith("protein_mass_filter")) {
					final int vindex = line.indexOf('=') + 2;
					final int vindex2 = line.indexOf(' ', vindex);

					float massst = Float.parseFloat(line.substring(vindex,
					        vindex2));
					float massed = Float.parseFloat(line.substring(vindex2 + 1,
					        line.indexOf(' ', vindex2 + 1)));

					if (massst > 0.0001f && massed > 0.0001f && massst < massed) {
						this.isProteinMassFilterUsed = true;

						this.proteinMassFilter[0] = massst;
						this.proteinMassFilter[1] = massed;
					}

					continue;
				}

				if (line.startsWith("match_peak_count")) {
					final int vindex = line.indexOf('=') + 2;
					this.matchPeakCount = Integer.parseInt(line.substring(
					        vindex, line.indexOf(' ', vindex)));
					continue;
				}

				if (line.startsWith("match_peak_allowed_error")) {
					final int vindex = line.indexOf('=') + 2;
					this.matchPeakAllowedError = Integer.parseInt(line
					        .substring(vindex, line.indexOf(' ', vindex)));
					continue;
				}

				if (line.startsWith("match_peak_tolerance")) {
					final int vindex = line.indexOf('=') + 2;
					this.matchTolerance = Float.parseFloat(line.substring(
					        vindex, line.indexOf(' ', vindex)));
					continue;
				}

				if (line.startsWith("sequence_header_filter")) {
					final int vindex = line.indexOf('=') + 2;
					this.sequenceHeaderFilter = line.substring(vindex);
					continue;
				}

				if (line.startsWith("digest_mass_range")) {
					final int vindex = line.indexOf('=') + 2;
					final int vindex2 = line.indexOf(' ', vindex);

					float massst = Float.parseFloat(line.substring(vindex,
					        vindex2));
					float massed = Float.parseFloat(line.substring(vindex2 + 1)
					        .trim());

					this.digest_mass_range[0] = massst;
					this.digest_mass_range[1] = massed;

					continue;
				}

				if (line.startsWith("add_Cterm_peptide")) {
					int vindex = line.indexOf('=') + 2;
					final int idx2 = line.indexOf(' ', vindex);
					double v = Double.parseDouble(line.substring(vindex, idx2));
					if (v != 0d)
						aminoacids.setCterminalStaticModif(v);

					breader.readLine();// Cterm_protein

					line = breader.readLine();// Nterm_peptide
					v = Double.parseDouble(line.substring(vindex, idx2));
					if (v != 0d)
						aminoacids.setCterminalStaticModif(v);

					breader.readLine();// Nterm_protein;

					// Static modifications
					while ((line = breader.readLine()) != null
					        && line.startsWith("add_")) {
						final char c = line.charAt(4);
						vindex = line.indexOf('=') + 2;
						v = Double.parseDouble(line.substring(vindex, line
						        .indexOf(' ', vindex)));
						if (v != 0d)
							aminoacids.setModification(c, v);
					}

					continue;
				}

				// Only used for old type params
				if (line.startsWith("[SEQUEST_ENZYME_INFO]")) {

					while ((line = breader.readLine()) != null) {
						if (line.trim().length() == 0)
							break;

						String[] cols = StringUtil.split(line, '\t');
						String id = cols[0];
						int idx = Integer.parseInt(id.substring(0,
						        id.length() - 1));
						if (idx == this.enzyme_Num) {
							final boolean offset_after = Integer
							        .parseInt(cols[5]) == 1 ? true : false;
							enzyme = new Enzyme(cols[1], offset_after, cols[6],
							        cols[8]);

							continue;
						}
					}
				}
			}
		} catch (final NumberFormatException ne) {
			throw new ParameterParseException(
			        "sequest.params has garbled number in this line: " + line,
			        ne.getCause());
		} catch (final IOException ioe) {
			throw new ParameterParseException(
			        "Errors occur when reading the params file, may be damaged?",
			        ioe.getCause());
		} catch (final InvalidEnzymeCleavageSiteException e) {
			throw new ParameterParseException(
			        "Errors occur when generating enzyme from the parameter file"
			                + ", may be damaged?", e.getCause());
		} catch (final RuntimeException re) {
			throw new ParameterParseException(
			        "Errors occur when parsing the parameters", re);
		}

		// ---process the variable modification

		SequestVariableMod[] mods = vmods.toArray(new SequestVariableMod[vmods
		        .size()]);
		this.modsymbol = new VariableModSymbols(mods, this.isMonoPeptideMass());

		for (SequestVariableMod mod : mods) {
			this.aamodif.addModifications(mod.getModifiedAt().toArray(
			        new ModSite[0]), modsymbol.getModSymbol(mod), this
			        .isMonoPeptideMass() ? mod.getAddedMonoMass() : mod
			        .getAddedAvgMass(), mod.getName());
		}

		return this;
	}

	/**
	 * Parse the peptide with new defined modification symbols (Refine the
	 * modification symbol of originally designed by the search algorithm). Only
	 * useful when readin from the original. CANNOT be used for peptide list file
	 * 
	 * @param peptidestr
	 * @return
	 */
	public String parsePeptide(String peptidestr) {

//		String seq = PeptideUtil.formatSequence(peptidestr);
		char [] aas = peptidestr.toCharArray();
		char c;
		if(aas[3]==']'){
			c = aas[2];
			aas[2] = aas[3];
			aas[3] = c;
		}
		String seq = new String(aas);
		IModifiedPeptideSequence mseq = ModifiedPeptideSequence
		        .parseSequence(seq);

		IModifSite[] sites = mseq.getModifications();
		
		if (sites == null || sites.length == 0)
			return mseq.getFormattedSequence();

		int len = sites.length;
		int[] loc = new int[len];
		SequestVariableMod[] mods = new SequestVariableMod[len];

		for (int i = 0; i < len; i++) {
			IModifSite site = sites[i];
			loc[i] = site.modifLocation();
			char sym = site.symbol();
			SequestVariableMod mod = this.varsymbolmap.get(sym);

			if (mod == null) {
				throw new NullPointerException("The symbol of '" + sym
				        + "' isn't defined in variable modificiation.");
			}
			mods[i] = mod;
		}

		return this.modsymbol.parseSequence(mseq, mods, loc);
	}

	/**
	 * Get the label type string, e.g. Dimethyl_1.
	 * @param peptidestr
	 * @return
	 */
/*	
	public String getLabelInfo(String peptidestr){
		
		char [] desChar = peptidestr.toCharArray();
		String info = "";
		for(int i=0;i<desChar.length;i++){
			if((desChar[i]>='A' && desChar[i]<='Z')||desChar[i]=='.'||desChar[i]=='-'){
				continue;
			}else{
				LabelType type = this.getLabelType();
				String labelInfo = LabelInfo.getDescrib(type, modsymbol.getAddedMass(desChar[i]));
				if(labelInfo!=null){
					if(info.length()==0){
						info = labelInfo;
					}else{
						if(!info.equals(labelInfo)){
							return "";
						}
					}
				}
			}
		}
		return info;
	}
*/	
	/**
	 * Commonly, if there is no reduction by iaa, there is no static
	 * modification; Set no static modification;
	 */
	public void setNoStaticModification() {
		aminoacids.reset();
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
	 * MwCalculator for the output sequest peptides using this search parameter
	 * 
	 * @return
	 */
	public MwCalculator getMwCalculator() {
		return new MwCalculator(this.aminoacids, this.aamodif);
	}

	/**
	 * Enzyme used for peptide digestion;
	 */
	public Enzyme getEnzyme() {
		return enzyme;
	}

	/**
	 * Get the type of enzymatic cleavage used for peptide identification.
	 * 
	 * @see Enzymes.ENZYMATIC_XXXX
	 * @return
	 */
	public int getEnzymaticType() {
		return this.enzymaticType;
	}

	/**
	 * The number of minmum cleavage site. For full enzymatic, this number will
	 * be 2 and for partial enzymatic 1, No enzyme, 0.
	 */
	public int getMinTermNum() {
		switch (this.enzymaticType) {
		case Enzyme.ENZYMATIC_NO:
			return 0;
		case Enzyme.ENZYMATIC_PARTIAL:
			return 1;
		case Enzyme.ENZYMATIC_FULL:
			return 2;

		}

		throw new IllegalArgumentException(
		        "Currently, the enzymatic type of \"" + this.enzymaticType
		                + "\" is unknown.");
	}

	/**
	 * @return the mass tolerance of precursor ions in database search.
	 */
	public float getPrecursorMassTolerance() {
		return mass_tolerance_pep;
	}

	/**
	 * 0=amu, 1=mmu, 2=ppm
	 * 
	 * @return the unit of precursor mass tolerence used.
	 */
	public int getPrecursorMassUnit() {
		return this.peptide_mass_unit;
	}

	/**
	 * @return If the fragment mass used for database search is average mass.
	 */
	public boolean getFragmentMassAverage() {
		return mass_type_fragment == 0;
	}

	/**
	 * @return the mass tolerance of fragment ions in database search.
	 */
	public float getFragmentMassTolerance() {
		return mass_tolerance_fragment;
	}

	/**
	 * @return the max number of miss cleavage allowed for peptide in the
	 *         database search.
	 */
	public int getMaxMissCleaveSites() {
		return miss_cleavage_site;
	}

	/**
	 * @return the max number of variable modifications per peptide.
	 */
	public int getMaxModifSitePerPeptide() {
		return max_variable_modif;
	}

	/**
	 * @return the number of output peptides in .out file
	 */
	public int getOutputPeptides() {
		return outputPeptides;
	}

	/**
	 * @return the ionseries. (e.g. 0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0)
	 */
	public String getIonseries() {
		return ionseries;
	}

	/**
	 * 0=protein db, 1-6, 7 = forward three, 8-reverse three, 9=all six
	 * 
	 * @return
	 */
	public int getNucleotide_reading_frame() {
		return this.nucleotide_reading_frame;
	}

	/**
	 * Number of peptides for Sp score calculation.
	 * 
	 * @return
	 */
	public int getNumPeptidesForSp() {
		return this.peptideForScore;
	}

	/**
	 * number of auto-detected peaks to try matching (max 5)
	 * 
	 * @return
	 */
	public int getMatchPeakCount() {
		return this.matchPeakCount;
	}

	/**
	 * number of allowed errors in matching auto-detected peaks
	 * 
	 * @return
	 */
	public int getMatchPeakAllowedError() {
		return this.matchPeakAllowedError;
	}

	/**
	 * mass tolerance for matching auto-detected peaks
	 * 
	 * @return
	 */
	public float getMatchTolerance() {
		return this.matchTolerance;
	}

	/**
	 * If the precursor ions peak is removed in fragment spectrum.
	 * 
	 * @return
	 */
	public boolean isRemovePrecursorPeak() {
		return this.removePrecursorPeak;
	}

	/**
	 * Ions cut offf percent
	 * 
	 * @return
	 */
	public float getIonPercentCutoff() {
		return this.ionPercentCutoff;
	}

	/**
	 * Only proteins with mass within this region will be accepted for protein
	 * database search. The returned array has two elements, mass range start
	 * and mass range end. If the protein mass filter is not used, this two
	 * values will both be 0.
	 * 
	 * <p>
	 * <b>Please use the method isProteinMassFilterUsed() to test whether
	 * protein filter is used first.</b>
	 * 
	 * @return
	 */
	public float[] getProteinMassFilter() {
		return this.proteinMassFilter;
	}

	/**
	 * If the protein mass filter is used for protein database search. If true,
	 * use getProteinMassFilter() to get the mass range. If this value if false,
	 * the corresponding mass filter will be {0,0}.
	 * 
	 * @return
	 */
	public boolean isProteinMassFilterUsed() {
		return this.isProteinMassFilterUsed;
	}

	/**
	 * <b>Currently not know how to use. Default value is ""</b>
	 * 
	 * @return the sequence header filter.
	 */
	public String getSequenceHeaderFilter() {
		return this.sequenceHeaderFilter;
	}

	/**
	 * The mass range of theoretical peptides used for database search. Default:
	 * 600 - 3500;
	 * 
	 * @return
	 */
	public float[] getDigestMassRange() {
		return this.digest_mass_range;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.SEQUEST;
	}

	/**
	 * A makeshift can be used if no parameters can be read, e.g. using .xml to create a .ppl file.
	 * 
	 * The only use is in SequestPeptideReaderFactory 401.
	 */
	public void setDefaultPara(){

		HashSet<ModSite> list = new HashSet<ModSite>();
		list.add(ModSite.newInstance_aa('M'));
		
		SequestVariableMod vmod = new SequestVariableMod(
				"Oxidation", 15.994900, 15.994900, list, '*');
		this.varsymbolmap.put(vmod.getSymbol(), vmod);
		this.aamodif.addModification(ModSite.newInstance_aa('M'), '*', 15.994900, "Oxidation");
		this.aminoacids.setCysCarboxyamidomethylation();
	}
	
	public void restore(HashMap <Character, Character> replaceAA){
		char [] cs = this.aminoacids.getModifiedAAs();
		HashMap <Character, Double> modMap = new HashMap<Character, Double>();
		for(int i=0;i<cs.length;i++){
			char c = cs[i];
			if(replaceAA.containsKey(c)){
//				double omass = Aminoacids.getAminoacid(c).getMonoMass();
//				this.aminoacids.setModification(c, 0);
			}else{
				if(c=='c'){
					modMap.put(c, aminoacids.getCterminalStaticModif());
				}else if(c=='n'){
					modMap.put(c, aminoacids.getNterminalStaticModif());
				}else{
					modMap.put(c, aminoacids.getAAInstance(c).getMonoMass());
				}
			}
		}
		this.aminoacids.reset();
		Iterator <Character> it = modMap.keySet().iterator();
		while(it.hasNext()){
			Character ch = it.next();
			if(ch.charValue()=='c'){
				this.aminoacids.setCterminalStaticModif(modMap.get(ch));
			}else if(ch.charValue()=='n'){
				this.aminoacids.setNterminalStaticModif(modMap.get(ch));
			}else{
				this.aminoacids.setModifiedMassForAA(ch, modMap.get(ch));
			}
		}
	}
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public SequestParameter clone() {
		try {
	        return (SequestParameter) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequestParameter deepClone() {
		
		SequestParameter copy = this.clone();
		
		copy.aamodif = this.aamodif.deepClone();
		copy.aminoacids = this.aminoacids.deepClone();
		
		copy.digest_mass_range = this.digest_mass_range.clone();
		copy.enzyme = this.enzyme.deepClone();
		copy.proteinMassFilter = this.proteinMassFilter.clone();
		
		if(this.modsymbol != null) {
			System.err.println("Need to clone the mod symbol");
		}
		
		if(this.varsymbolmap != null) {
			System.err.println("Need to clone the map");
		}
		
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void setStaticInfo(Aminoacids aas) {
		this.aminoacids = aas;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVariableInfo(AminoacidModification aamodif) {
		this.aamodif = aamodif;
	}

	/**
	 * Inner class contains neutral loss infomation; When an peptide contains an
	 * modification which tends to have neutral loss in mass spectralmater, this
	 * class return an instence contains infor what is the symbol for this
	 * modif(In sequence after database search, eg #) and how much weight this
	 * neutral loss loses;
	 * 
	 * @author Xinning
	 * @version 0.1, 09-01-2008, 21:35:38
	 */
	public static class NeutralLoss implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private char symbol = 0;
		private double lossmass = 0D;

		public NeutralLoss(char symbol, double lossmass) {
			this.symbol = symbol;
			this.lossmass = lossmass;
		}

		/**
		 * @return symbol char for this modification which is easy to lose
		 *         neutral fragment in the sequence after db search(eg #)
		 */
		public char symbol() {
			return symbol;
		}

		/**
		 * @return a double value which indicated the mass losed when the
		 *         neutral loss happens;
		 */
		public double lossmass() {
			return lossmass;
		}
	}

	/**
	 * @return A object contains symbol used for the neutral loss
	 *         modification(eg phosphate) After Sequest search, these symbols
	 *         were often assigned to # @ and so on; and the mass losed when the
	 *         neutral loss happens;
	 */
	// public NeutralLoss getNeutralSymbol() {
	// return neutralLoss;
	// }
	public static void main(String[] args) throws ParameterParseException {
		final SequestParameter paramter = new SequestParameter(
		        "E:\\Data\\SCX-ONLINE-DIMETHYL_final\\dime.params");
		paramter.setLabelType(LabelType.Dimethyl);
		System.out.println(paramter.varsymbolmap);
//		System.out.println(paramter.getLabelInfo("R.G#LSEGLPRMCTR"));
	}
}
