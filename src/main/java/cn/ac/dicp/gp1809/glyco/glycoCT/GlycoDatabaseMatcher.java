/* 
 ******************************************************************************
 * File: GlycoDatabaseMatcher.java * * * Created on 2012-4-18
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.glycoCT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.glyco.structure.NGlycoConstructor;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.math.RandomPCalor;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.RegionTopNIntensityFilter;

/**
 * @author ck
 *
 * @version 2012-4-18, 13:55:42
 */
public class GlycoDatabaseMatcher {
	
	private static String glycoDir = "/resources/N_GlycoCT2.ID.txt";
	private static String massesDir = "/resources/N_Mass2.Info.txt";
	
	private double [] monoMasses;
	
	private MassUnit [] massUnits;
	
	private GlycoTree [][] glycoUnits;
	
	private double ppm = 50.0;
	
	private RegionTopNIntensityFilter filter;

	/**
	 * 
	 */
	private int size = 1643;
	
	/**
	 * nbt.1511-S1, p9
	 */
	protected static final double dm = 1.00286864;
	
	
	public GlycoDatabaseMatcher() throws IOException{
		
		this.initialMasses();
		this.initialGlyco();
		this.filter = new RegionTopNIntensityFilter(10, 100);
	}
	
	public GlycoDatabaseMatcher(double ppm) throws IOException{
		
		this.initialMasses();
		this.initialGlyco();
		this.ppm = ppm;
		this.filter = new RegionTopNIntensityFilter(10, 100);
	}
	
	private void initialMasses() throws IOException{
		
		File file = new File(System.getProperty("user.dir") + massesDir);
		BufferedReader masRreader = new BufferedReader(new FileReader(file));

		this.monoMasses = new double [size];
		this.massUnits = new MassUnit [size];
		this.glycoUnits = new GlycoTree [size][];
		
		double [][] peaks = null;
//		double [][] revpeaks = null;
		int id = 0;
		double mono = 0;
		double avg = 0;
		int[] composition = null;
		int num = 0;
		int idx = 0;

		String line = null;
		while((line=masRreader.readLine())!=null){
			
			if(line.startsWith("ID")){
				
				if(mono!=0){
//					MassUnit unit = new MassUnit(id, mono, avg, peaks, revpeaks);
					MassUnit unit = new MassUnit(id, mono, avg, peaks);
					monoMasses[id-1] = mono;
					massUnits[id-1] = unit;
					glycoUnits[id-1] = new GlycoTree[peaks.length];
				}
				
				String[] ss = line.split("\t");

				id = Integer.parseInt(ss[1]);
				mono = Double.parseDouble(ss[2]);
				avg = Double.parseDouble(ss[3]);
				num = Integer.parseInt(ss[4]);
				peaks = new double[num][];
//				revpeaks = new double[num][];
				composition = new int[ss.length - 5];
				for (int i = 0; i < composition.length; i++) {
					composition[i] = Integer.parseInt(ss[i + 5]);
				}
				idx = 0;

			}else{
				
				String [] ss = line.split("\t");
//				String [] revss = masRreader.readLine().split("\t");
				peaks[idx] = new double [ss.length];
//				revpeaks[idx] = new double [ss.length];
				for(int i=0;i<ss.length;i++){
					peaks[idx][i] = Double.parseDouble(ss[i]);
//					revpeaks[idx][i] = Double.parseDouble(revss[i]);
				}
				idx++;
			}
		}
		
//		MassUnit unit = new MassUnit(id, mono, avg, peaks, revpeaks);
		MassUnit unit = new MassUnit(id, mono, avg, peaks);
		monoMasses[id-1] = mono;
		massUnits[id-1] = unit;
		glycoUnits[id-1] = new GlycoTree[peaks.length];

		int ccc = 0;
		int ccc2 = 0;
		for(int i=0;i<glycoUnits.length;i++){
			ccc2+=glycoUnits[i].length;
			if(glycoUnits[i].length>1) ccc++;
		}
//		System.out.println(glycoUnits.length+"\t"+ccc+"\t"+ccc2);
		masRreader.close();
	}
	
	private void initialGlyco() throws IOException{
		
//		BufferedReader glycoReader = new BufferedReader(new FileReader(glycoDir));
//		File file = new File(GlycoDatabaseMatcher.class.getResource(glycoDir).getFile());
		File file = new File(System.getProperty("user.dir") + glycoDir);
		BufferedReader glycoReader = new BufferedReader(new FileReader(file));
		
		boolean res = false;
		boolean lin = false;

		GlycoTree gtree = null;
		StringBuilder treeBuilder = new StringBuilder();

		String name = "";
		
		String line = null;
		
		while((line=glycoReader.readLine())!=null){

			if (line.startsWith("ID")) {

				String[] ss = line.split("\t");
				int massid = Integer.parseInt(ss[1]);
				int fragid = Integer.parseInt(ss[2]);

				MassUnit mu = this.massUnits[massid - 1];
				double[] fragments = mu.getFragments()[fragid - 1];

				gtree.setMonoMass(mu.getMono());
				gtree.setAveMass(mu.getAverage());
				gtree.setIupacName(name);
				gtree.setFragments(fragments);
				gtree.setGlycoCT(treeBuilder.toString());
				gtree.parseInfo();

				if(this.glycoUnits[massid-1][fragid-1]==null)
					this.glycoUnits[massid-1][fragid-1] = gtree;


			} else if (line.startsWith("IUPAC")) {

				name = line.split("\t")[1];

			} else if (line.startsWith("RES")) {

				gtree = new GlycoTree();
				treeBuilder = new StringBuilder();
				res = true;

				treeBuilder.append(line).append("\n");

			} else if (line.startsWith("LIN")) {

				lin = true;
				res = false;

				treeBuilder.append(line).append("\n");

			} else {

				treeBuilder.append(line).append("\n");

				if (res) {

					int beg = line.indexOf(":");

					String id = line.substring(0, beg - 1);
					String typejudeg = line.substring(0, beg);
					String content = line.substring(beg + 1);

					if (typejudeg.endsWith("b")) {

						GlycoTreeNode node = new GlycoTreeNode(id, content);
						gtree.addNode(id, node);

					} else if (typejudeg.endsWith("s")) {

						gtree.addSub(id, content);

					} else {

						glycoReader.close();
						return;
					}

				} else if (lin) {

					String[] ss = line.split("[:()+]");
					String parentid = ss[1].substring(0, ss[1].length() - 1);
					String childid = ss[4].substring(0, ss[4].length() - 1);
					char parentLinkType = ss[1].charAt(ss[1].length() - 1);
					char childLinkType = ss[4].charAt(ss[3].length() - 1);
					String linkPosition1 = ss[2];
					String linkPosition2 = ss[3];

					gtree.addLink(parentid, childid, parentLinkType,
							childLinkType, linkPosition1, linkPosition2);
				}
			}
		}
		
		glycoReader.close();
	}
	
	/**
	 * @param double1
	 * @param preMz
	 * @param preCharge
	 * @param scannum
	 * @param peaks
	 * @param integer
	 * @return
	 */
	public NGlycoSSM[] match(Double double1, float preMz, short preCharge,
			Integer scannum, IPeak[] peaks, Integer integer) {
		return null;
	}

	public NGlycoSSM [] match(NGlycoConstructor gc, int isotope, double neuAcScore){

		int preCharge = gc.getPreCharge();
		int scannum = gc.getScanNum();
		double preMz = gc.getPreMz();
		IPeak [] allpeaks = gc.getMS2PeakList();
		if(allpeaks.length==0)
			return null;
		
//		IPeak [] peaks = this.filter.filter(allpeaks);
		IPeak [] peaks = allpeaks;
		
		ArrayList <NGlycoSSM> list = new ArrayList <NGlycoSSM>();
		double preMass = (preMz-AminoAcidProperty.PROTON_W)* (double) preCharge;

		double [] pepMasses = gc.getPossiblePepMasses();
		Integer [][] chargeList = gc.getChargeList();
//System.out.println("matcher289\t"+Arrays.toString(pepMasses)+"\t"+preMz);		
		if(pepMasses==null)
			return null;

		for(int i=0;i<pepMasses.length;i++){
			
			double [] glycoMasses = new double[isotope+1];
			double [] preMasses = new double[isotope+1];
			for(int j=0;j<glycoMasses.length;j++){
				glycoMasses[j] = preMass - pepMasses[i] -(isotope-j)*dm;
				preMasses[j] = preMz -(isotope-j)*dm/(double)preCharge;
			}

//System.out.println("Matcher466\t"+isotope+"\t"+pepMasses[i]+"\t"+ppm+"\t"+Arrays.toString(preMasses)+"\n"+Arrays.toString(glycoMasses));
			double beg = glycoMasses[0] - glycoMasses[0]*ppm*1E-6 - dm*2;
			int id = Arrays.binarySearch(monoMasses, beg);

			if(id<0){
				id = -id-1;
			}

			for(int j=id;j<monoMasses.length;j++){

				for(int k=0;k<glycoMasses.length;k++){
					
					int isotopeDistance = glycoMasses.length-k-1;
					double addppm = ppm+isotopeDistance*10.0>60 ? 60 : ppm+isotopeDistance*10.0;
					double tolerance =  glycoMasses[0]*1.0E-6*addppm;
					
					if(Math.abs(monoMasses[j]-glycoMasses[k]) < tolerance){

						NGlycoSSM ssm = this.matchSSM(massUnits[j], pepMasses[i], chargeList[i], scannum, preCharge, preMasses[k], peaks);
						if(ssm==null) continue;
						ssm.setIsotope(k);
						
//System.out.println("matcher481\t"+monoMasses[j]+"\t"+glycoMasses[k]+"\t"+preMasses[k]+"\t"+pepMasses[i]+"\t"+(ssm==null)+"\t"+ssm.getGlycoTree().isMammal()
//		+"\t"+(isotope-k)+"\t"+ssm.getName()+"\t"+isotope+"\t"+k);

						if(ssm.getGlycoTree().isMammal()){
							
//System.out.println("321\t"+monoMasses[j]+"\t"+glycoMasses[k]+"\t"+preMasses[k]+"\t"+ssm.getScore()+"\t"+neuAcScore+"\t");

							if(neuAcScore>0){
								if(neuAcScore>2.2 && !ssm.getGlycoTree().hasNeuAc()){
									continue;
								}else{
									if(ssm.getGlycoTree().hasNeuAc()){
										ssm.setNeuAcScore(ssm.getScore()*neuAcScore);
									}else{
										ssm.setNeuAcScore(ssm.getScore()/neuAcScore);
									}
								}
							}else{
								if(ssm.getGlycoTree().hasNeuAc()){
//									ssm.setNeuAcScore(ssm.getScore()/2.0);
									continue;
								}else{
									ssm.setNeuAcScore(ssm.getScore());
								}
							}
							list.add(ssm);
						}
						
					}else if(glycoMasses[k]-monoMasses[j] > tolerance){
						break;
					}
				}

				if(monoMasses[j]-glycoMasses[glycoMasses.length-1]-dm*2 > dm){
					break;
				}				
			}
		}

		if(list.size()>0){
			
			NGlycoSSM [] ssms = list.toArray(new NGlycoSSM[list.size()]);
			Arrays.sort(ssms, new Comparator<NGlycoSSM>(){

				@Override
				public int compare(NGlycoSSM arg0, NGlycoSSM arg1) {
					// TODO Auto-generated method stub
					
					double s1 = arg0.getNeuAcScore()*Math.pow(0.8, (double)arg0.getIsotope());
					double s2 = arg1.getNeuAcScore()*Math.pow(0.8, (double)arg1.getIsotope());
					if(s1>s2){
						return -1;
					}else if(s1<s2){
						return 1;
					}else{
						return 0;
					}
				}
			});
			
			for(int i=0;i<ssms.length;i++){
//System.out.println("matcher308\t"+ssms[i].getGlycoTree().getIupacName());
				ssms[i].setRank(i+1);
			}
//			System.out.println(ssms[0].isTarget()+"\t"+ssms[0].getSocre());
			
			return ssms;
			
		}else{
			return null;
		}
	}

	private NGlycoSSM matchSSM(MassUnit mu, double possPepMass, Integer [] chargeList, int scannum, int preCharge, 
			double preMz, IPeak [] peaks){
		
		if(peaks.length==0)
			return null;
		
		double beginmz = peaks[0].getMz();
		ArrayList <IPeak> alllist = new ArrayList <IPeak>();
		ArrayList <IPeak> templist = new ArrayList <IPeak>();
				
		for(int i=0;i<peaks.length;i++){
			
			double mzi = peaks[i].getMz();
			
			if(mzi>beginmz+100){

				IPeak [] temppeaks = templist.toArray(new IPeak[templist.size()]);
				Arrays.sort(temppeaks, new Comparator<IPeak>(){

					@Override
					public int compare(IPeak arg0, IPeak arg1) {
						// TODO Auto-generated method stub
						if(arg0.getIntensity()<arg1.getIntensity()){
							return 1;
						}else if(arg0.getIntensity()>arg1.getIntensity()){
							return -1;
						}
						return 0;
					}
				});
				
				ArrayList<double[]> rangelist = new ArrayList<double[]>();
				for(int j=0;j<temppeaks.length;j++){
					double mzj = temppeaks[j].getMz();
					boolean haveIsotope = false;
					for(int k=0;k<rangelist.size();k++){
						double[] rangek = rangelist.get(k);
						if(mzj>=rangek[0] && mzj<=rangek[1]){
							haveIsotope = true;
							break;
						}
					}
					if(haveIsotope){
						alllist.add(temppeaks[j]);
					}else{
						if(rangelist.size()<4){
							double[] rangek = new double[]{mzj-2.0, mzj+2.0};
							rangelist.add(rangek);
							alllist.add(temppeaks[j]);
						}
					}
				}

				templist = new ArrayList <IPeak>();
				beginmz = mzi;
				templist.add(peaks[i]);
				
			}else{
				templist.add(peaks[i]);
			}
		}
		
		IPeak [] finalpeaks = alllist.toArray(new IPeak [alllist.size()]);
		Arrays.sort(finalpeaks);

//		HashMap <Double, Double> intenmap = new HashMap <Double, Double>();
		ArrayList <Double> mslist = new ArrayList <Double>(finalpeaks.length*chargeList.length);
		HashMap <Double, Integer> peakMzMap = new HashMap <Double, Integer>(peaks.length*chargeList.length);
		
		for(int i=0;i<finalpeaks.length;i++){
			
			double mz = finalpeaks[i].getMz();
			
			for(int j=0;j<chargeList.length;j++){
				
				double mass = (mz-AminoAcidProperty.PROTON_W)* (double)chargeList[j];
				mslist.add(mass);
				peakMzMap.put(mass, i);
//				System.out.println(mz+"\t"+mass+"\t"+chargeList[j]);
			}
		}
		
		Double [] msarrays = mslist.toArray(new Double[mslist.size()]);
		Arrays.sort(msarrays);
//System.out.println("GlycoDatabaseMatcher686\t"+Arrays.toString(msarrays)+"\t");
		double [][] theoryGlycoPeaks = mu.getFragments();
//		double [][] revGlycoPeaks = mu.getRevFragments();

		double [] scores = new double [theoryGlycoPeaks.length];
		int [] matchCount = new int [theoryGlycoPeaks.length];
//		double [] revScores = new double [revGlycoPeaks.length];

		HashSet <Integer> [] sets = new HashSet [theoryGlycoPeaks.length];
		Arrays.fill(scores, 0);
		
		for(int i=0;i<theoryGlycoPeaks.length;i++){
			
			sets[i] = new HashSet <Integer>();
			HashSet <Integer> usedset = new HashSet <Integer>();
			HashSet <Integer> countset = new HashSet<Integer>();
//System.out.println(i+"\t~~~~~~~~~~~~~~\t"+possPepMass+"\t"+Arrays.toString(theoryGlycoPeaks[i]));
			for(int j=0;j<theoryGlycoPeaks[i].length;j++){
				
				double gp = possPepMass + theoryGlycoPeaks[i][j];
//System.out.print(gp+"\t");				
				double beg = gp - gp*ppm*1E-6;
				int id = Arrays.binarySearch(msarrays, beg);

				if(id<0){
					id = -id-1;
				}
				
				for(int k=id;k<msarrays.length;k++){

					if(usedset.contains(k))
						continue;

					if(Math.abs(msarrays[k]-gp)<gp*ppm*1E-6){

						usedset.add(k);
						sets[i].add(peakMzMap.get(msarrays[k]));
						countset.add(j);
						
//						System.out.println("GlycoDatabaseMatcher707\t"+k+"\t"+msarrays[k]+"\t"+gp+"\t"+Math.abs(msarrays[k]-gp)+"\t"+gp*ppm*1E-6
//								+"\t"+finalpeaks[peakMzMap.get(msarrays[k])].getMz()+"\t"+j+"\t"+countset.size()+"\t"+i);	
					}
					
					if(Math.abs(msarrays[k]-gp-dm)<gp*ppm*1E-6){

						usedset.add(k);
						sets[i].add(peakMzMap.get(msarrays[k]));
						countset.add(j);
						
//						System.out.println("GlycoDatabaseMatcher720\t"+k+"\t"+msarrays[k]+"\t"+gp+"\t"+Math.abs(msarrays[k]-gp)+"\t"+gp*ppm*1E-6
//								+"\t"+finalpeaks[peakMzMap.get(msarrays[k])].getMz()+"\t"+j+"\t"+countset.size()+"\t"+i);
					}
					
					if(msarrays[k]-gp-dm>gp*ppm*1E-6){
						break;
					}
				}
			}
//System.out.println();			
//			int count = (int) doubleCount;
//			if(count>glycoPeaks[i].length) count = glycoPeaks[i].length;
//			scores[i] = (double)(scores[i]/(double)glycoPeaks[i].length);
//			scores[i] = RandomPCalor.getScore(RandomPCalor.getProbility(glycoPeaks[i].length, count, 
//				this.filter.singleP()));
//			scores[i] = -1.0 * Math.log10(RandomPCalor.getProbility(glycoPeaks[i].length, count, 2.0E-4));
/*			for(int j=0;j<4;j++){
				double ss = -1.0 * Math.log10(RandomPCalor.getProbility(glycoPeaks[i].length, countset[j].size(), 4.0E-4/(double)(j+1)));
				scores[i] += ss;
			}
			scores[i] = scores[i]/4.0;
*/			
			
			if(countset.size()<2) continue;
			
			matchCount[i] = countset.size();
			scores[i] =  -1.0 * Math.log10(RandomPCalor.getProbility(theoryGlycoPeaks[i].length, countset.size(), 4.0E-4/4.0));
//System.out.println("GlycoDatabaseMatcher647\t"+i+"\t"+possPepMass+"\t"+mu.getMono()+"\t"+scannum+"\t"+scores[i]+"\t"
//			+theoryGlycoPeaks[i].length+"\t"+countset.size()+"\t"+this.glycoUnits[mu.getId()-1][i].getIupacName());			
		}
		
/*		for(int i=0;i<revGlycoPeaks.length;i++){
			
			int count = 0;
			HashSet <Integer> usedset = new HashSet <Integer>();
			sets[i] = new HashSet <Integer>();
			
			for(int j=0;j<revGlycoPeaks[i].length;j++){
				
				double gp = possPepMass + revGlycoPeaks[i][j];
				double beg = gp - gp*ppm*1E-6;
				int id = Arrays.binarySearch(msarrays, beg);

				if(id<0){
					id = -id-1;
				}
				
				for(int k=id;k<msarrays.length;k++){
					
					if(usedset.contains(id))
						continue;
					
					if(msarrays[k]-gp<gp*ppm*1E-6){
						
						sets[i].add(peakMzMap.get(msarrays[k]));
						count++;
						break;
					}
					
					if(msarrays[k]-gp-dm<gp*ppm*1E-6){
						
						sets[i].add(peakMzMap.get(msarrays[k]));
						count++;
						break;
					}
					
					if(msarrays[k]-gp-dm>gp*ppm*1E-6){
						
//						scores[i] -= 0.5;
						break;
					}
				}
			}
			
//			scores[i] = (double)(scores[i]/(double)glycoPeaks[i].length);
			revScores[i] = RandomPCalor.getScore(RandomPCalor.getProbility(revGlycoPeaks[i].length, count, 
					this.filter.singleP()));
		}
*/		
//System.out.println(Arrays.toString(scores)+"\t"+Arrays.toString(revScores));
		double score = 0;
		double firstMatchCount = 0;
		int matchId = -1;
		for(int i=0;i<matchCount.length;i++){
			if(matchCount[i]==0) continue;
			if(matchCount[i]>firstMatchCount){
				score = scores[i];
				firstMatchCount = matchCount[i];
				matchId = i;
			}else if(matchCount[i]==firstMatchCount){
				GlycoTree treei = this.glycoUnits[mu.getId()-1][i];
				GlycoTree treej = this.glycoUnits[mu.getId()-1][matchId];
				if(treei.getNumOfFuc()>0 && treej.getNumOfFuc()>0){
					if(treei.hasCoreFuc()){
						if(treej.hasCoreFuc()){
							if(scores[i]>score){
								score = scores[i];
								matchId = i;
							}
						}else{
							score = scores[i];
							matchId = i;
						}
					}else{
						if(!treej.hasCoreFuc()){
							if(scores[i]>score){
								score = scores[i];
								matchId = i;
							}
						}
					}
				}else{
					if(scores[i]>score){
						score = scores[i];
						matchId = i;
					}
				}
			}
		}

		boolean target = true;
/*		for(int i=0;i<revScores.length;i++){
			
			if(revScores[i]>score){
				score = revScores[i];
				matchId = i;
				target = false;
			}
		}
*/		

		if(matchId == -1 || firstMatchCount==0)
			return null;
//		if(firstMatchCount<=2)
//		System.out.println(scannum+"\t"+possPepMass+"\t"+firstMatchCount+"\t"+score+"\t"+sets[matchId]);

		mu.setMatchPeakId(matchId);
		int secondMatchCount = 0;
		for(int i=0;i<matchCount.length;i++){
			if(matchId!=i){
				if(secondMatchCount<matchCount[i]){
					secondMatchCount = matchCount[i];
				}
			}
		}
		double deltaScore = ((double)firstMatchCount-(double)secondMatchCount)/(double)firstMatchCount;
/*		
		System.out.print("peak\t");
		for(Integer peakid : sets[matchId]){
			System.out.print(finalpeaks[peakid]+"\t");
		}
		System.out.println();
*/		
		NGlycoSSM ssm = new NGlycoSSM(scannum, preCharge, preMz, possPepMass, finalpeaks, sets[matchId],
				this.glycoUnits[mu.getId()-1][mu.getMatchPeakId()], score, deltaScore);
//System.out.println("matcher658\t"+ssm.getGlycoTree().getIupacName()+"\t"+score);
		ssm.setGlycanid(new int[]{mu.getId()-1, mu.getMatchPeakId()});
		ssm.setTarget(target);
		
		return ssm;
	}
	
	public double calculateProbability(double possPepMass, double [] glycoIons, IPeak [] peaks, int charge){

		int trial = 0;
		if (glycoIons == null || glycoIons.length == 0) {
			return 1.0;
		}

		int len = peaks.length;

		int matchedcount = 0;
		trial = glycoIons.length;


		/*
		 * If the charge state of the precursor ion is 1+ or 2+, only 1+
		 * fragment is used for identification. Otherwise, fragment with charge
		 * state from 1+ to charge-1 is used.
		 */
		int iz = charge <= 1 ? 2 : charge;

		/*
		 * Only the fragment of peptide is considered as a trial (without
		 * considerison of charge isomers). That is, whenever one of b7 fragment
		 * with whatever charge state (b7+ b7++ or b7+++), b7 will be considered
		 * as match with the match count of 1;
		 */
		L1: for (int i = 0; i < glycoIons.length; i++) {
			
			double massi = glycoIons[i]+possPepMass;

			for (int c = 1; c < iz; c++) {
				
				double ms = massi/(double)c + AminoAcidProperty.PROTON_W;

				for (int j = 0; j < len; j++) {
					
					double tempmz = peaks[j].getMz();

					if (Math.abs(ms-tempmz)<ms*ppm*1E-6) {
						matchedcount++;
						continue L1;
					}

					if (tempmz > ms)// over the range
						break;
				}
			}
		}

//		System.out.println("singlep: "+singlep+" Matched: "+matchedcount);
		double score = RandomPCalor.getScore(RandomPCalor.getProbility(trial, matchedcount, this.filter.singleP()));
//		System.out.println((double)matchedcount/(double)trial+"\t"+score+"\t"+matchedcount+"\t"+trial);
		return score;
	
	}
	
	public GlycoTree [][] getDatabaseUnit(){
		return this.glycoUnits;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		GlycoDatabaseMatcher matcher = new GlycoDatabaseMatcher();
	}

	

}
