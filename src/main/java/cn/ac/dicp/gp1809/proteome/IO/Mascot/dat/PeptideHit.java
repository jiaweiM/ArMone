/* 
 ******************************************************************************
 * File: PeptideHit.java * * * Created on 11-12-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.util.ArrayList;

/**
 * The peptide hit for mascot dat
 * 
 * @author Xinning
 * @version 0.1, 11-12-2008, 09:34:56
 */
public class PeptideHit {

    /**
     * a soft type for the number of missed cleavages
     */
    public static final Integer MISSED_CLEAVAGES = new Integer(1);
    /**
     * a soft type for the peptide mass
     */
    public static final Integer PEPTIDE_MASS = new Integer(2);
    /**
     * a soft type for the theoretical-experimental mass error
     */
    public static final Integer DELTA_MASS = new Integer(3);
    /**
     * a soft type for the number of ions that where matched
     */
    public static final Integer NUMBER_OF_IONS_MATCHED = new Integer(4);
    /**
     * a soft type for the peptide's sequence
     */
    public static final Integer SEQUENCE = new Integer(5);
    /**
     * a soft type for the peptide's modified sequence
     */
    public static final Integer MODIFIED_SEQUENCE = new Integer(6);
    /**
     * a soft type for the number of peaks that where used for the identification
     */
    public static final Integer PEAKS_USED_FROM_IONS = new Integer(7);
    /**
     * a soft type for the identification's ionsscore
     */
    public static final Integer IONS_SCORE = new Integer(8);
    /**
     * a soft type for the threshold that must be surpassed by the ionsscore to be a confident identification
     */
    public static final Integer THRESHOLD = new Integer(9);
    
    /**
     * The peptide hit strings
     */
    private String[] hitStr;
    
    /**
     * This int presents the total of missed cleavages
     */
    private int iMissedCleavages = 0;
    /**
     * This double presents the peptide mass
     */
    private double iPeptideMr = 0;
    /**
     * This double presents the mass error (Expected - Observed)
     */
    private double iDeltaMass = 0;
    /**
     * This int presents the number of ions that where matched
     */
    private int iNumberOfIonsMatched = 0;
    /**
     * This String presents the peptide sequence
     */
    private String iSequence = null;
    
    /**
     * This String presents the terminal aminoacids of this peptide sequence.
     */
    private char[][] teminalaa = null;
    /**
     * This int presents the peaks used from ions1
     */
    private int iPeaksUsedFromIons1 = 0;
    /**
     * This String presents the String with variable modifications
     */
    private int[] iVariableModificationsArray = null;
    /**
     * This String holds the Ionscore
     */
    private float iIonsScore = 0;
    /**
     * This int[] holds the Ion series that have been found
     */
    private int[] iIonSeriesFound = null;
    /**
     * This int presents the peaks used from ions2
     */
    private int iPeaksUsedFromIons2 = 0;
    /**
     * This int presents the peaks used from ions3
     */
    private int iPeaksUsedFromIons3 = 0;
    /**
     * The ArrayList is filled with ProteinHit instances. Each containing information about the (possible) origin(s) of this PeptideHit instances.
     */
    private ArrayList<ProteinHit> iProteinHits = new ArrayList<ProteinHit>(1);
    /**
     * Used for calculate the identity threshold
     */
    private int iQueryIdenNum = 0;
    /**
     * The identity treshold, when the peptideHitScore is above this treshold it will tagged as an identification.
     */
    private double iQueryIdentityThreshold = 0;
    /**
     * The homology treshold.
     */
    private double iHomologyThreshold = 0;
    /**
     * The delta ion score.
     */
    private float deltaS = 0f;

    /**
     * Create an empty peptide hit
     */
    public PeptideHit(){
    }

    /**
     * The raw hit string array from mascot dat file.
     * 
     * @return
     */
    public String[] getHitString(){
    	return this.hitStr;
    }
    
    /**
     * The raw hit string array from mascot dat file.
     * 
     * @param hitstr
     */
    public void setHitString(String[] hitstr){
    	this.hitStr = hitstr;
    }
    
    /**
     * This method returns the amount of missed cleavages
     *
     * @return the amount of missed cleavages
     */
    public int getMissedCleavages() {
        return iMissedCleavages;
    }

    /**
     * This method sets the missed cleavages.
     *
     * @param aMissedCleavages int with the new value of missed cleavages
     */
    public void setMissedCleavages(int aMissedCleavages) {
        iMissedCleavages = aMissedCleavages;
    }

    /**
     * This method gets the peptide mass
     *
     * @return double peptide mass
     */
    public double getPeptideMr() {
        return iPeptideMr;
    }

    /**
     * This method set's the peptide mass
     *
     * @param aPeptideMr
     */
    public void setPeptideMr(double aPeptideMr) {
        iPeptideMr = aPeptideMr;
    }

    /**
     * This method gets the experimental-theoretical peptide mass deviation.
     *
     * @return double iDeltaMass
     */
    public double getDeltaMass() {
        return iDeltaMass;
    }

    /**
     * This method sets the delta mass
     *
     * @param aDeltaMass
     */
    public void setDeltaMass(double aDeltaMass) {
        iDeltaMass = aDeltaMass;
    }

    /**
     * This method gets the number of matched ions
     *
     * @return int number of matched ions
     */
    public int getNumberOfIonsMatched() {
        return iNumberOfIonsMatched;
    }

    /**
     * This method sets the number of matched ions
     *
     * @param aNumberOfIonsMatched
     */
    public void setNumberOfIonsMatched(int aNumberOfIonsMatched) {
        iNumberOfIonsMatched = aNumberOfIonsMatched;
    }

    /**
     * This method gets the sequence of the peptide
     *
     * @return String with the sequence of the peptidehit
     */
    public String getSequence() {
        return iSequence;
    }

    /**
     * This method sets the peptide sequence
     *
     * @param aSequence
     */
    public void setSequence(String aSequence) {
        iSequence = aSequence;
    }

    /**
     * This method gets an int with the total number of used peaks from ions1
     *
     * @return int with the total number of used peaks from ions1
     */
    public int getPeaksUsedFromIons1() {
        return iPeaksUsedFromIons1;
    }

    /**
     * This method sets the total number of peaks that were used from ions1
     *
     * @param aPeaksUsedFromIons1
     */
    public void setPeaksUsedFromIons1(int aPeaksUsedFromIons1) {
        iPeaksUsedFromIons1 = aPeaksUsedFromIons1;
    }

    /**
     * This method gets a coded String with modifications on the sequence
     *
     * @return int[] with modification (coded)
     */
    public int[] getVariableModificationsArray() {
        return iVariableModificationsArray;
    }

    /**
     * This method sets the modifications on the peptidehit by a coded String
     *
     * @param aVariableModificationsArray
     */
    public void setVariableModificationsArray(int[] aVariableModificationsArray) {
        iVariableModificationsArray = aVariableModificationsArray;
    }

    /**
     * method
     * parses the coded variablemodificationString into an int[]
     *
     * @param aVariableModificationString
     */
    public void setVariableModificationsArray(String aVariableModificationString) {
        //initialise the int[]
        iVariableModificationsArray = new int[aVariableModificationString.length()];
        //for loop to fill the int[]
        for (int i = 0; i < aVariableModificationString.length(); i++) {
            iVariableModificationsArray[i] = Integer.parseInt(aVariableModificationString.substring(i, i + 1));
        }
    }

    /**
     * This gets a double with the ionscore of the peptidehit
     *
     * @return int with ionsscore
     */
    public float getIonsScore() {
        return iIonsScore;
    }

    /**
     * This sets the ionsscore of the peptidehit
     *
     * @param aIonsScore
     */
    public void setIonsScore(float aIonsScore) {
        iIonsScore = aIonsScore;
    }

    /**
     * This gets a coded String with found ionseries
     *
     * @return String with found ionseries
     */
    public int[] getIonSeriesFound() {
        return iIonSeriesFound;
    }

    /**
     * This sets a the ionseries
     *
     * @param aIonSeriesFound
     */
    public void setIonSeriesFound(int[] aIonSeriesFound) {
        iIonSeriesFound = aIonSeriesFound;
    }

    /**
     * This gets the peaks used from ions2
     *
     * @return int with the amount of peaks used from ions2
     */
    public int getPeaksUsedFromIons2() {
        return iPeaksUsedFromIons2;
    }

    /**
     * This sets how many peaks there were used from ions2
     *
     * @param aPeaksUsedFromIons2
     */
    public void setPeaksUsedFromIons2(int aPeaksUsedFromIons2) {
        iPeaksUsedFromIons2 = aPeaksUsedFromIons2;
    }

    /**
     * This gets the peaks used from ions3
     *
     * @return int with the amount of peaks used from ions3
     */
    public int getPeaksUsedFromIons3() {
        return iPeaksUsedFromIons3;
    }

    /**
     * This sets how many peaks there were used from ions3
     *
     * @param aPeaksUsedFromIons3
     */
    public void setPeaksUsedFromIons3(int aPeaksUsedFromIons3) {
        iPeaksUsedFromIons3 = aPeaksUsedFromIons3;
    }

    /**
     * Returns the ArrayList is filled with ProteinHit instances. Each containing information about the (possible) origin(s) of this PeptideHit instances.
     *
     * @return the ArrayList is filled with ProteinHit instances. Each containing information about the (possible) origin(s) of this PeptideHit instances.
     */
    public ArrayList<ProteinHit> getProteinHits() {
        return iProteinHits;
    }

    /**
     * Sets the ArrayList is filled with ProteinHit instances. Each containing information about the (possible) origin(s) of this PeptideHit instances.
     *
     * @param aProteinHits the ArrayList is filled with ProteinHit instances. Each containing information about the (possible) origin(s) of this PeptideHit instances.
     */
    public void setProteinHits(ArrayList<ProteinHit> aProteinHits) {
        iProteinHits = aProteinHits;
    }

    /**
     * This method returns the number of (parametrical specified) aminoacids this PeptideHit has.
     *
     * @param aAminoAcid The aminoAcid that we want to count.
     * @return returns an int with the number of aminoacids found in the sequence.
     */
    public int getNumberOfAminoAcid(char aAminoAcid) {
        int lCount = 0;
        for (int i = 0; i < iSequence.length(); i++) {
            if (iSequence.charAt(i) == aAminoAcid) {
                lCount++;
            }
        }
        return lCount;
    }

    /**
     * method to get the expectance at default confidence alpha = 0.05. <br>
     * Expectancy is the number of times you could expect to get this score or better by chance.
     *
     * @return double Expectancy
     */
    public double getExpectancy() {
        return getExpectancy(0.05);
    }


    /**
     * The terminalaa array for this peptide seqence. 
     * <p>char[0][0]-->left terminal aa for the first protein hit (A.XXXXX)
     * <p>char[0][1]-->right terminal aa for the first protein hit (XXXX.A)
     * <p>char[1][0]-->left terminal aa for the second protein hit if any (A.XXXXX)
     * <p>char[1][1]-->right terminal aa for the second protein hit if any (XXXX.A)
     * <p> ... ...
     * 
     * Only for mascot version 2.2 (?)
     * 
     * @return the teminalaa
     */
    public char[][] getTeminalaa() {
    	return teminalaa;
    }

	/**
	 * The terminalaa for this peptide seqence. Only for mascot version 2.2 (?)
	 * 
     * @param teminalaa the teminalaa to set
     */
    public void setTeminalaa(char[][] teminalaa) {
    	this.teminalaa = teminalaa;
    }

	/**
     * Returns Homology threshold (QPlughole value from .dat file).
     * @return homology threshold
     */
    public double getHomologyThreshold() {
        return iHomologyThreshold;
    }
    
	/**
     * Returns Homology threshold (QPlughole value from .dat file).
     * @return homology threshold
     */
    public void setHomologyThreshold(double threshold){
    	this.iHomologyThreshold = threshold;
    }
    
    /**
     * The query identity threshold (qmatch in dat file ??)
     * 
     * @param threshold
     */
    public void setQueryIdentityThreshold(double threshold){
    	this.iQueryIdentityThreshold = threshold;
    }
    
    /**
     * The query identity threshold
     * 
     * @param threshold
     */
    public double getQueryIdentityThreshold(){
    	return this.iQueryIdentityThreshold;
    }

    /**
     * method to get the expectance at confidence alpha = parameter. <br>
     * Expectancy is the number of times you could expect to get this score or better by chance.
     *
     * @return double Expectancy
     */
    public double getExpectancy(double aConfidenceInterval) {
        double lThreshold = calculateIdentityThreshold(aConfidenceInterval);
        return (aConfidenceInterval * Math.pow(10, ((lThreshold - iIonsScore) / 10)));
    }

    /**
     * method that calculates the IdentityThreshold at default confidence alpha = 0.05.<br>
     *
     * @return double      IdentityThreshold.
     */
    public double calculateIdentityThreshold() {
        return calculateIdentityThreshold(0.05);
    }

    /**
     * method that calculates the IdentityThreshold at confidence alpha = parameter.<br>
     *
     * @return double      IdentityThreshold.
     */
    public double calculateIdentityThreshold(double aConfidenceInterval) {
        return 10.0 * Math.log(iQueryIdenNum / (aConfidenceInterval * 20.0)) / Math.log(10);
    }

    /**
     * This method method parse the String with the coded information of the ionseries type's into an int[].
     *
     * @param aIonSeries String with the ionSeries information.
     */
    public void setIonSeries(String aIonSeries) {
        iIonSeriesFound = new int[aIonSeries.length()];
        for (int i = 0; i < aIonSeries.length(); i++) {
            iIonSeriesFound[i] = Character.getNumericValue(aIonSeries.charAt(i));
        }
    }

    public void setDeltaS(float deltaS){
    	this.deltaS = deltaS;
    }
    
    public float getDeltaS(){
    	return this.deltaS;
    }
    
    public void setQueryIdenNum(int queryIdenNum){
    	this.iQueryIdenNum = queryIdenNum;
    }
    
    public int getQueryIdenNum(){
    	return this.iQueryIdenNum;
    }
    
    /**
     * This method returns a String with information about this peptidehit.
     *
     * @return String with information about this peptidehit.
     */
    @Override
	public String toString() {
    	
    	StringBuilder sb = new StringBuilder();
    	for(String s : this.hitStr){
    		sb.append(s).append("\r\n");
    	}
    	
        return (sb.toString());
    }

}
