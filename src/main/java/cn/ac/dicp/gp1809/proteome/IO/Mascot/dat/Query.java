/* 
 ******************************************************************************
 * File: Query.java * * * Created on 11-14-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

/**
 * A Query to Mascot including the responded peptide hit informations
 * 
 * @author Xinning
 * @version 0.1, 11-14-2008, 15:00:53
 */
public class Query {
	
	private MascotScanDta dta;
	
	private QueryResult result;
	
	public Query(){
		
	}
	
	/**
     * @param dta
     * @param result
     */
    public Query(MascotScanDta dta, QueryResult result) {
	    this.dta = dta;
	    this.result = result;
    }

	/**
     * @return the dta
     */
    public final MascotScanDta getDta() {
    	return dta;
    }

	/**
     * @param dta the dta to set
     */
    public final void setDta(MascotScanDta dta) {
    	this.dta = dta;
    }

	/**
     * @return the result
     */
    public final QueryResult getResult() {
    	return result;
    }

	/**
     * @param result the result to set
     */
    public final void setResult(QueryResult result) {
    	this.result = result;
    }
}
