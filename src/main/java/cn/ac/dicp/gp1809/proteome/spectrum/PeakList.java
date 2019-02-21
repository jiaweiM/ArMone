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

/**
 * The list of peaks contained in a spectrum.
 *
 * @author Xinning
 * @version 0.3.2, 03-03-2009, 20:09:08
 */
public class PeakList extends AbstractPeakList
{
    public PeakList()
    {
        super();
    }

    public PeakList(int approximatecount)
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
        if (end == -1) {
            throw new RuntimeException("Illegal expression of peak list: " + peaksOneline);
        }

        String peakstr = peaksOneline.substring(start, end);
        int idx = peakstr.indexOf(' ');

        if (idx == -1) {
            throw new RuntimeException("Illegal expression of peak list: " + peaksOneline);
        }

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
    public PeakList normalize()
    {
        return (PeakList) super.normalize();
    }

    @Override
    public PeakList rewindPeaks()
    {
        return (PeakList) super.rewindPeaks();
    }

    @Override
    public PeakList newInstance()
    {
        return new PeakList();
    }


    @Override
    public byte[] toBytePeaks()
    {
        return null;
    }


    @Override
    public String toPeaksOneLine()
    {
        return null;
    }
}
