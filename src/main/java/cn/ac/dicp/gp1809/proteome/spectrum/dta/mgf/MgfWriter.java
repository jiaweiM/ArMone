/* 
 ******************************************************************************
 * File: MgfWriter.java * * * Created on 09-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Writer to construct a MGF file.
 * 
 * http://www.matrixscience.com/help/data_file_help.html
 * 
 * @author Xinning
 * @version 0.1.4, 05-25-2010, 15:32:59
 */
public class MgfWriter implements IBatchDtaWriter {

	private static final String lineSeparator = IOConstant.lineSeparator;

	private static final DecimalFormat DF5 = DecimalFormats.DF0_5;

	private PrintWriter pw;
	
	private File file;

	/**
	 * 
	 * @param mgffile
	 *            the path of output mgf file
	 * @param titleParam
	 *            the global title parameters.
	 * @throws DtaWritingException
	 */
	public MgfWriter(String mgffile, MgfParameters titleParam)
	        throws DtaWritingException {
		try {
			this.file = new File(mgffile);
//			this.pw = new PrintWriter(new BufferedWriter(
//			        new FileWriter(mgffile)));
			this.pw = new PrintWriter(mgffile);
		} catch (IOException e) {
			throw new DtaWritingException(e);
		}

		if (titleParam != null)
			this.pw.println(titleParam.toString());
	}

	/**
	 * 
	 * @param mgffile
	 *            the path of output mgf file
	 * @param titleParam
	 *            the global title parameters.
	 * @throws DtaWritingException
	 */
	public MgfWriter(File mgffile, MgfParameters titleParam)
	        throws DtaWritingException {
		try {
			this.file = mgffile;
			this.pw = new PrintWriter(new BufferedWriter(
			        new FileWriter(mgffile)));
		} catch (IOException e) {
			throw new DtaWritingException(e);
		}

		if (titleParam != null)
			this.pw.println(titleParam.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IDtaWriter
	 * #write(cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFile)
	 */
	@Override
	public void write(IScanDta dtafile) {

		StringBuilder sb = new StringBuilder(1000);

		sb.append("BEGIN IONS").append(lineSeparator);
		sb.append("PEPMASS=").append(DF5.format(dtafile.getPrecursorMZ()))
		        .append(lineSeparator);
		sb.append("CHARGE=").append(dtafile.getCharge()).append('+').append(
		        lineSeparator);
		sb.append("TITLE=").append(dtafile.getScanName().getScanName()).append(
		        lineSeparator);

		IMS2PeakList peaks = dtafile.getPeakList();

		if (peaks == null || peaks.size() == 0)
			throw new NullPointerException(
			        "Current dta for writing contains no peak list.");

		int size = peaks.size();

		for (int i = 0; i < size; i++) {
			IPeak peak = peaks.getPeak(i);
			sb.append(peak.getMz()).append(' ').append(peak.getIntensity())
			        .append(lineSeparator);
		}

		sb.append("END IONS");

		this.pw.println(sb);
	}
	
	public void write(IMS2Scan scan, int id) {

		StringBuilder sb = new StringBuilder(1000);

		sb.append("BEGIN IONS").append(lineSeparator);
		sb.append("PEPMASS=").append(DF5.format(scan.getPrecursorMZ()))
		        .append(lineSeparator);
		sb.append("CHARGE=").append(scan.getCharge()).append('+').append(
		        lineSeparator);
		sb.append("TITLE=Spectrum ").append(id).append(" scans: ").append(scan.getScanNum()).append(","+
		        lineSeparator);

		IMS2PeakList peaks = scan.getPeakList();

		if (peaks == null || peaks.size() == 0){
			return;
		}
//			throw new NullPointerException(
//			        "Current dta for writing contains no peak list.");

		int size = peaks.size();

		for (int i = 0; i < size; i++) {
			IPeak peak = peaks.getPeak(i);
			sb.append(peak.getMz()).append(' ').append(peak.getIntensity())
			        .append(lineSeparator);
		}

		sb.append("END IONS");

		this.pw.println(sb);
	}
	
	public void mzxml2Mgf(String mzxml) throws IOException, XMLStreamException{
		
		MzXMLReader reader = new MzXMLReader(mzxml);
		IMS2Scan scan = null;
		int id = 0;

		while((scan=reader.getNextMS2Scan())!=null){
			this.write(scan, ++id);
		}
		this.close();
	}
	
	public void mzxml2Mgf4Mascot(String mzxml) throws IOException, XMLStreamException{
		
		File file = new File(mzxml);
		MzXMLReader reader = new MzXMLReader(file);
		
		ISpectrum spec = null;
		IPeak [] ms1peaks = null;
		
		while((spec=reader.getNextSpectrum())!=null){
			
			int level = spec.getMSLevel();
			
			if(level==1){
				
				ms1peaks = spec.getPeakList().getPeakArray();
				
			}else{
				
				MS2Scan scan = (MS2Scan) spec;
				int charge = scan.getCharge();
				PrecursePeak pp = scan.getPeakList().getPrecursePeak();
				/*IPeak findpeak = this.findIsotope(pp, charge, ms1peaks);
				if(findpeak==null){
					findpeak = pp;
				}else{
					if(pp.getMz()-findpeak.getMz()>3) 
						continue;
				}*/

				double premz = pp.getMz();
				double intensity = pp.getIntensity();
				double rt = scan.getRTMinute();
				int scannum = scan.getScanNum();
				StringBuilder sb = new StringBuilder();
				
				sb.append("BEGIN IONS").append(lineSeparator);
				sb.append("PEPMASS=").append(DF5.format(premz))
				        .append(lineSeparator);
				sb.append("CHARGE=").append(charge).append('+').append(
				        lineSeparator);
				sb.append("TITLE=");
				sb.append("Elution from: ").append(DF5.format(rt)).append(" to ").append(DF5.format(rt));
				sb.append(" period: 0 experiment: 1 cycles: 1 precIntensity: ").append(intensity);
				sb.append(" FinneganScanNumber: ").append(scannum);
				sb.append(" MStype: enumIsNormalMS rawFile: ").append(file.getName()).append(" "+lineSeparator);
				
				IMS2PeakList peaks = scan.getPeakList();

				if (peaks == null || peaks.size() == 0){
					continue;
				}

				int size = peaks.size();

				for (int i = 0; i < size; i++) {
					IPeak peak = peaks.getPeak(i);
					sb.append(peak.getMz()).append(' ').append(peak.getIntensity())
					        .append(lineSeparator);
				}

				sb.append("END IONS");

				this.pw.println(sb);
			}
		}
		this.close();
	}
	
	public void mzxml2Mgf4ProteinPilot(String mzxml) throws IOException, XMLStreamException{
		
		File file = new File(mzxml);
		MzXMLReader reader = new MzXMLReader(file);
		
		ISpectrum spec = null;
		IPeak [] ms1peaks = null;
		
		while((spec=reader.getNextSpectrum())!=null){
			
			int level = spec.getMSLevel();
			
			if(level==1){
				
				ms1peaks = spec.getPeakList().getPeakArray();
				
			}else{
				
				MS2Scan scan = (MS2Scan) spec;
				int charge = scan.getCharge();
				PrecursePeak pp = scan.getPeakList().getPrecursePeak();
				IPeak findpeak = this.findIsotope(pp, charge, ms1peaks);
				if(findpeak==null){
					findpeak = pp;
				}else{
					if(pp.getMz()-findpeak.getMz()>3) 
						continue;
				}
				
				double rt = scan.getRTMinute();
				int scannum = scan.getScanNum();
				StringBuilder sb = new StringBuilder();
				
				sb.append("BEGIN IONS").append(lineSeparator);
				sb.append("TITLE=Locus:1.1.1."+scannum+".1").append(lineSeparator);
				sb.append("CHARGE="+charge+"+").append(lineSeparator);
				sb.append("PEPMASS="+DF5.format(findpeak.getMz())).append(lineSeparator);
				sb.append("RTINSECONDS="+(int)rt).append(lineSeparator);

				IMS2PeakList peaks = scan.getPeakList();

				if (peaks == null || peaks.size() == 0){
					continue;
				}

				int size = peaks.size();

				for (int i = 0; i < size; i++) {
					IPeak peak = peaks.getPeak(i);
					sb.append(peak.getMz()).append(' ').append(peak.getIntensity())
					        .append(lineSeparator);
				}

				sb.append("END IONS");

				this.pw.println(sb);
			}
		}
		this.close();
	}
	
	public void mzxml2Mgf(String scans, String mzxml) throws IOException, XMLStreamException{
		
		HashSet <Integer> set = new HashSet <Integer>();
		BufferedReader br = new BufferedReader(new FileReader(scans));
		String line = null;
		while((line=br.readLine())!=null){
			set.add(Integer.parseInt(line));
		}
		br.close();
		
		MzXMLReader reader = new MzXMLReader(mzxml);
		IMS2Scan scan = null;
		int id = 0;

		ISpectrum sp = null;
		while((sp=reader.getNextSpectrum())!=null){
			int level = sp.getMSLevel();
			int scannum = sp.getScanNum();
			if(level==2 && set.contains(scannum)){
				scan = (MS2Scan)sp;
				this.write(scan, ++id);
			}
		}
		this.close();
	}

	private IPeak findIsotope(IPeak precursorPeak, int charge, IPeak [] ms1Peaks){
		
		double mzThresPPM = 1E-5;
		double dm = 1.00286864;
		
		int isoloc = Arrays.binarySearch(ms1Peaks, precursorPeak);
		if(isoloc<0) isoloc = -isoloc-1;
		double mz = precursorPeak.getMz();
		double intensity = precursorPeak.getIntensity();
		IPeak peak = null;
		int i=isoloc;

		for(;i>=0;i--){
			double delta = mz-ms1Peaks[i].getMz()-dm/(double)charge;
			double loginten = Math.log10(intensity/ms1Peaks[i].getIntensity());
			if(Math.abs(delta)<=mz*mzThresPPM){
				if(Math.abs(loginten)<1){
					mz = ms1Peaks[i].getMz();
					peak = ms1Peaks[i];
					if(intensity<ms1Peaks[i].getIntensity())
						intensity = ms1Peaks[i].getIntensity();
				}
			}else if(delta>mz*mzThresPPM*1E-6){
				break;
			}
		}
		
		return peak;
	}
	
	public void plusTen(String in) throws DtaFileParsingException, IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null){
			if(line.startsWith("PEPMASS")){
				double mass = Double.parseDouble(line.substring(line.indexOf("=")+1));
				this.pw.write("PEPMASS="+(mass+10.0)+lineSeparator);
			}else{
				this.pw.write(line+lineSeparator);
			}
		}
		reader.close();
		this.close();
	}
	
	public void correction(String in, double ppm) throws DtaFileParsingException, IOException{
		DecimalFormat df5 = DecimalFormats.DF0_5;
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null){
			if(line.startsWith("PEPMASS")){
				double mass = Double.parseDouble(line.substring(line.indexOf("=")+1));
				mass = mass-mass*ppm*1E-6;
				this.pw.write("PEPMASS="+df5.format(mass)+lineSeparator);
			}else{
				this.pw.write(line+lineSeparator);
			}
		}
		reader.close();
		this.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IDtaWriter#close()
	 */
	@Override
	public void close() {
		this.pw.close();
	}
	
	/**
	 * size of current file
	 * 
	 * @return
	 */
	public long size() {
		this.pw.flush();
		return this.file.length();
	}

	/**
	 * 
	 * The parameters for MGF file. The input entry and its corresponding value
	 * should be validated.
	 * 
	 * @author Xinning
	 * @version 0.1, 09-29-2008, 13:55:58
	 */
	public static class MgfParameters {

		private StringBuilder comments;

		private LinkedHashMap<String, String> params;

		public MgfParameters() {
			this.params = new LinkedHashMap<String, String>();
			comments = new StringBuilder();
		}

		/**
		 * Add a parameter.
		 * 
		 * @param entry
		 * @param value
		 * @return
		 */
		public boolean addParameter(String entry, String value) {

			if (!validate(entry, value))
				return false;

			this.params.put(entry, value);

			return true;
		}

		/**
		 * Add comments
		 * 
		 * @param comments
		 * @return
		 */
		public boolean addComments(String comment) {
			if (comment != null) {
				this.comments.append('#').append(comment).append(lineSeparator);
				return true;
			}
			return false;
		}

		/*
		 * Validate the entry and its corresponding value.
		 */
		private static boolean validate(String entry, String value) {

			// Validation has not been used
			return true;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append(this.comments).append(lineSeparator);

			for (Iterator<Entry<String, String>> iterator = this.params
			        .entrySet().iterator(); iterator.hasNext();) {
				sb.append(iterator.next().toString()).append(lineSeparator);
			}

			return sb.toString();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaWriter#getDtaType()
	 */
	@Override
	public DtaType getDtaType() {
		return DtaType.MGF;
	}
	
	public static void main(String [] args) throws Exception{
		
/*		MgfWriter writer = new MgfWriter("H:\\2" +
				"\\F9.5286.mgf", new MgfParameters());
		SequestDtaReader reader = new SequestDtaReader("H:\\2" +
				"\\phosphopeptide_F9.5286.5286.3.dta");
		SequestScanDta dta = reader.getDtaFile(true);
		IScanName name = dta.getScanName();
		String basename = name.getBaseName();
		String exten = name.getExtension();
		short charge = name.getCharge();
		IMS2PeakList peaks = dta.getPeakList();
		for(int i=1;i<501;i++){
			SequestScanName sname = new SequestScanName(basename, i, i, charge, exten);
			MS2ScanDta scandta = new MS2ScanDta(sname, peaks);
			writer.write(scandta);
		}
		writer.close();
*/		
//		MgfWriter writer = new MgfWriter("H:\\OGLYCAN\\OGlycan_20140405\\trypsin_20140405\\" +
//				"trypsin_20140405.plus10.mgf", null);
//		MgfWriter writer = new MgfWriter("C:\\Inetpub\\wwwroot\\ISB\\data\\" +
//				"20141011_casein_deGal_HCD_EThcD_highintense.mgf", null);
//		writer.mzxml2Mgf4Mascot("C:\\Inetpub\\wwwroot\\ISB\\data\\" +
//				"20141011_casein_deGal_HCD_EThcD_highintense.mzXML");
//		writer.mzxml2Mgf4ProteinPilot("H:\\20130519_glyco\\" +
//				"Centroid_Rui_20130515_fetuin_HILIC_deglyco_HCD.mzXML");
//		writer.mzxml2Mgf("H:\\20130519_glyco\\HCD\\Centroid\\35_scannum.txt", 
//				"H:\\20130519_glyco\\HCD\\Centroid\\Centroid_Rui_20130515_fetuin_HILIC_HCD_35%.mzXML");
//		writer.plusTen("H:\\OGLYCAN\\OGlycan_20140405\\trypsin_20140405\\trypsin_20140405.oglycan.mgf");
		
		double ppm = 1.07864;
		MgfWriter writer = new MgfWriter("H:\\OGLYCAN2\\20141024_15glyco\\2D_trypsin\\plusTen\\" +
				"2D_trypsin.oglycan.mgf", null);
//		writer.correction("J:\\20141112\\2D_trypsin_GluC_HCC\\20141026_O_linked_serum_GluC_trypisn_HCC_F7.mgf", ppm);
		writer.plusTen("H:\\OGLYCAN2\\20141024_15glyco\\2D_trypsin\\2D_trypsin.oglycan.mgf");
	}

}
