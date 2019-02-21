package cn.ac.dicp.gp1809.proteome.penn;

import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import cn.ac.dicp.gp1809.util.gui.UIutilities;
import java.awt.Font;

class Information extends JDialog {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTextArea jTextArea = null;

	/**
	 * This is the default constructor
	 */
	public Information() {
		this(null);
	}
	
	public Information(JFrame parent) {
		super(parent, true);
		initialize();
		this.setLocation(UIutilities.getProperLocation(parent, this));
		this.setVisible(true);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(649, 590);
		this.setFont(new Font("Cambria", Font.PLAIN, 12));
		this.setContentPane(getJContentPane());
		this.setTitle("About");
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTextArea());
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setBounds(new Rectangle(0, 0, 640, 558));
			jTextArea.setEditable(false);
			jTextArea.setText("\n[Software Information]\n"+
					"Name\t: PENN (Probability estimator by k nearest neighbor algorithm)\n"+
					"Version\t:1.0.1\n"+
					"Author\t: Xinning Jiang(vext@163.com)\n"+
					"Contact\t: Prof. Hanfa Zou (hanfazou@dicp.ac.cn)\n"+
					"Address\t:Dalian Institute of Chemical Physics\n"+
					"\t 457 zhongshan Road, Dalian 116023, China\n"+
					"Homepage\t: http://bioanalysis.dicp.ac.cn/proteomics/software/PENN.html\n"+
					"Citation\t:\"Jiang, X.N.; Dong, X.L.; Ye, M.L.; Zou, H.F., An instance based algorithm for posterior" +
					"\n\tprobability estimation by target-decoy strategy to improve protein identifications\"\n\n"+
					
					
					"[Introduction]\nTarget-decoy database search strategy is often applied to determine the global false-discovery rate (FDR)" +
					"\nof peptide identifications in proteome researches. However, the confidence of individual peptide identification is" +
					"\ntypically not determined. In this study, we introduced an approach for the calculation of posterior probability" +
					"\nof individual peptide identification from the ��local false-discovery rate�� (local FDR), which is also determined" +
					"\nbased on target-decoy database search. The peptide identification scores outputted by SEQUEST were" +
					"\nweighted by their discriminating power using a Shannon information entropy based strategy. Then local FDR" +
					"\nof a peptide identification was calculated based on the fraction of decoy identifications among its nearest" +
					"\nneighbors within a small space defined by these weighted scores. It was demonstrated that the calculated" +
					"\nprobability matched the actual probability precisely and it provided powerful discriminating performance" +
					"\nbetween true positive and false positive identifications. And the sensitivity for peptide identification as well" +
					"\nas protein identification was significantly improved when the calculated probability was used to process" +
					"\ndifferent proteome datasets. As an instance based strategy, this algorithm provides a safe way for the posterior" +
					"\nprobability calculation and should work well for datasets with different characteristic.  " +
					
					
					"\n\n[Licence]" +
					"\nPENN is freely for acadamic usage." +
					"\nCommerical users please connect Prof. HanfaZou for licence.");
		}
		return jTextArea;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
