/*
 * *****************************************************************************
 * File: SequestDtaPeptideReader.java * * * Created on 08-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.zip.ZipException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequestFileIllegalException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.IO.sequest.ISequestReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestDtaOutReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFile;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFileReadingException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFile.Hit;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.zipdata.ZippedDtaOutReader;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * The reader of peptide and Dta out. This class will not be automatically used by the peptide reader factory
 * 
 * @see OutFilePeptideReader
 * @author Xinning
 * @version 0.1.1, 08-08-2009, 14:47:01
 */
public class SequestDtaPeptideReader extends AbstractSequestPeptideReader {

	private ISequestReader breader;

	private int curtPepIdx = 1;// current peptide count in an out file, for
	// more than one peptide accepted in an out file
	private int curtPepCount;

	// Current peptide list from out file
	private SequestPeptide[] curtPeptides = new SequestPeptide[0];

	// The current out file
	private OutFile curtOut;

	private SequestParameter parameter;
	
	private IFastaAccesser accesser;
	
	private ProteinNameAccesser proNameAccesser;

	public SequestDtaPeptideReader(String foldername, IFastaAccesser accesser)
	        throws ImpactReaderTypeException, SequestFileIllegalException, ZipException, ClassNotFoundException{
		this(new File(foldername), accesser);
	}

	public SequestDtaPeptideReader(File folder, IFastaAccesser accesser)
	        throws ImpactReaderTypeException, SequestFileIllegalException, ZipException, ClassNotFoundException{
		this(folder.isDirectory() ? new SequestDtaOutReader(folder, accesser) : new ZippedDtaOutReader(folder, accesser));
	}

	/**
	 * Create a ProReader from BatchOutReader
	 * 
	 * @param breader
	 * @throws ImpactReaderTypeException
	 */
	public SequestDtaPeptideReader(ISequestReader breader)
	        throws ImpactReaderTypeException {
		super(breader.getFile());

		this.breader = breader;
		this.parameter = breader.getSearchParameters();
		this.accesser = parameter.getFastaAccesser(getDecoyJudger());
		this.proNameAccesser = new ProteinNameAccesser(accesser);

		if (this.breader.getNumberofOutFiles() == 0)
			throw new ImpactReaderTypeException("OutReader unsuit Exception");
	}
	
	public SequestDtaPeptideReader(String foldername, String fasta, IDecoyReferenceJudger judger)
    		throws ImpactReaderTypeException, SequestFileIllegalException, ClassNotFoundException, FastaDataBaseException, IOException{
		this(new File(foldername), fasta, judger);
	}

	public SequestDtaPeptideReader(File folder, String fasta, IDecoyReferenceJudger judger)
			throws ImpactReaderTypeException, SequestFileIllegalException, ClassNotFoundException, FastaDataBaseException, IOException{
		this(folder.isDirectory() ? new SequestDtaOutReader(folder, new FastaAccesser(fasta, judger)) : 
			new ZippedDtaOutReader(folder, new FastaAccesser(fasta, judger)), 
				fasta, judger);
	}

	/**
	 * Create a ProReader from BatchOutReader
	 * 
	 * @param breader
	 * @throws ImpactReaderTypeException
	 * @throws IOException 
	 * @throws FastaDataBaseException 
	 */
	public SequestDtaPeptideReader(ISequestReader breader, String fasta, IDecoyReferenceJudger judger)
    		throws ImpactReaderTypeException, FastaDataBaseException, IOException {
		
		super(breader.getFile());

		this.breader = breader;
		this.accesser = new FastaAccesser(fasta, judger);
		this.parameter = breader.getSearchParameters();
		this.parameter.setFastaAccesser(accesser);
		this.setDecoyJudger(judger);
		this.proNameAccesser = new ProteinNameAccesser(accesser);

		if (this.breader.getNumberofOutFiles() == 0)
			throw new ImpactReaderTypeException("OutReader unsuit Exception");
	}

	@Override
	protected SequestPeptide getPeptideImp() throws PeptideParsingException {
		if (this.curtPepIdx >= this.curtPepCount) {
			this.curtPepIdx = 0;

			try {
				this.curtOut = this.breader.getNextOut();
			} catch (OutFileReadingException e) {
				throw new PeptideParsingException(
				        "Error in reading the out file: "
				                + this.breader.getNameofCurtOutFile()
				                + " for reading peptides.", e);
			}

			if (this.curtOut == null) {// End of out files
				return null;
			}

			Hit[] hits = this.curtOut.getHits(this.getTopN());

			if (hits == null) {
				this.curtPepCount = 0;
				return this.getPeptideImp();
			}

			this.curtPepCount = hits.length;
			this.curtPeptides = this.getPeptide(hits, this.curtOut);
		}

		SequestPeptide pep = this.curtPeptides[this.curtPepIdx++];
		
		HashSet <ProteinReference> refsets = pep.getProteinReferences();
		Iterator <ProteinReference> it = refsets.iterator();
		HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
		while(it.hasNext()){
			
			ProteinReference ref = it.next();
			String partName = ref.getName();

			if(ref.isDecoy()){
				partName = partName.substring(0, accesser.getSplitRevLength());
			}else{
				partName = partName.substring(0, accesser.getSplitLength());
			}
			
			try {
				
				ProteinSequence pseq = accesser.getSequence(ref);
				this.proNameAccesser.addRef(partName, pseq);

				String pepseq = PeptideUtil.getUniqueSequence(pep.getSequence());
				int beg = pseq.indexOf(pepseq)+1;
				int end = beg + pepseq.length() - 1;
				
				int preloc = beg-8<0 ? 0 : beg-8;
				String pre = pseq.getAASequence(preloc, beg-1);
				
				String next = "";
				if(end<pseq.length()){
					int endloc = end+7>pseq.length() ? pseq.length() : end+7;
					next = pseq.getAASequence(end, endloc);
				}

				SeqLocAround sla = new SeqLocAround(beg, end, pre, next);
				locAroundMap.put(ref.toString(), sla);
				
			} catch (ProteinNotFoundInFastaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MoreThanOneRefFoundInFastaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		pep.setPepLocAroundMap(locAroundMap);
		return pep;
	}

	/**
	 * Create a peptide instance.
	 * 
	 * @param hit
	 * @return
	 */
	private SequestPeptide[] getPeptide(Hit[] hits, OutFile curtout) {

		int siz = hits.length;
		SequestPeptide[] peps = new SequestPeptide[siz];

		for (int i = 0; i < siz; i++) {
			Hit hit = hits[i];

			SequestScanName scanname = curtout.getscanName();
			String sequence = this.parameter.parsePeptide(hit.getSequence());
			
			HashSet<ProteinReference> refs = hit.getRefs();
			//The deocy should be assigned
			this.parseDecoy(refs);
			
			SequestPeptide pep = new SequestPeptide(scanname, sequence,
			        scanname.getCharge(), hit.getRsp(), hit.getMh(), curtout
			                .getExperimentMH()
			                - hit.getMh(), hit.getRank(), hit
			                .getDcn_from_next_xcorr(), hit.getXcorr(), hit
			                .getSp(), hit.getIons(), hit.getRefs(), this
			                .getPeptideFormat());

			peps[i] = pep;
		}

		return peps;
	}
	
	private void parseDecoy(HashSet<ProteinReference> refs) {
		for(ProteinReference ref : refs) {
			ref.setDecoy(this.getDecoyJudger().isDecoy(ref.getName()));
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.ProReader#close()
	 */
	public void close() {
		this.breader.close();

		System.out.println("Finished reading.");
	}

	/**
	 * The current outfile. In order to calculate the true value this method
	 * should be invoked after the calling of {@link #getPeptide()}.
	 * 
	 * @return
	 */
	public OutFile getCurrentOut() {
		return this.curtOut;
	}

	/**
	 * The current Dta file. 
	 * 
	 * @return
	 * @throws DtaFileParsingException
	 */
	public SequestScanDta getCurrentDta() throws DtaFileParsingException {
		
		if(this.curtOut == null)
			return null;
		
		return this.breader.getDtaFileForOut(this.curtOut, true);
	}

	public ProteinNameAccesser getProNameAccesser(){
		return this.proNameAccesser;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#getSearchParameter
	 * ()
	 */
	@Override
	public SequestParameter getSearchParameter() {
		return this.parameter;
	}

	public static void main(String[] args) throws Exception {
		
//		String dir = "F:\\data\\best_label\\50.zip.ame";
//		SequestDtaPeptideReader reader = new SequestDtaPeptideReader(dir);
//		System.out.println(reader.getSearchParameter().getVariableInfo().getModficationDescription());
		
//		SequestPeptide pep;
//		while ((pep = reader.getPeptide()) != null) {
//			System.out.println(pep.getInten());
//		}

//		reader.close();
	}
}
