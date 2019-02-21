/* 
 ******************************************************************************
 * File: PplNeutralLossTester.java * * * Created on 07-15-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.usefulTools;

import java.io.IOException;
import java.io.PrintWriter;

import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossTest;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * Test the neutral loss in the ppl file
 * 
 * @author Xinning
 * @version 0.1, 07-15-2009, 08:54:03
 */
public class PplNeutralLossTester {
	
	public static final ISpectrumThreshold THRESHOLD = new SpectrumThreshold(0.8, 0.05);
	
	private PeptideListReader reader;
	
	private PrintWriter pw;
	
	public PplNeutralLossTester(String pplfile, String output) throws FileDamageException, IOException {
		this.reader = new  PeptideListReader(pplfile);
		pw = new PrintWriter(output);
	}
		
	
	public void process() {
		
		IPeptide pep ;
		while((pep = this.reader.getPeptide())!=null) {
			IMS2PeakList[] peaklists = this.reader.getPeakLists();
			
			NeutralLossTest.NeutralInfo info = NeutralLossTest.testNeutralLoss(peaklists[0], pep.getCharge(), THRESHOLD, 98);
			
			double per = info.getIntensityPercent();
			
			pw.println(pep+"\t"+per+"\t"+info.getTopn());
		}
		
	}
	
	public void close() {
		this.reader.close();
		this.pw.close();
	}
	

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException {
		
		
		PplNeutralLossTester tester = new PplNeutralLossTester(args[0], args[1]);
		
		tester.process();
		
		tester.close();
		
		
	}

}
