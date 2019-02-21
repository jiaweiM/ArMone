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
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
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
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ck
 *
 */
public class MaxQuantDrawer {
	
	private String file;
	
	public MaxQuantDrawer(String file) throws IOException{
		this.file = file;
	}
	
	private void parsePhosPeptide() throws IOException, DtaFileParsingException, XMLStreamException{
		
		HashMap <String, HashMap <String, double[]>> map = new HashMap <String, HashMap <String, double[]>>();
		BufferedReader pepreader = new BufferedReader(new FileReader(file+"\\Phospho (STY)Sites.txt"));
		String [] peptitle = pepreader.readLine().split("\t");
		int pepseqid = -1;
		int chargeid = -1;
		int mzid = -1;
		int fileid = -1;
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
				
			}else if(peptitle[i].equals("Best Localization Raw File")){
				fileid = i;
				
			}else if(peptitle[i].equals("Score")){
				pepscoreid = i;
				
			}else if(peptitle[i].equals("Leading Proteins")){
				proid = i;
				
			}
		}
		
		HashMap <String, String> namemap = new HashMap <String, String>();
		
		String pepline = null;
		while((pepline=pepreader.readLine())!=null){

			String [] pep = pepline.split("\t");
			if(pep[proid].startsWith("REV") || pep[proid].startsWith("CON"))
				continue;
			
			StringBuilder seqsb = new StringBuilder();
			for(int i=1;i<pep[pepseqid].length()-1;i++){
				char c = pep[pepseqid].charAt(i);
				if(c=='p'){
					seqsb.append(pep[pepseqid].charAt(i+1));
					seqsb.append(c);
					i++;
				}else if(c=='('){
					if(pep[pepseqid].charAt(i+1)=='o'){
						seqsb.append('*');
						i+=3;
					}
				}else{
					seqsb.append(c);
				}
			}
			String seq = seqsb.toString();

			String key = pep[fileid]+"\t"+pep[scanid];
			double charge = Double.parseDouble(pep[chargeid]);
			double mz = Double.parseDouble(pep[mzid]);
			double score = Double.parseDouble(pep[pepscoreid]);
			double [] mzcharge = new double []{mz, charge, score};
//			System.out.println(key);
			
			namemap.put(key, pep[pepseqid]);
			if(map.containsKey(key)){
				map.get(key).put(seq, mzcharge);
			}else{
				HashMap <String, double[]> pepmap = new HashMap <String, double[]>();
				pepmap.put(seq, mzcharge);
				map.put(key, pepmap);
			}
		}
		pepreader.close();
		System.out.println(map.size());
		
		AminoacidModification aam = new AminoacidModification();
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		aam.addModification('*', 15.994915, "Oxidation");
		aam.addModification('p', 97.976896, "Phospho");
		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		SpectrumMatchDatasetConstructor constructor = new SpectrumMatchDatasetConstructor(aaf);
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		
		int id = 0;
		File out = new File(file+"\\spectra");
		out.mkdir();
		
		File [] files = (new File(file)).listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().endsWith("mzXML")){

				MzXMLReader peakreader = new MzXMLReader(files[i]);
				MS2Scan scan = null;
				String rawname = files[i].getName().substring(0, files[i].getName().length()-6);
				
				while((scan=peakreader.getNextMS2Scan())!=null){

					String key = rawname+"\t"+scan.getScanNum();
//					System.out.println(key);
					double mz = scan.getPrecursorMZ();
					double charge = scan.getCharge();
					IMS2PeakList peaklist = scan.getPeakList();
					
					if(map.containsKey(key)){
						HashMap <String, double[]> pepmap = map.get(key);
						Iterator <String> it = pepmap.keySet().iterator();
						while(it.hasNext()){
							String pep = it.next();
							double [] info = pepmap.get(pep);
//							System.out.println(pep);
							if(charge==info[1]){
								draw(constructor, types, namemap.get(key), "X."+pep+".X", peaklist, out.getAbsolutePath(), ++id);
							}
						}
					}
				}
			}
		}
	}

	public static void draw(SpectrumMatchDatasetConstructor constructor, int [] types, String sequence, String modseq, 
			IMS2PeakList peaklist, String out, int id) throws IOException{
		
		double mz = peaklist.getPrecursePeak().getMz();
		short charge = peaklist.getPrecursePeak().getCharge();

		if(peaklist.getBasePeak()==null)
			return;
		
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
        String output = out+"//"+id+""+sequence+".png";
		ImageIO.write(image, "PNG", new File(output));
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws DtaFileParsingException 
	 * @throws XMLStreamException 
	 */
	public static void main(String[] args) throws IOException, DtaFileParsingException, XMLStreamException {
		// TODO Auto-generated method stub

		long begin = System.currentTimeMillis();
		
		String in1 = "K:\\2013-06-14-CK2 data\\04-04\\tech-1";
		MaxQuantDrawer drawer1 = new MaxQuantDrawer(in1);
		drawer1.parsePhosPeptide();
		
		String in2 = "K:\\2013-06-14-CK2 data\\04-04\\tech-2";
		MaxQuantDrawer drawer2 = new MaxQuantDrawer(in2);
		drawer2.parsePhosPeptide();
		
		String in3 = "K:\\2013-06-14-CK2 data\\04-04\\tech-3";
		MaxQuantDrawer drawer3 = new MaxQuantDrawer(in3);
		drawer3.parsePhosPeptide();
		
		long end = System.currentTimeMillis();
		System.out.println((end-begin)/6E4);
	}

}
