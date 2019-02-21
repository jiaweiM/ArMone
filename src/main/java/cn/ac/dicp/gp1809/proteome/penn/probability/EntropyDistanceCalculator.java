/* 
 ******************************************************************************
 * File: DistanceCalculator.java * * * Created on 11-30-2007
 *
 * Copyright (c) 2007 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn.probability;

import java.util.Arrays;

import cn.ac.dicp.gp1809.util.math.Logarithm;


/**
 * Calculate the distance between the identified peptides.
 * Before calculation of the distance, the peptide attributes must be 
 * normalized.
 * 
 * The weights of different attributes are first genrated from the relative information gain which
 * can be calculated by the loss of information entropy. The weight can be considered 
 * as the relative importance in discriminant performance of different score. Therefore,
 * in KNN distance calculation, the dimension with bigger weight, should extend by the square 
 * of its weight.
 * 
 * @author Xinning
 * @version 0.6, 04-08-2008, 10:37:28
 */
public class EntropyDistanceCalculator extends AbstractDistanceCalculator{
	
	public EntropyDistanceCalculator(PepNorm[] peps) {
		super(peps);
		System.out.println("EntropyDistanceCalculator is used.");
	}

	@Override
	protected void calculateWeights(PepNorm[] peps){
		this.getWeightByEntropy(peps);
	}
	
	/*
	 * The information entropy is commonly used in decision tree.
	 * The attribute with big information gain should be assigned with big weight
	 */
	private void getWeightByEntropy(PepNorm[] peps){
		double total = peps.length;
		int c = 0;
		for(int i=0;i<total;i++){
			if(peps[i].isRev)
				c ++;
		}
		double totalfalse = c;

		//For peptide for reversed database search, this value commonly be 1, 
		//but for accuracy compution, this value may be bias.
		double originalen = this.getEntropy(total, totalfalse, 0, 0);
		
		PepNorm[] pepsclone = peps.clone();
		
		//The entropies
		double xcen = 1d, dcnen = 1d, spen = 1d, rspen = 1d, dmsen = 1d, ionsen = 1d,
				mpfen = 1d, simen = 1d;
		
		double totalcount = 0d;
		
		/*
		 * ----------Xcorr --------------------------------------
		 */
		Arrays.sort(pepsclone, new PepNormComparator(PepNormComparator.SORT_BY_XCORR));
		
		//the previous peptide score value
		float prestat = -1f;
		int curtfalse = 0;
		for(int i=0;i<total;i++){
			PepNorm pt = pepsclone[i];
			
			//The state change point should be the point where the entropy decrease.
			if(pt.getXcn() > prestat){
				prestat = pt.getXcn();
				double curten = this.getEntropy(total, totalfalse, i, curtfalse);
				if(curten<xcen){ xcen = curten; totalcount = i;}
			}
			
			if(pt.isRev)
				curtfalse ++;
		}
		
		System.out.println("Xcorr: count = "+totalcount);
		
		
		/*
		 * ----Delta cn --------------------------------------
		 */
		Arrays.sort(pepsclone, new PepNormComparator(PepNormComparator.SORT_BY_DCN));

		prestat = -1f;
		curtfalse = 0;
		for(int i=0;i<total;i++){
			PepNorm pt = pepsclone[i];
			
			if(pt.getDcn() > prestat){
				prestat = pt.getDcn();
				double curten = this.getEntropy(total, totalfalse, i, curtfalse);
				if(curten<dcnen) {dcnen = curten; totalcount = i;}
			}
			
			if(pt.isRev)
				curtfalse ++;
		}
		
		System.out.println("Dcn: count = "+totalcount);
		
		/*
		 * ----Sp --------------------------------------
		 */
		Arrays.sort(pepsclone, new PepNormComparator(PepNormComparator.SORT_BY_SP));

		prestat = -1f;
		curtfalse = 0;
		for(int i=0;i<total;i++){
			PepNorm pt = pepsclone[i];
			
			if(pt.getSpn() > prestat){
				prestat = pt.getSpn();
				double curten = this.getEntropy(total, totalfalse, i, curtfalse);
				if(curten<spen) {spen = curten; totalcount = i;}
			}
			
			if(pt.isRev)
				curtfalse ++;
		}
		System.out.println("Sp: count = "+totalcount);
		
		/*
		 * ----Rsp --------------------------------------
		 */
		Arrays.sort(pepsclone, new PepNormComparator(PepNormComparator.SORT_BY_RSP));
		
		curtfalse = 0;
		prestat = -1f;
		for(int i=0;i<total;i++){
			PepNorm pt = pepsclone[i];
			
			if(pt.getRspn() > prestat){
				prestat = pt.getRspn();
				double curten = this.getEntropy(total, totalfalse, i, curtfalse);
				if(curten<rspen) {rspen = curten; totalcount = i;}
			}
			
			if(pt.isRev())
				curtfalse ++;
		}
		System.out.println("Rsp: count = "+totalcount);
		
	//-----------DeltaMS------------------------------------------------------
		Arrays.sort(pepsclone, new PepNormComparator(PepNormComparator.SORT_BY_DMS));
		
		prestat = -1f;
		curtfalse = 0;
		for(int i=0;i<total;i++){
			PepNorm pt = pepsclone[i];
			
			if(pt.getDMS() > prestat){
				//The change point.
				prestat = pt.getDMS();
				double curten = this.getEntropy(total, totalfalse, i, curtfalse);
				if(curten<dmsen) {dmsen = curten; totalcount = i;}
			}
			
			if(pt.isRev())
				curtfalse ++;
		}
		System.out.println("dms: count = "+totalcount);
		/*
		 * ----------ions --------------------------------------
		 */
		Arrays.sort(pepsclone, new PepNormComparator(PepNormComparator.SORT_BY_IONS));
		
		//the previous peptide states (reversed or forward)
		prestat = -1f;
		curtfalse = 0;
		for(int i=0;i<total;i++){
			PepNorm pt = pepsclone[i];
			
			//The state change point should be the point where the entropy decrease.
			if(pt.getIons() > prestat){
				prestat = pt.getIons();
				double curten = this.getEntropy(total, totalfalse, i, curtfalse);
				if(curten<ionsen) {ionsen = curten; totalcount = i;}
			}
			
			if(pt.isRev())
				curtfalse ++;
		}
		System.out.println("ions: count = "+totalcount);
		/*
		 * ----------mpf --------------------------------------
		 */
		Arrays.sort(pepsclone, new PepNormComparator(PepNormComparator.SORT_BY_MPF));
		
		//the previous peptide states (reversed or forward)
		prestat = -1f;
		curtfalse = 0;
		for(int i=0;i<total;i++){
			PepNorm pt = pepsclone[i];
			
			//The state change point should be the point where the entropy decrease.
			if(pt.getMPF() > prestat){
				prestat = pt.getMPF();
				double curten = this.getEntropy(total, totalfalse, i, curtfalse);
				if(curten<mpfen) {mpfen = curten; totalcount = i;}
			}
			
			if(pt.isRev())
				curtfalse ++;
		}
		System.out.println("mpf: count = "+totalcount);
		/*
		 * ----------sim --------------------------------------
		 */
		Arrays.sort(pepsclone, new PepNormComparator(PepNormComparator.SORT_BY_IONS));
		
		//the previous peptide states (reversed or forward)
		prestat = -1f;
		curtfalse = 0;
		for(int i=0;i<total;i++){
			PepNorm pt = pepsclone[i];
			
			//The state change point should be the point where the entropy decrease.
			if(pt.getSim() > prestat){
				prestat = pt.getSim();
				double curten = this.getEntropy(total, totalfalse, i, curtfalse);
				if(curten<simen) {simen = curten; totalcount = i;}
			}
			
			if(pt.isRev())
				curtfalse ++;
		}
		System.out.println("sim: count = "+totalcount);
		//set xcweight as 1;
		float xcengain = (float) (originalen-xcen);
		float dcnweight = (float) ((originalen-dcnen)/xcengain);
		float spweight = (float) ((originalen-spen)/xcengain);
		float rspweight = (float) ((originalen-rspen)/xcengain);
		float dmsweight = (float) ((originalen-dmsen)/xcengain);
		float ionsweight = (float) ((originalen-ionsen)/xcengain);
		float mpfweight = (float) ((originalen-mpfen)/xcengain);
		float simweight = (float) ((originalen-simen)/xcengain);
		
		this.setWeights2(1f, (dcnweight*dcnweight),(spweight*spweight),
				(rspweight*rspweight), (dmsweight*dmsweight),(ionsweight*ionsweight),
				mpfweight*mpfweight, simweight*simweight);
	}
	
	/*
	 * Compute the current entropy
	 * @param total total number of peptide
	 * @param totalfalse total number of false positive peptides
	 * @param curti current split point
	 * @param curtfalse current false positive number within the current split point
	 */
	private double getEntropy(double total, double totalfalse, double curti, double curtfalse){
		double e1 = 0d;
		double percent = curti/total;
		if(percent > 0.000001d){
			double p11 = curtfalse*2/curti;
			p11 = p11 > 1d? 1d : p11;
			double p12 = 1d-p11;
			e1 = percent*(getInfValue(p11)+getInfValue(p12));
		}
		
		double e2 = 0d;
		percent = 1d - percent;
		if(percent>0.000001d){
			double p21 = (totalfalse-curtfalse)*2/(total-curti);
			p21 = p21 > 1d ? 1d : p21;
			double p22 = 1- p21;
			e2 = percent*(getInfValue(p21)+getInfValue(p22));
		}

//		System.out.println("Total: "+curti+", false: "+curtfalse+" entropy: "+(e1+e2));
		
		return e1 + e2;
	}
	
	private static double getInfValue(double pi){
		if(pi<0.000001d)//the percent 1/1000,000
			return 0;
		return -pi*Logarithm.log2(pi);
	}
}
