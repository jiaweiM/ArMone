/* 
 ******************************************************************************
 * File: AbstractFeaLabelGetter2.java * * * Created on 2012-4-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.QuanFeatureGetter;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeature;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1ScanList;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import flanagan.analysis.Regression;

/**
 * @author ck
 * 
 * @version 2012-4-20, 09:17:10
 */
public class NGlyStrucLabelGetter {

	protected LabelType labeltype;
	protected int labelNum;
	protected AminoacidModification aamodif;
	protected GlycoJudgeParameter jpara;
	protected int topnStructure;

	// protected MS1PixelGetter pixGetter;
	protected MS1ScanList scanlist;
	protected QuanFeatureGetter getter;
	protected NGlycoSpecFeaturesGetter specGetter;
	protected ArrayList<Double> pepRt;
	protected ArrayList<Double> glycanRt;

	// key = ModSite and value = the symbol of the ModSite
	protected HashMap<ModSite, Character> symbolMap;

	// protected HashMap <ModSite, Double> [] modMassMap;
	// protected HashMap <ModSite, Boolean> [] modBooMap;
	protected HashMap<String, IGlycoPeptide> pepMap;
	// protected HashMap <String, ArrayList <Integer>> seqScanMap;

	/**
	 * the variable modification symbol which represent glycosylation, used in
	 * O-linked glycans
	 */
	protected HashSet<Character> glycoModSet;

	protected NGlycoSSM[] ssms;
	protected IGlycoPeptide[] peps;
	protected GlycoPeptideLabelPair[] feas;
	private double [] bestEstimate;
	
	protected double mzThresPPM, mzThresAMU;
//	protected double Asp_Asn = 0.984016;
	protected DecimalFormat df5 = DecimalFormats.DF0_5;

	public final static double [] labels = new double []{56.0626,  64.112814, 72.15134};
	
	public final static double [] labels2 = new double []{84.0939, 96.169221, 108.22701};
	
	public final static double [] labels3 = new double []{112.1252, 128.225628, 144.30268};
	
	public NGlyStrucLabelGetter(String peakfile, LabelType type, AminoacidModification aamodif) throws IOException,
			XMLStreamException {
		
		this(peakfile, GlycoJudgeParameter.defaultParameter(), type,
				aamodif);
	}

	/**
	 * @param peakfile
	 * @param defaultParameter
	 * @param type
	 * @param glycanType
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public NGlyStrucLabelGetter(String peakfile,
			GlycoJudgeParameter jpara, LabelType labeltype,
			AminoacidModification aamodif) throws IOException,
			XMLStreamException {

		this.specGetter = new NGlycoSpecFeaturesGetter(peakfile);
		this.scanlist = specGetter.getScanList();
		this.jpara = jpara;
		this.labeltype = labeltype;
		this.mzThresPPM = jpara.getMzThresPPM();
		this.mzThresAMU = jpara.getMzThresAMU();
		this.topnStructure = jpara.getTopnStructure();
		this.pepMap = new HashMap<String, IGlycoPeptide>();
		this.pepRt = new ArrayList<Double>();
		this.glycanRt = new ArrayList<Double>();
		this.ssms = specGetter.getGlycoSSMs();
		this.initial();
	}

	public NGlyStrucLabelGetter(String peakfile, LabelType type,
			int glycanType, HashSet<Character> glycoModSet,
			AminoacidModification aamodif) throws IOException,
			XMLStreamException {

		this(peakfile, GlycoJudgeParameter.defaultParameter(), type,
				glycoModSet, aamodif);
	}

	/**
	 * @param peakfile
	 * @param defaultParameter
	 * @param type
	 * @param glycanType2
	 * @param glycoModSet2
	 */
	public NGlyStrucLabelGetter(String peakfile,
			GlycoJudgeParameter jpara, LabelType labeltype,
			HashSet<Character> glycoModSet, AminoacidModification aamodif)
			throws IOException, XMLStreamException {

//		this.getter = new QuanFeatureGetter(peakfile);
		this.specGetter = new NGlycoSpecFeaturesGetter(peakfile);
		this.scanlist = specGetter.getScanList();
		this.jpara = jpara;
		this.labeltype = labeltype;
		this.mzThresPPM = jpara.getMzThresPPM();
		this.mzThresAMU = jpara.getMzThresAMU();
		this.glycoModSet = glycoModSet;
		this.pepMap = new HashMap<String, IGlycoPeptide>();
		this.pepRt = new ArrayList<Double>();
		this.glycanRt = new ArrayList<Double>();
		this.ssms = specGetter.getGlycoSSMs();
		this.initial();
	}

	private void initial() throws IOException,
			XMLStreamException {

		this.symbolMap = new HashMap<ModSite, Character>();
		LabelInfo[][] infos = labeltype.getInfo();
		this.labelNum = infos.length;
		for (int i = 0; i < infos.length; i++) {
			for (int j = 0; j < infos[i].length; j++) {
				ModSite site = infos[i][j].getSite();
				char symbol = infos[i][j].getSymbol();
				if (symbol == '\u0000')
					continue;

				if (!symbolMap.containsKey(site)) {
					symbolMap.put(site, symbol);
				}
			}
		}
	}

	public GlycoJudgeParameter getParameter() {
		return jpara;
	}

	/**
	 * Peptide without isotope label.
	 * 
	 * @param peptide
	 */
	public void addPeptide(IGlycoPeptide peptide,
			AminoacidModification aam){
		// TODO Auto-generated method stub

		Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		HashSet<Integer> set = new HashSet<Integer>();
		String sequence = peptide.getSequence();
		Matcher matcher = N_GLYCO.matcher(sequence);
		while (matcher.find()) {
			set.add(matcher.start());
		}

		double pepMr = peptide.getMH() - AminoAcidProperty.PROTON_W;
		int glycocount = 0;
		StringBuilder sb = new StringBuilder();
		ArrayList<ModSite> siteList = new ArrayList<ModSite>();
		int loc = 0;
		int n = -1;
		int sixModType = -1;
		
		boolean nTerm = true;
		boolean proNTerm = false;
		ModSite site = null;
		char[] pepChars = peptide.getSequence().toCharArray();

		sb.append(pepChars[0]);
//		if (pepChars[0] == '-')
//			proNTerm = true;
		
		if (labeltype == LabelType.SixLabel) {
			
			if(peptide.getSequence().contains("X"))
				return;

			int countK = 0;
			int modK = 0;
			double totalAddMass = 0;
			
			for (int i = 1; i < pepChars.length - 1; i++) {

				if ((pepChars[i] >= 'A' && pepChars[i] <= 'Z')) {

					sb.append(pepChars[i]);
					site = ModSite.newInstance_aa(pepChars[i]);
					loc++;
					if(pepChars[i]=='K')
						countK++;

				} else if (pepChars[i] == '.') {
					
					sb.append(pepChars[i]);
					if (nTerm) {
						if (proNTerm)
							site = ModSite.newInstance_ProNterm();
						else
							site = ModSite.newInstance_PepNterm();

						nTerm = false;
						
					} else {
						site = ModSite.newInstance_PepCterm();
					}
					
				} else if (pepChars[i] == '-') {

					sb.append(pepChars[i]);

				} else {

					char sym = pepChars[i];
					double modmass = aam.getAddedMassForModif(sym);

					if (site.getModifAt().equals("N") && set.contains(i-1)) {

						if(glycocount==0)
							pepMr -= aam.getAddedMassForModif(sym);
						GlycoSite glycoSite = new GlycoSite(site, loc, sym);
						glycoSite.setModMass(modmass);
						peptide.addGlycoSite(glycoSite);
						glycocount++;
						
					}else if(site.getModifAt().equals("K")){
						modK++;
						totalAddMass += modmass;
						
					}else if(site.getModifAt().equals("_n")){
						totalAddMass += modmass;
					}
				}
			}
			sb.append(pepChars[pepChars.length - 1]);

			if(countK==0 || countK>3 || countK!=modK){
				return;
			}
			
			if (n == -1 && siteList.size() > 0)
				n = 0;

			if (n == -1) {
				return;
			}
			
			
			for(int i=0;i<labels.length;i++){
				if(Math.abs(labels[i]-totalAddMass)<0.05){
					sixModType = i+1;
					break;
				}
			}
			
			if(sixModType==-1){
				for(int i=0;i<labels2.length;i++){
					if(Math.abs(labels2[i]-totalAddMass)<0.05){
						sixModType = i+1;
						break;
					}
				}
			}
			
			if(sixModType==-1){
				for(int i=0;i<labels3.length;i++){
					if(Math.abs(labels3[i]-totalAddMass)<0.05){
						sixModType = i+1;
						break;
					}
				}
			}
			
			pepMr -= totalAddMass;
			if(sixModType==-1){
				return;
			}

		} else {

			for (int i = 1; i < pepChars.length - 1; i++) {

				if ((pepChars[i] >= 'A' && pepChars[i] <= 'Z')) {

					sb.append(pepChars[i]);
					site = ModSite.newInstance_aa(pepChars[i]);
					if (symbolMap.containsKey(site)) {
						siteList.add(site);
					}
					loc++;

				} else if (pepChars[i] == '.') {
					sb.append(pepChars[i]);
					if (nTerm) {
						if (proNTerm)
							site = ModSite.newInstance_ProNterm();
						else
							site = ModSite.newInstance_PepNterm();

						nTerm = false;
						if (symbolMap.containsKey(site)) {
							siteList.add(site);
						}
					} else {
						site = ModSite.newInstance_PepCterm();
						if (symbolMap.containsKey(site)) {
							siteList.add(site);
						}
					}
				} else if (pepChars[i] == '-') {

					sb.append(pepChars[i]);

				} else {

					char sym = pepChars[i];
					double modmass = aam.getAddedMassForModif(sym);
					sb.append(sym);
					
					if (site.getModifAt().equals("N") && set.contains(i-1)) {
						if(glycocount==0)
							pepMr -= aam.getAddedMassForModif(sym);
						GlycoSite glycoSite = new GlycoSite(site, loc, sym);
						glycoSite.setModMass(modmass);
						peptide.addGlycoSite(glycoSite);
						glycocount++;
					}
				}
			}
			sb.append(pepChars[pepChars.length - 1]);

			if (siteList.size() == 0)
				return;
		}
		
		if(glycocount==0)
			return;

		peptide.setPepMrNoGlyco(pepMr);
		String key = sb.toString();
		peptide.setSequence(key);

		if (pepMap.containsKey(key)) {

			IGlycoPeptide p1 = pepMap.get(key);
			double score0 = peptide.getPrimaryScore();
			double score1 = p1.getPrimaryScore();
			if (score1 < score0){
				peptide.setLabelMasses(p1.getLabelMasses());
				pepMap.put(key, peptide);
			}

		} else {

			pepMap.put(key, peptide);

			if(labeltype == LabelType.SixLabel){
				
				if(sixModType==1){
					peptide.setLabelMasses(labels);
					
				}else if(sixModType==2){
					peptide.setLabelMasses(labels2);
					
				}else if(sixModType==3){
					peptide.setLabelMasses(labels3);
				}
				
			}else{
				
				double[] masses = new double[labelNum];
				HashMap<ModSite, float[]> siteMassMap = labeltype.getMassMap();

				for (int i = 0; i < masses.length; i++) {
					for (int j = 0; j < siteList.size(); j++) {
						masses[i] += siteMassMap.get(siteList.get(j))[i];
					}
				}
				
				peptide.setLabelMasses(masses);
			}
		}
	}

	/**
	 * Peptide with isotope label.
	 * 
	 * @param peptide
	 * @param aam
	 */
	public void addPeptide2(IGlycoPeptide peptide,
			AminoacidModification aam){
		// TODO Auto-generated method stub

		Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		HashSet<Integer> set = new HashSet<Integer>();
		String sequence = peptide.getSequence();
		Matcher matcher = N_GLYCO.matcher(sequence);
		while (matcher.find()) {
			set.add(matcher.start());
		}

		double pepMr = peptide.getMH() - AminoAcidProperty.PROTON_W;
		int glycocount = 0;
		StringBuilder sb = new StringBuilder();
		ArrayList<ModSite> siteList = new ArrayList<ModSite>();
		int loc = 0;
		int n = -1;
		int sixModType = -1;
		
		boolean nTerm = true;
		boolean proNTerm = false;
		ModSite site = null;
		
		char[] pepChars = peptide.getSequence().toCharArray();

		sb.append(pepChars[0]);
		if (pepChars[0] == '-')
			proNTerm = true;
		
		if (labeltype == LabelType.SixLabel) {
			
			if(peptide.getSequence().contains("X"))
				return;

			int countK = 0;
			int modK = 0;
			double totalAddMass = 0;
			
			for (int i = 1; i < pepChars.length - 1; i++) {

				if ((pepChars[i] >= 'A' && pepChars[i] <= 'Z')) {

					sb.append(pepChars[i]);
					site = ModSite.newInstance_aa(pepChars[i]);
					loc++;
					if(pepChars[i]=='K')
						countK++;

				} else if (pepChars[i] == '.') {
					
					sb.append(pepChars[i]);
					if (nTerm) {
						if (proNTerm)
							site = ModSite.newInstance_ProNterm();
						else
							site = ModSite.newInstance_PepNterm();

						nTerm = false;
						
					} else {
						site = ModSite.newInstance_PepCterm();
					}
					
				} else if (pepChars[i] == '-') {

					sb.append(pepChars[i]);

				} else {

					char sym = pepChars[i];
					double modmass = aam.getAddedMassForModif(sym);

					if (site.getModifAt().equals("N") && set.contains(i-1)) {
						
						if(glycocount==0)
							pepMr -= aam.getAddedMassForModif(sym);

						GlycoSite glycoSite = new GlycoSite(site, loc, sym);
						glycoSite.setModMass(modmass);
						peptide.addGlycoSite(glycoSite);
						glycocount++;
						
					}else if(site.getModifAt().equals("K")){
						modK++;
						totalAddMass += modmass;
						
					}else if(site.getModifAt().equals("_n")){
						totalAddMass += modmass;
					}
				}
			}
			sb.append(pepChars[pepChars.length - 1]);

			if(countK==0 || countK>3 || countK!=modK){
				return;
			}
			
			if (n == -1 && siteList.size() > 0)
				n = 0;

			if (n == -1) {
				return;
			}
			
			
			for(int i=0;i<labels.length;i++){
				if(Math.abs(labels[i]-totalAddMass)<0.05){
					sixModType = i+1;
					break;
				}
			}
			
			if(sixModType==-1){
				for(int i=0;i<labels2.length;i++){
					if(Math.abs(labels2[i]-totalAddMass)<0.05){
						sixModType = i+1;
						break;
					}
				}
			}
			
			if(sixModType==-1){
				for(int i=0;i<labels3.length;i++){
					if(Math.abs(labels3[i]-totalAddMass)<0.05){
						sixModType = i+1;
						break;
					}
				}
			}
			
			pepMr -= totalAddMass;
			if(sixModType==-1){
				return;
			}

		} else {

			for (int i = 1; i < pepChars.length - 1; i++) {

				if ((pepChars[i] >= 'A' && pepChars[i] <= 'Z')) {

					sb.append(pepChars[i]);
					site = ModSite.newInstance_aa(pepChars[i]);
					if (symbolMap.containsKey(site)) {
						siteList.add(site);
					}
					loc++;

				} else if (pepChars[i] == '.') {
					sb.append(pepChars[i]);
					if (nTerm) {
						if (proNTerm)
							site = ModSite.newInstance_ProNterm();
						else
							site = ModSite.newInstance_PepNterm();

						nTerm = false;
						if (symbolMap.containsKey(site)) {
							siteList.add(site);
						}
					} else {
						site = ModSite.newInstance_PepCterm();
						if (symbolMap.containsKey(site)) {
							siteList.add(site);
						}
					}
				} else if (pepChars[i] == '-') {

					sb.append(pepChars[i]);

				} else {

					char sym = pepChars[i];
					double modmass = aam.getAddedMassForModif(sym);

					if (site.getModifAt().equals("N") && set.contains(i-1)) {

						if(glycocount==0)
							pepMr -= aam.getAddedMassForModif(sym);

						GlycoSite glycoSite = new GlycoSite(site, loc, sym);
						glycoSite.setModMass(modmass);
						peptide.addGlycoSite(glycoSite);
						glycocount++;
					}

					int iso = labeltype.getIsoIndex(site, modmass);

					if (iso == -1) {
						sb.append(pepChars[i]);
						
					} else {
						
						pepMr -= aam.getAddedMassForModif(sym);
						if (n == -1) {
							n = iso;
						} else {
							if (n != iso && iso != -1) {
								return;
							}
						}
					}
				}
			}
			sb.append(pepChars[pepChars.length - 1]);

			if (n == -1 && siteList.size() > 0)
				n = 0;

			if (n == -1) {
				return;
			}
		}
		
		if(glycocount==0)
			return;

		peptide.setPepMrNoGlyco(pepMr);
		String key = sb.toString();
		peptide.setSequence(key);

		if (pepMap.containsKey(key)) {

			IGlycoPeptide p1 = pepMap.get(key);
			double score0 = peptide.getPrimaryScore();
			double score1 = p1.getPrimaryScore();
			if (score1 < score0){
				peptide.setLabelMasses(p1.getLabelMasses());
				pepMap.put(key, peptide);
			}

		} else {

			pepMap.put(key, peptide);

			if(labeltype == LabelType.SixLabel){
				
				if(sixModType==1){
					peptide.setLabelMasses(labels);
					
				}else if(sixModType==2){
					peptide.setLabelMasses(labels2);
					
				}else if(sixModType==3){
					peptide.setLabelMasses(labels3);
				}
				
			}else{
				
				double[] masses = new double[labelNum];
				HashMap<ModSite, float[]> siteMassMap = labeltype.getMassMap();

				for (int i = 0; i < masses.length; i++) {
					for (int j = 0; j < siteList.size(); j++) {
						masses[i] += siteMassMap.get(siteList.get(j))[i];
					}
				}
				
				peptide.setLabelMasses(masses);
			}
		}
	}

	/**
	 * 
	 */
	public void match(){


		double ppm = mzThresPPM / 2.0;

		IGlycoPeptide [] peps = this.pepMap.values().toArray(new IGlycoPeptide[pepMap.size()]);
		Arrays.sort(peps, new Comparator <IGlycoPeptide>(){

			@Override
			public int compare(IGlycoPeptide arg0, IGlycoPeptide arg1) {
				// TODO Auto-generated method stub
				if(arg0.getPepMrNoGlyco()>arg1.getPepMrNoGlyco()){
					return 1;
				}else if(arg0.getPepMrNoGlyco()<arg1.getPepMrNoGlyco()){
					return -1;
				}
				return 0;
			}
			
		});
		
		this.peps = peps;

		HashMap<String, Integer> labelTypeMap = new HashMap<String, Integer>();
		
		for (int pepid=0;pepid<peps.length;pepid++) {

			IGlycoPeptide peptide = peps[pepid];
			double pepMr = peptide.getPepMrNoGlyco();
			double[] masses = getMatchMasses(pepMr, peptide.getLabelMasses());

			for (int i = 0; i < ssms.length; i++) {

				double pepmass = ssms[i].getPepMassExperiment();
				
				for(int j=0;j<masses.length;j++){
					
					double deltaMz = pepmass-masses[j];
					double deltaPPM = ppm*pepmass*1E-6;
					if(Math.abs(deltaMz)<deltaPPM){
						
						ssms[i].addMatchPepID(pepid, j);
						
						if(peps[j].getRetentionTime()>0)
							this.pepRt.add(peps[j].getRetentionTime());
						
						this.glycanRt.add(ssms[i].getRT());
						
						labelTypeMap.put(pepid+"_"+i, j);
					}
				}
			}
		}

		if (this.glycanRt.size() == this.pepRt.size()) {

			for (int i = 0; i < ssms.length; i++) {
				
				NGlycoSSM ssm = ssms[i];
				ArrayList<Integer> matchPepIDs = ssm.getMatchPepIDs();
				int bestId = -1;
				double max = 0;
				for (int i1 = 0; i1 < matchPepIDs.size(); i1++) {
					double score = peps[matchPepIDs.get(i1)]
							.getPrimaryScore();
					double peprt = peps[matchPepIDs.get(i1)]
							.getRetentionTime();

					if (score / Math.abs(peprt - ssm.getRT()) > max) {
						max = score / Math.abs(peprt - ssm.getRT());
						bestId = matchPepIDs.get(i1);
					}
				}
				ssm.setPeptideid(bestId);
				if(bestId>=0) {
					peps[bestId].addHcdPsmInfo(ssm);
					peps[bestId].setLabelTypeID(ssm.getPepLabelType(bestId));
				}
			}
		} else {
			for (int i = 0; i < ssms.length; i++) {
				NGlycoSSM ssm = ssms[i];
				ArrayList<Integer> matchPepScannums = ssm.getMatchPepIDs();
				int bestId = -1;
				double max = 0;
				for (int i1 = 0; i1 < matchPepScannums.size(); i1++) {
					double score = peps[matchPepScannums.get(i1)]
							.getPrimaryScore();
					// double peprt =
					// peps[matchPepScannums.get(i1)].getRetentionTime();
					if (score > max) {
						max = score;
						bestId = matchPepScannums.get(i1);
					}
				}
				ssm.setPeptideid(bestId);
				if(bestId>=0){
					peps[bestId].addHcdPsmInfo(ssm);
					peps[bestId].setLabelTypeID(ssm.getPepLabelType(bestId));
				}
			}
		}
		
		HashMap<String, Integer> gpmap = new HashMap<String, Integer>();
		HashMap<String, ArrayList<Integer>> matchedssmmap = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<Integer>> allssmmap = new HashMap<String, ArrayList<Integer>>();

		ArrayList<Double> glycanRt2 = new ArrayList<Double>();
		ArrayList<Double> pepRt2 = new ArrayList<Double>();
		
		int matchedId = 0;
		for(int i=0;i<ssms.length;i++){
			
			int pepid = ssms[i].getPeptideid();
			if(pepid==-1) continue;
			
			glycanRt2.add(ssms[i].getRT());
			pepRt2.add(peps[pepid].getRetentionTime());
			
			int [] glycanid = ssms[i].getGlycanid();
			int charge = ssms[i].getPreCharge();
			int labelType = labelTypeMap.get(pepid+"_"+i);

			double pepMr = peps[pepid].getPepMrNoGlyco();
			double pepmass = ssms[i].getPepMassExperiment();
			double deltaMz = pepmass-pepMr-peps[pepid].getLabelMasses()[labelType];
			ssms[i].setDeltaMz(Math.abs(deltaMz));
			
			String key = pepid+"_"+glycanid[0]+"_"+glycanid[1]+"_"+charge;
			if(gpmap.containsKey(key)){
				matchedssmmap.get(key).add(matchedId++);
				allssmmap.get(key).add(i);
			}else{
				gpmap.put(key, pepid);
				ArrayList<Integer> ssmIdList = new ArrayList<Integer>();
				ssmIdList.add(matchedId++);
				matchedssmmap.put(key, ssmIdList);
				
				ArrayList<Integer> allssmIdList = new ArrayList<Integer>();
				allssmIdList.add(i);
				allssmmap.put(key, allssmIdList);
			}
		}
		
		double[] glycoRtList = new double[glycanRt2.size()];
		double[] pepRtList = new double[pepRt2.size()];
		for (int i = 0; i < glycoRtList.length; i++) {
			glycoRtList[i] = glycanRt2.get(i);
			pepRtList[i] = pepRt2.get(i);
		}
		Regression reg = new Regression(glycoRtList, pepRtList);
		reg.linear();
		this.bestEstimate = reg.getBestEstimates();

		ArrayList<GlycoPeptideLabelPair> feaslist = new ArrayList<GlycoPeptideLabelPair>();
		Iterator <String> it = gpmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			
			IGlycoPeptide peptide = peps[gpmap.get(key)];
			ArrayList <Integer> ssmIdList = matchedssmmap.get(key);
			ArrayList <Integer> allssmIdList = allssmmap.get(key);
			int [] scans = new int[ssmIdList.size()];
			double [] scores = new double[ssmIdList.size()];
			int deleId = -1;
			int deleScore = 0;
			int matchedDeleId = -1;
			for(int i=0;i<allssmIdList.size();i++){
				int ssmid = allssmIdList.get(i);
				scans[i] = ssms[ssmid].getScanNum();
				scores[i] = ssms[ssmid].getScore();
				if(scores[i]>deleScore){
					deleId = ssmid;
					matchedDeleId = ssmIdList.get(i);
				}
			}
			
			String key2 = key.substring(0, key.indexOf("_"))+"_"+deleId;
			int charge = ssms[deleId].getPreCharge();
			double [] labelMasses = peptide.getLabelMasses();
			double [] monoMasses = new double [labelMasses.length];
			int labelType = labelTypeMap.get(key2);
			
			for(int i=0;i<labelMasses.length;i++){
				monoMasses[i] = ssms[deleId].getPreMz()+(labelMasses[i]-labelMasses[labelType])/(double)charge;
			}

			LabelFeatures feas = this.getFeatures(charge, monoMasses, scans, scores);
/*if(key.equals("1_120_1_3")){
				System.out.println("NGlyStrucLabelGetter761\t"+Arrays.toString(monoMasses)+"\t"+Arrays.toString(scans)+"\t"+Arrays.toString(scores)
						+"\t"+feas.isValidate()+"\t"+ssms[deleId].getPreMz()+"\t"+labelType);
			}
			if(peptide.getSequence().equals("R.EN*GTISR.Y")){
				System.out.println("NGlyStrucLabelGetter764\t"+Arrays.toString(monoMasses)+"\t"+Arrays.toString(scans)+"\t"+Arrays.toString(scores));
}	*/
//			if(!feas.isValidate()) continue;
			GlycoPeptideLabelPair glycoPair = new GlycoPeptideLabelPair(peptide, feas, gpmap.get(key), ssmIdList, matchedDeleId);
			feaslist.add(glycoPair);
		}
//System.out.println("NGlystrucLabelGetter 757\t"+feaslist.size());
		this.feas = feaslist.toArray(new GlycoPeptideLabelPair[feaslist.size()]);
	}
	
	public LabelFeatures getFeatures(int charge, double [] monoMasses, int [] scans, 
			double [] scores){

		int scanNum = scans[0];
		IMS1Scan ms1Scan = null;
		while(true){
			if((ms1Scan=this.scanlist.getScan(scanNum))!=null){
				break;
			}else{
				scanNum--;
			}
		}

		double [] idenRtList = new double [scans.length];
		idenRtList[0] = ms1Scan.getRTMinute();

		if(scans.length>1){
			
			IMS1Scan ms1Scan00 = ms1Scan;
			int idenRtListId = 1;
			int scanNum00 = scanNum;
			double lastRt = idenRtList[0];
			
			while((ms1Scan00=this.scanlist.getNextScan(scanNum00))!=null){
				if(ms1Scan00.getScanNum()>scans[idenRtListId]){
					idenRtList[idenRtListId] = lastRt;
					idenRtListId++;
				}
				if(idenRtListId==idenRtList.length){
					break;
				}
				lastRt = ms1Scan00.getRTMinute();
				scanNum00++;
			}
			
			int [] subscans = this.validateScans(scans, scores, idenRtList);
			
			if(subscans.length<scans.length){
				int begid = -1;
				for(int i=0;i<scans.length;i++){
					if(subscans[0]==scans[i]){
						begid = i;
						break;
					}
				}
				
				double [] subRtList = new double [subscans.length];
				System.arraycopy(idenRtList, begid, subRtList, 0, subRtList.length);
				
				scans = subscans;
				idenRtList = subRtList;
				scanNum = scans[0];
				while(true){
					if((ms1Scan=this.scanlist.getScan(scanNum))!=null){
						break;
					}else{
						scanNum--;
					}
				}
			}
		}

//		if(idenRtList[idenRtList.length-1]-idenRtList[0]>3) {
//			return feas;
//		}
//		System.out.println(scans.length);
//		System.out.println(Arrays.toString(newScans));
//		System.out.println("quanfeaturegetter181\tscores\t"+Arrays.toString(scores));
//		System.out.println("quanfeaturegetter181\tscans\t"+Arrays.toString(scans));
//		System.out.println("quanfeaturegetter181\trts\t"+Arrays.toString(idenRtList));

//		System.out.println("quanfeaturegetter181\tscores\t"+Arrays.toString(scores));
//		System.out.println("quanfeaturegetter181\tscans\t"+Arrays.toString(scans));
//		System.out.println("quanfeaturegetter181\trts\t"+Arrays.toString(idenRtList));
		
		LabelFeatures feas = new LabelFeatures(monoMasses, charge, idenRtList);
		
		boolean [] leftMisses = new boolean [monoMasses.length];
		Arrays.fill(leftMisses, false);
		int [] leftMissNum = new int [monoMasses.length];
		Arrays.fill(leftMissNum, 0);
		boolean [] rightMisses = new boolean [monoMasses.length];
		Arrays.fill(rightMisses, false);
		int [] rightMissNum = new int [monoMasses.length];
		Arrays.fill(rightMissNum, 0);

		LabelFeature fea = this.getFeature(charge, monoMasses, ms1Scan, rightMissNum, rightMisses);

		if(!fea.isMiss())
			feas.addFeature(fea);
		
		IMS1Scan prev;
		int prevscan = scanNum;
		while((prev=scanlist.getPreviousScan(prevscan))!=null){
			prevscan = prev.getScanNum();
			LabelFeature prevfea = this.getFeature(charge, monoMasses, prev, leftMissNum, leftMisses);

			if(prevfea.isMiss()) break;
			else feas.addFeature(prevfea);

			if(idenRtList[0]-prev.getRTMinute()>2) break;
		}

L:		for(int sn=0;sn<scans.length;sn++){
			
			if(scanNum>scans[sn])
				continue;

			if(sn==0){
				
				IMS1Scan next;
				int nextscan = scanNum;

				while((next=scanlist.getNextScan(nextscan))!=null){
					
					nextscan = next.getScanNum();
					LabelFeature nextfea = this.getFeature(charge, monoMasses, next, rightMissNum, rightMisses);

					if(nextfea.isMiss()) break;
					else feas.addFeature(nextfea);
										
					if(next.getRTMinute()-idenRtList[idenRtList.length-1]>2) break L;
				}
				
				scanNum = nextscan;
				
			}else{
				
				IMS1Scan nexms1Scan = null;
				int reNexscan = scans[sn];
				while(true){
					if((nexms1Scan=this.scanlist.getScan(reNexscan))!=null){
						break;
					}else{
						reNexscan--;
					}
				}

				LabelFeature reNexfea = this.getFeature(charge, monoMasses, nexms1Scan, rightMissNum, rightMisses);

				if(!reNexfea.isMiss())
					feas.addFeature(reNexfea);
				
				IMS1Scan next;
				int nextscan = reNexscan;

				while((next=scanlist.getNextScan(nextscan))!=null){
					nextscan = next.getScanNum();

					LabelFeature nextfea = this.getFeature(charge, monoMasses, next, rightMissNum, rightMisses);

					if(nextfea.isMiss()) break;
					else feas.addFeature(nextfea);
					
					if(next.getRTMinute()-idenRtList[idenRtList.length-1]>2) break L;
				}
				
				scanNum = nextscan;
			}
		}
		
//		feas.setInfo();
		feas.setInfo2();
		return feas;
	}
	
	public LabelFeature getFeature(int charge, double [] monoMasses, IMS1Scan ms1Scan,
			int [] missNum, boolean [] misses){

		int scanNum = ms1Scan.getScanNum();
		LabelFeature fea = new LabelFeature(scanNum, charge, monoMasses, ms1Scan.getRTMinute());
		
		double tolerance = this.mzThresPPM*monoMasses[0]*1E-6;
		tolerance = tolerance<0.02 ? 0.02 : tolerance;
		IPeak [] peaks = ms1Scan.getPeakList().getPeakArray();
		fea.match(peaks, tolerance, missNum, misses);
		
		return fea;
	}
	
	private int [] validateScans(int [] scans, double [] scores, double [] rts){
		
		if(rts[rts.length-1]-rts[0]<3) {

			return scans;
			
		}else{
			
			int [] newScans = new int[scans.length-1];
			double [] newScores = new double[scores.length-1];
			double [] newRts = new double [rts.length-1];
			
			if(scores[0]>scores[scores.length-1]){
				
				System.arraycopy(scans, 0, newScans, 0, newScans.length);
				System.arraycopy(scores, 0, newScores, 0, newScores.length);
				System.arraycopy(rts, 0, newRts, 0, newRts.length);
				
			}else{
				
				System.arraycopy(scans, 1, newScans, 0, newScans.length);
				System.arraycopy(scores, 1, newScores, 0, newScores.length);
				System.arraycopy(rts, 1, newRts, 0, newRts.length);
			}

			scans = newScans;
			scores = newScores;
			rts = newRts;

			return validateScans(scans, scores, rts);
		}
	}
	
	/**
	 * 
	 * @param pepMr
	 *            the mass of the peptide without glycans and labels
	 * @param addMasses
	 *            possible label mass
	 * @return
	 */
	protected double[] getMatchMasses(double pepMr, double[] addMasses) {
		double[] matchMass = new double[addMasses.length];
		for (int i = 0; i < addMasses.length; i++) {
			double d = pepMr + addMasses[i];
			matchMass[i] = d;
		}
		return matchMass;
	}

	public HashMap<String, IGlycoPeptide> getPepMap() {
		return pepMap;
	}

	public LabelType getType() {
		return labeltype;
	}
	
	public NGlycoSSM[] getGlycoSpectra(){
		return ssms;
	}
	
	public IGlycoPeptide[] getGlycoPeptides(){
		return peps;
	}
	
	public GlycoPeptideLabelPair[] getFeatures(){
		return feas;
	}

	public double[] getBestEstimate(){
		
		return bestEstimate;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
