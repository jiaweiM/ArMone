/* 
 ******************************************************************************
 * File: PhosStatisticFrame.java * * * Created on 05-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.proteome.gui.PeptideListPagedRowGettor;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-04-2009, 20:52:00
 */
public class PhosStatisticFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private final PeptideListPagedRowGettor getter;
	
	private PhosStatisticPanel phosStatisticPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PhosStatisticFrame() {
		this(null);
	}
	
	public PhosStatisticFrame(PeptideListPagedRowGettor getter) {
		this.getter= getter;
		initComponents();
		
		this.setResizable(false);
	}

	private void initComponents() {
		setTitle("Site statistic");
		setResizable(false);
		setLayout(new GroupLayout());
		add(getPhosStatisticPanel0(), new Constraints(new Bilateral(6, 6, 520), new Bilateral(6, 6, 10)));
		setSize(537, 455);
	}

	private PhosStatisticPanel getPhosStatisticPanel0() {
    	if (phosStatisticPanel0 == null) {
    		phosStatisticPanel0 = new PhosStatisticPanel(this.getter);
    	}
    	return phosStatisticPanel0;
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

	/**
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PhosStatisticFrame frame = new PhosStatisticFrame();
				frame
				        .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("PhosStatisticFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
