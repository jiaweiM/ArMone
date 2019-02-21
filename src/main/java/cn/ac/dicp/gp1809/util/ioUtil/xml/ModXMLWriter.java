/* 
 ******************************************************************************
 * File:ModXMLWriter.java * * * Created on 2009-12-7
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import cn.ac.dicp.gp1809.proteome.dbsearch.DefaultMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite.ModType;

/**
 * @author ck
 *
 * @version 2009-12-7, 09:35:13
 */
public class ModXMLWriter {
	
	private File file;
	private SAXReader reader;
	private Document document;
	private Element root;
	
	public ModXMLWriter (String file) throws DocumentException, IOException{
		this (new File(file));
	}
	
	public ModXMLWriter (File fileName) throws DocumentException, IOException{
		
		this.file = fileName;
		if(file.exists()){
			reader = new SAXReader();
			document = reader.read(file);
			root = document.getRootElement();
		}
		else{
			
			document = DocumentHelper.createDocument();
			root = document.addElement("Modifications");
			XMLWriter output = new XMLWriter(new FileWriter(file));
		    output.write(document);
		    output.close();
		}
		
	}
	
	public void write(DefaultMod mod) throws IOException{
		String name = mod.getName();
		double addedMonoMass = mod.getAddedMonoMass();
		double addedAvgMass = mod.getAddedAvgMass();
		HashSet <ModSite> sites = mod.getModifiedAt();
		write(name, addedMonoMass, addedAvgMass, sites);
	}
	
	public void write(String name, double addedMonoMass, double addedAvgMass,
	        HashSet<ModSite> sites) throws IOException{
		Element mod = root.addElement("mod");
		mod.addAttribute("title", name);

		for(ModSite s:sites){
			Element specificity = mod.addElement("specificity");
			ModType type = s.getModType();
			if(type==ModType.modaa){
				specificity.addAttribute("site", s.getSymbol());
			}
			else if(type==ModType.modcp){
				specificity.addAttribute("site", "C-term");
			}
			else if(type==ModType.modnp){
				specificity.addAttribute("site", "N-term");
			}
			else{
				System.err.println("Unknown modType!");
			}
		}
		
		Element delta = mod.addElement("delta");
		delta.addAttribute("mono_mass", String.valueOf(addedMonoMass));
		delta.addAttribute("avge_mass", String.valueOf(addedAvgMass));
		
		XMLWriter output = new XMLWriter(new FileWriter(file));
	    output.write(document);
	    output.close();
	}

	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DocumentException, IOException {
		// TODO Auto-generated method stub
		ModXMLWriter writer = new ModXMLWriter("E:\\newMods.xml");
		ModSite s1 = ModSite.newInstance_aa('A');
		ModSite s2 = ModSite.newInstance_aa('K');
		ModSite s3 = ModSite.newInstance_PepNterm();
		ModSite s4 = ModSite.newInstance_PepCterm();
		
		HashSet <ModSite> set = new HashSet <ModSite> ();
		set.add(s1);
		set.add(s2);
		set.add(s3);
		set.add(s4);
		
		writer.write("mod", 0.4, 0.5, set);
	}

}
