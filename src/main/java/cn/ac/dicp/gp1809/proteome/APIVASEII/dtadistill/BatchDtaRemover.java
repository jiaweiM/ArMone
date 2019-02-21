/*
 ******************************************************************************
 * File: BatchDtaRemover.java * * * Created on 02-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.dtadistill;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;

/**
 * Batch dta remover for APIVSE to improve the search speed.
 * 
 * @author Xinning
 * @version 0.1, 02-24-2009, 21:48:50
 */
public class BatchDtaRemover {
	private File folder = null;
	private File[] subfolders = null;
	private String MSnCount = "3";
	private int removeType;

	public BatchDtaRemover(String foldername, String MSnCount, int removeType) {
		this.folder = new File(foldername);

		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println("Invalid folder ");
			System.exit(0);
		}

		this.subfolders = this.folder.listFiles(new FilenameFilter() {
			public boolean accept(File file, String name) {
				if (new File(file, name).isDirectory())
					return true;

				return false;
			}
		});

		this.MSnCount = MSnCount;
		this.removeType = removeType;

	}

	public void process() throws FileNotFoundException, NumberFormatException,
	        DtaFileParsingException, XMLStreamException {

		for (int i = 0; i < subfolders.length; i++) {
			processfolder(subfolders[i], MSnCount, removeType);
		}

	}

	/**
	 * Process the current work directory
	 * 
	 * @param folder
	 * @param MSnCount
	 * @param removeType
	 * @return
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException
	 * @throws NumberFormatException
	 * @throws XMLStreamException 
	 */
	public static boolean processfolder(File folder, String MSnCount,
	        int removeType) throws FileNotFoundException,
	        NumberFormatException, DtaFileParsingException, XMLStreamException {
		String foldername = folder.getName();

		System.out.println("Processing " + foldername + " ...");

		File xml[] = folder.listFiles(new FilenameFilter() {
			public boolean accept(File file, String name) {
				if (name.endsWith(".xml"))
					return true;

				return false;
			}
		});

		File subfolder[] = folder.listFiles(new FilenameFilter() {
			public boolean accept(File file, String name) {
				if (new File(file, name).isDirectory()) {
					if (name.equals("ms2") || name.equals("ms3"))
						return true;
				}
				return false;
			}
		});

		if (xml.length != 1) {
			return false;
		}

		if (subfolder.length != 2)
			return false;

		String mzdataname = xml[0].getPath();
		boolean istrue = subfolder[0].getName().contains("ms2");
		String ms2name = (istrue ? subfolder[0] : subfolder[1]).getPath();
		String ms3name = (istrue ? subfolder[1] : subfolder[0]).getPath();

		DuplicatedDtaFileRemover.main(new String[] { ms2name, ms3name,
		        mzdataname, MSnCount, String.valueOf(removeType) });

		return true;
	}

	/**
	 * �ж��Ƿ�ǰ�ļ���Ϊ�����ļ��У�������Ϣ�ģ���
	 * 
	 * @param folder
	 * @return
	 */
	public static boolean iscurrentfolder(File folder) {
		File xml[] = folder.listFiles(new FilenameFilter() {
			public boolean accept(File file, String name) {
				if (name.endsWith(".xml"))
					return true;

				return false;
			}
		});

		File subfolder[] = folder.listFiles(new FilenameFilter() {
			public boolean accept(File file, String name) {
				if (new File(file, name).isDirectory()) {
					if (name.contains("ms2") || name.contains("ms3"))
						return true;
				}

				return false;
			}
		});

		if (xml.length != 1) {
			return false;
		}

		if (subfolder.length != 2)
			return false;

		return true;
	}

	private static String usage() {
		return "BatchDtaRemover directory_name remove_type MSnCount\r\n"
		        + "\tOptions: directory_name name of the directory contains all necessary\r\n"
		        + "\t                        files (ms2 and ms3 dir and mzdata file).\r\n"
		        + "\t         remove_type remove which dta files?\r\n"
		        + "\t         MSnCount count of the MS2-MS3 in spectrum collection\r\n\r\n"
		        + "\t       remove_type: 0, only retain the paired MS2 and MS3\r\n"
		        + "\t                    1, only remove the dta with wrong charge states.";
	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException
	 * @throws NumberFormatException
	 * @throws XMLStreamException 
	 */
	public static void main(String[] args) throws FileNotFoundException,
	        NumberFormatException, DtaFileParsingException, XMLStreamException {
		if (args == null || args.length != 3) {
			System.out.println(usage());
		} else {
			String MSnCount = args[2];
			int removeType = Integer.parseInt(args[1]);

			File file = new File(args[0]);
			if (iscurrentfolder(file)) {
				processfolder(file, MSnCount, removeType);
			} else {
				new BatchDtaRemover(args[0], MSnCount, removeType).process();
			}
		}
	}

}
