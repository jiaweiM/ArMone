/* 
 ******************************************************************************
 * File: AbstractFastaAccesser.java * * * Created on 11-18-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

/**
 * The abstract fasta accesser containing mostly used methods for all the
 * inherit
 * 
 * <p>
 * Changes:
 * <li>0.1.3, 04-09-2009:
 * {@link #judgeMostProbableRef(String, FastaIndex, FastaIndex)} to return the
 * most probable reference when the partial name from database search engine is
 * too short.
 * <li>0.1.4, 07-24-2009: Fix bug when all the references are different even
 * when there is only one character left
 * <li>0.2 05-20-2010 add the support to inner decoy symbol
 * 
 * @author Xinning
 * @version 0.2, 05-20-2010, 12:33:02
 */
public abstract class AbstractFastaAccesser implements IFastaAccesser {

	private IDecoyReferenceJudger judger;
	private Indexer indexer;
	private File fastafile;
	protected ByteBuffer database = null;// Buffer of the database
	protected HashMap<String, FastaIndex> index;// index of the protein entry;
	protected FastaIndex[] indexArray;
	private int num_entries;// The total number of protein entries.
	private int splitlen;
	private int splitlenRev;

	/**
	 * If the input is a ProteinReference, and the index of the protein
	 * reference is not set (e.g. for SEQUEST xml or xls exported file), you may
	 * want to renew the index of protein reference for this protein database.
	 * Set this as true, the index of protein reference will be automatically
	 * renewed after the calling of {@link #getSequence(ProteinReference)}.
	 */
	private boolean isRenewReference;

	/**
	 * @param dbname
	 *            name of the fasta file
	 * @throws IOException
	 * @throws FastaDataBaseException
	 */
	protected AbstractFastaAccesser(Indexer indexer, IDecoyReferenceJudger judger)
	        throws FastaDataBaseException, IOException {
		this.indexer = indexer;
		this.judger = judger;
		this.fastafile = new File(indexer.getDatabasePath());
		this.database = indexer.getMappedDatabase();
		this.index = indexer.getIndexMap();
		this.indexArray = indexer.getIndexArray();
		this.num_entries = this.indexArray.length;

		this.splitlen = indexer.getSplitLength();
		this.splitlenRev = indexer.getRevSplitLength();
	}

	/**
	 * Create a fasta accesser.
	 * 
	 * <p>
	 * If the input is a ProteinReference, and the index of the protein
	 * reference is not set (e.g. for SEQUEST xml or xls exported file), you may
	 * want to renew the index of protein reference for this protein database.
	 * Or you may want to format the name of protein so that the length is
	 * minimum. Set this as true, the name and index of protein reference will
	 * be automatically renewed after the calling of
	 * {@link #getSequence(ProteinReference)}.
	 * <p>
	 * <b>Notice: only the index of ProteinReference with original index of 0
	 * (default value) will be renew. And the ProteinReference with index bigger
	 * than 0 will NOT be renewed even though the "isRenewIndex" is set as
	 * true.</b> The default value of isRenewIndex is <b>false</b>.
	 * 
	 * @param dbname
	 *            name of the fasta file
	 * @param isRenewReference
	 *            if renew the protein reference index.
	 * @throws IOException
	 * @throws FastaDataBaseException
	 */
	protected AbstractFastaAccesser(Indexer indexer, IDecoyReferenceJudger judger, boolean isRenewReference)
	        throws FastaDataBaseException, IOException {
		this(indexer, judger);
		this.isRenewReference = isRenewReference;
	}

	/**
	 * The decoy reference judger
	 * 
	 * @return
	 */
	public IDecoyReferenceJudger getDecoyJudger() {
		return this.judger;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser
	 * #getFastaFile()
	 */
	@Override
	public File getFastaFile() {
		return this.fastafile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser
	 * #getSequence(java.lang.String)
	 */
	public ProteinSequence getSequence(String proteinReference)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {
		/*
		 * After database search, the generated reference name often is the
		 * partial name of the protein reference. Thus, use startwith can find
		 * the proper protein and get the full name;
		 */
		String key = this.getKey(proteinReference);

		FastaIndex idx = this.index.get(key);

		// The partial may be too short to be distinguishable
		if (idx == null) {// @see also indexer.indexArray;
			FastaIndex tidx = null;
			for (int i = 0; i < this.indexArray.length; i++) {
				FastaIndex t = indexArray[i];
				if (t.getName().startsWith(proteinReference)) {
					if (tidx == null) {
						tidx = t;
					} else {
						tidx = this.judgeMostProbableRef(proteinReference,
						        tidx, t);
					}
				}
			}

			if (tidx == null)
//				return new ProteinSequence(proteinReference, "Not find!!!!!!");
				throw new ProteinNotFoundInFastaException("Reference: \""
				        + proteinReference
				        + "\" was not found in current Fasta database!");

			idx = tidx;
		}

		return this.getSequence(idx, database);
	}

	/**
	 * In some cases, the given partial name of the protein reference is shorter
	 * than the shortest length to distinguish between each other, use this
	 * strategy to test the most probable protein
	 * 
	 * @param idx1
	 * @param idx2
	 * @return
	 */
	private FastaIndex judgeMostProbableRef(String proteinReference,
	        FastaIndex idx1, FastaIndex idx2) {

		int len = proteinReference.length();
		String ref1 = idx1.getName();
		String ref2 = idx2.getName();
		int len1 = ref1.length();
		int len2 = ref2.length();

//		System.out.println("abstractFA199\t"+proteinReference+"\t###\t"+ref1+"\t@@@\t"+ref2);
		
		
		//equals
		if (len1 <= len)
			return idx1;

		if (len2 <= len)
			return idx2;

		char nextChar1 = ref1.charAt(len);
		char nextChar2 = ref2.charAt(len);

		/*
		 * Most database search algorithm commonly split the reference at the
		 * first space.
		 */
		if (Character.isSpaceChar(nextChar1))
			return idx1;

		if (Character.isSpaceChar(nextChar2))
			return idx2;

		/* throw new MoreThanOneRefFoundInFastaException */
		
		System.out
		        .println("Warning: partial name of protein outputted after database search"
		                + "was too short to justify the full name. More than one protein is found for: "
		                + proteinReference
		                + "\r\n\t1: "
		                + idx1
		                + "\r\n\t2: "
		                + idx2 + "\r\nThe first one will be selected");

		return idx1;
	}

	/**
	 * Here a hashmap is used for the fast getting of target protein sequence;
	 * therefore, a proper key is needed for accurate getting action.
	 * <p>
	 * This method must correspond to the <code>Indexer.parseEntriesToMap</code>.
	 * 
	 * @param partialName_accession
	 * @return
	 */
	protected abstract String getKey(String partialName_accession);

	/*
	 * Get the protein from FastaIndex
	 */
	protected ProteinSequence getSequence(FastaIndex fidx, ByteBuffer database) {
		int start = fidx.getStart();
		int end = fidx.getEnd();
		int len = end - start;

		database.position(start);
		byte[] databyte = new byte[len];
		database.get(databyte, 0, len);

		// Remove the LF and CR char
		StringBuilder sb = new StringBuilder(len);
		for (byte b : databyte) {
			char c = (char) b;
			if (Aminoacids.isAminoacid(c))
				sb.append(c);
			else if(c=='*')
				sb.append('X');
		}

		String seq = sb.toString();
		ProteinSequence sequence = new ProteinSequence(fidx.getName(), seq,
		        fidx.getIndex());

		return sequence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getSequence(int)
	 */
	public ProteinSequence getSequence(int proteinIdx)
	        throws ProteinNotFoundInFastaException {

		if (proteinIdx <= 0 || proteinIdx > this.num_entries) {
			throw new ProteinNotFoundInFastaException(
			        "The index of protein is not valid: " + proteinIdx);
		}

		return this.getSequence(this.indexArray[proteinIdx - 1], database);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getSequence(
	 * cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference)
	 */
	public ProteinSequence getSequence(ProteinReference ref)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {

		int idx = ref.getIndex();
		ProteinSequence pseq;
		if (idx > 0) {
			pseq = this.getSequence(idx);
			if (!pseq.getReference().contains(ref.getName())) {
				System.err
				        .println("The protein with index of "
				                + idx
				                + " is \n\t\""
				                + pseq.getReference()
				                + "\",\n doesn't contain the partial name \n\t\""
				                + ref.getName()
				                + "\".\n The protein contains the partial reference name "
				                + "will be selected.\n May be not the correct database?");

				// If the protein in database with the same index is not start
				// with the partial
				// reference, the protein in database with the partial reference
				// will be select.
				pseq = this.getSequence(ref.getName());

				/*
				 * throw new ProteinNotFoundInFastaException("The protein with
				 * index of "+idx+" is \""+ pseq.getReference()+"\", but not
				 * start with the partial name after database" + "search
				 * \""+ref.getName()+"\".");
				 */
			}
		} else {
			pseq = this.getSequence(ref.getName());
		}

		/*
		 * Renew the reference
		 */
		if (this.isRenewReference)
			this.renewReference(ref, pseq);

		return pseq;
	}

	/**
	 * Renew the ProteinReference by the ProteinSequence. Both the name and the
	 * index will be renewed.
	 * 
	 * @param pref
	 * @param pseq
	 */
	protected final void renewReference(ProteinReference pref,
	        ProteinSequence pseq) {
		if (pseq == null) {
			System.err.println("The input ProteinSequence is null, "
			        + "ProteinReference will not be renewed.");

			return;
		}

		String mininame = pseq.getReference();

		if (this.judger.isDecoy(mininame)) {
			if (mininame.length() > this.splitlenRev)
				mininame = mininame.substring(0, this.splitlenRev);
		} else {
			if (mininame.length() > this.splitlen)
				mininame = mininame.substring(0, this.splitlen);
		}

		pref.setName(mininame);

		if (pseq.index() != pref.getIndex())
			pref.setIndex(pseq.index());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#renewReference
	 * (cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference)
	 */
	@Override
	public void renewReference(ProteinReference ref)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {

		String mininame = ref.getName();

		if (this.judger.isDecoy(mininame)) {
			if (mininame.length() > this.splitlenRev)
				mininame = mininame.substring(0, this.splitlenRev);
		} else {
			if (mininame.length() > this.splitlen)
				mininame = mininame.substring(0, this.splitlen);
		}

		ref.setName(mininame);

		if (ref.getIndex() <= 0) {
			ProteinSequence pseq = this.getSequence(ref.getName());

			ref.setIndex(pseq.index());
		}
	}

	public void setSubRef(IReferenceDetail ref){
		String name = ref.getName();
		
		if (this.judger.isDecoy(name)) {
			if (name.length() > this.splitlenRev)
				name = name.substring(0, this.splitlenRev);
		} else {
			if (name.length() > this.splitlen)
				name = name.substring(0, this.splitlen);
		}

		ref.setSubRef(name);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getSplitLength()
	 */
	public int getSplitLength() {
		return this.splitlen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getSplitRevLength
	 * ()
	 */
	public int getSplitRevLength() {
		return this.splitlenRev;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getNumberofProteins
	 * ()
	 */
	public int getNumberofProteins() {
		return this.num_entries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getNamesofProteins
	 * ()
	 */
	@Override
	public String[] getNamesofProteins() {
		int len = this.indexArray.length;

		String[] names = new String[len];
		for (int i = 0; i < len; i++) {
			names[i] = this.indexArray[i].getName();
		}

		return names;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#close()
	 */
	public void close() {
		this.indexer.close();
		this.database = null;
	}

	/**
	 * Indexer for fasta database for fast reading and finding.
	 * 
	 * @author Xinning
	 * @version 0.4, 05-20-2010, 12:32:48
	 */
	protected static abstract class Indexer {

		/**
		 * If there is no duplicate, split the references with this length
		 */
		private int MIN_LENGTH = 10;
		private IDecoyReferenceJudger judger;

		private ByteBuffer buffer;
		private FileInputStream stream;
		private HashMap<String, FastaIndex> indexMap;
		/*
		 * Array of fasta index containing all protein sequence information.
		 * This is a supplement of indexMap. When can't be found in index map,
		 * references will be searched in this array one by one as the key of
		 * indexMap is the partial name of the reference and there may be some
		 * unpredicted exception especially for mixed protein database manually.
		 */
		private FastaIndex[] indexArray;
		/*
		 * To get a proper key of protein database for protein sequences
		 * generation, the protein name must be splitted to a proper length, and
		 * then it can be easily found in the map.
		 */
		private int splitlen = 1;

		/*
		 * Since the reversed protein name commonly has more words than normal
		 * protein (REVERSED_...), if these two type proteins are computed for
		 * split length, the outputed simple reference by SEQUEST may has less
		 * character than the split length, therefore, they are computed
		 * separately.
		 */
		private int splitlenRev = 1;

		/*
		 * The percentage of protein sequences have been indexed.
		 */
		private int percent_indexed;

		private String dbname;

		protected Indexer(String dbname, IDecoyReferenceJudger judger) throws FastaDataBaseException,
		        IOException {
			
			this.judger = judger;
			try {
				this.dbname = dbname;
				stream = new FileInputStream(dbname);
				buffer = stream.getChannel().map(FileChannel.MapMode.READ_ONLY,
				        0, stream.available());
			} catch (Exception e) {
				throw new IOException("Error in opening the Fasta database: "
				        + dbname, e);
			}
			this.indexArray = this.getDataBaseIndex(buffer);

//			this.interateProperSplitLength(this.indexArray);
		}

		/**
		 * The decoy reference judger
		 * 
		 * @return
		 */
		protected IDecoyReferenceJudger getDecoyRefJudger() {
			return this.judger;
		}
		
		/*
		 * key: subsequence of the protein name. value: the fastaindex.
		 * 
		 * @return IndexMap @throws FastaDataBaseException
		 */
		protected final FastaIndex[] getDataBaseIndex(ByteBuffer buffer)
		        throws FastaDataBaseException {
			String line, reference;
			int start = 0, end, thisp = 0, lastp;
			int sequencelength = 0;
			LinkedList<FastaIndex> list = new LinkedList<FastaIndex>();

			BufferUtil database = new BufferUtil(buffer);
			int length = database.length();

			line = database.readLine();
			start = database.position();
			if (line.charAt(0) != '>')
				throw new FastaDataBaseException(
				        "It doesn't seem like to be a Fasta database.");

			System.out.print("Indexing Database : 0%\r");
			reference = line.substring(1);
			long threshold = -1L;
			while ((line = database.readLine()) != null) {
				int tlen = line.length();
				// Skip the blank lines
				if (tlen != 0) {
					lastp = thisp;
					thisp = database.position();
					if (line.charAt(0) == '>') {
						end = lastp;
						list.add(new FastaIndex(list.size() + 1, reference,
						        start, end, sequencelength));

						start = thisp;
						reference = line.substring(1);
						sequencelength = 0;

						long percent = start * 100L / length;
						if (percent > threshold) {
							this.percent_indexed = (int) percent;
							System.out.print("Indexing Database : " + percent
							        + "%\r");
							threshold++;
						}
					} else
						sequencelength += tlen;
				}
			}
			// last one
			list.add(new FastaIndex(list.size() + 1, reference, start, length,
			        sequencelength));
			this.percent_indexed = 100;
			FastaIndex[] indexArray = list.toArray(new FastaIndex[list.size()]);

			System.out.println("Indexing Database : 100%.");
			System.out.println("Finished indexing the database.");

			return indexArray;
		}

		/**
		 * For convenience, protein names are often split into short strings at
		 * the start position of the protein name (e.g. "sp|P02769|ALBU_BOVIN
		 * Serum albumin precursor" ---> "sp|P02769|"). This method try to
		 * generate the minimum length of the string to present all the proteins
		 * in database without duplication.
		 * 
		 * @throws FastaDataBaseException
		 */
		private void interateProperSplitLength(FastaIndex[] idxArray)
		        throws FastaDataBaseException {
			int len = idxArray.length;
			HashSet<String> nameSet = new HashSet<String>(len / 2);
			HashSet<String> revNameSet = new HashSet<String>(len / 2);

			for (int i = 0; i < len; i++) {
				String name = idxArray[i].getName();
				if (this.judger.isDecoy(name)) {
					if (revNameSet.contains(name))
						throw new FastaDataBaseException(
						        "Error: more than one proteins in this database named: "
						                + name);
					revNameSet.add(name);
				} else {
					if (nameSet.contains(name))
						throw new FastaDataBaseException(
						        "Error: more than one proteins in this database named: "
						                + name);
					nameSet.add(name);
				}
			}

			String[] names = nameSet.toArray(new String[nameSet.size()]);
			String[] revNames = revNameSet
			        .toArray(new String[revNameSet.size()]);

			if (names.length != 0) {
				int dupe = this.isDuplicate(splitlen, names);
				/*
				 * No duplicate ones even though only one character left, this
				 * is rarely case when the database is constructed by oneself.
				 */
				if (dupe == -1) {
					splitlen = this.MIN_LENGTH;
				} else {
					do {
						splitlen += dupe;
					} while (dupe == this.isDuplicate(splitlen, names));

					if (dupe == -1)
						splitlen++;
				}
			}

			if (revNames.length != 0) {

				/*
				 * No duplicate ones even though only one character left, this
				 * is rarely case when the database is constructed by oneself.
				 */
				if (revNames.length == 1) {
					splitlenRev = this.MIN_LENGTH + 4;
				} else {

					int dupe = this.isDuplicate(splitlenRev, revNames);

					do {
						splitlenRev += dupe;
					} while (dupe == this.isDuplicate(splitlenRev, revNames));

					if (dupe == -1)
						splitlenRev++;
				}
				
				/*
				 * Make sure the decoy symbol is included
				 */
				this.splitlenRev = this.getMinLen4Decoy(revNames, splitlenRev);
			}
		}

		/**
		 * For fast finding, we always put the object for finding into a map
		 * with proper keys. However, as sequest outputted protein reference is
		 * often with different reference length. Therefore, a proper key should
		 * be with the smallest length of reference name.
		 * <p>
		 * <b>Override this method to generate map with different type of
		 * keys</b>
		 * 
		 */
		protected abstract HashMap<String, FastaIndex> parseEntriesToMap(
		        FastaIndex[] idxArray);

		/**
		 * If contains duplicated entries for current split length, return 1.
		 * Otherwise return -1.
		 * 
		 * @param splitlen
		 * @param names
		 * @return
		 * @throws FastaDataBaseException
		 */
		private int isDuplicate(int splitlen, String[] names)
		        throws FastaDataBaseException {
			int len = names.length;
			HashSet<String> set = new HashSet<String>(len);
			for (String name : names) {
				if (name.length() > splitlen) {
					name = name.substring(0, splitlen);
					if (set.contains(name))
						return 1;
				}
				/*
				 * Commonly, a fasta database downloaded from other websites
				 * should be with protein references in the same format (e.g.
				 * IPI NCBI and so on). But when the database is created
				 * manually, there may be with protein reference whose length is
				 * less than the split point, but this is not the matter,
				 * because there should be no duplaceted full name after check
				 * of the redundancy.
				 */
				// if(name.length()<=splitlen)
				set.add(name);
			}
			return -1;
		}

		/**
		 * Justify the min length for the deocy proteins. This is because when the deocy symbol 
		 * is not the start of the protein reference, the decoy symbol may be not included in the 
		 * mini reference even though the references are unduplicated. Use this method to make sure 
		 * the decoy symbol is included.
		 * 
		 * @param names
		 * @param min_undup_len
		 * @return
		 */
		private int getMinLen4Decoy(String[] names, int min_undup_len) {
			int max_idx = 0;
			for(String name : names) {
				int idx = this.judger.endIndexof(name);
				if(idx > max_idx)
					max_idx = idx;
			}
			
			return max_idx > min_undup_len ? max_idx : min_undup_len;
		}
		
		/**
		 * Get index of protein in this fasta file; key: substring of the
		 * protein name. value: fastaindex
		 * 
		 * @return index map
		 */
		protected synchronized HashMap<String, FastaIndex> getIndexMap() {
			if (this.indexMap == null) {
				System.out.println("Building reference index ...");
				this.indexMap = this.parseEntriesToMap(indexArray);
				System.out.println("Finished building reference index.");
			}

			return this.indexMap;
		}

		/**
		 * Array of fasta index containing all protein sequence information.
		 * This is a supplement of indexMap. When can't be found in index map,
		 * references will be searched in this array one by one as the key of
		 * indexMap is the partial name of the reference and there may be some
		 * unpredicted exception especially for mixed protein database manually.
		 * (<b>In a mixed protein database, if the outputted partial reference
		 * from SEQUEST is disciminatable but with length less than the split
		 * length, it can't be get() from index map (as the key in map is the
		 * partial reference with split length) (split length) </b>)
		 */
		protected FastaIndex[] getIndexArray() {
			return this.indexArray;
		}

		/**
		 * @return buffered fasta file;
		 */
		protected ByteBuffer getMappedDatabase() {
			return this.buffer;
		}

		/**
		 * Since SEQUEST often output protein short names with uncertain length,
		 * therefore, the length of key is need to be determined. After split
		 * the protein reference into short, the short value can be used to
		 * generated protein informations from the index map. <b>Very important
		 * value.</b>
		 * 
		 * @return The point where the protein sequence should be split from the
		 *         beginning.
		 */
		protected int getSplitLength() {
			return this.splitlen;
		}

		/**
		 * Since the reversed protein name commonly has more words than normal
		 * protein (REVERSED_...), if these two type proteins are computed for
		 * split length, the outputed simple reference by SEQUEST may has less
		 * character than the split length, therefore, they are computed
		 * separately.
		 * 
		 * @return The point where the reverse protein sequence should be split
		 *         from the beginning.
		 */
		protected int getRevSplitLength() {
			return this.splitlenRev;
		}

		/**
		 * The percentage of protein sequences haven been indexed.
		 * 
		 * @return
		 */
		protected int getPercent_Indexed() {
			return this.percent_indexed;
		}

		/**
		 * The path of the database.
		 * 
		 * @return
		 */
		protected String getDatabasePath() {
			return this.dbname;
		}

		/**
		 * Close the indexer and therefore close the accesser
		 */
		protected void close() {
			try {
				this.stream.close();
			} catch (IOException e) {
				System.err
				        .println("Error while closing the file, but it doesn't matter :)");
			}
			this.buffer = null;
		}
	}

	/**
	 * The fasta index. This is a basic unit of the fasta database containing
	 * protein name, start position and end position of the sequence, as well as
	 * the sequence length.
	 * 
	 * @author Xinning
	 * @version 0.2, 05-11-2008, 14:09:23
	 */
	protected static class FastaIndex {
		private int idx;
		private String name;
		private int start, end;
		private int seqlen;

		/**
		 * 
		 * 
		 * @param idx
		 *            index of protein in database, from 1 - n
		 * @param name
		 * @param start
		 * @param end
		 * @param seqlen
		 */
		FastaIndex(int idx, String name, int start, int end, int seqlen) {
			this.idx = idx;
			this.name = name;
			this.start = start;
			this.end = end;
			this.seqlen = seqlen;
		}

		/**
		 * Index of protein in database, from 1 - n
		 * 
		 * @return
		 */
		public int getIndex() {
			return this.idx;
		}

		/**
		 * @return the name of this protein
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the start of this protein sequence in the fasta file.
		 *         <b>Don't contains the protein name</b>
		 */
		public int getStart() {
			return start;
		}

		/**
		 * @return the end position of this protein sequence in the fasta file
		 */
		public int getEnd() {
			return end;
		}

		/**
		 * @return the seqlen of this protein sequence (the number of aa != end
		 *         - start).
		 */
		public int getSeqlen() {
			return seqlen;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.name).append("; ").append("StartP: ").append(
			        this.start).append("; EndP: ").append(this.end).append(".");
			return sb.toString();
		}
	}

}
