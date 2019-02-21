/* 
 ******************************************************************************
 * File: JTextAreaPrintStream.java * * * Created on 03-26-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import java.io.PrintStream;

import javax.swing.JTextArea;

/**
 * Redirect the print stream into a JTextArea to see what was happened while program running.
 * 
 * @author Xinning
 * @version 0.1, 03-26-2008, 16:10:07
 */
public class JTextAreaPrintStream extends PrintStream {

	private JTextArea tarea;
	
	public JTextAreaPrintStream(JTextArea tarea){
		super(System.out);//No use
		this.tarea = tarea;
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(boolean)
	 */
	@Override
	public void print(boolean b) {
		this.print(String.valueOf(b));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(char)
	 */
	@Override
	public void print(char c) {
		this.print(String.valueOf(c));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(char[])
	 */
	@Override
	public void print(char[] s) {
		this.print(new String(s));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(double)
	 */
	@Override
	public void print(double d) {
		this.print(String.valueOf(d));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(float)
	 */
	@Override
	public void print(float f) {
		this.print(String.valueOf(f));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(int)
	 */
	@Override
	public void print(int i) {
		this.print(String.valueOf(i));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(long)
	 */
	@Override
	public void print(long l) {
		this.print(String.valueOf(l));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(java.lang.Object)
	 */
	@Override
	public void print(Object obj) {
		tarea.append(obj.toString());
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(java.lang.String)
	 */
	@Override
	public void print(String s) {
		tarea.append(s);
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println()
	 */
	@Override
	public void println() {
		this.println("");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(boolean)
	 */
	@Override
	public void println(boolean x) {
		this.println(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(char)
	 */
	@Override
	public void println(char x) {
		this.println(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(char[])
	 */
	@Override
	public void println(char[] x) {
		this.println(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(double)
	 */
	@Override
	public void println(double x) {
		this.println(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(float)
	 */
	@Override
	public void println(float x) {
		this.println(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(int)
	 */
	@Override
	public void println(int x) {
		this.println(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(long)
	 */
	@Override
	public void println(long x) {
		this.println(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(java.lang.Object)
	 */
	@Override
	public void println(Object x) {
		String s = "";
		if(x!=null)
			s = x.toString();
		this.println(s);
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(java.lang.String)
	 */
	@Override
	public void println(String x) {
		if(x==null)
			x = "";
		this.print(x+"\n");
	}
}
