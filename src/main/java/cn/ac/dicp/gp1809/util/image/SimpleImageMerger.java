/* 
 ******************************************************************************
 * File: SimpleImageMerger.java * * * Created on 04-16-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.image;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Merge two or more graph vertically. One by one
 * 
 * @author Xinning
 * @version 0.1, 04-16-2009, 21:03:52
 */
public class SimpleImageMerger {

	private static Font defaultFont = new Font("Times", Font.PLAIN, 9);
	
	//The gap to print the title
	private static int gap = 15;
	
	/**
	 * Merge two or more graph vertically. One by one
	 * 
	 * @param images
	 * @return
	 */
	public static BufferedImage merge(BufferedImage[] images) {
		
		int maxWidth = 0;
		int thight = 0;
		for(BufferedImage image : images) {
			int width = image.getWidth();
			if(width > maxWidth)
				maxWidth = width;
			thight += image.getHeight();
		}
		
		BufferedImage merged = new BufferedImage(maxWidth, thight, BufferedImage.TYPE_INT_RGB);
		
		int curtPosition = 0;
		Graphics graphics = merged.getGraphics();
		for(BufferedImage image : images) {
			graphics.drawImage(image, 0, curtPosition, null);
			curtPosition += image.getHeight();
		}
		
		return merged;
	}
	
	
	/**
	 * Merge two or more graph vertically. One by one. Not completed
	 * 
	 * @param images
	 * @return
	 */
	public static BufferedImage merge(BufferedImage[] images, String[] titles) {
		
		int maxWidth = 0;
		int thight = 0;
		for(BufferedImage image : images) {
			int width = image.getWidth();
			if(width > maxWidth)
				maxWidth = width;
			thight += image.getHeight()+gap;
		}
		
		BufferedImage merged = new BufferedImage(maxWidth, thight, BufferedImage.TYPE_INT_RGB);
		merged.getGraphics().setFont(defaultFont);
		
		int curtPosition = 0;
		Graphics graphics = merged.getGraphics();
		for(BufferedImage image : images) {
			
			graphics.drawImage(image, 0, curtPosition, null);
			curtPosition += image.getHeight();
		}
		
		return merged;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
