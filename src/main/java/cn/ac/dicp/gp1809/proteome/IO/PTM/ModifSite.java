/* 
 ******************************************************************************
 * File: ModifSite.java * * * Created on 02-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * The modification site after database search on a peptide.
 * 
 * @author Xinning
 * @version 0.1.1, 05-19-2009, 16:14:13
 */
public class ModifSite implements IModifSite, Comparable<ModifSite>, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4504387395334393792L;

	private int loc;

	private char symbol;
	
	private ModSite site;

	/**
	 * A modification site
	 * 
	 * @param aa
	 *            the aminoacid
	 * @param loc
	 *            the localization (from 1- n)
	 * @param symbol
	 *            The symbol of this modification site
	 */

	public ModifSite(ModSite site, int loc, char symbol){
		this.site = site;
		this.loc = loc;
		this.symbol = symbol;
	}
	/**
	 * The aminoacids
	 * 
	 * @return
	 */
	public ModSite modifiedAt() {
		return site;
	}

	/**
	 * Set the phosphorylated aminoaicd
	 * 
	 * @param aa
	 */
	public void setModifiedAt(ModSite site) {
		this.site = site;
	}

	/**
	 * The localization (from 1 - n)
	 * 
	 * @return
	 */
	public int modifLocation() {
		return loc;
	}

	/**
	 * The localization (from 1 - n)
	 * 
	 * @param loc
	 */
	public void setModifLocation(int loc) {
		this.loc = loc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite#symbol()
	 */
	@Override
	public char symbol() {
		return this.symbol;
	}

	/**
	 * The symbol of this modification site
	 * 
	 * @param symbol
	 */
	public void setSymbol(char symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('@').append(this.site.getModifAt()).append('[').append(this.loc).append(
		        "]: ").append(this.symbol);
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ModifSite clone() {
		try {
			return (ModifSite) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite#deepClone()
	 */
	@Override
	public ModifSite deepClone() {

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
			return (ModifSite) ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	
	}
/*	

	@Override
	public boolean equals(IModifSite site) {
		// TODO Auto-generated method stub
		if(this.loc != site.modifLocation())
			return false;
		
		if(this.site != site.modifiedAt())
			return false;
		
		if(this.symbol != site.symbol())
			return false;
		
		return true;
	}
*/	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ModifSite){
			ModifSite site = (ModifSite) obj;
			if(this.loc != site.modifLocation()){
				
				return false;
			}

			if(!this.site.equals(site.modifiedAt())){
				return false;
			}

			if(this.symbol != site.symbol()){
				return false;
			}
			
			return true;
		}else{
			
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ModifSite o) {
		// TODO Auto-generated method stub
		int i0 = this.loc;
		int i1 = o.loc;
		if(i0<i1)
			return -1;
		else if(i0>i1)
			return 1;
		else{
			return 0;
		}
	}
	
	public static void main(String [] args){

		
	}

}
