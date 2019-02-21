/* 
 ******************************************************************************
 * File: ProteinPilotPeptideReader.java * * * Created on 2013-6-6
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.ProteinPilot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ck
 * 
 * @version 2013-6-6, 15:19:45
 */
public class ProteinPilotPeptideReader {

	private final double Deamidated = 0.984016;
	private BufferedReader reader;
	private double scoreThres;
	private ProteinPilotPeptide[] peptides;

	public ProteinPilotPeptideReader(String file) throws IOException {
		this.reader = new BufferedReader(new FileReader(file));
		this.initial();
		this.close();
	}

	public ProteinPilotPeptideReader(File file) throws IOException {
		this.reader = new BufferedReader(new FileReader(file));
		this.initial();
		this.close();
	}

	public ProteinPilotPeptideReader(String file, double scoreThres)
			throws IOException {
		this.reader = new BufferedReader(new FileReader(file));
		this.scoreThres = scoreThres;
		this.initial();
		this.close();
	}

	public ProteinPilotPeptideReader(File file, double scoreThres)
			throws IOException {
		this.reader = new BufferedReader(new FileReader(file));
		this.scoreThres = scoreThres;
		this.initial();
		this.close();
	}

	private void initial() throws IOException {

		String line = reader.readLine();
		String[] title = line.split("\t");
		int accid = -1;
		int nameid = -1;
		int confid = -1;
		int sequenceid = -1;
		int modid = -1;
		int precmwid = -1;
		int precmzid = -1;
		int theormwid = -1;
		int theormzid = -1;
		int scannumid = -1;
		int chargeid = -1;
		int rtid = -1;

		for (int i = 0; i < title.length; i++) {

			if (title[i].equals("Accessions")) {
				accid = i;
			} else if (title[i].equals("Names")) {
				nameid = i;
			} else if (title[i].equals("Conf")) {
				confid = i;
			} else if (title[i].equals("Sequence")) {
				sequenceid = i;
			} else if (title[i].equals("Modifications")) {
				modid = i;
			} else if (title[i].equals("Prec MW")) {
				precmwid = i;
			} else if (title[i].equals("Prec m/z")) {
				precmzid = i;
			} else if (title[i].equals("Theor MW")) {
				theormwid = i;
			} else if (title[i].equals("Theor m/z")) {
				theormzid = i;
			} else if (title[i].equals("Theor z")) {
				chargeid = i;
			} else if (title[i].equals("Spectrum")) {
				scannumid = i;
			} else if (title[i].equals("Time")) {
				rtid = i;
			}
		}

		ArrayList<ProteinPilotPeptide> peplist = new ArrayList<ProteinPilotPeptide>();
		while ((line = reader.readLine()) != null) {
			String[] cs = line.split("\t");
			double conf = Double.parseDouble(cs[confid]);
			if (conf < scoreThres)
				continue;

			HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
			if (cs[modid].length() > 0) {
				String[] mods = cs[modid].split(";[\\W]*");
				for (int i = 0; i < mods.length; i++) {
					String[] mm = mods[i].split("[@\\(\\)]");
					if (mm.length == 2) {
						if (map.containsKey(mm[0])) {
							map.get(mm[0]).add(mm[1]);
						} else {
							ArrayList<String> list = new ArrayList<String>();
							list.add(mm[1]);
							map.put(mm[0], list);
						}
					} else if (mm.length == 4) {
						if (map.containsKey(mm[0])) {
							map.get(mm[0]).add(mm[3]);
						} else {
							ArrayList<String> list = new ArrayList<String>();
							list.add(mm[3]);
							map.put(mm[0], list);
						}
					} else {
						// System.out.println(mm.length+"\t"+Arrays.toString(mm));
					}
				}
			}

			ProteinPilotPeptide peptide = new ProteinPilotPeptide(cs[accid],
					cs[nameid], conf, cs[sequenceid], map,
					Double.parseDouble(cs[precmwid]),
					Double.parseDouble(cs[precmzid]),
					Double.parseDouble(cs[theormwid]),
					Double.parseDouble(cs[theormzid]),
					Integer.parseInt(cs[chargeid]), cs[scannumid],
					Double.parseDouble(cs[rtid]));

			peplist.add(peptide);
		}

		this.peptides = peplist
				.toArray(new ProteinPilotPeptide[peplist.size()]);

	}

	private void close() throws IOException {
		this.reader.close();
	}

	public ProteinPilotPeptide[] getNGlycoPeptides() {

		HashMap<String, ProteinPilotPeptide> map = new HashMap<String, ProteinPilotPeptide>();
		Pattern pattern = Pattern.compile("N[A-OQ-Z][ST]");

		for (int i = 0; i < peptides.length; i++) {
			String sequence = peptides[i].getSequence();
			HashMap<String, ArrayList<String>> modmap = peptides[i].getModmap();
			if (!modmap.containsKey("Deamidated"))
				continue;
			
			Matcher matcher = pattern.matcher(sequence);
			if(!matcher.find())
				continue;

			double peptideBackboneMw = peptides[i].getTheorMw();
			boolean ng = false;
			ArrayList<String> locs = modmap.get("Deamidated");
			for (int j = 0; j < locs.size(); j++) {
				if (sequence.charAt(Integer.parseInt(locs.get(j)) - 1) == 'N') {
					ng = true;
					peptideBackboneMw -= Deamidated;
				}
			}

			if (!ng)
				continue;

			peptides[i].setPeptideBackboneMw(peptideBackboneMw);
			String[] mods = modmap.keySet().toArray(new String[modmap.size()]);
			Arrays.sort(mods);

			StringBuilder sb = new StringBuilder();
			sb.append(sequence + ";");
			for (int j = 0; j < mods.length; j++) {
				sb.append(mods[j]).append("_")
						.append(modmap.get(mods[j]).size()).append(";");
			}

			String key = sb.toString();
			if (map.containsKey(key)) {
				if (peptides[i].getConf() > map.get(key).getConf()) {
					map.put(key, peptides[i]);
				}
			} else {
				map.put(key, peptides[i]);
			}

		}

		ProteinPilotPeptide[] peps = map.values().toArray(
				new ProteinPilotPeptide[map.size()]);

		Arrays.sort(peps, new Comparator<ProteinPilotPeptide>() {

			@Override
			public int compare(ProteinPilotPeptide arg0,
					ProteinPilotPeptide arg1) {
				// TODO Auto-generated method stub

				if (arg0.getPeptideBackboneMw() < arg1.getPeptideBackboneMw()) {
					return -1;
				} else if (arg0.getPeptideBackboneMw() > arg1
						.getPeptideBackboneMw()) {
					return 1;
				}

				return 0;
			}

		});

		return peps;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		ProteinPilotPeptideReader reader = new ProteinPilotPeptideReader(
				"H:\\20130519_glyco\\iden\\AB\\Centroid_Rui_20130515_fetuin_HILIC_deglyco_HCD_AB_PeptideSummary.txt",
				95);
		reader.getNGlycoPeptides();
	}

}
