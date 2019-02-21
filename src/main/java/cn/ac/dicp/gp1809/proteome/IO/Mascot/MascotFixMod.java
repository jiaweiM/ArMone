/*
 * *****************************************************************************
 * File: MascotFixMod.java * * * Created on 11-16-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.dbsearch.IFixMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * Variable Modifications in Mascot database search.
 * 
 * @author Xinning
 * @version 0.1.1, 03-23-2009, 21:49:41
 */
public class MascotFixMod extends MascotMod implements IMascotMod, IFixMod ,java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The id in mascot search
//	private int id;

	/**
	 * Construct a mascot variable modification with no Neutral loss.
	 * 
	 * 
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
	 * @param modifiedAt
	 *            the modified aminoacid
	 * @param id
	 *            the unique int value indicating the index of this modification
	 *            in the used variable modifications <b>(1-n)</b>
	 * 
	 */
	public MascotFixMod(int id, String name, double addedMonoMass, double addedAvgMass,
			HashSet<ModSite> modifiedAt) {
		super(id, name, addedMonoMass, addedAvgMass, modifiedAt);

	}

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.IMascotMod#getId()
	 */
//	public int getId() {
//		return this.id;
//	}

	/**
	 * The description string which contains all the informations of this
	 * modification
	 * 
	 * @return
	 */
	public String toDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id: ").append(this.getIndex()).append("; Name: ").append(
		        this.getName()).append("; MonoMass: ").append(
		        this.getAddedMonoMass()).append("; AvgMass: ").append(
		        this.getAddedAvgMass());

		return sb.toString();
	}

	public static MascotFixMod Carbamidomethyl(){
		HashSet <ModSite> modifiedAt = new HashSet <ModSite> ();
		ModSite site = ModSite.newInstance_aa('C');
		modifiedAt.add(site);
		String name = "Carbamidomethyl";
		double addedMonoMass = 57.021464;
		double addedAvgMass = 57.0513;
		MascotFixMod mod = new MascotFixMod(0, name, addedMonoMass, addedAvgMass, modifiedAt);
		return mod;
	}
	
	/**
	 * For the use in JList, return the description of the modification. Same as
	 * getDescription();
	 */
	@Override
	public String toString() {
		return this.toDescription();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getIndex() + new Double(this.getAddedMonoMass()).hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MascotFixMod) {
			return obj.hashCode() == this.hashCode();
		}

		return false;
	}
}
