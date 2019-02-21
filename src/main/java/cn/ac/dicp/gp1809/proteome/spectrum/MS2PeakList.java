/*
 * *****************************************************************************
 * File: PeakList.java * * * Created on 04-25-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * The list of peaks contained in a spectrum.
 *
 * @author Xinning
 * @version 0.3.2, 03-03-2009, 20:09:08
 */
public class MS2PeakList extends AbstractPeakList implements IMS2PeakList
{
    private PrecursePeak parent = null;

    private DecimalFormat DF4 = new DecimalFormat("0.####");
    private DecimalFormat DF5 = new DecimalFormat("0.#####");

    public MS2PeakList()
    {
        super();
    }

    public MS2PeakList(int approximatecount)
    {
        super(approximatecount);
    }

    /**
     * Parse the formatted string of peaks in one line with the following format: mz inten,mz inten, ....,mz inten (The
     * first is the precursor peak) to the peaklist
     *
     * @return
     */
    public static MS2PeakList parsePeaksOneLine(String peaksOneline)
    {
        if (peaksOneline == null || peaksOneline.length() == 0)
            return null;

        int start = 0;
        int end = peaksOneline.indexOf(',');
        MS2PeakList list = new MS2PeakList();

//		System.out.println("ms289\t"+peaksOneline.length()+"\t"+peaksOneline.substring(0,10));
        String peakstr = peaksOneline.substring(start, end);
        int idx = peakstr.indexOf(' ');
        double mz = Double.parseDouble(peakstr.substring(0, idx));
        short charge = Short.parseShort(peakstr.substring(idx + 1));
        PrecursePeak ppeak = new PrecursePeak(mz, 0);
        ppeak.setCharge(charge);

        list.setPrecursePeak(ppeak);

        start = end + 1;
        end = peaksOneline.indexOf(',', start);

        while (end != -1) {
            list.add(parsePeak(start, end, peaksOneline));

            start = end + 1;
            end = peaksOneline.indexOf(',', start);
        }

        //The last one
        list.add(parsePeak(start, peaksOneline.length(), peaksOneline));

        return list;
    }

    /**
     * Parse each peak
     *
     * @param start
     * @param end
     * @param peaksOneline
     * @return
     */
    private static Peak parsePeak(int start, int end, String peaksOneline)
    {
        String peakstr = peaksOneline.substring(start, end);

        int idx = peakstr.indexOf(' ');
        double mz = Double.parseDouble(peakstr.substring(0, idx));
        double inten = Double.parseDouble(peakstr.substring(idx + 1));
        return new Peak(mz, inten);
    }

    /**
     * Parse the formatted byte peaks to the Peaklist
     *
     * @return
     */
    public static MS2PeakList parseBytePeaks(byte[] peakbytes)
    {
        if (peakbytes == null || peakbytes.length == 0)
            return null;

        ByteBuffer buffer = ByteBuffer.wrap(peakbytes);

        double pmz = buffer.getDouble();
        short charge = buffer.getShort();

        MS2PeakList list = new MS2PeakList();

        PrecursePeak ppeak = new PrecursePeak(pmz, 0);
        ppeak.setCharge(charge);

        list.setPrecursePeak(ppeak);


        while (buffer.hasRemaining()) {
            double mz = buffer.getDouble();
            double inten = buffer.getDouble();

            list.add(new Peak(mz, inten));
        }

        return list;
    }


    @Override
    public IPeak[] getPeakArray()
    {
        return this.peaklist.toArray(new IPeak[size()]);
    }


    @Override
    public MS2PeakList normalize()
    {
        return (MS2PeakList) super.normalize();
    }


    @Override
    public MS2PeakList rewindPeaks()
    {
        return (MS2PeakList) super.rewindPeaks();
    }


    public PrecursePeak getPrecursePeak()
    {
        return this.parent;
    }


    public void setPrecursePeak(PrecursePeak parent)
    {
        this.parent = parent;
    }


    public String toPeaksOneLine()
    {
        IPeak[] peaks = this.getPeakArray();
        StringBuilder sb = new StringBuilder();

        PrecursePeak ppeak = this.getPrecursePeak();
        if (ppeak != null) {
            sb.append(DF5.format(ppeak.getMz())).append(' ').append(
                    ppeak.getCharge()).append(',');
        } else {
            sb.append(0).append(' ').append(0).append(',');
        }

        for (IPeak peak : peaks) {
            sb.append(DF4.format(peak.getMz())).append(' ').append(
                    DF4.format(peak.getIntensity())).append(',');
        }
        //delete the last comma
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * The byte peaks, first 4 bytes are the int value of number of bytes for this peak. Then the precursor mz (double),
     * charge(short). And the mz (double), intensity (double)
     *
     * @return
     */
    public byte[] toBytePeaks()
    {
        IPeak[] peaks = this.getPeakArray();

        int len = peaks.length;
        int peakbytes = len * 2 * 8 + 8 + 2;
        ByteBuffer buffer = ByteBuffer.allocate(peakbytes + 4);

        buffer.putInt(peakbytes);

        PrecursePeak ppeak = this.getPrecursePeak();
        if (ppeak != null) {
            buffer.putDouble(ppeak.getMz());
            buffer.putShort(ppeak.getCharge());
        } else {
            buffer.putDouble(0d);
            buffer.putShort((short) 0);
        }

        for (IPeak peak : peaks) {
            buffer.putDouble(peak.getMz());
            buffer.putDouble(peak.getIntensity());
        }

        return buffer.array();
    }


    @Override
    public MS2PeakList newInstance()
    {
        return new MS2PeakList();
    }


    @Override
    public double getInten()
    {
        return parent.getIntensity();
    }


    @Override
    public void sort()
    {
        IPeak[] peaks = this.getPeakArray();
        Arrays.sort(peaks);
        this.setPeakList(peaks);
    }
}
