/* 
 ******************************************************************************
 * File: GlycoIdenPagedRowGetter.java * * * Created on 2012-5-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Iden;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor;

/**
 * @author ck
 *
 * @version 2012-5-16, 16:02:35
 */
public class GlycoIdenPagedRowGetter extends AbstractPagedRowGettor <GlycoIdenObject> {
	
	protected String [] titles;
	protected int [] indexes;
	
	protected boolean [] selects;
	protected Class<?>[] classes;
	protected boolean editable[];
	
	protected int startIdxCurtPage;
	protected int [] backIndexes;
	
	private GlycoIdenXMLReader reader;
	
	public GlycoIdenPagedRowGetter(GlycoIdenXMLReader reader){
		this(reader, null);
	}
	
	public GlycoIdenPagedRowGetter(GlycoIdenXMLReader reader, int[] usedIndxes){
		
		this.reader = reader;
		this.setTitle();
		int num = reader.getGlycanNum();
		
		if (usedIndxes != null)
			this.indexes = usedIndxes;
		else {			
			this.indexes = new int[num];
			for (int i = 0; i < num; i++) {
				this.indexes[i] = i;
			}
		}
		this.backIndexes = this.indexes;
		this.selects = new boolean[num];
		Arrays.fill(this.selects, true);

		this.classes = new Class<?>[this.getColumnCount()];
		this.classes[0] = Boolean.class;
		Arrays.fill(this.classes, 1, this.getColumnCount(), String.class);

		this.editable = new boolean[this.getColumnCount()];
		this.editable[0] = true;
	}

	public void setTitle(){
		
		titles = reader.getTitle();
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

	/**
	 * Select all the displayed pairs
	 * 
	 */
	public void selectAllDisplayedPairs() {
		for (int idx : this.indexes) {
			this.selects[idx] = true;
		}
	}

	/**
	 * DisSelect all the displayed pairs
	 * 
	 */
	public void disSelectAllDisplayedPairs() {
		for (int idx : this.indexes) {
			this.selects[idx] = false;
		}
	}
	
	@Override
	public Class<?>[] getColumnClasses() {
		return this.classes;
	}
	
	public boolean[] isColumnEditable() {
		return this.editable;
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
	public GlycoIdenObject getRow(int idx) {
		// TODO Auto-generated method stub
		
		int index = this.indexes[idx + this.startIdxCurtPage];
		NGlycoSSM ssm = this.reader.getGlycoMatch(index);
		GlycoIdenObject obj = new GlycoIdenObject(ssm, index, this.selects);
		
		return obj;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getRowCount()
	 */
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return this.indexes.length;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getRows(int[])
	 */
	@Override
	public GlycoIdenObject[] getRows(int[] idxs) {
		// TODO Auto-generated method stub
		
		GlycoIdenObject [] objs = new GlycoIdenObject[idxs.length];
		for(int i=0;i<objs.length;i++){
			objs[i] = this.getRow(idxs[i]);
		}
		
		return objs;
	}

	public NGlycoSSM [] getAllMatches(){
		return reader.getAllMatches();
	}
	
	public NGlycoSSM [] getAllSelectedMatches(){
		return reader.getAllSelectedMatches(getUsedList());
	}
	
	private int [] getUsedList(){
		
		ArrayList <Integer> idxlist = new ArrayList<Integer>();
		for(int i=0;i<indexes.length;i++){
			int idx = indexes[i];
			if(selects[idx])
				idxlist.add(idx);
		}
		int [] idxarray = new int[idxlist.size()];
		for(int i=0;i<idxarray.length;i++){
			idxarray[i] = idxlist.get(i);
		}
		Arrays.sort(idxarray);
		return idxarray;
	}
	
	/**
	 * @return
	 */
	public String getFileName() {
		// TODO Auto-generated method stub
		return reader.getFileName();
	}
	
	public File getParentFile(){
		return reader.getParentFile();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
}
