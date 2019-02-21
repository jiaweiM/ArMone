/*
 * *****************************************************************************
 * File: Header.java * * * Created on 11-11-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.util.ArrayList;
import java.util.HashMap;


 /**
  * This class contains all the parsed data from the 'header' section of 
  * the datfile.
  * 
  * <p>
  * #translated and modified from Mascot_dtafile project by Kenny
  * 
  * @author Xinning
  * @version 0.1, 11-11-2008, 10:45:52
  */
public class Header implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
	/**
     * Number of sequences in the database.
     */
    private long iSequences;
    /**
     * Number of sequences after taxonomy filter.
     */
    private long iSequences_after_tax;
    /**
     * Number of residues in the DB.
     */
    private long iResidues;
    /**
     * This is a ',' seperated list of values that represent a histogram of the complete protein score distribution.
     * Only meaningfull for a PMF search.
     */
    private String iDistribution;
    /**
     * Search time in seconds.
     */
    private long iExecutionTime;
    /**
     * Date when the search was done.
     */
    private long iDate;
    /**
     * Time on <iDate> that the search was requested.
     */
    private String iTime;
    /**
     * Number of queries done.
     */
    private int iQueries;
    /**
     * Maximum number of hits that should be listed in the datfile.
     */
    private int iMaxHits;
    /**
     * Database version ID.
     */
    private String iVersion;
    /**
     * Filename of the actual database. (ex: SP_human_20060207.fasta)
     */
    private String iRelease ;
    /**
     * Unique task identifier for searches submitted asynchronously.
     */
    private String iTaskID;
    /**
     * This is a String[] with warnings from Mascot.
     */
    private ArrayList<String> iWarnings = null;
    
    
    public Header(HashMap<String, String> h) {
        //parse all the key-values into instance variables.
        iSequences = Long.parseLong(h.get("sequences"));
        iSequences_after_tax = Long.parseLong(h.get("sequences_after_tax"));
        iResidues = Long.parseLong(h.get("residues"));
        iDistribution = h.get("distribution");
        iExecutionTime = Long.parseLong(h.get("exec_time"));
        iDate = Long.parseLong(h.get("date"));
        iTime = h.get("time");
        iQueries = Integer.parseInt(h.get("queries"));
        iMaxHits = Integer.parseInt(h.get("max_hits"));
        iVersion = h.get("version");
        iRelease = h.get("release");
        iTaskID = h.get("taskid");
        iWarnings = getWarnings(h);
    }
    
    private ArrayList<String> getWarnings(HashMap<String, String> h) {
        ArrayList<String> lWarnings = new ArrayList<String>(1);
        int index = 0;
        while (h.get("Warn" + index) != null) {
            String s = h.get("Warn" + index);
            lWarnings.add(s);
            index++;
        }
        return lWarnings;
    }
    
    public Header(String[] header) {
    	
    	if(header==null)
    		throw new NullPointerException("Header is null.");
    	
    	for(String line : header){
    		DatEntryParser.Entry entry = DatEntryParser.parseEntry(line);
    		String key = entry.getKey();
    		
    		if("sequences".equals(key)){
    			iSequences = Long.parseLong(entry.getValue());
    			continue;
    		}
    		
    		if("sequences_after_tax".equals(key)){
    			iSequences_after_tax = Long.parseLong(entry.getValue());
    			continue;
    		}
    		
    		if("residues".equals(key)){
    			iResidues = Long.parseLong(entry.getValue());
    			continue;
    		}
    		
    		if("distribution".equals(key)){
    			iDistribution = entry.getValue();
    			continue;
    		}
    		
    		if("exec_time".equals(key)){
    			iExecutionTime = Long.parseLong(entry.getValue());
    			continue;
    		}
    		
    		if("date".equals(key)){
    			iDate = Long.parseLong(entry.getValue());
    			continue;
    		}
    		
    		if("time".equals(key)){
    			iTime = entry.getValue();
    			continue;
    		}
    		
    		if("queries".equals(key)){
    			iQueries = Integer.parseInt(entry.getValue());
    			continue;
    		}
    		
    		if("max_hits".equals(key)){
    			iMaxHits = Integer.parseInt(entry.getValue());
    			continue;
    		}
    		
    		if("version".equals(key)){
    			iVersion = entry.getValue();
    			continue;
    		}
    		
    		if("release".equals(key)){
    			iRelease = entry.getValue();
    			continue;
    		}
    		
    		if("taskid".equals(key)){
    			iTaskID = entry.getValue();
    			continue;
    		}
    		
    		if(key.startsWith("Warn")){
    			if(this.iWarnings==null)
    				this.iWarnings = new ArrayList<String>();
    			
    			this.iWarnings.add(entry.getValue());
    		}
    	}
    }

    /**
     * Number of sequences in the database.
     */
    public long getSequences() {
        return iSequences;
    }

    /**
     * Number of sequences in the database.
     */
    public void setSequences(int aSequences) {
        iSequences = aSequences;
    }

    /**
     * Number of sequences after taxonomy filter.
     */
    public long getSequences_after_tax() {
        return iSequences_after_tax;
    }

    /**
     * Number of sequences after taxonomy filter.
     */
    public void setSequences_after_tax(int aSequences_after_tax) {
        iSequences_after_tax = aSequences_after_tax;
    }

    /**
     * Number of residues in the DB.
     */
    public long getResidues() {
        return iResidues;
    }

    /**
     * Number of residues in the DB.
     */
    public void setResidues(int aResidues) {
        iResidues = aResidues;
    }

    /**
     * This is a ',' seperated list of values that represent a histogram of the complete protein score distribution.
     * Only meaningfull for a PMF search.
     */
    public String getDistribution() {
        return iDistribution;
    }

    /**
     * This is a ',' seperated list of values that represent a histogram of the complete protein score distribution.
     * Only meaningfull for a PMF search.
     */
    public void setDistribution(String aDistribution) {
        iDistribution = aDistribution;
    }

    /**
     * Search time in seconds.
     */
    public long getExecutionTime() {
        return iExecutionTime;
    }

    /**
     * Search time in seconds.
     */
    public void setExecutionTime(int aExecutionTime) {
        iExecutionTime = aExecutionTime;
    }

    /**
     * Date when the search was done.
     */
    public long getDate() {
        return iDate;
    }

    /**
     * Date when the search was done.
     */
    public void setDate(int aDate) {
        iDate = aDate;
    }

    /**
     * Time on <iDate> that the search was requested.
     */
    public String getTime() {
        return iTime;
    }

    /**
     * Time on <iDate> that the search was requested.
     */
    public void setTime(String aTime) {
        iTime = aTime;
    }

    /**
     * Number of queries done.
     */
    public int getQueries() {
        return iQueries;
    }

    /**
     * Number of queries done.
     */
    public void setQueries(int aQueries) {
        iQueries = aQueries;
    }

    /**
     * Maximum number of hits that should be listed in the datfile.
     */
    public int getMaxHits() {
        return iMaxHits;
    }

    /**
     * Maximum number of hits that should be listed in the datfile.
     */
    public void setMaxHits(int aMaxHits) {
        iMaxHits = aMaxHits;
    }

    /**
     * Database version ID.
     */
    public String getVersion() {
        return iVersion;
    }

    /**
     * Database version ID.
     */
    public void setVersion(String aVersion) {
        iVersion = aVersion;
    }

    /**
     * Filename of the actual database. (ex: SP_human_20060207.fasta)
     */
    public String getRelease() {
        return iRelease;
    }

    /**
     * Filename of the actual database. (ex: SP_human_20060207.fasta)
     */
    public void setRelease(String aRelease) {
        iRelease = aRelease;
    }

    /**
     * Unique task identifier for searches submitted asynchronously.
     */
    public String getTaskID() {
        return iTaskID;
    }

    /**
     * Unique task identifier for searches submitted asynchronously.
     */
    public void setTaskID(String aTaskID) {
        iTaskID = aTaskID;
    }

    /**
     * This is a String[] with warnings from Mascot.
     */
    public ArrayList<String> getWarnings() {
        return iWarnings;
    }

    /**
     * This is a String[] with warnings from Mascot.
     */
    public void setWarnings(ArrayList<String> aWarnings) {
        iWarnings = aWarnings;
    }
}
