/* 
 ******************************************************************************
 * File: OGlycanPdfStamper.java * * * Created on 2013-2-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;
import cn.ac.dicp.gp1809.util.ioUtil.pdf.PdfUtilities;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * @author ck
 *
 * @version 2013-2-28, 10:03:59
 */
public class OGlycanPdfStamper {
	
	private String pdftemplate;
	private File tmpout;
	private File output;
	private boolean append;
	private FileOutputStream fos;
	private Document document;
	private PdfCopy copy;
	private PdfStamper stamper;
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public OGlycanPdfStamper(String pdftemplate, String output, boolean append) 
			throws DocumentException, IOException{

		this.pdftemplate = pdftemplate;

		this.output = new File(output);
		this.tmpout = new File(output + ".tmp");
		this.append = append;

		document = new Document(PageSize.A4);
		fos = new FileOutputStream(tmpout);
		this.copy = new PdfCopy(document, fos);

		document.open();
	
	}
	
/*	private void fillTemplateFilelds(AcroFields form,
	        HashMap<String, String> map) throws IOException, DocumentException {
		
		if (map != null && map.size() > 0) {
			for (Iterator<Entry<String, String>> iterator = map.entrySet()
			        .iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();
				String name = entry.getKey();
				form.setFieldProperty(name, "textcolor", BaseColor.BLUE, null);
				form.setFieldProperty(name, "textfont ", BaseFont.DESCENT, null);
				form.setField(name, entry.getValue());
			}
		}
		
	}
*/
	private void fillTemplateFilelds(AcroFields form,
	        HashMap<String, String> map, HashMap <Double, String> matchMap) throws IOException, DocumentException {
		
		Iterator <String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String value = map.get(key);
			if(matchMap.containsValue(value)){
				form.setFieldProperty(key, "textcolor", BaseColor.RED, null);
				form.setField(key, value);
			}else{
				form.setField(key, value);
			}
		}
	}
	
	private void fillTemplateFilelds(AcroFields form, OGlycanPepInfo opInfo, int idx) throws IOException, DocumentException {
		
		StringBuilder masssb = new StringBuilder();
		StringBuilder namesb = new StringBuilder();
		OGlycanSiteInfo [] sites = opInfo.getSiteInfo();
		for(int i=0;i<sites.length;i++){
			masssb.append(df4.format(sites[i].getGlycoMass())).append(";");
			namesb.append(sites[i].getGlycoName()).append("\n");
		}
		masssb.deleteCharAt(masssb.length()-1);
		namesb.deleteCharAt(namesb.length()-1);
		
		form.setField("", String.valueOf(idx));
		form.setField("Scan", String.valueOf(opInfo.getScanname()));
		form.setField("Peptide Mass", df4.format(opInfo.getPeptide().getMH()-AminoAcidProperty.PROTON_W));
		form.setField("Glycan Mass", masssb.toString());
		form.setField("Protein", opInfo.getDeleRef());
		form.setField("Sequence", opInfo.getModseq());
		
		HashMap <String, String> annomap1 = opInfo.getAnnotionMap1();
		HashMap <String, String> annomap2 = opInfo.getAnnotionMap2();
		HashMap<String, Boolean> annomap3 = opInfo.getAnnotionMap3();

		Iterator <String> it1 = annomap1.keySet().iterator();
		while(it1.hasNext()){
			String key = it1.next();
			String value = annomap1.get(key);
			if(annomap3.containsKey(key)){
				if(key.contains("glycan")){
					form.setFieldProperty(key, "textcolor", BaseColor.BLUE, null);
				}else{
					form.setFieldProperty(key, "textcolor", BaseColor.RED, null);
				}
				form.setField(key, value);
			}else{
				form.setField(key, value);
			}
		}
		Iterator <String> it2 = annomap2.keySet().iterator();
		while(it2.hasNext()){
			String key = it2.next();
			String value = annomap2.get(key);
			if(annomap3.containsKey(key)){
				if(key.endsWith("_2") || key.endsWith("_4")){
					form.setFieldProperty(key, "textcolor", BaseColor.BLUE, null);
				}else{
					form.setFieldProperty(key, "textcolor", BaseColor.RED, null);
				}
				form.setField(key, String.valueOf(value));
			}else{
				form.setField(key, String.valueOf(value));
			}
		}
	}
	
/*	public void stamp(HashMap<String, String> valueMap, Image specturm, int index)
	    throws DocumentException, IOException {

		File temp = FileUtil.getTempFile();
		FileOutputStream os = new FileOutputStream(temp);
		PdfReader reader = new PdfReader(pdftemplate);
		this.stamper = new PdfStamper(reader, os);
		
		AcroFields form = stamper.getAcroFields();
		this.fillTemplateFilelds(form, valueMap);
		stamper.setFormFlattening(true);
		
		PdfContentByte content = stamper.getOverContent(1);
		com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(content, specturm, 0.8f);
		pdfImage.setAbsolutePosition(90, 460);
		pdfImage.scaleAbsolute(360, 240);
		content.addImage(pdfImage);
		
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

	}
*/	
	public void stamp(HashMap<String, String> valueMap, HashMap <Double, String> matchMap, Image specturm, int index)
		throws DocumentException, IOException {

		File temp = FileUtil.getTempFile();
		FileOutputStream os = new FileOutputStream(temp);
		PdfReader reader = new PdfReader(pdftemplate);
		this.stamper = new PdfStamper(reader, os);
		
		AcroFields form = stamper.getAcroFields();
		this.fillTemplateFilelds(form, valueMap, matchMap);
		stamper.setFormFlattening(true);
		
		PdfContentByte content = stamper.getOverContent(1);
		com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(content, specturm, 0.8f);
		pdfImage.setAbsolutePosition(90, 460);
		pdfImage.scaleAbsolute(360, 240);
		content.addImage(pdfImage);
		
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
	
	public void stamp(OGlycanPepInfo opInfo, Image specturm, int index)
			throws DocumentException, IOException {

		File temp = FileUtil.getTempFile();
		FileOutputStream os = new FileOutputStream(temp);
		PdfReader reader = new PdfReader(pdftemplate);
		this.stamper = new PdfStamper(reader, os);
		
		AcroFields form = stamper.getAcroFields();
		this.fillTemplateFilelds(form, opInfo, index);
		stamper.setFormFlattening(true);
		
		PdfContentByte content = stamper.getOverContent(1);
		com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(content, specturm, 0.8f);
		pdfImage.setAbsolutePosition(90, 460);
		pdfImage.scaleAbsolute(360, 240);
		content.addImage(pdfImage);
		
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
