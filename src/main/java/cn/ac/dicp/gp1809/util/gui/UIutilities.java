/*
 ******************************************************************************
 * File: UIutilities.java * * * Created on 04-05-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashSet;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

/**
 * 
 * Some useful static tools for the construction easy used the UI (user
 * interface)
 * 
 * <p>
 * Changes:
 * <li>0.1.2, 04-09-2009: add method {@link #getWindowForComponent(Component)}
 * 
 * @author Xinning
 * @version 0.1.2, 04-09-2009, 16:34:17
 */
public final class UIutilities {

	private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

	/**
	 * Get the proper location of new pop up frames.
	 * <p>
	 * <b>If this is the main frame, the parent should be null.</b>
	 * <p>
	 * For very common cases, new frames will be created while the running of
	 * the main application. These frames may used for configuration settings,
	 * sub tools and etc. We may expect the location of the new popup frame is
	 * properly just over the main frame of the application. Therefore, this
	 * method can be used conveniently. If the parent is null, then the location
	 * is just in the centre.
	 * 
	 * 
	 * @param container
	 *            the parent frame (or the main frame), can be null
	 * @param popup
	 *            the new created frame to be show
	 * @return the proper localtion point
	 */
	public static Point getProperLocation(Container parent, Container popup) {
		if (parent == null)
			return getCenterLocation(popup);

		Point point = parent.getLocation();
		point.translate((parent.getWidth() - popup.getWidth()) / 2, (parent
		        .getHeight() - popup.getHeight()) / 2);
		return point;
	}

	/**
	 * Get the point for which the popup container can be shown properly at the
	 * center of the screen
	 * 
	 * @param popup
	 *            the container to be shown
	 * @return the point
	 * @throws NullPointerException
	 *             if the container is null
	 */
	public static Point getCenterLocation(Container popup) {
		Dimension dimention = TOOLKIT.getScreenSize();
		int x = ((dimention.width - popup.getWidth()) / 2);
		if (x < 0)
			x = 0;
		int y = ((dimention.height - popup.getHeight()) / 2);
		if (y < 0)
			y = 0;

		return new Point(x, y);
	}

	/**
	 * Add a file chooser listener for the jComponent
	 * 
	 * @param parent
	 * @param button
	 * @param file_selection_model
	 * @param extensions
	 * @param description
	 */
	public static JFileChooser addFileChooserListener(final Component parent,
	        AbstractButton button, int file_selection_model,
	        String[] extensions, String description) {

		final MyJFileChooser chooser = new MyJFileChooser();
		chooser.setFileSelectionMode(file_selection_model);

		if (extensions != null && extensions.length != 0) {
			final HashSet<String> extSet = new HashSet<String>();
			for (String ext : extensions)
				extSet.add(ext);
			final String des;
			if (description == null) {
				des = extSet.toString();
			} else
				des = description;

			chooser.setFileFilter(new FileFilter() {

				@Override
				public boolean accept(File f) {
					if (f.isDirectory())
						return true;
					String name = f.getName();
					int idx = name.lastIndexOf('.');
					if (idx == -1)
						return false;

					String ex = name.substring(idx + 1).toLowerCase();
					return extSet.contains(ex);
				}

				@Override
				public String getDescription() {
					return des;
				}
			});
		}

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chooser.showOpenDialog(parent);
			}
		});

		return chooser;
	}

	/**
	 * Right click the mouse on the specific component, and show up a popup
	 * menu.
	 * 
	 * @param component
	 * @return
	 */
	public static JPopupMenu setPopupMenu(final JComponent component) {
		final JPopupMenu jPopupMenu = new JPopupMenu();
		component.add(jPopupMenu);

		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int mods = e.getModifiers();
				if (mods == InputEvent.BUTTON3_MASK) {
					jPopupMenu.show(component, e.getX(), e.getY());
				}
			}
		});

		return jPopupMenu;
	}

	/**
	 * Returns the specified component's toplevel <code>Frame</code> or
	 * <code>Dialog</code>.
	 * 
	 * @param parentComponent
	 *            the <code>Component</code> to check for a <code>Frame</code>
	 *            or <code>Dialog</code>
	 * @return the <code>Frame</code> or <code>Dialog</code> that contains the
	 *         component, or the default frame if the component is
	 *         <code>null</code>, or does not have a valid <code>Frame</code> or
	 *         <code>Dialog</code> parent
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see GraphicsEnvironment#isHeadless
	 */
	public static Window getWindowForComponent(Component parentComponent)
	        throws HeadlessException {
		if (parentComponent == null)
			return JOptionPane.getRootFrame();

		if (parentComponent instanceof Frame
		        || parentComponent instanceof Dialog)
			return (Window) parentComponent;

		//Iterate until a top level frame or window
		return getWindowForComponent(parentComponent.getParent());
	}
	
	/**
	 * Returns the specified component's toplevel <code>Frame</code>.
	 * 
	 * @param parentComponent
	 *            the <code>Component</code> to check for a <code>Frame</code>
	 *            
	 * @return the <code>Frame</code> that contains the
	 *         component, or the default frame if the component is
	 *         <code>null</code>, or does not have a valid <code>Frame</code> parent
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns
	 *                <code>true</code>
	 * @see GraphicsEnvironment#isHeadless
	 */
	public static Frame getFrameForComponent(Component parentComponent)
	        throws HeadlessException {
		if (parentComponent == null)
			return JOptionPane.getRootFrame();

		if (parentComponent instanceof Frame)
			return (Frame) parentComponent;

		//Iterate until a top level frame or window
		return getFrameForComponent(parentComponent.getParent());
	}
}
