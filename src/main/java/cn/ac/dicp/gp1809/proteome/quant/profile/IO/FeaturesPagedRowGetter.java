/* 
 ******************************************************************************
 * File:FFeaturesPagedRowGetter.java * * * Created on 2010-6-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.profile.IO;

import cn.ac.dicp.gp1809.proteome.quant.gui.FeaturesObject;
import cn.ac.dicp.gp1809.proteome.quant.gui.RatioStatDataset;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author ck
 *
 * @version 2010-6-22, 14:57:37
 */
public class FeaturesPagedRowGetter extends AbstractPagedRowGettor <FeaturesObject> {

	protected AbstractFeaturesXMLReader reader;
	
	protected String [] titles;
	protected int [] indexes;
	protected LabelType type;
	protected boolean [] selects;
	protected Class<?>[] classes;
	protected boolean editable[];
	
	protected int startIdxCurtPage;
	protected int [] backIndexes;
	
	protected boolean glyco = false;

	public FeaturesPagedRowGetter(AbstractFeaturesXMLReader reader){
		this(reader, null);
	}
	
	public FeaturesPagedRowGetter(AbstractFeaturesXMLReader reader, int[] usedIndxes) {
		
		this.reader = reader;
		this.type = reader.getType();
		this.setTitle();
		int num = reader.getPairsNum();
		
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
	public void selectAllDisplayedFeaturess() {
		for (int idx : this.indexes) {
			this.selects[idx] = true;
		}
	}

	/**
	 * DisSelect all the displayed pairs
	 * 
	 */
	public void disSelectAllDisplayedFeaturess() {
		for (int idx : this.indexes) {
			this.selects[idx] = false;
		}
	}

	@Override
	public Class<?>[] getColumnClasses() {
		return this.classes;
	}
	
	/**
	 * If editable for each of the column. Default, can not be edit.
	 * 
	 * @return
	 */
	@Override
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
	public FeaturesObject getRow(int idx) {
		// TODO Auto-generated method stub
		int index = this.indexes[idx + this.startIdxCurtPage];
		
		PeptidePair pair = this.reader.getPairs(index);
		return new FeaturesObject(pair, index, this.selects);
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
	public FeaturesObject[] getRows(int[] idxs) {
		// TODO Auto-generated method stub
		FeaturesObject[] objs = new FeaturesObject[idxs.length];

		for (int i = 0; i < idxs.length; i++) {
			objs[i] = this.getRow(idxs[i]);
		}

		return objs;
	}

	/**
	 * Now this method is used.
	 * @param nomod
	 * @param normal
	 * @return
	 * @throws Exception
	 */
	public QuanResult[] getAllResult(boolean nomod, boolean normal, int [] outputRatio) 
			throws Exception{
		return reader.getAllResult(getUsedList(), nomod, normal, outputRatio);
	}
	
	public PeptidePair [] getAllSelectedFeatures(){
		return reader.getAllSelectedPairs(getUsedList());
	}

	public void setTheoryRatio(double [] theoryRatio){
		this.reader.setTheoryRatio(theoryRatio);
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

	public AbstractFeaturesXMLReader getReader(){
		return reader;
	}
	
	public boolean isGradient(){
		
		if(reader instanceof LabelFeaturesXMLReader){
			
			return ((LabelFeaturesXMLReader)reader).isGradient();
			
		}else{
			return false;
		}
	}
	
	public RatioStatDataset getRatioDistribution(){
		
		PeptidePair [] feas = this.getAllSelectedFeatures();
		if(feas==null || feas.length==0)
			return null;
		
/*		if(reader.getType()==LabelType.SixLabel){
			int [] select = new int []{0, 9, 14};
			for(int i=0;i<feas.length;i++){
				feas[i].setSelectRatio(select);
			}
		}else if(reader.getType()==LabelType.FiveLabel){
			int [] select = new int []{0, 7, 9};
			for(int i=0;i<feas.length;i++){
				feas[i].setSelectRatio(select);
			}
		}
*/
		return new RatioStatDataset(feas, false, reader.ratioNames);
	}

	public void closeList() {
		this.reader.close();
	}
	
	/**
	 * @return
	 */
	public LabelType getType() {
		// TODO Auto-generated method stub
		return reader.getType();
	}
	
	public ModInfo[] getMods() {
		return reader.getMods();
	}
	
	public File getFile(){
		return reader.getFile().getParentFile();
	}

	/**
	 * @return
	 */
	public String getFileName() {
		// TODO Auto-generated method stub
		return reader.getFile().getName();
	}
	
	public Object [][] getRatioModelInfo(){
		return reader.getRatioModelInfo();
	}
	
	public boolean isGlyco(){
		return glyco;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
