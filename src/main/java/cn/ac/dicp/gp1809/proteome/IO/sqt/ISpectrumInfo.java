/* 
 ******************************************************************************
 * File: ISpectrumInfo.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

/**
 * The spectrum information
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 20:53:30
 */
public interface ISpectrumInfo {
	
	/**
	 * The scan number begin
	 * 
	 * @return
	 */
	public int getScanNumBeg();
	
	/**
	 * The scan number end
	 * 
	 * @return
	 */
	public int getScanNumEnd();
	
	/**
	 * The charge state
	 * 
	 * @return
	 */
	public short getCharge();
	
	/**
	 * The time used for the search of this spectrum
	 * 
	 * @return
	 */
	public float getProcessTime();
	
	/**
	 * The name of the search pc
	 * 
	 * @return
	 */
	public String getSeverName();
	
	/**
	 * The actual (experimental) MH
	 * 
	 * @return
	 */
	public double getExperimentalMH();
	
	/**
	 * The total ion intensity. If not assigned, return -1.
	 * 
	 * @return
	 */
	public float getTic();
	
	/**
	 * The lowest sp 
	 * 
	 * @return
	 */
	public float getLowestSp();
	
	/**
	 * The number of matches.
	 * 
	 * @return
	 */
	public int getNumMatches();
}
