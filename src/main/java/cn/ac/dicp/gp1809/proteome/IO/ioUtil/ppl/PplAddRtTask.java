/* 
 ******************************************************************************
 * File: PplAddRtTask.java * * * Created on 2013-8-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2013-8-20, 18:30:00
 */
public class PplAddRtTask implements IPplCreationTask {
	
	private PeptideListReader pepReader;
	private MzXMLReader peakReader;
	private IPeptideWriter pwriter;
	
	private IPeptide peptide;
	private boolean end;
	private boolean closed = false;
	// key is scan number begin
	private HashMap <Integer, HashSet <IPeptide>> pepMap;
	private static final DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public PplAddRtTask(String ppl, String peak, String out) throws IOException, XMLStreamException, FileDamageException{
		
		this.pepReader = new PeptideListReader(ppl);
		this.peakReader = new MzXMLReader(peak);
		this.pepMap = new HashMap <Integer, HashSet <IPeptide>>();
		
		ISearchParameter parameter = pepReader.getSearchParameter();
		this.pwriter = new PeptideListWriter(out, pepReader.getPeptideFormat(),
		        parameter, pepReader.getDecoyJudger(), true, pepReader.getProNameAccesser());
	}

	

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub

		boolean has = (peptide = this.pepReader.getPeptide()) != null;

		if (has) {
			return true;
			
		} else {
			
			if (!this.end) {
				this.end = true;
				return true;
			} else
				return false;
		}
	
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		// TODO Auto-generated method stub

		if(end){

			IMS2Scan scan = null;
			while((scan=this.peakReader.getNextMS2Scan())!=null){

				int scannum = scan.getScanNum();

				if(pepMap.containsKey(scannum)){

					HashSet <IPeptide> pepset = pepMap.get(scannum);
					Iterator <IPeptide> pepit = pepset.iterator();
					while(pepit.hasNext()){
						
						IPeptide peptide = pepit.next();
						
						String key = PeptideUtil.getUniqueSequence(peptide.getSequence());
						
						IMS2PeakList peaklist = scan.getPeakList();
						
						if(peptide.getRetentionTime()<=0){
							peptide.setRetentionTime(Double.parseDouble(df4.format(peaklist.getPrecursePeak().getRT())));
						}
						
						if(peptide.getInten()<=0){
							peptide.setInten(Double.parseDouble(df4.format(peaklist.getPrecursePeak().getIntensity())));
						}
						
						pwriter.write(peptide, new IMS2PeakList []{peaklist});
					}
				}
			}
			
		}else{
			
			if(peptide!=null){

				if(pepMap.containsKey(peptide.getScanNumBeg())){

					pepMap.get(peptide.getScanNumBeg()).add(peptide);
					
				}else{
					
					HashSet <IPeptide> pepset = new HashSet <IPeptide>();
					pepset.add(peptide);
					pepMap.put(peptide.getScanNumBeg(), pepset);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		// TODO Auto-generated method stub
		return 0;
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
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

		if (!this.closed) {
			if (this.peakReader != null)
				this.peakReader.close();

			if (this.pepReader != null)
				this.pepReader.close();

			if (this.pwriter != null)
				try {
					this.pwriter.close();
				} catch (ProWriterException e) {
					throw new RuntimeException(e);
				}

			this.closed = true;
			System.gc();
		}
	
	}
	
	private static void batchProcess(String ppl, String peak) throws FileDamageException, IOException, XMLStreamException{

		HashMap <String, String> pplmap = new HashMap <String, String>();
		HashMap <String, String> peakmap = new HashMap <String, String>();
		
		File [] files1 = (new File(ppl)).listFiles();
		for(int i=0;i<files1.length;i++){
			String name = files1[i].getName();
			if(name.endsWith("ppl")){
				int id = name.lastIndexOf("_");
				pplmap.put(name.substring(0, name.length()-16), files1[i].getAbsolutePath());
				System.out.println("peptide\t"+name.substring(0, name.length()-16));
			}
		}
		
		File [] files2 = (new File(peak)).listFiles();
		for(int i=0;i<files2.length;i++){
			String name = files2[i].getName();
			if(name.endsWith("mzXML")){
				int id = name.lastIndexOf("_");
//				peakmap.put(name.substring(id+1, name.length()-21), files2[i].getAbsolutePath());
//				System.out.println(name.substring(id+1, name.length()-21)+"\tmzxml");
				peakmap.put(name.substring(0, name.length()-6), files2[i].getAbsolutePath());
				System.out.println(name.substring(0, name.length()-6)+"\tmzxml");
			}
		}
		
		Iterator <String> it = peakmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			if(pplmap.containsKey(key)){
				String pplfile = pplmap.get(key);
				String peakfile = peakmap.get(key);
				String result = peakfile.replace("mzXML", "RT.ppl");
				
				PplAddRtTask task = new PplAddRtTask(pplfile, peakfile, result);
				while(task.hasNext()){
					task.processNext();
				}
				task.dispose();
			}
		}
	
	}
	
	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException, XMLStreamException {
		// TODO Auto-generated method stub

		long beg = System.currentTimeMillis();
		
		/*String pep = "H:\\NGLYCO_Q\\F001930_20140330_Serum_HILIC_5.ppl";
		String peak = "H:\\NGLYCO_Q\\20140330_Serum_HILIC_5.mzXML";
		String out = "H:\\NGLYCO_Q\\F001923_20140330_Serum_HILIC_5.rt.ppl";
		
		PplAddRtTask task = new PplAddRtTask(pep, peak, out);
		while(task.hasNext()){
			task.processNext();
		}
		task.dispose();*/
		
		PplAddRtTask.batchProcess("J:\\serum glycan quantification\\4th\\20131109-velos\\deglyco", 
				"J:\\serum glycan quantification\\4th\\20131109-velos\\deglyco");

		long end = System.currentTimeMillis();
		System.out.println((end-beg)/1000+" s");
	}
	
}
