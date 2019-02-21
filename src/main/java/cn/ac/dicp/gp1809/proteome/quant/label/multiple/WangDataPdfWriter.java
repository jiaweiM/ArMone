/* 
 ******************************************************************************
 * File: WangDataPdfWriter.java * * * Created on 2012-10-8
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.itextpdf.text.DocumentException;

import jxl.JXLException;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.pdf.BatchPdfStamper;

/**
 * @author ck
 *
 * @version 2012-10-8, 21:23:35
 */
public class WangDataPdfWriter {
	
	private BatchPdfStamper pdf;
	
	public WangDataPdfWriter(String templete, String output) throws IOException, DocumentException{
		pdf = new BatchPdfStamper(templete, output, true);
	}
	
	public void writePro(String file, String images) throws IOException, JXLException, DocumentException{
		
		ExcelReader reader = new ExcelReader(file);
		String [] line = reader.readLine();
		int count = 0;
		while((line=reader.readLine())!=null){
			count++;
			String ref = line[0];
			String kloss = line[16];
			String half = line[18];
			BufferedImage image = ImageIO.read(new File(images+"\\"+count+".png"));
			this.writePro(ref, kloss, half, image);
		}
	}
	
	public void writePro(String name, String kloss, String half, BufferedImage image) 
			throws DocumentException, IOException{
		
		HashMap <String, String> map = new HashMap <String, String>();
//		map.put("Accession", ipi);
		map.put("Protein", name);
		map.put("Kloss", kloss);
		map.put("Halflife", half);
		this.pdf.stamp(map, image);
	}

	public void writePep(String file, String images) throws IOException, JXLException, DocumentException{
		
		HashMap <String, String[]> contentmap = new HashMap <String, String[]>();
		HashMap <String, ArrayList <String>> refmap = new HashMap <String, ArrayList <String>>();
		
		ExcelReader reader = new ExcelReader(file, new int[]{0, 1});
		String [] line1 = reader.readLine(1);
		int count = 0;
		while((line1=reader.readLine(1))!=null){
			count++;
			String ref = line1[1];
			String seq = line1[0];
			String kloss = line1[11];
			String half = line1[13];

			contentmap.put(seq, new String[]{ref, kloss, half, images+"\\"+count+".png"});
			String [] refs = ref.split(";");
			for(int i=0;i<refs.length;i++){
				if(refmap.containsKey(refs[i])){
					refmap.get(refs[i]).add(seq);
				}else{
					ArrayList <String> list = new ArrayList <String>();
					list.add(seq);
					refmap.put(refs[i], list);
				}
			}
		}
		System.out.println(contentmap.size());
		
		String [] line0 = reader.readLine(0);
		while((line0=reader.readLine(0))!=null){
			if(refmap.containsKey(line0[0])){
				ArrayList <String> list = refmap.get(line0[0]);
				for(int i=0;i<list.size();i++){
					String seq = list.get(i);
					if(contentmap.containsKey(seq)){
						String [] content = contentmap.get(seq);
						BufferedImage image = ImageIO.read(new File(content[3]));

						this.writePep(seq, content[0], content[1], content[2], image);
						contentmap.remove(seq);
					}
				}
			}
		}
		System.out.println(contentmap.size());
		
		Iterator <String> it = contentmap.keySet().iterator();
		while(it.hasNext()){
			String seq = it.next();
			String [] content = contentmap.get(seq);
			BufferedImage image = ImageIO.read(new File(content[3]));

			this.writePep(seq, content[0], content[1], content[2], image);
		}
		
		reader.close();
	}
	
	public void writePep(String sequence, String ref, String kloss, String half, BufferedImage image) 
			throws DocumentException, IOException{
		
		HashMap <String, String> map = new HashMap <String, String>();
		map.put("Sequence", sequence);
		map.put("Protein", ref);
		map.put("Kloss", kloss);
		map.put("Halflife", half);
		this.pdf.stamp(map, image);
	}

	public void close() {
		try {
			this.pdf.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JXLException 
	 */
	public static void main(String[] args) throws IOException, DocumentException, JXLException {
		// TODO Auto-generated method stub

		String templete = "J:\\Data\\sixple\\turnover\\dat\\final5\\pro_templete.pdf";
		String output = "J:\\Data\\sixple\\turnover\\dat\\final5\\pros.pdf";
		String file = "J:\\Data\\sixple\\turnover\\dat\\final5\\turnover.xls";
		String pngs = "J:\\Data\\sixple\\turnover\\dat\\final5\\turnover_pro";
		WangDataPdfWriter writer = new WangDataPdfWriter(templete, output);
		writer.writePro(file, pngs);
//		writer.writePep(file, pngs);
		writer.close();
	}

}
