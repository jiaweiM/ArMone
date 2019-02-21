/* 
 ******************************************************************************
 * File:QModCalculator.java * * * Created on 2010-7-12
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelQuanUnit;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFreePairXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.PTMUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * @author ck
 *
 * @version 2010-7-12, 20:45:06
 */
public class QModCalculator {

	private HashMap <String, HashSet <LabelQuanUnit>> feasMap;
	private boolean relaProRatio;
	private HashMap <String, double[]> ratioMap;
	private HashMap <String, double[]> rsdMap;
	private ModInfo [] mods;
	private HashSet <ModifLabelPair> mpset;

	public QModCalculator(ModInfo [] mods){
		this.relaProRatio = false;
		this.mods = mods;
		this.feasMap = new HashMap <String, HashSet <LabelQuanUnit>>();
		this.mpset = new HashSet <ModifLabelPair>();
	}
	
	public QModCalculator(ModInfo [] mods, String relaProRatio, LabelType type,
			boolean noModPep, boolean normal, double [] theoryRatio, int [] outputRatio){
		
		this.relaProRatio = true;
		this.mods = mods;
		this.feasMap = new HashMap <String, HashSet <LabelQuanUnit>>();
		this.mpset = new HashSet <ModifLabelPair>();
		this.ratioMap = new HashMap <String, double[]> ();
		this.rsdMap = new HashMap <String, double[]> ();

		this.initial(relaProRatio, type, noModPep, normal, theoryRatio, outputRatio);
	}
	
	private void initial(String relaProRatio, LabelType type, 
			boolean noModPep, boolean normal, double [] theoryRatio, int [] outputRatio){
		
		QuanResult [] results = null;
		
		if(type==LabelType.LabelFree){
			
			try {
				
				LFreePairXMLReader reader = new LFreePairXMLReader(relaProRatio);
				results = reader.getAllResult(noModPep, normal);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{

			try {
				
				LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(relaProRatio);
				reader.setTheoryRatio(theoryRatio);
				results = reader.getAllResult(noModPep, normal, outputRatio);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<results.length;i++){
			String [] refs = results[i].getRefs();
			double [] proRatio = results[i].getRatio();
			double [] rsd = results[i].getRSD();
			for(int j=0;j<refs.length;j++){
				this.ratioMap.put(refs[j], proRatio);
				this.rsdMap.put(refs[j], rsd);
			}
		}
	}

	/**
	 * For a protein group, one protein reference is corresponding to a {@link QModsResult}, so
	 * use this method can get a result list.
	 * 
	 * @param result
	 * @param mods
	 * @return
	 */
	public QModsResult [] calculte(QuanResult result) {
		
		ArrayList <QModsResult> resList = new ArrayList <QModsResult> ();
		String [] refs = result.getRefs();
		boolean isUnique = result.getUnique();
		boolean isGroup = refs.length>1;
		LabelQuanUnit [] feas = result.getUnits();

		if(relaProRatio){
			
			for(int i=0;i<refs.length;i++){
				if(this.ratioMap.containsKey(refs[i])){
					double [] proRatios = ratioMap.get(refs[i]);
					double [] rsd = rsdMap.get(refs[i]);
					QModsResult qs = this.getQModsResult(refs[i], feas, proRatios, rsd, isUnique, isGroup);
					if(qs!=null){
						resList.add(qs);
					}
				}
			}
			
		}else{
			for(int i=0;i<refs.length;i++){
				double [] proRatios = result.getRatio();
				double [] rsd = result.getRSD();
				QModsResult qs = this.getQModsResult(refs[i], feas, proRatios, rsd, isUnique, isGroup);
				if(qs!=null){
					resList.add(qs);
				}
			}
		}
		
		QModsResult [] resArray = resList.toArray(new QModsResult[resList.size()]);
		if(resArray.length>0)
			return resArray;	
		else
			return null;
	}
	
	public QModsResult getQModsResult(String ref, LabelQuanUnit [] pairs, double [] proRatios, 
			double [] rsd, boolean isUnique, boolean isGroup) {

		for(int i=0;i<proRatios.length;i++){
			if(proRatios[i]==0){
				return null;
			}
		}

		ArrayList <QModResult> modList = new ArrayList <QModResult>();
		
		for(int j=0;j<mods.length;j++){
			
			QModResult mResult = new QModResult(mods[j]);
			ModSite m = mods[j].getModSite();

			for(int l=0;l<pairs.length;l++){

				short modCount = 0;
				IPeptide pep = pairs[l].getPeptide();
				HashMap <String, SeqLocAround> seqLocMap = pep.getPepLocAroundMap();
				SeqLocAround sla = null;
				Iterator <String> it = seqLocMap.keySet().iterator();
				while(it.hasNext()){
					String key = it.next();
					String refName = ProteinReference.parseProReference(key).getName();
//					if(ref.startsWith(refName)){
					if(ref.contains(refName)){
						sla = seqLocMap.get(key);
					}
//					System.out.println((sla==null)+"\t"+ref+"\t"+key+"\t"+refName);
				}

				if(sla==null)
					continue;

				int beg = sla.getBeg();
				String uniseq = PeptideUtil.getUniqueSequence(pairs[l].getSequence());
				String pre = sla.getPre();
				String next = sla.getNext();
				String fullSeq = pre + uniseq + next;
				double [] ratio = pairs[l].getRatio();

				IModifSite [] modifSites = PTMUtil.getModifSites(pairs[l].getSequence());
				if(modifSites==null) continue;

				for(int k=0;k<modifSites.length;k++){
					ModSite m1 = modifSites[k].modifiedAt();	
					if(m1.equals(m)){
						
						int loc = modifSites[k].modifLocation()+beg-1;
						String site = m1.getModifAt()+loc;
						
						int aaroundBeg = modifSites[k].modifLocation()+pre.length()-8>0 ? modifSites[k].modifLocation()+pre.length()-8 : 0;
						int aaroundEnd = modifSites[k].modifLocation()+pre.length()+7<fullSeq.length() ? 
								modifSites[k].modifLocation()+pre.length()+7 : fullSeq.length();
								
						String aaround = fullSeq.substring(aaroundBeg, aaroundEnd);

						ModifLabelPair mPair = new ModifLabelPair(site, aaround, ratio, pairs[l].getSequence());
						mPair.setProRatio(proRatios);
						mResult.addModPair(mPair);
						modCount++;
						this.mpset.add(mPair);
					}
				}
				if(modCount>0){
					pairs[l].setModCount(modCount);
					String seq = pairs[l].getSequence();
					if(this.feasMap.containsKey(seq)){
						feasMap.get(seq).add(pairs[l]);
					}else{
						HashSet <LabelQuanUnit> pairset = new HashSet <LabelQuanUnit>();
						pairset.add(pairs[l]);
						feasMap.put(seq, pairset);
					}
				}
			}
			if(mResult.validata()){
				modList.add(mResult);
			}
		}
		
		if(modList.size()>0){
			QModResult [] modArrays = modList.toArray(new QModResult[modList.size()]);
			QModsResult modsResult = new QModsResult(ref, modArrays, proRatios, rsd);
			modsResult.setUnique(isUnique);
			modsResult.setGroup(isGroup);
			return modsResult;
			
		}else{
			return null;
		}
	}

	public HashMap <String, HashSet <LabelQuanUnit>> getTotalPair(){
		return this.feasMap;
	}
	
	public HashSet <ModifLabelPair> getTotalModSite(){
		return this.mpset;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
