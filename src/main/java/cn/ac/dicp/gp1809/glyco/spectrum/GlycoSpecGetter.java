/* 
 ******************************************************************************
 * File: GlycoSpecGetter.java * * * Created on 2011-3-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.Glycosyl;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2011-3-16, 09:00:19
 */
public class GlycoSpecGetter {

	private IRawSpectraReader reader;
	private GlycoJudgeParameter jpara;

	private HashMap <Integer, ArrayList<AbstractGlycoSpectrum>> glySpecMap;
	
	private double intenThres;
	private double  mzThresPPM;
	private double  mzThresAMU;

	private static final double nGlycanCoreFuc = 1038.375127;
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public GlycoSpecGetter(String peakfile, int glycanType) throws XMLStreamException, IOException{

		if(peakfile.endsWith("mzXML")){
			this.reader = new MzXMLReader(peakfile);
		}else if(peakfile.endsWith("mzData")){
			this.reader = new MzDataStaxReader(peakfile);
		}else{
			throw new IOException("Unknown file type: "+peakfile);
		}
		
		this.jpara = GlycoJudgeParameter.defaultParameter();
		this.parseGlycoSpec(glycanType);
	}
	
	public GlycoSpecGetter(String peakfile, GlycoJudgeParameter jpara, int glycanType) throws XMLStreamException, IOException{

		if(peakfile.endsWith("mzXML")){
			this.reader = new MzXMLReader(peakfile);
		}else if(peakfile.endsWith("mzData")){
			this.reader = new MzDataStaxReader(peakfile);
		}else{
			throw new IOException("Unknown file type: "+peakfile);
		}
		this.jpara = jpara;
		this.parseGlycoSpec(glycanType);
	}
	
	private void parseGlycoSpec(int glycanType) throws XMLStreamException{

// the key = retention time in minute, the value = all the GlycoSpectrum in this minute
		HashMap <Integer, ArrayList<AbstractGlycoSpectrum>> glySpecMap = 
			new HashMap <Integer, ArrayList<AbstractGlycoSpectrum>>();

		double [] mzs = new double [4];
		
// 204.086649; 274.08741263499996; 292.102692635; 366.139472; 657.2348890000001
		mzs[0] = Glycosyl.HexNAc.getMonoMass()+AminoAcidProperty.PROTON_W;
		mzs[1] = Glycosyl.NeuAc_H2O.getMonoMass()+AminoAcidProperty.PROTON_W;
		mzs[2] = Glycosyl.NeuAc.getMonoMass()+AminoAcidProperty.PROTON_W;
		mzs[3] = Glycosyl.HexNAc.getMonoMass()+Glycosyl.Hex.getMonoMass()+AminoAcidProperty.PROTON_W;
//		mzs[4] = Glycosyl.Hex1HexNAc1NeuAc1.getMonoMass()+AminoAcidProperty.PROTON_W;

		intenThres = jpara.getIntenThres();
		mzThresPPM = jpara.getMzThresPPM();
		mzThresAMU = jpara.getMzThresAMU();

		double mzLowLimit = jpara.getMzLowLimit();
		int preScanNum = 0;

		ISpectrum spectrum;
		
		switch (glycanType){
			
			case 0:{
				
				while((spectrum=reader.getNextSpectrum())!=null){

					int msLevel = spectrum.getMSLevel();
					double rt = spectrum.getRTMinute();
					double totIonCurrent = spectrum.getTotIonCurrent();
					int scanKey = (int) rt;
					
					if(msLevel>1){
						
						IMS2PeakList ms2peaks = new MS2PeakList();
						MS2Scan ms2 = (MS2Scan) spectrum;
			
						float preMz = (float) ms2.getPrecursorMZ();
						float preInten = (float) ms2.getPrecursorInten();
						short preCharge = ms2.getCharge();
						int snum = ms2.getScanNum();

						if(preMz*preCharge <= nGlycanCoreFuc)
							continue;

						IMS2PeakList peaklist = ms2.getPeakList();
						IPeak [] peaks = peaklist.getPeakArray();
						int count = 0;
						int loc = 0;
						int beg = 0;
						int [] lowGlyPeaks = new int[5];
												
		L:				for(int i=0;i<peaks.length;i++){
			
							double mz = peaks[i].getMz();
							double inten = peaks[i].getIntensity();
							if(inten/totIonCurrent < intenThres)
								continue;

							if(mz > mzLowLimit){
								beg = i;
								break;
							}
							
							for(int j=loc;j<mzs.length;j++){
								
								if((mzs[j]-mz)> mzThresAMU)
									continue L;
								
								if(Math.abs(mz-mzs[j]) <= mzThresAMU){
									
									if(j==0){
										lowGlyPeaks [2] = 1;
									}else if(j==1){
										lowGlyPeaks [3] = 1;
									}else if(j==2){
										lowGlyPeaks [3] = 1;
									}else if(j==3){
										lowGlyPeaks [1] = 1;
										lowGlyPeaks [2] = 1;
									}
									
									ms2peaks.add(peaks[i]);
									count++;
									
								}else if((mz-mzs[j])> mzThresAMU){
									loc = j+1;
								}
							}
						}

						if(count>=2){

							for(int i=beg;i<peaks.length;i++){
								
								double inten = peaks[i].getIntensity();

								if(inten/totIonCurrent < intenThres)
									continue;
											
								ms2peaks.add(peaks[i]);
							}

							PrecursePeak pp = new PrecursePeak(Float.parseFloat(df4.format(preMz)), 
									Float.parseFloat(df4.format(preInten)));

							pp.setCharge(preCharge);
							ms2peaks.setPrecursePeak(pp);

							AbstractGlycoSpectrum gspec = new NGlyHCDSpec(ms2peaks, preScanNum, snum);
							gspec.setLowGlyPeaks(lowGlyPeaks);

							if(glySpecMap.containsKey(scanKey)){
								glySpecMap.get(scanKey).add(gspec);
							}else{
								ArrayList <AbstractGlycoSpectrum> glist = new ArrayList <AbstractGlycoSpectrum>();
								glist.add(gspec);
								glySpecMap.put(scanKey, glist);
							}
						}
			
					}else if(msLevel==1){
						preScanNum = spectrum.getScanNum();
					}
				}
				break;
			}

			case 1:{
				
				while((spectrum=reader.getNextSpectrum())!=null){

					int msLevel = spectrum.getMSLevel();
					double rt = spectrum.getRTMinute();
					double totIonCurrent = spectrum.getTotIonCurrent();
					int scanKey = (int) rt;
					
					if(msLevel>1){
						
						IMS2PeakList ms2peaks = new MS2PeakList();
						MS2Scan ms2 = (MS2Scan) spectrum;
			
						float preMz = (float) ms2.getPrecursorMZ();
						float preInten = (float) ms2.getPrecursorInten();
						short preCharge = ms2.getCharge();
						int snum = ms2.getScanNum();
						
//						if(preMz*preCharge <= nGlycanCoreFuc)
//							continue;

						IMS2PeakList peaklist = ms2.getPeakList();
						IPeak [] peaks = peaklist.getPeakArray();
						int count = 0;
						int loc = 0;
						int beg = 0;
						int [] lowGlyPeaks = new int[5];
						
		L:				for(int i=0;i<peaks.length;i++){
							double mz = peaks[i].getMz();
							double inten = peaks[i].getIntensity();
							if(inten/totIonCurrent < intenThres)
								continue;

							ms2peaks.add(peaks[i]);
							
							if(mz > mzLowLimit){
								beg = i;
								break;
							}
							
							for(int j=loc;j<mzs.length;j++){
								if((mzs[j]-mz)> mzThresAMU)
									continue L;
								if(Math.abs(mz-mzs[j]) <= mzThresAMU){
									if(j==0){
										lowGlyPeaks [2] = 1;
									}else if(j==1){
										lowGlyPeaks [3] = 1;
									}else if(j==2){
										lowGlyPeaks [3] = 1;
									}else if(j==3){
										lowGlyPeaks [1] = 1;
										lowGlyPeaks [2] = 1;
									}
									count++;
								}else if((mz-mzs[j])> mzThresAMU){
									loc = j+1;
								}
							}
						}

						if(count>=2){
							
							for(int i=beg;i<peaks.length;i++){
								
								double inten = peaks[i].getIntensity();

								if(inten/totIonCurrent < intenThres)
									continue;
			
								ms2peaks.add(peaks[i]);
							}

							PrecursePeak pp = new PrecursePeak(Float.parseFloat(df4.format(preMz)), 
									Float.parseFloat(df4.format(preInten)));

							pp.setCharge(preCharge);
							ms2peaks.setPrecursePeak(pp);

							AbstractGlycoSpectrum gspec = new OGlyHCDSpec(ms2peaks, preScanNum, snum);
							gspec.setLowGlyPeaks(lowGlyPeaks);
	
							if(glySpecMap.containsKey(scanKey)){
								glySpecMap.get(scanKey).add(gspec);
							}else{
								ArrayList <AbstractGlycoSpectrum> glist = new ArrayList <AbstractGlycoSpectrum>();
								glist.add(gspec);
								glySpecMap.put(scanKey, glist);
							}
						}
			
					}else if(msLevel==1){
						preScanNum = spectrum.getScanNum();
					}
				}
				break;
			}

			default :{
				
				System.out.println("glycanType");
				System.out.println("Not design~");
				break;
			}
		}

		this.glySpecMap = glySpecMap;
	}

	public HashMap <Integer, ArrayList<AbstractGlycoSpectrum>> getGlySpecMap(){
		return glySpecMap;
	}
	
	public ArrayList <AbstractGlycoSpectrum> getGlycoSpecList(int rtMinute){
		return glySpecMap.get(rtMinute);
	}

	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XMLStreamException, IOException {
		
		long begin = System.currentTimeMillis();
		
//		String file = "F:\\data\\GlycoQuant\\new\\" +
//				"110325_AHSG_diemthyl_HCD.mzXML";
		
		String file = "I:\\AGP\\20121002_AGP_elestase.mzXML";
//		String file = "I:\\glyco\\SILAC\\20111123_HILIC_SILAC_HCD_111123230330.mzXML";
//		String file = "I:\\glyco\\dimethyl\\20111126_diglycols_cancer_1_HCD_30%.mzXML";
		
		GlycoSpecGetter getter = new GlycoSpecGetter(file, 0);
		HashMap <Integer, ArrayList<AbstractGlycoSpectrum>> glySpecMap = getter.getGlySpecMap();
		System.out.println(glySpecMap.size());
		
		int total = 0;
		Iterator <Integer> it = glySpecMap.keySet().iterator();
		while(it.hasNext()){
			Integer rt = it.next();
			ArrayList<AbstractGlycoSpectrum> splist = glySpecMap.get(rt);
			
			total += splist.size();
/*			
//			System.out.println(rt+"\t");
			for(int i=0;i<splist.size();i++){
				
				AbstractGlycoSpectrum abspec = splist.get(i);
				int scannum = abspec.getGlycoScanNum();
				IPeak [] peaks = abspec.getPeakList().getPeakList();
				
				if(scannum==3842){
					for(int j=0;j<peaks.length;j++){
						System.out.println(peaks[j]);
					}
					System.out.println();
					System.out.print(splist.get(i).toString());
				}
			}
*/			
//			System.out.print("\n");
			
		}
		System.out.println(total);
/*		
		
		ArrayList<GlycoSpectrum> speclist = glySpecMap.get(57);
		System.out.println(speclist.size());
		double [] mass = new double []{1923.9919, 1940.08064};
//		mass[0] = new double []{1895.9609, 1923.9919};
//		mass[1] = new double []{1904.00497, 1940.08064};
//		mass[0] = new double []{1923.9919};
//		mass[1] = new double []{1940.08064};
		
		for(int i=0;i<speclist.size();i++){
			Pixel [] pixs = getter.pepMatching(speclist.get(i), mass);
		}
		
		double d1 = Glycosyl.dHex.getMonoMass();
		double d2 = Glycosyl.Hex.getMonoMass();
		double d3 = Glycosyl.HexNAc.getMonoMass();
		System.out.println(d2*3+d3*2);
		System.out.println(d2*3+d3*2+d1);
*/		
		
		long end = System.currentTimeMillis();
		
		System.out.println("Total time:\t"+(end-begin)/1000.0);
	}

}
