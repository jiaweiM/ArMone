package cn.ac.dicp.gp1809.glyco.Quan.label;

import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import jxl.JXLException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 */
public class GlycoPaperDataProcessor
{

    private static void compareMutilple(String[] results, String out, int sheetid) throws IOException, JXLException
    {
        ExcelWriter writer = new ExcelWriter(out);
        ExcelFormat format = ExcelFormat.normalFormat;
        String title = "Sequence\tGlycan\tReference\tSites\tReplicate count\tRatio 1\tRatio 2\tAve\tRSD\tRatio 1\tRatio 2\tAve\tRSD";
        writer.addTitle(title, 0, format);

        HashMap<String, HashSet<String>> refmap = new HashMap<String, HashSet<String>>();
        HashMap<String, HashSet<String>> sitemap = new HashMap<String, HashSet<String>>();
        HashMap<String, double[]>[] ratiomaps = new HashMap[results.length];
        HashSet<String> totalset = new HashSet<String>();
        for (int i = 0; i < results.length; i++) {
            ratiomaps[i] = new HashMap<String, double[]>();
            ExcelReader reader = new ExcelReader(results[i], sheetid);
            String[] line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String key = line[0] + "\t" + line[3];
                if (line.length == 19) {
                    String[] refs = line[6].split(";");
                    String[] sites = line[7].split(";");
                    if (refmap.containsKey(key)) {
                        for (int j = 0; j < refs.length; j++) {
                            refmap.get(key).add(refs[j]);
                        }
                        for (int j = 0; j < sites.length; j++) {
                            sitemap.get(key).add(sites[j]);
                        }
                    } else {
                        HashSet<String> refset = new HashSet<String>();
                        HashSet<String> siteset = new HashSet<String>();
                        for (int j = 0; j < refs.length; j++) {
                            refset.add(refs[j]);
                        }
                        for (int j = 0; j < sites.length; j++) {
                            siteset.add(sites[j]);
                        }
                        refmap.put(key, refset);
                        sitemap.put(key, siteset);
                    }
                    ratiomaps[i].put(key, new double[]{Double.parseDouble(line[8]), Double.parseDouble(line[16])});

                } else if (line.length == 16) {

                    String[] refs = line[5].split(";");
                    String[] sites = line[6].split(";");
                    if (refmap.containsKey(key)) {
                        for (int j = 0; j < refs.length; j++) {
                            refmap.get(key).add(refs[j]);
                        }
                        for (int j = 0; j < sites.length; j++) {
                            sitemap.get(key).add(sites[j]);
                        }
                    } else {
                        HashSet<String> refset = new HashSet<String>();
                        HashSet<String> siteset = new HashSet<String>();
                        for (int j = 0; j < refs.length; j++) {
                            refset.add(refs[j]);
                        }
                        for (int j = 0; j < sites.length; j++) {
                            siteset.add(sites[j]);
                        }
                        refmap.put(key, refset);
                        sitemap.put(key, siteset);
                    }
                    ratiomaps[i].put(key, new double[]{Double.parseDouble(line[7]), Double.parseDouble(line[15])});
                }
                totalset.add(key);
            }
            System.out.println(results[i] + "\t" + ratiomaps[i].size());
        }

        int overlap = 0;
        int rsd50 = 0;
        int log2 = 0;
        Iterator<String> it = totalset.iterator();
        while (it.hasNext()) {
            String key = it.next();
            ArrayList<Double> ratio1list = new ArrayList<Double>();
            ArrayList<Double> ratio2list = new ArrayList<Double>();

            for (int i = 0; i < ratiomaps.length; i++) {
                if (ratiomaps[i].containsKey(key)) {
                    double[] ratioi = ratiomaps[i].get(key);
                    ratio1list.add(ratioi[0]);
                    ratio2list.add(ratioi[1]);
                }
            }

            if (ratio1list.size() < 2) continue;

            overlap++;
            double ratio1 = MathTool.getAveInDouble(ratio1list);
            double rsd1 = MathTool.getRSDInDouble(ratio1list);
            double ratio2 = MathTool.getAveInDouble(ratio2list);
            double rsd2 = MathTool.getRSDInDouble(ratio2list);
            if (rsd1 < 0.5 && rsd2 < 0.5) {
                rsd50++;

                StringBuilder sb = new StringBuilder();
                sb.append(key).append("\t");
                HashSet<String> refset = refmap.get(key);
                for (String ref : refset) {
                    sb.append(ref).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\t");
                HashSet<String> siteset = sitemap.get(key);
                for (String site : siteset) {
                    sb.append(site).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\t");

                sb.append(ratio1list.size()).append("\t");
                sb.append(ratio1).append("\t");
                sb.append(rsd1).append("\t");
                sb.append(ratio2).append("\t");
                sb.append(rsd2).append("\t");
                writer.addContent(sb.toString(), 0, format);

                if (ratio1 >= 0.5 && ratio1 <= 2) {
                    log2++;
                }
            }
        }
        System.out.println(totalset.size() + "\t" + overlap + "\t" + rsd50 + "\t" + log2);
        writer.close();
    }

    private static void compare(String s1, String s2, String out) throws IOException, JXLException
    {
        HashMap<String, double[]> am1 = new HashMap<String, double[]>();
        HashMap<String, double[]> nm1 = new HashMap<String, double[]>();
        ExcelReader r1 = new ExcelReader(s1, new int[]{0, 1});
        String[] line1 = r1.readLine(0);
        while ((line1 = r1.readLine(0)) != null) {
            String key = line1[0] + "\t" + line1[3] + "\t" + line1[6] + "\t" + line1[7];
            am1.put(key, new double[]{Double.parseDouble(line1[16]), Double.parseDouble(line1[18])});
        }
        line1 = r1.readLine(1);
        while ((line1 = r1.readLine(1)) != null) {
            String key = line1[0] + "\t" + line1[3] + "\t" + line1[6] + "\t" + line1[7];
            nm1.put(key, new double[]{Double.parseDouble(line1[9]), Double.parseDouble(line1[13])});
        }
        r1.close();
        System.out.println(am1.size() + "\t" + nm1.size());

        HashMap<String, double[]> am2 = new HashMap<String, double[]>();
        HashMap<String, double[]> nm2 = new HashMap<String, double[]>();
        ExcelReader r2 = new ExcelReader(s2, new int[]{0, 1});
        String[] line2 = r2.readLine(0);
        while ((line2 = r2.readLine(0)) != null) {
            String key = line2[0] + "\t" + line2[3] + "\t" + line2[6] + "\t" + line2[7];
            am2.put(key, new double[]{Double.parseDouble(line2[16]), Double.parseDouble(line2[18])});
        }
        line2 = r2.readLine(1);
        while ((line2 = r2.readLine(1)) != null) {
            String key = line2[0] + "\t" + line2[3] + "\t" + line2[6] + "\t" + line2[7];
            nm2.put(key, new double[]{Double.parseDouble(line2[9]), Double.parseDouble(line2[13])});
        }
        r2.close();
        System.out.println(am2.size() + "\t" + nm2.size());

        HashSet<String> set1 = new HashSet<String>();
        set1.addAll(am1.keySet());
        set1.addAll(am2.keySet());
        System.out.println(set1.size());

        DecimalFormat df4 = DecimalFormats.DF0_4;
        ExcelWriter writer = new ExcelWriter(out, 6);
        ExcelFormat format = ExcelFormat.normalFormat;
        String title1 = "Sequence\tGlycan\tReference\tSites\tRatio 1\tRatio 2\tAve\tRSD\tRatio 1\tRatio 2\tAve\tRSD";
        String title2 = "Sequence\tGlycan\tReference\tSites\tRatio\tRatio";
        String title3 = "Sequence\tGlycan\tReference\tSites\tRatio\tRatio";
        String title5 = "Sequence\tGlycan\tReference\tSites\tRIA\tRIA";
        String title6 = "Sequence\tGlycan\tReference\tSites\tRIA\tRIA";
        writer.addTitle(title1, 0, format);
        writer.addTitle(title2, 1, format);
        writer.addTitle(title3, 2, format);
        writer.addTitle(title1, 3, format);
        writer.addTitle(title5, 4, format);
        writer.addTitle(title6, 5, format);

        Iterator<String> it = set1.iterator();
        while (it.hasNext()) {
            String key = it.next();
            StringBuilder sb = new StringBuilder();
            if (am1.containsKey(key) && am2.containsKey(key)) {
                double[] d1 = am1.get(key);
                double[] d2 = am2.get(key);
                sb.append(key).append("\t");
                sb.append(d1[0]).append("\t");
                sb.append(d2[0]).append("\t");
                double ave1 = MathTool.getAve(new double[]{d1[0], d2[0]});
                sb.append(df4.format(ave1)).append("\t");
                double rsd1 = MathTool.getRSD(new double[]{d1[0], d2[0]});
                sb.append(df4.format(rsd1)).append("\t");
                sb.append(d1[1]).append("\t");
                sb.append(d2[1]).append("\t");
                double ave2 = MathTool.getAve(new double[]{d1[1], d2[1]});
                sb.append(df4.format(ave2)).append("\t");
                double rsd2 = MathTool.getRSD(new double[]{d1[1], d2[1]});
                sb.append(df4.format(rsd2)).append("\t");
                writer.addContent(sb.toString(), 0, format);
            } else if (am1.containsKey(key)) {
                double[] d1 = am1.get(key);
                sb.append(key).append("\t");
                sb.append(d1[0]).append("\t");
                sb.append(d1[1]).append("\t");
                writer.addContent(sb.toString(), 1, format);
            } else if (am2.containsKey(key)) {
                double[] d2 = am2.get(key);
                sb.append(key).append("\t");
                sb.append(d2[0]).append("\t");
                sb.append(d2[1]).append("\t");
                writer.addContent(sb.toString(), 2, format);
            }
        }

        HashSet<String> set2 = new HashSet<String>();
        set2.addAll(nm1.keySet());
        set2.addAll(nm2.keySet());
        System.out.println(set2.size());
        Iterator<String> it2 = set2.iterator();
        while (it2.hasNext()) {
            String key = it2.next();
            StringBuilder sb = new StringBuilder();
            if (nm1.containsKey(key) && nm2.containsKey(key)) {
                double[] d1 = nm1.get(key);
                double[] d2 = nm2.get(key);
                sb.append(key).append("\t");
                sb.append(d1[0]).append("\t");
                sb.append(d2[0]).append("\t");
                double ave1 = MathTool.getAve(new double[]{d1[0], d2[0]});
                sb.append(df4.format(ave1)).append("\t");
                double rsd1 = MathTool.getRSD(new double[]{d1[0], d2[0]});
                sb.append(df4.format(rsd1)).append("\t");
                sb.append(d1[1]).append("\t");
                sb.append(d2[1]).append("\t");
                double ave2 = MathTool.getAve(new double[]{d1[1], d2[1]});
                sb.append(df4.format(ave2)).append("\t");
                double rsd2 = MathTool.getRSD(new double[]{d1[1], d2[1]});
                sb.append(df4.format(rsd2)).append("\t");
                writer.addContent(sb.toString(), 3, format);
            } else if (nm1.containsKey(key)) {
                double[] d1 = nm1.get(key);
                sb.append(key).append("\t");
                sb.append(d1[0]).append("\t");
                sb.append(d1[1]).append("\t");
                writer.addContent(sb.toString(), 4, format);
            } else if (nm2.containsKey(key)) {
                double[] d2 = nm2.get(key);
                sb.append(key).append("\t");
                sb.append(d2[0]).append("\t");
                sb.append(d2[1]).append("\t");
                writer.addContent(sb.toString(), 5, format);
            }
        }
        writer.close();
    }

    private static void classify(String in, String out) throws IOException, JXLException
    {
        String[] sheets = new String[]{"up-up", "up-nochange", "up-down", "nochange-up", "nochange-nochange", "nochange-down",
                "down-up", "down-nochange", "down-down"};
        ExcelWriter writer = new ExcelWriter(out, sheets);
        ExcelFormat format = ExcelFormat.normalFormat;
        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        for (int i = 0; i < sheets.length; i++) {
            writer.addContent(line, i, format);
        }
        HashSet<String> siteset = new HashSet<String>();
        while ((line = reader.readLine()) != null) {
            double ratio1 = Double.parseDouble(line[7]);
            double rsd1 = Double.parseDouble(line[7]);
            double ratio2 = Double.parseDouble(line[15]);
            double rsd2 = Double.parseDouble(line[11]);
//			if(rsd1>0.5 || rsd2>0.5) continue;
            siteset.add(line[5] + "\t" + line[6]);
            if (ratio1 > 2) {
                if (ratio2 > 2) {
                    writer.addContent(line, 0, format);
                } else if (ratio2 < 0.5) {
                    writer.addContent(line, 2, format);
                } else {
                    writer.addContent(line, 1, format);
                }
            } else if (ratio1 < 0.5) {
                if (ratio2 > 2) {
                    writer.addContent(line, 6, format);
                } else if (ratio2 < 0.5) {
                    writer.addContent(line, 8, format);
                } else {
                    writer.addContent(line, 7, format);
                }
            } else {
                if (ratio2 > 2) {
                    writer.addContent(line, 3, format);
                } else if (ratio2 < 0.5) {
                    writer.addContent(line, 5, format);
                } else {
                    writer.addContent(line, 4, format);
                }
            }
        }
        reader.close();
        writer.close();
        System.out.println(siteset.size());
    }

    private static void classifyCommon(String s1, String s2, String out) throws IOException, JXLException
    {
        ExcelReader r1 = new ExcelReader(s1, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
        ExcelReader r2 = new ExcelReader(s2, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
        for (int i = 0; i < 9; i++) {
            String[] l1 = r1.readLine(i);
            String[] l2 = r2.readLine(i);
            HashMap<String, String[]> map = new HashMap<String, String[]>();
            while ((l1 = r1.readLine(i)) != null) {
                String key = l1[0] + "\t" + l1[3];
                map.put(key, l1);
//				System.out.println(key);
            }

            while ((l2 = r2.readLine(i)) != null) {
                String key = l2[0] + "\t" + l2[1];
//				System.out.println(key);
                if (map.containsKey(key)) {
                    System.out.println(i + "\t" + key);
                }
            }
        }
        r1.close();
        r2.close();
    }

    /**
     * @param args
     * @throws JXLException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, JXLException
    {
        // TODO Auto-generated method stub

        String s1 = "H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\Glycan\\20130723_serum_di-labeling_Normal_HCC_HCD_N-glycan_quantification.relative.xls";
        String s2 = "H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\Glycan\\20130723_serum_di-labeling_Normal_HCC_HCD_N-glycan_quantification-2.relative.xls";
        String out = "H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\Glycan\\20130723_serum_di-labeling_Normal_HCC.xls";
        GlycoPaperDataProcessor.compare(s1, s2, out);
//		GlycoPaperDataProcessor.classify("H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\Glycan\\20130723_serum_di-labeling_Normal_HCC.xls", 
//				"H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\Glycan\\20130723_serum_di-labeling_Normal_HCC.classify.xls");
        String[] results = new String[]{"J:\\human_liver_glycan_quantification\\2014.02.13_1D\\glyco\\20140213_humanliver_with-glycan_Normal_normal_1.relative.xls",
                "J:\\human_liver_glycan_quantification\\2014.02.13_1D\\glyco\\20140213_humanliver_with-glycan_Normal_normal_2.relative.xls",
                "J:\\human_liver_glycan_quantification\\2014.02.13_1D\\glyco\\20140213_humanliver_with-glycan_Normal_normal_3.relative.xls",
                "J:\\human_liver_glycan_quantification\\2014.02.15_1D\\glyco\\20140215_humanliver_with-glycan_Normal_normal_1.relative.xls",
                "J:\\human_liver_glycan_quantification\\2014.02.15_1D\\glyco\\20140215_humanliver_with-glycan_Normal_normal_2.relative.xls",
                "J:\\human_liver_glycan_quantification\\2014.02.15_1D\\glyco\\20140215_humanliver_with-glycan_Normal_normal_3.relative.xls"};
//		GlycoPaperDataProcessor.compareMutilple(results, 
//				"J:\\human_liver_glycan_quantification\\liver.control.combine.xls", 0);
//		GlycoPaperDataProcessor.classify("H:\\NGLYCO_QUAN\\NGlycan_Quan_20131111\\HCD\\2D\\2D.relative.xls", 
//				"H:\\NGLYCO_QUAN\\NGlycan_Quan_20131111\\HCD\\2D\\2D.classify.xls");
//		GlycoPaperDataProcessor.classifyCommon("H:\\NGLYCO_QUAN\\NGlycan_Quan_20131111\\HCD\\2D\\2D.classify.xls", 
//				"H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\Glycan\\20130723_serum_di-labeling_Normal_HCC.classify.xls", "");
    }

}
