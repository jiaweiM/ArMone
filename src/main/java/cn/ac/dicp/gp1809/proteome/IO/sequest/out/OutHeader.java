/* 
 ******************************************************************************
 * File: OutHeader.java * * * Created on 04-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.out;

import cn.ac.dicp.gp1809.proteome.util.SequestScanName;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Header of the out files
 * 
 * <p>
 * Changes:
 * <li>, 06-29-2009: add {@link #getVersion()}, {@link #getRevision()}
 * 
 * @author Xinning
 * @version 0.2.1, 06-29-2009, 21:39:17
 */
public class OutHeader {
	
	/**
	 *  PlatForm dependent turn for file writing.
	 */
	private static final String lineSeparator = IOConstant.lineSeparator;
	
	/*
	 * Default license
	 */
	private static final String Licence = "Molecular Biotechnology, Univ. of Washington, " +
			"J.Eng/S.Morgan/J.Yates"+lineSeparator+" Licensed to ThermoFinnigan Corp.";
	
	private static final String SequestInfo = "TurboSEQUEST v.27 (rev. 12), (c) 1998-2005";
	
	private static final String CODE = "101040";
	
	private static final String Ion_Serise = "0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0";
	
	/*
	 * The default description of hit entries.
	 */
	private static final String Hit_description = "  #   Rank/Sp      Id#     (M+H)+   deltCn   XCorr" +
			"     Sp    Ions   Reference             Peptide";
	
	/*
	 * Sub line of each descriptions
	 */
	private static final String Hit_lines = " ---  --------  --------  ---------  ------  ------" +
			"   -----  -----  ---------             -------";
	
	//The filename contained in out file at the first line
	private SequestScanName scanName;
	
	
	private double mh;
	private float pep_tolerance;
	private float frag_tolerance;
	private boolean pep_Mono;
	private boolean frag_Mono;
	private float tic;
	private float lowest_sp;
	private float used_time ;//Time used for search of this dta
	private int match_pep_count;
	private int aa_count;
	private int pro_in_db;

	/*
	 * The peptides with top n score(Xcorr) will be print to .out file
	 * If the peptide number which have been matched to this spectrum is 
	 * with less number, the number changed to the total number.
	 * For example, if the maximum output peptides is 10, but there are
	 * only 5 peptides matched to the spectrum, then this value
	 * changed to 5;
	 */
	private int display_top_n_pep ;
	//The full name of protein identified by top n peptides will be print 
	//to .out file at then end
	private int display_top_n_pro ;
	
	//The value in header of ion %
	private float ion_percent;
	
	private String licence = Licence;
	private String sequestInfo = SequestInfo;
	/**
	 * The version of sequest
	 */
	private int version;
	/**
	 * The revision of sequest
	 * 
	 */
	private int revision;
	private String computer;
	private String date;
	
	private String fasta_db;
	private String index_db;
	
	private String ion_serises = Ion_Serise;
	
	private String modification = "";
	
	private String enzyme;
	
	/*
	 * CODE = 101040
	 */
	private String code = CODE;
	
	private String hit_description = Hit_description;
	
	private String hit_lines = Hit_lines;

	/**
     * @return the scanName
     */
    public final SequestScanName getScanName() {
    	return scanName;
    }
	/**
     * @param scanName the scanName to set
     */
    public final void setScanName(SequestScanName scanName) {
    	this.scanName = scanName;
    }
	/**
     * @return the mh
     */
    public double getMh() {
    	return mh;
    }
	/**
     * @param mh the mh to set
     */
    public void setMh(double mh) {
    	this.mh = mh;
    }
	/**
     * @return the tic
     */
    public float getTic() {
    	return tic;
    }
	/**
     * @param tic the tic to set
     */
    public void setTic(float tic) {
    	this.tic = tic;
    }
	/**
     * @return the lowest_sp
     */
    public float getLowest_sp() {
    	return lowest_sp;
    }
	/**
     * @param lowest_sp the lowest_sp to set
     */
    public void setLowest_sp(float lowest_sp) {
    	this.lowest_sp = lowest_sp;
    }
	/**
     * @return the used_time
     */
    public float getUsed_time() {
    	return used_time;
    }
	/**
     * @param used_time the used_time to set
     */
    public void setUsed_time(float used_time) {
    	this.used_time = used_time;
    }
	/**
     * @return the match_pep_count
     */
    public int getMatch_pep_count() {
    	return match_pep_count;
    }
	/**
     * @param match_pep_count the match_pep_count to set
     */
    public void setMatch_pep_count(int match_pep_count) {
    	this.match_pep_count = match_pep_count;
    }
	/**
     * @return the aa_count amino acid count in out file
     */
    public int getAa_count() {
    	return aa_count;
    }
	/**
	 * Aminoacid count in out file
	 * 
     * @param aa_count the aa_count to set
     */
    public void setAa_count(int aa_count) {
    	this.aa_count = aa_count;
    }
	/**
     * @return the pro_in_db
     */
    public int getPro_in_db() {
    	return pro_in_db;
    }
	/**
     * @param pro_in_db the pro_in_db to set
     */
    public void setPro_in_db(int pro_in_db) {
    	this.pro_in_db = pro_in_db;
    }
	/**
     * @return the display_pep_count
     */
    public int getDisplay_top_n_pep() {
    	return display_top_n_pep;
    }
	/**
     * @param display_pep_count the display_pep_count to set
     */
    public void setDisplay_top_n_pep(int display_top_n_pep) {
    	this.display_top_n_pep = display_top_n_pep;
    }
	/**
     * @return the computer
     */
    public String getComputer() {
    	return computer;
    }
	/**
     * @param computer the computer to set
     */
    public void setComputer(String computer) {
    	this.computer = computer;
    }
	/**
     * @return the date
     */
    public String getDate() {
    	return date;
    }
	/**
     * @param date the date to set
     */
    public void setDate(String date) {
    	this.date = date;
    }
	/**
	 * The full name of proteins identified by top n peptides will be
	 * print to the .out file
	 * 
     * @return the display_top_n_pro
     */
    public int getDisplay_top_n_pro() {
    	return display_top_n_pro;
    }
	/**
	 * The full name of proteins identified by top n peptides will be
	 * print to the .out file
	 * 
     * @param display_top_n_pro the display_top_n_pro to set
     */
    public void setDisplay_top_n_pro(int display_top_n_pro) {
    	this.display_top_n_pro = display_top_n_pro;
    }
	/**
     * @return the licence
     */
    public String getLicence() {
    	return licence;
    }
	/**
     * @param licence the licence to set
     */
    public void setLicence(String licence) {
    	this.licence = licence;
    }
	/**
     * @return the pep_tolerance
     */
    public float getPep_tolerance() {
    	return pep_tolerance;
    }
	/**
     * @param pep_tolerance the pep_tolerance to set
     */
    public void setPep_tolerance(float pep_tolerance) {
    	this.pep_tolerance = pep_tolerance;
    }
	/**
     * @return the frag_tolerance
     */
    public float getFrag_tolerance() {
    	return frag_tolerance;
    }
	/**
     * @param frag_tolerance the frag_tolerance to set
     */
    public void setFrag_tolerance(float frag_tolerance) {
    	this.frag_tolerance = frag_tolerance;
    }
	/**
     * @return the pep_Mono
     */
    public boolean isPep_Mono() {
    	return pep_Mono;
    }
	/**
     * @param pep_Mono the pep_Mono to set
     */
    public void setPep_Mono(boolean pep_Mono) {
    	this.pep_Mono = pep_Mono;
    }
	/**
     * @return the frag_Mono
     */
    public boolean isFrag_Mono() {
    	return frag_Mono;
    }
	/**
     * @param frag_Mono the frag_Mono to set
     */
    public void setFrag_Mono(boolean frag_Mono) {
    	this.frag_Mono = frag_Mono;
    }
	/**
     * @return the fasta_db
     */
    public String getFasta_db() {
    	return fasta_db;
    }
	/**
     * @param fasta_db the fasta_db to set
     */
    public void setFasta_db(String fasta_db) {
    	this.fasta_db = fasta_db;
    }
	/**
	 * May be null if the database used for search is a fasta database.
	 * 
     * @return the index_db
     */
    public String getIndex_db() {
    	return index_db;
    }
	/**
	 * If the searched database is an indexed database, the original 
	 * database should also be indicated.
	 * 
     * @param index_db the index_db to set
     */
    public void setIndex_db(String index_db, String fasta_db) {
    	this.index_db = index_db;
    	this.fasta_db = fasta_db;
    }
	/**
	 * "TurboSEQUEST v.27 (rev. 12), (c) 1998-2005"
	 * 
     * @return the sequestInfo
     */
    public String getSequestInfo() {
    	return sequestInfo;
    }
    
    /**
     * The version of sequest (27, 28 or others).
     * <p>
     * 27--> .27
     * 28--> .28
     * 
     * @return
     */
    public int getVersion() {
    	return this.version;
    }
    
    /**
     * The revision of current sequest
     * 
     * @return
     */
    public int getRevision() {
    	return this.revision;
    }
    
	/**
	 * "TurboSEQUEST v.27 (rev. 12), (c) 1998-2005"
	 * 
     * @param sequestInfo the sequestInfo to set
     */
    public void setSequestInfo(String sequestInfo) {
    	this.sequestInfo = sequestInfo;
    	
    	int idx = sequestInfo.indexOf('.');
    	int idx2 = sequestInfo.indexOf('.', idx+3);
    	
    	this.version = Integer.parseInt(sequestInfo.substring(idx+1, idx+3));
    	this.revision = Integer.parseInt(sequestInfo.substring(idx2+2, idx2+3));
    }
	/**
     * @return the ion_serises
     */
    public String getIon_serises() {
    	return ion_serises;
    }
	/**
     * @param ion_serises the ion_serises to set
     */
    public void setIon_serises(String ion_serises) {
    	this.ion_serises = ion_serises;
    }
	/**
     * @return the ion_percent the ion % in header
     */
    public float getIon_percent() {
    	return ion_percent;
    }
	/**
	 * the ion % in header
	 * 
     * @param ion_percent the ion_percent to set
     */
    public void setIon_percent(float ion_percent) {
    	this.ion_percent = ion_percent;
    }
	/**
     * @return the code
     */
    public String getCode() {
    	return code;
    }
	/**
     * @param code the code to set
     */
    public void setCode(String code) {
    	this.code = code;
    }
	/**
     * @return the modification
     */
    public String getModification() {
    	return modification;
    }
	/**
     * @param modification the modification to set
     */
    public void setModification(String modification) {
    	this.modification = modification;
    }
	/**
     * @return the enzyme
     */
    public String getEnzyme() {
    	return enzyme;
    }
	/**
     * @param enzyme the enzyme to set
     */
    public void setEnzyme(String enzyme) {
    	this.enzyme = enzyme;
    }
	/**
     * @return the hit_description
     */
    public String getHit_description() {
    	return hit_description;
    }
	/**
     * @param hit_description the hit_description to set
     */
    public void setHit_description(String hit_description) {
    	this.hit_description = hit_description;
    }
	/**
     * @return the hit_lines
     */
    public String getHit_lines() {
    	return hit_lines;
    }
	/**
	 * Sub line under the hit description
	 * 
     * @param hit_lines the hit_lines to set
     */
    public void setHit_lines(String hit_lines) {
    	this.hit_lines = hit_lines;
    }
	/**
	 * 	The name of out file for creation of this OutFile instance. Need not to 
	 *  be set when this out file is read from a srf file.
	 * <p><b>Note: If this OutFile is created from srf file, this name
	 * will be the formatted out filename: "Basename.scanNumBeg.scanNumEnd.charge.out"</b>
	 * 
     * @return the filename
     */
    public String getFilename() {
		return this.scanName.getScanName();
    }

}
