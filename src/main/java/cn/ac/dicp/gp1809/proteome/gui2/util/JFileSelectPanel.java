/* 
 ******************************************************************************
 * File:JFileSelectPanel.java * * * Created on 2011-10-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import cn.ac.dicp.gp1809.util.gui.UIutilities;

public class JFileSelectPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane0;
	private JList jList0;
	
	private String exten;
	private String des;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public JFileSelectPanel(){
		super();
		this.setLayout(new GroupLayout());
		this.setSize(320, 240);
	}
	
	public JFileSelectPanel(String name, String exten, String des){
		super();
		this.exten = exten;
		this.des = des;
		this.setBorder(BorderFactory.createTitledBorder(null, name, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD,
				12), new Color(51, 51, 51)));
		this.setLayout(new GroupLayout());
		this.add(getJScrollPane0(), new Constraints(new Bilateral(0, 0, 6, 6), new Bilateral(0, 0, 6, 6)));
		this.setSize(320, 240);
	}
	
	private void jTable0MouseMouseEntered(MouseEvent event) {
		jScrollPane0.setToolTipText("Right-click to add files");
	}
	
	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJList0());
		}

		jScrollPane0.addMouseListener(new MouseAdapter() {

			public void mouseEntered(MouseEvent event) {
				jTable0MouseMouseEntered(event);
			}
		});

		return jScrollPane0;
	}
	
	private JList getJList0() {
		if (jList0 == null) {
			jList0 = new JList();
			DefaultListModel listModel = new DefaultListModel();
			jList0.setModel(listModel);
			
			JPopupMenu menu = UIutilities.setPopupMenu(jList0);
			JMenuItem additem = new JMenuItem("Add file");
			additem.addActionListener(new AddActionListener(jList0, this));
			JMenuItem removeitem = new JMenuItem("Remove file");
			removeitem.addActionListener(new RemoveActionListener(jList0));
			JMenuItem clearitem = new JMenuItem("Clear file");
			clearitem.addActionListener(new ClearActionListener(jList0));
			menu.add(additem);
			menu.add(removeitem);
			menu.add(clearitem);
		}

		return jList0;
	}
	
	public File [] getFiles(){
		
		DefaultListModel model = ((DefaultListModel) jList0
		        .getModel());
		
		int start = 0;
	    int end = jList0.getModel().getSize() - 1;
	    if (end >= 0) {
	    	jList0.setSelectionInterval(start, end);
	    }
	    
		int [] inds = this.jList0.getSelectedIndices();
		File [] files = new File[inds.length];
		for(int i=0;i<inds.length;i++){
			files[i] = (File) model.getElementAt(inds[i]);
		}
		return files;
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
				frame.setTitle("JFileSelectPanel");
				JFileSelectPanel content = new JFileSelectPanel("", "", "");
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	private class AddActionListener implements ActionListener {

		private JList list;
		private JPanel panel;
		private MyJFileChooser filechooser;
		
		private AddActionListener(JList list, JPanel panel){
			super();
			this.list = list;
			this.panel = panel;
			this.getFilechooser();
		}
		
		private MyJFileChooser getFilechooser() {
			
			if (this.filechooser == null) {
				this.filechooser = new MyJFileChooser();
				this.filechooser.setMultiSelectionEnabled(true);
				this.filechooser
				        .setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				this.filechooser.setFileFilter(
						new String[] { exten },
						des);
			}
			return filechooser;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int value = this.filechooser.showOpenDialog(panel);
			if (value == JFileChooser.APPROVE_OPTION) {
				File [] files = this.filechooser.getSelectedFiles();
				for(int i=0;i<files.length;i++){
					((DefaultListModel) this.list.getModel()).addElement(files[i]);
				}
			}
		}
		
	}

	private class RemoveActionListener implements ActionListener {

		private JList list;
		
		private RemoveActionListener(JList list){
			super();
			this.list = list;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int [] inds = list.getSelectedIndices();
			if (inds.length > 0) {
				DefaultListModel model = ((DefaultListModel) list
				        .getModel());
				for (int i = inds.length - 1; i >= 0; i--) {
					model.remove(inds[i]);
				}
			}
		}
		
	}
	
	private class ClearActionListener implements ActionListener {

		private JList list;
		
		private ClearActionListener(JList list){
			super();
			this.list = list;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultListModel model = ((DefaultListModel) list.getModel());
			model.removeAllElements();
		}
	}
}
