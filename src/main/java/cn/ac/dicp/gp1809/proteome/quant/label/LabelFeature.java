/* 
 ******************************************************************************
 * File:Feature.java * * * Created on 2010-3-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * A feature is a set of pixels in the same scan which is possible an isotope cluster.
 * 
 * @author ck
 * @version 2010-3-23, 13:47:03
 */
public class LabelFeature implements Comparable <LabelFeature> {
	
	/**
	 * nbt.1511-S1, p9
	 */
	private final static double dm = 1.00286864;
	private static int isotopeCount = 4;
	
	private int scanNum;
	private int charge;
	private double rt;

	private double [] monoMasses;
	private double [] masses;
	private double [] intens;
	private int [] labelId;
	private double [] ratios;
	private boolean miss;
	private int missCount;

	public LabelFeature(int scanNum, int charge, double [] monoMasses){
		this.scanNum = scanNum;
		this.charge = charge;
		this.monoMasses = monoMasses;
	}
	
	public LabelFeature(int scanNum, int charge, double [] monoMasses, double rt){
		this.scanNum = scanNum;
		this.charge = charge;
		this.monoMasses = monoMasses;
		this.rt = rt;
	}
	
	public void match(IPeak [] peaks, double [] intenMinusRatio, double tolerance, int [] missNum, boolean [] misses){

		this.labelId = new int[monoMasses.length];
		this.labelId[0] = 0;
		ArrayList <Double> list = new ArrayList <Double>();
		list.add(monoMasses[0]);
		
		double max3 = monoMasses[monoMasses.length-1]+dm*3.0/(double)charge;
		
L:		for(int i=1;;i++){

			double mass = list.get(list.size()-1)+dm/(double)charge;
			boolean add = false;
	
			for(int j=1;j<labelId.length;j++){
				if(Math.abs(mass-monoMasses[j])<0.1){
					labelId[j] = i;
					add = true;
					list.add(monoMasses[j]);
					break;
				}else if(Math.abs(mass-max3)<0.1){
					list.add(mass);
					break L;
				}
			}
			if(!add) list.add(mass);
//			if(list.size()>100){
//				System.out.println(monoMasses[0]+"\t"+(monoMasses[monoMasses.length-1]-monoMasses[0])+"\t"+max3+"\t"+charge+"\t"+list);		
//				System.exit(0);
//			}
		}

		int min = 100;
		double max = 0;
		this.masses = new double[list.size()];
		double [] intens = new double[list.size()];
		for(int i=0;i<labelId.length-1;i++){
			int diffi = (labelId[i+1]-labelId[i])>=isotopeCount ? isotopeCount : (labelId[i+1]-labelId[i]);
			for(int j=labelId[i];j<labelId[i]+diffi;j++){
				this.masses [j] = list.get(j);
			}
			if(min>diffi) min = diffi;
		}

//		for(int i=0;i<labelId.length;i++)System.out.print(labelId[i]+"\t");
//		System.out.println(labelId[labelId.length-1]+"\t"+min+"\t"+scanNum);
		double[] normalFactor = new double[min];
		for(int i=0;i<min;i++){
			normalFactor[i] = intenMinusRatio[i];
		}
		for(int j=labelId[labelId.length-1];j<labelId[labelId.length-1]+min;j++){
			this.masses [j] = list.get(j);
			if(masses[j]>max) max = masses[j];
		}
//		System.out.println(Arrays.toString(masses));
		IPeak findpeak = new cn.ac.dicp.gp1809.proteome.spectrum.Peak(masses[0]-tolerance, 0d);
//System.out.println(findpeak+"\t"+peaks.length);
		int index = Arrays.binarySearch(peaks, findpeak);
		if(index<0){
			index = -index-1;
		}else if(index >= peaks.length){
			return;
		}
		
//		System.out.print(scanNum+"\t"+tolerance+"\t"+Arrays.toString(masses)+"\t"+findpeak+"\t");
		for(int i=index;i<peaks.length;i++){
			double mass = peaks[i].getMz();
			double inten = peaks[i].getIntensity();
			for(int j=0;j<masses.length;j++){
				if(Math.abs(masses[j]-mass)<tolerance && inten>intens[j]){
					intens[j] = inten;
//					System.out.print(intens[j]+"\t");
				}
			}
			if(mass-max>tolerance) break;
		}
//		System.out.print("\n");
//System.out.println(scanNum+"\t"+Arrays.toString(intens));
		
		if(min>2){
			for(int i=0;i<labelId.length;i++){
				double [] norinten = new double[min];
				double maxj = 0;
				double minj = Double.MAX_VALUE;
				int countj = 0;
				for(int j=0;j<min;j++){
					if(intens[labelId[i]+j]>0){
						norinten[j] = intens[labelId[i]+j]/normalFactor[j];
						if(norinten[j]>maxj) maxj = norinten[j];
						if(norinten[j]<minj) minj = norinten[j];
						countj++;
					}
				}
				if(countj<3) continue;
				if(minj*4>maxj) continue;
				
				int maxcountj = 0;
				int mincountj = 0;
				for(int j=0;j<min;j++){
					if(norinten[j]>0){
						if(norinten[j]==maxj || norinten[j]==minj) continue;
						if(norinten[j]*4.0>maxj	) maxcountj++;
						if(norinten[j]/4.0<minj) mincountj++;
					}
				}
				if(maxcountj>mincountj){
					int minid = -1;
					double totalj = 0;
					for(int j=0;j<min;j++){
						if(norinten[j]>0){
							if(norinten[j]==minj){
								minid = j;
							}else{
								totalj += norinten[j];
							}
						}
					}
					intens[labelId[i]+minid] = totalj/(double)(countj-1)*normalFactor[minid];
				}else if(maxcountj<mincountj){
					int maxid = -1;
					double totalj = 0;
					for(int j=0;j<min;j++){
						if(norinten[j]>0){
							if(norinten[j]==maxj){
								maxid = j;
							}else{
								totalj += norinten[j];
							}
						}
					}
					intens[labelId[i]+maxid] = totalj/(double)(countj-1)*normalFactor[maxid];
				}else{
					continue;
				}
			}
		}
//System.out.println(scanNum+"\t"+Arrays.toString(intens));
		for(int i=1;i<labelId.length;i++){
			double minus = 0;
			if(intens[labelId[i-1]]==0){
				if(intens[labelId[i-1]+1]==0){
					minus = 0;
				}else{
					minus = intens[labelId[i-1]+1]/intenMinusRatio[1];
				}
			}else{
				if(intens[labelId[i-1]+1]==0){
					minus = intens[labelId[i-1]]/intenMinusRatio[0];
				}else{
					minus = (intens[labelId[i-1]]/intenMinusRatio[0]+intens[labelId[i-1]+1]/intenMinusRatio[1])/2.0;
				}
			}
//System.out.println("147\t"+scanNum+"\t"+i+"\t"+minus);
			for(int j=labelId[i];j<labelId[i]+min;j++){
				if(j-labelId[i-1]<6){
					intens[j] = intens[j]-minus*intenMinusRatio[j-labelId[i-1]];
					if(intens[j]<0) intens[j] = 0;
				}
			}
		}
		
//System.out.print(scanNum+"\t");
		double [] realInten = new double [labelId.length];
		for(int i=0;i<labelId.length;i++){
			
			if(misses[i]){
				realInten[i] = 0;
				missCount++;
				continue;
			}

			double [] total = new double [min];
			double noZero = 0;
			for(int j=0;j<min;j++){
//System.out.print(intens[labelId[i]+j]+"\t");
				if(intens[labelId[i]+j]>0){
					total[j] = intens[labelId[i]+j]/intenMinusRatio[j];
					noZero++;
				}
			}

			if(noZero<min/2){
				
				realInten[i] = 0;
				missNum[i]++;
				if(missNum[i]==2){
					misses[i] = true;
				}
				
			}else{
				
				double average = MathTool.getTotal(total)/(double)noZero;
				for(int j=0;j<min;j++){
					if(intens[labelId[i]+j]>0){
						realInten[i]+= intens[labelId[i]+j];
					}else{
						realInten[i]+= average*intenMinusRatio[j];
					}
				}
				
				if(realInten[i]==0){
					missNum[i]++;
					if(missNum[i]==2){
						misses[i] = true;
					}
				}
			}
//			System.out.println();
//			System.out.print(noZero+"\t"+realInten[i]+"\t");
			if(misses[i]){
				missCount++;
			}
		}
//System.out.println(Arrays.toString(intens)+"\t"+Arrays.toString(realInten));		
		this.intens = realInten;
		this.ratios = new double[labelId.length*(labelId.length-1)];
		int idcount = 0;
//		System.out.print("\n");
//		System.out.print(scanNum+"\t");
		for(int i=0;i<realInten.length;i++){
			for(int j=i+1;j<realInten.length;j++){
				if(realInten[j]*realInten[i]!=0){
					ratios[idcount] = realInten[j]/realInten[i];
					ratios[idcount+1] = realInten[i]/realInten[j];
				}
				idcount+=2;
			}
		}
//		System.out.println(scanNum+"\t"+Arrays.toString(ratios));
		int least = (labelId.length%2==0) ? labelId.length/2 : labelId.length/2+1;
//		this.miss = (missCount>=least);
		this.miss = (missCount>=(labelId.length-1));
	}
	
	public void match(IPeak [] peaks, double [] intenMinusRatio, double tolerance, ArrayList <Double> [] taillist,
			int [] tailNum, Double tailLimit, int [] missNum, boolean [] misses){

		this.labelId = new int[monoMasses.length];
		this.labelId[0] = 0;
		ArrayList <Double> list = new ArrayList <Double>();
		list.add(monoMasses[0]);
		
		double max3 = monoMasses[monoMasses.length-1]+dm*3.0/(double)charge;
		
L:		for(int i=1;;i++){

			double mass = list.get(list.size()-1)+dm/(double)charge;
			boolean add = false;
	
			for(int j=1;j<labelId.length;j++){
				if(Math.abs(mass-monoMasses[j])<0.1){
					labelId[j] = i;
					add = true;
					list.add(monoMasses[j]);
					break;
				}else if(Math.abs(mass-max3)<0.1){
					list.add(mass);
					break L;
				}
			}
			if(!add) list.add(mass);
//			if(list.size()>100){
//				System.out.println(monoMasses[0]+"\t"+(monoMasses[monoMasses.length-1]-monoMasses[0])+"\t"+max3+"\t"+charge+"\t"+list);		
//				System.exit(0);
//			}
		}

		int min = 100;
		double max = 0;
		this.masses = new double[list.size()];
		double [] intens = new double[list.size()];
		
		for(int i=0;i<labelId.length-1;i++){
			int diffi = (labelId[i+1]-labelId[i])>=isotopeCount ? isotopeCount : (labelId[i+1]-labelId[i]);
			for(int j=labelId[i];j<labelId[i]+diffi;j++){
				this.masses [j] = list.get(j);
			}
			if(min>diffi) min = diffi;
		}
//		for(int i=0;i<labelId.length;i++)System.out.print(labelId[i]+"\t");
//		System.out.println(labelId[labelId.length-1]+"\t"+min+"\t"+sequence);
		for(int j=labelId[labelId.length-1];j<labelId[labelId.length-1]+min;j++){
			this.masses [j] = list.get(j);
			if(masses[j]>max) max = masses[j];
		}

		IPeak findpeak = new cn.ac.dicp.gp1809.proteome.spectrum.Peak(masses[0]-tolerance, 0d);
//System.out.println(findpeak+"\t"+peaks.length);
		int index = Arrays.binarySearch(peaks, findpeak);
		if(index<0){
			index = -index-1;
		}else if(index >= peaks.length){
			return;
		}
		
		for(int i=index;i<peaks.length;i++){
			double mass = peaks[i].getMz();
			double inten = peaks[i].getIntensity();
			if(inten<0) inten = -inten;
			for(int j=0;j<masses.length;j++){
				if(Math.abs(masses[j]-mass)<tolerance && inten>intens[j]){
					intens[j] = inten;
				}
			}
			if(mass-max>tolerance) break;
		}
		
		double[] normalFactor = new double[min];
		for(int i=0;i<min;i++){
			normalFactor[i] = intenMinusRatio[i];
		}
		
		if (min>2) {
			for(int i=0;i<labelId.length;i++){
				double [] norinten = new double[min];
				double maxj = 0;
				double minj = Double.MAX_VALUE;
				int countj = 0;
				for(int j=0;j<min;j++){
					if(intens[labelId[i]+j]>0){
						norinten[j] = intens[labelId[i]+j]/normalFactor[j];
						if(norinten[j]>maxj) maxj = norinten[j];
						if(norinten[j]<minj) minj = norinten[j];
						countj++;
					}
				}
				if(countj<3) continue;
				if(minj*4>maxj) continue;
				
				int maxcountj = 0;
				int mincountj = 0;
				for(int j=0;j<min;j++){
					if(norinten[j]>0){
						if(norinten[j]==maxj || norinten[j]==minj) continue;
						if(norinten[j]*4.0>maxj	) maxcountj++;
						if(norinten[j]/4.0<minj) mincountj++;
					}
				}
				if(maxcountj>mincountj){
					int minid = -1;
					double totalj = 0;
					for(int j=0;j<min;j++){
						if(norinten[j]>0){
							if(norinten[j]==minj){
								minid = j;
							}else{
								totalj += norinten[j];
							}
						}
					}
					intens[labelId[i]+minid] = totalj/(double)(countj-1)*normalFactor[minid];
				}else if(maxcountj<mincountj){
					int maxid = -1;
					double totalj = 0;
					for(int j=0;j<min;j++){
						if(norinten[j]>0){
							if(norinten[j]==maxj){
								maxid = j;
							}else{
								totalj += norinten[j];
							}
						}
					}
					intens[labelId[i]+maxid] = totalj/(double)(countj-1)*normalFactor[maxid];
				}else{
					continue;
				}
			}
		}
		
		for(int i=1;i<labelId.length;i++){
			double minus = 0;
			if(intens[labelId[i-1]]==0){
				if(intens[labelId[i-1]+1]==0){
					minus = 0;
				}else{
					minus = intens[labelId[i-1]+1]/intenMinusRatio[1];
				}
			}else{
				if(intens[labelId[i-1]+1]==0){
					minus = intens[labelId[i-1]]/intenMinusRatio[0];
				}else{
					minus = (intens[labelId[i-1]]/intenMinusRatio[0]+intens[labelId[i-1]+1]/intenMinusRatio[1])/2.0;
				}
			}
//System.out.println("147\t"+scanNum+"\t"+i+"\t"+minus);
			for(int j=labelId[i];j<labelId[i]+min;j++){
				if(j-labelId[i-1]<6){
					intens[j] = intens[j]-minus*intenMinusRatio[j-labelId[i-1]];
					if(intens[j]<0) intens[j] = 0;
				}
			}
		}
//		System.out.println(scanNum+"\t"+Arrays.toString(intens));
//		System.out.println(scanNum+"\tmisses\t"+Arrays.toString(misses));
//		System.out.println(scanNum+"\ttail\t"+Arrays.toString(tailNum));
		double [] realInten = new double [labelId.length];
		int totalTail = 0;
		for(int i=0;i<labelId.length;i++){
			boolean b0 = false;
			boolean b1 = false;
			boolean b2 = false;
			for(int j=0;j<min;j++){
				if(intens[labelId[i]+j]>0){
					realInten[i]+= intens[labelId[i]+j];
					if(j==0) b0 = true;
					else if(j==1) b1 = true;
					else if(j==2) b2 = true;
				}
			}
//			System.out.println("167\t"+i+"\t"+"\t"+realInten[i]);
			if(misses[i]){
				realInten[i] = 0;
			}
			if(min==2){
				if(!(b0 && b1)){
					realInten[i] = 0;
				}
			}else{
				if(!(b0 && b1 && b2)){
					realInten[i] = 0;
				}
			}
			if(realInten[i]==0){
				missNum[i]++;
				if(missNum[i]==3){
					misses[i] = true;
				}
			}else{
				if(taillist[i].size()==6){
					double oldInten = MathTool.getAveInDouble(taillist[i]);
					taillist[i].remove(0);
					taillist[i].add(realInten[i]);
					double newInten = MathTool.getAveInDouble(taillist[i]);
//					System.out.print(MathTool.getRSD(taillist[i])+"\t");
					if(MathTool.getRSDInDouble(taillist[i])<tailLimit && newInten*0.9<oldInten){
						tailNum[i]++;
					}else{
						if(tailNum[i]>0 && tailNum[i]<10)
							tailNum[i]--;
					}
					if(tailNum[i]>=15){
						misses[i] = true;
					}else if(tailNum[i]>=12 && tailNum[i]<15){
						totalTail++;
					}
				}else{
					taillist[i].add(realInten[i]);
				}
			}
			if(misses[i]){
				missCount++;
			}
		}
		this.intens = realInten;
		this.ratios = new double[labelId.length*(labelId.length-1)];
		int idcount = 0;
		for(int i=0;i<realInten.length;i++){
			for(int j=i+1;j<realInten.length;j++){
				if(realInten[j]*realInten[i]!=0){
					ratios[idcount] = realInten[j]/realInten[i];
					ratios[idcount+1] = realInten[i]/realInten[j];
				}
				idcount+=2;
			}
		}
		int least = (labelId.length%2==0) ? labelId.length/2 : labelId.length/2+1;
//		this.miss = (missCount>=least);
		this.miss = (missCount>=(labelId.length-1));
		if(totalTail>labelId.length/2){
			tailLimit += 0.05;
		}
		
//		System.out.println("\n"+scanNum+"\t"+tailLimit+"\t"+miss+"\trelainten\t"+Arrays.toString(realInten));
	}
	
	public void match(IPeak [] peaks, double tolerance, int [] missNum, boolean [] misses){

		this.labelId = new int[monoMasses.length];
		this.labelId[0] = 0;
		ArrayList <Double> list = new ArrayList <Double>();
		list.add(monoMasses[0]);
		
		double max3 = monoMasses[monoMasses.length-1]+dm*3.0/(double)charge;
		
L:		for(int i=1;;i++){

			double mass = list.get(list.size()-1)+dm/(double)charge;
			boolean add = false;
	
			for(int j=1;j<labelId.length;j++){
				if(Math.abs(mass-monoMasses[j])<0.05){
					labelId[j] = i;
					add = true;
					list.add(monoMasses[j]);
					break;
				}else if(Math.abs(mass-max3)<0.05){
					list.add(mass);
					break L;
				}
			}
			if(!add) list.add(mass);
		}

		int min = 100;
		double max = 0;
		this.masses = new double[list.size()];
		double [] intens = new double[list.size()];
		
		for(int i=0;i<labelId.length-1;i++){
			int diffi = (labelId[i+1]-labelId[i])>=isotopeCount ? isotopeCount : (labelId[i+1]-labelId[i]);
			for(int j=labelId[i];j<labelId[i]+diffi;j++){
				this.masses [j] = list.get(j);
			}
			if(min>diffi) min = diffi;
		}
//		for(int i=0;i<labelId.length;i++)System.out.print(labelId[i]+"\t");
//		System.out.println(labelId[labelId.length-1]+"\t"+min+"\t"+sequence);
		for(int j=labelId[labelId.length-1];j<labelId[labelId.length-1]+min;j++){
			this.masses [j] = list.get(j);
			if(masses[j]>max) max = masses[j];
		}

		IPeak findpeak = new cn.ac.dicp.gp1809.proteome.spectrum.Peak(masses[0]-tolerance, 0d);
//System.out.println(findpeak+"\t"+peaks.length);
		int index = Arrays.binarySearch(peaks, findpeak);
		if(index<0){
			index = -index-1;
		}else if(index >= peaks.length){
			return;
		}

		for(int i=index;i<peaks.length;i++){
			double mass = peaks[i].getMz();
			double inten = peaks[i].getIntensity();
			if(inten<0) inten = -inten;
			for(int j=0;j<masses.length;j++){
				if(Math.abs(masses[j]-mass)<tolerance && inten>intens[j]){
					intens[j] = inten;
				}
			}
			if(mass-max>tolerance) break;
		}

		double [] realInten = new double [labelId.length];
		for(int i=0;i<labelId.length;i++){
			boolean b0 = false;
			boolean b1 = false;
			boolean b2 = false;
			double [] tempinten = new double[min];
			for(int j=0;j<min;j++){
				tempinten[j] = intens[labelId[i]+j];
				if(intens[labelId[i]+j]>0){
					realInten[i]+= intens[labelId[i]+j];
					if(j==0) b0 = true;
					else if(j==1) b1 = true;
					else if(j==2) b2 = true;
				}
			}

			if(misses[i]){
				realInten[i] = 0;
			}
			if(min==2){
				if(!(b0 && b1)){
					realInten[i] = 0;
				}
			}else{
				if(!(b0 && b1 && b2)){
					realInten[i] = 0;
				}
			}
			if(realInten[i]==0){
				missNum[i]++;
				if(missNum[i]==3){
					misses[i] = true;
				}
			}
			if(misses[i]){
				missCount++;
			}
		}

		this.intens = realInten;
		this.ratios = new double[labelId.length*(labelId.length-1)];
		int idcount = 0;
		for(int i=0;i<realInten.length;i++){
			for(int j=i+1;j<realInten.length;j++){
				if(realInten[j]*realInten[i]!=0){
					ratios[idcount] = realInten[j]/realInten[i];
					ratios[idcount+1] = realInten[i]/realInten[j];
				}
				idcount+=2;
			}
		}
		int least = (labelId.length%2==0) ? labelId.length/2 : labelId.length/2+1;
//		this.miss = (missCount>=least);
		this.miss = (missCount>=(labelId.length-1));
	}

	public void match(IPeak [] peaks, double tolerance, ArrayList <Double> [] taillist,
			int [] tailNum, int [] missNum, boolean [] misses){

		this.labelId = new int[monoMasses.length];
		this.labelId[0] = 0;
		ArrayList <Double> list = new ArrayList <Double>();
		list.add(monoMasses[0]);
		
		double max3 = monoMasses[monoMasses.length-1]+dm*3.0/(double)charge;
		
L:		for(int i=1;;i++){

			double mass = list.get(list.size()-1)+dm/(double)charge;
			boolean add = false;
	
			for(int j=1;j<labelId.length;j++){
				if(Math.abs(mass-monoMasses[j])<0.05){
					labelId[j] = i;
					add = true;
					list.add(monoMasses[j]);
					break;
				}else if(Math.abs(mass-max3)<0.05){
					list.add(mass);
					break L;
				}
			}
			if(!add) list.add(mass);
		}

		int min = 100;
		double max = 0;
		this.masses = new double[list.size()];
		double [] intens = new double[list.size()];
		
		for(int i=0;i<labelId.length-1;i++){
			int diffi = (labelId[i+1]-labelId[i])>=isotopeCount ? isotopeCount : (labelId[i+1]-labelId[i]);
			for(int j=labelId[i];j<labelId[i]+diffi;j++){
				this.masses [j] = list.get(j);
			}
			if(min>diffi) min = diffi;
		}
//		for(int i=0;i<labelId.length;i++)System.out.print(labelId[i]+"\t");
//		System.out.println(labelId[labelId.length-1]+"\t"+min+"\t"+sequence);
		for(int j=labelId[labelId.length-1];j<labelId[labelId.length-1]+min;j++){
			this.masses [j] = list.get(j);
			if(masses[j]>max) max = masses[j];
		}

		IPeak findpeak = new cn.ac.dicp.gp1809.proteome.spectrum.Peak(masses[0]-tolerance, 0d);
//System.out.println(findpeak+"\t"+peaks.length);
		int index = Arrays.binarySearch(peaks, findpeak);
		if(index<0){
			index = -index-1;
		}else if(index >= peaks.length){
			return;
		}
		
		for(int i=index;i<peaks.length;i++){
			double mass = peaks[i].getMz();
			double inten = peaks[i].getIntensity();
			if(inten<0) inten = -inten;
			for(int j=0;j<masses.length;j++){
				if(Math.abs(masses[j]-mass)<tolerance && inten>intens[j]){
					intens[j] = inten;
				}
			}
			if(mass-max>tolerance) break;
		}

		double [] realInten = new double [labelId.length];
		for(int i=0;i<labelId.length;i++){
			boolean b0 = false;
			boolean b1 = false;
			boolean b2 = false;
			for(int j=0;j<min;j++){
				if(intens[labelId[i]+j]>0){
					realInten[i]+= intens[labelId[i]+j];
					if(j==0) b0 = true;
					else if(j==1) b1 = true;
					else if(j==2) b2 = true;
				}
			}
//			System.out.println("167\t"+i+"\t"+labelCount+"\t"+realInten[i]);
			if(misses[i]){
				realInten[i] = 0;
			}
			if(min==2){
				if(!(b0 && b1)){
					realInten[i] = 0;
				}
			}else{
				if(!(b0 && b1 && b2)){
					realInten[i] = 0;
				}
			}
			if(realInten[i]==0){
				missNum[i]++;
				if(missNum[i]==3){
					misses[i] = true;
				}
			}else{
				if(taillist[i].size()==5){
					double oldInten = MathTool.getAveInDouble(taillist[i]);
					taillist[i].remove(0);
					taillist[i].add(realInten[i]);
					double newInten = MathTool.getAveInDouble(taillist[i]);
					
					if(newInten<oldInten*1.1){
						tailNum[i]++;
					}
					if(tailNum[i]>=10){
						misses[i] = true;
					}
				}else{
					taillist[i].add(realInten[i]);
				}
			}
			if(misses[i]){
				missCount++;
			}
		}
		
		
		this.intens = realInten;
		this.ratios = new double[labelId.length*(labelId.length-1)];
		int idcount = 0;
		for(int i=0;i<realInten.length;i++){
			for(int j=i+1;j<realInten.length;j++){
				if(realInten[j]*realInten[i]!=0){
					ratios[idcount] = realInten[j]/realInten[i];
					ratios[idcount+1] = realInten[i]/realInten[j];
				}
				idcount+=2;
			}
		}
//		int least = (labelId.length%2==0) ? labelId.length/2 : labelId.length/2+1;
		this.miss = (missCount>=(labelId.length-1));
	}
	
	public String getKey(){
		return scanNum+"\t"+charge;
	}
	
	public double [] getMasses(){
		return masses;
	}
	
	public double [] getIntens(){
		return intens;
	}
	
	public double [] getRatios(){
		return ratios;
	}
	
	public boolean isMiss(){
		return miss;
	}
	
	public int getMissCount(){
		return missCount;
	}

	public int getScanNum(){
		return scanNum;
	}
	
	public void setRT(double rt){
		this.rt = rt;
	}
	
	public double getRT(){
		return rt;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LabelFeature f1) {
		// TODO Auto-generated method stub
		double d = this.masses[0];
		double d1 = f1.masses[0];
		
		if(d>d1) return 1;
		else if(d<d1) return -1;
		else return 0;
	}

	@Override
	public String toString(){
		
		StringBuilder sb = new StringBuilder();

		return sb.toString();
	}
/*	
	public void makeFeaTable(String tableName, String pixTable, Statement state,
			int length, double thres) throws SQLException, ClassNotFoundException{
		
		iniFeaSQL(tableName);
		
		String getScanNum = "select distinct scanNum from "+pixTable+";";
		ResultSet rset = statement.executeQuery(getScanNum);
		ArrayList <Integer> scanNumList = new ArrayList <Integer>();
		while(rset.next()){
			int s = rset.getInt(2);
			scanNumList.add(s);
		}
		
		for(Integer sNum:scanNumList){
			ArrayList <Pixel> pixList = Pixel.getPixelList2(pixTable, sNum, state, length, thres);
			ArrayList <Feature> feaList = getAllFeature(pixList);
		}
	}
	
	public class FeatureSQL{

		private MySQL sql;
		private Connection connection;
		private Statement statement;

		public FeatureSQL(double pepMr, int scanNum, double mz, double rt, 
				double intensity, double dnInten){
			
		}
		
		public void iniFeaSQL(String tableName) throws SQLException, ClassNotFoundException{
			
			sql = new MySQL();
			connection = sql.getConnection();
			connection.setAutoCommit(false);
			statement = sql.getStatement();
			
			statement.execute("drop table if exists "+tableName);
			statement.execute("create table "+tableName+"(pepMr double not null default '0.0'," +
					"scanNum int not null default '0',mass double not null default 0.0," +
					"rt double not null default '0',inten double not null default 0.0," +
					"dnInten double not null default '0.0',"+
					"primary key(pepMr));");
			statement.execute("alter table Pixel disable keys");
			
		}
	}
	
	
	public static void test(String in) throws NumberFormatException, IOException{
		
		IPC ipc = new IPC();
		Options ipcOptions = new Options();
		ipcOptions.setFastCalc(32);
		
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		int count = 0;
		while((line=reader.readLine())!=null){
			
			count++;
			String [] info = line.split("\t");
			String [] peakline = reader.readLine().split("\t");
//			for(int i=0;i<peakline.length;i++)System.out.println(peakline.length+"\t"+peakline[i]);
			String seq = info[0].substring(2, info[0].length()-2);
			double [] labels = new double [6];
			for(int i=0;i<labels.length;i++){
				labels[i] = Double.parseDouble(info[i+4]);
			}

			IPeak [] peaks = new IPeak[peakline.length/2];
			int j=0;
			for(int i=0;i<peakline.length;){
				double mz = Double.parseDouble(peakline[i++]);
				double inten = Double.parseDouble(peakline[i++]);
				peaks[j++] = new cn.ac.dicp.gp1809.proteome.spectrum.Peak(mz, inten); 
			}
			
			Feature fea = new Feature(seq, 0, 4, labels);
			
			double [] ratio = fea.ratios;
			for(int i=0;i<ratio.length;i++){
				System.out.println(ratio[i]);
			}
			
			break;
		}
		reader.close();
	}
*/	
	public static void main(String[] args) throws NumberFormatException, IOException {

		
	}

}
