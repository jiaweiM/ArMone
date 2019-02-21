/* 
 ******************************************************************************
 * File: MyJFileChooser.java * * * Created on 07-30-2008
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

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.HashSet;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

/**
 * An easy to use JFileChooser. The current directory will be automatically
 * selected.
 * 
 * <p>
 * Changes:
 * <li>0.1.1, 03-12-2009: Add the method {@link #setFileFilter(FileFilter)}
 * 
 * @author Xinning
 * @version 0.1.2, 03-01-2010, 09:40:36
 */
public class MyJFileChooser extends JFileChooser {

	/**
     * 
     */
	private static final long serialVersionUID = 8839043864133068165L;

	/**
	 * Constructs a JFileChooser pointing to the user's default directory. This
	 * default depends on the operating system. It is typically the "My
	 * Documents" folder on Windows, and the user's home directory on Unix.
	 */
	public MyJFileChooser() {
		super();
	}

	/**
	 * Constructs a JFileChooser using the given current directory and
	 * FileSystemView.
	 * 
	 * @param currentDirectory
	 * @param fsv
	 */
	public MyJFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
	}

	/**
	 * Constructs a JFileChooser using the given File as the path. Passing in a
	 * null file causes the file chooser to point to the user's default
	 * directory. This default depends on the operating system. It is typically
	 * the "My Documents" folder on Windows, and the user's home directory on
	 * Unix.
	 * 
	 * @param currentDirectory
	 *            a File object specifying the path to a file or directory
	 */
	public MyJFileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	/**
	 * Constructs a JFileChooser using the given FileSystemView.
	 * 
	 * @param fsv
	 */
	public MyJFileChooser(FileSystemView fsv) {
		super(fsv);
	}

	/**
	 * Constructs a JFileChooser using the given current directory path and
	 * FileSystemView.
	 * 
	 * @param currentDirectoryPath
	 * @param fsv
	 */
	public MyJFileChooser(String currentDirectoryPath, FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}

	/**
	 * Constructs a JFileChooser using the given path. Passing in a null string
	 * causes the file chooser to point to the user's default directory. This
	 * default depends on the operating system. It is typically the "My
	 * Documents" folder on Windows, and the user's home directory on Unix.
	 * 
	 * @param currentDirectoryPath
	 *            a String giving the path to a file or directory
	 */
	public MyJFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	/*
	 * Renew the current directory (non-Javadoc)
	 * 
	 * @see javax.swing.JFileChooser#showOpenDialog(java.awt.Component)
	 */
	@Override
	public int showOpenDialog(Component parent) throws HeadlessException {
		int val = super.showOpenDialog(parent);

		if (val == APPROVE_OPTION) {
			this.setCurrentDirectory(this.getCurrentDirectory());
		}

		return val;
	}

	/*
	 * Renew the current directory (non-Javadoc)
	 * 
	 * @see javax.swing.JFileChooser#showOpenDialog(java.awt.Component)
	 */
	@Override
	public int showSaveDialog(Component parent) throws HeadlessException {
		int val = super.showSaveDialog(parent);

		if (val == APPROVE_OPTION)
			this.setCurrentDirectory(this.getCurrentDirectory());

		return val;
	}

	/*
	 * Renew the current directory (non-Javadoc)
	 * 
	 * @see javax.swing.JFileChooser#showOpenDialog(java.awt.Component)
	 */
	@Override
	public int showDialog(Component parent, String approveButtonText)
	        throws HeadlessException {
		int val = super.showDialog(parent, approveButtonText);

		if (val == APPROVE_OPTION)
			this.setCurrentDirectory(this.getCurrentDirectory());

		return val;
	}

	/**
	 * Set the file name filter. This method equals
	 * {@link #setFileFilter(FileFilter)} with the same
	 * name filter and just provide an easy way. If you want to remove the
	 * filters, just leave the two parameters as null.
	 * 
	 * @since 0.1.1
	 * @param extensions
	 * @param description
	 */
	public void setFileFilter(String[] extensions, String description) {
		
		FileFilter[] filters = this.getChoosableFileFilters();
		//Remove all other filters except the all file filter
		for(int i=1; i < filters.length ; i++){
			this.removeChoosableFileFilter(filters[i]);
		}

		if (extensions != null && extensions.length != 0) {
			final HashSet<String> extSet = new HashSet<String>();
			for (String ext : extensions)
				extSet.add(ext.toLowerCase());
			final String des;
			if (description == null) {
				des = extSet.toString();
			} else
				des = description;
			
			final String[] parsedExts = extSet.toArray(new String[extSet.size()]);

			this.addChoosableFileFilter(new FileFilter() {

				@Override
				public boolean accept(File f) {
					if (f.isDirectory())
						return true;
					String lowname = f.getName().toLowerCase();
					
					for(String ext : parsedExts) {
						if(lowname.endsWith(ext))
							return true;
					}
					
					return false;
				}

				@Override
				public String getDescription() {
					return des;
				}
			});
		}
	}
}
