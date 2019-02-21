/* 
 ******************************************************************************
 * File: DtaFormatConverTask.java * * * Created on 05-20-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.format;

import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaWriter;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-20-2009, 15:26:34
 */
public class DtaFormatConverTask implements ITask {

	
	
	public DtaFormatConverTask(IBatchDtaReader reader, IBatchDtaWriter writer) {
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
