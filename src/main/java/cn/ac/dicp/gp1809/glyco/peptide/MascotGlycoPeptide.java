/* 
 ******************************************************************************
 * File:MascotGlycoPeptide.java * * * Created on 2010-10-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.peptide;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;


/**
 * @author ck
 *
 */
public class MascotGlycoPeptide extends GlycoPeptide implements IGlycoPeptide, IMascotPeptide{

	/**
	 * @param baseName
	 * @param scanNumBeg
	 * @param scanNumEnd
	 * @param sequence
	 * @param charge
	 * @param mh
	 * @param deltaMs
	 * @param rank
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 * @param formatter
	 */
	protected MascotGlycoPeptide(String baseName, int scanNumBeg,
			int scanNumEnd, String sequence, short charge, double mh,
			double deltaMs, short rank, HashSet<ProteinReference> refs, float pi,
			short numofTerm, IPeptideFormat<?> formatter) {
		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs, rank,
				refs, pi, numofTerm, formatter);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getEvalue()
	 */
	@Override
	public double getEvalue() {
		// TODO Auto-generated method stub
		return this.getEvalue();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getHomoThres()
	 */
	@Override
	public float getHomoThres() {
		// TODO Auto-generated method stub
		return this.getHomoThres();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getIdenThres()
	 */
	@Override
	public float getIdenThres() {
		// TODO Auto-generated method stub
		return this.getIdenThres();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getIonscore()
	 */
	@Override
	public float getIonscore() {
		// TODO Auto-generated method stub
		return this.getIonscore();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getDeltaS()
	 */
	@Override
	public float getDeltaS() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#calEvalue(float)
	 */
	@Override
	public double calEvalue(float pvalue) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#calIdenThres(float)
	 */
	@Override
	public float calIdenThres(float pvalue) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getQueryIdenNum()
	 */
	@Override
	public int getQueryIdenNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#setHomoThres(float)
	 */
	@Override
	public void setHomoThres(float homoThres) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#setNumOfMatchedIons(int)
	 */
	@Override
	public void setNumOfMatchedIons(int numOfMatchedIons) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getNumOfMatchedIons()
	 */
	@Override
	public int getNumOfMatchedIons() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#setPeaksUsedFromIons1(int)
	 */
	@Override
	public void setPeaksUsedFromIons1(int peaksUsedFromIons1) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getPeaksUsedFromIons1()
	 */
	@Override
	public int getPeaksUsedFromIons1() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#setPeaksUsedFromIons2(int)
	 */
	@Override
	public void setPeaksUsedFromIons2(int peaksUsedFromIons2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getPeaksUsedFromIons2()
	 */
	@Override
	public int getPeaksUsedFromIons2() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#setPeaksUsedFromIons3(int)
	 */
	@Override
	public void setPeaksUsedFromIons3(int peaksUsedFromIons3) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide#getPeaksUsedFromIons3()
	 */
	@Override
	public int getPeaksUsedFromIons3() {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
