package cn.ac.dicp.gp1809.ga;

import java.util.ArrayList;
import java.util.List;

public class RoulettewheelSelector extends Selector
{

    public RoulettewheelSelector(Configuration a_config)
    {
        super(a_config);
    }

    @Override
    public void select(int a_howManyToSelect, Population from_pop, Population to_pop)
    {

        RandomGenerator generator = getConfiguration().getRandomGenerator();

        int numberOfEntries = from_pop.size();
        double[] fitnessValues = new double[numberOfEntries];
        List<Chromosome> chromosomes = new ArrayList<>(numberOfEntries);

        Chromosome[] from_chromosomes = from_pop.toChromosomes();

        int count = 0;
        double cumulate_fitness = 0d;
        double bestFitness = -1.0d;
        Chromosome fitestChrom = null;
        FitnessEvaluator evaluator = getConfiguration().getFitnessEvaluator();

        for (int i = 0; i < numberOfEntries; i++) {
            Chromosome chrom = from_chromosomes[i];
            double fitness = chrom.getFitnessValue();
            if (fitness == 0d) {//a dath individual
                //new generated random chromosome is makeup to instead the deth chromosome
                chromosomes.add(chrom.newChromosome());
                count++;
            } else {
                cumulate_fitness += fitness;

                if (evaluator.isFitter(fitness, bestFitness)) {
                    bestFitness = fitness;
                    fitestChrom = chrom;
                }
            }

            fitnessValues[i] = cumulate_fitness;
        }

        int len = numberOfEntries - 1;
        for (int i = count; i < len; i++) {
            chromosomes.add(this.spinWheel(generator, fitnessValues, from_chromosomes).clone());
        }

        /*
         * 如果不存在精英，则所有的个体适应度都为0，所以所有的个体都已经被随机生成的新个体取代
         * 存在的话，则原来的中群中至少有一个不为一，所以将该个体加入到最后一个；
         */
        if (fitestChrom != null) {
            chromosomes.add(fitestChrom);
        }

        if (numberOfEntries == a_howManyToSelect) {//the size of new population is the same as source
            to_pop.setChromosomes(chromosomes);
            return;
        } else if (numberOfEntries > a_howManyToSelect) {//random select from new generated chromosomes
            //Clear the population first.
            if (to_pop.size() > 0)
                to_pop.clearPopulation();

            for (int i = 0; i < a_howManyToSelect; i++) {
                /*
                 * The first n(n=a_howManyToSelect) random selected chromosome are used to
                 * make up new population;
                 */
                to_pop.addChromosome(chromosomes.get(generator.nextInt(numberOfEntries)));
            }
        } else {
            throw new RuntimeException("This method has not compeletly designed!");
        }
    }

    /**
     * This method "spins" the wheel and returns the Chromosome that is "landed upon." Each time a chromosome is
     * selected, one instance of it is removed from the wheel so that it cannot be selected again.
     *
     * @param a_generator     the random number generator to be used during the spinning process
     * @param a_fitnessValues an array of fitness values of the respective Chromosomes
     * @param a_counterValues an array of total counter values of the respective Chromosomes
     * @param a_chromosomes   the respective Chromosome instances from which selection is to occur
     * @return selected Chromosome from the roulette wheel
     */
    private Chromosome spinWheel(final RandomGenerator a_generator, final double[] a_fitnessValues,
            final Chromosome[] a_chromosomes)
    {
        double totalwheel = a_fitnessValues[a_fitnessValues.length - 1];
        double point = a_generator.nextDouble() * totalwheel;

        int wheel = dichotomySearch(point, a_fitnessValues);

        return a_chromosomes[wheel];
    }

    private int dichotomySearch(double target, double[] array)
    {
        int lowb = 0, upb = array.length - 1;
        int current = 0;
        //the first one
        if (target <= array[current])
            return 0;

        while (lowb <= upb) {
            current = (lowb + upb) / 2;

            if (target <= array[current]) {

                if (target > array[current - 1]) {
                    return current;
                } else {
                    upb = current - 1;
                }
            } else {
                lowb = current + 1;
            }
        }

        return 0;
    }
}
