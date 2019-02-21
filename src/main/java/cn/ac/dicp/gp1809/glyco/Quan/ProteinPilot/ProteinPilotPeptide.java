/* 
 ******************************************************************************
 * File: ProteinPilotPeptide.java * * * Created on 2013-6-6
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.ProteinPilot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ck
 *
 * @version 2013-6-6, 15:12:17
 */
public class ProteinPilotPeptide {
	
	private String accession;
	private String name;
	private double conf;
	private String sequence;
	private HashMap <String, ArrayList <String>> modmap;
	private double precMw;
	private double precMz;
	private double theorMw;
	private double theorMz;
	private double peptideBackboneMw;
	private int charge;
	private String scannum;
	private double rt;
	private int id;
	
	/**
	 * @param accession
	 * @param conf
	 * @param sequence
	 * @param modmap
	 * @param precMw
	 * @param precMz
	 * @param theorMw
	 * @param theorMz
	 * @param charge
	 * @param scannum
	 * @param rt
	 */
	public ProteinPilotPeptide(String accession, String name, double conf, String sequence,
			HashMap<String, ArrayList<String>> modmap, double precMw,
			double precMz, double theorMw, double theorMz, int charge,
			String scannum, double rt) {

		this.accession = accession;
		this.name = name;
		this.conf = conf;
		this.sequence = sequence;
		this.modmap = modmap;
		this.precMw = precMw;
		this.precMz = precMz;
		this.theorMw = theorMw;
		this.theorMz = theorMz;
		this.charge = charge;
		this.scannum = scannum;
		this.rt = rt;
	}

	
	
	/**
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}



	/**
	 * @param accession the accession to set
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the conf
	 */
	public double getConf() {
		return conf;
	}



	/**
	 * @param conf the conf to set
	 */
	public void setConf(double conf) {
		this.conf = conf;
	}



	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}



	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}



	/**
	 * @return the modmap
	 */
	public HashMap<String, ArrayList<String>> getModmap() {
		return modmap;
	}



	/**
	 * @param modmap the modmap to set
	 */
	public void setModmap(HashMap<String, ArrayList<String>> modmap) {
		this.modmap = modmap;
	}



	/**
	 * @return the precMw
	 */
	public double getPrecMw() {
		return precMw;
	}



	/**
	 * @param precMw the precMw to set
	 */
	public void setPrecMw(double precMw) {
		this.precMw = precMw;
	}



	/**
	 * @return the precMz
	 */
	public double getPrecMz() {
		return precMz;
	}



	/**
	 * @param precMz the precMz to set
	 */
	public void setPrecMz(double precMz) {
		this.precMz = precMz;
	}



	/**
	 * @return the theorMw
	 */
	public double getTheorMw() {
		return theorMw;
	}



	/**
	 * @param theorMw the theorMw to set
	 */
	public void setTheorMw(double theorMw) {
		this.theorMw = theorMw;
	}



	/**
	 * @return the theorMz
	 */
	public double getTheorMz() {
		return theorMz;
	}



	/**
	 * @param theorMz the theorMz to set
	 */
	public void setTheorMz(double theorMz) {
		this.theorMz = theorMz;
	}



	/**
	 * @return the charge
	 */
	public int getCharge() {
		return charge;
	}



	/**
	 * @param charge the charge to set
	 */
	public void setCharge(int charge) {
		this.charge = charge;
	}



	/**
	 * @return the scannum
	 */
	public String getScannum() {
		return scannum;
	}



	/**
	 * @param scannum the scannum to set
	 */
	public void setScannum(String scannum) {
		this.scannum = scannum;
	}



	/**
	 * @return the rt
	 */
	public double getRt() {
		return rt;
	}



	/**
	 * @param rt the rt to set
	 */
	public void setRt(double rt) {
		this.rt = rt;
	}



	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}



	/**
	 * @return the peptideBackboneMw
	 */
	public double getPeptideBackboneMw() {
		return peptideBackboneMw;
	}



	/**
	 * @param peptideBackboneMw the peptideBackboneMw to set
	 */
	public void setPeptideBackboneMw(double peptideBackboneMw) {
		this.peptideBackboneMw = peptideBackboneMw;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
