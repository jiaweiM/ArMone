/* 
 ******************************************************************************
 * File: TriTofPepListCreatorTask.java * * * Created on 2012-9-29
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers.MascotCSVPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;

/**
 * @author ck
 *
 * @version 2012-9-29, 9:53:47
 */
public class TriTofPepListCreatorTask implements IPplCreationTask {
	
	private MgfReader mgfReader;
	private IPeptideReader reader;
	private IPeptideWriter pwriter;
	
	private IPeptide peptide;
	private boolean closed = false;
	private boolean end = false;
	
	private HashMap <String, HashSet <IPeptide>> pepMap;
	private HashMap <String, HashSet<ProteinReference>> refMap;
	private HashMap <String, HashMap <String, SeqLocAround>> seqLocMap;

	public TriTofPepListCreatorTask(String output, String input, int topn,
	        String database, String reg, String dtaPath,  
	        boolean uniq_charge, IDecoyReferenceJudger judger) throws DtaFileParsingException, 
	        FileDamageException, ModsReadingException, InvalidEnzymeCleavageSiteException, 
	        IOException, FastaDataBaseException{
		
		this.mgfReader = new MgfReader(dtaPath);
		
		this.pepMap = new HashMap <String, HashSet <IPeptide>>();
		this.refMap = new HashMap <String, HashSet<ProteinReference>>();
		this.seqLocMap = new HashMap <String, HashMap <String, SeqLocAround>>();
		
		this.reader = new MascotCSVPeptideReader(input, database, reg, judger);
		this.reader.setTopN(topn);
		
		ISearchParameter parameter = reader.getSearchParameter();
		pwriter = new PeptideListWriter(output, reader.getPeptideFormat(),
		        parameter, judger, uniq_charge, reader.getProNameAccesser());
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		try {

			boolean has = (peptide = this.reader.getPeptide()) != null;

			if (has) {

				return true;
			} else {
				if (!this.end) {
					this.end = true;
					return true;
				} else
					return false;
			}
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		// TODO Auto-generated method stub

		if(end){

			MS2Scan scan = null;
			
			while((scan=this.mgfReader.getNextMS2Scan())!=null){

				String scannum = scan.getScanName().getBaseName();

				if(pepMap.containsKey(scannum)){

					HashSet <IPeptide> pepset = pepMap.get(scannum);
					Iterator <IPeptide> pepit = pepset.iterator();
					while(pepit.hasNext()){
						
						IPeptide peptide = pepit.next();
						
						String key = PeptideUtil.getUniqueSequence(peptide.getSequence());
						
						peptide.setProteinReference(this.refMap.get(key));
						peptide.setPepLocAroundMap(this.seqLocMap.get(key));
						
						IMS2PeakList peaklist = scan.getPeakList();
						
						if(peptide.getRetentionTime()<=0){
							peptide.setRetentionTime(peaklist.getPrecursePeak().getRT());
						}
						
						if(peptide.getInten()<=0){
							peptide.setInten(peaklist.getPrecursePeak().getIntensity());
						}
						peaklist.sort();
						pwriter.write(peptide, new IMS2PeakList []{peaklist});
					}
				}
			}
			
		}else{
			
			if(peptide!=null){
				
				String key = PeptideUtil.getUniqueSequence(peptide.getSequence());

				if(refMap.containsKey(key)){
					
					HashSet <ProteinReference> refset = refMap.get(key);
					HashMap <String, SeqLocAround> locMap = seqLocMap.get(key);
					
					refset.addAll(peptide.getProteinReferences());
					locMap.putAll(peptide.getPepLocAroundMap());
	
				}else{

					HashSet <ProteinReference> refset = new HashSet <ProteinReference>();
					HashMap <String, SeqLocAround> locMap = new HashMap <String, SeqLocAround>();
					
					refset.addAll(peptide.getProteinReferences());
					locMap.putAll(peptide.getPepLocAroundMap());
					
					refMap.put(key, refset);
					seqLocMap.put(key, locMap);
				}
				
				if(pepMap.containsKey(peptide.getBaseName())){

					pepMap.get(peptide.getBaseName()).add(peptide);
					
				}else{
					
					HashSet <IPeptide> pepset = new HashSet <IPeptide>();
					pepset.add(peptide);
					pepMap.put(peptide.getBaseName(), pepset);
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
			if (this.mgfReader != null)
				this.mgfReader.close();

			if (this.reader != null)
				this.reader.close();

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
	
	public static void batchWrite(String in, String database, String accession_regex, IDecoyReferenceJudger judger) 
			throws ReaderGenerateException, ImpactReaderTypeException, DtaFileParsingException, 
			IOException, FastaDataBaseException, FileDamageException, ModsReadingException, InvalidEnzymeCleavageSiteException{
		
		File [] files = (new File(in)).listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("csv"))
					return true;
				return false;
			}
			
		});
		
		for(int i=0;i<files.length;i++){
			String path = files[i].getAbsolutePath();
			String name = files[i].getName();
			String mgf = path.replace("csv", "mgf");
			String [] ss = name.split("_");
			String subname = "";
			for(int j=0;j<ss.length;j++){
				if(ss[j].startsWith("F")){
					subname = ss[j];
					break;
				}
			}
			String out = path.replace(name, subname);
			TriTofPepListCreatorTask task = new TriTofPepListCreatorTask(out, path, 1, database, accession_regex,
					mgf, false, judger);
			
			while(task.hasNext()){
				task.processNext();
			}
			task.dispose();
			System.gc();
		}
	}
	
	/**
	 * @param args
	 * @throws FastaDataBaseException 
	 * @throws IOException 
	 * @throws DtaFileParsingException 
	 * @throws ImpactReaderTypeException 
	 * @throws ReaderGenerateException 
	 * @throws InvalidEnzymeCleavageSiteException 
	 * @throws ModsReadingException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws ReaderGenerateException, ImpactReaderTypeException, DtaFileParsingException, IOException, FastaDataBaseException, FileDamageException, ModsReadingException, InvalidEnzymeCleavageSiteException {
		// TODO Auto-generated method stub
		
		String database = "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta";
		
		String accession_regex = "\\([^|]*\\)";
		IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		
		String in = "\\\\searcher7\\E\\BYY\\BYY-RP-RP\\Mascot-CSV\\5600";
		TriTofPepListCreatorTask.batchWrite(in, database, accession_regex, judger);
	}
	
}
