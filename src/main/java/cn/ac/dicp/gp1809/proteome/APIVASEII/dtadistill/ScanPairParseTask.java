/*
 * *****************************************************************************
 * File: ScanPairParseTask.java * * * Created on 07-29-2008
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
import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossSequestDtaReader;
import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossSequestScanDta;
import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralScanUtil;
import cn.ac.dicp.gp1809.proteome.proteometools.fileOperation.ScanFileUtil;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2ScanList;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * Generate the MS2/MS3 paired information.
 * 
 * @author Xinning
 * @version 0.2, 06-05-2009, 15:17:06
 */
public class ScanPairParseTask implements ITask {

	/**
	 * The default HALF_INTENSE_THRESHOLD
	 */
	private static final ISpectrumThreshold defaultThreshold = SpectrumThreshold.HALF_INTENSE_THRESHOLD;

	/**
	 * The max charge state
	 */
	private static final int MAX_CHARGE = 10;

	private File ms2file = null, ms3file = null;
	private HashSet<String> corelatedms2names, corelatedms3names;
	/**
	 * For low mass accuracy mass spectrometer, one scan may be exported into
	 * two or more dta files with ambiguous charge states. There should be only
	 * one right charge state, others should be duplicated names and should be
	 * removed.
	 */
	private HashSet<String> invalidChargems2names, invalidChargems3names;

	/**
	 * The names of ms2 and ms3 without significant neutral loss in MS2
	 */
	private HashSet<String> unNeutralMS2names, unNeutralMS3names;

	/**
	 * The MS2-MS3 scan pairs
	 */
	private HashSet<ScanPair> pairSet;

	private String[] ms2names, ms3names;
	private NeutralScanUtil scanUtil;
	private double lostmass;
	private ISpectrumThreshold threshold;

	//Enough to use the dta file
	private String extend = ScanFileUtil.DTA_FILE;

	//without extension
	private HashSet<String> ms2filenameset;
	private HashSet<String> ms3filenameset;

	private ScanFileUtil ms2scan;
	private ScanFileUtil ms3scan;

	private Integer[] ms3scans;

	//Current index
	private int curtIdx = -1;
	private int total;

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
	public ScanPairParseTask(String ms2folder, String ms3folder,
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
	public ScanPairParseTask(File ms2file, File ms3file, MS2ScanList scanlist,
	        int MSnCount, ISpectrumThreshold threshold, double lostmass)
	        throws FileNotFoundException, DtaFileParsingException {

		if (!ms2file.exists() || !ms2file.isDirectory())
			throw new FileNotFoundException();

		this.ms2file = ms2file;

		if (!ms3file.exists() || !ms3file.isDirectory())
			throw new FileNotFoundException();

		this.ms3file = ms3file;
		this.threshold = threshold == null ? defaultThreshold : threshold;
		this.lostmass = lostmass;

		scanUtil = new NeutralScanUtil(scanlist, MSnCount);

		ms2filenameset = new HashSet<String>();
		ms3filenameset = new HashSet<String>();

		this.invalidChargems2names = new HashSet<String>();
		this.invalidChargems3names = new HashSet<String>();

		this.unNeutralMS2names = new HashSet<String>();
		this.unNeutralMS3names = new HashSet<String>();

		pairSet = new HashSet<ScanPair>();

		ms2scan = new ScanFileUtil(this.ms2file, extend);
		ms3scan = new ScanFileUtil(this.ms3file, extend);

		this.ms2names = this.getNames(ms2scan.getScanFileNameList());
		this.ms3names = this.getNames(ms3scan.getScanFileNameList());

		ms3scans = ms3scan.getScanNumList();
		this.total = ms3scans.length;
	}
	
	
	private String[] getNames(SequestScanName[] names) {
		
		if(names == null)
			return null;
		
		String[] snames = new String[names.length];
		
		for(int i=0; i<names.length; i++) {
			snames[i] = names[i].getScanName();
		}
		
		return snames;
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

	@Override
	public float completedPercent() {
		return (this.curtIdx + 1) / (float) this.total;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean hasNext() {
		return this.curtIdx + 1 < this.total;
	}

	@Override
	public boolean inDetermineable() {
		return false;
	}

	@Override
	public void processNext() {

		try {
			Integer scannum = ms3scans[++this.curtIdx];

			/*
			 * The last MS3 in spectrum may not be transfered to mzdata, then
			 * the ms2scan number will be -1. Currently, I can't process these
			 * scans, just leave them as the MS2 scan with no MS3
			 */
			int ms2scannum = scanUtil.getCorrespondMS2(scannum);
			short charge = scanUtil.getChargeStatebyNeutralLoss(scannum,
			        this.lostmass);

			/**
			 * The charge state is in determined
			 */
			if (charge == 0) {
				LinkedList<SequestScanName> ms2names = ms2scan
				        .getScanFilenames(ms2scannum);
				LinkedList<SequestScanName> ms3names = ms3scan
				        .getScanFilenames(scannum);

				if (ms2names != null && ms2names.size() > 0 && ms3names != null
				        && ms3names.size() > 0) {

					SequestScanName[] name2arr = new SequestScanName[MAX_CHARGE];
					SequestScanName[] name3arr = new SequestScanName[MAX_CHARGE];

					for (SequestScanName name : ms2names) {
						int c = name.getCharge();

						if (c >= MAX_CHARGE) {
							throw new IllegalArgumentException(
							        "The current charge is large than the max charge: "
							                + c);
						}

						if (name2arr[c] != null) {
							throw new IllegalArgumentException(
							        "The scan file for same scan number with same charge state: "
							                + name.getScanNumBeg() + ", " + c);
						}

						name2arr[c] = name;
					}

					for (SequestScanName name : ms3names) {
						int c = name.getCharge();

						if (c >= MAX_CHARGE) {
							throw new IllegalArgumentException(
							        "The current charge is large than the max charge: "
							                + c);
						}

						if (name3arr[c] != null) {
							throw new IllegalArgumentException(
							        "The scan file for same scan number with same charge state: "
							                + name.getScanNumBeg() + ", " + c);
						}

						name3arr[c] = name;
					}

					boolean isNeu = false;
					HashSet<Integer> chargeSet = null;
					for (int m = 1; m < MAX_CHARGE; m++) {

						SequestScanName name2m = name2arr[m];
						SequestScanName name3m = name3arr[m];

						if (name2m != null && name3m != null) {
							NeutralLossSequestScanDta dta = new NeutralLossSequestDtaReader(
							        new File(this.ms2file, name2m.getScanName()),
							        this.threshold, this.lostmass)
							        .getDtaFile(true);

							if (dta.isNeutralLoss()) {

								isNeu = true;
								
								if(chargeSet == null)
									chargeSet = new HashSet<Integer>();
								
								chargeSet.add(m);

								break;
							}
						}
					}

					for (int m = 1; m < MAX_CHARGE; m++) {
						SequestScanName name2m = name2arr[m];
						SequestScanName name3m = name3arr[m];

						if (isNeu) {
							if (chargeSet.contains(m)) {
								ms2filenameset.add(name2m.getScanName());
								ms3filenameset.add(name3m.getScanName());

								pairSet.add(new ScanPair(new File(this.ms2file,
								        name2m.getScanName()), new File(
								        this.ms3file, name3m.getScanName())));
							} else {
								if (name2m != null)
									invalidChargems2names.add(name2m
									        .getScanName());
								if (name3m != null)
									invalidChargems3names.add(name3m
									        .getScanName());
							}
						} else {
							if (name2m != null)
								unNeutralMS3names.add(name2m
								        .getScanName());
							if (name3m != null)
								unNeutralMS2names.add(name3m
								        .getScanName());
						}
					}
				}

			} else {
				SequestScanName ms2name = ms2scan.getScanFileName(ms2scannum,
				        charge);
				SequestScanName ms3name = ms3scan.getScanFileName(scannum,
				        charge);

				if (ms2name != null && ms3name != null) {

					NeutralLossSequestScanDta dta = new NeutralLossSequestDtaReader(
					        new File(this.ms2file, ms2name.getScanName()),
					        this.threshold, this.lostmass).getDtaFile(true);
					LinkedList<SequestScanName> ms2names = ms2scan
					        .getScanFilenames(ms2scannum);
					LinkedList<SequestScanName> ms3names = ms3scan
					        .getScanFilenames(scannum);

					if (dta.isNeutralLoss()) {
						ms2filenameset.add(ms2name.getScanName());
						ms3filenameset.add(ms3name.getScanName());

						pairSet.add(new ScanPair(new File(this.ms2file, ms2name
						        .getScanName()), new File(this.ms3file, ms3name
						        .getScanName())));

						/*
						 * Add to duplicated list
						 */

						for (SequestScanName nam : ms2names) {
							if (!nam.equals(ms2name))
								invalidChargems2names.add(nam.getScanName());
						}

						for (SequestScanName nam : ms3names) {
							if (!nam.equals(ms3name))
								invalidChargems3names.add(nam.getScanName());
						}
					} else {
						/*
						 * The neutral loss peak intensity lower than the
						 * threshold, and remove the ms3 spectrum.
						 */
						for(SequestScanName name : ms2names) {
							this.unNeutralMS2names.add(name.getScanName());
						}
						
						for(SequestScanName name : ms3names) {
							this.unNeutralMS3names.add(name.getScanName());
						}
					}
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	public HashSet<String> getInvalidChargeMS2Names() {
		if (!this.hasNext())
			return this.invalidChargems2names;
		else {
			System.err.println("The parsing has not finished.");
			return null;
		}
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
	public HashSet<String> getInvalidChargeMS3Names() {

		if (!this.hasNext())
			return this.invalidChargems3names;
		else {
			System.err.println("The parsing has not finished.");
			return null;
		}

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
		if (!this.hasNext())
			return this.corelatedms2names;
		else {
			System.err.println("The parsing has not finished.");
			return null;
		}

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
		if (!this.hasNext())
			return this.corelatedms2names.toArray(new String[0]);
		else {
			System.err.println("The parsing has not finished.");
			return null;
		}

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
		if (!this.hasNext())
			return this.corelatedms3names.toArray(new String[0]);
		else {
			System.err.println("The parsing has not finished.");
			return null;
		}
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
		if (!this.hasNext())
			return this.corelatedms3names;
		else {
			System.err.println("The parsing has not finished.");
			return null;
		}
	}

	/**
	 * The pair of MS2-MS3 with valid charge state.
	 * 
	 * @see #getCorrelatedMS2NameSet()
	 * @see #getCorrelatedMS3NameSet()
	 * 
	 * @return
	 */
	public HashSet<ScanPair> getScanPairs() {
		if (!this.hasNext())
			return this.pairSet;
		else {
			System.err.println("The parsing has not finished.");
			return null;
		}
	}

	/**
	 * The name of the MS3 dta files whose MS2 spectra don't contain significant
	 * neutral loss.
	 * 
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return
	 */
	public HashSet<String> getUnNeutralMS3NameSet() {
		if (!this.hasNext())
			return this.unNeutralMS3names;
		else {
			System.err.println("The parsing has not finished.");
			return null;
		}
	}

	/**
	 * The name of MS2 dta files which has MS3 spectra but doesn't contain
	 * significant neutral loss.
	 * 
	 * <p>
	 * <b>The names are dta names.</b>
	 * 
	 * @return
	 */
	public HashSet<String> getUnNeutralMS2NameSet() {
		if (!this.hasNext())
			return this.unNeutralMS2names;
		else {
			System.err.println("The parsing has not finished.");
			return null;
		}
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
		return this.ms2names;
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
		return this.ms3names;
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
