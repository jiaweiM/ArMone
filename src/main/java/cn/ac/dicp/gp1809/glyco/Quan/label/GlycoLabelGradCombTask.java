/* 
 ******************************************************************************
 * File: GlycoLabelGradCombTask.java * * * Created on 2014��5��29��
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 * @deprecated
 * @version 2014��5��29��, ����4:48:43
 */
public class GlycoLabelGradCombTask implements ITask {
	
	private File [] files;
	private int currentFile = 0;
	private int length;
	private boolean last = false;
//	private double [] totalIntensity;

	private ProteinNameAccesser accesser;
	private GlycoLabelFeaturesXMLWriter writer;
	
	private HashSet <String> quanPepSeq;
	private ArrayList <IPeptide> idenPepList;
	
	public GlycoLabelGradCombTask(String dir, String out) throws IOException{
		this(new File(dir), out);
	}
	
	public GlycoLabelGradCombTask(File dir, String out) throws IOException{

		FileFilter fileFilter = new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp = pathname.getName().toLowerCase();
	            if(tmp.endsWith(".pxml")){
	                return true;
	            }
	            return false;
	        }
	    };
	    
	    File [] files = dir.listFiles(fileFilter);
	    if(files==null || files.length==0)
	    	throw new FileNotFoundException("There are no *.pxml file in this directory : "+dir);
	    
	    this.files = files;
	    this.length = files.length;
	    this.initial(out);
	
	}

	private void initial(String out) throws IOException{
		
		this.quanPepSeq = new HashSet <String>();
		this.idenPepList = new ArrayList <IPeptide>();
		
		try {
	    	
			GlycoLabelFeaturesXMLReader reader = new GlycoLabelFeaturesXMLReader(files[0]);
			this.writer = new GlycoLabelFeaturesXMLWriter(out, reader.getType(), true);
			this.accesser = reader.getProNameAccesser();
			ModInfo[] mods = reader.getAllMods();
			if(mods!=null)
				writer.addModification(mods);
			
//			this.totalIntensity = reader.getTotalIntensity();
			
/*			int feasNum = reader.getPairsNum();
			for(int i=0;i<feasNum;i++){
				PeptidePair feas = reader.getPairs(i);
				writer.addPeptidePair(feas);
				this.quanPepSeq.add(PeptideUtil.getUniqueSequence(feas.getSequence()));
			}

			IPeptide pep;
			while((pep=reader.getIdenPep())!=null){
				this.idenPepList.add(pep);
			}*/

			reader.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		// TODO Auto-generated method stub
		return (float)currentFile/length;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
		System.gc();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		
		currentFile++;
		boolean has = currentFile<length;
		
		if (has) {
			return true;
		} else {
			if (!this.last) {
				this.last = true;
				return true;
			} else
				return false;
		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		// TODO Auto-generated method stub
		
		if(last){
			
			Iterator <IPeptide> it = this.idenPepList.iterator();
			while(it.hasNext()){
				IPeptide pep = it.next();
				String key = PeptideUtil.getUniqueSequence(pep.getSequence());
				if(!this.quanPepSeq.contains(key)){
//					writer.addIdenPep(pep);
				}
			}
			
			this.writer.addProNameInfo(accesser);
			try {
				this.writer.write();
				this.writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else{
			
			LabelFeaturesXMLReader reader;
			try {
				reader = new LabelFeaturesXMLReader(files[currentFile]);
				this.accesser.appand(reader.getProNameAccesser());
				
//				double [] intensity = reader.getTotalIntensity();
//				for(int i=0;i<intensity.length;i++){
//					this.totalIntensity[i] += intensity[i];
//				}
				
				int feaNum = reader.getPairsNum();
				for(int i=0;i<feaNum;i++){
					PeptidePair pair = reader.getPairs(i);
//					writer.addPeptidePair(pair);
					this.quanPepSeq.add(PeptideUtil.getUniqueSequence(pair.getSequence()));
				}

				IPeptide pep;
				while((pep=reader.getIdenPep())!=null){
					this.idenPepList.add(pep);
				}
				
				reader.close();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
