/* 
 ******************************************************************************
 * File: PepListInfoPanel.java * * * Created on 04-11-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.dyno.visual.swing.layouts.GroupLayout;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-11-2009, 19:56:21
 */
public class PepListInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static DecimalFormat format ;
	
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		format = new DecimalFormat("0.##");
		
		Locale.setDefault(def);
	}
	
	private JLabel jLabel3;
	private JLabel jLabelc2;
	private JPanel jPanel3;
	private JPanel jPanel8;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JLabel jLabelTotal;
	private JPanel jPanel0;
	private JLabel jLabel5;
	private JLabel jLabelc1;
	private JPanel jPanel1;
	private JLabel jLabel7;
	private JLabel jLabelc3;
	private JPanel jPanel2;
	private JLabel jLabel9;
	private JLabel jLabel1c4;
	private JPanel jPanel4;
	private JPanel jPanel5;
	private JLabel jLabel11;
	private JLabel jLabel1target;
	private JPanel jPanel6;
	private JLabel jLabel13;
	private JLabel jLabeldecoy;
	private JPanel jPanel7;
	private JLabel jLabel15;
	private JLabel jLabelfdr;
	private JLabel jLabel17;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PepListInfoPanel() {
		initComponents();
	}
	
	public PepListInfoPanel(PeptideListInfo info) {
		initComponents();
		
		this.loadPeplistInfo(info);
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder("Information"));
    	setMinimumSize(new Dimension(659, 122));
    	setPreferredSize(new Dimension(659, 122));
    	setLayout(new FlowLayout(FlowLayout.LEADING));
    	add(getJPanel8());
    	add(getJPanel0());
    	add(getJPanel1());
    	add(getJPanel3());
    	add(getJPanel2());
    	add(getJPanel4());
    	add(getJPanel5());
    	add(getJPanel6());
    	add(getJPanel7());
    	setSize(659, 122);
    }
	
	/**
	 * Load the peptide list information
	 * 
	 * @param info
	 */
	public void loadPeplistInfo(PeptideListInfo info) {
		
		this.jLabelTotal.setText(String.valueOf(info.getPep_total()));
		this.jLabelc1.setText(String.valueOf(info.getPep1()));
		this.jLabelc2.setText(String.valueOf(info.getPep2()));
		this.jLabelc3.setText(String.valueOf(info.getPep3()));
		this.jLabel1c4.setText(String.valueOf(info.getPep3plus()));
		this.jLabel1target.setText(String.valueOf(info.getTarget()));
		this.jLabeldecoy.setText(String.valueOf(info.getDecoy()));
		this.jLabelfdr.setText(format.format(info.getFdr()*100));
		
	}

	private JLabel getJLabel17() {
    	if (jLabel17 == null) {
    		jLabel17 = new JLabel();
    		jLabel17.setText("%");
    	}
    	return jLabel17;
    }

	private JLabel getJLabelfdr() {
    	if (jLabelfdr == null) {
    		jLabelfdr = new JLabel();
    		jLabelfdr.setHorizontalAlignment(SwingConstants.LEFT);
    		jLabelfdr.setText("0");
    		jLabelfdr.setMinimumSize(new Dimension(40, 20));
    		jLabelfdr.setPreferredSize(new Dimension(40, 20));
    		jLabelfdr.setMaximumSize(new Dimension(40, 20));
    		jLabelfdr.setBorder(new LineBorder(Color.black, 1, false));
    	}
    	return jLabelfdr;
    }

	private JLabel getJLabel15() {
    	if (jLabel15 == null) {
    		jLabel15 = new JLabel();
    		jLabel15.setText("FDR");
    	}
    	return jLabel15;
    }

	private JPanel getJPanel7() {
    	if (jPanel7 == null) {
    		jPanel7 = new JPanel();
    		jPanel7.add(getJLabel15());
    		jPanel7.add(getJLabelfdr());
    		jPanel7.add(getJLabel17());
    	}
    	return jPanel7;
    }

	private JLabel getJLabeldecoy() {
    	if (jLabeldecoy == null) {
    		jLabeldecoy = new JLabel();
    		jLabeldecoy.setHorizontalAlignment(SwingConstants.LEFT);
    		jLabeldecoy.setText("0");
    		jLabeldecoy.setMinimumSize(new Dimension(40, 20));
    		jLabeldecoy.setPreferredSize(new Dimension(40, 20));
    		jLabeldecoy.setMaximumSize(new Dimension(40, 20));
    		jLabeldecoy.setBorder(new LineBorder(Color.black, 1, false));
    	}
    	return jLabeldecoy;
    }

	private JLabel getJLabel13() {
    	if (jLabel13 == null) {
    		jLabel13 = new JLabel();
    		jLabel13.setText("decoy");
    	}
    	return jLabel13;
    }

	private JPanel getJPanel6() {
    	if (jPanel6 == null) {
    		jPanel6 = new JPanel();
    		jPanel6.add(getJLabel13());
    		jPanel6.add(getJLabeldecoy());
    	}
    	return jPanel6;
    }

	private JLabel getJLabel1target() {
    	if (jLabel1target == null) {
    		jLabel1target = new JLabel();
    		jLabel1target.setHorizontalAlignment(SwingConstants.LEFT);
    		jLabel1target.setText("0");
    		jLabel1target.setMinimumSize(new Dimension(40, 20));
    		jLabel1target.setPreferredSize(new Dimension(40, 20));
    		jLabel1target.setMaximumSize(new Dimension(40, 20));
    		jLabel1target.setBorder(new LineBorder(Color.black, 1, false));
    	}
    	return jLabel1target;
    }

	private JLabel getJLabel11() {
    	if (jLabel11 == null) {
    		jLabel11 = new JLabel();
    		jLabel11.setText("target");
    	}
    	return jLabel11;
    }

	private JPanel getJPanel5() {
    	if (jPanel5 == null) {
    		jPanel5 = new JPanel();
    		jPanel5.add(getJLabel11());
    		jPanel5.add(getJLabel1target());
    	}
    	return jPanel5;
    }

	private JPanel getJPanel4() {
    	if (jPanel4 == null) {
    		jPanel4 = new JPanel();
    		jPanel4.add(getJLabel9());
    		jPanel4.add(getJLabel1c4());
    	}
    	return jPanel4;
    }

	private JLabel getJLabel1c4() {
    	if (jLabel1c4 == null) {
    		jLabel1c4 = new JLabel();
    		jLabel1c4.setHorizontalAlignment(SwingConstants.LEFT);
    		jLabel1c4.setText("0");
    		jLabel1c4.setMinimumSize(new Dimension(40, 20));
    		jLabel1c4.setPreferredSize(new Dimension(40, 20));
    		jLabel1c4.setMaximumSize(new Dimension(40, 20));
    		jLabel1c4.setBorder(new LineBorder(Color.black, 1, false));
    	}
    	return jLabel1c4;
    }

	private JLabel getJLabel9() {
    	if (jLabel9 == null) {
    		jLabel9 = new JLabel();
    		jLabel9.setText(">3+");
    	}
    	return jLabel9;
    }

	private JPanel getJPanel2() {
    	if (jPanel2 == null) {
    		jPanel2 = new JPanel();
    		jPanel2.add(getJLabel7());
    		jPanel2.add(getJLabelc3());
    	}
    	return jPanel2;
    }

	private JLabel getJLabelc3() {
    	if (jLabelc3 == null) {
    		jLabelc3 = new JLabel();
    		jLabelc3.setHorizontalAlignment(SwingConstants.LEFT);
    		jLabelc3.setText("0");
    		jLabelc3.setMinimumSize(new Dimension(40, 20));
    		jLabelc3.setPreferredSize(new Dimension(40, 20));
    		jLabelc3.setMaximumSize(new Dimension(40, 20));
    		jLabelc3.setBorder(new LineBorder(Color.black, 1, false));
    	}
    	return jLabelc3;
    }

	private JLabel getJLabel7() {
    	if (jLabel7 == null) {
    		jLabel7 = new JLabel();
    		jLabel7.setText("3+");
    	}
    	return jLabel7;
    }

	private JPanel getJPanel1() {
    	if (jPanel1 == null) {
    		jPanel1 = new JPanel();
    		jPanel1.add(getJLabel5());
    		jPanel1.add(getJLabelc1());
    	}
    	return jPanel1;
    }

	private JLabel getJLabelc1() {
    	if (jLabelc1 == null) {
    		jLabelc1 = new JLabel();
    		jLabelc1.setHorizontalAlignment(SwingConstants.LEFT);
    		jLabelc1.setText("0");
    		jLabelc1.setMinimumSize(new Dimension(40, 20));
    		jLabelc1.setPreferredSize(new Dimension(40, 20));
    		jLabelc1.setMaximumSize(new Dimension(40, 20));
    		jLabelc1.setBorder(new LineBorder(Color.black, 1, false));
    	}
    	return jLabelc1;
    }

	private JLabel getJLabel5() {
    	if (jLabel5 == null) {
    		jLabel5 = new JLabel();
    		jLabel5.setText("1+");
    	}
    	return jLabel5;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setLayout(new FlowLayout(FlowLayout.LEADING));
    		jPanel0.add(getJLabel0());
    		jPanel0.add(getJLabel1());
    		jPanel0.add(getJLabelTotal());
    	}
    	return jPanel0;
    }

	private JLabel getJLabelTotal() {
    	if (jLabelTotal == null) {
    		jLabelTotal = new JLabel();
    		jLabelTotal.setHorizontalAlignment(SwingConstants.LEFT);
    		jLabelTotal.setText("0");
    		jLabelTotal.setMinimumSize(new Dimension(40, 20));
    		jLabelTotal.setPreferredSize(new Dimension(40, 20));
    		jLabelTotal.setMaximumSize(new Dimension(40, 20));
    		jLabelTotal.setBorder(new LineBorder(Color.black, 1, false));
    	}
    	return jLabelTotal;
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("total");
    	}
    	return jLabel1;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Peptides");
    	}
    	return jLabel0;
    }

	private JPanel getJPanel8() {
    	if (jPanel8 == null) {
    		jPanel8 = new JPanel();
    		jPanel8.setLayout(new GroupLayout());
    	}
    	return jPanel8;
    }

	private JPanel getJPanel3() {
    	if (jPanel3 == null) {
    		jPanel3 = new JPanel();
    		jPanel3.add(getJLabel3());
    		jPanel3.add(getJLabelc2());
    	}
    	return jPanel3;
    }

	private JLabel getJLabelc2() {
    	if (jLabelc2 == null) {
    		jLabelc2 = new JLabel();
    		jLabelc2.setHorizontalAlignment(SwingConstants.LEFT);
    		jLabelc2.setText("0");
    		jLabelc2.setMinimumSize(new Dimension(40, 20));
    		jLabelc2.setPreferredSize(new Dimension(40, 20));
    		jLabelc2.setMaximumSize(new Dimension(40, 20));
    		jLabelc2.setBorder(new LineBorder(Color.black, 1, false));
    	}
    	return jLabelc2;
    }

	private JLabel getJLabel3() {
    	if (jLabel3 == null) {
    		jLabel3 = new JLabel();
    		jLabel3.setText("2+");
    	}
    	return jLabel3;
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
				frame.setTitle("PepListInfoPanel");
				PepListInfoPanel content = new PepListInfoPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
