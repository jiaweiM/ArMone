/*
 ******************************************************************************
 * File:MzXMLReader.java * * * Created on 2010-4-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import cn.ac.dicp.gp1809.proteome.spectrum.*;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ms2.MS2ScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.MSXMLSequentialParser;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.ScanHeader;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;
import cn.ac.dicp.gp1809.util.DecimalFormats;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;

/**
 * Read mzXML file use jrap.
 *
 * @author ck
 * @version 2010-4-23, 15:56:34
 */
public class MzXMLReader implements IMzXMLReader, IBatchDtaReader
{
    private File file;
    private MSXMLSequentialParser msParser;
    private int maxScan;

    private int preScanNum;

    private MS1ScanList ms1ScanList;
    private MS2ScanList ms2ScanList;

    private String baseName;

    private double MS1TotalCurrent;

    public static final DecimalFormat df4 = DecimalFormats.DF0_4;

    public MzXMLReader(String fileName) throws FileNotFoundException, XMLStreamException
    {
        file = new File(fileName);
        msParser = new MSXMLSequentialParser();
        msParser.open(fileName);
        maxScan = msParser.getMaxScanNumber();

        this.ms1ScanList = new MS1ScanList(DtaType.MZXML);
        this.ms2ScanList = new MS2ScanList(DtaType.MZXML);

        String name = file.getName();
        int loc = name.lastIndexOf(".");
        if (loc == -1) {
            this.baseName = name;
        } else {
            this.baseName = name.substring(0, loc);
        }
    }

    /**
     * @param file
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public MzXMLReader(File file) throws FileNotFoundException, XMLStreamException
    {
        this.file = file;
        msParser = new MSXMLSequentialParser();
        msParser.open(file.getAbsolutePath());
        maxScan = msParser.getMaxScanNumber();

        this.ms1ScanList = new MS1ScanList(DtaType.MZXML);
        this.ms2ScanList = new MS2ScanList(DtaType.MZXML);

        String name = file.getName();
        int loc = name.lastIndexOf(".");
        if (loc == -1) {
            this.baseName = name;
        } else {
            this.baseName = name.substring(0, loc);
        }
    }

    /**
     * Get ms2 peak list from the mzXML file directly without writing to the temp file.
     *
     * @param scan_num
     * @return
     */
    public IMS2PeakList getMS2PeakList(int scan_num)
    {
        return (MS2PeakList) this.ms2ScanList.getScan(scan_num).getPeakList();
    }

    public int getMaxScan()
    {
        return maxScan;
    }

    @Override
    public IPeakList getPeakList(int scan_num)
    {
        return this.ms1ScanList.getScan(scan_num).getPeakList();
    }

    @Override
    public ISpectrum getNextSpectrum()
    {
        if (msParser.hasNextScan()) {

            Scan scan = null;

            try {
                scan = msParser.getNextScan();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }

            ScanHeader header = scan.header;

            int scanNum = header.getNum();
            int msLevel = header.getMsLevel();
            double rt = header.getDoubleRetentionTime() / 60.0d;
            float totIonCurrent = header.getTotIonCurrent();
            float basepeakInten = header.getBasePeakIntensity();
            Description des;

            IPeakList peaklist;

            if (msLevel == 1) {

                this.preScanNum = scanNum;
                des = new Description(scanNum, msLevel, rt, totIonCurrent);

                peaklist = new MS1PeakList();
                double[][] mzIntenList = scan.getMassIntensityList();

                for (int i = 0; i < mzIntenList[0].length; i++) {
                    double mz = mzIntenList[0][i];
                    double inten = mzIntenList[1][i];
                    IPeak ip = new Peak(mz, inten);
                    peaklist.add(ip);
                }

                MS1Scan ms1scan = new MS1Scan(des, peaklist);
                return ms1scan;
            } else {

                int precursornum = msLevel - 1;
                int precursorScanNum = this.preScanNum;
                double preMz = header.getPrecursorMz();
                int charge = header.getPrecursorCharge();
                double precursorInten = header.getPrecursorIntensity();
                des = new Description(scanNum, msLevel, rt, precursornum, precursorScanNum, preMz, charge, precursorInten);

                PrecursePeak ppeak = new PrecursePeak(precursorScanNum, preMz, precursorInten);
                ppeak.setCharge((short) charge);
                ppeak.setRT(rt);

                peaklist = new MS2PeakList();
                ((MS2PeakList) peaklist).setPrecursePeak(ppeak);

                double[][] mzIntenList = scan.getMassIntensityList();
                for (int i = 0; i < mzIntenList[0].length; i++) {
                    double mz = Double.parseDouble(df4.format(mzIntenList[0][i]));
                    double inten = Double.parseDouble(df4.format(mzIntenList[1][i]));
                    IPeak ip = new Peak(mz, inten);
                    peaklist.add(ip);
                }

                MS2Scan ms2scan = new MS2Scan(des, (MS2PeakList) peaklist);
                return ms2scan;
            }
        }

        return null;
    }


    @Override
    public MS1Scan getNextMS1Scan()
    {
        if (msParser.hasNextScan()) {

            Scan scan = null;

            try {
                scan = msParser.getNextScan();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }

            ScanHeader header = scan.header;

            int scanNum = header.getNum();
            int msLevel = header.getMsLevel();

            if (msLevel != 1) {
                return getNextMS1Scan();
            }

            double rt = header.getDoubleRetentionTime() / 60.0d;
            float totIonCurrent = header.getTotIonCurrent();
            Description des = new Description(scanNum, msLevel, rt, totIonCurrent);
            IPeakList peaklist;

            this.preScanNum = scanNum;

            peaklist = new MS1PeakList();
            double[][] mzIntenList = scan.getMassIntensityList();

            for (int i = 0; i < mzIntenList[0].length; i++) {
                double mz = mzIntenList[0][i];
                double inten = mzIntenList[1][i];
                IPeak ip = new Peak(mz, inten);
                peaklist.add(ip);
            }

            this.MS1TotalCurrent += peaklist.getTotIonCurrent();

            MS1Scan ms1scan = new MS1Scan(des, peaklist);
            return ms1scan;

        }

        return null;
    }

    @Override
    public MS2Scan getNextMS2Scan()
    {
        if (msParser.hasNextScan()) {

            Scan scan = null;

            try {
                scan = msParser.getNextScan();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }

            ScanHeader header = scan.header;

            int scanNum = header.getNum();
            int msLevel = header.getMsLevel();

            if (msLevel == 1) {
                this.preScanNum = scanNum;
                return getNextMS2Scan();
            }

            double rt = header.getDoubleRetentionTime() / 60.0d;
            float totIonCurrent = header.getTotIonCurrent();
            Description des = new Description(scanNum, msLevel, rt, totIonCurrent);

            int precursornum = msLevel - 1;
            int precursorScanNum = this.preScanNum;
            double preMz = header.getPrecursorMz();
            int charge = header.getPrecursorCharge();
            double precursorInten = header.getPrecursorIntensity();
            des = new Description(scanNum, msLevel, rt, precursornum, precursorScanNum, preMz, charge, precursorInten);

            PrecursePeak ppeak = new PrecursePeak();
            ppeak.setCharge((short) charge);
            ppeak.setIntensity(precursorInten);
            ppeak.setMz(preMz);
            ppeak.setRT(rt);

            IMS2PeakList peaklist = new MS2PeakList();
            double[][] mzIntenList = scan.getMassIntensityList();
            for (int i = 0; i < mzIntenList[0].length; i++) {
                double mz = Double.parseDouble(df4.format(mzIntenList[0][i]));
                double inten = Double.parseDouble(df4.format(mzIntenList[1][i]));
                IPeak ip = new Peak(mz, inten);
                peaklist.add(ip);
            }

            peaklist.setPrecursePeak(ppeak);

            MS2Scan ms2scan = new MS2Scan(des, peaklist);
            return ms2scan;
        }

        return null;
    }

    @Override
    public void rapMS1ScanList()
    {
        MS1Scan ms1scan;
        while ((ms1scan = getNextMS1Scan()) != null) {
            this.ms1ScanList.add(ms1scan);
        }
    }

    @Override
    public void rapMS2ScanList()
    {
        MS2Scan ms2scan;
        while ((ms2scan = getNextMS2Scan()) != null) {
            this.ms2ScanList.add(ms2scan);
        }
    }

    @Override
    public void rapScanList()
    {
        ISpectrum is;
        while ((is = getNextSpectrum()) != null) {
            if (is.getMSLevel() == 1) {
                this.ms1ScanList.add(is);
            } else {
                this.ms2ScanList.add(is);
            }
        }
    }

    @Override
    public MS1ScanList getMS1ScanList()
    {
        return this.ms1ScanList;
    }

    @Override
    public MS2ScanList getMS2ScanList()
    {
        return this.ms2ScanList;
    }

    @Override
    public void close()
    {
        this.msParser.close();
        this.ms1ScanList = null;
        this.ms2ScanList = null;
        System.gc();
    }

    @Override
    public DtaType getDtaType()
    {
        return DtaType.MZXML;
    }

    @Override
    public File getFile()
    {
        return file;
    }

    @Override
    public String getNameofCurtDta()
    {
        return "";
    }

    @Override
    public IScanDta getNextDta(boolean isIncludePeakList)
    {
        MS2Scan ms2scan = this.getNextMS2Scan();
        int scanNum = ms2scan.getScanNum();
        double premz = ms2scan.getPrecursorMZ();
        short charge = ms2scan.getCharge();
        double mh = SpectrumUtil.getMH(premz, charge);
        IMS2PeakList peaklist = ms2scan.getPeakList();

        IScanName parseName = new SequestScanName(baseName, scanNum, scanNum, charge, "dta");

        MS2ScanDta ms2dta;
        if (isIncludePeakList) {
            ms2dta = new MS2ScanDta(parseName, peaklist);
        } else {
            ms2dta = new MS2ScanDta(parseName, mh);
        }

        return ms2dta;
    }

    @Override
    public int getNumberofDtas()
    {
        return 0;
    }

    @Override
    public double getMS1TotalCurrent()
    {
        return MS1TotalCurrent;
    }

    /**
     * @param args
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException
    {
        long startTime = System.currentTimeMillis();

        String file = "Z:\\MaoJiawei\\170722_uf_g_3.mzXML";

        MzXMLReader reader = new MzXMLReader(file);
        IMS2Scan scan = null;
        while ((scan = reader.getNextMS2Scan()) != null) {

        }
        reader.close();

        long endTime = System.currentTimeMillis();
        System.out.println("Run time:\t" + (endTime - startTime) / 1000 + "s");
    }

}
