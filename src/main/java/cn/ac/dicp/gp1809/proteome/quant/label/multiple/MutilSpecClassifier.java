/* 
 ******************************************************************************
 * File: MutilSpecClassfier.java * * * Created on 2012-6-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.stream.XMLStreamException;

import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ms2.MS2ScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.proteome.util.MaxQuantScanName;

/**
 * @author ck
 *
 * @version 2012-6-15, 10:55:06
 */
public class MutilSpecClassifier {
	
	private MS1ScanList ms1ScanList;
	private HashMap <Integer, double[][]> preMzMap;
	private static final double ppm = 2E-5;
	private ArrayList <Integer> [] typeLists;
	private HashMap <Integer, Integer> typeMap;
	private HashMap <Integer, IMS2PeakList> peakMap;
	private HashMap <Integer, Double> ppMzMap;
	
	private String rawName;
	private File parent;
	
	/**
	 * nbt.1511-S1, p9
	 */
	private final static double dm = 1.00286864;
	
	/**
	 * 
	 */
	private final static double labelDiff = 4.019263;

	public MutilSpecClassifier(String peakfile, String output) throws XMLStreamException, IOException{
		
		IRawSpectraReader reader;
		DtaType dtaType = null;
		
		if(peakfile.endsWith("mzXML")){
			
			reader = new MzXMLReader(peakfile);
			dtaType = DtaType.MZXML;
			
			int sp = peakfile.lastIndexOf("\\");
			this.rawName = peakfile.substring(sp+1).replace("mzXML", "raw");
			
		}else if(peakfile.endsWith("mzData")){
			
			reader = new MzDataStaxReader(peakfile);
			dtaType = DtaType.MZDATA;
			
			int sp = peakfile.lastIndexOf("\\");
			this.rawName = peakfile.substring(sp+1).replace("mzData", "raw");
			
		}else{
			throw new IOException("Unknown file type "+peakfile);
		}
		
		this.typeLists = new ArrayList [7];
		for(int i=0;i<typeLists.length;i++){
			typeLists[i] = new ArrayList <Integer>();
		}
		this.typeMap = new HashMap <Integer, Integer>();
		this.peakMap = new HashMap <Integer, IMS2PeakList>();
		this.ppMzMap = new HashMap <Integer, Double>();
//		this.parent = new File(output);
		
		ISpectrum spectrum = null;

		this.ms1ScanList = new MS1ScanList(dtaType);
		this.preMzMap = new HashMap <Integer, double[][]>();
		ArrayList <double[]> list = new ArrayList <double[]>();
		int ms1Scannum = 0;
		
		while((spectrum=reader.getNextSpectrum())!=null){

			int level = spectrum.getMSLevel();
			if(level==1){
				
				ms1ScanList.add(spectrum);
				if(list.size()!=0){
					double [][] mzs = list.toArray(new double[list.size()][]);
					this.preMzMap.put(ms1Scannum, mzs);
					list = new ArrayList <double[]>();
				}
				ms1Scannum = spectrum.getScanNum();
				
			}else if(level==2){
				
				IMS2Scan ms2Scan = (IMS2Scan) spectrum;
				double preMz = ms2Scan.getPrecursorMZ();
				short charge = ms2Scan.getCharge();
				int scannum = ms2Scan.getScanNum();
				list.add(new double[]{preMz, charge, scannum});
				peakMap.put(scannum, ms2Scan.getPeakList());
			}
		}		
		
		reader.close();
		
		this.classify();		
	}
	
	public MutilSpecClassifier(String peakfile, String mgf, String output) throws XMLStreamException, IOException, DtaFileParsingException{
		
		IRawSpectraReader reader;
		DtaType dtaType = null;
		
		if(peakfile.endsWith("mzXML")){
			
			reader = new MzXMLReader(peakfile);
			dtaType = DtaType.MZXML;
			
			int sp = peakfile.lastIndexOf("\\");
			this.rawName = peakfile.substring(sp+1).replace("mzXML", "raw");
			
		}else if(peakfile.endsWith("mzData")){
			
			reader = new MzDataStaxReader(peakfile);
			dtaType = DtaType.MZDATA;
			
			int sp = peakfile.lastIndexOf("\\");
			this.rawName = peakfile.substring(sp+1).replace("mzData", "raw");
			
		}else{
			throw new IOException("Unknown file type "+peakfile);
		}
		
		this.typeLists = new ArrayList [7];
		for(int i=0;i<typeLists.length;i++){
			typeLists[i] = new ArrayList <Integer>();
		}
		this.typeMap = new HashMap <Integer, Integer>();
		this.peakMap = new HashMap <Integer, IMS2PeakList>();
		this.ppMzMap = new HashMap <Integer, Double>();
		this.parent = new File(output);
		
		MgfReader mgfreader = new MgfReader(mgf);
		MS2Scan ms2scan = null;
		while((ms2scan=mgfreader.getNextMS2Scan())!=null){
			int scannum = ms2scan.getScanNum();
			peakMap.put(scannum, ms2scan.getPeakList());
		}
		mgfreader.close();
		
		ISpectrum spectrum = null;

		this.ms1ScanList = new MS1ScanList(dtaType);
		this.preMzMap = new HashMap <Integer, double[][]>();
		ArrayList <double[]> list = new ArrayList <double[]>();
		int ms1Scannum = 0;
		
		while((spectrum=reader.getNextSpectrum())!=null){

			int level = spectrum.getMSLevel();
			
			if(level==1){
				
				ms1ScanList.add(spectrum);
				if(list.size()!=0){
					double [][] mzs = list.toArray(new double[list.size()][]);
					this.preMzMap.put(ms1Scannum, mzs);
					list = new ArrayList <double[]>();
				}
				ms1Scannum = spectrum.getScanNum();
				
			}else if(level==2){
				
				int scannum = spectrum.getScanNum();
				if(peakMap.containsKey(scannum)){
					
					IMS2PeakList peaklist = peakMap.get(scannum);
					double preMz = peaklist.getPrecursePeak().getMz();
					short charge = peaklist.getPrecursePeak().getCharge();
					
					list.add(new double[]{preMz, charge, scannum});
				}
			}
		}		
		
		reader.close();
		
		this.classify();		
	}
	
	private void classify() throws IOException{
		
		Integer [] ms1Scans = this.preMzMap.keySet().toArray(new Integer[this.preMzMap.size()]);
		Arrays.sort(ms1Scans);

//		PrintWriter pw = new PrintWriter("H:\\WFJ_mutiple_label\\2D\\test.txt");
//		PrintWriter pw2 = new PrintWriter("H:\\WFJ_mutiple_label\\2D\\test2.txt");
		
L:		for(int num=0;num<ms1Scans.length;num++){

			Integer ms1Scannum = ms1Scans[num];
			IMS1Scan ms1Scan = this.ms1ScanList.getScan(ms1Scannum);
			double [][] list = this.preMzMap.get(ms1Scannum);
			IPeakList peaklist = ms1Scan.getPeakList();
			
			Integer preMs1Scannum = -1;
			Integer nexMs1Scannum = -1;
			IPeakList prePeaks = null;
			IPeakList nexPeaks = null;
			if(num>=1){
				preMs1Scannum = ms1Scans[num-1];
				prePeaks = this.ms1ScanList.getScan(preMs1Scannum).getPeakList();
			}
			if(num<ms1Scans.length-1){
				nexMs1Scannum = ms1Scans[num+1];
				nexPeaks = this.ms1ScanList.getScan(nexMs1Scannum).getPeakList();
			}
			
			for(int i=0;i<list.length;i++){
				
				double mz = list[i][0];
				double charge = list[i][1];
				int scannum = (int) list[i][2];
				int [] counts = new int [6];

//if(scannum==211){
	
				int [] preBegend = null;
				int [] nexBegend = null;
				
				if(prePeaks!=null){
					preBegend = this.find(prePeaks, mz, charge);
					counts[preBegend[0]-1]++;
					counts[preBegend[1]-1]++;
				}
				
				if(nexPeaks!=null){
					nexBegend = this.find(nexPeaks, mz, charge);
					counts[nexBegend[0]-1]++;
					counts[nexBegend[1]-1]++;
				}
//System.out.println("193\t"+mz);				
				int [] begend = this.find(peaklist, mz, charge);
				counts[begend[0]-1]++;
				counts[begend[1]-1]++;
//System.out.println("197\t"+begend[2]);		
				int poss1 = -1;
				int poss2 = -1;
				for(int j=0;j<counts.length;j++){
//					pw2.write(scannum+"\t"+(j+1)+"\t"+counts[j]+"\n");
					if(counts[j]>=3){
						if(poss1==-1){
							poss1 = j;
						}else{
							poss2 = j;
						}
					}
				}
//				pw2.write(scannum+"\t"+poss1+"\t"+poss2+"\n");
				if(poss2!=-1){
					
					this.typeLists[6].add(scannum);
					
				}else{
					
					if(poss1!=-1){
						this.typeLists[poss1].add(scannum);
						this.typeMap.put(scannum, poss1+1);
					}else{
						this.typeLists[6].add(scannum);
					}
				}
				this.ppMzMap.put(scannum, mz);
//break L;				
//}
			}
		}
//		pw.close();
//		pw2.close();
	}
	
	private double findCharge(IPeakList peaklist, double mz){
		
		IPeak [] peaks = peaklist.getPeakArray();
		IPeak peaki = new Peak(mz, 0);
		int index = Arrays.binarySearch(peaks, peaki);
		if(index<0){
			index = -index-1;
		}

		IPeak findPeak = null;
		if(peaks[index].getMz()-mz>mz-peaks[index-1].getMz()){
			findPeak = peaks[index-1];
		}else{
			findPeak = peaks[index];
		}
		
		double findMz = findPeak.getMz();
		int findId1 = 0;
		int findId2 = 0;
		double peakInten1 = 0;
		double peakInten2 = 0;
		double totalInten1 = 0;
		double totalInten2 = 0;
		double [] findPeaks1 = new double [] {findMz+dm/2.0, findMz+dm*2/2.0, findMz+dm*3/2.0};
		double [] findPeaks2 = new double [] {findMz+dm/3.0, findMz+dm*2/3.0, findMz+dm*3/3.0};
		
		for(int i=index;i<peaks.length;i++){
			
			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();
					
			if(Math.abs(mzi-findPeaks1[findId1])<mz*ppm){
						
				if(inteni>peakInten1){
					peakInten1 = inteni;
					
				}else if(mzi-findPeaks1[findId1]>mz*ppm){
						
					if(peakInten1>0){
						findId1++;
						if(findId1==3){
							break;
						}else{
							peakInten1 = 0;
							totalInten1 += peakInten1;
						}
					}
				}
			}
		}
		
		for(int i=index;i<peaks.length;i++){
			
			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();
					
			if(Math.abs(mzi-findPeaks2[findId2])<mz*ppm){
						
				if(inteni>peakInten2){
					peakInten2 = inteni;
					
				}else if(mzi-findPeaks2[findId2]>mz*ppm){
						
					if(peakInten2>0){
						findId2++;
						if(findId2==3){
							break;
						}else{
							peakInten2 = 0;
							totalInten2 += peakInten1;
						}
					}
				}
			}
		}
		
		if(totalInten1>=totalInten2){
			return 2.0;
		}else{
			return 3.0;
		}
	}
	
	/**
	 * 
	 * @param peaks
	 * @param mz
	 * @param charge
	 * @return
	 */
	private int [] find(IPeakList peaklist, double mz, double charge){
		
		IPeak [] peaks = peaklist.getPeakArray();
		IPeak peaki = new Peak(mz, 0);
		int index = Arrays.binarySearch(peaks, peaki);
		if(index<0){
			index = -index-1;
		}

		IPeak findPeak = null;
		if(peaks[index].getMz()-mz>mz-peaks[index-1].getMz()){
			findPeak = peaks[index-1];
		}else{
			findPeak = peaks[index];
		}
		
		double findMz = findPeak.getMz();
		double findInten = findPeak.getIntensity();
		LinkedList <Double> findIntenList = new LinkedList <Double>();
		findIntenList.addFirst(findInten);
/*		
		int findId = 0;
		int findCount = 0;
		boolean changeMono = false;
		double peakInten = 0;
		
		double preOneMz = findMz-dm/charge;
		double preOneInten = 0;
		for(int i=index;i>=0;i--){
			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();
			
			if(Math.abs(mzi-preOneMz)<mz*ppm){
				
				if(inteni>preOneInten){
					preOneInten = inteni;
				}
				
			}else if(preOneMz-mzi>mz*ppm){
				
				if(preOneInten>findInten){
					changeMono = true;
				}
				
				break;
			}
		}
		
		if(!changeMono){
			
			double [] findPeaks = new double [] {findMz+dm/charge, findMz+dm*2/charge, findMz+dm*3/charge};
L1:			for(int i=index;i<peaks.length;i++){
						
				double mzi = peaks[i].getMz();
				double inteni = peaks[i].getIntensity();
						
				if(Math.abs(mzi-findPeaks[findId])<mz*ppm){
							
					if(inteni>peakInten){
						peakInten = inteni;
					}
							
				}else if(mzi-findPeaks[findId]>mz*ppm){
						
					if(peakInten>0){
							
						findId++;
						findCount++;
						findIntenList.addLast(peakInten);
								
						if(findId==3){
							if(findCount==3){
										
								double lastInten = findIntenList.getLast();
								for(int j=0;j<findIntenList.size()-2;j++){
									if(lastInten>findIntenList.get(j)){
										changeMono = true;
										break L1;
									}
								}
								break;
							}else{
								break;
							}
						}else{
							peakInten = 0;
						}
					}else{
						break;
					}
				}
			}
		}

System.out.println("295\t"+mz+"\t"+changeMono+"\t"+findIntenList+"\t"+charge);
		boolean changed = false;
		
		if(changeMono){
			
			findMz -= dm/charge;
			double inten = 0;
			
L2:			for(int i=index;i>=0;i--){
				
				double mzi = peaks[i].getMz();
				double inteni = peaks[i].getIntensity();

				if(Math.abs(mzi-findMz)<mzi*ppm){
					
					if(inteni>inten){
						inten = inteni;
					}
					
				}else if(findMz-mzi>mzi*ppm){
					
					if(inten>0){
						
						findIntenList.removeLast();
						findIntenList.addFirst(inten);
						
						double lastInten = findIntenList.getLast();
						for(int j=0;j<findIntenList.size()-2;j++){
							if(lastInten>findIntenList.get(j)){
								findMz -= dm/charge;
								inten = 0;
								continue L2;
							}
						}
						
						changed = true;
						break;
						
					}else{
						break;
					}
				}
			}
		}
		
		if(changed){
			mz = findMz;
		}
*/		
		LinkedList <Double> intenList = new LinkedList <Double>();
		
		int preLen = 0;
		double [] prePeaks = new double []{mz-labelDiff/charge, mz-(labelDiff-dm)/charge};
		int preId = prePeaks.length-1;
		int preCount = 0;
		double preInten = 0;
//System.out.println("351\t"+findMz+"\t"+prePeaks[0]+"\t"+prePeaks[1]);

		for(int j=index;j>=0;j--){
			
			double mzj = peaks[j].getMz();
			double intenj = peaks[j].getIntensity();

			if(Math.abs(mzj-prePeaks[preId])<mz*ppm){
				
				preId--;
				preCount++;
				preInten += intenj;
				
				if(preId==-1){
					
					if(preCount==prePeaks.length){
						
						preLen++;
						intenList.addFirst(preInten);
						if(preLen>=5)
							break;
						
						preId = prePeaks.length-1;
						preCount = 0;
						prePeaks = new double []{prePeaks[0]-labelDiff/charge, 
								prePeaks[0]-(labelDiff-dm)/charge};
						preInten = 0;

					}else{
						break;
					}
				}
			}else if(prePeaks[0]-mzj>mz*ppm){
//System.out.println("377\t"+prePeaks[0]+"\t"+mzj+"\t"+preCount);
				break;
			}
		}

		int nexLen = 0;
		int nexId = 0;
		int nexCount = 0;
		double nexInten = 0;
		double [] nexPeaks = new double []{mz+labelDiff/charge, mz+(labelDiff+dm)/charge};
		
		for(int j=index;j<peaks.length;j++){
			
			double mzj = peaks[j].getMz();
			double intenj = peaks[j].getIntensity();

			if(Math.abs(mzj-nexPeaks[nexId])<mz*ppm){
				
				nexId++;
				nexCount++;
				nexInten += intenj;
				
				if(nexId==nexPeaks.length){
					
					if(nexCount==nexPeaks.length){
						
						nexLen++;
						if(nexLen>=5)
							break;
						
						nexId = 0;
						nexCount = 0;
						nexPeaks = new double []{nexPeaks[0]+labelDiff/charge, 
								nexPeaks[0]+(labelDiff+dm)/charge};
						
						if(preLen+nexLen>5){
							
							double pre0 = intenList.getFirst();

							if(nexInten>pre0){

								intenList.remove(pre0);
								preLen--;
								
								if(preLen==0){
									break;
								}
								
								intenList.addLast(nexInten);
								nexInten = 0;
								
							}else{
								nexLen--;
								break;
							}
							
						}else{
							
							intenList.addLast(nexInten);
							nexInten = 0;
						}

					}else{
						break;
					}
				}
				
			}else if(mzj-nexPeaks[nexPeaks.length-1]>mz*ppm){
//System.out.println("445\t"+nexPeaks[nexPeaks.length-1]+"\t"+mzj+"\t"+nexCount);
				break;
			}
		}
		
		int type1 = preLen+1;
		int type2 = 6-nexLen;
//		System.out.println(preLen+"\t"+nexLen+"\t"+type1+"\t"+type2+"\t");
		return new int[]{type1, type2};
	
	}
	
	public void write() throws DtaWritingException{
		
		String name = this.rawName.replace(".raw", "");
		
		for(int i=0;i<7;i++){
			
			File file = new File(this.parent.getAbsolutePath()+"\\"+name+"_"+(i+1)+".mgf");
			MgfWriter writer = new MgfWriter(file, null);
//			System.out.println(this.typeLists[i].size());
			
			for(int j=0;j<this.typeLists[i].size();j++){
				
				int scannum = typeLists[i].get(j);
				double preMz = this.ppMzMap.get(scannum);
				IMS2PeakList peaks = this.peakMap.get(scannum);
				
				peaks.getPrecursePeak().setMz(preMz);
				
				String title = this.getTitle(peaks.getPrecursePeak().getRT(), 
						peaks.getPrecursePeak().getIntensity(), scannum);
				
				MaxQuantScanName scanname = new MaxQuantScanName(title);
				MS2ScanDta scan = new MS2ScanDta(scanname, peaks);
				
				writer.write(scan);
			}
			System.out.print(typeLists[i].size()+"\t");
			writer.close();
		}
	}
	
	private String getTitle(double rt, double intensity, int scannum){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Elution from: ").append(rt).append(" to ").append(rt);
		sb.append(" period: 0 experiment: 1 cycles: 1 precIntensity: ").append(intensity);
		sb.append(" FinneganScanNumber: ").append(scannum);
		sb.append(" MStype: enumIsNormalMS rawFile: ").append(rawName).append(" ");
		
		return sb.toString();
	}
	
	public void dispose(){
		
		this.ms1ScanList = null;
		this.peakMap = null;
		this.ppMzMap = null;
		this.preMzMap = null;
		this.typeMap = null;
		this.typeLists = null;
		System.gc();
	}

	private static void testMgf(String [] ss, String total) throws DtaFileParsingException, FileNotFoundException{
		
//		PrintWriter pw = new PrintWriter("H:\\WFJ_mutiple_label\\2D\\150\\mass_error.txt");
		
		HashMap <Integer, Double> map = new HashMap <Integer, Double>();
		
		int in = 0;
		int out = 0;
		
		for(int i=0;i<ss.length;i++){
			
			MgfReader reader = new MgfReader(ss[i]);
			MS2Scan scan = null;
			while((scan=reader.getNextMS2Scan())!=null){
				IMS2PeakList peaklist = scan.getPeakList();
				PrecursePeak pp = peaklist.getPrecursePeak();
				map.put(scan.getScanNumInteger(), pp.getMz());
			}
			reader.close();
		}
		
		MgfReader reader = new MgfReader(total);
		MS2Scan scan = null;
		while((scan=reader.getNextMS2Scan())!=null){
			
			IMS2PeakList peaklist = scan.getPeakList();
			PrecursePeak pp = peaklist.getPrecursePeak();
			int scannum = scan.getScanNum();
			double mz = pp.getMz();
			
			if(map.containsKey(scannum)){
				double mmz = map.get(scannum);
				if(Math.abs(mmz-mz)<mz*ppm){
					in++;
				}else{
					out++;
//					pw.write(scannum+"\t"+mz+"\t"+mmz+"\n");
				}
			}
		}
		reader.close();
//		pw.close();
		
		System.out.println(in+"\t"+out);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws XMLStreamException 
	 * @throws DocumentException 
	 * @throws DtaWritingException 
	 * @throws DtaFileParsingException 
	 */
	public static void main(String[] args) throws XMLStreamException, IOException, DocumentException, DtaWritingException, DtaFileParsingException {
		// TODO Auto-generated method stub

		long startTime=System.currentTimeMillis();
		
//		MutilSpecClassifier c = new MutilSpecClassifier("H:\\WFJ_mutiple_label\\2d\\" +
//				"20120531Mix1_150mM.mzXML", "H:\\WFJ_mutiple_label\\2D\\Name it_20120531Mix1_150mM.mgf", 
//				"H:\\WFJ_mutiple_label\\2d\\150");

//		c.write();
//		c.dispose();

/*		MutilLabelPairXMLReader reader = new MutilLabelPairXMLReader("H:\\WFJ_mutiple_label\\2d" +
			"\\20120531Mix1_150mM.pxml");
		reader.readAllPairs();
		PeptidePair [] pairs = reader.getAllSelectedPairs();
		for(int i=0;i<pairs.length;i++){
			int scannum = pairs[i].getPeptide().getScanNumBeg();
			if(pairs[i].getFindFeasNum()==6){
				if(classifier.typeMap.containsKey(scannum)){
					System.out.println(scannum+"\t"+classifier.typeMap.get(scannum));
				}else{
					System.out.println(scannum+"\t0");
				}
			}
		}
		
		System.out.println(pairs.length);
*/		
		String [] ss = new String []{"H:\\WFJ_mutiple_label\\2D\\150\\20120531Mix1_150mM_1.mgf",
				"H:\\WFJ_mutiple_label\\2D\\150\\20120531Mix1_150mM_2.mgf",
				"H:\\WFJ_mutiple_label\\2D\\150\\20120531Mix1_150mM_3.mgf",
				"H:\\WFJ_mutiple_label\\2D\\150\\20120531Mix1_150mM_4.mgf",
				"H:\\WFJ_mutiple_label\\2D\\150\\20120531Mix1_150mM_5.mgf",
				"H:\\WFJ_mutiple_label\\2D\\150\\20120531Mix1_150mM_6.mgf",};
		
		String total = "H:\\WFJ_mutiple_label\\2D\\Name it_20120531Mix1_150mM.mgf";
		MutilSpecClassifier.testMgf(ss, total);
		
		long endTime=System.currentTimeMillis(); 
		System.out.println("��������ʱ�䣺 "+(endTime-startTime)/1000+"s"); 
	}

}
