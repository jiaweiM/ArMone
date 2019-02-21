/* 
 ******************************************************************************
 * File: MyRunableComponent.java * * * Created on 07-31-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.progress;


/**
 * This is a controlable Runnable component, UnFinished
 * 
 * @author Xinning
 * @version 0.1, 07-31-2008, 16:22:09
 */
public class MyRunnableComponent implements Runnable, IControlable {

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.IControlable#begin()
	 */
	@Override
	public void begin() {
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.IControlable#currentState()
	 */
	@Override
	public int currentState() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.IControlable#end()
	 */
	@Override
	public void end() {
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.IControlable#pause()
	 */
	@Override
	public void pause() {
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.IControlable#resume()
	 */
	@Override
	public void resume() {
	}

}
