/* 
 ******************************************************************************
 * File:PeptideListViewer2.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

//VS4E -- DO NOT REMOVE THIS LINE!
public class PeptideListViewer2 extends JPanel {

	private static final long serialVersionUID = 1L;
	private PeptideListPagedRowGetter2 getter;
	private PeptideListViewerPanel2 peptideListViewerPanel20;
	private JToolBar jToolBar0;
	private JButton jButtonExport;
	private static final Insets margins = new Insets(0, 0, 0, 0);

	private JButton jButton0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PeptideListViewer2() {
		initComponents();
	}
	
	public PeptideListViewer2(PeptideListPagedRowGetter2 getter) {
		this.getter = getter;
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getPeptideListViewerPanel20(), new Constraints(new Bilateral(0, 0, 0), new Leading(25, 800, 10, 10)));
		add(getJToolBar0(), new Constraints(new Bilateral(0, 0, 0), new Leading(0, 25, 6, 6)));
		setSize(1000, 800);
	}

	private JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
			jButton0.setText("jButton0");
			jButton0.setMargin(margins);
//			jButton0.setDefaultCapable(false);
		}
		return jButton0;
	}

	private JButton getJButtonExport() {
		if (jButtonExport == null) {
			jButtonExport = new JButton();
			jButtonExport.setText("Export");
			jButtonExport.setMargin(margins);
//			jButtonExport.setBorder(null);
		}
		return jButtonExport;
	}

	private JToolBar getJToolBar0() {
		if (jToolBar0 == null) {
			jToolBar0 = new JToolBar();
			jToolBar0.add(getJButtonExport());
			jToolBar0.add(getJButton0());
		}
		return jToolBar0;
	}

	private PeptideListViewerPanel2 getPeptideListViewerPanel20() {
		if (peptideListViewerPanel20 == null) {
			if(getter!=null){
				peptideListViewerPanel20 = new PeptideListViewerPanel2(getter);
			}else{
				peptideListViewerPanel20 = new PeptideListViewerPanel2();
			}
			
			peptideListViewerPanel20.setBorder(BorderFactory.createCompoundBorder(null, null));
		}
		return peptideListViewerPanel20;
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
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("PeptideListViewer2");
				PeptideListViewer2 content = new PeptideListViewer2();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/**
	 * @return
	 */
	public JButton getJButtonClose() {
		// TODO Auto-generated method stub
		return this.peptideListViewerPanel20.getJButtonClose();
	}

}
