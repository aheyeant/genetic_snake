package GA;

import Libs.Connector;

public class GeneticAlgorithm implements Runnable{
    public GeneticAlgorithm() {
        population = null;
    }

    public void init() {
        population = new Population(Connector.map_size_x,
                                    Connector.map_size_y,
                                    Connector.population_count,
                                    Connector.iterations_count);
    }



    @Override
    public void run() {
        Connector.started = true;
        while (Connector.started) {
            if (population.iteration >= population.iterationsCount) {
                Connector.started = false;
                Connector.stopped = true;
                continue;
            }

            System.out.printf("\n%d\n", population.iteration);
            System.out.printf("Avg  fitness=%7.1f\n", population.avgFitness);
            System.out.printf("Last fitness=%7.1f steps=%4d food=%4d\n", Connector.lastFitness, Connector.lastSteps, Connector.lastFoods);
            System.out.printf("Best fitness=%7.1f steps=%4d food=%4d\n", Connector.bestFitness, Connector.bestSteps, Connector.bestFoods);

            population.iteration();
            population.selection();

        }
    }

    public Population population;
}
