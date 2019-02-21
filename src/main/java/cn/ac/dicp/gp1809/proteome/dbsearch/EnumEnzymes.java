/* 
 ******************************************************************************
 * File:EnumEnzymes.java * * * Created on Nov 17, 2009
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import java.util.EnumMap;

/**
 * 
 * @author JiaweiMao
 * @version Jan 19, 2016, 9:45:55 AM
 */
public enum EnumEnzymes {
	
	Trypsin ("Trypsin", true, "KR", "P"),
	
	Arg_C ("Arg-CR", true, "P", null),
	
	Asp_N ("Asp-N", true, "BD", null),
	
	Asp_N_ambic ("Asp-N_ambic", true, "DE", null), 
	
	Chymotrypsin ("Chymotrypsin", true, "FYWL", "P"),
	
	CNBr ("CNBr", true, "M", null), 
	
	Formic_acid ("Formic_acid", true, "D", null),
	
	Lys_C ("Lys-C", true, "K", "P"),
	
	Lys_C_P ("Lys-C/P", true, "K", null),
	
	PepsinA ("PepsinA", true, "FL", null),
	
	Tryp_CNBr ("Tryp-CNBr", true, "KRM", "P"),
	
	TrypChymo ("TrypChymo", true, "FYWLKR", "P"),
	
	Trypsin_P ("Trypsin/P", true, "KR", null),
	
	V8_DE ("V8-DF", true, "BDEZ", "P"),
	
	V8_E ("V9-E", true, "EZ", "P");

	
	
	private EnumEnzymes(String enzymeName, boolean sense_C, String cleaveAt,
	        String notcleaveAt){
		
	}
	
	EnumMap <EnumEnzymes, String> nameMap = new EnumMap <EnumEnzymes, String> (EnumEnzymes.class);
	
	EnumMap <EnumEnzymes, String> cleaveMap = new EnumMap <EnumEnzymes, String> (EnumEnzymes.class);
	
	EnumMap <EnumEnzymes, String> noCleaveMap = new EnumMap <EnumEnzymes, String> (EnumEnzymes.class);
	
	EnumMap <EnumEnzymes, String> ctermMap = new EnumMap <EnumEnzymes, String> (EnumEnzymes.class);
	
	public static void main(String [] args){
		for(EnumEnzymes a: EnumEnzymes.values()){
			System.out.println(a);
		}
	}
}
