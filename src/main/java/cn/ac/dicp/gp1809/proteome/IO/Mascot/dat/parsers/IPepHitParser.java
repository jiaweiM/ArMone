/* 
 ******************************************************************************
 * File: IPepHitParser.java * * * Created on 11-12-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.parsers;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.PeptideHit;

/**
 * Because the structures of peptide hit for different version of mascot are no
 * same, different parser may be needed.
 * 
 * @author Xinning
 * @version 0.1, 11-12-2008, 09:32:23
 */
public interface IPepHitParser {

	/**
	 * Parse the hit str into a peptide hit.
	 * <p>
	 * Notice: currently, we know
	 * <li> the version 2.0 used one line to describe a peptide hit with no
	 * terminal information
	 * <li>the version 2.2 (?) used two line to describe a peptide hit. The
	 * second line is the terminal information
	 * 
	 * 
	 * @param hit_str[]
	 *            the array of hit_str
	 * @return
	 */
	public PeptideHit parse(String[] hit_str);

	/**
	 * Get number of lines per hit. For mascot v2.2, two lines indicate a
	 * peptide hit. However, for mascot v2.0, only one lines for a peptide hit
	 * 
	 * @return
	 */
	public int getNumLineperHit();

}
