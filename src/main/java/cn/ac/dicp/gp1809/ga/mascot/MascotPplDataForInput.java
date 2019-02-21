/* 
 ******************************************************************************
 * File: MascotPplDataForInput.java * * * Created on 2011-9-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.mascot;

import java.util.ArrayList;
import java.util.List;

import cn.ac.dicp.gp1809.ga.sequest.IDataForInput;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * @author ck
 *
 * @version 2011-9-1, 10:02:03
 */
public class MascotPplDataForInput implements IDataForInput {

	
	public MascotPplDataForInput() {
	}
	
	
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.sequest.IDataForInput#getPeptide(short)
	 */
	@Override
	public float[][] getPeptide(IPeptideListReader reader, short charge) throws PeptideParsingException {
		
		if(reader == null) {
			throw new NullPointerException("Reader is null.");
		}

		//This value is evaluated from human samples;
		int approximateNum = reader.getNumberofPeptides();
		List<float[]> list = new ArrayList<float[]>(approximateNum);
			
		IPeptide peptide;
		while((peptide=reader.getPeptide())!=null){
			
			if(peptide.getCharge()==charge)
				this.add(list,(IMascotPeptide)peptide);
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

		//This value is evaluated from human samples;
		int approximateNum = reader.getNumberofPeptides();
		List<float[]> list = new ArrayList<float[]>(approximateNum);
			
		IPeptide peptide;
		while((peptide=reader.getPeptide())!=null){
			
			if(peptide.getCharge()>=charge)
				this.add(list,(IMascotPeptide)peptide);
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

		//This value is evaluated from human samples;
		int approximateNum = reader.getNumberofPeptides();
		List<float[]> list = new ArrayList<float[]>(approximateNum);
			
		IPeptide peptide;
		while((peptide=reader.getPeptide())!=null){
			
			if(peptide.getCharge()==charge && peptide.getNumberofTerm() == ntt)
				this.add(list,(IMascotPeptide)peptide);
		}
		
		System.out.println("Total peptides: "+list.size());
		
		return list.toArray(new float[0][0]);
	}
	
	/**
	 * Peptides needed for further evaluation are inserted one by one.
	 * In default condition, five params are used, xcorr dcn sp rsp ion,
	 * and arranged using this order.
	 */
	protected final void add(List<float[]> list, IMascotPeptide peptide){
		float[] data = new float[7];
		
		data[0] = peptide.getIonscore();
		data[1] = peptide.getDeltaS();
		data[2] = (peptide.getIonscore()-peptide.getHomoThres());
		data[3] = (peptide.getIonscore()-peptide.getIdenThres());
		data[4] = (float) peptide.getEvalue();
		data[5] = (float) peptide.getAbsoluteDeltaMZppm();
		data[6] = peptide.isTP()? 1f : -1f;
		
		list.add(data);
	}


}
