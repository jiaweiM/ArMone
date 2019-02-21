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
import java.nio.ByteBuffer;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.IPeptideListIndex;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;

/**
 * Random access the target peptides in peptide list files (ppl & ppls)
 * 
 * @author Xinning
 * @version 0.1, 11-04-2008, 15:26:44
 */
public class PeptideListAccesserBytes extends AbstractPeptideListParser implements
        IPeptideListAccesser {

	public PeptideListAccesserBytes(String listfile) throws FileDamageException,
	        IOException {
		super(listfile);
	}

	public PeptideListAccesserBytes(File file) throws FileDamageException,
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

		this.bfutil.position((int) this.indexes[i].getPeptideStartPosition());
		return this.formatter.parse(this.bfutil.readLine());
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

		IPeptideListIndex index = (IPeptideListIndex) this.indexes[idx];
		int num = index.getNumerofSpectra();
		if (num == 0)
			return null;

		IMS2PeakList[] lists = new IMS2PeakList[num];
		this.bfutil.position(index.getSpectraStartPositions());
		
		ByteBuffer buffer = this.bfutil.getBuffer();
		

		for (int i = 0; i < num; i++) {
			int len = buffer.getInt();
			byte[] bytes = new byte[len];
			buffer.get(bytes);
			
			lists[i] = MS2PeakList.parseBytePeaks(bytes);
		}
		
		return lists;
	}

	public static void main(String args[]) throws FileDamageException,
	        IOException, PeptideParsingException {
		PeptideListAccesserBytes accesser = new PeptideListAccesserBytes("D:\\APIVASEII\\alpha_casein\\crux\\percolator\\ms2\\ms2_percolator.sqt.new.ppl");

		System.out.println(accesser.getPeptide(0));
		
		System.out.println(accesser.getPeakLists(0));
	}
}
