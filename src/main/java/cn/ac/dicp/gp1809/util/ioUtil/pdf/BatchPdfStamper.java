/* 
 ******************************************************************************
 * File: PdfStamperUtility.java * * * Created on 05-15-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.pdf;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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

import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;

/**
 * Write the template to the output stream for multiple times.
 * 
 * @author Xinning
 * @version 0.1, 05-15-2009, 14:21:16
 */
public class BatchPdfStamper {

	/**
	 * The class loader
	 */
	private final static ClassLoader LOADER = BatchPdfStamper.class.getClassLoader();
	
	private String pdftemplate;
	private File tmpout;
	private File output;
	private boolean append;
	private FileOutputStream fos;
	private Document document;
	private PdfCopy copy;
	private PdfStamper stamper;

	private HashMap<String, String> valueMap;
	private Image image;

	/**
	 * If the append is TURE and the output file is not null, the stamped pages
	 * will be appended to the end of the output file, otherwise, overwrite
	 * 
	 * @param pdftemplate
	 * @param output
	 * @param append
	 *            if append to the original pdf file.
	 * @throws IOException
	 * @throws DocumentException
	 */
	public BatchPdfStamper(String pdftemplate, String output, boolean append)
	        throws IOException, DocumentException {
		this.pdftemplate = pdftemplate;

		this.output = new File(output);
		this.tmpout = new File(output + ".tmp");
		this.append = append;

		
		document = new Document(PageSize.B5);
		fos = new FileOutputStream(tmpout);
		this.copy = new PdfCopy(document, fos);

		document.open();
	}
	
	/**
	 * Fill the fields in the template for writing. If no fields need to be
	 * filled, just do nothing.
	 * 
	 * @param form
	 * @throws DocumentException
	 * @throws IOException
	 */
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

	/**
	 * Append the template to the end of the output stream again and again. The
	 * fields will be flattened when finish.
	 * 
	 * @param valueMap
	 *            the key and value map for the stamper
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void stamp(HashMap<String, String> valueMap, Image image, int index)
	        throws DocumentException, IOException {

		if(index%2==1){
			this.valueMap = valueMap;
			this.image = image;
/*			
			temp = FileUtil.getTempFile();
			FileOutputStream os = new FileOutputStream(temp);
			PdfReader reader = new PdfReader(pdftemplate);
			this.stamper = new PdfStamper(reader, os);
			
			AcroFields form = stamper.getAcroFields();	
			this.fillTemplateFilelds(form, valueMap);
			stamper.setFormFlattening(true);

			PdfContentByte content = stamper.getOverContent(1);
			com.lowagie.text.Image pdfImage = com.lowagie.text.Image.getInstance(content, image, 0.8f);
			pdfImage.setAbsolutePosition(60, 450);
			pdfImage.scaleAbsolute(400, 240);
			content.addImage(pdfImage);
			
			reader.close();
			stamper.close();
			os.close();
*/			
		}else{
			if(this.valueMap==null || this.image==null)
				throw new IOException();
				
			File temp = FileUtil.getTempFile();
			FileOutputStream os = new FileOutputStream(temp);
			PdfReader reader = new PdfReader(pdftemplate);
			this.stamper = new PdfStamper(reader, os);
			
			HashMap <String, String> map2 = new HashMap<String, String>(valueMap.size());
			Iterator <String> it = valueMap.keySet().iterator();
			while(it.hasNext()){
				String name = it.next();
				map2.put(name+"_2", valueMap.get(name));
			}
			
			AcroFields form = stamper.getAcroFields();
			this.fillTemplateFilelds(form, this.valueMap);
			this.fillTemplateFilelds(form, map2);
			stamper.setFormFlattening(true);

			PdfContentByte content = stamper.getOverContent(1);
			com.itextpdf.text.Image pdfImage1 = com.itextpdf.text.Image.getInstance(content, this.image, 0.8f);
			com.itextpdf.text.Image pdfImage2 = com.itextpdf.text.Image.getInstance(content, image, 0.8f);
			pdfImage1.setAbsolutePosition(90, 450);
			pdfImage1.scaleAbsolute(304, 228);		
			pdfImage2.setAbsolutePosition(90, 90);
			pdfImage2.scaleAbsolute(304, 228);
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
			this.valueMap = null;
			this.image = null;
		}
	}

	public void stamp(HashMap<String, String> valueMap, Image image)
		throws DocumentException, IOException {
		
		File temp = FileUtil.getTempFile();
		FileOutputStream os = new FileOutputStream(temp);
		PdfReader reader = new PdfReader(pdftemplate);
		this.stamper = new PdfStamper(reader, os);

		AcroFields form = stamper.getAcroFields();
		this.fillTemplateFilelds(form, valueMap);
		stamper.setFormFlattening(true);

		PdfContentByte content = stamper.getOverContent(1);
		com.itextpdf.text.Image pdfImage1 = com.itextpdf.text.Image.getInstance(content, image, 1f);
		pdfImage1.setAbsolutePosition(90, 390);
		pdfImage1.scaleAbsolute(280, 210);		
		content.addImage(pdfImage1);
		
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
	/**
	 * Close and finish the stamping
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void close() throws IOException, DocumentException {

		if(this.valueMap!=null && this.image!=null){
			File temp = FileUtil.getTempFile();
			FileOutputStream os = new FileOutputStream(temp);
			PdfReader reader = new PdfReader(pdftemplate);
			this.stamper = new PdfStamper(reader, os);
			
			AcroFields form = stamper.getAcroFields();
			this.fillTemplateFilelds(form, this.valueMap);
			stamper.setFormFlattening(true);
			
			PdfContentByte content = stamper.getOverContent(1);
			com.itextpdf.text.Image pdfImage1 = com.itextpdf.text.Image.getInstance(content, this.image, 0.8f);
			pdfImage1.setAbsolutePosition(90, 450);
			pdfImage1.scaleAbsolute(304, 228);		
			content.addImage(pdfImage1);
			
			reader.close();
			stamper.close();
			os.close();
			
			PdfReader tempreader = new PdfReader(temp.getAbsolutePath());
			int n = tempreader.getNumberOfPages();
			for (int j = 1; j <= n; j++) {
				document.newPage();
				document.setPageSize(tempreader.getPageSize(j));
				PdfImportedPage page = copy.getImportedPage(tempreader, j);
				copy.addPage(page);
			}
			
			tempreader.close();
			this.valueMap = null;
			this.image = null;
		}
		
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
	
	public static void main(String [] args) throws IOException, DocumentException{
		String pdf = "E:\\CK\\workspace\\ArCommon\\src\\resources\\SEQUESTTemplate2.pdf";
		String out = "E:\\CK\\workspace\\ArCommon\\src\\resources\\test.pdf";
		PdfReader preader = new PdfReader(pdf);
		PdfStamper stamper = new PdfStamper(preader,new FileOutputStream(out));
		PdfContentByte under = stamper.getUnderContent(1);
		AcroFields form = stamper.getAcroFields();
		Map map = form.getFields();
		System.out.println(map.size());
	}
	
}
