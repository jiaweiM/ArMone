/* 
 ******************************************************************************
 * File: IDtaReader.java * * * Created on 04-25-2008
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
 * Reader for Sequest .dta file or SRF dta inner file.
 * 
 * @author Xinning
 * @version 0.1, 04-25-2008, 14:39:58
 */
public interface IDtaReader {

	/**
	 * Get the IScanDta instance. If the isIncudePeakList is true, both the peak
	 * list information and the common summary information is contained in the
	 * DtaFile instance. Otherwise, the peaklist in DtaFile will be null. That
	 * is, if the getPeakList() method is called, null will be returned. This is
	 * mainly used for the simplify and reduce the memory usage.
	 * 
	 * @param isIncludePeakList boolean to identify if include peak list
	 * @return IScanDta instance
	 * @throws DtaFileParsingException
	 */
	public IScanDta getDtaFile(boolean isIncludePeakList) throws DtaFileParsingException;

}
