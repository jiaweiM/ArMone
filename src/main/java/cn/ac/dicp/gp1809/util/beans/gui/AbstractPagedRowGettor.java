/* 
 ******************************************************************************
 * File: AbstractPagedRowGettor.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

/**
 * The abstract paged row gettor
 * 
 * @author Xinning
 * @version 0.1, 04-10-2009, 18:09:24
 */
public abstract class AbstractPagedRowGettor<Row extends ITableRowObject>
        extends AbstractRowGettor<Row> implements IPagedTableRowGettor<Row> {

	/**
	 * The number of record per page
	 */
	private int recordperpage = 50;

	/**
	 * The current page
	 */
	private int curtPage;

	/**
	 * The row count in current page
	 */
	private int curtRowCount;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.gui.IPagedTableRowGettor#getMaxRecordsperPage
	 * ()
	 */
	@Override
	public int getMaxRecordsperPage() {
		return recordperpage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.gui.IPagedTableRowGettor#getNumberofPages()
	 */
	@Override
	public int getNumberofPages() {

		int tcount = this.getRowCount();
		int num = tcount / this.recordperpage;

		if (tcount % this.recordperpage != 0)
			num++;

		return num;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.util.beans.gui.IPagedTableRowGettor#setCurrentPage(int)
	 */
	@Override
	public void setCurrentPage(int pageIdx) throws IndexOutOfBoundsException {

		int pages = this.getNumberofPages();
		//No record will not cause this exception
		if (pages > 0 && pageIdx >= this.getNumberofPages()) {
			throw new IndexOutOfBoundsException("Index: " + pageIdx);
		}

		this.curtPage = pageIdx;
		this.curtRowCount = this.move2Page(pageIdx);
	}

	/**
	 * Move the pointer to the page
	 * 
	 * @param pageIdx
	 * @return number of records in the specified page
	 */
	protected abstract int move2Page(int pageIdx);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.util.beans.gui.IPagedTableRowGettor#getCurrentPage()
	 */
	@Override
	public int getCurrentPage() {
		return this.curtPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.util.beans.gui.IPagedTableRowGettor#getRowCountCurtPage
	 * ()
	 */
	@Override
	public int getRowCountCurtPage() {
		return this.curtRowCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.gui.IPagedTableRowGettor#setMaxRecordsperPage
	 * (int)
	 */
	@Override
	public void setMaxRecordsperPage(int max) {
		if (max <= 0) {
			System.err.println("Invalid max record number: " + max
			        + ". Do nothing.");
		} else {
			this.recordperpage = max;
		}
	}
}
