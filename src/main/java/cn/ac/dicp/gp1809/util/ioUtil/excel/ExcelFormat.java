/* 
 ******************************************************************************
 * File:ExcelFormat.java * * * Created on 2010-9-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.excel;

import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableFont.FontName;

/**
 * @author ck
 *
 * @version 2010-9-17, 09:10:08
 */
public class ExcelFormat {

	private boolean hasIndex;
	private int colour;
	
	private static FontName [] fn = new FontName []{WritableFont.ARIAL,
		WritableFont.TIMES};
	
	private static int [] ps = new int []{11, 12, 13, 14};

	private static boolean [] italic = new boolean []{false, true};
	
	private static UnderlineStyle [] uls = new UnderlineStyle []{UnderlineStyle.NO_UNDERLINE, 
		UnderlineStyle.SINGLE};
	
	private static jxl.format.Colour [] colours = new jxl.format.Colour[]{Colour.BLACK, Colour.RED, Colour.BLUE,
		Colour.BRIGHT_GREEN};
	
	public static ExcelFormat normalFormat = new ExcelFormat(false, 0);
	
	public static ExcelFormat indexFormat = new ExcelFormat(true, 0);
	
	public ExcelFormat(boolean hasIndex, int colour){
		this.hasIndex = hasIndex;
		this.colour = colour;
	}

	public boolean getIndex(){
		return this.hasIndex;
	}
	
	public int colour(){
		return this.colour;
	}
	
	public WritableCellFormat createTitleFormat(){
		Colour c = colours[colour];
		WritableFont title = new WritableFont(fn[0],ps[3],WritableFont.NO_BOLD,italic[0],uls[0],c);
		WritableCellFormat format = new WritableCellFormat(title);
/*		
		try {
			format.setWrap(true);
			format.setShrinkToFit(true);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		return format;
	}
	
	public WritableCellFormat createIndexFormat(){
		Colour c = colours[colour];
		WritableFont title = new WritableFont(fn[0],ps[2],WritableFont.NO_BOLD,italic[0],uls[0],c);
		return new WritableCellFormat(title);
	}
	
	public WritableCellFormat createContentFormat(){
		Colour c = colours[colour];
		WritableFont title = new WritableFont(fn[0],ps[0],WritableFont.NO_BOLD,italic[0],uls[0],c);
		return new WritableCellFormat(title);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
