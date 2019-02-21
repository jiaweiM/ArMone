package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.spectrum.*;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.FileCopy;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ck
 */
public class OGlycanTest4
{

    private static void getMassRange(String mgf) throws IOException, DtaFileParsingException
    {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        MgfReader reader = new MgfReader(mgf);
        MS2Scan scan = null;
        while ((scan = reader.getNextMS2Scan()) != null) {
            double mz = scan.getPrecursorMZ();
            double charge = scan.getCharge();
            if (charge == 0) continue;
            double mass = (mz - AminoAcidProperty.PROTON_W) * charge;
            if (Math.abs(mass - 2053.821484) < 1) {
                System.out.println(scan.getScanName().getScanName());
            }
            int per = (int) (mass / 100);
            if (map.containsKey(per)) {
                map.put(per, map.get(per) + 1);
            } else {
                map.put(per, 1);
            }
        }
        reader.close();
        System.out.println("Mass\tCount");
        for (Integer key : map.keySet()) {
            System.out.println(key * 100 + "\t" + map.get(key));
        }
    }

    private static void extractMgf(String info, String mgfs, String out) throws IOException, DtaFileParsingException
    {
        HashSet<String> set = new HashSet<String>();
        BufferedReader reader = new BufferedReader(new FileReader(info));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] cs = line.split("\t");
            String scan = cs[0].substring(0, cs[0].indexOf("."));
            set.add(scan);
        }
        reader.close();

        PrintWriter writer = new PrintWriter(out);
        String lineSeparator = "\n";
        File[] files = (new File(mgfs)).listFiles();
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {

            MgfReader mr = new MgfReader(files[i]);
            MS2Scan ms2scan = null;

            while ((ms2scan = mr.getNextMS2Scan()) != null) {

                IMS2PeakList peaklist = ms2scan.getPeakList();
                PrecursePeak pp = peaklist.getPrecursePeak();
                double mz = pp.getMz();
                short charge = pp.getCharge();
                String name = ms2scan.getScanName().getScanName();
                if (name.endsWith(", "))
                    name = name.substring(0, name.length() - 2);
                name = name + "." + (i + 1);//System.out.println(name);

                if (set.contains(name)) System.out.println(name);
                else continue;

                IPeak[] peaks = peaklist.getPeakArray();
                StringBuilder sb = new StringBuilder();
                sb.append("BEGIN IONS" + lineSeparator);
                sb.append("PEPMASS=" + mz + lineSeparator);
                sb.append("CHARGE=" + charge + "+" + lineSeparator);
                sb.append("TITLE=" + name + lineSeparator);

                for (int j = 0; j < peaks.length; j++) {

                    double mzi = peaks[j].getMz();
                    double inteni = peaks[j].getIntensity();

                    sb.append(mzi + "\t" + inteni + lineSeparator);
                }
                sb.append("END IONS" + lineSeparator);

                writer.write(sb.toString());
            }
        }
        writer.close();
    }

    private static void extractMgf(String match3, String match4, String mgf,
            String out) throws IOException, DtaFileParsingException
    {
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        String line = null;
        BufferedReader reader3 = new BufferedReader(new FileReader(match3));
        while ((line = reader3.readLine()) != null) {
            String[] cs = line.split("\t");
            if (map.containsKey(cs[0])) {
                map.get(cs[0]).add(cs[1]);
            } else {
                HashSet<String> set = new HashSet<String>();
                set.add(cs[1]);
                map.put(cs[0], set);
            }
        }
        reader3.close();

        BufferedReader reader4 = new BufferedReader(new FileReader(match4));
        while ((line = reader4.readLine()) != null) {
            String[] cs = line.split("\t");
            if (map.containsKey(cs[0])) {
                map.get(cs[0]).add(cs[1]);
            } else {
                HashSet<String> set = new HashSet<String>();
                set.add(cs[1]);
                map.put(cs[0], set);
            }
        }
        reader4.close();

        PrintWriter writer = new PrintWriter(out);
        String lineSeparator = "\n";

        File[] files = (new File(mgf)).listFiles();
        for (int i = 0; i < files.length; i++) {

            String filename = files[i].getName();
            filename = filename.substring(0, filename.lastIndexOf("."));
            HashSet<String> set = map.get(filename);

            MgfReader mr = new MgfReader(files[i]);
            MS2Scan ms2scan = null;

            while ((ms2scan = mr.getNextMS2Scan()) != null) {

                IMS2PeakList peaklist = ms2scan.getPeakList();
                PrecursePeak pp = peaklist.getPrecursePeak();
                double mz = pp.getMz();
                short charge = pp.getCharge();
                String name = ms2scan.getScanName().getScanName();
                if (name.endsWith(", "))
                    name = name.substring(0, name.length() - 2);

                if (set.contains(name)) System.out.println(name);
                else continue;

                IPeak[] peaks = peaklist.getPeakArray();
                StringBuilder sb = new StringBuilder();
                sb.append("BEGIN IONS" + lineSeparator);
                sb.append("PEPMASS=" + mz + lineSeparator);
                sb.append("CHARGE=" + charge + "+" + lineSeparator);
                sb.append("TITLE=" + name + lineSeparator);

                for (int j = 0; j < peaks.length; j++) {

                    double mzi = peaks[j].getMz();
                    double inteni = peaks[j].getIntensity();

                    sb.append(mzi + "\t" + inteni + lineSeparator);
                }
                sb.append("END IONS" + lineSeparator);

                writer.write(sb.toString());
            }
        }

        writer.close();
    }

    private static void extractMgfFromXls(String match, String mgf,
            String out) throws IOException, DtaFileParsingException, JXLException
    {
        HashSet<String> scans = new HashSet<String>();
        ExcelReader reader = new ExcelReader(match);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String scan = line[0].substring(0, line[0].lastIndexOf("."));
            scans.add(scan);
        }
        reader.close();

        PrintWriter writer = new PrintWriter(out);
        String lineSeparator = "\n";

        File[] files = (new File(mgf)).listFiles(new FileFilter()
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
        Arrays.sort(files);

        for (int i = 0; i < files.length; i++) {

            String filename = files[i].getName();
            filename = filename.substring(0, filename.lastIndexOf("."));

            MgfReader mr = new MgfReader(files[i]);
            MS2Scan ms2scan = null;

            while ((ms2scan = mr.getNextMS2Scan()) != null) {

                IMS2PeakList peaklist = ms2scan.getPeakList();
                PrecursePeak pp = peaklist.getPrecursePeak();
                double mz = pp.getMz();
                short charge = pp.getCharge();
                String name = ms2scan.getScanName().getScanName();
                if (name.endsWith(", "))
                    name = name.substring(0, name.length() - 2);

                name = name + "." + (i + 1);
                if (scans.contains(name)) System.out.println(name);
                else continue;

                IPeak[] peaks = peaklist.getPeakArray();
                StringBuilder sb = new StringBuilder();
                sb.append("BEGIN IONS" + lineSeparator);
                sb.append("PEPMASS=" + mz + lineSeparator);
                if (charge != 0)
                    sb.append("CHARGE=" + charge + "+" + lineSeparator);
                sb.append("TITLE=" + name + lineSeparator);

                for (int j = 0; j < peaks.length; j++) {

                    double mzi = peaks[j].getMz();
                    double inteni = peaks[j].getIntensity();

                    sb.append(mzi + "\t" + inteni + lineSeparator);
                }
                sb.append("END IONS" + lineSeparator);

                writer.write(sb.toString());
            }
        }

        writer.close();
    }

    private static void resultCompare(String s1, String s2) throws IOException, JXLException
    {
        HashMap<String, String> m1 = new HashMap<String, String>();
        ExcelReader r1 = new ExcelReader(s1);
        String[] line1 = r1.readLine();
        while ((line1 = r1.readLine()) != null) {
            String scan = line1[0].substring(0, line1[0].lastIndexOf("."));
            m1.put(scan, line1[6]);
        }
        r1.close();

        HashMap<String, String> m2 = new HashMap<String, String>();
        ExcelReader r2 = new ExcelReader(s2);
        String[] line2 = r2.readLine();
        while ((line2 = r2.readLine()) != null) {
            String scan = line2[0].substring(0, line2[0].lastIndexOf("."));
            m2.put(scan, line2[6]);
        }
        r2.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        System.out.println(m1.size() + "\t" + m2.size() + "\t" + set.size());
        for (String key : set) {
            if (m1.containsKey(key) && m2.containsKey(key)) {
                String v1 = m1.get(key);
                String v2 = m2.get(key);
                int c1 = v1.length() - v1.replaceAll("\\[", "").length();
                int c2 = v2.length() - v2.replaceAll("\\[", "").length();
                if (!v1.equals(v2)) {
                    System.out.println(key + "\t" + v1 + "\t" + v2 + "\t" + c1 + "\t" + c2 + "\t" + (c2 - c1));
                }
            }
        }
    }

    private static void resultCompareMascot(String xls, String ppldir,
            String out) throws IOException, JXLException, FileDamageException
    {
        HashMap<String, String[]> m1 = new HashMap<String, String[]>();
        ExcelReader r1 = new ExcelReader(xls);
        String[] line1 = r1.readLine();
        while ((line1 = r1.readLine()) != null) {
            String scan = line1[0].substring(0, line1[0].lastIndexOf("."));
//			System.out.println(scan);
            m1.put(scan, line1);
        }
        r1.close();

        double[] masses = new double[]{365.132198, 656.227614635, 947.32303127, 859.306987635, 1021.359812635, 1312.45522927};
        String[] names = new String[]{"Gal-GalNAc", "NeuAc-Gal-GalNAc", "NeuAc-Gal-(NeuAc-)GalNAc", "NeuAc-Gal-(GlcNAc-)GalNAc",
                "NeuAc-Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc"};

        HashMap<String, IPeptide> m2 = new HashMap<String, IPeptide>();
        HashMap<String, String> seqmap = new HashMap<String, String>();
        File[] ppl = (new File(ppldir)).listFiles(new FileFilter()
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
        Arrays.sort(ppl);

        HashMap<String, ProteinSequence> psmap = new HashMap<String, ProteinSequence>();
        FastaReader fr = new FastaReader("F:\\DataBase\\o_glycan\\O-glycoprotein_0.fasta");
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            psmap.put(ps.getReference(), ps);
        }
        fr.close();

        int[] fetuinAB = new int[2];
        for (int id = 0; id < ppl.length; id++) {

            PeptideListReader r2 = new PeptideListReader(ppl[id]);
            AminoacidModification aam = r2.getSearchParameter().getVariableInfo();

            MascotPeptide peptide = null;
            while ((peptide = (MascotPeptide) r2.getPeptide()) != null) {
                if (peptide.getRank() > 1 || peptide.getIonscore() < 15) continue;
                peptide.reCal4PValue(0.01f);
                if (peptide.getIonscore() < peptide.getIdenThres()) continue;
				/*if(peptide.getHomoThres()==0){
					if(peptide.getIonscore()<peptide.getIdenThres()){
						continue;
					}
				}else{
					if(peptide.getIonscore()<peptide.getIdenThres() && peptide.getIonscore()<peptide.getHomoThres()){
						continue;
					}
				}*/
                String sequence = peptide.getSequence();
                boolean glycan = false;
                int stcount = 0;
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < sequence.length() - 2; i++) {

                    char aa = sequence.charAt(i);
                    if (aa >= 'A' && aa <= 'Z') {
                        if (aa == 'S' || aa == 'T') {
                            stcount++;
                        }
                        sb.append(aa);
                    } else {
                        double addmass = aam.getAddedMassForModif(aa);
                        for (int k = 0; k < masses.length; k++) {
                            if (Math.abs(addmass - masses[k]) < 0.1) {
                                sb.append("[" + names[k] + "]");
                                glycan = true;
                                break;
                            }
                        }
                    }
                }
                if (glycan == false) continue;
//				if(stcount!=1) continue;
                String scan = peptide.getBaseName();
                scan = scan + "." + (id + 1);
//				System.out.println(scan);
                seqmap.put(scan, sb.toString());
                m2.put(scan, peptide);

                String ref = peptide.getProteinReferenceString();
                if (ref.contains("0003")) {
                    fetuinAB[0]++;
                } else if (ref.contains("0004")) {
                    fetuinAB[1]++;
                }
            }
            r2.close();
        }
        System.out.println("fetuinA\t" + fetuinAB[0] + "\t" + "fetuinB\t" + fetuinAB[1]);
        HashSet<String> set = new HashSet<String>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        System.out.println(m1.size() + "\t" + m2.size() + "\t" + set.size());

        ExcelWriter writer = new ExcelWriter(out, 5);
        ExcelFormat format = ExcelFormat.normalFormat;
        StringBuilder title1 = new StringBuilder();
        title1.append("Scan\t");
        title1.append("Sequence1\t");
        title1.append("Score1\t");
        title1.append("Sequence2\t");
        title1.append("Score2\t");
        title1.append("Score difference\t");
        title1.append("Equal\t");
        writer.addTitle(title1.toString(), 0, format);

        StringBuilder title2 = new StringBuilder();
        title2.append("Scan\t");
        title2.append("Sequence\t");
        title2.append("Score\t");
        title2.append("Reference\t");
        title2.append("Begin\t");
        title2.append("End\t");
        title2.append("Length\t");
        writer.addTitle(title2.toString(), 1, format);
        writer.addTitle(title2.toString(), 2, format);
        writer.addTitle(title2.toString(), 3, format);
        writer.addTitle(title2.toString(), 4, format);

        DecimalFormat df2 = DecimalFormats.DF0_2;
        ;
        for (String key : set) {
            if (m1.containsKey(key) && m2.containsKey(key)) {
                StringBuilder sb = new StringBuilder();
                sb.append(key).append("\t");
                String[] line = m1.get(key);
                IPeptide pep = m2.get(key);
                sb.append(line[6]).append("\t");
                StringBuilder usb1 = new StringBuilder();
                boolean b1 = true;
                for (int i = 0; i < line[6].length(); i++) {
                    char aa = line[6].charAt(i);
                    if (aa == ']') {
                        b1 = true;
                    } else if (aa == '[') {
                        b1 = false;
                    } else if (aa >= 'A' && aa <= 'Z') {
                        if (b1) {
                            usb1.append(aa);
                        }
                    }
                }
                sb.append(line[2]).append("\t");

                String msq = seqmap.get(key);
                sb.append(msq).append("\t");
                StringBuilder usb2 = new StringBuilder();
                boolean b2 = true;
                for (int i = 0; i < msq.length(); i++) {
                    char aa = msq.charAt(i);
                    if (aa == ']') {
                        b2 = true;
                    } else if (aa == '[') {
                        b2 = false;
                    } else if (aa >= 'A' && aa <= 'Z') {
                        if (b2) {
                            usb2.append(aa);
                        }
                    }
                }
                sb.append(pep.getPrimaryScore()).append("\t");
                sb.append(df2.format(Double.parseDouble(line[2]) - pep.getPrimaryScore())).append("\t");
//System.out.println(usb1+"\t"+usb2);
                sb.append((usb1.toString()).equals(usb2.toString()));
                writer.addContent(sb.toString(), 0, format);

                HashMap<String, SeqLocAround> slamap = pep.getPepLocAroundMap();
                HashSet<ProteinReference> refset = pep.getProteinReferences();
                Iterator<ProteinReference> refit = refset.iterator();
                SeqLocAround sla = null;
                String reference = null;
                if (refit.hasNext()) {
                    ProteinReference proref = refit.next();
                    sla = slamap.get(proref.toString());
                    reference = proref.getName();
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append(key).append("\t");
                sb2.append(seqmap.get(key)).append("\t");
                sb2.append(pep.getPrimaryScore()).append("\t");
                sb2.append(reference).append("\t");
                sb2.append(sla.getBeg()).append("\t");
                sb2.append(sla.getEnd()).append("\t");
                sb2.append(sla.getEnd() - sla.getBeg()).append("\t");
                writer.addContent(sb2.toString(), 3, format);
                writer.addContent(sb2.toString(), 4, format);

            } else if (m2.containsKey(key)) {
                IPeptide pep = m2.get(key);
                HashMap<String, SeqLocAround> slamap = pep.getPepLocAroundMap();
                HashSet<ProteinReference> refset = pep.getProteinReferences();
                Iterator<ProteinReference> refit = refset.iterator();
                SeqLocAround sla = null;
                String reference = null;
                if (refit.hasNext()) {
                    ProteinReference proref = refit.next();
                    sla = slamap.get(proref.toString());
                    reference = proref.getName();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(key).append("\t");
                sb.append(seqmap.get(key)).append("\t");
                sb.append(pep.getPrimaryScore()).append("\t");
                sb.append(reference).append("\t");
                sb.append(sla.getBeg()).append("\t");
                sb.append(sla.getEnd()).append("\t");
                sb.append(sla.getEnd() - sla.getBeg()).append("\t");
                writer.addContent(sb.toString(), 1, format);
                writer.addContent(sb.toString(), 3, format);
//				System.out.println(key+"\t"+seqmap.get(key)+"\t"+pep.getPrimaryScore()+"\t"+reference+"\t"+sla.getBeg()+"\t"+sla.getEnd()+"\t"+(sla.getEnd()-sla.getBeg()));
            } else {
                String[] line = m1.get(key);
                StringBuilder sb = new StringBuilder();
                sb.append(line[0]).append("\t");
                sb.append(line[6]).append("\t");
                sb.append(line[2]).append("\t");
                sb.append(line[5]).append("\t");
                boolean b = true;
                StringBuilder seqsb = new StringBuilder();
                for (int i = 0; i < line[6].length(); i++) {
                    char aa = line[6].charAt(i);
                    if (aa == '[') {
                        b = false;
                    } else if (aa == ']') {
                        b = true;
                    } else {
                        if (b) {
                            seqsb.append(aa);
                        }
                    }
                }
                ProteinSequence proseq = psmap.get(line[5]);
                int begin = proseq.indexOf(seqsb.toString());
                sb.append(begin + 1).append("\t");
                sb.append(begin + seqsb.length()).append("\t");
                sb.append(seqsb.length()).append("\t");
                writer.addContent(sb.toString(), 2, format);
                writer.addContent(sb.toString(), 4, format);
            }
        }
        writer.close();
    }

    private static void resultCompareMascotExpect(String xls, String cao, String ppldir,
            String out) throws IOException, JXLException, FileDamageException
    {
        HashSet<String> caoset = new HashSet<String>();
        ExcelReader r0 = new ExcelReader(cao, 0);
        String[] line0 = r0.readLine();
        while ((line0 = r0.readLine()) != null) {
            String scan = line0[0].substring(0, line0[0].lastIndexOf("."));
            caoset.add(scan);
        }
        r0.close();
        HashMap<String, String[]> m1 = new HashMap<String, String[]>();
        ExcelReader r1 = new ExcelReader(xls);
        String[] line1 = r1.readLine();
        while ((line1 = r1.readLine()) != null) {
            String scan = line1[0].substring(0, line1[0].lastIndexOf("."));
//			System.out.println(scan);
            if (caoset.contains(scan))
                m1.put(scan, line1);
        }
        r1.close();

        double[] masses = new double[]{365.132198, 656.227614635, 947.32303127, 859.306987635, 1021.359812635, 1312.45522927};
        String[] names = new String[]{"Gal-GalNAc", "NeuAc-Gal-GalNAc", "NeuAc-Gal-(NeuAc-)GalNAc", "NeuAc-Gal-(GlcNAc-)GalNAc",
                "NeuAc-Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc"};

        HashMap<String, IPeptide> m2 = new HashMap<String, IPeptide>();
        HashMap<String, String> seqmap = new HashMap<String, String>();
        File[] ppl = (new File(ppldir)).listFiles(new FileFilter()
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
        Arrays.sort(ppl);

        HashMap<String, ProteinSequence> psmap = new HashMap<String, ProteinSequence>();
        FastaReader fr = new FastaReader("F:\\DataBase\\o_glycan\\O-glycoprotein_0.fasta");
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            psmap.put(ps.getReference(), ps);
        }
        fr.close();

        int[] fetuinAB = new int[2];
        for (int id = 0; id < ppl.length; id++) {

            PeptideListReader r2 = new PeptideListReader(ppl[id]);
            AminoacidModification aam = r2.getSearchParameter().getVariableInfo();

            MascotPeptide peptide = null;
            while ((peptide = (MascotPeptide) r2.getPeptide()) != null) {
                if (peptide.getRank() > 1 || peptide.getIonscore() < 20) continue;
//				peptide.reCal4PValue(0.01f);
//				if(peptide.getIonscore()<peptide.getIdenThres()) continue;
				/*if(peptide.getHomoThres()==0){
					if(peptide.getIonscore()<peptide.getIdenThres()){
						continue;
					}
				}else{
					if(peptide.getIonscore()<peptide.getIdenThres() && peptide.getIonscore()<peptide.getHomoThres()){
						continue;
					}
				}*/
                String sequence = peptide.getSequence();
                boolean glycan = false;
                int stcount = 0;
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < sequence.length() - 2; i++) {

                    char aa = sequence.charAt(i);
                    if (aa >= 'A' && aa <= 'Z') {
                        if (aa == 'S' || aa == 'T') {
                            stcount++;
                        }
                        sb.append(aa);
                    } else {
                        double addmass = aam.getAddedMassForModif(aa);
                        for (int k = 0; k < masses.length; k++) {
                            if (Math.abs(addmass - masses[k]) < 0.1) {
                                sb.append("[" + names[k] + "]");
                                glycan = true;
                                break;
                            }
                        }
                    }
                }
                if (glycan == false) continue;
//				if(stcount!=1) continue;
                String scan = peptide.getBaseName();
                scan = scan + "." + (id + 1);
//				System.out.println(scan);
                seqmap.put(scan, sb.toString());
                m2.put(scan, peptide);

                String ref = peptide.getProteinReferenceString();
                if (ref.contains("0003")) {
                    fetuinAB[0]++;
                } else if (ref.contains("0004")) {
                    fetuinAB[1]++;
                }
            }
            r2.close();
        }
        System.out.println("fetuinA\t" + fetuinAB[0] + "\t" + "fetuinB\t" + fetuinAB[1]);
        HashSet<String> set = new HashSet<String>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        System.out.println(m1.size() + "\t" + m2.size() + "\t" + set.size());

        ExcelWriter writer = new ExcelWriter(out, 5);
        ExcelFormat format = ExcelFormat.normalFormat;
        StringBuilder title1 = new StringBuilder();
        title1.append("Scan\t");
        title1.append("Sequence1\t");
        title1.append("Score1\t");
        title1.append("Expect1\t");
        title1.append("Sequence2\t");
        title1.append("Score2\t");
        title1.append("Expect2\t");
        title1.append("Score difference\t");
        title1.append("-Log(Expect) difference\t");
        title1.append("Equal\t");
        writer.addTitle(title1.toString(), 0, format);

        StringBuilder title2 = new StringBuilder();
        title2.append("Scan\t");
        title2.append("Sequence\t");
        title2.append("Score\t");
        title2.append("Reference\t");
        title2.append("Begin\t");
        title2.append("End\t");
        title2.append("Length\t");
        writer.addTitle(title2.toString(), 1, format);
        writer.addTitle(title2.toString(), 2, format);
        writer.addTitle(title2.toString(), 3, format);
        writer.addTitle(title2.toString(), 4, format);

        DecimalFormat df2 = DecimalFormats.DF0_2;
        DecimalFormat dfe = DecimalFormats.DF_E3;
        for (String key : set) {
            if (m1.containsKey(key) && m2.containsKey(key)) {
                StringBuilder sb = new StringBuilder();
                sb.append(key).append("\t");
                String[] line = m1.get(key);
                MascotPeptide pep = (MascotPeptide) m2.get(key);
                sb.append(line[7]).append("\t");
                StringBuilder usb1 = new StringBuilder();
                boolean b1 = true;
                for (int i = 0; i < line[7].length(); i++) {
                    char aa = line[7].charAt(i);
                    if (aa == ']') {
                        b1 = true;
                    } else if (aa == '[') {
                        b1 = false;
                    } else if (aa >= 'A' && aa <= 'Z') {
                        if (b1) {
                            usb1.append(aa);
                        }
                    }
                }
                sb.append(line[2]).append("\t");
                sb.append(line[3]).append("\t");

                String msq = seqmap.get(key);
                sb.append(msq).append("\t");
                StringBuilder usb2 = new StringBuilder();
                boolean b2 = true;
                for (int i = 0; i < msq.length(); i++) {
                    char aa = msq.charAt(i);
                    if (aa == ']') {
                        b2 = true;
                    } else if (aa == '[') {
                        b2 = false;
                    } else if (aa >= 'A' && aa <= 'Z') {
                        if (b2) {
                            usb2.append(aa);
                        }
                    }
                }
                sb.append(pep.getPrimaryScore()).append("\t");
                sb.append(dfe.format(pep.getEvalue())).append("\t");
                sb.append(df2.format(Double.parseDouble(line[2]) - pep.getPrimaryScore())).append("\t");
                double exp1 = Double.parseDouble(line[3]);
                double logexp1 = -Math.log10(exp1);
                double logexp2 = -Math.log10(pep.getEvalue());
                sb.append(df2.format(logexp1 - logexp2)).append("\t");
//System.out.println(usb1+"\t"+usb2);
                sb.append((usb1.toString()).equals(usb2.toString()));
                writer.addContent(sb.toString(), 0, format);

                HashMap<String, SeqLocAround> slamap = pep.getPepLocAroundMap();
                HashSet<ProteinReference> refset = pep.getProteinReferences();
                Iterator<ProteinReference> refit = refset.iterator();
                SeqLocAround sla = null;
                String reference = null;
                if (refit.hasNext()) {
                    ProteinReference proref = refit.next();
                    sla = slamap.get(proref.toString());
                    reference = proref.getName();
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append(key).append("\t");
                sb2.append(seqmap.get(key)).append("\t");
                sb2.append(pep.getPrimaryScore()).append("\t");
                sb2.append(reference).append("\t");
                sb2.append(sla.getBeg()).append("\t");
                sb2.append(sla.getEnd()).append("\t");
                sb2.append(sla.getEnd() - sla.getBeg()).append("\t");
                writer.addContent(sb2.toString(), 3, format);
                writer.addContent(sb2.toString(), 4, format);

            } else if (m2.containsKey(key)) {
                IPeptide pep = m2.get(key);
                HashMap<String, SeqLocAround> slamap = pep.getPepLocAroundMap();
                HashSet<ProteinReference> refset = pep.getProteinReferences();
                Iterator<ProteinReference> refit = refset.iterator();
                SeqLocAround sla = null;
                String reference = null;
                if (refit.hasNext()) {
                    ProteinReference proref = refit.next();
                    sla = slamap.get(proref.toString());
                    reference = proref.getName();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(key).append("\t");
                sb.append(seqmap.get(key)).append("\t");
                sb.append(pep.getPrimaryScore()).append("\t");
                sb.append(reference).append("\t");
                sb.append(sla.getBeg()).append("\t");
                sb.append(sla.getEnd()).append("\t");
                sb.append(sla.getEnd() - sla.getBeg()).append("\t");
                writer.addContent(sb.toString(), 1, format);
                writer.addContent(sb.toString(), 3, format);
//				System.out.println(key+"\t"+seqmap.get(key)+"\t"+pep.getPrimaryScore()+"\t"+reference+"\t"+sla.getBeg()+"\t"+sla.getEnd()+"\t"+(sla.getEnd()-sla.getBeg()));
            } else {
                String[] line = m1.get(key);
                StringBuilder sb = new StringBuilder();
                sb.append(line[0]).append("\t");
                sb.append(line[6]).append("\t");
                sb.append(line[2]).append("\t");
                sb.append(line[5]).append("\t");
                boolean b = true;
                StringBuilder seqsb = new StringBuilder();
                for (int i = 0; i < line[6].length(); i++) {
                    char aa = line[6].charAt(i);
                    if (aa == '[') {
                        b = false;
                    } else if (aa == ']') {
                        b = true;
                    } else {
                        if (b) {
                            seqsb.append(aa);
                        }
                    }
                }
                ProteinSequence proseq = psmap.get(line[5]);
                int begin = proseq.indexOf(seqsb.toString());
                sb.append(begin + 1).append("\t");
                sb.append(begin + seqsb.length()).append("\t");
                sb.append(seqsb.length()).append("\t");
                writer.addContent(sb.toString(), 2, format);
                writer.addContent(sb.toString(), 4, format);
            }
        }
        writer.close();
    }

    private static void resultCompareMascotTD(String xls, String[] ppl, String out,
            String fasta) throws IOException, JXLException, FileDamageException
    {
        HashMap<String, String[]> m1 = new HashMap<String, String[]>();
        ExcelReader r1 = new ExcelReader(xls);
        String[] line1 = r1.readLine();
        while ((line1 = r1.readLine()) != null) {
            String scan = line1[0].substring(0, line1[0].lastIndexOf("."));
            m1.put(scan, line1);
        }
        r1.close();

        double[] masses = new double[]{365.132198, 656.227614635, 947.32303127, 859.306987635, 1021.359812635, 1312.45522927};
        String[] names = new String[]{"Gal-GalNAc", "NeuAc-Gal-GalNAc", "NeuAc-Gal-(NeuAc-)GalNAc", "NeuAc-Gal-(GlcNAc-)GalNAc",
                "NeuAc-Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc"};

        HashMap<String, IPeptide> m2 = new HashMap<String, IPeptide>();
        HashMap<String, String> seqmap = new HashMap<String, String>();
        for (int id = 0; id < ppl.length; id++) {

            int[][] threshold = new int[50][2];
            PeptideListReader r2 = new PeptideListReader(ppl[id]);
            ArrayList<MascotPeptide> peplist = new ArrayList<MascotPeptide>();
            MascotPeptide pep = null;
            while ((pep = (MascotPeptide) r2.getPeptide()) != null) {

//				if(Math.abs(pep.getDeltaMZppm())>10) continue;
//				if(pep.getPrimaryScore()<15) continue;
                if (pep.getRank() > 1) continue;

                peplist.add(pep);
                double score = pep.getIonscore();
                if (pep.isTP()) {
                    for (int i = 0; i < threshold.length; i++) {
                        if (score < i) break;
                        threshold[i][0]++;
                    }
                } else {
                    for (int i = 0; i < threshold.length; i++) {
                        if (score < i) break;
                        threshold[i][1]++;
                    }
                }
            }
            int thres = 50;
            for (int i = 0; i < threshold.length; i++) {
                double fdr = (double) threshold[i][1] / (double) threshold[i][0];
                if (fdr < 0.01) {
                    thres = i;
                    break;
                }
            }

            AminoacidModification aam = r2.getSearchParameter().getVariableInfo();

            for (int i = 0; i < peplist.size(); i++) {
                MascotPeptide peptide = peplist.get(i);
                if (!peptide.isTP() || peptide.getIonscore() < thres) continue;
                String sequence = peptide.getSequence();
                boolean glycan = false;
                StringBuilder sb = new StringBuilder();
                for (int j = 2; j < sequence.length() - 2; j++) {

                    char aa = sequence.charAt(j);
                    if (aa >= 'A' && aa <= 'Z') {
                        sb.append(aa);
                    } else {
                        double addmass = aam.getAddedMassForModif(aa);
                        for (int k = 0; k < masses.length; k++) {
                            if (Math.abs(addmass - masses[k]) < 0.1) {
                                sb.append("[" + names[k] + "]");
                                glycan = true;
                                break;
                            }
                        }
                    }
                }
                if (glycan == false) continue;
                String scan = peptide.getBaseName();
//				scan = scan+"."+(id+1);
                if (seqmap.containsKey(scan)) {
                    if (peptide.getPrimaryScore() > m2.get(scan).getPrimaryScore()) {
                        seqmap.put(scan, sb.toString());
                        m2.put(scan, peptide);
                    }
                } else {
                    seqmap.put(scan, sb.toString());
                    m2.put(scan, peptide);
                }
            }
            r2.close();
        }

        HashSet<String> set = new HashSet<String>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        System.out.println(m1.size() + "\t" + m2.size() + "\t" + set.size());

        ExcelWriter writer = new ExcelWriter(out, 5);
        ExcelFormat format = ExcelFormat.normalFormat;
        StringBuilder title1 = new StringBuilder();
        title1.append("Scan\t");
        title1.append("Sequence1\t");
        title1.append("Score1\t");
        title1.append("Sequence2\t");
        title1.append("Score2\t");
        title1.append("Score difference\t");
        title1.append("Equal\t");
        writer.addTitle(title1.toString(), 0, format);

        StringBuilder title2 = new StringBuilder();
        title2.append("Scan\t");
        title2.append("Sequence\t");
        title2.append("Score\t");
        title2.append("Reference\t");
        title2.append("Begin\t");
        title2.append("End\t");
        title2.append("Length\t");
        writer.addTitle(title2.toString(), 1, format);
        writer.addTitle(title2.toString(), 2, format);
        writer.addTitle(title2.toString(), 3, format);
        writer.addTitle(title2.toString(), 4, format);

        DecimalFormat df2 = DecimalFormats.DF0_2;
        ;
        for (String key : set) {
            if (m1.containsKey(key) && m2.containsKey(key)) {
                StringBuilder sb = new StringBuilder();
                sb.append(key).append("\t");
                String[] line = m1.get(key);
                IPeptide pep = m2.get(key);
                sb.append(line[6]).append("\t");
                StringBuilder usb1 = new StringBuilder();
                boolean b1 = true;
                for (int i = 0; i < line[6].length(); i++) {
                    char aa = line[6].charAt(i);
                    if (aa == ']') {
                        b1 = true;
                    } else if (aa == '[') {
                        b1 = false;
                    } else if (aa >= 'A' && aa <= 'Z') {
                        if (b1) {
                            usb1.append(aa);
                        }
                    }
                }
                sb.append(line[2]).append("\t");

                String msq = seqmap.get(key);
                sb.append(msq).append("\t");
                StringBuilder usb2 = new StringBuilder();
                boolean b2 = true;
                for (int i = 0; i < msq.length(); i++) {
                    char aa = msq.charAt(i);
                    if (aa == ']') {
                        b2 = true;
                    } else if (aa == '[') {
                        b2 = false;
                    } else if (aa >= 'A' && aa <= 'Z') {
                        if (b2) {
                            usb2.append(aa);
                        }
                    }
                }
                sb.append(pep.getPrimaryScore()).append("\t");
                sb.append(df2.format(Double.parseDouble(line[2]) - pep.getPrimaryScore())).append("\t");
//System.out.println(usb1+"\t"+usb2);
                sb.append((usb1.toString()).equals(usb2.toString()));
                writer.addContent(sb.toString(), 0, format);

                HashMap<String, SeqLocAround> slamap = pep.getPepLocAroundMap();
                HashSet<ProteinReference> refset = pep.getProteinReferences();
                Iterator<ProteinReference> refit = refset.iterator();
                SeqLocAround sla = null;
                String reference = null;
                if (refit.hasNext()) {
                    ProteinReference proref = refit.next();
                    sla = slamap.get(proref.toString());
                    reference = proref.getName();
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append(key).append("\t");
                sb2.append(seqmap.get(key)).append("\t");
                sb2.append(pep.getPrimaryScore()).append("\t");
                sb2.append(reference).append("\t");
                sb2.append(sla.getBeg()).append("\t");
                sb2.append(sla.getEnd()).append("\t");
                sb2.append(sla.getEnd() - sla.getBeg()).append("\t");
                writer.addContent(sb2.toString(), 3, format);
                writer.addContent(sb2.toString(), 4, format);

            } else if (m2.containsKey(key)) {
                IPeptide pep = m2.get(key);
                HashMap<String, SeqLocAround> slamap = pep.getPepLocAroundMap();
                HashSet<ProteinReference> refset = pep.getProteinReferences();
                Iterator<ProteinReference> refit = refset.iterator();
                SeqLocAround sla = null;
                String reference = null;
                if (refit.hasNext()) {
                    ProteinReference proref = refit.next();
                    sla = slamap.get(proref.toString());
                    reference = proref.getName();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(key).append("\t");
                sb.append(seqmap.get(key)).append("\t");
                sb.append(pep.getPrimaryScore()).append("\t");
                sb.append(reference).append("\t");
                sb.append(sla.getBeg()).append("\t");
                sb.append(sla.getEnd()).append("\t");
                sb.append(sla.getEnd() - sla.getBeg()).append("\t");
                writer.addContent(sb.toString(), 1, format);
                writer.addContent(sb.toString(), 3, format);
//				System.out.println(key+"\t"+seqmap.get(key)+"\t"+pep.getPrimaryScore()+"\t"+reference+"\t"+sla.getBeg()+"\t"+sla.getEnd()+"\t"+(sla.getEnd()-sla.getBeg()));
            } else {
                HashMap<String, ProteinSequence> psmap = new HashMap<String, ProteinSequence>();
                FastaReader fr = new FastaReader(fasta);
                ProteinSequence ps = null;
                while ((ps = fr.nextSequence()) != null) {
                    psmap.put(ps.getReference(), ps);
                }
                fr.close();

                String[] line = m1.get(key);
                StringBuilder sb = new StringBuilder();
                sb.append(line[0]).append("\t");
                sb.append(line[6]).append("\t");
                sb.append(line[2]).append("\t");
                sb.append(line[5]).append("\t");
                boolean b = true;
                StringBuilder seqsb = new StringBuilder();
                for (int i = 0; i < line[6].length(); i++) {
                    char aa = line[6].charAt(i);
                    if (aa == '[') {
                        b = false;
                    } else if (aa == ']') {
                        b = true;
                    } else {
                        if (b) {
                            seqsb.append(aa);
                        }
                    }
                }

                ProteinSequence proseq = psmap.get(line[5]);
                int begin = proseq.indexOf(seqsb.toString());
                sb.append(begin + 1).append("\t");
                sb.append(begin + seqsb.length()).append("\t");
                sb.append(seqsb.length()).append("\t");
                writer.addContent(sb.toString(), 2, format);
                writer.addContent(sb.toString(), 4, format);
            }
        }
        writer.close();
    }

    private static void resultCompareMascotXls(String xls1,
            String xls2) throws IOException, JXLException, FileDamageException
    {
        HashSet<String> s11 = new HashSet<String>();
        HashSet<String> s12 = new HashSet<String>();
        ExcelReader r1 = new ExcelReader(xls1, 2);
        String[] line1 = r1.readLine();
        while ((line1 = r1.readLine()) != null) {
            s11.add(line1[0] + "\t" + line1[5]);
            s12.add(line1[0] + "\t" + line1[5] + "\t" + line1[2]);
        }
        r1.close();

        HashSet<String> s21 = new HashSet<String>();
        HashSet<String> s22 = new HashSet<String>();
        ExcelReader r2 = new ExcelReader(xls2, 2);
        String[] line2 = r2.readLine();
        while ((line2 = r2.readLine()) != null) {
            s21.add(line2[0] + "\t" + line2[2]);
            s22.add(line2[0] + "\t" + line2[2] + "\t" + line2[1]);
        }
        r2.close();

        HashSet<String> set1 = new HashSet<String>();
        set1.addAll(s11);
        set1.addAll(s21);
        System.out.println(s11.size() + "\t" + s21.size() + "\t" + set1.size() + "\t" + (s11.size() + s21.size() - set1.size()));

        HashSet<String> set2 = new HashSet<String>();
        set2.addAll(s12);
        set2.addAll(s22);
        System.out.println(s12.size() + "\t" + s22.size() + "\t" + set2.size() + "\t" + (s12.size() + s22.size() - set2.size()));
    }

    private static void pplSiteBatchWriter(String dir, int sample,
            double deltaThres) throws RowsExceededException, FileDamageException, WriteException, IOException
    {

        HashSet<String>[] identifiedSet = new HashSet[2];
        identifiedSet[0] = new HashSet<String>();
        identifiedSet[0].add("S271");
        identifiedSet[0].add("S282");
        identifiedSet[0].add("S290");
        identifiedSet[0].add("S296");
        identifiedSet[0].add("S320");
        identifiedSet[0].add("S323");
        identifiedSet[0].add("S325");
        identifiedSet[0].add("S324");
        identifiedSet[0].add("T280");
        identifiedSet[0].add("T334");
        identifiedSet[0].add("S157");
        identifiedSet[0].add("S164");
        identifiedSet[0].add("S297");
        identifiedSet[0].add("T151");
        identifiedSet[0].add("T158");
        identifiedSet[0].add("T19");
        identifiedSet[0].add("T266");
        identifiedSet[0].add("T284");
        identifiedSet[0].add("T292");
        identifiedSet[0].add("T295");
        identifiedSet[0].add("T299");

        identifiedSet[1] = new HashSet<String>();
        identifiedSet[1].add("S148");
        identifiedSet[1].add("S153");
        identifiedSet[1].add("S162");
        identifiedSet[1].add("S176");
        identifiedSet[1].add("S187");
        identifiedSet[1].add("T114");
        identifiedSet[1].add("T115");
        identifiedSet[1].add("T138");
        identifiedSet[1].add("T142");
        identifiedSet[1].add("T145");
        identifiedSet[1].add("T152");
        identifiedSet[1].add("T156");
        identifiedSet[1].add("T157");
        identifiedSet[1].add("T163");
        identifiedSet[1].add("T166");
        identifiedSet[1].add("T182");
        identifiedSet[1].add("T186");
        identifiedSet[1].add("T188");

        double[] masses = new double[]{203.0794, 365.132198, 406.1587, 568.211571, 656.227614635, 730.264396, 947.32303127,
                859.306987635, 1021.359812635, 1312.45522927};
        String[] names = new String[]{"GalNAc", "Gal-GalNAc", "GlcNAc-GalNAc", "Gal-(GlcNAc-)GalNAc", "NeuAc-Gal-GalNAc", "Gal-(Gal-GlcNAc-)GalNAc",
                "NeuAc-Gal-(NeuAc-)GalNAc", "NeuAc-Gal-(GlcNAc-)GalNAc",
                "NeuAc-Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc"};

        ExcelWriter writer = new ExcelWriter(dir + "\\result.combine20.xls", new String[]{"Content", "Glycopeptides", "Site-specific glycans", "Glycosylation sites all",
                "Glycosylation sites high", "Glycosylation sites low"});

        ExcelFormat format = ExcelFormat.normalFormat;

        String title0 = "Scan\tModified sequence\tPeptide Mr\tIon Score\tExpect\tDelta score\tLength\tProtein";
        String title1 = "Site\tSequence window\tGlycan\tIon Score\tDelta score\tProtein\tScan\tModified sequence";
        String title2 = "Site\tSequence window\tIon Score\tDelta score\tProtein\tModified sequence";
        writer.addTitle(title0, 1, format);
        writer.addTitle(title1, 2, format);
        writer.addTitle(title2, 3, format);
        writer.addTitle(title2, 4, format);
        writer.addTitle(title2, 5, format);

		/*StringBuilder sb1 = new StringBuilder();
		sb1.append("Scan\t");
		sb1.append("Peptide Mr\t");
		sb1.append("Ion Score\t");
		sb1.append("Length\t");
		sb1.append("Proteins\t");
		sb1.append("Protein\t");
		sb1.append("Modified sequence\t");
		sb1.append("Glycan\t");

		StringBuilder sb2 = new StringBuilder();
		sb2.append("Site\t");
		sb2.append("Glycan\t");
		sb2.append("Protein\t");
		sb2.append("Scan\t");
		sb2.append("Modified sequence\t");

		writer.addTitle(sb1.toString(), 0, format);
		writer.addTitle(sb2.toString(), 1, format);
		writer.addTitle(sb2.toString(), 2, format);*/

        DecimalFormat df2 = DecimalFormats.DF0_2;
        DecimalFormat dfe = DecimalFormats.DF_E3;

        HashMap<String, Double> sitemap = new HashMap<String, Double>();
        HashMap<String, String> sitemap2 = new HashMap<String, String>();
        File[] files = (new File(dir)).listFiles(new FileFilter()
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

        HashMap<String, String> highmap = new HashMap<String, String>();
        HashMap<String, String> lowmap = new HashMap<String, String>();

        for (int i = 0; i < files.length; i++) {

            String in = files[i].getAbsolutePath();
//			pplSiteWriter(in, in.replace("ppl", "site.xls"));
//			pplSiteWriterTD(in, out);
            PeptideListReader reader = new PeptideListReader(in);
            AminoacidModification aam = reader.getSearchParameter().getVariableInfo();
            Modif[] modifs = aam.getModifications();
            HashMap<Character, String> modnamemap = new HashMap<Character, String>();
            for (int j = 0; j < modifs.length; j++) {
                modnamemap.put(modifs[j].getSymbol(), modifs[j].getName());
            }
            ProteinNameAccesser accesser = reader.getProNameAccesser();
            MascotPeptide peptide = null;
            while ((peptide = (MascotPeptide) reader.getPeptide()) != null) {

//				if(Math.abs(peptide.getDeltaMZppm())>10) continue;
//				peptide.reCal4PValue(0.05f);
                if (peptide.getPrimaryScore() < 20) continue;
//				if(peptide.getDeltaS()<0.2) continue;
                if (peptide.getIonscore() < peptide.getIdenThres()) {
                    continue;
                }
//				if(peptide.getEvalue()>0.05) continue;
				/*if(peptide.getHomoThres()==0){
					if(peptide.getIonscore()<peptide.getIdenThres()){
						continue;
					}
				}else{
					if(peptide.getIonscore()<peptide.getIdenThres() && peptide.getIonscore()<peptide.getHomoThres()){
						continue;
					}
				}*/

                if (peptide.getRank() > 1) continue;
                String sequence = peptide.getSequence();
                boolean glycan = false;

                HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();
                HashSet<ProteinReference> refset = peptide.getProteinReferences();
                Iterator<ProteinReference> refit = refset.iterator();
                int beg = 0;
                SeqLocAround sla = null;
                String ref = "";
                int sampleType = 0;
                if (refit.hasNext()) {
                    ProteinReference proref = refit.next();
                    sla = slamap.get(proref.toString());
                    beg = sla.getBeg();
                    ref = accesser.getProInfo(proref.getName()).getRef();
                    if (sampleType == 0) {
                        if (ref.contains("sp0003") || ref.contains("sp0004")) {
                            sampleType = 0;
                        }
                        if (ref.contains("sp0006")) {
                            sampleType = 1;
                        }
                    }
                }
                if (sampleType != sample)
                    continue;

                StringBuilder sb = new StringBuilder();
                StringBuilder usb = new StringBuilder();
                HashMap<String, String> tempSiteMap = new HashMap<String, String>();

                for (int j = 2; j < sequence.length() - 2; j++) {
                    char aa = sequence.charAt(j);
                    if (aa >= 'A' && aa <= 'Z') {
                        sb.append(aa);
                        usb.append(aa);
                    } else {
                        double addmass = aam.getAddedMassForModif(aa);
                        for (int l = 0; l < masses.length; l++) {
                            if (Math.abs(addmass - masses[l]) < 0.1) {
                                sb.append("[" + names[l] + "]");
                                glycan = true;
                                String site = usb.charAt(usb.length() - 1) + "" + (beg + usb.length() - 1);
                                if (identifiedSet[sample].contains(site))
                                    tempSiteMap.put(site, names[l]);
                                break;
                            }
                        }
                    }
                }
                if (glycan == false || tempSiteMap.size() == 0) continue;
//				if(glycan==false) continue;

                String scan = peptide.getBaseName();
                double ionscore = peptide.getPrimaryScore();
                double deltascore = peptide.getDeltaS();

                //String title0 = "Scan\tModified sequence\tPeptide Mr\tIon Score\tExpect\tDelta score\tLength\tProtein";
                //String title1 = "Site\tSequence window\tGlycan\tIon Score\tDelta score\tProtein\tScan\tModified sequence";
                //String title2 = "Site\tSequence window\tIon Score\tDelta score\tProtein\tModified sequence";

                StringBuilder content = new StringBuilder();
                content.append(peptide.getScanNum()).append("\t");
                content.append(sb).append("\t");
                content.append(peptide.getMr()).append("\t");
                content.append(df2.format(ionscore)).append("\t");
                content.append(dfe.format(peptide.getEvalue())).append("\t");
                content.append(df2.format(deltascore)).append("\t");
//				content.append(df2.format(peptide.getDeltaS())).append("\t");
                content.append(usb.length()).append("\t");
                content.append(ref).append("\t");
                writer.addContent(content.toString(), 1, format);

                StringBuilder sitesb = new StringBuilder();
                sitesb.append(df2.format(ionscore)).append("\t");
                sitesb.append(df2.format(deltascore)).append("\t");
                sitesb.append(peptide.getScanNum()).append("\t");
                sitesb.append(sb).append("\t");

                String pre = sla.getPre();
                String nex = sla.getNext();
                for (int j = 0; j < 7 - pre.length(); j++) {
                    pre = "_" + pre;
                }
                for (int j = 0; j < 7 - nex.length(); j++) {
                    nex = nex + "_";
                }
                Iterator<String> it = tempSiteMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    int loc = Integer.parseInt(key.substring(1));
                    String window = (pre + usb + nex).substring(loc - beg, loc - beg + 15);

                    String value = tempSiteMap.get(key);
                    String key2 = key + "\t" + ref;
                    String content1 = key + "\t" + window + "\t" + value + "\t" + df2.format(ionscore) + "\t" + df2.format(deltascore) + "\t" + ref + "\t" + peptide.getScanNum() + "\t" + sb;
                    String content2 = key + "\t" + window + "\t" + df2.format(ionscore) + "\t" + df2.format(deltascore) + "\t" + ref + "\t" + sb;
                    writer.addContent(content1, 2, format);

                    if (deltascore >= deltaThres) {
                        if (highmap.containsKey(key2)) {
                            double ds = Double.parseDouble(highmap.get(key2).split("\t")[3]);
                            if (deltascore > ds) {
                                highmap.put(key2, content2);
                            }
                        } else {
                            highmap.put(key2, content2);
                            if (lowmap.containsKey(key2)) {
                                lowmap.remove(key2);
                            }
                        }
                    } else {
                        if (highmap.containsKey(key2)) continue;
                        if (lowmap.containsKey(key2)) {
                            double ds = Double.parseDouble(lowmap.get(key2).split("\t")[3]);
                            if (deltascore > ds) {
                                lowmap.put(key2, content2);
                            }
                        } else {
                            lowmap.put(key2, content2);
                        }
                    }
                }
            }
        }
        for (String key : highmap.keySet()) {
//			writer.addContent(info+"\t"+sitemap.get(info), 2, format);
            writer.addContent(highmap.get(key), 4, format);
            writer.addContent(highmap.get(key), 3, format);
        }
        for (String key : lowmap.keySet()) {
//			writer.addContent(info+"\t"+sitemap.get(info), 2, format);
            writer.addContent(lowmap.get(key), 5, format);
            writer.addContent(lowmap.get(key), 3, format);
        }
        writer.close();
    }

    private static void pplSiteBatchWriter2(String dir,
            int sample) throws RowsExceededException, FileDamageException, WriteException, IOException
    {
        File file = new File(dir);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String path = files[i].getAbsolutePath();
            if (path.endsWith("ppl")) {
                String out = path.replace("ppl", "site.xls");
                etdpplSiteWriter(path, out, sample);
            }
        }
    }

    private static void etdpplSiteWriter(String in, String out,
            int sample) throws FileDamageException, IOException, RowsExceededException, WriteException
    {

        HashSet<String>[] identifiedSet = new HashSet[2];
        identifiedSet[0] = new HashSet<String>();
        identifiedSet[0].add("S271");
        identifiedSet[0].add("S282");
        identifiedSet[0].add("S290");
        identifiedSet[0].add("S296");
        identifiedSet[0].add("S320");
        identifiedSet[0].add("S323");
        identifiedSet[0].add("S325");
        identifiedSet[0].add("S324");
        identifiedSet[0].add("T280");
        identifiedSet[0].add("T334");
        identifiedSet[0].add("S157");
        identifiedSet[0].add("S164");
        identifiedSet[0].add("S297");
        identifiedSet[0].add("T151");
        identifiedSet[0].add("T158");
        identifiedSet[0].add("T19");
        identifiedSet[0].add("T266");
        identifiedSet[0].add("T284");
        identifiedSet[0].add("T292");
        identifiedSet[0].add("T295");
        identifiedSet[0].add("T299");

        identifiedSet[1] = new HashSet<String>();
        identifiedSet[1].add("S148");
        identifiedSet[1].add("S153");
        identifiedSet[1].add("S162");
        identifiedSet[1].add("S176");
        identifiedSet[1].add("S187");
        identifiedSet[1].add("T114");
        identifiedSet[1].add("T115");
        identifiedSet[1].add("T138");
        identifiedSet[1].add("T142");
        identifiedSet[1].add("T145");
        identifiedSet[1].add("T152");
        identifiedSet[1].add("T156");
        identifiedSet[1].add("T157");
        identifiedSet[1].add("T163");
        identifiedSet[1].add("T166");
        identifiedSet[1].add("T182");
        identifiedSet[1].add("T186");
        identifiedSet[1].add("T188");

        double[] masses = new double[]{203.0794, 365.132198, 406.1587, 568.211571, 656.227614635, 730.264396, 947.32303127,
                859.306987635, 1021.359812635, 1312.45522927};
        String[] names = new String[]{"GalNAc", "Gal-GalNAc", "GlcNAc-GalNAc", "Gal-(GlcNAc-)GalNAc", "NeuAc-Gal-GalNAc", "Gal-(Gal-GlcNAc-)GalNAc",
                "NeuAc-Gal-(NeuAc-)GalNAc", "NeuAc-Gal-(GlcNAc-)GalNAc",
                "NeuAc-Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc"};

        ExcelWriter writer = new ExcelWriter(out, new String[]{"Peptides", "Sites", "Unique sites"});
        ExcelFormat format = ExcelFormat.normalFormat;

        StringBuilder sb1 = new StringBuilder();
        sb1.append("Scan\t");
        sb1.append("Peptide Mr\t");
        sb1.append("Ion Score\t");
        sb1.append("Delta Score\t");
        sb1.append("Length\t");
        sb1.append("Proteins\t");
        sb1.append("Protein\t");
        sb1.append("Modified sequence\t");
        sb1.append("Glycan\t");

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Site\t");
        sb2.append("Sequence around\t");
        sb2.append("Glycan\t");
        sb2.append("Proteins\t");
        sb2.append("Protein\t");
        sb2.append("Scan\t");
        sb2.append("Modified sequence\t");

        writer.addTitle(sb1.toString(), 0, format);
        writer.addTitle(sb2.toString(), 1, format);
        writer.addTitle(sb2.toString(), 2, format);

//		HashSet<String> siteset = new HashSet<String>();
        HashMap<String, Double> sitemap = new HashMap<String, Double>();
        PeptideListReader reader = new PeptideListReader(in);
        AminoacidModification aam = reader.getSearchParameter().getVariableInfo();
        Modif[] modifs = aam.getModifications();
        HashMap<Character, String> modnamemap = new HashMap<Character, String>();
        for (int i = 0; i < modifs.length; i++) {
            modnamemap.put(modifs[i].getSymbol(), modifs[i].getName());
        }
        ProteinNameAccesser accesser = reader.getProNameAccesser();
        MascotPeptide peptide = null;
        while ((peptide = (MascotPeptide) reader.getPeptide()) != null) {

//			if(Math.abs(peptide.getDeltaMZppm())>10) continue;
            peptide.reCal4PValue(0.05f);
            if (peptide.getPrimaryScore() < 15) continue;
            if (peptide.getDeltaS() < 0.2) continue;
            if (peptide.getIonscore() < peptide.getIdenThres()) {
                continue;
            }
//			if(peptide.getEvalue()>0.05) continue;
			/*if(peptide.getHomoThres()==0){
				if(peptide.getIonscore()<peptide.getIdenThres()){
					continue;
				}
			}else{
				if(peptide.getIonscore()<peptide.getIdenThres() && peptide.getIonscore()<peptide.getHomoThres()){
					continue;
				}
			}*/

            if (peptide.getRank() > 1) continue;
            String sequence = peptide.getSequence();
            boolean glycan = false;

            HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();
            HashSet<ProteinReference> refset = peptide.getProteinReferences();
            Iterator<ProteinReference> refit = refset.iterator();
            int beg = 0;
            SeqLocAround sla = null;
            String ref = "";
            int sampleType = 0;
            if (refit.hasNext()) {
                ProteinReference proref = refit.next();
                sla = slamap.get(proref.toString());
                beg = sla.getBeg();
                ref = accesser.getProInfo(proref.getName()).getRef();
                if (sampleType == 0) {
                    if (ref.contains("sp0003") || ref.contains("sp0004")) {
                        sampleType = 0;
                    }
                    if (ref.contains("sp0006")) {
                        sampleType = 1;
                    }
                }
            }
            if (sampleType != sample)
                continue;

            StringBuilder sb = new StringBuilder();
            StringBuilder usb = new StringBuilder();
            HashMap<String, String> tempSiteMap = new HashMap<String, String>();

            for (int i = 2; i < sequence.length() - 2; i++) {
                char aa = sequence.charAt(i);
                if (aa >= 'A' && aa <= 'Z') {
                    sb.append(aa);
                    usb.append(aa);
                }
				/*else if(aa=='@'){
					sb.append("[Gal-GalNAc]");
					glycan = true;
					String site = usb.charAt(usb.length()-1)+""+(beg+usb.length()-1);
					tempSiteMap.put(site, "Gal-GalNAc");
				}else if(aa=='$'){
					sb.append("[NeuAc-Gal-(NeuAc-)GalNAc]");
					glycan = true;
					String site = usb.charAt(usb.length()-1)+""+(beg+usb.length()-1);
					tempSiteMap.put(site, "NeuAc-Gal-(NeuAc-)GalNAc");
				}else if(aa==']'){
					sb.append("[NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc]");
					glycan = true;
					String site = usb.charAt(usb.length()-1)+""+(beg+usb.length()-1);
					tempSiteMap.put(site, "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc");
				}else if(aa=='~'){
					sb.append("[Gal-(Gal-GlcNAc-)GalNAc]");
					glycan = true;
					String site = usb.charAt(usb.length()-1)+""+(beg+usb.length()-1);
					tempSiteMap.put(site, "Gal-(Gal-GlcNAc-)GalNAc");
				}else if(aa=='^'){
					sb.append("[NeuAc-Gal-GalNAc]");
					glycan = true;
					String site = usb.charAt(usb.length()-1)+""+(beg+usb.length()-1);
					tempSiteMap.put(site, "NeuAc-Gal-GalNAc");
				}else if(aa=='['){
					sb.append("[NeuAc-Gal-(Gal-GlcNAc-)GalNAc]");
					glycan = true;
					String site = usb.charAt(usb.length()-1)+""+(beg+usb.length()-1);
					tempSiteMap.put(site, "NeuAc-Gal-(Gal-GlcNAc-)GalNAc");
				}*/
                else {
                    double addmass = aam.getAddedMassForModif(aa);
                    for (int l = 0; l < masses.length; l++) {
                        if (Math.abs(addmass - masses[l]) < 0.1) {
                            sb.append("[" + names[l] + "]");
                            glycan = true;
                            String site = usb.charAt(usb.length() - 1) + "" + (beg + usb.length() - 1);
                            if (identifiedSet[sample].contains(site))
                                tempSiteMap.put(site, names[l]);
                            break;
                        }
                    }
                }
            }
//			if(glycan==false || tempSiteMap.size()==0) continue;
            if (glycan == false) continue;

            String scan = peptide.getBaseName();
            StringBuilder content = new StringBuilder();
            content.append(peptide.getScanNum()).append("\t");
            content.append(peptide.getMr()).append("\t");
            content.append(peptide.getPrimaryScore()).append("\t");
            content.append(peptide.getDeltaS()).append("\t");
            content.append(usb.length()).append("\t");
            content.append(ref).append("\t");
            content.append(sb).append("\t");
            writer.addContent(content.toString(), 0, format);

            Iterator<String> it = tempSiteMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = tempSiteMap.get(key);
                String content2 = key + "\t" + value + "\t" + ref;
                writer.addContent(content2, 1, format);
//				siteset.add(content2);
                if (sitemap.containsKey(content2)) {
                    if (peptide.getPrimaryScore() > sitemap.get(content2)) {
                        sitemap.put(content2, (double) peptide.getPrimaryScore());
                    }
                } else {
                    sitemap.put(content2, (double) peptide.getPrimaryScore());
                }
            }
        }

        for (String info : sitemap.keySet()) {
            writer.addContent(info + "\t" + sitemap.get(info), 2, format);
        }

        reader.close();
        writer.close();
    }

    private static void pplSiteWriterTD(String in,
            String out) throws FileDamageException, IOException, RowsExceededException, WriteException
    {

        ExcelWriter writer = new ExcelWriter(out, new String[]{"Peptides", "Sites", "Unique sites"});
        ExcelFormat format = ExcelFormat.normalFormat;

        StringBuilder sb1 = new StringBuilder();
        sb1.append("Scan\t");
        sb1.append("Peptide Mr\t");
        sb1.append("Ion Score\t");
        sb1.append("Length\t");
        sb1.append("Proteins\t");
        sb1.append("Protein\t");
        sb1.append("Modified sequence\t");
        sb1.append("Glycan\t");

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Site\t");
        sb2.append("Sequence around\t");
        sb2.append("Glycan\t");
        sb2.append("Proteins\t");
        sb2.append("Protein\t");
        sb2.append("Scan\t");
        sb2.append("Modified sequence\t");

        writer.addTitle(sb1.toString(), 0, format);
        writer.addTitle(sb2.toString(), 1, format);
        writer.addTitle(sb2.toString(), 2, format);

        int[][] threshold = new int[50][2];

        HashSet<String> siteset = new HashSet<String>();
        PeptideListReader reader = new PeptideListReader(in);
        ProteinNameAccesser accesser = reader.getProNameAccesser();
        ArrayList<MascotPeptide> peplist = new ArrayList<MascotPeptide>();
        MascotPeptide pep = null;
        while ((pep = (MascotPeptide) reader.getPeptide()) != null) {

//			if(Math.abs(pep.getDeltaMZppm())>10) continue;
            if (pep.getPrimaryScore() < pep.getHomoThres()) continue;
            if (pep.getRank() > 1) continue;

            peplist.add(pep);
            double score = pep.getIonscore();
            if (pep.isTP()) {
                for (int i = 0; i < threshold.length; i++) {
                    if (score < i) break;
                    threshold[i][0]++;
                }
            } else {
                for (int i = 0; i < threshold.length; i++) {
                    if (score < i) break;
                    threshold[i][1]++;
                }
            }
        }
        int thres = 50;
        for (int i = 0; i < threshold.length; i++) {
            double fdr = (double) threshold[i][1] / (double) threshold[i][0];
            if (fdr < 0.01) {
                thres = i;
                break;
            }
        }
        System.out.println("Ion score threshold:" + thres);

        double[] masses = new double[]{365.132198, 656.227614635, 947.32303127, 859.306987635, 1021.359812635, 1312.45522927};
        String[] names = new String[]{"Gal-GalNAc", "NeuAc-Gal-GalNAc", "NeuAc-Gal-(NeuAc-)GalNAc", "NeuAc-Gal-(GlcNAc-)GalNAc",
                "NeuAc-Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc"};
        AminoacidModification aam = reader.getSearchParameter().getVariableInfo();

        for (int i = 0; i < peplist.size(); i++) {

            MascotPeptide peptide = peplist.get(i);
            if (!peptide.isTP() || peptide.getIonscore() < thres) continue;
            String sequence = peptide.getSequence();
            boolean glycan = false;

            HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();
            HashSet<ProteinReference> refset = peptide.getProteinReferences();
            Iterator<ProteinReference> refit = refset.iterator();
            int beg = 0;
            SeqLocAround sla = null;
            String ref = "";
            if (refit.hasNext()) {
                ProteinReference proref = refit.next();
                sla = slamap.get(proref.toString());
                beg = sla.getBeg();
                ref = accesser.getProInfo(proref.getName()).getRef();
            }

            StringBuilder sb = new StringBuilder();
            StringBuilder usb = new StringBuilder();
            HashMap<String, String> tempSiteMap = new HashMap<String, String>();

            for (int j = 2; j < sequence.length() - 2; j++) {
                char aa = sequence.charAt(j);
                if (aa >= 'A' && aa <= 'Z') {
                    sb.append(aa);
                    usb.append(aa);
                } else {
                    double addmass = aam.getAddedMassForModif(aa);
                    for (int k = 0; k < masses.length; k++) {
                        if (Math.abs(addmass - masses[k]) < 0.1) {
                            sb.append("[" + names[k] + "]");
                            glycan = true;
                            String site = usb.charAt(usb.length() - 1) + "" + (beg + usb.length() - 1);
                            tempSiteMap.put(site, names[k]);
                            break;
                        }
                    }
                }
            }
            if (glycan == false) continue;

            String scan = peptide.getBaseName();
            StringBuilder content = new StringBuilder();
            content.append(peptide.getScanNum()).append("\t");
            content.append(peptide.getMr()).append("\t");
            content.append(peptide.getPrimaryScore()).append("\t");
            content.append(usb.length()).append("\t");
            content.append(ref).append("\t");
            content.append(sb).append("\t");
            writer.addContent(content.toString(), 0, format);

            Iterator<String> it = tempSiteMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = tempSiteMap.get(key);
                String content2 = key + "\t" + value + "\t" + ref;
                writer.addContent(content2, 1, format);
                siteset.add(content2);
            }
        }

        Iterator<String> it = siteset.iterator();
        while (it.hasNext()) {
            writer.addContent(it.next(), 2, format);
        }
        reader.close();
        writer.close();
    }

    private static void pplSiteWriterOnly(String in,
            String out) throws FileDamageException, IOException, RowsExceededException, WriteException
    {

        ExcelWriter writer = new ExcelWriter(out, new String[]{"Peptides", "Sites", "Unique sites"});
        ExcelFormat format = ExcelFormat.normalFormat;

        StringBuilder sb1 = new StringBuilder();
        sb1.append("Scan\t");
        sb1.append("Peptide Mr\t");
        sb1.append("Ion Score\t");
        sb1.append("Length\t");
        sb1.append("Proteins\t");
        sb1.append("Protein\t");
        sb1.append("Modified sequence\t");
        sb1.append("Glycan\t");

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Site\t");
        sb2.append("Sequence around\t");
        sb2.append("Glycan\t");
        sb2.append("Proteins\t");
        sb2.append("Protein\t");
        sb2.append("Scan\t");
        sb2.append("Modified sequence\t");

        writer.addTitle(sb1.toString(), 0, format);
        writer.addTitle(sb2.toString(), 1, format);
        writer.addTitle(sb2.toString(), 2, format);

        HashSet<String> siteset = new HashSet<String>();
        PeptideListReader reader = new PeptideListReader(in);
        ProteinNameAccesser accesser = reader.getProNameAccesser();
        IPeptide peptide = null;
        while ((peptide = reader.getPeptide()) != null) {

            if (peptide.getPrimaryScore() < 15) continue;
            if (peptide.getRank() > 1) continue;
            String sequence = peptide.getSequence();
            boolean glycan = false;

            HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();
            HashSet<ProteinReference> refset = peptide.getProteinReferences();
            Iterator<ProteinReference> refit = refset.iterator();
            int beg = 0;
            SeqLocAround sla = null;
            String ref = "";
            if (refit.hasNext()) {
                ProteinReference proref = refit.next();
                sla = slamap.get(proref.toString());
                beg = sla.getBeg();
                ref = accesser.getProInfo(proref.getName()).getRef();
            }

            StringBuilder sb = new StringBuilder();
            StringBuilder usb = new StringBuilder();
            HashMap<String, String> tempSiteMap = new HashMap<String, String>();
            int stcount = 0;

            for (int i = 2; i < sequence.length() - 2; i++) {
                char aa = sequence.charAt(i);
                if (aa >= 'A' && aa <= 'Z') {
                    if (aa == 'S' || aa == 'T') {
                        stcount++;
                    }
                    sb.append(aa);
                    usb.append(aa);
                } else if (aa == '@') {
                    sb.append("[Gal-GalNAc]");
                    glycan = true;
                    String site = usb.charAt(usb.length() - 1) + "" + (beg + usb.length() - 1);
                    tempSiteMap.put(site, "Gal-GalNAc");
                } else if (aa == '$') {
                    sb.append("[NeuAc-Gal-(NeuAc-)GalNAc]");
                    glycan = true;
                    String site = usb.charAt(usb.length() - 1) + "" + (beg + usb.length() - 1);
                    tempSiteMap.put(site, "NeuAc-Gal-(NeuAc-)GalNAc");
                } else if (aa == ']') {
                    sb.append("[NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc]");
                    glycan = true;
                    String site = usb.charAt(usb.length() - 1) + "" + (beg + usb.length() - 1);
                    tempSiteMap.put(site, "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc");
                } else if (aa == '~') {
                    sb.append("[Gal-(Gal-GlcNAc-)GalNAc]");
                    glycan = true;
                    String site = usb.charAt(usb.length() - 1) + "" + (beg + usb.length() - 1);
                    tempSiteMap.put(site, "Gal-(Gal-GlcNAc-)GalNAc");
                } else if (aa == '^') {
                    sb.append("[NeuAc-Gal-GalNAc]");
                    glycan = true;
                    String site = usb.charAt(usb.length() - 1) + "" + (beg + usb.length() - 1);
                    tempSiteMap.put(site, "NeuAc-Gal-GalNAc");
                } else if (aa == '[') {
                    sb.append("[NeuAc-Gal-(Gal-GlcNAc-)GalNAc]");
                    glycan = true;
                    String site = usb.charAt(usb.length() - 1) + "" + (beg + usb.length() - 1);
                    tempSiteMap.put(site, "NeuAc-Gal-(Gal-GlcNAc-)GalNAc");
                }
            }
            if (glycan == false) continue;
            if (stcount > 1) continue;

            String scan = peptide.getBaseName();
            StringBuilder content = new StringBuilder();
            content.append(peptide.getScanNum()).append("\t");
            content.append(peptide.getMr()).append("\t");
            content.append(peptide.getPrimaryScore()).append("\t");
            content.append(usb.length()).append("\t");
            content.append(ref).append("\t");
            content.append(sb).append("\t");
            writer.addContent(content.toString(), 0, format);

            Iterator<String> it = tempSiteMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = tempSiteMap.get(key);
                String content2 = key + "\t" + value + "\t" + ref;
                writer.addContent(content2, 1, format);
                siteset.add(content2);
            }
        }

        Iterator<String> it = siteset.iterator();
        while (it.hasNext()) {
            writer.addContent(it.next(), 2, format);
        }
        reader.close();
        writer.close();
    }

    private static void scoreLengthTest(String mgf, String in, String out) throws IOException, DtaFileParsingException
    {
        HashMap<String, String[]> contentmap = new HashMap<String, String[]>();
        HashMap<Integer, HashSet<String>> map = new HashMap<Integer, HashSet<String>>();
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] cs = line.split("\t");
            String scan = cs[0].substring(0, cs[0].lastIndexOf("."));
            int filenum = Integer.parseInt(cs[0].substring(cs[0].lastIndexOf(".") + 1));
            contentmap.put(cs[0], cs);
            if (map.containsKey(filenum)) {
                map.get(filenum).add(scan);
            } else {
                HashSet<String> set = new HashSet<String>();
                set.add(scan);
                map.put(filenum, set);
            }
        }
        reader.close();
        PrintWriter writer = new PrintWriter(out);
        File[] files = (new File(mgf)).listFiles(new FileFilter()
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
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {

            HashSet<String> set = map.get(i + 1);
            if (set == null) continue;
            MgfReader mr = new MgfReader(files[i]);
            MS2Scan ms2scan = null;

            while ((ms2scan = mr.getNextMS2Scan()) != null) {

                IMS2PeakList peaklist = ms2scan.getPeakList();
                PrecursePeak pp = peaklist.getPrecursePeak();
                double mz = pp.getMz();
                short charge = pp.getCharge();
                String name = ms2scan.getScanName().getScanName();
                if (name.endsWith(", "))
                    name = name.substring(0, name.length() - 2);

                if (set.contains(name)) {
                    String key = name + "." + (i + 1);
                    String[] cs = contentmap.get(key);
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < cs.length; j++) {
                        sb.append(cs[j]).append("\t");
                    }
                    sb.append(peaklist.getPeakArray().length).append("\t");
                    sb.append(mz).append("\t");
                    sb.append(charge).append("\t");
                    writer.println(sb);
                }
            }
        }
        writer.close();
    }

    private static void intensityTest(String ppl,
            String mgf) throws FileDamageException, IOException, DtaFileParsingException
    {

        HashMap<String, IPeak[]> peakmap = new HashMap<String, IPeak[]>();
        MgfReader mr = new MgfReader(mgf);
        MS2Scan ms2scan = null;
        while ((ms2scan = mr.getNextMS2Scan()) != null) {
            IMS2PeakList peaklist = ms2scan.getPeakList();
            String name = ms2scan.getScanName().getScanName();
            if (name.endsWith(", "))
                name = name.substring(0, name.length() - 2);
            peakmap.put(name, peaklist.getPeakArray());
        }
        mr.close();

        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        AminoacidModification aam = new AminoacidModification();
        aam.addModification('*', 0.984, "de");
        aam.addModification('#', 15.9949, "ox");
        AminoacidFragment aaf = new AminoacidFragment(aas, aam);
        int[] types = new int[]{Ion.TYPE_B, Ion.TYPE_Y};

        PeptideListReader reader = new PeptideListReader(ppl);
        IPeptide peptide = null;
        while ((peptide = reader.getPeptide()) != null) {
            if (peptide.getPrimaryScore() < 15) continue;
            String scanname = peptide.getBaseName();
            String sequence = peptide.getSequence();
            int charge = peptide.getCharge();
            IPeak[] peaks = peakmap.get(scanname);
            Arrays.sort(peaks);
            double[] peakmz = new double[peaks.length];
            double[] peakintensity = new double[peaks.length];
            for (int i = 0; i < peaks.length; i++) {
                peakmz[i] = peaks[i].getMz();
                peakintensity[i] = peaks[i].getIntensity();
            }
            Arrays.sort(peakintensity);

            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < sequence.length() - 2; i++) {
                char aa = sequence.charAt(i);
                if (aa >= 'A' && aa <= 'Z') {
                    sb.append(aa);
                } else if (aa == '*') {
                    sb.append(aa);
                } else if (aa == '#') {
                    sb.append(aa);
                }
            }

            Ions ions = aaf.fragment(sb.toString(), types, true);
            Ion[] bys = ions.getTotalIons();
            double[] intensity1 = new double[bys.length];
            double[] intensity2 = new double[bys.length];
            double[] intensity3 = new double[bys.length];
            for (int i = 0; i < bys.length; i++) {
                double mz1 = bys[i].getMzVsCharge(1);
                int id1 = Arrays.binarySearch(peakmz, mz1 - 0.1);
                if (id1 < 0) id1 = -id1 - 1;
                for (int j = id1; j < peaks.length; j++) {
                    if (Math.abs(peakmz[j] - mz1) < 0.1) {
                        if (peaks[j].getIntensity() > intensity1[i]) {
                            intensity1[i] = peaks[j].getIntensity();
                        }
                    } else if (peakmz[j] - mz1 > 0.1) {
                        break;
                    }
                }

                double mz2 = bys[i].getMzVsCharge(2);
                int id2 = Arrays.binarySearch(peakmz, mz2 - 0.1);
                if (id2 < 0) id2 = -id2 - 1;
                for (int j = id2; j < peaks.length; j++) {
                    if (Math.abs(peakmz[j] - mz2) < 0.1) {
                        if (peaks[j].getIntensity() > intensity2[i]) {
                            intensity2[i] = peaks[j].getIntensity();
                        }
                    } else if (peakmz[j] - mz2 > 0.1) {
                        break;
                    }
                }

                if (charge > 3) {
                    double mz3 = bys[i].getMzVsCharge(3);
                    int id3 = Arrays.binarySearch(peakmz, mz3 - 0.1);
                    if (id3 < 0) id3 = -id3 - 1;
                    for (int j = id3; j < peaks.length; j++) {
                        if (Math.abs(peakmz[j] - mz3) < 0.1) {
                            if (peaks[j].getIntensity() > intensity3[i]) {
                                intensity3[i] = peaks[j].getIntensity();
                            }
                        } else if (peakmz[j] - mz3 > 0.1) {
                            break;
                        }
                    }
                }
            }

            ArrayList<Double> list = new ArrayList<Double>();
            for (int i = 0; i < bys.length; i++) {
                if (intensity1[i] > 0) {
                    list.add(intensity1[i]);
                }
                if (intensity2[i] > 0) {
                    list.add(intensity2[i]);
                }
                if (intensity3[i] > 0) {
                    list.add(intensity3[i]);
                }
            }
            if (list.size() == 0) continue;
            Double[] matchedIntensity = list.toArray(new Double[list.size()]);
            Arrays.sort(matchedIntensity);

            int id204 = Arrays.binarySearch(peakmz, 203.986649);
            if (id204 < 0) id204 = -id204 - 1;
            double intensity204 = 0;
            for (int i = id204; i < peaks.length; i++) {
                if (Math.abs(peakmz[i] - 204.086649) < 0.1) {
                    if (peaks[i].getIntensity() > intensity204) {
                        intensity204 = peaks[i].getIntensity();
                    }
                } else if (peakmz[i] > 204.186649) {
                    break;
                }
            }

            double aveIntensity = MathTool.getAve(matchedIntensity);
            double rsd1 = 0;
            double rsd2 = 0;
            double rsd3 = 0;
            ArrayList<Double> tempIntensity = new ArrayList<Double>();
            for (int i = 0; i < peakintensity.length; i++) {
                tempIntensity.add(peakintensity[i]);
                if (peakintensity[i] > intensity204) {
                    if (rsd1 == 0) {
                        rsd1 = MathTool.getRSDInDouble(tempIntensity);
                    }
                }
                if (peakintensity[i] > matchedIntensity[0]) {
                    if (rsd2 == 0) {
                        rsd2 = MathTool.getRSDInDouble(tempIntensity);
                    }
                }
                if (peakintensity[i] > aveIntensity) {
                    if (rsd3 == 0) {
                        rsd3 = MathTool.getRSDInDouble(tempIntensity);
                    }
                }
                if (rsd1 > 0 && rsd2 > 0 && rsd3 > 0) {
                    break;
                }
            }
            System.out.println(peaks.length + "\t" + peakintensity[0] + "\t" + peakintensity[peakintensity.length - 1] + "\t" +
                    matchedIntensity[0] + "\t" + MathTool.getAve(matchedIntensity) + "\t" + intensity204 + "\t" + rsd1 + "\t" + rsd2 + "\t" + rsd3);
        }
        reader.close();
    }

    private static void mascotResult(String in, String out) throws FileDamageException, IOException
    {
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        PeptideListReader reader = new PeptideListReader(in);
        IPeptide peptide = null;
        while ((peptide = reader.getPeptide()) != null) {
            double score = peptide.getPrimaryScore();
            if (score < 20) continue;
            String sequence = peptide.getSequence();
            HashMap<String, SeqLocAround> locmap = peptide.getPepLocAroundMap();
            HashSet<ProteinReference> proset = peptide.getProteinReferences();
            Iterator<ProteinReference> proit = proset.iterator();
            String ref = null;
            if (proit.hasNext()) {
                ref = proit.next().toString();
            }
            SeqLocAround sla = locmap.get(ref);
            int begin = sla.getBeg() - 1;
            for (int i = 2; i < sequence.length() - 2; i++) {
                char aa = sequence.charAt(i);
                if (aa >= 'A' && aa <= 'Z') {
                    begin++;
                } else if (aa == '@') {
                    String key = ref + "\t" + begin;
                    if (map.containsKey(key)) {
                        map.get(key).add("Gal-GalNAc");
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add("Gal-GalNAc");
                        map.put(key, set);
                    }
                } else if (aa == '$') {
                    String key = ref + "\t" + begin;
                    if (map.containsKey(key)) {
                        map.get(key).add("NeuAc-Gal-(NeuAc-)GalNAc");
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add("NeuAc-Gal-(NeuAc-)GalNAc");
                        map.put(key, set);
                    }
                } else if (aa == ']') {
                    String key = ref + "\t" + begin;
                    if (map.containsKey(key)) {
                        map.get(key).add("NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc");
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add("NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc");
                        map.put(key, set);
                    }
                } else if (aa == '~') {
                    String key = ref + "\t" + begin;
                    if (map.containsKey(key)) {
                        map.get(key).add("Gal-(Gal-GlcNAc-)GalNAc");
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add("Gal-(Gal-GlcNAc-)GalNAc");
                        map.put(key, set);
                    }
                } else if (aa == '^') {
                    String key = ref + "\t" + begin;
                    if (map.containsKey(key)) {
                        map.get(key).add("NeuAc-Gal-GalNAc");
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add("NeuAc-Gal-GalNAc");
                        map.put(key, set);
                    }
                } else if (aa == '[') {
                    String key = ref + "\t" + begin;
                    if (map.containsKey(key)) {
                        map.get(key).add("NeuAc-Gal-(Gal-GlcNAc-)GalNAc");
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add("NeuAc-Gal-(Gal-GlcNAc-)GalNAc");
                        map.put(key, set);
                    }
                }
            }
        }
        reader.close();
        System.out.println(map.size());
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            HashSet<String> set = map.get(key);
            Iterator<String> it1 = set.iterator();
            while (it1.hasNext()) {
                String value = it1.next();
                System.out.println(key + "\t" + value);
            }
        }
    }

    private static void markIonTest(String pepinfo, String result) throws IOException, JXLException
    {

        HashMap<String, int[]> infomap = new HashMap<String, int[]>();
        BufferedReader reader = new BufferedReader(new FileReader(pepinfo));
        String line = null;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            int[] mark = info.getMarkpeaks();
            String scanname = info.getScanname();
            infomap.put(scanname, mark);
        }
        reader.close();

        int[] all = new int[7];
        ExcelReader er = new ExcelReader(result);
        String[] cs = er.readLine();
        while ((cs = er.readLine()) != null) {//System.out.println(cs[0]);
            int[] mark = infomap.get(cs[0]);
			/*for(int i=0;i<mark.length;i++){
				all[i]+=mark[i];
				System.out.print(mark[i]+"\t");
			}
			System.out.println();*/
            if (!cs[6].contains("GlcNAc")) {
                StringBuilder ssssss = new StringBuilder();
                for (int i = 0; i < mark.length; i++) {
                    all[i] += mark[i];
                    ssssss.append(mark[i]).append("\t");
//					System.out.print(mark[i]+"\t");
                }
//				System.out.println();
                if (mark[4] + mark[5] + mark[6] > 0) {
                    System.out.println(cs[0] + "\t" + ssssss);
                }
            }
        }
        er.close();

        for (int i = 0; i < all.length; i++) {
            System.out.print(all[i] + "\t");
        }
    }

    private static void pplReader(String ppl) throws FileDamageException, IOException
    {

        HashMap<String, Double> scoreMap = new HashMap<String, Double>();
        PeptideListReader reader = new PeptideListReader(ppl);
        IPeptide peptide = null;
        while ((peptide = reader.getPeptide()) != null) {

            String scanname = peptide.getBaseName();
            if (scanname.endsWith(", ")) scanname = scanname.substring(0, scanname.length() - 2);

            if (peptide.getPrimaryScore() < 15)
                continue;

            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            if (scoreMap.containsKey(oriScanname)) {
                if (peptide.getPrimaryScore() > scoreMap.get(oriScanname)) {
                    scoreMap.put(oriScanname, (double) peptide.getPrimaryScore());
                }
            } else {
                scoreMap.put(oriScanname, new Double(peptide.getPrimaryScore()));
            }
        }
        reader.close();
        System.out.println(scoreMap.size());
        for (String key : scoreMap.keySet()) {
            System.out.println(key + "\t" + scoreMap.get(key));
        }
    }

    private static void caocaocao(String mgf,
            String ppl) throws DtaFileParsingException, FileDamageException, IOException
    {
        HashSet<String> set = new HashSet<String>();
        MgfReader mr = new MgfReader(mgf);
        MS2Scan scan = null;
        while ((scan = mr.getNextMS2Scan()) != null) {
            System.out.println(scan.getScanName().getBaseName());
            set.add(scan.getScanName().getBaseName());
        }
        mr.close();

        PeptideListReader pr = new PeptideListReader(ppl);
        IPeptide pep = null;
        while ((pep = pr.getPeptide()) != null) {
            String basename = pep.getBaseName();
            if (!set.contains(basename)) {
                System.out.println("cao!\t" + basename);
            }
        }
        pr.close();
    }

    private static void combineResult(String[] results) throws IOException, JXLException
    {
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        HashSet<String> pepset = new HashSet<String>();
        for (int i = 0; i < results.length; i++) {
            File filei = new File(results[i]);
            if (filei.isDirectory()) {
                File[] filesj = filei.listFiles();
                for (int j = 0; j < filesj.length; j++) {
                    if (!filesj[j].getName().endsWith("xls"))
                        continue;

                    ExcelReader reader = new ExcelReader(filesj[j], new int[]{0, 1, 2});
                    String[] line = reader.readLine(2);
                    while ((line = reader.readLine(2)) != null) {
                        String key = line[0] + "\t" + line[2];
                        if (map.containsKey(key)) {
                            map.get(key).add(line[1]);
                        } else {
                            HashSet<String> set = new HashSet<String>();
                            set.add(line[1]);
                            map.put(key, set);
                        }
                    }
                    line = reader.readLine(0);
                    while ((line = reader.readLine(0)) != null) {
                        pepset.add(line[5]);
                    }
                    reader.close();
                }
            } else if (filei.isFile()) {
                ExcelReader reader = new ExcelReader(results[i], new int[]{0, 1, 2});
                String[] line = reader.readLine(2);
                while ((line = reader.readLine(2)) != null) {
                    String key = line[0] + "\t" + line[2];
                    if (map.containsKey(key)) {
                        map.get(key).add(line[1]);
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add(line[1]);
                        map.put(key, set);
                    }
                }
                line = reader.readLine(0);
                while ((line = reader.readLine(0)) != null) {
                    pepset.add(line[5]);
                }
                reader.close();
            }
        }
        int count = 0;
        for (String key : map.keySet()) {
            count += map.get(key).size();
        }
        System.out.println(pepset.size() + "\t" + map.size() + "\t" + count);
    }

    private static void trueFalseTest(String ppl1, String ppl2) throws FileDamageException, IOException
    {
        int[][] count = new int[25][4];
        IPeptide peptide = null;
        PeptideListReader reader1 = new PeptideListReader(ppl1);
        HashMap<String, IPeptide> map1 = new HashMap<String, IPeptide>();
        while ((peptide = reader1.getPeptide()) != null) {
            String scanname = peptide.getBaseName();
            float score = peptide.getPrimaryScore();

            if (scanname.endsWith(", ")) scanname = scanname.substring(0, scanname.length() - 2);
//			String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            String oriScanname = scanname;

            if (map1.containsKey(oriScanname)) {
                if (score > map1.get(oriScanname).getPrimaryScore()) {
                    map1.put(oriScanname, peptide);
                }
            } else {
                map1.put(oriScanname, peptide);
            }
        }
        reader1.close();

        for (IPeptide pep : map1.values()) {
            float score = pep.getPrimaryScore();
            if (pep.isTP()) {
                for (int i = 0; i < 25; i++) {
                    if (i > score) break;
                    count[i][0]++;
                }
            } else {
                for (int i = 0; i < 25; i++) {
                    if (i > score) break;
                    count[i][1]++;
                }
            }
        }

        PeptideListReader reader2 = new PeptideListReader(ppl2);
        HashMap<String, IPeptide> map2 = new HashMap<String, IPeptide>();
        while ((peptide = reader2.getPeptide()) != null) {
            String scanname = peptide.getBaseName();
            float score = peptide.getPrimaryScore();

            if (scanname.endsWith(", ")) scanname = scanname.substring(0, scanname.length() - 2);
//			String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            String oriScanname = scanname;

            if (map2.containsKey(oriScanname)) {
                if (score > map2.get(oriScanname).getPrimaryScore()) {
                    map2.put(oriScanname, peptide);
                }
            } else {
                map2.put(oriScanname, peptide);
            }
        }
        reader2.close();

        for (IPeptide pep : map2.values()) {
            float score = pep.getPrimaryScore();
            if (pep.isTP()) {
                for (int i = 0; i < 25; i++) {
                    if (i > score) break;
                    count[i][2]++;
                }
            } else {
                for (int i = 0; i < 25; i++) {
                    if (i > score) break;
                    count[i][3]++;
                }
            }
        }

        System.out.println("Ion score threshold\tTT\tTD\tFT\tFD\tTD/TD\tFT/TT\tFD/FT");
        DecimalFormat df3 = DecimalFormats.DF0_3;
        for (int i = 0; i < count.length; i++) {
            double TDvsTT = (double) count[i][1] / (double) count[i][0];
            double FTvsTT = (double) count[i][2] / (double) count[i][0];
            double FDvsFT = (double) count[i][3] / (double) count[i][2];
            System.out.println(i + "\t" + count[i][0] + "\t" + count[i][1] + "\t" + count[i][2] + "\t" + count[i][3] + "\t" + df3.format(TDvsTT) + "\t" + df3.format(FTvsTT)
                    + "\t" + df3.format(FDvsFT));
        }
    }

    private static void etdCombine(String in) throws IOException, JXLException
    {
        HashSet<String> set1 = new HashSet<String>();
        HashSet<String> set2 = new HashSet<String>();
        File[] files = (new File(in)).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].getName().endsWith("xls")) continue;

            ExcelReader reader = new ExcelReader(files[i], 2);
            String[] line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                set1.add(line[0] + "\t" + line[2]);
                set2.add(line[0] + "\t" + line[1] + "\t" + line[2]);
            }
            reader.close();
        }
        System.out.println(set1.size() + "\t" + set2.size());
    }

    private static void getAADis(String fasta) throws IOException
    {

        int[][] aacount = new int[13][26];
        FastaReader fr = new FastaReader(fasta);
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            String sequence = ps.getUniqueSequence();
            for (int i = 0; i < sequence.length(); i++) {
                char aa = sequence.charAt(i);
                if (aa == 'S' || aa == 'T') {
                    for (int j = i - 6; j < i + 7; j++) {
                        if (j >= 0 && j < sequence.length())
                            aacount[j - i + 6][sequence.charAt(j) - 65]++;
                    }
                }
            }
        }
        fr.close();
        double[][] normalRatio = new double[13][26];
        for (int i = 0; i < normalRatio.length; i++) {
            int total = MathTool.getTotal(aacount[i]);
            for (int j = 0; j < normalRatio[i].length; j++) {
                normalRatio[i][j] = (double) aacount[i][j] / (double) total;
            }
        }

        int[][] normalAaroundCount = new int[13][26];
        for (int j = 0; j < normalAaroundCount.length; j++) {
            for (int k = 0; k < normalAaroundCount[j].length; k++) {
                normalAaroundCount[j][k] = (int) (normalRatio[j][k] * 1000.0);
            }
        }

        StringBuilder[] sbs = new StringBuilder[13];
        for (int j = 0; j < 13; j++) {
            sbs[j] = new StringBuilder();
            for (int k = 0; k < normalAaroundCount[j].length; k++) {
                int aaaacount = normalAaroundCount[j][k];
                while (aaaacount > 0) {
                    sbs[j].append((char) (k + 65));
                    aaaacount--;
                }
            }
        }

        for (int j = 0; j < 1000; j++) {
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < 13; k++) {
                if (sbs[k].length() > j)
                    sb.append(sbs[k].charAt(j));
                else
                    sb.append('_');
            }
            System.out.println(sb);
        }
    }

    private static void sttest(String in) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String sequence = line[5];
            int count = 0;
            for (int i = 0; i < sequence.length(); i++) {
                if (sequence.charAt(i) == 'S' || sequence.charAt(i) == 'T') {
                    count++;
                }
            }
            if (count == 1) {
                System.out.println(sequence);
            }
        }
        reader.close();
    }

    private static void caocaocao2(String mcp, String my) throws IOException, JXLException
    {
        HashSet<String> set = new HashSet<String>();
        Pattern pattern = Pattern.compile("(\\w*)\\W.*([ST]\\d*[\\W])+.*");
        BufferedReader br = new BufferedReader(new FileReader(mcp));
        String line = null;
        while ((line = br.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                int count = matcher.groupCount();
//				System.out.println(count);
                System.out.println(matcher.group(1) + "\t" + matcher.group(2) + "\t" + matcher.group(0));
                set.add(matcher.group(1));
            } else {
                System.out.println(line);
            }
        }
        br.close();

        int count = 0;
        ExcelReader reader = new ExcelReader(my);
        String[] cs = reader.readLine();
        while ((cs = reader.readLine()) != null) {
            String ref = cs[4];
            String key = ref.split("\\|")[1];
            System.out.println(key);
            if (set.contains(key)) {
                set.remove(key);
                count++;
            }
        }
        reader.close();
        System.out.println(count + "\t" + set.size());
    }

    private static void proteinSelected(String in) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String ref = line[4];//System.out.println(ref);
            String glycan = line[2];
            if (ref.contains("Apolipoprotein E")) {
                System.out.println(glycan);
            }
        }
        reader.close();
    }

    private static void databaseCompare(String database, String result) throws IOException, JXLException
    {

        HashSet<String>[] sitesets = new HashSet[4];
        HashSet<String>[] prosets = new HashSet[4];

        for (int i = 0; i < prosets.length; i++) {
            sitesets[i] = new HashSet<String>();
            prosets[i] = new HashSet<String>();
        }
        BufferedReader br = new BufferedReader(new FileReader(database));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] cs = line.split("\t");
            if (!cs[0].endsWith("HUMAN")) continue;
            if (!cs[6].equals("S") && !cs[6].equals("T")) continue;
            String site = cs[6] + cs[2] + "\t" + cs[1];
            if (cs[5].startsWith("Swiss")) {
                sitesets[0].add(site);
                prosets[0].add(cs[1]);
            }
            if (cs[5].startsWith("OGlycBase")) {
                sitesets[1].add(site);
                prosets[1].add(cs[1]);
            }
            if (cs[5].startsWith("HPRD")) {
                sitesets[2].add(site);
                prosets[2].add(cs[1]);
            }
            sitesets[3].add(site);
            prosets[3].add(cs[1]);
        }
        br.close();

        System.out.println(sitesets[0].size() + "\t" + sitesets[1].size() + "\t" + sitesets[2].size() + "\t" + sitesets[3].size());
        System.out.println(prosets[0].size() + "\t" + prosets[1].size() + "\t" + prosets[2].size() + "\t" + prosets[3].size());

        int sitecount = 0;
        int[] sitefind = new int[4];
        int[] profind = new int[4];
        ExcelReader reader = new ExcelReader(result, 1);
        String[] cs = reader.readLine();
        while ((cs = reader.readLine()) != null) {
            sitecount++;
            String swiss = cs[2].substring(3, 9);
            String site = cs[0] + "\t" + swiss;
            for (int i = 0; i < sitesets.length; i++) {
                if (sitesets[i].contains(site)) {
                    sitefind[i]++;
                }
                if (prosets[i].contains(swiss)) {
                    profind[i]++;
                }
            }
        }
        reader.close();
        System.out.println(sitefind[0] + "\t" + sitefind[1] + "\t" + sitefind[2] + "\t" + sitefind[3]);
        System.out.println(profind[0] + "\t" + profind[1] + "\t" + profind[2] + "\t" + profind[3]);
        System.out.println(sitecount);
    }

    private static void compareLiter(String liter, String result) throws IOException, JXLException
    {
        HashSet<String> siteset = new HashSet<String>();
        HashSet<String> proset1 = new HashSet<String>();
        ExcelReader br = new ExcelReader(liter);
        String[] cs = br.readLine();
        while ((cs = br.readLine()) != null) {
            int begin = Integer.parseInt(cs[3]);
            int end = Integer.parseInt(cs[5]);
            proset1.add(cs[0]);

            String siteseq = "";
            int ambbegin = 0;
            int ambend = 0;
            boolean unamb = false;
            if (cs.length < 7) continue;

            if (cs[6].trim().length() > 0) {
                unamb = true;
                siteseq = cs[6];
            } else {
                if (cs[7].trim().length() == 0) continue;
                if (cs[7].contains("or")) {
                    unamb = true;
                    siteseq += "\t";
                    siteseq += cs[7];
                    continue;
                }

                unamb = false;
                String[] cs7 = cs[7].split("[-()]");

                ambbegin = Integer.parseInt(cs7[1]);
                ambend = Integer.parseInt(cs7[2]);
            }

            for (int i = 0; i < cs[4].length(); i++) {
                char aa = cs[4].charAt(i);
                if (aa == 'S' || aa == 'T') {
                    int loc = begin + i;
                    if (unamb) {
                        if (siteseq.contains(String.valueOf(loc))) {
                            String site = cs[0] + "\t" + aa + "" + loc;
                            siteset.add(site);
                        }
                    } else {
                        if (loc >= ambbegin && loc <= ambend) {
                            String site = cs[0] + "\t" + aa + "" + loc;
                            siteset.add(site);
                        }
                    }
                }
            }
        }
        br.close();

        System.out.println(siteset.size());

        HashSet<String> proset2 = new HashSet<String>();
        int sitecount = 0;
        int total = 0;
        ExcelReader reader = new ExcelReader(result, 1);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            total++;
            String swiss = line[2].substring(3, 9);
            proset2.add(swiss);
            String site = swiss + "\t" + line[0];
            if (siteset.contains(site)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < line.length; i++) {
                    sb.append(line[i]).append("\t");
                }
                System.out.println(sb);
                sitecount++;
            }
        }
        reader.close();
        System.out.println(sitecount + "\t" + total);

        HashSet<String> prototal = new HashSet<String>();
        prototal.addAll(proset1);
        prototal.addAll(proset2);
        System.out.println(proset1.size() + "\t" + proset2.size() + "\t" + prototal.size() + "\t" + (proset1.size() + proset2.size() - prototal.size()));
    }

    private static void writeSite(String result, String out) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(result);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {

        }
        reader.close();
    }

    private static void cao(String in) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            StringBuilder sb = new StringBuilder();
            boolean add = true;
            for (int i = 0; i < line[2].length(); i++) {
                char aa = line[2].charAt(i);
                if (aa == '[') {
                    add = false;
                } else if (aa == ']') {
                    add = true;
                } else {
                    if (add) {
                        sb.append(aa);
                    }
                }
            }
            System.out.println(sb.length());
        }
        reader.close();
    }

    private static void cao2(String in) throws IOException, JXLException
    {
        int[] count = new int[3];
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < line.length; i++) {
                sb.append(line[i]).append("\t");
            }

            String scan = line[0];
            scan = scan.substring(0, scan.lastIndexOf("."));
            if (scan.endsWith("1")) {
                count[0]++;
            } else if (scan.endsWith("2")) {
                count[1]++;
                System.out.println(sb);
            }
            count[2]++;
        }
        reader.close();
        System.out.println(count[0] + "\t" + count[1] + "\t" + count[2]);
    }

    private static void readSite(String in) throws IOException, JXLException
    {
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        File file = new File(in);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("xls")) {
                ExcelReader reader = new ExcelReader(files[i], 2);
                String[] line = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String key = line[0] + "\t" + line[2];
                    if (map.containsKey(key)) {
                        map.get(key).add(line[1]);
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add(line[1]);
                        map.put(key, set);
                    }
                }
            }
        }
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            StringBuilder sb = new StringBuilder(key + "\t");
            HashSet<String> values = map.get(key);
            for (String s : values) {
                sb.append(s).append("\t");
            }
            System.out.println(sb);
        }
    }

    private static void selectPNG(String in, String out, String result) throws IOException, JXLException
    {
        HashSet<String> set = new HashSet<String>();
        ExcelReader reader = new ExcelReader(result, 2);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            set.add(line[7].substring(line[7].indexOf(":") + 1));
        }
        reader.close();

        File[] files = (new File(in)).listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (name.endsWith("png")) {
                name = name.substring(0, name.length() - 4);
                System.out.println(name);
                if (set.contains(name)) {
                    String newpath = out + "\\" + name + ".png";
                    FileCopy.Copy(files[i], new File(newpath));
                }
            }
        }
    }

    private static void writePep(String ppl, String result) throws IOException, JXLException, FileDamageException
    {
        HashSet<String> set = new HashSet<String>();
        ExcelReader reader = new ExcelReader(result);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            set.add(line[0]);
//			System.out.println(line[0]);
        }
        reader.close();

        PeptideListReader pr = new PeptideListReader(ppl);
        IPeptide peptide = null;
        while ((peptide = pr.getPeptide()) != null) {
            String scan = peptide.getBaseName();
//			System.out.println(scan+"\t"+set.contains(scan));
            if (set.contains(scan)) {
                if (peptide.getRank() > 1) continue;
                StringBuilder sb = new StringBuilder();
                sb.append(scan).append("\t");
                sb.append(peptide.getSequence()).append("\t");
                sb.append(peptide.getDeltaMH()).append("\t");
                sb.append(peptide.getDeltaMZppm()).append("\t");
                System.out.println(sb);
            }
        }
        pr.close();
    }

    private static void etdCompare(String etd, String tof) throws IOException, JXLException
    {
        ExcelReader etdreader = new ExcelReader(etd);
        String[] line = etdreader.readLine();
        while ((line = etdreader.readLine()) != null) {

        }
        etdreader.close();
    }

    /**
     * @param args
     * @throws IOException
     * @throws DtaFileParsingException
     * @throws JXLException
     * @throws FileDamageException
     */
    public static void main(
            String[] args) throws IOException, DtaFileParsingException, JXLException, FileDamageException
    {
        // TODO Auto-generated method stub
//		OGlycanTest4.cao2("H:\\OGLYCAN\\OGlycan_20140615_efficiency\\before\\deglyco\\before.xls");
//		OGlycanTest4.cao("H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\proteinaseK.xls");
//		OGlycanTest4.compareLiter("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\SimpleCells_SI.xls",
//				"H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\201405018.xls");
//		OGlycanTest4.databaseCompare("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\dbPTM3.txt",
//				"H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\201405018.xls");
//		OGlycanTest4.caocaocao2("D:\\P\\o-glyco\\2014.05.12\\�½��ı��ĵ�.txt",
//				"H:\\OGLYCAN\\OGlycan_20140423_core14b\\20140502.xls");
//		OGlycanTest4.sttest("H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\mascot\\20140321_O-fetuin_proteinaseK_500cps_CES5_3.F002357.dat.site.xls");
//		OGlycanTest4.getMassRange("H:\\OGLYCAN\\OGlycan_20140402_ETD\\ETD\\fetuin\\20140401_O-fetuin_proteinaseK_ETD_1.mgf");
//		OGlycanTest4.extractMgf("H:\\OGLYCAN\\OGlycan_20140324\\fetuin.20140324.2.0\\fetuin.20140324.2.0.info",
//				"H:\\OGLYCAN\\OGlycan_20140324\\original data",
//				"H:\\OGLYCAN\\OGlycan_20140324\\fetuin.20140324.2.0\\fetuin.20140324.2.0.original.mgf");
//		OGlycanTest4.extractMgf("H:\\OGLYCAN\\OGlycan_20140324\\sp0003 match.txt",
//				"H:\\OGLYCAN\\OGlycan_20140324\\sp0003 match.txt",
//				"H:\\OGLYCAN\\OGlycan_20140324\\original data\\fetuin",
//				"H:\\OGLYCAN\\OGlycan_20140324\\fetuin scans.mgf");
//		OGlycanTest4.resultCompare("H:\\OGLYCAN\\OGlycan_20140405\\trypsin_20140405\\trypsin_20140405.xls",
//				"H:\\OGLYCAN\\OGlycan_20140423_core14b\\trypsin\\trypsin.xls");
//		OGlycanTest4.scoreLengthTest("H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\2D_trypsin",
//				"H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin\\2.0vs2.1.txt",
//				"H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin\\2.0vs2.1.result.txt");
//		OGlycanTest4.intensityTest("H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin\\trypsin.original3.F001884.dat.ppl",
//				"H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\trypsin\\trypsin.original3.mgf");
//		OGlycanTest4.mascotResult("H:\\OGLYCAN\\OGlycan_final_201403_mascot\\fetuin\\fetuin.alloriginal.F002225.dat.ppl", "");
//		OGlycanTest4.resultCompareMascot("H:\\OGLYCAN\\OGlycan_20140324\\fetuin.20140405.3\\fetuin.20140405.3.xls",
//				new String[]{"H:\\OGLYCAN\\OGlycan_20140324\\mascot\\20140321_O-fetuin_proteinaseK_500cps_CES5_1.F002354.dat.ppl",
//				"H:\\OGLYCAN\\OGlycan_20140324\\mascot\\20140321_O-fetuin_proteinaseK_500cps_CES5_2.F002355.dat.ppl",
//				"H:\\OGLYCAN\\OGlycan_20140324\\mascot\\20140321_O-fetuin_proteinaseK_500cps_CES5_3.F002357.dat.ppl"});
//		OGlycanTest4.markIonTest("H:\\OGLYCAN2\\20141024_15glyco\\20141031_fetuin\\20141031_fetuin.info",
//				"H:\\OGLYCAN2\\20141024_15glyco\\20141031_fetuin\\20141031_fetuin.xls");
//		OGlycanTest4.selectPNG("H:\\OGLYCAN2\\casein\\20140925_casein\\png",
//				"H:\\OGLYCAN2\\casein\\20140925_casein\\site_png",
//				"H:\\OGLYCAN2\\casein\\20140925_casein\\20140925_casein.xls");

//		OGlycanTest4.resultCompareMascotTD("H:\\OGLYCAN\\OGlycan_20140423_core14b\\trypsin\\trypsin.xls",
//				new String[]{"H:\\OGLYCAN\\OGlycan_20140423_core14b\\trypsin\\trypsin_20140405.extract.F002563.dat.ppl",
//				"H:\\OGLYCAN\\OGlycan_20140423_core14b\\trypsin\\trypsin_20140405.extract.F002564.dat.ppl",
//				"H:\\OGLYCAN\\OGlycan_20140423_core14b\\trypsin\\trypsin_20140405.extract.F002565.dat.ppl"},
//				"H:\\OGLYCAN\\OGlycan_20140423_core14b\\trypsin\\trypsin.compare.xls",
//				"F:\\DataBase\\uniprot\\final.uniprot-human-20131211_0.fasta");
//		OGlycanTest4.pplSiteBatchWriter("H:\\OGLYCAN2\\20141014");

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~2014.11.12

//		OGlycanTest4.resultCompareMascotExpect("H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\NoREV\\fetuin.xls",
//				"H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\fetuin.xls",
//				"H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\fetuin",
//				"H:\\OGLYCAN2\\20141211_14glyco\\fetuin\\fetuin compare Expect 20.xls");

        OGlycanTest4.pplSiteBatchWriter("H:\\OGLYCAN2\\20150116_ETHCD", 0, 7);
//		OGlycanTest4.pplSiteBatchWriter2("H:\\OGLYCAN2\\20141110_OGlycan_ETD\\casein", 1);
//		OGlycanTest4.readSite("H:\\OGLYCAN2\\HCD\\fetuin");
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~

//		OGlycanTest4.pplSiteWriter("H:\\OGLYCAN2\\20141014\\20141013-fetin-deSA_3ul-ETD.F003593.dat.ppl",
//				"H:\\OGLYCAN2\\20141014\\20141013-fetin-deSA_3ul-ETD.F003593.site.xls");
//		OGlycanTest4.pplSiteWriterTD("H:\\OGLYCAN\\OGlycan_20140402_ETD\\HCD\\serum\\20140402_serum_trypsin_HCD_2.F002542.dat.ppl",
//				"H:\\OGLYCAN\\OGlycan_20140402_ETD\\HCD\\serum\\20140402_serum_trypsin_HCD_2.F002542.site.xls");
//		OGlycanTest4.pplSiteWriterOnly("H:\\OGLYCAN\\OGlycan_20140324\\fetuin.new.20140408.combine\\fetuin.new.20140408.combine.oglycan.F002417.dat.ppl",
//				"H:\\OGLYCAN\\OGlycan_20140324\\fetuin.new.20140408.combine\\fetuin.new.20140408.combine.oglycan.F002417.site.only.xls");
//		OGlycanTest4.resultCompareMascotXls("H:\\OGLYCAN\\OGlycan_20140405\\fetuin_20140405\\fetuin_20140405.xls",
//				"H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\fetuin\\20120329_Fetuin_elastase_HILIC_5ug.combine.site.xls");
//		OGlycanTest4.extractMgfFromXls("H:\\OGLYCAN\\OGlycan_20140405\\trypsin_20140405\\trypsin_20140405.xls",
//				"H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\2D_trypsin",
//				"H:\\OGLYCAN\\OGlycan_20140405\\trypsin_20140405\\trypsin_20140405.extract.mgf");
//		OGlycanTest4.pplReader("H:\\OGLYCAN\\OGlycan_20140405\\fetuin_20140405\\fetuin_20140405.plus10.F002464.dat.ppl");
//		OGlycanTest4.combineResult(new String[]{"H:\\OGLYCAN\\OGlycan_20140402_ETD\\HCD\\serum"});
//		OGlycanTest4.trueFalseTest("H:\\OGLYCAN2\\20141024_15glyco\\2D_trypsin\\2D_trypsin.oglycan.F004469.dat.ppl",
//				"H:\\OGLYCAN2\\20141024_15glyco\\2D_trypsin\\plusTen\\2D_trypsin.oglycan.F004467.dat.ppl");
//		OGlycanTest4.etdCombine("H:\\OGLYCAN\\OGlycan_20140402_ETD\\ETD\\serum");
//		OGlycanTest4.getAADis("F:\\DataBase\\uniprot\\uniprot-human-20131211_0.fasta");
//		OGlycanTest4.proteinSelected("H:\\OGLYCAN\\OGlycan_20140518_combine\\no_homo\\no_homo.201405018.xls");

//		OGlycanTest4.writePep("H:\\OGLYCAN2\\20141103_trypsin\\trypsin_normal_deglyco\\trypsin_normal_deglyco.oglycan.F004347.dat.ppl",
//				"H:\\OGLYCAN2\\20141103_trypsin\\trypsin_normal_deglyco\\trypsin_normal_deglyco.xls");
    }

}
