/*
 ******************************************************************************
 * File:IMS2PeakList.java * * * Created on 2010-4-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

/**
 * @version Sep 9, 2015, 4:52:16 PM
 */
public interface IMS2PeakList extends IPeakList
{

    /**
     * @return peak of precurse ion
     */
    PrecursePeak getPrecursePeak();

    /**
     * @param parent precurse ion peek;
     */
    void setPrecursePeak(PrecursePeak parent);

    /**
     * Get the intensity of the precursor peak.
     *
     * @return precursor intensity
     */
    double getInten();

    IMS2PeakList newInstance();

    void sort();

}
