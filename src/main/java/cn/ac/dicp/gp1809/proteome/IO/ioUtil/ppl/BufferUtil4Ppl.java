/* 
 ******************************************************************************
 * File: BufferUtil4Ppl.java * * * Created on 05-23-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import java.io.IOException;

import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

/**
 * The reader using buffer util for ppl file
 * 
 * @author Xinning
 * @version 0.1.1, 09-09-2010, 16:47:11
 */
public class BufferUtil4Ppl implements IReader4Ppl {

	private static final long maximun_value = Integer.MAX_VALUE;
	private BufferUtil buffer;

	public BufferUtil4Ppl(String file) throws IOException {
		this.buffer = new BufferUtil(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.IReader4Ppl#close()
	 */
	@Override
	public void close() {
		this.buffer.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.IReader4Ppl#position(long)
	 */
	@Override
	public void position(long position) {
		if (position > maximun_value) {
			throw new IllegalArgumentException(
			        "The postion for seek is too big, please use RandomaccessFile reader!");
		}

		this.buffer.position((int) position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.IReader4Ppl#readLine()
	 */
	@Override
	public String readLine() {
		return this.buffer.readLine();
	}

}
