/* 
 ******************************************************************************
 * File: PdfUtilities.java * * * Created on 05-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

/**
 * Utilities for itext pdf operation
 * 
 * @author Xinning
 * @version 0.1, 05-18-2009, 13:39:59
 */
public class PdfUtilities {

	/**
	 * merge the files to a new pdf file one by one
	 * 
	 * @param pdffiles
	 * @param to
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void merge2(String[] pdffiles, String to) throws IOException,
	        DocumentException {

		Document document = new Document();
		FileOutputStream fos = new FileOutputStream(to);
		PdfCopy copy = new PdfCopy(document, fos);
		document.open();
		
		/*
		 * Copy to the final file
		 */

		for (String pdffile : pdffiles) {
			PdfReader tempreader = new PdfReader(pdffile);
			int n = tempreader.getNumberOfPages();
			for (int j = 1; j <= n; j++) {
				document.newPage();
				document.setPageSize(tempreader.getPageSize(j));
				PdfImportedPage page = copy.getImportedPage(tempreader, j);
				copy.addPage(page);
			}

			tempreader.close();
		}

		copy.close();
		document.close();
		fos.close();
	}

	/**
	 * Append the files to the end one by one.
	 * 
	 * @param file
	 * @param toAppends
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void append(String file, String[] toAppends)
	        throws IOException, DocumentException {
		String tmpto = FileUtil.getTempFile().getAbsolutePath();

		String[] files = new String[toAppends.length + 1];
		System.arraycopy(toAppends, 0, files, 1, toAppends.length);

		files[0] = file;

		merge2(files, tmpto);
		
		File to = new File(file);
		to.delete();
		
		new File(tmpto).renameTo(to);
	}
	
	public static void main(String args[]) throws IOException, DocumentException {
		append("d:\\writer1.pdf", new String[] {"d:\\writer2.pdf"});
	}
}
