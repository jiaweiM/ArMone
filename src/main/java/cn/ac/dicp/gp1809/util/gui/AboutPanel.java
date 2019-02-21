/* 
 ******************************************************************************
 * File: AboutPanel.java * * * Created on 05-26-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The about panel
 * 
 * @author Xinning
 * @version 0.1, 05-26-2009, 16:41:51
 */
public class AboutPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextArea jTextArea;
	private JScrollPane jScrollPane;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public AboutPanel() {
		initComponents();
	}

	private void initComponents() {
    	setSize(511, 354);
    	setLayout(new BorderLayout(0, 0));
    	add(getJScrollPane());
    }

	private JScrollPane getJScrollPane() {
    	if (jScrollPane == null) {
    		jScrollPane = new JScrollPane();
    		jScrollPane.setViewportView(getJTextArea());
    	}
    	return jScrollPane;
    }

	private JTextArea getJTextArea() {
    	if (jTextArea == null) {
    		jTextArea = new JTextArea();
    		jTextArea.setEditable(false);
    		jTextArea.setLineWrap(true);
    		jTextArea.setWrapStyleWord(true);
    	}
    	return jTextArea;
    }

	/**
	 * Set about information
	 */
	public void setAboutInformation(String info) {
		
		if(info != null) {
			this.getJTextArea().setText(info);
			this.getJTextArea().setCaretPosition(0);
		}
	}

	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			if (lnfClassname == null)
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
			        + " on this platform:" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("AboutPanel");
				AboutPanel content = new AboutPanel();
				content.setPreferredSize(content.getSize());
				frame.getContentPane().add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
