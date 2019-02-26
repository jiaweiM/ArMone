package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.SequenceGenerationException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import jxl.JXLException;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ck
 */
public class OGlycanTest5
{
    private static void originalScanCountStat(String in)
    {
        File[] files = (new File(in)).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("mgf")) {

            }
        }
    }

    private static void scanCountStat(String in) throws IOException
    {

        HashMap<String, OGlycanScanInfo2> infomap = new HashMap<String, OGlycanScanInfo2>();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        int totalComposition = 0;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            infomap.put(scanname, info);
            totalComposition += info.getUnits().length;

            if (map.containsKey(oriScanname)) {
                map.put(oriScanname, map.get(oriScanname) + 1);
            } else {
                map.put(oriScanname, 1);
            }
        }
        reader.close();

        int totalScan = 0;
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            totalScan += map.get(key);
        }

        double a1 = (double) totalScan / (double) map.size();
        double a2 = (double) totalComposition / (double) infomap.size();
        System.out.println(a1 + "\t" + a2);
    }

    private static void scanCountStat(String infofile, String xlsfile) throws IOException, JXLException
    {

        HashMap<String, OGlycanScanInfo2> infomap = new HashMap<String, OGlycanScanInfo2>();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        BufferedReader reader = new BufferedReader(new FileReader(infofile));
        String line = null;
        int totalComposition = 0;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            infomap.put(scanname, info);
            totalComposition += info.getUnits().length;

            if (map.containsKey(oriScanname)) {
                map.put(oriScanname, map.get(oriScanname) + 1);
            } else {
                map.put(oriScanname, 1);
            }
        }
        reader.close();

        int totalScan = 0;
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            totalScan += map.get(key);
        }

        int pepcount = 0;
        ExcelReader er = new ExcelReader(xlsfile);
        String[] cs = er.readLine();
        while ((cs = er.readLine()) != null) {
            pepcount++;
        }
        er.close();

        double a1 = (double) totalScan / (double) map.size();
        double a2 = (double) totalComposition / (double) infomap.size();
        double a3 = (double) pepcount / (double) map.size();
//		System.out.println(a1+"\t"+a2+"\t"+a3+"\t"+map.size()+"\t"+pepcount);
        System.out.println(map.size() + "\t" + totalScan + "\t" + pepcount);
    }

    private static void peptideInfoTest(String result, String ppl) throws IOException, JXLException, PeptideParsingException, FileDamageException
    {
        ExcelReader er = new ExcelReader(result);
        HashSet<String> scanset = new HashSet<String>();
        String[] line = er.readLine();
        while ((line = er.readLine()) != null) {
            scanset.add(line[0]);
        }
        er.close();
        System.out.println(scanset.size());
        IPeptideListReader reader = new PeptideListReader(ppl);
        IPeptide peptide = null;
        while ((peptide = reader.getPeptide()) != null) {
            String scan = peptide.getBaseName();
//			System.out.println(scan);
            if (scanset.contains(scan)) {
                System.out.println(peptide.getScanNum() + "\t" + peptide.getSequence() + "\t" + peptide.getDeltaMH() + "\t" + peptide.getDeltaMZppm() + "\t" + peptide.isTP());
            }
        }
        reader.close();
    }

    private static void peptideInfoTest(String in) throws IOException, JXLException, PeptideParsingException, FileDamageException
    {
        File filein = new File(in);
        File[] files = filein.listFiles();
        String ppl = "";
        String result = "";
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("ppl")) {
                ppl = files[i].getAbsolutePath();
            }
            if (files[i].getName().endsWith("xls")) {
                result = files[i].getAbsolutePath();
            }
        }
        ExcelReader er = new ExcelReader(result);
        HashSet<String> scanset = new HashSet<String>();
        String[] line = er.readLine();
        while ((line = er.readLine()) != null) {
            scanset.add(line[0]);
        }
        er.close();
        System.out.println(scanset.size());
        IPeptideListReader reader = new PeptideListReader(ppl);
        IPeptide peptide = null;
        while ((peptide = reader.getPeptide()) != null) {
            String scan = peptide.getBaseName();
//			System.out.println(scan);
            if (scanset.contains(scan)) {
                System.out.println(peptide.getScanNum() + "\t" + peptide.getSequence() + "\t" + peptide.getDeltaMH() + "\t" + peptide.getDeltaMZppm() + "\t" + peptide.isTP());
            }
        }
        reader.close();
    }

    private static void precursorDiffTest(String mgf, String result) throws IOException, JXLException, DtaFileParsingException
    {

        HashMap<String, Double> massmap = new HashMap<String, Double>();
        OGlycanUnit[] units = OGlycanUnit.values();
        ExcelReader reader = new ExcelReader(result);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String scan = line[0].substring(0, line[0].lastIndexOf("."));
//			System.out.println(scan);
            double pepmr = Double.parseDouble(line[1]);
            String[] cs = line[7].split("[\\[\\]]");
            for (int i = 1; i < cs.length; i += 2) {
                for (int j = 0; j < units.length; j++) {
                    if (cs[i].equals(units[j].getComposition())) {
                        pepmr += units[j].getMass();
                        break;
                    }
                }
            }
            massmap.put(scan, pepmr);
        }
        reader.close();
        System.out.println(massmap.size());

        ArrayList<Double> list = new ArrayList<Double>();
        MgfReader mr = new MgfReader(mgf);
        MS2Scan ms2scan = null;
        while ((ms2scan = mr.getNextMS2Scan()) != null) {
            String scanname = ms2scan.getScanName().getBaseName() + "." + (1);
            if (massmap.containsKey(scanname)) {
                int charge = ms2scan.getCharge();
                double mz = ms2scan.getPrecursorMZ();
                double mass = massmap.get(scanname);
                if (charge == 0) {
                    for (int i = 0; i < 10; i++) {
                        double theoryMz = mass / (double) i + AminoAcidProperty.PROTON_W;
                        double ppm = (mz - theoryMz) / theoryMz * 1E6;
                        if (Math.abs(ppm) < 300) {
                            list.add(ppm);
                            break;
                        }
                    }
                } else {
                    double theoryMz = mass / (double) charge + AminoAcidProperty.PROTON_W;
                    double ppm = (mz - theoryMz) / theoryMz * 1E6;
                    list.add(ppm);
                }
            }
        }
        Double[] ppms = list.toArray(new Double[list.size()]);
        Arrays.sort(ppms);
        System.out.println(list.size() + "\t" + MathTool.getMedian(ppms) + "\t" + ppms[0] + "\t" + ppms[ppms.length - 1] + "\t" + (ppms[ppms.length - 1] - ppms[0]));
		/*File[] files = (new File(mgf)).listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if (arg0.getName().endsWith("mgf"))
					return true;

				return false;
			}

		});

		Arrays.sort(files);

		for(int i=0;i<files.length;i++){
			MgfReader mr = new MgfReader(files[i]);
			MS2Scan ms2scan = null;
			while ((ms2scan = mr.getNextMS2Scan()) != null) {
				String scanname = ms2scan.getScanName().getScanName() + "." + (i+1);
				System.out.println(scanname);
				if(massmap.containsKey(scanname)){
					int charge = ms2scan.getCharge();
					double mz = ms2scan.getPrecursorMZ();
					double mass = massmap.get(scanname);

					double theoryMz = mass/(double) charge+AminoAcidProperty.PROTON_W;
				}
			}
			reader.close();
		}*/
    }

    private static void caocaocao(String c1, String c2) throws IOException, JXLException
    {
        ExcelReader r1 = new ExcelReader(c1, 0);
        ExcelReader r2 = new ExcelReader(c2, 0);
        HashSet<String> s1 = new HashSet<String>();
        HashSet<String> s2 = new HashSet<String>();
        String[] line = r1.readLine();
        while ((line = r1.readLine()) != null) {
            String scan = line[0].substring(0, line[0].lastIndexOf("."));
            s1.add(scan);
        }
        r1.close();
        line = r2.readLine();
        while ((line = r2.readLine()) != null) {
            String scan = line[0].substring(0, line[0].lastIndexOf("."));
            s2.add(scan);
        }
        r2.close();
        HashSet<String> set = new HashSet<String>();
        set.addAll(s1);
        set.addAll(s2);
        System.out.println(s1.size() + "\t" + s2.size() + "\t" + set.size());
    }

    private static void comparePPL(String ppl, String xls) throws IOException, JXLException, FileDamageException
    {
        ExcelReader reader = new ExcelReader(xls, 1);
        HashSet<String> set = new HashSet<String>();
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            set.add(line[0]);
        }
        reader.close();

        int[] count = new int[2];
        PeptideListReader pr = new PeptideListReader(ppl);
        IPeptide peptide;
        while ((peptide = pr.getPeptide()) != null) {
            String basename = peptide.getBaseName();
            basename = basename.substring(0, basename.lastIndexOf("."));
            if (set.contains(basename)) {
                count[0]++;
            } else {
                count[1]++;
            }
        }
        pr.close();
        System.out.println(set.size() + "\t" + count[0] + "\t" + count[1]);
    }

    private static void typeCount(String in, int sheet) throws IOException, JXLException
    {
        int[] count = new int[15];
        ExcelReader reader = new ExcelReader(in, sheet);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            int tc = 0;
            for (int i = 3; i < line.length; i++) {
                int cc = Integer.parseInt(line[i]);
                if (cc > 0) {
                    count[i - 3] += cc;
                }
            }
//			count[tc-1]++;
//			if(tc>6) System.out.println(tc+"\t"+line[0]+"\t"+line[1]+"\t"+line[2]);
        }
        reader.close();

        int total = MathTool.getTotal(count);
        for (int i = 0; i < count.length; i++) {
            System.out.println((i + 1) + "\t" + count[i] + "\t" + (double) count[i] / (double) total);
        }
    }

    private static void heteroCount(String in, int sheet) throws IOException, JXLException
    {
        int[] count = new int[15];
        ExcelReader reader = new ExcelReader(in, sheet);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            int tc = 0;
            for (int i = 3; i < line.length; i++) {
                int cc = Integer.parseInt(line[i]);
                if (cc > 0) {
                    tc++;
                }
            }
            count[tc - 1]++;
            if (tc > 6) System.out.println(tc + "\t" + line[0] + "\t" + line[1] + "\t" + line[2]);
        }
        reader.close();

        int total = MathTool.getTotal(count);
        for (int i = 0; i < count.length; i++) {
            System.out.println((i + 1) + "\t" + count[i] + "\t" + (double) count[i] / (double) total);
        }
    }

    private static void heteroCount2(String in, int sheet) throws IOException, JXLException
    {

        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        ExcelReader reader = new ExcelReader(in, sheet);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String key = line[0] + "\t" + line[4];
            if (map.containsKey(key)) {
                map.get(key).add(line[2]);
            } else {
                HashSet<String> set = new HashSet<String>();
                set.add(line[2]);
                map.put(key, set);
            }
        }
        reader.close();

        int[] count = new int[14];
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            HashSet<String> set = map.get(key);
            count[set.size() - 1]++;
            if (set.size() >= 6) {
                System.out.println(set.size() + "\t" + key);
            }
        }

        int total = MathTool.getTotal(count);
        for (int i = 0; i < count.length; i++) {
            System.out.println((i + 1) + "\t" + count[i] + "\t" + (double) count[i] / (double) total);
        }
    }

    private static void heteroCount3(String in, double formthres, double sitethres) throws IOException, JXLException
    {

        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        ExcelReader reader = new ExcelReader(in, 2);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            double formscore = Double.parseDouble(line[4]);
            double sitescore = Double.parseDouble(line[5]);
            if (formscore < formthres || sitescore < sitethres) continue;

            String key = line[0] + "\t" + line[7];
            if (map.containsKey(key)) {
                map.get(key).add(line[2]);
            } else {
                HashSet<String> set = new HashSet<String>();
                set.add(line[2]);
                map.put(key, set);
            }
        }
        reader.close();

        int[] count = new int[14];
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            HashSet<String> set = map.get(key);
            count[set.size() - 1]++;
            if (set.size() >= 6) {
                System.out.println(set.size() + "\t" + key);
            }
        }

        int total = MathTool.getTotal(count);
        for (int i = 0; i < count.length; i++) {
            System.out.println((i + 1) + "\t" + count[i] + "\t" + (double) count[i] / (double) total);
        }
    }

    private static void abundanceCompare(String normal, String HCC, int leastCount) throws IOException, JXLException
    {
        HashMap<String, Integer> map1 = new HashMap<String, Integer>();
        ExcelReader reader1 = new ExcelReader(normal, 1);
        String[] line = reader1.readLine();
        String[] glycans = new String[15];
        System.arraycopy(line, 3, glycans, 0, 15);
        while ((line = reader1.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            int[] count = new int[15];
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
                if (count[i] >= leastCount) {
                    String key = ref + "    " + line[0] + "    " + glycans[i];
                    map1.put(key, count[i]);
                }
            }
        }
        reader1.close();

        HashMap<String, Integer> map2 = new HashMap<String, Integer>();
        ExcelReader reader2 = new ExcelReader(HCC, 1);
        line = reader2.readLine();
        while ((line = reader2.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            int[] count = new int[15];
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
                if (count[i] >= leastCount) {
                    String key = ref + "    " + line[0] + "    " + glycans[i];
                    map2.put(key, count[i]);
                }
            }
        }
        reader2.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(map1.keySet());
        set.addAll(map2.keySet());
        System.out.println(map1.size() + "\t" + map2.size() + "\t" + set.size());

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (map1.containsKey(key) && map2.containsKey(key)) {
                int c1 = map1.get(key);
                int c2 = map2.get(key);
                double n1 = (double) c1 / 3957.0;
                double n2 = (double) c2 / 3458.0;
                System.out.println(key + "\t" + c1 + "\t" + c2 + "\t" + n1 + "\t" + n2 + "\t" + n1 / (n1 + n2) + "\t" + n2 / (n1 + n2));
            } else if (map1.containsKey(key)) {
                int c1 = map1.get(key);
                int c2 = 0;
                double n1 = (double) c1 / 3957.0;
                double n2 = (double) c2 / 3458.0;
                System.out.println(key + "\t" + c1 + "\t" + c2 + "\t" + n1 + "\t" + n2 + "\t" + "1.0\t0.0");
            } else if (map2.containsKey(key)) {
                int c1 = 0;
                int c2 = map2.get(key);
                double n1 = (double) c1 / 3957.0;
                double n2 = (double) c2 / 3458.0;
                System.out.println(key + "\t" + c1 + "\t" + c2 + "\t" + n1 + "\t" + n2 + "\t" + "0.0\t1.0");
            }
        }
    }

    private static void occupacyCompare(String normal, String HCC, int leastCount) throws IOException, JXLException
    {
        HashMap<String, Double> map1 = new HashMap<String, Double>();
        ExcelReader reader1 = new ExcelReader(normal, 1);
        String[] line = reader1.readLine();
        String[] glycans = new String[15];
        System.arraycopy(line, 3, glycans, 0, 15);
        while ((line = reader1.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            int[] count = new int[15];
            int total = 0;
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
                total += count[i];
            }
            for (int i = 0; i < count.length; i++) {
                if (count[i] >= leastCount) {
                    String key = ref + "    " + line[0] + "    " + glycans[i];
                    map1.put(key, (double) count[i] / (double) total);
                }
            }
        }
        reader1.close();

        HashMap<String, Double> map2 = new HashMap<String, Double>();
        ExcelReader reader2 = new ExcelReader(HCC, 1);
        line = reader2.readLine();
        while ((line = reader2.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            int[] count = new int[15];
            int total = MathTool.getTotal(count);
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
                total += count[i];
            }
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
                if (count[i] >= leastCount) {
                    String key = ref + "    " + line[0] + "    " + glycans[i];
                    map2.put(key, (double) count[i] / (double) total);
                }
            }
        }
        reader2.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(map1.keySet());
        set.addAll(map2.keySet());
        System.out.println(map1.size() + "\t" + map2.size() + "\t" + set.size());

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (map1.containsKey(key) && map2.containsKey(key)) {
                double c1 = map1.get(key);
                double c2 = map2.get(key);
                System.out.println(key + "\t" + c1 + "\t" + c2 + "\t" + c1 / (c1 + c2) + "\t" + c2 / (c1 + c2));
            } else if (map1.containsKey(key)) {
                double c1 = map1.get(key);
                double c2 = 0;
                System.out.println(key + "\t" + c1 + "\t" + c2 + "\t" + "1.0\t0.0");
            } else if (map2.containsKey(key)) {
                double c1 = 0;
                double c2 = map2.get(key);
                System.out.println(key + "\t" + c1 + "\t" + c2 + "\t" + "0.0\t1.0");
            }
        }
    }

    private static void uniqueGlycopeptideCompare(String[] ETHCD, String tof) throws IOException, JXLException
    {
        HashSet<String>[] ehset = new HashSet[ETHCD.length];
        for (int i = 0; i < ehset.length; i++) {
            ehset[i] = new HashSet<String>();
        }
        for (int i = 0; i < ETHCD.length; i++) {
            ExcelReader ehreader = new ExcelReader(ETHCD[i]);
            String[] line = ehreader.readLine();
            while ((line = ehreader.readLine()) != null) {
                String seq = line[6];
                char[] cs = seq.toCharArray();
                StringBuilder sb = new StringBuilder();
                boolean b = true;
                for (int j = 0; j < cs.length; j++) {
                    if (cs[j] == '[') {
                        sb.append("#");
                        b = false;
                    } else if (cs[j] == ']') {
                        b = true;
                    } else {
                        if (b) {
                            sb.append(cs[j]);
                        }
                    }
                }
//				ehset.add(line[6]);
                ehset[i].add(sb.toString());
            }
            ehreader.close();
        }

        HashSet<String> tofset = new HashSet<String>();
        ExcelReader tofreader = new ExcelReader(tof);
        String[] line = tofreader.readLine();
        while ((line = tofreader.readLine()) != null) {
            String seq = line[6];
            char[] cs = seq.toCharArray();
            StringBuilder sb = new StringBuilder();
            boolean b = true;
            for (int j = 0; j < cs.length; j++) {
                if (cs[j] == '[') {
                    sb.append("#");
                    b = false;
                } else if (cs[j] == ']') {
                    b = true;
                } else {
                    if (b) {
                        sb.append(cs[j]);
                    }
                }
            }
//			tofset.add(line[6]);
            tofset.add(sb.toString());
        }
        tofreader.close();

        for (int i = 0; i < ehset.length; i++)
            System.out.println(ehset[i].size() + "\t" + tofset.size());

        int count = 0;
        Iterator<String> it = tofset.iterator();
        while (it.hasNext()) {
            String seq = it.next();
            boolean cont = false;
            for (int i = 0; i < ehset.length; i++) {
                if (ehset[i].contains(seq)) {
                    System.out.println(seq);
                    cont = true;
                }
            }
            if (cont) {
                count++;
            }
        }
        System.out.println(count + "\t" + (double) count / (double) tofset.size());
    }

    private static void getSequenceWindow(String[] files, String glycan, int sheet) throws IOException, JXLException
    {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < files.length; i++) {
            ExcelReader reader = new ExcelReader(files[i], sheet);
            String[] line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (glycan.equals("all") || glycan.equals(line[2])) {
                    set.add(line[1]);
                }
            }
            reader.close();
        }

        for (String s : set) {
            System.out.println(s);
        }
    }

    private static void getSequenceWindow2(String file, String glycan, int sheet) throws IOException, JXLException
    {
        ArrayList<String> list = new ArrayList<String>();
        ExcelReader reader = new ExcelReader(file, sheet);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (glycan.equals("all") || glycan.equals(line[2])) {
                list.add(line[1]);
            }
        }
        reader.close();


        for (String s : list) {
            System.out.println(s);
        }
    }

    private static void getSequenceWindowFromDatabase(String database, int seqcount) throws IOException, JXLException
    {
        int[][] count = new int[15][26];
        FastaReader fr = new FastaReader(database);
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            String seq = ps.getUniqueSequence();
            for (int i = 0; i < seq.length(); i++) {
                char aa = seq.charAt(i);
                if (aa == 'S' || aa == 'T') {
                    for (int j = i; j >= i - 7; j--) {
                        if (j >= 0) {
                            char aaj = seq.charAt(j);
                            count[7 + j - i][aaj - 'A']++;
                        }
                    }
                    for (int j = i + 1; j <= i + 7; j++) {
                        if (j < seq.length()) {
                            char aaj = seq.charAt(j);
                            count[7 + j - i][aaj - 'A']++;
                        }
                    }
                }
            }
        }
        fr.close();

        int[][] normalCount = new int[15][26];
        for (int i = 0; i < count.length; i++) {
            int totali = MathTool.getTotal(count[i]);
            for (int j = 0; j < count[i].length; j++) {
                normalCount[i][j] = (int) ((double) count[i][j] / (double) totali * (double) seqcount);
            }
        }

        int[] aaid = new int[15];
        Arrays.fill(aaid, 0);
        int[] currentid = new int[15];
        Arrays.fill(currentid, 0);
        String[] windows = new String[seqcount];
        for (int i = 0; i < windows.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < aaid.length; j++) {
                if (currentid[j] < normalCount[j][aaid[j]]) {
                    sb.append((char) ('A' + aaid[j]));
                    currentid[j]++;
                } else {
                    if (aaid[j] < normalCount[j].length - 1) {
                        boolean add = false;
                        while (aaid[j] < normalCount[j].length - 1) {
                            aaid[j]++;
                            currentid[j] = 0;
                            if (currentid[j] < normalCount[j][aaid[j]]) {
                                sb.append((char) ('A' + aaid[j]));
                                currentid[j]++;
                                add = true;
                                break;
                            }
                        }
                        if (!add) {
                            sb.append("_");
                        }
                    } else {
                        sb.append("_");
                    }
                }
            }
            System.out.println(sb);
        }
    }

    private static void spcountTest(String in, String out, String fasta) throws IOException, JXLException
    {
        HashMap<String, Integer> imap0 = new HashMap<String, Integer>();
        HashMap<String, String[]> cmap0 = new HashMap<String, String[]>();
        HashMap<String, Double> dmap0 = new HashMap<String, Double>();
        HashMap<String, Integer> imap1 = new HashMap<String, Integer>();
        HashMap<String, String[]> cmap1 = new HashMap<String, String[]>();
        HashMap<String, Double> dmap1 = new HashMap<String, Double>();
        ExcelReader reader = new ExcelReader(in, new int[]{0, 1});
        String[] line = reader.readLine(0);
        while ((line = reader.readLine(0)) != null) {
            double score = Double.parseDouble(line[2]);
            String seq = line[6];
            StringBuilder sb = new StringBuilder();
            boolean bb = true;
            for (int i = 0; i < seq.length(); i++) {
                if (seq.charAt(i) == '[') {
                    bb = false;
                } else if (seq.charAt(i) == ']') {
                    bb = true;
                } else {
                    if (bb) {
                        sb.append(seq.charAt(i));
                    }
                }
            }
            String uniseq = sb.toString();
            if (imap0.containsKey(uniseq)) {
                imap0.put(uniseq, imap0.get(uniseq) + 1);
                if (score > dmap0.get(uniseq)) {
                    cmap0.put(uniseq, line);
                    dmap0.put(uniseq, score);
                }
            } else {
                imap0.put(uniseq, 1);
                cmap0.put(uniseq, line);
                dmap0.put(uniseq, score);
            }
            if (imap1.containsKey(seq)) {
                imap1.put(seq, imap1.get(seq) + 1);
                if (score > dmap1.get(seq)) {
                    cmap1.put(seq, line);
                    dmap1.put(seq, score);
                }
            } else {
                imap1.put(seq, 1);
                cmap1.put(seq, line);
                dmap1.put(seq, score);
            }
        }

        HashMap<String, Integer> imap2 = new HashMap<String, Integer>();
        HashMap<String, Integer> imap2a = new HashMap<String, Integer>();
        HashMap<String, String[]> cmap2 = new HashMap<String, String[]>();
        HashMap<String, Double> dmap2 = new HashMap<String, Double>();
        HashMap<String, Integer> imap3 = new HashMap<String, Integer>();
        HashMap<String, Integer> imap3a = new HashMap<String, Integer>();
        HashMap<String, String[]> cmap3 = new HashMap<String, String[]>();
        HashMap<String, Double> dmap3 = new HashMap<String, Double>();
        HashMap<String, Integer> gmap = new HashMap<String, Integer>();

        line = reader.readLine(1);
        while ((line = reader.readLine(1)) != null) {
            String glycan = line[2];
            if (gmap.containsKey(glycan)) {
                gmap.put(glycan, gmap.get(glycan) + 1);
            } else {
                gmap.put(glycan, 1);
            }
            double score = Double.parseDouble(line[4]);
            String key1 = line[0] + "\t" + line[6];
            String key2 = line[0] + "\t" + line[2] + "\t" + line[6];
            if (imap2.containsKey(key1)) {
                imap2.put(key1, imap2.get(key1) + 1);
                if (score > dmap2.get(key1)) {
                    cmap2.put(key1, line);
                    dmap2.put(key1, score);
                }
                if (score >= 0.6) {
                    if (imap2a.containsKey(key1)) {
                        imap2a.put(key1, imap2a.get(key1) + 1);
                    } else {
                        imap2a.put(key1, 1);
                    }
                }
            } else {
                imap2.put(key1, 1);
                cmap2.put(key1, line);
                dmap2.put(key1, score);
                if (score >= 0.6) {
                    imap2a.put(key1, 1);
                } else {
                    imap2a.put(key1, 0);
                }
            }
            if (imap3.containsKey(key2)) {
                imap3.put(key2, imap3.get(key2) + 1);
                if (score > dmap3.get(key2)) {
                    cmap3.put(key2, line);
                    dmap3.put(key2, score);
                }
                if (score >= 0.6) {
                    if (imap3a.containsKey(key2)) {
                        imap3a.put(key2, imap3a.get(key2) + 1);
                    } else {
                        imap3a.put(key2, 1);
                    }
                }
            } else {
                imap3.put(key2, 1);
                cmap3.put(key2, line);
                dmap3.put(key2, score);
                if (score >= 0.6) {
                    imap3a.put(key2, 1);
                } else {
                    imap3a.put(key2, 0);
                }
            }
        }
        reader.close();

        for (String glycan : gmap.keySet()) {
            System.out.println(glycan + "\t" + gmap.get(glycan));
        }

        HashMap<String, ProteinSequence> psmap = new HashMap<String, ProteinSequence>();
        FastaReader fr = new FastaReader(fasta);
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            psmap.put(ps.getReference(), ps);
        }
        fr.close();

        ExcelWriter writer = new ExcelWriter(out, new String[]{"Deglycosylated peptides", "glycopeptides", "glycosylation sites", "site-specific glycans"});
        ExcelFormat format = ExcelFormat.normalFormat;
        String title0 = "Sequence\tIon score\tBegin\tEnd\tLength\tProtein\tCount";
        String title1 = "Sequence\tMW\tIon score\tLength\tProtein\tGlycans\tCount";
        String title2 = "Site\tSequence window\tScore\tProtein\tSequence\tCount";
        String title3 = "Site\tSequence window\tGlycan\tGlycan mass\tScore\tProtein\tSequence\tCount";
        writer.addTitle(title0, 0, format);
        writer.addTitle(title1, 1, format);
        writer.addTitle(title2, 2, format);
        writer.addTitle(title3, 3, format);
        for (String key : cmap0.keySet()) {
            String[] content = cmap0.get(key);
            ProteinSequence proseq = psmap.get(content[5]);
            int begin = proseq.indexOf(key) + 1;
            int end = begin + key.length() - 1;
            StringBuilder sb = new StringBuilder();
            sb.append(key).append("\t");
            sb.append(content[2]).append("\t");
            sb.append(begin).append("\t");
            sb.append(end).append("\t");
            sb.append(content[3]).append("\t");
            sb.append(content[5]).append("\t");
            sb.append(imap0.get(key)).append("\t");
            writer.addContent(sb.toString(), 0, format);
        }
        for (String key : cmap1.keySet()) {
            String[] content = cmap1.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(key).append("\t");
            sb.append(content[1]).append("\t");
            sb.append(content[2]).append("\t");
            sb.append(content[3]).append("\t");
            sb.append(content[5]).append("\t");
            sb.append(content[8]).append("\t");
            sb.append(imap1.get(key)).append("\t");
            writer.addContent(sb.toString(), 1, format);
        }
        for (String key : cmap2.keySet()) {
            String[] content = cmap2.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(content[0]).append("\t");
            sb.append(content[1]).append("\t");
            sb.append(content[4]).append("\t");
            sb.append(content[6]).append("\t");
            sb.append(content[8]).append("\t");
            sb.append(imap2.get(key)).append("\t");
            sb.append(imap2a.get(key)).append("\t");
            writer.addContent(sb.toString(), 2, format);
        }
        for (String key : cmap3.keySet()) {
            String[] content = cmap3.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(content[0]).append("\t");
            sb.append(content[1]).append("\t");
            sb.append(content[2]).append("\t");
            sb.append(content[3]).append("\t");
            sb.append(content[4]).append("\t");
            sb.append(content[6]).append("\t");
            sb.append(content[8]).append("\t");
            sb.append(imap3.get(key)).append("\t");
            sb.append(imap3a.get(key)).append("\t");
            writer.addContent(sb.toString(), 3, format);
        }
        writer.close();
    }

    private static void spcountSite(String[] in, String out, double formThres) throws IOException, JXLException
    {

        double siteThres = 0.6;
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

    private static void compareInfo(String info1, String info2) throws IOException
    {
        HashMap<String, HashSet<OGlycanScanInfo2>> map1 = new HashMap<String, HashSet<OGlycanScanInfo2>>();
        BufferedReader reader1 = new BufferedReader(new FileReader(info1));
        String line = null;
        while ((line = reader1.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            if (map1.containsKey(oriScanname)) {
                map1.get(oriScanname).add(info);
            } else {
                HashSet<OGlycanScanInfo2> set = new HashSet<OGlycanScanInfo2>();
                set.add(info);
                map1.put(oriScanname, set);
            }
        }
        reader1.close();

        HashMap<String, HashSet<OGlycanScanInfo2>> map2 = new HashMap<String, HashSet<OGlycanScanInfo2>>();
        BufferedReader reader2 = new BufferedReader(new FileReader(info2));
        while ((line = reader2.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            if (map2.containsKey(oriScanname)) {
                map2.get(oriScanname).add(info);
            } else {
                HashSet<OGlycanScanInfo2> set = new HashSet<OGlycanScanInfo2>();
                set.add(info);
                map2.put(oriScanname, set);
            }
        }
        reader2.close();
        int[] count = new int[2];
        System.out.println(map1.size() + "\t" + map2.size());
        Iterator<String> it = map1.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            HashSet<OGlycanScanInfo2> set1 = map1.get(key);
            HashSet<OGlycanScanInfo2> set2 = map2.get(key);
            if (!map2.containsKey(key)) {
                System.out.println(key);
            }
        }
//		System.out.println(count[0]+"\t"+count[1]);
    }

    private static void cao(String in, String out) throws IOException
    {
        PrintWriter writer = new PrintWriter(out);
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] cs = line.split("[ ]+");
            StringBuilder sb = new StringBuilder();
            sb.append(cs[2]).append("      ");
            sb.append(cs[1]).append("      ");
            sb.append(cs[0]).append("\n");
            writer.write(sb.toString());
        }
        reader.close();
        writer.close();
    }

    private static void countUnambigurous(String in) throws IOException, JXLException
    {
        int count = 0;
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String sequence = line[7];
            double ps = Double.parseDouble(line[9]);
            boolean b = ps >= 0.2;
            if (!b) continue;

            String[] cs = sequence.split("[\\[\\]]");
            for (int i = 1; i < cs.length; i += 2) {
                double s = Double.parseDouble(cs[i]);
                if (s < 0.6) {
                    b = false;
                    break;
                }
            }
            if (b) {
                count++;
            }
        }
        reader.close();
        System.out.println(count);
    }

    private static void siteAndGlycoCount(String in) throws IOException, JXLException
    {
        int total = 0;
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String key1 = line[6] + "\t" + line[0];
            String key2 = line[6] + "\t" + line[0] + "\t" + line[2];
        }
        reader.close();
    }

    private static void compareFigure3(String in) throws IOException, JXLException
    {
        DecimalFormat df2 = DecimalFormats.DF0_2;
        ExcelReader reader = new ExcelReader(in, 2);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            double d1 = Double.parseDouble(line[4]);
            double d2 = Double.parseDouble(line[5]);
            double p1 = d1 / (d1 + d2);
            double p2 = d2 / (d1 + d2);
            String ref = line[3].split("\\|")[1];
            String key0 = ref + "        " + line[0] + "        " + line[2] + "\t";
            String key = line[2] + "        " + line[0] + "        " + ref + "\t";
            System.out.println(key0 + key + df2.format(p1) + "\t" + df2.format(p2));
//			System.out.println(key+df2.format(d1)+"\t"+df2.format(d2));
        }
        reader.close();
    }

    private static void comparePeptide(String normal, String HCC, String seq) throws IOException, JXLException
    {
        ExcelReader reader1 = new ExcelReader(normal, 1);
        ExcelReader reader2 = new ExcelReader(HCC, 1);
        String[] line = reader1.readLine();
        while ((line = reader1.readLine()) != null) {
            String modseq = line[6];
        }
        reader1.close();
    }

    private static void select(String in, String ref, String site, int sheet) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(in, sheet);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            boolean choose = true;
            if (!ref.equals("")) {
                if (!line[5].contains(ref)) {
                    choose = false;
                }
            }
            if (!site.equals("")) {
                if (!line[0].equals(site)) {
                    choose = false;
                }
            }
            if (choose) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < line.length; i++) {
                    sb.append(line[i]).append("\t");
                }
                System.out.println(sb);
            }
        }
        reader.close();
    }

    private static void select(String in, String ref, String site, int sheet, double formthres, double sitethres) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(in, sheet);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            boolean choose = true;
            if (!ref.equals("")) {
                if (!line[5].contains(ref)) {
                    choose = false;
                }
            }
            if (!site.equals("")) {
                if (!line[0].equals(site)) {
                    choose = false;
                }
            }
            double formscore = Double.parseDouble(line[3]);
            double sitescore = Double.parseDouble(line[4]);
            if (formscore < formthres) {
                choose = false;
            }
            if (sitescore < sitethres) {
                choose = false;
            }
            if (choose) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < line.length; i++) {
                    sb.append(line[i]).append("\t");
                }
                System.out.println(sb);
            }
        }
        reader.close();
    }

    private static void selectPep(String in, String ref, String glycan, double formthres) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(in, 1);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            boolean choose = true;
            if (!ref.equals("")) {
                if (!line[6].contains(ref)) {
                    choose = false;
                }
            }
            if (!glycan.equals("")) {
                if (!line[7].contains(glycan)) {
                    choose = false;
                }
            }
            double formscore = Double.parseDouble(line[10]);
            if (formscore < formthres) {
                choose = false;
            }

            if (choose) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < line.length; i++) {
                    sb.append(line[i]).append("\t");
                }
                System.out.println(sb);
            }
        }
        reader.close();
    }

    private static void uniprotTest(String uniprot, String in) throws IOException, JXLException
    {
        HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
        ExcelReader ur = new ExcelReader(uniprot);
        String[] line = ur.readLine();
        while ((line = ur.readLine()) != null) {
            String[] cs = line[2].split(";");
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < cs.length; i++) {
                int loc = Integer.parseInt(cs[i]);
                list.add(loc);
            }
            map.put(line[0], list);
        }
        ur.close();

        int total1 = 0;
        int total2 = 0;

        ExcelReader reader = new ExcelReader(in, 4);
        line = reader.readLine();
        int[] sitecount = new int[6];
        int[] speccount = new int[6];
        while ((line = reader.readLine()) != null) {
            total1++;
            total2 += Integer.parseInt(line[5]);
            String ref = line[4].split("\\|")[1];
            int site = Integer.parseInt(line[0].substring(1));
            int dis = Integer.MAX_VALUE;
            if (map.containsKey(ref)) {
                ArrayList<Integer> list = map.get(ref);
                for (int i = 0; i < list.size(); i++) {
                    int dd = Math.abs(list.get(i) - site);
                    if (dd < dis) {
                        dis = dd;
                    }
                }
                if (dis == 0) {
                    for (int i = 0; i < line.length; i++)
                        System.out.print(line[i] + "\t");
                    System.out.println();
                    for (int i = 0; i < sitecount.length; i++) {
                        sitecount[i]++;
                        speccount[i] += Integer.parseInt(line[5]);
                    }
                } else if (dis > 0 && dis <= 3) {
                    for (int i = 1; i < sitecount.length; i++) {
                        sitecount[i]++;
                        speccount[i] += Integer.parseInt(line[5]);
                    }
                } else if (dis > 3 && dis <= 5) {
                    for (int i = 2; i < sitecount.length; i++) {
                        sitecount[i]++;
                        speccount[i] += Integer.parseInt(line[5]);
                    }
                } else if (dis > 5 && dis <= 10) {
                    for (int i = 3; i < sitecount.length; i++) {
                        sitecount[i]++;
                        speccount[i] += Integer.parseInt(line[5]);
                    }
                } else if (dis > 10 && dis <= 15) {
                    for (int i = 4; i < sitecount.length; i++) {
                        sitecount[i]++;
                        speccount[i] += Integer.parseInt(line[5]);
                    }
                } else {
                    for (int i = 5; i < sitecount.length; i++) {
                        sitecount[i]++;
                        speccount[i] += Integer.parseInt(line[5]);
                    }
                }
            }
        }
        reader.close();

        DecimalFormat df2 = DecimalFormats.DF0_2;

        double[] p1 = new double[sitecount.length];
        double[] p2 = new double[sitecount.length];
        for (int i = 0; i < sitecount.length; i++) {
            p1[i] = Double.parseDouble(df2.format((double) sitecount[i] / (double) total1));
            p2[i] = Double.parseDouble(df2.format((double) speccount[i] / (double) total2));
        }

        System.out.println(total1 + "\tsite\t" + sitecount[0] + "\t" + sitecount[1] + "\t" + sitecount[2] + "\t" + sitecount[3] + "\t" + sitecount[4] + "\t" + sitecount[5]);
        System.out.println(total1 + "\tsite\t" + p1[0] + "\t" + p1[1] + "\t" + p1[2] + "\t" + p1[3] + "\t" + p1[4] + "\t" + p1[5]);
        System.out.println(total2 + "\tspectra\t" + speccount[0] + "\t" + speccount[1] + "\t" + speccount[2] + "\t" + speccount[3] + "\t" + speccount[4] + "\t" + speccount[5]);
        System.out.println(total2 + "\tspectra\t" + p2[0] + "\t" + p2[1] + "\t" + p2[2] + "\t" + p2[3] + "\t" + p2[4] + "\t" + p2[5]);
    }

    private static void uniprotTest(String uniprot, String in, String fasta) throws IOException, JXLException, SequenceGenerationException
    {
        HashMap<String, ProteinSequence> psmap = new HashMap<String, ProteinSequence>();
        FastaReader fr = new FastaReader(fasta);
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            String ref = ps.getReference();
            ref = ref.split("\\|")[1];
            psmap.put(ref, ps);
        }
        fr.close();

        HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
        HashMap<String, HashSet<String>> windowmap = new HashMap<String, HashSet<String>>();
        ExcelReader ur = new ExcelReader(uniprot);
        String[] line = ur.readLine();
        while ((line = ur.readLine()) != null) {
            String[] cs = line[2].split(";");
            ArrayList<Integer> list = new ArrayList<Integer>();
            String ref = line[0];
            if (ref.contains("-")) {
                ref = ref.substring(0, ref.indexOf("-"));
            }
            ProteinSequence proseq = psmap.get(ref);
            HashSet<String> set = new HashSet<String>();

            for (int i = 0; i < cs.length; i++) {
                int loc = Integer.parseInt(cs[i]);
                list.add(loc);
                String window = proseq.getSeqAround(loc - 1, 7);
                set.add(window);
            }
            map.put(line[0], list);
            windowmap.put(line[0], set);
        }
        ur.close();

        ExcelReader reader = new ExcelReader(in, 1);
        line = reader.readLine();
        int[] sitecount = new int[2];
        int[] speccount = new int[2];
        while ((line = reader.readLine()) != null) {
            String ref = line[4].split("\\|")[1];
            int site = Integer.parseInt(line[0].substring(1));
            int dis = Integer.MAX_VALUE;
            if (map.containsKey(ref)) {
                HashSet<String> set = windowmap.get(ref);
                if (set.contains(line[1])) {
                    sitecount[0]++;
                    speccount[0] += Integer.parseInt(line[5]);
                } else {
                    sitecount[1]++;
                    speccount[1] += Integer.parseInt(line[5]);
                }
			/*
				ArrayList<Integer>list = map.get(ref);
				for(int i=0;i<list.size();i++){
					int dd = Math.abs(list.get(i)-site);
					if(dd<dis){
						dis = dd;
					}
				}
				if(dis==0){
					sitecount[0]++;
					speccount[0]+=Integer.parseInt(line[5]);
				}else{
					sitecount[1]++;
					speccount[1]+=Integer.parseInt(line[5]);
				}
			*/
            }
        }
        reader.close();
        System.out.println("site\t" + sitecount[0] + "\t" + sitecount[1]);
        System.out.println("spectra\t" + speccount[0] + "\t" + speccount[1]);
    }

    private static void uniprotTestProtein(String uniprot, String in, String fasta) throws IOException, JXLException, SequenceGenerationException
    {
        HashMap<String, ProteinSequence> psmap = new HashMap<String, ProteinSequence>();
        FastaReader fr = new FastaReader(fasta);
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            String ref = ps.getReference();
            ref = ref.split("\\|")[1];
            psmap.put(ref, ps);
        }
        fr.close();

        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        HashMap<String, HashSet<String>> windowmap = new HashMap<String, HashSet<String>>();
        ExcelReader ur = new ExcelReader(uniprot);
        String[] line = ur.readLine();
        while ((line = ur.readLine()) != null) {
            String[] cs = line[2].split(";");
            ArrayList<String> list = new ArrayList<String>();
            String ref = line[0];
            if (ref.contains("-")) {
                ref = ref.substring(0, ref.indexOf("-"));
            }
            ProteinSequence proseq = psmap.get(ref);
            HashSet<String> set = new HashSet<String>();

            for (int i = 0; i < cs.length; i++) {
                int loc = Integer.parseInt(cs[i]);
                if (ref.contains("B4E1H2")) {
                    loc = loc - 52;
                    if (loc < 0) continue;

                    list.add(proseq.getAminoaicdAt(loc) + "" + loc);
                    String window = proseq.getSeqAround(loc - 1, 7);
                    set.add(window);
                    System.out.println(loc + "\t" + window);
                } else {
                    list.add(proseq.getAminoaicdAt(loc) + "" + loc);
                    String window = proseq.getSeqAround(loc - 1, 7);
                    set.add(window);
                }
            }
            map.put(line[0], list);
            windowmap.put(line[0], set);
        }
        ur.close();

        HashMap<String, HashSet<String>> findmap = new HashMap<String, HashSet<String>>();
        HashMap<String, HashSet<String>> nofindmap = new HashMap<String, HashSet<String>>();
        HashMap<String, Integer> countmap = new HashMap<String, Integer>();
        HashMap<String, String> refmap = new HashMap<String, String>();
        ExcelReader reader = new ExcelReader(in, 2);
        line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String ref = line[7].split("\\|")[1];
            refmap.put(ref, line[7]);
            if (countmap.containsKey(ref)) {
                countmap.put(ref, countmap.get(ref) + 1);
            } else {
                countmap.put(ref, 1);
            }
            String site = line[0];
            String window = line[1];
            if (windowmap.containsKey(ref)) {
                HashSet<String> set = windowmap.get(ref);
                if (set.contains(window)) {
                    if (findmap.containsKey(ref)) {
                        findmap.get(ref).add(site);
                    } else {
                        HashSet<String> wset = new HashSet<String>();
                        wset.add(site);
                        findmap.put(ref, wset);
                    }
                } else {
                    if (nofindmap.containsKey(ref)) {
                        nofindmap.get(ref).add(site);
                    } else {
                        HashSet<String> wset = new HashSet<String>();
                        wset.add(site);
                        nofindmap.put(ref, wset);
                    }
                }
            } else {
                if (nofindmap.containsKey(ref)) {
                    nofindmap.get(ref).add(site);
                } else {
                    HashSet<String> wset = new HashSet<String>();
                    wset.add(site);
                    nofindmap.put(ref, wset);
                }
            }
        }
        reader.close();

        Iterator<String> it = refmap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            StringBuilder sb = new StringBuilder();
            sb.append(key).append("\t");
            sb.append(refmap.get(key)).append("\t");
            if (windowmap.containsKey(key)) {
                sb.append("Yes\t");
                ArrayList<String> list = map.get(key);
                String[] cs = list.toArray(new String[list.size()]);
                Arrays.sort(cs);
                for (int i = 0; i < cs.length; i++) {
                    sb.append(cs[i]).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\t");
            } else {
                sb.append("No\t");
                sb.append("-\t");
            }
            if (findmap.containsKey(key)) {
                HashSet<String> set = findmap.get(key);
                String[] cs = set.toArray(new String[set.size()]);
                Arrays.sort(cs);
                for (int i = 0; i < cs.length; i++) {
                    sb.append(cs[i]).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\t");
            } else {
                sb.append("-\t");
            }
            if (nofindmap.containsKey(key)) {
                HashSet<String> set = nofindmap.get(key);
                String[] cs = set.toArray(new String[set.size()]);
                Arrays.sort(cs);
                for (int i = 0; i < cs.length; i++) {
                    sb.append(cs[i]).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\t");
            } else {
                sb.append("-\t");
            }
            sb.append(countmap.get(key));
            System.out.println(sb);
        }
    }

    private static void peptideSTCountTest(String in) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(in, 1);
        String[] line = reader.readLine();
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        while ((line = reader.readLine()) != null) {
            String seq = line[8];
            double score = Double.parseDouble(line[10]);
            if (score >= 0.5) continue;
            int stcount = 0;
            for (int i = 0; i < seq.length(); i++) {
                char aa = seq.charAt(i);
                if (aa == 'S' || aa == 'T') {
                    stcount++;
                }
            }
            if (map.containsKey(stcount)) {
                map.put(stcount, map.get(stcount) + 1);
            } else {
                map.put(stcount, 1);
            }
        }
        reader.close();

        Integer[] cs = map.keySet().toArray(new Integer[map.size()]);
        Arrays.sort(cs);
        for (int i = 0; i < cs.length; i++) {
            System.out.println(cs[i] + "\t" + map.get(cs[i]));
        }
    }

    private static void glycanMWTest(String in) throws IOException, JXLException
    {
        OGlycanUnit[] units = OGlycanUnit.values();
        ExcelReader reader = new ExcelReader(in, 1);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String seq = line[8];
            double score = Double.parseDouble(line[10]);
            if (score < 0.5) continue;
            String[] gs = line[9].split(";");
            double mw = 0;
            for (int i = 0; i < gs.length; i++) {
                String glycan = gs[i].substring(0, gs[i].indexOf("@"));
                for (int j = 0; j < units.length; j++) {
                    if (glycan.equals(units[j].getComposition())) {
                        mw += units[j].getMass();
                    }
                }
            }
            System.out.println(mw);
        }
        reader.close();

    }

    private static void getExpGlycoMass(String infofile, String mgfs) throws IOException, DtaFileParsingException
    {
        HashMap<String, OGlycanScanInfo2> infomap = new HashMap<String, OGlycanScanInfo2>();
        BufferedReader reader = new BufferedReader(new FileReader(infofile));
        String line = null;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            infomap.put(scanname, info);

        }
        reader.close();

        File[] mgffiles = (new File(mgfs)).listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File arg0)
            {
                // TODO Auto-generated method stub
                if (arg0.getName().endsWith("mgf"))
                    return true;
                return false;
            }
        });
        Arrays.sort(mgffiles);

        for (int i = 0; i < mgffiles.length; i++) {
            MgfReader mr = new MgfReader(mgffiles[i]);
            MS2Scan ms2scan = null;
            while ((ms2scan = mr.getNextMS2Scan()) != null) {
                String basename = ms2scan.getScanName().getBaseName();
                String scanname = basename + "." + (i + 1);

                System.out.println(scanname);
                if (infomap.containsKey(scanname)) {
                    double premz = ms2scan.getPrecursorMZ();
                    int charge = ms2scan.getCharge();
                    OGlycanScanInfo2 oginfo = infomap.get(scanname);
                    double pepmass = oginfo.getPepMw();
                    System.out.println(scanname + "\t" + ((premz - AminoAcidProperty.PROTON_W) * charge - pepmass));
                }
            }
        }
    }

    /**
     * @param args
     * @throws IOException
     * @throws JXLException
     * @throws FileDamageException
     * @throws PeptideParsingException
     * @throws DtaFileParsingException
     * @throws SequenceGenerationException
     */
    public static void main(String[] args) throws IOException, JXLException, PeptideParsingException, FileDamageException, DtaFileParsingException, SequenceGenerationException
    {
        // TODO Auto-generated method stub

//		OGlycanTest5.scanCountStat("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\TC\\TC.info");
//		OGlycanTest5.scanCountStat("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\TC\\TC.info",
//				"H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\TC\\TC.xls");
//		OGlycanTest5.cao("H:\\OGLYCAN2\\Figure\\cao.txt", "H:\\OGLYCAN2\\Figure\\caocao.txt");
//		OGlycanTest5.scanCountStat("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\trypsin\\trypsin.info",
//				"H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\trypsin\\trypsin.filter8.xls");
//		OGlycanTest5.scanCountStat("H:\\OGLYCAN2\\20141024_15glyco\\2D_TC\\2D_TC.info",
//				"H:\\OGLYCAN2\\20141024_15glyco\\2D_TC\\2D_TC.xls");

//		OGlycanTest5.scanCountStat("J:\\20141112\\2D_elastase_HCC_deglyco\\2D_elastase_HCC_deglyco.info",
//				"J:\\20141112\\2D_elastase_HCC_deglyco\\");
//		OGlycanTest5.scanCountStat("H:\\OGLYCAN2\\20141113_normal_HCC\\TC_HCC\\2D_trypsin_GluC_HCC_deglyco.info",
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_HCC\\2D_trypsin_GluC_HCC_deglyco.xls");
//		OGlycanTest5.scanCountStat("H:\\OGLYCAN2\\20141113_normal_HCC\\TC_HCC\\2D_trypsin_GluC_HCC_deglyco.info",
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_HCC\\2D_trypsin_GluC_HCC_deglyco.xls");
//		OGlycanTest5.scanCountStat("H:\\OGLYCAN2\\20141113_normal_HCC\\TC_normal\\2D_trypsin_GluC_Normal_deglyco.info",
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_normal\\2D_trypsin_GluC_Normal_deglyco.xls");
//		OGlycanTest5.scanCountStat("H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_HCC\\2D_trypsin_HCC_deglyco.info",
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_HCC\\2D_trypsin_HCC_deglyco.xls");
//		OGlycanTest5.scanCountStat("H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_normal\\correction\\2D_trypsin_normal_deglyco_corr.info",
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_normal\\correction\\2D_trypsin_normal_deglyco_corr.xls");

//		OGlycanTest5.peptideInfoTest("H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_HCC\\200ppm\\2D_trypsin_HCC_deglyco.xls",
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_HCC\\200ppm\\2D_trypsin_HCC_deglyco.oglycan.F004407.dat.ppl");
//		OGlycanTest5.peptideInfoTest("H:\\OGLYCAN2\\20141113_normal_HCC\\TC_HCC\\200ppm");

//		OGlycanTest5.precursorDiffTest("J:\\20141112\\2D_trypsin_normal\\20141030_O_linked_serum_trypsin_Normal_F1.mgf",
//				"J:\\20141112\\2D_trypsin_normal\\correction\\F1\\F1.xls");
//		OGlycanTest5.precursorDiffTest("J:\\20141112\\2D_trypsin_GluC_HCC\\20141026_O_linked_serum_GluC_trypisn_HCC_F10.mgf",
//				"J:\\20141112\\2D_trypsin_GluC_HCC\\F10\\F10.xls");

//		OGlycanTest5.caocaocao("H:\\OGLYCAN2\\20141024_15glyco\\fetuin\\20141031_fetuin.xls",
//				"H:\\OGLYCAN\\OGlycan_20140423_core14b\\fetuin\\fetuin.xls");
//		OGlycanTest5.comparePPL("H:\\OGLYCAN2\\20141024_15glyco\\fetuin\\20141031_fetuin.oglycan.F004341.dat.ppl",
//				"H:\\OGLYCAN2\\20141024_15glyco\\fetuin\\20141031_fetuin.compare3.xls");

//		OGlycanTest5.typeCount("H:\\OGLYCAN2\\20141211_14glyco\\combine_F0.55.site.info.xls", 3);
//		OGlycanTest5.typeCount("H:\\OGLYCAN2\\20141211_14glyco\\normal.site.info_F0.5.xls", 3);
//		OGlycanTest5.typeCount("H:\\OGLYCAN2\\20141211_14glyco\\HCC.site.info_F0.5.xls", 3);

//		OGlycanTest5.heteroCount("H:\\OGLYCAN2\\20141211_14glyco\\normal.combine.1212.xls", 1);
//		OGlycanTest5.heteroCount("H:\\OGLYCAN2\\20141211_14glyco\\HCC.combine.1212.xls", 1);
//		OGlycanTest5.heteroCount("H:\\OGLYCAN2\\20141211_14glyco\\HCC.combine.1212.xls", 1);

//		OGlycanTest5.occupacyCompare("H:\\OGLYCAN2\\20141113_normal_HCC\\normal.combine.1205.xls",
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.1205.xls", 5);
//		OGlycanTest5.abundanceCompare("H:\\OGLYCAN2\\20141113_normal_HCC\\normal.combine.1205.xls",
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.1205.xls", 5);

//		String[] ethcd = new String[]{"I:\\20141110_OGlycan_ETD\\fetuin\\all\\result.combine.xls",
//				"H:\\OGLYCAN2\\HCD\\20141205\\all\\result.combine.xls",
//				"H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\fetuin\\result.combine.xls"};
//		OGlycanTest5.uniqueGlycopeptideCompare(ethcd, "H:\\OGLYCAN2\\20141024_15glyco\\fetuin\\20141031_fetuin.xls");

//		OGlycanTest5.getSequenceWindow(new String[]{"D:\\P\\o-glyco\\2014.12.24\\Supplementary Table 8.xls"}, "all", 4);

//		OGlycanTest5.spcountTest("H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\fetuin.xls",
//				"H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\fetuin.spectra count 2.xls", "F:\\DataBase\\o_glycan\\O-glycoprotein_0.fasta");

//		OGlycanTest5.compareInfo("H:\\OGLYCAN2\\20141024_15glyco\\fetuin\\20141031_fetuin.info",
//				"H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\fetuin.info");

//		OGlycanTest5.countUnambigurous("H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\fetuin.xls");

//		OGlycanTest5.spcountSite(new String[]{"H:\\OGLYCAN2\\casein\\20140925_casein\\20140925_casein.xls"},
//				"H:\\OGLYCAN2\\casein\\20140925_casein\\casein.site.info.xls");

		/*OGlycanTest5.spcountSite(new String[]{"H:\\OGLYCAN2\\20141211_14glyco\\2D_trypsin\\2D_trypsin.xls",
		"H:\\OGLYCAN2\\20141211_14glyco\\2D_TC\\2D_TC.xls",
		"H:\\OGLYCAN2\\20141211_14glyco\\2D_elastase\\2D_elastase.xls",
		"H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_normal\\correction\\2D_trypsin_normal_deglyco_corr_no_iso.xls",
		"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_normal\\2D_trypsin_GluC_Normal_deglyco_no_iso.xls",
		"H:\\OGLYCAN2\\20141113_normal_HCC\\elastase_normal\\2D_elastase_normal_deglyco_no_iso.xls",
		"H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_HCC\\2D_trypsin_HCC_deglyco_no_iso.xls",
		"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_HCC\\correction\\2D_trypsin_GluC_HCC_deglyco_corr_no_iso.xls",
		"H:\\OGLYCAN2\\20141113_normal_HCC\\elastase_HCC\\2D_elastase_HCC_deglyco_no_iso.xls"},
		"H:\\OGLYCAN2\\20141211_14glyco\\combine_F0.33.site.info.xls", 0.3);*/

		/*OGlycanTest5.spcountSite(new String[]{"H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\fetuin.xls"},
				"H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\fetuin.site.info.20150116.xls", 0.5);

		OGlycanTest5.spcountSite(new String[]{"H:\\OGLYCAN2\\20141211_14glyco\\2D_trypsin\\2D_trypsin.20150123.xls",
				"H:\\OGLYCAN2\\20141211_14glyco\\2D_TC\\2D_TC.20150123.xls",
				"H:\\OGLYCAN2\\20141211_14glyco\\2D_elastase\\2D_elastase.20150123.xls"},
				"H:\\OGLYCAN2\\20141211_14glyco\\serum.site.info.20150123.xls", 0.5);
		OGlycanTest5.spcountSite(new String[]{"H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_HCC\\2D_trypsin_HCC_deglyco_no_iso.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_HCC\\correction\\2D_trypsin_GluC_HCC_deglyco_corr_no_iso.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\elastase_HCC\\2D_elastase_HCC_deglyco_no_iso.20150123.xls"},
				"H:\\OGLYCAN2\\20141211_14glyco\\HCC.site.info.20150123.xls", 0.5);
		OGlycanTest5.spcountSite(new String[]{"H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_normal\\correction\\2D_trypsin_normal_deglyco_corr_no_iso.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_normal\\2D_trypsin_GluC_Normal_deglyco_no_iso.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\elastase_normal\\2D_elastase_normal_deglyco_no_iso.20150123.xls"},
				"H:\\OGLYCAN2\\20141211_14glyco\\normal.site.info.20150123.xls", 0.5);

		OGlycanTest5.spcountSite(new String[]{"H:\\OGLYCAN2\\20141211_14glyco\\2D_trypsin\\2D_trypsin.20150123.xls",
				"H:\\OGLYCAN2\\20141211_14glyco\\2D_TC\\2D_TC.20150123.xls",
				"H:\\OGLYCAN2\\20141211_14glyco\\2D_elastase\\2D_elastase.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_normal\\correction\\2D_trypsin_normal_deglyco_corr_no_iso.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_normal\\2D_trypsin_GluC_Normal_deglyco_no_iso.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\elastase_normal\\2D_elastase_normal_deglyco_no_iso.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_HCC\\2D_trypsin_HCC_deglyco_no_iso.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\TC_HCC\\correction\\2D_trypsin_GluC_HCC_deglyco_corr_no_iso.20150123.xls",
				"H:\\OGLYCAN2\\20141113_normal_HCC\\elastase_HCC\\2D_elastase_HCC_deglyco_no_iso.20150123.xls"},
				"H:\\OGLYCAN2\\20141211_14glyco\\combine.site.info.20150123.xls", 0.5);*/

//		OGlycanTest5.heteroCount3("H:\\OGLYCAN2\\20141211_14glyco\\combine_F0.55.site.info.xls", 0.5, 0.6);
//		OGlycanTest5.heteroCount3("H:\\OGLYCAN2\\20141211_14glyco\\combine_F0.55.site.info.xls", 0, 0);
        OGlycanTest5.getSequenceWindow2("H:\\OGLYCAN2\\20141211_14glyco\\combine.site.info.20150123.xls", "all", 7);
//		OGlycanTest5.compareFigure3("H:\\OGLYCAN2\\20141211_14glyco\\normal.HCC.compare.20150116.xls");
//		OGlycanTest5.getSequenceWindowFromDatabase("F:\\DataBase\\uniprot\\uniprot-human-20131211_0.fasta", 500);
//		OGlycanTest5.select("H:\\OGLYCAN2\\20141211_14glyco\\HCC.site.info.xls", "P04196", "S271", 4);

//		OGlycanTest5.select("H:\\OGLYCAN2\\20141211_14glyco\\combine_F0.55.site.info.xls", "P01876", "", 8, 0.5, 0);
//		OGlycanTest5.selectPep("H:\\OGLYCAN2\\20141211_14glyco\\normal.site.info.20150116.xls", "P01876", "GlcNAc-)GalNAc", 0.5);

//		OGlycanTest5.uniprotTest("H:\\OGLYCAN2\\20141211_14glyco\\uniprot_site.xls", "H:\\OGLYCAN2\\20141211_14glyco\\combine_F0.55.site.info.xls");
//		OGlycanTest5.uniprotTest("H:\\OGLYCAN2\\20141211_14glyco\\uniprot_site.xls", "H:\\OGLYCAN2\\20141211_14glyco\\combine.site.info.xls",
//				"F:\\DataBase\\uniprot\\uniprot-human-20131211_0.fasta");

//		OGlycanTest5.peptideSTCountTest("H:\\OGLYCAN2\\20141211_14glyco\\combine.site.info.20150116.xls");
//		OGlycanTest5.glycanMWTest("H:\\OGLYCAN2\\20141211_14glyco\\combine.site.info.20150123.xls");
//		OGlycanTest5.getExpGlycoMass("H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\fetuin.info",
//				"H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\fetuin");

//		OGlycanTest5.uniprotTestProtein("H:\\OGLYCAN2\\20141211_14glyco\\uniprot_site.xls", "H:\\OGLYCAN2\\20141211_14glyco\\combine.site.info.20150123.xls",
//				"F:\\DataBase\\uniprot\\uniprot-human-20131211_0.fasta");
    }

}
