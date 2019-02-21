package cn.ac.dicp.gp1809.ga;

public abstract class Selector {
	
	private Configuration a_config;
	
	public Selector(Configuration a_config){
		this.a_config = a_config;
	}
	
	public Configuration getConfiguration(){
		return this.a_config;
	}
	
	/**
	 * Select chromosomes from from_population and form a new population(to_population);
	 * The number of chromosomes in new population is same as the origin population;
	 * 
	 * @param from_population
	 * @param to_population
	 */
	public void select(Population from_population,Population to_population){
		select(from_population.size(),from_population,to_population);
	}
	
	/**
	 * Select chromosomes from from_population and form a new population (to_population);
	 * The number of chromosomes in new population is specified;
	 * 
	 * @param a_howManyToSelect
	 * @param a_from_population
	 * @param a_to_population
	 */
	public abstract void select(int a_howManyToSelect, Population a_from_population,
			Population a_to_population);
	
}
