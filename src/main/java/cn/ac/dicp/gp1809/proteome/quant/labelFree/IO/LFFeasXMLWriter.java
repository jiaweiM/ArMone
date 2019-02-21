/* 
 ******************************************************************************
 * File: LFFeasXMLWriter.java * * * Created on 2010-11-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree.IO;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.profile.IO.AbstractFeaturesXMLWriter;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeature;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * @author ck
 *
 * @version 2010-11-1, 15:11:02
 */
public class LFFeasXMLWriter extends AbstractFeaturesXMLWriter
{

	public LFFeasXMLWriter(String file) throws IOException{
		super(file);
	}
	
	public LFFeasXMLWriter(File file) throws IOException{
		super(file);
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLWriter#initial()
	 */
	@Override
	protected void initial() {
		// TODO Auto-generated method stub
		this.root = DocumentFactory.getInstance().createElement("ProFeatures");
		this.document.setRootElement(root);
		root.addAttribute("Label_Type", "Label_Free");
	}

	public void addTotalCurrent(double totalCurrent){
		root.addAttribute("TotalCurrent", String.valueOf(totalCurrent));
	}

	/**
	 * @param feasList
	 * @param pep
	 */
	public void addFeature(FreeFeatures feas, IPeptide pep) {
		
		// TODO Auto-generated method stub
		Element eFeas = DocumentFactory.getInstance().createElement("Features");
		
		String baseName = pep.getBaseName();
		int beg = pep.getScanNumBeg();
		int end = pep.getScanNumEnd();
		String ref = pep.getProteinReferenceString();		
		String seq = pep.getSequence();
		double pepMr = feas.getPepMass();
		int charge = pep.getCharge();
		
		eFeas.addAttribute("BaseName", baseName);
		eFeas.addAttribute("scanBeg", String.valueOf(beg));
		eFeas.addAttribute("scanEnd", String.valueOf(end));
		eFeas.addAttribute("Sequence", seq);
		eFeas.addAttribute("Charge", String.valueOf(charge));
		eFeas.addAttribute("pepMr", df5.format(pepMr));
		eFeas.addAttribute("Reference", ref);

		HashMap <Integer, FreeFeature> feaMap = feas.getFeaMap();
		Iterator <Integer> iit = feaMap.keySet().iterator();
		while(iit.hasNext()){
			Integer scan = iit.next();
			FreeFeature f = feaMap.get(scan);
			Element ef = DocumentFactory.getInstance().createElement("Feature");
			ef.addAttribute("scannum", String.valueOf(scan));
			double rt = f.getRT();
			ef.addAttribute("retention_time", df5.format(rt));
			double inten = f.getIntensity();
			ef.addAttribute("intensity", df5.format(inten));
			eFeas.add(ef);			
		}

		root.add(eFeas);
	}

	/**
	 * Create a reader to read this file.
	 * @return
	 * @throws DocumentException
	 */
	public LFFeasXMLReader createReader() throws DocumentException{
		return new LFFeasXMLReader(file);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
