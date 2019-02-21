/* 
 ******************************************************************************
 * File:ModifRowObject.java * * * Created on 2009-12-16
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import cn.ac.dicp.gp1809.proteome.dbsearch.IModification;
import cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject;

/**
 * @author ck
 *
 * @version 2009-12-16, 20:00:38
 */
public class ModifRowObject implements ITableRowObject {
	
	private IModification modification;
	private String [] columns;
	private boolean[] selected;
	private int index;
	
	ModifRowObject(IModification mod, int index, boolean [] selected){
		this.modification = mod;
		this.index = index;
		this.selected = selected;
		this.columns = mod.toString().split("\t");
	}

	public IModification getModification(){
		return this.modification;
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

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
