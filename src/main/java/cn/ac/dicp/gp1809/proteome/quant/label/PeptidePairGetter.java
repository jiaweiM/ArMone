/* 
 ******************************************************************************
 * File:FeaturesGetter.java * * * Created on 2010-9-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.QuanFeatureGetter;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import ipc.IPC;
import ipc.Peak;
import ipc.IPC.Options;
import ipc.IPC.Results;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 * 
 * @version 2010-9-15, 19:41:26
 */
public class PeptidePairGetter {

	protected LabelType type;
	protected AminoacidModification aamodif;
	// protected MS1PixelGetter pGetter;
	protected QuanFeatureGetter getter;
//	protected int leastIdenNum;

	protected HashMap<String, IPeptide> pepMap;

	protected HashMap<String, ArrayList<Integer>> seqScanMap;
	protected HashMap<Integer, Double> scoreMap;
	protected HashMap<String, double[]> massesMap;
	protected HashMap<String, HashSet<Integer>> labelTypeMap;
	// these peptides are not used in quantitation but only used for
	// identification of proteins.
	// the key is unique sequence.
	protected HashMap<String, IPeptide> idenPepMap;

	// key = ModSite and value = the symbol of the ModSite
	protected HashMap<ModSite, Character> firstSymbolMap;

	// key = the sequence of the peptide, value = different charges.
	protected HashMap<String, HashSet<Short>> seqChargeMap;
	protected int labelNum;
	protected String file;

	protected HashMap<String, double[]> isotopeMap;

	/**
	 * 0 = raw centroid 1 = wiff profile
	 */
	private int mzxmlType;

	public PeptidePairGetter(String peakFile, int mzxmlType) {
		try {
			if (mzxmlType == 0)
				this.getter = new QuanFeatureGetter(peakFile);
			// else if(mzxmlType==1)
			// this.getter = new QuanFeatureGetter2(peakFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.file = peakFile.substring(peakFile.lastIndexOf("\\")+1);
		this.pepMap = new HashMap<String, IPeptide>();
		this.seqScanMap = new HashMap<String, ArrayList<Integer>>();
		this.scoreMap = new HashMap<Integer, Double>();
		this.massesMap = new HashMap<String, double[]>();
		this.labelTypeMap = new HashMap<String, HashSet<Integer>>();
		this.firstSymbolMap = new HashMap<ModSite, Character>();
		this.idenPepMap = new HashMap<String, IPeptide>();
		this.seqChargeMap = new HashMap<String, HashSet<Short>>();
		this.isotopeMap = new HashMap<String, double[]>();
	}

	public PeptidePairGetter(String peakFile, LabelType type, int mzxmlType) {

		try {
			if (mzxmlType == 0)
				this.getter = new QuanFeatureGetter(peakFile);
			// else if(mzxmlType==1)
			// this.getter = new QuanFeatureGetter2(peakFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.type = type;
		this.file = peakFile.substring(peakFile.lastIndexOf("\\")+1);
		this.pepMap = new HashMap<String, IPeptide>();
		this.seqScanMap = new HashMap<String, ArrayList<Integer>>();
		this.scoreMap = new HashMap<Integer, Double>();
		this.massesMap = new HashMap<String, double[]>();
		this.labelTypeMap = new HashMap<String, HashSet<Integer>>();
		this.firstSymbolMap = new HashMap<ModSite, Character>();
		this.idenPepMap = new HashMap<String, IPeptide>();
		this.seqChargeMap = new HashMap<String, HashSet<Short>>();
		this.isotopeMap = new HashMap<String, double[]>();
		this.initial();
	}

	public PeptidePairGetter(String peakFile, LabelType type, int leastIdenNum,
			int mzxmlType) {

		try {
			if (mzxmlType == 0)
				this.getter = new QuanFeatureGetter(peakFile);
			// else if(mzxmlType==1)
			// this.getter = new QuanFeatureGetter2(peakFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.type = type;
		this.file = peakFile.substring(peakFile.lastIndexOf("\\")+1);
		this.pepMap = new HashMap<String, IPeptide>();
		this.seqScanMap = new HashMap<String, ArrayList<Integer>>();
		this.scoreMap = new HashMap<Integer, Double>();
		this.massesMap = new HashMap<String, double[]>();
		this.labelTypeMap = new HashMap<String, HashSet<Integer>>();
		this.firstSymbolMap = new HashMap<ModSite, Character>();
		this.idenPepMap = new HashMap<String, IPeptide>();
		this.seqChargeMap = new HashMap<String, HashSet<Short>>();
		this.isotopeMap = new HashMap<String, double[]>();
		this.initial();
	}

	public PeptidePairGetter(String peakFile, AminoacidModification aamodif,
			int mzxmlType) {

		try {

			if (mzxmlType == 0)
				this.getter = new QuanFeatureGetter(peakFile);
			// else if(mzxmlType==1)
			// this.getter = new QuanFeatureGetter2(peakFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.file = peakFile.substring(peakFile.lastIndexOf("\\")+1);
		this.aamodif = aamodif;
		this.pepMap = new HashMap<String, IPeptide>();
		this.seqScanMap = new HashMap<String, ArrayList<Integer>>();
		this.scoreMap = new HashMap<Integer, Double>();
		this.massesMap = new HashMap<String, double[]>();
		this.labelTypeMap = new HashMap<String, HashSet<Integer>>();
		this.firstSymbolMap = new HashMap<ModSite, Character>();
		this.idenPepMap = new HashMap<String, IPeptide>();
		this.seqChargeMap = new HashMap<String, HashSet<Short>>();
		this.isotopeMap = new HashMap<String, double[]>();
		this.initial();
	}

	public PeptidePairGetter(String peakFile, LabelType type, int leastIdenNum,
			AminoacidModification aamodif, int mzxmlType) {

		try {

			if (mzxmlType == 0)
				this.getter = new QuanFeatureGetter(peakFile);
			// else if(mzxmlType==1)
			// this.getter = new QuanFeatureGetter2(peakFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.type = type;
		this.file = peakFile.substring(peakFile.lastIndexOf("\\")+1);
		this.aamodif = aamodif;
		this.pepMap = new HashMap<String, IPeptide>();
		this.seqScanMap = new HashMap<String, ArrayList<Integer>>();
		this.scoreMap = new HashMap<Integer, Double>();
		this.massesMap = new HashMap<String, double[]>();
		this.labelTypeMap = new HashMap<String, HashSet<Integer>>();
		this.firstSymbolMap = new HashMap<ModSite, Character>();
		this.idenPepMap = new HashMap<String, IPeptide>();
		this.seqChargeMap = new HashMap<String, HashSet<Short>>();
		this.isotopeMap = new HashMap<String, double[]>();
		this.initial();
	}

	public void setLabelType(LabelType type) {
		this.type = type;
		this.initial();
	}

	public void setModif(AminoacidModification aamodif) {
		this.aamodif = aamodif;
	}

	public void initial() {
		LabelInfo[][] infos = type.getInfo();
		this.labelNum = infos.length;
		for (int i = 0; i < infos.length; i++) {
			for (int j = 0; j < infos[i].length; j++) {
				ModSite site = infos[i][j].getSite();
				char symbol = infos[i][j].getSymbol();
				if (symbol == '\u0000')
					continue;

				if (!firstSymbolMap.containsKey(site)) {
					firstSymbolMap.put(site, symbol);
				}
			}
		}
	}

	public void addPeptide(IPeptide peptide) {

		if (!peptide.isTP())
			return;

		int n = -1;
		boolean have0 = type.getInfo()[0].length == 0;
		LabelInfo[] info1 = type.getInfo()[0];
		for (int i = 0; i < info1.length; i++) {
			if (info1[i].getMass() == 0)
				have0 = true;
		}

		boolean nTerm = true;
		boolean proNTerm = false;
		ModSite site = null;
		ArrayList<ModSite> siteList = new ArrayList<ModSite>();

		StringBuilder sb = new StringBuilder();
		char[] pepChars = peptide.getSequence().toCharArray();

		if (have0) {
			sb.append(pepChars[0]);
			if (pepChars[0] == '-')
				proNTerm = true;

			for (int i = 1; i < pepChars.length - 1; i++) {
				if ((pepChars[i] >= 'A' && pepChars[i] <= 'Z')) {
					sb.append(pepChars[i]);
					site = ModSite.newInstance_aa(pepChars[i]);
					if (firstSymbolMap.containsKey(site)) {
						// sb.append(firstSymbolMap.get(site));
						siteList.add(site);
					}
				} else if (pepChars[i] == '.') {
					sb.append(pepChars[i]);
					if (nTerm) {
						if (proNTerm)
							site = ModSite.newInstance_ProNterm();
						else
							site = ModSite.newInstance_PepNterm();

						nTerm = false;
						if (firstSymbolMap.containsKey(site)) {
							// sb.append(firstSymbolMap.get(site));
							siteList.add(site);
						}
					} else {
						site = ModSite.newInstance_PepCterm();
						if (firstSymbolMap.containsKey(site)) {
							// sb.append(firstSymbolMap.get(site));
							siteList.add(site);
						}
					}
				} else if (pepChars[i] == '-') {

					sb.append(pepChars[i]);

				} else {

					float mass = (float) aamodif
							.getAddedMassForModif(pepChars[i]);
					int iso = type.getIsoIndex(site, mass);
					if (iso == -1)
						sb.append(pepChars[i]);

					else {
						if (n == -1) {
							n = iso;
						} else {
							if (n != iso && iso != -1) {
								// String unikey =
								// PeptideUtil.getUniqueSequence(peptide.getSequence());
								// this.idenPepMap.put(unikey, peptide);
								return;
							}
						}
					}
				}
			}
			sb.append(pepChars[pepChars.length - 1]);

			if (n == -1 && siteList.size() > 0)
				n = 0;

		} else {
			for (int i = 0; i < pepChars.length; i++) {
				if ((pepChars[i] >= 'A' && pepChars[i] <= 'Z')) {
					sb.append(pepChars[i]);
					site = ModSite.newInstance_aa(pepChars[i]);
				} else if (pepChars[i] == '.') {
					if (nTerm) {
						sb.append(pepChars[i]);
						if (proNTerm)
							site = ModSite.newInstance_ProNterm();
						else
							site = ModSite.newInstance_PepNterm();

						nTerm = false;
					} else {
						sb.append(pepChars[i]);
						site = ModSite.newInstance_PepCterm();
					}
				} else if (pepChars[i] == '-') {
					if (i == 0)
						proNTerm = true;

					sb.append(pepChars[i]);
				} else {
					float mass = (float) aamodif
							.getAddedMassForModif(pepChars[i]);
					int iso = type.getIsoIndex(site, mass);

					if (n == -1) {
						n = iso;
					} else {
						if (n != iso && iso != -1) {
							// String unikey =
							// PeptideUtil.getUniqueSequence(peptide.getSequence());
							// this.idenPepMap.put(unikey, peptide);
							return;
						}
					}
					if (firstSymbolMap.containsKey(site)) {
						// sb.append(firstSymbolMap.get(site));
						siteList.add(site);
					} else {
						sb.append(pepChars[i]);
					}
				}
			}
		}

		/**
		 * peptide sequence with variable modifications without term aminoacids.
		 */
		String uniseq = PeptideUtil.getSequence(sb.toString());
		if (this.labelTypeMap.containsKey(uniseq)) {
			this.labelTypeMap.get(uniseq).add(n);
		} else {
			HashSet<Integer> set = new HashSet<Integer>();
			set.add(n);
			this.labelTypeMap.put(uniseq, set);
		}
		if (n == -1) {
			this.idenPepMap.put(uniseq, peptide);
			return;
		}

		float score = peptide.getPrimaryScore();
		peptide.setSequence(sb.toString());

		if (seqChargeMap.containsKey(uniseq)) {

			seqChargeMap.get(uniseq).add(peptide.getCharge());
			if (score > pepMap.get(uniseq).getPrimaryScore()) {
				pepMap.put(uniseq, peptide);
			}
			seqScanMap.get(uniseq).add(peptide.getScanNumBeg());

		} else {

			HashSet<Short> chargeSet = new HashSet<Short>();
			chargeSet.add(peptide.getCharge());
			seqChargeMap.put(uniseq, chargeSet);
			pepMap.put(uniseq, peptide);

			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(peptide.getScanNumBeg());
			seqScanMap.put(uniseq, list);
		}

		char[] cs = uniseq.toCharArray();
		Arrays.sort(cs);
		String key = new String(cs) + "_" + peptide.getCharge();

		scoreMap.put(peptide.getScanNumBeg(),
				(double) peptide.getPrimaryScore());

		if (massesMap.containsKey(key)) {

			// seqScanMap.get(key2).add(peptide.getScanNumBeg());

		} else {

			double mz = peptide.getExperimentalMZ();
			short charge = peptide.getCharge();
			double[] masses = new double[labelNum];
			HashMap<ModSite, float[]> siteMassMap = type.getMassMap();
			for (int i = 0; i < siteList.size(); i++) {
				ModSite ss = siteList.get(i);
				float m = siteMassMap.get(ss)[n];
				mz = mz - m / charge;
			}
			Arrays.fill(masses, mz);

			for (int i = 0; i < masses.length; i++) {
				for (int j = 0; j < siteList.size(); j++) {
					masses[i] += siteMassMap.get(siteList.get(j))[i] / charge;
				}
			}
			massesMap.put(key, masses);
			// System.out.println(peptide.getSequence()+"\t"+mz+"\t"+Arrays.toString(masses));
			// ArrayList <Integer> list = new ArrayList <Integer>();
			// list.add(peptide.getScanNumBeg());
			// seqScanMap.put(key2, list);
		}
	}

	public HashMap<String, PeptidePair> getPeptidPairs() {

		Iterator<String> pepit = this.pepMap.keySet().iterator();
		while (pepit.hasNext()) {
			String key = pepit.next();
			String seq = PeptideUtil.getUniqueSequence(pepMap.get(key)
					.getSequence());
			if (seq.contains("X") || seq.contains("B") || seq.contains("U"))
				continue;

			if (!isotopeMap.containsKey(seq)) {

				IPC ipc = new IPC();
				Options ipcOptions = new Options();
				ipcOptions.addPeptide(seq);
				ipcOptions.setCharge(1);
				ipcOptions.setFastCalc(32);

				double[] intenMinusRatio = new double[6];
				int inteni = 0;
				Results res = ipc.execute(ipcOptions);
				TreeSet<ipc.Peak> isotopepeaks = res.getPeaks();
				Iterator<ipc.Peak> isotopepeaksit = isotopepeaks.iterator();
				while (isotopepeaksit.hasNext()) {
					Peak pp = isotopepeaksit.next();
					intenMinusRatio[inteni++] = pp.getP();
					if (inteni == 6)
						break;
				}
				isotopeMap.put(seq, intenMinusRatio);
			}
		}

		HashMap<String, PeptidePair> pairMap = new HashMap<String, PeptidePair>();
		Iterator<String> it = seqChargeMap.keySet().iterator();

		while (it.hasNext()) {

			String uniseq = it.next();
			IPeptide peptide = this.pepMap.get(uniseq);

			HashSet<Short> chargeSet = seqChargeMap.get(uniseq);
			Iterator<Short> chargeIt = chargeSet.iterator();

			char[] aas = uniseq.toCharArray();
			Arrays.sort(aas);

			while (chargeIt.hasNext()) {

				Short charge = chargeIt.next();
				String key = uniseq + "_" + charge;
				String key2 = new String(aas) + "_" + charge;

				// Integer [] scans = this.seqScanMap.get(key2).toArray(new
				// Integer [seqScanMap.get(key2).size()]);

				Integer[] scans = this.seqScanMap.get(uniseq).toArray(
						new Integer[seqScanMap.get(uniseq).size()]);
				Arrays.sort(scans);

				/*if (scans.length < leastIdenNum) {
					if (!pairMap.containsKey(key))
						this.idenPepMap.put(uniseq, peptide);
					continue;
				}*/

				double[] scores = new double[scans.length];
				for (int i = 0; i < scores.length; i++) {
					scores[i] = this.scoreMap.get(scans[i]);
				}

				double[] masses = massesMap.get(key2);

				LabelFeatures feas = null;
				String noModSeq = PeptideUtil.getUniqueSequence(uniseq);
//				if(noModSeq.contains("GSLSYLNVTR")){
//				if(noModSeq.contains("RIGPGEPLELLCNVSGALPPAGR")){
				if (isotopeMap.containsKey(noModSeq)) {
					feas = getter.getFeatures(charge, masses, scans, scores,
							isotopeMap.get(noModSeq));
				} else {
					feas = getter.getFeatures(charge, masses, scans, scores);
				}
				/*double[][] intens = feas.getIntenList();
				if(intens!=null){
					for(int i=0;i<intens.length;i++)
						System.out.println(uniseq+"\t"+i+"\t"+intens.length+"\t"+charge+"\t"+intens[i][0]+"\t"+intens[i][1]);
				}else{
					System.out.println("null\t"+charge+"\t"+uniseq);
				}
				*/
				PeptidePair pair = new PeptidePair(peptide, feas);
				pair.setSrc(file);
//				if (feas.isValidate()) {
//					System.out.println("validate\t"+charge+"\t"+uniseq);
				if(MathTool.getTotal(pair.getTotalIntens())==0){
					this.idenPepMap.put(uniseq, peptide);
				}else{
					pairMap.put(key, pair);
					double[][] intens = feas.getIntenList();
//					for(int i=0;i<intens.length;i++)
//					System.out.println(Arrays.toString(intens[i]));
				}
//				} else {
//					System.out.println("not\t"+charge+"\t"+uniseq);
//				}
//				}
			}
		}
		
		Iterator<String> it2 = pairMap.keySet().iterator();
		while(it2.hasNext()){
			String key = it2.next();
			String key2 = key.substring(0, key.indexOf("_"));
			if(this.idenPepMap.containsKey(key2)){
				this.idenPepMap.remove(key2);
			}
		}

		return pairMap;
	}

	public HashMap<String, IPeptide> getPepQuanMap() {
		return pepMap;
	}

	public HashMap<String, IPeptide> getIdenPepMap() {
		return idenPepMap;
	}

	public HashMap<String, HashSet<Integer>> getLabelTypeMap() {
		return labelTypeMap;
	}
	
	public LabelType getType() {
		return type;
	}

	/**
	 * 
	 */
	public void close() {
		// TODO Auto-generated method stub
		this.pepMap = null;
		this.massesMap = null;
		this.seqScanMap = null;
		this.firstSymbolMap = null;
		this.getter.close();
	}

	/**
	 * @return
	 */
	public String getOutPairFile() {
		// TODO Auto-generated method stub
		return (this.file + ".xml");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(Runtime.getRuntime().availableProcessors());
		OperatingSystemMXBean osBean = ManagementFactory
				.getOperatingSystemMXBean();

		String osArch = osBean.getArch();
		System.out.println(osArch);

		int numOfProcessors = osBean.getAvailableProcessors();
		System.out.println(numOfProcessors);

		double d = 0;
		for (int i = 0; i < 100000; i++) {
			double hp = 10;
			int count = 0;
			while (true) {
				double r = Math.random();
				int it = (int) (r * 10);
				if (it % 2 == 0) {
					hp -= 1;
				} else {
					hp -= 2;
				}
				count++;
				if (hp <= 0)
					break;
			}

			d += count;
		}

		System.out.println(d / 100000.0);

	}

}
