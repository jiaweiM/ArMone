/* 
 ******************************************************************************
 * File:ModDesObject.java * * * Created on 2010-7-19
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.IO;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject;

/**
 * @author ck
 *
 * @version 2010-7-19, 10:19:34
 */
public class ModInfoObject implements ITableRowObject {

	private String [] columns;
	private boolean[] selected;
	private int index;
	
	public ModInfoObject(ModInfo info, int index, boolean [] selected){
		this.columns = new String[5];
		this.columns[1] = info.getName();
		this.columns[2] = String.valueOf(info.getMass());
		this.columns[3] = String.valueOf(info.getSymbol());
		this.columns[4] = info.getModSite().getModifAt();
		this.index = index;
		this.selected = selected;
	}
	
	public ModInfoObject(String name, double mass, char symbol, ModSite site, int index, boolean [] selected){
		this.columns = new String[5];
		this.columns[1] = name;
		this.columns[2] = String.valueOf(mass);
		this.columns[3] = String.valueOf(symbol);
		this.columns[4] = site.getModifAt();
		this.index = index;
		this.selected = selected;
	}
	
	public String getName(){
		return columns[1];
	}
	
	public double getMass(){
		return Double.parseDouble(columns[2]);
	}
	
	public char getSymbol(){
		return columns[3].charAt(0);
	}
	
	public ModSite getModSite(){
//		return ModSite.parseSiteNew(columns[4]);
		return ModSite.parseSite(columns[4]);
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject#getValueAt(int)
	 */
	@Override
	public Object getValueAt(int colIdx) {
		// TODO Auto-generated method stub
		if (colIdx == 0) {
			return this.isSelected();
		}

		return columns[colIdx];
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject#setValueAt(java.lang.Object, int)
	 */
	@Override
	public void setValueAt(Object obj, int colIdx) {
		// TODO Auto-generated method stub
		if (colIdx == 0) {
			this.setSelected((Boolean) obj);
		}
	}

	public void setSelected(boolean selected) {
		this.selected[this.index] = selected;
	}

	public boolean isSelected() {
		return this.selected[this.index];
	}
	
	public String getInfo(){
		StringBuilder sb = new StringBuilder();
		sb.append(columns[1]).append("\t");
		sb.append("Mass = ").append(columns[2]).append("\t");
		sb.append("Symbol = ").append(columns[3]).append("\t");
		sb.append("ModSite = ").append(columns[4]).append("\t");
		return sb.toString();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i=1;i<columns.length;i++){
			sb.append(columns[i]).append("\t");
		}
		return sb.toString();
	}

}
