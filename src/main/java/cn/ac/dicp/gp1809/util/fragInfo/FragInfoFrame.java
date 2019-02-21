/* 
 ******************************************************************************
 * File: FragInfoFrame.java * * * Created on 2011-4-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.fragInfo;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.util.DecimalFormats;

//VS4E -- DO NOT REMOVE THIS LINE!
public class FragInfoFrame extends JFrame implements ItemListener {

	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane0;
	private JTable jTable;
	private DecimalFormat df5 = DecimalFormats.DF0_5;
	
	private Ion [] bs;
	private Ion [] ys;
	private JRadioButton jRadioButtonC1;
	private JRadioButton jRadioButtonC2;
	private JRadioButton jRadioButtonC3;
	private JRadioButton jRadioButtonC4;
	private ButtonGroup group;
	private int charge = 1;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public FragInfoFrame() {
		initComponents();
	}
	
	public FragInfoFrame(Ion [] bs, Ion [] ys) {
		this.bs = bs;
		this.ys = ys;
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJScrollPane0(), new Constraints(new Leading(35, 360, 10, 10), new Leading(25, 240, 10, 10)));
		add(getJRadioButtonC1(), new Constraints(new Leading(35, 10, 10), new Leading(280, 10, 10)));
		add(getJRadioButtonC2(), new Constraints(new Leading(105, 10, 10), new Leading(280, 10, 10)));
		add(getJRadioButtonC3(), new Constraints(new Leading(175, 10, 10), new Leading(280, 10, 10)));
		add(getJRadioButtonC4(), new Constraints(new Leading(245, 10, 10), new Leading(280, 10, 10)));
		getJButtonGroup();
		setSize(430, 365);
	}

	private ButtonGroup getJButtonGroup() {
		if (group == null){
			group = new ButtonGroup();
			group.add(jRadioButtonC1);
			group.add(jRadioButtonC2);
			group.add(jRadioButtonC3);
			group.add(jRadioButtonC4);
		}
		return group;
	}

	private JRadioButton getJRadioButtonC4() {
		if (jRadioButtonC4 == null) {
			jRadioButtonC4 = new JRadioButton();
			jRadioButtonC4.setText("+4");
			jRadioButtonC4.addItemListener(this);
		}
		return jRadioButtonC4;
	}

	private JRadioButton getJRadioButtonC3() {
		if (jRadioButtonC3 == null) {
			jRadioButtonC3 = new JRadioButton();
			jRadioButtonC3.setText("+3");
			jRadioButtonC3.addItemListener(this);
		}
		return jRadioButtonC3;
	}

	private JRadioButton getJRadioButtonC2() {
		if (jRadioButtonC2 == null) {
			jRadioButtonC2 = new JRadioButton();
			jRadioButtonC2.setText("+2");
			jRadioButtonC2.addItemListener(this);
		}
		return jRadioButtonC2;
	}

	private JRadioButton getJRadioButtonC1() {
		if (jRadioButtonC1 == null) {
			jRadioButtonC1 = new JRadioButton();
			jRadioButtonC1.setSelected(true);
			jRadioButtonC1.setText("+1");
			jRadioButtonC1.addItemListener(this);
		}
		return jRadioButtonC1;
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJTable(charge));
		}
		return jScrollPane0;
	}

	private JTable getJTable(int charge) {
//		if (jTable == null) {
			jTable = new JTable();
			if(bs==null || ys==null){
				jTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Type", "Mono_mass", "Type", "Mono_mass"}) {
					private static final long serialVersionUID = 1L;
					Class<?>[] types = new Class<?>[] { Object.class, Object.class, Object.class, Object.class};
		
					@Override
					public Class<?> getColumnClass(int columnIndex) {
						return types[columnIndex];
					}
				});
			}else{
				Object [][] objs = new Object[bs.length][4];
				for(int i=0;i<objs.length;i++){
					for(int j=0;j<objs[i].length;j++){
						if(j==0){
							objs[i][j] = "B_"+(i+1);
						}else if(j==1){
							objs[i][j] = df5.format(bs[i].getMzVsCharge(charge));
						}else if(j==2){
							objs[i][j] = "Y_"+(i+1);
						}else{
							objs[i][j] = df5.format(ys[i].getMzVsCharge(charge));
						}
					}
				}
				jTable.setModel(new DefaultTableModel(objs, new String[] { "Type", "Mono_mass", "Type", "Mono_mass"}) {
					private static final long serialVersionUID = 1L;
					Class<?>[] types = new Class<?>[] { Object.class, Object.class, Object.class, Object.class};
		
					@Override
					public Class<?> getColumnClass(int columnIndex) {
						return types[columnIndex];
					}
				});
			}
//		}
		return jTable;
	}
	
	private void update(){
		jScrollPane0.remove(jTable);
		jScrollPane0.setViewportView(getJTable(charge));
		jScrollPane0.updateUI();
//		this.initComponents();		
		this.repaint();
		this.setVisible(true);
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
				FragInfoFrame frame = new FragInfoFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("FragInfoFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		
		Object obj = e.getSource();
		
		if(obj==this.getJRadioButtonC1()){
			if(this.getJRadioButtonC1().isSelected()){
				this.charge = 1;
				this.update();
				return;
			}
		}
		
		if(obj==this.getJRadioButtonC2()){
			if(this.getJRadioButtonC2().isSelected()){
				this.charge = 2;
				this.update();
				return;
			}
		}
		
		if(obj==this.getJRadioButtonC3()){
			if(this.getJRadioButtonC3().isSelected()){
				this.charge = 3;
				this.update();
				return;
			}
		}
		
		if(obj==this.getJRadioButtonC4()){
			if(this.getJRadioButtonC4().isSelected()){
				this.charge = 4;
				this.update();
				return;
			}
		}
	}

}
