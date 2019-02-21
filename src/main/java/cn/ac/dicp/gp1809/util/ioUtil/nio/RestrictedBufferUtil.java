/* 
 ******************************************************************************
 * File: RestrictedBufferUtil.java * * * Created on 11-14-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.nio;

import java.nio.ByteBuffer;

/**
 * The restricted buffer util reader in which the index of byte_from and byte_to
 * can be set. And the number of bytes read will be restricted in this region
 * regardless the bytes out of this region.
 * 
 * @author Xinning
 * @version 0.1, 11-14-2008, 15:33:21
 */
public class RestrictedBufferUtil extends BufferUtil {

	private int from;
	private int to = -1;
	private int len;

	/**
	 * Restrict the start and the end of bytes for reading. The reading will
	 * begin at the byte_from, and end at the limitation of byte_to
	 * 
	 * @param _buffer
	 * @param byte_to
	 *            the restriction of the end limitation
	 */
	public RestrictedBufferUtil(ByteBuffer _buffer, int byte_from, int byte_to) {
		super(_buffer);

		this.len = byte_to - byte_from;
		if (len < 0)
			throw new IllegalArgumentException("The index of from_byte is "
			        + "bigger than to_byte.");

		this.from = byte_from;
		this.to = byte_to;

		super.position(byte_from);
	}

	/**
	 * Restrict the end of bytes for reading. The reading will begin at the
	 * current position of the input buffer.
	 * 
	 * @param _buffer
	 * @param byte_to
	 *            the restriction of the end limitation
	 */
	public RestrictedBufferUtil(ByteBuffer _buffer, int byte_to) {
		this(_buffer, _buffer.position(), byte_to);
	}

	@Override
	public String readLine() {
		String line = super.readLine();

		if (this.position() <= to) {
			return line;
		} else {

			super.position(to);

			int return_byte = this.to - this.getBSLinePosition();

			if (return_byte == 0)
				return null;

			if (line != null && line.length() > return_byte) {
				return line.substring(0, return_byte);
			}

			return line;
		}
	}

	/**
	 * Rewinds this buffer. The position is set to the start of restriction and
	 * the mark is discarded.
	 */
	@Override
	public ByteBuffer rewind() {
		this.getBuffer().rewind();
		this.position(this.from);
		return null;
	}

	/**
	 * Move to current position and prepare for reading
	 * 
	 * @param newpos
	 *            The new position value; must be non-negative and no larger
	 *            than the current limit
	 */
	@Override
	public void position(int newpos) {
		if (newpos > this.to || newpos < this.from) {
			throw new IllegalArgumentException("The new position \"" + newpos
			        + "\" is out of restriction [" + from + ", " + to + "]");
		}
		super.position(newpos);
	}

	/**
	 * @return the capacity of the buffer
	 */
	@Override
	public int length() {
		return this.len;
	}

	/**
	 * Do nothing
	 */
	@Override
	public void close() {
		return;
	}
	
	/**
	 * The start restriction
	 * 
	 * @return
	 */
	public int getFromByte(){
		return this.from;
	}
	
	/**
	 * The end restriction
	 * 
	 * @return
	 */
	public int getToByte(){
		return this.to;
	}
	
	public static void main(String[] args){
		
	}
}
