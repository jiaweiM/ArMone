/* 
 ******************************************************************************
 * File: ProTurnoverTask.java * * * Created on 2011-11-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover;

import java.io.File;

import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 *
 * @version 2011-11-14, 10:22:35
 */
public class ProTurnoverTask implements ITask {

	private File [] files;
	private File output;
	
	public ProTurnoverTask(File [] files){
		this.files = files;
	}
	
	public Object [][] getObjs(){
		Object [][] objs = new Object[files.length][];
//		ArrayList <File> [] filelist = new ArrayList [files.length];
		for(int i=0;i<files.length;i++){
			
//			filelist[i] = new ArrayList <File>();
			File [] fs = files[i].listFiles();
			objs[i] = new Object [fs.length];
			for(int j=0;j<fs.length;j++){
				objs[i][j] = fs[j];
//				filelist[i].add(fs[j]);
			}
		}
		return objs;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
