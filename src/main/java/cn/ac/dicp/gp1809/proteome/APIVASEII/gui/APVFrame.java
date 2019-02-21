/* 
 ******************************************************************************
 * File: APVFrame.java * * * Created on 05-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.util.gui.AboutDlg;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-19-2009, 10:00:58
 */
public class APVFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	
	public static final String VERSION_INFO = "[Software Information]\n"+
	"Name\t: APIVASE (Automatic phosphopeptide identification validating algorithm by MS2/MS3)\n"+
	"Version\t: 2.6 (02-06-2009)\n"+
	"Author\t: Xinning Jiang(vext@163.com)\n"+
	"Contact\t: Prof. Minliang Ye (mingliang@dicp.ac.cn)\n" +
	"\t  Prof. Hanfa Zou (hanfazou@dicp.ac.cn)\n"+
	"Address\t:Dalian Institute of Chemical Physics\n"+
	"\t 457 zhongshan Road, Dalian 116023, China\n"+
	"Homepage\t: http://bioanalysis.dicp.ac.cn/proteomics/software/APIVASE.html\n"+
	"Citation\t:\"Jiang, X. N.; Han, G. H.; Feng, S.; Jiang, X. G.; Ye, M. L.; Yao, X. B.; Zou, H. F., Automatic" +
	"Validation of Phosphopeptide Identifications by the MS2/MS3 Target-Decoy Search Strategy." +
	" J. Proteome Res. 2008, 7, (4), 1640-1649\"\n\n"+
	
	
	"[Introduction]\n" +
	"Manual checking is commonly employed to validate the phosphopeptide identifications from database " +
	"searching of tandem mass spectra. However it is very time consuming and labor intensive as the number of " +
	"phosphopeptide identifications increase greatly. APIVASE (Automatic Phosphopeptide Identification Validating " +
	"algorithm for SEQUEST) was developed for phosphopeptide validation by combining the information obtained " +
	"from MS2 spectra and its corresponding neutral loss MS3 spectra. The valid MS2 and MS3 pairs were extracted" +
	"for database searching using target-decoy database. And the resulting MS2 and MS3 search results were " +
	"combined. Three new defined scores were used to filter phosphopeptide identifications with specific " +
	"false-discovery rate (FDR). Compared with the approach based on either MS2 or MS3, this combined MS2/MS3 " +
	"approach allowed the identification of phosphopeptides with relative low quality spectra, which significantly " +
	"improve the sensitivity for phosphopeptide identification. " +
	
	
	"\n\n[Licence]\n" +
	"All right reserved by Hanfa Zou & Mingliang Ye at Dalian Institute of Chemical Physics in China.\n" +
	"Free for academic and non commercial usage.\n" +
	"Commercial users please contact Prof. Hanfa Zou or Prof. Mingliang Ye and get the licence.";
	
	
	
	private JMenuItem jMenuItemAbout;
	private JMenu jMenu0;
	private JMenuBar jMenuBar0;
	private APVPanel aPVPanel0;


	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";


	public APVFrame() {
		initComponents();
	}

	private void initComponents() {
    	setTitle("MS2/MS3 accurate phosphopeptide identification strategy");
    	setLayout(new GroupLayout());
    	add(getAPVPanel0(), new Constraints(new Bilateral(6, 6, 827), new Bilateral(6, 10, 10, 523)));
    	setJMenuBar(getJMenuBar0());
    	setSize(857, 583);
    }

	private APVPanel getAPVPanel0() {
    	if (aPVPanel0 == null) {
    		aPVPanel0 = new APVPanel();
    		aPVPanel0.getJButtonClose().addActionListener(this);
    	}
    	return aPVPanel0;
    }

	private JMenuBar getJMenuBar0() {
    	if (jMenuBar0 == null) {
    		jMenuBar0 = new JMenuBar();
    		jMenuBar0.add(getJMenu0());
    	}
    	return jMenuBar0;
    }

	private JMenu getJMenu0() {
    	if (jMenu0 == null) {
    		jMenu0 = new JMenu();
    		jMenu0.setText("Help");
    		jMenu0.add(getJMenuItemAbout());
    	}
    	return jMenu0;
    }

	private JMenuItem getJMenuItemAbout() {
    	if (jMenuItemAbout == null) {
    		jMenuItemAbout = new JMenuItem();
    		jMenuItemAbout.setText("About APIVASE");
    		jMenuItemAbout.addActionListener(this);
    	}
    	return jMenuItemAbout;
    }
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
    public void actionPerformed(ActionEvent e) {
		
		Object obj = e.getSource();
		
		try {
			
			if(obj == this.getJMenuItemAbout()) {
				AboutDlg dlg = new AboutDlg(this);
				dlg.setAboutInformation(VERSION_INFO);
				dlg.setVisible(true);
				
				return ;
			}
			
			
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
		
		if(obj==this.aPVPanel0.getJButtonClose()){
			this.dispose();
			return;
		}
		
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
				APVFrame frame = new APVFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("APVFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
