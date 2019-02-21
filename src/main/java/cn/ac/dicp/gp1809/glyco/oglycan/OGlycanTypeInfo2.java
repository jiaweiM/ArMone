/*
 ******************************************************************************
 * File: OGlycanTypeInfo2.java * * * Created on 2013-12-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

/**
 * @author ck
 * @version 2013-12-13, 15:20:48
 */
public class OGlycanTypeInfo2
{
    private double mass;
    private int[] findType;
    private String marks;
    private double[] fragments;
    private int charge;
    private double expGlycoMass;

    /**
     * @param pepPeakMz
     * @param pepPeakInten
     * @param type
     * @param info
     * @param fragments
     */
    public OGlycanTypeInfo2(double mass, int[] findType, double[] fragments, String marks)
    {
        this.mass = mass;
        this.findType = findType;
        this.fragments = fragments;
        this.marks = marks;
    }

    /**
     * @return the peptide mass
     */
    public double getMass()
    {
        return mass;
    }

    /**
     * @return the mass
     */
    public int[] getFindType()
    {
        return findType;
    }

    public void setFindType(int[] findType)
    {
        this.findType = findType;
    }

    public String getFindTypeString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < findType.length; i++) {
            sb.append(findType[i]).append("_");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * @return the mass
     */
    public String getMarks()
    {
        return marks;
    }

    /**
     * @return
     */
    public double[] getFragments()
    {
        return fragments;
    }

    /**
     * @return the charge
     */
    public int getCharge()
    {
        return charge;
    }

    /**
     * @param charge the charge to set
     */
    public void setCharge(int charge)
    {
        this.charge = charge;
    }

    /**
     * @return the expGlycoMass
     */
    public double getExpGlycoMass()
    {
        return expGlycoMass;
    }

    /**
     * @param expGlycoMass the expGlycoMass to set
     */
    public void setExpGlycoMass(double expGlycoMass)
    {
        this.expGlycoMass = expGlycoMass;
    }
}
