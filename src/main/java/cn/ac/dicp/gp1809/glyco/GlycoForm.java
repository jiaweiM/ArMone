/* 
 ******************************************************************************
 * File: GlycoForm.java * * * Created on 2011-6-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco;

/**
 * @author ck
 *
 * @version 2011-6-23, 09:27:49
 */
public class GlycoForm implements Comparable <GlycoForm> {

	protected int [] comp;
	// delta mass
	protected double dm;
	protected float score;
	
	public GlycoForm(int [] comp, double dm){
		this.comp = comp;
		this.dm = dm;
	}
	
	public GlycoForm(int [] comp, double dm, float score){
		this.comp = comp;
		this.dm = dm;
		this.score = score;
	}
	
	public int [] getComposition(){
		return comp;
	}
	
	public double getDeltaMass(){
		return dm;
	}

	public float getScore(){
		return score;
	}
	
	public void setScore(float score){
		this.score = score;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GlycoForm o) {
		// TODO Auto-generated method stub
		double dm0 = Math.abs(this.dm);
		double dm1 = Math.abs(o.dm);
		if(dm0<dm1){
			return 1;
		}else if(dm0>dm1){
			return -1;
		}else
			return 0;
	}
	
	public String getStrComp(){
		
		StringBuilder sb = new StringBuilder();
		
		for(int i=0;i<comp.length;i++){
			sb.append(comp[i]).append("_");
		}
		
		return sb.toString();
	}
	
	public String getCompDes(){
		return "";
	}
	
	
}
