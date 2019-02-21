/* 
 ******************************************************************************
 * File: PScoreCalculator2.java * * * Created on 2013-4-9
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.Pscore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.Combinator;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2013-4-9, 9:34:53
 */
public class PScoreCalculator {
	
	private HashSet <Character> [] siteAASet;
	private double [] addMasses;
	private double neuLossMass;
	private HashSet <Character> neuLossAA;
	private char [] symbols;
	private AminoacidFragment aaf;
	private int [] iontypes;
	private int modTypeCount;
	
	private DecimalFormat df2 = DecimalFormats.DF0_2;
	
	public PScoreCalculator(HashSet <Character> [] siteAASet, double [] addMasses, char [] symbols,
			AminoacidFragment aaf, int [] iontypes){
		
		this.siteAASet = siteAASet;
		this.addMasses = addMasses;
		this.symbols = symbols;
		this.aaf = aaf;
		this.iontypes = iontypes;
		this.modTypeCount = siteAASet.length;
	}
	
	public PScoreCalculator(HashSet <Character> [] siteAASet, double [] addMasses, double neuLossMass, 
			HashSet <Character> neuLossAA, char [] symbols, AminoacidFragment aaf, int [] iontypes){
		
		this.siteAASet = siteAASet;
		this.addMasses = addMasses;
		this.neuLossMass = neuLossMass;
		this.neuLossAA = neuLossAA;
		this.symbols = symbols;
		this.aaf = aaf;
		this.iontypes = iontypes;
		this.modTypeCount = siteAASet.length;
	}
	
	public SeqVsPScore compute(String sequence, short charge, IMS2PeakList peaklist, double tolerance){
		
		StringBuilder sb = new StringBuilder();
		StringBuilder uniPepSeq = new StringBuilder();
		
		int [] modCounts = new int [modTypeCount];
		ArrayList <Integer> [] siteLocList = new ArrayList [modTypeCount];
		for(int i=0;i<modTypeCount;i++){
			siteLocList[i] = new ArrayList <Integer>();
		}
		int aacount = 0;

		char [] tempIdenMod = new char [sequence.length()];
		
L:		for(int i=0;i<sequence.length();i++){
			if(sequence.charAt(i)>='A' && sequence.charAt(i)<='Z'){
				sb.append(sequence.charAt(i));
				if(i>=2 && i<sequence.length()-2){
					uniPepSeq.append(sequence.charAt(i));
					for(int j=0;j<modTypeCount;j++){
						if(this.siteAASet[j].contains(sequence.charAt(i))){
							siteLocList[j].add(aacount);
						}
					}
					aacount++;
				}
			}else{
				for(int j=0;j<modTypeCount;j++){
					if(sequence.charAt(i)==symbols[j]){
						modCounts[j]++;
						tempIdenMod[aacount-1] = symbols[j];
						continue L;
					}
				}
				sb.append(sequence.charAt(i));
			}
		}
		
		if(MathTool.getTotal(modCounts)==0) return null;
		
		int [] idCount = new int [modTypeCount];
		Object [][][] objs3 = new Object [modTypeCount][][];
		
		int matrixLength = 1;
		for(int i=0;i<modTypeCount;i++){
			if(siteLocList[i].size()>0 && modCounts[i]>0){
				Integer [] locs = siteLocList[i].toArray(new Integer[siteLocList[i].size()]);
				objs3[i] = Combinator.getCombination(locs, modCounts[i]);
				idCount[i] = objs3[i].length;
				matrixLength*=idCount[i];
			}else{
				idCount[i] = 0;
			}
		}
		
		char [][] modMatrix = new char [matrixLength][uniPepSeq.length()];
		char [] idenLoc = new char[uniPepSeq.length()];
		System.arraycopy(tempIdenMod, 0, idenLoc, 0, idenLoc.length);
		
		int [][] combineId = combine(idCount);
		
		int matrixId = 0;
		for(int i=0;i<combineId.length;i++){
			int [] ids = combineId[i];
			for(int j=0;j<ids.length;j++){
				if(objs3[j]!=null){
					Object [] objs1 = objs3[j][ids[j]];
					for(int k=0;k<objs1.length;k++){
						modMatrix[matrixId][(Integer)objs1[k]] = symbols[j];
					}
				}
			}
			
			matrixId++;
		}
		
		Ions ions = aaf.fragment(sb.toString(), iontypes, true);
		Ion [] bs = ions.bIons();
		Ion [] ys = ions.yIons();

		double [][] bIonMasses = new double [matrixLength][uniPepSeq.length()-1];
		double [][] yIonMasses = new double [matrixLength][uniPepSeq.length()-1];
		
		for(int i=0;i<modMatrix.length;i++){
			double beforeAdd = 0;
			for(int j=0;j<modMatrix[i].length;j++){
				for(int k=0;k<symbols.length;k++){
					if(modMatrix[i][j]==symbols[k]){
						beforeAdd += addMasses[k];
					}
					if(j<bs.length)
						bIonMasses[i][j] = bs[j].getMz()-AminoAcidProperty.PROTON_W+beforeAdd;
				}
			}
			
			double afterAdd = 0;
			for(int j=modMatrix[i].length-1;j>=0;j--){
				for(int k=0;k<symbols.length;k++){
					if(modMatrix[i][j]==symbols[k]){
						afterAdd += addMasses[k];
					}
					if(modMatrix[i].length-1-j<ys.length)
						yIonMasses[i][modMatrix[i].length-1-j] = ys[modMatrix[i].length-1-j].getMz()
							-AminoAcidProperty.PROTON_W+afterAdd;
				}
			}
//			System.out.println("B\t"+Arrays.toString(bIonMasses[i]));
//			System.out.println("Y\t"+Arrays.toString(yIonMasses[i]));
			System.out.println(Arrays.toString(modMatrix[i]));
		}
		
		double [] totalInten = new double [matrixLength];
		
/*		IPeak [] peaks = peaklist.getPeakList();
		for(int i=0;i<peaks.length;i++){
			double mz = peaks[i].getMz();
			double intensity = peaks[i].getIntensity();
			for(int j=1;j<=charge;j++){
				double mr = (mz-AminoAcidProperty.PROTON_W)*(double) j;
				int idy = Arrays.binarySearch(yIonMasses[0], mr);
				if(idy<0) idy = -idy-1;
//				System.out.println(Arrays.toString(yIonMasses[0])+"\n"+mr+"\n"+idy);

				for(int k=0;k<yIonMasses.length;k++){
					
					if(idy==yIonMasses[k].length){
						if(Math.abs(mr-yIonMasses[k][yIonMasses[k].length-1])<tolerance){
							totalInten[k] += intensity;
						}
						continue;
					}
					
					if(mr>yIonMasses[k][idy]){
						
						for(int l=idy+1;l<yIonMasses[k].length;l++){
							if(Math.abs(mr-yIonMasses[k][l])<tolerance){
//								System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//								System.out.println(Arrays.toString(yIonMasses[k]));
								totalInten[k] += intensity;
							}else if(yIonMasses[k][l]-mr>tolerance){
								break;
							}
						}
						
					}else{
						
						for(int l=idy;l>=0;l--){
							if(Math.abs(mr-yIonMasses[k][l])<tolerance){
//								System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//								System.out.println(Arrays.toString(yIonMasses[k]));
								totalInten[k] += intensity;
							}else if(mr-yIonMasses[k][l]>tolerance){
								break;
							}
						}
					}
				}
				
				int idb = Arrays.binarySearch(bIonMasses[0], mr);
				if(idb<0) idb = -idb-1;
//				System.out.println(Arrays.toString(yIonMasses[0])+"\n"+mr+"\n"+idy);

				for(int k=0;k<bIonMasses.length;k++){
					
					if(idb==bIonMasses[k].length){
						if(Math.abs(mr-bIonMasses[k][bIonMasses[k].length-1])<tolerance){
							totalInten[k] += intensity;
						}
						continue;
					}
					
					if(mr>bIonMasses[k][idb]){
						
						for(int l=idb+1;l<bIonMasses[k].length;l++){
							if(Math.abs(mr-bIonMasses[k][l])<tolerance){
//								System.out.println("B+"+(l+1)+"\t"+bIonMasses[k][l]+"\t"+mr+"\t"+k);
//								System.out.println(Arrays.toString(bIonMasses[k]));
								totalInten[k] += intensity;
							}else if(bIonMasses[k][l]-mr>tolerance){
								break;
							}
						}
						
					}else{
						
						for(int l=idb;l>=0;l--){
							if(Math.abs(mr-bIonMasses[k][l])<tolerance){
//								System.out.println("B+"+(l+1)+"\t"+bIonMasses[k][l]+"\t"+mr+"\t"+k);
//								System.out.println(Arrays.toString(bIonMasses[k]));
								totalInten[k] += intensity;
							}else if(mr-bIonMasses[k][l]>tolerance){
								break;
							}
						}
					}
				}
			}
		}
		
		double maxInten = -1;
		double idenInten = -1;
		int maxid = -1;
		int idenid = -1;
		double [] scoreIntens = new double [uniPepSeq.length()];
		for(int i=0;i<totalInten.length;i++){
//			System.out.println(i+"\t"+peaklist.getPrecursePeak().getScanNum()+"\t"+totalInten[i]+"\t"+Arrays.toString(modMatrix[i]));
			if(Arrays.toString(idenLoc).equals(Arrays.toString(modMatrix[i]))){
				idenInten =  totalInten[i];
				idenid = i;
			}
			for(int j=0;j<modMatrix[i].length;j++){
				if(modMatrix[i][j]!='\u0000'){
					scoreIntens[j]+=totalInten[i];
				}
			}
			if(totalInten[i]>maxInten){
				maxInten = totalInten[i];
				maxid = i;
			}
		}
		
		if(maxInten==idenInten){
			maxid = idenid;
		}
		
		double deltaScore = 0;
		
		if(maxInten>0){
			if(totalInten.length>1){
				Arrays.sort(totalInten);
				double secondInten = totalInten[totalInten.length-2];
				deltaScore = Double.parseDouble(df2.format((maxInten-secondInten)/maxInten));
//				System.out.println(maxInten+"\t"+secondInten+"\t"+deltaScore);
			}else{
				deltaScore = 1;
			}
		}

//System.out.println(Arrays.toString(scoreIntens)+"\n"+Arrays.toString(modMatrix[maxid])+"\t"+sequence);
		double [] scores = new double [uniPepSeq.length()];
		for(int i=0;i<modTypeCount;i++){
			if(siteLocList[i].size()>0 && modCounts[i]>0){
				double totalInteni = 0;
				for(int j=0;j<siteLocList[i].size();j++){
					totalInteni += scoreIntens[siteLocList[i].get(j)];
				}
				for(int j=0;j<siteLocList[i].size();j++){
					if(totalInteni==0)
						scores[siteLocList[i].get(j)] = 1.0/siteLocList[i].size();
					else
						scores[siteLocList[i].get(j)] = scoreIntens[siteLocList[i].get(j)]/totalInteni;
				}
			}
		}

		SeqVsPScore sps = this.getSequenceWithScore(sb.toString(), modMatrix[maxid], scores);
		sps.setDeltaScore(deltaScore);
//		System.out.println(sequence+"\t"+sb2+"\t"+finalSequence+"\t"+sequence.equals(finalSequence));
//		System.out.println(Arrays.toString(modMatrix[maxid]));
		return sps;
*/
		return null;
	}
	
	public SeqVsPScore computeWithNeuLoss(String sequence, short charge, IMS2PeakList peaklist, double tolerance, boolean mono){
		
		if(sequence.contains("X")) return null;
		
		StringBuilder sb = new StringBuilder();
		StringBuilder uniPepSeq = new StringBuilder();
		
		int [] modCounts = new int [modTypeCount];
		ArrayList <Integer> [] siteLocList = new ArrayList [modTypeCount];
		for(int i=0;i<modTypeCount;i++){
			siteLocList[i] = new ArrayList <Integer>();
		}
		int aacount = 0;

		char [] tempIdenMod = new char [sequence.length()];
		int [] tempNeuCountList = new int [sequence.length()];
		int neuCount = 0;
		
L:		for(int i=0;i<sequence.length();i++){
			char aai = sequence.charAt(i);
			if(aai>='A' && aai<='Z'){
				sb.append(aai);
				if(i>=2 && i<sequence.length()-2){
					uniPepSeq.append(aai);
					for(int j=0;j<modTypeCount;j++){
						if(this.siteAASet[j].contains(aai)){
							siteLocList[j].add(aacount);
						}
					}
					if(this.neuLossAA.contains(aai)){
						neuCount++;
					}
					tempNeuCountList[aacount] = neuCount;
					aacount++;
				}
			}else{
				for(int j=0;j<modTypeCount;j++){
					if(aai==symbols[j]){
						modCounts[j]++;
						tempIdenMod[aacount-1] = symbols[j];
						continue L;
					}
				}
				sb.append(aai);
			}
		}
		
		if(MathTool.getTotal(modCounts)==0) return null;

		int [] idCount = new int [modTypeCount];
		Object [][][] objs3 = new Object [modTypeCount][][];
		
		int matrixLength = 1;
		for(int i=0;i<modTypeCount;i++){
			if(siteLocList[i].size()>0 && modCounts[i]>0){
				Integer [] locs = siteLocList[i].toArray(new Integer[siteLocList[i].size()]);
				objs3[i] = Combinator.getCombination(locs, modCounts[i]);
				idCount[i] = objs3[i].length;
				matrixLength*=idCount[i];
			}else{
				idCount[i] = 0;
			}
		}
		
		char [][] modMatrix = new char [matrixLength][uniPepSeq.length()];
		char [] idenLoc = new char[uniPepSeq.length()];
		System.arraycopy(tempIdenMod, 0, idenLoc, 0, idenLoc.length);
		
		int [] bNeuCount = new int [uniPepSeq.length()-1];
		int [] yNeuCount = new int [uniPepSeq.length()-1];
		System.arraycopy(tempNeuCountList, 0, bNeuCount, 0, bNeuCount.length);
		for(int i=0;i<yNeuCount.length;i++){
			yNeuCount[i] = neuCount-bNeuCount[bNeuCount.length-1-i];
		}

		int [][] combineId = combine(idCount);
		
		int matrixId = 0;
		for(int i=0;i<combineId.length;i++){
			int [] ids = combineId[i];
			for(int j=0;j<ids.length;j++){
				if(objs3[j]!=null){
					Object [] objs1 = objs3[j][ids[j]];
					for(int k=0;k<objs1.length;k++){
						modMatrix[matrixId][(Integer)objs1[k]] = symbols[j];
					}
				}
			}
			matrixId++;
		}
		
		Ions ions = aaf.fragment(sb.toString(), iontypes, true);
		Ion [] bs = ions.bIons();
		Ion [] ys = ions.yIons();

		double [][] bIonMasses = new double [matrixLength][uniPepSeq.length()-1];
		double [][] yIonMasses = new double [matrixLength][uniPepSeq.length()-1];
		
		for(int i=0;i<modMatrix.length;i++){
			double beforeAdd = 0;
			for(int j=0;j<modMatrix[i].length;j++){
				for(int k=0;k<symbols.length;k++){
					if(modMatrix[i][j]==symbols[k]){
						beforeAdd += addMasses[k];
					}
					if(j<bs.length)
						bIonMasses[i][j] = bs[j].getMz()-AminoAcidProperty.PROTON_W+beforeAdd;
				}
			}
			
			double afterAdd = 0;
			for(int j=modMatrix[i].length-1;j>=0;j--){
				for(int k=0;k<symbols.length;k++){
					if(modMatrix[i][j]==symbols[k]){
						afterAdd += addMasses[k];
					}
					if(modMatrix[i].length-1-j<ys.length)
						yIonMasses[i][modMatrix[i].length-1-j] = ys[modMatrix[i].length-1-j].getMz()
							-AminoAcidProperty.PROTON_W+afterAdd;
				}
			}
		}
		
		double [] totalInten = new double [matrixLength];
		
//		IPeak [] peaks = peaklist.getPeakList();
		IPeak [] peaks = peaklist.getPeaksSortByIntensity();
		
		for(int i=0;i<peaks.length;i++){
			
			double mz = peaks[i].getMz();
//			double intensity = peaks[i].getIntensity();
			double intensity = (double)(peaks.length-i)/(double)peaks.length;
//			double intensity = Math.log(peaks[i].getIntensity());

			for(int j=1;j<=charge;j++){
				
				int matchCount = 0;
				ArrayList <Integer> matchedList = new ArrayList <Integer>();
				
				double mr = (mz-AminoAcidProperty.PROTON_W)*(double) j;
				int idy = Arrays.binarySearch(yIonMasses[0], mr);
				if(idy<0) idy = -idy-1;

				for(int k=0;k<yIonMasses.length;k++){
					
					if(idy==yIonMasses[k].length){
						if(Math.abs(mr-yIonMasses[k][yIonMasses[k].length-1])<tolerance){
							totalInten[k] += intensity;
							matchCount++;
							matchedList.add(k);
						}
						continue;
					}
					
					for(int l=idy-1;l>=0;l--){
						if(Math.abs(mr-yIonMasses[k][l])<tolerance){
//							System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//							System.out.println(Arrays.toString(yIonMasses[k]));
							totalInten[k] += intensity;
							matchCount++;
							matchedList.add(k);
							
						}else if(mr-yIonMasses[k][l]>tolerance){
							break;
						}
					}
					
					for(int l=idy;l<yIonMasses[k].length;l++){
						if(Math.abs(mr-yIonMasses[k][l])<tolerance){
//							System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//							System.out.println(Arrays.toString(yIonMasses[k]));
							totalInten[k] += intensity;
							matchCount++;
							matchedList.add(k);
							
						}else if(yIonMasses[k][l]-mr>tolerance){
							if(yNeuCount[idy]>0){
								for(int m=1;m<=yNeuCount[idy];m++){
									if(Math.abs(mr-(yIonMasses[k][l]-neuLossMass*m))<tolerance){
//										System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//										System.out.println(Arrays.toString(yIonMasses[k]));
										totalInten[k] += intensity;
										matchCount++;
										matchedList.add(k);
									}
								}
								
								if(yIonMasses[k][l]-neuLossMass*yNeuCount[idy]-mr>tolerance){
									break;
								}
							}else{
								break;
							}
						}
					}
				}
				
				int idb = Arrays.binarySearch(bIonMasses[0], mr);
				if(idb<0) idb = -idb-1;
//				System.out.println(Arrays.toString(yIonMasses[0])+"\n"+mr+"\n"+idy);

				for(int k=0;k<bIonMasses.length;k++){
					
					if(idb==bIonMasses[k].length){
						if(Math.abs(mr-bIonMasses[k][bIonMasses[k].length-1])<tolerance){
							totalInten[k] += intensity;
							matchCount++;
							matchedList.add(k);
						}
						continue;
					}
					
					for(int l=idb-1;l>=0;l--){
						if(Math.abs(mr-bIonMasses[k][l])<tolerance){
//							System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//							System.out.println(Arrays.toString(yIonMasses[k]));
							totalInten[k] += intensity;
							matchCount++;
							matchedList.add(k);
							
						}else if(mr-bIonMasses[k][l]>tolerance){
							break;
						}
					}
					
					for(int l=idb;l<bIonMasses[k].length;l++){
						if(Math.abs(mr-bIonMasses[k][l])<tolerance){
//							System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//							System.out.println(Arrays.toString(yIonMasses[k]));
							totalInten[k] += intensity;
							matchCount++;
							matchedList.add(k);
							
						}else if(bIonMasses[k][l]-mr>tolerance){
							if(bNeuCount[idb]>0){
								for(int m=1;m<=bNeuCount[idb];m++){
									if(Math.abs(mr-(bIonMasses[k][l]-neuLossMass*m))<tolerance){
//										System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//										System.out.println(Arrays.toString(yIonMasses[k]));
										totalInten[k] += intensity;
										matchCount++;
										matchedList.add(k);
									}
								}
								
								if(bIonMasses[k][l]-neuLossMass*bNeuCount[idb]-mr>tolerance){
									break;
								}
							}else{
								break;
							}
						}
					}
				}
/*				if(matchCount>1){
					double minus = intensity*((double)matchCount-1.0)/(double)matchCount;
					for(int k=0;k<matchedList.size();k++){
						totalInten[matchedList.get(k)] -= minus;
					}
				}
*/				
			}
		}
//		System.out.println(Arrays.toString(totalInten));
		double maxInten = -1;
		double idenInten = -1;
		int maxid = -1;
		int idenid = -1;
		double [] scoreIntens = new double [uniPepSeq.length()];
		for(int i=0;i<totalInten.length;i++){
//			System.out.println(i+"\t"+peaklist.getPrecursePeak().getScanNum()+"\t"+totalInten[i]+"\t"+Arrays.toString(modMatrix[i]));
			if(Arrays.toString(idenLoc).equals(Arrays.toString(modMatrix[i]))){
				idenInten =  totalInten[i];
				idenid = i;
			}
			for(int j=0;j<modMatrix[i].length;j++){
				if(modMatrix[i][j]!='\u0000'){
					scoreIntens[j]+=totalInten[i];
				}
			}
			if(totalInten[i]>maxInten){
				maxInten = totalInten[i];
				maxid = i;
			}
		}
		
		if(maxInten==idenInten){
			maxid = idenid;
		}
		
		double deltaScore = 0;
		
		if(maxInten>0){
			if(totalInten.length>1){
				Arrays.sort(totalInten);
				double secondInten = totalInten[totalInten.length-2];
				deltaScore = Double.parseDouble(df2.format((maxInten-secondInten)/maxInten));
//				System.out.println(maxInten+"\t"+secondInten+"\t"+deltaScore);
			}else{
				deltaScore = 1;
			}
		}

//System.out.println(Arrays.toString(scoreIntens)+"\n"+Arrays.toString(modMatrix[maxid])+"\t"+sequence);
		double [] scores = new double [uniPepSeq.length()];
		for(int i=0;i<modTypeCount;i++){
			if(siteLocList[i].size()>0 && modCounts[i]>0){
				double totalInteni = 0;
				for(int j=0;j<siteLocList[i].size();j++){
					totalInteni += scoreIntens[siteLocList[i].get(j)];
				}
				for(int j=0;j<siteLocList[i].size();j++){
					if(totalInteni==0)
						scores[siteLocList[i].get(j)] = 1.0/siteLocList[i].size();
					else
						scores[siteLocList[i].get(j)] = scoreIntens[siteLocList[i].get(j)]/totalInteni;
				}
			}
		}

		SeqVsPScore sps = this.getSequenceWithScore(sb.toString(), modMatrix[maxid], scores);
		sps.setDeltaScore(deltaScore);
//		System.out.println(sequence+"\t"+sb2+"\t"+finalSequence+"\t"+sequence.equals(finalSequence));
//		System.out.println(Arrays.toString(modMatrix[maxid]));
		return sps;
	}

	private SeqVsPScore getSequenceWithScore(String nomodseq, char [] sym, double [] scores){
		
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		boolean begin = false;
		int aaid = 0;
		
		for(int i=0;i<nomodseq.length();i++){
			if(begin){
				if(nomodseq.charAt(i)=='.'){
					sb.append('.');
					sb2.append('.');
					begin = false;
				}else if(nomodseq.charAt(i)>='A' && nomodseq.charAt(i)<='Z'){
					sb.append(nomodseq.charAt(i));
					sb2.append(nomodseq.charAt(i));
					if(sym[aaid]!='\u0000'){
						sb.append(sym[aaid]);
					}
					if(scores[aaid]!=0){
						sb2.append("("+df2.format(scores[aaid])+")");
					}
					aaid++;
				}else{
					sb.append(nomodseq.charAt(i));
					sb2.append(nomodseq.charAt(i));
				}
			}else{
				if(nomodseq.charAt(i)=='.'){
					sb.append('.');
					sb2.append('.');
					begin = true;
				}else{
					sb.append(nomodseq.charAt(i));
					sb2.append(nomodseq.charAt(i));
				}
			}
		}
		
		SeqVsPScore sps = new SeqVsPScore(sb.toString(), sb2.toString(), scores, sym);

		return sps;
	}

	private static int [][] combine(int [] num){
		if(num.length==1){
			if(num[0]==0){
				return new int [][]{{-1}};
			}
			int [][] combine = new int[num[0]][1];
			for(int i=0;i<combine.length;i++){
				combine[i] = new int[]{i};
			}
			return combine;
		}
		
		int [] noZero = new int [num.length-1];
		System.arraycopy(num, 1, noZero, 0, noZero.length);
		
		int [][] noZeroCombine = combine(noZero);
		
		int [][] result;
		
		if(num[0]==0){
			result = new int[noZeroCombine.length][];
			for(int i=0;i<noZeroCombine.length;i++){
				result[i] = new int[noZeroCombine[i].length+1];
				result[i][0] = -1;
				System.arraycopy(noZeroCombine[i], 0, result[i], 1, noZeroCombine[i].length);
			}
		}else{
			result = new int[noZeroCombine.length*num[0]][];
			for(int i=0;i<num[0];i++){
				for(int j=0;j<noZeroCombine.length;j++){
					result[i*noZeroCombine.length+j] = new int[noZeroCombine[j].length+1];
					result[i*noZeroCombine.length+j][0] = i;
					System.arraycopy(noZeroCombine[j], 0, result[i*noZeroCombine.length+j], 1, noZeroCombine[j].length);
				}
			}
		}

		return result;
	}
	
	private static void test(){
		
		HashSet <Character> [] siteAASet = new HashSet [3];
		for(int i=0;i<siteAASet.length;i++){
			siteAASet[i] = new HashSet <Character>();
		}
		siteAASet[0].add('S');
		siteAASet[0].add('T');
		siteAASet[1].add('M');
		siteAASet[2].add('K');
		
		double [] addMasses = new double []{98, 16, 28};
		char [] symbols = new char []{'#', '*', '@'};
		
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		
		AminoacidModification aam = new AminoacidModification();
		aam.addModification('#', 98, "phos");
		aam.addModification('*', 16, "oxi");
		aam.addModification('@', 28, "mod");
		
		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		int [] iontypes = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		
		PScoreCalculator psc = new PScoreCalculator(siteAASet, addMasses, symbols, aaf, iontypes);
		String sequence = "X.AMAAS#AAK@ATAAAAM*AAKAAS#AA.X";
		short charge = 2;
		IMS2PeakList peaklist = null;
		double tolerance = 1;
		
		psc.compute(sequence, charge, peaklist, tolerance);
	}
	
	private static void test2(String ppl, String literater, String mapid) throws FileDamageException, IOException{
		
		HashMap <String, String> map = new HashMap <String, String>();
		BufferedReader lireader = new BufferedReader(new FileReader(literater));
		String line = lireader.readLine();
		while((line=lireader.readLine())!=null){
			String [] cs = line.split("\t");
			if(cs[0].equals(mapid)){
				map.put(cs[5], cs[7]);
			}
		}
		lireader.close();
		
		PeptideListReader reader = new PeptideListReader(ppl);
		ISearchParameter parameter = reader.getSearchParameter();
		Aminoacids aas = parameter.getStaticInfo();
		AminoacidModification aam = parameter.getVariableInfo();
		Modif [] modifs = aam.getModifications();
		double [] masses = new double [modifs.length-1];
		char [] symbols = new char [modifs.length-1];
		HashSet <Character> [] siteAASets = new HashSet [modifs.length-1];
		HashSet <Character> neuSet = new HashSet <Character>();
		neuSet.add('S');
		neuSet.add('T');
		
		for(int i=0;i<modifs.length-1;i++){
			siteAASets[i] = new HashSet <Character>();
			masses[i] = modifs[i].getMass();
			symbols[i] = modifs[i].getSymbol();
			HashSet<ModSite> sites = aam.getModifSites(symbols[i]);
			for(ModSite site : sites){
				String modifat = site.getModifAt();
				siteAASets[i].add(modifat.charAt(0));
			}
		}
		
		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		int [] iontypes = new int []{Ion.TYPE_B, Ion.TYPE_Y};
//		PScoreCalculator psc = new PScoreCalculator(siteAASets, masses, symbols, aaf, iontypes);
		PScoreCalculator psc = new PScoreCalculator(siteAASets, masses, 97.976896, neuSet, symbols, aaf, iontypes);
		double tolerance = 0.02;
		
		int target = 0;
		int decoy = 0;
		IPeptide pep = null;
		while((pep=reader.getPeptide())!=null){
			IMS2PeakList peaklist = reader.getPeakLists()[0];
			String sequence = pep.getSequence();
			String uniqueseq = PeptideUtil.getUniqueSequence(sequence);
			if(pep.getRank()!=1) continue;
			
//if(uniqueseq.contains("SLDNGGYYISPR")){
//System.out.println(peaklist.getPrecursePeak().getScanNum());
//			System.out.println(sequence+"\t"+pep.getScanNumBeg()+"\t"+pep.getPrimaryScore());
			short charge = pep.getCharge();

//			SeqVsPScore sps = psc.compute(sequence, charge, peaklist, tolerance);
			SeqVsPScore sps = psc.computeWithNeuLoss(sequence, charge, peaklist, tolerance, true);
			
			if(sps!=null){
				
				String seq = sps.getSequenceString();
				
				Integer [] locs = sps.getLocList();
				String ss = "";
				for(int i=0;i<locs.length;i++){
					ss += "&";
					ss += String.valueOf(locs[i]);
				}
				ss = ss.substring(1);
				
				if(map.containsKey(uniqueseq)){
					
					int modcount = map.get(uniqueseq).split("&").length;
					if(modcount==locs.length){
						if(map.get(uniqueseq).equals(ss)){
//							System.out.println("T\t"+sps.getDeltaScore());
							target++;
						}else{
							decoy++;
							System.out.println("D\t"+sps.getDeltaScore());
//							if(sps.getDeltaScore()>0.4){
//								System.out.println(map.get(uniqueseq)+"\t"+(ss)+"\t"+sequence+"\t"+sps.getSequenceString()+"\t"+Arrays.toString(sps.getPScore()));
//							}
						}
					}
				}
			}
			
//}			
		}
		System.out.println(target+"\t"+decoy);
	}
	
	private static void test3(String ppl, String literater) throws FileDamageException, IOException{
		
		HashMap <String, String> [] maps = new HashMap [5];
		for(int i=0;i<maps.length;i++){
			maps[i] = new HashMap <String, String>();
		}
		BufferedReader lireader = new BufferedReader(new FileReader(literater));
		String line = lireader.readLine();
		while((line=lireader.readLine())!=null){
			String [] cs = line.split("\t");
			int mapid = Integer.parseInt(cs[0])-1;
			maps[mapid].put(cs[5], cs[7]);
		}
		lireader.close();
		
		File [] files = (new File(ppl)).listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("ppl"))
					return true;
				
				return false;
			}
			
		});
		
//		
//		ISearchParameter parameter = reader.getSearchParameter();
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		AminoacidModification aam = new AminoacidModification();
		aam.addModification('#', 79.966331, "phospho");
		aam.addModification('*', 15.994915, "oxidation");
		
		double [] masses = new double []{79.966331};
		char [] symbols = new char []{'#'};
		HashSet <Character> [] siteAASets = new HashSet [1];
		siteAASets[0] = new HashSet <Character>();
		siteAASets[0].add('S');
		siteAASets[0].add('T');
		siteAASets[0].add('Y');
		
		HashSet <Character> neuSet = new HashSet <Character>();
		neuSet.add('S');
		neuSet.add('T');

		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		int [] iontypes = new int []{Ion.TYPE_B, Ion.TYPE_Y};
//		PScoreCalculator psc = new PScoreCalculator(siteAASets, masses, symbols, aaf, iontypes);
		PScoreCalculator psc = new PScoreCalculator(siteAASets, masses, 97.976896, neuSet, symbols, aaf, iontypes);
//		double tolerance = 0.02;
		double tolerance = 0.8;
		
		int target = 0;
		int decoy = 0;
		int nochange = 0;
		int change = 0;
		
		HashSet <String> targetSet = new HashSet <String>();
		HashSet <String> decoySet = new HashSet <String>();
		
		Pattern pattern = Pattern.compile(".*mix(\\d)_.*");
		
		for(int fi=0;fi<files.length;fi++){
			int id = -1;
			String filename = files[fi].getName();
			Matcher matcher = pattern.matcher(filename);
			if(matcher.matches()){
				id = Integer.parseInt(matcher.group(1))-1;
			}
			
			PeptideListReader reader = new PeptideListReader(files[fi]);
			MascotPeptide pep = null;
			while((pep=(MascotPeptide) reader.getPeptide())!=null){
				
				IMS2PeakList peaklist = reader.getPeakLists()[0];
				String sequence = pep.getSequence();
				String uniqueseq = PeptideUtil.getUniqueSequence(sequence);
				if(pep.getRank()!=1) continue;

				short charge = pep.getCharge();

				SeqVsPScore sps = psc.computeWithNeuLoss(sequence, charge, peaklist, tolerance, true);
				
				if(sps!=null){
					
					String seq = sps.getSequenceString();
					
					Integer [] locs = sps.getLocList();
					String ss = "";
					for(int i=0;i<locs.length;i++){
						ss += "&";
						ss += String.valueOf(locs[i]);
					}
					ss = ss.substring(1);
					
					if(maps[id].containsKey(uniqueseq)){
						
						int modcount = maps[id].get(uniqueseq).split("&").length;
						if(modcount==locs.length){
							if(maps[id].get(uniqueseq).equals(ss)){
								
								if(sequence.equals(seq)){
									nochange++;
								}else{
									change++;
								}
								
								if(sps.getDeltaScore()>=0.12){
									targetSet.add(seq);
									target++;
//									System.out.println("T\t"+sps.getDeltaScore()+"\t"+pep.getIonscore()*pep.getDeltaS());

								}
							}else{
								
								if(sps.getDeltaScore()>=0.12){
									decoySet.add(seq);
									decoy++;
//									System.out.println("D\t"+sps.getDeltaScore()+"\t"+pep.getIonscore()*pep.getDeltaS());

								}
//								if(sps.getDeltaScore()>0.4){
//									System.out.println(map.get(uniqueseq)+"\t"+(ss)+"\t"+sequence+"\t"+sps.getSequenceString()+"\t"+Arrays.toString(sps.getPScore()));
//								}
							}
						}
					}
				}
			}
		}
		
		System.out.println(target+"\t"+decoy+"\t"+targetSet.size()+"\t"+decoySet.size());
		System.out.println(nochange+"\t"+change);
	}
	
	private static void test4(String in) throws IOException{
		
		int [] target = new int [100];
		int [] decoy = new int [100];
		int count = 0;
		
		BufferedReader reader =  new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null){
			count++;
			String [] cs = line.split("\t");
			if(cs.length==5){
				double targetScore = Double.parseDouble(cs[1]);
				double decoyScore = Double.parseDouble(cs[4]);
				for(int i=0;i<target.length;i++){
//					double thres = 1.0*(double)i/100.0;
					double thres = (double)i;
					if(targetScore>=thres){
						target[i]++;
					}else{
						break;
					}
				}
				for(int i=0;i<target.length;i++){
//					double thres = 1.0*(double)i/100.0;
					double thres = (double)i;
					if(decoyScore>=thres){
						decoy[i]++;
					}else{
						break;
					}
				}
			}else if(cs.length==2){
				double targetScore = Double.parseDouble(cs[1]);
				for(int i=0;i<target.length;i++){
//					double thres = 1.0*(double)i/100.0;
					double thres = (double)i;
					if(targetScore>=thres){
						target[i]++;
					}else{
						break;
					}
				}
			}else{
				System.out.println(line);
			}
		}
		reader.close();
		
		for(int i=0;i<target.length;i++){
			System.out.println(i+"\t"+target[i]+"\t"+decoy[i]+"\t"+
					(double)decoy[i]/(double)(target[i]+decoy[i]));
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException {
		// TODO Auto-generated method stub

		long begin = System.currentTimeMillis();
		
//		PScoreCalculator.test();
		
//		PScoreCalculator.test2("J:\\Data\\Site_loc\\HCD\\Pepmix5_HCD_F004397.dat.ppl",
//				"H:\\Validation\\phospho_download\\Literature\\peptide.txt", "5");
		
		PScoreCalculator.test3("J:\\Data\\Site_loc\\HCD",
				"H:\\Validation\\phospho_download\\Literature\\peptide.txt");

//		PScoreCalculator.test4("J:\\Data\\Site_loc\\HCD_MD.txt");
		
		long end = System.currentTimeMillis();
		
		System.out.println((end-begin)/1000.0);
	}

}
