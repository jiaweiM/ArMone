/* 
 ******************************************************************************
 * File: PeptideListMutilAccesser.java * * * Created on 2011-8-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;

/**
 * @author ck
 *
 * @version 2011-8-24, 10:15:27
 */
public class PeptideListMutilAccesser extends PeptideListAccesser {

	private PeptideListAccesser [] accesser;
	private int totalPepNum;
	private HashMap <Integer, Integer> accesserIdMap;
	private HashMap <Integer, Integer> idIdxMap;

	public PeptideListMutilAccesser(){
		
	}
	
	public PeptideListMutilAccesser(File [] files) throws FileDamageException, IOException{
		
		this.accesserIdMap = new HashMap <Integer, Integer>();
		this.idIdxMap = new HashMap <Integer, Integer>();
		this.accesser = new PeptideListAccesser[files.length];
		accesser[0] = new PeptideListAccesser(files[0]);
		totalPepNum = 0;

		for(;totalPepNum<accesser[0].getNumberofPeptides();totalPepNum++){
			this.accesserIdMap.put(totalPepNum, 0);
			this.idIdxMap.put(totalPepNum, totalPepNum);
		}
		this.parameter = accesser[0].getSearchParameter();
		this.proNameAccesser = accesser[0].getProNameAccesser();
		this.type = accesser[0].getPeptideType();
		this.formatter = accesser[0].getPeptideFormat();
		this.judger = accesser[0].judger;
		
		for(int i=1;i<files.length;i++){
			accesser[i] = new PeptideListAccesser(files[i]);
			for(int j=0;j<accesser[i].getNumberofPeptides();j++){
				this.accesserIdMap.put(totalPepNum, i);
				this.idIdxMap.put(totalPepNum, j);
				totalPepNum++;
			}
			this.proNameAccesser.appand(accesser[i].getProNameAccesser());
		}
	}
	
	public IPeptide getPeptide(int idx){
		int id1 = this.accesserIdMap.get(idx);
		int id2 = this.idIdxMap.get(idx);
		return this.accesser[id1].getPeptide(id2);
	}
	
	public IMS2PeakList [] getPeakLists(int idx){
		int id1 = this.accesserIdMap.get(idx);
		int id2 = this.idIdxMap.get(idx);
		return this.accesser[id1].getPeakLists(id2);
	}
	
	public int getNumberofPeptides() {
		return this.totalPepNum;
	}
	
	public String getFileName(){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<accesser.length;i++){
			sb.append(accesser[i].getFileName()).append("\n");
		}
		return sb.substring(0, sb.length()-1);
	}
	
	public int getFileNum(){
		return accesser.length;
	}

	public void close(){
		for(int i=0;i<accesser.length;i++){
			accesser[i].close();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
