/* 
 ******************************************************************************
 * File: AbstractSpectrumDataset.java * * * Created on 04-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf;

import cn.ac.dicp.gp1809.drawjf.AbstractJFDataset;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-13-2009, 15:12:00
 */
public abstract class AbstractSpectrumDataset extends AbstractJFDataset
        implements ISpectrumDataset {

	protected AbstractSpectrumDataset() {
		this.setXlabel(xlabel);
		this.setYlabel(ylabel);
	}
	
}
