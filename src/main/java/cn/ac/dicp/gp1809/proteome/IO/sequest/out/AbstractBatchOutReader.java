/* 
 ******************************************************************************
 * File: AbstractBatchOutReader.java * * * Created on 09-14-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.out;

import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * 
 * @author Xinning
 * @version 0.1, 09-14-2010, 18:54:31
 */
public abstract class AbstractBatchOutReader implements IBatchOutReader {
	
	private IDecoyReferenceJudger judger;

	@Override
	public IDecoyReferenceJudger getDecoyJudger() {
		return this.judger;
	}

	@Override
	public void setDecoyJudger(IDecoyReferenceJudger judger) {
		this.judger = judger;
	}

}
