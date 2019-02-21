/* 
 ******************************************************************************
 * File:LFreePanel.java * * * Created on 2011-8-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import cn.ac.dicp.gp1809.proteome.quant.labelFree.gui.LFreeBatchWriterFrame;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

//VS4E -- DO NOT REMOVE THIS LINE!
public class LFreePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton jButtonGenerate;
	private JButton jButtonRepeat;
	private JButton jButtonLoad;
	private JButton jButtonTurnOver;
	
	private MyJFileChooser resultChooser;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public LFreePanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJButtonLoad(), new Constraints(new Leading(10, 100, 10, 10), new Leading(15, 100, 10, 10)));
		add(getJButtonRepeat(), new Constraints(new Leading(10, 100, 10, 10), new Leading(135, 100, 10, 10)));
		add(getJButtonGenerate(), new Constraints(new Leading(130, 100, 10, 10), new Leading(15, 100, 10, 10)));
		add(getJButtonTurnOver(), new Constraints(new Leading(130, 100, 10, 10), new Leading(135, 100, 10, 10)));
		setSize(360, 270);
	}

	protected JButton getJButtonTurnOver() {
		if (jButtonTurnOver == null) {
			jButtonTurnOver = new JButton();
			jButtonTurnOver.setText("<html><p align=\"center\">Protein turnover analysis</p></html>");
		}
		return jButtonTurnOver;
	}

	protected JButton getJButtonGenerate() {
		if (jButtonGenerate == null) {
			jButtonGenerate = new JButton();
			jButtonGenerate.setText("<html><p align=\"center\">Generate quantative result</p></html>");
		}
		return jButtonGenerate;
	}

	/**
	 * @param event
	 */
	protected void jButtonGenerateMouseMouseClicked(MouseEvent event) {
		LFreeBatchWriterFrame frame = new LFreeBatchWriterFrame();
		frame.setLocationRelativeTo(this);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

	protected JButton getJButtonRepeat() {
		if (jButtonRepeat == null) {
			jButtonRepeat = new JButton();
			jButtonRepeat.setText("<html><p align=\"center\">Repeat result comparison</p></html>");
		}
		return jButtonRepeat;
	}

	/**
	 * @param event
	 */
	protected void jButtonRepeatMouseMouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	protected JButton getJButtonLoad() {
		if (jButtonLoad == null) {
			jButtonLoad = new JButton();
			jButtonLoad.setText("<html><p align=\"center\">Open quantative result</p></html>");
		}
		return jButtonLoad;
	}
	
	/**
	 * @param event
	 */
	protected void jButtonLoadMouseMouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		int value = this.getResultChooser().showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION){
			File file = this.getResultChooser().getSelectedFile();
/*			LFreePairLoader loader;
			try {
				loader = new LFreePairLoader(file);
				loader.load(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/			
		}
	}

	private MyJFileChooser getResultChooser(){
		if( this.resultChooser==null){
			this.resultChooser = new MyJFileChooser();
			this.resultChooser.setFileFilter(new String[] { "pxml" },
	        	" Peptide XML file (*.pxml)");
		}
		return resultChooser;
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
				frame.setTitle("LFreePanel");
				LFreePanel content = new LFreePanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
