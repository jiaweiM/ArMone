/* 
 ******************************************************************************
 * File: NeutralLoss.java * * * Created on 04-15-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

/**
 * The neutral loss mass and the caption
 * 
 * @author Xinning
 * @version 0.1, 04-15-2009, 13:51:03
 */
public class NeutralLossInfo {
	
	/**
	 * Solid neutral loss information: the MH, with loss of 0
	 */
	public static final NeutralLossInfo MH = new NeutralLossInfo(0, "MH");
	
	/**
	 * Solid neutral loss information: the [MH-H2O], with loss of 18.01054
	 */
	public static final NeutralLossInfo MH_H2O = new NeutralLossInfo(18.01054, "[MH-H2O]");
	
	/**
	 * Solid neutral loss information: the [MH-NH3], with loss of 17.03
	 */
	public static final NeutralLossInfo MH_NH3 = new NeutralLossInfo(17.03, "[MH-NH3]");
	
	/**
	 * Solid neutral loss information: the [MH-2H2O], with loss of 35.04
	 */
	public static final NeutralLossInfo MH_H2O_NH3 = new NeutralLossInfo(35.04, "[MH-2H2O]");
	
	/**
	 * Solid neutral loss information: the [MH-2H2O], with loss of 36.02108
	 */
	public static final NeutralLossInfo MH_2H2O = new NeutralLossInfo(36.02108, "[MH-2H2O]");
	
	/**
	 * Solid neutral loss information: the [MH-H3PO4], with loss of 97.97689
	 */
	public static final NeutralLossInfo MH_H3PO4 = new NeutralLossInfo(97.97689, "[MH-H3PO4]");
	
	/**
	 * Solid neutral loss information: the [MH-2H3PO4], with loss of 195.95378
	 */
	public static final NeutralLossInfo MH_2H3PO4 = new NeutralLossInfo(195.95378, "[MH-2H3PO4]");
	
	/**
	 * Solid neutral loss information: the [MH-3H3PO4], with loss of 293.93067
	 */
	public static final NeutralLossInfo MH_3H3PO4 = new NeutralLossInfo(293.93067, "[MH-3H3PO4]");
	
	/**
	 * Solid neutral loss information: the [MH-H3PO4-H2O], with loss of 115.98743
	 */
	public static final NeutralLossInfo MH_H3PO4_H2O = new NeutralLossInfo(115.98743, "[MH-H3PO4-H2O]");
	
	/**
	 * Solid neutral loss information: the [MH-2H3PO4-H2O], with loss of 213.96432
	 */
	public static final NeutralLossInfo MH_2H3PO4_H2O = new NeutralLossInfo(213.96432, "[MH-2H3PO4-H2O]");
	
	/**
	 * Solid neutral loss information: the [MH-2H3PO4-2H2O], with loss of 231.97486
	 */
	public static final NeutralLossInfo MH_2H3PO4_2H2O = new NeutralLossInfo(231.97486, "[MH-2H3PO4-2H2O]");
	
	
	private double loss;
	private String caption;
	/**
     * @param loss
     * @param caption
     */
    public NeutralLossInfo(double loss, String caption) {
	    this.loss = loss;
	    this.caption = caption;
    }
	/**
     * @return the loss
     */
    public double getLoss() {
    	return loss;
    }
	/**
     * @param loss the loss to set
     */
    public void setLoss(double loss) {
    	this.loss = loss;
    }
	/**
     * @return the caption
     */
    public String getCaption() {
    	return caption;
    }
	/**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
    	this.caption = caption;
    }
}
