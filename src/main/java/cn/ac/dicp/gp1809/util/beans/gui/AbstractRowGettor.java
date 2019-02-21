/* 
 ******************************************************************************
 * File: AbstractRowGettor.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

import java.util.Arrays;

/**
 * The abstract row getter
 * 
 * @author Xinning
 * @version 0.1, 04-10-2009, 18:13:13
 */
public abstract class AbstractRowGettor<Row extends ITableRowObject> implements
        ITableRowGettor<Row> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ITableRowGettor#getColumnWidths()
	 */
	@Override
	public int[] getColumnWidths() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getColumnClasses()
	 */
	@Override
	public Class<?>[] getColumnClasses() {
		Class<?>[] classes = new Class<?>[this.getColumnCount()];
		Arrays.fill(classes, Object.class);
		return classes;
	}

	/**
	 * If editable for each of the column. Default, can not be edit.
	 * 
	 * @return
	 */
	public boolean[] isColumnEditable() {
		return new boolean[this.getColumnCount()];
	}
}
