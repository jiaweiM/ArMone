/*
 ******************************************************************************
 * File: OGlycanTest3.java * * * Created on 2013-7-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import jxl.JXLException;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ck
 * @version 2013-7-13, 18:54:16
 */
public class OGlycanTest3
{

    private static void oldNewCompare(String oldResult, String newResult) throws IOException, JXLException
    {

        HashMap<String, String[]> map1 = new HashMap<String, String[]>();
        ExcelReader oldreader = new ExcelReader(oldResult);
        String[] oldline = oldreader.readLine();
        while ((oldline = oldreader.readLine()) != null) {

            String scan = oldline[0];
            String sequence = oldline[4];

            String[] ss = scan.split("\\.");
            String key = ss[ss.length - 2] + "." + ss[ss.length - 1];

            map1.put(key, oldline);
        }
        oldreader.close();

        HashMap<String, String[]> map2 = new HashMap<String, String[]>();
        ExcelReader newreader = new ExcelReader(newResult);
        String[] newline = newreader.readLine();
        while ((newline = newreader.readLine()) != null) {

            String scan = newline[0];
            String sequence = newline[4];

            String[] ss = scan.split("\\.");
            String key = ss[ss.length - 2] + "." + ss[ss.length - 1];

            map2.put(key, newline);
        }
        newreader.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(map1.keySet());
        set.addAll(map2.keySet());

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (map1.containsKey(key)) {
                if (map2.containsKey(key)) {
                    String[] l1 = map1.get(key);
                    String[] l2 = map2.get(key);

                    System.out.println(key + "\t" + l1[2] + "\t" + l1[4] + "\t" + l2[2] + "\t" + l2[4]);
                } else {
//					System.out.println(key+"\t"+map1.get(key));
                }
            } else {
                if (map2.containsKey(key)) {

                }
            }
        }
    }

    private static void mgftest(String in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("PEPMASS")) {
                double mass = Double.parseDouble(line.substring(line.indexOf("=") + 1));
                System.out.println(mass);
            }
        }
        reader.close();
    }

    private static void fetuinTest()
    {

        String fetuin = "MKSFVLLFCLAQLWGCHSIPLDPVAGYKEPACDDPDTEQAALAAVDYINKHLPRGYKHTL"
                + "NQIDSVKVWPRRPTGEVYDIEIDTLETTCHVLDPTPLANCSVRQQTQHAVEGDCDIHVLK"
                + "QDGQFSVLFTKCDSSPDSAEDVRKLCPDCPLLAPLNDSRVVHAVEVALATFNAESNGSYL"
                + "QLVEISRAQFVPLPVSVSVEFAVAATDCIAKEVVDPTKCNLLAEKQYGFCKGSVIQKALG"
                + "GEDVRVTCTLFQTQPVIPQPQPDGAEAEAPSAVPDAAGPTPSAAGPPVASVVVGPSVVAV"
                + "PLPLHRAHYDLRHTFSGVASVESSSGEAFHVGKTPIVGQPSIPGGPVRLCPGRIRYFKI";

        Enzyme enzyme = Enzyme.TRYPSIN;
//		String[] peps = enzyme.cleave(fetuin, 2, 1);
        String[] peps = Enzyme.NOENZYME.cleave(fetuin, 2, 1);

        MwCalculator mwcal = new MwCalculator();
        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        mwcal.setAacids(aas);

        for (int i = 0; i < peps.length; i++) {
            double mass = mwcal.getMonoIsotopeMZ(peps[i]);
            if (mass > 400 && mass < 500)
                System.out.println(peps[i] + "\t" + mwcal.getMonoIsotopeMZ(peps[i]));
        }
    }

    private static void pplCompare(String withMod, String noMod, String pepinfo) throws FileDamageException, IOException
    {

        HashMap<String, OGlycanScanInfo2> infomap = new HashMap<String, OGlycanScanInfo2>();
        HashMap<String, HashSet<String>> scanTypeMap = new HashMap<String, HashSet<String>>();
        BufferedReader reader = new BufferedReader(new FileReader(pepinfo));
        String line = null;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            infomap.put(scanname, info);
            if (scanTypeMap.containsKey(oriScanname)) {
                scanTypeMap.get(oriScanname).add(scanname);
            } else {
                HashSet<String> set = new HashSet<String>();
                set.add(scanname);
                scanTypeMap.put(oriScanname, set);
            }
        }
        reader.close();

        HashMap<String, IPeptide> pepmap1 = new HashMap<String, IPeptide>();
        HashMap<String, IPeptide> pepmap3 = new HashMap<String, IPeptide>();
        PeptideListReader withModReader = new PeptideListReader(withMod);
        AminoacidModification aam = withModReader.getSearchParameter().getVariableInfo();
        IPeptide peptide1 = null;
        while ((peptide1 = withModReader.getPeptide()) != null) {
            if (!peptide1.isTP()) continue;
            if (peptide1.getPrimaryScore() < 20) continue;
            String scanname = peptide1.getBaseName();

            if (peptide1.getRank() > 1) {
                pepmap3.put(scanname, peptide1);
                continue;
            }
            double mh = peptide1.getMH();
            String sequence = peptide1.getSequence();
            StringBuilder sb = new StringBuilder();
            boolean gp = false;
            for (int i = 0; i < sequence.length(); i++) {
                char ci = sequence.charAt(i);
                if (ci >= 'A' && ci <= 'Z') {
                    sb.append(ci);
                } else if (ci == '-') {
                    sb.append(ci);
                } else if (ci == '.') {
                    sb.append(ci);
                } else if (ci == '*') {
                    sb.append(ci);
                } else if (ci == '#') {
                    sb.append(ci);
                } else {
                    gp = true;
                    mh -= aam.getAddedMassForModif(ci);
                }
            }

            peptide1.setSequence(sb.toString());
            if (gp) {
                peptide1.setMH(mh);
                pepmap1.put(scanname, peptide1);
            }
        }
        withModReader.close();
        System.out.println(pepmap1.size() + "\t" + pepmap3.size());

        PeptideListReader noModReader = new PeptideListReader(noMod);
        HashMap<String, IPeptide> pepmap2 = new HashMap<String, IPeptide>();
        IPeptide peptide2 = null;
        while ((peptide2 = noModReader.getPeptide()) != null) {
            if (!peptide2.isTP()) continue;
            if (peptide2.getPrimaryScore() < 20) continue;
            String scanname = peptide2.getBaseName();
            scanname = scanname.substring(0, scanname.lastIndexOf("."));
            if (pepmap2.containsKey(scanname)) {
                if (peptide2.getPrimaryScore() > pepmap2.get(scanname).getPrimaryScore()) {
                    pepmap2.put(scanname, peptide2);
                }
            } else {
                pepmap2.put(scanname, peptide2);
            }
//			System.out.println(scanname);
        }
        System.out.println(pepmap2.size());
        System.out.println("scanname\tscore with mod\tscore no mod\tscore difference\tsequence");
        HashSet<String> set = new HashSet<String>();
        set.addAll(pepmap1.keySet());
        set.addAll(pepmap2.keySet());

        ArrayList<String> list = new ArrayList<String>();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (pepmap1.containsKey(key)) {
                IPeptide p1 = pepmap1.get(key);
                String seq1 = p1.getSequence();
                double score1 = p1.getPrimaryScore();

                if (pepmap2.containsKey(key)) {

                    IPeptide p2 = pepmap2.get(key);
                    String seq2 = p2.getSequence();
                    double score2 = p2.getPrimaryScore();
                    if (seq1.equals(seq2)) {
                        System.out.println(key + "\t" + score1 + "\t" + score2 + "\t" + (score2 - score1) + "\t" + seq1);
                    } else {
                        System.out.println(key + "\t" + score1 + "\t" + score2 + "\t" + (score2 - score1) + "\t" + seq1 + "\t" + seq2);
                    }

                } else {
                    double mh = p1.getMH();
                    double mh2 = (mh + AminoAcidProperty.PROTON_W) / 2.0;
                    double mh3 = (mh + AminoAcidProperty.PROTON_W * 2.0) / 3.0;
                    double mh4 = (mh + AminoAcidProperty.PROTON_W * 3.0) / 4.0;
                    list.add(key + "\t" + score1 + "\t" + seq1 + "\t" + mh + "\t" + mh2 + "\t" + mh3 + "\t" + mh4);
                }
            } else {
                if (pepmap3.containsKey(key)) {

                    IPeptide p2 = pepmap2.get(key);
                    String seq2 = p2.getSequence();
                    double score2 = p2.getPrimaryScore();

                    IPeptide p3 = pepmap3.get(key);
                    String seq3 = p3.getSequence();
                    double score3 = p3.getPrimaryScore();
                    if (seq2.equals(seq3)) {
                        System.out.println("~~~~~~~~~~\t" + key + "\t" + p3.getRank() + "\t" + score3 + "\t" + score2 + "\t" + (score2 - score3) + "\t" + seq3);
                    } else {
                        System.out.println("~~~~~~~~~~\t" + key + "\t" + p3.getRank() + "\t" + score3 + "\t" + score2 + "\t" + (score2 - score3) + "\t" + seq3 + "\t" + seq2);
                    }
                }
            }
        }

        int found = 0;
        int nf = 0;
        System.out.println(pepmap1.size() + "\t" + pepmap2.size() + "\t" + (pepmap1.size() + pepmap2.size() - set.size()));
        System.out.println();
        for (int i = 0; i < list.size(); i++) {
            String notfound = list.get(i);
            String key = notfound.split("\t")[0];
            if (scanTypeMap.containsKey(key)) {
                found++;
            } else {
                nf++;
            }
            System.out.println(notfound);
        }
        System.out.println(found + "\t" + nf);
    }

    private static void pplCompare2(String withMod, String noMod) throws FileDamageException, IOException
    {

        HashMap<String, IPeptide> pepmap1 = new HashMap<String, IPeptide>();
        PeptideListReader withModReader = new PeptideListReader(withMod);
        IPeptide peptide1 = null;
        while ((peptide1 = withModReader.getPeptide()) != null) {
            if (!peptide1.isTP()) continue;
            if (peptide1.getPrimaryScore() < 15) continue;
            String scanname = peptide1.getBaseName();
            String sequence = peptide1.getSequence();
            StringBuilder sb = new StringBuilder();
            boolean gp = false;
            for (int i = 0; i < sequence.length(); i++) {
                char ci = sequence.charAt(i);
                if (ci >= 'A' && ci <= 'Z') {
                    sb.append(ci);
                } else if (ci == '-') {
                    sb.append(ci);
                } else if (ci == '.') {
                    sb.append(ci);
                } else if (ci == '*') {
                    sb.append(ci);
                } else {
                    gp = true;
                }
            }

            String[] ss = scanname.split("\\.");
            int fileid = Integer.parseInt(ss[2]);

            String s1 = "";
            String s2 = "";
            for (int i = 0; i < ss.length; i++) {
                if (i == 2) {
                    s1 += fileid;
                    s1 += ".";
                    s2 += (fileid + 1);
                    s2 += ".";
                } else {
                    s1 += ss[i];
                    s1 += ".";
                    s2 += ss[i];
                    s2 += ".";
                }
            }
            s1 = s1.substring(0, s1.length() - 1);
            s2 = s2.substring(0, s2.length() - 1);

            peptide1.setSequence(sb.toString());
            if (gp) {
                pepmap1.put(s1, peptide1);
                pepmap1.put(s2, peptide1);
            }
        }
        withModReader.close();
        System.out.println(pepmap1.size() / 2);
        PeptideListReader noModReader = new PeptideListReader(noMod);
        HashMap<String, IPeptide> pepmap2 = new HashMap<String, IPeptide>();
        System.out.println("scanname\tscore with mod\tscore no mod\tscore difference\tsequence");
        IPeptide peptide2 = null;
        while ((peptide2 = noModReader.getPeptide()) != null) {
            if (!peptide2.isTP()) continue;
            if (peptide2.getPrimaryScore() < 15) continue;
            String scanname = peptide2.getBaseName();
            String[] ss = scanname.split("\\.");
            int fileid = Integer.parseInt(ss[2]);
            String s = "";
            if (fileid % 2 == 0) {
                for (int i = 0; i < ss.length; i++) {
                    if (i == 2) {
                        s += (fileid - 1);
                        s += ".";
                    } else {
                        s += ss[i];
                        s += ".";
                    }
                }
            } else {
                for (int i = 0; i < ss.length; i++) {
                    if (i == 2) {
                        s += (fileid + 1);
                        s += ".";
                    } else {
                        s += ss[i];
                        s += ".";
                    }
                }
            }

            if (pepmap2.containsKey(scanname)) {
                if (peptide2.getPrimaryScore() > pepmap2.get(scanname).getPrimaryScore()) {
                    pepmap2.put(scanname, peptide2);
                }
            } else {
                pepmap2.put(scanname, peptide2);
            }
            if (pepmap2.containsKey(s)) {
                if (peptide2.getPrimaryScore() > pepmap2.get(s).getPrimaryScore()) {
                    pepmap2.put(scanname, peptide2);
                    pepmap2.remove(s);
                }
            } else {
                pepmap2.put(scanname, peptide2);
            }
        }

        HashSet<String> set = new HashSet<String>();
        set.addAll(pepmap1.keySet());
        set.addAll(pepmap2.keySet());

        ArrayList<String> list = new ArrayList<String>();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (pepmap1.containsKey(key)) {
                IPeptide p1 = pepmap1.get(key);
                String seq1 = p1.getSequence();
                double score1 = p1.getPrimaryScore();

                if (pepmap2.containsKey(key)) {

                    IPeptide p2 = pepmap2.get(key);
                    String seq2 = p2.getSequence();
                    double score2 = p2.getPrimaryScore();
                    if (seq1.equals(seq2)) {
                        System.out.println(key + "\t" + score1 + "\t" + score2 + "\t" + (score2 - score1) + "\t" + seq1);
                    } else {
                        System.out.println(key + "\t" + score1 + "\t" + score2 + "\t" + (score2 - score1) + "\t" + seq1 + "\t" + seq2);
                    }

                } else {
                    list.add(key + "\t" + score1 + "\t" + seq1 + "\t" + p1.getMr());
                }
            }
        }

        System.out.println(pepmap1.size() + "\t" + pepmap2.size() + "\t" + (pepmap1.size() + pepmap2.size() - set.size()));
        System.out.println();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    private static void pplCompare2(String withMod, String noMod,
            String pepinfo) throws FileDamageException, IOException
    {

        HashMap<String, OGlycanScanInfo> infomap = new HashMap<String, OGlycanScanInfo>();
        BufferedReader reader = new BufferedReader(new FileReader(pepinfo));
        String line = null;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo info = new OGlycanScanInfo(line);
            infomap.put(info.getScanname(), info);
        }
        reader.close();

        HashMap<String, IPeptide> pepmap1 = new HashMap<String, IPeptide>();
        PeptideListReader withModReader = new PeptideListReader(withMod);
        IPeptide peptide1 = null;
        while ((peptide1 = withModReader.getPeptide()) != null) {
            if (!peptide1.isTP()) continue;
            if (peptide1.getPrimaryScore() < 20) continue;
            String scanname = peptide1.getBaseName();
            String sequence = peptide1.getSequence();
            StringBuilder sb = new StringBuilder();
            boolean gp = false;
            for (int i = 0; i < sequence.length(); i++) {
                char ci = sequence.charAt(i);
                if (ci >= 'A' && ci <= 'Z') {
                    sb.append(ci);
                } else if (ci == '-') {
                    sb.append(ci);
                } else if (ci == '.') {
                    sb.append(ci);
                } else if (ci == '*') {
                    sb.append(ci);
                } else {
                    gp = true;
                }
            }

            String[] ss = scanname.split("\\.");
            int fileid = Integer.parseInt(ss[2]);

            String s1 = "";
            String s2 = "";
            for (int i = 0; i < ss.length; i++) {
                if (i == 2) {
                    s1 += fileid;
                    s1 += ".";
                    s2 += (fileid + 1);
                    s2 += ".";
                } else {
                    s1 += ss[i];
                    s1 += ".";
                    s2 += ss[i];
                    s2 += ".";
                }
            }
            s1 = s1.substring(0, s1.length() - 1);
            s2 = s2.substring(0, s2.length() - 1);

            peptide1.setSequence(sb.toString());
            if (gp) {
                pepmap1.put(s1, peptide1);
                pepmap1.put(s2, peptide1);
            }
        }
        withModReader.close();

        PeptideListReader noModReader = new PeptideListReader(noMod);
        HashMap<String, IPeptide> pepmap2 = new HashMap<String, IPeptide>();
        System.out.println("scanname\tscore with mod\tscore no mod\tscore difference\tsequence");
        IPeptide peptide2 = null;
        while ((peptide2 = noModReader.getPeptide()) != null) {
            if (!peptide2.isTP()) continue;
            if (peptide2.getPrimaryScore() < 20) continue;
            String scanname = peptide2.getBaseName();
            String[] ss = scanname.split("\\.");
            int fileid = Integer.parseInt(ss[2]);
            String s = "";
            if (fileid % 2 == 0) {
                for (int i = 0; i < ss.length; i++) {
                    if (i == 2) {
                        s += (fileid - 1);
                        s += ".";
                    } else {
                        s += ss[i];
                        s += ".";
                    }
                }
            } else {
                for (int i = 0; i < ss.length; i++) {
                    if (i == 2) {
                        s += (fileid + 1);
                        s += ".";
                    } else {
                        s += ss[i];
                        s += ".";
                    }
                }
            }

            if (pepmap2.containsKey(scanname)) {
                if (peptide2.getPrimaryScore() > pepmap2.get(scanname).getPrimaryScore()) {
                    pepmap2.put(scanname, peptide2);
                }
            } else {
                pepmap2.put(scanname, peptide2);
            }
            if (pepmap2.containsKey(s)) {
                if (peptide2.getPrimaryScore() > pepmap2.get(s).getPrimaryScore()) {
                    pepmap2.put(scanname, peptide2);
                    pepmap2.remove(s);
                }
            } else {
                pepmap2.put(scanname, peptide2);
            }
        }

        HashSet<String> set = new HashSet<String>();
        set.addAll(pepmap1.keySet());
        set.addAll(pepmap2.keySet());

        int all = 0;
        int type1count = 0;

        ArrayList<String> list = new ArrayList<String>();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (pepmap1.containsKey(key)) {
                IPeptide p1 = pepmap1.get(key);
                String seq1 = p1.getSequence();
                double score1 = p1.getPrimaryScore();

                if (pepmap2.containsKey(key)) {

                    IPeptide p2 = pepmap2.get(key);
                    String seq2 = p2.getSequence();
                    double score2 = p2.getPrimaryScore();
                    if (seq1.equals(seq2)) {
//						System.out.println(key+"\t"+score1+"\t"+score2+"\t"+(score2-score1)+"\t"+seq1);
                    } else {
//						System.out.println(key+"\t"+score1+"\t"+score2+"\t"+(score2-score1)+"\t"+seq1+"\t"+seq2);
                    }

                } else {
                    all++;
                    list.add(key + "\t" + score1 + "\t" + seq1 + "\t" + p1.getMr());
                    if (infomap.containsKey(key)) {
                        OGlycanScanInfo info = infomap.get(key);
                        if (info.getType() == 1) {
                            type1count++;
                        }
                    }
                }
            }
        }
        System.out.println(all + "\t" + type1count);
        System.out.println(pepmap1.size() + "\t" + pepmap2.size() + "\t" + (pepmap1.size() + pepmap2.size() - set.size()));
        System.out.println();
        for (int i = 0; i < list.size(); i++) {
//			System.out.println(list.get(i));
        }
    }

    private static void xlsCompare(String type1, String all) throws IOException, JXLException
    {
        HashMap<String, String[]> map1 = new HashMap<String, String[]>();
        ExcelReader reader1 = new ExcelReader(type1);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            String scanname = line1[0];
            map1.put(scanname, line1);
        }
        reader1.close();

        HashMap<String, String[]> map2 = new HashMap<String, String[]>();
        ExcelReader reader2 = new ExcelReader(all);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            String scanname = line2[0];
            String[] ss = scanname.split("\\.");
            int fileid = Integer.parseInt(ss[2]);
            String s = "";
            if (fileid % 2 == 0) {
                for (int i = 0; i < ss.length; i++) {
                    if (i == 2) {
                        s += (fileid - 1);
                        s += ".";
                    } else {
                        s += ss[i];
                        s += ".";
                    }
                }
                s = s.substring(0, s.length() - 2);
                map2.put(s, line2);

            } else {
                map2.put(scanname, line2);
            }
        }

        HashSet<String> set = new HashSet<String>();
        set.addAll(map1.keySet());
        set.addAll(map2.keySet());

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (map1.containsKey(key)) {
                String[] s1 = map1.get(key);
                if (map2.containsKey(key)) {
                    String[] s2 = map2.get(key);
                    if (s1[4].equals(s2[4])) {
                        double score1 = Double.parseDouble(s1[2]);
                        double score2 = Double.parseDouble(s2[2]);
                        System.out.println("1\t" + key + "\t" + s1[4] + "\t" + score1 + "\t" + score2 + "\t" + (score2 - score1));
                    } else {
                        double score1 = Double.parseDouble(s1[2]);
                        double score2 = Double.parseDouble(s2[2]);
                        System.out.println("2\t" + key + "\t" + s1[4] + "\t" + s2[4] + "\t" + score1 + "\t" + score2 + "\t" + (score2 - score1));
                    }
                } else {
                    double score1 = Double.parseDouble(s1[2]);
                    System.out.println("0\t" + key + "\t" + s1[1] + "\t" + s1[4] + "\t" + score1);
                }
            }
        }
    }

    private static void scoreAnalysis(String in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        double[][] dd = new double[2][40];

        while ((line = reader.readLine()) != null) {
            String[] cs = line.split("\t");
            double score = Double.parseDouble(cs[1]);
            double diff = Double.parseDouble(cs[2]);
            int id = (int) (score - 20.0);
            if (id < 0) id = 0;
            if (id > 39) id = 39;

            for (int i = 0; i <= id; i++) {
                if (diff > 0) {
                    dd[0][i]++;
                } else {
                    dd[1][i]++;
                }
            }
//			System.out.println(score+"\t"+diff+"\t"+Arrays.toString(dd[0])+"\t"+Arrays.toString(dd[1]));
        }
        reader.close();

        for (int i = 0; i < dd[0].length; i++) {
            System.out.println((i + 20) + "\t" + dd[0][i] + "\t" + dd[1][i] + "\t" + (dd[1][i] / dd[0][i]));
        }
    }

    private static void typeAnalysis(String in) throws IOException, JXLException
    {

        int total = 0;
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] cs = line[8].split(";");
            int[] count = new int[3];
            for (int i = 0; i < cs.length; i++) {
                String key = cs[i].substring(0, cs[i].indexOf("@"));
                if (map.containsKey(key)) {
                    map.put(key, map.get(key) + 1);
                } else {
                    map.put(key, 1);
                }

                if (key.equals("Gal-GalNAc")) {
                    count[0] = 1;
                } else if (key.equals("NeuAc-Gal-GalNAc")) {
                    count[1] = 1;
                } else if (key.equals("NeuAc-Gal-(NeuAc-)GalNAc")) {
                    count[2] = 1;
                }
                if (count[0] + count[1] + count[2] == 1) {
                    total++;
                }
            }
        }
        reader.close();

        System.out.println(total);
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            System.out.println(key + "\t" + map.get(key));
        }
    }

    private static void siteCompare(String s1, String s2) throws IOException, JXLException
    {

        HashSet<String> set1 = new HashSet<String>();
        ExcelReader reader1 = new ExcelReader(s1, 2);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            String key = line1[0] + line1[1] + line1[2];
            set1.add(key);
        }
        reader1.close();

        HashSet<String> set2 = new HashSet<String>();
        ExcelReader reader2 = new ExcelReader(s2, 2);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            String key = line2[0] + line2[1] + line2[2];
            set2.add(key);
        }

        HashSet<String> set = new HashSet<String>();
        set.addAll(set1);
        set.addAll(set2);

        int common = 0;
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (set1.contains(key) && set2.contains(key)) {
                common++;
            }
        }
        System.out.println(set1.size() + "\t" + set2.size() + "\t" + common);
    }

    private static void siteCompare(String s1, String s2, String s3) throws IOException, JXLException
    {

        HashSet<String> set1 = new HashSet<String>();
        ExcelReader reader1 = new ExcelReader(s1, 2);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            String key = line1[0] + line1[1] + line1[2];
            set1.add(key);
        }
        reader1.close();

        HashSet<String> set2 = new HashSet<String>();
        ExcelReader reader2 = new ExcelReader(s2, 2);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            String key = line2[0] + line2[1] + line2[2];
            set2.add(key);
        }
        reader2.close();

        HashSet<String> set3 = new HashSet<String>();
        ExcelReader reader3 = new ExcelReader(s3, 2);
        String[] line3 = reader3.readLine();
        while ((line3 = reader3.readLine()) != null) {
            String key = line3[0] + line3[1] + line3[2];
            set3.add(key);
        }
        reader3.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(set1);
        set.addAll(set2);
        set.addAll(set3);

        int common = 0;
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (set1.contains(key) && set2.contains(key) && set3.contains(key)) {
                common++;
            }
        }
        System.out.println(set1.size() + "\t" + set2.size() + "\t" + set3.size() + "\t" + common);
    }

    private static void peptideCompare(String in1, String in2) throws FileDamageException, IOException
    {

        PeptideListReader r1 = new PeptideListReader(in1);
        PeptideListReader r2 = new PeptideListReader(in2);
        HashMap<String, IPeptide> m1 = new HashMap<String, IPeptide>();
        HashMap<String, IPeptide> m2 = new HashMap<String, IPeptide>();
        HashMap<String, IPeak[]> pm1 = new HashMap<String, IPeak[]>();
        HashMap<String, IPeak[]> pm2 = new HashMap<String, IPeak[]>();
        IPeptide p1 = null;
        while ((p1 = r1.getPeptide()) != null) {
            if (p1.getPrimaryScore() < 15) continue;
            m1.put(p1.getBaseName(), p1);
            pm1.put(p1.getBaseName(), r1.getPeakLists()[0].getPeakArray());
        }
        r1.close();

        IPeptide p2 = null;
        while ((p2 = r2.getPeptide()) != null) {
            if (p2.getPrimaryScore() < 15) continue;
            m2.put(p2.getBaseName(), p2);
            pm2.put(p2.getBaseName(), r2.getPeakLists()[0].getPeakArray());
        }
        r2.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (m1.containsKey(key) && m2.containsKey(key)) {
                IPeptide pep1 = m1.get(key);
                IPeptide pep2 = m2.get(key);
                IPeak[] ps1 = pm1.get(key);
                IPeak[] ps2 = pm2.get(key);
                if (pep1.getSequence().equals(pep2.getSequence())) {
                    if (!pep1.isTP())
                        System.out.println(key + "\t" + pep1.getSequence() + "\t" + pep1.getPrimaryScore() + "\t"
                                + pep2.getPrimaryScore() + "\t" + (pep2.getPrimaryScore() - pep1.getPrimaryScore())
                                + "\t" + ps1.length + "\t" + ps2.length);
                }
            }
        }
    }

    private static void sptest(String in) throws IOException
    {
        int count = 0;
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] ss = line.split("\t");
            String[] sss = ss[ss.length - 2].split("\\.");
            int fileid = Integer.parseInt(sss[2]);
            if (fileid % 2 == 1) {
                count++;
            }
        }
        reader.close();
        System.out.println(count);
    }

    private static void typeCount(String in) throws IOException, JXLException
    {

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            for (int i = 8; i < line.length; i++) {
                String type = line[i].substring(0, line[i].indexOf("@"));
                if (map.containsKey(type)) {
                    map.put(type, map.get(type) + 1);
                } else {
                    map.put(type, 1);
                }
            }
        }

        for (String type : map.keySet()) {
            System.out.println(map.size() + "\t" + type + "\t" + map.get(type));
        }
    }

    private static void siteTypeCount(String in) throws IOException, JXLException
    {

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        ExcelReader reader = new ExcelReader(in, 2);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
//			for(int i=8;i<line.length;i++){
            String type = line[2];
            if (map.containsKey(type)) {
                map.put(type, map.get(type) + 1);
            } else {
                map.put(type, 1);
            }
//			}
        }

        for (String type : map.keySet()) {
            System.out.println(type + "\t" + map.get(type));
        }

    }

    private static void intensityTest(String ppl, String mgf,
            double glycomass) throws FileDamageException, IOException, DtaFileParsingException
    {

        HashMap<String, IPeptide> pepmap = new HashMap<String, IPeptide>();
        PeptideListReader reader = new PeptideListReader(ppl);
        IPeptide peptide = null;
        while ((peptide = reader.getPeptide()) != null) {

            if (peptide.getPrimaryScore() < 15) continue;

            if (peptide.getSequence().contains("#")) {
                pepmap.put(peptide.getBaseName(), peptide);
            }
        }
        System.out.println("size\t" + pepmap.size());
        int number = 0;
        int[] count = new int[7];
        MgfReader mgfreader = new MgfReader(mgf);
        MS2Scan scan = null;
        while ((scan = mgfreader.getNextMS2Scan()) != null) {
            String scanname = scan.getScanName().getBaseName();

            if (pepmap.containsKey(scanname)) {
                number++;
                IPeptide pep = pepmap.get(scanname);
                double mh = pep.getMH();
                int glycanCount = pep.getSequence().length() - pep.getSequence().replaceAll("#", "").length();
                double pepmh = mh - glycanCount * glycomass;
                IPeak[] peaks = scan.getPeakList().getPeaksSortByIntensity();
                boolean find = false;

                for (int i = 0; i < peaks.length; i++) {
                    double mz = peaks[i].getMz();
                    double mz2 = mz * 2 - AminoAcidProperty.PROTON_W;
                    if (Math.abs(pepmh - mz) < 0.1 || Math.abs(pepmh - mz2) < 0.1) {
//						System.out.println(scanname+"\t"+pep.getSequence()+"\t"+mh+"\t"+pep.getCharge());
                        find = true;
                        if (i < 5) {
                            count[i]++;
                        } else {
                            count[5]++;
                        }
                        break;
                    }
                }

                if (!find) {
                    count[6]++;
                    System.out.println(scanname + "\t" + pep.getSequence() + "\t" + mh + "\t" + pep.getCharge());
                }
            }
        }

        System.out.println(pepmap.size() + "\t" + number);
        for (int i = 0; i < count.length; i++) {
            System.out.println(i + "\t" + count[i]);
        }
    }

    private static void combine(String s1, String s2, String s3, String out) throws IOException, JXLException
    {

        HashSet<String> pepset = new HashSet<String>();
        ExcelWriter writer = new ExcelWriter(out);
        ExcelFormat format = ExcelFormat.normalFormat;
        StringBuilder title1 = new StringBuilder();
        title1.append("Site\t");
        title1.append("Sequence around\t");
        title1.append("Glycan\t");
        title1.append("Proteins\t");
        title1.append("Protein\t");
        title1.append("Trypsin\t");
        title1.append("Trypsin+GluC\t");
        title1.append("Elastase\t");
        writer.addTitle(title1.toString(), 0, format);

        StringBuilder title2 = new StringBuilder();
        title2.append("Site\t");
        title2.append("Sequence around\t");
        title2.append("Protein\t");
        title2.append("GalNAc\t");
        title2.append("Gal-GalNAc\t");
        title2.append("NeuAc-GalNAc\t");
        title2.append("NeuAc-Gal-GalNAc\t");
        title2.append("NeuAc-Gal-(NeuAc-)GalNAc\t");
        title2.append("Gal-(GlcNAc-)GalNAc\t");
        title2.append("NeuAc-Gal-(GlcNAc-)GalNAc\t");
        title2.append("Gal-(Gal-GlcNAc-)GalNAc\t");
        title2.append("NeuAc-Gal-(Gal-GlcNAc-)GalNAc\t");
        title2.append("NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc\t");
        writer.addTitle(title2.toString(), 1, format);

        HashMap<String, int[]> map = new HashMap<String, int[]>();
        HashMap<String, String[]> m1 = new HashMap<String, String[]>();
        HashMap<String, Boolean> mb1 = new HashMap<String, Boolean>();
        ExcelReader r1 = new ExcelReader(s1, 2);
        String[] l1 = r1.readLine();
        while ((l1 = r1.readLine()) != null) {
            String key = l1[0] + l1[1] + l1[2];
            m1.put(key, l1);
            mb1.put(key, l1[9].equals("true"));
            pepset.add(l1[7]);
            String key2 = l1[0] + "\t" + l1[1] + "\t" + l1[5];
            int type = judgeType(l1[2]);
            if (map.containsKey(key2)) {
                int[] types = map.get(key2);
                types[type] = 1;
                map.put(key2, types);
            } else {
                int[] types = new int[10];
                types[type] = 1;
                map.put(key2, types);
            }
        }
        r1.close();

        HashMap<String, String[]> m2 = new HashMap<String, String[]>();
        HashMap<String, Boolean> mb2 = new HashMap<String, Boolean>();
        ExcelReader r2 = new ExcelReader(s2, 2);
        String[] l2 = r2.readLine();
        while ((l2 = r2.readLine()) != null) {
            String key = l2[0] + l2[1] + l2[2];
            m2.put(key, l2);
            mb2.put(key, l2[9].equals("true"));
            pepset.add(l2[7]);
            String key2 = l2[0] + "\t" + l2[1] + "\t" + l2[5];
            int type = judgeType(l2[2]);
            if (map.containsKey(key2)) {
                int[] types = map.get(key2);
                types[type] = 1;
                map.put(key2, types);
            } else {
                int[] types = new int[10];
                types[type] = 1;
                map.put(key2, types);
            }
        }
        r2.close();

        HashMap<String, String[]> m3 = new HashMap<String, String[]>();
        HashMap<String, Boolean> mb3 = new HashMap<String, Boolean>();
        ExcelReader r3 = new ExcelReader(s3, 2);
        String[] l3 = r3.readLine();
        while ((l3 = r3.readLine()) != null) {
            String key = l3[0] + l3[1] + l3[2];
            m3.put(key, l3);
            mb3.put(key, l3[9].equals("true"));
            pepset.add(l3[7]);
            String key2 = l3[0] + "\t" + l3[1] + "\t" + l3[5];
            int type = judgeType(l3[2]);
            if (map.containsKey(key2)) {
                int[] types = map.get(key2);
                types[type] = 1;
                map.put(key2, types);
            } else {
                int[] types = new int[10];
                types[type] = 1;
                map.put(key2, types);
            }
        }
        r3.close();

        System.out.println("pepset\t" + pepset.size());
        HashSet<String> set = new HashSet<String>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        set.addAll(m3.keySet());

        int count = 0;
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            String[] line = null;
            if (m1.containsKey(key)) {
                line = m1.get(key);
                sb2.append("+\t");
            } else {
                sb2.append("\t");
            }
            if (m2.containsKey(key)) {
                line = m2.get(key);
                sb2.append("+\t");
            } else {
                sb2.append("\t");
            }
            if (m3.containsKey(key)) {
                line = m3.get(key);
                sb2.append("+\t");
            } else {
                sb2.append("\t");
            }
            sb.append(line[0]).append("\t");
            sb.append(line[1]).append("\t");
            sb.append(line[2]).append("\t");
            sb.append(line[4]).append("\t");
            sb.append(line[5]).append("\t");
            sb.append(sb2);
            writer.addContent(sb.toString(), 0, format);

            boolean b = false;
            if (mb1.containsKey(key) && mb1.get(key)) {
                b = true;
            }
            if (mb2.containsKey(key) && mb2.get(key)) {
                b = true;
            }
            if (mb3.containsKey(key) && mb3.get(key)) {
                b = true;
            }
            if (b) {
                count++;
            }
        }
        System.out.println("Count\t" + count);

        Iterator<String> it2 = map.keySet().iterator();
        while (it2.hasNext()) {
            String key = it2.next();
            StringBuilder sb = new StringBuilder();
            sb.append(key).append("\t");
            int[] types = map.get(key);
            for (int i = 0; i < types.length; i++) {
                if (types[i] == 1) {
                    sb.append("+\t");
                } else {
                    sb.append("\t");
                }
            }
            writer.addContent(sb.toString(), 1, format);
        }

        writer.close();
    }

    private static int judgeType(String glycan)
    {
        int type = -1;
        if (glycan.equals("GalNAc")) {
            type = 0;
        } else if (glycan.equals("Gal-GalNAc")) {
            type = 1;
        } else if (glycan.equals("NeuAc-GalNAc")) {
            type = 2;
        } else if (glycan.equals("NeuAc-Gal-GalNAc")) {
            type = 3;
        } else if (glycan.equals("NeuAc-Gal-(NeuAc-)GalNAc")) {
            type = 4;
        } else if (glycan.equals("Gal-(GlcNAc-)GalNAc")) {
            type = 5;
        } else if (glycan.equals("NeuAc-Gal-(GlcNAc-)GalNAc")) {
            type = 6;
        } else if (glycan.equals("Gal-(Gal-GlcNAc-)GalNAc")) {
            type = 7;
        } else if (glycan.equals("NeuAc-Gal-(Gal-GlcNAc-)GalNAc")) {
            type = 8;
        } else if (glycan.equals("NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc")) {
            type = 9;
        }
        return type;
    }

    private static void resume(String pepinfo, String in, String out) throws IOException
    {

        HashMap<String, OGlycanScanInfo> infomap = new HashMap<String, OGlycanScanInfo>();
        BufferedReader reader = new BufferedReader(new FileReader(pepinfo));
        String line = null;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo info = new OGlycanScanInfo(line);
            infomap.put(info.getScanname(), info);
        }
        reader.close();

        PrintWriter pw = new PrintWriter(out);
        BufferedReader inreader = new BufferedReader(new FileReader(in));
        String inline = null;
        int charge = 0;
        double mass = 0;
        while ((inline = inreader.readLine()) != null) {
            if (inline.startsWith("PEPMASS")) {

            } else if (inline.startsWith("BEGIN")) {
                charge = 0;
                mass = 0;
                pw.write(inline + "\n");
            } else if (inline.startsWith("CHARGE")) {
                charge = Integer.parseInt(inline.substring(inline.indexOf("=") + 1, inline.length() - 1));
            } else if (inline.startsWith("TITLE")) {
                String scan = inline.substring(inline.indexOf("=") + 1);
                if (infomap.containsKey(scan)) {
                    OGlycanScanInfo oinfo = infomap.get(scan);
                    double gmass = oinfo.getPepMw();
                    OGlycanUnit[] units = oinfo.getUnits();
                    for (int i = 0; i < units.length; i++) {
                        gmass += units[i].getMass();
                    }
                    mass = gmass / (double) charge + AminoAcidProperty.PROTON_W;
                } else {
                    System.out.println("mipa");
                }
                pw.write("PEPMASS=" + mass + "\n");
                pw.write("CHARGE=" + charge + "+\n");
                pw.write(inline + "\n");
            } else {
                pw.write(inline + "\n");
            }
        }
        inreader.close();
        pw.close();
    }

    private static void siteFormCount(String in) throws IOException, JXLException
    {
        int[] count = new int[11];
        ExcelReader reader = new ExcelReader(in, 1);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            int c = 0;
            for (int i = 3; i < line.length; i++) {
                if (line[i].equals("+")) {
                    c++;
                }
            }
            count[c - 1]++;
        }
        reader.close();
        for (int i = 0; i < count.length; i++) {
            System.out.println((i + 1) + "\t" + count[i]);
        }
    }

    private static void cao(String in) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (line[4].startsWith("IPI:IPI00021842.1")) {
                StringBuilder sb1 = new StringBuilder();
                boolean b = true;
                for (int i = 0; i < line[6].length(); i++) {
                    if (line[6].charAt(i) == '[') {
                        b = false;
                    } else if (line[6].charAt(i) == ']') {
                        b = true;
                    } else {
                        if (b) sb1.append(line[6].charAt(i));
                    }
                }
                String seq = sb1.toString();//System.out.println(seq);
                if (seq.equals("AATVGSLAGQPLQER")) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < line.length; j++) {
                        sb.append(line[j]).append("\t");
                    }
                    System.out.println(sb);
                }
            }
        }
        reader.close();
    }

    private static void cao2(String in) throws IOException, JXLException
    {

        ExcelReader reader = new ExcelReader(in, 2);
        String[] line = reader.readLine();
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        while ((line = reader.readLine()) != null) {
            if (line[9].equals("false")) {
                c1++;
                if (line[8].charAt(6) == 'S' || line[8].charAt(8) == 'S' || line[8].charAt(6) == 'T' || line[8].charAt(8) == 'T') {
                    c2++;
                }
                if (line[8].charAt(5) == 'S' || line[8].charAt(9) == 'S' || line[8].charAt(5) == 'T' || line[8].charAt(9) == 'T' ||
                        line[8].charAt(6) == 'S' || line[8].charAt(8) == 'S' || line[8].charAt(6) == 'T' || line[8].charAt(8) == 'T') {
                    c3++;
                }
            }
        }
        reader.close();
        System.out.println(c1 + "\t" + c2 + "\t" + c3);
    }

    private static void cao3(String ppl, String xls) throws IOException, JXLException, FileDamageException
    {
        HashMap<String, String> map = new HashMap<String, String>();
        Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
        PeptideListReader pr = new PeptideListReader(ppl);
        IPeptide peptide = null;
        while ((peptide = pr.getPeptide()) != null) {
            String scanname = peptide.getBaseName();
            String sequence = peptide.getSequence();
            if (sequence.contains("N*")) {
                Matcher matcher = N_GLYCO.matcher(sequence);
                if (matcher.find()) {
                    map.put(scanname, "N");
                } else {
                    map.put(scanname, "G");
                }
            } else if (sequence.contains("Q*")) {
                map.put(scanname, "Q");
            } else {
                map.put(scanname, "P");
            }
        }
        pr.close();

        int[] count = new int[4];
        ExcelReader reader = new ExcelReader(xls);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (map.containsKey(line[0])) {
                String s = map.get(line[0]);
                if (s.equals("P")) {
                    count[0]++;
                } else if (s.equals("N")) {
                    count[1]++;
                } else if (s.equals("Q")) {
                    count[2]++;
                } else if (s.equals("G")) {
                    count[3]++;
                }
            }
        }
        System.out.println(Arrays.toString(count));
    }

    private static void compare(String s1, String s2) throws IOException, JXLException
    {
        HashMap<String, String[]> m1 = new HashMap<String, String[]>();
        ExcelReader reader1 = new ExcelReader(s1);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            String[] ss = line1[0].split("\\.");
            int fileid = Integer.parseInt(ss[2]);
            int nid = (fileid + 1) / 2;
            String scanname = ss[0] + "." + ss[1] + ".1." + ss[3] + "." + ss[4] + "." + nid;
//			System.out.println(scanname);
            m1.put(scanname, line1);
        }
        reader1.close();

        HashMap<String, String[]> m2 = new HashMap<String, String[]>();
        ExcelReader reader2 = new ExcelReader(s2);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            String scanname = line2[0].substring(0, line2[0].lastIndexOf("."));
            m2.put(scanname, line2);
        }
        reader2.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        System.out.println(m1.size() + "\t" + m2.size() + "\t" + set.size());

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (m1.containsKey(key) && m2.containsKey(key)) {
                String[] l1 = m1.get(key);
                String[] l2 = m2.get(key);
                if (!l1[7].equals(l2[7])) {
                    System.out.println(l1[2] + "\t" + l2[2] + "\t" + (!l2[6].contains("REV")) + "\t" + l1[7] + "\t" + l2[7]);
                }
            }
        }
    }

    private static void cao5(String info, String result) throws IOException, JXLException
    {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        BufferedReader reader = new BufferedReader(new FileReader(info));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] cs = line.split("#")[0].split("\t");
            int type = Integer.parseInt(cs[3]);
            map.put(cs[0], type);
        }
        reader.close();
        int c0 = 0;
        int[] counts = new int[5];
        ExcelReader er = new ExcelReader(result);
        String[] cs = er.readLine();
        while ((cs = er.readLine()) != null) {
            int type = map.get(cs[0]);
            counts[type]++;
            double ds = Double.parseDouble(cs[9]);
            if (ds == 0) {
                c0++;
            }
        }
        er.close();

        System.out.println(Arrays.toString(counts) + "\t" + c0);
    }

    private static void pplcompare(String old_ver,
            String new_ver) throws PeptideParsingException, FileDamageException, IOException
    {

        HashMap<String, IPeptide> m1 = new HashMap<String, IPeptide>();
        IPeptideListReader r1 = new PeptideListReader(old_ver);
        IPeptide p1 = null;
        while ((p1 = r1.getPeptide()) != null) {
//			if(p1.getPrimaryScore()<15) continue;
            String basename = p1.getBaseName();
            String[] ss = basename.split("\\.");
            int fileid = Integer.parseInt(ss[2]);
            int nid = (fileid + 1) / 2;
            String scanname = ss[0] + "." + ss[1] + ".1." + ss[3] + "." + ss[4] + "." + nid;
            if (m1.containsKey(scanname)) {
                if (p1.getPrimaryScore() > m1.get(scanname).getPrimaryScore()) {
                    m1.put(scanname, p1);
                }
            } else {
                m1.put(scanname, p1);
            }
        }
        r1.close();

        HashMap<String, IPeptide> m2 = new HashMap<String, IPeptide>();
        IPeptideListReader r2 = new PeptideListReader(new_ver);
        IPeptide p2 = null;
        while ((p2 = r2.getPeptide()) != null) {
//			if(p2.getPrimaryScore()<15) continue;
            String basename = p2.getBaseName();
            String scanname = basename.substring(0, basename.lastIndexOf("."));
            if (m2.containsKey(scanname)) {
                if (p2.getPrimaryScore() > m2.get(scanname).getPrimaryScore()) {
                    m2.put(scanname, p2);
                }
            } else {
                m2.put(scanname, p2);
            }
        }
        r2.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        System.out.println(m1.size() + "\t" + m2.size() + "\t" + set.size());

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (m1.containsKey(key) && m2.containsKey(key)) {
                IPeptide pep1 = m1.get(key);
                IPeptide pep2 = m2.get(key);
//				System.out.println(key+"\t"+pep1.getPrimaryScore()+"\t"+pep2.getPrimaryScore()
//						+"\t"+(pep2.getPrimaryScore()-pep1.getPrimaryScore())+"\t"+pep1.isTP()+"\t"+pep2.isTP());
            } else {
                if (m1.containsKey(key)) {
                    IPeptide pep1 = m1.get(key);
//					System.out.println(key+"\t"+pep1.getPrimaryScore()+"\t"+pep1.isTP());
                } else {
                    IPeptide pep2 = m2.get(key);
                    System.out.println(key + "\t" + pep2.getPrimaryScore() + "\t" + pep2.isTP());
                }
            }
        }
    }

    private static void testNLoss(String in) throws FileDamageException, IOException
    {

//		double [] fragments = new double []{0, 203.079373, 365.132198, 406.158746, 568.211571,
//				656.227614635, 730.264396, 859.306987635, 1021.359812635, 1312.45522927};

        double[] fragments = new double[]{0, 203.079373, 365.132198, 656.227614635};
        int[] ccc = new int[5];
        PeptideListReader reader = new PeptideListReader(in);
        IPeptide peptide;
        while ((peptide = reader.getPeptide()) != null) {
            if (!peptide.isTP() || peptide.getPrimaryScore() < 15) continue;
            String sequence = peptide.getSequence();
            int count = sequence.length() - sequence.replaceAll("@", "").length();
            if (count == 0) continue;

            ccc[2]++;
            IPeak[] peaks = reader.getPeakLists()[0].getPeakArray();
            double mw = peptide.getMr();
//			double pepmw = mw-count*1312.45522927;
            double pepmw = mw - count * 656.227614635;
            int[] find1 = new int[fragments.length];
            int[] find2 = new int[fragments.length];
            int[] find3 = new int[fragments.length];

            for (int pi = 0; pi < peaks.length; pi++) {
                for (int i = 0; i < fragments.length; i++) {
                    double nl = pepmw + fragments[i];
                    double c1 = nl + 1.007276;
                    double c2 = nl / 2.0 + 1.007276;
                    double c3 = nl / 3.0 + 1.007276;
                    if (Math.abs(c1 - peaks[pi].getMz()) < 0.05) {
                        find1[i] = 1;
                    }
                    if (Math.abs(c2 - peaks[pi].getMz()) < 0.05) {
                        find2[i] = 1;
                    }
                    if (Math.abs(c3 - peaks[pi].getMz()) < 0.05) {
                        find3[i] = 1;
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < find1.length; i++) {
                sb.append(find1[i]).append("\t");
            }
            if (MathTool.getTotal(find1) > 0 || MathTool.getTotal(find2) > 0) {
                ccc[3]++;
            }
            if (find1[0] > 0 || find2[0] > 0) {
                ccc[4]++;
            }
//			System.out.println(peptide.getBaseName()+"\t"+peptide.getSequence()+"\t"+peptide.getPrimaryScore()+"\t"+peptide.getCharge()+"\t"+MathTool.getTotal(find1)
//					+"\t"+sb);
            if (find1[0] == 0) {
                if (MathTool.getTotal(find1) > 0) {
                    ccc[0]++;
                } else {
                    ccc[1]++;
                }
            }
        }
        reader.close();
        System.out.println(ccc[0] + "\t" + ccc[1] + "\t" + ccc[2] + "\t" + ccc[3] + "\t" + ccc[4]);
    }

    private static void pplXlsCompare(String ppl,
            String xls) throws IOException, JXLException, PeptideParsingException, FileDamageException
    {
        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, Float> sm = new HashMap<String, Float>();
        ExcelReader er = new ExcelReader(xls);
        String[] line = er.readLine();
        while ((line = er.readLine()) != null) {
            String scanname = line[0].substring(0, line[0].lastIndexOf("."));
            String seq = line[7];
            StringBuilder sb = new StringBuilder();
            boolean start = true;
            for (int i = 0; i < seq.length(); i++) {
                char aa = seq.charAt(i);
                if (aa == '[') {
                    start = false;
                } else if (aa == ']') {
                    start = true;
                } else {
                    if (start) {
                        sb.append(aa);
                    }
                }
            }
            map.put(scanname, sb.toString());
            sm.put(scanname, Float.parseFloat(line[2]));
//			System.out.println(scanname);
        }
        er.close();

        int[] count = new int[5];
        IPeptideListReader pr = new PeptideListReader(ppl);
        IPeptide peptide = null;
        while ((peptide = pr.getPeptide()) != null) {
            String basename = peptide.getBaseName();//System.out.println(basename);
            String sequence = PeptideUtil.getUniqueSequence(peptide.getSequence());
            if (map.containsKey(basename)) {
                if (sequence.equals(map.get(basename))) {
                    count[0]++;
                    System.out.println(basename + "\t" + sequence + "\t" + sm.get(basename) + "\t" + peptide.getPrimaryScore()
                            + "\t" + (sm.get(basename) - peptide.getPrimaryScore()));
                } else {
                    count[1]++;
                }
            }
        }
        pr.close();
        System.out.println(count[0] + "\t" + count[1]);
    }

    private static void xlsCompare2(String ppl,
            String xls) throws IOException, JXLException, PeptideParsingException, FileDamageException
    {

        HashSet<String> coreset = new HashSet<String>();
        coreset.add("NeuAc-Gal-GalNAc");
        coreset.add("NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc");

        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, Float> sm = new HashMap<String, Float>();
        ExcelReader er = new ExcelReader(xls);
        String[] line = er.readLine();
        L:
        while ((line = er.readLine()) != null) {
            String scanname = line[0].substring(0, line[0].lastIndexOf("."));
            String seq = line[7];
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            boolean start = true;
            for (int i = 0; i < seq.length(); i++) {
                char aa = seq.charAt(i);
                if (aa == '[') {
                    start = false;
                    sb2 = new StringBuilder();
                } else if (aa == ']') {
                    start = true;
                    if (!coreset.contains(sb2.toString())) {
//						continue L;
                    }
                } else {
                    if (start) {
                        sb.append(aa);
                    } else {
                        sb2.append(aa);
                    }
                }
            }
            map.put(scanname, sb.toString());
            sm.put(scanname, Float.parseFloat(line[2]));
//			System.out.println(scanname);
        }
        er.close();

        System.out.println(map.size());

        int[] count = new int[5];
        ExcelReader er2 = new ExcelReader(ppl);
        while ((line = er2.readLine()) != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < line[1].length() - 2; i++) {
                char aa = line[1].charAt(i);
                if (aa >= 'A' && aa <= 'Z') {
                    sb.append(aa);
                }
            }
            if (map.containsKey(line[0])) {
                if (map.get(line[0]).equals(sb.toString())) {
                    count[2]++;
                } else {
                    count[3]++;
                }
            } else {
                if (line[4].equals("0")) {
                    count[0]++;
                } else {
                    count[1]++;
                }
            }
        }
        er2.close();
        System.out.println(count[0] + "\t" + count[1] + "\t" + count[2] + "\t" + count[3]);
    }

    private static void typecount(String pepinfo, String xls) throws IOException, JXLException
    {

        HashMap<String, OGlycanScanInfo2> infomap = new HashMap<String, OGlycanScanInfo2>();
        BufferedReader reader = new BufferedReader(new FileReader(pepinfo));
        String line = null;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            infomap.put(scanname, info);
        }
        reader.close();

        int count = 0;
        int alltype = 0;
        ExcelReader er = new ExcelReader(xls);
        String[] cs = er.readLine();
        while ((cs = er.readLine()) != null) {
            alltype++;
            if (infomap.containsKey(cs[0])) {
                double totalmass = 0;
                OGlycanScanInfo2 info2 = infomap.get(cs[0]);
                OGlycanUnit[] units = info2.getUnits()[0];
                for (int i = 0; i < units.length; i++) {
                    totalmass += units[i].getMass();
                }
                for (int i = 1; i <= 6; i++) {
                    if (Math.abs(totalmass - i * 656.227614635) < 1) {
                        count++;
                    }
                }
            }
        }
        er.close();
        System.out.println(count + "\t" + alltype);
    }

    private static HashMap<String, IPeptide> typeCountCore14(String ppl14,
            String ppl25) throws FileDamageException, IOException
    {
        HashMap<String, IPeptide> map = new HashMap<String, IPeptide>();
        PeptideListReader reader1 = new PeptideListReader(ppl14);
        IPeptide peptide = null;
        while ((peptide = reader1.getPeptide()) != null) {
            if (peptide.getPrimaryScore() < 15) continue;
            if (!peptide.isTP()) continue;
            if (!peptide.getSequence().contains("@")) continue;
            map.put(peptide.getBaseName(), peptide);
        }
        reader1.close();

        PeptideListReader reader2 = new PeptideListReader(ppl25);
        while ((peptide = reader2.getPeptide()) != null) {
            if (peptide.getPrimaryScore() < 15) continue;
            if (!peptide.isTP()) continue;
            if (!peptide.getSequence().contains("@")) continue;
            String basename = peptide.getBaseName();
            if (map.containsKey(basename)) {
                if (peptide.getPrimaryScore() > map.get(basename).getPrimaryScore()) {
                    map.put(peptide.getBaseName(), peptide);
                }
            } else {
                map.put(peptide.getBaseName(), peptide);
            }
        }
        reader2.close();
        System.out.println(map.size());
        return map;
    }

    private static void typeCompare(String pepinfo, String xls, String ppl1,
            String ppl2) throws FileDamageException, IOException, JXLException
    {

        HashMap<String, IPeptide> map1 = typeCountCore14(ppl1, ppl2);
        HashMap<String, Double> map2 = new HashMap<String, Double>();
        HashMap<String, OGlycanScanInfo2> infomap = new HashMap<String, OGlycanScanInfo2>();
        BufferedReader reader = new BufferedReader(new FileReader(pepinfo));
        String line = null;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            infomap.put(scanname, info);
        }
        reader.close();

        int count = 0;
        int alltype = 0;
        ExcelReader er = new ExcelReader(xls);
        String[] cs = er.readLine();
        while ((cs = er.readLine()) != null) {
            alltype++;
            if (infomap.containsKey(cs[0])) {
                double totalmass = 0;
                OGlycanScanInfo2 info2 = infomap.get(cs[0]);
                OGlycanUnit[] units = info2.getUnits()[0];
                for (int i = 0; i < units.length; i++) {
                    totalmass += units[i].getMass();
                }
                for (int i = 1; i <= 6; i++) {
                    if (Math.abs(totalmass - i * 656.227614635) < 1) {
                        count++;
                        map2.put(cs[0].substring(0, cs[0].lastIndexOf(".")), Double.parseDouble(cs[2]));
                        continue;
                    }
                }
            }
        }
        er.close();
        System.out.println(count + "\t" + alltype);

        HashSet<String> set = new HashSet<String>();
        set.addAll(map1.keySet());
        set.addAll(map2.keySet());
        System.out.println(map1.size() + "\t" + map2.size() + "\t" + set.size());

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (map1.containsKey(key) && map2.containsKey(key)) {
                System.out.println(key + "\t" + map1.get(key).getPrimaryScore() + "\t" + map2.get(key));
            }
        }
    }

    private static void xlsCompare3(String oldv, String newv) throws IOException, JXLException
    {
        HashSet<String> s1 = new HashSet<String>();
        ExcelReader oldr = new ExcelReader(oldv);
        String[] line = oldr.readLine();
        while ((line = oldr.readLine()) != null) {
            s1.add(line[0]);
        }
        oldr.close();
        HashSet<String> s2 = new HashSet<String>();
        ExcelReader newr = new ExcelReader(newv);
        while ((line = newr.readLine()) != null) {
            s2.add(line[0]);
        }
        newr.close();
        HashSet<String> set = new HashSet<String>();
        set.addAll(s1);
        set.addAll(s2);
        System.out.println(s1.size() + "\t" + s2.size() + "\t" + set.size());
    }

    private static void pplConvert(String in, String out) throws FileDamageException, IOException, ProWriterException
    {
        int count = 0;
        HashMap<String, IPeptide> pepmap = new HashMap<String, IPeptide>();
        HashMap<String, IMS2PeakList[]> peakmap = new HashMap<String, IMS2PeakList[]>();
        PeptideListReader reader = new PeptideListReader(in);
        PeptideListWriter writer = new PeptideListWriter(out, reader.getPeptideFormat(),
                reader.getSearchParameter(), reader.getDecoyJudger(), reader.getProNameAccesser());
        IPeptide peptide;
        while ((peptide = reader.getPeptide()) != null) {
//			if(peptide.getPrimaryScore()<1) continue;
            String basename = peptide.getBaseName();
            String key = basename.substring(0, basename.lastIndexOf("."));
            count++;
            if (pepmap.containsKey(key)) {
                if (peptide.getPrimaryScore() > pepmap.get(key).getPrimaryScore()) {
                    pepmap.put(key, peptide);
//					peakmap.put(basename, reader.getPeakLists());
                }
            } else {
                pepmap.put(key, peptide);
                peakmap.put(key, reader.getPeakLists());
            }
            if (count % 1000 == 0) System.out.println(count + "\t" + pepmap.size());
        }
        reader.close();
        System.out.println(pepmap.size());
        Iterator<String> it = pepmap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            IPeptide pep = pepmap.get(key);
            IMS2PeakList[] peaklist = peakmap.get(key);
            writer.write(pep, peaklist);
        }
        writer.close();
    }

    private static void pplCombine(String in, String out) throws FileDamageException, IOException, ProWriterException
    {

        File[] files = (new File(in)).listFiles(new FileFilter()
        {

            @Override
            public boolean accept(File arg0)
            {
                // TODO Auto-generated method stub
                if (arg0.getName().endsWith("ppl"))
                    return true;
                return false;
            }

        });

        PeptideListReader reader = new PeptideListReader(files[0]);
        PeptideListWriter writer = new PeptideListWriter(out, reader.getPeptideFormat(),
                reader.getSearchParameter(), reader.getDecoyJudger(), reader.getProNameAccesser());
        IPeptide peptide;
        while ((peptide = reader.getPeptide()) != null) {
            writer.write(peptide, reader.getPeakLists());
        }
        reader.close();
        for (int i = 1; i < files.length; i++) {
            PeptideListReader readeri = new PeptideListReader(files[i]);
            writer.getProteinAccesser().appand(readeri.getProNameAccesser());
            while ((peptide = readeri.getPeptide()) != null) {
                writer.write(peptide, readeri.getPeakLists());
            }
            readeri.close();
        }

        writer.close();
    }

    private static void scoredis(String in) throws FileDamageException, IOException
    {
        int[] tcount = new int[30];
        int[] dcount = new int[30];
        PeptideListReader reader = new PeptideListReader(in);
        IPeptide peptide = null;
        while ((peptide = reader.getPeptide()) != null) {
            int score = (int) peptide.getPrimaryScore();
            if (peptide.isTP()) {
                if (score >= 30) {
                    for (int i = 0; i < 30; i++)
                        tcount[i]++;
                } else {
                    for (int i = 0; i <= score; i++)
                        tcount[i]++;
                }
            } else {
                if (score >= 30) {
                    for (int i = 0; i < 30; i++)
                        dcount[i]++;
                } else {
                    for (int i = 0; i <= score; i++)
                        dcount[i]++;
                }
            }
        }
        reader.close();
        for (int i = 0; i < 30; i++) {
            System.out.println(tcount[i] + "\t" + dcount[i]);
        }
    }

    private static void resultCompare(String s1, String s2) throws FileDamageException, IOException
    {
        HashMap<String, String> m1 = new HashMap<String, String>();
        HashMap<String, String> m2 = new HashMap<String, String>();
        PeptideListReader r1 = new PeptideListReader(s1);
        IPeptide peptide;
        while ((peptide = r1.getPeptide()) != null) {

        }
        r1.close();
    }

    private static void heteroCount(String in) throws IOException, JXLException
    {
        int[] hete = new int[5];
        ExcelReader reader = new ExcelReader(in, 1);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            int count = 0;
            for (int i = 3; i < line.length; i++) {
                if (line[i].equals("+")) {
                    count++;
                }
            }
            if (count == 0) System.out.println(Arrays.toString(line));
            if (count >= 5) {
                hete[4]++;
            } else {
                hete[count - 1]++;
            }
        }
        reader.close();
        for (int i = 0; i < hete.length; i++) {
            System.out.println(hete[i]);
        }
    }

    private static void proteinPicker(String in, int sheet, String protein) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(in, sheet);
        String[] line = reader.readLine();
        int id = -1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length; i++) {
            sb.append(line[i]).append("\t");
            if (line[i].equals("Proteins")) {
                id = i;
            }
        }
        System.out.println(sb);
        while ((line = reader.readLine()) != null) {
            if (line[id].startsWith(protein)) {
                StringBuilder sb2 = new StringBuilder();
                for (int i = 0; i < line.length; i++) {
                    sb2.append(line[i]).append("\t");
                }
                System.out.println(sb2);
            }
        }
        reader.close();
    }

    private static void mgftest(String m1, String m2) throws IOException
    {
        HashSet<String> s1 = new HashSet<String>();
        BufferedReader r1 = new BufferedReader(new FileReader(m1));
        String line = null;
        while ((line = r1.readLine()) != null) {
            if (line.startsWith("TITLE")) {
                s1.add(line);
            }
        }
        r1.close();

        HashSet<String> s2 = new HashSet<String>();
        BufferedReader r2 = new BufferedReader(new FileReader(m2));
        while ((line = r2.readLine()) != null) {
            if (line.startsWith("TITLE")) {
                s2.add(line);
            }
        }
        r2.close();

        HashSet<String> all = new HashSet<String>();
        all.addAll(s1);
        all.addAll(s2);
        System.out.println(s1.size() + "\t" + s2.size() + "\t" + all.size());

        Iterator<String> it = s1.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (!s2.contains(key)) {
                System.out.println(key);
                break;
            }
        }
    }

    private static void filetest(String f1, String f2)
    {
        File[] files1 = (new File(f1)).listFiles();
        File[] files2 = (new File(f2)).listFiles();
        HashSet<String> s1 = new HashSet<String>();
        for (int i = 0; i < files1.length; i++) {
            s1.add(files1[i].getName());
        }
        HashSet<String> s2 = new HashSet<String>();
        for (int i = 0; i < files2.length; i++) {
            s2.add(files2[i].getName());
        }
        HashSet<String> set = new HashSet<String>();
        set.addAll(s1);
        set.addAll(s2);
        System.out.println(s1.size() + "\t" + s2.size() + "\t" + set.size());
    }

    private static void copyPic(String in, String protein, String pics, String out) throws IOException, JXLException
    {

        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        int id = -1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length; i++) {
            sb.append(line[i]).append("\t");
            if (line[i].equals("Proteins")) {
                id = i;
            }
        }
        System.out.println(sb);
        HashSet<String> set = new HashSet<String>();
        while ((line = reader.readLine()) != null) {
//			if(line[id].startsWith(protein)){
            if (line[6].contains(protein)) {
                StringBuilder sb2 = new StringBuilder();
                for (int i = 0; i < line.length; i++) {
                    sb2.append(line[i]).append("\t");
                }
                System.out.println(sb2);
                String scan = line[0].substring(6, line[0].lastIndexOf("."));
                set.add(scan);
//				System.out.println(scan);
            }
        }
        reader.close();

        File[] files = (new File(pics)).listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            String scan = name.substring(0, name.lastIndexOf("."));
            scan = scan.substring(0, scan.lastIndexOf("."));//System.out.println(scan);
            if (set.contains(scan)) {
                FileInputStream fis = new FileInputStream(files[i]);
                FileOutputStream fos = new FileOutputStream(out + "\\" + name);
                byte[] temp = new byte[1000];
                int size = fis.read(temp);
                while (size != -1) {
                    fos.write(temp);
                    size = fis.read(temp);
                }
                fis.close();
                fos.close();
            }
        }
    }

    private static void compareResult(String ppl1, String ppl2, String ppl3,
            String result) throws FileDamageException, IOException, JXLException
    {
        HashMap<String, IPeptide> map = new HashMap<String, IPeptide>();
        PeptideListReader r1 = new PeptideListReader(ppl1);
        PeptideListReader r2 = new PeptideListReader(ppl2);
        PeptideListReader r3 = new PeptideListReader(ppl3);
        IPeptide peptide = null;
        while ((peptide = r1.getPeptide()) != null) {
            String basename = peptide.getBaseName();
            map.put(basename, peptide);
        }
        r1.close();
        while ((peptide = r2.getPeptide()) != null) {
            String basename = peptide.getBaseName();
            map.put(basename, peptide);
        }
        r2.close();
        while ((peptide = r3.getPeptide()) != null) {
            String basename = peptide.getBaseName();
            map.put(basename, peptide);
//			System.out.println(basename);
        }
        r3.close();
        System.out.println(map.size());

        ExcelReader reader = new ExcelReader(result);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String scanname = line[0].substring(0, line[0].lastIndexOf("."));
            if (map.containsKey(scanname)) {
                IPeptide pep = map.get(scanname);
                StringBuilder sb = new StringBuilder();
                StringBuilder ssb = new StringBuilder();
                for (int i = 0; i < line[7].length(); i++) {
                    char aa = line[7].charAt(i);
                    if (aa >= 'A' && aa <= 'Z') {
                        ssb.append(aa);
                    }
                }
                String seq1 = ssb.toString();
                String seq2 = PeptideUtil.getUniqueSequence(pep.getSequence());
                if (seq1.equals(seq2)) {
                    sb.append("True\t");
                    sb.append(scanname).append("\t");
                    double score = Double.parseDouble(line[2]);
                    sb.append(score).append("\t");
                    sb.append(pep.getPrimaryScore()).append("\t");
                    sb.append(score - pep.getPrimaryScore()).append("\t");
                    sb.append(line[6]).append("\t");
                } else {
                    sb.append("False\t");
                    sb.append(scanname).append("\t");
                    double score = Double.parseDouble(line[2]);
                    sb.append(score).append("\t");
                    sb.append(pep.getPrimaryScore()).append("\t");
                    sb.append(score - pep.getPrimaryScore()).append("\t");
                    sb.append(line[6]).append("\t");
                    sb.append(pep.getSequence()).append("\t");
                }
                System.out.println(sb);
            }
        }
        reader.close();
    }

    private static void pepInfoCompare(String info1, String info2) throws IOException
    {
        HashSet<String> s11 = new HashSet<String>();
        HashSet<String> s12 = new HashSet<String>();
        BufferedReader r1 = new BufferedReader(new FileReader(info1));
        String line = null;
        while ((line = r1.readLine()) != null) {
            String[] cs = line.split("\t");
            String name = cs[0];
            s11.add(name);
            name = name.substring(0, name.lastIndexOf("."));
            s12.add(name);
        }
        r1.close();

        HashSet<String> s21 = new HashSet<String>();
        HashSet<String> s22 = new HashSet<String>();
        BufferedReader r2 = new BufferedReader(new FileReader(info2));
        while ((line = r2.readLine()) != null) {
            String[] cs = line.split("\t");
            String name = cs[0];
            s21.add(name);
            name = name.substring(0, name.lastIndexOf("."));
            s22.add(name);
        }
        r2.close();

        System.out.println(s11.size() + "\t" + s12.size() + "\t" + s21.size() + "\t" + s22.size());
    }

    private static void compare20140424(String s1, String s2) throws IOException, JXLException
    {

        HashMap<String, String> m1 = new HashMap<String, String>();
        ExcelReader reader1 = new ExcelReader(s1);
        String[] line = reader1.readLine();
        while ((line = reader1.readLine()) != null) {
            StringBuilder sb = new StringBuilder();
            boolean use = true;
            for (int i = 0; i < line[6].length(); i++) {
                char ai = line[6].charAt(i);
                if (ai == '[') {
                    use = false;
                } else if (ai == ']') {
                    use = true;
                } else {
                    if (use) {
                        sb.append(ai);
                    }
                }
            }
            m1.put(line[0], sb.toString());
        }
        reader1.close();

        HashMap<String, String> m2 = new HashMap<String, String>();
        ExcelReader reader2 = new ExcelReader(s2);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            StringBuilder sb = new StringBuilder();
            boolean use = true;
            for (int i = 0; i < line2[6].length(); i++) {
                char ai = line2[6].charAt(i);
                if (ai == '[') {
                    use = false;
                } else if (ai == ']') {
                    use = true;
                } else {
                    if (use) {
                        sb.append(ai);
                    }
                }
            }
            m2.put(line2[0], sb.toString());
        }
        reader2.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (m1.containsKey(key) && m2.containsKey(key)) {
                if (!m1.get(key).equals(m2.get(key))) {
                    System.out.println(key + "\t" + m1.get(key) + "\t" + m2.get(key));
                }
            }
        }
        System.out.println(m1.size() + "\t" + m2.size() + "\t" + set.size());
    }

    /**
     * @param args
     * @throws JXLException
     * @throws IOException
     * @throws FileDamageException
     * @throws DtaFileParsingException
     * @throws PeptideParsingException
     * @throws ProWriterException
     */
    public static void main(
            String[] args) throws IOException, JXLException, FileDamageException, DtaFileParsingException, PeptideParsingException, ProWriterException
    {

//		OGlycanTest3.mgftest("H:\\OGlycan_final_20130712\\standard\\fetuin\\20120329_Fetuin_elastase_HILIC_5ug-03\\" +
//				"20120329_Fetuin_elastase_HILIC_5ug-03.oglycan.mgf");
//		OGlycanTest3.fetuinTest();
/*
		OGlycanTest3.pplCompare("H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin\\" +
				"trypsin.original1.F001883.dat.ppl", "H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin\\" +
				"trypsin.oglycan.F001806.dat.ppl", "H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin\\trypsin.info");
*/
//		OGlycanTest3.pplCompare("H:\\OGlycan_final_20130830\\serum_1D\\denoise\\1D.denoise.oglycan.F005222.dat.ppl",
//				"H:\\OGlycan_final_20130830\\serum_1D\\glycan\\1D.glycan.oglycan.F005223.dat.ppl",
//				"H:\\OGlycan_final_20130830\\serum_1D\\glycan\\glycan.info");


/*		OGlycanTest3.peptideCompare("H:\\OGlycan_final_20130719\\serum_1D\\combine\\" +
				"serum_1D.deglyco.F005100.dat.ppl",
				"H:\\OGlycan_final_20130719\\serum_1D\\combine\\" +
				"serum_1D.deglyco.Allpeak.F005123.dat.ppl");
*/
/*
		OGlycanTest3.xlsCompare("H:\\OGlycan_final_20130719\\serum_1D\\combine" +
				"\\serum_1D.deglyco.F005100.xls",
				"H:\\OGlycan_final_20130719\\serum_1D\\combine" +
				"\\serum_1D.oglycan.F005101.xls");
*/

		/*OGlycanTest3.siteCompare("H:\\OGlycan_final_20130830\\serum_2D\\2D_trypsin" +
				"\\2D_trypsin.oglycan.F005251.xls",
				"H:\\OGlycan_final_20130830\\serum_2D\\2D_T+C" +
				"\\2D_T+C.oglycan.F005252.xls",
				"H:\\OGlycan_final_20130830\\serum_2D\\elastase" +
				"\\elastase.oglycan.F005253.xls");*/

//		OGlycanTest3.sptest("H:\\OGlycan_final_20130712\\humanserum\\20120328_humaneserum_trypsin\\20120328_humaneserum_trypsin.info");

//		OGlycanTest3.siteTypeCount("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\TC\\TC.xls");

//		OGlycanTest3.intensityTest("H:\\OGlycan_final_20130830\\fetuin\\denoise\\F005254.type2.dat.ppl",
//				"H:\\OGlycan_final_20130830\\fetuin\\denoise\\denoise.oglycan.mgf", 947.323);

//		OGlycanTest3.combine("H:\\OGlycan_final_20140101\\2D_trypsin_2\\2D_trypsin_2.oglycan.F001332.dat.xls",
//				"H:\\OGlycan_final_20140101\\2D_T+C\\2D_T+C.oglycan.F001395.dat.xls",
//				"H:\\OGlycan_final_20140101\\2D_elastase\\2D_elastase.oglycan.F001334.dat.xls",
//				"H:\\OGlycan_final_20140101\\support information.xls");
//		OGlycanTest3.resume("H:\\OGlycan_final_20131104\\serum_2D\\2D_T+C\\2D_T+C.info",
//				"H:\\OGlycan_final_20131104\\serum_2D\\2D_T+C\\2D_T+C.oglycan.mgf",
//				"H:\\OGlycan_final_20131104\\serum_2D\\2D_T+C\\2D_T+C.oglycan.denoise.mgf");
        OGlycanTest3.siteFormCount("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\201405018.xls");
//		OGlycanTest3.cao("H:\\OGlycan_final_20131120\\20131121_NQ\\2D_T+C.oglycan.F005987.xls");
//		OGlycanTest3.cao3("H:\\OGlycan_final_20131120\\20131121_NQ\\2D_trypsin.oglycan.F005986.dat.ppl",
//				"H:\\OGlycan_final_20131120\\20131121_NQ\\2D_trypsin.oglycan.F005986.xls");

//		OGlycanTest3.compare("H:\\OGlycan_final_20131212\\trypsin\\old_rev.2D_trypsin.oglycan.F001315.xls",
//				"H:\\OGlycan_final_20131212\\trypsin\\trypsin.oglycan.F001314.dat.xls");

//		OGlycanTest3.cao5("H:\\OGlycan_final_20131212\\trypsin\\trypsin.info", "H:\\OGlycan_final_20131212\\trypsin\\trypsin.oglycan.F001277.xls");
//		OGlycanTest3.pplcompare("H:\\OGlycan_final_20131212\\trypsin\\old_rev.2D_trypsin.oglycan.F001315.dat.ppl",
//				"H:\\OGlycan_final_20131212\\trypsin_20131219\\trypsin_20131219.oglycan.F001316.dat.ppl");
//		OGlycanTest3.testNLoss("H:\\OGlycan_final_20131212\\fetuin_20131224_combine\\fetuin_20131224_combine.oglycan.core14.F001391.dat.ppl");
//		OGlycanTest3.pplXlsCompare("H:\\OGlycan_final_20131212\\trypsin_20131224_sp3\\trypsin_20131224.oglycan.original.core14.F001382.dat.ppl",
//				"H:\\OGlycan_final_20131212\\trypsin_20131224_charge3\\trypsin_20131224_charge3.oglycan.F001384.dat.xls");
//		OGlycanTest3.typecount("H:\\OGlycan_final_20131212\\fetuin_20131224_charge3\\fetuin_20131224_charge3.info",
//				"H:\\OGlycan_final_20131212\\fetuin_20131224_charge3\\fetuin_20131224_charge3.oglycan.F001385.xls");
//		OGlycanTest3.typeCountCore14("H:\\OGlycan_final_20131212\\fetuin_20131224_combine\\" +
//				"fetuin_20131224_combine.oglycan.core14.F001391.dat.ppl",
//				"H:\\OGlycan_final_20131212\\fetuin_20131224_combine\\" +
//				"fetuin_20131224_combine.oglycan.core25.F001392.dat.ppl");

//		OGlycanTest3.typeCompare("H:\\OGlycan_final_20131212\\trypsin_20131224_charge3\\trypsin_20131224_charge3.info",
//				"H:\\OGlycan_final_20131212\\trypsin_20131224_charge3\\trypsin_20131224_charge3.oglycan.F001384.dat.xls",
//				"H:\\OGlycan_final_20131212\\trypsin_20131224_combine" +
//						"\\trypsin_20131224.oglycan.original.core14.F001382.dat.ppl",
//						"H:\\OGlycan_final_20131212\\trypsin_20131224_combine" +
//						"\\trypsin_20131224.oglycan.original.core25.F001383.dat.ppl");

//		OGlycanTest3.xlsCompare3("H:\\OGlycan_final_20131120\\20131121_NQ\\2D_T+C.oglycan.F005987.xls",
//				"H:\\OGlycan_final_20131212\\TC_20131224_charge3\\TC_20131224_charge3.F001390.dat.xls");

//		OGlycanTest3.pplConvert("H:\\OGlycan_final_20140101\\2D_trypsin_2\\2D_trypsin_2.oglycan.plus10.F001400.dat.ppl",
//				"H:\\OGlycan_final_20140101\\2D_trypsin_2\\2D_trypsin_2.oglycan.plus10.F001400.dat.rank1.ppl");

//		OGlycanTest3.pplCombine("H:\\OGlycan_final_20131212\\elastase_20131224_charge3",
//				"H:\\OGlycan_final_20131212\\elastase_20131224_charge3\\test\\elastase_20131224_charge3.ppl");

//		OGlycanTest3.scoredis("H:\\OGlycan_final_20140101\\2D_trypsin_2\\2D_trypsin_2.oglycan.plus10.F001400.dat.rank1.ppl");
//		OGlycanTest3.heteroCount("H:\\OGlycan_final_20140101\\support information.xls");
//		OGlycanTest3.proteinPicker("H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin\\trypsin.xls", 0, "sp|P01876");
//		OGlycanTest3.mgftest("H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin\\trypsin.oglycan.mgf",
//				"H:\\OGLYCAN\\OGlycan_final_20140312\\20140313\\trypsin\\trypsin.oglycan.mgf");
//		OGlycanTest3.filetest("D:\\P\\o-glyco\\2014.03.04.revise\\spectra all 20140316", "D:\\P\\o-glyco\\2014.03.04.revise\\spectra all");
//		OGlycanTest3.copyPic("H:\\OGLYCAN\\OGlycan_20140405\\fetuin_20140405\\fetuin_20140405.xls", "CPDCPS",
//				"H:\\OGLYCAN\\OGlycan_20140405\\fetuin_20140405\\fetuin spectra", "H:\\OGLYCAN\\OGlycan_20140405\\fetuin_20140405\\15789");

//		OGlycanTest3.compareResult("H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin"
//				+ "\\trypsin.original1.F001883.dat.ppl", "H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin"
//						+ "\\trypsin.original2.F001880.dat.ppl", "H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin"
//								+ "\\trypsin.original3.F001884.dat.ppl", "H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin"
//										+ "\\trypsin.xls");

//		OGlycanTest3.pepInfoCompare("H:\\OGLYCAN\\OGlycan_final_20140312\\20140324\\trypsin2.0\\trypsin2.0.info",
//				"H:\\OGLYCAN\\OGlycan_final_20140312\\20140324\\trypsin4.0\\trypsin4.0.info");
//		OGlycanTest3.compare20140424("H:\\OGLYCAN\\OGlycan_20140423_core14b\\trypsin\\trypsin.xls",
//				"H:\\OGLYCAN\\OGlycan_20140405\\trypsin_20140405\\trypsin_20140405.xls");
//		OGlycanTest3.heteroCount("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\no_homo.201405018.xls");
    }

}
