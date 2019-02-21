/* 
 ******************************************************************************
 * File: OMSSAMod.java * * * Created on 09-03-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.dbsearch.DefaultMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite.ModType;

/**
 * Modifications in OMSSA database search.
 * 
 * @author Xinning
 * @version 0.2.0.1, 03-25-2009, 19:51:34
 */
public class OMSSAMod extends DefaultMod implements IOMSSAMod, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int index;
	private String description;

	// unknown how to use
	private double addedN15Mass;
	private boolean isNeutralloss;
	private double lossMonoMass;
	private double lossAvgMass;
	private double lossN15Mass;
	private HashSet <ModSite> modset;
	

	/**
	 * @param index
	 *            the index in
	 * @param name
	 *            name of this modification
	 * @param description
	 *            this description will be shown in output file to indicate the
	 *            modification
	 * @param addedMonoMass
	 *            the added mono isotope mass of this modification
	 * @param addedAvgMass
	 *            the added average mass of this modification
	 * @param addedN15Mass:
	 *            unknown???
	 * @param modifiedAt
	 *            the modified aminoacid
	 * @param modtypename
	 *            the name of the modification type
	 * @param modtypeidx
	 *            the index of the modification type
	 */
	public OMSSAMod(int index, String name, String description,
	        double addedMonoMass, double addedAvgMass, double addedN15Mass,
	        String modifiedAt, String modtypename, int modtypeidx) {

		super(name, addedMonoMass, addedAvgMass, getModeSites(modifiedAt,
		        modtypename, modtypeidx));

		this.index = index;
		this.description = description;
		this.addedN15Mass = addedN15Mass;
		
		this.modset = getModeSites(modifiedAt, modtypename, modtypeidx);
	}

	/**
	 * @param index
	 *            the index in
	 * @param name
	 *            name of this modification
	 * @param description
	 *            this description will be shown in output file to indicate the
	 *            modification
	 * @param addedMonoMass
	 *            the added mono isotope mass of this modification
	 * @param addedAvgMass
	 *            the added average mass of this modification
	 * @param addedN15Mass:
	 *            unknown???
	 * @param modifiedAt
	 *            the modified aminoacid
	 * @param isNeutralloss
	 *            if has neutral loss
	 * @param lossMonoMass
	 *            the lost mono isotope mass of neutral loss
	 * @param lossAvgMass
	 *            the lost average mass of neutral loss
	 * @param modtypename
	 *            the name of the modification type
	 * @param modtypeidx
	 *            the index of the modification type
	 */
	public OMSSAMod(int index, String name, String description,
	        double addedMonoMass, double addedAvgMass, double addedN15Mass,
	        String modifiedAt, boolean isNeutralloss, double lossMonoMass,
	        double lossAvgMass, double lossN15Mass, String modtypename,
	        int modtypeidx) {

		this(index, name, description, addedMonoMass, addedAvgMass,
		        addedN15Mass, modifiedAt, modtypename, modtypeidx);
		
		this.isNeutralloss = isNeutralloss;
		this.lossMonoMass = lossMonoMass;
		this.lossAvgMass = lossAvgMass;
		this.lossN15Mass = lossN15Mass;
		
	}

	/**
	 * Parse the modification sites
	 * 
	 * @param modifiedAt
	 * @param modtypename
	 * @param modtyepidx
	 * @return
	 */
	private static HashSet<ModSite> getModeSites(String modifiedAt,
	        String modtypename, int modtypeidx) {
		
		ModType type = getModType(modtypeidx, modtypename);

		int len = modifiedAt == null || modifiedAt.length() == 0 ? 1
		        : modifiedAt.length();
		HashSet<ModSite> list = new HashSet<ModSite>(len);

		switch (type) {
		case modcp:
			list.add(ModSite.newInstance_PepCterm());
			break;
		case modnp:
			list.add(ModSite.newInstance_PepNterm());
			break;
		case modaa: {
			for (int i = 0; i < len; i++) {
				list.add(ModSite.newInstance_aa(modifiedAt.charAt(i)));
			}
			break;
		}
		case modc:
			list.add(ModSite.newInstance_ProCterm());
			break;
		case modn:
			list.add(ModSite.newInstance_ProNterm());
			break;
		case modcaa: {
			for (int i = 0; i < len; i++) {
				list.add(ModSite.newInstance_ProCterm_aa(modifiedAt.charAt(i)));
			}
			break;
		}
		case modnaa: {
			for (int i = 0; i < len; i++) {
				list.add(ModSite.newInstance_ProNterm_aa(modifiedAt.charAt(i)));
			}
			break;
		}
		case modcpaa: {
			for (int i = 0; i < len; i++) {
				list.add(ModSite.newInstance_PepCterm_aa(modifiedAt.charAt(i)));
			}
			break;
		}
		case modnpaa: {
			for (int i = 0; i < len; i++) {
				list.add(ModSite.newInstance_PepNterm_aa(modifiedAt.charAt(i)));
			}
			break;
		}
		default:
			System.err.println("Unkown type: " + type);
		}

		return list;
	}

	// Get the ModType
	private static ModType getModType(int modtypeidx, String modtypename) {
		ModType modType = ModType.valueOf(modtypename);

		// Validate the modification
		if (modType.ordinal() != modtypeidx) {
			throw new IllegalArgumentException(
			        "The name of modType is not corresponding to the index of modType: name, "
			                + modtypename + "; index, " + modtypeidx);
		}

		return modType;
	}

	/**
	 * The index of modification in mods.xml and usermods.xml
	 * 
	 * @return the index
	 */
	public final int getIndex() {
		return index;
	}

	/**
	 * The description which will be shown in the output file.
	 * 
	 * @return
	 */
	@Override
	public final String getDescription() {
		return this.description;
	}

	/**
	 * unknown???
	 * 
	 * @return the addedN15Mass
	 */
	public final double getAddedN15Mass() {
		return addedN15Mass;
	}

	/**
	 * If has neutral loss information
	 * 
	 * @return the isNeutralloss
	 */
	public final boolean isNeutralloss() {
		return isNeutralloss;
	}

	/**
	 * @return the lossMass the mono isotope mass of neutral loss
	 */
	public final double getLossMonoMass() {
		return lossMonoMass;
	}

	/**
	 * unknown ??
	 * 
	 * @return the lossN15Mass
	 */
	public final double getLossN15Mass() {
		return lossN15Mass;
	}

	/**
	 * @return the lossMass the average mass of neutral loss
	 */
	public final double getLossAvgMass() {
		return lossAvgMass;
	}

	/**
	 * The description string which contains all the informations of this
	 * modification
	 * 
	 * @return
	 */
	public String toDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Index: ").append(this.index).append("; Name: ").append(
		        this.getName()).append("; Description: ").append(this.description)
		        .append("; MonoMass: ").append(this.getAddedMonoMass()).append(
		                "; AvgMass: ").append(this.getAddedAvgMass()).append(
		                "; N15Mass: ").append(this.addedN15Mass).append(
		                "; IsNeutralLoss: ").append(this.isNeutralloss).append(
		                "; NeutralMonoMass: ").append(this.lossMonoMass)
		        .append("; NeutralAvgMass: ").append(this.lossAvgMass).append(
		                "; NeutralN15Mass: ").append(this.lossN15Mass);

		return sb.toString();
	}

	/**
	 * For the use in JList, return the description of the modification. Same as
	 * getDescription();
	 */
	@Override
	public String toString() {
		return this.description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OMSSAMod) {
			return obj.hashCode() == this.hashCode();
		}

		return false;
	}
}
