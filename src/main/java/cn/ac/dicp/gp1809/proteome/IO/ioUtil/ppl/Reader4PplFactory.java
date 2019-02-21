/* 
 ******************************************************************************
 * File: Reader4PplFactory.java * * * Created on 09-09-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import java.io.File;
import java.io.IOException;

/**
 * Factory for the reader creation of ppl temparory file
 * 
 * @author Xinning
 * @version 0.1, 09-09-2010, 17:19:27
 */
public class Reader4PplFactory {
	/**
	 * The max size of the file to create a buffer based reader
	 */
	public static final int MAX_SIZE_4_BYTEBUFFER = 300*1024*1024;
	
	/**
	 * 
	 * Create a reader for ppl file reading 
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static IReader4Ppl createReader(File file) throws IOException {
		long size = file.length();
		
		if(size > MAX_SIZE_4_BYTEBUFFER) {
			return new RandomAccessFile4Ppl(file.getAbsolutePath());
		}
		else {
			return new BufferUtil4Ppl(file.getAbsolutePath());
		}
	}
	
}
