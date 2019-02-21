package cn.ac.dicp.gp1809.proteome.drawjf.Test;

import cn.ac.dicp.gp1809.proteome.drawjf.Annotations;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import jxl.JXLException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
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
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 *
 */
public class MaxQuantDrawerXu {
	
	private HashSet <String> set;
	private HashMap <String, unit> map;
	
	private static final Color[] COLORS = new Color[] { Color.black, Color.red,
        Color.blue, Color.cyan, Color.green, Color.magenta };
	
	private static DecimalFormat DF0 = DecimalFormats.DF0_1;
	private static DecimalFormat DF4 = DecimalFormats.DF0_4;
	
	public MaxQuantDrawerXu(){
		this.set = new HashSet <String>();
		this.map = new HashMap <String, unit>();
	}
	
	private void readpeptide(String in) throws IOException, JXLException{

		ExcelReader reader1 = new ExcelReader(in);
		String [] line = reader1.readLine();
		while((line=reader1.readLine())!=null && line.length>=2){
			set.add(line[1]);
		}
		reader1.close();
	}
	
	private void read(String in) throws IOException{
		
		BufferedReader br1 = new BufferedReader(new FileReader(in));
		
		String [] peptitle = br1.readLine().split("\t");
		int pepseqid = -1;
		int fileid = -1;
		int scanid = -1;
		int pepscoreid = -1;
		int chargeid = -1;
		int matchesid = -1;
		int massesid = -1;
		int intensityid = -1;

		for(int i=0;i<peptitle.length;i++){
			
			if(peptitle[i].equals("Modified sequence")){
				pepseqid = i;
				
			}else if(peptitle[i].equals("Scan number")){
				scanid = i;
				
			}else if(peptitle[i].equals("Raw file")){
				fileid = i;
				
			}else if(peptitle[i].equals("Score")){
				pepscoreid = i;
				
			}else if(peptitle[i].equals("Charge")){
				chargeid = i;
				
			}else if(peptitle[i].equals("Matches")){
				matchesid = i;
				
			}else if(peptitle[i].equals("Masses")){
				massesid = i;
				
			}else if(peptitle[i].equals("Intensities")){
				intensityid = i;
				
			}
		}

		String pepline = null;
		while((pepline=br1.readLine())!=null){

			String [] pep = pepline.split("\t");
			String sequence = pep[pepseqid].substring(1, pep[pepseqid].length()-1);
			unit u = new unit(pep[fileid], Integer.parseInt(pep[scanid]), Double.parseDouble(pep[pepscoreid]), Integer.parseInt(pep[chargeid]),
					pep[matchesid], pep[massesid], pep[intensityid]);
			
			if(map.containsKey(sequence)){
				if(u.score>map.get(sequence).score){
					map.put(sequence, u);
				}
			}else{
				map.put(sequence, u);
			}
		}
		
		br1.close();
	}

	public void draw(String file) throws IOException, XMLStreamException{
		
		HashMap <String, unit> drawmap = new HashMap <String, unit>();
		
		Iterator <String> mapid = this.map.keySet().iterator();
		while(mapid.hasNext()){
			String key = mapid.next();
			String seq = key.replaceAll("\\(ox\\)", "");
			seq = key.replaceAll("\\(ac\\)", "");
			if(this.set.contains(seq)){
				unit u = this.map.get(key);
				u.seq = seq;
				drawmap.put(u.raw+"\t"+u.scannum, this.map.get(key));
			}
		}

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
					
					if(drawmap.containsKey(key)){
						
						unit u = drawmap.get(key);
						String [] matches = u.matches.split(";");
						String [] massesstr = u.masses.split(";");
						String []  intensstr = u.intens.split(";");

						double [] masses = new double [matches.length];
						double [] intensities = new double [matches.length];
						for(int j=0;j<masses.length;j++){
							masses[j] = Double.parseDouble(massesstr[j]);
							intensities[j] = Double.parseDouble(intensstr[j]);
						}
						
						this.draw(peaklist, u.charge, matches, masses, intensities, u.seq, out.getAbsolutePath(), ++id);
						
//						if(id==10)
//							return;
					}
				}
			}
		}		
	}
	
	public void draw(IMS2PeakList peaklist, int charge, String [] anns, double [] matchedMz, double [] intensities, 
			String sequence, String out, int id) throws IOException{

		XYSeriesCollection collection = new XYSeriesCollection();
		XYSeries series = new XYSeries("UnMatchedPeaks");
		XYSeries seriesb = new XYSeries("bPeaks");
		XYSeries seriesy = new XYSeries("yPeaks");
		XYSeries seriesother = new XYSeries("cPeaks");

		Annotations ans = new Annotations();

		IPeak [] peaks = peaklist.getPeakArray();
		double premz = peaklist.getPrecursePeak().getMz();
		int size = peaks.length;
		HashSet <Integer> usedset = new HashSet <Integer>();
		double maxintensity = 0;
		
		for (int i = 0; i < size; i++) {
			
			IPeak peak = peaks[i];
			double inten = peak.getIntensity();
			double mz = peak.getMz();
			boolean match = false;
			if(inten>maxintensity){
				maxintensity = inten;
			}
			
			for(int j=0;j<matchedMz.length;j++){
				if(Math.abs(mz-matchedMz[j])<1.0 && inten>intensities[j]*0.8 && !usedset.contains(j)){
					
					match = true;
					usedset.add(j);
					Color color = null;
					
					if(anns[j].startsWith("b")){
						seriesb.add(mz, inten);
						color = COLORS[1];
					}else if(anns[j].startsWith("y")){
						seriesy.add(mz, inten);
						color = COLORS[2];
					}else{
						seriesother.add(mz, inten);
						color = COLORS[3];
					}
					
					ArrayList<Color> colorlist = new ArrayList<Color>();
					ArrayList<String> labellist = new ArrayList<String>();
					
					labellist.add(anns[j]);
					colorlist.add(color);
//					peak.setLabel(anns[j]);
					
					String[] labels = labellist
					        .toArray(new String[labellist.size()]);
					Color[] colors = colorlist.toArray(new Color[colorlist.size()]);

					ans.add(DF0.format(peak.getMz()), labels, colors, mz, inten);
				}
			}
			
			if(!match){
				series.add(mz, inten);
			}
		}

		collection.addSeries(series);
		collection.addSeries(seriesb);
		collection.addSeries(seriesy);
		collection.addSeries(seriesother);
		
//		ISpectrumDataset dataset = constructor.construct(peaklist, mz, charge, modseq, sequence, types, true);
		NumberAxis numberaxis = new NumberAxis("m/z");
//      numberaxis.setAutoRangeIncludesZero(false);
		NumberAxis domainaxis = new NumberAxis("Intensity");
//      numberaxis1.setAutoRangeIncludesZero(false);

        XYBarRenderer renderer = new XYBarRenderer();
        for(int i=0;i<collection.getSeriesCount();i++) {
        	renderer.setSeriesPaint(i, COLORS[i]);
        	renderer.setSeriesFillPaint(i, null);
        }
		
        XYAnnotation  [] annotations = ans.getAnnotations(); 
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
        
        XYPlot xyplot = new XYPlot(new XYBarDataset(collection, 0.00005d), numberaxis, domainaxis, renderer);
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
	    numberaxis.setLowerBound(100);
//	    numberaxis.setUpperBound(1.2);
	    numberaxis.setLabelFont(labelFont);
	    NumberTickUnit unit = new NumberTickUnit(250);
	    numberaxis.setTickUnit(unit);
	    numberaxis.setTickLabelFont(tickFont);
	    
//	    NumberAxis domainaxis = (NumberAxis)xyplot.getDomainAxis();
	    domainaxis.setAutoRange(true);
//	    int i500 = (int)(maxintensity/500.0);
	    
	    double ntu = 0;
	    
	    if(maxintensity<2600){
	    	ntu = 300;
	    }else{
	    	ntu = (int)(maxintensity/2500.0)*500;
	    }
	    
	    NumberTickUnit unit2 = new NumberTickUnit(ntu);
	    domainaxis.setTickUnit(unit2);
	    domainaxis.setLabelFont(labelFont);
//	    domainaxis.setLowerBound(-1);
	    domainaxis.setUpperBound(maxintensity*1.1);
	    domainaxis.setTickLabelFont(tickFont);
		
        JFreeChart jfreechart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
        jfreechart.setTitle(new TextTitle(sequence+"  (precursor m/z:"+DF4.format(premz)+";charge:"+charge+")", titleFont));
        
        BufferedImage image = jfreechart.createBufferedImage(1200, 900);
        String output = out+"//"+id+"."+sequence+".png";
		ImageIO.write(image, "PNG", new File(output));
	}
	
	public class unit{
		
		String raw;
		int scannum;
		double score;
		int charge;
		String matches;
		String masses;
		String intens;
		String seq;
		
		unit(String raw, int scannum, double score, int charge, String matches, String masses, String intens){
			this.raw = raw;
			this.scannum = scannum;
			this.score = score;
			this.charge = charge;
			this.matches = matches;
			this.masses = masses;
			this.intens = intens;
		}
	}

	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public static void main(String[] args) throws IOException, JXLException, XMLStreamException {
		// TODO Auto-generated method stub

		MaxQuantDrawerXu xu = new MaxQuantDrawerXu();
		xu.readpeptide("K:\\For Spectra\\supplementary information\\Supplemental Table S3.xls");
		xu.read("K:\\For Spectra\\NgBR for EMT-1-txt\\msms.txt");
		xu.read("K:\\For Spectra\\NgBR for EMT-2-txt\\msms.txt");
		System.out.println(xu.set.size()+"\t"+xu.map.size());
		xu.draw("K:\\For Spectra");
	}

}
