/* 
 ******************************************************************************
 * File: PagedTable.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * The table which can limit the number of rows in a page. Change the settings
 * in the row getter to refresh the records in the table. After change of the
 * row getter, remember to invoke the method {@link #refresh()}, to update the
 * contents.
 * 
 * @author Xinning
 * @version 0.1, 04-10-2009, 15:28:14
 */
public class PagedTable extends JTable implements IPageSelector {

	private static final long serialVersionUID = 1L;
	private IPagedTableRowGettor<?> rowGettor;
	private PageModel pagemodel;

	public PagedTable() {
		initComponents();
	}

	public PagedTable(IPagedTableRowGettor<?> rowGettor) {
		this.rowGettor = rowGettor;
		initComponents();
	}

	private void initComponents() {
		if (this.rowGettor != null) {
			pagemodel = new PageModel(this.rowGettor);
			this.setModel(pagemodel);

			int[] widths = this.rowGettor.getColumnWidths();

			if (widths != null && widths.length > 0) {
				TableColumnModel colmode = this.getColumnModel();
				int len = widths.length;
				for (int i = 0; i < len; i++) {
					TableColumn col = colmode.getColumn(i);
					col.setPreferredWidth(widths[i]);
				}
			}
		}
		
		this.getSelectionModel().setSelectionMode(
		        ListSelectionModel.SINGLE_SELECTION);

		setSize(606, 384);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.IPageSelector#selectPage(int)
	 */
	@Override
	public void selectPage(int pageIdx) {
		if (this.pagemodel != null) {
			this.pagemodel.selectPage(pageIdx);
			this.pagemodel.fireTableDataChanged();
		}
	}

	/**
	 * If the settings are changed in the row gettor, use this method to update
	 * the table. The page number will be changed to 1.
	 */
	public void refresh() {
		this.selectPage(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.IPageSelector#totalPages()
	 */
	@Override
	public int totalPages() {
		return this.rowGettor == null ? 1 : this.rowGettor.getNumberofPages();
	}

	/**
	 * Add list selection listener
	 * 
	 * @param listener
	 */
	public void addListSelectionListener(ListSelectionListener listener) {
		this.getSelectionModel().addListSelectionListener(listener);
	}
	
	public void addTableModelListener(TableModelListener listener) {
//		this.add
	}

	/**
	 * Remove list selection listener
	 * 
	 * @param listener
	 */
	public void removeListSelectionListener(ListSelectionListener listener) {
		this.getSelectionModel().removeListSelectionListener(listener);
	}

	/**
	 * The TableModel
	 * 
	 * @author Xinning
	 * @version 0.1, 04-10-2009, 16:02:35
	 */
	private class PageModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private IPagedTableRowGettor<?> rowGettor;
		private boolean[] editable;
		private Class<?>[] columnClasses;
		private String[] names;

		private int curtPage;
		private ITableRowObject[] rows = new ITableRowObject[0];

		public PageModel(IPagedTableRowGettor<?> rowGettor) {
			this(rowGettor, 0);
		}

		public PageModel(IPagedTableRowGettor<?> rowGettor, int pageIdx) {
			this.rowGettor = rowGettor;
			this.initial();
			this.selectPage(pageIdx);
		}

		/**
		 * Initial the column information
		 * 
		 * @throws IllegalArgumentException
		 */
		private void initial() throws IllegalArgumentException {
			int colcount = this.rowGettor.getColumnCount();

			this.names = new String[colcount + 1];
			this.names[0] = "Index";
			String[] names = this.rowGettor.getColumnNames();
			if (names != null) {

				if (names.length != colcount)
					throw new IllegalArgumentException(
					        "The number of column names doesn't equal to the column count.");

				System.arraycopy(names, 0, this.names, 1, colcount);
			}

			this.editable = new boolean[colcount + 1];
			boolean[] editable = this.rowGettor.isColumnEditable();
			if (editable != null) {

				if (editable.length != colcount)
					throw new IllegalArgumentException(
					        "The number of column editable doesn't equal to the column count.");

				System.arraycopy(editable, 0, this.editable, 1, colcount);
			}

			this.columnClasses = new Class<?>[colcount + 1];
			this.columnClasses[0] = String.class;
			Class<?>[] columnClasses = this.rowGettor.getColumnClasses();
			if (columnClasses != null) {

				if (columnClasses.length != colcount)
					throw new IllegalArgumentException(
					        "The number of column classes doesn't equal to the column count.");

				System.arraycopy(columnClasses, 0, this.columnClasses, 1,
				        colcount);
			}
		}

		/**
		 * Select the records in the specific page and refresh the table
		 * 
		 * @param pageIdx
		 */
		public void selectPage(int pageIdx) {
			this.curtPage = pageIdx;
			this.rowGettor.setCurrentPage(pageIdx);

			/*
			 * Buffer the rows in current page
			 */

			int count = this.getRowCount();
			this.rows = new ITableRowObject[count];
			for (int i = 0; i < count; i++) {
				this.rows[i] = this.rowGettor.getRow(i);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return this.rowGettor.getColumnCount() + 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return this.rowGettor.getRowCountCurtPage();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0)
				return this.curtPage * this.rowGettor.getMaxRecordsperPage()
				        + rowIndex + 1;

			return this.rows[rowIndex].getValueAt(columnIndex - 1);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int columnIndex) {
			return this.names[columnIndex];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return this.editable[columnIndex];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return this.columnClasses[columnIndex];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
		 * int, int)
		 */
		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			//The first column is the index column and can not be edit
			if (columnIndex >= 1) {
				this.rows[rowIndex].setValueAt(value, columnIndex - 1);
			}
		}
	}
}
