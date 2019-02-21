/* 
 ******************************************************************************
 * File: OGlycanPdfWriter.java * * * Created on 2013-2-27
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import com.itextpdf.text.DocumentException;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoSpecMatchDataset;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2013-2-27, 16:22:33
 */
public class OGlycanPdfWriter {
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
	protected static final String FIELD_SEQUENCE = "Sequence";

	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_NAME = "Glycan";
	
	/**
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_PROTEINS = "Protein";
		
	/**
	 * The width of the spectrum
	 */
	public static int width = 800;
	/**
	 * The height of the spectrum
	 */
	public static int height = 480;
	
	private static String template = "/resources/OGlycanTemplate.pdf";

	private OGlycanPdfStamper pdf;

	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	private int index = 1;
	
	public OGlycanPdfWriter(String output) throws IOException, DocumentException{

//		this.pdf = new OGlycanPdfStamper(template, output, true);
		this.pdf = new OGlycanPdfStamper((new File(System.getProperty("user.dir") + template)).
				getAbsolutePath(), output, true);
	}
	
	public void write(OGlycanPepInfo opInfo) throws IOException{
		
		String scanname = opInfo.getScanname();
		GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(scanname);
		dataset.createDataset(opInfo);
		
		BufferedImage spectrum = JFChartDrawer.createXYBarChart(dataset)
				.createBufferedImage(width, height);
			
		try {
			
			this.pdf.stamp(opInfo, spectrum, index-1);
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
/*	protected HashMap<String, String> getFieldMap(OGlycanPepInfo opInfo){
		
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuilder masssb = new StringBuilder();
		StringBuilder namesb = new StringBuilder();
		OGlycanSiteInfo [] sites = opInfo.getSiteInfo();
		for(int i=0;i<sites.length;i++){
			masssb.append(df4.format(sites[i].getGlycoMass())).append(";");
			namesb.append(sites[i].getGlycoName()).append("\n");
		}
		masssb.deleteCharAt(masssb.length()-1);
		namesb.deleteCharAt(namesb.length()-1);
		
		map.put(FIELD_PAGEIDX, String.valueOf(index++));
		map.put(FIELD_SCAN, String.valueOf(opInfo.getScanname()));
		map.put(FIELD_PEPMASS, df4.format(opInfo.getPeptide().getMH()-AminoAcidProperty.PROTON_W));
		map.put(FIELD_GLYCOMASS, masssb.toString());
		map.put(FIELD_PROTEINS, opInfo.getDeleRef());
		map.put(FIELD_SEQUENCE, opInfo.getModseq());
		map.putAll(opInfo.getAnnotionMap());

		return map;
	}
*/	
	public void close() {
		try {
			this.pdf.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, DocumentException {
		// TODO Auto-generated method stub

		OGlycanPdfWriter writer = new OGlycanPdfWriter("");
	}

}
