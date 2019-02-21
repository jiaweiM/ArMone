/*
 ******************************************************************************
 * File:MS1PeakList.java * * * Created on 2010-4-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;

/**
 * @author ck
 * @version 2010-4-23, 04:29:24
 */
public class MS1PeakList extends AbstractPeakList implements IPeakList
{
    private DecimalFormat DF2 = new DecimalFormat("0.##");

    public MS1PeakList() { }

    public MS1PeakList(int peakCount)
    {
        super(peakCount);
    }

    @Override
    public IPeak[] getPeakArray()
    {
        return this.peaklist.toArray(new IPeak[size()]);
    }


    @Override
    public IPeakList newInstance()
    {
        return new MS1PeakList();
    }


    @Override
    public byte[] toBytePeaks()
    {
        IPeak[] peaks = this.getPeakArray();

        int len = peaks.length;
        int peakbytes = len * 2 * 8 + 8 + 2;
        ByteBuffer buffer = ByteBuffer.allocate(peakbytes + 4);

        buffer.putInt(peakbytes);

        for (IPeak peak : peaks) {
            buffer.putDouble(peak.getMz());
            buffer.putDouble(peak.getIntensity());
        }

        return buffer.array();
    }


    @Override
    public String toPeaksOneLine()
    {
        IPeak[] peaks = this.getPeakArray();
        StringBuilder sb = new StringBuilder();

        for (IPeak peak : peaks) {
            sb.append(DF2.format(peak.getMz())).append(' ').append(
                    DF2.format(peak.getIntensity())).append(',');
        }
        //delete the last comma
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

}
