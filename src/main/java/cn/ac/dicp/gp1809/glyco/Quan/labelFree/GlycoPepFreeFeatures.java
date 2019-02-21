/* 
 ******************************************************************************
 * File: GlycoPepFreeFeatures.java * * * Created on 2012-10-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.GlycoPeptide;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;

/**
 * @author ck
 *
 * @version 2012-10-25, 15:17:07
 */
public class GlycoPepFreeFeatures extends FreeFeatures
{
	
	private GlycoSite[] sites;

	/**
	 * @param peptide
	 * @param freeFeatures
	 * @param labelfree
	 * @param array
	 */
	public GlycoPepFreeFeatures(GlycoPeptide peptide,
			FreeFeatures freeFeatures, LabelType labelfree, GlycoSite[] sites) {
		// TODO Auto-generated constructor stub
		super(freeFeatures);
		super.peptide = peptide;
		this.sites = sites;
	}

	/**
	 * @return
	 */
	public String getGlycanInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public String getSiteInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public String getPepInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	

}
