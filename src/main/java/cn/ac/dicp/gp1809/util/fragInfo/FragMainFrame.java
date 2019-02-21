/* 
 ******************************************************************************
 * File: FragMainFrame.java * * * Created on 2011-4-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.fragInfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;

//VS4E -- DO NOT REMOVE THIS LINE!
public class FragMainFrame extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private JTextField jTextFieldSequence;
	private JCheckBox jCheckBoxMod;
	private JCheckBox jCheckBoxFragInfo;
	private JCheckBox jCheckBoxMS2Spec;
	
	private ModListFrame modListFrame;
	private FragInfoFrame framFrame;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public FragMainFrame() {
		initComponents();
		this.modListFrame = new ModListFrame();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJTextFieldSequence(), new Constraints(new Leading(41, 232, 10, 10), new Leading(49, 10, 10)));
		add(getJCheckBoxMod(), new Constraints(new Leading(41, 8, 8), new Leading(90, 10, 10)));
		add(getJCheckBoxMS2Spec(), new Constraints(new Leading(41, 8, 8), new Leading(180, 10, 10)));
		add(getJCheckBoxFragInfo(), new Constraints(new Leading(41, 8, 8), new Leading(135, 10, 10)));
		setSize(320, 240);
	}

	private JCheckBox getJCheckBoxMS2Spec() {
		if (jCheckBoxMS2Spec == null) {
			jCheckBoxMS2Spec = new JCheckBox();
			jCheckBoxMS2Spec.setText("Theoretical ms2 spectrum");
			jCheckBoxMS2Spec.addItemListener(this);
		}
		return jCheckBoxMS2Spec;
	}

	private JCheckBox getJCheckBoxFragInfo() {
		if (jCheckBoxFragInfo == null) {
			jCheckBoxFragInfo = new JCheckBox();
			jCheckBoxFragInfo.setText("Fragment Information");
			jCheckBoxFragInfo.addItemListener(this);
		}
		return jCheckBoxFragInfo;
	}

	private JCheckBox getJCheckBoxMod() {
		if (jCheckBoxMod == null) {
			jCheckBoxMod = new JCheckBox();
			jCheckBoxMod.setText("Modifications");
			jCheckBoxMod.addItemListener(this);
		}
		return jCheckBoxMod;
	}

	private JTextField getJTextFieldSequence() {
		if (jTextFieldSequence == null) {
			jTextFieldSequence = new JTextField();
		}
		return jTextFieldSequence;
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

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
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
				FragMainFrame frame = new FragMainFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("FragMainFrame");
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

		if(obj==this.getJCheckBoxMod()){
			if(this.getJCheckBoxMod().isSelected()){

				if(this.modListFrame==null)
					this.modListFrame = new ModListFrame();

				modListFrame.setVisible(true);
			}else{
				this.modListFrame.setVisible(false);
			}

			return;
		}
		
		if(obj==this.getJCheckBoxFragInfo()){
			
			if(this.getJCheckBoxFragInfo().isSelected()){
				String sequence = this.getJTextFieldSequence().getText();
				if(sequence==null || sequence.length()==0)
					throw new NullPointerException();
				
				Aminoacids aas = new Aminoacids();
				HashMap <Character, Double> modMap = this.modListFrame.getModMap();
				Iterator <Character> it = modMap.keySet().iterator();
				while(it.hasNext()){
					Character aa = it.next();
					aas.setModification(aa, modMap.get(aa));
				}
				
				AminoacidFragment aaf = new AminoacidFragment(aas);
				int [] type_b_y = new int []{Ion.TYPE_B, Ion.TYPE_Y};
				Ions ions = aaf.fragment(sequence, type_b_y, true);
				
				Ion [] bs = ions.bIons();
				Ion [] ys = ions.yIons();
				
				this.framFrame = new FragInfoFrame(bs, ys);
				framFrame.setVisible(true);
				
				return;
			}else{
				this.framFrame.setVisible(false);
			}
		}
		
		if(obj==this.getJCheckBoxMS2Spec()){
			return;
		}
	}

	
}
