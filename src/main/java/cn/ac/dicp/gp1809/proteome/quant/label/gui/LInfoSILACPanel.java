package cn.ac.dicp.gp1809.proteome.quant.label.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

//VS4E -- DO NOT REMOVE THIS LINE!
public class LInfoSILACPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel jLabelName;
	private JCheckBox jCheckBoxS1;
	private JCheckBox jCheckBoxS2;
	private JCheckBox jCheckBoxS3;
	
	private JComboBox jComboBoxS11;
	private JComboBox jComboBoxS12;
	private JComboBox jComboBoxS21;
	private JComboBox jComboBoxS22;
	private JComboBox jComboBoxS31;
	private JComboBox jComboBoxS32;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public LInfoSILACPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabelName(), new Constraints(new Leading(23, 10, 10), new Leading(16, 10, 10)));
		add(getJCheckBoxS1(), new Constraints(new Leading(23, 10, 10), new Leading(44, 10, 10)));
		add(getJCheckBoxS2(), new Constraints(new Leading(23, 10, 10), new Leading(79, 10, 10)));
		add(getJCheckBoxS3(), new Constraints(new Leading(23, 10, 10), new Leading(117, 10, 10)));
		add(getJComboBoxS11(), new Constraints(new Leading(140, 10, 10), new Leading(41, 6, 6)));
		add(getJComboBoxS12(), new Constraints(new Leading(300, 10, 10), new Leading(41, 6, 6)));
		add(getJComboBoxS21(), new Constraints(new Leading(140, 10, 10), new Leading(76, 6, 6)));
		add(getJComboBoxS22(), new Constraints(new Leading(300, 10, 10), new Leading(76, 6, 6)));
		add(getJComboBoxS31(), new Constraints(new Leading(140, 10, 10), new Leading(114, 6, 6)));
		add(getJComboBoxS32(), new Constraints(new Leading(300, 10, 10), new Leading(114, 6, 6)));
		setSize(460, 155);
	}

	private JComboBox getJComboBoxS11() {
		if (jComboBoxS11 == null) {
			jComboBoxS11 = new JComboBox();
			jComboBoxS11.setModel(new DefaultComboBoxModel(new Object[] { null, LabelInfo.SILAC_Arg6, 
					LabelInfo.SILAC_Arg10, LabelInfo.SILAC_Lys4, LabelInfo.SILAC_Lys6,
					LabelInfo.SILAC_Lys8, LabelInfo.SILAC_Leu3}));
			jComboBoxS11.setDoubleBuffered(false);
			jComboBoxS11.setBorder(null);
		}
		return jComboBoxS11;
	}
	
	private JComboBox getJComboBoxS12() {
		if (jComboBoxS12 == null) {
			jComboBoxS12 = new JComboBox();
			jComboBoxS12.setModel(new DefaultComboBoxModel(new Object[] { null, LabelInfo.SILAC_Arg6, 
					LabelInfo.SILAC_Arg10, LabelInfo.SILAC_Lys4, LabelInfo.SILAC_Lys6,
					LabelInfo.SILAC_Lys8, LabelInfo.SILAC_Leu3}));
			jComboBoxS12.setDoubleBuffered(false);
			jComboBoxS12.setBorder(null);
		}
		return jComboBoxS12;
	}
	
	private JComboBox getJComboBoxS21() {
		if (jComboBoxS21 == null) {
			jComboBoxS21 = new JComboBox();
			jComboBoxS21.setModel(new DefaultComboBoxModel(new Object[] { null, LabelInfo.SILAC_Arg6, 
					LabelInfo.SILAC_Arg10, LabelInfo.SILAC_Lys4, LabelInfo.SILAC_Lys6,
					LabelInfo.SILAC_Lys8, LabelInfo.SILAC_Leu3}));
			jComboBoxS21.setDoubleBuffered(false);
			jComboBoxS21.setBorder(null);
		}
		return jComboBoxS21;
	}
	
	private JComboBox getJComboBoxS22() {
		if (jComboBoxS22 == null) {
			jComboBoxS22 = new JComboBox();
			jComboBoxS22.setModel(new DefaultComboBoxModel(new Object[] { null, LabelInfo.SILAC_Arg6, 
					LabelInfo.SILAC_Arg10, LabelInfo.SILAC_Lys4, LabelInfo.SILAC_Lys6,
					LabelInfo.SILAC_Lys8, LabelInfo.SILAC_Leu3}));
			jComboBoxS22.setDoubleBuffered(false);
			jComboBoxS22.setBorder(null);
		}
		return jComboBoxS22;
	}
	
	private JComboBox getJComboBoxS31() {
		if (jComboBoxS31 == null) {
			jComboBoxS31 = new JComboBox();
			jComboBoxS31.setModel(new DefaultComboBoxModel(new Object[] { null, LabelInfo.SILAC_Arg6, 
					LabelInfo.SILAC_Arg10, LabelInfo.SILAC_Lys4, LabelInfo.SILAC_Lys6,
					LabelInfo.SILAC_Lys8, LabelInfo.SILAC_Leu3}));
			jComboBoxS31.setDoubleBuffered(false);
			jComboBoxS31.setBorder(null);
		}
		return jComboBoxS31;
	}
	
	private JComboBox getJComboBoxS32() {
		if (jComboBoxS32 == null) {
			jComboBoxS32 = new JComboBox();
			jComboBoxS32.setModel(new DefaultComboBoxModel(new Object[] { null, LabelInfo.SILAC_Arg6, 
					LabelInfo.SILAC_Arg10, LabelInfo.SILAC_Lys4, LabelInfo.SILAC_Lys6,
					LabelInfo.SILAC_Lys8, LabelInfo.SILAC_Leu3}));
			jComboBoxS32.setDoubleBuffered(false);
			jComboBoxS32.setBorder(null);
		}
		return jComboBoxS32;
	}

	private JCheckBox getJCheckBoxS3() {
		if (jCheckBoxS3 == null) {
			jCheckBoxS3 = new JCheckBox();
			jCheckBoxS3.setText("   3. SILAC_3");
		}
		return jCheckBoxS3;
	}

	private JCheckBox getJCheckBoxS2() {
		if (jCheckBoxS2 == null) {
			jCheckBoxS2 = new JCheckBox();
			jCheckBoxS2.setText("   2. SILAC_2");
		}
		return jCheckBoxS2;
	}

	private JCheckBox getJCheckBoxS1() {
		if (jCheckBoxS1 == null) {
			jCheckBoxS1 = new JCheckBox();
			jCheckBoxS1.setText("   1. SILAC_1");
		}
		return jCheckBoxS1;
	}

	private JLabel getJLabelName() {
		if (jLabelName == null) {
			jLabelName = new JLabel();
			jLabelName.setText("SILAC");
		}
		return jLabelName;
	}

	public LabelType getLabelType(){
		LabelType silac = LabelType.SILAC;
		ArrayList <Short> intList = new ArrayList <Short>();
		
		ArrayList <LabelInfo[]> infoList = new ArrayList <LabelInfo[]>();
		if(getJCheckBoxS1().isSelected()){
			intList.add((short)1);
			ArrayList <LabelInfo> iList = new ArrayList <LabelInfo>();
			Object obj1 = getJComboBoxS11().getSelectedItem();
			if(obj1!=null){
				iList.add((LabelInfo)obj1);
			}
			Object obj2 = getJComboBoxS12().getSelectedItem();
			if(obj2!=null){
				iList.add((LabelInfo)obj2);
			}
			infoList.add(iList.toArray(new LabelInfo[iList.size()]));
		}
		if(getJCheckBoxS2().isSelected()){
			intList.add((short)2);
			ArrayList <LabelInfo> iList = new ArrayList <LabelInfo>();
			Object obj1 = getJComboBoxS21().getSelectedItem();
			if(obj1!=null){
				iList.add((LabelInfo)obj1);
			}
			Object obj2 = getJComboBoxS22().getSelectedItem();
			if(obj2!=null){
				iList.add((LabelInfo)obj2);
			}
			infoList.add(iList.toArray(new LabelInfo[iList.size()]));
		}
		if(getJCheckBoxS3().isSelected()){
			intList.add((short)3);
			ArrayList <LabelInfo> iList = new ArrayList <LabelInfo>();
			Object obj1 = getJComboBoxS31().getSelectedItem();
			if(obj1!=null){
				iList.add((LabelInfo)obj1);
			}
			Object obj2 = getJComboBoxS32().getSelectedItem();
			if(obj2!=null){
				iList.add((LabelInfo)obj2);
			}
			infoList.add(iList.toArray(new LabelInfo[iList.size()]));
		}
		LabelInfo[][] infos = infoList.toArray(new LabelInfo[infoList.size()][]);
		short [] used = new short[intList.size()];
		for(int i=0;i<used.length;i++){
			used[i] = intList.get(i);
		}
		silac.setInfo(infos);
		silac.setUsed(used);
		return silac;
	}
	
	public short [] getUsed(){
		return getLabelType().getUsed();
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
				frame.setTitle("LInfoSILACPanel");
				LInfoSILACPanel content = new LInfoSILACPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
