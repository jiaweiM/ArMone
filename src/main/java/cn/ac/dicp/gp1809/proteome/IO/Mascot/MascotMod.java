/* 
 ******************************************************************************
 * File:MascotMod.java * * * Created on Nov 17, 2009
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.dbsearch.DefaultMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * @author ck
 *
 * @version Nov 17, 2009, 10:46:38 AM
 */
public class MascotMod extends DefaultMod implements IMascotMod{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int index;
	private String name;
	
	private boolean isNeutralloss;

	private double lossMonoMass;
	private double lossAvgMass;

	/**
	 * @param index
	 * @param name
	 * @param addedMonoMass
	 * @param addedAvgMass
	 * @param sites
	 */
	public MascotMod(int index, String name, double addedMonoMass,
			double addedAvgMass, HashSet<ModSite> sites) {
		super(name, addedMonoMass, addedAvgMass, sites);
		
		this.index = index;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.IMascotMod#getId()
	 */
	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return index;
	}
	
	
}
