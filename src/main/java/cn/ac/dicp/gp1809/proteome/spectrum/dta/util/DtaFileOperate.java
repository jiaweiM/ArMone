/*
 ******************************************************************************
 * File: DtaFileOperate.java * * * Created on 07-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.proteometools.fileOperation.ScanFileGenerator;
import cn.ac.dicp.gp1809.proteome.proteometools.fileOperation.ScanFileUtil;
import cn.ac.dicp.gp1809.proteome.proteometools.fileOperation.ScanFromFile;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaFilenameFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;
import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;

/**
 * Operations for the dta
 *
 * @author Xinning
 * @version 0.1.1, 05-25-2010, 16:06:18
 */
public class DtaFileOperate
{

    private DtaFileOperate() { }

    /**
     * Copy the selected dta and out to a new directory
     *
     * @param scanlistfile text file contains scannumber and charge
     * @param originfolder origin folder
     * @param targetfolder target folder
     * @throws IOException
     */
    public static void copyTo(String scanlistfile, String originfolder, String targetfolder) throws IOException
    {
        ScanFromFile sffile = new ScanFromFile(scanlistfile);
        int[] scans = sffile.getScans();
        short[] charges = sffile.getCharges();

        copyTo(scans, charges, originfolder, targetfolder);
    }

    /**
     * Copy the dta files with specific scan number and charge state to the target folder
     *
     * @param scans
     * @param charges
     * @param originfolder
     * @param targetfolder
     * @throws IOException
     */
    public static void copyTo(int[] scans, short[] charges, String originfolder, String targetfolder) throws IOException
    {
        File originfolderfile = new File(originfolder);
        File targetfolderfile = new File(targetfolder);

        ScanFileUtil sfutil = new ScanFileUtil(originfolder, ScanFileUtil.DTA_FILE);
        ScanFileGenerator sfgenerator = new ScanFileGenerator(sfutil);

        String[] filename = sfgenerator.getFileNames(scans, charges);

        if (!targetfolderfile.exists()) {
            targetfolderfile.mkdirs();
        }

        for (int i = 0; i < filename.length; i++) {
            String curtfilename = filename[i];
            FileUtil.copyTo(new File(originfolderfile, curtfilename), targetfolderfile);
        }

    }

    /**
     * Only copy dta file with specific precursor mass to the target folder;
     *
     * @param originfolder
     * @param targerfolder
     * @throws DtaFileParsingException
     * @throws IOException
     */
    public static void copyByPreMass(String originfolder, String targetfolder,
            final double MH) throws DtaFileParsingException, IOException
    {
        final double tolence = 2.0;
        File targetDir = new File(targetfolder);

        SequestBatchDtaReader breader = new SequestBatchDtaReader(originfolder);
        SequestScanDta dfile;
        while ((dfile = breader.getNextDta(true)) != null) {
            double mh = dfile.getPrecursorMH();
            if (Math.abs(mh - MH) <= tolence)
                FileUtil.copyTo(breader.getNameofCurtDta(), targetDir);
        }
    }

    /**
     * 当利用多级激活和普通的二级谱进行比较时，只把能匹配的谱图取出(multi stage and normal)
     *
     * @param originfolder
     * @param targetfolder
     * @throws DtaFileParsingException
     * @throws IOException
     */
    public static void copyByMatch(String originfolder) throws DtaFileParsingException, IOException
    {
        File tfolder1 = new File(originfolder + "_1");
        File tfolder2 = new File(originfolder + "_2");

        if (tfolder1.exists() || tfolder2.exists())
            throw new RuntimeException("Target folder exists, please check it out!");

        File ori = new File(originfolder);
        File[] files = ori.listFiles(new SequestDtaFilenameFilter());
        int len = files.length;

        if (len == 0) {
            System.out.println("Can't find dta file in the folder!");
            return;
        }


        SequestScanDta[] dtainfors = new SequestScanDta[len];
        for (int i = 0; i < len; i++) {
            dtainfors[i] = new SequestDtaReader(files[i]).getDtaFile(false);
        }

        Arrays.sort(dtainfors);

        double premass = dtainfors[0].getPrecursorMZ();
        int prescan = dtainfors[0].getScanNumberBeg();
        for (int i = 1, n = len - 1; i < n; i++) {
            double mass = dtainfors[i].getPrecursorMZ();
            int scan = dtainfors[i].getScanNumberBeg();

            //ֻ��scan���1������ĸ������ͬ�Ĳ�Ϊmatch scan
            if (scan - prescan == 1 && premass == mass) {
                FileUtil.copyTo(dtainfors[i - 1].getFile(), tfolder1);
                FileUtil.copyTo(dtainfors[i].getFile(), tfolder2);

                if (++i >= n)
                    break;

                premass = dtainfors[i].getPrecursorMZ();
                prescan = dtainfors[i].getScanNumberBeg();
            } else {
                premass = mass;
                prescan = scan;
            }

        }
    }
}
