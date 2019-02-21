/* 
 ******************************************************************************
 * File: UnknownFormatScanName.java * * * Created on 11-14-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

/**
 * If the name of a scan is with unknown format, it will be parsed into this
 * class by ScanNameFascoty. The initial values of all the fields except the
 * scanname are 0 for number values and null for object values. If you want get
 * these informations, please set them first.
 * 
 * <p>Changes:
 * <li>0.1.1, 03-03-2009: implements IUnknownFormatScanName
 * 
 * @author Xinning
 * @version 0.1.1, 03-03-2009, 14:21:01
 */
public class UnknownFormatScanName extends AbstractScanName implements IUnknownFormatScanName{

	private String scanname;

	public UnknownFormatScanName(String scanname) {
		this.scanname = scanname;
	}

	@Override
	public String getScanName() {
		return this.scanname;
	}

	@Override
	public String getScanNamenoExtension() {
		throw new NullPointerException(
		        "This is a UnkownFormatScanName, only getScanName() "
		                + "& setScanName(String) methods can be used.");
	}
	
	/*
	 * The filename hashcode (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.scanname.hashCode();
	}

	/*
	 * Same if they are with the same filename (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnknownFormatScanName) {
			return this.scanname.equals(((UnknownFormatScanName)obj).scanname);
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public UnknownFormatScanName deepClone() {
	    return this.clone();
    }
	
	@Override
	public UnknownFormatScanName clone() {
		try {
	        return (UnknownFormatScanName) super.clone();
        } catch (CloneNotSupportedException e) {
	        e.printStackTrace();
        }
        
        return null;
	}
}
