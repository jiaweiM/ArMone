/* 
 ******************************************************************************
 * File: IMaxQuantPeptide.java * * * Created on 2012-1-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * @author ck
 *
 * @version 2012-1-4, 14:29:38
 */
public interface IMaxQuantPeptide extends IPeptide {
	
	public double getScore();
	
	public double getPEP();
	
}
