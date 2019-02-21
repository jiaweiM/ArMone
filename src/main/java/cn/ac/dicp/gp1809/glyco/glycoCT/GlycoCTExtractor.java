/* 
 ******************************************************************************
 * File: GlycoCTExtractor.java * * * Created on 2012-3-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.glycoCT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.Combinator;

/**
 * @author ck
 * 
 * @version 2012-3-20, 10:16:48
 */
public class GlycoCTExtractor {

	private static double[] mono = new double[] { 12.0, 1.0078250319, 14.0030740074, 15.9949146223, 31.97207073,
			18.99840320, 34.96885271, 78.9183379, 126.904468, 30.97376149 };

	private static double[] ave = new double[] { 12.0107, 1.00794, 14.0067, 15.9994, 32.065, 18.9984032, 35.453, 79.904,
			126.90447, 30.973761 };

	private static DecimalFormat df5 = DecimalFormats.DF0_5;

	public GlycoCTExtractor() throws IOException {}

	private static void writeNGlycan(String dir, String output) throws IOException {

		File[] files = new File(dir).listFiles();
		PrintWriter pw = new PrintWriter(output);

		for (int i = 0; i < files.length; i++) {

			GlycoTree tree = readNGlycan(files[i]);
			if (tree != null && tree.isNGlycan()) {
				pw.write(tree.getGlycoCT());
				pw.write("\n");
			}
		}

		pw.close();
	}
	
	/**
	 * read the glyct txt file.
	 */
	private static GlycoTree readNGlycan(File file) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(file));
		boolean res = false;
		boolean lin = false;
		StringBuilder sb = new StringBuilder();

		GlycoTree gtree = new GlycoTree();

		String line = null;
		while ((line = reader.readLine()) != null) {

			sb.append(line).append("\n");

			if (line.startsWith("RES")) {

				res = true;
				continue;

			} else if (line.startsWith("LIN")) {

				lin = true;
				res = false;
				continue;

			} else if (line.startsWith("REP")) {
				reader.close();
				return null;

			} else if (line.startsWith("ALT")) {
				reader.close();
				return null;

			} else if (line.startsWith("UND")) {
				reader.close();
				return null;

			} else if (line.startsWith("ISO")) {
				reader.close();
				return null;

			} else {

				if (res) {

					int beg = line.indexOf(":");
					
					String id = line.substring(0, beg - 1);
					String typejudeg = line.substring(0, beg);
					String content = line.substring(beg + 1);

					if (typejudeg.endsWith("b")) {

						GlycoTreeNode node = new GlycoTreeNode(id, content);
						gtree.addNode(id, node);

					} else if (typejudeg.endsWith("s")) {

						gtree.addSub(id, content);

					} else {
						reader.close();
						return null;
					}

				} else if (lin) {

					String[] ss = line.split("[:()+]");
					String parentid = ss[1].substring(0, ss[1].length() - 1);
					String childid = ss[4].substring(0, ss[4].length() - 1);
					char parentLinkType = ss[1].charAt(ss[1].length() - 1);
					char childLinkType = ss[4].charAt(ss[3].length() - 1);
					String linkPosition1 = ss[2];
					String linkPosition2 = ss[3];

					gtree.addLink(parentid, childid, parentLinkType, childLinkType, linkPosition1, linkPosition2);
				}
			}
		}

		reader.close();
		gtree.setGlycoCT(sb.toString());

		return gtree;
	}

	public static double[] getMonoList(String in) throws IOException {

		ArrayList<Double> list = new ArrayList<Double>();

		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("Mono")) {
				double mass = Double.parseDouble(line.split(":")[1]);
				list.add(mass);
			}
		}
		reader.close();
		double[] arrays = new double[list.size()];
		for (int i = 0; i < arrays.length; i++) {
			arrays[i] = list.get(i);
		}

		Arrays.sort(arrays);
		return arrays;
	}

	private static void traverse(GlycoTreeNode node, HashSet<Double> peakset, HashMap<String, String> monomap,
			double mass) {

		ArrayList<GlycoTreeNode> childlist = node.getChildNodeList();
		for (int i = 0; i < childlist.size(); i++) {

			GlycoTreeNode child = childlist.get(i);
			String des = child.getGlycoCTName();
			Iterator<String> monoit = monomap.keySet().iterator();

			while (monoit.hasNext()) {

				String monokey = monoit.next();
				Pattern monop = Pattern.compile(monokey);
				Matcher monom = monop.matcher(des);
				if (monom.matches()) {
					// System.out.println("match\t"+des);
					mass += Double.parseDouble(monomap.get(monokey));
					peakset.add(mass);
					break;
				}
			}

			traverse(child, peakset, monomap, mass);
		}
	}

	private static double getChildMass(GlycoTreeNode node, HashMap<String, Double> monomap, double mass) {

		ArrayList<GlycoTreeNode> childlist = node.getChildNodeList();
		for (int i = 0; i < childlist.size(); i++) {

			GlycoTreeNode child = childlist.get(i);
			String des = child.getGlycoCTName();
			mass += monomap.get(des);
			mass = getChildMass(child, monomap, mass);
		}
		// System.out.println(mass);
		return mass;
	}

	private static double getParentMass(GlycoTreeNode node, HashMap<String, String> monomap, double mass) {

		String des = node.getGlycoCTName();
		Iterator<String> monoit = monomap.keySet().iterator();

		while (monoit.hasNext()) {

			String monokey = monoit.next();
			Pattern monop = Pattern.compile(monokey);
			Matcher monom = monop.matcher(des);
			if (monom.matches()) {
				// System.out.println("match\t"+des);
				mass += Double.parseDouble(monomap.get(monokey));
				break;
			}
		}

		GlycoTreeNode parent = node.getParentNode();
		if (parent == null) {
			return mass;
		} else {
			return getParentMass(parent, monomap, mass);
		}
	}

	/*
	 * private static void judgeNGlycan(String in, String out, HashMap<String,
	 * Double> monomap) throws IOException {
	 * 
	 * PrintWriter pw = new PrintWriter(out); BufferedReader reader = new
	 * BufferedReader(new FileReader(in)); boolean res = false; boolean lin =
	 * false;
	 * 
	 * GlycoTree gtree = null; int count = 0; StringBuilder sb = new
	 * StringBuilder(); String line = null;
	 * 
	 * L: while ((line = reader.readLine()) != null) {
	 * 
	 * if (line.trim().length() == 0) {
	 * 
	 * if (gtree != null) {
	 * 
	 * count++; if (count % 500 == 0) { System.out.println(count); }
	 * HashMap<String, GlycoTreeNode> nodemap = gtree.getNodeMap(); if
	 * (nodemap.size() < 5) { sb = new StringBuilder(); continue; }
	 * 
	 * GlycoTreeNode[] allNodes = nodemap.values().toArray( new
	 * GlycoTreeNode[nodemap.size()]); double mono = 0; for (int i = 0; i <
	 * allNodes.length; i++) { if (monomap.containsKey(allNodes[i]
	 * .getMonoDescription())) { mono += monomap.get(allNodes[i]
	 * .getMonoDescription()); } else { sb = new StringBuilder(); continue L; }
	 * } if (mono > 8000) { sb = new StringBuilder(); continue L; }
	 * gtree.setGlycoCT(sb.toString()); if (gtree.isRing()) { sb = new
	 * StringBuilder(); continue L; }
	 * 
	 * sb.append("Mono:").append(mono).append("\n");
	 * 
	 * HashSet<Double> set = new HashSet<Double>();
	 * 
	 * GlycoTreeNode node = nodemap.get("1"); GlycoTreeNode[] nodes = new
	 * GlycoTreeNode[] { node }; String des = node.getMonoDescription();
	 * 
	 * double mass = monomap.get(des); //
	 * System.out.println("size\t"+nodemap.size()+"\t"+gtree.getIUPACName()); //
	 * System.out.println(gtree.getGlycoCT()+"\n"); set =
	 * traversePeakList(nodes, mass, monomap, set);
	 * 
	 * Double[] peaklist = set.toArray(new Double[set.size()]);
	 * Arrays.sort(peaklist); // System.out.println(Arrays.toString(peaklist));
	 * sb.append("Fragment:");
	 * 
	 * for (int i = 0; i < peaklist.length; i++) { //
	 * System.out.println(peaklist[i]); sb.append(peaklist[i] + "\t"); }
	 * 
	 * pw.write(sb.toString() + "\n\n"); }
	 * 
	 * sb = new StringBuilder(); gtree = new GlycoTree(); continue; } else {
	 * sb.append(line).append("\n"); }
	 * 
	 * if (line.startsWith("RES")) {
	 * 
	 * gtree = new GlycoTree(); res = true;
	 * 
	 * } else if (line.startsWith("LIN")) {
	 * 
	 * lin = true; res = false;
	 * 
	 * } else if (line.startsWith("Mono")) {
	 * 
	 * } else if (line.startsWith("Avg")) {
	 * 
	 * } else {
	 * 
	 * if (res) {
	 * 
	 * int beg = line.indexOf(":");
	 * 
	 * String id = line.substring(0, beg - 1); String typejudeg =
	 * line.substring(0, beg); String content = line.substring(beg + 1);
	 * 
	 * if (typejudeg.endsWith("b")) {
	 * 
	 * GlycoTreeNode node = new GlycoTreeNode(id, content); gtree.addNode(id,
	 * node);
	 * 
	 * } else if (typejudeg.endsWith("s")) {
	 * 
	 * gtree.addSub(id, content);
	 * 
	 * } else {
	 * 
	 * reader.close(); pw.close();
	 * 
	 * return; }
	 * 
	 * } else if (lin) {
	 * 
	 * String[] ss = line.split("[:()+]"); String parentid = ss[1].substring(0,
	 * ss[1].length() - 1); String childid = ss[4].substring(0, ss[4].length() -
	 * 1); char parentLinkType = ss[1].charAt(ss[1].length() - 1); char
	 * childLinkType = ss[4].charAt(ss[3].length() - 1); String linkPosition1 =
	 * ss[2]; String linkPosition2 = ss[3];
	 * 
	 * gtree.addLink(parentid, childid, parentLinkType, childLinkType,
	 * linkPosition1, linkPosition2); } } }
	 * 
	 * reader.close(); pw.close(); System.out.println(count); // Iterator
	 * <String> it = totalset.iterator(); // while(it.hasNext()){ // String ss =
	 * it.next(); // System.out.println(ss); // }
	 * 
	 * }
	 */
	private static void addGlycanInfo(String in, String out, HashMap<String, Monosaccharide> monomap)
			throws IOException {

		PrintWriter pw = new PrintWriter(out);
		BufferedReader reader = new BufferedReader(new FileReader(in));
		boolean res = false;
		boolean lin = false;

		GlycoTree gtree = null;
		int count = 0;
		StringBuilder sb = new StringBuilder();
		String line = null;
		HashSet<String> nameset = new HashSet<String>();

		L: while ((line = reader.readLine()) != null) {

			if (line.trim().length() == 0) {

				if (gtree != null) {

					count++;
					if (count % 500 == 0) {
						System.out.println(count);
					}
					HashMap<String, GlycoTreeNode> nodemap = gtree.getNodeMap();
					if (nodemap.size() < 5) {
						sb = new StringBuilder();
						continue;
					}

					GlycoTreeNode[] allNodes = nodemap.values().toArray(new GlycoTreeNode[nodemap.size()]);
					double mono = 0;
					for (int i = 0; i < allNodes.length; i++) {
						if (monomap.containsKey(allNodes[i].getGlycoCTName())) {

							Monosaccharide saccharide = monomap.get(allNodes[i].getGlycoCTName());
							mono += saccharide.getMono_mass();

						} else {
							sb = new StringBuilder();
							continue L;
						}
					}
					if (mono > 8000) {
						sb = new StringBuilder();
						continue L;
					}
					gtree.setGlycoCT(sb.toString());
					if (gtree.isRing()) {
						sb = new StringBuilder();
						continue L;
					}

					sb.append("Mono:").append(mono).append("\n");

					HashSet<Double> set = new HashSet<Double>();

					GlycoTreeNode node = nodemap.get("1");
					GlycoTreeNode[] nodes = new GlycoTreeNode[] { node };
					String des = node.getGlycoCTName();

					double mass = monomap.get(des).getMono_mass();
					// System.out.println("size\t"+nodemap.size()+"\t"+gtree.getIUPACName());
					// System.out.println(gtree.getGlycoCT()+"\n");
					set = traversePeakList(nodes, mass, monomap, set);

					Double[] peaklist = set.toArray(new Double[set.size()]);
					Arrays.sort(peaklist);
					// System.out.println(Arrays.toString(peaklist));
					sb.append("Fragment:");

					for (int i = 0; i < peaklist.length; i++) {
						// System.out.println(peaklist[i]);
						sb.append(peaklist[i] + "\t");
					}

					pw.write(sb.toString() + "\n\n");
				}

				sb = new StringBuilder();
				gtree = new GlycoTree();
				continue;
			} else {
				sb.append(line).append("\n");
			}

			if (line.startsWith("RES")) {

				gtree = new GlycoTree();
				res = true;

			} else if (line.startsWith("LIN")) {

				lin = true;
				res = false;

			} else if (line.startsWith("Mono")) {

			} else if (line.startsWith("Avg")) {

			} else {

				if (res) {

					int beg = line.indexOf(":");

					String id = line.substring(0, beg - 1);
					String typejudeg = line.substring(0, beg);
					String content = line.substring(beg + 1);

					if (typejudeg.endsWith("b")) {

						GlycoTreeNode node = new GlycoTreeNode(id, content);
						gtree.addNode(id, node);

					} else if (typejudeg.endsWith("s")) {

						gtree.addSub(id, content);

					} else {

						reader.close();
						pw.close();

						return;
					}

				} else if (lin) {

					String[] ss = line.split("[:()+]");
					String parentid = ss[1].substring(0, ss[1].length() - 1);
					String childid = ss[4].substring(0, ss[4].length() - 1);
					char parentLinkType = ss[1].charAt(ss[1].length() - 1);
					char childLinkType = ss[4].charAt(ss[3].length() - 1);
					String linkPosition1 = ss[2];
					String linkPosition2 = ss[3];

					gtree.addLink(parentid, childid, parentLinkType, childLinkType, linkPosition1, linkPosition2);
				}
			}
		}

		/*
		 * HashMap <String, GlycoTreeNode> nodemap = gtree.getNodeMap();
		 * 
		 * Iterator <String> it = nodemap.keySet().iterator();
		 * while(it.hasNext()){ String key = it.next(); GlycoTreeNode node =
		 * nodemap.get(key); double parentmas = getParentMass(node, monomap, 0);
		 * set.add(parentmas); }
		 * 
		 * Double [] peaklist = set.toArray(new Double[set.size()]);
		 * Arrays.sort(peaklist);
		 * 
		 * for(int i=0;i<peaklist.length;i++){ // System.out.println();
		 * sb.append(peaklist[i]+"\t"); }
		 * 
		 * pw.write(sb.toString());
		 */
		reader.close();
		pw.close();
		System.out.println(count);
	}

	public static HashMap<String, Double> getMonoMap(String massfile) throws IOException {
		HashMap<String, Double> monomap = new HashMap<String, Double>();
		BufferedReader reader = new BufferedReader(new FileReader(massfile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] ss = line.split("\t");
			monomap.put(ss[0], Double.parseDouble(ss[4]));
		}
		reader.close();
		return monomap;
	}

	private static HashSet<Double> traversePeakList(GlycoTreeNode node, double mass, HashMap<String, Double> massmap,
			HashSet<Double> set) {

		double rootmass = massmap.get(node.getGlycoCTName());
		ArrayList<GlycoTreeNode> list = node.getChildNodeList();
		int size = list.size();
		GlycoTreeNode[] nodes = list.toArray(new GlycoTreeNode[size]);
		HashSet<Double> tempset = new HashSet<Double>();
		tempset.add(rootmass);

		for (int i = 1; i <= size; i++) {

			Object[][] combines = Combinator.getCombination(nodes, i);
			// ArrayList
			for (int j = 0; j < combines.length; j++) {

				double childmass = 0;

				for (int k = 0; k < combines[j].length; k++) {

					GlycoTreeNode child = (GlycoTreeNode) combines[j][k];
					childmass += massmap.get(child.getGlycoCTName());
				}

				set.add(childmass);
			}
		}

		return set;
	}

	/*
	 * private static HashSet<Double> traversePeakList(GlycoTreeNode[] nodelist,
	 * double mass, HashMap<String, Double> massmap, HashSet<Double> set) {
	 * 
	 * ArrayList<GlycoTreeNode> list = new ArrayList<GlycoTreeNode>(); for (int
	 * i = 0; i < nodelist.length; i++) { GlycoTreeNode node = nodelist[i];
	 * list.addAll(node.getChildNodeList()); }
	 * 
	 * int size = list.size(); GlycoTreeNode[] nodes = list.toArray(new
	 * GlycoTreeNode[size]); set.add(mass);
	 * 
	 * for (int i = 1; i <= size; i++) {
	 * 
	 * Object[][] combines = Combinator.getCombination(nodes, i);
	 * 
	 * for (int j = 0; j < combines.length; j++) {
	 * 
	 * GlycoTreeNode[] comb = new GlycoTreeNode[combines[j].length]; double dd =
	 * mass;
	 * 
	 * for (int k = 0; k < combines[j].length; k++) {
	 * 
	 * GlycoTreeNode child = (GlycoTreeNode) combines[j][k]; double childmass =
	 * massmap.get(child.getMonoDescription()); comb[k] = child; dd +=
	 * childmass; }
	 * 
	 * set.add(dd); traversePeakList(comb, dd, massmap, set); } }
	 * 
	 * return set; }
	 */
	private static HashSet<Double> traversePeakList(GlycoTreeNode[] nodelist, double mass,
			HashMap<String, Monosaccharide> massmap, HashSet<Double> set) {

		ArrayList<GlycoTreeNode> list = new ArrayList<GlycoTreeNode>();
		for (int i = 0; i < nodelist.length; i++) {
			GlycoTreeNode node = nodelist[i];
			list.addAll(node.getChildNodeList());
		}

		int size = list.size();
		GlycoTreeNode[] nodes = list.toArray(new GlycoTreeNode[size]);
		set.add(mass);

		for (int i = 1; i <= size; i++) {

			Object[][] combines = Combinator.getCombination(nodes, i);

			for (int j = 0; j < combines.length; j++) {

				GlycoTreeNode[] comb = new GlycoTreeNode[combines[j].length];
				double dd = mass;

				for (int k = 0; k < combines[j].length; k++) {

					GlycoTreeNode child = (GlycoTreeNode) combines[j][k];
					double childmass = massmap.get(child.getGlycoCTName()).getMono_mass();
					comb[k] = child;
					dd += childmass;
				}

				set.add(dd);
				traversePeakList(comb, dd, massmap, set);
			}
		}

		return set;
	}

	private static HashSet<Double> traversePeakList(GlycoTreeNode node, HashMap<String, Double> massmap) {

		double rootmass = massmap.get(node.getGlycoCTName());
		HashSet<Double> set = new HashSet<Double>();
		set.add(rootmass);

		ArrayList<GlycoTreeNode> list = node.getChildNodeList();
		int size = list.size();
		GlycoTreeNode[] nodes = list.toArray(new GlycoTreeNode[size]);

		for (int i = 1; i <= size; i++) {

			Object[][] combines = Combinator.getCombination(nodes, i);

			for (int j = 0; j < combines.length; j++) {

				double childmass = 0;

				for (int k = 0; k < combines[j].length; k++) {

					GlycoTreeNode child = (GlycoTreeNode) combines[j][k];

					if (child.isLeaf()) {

						childmass += massmap.get(child.getGlycoCTName());

					} else {

						HashSet<Double> temp = traversePeakList(child, massmap);
						Iterator<Double> tempit = temp.iterator();
						while (tempit.hasNext()) {
							Double tempd = tempit.next();
							childmass += tempd;
						}
						set.add(childmass + rootmass);
					}
				}
			}
		}

		return set;
	}
	
	/**
	 * 
	 * @param mono 
	 * @param in the folder of all glyct txt file.
	 * @param out
	 * @throws IOException
	 */
	private static void writeMonoInfo(String mono, String in, String out) throws IOException {

		File[] files = (new File(in)).listFiles();
		HashSet<String> monosaccharideSet = new HashSet<String>();
		for (int i = 0; i < files.length; i++) {

			GlycoTree glycanTree = readNGlycan(files[i]);
			if (glycanTree != null) {

				HashMap<String, GlycoTreeNode> nodemap = glycanTree.getNodeMap();
				Iterator<String> it = nodemap.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					monosaccharideSet.add(nodemap.get(key).getGlycoCTName());
				}
			}
		}

		PrintWriter pw = new PrintWriter(out);
		HashMap<String, String> patternmap = MonosaccharideDB.getAbbreNameMapNew(mono);
		HashMap<String, String> massmap = MonosaccharideDB.getMonoMassMap(mono);
		String[] pats = patternmap.keySet().toArray(new String[patternmap.size()]);
		Iterator<String> it = monosaccharideSet.iterator();
		while (it.hasNext()) {
			String ss = it.next();
			for (int i = 0; i < pats.length; i++) {
				Pattern p = Pattern.compile(pats[i]);
				Matcher m = p.matcher(ss);
				if (m.matches()) {
					String sss = patternmap.get(pats[i]);
					pw.write(ss + "\t" + sss + "\t" + GlycoDatabaseReader.findName(sss) + "\t" + massmap.get(pats[i])
							+ "\n");
					break;
				}
			}
		}
		pw.close();
	}

	private static void writeMasses(String in, String out) throws IOException {

		PrintWriter pw = new PrintWriter(out);
		BufferedReader reader = new BufferedReader(new FileReader(in));

		String line = null;

		HashMap<Double, HashSet<String>> massmap = new HashMap<Double, HashSet<String>>();

		while ((line = reader.readLine()) != null) {
			if (line.startsWith("Mono")) {
				double mono = Double.parseDouble(line.substring(line.indexOf(":") + 1));
				line = reader.readLine();
				String fragment = line.substring(line.indexOf(":") + 1);
				if (massmap.containsKey(mono)) {
					massmap.get(mono).add(fragment);
				} else {
					HashSet<String> set = new HashSet<String>();
					set.add(fragment);
					massmap.put(mono, set);
				}
			}
		}

		Double[] masses = massmap.keySet().toArray(new Double[massmap.size()]);
		Arrays.sort(masses);

		for (int i = 0; i < masses.length; i++) {
			HashSet<String> set = massmap.get(masses[i]);
			StringBuilder sb = new StringBuilder();
			sb.append("ID:\t").append(i + 1).append("\t");
			sb.append(masses[i]).append("\t").append(set.size()).append("\n");
			for (String fragment : set) {
				sb.append(fragment).append("\n");
			}
			pw.write(sb.toString());
		}

		reader.close();
		pw.close();
		System.out.println(masses.length);
	}

	private static void writeFinal(String in, String mass, String out) throws IOException {

		HashMap<String, String[]> massmap = new HashMap<String, String[]>();
		HashMap<String, String> idmap = new HashMap<String, String>();
		BufferedReader massreader = new BufferedReader(new FileReader(mass));
		String ml = null;
		while ((ml = massreader.readLine()) != null) {
			if (ml.startsWith("ID")) {
				String[] ss = ml.split("\t");
				idmap.put(ss[2], ss[1]);
				int num = Integer.parseInt(ss[3]);
				String[] fragment = new String[num];
				int i = 0;
				while ((ml = massreader.readLine()) != null && i < num) {
					fragment[i] = ml;
					i++;
				}
				massmap.put(ss[2], fragment);
			}
		}
		massreader.close();

		PrintWriter pw = new PrintWriter(out);
		BufferedReader reader = new BufferedReader(new FileReader(in));
		boolean res = false;
		boolean lin = false;

		GlycoTree gtree = null;
		int count = 0;
		StringBuilder sb = new StringBuilder();
		String line = null;
		String fragment = "";
		String monomass = "";

		while ((line = reader.readLine()) != null) {

			if (line.trim().length() == 0) {

				if (gtree != null) {

					count++;
					if (count % 500 == 0) {
						System.out.println(count);
					}

					// String iupac = gtree.getIUPACName();
					boolean isNglycan = gtree.isNGlycan();

					String id = idmap.get(monomass);
					String[] frags = massmap.get(monomass);

					if (id != null) {

						sb.append("ID:\t").append(id).append("\t");
						for (int i = 0; i < frags.length; i++) {
							if (fragment.equals(frags[i])) {
								sb.append(i).append("\t");
								break;
							}
						}
						if (isNglycan) {
							sb.append("1").append("\t");
						} else {
							sb.append("0").append("\t");
						}

					} else {
						// System.out.println(monomass);
					}

					pw.write(sb.toString() + "\n\n");
				}

				sb = new StringBuilder();
				gtree = new GlycoTree();
				continue;

			}

			if (line.startsWith("RES")) {

				sb.append(line).append("\n");
				gtree = new GlycoTree();
				res = true;

			} else if (line.startsWith("LIN")) {

				sb.append(line).append("\n");
				lin = true;
				res = false;

			} else if (line.startsWith("Mono")) {

				monomass = line.substring(line.indexOf(":") + 1);
				sb.append(line).append("\n");

			} else if (line.startsWith("Fragment")) {

				fragment = line.substring(line.indexOf(":") + 1);

			} else {

				sb.append(line).append("\n");

				if (res) {

					int beg = line.indexOf(":");

					String id = line.substring(0, beg - 1);
					String typejudeg = line.substring(0, beg);
					String content = line.substring(beg + 1);

					if (typejudeg.endsWith("b")) {

						GlycoTreeNode node = new GlycoTreeNode(id, content);
						gtree.addNode(id, node);

					} else if (typejudeg.endsWith("s")) {

						gtree.addSub(id, content);

					} else {

						reader.close();
						pw.close();

						return;
					}

				} else if (lin) {

					String[] ss = line.split("[:()+]");
					String parentid = ss[1].substring(0, ss[1].length() - 1);
					String childid = ss[4].substring(0, ss[4].length() - 1);
					char parentLinkType = ss[1].charAt(ss[1].length() - 1);
					char childLinkType = ss[4].charAt(ss[3].length() - 1);
					String linkPosition1 = ss[2];
					String linkPosition2 = ss[3];

					gtree.addLink(parentid, childid, parentLinkType, childLinkType, linkPosition1, linkPosition2);
				}
			}
		}

		/*
		 * HashMap <String, GlycoTreeNode> nodemap = gtree.getNodeMap();
		 * 
		 * Iterator <String> it = nodemap.keySet().iterator();
		 * while(it.hasNext()){ String key = it.next(); GlycoTreeNode node =
		 * nodemap.get(key); double parentmas = getParentMass(node, monomap, 0);
		 * set.add(parentmas); }
		 * 
		 * Double [] peaklist = set.toArray(new Double[set.size()]);
		 * Arrays.sort(peaklist);
		 * 
		 * for(int i=0;i<peaklist.length;i++){ // System.out.println();
		 * sb.append(peaklist[i]+"\t"); }
		 * 
		 * pw.write(sb.toString());
		 */
		reader.close();
		pw.close();
		System.out.println(count);
		// Iterator <String> it = totalset.iterator();
		// while(it.hasNext()){
		// String ss = it.next();
		// System.out.println(ss);
		// }

	}

	private static void writeMassIdFragment(String glycoCT, String massInfo, String glycoout, String massout)
			throws IOException {

		HashMap<String, Monosaccharide> monomap = MonosaccharideDB
				// .getMonosaccInfoMap(massInfo);
				.getMonosaccInfoMapModified1(massInfo);

		BufferedReader reader = new BufferedReader(new FileReader(glycoCT));
		boolean res = false;
		boolean lin = false;

		GlycoTree gtree = null;
		int count = 0;

		HashMap<String, double[]> massmap = new HashMap<String, double[]>();
		HashMap<String, int[]> compmap = new HashMap<String, int[]>();
		HashMap<String, HashSet<String>> namemap = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> fragmap = new HashMap<String, HashSet<String>>();

		HashMap<String, GlycoTree> gtreemap = new HashMap<String, GlycoTree>();
		HashMap<String, String> gtreefragmap = new HashMap<String, String>();
		// HashSet <String> compset = new HashSet <String>();

		String line = null;
		StringBuilder glycosb = new StringBuilder();

		while ((line = reader.readLine()) != null) {

			if (line.trim().length() == 0) {

				if (gtree != null) {

					count++;
					if (count % 500 == 0) {
						System.out.println(count);
					}

					if (!gtree.isNGlycan()) {
						continue;
					}

					if (gtree.isRing() || gtree.isSubLind() || gtree.getNodeMap().size() >= 50
							|| gtree.getNodeMap().size() < 5) {
						continue;
					}

					String name = getGlycoTreeName(gtree, monomap);

					double[] mass = getGlycoTreeMass(gtree, monomap);
					Double[] fragments = getGlycoTreeFragList(gtree, monomap);
					int[] composition = getComposition(gtree, monomap);
					String key = Arrays.toString(composition);
					// if(name.equals("Gal-(2,6-deoxy-lyxHex-)GlcNAc-Asn")){
					// System.out.println(glycosb);
					// System.out.println(mass[0]+"\t"+mass[1]);
					// System.out.println(Arrays.toString(composition));
					// }

					gtree.setGlycoCT(glycosb.toString());
					// gtree.setMonoMass(mass[0]);
					// gtree.setAveMass(mass[1]);
					// gtree.setIupacName(name);
					// gtree.setComposition(composition);
					// gtree.setFragments(fragments);

					String frag = "";
					for (int i = 0; i < fragments.length; i++) {
						frag += df5.format(fragments[i]);
						frag += "\t";
					}

					// if(Arrays.toString(composition).equals("[52, 86, 2, 39,
					// 0, 0, 0, 0, 0, 0]")){
					// System.out.println(mass[0]+"\t"+Arrays.toString(composition));
					// }

					if (massmap.containsKey(key)) {
						namemap.get(key).add(name);
						fragmap.get(key).add(frag);
					} else {
						massmap.put(key, mass);
						compmap.put(key, composition);
						HashSet<String> nameset = new HashSet<String>();
						nameset.add(name);
						namemap.put(key, nameset);
						HashSet<String> fragset = new HashSet<String>();
						fragset.add(frag);
						fragmap.put(key, fragset);
						// if(compset.contains(Arrays.toString(composition))){
						// System.out.println(mass[0]+"\t"+Arrays.toString(composition));
						// }
					}

					gtreemap.put(name, gtree);
					gtreefragmap.put(name, frag);
				}

				continue;
			}

			if (line.startsWith("RES")) {

				gtree = new GlycoTree();
				glycosb = new StringBuilder();
				res = true;

			} else if (line.startsWith("LIN")) {

				lin = true;
				res = false;

			} else {

				if (res) {

					int beg = line.indexOf(":");

					String id = line.substring(0, beg - 1);
					String typejudge = line.substring(0, beg);
					String content = line.substring(beg + 1);

					if (typejudge.endsWith("b")) {

						GlycoTreeNode node = new GlycoTreeNode(id, content);
						gtree.addNode(id, node);

					} else if (typejudge.endsWith("s")) {

						gtree.addSub(id, content);

					} else {

						continue;
					}

				} else if (lin) {

					String[] ss = line.split("[:()+]");
					String parentid = ss[1].substring(0, ss[1].length() - 1);
					String childid = ss[4].substring(0, ss[4].length() - 1);
					char parentLinkType = ss[1].charAt(ss[1].length() - 1);
					char childLinkType = ss[4].charAt(ss[3].length() - 1);
					String linkPosition1 = ss[2];
					String linkPosition2 = ss[3];

					gtree.addLink(parentid, childid, parentLinkType, childLinkType, linkPosition1, linkPosition2);
				}
			}

			glycosb.append(line).append("\n");
		}

		System.out.println(massmap.size() + "\t" + gtreemap.size());

		PrintWriter glycopw = new PrintWriter(glycoout);
		PrintWriter masspw = new PrintWriter(massout);

		// Double [] monomasses = massmap.keySet().toArray(new
		// Double[massmap.size()]);
		// Arrays.sort(monomasses);

		String[] keys = massmap.keySet().toArray(new String[massmap.size()]);
		// HashSet <String> redundant = new HashSet <String>();

		DanTeng[] dantengList = new DanTeng[massmap.size()];
		for (int i = 0; i < keys.length; i++) {

			double[] masses = massmap.get(keys[i]);
			int[] composition = compmap.get(keys[i]);
			HashSet<String> nameset = namemap.get(keys[i]);
			HashSet<String> fragset = fragmap.get(keys[i]);

			dantengList[i] = new DanTeng(masses, composition, nameset, fragset);

		}

		Arrays.sort(dantengList, new Comparator<DanTeng>() {

			@Override
			public int compare(DanTeng arg0, DanTeng arg1) {
				// TODO Auto-generated method stub
				if (arg0.masses[0] > arg1.masses[0]) {
					return 1;
				} else if (arg0.masses[0] < arg1.masses[0]) {
					return -1;
				} else {
					return 0;
				}
			}

		});

		for (int i = 0; i < dantengList.length; i++) {

			double[] masses = dantengList[i].masses;
			int[] composition = dantengList[i].composition;
			HashSet<String> nameset = dantengList[i].nameset;
			HashSet<String> fragset = dantengList[i].fragset;

			String[] names = nameset.toArray(new String[nameset.size()]);
			Arrays.sort(names, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					// TODO Auto-generated method stub
					int l1 = o1.length() - o1.replaceAll("\\(", "").length();
					int l2 = o2.length() - o2.replaceAll("\\(", "").length();

					if (l1 < l2) {
						return 1;
					} else if (l1 > l2) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			String[] frags = fragset.toArray(new String[fragset.size()]);
			Arrays.sort(frags, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					// TODO Auto-generated method stub
					int l1 = o1.split("\t").length;
					int l2 = o2.split("\t").length;

					if (l1 < l2) {
						return 1;
					} else if (l1 > l2) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			// if(names.length<frags.length)System.out.println("1103\t"+masses[0]+"\t"+(i+1)+"\t"+names.length+"\t"+frags.length);

			StringBuilder sb = new StringBuilder();
			sb.append("ID\t").append(i + 1).append("\t");
			sb.append(df5.format(masses[0])).append("\t").append(df5.format(masses[1])).append("\t")
					.append(frags.length).append("\t");
			for (int j = 0; j < composition.length; j++) {
				sb.append(composition[j]).append("\t");
			}
			sb.append("\n");
			for (int j = 0; j < frags.length; j++) {
				String[] ss = frags[j].split("\t");
				double[] decoy = new double[ss.length];
				for (int k = 0; k < ss.length; k++) {
					sb.append(ss[k]).append("\t");
					if (k < ss.length - 1)
						decoy[ss.length - k - 2] = masses[0] - Double.parseDouble(ss[k]) + 10.0;
					// decoy[k] = Double.parseDouble(ss[k]) + 10.0;
				}
				decoy[ss.length - 1] = masses[0];
				sb.append("\n");
				// for (int k = 0; k < decoy.length; k++) {
				// sb.append(df5.format(decoy[k])).append("\t");
				// }
				// sb.append("\n");
			}
			masspw.write(sb.toString());

			for (int j = 0; j < names.length; j++) {
				String name = names[j];
				// if(redundant.contains(name)){
				// System.out.println(name);
				// }
				// redundant.add(name);
				GlycoTree glycotree = gtreemap.get(name);
				glycopw.write(glycotree.getGlycoCT());
				glycopw.write("IUPAC\t" + name + "\n");
				String frag = gtreefragmap.get(name);
				for (int k = 0; k < frags.length; k++) {
					if (frag.equals(frags[k])) {
						glycopw.write("ID\t" + (i + 1) + "\t" + (k + 1) + "\n");
						break;
					}
				}
			}
		}

		reader.close();
		glycopw.close();
		masspw.close();

		System.out.println(count);
	}

	public static void writeMonosaccharideAndGlycoCT(String originalPattern, String GlycoCT_condenced, String monoOut,
			String glycoCTOut) throws IOException {

		HashMap<String, Monosaccharide> monoMap = MonosaccharideDB.getMonosaccMap(originalPattern);

		Monosaccharide[] monos = monoMap.values().toArray(new Monosaccharide[monoMap.size()]);
		HashMap<String, Monosaccharide> newMonoMap = new HashMap<String, Monosaccharide>();

		PrintWriter glycoWriter = new PrintWriter(glycoCTOut);

		File[] files = (new File(GlycoCT_condenced)).listFiles();
		L1: for (int i = 0; i < files.length; i++) {

			GlycoTree gtree = readNGlycan(files[i]);
			if (gtree == null)
				continue;

			HashMap<String, GlycoTreeNode> nodemap = gtree.getNodeMap();
			Iterator<String> it = nodemap.keySet().iterator();

			L2: while (it.hasNext()) {
				String key = it.next();
				GlycoTreeNode node = nodemap.get(key);
				String nodename = node.getGlycoCTName();

				if (monoMap.containsKey(nodename)) {
					newMonoMap.put(nodename, monoMap.get(nodename));
				} else {
					for (int j = 0; j < monos.length; j++) {
						Pattern pattern = monos[j].getPattern();
						Matcher matcher = pattern.matcher(nodename);
						if (matcher.matches()) {
							newMonoMap.put(nodename, monos[j]);
							continue L2;
						}
					}
					continue L1;
				}
			}

			glycoWriter.write(gtree.getGlycoCT() + "\n");
		}
		glycoWriter.close();

		System.out.println(newMonoMap.size());

		PrintWriter monoWriter = new PrintWriter(monoOut);
		Iterator<String> it = newMonoMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Monosaccharide mono = newMonoMap.get(key);
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			sb.append(mono.getCarbBank_Name()).append("\t");
			sb.append(mono.getIUPAC_Name()).append("\t");
			sb.append(df5.format(mono.getMono_mass())).append("\t");
			sb.append(df5.format(mono.getAvg_mass())).append("\t");
			int[] comp = mono.getComposition();
			for (int i = 0; i < comp.length; i++) {
				sb.append(comp[i]).append("\t");
			}
			monoWriter.write(sb.toString() + "\n");
		}
		monoWriter.close();
	}

	public static void writeMonosaccharide(String originalPattern, String glycoCT, String out) throws IOException {

		HashMap<String, Monosaccharide> monoMap = MonosaccharideDB.getMonosaccMap(originalPattern);

		Monosaccharide[] monos = monoMap.values().toArray(new Monosaccharide[monoMap.size()]);
		HashMap<String, Monosaccharide> newMonoMap = new HashMap<String, Monosaccharide>();

		GlycoTree gtree = null;
		boolean res = false;
		boolean lin = false;

		String line = null;
		BufferedReader reader = new BufferedReader(new FileReader(glycoCT));
		while ((line = reader.readLine()) != null) {

			if (line.trim().length() == 0) {

				if (gtree != null) {
					HashMap<String, GlycoTreeNode> nodemap = gtree.getNodeMap();
					Iterator<String> it = nodemap.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next();
						GlycoTreeNode node = nodemap.get(key);
						String nodename = node.getGlycoCTName();

						if (monoMap.containsKey(nodename)) {
							newMonoMap.put(nodename, monoMap.get(nodename));
						} else {
							for (int i = 0; i < monos.length; i++) {
								Pattern pattern = monos[i].getPattern();
								Matcher matcher = pattern.matcher(nodename);
								if (matcher.matches()) {
									newMonoMap.put(nodename, monos[i]);
									break;
								}
							}
						}
					}
				}
				continue;
			}

			if (line.startsWith("RES")) {

				gtree = new GlycoTree();
				res = true;

			} else if (line.startsWith("LIN")) {

				lin = true;
				res = false;

			} else {

				if (res) {

					int beg = line.indexOf(":");

					String id = line.substring(0, beg - 1);
					String typejudeg = line.substring(0, beg);
					String content = line.substring(beg + 1);

					if (typejudeg.endsWith("b")) {

						GlycoTreeNode node = new GlycoTreeNode(id, content);
						gtree.addNode(id, node);

					} else if (typejudeg.endsWith("s")) {

						gtree.addSub(id, content);

					} else {

						continue;
					}

				} else if (lin) {

					String[] ss = line.split("[:()+]");
					String parentid = ss[1].substring(0, ss[1].length() - 1);
					String childid = ss[4].substring(0, ss[4].length() - 1);
					char parentLinkType = ss[1].charAt(ss[1].length() - 1);
					char childLinkType = ss[4].charAt(ss[3].length() - 1);
					String linkPosition1 = ss[2];
					String linkPosition2 = ss[3];

					gtree.addLink(parentid, childid, parentLinkType, childLinkType, linkPosition1, linkPosition2);
				}
			}
		}
		reader.close();

		System.out.println(newMonoMap.size());

		PrintWriter writer = new PrintWriter(out);
		Iterator<String> it = newMonoMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Monosaccharide mono = newMonoMap.get(key);
			StringBuilder sb = new StringBuilder();
			sb.append(mono.getGlycoCT_Name()).append("\t");
			sb.append(mono.getCarbBank_Name()).append("\t");
			sb.append(mono.getIUPAC_Name()).append("\t");
			sb.append(mono.getMono_mass()).append("\t");
			sb.append(mono.getAvg_mass()).append("\t");
			int[] comp = mono.getComposition();
			for (int i = 0; i < comp.length; i++) {
				sb.append(comp[i]).append("\t");
			}
			writer.write(sb.toString() + "\n");
		}
		writer.close();
	}

	public static String getGlycoTreeName(GlycoTree gtree, HashMap<String, Monosaccharide> monomap) {

		GlycoTreeNode node = gtree.getNodeMap().get("1");

		return getSubTreeString(node, monomap);
	}

	private static String getSubTreeString(GlycoTreeNode node, HashMap<String, Monosaccharide> monomap) {

		StringBuilder sb = new StringBuilder();
		ArrayList<GlycoTreeNode> childlist = node.getChildNodeList();

		if (childlist.size() == 0) {

			// sb.append(monomap.get(node.getGlycoCTName()).getIUPAC_Name())
			// .append(node.getParentLink());

			sb.append(monomap.get(node.getGlycoCTName()).getIUPAC_Name()).append("-");

			return sb.toString();

		} else {

			int maxid = -1;
			int max = 0;
			for (int i = 0; i < childlist.size(); i++) {

				GlycoTreeNode childe = childlist.get(i);
				int length = getMaxLinkLength(childe);

				if (length > max) {
					max = length;
					maxid = i;
				}
			}

			sb.append(getSubTreeString(childlist.get(maxid), monomap));
			for (int i = 0; i < childlist.size(); i++) {
				if (i != maxid) {
					sb.append("(").append(getSubTreeString(childlist.get(i), monomap)).append(")");
				}
			}

			if (node.getParentNode() == null) {

				sb.append(monomap.get(node.getGlycoCTName()).getIUPAC_Name()).append("-Asn");

			} else {
				// sb.append(
				// monomap.get(node.getGlycoCTName()).getIUPAC_Name())
				// .append(node.getParentLink());
				sb.append(monomap.get(node.getGlycoCTName()).getIUPAC_Name()).append("-");
			}
		}

		return sb.toString();
	}

	private static int getMaxLinkLength(GlycoTreeNode node) {

		ArrayList<GlycoTreeNode> childlist = node.getChildNodeList();
		int max = 0;
		for (int i = 0; i < childlist.size(); i++) {

			int length = getMaxLinkLength(childlist.get(i));
			if (length > max) {
				max = length;
			}
		}

		return max + 1;
	}

	public static double[] getGlycoTreeMass(GlycoTree gtree, HashMap<String, Monosaccharide> monomap) {

		HashMap<String, GlycoTreeNode> nodemap = gtree.getNodeMap();
		double monoMass = 0;
		double aveMass = 0;
		Iterator<String> it = gtree.getNodeMap().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			GlycoTreeNode node = nodemap.get(key);
			String des = node.getGlycoCTName();
			monoMass += monomap.get(des).getMono_mass();
			aveMass += monomap.get(des).getAvg_mass();
		}
		return new double[] { Double.parseDouble(df5.format(monoMass)), Double.parseDouble(df5.format(aveMass)) };
	}

	public static int[] getComposition(GlycoTree gtree, HashMap<String, Monosaccharide> monomap) {

		int[] composition = new int[10];
		HashMap<String, GlycoTreeNode> nodemap = gtree.getNodeMap();
		Iterator<String> it = gtree.getNodeMap().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			GlycoTreeNode node = nodemap.get(key);
			String des = node.getGlycoCTName();
			int[] comp = monomap.get(des).getComposition();
			for (int i = 0; i < comp.length; i++) {
				composition[i] += comp[i];
			}
		}
		return composition;
	}

	public static Double[] getGlycoTreeFragList(GlycoTree gtree, HashMap<String, Monosaccharide> monomap) {

		HashSet<Double> set = new HashSet<Double>();
		GlycoTreeNode node = gtree.getNodeMap().get("1");
		GlycoTreeNode[] nodes = new GlycoTreeNode[] { node };
		double mass = monomap.get(node.getGlycoCTName()).getMono_mass();

		set = traverseFragList(nodes, mass, monomap, set);

		Double[] fragments = set.toArray(new Double[set.size()]);
		Arrays.sort(fragments);

		ArrayList<Double> list = new ArrayList<Double>();
		list.add(fragments[0]);
		for (int i = 1; i < fragments.length; i++) {
			if (fragments[i] - list.get(list.size() - 1) > 0.01) {
				list.add(fragments[i]);
			}
		}

		if (fragments.length == list.size()) {
			return fragments;
		} else {
			return list.toArray(new Double[list.size()]);
		}

	}

	private static HashSet<Double> traverseFragList(GlycoTreeNode[] nodelist, double mass,
			HashMap<String, Monosaccharide> monomap, HashSet<Double> set) {

		ArrayList<GlycoTreeNode> list = new ArrayList<GlycoTreeNode>();
		for (int i = 0; i < nodelist.length; i++) {
			GlycoTreeNode node = nodelist[i];
			list.addAll(node.getChildNodeList());
		}

		int size = list.size();
		GlycoTreeNode[] nodes = list.toArray(new GlycoTreeNode[size]);
		set.add(mass);

		for (int i = 1; i <= size; i++) {

			Object[][] combines = Combinator.getCombination(nodes, i);

			for (int j = 0; j < combines.length; j++) {

				GlycoTreeNode[] comb = new GlycoTreeNode[combines[j].length];
				double dd = mass;

				for (int k = 0; k < combines[j].length; k++) {

					GlycoTreeNode child = (GlycoTreeNode) combines[j][k];
					double childmass = monomap.get(child.getGlycoCTName()).getMono_mass();
					comb[k] = child;
					dd += childmass;
				}

				set.add(dd);
				traverseFragList(comb, dd, monomap, set);
			}
		}

		return set;
	}

	private static void creatRevMassDatabase(String in, String out) throws IOException {

		PrintWriter writer = new PrintWriter(out);
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("ID")) {
				writer.write(line + "\n");
			} else {
				StringBuilder sb = new StringBuilder();
				String[] sp = line.split("\t");
				double mono = Double.parseDouble(sp[sp.length - 1]);
				for (int i = sp.length - 2; i >= 0; i--) {
					sb.append(df5.format(mono - Double.parseDouble(sp[i]))).append("\t");
				}
				sb.append(mono).append("\n");
				writer.append(sb.toString());
			}
		}
		reader.close();
		writer.close();
	}

	private static class DanTeng {

		private double[] masses;
		private int[] composition;
		private HashSet<String> nameset;
		private HashSet<String> fragset;

		DanTeng(double[] masses, int[] composition, HashSet<String> nameset, HashSet<String> fragset) {
			this.masses = masses;
			this.composition = composition;
			this.nameset = nameset;
			this.fragset = fragset;
		}

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String mono = "H:\\Glyco_structure_20130507\\Glycan.mass.txt";
		String in = "H:\\Glyco_structure_20130507\\GlycoCT_condenced";
		String out = "H:\\Glyco_structure_20130507\\N.Glycan.txt";

		// GlycoCTExtractor.writeMonoInfo(mono, in, out);
		// GlycoCTExtractor.writeNGlycan(in, out);

		// HashMap <String, Double> monomap = GlycoCTExtractor.getMonoMap(mono);

		// GlycoCTExtractor.judgeNGlycan(in, out, monomap);
		// GlycoCTExtractor.writeMasses(in, out);
		// GlycoCTExtractor.writeFinal(in, mono, out);

		// GlycoCTExtractor.writeMonosaccharideAndGlycoCT("H:\\Glyco_structure_20130507\\"
		// +
		// "Original.Monosaccharide.Pattern.txt",
		// "H:\\Glyco_structure_20130507\\GlycoCT_condenced",
		// "H:\\Glyco_structure_20130507\\Monosaccharide.Info.txt",
		// "H:\\Glyco_structure_20130507\\GlycoCT.txt");

//		GlycoCTExtractor.writeMassIdFragment("H:\\Glyco_structure_20130507\\GlycoCT.txt",
//				"H:\\Glyco_structure_20130507\\Monosaccharide.Info.txt",
//				"H:\\Glyco_structure_20140103\\N_GlycoCT3.ID.txt", "H:\\Glyco_structure_20140103\\N_Mass3.Info.txt");

		/*
		 * GlycoCTExtractor.writeMassIdFragment(
		 * "H:\\Glyco_structure_20130507\\GlycoCT.txt",
		 * "H:\\Glyco_structure_20130507\\Monosaccharide.Info.txt",
		 * "H:\\Glyco_structure_20140103\\N_GlycoCT2.ID.txt",
		 * "H:\\Glyco_structure_20140103\\N_Mass2.Info.txt");
		 */

		// GlycoCTExtractor.creatRevMassDatabase("H:\\Glyco_structure_20130507\\N_Mass.Info.txt",
		// "H:\\Glyco_structure_20130507\\REV_N_Mass.Info.txt");

//		 HashMap<String, Monosaccharide> monomap = MonosaccharideDB
//		 .getMonosaccInfoMap("H:\\Glyco_structure_20130507\\Monosaccharide.Info.txt");
//		
		 GlycoTree gtree1 = GlycoCTExtractor.readNGlycan(new File("E:\\database\\glycome\\monosaccharide\\1.txt")); 
		 GlycoTree gtree2 = GlycoCTExtractor.readNGlycan(new File("E:\\database\\glycome\\monosaccharide\\2.txt"));
//		 System.out.println(gtree1.getGlycoCT());

		 writeNGlycan("E:\\database\\glycome\\monosaccharide", "E:\\database\\glycome\\monosaccharide.txt");
		 
		 
//		 System.out.println(GlycoCTExtractor.getGlycoTreeName(gtree1, monomap));
//		 System.out.println(GlycoCTExtractor.getGlycoTreeName(gtree2, monomap));
		 
		/*
		 * GlycoTree gtree = GlycoCTExtractor.readNGlycan(new
		 * File("H:\\Glyco_structure_20130507\\t3.txt")); String name =
		 * GlycoCTExtractor.getGlycoTreeName(gtree, monomap);
		 * 
		 * System.out.println(name+"\t"+gtree.getNodeMap().size()); String []
		 * ids = gtree.getNodeMap().keySet().toArray((new String
		 * [gtree.getNodeMap().size()])); Arrays.sort(ids); for(int
		 * i=0;i<ids.length;i++){
		 * System.out.println(ids[i]+"\t"+gtree.getNodeMap().get(ids[i]).
		 * getGlycoCTName()); }
		 */
		// double [] mass = GlycoCTExtractor.getGlycoTreeMass(gtree, monomap);
		// System.out.println(mass[0]);
		// GlycoCTExtractor.getGlycoTreeFragList(gtree, monomap);
	}

}
