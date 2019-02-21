/* 
 ******************************************************************************
 * File: PeptideListAccesser.java * * * Created on 11-04-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.ILongPeptideListIndex;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.PeakList;

/**
 * Random access the target peptides in peptide list files (ppl & ppls)
 * 
 * @author Xinning
 * @version 0.1.2, 05-02-2010, 11:27:45
 */
public class PeptideListAccesser extends AbstractPeptideListParser implements
        IPeptideListAccesser {
	
	public PeptideListAccesser(){
		
	}

	public PeptideListAccesser(String listfile) throws FileDamageException,
	        IOException {
		super(listfile);
	}

	public PeptideListAccesser(File file) throws FileDamageException,
	        IOException {
		super(file);	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.
	 * IPeptideListAccesser#getPeptide(int)
	 */
	public IPeptide getPeptide(int i) {

		if (i >= this.getNumberofPeptides())
			throw new IndexOutOfBoundsException(
			        "Peptide index exceedes total number of peptides exception: "
			                + i);

		this.bfutil.position((int)this.indexes[i].getPeptideStartPosition());
		String line = this.bfutil.readLine();
		IPeptide pep = this.formatter.parse(line);
		pep.setEnzyme(this.getSearchParameter().getEnzyme());
/*		
		String scanname = pep.getBaseName();
		int beg = pep.getScanNumBeg();
		if(scanname.equals("150mM") && beg>18000 && beg<19000){
			System.out.println("peptidlist58\t"+line+"\t"+pep.getProteinReferences());
			System.out.println(pep.getProteinReferenceString());
		}
*/			
		return pep;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListAccesser#getPeakLists
	 * (int)
	 */
	@Override
	public IMS2PeakList[] getPeakLists(int idx) {
		if (idx >= this.getNumberofPeptides())
			System.err.println(
			        "Peptide index exceedes total number of peptides exception: "
			                + idx+", return null");

		ILongPeptideListIndex index = this.indexes[idx];
		int num = index.getNumerofSpectra();
		if (num == 0)
			return null;

		IMS2PeakList[] lists = new IMS2PeakList[num];
		
		try {
			this.raf.seek(index.getSpectraStartPositions());
			//skip the scan key line
			this.raf.readLine();
	
			for (int i = 0; i < num; i++) {
				lists[i] = PeakList.parsePeaksOneLine(this.raf.readLine());
			}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		return lists;
	}
	
	public int getFileNum(){
		return 1;
	}
	
	public static void main(String [] args) throws FileDamageException, IOException{
		PeptideListAccesser accesser = new PeptideListAccesser("H:\\quatification_data_standard\\" +
				"1_1_1(1)\\0416mousebrain_0mM_2.csv.ppl");
		System.out.println(accesser.getDecoyJudger()==null);
	}
	
}
