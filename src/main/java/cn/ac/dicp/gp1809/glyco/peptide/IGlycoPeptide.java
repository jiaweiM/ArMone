/* 
 ******************************************************************************
 * File:IGlycoPeptide.java * * * Created on 2010-10-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.peptide;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;

/**
 * @author ck
 *
 * @version 2011-6-22, 15:49:48
 */
public interface IGlycoPeptide extends IPeptide{
	
	/**
	 * 
	 * @return
	 */
	public double getPepMrNoGlyco();
	
	/**
	 * 
	 * @param pepMrNoGlyco
	 */
	public void setPepMrNoGlyco(double pepMrNoGlyco);
	
	/**
	 * 
	 * @return
	 */
	public double getGlycoMass();

	/**
	 * 
	 * @return
	 */
	public int getGlycoSiteNum();

	/**
	 * 
	 * @param glycoSiteNum
	 */
//	public void setGlycoSiteNum(int glycoSiteNum);
	
	/**
	 * The first rank glycan composition, which is the most likely composition
	 * @param form
	 */
//	public void setR1GlycoComp(NGlycoPossiForm form);
	
	/**
	 * Get the most likely composition
	 * @return
	 */
//	public NGlycoPossiForm getR1GlycoComp();
	
	/**
	 * 
	 * @param glycoSite
	 */
	public void addGlycoSite(GlycoSite glycoSite);

	/**
	 * 
	 * @param loc
	 * @return
	 */
	public GlycoSite getGlycoSite(int loc);
	
	/**
	 * 
	 * @return
	 */
	public GlycoSite [] getAllGlycoSites();

	/**
	 * 
	 * @param form
	 */
//	public void setDeleForm(GlycoForm form);
	
	/**
	 * 
	 * @return
	 */
	public NGlycoSSM getDeleStructure();
	
	/**
	 * 
	 */
	public void setDeleStructure(NGlycoSSM ssm);
	
	/**
	 * 
	 * @return
	 */
	public HashMap <Integer, GlycoSite> getGlycoMap();

	/**
	 * 
	 * @param psmInfo
	 */
	public void addHcdPsmInfo(NGlycoSSM psmInfo);
	
	/**
	 * 
	 * @param hcdPsmInfoMap
	 */
	public void setHcdPsmInfoMap(HashMap <String, ArrayList<NGlycoSSM>> hcdPsmInfoMap);
	
	/**
	 * 
	 * @return
	 */
	public HashMap <String, ArrayList<NGlycoSSM>> getHcdPsmInfoMap();
	
	/**
	 * One glyco site may be have different glyco compositions, the proportion is calculated by 
	 * the intensity of the features.
	 * @param percent
	 */
	public void setGlycoPercents(double [] glycoPercent);
	
	public double [] getGlycoPercents();
	
	/**
	 * 
	 * @return
	 */
	public HashMap <String, SimpleProInfo> getProInfoMap();
	
	public float getRtDiff();
	
	public double getDeltaMass();
	
	public double getDeltaMassPPM();
	
	public double[] getLabelMasses();
	
	public void setLabelMasses(double[] labelMasses);
	
	public void setLabelTypeID(int labelTypeId);
	
	public int getLabelTypeID();
	
}
