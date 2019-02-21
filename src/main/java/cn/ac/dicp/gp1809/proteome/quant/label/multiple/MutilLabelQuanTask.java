/* 
 ******************************************************************************
 * File: MutilLabelQuanTask.java * * * Created on 2012-6-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelQuanTask;
import cn.ac.dicp.gp1809.proteome.quant.label.PeptidePairGetter;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 *
 * @version 2012-6-14, 20:07:44
 */
public class MutilLabelQuanTask extends LabelQuanTask implements ITask {

	private IPeptideListReader reader;
	private IPeptideListReader [] readers;
//	private MutilLabelPairXMLWriter writer;
	private PeptidePairGetter getter;
	private int currentReader = 0;

	private IPeptide curtPeptide;
	private boolean integration = false;
	
	public MutilLabelQuanTask(){
		super();
	}
	
	public MutilLabelQuanTask(IPeptideListReader reader, String pixfile, 
			String result, int type, int mzxmlType) throws IOException{
		
		this.readers = new IPeptideListReader[]{reader};
		this.reader = readers[0];
		
		AminoacidModification aamodif = reader.getSearchParameter().getVariableInfo();
		if(type==5){
			this.getter = new FiveFeaturesGetter(pixfile, mzxmlType);
			this.writer = new MutilLabelPairXMLWriter(new File(result), LabelType.FiveLabel, false);
			
		}else if(type==6){
			this.getter = new SixFeaturesGetter(pixfile, mzxmlType);
			this.writer = new MutilLabelPairXMLWriter(new File(result), LabelType.SixLabel, false);
		}
		
		getter.setModif(aamodif);
		writer.addModification(aamodif);
		writer.addProNameInfo(reader.getProNameAccesser());
	}
	
	public MutilLabelQuanTask(IPeptideListReader [] readers, String pixfile, 
			String result, int type, int mzxmlType) throws IOException{
		
		this.readers = readers;
		this.reader = readers[0];
		
		AminoacidModification aamodif = reader.getSearchParameter().getVariableInfo();
		if(type==5){
			this.getter = new FiveFeaturesGetter(pixfile, mzxmlType);
			this.writer = new MutilLabelPairXMLWriter(new File(result), LabelType.FiveLabel, false);
			
		}else if(type==6){
			this.getter = new SixFeaturesGetter(pixfile, mzxmlType);
			this.writer = new MutilLabelPairXMLWriter(new File(result), LabelType.SixLabel, false);
		}
		
		getter.setModif(aamodif);
		writer.addModification(aamodif);
		writer.addProNameInfo(reader.getProNameAccesser());
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		// TODO Auto-generated method stub
		float per = (float)currentReader/(float)readers.length;
		return per;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		getter.close();
		reader.close();
		System.gc();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		try {

			boolean has = false;
			
			this.curtPeptide = this.reader.getPeptide();
			if(curtPeptide==null){
				if(this.currentReader<readers.length-1){
					reader.close();
					
					currentReader++;
					this.reader = readers[currentReader];
					this.getter.setModif(reader.getSearchParameter().getVariableInfo());
					
					this.curtPeptide = this.reader.getPeptide();
					has = curtPeptide!=null;
				}
			}else{
				has = true;
			}

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
		
		if (this.integration) {
			
			HashMap <String, PeptidePair> pairMap = getter.getPeptidPairs();

			Iterator <String> it = pairMap.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				PeptidePair pair = pairMap.get(key);
				this.writer.addPeptidePair(pair);
			}
			
			HashMap <String, IPeptide> idenPepMap = getter.getIdenPepMap();
			HashMap <String, HashSet<Integer>> labelTypeMap = getter.getLabelTypeMap();
			Iterator <String> idenPepIt = idenPepMap.keySet().iterator();
			while(idenPepIt.hasNext()){
				String key = idenPepIt.next();
				this.writer.addIdenPep(idenPepMap.get(key), labelTypeMap.get(key));
			}

			try {
				writer.write();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else{
			if(this.curtPeptide == null)
				throw new NullPointerException("Null peptide. No more peptide?");

//			MascotPeptide mp = (MascotPeptide) curtPeptide;
//			if(mp.getIonscore()>25)
			this.getter.addPeptide(curtPeptide);
		}
	}
	
	public static void batchProcess(String in) throws IOException{
		
		HashMap <String, String> pplmap = new HashMap <String, String>();
		HashMap <String, String> peakmap = new HashMap <String, String>();
		
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			String name = files[i].getName();
			if(name.endsWith("mzXML")){
				int id = name.indexOf("_");
				peakmap.put(name.substring(id+1, name.length()-8), files[i].getAbsolutePath());
				System.out.println(name.substring(id-1, name.length()-6)+"\tmzxml");
			}else if(name.endsWith("ppl")){
				int id = name.lastIndexOf("_");
				pplmap.put(name.substring(0, id), files[i].getAbsolutePath());
				System.out.println(name.substring(0, id));
			}
		}
		
		Iterator <String> it = pplmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			String pplfile = pplmap.get(key);
			String peakfile = peakmap.get(key);
			String result = peakfile+".pxml";
			
			PeptideListReader [] pReaders = new PeptideListReader[1];
			try {
				pReaders[0] = new PeptideListReader(pplfile);
			} catch (FileDamageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			MutilLabelQuanTask task = new MutilLabelQuanTask(pReaders, peakfile, result, 6, 0);
			while(task.hasNext()){
				task.processNext();
			}
			task.dispose();
		}
	}
	
	public static void batchProcess(String ppl, String peak) throws IOException{
		
		HashMap <String, String> pplmap = new HashMap <String, String>();
		HashMap <String, String> peakmap = new HashMap <String, String>();
		
		File [] files1 = (new File(ppl)).listFiles();
		for(int i=0;i<files1.length;i++){
			String name = files1[i].getName();
			if(name.endsWith("ppl")){
				int firstid = name.indexOf("_");
				int id = name.lastIndexOf("_");
//				pplmap.put(name.substring(name.indexOf("_")+1, id), files1[i].getAbsolutePath());
//				pplmap.put(name.substring(firstid+1, id), files1[i].getAbsolutePath());
				pplmap.put(name.substring(0, id), files1[i].getAbsolutePath());
				System.out.println(name.substring(0, id));
			}
		}
		
		File [] files2 = (new File(peak)).listFiles();
		for(int i=0;i<files2.length;i++){
			String name = files2[i].getName();
			if(name.endsWith("mzXML")){
//				int id = name.lastIndexOf("_");
				int id = name.indexOf("_");
//				peakmap.put(name.substring(id+1, name.length()-21), files2[i].getAbsolutePath());
//				System.out.println(name.substring(id+1, name.length()-21)+"\tmzxml");
//				peakmap.put(name.substring(id-2, name.length()-8), files2[i].getAbsolutePath());
//				System.out.println(name.substring(id-2, name.length()-8)+"\tmzxml");
				peakmap.put(name.substring(0, name.length()-6), files2[i].getAbsolutePath());
				System.out.println(name.substring(0, name.length()-6)+"\tmzxml");
			}
		}
		
		Iterator <String> it = pplmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			if(!peakmap.containsKey(key)) continue;
			String pplfile = pplmap.get(key);
			String peakfile = peakmap.get(key);
			System.out.println(key);
			String result = (new File(peakfile)).getParent()+"\\"+key+".pxml";
			
			PeptideListReader [] pReaders = new PeptideListReader[1];
			try {
				pReaders[0] = new PeptideListReader(pplfile);
			} catch (FileDamageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			MutilLabelQuanTask task = new MutilLabelQuanTask(pReaders, peakfile, result, 6, 0);
			while(task.hasNext()){
				task.processNext();
			}
			task.dispose();
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		long startTime=System.currentTimeMillis();
		
		String ppl = "L:\\Data_DICP\\turnover\\20131115_HepG2_PT_turnover\\DATfiles_20131115_HepG2_PT" +
				"\\20131115_HepG2_PT_0_3_6h_100mM_F001569.dat.ppl";
		
		String pix = "L:\\Data_DICP\\turnover\\20131115_HepG2_PT_turnover" +
			"\\20131115_HepG2_PT_0_3_6h_100mM.mzXML";
		
		String result = "L:\\Data_DICP\\turnover\\20131115_HepG2_PT_turnover" +
			"\\20131115_HepG2_PT_0_3_6h_100mM.pxml";
		
		/*String ppl = "M:\\Data\\sixple\\turnover\\dat\\0_3_6" +
				"\\0_3_6_400_F001259.dat.ppl";
		
		String pix = "M:\\Data\\sixple\\turnover\\dat\\0_3_6_peak" +
			"\\20120805PT0_3_6_400mM.mzXML";
		
		String result = "M:\\Data\\sixple\\turnover\\dat\\0_3_6_peak" +
			"\\400mM.pxml";*/
		
		/*File [] files = new File(ppl).listFiles(new FileFilter(){

			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				
				if(file.getName().endsWith("ppl")){
					return true;
				}
				
				return false;
			}

		});

		File [] files = new File [] {new File("C:\\Inetpub\\wwwroot\\ISB\\data\\" +
				"F002325_20120825_Human_Normal_Cancer_Five_150mM.csv.ppl")};

		PeptideListReader [] pReaders = new PeptideListReader[files.length];		
		try {
			for(int i=0;i<files.length;i++){
				pReaders[i] = new PeptideListReader(files[i]);
			}
			
		} catch (FileDamageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*PeptideListReader [] pReaders = new PeptideListReader[]{new PeptideListReader(ppl)};
		MutilLabelQuanTask task = new MutilLabelQuanTask(pReaders, pix, result, 6, 0);
		while(task.hasNext()){
			task.processNext();
		}
		task.dispose();*/
		
		MutilLabelQuanTask.batchProcess("I:\\LJ\\20140907_hela_nucleus_turnover\\DATfiles\\temp", 
				"I:\\LJ\\20140907_hela_nucleus_turnover");
		
//		MutilLabelQuanTask.batchProcess("J:\\Data\\sixple\\control2\\pep1", 
//				"J:\\Data\\sixple\\control2\\peak1");
		
		long endTime=System.currentTimeMillis(); 
		System.out.println("��������ʱ�䣺 "+(endTime-startTime)/1000+"s");
	}
	
	
}
