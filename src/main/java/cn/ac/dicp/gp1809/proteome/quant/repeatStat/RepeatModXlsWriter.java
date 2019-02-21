/*
 ******************************************************************************
 * File: RepeatModXlsWriter.java * * * Created on 2011-11-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.repeatStat;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFreePairXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.*;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 * @version 2011-11-15, 15:18:04
 */
public class RepeatModXlsWriter
{
    private File[] files;
    private String output;
    private LabelType type;
    private ExcelWriter writer;

    private double[][] proRatioStat;
    private double[][] abRatioStat;
    private double[][] reRatioStat;
    private double[][] range;

    private int[] proSummary;
    private int[] siteSummary;
    private int totalNum;
    private int totalSiteNum;

    private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;

    public RepeatModXlsWriter(File[] files, String output, LabelType type)
    {
        this.files = files;
        this.output = output;
        this.type = type;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

    public void stat(boolean nomod, boolean normal, ModInfo[] mods, String[] ratioNames,
            double[] theoryRatios, double[] usedTheoryRatios, int[] outputRatio)
            throws Exception
    {

        this.writer = new ExcelWriter(output, new String[]{"Protein", "Site"});
        ExcelFormat f1 = ExcelFormat.indexFormat;
        ExcelFormat f2 = ExcelFormat.normalFormat;

        QModCalculator calculator = new QModCalculator(mods);

        int ratioNum = ratioNames.length;
        this.proRatioStat = new double[ratioNum][5];
        this.abRatioStat = new double[ratioNum][5];
        this.reRatioStat = new double[ratioNum][5];

        range = new double[ratioNum][5];
        for (int i = 0; i < usedTheoryRatios.length; i++) {
            double tr = usedTheoryRatios[i];
            range[i][0] = tr * 0.5;
            range[i][1] = tr * 0.8;
            range[i][2] = tr * 1.2;
            range[i][3] = tr * 2.0;
        }

        int fileNum = files.length;
        this.proSummary = new int[fileNum];
        this.siteSummary = new int[fileNum];

        String[] fileNames = new String[fileNum];
        HashMap<String, QModsResult>[] modResultMap = new HashMap[fileNum];
        HashSet<String> totalRefSet = new HashSet<String>();
        for (int i = 0; i < fileNum; i++) {

            String name = files[i].getName();
            fileNames[i] = name.substring(0, name.length() - 5);

            modResultMap[i] = new HashMap<String, QModsResult>();

            if (type == LabelType.LabelFree) {

                LFreePairXMLReader xmlreader = new LFreePairXMLReader(files[i]);
                xmlreader.readAllPairs();
                xmlreader.setTheoryRatio(theoryRatios);

                QuanResult[] quanResults = xmlreader.getAllResult(nomod, normal, outputRatio);

                for (int j = 0; j < quanResults.length; j++) {

                    QModsResult[] result = calculator.calculte(quanResults[j]);
                    if (result != null) {
                        for (int k = 0; k < result.length; k++) {
                            String ref = result[k].getRef();
                            modResultMap[i].put(ref, result[k]);
                            totalRefSet.add(ref);
                        }
                    }
                }

            } else {

                LabelFeaturesXMLReader xmlreader = new LabelFeaturesXMLReader(files[i]);
//				xmlreader.readAllFeatures();
                xmlreader.setTheoryRatio(theoryRatios);

                QuanResult[] quanResults = xmlreader.getAllResult(nomod, normal, outputRatio);

                for (int j = 0; j < quanResults.length; j++) {

                    QModsResult[] result = calculator.calculte(quanResults[j]);
                    if (result != null) {
                        for (int k = 0; k < result.length; k++) {
                            String ref = result[k].getRef();
                            modResultMap[i].put(ref, result[k]);
                            totalRefSet.add(ref);
                        }
                    }
                }
            }
        }

        this.addTitle(ratioNames, fileNames);

        Iterator<String> it = totalRefSet.iterator();
        HashSet<String> usedSite = new HashSet<String>();
        int id = 0;
        while (it.hasNext()) {

            String ref = it.next();
            double[][] proRatios = new double[ratioNum][fileNum];
            int repeatNum = 0;

            HashMap<String, String> aaroundMap = new HashMap<String, String>();
            HashMap<String, double[]>[] abMaps = new HashMap[fileNum];
            HashMap<String, double[]>[] reMaps = new HashMap[fileNum];

            for (int i = 0; i < fileNum; i++) {

                abMaps[i] = new HashMap<String, double[]>();
                reMaps[i] = new HashMap<String, double[]>();

                if (modResultMap[i].containsKey(ref)) {

                    QModsResult modsResult = modResultMap[i].get(ref);
                    repeatNum++;
                    double[] ratio = modsResult.getProRatio();
                    for (int j = 0; j < ratio.length; j++) {
                        proRatios[j][i] = ratio[i];
                    }

                    QModResult[] mrs = modsResult.getQModResult();
                    for (int j = 0; j < mrs.length; j++) {

                        HashMap<Integer, ModifLabelPair> modSiteMap = mrs[j].getPairMap();
                        Iterator<Integer> locIt = modSiteMap.keySet().iterator();
                        while (locIt.hasNext()) {

                            Integer loc = locIt.next();
                            ModifLabelPair modLabelPair = modSiteMap.get(loc);
                            String key = modLabelPair.getSite();
                            String aaround = modLabelPair.getAAround();
                            double[] abRatios = modLabelPair.getRatio();
                            double[] reRatios = modLabelPair.getRelaRatio();

                            aaroundMap.put(key, aaround);
                            abMaps[i].put(key, abRatios);
                            reMaps[i].put(key, reRatios);
                        }
                    }

                } else {
                    for (int j = 0; j < ratioNum; j++) {
                        proRatios[j][i] = 0;
                    }
                }
            }

            ProModSiteStatInfo proInfo = new ProModSiteStatInfo(++id, repeatNum, ref, proRatios);
            double[] ave = proInfo.getAveRatios();
            totalNum++;
            proSummary[repeatNum - 1]++;
            for (int k = 0; k < ave.length; k++) {
                if (ave[k] >= range[k][3]) {
                    this.proRatioStat[k][4]++;
                } else if (ave[k] < range[k][3] && ave[k] >= range[k][2]) {
                    this.proRatioStat[k][3]++;
                } else if (ave[k] < range[k][2] && ave[k] >= range[k][1]) {
                    this.proRatioStat[k][2]++;
                } else if (ave[k] < range[k][1] && ave[k] >= range[k][0]) {
                    this.proRatioStat[k][1]++;
                } else if (ave[k] < range[k][0] && ave[k] > 0) {
                    this.proRatioStat[k][0]++;
                }
            }

            writer.addContent(proInfo.toString(), 0, f1);

            Iterator<String> siteIt = aaroundMap.keySet().iterator();
            while (siteIt.hasNext()) {

                String key = siteIt.next();
                char aa = key.charAt(0);
                int loc = Integer.parseInt(key.substring(1));
                String aaround = aaroundMap.get(key);
                double[][] abSiteRatios = new double[ratioNum][fileNum];
                double[][] reSiteRatios = new double[ratioNum][fileNum];
                int siteRepeatNum = 0;

                for (int i = 0; i < fileNum; i++) {

                    if (abMaps[i].containsKey(key)) {
                        siteRepeatNum++;
                        double[] abr = abMaps[i].get(key);
                        double[] rer = reMaps[i].get(key);

                        for (int j = 0; j < abr.length; j++) {
                            abSiteRatios[j][i] = abr[j];
                            reSiteRatios[j][i] = rer[j];
                        }

                    } else {

                        for (int j = 0; j < ratioNum; j++) {
                            abSiteRatios[j][i] = 0;
                            reSiteRatios[j][i] = 0;
                        }
                    }
                }

                OneModSiteStatInfo modSiteInfo = new OneModSiteStatInfo(siteRepeatNum,
                        ref, loc, aa, aaround, abSiteRatios, reSiteRatios);

                if (usedSite.contains(ref + "" + key)) {

                    writer.addContent("\t\t" + modSiteInfo.toStringNoRef(), 0, f2);

                } else {

                    writer.addContent(modSiteInfo.toString(), 1, f2);
                    writer.addContent("\t\t" + modSiteInfo.toStringNoRef(), 0, f2);
                    usedSite.add(ref + "" + key);
                    totalSiteNum++;
                    siteSummary[siteRepeatNum - 1]++;

                    double[] abAve = modSiteInfo.getAbAve();
                    double[] reAve = modSiteInfo.getReAve();

                    for (int i = 0; i < abAve.length; i++) {

                        if (abAve[i] >= range[i][3]) {
                            this.abRatioStat[i][4]++;
                        } else if (abAve[i] < range[i][3] && abAve[i] >= range[i][2]) {
                            this.abRatioStat[i][3]++;
                        } else if (abAve[i] < range[i][2] && abAve[i] >= range[i][1]) {
                            this.abRatioStat[i][2]++;
                        } else if (abAve[i] < range[i][1] && abAve[i] >= range[i][0]) {
                            this.abRatioStat[i][1]++;
                        } else if (abAve[i] < range[i][0] && abAve[i] > 0) {
                            this.abRatioStat[i][0]++;
                        }

                        if (reAve[i] >= range[i][3]) {
                            this.reRatioStat[i][4]++;
                        } else if (reAve[i] < range[i][3] && reAve[i] >= range[i][2]) {
                            this.reRatioStat[i][3]++;
                        } else if (reAve[i] < range[i][2] && reAve[i] >= range[i][1]) {
                            this.reRatioStat[i][2]++;
                        } else if (reAve[i] < range[i][1] && reAve[i] >= range[i][0]) {
                            this.reRatioStat[i][1]++;
                        } else if (reAve[i] < range[i][0] && reAve[i] > 0) {
                            this.reRatioStat[i][0]++;
                        }
                    }
                }
            }
        }

        this.addSummary(fileNum, ratioNames);
        this.writer.close();
        System.gc();
    }

    private void addTitle(String[] ratioNames, String[] fileNames) throws RowsExceededException, WriteException
    {

        ExcelFormat f1 = ExcelFormat.normalFormat;
        StringBuilder sb1 = new StringBuilder();
        sb1.append("Index\tReference\tRepeat num\t");
        for (int i = 0; i < ratioNames.length; i++) {
            sb1.append(ratioNames[i] + "_ave\t" + ratioNames[i] + "_RSD\t");
            for (int j = 0; j < fileNames.length; j++) {
                sb1.append(fileNames[j] + "\t");
            }
        }
        this.writer.addTitle(sb1.toString(), 0, f1);

        StringBuilder sb2 = new StringBuilder();
        sb2.append("\t\tRepeat num\tSite\tSequence around\t");
        for (int i = 0; i < ratioNames.length; i++) {
            sb2.append(ratioNames[i] + "_absolute_ave\t" + ratioNames[i] + "_absolute_RSD\t");
            for (int j = 0; j < fileNames.length; j++) {
                sb2.append(fileNames[j] + "\t");
            }
            sb2.append(ratioNames[i] + "_relative_ave\t" + ratioNames[i] + "_relative_RSD\t");
            for (int j = 0; j < fileNames.length; j++) {
                sb2.append(fileNames[j] + "\t");
            }
            sb2.append("Reference");
        }
        this.writer.addTitle(sb2.toString(), 0, f1);
        this.writer.addTitle(sb2.substring(2), 1, f1);
    }

    private void addSummary(int fileNum, String[] ratioNames) throws RowsExceededException, WriteException
    {

        ExcelFormat f1 = new ExcelFormat(false, 0);
        writer.addTitle("\n\n\n-------------------Summary-------------------\n", 0, f1);
        StringBuilder sb = new StringBuilder("\n");
        sb.append("\tTotal Protein\tPercent\t").append("Total Site\tPercent\n");
        for (int i = fileNum; i > 0; i--) {
            sb.append("In " + i + " files:\t").append(proSummary[i - 1]).append("\t").
                    append(dfPer.format((float) proSummary[i - 1] / (float) totalNum)).
                    append("\t").append(siteSummary[i - 1]).append("\t").
                    append(dfPer.format((float) siteSummary[i - 1] / (float) totalSiteNum)).
                    append("\n");
        }
        sb.append("\n\n");

        for (int i = 0; i < ratioNames.length; i++) {
            sb.append("Ratio\t").append("Absolute Ave Ratio\tPercent\t").append("Relative Ave Ratio\tPercent\n");
            sb.append(">=2\t").append(abRatioStat[i][4]).append("\t")
                    .append(dfPer.format((float) abRatioStat[i][4] / totalSiteNum)).append("\t")
                    .append(reRatioStat[i][4]).append("\t").append(dfPer.format((float) reRatioStat[i][4] / totalSiteNum)).append("\n");
            sb.append("1.2~2\t").append(abRatioStat[i][3]).append("\t")
                    .append(dfPer.format((float) abRatioStat[i][3] / totalSiteNum)).append("\t")
                    .append(reRatioStat[i][3]).append("\t").append(dfPer.format((float) reRatioStat[i][3] / totalSiteNum)).append("\n");
            sb.append("0.8~1.2\t").append(abRatioStat[i][2]).append("\t")
                    .append(dfPer.format((float) abRatioStat[i][2] / totalSiteNum)).append("\t")
                    .append(reRatioStat[i][2]).append("\t").append(dfPer.format((float) reRatioStat[i][2] / totalSiteNum)).append("\n");
            sb.append("0.5~0.8\t").append(abRatioStat[i][1]).append("\t")
                    .append(dfPer.format((float) abRatioStat[i][1] / totalSiteNum)).append("\t")
                    .append(reRatioStat[i][1]).append("\t").append(dfPer.format((float) reRatioStat[i][1] / totalSiteNum)).append("\n");
            sb.append("<0.5\t").append(abRatioStat[i][0]).append("\t")
                    .append(dfPer.format((float) abRatioStat[i][0] / totalSiteNum)).append("\t")
                    .append(reRatioStat[i][0]).append("\t").append(dfPer.format((float) reRatioStat[i][0] / totalSiteNum)).append("\n");

            sb.append("Total\t").append(totalSiteNum).append("\t").append("100%").append("\n");
            sb.append("\n\n");
        }
        writer.addContent(sb.toString(), 0, f1);
    }

}
