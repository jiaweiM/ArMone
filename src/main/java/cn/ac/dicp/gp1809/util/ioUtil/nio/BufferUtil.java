/*
 ******************************************************************************
 * File: BufferUtil.java * * * Created on 12-11-2007
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Utility used for ByteBuffer. Commonly, generating a byte buffer from a mapped
 * file can improve the efficiency of reading. This utility contains several
 * useful method for ByteBuffer.
 * 
 * @author Xinning
 * @version 0.2.2, 03-27-2010, 20:33:33
 */
public class BufferUtil {
	// line feed
	private static final byte LF = '\n';

	// carriage return
	private static final byte CR = '\r';

	/**
	 * 200M
	 */
	private static final int MAX_SIZE = 800 * 1024 * 1024;

	private ByteBuffer buffer;
	private int capcity;
	private int bsLinePosition = 0;// postion before reading the line

	// If the buffer from a channel
	private boolean needMap;
	private FileChannel channel;
	private FileInputStream finstream;

	/**
	 * Utilities for easy reading of the bytebuffer. The reading will start at
	 * the current position of the buffer.
	 * 
	 * @param _buffer
	 */
	public BufferUtil(ByteBuffer buffer) {
		if (buffer == null)
			throw new NullPointerException("The input buffer is null.");

		this.buffer = buffer;
		this.bsLinePosition = buffer.position();
		this.capcity = buffer.capacity();
	}

	/**
	 * Create a bufferUtil reader for the file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public BufferUtil(File file) throws IOException {

		System.gc();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (file.length() > Integer.MAX_VALUE) {
			throw new RuntimeException("Currently, cannot hold file with bytes more than " + Integer.MAX_VALUE);
		}

		if (file.length() > MAX_SIZE) {
			// throw new RuntimeException("Can only be used for file size
			// smaller than "+MAX_SIZE/1024/1024+"M");
		}

		this.finstream = new FileInputStream(file);
		this.channel = this.finstream.getChannel();
		this.capcity = this.finstream.available();

		this.buffer = this.channel.map(FileChannel.MapMode.READ_ONLY, 0, capcity);
		this.needMap = true;
	}

	/**
	 * Create a bufferUtil reader for the file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public BufferUtil(String filepath) throws IOException {
		this(new File(filepath));
	}

	/**
	 * @return the next line until the end (return null).
	 */
	public String readLine() {
		this.bsLinePosition = buffer.position();

		byte b;
		StringBuilder sb = new StringBuilder(100);

		while (buffer.position() < capcity) {
			// For mac and windows
			if ((b = buffer.get()) == CR) {

				if (buffer.position() != capcity) {
					// For windows platform
					if (buffer.get() != LF)
						buffer.position(buffer.position() - 1);
				}

				return sb.toString();
			}

			// for Unix/Linux
			if (b == LF) {
				return sb.toString();
			}

			sb.append((char) b);
		}

		if (sb.length() == 0)
			return null;

		return sb.toString();
	}

	/**
	 * Position of the buffer before reading the line. If the
	 * {@link #rollBackTheLine()} has been called, the position of the buffer,
	 * will be the same as this value. In other word, this value will not be
	 * affected by the call of {@link #rollBackTheLine()}
	 * 
	 * @return
	 */
	public int getBSLinePosition() {
		return this.bsLinePosition;
	}

	/**
	 * When a line was readin, if you don't want to use this line, and roll back
	 * the line, just use this method. This method is designed for the
	 * convenience in some conditions that one line must be readin before you
	 * know that this line is useless, and want to return back so that this line
	 * can be read in next time.
	 * <p>
	 * <b>Only the current read line can be rolled back. If no line has been
	 * read, do nothing.</b>
	 * 
	 */
	public void rollBackTheLine() {
		
		this.buffer.position(this.bsLinePosition);
	}

	/**
	 * Rewinds this buffer. The position is set to zero and the mark is
	 * discarded.
	 */
	public ByteBuffer rewind() {
		this.bsLinePosition = 0;
		return (ByteBuffer) buffer.rewind();
	}

	/**
	 * Get the underlying ByteBuffer for this reader.
	 * 
	 * @return
	 */
	public ByteBuffer getBuffer() {
		return this.buffer;
	}

	/**
	 * Get the next int value
	 * 
	 * @return
	 */
	public int getInt() {
		return this.buffer.getInt();
	}

	/**
	 * Get the next float value
	 * 
	 * @return
	 */
	public float getFloat() {
		return this.buffer.getFloat();
	}

	/**
	 * Get the next double value
	 * 
	 * @return
	 */
	public double getDouble() {
		return this.buffer.getDouble();
	}

	/**
	 * Get the next short value
	 * 
	 * @return
	 */
	public short getShort() {
		return this.buffer.getShort();
	}

	/**
	 * @return this buffer's position.
	 */
	public int position() {
		return buffer.position();
	}

	/**
	 * Move to current position and prepare for reading
	 * 
	 * @param newpos The new position value; must be non-negative and no larger
	 *            than the current limit
	 */
	public void position(int newpos) {
		buffer.position(newpos);
		this.bsLinePosition = newpos;
	}

	/**
	 * @return the capacity of the buffer
	 */
	public int length() {
		return buffer.capacity();
	}

	/**
	 * Close the BufferUtil reader if it is closeable.
	 */
	public void close() {

		if (this.buffer instanceof MappedByteBuffer) {
			((MappedByteBuffer) this.buffer).force();
		}

		this.buffer = null;

		if (this.needMap) {
			try {
				if (this.channel != null) {
					this.channel.close();
					this.channel = null;
				}

				if (this.finstream != null) {
					this.finstream.close();
					this.finstream = null;
				}
			} catch (Exception e) {
				System.err.println("Error when closing the buffer.");
			}
		}

		System.gc();
	}
}