/* 
 ******************************************************************************
 * File: DtaWriter.java * * * Created on 12-29-2007
 *
 * Copyright (c) 2007 Xinning Jiang vext@163.com
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
package cn.ac.dicp.gp1809.proteome.spectrum.dta.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;


/**
 * 
 * @author Xinning
 * @version 0.1, 12-29-2007, 16:38:17
 */
public class DtaWriter {
	
	private DtaWriter(){
		
	}
	
	/**
	 * Write peaklist to dta file;
	 * 
	 * @param filename the full name of the dta output file
	 * @param peaklist 
	 * @throws IOException when something is wrong in writing.
	 */
	public static void writeToFile(String filename, IMS2PeakList peaklist) throws IOException{
		writeToFile(new File(filename),peaklist);
	}
	
	/**
	 * Write peaklist to dta file;
	 * 
	 * @param file the file to be write as a dta output file
	 * @param peaklist 
	 * @throws IOException when something is wrong in writing.
	 */
	public static void writeToFile(File file, IMS2PeakList peaklist) throws IOException{
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		StringBuilder sb = new StringBuilder();
		
		PrecursePeak prepeak = peaklist.getPrecursePeak();
		Short charge = prepeak.getCharge();
		double MH = (prepeak.getMz()-1)*charge+1;
		
		sb.append(MH).append(" ").append(charge);
		
		pw.println(sb);
		
		IPeak[] peeks = peaklist.getPeakArray();
		for(int i=0;i<peeks.length;i++){
			pw.println(peeks[i]);
		}
		
		pw.close();
	}
	
}
