/* 
 ******************************************************************************
 * File: PhosSequestDtaReader.java * * * Created on 02-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;

/**
 * The reader for sequest scan dta specified for neutral lost dta file.
 * 
 * @author Xinning
 * @version 0.1, 02-24-2009, 14:19:30
 */
public class NeutralLossSequestDtaReader extends SequestDtaReader {

	private ISpectrumThreshold threshold;
	private double lostmass;

	/**
	 * @param file
	 * @throws FileNotFoundException
	 */
	public NeutralLossSequestDtaReader(File file, ISpectrumThreshold threshold,
	        double lostmass) throws FileNotFoundException {
		super(file);
		this.threshold = threshold;
		this.lostmass = lostmass;
	}

	/**
	 * @param instream
	 * @param isCloseAfterReading
	 * @param dtafile
	 * @param baseName
	 * @param scanNumBeg
	 * @param scanNumEnd
	 */
	public NeutralLossSequestDtaReader(InputStream instream,
	        boolean isCloseAfterReading, File dtafile, String baseName,
	        int scanNumBeg, int scanNumEnd, ISpectrumThreshold threshold,
	        double lostmass) {
		super(instream, isCloseAfterReading, dtafile, baseName, scanNumBeg,
		        scanNumEnd);
		this.threshold = threshold;
		this.lostmass = lostmass;
	}

	/**
	 * @param instream
	 * @param isCloseAfterReading
	 * @param dtafile
	 * @param filename
	 */
	public NeutralLossSequestDtaReader(InputStream instream,
	        boolean isCloseAfterReading, File dtafile, String filename,
	        ISpectrumThreshold threshold, double lostmass) {
		super(instream, isCloseAfterReading, dtafile, filename);
		this.threshold = threshold;
		this.lostmass = lostmass;
	}

	/**
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public NeutralLossSequestDtaReader(String filename,
	        ISpectrumThreshold threshold, double lostmass)
	        throws FileNotFoundException {
		super(filename);
		this.threshold = threshold;
		this.lostmass = lostmass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaReader#getDtaFile
	 * (boolean)
	 */
	@Override
	public NeutralLossSequestScanDta getDtaFile(boolean isIncludePeakList)
	        throws DtaFileParsingException {
		SequestScanDta scandta = super.getDtaFile(isIncludePeakList);
		return new NeutralLossSequestScanDta(scandta, this.threshold,
		        this.lostmass);
	}
}
