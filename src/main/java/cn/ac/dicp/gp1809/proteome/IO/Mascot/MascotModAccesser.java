/* 
 ******************************************************************************
 * File:MascotModAccesser.java * * * Created on 2009-12-17
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

import cn.ac.dicp.gp1809.proteome.dbsearch.IModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;

/**
 * @author ck
 *
 * @version 2009-12-17, 18:22:38
 */
public class MascotModAccesser {
	
	HashMap <Integer, MascotMod> modMap;
	public static final String default_mascot_unimod_loc = "src\\Mascot\\unimod.xml";
	public static final String default_mascot_usermod_loc = "Mascot\\usermod.xml";
	
	public MascotModAccesser (String file,Boolean isRelative) throws FileNotFoundException, XMLStreamException, ModsReadingException{
		if(isRelative){
			MascotModsReader reader = new MascotModsReader(file, isRelative);
			modMap = reader.getModIndexMap();
		}else{
			MascotModsReader reader = new MascotModsReader(file);
			modMap = reader.getModIndexMap();
		}
		
	}
	
	public IModification getModification(int i) {

		if (i >= this.getNumberofMods())
			throw new IndexOutOfBoundsException(
			        "Modification index exceedes total number of peptides exception: "
			                + i);

		return this.modMap.get(i);
	}
	
	public int getNumberofMods(){
		return this.modMap.size();
	}
	
	public String [] getTitle(){
		String [] title = {"Name","Mono_mass","Avge_mass","Sites"};
		return title;
	}
	
	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws ModsReadingException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, ModsReadingException, XMLStreamException {
		// TODO Auto-generated method stub
		MascotModAccesser ms = new MascotModAccesser(default_mascot_unimod_loc, false);
		for(int i=0;i<100;i++){
			System.out.println(ms.getModification(i));
		}
		
	}

}
