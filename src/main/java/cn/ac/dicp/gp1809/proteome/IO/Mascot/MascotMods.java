/* 
 ******************************************************************************
 * File:MascotMods.java * * * Created on Nov 17, 2009
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot;

import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;

/**
 * @author ck
 *
 * @version 0.1, Nov 17, 2009, 9:42:22 AM
 */
public class MascotMods {
	
	private static final long serialVersionUID = 1L;

	private MascotMod[] mods;

	private HashMap <String, MascotMod> map;

	/**
	 * Get the list of modifications which are predefined for Mascot database
	 * search. The files containing these predefined modifications can be found
	 * at the Mascot program directory
	 * 
	 * <p>
	 * <b>The modification can be get by its index. This index also the same as
	 * that in database search. You may get a null mod if the modification of
	 * the current index is not predefined in unimod.xml </b>
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
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	
	public MascotMods(String unimod, String usermod) throws ModsReadingException, FileNotFoundException, XMLStreamException {
		parseModsFromFile(unimod);
	}

	public MascotMods(String unimod) throws ModsReadingException, FileNotFoundException, XMLStreamException {
		parseModsFromFile(unimod);
	}
	
	/**
	 * Create the MascotMods from the MascotMod[]. Set the mods.
	 * 
	 * @param mods
	 *            mod[]
	 */
	public MascotMods(MascotMod[] mods) {

		if (mods == null)
			throw new NullPointerException("The mods must not be null.");

		this.mods = mods;
		this.map = this.getModMap(mods);
	}

	/**
	 * Get the DefaultMod through the modification index in the predefined mods
	 * file. Null will be returned if the DefaultMod of current index is not
	 * predefined in current mods
	 * 
	 * @param index
	 * @return the mod for this index (May be null).
	 */
	public MascotMod getMod(int index) {
		if (index >= this.mods.length || index < 0)
			return null;

		return this.mods[index];
	}

	
	/**
	 * Create the map from the MascotMod[] for the easy accession of MascotMod by
	 * the name
	 * 
	 * @param mods
	 * @return modMap, key = mod.getName(); value = mod
	 */
	public HashMap<String, MascotMod> getModMap(MascotMod[] mods) {
		// TODO Auto-generated method stub
		
		HashMap modMap = new HashMap();
		for(MascotMod mod: mods){
			
			if (mod == null) {
				continue;
			}
			String name = mod.getName();

			if (modMap.containsKey(name)) {
				throw new IllegalArgumentException(
				        "There can NOT be two modifications with the same name: "
				                + name);
			}
			modMap.put(name, mod);
		}
		return modMap;
	}
	
	public HashMap <String, MascotMod> getModMap(){
		return map;
	}

	/**
	 * Get the list of modifications which are predefined for Mascot database
	 * search. The files containing these predefined modifications can be found
	 * at the Mascot program directory
	 * 
	 * <p>
	 * <b>The modification can be get by its index. This index also the same as
	 * that in database search. You may get a null mod if the modification of
	 * the current index is not predefined in unimod.xml or usermod.xml</b>
	 * 
	 * <p>
	 * <b>The predefined mods.xml and usermods.xml can not contain modifications
	 * with duplicated index or description</b>
	 * 
	 * @param unimod
	 * 			the default modification file
	 * @param usermod
	 * 			the file containing user defined modification
	 * @throws ModsReadingException
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	private void parseModsFromFile(String unimod) 
		throws ModsReadingException, FileNotFoundException, XMLStreamException {
		// TODO Auto-generated method stub
		
		int len = 1000;
		MascotMod[] mods = new MascotMod[len];

		MascotModsReader reader = new MascotModsReader(unimod);

		MascotMod mod;
		while ((mod = reader.getMod()) != null) {
			int idx = mod.getIndex();
			if (idx >= len) {
				MascotMod[] tmods = new MascotMod[idx + 1];
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
/*
		if (usermod != null && new File(usermod).exists()) {
			reader = new MascotModsReader(usermod);

			while ((mod = reader.getMod()) != null) {
				int idx = mod.getIndex();
				if (idx >= len) {
					MascotMod [] tmods = new MascotMod[idx + 1];
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
*/
		this.map = this.getModMap(mods);
//		System.out.println(map.size());
		this.mods = mods;
	}



	/**
	 * @param args
	 * @throws XMLStreamException, ModsReadingException 
	 * @throws FileNotFoundException 
	 * @throws ModsReadingException 
	 */
	public static void main(String[] args) throws ModsReadingException, FileNotFoundException, 
			XMLStreamException, ModsReadingException {
		// TODO Auto-generated method stub
		
		MascotMods mods = new MascotMods("E:\\CK\\workspace\\ArCommon\\Mascot\\unimod.xml", null);

		
		
	}

}
