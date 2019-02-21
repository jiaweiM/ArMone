/* 
 ******************************************************************************
 * File: LFreeQuanTask.java * * * Created on 2011-7-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree.IO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeasGetter;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 *
 * @version 2011-7-2, 08:58:25
 */
public class LFreeQuanTask implements ITask {

	private IPeptideListReader reader;
	private LFFeasXMLWriter writer;
	private FreeFeasGetter getter;
	
	private IPeptide curtPeptide;
	private int total;
	private int curt;
	private boolean integration = false;
	
	public LFreeQuanTask(IPeptideListReader reader, String pixfile, 
			String result, int leastIdenNum) throws FastaDataBaseException, IOException{
		this(reader, new FreeFeasGetter(pixfile, leastIdenNum), result);
	}
	
	public LFreeQuanTask(IPeptideListReader reader, FreeFeasGetter getter, String result) throws IOException{
		
		this.reader = reader;
		this.total = reader.getNumberofPeptides();
		
		this.getter = getter;
		this.writer = new LFFeasXMLWriter(result);
		writer.addTotalCurrent(getter.getMS1TotalCurrent());
		
		AminoacidModification aamodif = reader.getSearchParameter().getVariableInfo();
		writer.addModification(aamodif);
		writer.addProNameInfo(reader.getProNameAccesser());
	}

	@Override
	public float completedPercent() {
		float per = (float)curt/(float)total;
		return per;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		getter.close();
		reader.close();
		System.gc();
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		try {

			boolean has = (curtPeptide = this.reader.getPeptide()) != null;
			
			if (has) {
				return true;
			} else {
				if (!this.integration) {
					this.integration = true;
					return true;
				} else
					return false;
			}
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean inDetermineable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processNext() {
		// TODO Auto-generated method stub
		if (this.integration) {
			
			HashMap <String, FreeFeatures> feasMap = getter.getFeatures();
			HashMap <String, IPeptide> pepMap = getter.getPepMap();
			Iterator <String> it = feasMap.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				FreeFeatures feas = feasMap.get(key);
				IPeptide pep = pepMap.get(key);
				this.writer.addFeature(feas, pep);
			}
			
			try {
				writer.write();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}else{
			
			if(this.curtPeptide == null)
				throw new NullPointerException("Null peptide. No more peptide?");

			this.getter.addPeptide(curtPeptide);
			this.curt = this.reader.getCurtPeptideIndex();
		}
	}
}
