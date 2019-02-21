/* 
 ******************************************************************************
 * File: SymModFrame.java * * * Created on 2011-5-6
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.fragInfo;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

//VS4E -- DO NOT REMOVE THIS LINE!
public class SymModFrame extends JFrame {

	private JPanel jPanel0;
	private JTable jTable;
	private JScrollPane jScrollPane0;
	
	private static final long serialVersionUID = 1L;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public SymModFrame() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJScrollPane0(), new Constraints(new Leading(8, 300, 10, 10), new Leading(7, 280, 10, 10)));
		setSize(320, 300);
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJTable());
		}
		return jScrollPane0;
	}

	private JPanel getJPane0() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			jPanel0.add(getJScrollPane0());
		}
		return jPanel0;
	}

	public JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			jTable.setModel(new DefaultTableModel(new Object[][] { { '@', null, }, { '#', null, },
					{ '$', null, }, { '%', null, }, { '^', null, },
					{ null, null, }, { null, null, }, { null, null, },
					{ null, null, }, }, new String[] { "Symbol", "Mono_mass" }) {
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, };
	
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}
			});
		}
		return jTable;
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
				SymModFrame frame = new SymModFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("SymModFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
