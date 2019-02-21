/* 
 ******************************************************************************
 * File: IStamp.java * * * Created on 08-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.format;

/**
 * Stamp used to make sure the file is intact and has not been modified by
 * others. The stamp is commonly outputted as part of the file. Before the file
 * is readin, the stamp can be used for the validation.
 * 
 * @author Xinning
 * @version 0.1, 08-29-2008, 20:22:25
 */
public interface IStamp {
	
	/**
	 * The stamp used for the intact validation
	 * 
	 * @return
	 */
	public String getStamp();
	
	/**
	 * validate the stamp to make sure file is intact.
	 * 
	 * @return
	 */
	public boolean validateStamp(String stamp);
	
}
