/* 
 ******************************************************************************
 * File: FileCopy.java * * * Created on 2014年5月6日
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author ck
 *
 * @version 2014年5月6日, 下午4:03:04
 */
public class FileCopy {
	
	public static void Copy(File source, File target) throws IOException { 
		FileChannel fcin = new FileInputStream(source).getChannel();
		FileChannel fcout = new FileOutputStream(target).getChannel();
        fcin.transferTo(0, fcin.size(), fcout);
        fcin.close();
        fcout.close();
	}
	
	public static void CopyAndDelete(File source, File target) throws IOException { 
		FileChannel fcin = new FileInputStream(source).getChannel();
		FileChannel fcout = new FileOutputStream(target).getChannel();
        fcin.transferTo(0, fcin.size(), fcout);
        fcin.close();
        fcout.close();
        source.delete();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
