/*
 ******************************************************************************
 * File: OGlycanTest.java * * * Created on 2012-12-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.spectrum.*;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ck
 * @version 2012-12-20, 9:46:31
 */
public class OGlycanTest
{

    private static final String lineSeparator = IOConstant.lineSeparator;
    private static final double tolerance = 0.1;
    private static final DecimalFormat df2 = DecimalFormats.DF0_2;

    public static void test1(String in) throws IOException
    {

        BufferedReader reader = new BufferedReader(new FileReader(in));
        String[] title = reader.readLine().split("\t");
        int scanid = -1;
        int seqid = -1;
        int rankid = -1;
        int proid = -1;
        int scoreid = -1;
        for (int i = 0; i < title.length; i++) {
            if (title[i].equals("Sequence")) {
                seqid = i;
            } else if (title[i].equals("Rank")) {
                rankid = i;
            } else if (title[i].equals("Scan(s)")) {
                scanid = i;
            } else if (title[i].equals("Proteins")) {
                proid = i;
            } else if (title[i].equals("Ions-score")) {
                scoreid = i;
            }
        }

        int total = 0;
        int rank1 = 0;
        int rank25 = 0;
        int target = 0;
        int decoy = 0;
        ArrayList<Double> targetlist = new ArrayList<Double>();
        ArrayList<Double> decoylist = new ArrayList<Double>();

        String line = null;
        String scan = "";
        boolean rank1glyco = false;
        boolean rank2glyco = false;
        boolean first = false;
        boolean firsttc = false;

        while ((line = reader.readLine()) != null) {

            String[] cs = line.split("\t");
            double score = Double.parseDouble(cs[scoreid]);
            boolean glyco = false;
            for (int i = 2; i < cs[seqid].length() - 2; i++) {
                if (cs[seqid].charAt(i) < 'A' || cs[seqid].charAt(i) > 'Z') {
                    if (cs[seqid].charAt(i) != '*') {
                        glyco = true;
                        break;
                    }
                }
            }

            if (cs[scanid].equals(scan)) {
                if (glyco) {
                    rank2glyco = true;
                    if (first) {
                        if (cs[proid].startsWith("REV")) {
                            firsttc = false;
                        } else {
                            firsttc = true;
                        }
                        first = false;
                    }
                }
            } else {
                if (!rank1glyco && rank2glyco) {
                    rank25++;
                    if (firsttc) {
                        target++;
                        targetlist.add(score);
                    } else {
                        decoy++;
                        decoylist.add(score);
                    }
                }
                rank1glyco = false;
                rank2glyco = false;
                first = true;
                firsttc = false;

                total++;
                scan = cs[scanid];
                if (glyco) {
                    rank1++;
                    rank1glyco = true;
                    if (cs[proid].startsWith("REV")) {
                        decoy++;
                        decoylist.add(score);
                    } else {
                        target++;
                        targetlist.add(score);
                    }
                }
            }
        }
        if (!rank1glyco && rank2glyco) {
            rank25++;
            if (firsttc) {
                target++;
            } else {
                decoy++;
            }
        }
        System.out.println("total\t" + total);
        System.out.println("rank1\t" + rank1);
        System.out.println("rank25\t" + rank25);
        System.out.println("target\t" + target);
        System.out.println("decoy\t" + decoy);

        reader.close();

        for (int i = 0; i < decoylist.size(); i++) {
//			System.out.println(decoylist.get(i));
        }

        for (int i = 0; i < 20; i++) {
            int targetcount = 0;
            int decoycount = 0;
            for (int j = 0; j < targetlist.size(); j++) {
                if (targetlist.get(j) > i) {
                    targetcount++;
                }
            }
            for (int j = 0; j < decoylist.size(); j++) {
                if (decoylist.get(j) > i) {
                    decoycount++;
                }
            }
            System.out.println(i + "\t" + targetcount + "\t" + decoycount + "\t" + decoycount / (double) targetcount);
        }

    }

    public static void combineFile(String s1, String s2, String s3, String out) throws IOException
    {

        PrintWriter pw = new PrintWriter(out);
        BufferedReader r1 = new BufferedReader(new FileReader(s1));
        String line = null;
        while ((line = r1.readLine()) != null) {
            pw.write(line + "\n");
        }
        r1.close();

        BufferedReader r2 = new BufferedReader(new FileReader(s2));
        r2.readLine();
        while ((line = r2.readLine()) != null) {
            pw.write(line + "\n");
        }
        r2.close();

        BufferedReader r3 = new BufferedReader(new FileReader(s3));
        r3.readLine();
        while ((line = r3.readLine()) != null) {
            pw.write(line + "\n");
        }
        r3.close();
        pw.close();
    }

    public static void noModMgfT1(String pep, String mgfin, String mgfout) throws IOException, DtaFileParsingException
    {

        PrintWriter pw = new PrintWriter(mgfout);

        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        AminoacidModification aam = new AminoacidModification();
        aam.addModification('*', 15.994915, "Oxi");
        int[] itypes = new int[]{Ion.TYPE_B, Ion.TYPE_Y};
        AminoacidFragment aaf = new AminoacidFragment(aas, aam);

        HashMap<String, MS2Scan> map = new HashMap<String, MS2Scan>();
        MgfReader mgfinreader = new MgfReader(mgfin);
        MS2Scan ms2scan = null;
        while ((ms2scan = mgfinreader.getNextMS2Scan()) != null) {
            String name = ms2scan.getScanName().getBaseName();
            map.put(name, ms2scan);
        }
        mgfinreader.close();

        BufferedReader reader = new BufferedReader(new FileReader(pep));
        String[] title = reader.readLine().split("\t");
        int scanid = -1;
        int seqid = -1;
        int rankid = -1;
        int proid = -1;
        int scoreid = -1;
        for (int i = 0; i < title.length; i++) {
            if (title[i].equals("Sequence")) {
                seqid = i;
            } else if (title[i].equals("Rank")) {
                rankid = i;
            } else if (title[i].equals("Scan(s)")) {
                scanid = i;
            } else if (title[i].equals("Proteins")) {
                proid = i;
            } else if (title[i].equals("Ions-score")) {
                scoreid = i;
            }
        }

        HashSet<String> usedset = new HashSet<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {

            String[] cs = line.split("\t");
            if (usedset.contains(cs[scanid]))
                continue;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cs[seqid].length(); i++) {
                if (cs[seqid].charAt(i) >= 'A' && cs[seqid].charAt(i) <= 'Z') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '*') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '.') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '-') {
                    sb.append(cs[seqid].charAt(i));
                }
            }
            int count = cs[seqid].length() - sb.length();
            if (count == 0) continue;

            usedset.add(cs[scanid]);

            Ions ions = aaf.fragment(sb.toString(), itypes, true);
            Ion[] bs = ions.getIons(Ion.TYPE_B);
            Ion[] ys = ions.getIons(Ion.TYPE_Y);

            ArrayList<Double> list = new ArrayList<Double>();
            for (int i = 0; i < bs.length; i++) {
                list.add(bs[i].getMz());
                list.add(ys[i].getMz());
            }

            Double[] ionmz = list.toArray(new Double[list.size()]);
            Arrays.sort(ionmz);

            String name = cs[scanid].substring(0, cs[scanid].indexOf(","));
            if (map.containsKey(name)) {

                MS2Scan scan = map.get(name);
                IMS2PeakList peaklist = scan.getPeakList();
                PrecursePeak pp = peaklist.getPrecursePeak();
                double mz = pp.getMz();
                short charge = pp.getCharge();

                StringBuilder mgfsb = new StringBuilder();
                mgfsb.append("BEGIN IONS" + lineSeparator);
                mgfsb.append("PEPMASS=" + (mz - 656.227614635 / (double) charge) + lineSeparator);
                mgfsb.append("CHARGE=" + charge + "+" + lineSeparator);
                mgfsb.append("TITLE=" + name + lineSeparator);

                IPeak[] peaks = peaklist.getPeakArray();
                for (int i = 0; i < peaks.length; i++) {
                    double mzi = peaks[i].getMz();
                    double inteni = peaks[i].getIntensity();
                    boolean use = true;
                    for (int j = 0; j < ionmz.length; j++) {
                        if (Math.abs(mzi - ionmz[j] - 656.227614635) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 494.174789635) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 365.132198) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 203.079373) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 656.227614635 + 18.010565) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 494.174789635 + 18.010565) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 365.132198 + 18.010565) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 203.079373 + 18.010565) < tolerance) {
                            use = false;
                            break;
                        } else {

                        }
                    }
                    if (use)
                        mgfsb.append(mzi).append("\t").append(inteni).append(lineSeparator);
                }
                mgfsb.append("END IONS" + lineSeparator);
                pw.write(mgfsb.toString());

            } else {
                System.out.println(name);
            }
        }
        reader.close();
        pw.close();
    }

    public static void noModMgfT2(String pep, String mgfin, String mgfout) throws IOException, DtaFileParsingException
    {

        PrintWriter pw = new PrintWriter(mgfout);

        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        AminoacidModification aam = new AminoacidModification();
        aam.addModification('*', 15.994915, "Oxi");
        int[] itypes = new int[]{Ion.TYPE_B, Ion.TYPE_Y};
        AminoacidFragment aaf = new AminoacidFragment(aas, aam);

        HashMap<String, MS2Scan> map = new HashMap<String, MS2Scan>();
        MgfReader mgfinreader = new MgfReader(mgfin);
        MS2Scan ms2scan = null;
        while ((ms2scan = mgfinreader.getNextMS2Scan()) != null) {
            String name = ms2scan.getScanName().getBaseName();
            map.put(name, ms2scan);
        }
        mgfinreader.close();

        BufferedReader reader = new BufferedReader(new FileReader(pep));
        String[] title = reader.readLine().split("\t");
        int scanid = -1;
        int seqid = -1;
        int rankid = -1;
        int proid = -1;
        int scoreid = -1;
        for (int i = 0; i < title.length; i++) {
            if (title[i].equals("Sequence")) {
                seqid = i;
            } else if (title[i].equals("Rank")) {
                rankid = i;
            } else if (title[i].equals("Scan(s)")) {
                scanid = i;
            } else if (title[i].equals("Proteins")) {
                proid = i;
            } else if (title[i].equals("Ions-score")) {
                scoreid = i;
            }
        }

        HashSet<String> usedset = new HashSet<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {

            String[] cs = line.split("\t");
            if (usedset.contains(cs[scanid]))
                continue;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cs[seqid].length(); i++) {
                if (cs[seqid].charAt(i) >= 'A' && cs[seqid].charAt(i) <= 'Z') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '*') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '.') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '-') {
                    sb.append(cs[seqid].charAt(i));
                }
            }
            int count = cs[seqid].length() - sb.length();
            if (count == 0) continue;

            usedset.add(cs[scanid]);
            Ions ions = aaf.fragment(sb.toString(), itypes, true);
            Ion[] bs = ions.getIons(Ion.TYPE_B);
            Ion[] ys = ions.getIons(Ion.TYPE_Y);

            ArrayList<Double> list = new ArrayList<Double>();
            for (int i = 0; i < bs.length; i++) {
                list.add(bs[i].getMz());
                list.add(ys[i].getMz());
            }

            Double[] ionmz = list.toArray(new Double[list.size()]);
            Arrays.sort(ionmz);

            String name = cs[scanid].substring(0, cs[scanid].indexOf(","));
            if (map.containsKey(name)) {

                MS2Scan scan = map.get(name);
                IMS2PeakList peaklist = scan.getPeakList();
                PrecursePeak pp = peaklist.getPrecursePeak();
                double mz = pp.getMz();
                short charge = pp.getCharge();

                StringBuilder mgfsb = new StringBuilder();
                mgfsb.append("BEGIN IONS" + lineSeparator);
                mgfsb.append("PEPMASS=" + (mz - 656.227614635 / (double) charge * (double) count) + lineSeparator);
                mgfsb.append("CHARGE=" + charge + "+" + lineSeparator);
                mgfsb.append("TITLE=" + name + lineSeparator);

                IPeak[] peaks = peaklist.getPeakArray();
                for (int i = 0; i < peaks.length; i++) {
                    double mzi = peaks[i].getMz();
                    double inteni = peaks[i].getIntensity();
                    boolean use = true;
                    for (int j = 0; j < ionmz.length; j++) {
                        if (Math.abs(mzi - ionmz[j] - 656.227614635) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 494.174789635) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 365.132198) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 203.079373) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 947.32303127) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 656.227614635 + 18.010565) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 494.174789635 + 18.010565) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 365.132198 + 18.010565) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 203.079373 + 18.010565) < tolerance) {
                            use = false;
                            break;
                        } else if (Math.abs(mzi - ionmz[j] - 947.32303127 + 18.010565) < tolerance) {
                            use = false;
                            break;
                        } else {

                        }
                    }
                    if (use)
                        mgfsb.append(mzi).append("\t").append(inteni).append(lineSeparator);
                }
                mgfsb.append("END IONS" + lineSeparator);
                pw.write(mgfsb.toString());

            } else {
                System.out.println(name);
            }
        }
        reader.close();
        pw.close();
    }

    public static void comglyco(String glyco, String noglyco) throws IOException
    {

        BufferedReader noglycoReader = new BufferedReader(new FileReader(noglyco));
        String[] title = noglycoReader.readLine().split("\t");
        int scanid = -1;
        int seqid = -1;
        int rankid = -1;
        int proid = -1;
        int scoreid = -1;
        for (int i = 0; i < title.length; i++) {
            if (title[i].equals("Sequence")) {
                seqid = i;
            } else if (title[i].equals("Rank")) {
                rankid = i;
            } else if (title[i].equals("Scan(s)")) {
                scanid = i;
            } else if (title[i].equals("Proteins")) {
                proid = i;
            } else if (title[i].equals("Ions-score")) {
                scoreid = i;
            }
        }

        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, String> scoremap = new HashMap<String, String>();
        String noglycoline = null;
        while ((noglycoline = noglycoReader.readLine()) != null) {
            String[] cs = noglycoline.split("\t");
            if (!cs[rankid].equals("1"))
                continue;

            map.put(cs[scanid], cs[seqid].substring(2, cs[seqid].length() - 2));
            scoremap.put(cs[scanid], cs[scoreid]);
        }
        noglycoReader.close();

        int sametarget = 0;
        int samedecoy = 0;
        int difftarget = 0;
        int diffdecoy = 0;

        BufferedReader glycoReader = new BufferedReader(new FileReader(glyco));
        String glycoline = glycoReader.readLine();
        while ((glycoline = glycoReader.readLine()) != null) {

            String[] cs = glycoline.split("\t");
            if (!cs[rankid].equals("1"))
                continue;

            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < cs[seqid].length() - 2; i++) {
                if (cs[seqid].charAt(i) >= 'A' && cs[seqid].charAt(i) <= 'Z') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '*') {
                    sb.append(cs[seqid].charAt(i));
                }
            }

            if (map.containsKey(cs[scanid])) {
                if (map.get(cs[scanid]).equals(sb.toString())) {
                    System.out.println(cs[scanid] + "\t" + cs[seqid] + "\t" + cs[scoreid] + "\t" + scoremap.get(cs[scanid]));
                    if (cs[proid].startsWith("REV")) {
                        samedecoy++;
                    } else {
                        sametarget++;
                    }
                } else {
                    if (cs[proid].startsWith("REV")) {
                        diffdecoy++;
                    } else {
                        difftarget++;
                    }
                }
            }
        }
        glycoReader.close();
        System.out.println(sametarget + "\t" + samedecoy);
        System.out.println(difftarget + "\t" + diffdecoy);
    }

    public static void modValidationT1(String pep, String mgfin, String out) throws IOException, DtaFileParsingException
    {

        PrintWriter pw = new PrintWriter(out);

        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        AminoacidModification aam = new AminoacidModification();
        aam.addModification('*', 15.994915, "Oxi");
        int[] itypes = new int[]{Ion.TYPE_B, Ion.TYPE_Y};
        AminoacidFragment aaf = new AminoacidFragment(aas, aam);

        HashMap<String, MS2Scan> map = new HashMap<String, MS2Scan>();
        MgfReader mgfinreader = new MgfReader(mgfin);
        MS2Scan ms2scan = null;
        while ((ms2scan = mgfinreader.getNextMS2Scan()) != null) {
            String name = ms2scan.getScanName().getBaseName();
            map.put(name, ms2scan);
        }
        mgfinreader.close();

        BufferedReader reader = new BufferedReader(new FileReader(pep));
        String[] title = reader.readLine().split("\t");
        int scanid = -1;
        int seqid = -1;
        int rankid = -1;
        int proid = -1;
        int scoreid = -1;
        for (int i = 0; i < title.length; i++) {
            if (title[i].equals("Sequence")) {
                seqid = i;
            } else if (title[i].equals("Rank")) {
                rankid = i;
            } else if (title[i].equals("Scan(s)")) {
                scanid = i;
            } else if (title[i].equals("Proteins")) {
                proid = i;
            } else if (title[i].equals("Ions-score")) {
                scoreid = i;
            }
        }
        int pepcount = 0;

        HashSet<String> usedset = new HashSet<String>();
        HashMap<String, Double> scoremap = new HashMap<String, Double>();
        HashMap<String, Boolean> tdmap = new HashMap<String, Boolean>();
        HashMap<String, String> seqmap = new HashMap<String, String>();
        String line = null;
        while ((line = reader.readLine()) != null) {

            String[] cs = line.split("\t");
            double score = Double.parseDouble(cs[scoreid]);
            boolean target = !cs[proid].startsWith("REV");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cs[seqid].length(); i++) {
                if (cs[seqid].charAt(i) >= 'A' && cs[seqid].charAt(i) <= 'Z') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '*') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '.') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '-') {
                    sb.append(cs[seqid].charAt(i));
                }
            }

            if (usedset.contains(cs[scanid] + sb))
                continue;

            int count = cs[seqid].length() - sb.length();
            if (count == 0) continue;

            usedset.add(cs[scanid] + sb);
            Ions ions = aaf.fragment(sb.toString(), itypes, true);
            Ion[] bs = ions.getIons(Ion.TYPE_B);
            Ion[] ys = ions.getIons(Ion.TYPE_Y);

//			int modbegin = -1;
//			int modend = -1;
            double[] modscore = new double[bs.length + 1];
            double[] binten = new double[bs.length];
            double[] bglyinten = new double[bs.length];
            double[] yinten = new double[ys.length];
            double[] yglyinten = new double[ys.length];
            String name = cs[scanid].substring(0, cs[scanid].indexOf(","));
            if (map.containsKey(name)) {

                MS2Scan scan = map.get(name);
                IMS2PeakList peaklist = scan.getPeakList();
                PrecursePeak pp = peaklist.getPrecursePeak();
                double mz = pp.getMz();
                short charge = pp.getCharge();
                double baseinten = peaklist.getBasePeak().getIntensity();

                IPeak[] peaks = peaklist.getPeakArray();
                for (int i = 0; i < peaks.length; i++) {

                    double mzi = peaks[i].getMz();
                    double inteni = peaks[i].getIntensity();

                    for (int j = 0; j < bs.length; j++) {
                        double bfragmz = bs[j].getMz();
                        double yfragmz = ys[j].getMz();
                        String bfragseq = bs[j].getFragseq();
                        String yfragseq = ys[j].getFragseq();
                        if (bfragseq.contains("S") || bfragseq.contains("T")) {
                            if (Math.abs(mzi - bfragmz - 656.227614635) < tolerance) {
                                bglyinten[j] += inteni;
                            } else if (Math.abs(mzi - bfragmz - 494.174789635) < tolerance) {
                                bglyinten[j] += inteni;
                            } else if (Math.abs(mzi - bfragmz - 365.132198) < tolerance) {
                                bglyinten[j] += inteni;
                            } else if (Math.abs(mzi - bfragmz - 203.079373) < tolerance) {
                                bglyinten[j] += inteni;
                            } else if (Math.abs(mzi - bfragmz - 656.227614635 + 18.010565) < tolerance) {
                                bglyinten[j] += inteni;
                            } else if (Math.abs(mzi - bfragmz - 494.174789635 + 18.010565) < tolerance) {
                                bglyinten[j] += inteni;
                            } else if (Math.abs(mzi - bfragmz - 365.132198 + 18.010565) < tolerance) {
                                bglyinten[j] += inteni;
                            } else if (Math.abs(mzi - bfragmz - 203.079373 + 18.010565) < tolerance) {
                                bglyinten[j] += inteni;
                            } else if (Math.abs(mzi - bfragmz) < tolerance) {
                                binten[j] += inteni;
                            }
                        } else {
                            if (Math.abs(mzi - bfragmz) < tolerance) {
                                binten[j] += inteni;
                            }
                        }
                        if (yfragseq.contains("S") || yfragseq.contains("T")) {
                            if (Math.abs(mzi - yfragmz - 656.227614635) < tolerance) {
                                yglyinten[j] += inteni;
                            } else if (Math.abs(mzi - yfragmz - 494.174789635) < tolerance) {
                                yglyinten[j] += inteni;
                            } else if (Math.abs(mzi - yfragmz - 365.132198) < tolerance) {
                                yglyinten[j] += inteni;
                            } else if (Math.abs(mzi - yfragmz - 203.079373) < tolerance) {
                                yglyinten[j] += inteni;
                            } else if (Math.abs(mzi - yfragmz - 656.227614635 + 18.010565) < tolerance) {
                                yglyinten[j] += inteni;
                            } else if (Math.abs(mzi - yfragmz - 494.174789635 + 18.010565) < tolerance) {
                                yglyinten[j] += inteni;
                            } else if (Math.abs(mzi - yfragmz - 365.132198 + 18.010565) < tolerance) {
                                yglyinten[j] += inteni;
                            } else if (Math.abs(mzi - yfragmz - 203.079373 + 18.010565) < tolerance) {
                                yglyinten[j] += inteni;
                            } else if (Math.abs(mzi - yfragmz) < tolerance) {
                                yinten[j] += inteni;
                            }
                        } else {
                            if (Math.abs(mzi - yfragmz) < tolerance) {
                                yinten[j] += inteni;
                            }
                        }
                    }
                }

                double totalB = 0;
                double totalY = 0;
                for (int i = 0; i < binten.length; i++) {
                    if (binten[i] > 0) {
                        int con = 1;
                        int j = i + 1;
                        for (; j < binten.length; j++) {
                            if (binten[j] > 0) {
                                con++;
                            } else {
                                break;
                            }
                        }
                        if (con > 0) {
                            for (int k = i; k < j; k++) {
                                if (yinten[yinten.length - k - 1] > 0)
                                    totalB += (binten[k] + bglyinten[k]) / baseinten * Math.pow(con, 0.5) * 1.5;
                                else
                                    totalB += (binten[k] + bglyinten[k]) / baseinten * Math.pow(con, 0.5);
                            }
                        }
                    }
                }

                for (int i = 0; i < yinten.length; i++) {
                    if (yinten[i] > 0) {
                        int con = 1;
                        int j = i + 1;
                        for (; j < yinten.length; j++) {
                            if (yinten[j] > 0) {
                                con++;
                            } else {
                                break;
                            }
                        }
                        if (con > 0) {
                            for (int k = i; k < j; k++) {
                                if (binten[binten.length - k - 1] > 0)
                                    totalY += (yinten[k] + yglyinten[k]) / baseinten * Math.pow(con, 0.5) * 1.5;
                                else
                                    totalY += (yinten[k] + yglyinten[k]) / baseinten * Math.pow(con, 0.5);
                            }
                        }
                    }
                }
                totalB = totalB / (double) bs.length;
                totalY = totalY / (double) bs.length;

                if (totalB == 0 && totalY == 0) continue;

                double kby = totalB < totalY ? 1.0 : totalY / (totalB + totalY);
                double finalscore = (totalB + totalY) * kby;

                if (scoremap.containsKey(cs[scanid])) {
                    if (finalscore > scoremap.get(cs[scanid])) {
                        scoremap.put(cs[scanid], finalscore);
                        tdmap.put(cs[scanid], target);
                        seqmap.put(cs[scanid], sb.toString());
                    }
                } else {
                    scoremap.put(cs[scanid], finalscore);
                    tdmap.put(cs[scanid], target);
                    seqmap.put(cs[scanid], sb.toString());
                }

                if (target) {
                    for (int i = 0; i < binten.length; i++) {
//						if(totalB>totalY)
//						System.out.println(cs[scanid]+"\t"+cs[seqid]+"\t"+(i+1)+"\t"+bs[i].getMz()+"\t"+ys[i].getMz()+"\t"+
//								binten[i]+"\t"+bglyinten[i]+"\t"+
//								yinten[yinten.length-i-1]+"\t"+yglyinten[yinten.length-i-1]+"\t"+totalB+"\t"+totalY+"\t"+finalscore);
//						System.out.println(totalB+"\t"+totalY);
                    }
//					break;
//					System.out.println(cs[scanid]+"\t"+cs[seqid]+"\t"+cs[scoreid]+"\t"+totalB+"\t"+totalY+"\t"+finalscore);
                }
                if (cs[seqid].contains("K.RSVLT#VEHR.Q")) {
                    for (int i = 0; i < binten.length; i++) {
//						System.out.println(cs[scanid]+"\t"+cs[seqid]+"\t"+(i+1)+"\t"+bs[i].getMz()+"\t"+ys[i].getMz()+"\t"+
//						binten[i]+"\t"+bglyinten[i]+"\t"+
//						yinten[yinten.length-i-1]+"\t"+yglyinten[yinten.length-i-1]+"\t"+totalB+"\t"+totalY+"\t"+finalscore);
                    }
                }
//				if(pepcount==3)
//					break;

                pepcount++;
            } else {
                System.out.println(name);
            }
        }
        reader.close();
        pw.close();

        Iterator<String> it = scoremap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            double score = scoremap.get(key);
            String seq = seqmap.get(key);
            boolean td = tdmap.get(key);
            System.out.println(td + "\t" + key + "\t" + score + "\t" + seq);
        }
    }

    public static void modValidationT12(String pep, String mgfin,
            String out) throws IOException, DtaFileParsingException
    {

        PrintWriter pw = new PrintWriter(out);

        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        AminoacidModification aam = new AminoacidModification();
        aam.addModification('*', 15.994915, "Oxi");
        int[] itypes = new int[]{Ion.TYPE_B, Ion.TYPE_Y};
        AminoacidFragment aaf = new AminoacidFragment(aas, aam);

        HashMap<String, MS2Scan> map = new HashMap<String, MS2Scan>();
        MgfReader mgfinreader = new MgfReader(mgfin);
        MS2Scan ms2scan = null;
        while ((ms2scan = mgfinreader.getNextMS2Scan()) != null) {
            String name = ms2scan.getScanName().getBaseName();
            map.put(name, ms2scan);
        }
        mgfinreader.close();

        BufferedReader reader = new BufferedReader(new FileReader(pep));
        String[] title = reader.readLine().split("\t");
        int scanid = -1;
        int seqid = -1;
        int rankid = -1;
        int proid = -1;
        int scoreid = -1;
        for (int i = 0; i < title.length; i++) {
            if (title[i].equals("Sequence")) {
                seqid = i;
            } else if (title[i].equals("Rank")) {
                rankid = i;
            } else if (title[i].equals("Scan(s)")) {
                scanid = i;
            } else if (title[i].equals("Proteins")) {
                proid = i;
            } else if (title[i].equals("Ions-score")) {
                scoreid = i;
            }
        }
        int pepcount = 0;

        HashSet<String> usedset = new HashSet<String>();
        HashMap<String, Double> scoremap = new HashMap<String, Double>();
        HashMap<String, Boolean> tdmap = new HashMap<String, Boolean>();
        HashMap<String, String> seqmap = new HashMap<String, String>();
        String line = null;
        while ((line = reader.readLine()) != null) {

            String[] cs = line.split("\t");
            double score = Double.parseDouble(cs[scoreid]);
            if (score < 5)
                continue;

            boolean target = !cs[proid].startsWith("REV");
            StringBuilder sb = new StringBuilder();
            StringBuilder unisb = new StringBuilder();
            ArrayList<Integer> oxilist = new ArrayList<Integer>();
            ArrayList<Integer> glylist = new ArrayList<Integer>();
            for (int i = 0; i < cs[seqid].length(); i++) {
                if (cs[seqid].charAt(i) >= 'A' && cs[seqid].charAt(i) <= 'Z') {
                    sb.append(cs[seqid].charAt(i));
                    if (i >= 2 && i < cs[seqid].length() - 2)
                        unisb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '*') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '.') {
                    sb.append(cs[seqid].charAt(i));
                } else if (cs[seqid].charAt(i) == '-') {
                    sb.append(cs[seqid].charAt(i));
                }
            }

            if (usedset.contains(cs[scanid] + sb))
                continue;

            int count = cs[seqid].length() - sb.length();
            if (count == 0) continue;

            usedset.add(cs[scanid] + sb);
            Ions ions = aaf.fragment(sb.toString(), itypes, true);
            Ion[] bs = ions.getIons(Ion.TYPE_B);
            Ion[] ys = ions.getIons(Ion.TYPE_Y);

//			int modbegin = -1;
//			int modend = -1;
            double[] modscore = new double[bs.length + 1];
            double[] binten = new double[bs.length];
            double[] bglyinten = new double[bs.length];
            double[] yinten = new double[ys.length];
            double[] yglyinten = new double[ys.length];
            String name = cs[scanid].substring(0, cs[scanid].indexOf(","));
            if (map.containsKey(name)) {

                MS2Scan scan = map.get(name);
                IMS2PeakList peaklist = scan.getPeakList();
                PrecursePeak pp = peaklist.getPrecursePeak();
                double mz = pp.getMz();
                short charge = pp.getCharge();
                double baseinten = peaklist.getBasePeak().getIntensity();

                IPeak[] peaks = peaklist.getPeaksSortByIntensity();
                for (int i = 0; i < peaks.length; i++) {

                    double mzi = peaks[i].getMz();
                    double inteni = (peaks.length - i) / (double) peaks.length;
                    if (name.contains("Locus:1.1.1.7667.3")) {
//						System.out.println(mzi+"\t"+inteni);
                    }
                    for (int j = 0; j < bs.length; j++) {
                        double bfragmz = bs[j].getMz();
                        double yfragmz = ys[j].getMz();
                        String bfragseq = bs[j].getFragseq();
                        String yfragseq = ys[j].getFragseq();
                        if (bfragseq.contains("S") || bfragseq.contains("T")) {
                            if (Math.abs(mzi - bfragmz - 656.227614635) < tolerance) {
                                if (inteni > bglyinten[j])
                                    bglyinten[j] = inteni;
                            } else if (Math.abs(mzi - bfragmz - 494.174789635) < tolerance) {
                                if (inteni > bglyinten[j])
                                    bglyinten[j] = inteni;
                            } else if (Math.abs(mzi - bfragmz - 365.132198) < tolerance) {
                                if (inteni > bglyinten[j])
                                    bglyinten[j] = inteni;
                            } else if (Math.abs(mzi - bfragmz - 203.079373) < tolerance) {
                                if (inteni > bglyinten[j])
                                    bglyinten[j] = inteni;
                            } else if (Math.abs(mzi - bfragmz - 656.227614635 + 18.010565) < tolerance) {
                                if (inteni > bglyinten[j])
                                    bglyinten[j] = inteni;
                            } else if (Math.abs(mzi - bfragmz - 494.174789635 + 18.010565) < tolerance) {
                                if (inteni > bglyinten[j])
                                    bglyinten[j] = inteni;
                            } else if (Math.abs(mzi - bfragmz - 365.132198 + 18.010565) < tolerance) {
                                if (inteni > bglyinten[j])
                                    bglyinten[j] = inteni;
                            } else if (Math.abs(mzi - bfragmz - 203.079373 + 18.010565) < tolerance) {
                                if (inteni > bglyinten[j])
                                    bglyinten[j] = inteni;
                            } else if (Math.abs(mzi - bfragmz) < tolerance) {
                                if (inteni > bglyinten[j])
                                    bglyinten[j] = inteni;
                            }
                        } else {
                            if (Math.abs(mzi - bfragmz) < tolerance) {
                                if (inteni > binten[j])
                                    binten[j] = inteni;
                            }
                        }
                        if (yfragseq.contains("S") || yfragseq.contains("T")) {
                            if (Math.abs(mzi - yfragmz - 656.227614635) < tolerance) {
                                if (inteni > yglyinten[j])
                                    yglyinten[j] = inteni;
                            } else if (Math.abs(mzi - yfragmz - 494.174789635) < tolerance) {
                                if (inteni > yglyinten[j])
                                    yglyinten[j] = inteni;
                            } else if (Math.abs(mzi - yfragmz - 365.132198) < tolerance) {
                                if (inteni > yglyinten[j])
                                    yglyinten[j] = inteni;
                            } else if (Math.abs(mzi - yfragmz - 203.079373) < tolerance) {
                                if (inteni > yglyinten[j])
                                    yglyinten[j] = inteni;
                            } else if (Math.abs(mzi - yfragmz - 656.227614635 + 18.010565) < tolerance) {
                                if (inteni > yglyinten[j])
                                    yglyinten[j] = inteni;
                            } else if (Math.abs(mzi - yfragmz - 494.174789635 + 18.010565) < tolerance) {
                                if (inteni > yglyinten[j])
                                    yglyinten[j] = inteni;
                            } else if (Math.abs(mzi - yfragmz - 365.132198 + 18.010565) < tolerance) {
                                if (inteni > yglyinten[j])
                                    yglyinten[j] = inteni;
                            } else if (Math.abs(mzi - yfragmz - 203.079373 + 18.010565) < tolerance) {
                                if (inteni > yglyinten[j])
                                    yglyinten[j] = inteni;
                            } else if (Math.abs(mzi - yfragmz) < tolerance) {
                                if (inteni > yinten[j])
                                    yinten[j] = inteni;
                            }
                        } else {

                            if (Math.abs(mzi - yfragmz) < tolerance) {
                                if (inteni > yinten[j])
                                    yinten[j] = inteni;
                            }
                        }
                    }
                }

                double totalB = 0;
                double totalY = 0;
                int conb = 0;
                for (int i = 0; i < binten.length; i++) {
                    if (binten[i] > 0) {
                        conb++;
                        totalB += (binten[i] + bglyinten[i]);
                        if (bglyinten[i] > 0) {
                            for (int j = 0; j < i + 1; j++) {
                                modscore[j] += bglyinten[j];
                            }
                        }
                    } else {
                        if (bglyinten[i] > 0) {
                            totalB += bglyinten[i] * 0.2;
                            for (int j = 0; j < i + 1; j++) {
                                modscore[j] += bglyinten[j] * 0.2;
                            }
                        }
                    }
                }

                int cony = 0;
                for (int i = 0; i < yinten.length; i++) {
                    if (yinten[i] > 0) {
                        cony++;
                        totalY += (yinten[i] + yglyinten[i]);
                        if (yglyinten[i] > 0) {
                            for (int j = yglyinten.length - i; j < modscore.length; j++) {
                                modscore[j] += yglyinten[j - 1];
                            }
                        }
                    } else {
                        if (yglyinten[i] > 0) {
                            totalY += yglyinten[i] * 0.2;
                            for (int j = yglyinten.length - i; j < modscore.length; j++) {
                                modscore[j] += yglyinten[j - 1] * 0.2;
                            }
                        }
                    }
                }
                totalB = totalB * conb / (double) bs.length / (double) bs.length;
                totalY = totalY * cony / (double) bs.length / (double) bs.length;

                double modtotal = 0;
                double modmax = 0;
                int modsite = -1;
                ArrayList<Integer> sitelist = new ArrayList<Integer>();
                HashMap<Integer, Double> sitemap = new HashMap<Integer, Double>();
                for (int i = 0; i < modscore.length; i++) {
                    if (unisb.charAt(i) == 'S' || unisb.charAt(i) == 'T') {
                        modtotal += modscore[i];
                        sitemap.put(i, modscore[i]);
                    }
                }

                if (totalB == 0 && totalY == 0) continue;

                double kby = totalB < totalY ? 1.0 : totalY / (totalB + totalY);
//				double finalscore = (totalB+totalY)*kby;
                double finalscore = (totalB + totalY);

                if (scoremap.containsKey(cs[scanid])) {
                    if (finalscore > scoremap.get(cs[scanid])) {
                        scoremap.put(cs[scanid], finalscore);
                        tdmap.put(cs[scanid], target);
                        seqmap.put(cs[scanid], sb.toString());
                    }
                } else {
                    scoremap.put(cs[scanid], finalscore);
                    tdmap.put(cs[scanid], target);
                    seqmap.put(cs[scanid], sb.toString());
                }

                if (target && score < 5) {
                    for (int i = 0; i < binten.length; i++) {
//						if(totalB>totalY)
//						System.out.println(cs[scanid]+"\t"+cs[seqid]+"\t"+(i+1)+"\t"+bs[i].getMz()+"\t"+
//								ys[ys.length-i-1].getMz()+"\t"+binten[i]+"\t"+bglyinten[i]+"\t"+
//								yinten[yinten.length-i-1]+"\t"+yglyinten[yinten.length-i-1]+"\t"+totalB+"\t"+totalY+"\t"+finalscore);
//						System.out.println(totalB+"\t"+totalY);
                    }
//					break;
//					System.out.println(cs[scanid]+"\t"+cs[seqid]+"\t"+cs[scoreid]+"\t"+totalB+"\t"+totalY+"\t"+finalscore);
                }
                if (cs[scanid].contains("Locus:1.1.1.7656.4, 0")) {
                    for (int i = 0; i < binten.length; i++) {
//						if(totalB>totalY)
//						System.out.println(cs[scanid]+"\t"+cs[seqid]+"\t"+(i+1)+"\t"+bs[i].getMz()+"\t"+
//								ys[ys.length-i-1].getMz()+"\t"+binten[i]+"\t"+bglyinten[i]+"\t"+
//								yinten[yinten.length-i-1]+"\t"+yglyinten[yinten.length-i-1]+"\t"+totalB+"\t"+totalY+"\t"+finalscore);
//						System.out.println(totalB+"\t"+totalY);
                    }
                }
//				if(pepcount==3)
//					break;

                pepcount++;
            } else {
                System.out.println(name);
            }
        }
        reader.close();
        pw.close();

        Iterator<String> it = scoremap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            double score = scoremap.get(key);
            String seq = seqmap.get(key);
            boolean td = tdmap.get(key);
            System.out.println(td + "\t" + key + "\t" + score + "\t" + seq);
        }
    }

/*	public static void modValidationT2(String pep, String mgfin, String out) throws IOException, DtaFileParsingException{
		
		PrintWriter pw = new PrintWriter(out);
		
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		AminoacidModification aam = new AminoacidModification();
		aam.addModification('*', 15.994915, "Oxi");
		int [] itypes = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		
		HashMap <String, MS2Scan> map = new HashMap <String, MS2Scan>();
		MgfReader mgfinreader = new MgfReader(mgfin);
		MS2Scan ms2scan = null;
		while((ms2scan=mgfinreader.getNextMS2Scan())!=null){
			String name = ms2scan.getScanName().getBaseName();
			map.put(name, ms2scan);
		}
		mgfinreader.close();
		
		BufferedReader reader = new BufferedReader(new FileReader(pep));
		String [] title = reader.readLine().split("\t");
		int scanid = -1;
		int seqid = -1;
		int rankid = -1;
		int proid = -1;
		int scoreid = -1;
		for(int i=0;i<title.length;i++){
			if(title[i].equals("Sequence")){
				seqid = i;
			}else if(title[i].equals("Rank")){
				rankid = i;
			}else if(title[i].equals("Scan(s)")){
				scanid = i;
			}else if(title[i].equals("Proteins")){
				proid = i;
			}else if(title[i].equals("Ions-score")){
				scoreid = i;
			}
		}
		
		HashSet <String> usedset = new HashSet <String>();
		String line = null;
		while((line=reader.readLine())!=null){
			
			String [] cs = line.split("\t");
			if(usedset.contains(cs[scanid]+cs[seqid]))
				continue;
			
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<cs[seqid].length();i++){
				if(cs[seqid].charAt(i)>='A' && cs[seqid].charAt(i)<='Z'){
					sb.append(cs[seqid].charAt(i));
				}else if(cs[seqid].charAt(i)=='*'){
					sb.append(cs[seqid].charAt(i));
				}else if(cs[seqid].charAt(i)=='.'){
					sb.append(cs[seqid].charAt(i));
				}else if(cs[seqid].charAt(i)=='-'){
					sb.append(cs[seqid].charAt(i));
				}
			}
			int count = cs[seqid].length()-sb.length();
			if(count==0) continue;
			
			usedset.add(cs[scanid]+cs[seqid]);
			Ions ions = aaf.fragment(sb.toString(), itypes, true);
			Ion [] bs = ions.getIons(Ion.TYPE_B);
			Ion [] ys = ions.getIons(Ion.TYPE_Y);
			
			for(int i=0;i<bs.length;i++){
				System.out.println(bs[i].getMz()+"\t"+ys[i].getMz());
			}
			
			ArrayList <Double> list = new ArrayList <Double>();
			for(int i=0;i<bs.length;i++){
				list.add(bs[i].getMz());
				list.add(ys[i].getMz());
			}
			
			Double [] ionmz = list.toArray(new Double[list.size()]);
			Arrays.sort(ionmz);
			
			int modbegin = 0;
			int modend = 0;
			String name = cs[scanid].substring(0, cs[scanid].indexOf(","));
			if(map.containsKey(name)){
				
				MS2Scan scan = map.get(name);
				IMS2PeakList peaklist = scan.getPeakList();
				PrecursePeak pp = peaklist.getPrecursePeak();
				double mz = pp.getMz();
				short charge = pp.getCharge();

				IPeak [] peaks = peaklist.getPeakList();
				for(int i=0;i<peaks.length;i++){
					double mzi = peaks[i].getMz();
					double inteni = peaks[i].getIntensity();
					boolean use = true;
					for(int j=0;j<ionmz.length;j++){
						if(Math.abs(mzi-ionmz[j]-656.227614635)<tolerance){
							use = false;
							break;
						}else if(Math.abs(mzi-ionmz[j]-494.174789635)<tolerance){
							use = false;
							break;
						}else if(Math.abs(mzi-ionmz[j]-365.132198)<tolerance){
							use = false;
							break;
						}else if(Math.abs(mzi-ionmz[j]-203.079373)<tolerance){
							use = false;
							break;
						}else if(Math.abs(mzi-ionmz[j]-947.32303127)<tolerance){
							use = false;
							break;
						}else if(Math.abs(mzi-ionmz[j]-656.227614635+18.010565)<tolerance){
							use = false;
							break;
						}else if(Math.abs(mzi-ionmz[j]-494.174789635+18.010565)<tolerance){
							use = false;
							break;
						}else if(Math.abs(mzi-ionmz[j]-365.132198+18.010565)<tolerance){
							use = false;
							break;
						}else if(Math.abs(mzi-ionmz[j]-203.079373+18.010565)<tolerance){
							use = false;
							break;
						}else if(Math.abs(mzi-ionmz[j]-947.32303127+18.010565)<tolerance){
							use = false;
							break;
						}else{
							
						}
					}
				}
				break;
			}else{
				System.out.println(name);
			}
		}
		reader.close();
		pw.close();
	}
*/
}
