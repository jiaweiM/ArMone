/*
 ******************************************************************************
 * File: AbstractPeakList.java * * * Created on 05-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.Description;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The abstract peak list
 *
 * @author Xinning
 * @version 0.1, 05-12-2009, 20:27:20
 */
public abstract class AbstractPeakList implements IPeakList
{
    protected ArrayList<IPeak> peaklist;

    private IPeak highestPeak = null;
    /* Current max intensity value */
    private double maxintense;

    private double minmz = 10000;
    private double maxmz;
    /* If the peaks intensities have ben normalized */
    private boolean normalized;

    private double totalIonCurrent;

    private double rtMinute;

    public AbstractPeakList()
    {
        this(30);
    }

    public AbstractPeakList(int approximatecount)
    {
        this.peaklist = new ArrayList<>(approximatecount);
    }

    public static IPeakList createPeakList(Description des)
    {
        int level = des.getLevel();

        IPeakList peakList;
        if (level == 1) {
            peakList = new MS1PeakList();
        } else {
            double mz = des.getPreMs();
            double inten = des.getPrecursorInten();
            short charge = (short) des.getCharge();
            PrecursePeak precurse = new PrecursePeak(mz, inten);
            precurse.setCharge(charge);

            peakList = new MS2PeakList();
            ((MS2PeakList) peakList).setPrecursePeak(precurse);
        }
        return peakList;
    }

    public IPeak[] getPeakArray()
    {
        return this.peaklist.toArray(new IPeak[size()]);
    }


    public void setPeakList(IPeak[] peaklist)
    {
        this.peaklist.clear();
        for (IPeak aPeaklist : peaklist) this.add(aPeaklist);
    }

    public IPeak[] getPeaksSortByIntensity()
    {
        IPeak[] peaks = this.peaklist.toArray(new IPeak[size()]);
        Arrays.sort(peaks, (o1, o2) -> Double.compare(o2.getIntensity(), o1.getIntensity()));
        return peaks;
    }

    public int size()
    {
        return this.peaklist.size();
    }


    public double[] getPeakMzArray()
    {
        int size = this.size();
        if (size == 0)
            return null;

        double[] mzarr = new double[size];

        for (int i = 0; i < size; i++) {
            mzarr[i] = peaklist.get(i).getMz();
        }

        return mzarr;
    }

    public void add(IPeak peak)
    {
        double mz = peak.getMz();
        double inten = peak.getIntensity();

        if (mz < this.minmz)
            this.minmz = mz;
        else if (mz > this.maxmz)
            this.maxmz = mz;

        if (this.maxintense < inten) {
            this.highestPeak = peak;
            this.maxintense = inten;
        }

        this.totalIonCurrent += inten;
        this.peaklist.add(peak);
    }

    public AbstractPeakList normalize()
    {
        if (!this.normalized) {
            for (int i = 0, n = this.size(); i < n; i++) {
                IPeak peak = this.peaklist.get(i);
                peak.setIntensity(peak.getIntensity() / this.maxintense);
            }

            this.normalized = true;
        }
        return this;
    }

    @Override
    public AbstractPeakList rewindPeaks()
    {
        if (this.normalized) {
            for (int i = 0, n = this.size(); i < n; i++) {
                IPeak peak = this.peaklist.get(i);
                peak.setIntensity(peak.getIntensity() * this.maxintense);
            }

            this.normalized = false;
        }
        return this;
    }

    public IPeak getPeak(int i)
    {
        return this.peaklist.get(i);
    }

    public IPeak getBasePeak()
    {
        return this.highestPeak;
    }

    public double getMinMZ()
    {
        return this.minmz;
    }

    public double getMaxMZ()
    {
        return this.maxmz;
    }

    public double getTotIonCurrent()
    {
        return this.totalIonCurrent;
    }

    public double getRTMinute()
    {
        return this.rtMinute;
    }

    public void setRTMinute(double rt)
    {
        this.rtMinute = rt;
    }


    @Override
    public IPeakList newInstance(Description des)
    {
        return createPeakList(des);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (IPeak peak : peaklist) {
            sb.append(peak.toString()).append(IOConstant.lineSeparator);
        }
        return sb.toString();
    }
}
