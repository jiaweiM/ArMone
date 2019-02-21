/* 
 ******************************************************************************
 * File: IPCTest.java * * * Created on 2014��5��20��
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.test;

import java.util.Iterator;
import java.util.TreeSet;

import ipc.IPC;
import ipc.Peak;
import ipc.IPC.Options;
import ipc.IPC.Results;

/**
 * @author ck
 *
 */
public class IPCTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		IPC ipc = new IPC();
		Options ipcOptions = new Options();
		ipcOptions.addPeptide("SSSSS");
		ipcOptions.setCharge(1);
		ipcOptions.setFastCalc(4);
		
		Results res = ipc.execute(ipcOptions);
		TreeSet<ipc.Peak> isotopepeaks = res.getPeaks();
		Iterator<ipc.Peak> isotopepeaksit = isotopepeaks.iterator();
		while (isotopepeaksit.hasNext()) {
			Peak pp = isotopepeaksit.next();
			System.out.println(pp.getMass()+"\t"+pp.getP());
		}
		
	}

}
