/*
 * *****************************************************************************
 * File: PeptideListReader.java * * * Created on 09-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter.DefaultPeptideListHeader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.IPeptideListHeader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Reader for peptide list file (ppl)
 *
 * <p>
 * Changes:
 * <li>0.2, 04-22-2009: use inner accesser for peptide reading
 * <li>0.2.0.1, 06-08-2009: set default top n as {@link #MAX_TOPN}
 *
 * @author Xinning
 * @version 0.2.1.1, 06-08-2009, 14:51:09
 */
public class PeptideListReader implements IPeptideListReader
{
    /**
     * The maximum reported number of top matched peptides. The setting of topn by method {@link #setTopN(int)} will be
     * limited by this number. Default 50.
     */
    public static final int MAX_TOPN = 50;

    private int ntopscore = MAX_TOPN;

    private int curtIndex = -1;

    private PeptideListAccesser accesser;

    /**
     * The total number of peptides
     */
    private int totalPeps;

    /**
     * The scan and charge which have been read. key = scancharge, value=read peptide
     */
    private HashMap<String, Integer> readScan;

    private IPeptideFormat<?> formatter;

    //The formatter has been reset
    private boolean formatReset;

    public PeptideListReader(String listfile) throws FileDamageException, IOException
    {
        this(new File(listfile));
    }

    public PeptideListReader(File file) throws FileDamageException, IOException
    {
        this.readScan = new HashMap<>();
        this.accesser = new PeptideListAccesser(file);
        this.totalPeps = this.accesser.getNumberofPeptides();
        this.formatter = this.accesser.getPeptideFormat();

        this.setTopN(MAX_TOPN);
    }

    public PeptideListReader(PeptideListAccesser accesser)
    {
        this.readScan = new HashMap<>();
        this.accesser = accesser;
        this.totalPeps = this.accesser.getNumberofPeptides();
        this.formatter = this.accesser.getPeptideFormat();

        this.setTopN(MAX_TOPN);
    }

    /**
     * Check if the ppl(ppls) file is exist and is the original ppl file (not changed by others);
     *
     * @param pplfilename
     * @return
     * @throws IOException
     */
    public static boolean checkPPL(String pplfilename) throws IOException
    {
        File file = new File(pplfilename);
        if (!file.exists() || file.isDirectory())
            return false;

        boolean iftrue = true;
        BufferedReader reader = new BufferedReader(new FileReader(pplfilename));
        String firstline = reader.readLine();

        if (firstline == null || firstline.length() == 0) {
            return false;
        }

        StringBuilder sb = new StringBuilder(100);
        try {

            IPeptideListHeader header = DefaultPeptideListHeader.parseHeader(firstline);

            sb.append("Ppl file name: \"").append(
                    new File(pplfilename).getName()).append("\"").append(
                    IOConstant.lineSeparator);
            sb.append(header.getDescription()).append(IOConstant.lineSeparator);
        } catch (Exception e) {
            iftrue = false;
        }

        reader.close();
        return iftrue;
    }

    public static void main(String[] args) throws FileDamageException,
            IOException
    {


    }

    @Override
    public IPeptide getPeptide()
    {
        if (++this.curtIndex >= this.totalPeps) {
            return null;
        }

        IPeptide pep = this.accesser.getPeptide(this.curtIndex);
        String scanCharge = pep.getScanNum() + "_" + pep.getCharge();

        /*
         * Here, we assume that the top ranked peptides are read before the
         * peptides with lower ranks.
         */
        Integer num;
        if ((num = this.readScan.get(scanCharge)) != null) {
            if (num++ >= this.ntopscore) {
                this.readScan.put(scanCharge, num);
                return this.getPeptide();
            }
        } else
            num = 1;

        this.readScan.put(scanCharge, num);

        if (this.formatReset)
            pep.setPeptideFormat(this.formatter);

        return pep;
    }

    @Override
    public IMS2PeakList[] getPeakLists()
    {
        if (this.curtIndex == -1)
            return null;

        return this.accesser.getPeakLists(this.curtIndex);
    }

    @Override
    public int getCurtPeptideIndex()
    {
        return this.curtIndex;
    }

    @Override
    public int getNumberofPeptides()
    {
        return this.totalPeps;
    }

    @Override
    public int getTopN()
    {
        return this.ntopscore;
    }

    @Override
    public void setTopN(int topn)
    {
        if (topn < 1) {
            System.out.println("Top n <1, set to 1");
            this.ntopscore = 1;
        } else if (topn > MAX_TOPN) {
            this.ntopscore = MAX_TOPN;
        } else
            this.ntopscore = topn;

        System.out.println("Reading top " + this.ntopscore + " peptides per scan charge.");
    }

    @Override
    public ISearchParameter getSearchParameter()
    {
        return this.accesser.getSearchParameter();
    }


    @Override
    public IDecoyReferenceJudger getDecoyJudger()
    {
        return accesser.getDecoyJudger();
    }

    @Override
    public void setDecoyJudger(IDecoyReferenceJudger judger)
    {
        throw new IllegalArgumentException("Cannot set judger for the peptide list reader.");
    }

    public IPeptideListAccesser getAccesser()
    {
        return this.accesser;
    }

    @Override
    public IPeptideFormat<?> getPeptideFormat()
    {
        return this.formatter;
    }

    @Override
    public void setPeptideFormat(IPeptideFormat<?> format)
    {
        if (format != null) {
            this.formatter = format;
            this.formatReset = true;
        }
    }

    @Override
    public void setReplace(HashMap<Character, Character> replaceMap)
    {
    }


    @Override
    public PeptideType getPeptideType()
    {
        return this.accesser.getPeptideType();
    }

    @Override
    public ProteinNameAccesser getProNameAccesser()
    {
        return this.accesser.getProNameAccesser();
    }

    @Override
    public void close()
    {
        readScan = null;
        this.accesser.close();

        System.gc();
    }
}
