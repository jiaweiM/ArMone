/* 
 ******************************************************************************
 * File:ExcelWriter.java * * * Created on 2010-9-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.excel;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @author ck
 *
 * @version 2010-9-7, 09:13:01
 */
public class ExcelWriter {

	private WritableWorkbook wwb;
	private WritableSheet [] ws;
	private int [] currentLine;

	private String numRegex  = new String("-?\\d+\\.?\\d*");
//	private String numRegexPer  = new String("-?\\d+\\.?\\d*%");

	public ExcelWriter(String file) throws IOException{
		 this(new File(file));
	}
	
	public ExcelWriter(String file, int sheetNum) throws IOException{
		 this(new File(file), sheetNum);
	}
	
	public ExcelWriter(File file) throws IOException{
		 this.wwb = Workbook.createWorkbook(file);
		 this.ws = new WritableSheet[3];
		 for(int i=0;i<ws.length;i++){
			 ws[i] = wwb.createSheet("Sheet "+(i+1), i);
		 }
		 this.currentLine = new int[3];
	}
	
	public ExcelWriter(File file, int sheetNum) throws IOException{
		 this.wwb = Workbook.createWorkbook(file);
		 this.ws = new WritableSheet[sheetNum];
		 for(int i=0;i<ws.length;i++){
			 ws[i] = wwb.createSheet("Sheet "+(i+1), i);
		 }
		 this.currentLine = new int[sheetNum];
	}
	
	public ExcelWriter(String file, String [] sheetName) throws IOException{
		 this.wwb = Workbook.createWorkbook(new File(file));
		 this.ws = new WritableSheet [sheetName.length];
		 for(int i=0;i<ws.length;i++){
			 ws[i] = wwb.createSheet(sheetName[i], i);
		 }
		 this.currentLine = new int[sheetName.length];
	}
	
	private void addOneRow(String row, int sheetNum, WritableCellFormat format) throws RowsExceededException, WriteException{
		String [] cells = row.split("\t");
		for(int i=0;i<cells.length;i++){
			if(cells[i].matches(numRegex)){
				double num = Double.parseDouble(cells[i]);
				jxl.write.Number lab = new jxl.write.Number(i,currentLine[sheetNum],num, format);
				ws[sheetNum].addCell(lab);
			}else{
				Label lab = new Label(i,currentLine[sheetNum],cells[i].trim(), format);
				ws[sheetNum].addCell(lab);
			}
		}
		currentLine[sheetNum]++;
	}
	
	private void addOneRow(String [] cells, int sheetNum, WritableCellFormat format) throws RowsExceededException, WriteException{
		for(int i=0;i<cells.length;i++){
			if(cells[i]!=null){
				if(cells[i].matches(numRegex)){
					double num = Double.parseDouble(cells[i]);
					jxl.write.Number lab = new jxl.write.Number(i,currentLine[sheetNum],num, format);
					ws[sheetNum].addCell(lab);
				}else{
					Label lab = new Label(i,currentLine[sheetNum],cells[i].trim(), format);
					ws[sheetNum].addCell(lab);
				}
			}
		}
		currentLine[sheetNum]++;
	}
	
	private void addOneRow(String row, int sheetNum, ExcelFormat format, boolean title) 
			throws RowsExceededException, WriteException{
		
		boolean hasIndex = format.getIndex();
		
		if(title){
			WritableCellFormat conFormat = format.createTitleFormat();
			this.addOneRow(row, sheetNum, conFormat);
		}else{
			if(hasIndex){
				String [] cells = row.split("\t");
				WritableCellFormat idxFormat = format.createIndexFormat();
				WritableCellFormat conFormat = format.createContentFormat();
				if(cells[0].trim().length()>0){
					if(cells[0].matches(numRegex)){
						double num = Double.parseDouble(cells[0]);
						jxl.write.Number lab = new jxl.write.Number(0,currentLine[sheetNum],num, idxFormat);
						ws[sheetNum].addCell(lab);
					}else{
						Label lab = new Label(0,currentLine[sheetNum],cells[0].trim(), idxFormat);
						ws[sheetNum].addCell(lab);
					}								
				}	
				for(int i=1;i<cells.length;i++){
					if(cells[i].matches(numRegex)){
						double num = Double.parseDouble(cells[i]);
						jxl.write.Number lab = new jxl.write.Number(i,currentLine[sheetNum],num, conFormat);
						ws[sheetNum].addCell(lab);
					}else{
						Label lab = new Label(i,currentLine[sheetNum],cells[i].trim(), conFormat);
						ws[sheetNum].addCell(lab);
					}
				}
				currentLine[sheetNum]++;
			}else{
				WritableCellFormat conFormat = format.createContentFormat();
				this.addOneRow(row, sheetNum, conFormat);
			}
		}			
	}
	
	private void addOneRow(String [] cells, int sheetNum, ExcelFormat format, boolean title) 
			throws RowsExceededException, WriteException{
		
		boolean hasIndex = format.getIndex();
		
		if(title){
			WritableCellFormat conFormat = format.createTitleFormat();
			this.addOneRow(cells, sheetNum, conFormat);
		}else{
			if(hasIndex){
				WritableCellFormat idxFormat = format.createIndexFormat();
				WritableCellFormat conFormat = format.createContentFormat();
				if(cells[0].trim().length()>0){
					if(cells[0].matches(numRegex)){
						double num = Double.parseDouble(cells[0]);
						jxl.write.Number lab = new jxl.write.Number(0,currentLine[sheetNum],num, idxFormat);
						ws[sheetNum].addCell(lab);
					}else{
						Label lab = new Label(0,currentLine[sheetNum],cells[0].trim(), idxFormat);
						ws[sheetNum].addCell(lab);
					}								
				}	
				for(int i=1;i<cells.length;i++){
					if(cells[i].matches(numRegex)){
						double num = Double.parseDouble(cells[i]);
						jxl.write.Number lab = new jxl.write.Number(i,currentLine[sheetNum],num, conFormat);
						ws[sheetNum].addCell(lab);
					}else{
						Label lab = new Label(i,currentLine[sheetNum],cells[i].trim(), conFormat);
						ws[sheetNum].addCell(lab);
					}
				}
				currentLine[sheetNum]++;
			}else{
				WritableCellFormat conFormat = format.createContentFormat();
				this.addOneRow(cells, sheetNum, conFormat);
			}
		}			
	}
	
	private void addRows(String rows, int sheetNum, ExcelFormat format, boolean isTitle) 
			throws RowsExceededException, WriteException{
		
		String [] rowslist = rows.split("\n");
		for(int i=0;i<rowslist.length;i++){
			addOneRow(rowslist[i], sheetNum, format, isTitle);
		}
	}

	public void addTitle(String title, int sheetNum, ExcelFormat format) throws RowsExceededException, WriteException{
		this.addRows(title, sheetNum, format, true);
	}

	public void addContent(String conten, int sheetNum, ExcelFormat format) throws RowsExceededException, WriteException{
		this.addRows(conten, sheetNum, format, false);
	}
	
	public void addTitle(String [] title, int sheetNum, ExcelFormat format) throws RowsExceededException, WriteException{
		this.addOneRow(title, sheetNum, format, true);
	}

	public void addContent(String [] conten, int sheetNum, ExcelFormat format) throws RowsExceededException, WriteException{
		this.addOneRow(conten, sheetNum, format, false);
	}
	
	public void addBlankRow(int sheetNum){
		this.currentLine[sheetNum]++;
	}
	
	public void removeSheet(int sheetNum){
		this.wwb.removeSheet(sheetNum);
	}
	
	public void setSheetName(int sheetNum, String name){
		this.ws[sheetNum].setName(name);
	}
	
	public void close() throws WriteException, IOException{
		this.wwb.write();
		this.wwb.close();
		System.gc();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WriteException 
	 */
	public static void main(String[] args) throws IOException, WriteException {
		// TODO Auto-generated method stub
		ExcelWriter writer = new ExcelWriter("E:\\test.xls");
//		writer.addContent(";akjdg;hkalsdg\tal\nsd\ta;sgj\tas;gjdkl\t", true);
//		writer.close();
		System.out.println("100.0%".matches(writer.numRegex));

	}

}
