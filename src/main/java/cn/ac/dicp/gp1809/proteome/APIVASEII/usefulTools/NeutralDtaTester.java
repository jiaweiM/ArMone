/* 
 ******************************************************************************
 * File: NeutralDtaTester.java * * * Created on 2009-3-18
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.usefulTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossSequestDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * Test a neutral dta
 * 
 * @author Xinning
 * @version 0.1, 03-18-2009, 16:00:02
 */
public class NeutralDtaTester {

	private double loss;
	private ISpectrumThreshold threshold;

	public NeutralDtaTester(ISpectrumThreshold threshold, double loss) {
		this.threshold = threshold;
		this.loss = loss;
	}

	/**
	 * Test the neutral loss
	 * 
	 * @param dta
	 *            file name
	 * @return
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException
	 */
	public boolean testNeu(String dtafile) throws DtaFileParsingException,
	        FileNotFoundException {
		return new NeutralLossSequestDtaReader(dtafile, this.threshold,
		        this.loss).getDtaFile(true).isNeutralLoss();
	}
	
	public static void main(String[] args) throws DtaFileParsingException, FileNotFoundException {
		
		String dir = "D:\\try\\1\\mS2\\";
		
		File[] files = new File(dir).listFiles(new FilenameFilter() {

			@Override
            public boolean accept(File dir, String name) {
				
				if(name.endsWith(".dta"))
					return true;
				
	            return false;
            }
			
		});
		
		NeutralDtaTester tester = new NeutralDtaTester(SpectrumThreshold.HALF_INTENSE_THRESHOLD, 98);
		
		for(File file : files) {
			System.out.println(file.getName()+": "+tester.testNeu(file.getAbsolutePath()));
		}
	}
}
