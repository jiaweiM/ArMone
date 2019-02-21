/* 
 ******************************************************************************
 * File: PhosPeptideListViewer.java * * * Created on 04-20-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.PhosPepCriteria;
import cn.ac.dicp.gp1809.proteome.gui.PeptideListPagedRowGettor;
import cn.ac.dicp.gp1809.proteome.gui.PeptideListViewerPanel;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-20-2009, 20:24:38
 */
public class PhosPeptideListViewerPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;
	private final PeptideListPagedRowGettor getter;
	private IPeptideCriteria<IPeptide> phoscriteria;
	private IPeptideCriteria<IPeptide> nonphoscriteria;
	private IPeptideCriteria<IPeptide>  lastFilter;
	
	private PeptideListViewerPanel peptideListViewerPanel0;
	private JPanel jPanel0;
	private JCheckBox jCheckBoxPhosphoFilter;
	private JFormattedTextField jFormattedTextField0;
	private JCheckBox jCheckBoxPhosInfo;
	private JLabel jLabel0;
	private JFormattedTextField jFormattedTextField1;
	private JCheckBox jCheckBoxNonPhosfilter;
	private JCheckBox jCheckBoxShowAll;
	private ButtonGroup buttonGroup1;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PhosPeptideListViewerPanel() {
		this(null);
	}

	public PhosPeptideListViewerPanel(PeptideListPagedRowGettor getter) {
		this.getter = getter;
		initComponents();
	}

	private void initComponents() {
    	setLayout(new GroupLayout());
    	add(getPeptideListViewerPanel0(), new Constraints(new Bilateral(0, 6, 862), new Bilateral(0, 66, 10, 506)));
    	add(getJPanel0(), new Constraints(new Bilateral(0, 0, 874), new Trailing(10, 10, 10)));
    	initButtonGroup1();
    	setSize(874, 572);
    }

	private void initButtonGroup1() {
    	buttonGroup1 = new ButtonGroup();
    	buttonGroup1.add(getJCheckBoxNonPhosfilter());
    	buttonGroup1.add(getJCheckBoxPhosphoFilter());
    	buttonGroup1.add(getJCheckBoxShowAll());
    }

	private JCheckBox getJCheckBoxShowAll() {
    	if (jCheckBoxShowAll == null) {
    		jCheckBoxShowAll = new JCheckBox();
    		jCheckBoxShowAll.setSelected(true);
    		jCheckBoxShowAll.setText("show all");
    		jCheckBoxShowAll.addItemListener(this);
    	}
    	return jCheckBoxShowAll;
    }

	private JCheckBox getJCheckBoxNonPhosfilter() {
    	if (jCheckBoxNonPhosfilter == null) {
    		jCheckBoxNonPhosfilter = new JCheckBox();
    		jCheckBoxNonPhosfilter.setText("Only show peptides without target PTM");
    		jCheckBoxNonPhosfilter.addItemListener(this);
    	}
    	return jCheckBoxNonPhosfilter;
    }

	private JFormattedTextField getJFormattedTextField1() {
		if (jFormattedTextField1 == null) {
			jFormattedTextField1 = new JFormattedTextField(new Character('n'));
			jFormattedTextField1.setAutoscrolls(true);
			jFormattedTextField1.setToolTipText("The variable modification symbols of target PTM, e.g. # *");
		}
		return jFormattedTextField1;
	}

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Symbols of PTM");
    		jLabel0.setToolTipText("The variable modification symbols of target PTM, e.g. # *");
    	}
    	return jLabel0;
    }

	private JCheckBox getJCheckBoxPhosInfo() {
    	if (jCheckBoxPhosInfo == null) {
    		jCheckBoxPhosInfo = new JCheckBox();
    		jCheckBoxPhosInfo.setText("Show phosphopeptides information");
    		jCheckBoxPhosInfo.addItemListener(this);
    	}
    	return jCheckBoxPhosInfo;
    }

	private JFormattedTextField getJFormattedTextField0() {
    	if (jFormattedTextField0 == null) {
    		jFormattedTextField0 = new JFormattedTextField();
    		jFormattedTextField0.setText("p");
    		jFormattedTextField0.setMinimumSize(new Dimension(12, 25));
    		jFormattedTextField0.setPreferredSize(new Dimension(19, 25));
    		jFormattedTextField0.setToolTipText("The variable modification symbols of target PTM, e.g. # *");
    	}
    	return jFormattedTextField0;
    }

	private JCheckBox getJCheckBoxPhosphoFilter() {
    	if (jCheckBoxPhosphoFilter == null) {
    		jCheckBoxPhosphoFilter = new JCheckBox();
    		jCheckBoxPhosphoFilter.setText("Only show PTM peptides");
    		jCheckBoxPhosphoFilter.addItemListener(this);
    	}
    	return jCheckBoxPhosphoFilter;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setLayout(new GroupLayout());
    		jPanel0.add(getJCheckBoxNonPhosfilter(), new Constraints(new Leading(315, 262, 10, 10), new Leading(31, 6, 6)));
    		jPanel0.add(getJCheckBoxPhosphoFilter(), new Constraints(new Leading(604, 10, 10), new Leading(30, 6, 6)));
    		jPanel0.add(getJCheckBoxShowAll(), new Constraints(new Leading(5, 262, 6, 6), new Leading(30, 6, 6)));
    		jPanel0.add(getJCheckBoxPhosInfo(), new Constraints(new Leading(315, 6, 6), new Leading(6, 6, 6)));
    		jPanel0.add(getJFormattedTextField1(), new Constraints(new Leading(194, 24, 6, 6), new Leading(5, 21, 6, 6)));
    		jPanel0.add(getJFormattedTextField0(), new Constraints(new Leading(164, 24, 6, 6), new Leading(4, 22, 6, 6)));
    		jPanel0.add(getJLabel0(), new Constraints(new Leading(26, 6, 6), new Leading(6, 6, 6)));
    	}
    	return jPanel0;
    }

	private PeptideListViewerPanel getPeptideListViewerPanel0() {
		if (peptideListViewerPanel0 == null) {
			peptideListViewerPanel0 = new PeptideListViewerPanel(this.getter);
			peptideListViewerPanel0.setMinimumSize(new Dimension(862, 500));
			peptideListViewerPanel0.setPreferredSize(new Dimension(862, 515));
		}
		return peptideListViewerPanel0;
	}
	
	/**
	 * Clean 
	 */
	public void forceClean() {
		if(this.peptideListViewerPanel0 != null)
			this.peptideListViewerPanel0.forceClean();
	}

	public IPeptideCriteria<IPeptide> getFilter(){
		return this.peptideListViewerPanel0.getUsedFilter();
	}
	
	public IPeptideCriteria<IPeptide> getPTMFilter(){
		return this.lastFilter;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {

		try {

			Object obj = e.getSource();

			if (obj == this.getJCheckBoxPhosphoFilter()) {
				if (this.getJCheckBoxPhosphoFilter().isSelected()) {
					
					phoscriteria = new PhosPepCriteria(true, this.getPhosSymbols());
					this.getPeptideListViewerPanel0().addFilter(phoscriteria);
				}
				else {
					if(this.phoscriteria != null) {
						this.getPeptideListViewerPanel0().removeFilter(phoscriteria);
						this.phoscriteria = null;
					}
				}
				this.lastFilter = phoscriteria;
				return;
			}
			
			
			if (obj == this.getJCheckBoxNonPhosfilter()) {
				if (this.getJCheckBoxNonPhosfilter().isSelected()) {
					
					nonphoscriteria = new PhosPepCriteria(false, this.getPhosSymbols());
					this.getPeptideListViewerPanel0().addFilter(nonphoscriteria);
				}
				else {
					if(this.nonphoscriteria != null) {
						this.getPeptideListViewerPanel0().removeFilter(nonphoscriteria);
						this.nonphoscriteria = null;
					}
				}
				this.lastFilter = nonphoscriteria;
				return;
			}
			
			/*
			if (obj == this.getJCheckBoxShowAll()) {
				if (this.getJCheckBoxShowAll().isSelected()) {
					
					if(this.nonphoscriteria != null) {
						this.getPeptideListViewerPanel0().removeFilter(nonphoscriteria);
						this.nonphoscriteria = null;
					}
					
					if(this.phoscriteria != null) {
						this.getPeptideListViewerPanel0().removeFilter(phoscriteria);
						this.phoscriteria = null;
					}
				}

				return;
			}
	*/
			if (obj == this.getJCheckBoxPhosInfo()) {
				
				if(this.getJCheckBoxPhosInfo().isSelected()) {
					PhosStatisticFrame frame = new PhosStatisticFrame(getter);
					frame.pack();
					frame.setLocationRelativeTo(this);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setVisible(true);
				}
				
				return;
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error",
			        JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}

		return;
	}
/*	
	private class PhosPepFilter implements IPeptideCriteria<IPeptide> {

        private static final long serialVersionUID = 1L;
		private char[] symbols;
		private boolean retainPhos;
		
		private PhosPepFilter(boolean retainPhos, char[] symbols) {
			this.symbols = symbols;
			this.retainPhos = retainPhos;
		}
		
		@Override
		public boolean filter(IPeptide pep) {

			if (pep.getPeptideSequence()
			        .getModificationNumber() == 0)
				return this.retainPhos? false : true;

			IModifSite[] sites = pep.getPeptideSequence()
			        .getModifications();
			for (IModifSite site : sites) {
				for (char symbol : symbols)
					if (site.symbol() == symbol)
						return this.retainPhos ? true : false;
			}

			return this.retainPhos? false : true;
		}

		@Override
		public PeptideType getPeptideType() {
			return getter.getPeptideType();
		}

//		@Override
//		public boolean preFilter(IPeptide pep) {
//			return true;
//		}

	}
*/
	
	/**
	 * The phosphorylation symbol
	 * 
	 * @return
	 */
	private char[] getPhosSymbols() {
		
		String s1 = this.getJFormattedTextField0().getText();
		String s2 = this.getJFormattedTextField1().getText();
		
		if(s1.length() == 0 && s2.length() ==0) {
			throw new NullPointerException("Set the symbol of phosphorylation first");
		}
		
		if(s1.length() == 0) {
			return new char[] {s2.charAt(0)};
		}
		
		if(s2.length() == 0)
			return new char[] {s1.charAt(0)};
		
		return new char[] {s1.charAt(0), s2.charAt(0)};
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
	 * Main entry of the class. Note: This class is only created so that you can
	 * easily preview the result at runtime. It is not expected to be managed by
	 * the designer. You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("PhosPeptideListViewer");
				PhosPeptideListViewerPanel content = new PhosPeptideListViewerPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
