/* 
 ******************************************************************************
 * File: GlycoAntenna.java * * * Created on 2011-6-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco;

/**
 * See "Glycoprotein Structure Determination by Mass Spectrometry, Science 291, 2351 (2001)"
 * 
 * @author ck
 *
 * @version 2011-6-1, 15:56:31
 */
public class GlycoAntenna implements Comparable <GlycoAntenna> {

	private int [] comp;
	private double monoMass;
	
	public GlycoAntenna(String compstr){
		this.comp = parseComp(compstr);
		double mono = 0;
		double [] unit = NGlycoCompose.units;
		for(int i=0;i<unit.length;i++){
			mono += (double) comp[i] * unit[i];
		}
		this.monoMass = mono;
	}
	
	public GlycoAntenna(String compstr, double monoMass){
		this.comp = parseComp(compstr);
		this.monoMass = monoMass;
	}
	
	public GlycoAntenna(int [] comp){
		this.comp = comp;
		double mono = 0;
		double [] unit = NGlycoCompose.units;
		for(int i=0;i<unit.length;i++){
			mono += (double) comp[i] * unit[i];
		}
		this.monoMass = mono;
	}
	
	public GlycoAntenna(int [] comp, double monoMass){
		this.comp = comp;
		this.monoMass = monoMass;
	}
	
	public int [] getComp(){
		return comp;
	}
	
	public void setComp(int [] comp){
		this.comp = comp;
	}
	
	public double getMonoMass(){
		return monoMass;
	}
	
	public void setMonoMass(double monoMass){
		this.monoMass = monoMass;
	}
	
	public String getCompStr(){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<comp.length;i++){
			sb.append(comp[i]).append("_");
		}
		sb.deleteCharAt(sb.length()-1);
//		return sb.substring(0, sb.capacity()-1);
		return sb.toString();
	}
	
	public int [] parseComp(String compstr){
		String [] ss = compstr.split("_");
		int [] comp = new int [ss.length];
		for(int i=0;i<ss.length;i++){
			comp[i] = Integer.parseInt(ss[i]);
		}
		return comp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GlycoAntenna o) {
		// TODO Auto-generated method stub
		double d0 = o.monoMass;
		double d1 = this.monoMass;
		if(d0<d1)
			return 1;
		else if(d0>d1)
			return -1;
		return 0;
	}
	
}
