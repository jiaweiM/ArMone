/*
 ******************************************************************************
 * File: IPeakList.java * * * Created on 05-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.Description;

/**
 * The peak list
 *
 * <p>Changes:
 * <li>0.1.1, 05-25-2009: create empty peak list {@link #newInstance()}
 *
 * @author Xinning
 * @version 0.1.1, 05-25-2009, 20:34:10
 */
public interface IPeakList
{
    /**
     * @return the number of peaks in this peaklist.
     */
    int size();

    /**
     * @return the peaks in this list as array.
     */
    IPeak[] getPeakArray();

    /**
     * Reset the peaks for this peaklist
     *
     * @param peeklist peeklist
     */
    void setPeakList(IPeak[] peaklist);

    /**
     * @return the peaks sorted by intensity, from high to low.
     */
    IPeak[] getPeaksSortByIntensity();

    /**
     * @return get the peeks mz value;
     */
    double[] getPeakMzArray();

    /**
     * Add a peak to this list
     *
     * @param peak
     */
    void add(IPeak peak);

    /**
     * Normalize this PeakList so that the highest peak (base peak) is with intensity of 1;
     * <p><b>Notice: the normalization is performed on the original PeakList, but not
     * just return a copy. To rewind the peaks to the original intensity, call rewindPeaks methods. </b>
     *
     * @return this PeakList after the normalization
     */
    IPeakList normalize();

    /**
     * Rewind the normalized PeakList to the original intensity;
     * <p><b>Notice: the normalization is performed on the original PeakList, but not
     * just return a copy. To rewind the peaks to the original intensity, call rewindPeaks methods. </b>
     *
     * @return this PeakList of original intensity
     */
    IPeakList rewindPeaks();

    /**
     * Get the ith peak.
     */
    IPeak getPeak(int i);

    /**
     * @return the peak with max intensity;
     */
    IPeak getBasePeak();

    /**
     * @return the minum value of the m/z in this list;
     */
    double getMinMZ();

    /**
     * @return the maxmum value of the m/z in this list;
     */
    double getMaxMZ();

    /**
     * To the string of peaks in one line with the following format: mz inten,mz inten, ....,mz inten The first is the
     * precursor peak
     *
     * @return
     */
    String toPeaksOneLine();

    /**
     * The byte peaks, first 4 bytes are the int value of number of bytes for this peak. Then the precursor mz (double),
     * charge(short). And the mz (double), intensity (double)
     *
     * @return
     */
    byte[] toBytePeaks();

    /**
     * Create an empty peak list
     */
    IPeakList newInstance();

    IPeakList newInstance(Description des);

    /**
     * @return The total ion current.
     */
    double getTotIonCurrent();

    /**
     * Get the retention time
     */
    double getRTMinute();

    /**
     * Set the retention time
     */
    void setRTMinute(double rt);
}