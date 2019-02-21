/* 
 ******************************************************************************
 * File:MascotTest.java * * * Created on 2012-7-12
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.group;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.penn.probability.wekakd.MyInstance;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.EuclideanDistance;

import cn.ac.dicp.gp1809.clustering.PSMInstanceCreator;
import cn.ac.dicp.gp1809.clustering.SpectrumDataPoint;
import cn.ac.dicp.gp1809.clustering.kMean.Cluster;
import cn.ac.dicp.gp1809.clustering.kMean.ClusterAnalysis;
import cn.ac.dicp.gp1809.clustering.kMean.DataPoint;
import cn.ac.dicp.gp1809.clustering.kMean.TwoDArraysDataPoint;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.PeakForMatch;
import cn.ac.dicp.gp1809.proteome.spectrum.SpectrumMatcher;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2012-7-12, 17:44:45
 */
public class MascotTest {
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	private PeptideListReader reader;

	public MascotTest(String file) throws FileDamageException, IOException{
		
		this.reader = new PeptideListReader(file);
	}
	
	private void test(){
		
		ArrayList <IPeptide> list = new ArrayList <IPeptide>();
		int [][] count = new int [2][50];
		IPeptide pep;
		while((pep=reader.getPeptide())!=null){
			int score = (int) pep.getPrimaryScore();
			boolean tp = pep.isTP();
			int rank = pep.getRank();
			
//System.out.println(score+"\t"+rank+"\t"+tp);			
			if(rank==1){
				list.add(pep);
				if(tp){
					if(score<50){
						for(int i=0;i<=score;i++)
							count[0][i]++;
					}else{
						for(int i=0;i<50;i++)
							count[0][i]++;
					}
				}else{
					if(score<50){
						for(int i=0;i<=score;i++)
							count[1][i]++;
					}else{
						for(int i=0;i<50;i++)
							count[1][i]++;
					}
				}
			}
		}
		double score = 0;
		for(int i=0;i<50;i++){
			double fdr = count[0][i]==0 ? 0 : Double.parseDouble(df4.format((double)count[1][i]/(double)count[0][i]));
			System.out.println((i+1)+"\t"+fdr+"\t"+count[0][i]+"\t"+count[1][i]);
			if(fdr<0.01){
				score = i;
				break;
			}
		}
		int [][] lencount = new int [20][2];
		for(int i=0;i<list.size();i++){
			IPeptide peptide = list.get(i);
			if(peptide.getPrimaryScore()>score){
				int len = PeptideUtil.getSequenceLength(peptide.getSequence());
				int tp = peptide.isTP()? 1:0;
				if(len<=6){
					lencount[0][tp]++;
				}else if(len>=25){
					lencount[19][tp]++;
				}else{
					lencount[len-6][tp]++;
				}
			}
		}
		for(int i=0;i<20;i++){
			System.out.println(lencount[i][1]+"\t"+lencount[i][0]);
		}
	}
	
	private void testRandem(){
		
		ArrayList <int [][]> list = new ArrayList <int [][]>();
		IPeptide peptide;
		int [][] current = new int [2][50];
		
		HashMap <Integer, IPeptide> pepmap = new HashMap <Integer, IPeptide>();
		while((peptide=reader.getPeptide())!=null){
			int scannum = peptide.getScanNumBeg();
			if(peptide.getRank()==1)
				pepmap.put(scannum, peptide);
		}
		
		Iterator <Integer> it = pepmap.keySet().iterator();
		
		while(it.hasNext()){
			
			Integer key = it.next();
			IPeptide pep = pepmap.get(key);
			
			int score = (int) pep.getPrimaryScore();
			boolean tp = pep.isTP();
			int rank = pep.getRank();
			
			if(rank==1){
				if(tp){
					if(score<50){
						for(int i=0;i<=score;i++)
							current[0][i]++;
					}else{
						for(int i=0;i<50;i++)
							current[0][i]++;
					}
				}else{
					if(score<50){
						for(int i=0;i<=score;i++)
							current[1][i]++;
					}else{
						for(int i=0;i<50;i++)
							current[1][i]++;
					}
				}
			}
			
			if(current[0][0]+current[1][0]>200000){
				
				int [][] add = new int [2][50];
				for(int i=0;i<add.length;i++){
					System.arraycopy(current[i], 0, add[i], 0, add[i].length);
				}
				list.add(add);
				
				current = new int [2][50];
			}
		}
		list.add(current);
		
		System.out.println("listsize\t"+list.size()+"\n");
		int target = 0;
		int decoy = 0;
		
		for(int i=0;i<list.size();i++){
			
			int [][] count = list.get(i);
			for(int j=0;j<count[0].length;j++){
				double fdr = count[0][j]==0 ? 1 : Double.parseDouble(df4.format((double)count[1][j]/(double)count[0][j]));
				if(fdr<0.01){
					target += count[0][j];
					decoy += count[1][j];
					System.out.println(i+"\t"+j+"\t"+count[0][j]+"\t"+count[1][j]+"\t"+fdr);
					break;
				}
			}
		}
		
		double fdr = Double.parseDouble(df4.format((double)decoy/(double)target));
		System.out.println("total\tscore\t"+list.size()+"\t"+target+"\t"+decoy+"\t"+fdr);
		
/*		for(int i=0;i<50;i++){
			
			for(int j=0;j<4;j++){
				
				double fdr = count[j][0][i]==0 ? 0 : Double.parseDouble(df4.format((double)count[j][1][i]/(double)count[j][0][i]));
				System.out.print((j+1)+"\t"+(i+1)+"\t"+fdr+"\t"+count[j][0][i]+"\t"+count[j][1][i]+"\t\t");
			}
			System.out.println();
		}
		System.out.println();
		
		for(int i=0;i<50;i++){
			
			for(int j=4;j<8;j++){
				
				double fdr = count[j][0][i]==0 ? 0 : Double.parseDouble(df4.format((double)count[j][1][i]/(double)count[j][0][i]));
				System.out.print((j+1)+"\t"+(i+1)+"\t"+fdr+"\t"+count[j][0][i]+"\t"+count[j][1][i]+"\t\t");
			}
			System.out.println();
		}
		System.out.println();
		
		for(int i=0;i<50;i++){
			
			for(int j=8;j<12;j++){
				
				double fdr = count[j][0][i]==0 ? 0 : Double.parseDouble(df4.format((double)count[j][1][i]/(double)count[j][0][i]));
				System.out.print((j+1)+"\t"+(i+1)+"\t"+fdr+"\t"+count[j][0][i]+"\t"+count[j][1][i]+"\t\t");
			}
			System.out.println();
		}
		System.out.println();
		
		for(int i=0;i<50;i++){
			
			for(int j=12;j<16;j++){
				
				double fdr = count[j][0][i]==0 ? 0 : Double.parseDouble(df4.format((double)count[j][1][i]/(double)count[j][0][i]));
				System.out.print((j+1)+"\t"+(i+1)+"\t"+fdr+"\t"+count[j][0][i]+"\t"+count[j][1][i]+"\t\t");
			}
			System.out.println();
		}
		System.out.println();
		
		for(int i=0;i<50;i++){
			
			for(int j=16;j<20;j++){
				
				double fdr = count[j][0][i]==0 ? 0 : Double.parseDouble(df4.format((double)count[j][1][i]/(double)count[j][0][i]));
				System.out.print((j+1)+"\t"+(i+1)+"\t"+fdr+"\t"+count[j][0][i]+"\t"+count[j][1][i]+"\t\t");
			}
			System.out.println();
		}
*/		
	}
	
	private void testline(){
		
		ArrayList <int [][]> list = new ArrayList <int [][]>();
		IPeptide peptide;
		int [][] current = new int [2][50];
		
		HashMap <Integer, IPeptide> pepmap = new HashMap <Integer, IPeptide>();
		while((peptide=reader.getPeptide())!=null){
			int scannum = peptide.getScanNumBeg();
			if(peptide.getRank()==1)
				pepmap.put(scannum, peptide);
		}
		
		Integer [] keys = pepmap.keySet().toArray(new Integer[pepmap.size()]);
		Arrays.sort(keys);
		
		for(int k=0;k<keys.length;k++){

			IPeptide pep = pepmap.get(keys[k]);
			System.out.println(pep.getScanNumBeg());
			int score = (int) pep.getPrimaryScore();
			boolean tp = pep.isTP();
			int rank = pep.getRank();
			
			if(rank==1){
				if(tp){
					if(score<50){
						for(int i=0;i<=score;i++)
							current[0][i]++;
					}else{
						for(int i=0;i<50;i++)
							current[0][i]++;
					}
				}else{
					if(score<50){
						for(int i=0;i<=score;i++)
							current[1][i]++;
					}else{
						for(int i=0;i<50;i++)
							current[1][i]++;
					}
				}
			}
			
			if(current[0][0]+current[1][0]>3000){
				
				int [][] add = new int [2][50];
				for(int i=0;i<add.length;i++){
					System.arraycopy(current[i], 0, add[i], 0, add[i].length);
				}
				list.add(add);
				
				current = new int [2][50];
			}
		}
		
		list.add(current);
		
		System.out.println("listsize\t"+list.size()+"\n");
		int target = 0;
		int decoy = 0;
		
		for(int i=0;i<list.size();i++){
			
			int [][] count = list.get(i);
			for(int j=0;j<count[0].length;j++){
				double fdr = count[0][j]==0 ? 1 : Double.parseDouble(df4.format((double)count[1][j]/(double)count[0][j]));
				if(fdr<0.01){
					target += count[0][j];
					decoy += count[1][j];
					System.out.println(i+"\t"+j+"\t"+count[0][j]+"\t"+count[1][j]+"\t"+fdr);
					break;
				}
			}
		}
		
		double fdr = Double.parseDouble(df4.format((double)decoy/(double)target));
		System.out.println("total\tscore\t"+list.size()+"\t"+target+"\t"+decoy+"\t"+fdr);
	}
	
	private void groupFilter(){
		// charge, miss count, mod
		ArrayList <IPeptide> [][][] list = new ArrayList [4][3][2];
		for(int i=0;i<list.length;i++){
			for(int j=0;j<list[i].length;j++){
				for(int k=0;k<list[i][j].length;k++){
					list[i][j][k] = new ArrayList <IPeptide>();
				}
			}
		}
		
		IPeptide peptide;
		while((peptide=reader.getPeptide())!=null){
			
			int scannum = peptide.getScanNumBeg();
			int rank = peptide.getRank();
			int charge = peptide.getCharge();
			int miss = peptide.getMissCleaveNum();
			String sequence = peptide.getSequence();
			int mod = 0;
			char [] cs = sequence.toCharArray();
			for(int i=0;i<cs.length;i++){
				if(cs[i]<'A' || cs[i]>'Z'){
					if(cs[i]!='.'){
						mod = 1;
					}
				}
			}
			
			if(rank==1){
				if(charge>4){
					list[3][miss][mod].add(peptide);
				}else{
					list[charge-1][miss][mod].add(peptide);
				}
			}
		}
		
		int target = 0;
		int decoy = 0;
		
		for(int i=0;i<list.length;i++){
			for(int j=0;j<list[i].length;j++){
				for(int k=0;k<list[i][j].length;k++){
					if(list[i][j][k].size()>0){
						System.out.println((i+1)+"\t"+j+"\t"+k+"\t"+list[i][j][k].size());
						Iterator <IPeptide> it = list[i][j][k].iterator();
						int [][] count = new int [2][50];
						while(it.hasNext()){
							IPeptide pep = it.next();
							int score = (int) pep.getPrimaryScore();
							boolean tp = pep.isTP();
							
							if(tp){
								if(score<50){
									for(int l=0;l<=score;l++)
										count[0][l]++;
								}else{
									for(int l=0;l<50;l++)
										count[0][l]++;
								}
							}else{
								if(score<50){
									for(int l=0;l<=score;l++)
										count[1][l]++;
								}else{
									for(int l=0;l<50;l++)
										count[1][l]++;
								}
							}
						}
						
						for(int m=0;m<count[0].length;m++){
							double fdr = count[0][m]==0 ? 1 : Double.parseDouble(df4.format((double)count[1][m]/(double)count[0][m]));
							if(fdr<0.01){
								target += count[0][m];
								decoy += count[1][m];
								System.out.println(count[0][m]+"\t"+count[1][m]+"\t"+fdr);
								break;
							}
						}
					}
				}
			}
		}
		
		double fdr = decoy==0 ? 1 : Double.parseDouble(df4.format((double)decoy/(double)target));
		System.out.println(target+"\t"+decoy+"\t"+fdr);
	}

	public void close(){
		this.reader.close();
	}
	
	public void peakListTest(){
		
//		ArrayList <Integer> list = new ArrayList <Integer>();
		ArrayList <Double> [] lists = new ArrayList [5];
		for(int i=0;i<lists.length;i++){
			lists[i] = new ArrayList<Double>();
		}
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			if(pep.getRank()==1){
//			if(pep.getRank()==1 && pep.getPrimaryScore()>50){
				IMS2PeakList peaklist = reader.getPeakLists()[0];
//				list.add(peaklist.size());
				SpectrumDataPoint sp = new SpectrumDataPoint(peaklist);
				double [] dis = sp.getIntenDistribution();
				for(int i=0;i<dis.length;i++){
					lists[i].add(dis[i]);
				}
			}
		}
		System.out.println(lists[0].size());
//		System.out.println(MathTool.getMedian(list));
		for(int i=0;i<lists.length;i++){
			System.out.println(i+"\t"+MathTool.getAveInDouble(lists[i])+"\t"+MathTool.getMedianInDouble(lists[i]));
		}
		double [] dd = new double[5];
		for(int i=0;i<lists[0].size();i++){
			int score = (int) (lists[0].get(i)/0.05);
			if(score<5){
				dd[score]++;
			}else{
				dd[4]++;
			}
		}
		for(int i=0;i<5;i++)
			System.out.println((double)dd[i]/(double)lists[0].size());
	}
	
	public void peakIonTest() throws IOException{
		
		DecimalFormat df4 = DecimalFormats.DF0_4;
		PrintWriter pw = new PrintWriter("H:\\Validation\\Mann\\2025.txt");
		ArrayList <Double> countList = new ArrayList <Double>();
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		
		ArrayList <Double> [] lists = new ArrayList [10];
		for(int i=0;i<lists.length;i++){
			lists[i] = new ArrayList<Double>();
		}
		
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			if(pep.getRank()==1 && pep.getPrimaryScore()>20 && pep.getPrimaryScore()<25){
				
				IMS2PeakList peaklist = reader.getPeakLists()[0];
				Ions ions = aaf.fragment(pep.getPeptideSequence(), types,
				        true);
				
				PeakForMatch[] peaks = SpectrumMatcher.matchBY(peaklist, ions, 
						pep.getCharge(), SpectrumMatcher.DEFAULT_THRESHOLD);
								
				int [] count = new int [10];
				for(int i=0;i<peaks.length;i++){
					if(peaks[i].isMatched()){
						double inten = peaks[i].getIntensity();
						int id = (int) (inten*10);
						if(id<10){
							count[id]++;
						}else{
							count[9]++;
						}
					}
				}
				for(int i=0;i<10;i++){
					lists[i].add((double)count[i]/(double)peaks.length);
				}
//				double cr = (double)count/(double)peaks.length;
//				System.out.println(cr);
//				countList.add(cr);
			}
		}
		for(int i=0;i<10;i++)
		System.out.println(lists[i].size());
//		System.out.println("final\t"+MathTool.getAve(countList)+"\t"+MathTool.getMedian(countList));
		for(int i=0;i<lists[0].size();i++){
			String s = "";
			for(int j=0;j<10;j++){
				s+=df4.format(lists[j].get(i));
				s+="\t";
			}
			pw.write(s+"\n");
		}		
		pw.close();
	}
	
	public void peakCountTest() throws IOException{
		
		int [] count = new int [8];
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			int length = PeptideUtil.getSequenceLength(pep.getSequence());
			if(pep.getRank()==1 && length==6){
				
				IMS2PeakList peaklist = reader.getPeakLists()[0];
				int id = peaklist.getPeakArray().length/50;
				if(id<8){
					count[id]++;
				}else{
					count[7]++;
				}
			}
		}
		for(int i=0;i<8;i++){
			System.out.println(count[i]);
		}
	}

	public void pepLengthDis() throws IOException{
		
		ArrayList <IPeptide> [] peplist = new ArrayList [20];
		for(int i=0;i<peplist.length;i++){
			peplist[i] = new ArrayList <IPeptide>();
		}

		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			if(pep.getRank()==1){
				String seq = pep.getSequence();
				int length = PeptideUtil.getSequenceLength(seq);
				if(length<=6){
					peplist[0].add(pep);
				}else if(length>=25){
					peplist[19].add(pep);
				}else{
					peplist[length-6].add(pep);
				}
			}
		}
		
/*		int target = 0;
		int decoy = 0;
		for(int j=0;j<20;j++){
			int [][] count = new int [2][50];
			for(int k=0;k<peplist[j].size();k++){
				IPeptide peptide = peplist[j].get(k);
				int score = (int) peptide.getPrimaryScore();
				boolean tp = peptide.isTP();
				if(tp){
					if(score<50){
						for(int i=0;i<=score;i++)
							count[0][i]++;
					}else{
						for(int i=0;i<50;i++)
							count[0][i]++;
					}
				}else{
					if(score<50){
						for(int i=0;i<=score;i++)
							count[1][i]++;
					}else{
						for(int i=0;i<50;i++)
							count[1][i]++;
					}
				}
			}
			for(int i=0;i<50;i++){
				double fdr = count[0][i]==0 ? 0 : Double.parseDouble(df4.format((double)count[1][i]/(double)count[0][i]));
				if(fdr<0.01){
					System.out.println((j+1)+"\t"+(i+1)+"\t"+fdr+"\t"+count[0][i]+"\t"+count[1][i]+"\t"+count[0][0]+"\t"+count[1][0]);
					target += count[0][i];
					decoy += count[1][i];
					break;
				}
			}
		}
		double fdr = Double.parseDouble(df4.format((double)decoy/(double)target));
		System.out.println("total\tscore\t"+target+"\t"+decoy+"\t"+fdr);
*/
		for(int i=0;i<peplist[0].size();i++){
			IPeptide peptide = peplist[0].get(i);
			if(peptide.getPrimaryScore()<2){
				System.out.println(peptide.isTP()+"\t"+peptide.getSequence()+"\t"+peptide.getPrimaryScore()+"\t"+peptide.getScanNumBeg());
			}
		}
	}

	public void peakIonTest2() throws IOException{
		
		DecimalFormat df4 = DecimalFormats.DF0_4;
		ArrayList <Double> countList = new ArrayList <Double>();
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		
		ArrayList <Double> [] lists = new ArrayList [10];
		for(int i=0;i<lists.length;i++){
			lists[i] = new ArrayList<Double>();
		}
		
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			if(pep.getRank()==1 && pep.getPrimaryScore()>20 && pep.getPrimaryScore()<25){
				
				IMS2PeakList peaklist = reader.getPeakLists()[0];
				Ions ions = aaf.fragment(pep.getPeptideSequence(), types,
				        true);
				
				PeakForMatch[] peaks = SpectrumMatcher.matchBY(peaklist, ions, 
						pep.getCharge(), SpectrumMatcher.DEFAULT_THRESHOLD);
				
				
								
				int [] count = new int [10];
				for(int i=0;i<peaks.length;i++){
					if(peaks[i].isMatched()){
						double inten = peaks[i].getIntensity();
						int id = (int) (inten*10);
						if(id<10){
							count[id]++;
						}else{
							count[9]++;
						}
					}
				}
				for(int i=0;i<10;i++){
					lists[i].add((double)count[i]/(double)peaks.length);
				}
			}
		}
	}
	
	public void peakIonCorrTest2() throws IOException{
		
		DecimalFormat df4 = DecimalFormats.DF0_4;
		ArrayList <Double> countList = new ArrayList <Double>();
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};

		ArrayList <double[][]> matrixList = new ArrayList <double[][]>();
		ArrayList <double[][]> lowmatrixList = new ArrayList <double[][]>();
		ArrayList <Float> scorelist = new ArrayList <Float>();
		ArrayList <Boolean> tlist = new ArrayList <Boolean>();
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			String seq = pep.getSequence();
			int length = PeptideUtil.getSequenceLength(seq);
			if(length!=6)
				continue;
			
			if(pep.getRank()==1 && pep.getPrimaryScore()>50){
				
				IMS2PeakList peaklist = reader.getPeakLists()[0];
				Ions ions = aaf.fragment(pep.getPeptideSequence(), types,
				        true);
				
				PeakForMatch[] peaks = SpectrumMatcher.matchBY(peaklist, ions, 
						pep.getCharge(), SpectrumMatcher.DEFAULT_THRESHOLD);
				
				//b1, b2, y1, y2
				double [][] dd = new double[4][6];

				for(int i=0;i<peaks.length;i++){
					if(peaks[i].isMatched()){
						double inten = peaks[i].getIntensity();
						int [] mt = peaks[i].getMatchedTypes();
						for(int j=0;j<mt.length;j++){
							Ion [] is = peaks[i].getMatchIons(mt[j]);
							for(int k=0;k<is.length;k++){
								if(is[k]!=null){
									int it = is[k].getType();
									int series = is[k].getSeries();
									if(it==Ion.TYPE_B){
										if(k==0){
											dd[0][series-1]+=inten;
										}else if(k==1){
											dd[1][series-1]+=inten;
										}
									}else if(it==Ion.TYPE_Y){
										if(k==0){
											dd[2][series-1]+=inten;
										}else if(k==1){
											dd[3][series-1]+=inten;
										}
									}
								}
							}
						}
					}
				}
				matrixList.add(dd);
//				tlist.add(pep.isTP());
//				scorelist.add(pep.getPrimaryScore());
				
			}else if(pep.getRank()==1 && pep.getPrimaryScore()>20 && pep.getPrimaryScore()<21){

				IMS2PeakList peaklist = reader.getPeakLists()[0];
				Ions ions = aaf.fragment(pep.getPeptideSequence(), types,
				        true);
				
				PeakForMatch[] peaks = SpectrumMatcher.matchBY(peaklist, ions, 
						pep.getCharge(), SpectrumMatcher.DEFAULT_THRESHOLD);
				
				//b1, b2, y1, y2
				double [][] dd = new double[4][6];

				for(int i=0;i<peaks.length;i++){
					if(peaks[i].isMatched()){
						double inten = peaks[i].getIntensity();
						int [] mt = peaks[i].getMatchedTypes();
						for(int j=0;j<mt.length;j++){
							Ion [] is = peaks[i].getMatchIons(mt[j]);
							for(int k=0;k<is.length;k++){
								if(is[k]!=null){
									int it = is[k].getType();
									int series = is[k].getSeries();
									if(it==Ion.TYPE_B){
										if(k==0){
											dd[0][series-1]+=inten;
										}else if(k==1){
											dd[1][series-1]+=inten;
										}
									}else if(it==Ion.TYPE_Y){
										if(k==0){
											dd[2][series-1]+=inten;
										}else if(k==1){
											dd[3][series-1]+=inten;
										}
									}
								}
							}
						}
					}
				}
				lowmatrixList.add(dd);
				tlist.add(pep.isTP());
				scorelist.add(pep.getPrimaryScore());
			}
		}
		System.out.println(lowmatrixList.size());
		for(int i=0;i<lowmatrixList.size();i++){
			double corr = MathTool.getCorr2(matrixList.get(0), lowmatrixList.get(i));
			System.out.println(corr+"\t"+scorelist.get(i)+"\t"+tlist.get(i));
		}
	}
	
	public void clustering() throws IOException{
		
		DecimalFormat df4 = DecimalFormats.DF0_4;

		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};

		double [][] total = new double [4][6];
		HashMap <Integer, IPeptide> map = new HashMap <Integer, IPeptide>();

		DataPoint totalDp = new TwoDArraysDataPoint(total);
		ArrayList <DataPoint> matrixList = new ArrayList <DataPoint>();
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			String seq = pep.getSequence();
			int scannum = pep.getScanNumBeg();
			int length = PeptideUtil.getSequenceLength(seq);
			if(length!=6)
				continue;
			
			if(pep.getRank()==1){
				
				map.put(scannum, pep);
				IMS2PeakList peaklist = reader.getPeakLists()[0];
				Ions ions = aaf.fragment(pep.getPeptideSequence(), types,
				        true);
				
				PeakForMatch[] peaks = SpectrumMatcher.matchBY(peaklist, ions, 
						pep.getCharge(), SpectrumMatcher.DEFAULT_THRESHOLD);
				
				//b1, b2, y1, y2
				double [][] dd = new double[4][6];

				for(int i=0;i<peaks.length;i++){
					if(peaks[i].isMatched()){
						double inten = peaks[i].getIntensity();
						int [] mt = peaks[i].getMatchedTypes();
						for(int j=0;j<mt.length;j++){
							Ion [] is = peaks[i].getMatchIons(mt[j]);
							for(int k=0;k<is.length;k++){
								if(is[k]!=null){
									int it = is[k].getType();
									int series = is[k].getSeries();
									if(it==Ion.TYPE_B){
										if(k==0){
											dd[0][series-1]+=inten;
										}else if(k==1){
											dd[1][series-1]+=inten;
										}
									}else if(it==Ion.TYPE_Y){
										if(k==0){
											dd[2][series-1]+=inten;
										}else if(k==1){
											dd[3][series-1]+=inten;
										}
									}
								}
							}
						}
					}
				}
				DataPoint dp = new TwoDArraysDataPoint(dd, scannum);
				matrixList.add(dp);
				totalDp = totalDp.plus(dp);
			}
		}
		
		DataPoint aveDp = totalDp.divide(5);
		DataPoint [] cenDatas = new DataPoint[5];
		for(int i=0;i<5;i++){
			cenDatas[i] = aveDp.multiply(i+1);
//			System.out.println(cenDatas[i]);
		}
		System.out.println(matrixList.size());

		ClusterAnalysis ca = new ClusterAnalysis(matrixList, cenDatas);
		ca.analysis();
		Cluster [] clusters = ca.getClusters();
		for(int i=0;i<clusters.length;i++){
			
			System.out.print(i+"\t"+clusters[i].getNumOfDP()+"\t");
			ArrayList <DataPoint> list = clusters[i].getDataList();
			for(int k=0;k<50;k++){
				double totalScore = 0;
				int tc = 0;
				int dc = 0;
				for(int j=0;j<list.size();j++){
					DataPoint dp = list.get(j);
					IPeptide pepj = map.get(dp.getId());
					totalScore += pepj.getPrimaryScore();
					if(pepj.getPrimaryScore()>k){
						if(pepj.isTP()){
							tc++;
						}else{
							dc++;
						}
					}
				}
				double fdr = (double)dc/(double)tc;
				if(fdr<0.01){
					System.out.println(totalScore/(double)list.size()+"\t"+k+"\t"+tc+"\t"+dc+"\t"+fdr);
					break;
				}
			}
		}
	}

	public void spectrumClustering() throws Exception{

		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.01, 0.01);
		
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);

		HashMap <Integer, IPeptide> map = new HashMap <Integer, IPeptide>();
		int count6 = 0;
		
		Instances instances = creator.createInstances("");
		
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			String seq = pep.getSequence();
			int scannum = pep.getScanNumBeg();
			int length = PeptideUtil.getSequenceLength(seq);
//			if(length!=6)
//				continue;
			
			if(pep.getRank()==1 && pep.getCharge()==3 && pep.getMissCleaveNum()==0){
				
				count6++;
				map.put(scannum, pep);
				IMS2PeakList peaklist = reader.getPeakLists()[0];
				MyInstance mi = creator.createInstance(pep, peaklist);
				instances.add(mi);
			}
		}
		
		int clustercount = count6/300;
		System.out.println(instances.numInstances()+"\t"+map.size()+"\t"+count6+"\t"+clustercount);
//		SpectralClusterer sc = new SpectralClusterer();
//		sc.setDistanceFunction(new BYMatrixDistanceFunction());
//		sc.setDistanceFunction(new BYMatrixDistanceFunction());
//		sc.buildClusterer(instances);
//		XMeans cluster = new XMeans();		
		SimpleKMeans cluster = new SimpleKMeans();
		((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
//		HierarchicalClusterer cluster = new HierarchicalClusterer();
//		FarthestFirst cluster = new FarthestFirst();
		cluster.setNumClusters(clustercount);
//		cluster.setMinNumClusters(10);
//		cluster.setMaxNumClusters(14);
		cluster.buildClusterer(instances);
		System.out.println("count\t"+cluster.numberOfClusters());

		ArrayList <IPeptide> [] peplists = new ArrayList [cluster.numberOfClusters()];
		for(int i=0;i<peplists.length;i++){
			peplists[i] = new ArrayList <IPeptide>();
		}
		for(int i=0;i<instances.numInstances();i++){
			Instance ins = instances.instance(i);
			int type = cluster.clusterInstance(ins);
			int id = ((MyInstance)ins).getIdx();
			IPeptide peptide = map.get(id);
			peplists[type].add(peptide);
		}
		
		int [] scoreThres = new int[peplists.length];
		int totalTc = 0;
		int totalDc = 0;
		for(int i=0;i<peplists.length;i++){
			
			ArrayList <IPeptide> list = peplists[i];
			
			for(int k=0;k<50;k++){

				double totalScore = 0;
				int tc = 0;
				int dc = 0;
				int tc0 = 0;
				int dc0 = 0;
				for(int j=0;j<list.size();j++){
					IPeptide pepj = peplists[i].get(j);
					totalScore += pepj.getPrimaryScore();
					if(pepj.isTP()){
						tc0++;
						if(pepj.getPrimaryScore()>k)
							tc++;
					}else{
						dc0++;
						if(pepj.getPrimaryScore()>k)
							dc++;
					}
				}
				double ratio0 = (double)dc0/(double)tc0;
				double fdr = (double)dc/(double)tc;
				if(fdr<0.05){
					System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"--\t--"
							+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
					totalTc += tc;
					totalDc += dc;
					scoreThres[i] = k;
					break;
				}
			}
		}
		System.out.println(totalTc+"\t"+totalDc);

		for(int k=0;k<50;k++){
			
			int totalTc0 = 0;
			int totalDc0 = 0;
			
			for(int i=0;i<peplists.length;i++){
					
				ArrayList <IPeptide> list = peplists[i];
				
				double totalScore = 0;

				for(int j=0;j<list.size();j++){
					
					IPeptide pepj = peplists[i].get(j);
					totalScore += pepj.getPrimaryScore();
					if(pepj.isTP()){
						if(pepj.getPrimaryScore()>k && pepj.getPrimaryScore()>scoreThres[i])
							totalTc0++;
					}else{
						if(pepj.getPrimaryScore()>k && pepj.getPrimaryScore()>scoreThres[i])
							totalDc0++;
					}
				}
			}
			
			double fdr = (double)totalDc0/(double)totalTc0;
			
			if(fdr<0.01){
				
				System.out.println(k+"\t"+totalTc0+"\t"+totalDc0+"\t"+fdr);
				break;
			}
		}
	}

	public void randomClustering() throws Exception{

		int idx = 0;

		ArrayList <IPeptide> [] peplists = new ArrayList [1];
		for(int i=0;i<peplists.length;i++){
			peplists[i] = new ArrayList <IPeptide>();
		}
		
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			String seq = pep.getSequence();
			int length = PeptideUtil.getSequenceLength(seq);
//			if(length!=6)
//				continue;
			
			if(pep.getRank()==1 && pep.getCharge()==2 && pep.getMissCleaveNum()==0){
				peplists[idx].add(pep);
				idx++;
				if(idx==peplists.length)
					idx=0;
			}
		}

		int totalTc = 0;
		int totalDc = 0;
		for(int i=0;i<peplists.length;i++){
			
			ArrayList <IPeptide> list = peplists[i];
			
			for(int k=0;k<50;k++){
				double totalScore = 0;
				int tc = 0;
				int dc = 0;
				int tc0 = 0;
				int dc0 = 0;
				for(int j=0;j<list.size();j++){
					IPeptide pepj = peplists[i].get(j);
					totalScore += pepj.getPrimaryScore();
					if(pepj.isTP()){
						tc0++;
						if(pepj.getPrimaryScore()>k)
							tc++;
					}else{
						dc0++;
						if(pepj.getPrimaryScore()>k)
							dc++;
					}
				}
				double ratio0 = (double)dc0/(double)tc0;
				double fdr = (double)dc/(double)tc;
				if(fdr<0.01){
					System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"--\t--"
							+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
					totalTc += tc;
					totalDc += dc;
					break;
				}
			}
		}
		System.out.println(totalTc+"\t"+totalDc);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		long begin = System.currentTimeMillis();
		
		String file = "H:\\Validation\\Mann\\F002836.dat.ppl";
		MascotTest test = new MascotTest(file);
//		test.testRandem();
//		test.testline();
//		test.groupFilter();
//		test.peakListTest();
//		test.peakIonTest();
//		test.peakCountTest();
//		test.clustering();
//		test.test();
		test.spectrumClustering();
//		test.randomClustering();
		test.close();
		
		long end = System.currentTimeMillis();
		System.out.println((end-begin)/1000);
	}

}
