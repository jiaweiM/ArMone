/*
 ******************************************************************************
 * File: MatchedPeakList.java * * * Created on 05-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;


/**
 * Peak list with matched Information
 *
 * @author Xinning
 * @version 0.1, 05-12-2009, 20:13:07
 */
public class MatchedPeakList extends AbstractPeakList
{
    public MatchedPeakList()
    {
        super();
    }

    public MatchedPeakList(int approximatecount)
    {
        super(approximatecount);
    }


    @Override
    public PeakForMatch[] getPeakArray()
    {
        return this.peaklist.toArray(new PeakForMatch[size()]);
    }

    @Override
    public MatchedPeakList normalize()
    {
        return (MatchedPeakList) super.normalize();
    }


    @Override
    public MatchedPeakList rewindPeaks()
    {
        return (MatchedPeakList) super.rewindPeaks();
    }

    @Override
    public MatchedPeakList newInstance()
    {
        return new MatchedPeakList();
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
