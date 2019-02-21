/*
 * *****************************************************************************
 * File: Merger.java * * * Created on 03-24-2008
 * 
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.dbdecoy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Merge the original database and the decoy one into a
 * final composite database containing both the forward and the
 * decoy sequences.
 * 
 * @author Xinning
 * @version 0.1, 03-24-2008, 15:47:28
 */

public class Merger {
	
	/**
	 * Merge the original database and the decoy database into a composite database
	 * 
	 * @param originaldb forward one
	 * @param decoydb decoy one
	 * @param finaldb the final version
	 * @throws IOException
	 */
	public static void merge(String originaldb, String decoydb, String finaldb) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(originaldb));
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(finaldb)));
		String line;
		while((line=reader.readLine())!=null){
			pw.println(line);
		}
		reader.close();
		reader = new BufferedReader(new FileReader(decoydb));
		while((line=reader.readLine())!=null){
			pw.println(line);
		}
		reader.close();
		pw.close();
	}
	
	
}
