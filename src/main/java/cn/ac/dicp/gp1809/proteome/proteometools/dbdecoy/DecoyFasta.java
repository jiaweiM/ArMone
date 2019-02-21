/*
 * *****************************************************************************
 * File: DecoyFasta.java * * * Created on 03-24-2008
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

import java.io.IOException;

/**
 * Interface for creation of decoy database.
 * Commonly used strategy for creation of a decoy database is
 * the reversed database strategy. 
 * 
 * @author Xinning
 * @version 0.1, 03-24-2008, 15:45:24
 */
public interface DecoyFasta {
	
	/**
	 * The symbol of a decoy protein. Commonly this symbol is "REV";
	 * This symbol is the start of the protein reference. e.g. REV_IPI.IPI000000...
	 * 
	 */
	public final static String DECOY_SYM = "REV";
	
	/**
	 * Make a decoy database from the original one.
	 * The forward and the outputed (decoy) database are all from (to)
	 * a fasta file.
	 */
	public void makeDecoy() throws IOException;
	
}
