/* 
 ******************************************************************************
 * File:AbstractGlycoPeptide.java * * * Created on 2010-10-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.peptide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;

/**
 * @author ck
 *
 * @version 2011-6-22, 15:49:48
 */
public class GlycoPeptide extends AbstractPeptide implements IGlycoPeptide{
	
	/**
	 * The mass of the peptide without the glycans and isotope labels
	 */
	private double pepMrNoGlyco;
	
	private double matchGlycoInten;
	
	/**
	 * The key is location and the value is glycoSite
	 */
	private HashMap <Integer, GlycoSite> glycoSiteMap;
	
	private HashMap <String, SimpleProInfo> proInfoMap;

	/**
	 * key = NGlycoSSM.getGlycanid()[0]+"_"+NGlycoSSM.getGlycanid()[1]; value = the HCD PSM information with different score rank
	 */
	private HashMap <String, ArrayList<NGlycoSSM>> hcdPsmInfoMap;
	
	private double [] glycoPercents;
	
	private NGlycoSSM ssm;
	
	private double deltaMass;
	
	private double deltaMassPPM;
	
	private double [] labelMasses;
	
	private int labelTypeId;
	
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
	public GlycoPeptide(String baseName, int scanNumBeg,
			int scanNumEnd, String sequence, short charge, double mh,
			double deltaMs, short rank, HashSet<ProteinReference> refs, float pi,
			short numofTerm, IPeptideFormat<?> formatter) {
		
		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs, rank,
				refs, pi, numofTerm, formatter);
		// TODO Auto-generated constructor stub
		
		this.glycoSiteMap = new HashMap <Integer, GlycoSite>();
		this.hcdPsmInfoMap = new HashMap <String, ArrayList<NGlycoSSM>>();
	}
	
	/**
	 * 
	 * @param sequence
	 * @param charge
	 * @param refs
	 * @param scanName
	 * @param scanBeg
	 * @param scanEnd
	 * @param pepLocMap
	 */
	public GlycoPeptide(String sequence, short charge, HashSet<ProteinReference> refs, String scanName, 
			int scanBeg, int scanEnd, HashMap <String, SeqLocAround> locAroundMap, double pepMrNoGlyco){
		
		super(sequence, charge, refs, scanName, scanBeg, scanEnd, locAroundMap);
		this.pepMrNoGlyco = pepMrNoGlyco;
		
		this.glycoSiteMap = new HashMap <Integer, GlycoSite>();
		this.hcdPsmInfoMap = new HashMap <String, ArrayList<NGlycoSSM>>();
	}
	
	public GlycoPeptide(IPeptide pep){
		super(pep);
		this.glycoSiteMap = new HashMap <Integer, GlycoSite>();
		this.hcdPsmInfoMap = new HashMap <String, ArrayList<NGlycoSSM>>();
	}
	
	public GlycoPeptide(IGlycoPeptide pep){
		
		super(pep);
		this.pepMrNoGlyco = pep.getPepMrNoGlyco();
		
		this.glycoSiteMap = new HashMap <Integer, GlycoSite>();
		
		Iterator <Integer> siteit = pep.getGlycoMap().keySet().iterator();
		while(siteit.hasNext()){
			Integer site = siteit.next();
			GlycoSite ns = pep.getGlycoMap().get(site).deepClone();
			this.glycoSiteMap.put(site, ns);
		}

		this.hcdPsmInfoMap = new HashMap <String, ArrayList<NGlycoSSM>>();
		this.hcdPsmInfoMap.putAll(pep.getHcdPsmInfoMap());
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		// TODO Auto-generated method stub
		return PeptideType.GENERIC;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getGlycoMr()
	 */
	@Override
	public double getGlycoMass() {
		// TODO Auto-generated method stub
		return this.getDeleStructure().getGlycoMass();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getPepMrNoGlyco()
	 */
	@Override
	public double getPepMrNoGlyco() {
		// TODO Auto-generated method stub
		return this.pepMrNoGlyco;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#setPepMrNoGlyco()
	 */
	@Override
	public void setPepMrNoGlyco(double pepMrNoGlyco) {
		// TODO Auto-generated method stub
		this.pepMrNoGlyco = pepMrNoGlyco;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getGlycoSiteNum()
	 */
	@Override
	public int getGlycoSiteNum() {
		// TODO Auto-generated method stub
		return this.glycoSiteMap.size();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#addGlycoSite()
	 */
	@Override
	public void addGlycoSite(GlycoSite glycoSite) {
		// TODO Auto-generated method stub
		int loc = glycoSite.modifLocation();
		this.glycoSiteMap.put(loc, glycoSite);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getGlycoSite(int)
	 */
	@Override
	public GlycoSite getGlycoSite(int loc) {
		// TODO Auto-generated method stub
		if(this.glycoSiteMap.containsKey(loc))
			return glycoSiteMap.get(loc);
		
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getAllGlycoSites()
	 */
	@Override
	public GlycoSite[] getAllGlycoSites() {
		// TODO Auto-generated method stub
		
		Integer [] locs = this.glycoSiteMap.keySet().toArray(new Integer[glycoSiteMap.size()]);
		GlycoSite [] sites = new GlycoSite [locs.length];
		Arrays.sort(locs);
		
		for(int i=0;i<locs.length;i++){
			sites[i] = this.getGlycoSite(locs[i]);
		}
		
		return sites;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getGlycoMap()
	 */
	@Override
	public HashMap<Integer, GlycoSite> getGlycoMap() {
		// TODO Auto-generated method stub
		return this.glycoSiteMap;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getGlycoPercent()
	 */
	@Override
	public double [] getGlycoPercents() {
		// TODO Auto-generated method stub
		return glycoPercents;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#setGlycoPercent(double)
	 */
	@Override
	public void setGlycoPercents(double [] glycoPercents) {
		// TODO Auto-generated method stub
		this.glycoPercents = glycoPercents;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getDeleForm()
	 */
	@Override
	public NGlycoSSM getDeleStructure() {
		// TODO Auto-generated method stub
		return ssm;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#setDeleStructure()
	 */
	@Override
	public void setDeleStructure(NGlycoSSM ssm) {
		// TODO Auto-generated method stub
		this.ssm = ssm;
	}
	
	public void setProInfoMap(HashMap<String, SimpleProInfo> proInfoMap){
		this.proInfoMap = proInfoMap;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getProInfoMap()
	 */
	@Override
	public HashMap<String, SimpleProInfo> getProInfoMap() {
		// TODO Auto-generated method stub
		return this.proInfoMap;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#addHcdPsmInfo(cn.ac.dicp.gp1809.glyco.spectrum.GlycoHcdPsm)
	 */
	@Override
	public void addHcdPsmInfo(NGlycoSSM ssm) {
		// TODO Auto-generated method stub
		
		int [] glycanId = ssm.getGlycanid();
		String key = glycanId[0]+"_"+glycanId[1];
		
		if(this.hcdPsmInfoMap.containsKey(key)){
			
			this.hcdPsmInfoMap.get(key).add(ssm);
			
		}else{
			
			ArrayList <NGlycoSSM> list = new ArrayList <NGlycoSSM>();
			list.add(ssm);
			this.hcdPsmInfoMap.put(key, list);	
		}
		
/*		if(ssm.getBestPepScannum()==this.getScanNumBeg()){
			if(this.ssm == null){
				
				this.ssm = ssm;
				this.deltaMass = Math.abs(ssm.getPepMass()-this.pepMrNoGlyco);
				this.deltaMassPPM = deltaMass/pepMrNoGlyco*1E6;
				
			}else{
				
				double deltaMass = Math.abs(ssm.getPepMass()-this.pepMrNoGlyco);
				if(deltaMass<this.deltaMass){
					this.ssm = ssm;
					this.deltaMass = deltaMass;
					this.deltaMassPPM = deltaMass/pepMrNoGlyco*1E6;
				}
			}
		}
*/		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getHcdPsmInfoMap()
	 */
	@Override
	public HashMap<String, ArrayList<NGlycoSSM>> getHcdPsmInfoMap() {
		// TODO Auto-generated method stub
		return this.hcdPsmInfoMap;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#setHcdPsmInfoMap(java.util.HashMap)
	 */
	@Override
	public void setHcdPsmInfoMap(HashMap<String, ArrayList<NGlycoSSM>> hcdPsmInfoMap) {
		// TODO Auto-generated method stub
		this.hcdPsmInfoMap.putAll(hcdPsmInfoMap);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getRtDiff()
	 */
	@Override
	public float getRtDiff() {
		// TODO Auto-generated method stub
		double pepRt = this.getRetentionTime();
		double glycanRt = this.ssm.getRT();
		float diff = (float) (pepRt - glycanRt);
		
		return diff;
	}
	
	public double getDeltaMass(){
		return this.deltaMass;
	}
	
	public double getDeltaMassPPM(){
		return this.deltaMassPPM;
	}

	/**
	 * @return the labelMasses
	 */
	public double[] getLabelMasses() {
		return labelMasses;
	}

	/**
	 * @param labelMasses the labelMasses to set
	 */
	public void setLabelMasses(double[] labelMasses) {
		this.labelMasses = labelMasses;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#setLabelType(int)
	 */
	@Override
	public void setLabelTypeID(int labelTypeId) {
		// TODO Auto-generated method stub
		this.labelTypeId = labelTypeId;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide#getLabelTypeID()
	 */
	@Override
	public int getLabelTypeID() {
		// TODO Auto-generated method stub
		return this.labelTypeId;
	}

	

}
