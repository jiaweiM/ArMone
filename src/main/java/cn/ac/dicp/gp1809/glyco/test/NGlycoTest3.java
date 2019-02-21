/* 
 ******************************************************************************
 * File: NGlycoTest3.java * * * Created on 2014-2-27
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jxl.JXLException;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * @author ck
 *
 * @version 2014-2-27, ����10:20:21
 */
public class NGlycoTest3 {

	public static void compare(String s1, String s2) throws IOException, JXLException {

		ExcelReader reader1 = new ExcelReader(s1, new int[] { 0, 1, 2 });
		HashMap<String, String> m1 = new HashMap<String, String>();
		HashMap<String, Double> sm1 = new HashMap<String, Double>();
		String[] line1 = reader1.readLine(1);
		while ((line1 = reader1.readLine(1)) != null) {
			m1.put(line1[0], line1[8]);
			sm1.put(line1[0], Double.parseDouble(line1[7]));
		}
		line1 = reader1.readLine(2);
		while ((line1 = reader1.readLine(2)) != null) {
			m1.put(line1[0], line1[8]);
			sm1.put(line1[0], Double.parseDouble(line1[7]));
		}
		reader1.close();
		ExcelReader reader2 = new ExcelReader(s2, new int[] { 0, 1, 2 });
		HashMap<String, String> m2 = new HashMap<String, String>();
		HashMap<String, Double> sm2 = new HashMap<String, Double>();
		String[] line2 = reader2.readLine(1);
		while ((line2 = reader2.readLine(1)) != null) {
			m2.put(line2[0], line2[8]);
			sm2.put(line2[0], Double.parseDouble(line2[7]));
		}
		line2 = reader2.readLine(2);
		while ((line2 = reader2.readLine(2)) != null) {
			m2.put(line2[0], line2[8]);
			sm2.put(line2[0], Double.parseDouble(line2[7]));
		}
		reader2.close();

		HashSet<String> set = new HashSet<String>();
		set.addAll(m1.keySet());
		set.addAll(m2.keySet());

		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (m1.containsKey(key)) {
				if (m2.containsKey(key)) {
					if (!m1.get(key).equals(m2.get(key))) {
						System.out.println(key + " \t " + m1.get(key) + " \t " + m2.get(key) + " \t \t "
								+ (sm1.get(key) - sm2.get(key)));
					}
				} else {
					System.out.println(key + " \t " + m1.get(key) + " \t \t " + sm1.get(key));
				}
			} else {
				System.out.println(key + " \t \t " + m2.get(key) + " \t " + sm2.get(key));
			}
		}
	}

	public static void compare2(String s1, String s2) throws IOException, JXLException {
		ExcelReader reader1 = new ExcelReader(s1, 1);
		HashMap<String, String> m1 = new HashMap<String, String>();

		String[] line1 = reader1.readLine();
		while ((line1 = reader1.readLine()) != null) {
			m1.put(line1[0] + "\t" + line1[1], line1[7]);
		}
		reader1.close();
		ExcelReader reader2 = new ExcelReader(s2, 1);
		HashMap<String, String> m2 = new HashMap<String, String>();
		String[] line2 = reader2.readLine();
		while ((line2 = reader2.readLine()) != null) {
			m2.put(line2[0] + "\t" + line2[1], line2[7]);
		}
		reader2.close();

		HashSet<String> set = new HashSet<String>();
		set.addAll(m1.keySet());
		set.addAll(m2.keySet());
		System.out.println(m1.size() + "\t" + m2.size() + "\t" + set.size());
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (m1.containsKey(key)) {
				if (m2.containsKey(key)) {
					if (!m1.get(key).equals(m2.get(key))) {
						System.out.println(key + "\t" + m1.get(key) + "\t" + m2.get(key) + "\t\t");
					}
				} else {
					System.out.println(key + "\t" + m1.get(key) + "\t\t");
				}
			} else {
				System.out.println(key + "\t\t" + m2.get(key) + "\t");
			}
		}
	}

	/**
	 * @param args
	 * @throws JXLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, JXLException {
		NGlycoTest3.compare("H:\\NGLYCO\\NGlyco_20140318\\Rui_20130604_HEK_HILIC_F2.xls",
				"D:\\P\\n-glyco\\2014.02.26\\Rui_20130604_HEK_HILIC_F2.xls");
		// NGlycoTest3.compare2("H:\\NGLYCO\\NGlyco_20140318\\2014.03.18.dataset.xls",
		// "D:\\P\\n-glyco\\2014.02.26\\2014.02.26.dataset.xls");
	}

}
