/* 
 ******************************************************************************
 * File: MathUtil.java * * * Created on 08-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.math;

import java.nio.ByteBuffer;

/**
 * Utilities for math
 * 
 * @author Xinning
 * @version 0.1, 08-12-2009, 17:25:12
 */
public class MathUtil {

	/**
	 * Get the bytes for the input int value
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	/**
	 * Get the int value for the byte array
	 * 
	 * @param b
	 * @return
	 */
	public static int bytes2int(byte[] b) {
		//byte[] b=new byte[]{1,2,3,4};
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

	public static void main(String[] args) {
		int value = -1111111;
		
		ByteBuffer buffer = ByteBuffer.allocate(4);
		
		buffer.putInt(value);
		
		byte[] bbytes = buffer.array();
		
		byte[] abytes = int2bytes(value);
		
		for(int i=0; i<4 ; i++) {
			System.out.println(abytes[i] == bbytes[i]);
		}
		
	}

}
