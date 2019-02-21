/* 
 ******************************************************************************
 * File: PScoreCalculator.java * * * Created on 2013-2-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.Pscore;

import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.util.math.Arrangmentor;

/**
 * @author ck
 *
 * @version 2013-2-28, 10:28:15
 */
public class PScoreCalculator3 {

	private HashSet <Character> siteset;
	private double modmass;
	private String modname;
	private static final double tolerance = 0.1;

	public PScoreCalculator3(HashSet <Character> siteset, double modmass, String modname){
		this.siteset = siteset;
		this.modmass = modmass;
		this.modname = modname;
	}
	
	public void compute(String sequence, short charge,
	        IMS2PeakList peaklist, int siteNum, AminoacidFragment aaf,
	        int[] iontypes, char symbol){
		
		StringBuilder sb = new StringBuilder();
		StringBuilder uniPepSeq = new StringBuilder();
		int modcount = 0;
		int sitecount = 0;

		for(int i=0;i<sequence.length();i++){
			if(sequence.charAt(i)>='A' && sequence.charAt(i)<='Z'){
				sb.append(sequence.charAt(i));
				if(i>=2 && i<sequence.length()-2){
					uniPepSeq.append(sequence.charAt(i));
					if(this.siteset.contains(sequence.charAt(i)))
						sitecount++;
				}
			}else if(sequence.charAt(i)==symbol){
				modcount++;
			}else{
				sb.append(sequence.charAt(i));
			}
		}
		
		Ions ions = aaf.fragment(sb.toString(), iontypes, true);
		Ion [] bs = ions.getIons(Ion.TYPE_B);
		Ion [] ys = ions.getIons(Ion.TYPE_Y);
		IPeak [] peaks = peaklist.getPeaksSortByIntensity();
		
		int [] bionSiteCount = new int[uniPepSeq.length()];
		int [] yionSiteCount = new int[uniPepSeq.length()];
		for(int i=0;i<uniPepSeq.length();i++){
			if(uniPepSeq.charAt(i)=='S' || uniPepSeq.charAt(i)=='T'){
				for(int j=i;j<uniPepSeq.length();j++)
					bionSiteCount[j]++;
			}
		}
		for(int i=0;i<bionSiteCount.length-1;i++){
			yionSiteCount[i] = bionSiteCount[bionSiteCount.length-1]-bionSiteCount[bionSiteCount.length-i-2];
		}

		int [] initialList = new int[sitecount];
		for(int i=0;i<sitecount;i++){
			if(i<modcount){
				initialList[i] = i;
			}else{
				initialList[i] = -1;
			}
		}
		
		int [][] positionList = Arrangmentor.arrangementArrays(initialList);
		double [][] scoreList = new double[positionList.length][sitecount];
		
		double [] binten = new double[bs.length];
		double [] bmodinten = new double[bs.length];
		double [] yinten = new double[ys.length];
		double [] ymodinten = new double[ys.length];
		
		HashMap <Double, String> matchmap = new HashMap <Double, String>();
		HashSet <String> usedset = new HashSet <String>();
		
L:		for(int i=0;i<peaks.length;i++){

			double mzi = peaks[i].getMz();
			double inteni = (peaks.length-i)/(double)peaks.length;

			for(int j=0;j<bs.length;j++){
				
				double bfragmz = bs[j].getMz();
				double yfragmz = ys[j].getMz();
				
				if(Math.abs(mzi-yfragmz)<tolerance){
					if(usedset.contains("y"+(j+1)))
						continue L;
					
					usedset.add("y"+(j+1));
					yinten[j]=inteni;
					matchmap.put(mzi, "y"+(j+1));
					continue L;
				}
				
				if(Math.abs(mzi-bfragmz)<tolerance){
					if(usedset.contains("b"+(j+1)))
						continue L;
					
					usedset.add("b"+(j+1));
					binten[j]=inteni;
					matchmap.put(mzi, "b"+(j+1));
					continue L;
				}
				
				for(int k=1;k<=modcount;k++){
					if(Math.abs(mzi-yfragmz-modmass*k)<tolerance && k<=yionSiteCount[j]){
						if(usedset.contains("y"+(j+1)+"+("+modname+")"))
							continue L;
						
						if(inteni>ymodinten[j]) ymodinten[j] = inteni;
						usedset.add("y"+(j+1)+"+("+modname+")");
						matchmap.put(mzi, "y"+(j+1)+"+("+modname+")");
						this.match(positionList, scoreList, k, sitecount-yionSiteCount[j], inteni, 1);
					}
					if(Math.abs(mzi-bfragmz-modmass*k)<tolerance && k<=bionSiteCount[j]){
						if(usedset.contains("b"+(j+1)+"+("+modname+")"))
							continue L;
						
						if(inteni>bmodinten[j]) bmodinten[j] = inteni;
						usedset.add("b"+(j+1)+"+("+modname+")");
						matchmap.put(mzi, "b"+(j+1)+"+("+modname+")");
						this.match(positionList, scoreList, k, bionSiteCount[j], inteni, 0);
					}
				}
			}
		}
	}
	
	private void match(int [][] positionList, double [][] scoreList, int matchModCount, int position, 
			double intensity, int by){
				
		// b ion
		if(by==0){
			for(int i=0;i<positionList.length;i++){
				int mci = 0;
				boolean match = false;
				for(int j=0;j<position;j++){
					if(positionList[i][j]>=0){
						mci++;
						if(mci==matchModCount){
							match = true;
							break;
						}
					}
				}
				if(match){
					for(int j=0;j<scoreList[i].length;j++){
						if(positionList[i][j]>=0){
							scoreList[i][j] += intensity;
						}
					}
				}
			}
		}else if(by==1){// y ion

			for(int i=0;i<positionList.length;i++){
				int mci = 0;
				boolean match = false;
				for(int j=position;j<positionList[i].length;j++){
					if(positionList[i][j]>=0){
						mci++;
						if(mci==matchModCount){
							match = true;
							break;
						}
					}
				}
				if(match){
					for(int j=0;j<scoreList[i].length;j++){
						if(positionList[i][j]>=0){
							scoreList[i][j] += intensity;
						}
					}
				}
			}
		}
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
