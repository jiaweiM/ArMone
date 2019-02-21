/*
 * *****************************************************************************
 * File: ProCompare.java * * * Created on 12-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Protein contains compare information
 * 
 * @author Xinning
 * @version 0.1, 12-08-2008, 13:32:34
 */
public class ProCompare implements Comparable<ProCompare>{
	
	private static DecimalFormat df = new DecimalFormat("#.###");
	
	private boolean isstatispep;
	//Name of the protein
	private String refname;
	
	private double mw;
	private double pi;
	
	//index of this pro, for output;
	private int idx;
	
	//The number of replicate in this experiment
	private int replicateNum;
	
	//The spectrum count of this pro statistic.
	private double[] spcount;
	
	private double[] RSD;
	
	private int[] upepcount;
	
	private Map<String,PepCompare> pepMap;
	
	private int num;
	
	/**
	 * Create a pro compare instance without computation of pep replicate information
	 * @param how many replicates in this experiment.
	 */
	public ProCompare(int replicateNum){
		this(replicateNum, false);
	}
	
	/**
	 * Create a pro statistic instance with or without computation 
	 * of pep replicate information
	 * 
	 * @param how many replicates in this experiment.
	 * @param isstatispep statistic peptide information ?
	 */
	public ProCompare(int replicateNum, boolean isstatispep){
		this.replicateNum = replicateNum;
		this.spcount = new double[replicateNum];
		//set default value to 0.1
		for(int i=0;i<replicateNum;i++)
//			this.spcount[i] = 0.1d;
			this.spcount[i] = 0d;
		
		this.RSD = new double[replicateNum];
		this.upepcount = new int[replicateNum];
		
		this.isstatispep = isstatispep;
		
		this.pepMap = new HashMap<String,PepCompare>();
	}
	
	/**
	 * Set a same protein as this pro.
	 * The added protein commonly comes from different replicate.
	 * 
	 * @param protein
	 * @param curtReplicate the current replicate number.
	 */
	public void set(ProStatistic protein, int curtReplicate){
		
		if(curtReplicate>=this.replicateNum)
			throw new RuntimeException("");
		
		//The first time 
		if(this.refname == null){
			this.refname = protein.getRefname();
			this.mw = protein.getMw();
			this.pi = protein.getPi();
		}
		
		this.spcount[curtReplicate] = protein.getCount();
		this.RSD[curtReplicate] = protein.getCV();
		this.upepcount[curtReplicate] = protein.getUniquePepCount();
		
		
		this.setPep(protein,curtReplicate);
		if(spcount[curtReplicate]>0)
			num++;
	}
	
	/**
	 * Get the peptide map containing all information in this protein
	 */
	public Map<String,PepCompare> getPepMap(){
		return this.pepMap;
	}
	
	/**
	 * The number of unique peptide in this protein identification.
	 * While for all the replicates in experiment, the number of unique peptide
	 * in this prostats identification is that for protein identification in all
	 * replicates.
	 * e.g. in rep 1, for protein 1 there are 2 peptide , 1 and 2, for identification,
	 * and in rep 2, there are 3 pep for the same protein identification, peptide 1, 3, and 4,
	 * thus, the number of unique peptide for prostats identification is 4 (1,2,3 and 4).
	 */
	public int getUniquePepCount(){
		return pepMap.size();
	}
	
	public void setIndex(int idx){
		this.idx = idx;
	}
	
	private String getInfor(){
		StringBuilder  sb = new StringBuilder(500);
		
		for(int i=0;i<this.replicateNum;i++){
			sb.append( df.format(this.spcount[i]));
			sb.append("\t");
			sb.append( df.format(this.RSD[i]));
			sb.append("\t");
			sb.append( this.upepcount[i]);
			sb.append("\t");
		}
		
		sb.append(this.getUniquePepCount());
		sb.append("\t");
		sb.append(this.refname);
		sb.append("\t");
		sb.append(df.format(this.mw));
		sb.append("\t");
		sb.append(df.format(this.pi));
		sb.append("\r\n");
		
		if(this.isstatispep){
			PepCompare[] peps = this.pepMap.values().toArray(new PepCompare[0]);
			//sort??
			
			for(int i=0;i<peps.length;i++){
				sb.append(peps[i].toString());
				sb.append("\r\n");
			}
		}

		return sb.toString();
	}
	
	public String getRef(){
		return refname;
	}
	
	@Override
	public String toString(){
		return "$$-"+this.idx+"\t"+getInfor();
	}
	
	
	
	
	/*
	 * Add peptide information to statistic.
	 */
	private void setPep(ProStatistic protein, int curtReplicate){
		for(Iterator<PepStatistic> iterator = protein.getPepMap().values().iterator();
				iterator.hasNext();){
			PepStatistic upep = iterator.next();
			String useq = upep.sequence;
			PepCompare pep = null;
			if((pep=pepMap.get(useq))!=null){
				pep.set(upep, curtReplicate);
			}
			else{
				pep = new PepCompare(this.replicateNum);
				pep.set(upep, curtReplicate);
				pepMap.put(useq, pep);
			}
		}
	}
/*
	public int compareTo(ProCompare o) {
		int u1 = this.getUniquePepCount();
		int u2 = o.getUniquePepCount();
		if(u1 > u2)
			return -1;
		else
			return u1 == u2 ? 0 : 1;
	}
*/
	public int compareTo(ProCompare o) {
		int n0 = this.num;
		int n1 = o.num;
		if(n0>n1)
			return -1;
		else if(n0<n1)
			return 1;
		else{
			int u1 = this.getUniquePepCount();
			int u2 = o.getUniquePepCount();
			if(u1 > u2)
				return -1;
			else
				return u1 == u2 ? 0 : 1;
		}
	}
	
	
}
