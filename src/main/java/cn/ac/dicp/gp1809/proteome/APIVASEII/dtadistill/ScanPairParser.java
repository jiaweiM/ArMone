/*
 * *****************************************************************************
 * File: ScanPairParser.java * * * Created on 07-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.dtadistill;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralScanUtil;
import cn.ac.dicp.gp1809.proteome.proteometools.fileOperation.ScanFileUtil;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2ScanList;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * Parse the valid MS2-MS3 scan pairs with significant neutral loss (50% base
 * peak)
 * 
 * @deprecated
 * 
 * @author Xinning
 * @version 0.1.1, 06-04-2009, 11:00:58
 */
@Deprecated
public class ScanPairParser {

	private File ms2file = null, ms3file = null;
	private HashSet<String> corelatedms2names, corelatedms3names;
	/*
	 * For low mass accuracy mass spectrometer, one scan may be exported into
	 * two or more dta files with ambiguous charge states. There should be only
	 * one right charge state, others should be duplicated names and should be
	 * removed.
	 */
	private HashSet<String> duplicatedms2names, duplicatedms3names;
	private SequestScanName [] ms2names, ms3names;
	private NeutralScanUtil scanUtil;
	private double lostmass;
	private ISpectrumThreshold threshold;

	/**
	 * 
	 * @param ms2folder
	 *            Directory file which contains all the MS2 dta (out) files.
	 * @param ms3folder
	 *            Directory file which contains all the MS3 dta (out) files.
	 * @param mzdata
	 * @param extend
	 *            , extension: dta or out
	 * @param MSnCount
	 *            how many MSn scans in a MS circulation. e.g. for a MS circle :
	 *            MS MS2 MS2 MS2 MS3 MS3 MS3, this count is 3. Default MSnCount
	 *            == 3;
	 * @param threshold
	 *            the parsing threshold
	 * @param lostmass
	 *            the neutral lost mass for scan pair parsing.
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException
	 */
	public ScanPairParser(String ms2folder, String ms3folder,
	        MS2ScanList scanlist, int MSnCount, ISpectrumThreshold threshold,
	        double lostmass) throws FileNotFoundException,
	        DtaFileParsingException {
		this(new File(ms2folder), new File(ms3folder), scanlist, MSnCount,
		        threshold, lostmass);
	}

	/**
	 * 
	 * @param ms2file
	 *            Directory file which contains all the MS2 dta (out) files.
	 * @param ms3file
	 *            Directory file which contains all the MS3 dta (out) files.
	 * @param xmlfile
	 * @param MSnCount
	 *            how many MSn scans in a MS circulation. e.g. for a MS circle :
	 *            MS MS2 MS2 MS2 MS3 MS3 MS3, this count is 3. Default MSnCount
	 *            == 3;
	 * @param threshold
	 *            the parsing threshold
	 * @param lostmass
	 *            the neutral lost mass for scan pair parsing.
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException
	 */
	public ScanPairParser(File ms2file, File ms3file, MS2ScanList scanlist,
	        int MSnCount, ISpectrumThreshold threshold, double lostmass)
	        throws FileNotFoundException, DtaFileParsingException {

		if (!ms2file.exists() || !ms2file.isDirectory())
			throw new FileNotFoundException();

		this.ms2file = ms2file;

		if (!ms3file.exists() || !ms3file.isDirectory())
			throw new FileNotFoundException();

		this.ms3file = ms3file;
		this.threshold = threshold;
		this.lostmass = lostmass;

		scanUtil = new NeutralScanUtil(scanlist, MSnCount);

		this.parse();
	}

	private void parse() throws DtaFileParsingException, FileNotFoundException {
		//Enough to use the dta file
		String extend = ScanFileUtil.DTA_FILE;

		//without extension
		HashSet<String> ms2filenameset = new HashSet<String>();
		HashSet<String> ms3filenameset = new HashSet<String>();

		HashSet<ScanPair> pairSet = new HashSet<ScanPair>();

		HashSet<String> duplicatedms2names = new HashSet<String>();
		HashSet<String> duplicatedms3names = new HashSet<String>();

		ScanFileUtil ms2scan = new ScanFileUtil(this.ms2file, extend);
		ScanFileUtil ms3scan = new ScanFileUtil(this.ms3file, extend);

		Integer[] ms3scans = ms3scan.getScanNumList();

		for (int i = 0; i < ms3scans.length; i++) {
			Integer scannum = ms3scans[i];

			/*
			 * The last MS3 in spectrum may not be transfered to mzdata, then
			 * the ms2scan number will be -1. Currently, I can't process these
			 * scans, just leave them as the MS2 scan with no MS3
			 */
			int ms2scannum = scanUtil.getCorrespondMS2(scannum);
			short charge = scanUtil.getChargeStatebyNeutralLoss(scannum,
			        this.lostmass);

			SequestScanName ms2name = ms2scan.getScanFileName(ms2scannum, charge);
			SequestScanName ms3name = ms3scan.getScanFileName(scannum, charge);
/*
			if (ms2name != null && ms3name != null) {

				NeutralLossSequestScanDta dta = new NeutralLossSequestDtaReader(
				        new File(this.ms2file, ms2name.getScanName()), this.threshold,
				        this.lostmass).getDtaFile(true);

				if (dta.isNeutralLoss()) {
					ms2filenameset.add(ms2name.getScanName());
					ms3filenameset.add(ms3name.getScanName());

					pairSet.add(new ScanPair(new File(this.ms2file, ms2name.getScanName()),
					        new File(this.ms3file, ms3name.getScanName())));


					// Add to duplicated list

					
					LinkedList<String> ms2names = ms2scan.getScanFilenames(ms2scannum);
					LinkedList<String> ms3names = ms3scan.getScanFilenames(scannum);
					for (String nam : ms2names) {
						if (nam.equals(ms2name))
							continue;
						duplicatedms2names.add(nam);
					}

					for (String nam : ms3names) {
						if (nam.equals(ms3name))
							continue;
						duplicatedms3names.add(nam);
					}
				} else {
					
//					The neutral loss peak intensity lower than the threshold,
//					 and remove the ms3 spectrum.

					LinkedList<String> ms3names = ms3scan
					        .getScanFilenames(scannum);

					for (String nam : ms3names) {
						if (nam.equals(ms3name))
							continue;
						duplicatedms3names.add(nam);
					}
				}
			}
*/			
		}

		this.corelatedms2names = ms2filenameset;
		this.corelatedms3names = ms3filenameset;

		this.duplicatedms2names = duplicatedms2names;
		this.duplicatedms3names = duplicatedms3names;

		this.ms2names = ms2scan.getScanFileNameList();
		this.ms3names = ms3scan.getScanFileNameList();
				
	}

	/**
	 * For low mass accuracy mass spectrometer, one scan may be exported into
	 * two or more dta files with ambiguous charge states. There should be only
	 * one right charge state, others should be duplicated names and should be
	 * removed.
	 * 
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return
	 */
	public HashSet<String> getDuplicatedMS2Names() {
		return this.duplicatedms2names;
	}

	/**
	 * For low mass accuracy mass spectrometer, one scan may be exported into
	 * two or more dta files with ambiguous charge states. There should be only
	 * one right charge state, others should be duplicated names and should be
	 * removed.
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return
	 */
	public HashSet<String> getDuplicatedMS3Names() {
		return this.duplicatedms3names;
	}

	/**
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return the hashset contains all the MS2 which with corresponding MS3.
	 *         <b>Note that this name list was predicted from the MS3 dta file
	 *         so only outputted MS3(dta) can be used to the prediction of MS2.
	 *         The charge state of a single spectrum is unique in all the dta
	 *         files</b>
	 */
	public HashSet<String> getCorrelatedMS2NameSet() {
		return this.corelatedms2names;
	}

	/**
	 * 
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return the String array contaning all the MS2 which contains MS3 <b>Note
	 *         that this name list was predicted from the MS3 dta file so only
	 *         outputed MS3(dta) can be used to the prediction of MS2. The
	 *         charge state of a single spectrum is unique in all the dta
	 *         files</b>
	 */
	public String[] getCorrelatedMS2Names() {
		return this.corelatedms2names.toArray(new String[0]);
	}

	/**
	 * The name of MS3 dta files with the unique charge states. The charge
	 * states were calculated from the neutral loss mass. Note that if a MS2 dta
	 * file was not exported from spectrum for some reasons, e.g. outputed mass
	 * out of range (600-3500 Da), the corresponding MS3 will not appear in this
	 * list.
	 * 
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return
	 */
	public String[] getCorrelatedMS3Names() {
		return this.corelatedms3names.toArray(new String[0]);
	}

	/**
	 * the name of MS3 dta files with the unique charge states. The charge
	 * states were calculated from the neutral loss mass. Note that if a MS2 dta
	 * file was not exported from spectrum for some reasons, e.g. outputed mass
	 * out of range (600-3500 Da), the corresponding MS3 will not appear in this
	 * list.
	 * 
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return
	 */
	public HashSet<String> getCorrelatedMS3NameSet() {
		return this.corelatedms3names;
	}

	/**
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return ms2 folder file
	 */
	public File getMS2File() {
		return this.ms2file;
	}

	/**
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return all the file names
	 */
	
	
	public String[] getMS2Names() {
//		return this.ms2names;
		return null;
	}

	/**
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return the MS3 folder
	 */
	public File getMS3File() {
		return this.ms3file;
	}

	/**
	 * All the ms3 names
	 * 
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return
	 */
		
	public String[] getMS3Names() {
//		return this.ms3names;
		return null;
	}
	
	/**
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return the scanUtil used
	 */
	public NeutralScanUtil getScanUtil() {
		return this.scanUtil;
	}

	/**
	 * The scan pair of corresponding MS2-MS3
	 * 
	 * @author Xinning
	 * @version 0.1, 06-04-2009, 11:00:05
	 */
	public static class ScanPair {

		private File ms2dta, ms3dta;

		public ScanPair(File ms2dta, File ms3dta) {
			this.ms2dta = ms2dta;
			this.ms3dta = ms3dta;
		}

		public File getMS2() {
			return this.ms2dta;
		}

		public File getMS3() {
			return this.ms3dta;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return this.ms3dta.hashCode();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ScanPair) {
				if (((ScanPair) obj).ms3dta.equals(ms3dta)) {
					return true;
				}

				return false;
			}
			return false;
		}
	}
}
