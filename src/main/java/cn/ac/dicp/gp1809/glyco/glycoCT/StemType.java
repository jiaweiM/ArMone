/* 
 ******************************************************************************
 * File: StemType.java * * * Created on 2012-3-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.glycoCT;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author ck
 *
 * @version 2012-3-21, 13:45:06
 */
public enum StemType {

	gro ("GRO", "Glyceraldehyde", 3),
	
	ery ("ERY", "Erythrose", 4),
	
	rib ("RIB", "Ribose", 5),
	
	ara ("ARA", "Arabinose", 5),
	
	all ("ALL", "Allose", 6),
	
	alt ("ALT", "Altrose", 6),
	
	glc ("GLC", "Glucose", 6),
	
	man ("MAN", "Mannose", 6),
	
	tre ("TRE", "Threose", 4),
	
	xyl ("XYL", "Xylose", 5),
	
	lyx ("LYX", "Lyxose", 5),
	
	gul ("GUL", "Gulose", 6),
	
	ido ("IDO", "Idose", 6),
	
	gal ("GAL", "Galactose", 4),
	
	tal ("TAL", "Talose", 5),
	
	;
	
	StemType(String code, String name, int superClassType){
		
		this.code = code;
		this.name = name;
		this.superClassType = superClassType;
	}
	
	/**
	 * C3~C6
	 */
	private static double [] mono_mass = new double [] {72.0211293722, 102.03169405829999, 132.0422587444, 162.05282343049998};
	
	private static double [] ave_mass = new double [] {72.06266, 102.08864, 132.11462, 162.1406};
	
	private static String [] superClass = new String [] {"TRI", "TET", "PEN", "HEX"};
	
	private String code;
	private String name;
	private int superClassType;
	
	public String getCode(){
		return code;
	}
	
	public String getName(){
		return name;
	}
	
	public int getSuperClassType(){
		return superClassType;
	}
	
	public double getMonoMass(){
		return mono_mass[superClassType-3];
	}
	
	public double getAveMass(){
		return ave_mass[superClassType-3];
	}
	
	public String getSuperClass(){
		return superClass[superClassType-3];
	}
	
	/**
	 * "TRI", "TET", "PEN", "HEX"
	 * @return
	 */
	public static HashSet <String> getSuperClassSet(){
		HashSet <String> set = new HashSet <String>();
		for(int i=0;i<superClass.length;i++){
			set.add(superClass[i]);
		}
		return set;
	}
	
	public static HashMap <String, StemType> getStemMap(){
		
		HashMap <String, StemType> stemmap = new HashMap <String, StemType>();
		
		StemType [] stems = StemType.values();
		for(int i=0;i<stems.length;i++){
			stemmap.put(stems[i].toString(), stems[i]);
		}
		
		return stemmap;
	}
}
