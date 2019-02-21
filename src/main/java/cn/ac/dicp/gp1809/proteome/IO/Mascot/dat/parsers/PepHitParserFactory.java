/* 
 ******************************************************************************
 * File: PepHitParserFactory.java * * * Created on 11-12-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.parsers;

/**
 * Factory to create peptide hit parser for different version of mascot
 * 
 * @author Xinning
 * @version 0.1, 11-12-2008, 09:42:29
 */
public class PepHitParserFactory {

	/**
	 * Create the peptide parser for the version of mascot.
	 * 
	 * @param mascot_version from the header of mascot dat file
	 * @return
	 */
	public static IPepHitParser createParser(String mascot_version) {

		if (mascot_version.startsWith("2.2")) {
			return new PepHitParser2_2();
		}

		if (mascot_version.startsWith("2.0")) {
			return new PepHitParser2_0();
		}
		
		if (mascot_version.startsWith("2.3")) {
			return new PepHitParser2_2();
		}

		throw new IllegalArgumentException(
		        "UnSupported version of Mascot for peptide hit parsing.");
	}

}
