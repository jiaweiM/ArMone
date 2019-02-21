/* 
 ******************************************************************************
 * File: AbstractPeptideListParser.java * * * Created on 11-04-2008
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
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import cn.ac.dicp.gp1809.exceptions.AppendedObjectException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter.DefaultPeptideListHeader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter.ListDetails;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter.ListDetails2;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter.LongListDetails;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.ILongPeptideListIndex;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.IPeptideListHeader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.IPeptideListIndex;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;
import cn.ac.dicp.gp1809.util.ioUtil.ParameterAppender;
import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

/**
 * The peptide list parser of the peptide list file. Containing the main
 * validating methods and method for parsing the parameter file. This class
 * contains no methods for detail usage, therefore can be used as global parsers
 * for peptide list file.
 * 
 * @author Xinning
 * @version 0.2.2, 05-20-2010, 15:57:21
 */
abstract class AbstractPeptideListParser {

	protected static final String lineSeparator = IOConstant.lineSeparator;

	/**
	 * The file of peptide list.
	 */
	protected final File file;

	/**
	 * BufferUtil for peptide list file reading
	 */
	protected final BufferUtil bfutil;

	/**
	 * The file reader behind the channel
	 */
	protected final RandomAccessFile raf;

	private FileChannel channel;

	/**
	 * The formatter for peptides
	 */
	protected IPeptideFormat<?> formatter;

	/**
	 * Type of the peptides
	 */
	protected PeptideType type;

	/**
	 * The search parameter
	 */
	protected ISearchParameter parameter;
	
	/**
	 * The decoy reference judger
	 */
	protected IDecoyReferenceJudger judger;

	/**
	 * The peptide indexes.
	 */
	protected ILongPeptideListIndex[] indexes;
	
	protected ProteinNameAccesser proNameAccesser;
	
	public AbstractPeptideListParser(){
		this.file = null;
		this.bfutil = null;
		this.raf = null;
	}
	
	public AbstractPeptideListParser(String listfile)
	        throws FileDamageException, IOException {
		this(new File(listfile));
	}

	public AbstractPeptideListParser(File file) throws FileDamageException,
	        IOException {
		this.file = file;

		// Parse the parameter
		try {
			Object obj = ParameterAppender.readAppendedObject(file);
			
			//Compatibility of old version
			boolean old_list_details = obj instanceof ListDetails;
			if(old_list_details) {
				ListDetails details = (ListDetails) obj;
				this.parameter = details.getSearchParameter();
				this.formatter = details.getFormat();
				IPeptideListIndex[] idxs = details.getPeptideIndexes();
				int len = idxs == null ? 0 : idxs.length;
				this.indexes = new ILongPeptideListIndex[len];
				
				for(int i = 0; i< len; i++) {
					this.indexes[i] = idxs[i].toLongPeptideListIndex();
				}
			}
			else {
				LongListDetails details = (LongListDetails) obj;
				this.parameter = details.getSearchParameter();
				this.formatter = details.getFormat();
				this.indexes = details.getPeptideIndexes();
				this.proNameAccesser = details.getProNameAccesser();
			}

			/*
			 * For compatible with old version and new version
			 */
			if(obj instanceof ListDetails2) {
				this.judger = ((ListDetails2)obj).getDecoyJudger();
			}
			else {
				this.judger = new DefaultDecoyRefJudger();
			}
			
		} catch (AppendedObjectException e) {
			throw new FileDamageException(
			        "Unable to parse the search parameter, may be damaged?", e);
		}

		raf = new RandomAccessFile(file, "r");
		int endp = this.preRead();
		this.channel = raf.getChannel();
		MappedByteBuffer buffer = this.channel.map(
		        FileChannel.MapMode.READ_ONLY, 0, endp);
		this.bfutil = new BufferUtil(buffer);
	}

	/**
	 * Pre- read and parse the necessary informations.
	 * 
	 * @throws FileDamageException
	 * @return the end position of the peptide lists
	 * @throws IOException
	 */
	protected int preRead() throws FileDamageException, IOException {

		String firstline = this.raf.readLine();

		if (firstline == null || firstline.length() == 0) {
			close();
			throw new FileDamageException("PPL file may be damaged!");
		}

		StringBuilder sb = new StringBuilder(100);
		try {

			IPeptideListHeader header = DefaultPeptideListHeader
			        .parseHeader(firstline);

			type = header.getPeptideType();
			this.raf.readLine();

			sb.append("Ppl file name: \"").append(this.getFile().getName())
			        .append("\"").append(lineSeparator);
			sb.append(header.getDescription()).append(lineSeparator);

			if (this.indexes != null && this.indexes.length > 0) {
				this.raf.seek(indexes[this.indexes.length - 1]
				        .getPeptideStartPosition());
				this.raf.readLine();
			}

		} catch (Exception e) {
			close();
			throw new FileDamageException(
			        "Illegal peptide formatter, ppl file may be damaged!", e);
		}

		sb.append("Ready for parsing peptides ... ...");
		System.out.println(sb.toString());

		return (int) this.raf.getFilePointer();
	}

	/**
	 * Get the file behind this peptide reader, in which all peptides are
	 * included.
	 * 
	 * @return
	 */
	public File getFile() {
		return this.file;
	}

	public String getFileName(){
		return this.file.getName();
	}
	/**
	 * Get the database search parameter for this peptide reader.
	 * 
	 * @return
	 */
	public ISearchParameter getSearchParameter() {
		return this.parameter;
	}
	
	/**
	 * The judger of the decoy system
	 * 
	 * @return
	 */
	public IDecoyReferenceJudger getDecoyJudger() {
		return this.judger;
	}

	/**
	 * Get the peptide format used for the peptide list string formating
	 * 
	 * @return
	 */
	public IPeptideFormat getPeptideFormat() {
		return this.formatter;
	}

	/**
	 * Get the peptide type for this reader.
	 * 
	 * @see PeptideType
	 * @return
	 */
	public PeptideType getPeptideType() {
		return this.type;
	}

	public ProteinNameAccesser getProNameAccesser(){
		return this.proNameAccesser;
	}
	
	/**
	 * Close the peptide list parser for reading.
	 * 
	 * @throws IOException
	 */
	public void close() {
		indexes = null;
		this.bfutil.close();
		try {
			if (this.channel != null) {
				this.channel.close();
				this.channel = null;
			}

			if (this.raf != null) {
				this.raf.close();
			}
		} catch (Exception e) {
			System.err.println("Error when closing the file.");
		}

		System.gc();
	}

	/**
	 * The number of peptides in the peptide list
	 * 
	 * @return
	 */
	public int getNumberofPeptides() {
		return this.indexes.length;
	}
}
