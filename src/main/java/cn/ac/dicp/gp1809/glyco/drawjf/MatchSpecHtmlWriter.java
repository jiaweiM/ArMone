/* 
 ******************************************************************************
 * File: MatchSpecHtmlWriter.java * * * Created on 2011-12-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.drawjf;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoLabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoPeptideLabelPair;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * @author ck
 *
 * @version 2011-12-28, 15:27:55
 */
public class MatchSpecHtmlWriter {

	private static final String headstring = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
	"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
	"<head>" +
	"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
	"<title>Peptide list</title>\n" +
	"<style type=\"text/css\">\n" +
	/*
	* Change the css of table type
	*/

	"#rounded-corner\n" +
	"{\n"+
		"font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", Sans-Serif;\t" +
		"font-size: 12px;\n" +
		"margin: 45px;\n" +
		"width: 480px;\n" +
		"text-align: left;\n" +
		"border-collapse: collapse;\n" +
	"}\n" +
	"#rounded-corner thead th.rounded-company\n" +
	"{\n" +
		"background: #b9c9fe url('table-images/left.png') left -1px no-repeat;\n" +
	"}\n" +
	"#rounded-corner thead th.rounded-q4\n" +
	"{\n" +
		"background: #b9c9fe url('table-images/right.png') right -1px no-repeat;\n" +
	"}\n" +
	"#rounded-corner th\n" +
	"{\n" +
		"padding: 8px;\n" +
		"font-weight: normal;\n" +
		"font-size: 13px;\n" +
		"color: #039;\n" +
		"background: #b9c9fe;\n" +
	"}\n" +
	"#rounded-corner td\n" +
	"{\n" +
		"padding: 8px;\n" +
		"background: #e8edff;\n" +
		"border-top: 1px solid #fff;\n" +
		"color: #669;\n" +
	"}\n" +
	"#rounded-corner tfoot td.rounded-foot-left\n" +
	"{\n" +
		"background: #b9c9fe left bottom no-repeat;\n" +
	"}\n" +

	"#rounded-corner tbody tr:hover td\n" +
	"{\n" +
		"background: #d0dafd;\n" +
	"}\n" +

	"</style>\n" +
	"</head>";
	
	private static final String lineSeparator = IOConstant.lineSeparator;
	/**
	 * The width of the spectrum
	 */
	public static int width = 900;
	/**
	 * The height of the spectrum
	 */
	public static int height = 600;

	//The relative directory path (./filename)
	private String relativeImgDir;
	private File imageDir;
	private PrintWriter pw;
	private GlycoStrucDrawer drawer;

	public MatchSpecHtmlWriter(String output) throws IOException{
		
		File out = new File(output);
		this.imageDir = new File(out.getParent(), out.getName().substring(0,
		        out.getName().lastIndexOf('.')));
		this.relativeImgDir = "./" + this.imageDir.getName() + "/";

		if (!this.imageDir.exists()) {
			this.imageDir.mkdirs();
		}
		
		this.drawer = new GlycoStrucDrawer();
		this.pw = new PrintWriter(out);
		this.printTitle();
	}
	
	private void printTitle() throws IOException {
		
		StringReader headerstream = new StringReader(headstring);
		BufferedReader reader = new BufferedReader(
		        new BufferedReader(headerstream));

		String line;
		while ((line = reader.readLine()) != null) {
			this.pw.println(line);
		}

		this.pw.println("<body>");
		this.pw
		        .println("\t<table id=\"rounded-corner\" summary=\"Peptide list and match information\">");
		
		headerstream.close();

		ArrayList <String> list = new ArrayList <String>();
		list.add("PepScan");
		list.add("Sequence");
		list.add("Peptide Mass");
		list.add("Glycan Mass");
		list.add("Protein");
		list.add("GlycoScan");
		list.add("Name");
		list.add("Score");
		list.add("Rank");
		
		String [] titles = list.toArray(new String[list.size()]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("\t\t<tr>").append(lineSeparator);
		for (int i = 0; i < titles.length; i++) {
			sb.append("\t\t\t<th>").append(titles[i]).append("</th>").append(
			        lineSeparator);
		}
		sb.append("\t\t</tr>").append(lineSeparator);

		sb.append("\t\t<tfoot>").append(lineSeparator).append("\t\t\t<tr>")
		        .append(lineSeparator).append("\t\t\t\t<td colspan=\"").append(
		                titles.length - 1).append("\"><em>");

		sb.append("</em></td>").append(lineSeparator).append("</tr>").append(
		        lineSeparator).append("\t\t</tfoot>").append(lineSeparator);

		this.pw.print(sb.toString());
	}

	public void write(NGlycoSSM ssm, IGlycoPeptide peptide) throws IOException{

		GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(peptide.getScanNumBeg()+"_"+ssm.getScanNum());
		dataset.createDataset(ssm);
		
		BufferedImage structure = this.drawer.draw(ssm.getGlycoTree());
		
		BufferedImage spectrum = JFChartDrawer.createXYBarChart(dataset)
			.createBufferedImage(width, height);
		
		StringBuilder sb = new StringBuilder();
		String [] infos = getInfo(peptide, ssm);
		
		String strucname = this.getStrucImgName(peptide, ssm) + ".png";
		
		String specname = this.getSpecImgName(peptide, ssm) + ".png";
		//The scan number with hyper link
/*				sb.append("\t\t\t<td><a href=").append(
		        this.relativeImgDir + specname).append(" target=\"_blank\"")
		        .append('>').append(infos[0]).append("</a></td>").append(
		        		lineSeparator);
*/
		ImageIO.write(structure, "png", new File(this.imageDir, strucname));
		
		ImageIO.write(spectrum, "png", new File(this.imageDir, specname));
		
		for (int j = 0; j < infos.length; j++) {
			
			if(j==5){
				
				sb.append("\t\t\t<td><a href=").append(
				        this.relativeImgDir + specname).append(" target=\"_blank\"")
				        .append('>').append(infos[j]).append("</a></td>").append(
				        		lineSeparator);
				
			}else if(j==6){
				
				sb.append("\t\t\t<td><a href=").append(
				        this.relativeImgDir + strucname).append(" target=\"_blank\"")
				        .append('>').append(infos[j]).append("</a></td>").append(
				        		lineSeparator);
			}else{
				
				sb.append("\t\t\t<td>").append(infos[j]).append("</td>").append(
						lineSeparator);
				
			}
		}
		
		sb.append("\t\t</tr>").append(lineSeparator);
		this.pw.print(sb.toString());
		
	}

	private String getStrucImgName(IGlycoPeptide peptide, NGlycoSSM ssm){
		
		StringBuilder sb = new StringBuilder();
		sb.append(peptide.getScanNumBeg()).append("_");
		sb.append(ssm.getScanNum()).append("_");
		sb.append(ssm.getRank()).append("_");
		sb.append("struc");
		return sb.toString();
	}
	
	private String getSpecImgName(IGlycoPeptide peptide, NGlycoSSM ssm){
		
		StringBuilder sb = new StringBuilder();
		sb.append(peptide.getScanNumBeg()).append("_");
		sb.append(ssm.getScanNum()).append("_");
		sb.append(ssm.getRank()).append("_");
		sb.append("spec");
		return sb.toString();
	} 
	
	private String [] getInfo(IGlycoPeptide peptide, NGlycoSSM ssm){
		
		ArrayList <String> list = new ArrayList <String>();
		
		list.add(String.valueOf(peptide.getScanNumBeg()));
		list.add(peptide.getSequence());
		list.add(String.valueOf(peptide.getPepMrNoGlyco()));
		list.add(String.valueOf(peptide.getGlycoMass()));
		list.add(peptide.getDelegateReference());
		list.add(String.valueOf(ssm.getScanNum()));
		list.add(ssm.getName());
		list.add(String.valueOf(ssm.getScore()));
		list.add(String.valueOf(ssm.getRank()));

		return list.toArray(new String [list.size()]);
	}
	
	public void close() {
		this.pw.println("\t</table>");
		this.pw.println("</body>");
		this.pw.println("</html>");
		this.pw.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws IOException, DocumentException {
		// TODO Auto-generated method stub

		MatchSpecHtmlWriter writer = new MatchSpecHtmlWriter("H:\\glyco\\SILAC\\20111123_HILIC_SILAC_HCD.html");
		String file = "H:\\glyco\\SILAC\\20111123_HILIC_SILAC_HCD.pxml";
		GlycoLabelFeaturesXMLReader reader = new GlycoLabelFeaturesXMLReader(file);

		writer.close();
	}

}
