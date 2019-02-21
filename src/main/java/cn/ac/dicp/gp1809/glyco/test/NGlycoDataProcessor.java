/* 
 ******************************************************************************
 * File: NGlycoDataProcessor.java * * * Created on 2013-11-19
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.test;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.glyco.Quan.labelFree2.GlycoLFFeasXMLReader2;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoSpecMatchDataset;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.drawjf.ISpectrumDataset;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumMatchDatasetConstructor;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.util.ioUtil.FileCopy;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import jxl.JXLException;
import org.dom4j.DocumentException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author ck
 *
 * @version 2013-11-19, 6:08:26
 */
public class NGlycoDataProcessor {
	
	private static void drawGlyco(String in, String glycos, String out) throws IOException, JXLException, DocumentException{
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		ExcelReader reader = new ExcelReader(in, 1);
		String[] line = reader.readLine();
		int count = 0;
		while ((line = reader.readLine()) != null) {
			count++;
			String key = line[0] + "\t" + line[1] + "\t" + line[9];
			System.out.println(key);
			map.put(key, count);
		}
		reader.close();
		System.out.println(map.size());
		File[] files = (new File(glycos)).listFiles();
		for (int i = 0; i < files.length; i++) {
			String filename = files[i].getName();
			if (!filename.endsWith("pxml"))
				continue;
			
			filename = filename.substring(0, filename.length()-5);
			GlycoLFFeasXMLReader2 glycoreader = new GlycoLFFeasXMLReader2(files[i]);
			NGlycoSSM[] ssms = glycoreader.getMatchedGlycoSpectra();
			for(int j=0;j<ssms.length;j++){
				int scannum = ssms[j].getScanNum();
				String name = ssms[j].getName();
				String comp = struc2comp(name);
//				String key = filename+"\t"+scannum+"\t"+name;
				String key = filename+"\t"+scannum+"\t"+comp;
				if(map.containsKey(key)){
//					System.out.println("cao");
					GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(String.valueOf(scannum));
					dataset.createDataset(ssms[j]);
					BufferedImage spectrum = JFChartDrawer.createXYBarChart(dataset)
							.createBufferedImage(800, 480);
//					String output = out + "//" + map.get(key) + ".png";
					String output = out + "//" + filename+"_"+scannum + ".png";
					ImageIO.write(spectrum, "PNG", new File(output));
					
					/*GlycoTree tree = ssms[j].getGlycoTree();
					GlycoStrucDrawer drawer = new GlycoStrucDrawer();
					BufferedImage image = drawer.draw(tree);
					String output = out + "//" + map.get(key) + ".png";
					ImageIO.write(image, "PNG", new File(output));*/
				}
			}
			glycoreader.close();
		}
	}

	private static void drwaDeglyco(String in, String peps, String out)
			throws IOException, JXLException, FileDamageException {

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		ExcelReader reader = new ExcelReader(in);
		String[] line = reader.readLine();
		int count = 0;
		while ((line = reader.readLine()) != null) {
			count++;
			String key = line[2] + "\t" + line[3];
			map.put(key, count);
		}
		reader.close();
int ccc = 0;
		int[] types = new int[] { Ion.TYPE_B, Ion.TYPE_Y };
		java.awt.Font titleFont = new java.awt.Font("Times",
				java.awt.Font.BOLD, 24);
		
		File[] files = (new File(peps)).listFiles();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].getName().endsWith("ppl"))
				continue;

			PeptideListReader pepreader = new PeptideListReader(files[i]);
			SpectrumMatchDatasetConstructor constructor = new SpectrumMatchDatasetConstructor(
					pepreader.getSearchParameter());
			IPeptide peptide = null;
			while ((peptide = pepreader.getPeptide()) != null) {

				IMS2PeakList peaklist = pepreader.getPeakLists()[0];
				String key = peptide.getScanNumBeg() + "\t"
						+ peptide.getSequence();
				if (map.containsKey(key)) {
					ISpectrumDataset dataset = constructor.construct(peaklist,
							peptide.getExperimentalMZ(), peptide.getCharge(),
							peptide.getSequence(),
							String.valueOf(map.get(key)), types, true);

					JFreeChart jfreechart = draw(dataset);
					jfreechart.setTitle(new TextTitle(peptide.getSequence(), titleFont));

					BufferedImage image = jfreechart.createBufferedImage(800,
							480);
					String output = out + "//" + map.get(key) + ".png";
					ImageIO.write(image, "PNG", new File(output));
					ccc++;
				}
			}
			pepreader.close();
		}
	}

	private static JFreeChart draw(ISpectrumDataset dataset) {

		NumberAxis numberaxis = new NumberAxis(dataset.getXAxisLabel());
		NumberAxis domainaxis = new NumberAxis(dataset.getYAxisLabel());

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

		XYPlot xyplot = new XYPlot(dataset.getDataset(), numberaxis,
				domainaxis, renderer);
		xyplot.setBackgroundPaint(Color.white);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

		java.awt.Font titleFont = new java.awt.Font("Times",
				java.awt.Font.BOLD, 24);
		java.awt.Font legentFont = new java.awt.Font("Times",
				java.awt.Font.PLAIN, 30);
		java.awt.Font labelFont = new java.awt.Font("Times",
				java.awt.Font.BOLD, 14);
		java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
				14);

		// NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
		numberaxis.setAutoRange(true);
		// numberaxis.setUpperBound(1.2);
		numberaxis.setLabelFont(labelFont);
		NumberTickUnit unit = new NumberTickUnit(250);
		numberaxis.setTickUnit(unit);
		numberaxis.setTickLabelFont(tickFont);

		// NumberAxis domainaxis = (NumberAxis)xyplot.getDomainAxis();
		domainaxis.setAutoRange(false);
		NumberTickUnit unit2 = new NumberTickUnit(0.2);
		domainaxis.setTickUnit(unit2);
		domainaxis.setLabelFont(labelFont);
		// domainaxis.setLowerBound(-1);
		domainaxis.setUpperBound(1.1);
		domainaxis.setTickLabelFont(tickFont);

		JFreeChart jfreechart = new JFreeChart(null,
				JFreeChart.DEFAULT_TITLE_FONT, xyplot, dataset.createLegend());
		
		return jfreechart;
	}

	private static String struc2comp(String struc){
		int [] count = new int[4];
		String [] units = struc.split("[-()]");
		for(int i=0;i<units.length;i++){
			if(units[i].length()>0){
				if(units[i].equals("Gal") || units[i].equals("Glc") || units[i].equals("Man")){
					count[0]++;
				}else if(units[i].equals("GalNAc") || units[i].equals("GlcNAc") || units[i].equals("ManNAc")){
					count[1]++;
				}else if(units[i].equals("Fuc")){
					count[2]++;
				}else if(units[i].equals("NeuAc")){
					count[3]++;
				}else if(units[i].equals("Asn")){

				}else{
					System.out.println("967\t"+units[i]+"\n"+struc);
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("HexNAc(").append(count[1]).append(")");
		sb.append("Hex(").append(count[0]).append(")");
		if(count[3]>0)
			sb.append("NeuAc(").append(count[3]).append(")");
		if(count[2]>0)
			sb.append("Fuc(").append(count[2]).append(")");

		return sb.toString();
	}
	
	private static void copyPng(String result, String resource, String target) throws IOException, JXLException{
		HashSet<String> set = new HashSet<String>();
		ExcelReader reader = new ExcelReader(result, 1);
		String[] line = reader.readLine();
		while((line=reader.readLine())!=null){
			set.add(line[0]+"_"+line[1]+".png");
		}
		reader.close();
		
		File[] files = (new File(resource)).listFiles();
		for(int i=0;i<files.length;i++){
			String name = files[i].getName();
			if(set.contains(name)){
				File targetfile = new File(target+"\\"+name);
				FileCopy.Copy(files[i], targetfile);
			}
		}
	}
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 * @throws FileDamageException 
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException, JXLException, DocumentException {
		// TODO Auto-generated method stub

		NGlycoDataProcessor.drwaDeglyco("D:\\P\\n-glyco\\2014.05.31\\Supplementary dataset.xls", 
				"H:\\NGLYCO\\NGlyco_original_data_20130613\\2D\\iden",
				"D:\\P\\n-glyco\\2014.05.31\\Deglycopeptide spectra");
//		NGlycoDataProcessor.drawGlyco("D:\\P\\n-glyco\\2014.05.31\\Supplementary dataset.xls", 
//				"H:\\NGLYCO\\NGlyco_final_20140408", "D:\\P\\n-glyco\\2014.05.31\\spectra");
//		NGlycoDataProcessor.copyPng("D:\\P\\n-glyco\\2014.05.31\\Supplementary dataset.xls", "H:\\NGLYCO\\NGlyco_final_20140408\\glycopeptide", 
//				"D:\\P\\n-glyco\\2014.05.31\\spectra");
	}

}
