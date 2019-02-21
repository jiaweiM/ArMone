/* 
 ******************************************************************************
 * File: TritofPeptideCombiner.java * * * Created on 2013-3-22
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
 * @version 2013-3-22, 16:19:57
 */
public class TritofPeptideCombiner {
	
	private File peptide;
	private File peak;
	private double conf;
	private HashMap <String, IMS2PeakList> peakmap;
	private HashMap <String, Double> scoremap;
	private HashMap <String, String> modseqmap;
	private HashMap <String, String> sourcemap;
//	private HashSet <String> modset;
//	private HashMap <String, Integer> modcountmap;
//	private AminoacidFragment aaf;
//	private ISpectrumThreshold threshold;

	public TritofPeptideCombiner(File peptide, File peak, double conf){
		
		this.peptide = peptide;
		this.peak = peak;
		this.conf = conf;
		this.scoremap = new HashMap <String, Double>();
		this.modseqmap = new HashMap <String, String>();
		this.peakmap = new HashMap <String, IMS2PeakList>();
		this.sourcemap = new HashMap <String, String>();
	}
	
	public void extract() throws IOException, DtaFileParsingException {
		
		HashMap <String, IMS2PeakList> totalpeakmap = new HashMap <String,IMS2PeakList>();
		MgfReader peakreader = new MgfReader(peak);
		MS2Scan scan = null;
		while((scan=peakreader.getNextMS2Scan())!=null){
			String name = scan.getScanName().getBaseName();
			name = name.substring(name.indexOf(":")+1);
			IMS2PeakList peaklist = scan.getPeakList();
			totalpeakmap.put(name, peaklist);
		}
		peakreader.close();
		
		BufferedReader pepreader = new BufferedReader(new FileReader(peptide));
		String [] peptitle = pepreader.readLine().split("\t");
		int pepaccess = -1;
		int pepseq = -1;
		int pepmod = -1;
		int pepconf = -1;
		int pepspectrum = -1;

		for(int i=0;i<peptitle.length;i++){
			
			if(peptitle[i].equals("Sequence")){
				pepseq = i;
				
			}else if(peptitle[i].equals("Accessions")){
				pepaccess = i;
				
			}else if(peptitle[i].equals("Modifications")){
				pepmod = i;
				
			}else if(peptitle[i].equals("Conf")){
				pepconf = i;
				
			}else if(peptitle[i].equals("Spectrum")){
				pepspectrum = i;
				
			}
		}
		
		String pepline = null;
L:		while((pepline=pepreader.readLine())!=null){
			
			String [] pep = pepline.split("\t");
			String seq = pep[pepseq];
			String mod = pep[pepmod];
			String spectrum = pep[pepspectrum];
			double pepconfscore = Double.parseDouble(pep[pepconf]);
			
			if(pepconfscore<this.conf || mod.length()==0 || !mod.contains("Deamidated"))
				continue;
			
			if(!totalpeakmap.containsKey(spectrum))
				continue;
			
			StringBuilder glycosb = new StringBuilder();
			StringBuilder modsb = new StringBuilder();
			String [] mods = mod.split("; ");
			HashMap <Integer, Character> modmap = new HashMap <Integer, Character>();
			for(int j=0;j<mods.length;j++){
				
				int at = mods[j].indexOf("@");
				String name = mods[j].substring(0, at);
				
				if(name.equals("Deamidated(N)")){
					int loc = Integer.parseInt(mods[j].substring(at+1, mods[j].length()));
					modmap.put(loc, '*');
				}else if(name.equals("Deamidated(Q)")){
					int loc = Integer.parseInt(mods[j].substring(at+1, mods[j].length()));
					modmap.put(loc, '*');
				}else if(name.equals("Deamidated(R)")){
					int loc = Integer.parseInt(mods[j].substring(at+1, mods[j].length()));
					modmap.put(loc, '*');
				}else if(name.equals("Carbamidomethyl(C)")){
					int loc = Integer.parseInt(mods[j].substring(at+1, mods[j].length()));
					modmap.put(loc, '@');
				}else if(name.equals("Oxidation(M)")){
					int loc = Integer.parseInt(mods[j].substring(at+1, mods[j].length()));
					modmap.put(loc, '#');
				}else{
					continue L;
				}
			}
			
			for(int j=0;j<seq.length();j++){
				if(modmap.containsKey(j)){
					if(seq.charAt(j-1)=='N'){
						glycosb.append('*');
						modsb.append('*');
					}else{
						modsb.append(modmap.get(j));
					}
					glycosb.append(seq.charAt(j));
					modsb.append(seq.charAt(j));
				}else{
					glycosb.append(seq.charAt(j));
					modsb.append(seq.charAt(j));
				}
			}
			
			String modseq = glycosb.toString();
			String source = peak.getName().replace(".mgf", "\t"+spectrum);
			if(this.scoremap.containsKey(modseq)){
				if(pepconfscore>scoremap.get(modseq)){
					modseqmap.put(modseq, modsb.toString());
					scoremap.put(modseq, pepconfscore);
					peakmap.put(modseq, totalpeakmap.get(spectrum));
					sourcemap.put(modseq, source);
				}
			}else{
				modseqmap.put(modseq, modsb.toString());
				scoremap.put(modseq, pepconfscore);
				peakmap.put(modseq, totalpeakmap.get(spectrum));
				sourcemap.put(modseq, source);
			}
		}
		
		pepreader.close();
	}

	public static void combine(String in, HashMap <String, Double> totalscoremap, HashMap <String, String> totalmodseqmap,
			HashMap <String, IMS2PeakList> totalpeakmap, HashMap <String, String> totalsourcemap) 
					throws IOException, DtaFileParsingException {
		
		HashMap <String, Double> thresmap = new HashMap <String, Double>();
		HashMap <String, File> pepmap = new HashMap <String, File>();
		HashMap <String, File> mgfmap = new HashMap <String, File>();
		File [] dirs = (new File(in)).listFiles();
		for(int i=0;i<dirs.length;i++){
			System.out.println(dirs[i].getAbsolutePath());
			File [] files = dirs[i].listFiles();
			for(int j=0;j<files.length;j++){
				
				String name = files[j].getName().substring(0, files[j].getName().length()-4);
				
				if(files[j].getName().endsWith("mgf")){
					mgfmap.put(name+"_PeptideSummary", files[j]);
					
				}else{
					
					if(name.endsWith("PeptideSummary")){
						pepmap.put(name, files[j]);
					}else if(name.endsWith("ProteinSummary")){
//						mgfmap.put(name.replace("ProteinSummary", "PeptideSummary"), files[j]);
					}else{
						int id = name.lastIndexOf("_");
						double thres = Double.parseDouble(name.substring(id+1));
						thresmap.put(name.substring(0, id), thres);
					}
				}
			}
		}
		System.out.println(pepmap.size()+"\t"+mgfmap.size());
		Iterator <String> it = thresmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
//			System.out.println(key+"\t"+pepmap.get(key)+"\t"+mgfmap.get(key));
			TritofPeptideCombiner combiner = new TritofPeptideCombiner(pepmap.get(key), mgfmap.get(key), thresmap.get(key));
			combiner.extract();
			
			HashMap <String, Double> scoremap = combiner.scoremap;
			HashMap <String, String> modseqmap = combiner.modseqmap;
			HashMap <String, IMS2PeakList> peakmap = combiner.peakmap;
			HashMap <String, String> sourcemap = combiner.sourcemap;
			
			Iterator <String> it2 = scoremap.keySet().iterator();
			while(it2.hasNext()){
				String key2 = it2.next();
				if(totalscoremap.containsKey(key2)){
					if(scoremap.get(key2)>totalscoremap.get(key2)){
						totalscoremap.put(key2, scoremap.get(key2));
						totalmodseqmap.put(key2, modseqmap.get(key2));
						totalpeakmap.put(key2, peakmap.get(key2));
						totalsourcemap.put(key2, sourcemap.get(key2));
					}
				}else{
					totalscoremap.put(key2, scoremap.get(key2));
					totalmodseqmap.put(key2, modseqmap.get(key2));
					totalpeakmap.put(key2, peakmap.get(key2));
					totalsourcemap.put(key2, sourcemap.get(key2));
				}
			}
		}
	}

	public static void totalCombine(String [] ss, String out) throws IOException, DtaFileParsingException {
		
		PrintWriter pw = new PrintWriter(out+".txt");
		pw.write("Sequence\tFile\tScan\n");
		
		HashMap <String, Double> totalscoremap = new HashMap <String, Double>();
		HashMap <String, String> totalmodseqmap = new HashMap <String, String>();
		HashMap <String, IMS2PeakList> totalpeakmap = new HashMap <String, IMS2PeakList>(); 
		HashMap <String, String> totalsourcemap = new HashMap <String, String>();
		
		for(int i=0;i<ss.length;i++){
			HashMap <String, Double> scoremap = new HashMap <String, Double>();
			HashMap <String, String> modseqmap = new HashMap <String, String>();
			HashMap <String, IMS2PeakList> peakmap = new HashMap <String, IMS2PeakList>();
			HashMap <String, String> sourcemap = new HashMap <String, String>();
			TritofPeptideCombiner.combine(ss[i], scoremap, modseqmap, peakmap, sourcemap);
			
			Iterator <String> it = scoremap.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				if(totalscoremap.containsKey(key)){
					if(scoremap.get(key)>totalscoremap.get(key)){
						totalscoremap.put(key, scoremap.get(key));
						totalmodseqmap.put(key, modseqmap.get(key));
						totalpeakmap.put(key, peakmap.get(key));
						totalsourcemap.put(key, sourcemap.get(key));
					}
				}else{
					totalscoremap.put(key, scoremap.get(key));
					totalmodseqmap.put(key, modseqmap.get(key));
					totalpeakmap.put(key, peakmap.get(key));
					totalsourcemap.put(key, sourcemap.get(key));
				}
			}
		}
		System.out.println("total\t"+totalscoremap.size()+"\t"+totalmodseqmap.size()+"\t"+totalpeakmap.size());
		
		AminoacidModification aam = new AminoacidModification();
		Aminoacids aas = new Aminoacids();
		aam.addModification('*', 0.984016, "Deamidated");
		aam.addModification('#', 15.994915, "Oxidation");
		aam.addModification('@', 57.021464, "Carbamidomethyl");
		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		SpectrumMatchDatasetConstructor constructor = new SpectrumMatchDatasetConstructor(aaf);
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		
		int id = 0;
		Iterator <String> it = totalscoremap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String modseq = "X."+totalmodseqmap.get(key)+".X";
			IMS2PeakList peaklist = totalpeakmap.get(key);
			draw(constructor, types, key, modseq, peaklist, out, ++id);
			pw.write(key+"\t"+totalsourcemap.get(key)+"\n");
//			if(id==10){
//				break;
//			}
		}
		
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
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws DtaFileParsingException 
	 */
	public static void main(String[] args) throws IOException, DtaFileParsingException {
		// TODO Auto-generated method stub

		String s1 = "H:\\20130902_glyco\\5600_1D\\protein level\\chymotrypsin";
		String s2 = "H:\\20130902_glyco\\5600_1D\\protein level\\trypsin";
		String s3 = "H:\\20130902_glyco\\5600_1D\\protein level\\trypsin+GluC";
		String s4 = "J:\\Data\\glyco_peptide\\5600 data_zhujun\\trypsin";
		String s5 = "J:\\Data\\glyco_peptide\\5600 data_zhujun\\T+C";
		String s6 = "J:\\Data\\glyco_peptide\\5600 data_zhujun\\chymotrypsin";
		String out = "H:\\20130902_glyco\\5600_1D\\protein level\\protein_level_png";
//		TritofPeptideCombiner.totalCombine(new String []{"J:\\Data\\glyco_peptide\\5600_test"}, out);
		TritofPeptideCombiner.totalCombine(new String []{s1, s2, s3}, out);
		
	}

}
