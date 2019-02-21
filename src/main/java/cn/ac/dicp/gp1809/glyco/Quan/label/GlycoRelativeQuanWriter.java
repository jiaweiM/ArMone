/* 
 ******************************************************************************
 * File: GlycoRelativeQuanWriter.java * * * Created on 2013-8-18
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelQuanUnit;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import org.dom4j.DocumentException;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2013-8-18, 8:49:46
 */
public class GlycoRelativeQuanWriter {

	private LabelQuanUnit[] quanPeptides;
	private QuanResult[] quanProteins;
	private GlycoQuanResult [] quanGlycoPeptides;
	private NGlycoSSM [] matchedSSMs;
	private IGlycoPeptide [] glycoPeptides;
	
	private String[] ratioNames;
	private ExcelWriter writer;
	private ExcelFormat format;
	
	private ProteinNameAccesser accesser;
	private static DecimalFormat df4 = DecimalFormats.DF0_4;
	private static DecimalFormat dfe3 = DecimalFormats.DF_E3;
	
	public GlycoRelativeQuanWriter(String deglyco, String glyco, String out, String [] ratioNames) throws Exception{
		
		LabelFeaturesXMLReader deglycoReader = new LabelFeaturesXMLReader(deglyco);
		GlycoLabelFeaturesXMLReader glycoReader = new GlycoLabelFeaturesXMLReader(glyco);
		
		this.quanPeptides = deglycoReader.getAllLabelQuanUnits(false);
		this.quanProteins = deglycoReader.getAllResult(false, false, new int[]{0});
		this.quanGlycoPeptides = glycoReader.getAllResult();
		this.matchedSSMs = glycoReader.getMatchedGlycoSpectra();
		this.glycoPeptides = glycoReader.getAllGlycoPeptides();
		this.accesser = glycoReader.getProNameAccesser();
		
		this.ratioNames = ratioNames;
		this.writer = new ExcelWriter(out);
		this.format = ExcelFormat.normalFormat;
		
		this.addTitle();
	}
	
	public GlycoRelativeQuanWriter(String deglyco, String glyco, String out, String [] ratioNames, double[] theoryRatio) throws Exception{
		
		LabelFeaturesXMLReader deglycoReader = new LabelFeaturesXMLReader(deglyco);
		deglycoReader.setTheoryRatio(theoryRatio);
		GlycoLabelFeaturesXMLReader glycoReader = new GlycoLabelFeaturesXMLReader(glyco);
		glycoReader.setTheoryRatio(theoryRatio);
		
		this.quanPeptides = deglycoReader.getAllLabelQuanUnits(true);
		this.quanProteins = deglycoReader.getAllResult(false, true, new int[]{0});
		this.quanGlycoPeptides = glycoReader.getAllResult(true);
		this.matchedSSMs = glycoReader.getMatchedGlycoSpectra();
		this.glycoPeptides = glycoReader.getAllGlycoPeptides();
		this.accesser = glycoReader.getProNameAccesser();
		
		this.ratioNames = ratioNames;
		this.writer = new ExcelWriter(out);
		this.format = ExcelFormat.normalFormat;
		
		this.addTitle();
	}
	
	private void addTitle() throws RowsExceededException, WriteException{
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append("Sequence\t");
		sb1.append("Theor peptide mw\t");
		sb1.append("Theor glycan mw\t");
		sb1.append("IUPAC Name\t");
		sb1.append("Type\t");
		sb1.append("Matched glycan scnans\t");
		sb1.append("Reference\t");
		sb1.append("Site\t");
		
		StringBuilder sb2 = new StringBuilder();
		for(int i=0;i<ratioNames.length;i++){
			sb2.append("Glycopeptide_"+ratioNames[i]).append("\t");
		}
		for(int i=0;i<ratioNames.length;i++){
			sb2.append("RIA_"+ratioNames[i]).append("\t");
		}
		sb2.append("Intensity 1\tIntensity 2\t");

		for(int i=0;i<ratioNames.length;i++){
			sb2.append("Deglycopeptide_"+ratioNames[i]).append("\t");
		}
		for(int i=0;i<ratioNames.length;i++){
			sb2.append("RIA_"+ratioNames[i]).append("\t");
		}
		sb2.append("Intensity 1\tIntensity 2\t");

		for(int i=0;i<ratioNames.length;i++){
			sb2.append("Relative_"+ratioNames[i]).append("\t");
		}
		
		for(int i=0;i<ratioNames.length;i++){
			sb2.append("Glycoprotein_"+ratioNames[i]).append("\t");
		}
		
		for(int i=0;i<ratioNames.length;i++){
			sb2.append("Relative_"+ratioNames[i]).append("\t");
		}
		
		this.writer.addTitle(sb1.toString()+sb2.toString(), 0, format);
		this.writer.addTitle(sb1.toString()+sb2.toString(), 1, format);
	}
	
	public void write() throws RowsExceededException, WriteException, IOException{
		
		int deglycoCount = 0;
		int log2Count = 0;
		NGlycoPepCriteria ngp = new NGlycoPepCriteria(true);
		HashMap<String, LabelQuanUnit> quanPeptideMap = new HashMap<String, LabelQuanUnit>();
		for(int i=0;i<this.quanPeptides.length;i++){
			IPeptide peptide = this.quanPeptides[i].getPeptide();
			quanPeptideMap.put(peptide.getSequence(), this.quanPeptides[i]);
			if(ngp.filter(peptide) && this.quanPeptides[i].isAccurate()){
				deglycoCount++;
				double ratio = this.quanPeptides[i].getRatio()[0];
				if(ratio>=0.5 && ratio<=2){
					log2Count++;
				}
			}
		}
System.out.println(deglycoCount+"\t"+log2Count+"\t"+(double)log2Count/(double)deglycoCount);		

		HashMap<String, double[]> proteinRatioMap = new HashMap<String, double[]>();
		for(int i=0;i<this.quanProteins.length;i++){
			LabelQuanUnit[] units = this.quanProteins[i].getUnits();
			for(int j=0;j<units.length;j++){
				proteinRatioMap.put(units[j].getSequence(), this.quanProteins[i].getRatio());
			}
		}
		
		int glycoCount = 0;
		int log2Count2 = 0;
		
		for(int i=0;i<this.quanGlycoPeptides.length;i++){
			
			IGlycoPeptide peptide = this.quanGlycoPeptides[i].getPeptide();
			NGlycoSSM ssm = this.quanGlycoPeptides[i].getSsm();

			StringBuilder sb = new StringBuilder();
			String sequence = peptide.getSequence();
			sb.append(sequence).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");
			
			Integer [] ssmIdList = this.quanGlycoPeptides[i].getSSMIds();
			for(int k=0;k<ssmIdList.length;k++){
				sb.append(matchedSSMs[ssmIdList[k]].getScanNum()).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("\t");

			GlycoSite[] sites = peptide.getAllGlycoSites();
			int[] loc = new int[sites.length];
			for (int k= 0; k < loc.length; k++) {
				loc[k] = sites[k].modifLocation();
			}
			HashMap<String, SeqLocAround> slamap = peptide
					.getPepLocAroundMap();

			StringBuilder sitesb = new StringBuilder();
			StringBuilder refsb = new StringBuilder();
			HashSet<ProteinReference> refset = peptide
					.getProteinReferences();
			for (ProteinReference ref : refset) {
				SimpleProInfo info = accesser.getProInfo(ref.getName());
				refsb.append(info.getRef()).append(";");

				SeqLocAround sla = slamap.get(ref.toString());
				for (int k = 0; k < loc.length; k++) {
					loc[k] = sites[k].modifLocation();
					sitesb.append(sla.getBeg() + loc[k] - 1).append("/");
				}
				sitesb.deleteCharAt(sitesb.length() - 1);
				sitesb.append(";");
			}
			sitesb.deleteCharAt(sitesb.length() - 1);
			refsb.deleteCharAt(refsb.length() - 1);

			sb.append(refsb).append("\t");
			sb.append(sitesb).append("\t");
			
			double[] glycoPepRatio = this.quanGlycoPeptides[i].getRatio();
			double[] glycoPepRIA = this.quanGlycoPeptides[i].getRIA();

			boolean sheet2 = this.quanGlycoPeptides[i].isAccurate() ? false : true;
			for(int j=0;j<glycoPepRatio.length;j++){
				sb.append(df4.format(glycoPepRatio[j])).append("\t");
			}
			for(int j=0;j<glycoPepRIA.length;j++){
				sb.append(df4.format(glycoPepRIA[j])).append("\t");
			}
			double[] intensity = this.quanGlycoPeptides[i].getIntensity();
			for(int j=0;j<intensity.length;j++){
				sb.append(dfe3.format(intensity[j])).append("\t");
			}

			if(quanPeptideMap.containsKey(sequence)){
				LabelQuanUnit quanUnit = quanPeptideMap.get(sequence);
				double[] peptideRatio = quanUnit.getRatio();
				double[] peptideRIA = quanUnit.getRIA();
				double[] peptideIntensity = quanUnit.getIntensity();
				for(int j=0;j<peptideRatio.length;j++){
					sb.append(df4.format(peptideRatio[j])).append("\t");
				}
				for(int j=0;j<peptideRIA.length;j++){
					sb.append(df4.format(peptideRIA[j])).append("\t");
				}
				for(int j=0;j<peptideIntensity.length;j++){
					sb.append(dfe3.format(peptideIntensity[j])).append("\t");
				}
				for(int j=0;j<glycoPepRatio.length;j++){
					double relaRatio = peptideRatio[j]==0 ? 0 : glycoPepRatio[j]/peptideRatio[j];
					sb.append(df4.format(relaRatio)).append("\t");
				}
				if(!sheet2){
					sheet2 = quanUnit.isAccurate() ? false : true;
				}
			}else{
				sheet2 = true;
				for(int j=0;j<glycoPepRatio.length;j++){
					sb.append("0.0\t0.0\t0.0\t0.0\t0.0\t");
				}
			}
			if(proteinRatioMap.containsKey(sequence)){
				double[] proteinRatio = proteinRatioMap.get(sequence);
				for(int j=0;j<proteinRatio.length;j++){
					sb.append(proteinRatio[j]).append("\t");
				}
				for(int j=0;j<glycoPepRatio.length;j++){
					double relaRatio = proteinRatio[j]==0 ? 0 : glycoPepRatio[j]/proteinRatio[j];
					sb.append(df4.format(relaRatio)).append("\t");
				}
			}else{
				for(int j=0;j<glycoPepRatio.length;j++){
					sb.append("0.0\t0.0\t");
				}
			}
			if(sheet2){
				this.writer.addContent(sb.toString(), 1, format);
			}else{
				this.writer.addContent(sb.toString(), 0, format);
				glycoCount++;
				if(glycoPepRatio[0]>=0.5 && glycoPepRatio[0]<=2){
					log2Count2++;
				}
			}
		}
		this.writer.close();
		
		System.out.println(glycoCount+"\t"+log2Count2+"\t"+(double)log2Count2/(double)glycoCount);		

	}


	private static void gradWrite(String deglyco, String glyco, String out, String[] ratioNames, double[] theoryRatio) 
			throws DocumentException, RowsExceededException, WriteException, IOException{
		
		FileFilter filefilter = new FileFilter(){
			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("pxml"))
					return true;
				
				return false;
			}
		};
		
		File[] deglycofiles = (new File(deglyco)).listFiles(filefilter);
		Arrays.sort(deglycofiles);
		
		File[] glycofiles = (new File(glyco)).listFiles(filefilter);
		Arrays.sort(glycofiles);
		
		if(deglycofiles.length!=glycofiles.length) return;
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append("Sequence\t");
		sb1.append("Theor peptide mw\t");
		sb1.append("Theor glycan mw\t");
		sb1.append("IUPAC Name\t");
		sb1.append("Type\t");
		sb1.append("Reference\t");
		sb1.append("Site\t");
		
		StringBuilder sb2 = new StringBuilder();
		for(int i=0;i<ratioNames.length;i++){
			sb2.append("Glycopeptide_"+ratioNames[i]).append("\t");
		}
		for(int i=0;i<ratioNames.length;i++){
			sb2.append("RIA_"+ratioNames[i]).append("\t");
		}
		sb2.append("Intensity 1\tIntensity 2\t");

		for(int i=0;i<ratioNames.length;i++){
			sb2.append("Deglycopeptide_"+ratioNames[i]).append("\t");
		}
		for(int i=0;i<ratioNames.length;i++){
			sb2.append("RIA_"+ratioNames[i]).append("\t");
		}
		sb2.append("Intensity 1\tIntensity 2\t");

		for(int i=0;i<ratioNames.length;i++){
			sb2.append("Relative_"+ratioNames[i]).append("\t");
		}

		writer.addTitle(sb1.toString()+sb2.toString(), 0, format);
		writer.addTitle(sb1.toString()+sb2.toString(), 1, format);
		
		HashMap<String, LabelQuanUnit> peppairmap = new HashMap<String, LabelQuanUnit>();
		for(int i=0;i<deglycofiles.length;i++){
			System.out.println(deglycofiles[i].getName());
			LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(deglycofiles[i]);
			reader.setTheoryRatio(theoryRatio);
			PeptidePair[] pairs = reader.getAllSelectedPairs();
			for(int j=0;j<pairs.length;j++){
				LabelFeatures feas = pairs[j].getFeatures();
				feas.setNormal(true);
				feas.setNormalFactor(reader.getRealNormalFactor());
				
				IPeptide peptide = pairs[j].getPeptide();
				String key = peptide.getSequence();
				
				if(peppairmap.containsKey(key)){
					peppairmap.get(key).addRatioInfo(pairs[j].getSelectRatios(), pairs[j].getSelectRIA(), pairs[j].getTotalIntens(), pairs[j].isAccurate());
				}else{
					LabelQuanUnit unit = new LabelQuanUnit(peptide);
					unit.addRatioInfo(pairs[j].getSelectRatios(), pairs[j].getSelectRIA(), pairs[j].getTotalIntens(), pairs[j].isAccurate());
					peppairmap.put(key, unit);
				}
			}
		}

		int deglycoCount = 0;
		NGlycoPepCriteria ngp = new NGlycoPepCriteria(true);
		HashMap<String, LabelQuanUnit> quanPeptideMap = new HashMap<String, LabelQuanUnit>();
		LabelQuanUnit [] units = peppairmap.values().toArray(new LabelQuanUnit[peppairmap.size()]);
		for(int i=0;i<units.length;i++){
			units[i].initial();
			IPeptide peptide = units[i].getPeptide();
			quanPeptideMap.put(peptide.getSequence(), units[i]);

			if(ngp.filter(peptide) && units[i].isAccurate()){
				deglycoCount++;
			}
		}
System.out.println(deglycoCount);
		ProteinNameAccesser accesser = null;
		HashMap<String, GlycoQuanResult> glycopairmap = new HashMap<String, GlycoQuanResult>();
		for(int i=0;i<glycofiles.length;i++){
			System.out.println(glycofiles[i].getName());
			GlycoLabelFeaturesXMLReader reader = new GlycoLabelFeaturesXMLReader(glycofiles[i]);
			reader.setTheoryRatio(theoryRatio);
			IGlycoPeptide[] peps = reader.getAllGlycoPeptides();
			NGlycoSSM[] matchedssms = reader.getMatchedGlycoSpectra();
			GlycoPeptideLabelPair[] pairs = reader.getAllSelectedPairs();
			if(accesser==null){
				accesser = reader.getProNameAccesser();
			}else{
				accesser.appand(reader.getProNameAccesser());
			}
			for(int j=0;j<pairs.length;j++){
				LabelFeatures feas = pairs[j].getFeatures();
				feas.setNormal(true);
				feas.setNormalFactor(reader.getRealNormalFactor());
				
				IGlycoPeptide peptide = peps[pairs[j].getPeptideId()];
				NGlycoSSM ssm = matchedssms[pairs[j].getDeleSSMId()];
//				String key = peptide.getSequence()+"_"+ssm.getGlycanid()[0]+"_"+ssm.getGlycanid()[1];
				String key = peptide.getSequence()+"_"+ssm.getGlycoTree().getIupacName();
				if(glycopairmap.containsKey(key)){
					glycopairmap.get(key).addRatioInfo(pairs[j].getSelectRatios(), pairs[j].getSelectRIA(), pairs[j].getTotalIntens(), pairs[j].isAccurate());
					glycopairmap.get(key).addSSMIds(pairs[j].getSsmsIds());
				}else{
					GlycoQuanResult result = new GlycoQuanResult(peptide, ssm);
					result.addRatioInfo(pairs[j].getSelectRatios(), pairs[j].getSelectRIA(), pairs[j].getTotalIntens(), pairs[j].isAccurate());
					result.addSSMIds(pairs[j].getSsmsIds());
					result.setPeptideId(pairs[j].getPeptideId());
					glycopairmap.put(key, result);
				}
			}
		}
		
		GlycoQuanResult [] results = glycopairmap.values().toArray(new GlycoQuanResult[glycopairmap.size()]);
		for(int i=0;i<results.length;i++){
			results[i].initial();
		}
		
		for(int i=0;i<results.length;i++){
			
			IGlycoPeptide peptide = results[i].getPeptide();
			NGlycoSSM ssm = results[i].getSsm();

			StringBuilder sb = new StringBuilder();
			String sequence = peptide.getSequence();
			sb.append(sequence).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			GlycoSite[] sites = peptide.getAllGlycoSites();
			int[] loc = new int[sites.length];
			for (int k= 0; k < loc.length; k++) {
				loc[k] = sites[k].modifLocation();
			}
			HashMap<String, SeqLocAround> slamap = peptide
					.getPepLocAroundMap();

			StringBuilder sitesb = new StringBuilder();
			StringBuilder refsb = new StringBuilder();
			HashSet<ProteinReference> refset = peptide
					.getProteinReferences();
			for (ProteinReference ref : refset) {
				SimpleProInfo info = accesser.getProInfo(ref.getName());
				refsb.append(info.getRef()).append(";");

				SeqLocAround sla = slamap.get(ref.toString());
				for (int k = 0; k < loc.length; k++) {
					loc[k] = sites[k].modifLocation();
					sitesb.append(sla.getBeg() + loc[k] - 1).append("/");
				}
				sitesb.deleteCharAt(sitesb.length() - 1);
				sitesb.append(";");
			}
			sitesb.deleteCharAt(sitesb.length() - 1);
			refsb.deleteCharAt(refsb.length() - 1);

			sb.append(refsb).append("\t");
			sb.append(sitesb).append("\t");
			
			double[] glycoPepRatio = results[i].getRatio();
			double[] glycoPepRIA = results[i].getRIA();

			boolean sheet2 = results[i].isAccurate() ? false : true;
			for(int j=0;j<glycoPepRatio.length;j++){
				sb.append(df4.format(glycoPepRatio[j])).append("\t");
			}
			for(int j=0;j<glycoPepRIA.length;j++){
				sb.append(df4.format(glycoPepRIA[j])).append("\t");
			}
			double[] intensity = results[i].getIntensity();
			for(int j=0;j<intensity.length;j++){
				sb.append(dfe3.format(intensity[j])).append("\t");
			}

			if(quanPeptideMap.containsKey(sequence)){
				LabelQuanUnit quanUnit = quanPeptideMap.get(sequence);
				double[] peptideRatio = quanUnit.getRatio();
				double[] peptideRIA = quanUnit.getRIA();
				double[] peptideIntensity = quanUnit.getIntensity();
				for(int j=0;j<peptideRatio.length;j++){
					sb.append(df4.format(peptideRatio[j])).append("\t");
				}
				for(int j=0;j<peptideRIA.length;j++){
					sb.append(df4.format(peptideRIA[j])).append("\t");
				}
				for(int j=0;j<peptideIntensity.length;j++){
					sb.append(dfe3.format(peptideIntensity[j])).append("\t");
				}
				for(int j=0;j<glycoPepRatio.length;j++){
					double relaRatio = peptideRatio[j]==0 ? 0 : glycoPepRatio[j]/peptideRatio[j];
					sb.append(df4.format(relaRatio)).append("\t");
				}
				if(!sheet2){
					sheet2 = quanUnit.isAccurate() ? false : true;
				}
			}else{
				sheet2 = true;
				for(int j=0;j<glycoPepRatio.length;j++){
					sb.append("0.0\t0.0\t0.0\t0.0\t0.0\t");
				}
			}

			if(sheet2){
				writer.addContent(sb.toString(), 1, format);
			}else{
				writer.addContent(sb.toString(), 0, format);
			}
		}
		writer.close();
	}
	
	/**
	 * @deprecated
	 * @throws RowsExceededException
	 * @throws WriteException
	 * @throws IOException
	 */
	private void write2() throws RowsExceededException, WriteException, IOException{/*

HashMap<String, NGlycoSSM> ssmMap = new HashMap<String, NGlycoSSM>();
		HashMap<String, HashSet<Integer>> scanMap = new HashMap<String, HashSet<Integer>>();
		HashMap<String, int[]> labelTypeMap = new HashMap<String, int[]>();
		for(int i=0;i<this.matchedSSMs.length;i++){
			
			NGlycoSSM ssm = this.matchedSSMs[i];
			IGlycoPeptide peptide = this.glycoPeptides[ssm.getPeptideid()];
			int [] glycoId = ssm.getGlycanid();
			String key = glycoId[0]+"_"+glycoId[1]+"_"+peptide.getSequence();
			if(ssmMap.containsKey(key)){
				scanMap.get(key).add(ssm.getScanNum());
				int labelType = ssm.getPepLabelType(ssm.getPeptideid());
				labelTypeMap.get(key)[labelType] = 1;
			}else{
				ssmMap.put(key, ssm);
				HashSet<Integer> set = new HashSet<Integer>();
				set.add(ssm.getScanNum());
				scanMap.put(key, set);
				int[] label = new int[2];
				int labelType = ssm.getPepLabelType(ssm.getPeptideid());
				label[labelType] = 1;
				labelTypeMap.put(key, label);
			}
		}
		
		Iterator<String> it = ssmMap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			NGlycoSSM ssm = ssmMap.get(key);
			int[] label = labelTypeMap.get(key);
			
			IGlycoPeptide peptide = this.glycoPeptides[ssm.getPeptideid()];
			StringBuilder sb = new StringBuilder();
			String sequence = peptide.getSequence();
			sb.append(sequence).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			GlycoSite[] sites = peptide.getAllGlycoSites();
			int[] loc = new int[sites.length];
			for (int k= 0; k < loc.length; k++) {
				loc[k] = sites[k].modifLocation();
			}
			HashMap<String, SeqLocAround> slamap = peptide
					.getPepLocAroundMap();

			StringBuilder sitesb = new StringBuilder();
			StringBuilder refsb = new StringBuilder();
			HashSet<ProteinReference> refset = peptide
					.getProteinReferences();
			for (ProteinReference ref : refset) {
				SimpleProInfo info = accesser.getProInfo(ref.getName());
				refsb.append(info.getRef()).append(";");

				SeqLocAround sla = slamap.get(ref.toString());
				for (int k = 0; k < loc.length; k++) {
					loc[k] = sites[k].modifLocation();
					sitesb.append(sla.getBeg() + loc[k] - 1).append("/");
				}
				sitesb.deleteCharAt(sitesb.length() - 1);
				sitesb.append(";");
			}
			sitesb.deleteCharAt(sitesb.length() - 1);
			refsb.deleteCharAt(refsb.length() - 1);

			sb.append(refsb).append("\t");
			sb.append(sitesb).append("\t");
			
			for(Integer scannum : scanMap.get(key)){
				sb.append(scannum).append(";");
			}
			sb.append("\t");
			
			if(label[0]==1){
				if(label[1]==1){
					sb.append("0_1\t");
				}else{
					sb.append("0\t");
				}
			}else{
				if(label[1]==1){
					sb.append("1\t");
				}else{
					sb.append("\t");
				}
			}
			
			if(quanPeptideMap.containsKey(sequence)){
				double[] peptideRatio = quanPeptideMap.get(sequence).getRatio();
				for(int j=0;j<peptideRatio.length;j++){
					sb.append(peptideRatio[j]).append("\t");
				}
				double[] peptideIntensity = quanPeptideMap.get(sequence).getIntensity();
				for(int j=0;j<peptideIntensity.length;j++){
					sb.append(dfe3.format(peptideIntensity[j])).append("\t");
				}
			}else{
				sb.append("0.0\t0.0\t0.0\t");
			}
			if(proteinRatioMap.containsKey(sequence)){
				double[] proteinRatio = proteinRatioMap.get(sequence);
				for(int j=0;j<proteinRatio.length;j++){
					sb.append(proteinRatio[j]).append("\t");
				}
			}else{
				sb.append("0.0\t");
			}
			
			this.writer.addContent(sb.toString(), 2, format);
		}
		
		HashMap<String, ArrayList<GlycoQuanResult>> map = new HashMap<String, ArrayList<GlycoQuanResult>>();
		
		for(int i=0;i<results.length;i++){
			
			String sequence = results[i].getPeptide().getSequence();
			if(map.containsKey(sequence)){
				map.get(sequence).add(results[i]);
			}else{
				ArrayList<GlycoQuanResult> list = new ArrayList<GlycoQuanResult>();
				list.add(results[i]);
				map.put(sequence, list);
			}
		}

		int count = 0;
		for(int i=0;i<units.length;i++){
			
			String sequence = units[i].getPeptide().getSequence();
			if(map.containsKey(sequence)){
				
				ArrayList<GlycoQuanResult> list = map.get(sequence);
				double [] deglycoRatio = units[i].getRatio();
				
				for(int j=0;j<list.size();j++){
					
					GlycoQuanResult result = list.get(j);
					IGlycoPeptide peptide = result.getPeptide();
					NGlycoSSM ssm = result.getSsm();
					double [] glycoRatio = result.getRatio();
					
					if(deglycoRatio.length!=glycoRatio.length) continue;

					StringBuilder sb = new StringBuilder();

					sb.append(peptide.getSequence()).append("\t");
					sb.append(peptide.getPepMrNoGlyco()).append("\t");
					sb.append(ssm.getGlycoMass()).append("\t");
					sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
					sb.append(ssm.getGlycoTree().getType()).append("\t");
					
					Integer [] ssmIdList = result.getSSMIds();
					for(int k=0;k<ssmIdList.length;k++){
						sb.append(matchedSSMs[ssmIdList[k]].getScanNum()).append(",");
					}
					sb.deleteCharAt(sb.length()-1);
					sb.append("\t");

					GlycoSite[] sites = peptide.getAllGlycoSites();
					int[] loc = new int[sites.length];
					for (int k= 0; k < loc.length; k++) {
						loc[k] = sites[k].modifLocation();
					}
					HashMap<String, SeqLocAround> slamap = peptide
							.getPepLocAroundMap();

					StringBuilder sitesb = new StringBuilder();
					StringBuilder refsb = new StringBuilder();
					HashSet<ProteinReference> refset = peptide
							.getProteinReferences();
					for (ProteinReference ref : refset) {
						SimpleProInfo info = accesser.getProInfo(ref.getName());
						refsb.append(info.getRef()).append(";");

						SeqLocAround sla = slamap.get(ref.toString());
						for (int k = 0; k < loc.length; k++) {
							loc[k] = sites[k].modifLocation();
							sitesb.append(sla.getBeg() + loc[k] - 1).append("/");
						}
						sitesb.deleteCharAt(sitesb.length() - 1);
						sitesb.append(";");
					}
					sitesb.deleteCharAt(sitesb.length() - 1);
					refsb.deleteCharAt(refsb.length() - 1);

					sb.append(refsb).append("\t");
					sb.append(sitesb).append("\t");

					for(int k=0;k<glycoRatio.length;k++){
						sb.append(glycoRatio[k]).append("\t");
					}
					
					for(int k=0;k<deglycoRatio.length;k++){
						sb.append(deglycoRatio[k]).append("\t");
					}
					
					double [] relativeRatios = new double[glycoRatio.length];
					for(int k=0;k<relativeRatios.length;k++){
						relativeRatios[k] = deglycoRatio[k]==0 ? 0 : glycoRatio[k]/deglycoRatio[k];
						sb.append(df4.format(relativeRatios[k])).append("\t");
					}
					
					this.writer.addContent(sb.toString(), 0, format);
					count++;
				}
			}
		}
		this.writer.close();
	*/}
	
	private static void tempCao(String in1, String in2) throws IOException, JXLException{
		
		HashSet<String> set1 = new HashSet<String>();
		Pattern ng = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		File [] files1 = (new File(in1)).listFiles();
		for(int i=0;i<files1.length;i++){
			if(files1[i].getName().endsWith("xls")){
				ExcelReader reader = new ExcelReader(files1[i], 1);
				String [] line = reader.readLine();
				while((line=reader.readLine())!=null && line.length>0){
					Matcher matcher = ng.matcher(line[0]);
					if(matcher.find()){
						set1.add(line[0].substring(2, line[0].length()-2));
					}
				}
				reader.close();
			}
		}

		HashSet<String> set2 = new HashSet<String>();
		HashSet<String> set4 = new HashSet<String>();
		File [] files2 = (new File(in2)).listFiles();
		for(int i=0;i<files2.length;i++){
			if(files2[i].getName().endsWith("xls")){
				ExcelReader reader = new ExcelReader(files2[i], 1);
				String [] line = reader.readLine();
				while((line=reader.readLine())!=null && line.length>0){
					set2.add(line[0].substring(2, line[0].length()-2));
					set4.add(line[0].substring(2, line[0].length()-2)+"\t"+line[3]);
				}
				reader.close();
			}
		}
		
		HashSet<String> set3 = new HashSet<String>();
		set3.addAll(set1);
		set3.addAll(set2);
		System.out.println(set1.size()+"\t"+set2.size()+"\t"+set3.size()+"\t"+set4.size());
	}

	private static void cao2(String in) throws IOException, JXLException{
		Pattern ng = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		HashSet<String> set = new HashSet<String>();
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			ExcelReader reader = new ExcelReader(files[i]);
			String [] line = reader.readLine();
			while((line=reader.readLine())!=null && line.length>5){
//				Matcher matcher = ng.matcher(line[5]);
//				if(!matcher.find()) continue;
				StringBuilder sb = new StringBuilder();
				String s = line[5].substring(2, line[5].length()-2);
				for(int j=0;j<s.length();j++){
					char a = s.charAt(j);
					if(a<'A' || a>'Z'){
						if(a=='*' || a=='#'){
							sb.append(a);
						}
					}else{
						sb.append(a);
					}
				}
				set.add(sb.toString());
			}
			reader.close();
		}
		System.out.println(set.size());
	}
	
	private static void caoQaun(String in) throws IOException, JXLException{
		Pattern ng = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		HashSet<String> set = new HashSet<String>();
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			ExcelReader reader = new ExcelReader(files[i], 1);
			String [] line = reader.readLine();
			while((line=reader.readLine())!=null && line.length>5){
				Matcher matcher = ng.matcher(line[0]);
				if(!matcher.find()) continue;
				String s = line[0].substring(2, line[0].length()-2);
				set.add(s);
			}
			reader.close();
		}
		System.out.println(set.size());
	}
	
	private static void caoPPL(String in) throws IOException, JXLException, FileDamageException, PeptideParsingException{
		Pattern ng = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		File [] files = (new File(in)).listFiles();
		int count = 0;
		for(int i=0;i<files.length;i++){
			IPeptideListReader reader =  new PeptideListReader(files[i]);
			IPeptide peptide = null;
			while((peptide=reader.getPeptide())!=null){
				String sequence = peptide.getSequence();
				Matcher matcher = ng.matcher(sequence);
				if(!matcher.find()) continue;
				count++;
				StringBuilder sb = new StringBuilder();
				String s = sequence.substring(2, sequence.length()-2);
				for(int j=0;j<s.length();j++){
					char a = s.charAt(j);
					if(a<'A' || a>'Z'){
						if(a=='*' || a=='#'){
							sb.append(a);
						}
					}else{
						sb.append(a);
					}
				}
				String ss = sb.toString();
				if(map.containsKey(ss)){
					map.put(ss, map.get(ss)+1);
				}else{
					map.put(ss, 1);
				}
			}
		}
		System.out.println(map.size()+"\t"+count);
		for(String ss:map.keySet()){
			System.out.println(ss+"\t"+map.get(ss));
		}
	}
	
	private static void caoQaun2(String in) throws IOException, JXLException{
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			ExcelReader reader = new ExcelReader(files[i], 1);
			String [] line = reader.readLine();
			while((line=reader.readLine())!=null && line.length>5){
				String s = line[0].substring(2, line[0].length()-2);
				set1.add(s);
				set2.add(s+"\t"+line[3]);
			}
			reader.close();
		}
		System.out.println(set1.size()+"\t"+set2.size());
	}
	
	private static void batchWrite(String deglyco, String glyco) throws Exception {

		HashMap<String, String> deglycomap = new HashMap<String, String>();
		File[] deglycofiles = (new File(deglyco)).listFiles();
		for (int i = 0; i < deglycofiles.length; i++) {
			String name = deglycofiles[i].getName();
			if (name.endsWith("pxml")) {
				String key = name.replace("CID", "HCD_N-glycan");
				key = key.replace("20130805", "20130723");
//				key = key.substring(0, key.length() - 7);
				deglycomap.put(key, deglycofiles[i].getAbsolutePath());
				System.out.println(key);
			}
		}
		File[] glycofiles = (new File(glyco)).listFiles();
		for (int i = 0; i < glycofiles.length; i++) {

			String glycopath = glycofiles[i].getAbsolutePath();
			String name = glycofiles[i].getName();
//			name = name.substring(0, name.length() - 6);
			System.out.println(name);

			if (deglycomap.containsKey(name)) {
				String [] rationame = new String[]{"2/1"};
				String out = glycopath.replace("pxml", "relative.xls");
				GlycoRelativeQuanWriter writer = new GlycoRelativeQuanWriter(deglycomap.get(name), glycopath, out, rationame, new double[]{1.0});
				writer.write();
			}
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String deglyco = "J:\\serum glycan quantification\\2nd\\serum\\deglyco\\20130805_serum_di-labeling_Normal_HCC_CID_quantification.pxml";
		String glyco = "J:\\serum glycan quantification\\2nd\\serum\\glyco\\20130723_serum_di-labeling_Normal_HCC_HCD_N-glycan_quantification.pxml";
		String out = "J:\\serum glycan quantification\\2nd\\serum\\glyco\\20130723_serum_di-labeling_Normal_HCC_HCD_N-glycan_quantification.relative.xls";
		String [] names = new String[]{"2/1"};
		
//		GlycoRelativeQuanWriter writer = new GlycoRelativeQuanWriter(deglyco, glyco, out, names, new double[]{1.0});
//		writer.write();
		
//		GlycoRelativeQuanWriter.tempCao("H:\\NGLYCO_QUAN\\NGlycan_quan_20131111\\CID_iden\\percolator_rt\\quan_control", 
//				"H:\\NGLYCO_QUAN\\NGlycan_quan_20131111\\HCD\\quan_control");
//		GlycoRelativeQuanWriter.caoPPL("H:\\NGLYCO_QUAN\\NGlycan_quan_20131111\\CID_iden\\percolator_rt\\temp");
//		GlycoRelativeQuanWriter.caoQaun("H:\\NGLYCO_QUAN\\NGlycan_quan_20131111\\CID_iden\\percolator_rt\\quan");
		GlycoRelativeQuanWriter.gradWrite("H:\\NGLYCO_QUAN\\NGlycan_Quan_20131111\\CID_iden\\2D", 
				"H:\\NGLYCO_QUAN\\NGlycan_Quan_20131111\\HCD\\2D", 
				"H:\\NGLYCO_QUAN\\NGlycan_Quan_20131111\\HCD\\2D\\2D.relative.xls", 
				names, new double[]{1.0});
		
//		GlycoRelativeQuanWriter.batchWrite("J:\\serum glycan quantification\\2nd\\serum\\deglyco", 
//				"J:\\serum glycan quantification\\2nd\\serum\\glyco");
	}

}
