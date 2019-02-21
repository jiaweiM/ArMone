/*
 ******************************************************************************
 * File: CruxMod.java * * * Created on 04-01-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.dbsearch.DefaultMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * Modifications in crux database search.
 * 
 * @author Xinning
 * @version 0.1, 04-01-2009, 20:23:58
 */
public class CruxMod extends DefaultMod implements ICruxMod,
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
	public CruxMod(HashSet<ModSite> sites, double addedMono,
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
	public CruxMod(String name, HashSet<ModSite> sites, double addedMono,
	        double addedAvg) {
		super(name==null?"No name":name, addedMono, addedAvg, sites);
	}
}
