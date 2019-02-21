/* 
 ******************************************************************************
 * File: OGlycanPaperSI.java * * * Created on 2014��3��14��
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;

import jxl.JXLException;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import flanagan.analysis.Regression;

/**
 * @author Administrator
 *
 * @version Jan 19, 2016, 9:48:19 AM
 */
public class OGlycanPaperSI {
	
	private static String[] glycans = new String[]{"GalNAc", "Gal-GalNAc", "NeuAc-GalNAc", "NeuAc-Gal-GalNAc", "NeuAc-Gal-(NeuAc-)GalNAc",
		"Gal-(GlcNAc-)GalNAc", "NeuAc-Gal-(GlcNAc-)GalNAc", "Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc",
		"NeuAc-Gal-(Fuc-)GlcNAc-(Gal-)GalNAc", "NeuAc-Gal-(Fuc-)GlcNAc-(NeuAc-Gal-)GalNAc", "NeuAc-Gal-(NeuAc-NeuAc-Gal-GlcNAc-)GalNAc",
		"NeuAc-Gal-(Gal-(HSO3-)GlcNAc-)GalNAc"};

	private static void combine(String s1, String s2, String s3, String out)
			throws IOException, JXLException {

		HashSet<String> pepset = new HashSet<String>();
		ExcelWriter writer = new ExcelWriter(out, new String[]{"site-specific glycoforms", "glycoforms in each site"});
		ExcelFormat format = ExcelFormat.normalFormat;
		StringBuilder title1 = new StringBuilder();
		title1.append("Site\t");
		title1.append("Sequence around\t");
		title1.append("Glycan\t");
		title1.append("Proteins\t");
		title1.append("Protein\t");
		title1.append("Trypsin\t");
		title1.append("Trypsin+GluC\t");
		title1.append("Elastase\t");
		writer.addTitle(title1.toString(), 0, format);

		StringBuilder title2 = new StringBuilder();
		title2.append("Site\t");
		title2.append("Sequence around\t");
		title2.append("Protein\t");
		for(int i=0;i<glycans.length;i++){
			title2.append(glycans[i]).append("\t");
		}
		writer.addTitle(title2.toString(), 1, format);

		HashMap<String, int[]> map = new HashMap<String, int[]>();
		HashMap<String, String[]> m1 = new HashMap<String, String[]>();
		HashMap<String, Boolean> mb1 = new HashMap<String, Boolean>();
		ExcelReader r1 = new ExcelReader(s1, 2);
		String[] l1 = r1.readLine();
		while ((l1 = r1.readLine()) != null) {
			String key = l1[0] + l1[1] + l1[2];
			m1.put(key, l1);
			mb1.put(key, l1[9].equals("Yes"));
			pepset.add(l1[7]);
			String key2 = l1[0] + "\t" + l1[1] + "\t" + l1[5];
			int type = judgeType(l1[2]);
			if (map.containsKey(key2)) {
				int[] types = map.get(key2);
				types[type] = 1;
				map.put(key2, types);
			} else {
				int[] types = new int[glycans.length];
				types[type] = 1;
				map.put(key2, types);
			}
		}
		r1.close();

		HashMap<String, String[]> m2 = new HashMap<String, String[]>();
		HashMap<String, Boolean> mb2 = new HashMap<String, Boolean>();
		ExcelReader r2 = new ExcelReader(s2, 2);
		String[] l2 = r2.readLine();
		while ((l2 = r2.readLine()) != null) {
			String key = l2[0] + l2[1] + l2[2];
			m2.put(key, l2);
			mb2.put(key, l2[9].equals("Yes"));
			pepset.add(l2[7]);
			String key2 = l2[0] + "\t" + l2[1] + "\t" + l2[5];
			int type = judgeType(l2[2]);
			if (map.containsKey(key2)) {
				int[] types = map.get(key2);
				types[type] = 1;
				map.put(key2, types);
			} else {
				int[] types = new int[glycans.length];
				types[type] = 1;
				map.put(key2, types);
			}
		}
		r2.close();

		HashMap<String, String[]> m3 = new HashMap<String, String[]>();
		HashMap<String, Boolean> mb3 = new HashMap<String, Boolean>();
		ExcelReader r3 = new ExcelReader(s3, 2);
		String[] l3 = r3.readLine();
		while ((l3 = r3.readLine()) != null) {
			String key = l3[0] + l3[1] + l3[2];
			m3.put(key, l3);
			mb3.put(key, l3[9].equals("Yes"));
			pepset.add(l3[7]);
			String key2 = l3[0] + "\t" + l3[1] + "\t" + l3[5];
			int type = judgeType(l3[2]);
			if (map.containsKey(key2)) {
				int[] types = map.get(key2);
				types[type] = 1;
				map.put(key2, types);
			} else {
				int[] types = new int[glycans.length];
				types[type] = 1;
				map.put(key2, types);
			}
		}
		r3.close();

		System.out.println("pepset\t" + pepset.size());
		HashSet<String> set = new HashSet<String>();
		set.addAll(m1.keySet());
		set.addAll(m2.keySet());
		set.addAll(m3.keySet());

		int count = 0;
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			String[] line = null;
			if (m1.containsKey(key)) {
				line = m1.get(key);
				sb2.append("+\t");
			} else {
				sb2.append("\t");
			}
			if (m2.containsKey(key)) {
				line = m2.get(key);
				sb2.append("+\t");
			} else {
				sb2.append("\t");
			}
			if (m3.containsKey(key)) {
				line = m3.get(key);
				sb2.append("+\t");
			} else {
				sb2.append("\t");
			}
			sb.append(line[0]).append("\t");
			sb.append(line[1]).append("\t");
			sb.append(line[2]).append("\t");
			sb.append(line[4]).append("\t");
			sb.append(line[5]).append("\t");
			sb.append(sb2);
			writer.addContent(sb.toString(), 0, format);

			boolean b = false;
			if (mb1.containsKey(key) && mb1.get(key)) {
				b = true;
			}
			if (mb2.containsKey(key) && mb2.get(key)) {
				b = true;
			}
			if (mb3.containsKey(key) && mb3.get(key)) {
				b = true;
			}
			if (b) {
				count++;
			}
		}
		System.out.println("Count\t" + count+"\t"+set.size()+"\t"+(double)count/(double)set.size()+"\t"+map.size());

		Iterator<String> it2 = map.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			int[] types = map.get(key);
			for (int i = 0; i < types.length; i++) {

				if (types[i] == 1) {
					sb.append("+\t");
				} else {
					sb.append("\t");
				}
			
				/*if(i==3){
					if(types[i]==1){
						if(types[i+1]==1){
							sb.append("+/+\t");
						}else{
							sb.append("+/-\t");
						}
					}else{
						if(types[i+1]==1){
							sb.append("-/+\t");
						}else{
							sb.append("\t");
						}
					}
					i++;
				}else{}*/
			}
			writer.addContent(sb.toString(), 1, format);
		}

		writer.close();
	}

	private static void combine2(String s1, String s2, String s3, String out, double score)
			throws IOException, JXLException {

		HashSet<String> pepset = new HashSet<String>();
		ExcelWriter writer = new ExcelWriter(out, new String[]{"glycosylation sites unambiguous", "glycoforms unambiguous", 
				"glycosylation sites all", "site-specific glycoforms all"});
		ExcelFormat format = ExcelFormat.normalFormat;
		StringBuilder title1 = new StringBuilder();
		title1.append("Site\t");
		title1.append("Sequence around\t");
		title1.append("Glycan\t");
		title1.append("Proteins\t");
		title1.append("Protein\t");
		title1.append("Trypsin\t");
		title1.append("Trypsin+GluC\t");
		title1.append("Elastase\t");
		writer.addTitle(title1.toString(), 0, format);
		writer.addTitle(title1.toString(), 2, format);

		StringBuilder title2 = new StringBuilder();
		title2.append("Site\t");
		title2.append("Sequence around\t");
		title2.append("Protein\t");
		for(int i=0;i<glycans.length;i++){
			title2.append(glycans[i]).append("\t");
		}
		writer.addTitle(title2.toString(), 1, format);
		writer.addTitle(title2.toString(), 3, format);
		/*title2.append("GalNAc\t");
		title2.append("Gal-GalNAc\t");
		title2.append("NeuAc-GalNAc\t");
		title2.append("NeuAc-Gal-GalNAc\t");
		title2.append("Gal-(NeuAc-)GalNAc\t");
		title2.append("NeuAc-Gal-(NeuAc-)GalNAc\t");
		title2.append("Gal-(GlcNAc-)GalNAc\t");
		title2.append("NeuAc-Gal-(GlcNAc-)GalNAc\t");
		title2.append("Gal-(Gal-GlcNAc-)GalNAc\t");
		title2.append("NeuAc-Gal-(Gal-GlcNAc-)GalNAc\t");
		title2.append("NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc\t");*/
		HashMap<String, int[]> map = new HashMap<String, int[]>();
		HashMap<String, int[]> hmap = new HashMap<String, int[]>();
		HashMap<String, String[]> m1 = new HashMap<String, String[]>();
		HashMap<String, String[]> hm1 = new HashMap<String, String[]>();
//		HashMap<String, Boolean> mb1 = new HashMap<String, Boolean>();
		if(s1!=null){
			ExcelReader r1 = new ExcelReader(s1, 1);
			String[] l1 = r1.readLine();
			while ((l1 = r1.readLine()) != null) {
				double sitescore = Double.parseDouble(l1[4]);
				String key = l1[0] + l1[1] + l1[2] + l1[6];
				String key2 = l1[0] + "\t" + l1[1] + "\t" + l1[6];
				int type = judgeType(l1[2]);

				m1.put(key, l1);
				pepset.add(l1[7]);
				if (map.containsKey(key2)) {
					int[] types = map.get(key2);
					types[type]++;
					map.put(key2, types);
				} else {
					int[] types = new int[glycans.length];
					types[type] = 1;
					map.put(key2, types);
				}
				
				if(sitescore>=score){
					hm1.put(key, l1);
					if (hmap.containsKey(key2)) {
						int[] types = hmap.get(key2);
						types[type]++;
						hmap.put(key2, types);
					} else {
						int[] types = new int[glycans.length];
						types[type] = 1;
						hmap.put(key2, types);
					}
				}				
			}
			r1.close();
		}

		HashMap<String, String[]> m2 = new HashMap<String, String[]>();
		HashMap<String, String[]> hm2 = new HashMap<String, String[]>();
//		HashMap<String, Boolean> mb2 = new HashMap<String, Boolean>();
		if(s2!=null){
			ExcelReader r2 = new ExcelReader(s2, 1);
			String[] l2 = r2.readLine();
			while ((l2 = r2.readLine()) != null) {
				double sitescore = Double.parseDouble(l2[4]);
				String key = l2[0] + l2[1] + l2[2] + l2[6];
				String key2 = l2[0] + "\t" + l2[1] + "\t" + l2[6];
				int type = judgeType(l2[2]);
				
				m2.put(key, l2);
				pepset.add(l2[7]);
				if (map.containsKey(key2)) {
					int[] types = map.get(key2);
					types[type]++;
					map.put(key2, types);
				} else {
					int[] types = new int[glycans.length];
					types[type] = 1;
					map.put(key2, types);
				}
				
				if(sitescore>=score){
					hm2.put(key, l2);
					if (hmap.containsKey(key2)) {
						int[] types = hmap.get(key2);
						types[type]++;
						hmap.put(key2, types);
					} else {
						int[] types = new int[glycans.length];
						types[type] = 1;
						hmap.put(key2, types);
					}
				}				
			}
			r2.close();
		}
		
		HashMap<String, String[]> m3 = new HashMap<String, String[]>();
		HashMap<String, String[]> hm3 = new HashMap<String, String[]>();
//		HashMap<String, Boolean> mb3 = new HashMap<String, Boolean>();
		if(s3!=null){
			ExcelReader r3 = new ExcelReader(s3, 1);
			String[] l3 = r3.readLine();
			while ((l3 = r3.readLine()) != null) {
				double sitescore = Double.parseDouble(l3[4]);
				String key = l3[0] + l3[1] + l3[2] + l3[6];
				String key2 = l3[0] + "\t" + l3[1] + "\t" + l3[6];
				int type = judgeType(l3[2]);
				
				m3.put(key, l3);
				pepset.add(l3[7]);
				if (map.containsKey(key2)) {
					int[] types = map.get(key2);
					types[type]++;
					map.put(key2, types);
				} else {
					int[] types = new int[glycans.length];
					types[type] = 1;
					map.put(key2, types);
				}
				
				if(sitescore>=score){
					hm3.put(key, l3);
					if (hmap.containsKey(key2)) {
						int[] types = hmap.get(key2);
						types[type]++;
						hmap.put(key2, types);
					} else {
						int[] types = new int[glycans.length];
						types[type] = 1;
						hmap.put(key2, types);
					}
				}				
			}
			r3.close();
		}

		System.out.println("pepset\t" + pepset.size());
		HashSet<String> set = new HashSet<String>();
		set.addAll(m1.keySet());
		set.addAll(m2.keySet());
		set.addAll(m3.keySet());

		int count = 0;
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			boolean high = false;
			String[] line = null;
			if (m1.containsKey(key)) {
				line = m1.get(key);
				sb2.append("+\t");
				if(hm1.containsKey(key)){
					sb3.append("+\t");
					high = true;
				}else{
					sb3.append("\t");
				}
			} else {
				sb2.append("\t");
				sb3.append("\t");
			}
			if (m2.containsKey(key)) {
				line = m2.get(key);
				sb2.append("+\t");
				if(hm2.containsKey(key)){
					sb3.append("+\t");
					high = true;
				}else{
					sb3.append("\t");
				}
			} else {
				sb2.append("\t");
				sb3.append("\t");
			}
			if (m3.containsKey(key)) {
				line = m3.get(key);
				sb2.append("+\t");
				if(hm3.containsKey(key)){
					sb3.append("+\t");
					high = true;
				}else{
					sb3.append("\t");
				}
			} else {
				sb2.append("\t");
				sb3.append("\t");
			}
			sb.append(line[0]).append("\t");
			sb.append(line[1]).append("\t");
			sb.append(line[2]).append("\t");
//			sb.append(line[4]).append("\t");
			sb.append(line[5]).append("\t");
			sb.append(line[6]).append("\t");
			writer.addContent(sb+""+sb2, 2, format);
			if(high){
				writer.addContent(sb+""+sb3, 0, format);
			}
			count++;
			/*boolean b = false;
			if (mb1.containsKey(key) && mb1.get(key)) {
				b = true;
			}
			if (mb2.containsKey(key) && mb2.get(key)) {
				b = true;
			}
			if (mb3.containsKey(key) && mb3.get(key)) {
				b = true;
			}
			if (b) {
				count++;
			}*/
		}
		System.out.println("Count\t" + count);

		Iterator<String> it2 = hmap.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			int[] types = hmap.get(key);
			for (int i = 0; i < types.length; i++) {
				sb.append(types[i]).append("\t");
			}
			writer.addContent(sb.toString(), 1, format);
		}
		
		Iterator<String> it3 = map.keySet().iterator();
		while (it3.hasNext()) {
			String key = it3.next();
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			int[] types = map.get(key);
			for (int i = 0; i < types.length; i++) {
				sb.append(types[i]).append("\t");
			}
			writer.addContent(sb.toString(), 3, format);
		}

		writer.close();
	}
	
	private static void combine2(String s1, String s2, String s3, String out, double formThres, double siteThres)
			throws IOException, JXLException {

		HashSet<String> pepset = new HashSet<String>();
		ExcelWriter writer = new ExcelWriter(out, new String[]{"glycosylation sites unambiguous", "glycoforms unambiguous", 
				"glycosylation sites all", "site-specific glycoforms all"});
		ExcelFormat format = ExcelFormat.normalFormat;
		StringBuilder title1 = new StringBuilder();
		title1.append("Site\t");
		title1.append("Sequence around\t");
		title1.append("Glycan\t");
		title1.append("Proteins\t");
		title1.append("Protein\t");
		title1.append("Trypsin\t");
		title1.append("Trypsin+GluC\t");
		title1.append("Elastase\t");
		writer.addTitle(title1.toString(), 0, format);
		writer.addTitle(title1.toString(), 2, format);

		StringBuilder title2 = new StringBuilder();
		title2.append("Site\t");
		title2.append("Sequence around\t");
		title2.append("Protein\t");
		for(int i=0;i<glycans.length;i++){
			title2.append(glycans[i]).append("\t");
		}
		writer.addTitle(title2.toString(), 1, format);
		writer.addTitle(title2.toString(), 3, format);
		/*title2.append("GalNAc\t");
		title2.append("Gal-GalNAc\t");
		title2.append("NeuAc-GalNAc\t");
		title2.append("NeuAc-Gal-GalNAc\t");
		title2.append("Gal-(NeuAc-)GalNAc\t");
		title2.append("NeuAc-Gal-(NeuAc-)GalNAc\t");
		title2.append("Gal-(GlcNAc-)GalNAc\t");
		title2.append("NeuAc-Gal-(GlcNAc-)GalNAc\t");
		title2.append("Gal-(Gal-GlcNAc-)GalNAc\t");
		title2.append("NeuAc-Gal-(Gal-GlcNAc-)GalNAc\t");
		title2.append("NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc\t");*/
		HashMap<String, int[]> map = new HashMap<String, int[]>();
		HashMap<String, int[]> hmap = new HashMap<String, int[]>();
		HashMap<String, String[]> m1 = new HashMap<String, String[]>();
		HashMap<String, String[]> hm1 = new HashMap<String, String[]>();
//		HashMap<String, Boolean> mb1 = new HashMap<String, Boolean>();
		if(s1!=null){
			ExcelReader r1 = new ExcelReader(s1, 1);
			String[] l1 = r1.readLine();
			while ((l1 = r1.readLine()) != null) {
				double formscore = Double.parseDouble(l1[4]);
				double sitescore = Double.parseDouble(l1[5]);
				String key = l1[0] + l1[1] + l1[2] + l1[7];
				String key2 = l1[0] + "\t" + l1[1] + "\t" + l1[7];
				int type = judgeType(l1[2]);

				m1.put(key, l1);
				pepset.add(l1[7]);
				if (map.containsKey(key2)) {
					int[] types = map.get(key2);
					types[type]++;
					map.put(key2, types);
				} else {
					int[] types = new int[glycans.length];
					types[type] = 1;
					map.put(key2, types);
				}
				
				if(formscore>=formThres && sitescore>=siteThres){
					hm1.put(key, l1);
					if (hmap.containsKey(key2)) {
						int[] types = hmap.get(key2);
						types[type]++;
						hmap.put(key2, types);
					} else {
						int[] types = new int[glycans.length];
						types[type] = 1;
						hmap.put(key2, types);
					}
				}				
			}
			r1.close();
		}

		HashMap<String, String[]> m2 = new HashMap<String, String[]>();
		HashMap<String, String[]> hm2 = new HashMap<String, String[]>();
//		HashMap<String, Boolean> mb2 = new HashMap<String, Boolean>();
		if(s2!=null){
			ExcelReader r2 = new ExcelReader(s2, 1);
			String[] l2 = r2.readLine();
			while ((l2 = r2.readLine()) != null) {
				double formscore = Double.parseDouble(l2[4]);
				double sitescore = Double.parseDouble(l2[5]);
				String key = l2[0] + l2[1] + l2[2] + l2[7];
				String key2 = l2[0] + "\t" + l2[1] + "\t" + l2[7];
				int type = judgeType(l2[2]);
				
				m2.put(key, l2);
				pepset.add(l2[7]);
				if (map.containsKey(key2)) {
					int[] types = map.get(key2);
					types[type]++;
					map.put(key2, types);
				} else {
					int[] types = new int[glycans.length];
					types[type] = 1;
					map.put(key2, types);
				}
				
				if(formscore>=formThres && sitescore>=siteThres){
					hm2.put(key, l2);
					if (hmap.containsKey(key2)) {
						int[] types = hmap.get(key2);
						types[type]++;
						hmap.put(key2, types);
					} else {
						int[] types = new int[glycans.length];
						types[type] = 1;
						hmap.put(key2, types);
					}
				}				
			}
			r2.close();
		}
		
		HashMap<String, String[]> m3 = new HashMap<String, String[]>();
		HashMap<String, String[]> hm3 = new HashMap<String, String[]>();
//		HashMap<String, Boolean> mb3 = new HashMap<String, Boolean>();
		if(s3!=null){
			ExcelReader r3 = new ExcelReader(s3, 1);
			String[] l3 = r3.readLine();
			while ((l3 = r3.readLine()) != null) {
				double formscore = Double.parseDouble(l3[4]);
				double sitescore = Double.parseDouble(l3[5]);
				String key = l3[0] + l3[1] + l3[2] + l3[7];
				String key2 = l3[0] + "\t" + l3[1] + "\t" + l3[7];
				int type = judgeType(l3[2]);
				
				m3.put(key, l3);
				pepset.add(l3[7]);
				if (map.containsKey(key2)) {
					int[] types = map.get(key2);
					types[type]++;
					map.put(key2, types);
				} else {
					int[] types = new int[glycans.length];
					types[type] = 1;
					map.put(key2, types);
				}
				
				if(formscore>=formThres && sitescore>=siteThres){
					hm3.put(key, l3);
					if (hmap.containsKey(key2)) {
						int[] types = hmap.get(key2);
						types[type]++;
						hmap.put(key2, types);
					} else {
						int[] types = new int[glycans.length];
						types[type] = 1;
						hmap.put(key2, types);
					}
				}				
			}
			r3.close();
		}

		System.out.println("pepset\t" + pepset.size());
		HashSet<String> set = new HashSet<String>();
		set.addAll(m1.keySet());
		set.addAll(m2.keySet());
		set.addAll(m3.keySet());

		int count = 0;
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			boolean high = false;
			String[] line = null;
			if (m1.containsKey(key)) {
				line = m1.get(key);
				sb2.append("+\t");
				if(hm1.containsKey(key)){
					sb3.append("+\t");
					high = true;
				}else{
					sb3.append("\t");
				}
			} else {
				sb2.append("\t");
				sb3.append("\t");
			}
			if (m2.containsKey(key)) {
				line = m2.get(key);
				sb2.append("+\t");
				if(hm2.containsKey(key)){
					sb3.append("+\t");
					high = true;
				}else{
					sb3.append("\t");
				}
			} else {
				sb2.append("\t");
				sb3.append("\t");
			}
			if (m3.containsKey(key)) {
				line = m3.get(key);
				sb2.append("+\t");
				if(hm3.containsKey(key)){
					sb3.append("+\t");
					high = true;
				}else{
					sb3.append("\t");
				}
			} else {
				sb2.append("\t");
				sb3.append("\t");
			}
			sb.append(line[0]).append("\t");
			sb.append(line[1]).append("\t");
			sb.append(line[2]).append("\t");
//			sb.append(line[4]).append("\t");
			sb.append(line[5]).append("\t");
			sb.append(line[6]).append("\t");
			writer.addContent(sb+""+sb2, 2, format);
			if(high){
				writer.addContent(sb+""+sb3, 0, format);
			}
			count++;
			/*boolean b = false;
			if (mb1.containsKey(key) && mb1.get(key)) {
				b = true;
			}
			if (mb2.containsKey(key) && mb2.get(key)) {
				b = true;
			}
			if (mb3.containsKey(key) && mb3.get(key)) {
				b = true;
			}
			if (b) {
				count++;
			}*/
		}
		System.out.println("Count\t" + count);

		Iterator<String> it2 = hmap.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			int[] types = hmap.get(key);
			for (int i = 0; i < types.length; i++) {
				sb.append(types[i]).append("\t");
			}
			writer.addContent(sb.toString(), 1, format);
		}
		
		Iterator<String> it3 = map.keySet().iterator();
		while (it3.hasNext()) {
			String key = it3.next();
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			int[] types = map.get(key);
			for (int i = 0; i < types.length; i++) {
				sb.append(types[i]).append("\t");
			}
			writer.addContent(sb.toString(), 3, format);
		}

		writer.close();
	}
	
	private static int judgeType(String glycan) {
		int type = -1;
		for(int i=0;i<glycans.length;i++){
			if(glycan.equals(glycans[i])){
				type = i;
				break;
			}
		}
		/*if (glycan.equals("GalNAc")) {
			type = 0;
		} else if (glycan.equals("Gal-GalNAc")) {
			type = 1;
		} else if (glycan.equals("NeuAc-GalNAc")) {
			type = 2;
		} else if (glycan.equals("NeuAc-Gal-GalNAc")) {
			type = 3;
		} else if (glycan.equals("Gal-(NeuAc-)GalNAc")) {
			type = 4;
		} else if (glycan.equals("NeuAc-Gal-(NeuAc-)GalNAc")) {
			type = 5;
		} else if (glycan.equals("Gal-(GlcNAc-)GalNAc")) {
			type = 6;
		} else if (glycan.equals("NeuAc-Gal-(GlcNAc-)GalNAc")) {
			type = 7;
		} else if (glycan.equals("Gal-(Gal-GlcNAc-)GalNAc")) {
			type = 8;
		} else if (glycan.equals("NeuAc-Gal-(Gal-GlcNAc-)GalNAc")) {
			type = 9;
		} else if (glycan.equals("NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc")) {
			type = 10;
		} else if (glycan.equals("Gal-(Fuc-)GlcNAc-(NeuAc-Gal-)GalNAc")) {
			type = 11;
		} else if (glycan.equals("NeuAc-Gal-(Fuc-)GlcNAc-(NeuAc-Gal-)GalNAc")) {
			type = 12;
		} else if (glycan.equals("NeuAc-NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc")) {
			type = 13;
		} else if (glycan.equals("NeuAc-Gal-(Gal-(HSO3-)GlcNAc-)GalNAc")) {
			type = 14;
		}*/
		return type;
	}

	private static void fitting(String in) throws IOException{
		ArrayList<Double> xlist = new ArrayList<Double>();
		ArrayList<Double> ylist = new ArrayList<Double>();
		File[] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			if(!files[i].getName().endsWith("txt")) continue;
			BufferedReader reader = new BufferedReader(new FileReader(files[i]));
			String line = null;
			while((line=reader.readLine())!=null){
				String[] cs = line.split("\t");
				xlist.add(Double.parseDouble(cs[0]));
				ylist.add(Double.parseDouble(cs[1]));
			}
			reader.close();
		}
		
		double[] x = new double[xlist.size()];
		double[] y = new double[ylist.size()];
		for (int i = 0; i < x.length; i++) {
			x[i] = xlist.get(i);
			y[i] = ylist.get(i);
		}

		Regression reg = new Regression(x, y);
		reg.linear();
		double[] fit = reg.getBestEstimates();
System.out.println(fit[0]+"\t"+fit[1]+"\t"+reg.getCoefficientOfDetermination());
	}
	
	private static void compare(String s1, String s2) throws IOException, JXLException{
		HashSet<String> s11 = new HashSet<String>();
		HashSet<String> s12 = new HashSet<String>();
		HashSet<String> s21 = new HashSet<String>();
		HashSet<String> s22 = new HashSet<String>();
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		ExcelReader r1 = new ExcelReader(s1, new int[]{0, 1});
		ExcelReader r2 = new ExcelReader(s2, new int[]{0, 1});
		String [] line = r1.readLine(0);
		while((line=r1.readLine(0))!=null){
			String key = line[0]+"\t"+line[1]+"\t"+line[2];
			s11.add(key);
		}
		line = r1.readLine(1);
		while((line=r1.readLine(1))!=null){
			String key = line[0]+"\t"+line[1];
			s12.add(key);
		}
		r1.close();
		
		line = r2.readLine(0);
		while((line=r2.readLine(0))!=null){
			String key = line[0]+"\t"+line[1]+"\t"+line[2];
			s21.add(key);
		}
		line = r2.readLine(1);
		while((line=r2.readLine(1))!=null){
			String key = line[0]+"\t"+line[1];
			s22.add(key);
		}
		r2.close();
		
		set1.addAll(s11);
		set1.addAll(s21);
		set2.addAll(s12);
		set2.addAll(s22);
		System.out.println(s11.size()+"\t"+s21.size()+"\t"+(s11.size()+s21.size()-set1.size()));
		System.out.println(s12.size()+"\t"+s22.size()+"\t"+(s12.size()+s22.size()-set2.size()));
	}
	
	private static void compareSite(String s1, String s2, String out) throws IOException, JXLException{
		DecimalFormat df2 = DecimalFormats.DF0_2;
		HashMap<String, Integer> sitemap1 = new HashMap<String, Integer>();
		HashMap<String, Integer> sitemap2 = new HashMap<String, Integer>();
		HashMap<String, Integer> glycanmap1 = new HashMap<String, Integer>();
		HashMap<String, Integer> glycanmap2 = new HashMap<String, Integer>();
		
		ExcelReader reader1 = new ExcelReader(s1, new int[]{4, 7});
		String[] line = reader1.readLine(0);
		while((line=reader1.readLine(0))!=null){
			String key = line[0]+"\t"+line[1]+"\t"+line[4];
			int count = Integer.parseInt(line[5]);
			if(count>=5)
				sitemap1.put(key, count);
		}
		line = reader1.readLine(1);
		while((line=reader1.readLine(1))!=null){
			String key = line[0]+"\t"+line[1]+"\t"+line[2]+"\t"+line[5];
			int count = Integer.parseInt(line[6]);
			if(count>=5)
				glycanmap1.put(key, count);
		}
		reader1.close();
		
		ExcelReader reader2 = new ExcelReader(s2, new int[]{4, 7});
		line = reader2.readLine(0);
		while((line=reader2.readLine(0))!=null){
			String key = line[0]+"\t"+line[1]+"\t"+line[4];
			int count = Integer.parseInt(line[5]);
			if(count>=5)
				sitemap2.put(key, count);
		}
		line = reader2.readLine(1);
		while((line=reader2.readLine(1))!=null){
			String key = line[0]+"\t"+line[1]+"\t"+line[2]+"\t"+line[5];
			int count = Integer.parseInt(line[6]);
			if(count>=5)
				glycanmap2.put(key, count);
		}
		reader2.close();

		HashSet<String> siteset = new HashSet<String>();
		HashSet<String> glycanset = new HashSet<String>();

		siteset.addAll(sitemap1.keySet());
		siteset.addAll(sitemap2.keySet());
		glycanset.addAll(glycanmap1.keySet());
		glycanset.addAll(glycanmap2.keySet());

		ExcelWriter writer = new ExcelWriter(out, new String[]{"Content", "Glycosylation sites", "Site-specific glycans", "Occupation rates"});
		ExcelFormat format = ExcelFormat.normalFormat;
		String title1 = "Site\tSequence window\tProtein\tNormal spectra count\tHCC spectra count";
		String title2 = "Site\tSequence window\tGlycan\tProtein\tNormal spectra count\tHCC spectra count";
		String title3 = "Site\tSequence window\tGlycan\tProtein\tNormal occupation rates\tHCC occupation rates";
		writer.addTitle(title1, 1, format);
		writer.addTitle(title2, 2, format);
		writer.addTitle(title3, 3, format);
		
		Iterator<String> it1 = siteset.iterator();
		while(it1.hasNext()){
			String key = it1.next();
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			if(sitemap1.containsKey(key)){
				sb.append(sitemap1.get(key)).append("\t");
			}else{
				sb.append("0\t");
			}
			if(sitemap2.containsKey(key)){
				sb.append(sitemap2.get(key)).append("\t");
			}else{
				sb.append("0\t");
			}
			writer.addContent(sb.toString(), 1, format);
		}
		
		Iterator<String> it2 = glycanset.iterator();
		while(it2.hasNext()){
			String key = it2.next();
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			
			String[] cs = key.split("\t");
			String key2 = cs[0]+"\t"+cs[1]+"\t"+cs[3];
			StringBuilder sb2 = new StringBuilder();
			sb2.append(key).append("\t");
			
			if(glycanmap1.containsKey(key)){
				sb.append(glycanmap1.get(key)).append("\t");
				double rate = (double)glycanmap1.get(key)/(double)sitemap1.get(key2);
				sb2.append(df2.format(rate)).append("\t");
			}else{
				sb.append("0\t");
				sb2.append("0.0\t");
			}
			if(glycanmap2.containsKey(key)){
				sb.append(glycanmap2.get(key)).append("\t");
				double rate = (double)glycanmap2.get(key)/(double)sitemap2.get(key2);
				sb2.append(df2.format(rate)).append("\t");
			}else{
				sb.append("0\t");
				sb2.append("0.0\t");
			}
			writer.addContent(sb.toString(), 2, format);
			writer.addContent(sb2.toString(), 3, format);
		}
		writer.close();
	}
		
	private static void compare2(String s1, String s2, String out) throws IOException, JXLException{
		HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		HashMap<String, Integer> map2 = new HashMap<String, Integer>();
		HashMap<String, Double> map3 = new HashMap<String, Double>();
		HashMap<String, Double> map4 = new HashMap<String, Double>();
		ExcelReader r1 = new ExcelReader(s1, 1);
		ExcelReader r2 = new ExcelReader(s2, 1);
		String [] title = r1.readLine();
		String [] line = null;
		while((line=r1.readLine())!=null){
			String ref = line[2].split("\\|")[1];
			int [] counts = new int[glycans.length];
			for(int i=3;i<line.length;i++){
				counts[i-3] = Integer.parseInt(line[i]);
			}
			int total = MathTool.getTotal(counts);
			for(int i=0;i<counts.length;i++){
				if(counts[i]>=5){
					map1.put(ref+"        "+line[0]+"        "+title[i+3], counts[i]);
					map3.put(ref+"        "+line[0]+"        "+title[i+3], (double)counts[i]/(double)total);
				}
			}
		}
		r1.close();

		line = r2.readLine();
		while((line=r2.readLine())!=null){
			String ref = line[2].split("\\|")[1];
			int [] counts = new int[glycans.length];
			for(int i=3;i<line.length;i++){
				counts[i-3] = Integer.parseInt(line[i]);
			}
			int total = MathTool.getTotal(counts);
			for(int i=0;i<counts.length;i++){
				if(counts[i]>=5){
					map2.put(ref+"        "+line[0]+"        "+title[i+3], counts[i]);
					map4.put(ref+"        "+line[0]+"        "+title[i+3], (double)counts[i]/(double)total);
				}
			}
		}
		r2.close();
		
		HashSet<String> set = new HashSet<String>();
		set.addAll(map1.keySet());
		set.addAll(map2.keySet());
		
		System.out.println(map1.size()+"\t"+map2.size()+"\t"+(map1.size()+map2.size()-set.size()));
		
		String[] keys = set.toArray(new String[set.size()]);
		Arrays.sort(keys);
		/*for(String key : keys){
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			if(map1.containsKey(key)){
				sb.append(map1.get(key)).append("\t");
			}else{
				sb.append("0\t");
			}
			if(map2.containsKey(key)){
				sb.append(map2.get(key)).append("\t");
			}else{
				sb.append("0\t");
			}
			if(map3.containsKey(key)){
				sb.append(map3.get(key)).append("\t");
			}else{
				sb.append("0\t");
			}
			if(map4.containsKey(key)){
				sb.append(map4.get(key)).append("\t");
			}else{
				sb.append("0\t");
			}
		}*/
		
		int unitWeight = 400;
		int unitHeight = 50;

		int width = 2300;
		int height = keys.length * unitHeight + 800;

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics graphics = image.getGraphics();
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, width, height);
		
		Color c1 = new Color(0, 102, 204);
		Color c2 = new Color(255, 153, 51);
		Color c3 = new Color(0, 102, 204);
		Color c4 = new Color(255, 153, 51);

		for (int i = 0; i < keys.length; i++) {

			int[] counts = new int[2];
			double [] percent = new double[2];
			if(map1.containsKey(keys[i])){
				counts[0]=  map1.get(keys[i]);
			}
			if(map2.containsKey(keys[i])){
				counts[1]=  map2.get(keys[i]);
			}
			if(map3.containsKey(keys[i])){
				percent[0]=  map3.get(keys[i]);
			}
			if(map4.containsKey(keys[i])){
				percent[1]=  map4.get(keys[i]);
			}

			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Arial", Font.PLAIN, 40));
			g2.drawString(keys[i], 30, 435 + i * unitHeight);

			double p1 = (double)counts[0]/3957.0/(double)(counts[0]/3957.0+counts[1]/3458.0);
			double p2 = (double)counts[1]/3458.0/(double)(counts[0]/3957.0+counts[1]/3458.0);
			double p3 = percent[0]/(percent[0]+percent[1]);
			double p4 = percent[1]/(percent[0]+percent[1]);
			
			String[] cs = keys[i].split("[ ]+");
			StringBuilder keysb = new StringBuilder();
			keysb.append(cs[2]).append("        ");
			keysb.append(cs[1]).append("        ");
			keysb.append(cs[0]);
			System.out.println(keys[i]+"\t"+keysb+"\t"+counts[0]+"\t"+counts[1]+"\t"+percent[0]+"\t"+percent[1]+"\t"+p1+"\t"+p2+"\t"+p3+"\t"+p4);
			
			int position1 = (int)(p1*unitWeight);
			int position2 = (int)(p3*unitWeight);
			
			g2.setColor(Color.BLACK);
			g2.drawRect(1400, 401 + i * unitHeight,
					position1, unitHeight);
			g2.setColor(c1);
			g2.fillRect(1400, 401 + i * unitHeight,
					position1, unitHeight);

			g2.setColor(Color.BLACK);
			g2.drawRect(1400+position1, 401 + i * unitHeight,
					unitWeight-position1, unitHeight);
			g2.setColor(c2);
			g2.fillRect(1400+position1, 401 + i * unitHeight,
					unitWeight-position1, unitHeight);
			
			g2.setColor(Color.BLACK);
			g2.drawRect(1901, 401 + i * unitHeight,
					position2, unitHeight);
			g2.setColor(c3);
			g2.fillRect(1901, 401 + i * unitHeight,
					position2, unitHeight);

			g2.setColor(Color.BLACK);
			g2.drawRect(1901+position2, 401 + i * unitHeight,
					unitWeight-position2, unitHeight);
			g2.setColor(c4);
			g2.fillRect(1901+position2, 401 + i * unitHeight,
					unitWeight-position2, unitHeight);
			
		}			
		
		g2.setColor(c1);

		g2.drawRect(500, 551 + keys.length * unitHeight,
				unitWeight/2, unitHeight);
		g2.fillRect(500, 551 + keys.length * unitHeight,
				unitWeight/2, unitHeight);

		g2.setColor(c2);
		
		g2.drawRect(500, 551 + keys.length * unitHeight,
				unitWeight/2, unitHeight);
		g2.fillRect(500, 551 + keys.length * unitHeight,
				unitWeight/2, unitHeight);
		
		g2.setFont(new Font("Arial", Font.PLAIN, 50));
		g2.setColor(Color.BLACK);
		g2.drawString("%", 500,
				685 + keys.length * unitHeight);

		/*for (int i = 0; i < 5; i++) {

			int colorid = (int) (0.2 * (double) i * 255.0);

			Color color = new Color(colorid, 255 - colorid, 0);

			
		}*/

		/*
		 * g2.setFont(new Font("Arial", Font.PLAIN, 22));
		 * g2.drawString("High mannose", 480, 171 + countArrays[0].length * 80);
		 * g2.drawString("Complex/Hybrid", 480, 231 + countArrays[0].length *
		 * 80);
		 */

		ImageIO.write(image, "PNG", new File(out));
	}
	
	private static void typeCount(String in, int sheet) throws IOException, JXLException{
		int [] count = new int[glycans.length];
		ExcelReader reader = new ExcelReader(in, sheet	);
		String[] line = reader.readLine();
		while((line=reader.readLine())!=null){
			String glycan = line[2];
			int id = judgeType(glycan);
			count[id]++;
		}
		reader.close();
		
		for(int i=0;i<count.length;i++){
			System.out.println(i+"\t"+glycans[i]+"\t"+count[i]);
		}
	}
	
	private static void typeCount2(String in, double formThres, double siteThres) throws IOException, JXLException{
		int [] count = new int[glycans.length];
		ExcelReader reader = new ExcelReader(in, 2);
		String[] line = reader.readLine();
		while((line=reader.readLine())!=null){
			String glycan = line[2];
			int id = judgeType(glycan);
			double formscore = Double.parseDouble(line[4]);
			double sitescore = Double.parseDouble(line[5]);
			if(formscore<formThres || sitescore<siteThres){
				continue;
			}
			count[id]++;
		}
		reader.close();
		
		for(int i=0;i<count.length;i++){
			System.out.println(i+"\t"+glycans[i]+"\t"+count[i]);
		}
	}
	
	private static void combineNormal(String n1, String n2) throws IOException, JXLException{
		HashSet<String> glycoset1 = new HashSet<String>();
		HashSet<String> siteset1 = new HashSet<String>();
		HashSet<String> proset1 = new HashSet<String>();
		ExcelReader r1 = new ExcelReader(n1, 2);
		String[] line = r1.readLine();
		while((line=r1.readLine())!=null){
			glycoset1.add(line[0]+"\t"+line[2]+"\t"+line[4]);
			siteset1.add(line[0]+"\t"+line[4]);
			proset1.add(line[4]);
		}
		r1.close();
		
		HashSet<String> glycoset2 = new HashSet<String>();
		HashSet<String> siteset2 = new HashSet<String>();
		HashSet<String> proset2 = new HashSet<String>();
		ExcelReader r2 = new ExcelReader(n2, 2);
		line = r2.readLine();
		while((line=r2.readLine())!=null){
			glycoset2.add(line[0]+"\t"+line[2]+"\t"+line[4]);
			siteset2.add(line[0]+"\t"+line[4]);
			proset2.add(line[4]);
		}
		r2.close();
		
		HashSet<String> gset = new HashSet<String>();
		HashSet<String> sset = new HashSet<String>();
		HashSet<String> pset = new HashSet<String>();
		gset.addAll(glycoset1);
		gset.addAll(glycoset2);
		pset.addAll(proset1);
		sset.addAll(siteset1);
		sset.addAll(siteset2);
		pset.addAll(proset2);
		System.out.println(glycoset1.size()+"\t"+glycoset2.size()+"\t"+gset.size());
		System.out.println(siteset1.size()+"\t"+siteset2.size()+"\t"+sset.size());
		System.out.println(proset1.size()+"\t"+proset2.size()+"\t"+pset.size());
	}
	
	private static void combineAll(String [] rs, int sheet) throws IOException, JXLException{
		
		HashSet<String> [] glycoset = new HashSet[rs.length];
		HashSet<String> [] siteset = new HashSet[rs.length];
		HashSet<String> [] proset = new HashSet[rs.length];
		
		for(int i=0;i<rs.length;i++){
			glycoset[i] = new HashSet<String>();
			siteset[i] = new HashSet<String>();
			proset[i] = new HashSet<String>();
			
			ExcelReader reader = new ExcelReader(rs[i], sheet);
			String[] line = reader.readLine();
			while((line=reader.readLine())!=null){
				glycoset[i].add(line[0]+"\t"+line[2]+"\t"+line[4]);
				siteset[i].add(line[0]+"\t"+line[4]);
				proset[i].add(line[4]);
			}
			reader.close();
		}

		HashSet<String> gset = new HashSet<String>();
		HashSet<String> sset = new HashSet<String>();
		HashSet<String> pset = new HashSet<String>();
		for(int i=0;i<rs.length;i++){
			gset.addAll(glycoset[i]);
			sset.addAll(siteset[i]);
			pset.addAll(proset[i]);
		}
		for(int i=0;i<rs.length;i++){
			System.out.println(glycoset[i].size()+"\t"+gset.size());
			System.out.println(siteset[i].size()+"\t"+sset.size());
			System.out.println(proset[i].size()+"\t"+pset.size());
		}
	}
	
	private static void combineAllSite(String[] rs) throws IOException,
			JXLException {

		HashSet<String>[] glycoset = new HashSet[rs.length];
		HashSet<String>[] siteset = new HashSet[rs.length];
		HashSet<String>[] proset = new HashSet[rs.length];

		for (int i = 0; i < rs.length; i++) {
			glycoset[i] = new HashSet<String>();
			siteset[i] = new HashSet<String>();
			proset[i] = new HashSet<String>();

			ExcelReader reader = new ExcelReader(rs[i], 4);
			String[] line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				glycoset[i].add(line[0] + "\t" + line[2] + "\t" + line[5]);
				siteset[i].add(line[0] + "\t" + line[5]);
				proset[i].add(line[5]);
			}
			reader.close();
		}

		HashSet<String> gset = new HashSet<String>();
		HashSet<String> sset = new HashSet<String>();
		HashSet<String> pset = new HashSet<String>();
		for (int i = 0; i < rs.length; i++) {
			gset.addAll(glycoset[i]);
			sset.addAll(siteset[i]);
			pset.addAll(proset[i]);
		}
		for (int i = 0; i < rs.length; i++) {
			System.out.println(glycoset[i].size() + "\t" + gset.size());
			System.out.println(siteset[i].size() + "\t" + sset.size());
			System.out.println(proset[i].size() + "\t" + pset.size());
		}
		
		Iterator<String> it = gset.iterator();
		while(it.hasNext()){
			String key = it.next();
			if(!glycoset[3].contains(key)){
				for(int i=0;i<3;i++){
					if(glycoset[i].contains(key)){
						System.out.println(i+"\t"+key);
					}
				}
			}
		}
	}
	
	private static void combineAllSite(String[] rs, String out) throws IOException,
			JXLException {

		HashSet<String>[] glycoset = new HashSet[rs.length];
		HashSet<String>[] siteset = new HashSet[rs.length];
		HashSet<String>[] proset = new HashSet[rs.length];

		for (int i = 0; i < rs.length; i++) {
			glycoset[i] = new HashSet<String>();
			siteset[i] = new HashSet<String>();
			proset[i] = new HashSet<String>();

			ExcelReader reader = new ExcelReader(rs[i], 2);
			String[] line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				glycoset[i].add(line[0] + "\t" + line[2] + "\t" + line[5]);
				siteset[i].add(line[0] + "\t" + line[5]);
				proset[i].add(line[5]);
			}
			reader.close();
		}

		HashSet<String> gset = new HashSet<String>();
		HashSet<String> sset = new HashSet<String>();
		HashSet<String> pset = new HashSet<String>();
		for (int i = 0; i < rs.length; i++) {
			gset.addAll(glycoset[i]);
			sset.addAll(siteset[i]);
			pset.addAll(proset[i]);
		}
		for (int i = 0; i < rs.length; i++) {
			System.out.println(glycoset[i].size() + "\t" + gset.size());
			System.out.println(siteset[i].size() + "\t" + sset.size());
			System.out.println(proset[i].size() + "\t" + pset.size());
		}
	}

	private static void combineTypeCount(String in, int sheet) throws IOException, JXLException{
		int[] count = new int[glycans.length];
		int[] hetero = new int[glycans.length];
		ExcelReader reader = new ExcelReader(in, sheet);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			int he = 0;
			for(int i=3;i<line.length;i++){
				int cc = Integer.parseInt(line[i]);
				if(cc>0){
					he++;
					count[i-3] += cc;
//					count[i-3] ++;
				}
			}
			hetero[he-1]++;
		}
		reader.close();
		int total = MathTool.getTotal(count);
		
		for(int i=0;i<count.length;i++){
			System.out.println(glycans[i]+"\t"+count[i]+"\t"+(double)count[i]/(double)total);
		}
		
		int total2 = MathTool.getTotal(hetero);
		for(int i=0;i<hetero.length;i++){
			System.out.println((i+1)+"\t"+hetero[i]+"\t"+(double)hetero[i]/(double)total2);
		}
	}
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

//		OGlycanPaperSI.combine2("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\trypsin\\trypsin.xls", 
//				"H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\TC\\TC.xls", 
//				"H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\Elastase2\\elastase.xls", 
//				"H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\no_homo.201405018.count.xls");
		
//		OGlycanPaperSI.combine("H:\\OGLYCAN\\OGlycan_20140503\\trypsin\\trypsin.xls", 
//				"H:\\OGLYCAN\\OGlycan_20140503\\TC\\TC.xls", 
//				"H:\\OGLYCAN\\OGlycan_20140503\\Elastase\\Elastase.xls", 
//				"H:\\OGLYCAN\\OGlycan_20140503\\20140503.xls");
//		OGlycanPaperSI.fitting("H:\\NGLYCO\\NGlyco_final_20140401\\decoy_match");
		
		/*OGlycanPaperSI.combine2("H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_HCC\\2D_trypsin_HCC_deglyco_no_iso.xls", 
				"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_HCC\\correction\\2D_trypsin_GluC_HCC_deglyco_corr_no_iso.xls", 
				"H:\\OGLYCAN2\\20141113_normal_HCC\\elastase_HCC\\2D_elastase_HCC_deglyco_no_iso.xls", 
				"H:\\OGLYCAN2\\20141211_14glyco\\HCC.combine.1212.xls", 0.6);
		OGlycanPaperSI.combine2("H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_normal\\correction\\2D_trypsin_normal_deglyco_corr_no_iso.xls", 
				"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_normal\\2D_trypsin_GluC_Normal_deglyco_no_iso.xls", 
				"H:\\OGLYCAN2\\20141113_normal_HCC\\elastase_normal\\2D_elastase_normal_deglyco_no_iso.xls", 
				"H:\\OGLYCAN2\\20141211_14glyco\\normal.combine.1212.xls", 0.6);
		OGlycanPaperSI.combine2("H:\\OGLYCAN2\\20141211_14glyco\\2D_trypsin\\2D_trypsin.xls", 
				"H:\\OGLYCAN2\\20141211_14glyco\\2D_TC\\2D_TC.xls", 
				"H:\\OGLYCAN2\\20141211_14glyco\\2D_elastase\\2D_elastase.xls", 
				"H:\\OGLYCAN2\\20141211_14glyco\\2D_combine.1216.xls", 0.6);*/
		/*OGlycanPaperSI.combine2("H:\\OGLYCAN2\\20141211_14glyco\\2D_trypsin\\2D_trypsin.20150116.xls", 
				"H:\\OGLYCAN2\\20141211_14glyco\\2D_TC\\2D_TC.20150116.xls", 
				"H:\\OGLYCAN2\\20141211_14glyco\\2D_elastase\\2D_elastase.20150116.xls", 
				"H:\\OGLYCAN2\\20141211_14glyco\\serum.count.info.20150116.xls", 0.5, 0.6);*/
//		OGlycanPaperSI.typeCount2("H:\\OGLYCAN2\\20141211_14glyco\\normal.site.info_F0.5.xls", 0.5, 0.6);
//		OGlycanPaperSI.typeCount2("H:\\OGLYCAN2\\20141211_14glyco\\HCC.site.info_F0.5.xls", 0.5, 0.6);
//		OGlycanPaperSI.typeCount2("H:\\OGLYCAN2\\20141211_14glyco\\serum.site.info_F0.5.xls", 0.0, 0.0);
//		OGlycanPaperSI.typeCount("H:\\OGLYCAN\\OGlycan_20140518_combine\\above_homo\\Elastase\\elastase.xls", 1);
//		OGlycanPaperSI.compare("H:\\OGLYCAN2\\20141113_normal_HCC\\normal.combine.1205.xls", "H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.1205.xls");
//		OGlycanPaperSI.compareSite("H:\\OGLYCAN2\\20141211_14glyco\\normal.site.info.20150123.xls", "H:\\OGLYCAN2\\20141211_14glyco\\HCC.site.info.20150123.xls", 
//				"H:\\OGLYCAN2\\20141211_14glyco\\normal.HCC.compare.20150123.xls");
//		OGlycanPaperSI.compare2("H:\\OGLYCAN2\\20141211_14glyco\\normal.combine.1212.xls", "H:\\OGLYCAN2\\20141211_14glyco\\HCC.combine.1212.xls",
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\test.png");
//		OGlycanPaperSI.combineNormal("H:\\OGLYCAN2\\20141024_15glyco\\2D_combine.xls", 
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.xls");
//		OGlycanPaperSI.combineAll(new String[]{"H:\\OGLYCAN2\\20141211_14glyco\\2D_combine.1212.xls", 
//				"H:\\OGLYCAN2\\20141211_14glyco\\normal.combine.1212.xls", "H:\\OGLYCAN2\\20141211_14glyco\\HCC.combine.1212.xls"}, 0);
//		OGlycanPaperSI.combineAllSite(new String[]{"H:\\OGLYCAN2\\20141211_14glyco\\serum.site info.xls", 
//				"H:\\OGLYCAN2\\20141211_14glyco\\normal.site.info.xls", "H:\\OGLYCAN2\\20141211_14glyco\\HCC.site.info.xls",
//				"H:\\OGLYCAN2\\20141211_14glyco\\combine.site.info.xls"});
//		OGlycanPaperSI.combineTypeCount("H:\\OGLYCAN2\\20141113_normal_HCC\\normal.combine.1205.xls", 1);
//		OGlycanPaperSI.combineTypeCount("H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.1205.xls", 1);
//		OGlycanPaperSI.combineTypeCount("H:\\OGLYCAN2\\20141024_15glyco\\2D_combine.1205.xls");
	}

}
