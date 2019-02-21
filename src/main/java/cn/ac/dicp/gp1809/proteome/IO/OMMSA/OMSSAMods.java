/* 
 ******************************************************************************
 * File: OMSSAMods.java * * * Created on 09-03-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;

/**
 * Modification list of OMSSA database search
 * 
 * @author Xinning
 * @version 0.1.2, 10-20-2008, 08:50:42
 */
public class OMSSAMods implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OMSSAMod[] mods;

	private HashMap<String, OMSSAMod> map;

	/**
	 * Get the list of modifications which are predefined for OMSSA database
	 * search. The files containing these predefined modifications can be found
	 * at the OMSSA program directory
	 * 
	 * <p>
	 * <b>The modification can be get by its index. This index also the same as
	 * that in database search. You may get a null mod if the modification of
	 * the current index is not predefined in mods.xml or usermods.xml</b>
	 * 
	 * @param modsxml
	 *            the default modification file
	 * @param usermodsxml
	 *            the file containing user defined modification (can be null)
	 * @return the modification used in database search
	 * 
	 * 
	 * @throws ModsReadingException
	 *             if error occurs while parsing the mods file.
	 *             <p>
	 *             Or if the mods file is illegal: containing modifications with
	 *             duplicated index or description.
	 */
	public OMSSAMods(String modsxml, String usermodsxml)
	        throws ModsReadingException {
		parseModsFromFile(modsxml, usermodsxml);
	}

	/**
	 * Create the OMSSAMods from the OMSSAMod[]. Set the mods.
	 * 
	 * @param mods
	 *            mod[]
	 */
	public OMSSAMods(OMSSAMod[] mods) {

		if (mods == null)
			throw new NullPointerException("The mods must not be null.");

		this.mods = mods;
		this.map = this.getModMap(mods);
	}

	/**
	 * Get the OMSSAMod through the modification index in the predefined mods
	 * file. Null will be returned if the OMSSAMod of current index is not
	 * predefined in current mods
	 * 
	 * @param index
	 * @return the mod for this index (May be null).
	 */
	public OMSSAMod getMod(int index) {
		if (index >= this.mods.length || index < 0)
			return null;

		return this.mods[index];
	}

	/**
	 * Get the OMSSAMod through the description of the modification in
	 * predefined mods file (NOT the name).
	 * 
	 * @param description
	 * @return the mod for this description (May be null).
	 */
	public OMSSAMod getMod(String description) {
		return this.map.get(description);
	}

	/**
	 * Get the list of modifications which are predefined for OMSSA database
	 * search. The files containing these predefined modifications can be found
	 * at the OMSSA program directory
	 * 
	 * <p>
	 * The modification can be get by its index. This index also the same as
	 * that in database search. You may get a <b>null</b> mod if the
	 * modification of the current index is not predefined in mods.xml or
	 * usermods.xml
	 * 
	 * @return
	 */
	public OMSSAMod[] getMods() {
		return this.mods;
	}

	/**
	 * The length of the mods. Because the index of the mod is also the index of
	 * the mod in modification array, the length of the modification is <b>NOT</b>
	 * the number of pre defined modifications. If you want to know the number
	 * of pre defined modifications, use <b>{@link #getModNumber()}</b>.
	 * 
	 * @since 0.1.2
	 * @return
	 */
	public int length() {
		return this.mods.length;
	}
	
	/**
	 * The number of pre defined modifications.
	 * 
	 * @since0.1.2
	 * @return
	 */
	public int getModNumber(){
		return this.map.size();
	}

	/**
	 * Get the list of modifications which are predefined for OMSSA database
	 * search. The files containing these predefined modifications can be found
	 * at the OMSSA program directory
	 * 
	 * <p>
	 * <b>The modification can be get by its index. This index also the same as
	 * that in database search. You may get a null mod if the modification of
	 * the current index is not predefined in mods.xml or usermods.xml</b>
	 * 
	 * <p>
	 * <b>The predefined mods.xml and usermods.xml can not contain modifications
	 * with duplicated index or description</b>
	 * 
	 * @param modsxml
	 *            the default modification file
	 * @param usermodsxml
	 *            the file containing user defined modification
	 * @return the modification used in database search
	 * @throws ModsReadingException
	 */
	protected final void parseModsFromFile(String modsxml, String usermodsxml)
	        throws ModsReadingException {

		int len = 162;
		OMSSAMod[] mods = new OMSSAMod[len];

		OMSSAModsReader reader = new OMSSAModsReader(modsxml);

		OMSSAMod mod;
		while ((mod = reader.getMod()) != null) {
			int idx = mod.getIndex();
			if (idx >= len) {
				OMSSAMod[] tmods = new OMSSAMod[idx + 1];
				System.arraycopy(mods, 0, tmods, 0, len);
				len = idx + 1;
				mods = tmods;
			}
			// Two modifications with the same index
			if (mods[idx] != null) {
				throw new ModsReadingException(
				        "The modifications in predefined file \"mods.xml\" and \"usermods.xml\" "
				                + "can NOT contain two modifications with the same index: "
				                + idx);
			}

			mods[idx] = mod;
		}
		reader.close();

		if (usermodsxml != null && new File(usermodsxml).exists()) {
			reader = new OMSSAModsReader(usermodsxml);

			while ((mod = reader.getMod()) != null) {
				int idx = mod.getIndex();
				if (idx >= len) {
					OMSSAMod[] tmods = new OMSSAMod[idx + 1];
					System.arraycopy(mods, 0, tmods, 0, len);
					len = idx + 1;
					mods = tmods;
				}
				// Two modifications with the same index
				if (mods[idx] != null) {
					throw new ModsReadingException(
					        "The modifications in predefined file \"mods.xml\" and \"usermods.xml\" "
					                + "can NOT contain two modifications with the same index: "
					                + idx);
				}
				mods[idx] = mod;
			}

			reader.close();
		}

		this.map = this.getModMap(mods);
		this.mods = mods;
	}

	/**
	 * Create the map from the OMSSAMod[] for the easy accession of OMSSAMod by
	 * the description
	 * 
	 * @param mods
	 * @return
	 */
	protected final HashMap<String, OMSSAMod> getModMap(OMSSAMod[] mods) {
		HashMap<String, OMSSAMod> map = new HashMap<String, OMSSAMod>(256);

		for (OMSSAMod mod : mods) {

			// The current index is not a mod index
			if (mod == null) {
				continue;
			}

			String des = mod.getDescription();
			OMSSAMod tmod = map.get(mod.getDescription());
			if (tmod != null) {
				throw new IllegalArgumentException(
				        "There can NOT be two modifications with the same description: "
				                + des);
			}
			map.put(des, mod);
		}

		return map;
	}

	public static void main(String[] args) throws ModsReadingException {
		OMSSAMods mods = new OMSSAMods(
		        "E:\\Downloads\\chemsoft\\proteome\\omssa-win32\\omssa\\mods.xml",
		        "E:\\Downloads\\chemsoft\\proteome\\omssa-win32\\omssa\\usermods.xml");
		OMSSAMod[] modifs = mods.getMods();
		for (int i = 0; i < modifs.length; i++) {
			OMSSAMod modif = modifs[i];
			if (modif != null)
				System.out.println(modifs[i].toDescription());
		}
	}

}
