/* 
 ******************************************************************************
 * File: GlycoStrucIdenPanel.java * * * Created on 2012-5-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

//VS4E -- DO NOT REMOVE THIS LINE!
public class NGlycoStrucIdenPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private NGlycoParaPanel glycoParaPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public NGlycoStrucIdenPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getGlycoParaPanel0(), new Constraints(new Leading(0, 460, 12, 12), new Leading(0, 240, 10, 10)));
		setSize(460, 400);
	}

	private NGlycoParaPanel getGlycoParaPanel0() {
		if (glycoParaPanel0 == null) {
			glycoParaPanel0 = new NGlycoParaPanel(true);
			glycoParaPanel0.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		}
		return glycoParaPanel0;
	}

}
