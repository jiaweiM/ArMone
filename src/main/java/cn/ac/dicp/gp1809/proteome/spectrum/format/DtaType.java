/*
 ******************************************************************************
 * File: DtaType.java * * * Created on 03-05-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.format;

/**
 * The type of dta format
 * 
 * @author Xinning
 * @version 0.1.2, 04-15-2010, 17:00:41
 */
public enum DtaType {

	/**
	 * The dta file
	 */
	DTA("DTA", "dta", false),
	/**
	 * The mgf file.
	 */
	MGF("MGF", "mgf", true),

	/**
	 * The ms1 file.
	 */
	MS1("MS2", "ms2", true),
	
	/**
	 * The ms2 file.
	 */
	MS2("MS2", "ms2", true),

	/**
	 * The mzdata file
	 */
	MZDATA("MzData", "xml", true),

	/**
	 * The mzxml file
	 */
	MZXML("MzXML", "mzxml", true),
	
	MZML("MzML", "mzML", true),

	/**
	 * The dta from search out. Such as in dat file, in omssa file.
	 */
	SEARCHOUT("SEARCHOUT", "", true);

	
	private String type_name;
	private String extension;
	private boolean single_file;

	/**
	 * The DtaType from the name of the type
	 * 
	 * @param name type name of file
	 * @return a DtaType instance
	 */
	public static DtaType forTypeName(String name)
	        throws IllegalArgumentException {

		if (DTA.getType_name().equalsIgnoreCase(name))
			return DTA;

		if (MGF.getType_name().equalsIgnoreCase(name))
			return MGF;

		if (MS2.getType_name().equalsIgnoreCase(name))
			return MS2;
		
		if (MZDATA.getType_name().equalsIgnoreCase(name))
			return MZDATA;
		
		if (MZXML.getType_name().equalsIgnoreCase(name))
			return MZXML;

		if (SEARCHOUT.getType_name().equalsIgnoreCase(name))
			return SEARCHOUT;

		throw new IllegalArgumentException("Unknown dta type for name: \""
		        + name + "\"");
	}

	private DtaType(String type_name, String extendsion, boolean single_file) {
		this.type_name = type_name;
		this.extension = extendsion;
		this.single_file = single_file;
	}

	/**
	 * @return the type_name
	 */
	public String getType_name() {
		return type_name;
	}

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * If all the dta files are written to a single file (e.g. mgf) or one file
	 * per dta.
	 * 
	 * @return the single_file
	 */
	public boolean isSingle_file() {
		return single_file;
	}
}
