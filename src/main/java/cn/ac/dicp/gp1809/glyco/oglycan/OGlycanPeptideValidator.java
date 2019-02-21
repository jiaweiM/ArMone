/* 
 ******************************************************************************
 * File: OGlycanPeptideValidator.java * * * Created on 2014��11��7��
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.drawjf.MyXYPointerAnnotation3;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.drawjf.Annotations;
import cn.ac.dicp.gp1809.proteome.spectrum.*;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.RegionTopNIntensityFilter;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import jxl.JXLException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author ck
 */
public class OGlycanPeptideValidator {
	
	private static final double ionscoreThres = 15;
	private ProteinNameAccesser accesser;
	
	public OGlycanPeptideValidator(){
		
	}
	
public void readIn(String file) throws FileDamageException, IOException, PeptideParsingException{
		
		IPeptideListReader reader = new PeptideListReader(file);
		MascotPeptide mpeptide = null;
		ProteinNameAccesser accesser = reader.getProNameAccesser();
		if(this.accesser==null){
			this.accesser = accesser;
		}else{
			this.accesser.appand(accesser);
		}
//		this.ionType = new int []{Ion.TYPE_B, Ion.TYPE_Y};
//		this.aaf = new AminoacidFragment(reader.getSearchParameter().getStaticInfo(), reader.getSearchParameter().getVariableInfo());
		RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(8, 100);

		while((mpeptide=(MascotPeptide) reader.getPeptide())!=null){
			
			String scanname = mpeptide.getBaseName();
			if(scanname.endsWith(", ")) scanname = scanname.substring(0, scanname.length()-2);
			
//if(!scanname.contains("3116.4"))continue;
			if(mpeptide.getIonscore()<ionscoreThres)
				continue;
			
			mpeptide.reCal4PValue(0.01f);
			if(mpeptide.getEvalue()>0.01) continue;
			if(mpeptide.getIonscore()<mpeptide.getIdenThres()) continue;
			if(mpeptide.getHomoThres()==0){
				if(mpeptide.getIonscore()<mpeptide.getIdenThres()){
					continue;
				}
			}else{
				if(mpeptide.getIonscore()<mpeptide.getIdenThres() && mpeptide.getIonscore()<mpeptide.getHomoThres()){
					continue;
				}
			}
			
//			IMS2PeakList peaklist = reader.getPeakLists()[0];
			IMS2PeakList peaklist = filter.filter(reader.getPeakLists()[0]);
			String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
		}
		reader.close();
	}

	public static void ETDDraw(String site, String ppl, String out) throws FileDamageException, IOException, JXLException{
		RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(8, 100);
		HashMap<String, String> pepmap = new HashMap<String, String>();
		ExcelReader er = new ExcelReader(site);
		String[] line = er.readLine();
		while((line=er.readLine())!=null){
			pepmap.put(line[0], line[6]);
		}
		er.close();
		
		PeptideListReader reader =  new PeptideListReader(ppl);
		AminoacidFragment aaf = new AminoacidFragment(reader.getSearchParameter().getStaticInfo(), reader.getSearchParameter().getVariableInfo());
		int [] types = new int []{Ion.TYPE_C, Ion.TYPE_Z};
		
		IPeptide peptide = null;
		while((peptide=reader.getPeptide())!=null){
			String scan = peptide.getScanNum();
			if(pepmap.containsKey(scan)){
				IMS2PeakList peaklist = filter.filter(reader.getPeakLists()[0]);
				Ions ions = aaf.fragment(peptide.getSequence(), types, true);
				if(peptide.getScanNumBeg()==9077){
					Ion[] cs = ions.getIons(Ion.TYPE_C);
					Ion[] zs = ions.getIons(Ion.TYPE_Z);
					for(int i=0;i<cs.length;i++){
//						System.out.println(i+"\t"+cs[i]+"\t"+zs[i]);
					}
//					System.out.println(out);
					draw(ions, types, peptide.getSequence(), pepmap.get(scan), peaklist, peptide.getCharge(), out, String.valueOf(peptide.getScanNumBeg()));
				}
			}
		}
		reader.close();
	}
	
	public static void ETDDraw2(String site, String ppl, String out) throws FileDamageException, IOException, JXLException{
		RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(8, 100);
		HashMap<String, String> pepmap = new HashMap<String, String>();
		ExcelReader er = new ExcelReader(site, 1);
		String[] line = er.readLine();
		while((line=er.readLine())!=null){
			pepmap.put(line[0], line[1]);
		}
		er.close();
		
		PeptideListReader reader =  new PeptideListReader(ppl);
		AminoacidFragment aaf = new AminoacidFragment(reader.getSearchParameter().getStaticInfo(), reader.getSearchParameter().getVariableInfo());
		int [] types = new int []{Ion.TYPE_C, Ion.TYPE_Z, Ion.TYPE_B, Ion.TYPE_Y};
		DecimalFormat df2 = DecimalFormats.DF0_2;
		
		MascotPeptide peptide = null;
		while((peptide=(MascotPeptide) reader.getPeptide())!=null){
			String scan = peptide.getScanNum();
			if(pepmap.containsKey(scan)){
				IMS2PeakList peaklist = filter.filter(reader.getPeakLists()[0]);
				Ions ions = aaf.fragment(peptide.getSequence(), types, true);
				double score = peptide.getIonscore();
				double delta = peptide.getDeltaS();
				
				StringBuilder tsb = new StringBuilder();
				tsb.append("Scan:").append(peptide.getScanNumBeg()).append("     ");
				tsb.append("Ion score:").append(df2.format(score)).append("     ");
				tsb.append("Delta score:").append(df2.format(delta)).append("     ");
				tsb.append("\n");
				tsb.append(pepmap.get(scan));
				String title = tsb.toString();
				
				if(peptide.getScanNumBeg()==9077){
					Ion[] cs = ions.getIons(Ion.TYPE_C);
					Ion[] zs = ions.getIons(Ion.TYPE_Z);
					for(int i=0;i<cs.length;i++){
//						System.out.println(i+"\t"+cs[i]+"\t"+zs[i]);
					}
//					System.out.println(out);
					draw(ions, types, peptide.getSequence(), pepmap.get(scan), peaklist, peptide.getCharge(), out, String.valueOf(peptide.getScanNumBeg()), title);
				}
			}
		}
		reader.close();
	}
	
	public static void HCDDraw(String site, String ppl, String out) throws FileDamageException, IOException, JXLException{
		RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(8, 100);
		HashMap<String, String> pepmap = new HashMap<String, String>();
		ExcelReader er = new ExcelReader(site);
		String[] line = er.readLine();
		while((line=er.readLine())!=null){
			pepmap.put(line[0], line[6]);
		}
		er.close();
		
		PeptideListReader reader =  new PeptideListReader(ppl);
		AminoacidFragment aaf = new AminoacidFragment(reader.getSearchParameter().getStaticInfo(), reader.getSearchParameter().getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		
		IPeptide peptide = null;
		while((peptide=reader.getPeptide())!=null){
			String scan = peptide.getScanNum();
			if(pepmap.containsKey(scan)){
				IMS2PeakList peaklist = filter.filter(reader.getPeakLists()[0]);
				Ions ions = aaf.fragment(peptide.getSequence(), types, true);
//				if(peptide.getScanNumBeg()==2901){
					Ion[] cs = ions.getIons(Ion.TYPE_C);
					Ion[] zs = ions.getIons(Ion.TYPE_Z);
//					for(int i=0;i<cs.length;i++){
//						System.out.println(i+"\t"+cs[i]+"\t"+zs[i]);
//					}
					draw(ions, types, peptide.getSequence(), pepmap.get(scan), peaklist, peptide.getCharge(), out, String.valueOf(peptide.getScanNumBeg()));
//				}
			}
		}
		reader.close();
	}
	
	public static void draw(Ions ions, int [] types, String sequence, String modseq, 
			IMS2PeakList peaklist, short charge, String out, String filename) throws IOException{
		
		double mz = peaklist.getPrecursePeak().getMz();

		if(peaklist.getBasePeak()==null)
			return;
		
		PeakForMatch[] peaks = SpectrumMatcher.match(peaklist, mz, ions, null, charge, null, ions.getTypes());
		
//		String title = "Scan : "+ filename+"        "+modseq;
		String title = filename;
		XYSeriesCollection collection = new XYSeriesCollection();
		Annotations ans = new Annotations(true);
		XYSeries s1 = new XYSeries("m1");
		XYSeries s2 = new XYSeries("m2");
		
		for (int i = 0; i < peaks.length; i++) {

			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();
			if(peaks[i].isMatch2(Ion.TYPE_C)){
				Ion[] ins = peaks[i].getMatchIons(Ion.TYPE_C);
				ArrayList<String> list = new ArrayList<String>();
				for(int j=0;j<ins.length;j++){
					if(ins[j]!=null){
						String label = ins[j].getName(j+1);
						list.add(label);
					}
				}
				if(list.size()>0){
					s2.add(mzi, inteni);
					String[] labels = list.toArray(new String[list.size()]); System.out.println(Arrays.toString(labels)+"\t"+mzi);
					Color[] cs = new Color[labels.length];
					Arrays.fill(cs, Color.RED);
					ans.add3(String.valueOf(mzi), labels, cs, mzi, inteni);
				}
			}else if(peaks[i].isMatch2(Ion.TYPE_Z)){
				Ion[] ins = peaks[i].getMatchIons(Ion.TYPE_Z); 
				ArrayList<String> list = new ArrayList<String>();
				for(int j=0;j<ins.length;j++){
					if(ins[j]!=null){
						String label = ins[j].getName(j+1);
						list.add(label);
					}
				}
				if(list.size()>0){
					s2.add(mzi, inteni);
					String[] labels = list.toArray(new String[list.size()]); System.out.println(Arrays.toString(labels)+"\t"+mzi);
					Color[] cs = new Color[labels.length];
					Arrays.fill(cs, Color.RED);
					ans.add3(String.valueOf(mzi), labels, cs, mzi, inteni);
				}	
			}else{
				s1.add(mzi, inteni);
			}
		}
		collection.addSeries(s1);
		collection.addSeries(s2);
		
		XYBarDataset dataset = new XYBarDataset(collection, 2d);

		NumberAxis numberaxis = new NumberAxis("m/z");
		NumberAxis domainaxis = new NumberAxis("Relative Intensity");

		XYBarRenderer renderer = new XYBarRenderer();
		MyXYPointerAnnotation3[] anns3 = ans.getAnnotations3();
		for (int i = 0; i < anns3.length; i++) {
//			renderer.addAnnotation(anns3[i]);
		}
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
		xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

		java.awt.Font titleFont = new java.awt.Font("Times",
				java.awt.Font.BOLD, 16);
		java.awt.Font legentFont = new java.awt.Font("Times",
				java.awt.Font.PLAIN, 60);
		java.awt.Font labelFont = new java.awt.Font("Times",
				java.awt.Font.BOLD, 20);
		java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
				20);

		numberaxis.setAutoRange(true);
		numberaxis.setLabelFont(labelFont);
		NumberTickUnit unit = new NumberTickUnit(500);
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
		jfreechart.setTitle(new TextTitle(title, titleFont));
		
        BufferedImage image = jfreechart.createBufferedImage(1200, 800);
        String output = out+"//"+filename+".png";
		ImageIO.write(image, "PNG", new File(output));
	}
	
	public static void draw(Ions ions, int [] types, String sequence, String modseq, 
			IMS2PeakList peaklist, short charge, String out, String filename, String title) throws IOException{
		
		double mz = peaklist.getPrecursePeak().getMz();

		if(peaklist.getBasePeak()==null)
			return;
		
		PeakForMatch[] peaks = SpectrumMatcher.match(peaklist, mz, ions, null, charge, null, ions.getTypes());
		
		XYSeriesCollection collection = new XYSeriesCollection();
		Annotations ans = new Annotations(true);
		XYSeries s1 = new XYSeries("m1");
		XYSeries s2 = new XYSeries("m2");
		XYSeries s3 = new XYSeries("m3");
		
		for (int i = 0; i < peaks.length; i++) {

			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();
			if(peaks[i].isMatch2(Ion.TYPE_C)){
				Ion[] ins = peaks[i].getMatchIons(Ion.TYPE_C);
				ArrayList<String> list = new ArrayList<String>();
				for(int j=0;j<ins.length;j++){
					if(ins[j]!=null){
						String label = ins[j].getName(j+1);
						list.add(label);
					}
				}
				if(list.size()>0){System.out.println(mzi+"\t"+list);
					s2.add(mzi, inteni);
					String[] labels = list.toArray(new String[list.size()]); 
					Color[] cs = new Color[labels.length];
					Arrays.fill(cs, Color.RED);
					ans.add3(String.valueOf(mzi), labels, cs, mzi, inteni);
				}
			}else if(peaks[i].isMatch2(Ion.TYPE_Z)){
				Ion[] ins = peaks[i].getMatchIons(Ion.TYPE_Z); 
				ArrayList<String> list = new ArrayList<String>();
				for(int j=0;j<ins.length;j++){
					if(ins[j]!=null){
						String label = ins[j].getName(j+1);
						list.add(label);
					}
				}
				if(list.size()>0){System.out.println(mzi+"\t"+list);
					s2.add(mzi, inteni);
					String[] labels = list.toArray(new String[list.size()]);
					Color[] cs = new Color[labels.length];
					Arrays.fill(cs, Color.RED);
					ans.add3(String.valueOf(mzi), labels, cs, mzi, inteni);
				}	
			}else if(peaks[i].isMatch2(Ion.TYPE_B)){
				Ion[] ins = peaks[i].getMatchIons(Ion.TYPE_B); 
				ArrayList<String> list = new ArrayList<String>();
				for(int j=0;j<ins.length;j++){
					if(ins[j]!=null){
						String label = ins[j].getName(j+1);
						list.add(label);
					}
				}
				if(list.size()>0){System.out.println(mzi+"\t"+list);
					s3.add(mzi, inteni);
					String[] labels = list.toArray(new String[list.size()]);
					Color[] cs = new Color[labels.length];
					Arrays.fill(cs, Color.RED);
					ans.add3(String.valueOf(mzi), labels, cs, mzi, inteni);
				}	
			}else if(peaks[i].isMatch2(Ion.TYPE_Y)){
				Ion[] ins = peaks[i].getMatchIons(Ion.TYPE_Y); 
				ArrayList<String> list = new ArrayList<String>();
				for(int j=0;j<ins.length;j++){
					if(ins[j]!=null){
						String label = ins[j].getName(j+1);
						list.add(label);
					}
				}
				if(list.size()>0){System.out.println(mzi+"\t"+list);
					s3.add(mzi, inteni);
					String[] labels = list.toArray(new String[list.size()]); 
					Color[] cs = new Color[labels.length];
					Arrays.fill(cs, Color.RED);
					ans.add3(String.valueOf(mzi), labels, cs, mzi, inteni);
				}	
			}else{
				s1.add(mzi, inteni);
			}
		}
		collection.addSeries(s1);
		collection.addSeries(s2);
		collection.addSeries(s3);
		
		XYBarDataset dataset = new XYBarDataset(collection, 2d);

		NumberAxis numberaxis = new NumberAxis("m/z");
		NumberAxis domainaxis = new NumberAxis("Relative Intensity");

		XYBarRenderer renderer = new XYBarRenderer();
		MyXYPointerAnnotation3[] anns3 = ans.getAnnotations3();
		for (int i = 0; i < anns3.length; i++) {
			renderer.addAnnotation(anns3[i]);
		}
		renderer.setSeriesPaint(0, new Color(150, 150, 150));
		renderer.setSeriesFillPaint(0, null);
		renderer.setSeriesPaint(1, new Color(40, 80, 220));
		renderer.setSeriesFillPaint(1, null);
		renderer.setSeriesPaint(2, new Color(204, 0, 102));
		renderer.setSeriesFillPaint(2, null);
		renderer.setDrawBarOutline(false);
		renderer.setShadowVisible(false);

		XYPlot xyplot = new XYPlot(dataset, numberaxis, domainaxis, renderer);
		xyplot.setBackgroundPaint(Color.white);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

		java.awt.Font titleFont = new java.awt.Font("Times",
				java.awt.Font.BOLD, 16);
		java.awt.Font legentFont = new java.awt.Font("Times",
				java.awt.Font.PLAIN, 60);
		java.awt.Font labelFont = new java.awt.Font("Times",
				java.awt.Font.BOLD, 20);
		java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
				20);

		numberaxis.setAutoRange(true);
		numberaxis.setLabelFont(labelFont);
		NumberTickUnit unit = new NumberTickUnit(500);
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
		jfreechart.setTitle(new TextTitle(title, titleFont));
		
        BufferedImage image = jfreechart.createBufferedImage(1200, 800);
        String output = out+"//"+filename+".png";
		ImageIO.write(image, "PNG", new File(output));
	}
	
	public static void batchETDDraw(String in) throws FileDamageException, IOException, JXLException{
		File dir = new File(in);
		File[] files = dir.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().endsWith("ppl")){
//			if(files[i].getName().endsWith("F003857.dat.ppl")){
				String ppl = files[i].getAbsolutePath();
				String out = ppl.substring(0, ppl.length()-8);
				String site = out+".dat.site.xls";
				File outfile = new File(out);
				if(!outfile.exists()){
					outfile.mkdir();
				}
				ETDDraw(site, ppl, out);
			}
		}
	}
	
	public static void batchETDDraw(String combineResult, String ppls) throws FileDamageException, IOException, JXLException{
		File dir = new File(ppls);
		File[] files = dir.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().endsWith("ppl")){
//			if(files[i].getName().endsWith("F003857.dat.ppl")){
				String ppl = files[i].getAbsolutePath();
				String out = ppl.substring(0, ppl.length()-8);
				File outfile = new File(out);
				if(!outfile.exists()){
					outfile.mkdir();
				}
				ETDDraw2(combineResult, ppl, out);
			}
		}
	}
	
	public static void batchHCDDraw(String in) throws FileDamageException, IOException, JXLException{
		File dir = new File(in);
		File[] files = dir.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().endsWith("ppl")){
//			if(files[i].getName().endsWith("F003857.dat.ppl")){
				String ppl = files[i].getAbsolutePath();
				String out = ppl.substring(0, ppl.length()-8);
				String site = out+".dat.site.xls";
				File outfile = new File(out);
				if(!outfile.exists()){
					outfile.mkdir();
				}
				HCDDraw(site, ppl, out);
			}
		}
	}

	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException, JXLException {
		// TODO Auto-generated method stub

//		batchETDDraw("H:\\OGLYCAN2\\20141110_OGlycan_ETD\\fetuin");
//		batchETDDraw("H:\\OGLYCAN2\\20141110_OGlycan_ETD\\fetuin\\result.combine20.xls", 
//				"H:\\OGLYCAN2\\20141110_OGlycan_ETD\\fetuin");
		batchETDDraw("H:\\OGLYCAN2\\20150116_ETHCD\\result.combine20.xls", 
				"H:\\OGLYCAN2\\20150116_ETHCD");
	}

}
