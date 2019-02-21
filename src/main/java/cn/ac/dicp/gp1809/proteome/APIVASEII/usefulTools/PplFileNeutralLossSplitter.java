/* 
 ******************************************************************************
 * File: PplFileNeutralLossSplitter.java * * * Created on 03-28-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.usefulTools;

import cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss.NeutralLossTest;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

import java.io.IOException;

/**
 * Split the ppl file by whether there is neutral loss
 * 
 * @author Xinning
 * @version 0.1, 03-28-2010, 19:25:38
 */
public class PplFileNeutralLossSplitter {
	
	private ISpectrumThreshold threshold;
	private double loss = 98;
	
	private PeptideListReader reader;
	private PeptideListWriter neutralWriter, noneutralwriter;

	
	public PplFileNeutralLossSplitter(String pplfile) throws FileDamageException, IOException {
		this.reader = new PeptideListReader(pplfile);
		
		this.threshold = new SpectrumThreshold(0.8, 0.5);
		
		String neufile = pplfile.substring(0, pplfile.length()-3)+"neu.ppl";
		String noneufile = pplfile.substring(0, pplfile.length()-3)+"noneu.ppl";
		neutralWriter = new PeptideListWriter(neufile, this.reader.getPeptideFormat(), this.reader.getSearchParameter(), 
				this.reader.getDecoyJudger(), reader.getProNameAccesser());
		this.noneutralwriter = new PeptideListWriter(noneufile, this.reader.getPeptideFormat(), this.reader.getSearchParameter(), 
				this.reader.getDecoyJudger(), reader.getProNameAccesser());
	}
	
	/**
	 * process
	 * 
	 * @throws ProWriterException
	 */
	public void process() throws ProWriterException {
		IPeptide pep;
		while((pep=this.reader.getPeptide())!=null) {
			IMS2PeakList[] lists = this.reader.getPeakLists();
			IMS2PeakList ms2list = lists[0];
			
			boolean neu = NeutralLossTest.testNeutralLoss(ms2list, pep.getCharge(), threshold, loss).isNeutralLoss();
			if(neu) {
				this.neutralWriter.write(pep, lists);
			}
			else {
				this.noneutralwriter.write(pep, lists);
			}
		}
		
		this.reader.close();
		this.neutralWriter.close();
		this.noneutralwriter.close();
	}
	

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 * @throws ProWriterException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException, ProWriterException {
		
		String file = "E:\\APIVASEII\\cd1\\search_results\\yeast\\mascot\\ms2\\yeast_ms2_all.ppl";
		
		PplFileNeutralLossSplitter splitter = new PplFileNeutralLossSplitter(file);
		
		splitter.process();

	}

}
