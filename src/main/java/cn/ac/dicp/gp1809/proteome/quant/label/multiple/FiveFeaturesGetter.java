/* 
 ******************************************************************************
 * File: FiveFeaturesGetter.java * * * Created on 2012-7-9
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.label.PeptidePairGetter;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2012-7-9, 08:44:09
 */
public class FiveFeaturesGetter extends PeptidePairGetter {

	private final static double [] labels = new double []{56.0626, 60.087707, 64.112814, 68.132077,
		72.15134};
	
	private final static double [] labels2 = new double []{84.0939, 88.119007, 96.169221, 100.188484,
		108.22701};
	
	private final static double [] labels3 = new double []{112.1252, 116.150307, 128.225628, 132.244891,
		144.30268};
	
	private HashMap <Integer, Integer> isoTypeMap;
	
	/**
	 * @param peakFile
	 */
	public FiveFeaturesGetter(String peakFile, int mzxmlType) {
		super(peakFile, mzxmlType);
		// TODO Auto-generated constructor stub
		this.isoTypeMap = new HashMap <Integer, Integer>();
	}

	public void addPeptide(IPeptide peptide){
		
		if(!peptide.isTP())
			return;
		
		ModifiedPeptideSequence modPep = ModifiedPeptideSequence.parseSequence(peptide.getSequence());
		ArrayList <IModifSite> newmods = new ArrayList <IModifSite>();
		double mh = peptide.getMH();
		
		IModifSite[] mods = modPep.getModifications();
		if(mods==null)
			return;
		
		IModifSite[] newmodifs = newmods.toArray(new IModifSite[newmods.size()]);
		modPep.renewModifiedSequence(newmodifs);
		String uniseq = modPep.getSequence();
		
		int countN = 0;
		int countK = 0;
		double modmass = 0;
		for(int i=0;i<mods.length;i++){
			ModSite site = mods[i].modifiedAt();
			if(site.equals(ModSite.newInstance_aa('K'))){
				
				countK++;
				mh -= aamodif.getAddedMassForModif(mods[i].symbol());
				modmass += aamodif.getAddedMassForModif(mods[i].symbol());
				
			}else if(site.equals(ModSite.newInstance_PepNterm())){
				
				countN++;
				mh -= aamodif.getAddedMassForModif(mods[i].symbol());
				modmass += aamodif.getAddedMassForModif(mods[i].symbol());
				
			}else{
				newmods.add(mods[i]);
			}
		}
		
		if(countN!=1){
			return;
			
		}else{
			
			if(countK==0){
				this.idenPepMap.put(uniseq, peptide);
				return;
				
			}else if(countK>3){
				return;
			}
		}
		
		String noterm = PeptideUtil.getSequence(peptide.getSequence());
		String nok = noterm.replaceAll("K", "");
		if(noterm.length()-nok.length()!=countK){
			return;
		}

		int modType = -1;
		for(int i=0;i<labels.length;i++){
			if(Math.abs(labels[i]-modmass)<0.05){
				modType = i+1;
				break;
			}
		}
		
		if(modType==-1){
			for(int i=0;i<labels2.length;i++){
				if(Math.abs(labels2[i]-modmass)<0.05){
					modType = i+1;
					break;
				}
			}
		}
		
		if(modType==-1){
			for(int i=0;i<labels3.length;i++){
				if(Math.abs(labels3[i]-modmass)<0.05){
					modType = i+1;
					break;
				}
			}
		}
		
		this.isoTypeMap.put(peptide.getScanNumBeg(), modType);
		if(modType==-1){
			return;
		}
		
		peptide.setMH(mh);
		short charge = peptide.getCharge();
		float score = peptide.getPrimaryScore();
		
		peptide.setSequence(modPep.getFormattedSequence());
		String key = uniseq+"_"+peptide.getCharge();

		if(seqChargeMap.containsKey(uniseq)){
			seqChargeMap.get(uniseq).add(peptide.getCharge());
			if(score>pepMap.get(uniseq).getPrimaryScore()){
				pepMap.put(uniseq, peptide);
			}
		}else{
			HashSet <Short> chargeSet = new HashSet <Short>();
			chargeSet.add(peptide.getCharge());
			seqChargeMap.put(uniseq, chargeSet);
			pepMap.put(uniseq, peptide);
		}
		
		char [] cs = uniseq.toCharArray();
		Arrays.sort(cs);
		String key2 = new String (cs)+"_"+peptide.getCharge();
		
		if(massesMap.containsKey(key2)){
			
			seqScanMap.get(key2).add(peptide.getScanNumBeg());
			
		}else{
			
			double [] masses = new double [5];
			if(countK==1){
				for(int i=0;i<5;i++){
					masses[i] = (mh-AminoAcidProperty.PROTON_W + labels[i])/(double)charge + AminoAcidProperty.PROTON_W;
				}
			}else if(countK==2){
				for(int i=0;i<5;i++){
					masses[i] = (mh-AminoAcidProperty.PROTON_W + labels2[i])/(double)charge + AminoAcidProperty.PROTON_W;
				}
			}else if(countK==3){
				for(int i=0;i<5;i++){
					masses[i] = (mh-AminoAcidProperty.PROTON_W + labels3[i])/(double)charge + AminoAcidProperty.PROTON_W;
				}
			}
			
			massesMap.put(key2, masses);
			
			ArrayList <Integer> list = new ArrayList <Integer>();
			list.add(peptide.getScanNumBeg());
			seqScanMap.put(key2, list);
		}
	}
	
	public HashMap <String, PeptidePair> getPeptidPairs(){
		
		HashMap <String, PeptidePair> pairMap = new HashMap <String, PeptidePair> ();
		Iterator <String> it = seqChargeMap.keySet().iterator();
int count = 0;
		while(it.hasNext()){
			
			String uniseq = it.next();
			IPeptide peptide = this.pepMap.get(uniseq);

			HashSet <Short> chargeSet = seqChargeMap.get(uniseq);
			Iterator <Short> chargeIt = chargeSet.iterator();
			
			String noModSeq = PeptideUtil.getUniqueSequence(uniseq);
//			ipcOptions.addPeptide(noModSeq);
//			ipcOptions.setCharge(1);
			
			double [] intenMinusRatio = new double [6];
/*			int inteni = 0;
			Results res = ipc.execute(ipcOptions);
			TreeSet <ipc.Peak> isotopepeaks = res.getPeaks();
			Iterator <ipc.Peak> isotopepeaksit = isotopepeaks.iterator();
			while(isotopepeaksit.hasNext()){
				Peak pp = isotopepeaksit.next();
				intenMinusRatio[inteni++] = pp.getP();
				if(inteni==6)
					break;
			}
*/			
			char [] aas = uniseq.toCharArray();
			Arrays.sort(aas);
			
			while(chargeIt.hasNext()){
				
				Short charge = chargeIt.next();
				String key2 = new String (aas)+"_" + charge;
				String key = uniseq+"_"+charge;
				
				Integer [] scans = this.seqScanMap.get(key2).toArray(new Integer [seqScanMap.get(key2).size()]);
				Arrays.sort(scans);

				double [] masses = massesMap.get(key2);

//				Features feas = getter.getFeatures(pep, masses, intenMinusRatio, scans);
				LabelFeatures feas = getter.getFeatures(charge, masses, scans, masses);
				PeptidePair pair = new PeptidePair(peptide, feas);
				pair.setSrc(file);
//				if(feas.getLength()>5){
				if(MathTool.getTotal(pair.getTotalIntens())==0){
					this.idenPepMap.put(PeptideUtil.getUniqueSequence(peptide.getSequence()), peptide);
				}else{
					pairMap.put(key, pair);
				}
//				}else{
//					if(!pairMap.containsKey(key))
//						this.idenPepMap.put(PeptideUtil.getUniqueSequence(peptide.getSequence()), peptide);
//				}
			}
		}
		return pairMap;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
