/* 
 ******************************************************************************
 * File:QMod.java * * * Created on 2010-6-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.ModInfoObject;

/**
 * @author ck
 *
 * @version 2010-6-28, 13:43:41
 */
public class QMod {

	private String seq;
	private ModInfoObject site;
	private int loc;
	private String ref;
	private double inten;
	
	public QMod(String ref, String seq, ModInfoObject site, double inten){
		this.ref = ref;
		this.seq = seq;
		this.site = site;
		this.inten = inten;
	}
	
	public QMod(String ref, String seq, ModInfoObject site, int loc, double inten){
		this(ref,seq,site,inten);
		this.loc = loc;
	}
	
	public void setLoc(int loc){
		this.loc = loc;
	}
	
	public double getInten(){
		return inten;
	}
	
	public void addInten(double inten){
		this.inten += inten;
	}
	

}
