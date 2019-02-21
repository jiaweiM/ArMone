/* 
 ******************************************************************************
 * File: PotentialGlyPeptide.java * * * Created on 2012-4-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.protein;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

/**
 * @author Administrator
 *
 * @version 2012-4-13, 14:25:43
 */
public class PotentialGlyPeptide {
	
	private int id;
	private String sequence;
	private double monomass;
	private HashMap <String, int []> refmap; 
	
	public PotentialGlyPeptide(String sequence, double monomass){
		
		this.sequence = sequence;
		this.monomass = monomass;
		this.refmap = new HashMap <String, int []>();
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getSequence(){
		return sequence;
	}
	
	public double getMass(){
		return monomass;
	}

	public int [] getLocs(ProteinReference ref){
		return this.refmap.get(ref);
	}
	
	public void addRef(String ref, int [] locs){
		this.refmap.put(ref, locs);
	}
	
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.sequence).append("\t");
		sb.append(this.monomass).append("\t");
		
		Iterator <String> it = this.refmap.keySet().iterator();
		while(it.hasNext()){
			
			String ref = it.next();
			int [] locs = refmap.get(ref);
			sb.append(ref).append("\t");
			for(int i=0;i<locs.length;i++){
				sb.append(locs[i]).append("|");
			}
			
			sb.deleteCharAt(sb.length()-1);
			sb.append("\t");
		}
		
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
