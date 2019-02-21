/* 
 ******************************************************************************
 * File: MutilLabelPairXMLWriter.java * * * Created on 2012-6-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLWriter;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;

/**
 * @author ck
 *
 * @version 2012-6-14, 20:13:33
 */
public class MutilLabelPairXMLWriter extends LabelFeaturesXMLWriter {

	/**
	 * @param file
	 * @param type
	 * @param gradient
	 * @throws IOException
	 */
	public MutilLabelPairXMLWriter(File file, LabelType labelType, boolean gradient)
			throws IOException {
		super(file, labelType, gradient);
		// TODO Auto-generated constructor stub
	}
	
	protected void initial(){
		this.root = DocumentFactory.getInstance().createElement("ProFeatures");
		this.document.setRootElement(root);
		root.addAttribute("Label_Type", type.getLabelName());
		root.addAttribute("Gradient", String.valueOf(gradient));
		totalIntensity = new double [type.getLabelNum()];
		addLabelInfo();
	}
	
	protected void addLabelInfo(){
		
	}
	
	public void addModification(AminoacidModification aamods){

		Modif [] mods = aamods.getModifications();
		for(int i=0;i<mods.length;i++){
			Modif m = mods[i];
			String name = m.getName();
			double mass = m.getMass();
			char symbol = m.getSymbol();
			HashSet <ModSite> sites = aamods.getModifSites(symbol);
			Iterator <ModSite> it = sites.iterator();
			while(it.hasNext()){
				
				ModSite ms = it.next();
				if(ms.equals(ModSite.newInstance_aa('K')) || ms.equals(ModSite.newInstance_PepNterm())){
					continue;
				}
				String site = ms.getModifAt();
				Element eMod = DocumentFactory.getInstance().createElement("Modification");
				eMod.addAttribute("Name", name);
				eMod.addAttribute("Mass", String.valueOf(mass));
				eMod.addAttribute("Symbol", String.valueOf(symbol));
				eMod.addAttribute("ModSite", site);
				root.add(eMod);
			}
		}	
	}
	
}
