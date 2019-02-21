/* 
 ******************************************************************************
 * File: GlycoIdenObject.java * * * Created on 2012-5-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Iden;

import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject;

/**
 * @author ck
 *
 * @version 2012-5-16, 15:11:03
 */
public class GlycoIdenObject implements ITableRowObject {

	private String [] columns;
	private boolean [] selected;
	private int index;
	private NGlycoSSM ssm;

	public GlycoIdenObject(NGlycoSSM ssm, int index, boolean [] selected){
		
		this.columns = new String[10];
		this.index = index;
		this.selected = selected;
		this.ssm = ssm;
		
		this.columns[1] = String.valueOf(ssm.getScanNum());
		this.columns[2] = String.valueOf(ssm.getPreCharge());
		this.columns[3] = String.valueOf(ssm.getPreMz());
		this.columns[4] = String.valueOf(ssm.getRT());
		this.columns[5] = String.valueOf(ssm.getRank());
		
		this.columns[6] = ssm.getName();
		this.columns[7] = String.valueOf(ssm.getScore());
		this.columns[8] = String.valueOf(ssm.getGlycoMass());
		this.columns[9] = String.valueOf(ssm.getPepMass());
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

	public NGlycoSSM getGlycoMatch(){
		return ssm;
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
