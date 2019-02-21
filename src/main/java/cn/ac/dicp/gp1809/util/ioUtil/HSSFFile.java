/*
 ******************************************************************************
 * File: HSSFFile.java * * * Created on 10-15-2007
 *
 * Copyright (c) 2007 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileTypeErrorException;

/**
 * Reader and Writer for Excel 97 - 2003 by packing of POI xls module. 
 * 
 * @author Xinning
 * @version 0.3.2, 05-20-2010, 11:02:57
 */
public class HSSFFile {
	
    private boolean rw;

    private static final int O_RDONLY = 1;
    private static final int O_RDWR =   2;
    private static final int O_WR = 3;
	
	private HSSFWorkbook wb = null;
	private String outname = null;
	private File inputfile = null;
	private InputStream instream = null;
	private int numofsheets = 0;
	
	private HSSFSheet curtsheet = null;
	private HSSFRow curtrow = null;
	
	
	/**
	 * @param outname
	 * @param mode r, w, or rw (if w, it will first delete the file (if exist) 
	 * 		  and then write to a new file )
	 * @throws FileTypeErrorException 
	 */
	public HSSFFile(String name, String mode) throws FileTypeErrorException{
		this(name != null ? new File(name) : null, mode);
	}
	
	/**
	 * 
	 * @param file
	 * @param mode r, w, or rw (if w, it will first delete the file (if exist) 
	 * 		  and then write to a new file )
	 * @throws FileTypeErrorException
	 */
    public HSSFFile(File file, String mode) throws FileTypeErrorException{
    	this.inputfile = file;
    	String name = (file != null ? file.getPath() : null);
    	outname = (name==null? null : name+".tmp");
    	
        if (name == null) {
            throw new NullPointerException("File: "+name+" does not exist!");
        }
    	
    	int imode = -1;
    	
    	if (mode.equals("r"))
    	    imode = O_RDONLY;
    	else if (mode.equals("rw")){
    		imode = O_RDWR;
    		this.rw = true;
    	}
    	else if(mode.equals("w")){
    		imode = O_WR;
    		this.rw = true;
    	}

    	if (imode < 0)
    	    throw new IllegalArgumentException("Illegal mode \"" + mode
    					       + "\" must be one of \"r\", \"r\" or \"rw\"");
    	
    	SecurityManager security = System.getSecurityManager();
    	if (security != null) {
    	    security.checkRead(name);
    	    if (rw) {
    	    	security.checkWrite(outname);
    	    }
    	}
    	
    	if(this.inputfile.exists()){
        	if(this.checkUsing(this.inputfile))
        		throw new RuntimeException("File inuse!");   
            
        	if(imode==O_WR){//write only
        		this.inputfile.delete();
        		this.wb = new HSSFWorkbook();
        	}
        	else{
                try {
                	
                	this.instream = new FileInputStream(this.inputfile);
        			this.wb = new HSSFWorkbook(instream);

        		} catch (IOException e) {
        			throw new FileTypeErrorException("It doesn't seem like an excel file!");
        		}
        	}
    	}
    	else{
    		this.wb = new HSSFWorkbook();
    	}
    	
    	this.numofsheets = this.wb.getNumberOfSheets();
    	
    	//set current work sheet as the first one, if none, create one;
    	this.setCurrentSheet(0);
    	this.setCurrentRow(0);
    }
    
    private boolean checkUsing(File file){
    	
    	String origin = file.getAbsolutePath();
    	File test = new File(origin+".jangtempsx");
    	
    	boolean ifuse = !file.renameTo(test);
    	
    	if(!ifuse)
    		test.renameTo(file);
    	
    	return ifuse;
    }
	
    /**
     * Set the current sheet for reading,by default the sheet is number 1 sheet(sheet 0);
     * 
     * @param sheetnum
     * @return sheetnumber
     * @throws NullPointerException
     */
    public int setCurrentSheet(int sheetnum) throws NullPointerException {  	
    	
    	if(sheetnum>=this.numofsheets){
    		
    		if(this.rw)
    			this.curtsheet = this.wb.createSheet();
    		else
    			throw new NullPointerException();
    	}
    	else{
    		this.curtsheet = this.wb.getSheetAt(sheetnum);
    	}
    	
    	return sheetnum;
    }
    
    /**
     * Set the current sheet for reading
     * 
     * @param sheetname
     * @return sheetname
     * @throws NullPointerException
     */
    public String setCurrentSheet(String sheetname) throws NullPointerException{
    	this.curtsheet = this.wb.getSheet(sheetname);
    	
    	if(this.curtsheet == null){
    		if(this.rw)
    			this.curtsheet = this.wb.createSheet(sheetname);
    		else
    			throw new NullPointerException();
    	}
    	
    	return sheetname;
    }
    
    /**
     * Set the current active row (0 - 65534) for reading or writing (in write mode). 
     * 
     * <p>1. If the row number is less than -1, throws IndexOutofBoundException.
     * <p>2. In read only ("r") mode, if the number is bigger than the total number of 
     * rows in the sheet, -1 will be returned, indicating the end of sheet.
     * <p>3. In "read and write" mode, if the row number is bigger than the max_col_number (65534 in
     * excel 97 - 2003), throws IndexOutofBoundException.
     * 
     * <p><p> <b>In all cases, if the returned value is -1, current row is a null row, and should not
     * be used for reading and writing. And this can be a symbol for the end of xls file</b>
     * 
     * @param rownum
     * @return rownnum
     * @throws IndexOutofBoundException if the row number is not illegal (0-65534)
     */
    public int setCurrentRow(int rownum){

    	this.curtrow = this.curtsheet.getRow(rownum);

    	if(this.curtrow == null&&this.rw){
    		this.curtrow = this.curtsheet.createRow(rownum);
    	}
    	
    	if(this.curtrow==null)
    		return -1;
    	
    	return rownum;
    }
    
    /**
     * Get the value for the cell specified
     * 
     * @param rownum
     * @param cellnum
     * @return
     * @throws IndexOutOfBoundsException if the row number is 
     */
    public String getStringCellValue(int rownum, short cellnum){
    	
    	this.setCurrentRow(rownum);
    	
    	return this.getStringCellValue(cellnum);

    }
    
    /**
     * The string cell value. To test whether current row  
     * 
     * @param cellnum
     * @return the String value of current cell in the active row (see setRow(int))
     * @throws IndexOutOfBoundsException if the current active row ins out of index
     */
    public String getStringCellValue(short cellnum){
    	String value = null;
    	
    	if(this.curtrow == null)
    		throw new IndexOutOfBoundsException("The index must be with 0 - ");
    	
    	if(cellnum > this.curtrow.getLastCellNum())//The null cell
    		return null;
    	
    	HSSFCell cell = this.curtrow.getCell(cellnum);
    	value = getCellStringValue(cell);
    	
    	return value;
    }
    
    /**
     * Get String cell iterator for the specific row
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public Iterator<String> getStringCellIterator(int rownum){
    	this.setCurrentRow(rownum);
    	
    	if(this.curtrow == null)
    		throw new IndexOutOfBoundsException("The index must be with 0 - ");
    	
    	final Iterator iterator = this.curtrow.cellIterator();
    	
    	return new Iterator<String>(){

			@Override
            public boolean hasNext() {
	            return iterator.hasNext();
            }

			@Override
            public String next() {
	            return getCellStringValue((HSSFCell)iterator.next());
            }

			@Override
            public void remove() {
				iterator.remove();
            }
    		
    	};
    }
    
    
    /**
     * Set the cell value
     * 
     * @param value
     * @param rownum
     * @param cellnum
     * @return true
     * @throws NullPointerException
     */
    public boolean setCellValue(String value, int rownum, short cellnum) throws NullPointerException {
    	this.setCurrentRow(rownum);
    	
    	return this.setCellValue(value,cellnum);
    }
    
    /**
     * Set the cell value for current active row.
     * 
     * @param value
     * @param cellnum
     * @return true
     * @throws NullPointerException
     */
    public boolean setCellValue(String value, short cellnum) throws NullPointerException{
    	if(this.curtrow == null)
    		throw new NullPointerException();
    	
    	
    	if(!this.rw){
    		throw new RuntimeException("Can't write in read mode!");
    	}
    	
    	
    	HSSFCell cell = this.curtrow.createCell(cellnum);
    	
    	cell.setCellValue(new HSSFRichTextString(value));
    	
    	return true;
    }
    
    public boolean setCellValue(double value, int rownum, short cellnum) throws NullPointerException {
    	this.setCurrentRow(rownum);
    	
    	return this.setCellValue(value,cellnum);
    }
    
    public boolean setCellValue(double value,short cellnum) throws NullPointerException{
    	if(this.curtrow == null)
    		throw new NullPointerException();
    	
    	
    	if(!this.rw){
    		throw new RuntimeException("Can't write in read only mode!");
    	}
    	
    	
    	HSSFCell cell = this.curtrow.createCell(cellnum);
    	
    	cell.setCellValue(value);
    	
    	return true;
    }
    
    public boolean setCellValue(int value, int rownum, short cellnum) throws NullPointerException {
    	this.setCurrentRow(rownum);
    	
    	return this.setCellValue(value,cellnum);
    }
    
    public boolean setCellValue(int value,short cellnum) throws NullPointerException{
    	if(this.curtrow == null)
    		throw new NullPointerException();
    	
    	
    	if(!this.rw){
    		throw new RuntimeException("Can't write in read mode!");
    	}
    	
    	
    	HSSFCell cell = this.curtrow.createCell(cellnum);
    	
    	cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
    	
    	return true;
    }
    
    public boolean setCellValue(HSSFRichTextString rString, int rownum, short cellnum) throws NullPointerException {
    	this.setCurrentRow(rownum);
    	
    	return this.setCellValue(rString,cellnum);
    }
    
    public boolean setCellValue(HSSFRichTextString rString, short cellnum) throws NullPointerException{
    	if(this.curtrow == null)
    		throw new NullPointerException();
    	
    	
    	if(!this.rw){
    		throw new RuntimeException("Can't write in read mode!");
    	}
    	
    	
    	HSSFCell cell = this.curtrow.createCell(cellnum);
    	
    	cell.setCellValue(rString);
    	return true;
    }
    
    
    public boolean setCellValue(char value, int rownum, short cellnum) throws NullPointerException {
    	this.setCurrentRow(rownum);
    	
    	return this.setCellValue(value,cellnum);
    }
    
    public boolean setCellValue(char value,short cellnum) throws NullPointerException{
    	if(this.curtrow == null)
    		throw new NullPointerException();
    	
    	
    	if(!this.rw){
    		throw new RuntimeException("Can't write in read mode!");
    	}
    	
    	HSSFCell cell = this.curtrow.createCell(cellnum);
    	cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
    	
    	return true;
    }
    
    /**
     * Set a hyperlink for cell;
     * @param value cell value
     * @param link hyper link
     * @param rownum 
     * @param cellnum
     */
    public void setHyperLink(String value, String link, int rownum, short cellnum){
    	this.setCurrentRow(rownum);
    	this.setHyperLink(value, link, cellnum);
    }
    
    private HSSFCellStyle linkStyle;
    public void setHyperLink(String value, String link, short cellnum){
    	if(!this.rw){
    		throw new RuntimeException("Can't write in read only mode!");
    	}
    	
    	HSSFCell cell = this.curtrow.createCell(cellnum);
    	cell.setCellType(Cell.CELL_TYPE_FORMULA);
    	StringBuilder sb = new StringBuilder();
    	sb.append("HYPERLINK(\"");
    	sb.append(link);
    	sb.append("\",\"");
    	sb.append(value);
    	sb.append("\")");
    	cell.setCellFormula(sb.toString());
    	cell.setCellStyle(linkStyle==null? (linkStyle=this.getHyperLinkStyle(this.wb)) : linkStyle);
    }
    
    /**
     * Set a hyperlink using current cell value;
     * @param link
     * @param cellnum
     */
    public void setHyperLink(String link, short cellnum){
    	String value = this.getStringCellValue(cellnum);
    	this.setHyperLink(value, link, cellnum);
    }
    /**
     * Set a hyperlink using current cell value;
     * @param link link
     * @param cellnum
     */
    public void setHyperLink(String link, int rownum, short cellnum){
    	this.setCurrentRow(rownum);
    	
    	String value = this.getStringCellValue(cellnum);
    	this.setHyperLink(value, link, cellnum);
    }
    
    public void close(){
    	
    	if(this.rw){
    		try {
    			File outfile = new File(outname);
				BufferedOutputStream bstream = new BufferedOutputStream(new FileOutputStream(outfile));
				
				this.wb.write(bstream);
				bstream.close();
				
				if(this.instream!=null)
					instream.close();
				
				this.inputfile.delete();
				outfile.renameTo(this.inputfile);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else{
    		try{
    			
				if(this.instream!=null)
					instream.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	
    }
    
    public HSSFCellStyle getHyperLinkStyle(HSSFWorkbook workbook){
        HSSFCellStyle linkStyle = workbook.createCellStyle();
//		linkStyle.setBorderBottom((short)1);
//		linkStyle.setBorderLeft((short)1);
//		linkStyle.setBorderRight((short)1);
//		linkStyle.setBorderTop((short)1);
//		linkStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
//		linkStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        HSSFFont font = workbook.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setUnderline((byte)1);
        font.setColor(HSSFColor.BLUE.index);
        linkStyle.setFont(font);
        
        return linkStyle;
    }
    
    
    /**
     * ""
     * 
     * @param cell
     * @return string value for the cell. Null can be returned
     */
	private static String getCellStringValue(HSSFCell cell)
	{
		if(cell == null)
			return "";
		
		int celltype = cell.getCellType();
		String cellvalue = null;
		
		switch(celltype)
		{
			case 0://numeric
					if(DateUtil.isCellDateFormatted(cell))
					cellvalue = String.valueOf(cell.getDateCellValue());
					else{
						double value = cell.getNumericCellValue();
						int intvalue = (int)value;
						
						if(value==intvalue)
							cellvalue = String.valueOf(intvalue);
						else
							cellvalue = String.valueOf(value);
					}
					break;
			case 1: //string
					cellvalue = cell.getRichStringCellValue().getString();
					break;
			case 2: //Sheet.setDisplayFormulas(true);
					cellvalue = String.valueOf(cell.getNumericCellValue());
					break;
			case 3: //blank
					cellvalue = "";
					break;
			
			default : System.out.println("Unkown cell type!");
					  cellvalue = null;
		}
		
		return cellvalue;
	}
}
