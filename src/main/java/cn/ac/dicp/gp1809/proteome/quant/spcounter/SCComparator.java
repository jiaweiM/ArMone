/*
 * *****************************************************************************
 * File: Comparator.java * * * Created on 08-14-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProteinIOException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;

/**
 * Comparator of SpectraCounter
 * 
 * @author Xinning
 * @version 0.1, 08-14-2008, 15:18:41
 */
public class SCComparator {

	private HashMap<String, ProCompare> map;

	private File outdir;
	private File[] dirForComp;
	private boolean isCountPep;
	private boolean isAvg;

	/**
	 * @param files
	 *            the file(s) which conatins protein information. Currently,
	 *            this file should be "unduplicated" file
	 * @param isCountPep
	 *            if count peptide information(the spectra number for a unique
	 *            peptide identification)
	 * @throws IOException 
	 * @throws ProteinIOException 
	 * @throws IllegalFormaterException 
	 * @throws MoreThanOneRefFoundInFastaException 
	 * @throws ProteinNotFoundInFastaException 
	 */
	// public Counter(String[] files, boolean isCountPep){
	// try {
	// this.count(files,isCountPep);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	
	public SCComparator(String dirForComp, boolean isCountPep, boolean isAvg) 
		throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, 
		IllegalFormaterException, ProteinIOException, IOException{
		
		File folder = new File(dirForComp);
		this.dirForComp = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (!name.toLowerCase().equals("counter")
				        && new File(dir, name).isDirectory())
					return true;
				return false;
			}
		});
		this.outdir = new File(folder, "counter");
		map = new HashMap<String, ProCompare>();
		this.isCountPep = isCountPep;
		this.isAvg = isAvg;
		this.compare();
	}
	
	/**
	 * @param files
	 *            the file(s) which conatins protein information. Currently,
	 *            this file should be "unduplicated" file
	 * @param isCountPep
	 *            if count peptide information(the spectra number for a unique
	 *            peptide identification)
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 * @throws IOException
	 * @throws ProteinIOException 
	 * @throws IllegalFormaterException 
	 */
	public SCComparator(File[] dirForComp, File outdir, boolean isCountPep,
	        boolean isAvg) throws IOException, ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, IllegalFormaterException, ProteinIOException {
		map = new HashMap<String, ProCompare>();
		this.outdir = outdir;
		this.dirForComp = dirForComp;
		this.isCountPep = isCountPep;
		this.isAvg = isAvg;
		this.compare();
//		this.compare(dirForComp, isCountPep, isAvg);
	}

	public void compare()throws IOException, ProteinNotFoundInFastaException,
		MoreThanOneRefFoundInFastaException, IllegalFormaterException, ProteinIOException {
		
		int len = dirForComp.length;
		for (int i = 0; i < len; i++) {
			File file = dirForComp[i];
			Counter counter = new Counter(this.getFiles(file), isCountPep,
	        isAvg);
			ProStatistic[] pros = counter.getProStats();
			for (int j = 0; j < pros.length; j++) {
				ProStatistic pro = pros[j];
				String ref = pro.getRefname();
				ProCompare prostat = map.get(ref);
				if (prostat == null) {
					prostat = new ProCompare(len, isCountPep);
					prostat.set(pro, i);
					map.put(ref, prostat);
				} else {
					prostat.set(pro, i);
				}
			}

			counter.printTo(new File(this.outdir, file.getName())
				.getAbsolutePath());
		}
	}
	
	public void compare(File[] files, boolean isCountPep, boolean isAvg)
	        throws IOException, ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, IllegalFormaterException, ProteinIOException {
		int len = files.length;
		for (int i = 0; i < len; i++) {
			File file = files[i];
			Counter counter = new Counter(this.getFiles(file), isCountPep,
			        isAvg);
			ProStatistic[] pros = counter.getProStats();
			for (int j = 0; j < pros.length; j++) {
				ProStatistic pro = pros[j];
				String ref = pro.getRefname();
				ProCompare prostat = map.get(ref);
				if (prostat == null) {
					prostat = new ProCompare(len, isCountPep);
					prostat.set(pro, i);
					map.put(ref, prostat);
				} else {
					prostat.set(pro, i);
				}
			}

			counter.printTo(new File(this.outdir, file.getName())
			        .getAbsolutePath());
		}
	}

	private File[] getFiles(File dir) {
		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowname = name.toLowerCase();
				if (lowname.endsWith(".nord"))
					return true;
				else
					return false;
			}
		});
	}

	/**
	 * @return the proStats after counter.
	 */
	public ProCompare[] getProCompare() {
		return map.values().toArray(new ProCompare[0]);
	}

	/**
	 * @return the map of prostatistic after counter. the key is the name of
	 *         prostats, and the value is the instence of pro.
	 */
	public Map<String, ProCompare> getProCompareMap() {
		return this.map;
	}
	
	public void writeResult() throws IOException{
/*		
		HashSet <String> refset = new HashSet<String>();
		refset.add("P62739");
		refset.add("P00634");
		refset.add("P06278");
		refset.add("P00711");
		refset.add("P02666");
		refset.add("P00722");
		refset.add("P02754");
		refset.add("P00921");
		refset.add("P00432");
		refset.add("P62984");
		refset.add("P46406");
		refset.add("P00489");
		refset.add("P00946");
		refset.add("P68082");
		refset.add("P02602");
		refset.add("P01012");
		refset.add("Q29443");
		refset.add("P02769");
*/		
		PrintWriter pw = new PrintWriter(new BufferedWriter(
		        new FileWriter(new File(outdir, "Compare.cmp"))));

		StringBuilder sb = new StringBuilder();
		sb.append("Idx\t");
		for (int i = 0; i < dirForComp.length; i++) {
			String name = dirForComp[i].getName();
			sb.append("SpC_").append(name).append("\t");
			sb.append("RSD_").append(name).append("\t");
			sb.append("UniqueSpC_").append(name).append("\t");
		}

		sb.append("UniqueSeqC").append("\t");
		sb.append("Ref").append("\t");
		sb.append("MW").append("\t");
		sb.append("pI");

		pw.println(sb.toString());
		ProCompare[] pros = this.getProCompare();
		Arrays.sort(pros);

		for (int i = 0; i < pros.length; i++) {
			pros[i].setIndex(i + 1);
//			if(refset.contains(pros[i].getRef().substring(3, 9)))
//				System.out.print(pros[i]);
			pw.print(pros[i]);
		}

		pw.close();

		System.out.println("Finished!");
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws IllegalFormaterException
	 * @throws ProteinIOException
	 */
	public static void main(String[] args) throws IOException,
	        ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, IllegalFormaterException, ProteinIOException {
		if (args.length != 3) {
			System.out
			        .println("Counter folder_with_Unduplicated_files true_or_false_for_count_peptide"
			                + " average_or_median_spectrum_count");
		} else {
			File folder = new File(args[0]);
			File[] files = folder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (!name.toLowerCase().equals("counter")
					        && new File(dir, name).isDirectory())
						return true;
					return false;
				}
			});

			if (files.length == 0) {
				System.out
				        .println("Please select a folder containing unduplicated files");
			} else {
				File cdir = new File(folder, "counter");
				SCComparator counter = new SCComparator(files, cdir, Boolean
				        .parseBoolean(args[1]), Boolean.parseBoolean(args[2]));
				PrintWriter pw = new PrintWriter(new BufferedWriter(
				        new FileWriter(new File(cdir, "Compare.cmp"))));

				StringBuilder sb = new StringBuilder();
				sb.append("Idx\t");
				for (int i = 0; i < files.length; i++) {
					String name = files[i].getName();
					sb.append("SpC_").append(name).append("\t");
					sb.append("RSD_").append(name).append("\t");
					sb.append("UniqueSpC_").append(name).append("\t");
				}

				sb.append("UniqueSeqC").append("\t");
				sb.append("Ref").append("\t");
				sb.append("MW").append("\t");
				sb.append("pI");

				pw.println(sb.toString());
				ProCompare[] pros = counter.getProCompare();
				Arrays.sort(pros);

				for (int i = 0; i < pros.length; i++) {
					pros[i].setIndex(i + 1);
					pw.print(pros[i]);
				}

				pw.close();

				System.out.println("Finished!");
			}
		}
	}

}
