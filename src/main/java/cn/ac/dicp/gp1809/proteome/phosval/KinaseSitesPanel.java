/* 
 ******************************************************************************
 * File:KinaseSites.java * * * Created on 2010-4-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.stream.XMLStreamException;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.Kinase;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.KinaseXMLReader;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.ModifSequGetter;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

//VS4E -- DO NOT REMOVE THIS LINE!
public class KinaseSitesPanel extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel jLabelProSeq;
	private JLabel jLabelKinase;
	private JTextField jTextFieldProSeq;
	private JTextField jTextFieldKinase;
	private JButton jButtonSelePS;
	private JButton jButtonStart;
	
	private MyJFileChooser proSeqChoose;
	private MyJFileChooser outChoose;
	
	private String kinaseFile = "PhosphoMotif/PhosphoMotif.xml";
	private ModifSequGetter task;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabelProSeq(), new Constraints(new Leading(40, 10, 10), new Leading(40, 10, 10)));
		add(getJLabelKinase(), new Constraints(new Leading(40, 10, 10), new Leading(80, 10, 10)));
		add(getJTextFieldProSeq(), new Constraints(new Leading(170, 108, 10, 10), new Leading(40, 10, 10)));
		add(getJTextFieldKinase(), new Constraints(new Leading(170, 108, 10, 10), new Leading(80, 10, 10)));
		add(getJButtonSelePS(), new Constraints(new Leading(310, 10, 10), new Leading(40, 10, 10)));
		add(getJButtonStart(), new Constraints(new Leading(170, 10, 10), new Leading(150, 10, 10)));
		setSize(400, 220);
	}

	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("Output");
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}

	private JButton getJButtonSelePS() {
		if (jButtonSelePS == null) {
			jButtonSelePS = new JButton();
			jButtonSelePS.setText("select");
			jButtonSelePS.addActionListener(this);
		}
		return jButtonSelePS;
	}

	private JTextField getJTextFieldProSeq() {
		if (jTextFieldProSeq == null) {
			jTextFieldProSeq = new JTextField();
		}
		return jTextFieldProSeq;
	}

	private JTextField getJTextFieldKinase() {
		if (jTextFieldKinase == null) {
			jTextFieldKinase = new JTextField();
		}
		return jTextFieldKinase;
	}
	private JLabel getJLabelKinase() {
		if (jLabelKinase == null) {
			jLabelKinase = new JLabel();
			jLabelKinase.setText("Kinase");
		}
		return jLabelKinase;
	}

	private JLabel getJLabelProSeq() {
		if (jLabelProSeq == null) {
			jLabelProSeq = new JLabel();
			jLabelProSeq.setText("Protein Sequence");
		}
		return jLabelProSeq;
	}

	private MyJFileChooser getProSeqChooser() {
		if (this.proSeqChoose == null) {
			this.proSeqChoose = new MyJFileChooser();
			this.proSeqChoose.setFileFilter(new String[] { "fasta" },
			        "fasta (*.fasta)");
		}
		return proSeqChoose;
	}
	
	private MyJFileChooser getOutputChooser() {
		if (this.outChoose == null) {
			this.outChoose = new MyJFileChooser();
			this.outChoose.setFileFilter(new String[] { "csv" },
			        "csv (*.csv)");
		}
		return outChoose;
	}

	public KinaseSitesPanel() {
		initComponents();
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();
		try {
			if(obj==this.getJButtonSelePS()){
				int value = this.getProSeqChooser().showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldProSeq().setText(
					        this.proSeqChoose.getSelectedFile().getAbsolutePath());
				return;
			}
			
			if(obj==this.getJButtonStart()){
				int ap = this.getOutputChooser().showSaveDialog(this);
				if(ap == JFileChooser.APPROVE_OPTION) {
					String outPut = this.getOutputChooser().getSelectedFile().getAbsolutePath()+".csv";
					if(task==null)
						getTask();
					
					task.processNext(outPut);
				}
			}
			
		}catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error",
			        JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}

	}
	
	public void getTask() throws IOException, XMLStreamException{

		String proSeq = this.getJTextFieldProSeq().getText();

		if (proSeq.length() == 0) {
			throw new NullPointerException("Select the database first.");
		}
		

		String kinaInput = this.getJTextFieldKinase().getText();
		if (kinaInput.length() == 0) {
			throw new NullPointerException("Input a motif description first.");
		}
		
		KinaseXMLReader kinaseReader = new KinaseXMLReader(kinaseFile, true);
		Kinase kinase = null;
		
		HashMap <String, Kinase> kinaseMap = kinaseReader.getKinaseNameMap();
		if(kinaseMap.containsKey(kinaInput)){
			kinase = kinaseMap.get(kinaInput);
		}
		else{
			int i = 0;
			Kinase k = null;
			Set <String> kinaseSet = kinaseMap.keySet();
			for(String str:kinaseSet){
				if(str.startsWith(kinaInput)){
					i++;
					k = kinaseMap.get(str);
				}
			}
			if(i==1){
				kinase = k;
			}
			else if(i==0){
				throw new NullPointerException("Motif description is not in the list.");
			}
			else if(i>1){
				throw new NullPointerException("More than one kinase are fit for this description.");
			}
		}
		
		ModifSequGetter modifGetter = new ModifSequGetter(proSeq, kinase);
		this.task = modifGetter;
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

	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("Kinase");
				KinaseSitesPanel content = new KinaseSitesPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
