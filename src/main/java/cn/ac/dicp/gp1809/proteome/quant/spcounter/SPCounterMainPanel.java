package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintStream;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.ac.dicp.gp1809.util.gui.JTextAreaPrintStream;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JCheckBox;

public class SPCounterMainPanel extends JFrame implements ActionListener{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JMenuBar jJMenuBar = null;

	private JMenu helpMenu = null;

	private JMenuItem aboutMenuItem = null;

	private JButton jButtonBrowse = null;

	private JTextField jTextField = null;

	private JRadioButton jRadioButtonAverage = null;

	private JLabel jLabelAverage = null;

	JRadioButton jRadioButtonMedian = null;

	private JLabel jLabelMedian = null;

	private JButton jButtonGo = null;

	private JScrollPane jScrollPane = null;

	private JTextArea jTextArea = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JCheckBox jCheckBox = null;
	
	/**
	 * This method initializes jButtonBrowse	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonBrowse() {
		if (jButtonBrowse == null) {
			jButtonBrowse = new JButton();
			jButtonBrowse.setBounds(new java.awt.Rectangle(384,23,24,13));
			jButtonBrowse.setText("...");
			jButtonBrowse.addActionListener(this);
		}
		return jButtonBrowse;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(new java.awt.Rectangle(9,17,367,23));
			jTextField.setEditable(false);
		}
		return jTextField;
	}

	/**
	 * This method initializes jRadioButtonAverage	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonAverage() {
		if (jRadioButtonAverage == null) {
			jRadioButtonAverage = new JRadioButton();
			jRadioButtonAverage.setBounds(new Rectangle(238, 46, 21, 21));
			jRadioButtonAverage.setSelected(true);
		}
		return jRadioButtonAverage;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonMedian() {
		if (jRadioButtonMedian == null) {
			jRadioButtonMedian = new JRadioButton();
			jRadioButtonMedian.setBounds(new Rectangle(328, 45, 21, 21));
		}
		return jRadioButtonMedian;
	}

	/**
	 * This method initializes jButtonGo	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonGo() {
		if (jButtonGo == null) {
			jButtonGo = new JButton();
			jButtonGo.setBounds(new Rectangle(177, 117, 84, 16));
			jButtonGo.setText("Start");
			jButtonGo.addActionListener(this);
		}
		return jButtonGo;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(0, 142, 425, 87));
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setEditable(false);
		}
		return jTextArea;
	}

	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setBounds(new Rectangle(237, 80, 21, 21));
		}
		return jCheckBox;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		CopyRightPane.ShowCopyRightPane(null);
		SPCounterMainPanel application = new SPCounterMainPanel();
		application.setVisible(true);
	}

	/**
	 * This is the default constructor
	 */
	public SPCounterMainPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getJJMenuBar());
		this.setSize(432, 282);
		this.setContentPane(getJContentPane());
		this.setTitle("SpCounter v2.3");
		this.setResizable(false);
		PrintStream stream = new JTextAreaPrintStream(this.getJTextArea());
		System.setOut(stream);
		System.setErr(stream);
		int[] l = getGuiLocation(this);
		this.setLocation(l[0], l[1]);
	}
	
	private static int[] getGuiLocation(SPCounterMainPanel gui){
		int[] location = new int[2];
		int tempi,tempj;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimention = toolkit.getScreenSize();
		
		location[0] = (tempi=dimention.width/2-gui.getWidth()/2) >=0 ? tempi : 0;
		location[1] = (tempj=dimention.height/2-gui.getHeight()/2) >=0 ? tempj : 0;
		
		return location;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(11, 81, 216, 18));
			jLabel1.setText("Is output peptide quantity info ?");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(11, 45, 215, 21));
			jLabel.setText("The spectra used for quantification:");
			jLabelMedian = new JLabel();
			jLabelMedian.setBounds(new Rectangle(356, 46, 57, 19));
			jLabelMedian.setText("Median");
			jLabelAverage = new JLabel();
			jLabelAverage.setBounds(new Rectangle(264, 47, 58, 17));
			jLabelAverage.setText("Average");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJButtonBrowse(), null);
			jContentPane.add(getJTextField(), null);
			jContentPane.add(getJRadioButtonAverage(), null);
			jContentPane.add(jLabelAverage, null);
			jContentPane.add(getJRadioButtonMedian(), null);
			jContentPane.add(jLabelMedian, null);
			jContentPane.add(getJButtonGo(), null);
			jContentPane.add(getJScrollPane(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJCheckBox(), null);
			ButtonGroup group = new ButtonGroup();
			group.add(this.jRadioButtonAverage);
			group.add(this.jRadioButtonMedian);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(this);
		}
		return aboutMenuItem;
	}

	//Previously selected file;
	private File preFile = null;
	private JFileChooser chooser;
	
	private Thread curtThread;
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj == this.getJButtonBrowse()){
			if(chooser == null){
				chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			}
			chooser.setCurrentDirectory(preFile);
			int returnrev = chooser.showOpenDialog(SPCounterMainPanel.this);
			if(returnrev==JFileChooser.APPROVE_OPTION){
				preFile = chooser.getSelectedFile();
				jTextField.setText(preFile.getAbsolutePath());
			}
			
			return ;
		}
		
		if(obj == this.getJButtonGo()){
			if(curtThread==null||!curtThread.isAlive())
				(curtThread = new Thread(){
					@Override
					public void run(){
						jTextArea.setText("");
						String s = jTextField.getText();
						boolean type = getJRadioButtonAverage().isSelected();
						boolean pep = getJCheckBox().isSelected();
						if(s!=null&&s.length()>3)
							try {
								SCComparator c = new SCComparator(s, pep, type);
//								Comparator.main(new String[]{s,String.valueOf(pep),String.valueOf(type)});
								c.writeResult();
							} catch (Exception e) {
								e.printStackTrace();
							}
					}
				}).start();
			
			return ;
		}
		
		if(obj == this.getAboutMenuItem()){
			
			new Information(this);
			
//			JOptionPane.showMessageDialog(this, "Quantification using spectra counter algorithm.\n" +
//												"            Author: Xinning Jiang\n" +
//												"             All right reversed\n", 
//					"About SpCounter v2", JOptionPane.INFORMATION_MESSAGE);
			
			return ;
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
