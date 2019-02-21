/* 
 ******************************************************************************
 * File: AboutDlg.java * * * Created on 05-26-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Trailing;

/**
 * The about dialog about the copy right
 * 
 * @author Xinning
 * @version 0.1, 05-26-2009, 16:41:12
 */
public class AboutDlg extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	private AboutPanel aboutPanel;
	private JButton jButton;
	private JPanel jPanel;
	
	public AboutDlg() {
		this(null);
	}

	public AboutDlg(Frame parent) {
		super(parent, true);
		initComponents();
		
		this.setLocationRelativeTo(parent);
	}

	private void initComponents() {
    	setTitle("About");
    	setFont(new Font("Dialog", Font.PLAIN, 12));
    	setBackground(new Color(204, 232, 207));
    	setModal(true);
    	setForeground(Color.black);
    	getContentPane().setLayout(new GroupLayout());
    	getContentPane().add(getJPanel(), new Constraints(new Bilateral(6, 7, 510), new Trailing(6, 39, 43, 372)));
    	getContentPane().add(getAboutPanel(), new Constraints(new Bilateral(6, 6, 511), new Bilateral(6, 47, 10, 354)));
    	setSize(614, 409);
    }

	private JPanel getJPanel() {
    	if (jPanel == null) {
    		jPanel = new JPanel();
    		jPanel.add(getJButton());
    	}
    	return jPanel;
    }

	private JButton getJButton() {
    	if (jButton == null) {
    		jButton = new JButton();
    		jButton.setText("OK");
    		jButton.addActionListener(this);
    	}
    	return jButton;
    }

	private AboutPanel getAboutPanel() {
    	if (aboutPanel == null) {
    		aboutPanel = new AboutPanel();
    	}
    	return aboutPanel;
    }
	
	/**
	 * Set about information
	 * 
	 * @param info
	 */
	public void setAboutInformation(String info) {
		this.getAboutPanel().setAboutInformation(info);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		
		Object obj = e.getSource();
		
		if(obj == this.getJButton()) {
			this.dispose();
			return ;
		}
    }
}
