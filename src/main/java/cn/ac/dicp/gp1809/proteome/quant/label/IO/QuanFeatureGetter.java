/* 
 ******************************************************************************
 * File: QuanFeatureGetter2.java * * * Created on 2013-4-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.IO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeature;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelQParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;

/**
 * @author ck
 *
 * @version 2013-4-1, 16:48:53
 */
public class QuanFeatureGetter {

	private double ppm = 0f;
	private int leastINum = 0;
	private double rtHalfWidth = 5;

	private double ms1TotalCurrent;
	private MS1ScanList scanlist;

	/**
	 * 
	 * @param file
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public QuanFeatureGetter(String file) throws IOException, XMLStreamException{
		this(file, LabelQParameter.default_parameter());
	}
	
	/**
	 * 
	 * @param file
	 * @param parameter
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public QuanFeatureGetter(String file, LabelQParameter parameter) throws IOException, XMLStreamException{
		this(new File(file), parameter);
	}

	public QuanFeatureGetter(File file) throws IOException, XMLStreamException{
		this(file, LabelQParameter.default_parameter());
	}
	
	/**
	 * @param file
	 * @param parameter
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public QuanFeatureGetter(File file, LabelQParameter parameter) throws IOException, XMLStreamException {
		// TODO Auto-generated constructor stub
		this.createReader(file);
		this.ppm = parameter.getMzTole()/1E6f;
		this.leastINum = parameter.getLeastINum();
	}

	/**
	 * @param file
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	private void createReader(File file) throws IOException, XMLStreamException {
		// TODO Auto-generated method stub
		
		IRawSpectraReader reader;
		
		if(file.getName().endsWith("mzXML")){
			reader = new MzXMLReader(file);
			
		}else if(file.getName().endsWith("mzData")){
			reader = new MzDataStaxReader(file);
		}else{
			throw new IOException("Unknown file type "+file.getAbsolutePath());
		}
		reader.rapMS1ScanList();
		this.scanlist = reader.getMS1ScanList();
		this.ms1TotalCurrent = reader.getMS1TotalCurrent();
		
	}
	
	private Integer [] validateScans(Integer [] scans, double [] scores, double [] rts){
		
		if(rts[rts.length-1]-rts[0]<3) {

			return scans;
			
		}else{
			
			Integer [] newScans = new Integer[scans.length-1];
			double [] newScores = new double[scores.length-1];
			double [] newRts = new double [rts.length-1];
			
			if(scores[0]>scores[scores.length-1]){
				
				System.arraycopy(scans, 0, newScans, 0, newScans.length);
				System.arraycopy(scores, 0, newScores, 0, newScores.length);
				System.arraycopy(rts, 0, newRts, 0, newRts.length);
				
			}else{
				
				System.arraycopy(scans, 1, newScans, 0, newScans.length);
				System.arraycopy(scores, 1, newScores, 0, newScores.length);
				System.arraycopy(rts, 1, newRts, 0, newRts.length);
			}

			scans = newScans;
			scores = newScores;
			rts = newRts;

			return validateScans(scans, scores, rts);
		}
	}

	public LabelFeatures getFeatures( int charge, double [] monoMasses, Integer [] scans, 
			double [] scores, double [] intenMinusRatio){

		int scanNum = scans[0];
		IMS1Scan ms1Scan = null;
		while(true){
			if((ms1Scan=this.scanlist.getScan(scanNum))!=null){
				break;
			}else{
				scanNum--;
			}
		}

		double [] idenRtList = new double [scans.length];
		idenRtList[0] = ms1Scan.getRTMinute();

		if(scans.length>1){
			
			IMS1Scan ms1Scan00 = ms1Scan;
			int idenRtListId = 1;
			int scanNum00 = scanNum;
			double lastRt = idenRtList[0];
			
			while((ms1Scan00=this.scanlist.getNextScan(scanNum00))!=null){
				if(ms1Scan00.getScanNum()>scans[idenRtListId]){
					idenRtList[idenRtListId] = lastRt;
					idenRtListId++;
				}
				if(idenRtListId==idenRtList.length){
					break;
				}
				lastRt = ms1Scan00.getRTMinute();
				scanNum00++;
			}
			
			Integer [] subscans = this.validateScans(scans, scores, idenRtList);
			
			if(subscans.length<scans.length){
				int begid = -1;
				for(int i=0;i<scans.length;i++){
					if(subscans[0]==scans[i]){
						begid = i;
						break;
					}
				}
				
				double [] subRtList = new double [subscans.length];
				System.arraycopy(idenRtList, begid, subRtList, 0, subRtList.length);
				
				scans = subscans;
				idenRtList = subRtList;
				scanNum = scans[0];
				while(true){
					if((ms1Scan=this.scanlist.getScan(scanNum))!=null){
						break;
					}else{
						scanNum--;
					}
				}
			}
		}

//		if(idenRtList[idenRtList.length-1]-idenRtList[0]>3) {
//			return feas;
//		}
//		System.out.println(scans.length);
//		System.out.println("quanfeaturegetter181\tscores\t"+Arrays.toString(scores));
//		System.out.println("quanfeaturegetter181\tscans\t"+Arrays.toString(scans));
//		System.out.println("quanfeaturegetter181\trts\t"+Arrays.toString(idenRtList));
//		System.out.println(Arrays.toString(monoMasses));
		
		LabelFeatures feas = new LabelFeatures(monoMasses, charge, idenRtList);
		
		boolean [] leftMisses = new boolean [monoMasses.length];
		Arrays.fill(leftMisses, false);
		int [] leftMissNum = new int [monoMasses.length];
		Arrays.fill(leftMissNum, 0);
		boolean [] rightMisses = new boolean [monoMasses.length];
		Arrays.fill(rightMisses, false);
		int [] rightMissNum = new int [monoMasses.length];
		Arrays.fill(rightMissNum, 0);

		LabelFeature fea = this.getFeature(charge, monoMasses, ms1Scan, intenMinusRatio, rightMissNum, rightMisses);

		if(!fea.isMiss())
			feas.addFeature(fea);
//System.out.println("221\t"+ms1Scan.getScanNum()+"\t"+feas.getFeaMap().size());		
		IMS1Scan prev;
		int prevscan = scanNum;
		while((prev=scanlist.getPreviousScan(prevscan))!=null){
			prevscan = prev.getScanNum();
			LabelFeature prevfea = this.getFeature(charge, monoMasses, prev, intenMinusRatio, leftMissNum, leftMisses);

			if(prevfea.isMiss()) break;
			else feas.addFeature(prevfea);

//			if(idenRtList[0]-prev.getRTMinute()>rtHalfWidth) break;
		}
//System.out.println("233\t"+feas.getFeaMap().size());
L:		for(int sn=0;sn<scans.length;sn++){
			
			if(scanNum>scans[sn])
				continue;

			if(sn==0){
				
				IMS1Scan next;
				int nextscan = scanNum;

				while((next=scanlist.getNextScan(nextscan))!=null){
					
					nextscan = next.getScanNum();
					LabelFeature nextfea = this.getFeature(charge, monoMasses, next, intenMinusRatio, rightMissNum, rightMisses);

					if(nextfea.isMiss()) break;
					else feas.addFeature(nextfea);
										
//					if(next.getRTMinute()-idenRtList[idenRtList.length-1]>rtHalfWidth) break L;
				}
				
				scanNum = nextscan;
				
			}else{
				
				IMS1Scan nexms1Scan = null;
				int reNexscan = scans[sn];
				while(true){
					if((nexms1Scan=this.scanlist.getScan(reNexscan))!=null){
						break;
					}else{
						reNexscan--;
					}
				}

				LabelFeature reNexfea = this.getFeature(charge, monoMasses, nexms1Scan, intenMinusRatio, rightMissNum, rightMisses);

				if(!reNexfea.isMiss())
					feas.addFeature(reNexfea);
				
				IMS1Scan next;
				int nextscan = reNexscan;

				while((next=scanlist.getNextScan(nextscan))!=null){
					nextscan = next.getScanNum();

					LabelFeature nextfea = this.getFeature(charge, monoMasses, next, intenMinusRatio, rightMissNum, rightMisses);

					if(nextfea.isMiss()) break;
					else feas.addFeature(nextfea);
					
//					if(next.getRTMinute()-idenRtList[idenRtList.length-1]>rtHalfWidth) break L;
				}
				
				scanNum = nextscan;
			}
		}
//System.out.println("291\t"+feas.getFeaMap().size());		
//		feas.setInfo();
		feas.setInfo2();
		return feas;
	}
/*
	public LabelFeature getFeature(int charge, double [] monoMasses, IMS1Scan ms1Scan, double [] intenMinusRatio,
			ArrayList <Double> [] taillist, int [] tailNum, Double limit, int [] missNum, boolean [] misses){

		int scanNum = ms1Scan.getScanNum();
		LabelFeature fea = new LabelFeature(scanNum, charge, monoMasses, ms1Scan.getRTMinute());
		
		double tolerance = this.ppm*monoMasses[0];
		tolerance = tolerance<0.02 ? 0.02 : tolerance;
		IPeak [] peaks = ms1Scan.getPeakList().getPeakList();
		fea.match(peaks, intenMinusRatio, tolerance, taillist, tailNum, limit, missNum, misses);
		
		return fea;
	}
	*/
	public LabelFeature getFeature(int charge, double [] monoMasses, IMS1Scan ms1Scan, double [] intenMinusRatio,
			int [] missNum, boolean [] misses){

		int scanNum = ms1Scan.getScanNum();
		LabelFeature fea = new LabelFeature(scanNum, charge, monoMasses, ms1Scan.getRTMinute());
		
		double tolerance = this.ppm*monoMasses[0];
		tolerance = tolerance<0.02 ? 0.02 : tolerance;
		IPeak [] peaks = ms1Scan.getPeakList().getPeakArray();
		fea.match(peaks, intenMinusRatio, tolerance, missNum, misses);

		return fea;
	}

	public LabelFeatures getFeatures(int charge, double [] monoMasses, Integer [] scans, double [] scores){
	
		int scanNum = scans[0];
		IMS1Scan ms1Scan = null;
		while(true){
			if((ms1Scan=this.scanlist.getScan(scanNum))!=null){
				break;
			}else{
				scanNum--;
			}
		}

		double [] idenRtList = new double [scans.length];
		idenRtList[0] = ms1Scan.getRTMinute();

		if(scans.length>1){
			
			IMS1Scan ms1Scan00 = ms1Scan;
			int idenRtListId = 1;
			int scanNum00 = scanNum;
			double lastRt = idenRtList[0];
			
			while((ms1Scan00=this.scanlist.getNextScan(scanNum00))!=null){
				if(ms1Scan00.getScanNum()>scans[idenRtListId]){
					idenRtList[idenRtListId] = lastRt;
					idenRtListId++;
				}
				if(idenRtListId==idenRtList.length){
					break;
				}
				lastRt = ms1Scan00.getRTMinute();
				scanNum00++;
			}
			
			Integer [] subscans = this.validateScans(scans, scores, idenRtList);
			
			if(subscans.length<scans.length){
				int begid = -1;
				for(int i=0;i<scans.length;i++){
					if(subscans[0]==scans[i]){
						begid = i;
						break;
					}
				}
				
				double [] subRtList = new double [subscans.length];
				System.arraycopy(idenRtList, begid, subRtList, 0, subRtList.length);
				
				scans = subscans;
				idenRtList = subRtList;
				scanNum = scans[0];
				while(true){
					if((ms1Scan=this.scanlist.getScan(scanNum))!=null){
						break;
					}else{
						scanNum--;
					}
				}
			}
		}

		
//		if(idenRtList[idenRtList.length-1]-idenRtList[0]>3) {
//			return feas;
//		}

		LabelFeatures feas = new LabelFeatures(monoMasses, charge, idenRtList);
		
		boolean [] leftMisses = new boolean [monoMasses.length];
		Arrays.fill(leftMisses, false);
		int [] leftMissNum = new int [monoMasses.length];
		Arrays.fill(leftMissNum, 0);
		boolean [] rightMisses = new boolean [monoMasses.length];
		Arrays.fill(rightMisses, false);
		int [] rightMissNum = new int [monoMasses.length];
		Arrays.fill(rightMissNum, 0);

		LabelFeature fea = this.getFeature(charge, monoMasses, ms1Scan, rightMissNum, rightMisses);

		if(!fea.isMiss())
			feas.addFeature(fea);
		
		IMS1Scan prev;
		int prevscan = scanNum;
		while((prev=scanlist.getPreviousScan(prevscan))!=null){
			prevscan = prev.getScanNum();
			LabelFeature prevfea = this.getFeature(charge, monoMasses, prev, leftMissNum, leftMisses);

			if(prevfea.isMiss()) break;
			else feas.addFeature(prevfea);

			if(idenRtList[0]-prev.getRTMinute()>rtHalfWidth) break;
		}

L:		for(int sn=0;sn<scans.length;sn++){
			
			if(scanNum>scans[sn])
				continue;

			if(sn==0){
				
				IMS1Scan next;
				int nextscan = scanNum;

				while((next=scanlist.getNextScan(nextscan))!=null){
					
					nextscan = next.getScanNum();
					LabelFeature nextfea = this.getFeature(charge, monoMasses, next, rightMissNum, rightMisses);

					if(nextfea.isMiss()) break;
					else feas.addFeature(nextfea);
										
					if(next.getRTMinute()-idenRtList[idenRtList.length-1]>rtHalfWidth) break L;
				}
				
				scanNum = nextscan;
				
			}else{
				
				IMS1Scan nexms1Scan = null;
				int reNexscan = scans[sn];
				while(true){
					if((nexms1Scan=this.scanlist.getScan(reNexscan))!=null){
						break;
					}else{
						reNexscan--;
					}
				}

				LabelFeature reNexfea = this.getFeature(charge, monoMasses, nexms1Scan, rightMissNum, rightMisses);

				if(!reNexfea.isMiss())
					feas.addFeature(reNexfea);
				
				IMS1Scan next;
				int nextscan = reNexscan;

				while((next=scanlist.getNextScan(nextscan))!=null){
					nextscan = next.getScanNum();

					LabelFeature nextfea = this.getFeature(charge, monoMasses, next, rightMissNum, rightMisses);

					if(nextfea.isMiss()) break;
					else feas.addFeature(nextfea);
					
					if(next.getRTMinute()-idenRtList[idenRtList.length-1]>rtHalfWidth) break L;
				}
				
				scanNum = nextscan;
			}
		}
		
//		feas.setInfo();
		feas.setInfo2();
		return feas;
	}

	public LabelFeature getFeature(int charge, double [] monoMasses, IMS1Scan ms1Scan, int [] missNum, boolean [] misses){

		int scanNum = ms1Scan.getScanNum();
		LabelFeature fea = new LabelFeature(scanNum, charge, monoMasses, ms1Scan.getRTMinute());
		
		double tolerance = this.ppm*monoMasses[0];
		tolerance = tolerance<0.02 ? 0.02 : tolerance;
		IPeak [] peaks = ms1Scan.getPeakList().getPeakArray();
		fea.match(peaks, tolerance, missNum, misses);
		
		return fea;
	}
	
	/*public LabelFeature getFeature(int charge, double [] monoMasses, IMS1Scan ms1Scan,
			ArrayList <Double> [] taillist, int [] tailNum, int [] missNum, boolean [] misses){

		int scanNum = ms1Scan.getScanNum();
		LabelFeature fea = new LabelFeature(scanNum, charge, monoMasses, ms1Scan.getRTMinute());
		
		double tolerance = this.ppm*monoMasses[0];
		tolerance = tolerance<0.02 ? 0.02 : tolerance;
		IPeak [] peaks = ms1Scan.getPeakList().getPeakList();
		fea.match(peaks, tolerance, taillist, tailNum, missNum, misses);
		
		return fea;
	}*/
	
	/**
	 * @return
	 */
	public int getLeastIdenNum() {
		// TODO Auto-generated method stub
		return leastINum;
	}
	
	public double getTotalCurrent(){
		return this.ms1TotalCurrent;
	}
	
	/**
	 * 
	 */
	public void close() {
		// TODO Auto-generated method stub
		this.scanlist = null;
		System.gc();
	}
	
	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, XMLStreamException {
		// TODO Auto-generated method stub

		QuanFeatureGetter getter = new QuanFeatureGetter("H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\iden\\" +
				"20130805_serum_di-labeling_Normal_CID_quantification.mzXML");
		double [] monoMasses = new double[]{890.4666, 895.8295};
		Integer [] scans = new Integer[]{45049};
		double [] scores = new double[]{50};
		double [] intenMinusRatio = new double[]{0.2251310169, 0.3154441164, 0.2361374573, 0.1242300104};
		LabelFeatures features = getter.getFeatures(3, monoMasses, scans, scores, intenMinusRatio);
		System.out.println(features.isUse()+"\t"+Arrays.toString(features.getRatios()));
	}


}
