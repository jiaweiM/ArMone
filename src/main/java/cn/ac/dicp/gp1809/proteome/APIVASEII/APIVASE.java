/* 
 ******************************************************************************
 * File: APIVASE.java * * * Created on 02-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossTest;
import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralScanUtil;
import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralScanUtil.ScanPair;
import cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor.DefaultPhosphoPeptidePairMerger;
import cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor.IPhosphoPeptideMerger;
import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.AscorePhosPeptideConvertor;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListAccesser;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosConstants;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosPairParameterFacotry;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.RawSpectraReaderFactory;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2ScanList;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;

/**
 * APIVSE main class. The peptides will be splited into two parts: 1. the paired
 * (>50% or other set neutral loss percentage) MS2/MS3 with same phosphopeptide
 * identification. 2. Other MS2 peptides don't included in the first part.
 * 
 * <p>
 * Changes:
 * <li>0.1.2, 08-31-2009: Fix the {@link ArrayIndexOutOfBoundsException} when
 * the charge state is higher that Max_CHARGE, use ArrayList<String> instead
 * 
 * 
 * @author Xinning
 * @version 0.1.3, 05-25-2010, 14:22:38
 */
public class APIVASE {

	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/**
	 * The max charge state is 9
	 */
	private static final int MAX_CHARGE = 9;

	private double lostmass = PhosConstants.PHOSPHATE_MASS;

	private PeptideListAccesser ms2accesser, ms3accesser;
	private PeptideType type;

	/**
	 * Key = scan charge, value = the peak list for this scan and charge state
	 * is with significant neutral loss.
	 */
	private HashMap<String, Boolean> validNeutralLossMap;

	/**
	 * Key = scancharge value=index array list of peptide with this scan charge
	 */
	private HashMap<String, IntArrayList> pplmapms2;
	/**
	 * Key = scancharge value=index array list of peptide with this scan charge
	 */
	private HashMap<String, IntArrayList> pplmapms3;

	/**
	 * Key = scan number value = scancharge array of each charge state for this
	 * scan
	 */
	private HashMap<Integer, ArrayList<String>> pplChargeMap2;

	/**
	 * Key = scan number value = scancharge array of each charge state for this
	 * scan
	 */
	private HashMap<Integer, ArrayList<String>> pplChargeMap3;

	/**
	 * Key = ms2 scannum+"_"+charge; value = ScanPair of this scan number
	 */
	private HashMap<String, ScanPair> ms2scanPairMap;

	/**
	 * Only for the scan pair whose charge state is undetermined
	 * 
	 * Key = ms2 scannum; value = ScanPair of this scan number
	 */
	private HashMap<Integer, ScanPair> ms2scanNumPairMap;

	/*
	 * The scan number of scans that can give the identification of
	 * phosphopeptide pairs
	 */
	private HashSet<Integer> ms2scanPairNumSet;

	private MS2ScanList scanlist;
	private int MSnCount = 3;

	private ISpectrumThreshold threshold;

	private IPeptideListWriter writer, ms2neuWriter, ms3neuWriter;

	private AscorePhosPeptideConvertor ms2converter;

	private AscorePhosPeptideConvertor ms3converter;

	private String unPairedMS2Path;

	// the phospho peptide merger
	private IPhosphoPeptideMerger merger;
	/**
	 * Split into four parts: pair, MS2_on_neu, MS2_neu, MS3_neu,
	 */
	private boolean usFourParts = true;

	public APIVASE(String ms2ppl, String ms3ppl, String mzdata, DtaType type, String output, int MSnCount)
			throws FileDamageException, IOException, XMLStreamException, DtaFileParsingException {
		this(ms2ppl, ms3ppl, mzdata, type, output, MSnCount, SpectrumThreshold.HALF_INTENSE_THRESHOLD);
	}

	public APIVASE(String ms2ppl, String ms3ppl, String mzdata, DtaType type, String output, int MSnCount,
			ISpectrumThreshold threshold)
			throws FileDamageException, IOException, XMLStreamException, DtaFileParsingException {
		this.MSnCount = MSnCount;
		this.threshold = threshold;
		this.buildingScanPairMap(mzdata, type);
		this.buildingPPLIndexMap(ms2ppl, ms3ppl);

		this.prepareParsing(output);
	}

	/**
	 * Begin to process each entry.
	 * 
	 * @throws ProWriterException
	 * @throws PeptideParsingException
	 * @throws FileNotFoundException
	 */
	public void process() throws ProWriterException, PeptideParsingException, FileNotFoundException {

		logger.info("Scan pair size: " + ms2scanPairMap.size());

		this.printPairedPhosPeptides();

		this.printUnPairedMS2();

		this.ms2accesser.close();
		this.ms3accesser.close();

	}

	private void printPairedPhosPeptides() throws ProWriterException, PeptideParsingException {

		this.ms2scanPairNumSet = new HashSet<Integer>();

		Iterator<Entry<String, ScanPair>> iterator = ms2scanPairMap.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, ScanPair> entry = iterator.next();
			String ms2key = entry.getKey();
			ScanPair pair = entry.getValue();

			/*
			 * Only process the valid neutral loss scan
			 */
			Boolean valid = this.validNeutralLossMap.get(ms2key);
			/*
			 * May be this scan without valid peptide identification
			 */
			if (valid != null && valid)
				this.processPair(ms2key, pair);
		}

		/**
		 * If the charge state is undetermined, process the following peptides.
		 */
		Iterator<Entry<Integer, ScanPair>> it = this.ms2scanNumPairMap.entrySet().iterator();

		while (it.hasNext()) {
			Entry<Integer, ScanPair> entry = it.next();
			int scanNum = entry.getKey();
			ScanPair pair = entry.getValue();

			ArrayList<String> keys = this.pplChargeMap2.get(scanNum);

			if (keys != null) {
				for (String ms2key : keys) {

					/*
					 * Only process the valid neutral loss scan
					 */

					Boolean valid = this.validNeutralLossMap.get(ms2key);

					/*
					 * May be this scan without valid peptide identification
					 */
					if (valid != null && valid)
						this.processPair(ms2key, pair);
				}
			}
		}

		writer.close();

		if (this.usFourParts) {
			this.ms2neuWriter.close();
			this.ms3neuWriter.close();
		}
	}

	/**
	 * process the phospeptide pair and write to the file
	 * 
	 * @param ms2key commonly be the scan_charge
	 * @param pair
	 * @return
	 */
	private void processPair(String ms2key, ScanPair pair) {
		short charge = pair.getCharge();

		IntArrayList ms2Indeces = this.pplmapms2.get(ms2key);
		IntArrayList ms3Indeces = this.pplmapms3.get(this.getScanChargeKey(pair.getMs3scan(), charge));

		IPeptide[] pepsMS2 = new IPeptide[ms2Indeces == null ? 0 : ms2Indeces.size()];
		IPeptide[] pepsMS3 = new IPeptide[ms3Indeces == null ? 0 : ms3Indeces.size()];

		for (int i = 0; i < pepsMS2.length; i++) {
			pepsMS2[i] = this.ms2accesser.getPeptide(ms2Indeces.get(i));
		}

		for (int i = 0; i < pepsMS3.length; i++) {
			pepsMS3[i] = this.ms3accesser.getPeptide(ms3Indeces.get(i));
		}

		int scanms2 = pair.getMs2scan();
		int scanms3 = pair.getMs3scan();

		IPhosPeptidePair ppair = this.merger.merge(scanms2, scanms3, charge, pepsMS2, pepsMS3,
				(IMS2PeakList) this.scanlist.getScan(scanms2).getPeakList(),
				(IMS2PeakList) this.scanlist.getScan(scanms3).getPeakList());

		if (ppair != null) {
			this.writer.write(ppair, new IMS2PeakList[] { this.ms2accesser.getPeakLists(ms2Indeces.get(0))[0],
					this.ms3accesser.getPeakLists(ms3Indeces.get(0))[0] });
			this.ms2scanPairNumSet.add(scanms2);
		} else {
			if (this.usFourParts) {
				/*
				 * Print the peptides with significant neutral loss but without
				 * matched peptides for MS2 and MS3
				 */

				if (pepsMS2.length > 0) {

					IPeptide pep = pepsMS2[0];
					IMS2PeakList[] peaklists = this.ms2accesser.getPeakLists(ms2Indeces.get(0));

					boolean converted = ms2converter.convert(pep, peaklists[0], threshold);

					if (converted)
						this.ms2neuWriter.write(pep, peaklists);
				}

				if (pepsMS3.length > 0) {

					IPeptide pep = pepsMS3[0];
					IMS2PeakList[] peaklists = this.ms3accesser.getPeakLists(ms3Indeces.get(0));

					boolean converted = ms3converter.convert(pep, peaklists[0], threshold);

					if (converted)
						this.ms3neuWriter.write(pep, peaklists);
				}

				this.ms2scanPairNumSet.add(scanms2);
			}
		}
	}

	/**
	 * Print the top matched unpaired MS2 peptide identifications
	 * 
	 * @param output
	 * @throws FileNotFoundException
	 * @throws ProWriterException
	 * @throws PeptideParsingException
	 * @throws FileNotFoundException
	 */
	private void printUnPairedMS2() throws ProWriterException, PeptideParsingException, FileNotFoundException {

		IPeptideListWriter unPairedMS2Writer = new PeptideListWriter(this.unPairedMS2Path,
				ms2converter.getAscorePeptideFormat(), ms2converter.getAscoreParameter(),
				this.ms2accesser.getDecoyJudger(), true, ms2accesser.getProNameAccesser());

		Iterator<Entry<String, IntArrayList>> it = pplmapms2.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, IntArrayList> entry = it.next();
			String key = entry.getKey();

			int scan = this.parseScanChargeKey(key);

			if (!this.ms2scanPairNumSet.contains(scan)) {
				IntArrayList list = entry.getValue();
				int size = list.size();

				for (int i = 0; i < size; i++) {
					int idx = list.get(i);

					IPeptide pep = this.ms2accesser.getPeptide(idx);
					IMS2PeakList[] peaklists = this.ms2accesser.getPeakLists(idx);

					boolean converted = ms2converter.convert(pep, peaklists[0], threshold);

					if (converted)
						unPairedMS2Writer.write(pep, peaklists);
				}
			}
		}

		unPairedMS2Writer.close();
	}

	/**
	 * Prepare the needed terms
	 * 
	 * @param type
	 * @param output
	 * @throws FileNotFoundException
	 */
	private void prepareParsing(String output) throws FileNotFoundException {
		ISearchParameter ms2param = this.ms2accesser.getSearchParameter();
		ISearchParameter ms3param = this.ms3accesser.getSearchParameter();

		ProteinNameAccesser proNameAccesser = ms2accesser.getProNameAccesser();
		proNameAccesser.appand(ms3accesser.getProNameAccesser());

		merger = new DefaultPhosphoPeptidePairMerger(ms2param.getStaticInfo(), ms3param.getStaticInfo(),
				ms2param.getVariableInfo(), ms3param.getVariableInfo(), this.threshold, this.type);

		ISearchParameter pairParam = PhosPairParameterFacotry.createPairedParameter(this.ms2accesser.getPeptideType(),
				merger.getAminoacids(), merger.getModificationNewSymbol(), ms2param.getEnzyme(), ms2param.getDatabase(),
				ms2param.isMonoPeptideMass());

		ms2converter = new AscorePhosPeptideConvertor(this.ms2accesser.getSearchParameter(),
				this.ms2accesser.getPeptideType(), this.ms2accesser.getPeptideFormat());

		ms3converter = new AscorePhosPeptideConvertor(this.ms3accesser.getSearchParameter(),
				this.ms3accesser.getPeptideType(), this.ms3accesser.getPeptideFormat());

		writer = new PeptideListWriter(output, merger.getPeptideFormatter(), pairParam,
				this.ms2accesser.getDecoyJudger(), proNameAccesser);

		int idx = output.lastIndexOf('.');
		String base = output.substring(0, idx);
		String extendsion = output.substring(idx);
		this.unPairedMS2Path = base + ".nonNeutral" + extendsion;

		if (this.usFourParts) {
			String ms2neuPath = base + ".neuMS2" + extendsion;
			String ms3neuPath = base + ".neuMS3" + extendsion;
			this.ms2neuWriter = new PeptideListWriter(ms2neuPath, ms2converter.getAscorePeptideFormat(),
					ms2converter.getAscoreParameter(), this.ms2accesser.getDecoyJudger(), proNameAccesser);
			this.ms3neuWriter = new PeptideListWriter(ms3neuPath, ms3converter.getAscorePeptideFormat(),
					ms3converter.getAscoreParameter(), this.ms3accesser.getDecoyJudger(), proNameAccesser);
		}
	}

	/**
	 * The valid scan pairs from MzData file
	 * 
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException
	 */
	private void buildingScanPairMap(String mzdata, DtaType type)
			throws FileNotFoundException, XMLStreamException, DtaFileParsingException {

		System.out.println("Building scan pair index map ...");

		IRawSpectraReader reader = RawSpectraReaderFactory.createReader(type, mzdata);
		reader.rapMS2ScanList();

		scanlist = reader.getMS2ScanList();

		NeutralScanUtil scanutil = new NeutralScanUtil(scanlist, MSnCount);
		ScanPair[] pairs = scanutil.getScanPairs(lostmass);

		ms2scanPairMap = new HashMap<String, ScanPair>();
		this.ms2scanNumPairMap = new HashMap<Integer, ScanPair>();

		for (ScanPair pair : pairs) {
			short charge = pair.getCharge();
			if (charge == 0) {
				ms2scanNumPairMap.put(pair.getMs2scan(), pair);
			} else
				ms2scanPairMap.put(this.getScanChargeKey(pair.getMs2scan(), pair.getCharge()), pair);
		}

		System.out.println("Finished building scan pair index map ...");
	}

	/**
	 * The map for peptide list files
	 * 
	 * @throws FileDamageException
	 * @throws IOException
	 */
	private void buildingPPLIndexMap(String ms2ppl, String ms3ppl) throws FileDamageException, IOException {
		System.out.println("Begin to build peptide index ...");

		pplmapms2 = new HashMap<String, IntArrayList>();
		pplmapms3 = new HashMap<String, IntArrayList>();

		this.pplChargeMap2 = new HashMap<Integer, ArrayList<String>>();
		this.pplChargeMap3 = new HashMap<Integer, ArrayList<String>>();

		this.validNeutralLossMap = new HashMap<String, Boolean>();

		this.buildingMap(ms2ppl, this.pplmapms2, this.pplChargeMap2, validNeutralLossMap);
		this.buildingMap(ms3ppl, this.pplmapms3, this.pplChargeMap3);

		this.ms2accesser = new PeptideListAccesser(ms2ppl);
		this.ms3accesser = new PeptideListAccesser(ms3ppl);

		this.type = this.ms2accesser.getPeptideType();

		logger.info("Finished building peptide index.");
	}

	/**
	 * The MS3 map
	 * 
	 * @param ppl
	 * @param pplmap
	 * @param pplchargemap
	 * @param neutralMap whether or not a
	 * @return
	 * @throws FileDamageException
	 * @throws IOException
	 */
	private void buildingMap(String ppl, HashMap<String, IntArrayList> pplmap,
			HashMap<Integer, ArrayList<String>> pplchargemap) throws FileDamageException, IOException {

		PeptideListReader reader = new PeptideListReader(ppl);

		IPeptide pep;
		while ((pep = reader.getPeptide()) != null) {
			int scanbeg = pep.getScanNumBeg();
			int scanend = pep.getScanNumEnd();
			short charge = pep.getCharge();
			int idx_ppl = reader.getCurtPeptideIndex();

			if (scanbeg == scanend) {
				this.prcessPeptideMap(scanbeg, charge, idx_ppl, pplmap, pplchargemap);

			} else {
				this.prcessPeptideMap(scanbeg, charge, idx_ppl, pplmap, pplchargemap);
				this.prcessPeptideMap(scanend, charge, idx_ppl, pplmap, pplchargemap);
			}
		}

		reader.close();
	}

	/**
	 * 
	 * The MS2 map
	 * 
	 * @param ppl
	 * @param pplmap
	 * @param pplchargemap
	 * @param neutralMap whether or not a
	 * @return
	 * @throws FileDamageException
	 * @throws IOException
	 */
	private void buildingMap(String ppl, HashMap<String, IntArrayList> pplmap,
			HashMap<Integer, ArrayList<String>> pplchargemap, HashMap<String, Boolean> neutralMap)
			throws FileDamageException, IOException {

		PeptideListReader reader = new PeptideListReader(ppl);

		IPeptide pep;
		while ((pep = reader.getPeptide()) != null) {
			int scanbeg = pep.getScanNumBeg();
			int scanend = pep.getScanNumEnd();
			short charge = pep.getCharge();
			int idx_ppl = reader.getCurtPeptideIndex();

			if (scanbeg == scanend) {
				this.prcessPeptideMap(scanbeg, charge, idx_ppl, pplmap, pplchargemap);

				String scancharge = this.getScanChargeKey(scanbeg, charge);

				if (neutralMap.get(scancharge) == null) {
					neutralMap.put(scancharge, this.testNeutralLoss(reader.getPeakLists()[0], charge));
				}

			} else {
				this.prcessPeptideMap(scanbeg, charge, idx_ppl, pplmap, pplchargemap);
				this.prcessPeptideMap(scanend, charge, idx_ppl, pplmap, pplchargemap);

				String scancharge = this.getScanChargeKey(scanbeg, charge);
				boolean tested = false;
				boolean neu = false;

				if (neutralMap.get(scancharge) == null) {

					neu = this.testNeutralLoss(reader.getPeakLists()[0], charge);
					tested = true;

					neutralMap.put(scancharge, neu);
				}

				scancharge = this.getScanChargeKey(scanend, charge);

				if (neutralMap.get(scancharge) == null) {

					if (!tested) {
						neu = this.testNeutralLoss(reader.getPeakLists()[0], charge);
					}

					neutralMap.put(scancharge, neu);
				}
			}
		}

		reader.close();
	}

	/**
	 * Parse current peptide into the map
	 * 
	 * @param scan
	 * @param charge
	 * @param index_ppl
	 * @param pplmap
	 * @param pplchargemap
	 */
	private void prcessPeptideMap(int scan, short charge, int index_ppl, HashMap<String, IntArrayList> pplmap,
			HashMap<Integer, ArrayList<String>> pplchargemap) {
		String key = this.getScanChargeKey(scan, charge);
		IntArrayList list;
		if ((list = pplmap.get(key)) == null) {
			list = new IntArrayList();
			pplmap.put(key, list);
		}

		list.add(index_ppl);

		/*
		 * The charges
		 */
		ArrayList<String> scanCharges;
		if ((scanCharges = pplchargemap.get(scan)) == null) {
			scanCharges = new ArrayList<String>();
			pplchargemap.put(scan, scanCharges);
		}

		/*
		 * Here we assume the scan charge is unique,that is, one charge state
		 * corresponds to one scan charge
		 */
		scanCharges.add(key);
	}

	/**
	 * Test the neutral loss
	 * 
	 * @param peaklist
	 * @param charge
	 * @return
	 */
	private boolean testNeutralLoss(IMS2PeakList peaklist, short charge) {
		return NeutralLossTest.testNeutralLoss(peaklist, charge, threshold, lostmass).isNeutralLoss();
	}

	/**
	 * Get the key for getting by scan and charge
	 * 
	 * @param scan
	 * @param charge
	 * @return
	 */
	protected final String getScanChargeKey(int scan, short charge) {
		return scan + "_" + charge;
	}

	/**
	 * Get the scan number from the scan charge key
	 * 
	 * @param scancharge
	 * @return
	 */
	protected final int parseScanChargeKey(final String scancharge) {
		return Integer.parseInt(scancharge.substring(0, scancharge.lastIndexOf('_')));
	}

	private static String usage() {
		return "APIVASE ms2ppl ms3ppl mzdata outputppl";
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws FileDamageException
	 * @throws ProWriterException
	 * @throws PeptideParsingException
	 * @throws XMLStreamException
	 * @throws DtaFileParsingException
	 */
	public static void main(String[] args) throws ProWriterException, FileDamageException, IOException,
			PeptideParsingException, XMLStreamException, DtaFileParsingException {
		if (args.length != 4) {
			System.out.println(usage());
		} else {
			new APIVASE(args[0], args[1], args[2], DtaType.MZDATA, args[3], 3).process();
			System.gc();
		}
	}
}
