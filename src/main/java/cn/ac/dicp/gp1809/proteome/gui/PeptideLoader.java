/* 
 ******************************************************************************
 * File: PeptideLoader.java * * * Created on 04-12-2009
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
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListAccesser;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;

/**
 * The gui loader of peptide
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 19:40:34
 */
public class PeptideLoader {

	public static final int peptide = 0;
	
	public static final int phosPeptide = 1;
	
	public static final int glycoPeptide = 2;
	
	/**
	 * Load peptides for table display
	 * 
	 * @param ppl
	 * @param criteria
	 *            can be null.
	 * @param topN
	 * @return
	 * @throws FileDamageException
	 * @throws IOException
	 */
	public static PeptideListPagedRowGettor load(String ppl,
	        IPeptideCriteria criteria, int topN) throws FileDamageException,
	        IOException {

		PeptideListReader reader = new PeptideListReader(ppl);
		reader.setTopN(topN);
		IntArrayList indexlist = new IntArrayList();

		if (criteria != null) {
			if (reader.getPeptideType() != criteria.getPeptideType()) {
				reader.close();

				throw new IllegalArgumentException(
				        "Criteria cannot apply to peptides with different type.");
			}

			IPeptide pep;
			while ((pep = reader.getPeptide()) != null) {
				if (criteria.filter(pep)) {
					indexlist.add(reader.getCurtPeptideIndex());
				}
			}
		} else {
			while (reader.getPeptide() != null) {
				indexlist.add(reader.getCurtPeptideIndex());
			}
		}

		reader.close();

		if(indexlist.size() ==0)
			throw new NullPointerException("No peptide passes the filter.");
		
		return new PeptideListPagedRowGettor(new PeptideListAccesser(ppl),
		        indexlist.toArray());
	}
	
	public static void load(String ppl) throws FileDamageException, IOException{

		File [] files = (new File(ppl)).listFiles(new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				
				if(pathname.getName().endsWith(".ppl")){
					return true;
				}
				return false;
			}
			
		});
		
		PeptideListPagedRowGettor getter = new PeptideListPagedRowGettor(files);
		PeptideListViewer viewer = new PeptideListViewer(getter);
		viewer.setVisible(true);
	}
	
	public static void load(File ppl) throws FileDamageException, IOException{
		
		PeptideListPagedRowGettor getter = null;
		
		if(ppl.isFile()){
			getter = new PeptideListPagedRowGettor(ppl);
			
		}else if(ppl.isDirectory()){
			File [] files = ppl.listFiles(new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					
					if(pathname.getName().endsWith(".ppl")){
						return true;
					}
					return false;
				}
				
			});
			getter = new PeptideListPagedRowGettor(files);
		}

		PeptideListViewer viewer = new PeptideListViewer(getter);
		viewer.setVisible(true);
	}
	
	public static void load(File [] files) throws FileDamageException, IOException{
		
		PeptideListPagedRowGettor getter = new PeptideListPagedRowGettor(files);
		PeptideListViewer viewer = new PeptideListViewer(getter);
		viewer.setVisible(true);
	}
	
	public static void main(String [] args) throws FileDamageException, IOException{
		String file = "F:\\data\\best_label";
		PeptideLoader.load(file);
	}

}
