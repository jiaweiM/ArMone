/* 
 ******************************************************************************
 * File: PeptideListInfo.java * * * Created on 04-11-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

/**
 * Container of information of peptide list file
 * 
 * @author Xinning
 * @version 0.1, 04-11-2009, 20:41:06
 */
public class PeptideListInfo {
	
	private int pep_total;
	private int pep1, pep2, pep3, pep3plus;
	private int target, decoy;
	private float fdr;
	/**
     * @return the pep_total
     */
    public int getPep_total() {
    	return pep_total;
    }
	/**
     * @param pepTotal the pep_total to set
     */
    public void setPep_total(int pepTotal) {
    	pep_total = pepTotal;
    }
	/**
     * @return the pep1
     */
    public int getPep1() {
    	return pep1;
    }
	/**
     * @param pep1 the pep1 to set
     */
    public void setPep1(int pep1) {
    	this.pep1 = pep1;
    }
	/**
     * @return the pep2
     */
    public int getPep2() {
    	return pep2;
    }
	/**
     * @param pep2 the pep2 to set
     */
    public void setPep2(int pep2) {
    	this.pep2 = pep2;
    }
	/**
     * @return the pep3
     */
    public int getPep3() {
    	return pep3;
    }
	/**
     * @param pep3 the pep3 to set
     */
    public void setPep3(int pep3) {
    	this.pep3 = pep3;
    }
	/**
     * @return the pep3plus
     */
    public int getPep3plus() {
    	return pep3plus;
    }
	/**
     * @param pep3plus the pep3plus to set
     */
    public void setPep3plus(int pep3plus) {
    	this.pep3plus = pep3plus;
    }
	/**
     * @return the target
     */
    public int getTarget() {
    	return target;
    }
	/**
     * @param target the target to set
     */
    public void setTarget(int target) {
    	this.target = target;
    }
	/**
     * @return the decoy
     */
    public int getDecoy() {
    	return decoy;
    }
	/**
     * @param decoy the decoy to set
     */
    public void setDecoy(int decoy) {
    	this.decoy = decoy;
    }
	/**
     * @return the fdr
     */
    public float getFdr() {
    	return fdr;
    }
	/**
     * @param fdr the fdr to set
     */
    public void setFdr(float fdr) {
    	this.fdr = fdr;
    }
}
