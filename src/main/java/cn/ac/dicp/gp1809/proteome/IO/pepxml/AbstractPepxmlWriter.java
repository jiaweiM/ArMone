/* 
 ******************************************************************************
 * File: AbstractPepxmlWriter.java * * * Created on 07-23-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.pepxml;


import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.DataType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPepxmlWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * Abstract pepxml writer
 * 
 * @author Xinning
 * @version 0.3, 05-02-2010, 10:42:15
 */
public abstract class AbstractPepxmlWriter implements IPepxmlWriter {

	public static final DecimalFormat DF6 = DecimalFormats.DF0_6;
	
	public static final DecimalFormat DF4 = DecimalFormats.DF0_4;
	
	public static final DecimalFormat DF3 = DecimalFormats.DF0_3;
	
	public static final DecimalFormat DF1 = DecimalFormats.DF0_1;
	
	public static final DecimalFormat DF0 = DecimalFormats.DF0_0;
	
	public void setDataType(DataType dataType){

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public boolean write(IPeptide peptide, IMS2PeakList[] peaklist) {
		this.write(peptide);
		return true;
    }

}
