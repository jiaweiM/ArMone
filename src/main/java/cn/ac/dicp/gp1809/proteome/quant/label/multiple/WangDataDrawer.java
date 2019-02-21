/*
 ******************************************************************************
 * File: WangDataDrawer.java * * * Created on 2012-9-6
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import jxl.JXLException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ck
 * @version 2012-9-6, 15:32:31
 */
public class WangDataDrawer
{

    private static int width = 800;
    private static int heigth = 600;
    private static Color[] colors = new Color[]{Color.red, Color.blue, Color.green, Color.cyan,
            Color.magenta, Color.orange, Color.darkGray, Color.pink, Color.yellow, Color.lightGray};
    private static double[] times = new double[]{0, 3, 6, 12, 24, 48};
    private String in;
    private String out;

    public WangDataDrawer(String in, String out)
    {

        this.in = in;
        this.out = out;
    }

    public static void drawCompare(String in, String out) throws IOException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries[] series = new XYSeries[6];
        for (int i = 0; i < series.length; i++) {
            series[i] = new XYSeries("");
        }

        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] ss = line.split("\t");
            double x = Double.parseDouble(ss[0]);
            for (int i = 0; i < series.length; i++) {
                series[i].add(x, Double.parseDouble(ss[i + 1]));
            }
        }
        reader.close();
        for (int i = 0; i < series.length; i++) {
            collection.addSeries(series[i]);
        }

        XYSeries vline1 = new XYSeries("Heavy/Median");
        XYSeries vline2 = new XYSeries("Heavy/Light");
        XYSeries vline3 = new XYSeries("Median/Light");

        vline1.add(-2, 0);
        vline1.add(-2, 250);
        vline2.add(0, 0);
        vline2.add(0, 250);
        vline3.add(2, 0);
        vline3.add(2, 250);

        collection.addSeries(vline1);
        collection.addSeries(vline2);
        collection.addSeries(vline3);

        JFreeChart jfreechart = ChartFactory.createXYLineChart("",
                "Log2(Ratio)", "",
                (XYDataset) collection, PlotOrientation.VERTICAL, false, true, false);

        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 24);
        java.awt.Font labelFont2 = new java.awt.Font("Times", java.awt.Font.BOLD, 30);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        jfreechart.setBackgroundPaint(Color.WHITE);
        jfreechart.setBorderPaint(Color.WHITE);

        xyplot.setBackgroundPaint(Color.WHITE);

        XYTextAnnotation annotation1 = new XYTextAnnotation("Heavy/Median", -2d, -8d);
        annotation1.setFont(labelFont);
        xyplot.addAnnotation(annotation1);

        XYTextAnnotation annotation2 = new XYTextAnnotation("Heavy/Light", 0d, -8d);
        annotation2.setFont(labelFont);
        xyplot.addAnnotation(annotation2);

        XYTextAnnotation annotation3 = new XYTextAnnotation("Median/Light", 2d, -8d);
        annotation3.setFont(labelFont);
        xyplot.addAnnotation(annotation3);

        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        xylineandshaperenderer.setSeriesPaint(0, Color.RED);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(2.5f));
        xylineandshaperenderer.setSeriesPaint(1, Color.RED);
        xylineandshaperenderer.setSeriesStroke(1, new BasicStroke(2.5f));
        xylineandshaperenderer.setSeriesPaint(2, Color.RED);
        xylineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.5f));

        xylineandshaperenderer.setSeriesPaint(3, Color.BLUE);
        xylineandshaperenderer.setSeriesStroke(3, new BasicStroke(2.5f));
        xylineandshaperenderer.setSeriesPaint(4, Color.BLUE);
        xylineandshaperenderer.setSeriesStroke(4, new BasicStroke(2.5f));
        xylineandshaperenderer.setSeriesPaint(5, Color.BLUE);
        xylineandshaperenderer.setSeriesStroke(5, new BasicStroke(2.5f));

        xylineandshaperenderer.setSeriesPaint(6, Color.GRAY);
        xylineandshaperenderer.setSeriesStroke(6, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 1.0f, new float[]{6F, 6F}, 0.0F));
        xylineandshaperenderer.setSeriesPaint(7, Color.GRAY);
        xylineandshaperenderer.setSeriesStroke(7, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 1.0f, new float[]{6F, 6F}, 0.0F));
        xylineandshaperenderer.setSeriesPaint(8, Color.GRAY);
        xylineandshaperenderer.setSeriesStroke(8, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 1.0f, new float[]{6F, 6F}, 0.0F));

        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
//	    numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberTickUnit unit = new NumberTickUnit(100);
        numberaxis.setTickUnit(unit);
//	    NumberTickUnit unit = new NumberTickUnit(2.0E6);
        numberaxis.setTickUnit(unit);
        numberaxis.setAutoRange(false);
        numberaxis.setUpperBound(400);
        numberaxis.setLowerBound(-20);
        numberaxis.setTickLabelFont(labelFont);
        numberaxis.setLabelFont(labelFont2);

        NumberAxis domainaxis = (NumberAxis) xyplot.getDomainAxis();
        NumberTickUnit unit2 = new NumberTickUnit(0.5);
        domainaxis.setTickUnit(unit2);
        domainaxis.setAutoRange(false);
        domainaxis.setLowerBound(-3);
        domainaxis.setUpperBound(3);
        domainaxis.setTickLabelFont(labelFont);
        domainaxis.setLabelFont(labelFont2);

        BufferedImage image = jfreechart.createBufferedImage(1600, 1200);

        ImageIO.write(image, "PNG", new File(out));
    }

    /**
     * @param args
     * @throws JXLException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, JXLException
    {
        // TODO Auto-generated method stub

//		String in = "H:\\WFJ_mutiple_label\\turnover\\1024\\final.peps.new.function.xls.txt";
//		String in = "H:\\WFJ_mutiple_label\\turnover\\1024\\final.pros.new.function.xls.txt";
        String in = "J:\\Data\\sixple\\turnover\\dat\\final5\\turnover5.xls";
//		String in = "H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\re2.final.pros.ratio.manual.xls";
//		String out = "H:\\WFJ_mutiple_label\\turnover\\new\\histone.png";
//		String out = "H:\\WFJ_mutiple_label\\turnover\\1024\\peps";
        String out = "J:\\Data\\sixple\\turnover\\dat\\final5\\select2";
        WangDataDrawer drawer = new WangDataDrawer(in, out);
//		drawer.drawPro();
//		drawer.drawPepSD();
//		drawer.drawProTxt();
//		drawer.drawPep();
//		drawer.drawMutilHistone();
//		drawer.drawMutilPro();
//		drawer.drawMutilPep();
//		drawer.drawIntenBar();
//		drawer.drawPepSD();
        drawer.drawSelectPro("J:\\Data\\sixple\\turnover\\dat\\final5\\�½� �ı��ĵ�.txt");
//		WangDataDrawer.drawCompare("F:\\P\\Mutil_label\\1_4_1_nor.txt", "F:\\P\\Mutil_label\\1_4_1_nor_3.png");
    }

    public void drawPep() throws IOException, JXLException
    {

        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
//		String [] line = null;
        int id = 0;
        while ((line = reader.readLine()) != null) {

            double[] light = new double[6];
            double[] heavy = new double[6];
            for (int i = 0; i < 6; i++) {
                light[i] = line[i + 3].length() > 0 ? Double.parseDouble(line[i + 3]) : 0;
                heavy[i] = line[i + 12].length() > 0 ? Double.parseDouble(line[i + 12]) : 0;
//				System.out.println(light[i]+"\t"+heavy[i]);
            }

            double a1 = Double.parseDouble(line[9]);
            double a2 = Double.parseDouble(line[10]);
            double b1 = Double.parseDouble(line[18]);
            double b2 = Double.parseDouble(line[19]);

            this.drawPep(++id, light, heavy, line[0], a1, a2, b1, b2);
            if (line[0].equals("LAAIGEATRLK")) {
                for (int i = 0; i < 6; i++) {
                    System.out.println(light[i] + "\t" + heavy[i]);
                }
                break;
            }
//			if(id==50)break;
        }
        reader.close();
    }

    public void drawPep(int id, double[] light, double[] heavy, String seq, double a1, double a2, double b1, double b2)
            throws IOException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries pointlight = new XYSeries("");
        XYSeries pointheavy = new XYSeries("");
        XYSeries linelight = new XYSeries("");
        XYSeries lineheavy = new XYSeries("");

        for (int i = 1; i < light.length; i++) {
            if (light[i] > 0) pointlight.add(times[i], light[i]);
        }
        for (int i = 0; i < heavy.length; i++) {
            if (heavy[i] > 0) pointheavy.add(times[i], heavy[i]);
            if (seq.equals("LAAIGEATRLK")) System.out.println(times[i] + "\t" + heavy[i]);
        }
        collection.addSeries(pointlight);
        collection.addSeries(pointheavy);

        double maxlight = pointlight.getMaxY();
        double maxheavy = pointheavy.getMaxY();
        double max = maxlight > maxheavy ? maxlight : maxheavy;

        for (int i = 0; i < 100; i++) {
            linelight.add(0.5 * (double) i, a1 * Math.exp(a2 * 0.5 * (double) i));
            lineheavy.add(0.5 * (double) i, b1 * Math.exp(b2 * 0.5 * (double) i));
        }
        collection.addSeries(linelight);
        collection.addSeries(lineheavy);

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
        xylineandshaperenderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        xyplot.setRenderer(xylineandshaperenderer);
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        numberaxis.setAutoRange(false);
        numberaxis.setUpperBound(max * 1.1);
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

    public void drawPepSD() throws IOException, JXLException
    {

        BufferedReader reader = new BufferedReader(new FileReader(in));
        String sss = reader.readLine();
//		String [] line = null;
        int id = 0;
        while ((sss = reader.readLine()) != null) {

            String[] line = sss.split("\t");
            double[] dd = new double[6];
            double[] ss = new double[6];
            for (int i = 0; i < 6; i++) {
                dd[i] = line[i + 2].length() > 0 ? Double.parseDouble(line[i + 2]) : 0;
                ss[i] = dd[i] == 0 ? 0 : 1.0 - dd[i];
//				System.out.println(light[i]+"\t"+heavy[i]);
            }

            double a1 = Double.parseDouble(line[8]);
            double a2 = line[9].length() > 0 ? Double.parseDouble(line[9]) : 0.0;
            double a3 = Double.parseDouble(line[10]);
            double a4 = Double.parseDouble(line[12]);

            this.drawPepSD(++id, dd, ss, line[0], a1, a2, a3, a4);

//			if(id==20)break;
        }
        reader.close();
    }

    public void drawPepSD(int id, double[] dd, double[] ss, String seq,
            double a1, double a2, double a3, double a4) throws IOException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries pointdd = new XYSeries("");
        XYSeries pointss = new XYSeries("");
        XYSeries linedd = new XYSeries("");
        XYSeries liness = new XYSeries("");
        XYSeries linehalf = new XYSeries("");

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

/*		double half = Math.log(0.5/a1)/a2;
		if(half>0 && half<50){
			linehalf.add(half, 0);
			linehalf.add(half, 0.5);
			collection.addSeries(linehalf);
		}
*/
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

    public void drawPro() throws IOException, JXLException
    {

        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
//		String [] line = null;
        int id = 0;
        while ((line = reader.readLine()) != null) {

            id++;
//			String ref = line[0].substring(0, line[0].indexOf("|"));
            String ref = line[0].substring(0, line[0].indexOf("ref") - 1);

            double[] ratio = new double[6];
            for (int i = 0; i < 6; i++) {
                if (line[i + 1].length() > 0) ratio[i] = Double.parseDouble(line[i + 1]);
            }

            double a1 = Double.parseDouble(line[7]);
            double a2 = line[8].length() > 0 ? Double.parseDouble(line[8]) : 0.0;
            double a3 = Double.parseDouble(line[9]);
            double a4 = Double.parseDouble(line[11]);

            this.drawPro(id, ratio, ref, a1, a2, a3, a4);
//			System.out.println(id+"\t"+a1+"\t"+a2);

            if (id == 70) {
                break;
            }
        }
        reader.close();
    }

    public void drawProTxt() throws IOException, JXLException
    {

        BufferedReader reader = new BufferedReader(new FileReader(in));
        String ss = reader.readLine();
        int id = 0;
        while ((ss = reader.readLine()) != null) {

            String[] line = ss.split("\t");
            id++;
//			String ref = line[0].substring(0, line[0].indexOf("ref"));
            String ref = line[0].substring(0, line[0].indexOf("ref") - 1);

            double[] ratio = new double[6];
            for (int i = 0; i < 6; i++) {
                if (line[i + 1].length() > 0) ratio[i] = Double.parseDouble(line[i + 1]);
            }

            double a1 = Double.parseDouble(line[8]);
            double a2 = line[9].length() > 0 ? Double.parseDouble(line[9]) : 0.0;
            double a3 = Double.parseDouble(line[10]);
            double a4 = Double.parseDouble(line[12]);

            this.drawPro(id, ratio, ref, a1, a2, a3, a4);
//			System.out.println(id+"\t"+a1+"\t"+a2);

//			if(id==70){
//				break;
//			}
        }
        reader.close();
    }

    public void drawPro(int id, double[] ratio, String seq, double a1, double a2, double a3, double a4)
            throws IOException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries point = new XYSeries("");
        XYSeries line = new XYSeries("");
        XYSeries halfx = new XYSeries("");
        XYSeries halfy = new XYSeries("");

        for (int i = 0; i < ratio.length; i++) {
            if (ratio[i] > 0) point.add(times[i], ratio[i]);
        }
        collection.addSeries(point);

        halfx.add(a4, 0.0);
        halfx.add(a4, (a1) / 2.0);

        halfy.add(0, (a1) / 2.0);
        halfy.add(50, (a1) / 2.0);
//		System.out.println(id+"\t"+(a2+(a1-a2)*Math.exp(-a3*a4))+"\t"+(a1)/2.0+"\t"+a1+"\t"+a2+"\t"+a3);
//		System.out.println(id+"\t"+(-Math.log((a1-2*a2)/(2*a1-2*a2))/a3)+"\t"+a4);
        for (int i = 0; i < 100; i++) {
            line.add(0.5 * (double) i, a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i));
        }
        collection.addSeries(line);
        collection.addSeries(halfx);
        collection.addSeries(halfy);
//		IJFDataset dataset = new IntensityDataset(light, heavy, times, seq, line1, line2);

        JFreeChart jfreechart = ChartFactory.createXYLineChart(seq, "Time (h)", "Relative Abundance Ratio",
                (XYDataset) collection, PlotOrientation.VERTICAL, false, true, false);

        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD, 20);
        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 14);
        jfreechart.setTitle(new TextTitle(seq, titleFont));

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
        xylineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 1.0f, new float[]{6F, 6F}, 0.0F));
        xylineandshaperenderer.setSeriesPaint(3, Color.GRAY);
        xylineandshaperenderer.setSeriesStroke(3, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 1.0f, new float[]{6F, 6F}, 0.0F));
//	    xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
//	    xylineandshaperenderer.setBaseToolTipGenerator(null);
        xyplot.setRenderer(xylineandshaperenderer);
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
//	    numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
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

//	    String [] ss = seq.split("[:.]");
        String output = out + "//" + id + ".png";
        ImageIO.write(image, "PNG", new File(output));
    }

    public void drawMutilPro() throws IOException, JXLException
    {

        XYSeriesCollection collection = new XYSeriesCollection();

        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        double max = 0;


        while ((line = reader.readLine()) != null) {

            double a = Double.parseDouble(line[7]);
            double b = Double.parseDouble(line[8]);
            if (a > 2) continue;
            XYSeries proline = new XYSeries("");
            for (int i = 0; i < 100; i++) {
                proline.add(0.5 * (double) i, a * Math.exp(b * 0.5 * (double) i));
            }
            collection.addSeries(proline);
            if (a > max) {
                max = a;
            }
        }
        reader.close();

        JFreeChart jfreechart = ChartFactory.createXYLineChart("", "Time (h)", "Relative Abundance Ratio",
                (XYDataset) collection, PlotOrientation.VERTICAL, false, true, false);

        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD, 30);
//		java.awt.Font legentFont = new java.awt.Font("Times", java.awt.Font.PLAIN , 14);
        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 24);
//		LegendTitle lt = jfreechart.getLegend();
//		lt.setItemFont(legentFont);
//		jfreechart.setTitle(new TextTitle("Histone", titleFont));

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.getRenderer().setSeriesPaint(0, Color.RED);
        jfreechart.setBackgroundPaint(Color.WHITE);
        jfreechart.setBorderPaint(Color.WHITE);
        xyplot.setBackgroundPaint(Color.WHITE);
        int seriescount = xyplot.getSeriesCount();
        for (int i = 0; i < seriescount; i++) {
            xyplot.getRenderer().setSeriesStroke(i, new BasicStroke(1.5f));
        }

        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
//	    numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        numberaxis.setAutoRange(true);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(0.5);
        numberaxis.setTickUnit(unit);
        numberaxis.setUpperBound(max * 1.1);
        numberaxis.setLabelFont(titleFont);
        numberaxis.setTickLabelFont(labelFont);

        NumberAxis domainaxis = (NumberAxis) xyplot.getDomainAxis();
        domainaxis.setAutoRange(false);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setLowerBound(-1);
        domainaxis.setUpperBound(50);
        domainaxis.setLabelFont(titleFont);
        domainaxis.setTickLabelFont(labelFont);

        BufferedImage image = jfreechart.createBufferedImage(1600, 1200);

        ImageIO.write(image, "PNG", new File(out));
    }

    public void drawMutilHistone() throws IOException, JXLException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
//		BufferedReader reader = new BufferedReader(new FileReader(in));
        ExcelReader reader = new ExcelReader(in);
//		String ss = reader.readLine();
        double max = 0;

//		Pattern hisp = Pattern.compile(".*Gene_Symbol=([\\w]+).*Histone.*");
        Pattern hisp = Pattern.compile("(.*)\\|ref.*histone.*");
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {

//			String [] line = ss.split("\t");
            Matcher hism = hisp.matcher(line[0]);
            String gene = "";
            if (hism.matches()) {
                gene = hism.group(1);
            } else {
                continue;
            }

            for (int i = 0; i < line.length; i++)
                System.out.print(line[i] + "\t");
            System.out.println();

            double a1 = Double.parseDouble(line[14]);
            double a2 = line[15].length() > 0 ? Double.parseDouble(line[15]) : 0.0;
            double a3 = Double.parseDouble(line[16]);
            double a4 = Double.parseDouble(line[18]);

            XYSeries proline = new XYSeries(gene);
//			XYSeries proline = new XYSeries("");
            for (int i = 0; i < 100; i++) {
                proline.add(0.5 * (double) i, a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i));
            }
            collection.addSeries(proline);

            if (a1 > max) {
                max = a1;
            }

            XYSeries halfx = new XYSeries("");
            XYSeries halfy = new XYSeries("");
            halfx.add(a4, 0.0);
            halfx.add(a4, (a1) / 2.0);

            halfy.add(0, (a1) / 2.0);
            halfy.add(50, (a1) / 2.0);

//			collection.addSeries(halfx);
//			collection.addSeries(halfy);
        }
        reader.close();

        JFreeChart jfreechart = ChartFactory.createXYLineChart("Histone", "Time (h)", "Relative Isotope Abundance",
                (XYDataset) collection, PlotOrientation.VERTICAL, true, true, false);

        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD, 36);
        java.awt.Font legentFont = new java.awt.Font("Times", java.awt.Font.PLAIN, 30);
        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 30);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD, 24);
        LegendTitle lt = jfreechart.getLegend();
        lt.setItemFont(legentFont);
        jfreechart.setTitle(new TextTitle("Histone", titleFont));

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        jfreechart.setBackgroundPaint(Color.WHITE);
        jfreechart.setBorderPaint(Color.WHITE);
        xyplot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
        int seriescount = xyplot.getSeriesCount();
        for (int i = 0; i < seriescount; i++) {
            xylineandshaperenderer.setSeriesShapesVisible(i, false);
//			if(i%2!=0){
//				xylineandshaperenderer.setSeriesPaint(i, Color.GRAY);
//			    xylineandshaperenderer.setSeriesStroke(i, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
//						BasicStroke.JOIN_MITER, 1.0f, new float[] {6F, 6F}, 0.0F));
//			}else{
            xylineandshaperenderer.setSeriesStroke(i, new BasicStroke(2.5f));
//			}
        }
        xyplot.setRenderer(xylineandshaperenderer);

        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRange(true);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(0.5);
        numberaxis.setTickUnit(unit);
        numberaxis.setUpperBound(max * 1.1);
        numberaxis.setTickLabelFont(tickFont);

        NumberAxis domainaxis = (NumberAxis) xyplot.getDomainAxis();
        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(3);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setLowerBound(-1);
        domainaxis.setUpperBound(50);
        domainaxis.setTickLabelFont(tickFont);

        BufferedImage image = jfreechart.createBufferedImage(1600, 1200);

        ImageIO.write(image, "PNG", new File(out));
    }

    public void drawMutilPep() throws IOException, JXLException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String sss = null;
//		String [] line = null;
        double max = 0;

        while ((sss = reader.readLine()) != null) {

            String[] line = sss.split("\t");
            if (!line[1].contains("nuclear pore complex")) continue;

            double[] dd = new double[6];
            double[] ss = new double[6];
            for (int i = 0; i < 6; i++) {
                dd[i] = line[i + 2].length() > 0 ? Double.parseDouble(line[i + 2]) : 0;
                ss[i] = dd[i] == 0 ? 0 : 1.0 - dd[i];
//				System.out.println(light[i]+"\t"+heavy[i]);
            }

            double a1 = Double.parseDouble(line[8]);
            double a2 = line[9].length() > 0 ? Double.parseDouble(line[9]) : 0.0;
            double a3 = Double.parseDouble(line[10]);
            double a4 = Double.parseDouble(line[12]);

            XYSeries linelight = new XYSeries(line[0]);
            XYSeries lineheavy = new XYSeries("");
            XYSeries half = new XYSeries("");

            double halfx = 0;
            double diffy = Double.MAX_VALUE;
            double halfy = 0;
            for (int i = 0; i < 100; i++) {
                linelight.add(0.5 * (double) i, a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i));
                lineheavy.add(0.5 * (double) i, 1.0 - (a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i)));
            }
            half.add(halfx, 0);
            half.add(halfx, halfy);
//			System.out.println(halfx+"\t"+halfy);
            collection.addSeries(linelight);
            collection.addSeries(lineheavy);
        }
        reader.close();

        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD, 36);
        java.awt.Font legentFont = new java.awt.Font("Times", java.awt.Font.PLAIN, 30);
        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 30);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD, 24);

        JFreeChart jfreechart = ChartFactory.createXYLineChart("nuclear pore complex protein Nup155 isoform 2",
                "Time (h)", "Relative Isotope Abundance",
                (XYDataset) collection, PlotOrientation.VERTICAL, true, true, false);

        LegendTitle lt = jfreechart.getLegend();
        lt.setItemFont(legentFont);
        jfreechart.setTitle(new TextTitle("nuclear pore complex protein Nup155 isoform 2", titleFont));

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        jfreechart.setBackgroundPaint(Color.WHITE);
        jfreechart.setBorderPaint(Color.WHITE);

        xyplot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        xylineandshaperenderer.setSeriesPaint(0, Color.RED);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(2.5f));
        xylineandshaperenderer.setSeriesPaint(1, Color.RED);
        xylineandshaperenderer.setSeriesStroke(1, new BasicStroke(2.5f));
//		xylineandshaperenderer.setSeriesPaint(2, Color.RED);
//		xylineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
//				BasicStroke.JOIN_MITER, 1.0f, new float[] {6F, 6F}, 0.0F));
        xylineandshaperenderer.setSeriesPaint(2, Color.green);
        xylineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.5f));
        xylineandshaperenderer.setSeriesPaint(3, Color.green);
        xylineandshaperenderer.setSeriesStroke(3, new BasicStroke(2.5f));
//		xylineandshaperenderer.setSeriesPaint(5, Color.green);
//		xylineandshaperenderer.setSeriesStroke(5, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
//				BasicStroke.JOIN_MITER, 1.0f, new float[] {6F, 6F}, 0.0F));
        xylineandshaperenderer.setSeriesPaint(4, Color.BLUE);
        xylineandshaperenderer.setSeriesStroke(4, new BasicStroke(2.5f));
        xylineandshaperenderer.setSeriesPaint(5, Color.BLUE);
        xylineandshaperenderer.setSeriesStroke(5, new BasicStroke(2.5f));
//		xylineandshaperenderer.setSeriesPaint(8, Color.BLUE);
//		xylineandshaperenderer.setSeriesStroke(8, new BasicStroke(2.5f, BasicStroke.CAP_SQUARE,
//				BasicStroke.JOIN_MITER, 1.0f, new float[] {6F, 6F}, 0.0F));

//		LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer)xyplot.getRenderer();
//		lineandshaperenderer.s
//	    xylineandshaperenderer.setSeriesLinesVisible(0, false);
//	    xylineandshaperenderer.setSeriesPaint(0, Color.RED);
//	    xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
//	    xylineandshaperenderer.setBaseToolTipGenerator(null);
//	    xyplot.setRenderer(xylineandshaperenderer);
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRange(true);
        numberaxis.setUpperBound(1.2);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(0.5);
        numberaxis.setTickUnit(unit);
        numberaxis.setTickLabelFont(tickFont);

        NumberAxis domainaxis = (NumberAxis) xyplot.getDomainAxis();
        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(3);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setLowerBound(-1);
        domainaxis.setUpperBound(50);
        domainaxis.setTickLabelFont(tickFont);

        BufferedImage image = jfreechart.createBufferedImage(1600, 1200);

        ImageIO.write(image, "PNG", new File(out));
    }

    public void drawSelectPro(String select) throws IOException, JXLException
    {

        HashMap<String, String[]> promap = new HashMap<String, String[]>();
        HashMap<String, ArrayList<String[]>> map = new HashMap<String, ArrayList<String[]>>();
        BufferedReader sereader = new BufferedReader(new FileReader(select));
        String seline = null;
        while ((seline = sereader.readLine()) != null) {
            String[] cs = seline.split("\t");
            ArrayList<String[]> list = new ArrayList<String[]>();
            map.put(cs[0], list);
            promap.put(cs[0], cs);
        }
        sereader.close();

        ExcelReader reader = new ExcelReader(in, 1);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] refs = line[1].split(";");
            for (int i = 0; i < refs.length; i++) {
                if (map.containsKey(refs[i])) {
                    map.get(refs[i]).add(line);
                }
            }
        }
        reader.close();

        Iterator<String> it = map.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            String key = it.next();
            ArrayList<String[]> list = map.get(key);
            String[][] peps = new String[list.size()][];
            peps = list.toArray(peps);
            String[] pro = promap.get(key);
            String[] cs = key.split("[|\\[]");
            String name = "";
            if (cs.length > 2) name = cs[cs.length - 2];
            System.out.println(cs.length + "\t" + name);
            this.drawMutilPepAndPro(++i, name, peps, pro);
        }
    }

    public void drawMutilPep(int id, String ref, String[][] peps) throws IOException, JXLException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        double max = 0;

        for (int pn = 0; pn < peps.length; pn++) {

            String[] pepline = peps[pn];

            double[] ratio = new double[6];
            for (int i = 0; i < 6; i++) {
                ratio[i] = pepline[i + 3].length() > 0 ? Double.parseDouble(pepline[i + 3]) : 0;
            }

            double a1 = Double.parseDouble(pepline[9]);
            double a2 = pepline[10].length() > 0 ? Double.parseDouble(pepline[10]) : 0.0;
            double a3 = Double.parseDouble(pepline[11]);
            double a4 = Double.parseDouble(pepline[13]);

            XYSeries point = new XYSeries("");
            XYSeries line = new XYSeries("");
            XYSeries halfx = new XYSeries("");
//			XYSeries halfy = new XYSeries("");

            for (int i = 0; i < ratio.length; i++) {
                if (ratio[i] > 0) point.add(times[i], ratio[i]);
            }

            halfx.add(a4, 0.0);
            halfx.add(a4, (a1) / 2.0);

//			halfy.add(0, (a1)/2.0);
//			halfy.add(50, (a1)/2.0);
//			System.out.println(id+"\t"+(a2+(a1-a2)*Math.exp(-a3*a4))+"\t"+(a1)/2.0+"\t"+a1+"\t"+a2+"\t"+a3);
//			System.out.println(id+"\t"+(-Math.log((a1-2*a2)/(2*a1-2*a2))/a3)+"\t"+a4);
            for (int i = 0; i < 100; i++) {
                line.add(0.5 * (double) i, a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i));
            }

            collection.addSeries(point);
            collection.addSeries(line);
            collection.addSeries(halfx);
//			collection.addSeries(halfy);
        }

        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD, 36);
        java.awt.Font legentFont = new java.awt.Font("Times", java.awt.Font.PLAIN, 30);
        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 30);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD, 24);

        JFreeChart jfreechart = ChartFactory.createXYLineChart(ref,
                "Time (h)", "Relative Isotope Abundance",
                (XYDataset) collection, PlotOrientation.VERTICAL, true, true, false);

        LegendTitle lt = jfreechart.getLegend();
        lt.setItemFont(legentFont);
        jfreechart.setTitle(new TextTitle(ref, titleFont));
        lt.getID();

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        jfreechart.setBackgroundPaint(Color.WHITE);
        jfreechart.setBorderPaint(Color.WHITE);

        xyplot.setBackgroundPaint(Color.WHITE);
//		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();

        for (int pn = 0; pn < peps.length; pn++) {
            xylineandshaperenderer.setSeriesLinesVisible(pn * 3, false);
            xylineandshaperenderer.setSeriesShapesVisible(pn * 3 + 1, false);
            xylineandshaperenderer.setSeriesShapesVisible(pn * 3 + 2, false);
            xylineandshaperenderer.setSeriesPaint(pn * 3, colors[pn % 10]);
            xylineandshaperenderer.setSeriesPaint(pn * 3 + 1, colors[pn % 10]);
            xylineandshaperenderer.setSeriesStroke(pn * 3 + 1, new BasicStroke(
                    2.5f));
            xylineandshaperenderer.setSeriesPaint(pn * 3 + 2, Color.GRAY);
            xylineandshaperenderer.setSeriesStroke(pn * 3 + 2, new BasicStroke(
                    2.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
                    new float[]{6F, 6F}, 0.0F));
        }

        xylineandshaperenderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        xyplot.setRenderer(xylineandshaperenderer);

//		LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer)xyplot.getRenderer();
//		lineandshaperenderer.s
//	    xylineandshaperenderer.setSeriesLinesVisible(0, false);
//	    xylineandshaperenderer.setSeriesPaint(0, Color.RED);
//	    xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
//	    xylineandshaperenderer.setBaseToolTipGenerator(null);
//	    xyplot.setRenderer(xylineandshaperenderer);
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRange(true);
        numberaxis.setUpperBound(1.2);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(0.5);
        numberaxis.setTickUnit(unit);
        numberaxis.setTickLabelFont(tickFont);

        NumberAxis domainaxis = (NumberAxis) xyplot.getDomainAxis();
        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(3);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setLowerBound(-1);
        domainaxis.setUpperBound(50);
        domainaxis.setTickLabelFont(tickFont);

        BufferedImage image = jfreechart.createBufferedImage(1600, 1200);

        ImageIO.write(image, "PNG", new File(out + "\\" + id + ".png"));
    }

    public void drawMutilPepAndPro(int id, String ref, String[][] peps, String[] pro) throws IOException, JXLException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        double max = 0;

        for (int pn = 0; pn < peps.length; pn++) {

            String[] pepline = peps[pn];

            double[] ratio = new double[6];
            for (int i = 0; i < 6; i++) {
                ratio[i] = pepline[i + 3].length() > 0 ? Double.parseDouble(pepline[i + 3]) : 0;
            }

            double a1 = Double.parseDouble(pepline[9]);
            double a2 = pepline[10].length() > 0 ? Double.parseDouble(pepline[10]) : 0.0;
            double a3 = Double.parseDouble(pepline[11]);
            double a4 = Double.parseDouble(pepline[13]);

            XYSeries point = new XYSeries(pepline[0]);
            XYSeries line = new XYSeries("");
//			XYSeries halfx = new XYSeries("");
//			XYSeries halfy = new XYSeries("");

            for (int i = 0; i < ratio.length; i++) {
                if (ratio[i] > 0) point.add(times[i], ratio[i]);
            }

//			halfx.add(a4, 0.0);
//			halfx.add(a4, (a1)/2.0);

//			halfy.add(0, (a1)/2.0);
//			halfy.add(50, (a1)/2.0);
//			System.out.println(id+"\t"+(a2+(a1-a2)*Math.exp(-a3*a4))+"\t"+(a1)/2.0+"\t"+a1+"\t"+a2+"\t"+a3);
//			System.out.println(id+"\t"+(-Math.log((a1-2*a2)/(2*a1-2*a2))/a3)+"\t"+a4);
            for (int i = 0; i < 100; i++) {
                line.add(0.5 * (double) i, a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i));
            }

            collection.addSeries(point);
            collection.addSeries(line);
//			collection.addSeries(halfx);
//			collection.addSeries(halfy);
        }

        double[] proratio = new double[6];
        for (int i = 0; i < proratio.length; i++) {
            proratio[i] = pro[2 * i + 1].length() > 0 ? Double.parseDouble(pro[2 * i + 1]) : 0;
        }
        double a1 = Double.parseDouble(pro[14]);
        double a2 = pro[15].length() > 0 ? Double.parseDouble(pro[15]) : 0.0;
        double a3 = Double.parseDouble(pro[16]);
        double a4 = Double.parseDouble(pro[18]);

        XYSeries point = new XYSeries("");
        XYSeries line = new XYSeries("");
        XYSeries halfx = new XYSeries("");
        XYSeries halfy = new XYSeries("");

        for (int i = 0; i < proratio.length; i++) {
            if (proratio[i] > 0) point.add(times[i], proratio[i]);
        }
        halfx.add(a4, 0.0);
        halfx.add(a4, (a1) / 2.0);

        halfy.add(0, (a1) / 2.0);
        halfy.add(50, (a1) / 2.0);

        for (int i = 0; i < 100; i++) {
            line.add(0.5 * (double) i, a2 + (a1 - a2) * Math.exp(-a3 * 0.5 * (double) i));
        }

        collection.addSeries(point);
        collection.addSeries(line);
        collection.addSeries(halfx);
        collection.addSeries(halfy);

        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD, 60);
        java.awt.Font legentFont = new java.awt.Font("Times", java.awt.Font.PLAIN, 30);
        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 50);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD, 50);

        JFreeChart jfreechart = ChartFactory.createXYLineChart(ref,
                "Time (h)", "Relative Isotope Abundance",
                (XYDataset) collection, PlotOrientation.VERTICAL, false, true, false);

//		LegendTitle lt = jfreechart.getLegend();
//		lt.setItemFont(legentFont);
        jfreechart.setTitle(new TextTitle(ref, titleFont));

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        jfreechart.setBackgroundPaint(Color.WHITE);
        jfreechart.setBorderPaint(Color.WHITE);

        xyplot.setBackgroundPaint(Color.WHITE);
//		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();

        for (int pn = 0; pn < peps.length; pn++) {
            xylineandshaperenderer.setSeriesLinesVisible(pn * 2, false);
            xylineandshaperenderer.setSeriesShapesVisible(pn * 2 + 1, false);
            xylineandshaperenderer.setSeriesPaint(pn * 2, Color.GRAY);
            xylineandshaperenderer.setSeriesPaint(pn * 2 + 1, Color.GRAY);
            xylineandshaperenderer.setSeriesStroke(pn * 2 + 1, new BasicStroke(
                    6f));
        }

        int proplotid = collection.getSeriesCount() - 4;
        int prolineid = collection.getSeriesCount() - 3;
        int prohalfxid = collection.getSeriesCount() - 2;
        int prohalfyid = collection.getSeriesCount() - 1;

        xylineandshaperenderer.setSeriesLinesVisible(proplotid, false);
        xylineandshaperenderer.setSeriesShapesVisible(prolineid, false);
        xylineandshaperenderer.setSeriesShapesVisible(prohalfxid, false);
        xylineandshaperenderer.setSeriesShapesVisible(prohalfyid, false);
        xylineandshaperenderer.setSeriesPaint(proplotid, Color.red);
        xylineandshaperenderer.setSeriesPaint(prolineid, Color.red);
        xylineandshaperenderer.setSeriesStroke(prolineid, new BasicStroke(
                6f));
        xylineandshaperenderer.setSeriesPaint(prohalfxid, Color.red);
        xylineandshaperenderer.setSeriesStroke(prohalfxid, new BasicStroke(
                6f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
                new float[]{9F, 9F}, 0.0F));
        xylineandshaperenderer.setSeriesPaint(prohalfyid, Color.red);
        xylineandshaperenderer.setSeriesStroke(prohalfyid, new BasicStroke(
                6f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
                new float[]{9F, 9F}, 0.0F));

        xylineandshaperenderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
//        xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        xyplot.setRenderer(xylineandshaperenderer);

//		LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer)xyplot.getRenderer();
//		lineandshaperenderer.s
//	    xylineandshaperenderer.setSeriesLinesVisible(0, false);
//	    xylineandshaperenderer.setSeriesPaint(0, Color.RED);
//	    xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
//	    xylineandshaperenderer.setBaseToolTipGenerator(null);
//	    xyplot.setRenderer(xylineandshaperenderer);
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRange(true);
        numberaxis.setUpperBound(1.1);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(0.5);
        numberaxis.setTickUnit(unit);
        numberaxis.setTickLabelFont(tickFont);

        NumberAxis domainaxis = (NumberAxis) xyplot.getDomainAxis();
        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(5);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setLowerBound(-1);
        domainaxis.setUpperBound(51);
        domainaxis.setTickLabelFont(tickFont);

        BufferedImage image = jfreechart.createBufferedImage(1600, 1200);

        ImageIO.write(image, "PNG", new File(out + "\\" + id + ".png"));
    }

    public void drawIntenBar() throws IOException, JXLException
    {

        ExcelReader reader = new ExcelReader(in, 1);
        String[] line = reader.readLine();
//		String [] line = null;
        int id = 0;

        while ((line = reader.readLine()) != null) {

            id++;
            DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
            for (int i = 0; i < 6; i++) {
                double inten = Double.parseDouble(line[i + 16]);
                defaultcategorydataset.addValue(inten, String.valueOf(i), String.valueOf(i));
            }

            JFreeChart jfreechart = ChartFactory.createBarChart("",
                    "", "Intensity",
                    defaultcategorydataset, PlotOrientation.VERTICAL, false, true, false);

            CategoryPlot categoryplot = jfreechart.getCategoryPlot();
            jfreechart.setBackgroundPaint(Color.WHITE);
            jfreechart.setBorderPaint(Color.WHITE);
            categoryplot.setBackgroundPaint(Color.WHITE);

            BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
            barrenderer.setMaximumBarWidth(0.1);
            barrenderer.setShadowVisible(false);
            barrenderer.setDrawBarOutline(false);

            BufferedImage image = jfreechart.createBufferedImage(width, heigth);

//		    String [] ss = seq.split("[:.]");
            String output = out + "//" + id + ".png";
            ImageIO.write(image, "PNG", new File(output));

            if (id == 20) break;
        }
        reader.close();
    }

}
