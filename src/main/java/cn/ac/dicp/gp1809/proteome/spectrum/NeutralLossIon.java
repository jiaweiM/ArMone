/* 
 ******************************************************************************
 * File: NeutralLossIon.java * * * Created on 05-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

/**
 * The neutral loss ion
 * 
 * @author Xinning
 * @version 0.1, 05-13-2009, 09:28:08
 */
public class NeutralLossIon extends Ion {

	private double mz;
	private short charge;

	/**
	 * @param mass1
	 * @param type
	 * @param symbol
	 *            the symbo
	 * @param series
	 */
	public NeutralLossIon(double mz, short charge, String symbol) {
		super(charge == 1 ? mz : mz*charge-charge+1, TYPE_NEU, symbol, 0);

		this.charge = charge;
		this.mz = mz;
	}

	/**
	 * The charge state of the neutral loss ion
	 * 
	 * @return
	 */
	public short getCharge() {
		return this.charge;
	}

	/**
	 * The mz value of the neutral loss ion
	 */
	@Override
	public double getMz() {
		return this.mz;
	}

	/**
	 * This method is useless for neutral loss ion, same as {@link #getMz()}
	 */
	@Override
	public double getMzVsCharge(int charge) {
		return this.getMz();
	}

	/**
	 * The name of the neutral loss with current charge state. An example is
	 * "[MH-H3PO4]+"
	 */
	@Override
	public String getName() {
		StringBuilder sb = new StringBuilder(5);
		sb.append(this.getSymbol());
		for (short i = 0; i < this.charge; i++)
			sb.append('+');

		return sb.toString();
	}

	/**
	 * This method is useless for neutral loss ion, same as {@link #getName()}
	 */
	@Override
	public String getName(int charge) {
		return this.getName();
	}
}
