/* 
 ******************************************************************************
 * File: NGlyStrucFreeGetter.java * * * Created on 2012-5-11
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.GlycoPeptide;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
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
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.util.math.MathTool;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ck
 *
 * @version 2012-5-11, 14:13:03
 */
public class NGlyStrucFreeGetter extends AbstractFeaLFreeGetter {
	
	private ArrayList <Double> pepRt;
	private ArrayList <Double> glycanRt;
	private HashMap <Integer, IGlycoPeptide> scannumPepMap;

	/**
	 * 
	 * @param peakfile
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public NGlyStrucFreeGetter(String peakfile) throws IOException, XMLStreamException {
		super(peakfile, 0);
		// TODO Auto-generated constructor stub
		this.pepRt = new ArrayList <Double>();
		this.glycanRt = new ArrayList <Double>();
		this.scannumPepMap = new HashMap <Integer, IGlycoPeptide>();
	}

	/**
	 * @param peakfile
	 * @param para
	 * @param glycanType
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public NGlyStrucFreeGetter(String peakfile, GlycoJudgeParameter para) throws IOException, XMLStreamException {
		super(peakfile, para, 0);
		// TODO Auto-generated constructor stub
		this.pepRt = new ArrayList <Double>();
		this.glycanRt = new ArrayList <Double>();
		this.scannumPepMap = new HashMap <Integer, IGlycoPeptide>();
	}

	public void addPeptide(IGlycoPeptide peptide, AminoacidModification aam){
		
		String key = PeptideUtil.getSequence(peptide.getSequence());
		double pepMr = peptide.getMH() - AminoAcidProperty.PROTON_W;

		boolean begin = false;
		int loc = 0;
		char aa = '\u0000';
		
		char [] cs = peptide.getSequence().toCharArray();
		int glycoCount = 0;
		for(int i=0;i<cs.length;i++){
			if(cs[i]=='.'){
				if(begin){
					begin = false;
				}else{
					begin = true;
				}
			}else if(cs[i]>='A' && cs[i]<='Z'){
				if(begin){
					loc++;
					aa = cs[i];
				}
			}else if(cs[i]=='-'){
				
			}else{
				
				double add = aam.getAddedMassForModif(cs[i]);
				if(add<=1 && aa=='N'){	// Asp_Asn, 0.984016
					
					pepMr -= add;
					ModSite ms = ModSite.newInstance_aa(aa);
					GlycoSite site = new GlycoSite(ms, loc, aa, add);
					peptide.addGlycoSite(site);
					glycoCount++;
				}
			}
		}
		peptide.setPepMrNoGlyco(pepMr);
		if(glycoCount!=1) return;

		if(pepMap.containsKey(key)){
			
			IGlycoPeptide p1 = pepMap.get(key);
			double inten0 = peptide.getInten();
			double inten1 = p1.getInten();
			if(inten1 < inten0)
				pepMap.put(key, peptide);
		}else{
			pepMap.put(key, peptide);
		}
		this.scannumPepMap.put(peptide.getScanNumBeg(), peptide);
	}
	
	public HashMap <String, IGlycoPeptide> getPepMap(){
		return glycoPepMap;
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
	
	public void getFomula(){
		
		if(this.pepRt.size()==this.glycanRt.size()){
			
		}
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
		HashMap <String, FreeFeatures> feaMap = this.getGlycoFeatures();
		System.out.println("abstract517 feamap\t"+feaMap.size());
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
	
	private static void test2() throws FileDamageException, IOException, PeptideParsingException, XMLStreamException{
		
		NGlyStrucFreeGetter getter = new NGlyStrucFreeGetter("H:\\20130201_glyco\\20111123_HILIC_1105_HCD_111124034738.mzXML");

		NGlycoPepCriteria cri = new NGlycoPepCriteria(true);
		IPeptideListReader reader = new PeptideListReader("H:\\20130201_glyco\\" +
				"20111122_HILIC_1105_deglyco_111123021533_F001914.csv.ppl");
		ISearchParameter para = reader.getSearchParameter();
		AminoacidModification aam = para.getVariableInfo();
		IPeptide pep;
		while((pep=reader.getPeptide())!=null){
			if(cri.filter(pep)){
				IGlycoPeptide gp = new GlycoPeptide(pep);
				getter.addPeptide(gp, aam);
			}
		}

		System.out.println("abstract pepnum\t"+getter.pepMap.size());
		
		HashMap <String, FreeFeatures> feaMap = getter.getGlycoFeatures();
		
		System.out.println("abstract517 feamap\t"+feaMap.size());
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
		
		HashMap <String, FreeFeatures> feaMap = this.getGlycoFeatures();
		System.out.println("abstract517 feamap\t"+feaMap.size());
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

		NGlyStrucFreeGetter.test2();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.Quan.labelFree.AbstractFeaLFreeGetter#getGlycoFeatures()
	 */
	@Override
	public HashMap<String, FreeFeatures> getGlycoFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

}
