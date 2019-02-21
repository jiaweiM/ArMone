/* 
 ******************************************************************************
 * File: OGlycanTypeInfo.java * * * Created on 2013-6-19
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

/**
 * @author ck
 *
 * @version 2013-6-19, 9:43:54
 */
public class OGlycanTypeInfo {
	
	private double pepPeakMz;
	private double pepPeakInten;
	private double mass;
	private int type;
	private String info;
	private  double [] fragments;
	
	/**
	 * @param pepPeakMz
	 * @param pepPeakInten
	 * @param type
	 * @param info
	 * @param fragments
	 */
	public OGlycanTypeInfo(double pepPeakMz, double pepPeakInten, double mass, 
			String info, double[] fragments) {
		
		this.pepPeakMz = pepPeakMz;
		this.pepPeakInten = pepPeakInten;
		this.mass = mass;
		this.info = info;
		this.fragments = fragments;
	}

	
	
	
	/**
	 * @param pepPeakMz
	 * @param pepPeakInten
	 * @param type
	 * @param info
	 * @param fragments
	 */
	public OGlycanTypeInfo(double pepPeakMz, double pepPeakInten, int type,
			String info, double[] fragments) {
		
		this.pepPeakMz = pepPeakMz;
		this.pepPeakInten = pepPeakInten;
		this.type = type;
		this.info = info;
		this.fragments = fragments;
	}



	/**
	 * @return the pepPeakMz
	 */
	public double getPepPeakMz() {
		return pepPeakMz;
	}



	/**
	 * @param pepPeakMz the pepPeakMz to set
	 */
	public void setPepPeakMz(double pepPeakMz) {
		this.pepPeakMz = pepPeakMz;
	}



	/**
	 * @return the pepPeakInten
	 */
	public double getPepPeakInten() {
		return pepPeakInten;
	}



	/**
	 * @param pepPeakInten the pepPeakInten to set
	 */
	public void setPepPeakInten(double pepPeakInten) {
		this.pepPeakInten = pepPeakInten;
	}



	/**
	 * @return the mass
	 */
	public double getMass() {
		return mass;
	}



	/**
	 * @param mass the mass to set
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}



	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}



	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}



	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}



	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}



	/**
	 * @return the fragments
	 */
	public double[] getFragments() {
		return fragments;
	}



	/**
	 * @param fragments the fragments to set
	 */
	public void setFragments(double[] fragments) {
		this.fragments = fragments;
	}

	public boolean equals(Object obj){
		if(obj instanceof OGlycanTypeInfo){
			OGlycanTypeInfo info = (OGlycanTypeInfo) obj;
			return this.info.equals(info.info);
		}else{
			return false;
		}
	}
	
	public int hashCode(){
		return this.info.hashCode();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
