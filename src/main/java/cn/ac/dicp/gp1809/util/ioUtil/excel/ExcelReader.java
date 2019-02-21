/* 
 ******************************************************************************
 * File:ExcelReader.java * * * Created on 2010-9-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jxl.JXLException;
import jxl.Workbook;
import jxl.Sheet;
import jxl.Cell;

/**
 * @author ck
 *
 * @version 2010-9-7, 08:47:27
 */
public class ExcelReader {

	private Workbook rwb;
	private Sheet sheet;
	private int currentLine;
	private int totalLine;
	private int totalColumn;
	
	private Sheet [] sheets;
	private int [] currentLines;
	private int [] totalLines;
//	private int [] index;
	private int [] currentColumn;
	private int [] totalColumns;
	
	public ExcelReader(String file) throws IOException, JXLException{
		this(new File(file));
	}
	
	public ExcelReader(String file, int sheetNum) throws IOException, JXLException{
		this(new File(file), sheetNum);
	}
	
	public ExcelReader(File file) throws IOException, JXLException{
		this(file, 0);
	}
	
	public ExcelReader(File file, int sheetNum) throws IOException, JXLException{
		InputStream is = new FileInputStream(file);
		this.rwb = Workbook.getWorkbook(is);
		this.sheet = rwb.getSheet(sheetNum);
		this.sheets = new Sheet [] {sheet};
		this.totalLine = sheet.getRows();
		this.totalColumn = sheet.getColumns();
		this.totalColumns = new int [] {this.totalColumn};
	}
	
	public ExcelReader(String file, String sheetName) throws IOException, JXLException{
		InputStream is = new FileInputStream(file);
		this.rwb = Workbook.getWorkbook(is);
		this.sheet = rwb.getSheet(sheetName);
		this.sheets = new Sheet [] {sheet};
		this.totalLine = sheet.getRows();
		this.totalColumn = sheet.getColumns();
	}
	
	public ExcelReader(String file, int [] sheetNums) throws IOException, JXLException{
		this(new File(file), sheetNums);
	}
	
	public ExcelReader(File file, int [] sheetNums) throws IOException, JXLException{
		InputStream is = new FileInputStream(file);
		this.rwb = Workbook.getWorkbook(is);
		this.sheets = new Sheet[sheetNums.length];
		this.totalLines = new int[sheetNums.length];
		this.currentLines = new int[sheetNums.length];
		this.totalColumns = new int [sheetNums.length];

		for(int i=0;i<sheetNums.length;i++){
			this.sheets[i] = rwb.getSheet(sheetNums[i]);
			this.totalLines[i] = sheets[i].getRows();
			this.totalColumns[i] = sheets[i].getColumns();
		}		
	}
	
	public ExcelReader(String file, int [] index, boolean column) throws IOException, JXLException{
		if(column){
			InputStream is = new FileInputStream(file);
			this.rwb = Workbook.getWorkbook(is);
			this.sheets = new Sheet[index.length];
			this.totalColumns = new int[index.length];
			this.currentColumn = new int[index.length];
			for(int i=0;i<index.length;i++){
				this.sheets[i] = rwb.getSheet(index[i]);
				this.totalColumns[i] = sheets[i].getColumns();
			}
		}else{
			InputStream is = new FileInputStream(file);
			this.rwb = Workbook.getWorkbook(is);
			this.sheets = new Sheet[index.length];
			this.totalLines = new int[index.length];
			this.currentLines = new int[index.length];
			for(int i=0;i<index.length;i++){
				this.sheets[i] = rwb.getSheet(index[i]);
				this.totalLines[i] = sheets[i].getRows();
			}	
		}
	}
	
	public String [] readLine(){
		if(currentLine < totalLine){
			String [] columns = readOSLine(currentLine);
			currentLine++;
			return columns;
		}else{
			return null;
		}		
	}
	
	public String [] readLine(int sheetNum){
		if(currentLines[sheetNum] < totalLines[sheetNum]){
			String [] columns = readMSLine(sheetNum, currentLines[sheetNum]);
			currentLines[sheetNum]++;
			return columns;
		}else{
			return null;
		}		
	}

	private String [] readMSLine(int sheetNum, int lineNum){
		Cell [] cells = sheets[sheetNum].getRow(lineNum);
		String [] strs = new String [cells.length];
		for(int i=0;i<cells.length;i++){
			strs[i] = cells[i].getContents();
		}
		return strs;
	}
	
	private String [] readOSLine(int lineNum){
		Cell [] cells = sheet.getRow(lineNum);
		String [] strs = new String [cells.length];
		for(int i=0;i<cells.length;i++){
			strs[i] = cells[i].getContents();
		}
		return strs;
	}
	
	public String [] readColumn(){
		if(currentColumn[0] < totalColumns[0]){
			String [] columns = readOSColumn(currentColumn[0]);
			currentColumn[0]++;
			return columns;
		}else{
			return null;
		}		
	}
	
	public String [] readColumn(int sheetNum){
		if(currentColumn[sheetNum] < totalColumns[sheetNum]){
			String [] columns = readMSColumn(sheetNum, currentColumn[sheetNum]);
			currentColumn[sheetNum]++;
			return columns;
		}else{
			return null;
		}		
	}
	
	public String [] getColumn(int columnNum){
		if(columnNum < totalColumns[0]){
			String [] columns = readOSColumn(columnNum);
			return columns;
		}else{
			return null;
		}		
	}
	
	public String [] getColumn(int sheetNum, int columnNum){
		if(columnNum < totalColumns[sheetNum]){
			String [] columns = readMSColumn(sheetNum, columnNum);
			return columns;
		}else{
			return null;
		}		
	}
	
	private String [] readMSColumn(int index, int lineNum){
		Cell [] cells = sheets[index].getColumn(lineNum);
		String [] strs = new String [cells.length];
		for(int i=0;i<cells.length;i++){
			strs[i] = cells[i].getContents();
		}
		return strs;
	}
	
	public String [] readOSColumn(int lineNum){
		Cell [] cells = sheets[0].getColumn(lineNum);
		String [] strs = new String [cells.length];
		for(int i=0;i<cells.length;i++){
			strs[i] = cells[i].getContents();
		}
		return strs;
	}
	
	public void skip(int i){
		this.currentLine += i;
	}
	
	public void skip(int index, int i){
		this.currentLines[index] += i;
	}
	
	public int getTotalRowCount(){
		return sheet.getRows();
	}

	public int getNumOfSheets(){
		return rwb.getNumberOfSheets();
	}
	
	public void close(){
		this.sheet = null;
		this.rwb.close();
		System.gc();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
