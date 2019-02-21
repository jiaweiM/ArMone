/* 
 ******************************************************************************
 * File: MatchSpecWriter.java * * * Created on 2011-12-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.drawjf;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.itextpdf.text.DocumentException;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * @author ck
 *
 * @version 2011-12-23, 18:26:58
 */
public class MatchSpecPdfWriter {

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
	 * The field name of the specific term in the template
	 */
	protected static final String FIELD_PROTEINS = "Protein";
		
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
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	private int index = 1;
	
	public MatchSpecPdfWriter(String output) throws IOException, DocumentException{
		
		this.pdf = new MatchSpecPdfStamper((new File(System.getProperty("user.dir") + template)).
				getAbsolutePath(), output, true);
		this.drawer = new GlycoStrucDrawer();
	}

	public void write(NGlycoSSM ssm, IGlycoPeptide peptide) throws IOException{

		GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(peptide.getScanNumBeg()+"_"+ssm.getScanNum());
		dataset.createDataset(ssm);
		
		BufferedImage structure = this.drawer.draw(ssm.getGlycoTree());
		
		BufferedImage spectrum = JFChartDrawer.createXYBarChart(dataset)
			.createBufferedImage(width, height);
		
		try {
			
			this.pdf.stamp(this.getFieldMap(peptide, ssm), structure, spectrum, index-1);
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writer(HashMap<String, String> valueMap, Image structure, Image spectrum, int index) throws Exception{
		this.pdf.stamp(valueMap, structure, spectrum, index-1);
	}

	protected HashMap<String, String> getFieldMap(IGlycoPeptide peptide, NGlycoSSM ssm){
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(FIELD_PAGEIDX, String.valueOf(index++));
		map.put(FIELD_SCAN, String.valueOf(ssm.getScanNum()));
		map.put(FIELD_PEPMASS, df4.format(ssm.getPepMass()));
		map.put(FIELD_GLYCOMASS, df4.format(ssm.getGlycoMass()));
		map.put(FIELD_RT, String.valueOf(ssm.getRT()));
		map.put(FIELD_RANK, String.valueOf(ssm.getRank()));
		map.put(FIELD_SCORE, String.valueOf(ssm.getScore()));
		map.put(FIELD_NAME, String.valueOf(ssm.getName()));
		map.put(FIELD_PROTEINS, peptide.getDelegateReference());
		map.put(FIELD_SEQUENCE, peptide.getSequence());

		return map;
	}
	
	public void close() {
		try {
			this.pdf.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void combine(String info, String structure, String spectra, String out) throws Exception{
		MatchSpecPdfWriter writer = new MatchSpecPdfWriter(out);
		ExcelReader reader = new ExcelReader(info, 1);
		String [] line = reader.readLine();
		int i = 1;
		while((line=reader.readLine())!=null){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(FIELD_PAGEIDX, String.valueOf(i));
			map.put(FIELD_SCAN, line[0]+"_"+line[1]);
			map.put(FIELD_PEPMASS, line[14]);
			map.put(FIELD_GLYCOMASS, line[6]);
			map.put(FIELD_RT, line[2]);
			map.put(FIELD_RANK, "1");
			map.put(FIELD_SCORE, line[8]);
			String [] ss = line[9].split("[-()]");
			int [] comp = new int[4];
			for(int j=0;j<ss.length;j++){
				if(ss[j].equals("ManNAc") || ss[j].equals("GalNAc") || ss[j].equals("GlcNAc")){
					comp[0]++;
				}else if(ss[j].equals("Gal") || ss[j].equals("Glc") || ss[j].equals("Man")){
					comp[1]++;
				}else if(ss[j].equals("NeuAc")){
					comp[2]++;
				}else if(ss[j].equals("Fuc")){
					comp[3]++;
				}
			}
			StringBuilder sb = new StringBuilder();
			for(int j=0;j<comp.length;j++){
				if(j==0){
					sb.append("HexNAc(").append(comp[0]).append(")");
				}else if(j==1){
					sb.append("Hex(").append(comp[1]).append(")");
				}else if(j==2 && comp[2]>0){
					sb.append("NeuAc(").append(comp[2]).append(")");
				}else if(j==3 && comp[3]>0){
					sb.append("Fuc(").append(comp[3]).append(")");
				}
			}
			map.put(FIELD_NAME, sb.toString());
			map.put(FIELD_PROTEINS, line[17]);
			map.put(FIELD_SEQUENCE, line[12]);
			File f1 = new File(spectra+"\\"+i+".png");
			File f2 = new File(structure+"\\"+i+".png");
			if(f1.exists() && f2.exists()){
				Image im1 = ImageIO.read(f1);
				Image im2 = ImageIO.read(f2);
				writer.writer(map, im1, im2, i+1);
				System.out.println(i);
			}
			i++;
		}
		writer.close();
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		/*		MatchSpecPdfWriter writer = new MatchSpecPdfWriter("H:\\glyco\\" +
				"SILAC\\20111123_HILIC_SILAC_HCD.pdf");
		
		String file = "H:\\glyco\\SILAC\\20111123_HILIC_SILAC_HCD.pxml";
		GlycoLabelFeaturesXMLReader reader = new GlycoLabelFeaturesXMLReader(file);
		reader.readAllPairs();
		PeptidePair [] feas = reader.getAllSelectedPairs();
		for(int i=0;i<feas.length;i++){
			GlycoPeptideLabelPair gp = (GlycoPeptideLabelPair) feas[i];
//			System.out.println(((IGlycoPeptide) gp.getPeptide()).getDeleStructure().getName());
		}
		

		String file = "I:\\glyco\\label-free\\20111123_HILIC_1105_HCD_111124034738.pxml";
		GlycoLFFeasXMLReader reader = new GlycoLFFeasXMLReader(file);
		PeptidePair [] pairs = reader.getAllSelectedPairs();
		for(int i=0;i<pairs.length;i++){
			GlycoPepLabelPair gp = (GlycoPepLabelPair) pairs[i];
			writer.write((IGlycoPeptide) gp.getPeptide());
		}
*/		
//		writer.close();
		
		MatchSpecPdfWriter.combine("D:\\P\\n-glyco\\3014.01.03.unknown\\Supplementry Dataset.xls", 
				"D:\\P\\n-glyco\\3014.01.03.unknown\\glyco_structure", "D:\\P\\n-glyco\\3014.01.03.unknown\\glyco_spectra", 
				"D:\\P\\n-glyco\\3014.01.03.unknown\\Supplementry spectra 2.pdf");

	}

}
