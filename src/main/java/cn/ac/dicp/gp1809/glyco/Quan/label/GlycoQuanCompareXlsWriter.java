/* 
 ******************************************************************************
 * File: GlycoQuanCompareXlsWriter.java * * * Created on 2014-2-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 * 
 * @version 2014-2-24, 9:00:41
 */
public class GlycoQuanCompareXlsWriter {

	private GlycoLabelFeaturesXMLReader[] readers;
	private ExcelWriter writer;
	private ExcelFormat format;

	public GlycoQuanCompareXlsWriter(String[] files, String out)
			throws DocumentException, IOException, RowsExceededException, WriteException {

		this.readers = new GlycoLabelFeaturesXMLReader[files.length];
		String[] names = new String[files.length];
		for (int i = 0; i < readers.length; i++) {
			File file = new File(files[i]);
			readers[i] = new GlycoLabelFeaturesXMLReader(file);
			names[i] = file.getName().substring(0,
					file.getName().length() - 5);
		}

		this.writer = new ExcelWriter(out, new String[] { "Glycan Structure",
				"Glycopeptides", "Quantified glycopeptides" });
		this.format = ExcelFormat.normalFormat;
		this.addTitle(names);
	}

	public GlycoQuanCompareXlsWriter(File[] files, String out)
			throws DocumentException, IOException, RowsExceededException,
			WriteException {

		this.readers = new GlycoLabelFeaturesXMLReader[files.length];
		String[] names = new String[files.length];
		for (int i = 0; i < readers.length; i++) {
			readers[i] = new GlycoLabelFeaturesXMLReader(files[i]);
			names[i] = files[i].getName().substring(0,
					files[i].getName().length() - 5);
		}

		this.writer = new ExcelWriter(out, new String[] { "Glycan Structure",
				"Glycopeptides", "Quantified glycopeptides" });
		this.format = ExcelFormat.normalFormat;
		this.addTitle(names);
	}

	private void addTitle(String[] names) throws RowsExceededException,
			WriteException {
		StringBuilder sb1 = new StringBuilder();
		sb1.append("IUPAC Name\t");
		sb1.append("Theor glycan mw\t");
		for (int i = 0; i < names.length; i++) {
			sb1.append("Spectra Count_" + names[i]).append("\t");
		}
		sb1.append("In N experiments");
		this.writer.addTitle(sb1.toString(), 0, format);
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("IUPAC Name\t");
		sb2.append("Theor glycan mw\t");
		sb2.append("Sequence\t");
		sb2.append("Theor peptide mw\t");
		sb2.append("Reference\t");
		sb2.append("Site\t");
		for (int i = 0; i < names.length; i++) {
			sb2.append("Spectra Count_" + names[i]).append("\t");
		}
		sb2.append("In N experiments");
		this.writer.addTitle(sb2.toString(), 1, format);
		
		StringBuilder sb3 = new StringBuilder();
		sb3.append("IUPAC Name\t");
		sb3.append("Theor glycan mw\t");
		sb3.append("Sequence\t");
		sb3.append("Theor peptide mw\t");
		sb3.append("Reference\t");
		sb3.append("Site\t");
		for (int i = 0; i < names.length; i++) {
			sb3.append("Ratio_" + names[i]).append("\t");
		}
		sb3.append("In N experiments\t");
		sb3.append("Average\t");
		sb3.append("RSD");
		this.writer.addTitle(sb3.toString(), 2, format);
	}

	public void write() throws RowsExceededException, WriteException {

		HashMap<String, String> contentMap1 = new HashMap<String, String>();
		HashMap<String, String> contentMap2 = new HashMap<String, String>();
		HashMap<String, String> contentMap3 = new HashMap<String, String>();

		HashMap<String, Integer>[] allGlycoCountMaps = new HashMap[readers.length];
		HashMap<String, Integer>[] matchedGlycoCountMaps = new HashMap[readers.length];
		HashMap<String, double[]>[] ratioMaps = new HashMap[readers.length];
		for (int i = 0; i < allGlycoCountMaps.length; i++) {
			allGlycoCountMaps[i] = new HashMap<String, Integer>();
			matchedGlycoCountMaps[i] = new HashMap<String, Integer>();
			ratioMaps[i] = new HashMap<String, double[]>();
		}

		for (int i = 0; i < readers.length; i++) {

			NGlycoSSM[] matched = readers[i].getMatchedGlycoSpectra();
			NGlycoSSM[] unmatched = readers[i].getUnmatchedGlycoSpectra();
			IGlycoPeptide[] peptides = readers[i].getAllGlycoPeptides();
			GlycoPeptideLabelPair[] pairs = readers[i].getAllSelectedPairs();
			ProteinNameAccesser accesser = readers[i].getProNameAccesser();

			for (int j = 0; j < unmatched.length; j++) {
				int[] ids = unmatched[j].getGlycanid();
				String idkey = ids[0] + "_" + ids[1];
				String name = unmatched[j].getName();
				double mw = unmatched[j].getGlycoMass();
				StringBuilder sb = new StringBuilder();
				sb.append(name).append("\t");
				sb.append(mw).append("\t");
				contentMap1.put(idkey, sb.toString());
				if (allGlycoCountMaps[i].containsKey(idkey)) {
					allGlycoCountMaps[i].put(idkey,
							allGlycoCountMaps[i].get(idkey) + 1);
				} else {
					allGlycoCountMaps[i].put(idkey, 1);
				}
			}

			HashSet<String> quantifiedSet = new HashSet<String>();
			for(int j=0;j<pairs.length;j++){

				int ssmid = pairs[j].getDeleSSMId();
				int[] ids = matched[ssmid].getGlycanid();
				int pepid = pairs[j].getPeptideId();
				
				String name = matched[ssmid].getName();
				double mw = matched[ssmid].getGlycoMass();
				StringBuilder sb = new StringBuilder();
				sb.append(name).append("\t");
				sb.append(mw).append("\t");

				String sequence = peptides[pepid].getSequence();
				double pepmw = matched[ssmid].getPepMass();
				GlycoSite[] sites = peptides[pepid].getAllGlycoSites();
				int[] loc = new int[sites.length];
				for (int k = 0; k < loc.length; k++) {
					loc[k] = sites[k].modifLocation();
				}
				HashMap<String, SeqLocAround> slamap = peptides[pepid]
						.getPepLocAroundMap();

				StringBuilder sitesb = new StringBuilder();
				StringBuilder refsb = new StringBuilder();
				HashSet<ProteinReference> refset = peptides[pepid]
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

				String idkey = ids[0] + "_" + ids[1] + "_" + sequence;
				sb.append(sequence).append("\t");
				sb.append(pepmw).append("\t");
				sb.append(refsb).append("\t");
				sb.append(sitesb).append("\t");
				contentMap3.put(idkey, sb.toString());

				double[] ratios = pairs[j].getSelectRatios();
				ratioMaps[i].put(idkey, ratios);
				
				quantifiedSet.add(idkey);
			}
			
			for (int j = 0; j < matched.length; j++) {
				
				int[] ids = matched[j].getGlycanid();
				int pepid = matched[j].getPeptideid();
				String idkey1 = ids[0] + "_" + ids[1];

				String name = matched[j].getName();
				double mw = matched[j].getGlycoMass();
				StringBuilder sb = new StringBuilder();
				sb.append(name).append("\t");
				sb.append(mw).append("\t");
				contentMap1.put(idkey1, sb.toString());
				if (allGlycoCountMaps[i].containsKey(idkey1)) {
					allGlycoCountMaps[i].put(idkey1,
							allGlycoCountMaps[i].get(idkey1) + 1);
				} else {
					allGlycoCountMaps[i].put(idkey1, 1);
				}

				String sequence = peptides[pepid].getSequence();
				double pepmw = matched[j].getPepMass();
				GlycoSite[] sites = peptides[pepid].getAllGlycoSites();
				int[] loc = new int[sites.length];
				for (int k = 0; k < loc.length; k++) {
					loc[k] = sites[k].modifLocation();
				}
				HashMap<String, SeqLocAround> slamap = peptides[pepid]
						.getPepLocAroundMap();

				StringBuilder sitesb = new StringBuilder();
				StringBuilder refsb = new StringBuilder();
				HashSet<ProteinReference> refset = peptides[pepid]
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

				String idkey2 = ids[0] + "_" + ids[1] + "_" + sequence;
				sb.append(sequence).append("\t");
				sb.append(pepmw).append("\t");
				sb.append(refsb).append("\t");
				sb.append(sitesb).append("\t");
				contentMap2.put(idkey2, sb.toString());

				if (matchedGlycoCountMaps[i].containsKey(idkey2)) {
					matchedGlycoCountMaps[i].put(idkey2,
							matchedGlycoCountMaps[i].get(idkey2) + 1);
				} else {
					matchedGlycoCountMaps[i].put(idkey2, 1);
				}
				
				if(!quantifiedSet.contains(idkey2)){
					
				}
			}
		}

		Iterator<String> it1 = contentMap1.keySet().iterator();
		while (it1.hasNext()) {
			String key = it1.next();
			StringBuilder sb = new StringBuilder();
			sb.append(contentMap1.get(key));
			int count = 0;
			for (int i = 0; i < allGlycoCountMaps.length; i++) {
				if (allGlycoCountMaps[i].containsKey(key)) {
					sb.append(allGlycoCountMaps[i].get(key)).append("\t");
					count++;
				} else {
					sb.append("0").append("\t");
				}
			}
			sb.append(count);
			writer.addContent(sb.toString(), 0, format);
		}

		Iterator<String> it2 = contentMap2.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			StringBuilder sb = new StringBuilder();
			sb.append(contentMap2.get(key));
			int count = 0;
			for (int i = 0; i < matchedGlycoCountMaps.length; i++) {
				if (matchedGlycoCountMaps[i].containsKey(key)) {
					sb.append(matchedGlycoCountMaps[i].get(key)).append("\t");
					count++;
				} else {
					sb.append("0").append("\t");
				}
			}
			sb.append(count);
			writer.addContent(sb.toString(), 1, format);
		}
		
		Iterator<String> it3 = contentMap3.keySet().iterator();
		while (it3.hasNext()) {
			String key = it3.next();
			StringBuilder sb = new StringBuilder();
			sb.append(contentMap3.get(key));
			int count = 0;
			ArrayList<Double> [] rsdlist = null;
			for (int i = 0; i < ratioMaps.length; i++) {
				if (ratioMaps[i].containsKey(key)) {
					count++;
					double[] ratio = ratioMaps[i].get(key);
					if(rsdlist==null){
						rsdlist = new ArrayList[ratio.length];
						for(int j=0;j<rsdlist.length;j++){
							rsdlist[j] = new ArrayList<Double>();
							if(ratio[j]>0)
								rsdlist[j].add(ratio[j]);
						}
					}else{
						for(int j=0;j<rsdlist.length;j++){
							if(ratio[j]>0)
								rsdlist[j].add(ratio[j]);
						}
					}
					for(int j=0;j<ratio.length;j++){
						sb.append(ratio[j]).append("\t");
					}
				} else {
					sb.append("\t");
				}
			}
			
			sb.append(count).append("\t");
			for(int i=0;i<rsdlist.length;i++){
				double ave = MathTool.getAveInDouble(rsdlist[i]);
				double rsd = MathTool.getRSDInDouble(rsdlist[i]);
				sb.append(ave).append("\t");
				sb.append(rsd).append("\t");
			}
			writer.addContent(sb.toString(), 2, format);
		}
	}

	public void close() throws WriteException, IOException {
		this.writer.close();
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws DocumentException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws DocumentException, IOException, RowsExceededException, WriteException {
		// TODO Auto-generated method stub

		String s1 = "I:\\human liver glycan quantification\\20140227\\20140213_humanliver_with-glycan_HCC_normal_1.pxml";
		String s2 = "I:\\human liver glycan quantification\\20140227\\20140213_humanliver_with-glycan_HCC_normal_2.pxml";
		String s3 = "I:\\human liver glycan quantification\\20140227\\20140213_humanliver_with-glycan_HCC_normal_3.pxml";
		String[] files = new String[]{s1, s2, s3};
		String out = "I:\\human liver glycan quantification\\20140227\\20140213_humanliver_with-glycan_HCC_normal.xls";
		
		GlycoQuanCompareXlsWriter writer = new GlycoQuanCompareXlsWriter(files, out);
		writer.write();
		writer.close();
	}

}
