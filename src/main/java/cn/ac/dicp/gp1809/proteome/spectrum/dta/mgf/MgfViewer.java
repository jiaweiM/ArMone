/* 
 ******************************************************************************
 * File: MgfViewer.java * * * Created on 2012-12-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.SimpleSpectrumPanel;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import javax.swing.GroupLayout.Alignment;

public class MgfViewer extends JPanel implements ListSelectionListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JTable jTableScanlist;
	private JScrollPane jScrollPaneScanlist;
	private JTree jTreeScanlist;
	private JTable jTablePeaklist;
	private JScrollPane jScrollPanePeaklist;
	private SimpleSpectrumPanel jSpecPanel;
	private HashMap <String, MS2PeakList> peaklistMap;
	private Object [][] scannames;
	private JButton jButtonClose;
	private JTextField jTextFieldScan;
	private JButton jButton0;

	public MgfViewer() {
		initComponents();
	}
	
	public MgfViewer(String file){
		
		MgfReader reader = null;
		
		try {
			reader = new MgfReader(file);
		} catch (DtaFileParsingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		this.peaklistMap = new HashMap <String, MS2PeakList>();
		MS2Scan scan = null;
		while((scan=reader.getNextMS2Scan())!=null){
			MS2PeakList peaklist = (MS2PeakList) scan.getPeakList();
			String name = scan.getScanName().getScanName();
			this.peaklistMap.put(name, peaklist);
		}
		reader.close();

		this.scannames = new Object [peaklistMap.size()][2];
		Iterator <String> it = peaklistMap.keySet().iterator();
		int id = 0;
		while(it.hasNext()){
			String name = it.next();
			this.scannames[id][0] = name;
			this.scannames[id][1] = peaklistMap.get(name).getPrecursePeak().getMz();
			id++;
		}
		
		initComponents();
	}
	
	public MgfViewer(File file){
		
		MgfReader reader = null;
		
		try {
			reader = new MgfReader(file);
		} catch (DtaFileParsingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		this.peaklistMap = new HashMap <String, MS2PeakList>();
		MS2Scan scan = null;
		while((scan=reader.getNextMS2Scan())!=null){
			MS2PeakList peaklist = (MS2PeakList) scan.getPeakList();
			String name = scan.getScanName().getScanName();
			this.peaklistMap.put(name, peaklist);
		}
		reader.close();
		
		this.scannames = new Object [peaklistMap.size()][2];
		Iterator <String> it = peaklistMap.keySet().iterator();
		int id = 0;
		while(it.hasNext()){
			String name = it.next();
			this.scannames[id][0] = name;
			this.scannames[id][1] = peaklistMap.get(name).getPrecursePeak().getMz();
			id++;
		}
		
		initComponents();
	}

	private void initComponents() {
		setSize(1200, 800);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJScrollPaneScanlist(), javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(20)
							.addComponent(getJTextFieldScan(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(15)
							.addComponent(getJButton0(), javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
					.addComponent(getSimpleSpectrumPanel(), javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJScrollPanePeaklist(), javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(20)
							.addComponent(getJButtonClose(), javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(getJScrollPaneScanlist(), javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(70)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJTextFieldScan(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getJButton0())))
				.addComponent(getSimpleSpectrumPanel(), javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(getJScrollPanePeaklist(), javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(60)
					.addComponent(getJButtonClose(), javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		setLayout(groupLayout);
	}

	private JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
			jButton0.setText("...");
			jButton0.addActionListener(this);
		}
		return jButton0;
	}

	private JTextField getJTextFieldScan() {
		if (jTextFieldScan == null) {
			jTextFieldScan = new JTextField();
			jTextFieldScan.setText("");
		}
		return jTextFieldScan;
	}

	public JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
		}
		return jButtonClose;
	}

	private SimpleSpectrumPanel getSimpleSpectrumPanel() {
		if (jSpecPanel == null) {
			jSpecPanel = new SimpleSpectrumPanel();
			javax.swing.GroupLayout gl_jSpecPanel = new javax.swing.GroupLayout(jSpecPanel);
			gl_jSpecPanel.setHorizontalGroup(
				gl_jSpecPanel.createParallelGroup(Alignment.LEADING)
					.addGap(0, 800, Short.MAX_VALUE)
			);
			gl_jSpecPanel.setVerticalGroup(
				gl_jSpecPanel.createParallelGroup(Alignment.LEADING)
					.addGap(0, 800, Short.MAX_VALUE)
			);
			jSpecPanel.setLayout(gl_jSpecPanel);
		}
		return jSpecPanel;
	}
	
	private JScrollPane getJScrollPaneScanlist() {
		if (jScrollPaneScanlist == null) {
			jScrollPaneScanlist = new JScrollPane();
			jScrollPaneScanlist.setViewportView(getJTableScanlist());
		}
		return jScrollPaneScanlist;
	}
	
	private JTable getJTableScanlist() {
		if (jTableScanlist == null) {
			if(this.scannames==null){
				jTableScanlist = new JTable();
				jTableScanlist.getSelectionModel().addListSelectionListener(this);
//				jTableScanlist.setEnabled(false);
			}else{
				jTableScanlist = new JTable(this.scannames, new String []{"Scan name", "mz"});
				jTableScanlist.getSelectionModel().addListSelectionListener(this);
//				jTableScanlist.setEnabled(false);
			}
		}
		return jTableScanlist;
	}
	
/*	private JTree getJTreeScanlist() {
		if (jTreeScanlist == null) {
			if(this.scannames==null){
				jTreeScanlist = new JTree();
				jTreeScanlist.getModel().addTreeModelListener(this);
			}else{
				jTreeScanlist = new JTree(this.scannames);
				jTreeScanlist.getModel().addTreeModelListener(this);
			}
		}
		return jTreeScanlist;
	}
*/
	private JScrollPane getJScrollPanePeaklist() {
		if (jScrollPanePeaklist == null) {
			jScrollPanePeaklist = new JScrollPane();
			jScrollPanePeaklist.setViewportView(getJTablePeaklist());
		}
		return jScrollPanePeaklist;
	}

	private JTable getJTablePeaklist() {
		if (jTablePeaklist == null) {
			jTablePeaklist = new JTable();
			jTablePeaklist.setEnabled(false);
		}
		return jTablePeaklist;
	}
	
	private JTable getJTablePeaklist(Object [][] objs) {
		jTablePeaklist = new JTable(objs, new String []{"mz", "Intensity"});
		jTablePeaklist.setEnabled(false);
		return jTablePeaklist;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		ListSelectionModel model = (ListSelectionModel) e.getSource();
		int first = e.getFirstIndex();
		int last = e.getLastIndex();
		
		if(model.isSelectedIndex(first)){
			
			Object [] obj = this.scannames[first];
			MS2PeakList peaklist = this.peaklistMap.get((String)obj[0]);
			IPeak [] peaks = peaklist.getPeakArray();
			Object [][] peaksobj = new Object [peaks.length][2];
			for(int i=0;i<peaks.length;i++){
				peaksobj[i][0] = peaks[i].getMz();
				peaksobj[i][1] = peaks[i].getIntensity();
			}
			this.getJScrollPanePeaklist().setViewportView(getJTablePeaklist(peaksobj));
			this.getSimpleSpectrumPanel().draw(peaklist);
			this.repaint();
			this.updateUI();
			
		}else if(model.isSelectedIndex(last)){
			
			Object [] obj = this.scannames[last];
			MS2PeakList peaklist = this.peaklistMap.get((String)obj[0]);
			IPeak [] peaks = peaklist.getPeakArray();
			Object [][] peaksobj = new Object [peaks.length][2];
			for(int i=0;i<peaks.length;i++){
				peaksobj[i][0] = peaks[i].getMz();
				peaksobj[i][1] = peaks[i].getIntensity();
			}
			this.getJScrollPanePeaklist().setViewportView(getJTablePeaklist(peaksobj));
			this.getSimpleSpectrumPanel().draw(peaklist);
			this.repaint();
			this.updateUI();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Object src = arg0.getSource();
		if(src==this.getJButton0()){
			String scanname = this.getJTextFieldScan().getText();
			for(int i=0;i<this.scannames.length;i++){
				String content = (String) scannames[i][0];
				if(content.contains(scanname)){
					this.jTableScanlist.getSelectionModel().setSelectionInterval(i, i);
					MS2PeakList peaklist = this.peaklistMap.get(content);
					IPeak [] peaks = peaklist.getPeakArray();
					Object [][] peaksobj = new Object [peaks.length][2];
					for(int j=0;j<peaks.length;j++){
						peaksobj[j][0] = peaks[j].getMz();
						peaksobj[j][1] = peaks[j].getIntensity();
					}
					this.getJScrollPanePeaklist().setViewportView(getJTablePeaklist(peaksobj));
					this.getSimpleSpectrumPanel().draw(peaklist);
					this.repaint();
					this.updateUI();
				}
			}
			return;
		}
	}

}
