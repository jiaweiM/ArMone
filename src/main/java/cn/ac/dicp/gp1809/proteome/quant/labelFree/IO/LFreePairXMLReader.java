/* 
 ******************************************************************************
 * File: LFreeMutilReader.java * * * Created on 2011-7-5
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeature;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeature;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.LFreePeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanPeptide;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.quant.rsc.AbstractPairXMLReader;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2011-7-5, 09:13:51
 */
public class LFreePairXMLReader extends AbstractPairXMLReader {

	public LFreePeptidePair [] freePairs;
	private int num;
	private String [] fileNames;
	private double [] tics;
	
	public LFreePairXMLReader(String file) throws DocumentException{
		this(new File(file));
	}
	
	public LFreePairXMLReader(File file) throws DocumentException{
		super(file);
	}
	
	/**
	 * 
	 */
	protected void getProfileData() {
		// TODO Auto-generated method stub
		
		this.setFile();
		this.setMods();
		
		double [] normal = this.getRatiosMedian();
		for(int i=0;i<freePairs.length;i++){
			freePairs[i].setNormalRatio(normal);
		}
	}

	private void setFile(){
		
		Iterator <Element> fileIt = root.elementIterator("File");
		ArrayList <String> nameslist = new ArrayList<String>();
		ArrayList <Double> ticslist = new ArrayList <Double>();
		
		while(fileIt.hasNext()){
			Element file = fileIt.next();
			String name = file.attributeValue("Name");
			Double tic = Double.parseDouble(file.attributeValue("TIC"));
			nameslist.add(name);
			ticslist.add(tic);
		}
		this.num = nameslist.size();
		this.fileNames = new String[num];
		this.tics = new double[num];
		
		short [] used = new short[num];
		for(int i=0;i<num;i++){
			fileNames[i] = nameslist.get(i);
			tics[i] = ticslist.get(i);
			used[i] = (short) (i+1);
		}
		
		this.type = LabelType.LabelFree;
		this.type.setUsed(used);
		this.type.setFileNames(fileNames);

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
		this.totalPairMedian = new double [ratioNames.length];
	}

	private LFreePeptidePair getFeasPair(){

		if(feasIt.hasNext()){
			
			Element eFeas = feasIt.next();
		
			String baseName = eFeas.attributeValue("BaseName");
			int scanBeg = Integer.parseInt(eFeas.attributeValue("ScanBeg"));
			int scanEnd = Integer.parseInt(eFeas.attributeValue("ScanEnd"));
			String seq = eFeas.attributeValue("Sequence");
			String ref = eFeas.attributeValue("Reference");
			
			String [] ratiostrs = eFeas.attributeValue("Ratios").split("_");
			double [] ratios = new double[ratiostrs.length];
			
			for(int i=0;i<ratios.length;i++){
				ratios[i] = Double.parseDouble(ratiostrs[i]);
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
			
			IPeptide pep = new QuanPeptide(seq, (short) 0, refset, baseName, scanBeg, scanEnd, locAroundMap);
			
			ArrayList <FreeFeatures> feas = new ArrayList <FreeFeatures>();
			ArrayList <String> srcs = new ArrayList <String>();
			Iterator <Element> itFeas = eFeas.nodeIterator();
			while(itFeas.hasNext()){
				Element efs = itFeas.next();
				double pepMr = Double.parseDouble(efs.attributeValue("pepMr"));
				String src = efs.attributeValue("file");
				srcs.add(src);

				FreeFeatures fs = new FreeFeatures();

				Iterator <Element> itF = efs.nodeIterator();
				while(itF.hasNext()){
					Element ef = itF.next();
					int scannum = Integer.parseInt(ef.attributeValue("scannum"));
					double rt = Double.parseDouble(ef.attributeValue("retention_time"));
					double [] intens = new double[3];
					intens[0] = Float.parseFloat(ef.attributeValue("intensity_1"));
					intens[1] = Float.parseFloat(ef.attributeValue("intensity_2"));
					intens[2] = Float.parseFloat(ef.attributeValue("intensity_3"));
					
					FreeFeature f = new FreeFeature(scannum, pepMr, rt, intens);
					fs.addFeature(f);
				}
				fs.setInfo();
				feas.add(fs);
			}
			
			FreeFeatures [] feaslist = feas.toArray(new FreeFeatures[feas.size()]);
			String [] srclist = srcs.toArray(new String[srcs.size()]);
			
			LFreePeptidePair pair = new LFreePeptidePair(pep, feaslist, srclist, ratios);
			return pair;
		}
		return null;
	
	}

	public void readAllPairs(){
		
		ArrayList <Double> [] ratiolist = new ArrayList [this.totalPairMedian.length];
		for(int i=0;i<ratiolist.length;i++){
			ratiolist[i] = new ArrayList <Double>();
		}
		
		ArrayList <LFreePeptidePair> pairList = new ArrayList <LFreePeptidePair>();
		LFreePeptidePair pair;
		while((pair = this.getFeasPair())!=null){
			pairList.add(pair);
			double [] ratio = pair.getSelectRatio();
			for(int i=0;i<ratio.length;i++){
				ratiolist[i].add(ratio[i]);
			}
		}
		
		for(int i=0;i<totalPairMedian.length;i++){
			totalPairMedian[i] = MathTool.getMedianInDouble(ratiolist[i]);
		}
		this.freePairs = pairList.toArray(new LFreePeptidePair[pairList.size()]);
	}
	
	public LFreePeptidePair getPair(int index){
		return freePairs[index];
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLReader#getAllSelectedPairs(int[], boolean)
	 */
	@Override
	public LFreePeptidePair[] getAllSelectedPairs(int[] index) {
		// TODO Auto-generated method stub
		ArrayList <LFreePeptidePair> pairlist = new ArrayList <LFreePeptidePair>();
		for(int i=0;i<index.length;i++){
			LFreePeptidePair pair = freePairs[index[i]];
			pairlist.add(pair);
		}
		return pairlist.toArray(new LFreePeptidePair[pairlist.size()]);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLReader#getAllSelectedPairs(boolean)
	 */
	@Override
	public LFreePeptidePair[] getAllSelectedPairs() {
		// TODO Auto-generated method stub
		return freePairs;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLReader#getAllSelectedPairs(int[], boolean, int[])
	 */
	@Override
	public LFreePeptidePair[] getAllSelectedPairs(int[] index, boolean normal,
			int[] outputRatio) {
		// TODO Auto-generated method stub
		ArrayList <LFreePeptidePair> pairlist = new ArrayList <LFreePeptidePair>();
		for(int i=0;i<index.length;i++){
			LFreePeptidePair pair = freePairs[index[i]];
			pair.setNormal(normal);
			pair.setNormalFactor(realNormalFactor);
			pair.setSelectRatio(outputRatio);
			pairlist.add(pair);
		}
		return pairlist.toArray(new LFreePeptidePair[pairlist.size()]);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLReader#getAllSelectedPairs(boolean, int[])
	 */
	@Override
	public LFreePeptidePair[] getAllSelectedPairs(boolean normal, int[] outputRatio) {
		// TODO Auto-generated method stub
		for(int i=0;i<freePairs.length;i++){
			freePairs[i].setNormal(normal);
			freePairs[i].setNormalFactor(realNormalFactor);
			freePairs[i].setSelectRatio(outputRatio);
		}
		return freePairs;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLReader#getAllResult(int[], boolean, boolean, boolean)
	 */
	@Override
	public QuanResult[] getAllResult(int[] index, boolean nomod, boolean normal, int [] outputRatio) throws Exception {
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
		
		HashMap <IPeptide, LFreePeptidePair> pepMap = new HashMap <IPeptide, LFreePeptidePair> ();
		for(int i=0;i<freePairs.length;i++){
			LFreePeptidePair pair = freePairs[i];
			IPeptide pep = pair.getPeptide();
			pros.addPeptide(pep);
			if(idxset.contains(i)){
				pepMap.put(pep, pair);
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
			
			ArrayList <LFreePeptidePair> pairlist = new ArrayList <LFreePeptidePair>();
			Protein pro = proArray[i];
			IPeptide [] peps = pro.getAllPeptides();
			for(int j=0;j<peps.length;j++){
				if(pepMap.containsKey(peps[j])){
					LFreePeptidePair pair = pepMap.get(peps[j]);
					pair.setDelegateRef(pro.getRefwithSmallestMw().getName());
					pairlist.add(pair);
				}
			}
			
			if(pairlist.size()==0)
				continue;

			boolean unique = proArray[i].getUnique();
			IReferenceDetail[] refs = proArray[i].getReferences();
			String [] refName = new String [refs.length];
			for(int j=0;j<refs.length;j++){
				refName[j] = refs[j].getName();
			}
			Arrays.sort(refName);

			/*LFreePeptidePair [] pairs = pairlist.toArray(new LFreePeptidePair[pairlist.size()]);
			QuanResult lqs = new QuanResult(refName, pairs, nomod);
			lqs.setUnique(unique);
			reslist.add(lqs);*/
		}
		return reslist.toArray(new QuanResult[reslist.size()]);
	}
	
	public QuanResult[] getAllResult(boolean nomod, boolean normal) throws Exception {
		// TODO Auto-generated method stub

		Proteins2 pros = new Proteins2(accesser);
		
		IPeptide idenPep;
		while((idenPep=this.getIdenPep())!=null){
			pros.addPeptide(idenPep);
		}
		
		HashMap <IPeptide, LFreePeptidePair> pepMap = new HashMap <IPeptide, LFreePeptidePair> ();
		for(int i=0;i<freePairs.length;i++){
			LFreePeptidePair pair = freePairs[i];
			pair.setNormal(normal);
			pair.setNormalFactor(realNormalFactor);
			IPeptide pep = pair.getPeptide();
			pros.addPeptide(pep);
			pepMap.put(pep, pair);
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
			
			ArrayList <LFreePeptidePair> pairlist = new ArrayList <LFreePeptidePair>();
			Protein pro = proArray[i];
			IPeptide [] peps = pro.getAllPeptides();
			for(int j=0;j<peps.length;j++){
				if(pepMap.containsKey(peps[j])){
					LFreePeptidePair pair = pepMap.get(peps[j]);
					pair.setDelegateRef(pro.getRefwithSmallestMw().getName());
					pairlist.add(pair);
				}
			}
			
			if(pairlist.size()==0)
				continue;

			boolean unique = proArray[i].getUnique();
			IReferenceDetail[] refs = proArray[i].getReferences();
			String [] refName = new String [refs.length];
			for(int j=0;j<refs.length;j++){
				refName[j] = refs[j].getName();
			}
			Arrays.sort(refName);

			/*PeptidePair [] pairs = pairlist.toArray(new PeptidePair[pairlist.size()]);
			QuanResult lqs = new QuanResult(refName, pairs, nomod);
			lqs.setUnique(unique);
			reslist.add(lqs);*/
		}
		return reslist.toArray(new QuanResult[reslist.size()]);
	}


	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLReader#getAllResult(boolean, boolean, int[])
	 */
	@Override
	public QuanResult[] getAllResult(boolean nomod, boolean normal,
			int[] outputRatio) throws Exception {
		// TODO Auto-generated method stub

		Proteins2 pros = new Proteins2(accesser);
		
		IPeptide idenPep;
		while((idenPep=this.getIdenPep())!=null){
			pros.addPeptide(idenPep);
		}
		
		HashMap <IPeptide, LFreePeptidePair> pepMap = new HashMap <IPeptide, LFreePeptidePair> ();
		for(int i=0;i<freePairs.length;i++){
			LFreePeptidePair pair = freePairs[i];
			pair.setSelectRatio(outputRatio);
			pair.setNormal(normal);
			pair.setNormalFactor(realNormalFactor);
			IPeptide pep = pair.getPeptide();
			pros.addPeptide(pep);
			pepMap.put(pep, pair);
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
			
			ArrayList <LFreePeptidePair> pairlist = new ArrayList <LFreePeptidePair>();
			Protein pro = proArray[i];
			IPeptide [] peps = pro.getAllPeptides();
			for(int j=0;j<peps.length;j++){
				if(pepMap.containsKey(peps[j])){
					LFreePeptidePair pair = pepMap.get(peps[j]);
					pair.setDelegateRef(pro.getRefwithSmallestMw().getName());
					pairlist.add(pair);
				}
			}
			
			if(pairlist.size()==0)
				continue;

			boolean unique = proArray[i].getUnique();
			IReferenceDetail[] refs = proArray[i].getReferences();
			String [] refName = new String [refs.length];
			for(int j=0;j<refs.length;j++){
				refName[j] = refs[j].getName();
			}
			Arrays.sort(refName);

			/*PeptidePair [] pairs = pairlist.toArray(new PeptidePair[pairlist.size()]);
			QuanResult lqs = new QuanResult(refName, pairs, nomod);
			lqs.setUnique(unique);
			reslist.add(lqs);*/
		}
		return reslist.toArray(new QuanResult[reslist.size()]);
	}

	public String [] getFileNames(){
		return fileNames;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLReader#getTitle()
	 */
	@Override
	public String[] getTitle() {
		// TODO Auto-generated method stub
		
		String [] titles;
		
		ArrayList <String> tList = new ArrayList<String>();
		tList.add("Selected");
		tList.add("Sequence");
		for(int i=0;i<ratioNames.length;i++){
			tList.add(ratioNames[i]);
		}
		for(int i=0;i<fileNames.length;i++){
			tList.add(fileNames[i]);
		}
		tList.add("Reference");
		titles = tList.toArray(new String [tList.size()]);
		
		return titles;
	}
	
	public int getPairNum() {
		// TODO Auto-generated method stub
		return freePairs.length;
	}

	public void close(){
		this.freePairs = null;
		this.mods = null;
		this.feasIt = null;
		this.root = null;
		System.gc();
	}
	
	/**
	 * @return
	 */
	public File getParentFile() {
		// TODO Auto-generated method stub
		return file.getParentFile();
	}

	/**
	 * @param args
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws DocumentException {
		// TODO Auto-generated method stub

		LFreePairXMLReader reader = new LFreePairXMLReader("H:\\quatification_data_standard\\test_label_free\\100.pxml");
		for(int i=0;i<reader.freePairs.length;i++){
			System.out.println(reader.freePairs[i]);
		}
		
	}

}
