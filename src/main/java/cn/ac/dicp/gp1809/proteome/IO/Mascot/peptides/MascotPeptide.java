/*
 * *****************************************************************************
 * File: MascotPeptide.java * * * Created on 10-06-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

import java.util.HashSet;

/**
 * Peptide identified by Mascot implements IPeptide
 * <p>
 * Changes: 0.1.4, add new constructor from a mascot peptide
 *
 * @author Xinning
 * @version 0.1.5, 05-02-2010, 11:05:35
 */
public class MascotPeptide extends AbstractPeptide implements IMascotPeptide
{
    // The E-value
    private double evalue;
    // The hypersocre
    private float ionscore;

    private float primScore;

    private short numofterm;

    private double intensity;

    private float idenThres;

    private float homoThres;

    private float deltaS;

    private int queryIdenNum = 0;

    private int numOfMatchedIons = 0;

    private int peaksUsedFromIons1 = 0;

    private int peaksUsedFromIons2 = 0;

    private int peaksUsedFromIons3 = 0;


    /**
     * Construct a new peptide with the same informations
     *
     * @param pep
     */
    public MascotPeptide(IMascotPeptide pep)
    {
        this(pep.getScanNum(), pep.getSequence(), pep.getCharge(), pep.getMH(),
                pep.getDeltaMH(), pep.getMissCleaveNum(), pep.getRank(), pep.getIonscore(), pep
                        .getEvalue(), pep.getProteinReferences(), pep.getPI(),
                (IMascotPeptideFormat) pep.getPeptideFormat());

        this.setProbability(pep.getProbabilty());
        this.setEnzyme(pep.getEnzyme());
    }

    public MascotPeptide(String baseName, int scanNumBeg, int scanNumEnd,
            String sequence, short charge, double mh, double deltaMs,
            short rank, float ionscore, double evalue,
            HashSet<ProteinReference> refs, IMascotPeptideFormat formatter)
    {
        super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
                rank, refs, formatter);

        this.evalue = evalue;
        this.setIonscore(ionscore);
    }

    public MascotPeptide(String scanNum, String sequence, short charge,
            double mh, double deltaMs, short rank, float ionsocre,
            double evalue, HashSet<ProteinReference> refs,
            IMascotPeptideFormat formatter)
    {
        super(scanNum, sequence, charge, mh, deltaMs, rank, refs, formatter);

        this.evalue = evalue;
        this.setIonscore(ionsocre);
    }

    public MascotPeptide(String scanNum, String sequence, short charge,
            double mh, double deltaMs, short numofterms, short rank, float ionscore,
            double evalue, HashSet<ProteinReference> refs,
            IMascotPeptideFormat formatter)
    {
        this(scanNum, sequence, charge, mh, deltaMs, rank, ionscore, evalue, refs, formatter);

        this.setNumberofTerm(numofterms);
    }

    public MascotPeptide(String baseName, int scanNumBeg, int scanNumEnd,
            String sequence, short charge, double mh, double deltaMs,
            short numofTerms, short rank, float ionsocre, double evalue,
            HashSet<ProteinReference> refs,
            IMascotPeptideFormat formatter)
    {
        this(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
                rank, ionsocre, evalue, refs, formatter);

        this.setNumberofTerm(numofTerms);
    }

    public MascotPeptide(String baseName, int scanNumBeg, int scanNumEnd,
            String sequence, short charge, double mh, double deltaMs,
            short numofTerms, short rank, float ionsocre, double evalue,
            HashSet<ProteinReference> refs, float pi,
            IMascotPeptideFormat formatter)
    {
        this(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
                rank, ionsocre, evalue, refs, formatter);

        this.setPI(pi);
        this.setNumberofTerm(numofTerms);
    }

    public MascotPeptide(String scanNum, String sequence, short charge,
            double mh, double deltaMs, short numofTerms, short rank, float ionsocre,
            double evalue, HashSet<ProteinReference> refs, float pi,
            IMascotPeptideFormat formatter)
    {
        this(scanNum, sequence, charge, mh, deltaMs, numofTerms, rank, ionsocre, evalue,
                refs, formatter);

        this.setPI(pi);
        this.setNumberofTerm(numofTerms);
    }

    public int getQueryIdenNum()
    {
        return this.queryIdenNum;
    }

    public void setQueryIdenNum(int queryIdenNum)
    {
        this.queryIdenNum = queryIdenNum;
    }

    public final double getEvalue()
    {
        return evalue;
    }

    /**
     * @param evalue the expected value to set
     */
    public final void setEvalue(double evalue)
    {
        this.evalue = evalue;
    }

    /**
     * @return mascot score
     */
    public final float getIonscore()
    {
        return this.ionscore;
    }

    /**
     * @param ionscore the ionscore value to set
     */
    public final void setIonscore(float ionscore)
    {
        this.ionscore = ionscore;
        this.primScore = ionscore;
    }

    /**
     * The intensity of the precursor ion of the peptide.
     */
    @Override
    public double getInten()
    {
        return this.intensity;
    }

    @Override
    public void setInten(double intensity)
    {
        this.intensity = intensity;
    }

    public float getIdenThres()
    {
        return this.idenThres;
    }

    public void setIndenThres(float idenThres)
    {
        this.idenThres = idenThres;
    }

    public float getHomoThres()
    {
        return this.homoThres;
    }

    public void setHomoThres(float homoThres)
    {
        this.homoThres = homoThres;
    }

    /**
     * =ionsocre;
     */
    @Override
    public float getPrimaryScore()
    {
        return this.primScore;
    }

    @Override
    public PeptideType getPeptideType()
    {
        return PeptideType.MASCOT;
    }

    @Override
    public void setPeptideFormat(IPeptideFormat<?> format)
    {
        if (format == null) {
            return;
        }

        if (format instanceof IMascotPeptideFormat<?>) {
            super.setPeptideFormat(format);
        } else
            throw new IllegalArgumentException("The formater for set must be Mascot formater");
    }

    @Override
    public float getDeltaS()
    {
        return deltaS;
    }

    public void setDeltaS(float deltaS)
    {
        this.deltaS = deltaS;
    }


    @Override
    public double calEvalue(float pvalue)
    {
        this.evalue = pvalue * Math.pow(10, ((idenThres - ionscore) / 10));
        return evalue;
    }

    /**
     * Return the identity threshold of given p-value
     *
     * @param pvalue p-value
     * @return MIT value
     */
    @Override
    public float calIdenThres(float pvalue)
    {
        this.idenThres = (float) (10.0 * Math.log(queryIdenNum / (pvalue * 20.0)) / Math.log(10));
        return idenThres;
    }

    public void reCal4PValue(float pvalue)
    {
        this.idenThres = (float) (10.0 * Math.log(queryIdenNum / (pvalue * 20.0)) / Math.log(10));
        this.evalue = pvalue * Math.pow(10, ((idenThres - ionscore) / 10));
        this.homoThres = homoThres >= idenThres ? 0.0f : homoThres;
    }


    @Override
    public int getNumOfMatchedIons()
    {
        return numOfMatchedIons;
    }


    @Override
    public void setNumOfMatchedIons(int numOfMatchedIons)
    {
        this.numOfMatchedIons = numOfMatchedIons;
    }


    @Override
    public int getPeaksUsedFromIons1()
    {
        return peaksUsedFromIons1;
    }


    @Override
    public void setPeaksUsedFromIons1(int peaksUsedFromIons1)
    {
        this.peaksUsedFromIons1 = peaksUsedFromIons1;
    }

    @Override
    public int getPeaksUsedFromIons2()
    {
        return peaksUsedFromIons2;
    }

    @Override
    public void setPeaksUsedFromIons2(int peaksUsedFromIons2)
    {
        this.peaksUsedFromIons2 = peaksUsedFromIons2;
    }

    @Override
    public int getPeaksUsedFromIons3()
    {
        return peaksUsedFromIons3;
    }

    @Override
    public void setPeaksUsedFromIons3(int peaksUsedFromIons3)
    {
        this.peaksUsedFromIons3 = peaksUsedFromIons3;
    }
}
