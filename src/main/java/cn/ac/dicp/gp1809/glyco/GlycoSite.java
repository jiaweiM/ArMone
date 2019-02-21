/* 
 ******************************************************************************
 * File: GlycoSite.java * * * Created on 2011-6-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.proteome.IO.PTM.ModifSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * @author ck
 *
 * @version 2011-6-22, 19:51:04
 */
public class GlycoSite extends ModifSite {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8718228157374290817L;

	private double modMass;

	private double glycoMass;
	
	private GlycoTree [] trees;
	
	/**
	 * @param site
	 * @param loc
	 * @param symbol
	 */
	public GlycoSite(ModSite site, int loc, char symbol) {
		super(site, loc, symbol);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param site
	 * @param loc
	 * @param symbol
	 */
	public GlycoSite(ModSite site, int loc, char symbol, double modMass) {
		super(site, loc, symbol);
		// TODO Auto-generated constructor stub
		this.modMass = modMass;
	}

	public void setGlycoStructure(double glycoMass, GlycoTree [] trees){
		this.glycoMass = glycoMass;
		this.trees = trees;
	}

	public double getGlycoMass(){
		return this.glycoMass;
	}
	
	public GlycoTree [] getGlycoStructure(){
		return this.trees;
	}
	
	public void setModMass(double modMass){
		this.modMass = modMass;
	}
	
	public double getModMass(){
		return this.modMass;
	}

	public GlycoSite deepClone(){

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			oos.writeObject(this);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			return (GlycoSite) ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
