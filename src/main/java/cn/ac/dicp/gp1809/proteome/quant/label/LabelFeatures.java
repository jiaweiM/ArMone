/*
 ******************************************************************************
 * File:Features.java * * * Created on 2010-3-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import mr.go.sgfilter.SGFilter;

import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * Features are a set of same Feature approximatively in continuous scans
 * @author ck
 *
 * @version 2010-3-24, 09:18:58
 */
public class LabelFeatures implements Comparable <LabelFeatures> {

	private HashMap <Integer, LabelFeature> feaMap;
	private double [] masses;
	private int [] scanList;
	private double [][] intenList;
	private double [] totalLabelIntens;
	private double [] rtList;
	private double [] idenRtList;
	protected double [] ratios;
	/**
	 * used for turn over analysis
	 */
	private double [] rias;
	private int feaNum;
	private int presentFeaNum;
	private double pepMass;

	/**
	 * The rt of begin, max, end
	 */
	private double [] rtRange;

	private int charge;
	private int labelCount;
	private int ratioCount;
	private boolean validate;
	private boolean use;

	protected boolean normal;
	protected int [] select;
	protected double [] normalFactor;
	protected LabelType labeltype;

	protected DecimalFormat df4 = DecimalFormats.DF0_4;
	protected DecimalFormat dfE4 = DecimalFormats.DF_E4;

	public LabelFeatures(double [] masses, int charge){
		this.charge = charge;
		this.feaMap = new HashMap <Integer, LabelFeature> ();
		this.masses = masses;
		this.labelCount = masses.length;
		this.ratioCount = labelCount*(labelCount-1);
	}

	public LabelFeatures(double [] masses, int charge, double [] idenRtList){
		this.charge = charge;
		this.idenRtList = idenRtList;
		this.feaMap = new HashMap <Integer, LabelFeature> ();
		this.masses = masses;
		this.labelCount = masses.length;
		this.ratioCount = labelCount*(labelCount-1);
	}

	public LabelFeatures(double [] masses, int [] scanList, double [][] intenList,
			double [] totalLabelIntens, double [] rtList, double [] ratios, LabelType labeltype, int charge){
		this.charge = charge;
		this.feaMap = new HashMap <Integer, LabelFeature> ();
		this.masses = masses;
		this.labelCount = masses.length;
		this.ratioCount = labelCount*(labelCount-1)/2;
		this.scanList = scanList;
		this.intenList = intenList;
		this.totalLabelIntens = totalLabelIntens;
		this.rtList = rtList;
		this.ratios = ratios;
		this.labeltype = labeltype;
	}

	public void addFeature(LabelFeature fea){
		feaMap.put(fea.getScanNum(), fea);
	}

	public int getCharge(){
		return charge;
	}

	public double getPepMass(){
		return this.pepMass;
	}

	public void setPepMass(double pepMass){
		this.pepMass = pepMass;
	}

	public double [] getMasses(){
		return masses;
	}

	public double [] getRTRange(){
		return rtRange;
	}

	public double [] getTotalIntens(){
		return totalLabelIntens;
	}

	public double [] getRatios(){
		return ratios;
	}

	public int getPresentFeasNum(){
		return presentFeaNum;
	}

	public void setPresentFeasNum(int presentFeaNum){
		this.presentFeaNum = presentFeaNum;
	}

	/**
	 * @return the rias
	 */
	public double[] getRias() {
		return rias;
	}

	public double[] getRiasSixplex() {
		double [] rias = new double [3];
		rias[0] = this.rias[0];
		rias[1] = this.rias[18];
		rias[2] = this.rias[28];
		return rias;
	}

	/**
	 * @param rias the rias to set
	 */
	public void setRias(double[] rias) {
		this.rias = rias;
	}

	/**
	 * The inherited class should overload this method.
	 *
	 * @param fea
	 * @return
	 */
	public boolean contain(LabelFeature fea){
		return false;
	}

	public int getLength(){
		return feaNum;
	}

	public LabelFeature getFeature(int scanNum){
		return feaMap.get(scanNum);
	}

	public int getLastScan(){
		return scanList[feaNum-1];
	}

	public int [] getScanList(){
		return scanList;
	}

	public HashMap <Integer, LabelFeature> getFeaMap(){
		return feaMap;
	}

	public double [][] getIntenList(){
		return this.intenList;
	}

	public double [] getPartIntenList(double rtBeg, double rtEnd){

		int idbeg = Arrays.binarySearch(rtList, rtBeg);
		int idend = Arrays.binarySearch(rtList, rtEnd);

		if(idbeg<0){
			idbeg = -idbeg-1;
		}

		if(idend<0){
			idend = -idend-1;
		}

		double [] pIntenList = new double [idend-idbeg];

		if(pIntenList.length==0)
			return null;

		System.arraycopy(intenList, idbeg, pIntenList, 0, pIntenList.length-1);

		return pIntenList;
	}

	public double [] getRTList(){
		return this.rtList;
	}

	public double [] getPartRTList(double rtBeg, double rtEnd){

		int idbeg = Arrays.binarySearch(rtList, rtBeg);
		int idend = Arrays.binarySearch(rtList, rtEnd);

		if(idbeg<0){
			idbeg = -idbeg-1;
		}

		if(idend<0){
			idend = -idend-1;
		}

		double [] prtlist = new double [idend-idbeg];

//		System.out.println(rtBeg+"\t"+rtEnd+"\t"+idbeg+"\t"+idend+"\t"+rtList.length+"\t"+prtlist.length);
		if(prtlist.length==0)
			return null;

		System.arraycopy(rtList, idbeg, prtlist, 0, prtlist.length-1);

		return prtlist;
	}

	/**
	 * @deprecated
	 */
	public void setInfo(){

		if(feaMap.size()==0)
			return;

		Integer [] scanList = feaMap.keySet().toArray(new Integer[feaMap.size()]);
		Arrays.sort(scanList);

		int validateLenght = scanList.length;

		this.scanList = new int [validateLenght];
		this.intenList = new double [validateLenght][];
		this.rtList = new double [validateLenght];
		this.ratios = new double [ratioCount];
		this.totalLabelIntens = new double [labelCount];

		ArrayList <Double> [] ratiolist = new ArrayList [ratioCount];
		for(int i=0;i<ratiolist.length;i++){
			ratiolist[i] = new ArrayList <Double>();
		}

		ArrayList <Double> [] ilist = new ArrayList [labelCount];
		for(int i=0;i<ilist.length;i++){
			ilist[i] = new ArrayList <Double>();
		}

		for(int i=0;i<validateLenght;i++){

			this.scanList[i] = scanList[i];
			LabelFeature fea = feaMap.get(scanList[i]);
			this.intenList[i] = fea.getIntens();
			this.rtList[i] = fea.getRT();

			double [] intens = fea.getIntens();
			for(int j=0;j<intens.length;j++){
				ilist[j].add(intens[j]);
			}

			double [] fearatio = fea.getRatios();
			for(int j=0;j<fearatio.length;j++){
				if(fearatio[j]!=0){
					ratiolist[j].add(fearatio[j]);
				}
			}
		}

		double [] top8Intens = new double[labelCount];
		for(int i=0;i<labelCount;i++){
			totalLabelIntens[i] = MathTool.getTotalInDouble(ilist[i]);
			top8Intens[i] = MathTool.getTopnAve(ilist[i], 8);
			if(totalLabelIntens[i]>0){
				presentFeaNum++;
			}
		}
		int id = 0;
		for(int j=0;j<labelCount;j++){
			for(int k=j+1;k<labelCount;k++){
				if(top8Intens[j]*top8Intens[k]!=0){
					ratios[id++] = (top8Intens[k]/top8Intens[j]);
					ratios[id++] = (top8Intens[j]/top8Intens[k]);
				}else{
					ratios[id++] = (0.0);
					ratios[id++] = (0.0);
				}
			}
		}

		for(int i=0;i<ratios.length;i++){
			if(ratiolist[i].size()>=5){
				ratios[i] = MathTool.getMedianInDouble(ratiolist[i]);
			}
		}
		this.feaNum = feaMap.size();
	}

	public void setInfo2(){

		if(feaMap.size()==0)
			return;

		Integer [] scanList = feaMap.keySet().toArray(new Integer[feaMap.size()]);
		Arrays.sort(scanList);

		double [][] intenlist = new double [labelCount][scanList.length];
		double [] allRtList = new double [scanList.length];

		for(int i=0;i<scanList.length;i++){
//System.out.print(scanList[i]+"\t");
			LabelFeature fea = feaMap.get(scanList[i]);
//System.out.println(Arrays.toString(fea.getIntens())+"\t"+Arrays.toString(fea.getMasses()));			
			double [] intens = fea.getIntens();
			for(int j=0;j<intens.length;j++){
				intenlist[j][i] = intens[j];
			}
			allRtList[i] = fea.getRT();
//System.out.println();
		}

		PixelList [] pixlist = new PixelList [labelCount];
		this.intenList = new double[scanList.length][labelCount];

		int leftMaxId = Integer.MAX_VALUE;
		int rightMaxId = -1;

		double[] coeffs = null;
		SGFilter filter = null;
		if(scanList.length>10){
			coeffs = SGFilter.computeSGCoefficients(scanList.length, scanList.length, 4);
			filter = new SGFilter(scanList.length, scanList.length);
		}

		for(int i=0;i<labelCount;i++){

			double [] filterIntenList = intenlist[i];
			if(coeffs!=null){
				filterIntenList = filter.smooth(intenlist[i], coeffs);
			}
//			System.out.println("361\t"+scanList.length+"\t"+filterIntenList.length);
			for(int j=0;j<scanList.length;j++){
				this.intenList[j][i] = filterIntenList[j];
				if(this.intenList[j][i]<0)
					this.intenList[j][i] = 0;
			}
			pixlist[i] = new PixelList(filterIntenList, allRtList);
			if(pixlist[i].use){
				pixlist[i].findRange();
				if(pixlist[i].maxid<leftMaxId){
					leftMaxId = pixlist[i].maxid;
				}
				if(pixlist[i].maxid>rightMaxId){
					rightMaxId = pixlist[i].maxid;
				}
			}
//System.out.println(i+"\t"+scanList[pixlist[i].begid]+"\t"+scanList[pixlist[i].endid]+"\t"+scanList[pixlist[i].maxid]);
		}

		int [] maxscore = new int [labelCount];
		for(int i=0;i<labelCount;i++){
			int maxi = pixlist[i].maxid;

			for(int j=i+1;j<labelCount;j++){
				int maxj = pixlist[j].maxid;
				if(maxi!=-1 && maxj!=-1 && Math.abs(maxi-maxj)<=2){
					maxscore[i]++;
					maxscore[j]++;
				}
			}
		}

		int maxid = MathTool.getMaxIndex(maxscore);
		int totalMaxId = pixlist[maxid].maxid;
		HashSet <Integer> equalMax = new HashSet <Integer>();

		for(int i=0;i<labelCount;i++){
			pixlist[i].maxscore = maxscore[i];
			if(maxscore[i]==maxscore[maxid] && pixlist[i].maxid>-1){
				equalMax.add(pixlist[i].maxid);
			}
		}
//System.out.println("labelfeatures393\t"+totalMaxId+"\t"+maxscore[maxid]+"\n"+Arrays.toString(maxscore)+"\t"+equalMax);
		if(totalMaxId==-1){
			this.validate = false;
			this.feaNum = feaMap.size();
			this.scanList = new int[scanList.length];
			for(int i=0;i<scanList.length;i++){
				this.scanList[i] = scanList[i];
			}
			this.rtList = allRtList;
			this.totalLabelIntens = new double [labelCount];
			for(int i=0;i<labelCount;i++){
				this.totalLabelIntens[i] = MathTool.getTotal(pixlist[i].intenlist);
			}
			int ratioid = 0;
			this.ratios = new double [ratioCount];
			this.rias = new double [ratioCount];
			for(int i=0;i<labelCount;i++){
				for(int j=i+1;j<labelCount;j++){
					if(this.totalLabelIntens[i]==0){
						if(this.totalLabelIntens[j]==0){
							this.ratios[ratioid] = 0;
							this.rias[ratioid] = 0;
							this.ratios[ratioid+1] = 0;
							this.rias[ratioid+1] = 0;
						}else{
							this.ratios[ratioid] = 0;
							this.rias[ratioid] = 1.0;
							this.ratios[ratioid+1] = 0;
							this.rias[ratioid+1] = 0;
						}
					}else{
						if(this.totalLabelIntens[j]==0){
							this.ratios[ratioid] = 0;
							this.rias[ratioid] = 0;
							this.ratios[ratioid+1] = 0;
							this.rias[ratioid+1] = 1.0;
						}else{
							this.ratios[ratioid] = this.totalLabelIntens[j]/this.totalLabelIntens[i];
							this.ratios[ratioid+1] = this.totalLabelIntens[i]/this.totalLabelIntens[j];
							this.rias[ratioid] = this.ratios[ratioid]/(this.ratios[ratioid]+1.0);
							this.rias[ratioid+1] = 1.0-this.rias[ratioid];
						}
					}
					ratioid += 2;
				}
			}
			return;
		}

		int allBeginId = 0;
		int allEndId = scanList.length-1;

		for(int i=totalMaxId;i>=0;i--){
			double dd = 0;
			for(int j=0;j<labelCount;j++){
				dd += pixlist[j].trueIntenList[i];
			}
			if(dd==0){
				allBeginId = i+1;
				break;
			}
		}

		for(int i=totalMaxId;i<scanList.length;i++){
			double dd = 0;
			for(int j=0;j<labelCount;j++){
				dd += pixlist[j].trueIntenList[i];
			}
			if(dd==0){
				allEndId = i-1;
				break;
			}
		}
//System.out.println("425\t"+scanList[allBeginId]+"\t"+scanList[allEndId]);
		for(int i=0;i<labelCount;i++){
			if(pixlist[i].maxid<=allBeginId || pixlist[i].maxid>=allEndId){
				pixlist[i].reFindRange(equalMax, allBeginId, allEndId);
			}
		}

		if(allEndId-allBeginId<5){
			this.validate = false;
			this.feaNum = feaMap.size();
			this.scanList = new int[scanList.length];
			for(int i=0;i<scanList.length;i++){
				this.scanList[i] = scanList[i];
			}
			this.rtList = allRtList;
			this.totalLabelIntens = new double [labelCount];
			for(int i=0;i<labelCount;i++){
				this.totalLabelIntens[i] = MathTool.getTotal(pixlist[i].intenlist);
			}
			int ratioid = 0;
			this.ratios = new double [ratioCount];
			this.rias = new double [ratioCount];
			for(int i=0;i<labelCount;i++){
				for(int j=i+1;j<labelCount;j++){
					if(this.totalLabelIntens[i]==0){
						if(this.totalLabelIntens[j]==0){
							this.ratios[ratioid] = 0;
							this.rias[ratioid] = 0;
							this.ratios[ratioid+1] = 0;
							this.rias[ratioid+1] = 0;
						}else{
							this.ratios[ratioid] = 0;
							this.rias[ratioid] = 1.0;
							this.ratios[ratioid+1] = 0;
							this.rias[ratioid+1] = 0;
						}
					}else{
						if(this.totalLabelIntens[j]==0){
							this.ratios[ratioid] = 0;
							this.rias[ratioid] = 0;
							this.ratios[ratioid+1] = 0;
							this.rias[ratioid+1] = 1.0;
						}else{
							this.ratios[ratioid] = this.totalLabelIntens[j]/this.totalLabelIntens[i];
							this.ratios[ratioid+1] = this.totalLabelIntens[i]/this.totalLabelIntens[j];
							this.rias[ratioid] = this.ratios[ratioid]/(this.ratios[ratioid]+1.0);
							this.rias[ratioid+1] = 1.0-this.rias[ratioid];
						}
					}
					ratioid += 2;
				}
			}
			return;
		}

		this.totalLabelIntens = new double [labelCount];
		for(int i=0;i<labelCount;i++){
			pixlist[i].setNewIntenlist(allBeginId, allEndId);
			this.totalLabelIntens[i] = MathTool.getTotal(pixlist[i].newIntenList);
		}

		this.scanList = new int [allEndId-allBeginId+1];
		this.intenList = new double [allEndId-allBeginId+1][];
		this.rtList = new double [allEndId-allBeginId+1];

		for(int i=0;i<this.scanList.length;i++){
//System.out.print(scanList[i+allBeginId]+"\t");
			this.scanList[i] = scanList[i+allBeginId];
			this.rtList[i] = allRtList[i+allBeginId];
			this.intenList[i] = new double[labelCount];
			for(int j=0;j<labelCount;j++){
//System.out.print(pixlist[j].newIntenList[i]+"\t");
				this.intenList[i][j] = pixlist[j].newIntenList[i];
			}
//System.out.println();
		}

		int ratioid = 0;
		int noZeroRatioCount = 0;
		this.ratios = new double [ratioCount];
		this.rias = new double [ratioCount];

		for(int i=0;i<labelCount;i++){
			for(int j=i+1;j<labelCount;j++){
				ratios[ratioid] = pixlist[i].getRatio(pixlist[j]);
				if(ratios[ratioid]>0){
					noZeroRatioCount++;
					rias[ratioid] = ratios[ratioid]/(ratios[ratioid]+1.0);

					ratios[ratioid+1] = 1.0/ratios[ratioid];
					rias[ratioid+1] = 1.0-rias[ratioid];

				}else{

					ratios[ratioid+1] = 0.0;
					if(pixlist[j].use){
						rias[ratioid] = 1.0;
						rias[ratioid+1] = 0.0;
					}else{
						if(pixlist[i].use){
							rias[ratioid] = 0.0;
							rias[ratioid+1] = 1.0;
						}else{
							rias[ratioid] = 0.0;
							rias[ratioid+1] = 0.0;
						}
					}
				}
				ratioid += 2;
			}
		}
		this.feaNum = feaMap.size();
		this.validate = (noZeroRatioCount>0);
	}

	public boolean isValidate(){
		return validate;
	}

	public void setValidate(boolean validate){
		this.validate = validate;
	}

	public boolean isUse(){
		if(this.select==null)
			return true;
		else{
			double [] ratios = this.getSelectRatio();
			for(int i=0;i<ratios.length;i++){
				if(ratios[i]==0){
					return false;
				}
			}
			return true;
		}
	}

	public String getAllFeatureInfo(){

		StringBuilder sb = new StringBuilder();

		for(int i=0;i<scanList.length;i++){

		}

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LabelFeatures fs) {
		// TODO Auto-generated method stub
		double d = this.getPepMass();
		double d1 = fs.getPepMass();
		return d>d1 ? 1:-1;
	}

	/**
	 * @return
	 */
	public int getRatioNum() {
		// TODO Auto-generated method stub
		return this.getSelectRatio().length;
	}

	/**
	 * @return
	 */
	public int getPairNum() {
		// TODO Auto-generated method stub
		return labeltype.getLabelNum();
	}

	/**
	 * @return
	 */
	public double[] getSelectRatio() {

		if(normal){
			if(select!=null){
				double [] ratio = new double[select.length];
				for(int i=0;i<ratio.length;i++){
					ratio[i] = Double.parseDouble(df4.format(this.ratios[select[i]]/normalFactor[select[i]]));
				}
				return ratio;

			}else{

				double [] r = new double [ratios.length/2];
				for(int i=0;i<r.length;i++){
					r[i] = Double.parseDouble(df4.format(this.ratios[i*2]/normalFactor[i*2]));
				}
				return r;
			}

		}else{
			if(select!=null){
				double [] ratio = new double[select.length];
				for(int i=0;i<ratio.length;i++){
					ratio[i] = this.ratios[select[i]];
				}
				return ratio;

			}else{
				double [] r = new double [ratios.length/2];
				for(int i=0;i<r.length;i++){
					r[i] = ratios[i*2];
				}
				return r;
			}
		}
	}

	public double[] getSelectRIA() {

		if(normal){
			if(select!=null){
				double [] ria = new double[select.length];
				for(int i=0;i<ria.length;i++){
					if(ria[i]==0 || ria[i]==1){
						ria[i] = this.rias[select[i]];
					}else{
						ria[i] = Double.parseDouble(df4.format(this.rias[select[i]]/(this.rias[select[i]]+normalFactor[select[i]]*(1-this.rias[select[i]]))));
					}
				}
				return ria;

			}else{
				double [] r = new double [ratios.length/2];
				for(int i=0;i<r.length;i++){
					if(this.rias[i*2]==0 || this.rias[i*2]==1){
						r[i] = this.rias[i*2];
					}else{
						r[i] = Double.parseDouble(df4.format(this.rias[i*2]/(this.rias[i*2]+normalFactor[i*2]*(1-this.rias[i*2]))));
					}
				}
				return r;
			}

		}else{
			if(select!=null){
				double [] ria = new double[select.length];
				for(int i=0;i<ria.length;i++){
					ria[i] = this.rias[select[i]];
				}
				return ria;

			}else{
				double [] r = new double [rias.length/2];
				for(int i=0;i<r.length;i++){
					r[i] = rias[i*2];
				}
				return r;
			}
		}
	}

	/**
	 * @param normal
	 */
	public void setNormal(boolean normal) {
		// TODO Auto-generated method stub
		this.normal = normal;
	}

	/**
	 * @param realNormalFactor
	 */
	public void setNormalFactor(double[] normalFactor) {
		// TODO Auto-generated method stub
		this.normalFactor = normalFactor;
	}

	/**
	 * @param select
	 */
	public void setSelectRatio(int[] select) {
		// TODO Auto-generated method stub
		this.select = select;
	}

	/**
	 * @return
	 */
	public String[] getRatioNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public LabelType getLabelType(){
		return labeltype;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.Features#getPairNames()
	 */
	public String[] getFeatureNames() {
		// TODO Auto-generated method stub

		short [] used = labeltype.getUsed();
		String name = labeltype.getLabelName();
		String [] names = new String [used.length];
		for(int i=0;i<names.length;i++){
			names[i] = name+"_"+used[i];
		}

		return names;
	}

	private class PixelList{

		private double [] allRtList;
		private double [] intenlist;
		private ArrayList <Integer> maxIdList;
		private HashSet <Integer> maxIdSet;
		private HashSet <Integer> minIdSet;
		private int maxscore;

		private int begid = -1;
		private int endid = -1;
		private int maxid = -1;
		private int leftZero = -1;
		private int rightZero = -1;
		private double [] trueIntenList;
		private double [] newIntenList;
		private boolean use;

		private PixelList(double [] intens, double [] allRtList){

			this.intenlist = intens;
			this.trueIntenList = new double [intenlist.length];
			this.allRtList = allRtList;
			this.maxIdList = new ArrayList <Integer>();
			this.maxIdSet = new HashSet <Integer>();
			this.minIdSet = new HashSet <Integer>();

			int noZero = 0;
			for(int i=0;i<intenlist.length;i++){
				if(intenlist[i]>0){
					if(leftZero==-1){
						leftZero = i;
					}
					noZero++;
				}
			}

			for(int i=intenlist.length-1;i>=0;i--){
				if(intenlist[i]>0){
					if(rightZero==-1){
						rightZero = i;
					}
				}
			}

			if(noZero<5){
				this.use = false;
			}else{
				this.use = true;
				findMaxId();
			}
		}

		private void findMaxId(){

//			int combineCount = intenlist.length/30;

//			if(combineCount<=1){

//	*			for(int i=0;i<combinelist.length;i++){
//	*				combinelist[i] = intenlist[i];
//*				}

//			}else{
				
/*				for(int i=0;i<combinelist.length;i++){
					
					if(intenlist[i]>0){
						int beg = i-combineCount>=0 ? i-combineCount : 0;
						int end = i+combineCount<=combinelist.length ? i+combineCount : combinelist.length;
						for(int j=beg;j<end;j++){
							combinelist[i] += intenlist[j];
						}
					}
System.out.println("combine\t"+combinelist[i]+"\t"+intenlist[i]);					
				}
*/
//			}

			double max = -1;
			int totalmaxid = -1;

			for(int i=2;i<intenlist.length-2;i++){

				if(intenlist[i]>max){
					max = intenlist[i];
					totalmaxid = i;
				}

				if(intenlist[i]>intenlist[i-1] && intenlist[i-1]>0 && intenlist[i]>intenlist[i-2] && intenlist[i-2]>0
						&& intenlist[i]>intenlist[i+1] && intenlist[i+1]>0 && intenlist[i]>intenlist[i+2]
								 && intenlist[i+2]>0){

					if(intenlist[i]/intenlist[i-1]<10 && intenlist[i]/intenlist[i+1]<10){

						if(maxIdList.size()==0){
							maxIdList.add(i);
							maxIdSet.add(i);
						}else{
							Integer lastId = maxIdList.get(maxIdList.size()-1);
							if(i-lastId>3){
								maxIdList.add(i);
								maxIdSet.add(i);
							}else{
								if(intenlist[i]>intenlist[lastId]){
									maxIdList.remove(lastId);
									maxIdSet.remove(lastId);
									maxIdList.add(i);
									maxIdSet.add(i);
								}
							}
						}
					}

				}else if(intenlist[i]>0){

					boolean locmin = true;
					if(intenlist[i-1]>0 && intenlist[i-1]<intenlist[i]){
						locmin = false;
					}
					if(intenlist[i-2]>0 && intenlist[i-2]<intenlist[i]){
						locmin = false;
					}
					if(intenlist[i+1]>0 && intenlist[i+1]<intenlist[i]){
						locmin = false;
					}
					if(intenlist[i+2]>0 && intenlist[i+2]<intenlist[i]){
						locmin = false;
					}

					if(locmin)
						minIdSet.add(i);
				}
			}

//System.out.println("max\t"+maxIdList+"\tmin"+minIdSet);

			if(maxIdList.size()==0){

				this.maxid = totalmaxid;

			}else if(maxIdList.size()==1){

				this.maxid = maxIdList.get(0);

			}else{
				
/*				Double minDiff = Double.MAX_VALUE;
				ArrayList <Integer> less30 = new ArrayList <Integer>();
				
				for(int i=0;i<maxIdList.size();i++){
					double totalRtDiff = 0;
					for(int j=0;j<idenRtList.length;j++){
						totalRtDiff += Math.abs(idenRtList[j]-allRtList[maxIdList.get(i)]);
					}
					totalRtDiff = totalRtDiff/(double)idenRtList.length;
					if(totalRtDiff<0.5){
						less30.add(maxIdList.get(i));
					}
					if(minDiff>totalRtDiff){
						minDiff = totalRtDiff;
						this.maxid = maxIdList.get(i);
					}
//System.out.println("diff\t"+maxIdList.get(i)+"\t"+totalRtDiff);				
				}
				
				if(less30.size()>1){
					double less30Max = 0;
					for(int i=0;i<less30.size();i++){
						if(combinelist[less30.get(i)]>less30Max){
							this.maxid = less30.get(i);
						}
					}
				}
*/				
/*				
				for(int i=0;i<maxIdList.size()-1;i++){
					
					int s1 = 0;
					int s2 = 0;
					
					for(int j=0;j<idenRtList.length;j++){
						
						if(idenRtList[j]<=allRtList[maxIdList.get(i)]){
							
							s1++;
						
						}else if(idenRtList[j]>allRtList[maxIdList.get(i)] && 
								idenRtList[j]<allRtList[maxIdList.get(i+1)]){
							
							double min = combinelist[maxIdList.get(i)];
							double minrt = -1;
							int minid = -1;
							
							for(int k=maxIdList.get(i)+3;k<maxIdList.get(i+1)-2;k++){
								if(combinelist[k]<min){
									min = combinelist[k];
									minrt = allRtList[k];
									minid = k;
								}
							}
							
							if(minrt>0){
								
								if(idenRtList[j]<minrt){
									s1++;
								}else if(idenRtList[j]==minrt){
									s1++;
									s2++;
								}else{
									s2++;
								}

								minIdSet.add(minid);
								
							}else{
								
								if(combinelist[this.maxIdList.get(i)]>combinelist[this.maxIdList.get(i+1)]){
									s1++;
								}else{
									s2++;
								}
							}
						}else{
							s2++;
						}
					}
					
					if(s1>s2){
						
						this.maxid = maxIdList.get(i);

					}else if(s1==s2){
						
						if(combinelist[this.maxIdList.get(i)]>combinelist[this.maxIdList.get(i+1)]){
							
							this.maxid = maxIdList.get(i);
							
						}else{
							
							this.maxid = maxIdList.get(i+1);
						}
						
					}else{
						
						this.maxid = maxIdList.get(i+1);
					}
				}
*/

// better than above 2

				int idenId = 0;
				int [] score = new int [maxIdList.size()];

				for(int i=0;i<maxIdList.size()-1;i++){

					if(idenRtList[idenId]<=allRtList[maxIdList.get(i)]){

						score[i]++;
						idenId++;
						i--;

					}else if(idenRtList[idenId]>allRtList[maxIdList.get(i)] &&
							idenRtList[idenId]<allRtList[maxIdList.get(i+1)]){

						double min = intenlist[maxIdList.get(i)];
						double minrt = -1;
						int minid = -1;

						for(int j=maxIdList.get(i)+3;j<maxIdList.get(i+1)-2;j++){
							if(intenlist[j]<min){
								min = intenlist[j];
								minrt = allRtList[j];
								minid = j;
							}
						}

						if(minrt>0){

							if(idenRtList[idenId]<minrt){
								score[i]++;
							}else if(idenRtList[idenId]==minrt){
								score[i]++;
								score[i+1]++;
							}else{
								score[i+1]++;
							}

							idenId++;
							i--;
							minIdSet.add(minid);

						}else{

							if(intenlist[this.maxIdList.get(i)]>intenlist[this.maxIdList.get(i+1)]){
								score[i]++;
							}else{
								score[i+1]++;
							}

							idenId++;
							i--;
						}
					}

					if(idenId==idenRtList.length) break;
				}

				score[score.length-1] += (idenRtList.length-idenId);

				int maxScore = -1;
				int maxScoreId = -1;
				for(int i=0;i<score.length;i++){
					if(score[i]>maxScore){
						maxScore = score[i];
						maxScoreId = i;
					}else if(score[i]==maxScore){
						if(intenlist[maxIdList.get(i)]>intenlist[maxIdList.get(maxScoreId)]){
							maxScore = score[i];
							maxScoreId = i;
						}
					}
				}

//System.out.println(Arrays.toString(score));

				this.maxid = maxIdList.get(maxScoreId);

			}
		}

		private void findRange(){

			for(int i=this.maxid-1;i>=0;i--){

				if(minIdSet.contains(i) && this.maxid-i>=3){
					begid = i;
					break;
				}else{
					if(allRtList[this.maxid]-allRtList[i]>1.0 || i==leftZero){
						begid = i;
						break;
					}
				}
			}

			if(begid==-1) begid = 0;

			for(int i=begid;i<this.intenlist.length;i++){
				if(intenlist[i]==0){
					begid = i+1;
				}else{
					break;
				}
			}

			for(int i=this.maxid+1;i<intenlist.length;i++){

				if(minIdSet.contains(i) && i-maxid>=3){
					endid = i;
					break;
				}else{
					if(allRtList[i]-allRtList[this.maxid]>1.0 || i==rightZero){
						endid = i;
						break;
					}
				}
			}

			if(endid==-1) endid = intenlist.length-1;

			for(int i=endid;i>=0;i--){
				if(intenlist[i]==0){
					endid = i-1;
				}else{
					break;
				}
			}

			for(int i=begid;i<=endid;i++){
				if(intenlist[i]<=intenlist[maxid])
					this.trueIntenList[i] = intenlist[i];
			}
		}

		private void reFindRange(HashSet <Integer> possiMaxId, int allBeginId, int allEndId){

			double maxInten = 0;
			int reMaxId = 0;

			for(Integer mi : possiMaxId){
				if(this.intenlist[mi]>maxInten){
					maxInten = this.intenlist[mi];
					reMaxId = mi;
				}
			}

			this.maxid = reMaxId;

			for(int i=this.maxid-1;i>=0;i--){

				if(minIdSet.contains(i) && this.maxid-i>=3){
					begid = i;
					break;
				}else{
					if(allRtList[this.maxid]-allRtList[i]>0.6 || i==leftZero || i<=allBeginId){
						begid = i;
						break;
					}
				}
			}

			if(begid==-1) begid = 0;

			for(int i=begid;i<this.intenlist.length;i++){
				if(intenlist[i]==0){
					begid = i+1;
				}else{
					break;
				}
			}

			for(int i=this.maxid+1;i<intenlist.length;i++){

				if(minIdSet.contains(i) && i-maxid>=3){
					endid = i;
					break;
				}else{
					if(allRtList[i]-allRtList[this.maxid]>0.6 || i==rightZero || i>allEndId){
						endid = i;
						break;
					}
				}
			}

			if(endid==-1) endid = intenlist.length-1;

			for(int i=endid;i>=0;i--){
				if(intenlist[i]==0){
					endid = i-1;
				}else{
					break;
				}
			}

			for(int i=begid;i<=endid;i++){
				if(intenlist[i]<=intenlist[maxid])
					this.trueIntenList[i] = intenlist[i];
			}
		}

		private void setNewIntenlist(int beginId, int endId){

			if(this.maxid<=beginId || this.maxid>=endId){
				this.use = false;
			}

			this.newIntenList = new double [endId-beginId+1];

			System.arraycopy(trueIntenList, beginId, newIntenList, 0, newIntenList.length);

			this.maxid = maxid-beginId;
		}

		private double getRatio(PixelList p1){

			if(this.use && p1.use){

				ArrayList <Double> list0 = new ArrayList <Double>();
				ArrayList <Double> list1 = new ArrayList <Double>();

				list0.add(this.newIntenList[this.maxid]);
				list1.add(p1.newIntenList[p1.maxid]);

				for(int i=1;;i++){
					if(this.maxid-i>=0 && p1.maxid-i>=0 && this.newIntenList[this.maxid-i]!=0 &&
							p1.newIntenList[p1.maxid-i]!=0){

						list0.add(this.newIntenList[this.maxid-i]);
						list1.add(p1.newIntenList[p1.maxid-i]);

					}else{
						break;
					}
				}
				for(int i=1;;i++){
					if(this.maxid+i<this.newIntenList.length && p1.maxid+i<p1.newIntenList.length &&
							this.newIntenList[this.maxid+i]!=0 && p1.newIntenList[p1.maxid+i]!=0){

						list0.add(this.newIntenList[this.maxid+i]);
						list1.add(p1.newIntenList[p1.maxid+i]);

					}else{
						break;
					}
				}

// use this will be better				
				if((double)Math.abs(this.maxid-p1.maxid)/(double)list1.size()>=0.75){

					if(this.maxscore>p1.maxscore){

						ArrayList <Double> newlist0 = new ArrayList <Double>();
						ArrayList <Double> newlist1 = new ArrayList <Double>();

						for(int i=0;i<this.newIntenList.length;i++){
							if(this.newIntenList[i]>0 && p1.newIntenList[i]>0 &&
									p1.newIntenList[i]<=p1.newIntenList[this.maxid]){

								newlist0.add(this.newIntenList[i]);
								newlist1.add(p1.newIntenList[i]);
							}
						}
						if(newlist0.size()>=5)
							return MathTool.getTotalInDouble(newlist1)/MathTool.getTotalInDouble(newlist0);
						else
							return 0;

					}else if(this.maxscore<p1.maxscore){

						ArrayList <Double> newlist0 = new ArrayList <Double>();
						ArrayList <Double> newlist1 = new ArrayList <Double>();

						for(int i=0;i<this.newIntenList.length;i++){
							if(this.newIntenList[i]>0 && p1.newIntenList[i]>0 &&
									this.newIntenList[i]<=this.newIntenList[p1.maxid]){

								newlist0.add(this.newIntenList[i]);
								newlist1.add(p1.newIntenList[i]);
							}
						}

						if(newlist0.size()>=5)
							return MathTool.getTotalInDouble(newlist1)/MathTool.getTotalInDouble(newlist0);
						else
							return 0;

					}else{
						return 0;
					}

//					return 0;

				}

				if(MathTool.getTotalInDouble(list0)==0 || MathTool.getTotalInDouble(list1)==0){
					return 0;
				}
				return MathTool.getTotalInDouble(list1)/MathTool.getTotalInDouble(list0);

			}else{

				return 0;
			}
		}

	}

}
