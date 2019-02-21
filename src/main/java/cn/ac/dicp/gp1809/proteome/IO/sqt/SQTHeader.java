/* 
 ******************************************************************************
 * File: SQTHeader.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

import java.util.ArrayList;

import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * The header
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 20:25:48
 */
public class SQTHeader implements ISQTHeader {

	private String lineSeparator = IOConstant.lineSeparator;
	
	//The name of the software used to create the SQT file
	private String SQTGenerator;
	//The version number of the SQTGenerator software
	private String SQTGeneratorVersion;
	//Path to sequence database used to generate the SQT file
	private String database;
	//Were average or mono-istopic residue masses used to predict the fragment ion mass
	private String fragmentMasses;
	//Were average or mono-istopic residue masses used to predict the precursor ion mass
	private String precursorMasses;
	//Time when SQT file was created
	private String startTime;
	//Non-standard amino-acid masses used in identification (repeat this record if there are multiple non-standard masses)
	private String staticMod;
	//List of dynamic modifications used in identification
	private String dynamicMod;

	//The comments
	private ArrayList<IComment> comments;

	//Number of aminio acids in sequence database. May equals to ?
	private String DBSeqLength;
	//Number of proteins in sequence database
	private String DBLocusCount;
	//MD5 checksum of sequence database
	private String DBMD5Sum;
	//Name of field use to sort spectra
	private String SortedBy;
	//Field names begining with Alg- are algorithm specific
	private ArrayList<IAlgField> algFields;
	//Other fields
	private ArrayList<IOtherField> otherFields;

	/**
	 * @param sQtGenerator
	 * @param sQtGeneratorVersion
	 * @param database
	 * @param fragmentMassesString
	 * @param precursorMasses
	 * @param startTime
	 * @param staticMod
	 * @param dynamicMod
	 */
	public SQTHeader(String sQtGenerator, String sQtGeneratorVersion,
	        String database, String fragmentMassesString,
	        String precursorMasses, String startTime, String staticMod,
	        String dynamicMod) {
		this.SQTGenerator = sQtGenerator;
		this.SQTGeneratorVersion = sQtGeneratorVersion;
		this.database = database;
		this.fragmentMasses = fragmentMassesString;
		this.precursorMasses = precursorMasses;
		this.startTime = startTime;
		this.staticMod = staticMod;
		this.dynamicMod = dynamicMod;
	}
	
	SQTHeader(){
		
	}

	/**
	 * The name of the software used to create the SQT file. Necessary.
	 * 
	 * @param sQtGenerator
	 *            the sQTGenerator to set
	 */
	void setSQTGenerator(String sQtGenerator) {
		SQTGenerator = sQtGenerator;
	}

	/**
	 * The version number of the SQTGenerator software. Necessary.
	 * 
	 * @param sQtGeneratorVersion
	 *            the sQTGeneratorVersion to set
	 */
	void setSQTGeneratorVersion(String sQtGeneratorVersion) {
		SQTGeneratorVersion = sQtGeneratorVersion;
	}

	/**
	 * Path to sequence database used to generate the SQT file. Necessary.
	 * 
	 * 
	 * @param database
	 *            the database to set
	 */
	void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * * Were average or mono-istopic residue masses used to predict the
	 * fragment ion mass.
	 * 
	 * @param fragmentMasses
	 *            the fragmentMasses to set
	 */
	void setFragmentMasses(String fragmentMasses) {
		this.fragmentMasses = fragmentMasses;
	}

	/**
	 * Were average or mono-istopic residue masses used to predict the precursor
	 * ion mass
	 * 
	 * @param precursorMasses
	 *            the precursorMasses to set
	 */
	void setPrecursorMasses(String precursorMasses) {
		this.precursorMasses = precursorMasses;
	}

	/**
	 * Time when SQT file was created. Necessary.
	 * 
	 * @param startTime
	 *            the startTime to set
	 */
	void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * Non-standard amino-acid masses used in identification (repeat this record
	 * if there are multiple non-standard masses). Necessary.
	 * 
	 * @param staticMod
	 *            the staticMod to set
	 */
	void setStaticMod(String staticMod) {
		this.staticMod = staticMod;
	}

	/**
	 * List of dynamic modifications used in identification. Necessary.
	 * 
	 * @param dynamicMod
	 *            the dynamicMod to set
	 */
	void setDynamicMod(String dynamicMod) {
		this.dynamicMod = dynamicMod;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	void addComment(String value) {
		if (this.comments == null)
			this.comments = new ArrayList<IComment>();
		this.comments.add(new Comment(value));
	}

	/**
	 * @param dBSeqLength
	 *            the dBSeqLength to set
	 */
	void setDBSeqLength(String dBSeqLength) {
		DBSeqLength = dBSeqLength;
	}

	/**
	 * @param dBLocusCount
	 *            the dBLocusCount to set
	 */
	void setDBLocusCount(String dBLocusCount) {
		DBLocusCount = dBLocusCount;
	}

	/**
	 * @param dBmd5Sum
	 *            the dBMD5Sum to set
	 */
	void setDBMD5Sum(String dBmd5Sum) {
		DBMD5Sum = dBmd5Sum;
	}

	/**
	 * @param sortedBy
	 *            the sortedBy to set
	 */
	void setSortedBy(String sortedBy) {
		SortedBy = sortedBy;
	}

	/**
	 * The name of the algorithm specific field. Can with the prefix of alg-
	 * 
	 * @param fieldname
	 * @param value
	 */
	void addAlgField(String fieldname, String value) {
		if (this.algFields == null)
			this.algFields = new ArrayList<IAlgField>();

		String name = fieldname;
		if (fieldname.startsWith("Alg-")) {
			name = fieldname.substring(4);
		}

		this.algFields.add(new AlgField(name, value));
	}

	/**
	 * @param otherFields
	 *            the otherFields to set
	 */
	void addOtherFields(String otherFields) {
		if (this.otherFields == null)
			this.otherFields = new ArrayList<IOtherField>();

		this.otherFields.add(new OtherField(otherFields));
	}

	/**
	 * The name of the software used to create the SQT file. Necessary.
	 * 
	 * @return the sQTGenerator
	 */
	public String getSQTGenerator() {
		return SQTGenerator;
	}

	/**
	 * The version number of the SQTGenerator software. Necessary.
	 * 
	 * @return the sQTGeneratorVersion
	 */
	public String getSQTGeneratorVersion() {
		return SQTGeneratorVersion;
	}

	/**
	 * Path to sequence database used to generate the SQT file. Necessary.
	 * 
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Were average or mono-istopic residue masses used to predict the fragment
	 * ion mass.
	 * 
	 * Necessary.
	 * 
	 * @return the fragmentMassesString: mono or average
	 */
	public String getFragmentMassesString() {
		return fragmentMasses;
	}

	/**
	 * Were average or mono-istopic residue masses used to predict the precursor
	 * ion mass
	 * 
	 * @return the precursorMasses: mono or average
	 */
	public String getPrecursorMasses() {
		return precursorMasses;
	}

	/**
	 * Time when SQT file was created. Necessary.
	 * 
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * Non-standard amino-acid masses used in identification (repeat this record
	 * if there are multiple non-standard masses). Necessary.
	 * 
	 * @return the staticMod
	 */
	public String getStaticMod() {
		return staticMod;
	}

	/**
	 * List of dynamic modifications used in identification. Necessary.
	 * 
	 * @return the dynamicMod
	 */
	public String getDynamicMod() {
		return dynamicMod;
	}

	/**
	 * Remarks. Multiple comment lines are allowed. Not necessary. Can be null
	 * or with zero length.
	 * 
	 * @return the comments
	 */
	public ArrayList<IComment> getComments() {
		return comments;
	}

	/**
	 * Number of aminio acids in sequence database. Not necessary. Can be null
	 * or "?"
	 * 
	 * @return the dBSeqLength
	 */
	public String getDBSeqLength() {
		return DBSeqLength;
	}

	/**
	 * Number of proteins in sequence database. Not necessary. Can be null.
	 * 
	 * @return the dBLocusCount
	 */
	public String getDBLocusCount() {
		return DBLocusCount;
	}

	/**
	 * MD5 checksum of sequence database. Not necessary. Can be null.
	 * 
	 * @return the dBMD5Sum
	 */
	public String getDBMD5Sum() {
		return DBMD5Sum;
	}

	/**
	 * Name of field use to sort spectra. Not necessary. Can be null.
	 * 
	 * @return the sortedBy
	 */
	public String getSortedBy() {
		return SortedBy;
	}

	/**
	 * Field names begining with Alg- are algorithm specific. Not necessary. Can
	 * be null or with zero length.
	 * 
	 * @return the algFields
	 */
	public ArrayList<IAlgField> getAlgFields() {
		return algFields;
	}

	/**
	 * Other field names are allowed, but may not contain white-space. Not
	 * necessary. Can be null or with zero length.
	 * 
	 * @return the otherFields
	 */
	public ArrayList<IOtherField> getOtherFields() {
		return otherFields;
	}
	
	/**
	 * Parse the string for each field
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	private String getString(String field, String value) {
		if(value == null) {
			return "";
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("H\t").append(field).append('\t').append(value).append(lineSeparator);
			return sb.toString();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getString("SQTGenerator", this.SQTGenerator));
		sb.append(this.getString("SQTGeneratorVersion", this.SQTGeneratorVersion));
		sb.append(this.getString("StartTime", this.startTime));
		sb.append(this.getString("Database", this.database));
		sb.append(this.getString("DBSeqLength", this.DBSeqLength));
		sb.append(this.getString("DBLocusCount", this.DBLocusCount));
		sb.append(this.getString("PrecursorMasses", this.precursorMasses));
		sb.append(this.getString("FragmentMasses", this.fragmentMasses));
		sb.append(this.getString("StaticMod", this.staticMod));
		sb.append(this.getString("DynamicMod", this.dynamicMod));
		sb.append(this.getString("DBMD5Sum", this.DBMD5Sum));
		sb.append(this.getString("SortedBy", this.SortedBy));
		
		
		
		if(this.comments != null&&this.comments.size()>0) {
			for(int i=0; i< this.comments.size(); i++) {
				sb.append(comments.get(i)).append(lineSeparator);
			}
		}
		
		if(this.algFields != null&&this.algFields.size()>0) {
			for(int i=0; i< this.algFields.size(); i++) {
				sb.append(algFields.get(i)).append(lineSeparator);
			}
		}
		
		if(this.otherFields != null&&this.otherFields.size()>0) {
			for(int i=0; i< this.otherFields.size(); i++) {
				sb.append(otherFields.get(i)).append(lineSeparator);
			}
		}
		
		//Delete the last line separator
		sb.delete(sb.length()-lineSeparator.length(), sb.length());
		return sb.toString();
	}


	/**
	 * The comment
	 * 
	 * @author Xinning
	 * @version 0.1, 04-01-2009, 10:23:11
	 */
	private class Comment implements IComment {

		private String value;

		private Comment(String value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISQTHeader.IComment#value()
		 */
		@Override
		public String value() {
			return value;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("H\tComment\t").append(value);
			return sb.toString();
		}
	}
	
	/**
	 * The OtherField
	 * 
	 * @author Xinning
	 * @version 0.1, 04-01-2009, 10:23:11
	 */
	private class OtherField implements IOtherField {

		private String value;

		private OtherField(String value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISQTHeader.IOtherField#value()
		 */
		@Override
		public String value() {
			return value;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("H\t").append(value);
			return sb.toString();
		}
	}

	/**
	 * The algorithm specific field and values
	 * 
	 * @author Xinning
	 * @version 0.1, 04-01-2009, 10:39:52
	 */
	private class AlgField implements IAlgField {

		private String field;
		private String value;

		/**
		 * @param field
		 * @param value
		 */
		private AlgField(String field, String value) {
			this.field = field;
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISQTHeader.IAlgField#field()
		 */
		@Override
		public String field() {
			return field;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.ISQTHeader.IAlgField#value()
		 */
		@Override
		public String value() {
			return value;
		}
		
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("H\t").append("Alg-").append(field).append('\t').append(value);
			return sb.toString();
		}
	}
}
