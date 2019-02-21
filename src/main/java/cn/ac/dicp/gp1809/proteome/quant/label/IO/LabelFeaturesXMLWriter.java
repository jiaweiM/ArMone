/* 
 ******************************************************************************
 * File:LabelPairXMLWriter.java * * * Created on 2010-6-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.IO;
 
import java.io.File;
import java.io.IOException;

import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.AbstractFeaturesXMLWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * @author ck
 *
 * @version 2010-6-24, 16:06:01
 */
public class LabelFeaturesXMLWriter extends AbstractFeaturesXMLWriter {
	
	protected double [] totalIntensity;

	public LabelFeaturesXMLWriter(String file, LabelType type, boolean gradient) throws IOException{
		this(new File(file), type, gradient);
	}
	
	public LabelFeaturesXMLWriter(File file, LabelType type, boolean gradient) throws IOException{
		super(file, type, gradient);
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
		
		LabelInfo [][] infos = type.getInfo();
		String name = type.getLabelName();
		short [] used = type.getUsed();
		for(int i=0;i<infos.length;i++){			
			Element eType = DocumentFactory.getInstance().createElement(name+"_"+used[i]);
			if(infos[i].length>0){
				for(int j=0;j<infos[i].length;j++){
					Element eInfo = DocumentFactory.getInstance().createElement("Label_Infomation");
					LabelInfo info = infos[i][j];
					String des = info.getDes();
					ModSite site = info.getSite();
					double mass = info.getMass();
					char symbol = info.getSymbol();
					String symbolStr = symbol != '\u0000' ? String.valueOf(symbol) : "";
					
					eInfo.addAttribute("Description", des);
					eInfo.addAttribute("Site", site.getModifAt());
					eInfo.addAttribute("Mass", String.valueOf(mass));
					eInfo.addAttribute("Symbol", symbolStr);
					eType.add(eInfo);
				}
			}else{
				Element eInfo = DocumentFactory.getInstance().createElement("Label_Infomation");
				eType.add(eInfo);
			}
			root.add(eType);
		}
	}
	
	/**
	 * @param pair
	 */
	public void addPeptidePair(PeptidePair pair) {
		// TODO Auto-generated method stub
		
		Element eFeaPair = DocumentFactory.getInstance().createElement("Features_Pair");
		IPeptide pep = pair.getPeptide();
		LabelFeatures features = pair.getFeatures();
		
		String scanName = pep.getBaseName();
		int beg = pep.getScanNumBeg();
		int end = pep.getScanNumEnd();
		int charge = pair.getCharge();
		String ref = pep.getProteinReferenceString();		
		String file = pair.getSrc();
		String seq = pep.getSequence();
		float score = pep.getPrimaryScore();
		boolean validate = features.isValidate();

		eFeaPair.addAttribute("BaseName", scanName);
		eFeaPair.addAttribute("ScanBeg", String.valueOf(beg));
		eFeaPair.addAttribute("ScanEnd", String.valueOf(end));
		eFeaPair.addAttribute("Charge", String.valueOf(charge));
		eFeaPair.addAttribute("Sequence", seq);
		eFeaPair.addAttribute("Reference", ref);
		eFeaPair.addAttribute("File", file);
		eFeaPair.addAttribute("Score", String.valueOf(score));
		eFeaPair.addAttribute("Accurate", validate? "1":"0");
		
		double [] masses = features.getMasses();
		double [] totalIntens = features.getTotalIntens();
		
		StringBuilder s1 = new StringBuilder();
		StringBuilder s2 = new StringBuilder();
		for(int i=0;i<masses.length;i++){
			s1.append(df4.format(masses[i])).append("_");
			s2.append(df4.format(totalIntens[i])).append("_");
		}
		
		eFeaPair.addAttribute("Masses", s1.substring(0, s1.length()-1));
		eFeaPair.addAttribute("TotalIntensity", s2.substring(0, s2.length()-1));

		double [] ratios = features.getRatios();
		double [] rias = features.getRias();
		StringBuilder s3 = new StringBuilder();
		StringBuilder s4 = new StringBuilder();
		for(int i=0;i<ratios.length;i++){
			s3.append(df4.format(ratios[i])).append("_");
			s4.append(df4.format(rias[i])).append("_");
		}
		eFeaPair.addAttribute("Ratio", s3.substring(0, s3.length()-1));
		eFeaPair.addAttribute("RIA", s4.substring(0, s4.length()-1));
		
		int [] scans = features.getScanList();
		double [] rt = features.getRTList();
		double [][] labelIntens = features.getIntenList();
		for(int i=0;i<scans.length;i++){
			Element eFeas = DocumentFactory.getInstance().createElement("Scan");
			eFeas.addAttribute("Scannum", String.valueOf(scans[i]));
			eFeas.addAttribute("Rt", df4.format(rt[i]));
			StringBuilder s5 = new StringBuilder();
			for(int j=0;j<labelIntens[i].length;j++){
				s5.append(df4.format(labelIntens[i][j])).append("_");
				this.totalIntensity[j] += labelIntens[i][j];
			}
			eFeas.addAttribute("Intensity", s5.substring(0, s5.length()-1));
			eFeaPair.add(eFeas);
		}
		root.add(eFeaPair);
	}
	
	/**
	 * Write the content.
	 */
	public void write() throws IOException{
		
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<totalIntensity.length;i++){
			sb.append(dfe4.format(totalIntensity[i])).append("_");
		}
		root.addAttribute("TotalIntensity", sb.substring(0, sb.length()-1));
		
		writer.write(document);
	}

/*	public void addFeatures(ArrayList<Features> feas, IPeptide pep){
		
		Element eFeaPair = DocumentFactory.getInstance().createElement("Features_Pair");
		
		String scanName = pep.getBaseName();
		int beg = pep.getScanNumBeg();
		int end = pep.getScanNumEnd();
		String ref = pep.getProteinReferenceString();		
		String seq = pep.getSequence();
		float score = pep.getPrimaryScore();

		eFeaPair.addAttribute("BaseName", scanName);
		eFeaPair.addAttribute("ScanBeg", String.valueOf(beg));
		eFeaPair.addAttribute("ScanEnd", String.valueOf(end));
		eFeaPair.addAttribute("Sequence", seq);
		eFeaPair.addAttribute("Reference", ref);
		eFeaPair.addAttribute("Score", String.valueOf(score));

		for(int i=0;i<feas.size();i++){
			Element eFeas = DocumentFactory.getInstance().createElement("Features");
			Features fs = feas.get(i);
			int isotope = fs.getIsotope();
			double pepMr = fs.getPepMass();

			eFeas.addAttribute("pepMr", String.valueOf(pepMr));
			eFeas.addAttribute("isotope_type", String.valueOf(isotope));
			eFeas.addAttribute("Possible_intensity", String.valueOf(fs.getPossibleInten()));
			eFeas.addAttribute("Total_intensity", String.valueOf(fs.getInten()));
			
			HashMap <Integer, Feature> feaMap = fs.getFeaMap();
			Iterator <Integer> iit = feaMap.keySet().iterator();
			while(iit.hasNext()){
				Integer scan = iit.next();
				Feature f = feaMap.get(scan);
				Element ef = DocumentFactory.getInstance().createElement("Feature");
				ef.addAttribute("scannum", String.valueOf(scan));
				double rt = f.getRT();
				ef.addAttribute("retention_time", df5.format(rt));
				double [] intens = f.getIntenList();
				ef.addAttribute("intensity_1", df5.format(intens[0]));
				ef.addAttribute("intensity_2", df5.format(intens[1]));
				ef.addAttribute("intensity_3", df5.format(intens[2]));
				eFeas.add(ef);			
			}
			eFeaPair.add(eFeas);
		}		
		root.add(eFeaPair);
	}
	
	public void addFeatures(Features [] feas, IPeptide pep){
		
		Element eFeaPair = DocumentFactory.getInstance().createElement("Features_Pair");
		
		String scanName = pep.getBaseName();
		int beg = pep.getScanNumBeg();
		int end = pep.getScanNumEnd();
		String ref = pep.getProteinReferenceString();		
		String seq = pep.getSequence();
		float score = pep.getPrimaryScore();

		eFeaPair.addAttribute("BaseName", scanName);
		eFeaPair.addAttribute("ScanBeg", String.valueOf(beg));
		eFeaPair.addAttribute("ScanEnd", String.valueOf(end));
		eFeaPair.addAttribute("Sequence", seq);
		eFeaPair.addAttribute("Reference", ref);
		eFeaPair.addAttribute("Score", String.valueOf(score));

		for(int i=0;i<feas.length;i++){
			Element eFeas = DocumentFactory.getInstance().createElement("Features");
			Features fs = feas[i];
			int isotope = fs.getIsotope();
			double pepMr = fs.getPepMass();

			eFeas.addAttribute("pepMr", String.valueOf(pepMr));
			eFeas.addAttribute("isotope_type", String.valueOf(isotope));
			eFeas.addAttribute("Possible_intensity", String.valueOf(fs.getPossibleInten()));
			eFeas.addAttribute("Total_intensity", String.valueOf(fs.getInten()));
			
			HashMap <Integer, Feature> feaMap = fs.getFeaMap();
			Iterator <Integer> iit = feaMap.keySet().iterator();
			while(iit.hasNext()){
				Integer scan = iit.next();
				Feature f = feaMap.get(scan);
				Element ef = DocumentFactory.getInstance().createElement("Feature");
				ef.addAttribute("scannum", String.valueOf(scan));
				double rt = f.getRT();
				ef.addAttribute("retention_time", df5.format(rt));
				double [] intens = f.getIntenList();
				ef.addAttribute("intensity_1", df5.format(intens[0]));
				ef.addAttribute("intensity_2", df5.format(intens[1]));
				ef.addAttribute("intensity_3", df5.format(intens[2]));
				eFeas.add(ef);			
			}
			eFeaPair.add(eFeas);
		}		
		root.add(eFeaPair);
	}
	
	public void addFeatures(PeptidePair pair){

		Element eFeaPair = DocumentFactory.getInstance().createElement("Features_Pair");
		IPeptide pep = pair.getPeptide();
		
		String scanName = pep.getBaseName();
		int beg = pep.getScanNumBeg();
		int end = pep.getScanNumEnd();
		String ref = pep.getProteinReferenceString();		
		String seq = pep.getSequence();
		double [] ratios = pair.getRatios();
		StringBuilder ratiosb = new StringBuilder();
		for(int i=0;i<ratios.length;i++){
			ratiosb.append(ratios[i]).append("_");
		}
		StringBuilder usesb = new StringBuilder();
		short [] use = pair.getUse();
		for(int i=0;i<use.length;i++){
			usesb.append(use[i]).append("_");
		}
		float score = pep.getPrimaryScore();

		eFeaPair.addAttribute("BaseName", scanName);
		eFeaPair.addAttribute("ScanBeg", String.valueOf(beg));
		eFeaPair.addAttribute("ScanEnd", String.valueOf(end));
		eFeaPair.addAttribute("Sequence", seq);
		eFeaPair.addAttribute("Reference", ref);
		eFeaPair.addAttribute("Ratios", ratiosb.substring(0, ratiosb.length()-1));
		eFeaPair.addAttribute("Use", usesb.substring(0, usesb.length()-1));
		eFeaPair.addAttribute("Score", String.valueOf(score));

		Features [] feas = pair.getFeas();
		for(int i=0;i<feas.length;i++){
			Element eFeas = DocumentFactory.getInstance().createElement("Features");
			Features fs = feas[i];
			int isotope = fs.getIsotope();
			double pepMr = fs.getPepMass();

			eFeas.addAttribute("pepMr", String.valueOf(pepMr));
			eFeas.addAttribute("isotope_type", String.valueOf(isotope));
			eFeas.addAttribute("Possible_intensity", String.valueOf(fs.getCalInten()));
			eFeas.addAttribute("Total_intensity", String.valueOf(fs.getInten()));
			
			HashMap <Integer, Feature> feaMap = fs.getFeaMap();
			Iterator <Integer> iit = feaMap.keySet().iterator();
			while(iit.hasNext()){
				Integer scan = iit.next();
				Feature f = feaMap.get(scan);
				Element ef = DocumentFactory.getInstance().createElement("Feature");
				ef.addAttribute("scannum", String.valueOf(scan));
				double rt = f.getRT();
				ef.addAttribute("retention_time", df5.format(rt));
				double [] intens = f.getIntenList();
				ef.addAttribute("intensity_1", df5.format(intens[0]));
				ef.addAttribute("intensity_2", df5.format(intens[1]));
				ef.addAttribute("intensity_3", df5.format(intens[2]));
				eFeas.add(ef);			
			}
			eFeaPair.add(eFeas);
		}		
		root.add(eFeaPair);
	}
*/
	/**
	 * Create a reader to read this file.
	 * @return
	 * @throws DocumentException
	 */
	public LabelFeaturesXMLReader createReader() throws DocumentException{
		return new LabelFeaturesXMLReader(file);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String file = "E:\\test.xml";
//		LabelPairXMLWriter writer = new LabelPairXMLWriter(file);
//		writer.close();

	}

	

}
