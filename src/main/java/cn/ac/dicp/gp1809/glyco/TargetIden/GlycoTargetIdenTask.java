/* 
 ******************************************************************************
 * File: GlycoTargetIdenTask.java * * * Created on 2013-5-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.TargetIden;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenXMLReader;
import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenXMLWriter;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 * 
 * @version 2013-5-28, 8:38:44
 */
public class GlycoTargetIdenTask implements ITask {
	
	private NGlycoTargetGetter getter;
	private GlycoIdenXMLWriter writer;
	private HashMap<Integer, NGlycoSSM[]> ssmmap;
	private Iterator<Integer> it;

	public GlycoTargetIdenTask(String peakfile, String fasta, String result,
			GlycoJudgeParameter jpara) throws XMLStreamException, IOException {

		this.getter = new NGlycoTargetGetter(peakfile, fasta, jpara);
		this.writer = new GlycoIdenXMLWriter(result);

		this.ssmmap = getter.getGlySpecMap();
		this.it = ssmmap.keySet().iterator();
	}
	
	public GlycoTargetIdenTask(String peakfile, HashMap<Double, String> pepmap, String result,
			GlycoJudgeParameter jpara) throws XMLStreamException, IOException {

		this.getter = new NGlycoTargetGetter(peakfile, pepmap, jpara);
		this.writer = new GlycoIdenXMLWriter(result);

		this.ssmmap = getter.getGlySpecMap();
		this.it = ssmmap.keySet().iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

		try {
			this.writer.write();
			this.writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.getter = null;
		System.gc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return it.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		// TODO Auto-generated method stub

		Integer in = it.next();
		NGlycoSSM[] ssms = this.ssmmap.get(in);
		for (int i = 0; i < ssms.length; i++) {
			this.writer.addGlycan(ssms[i], ssms[i].getSequence());
		}
	}

	private static void batchProcess(String in, String fasta) throws Exception{
		
		GlycoJudgeParameter jpara = 
				new GlycoJudgeParameter(0.001f, 20f, 0.15f, 500, 0.3f, 60.0f, 1);
		
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			
			String path = files[i].getAbsolutePath();
			if(!path.endsWith("mzXML"))
				continue;
			
			String pxml = path.replace("mzXML", "proteinmatch.pxml");
			String xls = pxml.replace("pxml", "xls");
			
			GlycoTargetIdenTask task = new GlycoTargetIdenTask(path, fasta, pxml, jpara);
			while(task.hasNext()){
				task.processNext();
			}
			
			task.dispose();
			
			GlycoTargetIdenXlsWriter writer = new GlycoTargetIdenXlsWriter(xls);
			GlycoIdenXMLReader reader = new GlycoIdenXMLReader(pxml);
			
			NGlycoSSM[] ssms = reader.getAllMatches();
			for (int j = 0; j < ssms.length; j++) {
				if (ssms[j].getRank() == 1)
					writer.write(ssms[j]);
			}

			writer.close();
		}
	}
	
	private static void batchProcessPeptide(String in, String peps) throws Exception{
		
		HashMap <Double, String> map = new HashMap <Double, String>();
		BufferedReader br = new BufferedReader(new FileReader(peps));
		String line = null;
		while((line=br.readLine())!=null){
			String [] cs = line.split("\t");
			map.put(Double.parseDouble(cs[1]), cs[0]);
		}
		br.close();
		
		GlycoJudgeParameter jpara = 
				new GlycoJudgeParameter(0.001f, 50f, 0.15f, 500, 0.3f, 60.0f, 1);
		
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			
			String path = files[i].getAbsolutePath();
			if(!path.endsWith("mzXML"))
				continue;
			
			String pxml = path.replace("mzXML", "peptidematch.pxml");
			String xls = pxml.replace("pxml", "xls");
			
			GlycoTargetIdenTask task = new GlycoTargetIdenTask(path, map, pxml, jpara);
			while(task.hasNext()){
				task.processNext();
			}
			
			task.dispose();
			
			GlycoTargetIdenXlsWriter writer = new GlycoTargetIdenXlsWriter(xls);
			GlycoIdenXMLReader reader = new GlycoIdenXMLReader(pxml);
			
			NGlycoSSM[] ssms = reader.getAllMatches();
			for (int j = 0; j < ssms.length; j++) {
				if (ssms[j].getRank() == 1)
					writer.write(ssms[j]);
			}

			writer.close();
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		long beg = System.currentTimeMillis();
		
		String in = "H:\\20130519_glyco\\HCD20130523";
//		String fasta = "D:\\sun_glyco\\20130529\\Transferrin-P02787.fasta";
//		String fasta = "D:\\hulianghai\\glyco-antibody_HLH.fasta";
		String fasta = "H:\\20130519_glyco\\fetuin.fasta";
//		String out = "D:\\sun_glyco\\20130529\\130528_TRAF_FA_glyco_HCD_35%_10ms_10MSMS.proteinmatch.pxml";
//		GlycoJudgeParameter jpara = GlycoJudgeParameter.defaultParameter();
		GlycoTargetIdenTask.batchProcess(in, fasta);
//		GlycoTargetIdenTask.batchProcess2(in, "H:\\20130519_glyco\\HCD20130523\\peps.txt");
		
//		String in = "H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_5ms.mzXML";
//		String fasta = "H:\\20130519_glyco\\fetuin.fasta";
//		String out = "H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_5ms.proteinmatch.pxml";
		
		GlycoJudgeParameter jpara = 
			new GlycoJudgeParameter(0.001f, 20f, 0.15f, 500, 0.3f, 60.0f, 3);
		
//		GlycoTargetIdenTask task = new GlycoTargetIdenTask(in, fasta, out, jpara);
//		while(task.hasNext()){
//			task.processNext();
//		}
		
//		task.dispose();
		
		long end = System.currentTimeMillis();
		
		System.out.println((end-beg)/60000.0);
	}

}
