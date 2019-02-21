/* 
 ******************************************************************************
 * File: NeutralLossTester.java * * * Created on 2009-3-18
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.usefulTools;

import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossTest;
import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossTest.NeutralInfo;
import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralScanUtil;
import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralScanUtil.ScanPair;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakListGetter;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2ScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;
import cn.ac.dicp.gp1809.util.StringUtil;

import javax.xml.stream.XMLStreamException;
import java.io.*;

/**
 * 
 * 
 * @author Xinning
 * @version 0.1, 03-18-2009, 16:00:02
 */
public class NeutralLossTester {

	private IPeakListGetter gettor;
	private MS2ScanList scanlist;
	private ISpectrumThreshold threshold;

	public NeutralLossTester(String mzdatafile, ISpectrumThreshold threshold) throws FileNotFoundException, XMLStreamException {
		MzDataStaxReader reader = new MzDataStaxReader(mzdatafile);
		reader.rapMS2ScanList();
		this.scanlist = reader.getMS2ScanList();
		this.threshold = threshold;
	}

	/**
	 * Test the neutral loss
	 * 
	 * @param scannum
	 * @param charge
	 * @param lostmass
	 * @return
	 */
	public NeutralInfo testNeu(int scannum, short charge, double lostmass) {
		return NeutralLossTest.testNeutralLoss((IMS2PeakList) this.scanlist.getScan(scannum)
		        .getPeakList(), charge, threshold, lostmass);
	}

	/**
	 * Finish.
	 */
	public void finish() {
		gettor.dispose();
	}

	/**
	 * First create a file contains scan number and charge. One per line
	 * delimited by Tab.
	 * 
	 * @param mzdatafile
	 * @param file_with_scancharge
	 * @throws IOException
	 * @throws NumberFormatException
	 * @throws XMLStreamException 
	 */
	public static void testNeutralLoss_scan_in_file(String mzdatafile,
	        String file_with_scancharge) throws NumberFormatException,
	        IOException, XMLStreamException {

		NeutralLossTester tester = new NeutralLossTester(mzdatafile,
		        SpectrumThreshold.ZERO_INTENSE_THRESHOLD);
		double lostmass = 98d;

		PrintWriter pw = new PrintWriter(file_with_scancharge + ".neu.txt");

		BufferedReader reader = new BufferedReader(new FileReader(
		        file_with_scancharge));

		pw
		        .println("scan\tcharge\tlostPhosphate\ttopN\tpercentage\tlostPhosphateNH3\ttopN\tpercentage\tlostPhosphateH2O\ttopN\tpercentage");

		String line;
		while ((line = reader.readLine()) != null) {
			String[] scancharge = StringUtil.split(line, '\t');
			int scan = Integer.parseInt(scancharge[0]);
			short charge = Short.parseShort(scancharge[1]);
			StringBuilder sb = new StringBuilder(line);

			NeutralInfo info = tester.testNeu(scan, charge, lostmass);
			boolean isneu = info.isNeutralLoss();

			sb.append('\t').append(isneu);
			if (isneu) {
				sb.append('\t').append(info.getTopn()).append('\t').append(
				        info.getIntensityPercent());
			} else {
				sb.append('\t').append(0).append('\t').append(0);
			}

			//phosphate+NH3
			info = tester.testNeu(scan, charge, lostmass + 17);
			isneu = info.isNeutralLoss();

			sb.append('\t').append(isneu);
			if (isneu) {
				sb.append('\t').append(info.getTopn()).append('\t').append(
				        info.getIntensityPercent());
			} else {
				sb.append('\t').append(0).append('\t').append(0);
			}

			//phosphate+H2O
			info = tester.testNeu(scan, charge, lostmass + 18);
			isneu = info.isNeutralLoss();

			sb.append('\t').append(isneu);
			if (isneu) {
				sb.append('\t').append(info.getTopn()).append('\t').append(
				        info.getIntensityPercent());
			} else {
				sb.append('\t').append(0).append('\t').append(0);
			}

			pw.println(sb);
		}

		reader.close();
		pw.close();
		tester.finish();
	}

	/**
	 * The number of scan pairs with charge states of 1+ 2+ and 3+
	 * 
	 * @param mzdata
	 * @return
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public static int[] getNumberCharge_ScanPair(String mzdata) throws FileNotFoundException, XMLStreamException {
		MzDataStaxReader reader = new MzDataStaxReader(mzdata);
		reader.rapMS2ScanList();
		ScanPair [] pairs = new NeutralScanUtil(reader
		        .getMS2ScanList(), 3).getScanPairs_validLoss(
		        SpectrumThreshold.HALF_INTENSE_THRESHOLD, 98);
		
		int[] nums = new int[4];
		for(ScanPair pair : pairs) {
			nums[pair.getCharge()]++;
		}
		
		reader.close();
		
		return nums;
	}

	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		
		NeutralLossTester tester = new NeutralLossTester("D:\\try\\1\\20060622_ALPHA_CASEIN.xml", SpectrumThreshold.HALF_INTENSE_THRESHOLD);
		
		tester.testNeu(80, (short)2, 98);
		
	}

}
