package cn.ac.dicp.gp1809.drawjf;

import java.awt.Color;

/**
 * The annotation label
 * 
 * @author Xinning
 * @version 0.1, 05-31-2009, 10:21:29
 */
public class AnnotationLabel {

	private String label;
	private Color color;

	public AnnotationLabel(String label, Color color) {
		this.label = label;
		this.color = color;
	}

	/**
	 * The label string.
	 * 
	 * @return
	 */
	public String getLabelString() {
		return this.label;
	}

	/**
	 * @return The color for this label
	 */
	public Color getColor() {
		return this.color;
	}

	@Override
	public int hashCode() {
		return this.label.hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof AnnotationLabel) {
			return this.label.equals(((AnnotationLabel) obj).label);
		}

		return false;
	}
}
