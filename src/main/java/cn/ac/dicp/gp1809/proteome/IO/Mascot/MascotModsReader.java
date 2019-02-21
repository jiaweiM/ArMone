/* 
 ******************************************************************************
 * File: MascotModsReader.java * * * Created on 11-16-2009
 *
 * Copyright (c) 200 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
 
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;

/**
 * Reader for XML Mascot mods file
 * 
 * @author ck
 * @version 0.1
 */

public class MascotModsReader {
	
	private XMLEventReader r ;
	private int index = 0;
	public static final String default_mascot_unimod_loc = "Mascot/unimod.xml";
	public static final String default_mascot_usermod_loc = "src\\Mascot\\unimod.xml";

	public MascotModsReader(String file) throws FileNotFoundException, XMLStreamException{

		InputStream stream = new FileInputStream(new File(file));
		XMLInputFactory f = XMLInputFactory.newInstance();
		r = f.createXMLEventReader(stream);
	}
	
	public MascotModsReader(String file, Boolean isRelative) throws XMLStreamException, FileNotFoundException{
		if(isRelative){
			InputStream stream = ClassLoader.getSystemResourceAsStream(file);
			XMLInputFactory f = XMLInputFactory.newInstance();
			r = f.createXMLEventReader(stream);
		}else{
			throw new FileNotFoundException();
		}
	}
	

	/**
	 * Get next modification
	 * 
	 * @return
	 * @throws ModsReadingException XMLStreamException
	 * 
	 */
	
	protected MascotMod getMod() throws XMLStreamException, ModsReadingException{

		HashSet <ModSite> site = new HashSet <ModSite> ();
		String name = "";
		double mono_mass = 0.0;
		double avge_mass = 0.0;
		
		while(r.hasNext()){
			
			XMLEvent e = r.nextEvent();
			if(e.isStartElement()){
				if(e.asStartElement().getName().getLocalPart().equals("mod")){
					
					index++;
					Iterator it = e.asStartElement().getAttributes();
					while(it.hasNext()){
						Attribute ab = (Attribute) it.next();
						if(ab.getName().getLocalPart().equals("title")){
							name = ab.getValue().trim();
						}
					}
				}
				
				if(e.asStartElement().getName().getLocalPart().equals("specificity")){
					Iterator it = e.asStartElement().getAttributes();
					while(it.hasNext()){
						Attribute ab = (Attribute) it.next();
						if(ab.getName().getLocalPart().equals("site")){
							
							if(ab.getValue().trim().length()==1){
								char aa = ab.getValue().charAt(0);
								site.add(ModSite.newInstance_aa(aa));
						//		System.out.println(ModSite.newInstance_aa(aa).getModType());
							}
							if(ab.getValue().trim().equals("N-term")){

								site.add(ModSite.newInstance_PepNterm());
						//		System.out.println(ModSite.newInstance_PepNterm().getModType());
							}
							if(ab.getValue().trim().equals("C-term")){

								site.add(ModSite.newInstance_PepCterm());
						//		System.out.println(ModSite.newInstance_PepNterm().getModType());
							}
						}
					}	
				}
				
				if(e.asStartElement().getName().getLocalPart().equals("delta")){
					Iterator it = e.asStartElement().getAttributes();
					while(it.hasNext()){
						Attribute ab = (Attribute) it.next();
						if(ab.getName().getLocalPart().equals("mono_mass")){
							mono_mass = Double.parseDouble(ab.getValue().trim());
						}
						
						if(ab.getName().getLocalPart().equals("avge_mass")){
							avge_mass = Double.parseDouble(ab.getValue().trim());
						}
						
					}
					
				}
			}
			else if(e.isEndElement()){
				if(e.asEndElement().getName().getLocalPart().equals("mod")){
					
					MascotMod mod = new MascotMod(index, name, mono_mass, avge_mass, site);
			//		System.out.println(mod);
					return mod;
				}
			}
			
		}	
		return null;
				
	}
	
	public HashMap <String, MascotMod> getModNameMap() throws ModsReadingException, XMLStreamException{
		HashMap <String, MascotMod> modMap = new HashMap <String, MascotMod> ();
		MascotMod mascotMod = this.getMod();
		while(mascotMod!=null){
			modMap.put(mascotMod.getName(), mascotMod);
			mascotMod = this.getMod();
		}
		return modMap;
	}
	
	public HashMap <Integer, MascotMod> getModIndexMap() throws ModsReadingException, XMLStreamException{
		HashMap <Integer, MascotMod> modMap = new HashMap <Integer, MascotMod> ();
		MascotMod mascotMod = this.getMod();
		while(mascotMod!=null){
			modMap.put((mascotMod.getIndex()-1), mascotMod);
			mascotMod = this.getMod();
		}
		return modMap;
	}
	
	/**
	 * Close the reader
	 */
	
	public void close() throws XMLStreamException{
		this.r.close();
	}
	
	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 * @throws ModsReadingException 
	 */
	public static void main(String[] args) throws FileNotFoundException, XMLStreamException, ModsReadingException {
		// TODO Auto-generated method stub
		String file = "E:\\database\\unimod.xml";
//		MascotModsReader reader = new MascotModsReader(default_mascot_usermod_loc);
		MascotModsReader reader = new MascotModsReader(file);
		System.out.println(reader.getMod());
//		HashMap map = reader.getModIndexMap();
//		System.out.println(map.keySet());
	}

}
