/* 
 ******************************************************************************
 * File: VelosPeptideCombiner.java * * * Created on 2013-3-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.combiner;

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
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ck
 *
 * @version 2013-3-24, 14:48:47
 */
public class VelosPeptideCombiner {
	
	private File site;
	private File peak;
	private HashMap <String, IMS2PeakList> peakmap;
	private HashMap <String, Double> scoremap;
	private HashMap <String, String> scanmap;
	
	public VelosPeptideCombiner(File site, File peak){
		this.site = site;
		this.peak = peak;
		this.scoremap = new HashMap <String, Double>();
		this.peakmap = new HashMap <String, IMS2PeakList>();
		this.scanmap = new HashMap <String, String>();
	}

	public void extract() throws IOException, DtaFileParsingException{
		
		HashMap <Integer, HashMap <String, double[]>> map = new HashMap <Integer, HashMap <String, double[]>>();
		BufferedReader pepreader = new BufferedReader(new FileReader(site));
		String [] peptitle = pepreader.readLine().split("\t");
		int pepseqid = -1;
		int chargeid = -1;
		int mzid = -1;
		int scanid = -1;
		int pepscoreid = -1;
		int proid = -1;

		for(int i=0;i<peptitle.length;i++){
			
			if(peptitle[i].equals("Modified Sequence")){
				pepseqid = i;
				
			}else if(peptitle[i].equals("Charge")){
				chargeid = i;
				mzid = i+1;
				
			}else if(peptitle[i].equals("Best Identification Scan Number")){
				scanid = i;
				
			}else if(peptitle[i].equals("Score")){
				pepscoreid = i;
				
			}else if(peptitle[i].equals("Leading Proteins")){
				proid = i;
				
			}
		}
		
		String pepline = null;
		while((pepline=pepreader.readLine())!=null){

			String [] pep = pepline.split("\t");
			if(pep[proid].startsWith("REV") || pep[proid].startsWith("CON"))
				continue;
			
			String seq = pep[pepseqid];
			seq = seq.replaceAll("_", "");
			seq = seq.replaceAll("\\(de\\)", "*");
			seq = seq.replaceAll("\\(ox\\)", "@");
			seq = seq.replaceAll("\\(ac\\)", "#");
			
			int scan = Integer.parseInt(pep[scanid]);
			double charge = Double.parseDouble(pep[chargeid]);
			double mz = Double.parseDouble(pep[mzid]);
			double score = Double.parseDouble(pep[pepscoreid]);
			double [] mzcharge = new double []{mz, charge, score};
//			System.out.println(scan);
			if(map.containsKey(scan)){
				map.get(scan).put(seq, mzcharge);
			}else{
				HashMap <String, double[]> pepmap = new HashMap <String, double[]>();
				pepmap.put(seq, mzcharge);
				map.put(scan, pepmap);
			}
		}
		pepreader.close();
		System.out.println(map.size());
		
		AminoacidModification aam = new AminoacidModification();
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		aam.addModification('*', 0.984016, "Deamidated");
		aam.addModification('@', 15.994915, "Oxidation");
		aam.addModification('#', 42.010565, "Acetyl");
		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		SpectrumMatchDatasetConstructor constructor = new SpectrumMatchDatasetConstructor(aaf);
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		
		int id = 0;
		File out = new File(site.getParent()+"\\result");
		out.mkdir();
		
		PrintWriter pw = new PrintWriter(site.getParent()+"\\result.txt");
		MgfReader peakreader = new MgfReader(peak);
		MS2Scan scan = null;
		while((scan=peakreader.getNextMS2Scan())!=null){

			int scannum = scan.getScanNum();
			double mz = scan.getPrecursorMZ();
			double charge = scan.getCharge();
			IMS2PeakList peaklist = scan.getPeakList();
			
			if(map.containsKey(scannum)){
				HashMap <String, double[]> pepmap = map.get(scannum);
				Iterator <String> it = pepmap.keySet().iterator();
				while(it.hasNext()){
					String pep = it.next();
					String noxi = pep.replaceAll("@", "");
					double [] info = pepmap.get(pep);
//					System.out.println(pep);
					if(Math.abs(mz-info[0])<0.1 && charge==info[1]){
						draw(constructor, types, noxi, "X."+pep+".X", peaklist, out.getAbsolutePath(), ++id);
						pw.write(pep+"\t"+info[2]+"\n");
					}
				}
			}
		}
		peakreader.close();
		pw.close();
	}
	
	public static void draw(SpectrumMatchDatasetConstructor constructor, int [] types, String sequence, String modseq, 
			IMS2PeakList peaklist, String out, int id) throws IOException{
		
		double mz = peaklist.getPrecursePeak().getMz();
		short charge = peaklist.getPrecursePeak().getCharge();

		ISpectrumDataset dataset = constructor.construct(peaklist, mz, charge, modseq, sequence, types, true);
		NumberAxis numberaxis = new NumberAxis(dataset.getXAxisLabel());
//      numberaxis.setAutoRangeIncludesZero(false);
		NumberAxis domainaxis = new NumberAxis(dataset.getYAxisLabel());
//      numberaxis1.setAutoRangeIncludesZero(false);

        XYBarRenderer renderer = new XYBarRenderer();
        Color[] colors = dataset.getColorForDataset();
        int size = dataset.getSeriesCount();
        for(int i=0;i<size;i++) {
        	renderer.setSeriesPaint(i, colors[i]);
        	renderer.setSeriesFillPaint(i, null);
        }
		
        XYAnnotation  [] annotations = dataset.getAnnotations(); 
        if(annotations!=null){
        	for(int i=0,n=annotations.length;i<n;i++) {
        		XYTextAnnotation ann = (XYTextAnnotation)annotations[i];
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
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
        
        java.awt.Font titleFont = new java.awt.Font("Times", java.awt.Font.BOLD , 24);
		java.awt.Font legentFont = new java.awt.Font("Times", java.awt.Font.PLAIN , 30);
		java.awt.Font labelFont = new java.awt.Font("Times", java.awt.Font.BOLD , 14);
		java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD , 14);
		
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
        String output = out+"//"+id+".png";
		ImageIO.write(image, "PNG", new File(output));
	}
	
	public static void drawOneFile(String in) throws DtaFileParsingException, IOException{
		
		File [] dirs = (new File(in)).listFiles();
		for(int i=0;i<dirs.length;i++){
			System.out.println(dirs[i].getAbsolutePath());
			File [] files = dirs[i].listFiles();
			File mgf = null;
			File site = null;
			
			for(int j=0;j<files.length;j++){
				
				if(files[j].getName().endsWith("mgf")){
					mgf = files[j];
				}else if(files[j].getName().endsWith("Sites.txt")){
					site = files[j];
				}
			}
			
			if(mgf!=null && site!=null){
				VelosPeptideCombiner combiner = new VelosPeptideCombiner(site, mgf);
				combiner.extract();
				System.gc();
			}else{
				System.out.println("null\t"+dirs[i].getAbsolutePath());
			}
		}
	}
	
	public static void combine(String [] ss, String out) throws IOException{
		
		PrintWriter pw = new PrintWriter(out+".txt");
		pw.write("Sequence\tFile\n");
		
		HashMap <String, String> filemap = new HashMap <String, String>();
		HashMap <String, Double> scoremap = new HashMap <String, Double>();
		for(int i=0;i<ss.length;i++){

			File [] dirs = (new File(ss[i])).listFiles();
			for(int j=0;j<dirs.length;j++){
				File [] files = dirs[j].listFiles();
				for(int k=0;k<files.length;k++){
					if(files[k].getName().equals("result.txt")){
						
						BufferedReader reader = new BufferedReader(new FileReader(files[k]));
						String line = null;
						int id = 0;
						while((line=reader.readLine())!=null){
							String [] cs = line.split("\t");
							String seq = cs[0].replaceAll("@", "");
							seq = seq.replaceAll("#", "");
							String png = dirs[j].getAbsolutePath()+"\\result\\"+(++id)+".png";
							double score = Double.parseDouble(cs[1]);
							
							if(scoremap.containsKey(seq)){
								if(score>scoremap.get(seq)){
									scoremap.put(seq, score);
									filemap.put(seq, png);
								}
							}else{
								scoremap.put(seq, score);
								filemap.put(seq, png);
							}
						}
						reader.close();
					}
				}
			}
		}
		
		int id = 0;
		Iterator <String> it = filemap.keySet().iterator();
		while(it.hasNext()){
			
			String seq = it.next();
			pw.write(seq+"\t"+filemap.get(seq)+"\n");
			
			File to = new File(out+"\\"+(++id)+".png");
			System.out.println(to);
			
			FileInputStream from = new FileInputStream(new File(filemap.get(seq)));
			FileOutputStream tostream = new FileOutputStream(to);
			
			 byte[] buffer = new byte[1024 * 512]; 
             int length; 
             while ((length = from.read(buffer)) != -1) { 
            	 tostream.write(buffer, 0, length); 
             } 
             from.close(); 
             tostream.flush(); 
             tostream.close(); 
		}
		pw.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws DtaFileParsingException 
	 */
	public static void main(String[] args) throws DtaFileParsingException, IOException {
		// TODO Auto-generated method stub

//		VelosPeptideCombiner c = new VelosPeptideCombiner(new File("J:\\Data\\glyco_peptide\\sunzh_velos\\chymotrypsin\\1\\" +
//				"pepcap_chymoT_1_Deamidation (N)Sites.txt"), 
//				new File("J:\\Data\\glyco_peptide\\sunzh_velos\\chymotrypsin\\1\\121118_human_liver_pepcap_Hz_chymoT_2D.mgf"));
//		c.extract();
//		VelosPeptideCombiner.drawOneFile("J:\\Data\\glyco_peptide\\sunzh_velos\\test");
//		VelosPeptideCombiner.drawOneFile("J:\\Data\\glyco_peptide\\sunzh_velos\\chymotrypsin");
//		VelosPeptideCombiner.drawOneFile("J:\\Data\\glyco_peptide\\sunzh_velos\\trypsin");
//		VelosPeptideCombiner.drawOneFile("J:\\Data\\glyco_peptide\\sunzh_velos\\trypsin+GluC");
//		VelosPeptideCombiner.drawOneFile("J:\\Data\\glyco_peptide\\velos mgf\\chymotrypsin");
//		VelosPeptideCombiner.drawOneFile("J:\\Data\\glyco_peptide\\velos mgf\\trypsin+glu-c");
//		VelosPeptideCombiner.drawOneFile("J:\\Data\\glyco_peptide\\velos mgf\\trypsin");
		String s1 = "J:\\Data\\glyco_peptide\\sunzh_velos\\chymotrypsin";
		String s2 = "J:\\Data\\glyco_peptide\\sunzh_velos\\trypsin";
		String s3 = "J:\\Data\\glyco_peptide\\sunzh_velos\\trypsin+GluC";
		String s4 = "J:\\Data\\glyco_peptide\\velos mgf\\chymotrypsin";
		String s5 = "J:\\Data\\glyco_peptide\\velos mgf\\trypsin";
		String s6 = "J:\\Data\\glyco_peptide\\velos mgf\\trypsin+glu-c";
		String out = "J:\\Data\\glyco_peptide\\velos_png";
		VelosPeptideCombiner.combine(new String []{s1, s2, s3, s4, s5, s6}, out);
	}

}
