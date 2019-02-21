/* 
 ******************************************************************************
 * File: QuanFeatureGetter4Profile.java * * * Created on 2013-4-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.IO;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelQParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * @author ck
 *
 * @version 2013-4-16, 16:23:00
 */
public class QuanFeatureGetter4Profile {
	
	private double ppm = 0f;
	private int leastINum = 0;
	private MzXMLReader reader;
	
	public QuanFeatureGetter4Profile(String file){
		this(file, LabelQParameter.default_parameter());
	}
	
	public QuanFeatureGetter4Profile(String file, LabelQParameter parameter){
		this(new File(file), parameter);
	}
	
	public QuanFeatureGetter4Profile(File file){
		this(file, LabelQParameter.default_parameter());
	}
	
	public QuanFeatureGetter4Profile(File file, LabelQParameter parameter){
		this.ppm = parameter.getMzTole()/1E6f;
		this.leastINum = parameter.getLeastINum();
	}
	
	private void createReader(File file) throws IOException, XMLStreamException {
		// TODO Auto-generated method stub
		
		this.reader = new MzXMLReader(file);
		
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

	public void addPeptide(IPeptide peptide, int charge, double [] monoMasses, Integer [] scans, 
			double [] idenRtList, double [] scores, double [] intenMinusRatio){

		if(scans.length>1){

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
			}
		}

		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
