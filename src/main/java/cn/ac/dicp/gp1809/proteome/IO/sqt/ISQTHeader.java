/* 
 ******************************************************************************
 * File: ISQTHeader.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

import java.util.ArrayList;

/**
 * The header descriptions for SQT file
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 19:31:51
 */
public interface ISQTHeader {

	/**
	 * The comments in header
	 * 
	 * @author Xinning
	 * @version 0.1, 04-01-2009, 10:25:53
	 */
	public static interface IComment {

		/**
		 * The value
		 * 
		 * @return
		 */
		public String value();
	}

	
	/**
	 * The "other field" in header
	 * 
	 * @author Xinning
	 * @version 0.1, 04-01-2009, 10:25:53
	 */
	public static interface IOtherField {

		/**
		 * The value
		 * 
		 * @return
		 */
		public String value();
	}
	
	/**
	 * The algorithm specific field
	 * 
	 * @author Xinning
	 * @version 0.1, 04-01-2009, 10:26:53
	 */
	public static interface IAlgField {

		/**
		 * The name of the field. No prefix of Alg-.
		 * 
		 * @return
		 */
		public String field();

		/**
		 * The value
		 * 
		 * @return
		 */
		public String value();
	}

	/**
	 * The name of the software used to create the SQT file. Necessary.
	 * 
	 * @return the sQTGenerator
	 */
	public String getSQTGenerator();

	/**
	 * The version number of the SQTGenerator software. Necessary.
	 * 
	 * @return the sQTGeneratorVersion
	 */
	public String getSQTGeneratorVersion();

	/**
	 * Path to sequence database used to generate the SQT file. Necessary.
	 * 
	 * @return the database
	 */
	public String getDatabase();

	/**
	 * Were average or mono-istopic residue masses used to predict the fragment
	 * ion mass.
	 * 
	 * Necessary.
	 * 
	 * @return the fragmentMassesString: mono or average
	 */
	public String getFragmentMassesString();

	/**
	 * Were average or mono-istopic residue masses used to predict the precursor
	 * ion mass
	 * 
	 * @return the precursorMasses: mono or average
	 */
	public String getPrecursorMasses();

	/**
	 * Time when SQT file was created. Necessary.
	 * 
	 * @return the startTime
	 */
	public String getStartTime();

	/**
	 * Non-standard amino-acid masses used in identification (repeat this record
	 * if there are multiple non-standard masses). Necessary.
	 * 
	 * @return the staticMod
	 */
	public String getStaticMod();

	/**
	 * List of dynamic modifications used in identification. Necessary.
	 * 
	 * @return the dynamicMod
	 */
	public String getDynamicMod();

	/**
	 * Remarks. Multiple comment lines are allowed. Not necessary. Can be null
	 * or with zero length.
	 * 
	 * @return the comments
	 */
	public ArrayList<IComment> getComments();

	/**
	 * Number of aminio acids in sequence database. Not necessary. Can be null
	 * or "?"
	 * 
	 * @return the dBSeqLength
	 */
	public String getDBSeqLength();

	/**
	 * Number of proteins in sequence database. Not necessary. Can be null.
	 * 
	 * @return the dBLocusCount
	 */
	public String getDBLocusCount();

	/**
	 * MD5 checksum of sequence database. Not necessary. Can be null.
	 * 
	 * @return the dBMD5Sum
	 */
	public String getDBMD5Sum();

	/**
	 * Name of field use to sort spectra. Not necessary. Can be null.
	 * 
	 * @return the sortedBy
	 */
	public String getSortedBy();

	/**
	 * Field names begining with Alg- are algorithm specific. Not necessary. Can
	 * be null or with zero length.
	 * 
	 * @return the algFields
	 */
	public ArrayList<IAlgField> getAlgFields();

	/**
	 * Other field names are allowed, but may not contain white-space. Not
	 * necessary. Can be null or with zero length.
	 * 
	 * @return the otherFields
	 */
	public ArrayList<IOtherField> getOtherFields();
}
