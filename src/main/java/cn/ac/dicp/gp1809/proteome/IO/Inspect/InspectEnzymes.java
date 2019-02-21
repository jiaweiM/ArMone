/* 
 ******************************************************************************
 * File: InspectEnzymes.java * * * Created on 03-23-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;

/**
 * The static inspect predefined enzymes
 * 
 * @author Xinning
 * @version 0.1, 03-23-2009, 20:14:26
 */
public class InspectEnzymes {

	private HashMap<String, Enzyme> nameMap;

	public InspectEnzymes() {
		nameMap = new HashMap<String, Enzyme>();
		this.initialize();
	}

	private void initialize() {
		//Currently only three choices
		try {
			nameMap.put("trypsin", Enzyme.TRYPSIN);
			nameMap.put("none", Enzyme.NOENZYME);
			nameMap.put("chymotrypsin", new Enzyme("Chymotrypsin", true,
			        "FWYL", null));
		} catch (InvalidEnzymeCleavageSiteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * From a config file (no use currently)
	 * 
	 * @param cfgfile
	 */
	protected InspectEnzymes(String cfgfile) {
		//Not designed
	}

	/**
	 * Get the enzyme by name
	 * 
	 * @param name
	 *            the name of enzyme (case insensitive)
	 * @return
	 */
	public Enzyme getEnzymeByName(String name) throws NullPointerException{
		Enzyme enzyme;

		if (name != null
		        && (enzyme = this.nameMap.get(name.toLowerCase())) != null)
			return enzyme;

		throw new NullPointerException(
		        "Cann't find predefined enzyme for name \"" + name + "\".");
	}

}
