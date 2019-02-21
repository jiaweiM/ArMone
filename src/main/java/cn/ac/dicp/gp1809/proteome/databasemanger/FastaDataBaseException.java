/* 
 ******************************************************************************
 * File: NotFastaDataBaseException.java * * * Created on 12-11-2007
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
package cn.ac.dicp.gp1809.proteome.databasemanger;


/**
 * If a file for database index is not a fasta database file or even not
 * a database, and some other exceptions that occurs while reading
 * of fasta database, this exception will be threw.
 * 
 * @author Xinning
 * @version 0.1, 12-11-2007, 13:49:28
 */
public class FastaDataBaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8224741223593241472L;

	/**
	 * 
	 */
	public FastaDataBaseException() {
	}

	/**
	 * @param message
	 */
	public FastaDataBaseException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FastaDataBaseException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FastaDataBaseException(String message, Throwable cause) {
		super(message, cause);
	}

}
