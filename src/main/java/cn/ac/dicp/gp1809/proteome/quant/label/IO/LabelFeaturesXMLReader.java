/* 
 ******************************************************************************
 * File: LabelFeaturesXMLReader.java * * * Created on 2012-10-12
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanPeptide;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.AbstractFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * @author ck
 *
 * @version 2012-10-12, 9:52:05
 */
public class LabelFeaturesXMLReader extends AbstractFeaturesXMLReader {

	protected PeptidePair [] allPairs;
	protected int pairid = 0;
	
	/**
	 * @param file
	 * @throws DocumentException
	 */
	public LabelFeaturesXMLReader(String file) throws DocumentException {
		super(file);
	}

	/**
	 * @param file
	 * @throws DocumentException
	 */
	public LabelFeaturesXMLReader(File file) throws DocumentException {
		super(file);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getProfileData()
	 */
	@Override
	protected void getProfileData() {
		
		System.out.println("Reading "+file.getName()+" ......");
		
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
//			this.totalFeaturesMedian = new double [allRatioNames.length];

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
//			this.totalFeaturesMedian = new double [allRatioNames.length];

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
//			this.totalFeaturesMedian = new double [allRatioNames.length];
			
			this.type.setUsed(used);
			this.type.setInfo(infosArray);

			this.setMods();
		}
		
		this.readAllPairs();
	}
	
	protected PeptidePair getPairs(){
		
		if(feasIt.hasNext()){
			
			Element eFeas = feasIt.next();
			String baseName = eFeas.attributeValue("BaseName");
			int scanBeg = Integer.parseInt(eFeas.attributeValue("ScanBeg"));
			int scanEnd = Integer.parseInt(eFeas.attributeValue("ScanEnd"));
			short charge = Short.parseShort(eFeas.attributeValue("Charge"));
			String seq = eFeas.attributeValue("Sequence");
			String ref = eFeas.attributeValue("Reference");
			String src = eFeas.attributeValue("File");
			
			float score = 0;
			if(eFeas.attributeValue("Score")!=null){
				score = Float.parseFloat(eFeas.attributeValue("Score"));
			}
			
			boolean accurate = false;
			if(eFeas.attributeValue("Accurate")!=null){
				accurate = eFeas.attributeValue("Accurate").equals("1");
			}
			
			HashSet <ProteinReference> refset = new HashSet <ProteinReference>();
			HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
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
			}
			
			QuanPeptide pep = new QuanPeptide(seq, charge, refset, baseName, scanBeg, scanEnd, locAroundMap);
			pep.setPrimaryScore(score);
			
			String [] massesStr = eFeas.attributeValue("Masses").split("_");
			double [] masses = new double [massesStr.length];
			for(int i=0;i<masses.length;i++){
				masses[i] = Double.parseDouble(massesStr[i]);
			}
			
			int presentFeaNum = 0;
			String [] totalIntenStr = eFeas.attributeValue("TotalIntensity").split("_");
			double [] totalInten = new double [totalIntenStr.length];
			for(int i=0;i<totalInten.length;i++){
				totalInten[i] = Double.parseDouble(totalIntenStr[i]);
				if(totalInten[i]>0) presentFeaNum++;
			}
			
			String [] ratioStr = eFeas.attributeValue("Ratio").split("_");
			double [] ratio = new double [ratioStr.length];
			for(int i=0;i<ratio.length;i++){
				ratio[i] = Double.parseDouble(ratioStr[i]);
			}
			
			String [] riaStr = eFeas.attributeValue("RIA").split("_");
			double [] ria = new double [riaStr.length];
			for(int i=0;i<ria.length;i++){
				ria[i] = Double.parseDouble(riaStr[i]);
			}
			
			int scancount = eFeas.nodeCount();
			int [] scanList = new int[scancount];
			double [][] intenList = new double[scancount][];
			double [] rtList = new double[scancount];
			int id = 0;
			
			ArrayList <Double> [] riaList = new ArrayList [totalIntenStr.length/2];
			for(int i=0;i<riaList.length;i++){
				riaList[i] = new ArrayList <Double>();
			}
			
			Iterator <Element> itScan = eFeas.nodeIterator();
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
/*			double [] ria = new double [riaList.length];
			for(int i=0;i<ria.length;i++){
				if(riaList[i].size()==0){
					if(totalInten[i*2]==0 && totalInten[i*2+1]!=0){
						ria[i] = 1.0;
					}else{
						ria[i] = 0.0;
					}
				}else{
					ria[i] = MathTool.getMedian(riaList[i]);
				}
			}

			double [] ria = new double [ratio.length];
*/			
			LabelFeatures feas = new LabelFeatures(masses, scanList, 
					intenList, totalInten, rtList, ratio, type, charge);

			feas.setPresentFeasNum(presentFeaNum);
			feas.setRias(ria);
			pep.setFeasId(pairid);
//			feas.setValidate(accurate);
			feas.setValidate(true);
			pairid++;
			
			PeptidePair pair = new PeptidePair(pep, feas);
			pair.setSrc(src);
			
			return pair;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#readAllFeatures()
	 */
	@Override
	public void readAllPairs() {
		// TODO Auto-generated method stub

		ArrayList <PeptidePair> pairlist = new ArrayList <PeptidePair>();
		PeptidePair pair;

		while((pair = this.getPairs())!=null){
			pairlist.add(pair);
		}
		
		this.allPairs = pairlist.toArray(new PeptidePair[pairlist.size()]);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getfeaturesNum()
	 */
	@Override
	public int getPairsNum() {
		// TODO Auto-generated method stub
		return allPairs.length;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getAllSelectedFeatures(int[])
	 */
	@Override
	public PeptidePair[] getAllSelectedPairs(int[] index) {
		// TODO Auto-generated method stub
		ArrayList <PeptidePair> pairlist = new ArrayList <PeptidePair>();
		for(int i=0;i<index.length;i++){
			PeptidePair fea = allPairs[index[i]];
			pairlist.add(fea);
		}
		return pairlist.toArray(new PeptidePair[pairlist.size()]);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getAllSelectedFeatures(int[], boolean, int[])
	 */
	@Override
	public PeptidePair[] getAllSelectedPairs(int[] index, boolean normal,
			int[] outputRatio) {
		// TODO Auto-generated method stub
		ArrayList <PeptidePair> pairlist = new ArrayList <PeptidePair>();
		for(int i=0;i<index.length;i++){
			PeptidePair pair = allPairs[index[i]];
			LabelFeatures feas = pair.getFeatures(); 
			feas.setNormal(normal);
			feas.setNormalFactor(realNormalFactor);
			feas.setSelectRatio(outputRatio);
			pairlist.add(pair);
		}
		return pairlist.toArray(new PeptidePair[pairlist.size()]);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getAllSelectedFeatures()
	 */
	@Override
	public PeptidePair[] getAllSelectedPairs() {
		// TODO Auto-generated method stub
		return allPairs;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getAllSelectedFeatures(boolean, int[])
	 */
	@Override
	public PeptidePair[] getAllSelectedPairs(boolean normal, int[] outputRatio) {
		// TODO Auto-generated method stub
		for(int i=0;i<allPairs.length;i++){
			LabelFeatures feas = allPairs[i].getFeatures();
			feas.setNormal(normal);
			feas.setNormalFactor(realNormalFactor);
			feas.setSelectRatio(outputRatio);
		}
		return allPairs;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getFeatures(int)
	 */
	@Override
	public PeptidePair getPairs(int index) {
		// TODO Auto-generated method stub
		return allPairs[index];
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getAllResult(int[], boolean, boolean, int[])
	 */
	@Override
	public QuanResult[] getAllResult(int[] index, boolean nomod,
			boolean normal, int[] outputRatio) throws Exception {
		// TODO Auto-generated method stub
		
		if(this.accesser==null)
			setProNameAccesser();
		
		HashSet <Integer> idxset = new HashSet <Integer>();
		for(int i=0;i<index.length;i++){
			idxset.add(index[i]);
		}
		
		Proteins2 pros = new Proteins2(accesser);
		
		IPeptide idenPep;
		while((idenPep=this.getIdenPep())!=null){
			pros.addPeptide(idenPep);
		}
		
//		HashMap <IPeptide, LabelFeatures> pepMap = new HashMap <IPeptide, LabelFeatures> ();
		for(int i=0;i<allPairs.length;i++){
			if(idxset.contains(i)){
				PeptidePair pair = allPairs[i];
				LabelFeatures feas = pair.getFeatures();
				feas.setNormal(normal);
				feas.setNormalFactor(realNormalFactor);
				feas.setSelectRatio(outputRatio);
				IPeptide pep = pair.getPeptide();
				pros.addPeptide(pep);
			}
		}

		Protein [] proArray = null;
		try {
			proArray = pros.getAllProteins();
		} catch (ProteinNotFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MoreThanOneRefFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FastaDataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList <QuanResult> reslist = new ArrayList <QuanResult>();
		for(int i=0;i<proArray.length;i++){
			
			HashMap<String, LabelQuanUnit> labelQnitMap = new HashMap<String, LabelQuanUnit>();
			Protein pro = proArray[i];
			IPeptide [] peps = pro.getAllPeptides();
			for(int j=0;j<peps.length;j++){
				
				int pairid = ((QuanPeptide)peps[j]).getFeasId();
				if(pairid>=0){
					
					LabelFeatures feas = allPairs[pairid].getFeatures();
					if(feas.isUse()&& feas.isValidate()){
						String key = peps[j].getSequence();
						if(labelQnitMap.containsKey(key)){
							labelQnitMap.get(key).addRatioInfo(allPairs[pairid].getSelectRatios(), allPairs[pairid].getSelectRIA(), allPairs[pairid].getTotalIntens(), allPairs[pairid].isAccurate());
						}else{
							LabelQuanUnit unit = new LabelQuanUnit(peps[j]);
							unit.setDelegateRef(pro.getRefwithSmallestMw().getName());
							unit.setSrc(allPairs[pairid].getSrc());
							unit.setRatioNum(allPairs[pairid].getFeatures().getRatioNum());
							unit.setPairNum(allPairs[pairid].getFeatures().getPairNum());
							unit.addRatioInfo(allPairs[pairid].getSelectRatios(), allPairs[pairid].getSelectRIA(), allPairs[pairid].getTotalIntens(), allPairs[pairid].isAccurate());
							labelQnitMap.put(key, unit);
						}
					}else{
						allPairs[pairid].setDelegateRef(pro.getRefwithSmallestMw().getName());
						allPairs[pairid].setSrc(allPairs[pairid].getSrc());
					}
				}else{
					peps[j].setDelegateReference(pro.getRefwithSmallestMw().getName());
				}
			}
			
			if(labelQnitMap.size()==0)
				continue;

			boolean unique = proArray[i].getUnique();
			IReferenceDetail[] refs = proArray[i].getReferences();
			String [] refName = new String [refs.length];
			for(int j=0;j<refs.length;j++){
				refName[j] = refs[j].getName();
			}
			Arrays.sort(refName);

			LabelQuanUnit[] units = labelQnitMap.values().toArray(new LabelQuanUnit[labelQnitMap.size()]);
			for(int j=0;j<units.length;j++){
				units[j].initial();
			}
			QuanResult lqs = new QuanResult(refName, units, nomod);
			lqs.setUnique(unique);
			if(unique)
				reslist.add(lqs);
		}
		return reslist.toArray(new QuanResult[reslist.size()]);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getAllResult(boolean, boolean, int[])
	 */
	@Override
	public QuanResult[] getAllResult(boolean nomod, boolean normal,
			int[] outputRatio) throws Exception {
		// TODO Auto-generated method stub
		if(this.accesser==null)
			setProNameAccesser();

		Proteins2 pros = new Proteins2(accesser);
		
		QuanPeptide idenPep;
		while((idenPep=((QuanPeptide)this.getIdenPep()))!=null){
			pros.addPeptide(idenPep);
		}
		
		for(int i=0;i<allPairs.length;i++){
			LabelFeatures feas = allPairs[i].getFeatures();
			feas.setNormal(normal);
			feas.setNormalFactor(realNormalFactor);
			feas.setSelectRatio(outputRatio);
			IPeptide pep = allPairs[i].getPeptide();
			pros.addPeptide(pep);
		}

		Protein [] proArray = null;
		try {
			proArray = pros.getAllProteins();
		} catch (ProteinNotFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MoreThanOneRefFoundInFastaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FastaDataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList <QuanResult> reslist = new ArrayList <QuanResult>();
		for(int i=0;i<proArray.length;i++){
			
			HashMap<String, LabelQuanUnit> labelQnitMap = new HashMap<String, LabelQuanUnit>();
			Protein pro = proArray[i];
			IPeptide [] peps = pro.getAllPeptides();
			for(int j=0;j<peps.length;j++){
				
				int pairid = ((QuanPeptide)peps[j]).getFeasId();
				if(pairid>=0){
					LabelFeatures feas = allPairs[pairid].getFeatures();
					if(feas.isUse()&& feas.isValidate()){
						String key = peps[j].getSequence();
						if(labelQnitMap.containsKey(key)){
							labelQnitMap.get(key).addRatioInfo(allPairs[pairid].getSelectRatios(), allPairs[pairid].getSelectRIA(), allPairs[pairid].getTotalIntens(), allPairs[pairid].isAccurate());
						}else{
							LabelQuanUnit unit = new LabelQuanUnit(peps[j]);
							unit.setDelegateRef(pro.getRefwithSmallestMw().getName());
							unit.setSrc(allPairs[pairid].getSrc());
							unit.setRatioNum(allPairs[pairid].getFeatures().getRatioNum());
							unit.setPairNum(allPairs[pairid].getFeatures().getPairNum());
							unit.addRatioInfo(allPairs[pairid].getSelectRatios(), allPairs[pairid].getSelectRIA(), allPairs[pairid].getTotalIntens(), allPairs[pairid].isAccurate());
							labelQnitMap.put(key, unit);
						}
					}else{
						allPairs[pairid].setDelegateRef(pro.getRefwithSmallestMw().getName());
						allPairs[pairid].setSrc(allPairs[pairid].getSrc());
					}
				}else{
					peps[j].setDelegateReference(pro.getRefwithSmallestMw().getName());
				}
			}
			
			if(labelQnitMap.size()==0)
				continue;

			boolean unique = proArray[i].getUnique();
			IReferenceDetail[] refs = proArray[i].getReferences();
			String [] refName = new String [refs.length];
			for(int j=0;j<refs.length;j++){
				refName[j] = refs[j].getName();
			}
			Arrays.sort(refName);

			LabelQuanUnit[] units = labelQnitMap.values().toArray(new LabelQuanUnit[labelQnitMap.size()]);
			for(int j=0;j<units.length;j++){
				units[j].initial();
			}
			QuanResult lqs = new QuanResult(refName, units, nomod);
			lqs.setUnique(unique);
			if(unique)
				reslist.add(lqs);
		}
		return reslist.toArray(new QuanResult[reslist.size()]);
	}

	public LabelQuanUnit [] getAllLabelQuanUnits(boolean normal){
		
		HashMap<String, LabelQuanUnit> map = new HashMap<String, LabelQuanUnit>();
		for(int i=0;i<allPairs.length;i++){
			
			LabelFeatures feas = allPairs[i].getFeatures();
			feas.setNormal(normal);
			feas.setNormalFactor(realNormalFactor);
			IPeptide peptide = allPairs[i].getPeptide();
			String key = peptide.getSequence();

			if(map.containsKey(key)){
				map.get(key).addRatioInfo(allPairs[i].getSelectRatios(), allPairs[i].getSelectRIA(), allPairs[i].getTotalIntens(), allPairs[i].isAccurate());
			}else{
				LabelQuanUnit unit = new LabelQuanUnit(peptide);
				unit.addRatioInfo(allPairs[i].getSelectRatios(), allPairs[i].getSelectRIA(), allPairs[i].getTotalIntens(), allPairs[i].isAccurate());
				map.put(key, unit);
			}
		}
		
		LabelQuanUnit [] units = map.values().toArray(new LabelQuanUnit[map.size()]);
		for(int i=0;i<units.length;i++){
			units[i].initial();
		}
		return units;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#getTitle()
	 */
	@Override
	public String[] getTitle() {
		// TODO Auto-generated method stub
		String [] titles;
		
		ArrayList <String> tList = new ArrayList<String>();
		tList.add("Selected");
		short [] used = type.getUsed();
		tList.add("Sequence");
		for(int i=0;i<ratioNames.length;i++){
			tList.add(ratioNames[i]);
		}
		for(int i=0;i<used.length;i++){
			tList.add(type.getLabelName()+"_"+used[i]);
		}
		tList.add("Reference");
		tList.add("Source");
		titles = tList.toArray(new String [tList.size()]);
		
		return titles;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractFeaturesXMLReader#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		this.allPairs = null;
		this.mods = null;
		this.feasIt = null;
		this.root = null;
		this.type = null;
		System.gc();
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws DocumentException, FileNotFoundException {
		// TODO Auto-generated method stub

//		PrintWriter pw = new PrintWriter("D:\\" +
//				"quatification_data_standard\\1_1_1(1)\\new\\100416mousebrain270ug_250mM.pxml.txt");
		LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader("F:\\Data\\SCX_mouse-liver-control\\W-H-X-D-1" +
				"\\mouse_liver_w_combine.pxml");
//		double [] nor = new double [15];
//		Arrays.fill(nor, 1);
//		nor[0] = 4;
//		nor[1] = 1;
//		nor[2] = 2;
//		nor[3] = 2;
//		nor[4] = 4;
		
		reader.setTheoryRatio(new double[]{1.0});
		QuanResult [] results = null;
		
		try {
			results = reader.getAllResult(false, true, new int[]{0});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(results.length);
		int count = 0;
		
		/*for(int i=0;i<results.length;i++){
			double [] ratios = results[i].getRatio();
			for(int j=0;j<ratios.length;j++){
				System.out.print(ratios[j]+"\t");
			}
			System.out.println();
		}*/
//		System.out.println(count+"\t"+results.length);
		
/*		HashSet <String> set = new HashSet <String>();
		LabelFeatures feas = null;
		while((feas=reader.getFeatures())!=null){
			
			double [] ratios = feas.getRatios();
			pw.print(feas.getSequence()+"\t");
			for(int i=0;i<ratios.length;i++){
				pw.print(Math.log(ratios[i])/Math.log(2)+"\t");
			}
			pw.println();

			String seq = feas.getSequence();
			set.add(seq);
			int charge = feas.getCharge();
			int [] scans = feas.getScanList();
			double [] masses = feas.getMasses();

			if(seq.contains("VYSPHVLNLTLVDLPGMTK")){
				
				for(int i=0;i<masses.length;i++){
					System.out.print(masses[i]+"\t");
				}
				System.out.println();
				
				System.out.println(charge);
				double [][] intens = feas.getIntenList();
				for(int i=0;i<intens.length;i++){
					System.out.print(scans[i]+"\t");
					for(int j=0;j<intens[i].length;j++)
						System.out.print(intens[i][j]+"\t");
					System.out.println();
				}
				System.out.println("~~~~~~~~~~~~");
				double [] ratio = feas.getRatios();
				for(int i=0;i<ratio.length;i++){
					System.out.print(ratio[i]+"\t");
				}
				System.out.println("\n~~~~~~~~~~~~");
			}
			
		}
*/		
		reader.close();
//		pw.close();
	}

}
