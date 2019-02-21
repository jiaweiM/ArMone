/*
 ******************************************************************************
 * File: OGlycanSpectraDrawer.java * * * Created on 2014-3-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.drawjf.MyXYPointerAnnotation3;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.drawjf.Annotations;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.math.MathTool;
import jxl.JXLException;
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
 * @version 2014-3-7, 10:08:51
 */
public class OGlycanSpectraDrawer
{

    private static DecimalFormat df = DecimalFormats.DF0_2;

    private static void writer(String[] infos, String[] ppls, String mgf, String out) throws NumberFormatException,
            FileDamageException, PeptideParsingException, ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, IOException, DtaFileParsingException
    {

        HashMap<String, OGlycanPepInfo> totalMap = new HashMap<String, OGlycanPepInfo>();
        HashMap<String, OGlycanPepInfo> totalMap2 = new HashMap<String, OGlycanPepInfo>();
        HashMap<String, String> scanMap = new HashMap<String, String>();
        HashMap<String, Integer> fileMap = new HashMap<String, Integer>();

        for (int i = 0; i < infos.length; i++) {

            OGlycanValidator4PPL validator = new OGlycanValidator4PPL(infos[i]);
            validator.readIn(ppls[i]);
            validator.validate();
            HashMap<String, OGlycanPepInfo[]> infomap = validator.getInfoMap();
            ArrayList<String> infolist = validator.getList(0.01);

            for (int j = 0; j < infolist.size(); j++) {
                String key = infolist.get(j);
                OGlycanPepInfo[] pepInfo = infomap.get(key);
                key = key.substring(0, key.lastIndexOf("."));
                OGlycanSiteInfo[] siteInfos = pepInfo[0].getSiteInfo();
                for (int k = 0; k < siteInfos.length; k++) {
                    String key2 = siteInfos[k].getSite()
                            + siteInfos[k].getSeqAround()
                            + siteInfos[k].getGlycoName();
                    if (totalMap.containsKey(key2)) {
                        OGlycanPepInfo infoi = pepInfo[0];
                        OGlycanPepInfo infoj = totalMap.get(key2);
                        if (infoi.getPeptide().getPrimaryScore() > infoj.getPeptide().getPrimaryScore()) {
                            totalMap.put(key2, pepInfo[0]);
                            totalMap2.remove(scanMap.get(key2));
                            totalMap2.put(key, pepInfo[0]);
                            fileMap.remove(scanMap.get(key2));
                            fileMap.put(key, i);
                            scanMap.put(key2, key);
                        }
                    } else {
                        totalMap.put(key2, pepInfo[0]);
                        scanMap.put(key2, key);
                        totalMap2.put(key, pepInfo[0]);
                        fileMap.put(key, i);
                    }
                }
            }
        }

        System.out.println(totalMap.size() + "\t" + totalMap2.size() + "\t" + scanMap.size());

        Iterator<String> it = scanMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String key2 = scanMap.get(key);
            if (!totalMap2.containsKey(key2)) continue;
//System.out.println(key2+"\t"+fileMap.get(key2));
            OGlycanPepInfo info = totalMap.get(key);
            IPeptide peptide = info.getPeptide();
            StringBuilder sb = new StringBuilder();
            sb.append(key2).append("\t");
            sb.append(info.getModseq()).append("\t");
            sb.append(peptide.getPrimaryScore()).append("\t");
            System.out.println(sb);
            BufferedImage image1 = createImage(info);
            String filename = key2.substring(key2.indexOf(":") + 1) + ".processed";
            ImageIO.write(image1, "PNG",
                    new File(out + "\\" + filename + ".png"));
        }

        MgfReader reader = new MgfReader(mgf);
        MS2Scan ms2scan = null;
        while ((ms2scan = reader.getNextMS2Scan()) != null) {
            String scanname = ms2scan.getScanName().getScanName();
            scanname = scanname.substring(0, scanname.indexOf(","));
            OGlycanPepInfo info = totalMap2.get(scanname);
            OGlycanUnit[] units = info.getUnits();
            int charge = info.getPeptide().getCharge();
            boolean have366 = false;
            boolean have292 = false;
            for (int i = 0; i < units.length; i++) {
                int[] comps = units[i].getCompCount();
                if (comps[1] > 0) have366 = true;
                if (comps[2] > 0) have292 = true;
            }
            double pepmr = info.getPeptide().getMr();
            IPeak[] peaks = ms2scan.getPeakList().getPeakArray();
            double[] markMZs = new double[13];
            markMZs[0] = 204.086649;
            markMZs[1] = have292 ? 274.087412635 : 0.0;
            markMZs[2] = have292 ? 292.102692635 : 0.0;
            markMZs[3] = have366 ? 366.139472 : 0.0;
            markMZs[4] = pepmr + AminoAcidProperty.PROTON_W;
            markMZs[5] = pepmr + 203.079373 + AminoAcidProperty.PROTON_W;
            markMZs[6] = have366 ? pepmr + 365.132198 + AminoAcidProperty.PROTON_W : 0.0;
            markMZs[7] = charge > 2 ? (pepmr) / 2.0 + AminoAcidProperty.PROTON_W : 0.0;
            markMZs[8] = charge > 2 ? (pepmr + 203.079373) / 2.0 + AminoAcidProperty.PROTON_W : 0.0;
            markMZs[9] = (charge > 2 && have366) ? (pepmr + 365.132198) / 2.0 + AminoAcidProperty.PROTON_W : 0.0;
            markMZs[10] = charge > 3 ? (pepmr) / 3.0 + AminoAcidProperty.PROTON_W : 0.0;
            markMZs[11] = charge > 3 ? (pepmr + 203.079373) / 3.0 + AminoAcidProperty.PROTON_W : 0.0;
            markMZs[12] = (charge > 3 && have366) ? (pepmr + 365.132198) / 3.0 + AminoAcidProperty.PROTON_W : 0.0;
            BufferedImage image = createImage(info, peaks, markMZs);
            String filename = scanname.substring(scanname.indexOf(":") + 1) + ".unprocessed";
            ImageIO.write(image, "PNG",
                    new File(out + "\\" + filename + ".png"));
        }
        reader.close();
    }

    private static void writerAll(String[] infos, String[] ppls, String[] mgfs,
            String out) throws NumberFormatException,
            FileDamageException, PeptideParsingException, ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, IOException,
            DtaFileParsingException
    {

        for (int i = 0; i < infos.length; i++) {

            HashMap<String, OGlycanPepInfo> totalMap = new HashMap<String, OGlycanPepInfo>();
            OGlycanValidator4PPL validator = new OGlycanValidator4PPL(infos[i]);
            validator.readIn(ppls[i]);
            validator.validate();
            HashMap<String, OGlycanPepInfo[]> infomap = validator.getInfoMap();
            ArrayList<String> infolist = validator.getList(0.01);

            for (int j = 0; j < infolist.size(); j++) {
                String key = infolist.get(j);
                OGlycanPepInfo[] pepInfo = infomap.get(key);
                key = key.substring(0, key.lastIndexOf("."));
                totalMap.put(key, pepInfo[0]);
//				System.out.println(key);
                OGlycanPepInfo info = pepInfo[0];
//				IPeptide peptide = info.getPeptide();
//				StringBuilder sb = new StringBuilder();
//				sb.append(key).append("\t");
//				sb.append(info.getModseq()).append("\t");
//				sb.append(peptide.getPrimaryScore()).append("\t");
//				System.out.println(sb);
                BufferedImage image1 = createImage(info);
                String filename = key.substring(key.indexOf(":") + 1)
                        + ".processed";
                ImageIO.write(image1, "PNG", new File(out + "\\" + filename
                        + ".png"));
            }
            System.out.println(totalMap.size());

            File[] files = (new File(mgfs[i])).listFiles(new FileFilter()
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

            for (int k = 0; k < files.length; k++) {
                MgfReader reader = new MgfReader(files[k]);
                MS2Scan ms2scan = null;
                while ((ms2scan = reader.getNextMS2Scan()) != null) {
                    String scanname = ms2scan.getScanName().getScanName();
                    scanname = scanname.substring(0, scanname.indexOf(","));
                    scanname = scanname + "." + (k + 1);
                    if (!totalMap.containsKey(scanname)) continue;

                    OGlycanPepInfo info = totalMap.get(scanname);
                    OGlycanUnit[] units = info.getUnits();
                    int charge = info.getPeptide().getCharge();
                    boolean have366 = false;
                    boolean have292 = false;
                    for (int j = 0; j < units.length; j++) {
                        int[] comps = units[j].getCompCount();
                        if (comps[1] > 0)
                            have366 = true;
                        if (comps[2] > 0)
                            have292 = true;
                    }
                    double pepmr = info.getPeptide().getMr();
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
                    markMZs[9] = (charge > 2 && have366) ? (pepmr + 365.132198) / 2.0
                            + AminoAcidProperty.PROTON_W : 0.0;
                    markMZs[10] = charge > 3 ? (pepmr) / 3.0
                            + AminoAcidProperty.PROTON_W : 0.0;
                    markMZs[11] = charge > 3 ? (pepmr + 203.079373) / 3.0
                            + AminoAcidProperty.PROTON_W : 0.0;
                    markMZs[12] = (charge > 3 && have366) ? (pepmr + 365.132198) / 3.0
                            + AminoAcidProperty.PROTON_W : 0.0;
                    BufferedImage image = createImage(info, peaks, markMZs);
                    String filename = scanname.substring(scanname.indexOf(":") + 1)
                            + ".unprocessed";
                    ImageIO.write(image, "PNG",
                            new File(out + "\\" + filename + ".png"));
                }
                reader.close();
            }
        }
    }

    private static void write(String info1, String ppl1, String info2,
            String ppl2, String info3, String ppl3, String out)
            throws NumberFormatException, FileDamageException,
            PeptideParsingException, ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, IOException
    {/*

		OGlycanValidator4PPL validator1 = new OGlycanValidator4PPL(info1);
		validator1.validate(ppl1);
		HashMap<String, OGlycanPepInfo[]> map1 = validator1.getInfoMap();
		ArrayList<String> list1 = validator1.getList(0.01);

		OGlycanValidator4PPL validator2 = new OGlycanValidator4PPL(info2);
		validator2.validate(ppl2);
		HashMap<String, OGlycanPepInfo[]> map2 = validator2.getInfoMap();
		ArrayList<String> list2 = validator2.getList(0.01);

		OGlycanValidator4PPL validator3 = new OGlycanValidator4PPL(info3);
		validator3.validate(ppl3);
		HashMap<String, OGlycanPepInfo[]> map3 = validator3.getInfoMap();
		ArrayList<String> list3 = validator3.getList(0.01);

		HashMap<String, OGlycanPepInfo> totalMap = new HashMap<String, OGlycanPepInfo>();
		HashMap<String, String> scanMap = new HashMap<String, String>();

		for (int i = 0; i < list1.size(); i++) {
			String key = list1.get(i);
			OGlycanPepInfo[] infos = map1.get(key);
			OGlycanSiteInfo[] siteInfos = infos[0].getSiteInfo();
			for (int j = 0; j < siteInfos.length; j++) {
				String key2 = siteInfos[j].getSite()
						+ siteInfos[j].getSeqAround()
						+ siteInfos[j].getGlycoName();
				if (totalMap.containsKey(key2)) {
					OGlycanPepInfo infoi = infos[0];
					OGlycanPepInfo infoj = totalMap.get(key2);
					if (infoi.getMatchScore() > infoj.getMatchScore()) {
						totalMap.put(key2, infos[0]);
						scanMap.put(key2, key.replace("Locus:", "0."));
					}
				} else {
					totalMap.put(key2, infos[0]);
					scanMap.put(key2, key.replace("Locus:", "0."));
				}
			}
		}

		for (int i = 0; i < list2.size(); i++) {
			String key = list2.get(i);
			OGlycanPepInfo[] infos = map2.get(key);
			OGlycanSiteInfo[] siteInfos = infos[0].getSiteInfo();
			for (int j = 0; j < siteInfos.length; j++) {
				String key2 = siteInfos[j].getSite()
						+ siteInfos[j].getSeqAround()
						+ siteInfos[j].getGlycoName();
				if (totalMap.containsKey(key2)) {
					OGlycanPepInfo infoi = infos[0];
					OGlycanPepInfo infoj = totalMap.get(key2);
					if (infoi.getMatchScore() > infoj.getMatchScore()) {
						totalMap.put(key2, infos[0]);
						scanMap.put(key2, key.replace("Locus:", "1."));
					}
				} else {
					totalMap.put(key2, infos[0]);
					scanMap.put(key2, key.replace("Locus:", "1."));
				}
			}
		}

		for (int i = 0; i < list3.size(); i++) {
			String key = list3.get(i);
			OGlycanPepInfo[] infos = map3.get(key);
			OGlycanSiteInfo[] siteInfos = infos[0].getSiteInfo();
			for (int j = 0; j < siteInfos.length; j++) {
				String key2 = siteInfos[j].getSite()
						+ siteInfos[j].getSeqAround()
						+ siteInfos[j].getGlycoName();
				if (totalMap.containsKey(key2)) {
					OGlycanPepInfo infoi = infos[0];
					OGlycanPepInfo infoj = totalMap.get(key2);
					if (infoi.getMatchScore() > infoj.getMatchScore()) {
						totalMap.put(key2, infos[0]);
						scanMap.put(key2, key.replace("Locus:", "2."));
					}
				} else {
					totalMap.put(key2, infos[0]);
					scanMap.put(key2, key.replace("Locus:", "2."));
				}
			}
		}

		System.out.println(scanMap.size() + "\t" + totalMap.size());

		HashSet<String> set = new HashSet<String>();
		Iterator<String> it = scanMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String scanname = scanMap.get(key);
			if (set.contains(scanname))
				continue;
			set.add(scanname);

			OGlycanPepInfo info = totalMap.get(key);
			// System.out.println(scanname);
			BufferedImage image = createImage(info);
			String filename = scanname.substring(scanname.indexOf(":") + 1);
			ImageIO.write(image, "PNG",
					new File(out + "\\" + filename + ".png"));
		}
	*/
    }

    private static void write(String info1, String ppl1, String info2,
            String ppl2, String info3, String ppl3, String out, String mgf)
            throws NumberFormatException, FileDamageException,
            PeptideParsingException, ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, IOException, DtaFileParsingException
    {/*

		OGlycanValidator4PPL validator1 = new OGlycanValidator4PPL(info1);
		validator1.validate(ppl1);
		HashMap<String, OGlycanPepInfo[]> map1 = validator1.getInfoMap();
		ArrayList<String> list1 = validator1.getList(0.01);

		OGlycanValidator4PPL validator2 = new OGlycanValidator4PPL(info2);
		validator2.validate(ppl2);
		HashMap<String, OGlycanPepInfo[]> map2 = validator2.getInfoMap();
		ArrayList<String> list2 = validator2.getList(0.01);

		OGlycanValidator4PPL validator3 = new OGlycanValidator4PPL(info3);
		validator3.validate(ppl3);
		HashMap<String, OGlycanPepInfo[]> map3 = validator3.getInfoMap();
		ArrayList<String> list3 = validator3.getList(0.01);

		HashMap<String, OGlycanPepInfo> totalMap = new HashMap<String, OGlycanPepInfo>();
		HashMap<String, OGlycanPepInfo> totalMap2 = new HashMap<String, OGlycanPepInfo>();
		HashMap<String, String> scanMap = new HashMap<String, String>();
		
		for (int i = 0; i < list1.size(); i++) {
			String key = list1.get(i);
			OGlycanPepInfo[] infos = map1.get(key);
			key = key.substring(0, key.lastIndexOf("."));
			OGlycanSiteInfo[] siteInfos = infos[0].getSiteInfo();
			for (int j = 0; j < siteInfos.length; j++) {
				String key2 = siteInfos[j].getSite()
						+ siteInfos[j].getSeqAround()
						+ siteInfos[j].getGlycoName();
				if (totalMap.containsKey(key2)) {
					OGlycanPepInfo infoi = infos[0];
					OGlycanPepInfo infoj = totalMap.get(key2);
					if (infoi.getMatchScore() > infoj.getMatchScore()) {
						totalMap.put(key2, infos[0]);
						scanMap.put(key2, key);
						totalMap2.put(key, infos[0]);
					}
				} else {
					totalMap.put(key2, infos[0]);
					scanMap.put(key2, key);
					totalMap2.put(key, infos[0]);
				}
			}
		}

		for (int i = 0; i < list2.size(); i++) {
			String key = list2.get(i);
			OGlycanPepInfo[] infos = map2.get(key);
			key = key.substring(0, key.lastIndexOf("."));
			OGlycanSiteInfo[] siteInfos = infos[0].getSiteInfo();
			for (int j = 0; j < siteInfos.length; j++) {
				String key2 = siteInfos[j].getSite()
						+ siteInfos[j].getSeqAround()
						+ siteInfos[j].getGlycoName();
				if (totalMap.containsKey(key2)) {
					OGlycanPepInfo infoi = infos[0];
					OGlycanPepInfo infoj = totalMap.get(key2);
					if (infoi.getMatchScore() > infoj.getMatchScore()) {
						totalMap.put(key2, infos[0]);
						scanMap.put(key2, key);
						totalMap2.put(key, infos[0]);
					}
				} else {
					totalMap.put(key2, infos[0]);
					scanMap.put(key2, key);
					totalMap2.put(key, infos[0]);
				}
			}
		}

		for (int i = 0; i < list3.size(); i++) {
			String key = list3.get(i);
			OGlycanPepInfo[] infos = map3.get(key);
			key = key.substring(0, key.lastIndexOf("."));
			OGlycanSiteInfo[] siteInfos = infos[0].getSiteInfo();
			for (int j = 0; j < siteInfos.length; j++) {
				String key2 = siteInfos[j].getSite()
						+ siteInfos[j].getSeqAround()
						+ siteInfos[j].getGlycoName();
				if (totalMap.containsKey(key2)) {
					OGlycanPepInfo infoi = infos[0];
					OGlycanPepInfo infoj = totalMap.get(key2);
					if (infoi.getMatchScore() > infoj.getMatchScore()) {
						totalMap.put(key2, infos[0]);
						scanMap.put(key2, key);
						totalMap2.put(key, infos[0]);
					}
				} else {
					totalMap.put(key2, infos[0]);
					scanMap.put(key2, key);
					totalMap2.put(key, infos[0]);
				}
			}
		}
		
		Iterator<String> it = totalMap2.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			if(!scanMap.values().contains(key)){
				it.remove();
			}
		}

		System.out.println(scanMap.size() + "\t" + totalMap.size()+"\t"+totalMap2.size());
PrintWriter pw = new PrintWriter("D:\\P\\o-glyco\\2014.03.04.revise\\temp.test.txt");
		MgfReader reader = new MgfReader(mgf);
		MS2Scan ms2scan = null;
		while ((ms2scan = reader.getNextMS2Scan()) != null) {
			String scanname = ms2scan.getScanName().getScanName();
			scanname = scanname.substring(0, scanname.indexOf(","));
			OGlycanPepInfo info = totalMap2.get(scanname);
			double pepmr = info.getPeptide().getMr();
			IPeak [] peaks = ms2scan.getPeakList().getPeakList();
			double [] markMZs = new double[10];
			markMZs[0] = 204.086649;
			markMZs[1] = 274.087412635;
			markMZs[2] = 292.102692635;
			markMZs[3] = 366.139472;
			markMZs[4] = pepmr+AminoAcidProperty.PROTON_W;
			markMZs[5] = pepmr+203.079373+AminoAcidProperty.PROTON_W;
			markMZs[6] = pepmr+365.132198+AminoAcidProperty.PROTON_W;
			markMZs[7] = (pepmr)/2.0+AminoAcidProperty.PROTON_W;
			markMZs[8] = (pepmr+203.079373)/2.0+AminoAcidProperty.PROTON_W;
			markMZs[9] = (pepmr+365.132198)/2.0+AminoAcidProperty.PROTON_W;
//			System.out.println(scanname+"\t"+peaks.length+"\t"+markMZs.length+"\t"+pepmr+"\t"+Arrays.toString(markMZs));
			pw.println(scanname+"\t"+peaks.length+"\t"+markMZs.length+"\t"+pepmr+"\t"+Arrays.toString(markMZs));
			BufferedImage image = createImage(info, peaks, markMZs);
			String filename = scanname.substring(scanname.indexOf(":") + 1);
			ImageIO.write(image, "PNG",
					new File(out + "\\" + filename + ".png"));
		}
		reader.close();
		pw.close();
//		HashSet<String> set = new HashSet<String>();
//		Iterator<String> it = scanMap.keySet().iterator();
//		while (it.hasNext()) {
//			String key = it.next();
//			String scanname = scanMap.get(key);
//			if (set.contains(scanname))
//				continue;
//			set.add(scanname);

//			OGlycanPepInfo info = totalMap.get(key);
//			System.out.println(scanname);
//			BufferedImage image = createImage(info);
//			String filename = scanname.substring(scanname.indexOf(":") + 1);
//			ImageIO.write(image, "PNG",
//					new File(out + "\\" + filename + ".png"));
//		}
	*/
    }

    public static BufferedImage createImage(OGlycanPepInfo opInfo)
    {

        double BarWidth = 5d;
        IPeak[] peaks = opInfo.getPeaks();
        HashMap<Double, String> matchedPeaks = opInfo.getMatchMap();
        String scanname = opInfo.getScanname();
        scanname = scanname.substring(0, scanname.lastIndexOf("."));
        String sequence = opInfo.getModseq();
        double score = opInfo.getPeptide().getPrimaryScore();
        String title = "Scan:" + scanname + "          " + "Ion score:" + df.format(score) + "\n" + sequence;

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
                    // s3.add(mz, inten);
                    // ans.add3(String.valueOf(mz), new String
                    // []{String.valueOf(mz)}, new Color[]{Color.RED}, mz,
                    // inten);
                    s3.add(mz, inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{Color.RED}, mz, inten);
                } else {
                    s2.add(mz, inten);
                    // ans.add3(String.valueOf(mz), new String
                    // []{String.valueOf(mz)}, new Color[]{new Color(40, 80,
                    // 220)}, mz,
                    // inten);
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
        renderer.setSeriesFillPaint(2, null);
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        XYPlot xyplot = new XYPlot(dataset, numberaxis, domainaxis, renderer);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

        java.awt.Font titleFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 16);
        java.awt.Font legentFont = new java.awt.Font("Times",
                java.awt.Font.PLAIN, 60);
        java.awt.Font labelFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 20);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
                20);

        numberaxis.setAutoRange(true);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(500);
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

    public static BufferedImage createImage(OGlycanPepInfo opInfo, IPeak[] peaks, double[] markmzs)
    {

        double BarWidth = 5d;
        HashMap<Double, String> matchedPeaks = opInfo.getMatchMap();
        String scanname = opInfo.getScanname();
        scanname = scanname.substring(0, scanname.lastIndexOf("."));
        String sequence = opInfo.getModseq();
        double score = opInfo.getPeptide().getPrimaryScore();
        String title = "Scan:" + scanname + "          " + "Ion score:" + df.format(score) + "\n" + sequence;

        Double[] matchedMzs = matchedPeaks.keySet().toArray(new Double[matchedPeaks.size()]);
        XYSeriesCollection collection = new XYSeriesCollection();
        Annotations ans = new Annotations(true);

        XYSeries s1 = new XYSeries("m1");
        XYSeries s2 = new XYSeries("m2");
        XYSeries s3 = new XYSeries("m3");

        IPeak[] markPeaks1 = new IPeak[markmzs.length];
        IPeak[] markPeaks2 = new IPeak[matchedMzs.length];
        double baseIntensity = 0;
        for (int i = 0; i < peaks.length; i++) {
            double mzi = peaks[i].getMz();
            double intensityi = peaks[i].getIntensity();
            if (intensityi > baseIntensity)
                baseIntensity = intensityi;
            for (int j = 0; j < markmzs.length; j++) {
                double mzj = markmzs[j];
                if (Math.abs(mzi - mzj) < 0.1) {
                    if (markPeaks1[j] == null) {
                        markPeaks1[j] = peaks[i];
                    } else {
                        if (intensityi > markPeaks1[j].getIntensity()) {
                            markPeaks1[j] = peaks[i];
                        }
                    }
                }
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
//System.out.println("456\t");
        String[] pepAnno = new String[]{"HexNAc", "NeuAc-H2O", "NeuAc", "HexNAc+Hex", "Pep", "Pep+HexNAc", "Pep+HexNAc+Hex",
                "Pep", "Pep+HexNAc", "Pep+HexNAc+Hex", "Pep", "Pep+HexNAc", "Pep+HexNAc+Hex"};

        for (int i = 0; i < peaks.length; i++) {

            double mz = peaks[i].getMz();
            double inten = peaks[i].getIntensity() / baseIntensity;

            if (matchedPeaks.containsKey(mz)) {

                String ann = matchedPeaks.get(mz);
                if (ann.contains("(")) {
                    // s3.add(mz, inten);
                    // ans.add3(String.valueOf(mz), new String
                    // []{String.valueOf(mz)}, new Color[]{Color.RED}, mz,
                    // inten);
                    s3.add(mz, inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{Color.RED}, mz, inten);
                } else {
                    s2.add(mz, inten);
                    // ans.add3(String.valueOf(mz), new String
                    // []{String.valueOf(mz)}, new Color[]{new Color(40, 80,
                    // 220)}, mz,
                    // inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{new Color(40, 80, 220)}, mz, inten);
                }
            } else {
                boolean mark = false;
                for (int j = 0; j < markPeaks1.length; j++) {
                    if (markPeaks1[j] != null && markPeaks1[j].getMz() == mz) {
                        mark = true;

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
                            // s3.add(mz, inten);
                            // ans.add3(String.valueOf(mz), new String
                            // []{String.valueOf(mz)}, new Color[]{Color.RED}, mz,
                            // inten);
                            s3.add(mz, inten);
                            ans.add3(String.valueOf(mz), new String[]{ann},
                                    new Color[]{Color.RED}, mz, inten);
                        } else {
                            s2.add(mz, inten);
                            // ans.add3(String.valueOf(mz), new String
                            // []{String.valueOf(mz)}, new Color[]{new Color(40, 80,
                            // 220)}, mz,
                            // inten);
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
        for (int i = 0; i < anns3.length; i++) {
            renderer.addAnnotation(anns3[i]);
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
                java.awt.Font.BOLD, 16);
        java.awt.Font legentFont = new java.awt.Font("Times",
                java.awt.Font.PLAIN, 60);
        java.awt.Font labelFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 20);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
                20);

        numberaxis.setAutoRange(true);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(500);
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

    private static void combineOriginalSpectra(String in, String[] mgfs,
            String out) throws IOException, DtaFileParsingException
    {

        PrintWriter writer = new PrintWriter(out);
        String lineSeparator = "\n";
        HashSet<String>[] sets = new HashSet[mgfs.length];
        for (int i = 0; i < sets.length; i++) {
            sets[i] = new HashSet<String>();
        }
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String scanname = line.substring(0, line.lastIndexOf("\t"));
            int fileid = Integer.parseInt(line.substring(line.lastIndexOf("\t") + 1));
            sets[fileid].add(scanname);//System.out.println(scanname+"\t"+line);
        }
        reader.close();

        for (int i = 0; i < mgfs.length; i++) {

            File[] files = (new File(mgfs[i])).listFiles(new FileFilter()
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

            for (int fileid = 0; fileid < files.length; fileid++) {

                System.out.println((fileid + 1) + "\t" + files[fileid].getName());
                MgfReader mr = new MgfReader(files[fileid]);
                MS2Scan ms2scan = null;

                while ((ms2scan = mr.getNextMS2Scan()) != null) {

                    IMS2PeakList peaklist = ms2scan.getPeakList();
                    PrecursePeak pp = peaklist.getPrecursePeak();
                    double mw = pp.getMH() - AminoAcidProperty.PROTON_W;
                    double mz = pp.getMz();
                    short charge = pp.getCharge();
                    String name = ms2scan.getScanName().getScanName();
                    if (name.endsWith(", "))
                        name = name.substring(0, name.length() - 2);
                    name = name + "." + (fileid + 1);//System.out.println(name);

                    if (sets[i].contains(name)) System.out.println(name);
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
        }

        writer.close();
    }

    private static void createHeatMap(String in, String out, int sheet) throws IOException, JXLException
    {

        DecimalFormat dfper = DecimalFormats.DF_PRECENT0_2;
        HashMap<String, int[]> map = new HashMap<String, int[]>();
        ExcelReader reader = new ExcelReader(in, sheet);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            String key = ref + "        " + line[0];
            int[] count = new int[14];
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
            }

            int total = MathTool.getTotal(count);
            if (total < 5) continue;

            map.put(key, count);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < line.length; i++) {
                sb.append(line[i]).append("\t");
            }
            for (int i = 0; i < count.length; i++) {
                sb.append(dfper.format((double) count[i] / (double) total)).append("\t");
            }
            System.out.println(sb);
        }
        reader.close();
        System.out.println(map.size());
        String[] keys = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(keys);

        int unitWeight = 200;
        int unitHeight = 50;

        int width = 14 * unitWeight + 450;
        int height = keys.length * unitHeight + 800;

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

        Graphics graphics = image.getGraphics();
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setBackground(Color.white);
        g2.clearRect(0, 0, width, height);

        for (int i = 0; i < keys.length; i++) {

            int[] counts = map.get(keys[i]);
            int total = MathTool.getTotal(counts);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 40));
            g2.drawString(keys[i], 30, 435 + i * unitHeight);

            for (int j = 0; j < counts.length; j++) {

                int count = counts[j];
                double percent = (double) count / (double) total;
                Color color;
//				int colorid = count>255 ? 255 : count;
                int colorid = (int) (percent * 255.0);

//System.out.print(colorid+"\t");				

                color = new Color(255 - colorid, 255 - colorid, colorid);
//				color = new Color(0, 255, 255);
                g2.setColor(color);

                g2.drawRect(451 + j * unitWeight, 401 + i * unitHeight, unitWeight, unitHeight);
                g2.fillRect(451 + j * unitWeight, 401 + i * unitHeight, unitWeight, unitHeight);
            }
//System.out.println();	
        }
		
		/*g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.PLAIN, 14));
		for (int i = 0; i < countArrays.length; i++) {
//			g2.drawString(String.valueOf(i+1), 140 + i * 120, 40);
			g2.drawLine(181 + i * 120, 51, 181 + i * 120, 100);
		}
		for (int j = 0; j < countArrays[0].length; j++) {
//			g2.drawString(String.valueOf(j+1), 8, 148 + j * 80);
			g2.drawLine(71, 141 + j * 80, 120, 141 + j * 80);
		}*/

//		int barlength = width/20;
//		int barheight = barlength/6;

        g2.setFont(new Font("Arial", Font.PLAIN, 50));

        for (int i = 0; i < 5; i++) {

            int colorid = (int) (0.2 * (double) i * 255.0);

            Color color = new Color(255 - colorid, 255 - colorid, colorid);

            g2.setColor(color);

            g2.drawRect(451 + i * unitWeight, 551 + keys.length * unitHeight, unitWeight, unitHeight);
            g2.fillRect(451 + i * unitWeight, 551 + keys.length * unitHeight, unitWeight, unitHeight);

            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(20 * i) + "%", 451 + i * unitWeight, 685 + keys.length * unitHeight);
        }
		
		/*g2.setFont(new Font("Arial", Font.PLAIN, 22));
		g2.drawString("High mannose", 480, 171 + countArrays[0].length * 80);
		g2.drawString("Complex/Hybrid", 480, 231 + countArrays[0].length * 80);*/

        ImageIO.write(image, "PNG", new File(out));
    }

    private static void createHeatMaps(String normal, String HCC,
            String normalOut, String HCCOut) throws IOException, JXLException
    {

        DecimalFormat dfper = DecimalFormats.DF_PRECENT0_2;
        HashMap<String, int[]> map1 = new HashMap<String, int[]>();
        ExcelReader reader1 = new ExcelReader(normal, 1);
        String[] line = reader1.readLine();
        while ((line = reader1.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            String key = ref + "        " + line[0];
            int[] count = new int[15];
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
            }

            int total = MathTool.getTotal(count);
            if (total < 10)
                continue;

            map1.put(key, count);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < line.length; i++) {
                sb.append(line[i]).append("\t");
            }
            for (int i = 0; i < count.length; i++) {
                sb.append(dfper.format((double) count[i] / (double) total))
                        .append("\t");
            }
            System.out.println(sb);
        }
        reader1.close();

        HashMap<String, int[]> map2 = new HashMap<String, int[]>();
        ExcelReader reader2 = new ExcelReader(HCC, 1);
        line = reader2.readLine();
        while ((line = reader2.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            String key = ref + "        " + line[0];
            int[] count = new int[15];
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
            }

            int total = MathTool.getTotal(count);
            if (total < 10)
                continue;

            map2.put(key, count);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < line.length; i++) {
                sb.append(line[i]).append("\t");
            }
            for (int i = 0; i < count.length; i++) {
                sb.append(dfper.format((double) count[i] / (double) total))
                        .append("\t");
            }
            System.out.println(sb);
        }
        reader2.close();

        Iterator<String> it1 = map1.keySet().iterator();
        while (it1.hasNext()) {
            String key = it1.next();
            if (!map2.containsKey(key)) {
                it1.remove();
            }
        }

        Iterator<String> it2 = map2.keySet().iterator();
        while (it2.hasNext()) {
            String key = it2.next();
            if (!map1.containsKey(key)) {
                it2.remove();
            }
        }

        draw(map1, normalOut);
        draw(map2, HCCOut);
    }

    private static void createProHeatMap(String in, String out) throws IOException, JXLException
    {
        DecimalFormat dfper = DecimalFormats.DF_PRECENT0_2;
        HashMap<String, int[]> map = new HashMap<String, int[]>();
        ExcelReader reader = new ExcelReader(in, 3);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            int[] count = new int[15];
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
            }

//			int total = MathTool.getTotal(count);
//			if(total<10) continue;
            if (map.containsKey(ref)) {
                int[] allcount = map.get(ref);
                for (int i = 0; i < allcount.length; i++) {
                    allcount[i] += count[i];
                }
                map.put(ref, allcount);
            } else {
                map.put(ref, count);
            }
			
			/*StringBuilder sb = new StringBuilder();
			for(int i=0;i<line.length;i++){
				sb.append(line[i]).append("\t");
			}
			for(int i=0;i<count.length;i++){
				sb.append(dfper.format((double)count[i]/(double)total)).append("\t");
			}
			System.out.println(sb);*/
        }
        reader.close();

        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            int[] count = map.get(key);
            int total = MathTool.getTotal(count);
            if (total < 10) {
                it.remove();
            }
        }

        String[] keys = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(keys);

        draw(map, out);
    }

    private static void createProHeatMaps(String normal, String HCC,
            String normalOut, String HCCOut) throws IOException, JXLException
    {

        DecimalFormat dfper = DecimalFormats.DF_PRECENT0_2;
        HashMap<String, int[]> map1 = new HashMap<String, int[]>();
        ExcelReader reader1 = new ExcelReader(normal, 1);
        String[] line = reader1.readLine();
        while ((line = reader1.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            int[] count = new int[15];
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
            }

            if (map1.containsKey(ref)) {
                int[] allcount = map1.get(ref);
                for (int i = 0; i < allcount.length; i++) {
                    allcount[i] += count[i];
                }
                map1.put(ref, allcount);
            } else {
                map1.put(ref, count);
            }
        }
        reader1.close();

        HashMap<String, int[]> map2 = new HashMap<String, int[]>();
        ExcelReader reader2 = new ExcelReader(HCC, 1);
        line = reader2.readLine();
        while ((line = reader2.readLine()) != null) {
            String ref = line[2].split("\\|")[1];
            int[] count = new int[15];
            for (int i = 0; i < count.length; i++) {
                count[i] = Integer.parseInt(line[i + 3]);
            }

            if (map2.containsKey(ref)) {
                int[] allcount = map2.get(ref);
                for (int i = 0; i < allcount.length; i++) {
                    allcount[i] += count[i];
                }
                map2.put(ref, allcount);
            } else {
                map2.put(ref, count);
            }
        }
        reader2.close();

        Iterator<String> it1 = map1.keySet().iterator();
        while (it1.hasNext()) {
            String key = it1.next();
            if (!map2.containsKey(key)) {
                it1.remove();
            }
        }

        Iterator<String> it2 = map2.keySet().iterator();
        while (it2.hasNext()) {
            String key = it2.next();
            if (!map1.containsKey(key)) {
                it2.remove();
            }
        }

        draw(map1, normalOut);
        draw(map2, HCCOut);
    }

    private static void draw(HashMap<String, int[]> map, String out) throws IOException
    {

        String[] keys = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(keys);

        int unitWeight = 200;
        int unitHeight = 50;

        int width = 15 * unitWeight + 450;
        int height = keys.length * unitHeight + 800;

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

        Graphics graphics = image.getGraphics();
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setBackground(Color.white);
        g2.clearRect(0, 0, width, height);

        for (int i = 0; i < keys.length; i++) {

            int[] counts = map.get(keys[i]);
            int total = MathTool.getTotal(counts);
            System.out.println(keys[i] + "\t" + total + "\t" + Arrays.toString(counts));
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 40));
            g2.drawString(keys[i], 30, 435 + i * unitHeight);

            for (int j = 0; j < counts.length; j++) {

                int count = counts[j];
                double percent = (double) count / (double) total;
                Color color;
                // int colorid = count>255 ? 255 : count;
                int colorid = (int) (percent * 255.0);

                // System.out.print(colorid+"\t");

                color = new Color(colorid, 255 - colorid, 0);
                // color = new Color(0, 255, 255);
                g2.setColor(color);

                g2.drawRect(451 + j * unitWeight, 401 + i * unitHeight,
                        unitWeight, unitHeight);
                g2.fillRect(451 + j * unitWeight, 401 + i * unitHeight,
                        unitWeight, unitHeight);
            }
            // System.out.println();
        }

        /*
         * g2.setColor(Color.BLACK); g2.setFont(new Font("Arial", Font.PLAIN,
         * 14)); for (int i = 0; i < countArrays.length; i++) { //
         * g2.drawString(String.valueOf(i+1), 140 + i * 120, 40);
         * g2.drawLine(181 + i * 120, 51, 181 + i * 120, 100); } for (int j = 0;
         * j < countArrays[0].length; j++) { //
         * g2.drawString(String.valueOf(j+1), 8, 148 + j * 80); g2.drawLine(71,
         * 141 + j * 80, 120, 141 + j * 80); }
         */

        // int barlength = width/20;
        // int barheight = barlength/6;

        g2.setFont(new Font("Arial", Font.PLAIN, 50));

        for (int i = 0; i < 5; i++) {

            int colorid = (int) (0.2 * (double) i * 255.0);

            Color color = new Color(colorid, 255 - colorid, 0);

            g2.setColor(color);

            g2.drawRect(451 + i * unitWeight, 551 + keys.length * unitHeight,
                    unitWeight, unitHeight);
            g2.fillRect(451 + i * unitWeight, 551 + keys.length * unitHeight,
                    unitWeight, unitHeight);

            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(20 * i) + "%", 451 + i * unitWeight,
                    685 + keys.length * unitHeight);
        }

        /*
         * g2.setFont(new Font("Arial", Font.PLAIN, 22));
         * g2.drawString("High mannose", 480, 171 + countArrays[0].length * 80);
         * g2.drawString("Complex/Hybrid", 480, 231 + countArrays[0].length *
         * 80);
         */

        ImageIO.write(image, "PNG", new File(out));
    }


    /**
     * @param args
     * @throws IOException
     * @throws MoreThanOneRefFoundInFastaException
     * @throws ProteinNotFoundInFastaException
     * @throws PeptideParsingException
     * @throws FileDamageException
     * @throws NumberFormatException
     * @throws DtaFileParsingException
     * @throws JXLException
     */
    public static void main(String[] args) throws NumberFormatException,
            FileDamageException, PeptideParsingException,
            ProteinNotFoundInFastaException,
            MoreThanOneRefFoundInFastaException, IOException,
            DtaFileParsingException, JXLException
    {
        // TODO Auto-generated method stub

        long begin = System.currentTimeMillis();

        String p1 =
                "H:\\OGLYCAN\\OGlycan_20140405\\trypsin_20140405\\trypsin_20140405.oglycan.F002393.dat.ppl";
        String i1 =
                "H:\\OGLYCAN\\OGlycan_20140405\\trypsin_20140405\\trypsin_20140405.info";
        String p2 =
                "H:\\OGLYCAN\\OGlycan_20140405\\TC_20140405\\TC_20140405.oglycan.F002394.dat.ppl";
        String i2 =
                "H:\\OGLYCAN\\OGlycan_20140405\\TC_20140405\\TC_20140405.info";
        String p3 =
                "H:\\OGLYCAN\\OGlycan_20140405\\elastase_20140405\\elastase_20140405.combine.ppl";
        String i3 =
                "H:\\OGLYCAN\\OGlycan_20140405\\elastase_20140405\\elastase_20140405.info";
        String out =
                "H:\\OGLYCAN\\OGlycan_20140405\\spectra all";
//		String mgf = "D:\\P\\o-glyco\\2014.03.04.revise\\combine2.original.mgf";
        // OGlycanSpectraDrawer.write(i1, p1, i2, p2, i3, p3, out);

//		String in = "D:\\P\\o-glyco\\2014.03.04.revise\\scans2.txt";
        String mgf1 = "H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\2D_trypsin";
        String mgf2 = "H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\2D_T+C";
        String mgf3 = "H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\2D_elastase";
//		OGlycanSpectraDrawer.combineOriginalSpectra(in, new String[]{mgf1, mgf2, mgf3}, mgf);

//		String[] infos = new String[]{i1, i2, i3};
//		String[] ppls = new String[]{p1, p2, p3};
//		String[] mgfs = new String[]{mgf1, mgf2, mgf3};
        String[] infos = new String[]{"H:\\OGLYCAN\\OGlycan_20140405\\fetuin_20140405\\fetuin_20140405.info"};
        String[] ppls = new String[]{"H:\\OGLYCAN\\OGlycan_20140405\\fetuin_20140405\\fetuin_20140405.oglycan.F002407.dat.ppl"};
        String[] mgfs = new String[]{"H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\fetuin"};
//		OGlycanSpectraDrawer.writer(infos, ppls, mgf, out);
//		OGlycanSpectraDrawer.writerAll(infos, ppls, mgfs, "H:\\OGLYCAN\\OGlycan_20140405\\fetuin_20140405\\fetuin spectra");

        OGlycanSpectraDrawer.createHeatMap("H:\\OGLYCAN2\\20141211_14glyco\\serum.count.info.20150116.xls",
                "H:\\OGLYCAN2\\20141211_14glyco\\serum.png", 1);
//		OGlycanSpectraDrawer.createHeatMap("H:\\OGLYCAN2\\20141113_normal_HCC\\normal.combine.1205.xls", 
//				"H:\\OGLYCAN2\\Figure\\heatmap\\normal.combine.1205.png", 1);
//		OGlycanSpectraDrawer.createHeatMap("H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.1205.xls", 
//				"H:\\OGLYCAN2\\Figure\\heatmap\\HCC.combine.1205.png", 1);
		
		/*OGlycanSpectraDrawer.createProHeatMap("H:\\OGLYCAN2\\20141024_15glyco\\2D_combine.xls", 
				"H:\\OGLYCAN2\\Figure\\heatmap\\normal-1.pro.png");
		OGlycanSpectraDrawer.createProHeatMap("H:\\OGLYCAN2\\20141113_normal_HCC\\normal.combine.xls", 
				"H:\\OGLYCAN2\\Figure\\heatmap\\normal-2.pro.png");
		OGlycanSpectraDrawer.createProHeatMap("H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.xls", 
				"H:\\OGLYCAN2\\Figure\\heatmap\\HCC-1.pro.png");*/
		
		/*OGlycanSpectraDrawer.createHeatMaps("H:\\OGLYCAN2\\20141024_15glyco\\2D_combine55.xls", 
				"H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine55.xls", "H:\\OGLYCAN2\\Figure\\normal1_heat.png", 
				"H:\\OGLYCAN2\\Figure\\HCC1_heat.png");*/
		/*OGlycanSpectraDrawer.createHeatMaps("H:\\OGLYCAN2\\20141113_normal_HCC\\normal.combine.1205.xls", 
				"H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.1205.xls", "H:\\OGLYCAN2\\Figure\\normal2_heat.png", 
				"H:\\OGLYCAN2\\Figure\\HCC2_heat.png");*/

//		OGlycanSpectraDrawer.createProHeatMaps("H:\\OGLYCAN2\\20141024_15glyco\\2D_combine.xls", 
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.xls", "H:\\OGLYCAN2\\Figure\\heatmap\\compare1.normal.pro.png", 
//				"H:\\OGLYCAN2\\Figure\\heatmap\\compare1.HCC.pro.png");
//		OGlycanSpectraDrawer.createProHeatMaps("H:\\OGLYCAN2\\20141113_normal_HCC\\normal.combine.xls", 
//				"H:\\OGLYCAN2\\20141113_normal_HCC\\HCC.combine.xls", "H:\\OGLYCAN2\\Figure\\heatmap\\compare2.normal.pro.high.png", 
//				"H:\\OGLYCAN2\\Figure\\heatmap\\compare2.HCC.pro.high.png");

        long end = System.currentTimeMillis();
        System.out.println((end - begin) / 60000.0 + "min");
    }

}
