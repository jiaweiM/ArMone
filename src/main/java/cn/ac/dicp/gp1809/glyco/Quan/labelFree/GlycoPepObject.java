/* 
 ******************************************************************************
 * File: GlycoPepObject.java * * * Created on 2012-5-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject;

/**
 * @author ck
 *
 * @version 2012-5-24, 15:28:56
 */
public class GlycoPepObject implements ITableRowObject {
	
	private String [] columns;
	private boolean [] selected;
	private int index;
	private IGlycoPeptide pep;
	
	public GlycoPepObject(IGlycoPeptide pep, int index, boolean [] selected){
		
		this.columns = new String[11];
		this.index = index;
		this.selected = selected;
		this.pep = pep;
		NGlycoSSM ssm = pep.getDeleStructure();
		
		this.columns[1] = String.valueOf(pep.getScanNumBeg());
		this.columns[2] = pep.getSequence();
		this.columns[3] = pep.getDelegateReference();
		
		this.columns[4] = String.valueOf(ssm.getScanNum());
		this.columns[5] = ssm.getName();
		this.columns[6] = String.valueOf(ssm.getPepMass());
		this.columns[7] = String.valueOf(ssm.getGlycoMass());
		this.columns[8] = String.valueOf(ssm.getScore());
		this.columns[9] = String.valueOf(ssm.getRank());
		this.columns[10] = String.valueOf(ssm.getRT());
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
	
	public IGlycoPeptide getGlycoPeptide(){
		return pep;
	}
}
