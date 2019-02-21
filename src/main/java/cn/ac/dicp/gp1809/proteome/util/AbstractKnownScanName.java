/* 
 ******************************************************************************
 * File: AbstractKnownScanName.java * * * Created on 03-03-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

/**
 * 
 * @author Xinning
 * @version 0.1, 03-03-2009, 14:40:37
 */
public abstract class AbstractKnownScanName extends AbstractScanName implements
        IKnownFormatScanName {

	protected AbstractKnownScanName() {

	}

	/**
	 * @param baseName
	 * @param scanBeg
	 * @param scanEnd
	 * @param charge
	 * @param extension
	 */
	protected AbstractKnownScanName(String baseName, int scanBeg, int scanEnd,
	        short charge, String extension) {
		super(baseName, scanBeg, scanEnd, charge, extension);
	}

	/**
	 * Construct the sequest scan name without extension. For example, for
	 * sequest scan name, XXXXX.0000.0000.0
	 * 
	 * @return
	 */
	private String getScanNoExt(String baseName, int scanBeg, int scanEnd,
	        short charge) {
		StringBuilder sb = new StringBuilder(18);
		sb.append(baseName).append('.').append(scanBeg).append('.').append(
		        scanEnd).append('.').append(charge);

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getScanNamenoExtension()
	 */
	@Override
	public String getScanNamenoExtension() {
		return this.getScanNoExt(getBaseName(), getScanNumBeg(),
		        getScanNumEnd(), getCharge());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName#getFormattedScanName
	 * ()
	 */
	@Override
	public FormattedScanName getFormattedScanName() {
		return new FormattedScanName(this.getBaseName(), this.getScanNumBeg(),
		        this.getScanNumEnd());
	}
}
