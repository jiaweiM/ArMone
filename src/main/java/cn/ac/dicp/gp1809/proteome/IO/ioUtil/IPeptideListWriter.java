/* 
 ******************************************************************************
 * File: IPeptideListWriter.java * * * Created on 05-13-2008
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
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.util.Date;

/**
 * A storage of peptides after database search. Then the peptide can be read
 * from this file. All the source of peptides after database search by SEQUEST
 * will be translated into this format first before the return.
 * 
 * @author Xinning
 * @version 0.1, 01-02-2009, 15:28:34
 */
public interface IPeptideListWriter extends IPeptideWriter {

	/**
	 * Get the type of the peptides for written.
	 * 
	 * @return
	 */
	public PeptideType getPeptideType();

	/**
	 * The header of the peptide list file. This header can be used for the test
	 * of whether the peptide list file is intact. And it also containing useful
	 * informations.
	 * 
	 * @author Xinning
	 * @version 0.1, 01-02-2009, 15:26:33
	 */
	public interface IPeptideListHeader extends java.io.Serializable {

		/**
		 * @return The Date when the peptide list file is written.
		 */
		public Date getWriteDate();

		/**
		 * @return The type of wrote peptide.
		 */
		public PeptideType getPeptideType();

		/**
		 * @return The description of this header. Used for the println()
		 */
		public String getDescription();

	}

	/**
	 * The indexes of peptides in the peptide list
	 * 
	 * @author Xinning
	 * @version 0.1, 04-22-2009, 10:18:25
	 */
	public interface IPeptideListIndex extends java.io.Serializable {

		/**
		 * @return The start position for the peptide string
		 */
		public int getPeptideStartPosition();

		/**
		 * The start position for the spectra identified this peptides. For
		 * normal peptides, the number of spectra should always be 1. For
		 * MS2/MS3 peptide pair, the number should be 2 and the two spectra will
		 * be printed together.
		 * 
		 * @return
		 */
		public int getSpectraStartPositions();

		/**
		 * @return Number of peak list for this peptide identification
		 */
		public int getNumerofSpectra();
		
		/**
		 * Convert to a long peptide list index
		 * 
		 * @return long peptide list index.
		 */
		public ILongPeptideListIndex toLongPeptideListIndex();
	}
	
	
	/**
	 * The indexes of peptides in the peptide list, for long list of peptides
	 * 
	 * @author Xinning
	 * @version 0.1, 04-22-2009, 10:18:25
	 */
	public interface ILongPeptideListIndex extends java.io.Serializable {

		/**
		 * @return The start position for the peptide string
		 */
		public long getPeptideStartPosition();

		/**
		 * The start position for the spectra identified this peptides. For
		 * normal peptides, the number of spectra should always be 1. For
		 * MS2/MS3 peptide pair, the number should be 2 and the two spectra will
		 * be printed together.
		 * 
		 * @return
		 */
		public long getSpectraStartPositions();

		/**
		 * @return Number of peak list for this peptide identification
		 */
		public int getNumerofSpectra();
	}
}
