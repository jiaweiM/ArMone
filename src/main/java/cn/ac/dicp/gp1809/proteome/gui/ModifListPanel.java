/* 
 ******************************************************************************
 * File:ModifListPanel.java * * * Created on 2009-12-14
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.xml.stream.XMLStreamException;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.util.beans.gui.SelectablePagedTable;

//VS4E -- DO NOT REMOVE THIS LINE!
public class ModifListPanel extends JFrame implements ActionListener{

	public static final String default_mascot_usermod_loc = "Mascot/unimod.xml";
	public static final String default_mascot_unimod_loc = "E:\\CK\\workspace\\ArCommon\\src\\Mascot\\unimod.xml";
	private ModifListPagedRowGetter getter;
	private static final long serialVersionUID = 1L;
	private SelectablePagedTable selectablePagedTable0;
	private JButton jButtonAdd;
	private JButton jButtonOK;
	private JButton jButtonRemove;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public ModifListPanel() throws FileNotFoundException, ModsReadingException, XMLStreamException{
		this.getter = new ModifListPagedRowGetter(default_mascot_usermod_loc, true);
		System.out.println(getter.getRowCount());
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new GroupLayout());
		add(getSelectablePagedTable0(), new Constraints(new Bilateral(0, 0, 300), new Leading(0, 320, 10, 10)));
		add(getJButtonAdd(), new Constraints(new Leading(160, 10, 10), new Leading(338, 6, 6)));
		add(getJButtonOK(), new Constraints(new Leading(494, 10, 10), new Leading(338, 6, 6)));
		add(getJButtonRemove(), new Constraints(new Leading(313, 10, 10), new Leading(338, 6, 6)));
		setSize(654, 429);
	}

	private JButton getJButtonRemove() {
		if (jButtonRemove == null) {
			jButtonRemove = new JButton();
			jButtonRemove.setText("Remove");
			jButtonRemove.addActionListener(this);
		}
		return jButtonRemove;
	}

	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("OK");
		}
		return jButtonOK;
	}

	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setText("Add");
			jButtonAdd.addActionListener(this);
		}
		return jButtonAdd;
	}

	private SelectablePagedTable getSelectablePagedTable0() {
		if (selectablePagedTable0 == null) {
			selectablePagedTable0 = new SelectablePagedTable(getter);
			selectablePagedTable0.setMinimumSize(new Dimension(300, 200));
			selectablePagedTable0.setPreferredSize(new Dimension(300, 200));
		}
		return selectablePagedTable0;
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
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj==this.getJButtonAdd()){
			JFrame frame = new AddModifFrame();
			frame.getContentPane().setPreferredSize(frame.getSize());
			frame.pack();
			frame.setLocationRelativeTo(this);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
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
				frame.setTitle("ModifListPanel");
				
				ModifListPanel content = null;
				try {
					content = new ModifListPanel();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ModsReadingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
