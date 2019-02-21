/* 
 ******************************************************************************
 * File: SequestVariableMod.java * * * Created on 11-20-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.dbsearch.DefaultMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.IVariableMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * The sequest variable modification
 * 
 * @author Xinning
 * @version 0.1.1, 03-23-2009, 21:55:58
 */
public class SequestVariableMod extends DefaultMod implements IVariableMod, java.io.Serializable  {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private char symbol;
	
	/**
	 * 
	 * 
     * @param name
     * @param addedMonoMass
     * @param addedAvgMass
     * @param modifiedAt
     * @param symbol the sequest predefined symbol for this variable modification
     */
    public SequestVariableMod(String name, double addedMonoMass,
            double addedAvgMass, HashSet<ModSite> modifiedAt, char symbol) {
	    super(name, addedMonoMass, addedAvgMass, modifiedAt);
	    this.symbol = symbol;
    }

	/**
     * @return the symbol
     */
    public char getSymbol() {
    	return symbol;
    }

	/**
     * @param symbol the symbol to set
     */
    public void setSymbol(char symbol) {
    	this.symbol = symbol;
    }

}
