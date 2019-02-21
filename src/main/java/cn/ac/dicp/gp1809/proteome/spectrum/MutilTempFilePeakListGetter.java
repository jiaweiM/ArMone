/* 
 ******************************************************************************
 * File: MutilTempFilePeakListGetter.java * * * Created on 2012-7-10
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

/**
 * @author ck
 *
 * @version 2012-7-10, 13:45:13
 */
public class MutilTempFilePeakListGetter {

	private HashMap<Integer, Integer> indexmap;
	private DataOutputStream ostream;

	private File file;
	private ByteBuffer mappedBuffer;
	private FileInputStream bufferStream;
	private FileChannel bufferChannel;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ByteBuffer buffer1 = ByteBuffer.allocateDirect(500000000);
//		ByteBuffer buffer2 = ByteBuffer.allocateDirect(388659144);
	}

}
