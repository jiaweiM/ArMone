/* 
 ******************************************************************************
 * File: NGlycoSpecFeaturesGetter.java * * * Created on 2013-7-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.Glycosyl;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoDatabaseMatcher;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoConstructor;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 * 
 * @version 2013-7-23, 10:13:29
 */
public class NGlycoSpecFeaturesGetter {

	private IRawSpectraReader reader;
	private GlycoJudgeParameter jpara;
	private GlycoDatabaseMatcher matcher;

	private double intenThres;
	private double mzThresPPM;
	private double mzThresAMU;

	private int topn;
	private MS1ScanList scanlist;
	private HashMap<Integer, Integer> isotopeMap;
	private NGlycoSSM[] ssms;

	/**
	 * nbt.1511-S1, p9
	 */
	protected static final double dm = 1.00286864;

	private static final double nGlycanCoreFuc = 1038.375127;
	protected static final double Hex = Glycosyl.Hex.getMonoMass();
	protected static final double HexNAc = Glycosyl.HexNAc.getMonoMass();

	public NGlycoSpecFeaturesGetter(String peakfile) throws IOException,
			XMLStreamException {

		this(peakfile, GlycoJudgeParameter.defaultParameter());
	}

	public NGlycoSpecFeaturesGetter(String peakfile, GlycoJudgeParameter jpara)
			throws IOException, XMLStreamException {

		if (peakfile.endsWith("mzXML")) {

			this.reader = new MzXMLReader(peakfile);
			this.scanlist = new MS1ScanList(DtaType.MZXML);

		} else if (peakfile.endsWith("mzData")) {

			this.reader = new MzDataStaxReader(peakfile);
			this.scanlist = new MS1ScanList(DtaType.MZDATA);

		} else {
			throw new IOException("Unknown file type: " + peakfile);
		}

		this.jpara = jpara;
		this.matcher = new GlycoDatabaseMatcher(jpara.getMzThresPPM());
		this.isotopeMap = new HashMap<Integer, Integer>();
		this.parseNGlyco();
	}

	private void parseNGlyco() throws IOException {

		HashMap <Integer, IMS2Scan> ms2ScanMap = new HashMap <Integer, IMS2Scan>();
		HashMap <Integer, Double> neuAcMap = new HashMap <Integer, Double>();
		double ms1TotalCurrent = reader.getMS1TotalCurrent();
		
		double [] mzs = new double [5];
		

		// 162.052824; 204.086649; 274.08741263499996; 292.102692635; 366.139472; 657.2348890000001
		mzs[0] = Glycosyl.Hex.getMonoMass()+AminoAcidProperty.PROTON_W;
		mzs[1] = Glycosyl.HexNAc.getMonoMass()+AminoAcidProperty.PROTON_W;
		mzs[2] = Glycosyl.NeuAc_H2O.getMonoMass()+AminoAcidProperty.PROTON_W;
		mzs[3] = Glycosyl.NeuAc.getMonoMass()+AminoAcidProperty.PROTON_W;
		mzs[4] = Glycosyl.Hex.getMonoMass()+Glycosyl.HexNAc.getMonoMass()+AminoAcidProperty.PROTON_W;
		
		intenThres = jpara.getIntenThres();
		mzThresPPM = jpara.getMzThresPPM();
		mzThresAMU = jpara.getMzThresAMU();
		topn = jpara.getTopnStructure();

		ArrayList <NGlycoSSM> list = new ArrayList <NGlycoSSM>();
		ISpectrum spectrum;
		IPeak [] ms1Peaks = null;

		while((spectrum=reader.getNextSpectrum())!=null){

			int msLevel = spectrum.getMSLevel();
			double totIonCurrent = spectrum.getTotIonCurrent();
			
			if(msLevel>1){

				MS2Scan ms2 = (MS2Scan) spectrum;

				float preMz = (float) ms2.getPrecursorMZ();
				short preCharge = ms2.getCharge();
				int snum = ms2.getScanNum();

				if(preMz*preCharge <= nGlycanCoreFuc)
					continue;

				int count = 0;
				IMS2PeakList peaklist = ms2.getPeakList();
				
				IPeak [] peaks = peaklist.getPeakArray();
				double [] symbolPeakIntensity = new double [5];
			
L:				for(int i=0;i<peaks.length;i++){

					double mz = peaks[i].getMz();
					double inten = peaks[i].getIntensity();
					if(inten/totIonCurrent < intenThres)
						continue;

					if((mz-mzs[4])> mzThresAMU){
						break;
					}
					
					for(int j=0;j<mzs.length;j++){
						
						if((mzs[j]-mz)> mzThresAMU)
							continue L;
						
						if(Math.abs(mz-mzs[j]) <= mzThresAMU){
							if(symbolPeakIntensity[j]==0){
								symbolPeakIntensity[j] = inten;
								count++;
							}else{
								if(inten>symbolPeakIntensity[j]){
									symbolPeakIntensity[j] = inten;
								}
							}
						}
					}
				}

				if(count>=2){
					
					this.findIsotope(ms2, ms1Peaks);
					ms2ScanMap.put(snum, ms2);
					double neuAcScore = 0;
					if(symbolPeakIntensity[1]>0){
						neuAcScore += symbolPeakIntensity[2]>0 ? (symbolPeakIntensity[2]/symbolPeakIntensity[1]+1) : 0;
						neuAcScore += symbolPeakIntensity[3]>0 ? (symbolPeakIntensity[3]/symbolPeakIntensity[1]+1) : 0;
					}else{
						neuAcScore += symbolPeakIntensity[2]>0 ? 1 : 0;
						neuAcScore += symbolPeakIntensity[3]>0 ? 1 : 0;
					}
					neuAcMap.put(snum, neuAcScore);
				}
	
			}else if(msLevel==1){
				this.scanlist.add(spectrum);
				ms1Peaks = spectrum.getPeakList().getPeakArray();
			}
		}

		Integer [] scans = ms2ScanMap.keySet().toArray(new Integer[ms2ScanMap.size()]);
		Arrays.sort(scans);
		for(int scanid=0;scanid<scans.length;scanid++){

			Integer scannum = scans[scanid];
			IMS2Scan ms2 = ms2ScanMap.get(scannum);

			float preMz = (float) ms2.getPrecursorMZ();
			float preInten = (float) ms2.getPrecursorInten();
			short preCharge = ms2.getCharge();
			
			IMS2PeakList peaklist = ms2.getPeakList();
			PrecursePeak ppeak = peaklist.getPrecursePeak();
			int preScanNum0 = ppeak.getScanNum();
			double rt = ppeak.getRT();

			NGlycoConstructor gc = new NGlycoConstructor(preMz, preCharge, mzThresPPM);
			gc.setScanNum(scannum);

			IPeak [] peaks = peaklist.getPeakArray();
			
			double [] peakinten = new double[peaks.length];
			for(int i=0;i<peaks.length;i++){
				peakinten[i] = peaks[i].getIntensity();
			}
			Arrays.sort(peakinten);
			
			double ave = 0;
			ArrayList <Double> ddlist = new ArrayList <Double>();
			for(int i=0;i<peakinten.length;i++){
				
				if(ddlist.size()==10){
					double rsd = MathTool.getRSDInDouble(ddlist);
					ddlist.remove(0);
					
					if(rsd>0.05){
						ave = MathTool.getAveInDouble(ddlist);
						break;
					}
				}
				ddlist.add(peakinten[i]);
			}
			
			for(int i=0;i<peaks.length;i++){
					
				double inten = peaks[i].getIntensity();

				if(inten < ave)
					continue;

				gc.addNCorePeak(peaks[i]);
			}

			gc.initial();

			NGlycoSSM [] ssm = matcher.match(gc, isotopeMap.get(scannum), neuAcMap.get(scannum));

			if(ssm!=null){

				for(int i=0;i<ssm.length;i++){
					
					if(ssm[i].getRank()>topn){
						break;
					}

					ssm[i].setMS1Scannum(preScanNum0);
					ssm[i].setRT(rt);

					list.add(ssm[i]);
				}
			}else{
//				System.out.println("null");
			}
		}

		this.ssms = list.toArray(new NGlycoSSM[list.size()]);
	}

	private void findIsotope(IMS2Scan scan, IPeak[] ms1Peaks) {
		
		IPeak precursorPeak = scan.getPeakList().getPrecursePeak();
		int isoloc = Arrays.binarySearch(ms1Peaks, precursorPeak);
		if(isoloc<0) isoloc = -isoloc-1;
		if(isoloc>=ms1Peaks.length){
			this.isotopeMap.put(scan.getScanNumInteger(), 0);
			return;
		}
		int charge = scan.getCharge();
		double mz = precursorPeak.getMz();
		double intensity = precursorPeak.getIntensity();
		int k=1;
		int i=isoloc;

		for(;i>=0;i--){
			
			double delta = mz-ms1Peaks[i].getMz()-k*dm/(double)charge;
			double loginten = Math.log10(intensity/ms1Peaks[i].getIntensity());
			if(Math.abs(delta)<=mz*mzThresPPM*1E-6){
				if(Math.abs(loginten)<1){
					k++;
					if(intensity<ms1Peaks[i].getIntensity())
						intensity = ms1Peaks[i].getIntensity();
					
				}
			}else if(delta>mz*mzThresPPM*1E-6){
				break;
			}
			
			if(k>7) break;
		}
		this.isotopeMap.put(scan.getScanNumInteger(), k-1);
	}

	public NGlycoSSM[] getGlycoSSMs(){
		return ssms;
	}
	
	public MS1ScanList getScanList(){
		return scanlist;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
