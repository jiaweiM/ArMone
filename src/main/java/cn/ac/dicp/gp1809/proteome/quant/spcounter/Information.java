/*
 * *****************************************************************************
 * File: Information.java * * * Created on 07-24-2008
 * 
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import cn.ac.dicp.gp1809.util.gui.UIutilities;
import java.awt.Font;

/**
 * Information of the software
 * 
 * @author Xinning
 * @version 0.1, 07-24-2008, 16:43:32
 */
class Information extends JDialog {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTextArea jTextArea = null;

	/**
	 * This is the default constructor
	 */
	public Information() {
		this(null);
		this.setLocation(UIutilities.getProperLocation(null, this));
		this.setVisible(true);
	}
	
	public Information(JFrame parent) {
		super(parent, true);
		initialize();
		this.setLocation(UIutilities.getProperLocation(parent, this));
		this.setVisible(true);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(649, 556);
		this.setFont(new Font("Cambria", Font.PLAIN, 12));
		this.setContentPane(getJContentPane());
		this.setTitle("About");
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTextArea());
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setBounds(new Rectangle(0, 0, 640, 526));
			jTextArea.setEditable(false);
			jTextArea.setText("\n[Software Information]\n"+
					"Name\t: SpCounter (Semi-quantification by spectra counting)\n"+
					"Version\t: 2.3\n"+
					"Author\t: Xinning Jiang(vext@163.com)\n"+
					"Contact\t: Prof. Hanfa Zou (hanfazou@dicp.ac.cn)\n"+
					"Address\t:Dalian Institute of Chemical Physics\n"+
					"\t 457 zhongshan Road, Dalian 116023, China\n"+
					"Homepage\t: \n"+
					"Citation\t:\n\n"+
					
					
					"[Introduction]\n\n\n" +
					
					
					"\n\n[Licence]\n" +
					"All right reserved by Hanfa Zou & Xinning Jiang at Dalian Institute of Chemical Physics in China.\n" +
					"Free for academic and non commercial usage.\n" +
					"Commercial users please contact Prof. Hanfa Zou and get the licence.");
		}
		return jTextArea;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
