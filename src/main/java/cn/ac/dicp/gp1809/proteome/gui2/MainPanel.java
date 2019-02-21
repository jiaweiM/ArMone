/* 
 ******************************************************************************
 * File:PplPanel.java * * * Created on 2011-8-3
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.glyco.gui.NGlycoPanel;
import cn.ac.dicp.gp1809.glyco.gui.OGlycoPanel;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import javax.swing.GroupLayout.Alignment;


public class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton jButtonCreate;
	private JButton jButtonLoading;
	private JButton jButtonPenn;
	private JButton jButtonApivase;
	private JPanel jPopPanel;
	private MyJFileChooser pplChooser;
	private PeptideListViewerPanel2 pepViewPanel;
	private QuanPanel quanPanel0;
	private JPanel jPanelIden;
	private NGlycoPanel nGlycoPanel0;
	private OGlycoPanel oGlycoPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public MainPanel() {
		initComponents();
	}

	private void initComponents() {
		setSize(1000, 750);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(50)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getJPanelIden(), javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(70)
							.addComponent(getQuanPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getOGlycoPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(70)
							.addComponent(getNGlycoPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJPanelIden(), javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getQuanPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(19)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getOGlycoPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getNGlycoPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
		);
		setLayout(groupLayout);
	}

	private OGlycoPanel getOGlycoPanel0() {
		if (oGlycoPanel0 == null) {
			oGlycoPanel0 = new OGlycoPanel();
			oGlycoPanel0.setBorder(BorderFactory.createTitledBorder(null, "OGlyco Analysis", TitledBorder.LEADING, TitledBorder.CENTER, new Font("SansSerif",
					Font.BOLD, 12), new Color(59, 59, 59)));
		}
		return oGlycoPanel0;
	}
	
	private NGlycoPanel getNGlycoPanel0() {
		if (nGlycoPanel0 == null) {
			nGlycoPanel0 = new NGlycoPanel();
			nGlycoPanel0.setBorder(BorderFactory.createTitledBorder(null, "NGlyco Analysis", TitledBorder.LEADING, TitledBorder.CENTER, new Font("SansSerif",
					Font.BOLD, 12), new Color(59, 59, 59)));
		}
		return nGlycoPanel0;
	}

	private JPanel getJPanelIden() {
		if (jPanelIden == null) {
			jPanelIden = new JPanel();
			jPanelIden.setBorder(BorderFactory.createTitledBorder(null, "Validation of identification", TitledBorder.LEADING, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12),
					new Color(59, 59, 59)));
			javax.swing.GroupLayout gl_jPanelIden = new javax.swing.GroupLayout(jPanelIden);
			gl_jPanelIden.setHorizontalGroup(
				gl_jPanelIden.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_jPanelIden.createSequentialGroup()
						.addGap(30)
						.addGroup(gl_jPanelIden.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_jPanelIden.createSequentialGroup()
								.addComponent(getJButtonCreate(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(60)
								.addComponent(getJButtonLoading(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_jPanelIden.createSequentialGroup()
								.addComponent(getJButtonPenn(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(60)
								.addComponent(getJButtonApivase(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
			);
			gl_jPanelIden.setVerticalGroup(
				gl_jPanelIden.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_jPanelIden.createSequentialGroup()
						.addGap(15)
						.addGroup(gl_jPanelIden.createParallelGroup(Alignment.LEADING)
							.addComponent(getJButtonCreate(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addComponent(getJButtonLoading(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(45)
						.addGroup(gl_jPanelIden.createParallelGroup(Alignment.LEADING)
							.addComponent(getJButtonPenn(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addComponent(getJButtonApivase(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
			);
			jPanelIden.setLayout(gl_jPanelIden);
		}
		return jPanelIden;
	}

	private QuanPanel getQuanPanel0() {
		if (quanPanel0 == null) {
			quanPanel0 = new QuanPanel();
			quanPanel0.setBorder(BorderFactory.createTitledBorder(null, "Quantification", TitledBorder.LEADING, TitledBorder.CENTER, new Font("SansSerif",
					Font.BOLD, 12), new Color(59, 59, 59)));
		}
		return quanPanel0;
	}

	protected JButton getJButtonLabelMerge() {
		return this.quanPanel0.getJButtonLabelMerge();
	}

	protected JButton getJButtonLabelRepeat() {
		return this.quanPanel0.getJButtonLabelRepeat();
	}
	
	protected JButton getJButtonLabelTurnover() {
		return this.quanPanel0.getJButtonLabelTurnover();
	}

	protected JButton getJButtonLabelLoad() {
		return this.quanPanel0.getJButtonLabelLoad();
	}
	
	protected JButton getJButtonGlycoLabelLoad() {
		return this.nGlycoPanel0.getJButtonLoadLabelQuanGlyco();
	}
	
	protected JButton getJButtonLabelFreeGenerate() {
		return this.quanPanel0.getJButtonLabelFreeGenerate();
	}
	
	protected JButton getJButtonLabelFreeLoad() {
		return this.quanPanel0.getJButtonLabelFreeLoad();
	}
	
	protected JButton getJButtonLabelFreeRepeat() {
		return this.quanPanel0.getJButtonLabelFreeRepeat();
	}
	
	protected JButton getJButtonLabelFreeTurnover() {
		return this.quanPanel0.getJButtonLabelFreeTurnover();
	}
	
	protected JButton getJButtonLabelSpectralCount() {
		return this.quanPanel0.getJButtonSPC();
	}
	
	protected JButton getJButtonGlycoStrucIden(){
		return this.nGlycoPanel0.getJButtonGlycoStrucIden();
	}
	
	protected JButton getJButtonLoadGlycoStruc(){
		return this.nGlycoPanel0.getJButtonLoadGlycoStruc();
	}
	
	protected JButton getJButtonLoadGlycoMatch(){
		return this.nGlycoPanel0.getJButtonLoadGlycoMatch();
	}
	
	protected JButton getJButtonOGlycoSpectra(){
		return this.oGlycoPanel0.getBtnDeglyco();
	}
	
	protected JButton getJButtonOGlycoValidate(){
		return this.oGlycoPanel0.getBtnGlycoform();
	}

	protected JButton getJButtonLoading() {
		if (jButtonLoading == null) {
			jButtonLoading = new JButton();
			jButtonLoading.setText("<html><p align=\"center\">Peptide list loading</p></html>");
		}
		return jButtonLoading;
	}
	
	private MyJFileChooser getPplChooser() {
		if (this.pplChooser == null) {
			this.pplChooser = new MyJFileChooser();
			this.pplChooser.setMultiSelectionEnabled(true);
			this.pplChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			this.pplChooser.setFileFilter(new String[] { "ppl" },
			        "Peptide list file (*.ppl)");
		}
		return pplChooser;
	}

	protected JButton getJButtonCreate() {
		if (jButtonCreate == null) {
			jButtonCreate = new JButton();
			jButtonCreate.setText("<html><p align=\"center\">Peptide list creation</p></html>");
		}
		return jButtonCreate;
	}
	
	protected JButton getJButtonPenn() {
		if (jButtonPenn == null) {
			jButtonPenn = new JButton();
			jButtonPenn.setText("<html><p align=\"center\">Peptide probability calculate (PENN)</p></html>");
		}
		return jButtonPenn;
	}
	
	protected JButton getJButtonApivase() {
		if (jButtonApivase == null) {
			jButtonApivase = new JButton();
			jButtonApivase.setText("<html><p align=\"center\">MS2/MS3 strategy (APIVASE)</p></html>");
		}
		return jButtonApivase;
	}
	
	protected JPanel getJPopPanel(){
		return this.jPopPanel;
	}
	
	protected PeptideListViewerPanel2 getViewPanel(){
		return pepViewPanel;
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
				frame.setTitle("PplPanel");
				MainPanel content = new MainPanel();
				content.setPreferredSize(content.getSize());
				frame.getContentPane().add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
	protected boolean load(){

		int value = this.getPplChooser().showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION){
			File [] files = this.getPplChooser().getSelectedFiles();
			if(files.length>0){
				try {
					PeptideListPagedRowGetter2 getter = new PeptideListPagedRowGetter2(files);
					PeptideListViewerPanel2 panel = new PeptideListViewerPanel2(getter);
					this.pepViewPanel = panel;
					panel.setFile(files[0].getParentFile());
					
				} catch (FileDamageException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
}
