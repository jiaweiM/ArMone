/* 
 ******************************************************************************
 * File: MatchSpecPdfStamper.java * * * Created on 2012-5-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.drawjf;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;
import cn.ac.dicp.gp1809.util.ioUtil.pdf.PdfUtilities;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * @author ck
 *
 * @version 2012-5-21, 13:38:23
 */
public class MatchSpecPdfStamper {

	private String pdftemplate;
	private File tmpout;
	private File output;
	private boolean append;
	private FileOutputStream fos;
	private Document document;
	private PdfCopy copy;
	private PdfStamper stamper;
	
	public MatchSpecPdfStamper(String pdftemplate, String output, boolean append) throws IOException, DocumentException{
		
		this.pdftemplate = pdftemplate;

		this.output = new File(output);
		this.tmpout = new File(output + ".tmp");
		this.append = append;

		document = new Document(PageSize.A4);
		fos = new FileOutputStream(tmpout);
		this.copy = new PdfCopy(document, fos);

		document.open();
	}
	
	private void fillTemplateFilelds(AcroFields form,
	        HashMap<String, String> map) throws IOException, DocumentException {
		
		if (map != null && map.size() > 0) {
			for (Iterator<Entry<String, String>> iterator = map.entrySet()
			        .iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();
				String name = entry.getKey();
				form.setField(name, entry.getValue());
				form.setFieldProperty(name, "fflag", PdfFormField.FF_READ_ONLY, null);
			}
		}
		
	}
	
	public void stamp(HashMap<String, String> valueMap, Image structure, Image specturm, int index)
    	throws DocumentException, IOException {
		
		File temp = FileUtil.getTempFile();
		FileOutputStream os = new FileOutputStream(temp);
		PdfReader reader = new PdfReader(pdftemplate);
		this.stamper = new PdfStamper(reader, os);
		
		AcroFields form = stamper.getAcroFields();
		this.fillTemplateFilelds(form, valueMap);
		stamper.setFormFlattening(true);
		
		PdfContentByte content = stamper.getOverContent(1);
		com.itextpdf.text.Image pdfImage1 = com.itextpdf.text.Image.getInstance(content, structure, 0.8f);
		com.itextpdf.text.Image pdfImage2 = com.itextpdf.text.Image.getInstance(content, specturm, 0.8f);
		pdfImage1.setAbsolutePosition(90, 320);
		pdfImage1.scaleAbsolute(360, 288);
		pdfImage2.setAbsolutePosition(90, 50);
		pdfImage2.scaleAbsolute(360, 288);
		content.addImage(pdfImage1);
		content.addImage(pdfImage2);
		
		reader.close();
		stamper.close();
		os.close();

		/*
		 * Copy to the final file
		 */

		PdfReader tempreader = new PdfReader(temp.getAbsolutePath());
		int n = tempreader.getNumberOfPages();
		for (int j = 1; j <= n; j++) {
			document.newPage();
			document.setPageSize(tempreader.getPageSize(j));
			PdfImportedPage page = copy.getImportedPage(tempreader, j);
			copy.addPage(page);
		}
		
		tempreader.close();
	}

	public void close() throws IOException, DocumentException {
		
		this.copy.close();
		this.document.close();
		this.fos.close();

		if (append && this.output.exists()) {
			PdfUtilities.append(this.output.getAbsolutePath(),
			        new String[] { this.tmpout.getAbsolutePath() });
			
			this.tmpout.delete();
		} else {
			this.tmpout.renameTo(this.output);
		}
	}

	
}
