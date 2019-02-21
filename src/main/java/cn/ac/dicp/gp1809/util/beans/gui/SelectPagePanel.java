/* 
 ******************************************************************************
 * File: SelectPagePanel.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The panel used for the selection of pages
 * 
 * @author Xinning
 * @version 0.1, 04-10-2009, 10:46:04
 */
public class SelectPagePanel extends JPanel implements MouseListener,
        KeyListener {

	private static final long serialVersionUID = 1L;
	private IPageSelector selector;
	//The index of current page
	private int pageIdx;

	private JLabel jLabelFirst;
	private JLabel jLabelPre;
	private JFormattedTextField jFormattedTextFieldPage;
	private JLabel jLabelNext;
	private JLabel jLabelLast;
	private JLabel jLabel5;
	private JLabel jLabel4;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public SelectPagePanel() {
		initComponents();
	}

	public SelectPagePanel(IPageSelector selector) {
		this.selector = selector;
		initComponents();

		this.refreshPageIdx();
	}
	
	/**
	 * Set the page selector
	 * 
	 * @param selector
	 */
	public void setPageSelector(IPageSelector selector) {
		this.selector = selector;
		this.refreshPageIdx();
	}

	private void initComponents() {
    	add(getJLabelFirst());
    	add(getJLabelPre());
    	add(getJFormattedTextFieldPage());
    	add(getJLabel5());
    	add(getJLabel4());
    	add(getJLabelNext());
    	add(getJLabelLast());
    	setSize(172, 28);
    }

	private JLabel getJLabel4() {
    	if (jLabel4 == null) {
    		jLabel4 = new JLabel();
    		jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
    		jLabel4.setText("1");
    		jLabel4.setMinimumSize(new Dimension(35, 18));
    		jLabel4.setPreferredSize(new Dimension(30, 18));
    		jLabel4.setMaximumSize(new Dimension(35, 18));
    	}
    	return jLabel4;
    }

	private JLabel getJLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("/");
		}
		return jLabel5;
	}

	private JLabel getJLabelLast() {
		if (jLabelLast == null) {
			jLabelLast = new JLabel();
			jLabelLast.setText(">|");
			jLabelLast.addMouseListener(this);
		}
		return jLabelLast;
	}

	private JLabel getJLabelNext() {
		if (jLabelNext == null) {
			jLabelNext = new JLabel();
			jLabelNext.setText(">>");
			jLabelNext.addMouseListener(this);
		}
		return jLabelNext;
	}

	private JFormattedTextField getJFormattedTextFieldPage() {
    	if (jFormattedTextFieldPage == null) {
    		jFormattedTextFieldPage = new JFormattedTextField(NumberFormat.getIntegerInstance());
    		jFormattedTextFieldPage.setHorizontalAlignment(SwingConstants.CENTER);
    		jFormattedTextFieldPage.setText("1");
    		jFormattedTextFieldPage.setMinimumSize(new Dimension(35, 22));
    		jFormattedTextFieldPage.setPreferredSize(new Dimension(35, 22));
    		jFormattedTextFieldPage.addKeyListener(this);
    	}
    	return jFormattedTextFieldPage;
    }

	private JLabel getJLabelPre() {
		if (jLabelPre == null) {
			jLabelPre = new JLabel();
			jLabelPre.setText("<<");
			jLabelPre.addMouseListener(this);
		}
		return jLabelPre;
	}

	private JLabel getJLabelFirst() {
		if (jLabelFirst == null) {
			jLabelFirst = new JLabel();
			jLabelFirst.setText("|<");
			jLabelFirst.addMouseListener(this);
		}
		return jLabelFirst;
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
	 * Refresh the page index number
	 */
	private void refreshPageIdx() {
		this.jFormattedTextFieldPage.setText(String.valueOf(this.pageIdx + 1));
		if(this.selector != null)
			this.jLabel4.setText(String.valueOf(this.selector.totalPages()));
	}
	
	/**
	 * Select the page with specific index
	 * 
	 * @param pageIdx
	 */
	public void select(int pageIdx) {
		this.pageIdx = pageIdx;
		this.refreshPageIdx();
		this.selector.selectPage(pageIdx);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Object obj = e.getSource();

		if (this.selector != null) {
			if (obj == this.jLabelFirst) {
				this.pageIdx = 0;
			}

			else if (obj == this.jLabelLast) {
				int tpages = this.selector.totalPages();
				this.pageIdx = tpages == 0 ? 0 : tpages - 1;
			}

			else if (obj == this.jLabelNext) {
				if (++pageIdx < this.selector.totalPages())
					this.selector.selectPage(pageIdx);
				else
					this.pageIdx--;
			}

			else if (obj == this.jLabelPre) {
				if (--pageIdx >= 0)
					this.selector.selectPage(pageIdx);
				else
					this.pageIdx++;
			}
			
			this.select(this.pageIdx);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		char typed = e.getKeyChar();
		Object obj = e.getSource();

		if (obj == this.jFormattedTextFieldPage) {
			if (typed == '\n') {
				this.jFormattedTextFieldPage.requestFocus(false);

				int idx = Integer.parseInt(this.jFormattedTextFieldPage
				        .getText()) - 1;
				if (idx != this.pageIdx) {
					
					if(this.selector != null) {
						if(idx < this.selector.totalPages() && idx >=0) {
							this.pageIdx = idx;
							this.selector.selectPage(this.pageIdx);
						}
						else
							//do nothing
							;
					}
					
					this.refreshPageIdx();
				}
			}
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
				frame.setTitle("SelectPagePanel");
				SelectPagePanel content = new SelectPagePanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
