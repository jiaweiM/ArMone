/* 
 ******************************************************************************
 * File: PairPeptide.java * * * Created on 2011-7-3
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.profile;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author ck
 *
 * @version 2011-7-3, 16:32:27
 */
public class QuanPeptide extends AbstractPeptide implements IPeptide{
	
	private int feasId = -1;

	public QuanPeptide(String sequence, HashSet<ProteinReference> refs, String scanName, 
			HashMap <String, SeqLocAround> locAroundMap) {
		
		super(sequence, (short)0, refs, scanName, 0, 0, locAroundMap);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param sequence
	 * @param charge
	 * @param refs
	 * @param scanName
	 * @param pepLocMap
	 */
	public QuanPeptide(String sequence, short charge, HashSet<ProteinReference> refs, String scanName, 
			int scanBeg, int scanEnd, HashMap <String, SeqLocAround> locAroundMap) {
		
		super(sequence, charge, refs, scanName, scanBeg, scanEnd, locAroundMap);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		// TODO Auto-generated method stub
		return PeptideType.GENERIC;
	}

	/**
	 * @return the feas
	 */
	public int getFeasId() {
		return feasId;
	}

	/**
	 * @param feas the feas to set
	 */
	public void setFeasId(int feasId) {
		this.feasId = feasId;
	}
}
