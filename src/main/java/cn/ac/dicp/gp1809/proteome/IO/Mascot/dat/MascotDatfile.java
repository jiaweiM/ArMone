/*
 * *****************************************************************************
 * File: MascotDatfile.java * * * Created on 11-14-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotDatParser.QueryIdx;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.parsers.IPepHitParser;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.parsers.PepHitParserFactory;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.ScanNameFactory;
import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

/**
 * The Mascot Dat file after parsing.
 * 
 * @author Xinning
 * @version 0.2, 05-19-2010, 10:39:43
 */
public class MascotDatfile {

	/**
	 * Private variable iHeader is a (lazy) instance of Header.
	 */
	private Header iHeader = null;

	/**
	 * Private variable iMasses is a (lazy) instance of Masses.
	 */
	private Masses iMasses = null;

	/**
	 * Private variable iEnzymes is a (lazy) instance of Enzymes.
	 */
	private Enzymes iEnzymes = null;

	/**
	 * Private variable iMRP is an instance of MascotDatParser. Elementary
	 * datfile parsing object.
	 */
	private MascotDatParser parser = null;

	/**
	 * Private variable iParameters is a (lazy) instance of Parameters.
	 */
	private Parameters iParameters = null;

	/**
	 * Private variable iProteinMap is a (lazy) instance of ProteinMap.
	 */
	private ProteinMap iProteinMap = null;

	/**
	 * A lazy instance of the IPepHitParser
	 */
	private IPepHitParser pepparser;

	/**
	 * The filename of the Mascot dat file.
	 */
	private String iFileName = null;

	/**
	 * Constructs MascotDatfile instance from a String containing to an existing
	 * path and filename.
	 * 
	 * @param aDatFile
	 *            where to parse your data from
	 * @throws MascotDatParsingException
	 */
	public MascotDatfile(String aDatFile) throws MascotDatParsingException {
		parser = new MascotDatParser(aDatFile);

		setFileName(new File(aDatFile).getAbsolutePath());
	}

	/**
	 * Constructs MascotDatfile instance from a String containing to an existing
	 * path and filename.
	 * 
	 * @param aDatFile
	 *            where to parse your data from
	 * @throws MascotDatParsingException
	 */
	public MascotDatfile(File aDatFile) throws MascotDatParsingException {
		parser = new MascotDatParser(aDatFile);

		setFileName(aDatFile.getAbsolutePath());
	}

	/**
	 * Parse a section (an entry) into map with key--value pairs
	 * 
	 * @param section
	 * @return
	 */
	private HashMap<String, String> parseIntoMap(String[] section) {
		if (section == null)
			throw new NullPointerException("section is null.");

		int len = section.length - 1;
		/*
		 * As the section is the raw strings with the title line and the
		 * boundary line, there should be more than 3 lines
		 */
		if (len < 2)
			throw new IllegalArgumentException(
			        "The section is excpected to be with more than 3 lines, current: "
			                + section.length);
		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 2; i < len; i++) {
			String line = section[i];
			DatEntryParser.Entry entry = DatEntryParser.parseEntry(line);
			map.put(entry.getKey(), entry.getValue());
		}

		return map;
	}

	/**
	 * This method creates a new Header instance. 1.Submit the header String in
	 * the constructor. 2.Receive the parsed data into a Header instance.
	 * 
	 * @return Header instance of Header with all the parsed data of the header
	 *         section of the datfile.
	 */
	public Header getHeaderSection() {
		
		if (iHeader == null) {
			iHeader = new Header(this.parseIntoMap(parser.getHeader()));
		}
		return iHeader;
	}

	/**
	 * This method creates a new Masses instance. 1.Submit the masses HashMap in
	 * the constructor. 2.Receive the parsed data into an Masses instance.
	 * 
	 * @return Masses instance of Masses with all the parsed data of the masses
	 *         section of the datfile.
	 */
	public Masses getMasses() {
		if (iMasses == null) {
			iMasses = new Masses(this.parseIntoMap(parser.getMasses()));
		}
		return iMasses;
	}

	/**
	 * This method creates a new Enzymes instance. 1.Submit the masses raw
	 * strings in the constructor. 2.Receive the parsed data into an Enzymes
	 * instance.
	 * 
	 * @return
	 */
	public Enzymes getEnzyme() {

		if (this.iEnzymes == null) {

			String[] ens = parser.getEnzyme();
			// For some old version mascot, there may be no enzymes section
			if (ens != null) {

				String title = null;
				String cleavage = null;
				String restrict = null;
				String sense = null;

				for (String en : ens) {
					if (en.startsWith("Title"))
						title = en.substring(6);

					else if (en.startsWith("Cleavage"))
						cleavage = en.substring(9);

					else if (en.startsWith("Restrict"))
						restrict = en.substring(9);

					else if (en.equals("Cterm"))
						sense = en;
					
					else if (en.equals("Nterm"))
						sense = en;
					
					else{
						//Useless
					}
				}

				this.iEnzymes = new Enzymes(title, cleavage, restrict, sense);
				
				if(cleavage==null || restrict==null || sense==null)
					this.iEnzymes = Enzymes.Trypsin;
				
			}else{
				this.iEnzymes = Enzymes.Trypsin;
			}
		}

		return this.iEnzymes;
	}

	/**
	 * The number of queries done in the mascot search (a parameter of a Header
	 * instance).
	 * 
	 * @return int number of queries done.
	 */
	public int getNumberOfQueries() {
		
		return this.parser.getQueryNum();
	}
	
	/**
	 * The query index for the qidx value
	 * 
	 * @param qidx
	 * @return
	 */
	private QueryIdx getQueryIndex(int qidx){
		
		if (this.pepparser == null) {
			this.pepparser = PepHitParserFactory.createParser(this
			        .getHeaderSection().getVersion());
		}
		
		return this.parser.getQueryIdx(qidx);
	}

	/**
	 * Get the query with index of "qidx".
	 * 
	 * @param qidx
	 * @return
	 */
	public Query getQuery(int qidx, boolean includePeaks) {
		QueryIdx queryIdx = this.getQueryIndex(qidx);
		return new Query(this.getScanDta(queryIdx, includePeaks), this.getQueryResult(queryIdx));
	}

	/**
	 * Get the hitted peptides of Query with index of "qidx".
	 * 
	 * @param qidx
	 * @return the peptide hits for this query, null if no peptide hit.
	 */
	public QueryResult getQueryResult(int qidx) {
		return this.getQueryResult(this.getQueryIndex(qidx));
	}

	/**
	 * Parse the QueryResult for the Query.
	 * 
	 * @param queryIdx
	 * @return
	 */
	public QueryResult getQueryResult(QueryIdx queryIdx) {
		if (this.pepparser == null) {
			this.pepparser = PepHitParserFactory.createParser(this
			        .getHeaderSection().getVersion());
		}

		int num = queryIdx.getNum_pep_hits();
		
		if (num == -1)
			return null;
		else {
			PeptideHit[] hits = new PeptideHit[num];
			int from = queryIdx.getByteIdx_pep();
			int to = queryIdx.getByteLen_pep() + from;
			
			BufferUtil reader = this.parser.getRestrictedReader(from, to);
			
			int count = 0;
			String qn = "^_^ not start";
			ArrayList<String> list = null;
			String line;
			while(true){
				line=reader.readLine();
				
				if(line == null || !line.startsWith(qn)){
					
					//Not the first instance
					if(list!= null){
						PeptideHit hit = this.pepparser.parse(list.toArray(new String[list.size()]));
						hit.setHomologyThreshold(queryIdx.getQplughole());
						hit.setQueryIdenNum((int) queryIdx.getQmatch());
						hit.setQueryIdentityThreshold(hit.calculateIdentityThreshold());
						hits[count++] = hit;
					}
					
					if(line == null)
						break;
					
					list = new ArrayList<String>(3);
					qn = line.substring(0, line.indexOf('='));
				}
				
				list.add(line);
			}

			QueryResult result = new QueryResult();
			result.setTitle(queryIdx.getQname());
			result.setHits(hits);
			result.setCharge(queryIdx.getCharge());
			
			return result;
		}
	}

	/**
	 * Get the query spectrum of Query with index of "qidx"
	 * 
	 * @param qidx
	 * @return
	 */
	public MascotScanDta getScanDta(int qidx, boolean includePeaks) {
		
		return this.getScanDta(this.getQueryIndex(qidx), includePeaks);
	}
	
	/**
	 * Get the query spectrum of Query with index of "qidx"
	 * 
	 * @param qidx
	 * @return
	 */
	public MascotScanDta getScanDta(QueryIdx queryIdx, boolean includePeaks) {
		if (this.pepparser == null) {
			this.pepparser = PepHitParserFactory.createParser(this
			        .getHeaderSection().getVersion());
		}

    	String name = queryIdx.getQname();
    	
    	if(name == null)
    		return null;
    	IScanName scanname = ScanNameFactory.parseName(name);
    	
		if(includePeaks) {
			
			int from = queryIdx.getByteIdx_dta();
			int to = queryIdx.getByteLen_dta() + from;
			BufferUtil reader = this.parser.getRestrictedReader(from, to);

			
			String line;
			int peakcount = 0;
			MS2PeakList peaklist = null;
			while((line = reader.readLine())!=null) {

				if(line.startsWith("num_vals")) {
					String pc = MascotDatParser.getProp(line, "num_vals");
					peakcount = Integer.parseInt(pc);
					continue;
				}
				
				if(line.startsWith("Ions1")) {
					String peaks = MascotDatParser.getProp(line, "Ions1");
					String[] pks = StringUtil.split(peaks, ',');
					if(pks.length != peakcount) {
						throw new IllegalArgumentException("Nonidentical peak number");
					}
					
					peaklist = new MS2PeakList(peakcount);
					
					PrecursePeak ppeak = new PrecursePeak(queryIdx.getMz(), queryIdx.getInten());
					ppeak.setCharge(queryIdx.getCharge());
					peaklist.setPrecursePeak(ppeak);
					
					for(String pk : pks) {
						int idx = pk.indexOf(':');
						if(idx == -1)
							throw new NullPointerException("No a valid peak.");
						peaklist.add(new Peak(Double.parseDouble(pk.substring(0,idx)), Double.parseDouble(pk.substring(idx+1))));
					}
					
					continue;
				}
			}
			
			
			return new MascotScanDta(scanname, peaklist);
		}
		else {
			double premz = queryIdx.getMz();
			return new MascotScanDta(scanname, premz);
		}
	}

	/**
	 * This method creates a new Parameters instance. 1.Submit the paramaters
	 * HashMap in the constructor. 2.Receive the parsed data into an Parameters
	 * instance.
	 * 
	 * @return Parameters instance of Parameters with all the parsed data of the
	 *         parameters section of the datfile.
	 */
	public Parameters getParameters() {
		if (iParameters == null) {
			iParameters = new Parameters(this.parseIntoMap(parser
			        .getParameter()));
		}
		return iParameters;
	}

	/**
	 * This method gets the ProteinMap. All the proteins from the protein
	 * section are included. The proteinID's include a 2D array with the queries
	 * and peptidehits wherein they were found.
	 * 
	 * @return ProteinMap
	 */
	public ProteinMap getProteinMap() {
		if (iProteinMap == null) {
			iProteinMap = new ProteinMap(this
			        .parseIntoMap(parser.getProteins()));
		}
		return iProteinMap;
	}

	public ProteinMap getDecoyProteinMap() {
		return getProteinMap();
	}

	/**
	 * Returns the name of the filesystem of this Mascot dat file.
	 * 
	 * @return String with the filename.
	 */
	public String getFileName() {
		if (iFileName == null) {
			return "NA";
		} else {
			return iFileName;
		}
	}

	/**
	 * Sets the name of this Mascot dat file as used in the filesystem.
	 * 
	 * @param aFileName
	 *            String with the filename of the Datfile.
	 */
	public void setFileName(String aFileName) {
		iFileName = aFileName;
	}

	/**
	 * close the instance and clear all the lazy instances in memory
	 */
	public void close() {
		parser.close();

		this.iEnzymes = null;
		this.iHeader = null;
		this.iMasses = null;
		this.iParameters = null;
		this.iProteinMap = null;

		//Let garbage collector work
		System.gc();
	}
	
	public static void main(String[] args) throws IOException, MascotDatParsingException {
		
		File file = new File("L:\\Data_DICP\\turnover\\20130920_MCF_PTturnover_velos\\DATfiles_20130920_MCF_PT\\temp" +
				"\\2013920-MCFPT0_3_6h_50mM_F001496.dat");
		MascotDatfile datfile = new MascotDatfile(file);
		MascotDatParser parser = datfile.parser;
		int num = datfile.getNumberOfQueries();
		for(int i=0;i<num;i++){
			System.out.println(datfile.getQueryResult(i));
		}
		
		
	}

}
