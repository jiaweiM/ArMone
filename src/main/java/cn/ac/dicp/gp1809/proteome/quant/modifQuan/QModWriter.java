/*
 ******************************************************************************
 * File:QModWriter.java * * * Created on 2010-7-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelQuanUnit;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.FeaturesPagedRowGetter;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 * @version 2010-7-21, 13:53:41
 */
public class QModWriter
{

    private QModCalculator calculator;
    private QuanResult[] quanResults;
    private ExcelWriter writer;
    private int index;
    private int count;
    private int proCount;
    private double[][] proRatio;
    private double[][] abRatio;
    private double[][] reRatio;
    private String[] ratioNames;
    private double[][] range;
    private LabelType type;

    private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;
    private DecimalFormat df4 = DecimalFormats.DF0_4;

    public QModWriter(String output, boolean useRelaProRatio, String proRatio, FeaturesPagedRowGetter getter,
            ModInfo[] mods, boolean noModPep, boolean normal, String[] ratioNames,
            int[] outputids, double[] theoryRatio, double[] usedTheoryRatio) throws Exception
    {

        this.writer = new ExcelWriter(output, new String[]{"Protein", "Peptide", "Site"});
        int ratioNum = ratioNames.length;
        this.type = getter.getType();

        this.ratioNames = ratioNames;
        this.proRatio = new double[ratioNum][5];
        this.abRatio = new double[ratioNum][5];
        this.reRatio = new double[ratioNum][5];

        getter.setTheoryRatio(theoryRatio);
        this.quanResults = getter.getAllResult(noModPep, normal, outputids);
        if (useRelaProRatio)
            this.calculator = new QModCalculator(mods, proRatio, type, noModPep, normal,
                    theoryRatio, outputids);
        else
            this.calculator = new QModCalculator(mods);

        this.range = new double[ratioNum][4];
        for (int i = 0; i < usedTheoryRatio.length; i++) {
            double tr = usedTheoryRatio[i];
            range[i][0] = tr * 0.5;
            range[i][1] = tr * 0.8;
            range[i][2] = tr * 1.2;
            range[i][3] = tr * 2.0;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

    public void write() throws RowsExceededException, WriteException
    {

        this.addTitle();

        for (int i = 0; i < quanResults.length; i++) {
            QModsResult[] result = this.calculator.calculte(quanResults[i]);
            if (result != null) {
                this.add(result);
            }
        }

        this.addSummary();
        this.addPepInfo();
        this.addAllSite();
    }

    /**
     * This QModResult arrays are a group of modif quantitation result, which are different quantitation result of
     * different modif type from the same protein group.
     *
     * @param result
     * @throws WriteException
     * @throws RowsExceededException
     */
    private void add(QModsResult[] result) throws RowsExceededException, WriteException
    {

        ExcelFormat f1 = new ExcelFormat(true, 0);
        ExcelFormat f2 = new ExcelFormat(true, 1);
        ExcelFormat f3 = new ExcelFormat(true, 2);

        if (judge(result)) {
            for (int i = 0; i < result.length; i++) {
                index++;
                result[i].setIndex(index);
                if (result[i].getUnique()) {
                    if (result[i].getGroup()) {
                        writer.addContent(result[i].toString(), 0, f3);
                    } else {
                        writer.addContent(result[i].toString(), 0, f1);
                    }
                } else {
                    writer.addContent(result[i].toString(), 0, f2);
                }

                double[] ratio = result[i].getProRatio();
                for (int k = 0; k < ratio.length; k++) {
                    if (ratio[k] < range[k][0] && ratio[k] > 0)
                        proRatio[k][0]++;
                    else if (ratio[k] >= range[k][0] && ratio[k] < range[k][1])
                        proRatio[k][1]++;
                    else if (ratio[k] >= range[k][1] && ratio[k] < range[k][2])
                        proRatio[k][2]++;
                    else if (ratio[k] >= range[k][2] && ratio[k] < range[k][3])
                        proRatio[k][3]++;
                    else if (ratio[k] >= range[k][3])
                        proRatio[k][4]++;
                }
                proCount++;
            }
        }
    }

    private boolean judge(QModsResult[] result)
    {

        boolean use = false;
        for (int i = 0; i < result.length; i++) {
            QModResult[] modResult = result[i].getQModResult();
            for (int j = 0; j < modResult.length; j++) {
                HashMap<Integer, ModifLabelPair> pairMap = modResult[j].getPairMap();
                if (pairMap.size() > 0) {
                    Iterator<Integer> it = pairMap.keySet().iterator();
                    while (it.hasNext()) {
                        Integer site = it.next();
                        ModifLabelPair mp = pairMap.get(site);
                        double[] ab = mp.getRatio();
                        double[] re = mp.getRelaRatio();
                        for (int k = 0; k < ab.length; k++) {
                            if (ab[k] < range[k][0] && ab[k] > range[k][0])
                                abRatio[k][0]++;
                            else if (ab[k] >= range[k][0] && ab[k] < range[k][1])
                                abRatio[k][1]++;
                            else if (ab[k] >= range[k][1] && ab[k] < range[k][2])
                                abRatio[k][2]++;
                            else if (ab[k] >= range[k][2] && ab[k] < range[k][3])
                                abRatio[k][3]++;
                            else if (ab[k] >= range[k][3])
                                abRatio[k][4]++;

                            if (re[k] < range[k][0] && re[k] > 0)
                                reRatio[k][0]++;
                            else if (re[k] >= range[k][0] && re[k] < range[k][1])
                                reRatio[k][1]++;
                            else if (re[k] >= range[k][1] && re[k] < range[k][2])
                                reRatio[k][2]++;
                            else if (re[k] >= range[k][2] && re[k] < range[k][3])
                                reRatio[k][3]++;
                            else if (re[k] >= range[k][3])
                                reRatio[k][4]++;
                        }
                        count++;
                    }
                    use = true;
                }
            }
        }
        return use;
    }

    private void addTitle() throws RowsExceededException, WriteException
    {
        StringBuilder sb = new StringBuilder();

        sb.append("Index\t").append("Reference\t");
        for (int i = 0; i < ratioNames.length; i++) {
            sb.append(ratioNames[i]).append("\tRSD\t");
        }

        sb.append("\n");

        sb.append("Modification\t").append("Mod_Site\t").append("Sequence Around\t");
        for (int i = 0; i < ratioNames.length; i++) {
            sb.append("Absolute Ratio_" + ratioNames[i] + "\t");
            sb.append("Relative Ratio_" + ratioNames[i] + "\t");
        }

        sb.append("Peptide");
        ExcelFormat f = new ExcelFormat(false, 0);
        this.writer.addTitle(sb.toString(), 0, f);
    }

    private void addSummary() throws RowsExceededException, WriteException
    {

        ExcelFormat f1 = new ExcelFormat(false, 0);
        ExcelFormat f2 = new ExcelFormat(true, 0);
        writer.addBlankRow(0);
        writer.addBlankRow(0);
        writer.addBlankRow(0);
        writer.addTitle("-------------------Summary-------------------\n", 0, f1);

        for (int i = 0; i < ratioNames.length; i++) {

            StringBuilder sb = new StringBuilder();
            sb.append(ratioNames[i] + "\t").append("Protein Ratio\t").append("Percent\t")
                    .append("Absolute Ratio\t").append("Percent\t")
                    .append("Relative Ratio\t").append("Percent\n");
            sb.append("Ratio: >=200%\t").append(proRatio[i][4]).append("\t")
                    .append(dfPer.format((float) proRatio[i][4] / proCount)).append("\t")
                    .append(abRatio[i][4]).append("\t")
                    .append(dfPer.format((float) abRatio[i][4] / count)).append("\t")
                    .append(reRatio[i][4]).append("\t")
                    .append(dfPer.format((float) reRatio[i][4] / count)).append("\n");
            sb.append("Ratio: 120%~200%\t").append(proRatio[i][3]).append("\t")
                    .append(dfPer.format((float) proRatio[i][3] / proCount)).append("\t")
                    .append(abRatio[i][3]).append("\t")
                    .append(dfPer.format((float) abRatio[i][3] / count)).append("\t")
                    .append(reRatio[i][3]).append("\t")
                    .append(dfPer.format((float) reRatio[i][3] / count)).append("\n");
            sb.append("Ratio: 80%~120%\t").append(proRatio[i][2]).append("\t")
                    .append(dfPer.format((float) proRatio[i][2] / proCount)).append("\t")
                    .append(abRatio[i][2]).append("\t")
                    .append(dfPer.format((float) abRatio[i][2] / count)).append("\t")
                    .append(reRatio[i][2]).append("\t")
                    .append(dfPer.format((float) reRatio[i][2] / count)).append("\n");
            sb.append("Ratio: 50%~80%\t").append(proRatio[i][1]).append("\t")
                    .append(dfPer.format((float) proRatio[i][1] / proCount)).append("\t")
                    .append(abRatio[i][1]).append("\t")
                    .append(dfPer.format((float) abRatio[i][1] / count)).append("\t")
                    .append(reRatio[i][1]).append("\t")
                    .append(dfPer.format((float) reRatio[i][1] / count)).append("\n");
            sb.append("Ratio: <50%\t").append(proRatio[i][0]).append("\t")
                    .append(dfPer.format((float) proRatio[i][0] / proCount)).append("\t")
                    .append(abRatio[i][0]).append("\t")
                    .append(dfPer.format((float) abRatio[i][0] / count)).append("\t")
                    .append(reRatio[i][0]).append("\t")
                    .append(dfPer.format((float) reRatio[i][0] / count)).append("\n");
            sb.append("Total\t").append(proCount).append("\t").append("100%\t")
                    .append(count).append("\t").append("100%");

            writer.addContent(sb.toString(), 0, f2);
            writer.addBlankRow(0);
        }
    }

    private void addPepInfo() throws RowsExceededException, WriteException
    {

        ExcelFormat f = new ExcelFormat(false, 0);
        StringBuilder sb = new StringBuilder();
        sb.append("Mod_site_count\t").append("Sequence\t");

        String name = type.getLabelName();
        int num = type.getLabelNum();

        for (int i = 0; i < ratioNames.length; i++) {
            sb.append(ratioNames[i]).append("\t");
        }

        for (int i = 0; i < num; i++) {
            sb.append(name).append("_" + (i + 1)).append("\t");
        }
        sb.append("Reference");
        this.writer.addTitle(sb.toString(), 1, f);

        double[][] pepRatio = new double[type.getRatioNum()][5];

        HashMap<String, HashSet<LabelQuanUnit>> pairMap = calculator.getTotalPair();

        Iterator<String> it = pairMap.keySet().iterator();
        while (it.hasNext()) {
            String seq = it.next();
            HashSet<LabelQuanUnit> pairset = pairMap.get(seq);
            Iterator<LabelQuanUnit> ppit = pairset.iterator();

            if (pairset.size() == 1) {
                if (ppit.hasNext()) {

                    LabelQuanUnit pair = ppit.next();
                    double[] pepR = pair.getRatio();
                    for (int l = 0; l < pepR.length; l++) {
                        if (pepR[l] < range[l][0])
                            pepRatio[l][0]++;
                        else if (pepR[l] >= range[l][0] && pepR[l] < range[l][1])
                            pepRatio[l][1]++;
                        else if (pepR[l] >= range[l][1] && pepR[l] < range[l][2])
                            pepRatio[l][2]++;
                        else if (pepR[l] >= range[l][2] && pepR[l] < range[l][3])
                            pepRatio[l][3]++;
                        else if (pepR[l] >= range[l][3])
                            pepRatio[l][4]++;
                    }

                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(pair.getModCount()).append("\t");
                    sb2.append(pair.getSequence()).append("\t");

                    for (int i = 0; i < pepR.length; i++) {
                        sb2.append(pepR[i]).append("\t");
                    }

                    double[] intens = pair.getIntensity();
                    for (int j = 0; j < intens.length; j++) {
                        sb2.append(df4.format(intens[j])).append("\t");
                    }

                    String deleRef = pair.getDelegateRef();
                    if (deleRef == null) {
                        sb2.append(pair.getRefs());
                    } else {
                        sb2.append(deleRef);
                    }

                    this.writer.addContent(sb2.toString(), 1, f);

                }
            } else {

                double[] ratio = new double[ratioNames.length];
                LabelQuanUnit pair = null;
                while (ppit.hasNext()) {

                    pair = ppit.next();
                    double[] pepR = pair.getRatio();
                    for (int i = 0; i < pepR.length; i++) {
                        ratio[i] += pepR[i];
                    }

                }
                for (int i = 0; i < ratio.length; i++) {
                    ratio[i] = Double.parseDouble(df4.format(ratio[i] / (double) pairset.size()));
                    if (ratio[i] < range[i][0])
                        pepRatio[i][0]++;
                    else if (ratio[i] >= range[i][0] && ratio[i] < range[i][1])
                        pepRatio[i][1]++;
                    else if (ratio[i] >= range[i][1] && ratio[i] < range[i][2])
                        pepRatio[i][2]++;
                    else if (ratio[i] >= range[i][2] && ratio[i] < range[i][3])
                        pepRatio[i][3]++;
                    else if (ratio[i] >= range[i][3])
                        pepRatio[i][4]++;
                }

                StringBuilder sb2 = new StringBuilder();
                sb2.append(pair.getModCount()).append("\t");
                sb2.append(pair.getSequence()).append("\t");

                for (int i = 0; i < ratio.length; i++) {
                    sb2.append(ratio[i]).append("\t");
                }

                double[] intens = pair.getIntensity();
                for (int j = 0; j < intens.length; j++) {
                    sb2.append(df4.format(intens[j])).append("\t");
                }

                String deleRef = pair.getDelegateRef();
                if (deleRef == null) {
                    sb2.append(pair.getRefs());
                } else {
                    sb2.append(deleRef);
                }

                this.writer.addContent(sb2.toString(), 1, f);
            }
        }

        writer.addBlankRow(1);
        writer.addBlankRow(1);
        writer.addBlankRow(1);
        writer.addTitle("-------------------Summary-------------------\n", 1, f);

        int snum = 0;
        int total = pairMap.size();

        for (int i = 0; i < ratioNames.length; i++) {

            StringBuilder sb1 = new StringBuilder();
            sb1.append(ratioNames[i]).append("\t").append("Protein Number\t").append("Percent\n");

            sb1.append("Ratio: >=200%\t").append(pepRatio[snum][4]).append("\t")
                    .append(dfPer.format((float) pepRatio[snum][4] / total)).append("\n");
            sb1.append("Ratio: 120%~200%\t").append(pepRatio[snum][3]).append("\t")
                    .append(dfPer.format((float) pepRatio[snum][3] / total)).append("\n");
            sb1.append("Ratio: 80%~120%\t").append(pepRatio[snum][2]).append("\t")
                    .append(dfPer.format((float) pepRatio[snum][2] / total)).append("\n");
            sb1.append("Ratio: 50%~80%\t").append(pepRatio[snum][1]).append("\t")
                    .append(dfPer.format((float) pepRatio[snum][1] / total)).append("\n");
            sb1.append("Ratio: <50%\t").append(pepRatio[snum][0]).append("\t")
                    .append(dfPer.format((float) pepRatio[snum][0] / total)).append("\n");
            sb1.append("Total Number\t").append(String.valueOf(total)).append("\t")
                    .append("100%\n");

            writer.addContent(sb1.toString(), 1, f);
            writer.addBlankRow(1);

            snum++;
        }
    }

    private void addAllSite() throws RowsExceededException, WriteException
    {

        ExcelFormat f = new ExcelFormat(false, 0);
        StringBuilder sb = new StringBuilder();
        sb.append("Mod_Site\t").append("Sequence Around\t");
        for (int i = 0; i < ratioNames.length; i++) {
            sb.append("Absolute Ratio_" + ratioNames[i] + "\t");
            sb.append("Relative Ratio_" + ratioNames[i] + "\t");
        }
        this.writer.addTitle(sb.toString(), 2, f);

        Iterator<ModifLabelPair> it = this.calculator.getTotalModSite().iterator();
        while (it.hasNext()) {
            ModifLabelPair mp = it.next();
            writer.addContent(mp.toStringWithoutPep(), 2, f);
        }
    }

    public void close() throws WriteException, IOException
    {
        this.writer.close();
    }

}
