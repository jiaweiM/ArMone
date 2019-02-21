/* 
 ******************************************************************************
 * File: PepHitParser2_0.java * * * Created on 11-12-2008
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
 * peptide hit parser of mascot v2.0
 * 
 * @author Xinning
 * @version 0.1, 11-12-2008, 09:40:14
 */
class PepHitParser2_0 extends AbstractPepHitParser  {

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.Mascot.dat1.IPepHitParser#parse(java.lang.String[])
	 */
	@Override
	public PeptideHit parse(String[] hit_str) {
		return null;
	}

	@Override
    public int getNumLineperHit() {
	    return 1;
    }
	
	

}
