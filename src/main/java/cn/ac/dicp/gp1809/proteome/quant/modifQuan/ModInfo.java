/* 
 ******************************************************************************
 * File:ModInfo.java * * * Created on 2010-7-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * @author ck
 *
 * @version 2010-7-22, 10:18:34
 */
public class ModInfo {
	
	private String name;
	private double mass;
	private char symbol;
	private ModSite site;

	public ModInfo(String name, double mass, char symbol, ModSite site){
		this.name = name;
		this.mass = mass;
		this.symbol = symbol;
		this.site =site;
	}
	
	public String getName(){
		return name;
	}
	
	public double getMass(){
		return mass;
	}
	
	public char getSymbol(){
		return symbol;
	}
	
	public ModSite getModSite(){
		return site;
	}
	
	public String getDes(){
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("_").append(site);
		return sb.toString();
	}
	
	@Override
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(site).append("\t");
		sb.append(mass).append("\t");
		sb.append(symbol).append("\t");
		sb.append(name).append("\t");
		
		return sb.toString();
	}
}
