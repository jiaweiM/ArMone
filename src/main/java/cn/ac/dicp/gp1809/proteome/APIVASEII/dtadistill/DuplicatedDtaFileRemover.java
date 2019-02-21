/*
 ******************************************************************************
 * File: DuplicatedDtaFileRemover.java * * * Created on 07-15-2008
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
import java.util.HashSet;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosConstants;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;

/**
 * Remove the MS2 without MS3, MS2 with neutral loss intensity less than 50%,
 * and MS2_MS3 pairs with wrong charge states
 * 
 * @author Xinning
 * @version 0.2, 02-24-2009, 13:55:22
 */
public class DuplicatedDtaFileRemover {

	/**
	 * The peaks within +- this threshold will be considered as match.
	 */
	public static double peakThreshold = 1;

	/**
	 * The intensity must bigger than this value will be considered as a valid
	 * peak.
	 */
	public static double intenseThreshold = 0.5;

	private ScanPairParser dtaparser = null;

	public DuplicatedDtaFileRemover(File ms2file, File ms3file,
	        MS2ScanList scanlist, int MSnCount, ISpectrumThreshold threshold,
	        double lostmass) throws FileNotFoundException,
	        DtaFileParsingException {

		this.dtaparser = new ScanPairParser(ms2file, ms3file, scanlist,
		        MSnCount, threshold, lostmass);
	}

	public DuplicatedDtaFileRemover(String ms2folder, String ms3folder,
	        MS2ScanList scanlist, int MSnCount, ISpectrumThreshold threshold,
	        double lostmass) throws FileNotFoundException,
	        DtaFileParsingException {
		this(new File(ms2folder), new File(ms3folder), scanlist, MSnCount,
		        threshold, lostmass);
	}

	/**
	 * Only retain the paired dta files for MS2 and MS3 and remove all other dta
	 * files including scans with no MS3, dta files with wrong charge states.
	 * This is the most strict removal.
	 */
	public void remove_paired_retain() {
		System.out
		        .println("Removing dta files with only paired MS2 and MS3 retained ...");

		File ms2file = this.dtaparser.getMS2File();
		File ms3file = this.dtaparser.getMS3File();

		HashSet<String> dupems2 = this.dtaparser.getCorrelatedMS2NameSet();
		HashSet<String> dupems3 = this.dtaparser.getCorrelatedMS3NameSet();
		String[] ms2 = this.dtaparser.getMS2Names();
		String[] ms3 = this.dtaparser.getMS3Names();

		for (int i = 0; i < ms2.length; i++) {
			String name = ms2[i];
			if (!dupems2.contains(name)) {
				this.remove(ms2file, name);
			}
		}

		for (int i = 0; i < ms3.length; i++) {
			String name = ms3[i];
			if (!dupems3.contains(name)) {
				this.remove(ms3file, name);
			}
		}

		System.out.println("Finished.");
	}

	/**
	 * Only remove the dta files with wrong charge states. That is MS2 with MS3
	 * but with wrong charge in low mass spectrometry.
	 */
	public void remove_wrong_charge() {
		System.out.println("Removing ambiguate dta with wrong charge ...");

		File ms2file = this.dtaparser.getMS2File();
		File ms3file = this.dtaparser.getMS3File();

		HashSet<String> dupems2 = this.dtaparser.getDuplicatedMS2Names();
		HashSet<String> dupems3 = this.dtaparser.getDuplicatedMS3Names();

		for (String name : dupems2) {
			this.remove(ms2file, name);
		}

		for (String name : dupems3) {
			this.remove(ms3file, name);
		}

		System.out.println("Finished.");
	}

	/**
	 * Remove dta file and its out file (if exists)
	 * 
	 * @param dir
	 * @param name
	 */
	private void remove(File dir, String name) {
		new File(dir, name).delete();
		File out = new File(dir, name.substring(0, name.length() - 3) + "out");
		if (out.exists())
			out.delete();
	}

	/**
	 * Remove the dta files for the phosphopeptides
	 * 
	 * @param ms2file
	 * @param ms3file
	 * @param mzdata
	 * @param MSnCount
	 * @param threshold
	 * @param lostmass
	 * @param type
	 *            the type of the removing.
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException
	 * @throws XMLStreamException 
	 */
	public static void remove_phos(File ms2file, File ms3file, File mzdata,
	        int MSnCount, int type) throws FileNotFoundException,
	        DtaFileParsingException, XMLStreamException {

		ISpectrumThreshold threshold = new SpectrumThreshold(peakThreshold,
		        intenseThreshold);

		remove(ms2file, ms3file, mzdata, MSnCount, threshold,
		        PhosConstants.PHOSPHATE_MASS, type);

	}

	/**
	 * select a type of removal
	 * 
	 * @param ms2file
	 * @param ms3file
	 * @param mzdata
	 * @param MSnCount
	 * @param threshold
	 * @param lostmass
	 * @param type
	 *            the type of the removing.
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException
	 * @throws XMLStreamException 
	 */
	public static void remove(File ms2file, File ms3file, File mzdata,
	        int MSnCount, ISpectrumThreshold threshold, double lostmass,
	        int type) throws FileNotFoundException, DtaFileParsingException, XMLStreamException {

		MzDataStaxReader reader = new MzDataStaxReader(mzdata);
		reader.rapMS2ScanList();
		
		MS2ScanList scanlist = reader.getMS2ScanList();

		DuplicatedDtaFileRemover remover = new DuplicatedDtaFileRemover(
		        ms2file, ms3file, scanlist, MSnCount, threshold, lostmass);

		switch (type) {
		case 0:
			remover.remove_paired_retain();
			break;
		case 1:
			remover.remove_wrong_charge();
			break;
		default:
			throw new IllegalArgumentException("");
		}

	}

	private static String usage() {
		return "Usage: \r\n"
		        + "\tdtaremover ms2folder ms3folder mzdata MSnCount remove_Type\r\n"
		        + "\t     options: ms2folder the directory contains ms2 scans\r\n"
		        + "\t              ms3folder the directory contains ms3 scans\r\n"
		        + "\t              mzdata  mzdata file path\r\n"
		        + "\t              MSnCount count of ms2-ms3 in a collection circle\r\n"
		        + "\t              remove_type remove which dta files?\r\n\r\n"
		        + "\t       remove_type: 0, only retain the paired MS2 and MS3\r\n"
		        + "\t                    1, only remove the dta with wrong charge states.";
	}

	public static void main(String[] args) throws FileNotFoundException,
	        NumberFormatException, DtaFileParsingException, XMLStreamException {
		if (args == null || args.length != 5)
			System.out.println(usage());
		else
			DuplicatedDtaFileRemover.remove_phos(new File(args[0]), new File(
			        args[1]), new File(args[2]), Integer.parseInt(args[3]),
			        Integer.parseInt(args[4]));
	}
}
