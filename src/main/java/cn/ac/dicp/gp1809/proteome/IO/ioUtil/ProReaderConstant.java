/*
 * *****************************************************************************
 * File: ProReaderConstent.java * * * Created on 11-21-2007 
 * Copyright (c) 2007 Xinning Jiang vext@163.com 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

//import java.util.HashMap;
//import java.util.Map;

/**
 * Constants for protein reading.
 * 
 * @author Xinning
 * @version 0.1.4, 03-27-2008, 17:10:31
 */
public interface ProReaderConstant {
	public static final String proteinTitle = new String("\tReference\tPepCount\tUniquePepCount\tCoverPercent\tProb\tMw\tpI\tGroupIdx\tCrossProCount\tTarget?");
	
	public static int peptideIndexLength = 14;
	
	/*
	public static HashMap<String, Integer> peptideIndexMap = new HashMap<String, Integer>(12);
	
	static
	{
		//initial peptide index

		peptideIndexMap.put("scan(s)"     , new Integer(0));
		peptideIndexMap.put("sequence" , new Integer(1));
		peptideIndexMap.put("mass"     , new Integer(2));
		peptideIndexMap.put("deltamass", new Integer(3));
		peptideIndexMap.put("charge"   , new Integer(4));
		peptideIndexMap.put("xcorr"    , new Integer(5));
		peptideIndexMap.put("deltacn"  , new Integer(6));
		peptideIndexMap.put("sp"       , new Integer(7));
		peptideIndexMap.put("rsp"      , new Integer(8));
		peptideIndexMap.put("ions"     , new Integer(9));
		peptideIndexMap.put("protein"  , new Integer(10));
		peptideIndexMap.put("pi"       , new Integer(11));
		peptideIndexMap.put("NTT" , new Integer(12));
	}
	
	
	
	
	
	 // the column number of each entry in peptideArray;

	public static final int scanColumn = peptideIndexMap.get("scan(s)");
	public static final int sequenceColumn = peptideIndexMap.get("sequence");
	public static final int chargeColumn = peptideIndexMap.get("charge");
	public static final int massColumn = peptideIndexMap.get("mass");
	public static final int deltaMassColumn = peptideIndexMap.get("deltamass");
	public static final int xcorrColumn = peptideIndexMap.get("xcorr");
	public static final int deltaCnColumn = peptideIndexMap.get("deltacn");
	public static final int spColumn = peptideIndexMap.get("sp");
	public static final int rspColumn = peptideIndexMap.get("rsp");
	public static final int ionsColumn = peptideIndexMap.get("ions");
	public static final int proteinColumn = peptideIndexMap.get("protein");
	public static final int piColumn = peptideIndexMap.get("pi");
	public static final int NTT = peptideIndexMap.get("NTT");
	*/
	
	/**
	 * These constant is retained for the xml and excel peptide reader.
	 */
	
	public static final int scanColumn = 0;
	public static final int sequenceColumn = 1;
	public static final int massColumn = 2;
	public static final int deltaMassColumn = 3;
	public static final int chargeColumn = 4;
	public static final int xcorrColumn = 5;
	public static final int deltaCnColumn = 6;
	public static final int spColumn = 7;
	public static final int rspColumn = 8;
	public static final int ionsColumn = 9;
	public static final int simColumn = 10;
	public static final int proteinColumn = 11;
	public static final int piColumn = 12;
	public static final int NTTColumn = 13;
	
	
	
	/**
	 * For very few cases, proteins loaded in Bioworks are with illegal reference
	 * such as " " and other null reference, this protein will not be parsed and ignored 
	 * from reading. The protein reference length less than this value will be considered 
	 * as illegal reference.
	 */
	public static final int MIN_PRO_REF_LEN = 4;

}
