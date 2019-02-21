/* 
 ******************************************************************************
 * File: IProteinProbCalculator.java * * * Created on 12-27-2007
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
package cn.ac.dicp.gp1809.proteome.probability;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.NoPeptideProbabilityException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
/**
 * Interface for the protein probability calculation
 * 
 * @author Xinning
 * @version 0.2, 01-05-2008, 17:36:56
 */
public interface IProteinProbCalculator {
	
	/**
	 * Compute the probability for a protein identification;
	 * @param pro
	 * @throws NoPeptideProbabilityException if the peptides resulting in the protein identification
	 * 		   have not been calculated for probability.
	 */
	public float getProbability(Protein pro) throws NoPeptideProbabilityException;
	
	/**
	 * Compute the probability for proteins with crossed peptides for their identifications.
	 * Commonly, these proteins should come from a same ProteinGroup, and all proteins in this protein
	 * group should in this protein array.
	 * 
	 * @param protein[] proteins come from a single ProteinGroup. 
	 * @throws NoPeptideProbabilityException if the peptides resulting in the protein identification
	 * 		   have not been calculated for probability.
	 */
	public float[] getProbability(Protein[] proteins) throws NoPeptideProbabilityException;
}

