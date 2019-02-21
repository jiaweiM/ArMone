/* 
 ******************************************************************************
 * File: MutilCompFrame.java * * * Created on 2011-11-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;


//VS4E -- DO NOT REMOVE THIS LINE!
public class TurnOverFrame extends JFrame implements ActionListener {

	private File [] files;
	
	private static final long serialVersionUID = 1L;
	private TurnOverPanel1 mutilCompPanel10;
	private TurnOverPanel2 mutilCompPanel20;
	private TurnOverPanel3 mutilCompPanel30;

	private JPanel jPanel0;
	private JButton jButtonNex0;
	private JLabel jLabel0;
	private JTextField jTextField0;
	
	private JPanel jPanel1;
	private int timePointsCount;
	private JButton jButtonNex1;
	private JTable jTable0;
	private JScrollPane jScrollPane0;
	private String [] titles;

	private JButton jButtonPre1;

	private JButton jButtonClose;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public TurnOverFrame() {
		initComponents();
	}
	
	public TurnOverFrame(String name, File [] files) {
		initComponents();
		this.setName(name);
		this.files = files;
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJPanel0(), new Constraints(new Bilateral(0, 0, 0, 0), new Bilateral(0, 0, 0, 0)));
		setSize(540, 360);
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
			jButtonClose.addActionListener(this);
		}
		return jButtonClose;
	}

	private JButton getJButtonPre1() {
		if (jButtonPre1 == null) {
			jButtonPre1 = new JButton();
			jButtonPre1.setText("Previous");
			jButtonPre1.addActionListener(this);
		}
		return jButtonPre1;
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
			if(timePointsCount>0){
				
				Object [][] objs = new Object [timePointsCount][2];
				for(int i=0;i<objs.length;i++){
					objs[i][0] = "Time point "+(i+1);
					objs[i][1] = "";
				}
				
				String [] title = new String []{"", "Time (min)"};
				DefaultTableModel model = new DefaultTableModel(objs, title);
				jTable0.setModel(model);
				
			}else
				jTable0.setModel(new DefaultTableModel(new Object[][] { { "0x0", "0x1", }, 
					{ "1x0", "1x1", }, }, new String[] { "Title 0", "Title 1", }));
			
		}
		return jTable0;
	}

	private JPanel getJPanel1() {		
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GroupLayout());
			jPanel1.add(getJButtonNex1(), new Constraints(new Leading(420, 10, 10), new Leading(289, 10, 10)));
			jPanel1.add(getJButtonPre1(), new Constraints(new Leading(314, 10, 10), new Leading(289, 12, 12)));
			jPanel1.add(getJScrollPane0(), new Constraints(new Leading(11, 200, 10, 10), new Leading(12, 307, 12, 12)));
		}		
		return jPanel1;
	}

	private JButton getJButtonNex1() {
		if (jButtonNex1 == null) {
			jButtonNex1 = new JButton();
			jButtonNex1.setText("Next");
			jButtonNex1.addActionListener(this);
		}
		return jButtonNex1;
	}
	
	private JTextField getJTextField0() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
			jTextField0.setText("");
		}
		return jTextField0;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Time points count");
		}
		return jLabel0;
	}

	private JButton getJButtonNex0() {
		if (jButtonNex0 == null) {
			jButtonNex0 = new JButton();
			jButtonNex0.setText("Next");
			jButtonNex0.addActionListener(this);
		}
		return jButtonNex0;
	}
	
	

	private JPanel getJPanel0() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			jPanel0.setLayout(new GroupLayout());
			jPanel0.add(getJButtonNex0(), new Constraints(new Leading(330, 10, 10), new Leading(290, 10, 10)));
			jPanel0.add(getJLabel0(), new Constraints(new Leading(290, 10, 10), new Leading(238, 10, 10)));
			jPanel0.add(getJTextField0(), new Constraints(new Leading(420, 63, 10, 10), new Leading(232, 12, 12)));
			jPanel0.add(getJButtonClose(), new Constraints(new Leading(420, 10, 10), new Leading(290, 12, 12)));
		}
		return jPanel0;
	}

	private TurnOverPanel1 getMutilCompPanel10() {
		if (mutilCompPanel10 == null) {
			if(this.titles==null)
				mutilCompPanel10 = new TurnOverPanel1();
			else
				mutilCompPanel10 = new TurnOverPanel1(titles);
			
			mutilCompPanel10.getJButtonClose().addActionListener(this);
			mutilCompPanel10.getJButtonNex().addActionListener(this);
			mutilCompPanel10.getJButtonPre().addActionListener(this);
		}
		return mutilCompPanel10;
	}
	
	private TurnOverPanel2 getMutilCompPanel20() {
		if (mutilCompPanel20==null) {
			mutilCompPanel20 = new TurnOverPanel2();
			mutilCompPanel20.getJButtonClose().addActionListener(this);
			mutilCompPanel20.getJButtonMod().addActionListener(this);
			mutilCompPanel20.getJButtonPre().addActionListener(this);
			mutilCompPanel20.getJButtonStart().addActionListener(this);
		}
		return mutilCompPanel20;
	}
	
	private TurnOverPanel3 getMutilCompPanel30() {
		if (mutilCompPanel30 == null) {
			mutilCompPanel30 = new TurnOverPanel3();
			mutilCompPanel30.getJButtonClose().addActionListener(this);
			mutilCompPanel30.getJButtonPre().addActionListener(this);
			mutilCompPanel30.getJButtonStart().addActionListener(this);
		}
		return mutilCompPanel30;
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
	
	private void reLoadTable(){
		if(this.jPanel1 == null){
			this.getJPanel1();
			
		}else{
			
			jTable0 = new JTable();
			if(timePointsCount>0){
				
				Object [][] objs = new Object [timePointsCount][2];
				for(int i=0;i<objs.length;i++){
					objs[i][0] = "Time point "+(i+1);
					objs[i][1] = "";
				}
				
				String [] title = new String []{"", "Time (min)"};
				DefaultTableModel model = new DefaultTableModel(objs, title);
				jTable0.setModel(model);
				
			}else
				jTable0.setModel(new DefaultTableModel(new Object[][] { { "0x0", "0x1", }, 
					{ "1x0", "1x1", }, }, new String[] { "Title 0", "Title 1", }));
			
			jScrollPane0.setViewportView(jTable0);
			jPanel1.updateUI();
		}
	}

	private String [] getTableTitle(){
		
		DefaultTableModel model = (DefaultTableModel) jTable0.getModel();
		int rowCount = this.jTable0.getRowCount();
		String [] titles = new String [rowCount];
		for(int i=0;i<rowCount;i++){
			String name = (String) model.getValueAt(i, 0);
			String time = (String) model.getValueAt(i, 1);
			titles[i] = name+":"+time;
		}
		
		return titles;
	}
	
	private double [] getTimePoints(){
		
		DefaultTableModel model = (DefaultTableModel) jTable0.getModel();
		int rowCount = this.jTable0.getRowCount();
		double [] points = new double [rowCount];
		for(int i=0;i<rowCount;i++){
			String time = (String) model.getValueAt(i, 1);
			points[i] = Double.parseDouble(time);
		}
		return points;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		Object obj = e.getSource();
		
		if(obj==this.getJButtonClose()){
			this.dispose();
			return;
		}
		
		if(obj==this.getJButtonNex0()){			
			int count = Integer.parseInt(this.getJTextField0().getText().trim());
			this.timePointsCount = count;
			this.remove(jPanel0);
			this.reLoadTable();
			this.add(getJPanel1(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
			this.repaint();
			this.setVisible(true);
			return;
		}
		
		if(obj==this.getJButtonNex1()){
			this.titles = this.getTableTitle();
			this.remove(jPanel1);
			this.add(getMutilCompPanel10(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
			this.mutilCompPanel10.reLoadTable(titles);
			this.repaint();
			this.setVisible(true);
			return;
		}
		
		if(obj==this.getJButtonPre1()){
			this.remove(jPanel1);
			this.add(getJPanel0(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
			this.repaint();
			this.setVisible(true);
			return;
		}
		
		if(obj==this.getMutilCompPanel10().getJButtonPre()){
			this.remove(mutilCompPanel10);
			this.add(getJPanel1(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
			this.repaint();
			this.setVisible(true);
			return;
		}
		
		if(obj==this.getMutilCompPanel10().getJButtonClose()){
			this.dispose();
			return;
		}
		
		if(obj==this.getMutilCompPanel10().getJButtonNex()){
			this.remove(mutilCompPanel10);
			this.add(getMutilCompPanel20(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
			this.repaint();
			this.setVisible(true);
			return;
		}
		
		if(obj==this.getMutilCompPanel20().getJButtonClose()){
			this.dispose();
			return;
		}
		
		if(obj==this.getMutilCompPanel20().getJButtonMod()){
			this.remove(mutilCompPanel20);
			this.add(getMutilCompPanel30(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
			this.repaint();
			this.setVisible(true);
			return;
		}
		
		if(obj==this.getMutilCompPanel20().getJButtonPre()){
			this.remove(this.mutilCompPanel20);
			this.add(getMutilCompPanel10(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
			this.repaint();
			this.setVisible(true);
			return;
		}
		
		if(obj==this.getMutilCompPanel20().getJButtonStart()){
			return;
		}
		
		if(obj==this.getMutilCompPanel30().getJButtonClose()){
			this.dispose();
			return;
		}
		
		if(obj==this.getMutilCompPanel30().getJButtonPre()){
			this.remove(this.mutilCompPanel30);
			this.add(getMutilCompPanel20(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
			this.repaint();
			this.setVisible(true);
			return;
		}
		
		if(obj==this.getMutilCompPanel30().getJButtonStart()){
			return;
		}
	}

	private class MutilCompThread extends Thread{
		
	}
	
	private class MutilModCompThread extends Thread{
		
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
				TurnOverFrame frame = new TurnOverFrame();
				frame.setDefaultCloseOperation(TurnOverFrame.EXIT_ON_CLOSE);
				frame.setTitle("MutilCompFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());				
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
