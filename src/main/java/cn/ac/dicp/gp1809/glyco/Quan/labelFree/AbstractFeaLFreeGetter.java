/*
 ******************************************************************************
 * File: GlycoFeaLFreeGetter.java * * * Created on 2011-3-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSpecStrucGetter;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.quant.profile.MS1PixelGetter;
import cn.ac.dicp.gp1809.util.DecimalFormats;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author ck
 * @version 2011-3-21, 13:15:20
 */
public abstract class AbstractFeaLFreeGetter
{

    protected MS1PixelGetter pixGetter;
    protected NGlycoSpecStrucGetter specGetter;
    protected GlycoJudgeParameter para;
    protected int glycanType;
    protected HashMap<Integer, ArrayList<NGlycoSSM>> glySpecMap;

    protected HashMap<String, IGlycoPeptide> pepMap;
    protected HashMap<String, IGlycoPeptide> glycoPepMap;

    /**
     * the variable modification symbol which represent glycosylation, used in O-linked glycans
     */
    protected HashSet<Character> glycoModSet;

    protected float rtTole = 0.0f;
    protected double mzThresPPM, mzThresAMU;
    protected double Asp_Asn = 0.984016;
    protected DecimalFormat df5 = DecimalFormats.DF0_5;

    public AbstractFeaLFreeGetter(String peakfile, int glycanType) throws IOException, XMLStreamException
    {
        this(peakfile, GlycoJudgeParameter.defaultParameter(), glycanType);
    }

    public AbstractFeaLFreeGetter(String peakfile, GlycoJudgeParameter para,
            int glycanType) throws IOException, XMLStreamException
    {
        this.pixGetter = new MS1PixelGetter(peakfile);
        this.para = para;
        this.glycanType = glycanType;
        this.glycoPepMap = new HashMap<String, IGlycoPeptide>();
        this.initial(peakfile);
    }

    /**
     * @param args
     * @throws IOException
     * @throws InvalidEnzymeCleavageSiteException
     * @throws ModsReadingException
     * @throws PeptideParsingException
     */
    public static void main(String[] args) throws Exception
    {
        // TODO Auto-generated method stub

        String file = "F:\\data\\GlycoQuant\\�½��ļ���\\20110125_AHSG_dimethyl_direct_RP_HCD_110126195943.mzXML";
        String iden = "F:\\data\\GlycoQuant\\�½��ļ���\\110323_110124_AHSG_dimethyl_deglyco_CID.csv";

    }

    private void initial(String peakfile) throws IOException, XMLStreamException
    {

        this.specGetter = new NGlycoSpecStrucGetter(peakfile, para);
        this.pepMap = new HashMap<String, IGlycoPeptide>();

        this.rtTole = para.getRtTole();
        this.mzThresPPM = para.getMzThresPPM();
        this.mzThresAMU = para.getMzThresAMU();
    }

    public HashMap<String, IGlycoPeptide> getPepMap()
    {
        return pepMap;
    }

    public HashMap<String, IGlycoPeptide> getGlycoPepMap()
    {
        return glycoPepMap;
    }

    public double getMS1TotalCurrent()
    {
        return this.pixGetter.getMS1TotalCurrent();
    }

    /**
     * @param peptide
     * @param aam
     */
    public abstract void addPeptide(IGlycoPeptide peptide, AminoacidModification aam);

    /**
     * @return
     */
    public abstract HashMap<String, FreeFeatures> getGlycoFeatures();

}
