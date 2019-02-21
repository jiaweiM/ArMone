/* 
 ******************************************************************************
 * File: SixFeaturesGetter.java * * * Created on 2012-6-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.PeptidePairGetter;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import ipc.IPC;
import ipc.Peak;
import ipc.IPC.Options;
import ipc.IPC.Results;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2012-6-14, 18:51:03
 */
public class SixFeaturesGetter extends PeptidePairGetter
{
	
	public final static double [] labelN = new double[]{28.0313, 32.0564, 36.0757};
	
	public final static double [][] labelK = new double[][]{{28.0313, 32.0564, 36.0757}, {32.0564, 36.0815, 40.1008}};

	/**
	 * @param peakFile
	 */
	public SixFeaturesGetter(String peakFile, int mzxmlType) {
		super(peakFile, mzxmlType);
		// TODO Auto-generated constructor stub
	}
	
	public void addPeptide(IPeptide peptide){
		
		if(!peptide.isTP())
			return;
		
		if(peptide.getSequence().contains("X"))
			return;
		
		boolean nTerm = true;
		boolean proNTerm = false;
		ModSite site = null;
		double modK = 0;
		double modN = 0;
		int countK = 0;
		int aaK = 0;
		
		StringBuilder sb = new StringBuilder();
		char[] pepChars = peptide.getSequence().toCharArray();
		for (int i = 0; i < pepChars.length; i++) {
			if ((pepChars[i] >= 'A' && pepChars[i] <= 'Z')) {
				sb.append(pepChars[i]);
				site = ModSite.newInstance_aa(pepChars[i]);
				if(pepChars[i] == 'K' && (i>=2 && i<pepChars.length-2)) aaK++;
			} else if (pepChars[i] == '.') {
				if (nTerm) {
					sb.append(pepChars[i]);
					if (proNTerm)
						site = ModSite.newInstance_ProNterm();
					else
						site = ModSite.newInstance_PepNterm();

					nTerm = false;
				} else {
					sb.append(pepChars[i]);
					site = ModSite.newInstance_PepCterm();
				}
			} else if (pepChars[i] == '-') {
				if (i == 0)
					proNTerm = true;

				sb.append(pepChars[i]);
			} else {
				float mass = (float) aamodif
						.getAddedMassForModif(pepChars[i]);

				if(site.getModifAt().equals("K")){
					countK++;
					if(modK==0){
						modK = mass;
					}else{
						if(modK!=mass){
							return;
						}
					}
				}else if(site.getModifAt().equals("_n")){
					modN = mass;
				}else {
					sb.append(pepChars[i]);
				}
			}
		}
		
		String uniseq = PeptideUtil.getSequence(sb.toString());

		if(aaK==0) {
			this.idenPepMap.put(uniseq, peptide);
			if (this.labelTypeMap.containsKey(uniseq)) {
				this.labelTypeMap.get(uniseq).add(-1);
			} else {
				HashSet<Integer> set = new HashSet<Integer>();
				set.add(-1);
				this.labelTypeMap.put(uniseq, set);
			}
			return;
		}

		if(aaK!=countK) return;
		int labelNid = -1;
		for(int i=0;i<labelN.length;i++){
			if(Math.abs(modN-labelN[i])<0.1){
				labelNid = i;
				break;
			}
		}
		if(labelNid == -1) return;
		
		int labelKid = -1;
		for(int i=0;i<labelK.length;i++){
			if(Math.abs(modK-labelK[i][labelNid])<0.1){
				labelKid = i;
				break;
			}
		}
		if(labelKid == -1) return;
		double modmass = modK*countK+modN;
		double mh = peptide.getMH()-modmass;

		peptide.setMH(mh);
		short charge = peptide.getCharge();
		float score = peptide.getPrimaryScore();
		
		int modtype = labelKid+labelNid*2;
		if (this.labelTypeMap.containsKey(uniseq)) {
			this.labelTypeMap.get(uniseq).add(modtype);
		} else {
			HashSet<Integer> set = new HashSet<Integer>();
			set.add(modtype);
			this.labelTypeMap.put(uniseq, set);
		}
		
		peptide.setSequence(sb.toString());

		if(seqChargeMap.containsKey(uniseq)){
			seqChargeMap.get(uniseq).add(peptide.getCharge());
			if(score>pepMap.get(uniseq).getPrimaryScore()){
				pepMap.put(uniseq, peptide);
			}
			seqScanMap.get(uniseq).add(peptide.getScanNumBeg());
		}else{
			HashSet <Short> chargeSet = new HashSet <Short>();
			chargeSet.add(peptide.getCharge());
			seqChargeMap.put(uniseq, chargeSet);
			pepMap.put(uniseq, peptide);
			
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(peptide.getScanNumBeg());
			seqScanMap.put(uniseq, list);
		}
		
		char [] cs = uniseq.toCharArray();
		Arrays.sort(cs);
		String key2 = new String (cs)+"_"+peptide.getCharge();
		
		scoreMap.put(peptide.getScanNumBeg(), (double)peptide.getPrimaryScore());
		
		if(massesMap.containsKey(key2)){

		}else{
			
			double [] masses = new double [6];
			for(int i=0;i<masses.length;i++){
				double addmass = labelN[i/2]+labelK[i%2][i/2]*countK;
				masses[i] = (mh-AminoAcidProperty.PROTON_W + addmass)/(double)charge + AminoAcidProperty.PROTON_W;
			}
			massesMap.put(key2, masses);
//			System.out.println(countK+"\t"+Arrays.toString(masses));
		}
	}
	
	public HashMap <String, PeptidePair> getPeptidPairs(){

		Iterator <String> pepit = this.pepMap.keySet().iterator();
		while(pepit.hasNext()){
			
			String key = pepit.next();
			String seq = PeptideUtil.getUniqueSequence(pepMap.get(key).getSequence());
			if(seq.contains("X"))
				continue;
			
			if(!isotopeMap.containsKey(seq)){
			
				IPC ipc = new IPC();
				Options ipcOptions = new Options();
				ipcOptions.addPeptide(seq);
				ipcOptions.setCharge(1);
				ipcOptions.setFastCalc(32);

				double [] intenMinusRatio = new double [6];
				int inteni = 0;
				Results res = ipc.execute(ipcOptions);
				TreeSet <ipc.Peak> isotopepeaks = res.getPeaks();
				Iterator <ipc.Peak> isotopepeaksit = isotopepeaks.iterator();
				while(isotopepeaksit.hasNext()){
					Peak pp = isotopepeaksit.next();
					intenMinusRatio[inteni++] = pp.getP();
					if(inteni==6)
						break;
				}
				isotopeMap.put(seq, intenMinusRatio);
			}
		}
		
		HashMap <String, PeptidePair> pairMap = new HashMap <String, PeptidePair> ();
		Iterator <String> it = seqChargeMap.keySet().iterator();

		while(it.hasNext()){
			
			String uniseq = it.next();
			IPeptide peptide = this.pepMap.get(uniseq);

			HashSet <Short> chargeSet = seqChargeMap.get(uniseq);
			Iterator <Short> chargeIt = chargeSet.iterator();

			char [] aas = uniseq.toCharArray();
			Arrays.sort(aas);
			
			while(chargeIt.hasNext()){
				
				Short charge = chargeIt.next();
				String key2 = new String (aas)+"_" + charge;
				String key = uniseq+"_"+charge;
				
				Integer [] scans = this.seqScanMap.get(uniseq).toArray(new Integer [seqScanMap.get(uniseq).size()]);
				Arrays.sort(scans);

				double [] scores = new double [scans.length];
				for(int i=0;i<scores.length;i++){
					scores[i] = this.scoreMap.get(scans[i]);
				}
				
				double [] masses = massesMap.get(key2);

				LabelFeatures feas = null;
				String noModSeq = PeptideUtil.getUniqueSequence(uniseq);
//if(noModSeq.contains("DTLQSELVGQLYK") && charge==2){				
				if(isotopeMap.containsKey(noModSeq)){
					feas = getter.getFeatures(charge, masses, scans, scores, isotopeMap.get(noModSeq));
				}else{
					feas = getter.getFeatures(charge, masses, scans, scores);
				}
				
				PeptidePair pair = new PeptidePair(peptide, feas);
				pair.setSrc(file);
//				if(feas.isValidate()){
				if(MathTool.getTotal(pair.getTotalIntens())==0){
					this.idenPepMap.put(uniseq, peptide);
				}else{
					pairMap.put(key, pair);
				}
//				}else{
//					if(!pairMap.containsKey(key))
//						this.idenPepMap.put(PeptideUtil.getUniqueSequence(peptide.getSequence()), peptide);
//				}
			}
//}
		}

		return pairMap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String ppl = "M:\\Data\\sixple\\turnover\\dat\\0_3_6" +
				"\\0_3_6_50_F001249.dat.ppl";
		
		String pix = "M:\\Data\\sixple\\turnover\\dat\\0_3_6_peak" +
				"\\20120805PT0_3_6_50mM.mzXML";
		
/*		PrintWriter pw = null;
		try {
			pw = new PrintWriter("H:\\WFJ_mutiple_label\\2D\\150mM.txt");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
*/		
		PeptideListReader pReader = null;		
		try {
			pReader = new PeptideListReader(ppl);
		} catch (FileDamageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SixFeaturesGetter getter = new SixFeaturesGetter(pix, 0);
		getter.setModif(pReader.getSearchParameter().getVariableInfo());
		
		IPeptide pep = null;
		while((pep=pReader.getPeptide())!=null){
			if(pep.getScanNumBeg()==5003)
				getter.addPeptide(pep);
		}
		pReader.close();
		
		HashMap<String, PeptidePair> map = getter.getPeptidPairs();
		Iterator <String> it = map.keySet().iterator();
		while(it.hasNext()){
			
		}
/*
		try {

			MutilLabelPairXMLReader pairReader = new MutilLabelPairXMLReader("H:\\WFJ_mutiple_label\\2D\\20120531Mix1_150mM.pxml");
			pairReader.readAllPairs();
			PeptidePair [] pairs = pairReader.getAllSelectedPairs();
			for(int i=0;i<pairs.length;i++){
				if(pairs[i].getFindFeasNum()==6){
					int scannum = pairs[i].getPeptide().getScanNumBeg();
					System.out.println(scannum+"\t"+map.get(scannum));
				}
			}
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
	}

}
