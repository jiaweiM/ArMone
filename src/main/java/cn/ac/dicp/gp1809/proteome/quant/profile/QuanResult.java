/* 
 ******************************************************************************
 * File:LabelQResult.java * * * Created on 2010-5-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.profile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelQuanUnit;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2010-5-17, 18:36:25
 */
public class QuanResult {

//	private PeptidePair [] pairs;
	private LabelQuanUnit [] units;
	private String [] refs;
	private int ratioNum;
	private int pairNum;
	private int index;
	private double [] ratio;
	private double [] RSD;
	private double [] P_value;
	
	private boolean isUnique;
	private boolean isUse;
	private boolean noVariModPep;
	private boolean normal;
	
	private ArrayList <Double> [] feaIntenList;
	private double [] medianIntensity;
	private double [] score;

	private DecimalFormat df4 = DecimalFormats.DF0_4;
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;
	private DecimalFormat dfE3 = DecimalFormats.DF_E3;

	/**
	 * A LabelQResult is a quantitative result of a protein group.
	 * @param refs
	 * @param units
	 * @throws IOException
	 */
	public QuanResult(String[] refs, LabelQuanUnit [] units) throws IOException{
		this.refs = refs;
		this.units = units;
		if(units==null || units.length==0){
			throw new IOException("The peptide pair of "+refs[0]+" is null.");
		}else{
			this.ratioNum = units[0].getRatioNum();
			this.pairNum = units[0].getPairNum();
		}
		this.RSD = new double[ratioNum];
		this.P_value = new double[ratioNum];
		this.ratio = this.getRatio2();
		this.setFeaIntenList();
	}
	
	/**
	 * A LabelQResult is a quantitative result of a protein group.
	 * @param refs
	 * @param units
	 * @param noVariModPep
	 * @throws IOException
	 */
	public QuanResult(String[] refs, LabelQuanUnit [] units, boolean noVariModPep) throws IOException{
		this.refs = refs;
		this.units = units;
		this.noVariModPep = noVariModPep;
		if(units==null || units.length==0){
			throw new IOException("The peptide pair of "+refs[0]+" is null.");
		}else{
			this.ratioNum = units[0].getRatioNum();
			this.pairNum = units[0].getPairNum();
		}
		this.RSD = new double[ratioNum];
		this.P_value = new double[ratioNum];
		this.ratio = this.getRatio2();
		this.setFeaIntenList();
	}
	
	/**
	 * @return the units
	 */
	public LabelQuanUnit[] getUnits() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(LabelQuanUnit[] units) {
		this.units = units;
	}

	private void setFeaIntenList(){
		
		this.feaIntenList = new ArrayList [pairNum];
		for(int i=0;i<pairNum;i++){
			feaIntenList[i] = new ArrayList <Double>();
		}
		for(int i=0;i<units.length;i++){

			double [] intens = units[i].getIntensity();
			for(int j=0;j<intens.length;j++){
//				double inten = MathTool.getTotal(fList[j]);
//				this.feaIntenList[j].addAll(fList[j]);
				if(intens[j]>0)
					this.feaIntenList[j].add(intens[j]);
			}
		}
		this.medianIntensity = new double [pairNum];
		for(int i=0;i<pairNum;i++){
			if(this.feaIntenList[i].size()>0){
				medianIntensity[i] = MathTool.getMedianInDouble(feaIntenList[i]);
			}else{
				medianIntensity[i] = 0;
			}
		}
		
/*		double [][] intens = new double[pairNum][];
		for(int i=0;i<pairNum;i++){
			intens[i] = new double[feaIntenList[i].size()];
			for(int j=0;j<intens[i].length;j++){
				intens[i][j] = feaIntenList[i].get(j);
			}
		}

		int index = 0;
		for(int i=0;i<pairNum;i++){
			for(int j=i+1;j<pairNum;j++){
				ArrayList <double[]> total = new ArrayList <double[]>();
				if(intens[i].length<2 || intens[j].length<2){
					this.P_value[index] = 0;
					index++;
					continue;
				}
					
				total.add(intens[i]);
				total.add(intens[j]);
				
				OneWayAnovaImpl anovaImpl = new OneWayAnovaImpl();
				try {
					this.P_value[index] = anovaImpl.anovaPValue(total);
					if(Double.isNaN(P_value[index]))
						P_value[index] = 0.0;

					this.P_value[index] = Double.parseDouble(dfE3.format(P_value[index]));
				
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				index++;
			}
		}
*/		
	}
	
	public String getQRef(){
		StringBuilder sb = new StringBuilder();
		if(refs.length==1){
			if(this.isUnique){
				sb.append(index).append("\t");
			}else{
				sb.append("(").append(index).append(")").append("\t");
			}
			sb.append(refs[0]).append("\t").append(isUnique? "1" : "0").append("\t").
				append(getRatioStr()).append(getIntenStr()).append("\n");
		}else{
			if(this.isUnique){
				for(int i=0;i<refs.length;i++){
					sb.append(index+"-"+(i+1)).append("\t")
						.append(refs[i]).append("\t").append(isUnique? "1" : "0").append("\t").
						append(getRatioStr()).append(getIntenStr()).append("\n");
				}
			}else{
				for(int i=0;i<refs.length;i++){
					sb.append("(").append(index+"-"+(i+1)).append(")").append("\t")
						.append(refs[i]).append("\t").append(isUnique? "1" : "0").append("\t").
						append(getRatioStr()).append(getIntenStr()).append("\n");
				}
			}			
		}		
		return sb.toString();
	}
	
	public void setNoVariModPep(boolean noVariModPep){
		this.noVariModPep = noVariModPep;
	}
	
	public boolean getNoVariModPep(){
		return noVariModPep;
	}
	
	public void setNormal(boolean normal){
		this.normal = normal;
	}
	
	public boolean getNormal(){
		return normal;
	}
	
	public boolean getUse(){
		return isUse;
	}
	
/*	
	public double [] getRatio(){
		double [] ratio = new double[pairNum];
		int num = 0;
		Arrays.sort(pairs);
		HashMap <String,HashSet<String>> keyMap = new HashMap <String,HashSet<String>>();
		HashSet <PeptideLabelPair> pairSet = new HashSet <PeptideLabelPair> ();
		
		for(int i=0;i<pairs.length;i++){
			String seq = pairs[i].getSequence();
			String src = pairs[i].getSrc();
			short charge = pairs[i].getCharge();
			char [] chars = seq.toCharArray();
			Arrays.sort(chars);
			String key = (new String(chars))+charge;
			if(!keyMap.containsKey(key)){
				HashSet<String> srcset = new HashSet<String>();
				srcset.add(src);
				keyMap.put(key,srcset);
				pairSet.add(pairs[i]);			
			}else{
				if(!keyMap.get(key).contains(src)){
					keyMap.get(key).add(src);
					pairSet.add(pairs[i]);
				}
			}
			
		}
		Iterator <PeptideLabelPair> it = pairSet.iterator();
		while(it.hasNext()){
			PeptideLabelPair p = it.next();
			double [] r = p.getRatioArray();
			boolean add1 = true;
			for(int j=0;j<pairNum;j++){
				if(r[j]==0){
					add1 = false;
					break;
				}
			}
			if(add1){	
				num++;
				for(int j=0;j<pairNum;j++){					
					ratio[j] += r[j];						
				}
			}
			if(num>3)
				break;
		}
		for(int j=0;j<pairNum;j++){
			if(num!=0){
				ratio[j] = ratio[j]/(double)num;
			}else{
				ratio[j] = 0;
			}
		}

		return ratio;		
	}
*/	
	public double [] getRatio(){
		return ratio;
	}
	
	private double [] getRatio2(){
		
		double [] ratio = new double[ratioNum];
		Arrays.fill(ratio, 0.0);
		boolean [] h1000 = new boolean [ratioNum];
		Arrays.fill(h1000, false);
		boolean [] h0 = new boolean [ratioNum];
		Arrays.fill(h0, false);
		
		ArrayList <Double> [] ratioList = new ArrayList [ratio.length];
		for(int i=0;i<ratioList.length;i++){
			ratioList[i] = new ArrayList<Double>();
		}
				
		HashSet <String> uniqueSet = new HashSet <String>();
		
		for(int i=0;i<units.length;i++){

			if(noVariModPep){
				if(units[i].hasVariMod())
					continue;
			}

			String seq = units[i].getSequence();
			char [] chars = seq.toCharArray();
			Arrays.sort(chars);
			String key = (new String(chars));
			String src =units[i].getSrc();
			key += src;
			
			if(!uniqueSet.contains(key)){
				
				uniqueSet.add(key);
				double [] rs = units[i].getRatio();
				for(int j=0;j<rs.length;j++){
					if(rs[j]==0){
						h0[j] = true;
					}else if(rs[j]==1000){
						h1000[j] = true;
					}else{
						ratioList[j].add(rs[j]);				
					}
				}
			}		
		}
		
		boolean use = false;
		for(int i=0;i<ratioList.length;i++){
			if(ratioList[i].size()>0){
				use = true;
				break;
			}
		}
		
		if(!use){
			
			this.isUse = false;
			for(int i=0;i<units.length;i++){
				
				String seq = units[i].getSequence();
				char [] chars = seq.toCharArray();
				Arrays.sort(chars);
				String key = (new String(chars));
				String src = units[i].getSrc();
				key += src;
				
				if(!uniqueSet.contains(key)){
					
					uniqueSet.add(key);
					double [] rs = units[i].getRatio();
					for(int j=0;j<rs.length;j++){
						if(rs[j]==0){
							h0[j] = true;
						}else if(rs[j]==1000){
							h1000[j] = true;
						}else{
							ratioList[j].add(rs[j]);				
						}
					}
				}
			}
		}else{
			
			this.isUse = true;
		}

		for(int i=0;i<RSD.length;i++){
			this.RSD[i] = MathTool.getRSDInDouble(ratioList[i]);
		}
		
		for(int i=0;i<ratio.length;i++){
			
			if(ratioList[i].size()==0){
				
				if(h1000[i]){
					if(h0[i]){
						ratio[i] = 0;
					}else{
						ratio[i] = 1000;
					}
				}else{
					ratio[i] = 0;
				}
				
			}else if(ratioList[i].size()>3){
				
				double med = MathTool.getMedianInDouble(ratioList[i]);
				ratio[i] = med;
				
			}else{
				
				double ave = MathTool.getAveInDouble(ratioList[i]);
				double med = MathTool.getMedianInDouble(ratioList[i]);
				ratio[i] = (ave+med)/2.0;
				ratio[i] = Double.parseDouble(df4.format(ratio[i]));
			}
		}
/*		
		for(int i=0;i<ratio.length;i++){
			ArrayList <double[]> anovalist = new ArrayList <double[]>();
			double [] d1 = new double [ratioList[i].size()];
			double [] d2 = new double [ratioList[i].size()];
			for(int j=0;j<d1.length;j++){
				d1[j] = ratioList[i].get(j);
				d2[j] = 0.0;
			}
			anovalist.add(d1);
			anovalist.add(d2);
			
			OneWayAnovaImpl anovaImpl = new OneWayAnovaImpl();
			try {
				this.anova[i] = anovaImpl.anovaPValue(anovalist);
				System.out.println(this.anova[i]);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
*/		
		return ratio;
	}

/*	
	private double getAnovaFValue(){
		ArrayList <ArrayList <Double>> datalist = new ArrayList <ArrayList <Double>> ();
		for(int i=0;i<ratioNum;i++){
			ArrayList <Double> dlist = new ArrayList <Double>();
			datalist.add(dlist);
		}
		for(int i=0;i<pairs.length;i++){
			double [] rs = pairs[i].getRatios();
			for(int j=0;j<rs.length;j++){
				datalist.get(j).add(rs[j]);
			}
		}
		double f = AnovaSF.Analysis(datalist);
		return f;
	}
*/	
	/**
	 * The abundance ratio of the protein.
	 * @return
	 */
	private String getRatioStr(){
		StringBuilder sb = new StringBuilder();
		for(int k=0;k<ratio.length;k++){
			sb.append(ratio[k]).append("\t");
			sb.append(dfPer.format(RSD[k])).append("\t");
//			sb.append(P_value[k]).append("\t");
		}			
		return sb.toString();
	}
	
	private String getIntenStr(){
		StringBuilder sb = new StringBuilder();
		for(int k=0;k<this.medianIntensity.length;k++){
			sb.append(medianIntensity[k]).append("\t");
		}			
		return sb.toString();
	}

	public String [] getRefs(){
		return refs;
	}
	
	public void setRefs(String [] refs){
		this.refs = refs;
	}
	
	public String getDeleRef(){
		return refs[0];
	}

	public double [] getRSD(){
		return this.RSD;
	}
	
	public void setUnique(boolean unique){
		this.isUnique = unique;
	}
	
	public boolean getUnique(){
		return isUnique;
	}

	public boolean validata(){
		return this.validata(0);
	}
	
	public boolean validata(int pairNum){
		
/*		for(int i=0;i<ratio.length;i++){
			if(ratio[i]==0)
				return false;
		}
		PeptidePair [] features = this.features;
		ArrayList <PeptidePair> ps = new ArrayList<PeptidePair>();
		for(int i=0;i<features.length;i++){
//			boolean add = pairs[i].getUse();			
//			if(add){
				ps.add(features[i]);
//			}
		}
*/		
		if(this.getDeleRef().contains("REV_"))
			return false;
		
		if(units.length >= pairNum)
			return true;
		else
			return false;
	}
	
	public boolean validataNoZero(){
		for(int i=0;i<ratio.length;i++){
			if(ratio[i]==0)
				return false;
		}
		return true;
	}
	
	public boolean containPair(String pairSeq){
		boolean contain = false;
		for(int i=0;i<units.length;i++){
			String si = units[i].getSequence();
			if(si.equals(pairSeq)){
				contain = true;
				break;
			}
		}
		return contain;
	}
	
	public LabelQuanUnit getPair(String pairSeq){
		for(int i=0;i<units.length;i++){
			String si = units[i].getSequence();
			if(si.equals(pairSeq)){
				return units[i];
			}
		}
		return null;
	}
	
	public HashMap <String, Double> getPepRatioMap(){
		
		HashMap <String, double[]> pepResMap = new HashMap <String, double[]> ();
		HashMap <String, Double> ratioMap = new HashMap <String, Double> ();
		for(int i=0;i<this.units.length;i++){
			String pep = units[i].getSequence();
			if(pepResMap.containsKey(pep)){
				double [] intens1 = units[i].getIntensity();
				double [] intens2 = pepResMap.get(pep);
				if((intens1[0]+intens1[1])>(intens2[0]+intens2[1]))
					pepResMap.put(pep, intens1);
			}else{
				pepResMap.put(pep, units[i].getIntensity());
			}
		}
		Iterator <String> it = pepResMap.keySet().iterator();
		while(it.hasNext()){
			String pep = it.next();
			double [] intens = pepResMap.get(pep);
			Double ratio = intens[0]==0 ? 0f : intens[1]/intens[0];
			ratioMap.put(pep, ratio);
		}
		return ratioMap;
	}
	
	/*public HashMap <String, LabelQuanUnit> getPepPairMap(){
		HashMap <String, double[]> pepResMap = new HashMap <String, double[]> ();
		HashMap <String, LabelQuanUnit> unitmap = new HashMap <String, LabelQuanUnit> ();
		for(int i=0;i<this.units.length;i++){
			String pep = units[i].getSequence();
			if(pepResMap.containsKey(pep)){
				double [] intens1 = units[i].getIntensity();
				double [] intens2 = pepResMap.get(pep);
				if((intens1[0]+intens1[1])>(intens2[0]+intens2[1]))
					pepResMap.put(pep, intens1);
			}else{
				pepResMap.put(pep, units[i].getIntensity());
			}
		}
		return unitmap;
	}*/
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public int getIndex(){
		return index;
	}

	public void simplify(){
		String [] ref = new String [1];
		ref[0] = this.refs[0];
		this.setRefs(ref);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(getQRef());
		for(int j=0;j<units.length;j++){
			sb.append("\t").append(units[j].toString()).append("\n");
		}
		
		return sb.toString();
	}
}
