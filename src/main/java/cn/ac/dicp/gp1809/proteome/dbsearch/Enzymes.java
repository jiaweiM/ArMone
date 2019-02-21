/* 
 ******************************************************************************
 * File: Enzymes.java * * * Created on 10-19-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;

/**
 * The pre set enzymes for database search.
 * 
 * @author Xinning
 * @version 0.1, 10-19-2008, 15:51:15
 */
public enum Enzymes {
	/** Peptide identified by SEQUEST, index: 0 */
	SEQUEST("SEQUEST", 0),

	/** Peptide identified by Mascot, index: 1 */
	MASCOT("MASCOT", 1),

	/** Peptide identified by X!Tandem, index: 2 */
	XTANDEM("X!TANDEM", 2),

	/** Peptide identified by OMSSA, index: 3 */
	OMSSA("OMSSA", 3),

	/** APIVASE Peptide Pair for Sequest, index: 20 */
	APIVASE("APIVASE", 20);

	/**
	 * Get the peptide type by the index
	 * 
	 * <li>SEQUEST("SEQUEST", 0),
	 * 
	 * <li>MASCOT("MASCOT", 1),
	 * 
	 * <li>XTANDEM("X!TANDEM", 2),
	 * 
	 * <li>OMSSA("OMSSA", 3),
	 * 
	 * <li>APIVASE("APIVASE", 20);
	 * 
	 * @param index
	 * @return
	 */
	public static PeptideType getTypebyIndex(int index) {
		PeptideType[] types = PeptideType.values();
		for(PeptideType type : types){
			//The name and index must both equals
			if(type.getType() == index){
				return type;
			}
		}
		
		throw new IllegalArgumentException("Unkown type for index: "+index);
	}
	
	
	/**
	 * Get the peptide type by the name of the algorithm
	 * 
	 * <li>SEQUEST("SEQUEST", 0),
	 * 
	 * <li>MASCOT("MASCOT", 1),
	 * 
	 * <li>XTANDEM("X!TANDEM", 2),
	 * 
	 * <li>OMSSA("OMSSA", 3),
	 * 
	 * <li>APIVASE("APIVASE", 20);
	 * 
	 * @param name: e.g. X!TANDEM by NOT XTANDEM
	 * @return
	 */
	public static PeptideType getTypebyName(String name) {
		PeptideType[] types = PeptideType.values();
		for(PeptideType type : types){
			//The name and index must both equals
			if(type.getAlgorithm_name().equals(name)){
				return type;
			}
		}
		
		throw new IllegalArgumentException("Unkown type for name: "+name);
	}
	
	
	/**
	 * Parse the formatted string.
	 * 
	 * @param formatStr
	 *            The string should be "Algorithm_name, type"
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static PeptideType typeOfFormat(String formatStr)
	        throws IllegalArgumentException {
		if (formatStr == null || formatStr.length() == 0)
			throw new IllegalArgumentException(
			        "The format string is null or with length of 0");

		String[] cells = formatStr.split(",");
		if (cells.length != 2)
			throw new IllegalArgumentException(
			        "The legal format string is \"algorithm_name, type\", current: "
			                + formatStr);

		String name = cells[0].trim();
		int idx = Integer.parseInt(cells[1].trim());

		PeptideType[] types = PeptideType.values();
		for (PeptideType type : types) {
			// The name and index must both equals
			if (type.getAlgorithm_name().equals(name) && type.getType() == idx) {
				return type;
			}
		}

		throw new IllegalArgumentException("Unkown type for format string: "
		        + formatStr);
	}
	
	

	private int type;
	private String algorithm_name;

	/**
	 * @param type
	 * @param algorithm_name
	 */
	private Enzymes(String algorithm_name, int type) {
		this.type = type;
		this.algorithm_name = algorithm_name;
	}

	/**
	 * @return the type of the algorithm used for database search
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the name of algorithm used for peptide search
	 */
	public String getAlgorithm_name() {
		return algorithm_name;
	}

	/**
	 * Algorithm_name+", "+type
	 */
	@Override
	public String toString() {
		return this.algorithm_name + ", " + type;
	}
}
