/* 
 ******************************************************************************
 * File: MoreThanOneProteinFound.java * * * Created on 12-21-2007
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

import cn.ac.dicp.gp1809.exceptions.MyException;


/**
 * If more than one protein references are found in a fasta database, this exception
 * will be threw. This probably because that the partial name from SEQUEST of other
 * database search algorithm was with length less than discrimination of duplicated 
 * protein reference.
 * 
 * @author Xinning
 * @version 0.1, 12-21-2007, 10:42:38
 */
public class MoreThanOneRefFoundInFastaException extends MyException {

	/**
     * 
     */
    private static final long serialVersionUID = -5711320491081044673L;

	/**
	 * 
	 */
	public MoreThanOneRefFoundInFastaException() {
	}

	/**
	 * @param message
	 */
	public MoreThanOneRefFoundInFastaException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MoreThanOneRefFoundInFastaException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MoreThanOneRefFoundInFastaException(String message, Throwable cause) {
		super(message, cause);
	}

}
