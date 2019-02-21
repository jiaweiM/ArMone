/* 
 ******************************************************************************
 * File: MgfReader.java * * * Created on 09-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.Description;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;
import cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.QTofMgfScanName;
import cn.ac.dicp.gp1809.proteome.util.ScanNameFactory;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;
import cn.ac.dicp.gp1809.proteome.util.UnknownFormatScanName;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;

/**
 * The reader for mgf file
 * 
 * @author Xinning
 * @version 0.1.2, 08-10-2009, 19:34:40
 */
public class MgfReader implements IBatchDtaReader, IRawSpectraReader {
	/**
	 * The title pattern. Contains the trim action
	 */
	private static final Pattern TITLE_PATTERN = Pattern
	        .compile(" *(\\w+) *= *(.+) *");

	/**
	 * The charge state pattern, used to parse the charge state. Please use
	 * find() method
	 */
	private static final Pattern CHARGE_PATTERN = Pattern.compile("(\\d)+");

	private BufferedReader reader;

	private String curtTitle;

	private File file;

	private LinkedList<MgfScanDta> queue;
	
	private MS2ScanList ms2ScanList;
	
	private String baseName;

	
	/**
	 * The charges of global. If new charge defined ,use this as global charge
	 * state
	 */
//	private short [] charges = new short[] { 2, 3 };
	private short [] charges = new short[] {0};

	public MgfReader(String input) throws DtaFileParsingException, FileNotFoundException {
		this(new File(input));
	}

	public MgfReader(File input) throws DtaFileParsingException, FileNotFoundException {
		this.file = input;
		try {
//			this.reader = new BufferUtil(input);
			this.reader = new BufferedReader(new FileReader(input));
		} catch (IOException e) {
			throw new DtaFileParsingException(e);
		}
		queue = new LinkedList<MgfScanDta>();

		try {
			this.parseGlobalParam();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public String getFileName(){
		return this.baseName;
	}
	
	@Override
	public String getNameofCurtDta() {
		return this.curtTitle;
	}

	/**
	 * The global parameter
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	private void parseGlobalParam() throws NumberFormatException, IOException {

		String line;
		while ((line = reader.readLine()) != null) {

			Matcher matcher = TITLE_PATTERN.matcher(line);
			//A title column
			if (matcher.matches()) {
				String key = matcher.group(1);
				String value = matcher.group(2);

				//The global charge states
				if (key.equalsIgnoreCase("CHARGE")) {
					Matcher cmatcher = CHARGE_PATTERN.matcher(value);

					IntArrayList intlist = new IntArrayList();
					while (cmatcher.find()) {
						intlist.add(Short.parseShort(cmatcher.group()));
					}

					charges = intlist.toShortArray();

					break;
				}
			}else{
				this.reader = new BufferedReader(new FileReader(file));
				break;
			}
		}

		String name = file.getName();
		int loc = name.lastIndexOf(".");
		if(loc==-1){
			this.baseName = name;
		}else{
			this.baseName = name.substring(0,loc);
		}
	}

	@Override
	public MgfScanDta getNextDta(boolean isIncludePeakList)
	        throws DtaFileParsingException {
		try {

			if (queue.size() > 0) {
				return queue.pop();
			}

			String line;
			while ((line = reader.readLine()) != null) {
				
				if (line.equalsIgnoreCase("BEGIN IONS")) {

					boolean start_peaks = false;
					MS2PeakList peaklist = null;

					double premz = 0;
					double preintens = 0;
//					short[] charges = this.charges;
					short[] charges = new short[]{0};
					String scanname = "";

					while (!(line = reader.readLine())
					        .equalsIgnoreCase("END IONS")) {

						if (!start_peaks) {
							Matcher matcher = TITLE_PATTERN.matcher(line);
							//A title column
							if (matcher.matches()) {
								String key = matcher.group(1);
								String value = matcher.group(2);

								if (key.equalsIgnoreCase("TITLE")) {
									scanname = value;
									continue;
								}

								if (key.equalsIgnoreCase("CHARGE")) {
									Matcher cmatcher = CHARGE_PATTERN
									        .matcher(value);

									IntArrayList intlist = new IntArrayList();
									while (cmatcher.find()) {
										intlist.add(Short.parseShort(cmatcher
										        .group()));
									}

									charges = intlist.toShortArray();

									continue;
								}

								if (key.equalsIgnoreCase("PEPMASS")) {

									String vt = value.trim();
									int idx;
									if ((idx = vt.indexOf(' ')) != -1) {
										premz = Double.parseDouble(vt
										        .substring(0, idx));
										preintens = Double.parseDouble(vt
										        .substring(idx + 1));
									} else {
										premz = Double.parseDouble(vt);
									}

									continue;
								}

								//		System.out.println("Unused caption: " + key);

								continue;
							}

							//begin reading the ions
							start_peaks = true;

							if (isIncludePeakList) {
								PrecursePeak parent = new PrecursePeak();
								parent.setCharge(charges[0]);
								parent.setMz(premz);
								parent.setIntensity(preintens);

								peaklist = new MS2PeakList();
								peaklist.setPrecursePeak(parent);
							}
						}

						if (isIncludePeakList) {
/*							
							if(line.indexOf(' ')>-1){
								int s = line.indexOf(' ');
								String s1 = line.substring(0, s).trim();
								String s2 = line.substring(s).trim();
								Peak peak = new Peak(Double.parseDouble(s1),
									Double.parseDouble(s2));
								peaklist.add(peak);
							}
							
							else if(line.indexOf('\t')>-1){
								int s = line.indexOf('\t');
								String s1 = line.substring(0, s).trim();
								String s2 = line.substring(s).trim();
								Peak peak = new Peak(Double.parseDouble(s1),
									Double.parseDouble(s2));
								peaklist.add(peak);
							}
							
							else{
								throw new IllegalArgumentException(
								        "Illegal peak list format: \"" + line
								                + "\".");
							}
*/							
							String ss [] = line.split("\\s");
/*							if (ss.length != 2) {
								throw new IllegalArgumentException(
								        "Illegal peak list format: \"" + line
								                + "\".");
							}
*/
							Peak peak = new Peak(Double.parseDouble(ss[0]),
							        Double.parseDouble(ss[1]));
							peaklist.add(peak);
				
						}
					}

					this.curtTitle = scanname;
					IScanName scanName = ScanNameFactory.parseName(scanname);
					if(scanName.getBaseName()==null)
						scanName.setBaseName(baseName);
					
					boolean known = scanName instanceof IKnownFormatScanName;

					/*
					 * Renew the scan if the name format is known
					 */
					IScanName parseName;
					if (known) {
						if(scanName instanceof QTofMgfScanName){
							scanName.setBaseName(this.baseName);
							parseName = new SequestScanName(this.baseName,
							        scanName.getScanNumBeg(), scanName
							                .getScanNumEnd(), charges[0],
							        "dta");

						}else{
							parseName = new SequestScanName(scanName.getBaseName(),
							        scanName.getScanNumBeg(), scanName
							                .getScanNumEnd(), charges[0],
							        "dta");
						}
					}
					else {
						parseName = new UnknownFormatScanName(scanName.getScanName()+"."+charges[0]);
					}

					MgfScanDta dta = isIncludePeakList ? new MgfScanDta(
					        parseName, peaklist) : new MgfScanDta(parseName,
					        SpectrumUtil.getMH(premz, charges[0]));
   
					this.queue.add(dta);

					for (int i = 1; i < charges.length; i++) {
						short charge = charges[i];

						if (known) {
							parseName = new SequestScanName(scanName.getBaseName(),
							        scanName.getScanNumBeg(), scanName
							                .getScanNumEnd(), charges[i],
							        "dta");
						}
						else {
							parseName = new UnknownFormatScanName(scanName.getScanName()+"."+charges[i]);
						}

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
							        .add(new MgfScanDta(parseName, peaklist1));
						} else {
							this.queue.add(new MgfScanDta(parseName,
							        SpectrumUtil.getMH(premz, charges[i])));
						}

					}

					return this.getNextDta(isIncludePeakList);
				}
			}

			return null;

		} catch (Exception e) {
			throw new DtaFileParsingException(e);
		}
	}

	/**
	 * Not implemented for mgf file, the returned value will always be 0
	 */
	@Override
	public int getNumberofDtas() {
		return 0;
	}

	@Override
	public void close() {
		this.queue = null;
		this.ms2ScanList = null;
		try {
			this.reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getDtaType()
	 */
	@Override
	public DtaType getDtaType() {
		return DtaType.MGF;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getFile()
	 */
	@Override
	public File getFile() {
		return file;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS1ScanList()
	 */
	@Override
	public MS1ScanList getMS1ScanList() {
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS2PeakList(int)
	 */
	@Override
	public IMS2PeakList getMS2PeakList(int scan_num) {
		return (IMS2PeakList) this.ms2ScanList.getScan(scan_num).getPeakList();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS2ScanList()
	 */
	@Override
	public MS2ScanList getMS2ScanList() {
		return this.ms2ScanList;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getNextMS1Scan()
	 */
	@Override
	public MS1Scan getNextMS1Scan() {
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getNextMS2Scan()
	 */
	@Override
	public MS2Scan getNextMS2Scan() {
		
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				
				if(line.trim().length()==0)
					continue;

				if (line.equalsIgnoreCase("BEGIN IONS")) {

					boolean start_peaks = false;
					MS2PeakList peaklist = null;

					double premz = 0;
					double preintens = 0;
					short[] charges = this.charges;
					String scanname = "";

					while (!(line = reader.readLine())
					        .equalsIgnoreCase("END IONS")) {
						
						if(line.trim().length()==0)
							continue;

						if (!start_peaks) {
							Matcher matcher = TITLE_PATTERN.matcher(line);
							//A title column
							if (matcher.matches()) {
								String key = matcher.group(1);
								String value = matcher.group(2);

								if (key.equalsIgnoreCase("TITLE")) {
									scanname = value;
									continue;
								}

								if (key.equalsIgnoreCase("CHARGE")) {
									Matcher cmatcher = CHARGE_PATTERN
									        .matcher(value);

									IntArrayList intlist = new IntArrayList();
									while (cmatcher.find()) {
										intlist.add(Short.parseShort(cmatcher
										        .group()));
									}

									charges = intlist.toShortArray();

									continue;
								}

								if (key.equalsIgnoreCase("PEPMASS")) {

									String vt = value.trim();
									int idx;
									if ((idx = vt.indexOf(' ')) != -1) {
										premz = Double.parseDouble(vt
										        .substring(0, idx));
										preintens = Double.parseDouble(vt
										        .substring(idx + 1));
									} else {
										premz = Double.parseDouble(vt);
									}

									continue;
								}

								//		System.out.println("Unused caption: " + key);

								continue;
							}

							//begin reading the ions
							start_peaks = true;

							PrecursePeak parent = new PrecursePeak();
							parent.setCharge(charges[0]);
							parent.setMz(premz);
							parent.setIntensity(preintens);

							peaklist = new MS2PeakList();
							peaklist.setPrecursePeak(parent);
						}

						String ss [] = line.split("\\s");
						if (ss.length != 2 && ss.length!=3) {
							System.out.println(this.file.getAbsolutePath());
							continue;
//							throw new IllegalArgumentException(
//							        "Illegal peak list format: \"" + line
//							                + "\".");
						}

						Peak peak = new Peak(Double.parseDouble(ss[0]),
						        Double.parseDouble(ss[1]));
						peaklist.add(peak);
					}

					this.curtTitle = scanname;
					IScanName scanName = ScanNameFactory.parseName(scanname);
					int scannum = scanName.getScanNumBeg();

					Description des = new Description(scannum, 2, premz, charges[0], preintens);
					MS2Scan ms2scan = new MS2Scan(des, peaklist);
					ms2scan.setScanName(scanName);
					
					return ms2scan;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getNextSpectrum()
	 */
	@Override
	public ISpectrum getNextSpectrum() {
		
		IMS2Scan scan = this.getNextMS2Scan();
		return scan;

	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getPeakList(int)
	 */
	@Override
	public IPeakList getPeakList(int scan_num) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#rapMS1ScanList()
	 */
	@Override
	public void rapMS1ScanList() {
		// TODO Auto-generated method stub
		// do nothing
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#rapMS2ScanList()
	 */
	@Override
	public void rapMS2ScanList() {
		// TODO Auto-generated method stub
		
		this.ms2ScanList = new MS2ScanList(DtaType.MGF);

		IMS2Scan scan;
		while ((scan=this.getNextMS2Scan()) != null) {

//			System.out.println(scan.getPrecursorMZ());
			ms2ScanList.add(scan);

		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS1TotalCurrent()
	 */
	@Override
	public double getMS1TotalCurrent() {
		// TODO Auto-generated method stub
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#rapScanList()
	 */
	@Override
	public void rapScanList() {
		// TODO Auto-generated method stub
		// do nothing
	}

	private static void compare(String in1, String in2, String out) throws DtaFileParsingException, 
		FileDamageException, IOException{
		
		HashMap <String, IPeptide> map = new HashMap <String, IPeptide>();
		PeptideListReader reader = new PeptideListReader("H:\\wiff2mgf\\charge234\\" +
				"121119_human_liver_T_2nd_batch_test_F004440.dat.ppl");
		IPeptide pep = null;
		while((pep=reader.getPeptide())!=null){
			String scannum = pep.getScanNum();
			map.put(scannum.substring(0, scannum.indexOf(",")+2), pep);
		}
		reader.close();
		
		PrintWriter pw = new PrintWriter(out);
		
		MgfReader r1 = new MgfReader(in1);
		MgfReader r2 = new MgfReader(in2);

		MS2Scan scan1 = null;
		MS2Scan scan2 = null;

		while((scan1=r1.getNextMS2Scan())!=null && (scan2=r2.getNextMS2Scan())!=null){
			
			double mz1 = scan1.getPrecursorMZ();
			double mz2 = scan2.getPrecursorMZ();
			short charge1 = scan1.getCharge();
			short charge2 = scan2.getCharge();
			
			StringBuilder sb = new StringBuilder();
			sb.append(scan1.getScanName().getScanName()).append("\t");
			sb.append(scan2.getScanNum()).append("\t");
			sb.append(mz1).append("\t");
			sb.append(mz2).append("\t");
			sb.append(mz2-mz1).append("\t");

			sb.append(charge1).append("\t");
			sb.append(charge2).append("\t");
			sb.append(charge1==charge2).append("\t");

			if(charge1!=0 && charge2!=0){
				sb.append(charge1==charge2).append("\t");
				if(map.containsKey(scan1.getScanName().getScanName())){
					IPeptide peptide = map.get(scan1.getScanName().getScanName());
					sb.append(peptide.getPrimaryScore()).append("\t");
					sb.append(peptide.getMr()/(double)peptide.getCharge()+AminoAcidProperty.PROTON_W).append("\t");
					if(peptide.getPrimaryScore()>20 && (charge1!=charge2)){
						sb.append("mipa\n");
//						pw.write(sb.toString());
					}else{
						sb.append("\n");
					}
				}else{
					sb.append("\n");
				}
			}else{
				sb.append("\n");
			}
			pw.write(sb.toString());
		}

		pw.close();
	}
	
	private static void compare2(String in1, String in2, String out) throws DtaFileParsingException, 
		FileDamageException, IOException{
		
		HashMap <Integer, Integer> scanmap = new HashMap <Integer, Integer>();
		HashMap <Integer, Double> mzmap = new HashMap <Integer, Double>();
		HashMap <Integer, Double> scoremap = new HashMap <Integer, Double>();
		BufferedReader reader1 = new BufferedReader(new FileReader(in1));
		String line = null;
		while((line=reader1.readLine())!=null){
			String [] cs = line.split("\t");
			scanmap.put(Integer.parseInt(cs[1]), Integer.parseInt(cs[6]));
			mzmap.put(Integer.parseInt(cs[1]), Double.parseDouble(cs[2]));
			if(cs.length>9 && cs[9].length()>0)
			scoremap.put(Integer.parseInt(cs[1]), Double.parseDouble(cs[9]));
		}
		reader1.close();

		PrintWriter pw = new PrintWriter(out);
		
		MgfReader r2 = new MgfReader(in2);

		MS2Scan scan2 = null;

		while((scan2=r2.getNextMS2Scan())!=null){
			
			StringBuilder sb = new StringBuilder();
			int scannum = scan2.getScanNum();
			if(scanmap.containsKey(scannum)){
				if(scanmap.get(scannum)==scan2.getCharge() && scanmap.get(scannum)!=0){
					sb.append(scan2.getScanNum()).append("\t");
					double mz1 = mzmap.get(scannum);
					double mz2 = scan2.getPrecursorMZ();
					short charge2 = scan2.getCharge();
					
					sb.append(charge2).append("\t");
					sb.append(mz1).append("\t");
					sb.append(mz2).append("\t");
					sb.append(mz2-mz1).append("\t");
					if(scoremap.containsKey(scannum)){
						sb.append(scoremap.get(scannum)).append("\n");
					}else{
						sb.append("\n");
					}
					
					pw.write(sb.toString());
				}
			}
		}

		pw.close();
	}
	
	public static void main(String[] args) throws DtaFileParsingException, FileDamageException, IOException {
		
/*		MgfReader reader = new MgfReader("H:\\wiff2mgf" +
				"\\121119_human_liver_T_2nd_batch_test-121119-test.MzXMLProfileReader0.mgf");
		
		int [] chargelist = new int [5];
		int total = 0;
//		reader.rapMS2ScanList();
		MgfScanDta scan;
		while((scan = reader.getNextDta(true))!=null){
			total++;
			int charge = scan.getCharge();
			if(charge>=0 && charge<=4){
				chargelist[charge]++;
			}else{
				System.out.println(charge);
			}
			IMS2PeakList peaks = scan.getPeakList();
			if(scan.getScanNumberBeg()==11538){
//				System.out.println(peaks.getTotIonCurrent()+"\t"+peaks.getTotIonCurrent()*0.0002);
				IPeak [] peaklist = peaks.getPeakList();
				for(int i=1;i<peaklist.length;i++){
					if(peaklist[i].getMz()-peaklist[i-1].getMz()<0.006)
					System.out.println(peaklist[i].getMz()+"\t"
							+(peaklist[i].getMz()-peaklist[i-1].getMz()));
				}
				break;
			}
			
			System.out.println(peaks.getPrecursePeak()+"\t"+peaks.getPrecursePeak().getCharge());
		}
		System.out.println(total+"\t"+MathTool.getTotal(chargelist));
		System.out.println(Arrays.toString(chargelist));
//		System.out.println(reader.getPeakList(22));
		reader.close();
*/
		MgfReader.compare2("H:\\wiff2mgf\\test.direct.compare.txt", 
				"H:\\wiff2mgf\\test.direct.2.mgf", 
				"H:\\wiff2mgf\\test.direct.compare.2.txt");
		
	}

	

}
