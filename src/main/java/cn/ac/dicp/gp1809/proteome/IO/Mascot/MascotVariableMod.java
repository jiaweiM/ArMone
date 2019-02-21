/*
 * *****************************************************************************
 * File: MascotVariableMod.java * * * Created on 11-16-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.dbsearch.IVariableMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * Variable Modifications in Mascot database search.
 * 
 * @author Xinning
 * @version 0.1.1, 03-23-2009, 21:50:10
 */
public class MascotVariableMod extends MascotMod implements IMascotMod, IVariableMod, 
        java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The id in mascot search
//	private int id;

	private boolean isNeutralloss;

	private double lossMonoMass;
	private double lossAvgMass;

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
	 * @param type
	 *            the modification type.
	 * @param id
	 *            the unique int value indicating the index of this modification
	 *            in the used variable modifications <b>(1-n)</b>
	 * 
	 */
	public MascotVariableMod(int id, String name, double addedMonoMass,
	        double addedAvgMass, HashSet<ModSite> modifiedAt) {
		super(id, name, addedMonoMass, addedAvgMass, modifiedAt);

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
	 * @param modifiedAt
	 *            the modified aminoacid
	 * @param type
	 *            the modification type.
	 * @param isNeutralloss
	 *            if has neutral loss
	 * @param lossMonoMass
	 *            the lost mono isotope mass of neutral loss
	 * @param lossAvgMass
	 *            the lost average mass of neutral loss
	 * @param id
	 *            the unique int value indicating the index of this modification
	 *            in the used variable modifications <b>(1-n)</b>
	 */
	public MascotVariableMod(int id, String name, double addedMonoMass,
	        double addedAvgMass, HashSet<ModSite> modifiedAt,
	        boolean isNeutralloss, double lossMonoMass, double lossAvgMass) {

		super(id, name, addedMonoMass, addedAvgMass, modifiedAt);

		this.isNeutralloss = isNeutralloss;
		this.lossMonoMass = lossMonoMass;
		this.lossAvgMass = lossAvgMass;

	}

	public void setNeutralloss(double neutrallossMono, double neutrallossAvg){
		this.isNeutralloss = true;
		this.lossMonoMass = neutrallossMono;
		this.lossAvgMass = neutrallossAvg;
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
	 * @return the lossMass the average mass of neutral loss
	 */
	public final double getLossAvgMass() {
		return lossAvgMass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.IMascotMod#getId()
	 */
//	public int getIndex() {
//		return super.getIndex();
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
		        this.getAddedAvgMass()).append("; IsNeutralLoss: ").append(
		        this.isNeutralloss).append("; NeutralMonoMass: ").append(
		        this.lossMonoMass).append("; NeutralAvgMass: ").append(
		        this.lossAvgMass);

		return sb.toString();
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
		if (obj instanceof MascotVariableMod) {
			return obj.hashCode() == this.hashCode();
		}

		return false;
	}
}
