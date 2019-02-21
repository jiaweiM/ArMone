/* 
 ******************************************************************************
 * File: MyJTextArea.java * * * Created on 04-08-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import javax.swing.JTextArea;

/**
 * Add charet position auto scroll
 * 
 * @author Xinning
 * @version 0.1, 04-08-2010, 11:41:29
 */
public class MyJTextArea extends JTextArea {

    
    /**
     * 
     */
    private static final long serialVersionUID = -7917506343986125929L;

	/*
     * 
     */
    @Override
	public void append(String s) {
    	super.append(s);
    	super.setCaretPosition(super.getText().length());
    }

}
