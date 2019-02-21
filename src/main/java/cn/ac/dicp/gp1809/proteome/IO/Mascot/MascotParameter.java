/*
 * *****************************************************************************
 * File: MascotParameter.java * * * Created on 11-16-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.Enzymes;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.Masses;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.Parameters;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.*;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Configuration for Mascot database search
 * 
 * @author Xinning
 * @version 0.1.5, 08-16-2010, 12:42:18
 */
public class MascotParameter extends AbstractParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Enzyme enzyme;
	//This map is useless for the outputted peptide list file
	private transient HashMap<Integer, MascotVariableMod> vmodmap;
	private HashSet <MascotFixMod> fixMods;
	private HashSet <MascotVariableMod> variMods;
	private boolean isMonoMass;
	
	private Aminoacids aas;
	private AminoacidModification aamodif;
	private VariableModSymbols modsymbol;

	public MascotParameter(Parameters parameters, Masses masses, Enzymes enzymes)
	        throws ModsReadingException, InvalidEnzymeCleavageSiteException {
		this.enzyme = this.parseEnzyme(enzymes);
		this.setDatabase(parameters.getDatabase());
		this.setPeptideMass(parameters.isMono_isotope());
		this.parseModif(masses);
	}
	
	public MascotParameter(HashSet <MascotFixMod> fixMods, 
			HashSet <MascotVariableMod> variMods, Enzymes enzymes, boolean isMonoMass)
			throws ModsReadingException, InvalidEnzymeCleavageSiteException{
		this.fixMods = fixMods;
		this.variMods = variMods;
		this.enzyme = this.parseEnzyme(enzymes);
		this.isMonoMass = isMonoMass;
		this.parseModif();
	}

	/**
	 * Parse the Enzyme
	 * 
	 * @param enzymes
	 * @return
	 * @throws InvalidEnzymeCleavageSiteException
	 */
	private Enzyme parseEnzyme(Enzymes enzymes)
	        throws InvalidEnzymeCleavageSiteException {
		return new Enzyme(enzymes.getTitle(), enzymes.isSense_Cterm(), enzymes
		        .getCleavage(), enzymes.getRestrict());
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
	 * Parse the sequence into formatted peptide sequence.
	 * 
	 * A.AAAAA#AAAA.A
	 * 
	 * @param raw
	 *            the raw PeptideSequence
	 * @param modifs
	 *            the modification IDs on this peptide
	 * @param modif_at
	 *            the modified aminoacid indexes according to the modifs.
	 * @return
	 */
	public String parseSequence(PeptideSequence raw, int[] modifs,
	        int[] modif_at) {
		
		MascotVariableMod[] mods = null;
		if (modifs != null && modifs.length > 0) {
			int len = modifs.length;
			mods = new MascotVariableMod[len];

			for (int i = 0; i < len; i++) {
				mods[i] = this.vmodmap.get(modifs[i]);
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
	 * Parse the modification. <li>1. "Protein terminal modifications" and
	 * "N-term X" like modifications are always the variable modifications. <li>
	 * 2.
	 * 
	 * @param masses
	 */
	private void parseModif(Masses masses) {

		Aminoacids aas = new Aminoacids();
		AminoacidModification aamodif = new AminoacidModification();

		MascotVariableMod[] vmods = this.parseVariableMod(masses
		        .getVariableModifications());
		MascotFixMod[] fmods = this.parseFixMod(masses.getFixedModifications());
		boolean mono_precursor = this.isMonoPeptideMass();
		if (fmods != null && fmods.length > 0) {

			for (MascotFixMod fmod : fmods) {
				HashSet<ModSite> list = fmod.getModifiedAt();
				double add = mono_precursor ? fmod.getAddedMonoMass() : fmod
				        .getAddedAvgMass();

				for (Iterator<ModSite> it = list.iterator(); it.hasNext();) {
					aas.setModification(it.next(), add);
				}
			}
		}

		if (vmods != null && vmods.length > 0) {

			this.modsymbol = new VariableModSymbols(vmods, mono_precursor);

			for (int i = 0; i < vmods.length; i++) {
				MascotVariableMod vmod = vmods[i];
				char symbol = this.modsymbol.getModSymbol(vmod);
				double add = mono_precursor ? vmod.getAddedMonoMass() : vmod
				        .getAddedAvgMass();
				String name = vmod.getName();
				aamodif.addModification(symbol, add, name);
				HashSet <ModSite> modset = vmod.getModifiedAt();
				ModSite [] sites = modset.toArray(new ModSite[modset.size()]);
				aamodif.addModifications(sites, symbol, add, name);
			}
		}

		this.aas = aas;
		this.aamodif = aamodif;

		this.vmodmap = this.generateVariableModMap(vmods);
	}

	private void parseModif() {

		Aminoacids aas = new Aminoacids();
		AminoacidModification aamodif = new AminoacidModification();
		
		MascotVariableMod [] variModArrays = variMods.toArray(new MascotVariableMod[variMods.size()]);
		
/*		MascotVariableMod [] variModArrays = new MascotVariableMod [variMods.size()];
		Iterator variMIt = variMods.iterator();
		int num = 0;
		while(variMIt.hasNext()){
			variModArrays[num] = (MascotVariableMod) variMIt.next();
			num++;
		}
*/		
		boolean mono_precursor = this.isMonoMass;
		
		if (fixMods!= null && fixMods.size() > 0) {

			for (MascotFixMod fmod : fixMods) {
				HashSet<ModSite> list = fmod.getModifiedAt();
				double add = mono_precursor ? fmod.getAddedMonoMass() : fmod
				        .getAddedAvgMass();

				for (Iterator<ModSite> it = list.iterator(); it.hasNext();) {
					aas.setModification(it.next(), add);
				}
			}
		}

		if (variModArrays!= null && variModArrays.length> 0) {
			
			this.modsymbol = new VariableModSymbols(variModArrays, mono_precursor);

			for (int i = 0; i < variModArrays.length; i++) {
				MascotVariableMod vmod = variModArrays[i];
				char symbol = this.modsymbol.getModSymbol(vmod);
				double add = mono_precursor ? vmod.getAddedMonoMass() : vmod
				        .getAddedAvgMass();
				String name = vmod.getName();
				
				HashSet <ModSite> sites = vmod.getModifiedAt();
				Iterator <ModSite> it = sites.iterator();
				while(it.hasNext()){
					ModSite s = it.next();
					aamodif.addModification(s, symbol, add, name);
				}				
			}
		}

		this.aas = aas;
		this.aamodif = aamodif;

		this.vmodmap = this.generateVariableModMap(variModArrays);
	}

	/**
	 * Create a id->instance map for fast getting of mods through the
	 * modification id.
	 * 
	 * @return
	 */
	private HashMap<Integer, MascotVariableMod> generateVariableModMap(
	        MascotVariableMod[] mods) {
		HashMap<Integer, MascotVariableMod> map = new HashMap<Integer, MascotVariableMod>();
		if (mods == null || mods.length == 0)
			return map;

		for (MascotVariableMod mod : mods) {
			Integer key = mod.getIndex();
			if (map.get(key) != null) {
				throw new IllegalArgumentException(
				        "The variable modification: \"" + mod + "\" and \""
				                + map.get(key)
				                + "\" are with the same modification ID");
			}

			map.put(key, mod);
		}

		return map;
	}

	/**
	 * Create a Vector containing all the variable modification instances.
	 */
	private MascotVariableMod[] parseVariableMod(String[] aVModStringArrayList) {

		MascotVariableMod[] vmods = new MascotVariableMod[aVModStringArrayList.length];

		// Run every element in the aVModStringArrayList trough a for loop.
		// Create a VariableModification instance at the end of each loop.
		// Put the instance into the vector.
		for (int i = 0; i < aVModStringArrayList.length; i++) {
			// Save the value of the StringArrayList in this variable and
			// refresh each time it gets trough the loop.
			String lStringV = aVModStringArrayList[i];
			String st[] = StringUtil.split(lStringV, ',');
			MascotVariableMod lV = null;

			// 1. First element in the String is the mass.
			double lMass = Double.parseDouble(st[0]);

			// 2. Second element in the String is the type AND location.
			String lTypeAndLocation = st[1].trim();

			// 2.b)
			// throw lTypeAndLocation to parseVariableModName method and
			// get a String[] in return containing the type @[0] and the
			// location @[1]
			String[] lNameArray = parseVariableModName(lTypeAndLocation);
			String lType = lNameArray[0].trim();
			HashSet<ModSite> lLocation = this.parseLocalization(lNameArray[1]
			        .trim());

			// 3. The ModificationID runs parrallel with the for-loop.
			int lModificationID = i + 1;

			// 4. Third element in the StringTokenizer is the (possible)
			// Neutralloss.
			double lNeutralLoss = Double.parseDouble(st[2]);

			if (lNeutralLoss != 0d) {
				// 5. Finally create a new Variable modification instance.
				lV = new MascotVariableMod(lModificationID, lType, lMass,
				        lMass, lLocation, true, lNeutralLoss, lNeutralLoss);
			} else {
				// 5. Finally create a new Variable modification instance
				// without neutral loss
				lV = new MascotVariableMod(lModificationID, lType, lMass,
				        lMass, lLocation);
			}
			// 6. add the instance to this this class its VariableModification
			// Vector.
			vmods[i] = lV;
		}

		return vmods;
	}

	/**
	 * Create a Vector containing all the variable modification instances.
	 */
	private MascotFixMod[] parseFixMod(String[] aFModStringArrayList) {

		MascotFixMod[] fmods = new MascotFixMod[aFModStringArrayList.length];

		// Run every element in the aFModStringArrayList trough a for loop.
		// Create a FixedModification instance at the end of each loop.
		// Put the instance into the vector.
		for (int i = 0; i < aFModStringArrayList.length; i++) {
			// Save the value of the StringArrayList in this variable and
			// refresh each time it gets trough the loop.
			String lStringF = aFModStringArrayList[i];
			String[] st = StringUtil.split(lStringF, ",");
			MascotFixMod lF = null;

			// 1. First element in the StringTokenizer is the mass.
			double lMass = Double.parseDouble(st[0]);

			// 2. Second element in the StringTokenizer is the type and
			// location.
			String lTypeAndLocation = st[1].trim();
			// We only need the type because the location is allready in the
			// next token.
			String lType = parseFixedModName(lTypeAndLocation);

			// 3. Third element in the StringTokenizer is the location.
			HashSet<ModSite> lLocation = this.parseLocalization(st[2]);

			// 4. The ModificationID runs parrallel with the for-loop.
			int lModificationID = i + 1;

			// 5. Finally create a new Fixed modification instance.
			lF = new MascotFixMod(lModificationID, lType, lMass, lMass,
			        lLocation);

			// 6. add the instance to this this class its VariableModification
			// Vector.
			fmods[i] = lF;
		}

		return fmods;
	}

	/**
	 * Parse the mascot formatted localization into well formatted localization
	 * for modification.
	 * <p>
	 * We think that the only modifications at specific aminoacid (but not the
	 * terminal or terminal aminoaicid, e.g. N-(_)term or N-(_)term Q) can be
	 * merged together. In other words, if a modification in a single defining
	 * can occurs at more than one points (e.g. phospho (ST)), these points are
	 * specific aminoacid. (No modification is defined like (N-term QP))
	 * <p>
	 * For example, N-term --> n, Protein N-term --> -n
	 * 
	 * @param localMs
	 * @return
	 */
	private HashSet<ModSite> parseLocalization(String loc) {

		HashSet<ModSite> modlist = new HashSet<ModSite>();

		// Variable modification
		if (loc.startsWith("N-term")) {
			if (loc.length() == 6) {
				modlist.add(ModSite.newInstance_PepNterm());
			} else {
				String s = loc.substring(6).trim();

				for (int i = 0; i < s.length(); i++) {
					modlist.add(ModSite.newInstance_PepNterm_aa(s.charAt(i)));
				}
			}
		}

		// Variable modification
		else if (loc.startsWith("C-term")) {
			if (loc.length() == 6) {
				modlist.add(ModSite.newInstance_PepCterm());
			} else {
				String s = loc.substring(6).trim();

				for (int i = 0; i < s.length(); i++) {
					modlist.add(ModSite.newInstance_PepCterm_aa(s.charAt(i)));
				}
			}
		}

		// Fix modification
		else if (loc.startsWith("N_term")) {
			if (loc.length() == 6) {
				modlist.add(ModSite.newInstance_PepNterm());
			} else {
				throw new IllegalArgumentException("The modification at " + loc
				        + " should not be a fixed modification");
			}
		}

		// Fix modification
		else if (loc.startsWith("C_term")) {
			if (loc.length() == 6) {
				modlist.add(ModSite.newInstance_PepCterm());
			} else {
				throw new IllegalArgumentException("The modification at " + loc
				        + " should not be a fixed modification");
			}
		}

		// Variable modification
		else if (loc.startsWith("Protein C-term")) {
			if (loc.length() == 6) {
				modlist.add(ModSite.newInstance_ProCterm());
			} else {
				String s = loc.substring(6).trim();

				for (int i = 0; i < s.length(); i++) {
					modlist.add(ModSite.newInstance_ProCterm_aa(s.charAt(i)));
				}
			}
		}

		// Variable modification
		else if (loc.startsWith("Protein N-term")) {
			if (loc.equals("Protein N-term")) {
//			if (loc.length() == 6) {
				modlist.add(ModSite.newInstance_ProNterm());
			} else {
				String s = loc.substring(6).trim();

				for (int i = 0; i < s.length(); i++) {
					modlist.add(ModSite.newInstance_ProNterm_aa(s.charAt(i)));
				}
			}
		} else {
			String s = loc.trim();

			for (int i = 0; i < s.length(); i++) {
				modlist.add(ModSite.newInstance_aa(s.charAt(i)));
			}
		}

		return modlist;
	}

	/**
	 * Method Parse the name string of a variable modidication into type of
	 * modifiction(ex: 'Acetyl') and the location(ex:'N-term')
	 * 
	 * @param aName
	 *            String with modificationType and modificationLocation.
	 * @return String[] with the parsed modificationType and
	 *         modificationLocation. input example: 'Pyro-cmC (N-term camC)'
	 *         returns [0]='Pyro-cmC' [1]='N-term camC'
	 */
	private String[] parseVariableModName(String aName) {
		String[] lNameArray = new String[2];
		int lBeginIndex = aName.lastIndexOf('(');
		if (lBeginIndex > 0) {
			int lEndIndex = aName.lastIndexOf(')');
			lNameArray[0] = aName.substring(0, (lBeginIndex));
			lNameArray[1] = aName.substring(lBeginIndex + 1, lEndIndex);
		} else {
			lNameArray[0] = aName;
			lNameArray[1] = null;
		}
		return lNameArray;
	}

	/**
	 * Method Parse the name string of a fixed modidication into type of
	 * modifiction(ex: 'Acetyl_heavy').
	 * 
	 * @param aName
	 *            String with modificationType( and modificationLocation).
	 * @return String with the parsed modificationType. input example: 'Arg
	 *         6xC(13) (R)' returns 'Arg 6xC(13)'
	 */
	private String parseFixedModName(String aName) {
		aName = aName.trim(); // Cut of the leading whitespace.
		int lEndIndex = aName.lastIndexOf('(') - 1; // Find the last ')'
		// bracket; minus 1 is the
		// end of the Mod type.
		if (lEndIndex >= 0) {
			aName = aName.substring(0, lEndIndex);
		}
		return aName;
	}

	@Override
	public PeptideType getPeptideType() {
		return PeptideType.MASCOT;
	}

	@Override
	public MascotParameter clone() {
		try {
	        return (MascotParameter) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}
	
	@Override
	public MascotParameter deepClone() {
		
		MascotParameter copy = this.clone();
		
		copy.aamodif = this.aamodif.deepClone();
		copy.aas = this.aas.deepClone();
		
		copy.enzyme = this.enzyme.deepClone();
		
		if(this.modsymbol != null) {
			System.err.println("Need to clone the mod symbol");
		}
		
		if(this.vmodmap != null) {
			System.err.println("Need to clone the map");
		}
		
		return copy;
	}

	@Override
    public void setStaticInfo(Aminoacids aas) {
		this.aas = aas;
    }

	@Override
    public void setVariableInfo(AminoacidModification aamodif) {
		this.aamodif = aamodif;
    }

	@Override
	public void restore(HashMap <Character, Character> replaceAA){
		char [] cs = this.aas.getModifiedAAs();
		HashMap <Character, Double> modMap = new HashMap<Character, Double>();
		for(int i=0;i<cs.length;i++){
			char c = cs[i];
			if(replaceAA.containsKey(c)){
//				double omass = Aminoacids.getAminoacid(c).getMonoMass();
//				this.aminoacids.setModification(c, 0);
			}else{
				if(c=='c'){
					modMap.put(c, aas.getCterminalStaticModif());
				}else if(c=='n'){
					modMap.put(c, aas.getNterminalStaticModif());
				}else{
					modMap.put(c, aas.getAAInstance(c).getMonoMass());
				}
			}
		}
		this.aas.reset();
		Iterator <Character> it = modMap.keySet().iterator();
		while(it.hasNext()){
			Character ch = it.next();
			if(ch.charValue()=='c'){
				this.aas.setCterminalStaticModif(modMap.get(ch));
			}else if(ch.charValue()=='n'){
				this.aas.setNterminalStaticModif(modMap.get(ch));
			}else{
				this.aas.setModifiedMassForAA(ch, modMap.get(ch));
			}
		}
	}
}
