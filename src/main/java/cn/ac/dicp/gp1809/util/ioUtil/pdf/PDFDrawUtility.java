/*
 ******************************************************************************
 * File: PDFDrawUtility.java * * * Created on 02-29-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.pdf;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfree.chart.JFreeChart;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Utilities used for the create a pdf file (mainly for spectrum drawer).
 * 
 * @author Xinning
 * @version 0.2, 04-30-2009, 10:38:43
 */
public class PDFDrawUtility {

	private static final String lineSeparator = IOConstant.lineSeparator;
	
	
	/**
	 * The default font used for pdf printing. If the font is not specified,
	 * this font will be used automaticlly.
	 */
	public static final com.itextpdf.text.Font DEFAULT_FONT = 
		new com.itextpdf.text.Font();
//		new com.itextpdf.text.Font(Font.BOLD, 8,
//	        Font.ITALIC, Color.red);

	/*
	 * Document object indicating this pdf file
	 */
	private Document document;

	/*
	 * The width of the page for print; equals document.right()-document.left()
	 */
	private float width;

	/**
	 * Create a pdf document using the default page size: (PageSize.A4,left
	 * margin 70, right margin 70, top margin 50,bottom margin 50)
	 * 
	 * @param outname
	 *            the pdf file name.
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public PDFDrawUtility(String outname) throws FileNotFoundException,
	        DocumentException {
		this(outname, PageSize.A4, 70, 70, 50, 50);
	}

	/**
	 * Create a pdf document using the default page size: (PageSize.A4,left
	 * margin 70, right margin 70, top margin 50,bottom margin 50)
	 * 
	 * @param outname
	 *            the pdf file name.
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public PDFDrawUtility(String outname, Rectangle pagesize, float marginLeft,
	        float marginRight, float marginTop, float marginBottom)
	        throws FileNotFoundException, DocumentException {

		this.document = new Document(pagesize, marginLeft, marginRight,
		        marginTop, marginBottom);
		PdfWriter.getInstance(document, new FileOutputStream(outname));
		document.open();

		this.width = (document.right() - document.left());
	}

	/**
	 * @return the document object indicating this pdf file
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @return the width of page for print. (only the writable region, not
	 *         include the margin)
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * Add a string to the document as a single paragraph.
	 * 
	 * @param paragraph
	 * @throws DocumentException
	 */
	public void add(String paragraph) throws DocumentException {
		this.add(paragraph, DEFAULT_FONT);
	}

	/**
	 * Add a string to the document as a single paragraph.
	 * 
	 * @param paragraph
	 * @throws DocumentException
	 */
	public void add(String paragraph, Font font) throws DocumentException {
		this.add(new Paragraph(paragraph, font));
	}

	/**
	 * Add a paragraph to the pdf file
	 * 
	 * @param para
	 * @throws DocumentException
	 */
	public void add(Paragraph para) throws DocumentException {
		document.add(para);
	}

	/**
	 * Print a blank line into the pdf document.
	 * 
	 * @throws DocumentException
	 */
	public void nextLine() throws DocumentException {
		document.add(new Paragraph(lineSeparator, DEFAULT_FONT));
	}

	/**
	 * Print the image in the JFreeChart to the PDF document.
	 * 
	 * @param chart
	 * @param ms
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void add(JFreeChart chart) throws IOException, DocumentException {
		Image image = Image.getInstance(chart.createBufferedImage(1000, 700),
		        null);
		image.scaleToFit(width, document.top());
		image.setAlignment(Image.MIDDLE);
		document.add(image);
	}
	
	/**
	 * Print the image in the JFreeChart to the PDF document.
	 * 
	 * @param chart
	 * @param ms
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void add(Image image) throws IOException, DocumentException {
		image.scaleToFit(width, document.top());
		image.setAlignment(Image.MIDDLE);
		document.add(image);
	}

	/**
	 * Print a line of cells to the PDF document. The width and value of the
	 * cell must be specified, and corresponding to each other. And the width
	 * must be optimized so that all the cells are displayed in a beautiful
	 * view.
	 * 
	 * @param cells
	 *            string value of all the cells
	 * @param width
	 *            width[] of all the cells
	 * @param Font
	 *            font
	 */
	public void add(String[] cells, int[] width, Font font)
	        throws DocumentException {
		addCellsLineToPdf(cells, width, font, document);
	}

	/**
	 * Print a line of cells to the PDF document using default font The width
	 * and value of the cell must be specified, and corresponding to each other.
	 * And the width must be optimized so that all the cells are displayed in a
	 * beautiful view.
	 * 
	 * @param cells
	 *            string value of all the cells
	 * @param width
	 *            width[] of all the cells
	 */
	public void add(String[] cells, int[] width) throws DocumentException {
		this.add(cells, width, DEFAULT_FONT);
	}

	/**
	 * Print a line of cells to the PDF document The width and value of the cell
	 * must be specified, and corresponding to each other. And the width must be
	 * optimized so that all the cells are displayed in a beautiful view.
	 * 
	 * @param cells
	 *            Paragraph value of all the cells
	 * @param width
	 *            width[] of all the cells
	 */
	public void add(Paragraph[] cells, int[] width) throws DocumentException {
		addCellsLineToPdf(cells, width, document);
	}

	/**
	 * Signals that an new page has to be started.
	 * 
	 * @return true if new page started, otherwise false.
	 */
	public boolean newPage() {
		return document.newPage();
	}

	/**
	 * Close the pdf file and save the changes.
	 */
	public void close() {
		this.document.close();
		//useful ?
		this.document = null;
	}

	/**
	 * Print a line of cells to the PDF document. The width and value of the
	 * cell must be specified, and corresponding to each other. And the width
	 * must be optimized so that all the cells are displayed in a beautiful
	 * view.
	 * 
	 * @param cells
	 *            string value of all the cells
	 * @param width
	 *            width[] of all the cells
	 * @param Font
	 *            font
	 * @param document
	 *            document object of the PDF file.
	 */
	public static void addCellsLineToPdf(String[] cells, int[] width,
	        Font font, Document document) throws DocumentException {

		if (cells == null || width == null) {
			System.out.println("Null cells inputed! Nothing will be printed.");
			return;
		}

		int num = cells.length;
		int num2 = width.length;

		if (num == 0 || num2 == 0) {
			System.out.println("Null cells inputed! Nothing will be printed.");
			return;
		}

		if (document == null) {
			throw new DocumentException("Null document exception.");
		}

		if (num != num2) {
			throw new DocumentException(
			        "Num. of cells doesn't equal num. of width exception.");
		}

		PdfPTable table = new PdfPTable(num);
		table.setWidthPercentage(100f);
		table.setWidths(width);

		for (int i = 0; i < num; i++) {
			Paragraph para = new Paragraph(cells[i], font);
			PdfPCell cell = new PdfPCell(para);
			table.addCell(cell);
		}

		document.add(table);
	}

	/**
	 * Print a line of cells to the PDF document. The width and value of the
	 * cell must be specified, and corresponding to each other. And the width
	 * must be optimized so that all the cells are displayed in a beautiful
	 * view.
	 * 
	 * @param cells
	 *            values of all the cells
	 * @param width
	 *            width[] of all the cells
	 * @param Font
	 *            font
	 * @param document
	 *            document object of the PDF file.
	 */
	public static void addCellsLineToPdf(Paragraph[] cells, int[] width,
	        Document document) throws DocumentException {

		if (cells == null || width == null) {
			System.out.println("Null cells inputed! Nothing will be printed.");
			return;
		}

		int num = cells.length;
		int num2 = width.length;

		if (num == 0 || num2 == 0) {
			System.out.println("Null cells inputed! Nothing will be printed.");
			return;
		}

		if (document == null) {
			throw new DocumentException("Null document exception.");
		}

		if (num != num2) {
			throw new DocumentException(
			        "Num. of cells doesn't equal num. of width exception.");
		}

		PdfPTable table = new PdfPTable(num);
		table.setWidthPercentage(100f);
		table.setWidths(width);

		for (int i = 0; i < num; i++) {
			Paragraph para = cells[i];
			PdfPCell cell = new PdfPCell(para);
			table.addCell(cell);
		}

		document.add(table);
	}
}
