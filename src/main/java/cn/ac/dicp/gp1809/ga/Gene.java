package cn.ac.dicp.gp1809.ga;

import java.util.Random;

public abstract class Gene implements Cloneable {

	private static Random random = new Random();

	private RandomGenerator rand;

	private final Configuration param;

	protected String genestring;
	protected char[] genechar;

	private short length;
	private float mutate_rate;

	/**
	 * Use the paramter to form a new gene;
	 * 
	 * @param param
	 */
	public Gene(final Configuration param) {
		this.param = param;
		this.mutate_rate = this.param.getMutateRate();

		this.setLength();

		this.rand = param.getRandomGenerator();
		this.genestring = this.encode();
		this.genechar = this.genestring.toCharArray();
	}

	/**
	 * Use the paramter and specified code string to form a new gene;
	 * 
	 * @param param
	 * @param geneString
	 */
	public Gene(final Configuration param, String geneString) {
		this.param = param;
		this.mutate_rate = this.param.getMutateRate();
		this.genestring = geneString;
		this.genechar = this.genestring.toCharArray();
		this.length = (short) this.genestring.length();
	}

	public Configuration getConfiguration() {
		return this.param;
	}

	/**
	 * reset the genestring
	 * 
	 * @param geneString
	 * @return
	 */
	public String setGeneCode(String geneString) {
		this.genestring = geneString;
		this.genechar = this.genestring.toCharArray();
		this.length = (short) this.genestring.length();

		return geneString;
	}

	protected RandomGenerator getRandomGenerator() {
		return this.rand;
	}

	/**
	 * Random encode the gene to binary string with 0 and 1;
	 * 
	 * @return binary string of the gene;
	 */
	protected abstract String encode();

	/**
	 * Value of the gene;
	 * 
	 * @return value of the gene;
	 */
	public abstract double value();

	/**
	 * Random generate another gene using this gene as seed;
	 * 
	 * @return a new gene with same configuration;
	 */
	public Gene newGene() {
		Gene newone = null;
		try {
			newone = (Gene) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		newone.genestring = newone.encode();
		newone.genechar = newone.genestring.toCharArray();

		return newone;
	}

	/**
	 * Mutate using default percentage of the paramter
	 * 
	 * @return if mutation occured;
	 */
	public boolean mutate() {
		// mark
		boolean changed = false;

		for (int i = 0; i < this.length; i++) {
			if (random.nextFloat() <= this.mutate_rate) {
				char c = this.genechar[i];
				if (c == '0')
					this.genechar[i] = '1';
				else
					this.genechar[i] = '0';

				if (!changed) {
					changed = true;
				}
			}
		}

		if (changed)
			this.genestring = new String(this.genechar);

		return changed;
	}

	/**
	 * Set the bit length for this gene; normally this value is indicated by
	 * configuration eg a random gene has value of 011011, this gene bit length
	 * equals 6;
	 */
	protected abstract void setLength();

	/**
	 * Set bit length of this gene as the value specified. Warning: length of a
	 * gene can be automatically generated when the gene is formed, changed this
	 * value may cause some unpredicted errors. Normally, these errors may be
	 * avoid by using method of decode() afterward, but this is not sure;
	 * 
	 * @param length
	 */
	protected void setLength(short length) {
		this.length = length;
	}

	public short length() {
		return this.length;
	}

	/**
	 * Deep clone
	 */
	@Override
	public Gene clone() {
		Gene cloned = null;
		try {
			cloned = (Gene) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		cloned.genechar = new char[this.length];
		System.arraycopy(this.genechar, 0, cloned.genechar, 0, this.length);

		return cloned;
	}
}
