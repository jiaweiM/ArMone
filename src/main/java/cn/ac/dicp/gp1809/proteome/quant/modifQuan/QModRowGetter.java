/* 
 ******************************************************************************
 * File:QModRowGetter.java * * * Created on 2010-7-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import java.util.ArrayList;
import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.ModInfoObject;
import cn.ac.dicp.gp1809.proteome.quant.rsc.AbstractPairXMLReader;
import cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor;

/**
 * @author ck
 *
 * @version 2010-7-21,21:26:23
 */
public class QModRowGetter extends AbstractPagedRowGettor <ModInfoObject>{

	private AbstractPairXMLReader reader;
	private ModInfo [] mods;
	
	private String [] titles;
	private int[] indexes;
	private boolean[] selects;
	private Class<?>[] classes;
	private boolean editable[];
	
	private int startIdxCurtPage;
//	private final int[] backIndexes;

	public QModRowGetter(AbstractPairXMLReader reader) throws Exception{
		this(reader, null);
	}
	
	public QModRowGetter(AbstractPairXMLReader reader, int[] usedIndxes) throws Exception{
		this.reader = reader;
		this.mods = reader.getMods();
		int num = mods.length;

		if (usedIndxes != null)
			this.indexes = usedIndxes;
		else {			
			this.indexes = new int[num];
			for (int i = 0; i < num; i++) {
				this.indexes[i] = i;
			}
		}

		this.selects = new boolean[num];
		Arrays.fill(this.selects, true);

		this.classes = new Class<?>[this.getColumnCount()];
		this.classes[0] = Boolean.class;
		Arrays.fill(this.classes, 1, this.getColumnCount(), String.class);

		this.editable = new boolean[this.getColumnCount()];
		this.editable[0] = true;
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

	@Override
	public Class<?>[] getColumnClasses() {
		return this.classes;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return getColumnNames().length;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getColumnNames()
	 */
	@Override
	public String[] getColumnNames() {
		// TODO Auto-generated method stub
		String [] title = new String [] {"Selected","Name","Mass","Symbol","ModSite"};
		return title;
	}

	@Override
	public boolean[] isColumnEditable() {
		return this.editable;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getRow(int)
	 */
	@Override
	public ModInfoObject getRow(int idx) {
		// TODO Auto-generated method stub
		int index = this.indexes[idx + this.startIdxCurtPage];
		ModInfo info = mods[index];
		return new ModInfoObject(info, index, selects);
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
	public ModInfoObject[] getRows(int[] idxs) {
		// TODO Auto-generated method stub
		ModInfoObject [] objs = new ModInfoObject[idxs.length];
		for(int i=0;i<objs.length;i++){
			objs[i] = this.getRow(idxs[i]);
		}
		return objs;
	}

	public ModInfo [] getMods(){
		int [] indexs = getUsedList();
		ModInfo [] modlist = new ModInfo[indexs.length];
		for(int i=0;i<indexs.length;i++){
			modlist[i] = this.mods[indexs[i]];
		}
		return modlist;
	}
	
	public int [] getUsedList(){
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
	
	public Object [][] getObject4Table(){
		
		int size = this.getRowCount();
		Object [][] ss = new Object [size][];
		for(int i=0;i<size;i++){
			ModInfoObject obj = this.getRow(i);
			ss[i] = new Object [5];
			ss[i][0] = Boolean.FALSE;
			ss[i][1] = obj.getName()==null ? "" : obj.getName();
			ss[i][2] = obj.getMass();
			ss[i][3] = obj.getSymbol();
			ss[i][4] = obj.getModSite();
		}
		return ss;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
}
