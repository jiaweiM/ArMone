/* 
 ******************************************************************************
 * File: PeptideListPagedRowGettor.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListAccesser;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListAccesser;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.gui.PeptideStatInfo.PeptideCountInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;
import cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor;

/**
 * The peptides in the paged rows
 * 
 * @deprecated 08-24-2011
 * @author Xinning
 * @version 0.1, 04-10-2009, 18:54:57
 * @version 0.2, 08-23-2011
 */
public class MultiPeptideListPagedRowGettor extends
	PeptideListPagedRowGettor {
	
	/**
	 * Contain the filters
	 */
	private HashSet<IPeptideCriteria> filters = new HashSet<IPeptideCriteria>();

	private PeptideListAccesser [] accesser;
	private String[] titles;
	private Class<?>[] classes;
	private boolean editable[];
	
	private PeptideType type;
	private ISearchParameter parameter;
	private ProteinNameAccesser proNameAccesser;
	private IPeptideFormat format;
	
	private HashMap <Integer, Integer> accesserIdMap;
	private HashMap <Integer, Integer> idIdxMap;

	/**
	 * The backup indexes before the set of filters
	 */
	private final int[] backIndexes;

	/**
	 * whether the peptide selected. Use the index in the accesser to check
	 * whether the peptide is selected
	 */
	private boolean[] selects;

	/**
	 * The indexes of the used peptides in the peptide list.
	 */
	private int[] indexes;

	/**
	 * The start index in current page
	 */
	private int startIdxCurtPage;

	public MultiPeptideListPagedRowGettor(String dir) throws FileDamageException, IOException{
		this(dir, null);
	}
	
	public MultiPeptideListPagedRowGettor(String dir, int[] usedPepIndxes)
	        throws FileDamageException, IOException {

		this.accesserIdMap = new HashMap <Integer, Integer>();
		this.idIdxMap = new HashMap <Integer, Integer>();
		
		File [] files = new File(dir).listFiles(new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				
				if(pathname.getName().endsWith(".ppl")){
					return true;
				}
				return false;
			}
			
		});

		this.accesser = new PeptideListAccesser[files.length];
		accesser[0] = new PeptideListAccesser(files[0]);
		int num = 0;
		for(;num<accesser[0].getNumberofPeptides();num++){
			this.accesserIdMap.put(num, 0);
			this.idIdxMap.put(num, num);
		}
		this.parameter = accesser[0].getSearchParameter();
		this.proNameAccesser = accesser[0].getProNameAccesser();
		this.type = accesser[0].getPeptideType();
		this.format = accesser[0].getPeptideFormat();
		
		for(int i=1;i<files.length;i++){
			accesser[i] = new PeptideListAccesser(files[i]);
			for(int j=0;j<accesser[i].getNumberofPeptides();j++){
				num++;
				this.accesserIdMap.put(num, i);
				this.idIdxMap.put(num, j);
			}
			this.proNameAccesser.appand(accesser[i].getProNameAccesser());
		}
		
		this.titles = format.getTitle();
		//If this peptide selected
		this.titles[0] = "Selected";

		if (usedPepIndxes != null)
			this.indexes = usedPepIndxes;
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
	
	public MultiPeptideListPagedRowGettor(File [] files) throws FileDamageException, IOException {
		this(files, null);
	}
	
	public MultiPeptideListPagedRowGettor(File [] files, int[] usedPepIndxes) throws FileDamageException, IOException {
		
		this.accesserIdMap = new HashMap <Integer, Integer>();
		this.idIdxMap = new HashMap <Integer, Integer>();

		this.accesser = new PeptideListAccesser[files.length];
		accesser[0] = new PeptideListAccesser(files[0]);
		int num = 0;
		for(;num<accesser[0].getNumberofPeptides();num++){
			this.accesserIdMap.put(num, 0);
			this.idIdxMap.put(num, num);
		}
		this.parameter = accesser[0].getSearchParameter();
		this.proNameAccesser = accesser[0].getProNameAccesser();
		this.type = accesser[0].getPeptideType();
		this.format = accesser[0].getPeptideFormat();
		
		for(int i=1;i<files.length;i++){
			accesser[i] = new PeptideListAccesser(files[i]);
			for(int j=0;j<accesser[i].getNumberofPeptides();j++){
				num++;
				this.accesserIdMap.put(num, i);
				this.idIdxMap.put(num, j);
			}
			this.proNameAccesser.appand(accesser[i].getProNameAccesser());
		}
		
		this.titles = format.getTitle();
		//If this peptide selected
		this.titles[0] = "Selected";

		if (usedPepIndxes != null)
			this.indexes = usedPepIndxes;
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

/*
	public MultiPeptideListPagedRowGettor(PeptideListAccesser [] accesser) {
		this(accesser, null);
	}	

	public MultiPeptideListPagedRowGettor(PeptideListAccesser [] accesser,
	        int[] usedPepIndxes) {
		
		this.accesserIdMap = new HashMap <Integer, Integer>();
		this.idIdxMap = new HashMap <Integer, Integer>();

		this.accesser = accesser;
		this.titles = this.accesser[0].getPeptideFormat().getTitle();
		//If this peptide selected
		this.titles[0] = "Selected";

		int num = 0;
		for(int i=0;i<num;i++){
			this.accesserIdMap.put(i, 0);
			this.idIdxMap.put(i, i);
		}
		
		for(int i=0;i<accesser.length;i++){
			num += accesser[i].getNumberofPeptides();
		}
		
		if (usedPepIndxes != null)
			this.indexes = usedPepIndxes;
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
*/	

	/**
	 * Parse the peptide information
	 * 
	 * @deprecated
	 * @throws PeptideParsingException
	 */
	public PeptideListInfo getPeptideInfo() {

		PeptideListInfo info = new PeptideListInfo();
		if (this.indexes != null && this.indexes.length > 0) {
			int total = this.indexes.length;

			int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
			int target = 0, decoy = 0;
			for (int index : indexes) {
				IPeptide pep = getPeptide(index);
				short charge = pep.getCharge();
				switch (charge) {
				case 0:
					System.err.println("Charge state of 0, skip counting.");
					break;
				case 1:
					c1++;
					break;
				case 2:
					c2++;
					break;
				case 3:
					c3++;
					break;
				default:
					c4++;
					break;
				}

				if (pep.isTP())
					target++;
				else
					decoy++;
			}

			float fdr = decoy * 2f / total;
			if (fdr > 1)
				fdr = 1;

			info.setPep_total(total);
			info.setPep1(c1);
			info.setPep2(c2);
			info.setPep3(c3);
			info.setPep3plus(c4);
			info.setTarget(target);
			info.setDecoy(decoy);
			info.setFdr(fdr);
		}

		return info;
	}

	/**
	 * Parse the peptide information
	 * 
	 * @throws PeptideParsingException
	 */
	public PeptideStatInfo getPeptideStatInfo() {

		PeptideCountInfo[] infos = new PeptideCountInfo[5];
		double totalSIn = 0;

		if (this.indexes != null && this.indexes.length > 0) {

			int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
			int c1d = 0, c2d = 0, c3d = 0, c4d = 0;
			
			for (int index : indexes) {
				
				if(!this.selects[index])
					continue;
				
				IPeptide pep = getPeptide(index);
				double fragInten = pep.getFragInten();

				totalSIn += fragInten;
			
				short charge = pep.getCharge();
				switch (charge) {
				case 0:
					System.err.println("Charge state of 0, skip counting.");
					break;
				case 1:
					c1++;
					if (!pep.isTP())
						c1d++;
					break;
				case 2:
					c2++;
					if (!pep.isTP())
						c2d++;
					break;
				case 3:
					c3++;
					if (!pep.isTP())
						c3d++;
					break;
				default:
					c4++;
					if (!pep.isTP())
						c4d++;
					break;
				}
			}
			
			int total = c1+c2+c3+c4;
			int totalD = c1d+c2d+c3d+c4d;

			infos[0] = new PeptideCountInfo("z=1", c1-c1d, c1d);
			infos[1] = new PeptideCountInfo("z=2", c2-c2d, c2d);
			infos[2] = new PeptideCountInfo("z=3", c3-c3d, c3d);
			infos[3] = new PeptideCountInfo("z>=4", c4-c4d, c4d);
			infos[4] = new PeptideCountInfo("total", total-totalD, totalD);
		}

		return new PeptideStatInfo(infos,totalSIn);
	}
	
	public IPeptide getPeptide(int idx){
		int id1 = this.accesserIdMap.get(idx);
		int id2 = this.idIdxMap.get(idx);
		return this.accesser[id1].getPeptide(id2);
	}
	
	public IMS2PeakList [] getPeakLists(int idx){
		int id1 = this.accesserIdMap.get(idx);
		int id2 = this.idIdxMap.get(idx);
		return this.accesser[id1].getPeakLists(id2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ITableRowGettor#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return this.titles.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ITableRowGettor#getColumnNames()
	 */
	@Override
	public String[] getColumnNames() {
		return this.titles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor#move2Page(int)
	 */
	@Override
	protected int move2Page(int pageIdx) throws IndexOutOfBoundsException {
		int max = this.getMaxRecordsperPage();
		this.startIdxCurtPage = pageIdx * max;
		int end = (pageIdx + 1) * max;

		if (end > this.getRowCount())
			end = this.getRowCount();

		return end - this.startIdxCurtPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ITableRowGettor#getRow(int)
	 */
	@Override
	public PeptideRowObject getRow(int idx) {

		int index = this.indexes[idx + this.startIdxCurtPage];
		IPeptide pep = getPeptide(index);
		return new PeptideRowObject(pep, index, this.selects);
	}
	
	/**
	 * Get the row and the spectra from the peptide list
	 * 
	 * @param idx
	 * @return
	 */
	public PeptideRowObject getRowandSpectra(int idx) {

		int index = this.indexes[idx + this.startIdxCurtPage];
		IPeptide pep = getPeptide(index);
		IMS2PeakList [] peaklists = getPeakLists(index);
		
		return new PeptideRowObject(pep, peaklists, index, this.selects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ITableRowGettor#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return this.indexes.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ITableRowGettor#getRows(int[])
	 */
	@Override
	public PeptideRowObject[] getRows(int[] idxs) {

		PeptideRowObject[] objs = new PeptideRowObject[idxs.length];

		for (int i = 0; i < idxs.length; i++) {
			objs[i] = this.getRow(idxs[i]);
		}

		return objs;
	}

	/**
	 * The type of the peptides
	 * 
	 * @return
	 */
	public PeptideType getPeptideType() {
		return this.type;
	}

	/**
	 * The search parameter
	 * 
	 * @return
	 */
	public ISearchParameter getSearchParameter() {
		return this.parameter;
	}

	/**
	 * Select all the displayed peptides
	 * 
	 */
	public void selectAllDisplayedPeptides() {
		for (int idx : this.indexes) {
			this.selects[idx] = true;
		}
	}

	/**
	 * DisSelect all the displayed peptides
	 * 
	 */
	public void disSelectAllDisplayedPeptides() {
		for (int idx : this.indexes) {
			this.selects[idx] = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowGettor#getColumnClasses()
	 */
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

	/**
	 * Set the filter
	 * 
	 * @param filter
	 */
/*	
	public void filter(IPeptideCriteria filter) {
		if (filter.getPeptideType() != this.type) {
			throw new IllegalArgumentException(
			        "Unsuitable peptide filter. The peptide type is "
			                + this.type
			                + ", the criteria type is "
			                + filter.getPeptideType());
		}

		IntArrayList list = new IntArrayList();
		for (int index : this.backIndexes) {
			if (filter.filter(getPeptide(index))) {
				list.add(index);
			}
		}

		this.indexes = list.toArray();
	}
*/
	/**
	 * Set the filter
	 * 
	 * @param filter
	 */
	public void addFilter(IPeptideCriteria filter) {
		PeptideType filterType = filter.getPeptideType();
		
		if (filterType != PeptideType.GENERIC && filter.getPeptideType() != getPeptideType()) {
			
			throw new IllegalArgumentException(
			        "Unsuitable peptide filter. The peptide type is "
			                + this.type
			                + ", the criteria type is "
			                + filter.getPeptideType());
		}

		if (!this.filters.contains(filter)) {
			IntArrayList list = new IntArrayList();
			for (int index : this.indexes) {
				if (filter.filter(getPeptide(index))) {
					list.add(index);
				}
			}

			this.indexes = list.toArray();

			this.filters.add(filter);
		}
	}

	/**
	 * Refilter
	 */
	private void reFilter() {

		if (this.filters.size() > 0) {
			IPeptideCriteria[] cirteria = this.filters
			        .toArray(new IPeptideCriteria[this.filters.size()]);

			IntArrayList list = new IntArrayList();
			for (int index : this.backIndexes) {
				boolean isTrue = true;
				IPeptide pep = getPeptide(index);
				for (IPeptideCriteria filter : cirteria) {
					if (!filter.filter(pep)) {
						isTrue = false;
						break;
					}
				}

				if (isTrue)
					list.add(index);

			}
			this.indexes = list.toArray();
		} else {
			this.indexes = this.backIndexes.clone();
		}
	}

	/**
	 * Remove the filter instance. The filter should be the same instance as has
	 * been added.
	 * 
	 * @param filter
	 * @return if removed
	 */
	public boolean removeFilter(IPeptideCriteria filter) {

		boolean removed = this.filters.remove(filter);

		if (removed) {
			this.reFilter();
		}

		return removed;
	}


	/**
	 * The information of peptide list file.
	 * 
	 * @return
	 */
	public String getPplInfo() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("[File name]: ");
		for(int i=0;i<accesser.length;i++){
			sb.append(accesser[i].getFile().getName()).append("\n");
		}
		sb.append("[Search algorithm]: ").append(this.type.getAlgorithm_name()).append("\n");
		sb.append("[Search parameters]: \n").append(this.parameter.getStaticInfo().getModfiedAADescription(true))
		  .append(this.parameter.getVariableInfo().getModficationDescription());
		return sb.toString();
	}
	
	/**
	 * Export the displayed peptides to new ppl file
	 * 
	 * @param pplpath
	 * @throws FileNotFoundException
	 * @throws PeptideParsingException
	 * @throws ProWriterException
	 */
	public void exportDisplayToFile(String pplpath)
	        throws FileNotFoundException, PeptideParsingException,
	        ProWriterException {

		PeptideListWriter writer = new PeptideListWriter(pplpath, this.format, this.parameter, 
		        this.accesser[0].getDecoyJudger(), this.proNameAccesser);

		for (int idx : this.indexes) {
			if (this.selects[idx])
				writer.write(getPeptide(idx), getPeakLists(idx));
		}

		writer.close();
	}

	/**
	 * Close and finished reading
	 */
	public void closeList() {
		for(int i=0;i<accesser.length;i++)
			this.accesser[i].close();
	}

}
