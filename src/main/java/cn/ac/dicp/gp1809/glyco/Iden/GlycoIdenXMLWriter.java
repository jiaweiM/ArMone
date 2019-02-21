/* 
 ******************************************************************************
 * File: GlycoIdenXMLWriter.java * * * Created on 2012-5-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Iden;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2012-5-16, 08:53:10
 */
public class GlycoIdenXMLWriter {
	
	private Document document;
	private XMLWriter writer;
	private Element root;
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	private DecimalFormat df2 = DecimalFormats.DF0_2;
	private HashMap <Integer, String> peakOneLineMap;

	public GlycoIdenXMLWriter(String file) throws IOException{
		
		this.document = DocumentHelper.createDocument();
		this.writer = new XMLWriter(new FileWriter(file));
		this.initial();
	}

	public GlycoIdenXMLWriter(File file) throws IOException{
		
		this.document = DocumentHelper.createDocument();
		this.writer = new XMLWriter(new FileWriter(file));
		this.initial();
	}

	/**
	 * 
	 */
	private void initial() {
		// TODO Auto-generated method stub
		
		this.root = DocumentFactory.getInstance().createElement("Glycans");
		this.document.setRootElement(root);		
		this.peakOneLineMap = new HashMap <Integer, String>();
	}

	public void addGlycan(NGlycoSSM ssm){
		
		Element eGlyscan = DocumentFactory.getInstance().createElement("Glycan");
		
		int scannum = ssm.getScanNum();
		double rt = ssm.getRT();
		String peakOneLine = ssm.getPeakOneLine();
		
		eGlyscan.addAttribute("ScanNum", String.valueOf(scannum));
		eGlyscan.addAttribute("RT", df4.format(rt));
		this.peakOneLineMap.put(scannum, peakOneLine);

		GlycoTree tree = ssm.getGlycoTree();
		int rank = ssm.getRank();
		double score = ssm.getScore();
		double monoMass = ssm.getGlycoMass();
		StringBuilder labelSb = new StringBuilder();
		HashSet <Integer> matchedPeaks = ssm.getMatchedPeaks();
		Iterator <Integer> it = matchedPeaks.iterator();
		
		while(it.hasNext()){
			Integer id = it.next();
			labelSb.append(id).append("_");
		}
		if(labelSb.length()>0){
			labelSb = labelSb.deleteCharAt(labelSb.length()-1);
		}

		eGlyscan.addAttribute("Rank", String.valueOf(rank));
		eGlyscan.addAttribute("Score", df2.format(score));
		eGlyscan.addAttribute("PrecorsorMz", df4.format(ssm.getPreMz()));
		eGlyscan.addAttribute("PrecorsorMr", df4.format(ssm.getPreMr()));
		eGlyscan.addAttribute("GlycoMass", df4.format(monoMass));
		eGlyscan.addAttribute("PeptideMass", df4.format(ssm.getPepMass()));
		eGlyscan.addAttribute("PeptideMassExperiment", df4.format(ssm.getPepMassExperiment()));
		eGlyscan.addAttribute("MatchedPeaks", labelSb.toString());
		eGlyscan.addAttribute("GlycoCT", tree.getGlycoCT());
		eGlyscan.addAttribute("Name", tree.getIupacName());

		this.root.add(eGlyscan);
	}
	
	public void addGlycan(NGlycoSSM ssm, String sequence){
		
		Element eGlyscan = DocumentFactory.getInstance().createElement("Glycan");
		
		int scannum = ssm.getScanNum();
		double rt = ssm.getRT();
		String peakOneLine = ssm.getPeakOneLine();
		
		eGlyscan.addAttribute("ScanNum", String.valueOf(scannum));
		eGlyscan.addAttribute("RT", df4.format(rt));
		this.peakOneLineMap.put(scannum, peakOneLine);

		GlycoTree tree = ssm.getGlycoTree();
		int rank = ssm.getRank();
		double score = ssm.getScore();
		double monoMass = ssm.getGlycoMass();
		StringBuilder labelSb = new StringBuilder();
		HashSet <Integer> matchedPeaks = ssm.getMatchedPeaks();
		Iterator <Integer> it = matchedPeaks.iterator();
		
		while(it.hasNext()){
			Integer id = it.next();
			labelSb.append(id).append("_");
		}
		if(labelSb.length()>0){
			labelSb = labelSb.deleteCharAt(labelSb.length()-1);
		}

		eGlyscan.addAttribute("Rank", String.valueOf(rank));
		eGlyscan.addAttribute("Score", df4.format(score));
		eGlyscan.addAttribute("Sequence", sequence);
		eGlyscan.addAttribute("PrecorsorMz", df4.format(ssm.getPreMz()));
		eGlyscan.addAttribute("PrecorsorMr", df4.format(ssm.getPreMr()));
		eGlyscan.addAttribute("GlycoMass", df4.format(monoMass));
		eGlyscan.addAttribute("PeptideMass", df4.format(ssm.getPepMass()));
		eGlyscan.addAttribute("PeptideMassExperiment", df4.format(ssm.getPepMassExperiment()));
		eGlyscan.addAttribute("MatchedPeaks", labelSb.toString());
		eGlyscan.addAttribute("GlycoCT", tree.getGlycoCT());
		eGlyscan.addAttribute("Name", tree.getIupacName());

		this.root.add(eGlyscan);
	}	

	/**
	 * Write the content.
	 */
	public void write() throws IOException{
		
		Iterator <Integer> it = this.peakOneLineMap.keySet().iterator();
		
		while(it.hasNext()){
			
			Integer scannum = it.next();
			String peakOneLine = this.peakOneLineMap.get(scannum);
			
			Element eSpectrum = DocumentFactory.getInstance().createElement("Spectrum");
			eSpectrum.addAttribute("Scannum", String.valueOf(scannum));
			eSpectrum.addAttribute("PeakOneLine", peakOneLine);
			
			this.root.add(eSpectrum);
		}
		
		writer.write(document);
	}
	
	public void close() throws IOException{
		writer.close();
	}
}
