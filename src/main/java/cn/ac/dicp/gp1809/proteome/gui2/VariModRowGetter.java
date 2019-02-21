/* 
 ******************************************************************************
 * File: VariModRowGetter.java * * * Created on 2011-9-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.ModInfoObject;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor;

/**
 * @author ck
 *
 * @version 2011-9-1, 19:03:00
 */
public class VariModRowGetter extends AbstractPagedRowGettor <ModInfoObject>{

	private ModInfo[] mods;
	
	private int[] indexes;
	private boolean[] selects;
	private Class<?>[] classes;
	private boolean editable[];
	
	private int startIdxCurtPage;
//	private final int[] backIndexes;

	public VariModRowGetter(AminoacidModification aaf) {
		this(aaf, null);
	}
	
	public VariModRowGetter(AminoacidModification aaf, int[] usedIndxes) {

		Modif [] modifs = aaf.getModifications();
		ModInfo nglycan = null;
		
		
		ArrayList <ModInfo> modlist = new ArrayList <ModInfo>();
		for(int i=0;i<modifs.length;i++){
			
			boolean containN = false;
			String name = modifs[i].getName();
			char symbol = modifs[i].getSymbol();
			double mass = modifs[i].getMass();

			HashSet <ModSite> modset = aaf.getModifSites(symbol);
			if(modset!=null){
				Iterator <ModSite> it = modset.iterator();
				while(it.hasNext()){
					ModSite ms  = it.next();
					ModInfo info = new ModInfo(name, mass, symbol, ms);
					modlist.add(info);
					if(ms.getModifAt().equals("N")){
						containN = true;
					}
				}
			}

			if(Math.abs(mass-0.984)<0.001 && containN){
				nglycan = new ModInfo("N-Glyco", 0.984016, symbol, ModSite.newInstance_aa('N'));
			}
		}
		
		if(nglycan!=null){
			modlist.add(nglycan);
		}
		this.mods = modlist.toArray(new ModInfo[modlist.size()]);
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

		this.classes = new Class<?>[this.getColumnCount()];
		this.classes[0] = Boolean.class;
		Arrays.fill(this.classes, 1, this.getColumnCount(), String.class);

		this.editable = new boolean[this.getColumnCount()];
		this.editable[0] = true;
	}
	
	public VariModRowGetter(ModInfo [] mods) {
		this(mods, null);
	}

	public VariModRowGetter(ModInfo [] mods, int[] usedIndxes) {
		
		this.mods = mods;
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
	
	public static String [] getTitle(){
		return new String [] {"Selected","Name","Mass","Symbol","ModSite"};
	}

	public Object [][] getString4Table(){
		int size = this.getRowCount();
		Object [][] ss = new Object [size][];
		for(int i=0;i<size;i++){
			ModInfoObject obj = this.getRow(i);
			ss[i] = new String [4];
			ss[i][0] = obj.getName();
			ss[i][1] = String.valueOf(obj.getMass());
			ss[i][2] = String.valueOf(obj.getSymbol());
			ss[i][3] = obj.getModSite().toString();
		}
		return ss;
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
	
	public Object [][] getObject4InfoPanel2(){
		
		int size = this.getRowCount();
		Object [][] ss = new Object [size][];
		for(int i=0;i<size;i++){
			ModInfoObject obj = this.getRow(i);
			ss[i] = new Object [5];
			ss[i][0] = Boolean.TRUE;
			ss[i][1] = obj.getName()==null ? "" : obj.getName();
			ss[i][2] = obj.getMass();
			ss[i][3] = obj.getSymbol();
			ss[i][4] = obj.getModSite();
		}
		return ss;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException {
		// TODO Auto-generated method stub

		PeptideListReader reader = new PeptideListReader("H:\\other_search_enginer\\OMSSA\\" +
				"test.omx.ppl");
		
		ISearchParameter para = reader.getSearchParameter();
		
		System.out.println(para.getVariableInfo().getModifSites('#'));
		
	}

	

}
