/*
 ******************************************************************************
 * File: GlycoStrucDrawer.java * * * Created on 2012-4-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.drawjf;

import cn.ac.dicp.gp1809.glyco.Glycosyl;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTreeNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ck
 * @version 2012-4-26, 16:08:34
 */
public class GlycoStrucDrawer
{
    private BufferedImage image;
    private Graphics2D graphics;

    private int width = 600;
    private int height = 480;

    private int disX = 50;
    private int disY = 40;

    private int rootX = 550;
    private int rootY = 240;

    private Font f = new Font("Arial", Font.BOLD, 12);

    private String glyco_png = "/resources/Monosaccharide_png";

    private BufferedImage[] glycoImages;

    public GlycoStrucDrawer() throws IOException
    {

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.graphics = this.image.createGraphics();
        this.graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));

        File file = new File(System.getProperty("user.dir") + glyco_png);
        File[] fs = file.listFiles();
        this.glycoImages = new BufferedImage[fs.length];
        for (int i = 0; i < fs.length; i++) {
            glycoImages[i] = ImageIO.read(fs[i]);
        }
    }

    private void draw(GlycoTree tree, String out) throws IOException
    {

        this.draw(tree);

        if (image == null) {

            ImageIO.write(this.image, "PNG", new File(out));

        } else {

            ImageIO.write(this.image, "PNG", new File(out));
        }
    }

    public BufferedImage draw(GlycoTree tree)
    {
        tree.getCoordinate(rootX, rootY, disX, disY);
        graphics.setBackground(Color.white);
        graphics.clearRect(0, 0, width, height);

        graphics.setColor(Color.black);

        HashMap<String, int[]> coordinateMap = tree.getCoordinateMap();
        HashMap<String, GlycoTreeNode> nodemap = tree.getNodeMap();

        if (coordinateMap.size() != nodemap.size()) {
            return image;
        }

        Iterator<String> it1 = nodemap.keySet().iterator();
        while (it1.hasNext()) {

            String key = it1.next();
            GlycoTreeNode node = nodemap.get(key);
            int[] cooParent = coordinateMap.get(key);
            ArrayList<GlycoTreeNode> list = node.getChildNodeList();
            for (int i = 0; i < list.size(); i++) {

                GlycoTreeNode child = list.get(i);
                int[] cooChild = coordinateMap.get(child.getId());
                if (child.getGlycosyl() == Glycosyl.Fuc) {
                    graphics.drawLine(cooParent[0] + 12, cooParent[1] + 12, cooChild[0] + 14, cooChild[1] + 12);
                } else {
                    graphics.drawLine(cooParent[0] + 12, cooParent[1] + 12, cooChild[0] + 12, cooChild[1] + 12);
                }
            }
        }

        Iterator<String> it2 = coordinateMap.keySet().iterator();
        while (it2.hasNext()) {

            String key = it2.next();
            int[] coordinate = coordinateMap.get(key);
            GlycoTreeNode node = nodemap.get(key);
            Glycosyl glyco = node.getGlycosyl();

            int type = glyco.getGraphicsId();
            BufferedImage bi;
            if (glyco == Glycosyl.Fuc) {

                if (node.getOrient() == 4) {

                    bi = this.glycoImages[type];

                } else {

                    bi = this.glycoImages[type - 1];
                }

            } else {
                bi = this.glycoImages[type - 1];
            }

            graphics.drawImage(bi, coordinate[0], coordinate[1], bi.getWidth(), bi.getHeight(), null);
        }
		
/*		String glycoCT = tree.getGlycoCT();
		AttributedString as = new AttributedString(glycoCT);
		as.addAttribute(TextAttribute.FONT, f, 0, glycoCT.length());
		AttributedCharacterIterator it = as.getIterator();

		
		String [] ss = glycoCT.split("\n");
		for(int i=0;i<ss.length;i++){
			g2.drawString(ss[i], 20, 20+14*i);
		}
		g2.dispose();
*/
        return image;
    }
}
