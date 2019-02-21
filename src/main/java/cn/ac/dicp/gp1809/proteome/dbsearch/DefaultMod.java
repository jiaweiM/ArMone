/* 
 ******************************************************************************
 * File: DefaultMod.java * * * Created on 11-11-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import java.util.HashSet;

/**
 * The default type of modification in database search.
 * 
 * <p>
 * Changes:
 * <li>0.2.1, 03-23-2009: Change the site container as HashSet
 * 
 * @author Xinning
 * @version 0.2.1, 03-23-2009, 21:36:06
 */
public class DefaultMod implements IModification, Comparable<DefaultMod>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private String name;

	private double addedMonoMass;
	private double addedAvgMass;

	private HashSet<ModSite> modifiedAt;

	/**
	 * The modification for aminoacids.
	 * 
	 * @param name
	 *            name of this modification
	 * @param addedMonoMass
	 *            the added mono isotope mass of this modification
	 * @param addedAvgMass
	 *            the added average mass of this modification
	 * @param modifiedAt
	 *            a string of modified aminoacids (or N(C)-term).
	 */
	public DefaultMod(String name, double addedMonoMass, double addedAvgMass,
	        HashSet<ModSite> sites) {

		this.name = name;

		this.addedMonoMass = addedMonoMass;
		this.addedAvgMass = addedAvgMass;
		this.modifiedAt = sites;
	}
	
	/**
	 * the name of this modification
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * The added Mono isotope mass
	 * 
	 * @return the addedMass
	 */
	public final double getAddedMonoMass() {
		return addedMonoMass;
	}

	/**
	 * The added average mass of this modification
	 * 
	 * @return the addedAvgMass
	 */
	public final double getAddedAvgMass() {
		return addedAvgMass;
	}

	/**
	 * the modified aminoacid
	 * 
	 * @return the modifiedAt
	 */
	public HashSet<ModSite> getModifiedAt() {
		return modifiedAt;
	}

	/**
	 * For the use in JList, return the description of the modification. Same as
	 * getDescription();
	 */
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Mono: ").append(this.addedMonoMass).append("; Avg: ")
		        .append(this.addedAvgMass).append(" @").append(this.modifiedAt);
		return sb.toString();
	}

	@Override
	public void merge(IModification mod) throws IllegalArgumentException {

		//Do nothing
		if (mod == null)
			return;

		if (this.getClass() != mod.getClass()) {
			throw new IllegalArgumentException(
			        "The two modifications for merging are not the same type of modification.");
		}

		if (this.addedAvgMass == mod.getAddedAvgMass()
		        && this.addedMonoMass == mod.getAddedMonoMass()){
			this.modifiedAt.addAll(mod.getModifiedAt());
		}
		else
			throw new IllegalArgumentException(
			        "These two modfication cannot be merged together because of their different mass.");

	}
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public DefaultMod clone() {
		try {
	        return (DefaultMod) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}
	
	/**
	 * {@inheritDoc}}
	 * 
	 */
	@Override
    public DefaultMod deepClone() {
		
		DefaultMod copy = this.clone();
		
		if(this.modifiedAt!=null) {
			HashSet<ModSite> sitelist = new HashSet<ModSite>();
			for(ModSite site : this.modifiedAt) {
				sitelist.add(site.deepClone());
			}
			copy.modifiedAt = sitelist;
		}
		
	    return copy;
    }

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.IModification#toString(cn.ac.dicp.gp1809.proteome.dbsearch.IModification)
	 */
	@Override
	public String toString() {
		
		String name = this.getName();
		String mono = String.valueOf(this.getAddedMonoMass());
		String avg = String.valueOf(this.getAddedAvgMass());
		HashSet <ModSite> modSites = this.getModifiedAt();
		
		StringBuilder sb = new StringBuilder();
		for(ModSite s: modSites){
			String local = s.getModifAt();
			sb.append(local).append(";");
		}
		
		String str = name+"\t"+mono+"\t"+avg+"\t"+sb.toString();
	
		// TODO Auto-generated method stub
		return str;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DefaultMod mod) {
		// TODO Auto-generated method stub
		double dm1 = this.addedMonoMass;
		double dm2 = mod.addedMonoMass;
		double da1 = this.addedAvgMass;
		double da2 = mod.addedAvgMass;
		if(dm1>dm2)
			return 1;
		else if(dm1<dm2)
			return -1;
		else
			return da1>da2 ? 1:-1;
	}
}
