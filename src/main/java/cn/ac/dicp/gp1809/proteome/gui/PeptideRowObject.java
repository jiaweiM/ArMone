/* 
 ******************************************************************************
 * File: PeptideRowObject.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject;

/**
 * The peptide object
 * 
 * @author Xinning
 * @version 0.1, 04-10-2009, 19:08:10
 */
public class PeptideRowObject implements ITableRowObject {

	private IPeptide peptide;

	private IMS2PeakList [] peaklists;

	private String[] cols;

	private boolean[] selected;
	private int index;

	/**
	 * 
	 * 
	 * @param peptide
	 * @param index
	 *            index in the peptide accesser
	 * @param selected
	 * @param cols
	 * 			columns
	 */
	public PeptideRowObject(IPeptide peptide, int index, boolean[] selected) {
		this.peptide = peptide;
		this.index = index;
		this.selected = selected;
		this.cols = StringUtil.split(peptide.toString(), '\t');
	}

	/**
	 * 
	 * @param peptide
	 * @param peaklists
	 * @param index
	 *            index in the peptide accesser
	 * @param selected
	 */
	public PeptideRowObject(IPeptide peptide, IMS2PeakList [] peaklists, int index,
	        boolean[] selected) {
		this.peptide = peptide;
		this.peaklists = peaklists;
		this.index = index;
		this.selected = selected;
		this.cols = StringUtil.split(peptide.toString(), '\t');
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ITableRowObject#getValueAt(int)
	 */
	@Override
	public Object getValueAt(int colIdx) {
		if (colIdx == 0) {
			return this.isSelected();
		}

		return cols[colIdx];
	}

	/**
	 * Get the peptide
	 * 
	 * @return
	 */
	public IPeptide getPeptide() {
		return this.peptide;
	}

	/**
	 * The peak lists for this peptide identification. May be null
	 * 
	 * @return
	 */
	public IMS2PeakList [] getPeakLists() {
		return this.peaklists;
	}

	/**
	 * Set this peptide is selected for use
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected[this.index] = selected;
	}

	/**
	 * If this peptide selected
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return this.selected[this.index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject#setValueAt(java.lang
	 * .Object, int)
	 */
	@Override
	public void setValueAt(Object obj, int colIdx) {
		if (colIdx == 0) {
			this.setSelected((Boolean) obj);
		}
	}
}
