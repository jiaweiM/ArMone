/* 
 ******************************************************************************
 * File: GlycoIdenSpecHtmlWriter.java * * * Created on 2012-5-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Iden;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoSpecMatchDataset;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoStrucDrawer;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * @author ck
 *
 * @version 2012-5-23, 09:20:13
 */
public class GlycoIdenSpecHtmlWriter {
	
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
	
	public GlycoIdenSpecHtmlWriter(String output) throws IOException{
		
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
		list.add("Scannum");
		list.add("Peptide Mass");
		list.add("Glycan Mass");
		list.add("Name");
		list.add("Score");
		list.add("Rank");
		list.add("RT");
		
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

	public void write(NGlycoSSM ssm) throws IOException{
		
		int scannum = ssm.getScanNum();
		GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(scannum);
		dataset.createDataset(ssm);
		
		BufferedImage structure = this.drawer.draw(ssm.getGlycoTree());
		
		BufferedImage spectrum = JFChartDrawer.createXYBarChart(dataset)
			.createBufferedImage(width, height);
		
		StringBuilder sb = new StringBuilder();
		ArrayList <String> infolist = new ArrayList <String>();
		
		infolist.add(String.valueOf(ssm.getScanNum()));
		infolist.add(String.valueOf(ssm.getPepMass()));
		infolist.add(String.valueOf(ssm.getGlycoMass()));
		infolist.add(ssm.getName());
		infolist.add(String.valueOf(ssm.getScore()));
		infolist.add(String.valueOf(ssm.getRank()));
		infolist.add(String.valueOf(ssm.getRT()));
		
		String [] infos = new String [infolist.size()];
		infos = infolist.toArray(infos);
		
		String strucname = this.getStrucImgName(ssm) + ".png";
		
		String specname = this.getSpecImgName(ssm) + ".png";

		ImageIO.write(structure, "png", new File(this.imageDir, strucname));
		
		ImageIO.write(spectrum, "png", new File(this.imageDir, specname));
		
		for (int j = 0; j < infos.length; j++) {
			
			if(j==0){
				
				sb.append("\t\t\t<td><a href=").append(
				        this.relativeImgDir + specname).append(" target=\"_blank\"")
				        .append('>').append(infos[j]).append("</a></td>").append(
				        		lineSeparator);
				
			}else if(j==3){
				
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
	
	
	/**
	 * @param ssm
	 * @return
	 */
	private String getSpecImgName(NGlycoSSM ssm) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(ssm.getScanNum()).append("_");
		sb.append(ssm.getRank()).append("_");
		sb.append("spec");
		return sb.toString();
	}

	/**
	 * @param ssm
	 * @return
	 */
	private String getStrucImgName(NGlycoSSM ssm) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(ssm.getScanNum()).append("_");
		sb.append(ssm.getRank()).append("_");
		sb.append("struc");
		return sb.toString();
	}

	public void close() {
		this.pw.println("\t</table>");
		this.pw.println("</body>");
		this.pw.println("</html>");
		this.pw.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
