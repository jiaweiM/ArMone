/*
 ******************************************************************************
 * File: MS2DtaReader.java * * * Created on 08-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.ms2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;
import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

/**
 * The MS2 DtaReader
 * 
 * @author Xinning
 * @version 0.1, 08-10-2009, 19:39:35
 */
public class MS2DtaReader implements IBatchDtaReader {

	/**
	 * The pattern to split the header. In some cases such as the output by
	 * crux, the header is not well formated using the delimiter of tab, but
	 * also with space. The splitter is used for this.
	 */
	private Pattern headerPattern = Pattern.compile("([^\t ]+)");

	private BufferUtil bfreader;

	private MS2Header header;

	private String curtTitle;

	/**
	 * The base name of the file
	 */
	private String basename;

	private File file;

	private LinkedList<MS2ScanDta> queue;

	public MS2DtaReader(String input) throws DtaFileParsingException,
	        IOException {
		this(new File(input));
	}

	public MS2DtaReader(File input) throws DtaFileParsingException {
		this.file = input;
		this.basename = this.parseBasename(input);

		try {
			this.bfreader = new BufferUtil(input);
		} catch (IOException e) {
			throw new DtaFileParsingException(e);
		}
		
		this.queue = new LinkedList<MS2ScanDta>();
		
		this.parseHeader();
	}

	private String parseBasename(File file) {
		String name = file.getName();

		int idx = name.lastIndexOf('.');
		String basename;

		if (idx == -1) {
			basename = name;
		} else {
			basename = name.substring(0, idx);
		}

		return basename;
	}

	/**
	 * Parse the header
	 * 
	 * @throws DtaFileParsingException
	 * 
	 */
	private void parseHeader() {

		this.header = new MS2Header();

		String line;
		while ((line = bfreader.readLine()) != null) {

			if (line.length() > 0) {

				if (!line.startsWith("H\t")) {
					this.bfreader.rollBackTheLine();
					break;
				}

				String cap = line.substring(2);
				Matcher matcher = this.headerPattern.matcher(cap);
				matcher.find();
				String field = matcher.group();
				int end = matcher.end() + 1;
				if (end > cap.length())
					end = cap.length();

				String value = cap.substring(end).trim();

				if (field.equalsIgnoreCase("CreationDate")) {
					this.header.setCreateTime(value);
					continue;
				}

				if (field.equalsIgnoreCase("Extractor")) {
					this.header.setExtractor(value);
					continue;
				}

				if (field.equalsIgnoreCase("ExtractorVersion")) {
					this.header.setExtractorVersion(value);
					continue;
				}

				if (field.equalsIgnoreCase("ExtractorOptions")) {
					this.header.setExtratorOption(value);
					continue;
				}

				if (field.equalsIgnoreCase("Comments")) {
					this.header.setComments(value);
					continue;
				}

				if (field.equalsIgnoreCase("Comment")) {
					this.header.addComments(value);
					continue;
				}

				//Other fields

			}
		}

		if (line == null) {
			throw new RuntimeException("No scan in the file.");
		}
	}

	/**
	 * The MS2 header
	 * 
	 * @return
	 */
	public MS2Header getHeader() {
		return this.header;
	}

	@Override
	public String getNameofCurtDta() {
		return this.curtTitle;
	}

	@Override
	public MS2ScanDta getNextDta(boolean isIncludePeakList)
	        throws DtaFileParsingException {

		try {

			if (queue.size() > 0) {
				MS2ScanDta dta =  queue.pop();
				this.curtTitle = dta.getScanName().getScanName();
				return dta;
			}

			boolean info = true;
			MS2PeakList peaklist = null;
			int scanBeg = -1, scanEnd = -1;
			double premz = 0;
			ArrayList<Short> charges = new ArrayList<Short>();
			ArrayList<Double> mhs = new ArrayList<Double>();

			String line;
			while ((line = bfreader.readLine()) != null) {

				if (info) {
					//Skip the blank
					if (line.length() < 2)
						continue;

					if (line.startsWith("S\t")) {

						String[] infos = StringUtil.split(line, '\t');

						scanBeg = Integer.parseInt(infos[1]);
						scanEnd = Integer.parseInt(infos[2]);

						premz = Double.parseDouble(infos[3]);

						continue;
					}

					if (line.startsWith("Z\t")) {
						String[] infos = StringUtil.split(line, '\t');

						charges.add(Short.valueOf(infos[1]));
						mhs.add(Double.valueOf(infos[2]));

						continue;
					}

					if (line.startsWith("I\t")) {

						continue;
					}

					if (line.startsWith("D\t")) {

						continue;
					}

					/*
					 * The peaks
					 */
					if(Character.isDigit(line.charAt(0))) {
						bfreader.rollBackTheLine();
						info = false;
						continue;
					}
				}

				if (line.length() < 2 || !Character.isDigit(line.charAt(0))) {

					if (scanBeg == -1) {
						throw new NullPointerException(
						        "Must contain the line with \"S\" tag");
					}

					if (charges.size() == 0) {
						throw new NullPointerException(
						        "Must contain the line with \"Z\" tag");
					}
					
					bfreader.rollBackTheLine();

					
					break;
					
				} else {
					if (isIncludePeakList) {

						if (peaklist == null)
							peaklist = new MS2PeakList();

						String ss[] = StringUtil.split(line, ' ');
						if (ss.length != 2) {
							throw new IllegalArgumentException(
							        "Illegal peak list format: \"" + line
							                + "\".");
						}

						Peak peak = new Peak(Double.parseDouble(ss[0]), Double
						        .parseDouble(ss[1]));

						peaklist.add(peak);
					}
				}
			}
			
			/*
			 * The end of file
			 */
			if (scanBeg == -1) {
				return null;
			}

			
			short charge0 = charges.get(0);
			SequestScanName scanname = new SequestScanName(
			        this.basename, scanBeg, scanEnd, charge0,
			        "dta");

			if (isIncludePeakList) {
				PrecursePeak parent = new PrecursePeak();
				parent.setCharge(charge0);
				parent.setMz(premz);

				peaklist.setPrecursePeak(parent);
			}

			MS2ScanDta dta = isIncludePeakList ? new MS2ScanDta(
			        scanname, peaklist) : new MS2ScanDta(scanname,
			        SpectrumUtil.getMH(premz, charge0));

			this.queue.add(dta);

			for (int i = 1; i < charges.size(); i++) {
				short charge = charges.get(i);

				SequestScanName scanname1 = new SequestScanName(
				        this.basename, scanBeg, scanEnd, charge, "dta");

				if (isIncludePeakList) {
					MS2PeakList peaklist1 = new MS2PeakList();
					PrecursePeak ppeak = peaklist.getPrecursePeak()
					        .deepClone();
					ppeak.setCharge(charge);
					peaklist1.setPrecursePeak(ppeak);

					int size = peaklist.size();

					for (int j = 0; j < size; j++) {
						peaklist1.add(peaklist.getPeak(j).deepClone());
					}

					this.queue
					        .add(new MS2ScanDta(scanname1, peaklist1));
				} else {
					this.queue.add(new MS2ScanDta(scanname1,
					        SpectrumUtil.getMH(premz, charge)));
				}
			}
			
			return this.getNextDta(isIncludePeakList);

		} catch (Exception e) {
			throw new DtaFileParsingException(e);
		}
	}

	@Override
	public int getNumberofDtas() {
		return -1;
	}

	@Override
	public void close() {
		this.bfreader.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getDtaType()
	 */
	@Override
	public DtaType getDtaType() {
		return DtaType.MS2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getFile()
	 */
	@Override
	public File getFile() {
		return this.file;
	}

	/**
	 * The header elements
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 08-10-2009, 09:48:59
	 */
	public static class MS2Header {

		private String createTime;
		private String extractor;
		private String extractorVersion;
		private String extratorOption;
		private String comments;

		private ArrayList<String> commentlist = new ArrayList<String>();

		public MS2Header() {

		}

		/**
		 * @return the createTime
		 */
		public String getCreateTime() {
			return createTime;
		}

		/**
		 * @param createTime
		 *            the createTime to set
		 */
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		/**
		 * @return the extractor
		 */
		public String getExtractor() {
			return extractor;
		}

		/**
		 * @param extractor
		 *            the extractor to set
		 */
		public void setExtractor(String extractor) {
			this.extractor = extractor;
		}

		/**
		 * @return the extractorVersion
		 */
		public String getExtractorVersion() {
			return extractorVersion;
		}

		/**
		 * @param extractorVersion
		 *            the extractorVersion to set
		 */
		public void setExtractorVersion(String extractorVersion) {
			this.extractorVersion = extractorVersion;
		}

		/**
		 * @return the extratorOption
		 */
		public String getExtratorOption() {
			return extratorOption;
		}

		/**
		 * @param extratorOption
		 *            the extratorOption to set
		 */
		public void setExtratorOption(String extratorOption) {
			this.extratorOption = extratorOption;
		}

		/**
		 * @return the comments
		 */
		public String getComments() {
			return comments;
		}

		/**
		 * @param comments
		 *            the comments to set
		 */
		public void setComments(String comments) {
			this.comments = comments;
		}

		/**
		 * Add a comment
		 * 
		 * @param comment
		 */
		void addComments(String comment) {
			this.commentlist.add(comment);
		}

		/**
		 * The comment list
		 * 
		 * @return
		 */
		public ArrayList<String> getCommentlist() {
			return this.commentlist;
		}
	}

	public static void main(String[] args) throws DtaFileParsingException,
	        IOException {
		String path = "D:\\try\\dta.ms2";

		MS2DtaReader reader = new MS2DtaReader(path);

		MS2ScanDta dta ;
		while ((dta = reader.getNextDta(true)) != null) {
			System.out.println(dta);
		}

		reader.close();
	}
}
