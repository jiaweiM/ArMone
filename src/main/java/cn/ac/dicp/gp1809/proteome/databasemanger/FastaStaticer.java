/* 
 ******************************************************************************
 * File: FastaStaticer.java * * * Created on 2010-12-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.aaproperties.Aminoacid;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2010-12-26, 15:05:08
 */
public class FastaStaticer {
	
	private FastaReader reader;
	private HashMap <Character, Double> massMap;
	private HashMap <Character, Double> modMap;
	private long count;
	
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;

	public FastaStaticer(String fasta) throws IOException{
		this(new FastaReader(fasta));
	}
	
	public FastaStaticer(FastaReader reader){
		this.reader = reader;
		this.initial();
	}
	
	private void initial(){
		this.massMap = new HashMap <Character, Double>();
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		for(char i='A';i<='Z';i++){
			
			if(i=='J'){
				massMap.put(i, 87.032028);
			}else if(i=='O'){
				massMap.put(i, 101.047679);
			}else if(i=='U'){
				massMap.put(i, 163.063329);
			}else if(i=='C'){
				massMap.put(i, 160.0307);
			}else{
				Aminoacid a = aas.get(i);
				if(a!=null){
					char one = a.getOneLetter();
					double mass = a.getMonoMass();
					massMap.put(one, mass);
				}
			}
		}
		this.modMap = new HashMap <Character, Double>();
		modMap.put('M', 15.99492);
		modMap.put('S', 79.966331);
		modMap.put('T', 79.966331);
		modMap.put('Y', 79.966331);
//		modMap.put('L', 3.018845);
	}

	public void getAADistribution(){
		int proteinsty = 0;
		ProteinSequence sequence = null;
		int totalAA = 0;
		int c = 0;
		int [] aacount = new int [26];
		while((sequence=reader.nextSequence())!=null){
			c++;
			totalAA += sequence.length();
			String seq = sequence.getUniqueSequence();
			char [] cs = seq.toCharArray();
			boolean sty = false;
			for(int i=0;i<cs.length;i++){
				char a = cs[i];
				aacount[a-65]++;
				if(a=='J' || a=='O' || a=='U'){
					sty = true;
				}
			}
			if(sty) proteinsty++;
		}
		for(int j=0;j<26;j++){
			System.out.println("Aminoacid "+(char)(j+65)+" :\t"+aacount[j]+
					"\tPercent: \t"+dfPer.format((double)aacount[j]/totalAA));
		}
		System.out.println("Total protein count:\t"+c);
		System.out.println(proteinsty);
	}
	
	public void getProMWDis(int miss, double ppm){
		
		ArrayList <Double> mwlist = new ArrayList <Double>();
		ProteinSequence sequence = null;
		
		MwCalculator mwc = new MwCalculator();
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		mwc.setAacids(aas);
		
		int totalAA = 0;
		int [] aacount = new int [26];
		
		while((sequence=reader.nextSequence())!=null){
			
			if(sequence.getReference().startsWith("REV")){
				break;
			}
			
			String seq = sequence.getUniqueSequence();
			
			totalAA += sequence.length();
			char [] cs = seq.toCharArray();
			for(int i=0;i<cs.length;i++){
				char a = cs[i];
				aacount[a-65]++;
			}

			double mw = mwc.getMonoIsotopeMZ(seq);
			mwlist.add(mw);
		}
		
		double index = 0;
		double [] dis = new double[aacount.length];
		for(int i=0;i<aacount.length;i++){
			char aa = (char)(i+65);
			double aams = aas.getAminoacid(aa).getMonoMass();
			dis[i] = aacount[i]/(double)totalAA;
			index += aams*dis[i];
		}
		
		Double med = MathTool.getMedianInDouble(mwlist);
		Double ave = MathTool.getAveInDouble(mwlist);
		int len = (int) (med/index);
		System.out.println("ave\t"+ave);
		System.out.println("med\t"+med);
		System.out.println("len\t"+len);
		
		int numk = (int) ((int) len*dis['K'-'A']);
		int numr = (int) ((int) len*dis['R'-'A']);
		int numm = (int) ((int) len*dis['M'-'A']);
		int nums = (int) ((int) len*dis['S'-'A']);
		int numt = (int) ((int) len*dis['T'-'A']);
		int numy = (int) ((int) len*dis['Y'-'A']);
		
		System.out.println(numk);
		System.out.println(numr);
		System.out.println(numm);
		System.out.println(nums);
		System.out.println(numt);
		System.out.println(numy);
		
		
	}
	
	/**
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * @param ppm
	 */
	public void getProMWDis2(double ppm){
		
		ProteinSequence sequence = null;
		Enzyme en = Enzyme.TRYPSIN;

		int totalAA = 0;
		int [] aacount = new int [26];
		int [] lenlist = new int [25];
		Arrays.fill(lenlist, 0);
		
		while((sequence=reader.nextSequence())!=null){
			
			if(sequence.getReference().startsWith("REV")){
				break;
			}
			
			String seq = sequence.getUniqueSequence();
			String [] peps = en.cleave(seq, 4, 1);
			
			for(int i=0;i<peps.length;i++){
				if(peps[i].length()>=6 && peps[i].length()<=30){
					lenlist[peps[i].length()-6]++;
				}
			}
			
			totalAA += sequence.length();
			char [] cs = seq.toCharArray();
			for(int i=0;i<cs.length;i++){
				char a = cs[i];
				aacount[a-65]++;
			}

		}
		
		int aatotal = MathTool.getTotal(aacount);
		int lentotal = MathTool.getTotal(lenlist);
		
		double ds = (double)aacount['S'-'A']/(double)aatotal;
		double dt = (double)aacount['T'-'A']/(double)aatotal;
		double dy = (double)aacount['Y'-'A']/(double)aatotal;
		double dm = (double)aacount['M'-'A']/(double)aatotal;
		
		double dk = (double)aacount['K'-'A']/(double)aatotal;
		
		double space = 0;
		for(int i=0;i<lenlist.length;i++){
			int len = i+6;
			int lencount = lenlist[i];
			double modsty = (double)len*(ds+dt+dy);
			double modm = (double)len*dm;
			double modlabel = (double)len*dk+1;
			
//			space += (modsty+1)*(modm+1)*Math.pow((modlabel+1), 4);
//			space += Math.pow(2, (modsty+modm))*(double)lencount/(double)lentotal;
			space += Math.pow(2, (modsty+modm)) * Math.pow(3, modlabel) *(double)lencount/(double)lentotal;
		}
		System.out.println("Space\t"+space);
		System.out.println("Space*ppm\t"+space*ppm);
		System.out.println("(Log-Space)*ppm\t"+Math.log10(space)*ppm);
		System.out.println("(Log-Space*ppm)\t"+Math.log10(space*ppm));
	}
	
	private void getModNum(int siteNum, int labelNum){

		if(labelNum==2){
			int num = 0;
			for(int i=0;i<siteNum;i++){
				for(int j=0;j<siteNum;j++){
					if(i+j<=siteNum){
						num++;
					}else{
						break;
					}
				}
			}
		}else if(labelNum==3){
			
			int num = 0;
			for(int i=0;i<siteNum;i++){
				for(int j=0;j<siteNum;j++){
					for(int k=0;k<siteNum;k++){
						if(i+j+k<=siteNum){
							num++;
						}else{
							break;
						}
					}
				}
			}
			
		}else if(labelNum==4){
			
			int num = 0;
			for(int i=0;i<siteNum;i++){
				for(int j=0;j<siteNum;j++){
					for(int k=0;k<siteNum;k++){
						for(int h=0;h<siteNum;h++){
							if(i+j+k+h<=siteNum){
								num++;
							}else{
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public void statMWDis(String output) throws IOException{
		PrintWriter writer = new PrintWriter(output);
		int len = (5000-600)/5+1;
		int [] dis = new int[len];
		ProteinSequence sequence = null;
		while((sequence=reader.nextSequence())!=null){
			if(sequence.getReference().startsWith("REV")){
				for(int i=0;i<dis.length;i++){
					int beg = 600 + i*5;
					int end = 600 + (i+1)*5;
					writer.write(beg+"~"+end+"\t"+dis[i]);
				}
				break;
			}
			statistic5(sequence, dis);
		}
	}
	
	public void statMWDis2(String fasta) throws IOException{
//		PrintWriter writer = new PrintWriter(output);
		
		int len = (2100-2000)/100+1;
		double [] dis = new double[len];
		for(int i=0;i<dis.length;i++){
			FastaReader reader = new FastaReader(fasta);
			dis[i] = 2000.0 + i*100.0;
			ProteinSequence sequence = null;
			int count = 0;
			while((sequence=reader.nextSequence())!=null){
				if(sequence.getReference().startsWith("REV")){
					break;
				}
				statistic4(sequence, dis[i], 10, count);
			}
			System.out.print(dis[i]+"\t"+count+"\n");
//			writer.write(dis[i]+"\t"+count+"\n");
		}
	}
	
	public void statistic(double mass){

		ProteinSequence sequence = null;
		int c = 1;
		while((sequence=reader.nextSequence())!=null){
//			int c = statistic(sequence);
			statistic4(sequence, mass);
//			System.out.println(c++);
		}
		System.out.println("Final\t"+count);
	}
	
	public void statistic(double mass, double ppm){

		ProteinSequence sequence = null;
		int c = 1;
		while((sequence=reader.nextSequence())!=null){
//			int c = statistic(sequence);
			statistic4(sequence, mass, ppm);
//			System.out.println(c++);
		}
//		System.out.println(count);
	}
/*	
	public int statistic(ProteinSequence sequence){
		String seq = sequence.getUniqueSequence();
		char [] aas = seq.toCharArray();
		int count = 0;
		int begin = 0;
		int end = 0;
		double mass = 18.01528D;
		for(int i=0;i<aas.length;i++){
			
//			if(!massMap.containsKey(aas[i]))
//				System.out.println(aas[i]);
			
			mass += massMap.get(aas[i]);
			end = i;
			
			if(mass<999.99){
				
			}else if(mass>1000.01){
				mass -= massMap.get(aas[begin]);
				begin ++;
				for(int j=end;j>0;j--){
					mass -= massMap.get(aas[j]);
					if(mass<999.5){
						end = j-1;
						break;
					}
				}
				i = end;
			}else{
				count++;
				mass -= massMap.get(aas[begin]);
				begin ++;
			}
		}
		return count;
	}
*/	
	public void statistic2(ProteinSequence sequence){
		String seq = sequence.getUniqueSequence();
		char [] aas = seq.toCharArray();

L:		for(int i=0;i<aas.length;i++){
/*	
			if(i!=0){
				if(aas[i]!='P')
					if(aas[i-1]!='K' && aas[i-1]!='R')
						continue;
			}
*/			
			for(int j=i+11;j<aas.length;j++){
/*				
				if(j!=aas.length-1){
					if(aas[j-1]!='P')
						if(aas[j]!='K' && aas[j]!='R')
							continue;
				}
*/
				double mass = 18.01528D;
				ArrayList <Double> addList = new ArrayList <Double>();
				int miss = 0;

				for(int k=i;k<=j;k++){
					mass += massMap.get(aas[k]);
					if(aas[k]=='K' || aas[k]=='R')
						miss++;
					if(modMap.containsKey(aas[k])){
						addList.add(modMap.get(aas[k]));
					}
				}

//				if(miss>2)
//					continue L;
				
				if(mass-2381.986>0.1)
					continue L;
				
				if(addList.size()==0){
					if(Math.abs(mass-2381.986)<0.02382){
						count++;
						continue L;
					}
				}else{
					judge(mass, addList);
/*					
					HashMap <Double, Long> addMap = getAddMap(addList);
					Iterator <Double> it = addMap.keySet().iterator();
					while(it.hasNext()){
						Double d = it.next();
						Long l = addMap.get(d);
						if(Math.abs(mass+d-2740.1855)<0.0274){
							count+=l;
						}
					}
					
					LinkedList <ArrayList <Double>> total = getAddList(addList);
					Iterator <ArrayList <Double>> totalIt = total.iterator();
					while(totalIt.hasNext()){
						ArrayList <Double> llist = totalIt.next();
						Iterator <Double> it = llist.iterator();
						while(it.hasNext()){
							Double ddd = it.next();
							if(Math.abs(mass+ddd-2740.1855)<0.0274){
								count++;
							}
						}
					}
*/					
				}
			}
		}
	}
	
	private void judge(double mass, ArrayList <Double> addList){

		int size = addList.size();
		long num = (long) Math.pow(2.0, size) -1;
		for(int i=0;i<=num;i++){
			int n = i;
			double d = 0;
			for(int j=0;j<size;j++){
				int k = n%2;
				if(k==1)
					d+=addList.get(j);
				n/=2;
			}
			
			if(Math.abs(mass+d-2740.1855)<0.0274){
				count++;
			}
		}
	}
	
	private void statistic3(ProteinSequence sequence, double pepmass){
		String seq = sequence.getUniqueSequence();
		char [] aas = seq.toCharArray();

L:		for(int i=0;i<aas.length;i++){
	
			if(i!=0){
				if(aas[i]!='P')
					if(aas[i-1]!='K' && aas[i-1]!='R')
						continue;
			}
			
			for(int j=i+5;j<aas.length;j++){
/*				
				if(j!=aas.length-1){
					if(aas[j-1]!='P')
						if(aas[j]!='K' && aas[j]!='R')
							continue;
				}
*/
				double mass = 18.01528D;
				int miss = 0;

				double [][] masslist = new double[j-i+1][];
				for(int k=i;k<=j;k++){
					mass += massMap.get(aas[k]);
					
					if(aas[k]=='K' || aas[k]=='R'){
						if(k!=0)
							if(aas[k-1]!='P')
								miss++;
					}
						
					
					if(modMap.containsKey(aas[k])){
						masslist[k-i] = new double[2];
						masslist[k-i][0] = 0;
						masslist[k-i][1] = modMap.get(aas[k]);
					}else{
						masslist[k-i] = new double[1];
						masslist[k-i][0] = 0;
					}
				}

				if(miss>1)
					continue L;
				
				if(mass-pepmass>1)
					continue L;
				
				judge2(masslist, 0, mass, pepmass);
			}
		}
	}
	
	private void statistic4(ProteinSequence sequence, double pepmass){
		
		Enzyme en = Enzyme.TRYPSIN;
		String seq = sequence.getUniqueSequence();
		String [] peps = en.cleave(seq, 1, 1);
		
		for(int i=0;i<peps.length;i++){
			int len = peps[i].length();
			double [][] masslist = new double[len][];
			char [] aas = peps[i].toCharArray();
			
			double mass = 18.01528D;
			for(int k=0;k<aas.length;k++){
				
				mass += massMap.get(aas[k]);
				
				if(modMap.containsKey(aas[k])){
					masslist[k] = new double[2];
					masslist[k][0] = 0;
					masslist[k][1] = modMap.get(aas[k]);
				}else{
					masslist[k] = new double[1];
					masslist[k][0] = 0;
				}
			}
			
			if(mass-pepmass>1)
				continue;
			
			judge2(masslist, 0, mass, pepmass);
			
		}
	}
	
	private void statistic4(ProteinSequence sequence, double pepmass, double ppm, int count){
		
		Enzyme en = Enzyme.TRYPSIN;
		String seq = sequence.getUniqueSequence();
		String [] peps = en.cleave(seq, 2, 1);
		double tola = pepmass*ppm/1.0E6;
		
		for(int i=0;i<peps.length;i++){
			int len = peps[i].length();
			double [][] masslist = new double[len][];
			char [] aas = peps[i].toCharArray();
			
			double mass = 18.01528D;
			for(int k=0;k<aas.length;k++){
				
				mass += massMap.get(aas[k]);
				
				if(modMap.containsKey(aas[k])){
					masslist[k] = new double[2];
					masslist[k][0] = 0;
					masslist[k][1] = modMap.get(aas[k]);
				}else{
					masslist[k] = new double[1];
					masslist[k][0] = 0;
				}
			}
			
			if(mass-pepmass>=tola)
				continue;
			
			judge2(masslist, 0, mass, pepmass, ppm, count);
			
		}
	}
	
	private void statistic4(ProteinSequence sequence, double pepmass, double ppm){
		
		Enzyme en = Enzyme.TRYPSIN;
		String seq = sequence.getUniqueSequence();
		String [] peps = en.cleave(seq, 2, 1);
		double tola = pepmass*ppm/1.0E6;
		
		for(int i=0;i<peps.length;i++){
			int len = peps[i].length();
			double [][] masslist = new double[len][];
			char [] aas = peps[i].toCharArray();
			
			double mass = 18.01528D;
			for(int k=0;k<aas.length;k++){
				
				mass += massMap.get(aas[k]);
				
				if(modMap.containsKey(aas[k])){
					masslist[k] = new double[2];
					masslist[k][0] = 0;
					masslist[k][1] = modMap.get(aas[k]);
				}else{
					masslist[k] = new double[1];
					masslist[k][0] = 0;
				}
			}
			
			if(mass-pepmass>=tola)
				continue;
			
			judge2(masslist, 0, mass, pepmass, ppm);
			
		}
	}
	
	private void statistic5(ProteinSequence sequence, int [] dis){
		
		Enzyme en = Enzyme.TRYPSIN;
		String seq = sequence.getUniqueSequence();
		String [] peps = en.cleave(seq, 2, 1);
		
		for(int i=0;i<peps.length;i++){
			int len = peps[i].length();
			double [][] masslist = new double[len][];
			char [] aas = peps[i].toCharArray();
			
			double mass = 18.01528D;
			for(int k=0;k<aas.length;k++){
				
				mass += massMap.get(aas[k]);
				
				if(modMap.containsKey(aas[k])){
					masslist[k] = new double[2];
					masslist[k][0] = 0;
					masslist[k][1] = modMap.get(aas[k]);
				}else{
					masslist[k] = new double[1];
					masslist[k][0] = 0;
				}
			}

			judge3(masslist, 0, mass, dis);
		}
	}
	
	private void judge2(double [][] masslist, int i, double mass, double pepmass){
		if(i==masslist.length){
			if(Math.abs(mass-pepmass)<1){
				count++;
			}
		}else{
			for(int j=0;j<masslist[i].length;j++){
				judge2(masslist, i+1, mass+masslist[i][j], pepmass); 
			}
		}
	}
	
	private void judge2(double [][] masslist, int i, double mass, double pepmass, double ppm){
		
		double tola = pepmass*ppm/1E6;
		if(i==masslist.length){
			if(Math.abs(mass-pepmass)<tola){
				count++;
			}
		}else{
			for(int j=0;j<masslist[i].length;j++){
				judge2(masslist, i+1, mass+masslist[i][j], pepmass, ppm); 
			}
		}
	}
	
	private void judge2(double [][] masslist, int i, double mass, double pepmass, double ppm, int count){
		
		double tola = pepmass*ppm/1.0E6;
//		System.out.println(tola);
		if(i==masslist.length){
			if(Math.abs(mass-pepmass)<tola){
				count++;
			}
		}else{
			for(int j=0;j<masslist[i].length;j++){
				judge2(masslist, i+1, mass+masslist[i][j], pepmass, ppm, count); 
			}
		}
	}
	
	private void judge3(double [][] masslist, int i, double mass, int [] dis){

		if(i==masslist.length){
			if(mass>=600 && mass<=5000){
				int id = (int) ((mass-600)/5);
				dis[id]++;
			}
		}else{
			for(int j=0;j<masslist[i].length;j++){
				judge3(masslist, i+1, mass+masslist[i][j], dis); 
			}
		}
	}
	
	private void statistic6(ProteinSequence sequence, double ppm){

		
		Enzyme en = Enzyme.TRYPSIN;
		String seq = sequence.getUniqueSequence();
		String [] peps = en.cleave(seq, 2, 1);
		
		int len = (5000-600)/100+1;
		
		for(int i=0;i<peps.length;i++){
			
			char [] aas = peps[i].toCharArray();
			if(aas.length<6)
				continue;
			
			ArrayList <Double> addlist = new ArrayList <Double>();
			
			double mass = 18.01528D;
			for(int k=0;k<aas.length;k++){
				
				mass += massMap.get(aas[k]);
				
				if(modMap.containsKey(aas[k])){
					addlist.add(modMap.get(aas[k]));
				}
			}
			
			HashMap <Double, Long> disMap = getAddMap(addlist);
			Iterator <Double> it = disMap.keySet().iterator();
			while(it.hasNext()){
				Double add = it.next();
			}

		}
	
	}
	
	private static HashMap <Double, Long> getAddMap(ArrayList <Double> addList){
		
		HashMap <Double, Long> massMap = new HashMap <Double, Long>();
		int size = addList.size();
		long num = (long) Math.pow(2.0, size) -1;
		for(int i=0;i<=num;i++){
			int n = i;
			double d = 0;
			for(int j=0;j<size;j++){
				int k = n%2;
				if(k==1)
					d+=addList.get(j);
				n/=2;
			}

			if(massMap.containsKey(d)){
				Long c = massMap.get(d);
				massMap.put(d, c+1);
			}else{
				massMap.put(d, 1l);
			}
		}
		return massMap;
	}
	
	public void aaStatic(){
		long [] aas = new long[26];
		ProteinSequence sequence = null;

		int count = 1;
		int total = 0;
		while((sequence=reader.nextSequence())!=null){
			count++;
//			System.out.println(count++);
			char [] aachars = sequence.getUniqueSequence().toCharArray();
			for(int i=0;i<aachars.length;i++){
				int index = aachars[i]-65;
				aas[index]++;
			}
			total += aachars.length;
		}

		for(int j=0;j<aas.length;j++){
			char c = (char) (j+65);
			System.out.println(c+"\t"+aas[j]/2+"\t"+dfPer.format(((double)aas[j]/total)));
		}
		
		System.out.println("Total\t"+total/2);
	}
	
	public void totalPhosStat(){
		
		ProteinSequence sequence = null;

		int [][] aaScount = new int [15][26];
		int [][] aaTcount = new int [15][26];
		int [][] aaYcount = new int [15][26];
		int [][] aaAllCount = new int [15][26];
		
		while((sequence=reader.nextSequence())!=null){

			String seq = sequence.getUniqueSequence();
			char [] cs = seq.toCharArray();
			for(int i=0;i<cs.length;i++){
				char a = cs[i];
				if(a=='S'){
					int beg = i-7;
					for(int j=beg;j<beg+15;j++){
						if(j>=0 && j<cs.length){
							char aaj = cs[j];
							aaScount[j-beg][aaj-65]++;
							aaAllCount[j-beg][aaj-65]++;
						}
					}
				}else if(a=='T'){
					int beg = i-7;
					for(int j=beg;j<beg+15;j++){
						if(j>=0 && j<cs.length){
							char aaj = cs[j];
							aaTcount[j-beg][aaj-65]++;
							aaAllCount[j-beg][aaj-65]++;
						}
					}
				}else if(a=='Y'){
					int beg = i-7;
					for(int j=beg;j<beg+15;j++){
						if(j>=0 && j<cs.length){
							char aaj = cs[j];
							aaYcount[j-beg][aaj-65]++;
							aaAllCount[j-beg][aaj-65]++;
						}
					}
				}
			}
		}
/*		
		System.out.println("S");
		System.out.print("Aminoacid\t");
		for(int i=0;i<15;i++){
			System.out.print("Number\t");
		}
		System.out.println();
		for(int i=0;i<26;i++){
			System.out.print(((char)(i+65))+"\t");
			for(int j=0;j<15;j++){
				System.out.print(aaScount[i][j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
*/		
	
		int schar = 0;
		for(int i=0;i<aaScount[0].length;i++){
			System.out.print((char)(schar+65)+" :\t");
			for(int j=0;j<15;j++){
				System.out.print(aaScount[j][schar]+"\t");
			}
			schar++;
			System.out.print("\n");
		}
		
		int tchar = 0;
		for(int i=0;i<aaTcount[0].length;i++){
			System.out.print((char)(tchar+65)+" :\t");
			for(int j=0;j<15;j++){
				System.out.print(aaTcount[j][tchar]+"\t");
			}
			tchar++;
			System.out.print("\n");
		}
		
		int ychar = 0;
		for(int i=0;i<aaYcount[0].length;i++){
			System.out.print((char)(ychar+65)+" :\t");
			for(int j=0;j<15;j++){
				System.out.print(aaYcount[j][ychar]+"\t");
			}
			ychar++;
			System.out.print("\n");
		}
		
		int allchar = 0;
		for(int i=0;i<aaAllCount[0].length;i++){
			System.out.print((char)(allchar+65)+" :\t");
			for(int j=0;j<15;j++){
				System.out.print(aaAllCount[j][allchar]+"\t");
			}
			allchar++;
			System.out.print("\n");
		}
	}
	
	public void totalPhosJOUStat(){
		
		ProteinSequence sequence = null;

		int [][] aaScount = new int [15][26];
		int [][] aaTcount = new int [15][26];
		int [][] aaYcount = new int [15][26];
		
		
		
		while((sequence=reader.nextSequence())!=null){
			
			String ref = sequence.getReference();
			if(ref.startsWith("REV"))
				break;

			String seq = sequence.getUniqueSequence();
			char [] cs = seq.toCharArray();
			for(int i=0;i<cs.length;i++){
				char a = cs[i];
				if(a=='S'){
					int beg = i-7;
					for(int j=beg;j<beg+15;j++){
						if(j>=0 && j<cs.length){
							char aaj = cs[j];
							if(aaj=='J'){
								aaScount[j-beg]['S'-65]++;
							}else if(aaj=='O'){
								aaScount[j-beg]['T'-65]++;
							}else if(aaj=='U'){
								aaScount[j-beg]['Y'-65]++;
							}else{
								aaScount[j-beg][aaj-65]++;
							}
						}
					}
				}else if(a=='T'){
					int beg = i-7;
					for(int j=beg;j<beg+15;j++){
						if(j>=0 && j<cs.length){
							char aaj = cs[j];
							if(aaj=='J'){
								aaTcount[j-beg]['S'-65]++;
							}else if(aaj=='O'){
								aaTcount[j-beg]['T'-65]++;
							}else if(aaj=='U'){
								aaTcount[j-beg]['Y'-65]++;
							}else{
								aaTcount[j-beg][aaj-65]++;
							}
						}
					}
				}else if(a=='Y'){
					int beg = i-7;
					for(int j=beg;j<beg+15;j++){
						if(j>=0 && j<cs.length){
							char aaj = cs[j];
							if(aaj=='J'){
								aaYcount[j-beg]['S'-65]++;
							}else if(aaj=='O'){
								aaYcount[j-beg]['T'-65]++;
							}else if(aaj=='U'){
								aaYcount[j-beg]['Y'-65]++;
							}else{
								aaYcount[j-beg][aaj-65]++;
							}
						}
					}
				}
			}
		}
		
		int schar = 0;
		for(int i=0;i<aaScount[0].length;i++){
			System.out.print((char)(schar+65)+" :\t");
			for(int j=0;j<15;j++){
				System.out.print(aaScount[j][schar]+"\t");
			}
			schar++;
			System.out.print("\n");
		}
		System.out.print("\n");
		
		int tchar = 0;
		for(int i=0;i<aaTcount[0].length;i++){
			System.out.print((char)(tchar+65)+" :\t");
			for(int j=0;j<15;j++){
				System.out.print(aaTcount[j][tchar]+"\t");
			}
			tchar++;
			System.out.print("\n");
		}
		System.out.print("\n");
		
		int ychar = 0;
		for(int i=0;i<aaYcount[0].length;i++){
			System.out.print((char)(ychar+65)+" :\t");
			for(int j=0;j<15;j++){
				System.out.print(aaYcount[j][ychar]+"\t");
			}
			ychar++;
			System.out.print("\n");
		}
		
	}
	
	public void findPro(String file) throws IOException{
		
		BufferedReader breader = new BufferedReader(new FileReader(file));
		HashSet <String> seqset = new HashSet <String>();
		String line;
		while((line=breader.readLine())!=null){
			seqset.add(line);
		}

		ProteinSequence ps = null;
		while((ps=this.reader.nextSequence())!=null){
			Iterator <String> it = seqset.iterator();
			int pslen = ps.length();
			String proseq = ps.getUniqueSequence();
			
			while(it.hasNext()){
				String seq = it.next();
				int seqlen = seq.length();
//				int loc = ps.indexOf(seq);
				
				int loc = proseq.indexOf(seq);
				if(loc>=0){
					
					int beg = loc-1;
					int end = loc+seqlen-1;
					
					char pre = beg>=0 ? proseq.charAt(beg) : '-';
					char nex = end<pslen ? proseq.charAt(end) : '-';
					
					System.out.println(pre+"."+seq+"."+nex+"\t"+ps.getReference());
					it.remove();
				}
			}
			if(seqset.size()==0){
//				System.out.println("End");
				break;
			}
		}
		
		System.out.println(seqset);
		
		this.reader.close();
		breader.close();
	}
	
	public void findProSeq(String seq) throws IOException{

		ProteinSequence ps = null;
		while((ps=this.reader.nextSequence())!=null){

			int pslen = ps.length();
			String proseq = ps.getUniqueSequence();
			
			int seqlen = seq.length();
			int loc = ps.indexOf(seq);
			
//			int loc = proseq.indexOf(seq);
			if(loc>=0){
				
				int beg = loc-1;
				int end = loc+seqlen-1;
				
				char pre = beg>=0 ? proseq.charAt(beg) : '-';
				char nex = end<pslen ? proseq.charAt(end) : '-';
				
				System.out.println(pre+"."+seq+"."+nex+"\t"+ps.getReference());

			}

		}

		this.reader.close();

	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

//		FastaStaticer stat = new FastaStaticer("E:\\DataBase\\ipi.HUMAN.v3.52" +
//				"\\Final_ipi_human352_0.fasta");
		
		String fasta = "E:\\ModDataBase\\NEW\\AA7\\final.ipi.Human.v3.80_jou_0.fasta";
		String fasta3 = "E:\\ModDataBase\\NEW\\AA3\\final.ipi.Human.v3.80_jou_3aa_0.fasta";
		String fasta4 = "E:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta";
		String fasta5 = "E:\\ModDataBase\\NEW\\AA7\\ipi.Human.v3.80_jou.fasta";
		String fasta6 = "E:\\DataBase\\IPI_mouse\\current\\ipi.MOUSE.v3.80.fasta";
		String fasta7 = "E:\\ModDataBase\\NEW\\AA7\\Final_ipi.Human.v3.80_jou_18mix.fasta";
		
		long fin = 0;
		
//		FastaStaticer s1 =  new FastaStaticer(fasta6);
//		s1.getProMWDis2(50);
		
//		for(int j=50;j<=500;j+=50){
//			System.out.println(j+"\tppm");
/*		
			for(int i=1000;i<3600;i+=100){
				FastaStaticer s1 =  new FastaStaticer(fasta4);
				s1.statistic((double)i, 10);
				long count = s1.count;
				double in = (i*10/1E6*2);
				System.out.println(count);
				fin += count * 100/in;
			}
			System.out.println("Fin\t"+Math.log10(fin));
			System.out.println("");
*/			
//		}

//		FastaStaticer s1 =  new FastaStaticer(fasta4);
//		s1.getProMWDis(2,10d);
		
//		s1.totalPhosStat();
//		s1.totalPhosJOUStat();
		
//		String output = "E:\\ModDataBase\\NEW\\AA7\\Mw_distribution.txt";
//		s1.statMWDis2(fasta);
//		stat.statistic();
//		FastaStaticer stat = new FastaStaticer("I:\\" +
//				"\\Final_HuSPep_20101117.fasta");
		
//		stat.statistic();
//		String bovin = "E:\\ModDataBase\\Phospho\\Combine_3_mouse\\" +
//		"final.ipi.MOUSE.v3.80_jou.fasta";
//		FastaStaticer stat = new FastaStaticer("E:\\DataBase\\" +
//				"ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80_NoBZ.fasta");
		
		String f1 = "D:\\ModDataBase\\v20141011.human\\human.jou.fasta";
		String f2 = "F:\\DataBase\\uniprot\\uniprot-human-20131211_0.fasta";
		FastaStaticer stat = new FastaStaticer(f1);
		stat.totalPhosStat();
//		stat.getAADistribution();
/*		
		ArrayList <Double> list = new ArrayList <Double>();
		list.add(2d);
		list.add(2d);
		list.add(3d);
		System.out.println(getAddMap(list));
*/
	}

}
