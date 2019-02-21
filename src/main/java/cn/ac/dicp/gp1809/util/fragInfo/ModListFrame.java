/* 
 ******************************************************************************
 * File: ModListFrame.java * * * Created on 2011-4-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.fragInfo;

import java.util.HashMap;

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
public class ModListFrame extends JFrame {
	
	private JTable jTable;

	private static final long serialVersionUID = 1L;
	private JPanel jPane0;

	private JScrollPane jScrollPane0;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public ModListFrame() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJPane0(), new Constraints(new Leading(40, 460, 12, 12), new Leading(30, 180, 10, 10)));
		setSize(540, 263);
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJTable());
		}
		return jScrollPane0;
	}

	private JPanel getJPane0() {
		if (jPane0 == null) {
			jPane0 = new JPanel();
			jPane0.add(getJScrollPane0());
		}
		return jPane0;
	}

	public JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			jTable.setModel(new DefaultTableModel(new Object[][] { { 'A', 0.0d, 'B', 0.0d, 'C', 0.0d, }, { 'D', 0.0d, 'E', 0.0d, 'F', 0.0d, },
					{ 'G', 0.0d, 'H', 0.0d, 'I', 0.0d, }, { 'J', 0.0d, 'K', 0.0d, 'L', 0.0d, }, { 'M', 0.0d, 'N', 0.0d, 'O', 0.0d, },
					{ 'P', 0.0d, 'Q', 0.0d, 'R', 0.0d, }, { 'S', 0.0d, 'T', 0.0d, 'U', 0.0d, }, { 'V', 0.0d, 'W', 0.0d, 'X', 0.0d, },
					{ 'Y', 0.0d, 'Z', 0.0d, null, null, }, }, new String[] { "Aminoacid", "Mono_mass", "Aminoacid", "Mono_mass", "Aminoacid", "Mono_mass", }) {
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

	public HashMap <Character, Double> getModMap(){
		HashMap <Character, Double>	modMap = new HashMap <Character, Double> ();
		Character c = 0;
		for(int i=0;i<jTable.getRowCount();i++){
			for(int j=0;j<jTable.getColumnCount();j++){
				Object obj = this.jTable.getValueAt(i, j);
				if(obj!=null){
					if(j%2==0){
						c = (Character) obj;
					}else{
						if(obj instanceof Double){
							Double mass = (Double) obj;
							modMap.put(c, mass);
						}else if(obj instanceof String){
							String str = (String) obj;
							Double mass = Double.parseDouble(str);
							modMap.put(c, mass);
						}
					}
				}
			}
		}
		return modMap;
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
				ModListFrame frame = new ModListFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("ModListFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.getModMap();
			}
		});
		
	}

}
