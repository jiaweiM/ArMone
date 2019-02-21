/* 
 ******************************************************************************
 * File: GlycoMatchSpecPdfWriter.java * * * Created on 2012-5-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import com.itextpdf.text.DocumentException;

import cn.ac.dicp.gp1809.glyco.drawjf.GlycoStrucDrawer;
import cn.ac.dicp.gp1809.glyco.drawjf.MatchSpecPdfStamper;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2012-5-25, 15:16:52
 */
public class GlycoMatchSpecPdfWriter {
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
	
//	private static String template = "src/resources/Glyco_Pep_Match.pdf";
	private static String template = "/resources/Glyco_Pep_Match.pdf";
	
	private MatchSpecPdfStamper pdf;
	
	private GlycoStrucDrawer drawer;
	
	private int index = 1;
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public GlycoMatchSpecPdfWriter(String file) throws IOException, DocumentException{
//		this.pdf = new MatchSpecPdfStamper(template, file, true);
		this.pdf = new MatchSpecPdfStamper((new File(System.getProperty("user.dir") + template)).
				getAbsolutePath(), file, true);
		this.drawer = new GlycoStrucDrawer();
	}
	
	public void write(IGlycoPeptide pep){
		
		NGlycoSSM ssm = pep.getDeleStructure();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
