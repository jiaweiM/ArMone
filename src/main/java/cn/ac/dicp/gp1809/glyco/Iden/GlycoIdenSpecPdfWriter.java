/* 
 ******************************************************************************
 * File: GlycoIdenSpecPdfWriter.java * * * Created on 2012-5-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Iden;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import com.itextpdf.text.DocumentException;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoSpecMatchDataset;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoStrucDrawer;
import cn.ac.dicp.gp1809.glyco.drawjf.MatchSpecPdfStamper;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2012-5-21, 10:04:54
 */
public class GlycoIdenSpecPdfWriter {
	
	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_PAGEIDX = "pageIdx";
	
	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_SCAN = "Scan";
	
	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_PEPMASS = "Peptide Mass";
	
	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_GLYCOMASS = "Glycan Mass";
	
	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_RT = "RT";
	
	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_RANK = "Rank";
	
	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_SCORE = "Score";
	
	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_NAME = "IUPAC Name";
	/**
	 * The width of the spectrum
	 */
	public static int width = 900;
	/**
	 * The height of the spectrum
	 */
	public static int height = 600;
	
//	private static String template = "src/resources/Glyco_Iden.pdf";
	
	private static String template = "/resources/Glyco_Iden.pdf";
	
	private MatchSpecPdfStamper pdf;
	
	private GlycoStrucDrawer drawer;
	
	private int index = 1;
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public GlycoIdenSpecPdfWriter(String file) throws IOException, DocumentException{
		
//		this.pdf = new MatchSpecPdfStamper(template, file, true);
		this.pdf = new MatchSpecPdfStamper((new File(System.getProperty("user.dir") + template)).
				getAbsolutePath(), file, true);
		this.drawer = new GlycoStrucDrawer();
	}
	
	public void write(NGlycoSSM ssm) throws IOException{
		
		int scannum = ssm.getScanNum();
		GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(scannum);
		dataset.createDataset(ssm);
		
		BufferedImage structure = this.drawer.draw(ssm.getGlycoTree());
		
		BufferedImage spectrum = JFChartDrawer.createXYBarChart(dataset)
			.createBufferedImage(width, height);
		
		try {
			
			this.pdf.stamp(this.getFieldMap(ssm), structure, spectrum, index-1);
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private HashMap<String, String> getFieldMap(NGlycoSSM ssm){
		
		HashMap <String, String> map = new HashMap <String, String>();
		
		map.put(FIELD_PAGEIDX, String.valueOf(index++));
		map.put(FIELD_SCAN, String.valueOf(ssm.getScanNum()));
		map.put(FIELD_PEPMASS, df4.format(ssm.getPepMass()));
		map.put(FIELD_GLYCOMASS, df4.format(ssm.getGlycoMass()));
		map.put(FIELD_RT, String.valueOf(ssm.getRT()));
		map.put(FIELD_RANK, String.valueOf(ssm.getRank()));
		map.put(FIELD_SCORE, String.valueOf(ssm.getScore()));
		map.put(FIELD_NAME, String.valueOf(ssm.getName()));
		
		return map;
	}
	
	public void close() {
		
		try {
			this.pdf.close();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		GlycoIdenSpecPdfWriter writer = new GlycoIdenSpecPdfWriter("H:\\glyco\\label-free\\20111123_HILIC_1105_HCD.pdf");
		GlycoIdenXMLReader reader = new GlycoIdenXMLReader("H:\\glyco\\label-free\\20111123_HILIC_1105_HCD.pxml");
		NGlycoSSM [] ssms = reader.getAllMatches();
		for(int i=0;i<ssms.length;i++){
			writer.write(ssms[i]);
		}
		writer.close();
	}

}
