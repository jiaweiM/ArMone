/* 
 ******************************************************************************
 * File: FreeFeasGetter.java * * * Created on 2011-6-29
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.quant.profile.MS1PixelGetter;
import cn.ac.dicp.gp1809.proteome.quant.profile.Pixel;

/**
 * @author ck
 *
 * @version 2011-6-29, 14:14:43
 */
public class FreeFeasGetter {

	private MS1PixelGetter pGetter;
	private int leastIdenNum;
	
	private HashMap <String, IPeptide> pepMap;
	private HashMap <String, IPeptide> uniSeqMap;
	private HashMap <String, Integer> countMap;
	
	public FreeFeasGetter(String peakFile){
		try {
			this.pGetter = new MS1PixelGetter(peakFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.leastIdenNum = pGetter.getLeastIdenNum();
		this.pepMap = new HashMap <String,IPeptide> ();
		this.uniSeqMap = new HashMap <String, IPeptide> ();
		this.countMap = new HashMap <String, Integer> ();
	}
	
	public FreeFeasGetter(String peakFile, int leastIdenNum){
		try {
			this.pGetter = new MS1PixelGetter(peakFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.leastIdenNum = leastIdenNum;
		this.pepMap = new HashMap <String,IPeptide> ();
		this.uniSeqMap = new HashMap <String, IPeptide> ();
		this.countMap = new HashMap <String, Integer> ();
	}
	
	public void addPeptide(IPeptide peptide){
		
		if(!peptide.isTP())
			return;

		String sequence = peptide.getSequence();
		String uSeq = PeptideUtil.getSequence(sequence);
		String key = uSeq+"_"+peptide.getCharge();
		
		char [] cs = uSeq.toCharArray();
		Arrays.sort(cs);
		String key2 = new String (cs)+"_"+peptide.getCharge();
		
		if(uniSeqMap.containsKey(key2)){
			
			IPeptide p1 = uniSeqMap.get(key2);
			double inten0 = peptide.getInten();
			double inten1 = p1.getInten();
			if(inten1 < inten0){
				uniSeqMap.put(key2, peptide);
			}

			if(pepMap.containsKey(key)){

				int count = countMap.get(key);
				countMap.put(key, count+1);
				
			}else{

				pepMap.put(key, peptide);
				countMap.put(key, 1);
			}

		}else{

			pepMap.put(key, peptide);
			uniSeqMap.put(key2, peptide);
			countMap.put(key, 1);
		}
	}
	
	public HashMap <String, FreeFeatures> getFeatures(){

		HashMap <String, FreeFeatures> feasMap = new HashMap <String, FreeFeatures> ();
		Iterator <String> it = pepMap.keySet().iterator();

		while(it.hasNext()){

			String key = it.next();
			
			if(countMap.get(key)<leastIdenNum)
				continue;
			
			IPeptide pep = pepMap.get(key);
			String [] ss = key.split("_");
			char [] aas = ss[0].toCharArray();
			Arrays.sort(aas);
			String key2 = new String (aas)+"_"+ss[1];

			int scanNum = uniSeqMap.get(key2).getScanNumBeg();
			short charge = pep.getCharge();
			double mz = (pep.getMH()+(double)(charge-1)*AminoAcidProperty.PROTON_W)/(double)charge;
			
			Pixel p = new Pixel(scanNum, mz, 0f);
			p.setCharge(charge);
			FreeFeatures fs = pGetter.getFreeFeatures(p);
			if(fs!=null){
				feasMap.put(key, fs);
			}
		}
		return feasMap;
	
	}
	
	public HashMap <String, IPeptide> getPepMap(){
		return pepMap;
	}
	
	public double getMS1TotalCurrent(){
		return this.pGetter.getMS1TotalCurrent();
	}
	
	public void close(){
		this.pepMap = null;
		this.uniSeqMap = null;
		this.countMap = null;
		this.pGetter.close();
	}
}
