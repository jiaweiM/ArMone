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
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.APIVASEII.dtadistill.ScanPairParseTask.ScanPair;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosConstants;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.util.DtaPrecursorIonMassChanger;

/**
 * Only retain the paired spectra which contains neutral loss peak intensity
 * higher than specified in threshold
 * 
 * @author Xinning
 * @version 0.1.1, 06-04-2009, 14:26:27
 */
class IvSpRemovePairedRetainTask implements IInvalidSpectraRemoveTask {

	private File ms2file, ms3file;

	private DtaPrecursorIonMassChanger massChanger;

	private HashSet<String> pairedMS2;
	private HashSet<String> pairedMS3;
	private String[] ms2Names;
	private String[] ms3Names;

	private HashMap<String, ScanPair> ms3map;

	/**
	 * First remove the MS2 spectra, then the MS3 spectra
	 */
	private boolean removeMS2 = true;
	private boolean renewMzfromMS2;

	private int curtTotal = -1;
	private int total;
	private int ms2total;
	private int curt = -1;

	IvSpRemovePairedRetainTask(File ms2file, File ms3file,
	        HashSet<ScanPair> scanPairSet, String[] ms2Names, String[] ms3Names) {
		this(ms3file, ms3file, scanPairSet, ms3Names, ms3Names, false);
	}

	/**
	 * 
	 * @param ms2file
	 * @param ms3file
	 * @param pairedMS2
	 * @param pairedMS3
	 * @param ms2Names
	 * @param ms3Names
	 * @param renewMzfromMS2
	 *            if extend the high mass accuracy from the ms2 spectra? If
	 *            true, the mz value in ms3 will be reculated from the ms2
	 *            spectra.
	 */
	IvSpRemovePairedRetainTask(File ms2file, File ms3file,
	        HashSet<ScanPair> scanPairSet, String[] ms2Names,
	        String[] ms3Names, boolean renewMzfromMS2) {

		this.ms2file = ms2file;
		this.ms3file = ms3file;

		this.ms2Names = ms2Names;
		this.ms3Names = ms3Names;

		this.build(scanPairSet);

		this.ms2total = ms2Names.length;
		this.total = ms2Names.length + ms3Names.length;
		this.renewMzfromMS2 = renewMzfromMS2;

		if (renewMzfromMS2) {
			this.massChanger = new DtaPrecursorIonMassChanger();
		}
	}

	/**
	 * The map for fast access of scan pair by the ms3 file name
	 * 
	 * @param scanPairSet
	 * @return
	 */
	private void build(HashSet<ScanPair> scanPairSet) {

		this.ms3map = new HashMap<String, ScanPair>();
		this.pairedMS2 = new HashSet<String>();
		this.pairedMS3 = new HashSet<String>();
		for (ScanPair pair : scanPairSet) {

			String ms2name = pair.getMS2().getName();
			String ms3name = pair.getMS3().getName();

			ms3map.put(ms3name, pair);

			this.pairedMS2.add(ms2name);
			this.pairedMS3.add(ms3name);
		}
	}

	@Override
	public float completedPercent() {
		return (curtTotal + 1) / (float) total;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean hasNext() {
		return this.curtTotal + 1 < this.total;
	}

	@Override
	public boolean inDetermineable() {
		return false;
	}

	@Override
	public void processNext() {

		if (removeMS2) {
			if (curt + 1 < this.ms2total) {
				String name = ms2Names[++curt];
				if (!this.pairedMS2.contains(name)) {
					this.remove(ms2file, name);
				}

				this.curtTotal++;
			} else {
				this.removeMS2 = false;
				this.curt = -1;
			}
		} else {
			String name = ms3Names[++curt];
			if (!this.pairedMS3.contains(name)) {
				this.remove(ms3file, name);
			} else {
				//Renew the precursor mass
				if (this.renewMzfromMS2) {
					ScanPair pair = this.ms3map.get(name);
					this.renewMS3Precursor(pair.getMS2(), pair.getMS3());
				}
			}

			this.curtTotal++;
		}
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
	 * Renew the mass to extend the high mass accuracy from MS2 spectra
	 * 
	 * @param ms2file
	 * @param ms3file
	 */
	private void renewMS3Precursor(File ms2file, File ms3file) {
		try {
			SequestDtaReader reader = new SequestDtaReader(ms2file);
			IScanDta scandta = reader.getDtaFile(false);

			double ms3pre = scandta.getPrecursorMH()
			        - PhosConstants.PHOSPHATE_MASS;
			
			this.massChanger.changeMH(ms3file, ms3pre);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
