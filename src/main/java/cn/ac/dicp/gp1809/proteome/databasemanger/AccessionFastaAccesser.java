/* 
 ******************************************************************************
 * File: AccessionFastaAccesser.java * * * Created on 11-18-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotDBPattern;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * For some cases, accessions may be generated to indicate the protein. This may
 * be more convenient than the use of start string of a protein reference.
 * However, for fasta generating of the full protein name, we need to know how
 * the accession is generated.
 * 
 * <p>
 * Changes:
 * <li>0.2, 03-08-2009: remove the mascot specific usage.
 * 
 * @author Xinning
 * @version 0.2.1, 05-20-2010, 15:15:24
 */
public class AccessionFastaAccesser extends AbstractFastaAccesser {

	private Pattern pattern;

	public AccessionFastaAccesser(String dbpath, Pattern pattern, IDecoyReferenceJudger judger)
			throws FastaDataBaseException, IOException {
		super(new Indexer(dbpath, pattern, judger), judger);
		this.pattern = pattern;
	}

	public AccessionFastaAccesser(File dbfile, Pattern pattern, IDecoyReferenceJudger judger)
			throws FastaDataBaseException, IOException {
		super(new Indexer(dbfile.getAbsolutePath(), pattern, judger), judger);
		this.pattern = pattern;
	}

	/**
	 * The accession will be directly returned.
	 */
	@Override
	protected String getKey(String partialName_accession) {
		return partialName_accession;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public String getAccession(String fullname) {

		String accession = "";
		Matcher matcher = this.pattern.matcher(fullname);
		if (matcher.find()) {

			if (matcher.groupCount() == 1) {
				accession = matcher.group(1);

			}
		} else
			throw new IllegalArgumentException("Unable to parse the full name \"" + fullname
					+ "\" to generate the accession using regular expression: \"" + pattern.pattern() + "\"");

		return accession;
	}

	/**
	 * Indexer for fasta database for fast reading and finding.
	 * 
	 * @author Xinning
	 * @version 0.4.1, 05-20-2010, 15:15:14
	 */
	private static class Indexer extends AbstractFastaAccesser.Indexer {

		private Pattern pattern;

		private Indexer(String dbname, Pattern pattern, IDecoyReferenceJudger judger)
				throws FastaDataBaseException, IOException {
			super(dbname, judger);
			this.pattern = pattern;
		}

		/**
		 * For fast finding, we always put the object for finding into a map
		 * with proper keys. However, as sequest outputted protein reference is
		 * often with different reference length. Therefore, a proper key should
		 * be with the smallest length of reference name.
		 * <p>
		 * <b>Override this method to generate map with different type of
		 * keys</b>
		 * 
		 */
		@Override
		protected HashMap<String, FastaIndex> parseEntriesToMap(FastaIndex[] idxArray) {
			int len = idxArray.length;

			HashMap<String, FastaIndex> map = new HashMap<String, FastaIndex>(len);
			for (int i = 0; i < len; i++) {
				FastaIndex tidx = idxArray[i];
				String name = this.getAccession(tidx.getName());

				map.put(name, tidx);
			}

			return map;
		}

		/**
		 * Get the accession from the protein full name and return the parsed
		 * accession.
		 * 
		 * @param fullname
		 * @return
		 */
		private String getAccession(String fullname) {

			String accession = "";
			Matcher matcher = this.pattern.matcher(fullname);
			if (matcher.find()) {

				if (matcher.groupCount() == 0) {
					accession = matcher.group(0);

				}
				if (matcher.groupCount() == 1) {
					accession = matcher.group(1);

				}
			} else
				throw new IllegalArgumentException("Unable to parse the full name \"" + fullname
						+ "\" to generate the accession using regular expression: \"" + pattern.pattern() + "\"");

			return accession;
		}
	}

	public static void main(String[] args) throws FastaDataBaseException, IOException {
		MascotDBPattern mp = new MascotDBPattern("..\\|\\([^|]*\\)");
		Pattern p = mp.getPattern();
		System.out.println(p);
		Matcher m = p.matcher("sp|P31946|1433B_HUMAN 14-3-3 protein beta/alpha OS=Homo sapiens GN=YWHAB PE=1 SV=3");
		if (m.find())
			System.out.println(m.group(1));
	}
}
