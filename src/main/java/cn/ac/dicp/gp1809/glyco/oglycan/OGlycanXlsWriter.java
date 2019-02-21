/*
 ******************************************************************************
 * File: OGlycanXlsWriter.java * * * Created on 2013-2-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotDatParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import com.itextpdf.text.DocumentException;
import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ck
 * @version 2013-2-25, 15:41:01
 */
public class OGlycanXlsWriter
{

    private static double deltaF = 0.5;
    private HashMap<String, OGlycanSiteInfo> sitemap1;
    //	private HashMap <String, OGlycanSiteInfo> sitemap2;
    private ExcelWriter writer;
    private ExcelFormat format = ExcelFormat.normalFormat;
    private DecimalFormat df2 = DecimalFormats.DF0_2;
    private HashMap<String, int[]> spectralCountMap;
    private HashMap<String, double[]> scoreMap;

    public OGlycanXlsWriter(String out) throws IOException, RowsExceededException, WriteException
    {

        this.sitemap1 = new HashMap<String, OGlycanSiteInfo>();
//		this.sitemap2 = new HashMap <String, OGlycanSiteInfo>();
        this.spectralCountMap = new HashMap<String, int[]>();
        this.scoreMap = new HashMap<String, double[]>();
        this.writer = new ExcelWriter(out, new String[]{"Peptides", "Sites", "Unique sites"});
        this.addTitle();
    }

    private static void spcountSite(String[] in, String out, double formThres,
            double siteThres) throws IOException, JXLException
    {

        ExcelWriter writer = new ExcelWriter(out, new String[]{"Content", "Glycopeptides", "Site-specific glycans", "Glycosylation sites all",
                "Glycosylation sites high", "Glycosylation sites low", "Site-specific glycans all"
                , "Site-specific glycans high", "Site-specific glycans low"});
        ExcelFormat format = ExcelFormat.normalFormat;
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

        HashMap<String, Integer> gmap = new HashMap<String, Integer>();

        HashMap<String, Integer> countMapSiteHigh = new HashMap<String, Integer>();
        HashMap<String, Integer> countMapSiteLow = new HashMap<String, Integer>();
        HashMap<String, Integer> countMapGlycanSiteHigh = new HashMap<String, Integer>();
        HashMap<String, Integer> countMapGlycanSiteLow = new HashMap<String, Integer>();
        HashMap<String, String[]> contentMapSiteHigh = new HashMap<String, String[]>();
        HashMap<String, String[]> contentMapSiteLow = new HashMap<String, String[]>();
        HashMap<String, String[]> contentMapGlycanSiteHigh = new HashMap<String, String[]>();
        HashMap<String, String[]> contentMapGlycanSiteLow = new HashMap<String, String[]>();
        int[] count = new int[2];
        for (int i = 0; i < in.length; i++) {
            ExcelReader reader = new ExcelReader(in[i], new int[]{0, 1});
            String[] line = reader.readLine(0);
            while ((line = reader.readLine(0)) != null) {
                writer.addContent(line, 1, format);
                count[0]++;
            }

            line = reader.readLine(1);
            while ((line = reader.readLine(1)) != null) {
                writer.addContent(line, 2, format);
                count[1]++;
                double formScore = Double.parseDouble(line[4]);
                double siteScore = Double.parseDouble(line[5]);
                String key1 = line[0] + "\t" + line[7];
                String key2 = line[0] + "\t" + line[2] + "\t" + line[7];

                if (formScore >= formThres) {
                    String glycan = line[2];
                    if (gmap.containsKey(glycan)) {
                        gmap.put(glycan, gmap.get(glycan) + 1);
                    } else {
                        gmap.put(glycan, 1);
                    }
                }

                if (formScore >= formThres && siteScore >= siteThres) {
                    if (countMapSiteHigh.containsKey(key1)) {
                        countMapSiteHigh.put(key1, countMapSiteHigh.get(key1) + 1);
                        String[] content = contentMapSiteHigh.get(key1);
                        double formScore0 = Double.parseDouble(content[4]);
                        double siteScore0 = Double.parseDouble(content[5]);
                        if (siteScore > siteScore0) {
                            contentMapSiteHigh.put(key1, line);
                        } else if (siteScore == siteScore0) {
                            if (formScore > formScore0) {
                                contentMapSiteHigh.put(key1, line);
                            }
                        }
                    } else {
                        countMapSiteHigh.put(key1, 1);
                        contentMapSiteHigh.put(key1, line);
                    }

                    if (countMapGlycanSiteHigh.containsKey(key2)) {
                        countMapGlycanSiteHigh.put(key2, countMapGlycanSiteHigh.get(key2) + 1);
                        String[] content = contentMapGlycanSiteHigh.get(key2);
                        double formScore0 = Double.parseDouble(content[4]);
                        double siteScore0 = Double.parseDouble(content[5]);
                        if (siteScore > siteScore0) {
                            contentMapGlycanSiteHigh.put(key2, line);
                        } else if (siteScore == siteScore0) {
                            if (formScore > formScore0) {
                                contentMapGlycanSiteHigh.put(key2, line);
                            }
                        }
                    } else {
                        countMapGlycanSiteHigh.put(key2, 1);
                        contentMapGlycanSiteHigh.put(key2, line);
                    }

                } else {
                    if (countMapSiteLow.containsKey(key1)) {
                        countMapSiteLow.put(key1, countMapSiteLow.get(key1) + 1);
                        String[] content = contentMapSiteLow.get(key1);
                        double formScore0 = Double.parseDouble(content[4]);
                        double siteScore0 = Double.parseDouble(content[5]);
                        if (siteScore > siteScore0) {
                            contentMapSiteLow.put(key1, line);
                        } else if (siteScore == siteScore0) {
                            if (formScore > formScore0) {
                                contentMapSiteLow.put(key1, line);
                            }
                        }
                    } else {
                        countMapSiteLow.put(key1, 1);
                        contentMapSiteLow.put(key1, line);
                    }

                    if (countMapGlycanSiteLow.containsKey(key2)) {
                        countMapGlycanSiteLow.put(key2, countMapGlycanSiteLow.get(key2) + 1);
                        String[] content = contentMapGlycanSiteLow.get(key2);
                        double formScore0 = Double.parseDouble(content[4]);
                        double siteScore0 = Double.parseDouble(content[5]);
                        if (siteScore > siteScore0) {
                            contentMapGlycanSiteLow.put(key2, line);
                        } else if (siteScore == siteScore0) {
                            if (formScore > formScore0) {
                                contentMapGlycanSiteLow.put(key2, line);
                            }
                        }
                    } else {
                        countMapGlycanSiteLow.put(key2, 1);
                        contentMapGlycanSiteLow.put(key2, line);
                    }
                }
            }
            reader.close();
            System.out.println(in[i] + "\t" + count[0] + "\t" + count[1]);
        }

        for (String glycan : gmap.keySet()) {
//			System.out.println(glycan+"\t"+gmap.get(glycan));
        }


        for (String key : contentMapSiteHigh.keySet()) {
            String[] content = contentMapSiteHigh.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(content[0]).append("\t");
            sb.append(content[1]).append("\t");
            sb.append(content[4]).append("\t");
            sb.append(content[5]).append("\t");
            sb.append(content[7]).append("\t");
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
            String[] content = contentMapSiteLow.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(content[0]).append("\t");
            sb.append(content[1]).append("\t");
            sb.append(content[4]).append("\t");
            sb.append(content[5]).append("\t");
            sb.append(content[7]).append("\t");
            sb.append(countMapSiteLow.get(key)).append("\t");
            writer.addContent(sb.toString(), 3, format);
            writer.addContent(sb.toString(), 5, format);
        }
        for (String key : contentMapGlycanSiteHigh.keySet()) {
            String[] content = contentMapGlycanSiteHigh.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(content[0]).append("\t");
            sb.append(content[1]).append("\t");
            sb.append(content[2]).append("\t");
            sb.append(content[4]).append("\t");
            sb.append(content[5]).append("\t");
            sb.append(content[7]).append("\t");
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
            String[] content = contentMapGlycanSiteLow.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(content[0]).append("\t");
            sb.append(content[1]).append("\t");
            sb.append(content[2]).append("\t");
            sb.append(content[4]).append("\t");
            sb.append(content[5]).append("\t");
            sb.append(content[7]).append("\t");
            sb.append(countMapGlycanSiteLow.get(key)).append("\t");
            writer.addContent(sb.toString(), 6, format);
            writer.addContent(sb.toString(), 8, format);
        }

        writer.close();
    }

    /**
     * @param args
     * @throws FastaDataBaseException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, FastaDataBaseException
    {
        // TODO Auto-generated method stub

        String fasta = "F:\\DataBase\\ipi.HUMAN.v3.80\\Final_ipi.HUMAN.v3.80.fasta";
        String regex = "([^| ]*)";
        IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
        String pepinfo = "H:\\OGlycan_final_20131104\\serum_2D\\2D_trypsin\\" +
                "20120328_humaneserum_trypsin_HILIC_8uL-03.peps.info";
        OGlycanValidatorRank1 validator = new OGlycanValidatorRank1(fasta, regex, judger, pepinfo);

//		String t3 = "H:\\OGlycan\\20130305\\2\\2_3_F004162.dat";
//		String t4 = "H:\\OGlycan\\20130305\\2\\2_4_F004164.dat";
//		String t5 = "H:\\OGlycan\\20130305\\2\\2_5_F004163.dat";

//		validator.validate(t3);
//		validator.validate(t4);
//		validator.validate(t5);


    }

    private void addTitle() throws RowsExceededException, WriteException
    {

        StringBuilder sb1 = new StringBuilder();

        sb1.append("Scan\t");
        sb1.append("Peptide Mr\t");
        sb1.append("Ion Score\t");
        sb1.append("Expect\t");
        sb1.append("Length\t");
        sb1.append("Proteins\t");
        sb1.append("Protein\t");
        sb1.append("Modified sequence\t");
        sb1.append("Modified probability\t");
        sb1.append("Glycan\t");
        sb1.append("Other possibilities\t\t\t");
//		sb1.append("Other possibilities 2\t\t\t");
//		sb1.append("Modified sequence\t");
//		sb1.append("Modified probability\t");
//		sb1.append("Glycan\t");
//		sb1.append("Match Score\t");

        StringBuilder sb2 = new StringBuilder();

        sb2.append("Site\t");
        sb2.append("Sequence around\t");
        sb2.append("Glycan\t");
        sb2.append("Glycan mass\t");
        sb2.append("Proteins\t");
        sb2.append("Protein\t");
        sb2.append("Scan\t");
        sb2.append("Modified sequence\t");
        sb2.append("Modified probability\t");
        sb2.append("Unambiguous localization\t");

        writer.addTitle(sb1.toString(), 0, format);
        writer.addTitle(sb2.toString(), 1, format);
        writer.addTitle(sb2.toString(), 2, format);
    }

    public void write(OGlycanPepInfo pepinfo) throws RowsExceededException, WriteException
    {

        this.writer.addContent(pepinfo.toStringOutput(), 0, format);
        OGlycanSiteInfo[] siteInfos = pepinfo.getSiteInfo();
        for (int i = 0; i < siteInfos.length; i++) {
            this.writer.addContent(siteInfos[i].toStringOutput(), 1, format);
            String key = siteInfos[i].getSite() + siteInfos[i].getSeqAround() + siteInfos[i].getGlycoName();
            if (!this.sitemap1.containsKey(key)) {
                this.sitemap1.put(key, siteInfos[i]);
            } else {
                if (!this.sitemap1.get(key).isDetermined()) {
                    if (siteInfos[i].isDetermined()) {
                        this.sitemap1.put(key, siteInfos[i]);
                    }
                }
            }
        }
    }

    public void write(OGlycanPepInfo[] pepinfos) throws RowsExceededException, WriteException
    {

        StringBuilder sb = new StringBuilder();
        sb.append(pepinfos[0].toStringOutput());

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
//				sb.append(df2.format(pepinfos[i].getFormDeltaScore())).append("\t");
                break;
            }
        }

        this.writer.addContent(sb.toString(), 0, format);
        OGlycanSiteInfo[] siteInfos = pepinfos[0].getSiteInfo();
        for (int i = 0; i < siteInfos.length; i++) {
            this.writer.addContent(siteInfos[i].toStringOutput(), 1, format);
            String key = siteInfos[i].getSite() + siteInfos[i].getSeqAround() + siteInfos[i].getGlycoName();
            String key2 = siteInfos[i].getSite() + "\t" + siteInfos[i].getSeqAround();
            if (this.spectralCountMap.containsKey(key2)) {
                int[] count = this.spectralCountMap.get(key2);
                if (siteInfos[i].getGlycoName().contains("GlcNAc")) {
                    count[1]++;
                } else {
                    count[0]++;
                }
                this.spectralCountMap.put(key2, count);
                double[] score = this.scoreMap.get(key2);
                if (siteInfos[i].getGlycoName().contains("GlcNAc")) {
                    if (siteInfos[i].getScore() > score[1]) {
                        score[1] = siteInfos[i].getScore();
                        this.scoreMap.put(key2, score);
                    }
                } else {
                    if (siteInfos[i].getScore() > score[0]) {
                        score[0] = siteInfos[i].getScore();
                        this.scoreMap.put(key2, score);
                    }
                }
            } else {
                int[] count = new int[2];
                double[] score = new double[2];
                if (siteInfos[i].getGlycoName().contains("GlcNAc")) {
                    count[1]++;
                    score[1] = siteInfos[i].getScore();
                } else {
                    count[0]++;
                    score[0] = siteInfos[i].getScore();
                }
                this.spectralCountMap.put(key2, count);
                this.scoreMap.put(key2, score);
            }
            if (!this.sitemap1.containsKey(key)) {
                this.sitemap1.put(key, siteInfos[i]);
            } else {
                OGlycanSiteInfo infox = sitemap1.get(key);
                double deltaScoreX = infox.getGlycoFormDeltaScore();
                double siteScoreX = infox.getScore();
                double deltaScoreI = siteInfos[i].getGlycoFormDeltaScore();
                double siteScoreI = siteInfos[i].getScore();

                if (deltaScoreX >= deltaF) {
                    if (deltaScoreI >= deltaF && siteScoreI > siteScoreX) {
                        this.sitemap1.put(key, siteInfos[i]);
                    }
                } else {
                    if (deltaScoreI >= deltaF) {
                        this.sitemap1.put(key, siteInfos[i]);
                    } else {
                        if (siteScoreI > siteScoreX) {
                            this.sitemap1.put(key, siteInfos[i]);
                        }
                    }
                }
            }
        }
    }

    public void write(ArrayList<OGlycanPepInfo> pepinfolist) throws RowsExceededException, WriteException
    {
        for (int i = 0; i < pepinfolist.size(); i++) {
            this.write(pepinfolist.get(i));
        }
    }

    public void writeWithPdf(ArrayList<OGlycanPepInfo> pepinfolist,
            String out) throws RowsExceededException, WriteException, IOException, DocumentException
    {

        OGlycanPdfWriter writer = new OGlycanPdfWriter(out);
        for (int i = 0; i < pepinfolist.size(); i++) {
            this.write(pepinfolist.get(i));
            writer.write(pepinfolist.get(i));
        }
        writer.close();
    }

    public void close() throws WriteException, IOException
    {

        Iterator<String> it1 = this.sitemap1.keySet().iterator();
        while (it1.hasNext()) {
            String key = it1.next();
            this.writer.addContent(sitemap1.get(key).toStringOutput(), 2, format);
        }
        this.writer.close();

        Iterator<String> it2 = this.spectralCountMap.keySet().iterator();
        while (it2.hasNext()) {
            String key = it2.next();
            int[] count = this.spectralCountMap.get(key);
            double[] score = this.scoreMap.get(key);
            System.out.println(key + "\t" + df2.format(score[0]) + "\t" + df2.format(score[1]) + "\t" + count[0] + "\t" + count[1]);
        }
    }

    public void fileWrite(String in, OGlycanValidatorRank1 validator) throws MascotDatParsingException,
            ModsReadingException, InvalidEnzymeCleavageSiteException,
            ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException,
            PeptideParsingException, DtaFileParsingException, FastaDataBaseException, IOException
    {

        File[] files = (new File(in)).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("dat")) {
                validator.validate(files[i].getAbsolutePath());
            }
        }
    }

}
