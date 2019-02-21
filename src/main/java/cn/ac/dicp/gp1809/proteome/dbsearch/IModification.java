/* 
 ******************************************************************************
 * File: IModification.java * * * Created on 10-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import java.util.HashSet;

import cn.ac.dicp.gp1809.lang.IDeepCloneable;

/**
 * The modification for database search
 * 
 * <p>
 * Changes:
 * <li>0.2.1, 03-23-2009: Change the site container as HashSet; add method
 * {@link #merge(IModification)}
 * <li>0.2.2, 06-13-2009: implemented IDeepCloneable
 * @author Xinning
 * @version 0.2.2, 06-13-2009, 20:46:10
 */
public interface IModification extends IDeepCloneable, java.io.Serializable{
	/**
	 * The name describe the modification
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * The added Mono isotope mass
	 * 
	 * @return the addedMass
	 */
	public double getAddedMonoMass();

	/**
	 * The added average mass of this modification
	 * 
	 * @return the addedAvgMass
	 */
	public double getAddedAvgMass();

	/**
	 * Which aminoacid the modification will appear.
	 * 
	 * @return the modifiedAt the one-length-characters of the aminoacids
	 */
	public HashSet<ModSite> getModifiedAt();

	/**
	 * Merge together with another modification. The two modifications must be
	 * with the same mass adding. Otherwise, IllegalArgumentException will be
	 * threw.
	 * 
	 * @since 0.2.1
	 * @param mod
	 */
	public void merge(IModification mod) throws IllegalArgumentException;
	
	public String toString();
	
	/**
	 * {@inheritDoc}}
	 */
	public IModification deepClone();

}