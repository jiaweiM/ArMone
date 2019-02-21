/*
 ******************************************************************************
 * File: OGlycanXlsWriter2.java * * * Created on 2015��1��20��
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author ck
 */
public class OGlycanXlsWriter2
{
    private static DecimalFormat df2 = DecimalFormats.DF0_2;
    private String projectName;
    private ExcelWriter writer;
    private ExcelFormat format;
    private double formThres;
    private double siteThres;
    private HashMap<String, Integer> countMapSiteHigh;
    private HashMap<String, Integer> countMapSiteLow;
    private HashMap<String, Integer> countMapGlycanSiteHigh;
    private HashMap<String, Integer> countMapGlycanSiteLow;
    private HashMap<String, OGlycanSiteInfo> contentMapSiteHigh;
    private HashMap<String, OGlycanSiteInfo> contentMapSiteLow;
    private HashMap<String, OGlycanSiteInfo> contentMapGlycanSiteHigh;
    private HashMap<String, OGlycanSiteInfo> contentMapGlycanSiteLow;

    public OGlycanXlsWriter2(String out, double formThres,
            double siteThres) throws IOException, RowsExceededException, WriteException
    {
        this.writer = new ExcelWriter(out, new String[]{"Content", "Glycopeptides", "Site-specific glycoforms", "Glycosylation sites all",
                "Glycosylation sites high", "Glycosylation sites low", "Site-specific glycoforms all"
                , "Site-specific glycoforms high", "Site-specific glycoforms low"});
        this.projectName = out.substring(0, out.length() - 4);
        this.format = ExcelFormat.normalFormat;
        this.formThres = formThres;
        this.siteThres = siteThres;
        this.countMapSiteHigh = new HashMap<String, Integer>();
        this.countMapSiteLow = new HashMap<String, Integer>();
        this.countMapGlycanSiteHigh = new HashMap<String, Integer>();
        this.countMapGlycanSiteLow = new HashMap<String, Integer>();
        this.contentMapSiteHigh = new HashMap<String, OGlycanSiteInfo>();
        this.contentMapSiteLow = new HashMap<String, OGlycanSiteInfo>();
        this.contentMapGlycanSiteHigh = new HashMap<String, OGlycanSiteInfo>();
        this.contentMapGlycanSiteLow = new HashMap<String, OGlycanSiteInfo>();
        this.addTitle();
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

    private void addTitle() throws RowsExceededException, WriteException
    {
        String title0 = "Scan\tPeptide Mr\tIon Score\tExpect\tLength\tProteins\tProtein\tModified sequence\tModified probability\tGlycan\tDelta F score\tOther possibilities";
        String title1 = "Site\tSequence window\tGlycan\tGlycan mass\tDelta F score\tSite score\tProteins\tProtein\tScan\tModified sequence\tModified probability";
        String title2 = "Site\tSequence window\tDelta F Score\tSite score\tProtein\tSpectra count";
        String title3 = "Site\tSequence window\tGlycan\tDelta F Score\tSite score\tProtein\tSpectra count";
        writer.addTitle(title0, 1, format);
        writer.addTitle(title1, 2, format);
        writer.addTitle(title2, 3, format);
        writer.addTitle(title2, 4, format);
        writer.addTitle(title2, 5, format);
        writer.addTitle(title3, 6, format);
        writer.addTitle(title3, 7, format);
        writer.addTitle(title3, 8, format);

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("\t").append("All of the O-glycopeptides, glycosylation sites and site-specific glycoforms identified from " + projectName).append("\n");
        sb.append("\n");
        sb.append("\t").append("Glycopeptides: all the glycopeptide identifications from " + projectName).append("\n");
        sb.append("\t").append("Site-specific glycoforms: all the site-specific glycoforms identified from " + projectName).append("\n");
        sb.append("\t").append("Glycosylation sites all: unique glycosylation sites").append("\n");
        sb.append("\t").append("Glycosylation sites high: unique glycosylation sites with unambiguous identification").append("\n");
        sb.append("\t").append("Glycosylation sites low: unique glycosylation sites with ambiguous identification").append("\n");
        sb.append("\t").append("Site-specific glycoforms all: unique site-specific glycoforms").append("\n");
        sb.append("\t").append("Site-specific glycoforms high: unique site-specific glycoforms with unambiguous identification").append("\n");
        sb.append("\t").append("Site-specific glycoforms low: unique site-specific glycoforms with ambiguous identification").append("\n");
        writer.addContent(sb.toString(), 0, format);
    }

    public void write(OGlycanPepInfo[] pepinfos) throws RowsExceededException, WriteException
    {

        StringBuilder sb = new StringBuilder();
        sb.append(pepinfos[0].toStringOutput());
        String deleref = pepinfos[0].getDeleRef();

        for (int i = 1; i < pepinfos.length; i++) {
            if (pepinfos[i] != null) {
                sb.append(pepinfos[i].getModseq()).append("\t");
                sb.append(pepinfos[i].getScoreseq()).append("\t");

                for (int j = 0; j < pepinfos[i].getUnits().length; j++) {
                    sb.append(pepinfos[i].getUnits()[j].getComposition() + "@");
                    sb.append(pepinfos[i].getPosition()[j]).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\t");
                break;
            }
        }

        this.writer.addContent(sb.toString(), 1, format);

        OGlycanSiteInfo[] siteInfos = pepinfos[0].getSiteInfo();
        for (int i = 0; i < siteInfos.length; i++) {

            this.writer.addContent(siteInfos[i].toStringOutput(), 2, format);

            String key1 = siteInfos[i].getSite() + "\t" + deleref;
            String key2 = siteInfos[i].getSite() + "\t" + siteInfos[i].getGlycoName() + "\t" + deleref;
            double formScore = siteInfos[i].getGlycoFormDeltaScore();
            double siteScore = siteInfos[i].getScore();

            if (formScore >= formThres && siteScore >= siteThres) {
                if (countMapSiteHigh.containsKey(key1)) {
                    countMapSiteHigh.put(key1, countMapSiteHigh.get(key1) + 1);
                    OGlycanSiteInfo info0 = contentMapSiteHigh.get(key1);
                    double formScore0 = info0.getGlycoFormDeltaScore();
                    double siteScore0 = info0.getScore();
                    if (siteScore > siteScore0) {
                        contentMapSiteHigh.put(key1, info0);
                    } else if (siteScore == siteScore0) {
                        if (formScore > formScore0) {
                            contentMapSiteHigh.put(key1, info0);
                        }
                    }
                } else {
                    countMapSiteHigh.put(key1, 1);
                    contentMapSiteHigh.put(key1, siteInfos[i]);
                }

                if (countMapGlycanSiteHigh.containsKey(key2)) {
                    countMapGlycanSiteHigh.put(key2, countMapGlycanSiteHigh.get(key2) + 1);
                    OGlycanSiteInfo info0 = contentMapGlycanSiteHigh.get(key2);
                    double formScore0 = info0.getGlycoFormDeltaScore();
                    double siteScore0 = info0.getScore();
                    if (siteScore > siteScore0) {
                        contentMapGlycanSiteHigh.put(key2, info0);
                    } else if (siteScore == siteScore0) {
                        if (formScore > formScore0) {
                            contentMapGlycanSiteHigh.put(key2, info0);
                        }
                    }
                } else {
                    countMapGlycanSiteHigh.put(key2, 1);
                    contentMapGlycanSiteHigh.put(key2, siteInfos[i]);
                }

            } else {
                if (countMapSiteLow.containsKey(key1)) {
                    countMapSiteLow.put(key1, countMapSiteLow.get(key1) + 1);
                    OGlycanSiteInfo info0 = contentMapSiteLow.get(key1);
                    double formScore0 = info0.getGlycoFormDeltaScore();
                    double siteScore0 = info0.getScore();
                    if (siteScore > siteScore0) {
                        contentMapSiteLow.put(key1, info0);
                    } else if (siteScore == siteScore0) {
                        if (formScore > formScore0) {
                            contentMapSiteLow.put(key1, info0);
                        }
                    }
                } else {
                    countMapSiteLow.put(key1, 1);
                    contentMapSiteLow.put(key1, siteInfos[i]);
                }

                if (countMapGlycanSiteLow.containsKey(key2)) {
                    countMapGlycanSiteLow.put(key2, countMapGlycanSiteLow.get(key2) + 1);
                    OGlycanSiteInfo info0 = contentMapGlycanSiteLow.get(key2);
                    double formScore0 = info0.getGlycoFormDeltaScore();
                    double siteScore0 = info0.getScore();
                    if (siteScore > siteScore0) {
                        contentMapGlycanSiteLow.put(key2, info0);
                    } else if (siteScore == siteScore0) {
                        if (formScore > formScore0) {
                            contentMapGlycanSiteLow.put(key2, info0);
                        }
                    }
                } else {
                    countMapGlycanSiteLow.put(key2, 1);
                    contentMapGlycanSiteLow.put(key2, siteInfos[i]);
                }
            }
        }
    }

    public void close() throws WriteException, IOException
    {

        for (String key : contentMapSiteHigh.keySet()) {
            OGlycanSiteInfo info = contentMapSiteHigh.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(info.getSite()).append("\t");
            sb.append(info.getSeqAround()).append("\t");
            sb.append(df2.format(info.getGlycoFormDeltaScore())).append("\t");
            sb.append(df2.format(info.getScore())).append("\t");
            sb.append(info.getDeleRef()).append("\t");
            writer.addContent(sb + "" + countMapSiteHigh.get(key), 4, format);

            if (contentMapSiteLow.containsKey(key)) {
                sb.append(countMapSiteHigh.get(key) + countMapSiteLow.get(key)).append("\t");
                writer.addContent(sb.toString(), 3, format);
            } else {
                sb.append(countMapSiteHigh.get(key)).append("\t");
                writer.addContent(sb.toString(), 3, format);
            }
        }

        for (String key : contentMapSiteLow.keySet()) {
            if (contentMapSiteHigh.containsKey(key)) continue;
            OGlycanSiteInfo info = contentMapSiteLow.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(info.getSite()).append("\t");
            sb.append(info.getSeqAround()).append("\t");
            sb.append(df2.format(info.getGlycoFormDeltaScore())).append("\t");
            sb.append(df2.format(info.getScore())).append("\t");
            sb.append(info.getDeleRef()).append("\t");
            sb.append(countMapSiteLow.get(key)).append("\t");
            writer.addContent(sb.toString(), 3, format);
            writer.addContent(sb.toString(), 5, format);
        }
        for (String key : contentMapGlycanSiteHigh.keySet()) {
            OGlycanSiteInfo info = contentMapGlycanSiteHigh.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(info.getSite()).append("\t");
            sb.append(info.getSeqAround()).append("\t");
            sb.append(info.getGlycoName()).append("\t");
            sb.append(df2.format(info.getGlycoFormDeltaScore())).append("\t");
            sb.append(df2.format(info.getScore())).append("\t");
            sb.append(info.getDeleRef()).append("\t");
            writer.addContent(sb + "" + countMapGlycanSiteHigh.get(key), 7, format);

            if (contentMapGlycanSiteLow.containsKey(key)) {
                sb.append(countMapGlycanSiteHigh.get(key) + countMapGlycanSiteLow.get(key)).append("\t");
                writer.addContent(sb.toString(), 6, format);
            } else {
                sb.append(countMapGlycanSiteHigh.get(key)).append("\t");
                writer.addContent(sb.toString(), 6, format);
            }
        }
        for (String key : contentMapGlycanSiteLow.keySet()) {
            if (contentMapGlycanSiteHigh.containsKey(key)) continue;
            OGlycanSiteInfo info = contentMapGlycanSiteLow.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(info.getSite()).append("\t");
            sb.append(info.getSeqAround()).append("\t");
            sb.append(info.getGlycoName()).append("\t");
            sb.append(df2.format(info.getGlycoFormDeltaScore())).append("\t");
            sb.append(df2.format(info.getScore())).append("\t");
            sb.append(info.getDeleRef()).append("\t");
            sb.append(countMapGlycanSiteLow.get(key)).append("\t");
            writer.addContent(sb.toString(), 6, format);
            writer.addContent(sb.toString(), 8, format);
        }

        this.writer.close();
    }

}
