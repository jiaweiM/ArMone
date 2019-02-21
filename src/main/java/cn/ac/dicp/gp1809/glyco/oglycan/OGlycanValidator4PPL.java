/*
 ******************************************************************************
 * File: OGlycanValidator.java * * * Created on 2013-1-12
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.drawjf.MyXYPointerAnnotation3;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.databasemanger.*;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.drawjf.Annotations;
import cn.ac.dicp.gp1809.proteome.spectrum.*;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.RegionTopNIntensityFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.math.Arrangmentor;
import cn.ac.dicp.gp1809.util.math.MathTool;
import com.itextpdf.text.DocumentException;
import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ck
 * @version 2013-1-12, 15:12:47
 */
public class OGlycanValidator4PPL
{
    private static final int[][] fragmentIdList = OGlycanUnit.getFragmentIdList();
    private static final OGlycanUnit[] allUnits = OGlycanUnit.values();
    private static final double ionscoreThres = 15; // mascot score threshold
    private static final double tolerance = 0.1;
    private static final DecimalFormat df2 = DecimalFormats.DF0_2;
    private static final DecimalFormat df4 = DecimalFormats.DF0_4;
    private static final double H2O = 18.010565;
    private HashMap<String, OGlycanScanInfo2> infomap;
    /**
     * scanname -> full scan name, as a spectrum can match to multiple scan
     */
    private HashMap<String, HashSet<String>> scanTypeMap;
    private HashMap<String, IPeptide> pepmap;
    /**
     * best peptide for a scan
     */
    private HashMap<String, IPeptide> rank1Map;
    private HashMap<String, OGlycanPepInfo[]> glycoMap;
    private ProteinNameAccesser accesser;
    private HashMap<String, IMS2PeakList> peakmap;
    private HashMap<String, String> fragNameMap;
    private HashMap<String, Double> scoreMap;
    /**
     * score of given scan
     */
    private HashMap<String, ArrayList<Double>> scorelistMap;
    private int[] ionType;
    private OGlycanPepInfo[][] glycoinfolist;
    private double[] fdrs;
    private AminoacidFragment aaf;

    public OGlycanValidator4PPL(String pepinfo) throws IOException
    {
        this.pepmap = new HashMap<>();
        this.glycoMap = new HashMap<>();
        this.peakmap = new HashMap<>();
        this.rank1Map = new HashMap<>();
        this.scoreMap = new HashMap<>();
        this.scorelistMap = new HashMap<>();
        this.initial(pepinfo);

        this.fragNameMap = new HashMap<>();
        String[] glycoFragNames = OGlycanUnit.getTotalFragmentNames();
        for (int i = 0; i < glycoFragNames.length; i++) {
            fragNameMap.put("f" + (i + 1), glycoFragNames[i]);
            fragNameMap.put("f" + (i + 1) + "-H2O", glycoFragNames[i] + "(-H2O)");
        }
    }

    private static void test(String in) throws NumberFormatException, FileDamageException,
            PeptideParsingException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException,
            IOException, WriteException, DocumentException
    {
        File[] files = (new File(in)).listFiles();
        String info = null;
        ArrayList<String> ppllist = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith("ppl")) {
                ppllist.add(file.getAbsolutePath());
            }
            if (file.getName().endsWith("info")) {
                info = file.getAbsolutePath();
            }
        }
        String out = info.replace("info", "20150123.xls");

        OGlycanValidator4PPL validator = new OGlycanValidator4PPL(info);
        for (String aPpllist : ppllist)
            validator.readIn(aPpllist);

        validator.validate();
        validator.write(out, 0.01);
    }

    private static void testFetuin(String in) throws NumberFormatException, FileDamageException,
            PeptideParsingException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException,
            IOException, WriteException, DocumentException
    {
        File[] files = (new File(in)).listFiles();
        String info = null;
        String out = null;
        ArrayList<String> ppllist = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith("ppl")) {
                ppllist.add(file.getAbsolutePath());
            }
            if (file.getName().endsWith("info")) {
                info = file.getAbsolutePath();
            }
        }
        out = info.replace("info", "xls");

        OGlycanValidator4PPL validator = new OGlycanValidator4PPL(info);
        for (String aPpllist : ppllist) {
            validator.readIn(aPpllist);
        }

        validator.validate();
        validator.writeFetuin(out, 0.01);
//		validator.writePng(out, in+"\\supp spectra", 0.01);

        int[] count = new int[2];
        ArrayList<String> list = validator.getList(0.01);
        for (String scan : list) {
            if (validator.infomap.containsKey(scan)) {
                OGlycanScanInfo2 info2 = validator.infomap.get(scan);
                String modseq = validator.glycoMap.get(scan)[0].getModseq();
                int stcount = 0;
                for (int j = 0; j < modseq.length(); j++) {
                    char ac = modseq.charAt(j);
                    if (ac == 'S' || ac == 'T') {
                        stcount++;
                    }
                }
                int typecount = 0;
                OGlycanUnit[][] unitsss = info2.getUnits();
                for (OGlycanUnit[] unitss : unitsss) {
                    if (unitss.length <= stcount) {
                        typecount++;
                    }
                }
                if (typecount == 1) {
                    count[0]++;
                } else {
                    count[1]++;
                    System.out.println(modseq);
                }
            }
        }
        System.out.println(count[0] + "\t" + count[1]);
    }

    private static void testCasein(
            String in) throws NumberFormatException, FileDamageException, PeptideParsingException,
            ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, IOException, WriteException, DocumentException
    {
        File[] files = (new File(in)).listFiles();
        String info = null;
        String out = null;
        ArrayList<String> ppllist = new ArrayList<String>();
        for (File file : files) {
            if (file.getName().endsWith("ppl")) {
                ppllist.add(file.getAbsolutePath());
            }
            if (file.getName().endsWith("info")) {
                info = file.getAbsolutePath();
            }
        }
        out = info.replace("info", "xls");

        OGlycanValidator4PPL validator = new OGlycanValidator4PPL(info);
        for (String aPpllist : ppllist) {
            validator.readIn(aPpllist);
        }

        validator.validate();
        validator.writeCasein(out, 0.01);
    }

    private static void writeWithOriginal(String in, String mgfs) throws NumberFormatException,
            FileDamageException, PeptideParsingException, ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, IOException, DtaFileParsingException
    {
        File[] files = (new File(in)).listFiles();
        String info = null;
        String out = null;
        ArrayList<String> ppllist = new ArrayList<>();

        for (File file : files) {
            if (file.getName().endsWith("ppl")) {
                ppllist.add(file.getAbsolutePath());
            }
            if (file.getName().endsWith("info")) {
                info = file.getAbsolutePath();
            }
        }
        out = info.replace("info", "xls");

        OGlycanValidator4PPL validator = new OGlycanValidator4PPL(info);
        for (String aPpllist : ppllist)
            validator.readIn(aPpllist);
        validator.validate();

        HashMap<String, OGlycanPepInfo> totalMap = new HashMap<>();
        File pngout = new File(in + "\\T212");
//		File pngout = new File(in);
        pngout.mkdir();
        ArrayList<String> list = validator.getList(0.01);
//		OGlycanXlsWriter writer = new OGlycanXlsWriter(out);
        for (String key : list) {
            OGlycanPepInfo[] infos = validator.glycoMap.get(key);
//			writer.write(infos);
//if(key.contains("Locus:1.1.1.5241.2")){
            BufferedImage spectrum = createImage(infos[0]);
            String filename = key.substring(key.indexOf(":") + 1)
                    + ".processed";
            ImageIO.write(spectrum, "PNG", new File(pngout + "\\" + filename + ".png"));

            key = key.substring(0, key.lastIndexOf("."));
            totalMap.put(key, infos[0]);
//}
        }
//		writer.close();

        File[] mgffiles = (new File(mgfs)).listFiles(arg0 -> arg0.getName().endsWith("mgf"));
        Arrays.sort(mgffiles);

        for (int i = 0; i < mgffiles.length; i++) {
            MgfReader reader = new MgfReader(mgffiles[i]);
            MS2Scan ms2scan = null;
            while ((ms2scan = reader.getNextMS2Scan()) != null) {
                String scanname = ms2scan.getScanName().getScanName();
                scanname = scanname.substring(0, scanname.indexOf(","));
                scanname = scanname + "." + (i + 1);
                if (!totalMap.containsKey(scanname)) continue;

                OGlycanPepInfo oginfo = totalMap.get(scanname);
                OGlycanUnit[] units = oginfo.getUnits();
                int charge = oginfo.getPeptide().getCharge();
                boolean have366 = false;
                boolean have292 = false;
                boolean have406 = false;
                boolean have568 = false;
                boolean have730 = false;
                for (OGlycanUnit unit : units) {
                    int[] comps = unit.getCompCount();
                    if (comps[1] > 0)
                        have366 = true;
                    if (comps[2] > 0)
                        have292 = true;
                    if (comps[0] > 1) {
                        have406 = true;
                        have568 = true;
                    }
                    if (comps[0] > 1 && comps[1] > 1)
                        have730 = true;
                }
                double pepmr = oginfo.getPeptide().getMr();
                IPeak[] peaks = ms2scan.getPeakList().getPeakArray();
                double[] markMZs = new double[16];
                markMZs[0] = 204.086649;
                markMZs[1] = have292 ? 274.087412635 : 0.0;
                markMZs[2] = have292 ? 292.102692635 : 0.0;
                markMZs[3] = have366 ? 366.139472 : 0.0;
                markMZs[4] = have406 ? 406.158746 + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[5] = have568 ? 568.211571 + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[6] = have730 ? 730.264396 + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[7] = pepmr + AminoAcidProperty.PROTON_W;
                markMZs[8] = pepmr + 203.079373 + AminoAcidProperty.PROTON_W;
                markMZs[9] = have366 ? pepmr + 365.132198
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[10] = charge > 2 ? (pepmr) / 2.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[11] = charge > 2 ? (pepmr + 203.079373) / 2.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[12] = (charge > 2 && have366) ? (pepmr + 365.132198) / 2.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[13] = charge > 3 ? (pepmr) / 3.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[14] = charge > 3 ? (pepmr + 203.079373) / 3.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[15] = (charge > 3 && have366) ? (pepmr + 365.132198) / 3.0
                        + AminoAcidProperty.PROTON_W : 0.0;
//				BufferedImage image = createImage(oginfo, peaks, markMZs);
                BufferedImage image = createUnprocessImageNoAnnotation(oginfo, peaks, markMZs);
                String filename = scanname.substring(scanname.indexOf(":") + 1)
                        + ".unprocessed";
                ImageIO.write(image, "PNG",
                        new File(pngout + "\\" + filename + ".png"));
            }
            reader.close();
        }
    }

    private static void writeWithOriginalHCD(String in, String mgfs) throws NumberFormatException, FileDamageException,
            PeptideParsingException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, IOException,
            WriteException, DtaFileParsingException
    {
        File[] files = (new File(in)).listFiles();
        String info = null;
        String ppl = null;
        String out = null;
        for (File file : files) {
            if (file.getName().endsWith("ppl")) {
                ppl = file.getAbsolutePath();
            }
            if (file.getName().endsWith("info")) {
                info = file.getAbsolutePath();
            }
        }
        out = info.replace("info", "temp.xls");

        OGlycanValidator4PPL validator = new OGlycanValidator4PPL(info);
        validator.readIn(ppl);
        validator.validate();

        HashMap<String, OGlycanPepInfo> totalMap = new HashMap<>();
        File pngout = new File(in + "\\png");
        // File pngout = new File(in);
        pngout.mkdir();
        ArrayList<String> list = validator.getList(0.01);
        OGlycanXlsWriter writer = new OGlycanXlsWriter(out);
        for (String key : list) {
            OGlycanPepInfo[] infos = validator.glycoMap.get(key);
            writer.write(infos);
            // if(key.contains("Locus:1.1.1.5241.2")){
            BufferedImage spectrum = createImage(infos[0]);
            String filename = key.substring(key.indexOf(":") + 1)
                    + ".processed";
            ImageIO.write(spectrum, "PNG", new File(pngout + "\\" + filename + ".png"));

            key = key.substring(0, key.lastIndexOf("."));
            totalMap.put(key, infos[0]);
            // }
        }
        writer.close();

        File[] mgffiles = (new File(mgfs)).listFiles(arg0 -> arg0.getName().endsWith("mgf"));
        Arrays.sort(mgffiles);

        for (int i = 0; i < mgffiles.length; i++) {
            MgfReader reader = new MgfReader(mgffiles[i]);
            MS2Scan ms2scan = null;
            while ((ms2scan = reader.getNextMS2Scan()) != null) {
                int scannum = ms2scan.getScanNum();
                String scanname = "Locus:1.1.1." + scannum + "." + (i + 1);
                if (!totalMap.containsKey(scanname))
                    continue;

                OGlycanPepInfo oginfo = totalMap.get(scanname);
                OGlycanUnit[] units = oginfo.getUnits();
                int charge = oginfo.getPeptide().getCharge();
                boolean have366 = false;
                boolean have292 = false;
                for (OGlycanUnit unit : units) {
                    int[] comps = unit.getCompCount();
                    if (comps[1] > 0)
                        have366 = true;
                    if (comps[2] > 0)
                        have292 = true;
                }
                double pepmr = oginfo.getPeptide().getMr();
                IPeak[] peaks = ms2scan.getPeakList().getPeakArray();
                double[] markMZs = new double[13];
                markMZs[0] = 204.086649;
                markMZs[1] = have292 ? 274.087412635 : 0.0;
                markMZs[2] = have292 ? 292.102692635 : 0.0;
                markMZs[3] = have366 ? 366.139472 : 0.0;
                markMZs[4] = pepmr + AminoAcidProperty.PROTON_W;
                markMZs[5] = pepmr + 203.079373 + AminoAcidProperty.PROTON_W;
                markMZs[6] = have366 ? pepmr + 365.132198
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[7] = charge > 2 ? (pepmr) / 2.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[8] = charge > 2 ? (pepmr + 203.079373) / 2.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[9] = (charge > 2 && have366) ? (pepmr + 365.132198)
                        / 2.0 + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[10] = charge > 3 ? (pepmr) / 3.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[11] = charge > 3 ? (pepmr + 203.079373) / 3.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[12] = (charge > 3 && have366) ? (pepmr + 365.132198)
                        / 3.0 + AminoAcidProperty.PROTON_W : 0.0;
                BufferedImage image = createImage(oginfo, peaks, markMZs);
                String filename = scanname.substring(scanname.indexOf(":") + 1)
                        + ".unprocessed";
                ImageIO.write(image, "PNG", new File(pngout + "\\" + filename
                        + ".png"));
            }
            reader.close();
        }
    }

    private static BufferedImage createImage(OGlycanPepInfo opInfo)
    {
        DecimalFormat df = DecimalFormats.DF0_1;
        DecimalFormat dfe = DecimalFormats.DF_E2;
        double BarWidth = 2d;
        IPeak[] peaks = opInfo.getPeaks();
        HashMap<Double, String> matchedPeaks = opInfo.getMatchMap();
        for (Double mz : matchedPeaks.keySet()) {
//			System.out.println(mz+"\t"+matchedPeaks.get(mz));
        }
        String scanname = opInfo.getScanname();
        scanname = scanname.substring(0, scanname.lastIndexOf("."));
        String sequence = opInfo.getModseq();
        double score = opInfo.getPeptide().getPrimaryScore();
        double expect = ((MascotPeptide) opInfo.getPeptide()).getEvalue();
        double fscore = opInfo.getFormDeltaScore();
        double[] sitescore = opInfo.getScores();
        StringBuilder tsb = new StringBuilder();
        tsb.append("Scan:").append(scanname).append("     ");
        tsb.append("Ion score:").append(df.format(score)).append("     ");
        tsb.append("Expect:").append(dfe.format(expect)).append("     ");
        tsb.append("Delta F score:").append(df.format(fscore)).append("     ");
        tsb.append("Site score:");
        for (double aSitescore : sitescore) {
            tsb.append(df.format(aSitescore)).append(";");
        }
        tsb.deleteCharAt(tsb.length() - 1);
        tsb.append("\n");
        tsb.append(sequence);
        String title = tsb.toString();
//		String title = "Scan:"+scanname+"          "+"Ion score:"+df.format(score)+"\n"+sequence;

        XYSeriesCollection collection = new XYSeriesCollection();
        Annotations ans = new Annotations(true);

        XYSeries s1 = new XYSeries("m1");
        XYSeries s2 = new XYSeries("m2");
        XYSeries s3 = new XYSeries("m3");

        double baseIntensity = 0;
        for (int i = 0; i < peaks.length; i++) {
            if (peaks[i].getIntensity() > baseIntensity)
                baseIntensity = peaks[i].getIntensity();
        }

        for (int i = 0; i < peaks.length; i++) {

            double mz = peaks[i].getMz();
            double inten = peaks[i].getIntensity() / baseIntensity;

            if (matchedPeaks.containsKey(mz)) {

                String ann = matchedPeaks.get(mz);
                if (ann.contains("(")) {
                    s3.add(mz, inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{Color.RED}, mz, inten);
                } else {
                    s2.add(mz, inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{new Color(40, 80, 220)}, mz, inten);
                }
            } else {
                s1.add(mz, inten);
            }
        }
        collection.addSeries(s1);
        collection.addSeries(s2);
        collection.addSeries(s3);

        XYBarDataset dataset = new XYBarDataset(collection, BarWidth);

        NumberAxis numberaxis = new NumberAxis("m/z");
        NumberAxis domainaxis = new NumberAxis("Relative Intensity");

        XYBarRenderer renderer = new XYBarRenderer();
        MyXYPointerAnnotation3[] anns3 = ans.getAnnotations3();
        for (int i = 0; i < anns3.length; i++) {
            renderer.addAnnotation(anns3[i]);
        }
        renderer.setSeriesPaint(0, new Color(150, 150, 150));
        renderer.setSeriesFillPaint(0, null);
        renderer.setSeriesPaint(1, new Color(40, 80, 220));
        renderer.setSeriesFillPaint(1, null);
        renderer.setSeriesPaint(2, Color.RED);
//		renderer.setSeriesPaint(2, new Color(150, 150, 150));
        renderer.setSeriesFillPaint(2, null);
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        XYPlot xyplot = new XYPlot(dataset, numberaxis, domainaxis, renderer);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

        java.awt.Font titleFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 20);
        java.awt.Font legentFont = new java.awt.Font("Times",
                java.awt.Font.PLAIN, 60);
        java.awt.Font labelFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 20);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
                20);

        numberaxis.setAutoRange(true);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(300);
        numberaxis.setTickUnit(unit);
        numberaxis.setTickLabelFont(tickFont);
//		numberaxis.setUpperBound(1500);

        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(0.2);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setUpperBound(1.1);
        domainaxis.setTickLabelFont(tickFont);

        JFreeChart jfreechart = new JFreeChart(null,
                JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
        jfreechart.setTitle(new TextTitle(title, titleFont));

        BufferedImage image = jfreechart.createBufferedImage(1200, 800);
//		BufferedImage image = jfreechart.createBufferedImage(1467, 800);
        return image;
    }

    private static BufferedImage createImage(OGlycanPepInfo opInfo, IPeak[] peaks, double[] markmzs)
    {
        DecimalFormat df = DecimalFormats.DF0_1;
        DecimalFormat dfe = DecimalFormats.DF_E2;
        double BarWidth = 2d;
        HashMap<Double, String> matchedPeaks = opInfo.getMatchMap();
//		HashMap<Double, String> matchedPeaks = new HashMap<Double, String>();
        String scanname = opInfo.getScanname();
        scanname = scanname.substring(0, scanname.lastIndexOf("."));
        String sequence = opInfo.getModseq();
        double score = opInfo.getPeptide().getPrimaryScore();
        double expect = ((MascotPeptide) opInfo.getPeptide()).getEvalue();
        double fscore = opInfo.getFormDeltaScore();
        double[] sitescore = opInfo.getScores();
        StringBuilder tsb = new StringBuilder();
        tsb.append("Scan:").append(scanname).append("     ");
        tsb.append("Ion score:").append(df.format(score)).append("     ");
        tsb.append("Expect:").append(dfe.format(expect)).append("     ");
        tsb.append("Delta F score:").append(df.format(fscore)).append("     ");
        tsb.append("Site score:");
        for (double aSitescore : sitescore) {
            tsb.append(df.format(aSitescore)).append(";");
        }
        tsb.deleteCharAt(tsb.length() - 1);
        tsb.append("\n");
        tsb.append(sequence);
        String title = tsb.toString();
//		String title = "Scan:"+scanname+"          "+"Ion score:"+df.format(score)+"\n"+sequence;

        Double[] matchedMzs = matchedPeaks.keySet().toArray(new Double[matchedPeaks.size()]);
        XYSeriesCollection collection = new XYSeriesCollection();
        Annotations ans = new Annotations(true);

        XYSeries s1 = new XYSeries("m1");
        XYSeries s2 = new XYSeries("m2");
        XYSeries s3 = new XYSeries("m3");
//System.out.println(Arrays.toString(markmzs));
        IPeak[] markPeaks1 = new IPeak[markmzs.length];
        IPeak[] markPeaks2 = new IPeak[matchedMzs.length];
        double baseIntensity = 0;
        for (IPeak peak : peaks) {
            double mzi = peak.getMz();
            double intensityi = peak.getIntensity();
            if (intensityi > baseIntensity)
                baseIntensity = intensityi;
            for (int j = 0; j < markmzs.length; j++) {
                double mzj = markmzs[j];
                if (Math.abs(mzi - mzj) < 0.1) {
                    if (markPeaks1[j] == null) {
                        markPeaks1[j] = peak;
                    } else {
                        if (intensityi > markPeaks1[j].getIntensity()) {
                            markPeaks1[j] = peak;
                        }
                    }
                }
            }

            for (int j = 0; j < matchedMzs.length; j++) {
                double mzj = matchedMzs[j];
                if (Math.abs(mzi - mzj) < 0.1) {
                    if (markPeaks2[j] == null) {
                        markPeaks2[j] = peak;
                    } else {
                        if (intensityi > markPeaks2[j].getIntensity()) {
                            markPeaks2[j] = peak;
                        }
                    }
                }
            }
        }
//System.out.println("456\t");
        String[] pepAnno = new String[]{"HexNAc", "NeuAc-H2O", "NeuAc",
                "HexNAc+Hex", "GlcNAc-GalNAc", "Gal-(GlcNAc-)GalNAc",
                "Gal-(Gal-GlcNAc-)GalNAc", "pep+", "(pep-HexNAc)+",
                "(pep-HexNAc-Hex)+", "pep++", "(pep-HexNAc)++", "(pep-HexNAc-Hex)++", "pep+++",
                "(pep-HexNAc)+++", "(pep-HexNAc-Hex)+++"};

        for (IPeak peak : peaks) {
            double mz = peak.getMz();
            double inten = peak.getIntensity() / baseIntensity;

            if (matchedPeaks.containsKey(mz)) {

                String ann = matchedPeaks.get(mz);
//System.out.println("A\t"+mz+"\t"+ann);
                if (ann.contains("(")) {
                    s3.add(mz, inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{Color.RED}, mz, inten);
                } else {
                    s2.add(mz, inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{new Color(40, 80, 220)}, mz, inten);
                }

            } else {
                boolean mark = false;
                for (int j = 0; j < markPeaks1.length; j++) {
                    if (markPeaks1[j] != null && markPeaks1[j].getMz() == mz) {
                        mark = true;
//System.out.println("B\t"+mz+"\t"+pepAnno[j]);
                        s3.add(mz, inten);
                        ans.add3(String.valueOf(mz), new String[]{pepAnno[j]},
                                new Color[]{Color.RED}, mz, inten);
                    }
                }
                for (int j = 0; j < markPeaks2.length; j++) {
                    if (markPeaks2[j] != null && markPeaks2[j].getMz() == mz) {
                        mark = true;
                        String ann = matchedPeaks.get(matchedMzs[j]);
                        if (ann.contains("(")) {
                            s3.add(mz, inten);
                            ans.add3(String.valueOf(mz), new String[]{ann},
                                    new Color[]{Color.RED}, mz, inten);
                        } else {
                            s2.add(mz, inten);
                            ans.add3(String.valueOf(mz), new String[]{ann},
                                    new Color[]{new Color(40, 80, 220)}, mz, inten);
                        }
                    }
                }
                if (!mark) s1.add(mz, inten);
            }
        }
        collection.addSeries(s1);
        collection.addSeries(s2);
        collection.addSeries(s3);

        XYBarDataset dataset = new XYBarDataset(collection, BarWidth);

        NumberAxis numberaxis = new NumberAxis("m/z");
        NumberAxis domainaxis = new NumberAxis("Relative Intensity");

        XYBarRenderer renderer = new XYBarRenderer();
        MyXYPointerAnnotation3[] anns3 = ans.getAnnotations3();
        for (MyXYPointerAnnotation3 anAnns3 : anns3) {
            renderer.addAnnotation(anAnns3);
        }
        renderer.setSeriesPaint(0, new Color(150, 150, 150));
        renderer.setSeriesFillPaint(0, null);
        renderer.setSeriesPaint(1, new Color(40, 80, 220));
        renderer.setSeriesFillPaint(1, null);
        renderer.setSeriesPaint(2, Color.RED);
        renderer.setSeriesFillPaint(2, null);
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        XYPlot xyplot = new XYPlot(dataset, numberaxis, domainaxis, renderer);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

        java.awt.Font titleFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 20);
        java.awt.Font legentFont = new java.awt.Font("Times",
                java.awt.Font.PLAIN, 60);
        java.awt.Font labelFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 20);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
                20);

        numberaxis.setAutoRange(true);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(300);
        numberaxis.setTickUnit(unit);
        numberaxis.setTickLabelFont(tickFont);

        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(0.2);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setUpperBound(1.1);
        domainaxis.setTickLabelFont(tickFont);

        JFreeChart jfreechart = new JFreeChart(null,
                JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
        jfreechart.setTitle(new TextTitle(title, titleFont));

        BufferedImage image = jfreechart.createBufferedImage(1200, 800);
        return image;
    }

    private static BufferedImage createUnprocessImageNoAnnotation(OGlycanPepInfo opInfo,
            IPeak[] peaks, double[] markmzs)
    {
        DecimalFormat df = DecimalFormats.DF0_1;
        DecimalFormat dfe = DecimalFormats.DF_E2;
        double BarWidth = 2d;
        HashMap<Double, String> matchedPeaks = opInfo.getMatchMap();
        // HashMap<Double, String> matchedPeaks = new HashMap<Double, String>();
        String scanname = opInfo.getScanname();
        scanname = scanname.substring(0, scanname.lastIndexOf("."));
        String sequence = opInfo.getModseq();
        double score = opInfo.getPeptide().getPrimaryScore();
        double expect = ((MascotPeptide) opInfo.getPeptide()).getEvalue();
        double fscore = opInfo.getFormDeltaScore();
        double[] sitescore = opInfo.getScores();
        StringBuilder tsb = new StringBuilder();
        tsb.append("Scan:").append(scanname).append("     ");
        tsb.append("Ion score:").append(df.format(score)).append("     ");
        tsb.append("Expect:").append(dfe.format(expect)).append("     ");
        tsb.append("Delta F score:").append(df.format(fscore)).append("     ");
        tsb.append("Site score:");
        for (int i = 0; i < sitescore.length; i++) {
            tsb.append(df.format(sitescore[i])).append(";");
        }
        tsb.deleteCharAt(tsb.length() - 1);
        tsb.append("\n");
        tsb.append(sequence);
        String title = tsb.toString();
        // String title =
        // "Scan:"+scanname+"          "+"Ion score:"+df.format(score)+"\n"+sequence;

        Double[] matchedMzs = matchedPeaks.keySet().toArray(
                new Double[matchedPeaks.size()]);
        XYSeriesCollection collection = new XYSeriesCollection();
        Annotations ans = new Annotations(true);

        XYSeries s1 = new XYSeries("m1");
        XYSeries s2 = new XYSeries("m2");
        XYSeries s3 = new XYSeries("m3");
        // System.out.println(Arrays.toString(markmzs));
        IPeak[] markPeaks1 = new IPeak[markmzs.length];
        IPeak[] markPeaks2 = new IPeak[matchedMzs.length];
        double baseIntensity = 0;
        for (int i = 0; i < peaks.length; i++) {
            double mzi = peaks[i].getMz();
            double intensityi = peaks[i].getIntensity();
            boolean oxo = false;

            for (int j = 0; j < markmzs.length; j++) {
                double mzj = markmzs[j];
                if (Math.abs(mzi - mzj) < 0.1) {
                    oxo = true;
                    if (markPeaks1[j] == null) {
                        markPeaks1[j] = peaks[i];
                    } else {
                        if (intensityi > markPeaks1[j].getIntensity()) {
                            markPeaks1[j] = peaks[i];
                        }
                    }
                }
            }

            if (!oxo) {
                if (intensityi > baseIntensity)
                    baseIntensity = intensityi;
            }

            for (int j = 0; j < matchedMzs.length; j++) {
                double mzj = matchedMzs[j];
                if (Math.abs(mzi - mzj) < 0.1) {
                    if (markPeaks2[j] == null) {
                        markPeaks2[j] = peaks[i];
                    } else {
                        if (intensityi > markPeaks2[j].getIntensity()) {
                            markPeaks2[j] = peaks[i];
                        }
                    }
                }
            }
        }
        // System.out.println("456\t");
        String[] pepAnno = new String[]{"HexNAc", "NeuAc-H2O", "NeuAc",
                "HexNAc+Hex", "GlcNAc-GalNAc", "Gal-(GlcNAc-)GalNAc",
                "Gal-(Gal-GlcNAc-)GalNAc", "Pep+", "(Pep+HexNAc)+",
                "(Pep+HexNAc+Hex)+", "Pep++", "(Pep+HexNAc)++", "(Pep+HexNAc+Hex)++", "Pep+++",
                "(Pep+HexNAc)+++", "(Pep+HexNAc+Hex)+++"};

        for (IPeak peak : peaks) {
            double mz = peak.getMz();
            double inten = peak.getIntensity() / baseIntensity;

            if (matchedPeaks.containsKey(mz)) {

                String ann = matchedPeaks.get(mz);
                if (ann.contains("(")) {
                    s3.add(mz, inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{Color.RED}, mz, inten);
                } else {
                    s2.add(mz, inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{new Color(40, 80, 220)}, mz, inten);
                }

            } else {
                boolean mark = false;
                for (int j = 0; j < markPeaks1.length; j++) {
                    if (markPeaks1[j] != null && markPeaks1[j].getMz() == mz) {
                        mark = true;

                        s3.add(mz, inten);
                        ans.add3(String.valueOf(mz),
                                new String[]{pepAnno[j]},
                                new Color[]{Color.RED}, mz, inten);
                    }
                }
                for (int j = 0; j < markPeaks2.length; j++) {
                    if (markPeaks2[j] != null && markPeaks2[j].getMz() == mz) {
                        mark = true;
                        String ann = matchedPeaks.get(matchedMzs[j]);
                        if (ann.contains("(")) {
                            s3.add(mz, inten);
                            ans.add3(String.valueOf(mz), new String[]{ann},
                                    new Color[]{Color.RED}, mz, inten);
                        } else {
                            s2.add(mz, inten);
                            ans.add3(String.valueOf(mz), new String[]{ann},
                                    new Color[]{new Color(40, 80, 220)}, mz,
                                    inten);
                        }
                    }
                }
                if (!mark)
                    s1.add(mz, inten);
            }
        }
        collection.addSeries(s1);
        collection.addSeries(s2);
        collection.addSeries(s3);

        XYBarDataset dataset = new XYBarDataset(collection, BarWidth);

        NumberAxis numberaxis = new NumberAxis("m/z");
        NumberAxis domainaxis = new NumberAxis("Relative Intensity");

        XYBarRenderer renderer = new XYBarRenderer();
        MyXYPointerAnnotation3[] anns3 = ans.getAnnotations3();
        for (int i = 0; i < anns3.length; i++) {
//			renderer.addAnnotation(anns3[i]);
        }
        renderer.setSeriesPaint(0, new Color(150, 150, 150));
        renderer.setSeriesFillPaint(0, null);
        renderer.setSeriesPaint(1, new Color(40, 80, 220));
        renderer.setSeriesFillPaint(1, null);
        renderer.setSeriesPaint(2, Color.RED);
        renderer.setSeriesFillPaint(2, null);
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        XYPlot xyplot = new XYPlot(dataset, numberaxis, domainaxis, renderer);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

        java.awt.Font titleFont = new java.awt.Font("Times",
                // java.awt.Font.BOLD, 16);
                java.awt.Font.BOLD, 20);
        java.awt.Font legentFont = new java.awt.Font("Times",
                java.awt.Font.PLAIN, 60);
        java.awt.Font labelFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 20);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
                20);

        numberaxis.setAutoRange(true);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(200);
        numberaxis.setTickUnit(unit);
        numberaxis.setTickLabelFont(tickFont);
        numberaxis.setUpperBound(1400);

        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(0.2);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setUpperBound(1.1);
        domainaxis.setTickLabelFont(tickFont);

        JFreeChart jfreechart = new JFreeChart(null,
                JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
        jfreechart.setTitle(new TextTitle(title, titleFont));

        BufferedImage image = jfreechart.createBufferedImage(1200, 800);
        // BufferedImage image = jfreechart.createBufferedImage(1467, 800);
        return image;
    }

    private static void compare(String s1, String s2) throws IOException, JXLException
    {
        HashMap<String, String> m1 = new HashMap<>();
        ExcelReader r1 = new ExcelReader(s1);
        String[] l1 = r1.readLine();
        while ((l1 = r1.readLine()) != null) {
            String scan = l1[0].substring(0, l1[0].lastIndexOf("."));
            m1.put(scan, l1[6]);
        }
        r1.close();

        HashMap<String, String> m2 = new HashMap<>();
        ExcelReader r2 = new ExcelReader(s2);
        String[] l2 = r2.readLine();
        while ((l2 = r2.readLine()) != null) {
            String scan = l2[0].substring(0, l2[0].lastIndexOf("."));
            m2.put(scan, l2[6]);
        }
        r2.close();

        int count = 0;
        HashSet<String> set = new HashSet<>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (m1.containsKey(key) && m2.containsKey(key)) {
                count++;
            }
        }
        System.out.println(m1.size() + "\t" + m2.size() + "\t" + count);
    }

    private static void compareSite(String s1, String s2) throws IOException, JXLException
    {
        HashMap<String, HashSet<String>> m1 = new HashMap<>();
        ExcelReader r1 = new ExcelReader(s1, 2);
        String[] l1 = r1.readLine();
        while ((l1 = r1.readLine()) != null) {
            String key = l1[0] + "\t" + l1[1];
            if (m1.containsKey(key)) {
                m1.get(key).add(l1[2]);
            } else {
                HashSet<String> set = new HashSet<String>();
                set.add(l1[2]);
                m1.put(key, set);
            }
        }
        r1.close();

        HashMap<String, HashSet<String>> m2 = new HashMap<>();
        ExcelReader r2 = new ExcelReader(s2, 2);
        String[] l2 = r2.readLine();
        while ((l2 = r2.readLine()) != null) {
            String key = l2[0] + "\t" + l2[1];
            if (m2.containsKey(key)) {
                m2.get(key).add(l2[2]);
            } else {
                HashSet<String> set = new HashSet<>();
                set.add(l2[2]);
                m2.put(key, set);
            }
        }
        r2.close();

        int count = 0;
        HashSet<String> set = new HashSet<>();
        set.addAll(m1.keySet());
        set.addAll(m2.keySet());
        for (String key : set) {
            if (m1.containsKey(key) && m2.containsKey(key)) {
                HashSet<String> set1 = m1.get(key);
                HashSet<String> set2 = m2.get(key);
                HashSet<String> totalset = new HashSet<>();
                totalset.addAll(set1);
                totalset.addAll(set2);
                if (set1.size() != totalset.size() || set2.size() != totalset.size()) {
                    System.out.println(key + "\n" + set1 + "\n" + set2);
                }
                count++;
            }
        }
        System.out.println(m1.size() + "\t" + m2.size() + "\t" + count);
    }

    public static void main(String[] args) throws ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, PeptideParsingException,
            IOException, JXLException, FileDamageException, DocumentException
    {
        long begin = System.currentTimeMillis();

        OGlycanValidator4PPL ppl = new OGlycanValidator4PPL("I:\\o_glycan\\data set\\20170616_remove\\raw\\20170616_serum_Oglyco_deSA_1TFA_1_deglyco.info");
        HashMap<String, OGlycanPepInfo[]> infoMap = ppl.getInfoMap();


        String fasta = "H:\\OGlycan_0417_standard\\O-glycoprotein_0.fasta";
        String regex = "([^ ]*)";
        IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
        String pepinfo = "H:\\OGLYCAN2\\20141024_15glyco\\2D_trypsin\\" +
                "2D_trypsin.info";
//		OGlycanValidator4PPL.writeWithOriginalHCD("H:\\OGLYCAN\\OGlycan_20140609_HCD_Deglyco\\serum\\deglyco",
//				"H:\\OGLYCAN\\OGlycan_20140609_HCD_Deglyco\\serum");
//		OGlycanValidator4PPL.testCasein("H:\\OGLYCAN2\\casein\\20140925_casein");
        OGlycanValidator4PPL.test("H:\\OGLYCAN2\\20141211_14glyco\\2D_trypsin");
//		OGlycanValidator4PPL.testFetuin("H:\\OGLYCAN2\\20141211_14glyco\\fetuin");

		/*OGlycanValidator4PPL.writeWithOriginal("H:\\OGLYCAN2\\20141113_normal_HCC\\trypsin_normal\\correction",
				"j:\\20141112\\2D_trypsin_normal\\correction");*/

        long end = System.currentTimeMillis();
        System.out.println((end - begin) / 60000.0 + "min");
    }

    /**
     * read the pepinfo file
     *
     * @param pepinfo pep info file
     * @throws IOException for IOException
     */
    private void initial(String pepinfo) throws IOException
    {
        this.infomap = new HashMap<>();
        this.scanTypeMap = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(pepinfo));
        String line;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            this.infomap.put(scanname, info);

            HashSet<String> set = this.scanTypeMap.computeIfAbsent(oriScanname, k -> new HashSet<>());
            set.add(scanname);
        }
        reader.close();
    }

    /**
     * read the ppl file.
     *
     * @param file ppl file
     */
    public void readIn(String file) throws FileDamageException, IOException, PeptideParsingException
    {
        IPeptideListReader reader = new PeptideListReader(file);
        ProteinNameAccesser accesser = reader.getProNameAccesser();
        if (this.accesser == null) {
            this.accesser = accesser;
        } else {
            this.accesser.appand(accesser);
        }
        this.ionType = new int[]{Ion.TYPE_B, Ion.TYPE_Y};
        this.aaf = new AminoacidFragment(reader.getSearchParameter().getStaticInfo(), reader.getSearchParameter().getVariableInfo());
        RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(8, 100);

        MascotPeptide mpeptide;
        while ((mpeptide = (MascotPeptide) reader.getPeptide()) != null) {
            String scanname = mpeptide.getBaseName();
            if (scanname.endsWith(", "))
                scanname = scanname.substring(0, scanname.length() - 2);

            if (!this.infomap.containsKey(scanname))
                continue;

            if (mpeptide.getIonscore() < ionscoreThres)
                continue;

            if (mpeptide.getHomoThres() == 0) {
                if (mpeptide.getIonscore() < mpeptide.getIdenThres()) {
                    continue;
                }
            } else {
                if (mpeptide.getIonscore() < mpeptide.getIdenThres() && mpeptide.getIonscore() < mpeptide.getHomoThres()) {
                    continue;
                }
            }

            IMS2PeakList peaklist = filter.filter(reader.getPeakLists()[0]);
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            if (scoreMap.containsKey(oriScanname)) {
                scorelistMap.get(oriScanname).add((double) mpeptide.getPrimaryScore());

                if (mpeptide.getIonscore() > scoreMap.get(oriScanname)) {
                    HashSet<String> set = this.scanTypeMap.get(oriScanname);
                    for (String sn : set) {
                        this.rank1Map.remove(sn);
                    }
                    rank1Map.put(scanname, mpeptide);
                    peakmap.put(scanname, peaklist);
                }
            } else {
                rank1Map.put(scanname, mpeptide);
                peakmap.put(scanname, peaklist);
                scoreMap.put(oriScanname, (double) mpeptide.getPrimaryScore());
                ArrayList<Double> list = new ArrayList<>();
                list.add((double) mpeptide.getPrimaryScore());
                scorelistMap.put(oriScanname, list);
            }
        }
        reader.close();
    }

    public void readInHigh(String file) throws FileDamageException, IOException, PeptideParsingException
    {
        IPeptideListReader reader = new PeptideListReader(file);
        MascotPeptide mpeptide = null;
        ProteinNameAccesser accesser = reader.getProNameAccesser();
        if (this.accesser == null) {
            this.accesser = accesser;
        } else {
            this.accesser.appand(accesser);
        }
        this.ionType = new int[]{Ion.TYPE_B, Ion.TYPE_Y};
        this.aaf = new AminoacidFragment(reader.getSearchParameter().getStaticInfo(), reader.getSearchParameter().getVariableInfo());
        RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(8, 100);

//		System.out.println("peptide count\t"+reader.getNumberofPeptides());
        while ((mpeptide = (MascotPeptide) reader.getPeptide()) != null) {

            String scanname = mpeptide.getBaseName();
            if (scanname.endsWith(", ")) scanname = scanname.substring(0, scanname.length() - 2);

            if (!this.infomap.containsKey(scanname)) continue;
//if(!scanname.contains("4698.3"))continue;
            if (mpeptide.getIonscore() < ionscoreThres)
                continue;

            mpeptide.reCal4PValue(0.01f);
//			if(mpeptide.getEvalue()>0.01) continue;
            if (mpeptide.getIonscore() < mpeptide.getIdenThres()) continue;

//			IMS2PeakList peaklist = reader.getPeakLists()[0];
            IMS2PeakList peaklist = filter.filter(reader.getPeakLists()[0]);
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
//			System.out.println(oriScanname+"\t"+scanname);
            if (scoreMap.containsKey(oriScanname)) {
                scorelistMap.get(oriScanname).add(new Double(mpeptide.getPrimaryScore()));
                if (mpeptide.getIonscore() > scoreMap.get(oriScanname)) {
                    HashSet<String> set = this.scanTypeMap.get(oriScanname);
                    for (String sn : set) {
                        if (this.rank1Map.containsKey(sn)) {
                            this.rank1Map.remove(sn);
                        }
                    }
                    rank1Map.put(scanname, mpeptide);
                    peakmap.put(scanname, peaklist);
                }
            } else {
                rank1Map.put(scanname, mpeptide);
                peakmap.put(scanname, peaklist);
                scoreMap.put(oriScanname, new Double(mpeptide.getPrimaryScore()));
                ArrayList<Double> list = new ArrayList<Double>();
                list.add(new Double(mpeptide.getPrimaryScore()));
                scorelistMap.put(oriScanname, list);
            }
        }
        reader.close();
    }

    public void validate() throws NumberFormatException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException
    {
        for (String scanname : rank1Map.keySet()) {
            IPeptide peptide = rank1Map.get(scanname);
            IMS2PeakList peaklist = peakmap.get(scanname);
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            ArrayList<Double> scorelist = scorelistMap.get(oriScanname);
            Double[] scores = scorelist.toArray(new Double[scorelist.size()]);
            Arrays.sort(scores);
            double deltascore = 0;
            if (scores.length > 1) {
                deltascore = (scores[scores.length - 1] - scores[scores.length - 2]) / scores[scores.length - 1];
            } else if (scores.length == 1) {
                deltascore = 1;
            }
            String sequence = peptide.getSequence();

            StringBuilder sb = new StringBuilder();
            StringBuilder unisb = new StringBuilder();
            HashMap<Integer, Character> symap = new HashMap<>();
            int stcount = 0;

            for (int i = 0; i < sequence.length(); i++) {
                if (sequence.charAt(i) >= 'A' && sequence.charAt(i) <= 'Z') {
                    sb.append(sequence.charAt(i));
                    if (i >= 2 && i < sequence.length() - 2) {
                        unisb.append(sequence.charAt(i));
                        if (sequence.charAt(i) == 'S' || sequence.charAt(i) == 'T')
                            stcount++;
                    }
                } else if (sequence.charAt(i) == '*') {
                    sb.append(sequence.charAt(i));
                    symap.put(unisb.length(), '*');
                } else if (sequence.charAt(i) == '#') {
                    sb.append(sequence.charAt(i));
                    symap.put(unisb.length(), '#');
                } else if (sequence.charAt(i) == '.') {
                    sb.append(sequence.charAt(i));
                } else if (sequence.charAt(i) == '-') {
                    sb.append(sequence.charAt(i));
                }
            }
            OGlycanScanInfo2 info = this.infomap.get(scanname);
            String uniPepSeq = unisb.toString();

            Ions ions = aaf.fragment(sb.toString(), ionType, true);
            Ion[] bs = ions.getIons(Ion.TYPE_B);
            Ion[] ys = ions.getIons(Ion.TYPE_Y);
            IPeak[] peaks = peaklist.getPeaksSortByIntensity();

            ArrayList<OGlycanPepInfo> infolist = new ArrayList<>();
            OGlycanUnit[][] units = info.getUnits();

            for (OGlycanUnit[] unit : units) {
                if (stcount < unit.length) continue;
                OGlycanPepInfo pepinfo = this.validate(unit, stcount, bs, ys, uniPepSeq, peaks, symap);
                if (pepinfo == null) continue;

                pepinfo.setPepDeltaScore(deltascore);
                pepinfo.setPeptide(peptide);
                pepinfo.setRefs(this.getRefs(peptide, accesser));
                pepinfo.setScanname(scanname);
                pepinfo.setOxoniumPeaks(info.getMarkpeaks());
                infolist.add(pepinfo);
            }

            OGlycanPepInfo[] infos = infolist.toArray(new OGlycanPepInfo[infolist.size()]);
            if (infos.length == 0) continue;

            Arrays.sort(infos, (info1, info2) -> {
                if (info1 == null) {
                    if (info2 == null) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    if (info2 == null) {
                        return -1;
                    } else {
                        if (info1.getGlycoScore() > info2.getGlycoScore()) {
                            return -1;

                        } else if (info1.getGlycoScore() < info2.getGlycoScore()) {
                            return 1;

                        } else {
                            double ts1 = 0;
                            OGlycanUnit[] units1 = info1.getUnits();
                            for (OGlycanUnit anUnits1 : units1) {
                                if (anUnits1 == OGlycanUnit.core1_2) {
                                    ts1 += 0.5;
                                } else if (anUnits1 == OGlycanUnit.core1_4) {
                                    ts1 += 1.2;
                                } else if (anUnits1 == OGlycanUnit.core1_5) {
                                    ts1 += 0.5;
                                }
                            }
                            double ts2 = 0;
                            OGlycanUnit[] units2 = info2.getUnits();
                            for (OGlycanUnit anUnits2 : units2) {
                                if (anUnits2 == OGlycanUnit.core1_2) {
                                    ts2 += 0.5;
                                } else if (anUnits2 == OGlycanUnit.core1_4) {
                                    ts2 += 1.2;
                                } else if (anUnits2 == OGlycanUnit.core1_5) {
                                    ts2 += 0.5;
                                }
                            }
                            if (ts1 > ts2) {
                                return -1;
                            } else if (ts1 < ts2) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
            });

            ArrayList<OGlycanPepInfo> templist = new ArrayList<>();
            templist.add(infos[0]);

            OGlycanUnit[] maxUnits0 = infos[0].getUnits();

            for (int i = 1; i < infos.length; i++) {

                OGlycanUnit[] unitsi = infos[i].getUnits();
                if (maxUnits0.length == unitsi.length) {
                    boolean different = false;
                    for (int j = 0; j < maxUnits0.length; j++) {
                        int maxIdj = maxUnits0[j].getID();
                        int iIdj = unitsi[j].getID();
                        if (maxIdj != iIdj) {
                            if (!((maxIdj == 3 || maxIdj == 4) && (iIdj == 3 || iIdj == 4))) {
                                different = true;
                                break;
                            }
                        }
                    }
                    if (different) {
                        templist.add(infos[i]);
                    }
                } else {
                    templist.add(infos[i]);
                }
            }

            infos = templist.toArray(new OGlycanPepInfo[templist.size()]);

            if (infos.length == 1) {
                infos[0].setFormDeltaScore(2.0);
            } else {
                for (int i = 0; i < infos.length - 1; i++) {
                    if (infos[i].getGlycoScore() > 0) {

                        double formScore = (infos[i].getGlycoScore() - infos[i + 1].getGlycoScore()) / infos[i].getGlycoScore();
                        infos[i].setFormDeltaScore(formScore);

                        OGlycanUnit[] units0 = infos[i].getUnits();
                        OGlycanUnit[] units1 = infos[i + 1].getUnits();
                        int[] position0 = infos[i].getPosition();
                        int[] position1 = infos[i + 1].getPosition();
                        boolean[] determined = new boolean[units0.length];
                        for (int j = 0; j < units0.length; j++) {
                            for (int k = 0; k < units1.length; k++) {
                                if (units0[j] == units1[k] && position0[j] == position1[k]) {
                                    determined[j] = true;
                                }
                            }
                        }
                        infos[i].setDetermined(determined);
                    } else {
                        infos[i].setFormDeltaScore(0.0);
                    }
                }
            }

            this.glycoMap.put(scanname, infos);
        }
    }

    private OGlycanPepInfo validate(OGlycanUnit[] units, int stcount, Ion[] bs, Ion[] ys, String uniPepSeq,
            IPeak[] peaks, HashMap<Integer, Character> symap)
    {
        int glycoCount = units.length;
        int[] bionSTCount = new int[uniPepSeq.length()];
        int[] yionSTCount = new int[uniPepSeq.length()];
        for (int i = 0; i < uniPepSeq.length(); i++) {
            if (uniPepSeq.charAt(i) == 'S' || uniPepSeq.charAt(i) == 'T') {
                for (int j = i; j < uniPepSeq.length(); j++)
                    bionSTCount[j]++;
            }
        }
        for (int i = 0; i < bionSTCount.length - 1; i++) {
            yionSTCount[i] = bionSTCount[bionSTCount.length - 1] - bionSTCount[bionSTCount.length - i - 2];
        }

        HashMap<Integer, double[]> positionTypeModScoreMap = new HashMap<>();
        for (OGlycanUnit unit : units) {
            positionTypeModScoreMap.put(unit.getID(), new double[stcount]);
        }

        int[] initialList = new int[stcount];
        for (int i = 0; i < stcount; i++) {
            if (i < glycoCount) {
                initialList[i] = units[i].getID();
            } else {
                initialList[i] = -1;
            }
        }
        int[][] positionList = Arrangmentor.arrangementArrays(initialList);
        double[] scoreList = new double[positionList.length];
        HashMap<Double, String> bymatchmap = new HashMap<>();
        HashMap<Double, String>[] matchmaps = new HashMap[scoreList.length];
        for (int i = 0; i < matchmaps.length; i++) {
            matchmaps[i] = new HashMap<>();
        }
        HashSet<Double> byset = new HashSet<>();

        double[] binten = new double[bs.length];
        double[] yinten = new double[ys.length];

        int[][] glycoFragmentId = new int[units.length][];
        for (int i = 0; i < glycoFragmentId.length; i++) {
            glycoFragmentId[i] = new int[units[i].getFragid().length];
            System.arraycopy(units[i].getFragid(), 0, glycoFragmentId[i], 0, units[i].getFragid().length);
        }
        int[][] comFragIds = Arrangmentor.arrangeAll(glycoFragmentId);

        String[] fragnames = OGlycanUnit.getTotalFragmentNames();
        double[] fragmasses = OGlycanUnit.getTotalFragmentMasses();
        HashSet<String> usedset1 = new HashSet<String>();
        HashSet<String> usedset2 = new HashSet<String>();
        HashMap<String, String> annotionMap1 = new HashMap<String, String>();
        HashMap<String, String> annotionMap2 = new HashMap<String, String>();
        HashMap<String, Boolean> annotionMap3 = new HashMap<String, Boolean>();
        for (int i = 0; i < bs.length; i++) {
            annotionMap1.put("bRow" + (i + 1), "b" + (i + 1));
            annotionMap2.put("mzRow" + (i + 1), df4.format(bs[i].getMz()));
            annotionMap1.put("yRow" + (i + 1), "y" + (i + 1));
            annotionMap2.put("mzRow" + (i + 1) + "_3", df4.format(ys[i].getMz()));
        }

        double thres = 0;
        int thres50 = 0;
        double topinten = peaks[0].getIntensity();
        for (int i = 0; i < peaks.length; i++) {
            if (i == 50) break;
            thres += peaks[i].getIntensity();
            thres50++;
        }
        thres = thres / thres50;

        L:
        for (int i = 0; i < peaks.length; i++) {

            double mzi = Double.parseDouble(df4.format(peaks[i].getMz()));
            double inteni = (peaks.length - i) / (double) peaks.length;
            for (int j = 0; j < bs.length; j++) {
                double bfragmz = bs[j].getMz();
                double yfragmz = ys[j].getMz();

                if (Math.abs(mzi - yfragmz) < tolerance) {
                    if (usedset1.contains("y" + (j + 1)))
                        continue L;
//					System.out.println(i);
                    usedset1.add("y" + (j + 1));
                    yinten[j] = inteni;
                    bymatchmap.put(mzi, "y" + (j + 1));
                    byset.add(mzi);
                    annotionMap3.put("yRow" + (j + 1), true);
                    annotionMap3.put("mzRow" + (j + 1) + "_3", true);
                    continue L;
                }

                if (Math.abs(mzi - bfragmz) < tolerance) {
                    if (usedset1.contains("b" + (j + 1)))
                        continue L;
//					System.out.println(i);
                    usedset1.add("b" + (j + 1));
                    binten[j] = inteni;
                    bymatchmap.put(mzi, "b" + (j + 1));
                    byset.add(mzi);
                    annotionMap3.put("bRow" + (j + 1), true);
                    annotionMap3.put("mzRow" + (j + 1), true);
                    continue L;
                }
            }
        }

        L:
        for (int i = 0; i < peaks.length; i++) {

            double mzi = Double.parseDouble(df4.format(peaks[i].getMz()));
            double inteni = (peaks.length - i) / (double) peaks.length;

            if (bymatchmap.containsKey(mzi)) continue;

			/*double inteni = peaks[i].getIntensity();
			if(inteni>thres){
				inteni = (peaks.length-i)/(double)peaks.length*(2-thres/peaks[i].getIntensity());
			}else{
				inteni = (peaks.length-i)/(double)peaks.length*(peaks[i].getIntensity()/thres);
			}*/
            double[] tempScoreList = new double[positionList.length];
            int mzMatchCount = 0;

            for (int j = 0; j < bs.length; j++) {

                double bfragmz = bs[j].getMz();
                double yfragmz = ys[j].getMz();

                for (int k = 0; k < comFragIds.length; k++) {
                    double glycoMasses = 0;
                    StringBuilder glycoSb = new StringBuilder();
                    for (int l = 0; l < comFragIds[k].length; l++) {
                        glycoMasses += fragmasses[comFragIds[k][l]];
                        glycoSb.append(fragnames[comFragIds[k][l]] + "+");
                    }
                    glycoSb.deleteCharAt(glycoSb.length() - 1);

                    if (comFragIds[k].length <= yionSTCount[j]) {
                        if (Math.abs(mzi - yfragmz - glycoMasses) < tolerance) {
                            if (usedset2.contains("y" + (j + 1) + "+(" + glycoSb + ")"))
                                continue L;

                            annotionMap1.put("y  glycanRow" + (j + 1), "y" + (j + 1) + "+(" + glycoSb + ")");
                            annotionMap2.put("mzRow" + (j + 1) + "_4", df4.format(mzi));
                            annotionMap3.put("y  glycanRow" + (j + 1), true);
                            annotionMap3.put("mzRow" + (j + 1) + "_4", true);

                            usedset2.add("y" + (j + 1) + "+(" + glycoSb + ")");
//							matchmap.put(mzi, "y"+(j+1)+"+("+glycoSb+")");
//							this.match(positionList, tempScoreList, comFragIds[k], units, stcount-yionSTCount[j], mzi, inteni, fragSets, 1);
                            this.match(positionList, tempScoreList, comFragIds[k], stcount - yionSTCount[j], mzi, inteni, "y" + (j + 1) + "+(" + glycoSb + ")", matchmaps, 1);
                            mzMatchCount++;
//							System.out.println(i);
//							System.out.println(mzi+"\t"+inteni+"\t"+"y"+(j+1)+"+("+glycoSb+")"+"\t"+yfragmz+"\t"+glycoMasses+"\t"+Arrays.toString(scoreList));
//							continue L;
                        }
                        for (int l = 1; l <= units.length && l <= comFragIds[k].length; l++) {
                            if (Math.abs(mzi - yfragmz - (glycoMasses - H2O * l)) < tolerance) {
                                if (usedset2.contains("y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")"))
                                    continue L;

                                annotionMap1.put("y  glycanRow" + (j + 1), "y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
                                annotionMap2.put("mzRow" + (j + 1) + "_4", df4.format(mzi));
                                annotionMap3.put("y  glycanRow" + (j + 1), true);
                                annotionMap3.put("mzRow" + (j + 1) + "_4", true);

                                usedset2.add("y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
//								matchmap.put(mzi, "y"+(j+1)+"+("+glycoSb+"-H2O*"+l+")");
//								this.match(positionList, tempScoreList, comFragIds[k], units, stcount-yionSTCount[j], mzi, inteni, fragSets, 1);
                                this.match(positionList, tempScoreList, comFragIds[k], stcount - yionSTCount[j], mzi, inteni, "y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")", matchmaps, 1);
                                mzMatchCount++;
//								System.out.println(i);
//								System.out.println(mzi+"\t"+inteni+"\t"+"y"+(j+1)+"+("+glycoSb+"-H2O"+")"+"\t"+yfragmz+"\t"+glycoMasses+"\t"+Arrays.toString(scoreList));
//								continue L;
                            }
                        }
                    }
                    if (comFragIds[k].length <= bionSTCount[j]) {
                        if (Math.abs(mzi - bfragmz - glycoMasses) < tolerance) {
                            if (usedset2.contains("b" + (j + 1) + "+(" + glycoSb + ")"))
                                continue L;

                            annotionMap1.put("b  glycanRow" + (j + 1), "b" + (j + 1) + "+(" + glycoSb + ")");
                            annotionMap2.put("mzRow" + (j + 1) + "_2", df4.format(mzi));
                            annotionMap3.put("b  glycanRow" + (j + 1), true);
                            annotionMap3.put("mzRow" + (j + 1) + "_2", true);

                            usedset2.add("b" + (j + 1) + "+(" + glycoSb + ")");
//							matchmap.put(mzi, "b"+(j+1)+"+("+glycoSb+")");
//							this.match(positionList, tempScoreList, comFragIds[k], units, bionSTCount[j], mzi, inteni, fragSets, 0);
                            this.match(positionList, tempScoreList, comFragIds[k], bionSTCount[j], mzi, inteni, "b" + (j + 1) + "+(" + glycoSb + ")", matchmaps, 0);
                            mzMatchCount++;
//							System.out.println(i);
//							System.out.println(mzi+"\t"+inteni+"\t"+"b"+(j+1)+"+("+glycoSb+")"+"\t"+bfragmz+"\t"+glycoMasses+"\t"+Arrays.toString(scoreList));
//							continue L;
                        }
                        for (int l = 1; l <= units.length && l <= comFragIds[k].length; l++) {
                            if (Math.abs(mzi - bfragmz - (glycoMasses - H2O * l)) < tolerance) {
                                if (usedset2.contains("b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")"))
                                    continue L;

                                annotionMap1.put("b  glycanRow" + (j + 1), "b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
                                annotionMap2.put("mzRow" + (j + 1) + "_2", df4.format(mzi));
                                annotionMap3.put("b  glycanRow" + (j + 1), true);
                                annotionMap3.put("mzRow" + (j + 1) + "_2", true);

                                usedset2.add("b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
//								matchmap.put(mzi, "b"+(j+1)+"+("+glycoSb+"-H2O*"+l+")");
//								this.match(positionList, tempScoreList, comFragIds[k], units, bionSTCount[j], mzi, inteni, fragSets, 0);
                                this.match(positionList, tempScoreList, comFragIds[k], bionSTCount[j], mzi, inteni, "b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")", matchmaps, 0);
                                mzMatchCount++;
//								System.out.println(i);
//								System.out.println(mzi+"\t"+inteni+"\t"+"b"+(j+1)+"+("+glycoSb+"-H2O"+")"+"\t"+bfragmz+"\t"+glycoMasses+"\t"+Arrays.toString(scoreList));
//								continue L;
                            }
                        }
                    }
                }
            }

            if (mzMatchCount > 0) {
                for (int j = 0; j < scoreList.length; j++) {
                    scoreList[j] += tempScoreList[j] / (double) mzMatchCount;
                }
            }
        }

//		System.out.println("scorelist\t"+Arrays.toString(scoreList)+"\t"+usedset1.size());
//		System.out.println("usedset\t"+usedset1);
        if (usedset1.size() < 4) {
            return null;
        }
//System.out.println(usedset1.size());
        double totalModScore = 0;
        double maxScore = 0;
        double baseScore = MathTool.getTotal(binten) + MathTool.getTotal(yinten);

        int maxId = -1;
//		double [][] positionTypeModScore = new double [glycoCount][stcount];
        for (int i = 0; i < scoreList.length; i++) {

            totalModScore += scoreList[i];

            for (int j = 0; j < stcount; j++) {
                if (positionList[i][j] >= 0) {
//					positionTypeModScore[positionList[i][j]][j] += scoreList[i];
                    positionTypeModScoreMap.get(positionList[i][j])[j] += scoreList[i];
                }
            }
//			System.out.println("748\t"+i+"\t"+scoreList[i]);
            if (scoreList[i] > maxScore) {
                maxScore = scoreList[i];
                maxId = i;
            }
        }
//		System.out.println("724\tscorelist\t"+Arrays.toString(scoreList));

        Arrays.sort(scoreList);
        double peptideScore = 0;
        if (maxScore != 0) {
            if (scoreList.length == 1) {
                peptideScore = 1.0;
            } else {
                peptideScore = (maxScore - scoreList[scoreList.length - 2]) / maxScore;
            }
        }
//		System.out.println("840\t"+bymatchmap);
//		System.out.println("840\t"+matchmaps[maxId]);

        HashMap<Double, String> usedmatchmap = new HashMap<Double, String>();
        usedmatchmap.putAll(bymatchmap);
        if (maxId == -1) {
            usedmatchmap.putAll(matchmaps[0]);
        } else {
            usedmatchmap.putAll(matchmaps[maxId]);
        }

        double[] positionModScore = new double[stcount];
        if (totalModScore == 0) {
			/*for(int i=0;i<positionModScore.length;i++){
//				positionModScore[i] = glycoCount/(double)stcount;
//				positionModScore[i] = 1.0/(double)stcount;
				positionModScore[i] = 1.0/(double)positionTypeModScoreMap.size();
			}*/
            if (scoreList.length == 1) {
                for (int i = 0; i < positionModScore.length; i++) {
//					positionModScore[i] = 1.0/(double)positionList.length;
                    positionModScore[i] = 2.0;
                }
            } else {
                for (int i = 0; i < positionModScore.length; i++) {
//					positionModScore[i] = 1.0/(double)positionList.length;
                    positionModScore[i] = 0.0;
                }
            }
        } else {

            if (scoreList.length == 1) {
                for (int i = 0; i < positionModScore.length; i++) {
                    positionModScore[i] = 2.0;
                }
            } else {
                initialList = positionList[maxId];
                for (int i = 0; i < positionModScore.length; i++) {
					/*for(int j=0;j<glycoCount;j++){
						if(initialList[i]>=0 && units[j]==units[initialList[i]]){
//							positionModScore[i] += positionTypeModScore[j][i];
							positionModScore[i] += positionTypeModScoreMap.get(units[j].getID())[i];
						}
					}
					positionModScore[i] = positionModScore[i]/totalModScore;*/

                    if (initialList[i] >= 0) {
                        positionModScore[i] = positionTypeModScoreMap.get(initialList[i])[i] / totalModScore;
                    }

//					System.out.println("748\t"+i+"\t"+positionModScore[i]);
                }
            }

			/*Iterator<Double> matchit = matchmap.keySet().iterator();
			while(matchit.hasNext()){
				Double mz = matchit.next();
				if(!fragSets[maxId].contains(mz) && !byset.contains(mz)){
					matchit.remove();
				}else{
					System.out.println("906\t"+mz+"\t"+matchmap.get(mz));
				}
			}*/
        }

		/*for(int j=0;j<positionTypeModScore[0].length;j++){
			double posiTotal = 0;
			for(int i=0;i<positionTypeModScore.length;i++){
				posiTotal += positionTypeModScore[i][j];
			}
			for(int i=0;i<positionTypeModScore.length;i++){
				positionTypeModScore[i][j] = posiTotal==0 ? 0 : positionTypeModScore[i][j]/posiTotal;
			}
		}*/

        int stid = 0;
        int glycoid = 0;
        int[] locList = new int[glycoCount];
        double[] locScoreList = new double[glycoCount];
        boolean[] determined = new boolean[glycoCount];
        ArrayList<Double> restScore = new ArrayList<Double>();

        OGlycanUnit[] newUnits = new OGlycanUnit[glycoCount];
        double[][] newPositionTypeModScore = new double[glycoCount][stcount];
        StringBuilder finalsb = new StringBuilder();
        StringBuilder scoresb = new StringBuilder();
        for (int i = 0; i < uniPepSeq.length(); i++) {
            finalsb.append(uniPepSeq.charAt(i));
            if (symap.containsKey(i + 1)) {
                char sym = symap.get(i + 1);
                if (sym == '*') {
                    finalsb.append("[de]");
                } else if (sym == '#') {
                    finalsb.append("[ox]");
                }
            }
            scoresb.append(uniPepSeq.charAt(i));
            if (uniPepSeq.charAt(i) == 'S' || uniPepSeq.charAt(i) == 'T') {
//				if(positionModScore[stid]!=0){
                if (initialList[stid] >= 0) {
                    scoresb.append("[").append(df2.format(positionModScore[stid])).append("]");
//						finalsb.append("(").append(units[initialList[stid]].getName())
//						.append(",").append(df2.format(positionModScore[stid])).append(")");
//						finalsb.append("[").append(units[initialList[stid]].getComposition()).append("]");
                    finalsb.append("[").append(OGlycanUnit.getUnitFromID(initialList[stid]).getComposition()).append("]");
                    locList[glycoid] = (i + 1);
                    locScoreList[glycoid] = positionModScore[stid];
//						newUnits[glycoid] = units[initialList[stid]];
                    newUnits[glycoid] = OGlycanUnit.getUnitFromID(initialList[stid]);
//						newPositionTypeModScore[glycoid] = positionTypeModScore[initialList[stid]];
//						newPositionTypeModScore[glycoid] = positionTypeModScoreMap.get(initialList[stid]);
                    glycoid++;
                } else {
//						scoresb.append("[").append(df2.format(positionModScore[stid])).append("]");
                    restScore.add(positionModScore[stid]);
                }
//				}
                stid++;
            }
        }
/*
L:		for(int i=0;i<determined.length;i++){
			for(int j=0;j<restScore.size();j++){
				if(locScoreList[i]==restScore.get(j)){
					determined[i] = false;
					continue L;
				}
			}
			determined[i] = true;
		}
		*/
        for (int i = 0; i < newUnits.length; i++) {
            if (newUnits[i] == null) {
                return null;
            }
        }

        OGlycanPepInfo pepInfo = new OGlycanPepInfo(uniPepSeq, finalsb.toString(), scoresb.toString(), maxScore, baseScore,
                locList, newUnits, locScoreList, determined, positionModScore, newPositionTypeModScore, usedmatchmap, peaks);
//		if(peaks.length>=0 && peaks.length<30)
//		System.out.println(maxScore+"\t"+peaks.length);
        pepInfo.setAnnotionMap1(annotionMap1);
        pepInfo.setAnnotionMap2(annotionMap2);
        pepInfo.setAnnotionMap3(annotionMap3);
//		System.out.println(matchmap);
//		pepInfo.writeAnnotion();

        return pepInfo;
    }

    private void match(int[][] positionList, double[] scoreList, int[] glycoFragId, OGlycanUnit[] units,
            int position, double mz, double intensity, HashSet<Double>[] fragSets, int by)
    {
//System.out.println("843\tglycoFragId\t"+Arrays.toString(glycoFragId));
        int[][] allArrFragId = Arrangmentor.arrangementArrays(glycoFragId);

        // b ion
        if (by == 0) {
            for (int i = 0; i < positionList.length; i++) {
                for (int j = 0; j < allArrFragId.length; j++) {
                    int mci = 0;
                    boolean match = false;
                    for (int k = 0; k < position; k++) {
                        if (positionList[i][k] >= 0) {
                            if (units[positionList[i][k]].getFragmentIdSet().contains(allArrFragId[j][mci])) {
                                mci++;
                                if (mci == glycoFragId.length) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (match) {
                        fragSets[i].add(mz);
                        scoreList[i] += intensity;
                        break;
                    }
                }
            }
        } else if (by == 1) {// y ion
            for (int i = 0; i < positionList.length; i++) {
                for (int j = 0; j < allArrFragId.length; j++) {
                    int mci = 0;
                    boolean match = false;
                    for (int k = position; k < positionList[i].length; k++) {
                        if (positionList[i][k] >= 0) {
                            if (units[positionList[i][k]].getFragmentIdSet().contains(allArrFragId[j][mci])) {
                                mci++;
                                if (mci == glycoFragId.length) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (match) {
                        fragSets[i].add(mz);
                        scoreList[i] += intensity;
                        break;
                    }
                }
            }
        }
    }

    private void match(int[][] positionList, double[] scoreList, int[] glycoFragId,
            int position, double mz, double intensity, String fragname, HashMap<Double, String>[] matchmaps, int by)
    {
        // System.out.println("843\tglycoFragId\t"+Arrays.toString(glycoFragId));
        int[][] allArrFragId = Arrangmentor.arrangementArrays(glycoFragId);

        // b ion
        if (by == 0) {
            for (int i = 0; i < positionList.length; i++) {
                for (int j = 0; j < allArrFragId.length; j++) {
                    int mci = 0;
                    boolean match = false;
                    for (int k = 0; k < position; k++) {
                        if (positionList[i][k] >= 0) {
                            if (OGlycanUnit.getUnitFromID(positionList[i][k]).getFragmentIdSet()
                                    .contains(allArrFragId[j][mci])) {
                                mci++;
                                if (mci == glycoFragId.length) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (match) {
                        scoreList[i] += intensity;
                        matchmaps[i].put(mz, fragname);
                        break;
                    }
                }
            }
        } else if (by == 1) {// y ion
            for (int i = 0; i < positionList.length; i++) {
                for (int j = 0; j < allArrFragId.length; j++) {
                    int mci = 0;
                    boolean match = false;
                    for (int k = position; k < positionList[i].length; k++) {
                        if (positionList[i][k] >= 0) {
                            if (OGlycanUnit.getUnitFromID(positionList[i][k]).getFragmentIdSet()
                                    .contains(allArrFragId[j][mci])) {
                                mci++;
                                if (mci == glycoFragId.length) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (match) {
                        scoreList[i] += intensity;
                        matchmaps[i].put(mz, fragname);
                        break;
                    }
                }
            }
        }
    }

    public ArrayList<String> getList(double fdr)
    {
        ArrayList<String> list = new ArrayList<String>();

        ArrayList<OGlycanPepInfo[]>[] typelist = new ArrayList[2];
        for (int i = 0; i < typelist.length; i++) {
            typelist[i] = new ArrayList<OGlycanPepInfo[]>();
        }

        Iterator<String> it = this.glycoMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            OGlycanScanInfo2 info2 = this.infomap.get(key);
            int[] findtype = info2.getFindType();
            if (findtype[0] == 1) {
                if (findtype[1] == 1) {
                    typelist[0].add(this.glycoMap.get(key));
                } else {
                    typelist[1].add(this.glycoMap.get(key));
                }
            } else {
//				typelist[2].add(this.glycoMap.get(key));
//				if(findtype[1]==1){
//					typelist[2].add(this.glycoMap.get(key));
//				}else{
//					typelist[3].add(this.glycoMap.get(key));
//				}
            }
        }

        for (int i = 0; i < typelist.length; i++) {

            OGlycanPepInfo[][] glycoinfolist = typelist[i].toArray(new OGlycanPepInfo[typelist[i].size()][]);
            Arrays.sort(glycoinfolist, (o1, o2) -> {
                if (o1[0].getPeptide().getPrimaryScore() > o2[0].getPeptide().getPrimaryScore()) {
                    return -1;
                } else if (o1[0].getPeptide().getPrimaryScore() < o2[0].getPeptide().getPrimaryScore()) {
                    return 1;
                } else {
                    return 0;
                }
            });

            double[] fdrs = new double[glycoinfolist.length];
            int target = 0;
            int decoy = 0;
            for (int j = 0; j < glycoinfolist.length; j++) {
                if (glycoinfolist[j][0].getPeptide().isTP()) {
                    target++;
                } else {
                    decoy++;
                }
                if (target != 0)
                    fdrs[j] = (double) decoy / (double) target;
                else
                    fdrs[j] = 0;
            }

            int j = fdrs.length - 1;
            for (; j >= 0; j--) {
                if (fdrs[j] <= fdr) {
                    break;
                }
            }
//			System.out.println("cao\t"+i+"\t"+glycoinfolist.length+"\t"+j+"\t"+glycoinfolist[j][0].getPeptide().getPrimaryScore());
            for (int k = 0; k <= j; k++) {
                if (glycoinfolist[k][0].getPeptide().isTP()) {
                    list.add(glycoinfolist[k][0].getScanname());
                }
            }
        }
        return list;
    }

    private String[] getRefs(IPeptide peptide, ProteinNameAccesser accesser) throws ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException
    {

        ProteinReference[] reflist = peptide.getProteinReferences().toArray(
                new ProteinReference[peptide.getProteinReferences().size()]);
        String[] refs = new String[reflist.length];
        for (int i = 0; i < refs.length; i++) {
            SimpleProInfo simpinfo = accesser.getProInfo(reflist[i].getName());
            refs[i] = simpinfo.getRef();
        }

        Arrays.sort(refs);
        return refs;
    }

    public HashMap<String, OGlycanPepInfo[]> getInfoMap()
    {
        return this.glycoMap;
    }

    private void glycoTest()
    {
        Iterator<String> it = this.glycoMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            OGlycanPepInfo pepInfo = this.glycoMap.get(key)[0];
            IPeptide pep = this.pepmap.get(key);
            IPeptide rank1pep = this.rank1Map.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(pep.getScanNum()).append("\t");
            sb.append(pepInfo.getModseq()).append("\t");
            sb.append(pepInfo.getUniseq()).append("\t");
            sb.append(pepInfo.getGlycoScore()).append("\t");
            sb.append(pep.getSequence()).append("\t");
            sb.append(pep.isTP()).append("\t");
            sb.append(pep.getPrimaryScore()).append("\t");
            sb.append(pep.getRank()).append("\t");
            sb.append(rank1pep.getSequence()).append("\t");
            sb.append(rank1pep.getPrimaryScore()).append("\t");
            sb.append(rank1pep.getRank()).append("\t");
//			System.out.println(sb);
        }
    }

    public void writeCombineMgf(String in, String out) throws DtaFileParsingException, FileNotFoundException
    {

        File[] files = (new File(in)).listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                // TODO Auto-generated method stub
                if (pathname.getName().endsWith("mgf"))
                    return true;
                return false;
            }
        });
        Arrays.sort(files);

//		ArrayList<String> list = this.getList(0.01);
        HashSet<String> set = new HashSet<String>();
//		for(int i=0;i<list.size();i++){
        for (String name : this.scanTypeMap.keySet()) {
//			String name = list.get(i);
//			name = name.substring(0, name.lastIndexOf("."));
            set.add(name);
        }
//System.out.println(set.size());
        PrintWriter writer = new PrintWriter(out);
        String lineSeparator = "\n";

        for (int i = 0; i < files.length; i++) {

            MgfReader reader = new MgfReader(files[i]);
            MS2Scan ms2scan = null;

            while ((ms2scan = reader.getNextMS2Scan()) != null) {

                IMS2PeakList peaklist = ms2scan.getPeakList();
                PrecursePeak pp = peaklist.getPrecursePeak();
                double mz = pp.getMz();
                short charge = pp.getCharge();
                String name = ms2scan.getScanName().getScanName();
                if (name.endsWith(", "))
                    name = name.substring(0, name.length() - 2);
                name = name + "." + (i + 1);//System.out.println(name);

                if (set.contains(name)) {
//					System.out.println(name);
                } else continue;

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

    public void writeTest(String out) throws RowsExceededException, WriteException, IOException
    {
        OGlycanXlsWriter writer = new OGlycanXlsWriter(out);
//		writer.write(this.getList(0.01));
        writer.close();
    }

    public void write(String out,
            double fdr) throws RowsExceededException, WriteException, IOException, DocumentException
    {

        ArrayList<String> list = this.getList(fdr);
        OGlycanXlsWriter writer = new OGlycanXlsWriter(out);
        for (int i = 0; i < list.size(); i++) {
            OGlycanPepInfo[] infos = this.glycoMap.get(list.get(i));
//			System.out.println(infos[i].getModseq()+"\t"+infos[i].getScoreseq());
//			System.out.println(infos.length);
            writer.write(infos);
        }
        writer.close();
    }

    public void writeCasein(String out,
            double fdr) throws RowsExceededException, WriteException, IOException, DocumentException
    {

        ArrayList<String> list = this.getList(fdr);
        OGlycanXlsWriter writer = new OGlycanXlsWriter(out);
        for (int i = 0; i < list.size(); i++) {
            OGlycanPepInfo[] infos = this.glycoMap.get(list.get(i));
            String ref = infos[0].getRefs()[0];
            if (!ref.startsWith("sp0006")) continue;
//			System.out.println(infos.length);
            writer.write(infos);
        }
        writer.close();
    }

    public void writeFetuin(String out,
            double fdr) throws RowsExceededException, WriteException, IOException, DocumentException
    {

        HashSet<String> etdset = new HashSet<String>();
//		etdset.add("S271_core1");
//		etdset.add("S271_core2");
//		etdset.add("S282_core1");
//		etdset.add("S290_core1");
//		etdset.add("S296_core1");
//		etdset.add("T280_core1");
//		etdset.add("T280_core2");
//		etdset.add("T292_core2");
//		etdset.add("T295_core1");
        etdset.add("S271");
        etdset.add("S282");
        etdset.add("S290");
        etdset.add("S296");
        etdset.add("T280");
        etdset.add("T292");
        etdset.add("T295");
        int[][] scoreArrays = new int[21][2];
        int totalcount = 0;
        ArrayList<String> list = this.getList(fdr);
        OGlycanXlsWriter writer = new OGlycanXlsWriter(out);
        int pepcount = 0;
        for (int i = 0; i < list.size(); i++) {
            OGlycanPepInfo[] infos = this.glycoMap.get(list.get(i));
//			System.out.println(infos.length);
            writer.write(infos);

            if (infos[0].getFormDeltaScore() < 0.3) continue;
            IPeptide peptide = infos[0].getPeptide();
            HashMap<String, SeqLocAround> locmap = peptide.getPepLocAroundMap();
            ProteinReference[] reflist = peptide.getProteinReferences().toArray(
                    new ProteinReference[peptide.getProteinReferences().size()]);
            pepcount++;

            int st = 0;
            String sequence = peptide.getSequence();
            for (int j = 2; j < sequence.length() - 2; j++) {
                char caca = sequence.charAt(j);
                if (caca == 'S' || caca == 'T') {
                    st++;
                }
            }
//			if(st==1) continue;

            String[] refs = infos[0].getRefs();
            SeqLocAround sla = null;
            for (int j = 0; j < reflist.length; j++) {
                String name = reflist[j].getName();
                if (refs[0].contains(name)) {
                    sla = locmap.get(reflist[j].toString());
                    break;
                }
            }
            int begin = sla.getBeg();
            String scoreseq = infos[0].getScoreseq();
            int length = 0;
            int stcount = 0;
            boolean bbb = true;
            char[] cs = scoreseq.toCharArray();
            double[] posiscores = infos[0].getPositionScores();
            for (int j = 0; j < cs.length; j++) {
                char c = cs[j];
                if (c == '[') {
                    bbb = false;
                } else if (c == ']') {
                    bbb = true;
                } else {
                    if (bbb) {
                        length++;
                        if (c == 'S' || c == 'T') {
                            if (posiscores[stcount] != 2.0) {
                                String site = c + "" + (begin + length - 1);
                                System.out.println(totalcount++ + "\t" + posiscores[stcount] + "\t" + site + "\t" + etdset.contains(site));
                                int scoreid = (int) (posiscores[stcount] * 20);
                                for (int k = 0; k <= scoreid; k++) {
                                    if (etdset.contains(site)) {
                                        scoreArrays[k][0]++;
                                    } else {
                                        scoreArrays[k][1]++;
                                    }
                                }
                            }
                            stcount++;
                        }
                    }
                }
            }
/*
			String[] cs = scoreseq.split("[\\[\\]]");
			for(int j=0;j<cs.length-1;j+=2){
				length+=cs[j].length();
				String site = cs[j].charAt(cs[j].length()-1)+""+(begin+length-1);
				double score = Double.parseDouble(cs[j+1]);
				System.out.println(totalcount+++"\t"+score+"\t"+site+"\t"+etdset.contains(site));
				int scoreid = (int) (score*20);
				for(int k=0;k<=scoreid;k++){
					if(etdset.contains(site)){
						scoreArrays[k][0]++;
					}else{
						scoreArrays[k][1]++;
					}
				}
			}*/
        }
        writer.close();
        System.out.println("pepcount\t" + pepcount);

        for (int i = 0; i < scoreArrays.length; i++) {
            System.out.println(i + "\t" + (double) i / 20.0 + "\t" + scoreArrays[i][0] + "\t" + scoreArrays[i][1] + "\t" + (double) scoreArrays[i][0] / (double) (scoreArrays[i][0] + scoreArrays[i][1]));
        }
    }

    public void writePdf(String out, String pdfout,
            double fdr) throws RowsExceededException, WriteException, IOException, DocumentException
    {

        ArrayList<String> list = this.getList(fdr);
        OGlycanXlsWriter writer = new OGlycanXlsWriter(out);
        OGlycanPdfWriter pdfwriter = new OGlycanPdfWriter(pdfout);
        for (int i = 0; i < list.size(); i++) {
            OGlycanPepInfo[] infos = this.glycoMap.get(list.get(i));
            writer.write(infos);
            pdfwriter.write(infos[0]);
        }
        writer.close();
        pdfwriter.close();
    }

    public void writePng(String out, String pngout,
            double fdr) throws RowsExceededException, WriteException, IOException
    {

        File f = new File(pngout);
        if (!f.exists()) f.mkdir();

        ArrayList<String> list = this.getList(fdr);
        OGlycanXlsWriter writer = new OGlycanXlsWriter(out);
        for (int i = 0; i < list.size(); i++) {
            OGlycanPepInfo[] infos = this.glycoMap.get(list.get(i));
            writer.write(infos);
            String scanname = infos[0].getScanname();
            String name = scanname.substring(scanname.indexOf(":") + 1);
            File output = new File(pngout + "\\" + name + ".png");
            BufferedImage spectrum = createImage(infos[0]);
            ImageIO.write(spectrum, "PNG", output);
        }
        writer.close();
    }

    public void writeDrawFinal(String out, String png, double fdr, double formThres,
            double siteThres) throws NumberFormatException,
            FileDamageException, PeptideParsingException,
            ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, IOException,
            RowsExceededException, WriteException, DocumentException,
            DtaFileParsingException
    {

        File pngout = new File(png);
        if (!pngout.exists())
            pngout.mkdir();

        ArrayList<String> list = this.getList(fdr);
        OGlycanXlsWriter2 writer = new OGlycanXlsWriter2(out, formThres, siteThres);
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            OGlycanPepInfo[] infos = this.glycoMap.get(key);
            writer.write(infos);
            BufferedImage spectrum = createImage(infos[0]);
            String filename = key.substring(key.indexOf(":") + 1)
                    + ".processed";
            ImageIO.write(spectrum, "PNG", new File(pngout + "\\" + filename
                    + ".png"));
        }
        writer.close();
    }

    public void writeFinal(String out, double fdr, double formThres, double siteThres) throws NumberFormatException,
            FileDamageException, PeptideParsingException,
            ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, IOException,
            RowsExceededException, WriteException, DocumentException,
            DtaFileParsingException
    {

        ArrayList<String> list = this.getList(fdr);
        OGlycanXlsWriter2 writer = new OGlycanXlsWriter2(out, formThres, siteThres);
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            OGlycanPepInfo[] infos = this.glycoMap.get(key);
            writer.write(infos);
        }
        writer.close();
    }

    public void writeFinalDrawOriginal(String out, String png, String mgfs, double fdr,
            double formThres, double siteThres) throws NumberFormatException,
            FileDamageException, PeptideParsingException,
            ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, IOException,
            RowsExceededException, WriteException, DocumentException,
            DtaFileParsingException
    {

        HashMap<String, OGlycanPepInfo> totalMap = new HashMap<>();
        File pngout = new File(png);
        if (!pngout.exists())
            pngout.mkdir();

        ArrayList<String> list = this.getList(fdr);
        OGlycanXlsWriter2 writer = new OGlycanXlsWriter2(out, formThres, siteThres);
        for (String key : list) {
            OGlycanPepInfo[] infos = this.glycoMap.get(key);
            writer.write(infos);
            BufferedImage spectrum = createImage(infos[0]);
            String filename = key.substring(key.indexOf(":") + 1)
                    + ".processed";
            ImageIO.write(spectrum, "PNG", new File(pngout + "\\" + filename
                    + ".png"));

            key = key.substring(0, key.lastIndexOf("."));
            totalMap.put(key, infos[0]);
        }
        writer.close();

        File[] mgffiles = null;
        File mgff = new File(mgfs);
        if (mgff.isFile()) {
            mgffiles = new File[]{mgff};
        } else if (mgff.isDirectory()) {
            mgffiles = (new File(mgfs)).listFiles(arg0 -> arg0.getName().endsWith("mgf"));
            Arrays.sort(mgffiles);
        }

        for (int i = 0; i < mgffiles.length; i++) {
            MgfReader reader = new MgfReader(mgffiles[i]);
            MS2Scan ms2scan = null;
            while ((ms2scan = reader.getNextMS2Scan()) != null) {
                String scanname = ms2scan.getScanName().getScanName();
                scanname = scanname.substring(0, scanname.indexOf(","));
                scanname = scanname + "." + (i + 1);
                if (!totalMap.containsKey(scanname))
                    continue;

                OGlycanPepInfo oginfo = totalMap.get(scanname);
                OGlycanUnit[] units = oginfo.getUnits();
                int charge = oginfo.getPeptide().getCharge();
                boolean have366 = false;
                boolean have292 = false;
                boolean have406 = false;
                boolean have568 = false;
                boolean have730 = false;
                for (int j = 0; j < units.length; j++) {
                    int[] comps = units[j].getCompCount();
                    if (comps[1] > 0)
                        have366 = true;
                    if (comps[2] > 0)
                        have292 = true;
                    if (comps[0] > 1) {
                        have406 = true;
                        have568 = true;
                    }
                    if (comps[0] > 1 && comps[1] > 1)
                        have730 = true;
                }
                double pepmr = oginfo.getPeptide().getMr();
                IPeak[] peaks = ms2scan.getPeakList().getPeakArray();
                double[] markMZs = new double[16];
                markMZs[0] = 204.086649;
                markMZs[1] = have292 ? 274.087412635 : 0.0;
                markMZs[2] = have292 ? 292.102692635 : 0.0;
                markMZs[3] = have366 ? 366.139472 : 0.0;
                markMZs[4] = have406 ? 406.158746 + AminoAcidProperty.PROTON_W
                        : 0.0;
                markMZs[5] = have568 ? 568.211571 + AminoAcidProperty.PROTON_W
                        : 0.0;
                markMZs[6] = have730 ? 730.264396 + AminoAcidProperty.PROTON_W
                        : 0.0;
                markMZs[7] = pepmr + AminoAcidProperty.PROTON_W;
                markMZs[8] = pepmr + 203.079373 + AminoAcidProperty.PROTON_W;
                markMZs[9] = have366 ? pepmr + 365.132198
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[10] = charge > 2 ? (pepmr) / 2.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[11] = charge > 2 ? (pepmr + 203.079373) / 2.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[12] = (charge > 2 && have366) ? (pepmr + 365.132198)
                        / 2.0 + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[13] = charge > 3 ? (pepmr) / 3.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[14] = charge > 3 ? (pepmr + 203.079373) / 3.0
                        + AminoAcidProperty.PROTON_W : 0.0;
                markMZs[15] = (charge > 3 && have366) ? (pepmr + 365.132198)
                        / 3.0 + AminoAcidProperty.PROTON_W : 0.0;
                BufferedImage image = createImage(oginfo, peaks, markMZs);
                String filename = scanname.substring(scanname.indexOf(":") + 1)
                        + ".unprocessed";
                ImageIO.write(image, "PNG", new File(pngout + "\\" + filename
                        + ".png"));
            }
            reader.close();
        }
    }

}
