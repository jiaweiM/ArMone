/* 
 ******************************************************************************
 * File: LabelGradCombTask.java * * * Created on 2011-8-19
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.IO;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 *
 * @version 2011-8-19, 15:14:57
 */
public class LabelGradCombTask implements ITask {

	private File [] files;
	private int currentFile = 0;
	private int length;
	private boolean last = false;
//	private double [] totalIntensity;

	private ProteinNameAccesser accesser;
	private LabelFeaturesXMLWriter writer;
	
	private HashSet <String> quanPepSeq;
	private ArrayList <IPeptide> idenPepList;

	public LabelGradCombTask(String dir, String out) throws IOException{
		this(new File(dir), out);
	}
	
	public LabelGradCombTask(File dir, String out) throws IOException{
		
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
	
	public LabelGradCombTask(File [] files, String out) throws IOException{
		this.files = files;
		this.length = files.length;
		this.initial(out);
	}
	
	private void initial(String out) throws IOException{
		
		this.quanPepSeq = new HashSet <String>();
		this.idenPepList = new ArrayList <IPeptide>();
		
		try {
	    	
			LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(files[0]);
			this.writer = new LabelFeaturesXMLWriter(out, reader.getType(), true);
			this.accesser = reader.getProNameAccesser();
			ModInfo [] mods = reader.getAllMods();
			if(mods!=null)
				writer.addModification(mods);
			
//			this.totalIntensity = reader.getTotalIntensity();
			
			int feasNum = reader.getPairsNum();
			for(int i=0;i<feasNum;i++){
				PeptidePair feas = reader.getPairs(i);
				writer.addPeptidePair(feas);
				this.quanPepSeq.add(PeptideUtil.getUniqueSequence(feas.getSequence()));
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
					writer.addIdenPep(pep);
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
					writer.addPeptidePair(pair);
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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String out = "H:\\NGLYCO_QUAN\\NGlycan_Quan_20131111\\CID_iden\\F1-F5.pxml";
		String in = "H:\\NGLYCO_QUAN\\NGlycan_Quan_20131111\\CID_iden\\temp";
		File [] files = new File(in).listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("pxml"))
					return true;
				return false;
			}
			
		});
		LabelGradCombTask task = new LabelGradCombTask(files, out);
		while(task.hasNext())
			task.processNext();
		
		task.dispose();

	}
}
