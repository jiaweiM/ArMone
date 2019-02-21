/*
 ******************************************************************************
 * File:LabelFeaturesDataset.java * * * Created on 2010-7-8
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import cn.ac.dicp.gp1809.proteome.drawjf.AbstractSpectrumDataset;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * @author ck
 * @version 2010-7-8, 15:53:56
 */
public class PepFeaturesDataset extends AbstractSpectrumDataset
{

    public static int beforeFilter = 0;
    public static int afterFilter = 1;
    private static DecimalFormat DF0 = DecimalFormats.DF0_0;
    private Color[] COLORS = new Color[]{Color.red, Color.blue, Color.green, Color.yellow,
            Color.cyan, Color.magenta};
    //	private static DecimalFormat DF2 = DecimalFormats.DF0_2;
    private FeaturesObject obj;

    public PepFeaturesDataset(FeaturesObject obj)
    {
        this.obj = obj;
    }

    public void selectType(int datatype)
    {

        if (datatype == beforeFilter) {

            this.setTitle(new TextTitle("Peptide Features"));
            this.setColors(COLORS);

            XYSeriesCollection collection = new XYSeriesCollection();
//			Annotations ans = new Annotations();

            PeptidePair pair = obj.getPeitdePair();
            LabelFeatures feas = pair.getFeatures();

            String[] pairNames = feas.getFeatureNames();
            XYSeries[] series = new XYSeries[pairNames.length];
            for (int i = 0; i < series.length; i++) {
                series[i] = new XYSeries(pairNames[i]);
            }

            int[] scanlist = feas.getScanList();
            double[][] intenlist = feas.getIntenList();

            for (int i = 0; i < scanlist.length; i++) {
                for (int j = 0; j < intenlist[i].length; j++) {
                    series[j].add(Double.parseDouble(DF0.format(scanlist[i])), intenlist[i][j]);
                }
            }

            for (int i = 0; i < series.length; i++) {
                collection.addSeries(series[i]);
            }

            this.setDataset(new XYBarDataset(collection, BarWidth));
            this.setXlabel("ScanNum");
            this.setYlabel("Intensity");

        } else if (datatype == afterFilter) {

            this.setTitle(new TextTitle("Peptide Features"));
            this.setColors(COLORS);

            XYSeriesCollection collection = new XYSeriesCollection();
//			Annotations ans = new Annotations();

            PeptidePair pair = obj.getPeitdePair();
            LabelFeatures feas = pair.getFeatures();

            String[] pairNames = feas.getFeatureNames();
            XYSeries[] series = new XYSeries[pairNames.length];
            for (int i = 0; i < series.length; i++) {
                series[i] = new XYSeries(pairNames[i]);
            }

            int[] scanlist = feas.getScanList();
            double[][] intenlist = feas.getIntenList();

            for (int i = 0; i < scanlist.length; i++) {
                for (int j = 0; j < intenlist[i].length; j++) {
                    series[j].add(Double.parseDouble(DF0.format(scanlist[i])), intenlist[i][j]);
                }
            }

            for (int i = 0; i < series.length; i++) {
                collection.addSeries(series[i]);
            }

            this.setDataset(new XYBarDataset(collection, BarWidth));
            this.setXlabel("ScanNum");
            this.setYlabel("Intensity");

        }
    }

    public FeaturesObject getFeatures()
    {
        return obj;
    }

}
