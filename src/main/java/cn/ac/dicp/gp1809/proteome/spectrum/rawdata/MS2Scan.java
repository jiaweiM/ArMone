/*
 ******************************************************************************
 * File: Scan.java * * * Created on 11-21-2007
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.util.IScanName;

/**
 * A scan including peaklist and spectrum infromations. It is generated from mzdata or mzxml files.
 *
 * <p><b>Not an ms1 scan.
 *
 * @author Xinning
 * @version 0.2.1, 06-05-2009, 10:18:00
 */
public class MS2Scan implements IMS2Scan
{
    //Description
    private Description des;
    private IMS2PeakList peaklist;
    private IScanName scanName;

    /**
     * @param des description;
     */
    public MS2Scan(Description des)
    {
        this(des, null);
    }

    /**
     * @param des      description; (Null unpermited)
     * @param PeakList peaks in this scan. (Null permited, for the description of this scan)
     */
    public MS2Scan(Description des, IMS2PeakList peaklist)
    {
        this.des = des;
        this.peaklist = peaklist;
    }

    public IScanName getScanName()
    {
        return scanName;
    }

    public void setScanName(IScanName scanName)
    {
        this.scanName = scanName;
    }

    /**
     * Get the description for this scan.
     *
     * @return
     */
    public Description getDescription()
    {
        return this.des;
    }

    /**
     * For ms2 only one precursor ms is valide; while for ms3, the mslev of precursor ms must be defined; e.g. ion with
     * m/z value of 800 cid to ms2, and a neutral loss peek of 751 is selected to form a ms3 the mz value return is 2 ?
     * 800 : 751; <b> Warning: </b> this method currently can only be used for MzData file type.
     *
     * @param mslev (2, or 3) (else return as 2)
     * @return m/z value
     */
    public double getPrecursorMZ(int mslev)
    {
        if (mslev == 2)
            return des.getPreMs();
        else if (mslev == 3)
            return des.getPreMs2();
        else
            return des.getPreMs();//same as ms2
    }

    public int getPrecursorScannum()
    {
        return this.peaklist.getPrecursePeak().getScanNum();
    }

    /**
     * Get the default precursor MZ value. That is, if this scan event is MS2 then return the precursor MZ of MS2 else
     * if the scan event if MS3, it return the precursor mz of MS3 scan (the ion in MS2 scan). <b>Warning: before
     * excution of this method, getMSLev() should be excuted to confurm that this scan is not a full MS scan </b>
     *
     * @return the precursor mz
     */
    public double getPrecursorMZ()
    {
        if (getMSLevel() == 2)
            return des.getPreMs();
        else if (getMSLevel() == 3)
            return des.getPreMs2();
        else if (getMSLevel() == 1)
            throw new RuntimeException(
                    "There is no precursor ion for a full MS scan!");
        else
            return des.getPreMs();//same as ms2
    }

    public double getPrecursorInten()
    {
        if (getMSLevel() == 2)
            return des.getPrecursorInten();
        else if (getMSLevel() == 3)
            return des.getPrecursorInten2();
        else if (getMSLevel() == 1)
            throw new RuntimeException(
                    "There is no precursor ion for a full MS scan!");
        else
            return des.getPreMs();//same as ms2
    }

    /**
     * The MSLev of this scan, typically, if the precursor ion of this scan is from the full MS, this scan is a second
     * level scan-MS2. Otherwise, if the precursor ion is from MS2 scan , this scan is a MS3 scan and so on. <b>
     * Warning: <b> this method currently can only be used for MzData file type.
     */
    public int getMSLevel()
    {
        return this.des.getLevel();
    }

    /**
     * The scan number of this scan
     */
    public int getScanNum()
    {
        return des.getScanNum();
    }

    /**
     * The Integer instance of this scan number
     */
    public Integer getScanNumInteger()
    {
        return des.getScanNum();
    }

    /**
     * The index of this scan for all the scans (int some conditions, the MS1 scan will not be reported, then the scan
     * index doesn't equal to the scan number.)
     *
     * @return the Nth number of this scan;
     */
    public int getIndex()
    {
        return des.getIndex();
    }

    @Override
    public short getCharge()
    {
        return (short) this.des.getCharge();
    }

    @Override
    public IMS2PeakList getPeakList()
    {
        return this.peaklist;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(50);
        sb.append("Index: ");
        sb.append(des.getIndex());
        sb.append("\n");
        sb.append("Scan: ");
        sb.append(des.getScanNum());
        sb.append("\n");
        sb.append("MSLev: ");
        sb.append(des.getLevel());
        sb.append("\n");
        sb.append("PrecursorMz: ");
        sb.append(des.getPreMs());
        sb.append("\n");
        if (des.getLevel() == 3) {
            sb.append("PrecursorMz: ");
            sb.append(des.getPreMs2());
            sb.append("\n");
        }
        return sb.toString();
    }


    @Override
    public boolean isContainPeaklist()
    {
        return this.peaklist != null;
    }


    @Override
    public double getRTMinute()
    {
        return des.getRenTimeMinute();
    }


    @Override
    public double getTotIonCurrent()
    {
        return peaklist.getTotIonCurrent();
    }
}
