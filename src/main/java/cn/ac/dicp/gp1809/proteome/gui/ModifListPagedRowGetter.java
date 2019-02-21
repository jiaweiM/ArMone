/* 
 ******************************************************************************
 * File:ModifListPagedRowGetter.java * * * Created on 2009-12-16
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.io.FileNotFoundException;
import java.util.Arrays;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotModAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.IModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor;

/**
 * @author ck
 *
 * @version 2009-12-16, 19:58:08
 */
public class ModifListPagedRowGetter extends AbstractPagedRowGettor <ModifRowObject> {
	
	private MascotModAccesser accesser;
	private String[] titles;
	private Class<?>[] classes;
	private boolean editable[];
	private int[] index;
	private boolean[] select;
	private int startIdxCurtPage;
	
	public ModifListPagedRowGetter(String file, Boolean isRelative) throws FileNotFoundException, ModsReadingException, XMLStreamException{
		this(new MascotModAccesser(file, isRelative));
	}
	
	/**
	 * @param mascotModAccesser
	 */
	public ModifListPagedRowGetter(MascotModAccesser accesser) {
		this(accesser,null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param accesser2
	 * @param object
	 */
	public ModifListPagedRowGetter(MascotModAccesser accesser, int[] usedModIndex) {
		this(accesser, usedModIndex, null);
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param accesser2
	 * @param usedModIndex
	 * @param object
	 */
	public ModifListPagedRowGetter(MascotModAccesser accesser,
			int[] usedModIndex, boolean selected[]) {
		this.accesser = accesser;
		this.titles = accesser.getTitle();
		
		if (usedModIndex != null)
			this.index = usedModIndex;
		else {
			int num = accesser.getNumberofMods();
			this.index = new int[num];

			for (int i = 0; i < num; i++) {
				this.index[i] = i;
			}
		}
		
		if (selected == null) {
			this.select = new boolean[this.accesser.getNumberofMods()];
			Arrays.fill(this.select, true);
		} else {
			if (selected.length != this.accesser.getNumberofMods()) {
				throw new IllegalArgumentException(
				        "The length of the selects doesn't equal to the number "
				                + "of modifications in the modification list file");
			}

			this.select = selected;
		}
		
		this.classes = new Class<?>[this.getColumnCount()];
		this.classes[0] = Boolean.class;
		Arrays.fill(this.classes, 1, this.getColumnCount(), String.class);

		this.editable = new boolean[this.getColumnCount()];
		this.editable[0] = true;
		// TODO Auto-generated constructor stub
	}
	
	public ModifListPagedRowGetter(ModifListPagedRowGetter getter,
	        int[] usedPepIndxes, boolean selected[]) {
		this(getter.accesser, usedPepIndxes, selected);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor#move2Page(int)
	 */
	@Override
	protected int move2Page(int pageIdx) {
		// TODO Auto-generated method stub
		int max = this.getMaxRecordsperPage();
		this.startIdxCurtPage = pageIdx * max;
		int end = (pageIdx + 1) * max;

		if (end > this.getRowCount())
			end = this.getRowCount();

		return end - this.startIdxCurtPage;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return this.titles.length;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getColumnNames()
	 */
	@Override
	public String[] getColumnNames() {
		// TODO Auto-generated method stub
		return this.titles;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getRow(int)
	 */
	@Override
	public ModifRowObject getRow(int idx) {
		// TODO Auto-generated method stub
		int index = this.index[idx + this.startIdxCurtPage];
		IModification mod = this.accesser.getModification(index);
		return new ModifRowObject(mod, index, this.select);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getRowCount()
	 */
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return this.index.length;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getRows(int[])
	 */
	@Override
	public ModifRowObject[] getRows(int[] idxs) {
		// TODO Auto-generated method stub
		ModifRowObject [] objs = new ModifRowObject[idxs.length];

		for (int i = 0; i < idxs.length; i++) {
			objs[i] = this.getRow(idxs[i]);
		}

		return objs;
	}

	@Override
	public boolean[] isColumnEditable() {
		return this.editable;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
}
