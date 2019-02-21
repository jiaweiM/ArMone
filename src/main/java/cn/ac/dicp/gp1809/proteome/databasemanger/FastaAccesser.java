/*
 * *****************************************************************************
 * File: FastaAccesser.java * * * Created on 12-11-2007
 * 
 * Copyright (c) 2007 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.IOException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * Random accessor for fasta protein database file. Sequence, full name, and
 * index of protein can be generated from protein reference name.
 * 
 * @author Xinning
 * @version 0.5.1, 05-20-2010, 14:37:10
 */
public class FastaAccesser extends AbstractFastaAccesser {

//	private static final IDecoyReferenceJudger DEFAULT_DECOY_JUDGER = new DefaultDecoyRefJudger();
	
	/**
	 * @param dbname
	 *            name of the fasta file
	 * @throws IOException
	 * @throws FastaDataBaseException
	 */
/*	
	public FastaAccesser(String dbname) throws FastaDataBaseException,
	        IOException {
		super(new Indexer(dbname), DEFAULT_DECOY_JUDGER);
	}
*/	

	/**
	 * @param dbfile
	 *            name of the fasta file
	 * @throws IOException
	 * @throws FastaDataBaseException
	 */
/*	
	public FastaAccesser(File dbfile) throws FastaDataBaseException,
	        IOException {
		this(dbfile.getAbsolutePath());
	}
*/

	/**
	 * @param dbname
	 *            name of the fasta file
	 * @param djudger the decoy reference judger
	 * @throws IOException
	 * @throws FastaDataBaseException
	 */
	public FastaAccesser(String dbname, IDecoyReferenceJudger djudger) throws FastaDataBaseException,
	        IOException {
		super(new Indexer(dbname, djudger), djudger);
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
	 * than 0 will NOT be renewed even though the "isRenewIndex" is set as true.</b>
	 * The default value of isRenewIndex is <b>false</b>.
	 * 
	 * @param dbname
	 *            name of the fasta file
	 * @param isRenewReference
	 *            if renew the protein reference index.
	 * @throws IOException
	 * @throws FastaDataBaseException
	 */
	public FastaAccesser(String dbname, IDecoyReferenceJudger djudger, boolean isRenewReference)
    		throws FastaDataBaseException, IOException {
		super(new Indexer(dbname, djudger), djudger, isRenewReference);
	}

	/*
	 * The key is the minimum starting part of the full protein name.
	 * 
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.
	 * AbstractFastaAccesser#getKey(java.lang.String)
	 */
	@Override
    protected String getKey(String partialName_accession) {

		/*
		 * After database search, the generated reference name often is the
		 * partial name of the protein reference. Thus, use startwith can find
		 * the proper protein and get the full name;
		 */
		String key = partialName_accession;

		if (getDecoyJudger().isDecoy(partialName_accession)) {
			if (partialName_accession.length() > this.getSplitRevLength())
				key = partialName_accession.substring(0, this.getSplitRevLength());
		} else {
			if (partialName_accession.length() > this.getSplitLength())
				key = partialName_accession.substring(0, this.getSplitLength());
		}

	    return key;
    }
	
	
	

	/**
	 * Indexer for fasta database for fast reading and finding.
	 * 
	 * @author Xinning
	 * @version 0.4, 11-18-2008, 15:25:30
	 */
	private static class Indexer extends AbstractFastaAccesser.Indexer{

		private Indexer(String dbname, IDecoyReferenceJudger djudger) throws FastaDataBaseException,
		        IOException {
			super(dbname, djudger);
		}

		/**
		 * For fast finding, we always put the object for finding into a map
		 * with proper keys. However, as sequest outputted protein reference is
		 * often with different reference length. Therefore, a proper key should
		 * be with the smallest length of reference name.
		 * <p>
		 * <b>Override this method to generate map with different type of keys</b>
		 * 
		 */
		@Override
		protected HashMap<String, FastaIndex> parseEntriesToMap(
		        FastaIndex[] idxArray){
			int len = idxArray.length;
			int splitlenRev = super.getRevSplitLength();
			int splitlen = super.getSplitLength();
			
			HashMap<String, FastaIndex> map = new HashMap<String, FastaIndex>(
			        len);
			for (int i = 0; i < len; i++) {
				FastaIndex tidx = idxArray[i];
				String name = tidx.getName();
				if (getDecoyRefJudger().isDecoy(name)) {
					if (name.length() > splitlenRev)
						name = name.substring(0, splitlenRev);
				} else {
					if (name.length() > splitlen)
						name = name.substring(0, splitlen);
				}
				map.put(name, tidx);
			}
			return map;
		}
	}
}
