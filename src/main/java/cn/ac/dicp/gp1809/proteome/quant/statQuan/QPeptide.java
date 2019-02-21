/* 
 ******************************************************************************
 * File:QPeptide.java * * * Created on 2010-8-19
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.statQuan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;

/**
 * @author ck
 *
 * <b> used for read peptide from statQuant quantitation file
 * @version 2010-8-19, 09:40:45
 */
public class QPeptide extends AbstractPeptide implements IPeptide{
	
	private double ratio;
	private String seq;
	private HashMap <String, Double> modScore;
	private HashMap <String, String> modInfo;
	private String key;
	
	public QPeptide(String sequence, HashSet<ProteinReference> refs, String scanName, String key) {
		super(sequence, refs, scanName);
		this.seq = sequence;
		this.key = key;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param sequence
	 * @param refs
	 */
	public QPeptide(String sequence, HashSet<ProteinReference> refs, 
			String scanName, HashMap <String, Double> modScore, HashMap <String, String> modInfo, double ratio, String key) {
		super(sequence, refs, scanName);
		this.ratio = ratio;
		this.modScore = modScore;
		this.modInfo = modInfo;
		this.seq = sequence;
		this.key = key;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param sequence
	 * @param refs
	 */
	public QPeptide(String sequence, HashSet<ProteinReference> refs, String scanName, double ratio, String key) {
		super(sequence, refs, scanName);
		this.ratio = ratio;
		this.seq = sequence;
		this.key = key;
		this.modScore = new HashMap <String, Double>();
		this.modInfo = new HashMap <String, String>();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getEnzyme()
	 */
	@Override
	public Enzyme getEnzyme() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPrimaryScore()
	 */
	@Override
	public float getPrimaryScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSequence(){
		return seq;
	}
	
	public void setRatio(double ratio){
		this.ratio = ratio;
	}
	
	public double getRatio(){
		return ratio;
	}
	
	public HashMap <String, Double> getModScore(){
		return modScore;
	}
	
	public HashMap <String, String> getModInfo(){
		return modInfo;
	}
	
	public void addMod(HashMap <String, Double> modScore, HashMap <String, String> newModInfo){
		
		Iterator <String> it = modScore.keySet().iterator();
		while(it.hasNext()){
			String mod = it.next();
			double score = modScore.get(mod);
			if(this.modScore.containsKey(mod)){
				double s0 = this.modScore.get(mod);
				if(score>s0)
					this.modScore.put(mod, score);
			}else{
				this.modScore.put(mod, score);
				this.modInfo.put(mod, newModInfo.get(mod));
			}
		}
	}
	
	public String getKey(){
		return key;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(seq).append("\t");
		sb.append(ratio).append("\t");
		String ref = this.getProteinReferenceString();
		sb.append(ref).append("\t");
		Iterator <String> it = modScore.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String info = modInfo.get(key);
			double score = modScore.get(key);
			sb.append(info+" "+score+"\t");
		}
		
		return sb.toString();
	}
	
	/**
	 * In class AbstractPeptide the method equals() was overloaded, so all QPeptide with same
	 * sequence will be same as equal. Overload this method can avoid this mistake.
	 */
	@Override
	public boolean equals(Object obj){
		String s1 = this.seq + ratio;
		if(obj instanceof QPeptide){
			String s2 = ((QPeptide)obj).getSequence() + ((QPeptide)obj).getRatio();
			return s1.equals(s2);
		}else
			return false;
		
	}
	
	@Override
	public int hashCode() {
		String s1 = this.seq + ratio;
		return s1.hashCode();
	}
}
