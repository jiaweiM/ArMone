/* 
 ******************************************************************************
 * File: IMaxQuantPeptideFormat.java * * * Created on 2012-1-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * @author ck
 *
 * @version 2012-1-4, 14:28:06
 */
public interface IMaxQuantPeptideFormat<Pep extends IMaxQuantPeptide> extends
	IPeptideFormat<Pep> {
	

	/**
	 * The name indicating score
	 */
	public static final String Score = "Score";

	/**
	 * The name indicating PEP
	 */
	public static final String PEP = "PEP";
}
