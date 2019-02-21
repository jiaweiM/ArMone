/* 
 ******************************************************************************
 * File: AbstractScanName.java * * * Created on 11-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

/**
 * Abstract class of IScanName
 * 
 * @author Xinning
 * @version 0.1, 11-07-2008, 15:11:41
 */
public abstract class AbstractScanName implements IScanName {

	/**
	 * The extension for the dta type file (dta)
	 */
	public static final String FILETYPE_DTA = "dta";

	/**
	 * The extension for the out type file (out)
	 */
	public static final String FILETYPE_OUT = "out";

	private String baseName;
	private int scanNumBeg;
	private int scanNumEnd;
	private short charge;
	private String extension;

	/**
	 * For inheritance, Entries should be set manually in inherit class.
	 * 
	 * @param scanname
	 */
	protected AbstractScanName() {

	}

	protected AbstractScanName(String baseName, int scanBeg, int scanEnd,
	        short charge, String extension) {
		this.baseName = baseName;
		this.scanNumBeg = scanBeg;
		this.scanNumEnd = scanEnd;
		this.charge = charge;
		this.extension = extension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getBaseName()
	 */
	public String getBaseName() {
		return baseName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getScanNumBeg()
	 */
	public int getScanNumBeg() {
		return scanNumBeg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getScanNumEnd()
	 */
	public int getScanNumEnd() {
		return scanNumEnd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getCharge()
	 */
	public short getCharge() {
		return charge;
	}

	@Override
	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	@Override
	public void setCharge(short charge) {
		this.charge = charge;
	}

	@Override
	public void setScanNumBeg(int scanNumBeg) {
		this.scanNumBeg = scanNumBeg;
	}

	@Override
	public void setScanNumEnd(int scanNumEnd) {
		this.scanNumEnd = scanNumEnd;
	}

	@Override
	public void setExtension(String ext) {
		this.extension = ext;
	}

	@Override
	public boolean hasExtension() {
		return this.extension != null && this.extension.length() > 0;
	}

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getExtension()
     */
    @Override
    public String getExtension() {
	    return this.extension;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#isOutFile()
	 */
	public boolean isOutFile() {
		return FILETYPE_OUT.equalsIgnoreCase(this.extension);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#isDtaFile()
	 */
	public boolean isDtaFile() {
		return FILETYPE_DTA.equalsIgnoreCase(this.extension);
	}

	/*
	 * The filename hashcode (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int hash = this.baseName == null ? 0 : this.baseName.hashCode()
		        + this.scanNumBeg + this.scanNumEnd + this.charge;

		return hash;
	}

	/*
	 * Same if they are with the same filename (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractScanName) {
			AbstractScanName s2 = ((AbstractScanName) obj);
			if(this.baseName != null){
				if(!this.baseName.equals(s2.baseName))
					return false;
			}
			else{
				if(this.baseName != s2.baseName)
					return false;
			}
			
			if(this.scanNumBeg != s2.scanNumBeg)
				return false;
			
			if(this.scanNumEnd != s2.scanNumEnd)
				return false;
			
			if(this.charge != s2.charge)
				return false;
			
			return true;
		}
		return false;
	}

	/**
	 * The input name. Equals {@link #getScanName()}
	 */
	@Override
	public String toString() {
		return this.getScanName();
	}

}
