/*
 ******************************************************************************
 * File: Peak.java * * * Created on 04-25-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import java.io.*;

/**
 * A peak in a mass spectrum
 *
 * @author Xinning
 * @version 0.3.3, 11-14-2008, 10:58:53
 */
public class Peak implements IPeak
{
    private static final long serialVersionUID = 1L;

    private double mz;
    private double intensity;
    private double[] massRange;

    public Peak() { }

    public Peak(IPeak peak)
    {
        this.mz = peak.getMz();
        this.intensity = peak.getIntensity();
    }

    public Peak(double mz, double intens)
    {
        this.mz = mz;
        this.intensity = intens;
    }


    public double getMz()
    {
        return this.mz;
    }

    public void setMz(double mz)
    {
        this.mz = mz;
    }


    public double getIntensity()
    {
        return this.intensity;
    }


    public void setIntensity(double intensity)
    {
        this.intensity = intensity;
    }

    /**
     * Sort the peaks by m/z values from small to big
     */
    public int compareTo(IPeak obj)
    {
        return Double.compare(mz, obj.getMz());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.mz);
        sb.append(' ');
        sb.append(this.intensity);
        return sb.toString();
    }

    @Override
    public Peak deepClone()
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oos.writeObject(this);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return (Peak) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Peak clone()
    {
        Peak cloned = null;

        try {
            cloned = (Peak) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return cloned;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Peak) {
            Peak p = (Peak) obj;
            return this.toString().equals(p.toString());
        } else
            return false;
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }


    @Override
    public double[] getMassRange()
    {
        return massRange;
    }


    @Override
    public void setMassRange(double[] range)
    {
        this.massRange = range;
    }
}
