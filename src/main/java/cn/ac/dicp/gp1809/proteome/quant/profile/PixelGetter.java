/* 
 ******************************************************************************
 * File:PixelGetter.java * * * Created on 2010-4-16
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

import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.MSXMLParser;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.ScanHeader;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * Get pixel list from mzXML file.
 * @author ck
 *
 * @version 2010-4-16, 10:08:14
 */
public class PixelGetter {

	private String xmlFile;
	private MSXMLParser msParser;

	public static final DecimalFormat df4 = DecimalFormats.DF0_4;
	public static final DecimalFormat df5 = DecimalFormats.DF0_5;

	public PixelGetter(String file){
		this.xmlFile = file;
		msParser = new MSXMLParser(xmlFile);
	}
	
	public Scan getScan(int scanNum){
		Scan oneScan = msParser.rap(scanNum);		
		if(oneScan==null)
			return null;
		else
			return oneScan;
	}
	
	/**
	 * Get pixel list from the mzXML file and for every scan get the corresponding 
	 * feature list .
	 * 
	 * @param length
	 * @param thres
	 * parameters used in MathTool.minusBG();
	 * 
	 * @return ArrayList contain all the feature in the run.
	 */
//	public abstract ArrayList <Feature> getFeaList(int length, double thres) throws IOException;

	/**
	 * Get pixel list from the mzXML file and for every scan get the corresponding 
	 * feature list. Corresponding to previous getFeaList() method, this method did not use 
	 * any denoising process.
	 * 
	 * @return ArrayList contain all the feature in the run.
	 */
//	public abstract ArrayList <Feature> getFeaList() throws IOException;

	/**
	 * Get all pixels in this scan not use any denoising method.
	 * 
	 * @param scanNum
	 * @return Pixel []
	 */
	public Pixel [] getPixList(int scanNum){
		
		Scan oneScan = getScan(scanNum);
		if(oneScan==null)
			return null;
		
		ScanHeader theHeader = oneScan.getHeader();
		int peakCount = theHeader.getPeaksCount();

		double rt = Float.parseFloat(df5.format(theHeader.getDoubleRetentionTime()));
		double [][] mzIntenList = oneScan.getMassIntensityList();
		Pixel [] pixels = new Pixel[peakCount];
		
		for(int i=0;i<pixels.length;i++){
			double mass = Float.parseFloat(df4.format(mzIntenList[0][i]));
			double inten = Float.parseFloat(df4.format(mzIntenList[1][i]));
			pixels[i]= new Pixel(scanNum,mass,rt,inten);
		}	

		return pixels;
	}
	
	public Pixel [] getPixList(int scanNum, double [][] mzIntenList){
		int peakCount = mzIntenList[0].length;
		Pixel [] pixels = new Pixel[peakCount];
		for(int i=0;i<pixels.length;i++){
			double mass = Float.parseFloat(df4.format(mzIntenList[0][i]));
			double inten = Float.parseFloat(df4.format(mzIntenList[1][i]));
			pixels[i]= new Pixel(scanNum,mass,inten);
		}
		return pixels;
	}
	
	public Pixel [] getPixList(int scanNum, double [][] mzIntenList, double mzLowLimit){
		int peakCount = mzIntenList[0].length;
		ArrayList <Pixel> pixList = new ArrayList <Pixel>();
		for(int i=0;i<peakCount;i++){
			double mass = Float.parseFloat(df4.format(mzIntenList[0][i]));
			double inten = Float.parseFloat(df4.format(mzIntenList[1][i]));
			if(mass>mzLowLimit)
				pixList.add(new Pixel(scanNum,mass,inten));
		}
		Pixel [] pixels = pixList.toArray(new Pixel[pixList.size()]);
		return pixels;
	}
	
	public int getMSLevel(int scanNum){
		Scan oneScan = getScan(scanNum);
		if(oneScan==null){
			return 0;
		}else{
			return oneScan.getHeader().getMsLevel();
		}
	}

	public double [][] getPeakList(int scanNum){
		Scan oneScan = getScan(scanNum);

		double [][] mzIntenList = oneScan.getMassIntensityList();
		double [][] floatList = new double [mzIntenList.length][];
		for(int i=0;i<mzIntenList.length;i++){
			floatList[i] = new double[mzIntenList[i].length];
			for(int j=0;j<mzIntenList[i].length;j++){
				floatList[i][j] = (double) mzIntenList[i][j];
			}
		}
		return floatList;
	}
	
	/**
	 * Use method MathTool.minusBG() to filter the pixel data. If one scan has many peaks,
	 * e.g about 10000 peaks, using all the data set to get feature will be very slow.
	 * 
	 * @param scanNum
	 * @return
	 */
	public Pixel [] getPixListDf(int scanNum){
		
		Scan oneScan = getScan(scanNum);
		if(oneScan==null)
			return null;
		
		ScanHeader theHeader = oneScan.getHeader();
		int level = theHeader.getMsLevel();
		int peakCount = theHeader.getPeaksCount();
		Pixel [] pixels = new Pixel[peakCount];
		
		if(level==1){
			double rt = Float.parseFloat(df5.format(theHeader.getDoubleRetentionTime()));
			double [][] mzIntenList = oneScan.getMassIntensityList();
			Double [] Inten = new Double[peakCount];
			ArrayList <Pixel> pixList = new ArrayList<Pixel>();

			for(int i=0;i<peakCount;i++){
				double mass = Float.parseFloat(df4.format(mzIntenList[0][i]));
				double inten = Float.parseFloat(df4.format(mzIntenList[1][i]));
				pixels[i]= new Pixel(scanNum,mass,rt,inten);
				Inten[i]=Double.parseDouble(df4.format(inten));
				pixels[i].setMbgInten(inten);
			}	
			double [] dnList = MathTool.minusBG(Inten, 5, 2.5);
			for(int k=0;k<peakCount;k++){
				if(dnList[k]>0){
//					double coe = 1-1/(Math.exp(dnList[k]));
//					pixels[k].setMbgInten(pixels[k].getInten()*coe);
					pixels[k].setMbgInten((double) dnList[k]);
					pixList.add(pixels[k]);
				}
//				System.out.println(pixels[k].getMz()+"\t"+Inten[k]+"\t"+dnList[k]+"\t"+pixels[k].getMbgInten());					
			}
			Pixel [] pixArrays = pixList.toArray(new Pixel[pixList.size()]);
			return pixArrays;

		}else{
//			System.out.println(theHeader.getPrecursorMz()+"\t"+theHeader.getPrecursorIntensity());
			return null;
		}	
	}
	
	
	
	public int getMaxScan(){
		return msParser.getMaxScanNumber();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, Exception{
		// TODO Auto-generated method stub

		long startTime=System.currentTimeMillis();
		String file = "E:\\Data\\best_label" +
		"\\RP_50mM.mzXML";
		PixelGetter getter = new PixelGetter(file);

		Pixel p1 = new Pixel(2763,401.2601f,0);
		p1.setCharge(2);

/*		
		String file1 = "C:\\Inetpub\\wwwroot\\ISB\\data" +
		"\\80426_cartilage_1_50mM.mzXML";
		PixelGetter getter = new PixelGetter(file,2);
		
		ArrayList <Feature> f9868  = getter.getAllFeature(getter.getPixListDf(9868));
		
		ArrayList <Feature> f9879 = getter.getAllFeature(getter.getPixListDf(9879));
		HashSet <Feature> used = new HashSet<Feature>();
		int num = 0;
		
		for(int i=0;i<f9868.size();i++){
			Feature f1 = f9868.get(i);
			for(int j=0;j<f9879.size();j++){
				Feature f2  = f9879.get(j);
				if(used.contains(f2))
					continue;
				
				if(Math.abs(f1.getMono()-f2.getMono())<0.005){
					if(f1.getMonoNum()>3)
						num++;
					used.add(f2);
				}
			}
		}
		System.out.println(num);
*/		
//		getter.getFeaList(2,2.5);
//		getter.getAllFeature(getter.getPixList(1));
		long endTime=System.currentTimeMillis(); 
		System.out.println("��������ʱ�䣺 "+(endTime-startTime)+"ms");   
	}

}
