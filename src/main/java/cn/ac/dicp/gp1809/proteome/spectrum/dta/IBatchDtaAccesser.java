/* 
 ******************************************************************************
 * File: IBatchDtaAccesser.java * * * Created on 05-30-2008
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
package cn.ac.dicp.gp1809.proteome.spectrum.dta;


/**
 * Random access the dta file in sequest directory.
 * 
 * @author Xinning
 * @version 0.1, 05-30-2008, 10:44:08
 */
public interface IBatchDtaAccesser {
	/**
	 * Random access the dta file versus the scan number and charge state.
	 * If the dta file with the specific scan number is not found, null will be
	 * returned. 
	 * 
	 * @param scanNumBeg
	 * @param scanNumEnd
	 * @param charge
	 * @param isIncludePeakList
	 * @return
	 * @throws DtaFileParsingException
	 */
	public IScanDta getDta(int scanNumBeg, int scanNumEnd, short charge, boolean isIncludePeakList) 
							throws DtaFileParsingException;
}
