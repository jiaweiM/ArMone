/* 
 ******************************************************************************
 * File: PixelGetter2.java * * * Created on 2011-1-11
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelQParameter;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeature;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2011-1-11, 16:08:04
 */
public class MS1PixelGetter {

	/**
	 * nbt.1511-S1, p9
	 */
	private final static double dm = 1.00286864;
	private double ppm = 0f;
	private int missNum = 0;
	private int leastINum = 0;

	private double ms1TotalCurrent;
	private MS1ScanList scanlist;
	
	public MS1PixelGetter(String file) throws IOException, XMLStreamException{
		this(file, LabelQParameter.default_parameter());
	}
	
	public MS1PixelGetter(String file, LabelQParameter parameter) throws IOException, XMLStreamException{
		
		this(new File(file), parameter);
	}
	
	public MS1PixelGetter(File file) throws IOException, XMLStreamException{
		this(file, LabelQParameter.default_parameter());
	}
	
	public MS1PixelGetter(File file, LabelQParameter parameter) throws IOException, XMLStreamException{
		
		this.createReader(file);

		this.ppm = parameter.getMzTole()/1000000f;
		this.missNum = parameter.getMissNum();
		this.leastINum = parameter.getLeastINum();

	}
	
	public MS1PixelGetter(MS1ScanList scanlist, double ms1TotalCurrent){
		
		this(scanlist, ms1TotalCurrent, LabelQParameter.default_parameter());
	}
	
	public MS1PixelGetter(MS1ScanList scanlist, double ms1TotalCurrent, LabelQParameter parameter){
		
		this.scanlist = scanlist;
		this.ms1TotalCurrent = ms1TotalCurrent;
		
		this.ppm = parameter.getMzTole()/1000000f;
		this.missNum = parameter.getMissNum();
		this.leastINum = parameter.getLeastINum();
	}
	
	protected void createReader(File file) throws IOException, XMLStreamException{
		
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

	/**
	 * Find the monoisotope peak.
	 * @param scanNum
	 * @param preMz
	 * @param charge
	 * @return
	 */
	public double findMono(int scanNum, double preMz, short charge){

		IMS1Scan scan = this.scanlist.getScan(scanNum);
		IPeak findpeak = new Peak(preMz, 0d);
		
		IPeak [] peaks = scan.getPeakList().getPeakArray();
		double tola = preMz * ppm;
		
		int index = Arrays.binarySearch(peaks, findpeak);
		
		if(index<0){
			
			index = -index-1;
			
			double d1 = peaks[index].getMz()-preMz;
			double d2 = preMz - peaks[index-1].getMz();
			
			index = d1<d2 ? index : index -1;
			
		}else if(index >= peaks.length-1){
			index = peaks.length-1;
		}

		int loc = findPreIsotope(charge, peaks, index, tola);

		return peaks[loc].getMz();
	}
	
	private int findPreIsotope(short charge, IPeak [] peaks, int loc, double tola){
		
		double premz = peaks[loc].getMz() - dm/(double)charge;
		double preInten = peaks[loc].getIntensity();
		
		for(int i=loc-1;i>=0;i--){
			
			double listmz = peaks[i].getMz();
			double listInten = peaks[i].getIntensity();

			if(Math.abs(listmz-premz)<=tola){
				if((listInten/preInten)>=0.3){
					
//					if((listInten/peaks[i].getIntensity())>=1.1){
//						return loc;
//					}
					
					loc = i;
					return findPreIsotope(charge, peaks, loc, tola);
				}
			}else if((premz-listmz)>tola){
				return loc;
			}
		}
		return loc;
	}

	public double findMono2(int scanNum, double preMz, short charge){
		
		PixelList list0 = this.getPixelList(new Pixel(scanNum, preMz));
		preMz = list0.getAveMz();
		int length = list0.getLength();
		int limit = (length/2) >3 ? length/2 : 3;

		double finMz = preMz - dm/(double)charge;
		Pixel pix = new Pixel(scanNum, finMz);
		PixelList list = this.getIsotopePixelList(pix);

		if(list.getLength()<limit || list.getInten()*3<list0.getInten())
			return preMz;
		else
			return findMono2(scanNum, finMz, charge);
	}
	
	public double findMono2(int scanNum, PixelList list0, short charge){
		
		double preMz = list0.getAveMz();
		int length = list0.getLength();
		int limit = (length/2) >3 ? length/2 : 3;

		double finMz = preMz - dm/(double)charge;
		Pixel pix = new Pixel(scanNum, finMz);
		PixelList list = this.getIsotopePixelList(pix);

		if(list.getLength()<=limit || list0.intenCompare(list)>3 || list.intenCompare(list0)>1.2){
			
			return preMz;
			
		}else{
			return findMono2(scanNum, list, charge);
		}
	}
	
	public Pixel getPixel(int scannum, double mz){
		IMS1Scan scan = this.scanlist.getScan(scannum);
		return getPixel(scan, mz);
	}
	
	public Pixel getPixel(IMS1Scan scan, double mz){
		
		int scanNum = scan.getScanNum();
		IPeak [] peaks = scan.getPeakList().getPeakArray();
		int peakCount = peaks.length;
		double tola = mz * ppm;
		
		IPeak findpeak = new Peak(mz-tola, 0d);

		int index = Arrays.binarySearch(peaks, findpeak);
		if(index<0){
			index = -index-1;
		}else if(index >= peaks.length){
			return new Pixel(scanNum, mz, 0);
		}
//System.out.println("mp392\t"+scan.getScanNum()+"\t"+mz+"\t"+peaks[index].getMz()+"\t"+peaks[index-1].getMz());		

		Pixel pix = new Pixel(scanNum, mz, 0);
		double inten1 = 0;
		
		for(int i=index;i<peakCount;i++){
			double mass = peaks[i].getMz();
			double inten = peaks[i].getIntensity();
			if(Math.abs(mz-mass)<=tola){

				if(inten>inten1){
					
					pix = new Pixel(scanNum, mass, inten);
					inten1 = inten;
				}
				
			}else if(mass-mz>tola)
				break;
		}
		
		return pix;
	}

	public Pixel getPixel(IMS1Scan scan, double mz, double ppm){
		
		int scanNum = scan.getScanNum();
		IPeak [] peaks = scan.getPeakList().getPeakArray();
		int peakCount = peaks.length;
		double tola = mz * ppm * 1E-6;
		
		IPeak findpeak = new Peak(mz-tola, 0d);

		int index = Arrays.binarySearch(peaks, findpeak);
		if(index<0){
			index = -index-1;
		}else if(index >= peaks.length){
			return new Pixel(scanNum, mz, 0);
		}
//System.out.println("mp392\t"+scan.getScanNum()+"\t"+mz+"\t"+peaks[index].getMz()+"\t"+peaks[index-1].getMz());		
		
		Pixel pix = new Pixel(scanNum, mz, 0);
		double inten1 = 0;
		
		for(int i=index;i<peakCount;i++){
			double mass = peaks[i].getMz();
			double inten = peaks[i].getIntensity();
			if(Math.abs(mz-mass)<=tola){

				if(inten>inten1){
					
					pix = new Pixel(scanNum, mass, inten);
					inten1 = inten;
				}
				
			}else if(mass-mz>tola)
				break;
		}
		
		return pix;
	}
	
	public PixelList getIsotopePixelList(Pixel pix) {
		
		int scanNum = pix.getScanNum();
		PixelList pixList = new PixelList(pix);
		
		double mz = pix.getMz();
		
		IMS1Scan next;
		int nextscan = scanNum;
		while((next=scanlist.getNextScan(nextscan))!=null){
			
			nextscan = next.getScanNum();
			Pixel nexPix = getPixel(next, mz, 10);
			
			if(nexPix!=null){

				pixList.addPixel(nexPix);

			}else{
				break;
			}
		}

		IMS1Scan prev;
		int prevScan = scanNum;
		while((prev=scanlist.getPreviousScan(prevScan))!=null){
			
			prevScan = prev.getScanNum();
			Pixel prePix = getPixel(prev, mz, 10);

			if(prePix!=null){

				pixList.addPixel(prePix);
				
			}else{
				break;
			}
		}

		return pixList;
	}
	
	public PixelList getPixelList(Pixel pix) {
		
		int scanNum = pix.getScanNum();
		double mz = pix.getMz();
		
		PixelList pixList;
		boolean miss1;
		boolean miss2;
		int missCount1;
		int missCount2;
		
		if(pix.getInten()>0){
			
			pixList = new PixelList(pix);
			miss1 = false;
			miss2 = false;
			missCount1 = 0;
			missCount2 = 0;
			
		}else{
			
			pixList = new PixelList();
			miss1 = true;
			miss2 = true;
			missCount1 = 1;
			missCount2 = 1;
		}

		IMS1Scan next;
		int nextscan = scanNum;
		while((next=scanlist.getNextScan(nextscan))!=null){
			
			nextscan = next.getScanNum();
			Pixel nexPix = getPixel(next, mz);
			
			if(nexPix!=null){

				pixList.addPixel(nexPix);

				if(miss1){
					missCount1 = 0;
					miss1=false;
				}
			}else{
				if(miss1)
					missCount1++;
				else
					miss1=true;
			}
			if(missCount1>=2)
				break;
		}

		IMS1Scan prev;
		int prevScan = scanNum;
		while((prev=scanlist.getPreviousScan(prevScan))!=null){
			
			prevScan = prev.getScanNum();
			Pixel prePix = getPixel(prev, mz);

			if(prePix!=null){

				pixList.addPixel(prePix);
				if(miss2){
					missCount2 = 0;
					miss2=false;
				}
			}else{
				if(miss2)
					missCount2++;
				else
					miss2=true;
			}
			if(missCount2>=2)
				break;
		}

		return pixList;
	}
	
	public FreeFeature getFeature(Pixel pix, int scanNum){
		return this.getFreeFeature(pix, scanlist.getScan(scanNum));
	}
	
	public FreeFeature getFreeFeature(Pixel pix, IMS1Scan scan) {

		IPeak [] peaks = scan.getPeakList().getPeakArray();
		int peakCount = peaks.length;
		int value = pix.getCharge();
		
		double rt = (double) scan.getRTMinute();
		
		double mz1 = pix.getMz();
		double mz2 = mz1 + dm/(double)value;
		double mz3 = mz2 + dm/(double)value;

		double tola = mz1*ppm;
		Pixel pix1 = null;
		Pixel pix2 = null;
		Pixel pix3 = null;

		double inten1 = 0;
		double inten2 = 0;
		double inten3 = 0;

		int scanNum = scan.getScanNum();
		IPeak findpeak = new Peak(mz1-tola ,0d);

		int index = Arrays.binarySearch(peaks, findpeak);
		if(index<0){
			index = -index-1;
		}else if(index >= peaks.length){
			return null;
		}

		for(int i=index;i<peakCount;i++){
			double mass = peaks[i].getMz();
			double inten = peaks[i].getIntensity();
			if(Math.abs(mz1-mass)<tola){
				
//				System.out.println("1\t"+scanNum+"\t"+mass);
				
				if(inten>inten1){
					pix1 = new Pixel(scanNum, mass, inten);
					inten1 = inten;
				}
			}else if(Math.abs(mz2-mass)<tola){

//				System.out.println("2\t"+scanNum+"\t"+mass);
				
				if(inten>inten2){
					pix2 = new Pixel(scanNum, mass, inten);
					inten2 = inten;
				}
			}else if(Math.abs(mz3-mass)<tola){
				
//				System.out.println("3\t"+scanNum+"\t"+mass);
				
				if(inten>inten3){
					pix3 = new Pixel(scanNum, mass, inten);
					inten3 = inten;
				}
			}
			else if(mass-mz3>0.1)
				break;
		}

		if(pix1==null || pix2==null || pix3==null){
			
			return null;
			
		}else{
			
			double d1 = pix2.getMz()-pix1.getMz();
			double d2 = pix3.getMz()-pix2.getMz();
			if(Math.abs(d1-d2)>tola)
				return null;
			
			double totalInten = inten1+inten2+inten3;
			FreeFeature f = new FreeFeature(scanNum, value, mz1, totalInten);
			f.setRT(rt);
			f.validate();
			
			return f;
		}		
	}

	/**
	 * @param p
	 * @return
	 */
	public FreeFeatures getFreeFeatures(Pixel pix) {
		// TODO Auto-generated method stub
		
		int scanNum = pix.getScanNum();

		while(true){
			if(this.scanlist.getScan(scanNum)!=null){
				break;
			}else{
				scanNum--;
			}
		}

		FreeFeature f = getFeature(pix, scanNum);
		FreeFeatures fs;
		boolean miss1 = false;
		boolean miss2 = false;
		int missCount1 = 0;
		int missCount2 = 0;
		if(f==null){
			fs = new FreeFeatures();
			miss1 = true;
			miss2 = true;
			missCount1 = 1;
			missCount2 = 1;
		}else{
			fs = new FreeFeatures(f);
//		System.out.println("255\t"+f);			
		}
		
		int down1 = 0;
		ArrayList <Double> tailInten1 = new ArrayList <Double>();

		IMS1Scan next;
		int nextscan = scanNum;
		while((next=scanlist.getNextScan(nextscan))!=null){
			nextscan = next.getScanNum();
			FreeFeature fi = getFreeFeature(pix, next);
			if(fi!=null){
//			System.out.println("267\t"+fi);					
				// remove the tail
				
				if(tailInten1.size()==5){
					
					double oldInten = MathTool.getAveInDouble(tailInten1);
					tailInten1.remove(0);
					tailInten1.add(fi.getIntensity());
					double newInten = MathTool.getAveInDouble(tailInten1);
					
					if(newInten<oldInten*1.1){
						down1++;
					}
					if(down1>=10)
						break;
					
				}else{
					tailInten1.add(fi.getIntensity());
				}

				fs.addFeature(fi);
				if(miss1){
					missCount1 = 0;
					miss1=false;
				}
			}else{
				if(miss1)
					missCount1++;
				else
					miss1=true;
			}
			if(missCount1>=missNum)
				break;
		}

		IMS1Scan prev;
		int prevScan = scanNum;
		while((prev=scanlist.getPreviousScan(prevScan))!=null){
			
			prevScan = prev.getScanNum();
			FreeFeature fi = getFreeFeature(pix, prev);
			if(fi!=null){
//				System.out.println("309\t"+fi);			
				fs.addFeature(fi);
				if(miss2){
					missCount2 = 0;
					miss2=false;
				}
			}else{
				if(miss2)
					missCount2++;
				else
					miss2=true;
			}
			if(missCount2>=missNum)
				break;
		}

		if(fs.getLength()>0){

			fs.setInfo();
			
		}else{
			
			double mono = pix.getMz();
			double value = pix.getCharge();
			double mr = (mono-AminoAcidProperty.PROTON_W)*value;
			
			fs.setPepMass(mr);
		}
		
		return fs;
	}
	
	public void setPPM(double ppm){
		this.ppm = ppm;
	}
	
	public void setLeastIdenNum(int num){
		this.leastINum = num;
	}
	
	public int getLeastIdenNum(){
		return this.leastINum;
	}
	
	public double getMS1TotalCurrent(){
		return ms1TotalCurrent;
	}
	
	public void close(){
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

		long startTime=System.currentTimeMillis();
		String file = "I:\\glyco\\SILAC" +
		"\\20111123_HILIC_SILAC_HCD_111123230330.mzXML";
		
		MS1PixelGetter getter = new MS1PixelGetter(file);
		Pixel pix = new Pixel(2998, 1011.4222);
		PixelList list = getter.getPixelList(pix);
		
		double mz = getter.findMono2(2998, 1011.4222, (short) 3);
		System.out.println(mz);
		
//		Pixel p1 = new Pixel(10855, 770.9779, 0);
//		p1.setCharge(2);
//		Feature f = getter.getSeedFea(p1, 8383);
//		System.out.println(f);
//		Features fs = getter.getFeatures(p1);
//		System.out.println(fs.getAllFeatureInfo());
		
		long endTime=System.currentTimeMillis(); 
		System.out.println("��������ʱ�䣺 "+(endTime-startTime)+"ms");   
	}

	

}
