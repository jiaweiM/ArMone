/*
 ******************************************************************************
 * File: InvalidSpectraRemoveTask.java * * * Created on 05-14-2009
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.dtadistill;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;

/**
 * Remove the MS2 without MS3, MS2 with neutral loss intensity less than 50% (or the specific threshold),
 * and MS2_MS3 pairs with wrong charge states
 * 
 * @author Xinning
 * @version 0.1.2, 07-04-2009, 16:55:00
 */
public class InvalidSpectraRemoveTask implements IInvalidSpectraRemoveTask {

	/**
	 * Only retain the paired ms2 and ms3 spectra and remove all other spectra
	 */
	public static final int TYPE_PAIRED_RETAIN = 0;

	/**
	 * Only remove the spectra whose charge state can be calculated from the
	 * significant charge state.
	 */
	public static final int TYPE_RMOVE_INVALID_CHARGE = 1;

	private IRawSpectraReader reader;

	private ScanPairParseTask pairParseTask;
	private IInvalidSpectraRemoveTask removeTask;

	private int type;
	private boolean isRenewMS3;

	//The current task
	private boolean parsePairTask = true;

	public InvalidSpectraRemoveTask(File ms2file, File ms3file,
	        MS2ScanList scanlist, int MSnCount, boolean isRenewMS3, ISpectrumThreshold threshold,
	        double lostmass, int type) throws FileNotFoundException,
	        DtaFileParsingException {

		this.pairParseTask = new ScanPairParseTask(ms2file, ms3file, scanlist,
		        MSnCount, threshold, lostmass);

		this.type = type;
		this.isRenewMS3 = isRenewMS3;
	}

	public InvalidSpectraRemoveTask(String ms2folder, String ms3folder,
	        MS2ScanList scanlist, int MSnCount, boolean isRenewMS3, ISpectrumThreshold threshold,
	        double lostmass, int type) throws FileNotFoundException,
	        DtaFileParsingException {
		this(new File(ms2folder), new File(ms3folder), scanlist, MSnCount, isRenewMS3,
		        threshold, lostmass, type);
	}

	public InvalidSpectraRemoveTask(String ms2folder, String ms3folder,
	        String mzdata, DtaType dtatype, int MSnCount, boolean isRenewMS3,
	        ISpectrumThreshold threshold, double lostmass, int type)
	        throws FileNotFoundException, DtaFileParsingException,
	        XMLStreamException {

		MSnCount = this.initialMzReader(dtatype, mzdata, MSnCount);
		reader.rapMS2ScanList();

		this.pairParseTask = new ScanPairParseTask(new File(ms2folder),
		        new File(ms3folder), reader.getMS2ScanList(), MSnCount, threshold,
		        lostmass);

		this.type = type;
		this.isRenewMS3 = isRenewMS3;
	}

	/**
	 * Initial MzReader
	 * 
	 * @param type
	 * @param mzfile
	 * @param MSnCount
	 * @return
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private int initialMzReader(DtaType type, String mzfile, int MSnCount)
	        throws FileNotFoundException, XMLStreamException {
		switch (type) {
		case MZDATA:
			reader = new MzDataReader(mzfile);
			break;
		case MZXML:
			reader = new MzXMLReader(mzfile);
			MSnCount = 1;
			break;
		default:
			throw new NullPointerException("UnSupported type: " + type);
		}

		return MSnCount;
	}

	@Override
	public float completedPercent() {
		return this.parsePairTask ? this.pairParseTask.completedPercent() * 0.5f
		        : 0.5f + this.removeTask.completedPercent() * 0.5f;
	}

	@Override
	public void dispose() {
		if (this.reader != null)
			this.reader.close();
	}

	@Override
	public boolean hasNext() {
		if (this.parsePairTask) {

			if (!this.pairParseTask.hasNext()) {
				this.pairParseTask.dispose();

				this.parsePairTask = false;
				this.removeTask = this.createRemover(this.type,
				        this.pairParseTask);
			}

			return true;
		} else {
			boolean has = this.removeTask.hasNext();
			if (!has)
				this.dispose();

			return has;
		}
	}

	@Override
	public boolean inDetermineable() {
		return false;
	}

	@Override
	public void processNext() {
		if (this.parsePairTask)
			this.pairParseTask.processNext();
		else
			this.removeTask.processNext();
	}

	private IInvalidSpectraRemoveTask createRemover(int type,
	        ScanPairParseTask pairParser) {
		switch (type) {
		case TYPE_PAIRED_RETAIN:
			return new IvSpRemovePairedRetainTask(pairParser.getMS2File(),
			        pairParser.getMS3File(), pairParser.getScanPairs(),
			        pairParser.getMS2Names(), pairParser.getMS3Names(), this.isRenewMS3);
		case TYPE_RMOVE_INVALID_CHARGE:
			return new IvSpRemoveInvalidChargeTask(pairParser.getMS2File(),
			        pairParser.getMS3File(), pairParser
			                .getInvalidChargeMS2Names(), pairParser
			                .getInvalidChargeMS3Names());
		}

		throw new IllegalArgumentException("Unkown remove type");
	}
}
