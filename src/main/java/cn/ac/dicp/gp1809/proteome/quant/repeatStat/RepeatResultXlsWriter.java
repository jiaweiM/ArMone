/*
 ******************************************************************************
 * File: RepeatResultXlsWriter.java * * * Created on 2011-11-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.repeatStat;

import cn.ac.dicp.gp1809.proteome.IO.proteome.*;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFreePairXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.LFreePeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ck
 * @version 2011-11-15, 14:11:09
 */
public class RepeatResultXlsWriter
{

    private File[] files;
    private String output;
    private LabelType type;
    private ExcelWriter writer;

    private int[][] proRatioStat;
    private int[][] pepRatioStat;
    private double[][] range;

    private int[] proSummary;
    private int[] pepSummary;
    private int totalNum;
    private int totalPepNum;

    private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;

    public RepeatResultXlsWriter(File[] files, String output, LabelType type)
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

    public void stat(boolean normal, String[] ratioNames, double[] theoryRatios,
            double[] usedTheoryRatios, int[] outputRatio) throws Exception
    {

        this.writer = new ExcelWriter(output, new String[]{"Protein", "Peptide"});
        ExcelFormat f1 = new ExcelFormat(true, 0);
//		ExcelFormat f2 = new ExcelFormat(true, 2);

        int ratioNum = ratioNames.length;
        proRatioStat = new int[ratioNum][5];
        pepRatioStat = new int[ratioNum][5];
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
        this.pepSummary = new int[fileNum];

        String[] fileNames = new String[fileNum];
        HashMap<String, IPeptide> pepmap = new HashMap<String, IPeptide>();
        HashMap<String, double[]>[] pepRatioMaps = new HashMap[fileNum];

        ProteinNameAccesser accesser = null;

        for (int i = 0; i < fileNum; i++) {

            String name = files[i].getName();
            fileNames[i] = name.substring(0, name.length() - 5);
            pepRatioMaps[i] = new HashMap<String, double[]>();

            if (type == LabelType.LabelFree) {

                LFreePairXMLReader xmlreader = new LFreePairXMLReader(files[i]);
                xmlreader.getAllMods();
                xmlreader.readAllPairs();
                xmlreader.setTheoryRatio(theoryRatios);

                if (accesser == null) {
                    accesser = xmlreader.getProNameAccesser();
                } else {
                    accesser.appand(xmlreader.getProNameAccesser());
                }

                LFreePeptidePair[] pairs = xmlreader.getAllSelectedPairs(normal, outputRatio);

                for (int j = 0; j < pairs.length; j++) {

                    double[] ratio = pairs[j].getSelectRatio();
                    IPeptide pep = pairs[j].getPeptide();
                    String seq = PeptideUtil.getSequence(pairs[j].getSequence());
                    pepRatioMaps[i].put(seq, ratio);

                    if (pepmap.containsKey(seq)) {

                        IPeptide p0 = pepmap.get(seq);
                        p0.getProteinReferences().addAll(pep.getProteinReferences());
                        p0.getPepLocAroundMap().putAll(pep.getPepLocAroundMap());

                    } else {

                        pepmap.put(seq, pep);
                    }
                }

                xmlreader.close();

            } else {

                LabelFeaturesXMLReader xmlreader = new LabelFeaturesXMLReader(files[i]);
//				xmlreader.readAllFeatures();
                xmlreader.setTheoryRatio(theoryRatios);

                if (accesser == null) {
                    accesser = xmlreader.getProNameAccesser();
                } else {
                    accesser.appand(xmlreader.getProNameAccesser());
                }

                PeptidePair[] pairs = xmlreader.getAllSelectedPairs(normal, outputRatio);

                for (int j = 0; j < pairs.length; j++) {

                    double[] ratio = pairs[j].getFeatures().getSelectRatio();
                    IPeptide pep = pairs[j].getPeptide();
                    String seq = PeptideUtil.getSequence(pairs[j].getSequence());
                    pepRatioMaps[i].put(seq, ratio);

                    if (pepmap.containsKey(seq)) {

                        IPeptide p0 = pepmap.get(seq);
                        p0.getProteinReferences().addAll(pep.getProteinReferences());
                        p0.getPepLocAroundMap().putAll(pep.getPepLocAroundMap());

                    } else {

                        pepmap.put(seq, pep);
                    }
                }

                xmlreader.close();
            }
        }

        this.addTitle(ratioNames, fileNames);

        Proteins2 pros = new Proteins2(accesser);
        int count = 0;
        HashSet<String> usedPepSet = new HashSet<String>();
        Iterator<String> it = pepmap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            IPeptide pep = pepmap.get(key);
            pros.addPeptide(pep);
        }

        Protein[] prolist = pros.getAllProteins();
        for (int i = 0; i < prolist.length; i++) {

            Protein pro = prolist[i];
            IReferenceDetail[] refs = pro.getReferences();
            String[] refName = new String[refs.length];
            for (int j = 0; j < refs.length; j++) {
                refName[j] = refs[j].getName();
            }

            IPeptide[] peps = pro.getAllPeptides();
            HashSet<String> seqSet = new HashSet<String>();
            ArrayList<PepStatInfo> pepInfoList = new ArrayList<PepStatInfo>();
            boolean[] have = new boolean[fileNum];
            Arrays.fill(have, false);

            for (int j = 0; j < peps.length; j++) {

                IPeptide p = peps[j];
                String seq = PeptideUtil.getSequence(p.getSequence());
                if (seqSet.contains(seq))
                    continue;

                seqSet.add(seq);
                double[][] ratios = new double[ratioNum][fileNum];
                int num = 0;

                for (int l = 0; l < fileNum; l++) {
                    if (pepRatioMaps[l].containsKey(seq)) {
                        double[] pairRatio = pepRatioMaps[l].get(seq);
                        for (int m = 0; m < pairRatio.length; m++) {
                            ratios[m][l] = pairRatio[m];
                        }
                        have[l] = true;
                        num++;
                    } else {
                        for (int m = 0; m < ratioNum; m++) {
                            ratios[m][l] = 0;
                        }
                    }
                }

                PepStatInfo pepInfo = new PepStatInfo(p.getSequence(), num, ratios);
                pepInfoList.add(pepInfo);

                if (!usedPepSet.contains(seq)) {
                    totalPepNum++;
                    pepSummary[num - 1]++;
                    double[] pr = pepInfo.getAve();
                    for (int k = 0; k < pr.length; k++) {
                        if (pr[k] >= range[k][3]) {
                            this.pepRatioStat[k][4]++;
                        } else if (pr[k] < range[k][3] && pr[k] >= range[k][2]) {
                            this.pepRatioStat[k][3]++;
                        } else if (pr[k] < range[k][2] && pr[k] >= range[k][1]) {
                            this.pepRatioStat[k][2]++;
                        } else if (pr[k] < range[k][1] && pr[k] >= range[k][0]) {
                            this.pepRatioStat[k][1]++;
                        } else if (pr[k] < range[k][0] && pr[k] > 0) {
                            this.pepRatioStat[k][0]++;
                        }
                    }
                    writer.addContent(totalPepNum + "\t" + pepInfo.toString(), 1, f1);
                }
            }

            PepStatInfo[] pepInfos = pepInfoList.toArray(new PepStatInfo[pepInfoList.size()]);
            int num = 0;
            for (int j = 0; j < have.length; j++) {
                if (have[j])
                    num++;
            }

            if (num != 0) {
                count++;
                ProStatInfo info = new ProStatInfo(count, refName, num, pepInfos, ratioNum, fileNum);
//				if(pro.getUnique()){
                totalNum++;
                proSummary[num - 1]++;
                double[] ave = info.getAve();
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

                writer.addContent(info.toString(), 0, f1);
                PepStatInfo[] pepInfoArray = info.getPepInfo();
                for (int k = 0; k < pepInfoArray.length; k++) {
                    writer.addContent(" \t" + pepInfoArray[k].toStringProFormat(), 0, f1);
                }
/*
				}else{

					writer.addContent(info.toString(), 0, f2);
					PepStatInfo [] pepInfoArray = info.getPepInfo();
					for(int k=0;k<pepInfoArray.length;k++){
						writer.addContent(" \t"+pepInfoArray[k].toString(), 0, f2);
					}
				}
*/
            }
        }

        this.addSummary(fileNum, ratioNames);
        this.writer.close();
        System.gc();
    }

    private void addTitle(String[] ratioNames, String[] fileNames)
            throws RowsExceededException, WriteException
    {

        StringBuilder sb = new StringBuilder();
        sb.append("Index\t").append("Reference\t").append("Replicate Num\t");

        for (int i = 0; i < ratioNames.length; i++) {
            sb.append("Ave_" + ratioNames[i])
                    .append("\tRSD\t").append("P-value\t");
            for (int k = 0; k < fileNames.length; k++) {
                sb.append(fileNames[k]).append("\t");
            }
        }

        ExcelFormat f = new ExcelFormat(false, 0);
        this.writer.addTitle(sb.toString(), 0, f);

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Index\t").append("Sequence\t").append("Replicate Num\t");
        for (int i = 0; i < ratioNames.length; i++) {
            sb2.append("Ave_" + ratioNames[i]).append("\tRSD\t");
            for (int k = 0; k < fileNames.length; k++) {
                sb2.append(fileNames[k]).append("\t");
            }
        }

        this.writer.addTitle(sb2.toString(), 1, f);
    }

    private void addSummary(int fileNum, String[] ratioNames) throws RowsExceededException, WriteException
    {

        ExcelFormat f1 = new ExcelFormat(false, 0);
        writer.addTitle("\n\n\n-------------------Summary-------------------\n", 0, f1);
        StringBuilder sb = new StringBuilder("\n");
        sb.append("\tTotal Protein\tPercent\t").append("Total Peptide\tPercent\n");
        for (int i = fileNum; i > 0; i--) {
            sb.append("In " + i + " files:\t").append(proSummary[i - 1]).append("\t").
                    append(dfPer.format((float) proSummary[i - 1] / (float) totalNum)).
                    append("\t").append(pepSummary[i - 1]).append("\t").
                    append(dfPer.format((float) pepSummary[i - 1] / (float) totalPepNum)).
                    append("\n");
        }
        sb.append("\n\n");

        int index = 0;
        for (int i = 0; i < ratioNames.length; i++) {
            sb.append(ratioNames[i] + "\t").append("Protein Ratio\t").append("Percent\t")
                    .append("Peptide Ratio\t").append("Percent\n");
            sb.append("Ratio: >=2\t").append(proRatioStat[index][4]).append("\t").append(dfPer.format((float) proRatioStat[index][4] / totalNum)).append("\t")
                    .append(pepRatioStat[index][4]).append("\t").append(dfPer.format((float) pepRatioStat[index][4] / totalPepNum)).append("\n");
            sb.append("Ratio: 1.2~2\t").append(proRatioStat[index][3]).append("\t").append(dfPer.format((float) proRatioStat[index][3] / totalNum)).append("\t")
                    .append(pepRatioStat[index][3]).append("\t").append(dfPer.format((float) pepRatioStat[index][3] / totalPepNum)).append("\n");
            sb.append("Ratio: 0.8~1.2\t").append(proRatioStat[index][2]).append("\t").append(dfPer.format((float) proRatioStat[index][2] / totalNum)).append("\t")
                    .append(pepRatioStat[index][2]).append("\t").append(dfPer.format((float) pepRatioStat[index][2] / totalPepNum)).append("\n");
            sb.append("Ratio: 0.5~0.8\t").append(proRatioStat[index][1]).append("\t").append(dfPer.format((float) proRatioStat[index][1] / totalNum)).append("\t")
                    .append(pepRatioStat[index][1]).append("\t").append(dfPer.format((float) pepRatioStat[index][1] / totalPepNum)).append("\n");
            sb.append("Ratio: <0.5\t").append(proRatioStat[index][0]).append("\t").append(dfPer.format((float) proRatioStat[index][0] / totalNum)).append("\t")
                    .append(pepRatioStat[index][0]).append("\t").append(dfPer.format((float) pepRatioStat[index][0] / totalPepNum)).append("\n");
            sb.append("Total\t").append(totalNum).append("\t").append("100%").append("\t")
                    .append(totalPepNum).append("\t").append("100%").append("\n");
            sb.append("\n\n");
            index++;
        }

        ExcelFormat f2 = new ExcelFormat(true, 0);
        this.writer.addContent(sb.toString(), 0, f2);
    }

}
