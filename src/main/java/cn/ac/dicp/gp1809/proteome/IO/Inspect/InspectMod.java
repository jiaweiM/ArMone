/*
 * *****************************************************************************
 * File: InspectMod.java * * * Created on 03-23-2009
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.dbsearch.DefaultMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * Modifications in XTandem database search.
 * 
 * @author Xinning
 * @version 0.1, 03-23-2009, 21:19:25
 */
public class InspectMod extends DefaultMod implements IInspectMod,
        java.io.Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a xtandem modification
	 * 
	 * @param modifAt
	 *            the aminoacid where the modification occurs
	 * @param addedMono
	 *            added mono isotope mass
	 * @param addedAvg
	 *            added average mass
	 */
	public InspectMod(HashSet<ModSite> sites, double addedMono,
	        double addedAvg) {
		super("No name", addedMono, addedAvg, sites);
	}

	/**
	 * Create a inspect modification
	 * 
	 * @param modifAt
	 *            the aminoacid where the modification occurs
	 * @param addedMono
	 *            added mono isotope mass
	 * @param addedAvg
	 *            added average mass
	 */
	public InspectMod(String name, HashSet<ModSite> sites, double addedMono,
	        double addedAvg) {
		super(name==null?"No name":name, addedMono, addedAvg, sites);
	}
}
