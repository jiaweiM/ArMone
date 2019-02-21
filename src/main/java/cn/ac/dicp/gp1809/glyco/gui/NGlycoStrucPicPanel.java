/* 
 ******************************************************************************
 * File: GlycoStrucPicPanel.java * * * Created on 2012-5-18
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import java.awt.Image;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.glyco.drawjf.GlycoStrucDrawer;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;

//VS4E -- DO NOT REMOVE THIS LINE!
public class NGlycoStrucPicPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel jLabel1;
	private GlycoStrucDrawer drawer;

	public NGlycoStrucPicPanel() {
		
		initComponents();
		try {
			this.drawer = new GlycoStrucDrawer();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initComponents() {
		
		setLayout(new GroupLayout());
		add(getJLabel1(), new Constraints(new Bilateral(0, 0, 10, 10), new Bilateral(0, 0, 10, 10)));
		setSize(535, 340);
	}

	private JLabel getJLabel1() {
		
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
   	}
    	return jLabel1;
    }
	
	public void draw(GlycoTree tree){
		
		Image image = this.drawer.draw(tree).getScaledInstance(450, 360, Image.SCALE_SMOOTH);
		this.getJLabel1().setIcon(new ImageIcon(image));
		this.repaint();
		this.updateUI();
	}
	
}
