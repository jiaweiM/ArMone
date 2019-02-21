/* 
 ******************************************************************************
 * File: GlycoLFFeasXMLWriter2.java * * * Created on 2013-6-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree2;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.profile.IO.AbstractFeaturesXMLWriter;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.Quan.labelFree.GlycoLFFeasXMLReader;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2013-6-13, 13:47:44
 */
public class GlycoLFFeasXMLWriter2 extends AbstractFeaturesXMLWriter
{

	private HashMap <Integer, String> peakOneLineMap;
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	/**
	 * @param file
	 * @throws IOException
	 */
	public GlycoLFFeasXMLWriter2(String file) throws IOException {
		super(file);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param file
	 * @throws IOException
	 */
	public GlycoLFFeasXMLWriter2(File file) throws IOException {
		super(file);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLWriter#initial()
	 */
	@Override
	protected void initial() {
		// TODO Auto-generated method stub
		this.root = DocumentFactory.getInstance().createElement("Glyco_Features");
		this.document.setRootElement(root);
		this.root.addAttribute("Label_Type", "Label_Free");
		this.peakOneLineMap = new HashMap <Integer, String> ();
	}

	public void addGlycoSpectra(NGlycoSSM ssm){
		
		Element eGlyscan = DocumentFactory.getInstance().createElement("GlycoSpectra");
		
		String peakOneLine = ssm.getPeakOneLine();
		int scannum = ssm.getScanNum();
		double rt = ssm.getRT();
		
		eGlyscan.addAttribute("ScanNum", String.valueOf(scannum));
		eGlyscan.addAttribute("RT", df4.format(rt));
		this.peakOneLineMap.put(scannum, peakOneLine);
		
		GlycoTree tree = ssm.getGlycoTree();
		StringBuilder labelSb = new StringBuilder();
		HashSet <Integer> matchedPeaks = ssm.getMatchedPeaks();
		Iterator <Integer> it = matchedPeaks.iterator();
		
		while(it.hasNext()){
			Integer id = it.next();
			labelSb.append(id).append("_");
		}

		eGlyscan.addAttribute("Rank", String.valueOf(ssm.getRank()));
		eGlyscan.addAttribute("Score", df4.format(ssm.getScore()));
		eGlyscan.addAttribute("PrecursorMz", df4.format(ssm.getPreMz()));
		eGlyscan.addAttribute("GlycoMass", df4.format(ssm.getGlycoMass()));
		eGlyscan.addAttribute("PeptideMassExperiment", df4.format(ssm.getPepMassExperiment()));
		eGlyscan.addAttribute("PeptideID", String.valueOf(ssm.getPeptideid()));
		eGlyscan.addAttribute("MatchedPeaks", labelSb.substring(0, labelSb.length()-1));
		eGlyscan.addAttribute("GlycoCT", tree.getGlycoCT());
		eGlyscan.addAttribute("Name", tree.getIupacName());
		eGlyscan.addAttribute("PeakOneLine", peakOneLine);
/*
		Element eFeas = DocumentFactory.getInstance().createElement("Features");
		FreeFeatures feas = ssm.getFeatures();
		if(feas!=null){
			HashMap <Integer, FreeFeature> feaMap = feas.getFeaMap();
			Iterator <Integer> iit = feaMap.keySet().iterator();
			while(iit.hasNext()){
				Integer scan = iit.next();
				FreeFeature f = feaMap.get(scan);
				Element ef = DocumentFactory.getInstance().createElement("Feature");
				ef.addAttribute("scannum", String.valueOf(scan));
				double feart = f.getRT();
				ef.addAttribute("retention_time", df4.format(feart));
				double inten = f.getIntensity();
				ef.addAttribute("intensity", df4.format(inten));
				eFeas.add(ef);			
			}
			
			eGlyscan.add(eFeas);
		}
*/

		root.add(eGlyscan);
	}
	
	/**
	 * Add the glycopeptides.
	 * @param pep
	 */
	public void addIdenPep(IGlycoPeptide pep){
		
		Element eIdenPep = DocumentFactory.getInstance().createElement("GlycoPeptides");

		eIdenPep.addAttribute("BaseName", pep.getBaseName());
		eIdenPep.addAttribute("ScanNum", String.valueOf(pep.getScanNumBeg()));
		eIdenPep.addAttribute("Sequence", pep.getSequence());
		eIdenPep.addAttribute("Charge", String.valueOf(pep.getCharge()));
		eIdenPep.addAttribute("Score", String.valueOf(pep.getPrimaryScore()));
		eIdenPep.addAttribute("rt", df4.format(pep.getRetentionTime()));
		eIdenPep.addAttribute("pepMr", df4.format(pep.getPepMrNoGlyco()));
		eIdenPep.addAttribute("Reference", pep.getProteinReferenceString());
		
		GlycoSite [] sites = pep.getAllGlycoSites();
		
		for(int i=0;i<sites.length;i++){
			
			Element eSite = DocumentFactory.getInstance().createElement("Site_Info");
			GlycoSite gsite = sites[i];
			ModSite ms = gsite.modifiedAt();
			int loc = gsite.modifLocation();
			char sym = gsite.symbol();
			double mass = gsite.getModMass();
			
			eSite.addAttribute("Site", ms.toString());
			eSite.addAttribute("Loc", String.valueOf(loc));
			eSite.addAttribute("Symbol", String.valueOf(sym));
			eSite.addAttribute("Mass", String.valueOf(mass));
			
			eIdenPep.add(eSite);
		}
		
		root.add(eIdenPep);
	}
	
	public void addBestEstimate(double[] fit){
		this.root.addAttribute("BestEstimates", fit[0]+"_"+fit[1]);
	}
	
	/**
	 * @param ms1TotalCurrent
	 */
	public void addTotalCurrent(double ms1TotalCurrent) {
		// TODO Auto-generated method stub
		
	}

	public GlycoLFFeasXMLReader2 createReader(){
		
		try {
			return new GlycoLFFeasXMLReader2(file);
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
}
