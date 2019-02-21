/* 
 ******************************************************************************
 * File: MzXMLProfileReader.java * * * Created on 2013-4-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.MS1PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.MSXMLSequentialParser;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.ScanHeader;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2013-4-16, 20:20:18
 */
public class MzXMLProfileReader {
	
	private File file;
	private MSXMLSequentialParser msParser;
	private int maxScan;
	private int preScanNum;
	
	private final static double [] dm2 = new double[]{0.002, 0.003, 0.004, 0.0045, 0.005, 0.005, 0.006};
	private static final String lineSeparator = IOConstant.lineSeparator;
	private static final DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public MzXMLProfileReader(String fileName) throws IOException, XMLStreamException{
		
		file = new File(fileName);
		msParser = new MSXMLSequentialParser();
		msParser.open(fileName);
		maxScan = msParser.getMaxScanNumber();
	}
	
	public ISpectrum getNextSpectrum() {
		if(msParser.hasNextScan()){
			
			Scan scan = null;
			
			try {
				scan = msParser.getNextScan();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ScanHeader header = scan.header;
			
			int scanNum = header.getNum();
			int msLevel = header.getMsLevel();
			double rt = header.getDoubleRetentionTime()/60.0d;
			float totIonCurrent = header.getTotIonCurrent();
			float basepeakInten = header.getBasePeakIntensity();
			Description des;
			
			IPeakList peaklist;
			
			if(msLevel==1){
				
				this.preScanNum = scanNum;
				des = new Description(scanNum, msLevel, rt, totIonCurrent);
				
				peaklist = new MS1PeakList();
				double [][] mzIntenList = scan.getMassIntensityList();
				
				for(int i=0;i<mzIntenList[0].length;i++){
					double mz = mzIntenList[0][i];
					double inten = mzIntenList[1][i];
					IPeak ip = new Peak(mz, inten);
					peaklist.add(ip);
				}

				MS1Scan ms1scan = new MS1Scan(des, peaklist);
				return ms1scan;
				
			}else{

				int precursornum = msLevel-1;
				int precursorScanNum = this.preScanNum;
				double preMz = header.getPrecursorMz();
				int charge = header.getPrecursorCharge();
				double precursorInten = header.getPrecursorIntensity();
				des = new Description(scanNum, msLevel, rt, precursornum, precursorScanNum, preMz, charge, precursorInten);
			
				PrecursePeak ppeak = new PrecursePeak(precursorScanNum, preMz, precursorInten);
				ppeak.setCharge((short)charge);
				ppeak.setRT(rt);
				
				peaklist = new MS2PeakList();
				((MS2PeakList) peaklist).setPrecursePeak(ppeak);
				
				double [][] mzIntenList = scan.getMassIntensityList();
				for(int i=0;i<mzIntenList[0].length;i++){
					double mz = Double.parseDouble(df4.format(mzIntenList[0][i]));
					double inten = Double.parseDouble(df4.format(mzIntenList[1][i]));
					IPeak ip = new Peak(mz, inten);
					peaklist.add(ip);
				}
				
				MS2Scan ms2scan = new MS2Scan(des, (MS2PeakList) peaklist);
				return ms2scan;
			}
		}
		
		return null;
	}
		
	public void toMgfDirect2(String output) throws IOException{
		
		PrintWriter writer = new PrintWriter(output);
		double [][] ms1MzIntenList = null;
		double ms1LeastIntensity = 0;
		
		while(msParser.hasNextScan()){
			
			Scan scan = null;
			
			try {
				scan = msParser.getNextScan();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ScanHeader header = scan.header;
			
			int scanNum = header.getNum();
			int msLevel = header.getMsLevel();
			double rt = header.getDoubleRetentionTime()/60.0d;
			float totIonCurrent = header.getTotIonCurrent();
			float basepeakInten = header.getBasePeakIntensity();

			if(msLevel==1){

				ms1MzIntenList = scan.getMassIntensityList();
				ms1LeastIntensity = 100000;
				int up = ms1MzIntenList[1].length>50 ? 50 : ms1MzIntenList[1].length;
				for(int i=0;i<up;i++){
					double inteni = ms1MzIntenList[1][i];
					if(inteni>0 && inteni<ms1LeastIntensity){
						ms1LeastIntensity = (int) inteni;
					}
				}
				ms1LeastIntensity = ms1LeastIntensity<25? ms1LeastIntensity*2 : ms1LeastIntensity*1.5;

			}else{
if(scanNum==702){
				int precursorScanNum = this.preScanNum;
				double precursorMz = header.getPrecursorMz();
				double precursorIntensity = header.getPrecursorIntensity();

				double [][] mzIntenList = scan.getMassIntensityList();
				double leastIntensity = 10000;
				int up = mzIntenList[1].length>50 ? 50 : mzIntenList[1].length;
				for(int i=0;i<up;i++){
					double inteni = mzIntenList[1][i];
					if(inteni>0 && inteni<leastIntensity){
						leastIntensity = inteni;
					}
				}
				leastIntensity = leastIntensity*1.5;

				boolean begin = false;
				ArrayList <Double> mzList = null;
				ArrayList <Double> intensityList = null;
				
				int misscount = 0;
				ArrayList <IPeak> highIntenList = new ArrayList <IPeak>();
				
				for(int i=2;i<mzIntenList[0].length-2;i++){

					double mzi = mzIntenList[0][i];
					double inteni = (mzIntenList[1][i-2]+mzIntenList[1][i-1]+mzIntenList[1][i]
							+mzIntenList[1][i+1]+mzIntenList[1][i+2])/5.0;
					
					int dmid = mzi/200.0>6 ? 6 : (int)mzi/200;
					if(begin){

						if(mzi-mzIntenList[0][i-1]<=dm2[dmid]){
							if(inteni<leastIntensity){
								misscount++;
								if(misscount==2){
									if(mzList.size()>=4){
										this.addPeak(mzList, intensityList, highIntenList);
									}
									begin = false;
								}else{
									mzList.add(mzi);
									intensityList.add(inteni);
								}
							}else{
								misscount = 0;
								mzList.add(mzi);
								intensityList.add(inteni);
							}
						}else{
							if(mzList.size()>=4){
								this.addPeak(mzList, intensityList, highIntenList);
							}
							if(inteni>=leastIntensity){
								begin = true;
								misscount = 0;
								mzList = new ArrayList <Double>();
								intensityList = new ArrayList <Double>();
								mzList.add(mzi);
								intensityList.add(inteni);
							}else{
								begin = false;
							}
						}
					}else{
						if(inteni>=leastIntensity){
							begin = true;
							misscount = 0;
							mzList = new ArrayList <Double>();
							intensityList = new ArrayList <Double>();
							mzList.add(mzi);
							intensityList.add(inteni);
						}
					}
				}
				
				int precursorId = Arrays.binarySearch(ms1MzIntenList[0], precursorMz-1.0);
				if(precursorId<0) precursorId = -precursorId-1;
				boolean preBegin = false;
				int preMissCount = 0;
				int preAboveCount = 0;
				ArrayList <Double> preMzlist = new ArrayList <Double>();
				ArrayList <Double> preIntenlist = new ArrayList <Double>();
				
				ArrayList <IPeak> preIsotope = new ArrayList <IPeak>();
				
				for(int i=precursorId+2;i<ms1MzIntenList[0].length-2;i++){
					
					double mzi = ms1MzIntenList[0][i];
					double inteni = (ms1MzIntenList[1][i-2]+ms1MzIntenList[1][i-1]+ms1MzIntenList[1][i]
							+ms1MzIntenList[1][i+1]+ms1MzIntenList[1][i+2])/5.0;
					
					int dmid = mzi/200.0>6 ? 6 : (int)mzi/200;
					if(mzi-ms1MzIntenList[0][precursorId]>3.0)
						break;
					
					if(preBegin){

						if(mzi-ms1MzIntenList[0][i-1]<=dm2[dmid]){
							if(inteni<ms1LeastIntensity){
								preMissCount++;
								if(preMissCount==2){
									if(preMzlist.size()>=4 && preAboveCount>=2){
										this.addPeak(preMzlist, preIntenlist, preIsotope);
									}
									preBegin = false;
								}else{
									preMzlist.add(mzi);
									preIntenlist.add(inteni);
								}
							}else{
								preMissCount = 0;
								preMzlist.add(mzi);
								preIntenlist.add(inteni);
								if(inteni>=ms1LeastIntensity){
									preAboveCount++;
								}
							}
						}else{
							if(preMzlist.size()>=4 && preAboveCount>=2){
								this.addPeak(preMzlist, preIntenlist, preIsotope);
							}
							if(inteni>=ms1LeastIntensity){
								preBegin = true;
								preMissCount = 0;
								preAboveCount = 0;
								preMzlist = new ArrayList <Double>();
								preIntenlist = new ArrayList <Double>();
								preMzlist.add(mzi);
								preIntenlist.add(inteni);
							}else{
								preBegin = false;
							}
						}
					}else{
						if(inteni>=ms1LeastIntensity){
							preBegin = true;
							preMissCount = 0;
							preAboveCount = 0;
							preMzlist = new ArrayList <Double>();
							preIntenlist = new ArrayList <Double>();
							preMzlist.add(mzi);
							preIntenlist.add(inteni);
						}
					}
				}
				
				double massdiffscore = 0;
				int preId = -1;
				double monomass = -1;
				double monointen = -1;
				double [] monorange = null;
				for(int i=0;i<preIsotope.size();i++){
					IPeak pi = preIsotope.get(i);
					int diffi = (int) (Math.abs(precursorMz-pi.getMz())*100);
					if(diffi>50){
						continue;
					}else if(diffi<1){
						diffi = 1;
					}

					if(pi.getIntensity()/(double)diffi>massdiffscore){
						massdiffscore = pi.getIntensity()/(double)diffi;
						preId = i;
						monomass = pi.getMz();
						monointen = pi.getIntensity();
						monorange = pi.getMassRange();
					}
//System.out.println("343\t"+pi.getMz()+"\t"+pi.getIntensity()+"\t"+massdiffscore);						
					
				}
				if(monorange==null){
					monomass = precursorMz;
					monointen = precursorIntensity;
					monorange = new double [] {precursorMz-0.04, precursorMz+0.04};
				}
				
				double [] preIsoScore = new double [5];
				double [] preIsoInten = new double [5];
				int [] isocount1 = new int [5];
				double [] isointen1 = new double [5];
				boolean [] down1 = new boolean [5];
				Arrays.fill(isocount1, 1);
				Arrays.fill(isointen1, monointen);
				for(int i=preId+1;i<preIsotope.size();i++){
					IPeak pi = preIsotope.get(i);
					double [] massRange = pi.getMassRange();
					if(monointen/pi.getIntensity()>4 || pi.getIntensity()/monointen>2.5)
						continue;

					if(monorange[0]+0.2*(double)isocount1[4]<massRange[1] &&
							monorange[1]+0.2*(double)isocount1[4]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+0.2*(double)isocount1[4];
						range[1] = monorange[1]+0.2*(double)isocount1[4];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
						
						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6){
							if(down1[4] && pi.getIntensity()/2.0>isointen1[4]){

							}else{
								preIsoScore[4]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity();
								preIsoInten[4]+=pi.getIntensity();
								isocount1[4]++;
								if(pi.getIntensity()<isointen1[4]){
									down1[4] = true;
								}
								isointen1[4] = pi.getIntensity();
							}
						}
					}
					
					if(monorange[0]+0.25*(double)isocount1[3]<massRange[1] &&
							monorange[1]+0.25*(double)isocount1[3]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+0.25*(double)isocount1[3];
						range[1] = monorange[1]+0.25*(double)isocount1[3];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
						
						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6){
							if(down1[3] && pi.getIntensity()/1.5>isointen1[3]){

							}else{
								preIsoScore[3]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity();
								preIsoInten[3]+=pi.getIntensity();
								isocount1[3]++;
								if(pi.getIntensity()<isointen1[3]){
									down1[3] = true;
								}
								isointen1[3] = pi.getIntensity();
							}
						}
					}
					
					if(monorange[0]+0.3333*(double)isocount1[2]<massRange[1] &&
							monorange[1]+0.3333*(double)isocount1[2]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+0.3333*(double)isocount1[2];
						range[1] = monorange[1]+0.3333*(double)isocount1[2];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
						
						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6){
							if(down1[2] && pi.getIntensity()/1.25>isointen1[2]){

							}else{
								preIsoScore[2]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity();
								preIsoInten[2]+=pi.getIntensity();
								isocount1[2]++;
								if(pi.getIntensity()<isointen1[2]){
									down1[2] = true;
								}
								isointen1[2] = pi.getIntensity();
							}
						}
					}
					if(monorange[0]+0.5*(double)isocount1[1]<massRange[1] &&
							monorange[1]+0.5*(double)isocount1[1]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+0.5*(double)isocount1[1];
						range[1] = monorange[1]+0.5*(double)isocount1[1];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
//System.out.println("449\t"+(range[2]-range[1])/(massRange[1]-massRange[0]));

						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6){
							if(down1[1] && pi.getIntensity()>isointen1[1]){

							}else{
								preIsoScore[1]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity();
								preIsoInten[1]+=pi.getIntensity();
								isocount1[1]++;
								if(pi.getIntensity()<isointen1[1]){
									down1[1] = true;
								}
								isointen1[1] = pi.getIntensity();	
							}
						}
						
					}
					
					if(monorange[0]+1.0*(double)isocount1[0]<massRange[1] &&
							monorange[1]+1.0*(double)isocount1[0]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+1.0*(double)isocount1[0];
						range[1] = monorange[1]+1.0*(double)isocount1[0];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
						
						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6){
							if(down1[0] && pi.getIntensity()>isointen1[0]){

							}else{
								preIsoScore[0]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity();
								preIsoInten[0]+=pi.getIntensity();
								isocount1[0]++;
								if(pi.getIntensity()<isointen1[0]){
									down1[0] = true;
								}
								isointen1[0] = pi.getIntensity();
							}
						}
					}
				}
				
				int [] isocount2 = new int [5];
				Arrays.fill(isocount2, 1);

				double [] isomz2 = new double [5];
				double [] isointen2 = new double [5];
				boolean [] down2 = new boolean [5];
				Arrays.fill(isointen2, monointen);
				for(int i=preId-1;i>=0;i--){
					IPeak pi = preIsotope.get(i);
					double [] massRange = pi.getMassRange();
					if(monointen/pi.getIntensity()>4 || pi.getIntensity()/monointen>4 || pi.getIntensity()<400)
						continue;
					
					if(massRange[0]+0.2*(double)isocount2[4]<monorange[1] &&
							massRange[1]+0.2*(double)isocount2[4]>monorange[0]){
						
						double [] range = new double [4];
						range[0] = massRange[0]+0.2*(double)isocount2[4];
						range[1] = massRange[1]+0.2*(double)isocount2[4];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
						
						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6 && pi.getIntensity()>monointen*0.2){
							if(down2[4] && pi.getIntensity()/2.0>isointen2[4]){

							}else{
								preIsoScore[4]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity()*0.7;
								preIsoInten[4]+=pi.getIntensity();
								isocount2[4]++;
								if(pi.getIntensity()<isointen2[4]){
									down2[4] = true;
								}
								isomz2[4] = pi.getMz();
								isointen2[4] = pi.getIntensity();
							}
						}
					}
					
					if(massRange[0]+0.25*(double)isocount2[3]<monorange[1] &&
							massRange[1]+0.25*(double)isocount2[3]>monorange[0]){
						
						double [] range = new double [4];
						range[0] = massRange[0]+0.25*(double)isocount2[3];
						range[1] = massRange[1]+0.25*(double)isocount2[3];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
						
						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6 && pi.getIntensity()>monointen*0.3){
							if(down2[3] && pi.getIntensity()/2.0>isointen2[3]){

							}else{
								preIsoScore[3]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity()*0.7;
								preIsoInten[3]+=pi.getIntensity();
								isocount2[3]++;
								if(pi.getIntensity()<isointen2[3]){
									down2[3] = true;
								}
								isomz2[3] = pi.getMz();
								isointen2[3] = pi.getIntensity();	
							}
						}
					}
					
					if(massRange[0]+0.3333*(double)isocount2[2]<monorange[1] &&
							massRange[1]+0.3333*(double)isocount2[2]>monorange[0]){

						double [] range = new double [4];
						range[0] = massRange[0]+0.3333*(double)isocount2[2];
						range[1] = massRange[1]+0.3333*(double)isocount2[2];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
						
						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6 && pi.getIntensity()>monointen*0.5){
							if(down2[2] && pi.getIntensity()/2.0>isointen2[2]){

							}else{
								preIsoScore[2]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity()*0.7;
								preIsoInten[2]+=pi.getIntensity();
								isocount2[2]++;
								if(pi.getIntensity()<isointen2[2]){
									down2[2] = true;
								}
								isomz2[2] = pi.getMz();
								isointen2[2] = pi.getIntensity();
							}
						}
					}
					
					if(massRange[0]+0.5*(double)isocount2[1]<monorange[1] &&
							massRange[1]+0.5*(double)isocount2[1]>monorange[0]){
						
						double [] range = new double [4];
						range[0] = massRange[0]+0.5*(double)isocount2[1];
						range[1] = massRange[1]+0.5*(double)isocount2[1];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
						
						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6 && pi.getIntensity()>monointen*0.8){
							if(down2[1] && pi.getIntensity()/2.0>isointen2[1]){

							}else{
								preIsoScore[1]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity()*0.7;
								preIsoInten[1]+=pi.getIntensity();
								isocount2[1]++;
								if(pi.getIntensity()<isointen2[1]){
									down2[1] = true;
								}
								isomz2[1] = pi.getMz();
								isointen2[1] = pi.getIntensity();
							}
						}
					}
					
					if(massRange[0]+1.0*(double)isocount2[0]<monorange[1] &&
							massRange[1]+1.0*(double)isocount2[0]>monorange[0]){
						
						double [] range = new double [4];
						range[0] = massRange[0]+1.0*(double)isocount2[0];
						range[1] = massRange[1]+1.0*(double)isocount2[0];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
						
						if((range[2]-range[1])/(massRange[1]-massRange[0])>=0.6 && pi.getIntensity()>monointen*0.8){
							if(down2[0] && pi.getIntensity()/2.0>isointen2[0]){

							}else{
								preIsoScore[0]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity()*0.7;
								preIsoInten[0]+=pi.getIntensity();
								isocount2[0]++;
								if(pi.getIntensity()<isointen2[0]){
									down2[0] = true;
								}
								isomz2[0] = pi.getMz();
								isointen2[0] = pi.getIntensity();
							}
						}
					}
				}

				int charge = 0;
				double maxScore = 0;
				double maxInten = 0;
				for(int i=0;i<preIsoScore.length;i++){
					if(preIsoScore[i]==0)
						continue;
					
					if(preIsoScore[i]>maxScore){
						maxScore = preIsoScore[i];
						maxInten = preIsoInten[i];
						charge = i+1;
					}else if(preIsoScore[i]==maxScore){
						if(charge==1){
							maxScore = preIsoScore[i];
							maxInten = preIsoInten[i];
							charge = i+1;
						}else{
							if(preIsoInten[i]>maxInten){
								maxScore = preIsoScore[i];
								maxInten = preIsoInten[i];
								charge = i+1;
							}
						}
						
					}else{
						if(charge==1){
							maxScore = preIsoScore[i];
							maxInten = preIsoInten[i];
							charge = i+1;
						}
					}
				}

				System.out.println(scanNum+"\t"+monomass+"\t"+precursorMz+"\t"+charge+"\t"+Arrays.toString(preIsoScore)+"\t"+preIsotope.size()+"\t"+
				ms1LeastIntensity+"\t"+Arrays.toString(isocount1)+"\t"+Arrays.toString(isocount2)+"\t"+Arrays.toString(isomz2));
				
				if(charge==0){
					
//					nocharge++;
					
				}else{
					if(isomz2[charge-1]>0){
						monomass = isomz2[charge-1];
					}
/*					for(int i=preId-1;i>=0;i--){
						IPeak pi = preIsotope.get(i);
						if(Math.abs(monomass-pi.getMz()-1.0/(double)charge)<0.01){
							if(charge<=3){
								if(pi.getIntensity()>monointen){
									monomass = pi.getMz();
									monointen = pi.getIntensity();
								}
							}else{
								if(Math.abs(Math.log10(monointen/pi.getIntensity()))<0.5){
									monomass = pi.getMz();
									monointen = pi.getIntensity();
								}
							}
						}
					}
*/					
				}
				
//				System.out.println(monomass+"\t"+precursorMz+"\t"+charge+"\t"+Arrays.toString(preIsoScore)+"\t"+preIsotope.size()+"\t"+
//						ms1LeastIntensity+"\t"+Arrays.toString(isocount1)+"\t"+Arrays.toString(isocount2));
				for(int i=0;i<preIsotope.size();i++){
					IPeak peaki = preIsotope.get(i);
					System.out.println(precursorMz+"\t"+peaki.getMz()+"\t"+peaki.getIntensity()
							+"\t"+peaki.getMassRange()[0]+"\t"+peaki.getMassRange()[1]+"\t"+
							(peaki.getMz()-monomass)+"\t"+(peaki.getMz()-precursorMz));
				}
				System.out.println("~~~~~~~~~~~~~~~");
				for(int i=precursorId+1;i<ms1MzIntenList[0].length;i++){
//				for(int i=0;i<ms1MzIntenList[0].length;i++){
					double mzi = ms1MzIntenList[0][i];
					double inteni = ms1MzIntenList[1][i];
					double inteni2 = (ms1MzIntenList[1][i-2]+ms1MzIntenList[1][i-1]+ms1MzIntenList[1][i]
							+ms1MzIntenList[1][i+1]+ms1MzIntenList[1][i+2])/5.0;
					System.out.println(mzi+"\t"+inteni+"\t"+inteni2);
					if(mzi-precursorMz>3)
						break;
				}
				System.exit(0);

				StringBuilder namesb = new StringBuilder();
				namesb.append("Elution from: ").append(rt).append(" to ").append(rt);
				namesb.append(" period: 0 experiment: 1 cycles: 1 precIntensity: ").append(monointen);
				namesb.append(" FinneganScanNumber: ").append(scanNum);
				namesb.append(" MStype: enumIsNormalMS rawFile: ").append(file.getName());

				StringBuilder sb = new StringBuilder();
				sb.append("BEGIN IONS"+lineSeparator);
				sb.append("PEPMASS="+monomass+lineSeparator);
				if(charge>1)
					sb.append("CHARGE="+charge+"+"+lineSeparator);
				sb.append("TITLE="+namesb+lineSeparator);
				
				for(int i=0;i<highIntenList.size();i++){
					IPeak peaki = highIntenList.get(i);
					sb.append(peaki.getMz()+"\t"+df4.format(peaki.getIntensity())+lineSeparator);
				}
				
				sb.append("END IONS"+lineSeparator);
				writer.write(sb.toString());
}
			}
		}
		
		writer.close();
	}
	
	private void addPeak(ArrayList <Double> mzlist, ArrayList <Double> intenlist, ArrayList <IPeak> peaklist){

//System.out.println("677\t"+mzlist);
		
		double totalDistance = mzlist.get(mzlist.size()-1)-mzlist.get(0);
		if(totalDistance<=0.06){
			double mz = MathTool.getWeightAveInDouble(intenlist, mzlist);
			double intensity = MathTool.getTotalInDouble(intenlist);
			IPeak peak = new Peak(mz, intensity);
			peak.setMassRange(new double []{mzlist.get(0), mzlist.get(mzlist.size()-1)});
			peaklist.add(peak);
			
			return;
		}
		
		ArrayList <Integer> maxlist = new ArrayList <Integer>();
		ArrayList <Integer> minlist = new ArrayList <Integer>();
		double maxinten = 0;
		int currentMaxMin = -1;
		for(int i=3;i<intenlist.size()-3;i++){
			if(intenlist.get(i)>maxinten){
				maxinten = intenlist.get(i);
			}
			int left = -1;
			int leftup = 0;
			int leftdown = 0;
			int right = -1;
			int rightup = 0;
			int rightdown = 0;
			double inteni = intenlist.get(i);
			for(int j=i-1;j>=0;j--){
				double intenj = intenlist.get(j);
				if(intenj>inteni){
					if(leftup>0) break;
					leftdown++;
					if(leftdown==2){
						left = 0;
						break;
					}
				}else if(intenj<inteni){
					if(leftdown>0) break;
					leftup++;
					if(leftup==3){
						left = 1;
						break;
					}
				}else{
					continue;
				}
			}
			for(int j=i+1;j<intenlist.size();j++){
				double intenj = intenlist.get(j);
				if(intenj>inteni){
					if(rightup>0) break;
					rightdown++;
					if(rightdown==2){
						right = 0;
						break;
					}
				}else if(intenj<inteni){
					if(rightdown>0) break;
					rightup++;
					if(rightup==3){
						right = 1;
						break;
					}
				}else{
					continue;
				}
			}
			if(left==0 && right==0){
				if(currentMaxMin==0){
					if(i-minlist.get(minlist.size()-1)>4){
						minlist.add(i);
					}else{
						if(intenlist.get(minlist.get(minlist.size()-1))>intenlist.get(i)){
							minlist.remove(minlist.size()-1);
							minlist.add(i);
						}
					}
				}else if(currentMaxMin==1){
					if(inteni*3.0<intenlist.get(maxlist.get(maxlist.size()-1))){
						minlist.add(i);
						currentMaxMin = 0;
					}
				}else{
					minlist.add(i);
					currentMaxMin = 0;
				}
			}
			if(left==1 && right==1){
				if(currentMaxMin==1){
					if(intenlist.get(maxlist.get(maxlist.size()-1))<intenlist.get(i)){
						maxlist.remove(maxlist.size()-1);
						maxlist.add(i);
					}
				}else if(currentMaxMin==0){
					if(inteni/3.0>intenlist.get(minlist.get(minlist.size()-1))){
						maxlist.add(i);
						currentMaxMin = 1;
					}
				}else{
					maxlist.add(i);
					currentMaxMin = 1;
				}
			}
		}

		Iterator <Integer> it = maxlist.iterator();
		while(it.hasNext()){
			double intenit = intenlist.get(it.next());
			if(intenit*3.0<maxinten){
				it.remove();
			}
		}
//System.out.println("762\t"+maxlist+"\t"+minlist);
		if(maxlist.size()==0){
			
			double mz = MathTool.getWeightAveInDouble(intenlist, mzlist);
			double intensity = MathTool.getTotalInDouble(intenlist);
			IPeak peak = new Peak(mz, intensity);
			peak.setMassRange(new double []{mzlist.get(0), mzlist.get(mzlist.size()-1)});
			peaklist.add(peak);

		}else if(maxlist.size()==1){
			
			if(minlist.size()==0){
				
				int half = maxlist.get(0)<(mzlist.size()-1-maxlist.get(0)) ? maxlist.get(0) : mzlist.size()-1-maxlist.get(0);
				ArrayList <Double> newmzlist = new ArrayList <Double>();
				ArrayList <Double> newintenlist = new ArrayList <Double>();

				if(maxlist.get(0)<mzlist.size()-1-maxlist.get(0)){
					for(int i=0;i<=2*half;i++){
						newmzlist.add(mzlist.get(i));
						newintenlist.add(intenlist.get(i));
					}
				}else{
					for(int i=0;i<=2*half;i++){
						newmzlist.add(mzlist.get(i+maxlist.get(0)-half));
						newintenlist.add(intenlist.get(i+maxlist.get(0)-half));
					}
				}
				
				double mz = MathTool.getWeightAveInDouble(newintenlist, newmzlist);
				double intensity = MathTool.getTotalInDouble(intenlist);
				IPeak peak = new Peak(mz, intensity);
				peak.setMassRange(new double []{newmzlist.get(0), newmzlist.get(newmzlist.size()-1)});
				peaklist.add(peak);
				
			}else{
				
				int left = 0;
				int right = intenlist.size()-1;
				
				for(int i=0;i<minlist.size();i++){
					if(minlist.get(i)>left && minlist.get(i)<maxlist.get(0)-3){
						left = minlist.get(i);
					}
					if(minlist.get(i)<right && minlist.get(i)>maxlist.get(0)+3){
						right = minlist.get(i);
					}
				}
				
				int half = (maxlist.get(0)-left<right-maxlist.get(0)) ? maxlist.get(0)-left : right-maxlist.get(0);
				ArrayList <Double> newmzlist = new ArrayList <Double>();
				ArrayList <Double> newintenlist = new ArrayList <Double>();

				if(maxlist.get(0)-left<right-maxlist.get(0)){
					for(int i=0;i<=2*half;i++){
						newmzlist.add(mzlist.get(i+left));
						newintenlist.add(intenlist.get(i+left));
					}
				}else{
					for(int i=0;i<=2*half;i++){
						newmzlist.add(mzlist.get(i+maxlist.get(0)-half));
						newintenlist.add(intenlist.get(i+maxlist.get(0)-half));
					}
				}

				double mz = MathTool.getWeightAveInDouble(newintenlist, newmzlist);
				double intensity = MathTool.getTotalInDouble(newintenlist);
				IPeak peak = new Peak(mz, intensity);
				peak.setMassRange(new double []{newmzlist.get(0), newmzlist.get(newmzlist.size()-1)});
				peaklist.add(peak);

			}
			
		}else{

			double maxPeakInten = 0;
			ArrayList <IPeak> tempPeakList = new ArrayList <IPeak>();
			
			for(int i=0;i<maxlist.size();i++){
				
				int maxi = maxlist.get(i);
				int left = i==0 ? 0 : maxlist.get(i-1);
				int right = i==maxlist.size()-1 ? intenlist.size()-1 : maxlist.get(i+1);
				
				for(int j=0;j<minlist.size();j++){
					if(minlist.get(j)>left && minlist.get(j)<maxi){
						left = minlist.get(j);
					}
					if(minlist.get(j)<right && minlist.get(j)>maxi){
						right = minlist.get(j);
					}
				}

				if(maxi-left<=3 || right-maxi<=3)
					continue;
				
				int half = (maxlist.get(i)-left<right-maxlist.get(i)) ? maxlist.get(i)-left : right-maxlist.get(i);
				ArrayList <Double> newmzlist = new ArrayList <Double>();
				ArrayList <Double> newintenlist = new ArrayList <Double>();

				if(maxlist.get(i)-left<right-maxlist.get(i)){
					for(int i1=0;i1<=2*half;i1++){
						newmzlist.add(mzlist.get(i1+left));
						newintenlist.add(intenlist.get(i1+left));
					}
				}else{
					for(int i1=0;i1<=2*half;i1++){
						newmzlist.add(mzlist.get(i1+maxlist.get(i)-half));
						newintenlist.add(intenlist.get(i1+maxlist.get(i)-half));
					}
				}
				
				double mz = MathTool.getWeightAveInDouble(newintenlist, newmzlist);
				double intensity = MathTool.getTotalInDouble(newintenlist);
				IPeak peak = new Peak(mz, intensity);
				peak.setMassRange(new double []{newmzlist.get(0), newmzlist.get(newmzlist.size()-1)});
				tempPeakList.add(peak);
				if(peak.getIntensity()>maxPeakInten){
					maxPeakInten = peak.getIntensity();
				}
			}
			for(int i=0;i<tempPeakList.size();i++){
				if(tempPeakList.get(i).getIntensity()*3>maxPeakInten){
					peaklist.add(tempPeakList.get(i));
				}
			}
		}
	}
	
	private double [][] getAverageList(double [][] mzlist, int range){
		double [][] averageList = new double [2][mzlist[0].length];
		int half = range/2;
		for(int i=half;i<mzlist[0].length-half;i++){
			averageList[0][i] = mzlist[0][i];
			double intensity = 0;
			for(int j=i-half;j<=i+half;j++){
				intensity += mzlist[1][j];
			}
			averageList[1][i] =intensity/(double)range;
		}
		return averageList;
	}
	
	private MonoPeak [] getCharge(double [][] ms1MzIntenList, double ms1LeastIntensity, double precursorMz){
		
		int precursorId = Arrays.binarySearch(ms1MzIntenList[0], precursorMz-1.0);
		if(precursorId<0) precursorId = -precursorId-1;
		boolean preBegin = false;
		int preMissCount = 0;
		int preAboveCount = 0;
		ArrayList <Double> preMzlist = new ArrayList <Double>();
		ArrayList <Double> preIntenlist = new ArrayList <Double>();
		
		ArrayList <IPeak> preIsotope = new ArrayList <IPeak>();
		
		for(int i=precursorId+2;i<ms1MzIntenList[0].length-2;i++){
			
			double mzi = ms1MzIntenList[0][i];
			double inteni = (ms1MzIntenList[1][i-2]+ms1MzIntenList[1][i-1]+ms1MzIntenList[1][i]
					+ms1MzIntenList[1][i+1]+ms1MzIntenList[1][i+2])/5.0;
//System.out.println(mzi+"\t"+ms1MzIntenList[1][i]+"\t"+inteni);
			int dmid = mzi/200.0>6 ? 6 : (int)mzi/200;
			if(mzi-ms1MzIntenList[0][precursorId]>3.0)
				break;
			
			if(preBegin){

				if(mzi-ms1MzIntenList[0][i-1]<=dm2[dmid]){
					if(inteni<ms1LeastIntensity){
						preMissCount++;
						if(preMissCount==2){
							if(preMzlist.size()>=4 && preAboveCount>=2){
								this.addPeak(preMzlist, preIntenlist, preIsotope);
							}
							preBegin = false;
						}else{
							preMzlist.add(mzi);
							preIntenlist.add(inteni);
						}
					}else{
						preMissCount = 0;
						preMzlist.add(mzi);
						preIntenlist.add(inteni);
						if(inteni>=ms1LeastIntensity){
							preAboveCount++;
						}
					}
				}else{
					preBegin = false;
					if(preMzlist.size()>=4 && preAboveCount>=2){
						this.addPeak(preMzlist, preIntenlist, preIsotope);
					}
					if(inteni>=ms1LeastIntensity){
						preBegin = true;
						preMissCount = 0;
						preAboveCount = 0;
						preMzlist = new ArrayList <Double>();
						preIntenlist = new ArrayList <Double>();
						preMzlist.add(mzi);
						preIntenlist.add(inteni);
					}
				}
			}else{
				if(inteni>=ms1LeastIntensity){
					preBegin = true;
					preMissCount = 0;
					preAboveCount = 0;
					preMzlist = new ArrayList <Double>();
					preIntenlist = new ArrayList <Double>();
					preMzlist.add(mzi);
					preIntenlist.add(inteni);
				}
			}
		}
		
		ArrayList <MonoPeak> [] monopeaks = new ArrayList [3];
		for(int i=0;i<monopeaks.length;i++){
			monopeaks[i] = new ArrayList <MonoPeak>();
		}
		ArrayList <Double> difflist = new ArrayList <Double>();
		double maxinten = 0;
		
		for(int chaId=1;chaId<=5;chaId++){
			double distance = 1.0/(double)chaId;
			HashSet <Integer> usedset = new HashSet <Integer>();
			for(int i=0;i<preIsotope.size();i++){

				IPeak pi = preIsotope.get(i);
				
				double mzi = pi.getMz();
				double inteni = pi.getIntensity();
				double [] rangei = pi.getMassRange();
				double densityi = inteni/(rangei[1]-rangei[0]);
				
System.out.println("1104\t"+chaId+"\t"+mzi+"\t"+rangei[0]+"\t"+rangei[1]);
				
				if(usedset.contains(i)) continue;
				
System.out.println("1108\t"+chaId+"\t"+mzi+"\t"+rangei[0]+"\t"+rangei[1]);

				double mass = mzi*chaId;
				double diff = Math.abs(mzi-precursorMz);
				double totalInten = inteni;
				int id = 1;
				ArrayList <Double> isotopeMz = new ArrayList <Double>();
				ArrayList <Double> isotopeIntensity = new ArrayList <Double>();
				isotopeMz.add(mzi);
				isotopeIntensity.add(inteni);
				
				for(int j=i+1;j<preIsotope.size();j++){
					IPeak pj = preIsotope.get(j);
					double mzj = pj.getMz();
					double intenj = pj.getIntensity();
					double [] rangej = pj.getMassRange();
					double densityj = intenj/(rangej[1]-rangej[0]);
					
System.out.println("1123\t"+mass+"\t"+mzi+"\t"+(rangei[0]+id*distance)+
		"\t"+(rangei[1]+id*distance)+"\t"+inteni+"\t"+mzj+"\t"+rangej[0]+"\t"+rangej[1]+"\t"+intenj);						
					
					if(rangej[0]<rangei[1]+id*distance && rangej[1]>rangei[0]+id*distance){
						double [] ranges = new double [4];
						ranges[0] = rangei[0]+id*distance;
						ranges[1] = rangei[1]+id*distance;
						ranges[2] = rangej[0];
						ranges[3] = rangej[1];
						Arrays.sort(ranges);

						if(ranges[2]-ranges[1]>0.03 || ranges[2]-ranges[1]>(rangej[1]-rangej[0])*0.5){
							boolean pass = false;
							if(mass<1000){
								if(densityi>densityj && densityj*8.0>densityi){
									pass = true;
								}
							}else if(mass>=1000 && mass<1500){
								if(densityi>densityj && densityj*5.0>densityi){
									pass = true;
								}
							}else if(mass>=1500 && mass<2000){
								if(densityi*1.2>densityj && densityj*3.0>densityi){
									pass = true;
								}
							}else if(mass>=2000 && mass<2500){
								if(densityi*1.5>densityj && densityj*1.5>densityi){
									pass = true;
								}
							}else{
								if(densityi<densityj && densityj<densityi*4.0){
									pass = true;
								}
							}
							if(pass){
								usedset.add(j);
								id++;
								totalInten += intenj;
								if(Math.abs(mzj-precursorMz)<diff){
									diff = Math.abs(mzj-precursorMz);
								}
								isotopeMz.add(mzj);
								isotopeIntensity.add(intenj);
							}
						}
					}else if(rangej[0]>=rangei[1]+id*distance){
						break;
					}
				}
				
				if(id>1){
					MonoPeak peak = new MonoPeak(mzi, totalInten);
					peak.setChareg(chaId);
					if(Math.abs(mzi-precursorMz)<0.01){
						monopeaks[0].add(peak);
					}else{
						if(Math.abs(diff)<0.01){
							monopeaks[1].add(peak);
						}else{
							monopeaks[2].add(peak);
						}
					}
					
					difflist.add(diff);
					if(totalInten>maxinten){
						maxinten = totalInten;
					}
					
System.out.println("1146\t"+mzi+"\t"+chaId+"\t"+isotopeMz+"\t"+isotopeIntensity);
				}
			}
		}
		
		return null;
		
/*		if(monopeaks.size()==0){
			return new MonoPeak []{new MonoPeak(precursorMz, ms1LeastIntensity)};
		}
		
		MonoPeak [] resultpeaks = new MonoPeak[2];
		for(int i=0;i<monopeaks.size();i++){
			MonoPeak peak = monopeaks.get(i);
System.out.println("1144\t"+peak.getMz()+"\t"+precursorMz+"\t"+peak.getIntensity()+"\t"+peak.getCharge()+"\t"+difflist.get(i)+"\t"+precursorMz);
			if(peak.getIntensity()*5>maxinten && difflist.get(i)<0.8 && Math.abs(peak.getMz()-precursorMz)<0.5){
				if(resultpeaks[0]==null){
					resultpeaks[0] = peak;
				}else if(resultpeaks[1]==null){
					if(peak.getIntensity()>resultpeaks[0].getIntensity()){
						if(resultpeaks[0].getCharge()==1){
							resultpeaks[1] = resultpeaks[0];
							resultpeaks[0] = peak;
						}else{
							if(peak.getCharge()==1){
								resultpeaks[1] = peak;
							}else{
								resultpeaks[1] = resultpeaks[0];
								resultpeaks[0] = peak;
							}
						}
					}else{
						if(resultpeaks[0].getCharge()==1){
							if(peak.getCharge()==1){
								resultpeaks[1] = peak;
							}else{
								resultpeaks[1] = resultpeaks[0];
								resultpeaks[0] = peak;
							}
						}
					}
				}else{
					if(peak.getIntensity()>resultpeaks[0].getIntensity()){
						if(resultpeaks[0].getCharge()==1){
							resultpeaks[1] = resultpeaks[0];
							resultpeaks[0] = peak;
						}else{
							if(peak.getCharge()==1){
								if(resultpeaks[1].getCharge()==1){
									resultpeaks[1] = peak;
								}
							}else{
								resultpeaks[1] = resultpeaks[0];
								resultpeaks[0] = peak;
							}
						}
					}else{
						if(peak.getIntensity()>resultpeaks[1].getIntensity()){
							if(resultpeaks[1].getCharge()==1){
								resultpeaks[1] = peak;
							}else{
								if(peak.getCharge()>1){
									resultpeaks[1] = peak;
								}
							}
						}else{
							if(peak.getCharge()>1){
								if(resultpeaks[0].getCharge()==1){
									resultpeaks[1] = resultpeaks[0];
									resultpeaks[0] = peak;
								}else{
									if(resultpeaks[1].getCharge()==1){
										resultpeaks[1] = peak;
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(resultpeaks[0]!=null){
			if(resultpeaks[1]!=null){
				if(resultpeaks[1].getCharge()>1){
					System.out.println("result\t"+resultpeaks[0].getMz()+"\t"+resultpeaks[0].getCharge()+"\t"+
							resultpeaks[1].getMz()+"\t"+resultpeaks[1].getCharge());
					return resultpeaks;
				}else{
					System.out.println("result\t"+resultpeaks[0].getMz()+"\t"+resultpeaks[0].getCharge());
					return new MonoPeak []{resultpeaks[0]};
				}
			}else{
				System.out.println("result\t"+resultpeaks[0].getMz()+"\t"+resultpeaks[0].getCharge());
				return new MonoPeak []{resultpeaks[0]};
			}
		}else{
			return new MonoPeak []{new MonoPeak(precursorMz, ms1LeastIntensity)};
		}
*/		
		
/*		MonoPeak [] peaks = monopeaks.toArray(new MonoPeak[monopeaks.size()]);
		Arrays.sort(peaks, new Comparator<MonoPeak>(){
			@Override
			public int compare(MonoPeak peak0, MonoPeak peak1) {
				// TODO Auto-generated method stub
				if(peak0.getIntensity()<peak1.getIntensity()){
					return 1;
				}else if(peak0.getIntensity()>peak1.getIntensity()){
					return -1;
				}
				return 0;
			}
			
		});
*/		
		
	}
	
	public void toMgf(String output) throws IOException{
		
		PrintWriter writer = new PrintWriter(output);
		double [][] ms1MzIntenList = null;
		double ms1LeastIntensity = 0;
		
		while(msParser.hasNextScan()){
			
			Scan scan = null;
			
			try {
				scan = msParser.getNextScan();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ScanHeader header = scan.header;
			
			int scanNum = header.getNum();
			int msLevel = header.getMsLevel();
			double rt = header.getDoubleRetentionTime()/60.0d;
			float totIonCurrent = header.getTotIonCurrent();
			float basepeakInten = header.getBasePeakIntensity();

			if(msLevel==1){

				ms1MzIntenList = scan.getMassIntensityList();
				ms1LeastIntensity = 100000;
				int up = ms1MzIntenList[1].length>50 ? 50 : ms1MzIntenList[1].length;
				for(int i=0;i<up;i++){
					double inteni = ms1MzIntenList[1][i];
					if(inteni>0 && inteni<ms1LeastIntensity){
						ms1LeastIntensity = (int) inteni;
					}
				}
//				ms1MzIntenList = this.getAverageList(ms1MzIntenList, 5);
				ms1LeastIntensity = ms1LeastIntensity*2.0;

			}else{
if(scanNum==5485){
				int precursorScanNum = this.preScanNum;
				double precursorMz = header.getPrecursorMz();
				double precursorIntensity = header.getPrecursorIntensity();

				double [][] mzIntenList = scan.getMassIntensityList();
				int leastIntensity = 0;
/*				int up = mzIntenList[1].length>50 ? 50 : mzIntenList[1].length;
				for(int i=0;i<up;i++){
					double inteni = mzIntenList[1][i];
					if(inteni>0 && inteni<leastIntensity){
						leastIntensity = (int) inteni;
					}
				}
*/
				boolean begin = false;
				ArrayList <Double> mzList = null;
				ArrayList <Double> intensityList = null;
				
				int misscount = 0;
				ArrayList <IPeak> highIntenList = new ArrayList <IPeak>();
				
				for(int i=1;i<mzIntenList[0].length;i++){

					double mzi = mzIntenList[0][i];
					double inteni = mzIntenList[1][i];
					
					int dmid = mzi/200.0>6 ? 6 : (int)mzi/200;
					if(begin){

						if(mzi-mzIntenList[0][i-1]<=dm2[dmid]){
//							if(mzi-mzList.get(0)<0.12){
								if(inteni<=leastIntensity){
									misscount++;
									if(misscount==2){
										if(mzList.size()>=4){
											this.addPeak(mzList, intensityList, highIntenList);
										}
										begin = false;
									}else{
										mzList.add(mzi);
										intensityList.add(inteni);
									}
								}else{
									misscount = 0;
									mzList.add(mzi);
									intensityList.add(inteni);
								}
								
/*							}else{
								if(mzList.size()>=4){
									this.addPeak(mzList, intensityList, highIntenList);
								}
								begin = false;
							}
*/							
							
						}else{
							if(mzList.size()>=4){
								this.addPeak(mzList, intensityList, highIntenList);
							}
							if(inteni>leastIntensity){
								begin = true;
								misscount = 0;
								mzList = new ArrayList <Double>();
								intensityList = new ArrayList <Double>();
								mzList.add(mzi);
								intensityList.add(inteni);
							}else{
								begin = false;
							}
						}
					}else{
						if(inteni>leastIntensity){
							begin = true;
							misscount = 0;
							mzList = new ArrayList <Double>();
							intensityList = new ArrayList <Double>();
							mzList.add(mzi);
							intensityList.add(inteni);
						}
					}
				}
				
				int precursorId = Arrays.binarySearch(ms1MzIntenList[0], precursorMz-1.0);
				if(precursorId<0) precursorId = -precursorId-1;
				boolean preBegin = false;
				int preMissCount = 0;
				int preAboveCount = 0;
				ArrayList <Double> preMzlist = new ArrayList <Double>();
				ArrayList <Double> preIntenlist = new ArrayList <Double>();
				
				ArrayList <IPeak> preIsotope = new ArrayList <IPeak>();
				
				for(int i=precursorId+1;i<ms1MzIntenList[0].length-2;i++){
					
					double mzi = ms1MzIntenList[0][i];
					double inteni = (ms1MzIntenList[1][i-2]+ms1MzIntenList[1][i-1]+ms1MzIntenList[1][i]
							+ms1MzIntenList[1][i+1]+ms1MzIntenList[1][i+2])/5.0;
					
					int dmid = mzi/200.0>6 ? 6 : (int)mzi/200;
					if(mzi-ms1MzIntenList[0][precursorId]>3.0)
						break;
					
					if(preBegin){

						if(mzi-ms1MzIntenList[0][i-1]<=dm2[dmid]){
//							if(mzi-preMzlist.get(0)<0.12){
								if(inteni<=ms1LeastIntensity){
									preMissCount++;
									if(preMissCount==2){
										if(preMzlist.size()>=4 && preAboveCount>=2){
											this.addPeak(preMzlist, preIntenlist, preIsotope);
										}
										preBegin = false;
									}else{
										preMzlist.add(mzi);
										preIntenlist.add(inteni);
									}
								}else{
									preMissCount = 0;
									preMzlist.add(mzi);
									preIntenlist.add(inteni);
									if(inteni>=ms1LeastIntensity){
										preAboveCount++;
									}
								}
/*							}else{
								if(preMzlist.size()>=4){
									this.addPeak(preMzlist, preIntenlist, preIsotope);
								}
								preBegin = false;
							}
*/							
						}else{
							if(preMzlist.size()>=4 && preAboveCount>=2){
								this.addPeak(preMzlist, preIntenlist, preIsotope);
							}
							if(inteni>ms1LeastIntensity){
								preBegin = true;
								preMissCount = 0;
								preAboveCount = 0;
								preMzlist = new ArrayList <Double>();
								preIntenlist = new ArrayList <Double>();
								preMzlist.add(mzi);
								preIntenlist.add(inteni);
							}else{
								preBegin = false;
							}
						}
					}else{
						if(inteni>ms1LeastIntensity){
							preBegin = true;
							preMissCount = 0;
							preAboveCount = 0;
							preMzlist = new ArrayList <Double>();
							preIntenlist = new ArrayList <Double>();
							preMzlist.add(mzi);
							preIntenlist.add(inteni);
						}
					}
				}
				
				double massdiffscore = 0;
				int preId = -1;
				double monomass = -1;
				double monointen = -1;
				double [] monorange = null;
				for(int i=0;i<preIsotope.size();i++){
					IPeak pi = preIsotope.get(i);
//					double diffi = Math.abs(precursorMz-pi.getMz());
//					System.out.println();
					if(pi.getIntensity()/Math.abs(pi.getMz()-precursorMz)>massdiffscore){
						massdiffscore = pi.getIntensity()/Math.abs(pi.getMz()-precursorMz);
						preId = i;
						monomass = pi.getMz();
						monointen = pi.getIntensity();
						monorange = pi.getMassRange();
					}
				}
				if(monorange==null){
					monomass = precursorMz;
					monointen = precursorIntensity;
					monorange = new double [] {precursorMz-0.04, precursorMz+0.04};
				}
				
				double [] preIsoScore = new double [5];
				double [] preIsoInten = new double [5];
				int [] isocount1 = new int [5];
				double [] isointen1 = new double [5];
				boolean [] down1 = new boolean [5];
				Arrays.fill(isocount1, 1);
				Arrays.fill(isointen1, monointen);
				for(int i=preId+1;i<preIsotope.size();i++){
					IPeak pi = preIsotope.get(i);
					double [] massRange = pi.getMassRange();
					if(monointen/pi.getIntensity()>10 || pi.getIntensity()/monointen>2.5)
						continue;

					if(monorange[0]+0.2*(double)isocount1[4]<massRange[1] &&
							monorange[1]+0.2*(double)isocount1[4]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+0.2*(double)isocount1[4];
						range[1] = monorange[1]+0.2*(double)isocount1[4];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
						
						if(down1[4]){
							if(pi.getIntensity()<isointen1[4]){
								preIsoScore[4]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
								preIsoInten[4]+=pi.getIntensity();
								isocount1[4]++;
								isointen1[4] = pi.getIntensity();
							}
						}else{
							preIsoScore[4]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
							preIsoInten[4]+=pi.getIntensity();
							isocount1[4]++;
							if(pi.getIntensity()<isointen1[4]){
								down1[4] = true;
							}
							isointen1[4] = pi.getIntensity();
						}
					}
					
					if(monorange[0]+0.25*(double)isocount1[3]<massRange[1] &&
							monorange[1]+0.25*(double)isocount1[3]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+0.25*(double)isocount1[3];
						range[1] = monorange[1]+0.25*(double)isocount1[3];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
System.out.println("411\t"+(range[2]-range[1])/(range[3]-range[0])+"\t"+pi.getIntensity()+"\t"+preIsoScore[3]);
						
						if(down1[3]){
							if(pi.getIntensity()<isointen1[3]){
								preIsoScore[3]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity();
								preIsoInten[3]+=pi.getIntensity();
								isocount1[3]++;
								isointen1[3] = pi.getIntensity();
							}
						}else{
							preIsoScore[3]+=(range[2]-range[1])/(massRange[1]-massRange[0])*pi.getIntensity();
							preIsoInten[3]+=pi.getIntensity();
							isocount1[3]++;
							if(pi.getIntensity()<isointen1[3]){
								down1[3] = true;
							}
							isointen1[3] = pi.getIntensity();
						}
System.out.println("411\t"+(range[2]-range[1])/(range[3]-range[0])+"\t"+pi.getIntensity()+"\t"+preIsoScore[3]);
						
					}
					
					if(monorange[0]+0.3333*(double)isocount1[2]<massRange[1] &&
							monorange[1]+0.3333*(double)isocount1[2]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+0.3333*(double)isocount1[2];
						range[1] = monorange[1]+0.3333*(double)isocount1[2];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
						
						if(pi.getIntensity()*0.5<isointen1[2]){
							preIsoScore[2]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
							preIsoInten[2]+=pi.getIntensity();
							isocount1[2]++;
							isointen1[2] = pi.getIntensity();
						}
						
/*						if(down1[2]){
							if(pi.getIntensity()<isointen1[2]){
								preIsoScore[2]++;
								preIsoInten[2]+=pi.getIntensity();
								isocount1[2]++;
								isointen1[2] = pi.getIntensity();
							}
						}else{
							preIsoScore[2]++;
							preIsoInten[2]+=pi.getIntensity();
							isocount1[2]++;
							if(pi.getIntensity()<isointen1[2]){
								down1[2] = true;
							}
							isointen1[2] = pi.getIntensity();
						}
*/						
					}
					
					if(monorange[0]+0.5*(double)isocount1[1]<massRange[1] &&
							monorange[1]+0.5*(double)isocount1[1]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+0.5*(double)isocount1[1];
						range[1] = monorange[1]+0.5*(double)isocount1[1];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
System.out.println("475\t"+(range[2]-range[1])/(range[3]-range[0])+"\t"+pi.getIntensity()+"\t"+preIsoScore[1]);

						if(pi.getIntensity()*0.25<isointen1[1]){
							preIsoScore[1]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
							preIsoInten[1]+=pi.getIntensity();
							isocount1[1]++;
							isointen1[1] = pi.getIntensity();
						}
/*						if(down1[1]){
							if(pi.getIntensity()<isointen1[1]){
								preIsoScore[1]++;
								preIsoInten[1]+=pi.getIntensity();
								isocount1[1]++;
								isointen1[1] = pi.getIntensity();
							}
						}else{
							preIsoScore[1]++;
							preIsoInten[1]+=pi.getIntensity();
							isocount1[1]++;
							if(pi.getIntensity()<isointen1[1]){
								down1[1] = true;
							}
							isointen1[1] = pi.getIntensity();
						}
*/						
					}
					
					if(monorange[0]+1.0*(double)isocount1[0]<massRange[1] &&
							monorange[1]+1.0*(double)isocount1[0]>massRange[0]){
						
						double [] range = new double [4];
						range[0] = monorange[0]+1.0*(double)isocount1[0];
						range[1] = monorange[1]+1.0*(double)isocount1[0];
						range[2] = massRange[0];
						range[3] = massRange[1];
						Arrays.sort(range);
						
						if(pi.getIntensity()*0.25<isointen1[0]){
							preIsoScore[0]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
							preIsoInten[0]+=pi.getIntensity();
							isocount1[0]++;
							isointen1[0] = pi.getIntensity();
						}
/*						if(down1[0]){
							if(pi.getIntensity()<isointen1[0]){
								preIsoScore[0]++;
								preIsoInten[0]+=pi.getIntensity();
								isocount1[0]++;
								isointen1[0] = pi.getIntensity();
							}
						}else{
							preIsoScore[0]++;
							preIsoInten[0]+=pi.getIntensity();
							isocount1[0]++;
							if(pi.getIntensity()<isointen1[0]){
								down1[0] = true;
							}							
							isointen1[0] = pi.getIntensity();
						}
*/						
					}
				}
				
				int [] isocount2 = new int [5];
				Arrays.fill(isocount2, 1);

				double [] isointen2 = new double [5];
				boolean [] down2 = new boolean [5];
				Arrays.fill(isointen2, monointen);
				for(int i=preId-1;i>=0;i--){
					IPeak pi = preIsotope.get(i);
					double [] massRange = pi.getMassRange();
					if(monointen/pi.getIntensity()>2.5 || pi.getIntensity()/monointen>10)
						continue;
					
					if(massRange[0]+0.2*(double)isocount2[4]<monorange[1] &&
							massRange[1]+0.2*(double)isocount2[4]>monorange[0]){
						
						double [] range = new double [4];
						range[0] = massRange[0]+0.2*(double)isocount2[4];
						range[1] = massRange[1]+0.2*(double)isocount2[4];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
						
						if(down2[4]){
							if(pi.getIntensity()<isointen2[4]){
								preIsoScore[4]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
								preIsoInten[4]+=pi.getIntensity();
								isocount2[4]++;
								isointen2[4] = pi.getIntensity();
							}
						}else{
							preIsoScore[4]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
							preIsoInten[4]+=pi.getIntensity();
							isocount2[4]++;
							if(pi.getIntensity()<isointen2[4]){
								down2[4] = true;
							}
							isointen2[4] = pi.getIntensity();
						}
					}
					
					if(massRange[0]+0.25*(double)isocount2[3]<monorange[1] &&
							massRange[1]+0.25*(double)isocount2[3]>monorange[0]){
						
						double [] range = new double [4];
						range[0] = massRange[0]+0.25*(double)isocount2[3];
						range[1] = massRange[1]+0.25*(double)isocount2[3];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
System.out.println("586\t"+(range[2]-range[1])/(range[3]-range[0])+"\t"+pi.getIntensity()+"\t"+preIsoScore[3]);
						if(down2[3]){
							if(pi.getIntensity()<isointen2[3]){
								preIsoScore[3]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
								preIsoInten[3]+=pi.getIntensity();
								isocount2[3]++;
								isointen2[3] = pi.getIntensity();
							}
						}else{
							preIsoScore[3]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
							preIsoInten[3]+=pi.getIntensity();
							isocount2[3]++;
							if(pi.getIntensity()<isointen2[3]){
								down2[3] = true;
							}
							isointen2[3] = pi.getIntensity();
						}
System.out.println("586\t"+(range[2]-range[1])/(range[3]-range[0])+"\t"+pi.getIntensity()+"\t"+preIsoScore[3]);						
					}
					
					if(massRange[0]+0.3333*(double)isocount2[2]<monorange[1] &&
							massRange[1]+0.3333*(double)isocount2[2]>monorange[0]){

						double [] range = new double [4];
						range[0] = massRange[0]+0.3333*(double)isocount2[2];
						range[1] = massRange[1]+0.3333*(double)isocount2[2];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
						
						if(pi.getIntensity()*4>isointen2[2]){
							preIsoScore[2]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
							preIsoInten[2]+=pi.getIntensity();
							isocount2[2]++;
							isointen2[2] = pi.getIntensity();
						}
/*						if(down2[2]){
							if(pi.getIntensity()<isointen2[2]){
								preIsoScore[2]++;
								preIsoInten[2]+=pi.getIntensity();
								isocount2[2]++;
								isointen2[2] = pi.getIntensity();
							}
						}else{
							preIsoScore[2]++;
							preIsoInten[2]+=pi.getIntensity();
							isocount2[2]++;
							if(pi.getIntensity()<isointen2[2]){
								down2[2] = true;
							}
							isointen2[2] = pi.getIntensity();
						}
*/						
					}
					
					if(massRange[0]+0.5*(double)isocount2[1]<monorange[1] &&
							massRange[1]+0.5*(double)isocount2[1]>monorange[0]){
						
						double [] range = new double [4];
						range[0] = massRange[0]+0.5*(double)isocount2[1];
						range[1] = massRange[1]+0.5*(double)isocount2[1];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
						
						if(pi.getIntensity()*4>isointen2[1]){
							preIsoScore[1]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
							preIsoInten[1]+=pi.getIntensity();
							isocount2[1]++;
							isointen2[1] = pi.getIntensity();
						}
/*						if(down2[1]){
							if(pi.getIntensity()<isointen2[1]){
								preIsoScore[1]++;
								preIsoInten[1]+=pi.getIntensity();
								isocount2[1]++;
								isointen2[1] = pi.getIntensity();
							}
						}else{
							preIsoScore[1]++;
							preIsoInten[1]+=pi.getIntensity();
							isocount2[1]++;
							if(pi.getIntensity()<isointen2[1]){
								down2[1] = true;
							}
							isointen2[1] = pi.getIntensity();
						}
*/						
					}
					
					if(massRange[0]+1.0*(double)isocount2[0]<monorange[1] &&
							massRange[1]+1.0*(double)isocount2[0]>monorange[0]){
						
						double [] range = new double [4];
						range[0] = massRange[0]+1.0*(double)isocount2[0];
						range[1] = massRange[1]+1.0*(double)isocount2[0];
						range[2] = monorange[0];
						range[3] = monorange[1];
						Arrays.sort(range);
						
						if(pi.getIntensity()*4>isointen2[0] && isocount2[0]==1){
							preIsoScore[0]+=(range[2]-range[1])/(range[3]-range[0])*pi.getIntensity();
							preIsoInten[0]+=pi.getIntensity();
							isocount2[0]++;
							isointen2[0] = pi.getIntensity();
						}
/*						if(down2[0]){
							if(pi.getIntensity()<isointen2[0]){
								preIsoScore[0]++;
								preIsoInten[0]+=pi.getIntensity();
								isocount2[0]++;
								isointen2[0] = pi.getIntensity();
							}
						}else{
							preIsoScore[0]++;
							preIsoInten[0]+=pi.getIntensity();
							isocount2[0]++;
							if(pi.getIntensity()<isointen2[0]){
								down2[0] = true;
							}
							isointen2[0] = pi.getIntensity();
						}
*/						
					}
				}
			

				int charge = 0;
				double maxScore = 0;
				double maxInten = 0;
				for(int i=0;i<preIsoScore.length;i++){
					if(preIsoScore[i]==0)
						continue;
					
					if(preIsoScore[i]>maxScore){
						maxScore = preIsoScore[i];
						maxInten = preIsoInten[i];
						charge = i+1;
					}else if(preIsoScore[i]==maxScore){
						if(charge==1){
							maxScore = preIsoScore[i];
							maxInten = preIsoInten[i];
							charge = i+1;
						}else{
							if(preIsoInten[i]>maxInten){
								maxScore = preIsoScore[i];
								maxInten = preIsoInten[i];
								charge = i+1;
							}
						}
						
					}else{
						if(charge==1){
							maxScore = preIsoScore[i];
							maxInten = preIsoInten[i];
							charge = i+1;
						}
					}
				}

				System.out.println(monomass+"\t"+precursorMz+"\t"+charge+"\t"+Arrays.toString(preIsoScore)+"\t"+preIsotope.size()+"\t"+
						ms1LeastIntensity+"\t"+Arrays.toString(isocount1)+"\t"+Arrays.toString(isocount2));
				
				if(charge==0){
					
//					nocharge++;
					
				}else{
					for(int i=preId-1;i>=0;i--){
						IPeak pi = preIsotope.get(i);
						if(Math.abs(monomass-pi.getMz()-1.0/(double)charge)<0.01){
							if(charge<=3){
								if(pi.getIntensity()>monointen){
									monomass = pi.getMz();
									monointen = pi.getIntensity();
								}
							}else{
								if(Math.abs(Math.log10(monointen/pi.getIntensity()))<0.5){
									monomass = pi.getMz();
									monointen = pi.getIntensity();
								}
							}
						}
					}
				}
				
//				System.out.println(monomass+"\t"+precursorMz+"\t"+charge+"\t"+Arrays.toString(preIsoScore)+"\t"+preIsotope.size()+"\t"+
//						ms1LeastIntensity+"\t"+Arrays.toString(isocount1)+"\t"+Arrays.toString(isocount2));
				for(int i=0;i<preIsotope.size();i++){
					IPeak peaki = preIsotope.get(i);
					System.out.println(precursorMz+"\t"+peaki.getMz()+"\t"+peaki.getIntensity()+"\t"+
							(peaki.getMz()-monomass)+"\t"+(peaki.getMz()-precursorMz));
				}
				System.out.println("~~~~~~~~~~~~~~~");
				for(int i=precursorId+1;i<ms1MzIntenList[0].length;i++){
//				for(int i=0;i<ms1MzIntenList[0].length;i++){
					double mzi = ms1MzIntenList[0][i];
					double inteni = ms1MzIntenList[1][i];
					System.out.println(mzi+"\t"+inteni);
					if(mzi-precursorMz>3)
						break;
				}
				System.exit(0);

				if(monomass<0) continue;
				StringBuilder namesb = new StringBuilder();
				namesb.append("Elution from: ").append(rt).append(" to ").append(rt);
				namesb.append(" period: 0 experiment: 1 cycles: 1 precIntensity: ").append(monointen);
				namesb.append(" FinneganScanNumber: ").append(scanNum);
				namesb.append(" MStype: enumIsNormalMS rawFile: ").append(file.getName());

				StringBuilder sb = new StringBuilder();
				sb.append("BEGIN IONS"+lineSeparator);
				sb.append("PEPMASS="+monomass+lineSeparator);
				if(charge>1)
					sb.append("CHARGE="+charge+"+"+lineSeparator);
				sb.append("TITLE="+namesb+lineSeparator);
				
				for(int i=0;i<highIntenList.size();i++){
					IPeak peaki = highIntenList.get(i);
					sb.append(peaki.getMz()+"\t"+peaki.getIntensity()+lineSeparator);
				}
				
				sb.append("END IONS"+lineSeparator);
				writer.write(sb.toString());
}
			}
		}
		
		writer.close();
	}

	public void toMgfDirect(String output) throws IOException{
		
		PrintWriter writer = new PrintWriter(output);
		double [][] ms1MzIntenList = null;
		double ms1LeastIntensity = 0;
		
		while(msParser.hasNextScan()){
			Scan scan = null;
			
			try {
				scan = msParser.getNextScan();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ScanHeader header = scan.header;
			
			int scanNum = header.getNum();
			int msLevel = header.getMsLevel();
			double rt = header.getDoubleRetentionTime()/60.0d;
			float totIonCurrent = header.getTotIonCurrent();
			float basepeakInten = header.getBasePeakIntensity();

			if(msLevel==1){

				ms1MzIntenList = scan.getMassIntensityList();
				ms1LeastIntensity = 100000;
				int up = ms1MzIntenList[1].length>50 ? 50 : ms1MzIntenList[1].length;
				for(int i=0;i<up;i++){
					double inteni = ms1MzIntenList[1][i];
					if(inteni>0 && inteni<ms1LeastIntensity){
						ms1LeastIntensity = (int) inteni;
					}
				}
				ms1LeastIntensity = ms1LeastIntensity<25? ms1LeastIntensity*2 : ms1LeastIntensity*1.5;

			}else{
if(scanNum==5454){
				int precursorScanNum = this.preScanNum;
				double precursorMz = header.getPrecursorMz();
				double precursorIntensity = header.getPrecursorIntensity();

				double [][] mzIntenList = scan.getMassIntensityList();
				double leastIntensity = 10000;
				int up = mzIntenList[1].length>50 ? 50 : mzIntenList[1].length;
				for(int i=0;i<up;i++){
					double inteni = mzIntenList[1][i];
					if(inteni>0 && inteni<leastIntensity){
						leastIntensity = inteni;
					}
				}
//				leastIntensity = leastIntensity;

				boolean begin = false;
				ArrayList <Double> mzList = null;
				ArrayList <Double> intensityList = null;
				
				int misscount = 0;
				ArrayList <IPeak> highIntenList = new ArrayList <IPeak>();
				
				for(int i=2;i<mzIntenList[0].length-2;i++){

					double mzi = mzIntenList[0][i];
					double inteni = (mzIntenList[1][i-2]+mzIntenList[1][i-1]+mzIntenList[1][i]
							+mzIntenList[1][i+1]+mzIntenList[1][i+2])/5.0;
					
					int dmid = mzi/200.0>6 ? 6 : (int)mzi/200;
					if(begin){

						if(mzi-mzIntenList[0][i-1]<=dm2[dmid]){
							if(inteni<leastIntensity){
								misscount++;
								if(misscount==2){
									if(mzList.size()>=4){
										this.addPeak(mzList, intensityList, highIntenList);
									}
									begin = false;
								}else{
									mzList.add(mzi);
									intensityList.add(inteni);
								}
							}else{
								misscount = 0;
								mzList.add(mzi);
								intensityList.add(inteni);
							}
						}else{
							if(mzList.size()>=4){
								this.addPeak(mzList, intensityList, highIntenList);
							}
							if(inteni>=leastIntensity){
								begin = true;
								misscount = 0;
								mzList = new ArrayList <Double>();
								intensityList = new ArrayList <Double>();
								mzList.add(mzi);
								intensityList.add(inteni);
							}else{
								begin = false;
							}
						}
					}else{
						if(inteni>=leastIntensity){
							begin = true;
							misscount = 0;
							mzList = new ArrayList <Double>();
							intensityList = new ArrayList <Double>();
							mzList.add(mzi);
							intensityList.add(inteni);
						}
					}
				}
				
				MonoPeak [] precursorPeaks = this.getCharge(ms1MzIntenList, ms1LeastIntensity, precursorMz);

				for(int i=0;i<precursorPeaks.length;i++){
					
					StringBuilder namesb = new StringBuilder();
					namesb.append("Elution from: ").append(df4.format(rt)).append(" to ").append(df4.format(rt));
					namesb.append(" period: 0 experiment: 1 cycles: 1 precIntensity: ").append(precursorPeaks[i].getIntensity());
					namesb.append(" FinneganScanNumber: ").append(scanNum);
					namesb.append(" MStype: enumIsNormalMS rawFile: ").append(file.getName());

					StringBuilder sb = new StringBuilder();
					sb.append("BEGIN IONS"+lineSeparator);
					sb.append("PEPMASS="+precursorPeaks[i].getMz()+lineSeparator);
					if(precursorPeaks[i].getCharge()>=1)
						sb.append("CHARGE="+precursorPeaks[i].getCharge()+"+"+lineSeparator);
					sb.append("TITLE="+namesb+lineSeparator);
					
					for(int j=0;j<highIntenList.size();j++){
						IPeak peakj = highIntenList.get(j);
						sb.append(peakj.getMz()+"\t"+df4.format(peakj.getIntensity())+lineSeparator);
					}
					
					sb.append("END IONS"+lineSeparator);
					writer.write(sb.toString());
				}
				System.exit(0);
}
			}
		}
		
		writer.close();
	}

	private class MonoPeak extends Peak{
		
		private int charge;
		
		MonoPeak(IPeak peak){
			super(peak);
		}
		
		MonoPeak(double mz, double intensity){
			super(mz, intensity);
		}
		
		private void setChareg(int charge){
			this.charge = charge;
		}
		
		private int getCharge(){
			return charge;
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public static void main(String[] args) throws IOException, XMLStreamException {
		// TODO Auto-generated method stub
		
		long begin = System.currentTimeMillis();

		MzXMLProfileReader reader = new MzXMLProfileReader("H:\\wiff2mgf\\121119_human_liver_T_2nd_batch_test-121119-test.mzXML");
		reader.toMgfDirect("H:\\wiff2mgf\\test780.mgf");
//		reader.toMgfDirect("H:\\wiff2mgf\\test.direct.mgf");
		long end = System.currentTimeMillis();
		
		System.out.println((end-begin)/1000+"s");
	}

}
