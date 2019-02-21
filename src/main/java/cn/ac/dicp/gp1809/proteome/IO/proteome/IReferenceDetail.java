/* 
 ******************************************************************************
 * File: IReferenceDetail.java * * * Created on 08-28-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IReferenceDetailFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.ISimplifyable;

/**
 * The reference detail for a protein identification
 * 
 * @author Xinning
 * @version 0.2, 09-11-2008, 21:37:46
 */
public interface IReferenceDetail extends ISimplifyable, Cloneable {

	/**
	 * The reference detail format for this reference detail formatting
	 * 
	 * @return
	 */
	public IReferenceDetailFormat getReferenceFormat();

	/**
	 * @return the name of this reference;
	 */
	public String getName();

	/**
	 * Set the probability for this protein. <b>The probability must within 0
	 * and 1, if the value indicated is below 0 set as 0 instead. If the value
	 * is bigger than 1, set as 1 instead.</b>
	 * 
	 * @param value
	 */
	public void setProbability(float value);

	/**
	 * @return the probability of this protein. If the probablity is not
	 *         calculated, the returned value is -1d;
	 */
	public float getProbability();

	/**
	 * @return pi value of this protein
	 */
	public double getPI();

	/**
	 * @return If this is a target protein
	 */
	public boolean isTarget();

	/**
	 * @return mw of this protein
	 */
	public double getMW();

	/**
	 * The number of aminoacids in protein sequence
	 * 
	 * @return
	 */
	public int getNumAminoacids();

	/**
	 * The coverage of this protein reference
	 * 
	 * @return
	 */
	public float getCoverage();

	/**
	 * The coverage of this protein reference
	 * 
	 * @param coverage
	 */
	public void setCoverage(float coverage);

	/**
	 * The count of spectra for this protein identification
	 * 
	 * @return
	 */
	public int getSpectrumCount();

	/**
	 * The count of spectra for this protein identification
	 * 
	 * @param spectrumCount
	 */
	public void setSpectrumCount(int spectrumCount);

	/**
	 * The count of peptide for this protein identification
	 * 
	 * @return
	 */
	public int getPeptideCount();

	/**
	 * The count of peptide for this protein identification
	 * 
	 * @param peptideCount
	 *            unique peptide count
	 */
	public void setPeptideCount(int peptideCount);

	/**
	 * The index of this protein group in the protein group collection. If
	 * unassigned, return -1;
	 * 
	 * @return
	 */
	public int getGroupIndex();

	/**
	 * The index of this protein group in the protein group collection.
	 * 
	 * @param idx
	 *            index of the protein group in Proteins
	 */
	public void setGroupIndex(int idx);

	/**
	 * How many proteins are crossed with this protein. The crossed proteins
	 * have same peptides for their identifications, but also have unique
	 * distinct peptides for each of their identifications. If unassigned,
	 * return -1;
	 * 
	 * @return
	 */
	public int getCrossProtein();

	/**
	 * How many proteins are crossed with this protein. The crossed proteins
	 * have same peptides for their identifications, but also have unique
	 * distinct peptides for each of their identifications.
	 * 
	 * @param count
	 *            count of crossed protein
	 */
	public void setCrossProtein(int count);
	
	public void setSIn(double SIn);
	
	public double getSIn();

	/**
	 * This method is used for label quantitation. For different label type, calculate
	 * their SIn respectively.
	 * @param SIns
	 */
	public void setSIns(double[] SIns);
	
	public double [] getSIns();
	
	public String getSInStr();
	/**
	 * This ratio is the isotope label ratio used in label quantitation.
	 * @param Ratio
	 */
	public void setRatio(double Ratio);
	
	public double getRatio();
	
	public void setHyproScore(double hyproScore);
	
	public double getHyporScore();
	
	/**
	 * A shorter reference.
	 * @return
	 */
	public String getSubRef();
	
	public void setSubRef(String subRef);
}