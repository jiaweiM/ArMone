/* 
 ******************************************************************************
 * File: GlycoPepPeak.java * * * Created on 2011-3-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import cn.ac.dicp.gp1809.glyco.NGlycoPossiForm;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;

/**
 * A glycopeptide peak in an HCD spectrum. The composition of this ion 
 * is an peptide ion with glycosylation.
 *  
 * 
 * @author ck
 *
 * @version 2011-3-21, 14:56:22
 */
public class GlycoPepPeak extends Peak {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private short charge;
	private double pepMr;
	private double glycoMr;
	
	private NGlycoPossiForm [] forms;
	
	public GlycoPepPeak(IPeak peak, short charge, double pepMr){
		super(peak);
		this.charge = charge;
		this.pepMr = pepMr;
		this.glycoMr = (this.getMz()-AminoAcidProperty.PROTON_W)
			*charge - pepMr;
	}
	
	public GlycoPepPeak(IPeak peak, short charge, double pepMr, double glycoMr){
		super(peak);
		this.charge = charge;
		this.pepMr = pepMr;
		this.glycoMr = glycoMr;
	}
	
	public void setForm(NGlycoPossiForm [] forms){
		this.forms = forms;
	}
	
	public NGlycoPossiForm [] getForms(){
		return forms;
	}
	
	public void setCharge(short charge){
		this.charge = charge;
	}
	
	public short getCharge(){
		return charge;
	}
	
	public void setPepMr(double pepMr){
		this.pepMr = pepMr;
	}
	
	public double getPepMr(){
		return pepMr;
	}
	
	public void setGlycoMr(double glycoMr){
		this.glycoMr = glycoMr;
	}
	
	public double getGlycoMr(){
		return glycoMr;
	}
	
	@Override
	public int compareTo(IPeak peak) {
		if(peak instanceof GlycoPepPeak){
			GlycoPepPeak p1 = (GlycoPepPeak) peak;
			double gmr0 = this.glycoMr;
			double gmr1 = p1.glycoMr;
			if(gmr0>gmr1){
				return 1;
			}else if(gmr0<gmr1){
				return -1;
			}else{
				return 0;
			}
		}else{
			return super.compareTo(peak);
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof GlycoPepPeak){
			GlycoPepPeak p = (GlycoPepPeak) obj;
			
			NGlycoPossiForm [] f0 = this.forms;
			NGlycoPossiForm [] f1 = p.forms;
			
			for(int i=0;i<f0.length;i++){
				int [] c0 = f0[i].getComposition();
				int [] c1 = f1[i].getComposition();
				for(int j=0;j<c0.length;j++){
					if(c0[j]!=c1[j]){
						return false;
					}
				}
			}
			
			return true;
		}else
			return false;
	}
	
	@Override
	public int hashCode(){
		
		NGlycoPossiForm [] f0 = this.forms;
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<f0.length;i++){
			String d = f0[i].getCompDesNoCore();
			sb.append(d);
		}
		
		return sb.toString().hashCode();
	}
	
}
