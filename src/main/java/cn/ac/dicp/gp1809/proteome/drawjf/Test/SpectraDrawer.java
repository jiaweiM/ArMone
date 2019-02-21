/**
 *
 */
package cn.ac.dicp.gp1809.proteome.drawjf.Test;

import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.drawjf.ISpectrumDataset;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumMatchDatasetConstructor;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.math.MathTool;
import jxl.JXLException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author ck
 */
public class SpectraDrawer
{

    private static double BarWidth = 5d;

    private static void draw(double[][] peaklist, int[] matchid, String out)
            throws IOException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries s1 = new XYSeries("matches");
        XYSeries s2 = new XYSeries("unmatches");
        for (int i = 0; i < matchid.length; i++) {
            if (matchid[i] == 0) {
                s1.add(peaklist[i][0], peaklist[i][1]);
            } else {
                s2.add(peaklist[i][0], peaklist[i][1]);
            }
        }
        collection.addSeries(s1);
        collection.addSeries(s2);

        XYBarDataset dataset = new XYBarDataset(collection, BarWidth);

        NumberAxis numberaxis = new NumberAxis("m/z");
        NumberAxis domainaxis = new NumberAxis("Relative Intensity");

        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesFillPaint(0, null);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesFillPaint(1, null);
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        XYPlot xyplot = new XYPlot(dataset, numberaxis, domainaxis, renderer);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new org.jfree.chart.ui.RectangleInsets(4D, 4D, 4D, 4D));

        java.awt.Font titleFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 48);
        java.awt.Font legentFont = new java.awt.Font("Times",
                java.awt.Font.PLAIN, 60);
        java.awt.Font labelFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 36);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
                36);

        numberaxis.setAutoRange(true);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(250);
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
        jfreechart.setTitle(new TextTitle("", titleFont));

        BufferedImage image = jfreechart.createBufferedImage(2400, 900);
        String output = out;
        ImageIO.write(image, "PNG", new File(output));
    }

    private static void test(String in, String out) throws IOException,
            JXLException
    {

        ExcelReader reader = new ExcelReader(in);
        String[] l1 = reader.getColumn(0);
        String[] l2 = reader.getColumn(1);
        String[] l3 = reader.getColumn(2);
        System.out.println(l1.length + "\t" + l2.length + "\t" + l3.length);
        double[] mp = new double[l3.length];
        for (int i = 0; i < l3.length; i++) {
            mp[i] = Double.parseDouble(l3[i]);
        }
        double max = 0;
        double[][] peaklist = new double[l1.length][2];
        int[] matched = new int[l1.length];
        for (int i = 0; i < l1.length; i++) {
            peaklist[i][0] = Double.parseDouble(l1[i]);
            peaklist[i][1] = Double.parseDouble(l2[i]);
            for (int j = 0; j < mp.length; j++) {
                if (Math.abs(peaklist[i][0] - mp[j]) < 0.01) {
                    matched[i] = 1;
                    break;
                }
            }
            if (peaklist[i][1] > max) {
                max = peaklist[i][1];
            }
        }
        for (int i = 0; i < l1.length; i++) {
            peaklist[i][1] = peaklist[i][1] / max;
        }
        System.out.println(MathTool.getTotal(matched));

        draw(peaklist, matched, out);
    }

    private static void test2(String in, String out, int sheetnum) throws IOException,
            JXLException
    {

        ExcelReader reader = new ExcelReader(in, sheetnum);
        String[] l1 = reader.getColumn(0);
        String[] l2 = reader.getColumn(1);
        String[] l3 = reader.getColumn(2);
        String[] l4 = reader.getColumn(3);
        System.out.println(l1.length + "\t" + l2.length + "\t" + l3.length
                + "\t" + l4.length);
        double[] mp1 = new double[l3.length];
        for (int i = 0; i < l3.length; i++) {
            mp1[i] = Double.parseDouble(l3[i]);
        }
        double[] mp2 = new double[l4.length];
        for (int i = 0; i < l4.length; i++) {
            mp2[i] = Double.parseDouble(l4[i]);
        }
        double max = 0;
        double[][] peaklist = new double[l1.length][2];
        int[] matched1 = new int[l1.length];
        int[] matched2 = new int[l1.length];
        for (int i = 0; i < l1.length; i++) {
            peaklist[i][0] = Double.parseDouble(l1[i]);
            peaklist[i][1] = Double.parseDouble(l2[i]);
            for (int j = 0; j < mp1.length; j++) {
                if (Math.abs(peaklist[i][0] - mp1[j]) < 0.01) {
                    matched1[i] = 1;
                    break;
                }
            }
            for (int j = 0; j < mp2.length; j++) {
                if (Math.abs(peaklist[i][0] - mp2[j]) < 0.01) {
                    matched2[i] = 1;
                    break;
                }
            }
            if (peaklist[i][1] > max) {
                max = peaklist[i][1];
            }
        }
        for (int i = 0; i < l1.length; i++) {
            peaklist[i][1] = peaklist[i][1] / max;
        }
        System.out.println(MathTool.getTotal(matched1) + "\t"
                + MathTool.getTotal(matched2));

        draw(peaklist, matched1, matched2, out);
    }

    private static void draw(double[][] peaklist, int[] matchid1,
            int[] matchid2, String out) throws IOException
    {

        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries s1 = new XYSeries("m1");
        XYSeries s2 = new XYSeries("m2");
        XYSeries s3 = new XYSeries("m3");
        for (int i = 0; i < matchid1.length; i++) {
            if (matchid1[i] == 0) {
                if (matchid2[i] == 0) {
                    s1.add(peaklist[i][0], peaklist[i][1]);
                } else {
                    s3.add(peaklist[i][0], peaklist[i][1]);
                }
            } else {
                if (matchid2[i] == 0) {
                    s2.add(peaklist[i][0], peaklist[i][1]);
                } else {
                    s3.add(peaklist[i][0], peaklist[i][1]);
                }
            }
        }
        collection.addSeries(s1);
        collection.addSeries(s2);
        collection.addSeries(s3);

        XYBarDataset dataset = new XYBarDataset(collection, BarWidth);

        NumberAxis numberaxis = new NumberAxis("m/z");
        NumberAxis domainaxis = new NumberAxis("Relative Intensity");

        XYBarRenderer renderer = new XYBarRenderer();
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
        xyplot.setAxisOffset(new org.jfree.chart.ui.RectangleInsets(4D, 4D, 4D, 4D));

        java.awt.Font titleFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 48);
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

        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(0.2);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setUpperBound(1.0);
        domainaxis.setTickLabelFont(tickFont);

        JFreeChart jfreechart = new JFreeChart(null,
                JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
        jfreechart.setTitle(new TextTitle("", titleFont));

        BufferedImage image = jfreechart.createBufferedImage(1100, 610);
        String output = out;
        ImageIO.write(image, "PNG", new File(output));
    }

	/*private static void drawTxt(String in, String out) throws IOException{

		double max = 0;
		ArrayList<Double> mzs = new ArrayList<Double>();
		ArrayList<Double> intens = new ArrayList<Double>();
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null){
			String [] cs = line.split(" ");
			double mz = Double.parseDouble(cs[0]);
			double inten = Double.parseDouble(cs[1]);
			if(inten>max) max = inten;
			mzs.add(mz);
			intens.add(inten);
		}
		reader.close();
		
		XYSeriesCollection collection = new XYSeriesCollection();
		XYSeries series = new XYSeries("m1");
		for(int i=0;i<mzs.size();i++){
			series.add(mzs.get(i).doubleValue(), intens.get(i)/max);
		}
		collection.addSeries(series);
		
		XYBarDataset dataset = new XYBarDataset(collection, BarWidth);

		NumberAxis numberaxis = new NumberAxis("m/z");
		NumberAxis domainaxis = new NumberAxis("Relative Intensity");

		XYBarRenderer renderer = new XYBarRenderer();
		renderer.setSeriesPaint(0, Color.BLACK);
		renderer.setSeriesFillPaint(0, null);
		renderer.setDrawBarOutline(false);
		renderer.setShadowVisible(false);

		XYPlot xyplot = new XYPlot(dataset, numberaxis, domainaxis, renderer);
		xyplot.setBackgroundPaint(Color.white);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

		java.awt.Font titleFont = new java.awt.Font("Times",
				java.awt.Font.BOLD, 48);
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
		numberaxis.setUpperBound(1500);
		
		domainaxis.setAutoRange(false);
		NumberTickUnit unit2 = new NumberTickUnit(0.2);
		domainaxis.setTickUnit(unit2);
		domainaxis.setLabelFont(labelFont);
		domainaxis.setUpperBound(1.1);
		domainaxis.setTickLabelFont(tickFont);

		JFreeChart jfreechart = new JFreeChart(null,
				JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
		jfreechart.setTitle(new TextTitle("", titleFont));

		BufferedImage image = jfreechart.createBufferedImage(1100, 610);
		ImageIO.write(image, "PNG", new File(out));
	}
	*/

    private static void drawPeptide(String sequence, String title, String peaks, int scannum,
            String out) throws DtaFileParsingException, IOException
    {
        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        AminoacidModification aam = new AminoacidModification();
        AminoacidFragment aaf = new AminoacidFragment(aas, aam);
        aam.addModification('*', 15.994915, "Oxidation");
        aam.addModification('p', 97.976896, "Phospho");
        SpectrumMatchDatasetConstructor constructor = new SpectrumMatchDatasetConstructor(aaf);
        int[] types = new int[]{Ion.TYPE_B, Ion.TYPE_Y};

        HashMap<Integer, IMS2PeakList> peakmap = new HashMap<Integer, IMS2PeakList>();
        MgfReader mgfreader = new MgfReader(peaks);
        MS2Scan scan = null;
        while ((scan = mgfreader.getNextMS2Scan()) != null) {
            peakmap.put(scan.getScanNumInteger(), scan.getPeakList());
        }
        mgfreader.close();

        IMS2PeakList peaklist = peakmap.get(scannum);
        double mz = peaklist.getPrecursePeak().getMz();
        short charge = peaklist.getPrecursePeak().getCharge();
        ISpectrumDataset dataset = constructor.construct(peaklist, mz, charge, sequence, title, types, true);

        NumberAxis numberaxis = new NumberAxis(dataset.getXAxisLabel());
//      numberaxis.setAutoRangeIncludesZero(false);
        NumberAxis domainaxis = new NumberAxis(dataset.getYAxisLabel());
//      numberaxis1.setAutoRangeIncludesZero(false);

        XYBarRenderer renderer = new XYBarRenderer();
        Color[] colors = dataset.getColorForDataset();
        int size = dataset.getSeriesCount();
        for (int i = 0; i < size; i++) {
            renderer.setSeriesPaint(i, colors[i]);
            renderer.setSeriesFillPaint(i, null);
        }

        XYAnnotation[] annotations = dataset.getAnnotations();
        if (annotations != null) {
            for (int i = 0, n = annotations.length; i < n; i++) {
                XYTextAnnotation ann = (XYTextAnnotation) annotations[i];
                Font font = ann.getFont();
                ann.setFont(font);
                renderer.addAnnotation(ann);
            }
        }

        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        XYPlot xyplot = new XYPlot(dataset.getDataset(), numberaxis, domainaxis, renderer);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new org.jfree.chart.ui.RectangleInsets(4D, 4D, 4D, 4D));

        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD, 24);
        java.awt.Font legentFont = new java.awt.Font("Times", java.awt.Font.PLAIN, 30);
        java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD, 14);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD, 14);

//		NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
        numberaxis.setAutoRange(true);
//	    numberaxis.setUpperBound(1.2);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(250);
        numberaxis.setTickUnit(unit);
        numberaxis.setTickLabelFont(tickFont);

//	    NumberAxis domainaxis = (NumberAxis)xyplot.getDomainAxis();
        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(0.2);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
//	    domainaxis.setLowerBound(-1);
        domainaxis.setUpperBound(1.1);
        domainaxis.setTickLabelFont(tickFont);

        JFreeChart jfreechart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, xyplot, dataset.createLegend());
        jfreechart.setTitle(new TextTitle(sequence, titleFont));

        BufferedImage image = jfreechart.createBufferedImage(1200, 900);
        String output = out + "//" + title + ".png";
        ImageIO.write(image, "PNG", new File(output));
    }

    /**
     * @param args
     * @throws JXLException
     * @throws IOException
     * @throws DtaFileParsingException
     */
    public static void main(String[] args) throws IOException, JXLException, DtaFileParsingException
    {
        SpectraDrawer.drawPeptide("X.ERHPSpWR.X", "_ERHPS(ph)WR_", "E:\\xu\\scan6.mgf", 4832, "E:\\xu");
    }

}
