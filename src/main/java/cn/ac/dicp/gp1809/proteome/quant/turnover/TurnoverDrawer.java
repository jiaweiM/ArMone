/*
 ******************************************************************************
 * File: TurnoverDrawer.java * * * Created on 2013-3-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover;

import cn.ac.dicp.gp1809.util.math.MathTool;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author ck
 * @version 2013-3-21, 16:18:02
 */
public class TurnoverDrawer
{

    private static int width = 800;
    private static int heigth = 600;
    private static Color[] colors = new Color[]{Color.red, Color.blue, Color.green, Color.cyan,
            Color.magenta, Color.orange, Color.darkGray, Color.pink, Color.yellow, Color.lightGray};
    private double[] times;

    public TurnoverDrawer(double[] times)
    {
        this.times = times;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

    public void drawPro(int id, double[] ratio, String ref, double a1, double a2, double a3, double a4, String out)
            throws IOException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries point = new XYSeries("");
        XYSeries line = new XYSeries("");
        XYSeries halfx = new XYSeries("");
        XYSeries halfy = new XYSeries("");

        for (int i = 0; i < ratio.length; i++) {
            if (ratio[i] > 0)
                point.add(times[i], ratio[i]);
        }
        collection.addSeries(point);

        halfx.add(a4, 0.0);
        halfx.add(a4, (a1) / 2.0);

        halfy.add(0, (a1) / 2.0);
        halfy.add(50, (a1) / 2.0);
        // System.out.println(id+"\t"+(a2+(a1-a2)*Math.exp(-a3*a4))+"\t"+(a1)/2.0+"\t"+a1+"\t"+a2+"\t"+a3);
        // System.out.println(id+"\t"+(-Math.log((a1-2*a2)/(2*a1-2*a2))/a3)+"\t"+a4);
        for (int i = 0; i < 100; i++) {
            line.add(0.5 * (double) i,
                    a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i));
        }
        collection.addSeries(line);
        collection.addSeries(halfx);
        collection.addSeries(halfy);
        // IJFDataset dataset = new IntensityDataset(light, heavy, times, seq,
        // line1, line2);

        JFreeChart jfreechart = ChartFactory.createXYLineChart(ref, "Time (h)",
                "Relative Abundance Ratio", (XYDataset) collection,
                PlotOrientation.VERTICAL, false, true, false);

        java.awt.Font titleFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 20);
        java.awt.Font labelFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 14);
        jfreechart.setTitle(new TextTitle(ref, titleFont));

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        jfreechart.setBackgroundPaint(Color.WHITE);
        jfreechart.setBorderPaint(Color.WHITE);
        xyplot.setBackgroundPaint(Color.WHITE);
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
        xylineandshaperenderer.setSeriesLinesVisible(0, false);
        xylineandshaperenderer.setSeriesShapesVisible(1, false);
        xylineandshaperenderer.setSeriesShapesVisible(2, false);
        xylineandshaperenderer.setSeriesShapesVisible(3, false);

        xylineandshaperenderer.setSeriesPaint(0, Color.RED);
        xylineandshaperenderer.setSeriesPaint(1, Color.RED);
        xylineandshaperenderer.setSeriesStroke(1, new BasicStroke(2.5f));
        xylineandshaperenderer.setSeriesPaint(2, Color.GRAY);
        xylineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.5f,
                BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
                new float[]{6F, 6F}, 0.0F));
        xylineandshaperenderer.setSeriesPaint(3, Color.GRAY);
        xylineandshaperenderer.setSeriesStroke(3, new BasicStroke(2.5f,
                BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
                new float[]{6F, 6F}, 0.0F));
        // xylineandshaperenderer.setBaseToolTipGenerator(new
        // StandardXYToolTipGenerator());
        // xylineandshaperenderer.setBaseToolTipGenerator(null);
        xyplot.setRenderer(xylineandshaperenderer);
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        // numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberTickUnit numunit = new NumberTickUnit(0.25);
        numberaxis.setTickUnit(numunit);
        numberaxis.setLabelFont(labelFont);
        numberaxis.setAutoRange(false);
        numberaxis.setUpperBound(1.1 * a1);
        NumberAxis domainaxis = (NumberAxis) xyplot.getDomainAxis();

        NumberTickUnit domainunit = new NumberTickUnit(3.0);
        domainaxis.setTickUnit(domainunit);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setAutoRange(false);
        domainaxis.setLowerBound(-1);
        domainaxis.setUpperBound(50);

        BufferedImage image = jfreechart.createBufferedImage(width, heigth);

        String output = out + "//" + id + ".png";
        ImageIO.write(image, "PNG", new File(output));
    }

    public void drawProBox(int id, ArrayList<Double>[] ratiolist, String ref, String out) throws IOException
    {

        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (int i = 0; i < ratiolist.length; i++) {
            dataset.add(ratiolist[i], this.times[i] + " h", "");
        }

        JFreeChart jfreechart = ChartFactory.createBoxAndWhiskerChart(ref, "Time (h)", "Relative Abundance Ratio",
                dataset, true);

        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setRangePannable(true);
        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        BufferedImage image = jfreechart.createBufferedImage(width, heigth);

        String output = out + "//" + id + ".png";
        ImageIO.write(image, "PNG", new File(output));
    }

    public void drawProLineStat(int id, ArrayList<Double>[] ratiolist,
            String ref, String out) throws IOException
    {

        ref = ref.substring(ref.lastIndexOf("|") + 1, ref.indexOf("[") - 1);
        YIntervalSeriesCollection collection = new YIntervalSeriesCollection();
        YIntervalSeries series = new YIntervalSeries("");

        for (int i = 0; i < ratiolist.length; i++) {
            Double[] listi = new Double[ratiolist[i].size()];
            listi = ratiolist[i].toArray(listi);
            Arrays.sort(listi);
            if (ratiolist[i].size() > 0) {
                series.add(this.times[i], MathTool.getMedianInDouble(ratiolist[i]),
                        listi[0], listi[listi.length - 1]);
            }
        }
        collection.addSeries(series);

        NumberAxis numberaxis = new NumberAxis("Time (h)");
        NumberAxis domainaxis = new NumberAxis("Relative Abundance Ratio");

        XYErrorRenderer xyerrorrenderer = new XYErrorRenderer();
//        xyerrorrenderer.setBaseLinesVisible(true);
//        xyerrorrenderer.setBaseShapesVisible(false);

        xyerrorrenderer.setSeriesPaint(0, Color.RED);
        xyerrorrenderer.setSeriesStroke(0, new BasicStroke(6f));

        XYPlot xyplot = new XYPlot(collection, numberaxis, domainaxis,
                xyerrorrenderer);

        JFreeChart jfreechart = new JFreeChart(ref, xyplot);

        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD, 60);
        java.awt.Font legentFont = new java.awt.Font("Times", java.awt.Font.PLAIN, 30);
        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 50);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD, 50);

//		java.awt.Font titleFont = new java.awt.Font("Times",
//				java.awt.Font.BOLD, 20);
//		java.awt.Font labelFont = new java.awt.Font("Times",
//				java.awt.Font.BOLD, 14);
//		java.awt.Font tickFont = new java.awt.Font("Times",
//				java.awt.Font.PLAIN, 12);

        jfreechart.setTitle(new TextTitle(ref, titleFont));
        jfreechart.setBackgroundPaint(Color.WHITE);
        jfreechart.setBorderPaint(Color.WHITE);
        jfreechart.removeLegend();

        xyplot.setBackgroundPaint(Color.WHITE);
        xyplot.setRangeGridlinesVisible(false);
        xyplot.setDomainGridlinesVisible(false);

        NumberTickUnit numunit = new NumberTickUnit(5.0);
        numberaxis.setTickUnit(numunit);
        numberaxis.setLabelFont(labelFont);
        numberaxis.setAutoRange(false);
        numberaxis.setLowerBound(-1);
        numberaxis.setUpperBound(50);
        numberaxis.setTickLabelFont(tickFont);

        NumberTickUnit domainunit = new NumberTickUnit(0.5);
        domainaxis.setTickUnit(domainunit);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setAutoRange(false);
        domainaxis.setUpperBound(1.1);
        domainaxis.setTickLabelFont(tickFont);

        BufferedImage image = jfreechart.createBufferedImage(1600, 1200);

        String output = out + "//" + id + ".png";
        ImageIO.write(image, "PNG", new File(output));
    }

    public void drawPep(int id, double[] dd, String seq,
            double a1, double a2, double a3, double a4, String out) throws IOException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries pointdd = new XYSeries("");
        XYSeries pointss = new XYSeries("");
        XYSeries linedd = new XYSeries("");
        XYSeries liness = new XYSeries("");
        XYSeries linehalf = new XYSeries("");

        double[] ss = new double[dd.length];
        for (int i = 0; i < ss.length; i++) {
            if ((dd[i] == 0)) {
                ss[i] = 0;
            } else {
                ss[i] = 1.0 - dd[i];
            }
        }
        for (int i = 0; i < ss.length; i++) {
            if (ss[i] > 0) pointss.add(times[i], ss[i]);
        }
        for (int i = 0; i < dd.length; i++) {
            if (dd[i] > 0) pointdd.add(times[i], dd[i]);
//			if(seq.equals("LAAIGEATRLK")) System.out.println(times[i]+"\t"+heavy[i]);
        }
        collection.addSeries(pointdd);
        collection.addSeries(pointss);

        double maxdd = pointdd.getMaxY();
        double maxss = pointss.getMaxY();
        double max = maxdd > maxss ? maxdd : maxss;

        for (int i = 0; i < 100; i++) {
            linedd.add(0.5 * (double) i, a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i));
            liness.add(0.5 * (double) i, 1.0 - (a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i)));
        }
        collection.addSeries(linedd);
        collection.addSeries(liness);

        JFreeChart jfreechart = ChartFactory.createXYLineChart(seq, "Time (h)", "Intensity",
                (XYDataset) collection, PlotOrientation.VERTICAL, false, true, false);

        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD, 20);
        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 14);
        jfreechart.setTitle(new TextTitle(seq, titleFont));

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(Color.WHITE);
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
        xylineandshaperenderer.setSeriesLinesVisible(0, false);
        xylineandshaperenderer.setSeriesLinesVisible(1, false);
        xylineandshaperenderer.setSeriesShapesVisible(2, false);
        xylineandshaperenderer.setSeriesShapesVisible(3, false);
        xylineandshaperenderer.setSeriesPaint(0, Color.RED);
        xylineandshaperenderer.setSeriesPaint(1, Color.BLUE);
        xylineandshaperenderer.setSeriesPaint(2, Color.RED);
        xylineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.5f));
        xylineandshaperenderer.setSeriesPaint(3, Color.BLUE);
        xylineandshaperenderer.setSeriesStroke(3, new BasicStroke(2.5f));
/*	    if(collection.getSeriesCount()==5){
	    	xylineandshaperenderer.setSeriesShapesVisible(4, false);
	    	xylineandshaperenderer.setSeriesPaint(4, Color.GRAY);
	    	xylineandshaperenderer.setSeriesStroke(4, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
					BasicStroke.JOIN_MITER, 1.0f, new float[] {6F, 6F}, 0.0F));
	    }

*/
        xylineandshaperenderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        xyplot.setRenderer(xylineandshaperenderer);

        NumberTickUnit numberunit = new NumberTickUnit(0.25);
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setTickUnit(numberunit);
        numberaxis.setAutoRange(false);
        numberaxis.setUpperBound(1.2);
        numberaxis.setLabelFont(labelFont);

        NumberTickUnit domainunit = new NumberTickUnit(3.0);
        NumberAxis domainaxis = (NumberAxis) xyplot.getDomainAxis();
        domainaxis.setTickUnit(domainunit);
        domainaxis.setAutoRange(false);
        domainaxis.setLowerBound(-1);
        domainaxis.setUpperBound(50);
        domainaxis.setLabelFont(labelFont);

        BufferedImage image = jfreechart.createBufferedImage(width, heigth);

        String output = out + "//" + id + ".png";
        ImageIO.write(image, "PNG", new File(output));
    }

}
