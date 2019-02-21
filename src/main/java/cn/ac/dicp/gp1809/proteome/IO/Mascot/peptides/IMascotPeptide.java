/*
 * *****************************************************************************
 * File: IMascotPeptide.java * * * Created on 11-04-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * The Mascot Peptide
 *
 * @author Xinning
 * @version 0.1, 11-04-2008, 20:06:41
 */
public interface IMascotPeptide extends IPeptide
{
    /**
     * @return the expected value
     */
    double getEvalue();

    /**
     * calculate the E-value from p-value
     *
     * @param pvalue
     * @return
     */
    double calEvalue(float pvalue);

    /**
     * @return the Ionscore
     */
    float getIonscore();

    /**
     * @return the Mascot Identity Threshold (MIT)
     */
    float getIdenThres();

    /**
     * calculate the Mascot Identity Threshold (MIT)
     *
     * @param pvalue
     * @return
     */
    float calIdenThres(float pvalue);

    /**
     * @return the Mascot Homology Threshold (MHT)
     */
    float getHomoThres();

    /**
     * @param homoThres
     */
    void setHomoThres(float homoThres);

    /**
     * @return the delta ion score
     */
    float getDeltaS();

    /**
     * @return
     */
    int getQueryIdenNum();

    int getNumOfMatchedIons();

    void setNumOfMatchedIons(int numOfMatchedIons);

    int getPeaksUsedFromIons1();

    void setPeaksUsedFromIons1(int peaksUsedFromIons1);

    int getPeaksUsedFromIons2();

    void setPeaksUsedFromIons2(int peaksUsedFromIons2);

    int getPeaksUsedFromIons3();

    void setPeaksUsedFromIons3(int peaksUsedFromIons3);

}