/* 
 ******************************************************************************
 * File: BatchDrawHtmlWriter.java * * * Created on 04-16-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.batchdraw;

import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Write the peptide to a html file with hyper links
 * 
 * @author Xinning
 * @version 0.1.1, 07-21-2009, 15:01:04
 */
public class BatchDrawHtmlWriter extends AbstractBatchDrawWriter implements
        IBatchDrawWriter {

	private static final String lineSeparator = IOConstant.lineSeparator;

	//The relative directory path (./filename)
	private String relativeImgDir;
	private File imageDir;
	private PrintWriter pw;

	private String paramDes;

	//The printed title
	private boolean titlePrinted;
	
//	private final static String headerTxt = "resources/html_header.txt";

	/**
	 * 
	 * 
	 * @param output
	 * @param htmHeader
	 *            header contains style and other informations
	 * @throws IOException
	 */
	public BatchDrawHtmlWriter(String output, ISearchParameter parameter, PeptideType type)
	        throws IOException {
//		this(output, parameter, Object.class
//		        .getResourceAsStream("/resources/html_header.txt"), type);
		this(output, parameter, new StringReader(headstring), type);
	}

	/**
	 * 
	 * 
	 * @param output
	 * @param htmHeader
	 *            header contains style and other informations
	 * @throws IOException
	 */
	public BatchDrawHtmlWriter(String output, ISearchParameter parameter,
	        String htmHeader, PeptideType type) throws IOException {
		this(output, parameter, new FileInputStream(htmHeader), type);
	}

	/**
	 * 
	 * 
	 * @param output
	 * @param htmHeader
	 *            header contains style and other informations
	 * @throws IOException
	 */
	public BatchDrawHtmlWriter(String output, ISearchParameter parameter,
	        InputStream headerstream, PeptideType type) throws IOException {

		super(parameter, type);
		
		//Check the extension
		String lowname = output.toLowerCase();
		if (!lowname.endsWith("html") && !lowname.endsWith("htm")) {
			output += ".html";
		}

		File out = new File(output);
		this.imageDir = new File(out.getParent(), out.getName().substring(0,
		        out.getName().lastIndexOf('.')));
		this.relativeImgDir = "./" + this.imageDir.getName() + "/";

		if (!this.imageDir.exists()) {
			this.imageDir.mkdirs();
		}

		this.paramDes = "<p>Fix modification(s): "
		        + parameter.getStaticInfo().getModfiedAADescription(true)
		        + "</p>\n<p>Variable modification(s): "
		        + parameter.getVariableInfo().getModficationDescription()
		        + "</p>";

		this.pw = new PrintWriter(out);

		this.writeHeader(headerstream);
		headerstream.close();
	}
	
	public BatchDrawHtmlWriter(String output, ISearchParameter parameter,
			StringReader sr, PeptideType type) throws IOException {

		super(parameter, type);
		
		//Check the extension
		String lowname = output.toLowerCase();
		if (!lowname.endsWith("html") && !lowname.endsWith("htm")) {
			output += ".html";
		}

		File out = new File(output);
		this.imageDir = new File(out.getParent(), out.getName().substring(0,
		        out.getName().lastIndexOf('.')));
		this.relativeImgDir = "./" + this.imageDir.getName() + "/";

		if (!this.imageDir.exists()) {
			this.imageDir.mkdirs();
		}

		this.paramDes = "<p>Fix modification(s): "
		        + parameter.getStaticInfo().getModfiedAADescription(true)
		        + "</p>\n<p>Variable modification(s): "
		        + parameter.getVariableInfo().getModficationDescription()
		        + "</p>";

		this.pw = new PrintWriter(out);

		this.writeHeader(sr);
		sr.close();
	}

	/**
	 * Write the header
	 * 
	 * @throws IOException
	 */
	private void writeHeader(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(
		        new InputStreamReader(stream));

		String line;
		while ((line = reader.readLine()) != null) {
			this.pw.println(line);
		}

		this.pw.println("<body>");
		this.pw
		        .println("\t<table id=\"rounded-corner\" summary=\"Peptide list and match information\">");

	}
	
	private void writeHeader(StringReader sr) throws IOException {
		BufferedReader reader = new BufferedReader(sr);

		String line;
		while ((line = reader.readLine()) != null) {
			this.pw.println(line);
		}

		this.pw.println("<body>");
		this.pw
		        .println("\t<table id=\"rounded-corner\" summary=\"Peptide list and match information\">");

	}

	private void printTitle(String[] titles) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t\t<tr>").append(lineSeparator);
		for (int i = 1; i < titles.length; i++) {
			sb.append("\t\t\t<th>").append(titles[i]).append("</th>").append(
			        lineSeparator);
		}
		sb.append("\t\t</tr>").append(lineSeparator);

		sb.append("\t\t<tfoot>").append(lineSeparator).append("\t\t\t<tr>")
		        .append(lineSeparator).append("\t\t\t\t<td colspan=\"").append(
		                titles.length - 1).append("\"><em>");

		sb.append(this.paramDes);

		sb.append("</em></td>").append(lineSeparator).append("</tr>").append(
		        lineSeparator).append("\t\t</tfoot>").append(lineSeparator);

		this.pw.print(sb.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter#write(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide,
	 * cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold,
	 * cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo[])
	 */
	@Override
	public void write(IPeptide peptide, IMS2PeakList[] peaklists, int[] types,
	        ISpectrumThreshold threshold, NeutralLossInfo[] losses)
	        throws IOException {

		this.validate(peptide);
		
		BufferedImage image = this.createImage(peptide, peaklists, types, threshold, losses);

		if (!this.titlePrinted) {
			this.printTitle(peptide.getPeptideFormat().getTitle());
			this.titlePrinted = true;
		}

		String[] strs = StringUtil.split(peptide.toString(), '\t');
		StringBuilder sb = new StringBuilder();

		// put p before S/T/Y
		strs[2] = putPBefore(peptide.getSequence());
		
		sb.append("\t\t<tr>").append(lineSeparator);

		if (image != null) {
			String imgname = this.getImgName(peptide) + ".png";
			//The scan number with hyper link
			sb.append("\t\t\t<td><a href=").append(
			        this.relativeImgDir + imgname).append(" target=\"_blank\"")
			        .append('>').append(strs[1]).append("</a></td>").append(
			                lineSeparator);

			ImageIO.write(image, "png", new File(this.imageDir, imgname));

		} else {
			sb.append("\t\t\t<td>").append(strs[1]).append("</td>").append(
			        lineSeparator);
		}

		for (int i = 2; i < strs.length; i++) {
			sb.append("\t\t\t<td>").append(strs[i]).append("</td>").append(
			        lineSeparator);
		}
		sb.append("\t\t</tr>").append(lineSeparator);
		this.pw.print(sb.toString());
	}

	/**
	 * The name of the image for this peptide
	 * 
	 * @param pep
	 * @return
	 */
	private String getImgName(IPeptide pep) {
		String baseName = pep.getBaseName();
		int scanBeg = pep.getScanNumBeg();
		short charge = pep.getCharge();

		StringBuilder sb = new StringBuilder();

		if (baseName != null) {
			sb.append(baseName).append('_');
		}

		sb.append(scanBeg).append('_').append(charge);

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter#close()
	 */
	@Override
	public void close() {
		this.pw.println("\t</table>");
		this.pw.println("</body>");
		this.pw.println("</html>");
		this.pw.close();
	}

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
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public static void main(String[] args) throws FileDamageException,
	        IOException {

		PeptideListReader reader = new PeptideListReader("E:\\Data\\srf\\" +
			"8h1_50mM.xml.ppl");

		BatchDrawHtmlWriter writer = new BatchDrawHtmlWriter(
				"E:\\Data\\srf\\" +
				"drawer.html", reader.getSearchParameter(), reader.getPeptideType());
		
		int[] types = new int[] {Ion.TYPE_B, Ion.TYPE_Y};
		
		IPeptide pep;
		int count=0;
		while ((pep = reader.getPeptide()) != null) {
			if(count++ > 10)
				break;
			writer.write(pep,reader.getPeakLists(),types);
		}

		writer.close();
		reader.close();
	}
}
