/* 
 ******************************************************************************
 * File: MutilModCompFrame.java * * * Created on 2011-11-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

//VS4E -- DO NOT REMOVE THIS LINE!
public class MutilModCompFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable jTable0;
	private JScrollPane jScrollPane0;
	private JButton jButtonPre;
	private JButton jButtonNex;
	private JButton jButtonClose;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public MutilModCompFrame() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJScrollPane0(), new Constraints(new Bilateral(0, 0, 200), new Leading(0, 187, 10, 10)));
		add(getJButtonPre(), new Constraints(new Leading(166, 10, 10), new Leading(302, 10, 10)));
		add(getJButtonNex(), new Constraints(new Leading(283, 10, 10), new Leading(302, 12, 12)));
		add(getJButtonClose(), new Constraints(new Leading(401, 10, 10), new Leading(302, 12, 12)));
		setSize(540, 360);
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
		}
		return jButtonClose;
	}

	private JButton getJButtonNex() {
		if (jButtonNex == null) {
			jButtonNex = new JButton();
			jButtonNex.setText("jButton0");
		}
		return jButtonNex;
	}

	private JButton getJButtonPre() {
		if (jButtonPre == null) {
			jButtonPre = new JButton();
			jButtonPre.setText("jButton0");
		}
		return jButtonPre;
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
//			jScrollPane0.setBorder(BorderFactory.createTitledBorder(null, "Border Title", TitledBorder.LEADING, TitledBorder.CENTER, new Font("Dialog",
//					Font.BOLD, 12), new Color(51, 51, 51)));
			jScrollPane0.setViewportView(getJTable0());
		}
		return jScrollPane0;
	}

	private JTable getJTable0() {
		if (jTable0 == null) {
			jTable0 = new JTable();
			jTable0.setModel(new DefaultTableModel(new Object[][] { { "", "", }, { "", "", }, }, new String[] { "Title 0", "Title 1", }) {
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { Object.class, Object.class, };
	
				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}
			});
		}
		return jTable0;
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
				MutilModCompFrame frame = new MutilModCompFrame();
				frame.setDefaultCloseOperation(MutilModCompFrame.EXIT_ON_CLOSE);
				frame.setTitle("MutilModCompFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
