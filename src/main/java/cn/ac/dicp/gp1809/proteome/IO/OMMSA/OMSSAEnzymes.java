/* 
 ******************************************************************************
 * File: OMSSAEnzymes.java * * * Created on 10-19-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;

/**
 * The default OMSSA predefined enzymes. See
 * http://pubchem.ncbi.nlm.nih.gov/omssa/omssacgi.cgi.
 * 
 * @author Xinning
 * @version 0.1, 10-19-2008, 16:04:04
 */
public class OMSSAEnzymes {
	
	/*
	 * Map used to translate the name of enzyme to the cleavage sites
	 */
	private static HashMap<String, String> map = new HashMap<String, String>();
	
	static{
		map.put("Trypsin", "Trypsin, true, KR, P");
		map.put("No Enzyme", "No Enzyme, true, -, -");
	}
	
	private String[] enzymeStrs;
	
	
	
	/**
	 * Using the default predefined enzymes to construct the OMSSAEnzymes. 
	 * The indexes of the enzymes are the same as 
	 * http://pubchem.ncbi.nlm.nih.gov/omssa/omssacgi.cgi.
	 * 
	 *<li> 0					"Trypsin",
     *<li> 1                   	"Arg-C",
     *<li> 2                   	"CNBr",
     *<li> 3                   	"Chymotrypsin",
     *<li> 4                   	"Formic Acid",
     *<li> 5                   	"Lys-C",
     *<li> 6                   	"Lys-C, no P rule",
     *<li> 7                   	"Pepsin A",
     *<li> 8                   	"Trypsin+CNBr",
     *<li> 9                   	"Trypsin+Chymotrypsin",
     *<li> 10                   "Trypsin, no P rule",
     *<li> 11                   "Whole protein",
     *<li> 12                   "Asp-N",
     *<li> 13                   "Glu-C",
     *<li> 14                   "Asp-N+Glu-C",
     *<li> 15                   "Top-Down",
     *<li> 16                   "Semi-Tryptic",
     *<li> 17                  	"No Enzyme",
     *<li> 18                   "Chymotrypsin, no P rule",
     *<li> 19                   "Asp-N (DE)",
     *<li> 20                   "Glu-C (DE)" 
	 */
	public OMSSAEnzymes(){
		enzymeStrs = new String[] {"Trypsin",
                "Arg-C",
                "CNBr",
                "Chymotrypsin",
                "Formic Acid",
                "Lys-C",
                "Lys-C, no P rule",
                "Pepsin A",
                "Trypsin+CNBr",
                "Trypsin+Chymotrypsin",
                "Trypsin, no P rule",
                "Whole protein",
                "Asp-N",
                "Glu-C",
                "Asp-N+Glu-C",
                "Top-Down",
                "Semi-Tryptic",
                "No Enzyme",
                "Chymotrypsin, no P rule",
                "Asp-N (DE)",
                "Glu-C (DE)"};
	}
	
	/**
	 * Has not completed.
	 * 
	 * @param input
	 */
	public OMSSAEnzymes(String input){
		this();
	}
	
	/**
	 * Get the enzyme of the specific index
	 * 
	 * @param idx
	 * @return
	 */
	public Enzyme getEnzyme(int idx){
		if(idx < 0 || idx > this.enzymeStrs.length)
			throw new IllegalArgumentException("The index of enzyme is illegal: "+idx);
		
		return Enzyme.parse(this.getEnzymeString(this.enzymeStrs[idx]));
	}
	
	/**
	 * Get the formatted enzyme string. The format of
	 * the enzyme string is "name, sense_C (true or false), cleaveAt,
	 * notCleaveAt". e.g.
	 * <p>
	 * "Trypsin, true, KR, P" and "NoEnzyme, true(or false), -, -"
	 * 
	 * @see Enzyme
	 * @param enzymeName
	 * @return
	 */
	protected String getEnzymeString(String enzymeName){
		String enzyPattern = map.get(enzymeName);
		if(enzyPattern == null)
			throw new NullPointerException("Can find the enzymatic pattern for the enzyme: "+enzymeName);
		
		return enzyPattern;
	}
}
