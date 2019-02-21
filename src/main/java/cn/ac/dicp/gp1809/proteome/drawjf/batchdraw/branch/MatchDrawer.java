/* 
 ******************************************************************************
 * File: MatchDrawer.java * * * Created on 02-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.branch;

import java.io.IOException;

import com.itextpdf.text.DocumentException;

/**
 * Draw matched spectra to xls linked pictures or PDF file
 * 
 * @author Xinning
 * @version 0.2.3, 12-19-2008, 15:22:16
 */
public abstract class MatchDrawer {
	
	/**
	 * After creation of an instance for subclass of MatchDrawer,
	 * all the tasks should be down in this method.
	 * That is, draw all the pictures to pdf file or link them to excel. 
	 * 
	 * @throws IOException
	 */
	public abstract void draw() throws IOException;
	
	/**
	 * when finish, this method must be called!;
	 */
	public abstract void finish();
	
	protected static void usage(){
		System.out.println("MatchDrawer(V2.3, 12-19-2008): Draw matched ions for spectra\r\n" +
				"\tUsage: MatchDrawer -xls|pdf -Norm|Phos input output dir1 [dir2]\r\n" +
				"\t\tOptions: -xls|pdf select the output file format, if want a pdf file output" +
				                         "type \"-pdf\"\r\n" +
				"\t\t         -Norm|Phos select the file type of the inputed file. If it is a " +
										 "normal noredundant file, type \"-norm\", otherwise, if it" +
										 "is an APIVASE outputed top file, type \"-phos\"\r\n" +
				"\t\t         inputfile the name of the input file which contains peptide or protein" +
									   "list for draw\r\n" +
				"\t\t         output the name of the output file, make sure the extension of the file" +
									"is corresponding to the selected file format\r\n" +
				"\t\t         dir1 directory containing all the dta and outfiles. If \"-b\" option is" +
								 "is selected on, this dir is the directory of upper dir containing all" +
								 "the needed directories with dta and out files\r\n" +
				"\t\t         [dir2] when \"-phos\" option is selected, the dir1 is the ms2 directory, and" +
									"dir2 is the ms3 directory.");
		System.exit(0);
	}
	
	public static void main(String[] args) throws IOException, DocumentException {
		int len = args.length;
		if(len<5)
			usage();
		
		boolean xls=true;
		String type = args[0].toLowerCase();
		
		if(type.equals("-xls"))	;
		else if(type.equals("-pdf")) xls = false;
		else{
			System.out.println("the output file type must be \"-pdf\" or \"-xls\"");
			usage();
		}
		
		boolean phos = true;
		String ph = args[1].toLowerCase();
		if(ph.equals("-norm")) phos = false;
		else if(ph.equals("-phos")) ;
		else{
			System.out.println("the option of \"-Norm\" or \"-Phos\" must be set");
			usage();
		}
		
		if(phos&&len!=6)
			usage();
		
		if(phos==false&&len!=5)
			usage();
		
		
		String topfile = args[2];
		String outfile = args[3];
		String dir1 = args[4];
		String ms3folder = phos ? args[5] : null;
		
		MatchDrawer match = null;
/*		
		if(phos)
			match = xls? new XlsPhosMatchDrawer(topfile,outfile,dir1, ms3folder)
									: new PDFPhosMatchDrawer(topfile,outfile,dir1, ms3folder);
		else
			match = new PDFNoredMatchDrawer(topfile,outfile,dir1);
*/		
		match.draw();
	}
}
