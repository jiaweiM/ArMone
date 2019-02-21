/*
 * *****************************************************************************
 * File: ExcelProReader.java * * * Created on 09-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.export;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;

import cn.ac.dicp.gp1809.exceptions.UnSupportingMethodException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ProReaderConstant;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.AbstractSequestPeptideReader;

/**
 * A reader for xls file outputted by bioworks in protein list form. Using
 * apache poi project for xls file reading.
 * 
 * @author Xinning
 * @version 0.3.4, 05-02-2010, 10:27:31
 */

public class ExcelProReader extends AbstractSequestPeptideReader implements
        ProReaderConstant {
	private HSSFSheet sheet = null;
	private int[] peptideIndexPosition = null;
	private int[] proteinIndexPosition = null;

	private String currentProtein = null;
	private int currentRowNumber = 2;
	private int rowNumber = 0;// row number in a sheet
	private short cellNumber = 0;// cell number in a line

	private BufferedInputStream instream;

	private SequestParameter parameter;

	public ExcelProReader(String filename, SequestParameter parameter)
	        throws ImpactReaderTypeException, ReaderGenerateException {
		this(new File(filename), parameter);
	}

	public ExcelProReader(File file, SequestParameter parameter)
	        throws ImpactReaderTypeException, ReaderGenerateException {
		super(file);

		HSSFWorkbook wb = null;
		try {
			instream = new BufferedInputStream(new FileInputStream(file));
			wb = new HSSFWorkbook(instream);
		} catch (FileNotFoundException e) {
			throw new ReaderGenerateException("The target file: "
			        + file.getName() + " is not reachable.");
		} catch (IOException e) {
			close();
			throw new ImpactReaderTypeException("ExcelReader unsuit Exception");
		}

		sheet = wb.getSheetAt(0);
		this.rowNumber = sheet.getLastRowNum();
		
		this.preRead();

		this.parameter = parameter;

		System.out.println("Begin to reading ...");
	}

	private void preRead() {
		HSSFRow proteinIndexRow = sheet.getRow(0);
		this.cellNumber = proteinIndexRow.getLastCellNum();
		cellNumber++;// last cell number need +1 to get the column number
		proteinIndexPosition = new int[cellNumber];
		for (short i = 0; i < cellNumber; i++) {
			HSSFCell cell = proteinIndexRow.getCell(i);
			String temp = getCellStringValue(cell);

			if (temp.equals("Reference"))
				proteinIndexPosition[i] = 0;

			/*
			 * used for other info, else if(temp.equals("consensus_score"))
			 * proteinlabelposition[i] = 1; else if(temp.equals("pI"))
			 * proteinlabelposition[i] = 2; else if(temp.equals("weight"))
			 * proteinlabelposition[i] = 3; else if(temp.equals("accession"))
			 * proteinlabelposition[i] = 4;
			 */
			else
				proteinIndexPosition[i] = -1;
		}

		HSSFRow peptideIndexRow = sheet.getRow(1);
		peptideIndexPosition = new int[cellNumber];
		for (short i = 0; i < cellNumber; i++) {
			HSSFCell cell = peptideIndexRow.getCell(i);
			String temp = getCellStringValue(cell);

			if (temp.equals("Scan(s)") || temp.equals("File, Scan(s)"))
				peptideIndexPosition[i] = scanColumn;
			else if (temp.equals("Peptide"))
				peptideIndexPosition[i] = sequenceColumn;
			else if (temp.equals("MH+"))
				peptideIndexPosition[i] = massColumn;
			else if (temp.equals("deltamass"))
				peptideIndexPosition[i] = deltaMassColumn;
			else if (temp.equals("z"))
				peptideIndexPosition[i] = chargeColumn;
			else if (temp.equals("XC"))
				peptideIndexPosition[i] = xcorrColumn;
			else if (temp.equals("DeltaCn"))
				peptideIndexPosition[i] = deltaCnColumn;
			else if (temp.equals("Sp"))
				peptideIndexPosition[i] = spColumn;
			else if (temp.equals("RSp"))
				peptideIndexPosition[i] = rspColumn;
			else if (temp.equals("Ions"))
				peptideIndexPosition[i] = ionsColumn;
			else
				peptideIndexPosition[i] = -1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .AbstractPeptideReader#getPeptideImp()
	 */
	@Override
	protected SequestPeptide getPeptideImp() {

		if (this.currentRowNumber > this.rowNumber) {
			this.close();
			return null;
		}

		String[] peptideArray = null;
		HSSFRow row = sheet.getRow(this.currentRowNumber);
		HSSFCell firstcell = row.getCell((short) 0);
		HSSFCell secondcell = row.getCell((short) 1);

		// peptide line start with "" while protein with number
		if (getCellStringValue(firstcell).length() == 0) {
			/*
			 * end of the file
			 */
			if (getCellStringValue(secondcell).length() == 0) {

				try {
					this.instream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			peptideArray = this.parseArray(row, this.currentProtein);
		} else {
			this.currentProtein = this.getCurrtentProtein(row);
			this.currentRowNumber++;

			row = sheet.getRow(this.currentRowNumber);
			peptideArray = this.parseArray(row, this.currentProtein);
		}
		this.currentRowNumber++;

		/*
		 * For very few cases, illegal reference for protein is outputted by
		 * Sequest (Bug ??), these peptides with this reference will be
		 * automately ignored.
		 */
		if (this.currentProtein.length() < MIN_PRO_REF_LEN) {
			System.out.println("Peptide: \"" + peptideArray[sequenceColumn]
			        + "\" with illegal protein reference: \""
			        + this.currentProtein + "\", was ignored");
			return getPeptideImp();
		}

		SequestPeptide pep = new SequestPeptide(peptideArray, this.getPeptideFormat());
		pep.setEnzyme(this.parameter.getEnzyme());
		return pep;
	}

	public static String getCellStringValue(HSSFCell cell) {
		if (cell == null)
			return "";

		int celltype = cell.getCellType();
		String cellvalue = null;

		switch (celltype) {
		case 0:// numeric
			if (DateUtil.isCellDateFormatted(cell))
				cellvalue = String.valueOf(cell.getDateCellValue());
			else {
				double value = cell.getNumericCellValue();
				int intvalue = (int) value;

				if (value == intvalue)
					cellvalue = String.valueOf(intvalue);
				else
					cellvalue = String.valueOf(value);
			}
			break;
		case 1: // string
			cellvalue = cell.getRichStringCellValue().getString();
			break;
		case 2: // Sheet.setDisplayFormulas(true);
			cellvalue = String.valueOf(cell.getNumericCellValue());
			break;
		case 3: // blank
			cellvalue = cell.getRichStringCellValue().getString();
			break;

		default:
			System.out.println("Unkown cell type!");
			System.exit(1);
		}

		return cellvalue;
	}

	/*
	 * 
	 */
	private String[] parseArray(HSSFRow row, String currentProtein) {

		String[] peptideArray = new String[peptideIndexLength];
		int positon = 0;

		for (short i = 1; i < this.cellNumber; i++) {
			String cellString = null;
			HSSFCell cell = row.getCell(i);

			cellString = getCellStringValue(cell);

			if ((positon = this.peptideIndexPosition[i]) >= 0)
				peptideArray[positon] = cellString;
		}

		// In excel mode, no delta mass column, so insert with 0;
		peptideArray[deltaMassColumn] = "0";
		peptideArray[proteinColumn] = currentProtein;

		return peptideArray;
	}

	private String getCurrtentProtein(HSSFRow row) {
		String currentProtein = null;

		for (short i = 1; i < this.cellNumber; i++) {
			String cellString = null;
			HSSFCell cell = row.getCell(i);

			cellString = getCellStringValue(cell);

			if (this.peptideIndexPosition[i] >= 0) {
				currentProtein = cellString;
				break;
			}
		}
		return currentProtein;
	}

	@Override
	public void close() {
		if (this.instream != null) {
			try {
				this.instream.close();
			} catch (IOException e) {
				System.out
				        .println("Error in closing the excel file after reading."
				                + " But it doesn't matter :)");
			}
		}
		System.out.println("Finished reading.");
	}

	public String getScanNum() {
		return null;
	}

	@Override
	public SequestParameter getSearchParameter() {
		return this.parameter;
	}
	
	/**
	 * Always return the maximum integer value
	 */
	@Override
	public int getTopN() {
		return Integer.MAX_VALUE;
	}

	/**
	 * throw new UnSupportingMethodException
	 */
	@Override
	public void setTopN(int topn) {
		throw new UnSupportingMethodException(
        	"Cannot limit the top n for peptide reading.");
	}
}