/*
 ******************************************************************************
 * File: ProStatInfo.java * * * Created on 2011-11-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.repeatStat;

import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;
import org.apache.commons.math3.stat.inference.OneWayAnova;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author ck
 * @version 2011-11-15, 15:57:52
 */
public class ProStatInfo implements Comparable<ProStatInfo>
{

    private int ratioNum;
    private int fileNum;
    private String[] ref;
    private double[][] ratios;
    private int num;
    private double[] RSD;
    private double[] ave;
    private double[] p_value;
    private int id;
    private PepStatInfo[] pepInfo;

    private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;
    private DecimalFormat dfE3 = DecimalFormats.DF_E3;
    private DecimalFormat df4 = DecimalFormats.DF0_4;

    public ProStatInfo(int id, String[] refs, int num, PepStatInfo[] infos,
            int ratioNum, int fileNum)
    {

        this.id = id;
        this.num = num;
        this.ref = refs;
        this.pepInfo = infos;
        this.ratioNum = ratioNum;
        this.fileNum = fileNum;

        this.iniRatio();

        this.RSD = new double[ratioNum];
        this.ave = new double[ratioNum];

        for (int i = 0; i < ratioNum; i++) {
            double[] r = ratios[i];
            ArrayList<Double> datalist = new ArrayList<Double>();
            for (int j = 0; j < r.length; j++) {
                if (r[j] != 0)
                    datalist.add(r[j]);
            }
            ave[i] = MathTool.getAveInDouble(datalist);
            RSD[i] = MathTool.getRSDInDouble(datalist);
        }
    }

    private void iniRatio()
    {

        ArrayList<Double>[][] ratioList = new ArrayList[ratioNum][fileNum];
        for (int i = 0; i < ratioNum; i++) {
            for (int j = 0; j < fileNum; j++) {
                ratioList[i][j] = new ArrayList<Double>();
            }
        }
        for (int i = 0; i < pepInfo.length; i++) {
            double[][] pepRatios = pepInfo[i].getRatios();
            for (int j = 0; j < ratioNum; j++) {
                for (int k = 0; k < fileNum; k++) {
                    if (pepRatios[j][k] > 0)
                        ratioList[j][k].add(pepRatios[j][k]);
                }
            }
        }
        this.ratios = new double[ratioNum][fileNum];
        for (int i = 0; i < ratioNum; i++) {
            for (int j = 0; j < fileNum; j++) {
                ratios[i][j] = MathTool.getMedianInDouble(ratioList[i][j]);
            }
        }

        this.p_value = new double[ratioNum];
        OneWayAnova anova = new OneWayAnova();
        L:
        for (int i = 0; i < ratioNum; i++) {
            ArrayList<Double>[] idata = ratioList[i];
            ArrayList<double[]> dataList = new ArrayList<double[]>();
            for (int j = 0; j < idata.length; j++) {
                ArrayList<Double> jdata = idata[j];
                if (jdata.size() < 2) {
                    p_value[i] = 0;
                    continue L;
                }
                double[] d = new double[jdata.size()];
                for (int k = 0; k < d.length; k++) {
                    d[k] = jdata.get(k);
                }
                dataList.add(d);
            }

            try {
                p_value[i] = anova.anovaPValue(dataList);

                if (Double.isNaN(p_value[i]))
                    p_value[i] = 0.0;

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (p_value[i] < 0.001)
                p_value[i] = Double.parseDouble(dfE3.format(p_value[i]));
            else
                p_value[i] = Double.parseDouble(df4.format(p_value[i]));
        }
    }

    public int getNum()
    {
        return this.num;
    }

    public PepStatInfo[] getPepInfo()
    {
        return pepInfo;
    }

    public double[] getAve()
    {
        return ave;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ProStatInfo s1)
    {
        // TODO Auto-generated method stub
        int i = this.num;
        int i1 = s1.num;
        if (i > i1)
            return -1;
        else if (i < i1)
            return 1;
        else
            return 0;
    }

    public void setID(int id)
    {
        this.id = id;
    }

    public String toString()
    {

        StringBuilder sb = new StringBuilder();

        if (ref.length > 1) {
            for (int i = 0; i < ref.length; i++) {
                sb.append(id + "-" + (i + 1)).append("\t");
                sb.append(ref[i]).append("\t");
                sb.append(num).append("\t");

                for (int j = 0; j < ratios.length; j++) {
                    sb.append(ave[j]).append("\t");
                    sb.append(dfPer.format(RSD[j])).append("\t");
                    sb.append(p_value[j]).append("\t");
                    for (int k = 0; k < ratios[j].length; k++) {
                        sb.append(ratios[j][k]).append("\t");
                    }
                }
                sb.append("\n");
            }
        } else {
            sb.append(id).append("\t");
            sb.append(ref[0]).append("\t");
            sb.append(num).append("\t");

            for (int j = 0; j < ratioNum; j++) {
                sb.append(ave[j]).append("\t");
                sb.append(dfPer.format(RSD[j])).append("\t");
                sb.append(p_value[j]).append("\t");
                for (int k = 0; k < ratios[j].length; k++) {
                    sb.append(ratios[j][k]).append("\t");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
