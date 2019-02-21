/*
 ******************************************************************************
 * File: SixTurnOverFrame.java * * * Created on 2013-3-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover.gui;

import cn.ac.dicp.gp1809.proteome.quant.label.multiple.MutilDataProcessor2;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

//VS4E -- DO NOT REMOVE THIS LINE!
public class SixTurnOverFrame extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    private JTable jTableFile;
    private JTable jTableTime;
    private JScrollPane jScrollPaneFile;
    private JScrollPane jScrollPaneTime;
    private JButton jButtonClose;
    private JButton jButtonStart;
    private JButton jButtonClear;
    private JProgressBar jProgressBar0;
    private JCheckBox jCheckBoxDraw;
    private MyJFileChooser xmlChooser;
    private MyJFileChooser xlsChooser;
    private JLabel jLabelOutput;
    private JTextField jTextFieldOutput;
    private JButton jButtonOutput;


    public SixTurnOverFrame()
    {
        initComponents();
    }

    private static void installLnF()
    {
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
     * Main entry of the class. Note: This class is only created so that you can easily preview the result at runtime.
     * It is not expected to be managed by the designer. You can modify it as you like.
     */
    public static void main(String[] args)
    {
        installLnF();
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                SixTurnOverFrame frame = new SixTurnOverFrame();
                frame.setDefaultCloseOperation(SixTurnOverFrame.EXIT_ON_CLOSE);
                frame.setTitle("SixTurnOverFrame");
                frame.getContentPane().setPreferredSize(frame.getSize());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private void initComponents()
    {
        setLayout(new GroupLayout());
        add(getJScrollPaneFile(), new Constraints(new Leading(0, 270, 0, 0), new Leading(0, 235, 10, 10)));
        add(getJScrollPaneTime(), new Constraints(new Leading(268, 274, 0, 0), new Leading(0, 235, 10, 10)));
        add(getJButtonClose(), new Constraints(new Leading(450, 10, 10), new Leading(340, 12, 12)));
        add(getJButtonStart(), new Constraints(new Leading(350, 10, 10), new Leading(340, 12, 12)));
        add(getJButtonClear(), new Constraints(new Leading(250, 10, 10), new Leading(340, 12, 12)));
        add(getJProgressBar0(), new Constraints(new Bilateral(20, 20, 10), new Leading(305, 12, 12)));
        add(getJLabelOutput(), new Constraints(new Leading(25, 10, 10), new Leading(260, 10, 10)));
        add(getJButtonOutput(), new Constraints(new Leading(470, 10, 10), new Leading(256, 10, 10)));
        add(getJTextFieldOutput(), new Constraints(new Leading(85, 355, 10, 10), new Leading(256, 30, 10, 10)));
        add(getJCheckBoxDraw(), new Constraints(new Leading(25, 10, 10), new Leading(340, 10, 10)));
        setSize(540, 400);
        setResizable(false);
    }

    private JScrollPane getJScrollPaneFile()
    {
        if (jScrollPaneFile == null) {
            jScrollPaneFile = new JScrollPane();
            jScrollPaneFile.setViewportView(getJTableFile());
        }
        return jScrollPaneFile;
    }

    private JTable getJTableFile()
    {
        if (jTableFile == null) {
            jTableFile = new JTable();

            jTableFile.setModel(new DefaultTableModel(new Object[][]{{null}, {null},
                    {null}, {null}, {null}, {null}, {null}, {null},
                    {null}, {null}, {null}, {null},},
                    new String[]{"File"}));

            jTableFile.addMouseListener(new MouseAdapter()
            {

                public void mouseClicked(MouseEvent event)
                {
                    jTable0MouseMouseClicked(event);
                }

                public void mouseEntered(MouseEvent event)
                {
                    jTable0MouseMouseEntered(event);
                }
            });
        }
        return jTableFile;
    }

    private JScrollPane getJScrollPaneTime()
    {
        if (jScrollPaneTime == null) {
            jScrollPaneTime = new JScrollPane();
            jScrollPaneTime.setViewportView(getJTableTime());
        }
        return jScrollPaneTime;
    }

    private JTable getJTableTime()
    {
        if (jTableTime == null) {
            jTableTime = new JTable();

            jTableTime.setModel(new DefaultTableModel(new Object[][]{{null, null, null}, {null, null, null},
                    {null, null, null}, {null, null, null}, {null, null, null}, {null, null, null},
                    {null, null, null}, {null, null, null}, {null, null, null}, {null, null, null},
                    {null, null, null}, {null, null, null},},
                    new String[]{"Time point 2/1", "Time point 4/3", "Time point 6/5"}));
        }
        return jTableTime;
    }

    private MyJFileChooser getxmlChooser()
    {
        if (this.xmlChooser == null) {
            this.xmlChooser = new MyJFileChooser();
            this.xmlChooser.setMultiSelectionEnabled(true);
            this.xmlChooser.setFileFilter(new String[]{"pxml"},
                    " Peptide quantitation XML file (*.pxml)");
        }
        return xmlChooser;
    }

    public String getOutput()
    {
        return this.getJTextFieldOutput().getText();
    }

    private MyJFileChooser getOutchooser()
    {
        if (this.xlsChooser == null) {
            this.xlsChooser = new MyJFileChooser();
            this.xlsChooser.setFileFilter(new String[]{"xls"},
                    " protein and peptide turnover result (*.xls)");
        }
        return xlsChooser;
    }

    private JButton getJButtonOutput()
    {
        if (jButtonOutput == null) {
            jButtonOutput = new JButton();
            jButtonOutput.setText("...");
            jButtonOutput.addActionListener(this);
        }
        return jButtonOutput;
    }

    private JTextField getJTextFieldOutput()
    {
        if (jTextFieldOutput == null) {
            jTextFieldOutput = new JTextField();
        }
        return jTextFieldOutput;
    }

    private JLabel getJLabelOutput()
    {
        if (jLabelOutput == null) {
            jLabelOutput = new JLabel();
            jLabelOutput.setText("Output");
        }
        return jLabelOutput;
    }

    private JCheckBox getJCheckBoxDraw()
    {
        if (jCheckBoxDraw == null) {
            jCheckBoxDraw = new JCheckBox();
            jCheckBoxDraw.setText("Draw curve");
            jCheckBoxDraw.setSelected(true);
        }
        return jCheckBoxDraw;
    }

    public JProgressBar getJProgressBar0()
    {
        if (jProgressBar0 == null) {
            jProgressBar0 = new JProgressBar();
        }
        return jProgressBar0;
    }

    public JButton getJButtonStart()
    {
        if (jButtonStart == null) {
            jButtonStart = new JButton();
            jButtonStart.setText("Start");
            jButtonStart.addActionListener(this);
        }
        return jButtonStart;
    }

    public JButton getJButtonClose()
    {
        if (jButtonClose == null) {
            jButtonClose = new JButton();
            jButtonClose.setText("Close");
            jButtonClose.addActionListener(this);
        }
        return jButtonClose;
    }

    public JButton getJButtonClear()
    {
        if (jButtonClear == null) {
            jButtonClear = new JButton();
            jButtonClear.setText("Clear");
            jButtonClear.addActionListener(this);
        }
        return jButtonClear;
    }

    private void jTable0MouseMouseClicked(MouseEvent event)
    {

        java.awt.Point p = event.getPoint();
        int rowIndex = this.jTableFile.rowAtPoint(p);
        int columnIndex = this.jTableFile.columnAtPoint(p);

        int rowCount = this.jTableFile.getRowCount();
        int columnCount = this.jTableFile.getColumnCount();

        DefaultTableModel model = (DefaultTableModel) jTableFile.getModel();

        int value = this.getxmlChooser().showOpenDialog(this);
        if (value == JFileChooser.APPROVE_OPTION) {
            File file = this.getxmlChooser().getSelectedFile();
            model.setValueAt(file, rowIndex, columnIndex);
        }
    }

    private void jTable0MouseMouseEntered(MouseEvent event)
    {
        this.jTableFile.setToolTipText("Click to add files in this column");
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        // TODO Auto-generated method stub

        Object obj = arg0.getSource();

        if (obj == this.getJButtonClose()) {
            this.dispose();
            return;
        }

        if (obj == this.getJButtonClear()) {

            jTableFile.setModel(new DefaultTableModel(new Object[][]{{null}, {null},
                    {null}, {null}, {null}, {null}, {null}, {null},
                    {null}, {null}, {null}, {null},},
                    new String[]{"File"}));

            jTableTime.setModel(new DefaultTableModel(new Object[][]{{null, null, null}, {null, null, null},
                    {null, null, null}, {null, null, null}, {null, null, null}, {null, null, null},
                    {null, null, null}, {null, null, null}, {null, null, null}, {null, null, null},
                    {null, null, null}, {null, null, null},},
                    new String[]{"Time point 2/1", "Time point 4/3", "Time point 6/5"}));

            this.repaint();
            return;
        }

        if (obj == this.getJButtonOutput()) {
            int value = this.getOutchooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION)
                this.getJTextFieldOutput().setText(
                        this.getOutchooser().getSelectedFile().getAbsolutePath() + ".xls");
            return;
        }

        if (obj == this.getJButtonStart()) {

            HashMap<String, double[]> map = new HashMap<String, double[]>();
            HashSet<Double> timeset = new HashSet<Double>();
            int rowCount = this.jTableFile.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                Object fileobj = jTableFile.getValueAt(i, 0);
                if (fileobj != null) {
                    String path = ((File) fileobj).getAbsolutePath();
                    Object obj0 = this.jTableTime.getValueAt(i, 0);
                    Object obj1 = this.jTableTime.getValueAt(i, 1);
                    Object obj2 = this.jTableTime.getValueAt(i, 2);
                    double[] times = new double[3];

                    if (obj0 == null) {
                        throw new NullPointerException("Time point is missing.");
                    } else {
                        times[0] = Double.parseDouble((String) obj0);
                        timeset.add(times[0]);
                    }
                    if (obj1 == null) {
                        throw new NullPointerException("Time point is missing.");
                    } else {
                        times[1] = Double.parseDouble((String) obj1);
                        timeset.add(times[1]);
                    }
                    if (obj2 == null) {
                        throw new NullPointerException("Time point is missing.");
                    } else {
                        times[2] = Double.parseDouble((String) obj2);
                        timeset.add(times[2]);
                    }

                    map.put(path, times);
                }
            }

            double[] times = new double[timeset.size()];
            int id = 0;
            for (Double dd : timeset) {
                times[id++] = dd;
            }
            Arrays.sort(times);

            String output = this.getJTextFieldOutput().getText();
            if (output == null || output.length() == 0) {
                throw new NullPointerException("The output file path is null.");
            }

            boolean draw = this.getJCheckBoxDraw().isSelected();
            this.getJButtonClear().setEnabled(false);
            this.getJButtonStart().setEnabled(false);

            try {

                TurnOverProcessThread thread = new TurnOverProcessThread(times, output, draw, map, jProgressBar0, this);
                thread.start();

            } catch (Exception e1) {
                JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }

            return;
        }

    }

    private class TurnOverProcessThread extends Thread
    {

        private double[] times;
        private String output;
        private boolean draw;
        private HashMap<String, double[]> map;
        private JProgressBar bar;
        private SixTurnOverFrame frame;

        private TurnOverProcessThread(double[] times, String output, boolean draw, HashMap<String, double[]> map,
                JProgressBar bar, SixTurnOverFrame frame)
        {

            this.times = times;
            this.output = output;
            this.draw = draw;
            this.map = map;
            this.bar = bar;
            this.frame = frame;
        }

        public void run()
        {
            bar.setStringPainted(true);
            bar.setString("Reading...");
            bar.setIndeterminate(true);

            MutilDataProcessor2 processor = new MutilDataProcessor2(times, output, draw);
            try {
                processor.read(map);
                bar.setString("Writing...");
                processor.write();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            jProgressBar0.setString("Complete");
            try {
                sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            frame.dispose();
        }
    }
}
