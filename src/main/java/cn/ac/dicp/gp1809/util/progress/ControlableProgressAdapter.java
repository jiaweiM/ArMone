/* 
 ******************************************************************************
 * File: ControlableProgressAdapter.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.progress;

/**
 * The adapter
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 16:53:39
 */
public class ControlableProgressAdapter implements IControlableProgress {

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.IControlable#begin()
	 */
	@Override
	public void begin() {
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.IControlable#currentState()
	 */
	@Override
	public int currentState() {
		return STATES_UNKOWN;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.IControlable#end()
	 */
	@Override
	public void end() {
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.IControlable#pause()
	 */
	@Override
	public void pause() {
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.IControlable#resume()
	 */
	@Override
	public void resume() {
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.IProgress#completedPercent()
	 */
	@Override
	public float completedPercent() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.IProgress#currentAction()
	 */
	@Override
	public String currentAction() {
		return "Process";
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.IProgress#isIndeterminate()
	 */
	@Override
	public boolean isIndeterminate() {
		return true;
	}

}
