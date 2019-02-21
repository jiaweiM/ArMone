package cn.ac.dicp.gp1809.ga;

public abstract class FitnessFunction {

	  public final static double NO_FITNESS_VALUE = -1d;

	  /**
	   * The fitness value computed during the previous run
	   */
	  private double m_lastComputedFitnessValue = NO_FITNESS_VALUE;

	  /**
	   * Retrieves the fitness value of the given Chromosome. The fitness
	   * value will be a positive double.
	   *
	   * @param a_chrome the Chromosome for which to compute and return the fitness value
	   * @return the fitness value of the given Chromosome
	   *
	   */
	  public final double getFitnessValue(final Chromosome a_chrome) {
	    double fitnessValue = evaluate(a_chrome);
	    if (fitnessValue < 0d) {
	      throw new RuntimeException(
	          "Fitness values must be positive! Received value: "+ fitnessValue);
	    }
	    m_lastComputedFitnessValue = fitnessValue;
	    return fitnessValue;
	  }

	  /**
	   * @return the last fitness value computed via method getFitnessValue(
	   * Chromosome), or NO_FITNES_VALUE if the former method has not been called
	   * yet
	   */
	  public double getLastComputedFitnessValue() {
	    return m_lastComputedFitnessValue;
	  }

	  /**
	   * Determine the fitness of the given Chromosome instance. The higher the
	   * return value, the more fit the instance. This method should always
	   * return the same fitness value for two equivalent Chromosome instances.
	   *
	   * @param a_chrome the Chromosome instance to evaluate
	   * @return positive double reflecting the fitness rating of the given Chromosome.
	   */
	  protected abstract double evaluate(Chromosome a_chrome);
}
