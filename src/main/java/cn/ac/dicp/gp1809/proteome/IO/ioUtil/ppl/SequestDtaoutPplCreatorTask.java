/* 
 ******************************************************************************
 * File: SequestDtaoutPplCreatorTask.java * * * Created on 05-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequestFileIllegalException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFile;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.SequestDtaPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.zipdata.ZippedDtaOutUltility;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * For dta and out in a directory of SEQUEST
 * 
 * @author Xinning
 * @version 0.2.1, 05-20-2010, 18:07:19
 */
public class SequestDtaoutPplCreatorTask implements IEmbededPplCreationTask {

	private SequestDtaPeptideReader reader;
	private File file;
	private IPeptideWriter pwriter;

	private IPeptide peptide;
	
	private OutFile curtOut;
	private IMS2PeakList[] curtPeaklist;

	private boolean hasNext = true;
	private boolean closed = false;
	
	private boolean zipanddelafter = true;
	
	/**
	 * For others
	 * 
	 * @param output
	 * @param input
	 * @param topn
	 * @param database
	 * @param type
	 * @param dtaType
	 * @param dtaPath
	 * @throws ImpactReaderTypeException
	 * @throws ClassNotFoundException 
	 * @throws SequestFileIllegalException 
	 * @throws ImpactReaderTypeException 
	 * @throws IOException 
	 * @throws FastaDataBaseException 
	 * @throws ReaderGenerateException
	 */
	public SequestDtaoutPplCreatorTask(String output, String input, int topn,
	        String database, boolean uniq_charge, 
	        IDecoyReferenceJudger judger, HashMap <Character, Character> replaceMap) 
	throws ClassNotFoundException, ImpactReaderTypeException, SequestFileIllegalException, FastaDataBaseException, IOException {

		reader = new SequestDtaPeptideReader(input, database, judger);
		reader.setTopN(topn);
		
		if(replaceMap!=null && replaceMap.size()>0){
			reader.setReplace(replaceMap);
		}
		/*
		 * only for dta & out folder
		 */
		if(!(file = reader.getFile()).isDirectory()) {
			this.zipanddelafter = false;
		}

		ISearchParameter parameter = reader.getSearchParameter();
		
		if(replaceMap!=null && replaceMap.size()>0){
			parameter.restore(replaceMap);
		}

		pwriter = new PeptideListWriter(output, reader.getPeptideFormat(),
		        parameter, judger, uniq_charge, reader.getProNameAccesser());
		
	}
	
	/**
	 * For others
	 * 
	 * @param output
	 * @param input
	 * @param topn
	 * @param database
	 * @param type
	 * @param dtaType
	 * @param dtaPath
	 * @throws ImpactReaderTypeException
	 * @throws ClassNotFoundException 
	 * @throws SequestFileIllegalException 
	 * @throws ImpactReaderTypeException 
	 * @throws IOException 
	 * @throws FastaDataBaseException 
	 * @throws ReaderGenerateException
	 */
	public SequestDtaoutPplCreatorTask(String output, String input, int topn,
	        String database, boolean uniq_charge, IDecoyReferenceJudger judger) 
	throws ClassNotFoundException, ImpactReaderTypeException, SequestFileIllegalException, FastaDataBaseException, IOException {

		reader = new SequestDtaPeptideReader(input, database, judger);
		reader.setTopN(topn);

		/*
		 * only for dta & out folder
		 */
		if(!(file = reader.getFile()).isDirectory()) {
			this.zipanddelafter = false;
		}

		ISearchParameter parameter = reader.getSearchParameter();
		pwriter = new PeptideListWriter(output, reader.getPeptideFormat(),
		        parameter, judger, uniq_charge, reader.getProNameAccesser());
	}

	public SequestDtaoutPplCreatorTask(String output, String input, int topn,
	        String database, boolean uniq_charge, boolean zipanddelafter, 
	        IDecoyReferenceJudger judger, HashMap <Character, Character> replaceMap) throws
	        ImpactReaderTypeException,
	        SequestFileIllegalException, ClassNotFoundException, FastaDataBaseException, IOException {

		reader = new SequestDtaPeptideReader(input, database, judger);
		reader.setTopN(topn);
		
		if(replaceMap!=null && replaceMap.size()>0)
			reader.setReplace(replaceMap);
		
		/*
		 * only for dta & out folder
		 */
		if(!(file = reader.getFile()).isDirectory()) {
			this.zipanddelafter = false;
		}

		ISearchParameter parameter = reader.getSearchParameter();
		if(replaceMap!=null && replaceMap.size()>0)
			parameter.restore(replaceMap);
		
		pwriter = new PeptideListWriter(output, reader.getPeptideFormat(),
		        parameter, judger, uniq_charge, reader.getProNameAccesser());
	}
	
	@Override
	public float completedPercent() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask1#hasNext()
	 */
	@Override
	public boolean hasNext() {

		if (!hasNext)
			return hasNext;

		try {
			hasNext = (peptide = reader.getPeptide()) != null;
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
		}

		if (!hasNext)
			this.dispose();

		return hasNext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask1#processNext()
	 */
	@Override
	public void processNext() {

		try {
		if (this.peptide != null) {

			OutFile out = this.reader.getCurrentOut();
			
			if(this.curtOut != out) {
				this.curtOut = out;
				this.curtPeaklist = new IMS2PeakList[] {this.reader.getCurrentDta().getPeakList()};
			}

			pwriter.write(peptide, this.curtPeaklist);
		} else {
			System.err
			        .println("No next inner task need to be processed or use the hasNext() fist.");
		}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask1#unDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask1#dispose()
	 */
	@Override
	public void dispose() {
		if (!this.closed) {

			if (this.reader != null)
				this.reader.close();

			if (this.pwriter != null)
				try {
					this.pwriter.close();
				} catch (ProWriterException e) {
					throw new RuntimeException(e);
				}

			if(this.zipanddelafter) {
				try {
			           ZippedDtaOutUltility.zipAnddel(this.file);
		           } catch (IOException e) {
			           throw new RuntimeException("Unable to compress and delete the original dta&out!\r\n"+e.getMessage(),e);
		           }
			}	
				
			this.closed = true;
		}
	}

	@Override
	public String toString() {
		return this.pwriter.toString();
	}
	
	public static void main(String [] args) throws ImpactReaderTypeException, SequestFileIllegalException, ClassNotFoundException, FastaDataBaseException, IOException{
		
		String output = "\\\\searcher3\\G\\WFJ\\20120410\\20120410BSA5ug_60mindigestion_2\\MS2_60mindigestion_2.ppl";
		String input = "\\\\searcher3\\G\\WFJ\\20120410\\20120410BSA5ug_60mindigestion_2\\MS2_60mindigestion_2";
//		String input = "\\\\searcher1\\ck\\human-liver-40ug\\general\\ms2";
		int topn = 1;
//        String database = "E:\\ModDataBase\\Phospho\\fasta\\STY_BOZ_2\\ipi.HUMAN.v3.80_Phos_STY_BOZ_2.fasta";
        String database = "\\\\searcher3\\G\\WFJ\\20120410\\bsa.fasta";
		boolean uniq_charge = false;
        IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
        HashMap <Character, Character> replaceMap = new HashMap <Character, Character>();
/*        
        replaceMap.put('B', 'S');
        replaceMap.put('O', 'T');
        replaceMap.put('Z', 'Y');
*/        
		SequestDtaoutPplCreatorTask task = new SequestDtaoutPplCreatorTask(output, input, topn,
				database, uniq_charge, judger, replaceMap);
		
		while(task.hasNext()){
			task.processNext();
		}
		task.dispose();
/*
		String [] ss = new String("REV_IPI:IPI00889744(0146937.2)+1+5+aaa+").split("\\+");
		for(int i=0;i<ss.length;i++){
			System.out.println("mipa\t"+ss[i]);
		}
		System.out.println(ss.length);
//		String [] ss2 = new String("aa+1+3++").split("\\+");
		String [] ss2 = StringUtil.split("aa+1+3++", "+");
		System.out.println(ss2.length);
		for(int i=0;i<ss2.length;i++){
			System.out.println("mipa\t"+ss2[i]);
		}
*/		
	}
	
}
