/* 
 ******************************************************************************
 * File: WangDataSpecProcessor.java * * * Created on 2012-10-10
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamException;

import org.dom4j.DocumentException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;

/**
 * @author ck
 *
 * @version 2012-10-10, 14:31:53
 */
public class WangDataSpecProcessor {
	
	private static int width = 800;
	private static int heigth = 600;
	private static double BarWidth = 0.0001d;
	
	public static void extractLabelInfo(String in, String out) throws DocumentException, IOException{
		
		HashMap <String, String> contentMap = new HashMap <String, String>();
		HashMap <String, Double> scoreMap = new HashMap <String, Double>();
		
		MutilLabelFeaturesXMLReader reader = new MutilLabelFeaturesXMLReader(in);
		reader.readAllPairs();
		for(int i=0;i<reader.getPairsNum();i++){
			
			PeptidePair pair = reader.getPairs(i);
			String seq = pair.getSequence();
			IPeptide pep = pair.getPeptide();
			String basename = pep.getBaseName();
			int scannum = pep.getScanNumBeg();

			int maxSn = -1;
			int maxnum = 0;
			double maxinten = 0;
			double totalInten = 0;
			
			double [][] totalintens = pair.getFeatures().getIntenList();
			int [] scanlist = pair.getFeatures().getScanList();
			for(int j=0;j<totalintens.length;j++){
				int num = 0;
				double total = 0;
				for(int k=0;k<totalintens[j].length;k++){
					if(totalintens[j][k]>0){
						num++;
						total += totalintens[j][k];
					}
					if(num>=maxnum && total>maxinten){
						maxnum = num;
						maxinten = total;
						maxSn = scanlist[j];
					}
				}
				totalInten += total;
			}

			if(maxSn==-1) continue;
			
			double [] masses = pair.getFeatures().getMasses();
			double diff = masses[masses.length-1]-masses[0];
			double beg = masses[0]-diff/4.0;
			double end = masses[masses.length-1]+diff/4.0;
			
			StringBuilder sb = new StringBuilder();
			sb.append(seq).append("\t");
			sb.append(basename).append("\t");
			sb.append(scannum).append("\t");
			sb.append(maxSn).append("\t");
			sb.append(beg).append("\t");
			sb.append(end).append("\t");
			
			
			for(int j=0;j<masses.length;j++){
				sb.append(masses[j]).append("\t");
			}
			
			if(contentMap.containsKey(seq)){
				if(totalInten>scoreMap.get(seq)){
					contentMap.put(seq, sb.toString());
					scoreMap.put(seq, totalInten);
				}
			}else{
				contentMap.put(seq, sb.toString());
				scoreMap.put(seq, totalInten);
			}			
		}
		reader.close();
		
		PrintWriter pw = new PrintWriter(out);
		Iterator <String> it = contentMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			pw.write(contentMap.get(key)+"\n");
		}
		pw.close();
	}

	public static void extractSpecInfo(String in, String out, String spec) throws IOException, XMLStreamException{
		
		PrintWriter pw = new PrintWriter(out);
//		double PROTON_W = AminoAcidProperty.PROTON_W;
		
		HashMap <String, ArrayList <String[]>> totalMap = new HashMap <String, ArrayList <String[]>>();
		HashMap <String, HashSet <String>> scanMap = new HashMap <String, HashSet <String>>();
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null){
			String [] ss = line.split("\t");
			if(totalMap.containsKey(ss[1])){
				totalMap.get(ss[1]).add(ss);
				scanMap.get(ss[1]).add(ss[3]);
			}else{
				ArrayList <String[]> list = new ArrayList <String[]>();
				list.add(ss);
				totalMap.put(ss[1], list);
				HashSet <String> set = new HashSet <String>();
				set.add(ss[3]);
				scanMap.put(ss[1], set);
			}
		}
		reader.close();
		System.out.println(totalMap.size());
		
		Iterator <String> it = totalMap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();			
			System.out.println(key);
			String spfile = spec+key+".mzXML";
			ArrayList <String[]> list = totalMap.get(key);
			HashSet <String> set = scanMap.get(key);
			
			MzXMLReader mzXMLReader = new MzXMLReader(spfile);
			ISpectrum spectrum = null;
			
			HashMap <String, IPeak []> peakmap = new HashMap <String, IPeak []>();
			
			while((spectrum=mzXMLReader.getNextSpectrum())!=null){
				
				int level = spectrum.getMSLevel();
				if(level==1){
					
					IPeak [] peaks = spectrum.getPeakList().getPeakArray();
					String scannum = String.valueOf(spectrum.getScanNum());
					
					if(set.contains(scannum)){
						peakmap.put(scannum, peaks);
					}
					
				}
			}
			
			Iterator <String[]> ms2it = list.iterator();
			while(ms2it.hasNext()){
				
				String [] content = ms2it.next();				
				IPeak [] peaks = peakmap.get(content[3]);

				StringBuilder sb = new StringBuilder();
				
				double begin = Double.parseDouble(content[4]);
				double end = Double.parseDouble(content[5]);
				for(int i=0;i<content.length;i++){
					if(i<=3){
						sb.append(content[i]).append("\t");
					}else if(i>=6){
						sb.append(Double.parseDouble(content[i])).append("\t");
					}
				}
				sb.append("\n");

				for(int i=0;i<peaks.length;i++){
					double mz = peaks[i].getMz();
					double inten = peaks[i].getIntensity();
					if(mz>=begin && mz<=end){
						sb.append(mz).append("\t");
						sb.append(inten).append("\t");
					}
				}
				sb.append("\n");
				pw.write(sb.toString());
			}
			
			mzXMLReader.close();
			break;
		}
		pw.close();
	}
	
	public static void drawSpec(String in, String out) throws IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		int count = 0;
		while((line=reader.readLine())!=null){
			
			count++;
			String [] info = line.split("\t");
			String [] peaks = reader.readLine().split("\t");
			
			String seq = info[0].substring(2, info[0].length()-2);
			double [] labels = new double [6];
			for(int i=0;i<labels.length;i++){
				labels[i] = Double.parseDouble(info[i+4]);
			}
			
			XYSeriesCollection collection = new XYSeriesCollection();
			XYSeries series = new XYSeries("");
			XYSeries [] labelSeries = new XYSeries [6];
			for(int i=0;i<labelSeries.length;i++){
				labelSeries[i] = new XYSeries("");
			}
			
			double max = 0;
			double [] labelInten = new double [6];
L:			for(int i=0;i<peaks.length;){
				double mz = Double.parseDouble(peaks[i++]);
				double inten = Double.parseDouble(peaks[i++]);

				for(int j=0;j<labels.length;j++){
					if(Math.abs(mz-labels[j])<0.02){
						if(labelSeries[j].getItemCount()==0){
							if(inten>max) max = inten;
							labelSeries[j].add(mz, inten);
							labelInten[j] = inten;
							continue L;
						}
					}
				}
				series.add(mz, inten);
			}
			
			collection.addSeries(series);
			
			XYBarDataset dataset = new XYBarDataset(collection, BarWidth);
			XYBarRenderer renderer = new XYBarRenderer();
	        renderer.setShadowVisible(false);
	        renderer.setSeriesPaint(0, Color.BLACK);
	        java.awt.Font font = new java.awt.Font("Times", java.awt.Font.PLAIN, 12);
	        
			for(int i=0;i<labelSeries.length;i++){
				if(labelSeries[i].getItemCount()==0){
					labelSeries[i].add(labels[i], 0);
				}
				collection.addSeries(labelSeries[i]);
				XYPointerAnnotation annotation = new XYPointerAnnotation("L"+(i+1), labels[i], labelInten[i], Math.PI*1.5);
				annotation.setBaseRadius(25d);
				annotation.setArrowStroke(new BasicStroke(0, BasicStroke.CAP_ROUND,
				        BasicStroke.JOIN_ROUND, 1, new float[] { 3, 1 }, 0));
				annotation.setArrowWidth(1.5);
				annotation.setFont(font);
				renderer.addAnnotation(annotation);
				if(i%2==0){
					renderer.setSeriesPaint(i+1, Color.RED);
				}else{
					renderer.setSeriesPaint(i+1, Color.BLUE);
				}
			}

			NumberAxis numberaxis = new NumberAxis("");
	        numberaxis.setAutoRangeIncludesZero(false);
	        
	        NumberAxis numberaxis1 = new NumberAxis("");
	        numberaxis1.setAutoRangeIncludesZero(false);
	        numberaxis1.setUpperBound(max*1.2);

			XYPlot xyplot = new XYPlot(dataset, numberaxis, numberaxis1, renderer);
	        xyplot.setBackgroundPaint(Color.white);
	        xyplot.setDomainGridlinePaint(Color.white);
	        xyplot.setRangeGridlinePaint(Color.white);
	        xyplot.setAxisOffset(new org.jfree.chart.ui.RectangleInsets(4D, 4D, 4D, 4D));

			JFreeChart jfreechart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, 
					xyplot, false);
			
			jfreechart.setBackgroundPaint(Color.WHITE);
			jfreechart.setBorderPaint(Color.WHITE);
			xyplot.setBackgroundPaint(Color.WHITE);

			BufferedImage image = jfreechart.createBufferedImage(width, heigth);
			
//		    String [] ss = seq.split("[:.]");
			String output = out+"//"+seq+".png";
			ImageIO.write(image, "PNG", new File(output));
			
			if(count==20) break;
		}
		reader.close();
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public static void main(String[] args) throws DocumentException, IOException, XMLStreamException {
		// TODO Auto-generated method stub
		
		long begin = System.currentTimeMillis();

		String in = "H:\\WFJ_mutiple_label\\turnover\\new\\0_3_6.pxml";
		String out = "H:\\WFJ_mutiple_label\\turnover\\new\\spectra\\0_3_6.txt";
		String out2 = "H:\\WFJ_mutiple_label\\turnover\\new\\spectra\\0_3_6_spectra.txt";
		String out3 = "H:\\WFJ_mutiple_label\\turnover\\new\\spectra\\0_3_6";
		String spec = "H:\\WFJ_mutiple_label\\turnover\\0_3_6\\";
//		WangDataSpecProcessor.extractLabelInfo(in, out);
//		WangDataSpecProcessor.extractSpecInfo(out, out2, spec);
		WangDataSpecProcessor.drawSpec(out2, out3);
		
		long end = System.currentTimeMillis();
		System.out.println((end-begin)/6E4);
	}

}
