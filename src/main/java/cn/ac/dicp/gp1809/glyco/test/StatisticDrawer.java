/* 
 ******************************************************************************
 * File: StatisticDrawer.java * * * Created on 2013-7-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.dom4j.DocumentException;

import jxl.JXLException;
import jxl.write.WriteException;

import cn.ac.dicp.gp1809.glyco.Quan.labelFree2.GlycoLFFeasXMLReader2;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoStrucDrawer;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoDatabaseReader;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 * 
 * @version 2013-7-25, 15:30:41
 */
public class StatisticDrawer {
	
	private static void count(String in) throws IOException, JXLException{

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		HashMap<Integer, Integer> manmap = new HashMap<Integer, Integer>();
		HashSet<String> refset = new HashSet<String>();
		int i1 = 0;
		int i2 = 0;
		int i3 = 0;
		
		ExcelReader reader = new ExcelReader(in, 1);
		String[] line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			
			String name = line[8];
			String ref = line[16];
			String type = line[9];
			String ipi = ref.substring(4, 15);
			if(!refset.contains(ipi)){
				refset.add(ipi);
				System.out.println(ipi);
			}
			
			String dename = name.replaceAll("-", "");
			int gc = name.length()-dename.length()-5;
			
			if(type.equals("High mannose")){
				if(manmap.containsKey(gc)){
					manmap.put(gc, manmap.get(gc)+1);
				}else{
					manmap.put(gc, 1);
				}
				i1++;
			}else{
				if(name.contains("NeuAc")){
					i2++;
				}else{
					i3++;
				}
			}

			int count = Integer.parseInt(line[18]);
			if (map.containsKey(name)) {
				map.put(name, map.get(name)+1);
			} else {
				map.put(name, 1);
			}
		}
		
		/*for(String name : map.keySet()){
			System.out.println(name+"\t"+map.get(name));
		}*/

		for(Integer gc : manmap.keySet()){
//			System.out.println(gc+"\t"+manmap.get(gc));
		}
//		System.out.println(i1+"\t"+i2+"\t"+i3);
	}

	private static void draw(String in, String file, int width, int height)
			throws IOException, JXLException {

		HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		HashSet<String> set3 = new HashSet<String>();

		ExcelReader reader = new ExcelReader(in, 1);
		String[] line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String name = line[8];
			String ref = line[16];
			String type = line[9];

			if (type.equals("High mannose")) {
				set1.add(name);
			} else {
				if (name.contains("NeuAc")) {
					set2.add(name);
				} else {
					set3.add(name);
				}
			}
			int count = Integer.parseInt(line[18]);
			if (map.containsKey(ref)) {
				HashMap<String, Integer> m = map.get(ref);
				if (m.containsKey(name)) {
					m.put(name, m.get(name) + count);
				} else {
					m.put(name, count);
				}
			} else {
				HashMap<String, Integer> m = new HashMap<String, Integer>();
				m.put(name, count);
				map.put(ref, m);
			}
		}

		int max = 0;
		int total = 0;
		int bcount = 0;
		String[] refs = map.keySet().toArray(new String[map.size()]);
		String[] names1 = set1.toArray(new String[set1.size()]);
		String[] names2 = set2.toArray(new String[set2.size()]);
		String[] names3 = set3.toArray(new String[set3.size()]);
		String[] names = new String[names1.length + names2.length
				+ names3.length];

		Arrays.sort(refs);
		Arrays.sort(names1);
		Arrays.sort(names2);
		Arrays.sort(names3);
		System.arraycopy(names1, 0, names, 0, names1.length);
		System.arraycopy(names2, 0, names, names1.length, names2.length);
		System.arraycopy(names3, 0, names, names1.length + names2.length,
				names3.length);

		int[][] countArrays = new int[refs.length][names.length];

		HashMap<String, Integer> totalnamemap = new HashMap<String, Integer>();
		
		for (int i = 0; i < refs.length; i++) {

			HashMap<String, Integer> namemap = map.get(refs[i]);

			for (int j = 0; j < names.length; j++) {

				if (namemap.containsKey(names[j])) {

					bcount++;
					countArrays[i][j] = namemap.get(names[j]);
					total += countArrays[i][j];
					if (countArrays[i][j] > max) {
						max = countArrays[i][j];
					}
					
					if (totalnamemap.containsKey(names[j])) {
						totalnamemap.put(names[j], totalnamemap.get(names[j])+namemap.get(names[j]));
					}else{
						totalnamemap.put(names[j], namemap.get(names[j]));
					}
				}
			}
		}
		
	
		double average = (double) total / (double) bcount;
//		System.out.println(max + "\t" + average);
//		System.out.println(map.size() + "\t" + (set1.size() + set2.size() + set3.size()));

		/*BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics();
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, width, height);

		for (int i = 0; i < countArrays.length; i++) {
			for (int j = 0; j < countArrays[i].length; j++) {

				int count = countArrays[i][j];
				Color color;

				if (count == 0) {
					color = Color.WHITE;
				} else {
					if (j < names1.length) {
						if (count + 40 > 255) {
							int RGB = 510 - count - 40;
							color = new Color(RGB, 0, 0);
						} else {
							int RGB = 255 - count - 40;
							color = new Color(255, RGB, RGB);
						}
					} else if (j >= names1.length
							&& j < names1.length + names2.length) {
						if (count + 40 > 255) {
							int RGB = 510 - count - 40;
							color = new Color(0, RGB, 0);
						} else {
							int RGB = 255 - count - 40;
							color = new Color(RGB, 255, RGB);
						}
					} else {
						if (count + 40 > 255) {
							int RGB = 510 - count - 40;
							color = new Color(0, 0, RGB);
						} else {
							int RGB = 255 - count - 40;
							color = new Color(RGB, RGB, 255);
						}
					}
				}

				g2.setColor(color);
				g2.drawRect(1 + i * 30, 1 + j * 30, 30, 30);
				g2.fillRect(1 + i * 30, 1 + j * 30, 30, 30);
			}
		}

		ImageIO.write(image, "PNG", new File(file));
		*/
	}

	private static void draw(String in, String proteins, String file,
			int width, int height) throws IOException, JXLException {

		HashSet<String> proset = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(proteins));
		String bl = null;
		while ((bl = br.readLine()) != null) {
			proset.add(bl);
		}
		br.close();

		HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		HashSet<String> set3 = new HashSet<String>();

		ExcelReader reader = new ExcelReader(in, 1);
		String[] line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String name = line[8];
			String ref = line[16];
			String type = line[9];
			if (!proset.contains(ref))
				continue;

			if (type.equals("High mannose")) {
				set1.add(name);
			} else {
				if (name.contains("NeuAc")) {
					set2.add(name);
				} else {
					set3.add(name);
				}
			}
			int count = Integer.parseInt(line[18]);
			if (map.containsKey(ref)) {
				HashMap<String, Integer> m = map.get(ref);
				if (m.containsKey(name)) {
					m.put(name, m.get(name) + count);
				} else {
					m.put(name, count);
				}
			} else {
				HashMap<String, Integer> m = new HashMap<String, Integer>();
				m.put(name, count);
				map.put(ref, m);
			}
		}

		int max = 0;
		int total = 0;
		int bcount = 0;
		String[] refs = map.keySet().toArray(new String[map.size()]);
		String[] names1 = set1.toArray(new String[set1.size()]);
		String[] names2 = set2.toArray(new String[set2.size()]);
		String[] names3 = set3.toArray(new String[set3.size()]);
		String[] names = new String[names1.length + names2.length
				+ names3.length];

		Arrays.sort(refs);
		Arrays.sort(names1);
		Arrays.sort(names2);
		Arrays.sort(names3);
		System.arraycopy(names1, 0, names, 0, names1.length);
		System.arraycopy(names2, 0, names, names1.length, names2.length);
		System.arraycopy(names3, 0, names, names1.length + names2.length,
				names3.length);

		int[][] countArrays = new int[refs.length][names.length];

		for (int i = 0; i < refs.length; i++) {

			HashMap<String, Integer> namemap = map.get(refs[i]);

			for (int j = 0; j < names.length; j++) {

				if (namemap.containsKey(names[j])) {

					bcount++;
					countArrays[i][j] = namemap.get(names[j]);
					total += countArrays[i][j];
					if (countArrays[i][j] > max) {
						max = countArrays[i][j];
					}
				}
			}
		}

		double average = (double) total / (double) bcount;
		System.out.println(max + "\t" + average);
		System.out.println(map.size() + "\t" + (set1.size() + set2.size() + set3.size()));

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics();
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, width, height);

		for (int i = 0; i < countArrays.length; i++) {
			for (int j = 0; j < countArrays[i].length; j++) {

				int count = countArrays[i][j];
				Color color;

				if (count == 0) {
					color = Color.WHITE;
				} else {
					if (j < names1.length) {
						if (count + 40 > 255) {
							int RGB = 510 - count - 40;
							color = new Color(RGB, 0, 0);
						} else {
							int RGB = 255 - count - 40;
							color = new Color(255, RGB, RGB);
						}
					} else if (j >= names1.length
							&& j < names1.length + names2.length) {
						if (count + 40 > 255) {
							int RGB = 510 - count - 40;
							color = new Color(0, RGB, 0);
						} else {
							int RGB = 255 - count - 40;
							color = new Color(RGB, 255, RGB);
						}
					} else {
						if (count + 40 > 255) {
							int RGB = 510 - count - 40;
							color = new Color(0, 0, RGB);
						} else {
							int RGB = 255 - count - 40;
							color = new Color(RGB, RGB, 255);
						}
					}
				}

				g2.setColor(color);
				g2.drawRect(1 + i * 60, 1 + j * 30, 60, 30);
				g2.fillRect(1 + i * 60, 1 + j * 30, 60, 30);
			}
		}

		ImageIO.write(image, "PNG", new File(file));
	}

	
	private static void drawSite(String in, String proteins, String file) throws IOException, JXLException {

		HashMap<String, Integer> promap = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(proteins));
		int proid = 0;
		String bl = null;
		while ((bl = br.readLine()) != null) {
			promap.put("IPI00"+bl, proid++);
		}
		br.close();

		HashMap<String, HashMap<Integer, HashMap<String, Integer>>> map = 
				new HashMap<String, HashMap<Integer, HashMap<String, Integer>>>();
		
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();

		ExcelReader reader = new ExcelReader(in, 2);
		String[] line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			
			String name = line[2];
			String type = line[3];
			String key = line[0].substring(4, 15);;
			int site = Integer.parseInt(line[1]);
			
			if (!promap.containsKey(key))
				continue;

			if (type.equals("High mannose")) {
				set1.add(name);
			} else {
				set2.add(name);
			}
			
			int count = Integer.parseInt(line[4]);
			if (map.containsKey(key)) {
				HashMap<Integer, HashMap<String, Integer>> m = map.get(key);
				if (m.containsKey(site)) {
					
					HashMap<String, Integer> m2 = m.get(site);
					if(m2.containsKey(name)){
						m2.put(name, m2.get(name)+count);
					}else{
						m2.put(name, count);
					}
				} else {
					HashMap<String, Integer> m2 = new HashMap<String, Integer>();
					m2.put(name, count);
					m.put(site, m2);
				}
			} else {
				HashMap<String, Integer> m = new HashMap<String, Integer>();
				m.put(name, count);
				HashMap<Integer, HashMap<String, Integer>> m2 = new HashMap<Integer, HashMap<String, Integer>>();
				m2.put(site, m);
				map.put(key, m2);
			}
		}

		int max = 0;
		int total = 0;
		int bcount = 0;
		int totalSiteCount = 0;
		String[] refs = new String [map.size()];
		for(String ref : map.keySet()){
			refs[promap.get(ref)] = ref;
			totalSiteCount += map.get(ref).size();
		}
		String[] names1 = set1.toArray(new String[set1.size()]);
		String[] names2 = set2.toArray(new String[set2.size()]);
		String[] names = new String[names1.length + names2.length];

		Arrays.sort(names1);
		Arrays.sort(names2);

		System.arraycopy(names1, 0, names, 0, names1.length);
		System.arraycopy(names2, 0, names, names1.length, names2.length);

		String out = proteins.replace("txt", "info.txt");
		PrintWriter pw = new PrintWriter(out);
		
		int[][] countArrays = new int[totalSiteCount][names.length];
		
		int totalid = 0;
		for (int i = 0; i < refs.length; i++) {

			HashMap<Integer, HashMap<String, Integer>> sitemap = map.get(refs[i]);
			
			for(Integer site : sitemap.keySet()){
				
				HashMap<String, Integer> namemap = sitemap.get(site);
				
				for (int j = 0; j < names.length; j++) {

					if (namemap.containsKey(names[j])) {
						bcount++;
						countArrays[totalid][j] = namemap.get(names[j]);
						total += countArrays[totalid][j];
						if (countArrays[totalid][j] > max) {
							max = countArrays[totalid][j];
						}
					}
				}
				pw.write("Site\t"+refs[i]+"\t"+site+"\n");
				totalid++;
			}
		}

		for(int i=0;i<names.length;i++){
			pw.write("Glycan\t"+names[i]+"\n");
		}
		pw.close();
		
		double average = (double) total / (double) bcount;
		System.out.println(max + "\t" + average);
		System.out.println(map.size() +"\t"+totalSiteCount+ "\t" + (set1.size() + set2.size()));
		
		int glycocount = set1.size() + set2.size();
		
		int width = totalSiteCount*120+120;
//		int legendHeight = 
		int height = glycocount*80+width/8;
		
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		Graphics graphics = image.getGraphics();
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, width, height);

		for (int i = 0; i < countArrays.length; i++) {
			for (int j = 0; j < countArrays[i].length; j++) {
				
				int count = countArrays[i][j];
				Color color;
				int colorid = count*2+40;

				if (count == 0) {
					color = Color.WHITE;
				} else {
					if (j < names1.length) {
						if (colorid > 255) {
							int RGB = 510 - colorid;
							color = new Color(RGB, 0, 0);
						} else {
							int RGB = 255 - colorid;
							color = new Color(255, RGB, RGB);
						}
					} else {
						if (colorid > 255) {
							int RGB = 510 - colorid;
							color = new Color(0, 0, RGB);
						} else {
							int RGB = 255 - colorid;
							color = new Color(RGB, RGB, 255);
						}
					}
				}

				g2.setColor(color);

				g2.drawRect(121 + i * 120, 101 + j * 80, 120, 80);
				g2.fillRect(121 + i * 120, 101 + j * 80, 120, 80);
			}
		}
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.PLAIN, 14));
		for (int i = 0; i < countArrays.length; i++) {
//			g2.drawString(String.valueOf(i+1), 140 + i * 120, 40);
			g2.drawLine(181 + i * 120, 51, 181 + i * 120, 100);
		}
		for (int j = 0; j < countArrays[0].length; j++) {
//			g2.drawString(String.valueOf(j+1), 8, 148 + j * 80);
			g2.drawLine(71, 141 + j * 80, 120, 141 + j * 80);
		}

		int barlength = width/20;
		int barheight = barlength/6;
		
		for(int i=0;i<5;i++){
			
			int count = (i+1)*20;
			int colorid = count*2+40;

			Color color;
			
			if (colorid > 255) {
				int RGB = 510 - colorid;
				color = new Color(RGB, 0, 0);
			} else {
				int RGB = 255 - colorid;
				color = new Color(255, RGB, RGB);
			}
			
			g2.setColor(color);

			g2.drawRect(201 + i * barlength, 161 + countArrays[0].length * 80, barlength, barheight);
			g2.fillRect(201 + i * barlength, 161 + countArrays[0].length * 80, barlength, barheight);
			
			g2.setColor(Color.BLACK);
			g2.drawString(String.valueOf(count), 195 + i * 50, 185 + countArrays[0].length * 80);
			

			if (colorid > 255) {
				int RGB = 510 - colorid;
				color = new Color(0, 0, RGB);
			} else {
				int RGB = 255 - colorid;
				color = new Color(RGB, RGB, 255);
			}
		
			g2.setColor(color);

			g2.drawRect(201 + i * barlength, 161 + countArrays[0].length * 80 + barlength*4/5, barlength, barheight);
			g2.fillRect(201 + i * barlength, 161 + countArrays[0].length * 80 + barlength*4/5, barlength, barheight);
			
			g2.setColor(Color.BLACK);
			g2.drawString(String.valueOf(count), 195 + i * 50, 245 + countArrays[0].length * 80);
		}
		
		g2.setFont(new Font("Arial", Font.PLAIN, 22));
		g2.drawString("High mannose", 480, 171 + countArrays[0].length * 80);
		g2.drawString("Complex/Hybrid", 480, 231 + countArrays[0].length * 80);
		
		ImageIO.write(image, "PNG", new File(file));
	}
	
	private static void drawSiteAll(String in, String out, String file) throws IOException, JXLException {

		HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();

		ExcelReader reader = new ExcelReader(in, 2);
		String[] line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String name = line[2];
			String type = line[3];
			String key = line[0]+"\t"+line[1];

			if (type.equals("High mannose")) {
				set1.add(name);
			} else {
				set2.add(name);
			}
			int count = Integer.parseInt(line[4]);
			if (map.containsKey(key)) {
				HashMap<String, Integer> m = map.get(key);
				if (m.containsKey(name)) {
					m.put(name, m.get(name) + count);
				} else {
					m.put(name, count);
				}
			} else {
				HashMap<String, Integer> m = new HashMap<String, Integer>();
				m.put(name, count);
				map.put(key, m);
			}
		}

		int max = 0;
		int total = 0;
		int bcount = 0;
		String[] refs = map.keySet().toArray(new String[map.size()]);
		String[] names1 = set1.toArray(new String[set1.size()]);
		String[] names2 = set2.toArray(new String[set2.size()]);
		String[] names = new String[names1.length + names2.length];

		Arrays.sort(refs);
		Arrays.sort(names1);
		Arrays.sort(names2);

		System.arraycopy(names1, 0, names, 0, names1.length);
		System.arraycopy(names2, 0, names, names1.length, names2.length);

		Arrays.sort(refs);
		
		int glycocount = names.length;

		PrintWriter pw = new PrintWriter(out);
		
		for(int i=0;i<refs.length;i++){
			String [] cs = refs[i].split("\t");
			String ipi = cs[0].substring(4, 15);
			pw.write("Site\t"+refs[i]+"\t"+ipi+" "+cs[1]+"\n");
		}
		for(int i=0;i<names.length;i++){
			pw.write("Glycan\t"+names[i]+"\n");
		}
		pw.close();
		
		int[][] countArrays = new int[refs.length][names.length];

		for (int i = 0; i < refs.length; i++) {

			HashMap<String, Integer> namemap = map.get(refs[i]);

			for (int j = 0; j < names.length; j++) {

				if (namemap.containsKey(names[j])) {

					bcount++;
					countArrays[i][j] = namemap.get(names[j]);
					total += countArrays[i][j];
					if (countArrays[i][j] > max) {
						max = countArrays[i][j];
					}
				}
			}
		}

		double average = (double) total / (double) bcount;
		System.out.println(max + "\t" + average);
		System.out.println(refs.length + "\t" + names.length);

		int width = map.size()*12+120;
		int height = glycocount*8+100+240;
		
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		Graphics graphics = image.getGraphics();
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, width, height);

		for (int i = 0; i < countArrays.length; i++) {
			for (int j = 0; j < countArrays[i].length; j++) {
				
				int count = countArrays[i][j];
				Color color;
				
				if(count<=5){
					
					int colorid = (int) (count*51);
					if (count == 0) {
						color = Color.WHITE;
					} else {
						color = new Color(0, colorid, 0);
					}
					
				}else{
					
					int colorid = (int) (count*1.2);
					int RGB = 255 - colorid;
					color = new Color(255, RGB, 0);
				}

				g2.setColor(color);

				g2.drawRect(121 + i * 12, 101 + j * 8, 12, 8);
				g2.fillRect(121 + i * 12, 101 + j * 8, 12, 8);
			}
		}
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.PLAIN, 14));
		for (int i = 0; i < countArrays.length; i++) {
//			g2.drawString(String.valueOf(i+1), 140 + i * 120, 40);
//			g2.drawLine(181 + i * 12, 51, 181 + i * 12, 100);
		}
		for (int j = 0; j < countArrays[0].length; j++) {
//			g2.drawString(String.valueOf(j+1), 8, 148 + j * 80);
//			g2.drawLine(71, 141 + j * 8, 120, 141 + j * 80);
		}

		for(int i=0;i<5;i++){
			
			int count = (i+1)*20;
			int colorid = (int) (count*2.5);
			Color color;
			
			if (count == 0) {
				color = Color.BLACK;
			} else {
				if (colorid > 255) {
					int RGB = 510 - colorid;
					color = new Color(255, RGB, 0);
				} else {
					int RGB = 255 - colorid;
					color = new Color(0, RGB, 0);
				}
			}
			
			g2.setColor(color);

			g2.drawRect(201 + i * 50, 161 + countArrays[0].length * 80, 50, 8);
			g2.fillRect(201 + i * 50, 161 + countArrays[0].length * 80, 50, 8);
			
			g2.setColor(Color.BLACK);
			g2.drawString(String.valueOf(count), 195 + i * 50, 185 + countArrays[0].length * 80);
			

			if (colorid > 255) {
				int RGB = 510 - colorid;
				color = new Color(0, 0, RGB);
			} else {
				int RGB = 255 - colorid;
				color = new Color(RGB, RGB, 255);
			}
		
			g2.setColor(color);

			g2.drawRect(201 + i * 50, 221 + countArrays[0].length * 80, 50, 8);
			g2.fillRect(201 + i * 50, 221 + countArrays[0].length * 80, 50, 8);
			
			g2.setColor(Color.BLACK);
			g2.drawString(String.valueOf(count), 195 + i * 50, 245 + countArrays[0].length * 80);
		}
		
		g2.setFont(new Font("Arial", Font.PLAIN, 22));
		g2.drawString("High mannose", 480, 171 + countArrays[0].length * 80);
		g2.drawString("Complex/Hybrid", 480, 231 + countArrays[0].length * 80);
		
		ImageIO.write(image, "PNG", new File(file));
	}
	
	private static void drawGlycan(String in, String out) throws IOException{
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String brline = null;
		while((brline=br.readLine())!=null){
			String [] cs = brline.split("\t");
			/*if(cs[0].equals("Glycan")){
				if(!map.containsKey(cs[1]))
				map.put(cs[1], map.size()+1);
			}*/
			if(!map.containsKey(cs[2]))
				map.put(cs[2], map.size()+1);
		}
		br.close();
		System.out.println(map.size());
		GlycoStrucDrawer drawer = new GlycoStrucDrawer();
		GlycoDatabaseReader dbreader = new GlycoDatabaseReader();
		GlycoTree[] trees = dbreader.getUnits();
		
		File dir = new File(out);
		if(!dir.exists()){
			dir.mkdir();
		}
		for(int i=0;i<trees.length;i++){
			String name = trees[i].getIupacName();
			if(map.containsKey(name)){
				String output = dir.getAbsolutePath()+"\\"+map.get(name)+".png";
				BufferedImage image = drawer.draw(trees[i]);
				ImageIO.write(image, "PNG", new File(output));
			}
		}
	}
	
	private static void combineXMLWithStructure(String in, String out, String dir) throws DocumentException, IOException, WriteException{

		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;

		HashMap<String, Integer> countmap = new HashMap<String, Integer>();

		File[] files = (new File(in)).listFiles();
		for (int id = 0; id < files.length; id++) {

			if (!files[id].getName().endsWith("pxml"))
				continue;

			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(files[id]);
			NGlycoSSM[] ssms1 = reader.getMatchedGlycoSpectra();
			for (int i = 0; i < ssms1.length; i++) {

				NGlycoSSM ssm = ssms1[i];
				int [] glycoid = ssm.getGlycanid();
				String key = ssm.getName();
				countmap.put(key, 0);
			}
			
			NGlycoSSM[] ssms2 = reader.getUnmatchedGlycoSpectra();
			for(int i=0;i<ssms2.length;i++){
				NGlycoSSM ssm = ssms2[i];
				int [] glycoid = ssm.getGlycanid();
				String key = ssm.getName();
				countmap.put(key, 0);
			}
		}

		System.out.println(countmap.size());	
		int count = 0;
		GlycoStrucDrawer drawer = new GlycoStrucDrawer();
		GlycoDatabaseReader dbreader = new GlycoDatabaseReader();
		GlycoTree[] trees = dbreader.getUnits();

		for(int i=0;i<trees.length;i++){
			String name = trees[i].getIupacName();
			if(countmap.containsKey(name)){
				count++;
				String output = dir+"\\"+count+".png";
//				BufferedImage image = drawer.draw(trees[i]);
//				ImageIO.write(image, "PNG", new File(output));
				writer.addContent(name, 0, format);
				System.out.println(name);
			}
		}
		
		writer.close();
	}
	
	private static void drawFinal(String in, String hotout) throws IOException{

		HashMap<String, Integer> promap = new HashMap<String, Integer>();
		
		HashMap<String, HashMap<Integer, HashMap<String, Integer>>> map = 
				new HashMap<String, HashMap<Integer, HashMap<String, Integer>>>();
		
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(in));
		String bl = null;
		while ((bl = br.readLine()) != null) {

			String [] cs = bl.split("\t");
			String ref = cs[0];
			int site = Integer.parseInt(cs[1]);
			String name = cs[2];
			String type = cs[3];
			int count = Integer.parseInt(cs[4]);
			String key = ref.substring(4, 15);
			
			if (type.equals("High mannose")) {
				set1.add(name);
			} else {
				set2.add(name);
			}
			
			if(!promap.containsKey(key)){
				promap.put(key, promap.size());
			}
			
			if (map.containsKey(key)) {
				HashMap<Integer, HashMap<String, Integer>> m = map.get(key);
				if (m.containsKey(site)) {
					
					HashMap<String, Integer> m2 = m.get(site);
					if(m2.containsKey(name)){
						m2.put(name, m2.get(name)+count);
					}else{
						m2.put(name, count);
					}
				} else {
					HashMap<String, Integer> m2 = new HashMap<String, Integer>();
					m2.put(name, count);
					m.put(site, m2);
				}
			} else {
				HashMap<String, Integer> m = new HashMap<String, Integer>();
				m.put(name, count);
				HashMap<Integer, HashMap<String, Integer>> m2 = new HashMap<Integer, HashMap<String, Integer>>();
				m2.put(site, m);
				map.put(key, m2);
			}
		}
		br.close();

		int max = 0;
		int total = 0;
		int bcount = 0;
		int totalSiteCount = 0;
		String[] refs = new String [map.size()];
		for(String ref : map.keySet()){
			refs[promap.get(ref)] = ref;
			totalSiteCount += map.get(ref).size();
		}
		String[] names1 = set1.toArray(new String[set1.size()]);
		String[] names2 = set2.toArray(new String[set2.size()]);
		String[] names = new String[names1.length + names2.length];

		Arrays.sort(names1);
		Arrays.sort(names2);

		System.arraycopy(names1, 0, names, 0, names1.length);
		System.arraycopy(names2, 0, names, names1.length, names2.length);

		String out = in.replace("txt", "info.txt");
		PrintWriter pw = new PrintWriter(out);
		
		int[][] countArrays = new int[totalSiteCount][names.length];
		
		int totalid = 0;
		for (int i = 0; i < refs.length; i++) {

			HashMap<Integer, HashMap<String, Integer>> sitemap = map.get(refs[i]);
			
			for(Integer site : sitemap.keySet()){
				
				HashMap<String, Integer> namemap = sitemap.get(site);
				
				for (int j = 0; j < names.length; j++) {

					if (namemap.containsKey(names[j])) {
						bcount++;
						countArrays[totalid][j] = namemap.get(names[j]);
						total += countArrays[totalid][j];
						if (countArrays[totalid][j] > max) {
							max = countArrays[totalid][j];
						}
					}
				}
				pw.write("Site\t"+refs[i]+"\t"+site+"\n");
				totalid++;
			}
		}

		for(int i=0;i<names.length;i++){
			pw.write("Glycan\t"+names[i]+"\n");
		}
		pw.close();
		
		double average = (double) total / (double) bcount;
		System.out.println(max + "\t" + average);
		System.out.println(map.size() +"\t"+totalSiteCount+ "\t" + (set1.size() + set2.size()));
		
		int glycocount = set1.size() + set2.size();
		
		int width = totalSiteCount*120+120;
//		int legendHeight = 
		int height = glycocount*80+width/8;
		
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		Graphics graphics = image.getGraphics();
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, width, height);

		for (int i = 0; i < countArrays.length; i++) {
			for (int j = 0; j < countArrays[i].length; j++) {
				
				int count = countArrays[i][j];
				Color color;
				int colorid = count*2+40;

				if (count == 0) {
					color = Color.WHITE;
				} else {
					if (j < names1.length) {
						if (colorid > 255) {
							int RGB = 510 - colorid;
							color = new Color(RGB, 0, 0);
						} else {
							int RGB = 255 - colorid;
							color = new Color(255, RGB, RGB);
						}
					} else {
						if (colorid > 255) {
							int RGB = 510 - colorid;
							color = new Color(0, 0, RGB);
						} else {
							int RGB = 255 - colorid;
							color = new Color(RGB, RGB, 255);
						}
					}
				}

				g2.setColor(color);

				g2.drawRect(121 + i * 120, 101 + j * 80, 120, 80);
				g2.fillRect(121 + i * 120, 101 + j * 80, 120, 80);
			}
		}
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.PLAIN, 14));
		for (int i = 0; i < countArrays.length; i++) {
//			g2.drawString(String.valueOf(i+1), 140 + i * 120, 40);
			g2.drawLine(181 + i * 120, 51, 181 + i * 120, 100);
		}
		for (int j = 0; j < countArrays[0].length; j++) {
//			g2.drawString(String.valueOf(j+1), 8, 148 + j * 80);
			g2.drawLine(71, 141 + j * 80, 120, 141 + j * 80);
		}

		int barlength = width/20;
		int barheight = barlength/6;
		
		for(int i=0;i<5;i++){
			
			int count = (i+1)*20;
			int colorid = count*2+40;

			Color color;
			
			if (colorid > 255) {
				int RGB = 510 - colorid;
				color = new Color(RGB, 0, 0);
			} else {
				int RGB = 255 - colorid;
				color = new Color(255, RGB, RGB);
			}
			
			g2.setColor(color);

			g2.drawRect(201 + i * barlength, 161 + countArrays[0].length * 80, barlength, barheight);
			g2.fillRect(201 + i * barlength, 161 + countArrays[0].length * 80, barlength, barheight);
			
			g2.setColor(Color.BLACK);
			g2.drawString(String.valueOf(count), 195 + i * 50, 185 + countArrays[0].length * 80);
			

			if (colorid > 255) {
				int RGB = 510 - colorid;
				color = new Color(0, 0, RGB);
			} else {
				int RGB = 255 - colorid;
				color = new Color(RGB, RGB, 255);
			}
		
			g2.setColor(color);

			g2.drawRect(201 + i * barlength, 161 + countArrays[0].length * 80 + barlength*4/5, barlength, barheight);
			g2.fillRect(201 + i * barlength, 161 + countArrays[0].length * 80 + barlength*4/5, barlength, barheight);
			
			g2.setColor(Color.BLACK);
			g2.drawString(String.valueOf(count), 195 + i * 50, 245 + countArrays[0].length * 80);
		}
		
		g2.setFont(new Font("Arial", Font.PLAIN, 22));
		g2.drawString("High mannose", 480, 171 + countArrays[0].length * 80);
		g2.drawString("Complex/Hybrid", 480, 231 + countArrays[0].length * 80);
		
		ImageIO.write(image, "PNG", new File(hotout));
	}
	
	private static void draw20130909(String in, String out) throws IOException, JXLException{
		
		ArrayList <String> list = new ArrayList<String>();
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			list.add(line[9]);
		}
		reader.close();
		
		GlycoStrucDrawer drawer = new GlycoStrucDrawer();
		GlycoDatabaseReader dbreader = new GlycoDatabaseReader();
		GlycoTree[] trees = dbreader.getUnits();

		for(int i=0;i<trees.length;i++){
			String name = trees[i].getIupacName();
/*			
			if(name.equals("Gal-GlcNAc-Man-(Gal-GlcNAc-)(Gal-GlcNAc-Man-)Man-GlcNAc-(Fuc-)GlcNAc-Asn")){
				String output = out+"\\"+"19.png";
				BufferedImage image = drawer.draw(trees[i]);
				ImageIO.write(image, "PNG", new File(output));
			}
*/			
			for(int j=0;j<list.size();j++){
				if(name.equals(list.get(j))){
					String output = out+"\\"+(j+1)+".png";
					BufferedImage image = drawer.draw(trees[i]);
					ImageIO.write(image, "PNG", new File(output));
				}
			}
			
		}
	}
	
	private static void drawXls(String in, String out) throws IOException, JXLException{
		
		HashSet<String> set = new HashSet<String>();
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			set.add(line[8]);
		}
		reader.close();
		System.out.println("1178\t"+set.size());
		GlycoStrucDrawer drawer = new GlycoStrucDrawer();
		GlycoDatabaseReader dbreader = new GlycoDatabaseReader();
		GlycoTree[] trees = dbreader.getUnits();

		for(int i=0;i<trees.length;i++){
			String name = trees[i].getIupacName();
/*			
			if(name.equals("Gal-GlcNAc-Man-(Gal-GlcNAc-)(Gal-GlcNAc-Man-)Man-GlcNAc-(Fuc-)GlcNAc-Asn")){
				String output = out+"\\"+"19.png";
				BufferedImage image = drawer.draw(trees[i]);
				ImageIO.write(image, "PNG", new File(output));
			}
*/			
			for(String glyco:set){
				if(name.equals(glyco)){
					String output = out+"\\"+(i)+".png";
					BufferedImage image = drawer.draw(trees[i]);
					ImageIO.write(image, "PNG", new File(output));
				}
			}
		}
	}
	
	/**
	 * @param args
	 * @throws JXLException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws IOException, JXLException, DocumentException {
		// TODO Auto-generated method stub

//		StatisticDrawer.count("H:\\NGlyco_final_20130725\\All16_filtered_20.combine.xls");
		
		/* StatisticDrawer.draw(
		 "H:\\NGlyco_final_20130725\\All16_filtered_20.combine.xls",
		  "H:\\NGlyco_final_20130725\\All.png", 9060,
		 8940);*/

		/*StatisticDrawer.drawSite(
				"H:\\NGlyco_final_20130730\\RT10.2D.xls",er
				"H:\\NGlyco_final_20130730\\Fuction\\nuclear ipi.txt",
				"H:\\NGlyco_final_20130730\\Fuction\\nuclear 2.png");*/
		
//		StatisticDrawer.drawGlycan("H:\\NGlyco_final_20130730\\Fuction\\IPI00289819.txt", 
//				"H:\\NGlyco_final_20130730\\Fuction\\IPI00289819");
		
//		StatisticDrawer.combineXMLWithStructure("H:\\NGlyco_final_20130730\\2D_4", 
//				"H:\\NGlyco_final_20130730\\structure.xls", "H:\\NGlyco_final_20130730\\structure");
//		StatisticDrawer.drawSiteAll("H:\\NGlyco_final_20130730\\RT10.2D.xls", 
//				"H:\\NGlyco_final_20130730\\RT10.2D.info.txt", "H:\\NGlyco_final_20130730\\white.all.png");

		
		StatisticDrawer.draw20130909("D:\\P\\n-glyco\\3014.01.03.unknown\\combine.xls", 
				"D:\\P\\n-glyco\\3014.01.03.unknown\\glyco_structure");
//		StatisticDrawer.drawXls("H:\\NGlyco_final_20131217\\Rui_20130604_HEK_HILIC_F3_130606064201.xls", 
//				"H:\\NGlyco_final_20131217\\png_2");
	}

}
