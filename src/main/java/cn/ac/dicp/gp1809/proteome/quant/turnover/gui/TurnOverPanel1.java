/* 
 ******************************************************************************
 * File: MutilCompPanel.java * * * Created on 2011-11-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

//VS4E -- DO NOT REMOVE THIS LINE!
public class TurnOverPanel1 extends JPanel {

	private String [] titles;
	private JTable jTable0;
	private JScrollPane jScrollPane0;
	private JButton jButtonNex;
	private JButton jButtonClose;
	private JButton jButtonPre;
	private MyJFileChooser xmlChooser;
	
	private static final long serialVersionUID = 1L;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public TurnOverPanel1() {
		initComponents();
	}
	
	public TurnOverPanel1(String [] titles) {
		this.titles = titles;
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJScrollPane0(), new Constraints(new Bilateral(0, 0, 200), new Leading(0, 180, 10, 10)));
		add(getJButtonNex(), new Constraints(new Trailing(130, 10, 10), new Leading(320, 12, 12)));
		add(getJButtonClose(), new Constraints(new Trailing(30, 10, 10), new Leading(320, 12, 12)));
		add(getJButtonPre(), new Constraints(new Trailing(230, 10, 10), new Leading(320, 12, 12)));
		setSize(540, 390);
	}

	protected JButton getJButtonPre() {
		if (jButtonPre == null) {
			jButtonPre = new JButton();
			jButtonPre.setText("Previous");
		}
		return jButtonPre;
	}

	protected JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
		}
		return jButtonClose;
	}

	protected JButton getJButtonNex() {
		if (jButtonNex == null) {
			jButtonNex = new JButton();
			jButtonNex.setText("Next");
		}
		return jButtonNex;
	}
	
	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJTable0());
		}
		return jScrollPane0;
	}

	private JTable getJTable0() {
		if (jTable0 == null) {
			jTable0 = new JTable();
			
			if(this.titles==null)
				jTable0.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null, }, { null, null, null, null, null, },
					{ null, null, null, null, null, }, { null, null, null, null, null, }, { null, null, null, null, null, }, }, new String[] { "Time point 1",
					"Time point 2", "Time point 3", "Time point 4", "Time point 5", }));
			else{
				
				Object [][] objs = new Object [titles.length][5];
				DefaultTableModel model = new DefaultTableModel(objs, titles);
				jTable0.setModel(model);
			}
			
			jTable0.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jTable0MouseMouseClicked(event);
				}
	
				public void mouseEntered(MouseEvent event) {
					jTable0MouseMouseEntered(event);
				}
			});
		}
		return jTable0;
	}

	private MyJFileChooser getOutchooser() {
		if (this.xmlChooser == null) {
			this.xmlChooser = new MyJFileChooser();
			this.xmlChooser.setMultiSelectionEnabled(true);
			this.xmlChooser.setFileFilter(new String[] { "pxml" },
			        " Peptide quantitation XML file (*.pxml)");
		}
		return xmlChooser;
	}
	
	protected void addColumn(JFrame frame){
		
		DefaultTableModel model = (DefaultTableModel) jTable0.getModel();
		int currentColumnCount = model.getColumnCount();
		model.addColumn("Time point "+(++currentColumnCount));
		int width = this.getWidth() + 108;
		this.setSize(width, this.getHeight());
		frame.setSize(width, frame.getHeight());
	}

	protected void getFiles(){
		
		File [][] files = new File[this.jTable0.getColumnCount()][];
		int rowCount = this.jTable0.getRowCount();
		for(int i=0;i<files.length;i++){
			ArrayList <File> filelist = new ArrayList <File>();
			for(int j=0;j<rowCount;j++){
				Object obj = jTable0.getValueAt(j, i);
				filelist.add((File) obj);
			}
			files[i] = filelist.toArray(new File[filelist.size()]);
		}
	}
	
	public void reLoadTable(String [] titles){
		this.titles = titles;
		Object [][] objs = new Object [titles.length][5];
		DefaultTableModel model = new DefaultTableModel(objs, titles);
		jTable0.setModel(model);
		this.updateUI();
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
				frame.setTitle("MutilCompPanel");
				TurnOverPanel1 content = new TurnOverPanel1();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	private void jTable0MouseMouseClicked(MouseEvent event) {
		
		java.awt.Point p = event.getPoint();
		int rowIndex = this.jTable0.rowAtPoint(p);
		int columnIndex = this.jTable0.columnAtPoint(p);

		int rowCount = this.jTable0.getRowCount();
		int columnCount = this.jTable0.getColumnCount();
		
		DefaultTableModel model = (DefaultTableModel) jTable0.getModel();

		int value = this.getOutchooser().showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION){
			File [] files = this.getOutchooser().getSelectedFiles();
			if(files.length>rowCount){
				int addCount = files.length - rowCount;
				for(int i=0;i<addCount;i++){
					Object [] rows = new Object[columnCount];
					model.addRow(rows);
				}
			}
			for(int i=0;i<files.length;i++){
				model.setValueAt(files[i], i, columnIndex);
			}
		}
	}

	private void jTable0MouseMouseEntered(MouseEvent event) {
		this.jTable0.setToolTipText("Click to add files in this column");
	}
}
