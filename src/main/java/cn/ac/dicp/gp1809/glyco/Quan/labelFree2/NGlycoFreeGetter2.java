/* 
 ******************************************************************************
 * File: NGlyStrucFreeGetter2.java * * * Created on 2013-6-9
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.GlycoPeptide;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSpecStrucGetter;
import cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.readers.MaxQuantSitePepReader;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.proteome.quant.profile.Pixel;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;
import flanagan.analysis.Regression;

/**
 * @author ck
 *
 * @version 2013-6-9, 10:31:19
 */
public class NGlycoFreeGetter2 {
	
	protected NGlycoSpecStrucGetter specGetter;
	protected int glycanType;
	
	protected HashMap <String, IGlycoPeptide> pepMap;
	
	private ArrayList <Double> pepRt;
	private ArrayList <Double> glycanRt;
	private double [] fit;

	private NGlycoSSM [] ssms;
	private IGlycoPeptide [] peps;
	private GlycoJudgeParameter parameter;
	
	/**
	 * the variable modification symbol which represent glycosylation, used in O-linked glycans
	 */
	protected HashSet <Character> glycoModSet;

	protected float rtTole = 0.0f;
	protected double mzThresPPM, mzThresAMU; 
	protected double Asp_Asn = 0.984016;
	protected DecimalFormat df5 = DecimalFormats.DF0_5;

	/**
	 * 
	 * @param peakfile
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public NGlycoFreeGetter2(String peakfile) throws IOException, XMLStreamException {
		// TODO Auto-generated constructor stub
		this.pepRt = new ArrayList <Double>();
		this.glycanRt = new ArrayList <Double>();
		this.pepMap = new HashMap <String, IGlycoPeptide>();
		this.fit = new double[]{0, 0};
		
		NGlycoSpecStrucGetter getter = new NGlycoSpecStrucGetter(peakfile);
		this.ssms = getter.getGlycoSSMs();
		this.parameter = GlycoJudgeParameter.defaultParameter();
	}

	/**
	 * @param peakfile
	 * @param para
	 * @param glycanType
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public NGlycoFreeGetter2(String peakfile, GlycoJudgeParameter parameter) throws IOException, XMLStreamException {
		// TODO Auto-generated constructor stub
		this.pepRt = new ArrayList <Double>();
		this.glycanRt = new ArrayList <Double>();
		this.pepMap = new HashMap <String, IGlycoPeptide>();
		this.fit = new double[]{0, 0};
		
		NGlycoSpecStrucGetter getter = new NGlycoSpecStrucGetter(peakfile, parameter);
		this.ssms = getter.getGlycoSSMs();
		this.parameter = parameter;
	}

	public void addPeptide(IGlycoPeptide peptide, AminoacidModification aam) {

		Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		HashSet <Integer> set = new HashSet <Integer>();
		String sequence = peptide.getSequence();
		Matcher matcher = N_GLYCO.matcher(sequence);
		while(matcher.find()){
			set.add(matcher.start());
		}

		double pepMr = peptide.getMH() - AminoAcidProperty.PROTON_W;

		boolean begin = false;
		int loc = 0;
		char aa = '\u0000';

		char[] cs = sequence.toCharArray();
		int glycoCount = 0;
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] == '.') {
				if (begin) {
					begin = false;
				} else {
					begin = true;
				}
			} else if (cs[i] >= 'A' && cs[i] <= 'Z') {
				if (begin) {
					loc++;
					aa = cs[i];
				}
			} else if (cs[i] == '-') {

			} else {

				double add = aam.getAddedMassForModif(cs[i]);
				if (aa == 'N' && set.contains(i-1)) { // Asp_Asn, 0.984016
//				if (aa == 'N' && set.size()==0) { // Asp_Asn, 0.984016
//				if (aa == 'N') { // Asp_Asn, 0.984016
					if(glycoCount==0){
						pepMr -= add;
//						pepMr -= 0.984016;
					}
					ModSite ms = ModSite.newInstance_aa(aa);
					GlycoSite site = new GlycoSite(ms, loc, aa, add);
					peptide.addGlycoSite(site);
					glycoCount++;
				}
			}
		}
		peptide.setPepMrNoGlyco(pepMr);
		if (glycoCount != 1)
			return;
//		System.out.println("NGlycoFreeGetter2_168\t"+peptide.getSequence());
//		if(set.size()==0) System.out.println("NGlycoFreeGetter2_168\t"+peptide.getSequence());
		String key = PeptideUtil.getSequence(peptide.getSequence());
		
		if (pepMap.containsKey(key)) {

			IGlycoPeptide p1 = pepMap.get(key);
			double score0 = peptide.getPrimaryScore();
			double score1 = p1.getPrimaryScore();
			if (score1 < score0)
				pepMap.put(key, peptide);
		} else {
			pepMap.put(key, peptide);
		}
	}
	
	public void match(){
		
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
		
		double [] pepBackMws = new double[peps.length];
		for(int i=0;i<peps.length;i++){
			pepBackMws[i] = peps[i].getPepMrNoGlyco();
		}
		
		double ppm = parameter.getMzThresPPM()/3.0;
		
		for(int i=0;i<ssms.length;i++){

			double pepmass = ssms[i].getPepMassExperiment();
			int id = Arrays.binarySearch(pepBackMws, pepmass-1);
			if(id<0) id = -id-1;
			
			for(int j=id;j<pepBackMws.length;j++){
				
				double deltaMz = pepmass-pepBackMws[j];
				double deltaPPM = ppm*pepmass*1E-6;
				if(Math.abs(deltaMz)<deltaPPM){
					
					ssms[i].addMatchPepID(j);
					
					if(peps[j].getRetentionTime()>0)
						this.pepRt.add(peps[j].getRetentionTime());
					
					this.glycanRt.add(ssms[i].getRT());
					
				}else if(deltaMz*-1.0>deltaPPM){
					break;
				}
			}
			
			int ms1ScanNum = ssms[i].getMS1Scannum();
			int preCharge = ssms[i].getPreCharge();
			double preMz = ssms[i].getPreMz();
			
			Pixel pix = new Pixel(ms1ScanNum, (float) preMz);
			pix.setCharge(preCharge);
//			FreeFeatures fea = pixGetter.getFreeFeatures(pix);
			
//			ssms[i].setFeatures(fea);
		}

		if(this.glycanRt.size()==this.pepRt.size() && this.glycanRt.size()>10){
			
			double [] glycoRtList = new double[glycanRt.size()];
			double [] pepRtList = new double[pepRt.size()];
			for(int i=0;i<glycoRtList.length;i++){
				glycoRtList[i] = glycanRt.get(i);
				pepRtList[i] = pepRt.get(i);
			}
			Regression reg = new Regression(glycoRtList, pepRtList);
			reg.linear();
			this.fit = reg.getBestEstimates();

			for(int i=0;i<ssms.length;i++){
				NGlycoSSM ssm =ssms[i];
				ArrayList <Integer> matchPepScannums = ssm.getMatchPepIDs();
				int bestId = -1;
				double max = 0;
				for(int i1=0;i1<matchPepScannums.size();i1++){
					double score = peps[matchPepScannums.get(i1)].getPrimaryScore();
					double peprt = peps[matchPepScannums.get(i1)].getRetentionTime();
					double y = fit[0]+fit[1]*ssm.getRT();
					if(score/Math.abs(peprt-y)>max){
						max = score/Math.abs(peprt-y);
						bestId = matchPepScannums.get(i1);
					}
				}
				ssm.setPeptideid(bestId);
			}
		}else{
			for(int i=0;i<ssms.length;i++){
				NGlycoSSM ssm =ssms[i];
				ArrayList <Integer> matchPepScannums = ssm.getMatchPepIDs();
				int bestId = -1;
				double max = 0;
				for(int i1=0;i1<matchPepScannums.size();i1++){
					double score = peps[matchPepScannums.get(i1)].getPrimaryScore();
//					double peprt = peps[matchPepScannums.get(i1)].getRetentionTime();
					if(score>max){
						max = score;
						bestId = matchPepScannums.get(i1);
					}
				}
				ssm.setPeptideid(bestId);
			}
		}
	}
	
	public NGlycoSSM[] getGlycoSpectra(){
		return ssms;
	}
	
	public IGlycoPeptide[] getGlycoPeptides(){
		return peps;
	}

	private int matching(NGlycoSSM ssm, double pepMass, int pepScannum, double mzThresPPM, double mzThresAMU){
		
		double mass = ssm.getPepMass();
		double deltaMassPPM = Math.abs(mass-pepMass)/mass*1E6;
		
		if(deltaMassPPM<mzThresPPM){
			
			ssm.addMatchPepID(pepScannum);
			boolean bestMatch = ssm.setDeltaMass(deltaMassPPM);
			if(bestMatch)
				ssm.setBestPepScannum(pepScannum);
			
			return 1;
		}
		
		return -1;
	}
	
	public double[] getBestEstimate(){
		
		return fit;
	}
	
	public double getMedianDiff(){
		
		if(this.pepRt.size()==this.glycanRt.size()){
			
			double [] diffs = new double[this.pepRt.size()];
			for(int i=0;i<diffs.length;i++){
				diffs[i] = pepRt.get(i) - glycanRt.get(i);
			}
			
			double median = MathTool.getMedian(diffs);
			
			return median;
			
		}else{
			return 1000;
		}
	}
	
	private void test(String ppl, String ppl2) throws FileDamageException, IOException, PeptideParsingException{

		NGlycoPepCriteria cri = new NGlycoPepCriteria(true);
		IPeptideListReader reader = new PeptideListReader(ppl);
		ISearchParameter para = reader.getSearchParameter();
		AminoacidModification aam = para.getVariableInfo();
		IPeptide pep;
		while((pep=reader.getPeptide())!=null){
			if(cri.filter(pep)){
				IGlycoPeptide gp = new GlycoPeptide(pep);
				this.addPeptide(gp, aam);
			}
		}
		
		IPeptideListReader reader2 = new PeptideListReader(ppl2);
		ISearchParameter para2 = reader2.getSearchParameter();
		AminoacidModification aam2 = para2.getVariableInfo();
		while((pep=reader2.getPeptide())!=null){
			if(cri.filter(pep)){
				IGlycoPeptide gp = new GlycoPeptide(pep);
				this.addPeptide(gp, aam2);
			}
		}
		
		System.out.println("abstract pepnum\t"+this.pepMap.size());
/*		
		Iterator <String> it = pepMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			double [] mass = massesMap.get(key);
			for(int i=0;i<mass.length;i++){
				System.out.println(key+"\t"+mass[i]);
			}
		}
*/		

/*		
		Iterator <String> it = feaMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			ArrayList<Features> feas = feaMap.get(key);
			double d1 = feas.get(0).getInten();
			double d2 = feas.get(1).getInten();

			Features [] fs = feas.toArray(new Features[feas.size()]);
			GlycoPepLabelPair pair = new GlycoPepLabelPair("", "", (short)0, 0d, fs, type, "", 0d, 0d);
			
//			System.out.println(key+"\t"+d2/d1+"\t"+pair.getRatios()[0]);
		}
*/		
	}
	
	private void zlTest() throws ProteinNotFoundInFastaException, 
		MoreThanOneRefFoundInFastaException, IOException, FastaDataBaseException{
		
		String fasta = "I:\\AGP\\AGP.fasta";
		String file1 = "I:\\AGP\\chytrpsin\\Deamidation (N)Sites.txt";
		String file2 = "I:\\AGP\\trypsin\\Deamidation (N)Sites.txt";
		String file3 = "I:\\AGP\\Gluc\\Deamidation (N)Sites.txt";
		String file4 = "I:\\AGP\\elestase\\Deamidation (N)Sites.txt";
		
		String reg = ">\\([^| ]*\\)";
		IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		
		AminoacidModification aam = new AminoacidModification();
		aam.addModification(ModSite.newInstance_aa('N'), '*', 0.984016, "Deamidation");
		
		NGlycoPepCriteria cri = new NGlycoPepCriteria(true);
		
		MaxQuantSitePepReader reader = new MaxQuantSitePepReader(new File(file1), new File(fasta), reg, judger);
		IPeptide pep = null;
		while((pep=reader.getPeptide())!=null){
			if(cri.filter(pep)){
//System.out.println(pep);				
				IGlycoPeptide gp = new GlycoPeptide(pep);
				this.addPeptide(gp, aam);
			}
		}
		
		System.out.println("abstract pepnum\t"+this.pepMap.size());
	}
	
	/**
	 * @return
	 */
	public double getMS1TotalCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 * @throws XMLStreamException 
	 * @throws PeptideParsingException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException, PeptideParsingException, 
		XMLStreamException {
		// TODO Auto-generated method stub

		Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");

		PeptideListReader reader = new PeptideListReader("H:\\NGLYCO\\NGlyco_original_data_20130613\\2D\\iden\\"
				+ "Rui_20130604_HEK_HILIC_F1_deglyco_F004815.csv.ppl");
		IPeptide peptide = null;
		while((peptide=reader.getPeptide())!=null){
			HashSet <Integer> set = new HashSet <Integer>();
			String sequence = peptide.getSequence();
			Matcher matcher = N_GLYCO.matcher(sequence);
			while(matcher.find()){
				set.add(matcher.start());
			}
			if(set.size()==0 && sequence.contains("N*")){
				System.out.println(sequence);
			}
		}
		reader.close();
	}

	
}
