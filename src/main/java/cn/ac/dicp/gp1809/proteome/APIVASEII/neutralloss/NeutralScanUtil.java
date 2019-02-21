/*
 * *****************************************************************************
 * File: ScanUtil.java * * * Created on 11-01-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss;

import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2ScanList;

/**
 * Utilities to access the scan list from raw data
 * 
 * @author Xinning
 * @version 0.3.1, 06-04-2009, 15:39:45
 */
public class NeutralScanUtil {
	private MS2ScanList scanlist;
	private int MSnCount = 3;
	private boolean mzxml;

	//	public ScanUtil(MS2ScanList scanlist){
	//		this.scanlist = scanlist;
	//	}

	/**
	 * If the scan circle is in a alternative mode, that is MS2 followed by one
	 * MS3, just set the MSnCount as 1;
	 * 
	 * @param scanlist
	 *            a scan list from Raw file
	 * @param MSnCount
	 *            how many MSn scans in a MS circulation. e.g. for a MS circle :
	 *            MS MS2 MS2 MS2 MS3 MS3 MS3, this count is 3. Default MSnCount
	 *            == 3;
	 */
	public NeutralScanUtil(MS2ScanList scanlist, int MSnCount) {
		this.scanlist = scanlist;
		this.mzxml = scanlist.getDtaType() == DtaType.MZXML;

		if (this.mzxml) {

			System.out.println("For mzxml, because there is no sufficient "
			        + "information for MS2-MS3 pair, just get "
			        + "the previous MS2 scan for each MS3.");

			this.MSnCount = 1;

			System.out
			        .println("Set MSn count as 1. (MS1-MS2-MS3-MS2-MS3-----)");
		}

		if (MSnCount <= 0)
			throw new IllegalArgumentException(
			        "The number of MSn count must bigger than 0");

		this.MSnCount = MSnCount;
	}

	/**
	 * Get the corresponding MS2 scan number for the ms3 scan number. The
	 * judgment of which two scans are the pars is mainly based on the two scans
	 * with the same precursor ion. The MS3 scan MUST has two precursor ions.
	 * 
	 * @param ms3scannumber
	 * @return corresponding ms2 scannumber for the ms3, if find null, return 0;
	 */
	public int getCorrespondMS2(int ms3scannumber) {
		IMS2Scan ms2scan = this.getCorrespondMS2Scan(ms3scannumber);
		return ms2scan == null ? -1 : ms2scan.getScanNum();
	}

	/**
	 * Get the corresponding MS2 scan number for the ms3 scan number. The
	 * judgment of which two scans are the pars is mainly based on the two scans
	 * with the same precursor ion. The MS3 scan MUST has two precursor ions.
	 * 
	 * @param ms3scannumber
	 * @return corresponding ms2 scannumber for the ms3, return null if the ms2
	 *         scan cannot be found.
	 */
	public IMS2Scan getCorrespondMS2Scan(int ms3scannumber) {
		IMS2Scan ms3 = (IMS2Scan) scanlist.getScan(ms3scannumber);

		/*
		 * The last MS3 in spectrum may not be transfered to mzdata
		 */
		if (ms3 == null) {
			return null;
		}

		IMS2Scan ms2scan = null;

		if (this.mzxml) {
			ms2scan = (IMS2Scan) scanlist.getScan(ms3scannumber - 1);

			if (ms2scan.getMSLevel() != 2) {
				throw new IllegalArgumentException(
				        "For mzxml, because there is no enough "
				                + "information for MS2-MS3 pair, currently, only MS2 "
				                + "circle as (MS1-MS2-MS3-MS2-MS3-----) can be parsed. "
				                + "Please convert the raw file to mzdata and retry.");
			}

		} else {
			double ms3precursor = ms3.getPrecursorMZ(2);
			IMS2Scan[] scans = scanlist.getPreviousScans(ms3scannumber, MSnCount);

			for (int i = 0; i < scans.length; i++) {
				IMS2Scan ms = scans[i];

				//null find in the spectra;
				if (ms == null)
					continue;

				double ms2precursor = ms.getPrecursorMZ(2);

				if (ms2precursor == ms3precursor) {
					ms2scan = ms;
					break;
				}

			}
		}

		if (ms2scan == null) {
			System.err
			        .println("Warnning, cann't find corresponding ms2 for scan "
			                + ms3scannumber);
		}

		return ms2scan;
	}

	/**
	 * The scan pairs of MS2 and its MS3. The charge state for the scan will be
	 * calculated from the loss mass. However, for mzxml, because the
	 * information for MS2-MS3 is not sufficient for charge state evaluation,
	 * the charge state will be set as follows: for low mass accuracy mass
	 * spectra, the charge state will be set as 0; for high mass accuracy mass
	 * spectra, if the charge state have been determined by the readw algorithm,
	 * the charge state will be set as this charge state, other wise, set as 0.
	 * 
	 * @return
	 */
	public ScanPair[] getScanPairs(double lostmass) {
		LinkedList<ScanPair> list = new LinkedList<ScanPair>();
		IMS2Scan[] scans = scanlist.getScans();
		for (IMS2Scan scan : scans) {
			if (scan.getMSLevel() == 3) {
				int ms3scan = scan.getScanNum();
				IMS2Scan ms2scan = this.getCorrespondMS2Scan(ms3scan);

				if (ms2scan != null) {
					short charge = this.getChargeStatebyNeutralLoss(scan,
					        ms2scan, lostmass);

					if (charge == 0) {
						/*
						 * For high accuracy mass spectra in Mzxml, the charge
						 * state has been determined, just use this
						 */
						charge = ms2scan.getCharge();
					}

					list
					        .add(new ScanPair(ms2scan.getScanNum(), ms3scan,
					                charge));
				}
			}
		}

		return list.toArray(new ScanPair[list.size()]);
	}

	/**
	 * <b>If from mzxml file, the neutral loss will not be tested, equals to
	 * {@link #getScanPairs(double)}.</b>
	 * 
	 * <p>
	 * The scan pairs of MS2 and its MS3 which have valid neutral loss peaks in
	 * MS2. That is, the intensity of neutral loss peaks should bigger than the
	 * intensity threshold defined in the parameter. <b>Please make sure all the
	 * scans contains peak informations.</b>
	 * 
	 * @return
	 */
	public ScanPair[] getScanPairs_validLoss(ISpectrumThreshold threshold,
	        double lostmass) {
		LinkedList<ScanPair> list = new LinkedList<ScanPair>();
		IMS2Scan[] scans = scanlist.getScans();
		for (IMS2Scan scan : scans) {
			if (scan.getMSLevel() == 3) {
				int ms3scan = scan.getScanNum();
				IMS2Scan ms2scan = this.getCorrespondMS2Scan(ms3scan);
				if (ms2scan != null) {
					short charge = this.getChargeStatebyNeutralLoss(scan,
					        ms2scan, lostmass);

					if (charge == 0) {
						/*
						 * For high accuracy mass spectra in Mzxml, the charge
						 * state has been determined, just use this
						 */
						charge = ms2scan.getCharge();
					}

					if (charge == 0
					        || NeutralLossTest.testNeutralLoss(
					                ms2scan.getPeakList(), charge, threshold,
					                lostmass).isNeutralLoss()) {

						list.add(new ScanPair(ms2scan.getScanNum(), ms3scan,
						        charge));

					}
				}

			}
		}

		return list.toArray(new ScanPair[list.size()]);
	}

	/**
	 * <p>
	 * If Mzmxl file ,always return 0;
	 * 
	 * In some experiments (e.g. phosphopeptides with neutral loss), you can
	 * judge the charge state by the loss of neutral group if you know what will
	 * be lost because the charge state between the precursor ion and neutral
	 * loss peak is the same .
	 * 
	 * @param ms3scannumber
	 * @param lostmass
	 *            the lost mass (for phosphate, this value is 98)
	 * @return charge state for the scan specified, if null scan found, return
	 *         0;
	 */
	public short getChargeStatebyNeutralLoss(int ms3scannumber, double lostmass) {

		if (this.mzxml)
			return 0;

		IMS2Scan ms3 = (IMS2Scan) scanlist.getScan(ms3scannumber);

		if (ms3 == null)
			return 0;

		return this.getChargeStatebyNeutralLoss(ms3, lostmass);
	}

	/**
	 * <p>
	 * If Mzmxl file ,always return 0;
	 * 
	 * <b>Only for the MS3 scan of "data dependent neutral loss ms3 scans".
	 * <p>
	 * <b>Warning: <b>this method currently can only be used for MzData file
	 * type. Using the neutral loss mass and the neutral loss MZ value for the
	 * MS3 and MS2 to calculate the actual charge state of this can. If the scan
	 * is not a valid neutral loss MS3 event, return 0
	 * 
	 * @param neuloss
	 *            the mass of the neutral loss event. (e.g. for H3PO4 this value
	 *            if 98d)
	 * @return charge or 0 when this scan is not a ms3 scan;
	 */
	public short getChargeStatebyNeutralLoss(IMS2Scan ms3, double lostmass) {
		if (this.mzxml)
			return 0;

		short charge = 0;
		if (ms3.getMSLevel() == 3) {
			double mz2 = ms3.getPrecursorMZ(2);
			double mz3 = ms3.getPrecursorMZ(3);

			return this.getChargeStatebyNeutralLoss(mz2, mz3, lostmass);
		}
		return charge;
	}

	/**
	 * <p>
	 * If Mzmxl file ,always return 0;
	 * 
	 * <b>Only for the MS3 scan of "data dependent neutral loss ms3 scans". The
	 * MS2 precursor ion mz is get from the ms2 scan and the MS3 precursor ion
	 * mz is get from the ms3 scan
	 * 
	 * @param neuloss
	 *            the mass of the neutral loss event. (e.g. for H3PO4 this value
	 *            if 98d)
	 * @return charge or 0 when this scan is not a ms3 scan;
	 */
	public short getChargeStatebyNeutralLoss(IMS2Scan ms3, IMS2Scan ms2,
	        double lostmass) {

		if (this.mzxml)
			return 0;

		if (ms2 == null) {
			return this.getChargeStatebyNeutralLoss(ms3, lostmass);
		} else {

			double mz2 = ms2.getPrecursorMZ(2);
			double mz3 = ms3.getPrecursorMZ(3);

			return this.getChargeStatebyNeutralLoss(mz2, mz3, lostmass);
		}
	}

	/**
	 * The charge state from neutral loss.
	 * 
	 * @param mz2
	 * @param mz3
	 * @param lossmass
	 * @return
	 */
	private short getChargeStatebyNeutralLoss(double mz2, double mz3,
	        double lossmass) {

		if (mz2 == 0) {
			throw new NullPointerException("The mz value in ms2 is 0.");
		}

		if (mz3 == 0) {
			throw new NullPointerException("The mz value in ms3 is 0.");
		}

		double neurallossvscharge = mz2 - mz3;

		if (neurallossvscharge < 0)
			throw new IllegalArgumentException("The mz value of MS2 " + mz2
			        + " is smaller than " + mz3);

		return (short) Math.rint(lossmass / neurallossvscharge);
	}

	/**
	 * The scan pair of MS2 and its MS3 scan
	 * 
	 * @author Xinning
	 * @version 0.1, 02-25-2009, 19:53:57
	 */
	public static class ScanPair {
		private int ms2scan;
		private int ms3scan;
		private short charge;

		/**
		 * @param ms2scan
		 * @param ms3scan
		 * @param charge
		 */
		private ScanPair(int ms2scan, int ms3scan, short charge) {
			this.ms2scan = ms2scan;
			this.ms3scan = ms3scan;
			this.charge = charge;
		}

		/**
		 * @return the ms2scan
		 */
		public int getMs2scan() {
			return ms2scan;
		}

		/**
		 * @return the ms3scan
		 */
		public int getMs3scan() {
			return ms3scan;
		}

		/**
		 * @return the charge
		 */
		public short getCharge() {
			return charge;
		}
	}
}
