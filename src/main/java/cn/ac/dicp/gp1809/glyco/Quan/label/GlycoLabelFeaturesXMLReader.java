/* 
 ******************************************************************************
 * File: GlycoQuanXMLReader.java * * * Created on 2011-3-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.peptide.GlycoPeptide;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;

/**
 * @author ck
 *
 * @version 2011-3-16, 10:41:00
 */
public class GlycoLabelFeaturesXMLReader extends LabelFeaturesXMLReader
{

	private Iterator <Element> glycospectraIt;
	private Iterator <Element> glycopeptideIt;
	private Iterator <Element> glycofeaturesIt;
	
	private IGlycoPeptide[] peps;
	private NGlycoSSM[] matchedssms;
	private NGlycoSSM[] unmatchedssms;
	private GlycoPeptideLabelPair [] pairs;
	private double[] bestEstimate;
	
	private HashMap <Integer, String> peakOneLineMap;

	public GlycoLabelFeaturesXMLReader(String file) throws DocumentException{
		this(new File(file));
	}
	
	public GlycoLabelFeaturesXMLReader(File file) throws DocumentException{
		super(file);
	}
	
	protected void getProfileData() {
		
		System.out.println("Reading "+file.getName()+" ......");
		
		this.glycospectraIt = root.elementIterator("GlycoSpectra");
		this.glycopeptideIt = root.elementIterator("GlycoPeptides");
		this.glycofeaturesIt = root.elementIterator("Features_Pair");

		String ss = root.attributeValue("BestEstimates");
		if(ss!=null){
			String [] sss = ss.split("_");
			this.bestEstimate = new double[]{Double.parseDouble(sss[0]), Double.parseDouble(sss[1])};
		}
		
		try {
			
			String sType = root.attributeValue("Label_Type");
			this.type = LabelType.getLabelType(sType);
			String sGrad = root.attributeValue("Gradient");
			this.gradient = sGrad.equals("true");
			
			String [] sIntensity = root.attributeValue("TotalIntensity").split("_");
			this.totalIntensity = new double [sIntensity.length];
			for(int i=0;i<sIntensity.length;i++){
				this.totalIntensity[i] = Double.parseDouble(sIntensity[i]);
			}
			
			if(type==LabelType.SixLabel){
				
				ArrayList <String> ratioNameList = new ArrayList<String>();
				ArrayList <String> allRatioNameList = new ArrayList<String>();
				for(int i=1;i<=6;i++){
					for(int j=i+1;j<=6;j++){
						String s1 = j+"/"+i;
						String s2 = i+"/"+j;
						ratioNameList.add(s1);
						allRatioNameList.add(s1);
						allRatioNameList.add(s2);
					}
				}

				this.ratioNames = ratioNameList.toArray(new String[ratioNameList.size()]);
				this.allRatioNames = allRatioNameList.toArray(new String[ratioNameList.size()]);
//				this.totalFeaturesMedian = new double [allRatioNames.length];

				this.setMods();
				
			}else if(type==LabelType.FiveLabel){
				
				ArrayList <String> ratioNameList = new ArrayList<String>();
				ArrayList <String> allRatioNameList = new ArrayList<String>();
				for(int i=1;i<=5;i++){
					for(int j=i+1;j<=5;j++){
						String s1 = j+"/"+i;
						String s2 = i+"/"+j;
						ratioNameList.add(s1);
						allRatioNameList.add(s1);
						allRatioNameList.add(s2);
					}
				}

				this.ratioNames = ratioNameList.toArray(new String[ratioNameList.size()]);
				this.allRatioNames = allRatioNameList.toArray(new String[ratioNameList.size()]);
//				this.totalFeaturesMedian = new double [allRatioNames.length];

				this.setMods();
				
			}else{
				
				ArrayList <LabelInfo[]> infosList = new ArrayList <LabelInfo[]>();
				ArrayList <Short> shortList = new ArrayList <Short>();
				
				Iterator <Element> labelIt = root.elementIterator();
				while(labelIt.hasNext()){
					Element eLabel = labelIt.next();
					String s = eLabel.getName();
					if(s.startsWith(sType)){
						short labelType = Short.parseShort(s.substring(s.length()-1,s.length()));
						shortList.add(labelType);
						ArrayList <LabelInfo> iList = new ArrayList <LabelInfo>();
						Iterator <Element> typeIt = eLabel.elementIterator("Label_Infomation");
						while(typeIt.hasNext()){
							Element eType = typeIt.next();
							if(eType.attributeCount()>0){
								String des = eType.attributeValue("Description");
								ModSite site = ModSite.parseSite(eType.attributeValue("Site"));
								double mass = Double.parseDouble(eType.attributeValue("Mass"));
								String symbolStr = eType.attributeValue("Symbol");
								char symbol = symbolStr.length()==0 ? '\u0000' : symbolStr.charAt(0);

								LabelInfo info = new LabelInfo(site, mass, des);
								info.setSymbol(symbol);
								iList.add(info);
							}
						}
						LabelInfo [] infos = iList.toArray(new LabelInfo[iList.size()]);
						infosList.add(infos);
					}else{
						break;
					}
				}
				
				LabelInfo [][] infosArray = infosList.toArray(new LabelInfo [infosList.size()][]);
				short [] used = new short[shortList.size()];
				for(int i=0;i<shortList.size();i++){
					used[i] = shortList.get(i);
				}
				
				ArrayList <String> ratioNameList = new ArrayList<String>();
				ArrayList <String> allRatioNameList = new ArrayList<String>();
				for(int i=0;i<used.length;i++){
					for(int j=i+1;j<used.length;j++){
						String s1 = used[j]+"/"+used[i];
						String s2 = used[i]+"/"+used[j];
						ratioNameList.add(s1);
						allRatioNameList.add(s1);
						allRatioNameList.add(s2);
					}
				}
				
				this.ratioNames = ratioNameList.toArray(new String[ratioNameList.size()]);
				this.allRatioNames = allRatioNameList.toArray(new String[ratioNameList.size()]);
//				this.totalFeaturesMedian = new double [allRatioNames.length];
				
				this.type.setUsed(used);
				this.type.setInfo(infosArray);

				this.setMods();
			}
			
			this.setProNameAccesser();
			this.setMods();
			this.parse();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parse(){

		ArrayList <IGlycoPeptide> peplist = new ArrayList <IGlycoPeptide>();
		
		while(glycopeptideIt.hasNext()){
			
			Element eGlycoPeptide = glycopeptideIt.next();
			
			String baseName = eGlycoPeptide.attributeValue("BaseName");
			int scanBeg = Integer.parseInt(eGlycoPeptide.attributeValue("ScanNum"));
			String seq = eGlycoPeptide.attributeValue("Sequence");
			short pepCharge = Short.parseShort(eGlycoPeptide.attributeValue("Charge"));
			double peprt = Double.parseDouble(eGlycoPeptide.attributeValue("rt"));
			double pepMass = Double.parseDouble(eGlycoPeptide.attributeValue("pepMr"));
			float score = Float.parseFloat(eGlycoPeptide.attributeValue("Score"));
			String ref = eGlycoPeptide.attributeValue("Reference");

			HashSet <ProteinReference> refset = new HashSet <ProteinReference>();
			HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
			HashMap <String, SimpleProInfo> proInfoMap = new HashMap <String, SimpleProInfo>();
			
			String proref = "";
			String [] reflist = ref.split("\\$");
			
			for(int i=0;i<reflist.length;i++){
				
				String [] sss = reflist[i].split("\\+");
				ProteinReference pr = ProteinReference.parse(sss[0]);
				refset.add(pr);
				
				int beg = Integer.parseInt(sss[1]);
				int end = Integer.parseInt(sss[2]);
				
				if(sss.length==3){
					SeqLocAround sla = new SeqLocAround(beg, end, "", "");
					locAroundMap.put(pr.toString(), sla);
				}else if(sss.length==4){
					SeqLocAround sla = new SeqLocAround(beg, end, sss[3], "");
					locAroundMap.put(pr.toString(), sla);
				}else if(sss.length==5){
					SeqLocAround sla = new SeqLocAround(beg, end, sss[3], sss[4]);
					locAroundMap.put(sss[0], sla);
				}
				
				String refname = pr.getName();
				SimpleProInfo info = this.accesser.getProInfo(refname);
				proInfoMap.put(pr.toString(), info);
				proref += info.getPartRef();
				proref += ";";
			}

			GlycoPeptide peptide = new GlycoPeptide(seq, pepCharge, refset, baseName, 
					scanBeg, scanBeg, locAroundMap, pepMass);
			
			peptide.setPrimaryScore(score);
			peptide.setRetentionTime(peprt);
			peptide.setProInfoMap(proInfoMap);
			peptide.setDelegateReference(proref.substring(0, proref.length()-1));

			Iterator <Element> itF = eGlycoPeptide.nodeIterator();
			
			while(itF.hasNext()){
		
				Element ef = itF.next();

				ModSite site = ModSite.newInstance_aa(ef.attributeValue("Site").charAt(0));
				int loc = Integer.parseInt(ef.attributeValue("Loc"));
				char sym = ef.attributeValue("Symbol").charAt(0);
				double mass = Double.parseDouble(ef.attributeValue("Mass"));
				
				peptide.addGlycoSite(new GlycoSite(site, loc, sym));
			}
			
			peplist.add(peptide);
		}

		this.peps = peplist.toArray(new IGlycoPeptide[peplist.size()]);
		
		ArrayList <NGlycoSSM> matchedlist = new ArrayList <NGlycoSSM>();
		ArrayList <NGlycoSSM> unmatchedlist = new ArrayList <NGlycoSSM>();
		
		while(glycospectraIt.hasNext()){
			
			Element eGlycoSpectrum = glycospectraIt.next();
			Integer scannum = Integer.parseInt(eGlycoSpectrum.attributeValue("ScanNum"));
			double rt = Double.parseDouble(eGlycoSpectrum.attributeValue("RT"));
			
			String peakOneLine = eGlycoSpectrum.attributeValue("PeakOneLine");
			MS2PeakList ms2PeakList = MS2PeakList.parsePeaksOneLine(peakOneLine);
			IPeak [] peaks = ms2PeakList.getPeakArray();

			int rank = Integer.parseInt(eGlycoSpectrum.attributeValue("Rank"));
			double score = Double.parseDouble(eGlycoSpectrum.attributeValue("Score"));
			double preMz = Double.parseDouble(eGlycoSpectrum.attributeValue("PrecursorMz"));
			int preCharge = ms2PeakList.getPrecursePeak().getCharge();
			double mass = Double.parseDouble(eGlycoSpectrum.attributeValue("GlycoMass"));
			double pepMassExp = Double.parseDouble(eGlycoSpectrum.attributeValue("PeptideMassExperiment"));
			int peptideID = Integer.parseInt(eGlycoSpectrum.attributeValue("PeptideID"));
			int labelTypeID = Integer.parseInt(eGlycoSpectrum.attributeValue("LabelTypeID"));
			String [] strucss = eGlycoSpectrum.attributeValue("StructureID").split("_");
			int[] structureid = new int[]{Integer.parseInt(strucss[0]), Integer.parseInt(strucss[1])};
			String matched = eGlycoSpectrum.attributeValue("MatchedPeaks");
			String glycoCT = eGlycoSpectrum.attributeValue("GlycoCT").replaceAll(" ", "\n");
			String name = eGlycoSpectrum.attributeValue("Name");
			
			HashSet <Integer> matchedPeaks = new HashSet <Integer>();
			String [] ss = matched.split("_");
			for(int i=0;i<ss.length;i++){
				matchedPeaks.add(Integer.parseInt(ss[i]));
			}

			GlycoTree tree = new GlycoTree(glycoCT);
			tree.setIupacName(name);
			tree.setMonoMass(mass);

			NGlycoSSM ssm = new NGlycoSSM(scannum, preCharge, preMz, pepMassExp, peaks, rank, matchedPeaks, tree, score);
			ssm.setRT(rt);
			ssm.setPeptideid(peptideID);
			ssm.addMatchPepID(peptideID, labelTypeID);
			ssm.setGlycanid(structureid);
			
			if(peptideID==-1){
				unmatchedlist.add(ssm);
			}else{
				double deltaMz = Double.parseDouble(eGlycoSpectrum.attributeValue("DeltaMz")); 
				ssm.setDeltaMz(deltaMz);
				matchedlist.add(ssm);
			}
		}
		
		this.matchedssms = matchedlist.toArray(new NGlycoSSM[matchedlist.size()]);
		this.unmatchedssms = unmatchedlist.toArray(new NGlycoSSM[unmatchedlist.size()]);

		ArrayList<GlycoPeptideLabelPair> glycoFeasList = new ArrayList<GlycoPeptideLabelPair>();
		while(glycofeaturesIt.hasNext()){

			Element eGlycoFeas = glycofeaturesIt.next();
			int peptideid = Integer.parseInt(eGlycoFeas.attributeValue("PeptideId"));
			String [] ss = eGlycoFeas.attributeValue("GlycanId").split("_");
			int bestid = Integer.parseInt(ss[0]);
			ArrayList<Integer> ssmsidlist = new ArrayList<Integer>();
			for(int i=1;i<ss.length;i++){
				ssmsidlist.add(Integer.parseInt(ss[i]));
			}
			
			String [] massesStr = eGlycoFeas.attributeValue("Masses").split("_");
			double [] masses = new double [massesStr.length];
			for(int i=0;i<masses.length;i++){
				masses[i] = Double.parseDouble(massesStr[i]);
			}
			
			int presentFeaNum = 0;
			String [] totalIntenStr = eGlycoFeas.attributeValue("TotalIntensity").split("_");
			double [] totalInten = new double [totalIntenStr.length];
			for(int i=0;i<totalInten.length;i++){
				totalInten[i] = Double.parseDouble(totalIntenStr[i]);
				if(totalInten[i]>0) presentFeaNum++;
			}
			
			String [] ratioStr = eGlycoFeas.attributeValue("Ratio").split("_");
			double [] ratio = new double [ratioStr.length];
			for(int i=0;i<ratio.length;i++){
				ratio[i] = Double.parseDouble(ratioStr[i]);
			}
			
			String [] riaStr = eGlycoFeas.attributeValue("RIA").split("_");
			double [] ria = new double [riaStr.length];
			for(int i=0;i<ria.length;i++){
				ria[i] = Double.parseDouble(riaStr[i]);
			}

			int scancount = eGlycoFeas.nodeCount();
			int [] scanList = new int[scancount];
			double [][] intenList = new double[scancount][];
			double [] rtList = new double[scancount];
			int id = 0;
			
			ArrayList <Double> [] riaList = new ArrayList [totalIntenStr.length/2];
			for(int i=0;i<riaList.length;i++){
				riaList[i] = new ArrayList <Double>();
			}
			
			Iterator <Element> itScan = eGlycoFeas.nodeIterator();
			while(itScan.hasNext()){
				Element eScan = itScan.next();
				scanList[id] = Integer.parseInt(eScan.attributeValue("Scannum"));
				rtList[id] = Double.parseDouble(eScan.attributeValue("Rt"));
				String [] labelIntenStr = eScan.attributeValue("Intensity").split("_");
				intenList[id] = new double [labelIntenStr.length];
				for(int i=0;i<intenList[id].length;i++){
					intenList[id][i] = Double.parseDouble(labelIntenStr[i]);
				}
				
				for(int i=0;i<riaList.length;i++){
					double light = intenList[id][2*i];
					double heavy = intenList[id][2*i+1];
					if(heavy!=0 && light!=0){
						riaList[i].add(heavy/(light+heavy));
					}
				}
				id++;
			}
			
			boolean accurate = eGlycoFeas.attributeValue("Accurate").equals("1");
			
			LabelFeatures feas = new LabelFeatures(masses, scanList,
					intenList, totalInten, rtList, ratio, type, peps[peptideid].getCharge());
			feas.setPresentFeasNum(presentFeaNum);
			feas.setRias(ria);
			feas.setValidate(accurate);

			GlycoPeptideLabelPair glycoFeas = new GlycoPeptideLabelPair(peps[peptideid], feas, matchedssms[bestid], peptideid, ssmsidlist, bestid);
			glycoFeasList.add(glycoFeas);
		}

		this.pairs = glycoFeasList.toArray(new GlycoPeptideLabelPair[glycoFeasList.size()]);
	}
	
	public String[] getTitle() {
		// TODO Auto-generated method stub
		
		ArrayList <String> tList = new ArrayList<String>();
		tList.add("Selected");
		short [] used = type.getUsed();
		tList.add("Sequence");
		tList.add("Glyco_Mass");
		tList.add("Pep_Mass");
		
		for(int i=0;i<ratioNames.length;i++){
			tList.add(ratioNames[i]);
		}
		for(int i=0;i<used.length;i++){
			tList.add(type.getLabelName()+"_"+used[i]);
		}
		
		/*for(int i=0;i<used.length;i++){
			for(int j=i+1;j<used.length;j++){
				tList.add(used[j]+"/"+used[i]);
			}
		}
		for(int i=0;i<used.length;i++){
			tList.add(type.getLabelName()+"_"+used[i]);
			tList.add("Percent");
		}*/
		tList.add("Reference");
		tList.add("Name");
		tList.add("Score");
//		tList.add("Source");

		return tList.toArray(new String [tList.size()]);
	}
	
	public GlycoPeptideLabelPair[] getAllSelectedPairs(){
		return pairs;
	}

	public IGlycoPeptide[] getAllGlycoPeptides(){
		return peps;
	}
	
	public NGlycoSSM[] getMatchedGlycoSpectra(){
		return matchedssms;
	}
	
	public NGlycoSSM[] getUnmatchedGlycoSpectra(){
		return unmatchedssms;
	}
	
	public int getPairsNum(){
		return pairs.length;
	}
	
	public GlycoPeptideLabelPair getPairs(int index) {
		// TODO Auto-generated method stub
		return pairs[index];
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getAllSelectedFeatures(int[])
	 */
	@Override
	public GlycoPeptideLabelPair[] getAllSelectedPairs(int[] index) {
		// TODO Auto-generated method stub
		ArrayList <GlycoPeptideLabelPair> pairlist = new ArrayList <GlycoPeptideLabelPair>();
		for(int i=0;i<index.length;i++){
			GlycoPeptideLabelPair fea = pairs[index[i]];
			pairlist.add(fea);
		}
		return pairlist.toArray(new GlycoPeptideLabelPair[pairlist.size()]);
	}
	
	public GlycoPeptideLabelPair[] getAllSelectedPairs(int[] index, boolean normal,
			int[] outputRatio) {
		// TODO Auto-generated method stub
		GlycoPeptideLabelPair[] pairs = new GlycoPeptideLabelPair[index.length];
		for(int i=0;i<index.length;i++){
			LabelFeatures feas = this.pairs[index[i]].getFeatures();
			feas.setNormal(normal);
			feas.setNormalFactor(realNormalFactor);
			feas.setSelectRatio(outputRatio);
			pairs[i] = this.pairs[index[i]];
		}
		return pairs;
	}

	public GlycoQuanResult [] getAllResult(){
		
		HashMap<String, GlycoQuanResult> map = new HashMap<String, GlycoQuanResult>();
		for(int i=0;i<pairs.length;i++){
			
			IGlycoPeptide peptide = peps[pairs[i].getPeptideId()];
			NGlycoSSM ssm = matchedssms[pairs[i].getDeleSSMId()];
			String key = pairs[i].getPeptideId()+"_"+ssm.getGlycanid()[0]+"_"+ssm.getGlycanid()[1];
//System.out.println("glycoLabelFeatureReader487\t"+peptide.getSequence()+"\t"+key);			
			if(map.containsKey(key)){
				map.get(key).addRatioInfo(pairs[i].getSelectRatios(), pairs[i].getSelectRIA(), pairs[i].getTotalIntens(), pairs[i].isAccurate());
				map.get(key).addSSMIds(pairs[i].getSsmsIds());
			}else{
				GlycoQuanResult result = new GlycoQuanResult(peptide, ssm);
				result.addRatioInfo(pairs[i].getSelectRatios(), pairs[i].getSelectRIA(), pairs[i].getTotalIntens(), pairs[i].isAccurate());
				result.addSSMIds(pairs[i].getSsmsIds());
				result.setPeptideId(pairs[i].getPeptideId());
				map.put(key, result);
			}
		}
		
		GlycoQuanResult [] results = map.values().toArray(new GlycoQuanResult[map.size()]);
		for(int i=0;i<results.length;i++){
			results[i].initial();
		}
		return results;
	}
	
	public GlycoQuanResult [] getAllResult(boolean normal){
		
		HashMap<String, GlycoQuanResult> map = new HashMap<String, GlycoQuanResult>();
		for(int i=0;i<pairs.length;i++){
			
			LabelFeatures feas = pairs[i].getFeatures();
			feas.setNormal(normal);
			feas.setNormalFactor(realNormalFactor);
			IGlycoPeptide peptide = peps[pairs[i].getPeptideId()];
			NGlycoSSM ssm = matchedssms[pairs[i].getDeleSSMId()];
			String key = pairs[i].getPeptideId()+"_"+ssm.getGlycanid()[0]+"_"+ssm.getGlycanid()[1];
//System.out.println("glycoLabelFeatureReader487\t"+peptide.getSequence()+"\t"+key);			
			if(map.containsKey(key)){
				map.get(key).addRatioInfo(pairs[i].getSelectRatios(), pairs[i].getSelectRIA(), pairs[i].getTotalIntens(), pairs[i].isAccurate());
				map.get(key).addSSMIds(pairs[i].getSsmsIds());
			}else{
				GlycoQuanResult result = new GlycoQuanResult(peptide, ssm);
				result.addRatioInfo(pairs[i].getSelectRatios(), pairs[i].getSelectRIA(), pairs[i].getTotalIntens(), pairs[i].isAccurate());
				result.addSSMIds(pairs[i].getSsmsIds());
				result.setPeptideId(pairs[i].getPeptideId());
				map.put(key, result);
			}
		}
		
		GlycoQuanResult [] results = map.values().toArray(new GlycoQuanResult[map.size()]);
		for(int i=0;i<results.length;i++){
			results[i].initial();
		}
		return results;
	}

	public double[] getBestEstimate(){
		return bestEstimate;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#close()
	 */
	public void close() {
		// TODO Auto-generated method stub
		this.mods = null;
		this.root = null;
		this.type = null;
		System.gc();
	}

	/**
	 * @param args
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws DocumentException {
		// TODO Auto-generated method stub
		String file = "J:\\human_liver_glycan_quantification\\2014.02.16_2D\\glyco\\20140216_humanliver_2D_with-glycan_HCC_normal_C18-PGC_35%ACN-1.pxml";
		GlycoLabelFeaturesXMLReader reader = new GlycoLabelFeaturesXMLReader(file);
		System.out.println(reader.pairs.length);
	}

}
