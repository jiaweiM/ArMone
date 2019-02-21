/* 
 ******************************************************************************
 * File: SequestDtawriter.java * * * Created on 06-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IDtaWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Writer to a dta file
 * 
 * @author Xinning
 * @version 0.1.1, 08-10-2009, 19:26:45
 */
public class SequestDtawriter implements IDtaWriter {

	// To avoid the confusing situation that peak intensity have been normalized
	// to 1;
	private static final DecimalFormat DF3 = new DecimalFormat("0.###");

	private static final DecimalFormat DF4 = new DecimalFormat("0.####");

	private static final DecimalFormat DF6 = new DecimalFormat("0.000000");

	/**
	 * PlatForm dependent turn for file writing.
	 */
	private static final String lineSeparator = IOConstant.lineSeparator;

	public SequestDtawriter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.dta.IDtaWriter#write(cn.ac.dicp.gp1809
	 * .proteome.spectrum.dta.IScanDta, java.lang.String)
	 */
	@Override
	public boolean write(IScanDta dta, String path) throws DtaWritingException {

		IMS2PeakList peaklist = dta.getPeakList();
		int size = peaklist.size();

		StringBuilder sb = new StringBuilder(size * 16);
		sb.append(DF6.format(dta.getPrecursorMH())).append(' ').append(
		        dta.getCharge()).append(lineSeparator);

		for (int i = 0; i < size; i++) {
			IPeak peak = peaklist.getPeak(i);
			sb.append(DF4.format(peak.getMz())).append(' ').append(
			        DF3.format(peak.getIntensity())).append(lineSeparator);
		}
		

        try {
        	File file = new File(path);
        	PrintWriter pw = new PrintWriter(file);
    		pw.print(sb);
    		pw.close();
    		
        } catch (FileNotFoundException e) {
	        throw new DtaWritingException(e);
        }


		return true;
	}

}
