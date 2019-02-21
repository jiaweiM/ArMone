/* 
 ******************************************************************************
 * File: SequestPplDataForInput.java * * * Created on 08-07-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

import java.util.ArrayList;
import java.util.List;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide;

/**
 * Data input for SEQUEST results from ppl file
 * 
 * @author Xinning
 * @version 0.1.1, 09-14-2010, 20:20:28
 */
public class SequestPplDataForInput implements IDataForInput {
	
	public SequestPplDataForInput() {
	}
	
	
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.sequest.IDataForInput#getPeptide(short)
	 */
	@Override
	public float[][] getPeptide(IPeptideListReader reader, short charge) throws PeptideParsingException {
		
		if(reader == null) {
			throw new NullPointerException("Reader is null.");
		}
		
		if(reader.getPeptideType() != PeptideType.SEQUEST) {
			throw new IllegalArgumentException("Currently only support SEQUEST.");
		}
		
		
		//This value is evaluated from human samples;
		int approximateNum = reader.getNumberofPeptides();
		List<float[]> list = new ArrayList<float[]>(approximateNum);
			
		IPeptide peptide;
		while((peptide=reader.getPeptide())!=null){
			
			if(peptide.getCharge()==charge)
				this.add(list,(ISequestPeptide)peptide);
		}
		
		System.out.println("Total peptides: "+list.size());
		
		return list.toArray(new float[0][0]);
	
	}
	
	/**
	 * Get peptides with higher charge than the specific. For 4+ and upper
	 * 
	 * @param reader
	 * @param charge
	 * @return
	 * @throws PeptideParsingException
	 */
	public float[][] getPeptideWithHigherCharge(IPeptideListReader reader, short charge) throws PeptideParsingException {
		
		if(reader == null) {
			throw new NullPointerException("Reader is null.");
		}
		
		if(reader.getPeptideType() != PeptideType.SEQUEST) {
			throw new IllegalArgumentException("Currently only support SEQUEST.");
		}
		
		
		//This value is evaluated from human samples;
		int approximateNum = reader.getNumberofPeptides();
		List<float[]> list = new ArrayList<float[]>(approximateNum);
			
		IPeptide peptide;
		while((peptide=reader.getPeptide())!=null){
			
			if(peptide.getCharge()>=charge)
				this.add(list,(ISequestPeptide)peptide);
		}
		
		System.out.println("Total peptides: "+list.size());
		
		return list.toArray(new float[0][0]);
	
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.sequest.IDataForInput#getPeptide(short, short)
	 */
	@Override
	public float[][] getPeptide(IPeptideListReader reader, short charge, short ntt) throws PeptideParsingException {
		
		if(reader == null) {
			throw new NullPointerException("Reader is null.");
		}
		
		if(reader.getPeptideType() != PeptideType.SEQUEST) {
			throw new IllegalArgumentException("Currently only support SEQUEST.");
		}
		
		//This value is evaluated from human samples;
		int approximateNum = reader.getNumberofPeptides();
		List<float[]> list = new ArrayList<float[]>(approximateNum);
			
		IPeptide peptide;
		while((peptide=reader.getPeptide())!=null){
			
			if(peptide.getCharge()==charge && peptide.getNumberofTerm() == ntt)
				this.add(list,(ISequestPeptide)peptide);
		}
		
		System.out.println("Total peptides: "+list.size());
		
		return list.toArray(new float[0][0]);
	}
	
	/**
	 * Peptides needed for further evaluation are inserted one by one.
	 * In default condition, five params are used, xcorr dcn sp rsp ion,
	 * and arranged using this order.
	 */
	protected final void add(List<float[]> list, ISequestPeptide peptide){
		float[] data = new float[7];
		
		data[0] = peptide.getXcorr();
		data[1] = peptide.getDeltaCn();
		data[2] = peptide.getSp();
		data[3] = peptide.getRsp();
		data[4] = (float) peptide.getAbsoluteDeltaMZppm();
		data[5] = peptide.getIonPercent();
		data[6] = peptide.isTP()? 1f : -1f;
		
		list.add(data);
	}

}
