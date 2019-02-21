/* 
 ******************************************************************************
 * File: AbstractPairXMLWriter.java * * * Created on 2011-8-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.profile.IO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2011-8-1, 15:26:58
 */
public abstract class AbstractFeaturesXMLWriter {
	
	protected Document document;
	protected XMLWriter writer;
	protected Element root;
	protected LabelType type;
	protected boolean gradient;
	protected File file;
	
	protected DecimalFormat df2 = DecimalFormats.DF0_2;
	protected DecimalFormat df4 = DecimalFormats.DF0_4;
	protected DecimalFormat df5 = DecimalFormats.DF0_5;
	protected DecimalFormat dfe4 = DecimalFormats.DF_E4;
	
	/**
	 * For label free constructor.
	 * @param file
	 * @throws IOException
	 */
	public AbstractFeaturesXMLWriter(String file) throws IOException{
		this(new File(file));
	}
	
	public AbstractFeaturesXMLWriter(File file) throws IOException{
		this.document = DocumentHelper.createDocument();
		this.writer = new XMLWriter(new FileWriter(file));
		this.file = file;
		this.initial();
	}
	
	/**
	 * For label constructor.
	 * @param file
	 * @param type
	 * @param gradient
	 * @throws IOException
	 */
	public AbstractFeaturesXMLWriter(String file, LabelType type, boolean gradient) throws IOException{
		this(new File(file), type, gradient);
	}
	
	public AbstractFeaturesXMLWriter(File file, LabelType type, boolean gradient) throws IOException{
		this.file = file;
		this.type = type;
		this.gradient = gradient;
		this.document = DocumentHelper.createDocument();
		this.writer = new XMLWriter(new FileWriter(file));
		this.initial();
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
				String site = it.next().getModifAt();
				Element eMod = DocumentFactory.getInstance().createElement("Modification");
				eMod.addAttribute("Name", name);
				eMod.addAttribute("Mass", String.valueOf(mass));
				eMod.addAttribute("Symbol", String.valueOf(symbol));
				eMod.addAttribute("ModSite", site);
				root.add(eMod);
			}
		}	
	}
	
	public void addModification(ModInfo [] mods){

		for(int i=0;i<mods.length;i++){
			ModInfo m = mods[i];
			String name = m.getName();
			double mass = m.getMass();
			char symbol = m.getSymbol();
			ModSite site = mods[i].getModSite();
			
			Element eMod = DocumentFactory.getInstance().createElement("Modification");
			eMod.addAttribute("Name", name);
			eMod.addAttribute("Mass", String.valueOf(mass));
			eMod.addAttribute("Symbol", String.valueOf(symbol));
			eMod.addAttribute("ModSite", site.getModifAt());
			root.add(eMod);
		}	
	}
	
	public void addProNameInfo(ProteinNameAccesser accesser){
		
		int lt = accesser.getSplitLength();
		int lr = accesser.getSplitRevLength();
		boolean usePattern = accesser.usePattern();
		
		Element eProName = DocumentFactory.getInstance().createElement("Protein_Info");
		eProName.addAttribute("Use_Pattern", String.valueOf(usePattern));
		eProName.addAttribute("Target_Length", String.valueOf(lt));
		eProName.addAttribute("Decoy_Length", String.valueOf(lr));
		if(usePattern){
			Pattern pattern = accesser.getPattern();
			eProName.addAttribute("Pattern", pattern.pattern());
		}
		
		SimpleProInfo [] infos = accesser.getInfosofProteins();
		for(int i=0;i<infos.length;i++){
			
			Element eInfo = DocumentFactory.getInstance().createElement("Info");
			
			String ref = infos[i].getRef();
			int length = infos[i].getLength();
			double mw = infos[i].getMw();
			double hydro = infos[i].getHydroScore();
			double PI = infos[i].getPI();
			boolean isDecoy = infos[i].isDecoy();
			
			eInfo.addAttribute("Reference", ref);
			eInfo.addAttribute("Length", String.valueOf(length));
			eInfo.addAttribute("MW", String.valueOf(mw));
			eInfo.addAttribute("Hydro", String.valueOf(hydro));
			eInfo.addAttribute("PI", String.valueOf(PI));
			eInfo.addAttribute("isDecoy", String.valueOf(isDecoy));
			
			eProName.add(eInfo);
		}
		root.add(eProName);
	}


	/**
	 * Add the peptides only used in the identification of proteins.
	 * @param pep
	 */
	public void addIdenPep(IPeptide pep){
		
		Element eIdenPep = DocumentFactory.getInstance().createElement("Iden_Pep");
		String scanName = pep.getBaseName();
		int beg = pep.getScanNumBeg();
		int end = pep.getScanNumEnd();
		String ref = pep.getProteinReferenceString();		
		String seq = pep.getSequence();
		int charge = pep.getCharge();

		eIdenPep.addAttribute("BaseName", scanName);
		eIdenPep.addAttribute("ScanBeg", String.valueOf(beg));
		eIdenPep.addAttribute("ScanEnd", String.valueOf(end));
		eIdenPep.addAttribute("Sequence", seq);
		eIdenPep.addAttribute("Charge", String.valueOf(charge));
		eIdenPep.addAttribute("Reference", ref);
		
		root.add(eIdenPep);
	}
	
	public void addIdenPep(IPeptide pep, HashSet<Integer> labelTypeSet){
		
		Element eIdenPep = DocumentFactory.getInstance().createElement("Iden_Pep");
		String scanName = pep.getBaseName();
		int beg = pep.getScanNumBeg();
		int end = pep.getScanNumEnd();
		String ref = pep.getProteinReferenceString();		
		String seq = pep.getSequence();
		int charge = pep.getCharge();

		eIdenPep.addAttribute("BaseName", scanName);
		eIdenPep.addAttribute("ScanBeg", String.valueOf(beg));
		eIdenPep.addAttribute("ScanEnd", String.valueOf(end));
		eIdenPep.addAttribute("Sequence", seq);
		eIdenPep.addAttribute("Charge", String.valueOf(charge));
		eIdenPep.addAttribute("Reference", ref);
		
		Integer [] labels = labelTypeSet.toArray(new Integer[labelTypeSet.size()]);
		Arrays.sort(labels);
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<labels.length;i++){
			sb.append(labels[i]).append("_");
		}
		eIdenPep.addAttribute("Labels", sb.substring(0, sb.length()-1));
		
		root.add(eIdenPep);
	}
	
	/**
	 * 
	 */
	protected abstract void initial();

	/**
	 * Write the content.
	 */
	public void write() throws IOException{
		writer.write(document);
	}
	
	/**
	 * Close the writer.
	 */
	public void close() throws IOException{					
		writer.close();
		System.gc();
	}
	
	
}
