/*
 * *****************************************************************************
 * File: SequestPeptide.java * * * Created on 08-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ProReaderConstant;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;
import cn.ac.dicp.gp1809.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Detail peptide contains peptide informations from sequest database search
 *
 * @author Xinning
 * @version 0.7.7, 05-25-2010, 16:33:28
 */

public class SequestPeptide extends AbstractPeptide implements
        ProReaderConstant, ISequestPeptide
{

    private float xcorr;
    private float deltaCn;
    private float sp;
    private short rsp;
    private String ions;
    private float ionPercent = -1f;
    /*
     * An allene peptide; This peptide is commonly from the anambiguate
     * identification of peptide from low mass spectral. In low mass accuracy
     * spectral, peptide with some amino acid may be unclassified. e.g. I & L
     * and so on. These peptide may be identified with same scores. Thus, add to
     * allelePep;
     */
    private ArrayList<SequestPeptide> allelelist;

    /**
     * Construct a same peptide from the original one
     *
     * @param pep
     */
    public SequestPeptide(ISequestPeptide pep)
    {
        this(pep.getScanNum(), pep.getSequence(), pep.getCharge(), pep.getMH(),
                pep.getDeltaMH(), pep.getRank(), pep.getDeltaCn(), pep
                        .getXcorr(), pep.getSp(), pep.getRsp(), pep.getIons(),
                pep.getSim(), pep.getProteinReferences(),
                pep.getNumberofTerm(), pep.getPI(),
                (ISequestPeptideFormat<?>) pep.getPeptideFormat());

        this.setEnzyme(pep.getEnzyme());
    }

    /**
     * Create a peptide from the values
     *
     * @param sequence sequece with terminals
     * @param rank     rank of this peptide
     * @param charge
     * @param rsp
     * @param mh
     * @param dcn
     * @param xcorr
     * @param sp
     * @param ions
     * @param refs     references from out (Null not permitted )
     */
    public SequestPeptide(String baseName, int scanNumBeg, int scanNumEnd,
                          String sequence, short charge, short rsp, double mh,
                          double deltaMs, short rank, float dcn, float xcorr, float sp,
                          String ions, HashSet<ProteinReference> refs,
                          ISequestPeptideFormat<?> formatter)
    {

        super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
                rank, refs, formatter);

        this.rsp = rsp;
        this.deltaCn = dcn;
        this.xcorr = xcorr;
        this.sp = sp;
        this.ions = ions;
    }

    /**
     * Create a peptide from the values
     *
     * @param scanname the sequestscan name instance.
     * @param sequence sequece with terminals
     * @param rank     rank of this peptide
     * @param charge
     * @param rsp
     * @param mh
     * @param dcn
     * @param xcorr
     * @param sp
     * @param ions
     * @param refs     references from out (Null not permitted )
     */
    public SequestPeptide(SequestScanName scanname, String sequence,
                          short charge, short rsp, double mh, double deltaMs, short rank,
                          float dcn, float xcorr, float sp, String ions,
                          HashSet<ProteinReference> refs, ISequestPeptideFormat<?> formatter)
    {
        super(scanname, sequence, charge, mh, deltaMs, rank, refs, formatter);

        this.rsp = rsp;
        this.deltaCn = dcn;
        this.xcorr = xcorr;
        this.sp = sp;
        this.ions = ions;
    }

    /**
     * Peptide from exported file of Bioworks
     *
     * @param scanNum
     * @param sequence
     * @param charge
     * @param mh
     * @param deltaMs
     * @param dcn
     * @param xcorr
     * @param sp
     * @param rsp
     * @param ions
     * @param refs
     */
    public SequestPeptide(String scanNum, String sequence, short charge,
                          short rsp, double mh, double deltaMs, short rank, float dcn,
                          float xcorr, float sp, String ions, HashSet<ProteinReference> refs,
                          ISequestPeptideFormat<?> formatter)
    {

        super(scanNum, sequence, charge, mh, deltaMs, rank, refs, formatter);

        this.rsp = rsp;
        this.deltaCn = dcn;
        this.xcorr = xcorr;
        this.sp = sp;
        this.ions = ions;
    }

    /**
     * Mainly for the PeptideFormater input
     *
     * @param scanNum
     * @param sequence
     * @param charge
     * @param mh
     * @param deltaMs
     * @param dcn
     * @param xcorr
     * @param sp
     * @param rsp
     * @param ions
     * @param sim
     * @param refs
     * @param numofterms
     * @param probability
     */
    public SequestPeptide(String scanNum, String sequence, short charge,
                          double mh, double deltaMs, short rank, float dcn, float xcorr,
                          float sp, short rsp, String ions, float sim,
                          HashSet<ProteinReference> refs, short numofterms, float pi,
                          ISequestPeptideFormat<?> formatter)
    {

        super(scanNum, sequence, charge, mh, deltaMs, rank, refs, pi,
                numofterms, formatter);

        this.rsp = rsp;
        this.deltaCn = dcn;
        this.xcorr = xcorr;
        this.sp = sp;
        this.ions = ions;

        this.setSim(sim);
//		this.setProbability(probability);
    }

    /**
     * construct from the peptide array.
     *
     * @param peptideArray
     * @see ProReaderConstant
     * @deprecated
     */
    @Deprecated
    public SequestPeptide(String[] peptideArray, ISequestPeptideFormat<?> formatter)
    {
        super(peptideArray[scanColumn], peptideArray[sequenceColumn], Short
                        .parseShort(peptideArray[chargeColumn]), Double
                        .parseDouble(peptideArray[massColumn]), Double
                        .parseDouble(peptideArray[deltaMassColumn]), (short) 0,
                getProteins(peptideArray[proteinColumn]), formatter);

        this.xcorr = Float.parseFloat(peptideArray[xcorrColumn]);
        this.sp = Float.parseFloat(peptideArray[spColumn]);
        this.rsp = Short.parseShort(peptideArray[rspColumn]);
        /*
         * If from the list file ,trim the space.
         */
        this.ions = peptideArray[ionsColumn].trim();

        String deltacnString = peptideArray[deltaCnColumn];
        this.deltaCn = deltacnString.equals("-") ? 1f : Float
                .parseFloat(deltacnString);

        /*
         * In the excel or xml file outputed by biowork, sometimes the deltacn
         * equals 0 indicated there is no other peptide matched, this peptide is
         * the only one candidate. This may be occured when database is quite
         * samll.
         */
        if (this.deltaCn == 0f || this.deltaCn > 1f)
            this.deltaCn = 1f;
    }

    /*
     * Translate formatted protein string to the list of reference
     *
     * @param proteins @return
     */
    private static HashSet<ProteinReference> getProteins(String proteins)
    {
        HashSet<ProteinReference> references = new HashSet<ProteinReference>();
        String[] names = StringUtil.split(proteins, ProteinNameSpliter);
        for (String name : names)
            references.add(ProteinReference.parse(name));

        return references;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide#getXcorr()
     */
    public float getXcorr()
    {
        return this.xcorr;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide#getDeltaCn
     * ()
     */
    public float getDeltaCn()
    {
        return this.deltaCn;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide#getSp()
     */
    public float getSp()
    {
        return this.sp;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide#getRsp()
     */
    public short getRsp()
    {
        return this.rsp;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide#getIons()
     */
    public String getIons()
    {
        return this.ions;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide#getIonPercent
     * ()
     */
    public float getIonPercent()
    {
        if (this.ionPercent < 0f)
            this.ionPercent = getIonPercent(this.ions);

        return this.ionPercent;
    }

    /**
     * Get the preference name for the dta or out file which generated this
     * peptide identification. e.g. for if the scan name is "yeast_10m, 2346 -
     * 2347", and the charge state is 2, then the the returned name is
     * yeast_10m.2346.2347.2. So one only to add the extension (.dta or .out)
     * for the accessement of the dta or out file.
     *
     * @return preference name of the scan file e.g. yeast_10m.2346.2347.2
     */
    public String getDtaOutPreference()
    {
        String name = this.getBaseName();
        if (name == null) {
            throw new NullPointerException(
                    "Cann't find filename from the scan number: "
                            + this.getScanNum());
        }

        StringBuilder sb = new StringBuilder(name);
        sb.append('.').append(this.getScanNumBeg()).append('.').append(
                this.getScanNumEnd()).append('.').append(this.getCharge());

        return sb.toString();
    }

    /**
     * An allene peptide; This peptide is commonly from the anambiguate
     * identification of peptide from low mass spectral. In low mass accuracy
     * spectral, peptide with some amino acid may be unclassified. e.g. I & L
     * and so on. These peptide may be identified with same scores. Thus, add to
     * allelePep;
     */
    public void setAllelePeptide(SequestPeptide pep)
    {
        if (this.allelelist == null)
            this.allelelist = new ArrayList<SequestPeptide>(2);
        this.allelelist.add(pep);
    }

    /**
     * An allene peptide; This peptide is commonly from the anambiguate
     * identification of peptide from low mass spectral. In low mass accuracy
     * spectral, peptide with some amino acid may be unclassified. e.g. I & L
     * and so on. These peptide may be identified with same scores. Thus, add to
     * allelePep;
     *
     * @return the allele peptide if any.(or Null)
     */
    public IPeptide[] getAllelePeptide()
    {
        if (this.allelelist == null)
            return null;

        return this.allelelist.toArray(new SequestPeptide[this.allelelist
                .size()]);
    }

    /**
     * In some conditions, this value must be changed;
     *
     * @param xcorr new xcorr value
     */
    public void setXcorr(float xcorr)
    {
        this.xcorr = xcorr;
    }

    /**
     * @param dcn the dcn to set
     */
    public void setDeltaCn(float dcn)
    {
        this.deltaCn = dcn;
    }

    /**
     * Compute float value of matched ion percent
     *
     * @param ions
     * @return value of ion percent;
     */
    public static final float getIonPercent(String ions)
    {
        int point = ions.indexOf('/');
        float match = Float.parseFloat(ions.substring(0, point));
        float total = Float.parseFloat(ions.substring(point + 1));

        return match / total;
    }

    /*
     * The xcorr value is used as the primary score
     *
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#getPrimaryScore()
     */
    @Override
    public float getPrimaryScore()
    {
        return this.xcorr;
    }

    /*
     * (non-Javadoc)
     *
     * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
     */
    @Override
    public PeptideType getPeptideType()
    {
        return PeptideType.SEQUEST;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#setPeptideFormat(
     * cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat)
     */
    @Override
    public void setPeptideFormat(IPeptideFormat<?> format)
    {

        if (format == null) {
            return;
        }

        if (format instanceof ISequestPeptideFormat<?>) {
            super.setPeptideFormat(format);
        } else
            throw new IllegalArgumentException(
                    "The formater for set must be sequest formater");
    }

}
