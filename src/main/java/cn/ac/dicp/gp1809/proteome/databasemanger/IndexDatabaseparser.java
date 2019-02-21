/* 
 ******************************************************************************
 * File: IndexDatabaseparser.java * * * Created on 05-01-2008
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
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-01-2008, 23:39:38
 */
public class IndexDatabaseparser {

	private Header header;
	
	/**
	 * @param hdr_db The .hdr file for indexed database suite
	 * @throws IndexedDatabaseException 
	 */
	public IndexDatabaseparser(String hdr_db) throws IndexedDatabaseException{
		
		this.initialHeader(hdr_db);
		
	}
	
	private void initialHeader(String hdr_db) throws IndexedDatabaseException{
		
		try {
			
			this.header = new Header();
	        BufferedReader reader = new BufferedReader(new FileReader(hdr_db));
	        String line = null;
	        while((line = reader.readLine())!= null){
	        	if(line.startsWith("OrigDatabaseName")){
	        		header.setOriginal_fasta_db(line.substring(18).trim());
	        	}
	        }  
	        
        } catch (FileNotFoundException e) {
	        throw new IndexedDatabaseException("Can't find the specified hdr database: "+hdr_db, e);
        } catch (IOException e) {
        	throw new IndexedDatabaseException
        			("Error occurs while parsing the hdr file, may be damaged?", e);
        }
		
	}
	
	
	/**
	 * Header information of indexed database containing the index informations.
	 * These informations are contained in .hdr files
	 * 
	 * @return
	 */
	public Header getHeader(){
		return this.header;
	}
	
	
	/**
	 * Header information of indexed database containing the index informations.
	 * These informations are contained in .hdr files
	 * 
	 * @author Xinning
	 * @version 0.1, 05-01-2008, 23:40:04
	 */
	public static class Header{
		private String original_fasta_db;
		
		public Header(){
			
		}

		/**
		 * The original fasta database used for the creation of index database
		 * 
         * @return the original_fasta_db
         */
        public String getOriginal_fasta_db() {
        	return original_fasta_db;
        }

		/**
         * @param original_fasta_db the original_fasta_db to set
         */
        protected void setOriginal_fasta_db(String original_fasta_db) {
        	this.original_fasta_db = original_fasta_db;
        }
	}
	
}
