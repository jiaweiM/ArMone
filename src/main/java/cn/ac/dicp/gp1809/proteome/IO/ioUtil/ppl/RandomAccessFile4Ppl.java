/* 
 ******************************************************************************
 * File: RandomAccessFile4Ppl.java * * * Created on 05-23-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * RandomAccessFile for peptide list writer
 * 
 * @author Xinning
 * @version 0.1, 05-23-2010, 23:09:29
 */
public class RandomAccessFile4Ppl implements IReader4Ppl {
	
	private RandomAccessFile raf;
	
	public RandomAccessFile4Ppl(String file) throws IOException {
		this.raf = new RandomAccessFile(file, "r");
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.IReader4Ppl#close()
	 */
	@Override
	public void close() {
		try {
	        this.raf.close();
        } catch (IOException e) {
	        System.err.println("File close exception, but it doesn't matter.");
        }
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.IReader4Ppl#position(int)
	 */
	@Override
	public void position(long position) {
		try {
	        this.raf.seek(position);
        } catch (IOException e) {
	        throw new RuntimeException(e);
        }
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.IReader4Ppl#readLine()
	 */
	@Override
	public String readLine() {
		try {
	        return this.raf.readLine();
        } catch (IOException e) {
	        throw new RuntimeException(e);
        }
	}

}
