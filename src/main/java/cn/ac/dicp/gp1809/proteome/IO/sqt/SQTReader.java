/* 
 ******************************************************************************
 * File: SQTReader.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

/**
 * The SQT peptide identification result reader
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 19:30:32
 */
public class SQTReader implements ISQTReader {

	/**
	 * The pattern to split the header. In some cases such as the output by
	 * crux, the header is not well formated using the delimiter of tab, but
	 * also with space. The splitter is used for this.
	 */
	private Pattern headerPattern = Pattern.compile("([^\t ]+)");

	private SQTHeader header;

	private BufferUtil reader;

	public SQTReader(String file) throws IOException {
		this(new File(file));
	}

	public SQTReader(File file) throws IOException {
		this.reader = new BufferUtil(file);
		this.parseHeader();
	}

	/**
	 * Parse the header
	 * 
	 */
	private void parseHeader() {

		this.header = new SQTHeader();

		String line;
		while ((line = reader.readLine()) != null && line.startsWith("H\t")) {
			String cap = line.substring(2);
			Matcher matcher = this.headerPattern.matcher(cap);
			matcher.find();
			String field = matcher.group();
			int end = matcher.end() + 1;
			if (end > cap.length())
				end = cap.length();

			String value = cap.substring(end).trim();

			if (field.equalsIgnoreCase("SQTGenerator")) {
				this.header.setSQTGenerator(value);
				continue;
			}

			if (field.equalsIgnoreCase("SQTGeneratorVersion")) {
				this.header.setSQTGeneratorVersion(value);
				continue;
			}

			if (field.equalsIgnoreCase("Database")) {
				this.header.setDatabase(value);
				continue;
			}

			if (field.equalsIgnoreCase("FragmentMasses")) {
				this.header.setFragmentMasses(value);
				continue;
			}

			if (field.equalsIgnoreCase("PrecursorMasses")) {
				this.header.setPrecursorMasses(value);
				continue;
			}

			if (field.equalsIgnoreCase("StartTime")) {
				this.header.setStartTime(value);
				continue;
			}

			if (field.equalsIgnoreCase("StaticMod")) {
				this.header.setStaticMod(value);
				continue;
			}

			if (field.equalsIgnoreCase("DynamicMod")) {
				this.header.setDynamicMod(value);
				continue;
			}

			if (field.equalsIgnoreCase("DBSeqLength")) {
				this.header.setDBSeqLength(value);
				continue;
			}

			if (field.equalsIgnoreCase("DBLocusCount")) {
				this.header.setDBLocusCount(value);
				continue;
			}

			if (field.equalsIgnoreCase("DBMD5Sum")) {
				this.header.setDBMD5Sum(value);
				continue;
			}

			if (field.equalsIgnoreCase("SortedBy")) {
				this.header.setSortedBy(value);
				continue;
			}

			if (field.equalsIgnoreCase("Comment")) {
				this.header.addComment(value);
				continue;
			}

			if (field.length() > 4
			        && field.substring(0, 4).equalsIgnoreCase("Alg-")) {
				this.header.addAlgField(field, value);
				continue;
			}

			this.header.addOtherFields(cap);
		}

		if (line != null) {
			this.reader.rollBackTheLine();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISQTReader#getHeader()
	 */
	@Override
	public ISQTHeader getHeader() {
		return header;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISQTReader#getNextMatch()
	 */
	@Override
	public IPepMatches getNextMatch() throws SQTReadingException {
		ISpectrumInfo info = null;
		LinkedHashMap<String, PepMatch> map = null;
		String line;
		try {
			while ((line = reader.readLine()) != null
			        && !line.startsWith("S\t")) {
				//go to the spectrum info line
			}

			if (line == null)
				return null;

			info = this.parseSpectrumInfo(line);

			map = new LinkedHashMap<String, PepMatch>();
			PepMatch match;
			while ((match = this.getNextHit()) != null) {
				String seq = match.getSequence();
				PepMatch put = map.get(seq);
				if (put != null) {
					put.addReferences(match.getReferences());
				} else
					map.put(seq, match);
			}

			return new PepMatches(map.values()
			        .toArray(new PepMatch[map.size()]), info);
		} catch (Exception e) {

			/*
			 * Here we try to handle the exception in an error tolerance manner.
			 */

			System.err.println("Warning: find illegal description for: " + info
			        + ". Try to skip.");

			if (info != null && map != null && map.size() > 0) {
				return new PepMatches(map.values().toArray(
				        new PepMatch[map.size()]), info);
			} else {
				return this.getNextMatch();
			}

			//		throw new SQTReadingException(e);
		}
	}

	/**
	 * The spectrum information
	 * 
	 * @param line
	 * @return
	 */
	private SpectrumInfo parseSpectrumInfo(String line)
	        throws NumberFormatException, NullPointerException,
	        IndexOutOfBoundsException {
		String[] infos = StringUtil.split(line, '\t');

		int scanBeg = Integer.parseInt(infos[1]), scanEnd = Integer
		        .parseInt(infos[2]);
		short charge = Short.parseShort(infos[3]);
		float time = Float.parseFloat(infos[4]);
		String serverName = infos[5];
		double expMH = Double.parseDouble(infos[6]);
		float tic = Float.parseFloat(infos[7]);
		float lowestSp = Float.parseFloat(infos[8]);
		int numMatches = Integer.parseInt(infos[9]);

		return new SpectrumInfo(scanBeg, scanEnd, charge, time, serverName,
		        expMH, tic, lowestSp, numMatches);
	}

	/**
	 * Next hit for the spectrum
	 */
	private PepMatch getNextHit() throws NullPointerException,
	        NumberFormatException, IndexOutOfBoundsException {
		String line = reader.readLine();
		if (line == null)
			return null;

		if (line.startsWith("S\t")) {
			reader.rollBackTheLine();
			return null;
		}

		String[] matchinfo = StringUtil.split(line, '\t');
		int len = matchinfo.length;

		double calculatedMH = Double.parseDouble(matchinfo[3]);
		short rankPre = Short.parseShort(matchinfo[2].trim());
		short rankPrim = Short.parseShort(matchinfo[1].trim());

		int matchedIons = Integer.parseInt(matchinfo[len - 4].trim()), totalIons = Integer
		        .parseInt(matchinfo[len - 3].trim());
		String sequence = matchinfo[len - 2].trim();

		char validationStatus = matchinfo[len - 1].trim().charAt(0);

		/*
		 * The number of scores, for unformed sqt. May be useless? commonly have
		 * 3 scores
		 */
		double[] scores = new double[len - 8];
		for (int i = 0; i < scores.length; i++) {
			String sco = matchinfo[i + 4];

			try {
				scores[i] = Double.parseDouble(sco);
			} catch (NumberFormatException e) {
				//such as "nan"
				if (sco.equalsIgnoreCase("NaN"))
					scores[i] = Double.NaN;
				else
					throw e;
			}
		}

		HashSet<ProMatch> references = new HashSet<ProMatch>();
		while ((line = reader.readLine()) != null && line.startsWith("L\t")) {
			String[] strs = StringUtil.split(line, '\t');
			if (strs.length > 2)
				references.add(new ProMatch(strs[1], strs[2]));
			else
				references.add(new ProMatch(strs[1]));
		}

		if (line != null)
			reader.rollBackTheLine();

		return new PepMatch(rankPrim, rankPre, calculatedMH, scores,
		        matchedIons, totalIons, sequence, validationStatus, references);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISQTReader#close()
	 */
	@Override
	public void close() {
		this.reader.close();
	}
}
