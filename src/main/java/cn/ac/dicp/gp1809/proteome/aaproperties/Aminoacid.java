/*
 ****************************************************************************** File: Aminoacid.java * * * Created on 01-01-2008 Copyright (c) 2009 Xinning
 * Jiang (vext@163.com) All right reserved. Use is subject to license terms.
 */
package cn.ac.dicp.gp1809.proteome.aaproperties;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * An aminoacid instance.
 * 
 * @author Xinning
 * @version 0.2, 03-24-2008, 19:26:33
 */
public class Aminoacid implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat DF1;
	private static final DecimalFormat DF2;

	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);

		DF1 = new DecimalFormat("0.##");
		DF2 = new DecimalFormat("0.####");

		Locale.setDefault(def);
	}

	private double averageMass;
	private String description = "";
	private double monoMass;
	private double hydropathicity;
	private char oneLetter;
	private String threeLetter = "   ";
	private boolean visible;
	private double[] pka = new double[5];

	/**
	 * Construct an blank aminoacid.
	 */
	public Aminoacid() {

	}

	/**
	 * Construct an aminoacid using specific information.
	 * 
	 * @param oneLetter
	 * @param threeLetter
	 * @param monoMass
	 * @param averageMass
	 * @param hydropathicity
	 * @param description
	 * @param visible
	 */
	public Aminoacid(char oneLetter, String threeLetter, double monoMass, double averageMass, double hydropathicity,
			double[] pka, String description, boolean visible) {

		this.set(oneLetter, threeLetter, monoMass, averageMass, hydropathicity, pka, description, visible);
	}

	private void init() {
		oneLetter = ' ';
		threeLetter = "   ";
		setMonoMass(0.0D);
		averageMass = 0.0D;
		description = "";
		visible = false;
		hydropathicity = 0.0D;
	}

	/**
	 * Clear all the informations in the aminoacid to get a "blank" aminoacid.
	 */
	public void clear() {
		init();
	}

	/**
	 * Set the detail information for the aminoacid
	 * 
	 * @param oneLetter
	 * @param threeLetter
	 * @param monoMass
	 * @param averageMass
	 * @param hydropathicity
	 * @param description
	 * @param visible
	 */
	public void set(char oneLetter, String threeLetter, double monoMass, double averageMass, double hydropathicity,
			double[] pka, String description, boolean visible) {

		this.oneLetter = oneLetter;
		setThreeLetter(threeLetter);
		setMonoMass(monoMass);
		this.setAverageMass(averageMass);
		this.setDescription(description);
		this.setHydropathicity(hydropathicity);
		this.setPka(pka);
		this.visible = visible;
	}

	/**
	 * Hydropathicity for GRAVY calcualtion.
	 */
	public double getHydropathicity() {
		return hydropathicity;
	}

	/**
	 * pka value for this aminoacid in different positions.
	 * <p>
	 * Ct Nt Sm Sc Sn
	 * 
	 * @return pka
	 */
	public double[] getPka() {
		return this.pka;
	}

	public char getOneLetter() {
		return oneLetter;
	}

	public boolean isVisible() {
		return visible;
	}

	/**
	 * The description. The default information is the full name and the
	 * molecular formular, e.g. "Glutamic acid C5H7NO3"
	 */
	public String getDescription() {
		return description;
	}

	public String getThreeLetter() {
		return threeLetter;
	}

	/**
	 * The momo isotope mass of the aminoacid residues in the peptide, not the
	 * mass of aminoacid itself. Add H2O to get the mass of aminoacid.
	 * 
	 * @return
	 */
	public double getMonoMass() {
		return monoMass;
	}

	/**
	 * The average mass of the aminoacid residues in the peptide, not the mass
	 * of aminoacid itself. Add H2O to get the mass of aminoacid.
	 * 
	 * @return
	 */
	public double getAverageMass() {
		return averageMass;
	}

	/**
	 * pka value for this aminoacid in different positions.
	 * <p>
	 * Ct Nt Sm Sc Sn
	 * 
	 * @param pka
	 */
	public void setPka(double[] pka) {
		this.pka = pka;
	}

	public void setHydropathicity(double hydropathicity) {
		this.hydropathicity = hydropathicity;
	}

	public void setAverageMass(double averageMass) {
		this.averageMass = averageMass;
	}

	public void setOneLetter(char oneLetter) {
		this.oneLetter = oneLetter;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setThreeLetter(String threeLetter) {
		this.threeLetter = threeLetter;
	}

	public void setMonoMass(double monoMass) {
		this.monoMass = monoMass;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Aminoacid) {
			if (this.hashCode() == arg0.hashCode())
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return oneLetter;
	}

	/*
	 * Deep clone (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Aminoacid clone() {
		Aminoacid copy = null;
		try {
			copy = (Aminoacid) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		copy.setPka(Arrays.copyOf(this.pka, this.pka.length));
		return copy;
	}

	@Override
	public String toString() {
		StringBuilder res = (new StringBuilder()).append(oneLetter).append(' ').append(threeLetter).append(' ')
				.append(DF2.format(averageMass)).append(' ').append(DF2.format(monoMass)).append(' ')
				.append(DF1.format(hydropathicity)).append(' ').append(description).append(' ');

		if (visible)
			res.append("Visible");
		else
			res.append("Unvisible");
		return res.toString();
	}
}
