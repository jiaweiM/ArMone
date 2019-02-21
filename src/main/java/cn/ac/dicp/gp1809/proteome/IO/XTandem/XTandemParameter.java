/*
 * *****************************************************************************
 * File: XTandemParameter.java * * * Created on 10-07-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.XTandem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.*;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Parameters used in XTandem database search.
 * 
 * @author Xinning
 * @version 0.2, 05-02-2010, 23:02:12
 */
public class XTandemParameter extends AbstractParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final static QName label = new QName("label");
	
	private XTandemMod[] variblemods = new XTandemMod[0];
	
	private XTandemMod[] staticmods = new XTandemMod[0];
	
	private HashMap<Double, XTandemMod> variablemassmap;
	
	private HashMap<Double, XTandemMod> staticmassmap;
	
	private boolean ismono = true;
	private double TOL = 0.01;
	
	
	private Aminoacids aminoacids = null;
	private AminoacidModification aamodif = null;

	private VariableModSymbols modsymbol = new VariableModSymbols();

	// per peptide;

	private Enzyme enzyme = Enzyme.TRYPSIN;

	/**
	 * A null parameter, must get the parameter from params file
	 */
//	public XTandemParameter() {
//		this.initial();
//	}

	/**
	 * Get parameter instance from sequest parameter files;
	 * 
	 * @param param
	 * @throws ParameterParseException
	 */
	public XTandemParameter(InputStream stream) throws ParameterParseException {
		this.initial();
		this.loadFromStream(stream);
	}

	/**
	 * Get parameter instance from sequest parameter files;
	 * 
	 * @param param
	 * @throws ParameterParseException
	 */
	public XTandemParameter(File cfgxml, File defaultcfgxml)
	        throws ParameterParseException {
		this.initial();

		this.loadFromFile(cfgxml, defaultcfgxml);
	}

	private void initial() {
		aminoacids = new Aminoacids();
		aamodif = new AminoacidModification();
		
		variablemassmap = new HashMap<Double, XTandemMod>();
		staticmassmap = new HashMap<Double, XTandemMod>();
	}

	/**
	 * Get Parameter from XTandem configure parameter files. Two configuration
	 * xml files can be specified. If only one configuration xml is used for
	 * database search, set as any one of the two files and leave another one as
	 * null.
	 * <p>
	 * Notice: if the two parameter files contains the same entry, the values
	 * defined by the first one(cfgxml) will be used. Same as the input.xml and
	 * default_input.xml.
	 * 
	 * @param cfgxml
	 *            the user defined xml file.
	 * @param defaultcfgxml
	 *            the default xml file.
	 * @return
	 * @throws ParameterParseException
	 */
	public void loadFromFile(File cfgxml, File defaultcfgxml)
	        throws ParameterParseException {
		try {

			if (cfgxml == null && defaultcfgxml == null)
				throw new NullPointerException(
				        "At least one configuration xml file must be specified.");

			if (defaultcfgxml != null) {
				System.out.println("Loading parameter file: "
				        + defaultcfgxml.getName());
				this.loadFromStream(new FileInputStream(defaultcfgxml));
			}

			if (cfgxml != null) {
				System.out.println("Loading parameter file: "
				        + cfgxml.getName());
				this.loadFromStream(new FileInputStream(cfgxml));
			}

		} catch (Exception e) {
			throw new ParameterParseException(e);
		}
	}

	/**
	 * Get Parameter from stream containing X!Tandem parameters. 
	 * 
	 * @param param
	 * @return
	 * @throws ParameterParseException
	 * @throws IOException
	 */
	public void loadFromStream(InputStream paramStream)
	        throws ParameterParseException {
		String caption = null;
		String value = null;
		try {
			
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader reader = factory.createXMLEventReader(paramStream);

			XMLEvent event;
			while (!(event = reader.nextEvent()).isEndDocument()) {
				if (event.isStartElement()) {
					StartElement se = ((StartElement) event);
					Attribute att = se.getAttributeByName(label);
					if (att != null) {
						caption = att.getValue();

						event = reader.nextEvent();
						// If the next value is not the character, indicating
						// the null value
						if (event.isCharacters()) {
							value = ((Characters) event).getData();
						} else {
							continue;
						}

						if (caption.equals("list path, sequence source #1")) {
							this.setDatabase(value);
							continue;
						}
						
						if(caption.equals("spectrum, fragment mass type")){
							boolean monomass = false;
							if(value.equalsIgnoreCase("average"))
								monomass = false;
							
							else if(value.equalsIgnoreCase("monoisotopic"))
								monomass = true;
							else
							throw new IllegalArgumentException("Illegal mass type: "+value);
							
							this.setPeptideMass(monomass);
							continue;
						}

						if (caption.equals("protein, cleavage site")) {
							this.enzyme = this.parseEnzyme(value);
							continue;
						}

						if (caption.equals("residue, modification mass")) {

							if (value != null) {
								XTandemMod[] mods = parseModification(value);
								
								for (XTandemMod mod : mods) {
									HashSet<ModSite> sites = mod.getModifiedAt();
									for(ModSite site : sites){
										this.aminoacids.setModification(site, mod
									        .getAddedMonoMass());
									}
								}
								
								this.staticmods = mods;
							}

							continue;
						}

						if (caption
						        .equals("residue, potential modification mass")) {

							if (value != null) {
								XTandemMod[] mods = parseModification(value);

								this.modsymbol = new VariableModSymbols(mods,
								        true);

								for (XTandemMod mod : mods) {
									//For xtandem, mono and avg are the same
									double mono_mass = mod.getAddedMonoMass();
									aamodif.addModifications(
											mod.getModifiedAt().toArray(new ModSite[0]), 
											this.modsymbol.getModSymbol(mod), 
											mono_mass, mod.getName());
									
								}
								
								this.variblemods = mods;
							}

							continue;
						}
						
						if (caption
						        .equals("residue, potential modification motif")) {
							
							if (value != null) {
								throw new RuntimeException("Currently, the parser for regular pattern " +
										"to process the \"residue, potential modification motif\" type " +
										"modification has no been designed, please use the \"potnetial " +
										"modification mass\" instead.");
							}
							
							continue;
							
						}

					}
				}
			}
		} catch (final NumberFormatException ne) {
			throw new ParameterParseException(
			        "The parameters has garbled number for the entry: "
			                + caption + " with value: " + value, ne.getCause());
		} catch (final InvalidEnzymeCleavageSiteException e) {
			throw new ParameterParseException(
			        "Errors occur when generating enzyme from the parameter file"
			                + ", may be damaged?", e.getCause());
		} catch (final RuntimeException re) {
			throw new ParameterParseException(re);
		} catch (XMLStreamException e) {
			throw new ParameterParseException(
			        "Errors occur when parsing the parameters", e);
		}
	}

	private static Pattern ez = Pattern
	        .compile("[\\{\\[]\\p{Upper}+[\\]\\}]\\|[\\{\\[]\\p{Upper}+[\\]\\}]");

	/**
	 * Parse the cleavage information into enzyme
	 * 
	 * @param enstr
	 * @return
	 * @throws InvalidEnzymeCleavageSiteException
	 */
	private Enzyme parseEnzyme(String enstr)
	        throws InvalidEnzymeCleavageSiteException {
		if (enstr == null || enstr.length() == 0)
			throw new NullPointerException(
			        "The enzymatic cleavage pattern must be specified");

		String[] cleavages = StringUtil.split(enstr, ',');

		if (cleavages.length > 1) {
			throw new IllegalArgumentException(
			        "Currently, only enzyme with single enzymatic "
			                + "pattern is acceptable: " + enstr);
		}

		// current recognized patterns
		if (ez.matcher(enstr).matches()) {

			// no enzyme
			if (enstr.equals("[X]|[X]")) {
				return new Enzyme(enstr, true, "", "");
			}
			// e.g. [X]|[D]
			else if (enstr.matches("\\[X\\]\\|\\[\\p{Upper}+\\]")) {
				boolean sense_c = false;
				String cleave = enstr.substring(5, enstr.length() - 1);

				return new Enzyme(enstr, sense_c, cleave, "");
			}
			// e.g. [D]|[X]
			else if (enstr.matches("\\[\\p{Upper}+\\]\\|\\[X\\]")) {
				boolean sense_c = true;
				String cleave = enstr.substring(1, enstr.length() - 5);

				return new Enzyme(enstr, sense_c, cleave, "");
			}
			// e.g. [KR]|{P}
			else if (enstr.matches("\\[\\p{Upper}+\\]\\|\\{\\p{Upper}+\\}")) {
				boolean sense_c = true;
				int idx = enstr.indexOf('|');
				String cleave = enstr.substring(1, idx - 1);
				String notcleave = enstr.substring(idx + 2, enstr.length() - 1);

				return new Enzyme(enstr, sense_c, cleave, notcleave);
			}

		}

		throw new IllegalArgumentException(
		        "Current enzymatic pattern is unrecognizable: " + enstr
		                + ", please contact the author.");
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

	private static Pattern MODIF = Pattern.compile("[\\d\\.]+@\\p{Upper}");

	/*
	 * The modification string must be ddd@x, ddd@x @param modif
	 */
	private static XTandemMod[] parseModification(String modif) {
		String[] modifs = StringUtil.split(modif, ',');
		int len = modifs.length;
		ArrayList<XTandemMod> mods = new ArrayList<XTandemMod>(len);
		//key = addmass
		HashMap<String, XTandemMod> map = new HashMap<String, XTandemMod>();
		
		for (int i = 0; i < len; i++) {
			String mod = modifs[i].trim();

			if (!MODIF.matcher(mod).matches()) {
				throw new IllegalArgumentException(
				        "Currently, the modifications acceptable should be \"ddd.ddd@A, "
				                + "ddd.dd@A\" (e.g. 57@C), current: " + mod);
			}

			
			String addstr = mod.substring(0, mod.length() - 2);
			char modifAt = mod.charAt(mod.length() - 1);
			
			//Merge same modification at different sites together
			XTandemMod xmod = map.get(addstr);
			if(xmod != null){
				xmod.getModifiedAt().add(ModSite.newInstance_aa(modifAt));
			}
			else{
				double add = Double.parseDouble(addstr);

				HashSet<ModSite> list = new HashSet<ModSite>();
				list.add(ModSite.newInstance_aa(modifAt));
				xmod = new XTandemMod(list, add, add);
				// The same value for mono isotope and average modification
				mods.add(xmod);
				map.put(addstr, xmod);
			}
		}

		return mods.toArray(new XTandemMod[mods.size()]);
	}
	
	/*
	 * <note label="residue, potential modification motif" type="input">80@[STY!]</note>
	 * 
	 * The modification string must be ddd@x, ddd@x @param modif
	 */
	@SuppressWarnings("unused")
    private static XTandemMod[] parseModificationModif(String modif) {
		String[] modifs = StringUtil.split(modif, ',');
		int len = modifs.length;
		ArrayList<XTandemMod> mods = new ArrayList<XTandemMod>(len);
		//key = addmass
		HashMap<String, XTandemMod> map = new HashMap<String, XTandemMod>();
		
		for (int i = 0; i < len; i++) {
			String mod = modifs[i].trim();

			if (!MODIF.matcher(mod).matches()) {
				throw new IllegalArgumentException(
				        "Currently, the modifications acceptable should be \"ddd.ddd@A, "
				                + "ddd.dd@A\" (e.g. 57@C), current: " + mod);
			}

			
			String addstr = mod.substring(0, mod.length() - 2);
			char modifAt = mod.charAt(mod.length() - 1);
			
			//Merge same modification at different sites together
			XTandemMod xmod = map.get(addstr);
			if(xmod != null){
				xmod.getModifiedAt().add(ModSite.newInstance_aa(modifAt));
			}
			else{
				double add = Double.parseDouble(addstr);

				HashSet<ModSite> list = new HashSet<ModSite>();
				list.add(ModSite.newInstance_aa(modifAt));
				xmod = new XTandemMod(list, add, add);
				// The same value for mono isotope and average modification
				mods.add(xmod);
				map.put(addstr, xmod);
			}
		}

		return mods.toArray(new XTandemMod[mods.size()]);
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
	 * If the current modification variable modification. This is determined by
	 * the mass of modification
	 * 
	 * @param mod
	 * @return
	 */
	public boolean isVariableModif(double add) {
		return this.getVariableModForMass(add)!= null;
	}
	
	/**
	 * If the current modification variable modification. This is determined by
	 * the mass of modification
	 * 
	 * @param mod
	 * @return
	 */
	public boolean isFixModif(double add) {
		return this.getModWithTolerance(add, this.staticmods, this.staticmassmap, TOL, ismono)!= null;
	}
	
	
	/**
	 * Get the mod for the mass with tolerance
	 * 
	 * @param mass
	 * @param mods
	 * @param map
	 * @param tol
	 * @param ismono
	 * @return
	 */
	private XTandemMod getModWithTolerance(double mass, XTandemMod[] mods, HashMap<Double, XTandemMod> map, double tol, boolean ismono) {
		
		XTandemMod mod = map.get(mass);
		if(mod != null) {
			return mod;
		}
		
		for(XTandemMod m : mods) {
			double ma = ismono ? m.getAddedMonoMass() : m.getAddedAvgMass();
			double diff = Math.abs(mass-ma);
			if(diff <= tol) {
				map.put(mass, m);
				return m;
			}
		}
		
		return null;
		
	}

	/**
	 * Get the variable modification instance for the added mass. If this added 
	 * mass is not a predefined variable modification (may be fix mod ?), null 
	 * will be returned.
	 * 
	 * @param add
	 * @return
	 */
	public XTandemMod getVariableModForMass(double add){
		return this.getModWithTolerance(add, this.variblemods, this.variablemassmap, TOL, ismono);
	}

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter#getPeptideType()
	 */
	@Override
    public PeptideType getPeptideType() {
	    return PeptideType.XTANDEM;
    }

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public XTandemParameter clone() {
		try {
	        return (XTandemParameter) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public XTandemParameter deepClone() {
		
		XTandemParameter copy = this.clone();
		
		copy.aamodif = this.aamodif.deepClone();
		copy.aminoacids = this.aminoacids.deepClone();
		
		copy.enzyme = this.enzyme.deepClone();
		
		if(this.modsymbol != null) {
			System.err.println("Need to clone the mod symbol");
		}
		
//		if(this.vmodmap != null) {
//			System.err.println("Need to clone the map");
//		}
		
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
	
	public static void main(String[] args) throws ParameterParseException {
		
		final XTandemParameter parameter = new XTandemParameter(new File(
		        "d:\\tandem\\input.xml"), new File(
		        "d:\\tandem\\my_default_input.xml"));

		parameter.getDatabase();
		
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
