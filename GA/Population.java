package GA;

import Libs.Connector;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Population {
    public Population(int x, int y, int populationSize, int iterationsCount) {
        x_size = x;
        y_size = y;
        new_x_size = x;
        new_y_size = y;
        this.populationSize = populationSize;
        this.iterationsCount = iterationsCount;
        snakes = new Snake[this.populationSize];
        maps = new SnakeMap[this.populationSize];
        for (int i = 0; i < populationSize; i++) {
            snakes[i] = new Snake();
            maps[i] = new SnakeMap(x_size, y_size);
        }
        iteration = 0;
        needResize = false;
        avgFitness = 0;
        random = new Random();
        if (populationSize < 50) {
            tournamentMaxGroup = populationSize / 2 + 1;
            tournamentMinGroup = 5;
        } else {
            tournamentMinGroup = 20;
            tournamentMaxGroup = 50;
        }
    }

    void iteration() {
        iteration++;
        if (needResize) {
            resize();
        }
        for (int i = 0; i < populationSize; i++) {
            snakes[i].init(maps[i]);
        }
        int lost = populationSize;
        while (lost != 0) {
            while (!Connector.stopped && Connector.started && Connector.paused) {}
            if (Connector.stopped) return;
            lost = 0;
            long current = System.currentTimeMillis();
            if (Connector.triggered == -1 || !snakes[Connector.triggered].isAlive()) {
                Connector.triggered = findBestFromAlive();
            }
            for (int i = 0; i < populationSize; i++) {
                if (snakes[i].isAlive()) {
                    lost++;
                    snakes[i].nextStep();
                    if (Connector.triggered == i) {
                        if (Connector.visualization) {
                            drawAction();
                        }
                    }
                }
            }

            if (Connector.visualization) {
                while (System.currentTimeMillis() - current < Connector.fps) {}
            }
        }
        for (int i = 0; i < populationSize; i++) {
            avgFitness += snakes[i].fitness;
        }
        avgFitness /= populationSize;
        int iter = findBestFromAll();
        Connector.lastFitness = snakes[iter].fitness;
        Connector.lastSteps = snakes[iter].steps;
        Connector.lastFoods = snakes[iter].foods;
        if (Connector.bestFitness < snakes[iter].fitness) {
            Connector.bestFitness = snakes[iter].fitness;
            Connector.bestFoods = snakes[iter].foods;
            Connector.bestSteps = snakes[iter].steps;
            Connector.bestPhenotype = snakes[iter].phenotype.clone();
        }
    }

    void selection() {
        switch (Connector.selectionFunction) {
            case 1:
                rouletteSelection();
            case 2:
                tournamentSelection();
            case 3:
                bestSelection();
        }
    }

    private void mutation(Snake snake) {
        if (Connector.mutationFunction == 1) {
            mutationCoin(snake);
            return;
        }
        if (Connector.mutationFunction == 2) {
            mutationChance(snake);
            return;
        }
    }

    private void crossover(Snake s1, Snake s2) {
        if (Connector.crossoverFunction == 1) {
            nCrossover(s1, s2);
            return;
        }
        if (Connector.crossoverFunction == 2) {
            coinCrossover(s1, s2);
            return;
        }
    }

    /**
     * Copy 50% best snakes and create 50% from bests
     */
    private void bestSelection() {
        int copies = (int) (populationSize * Connector.savedSnakes);
        copies = copies == 0 ? 1 : copies;
        Snake[] tmp = new Snake[populationSize];
        for (int i = 0; i < copies; i++) {
            int iter = findBestFromAll();
            snakes[iter].fitness = -2;
            tmp[i] = snakes[iter];
        }
        int position = copies;
        while (true) {
            int i1 = random.nextInt(copies);
            int i2 = random.nextInt(copies);
            if (i2 == i1) {i2++; i2 %= copies;}
            Snake s1 = new Snake(tmp[i1].phenotype.clone());
            Snake s2 = new Snake(tmp[i2].phenotype.clone());
            crossover(s1, s2);
            if (position == populationSize) {
                break;
            } else {
                mutation(s1);
                tmp[position++] = s1;
            }
            if (position == populationSize) {
                break;
            } else {
                mutation(s2);
                tmp[position++] = s2;
            }
        }
        snakes = tmp;
    }

    private void rouletteSelection() {
        int copies = (int) (populationSize * Connector.savedSnakes);
        copies = copies == 0 ? 1 : copies;
        Snake[] tmp = new Snake[populationSize];
        for (int i = 0; i < copies; i++) {
            int iter = findBestFromAll();
            snakes[iter].fitness = -snakes[iter].fitness;
            tmp[i] = snakes[iter];
        }
        for (int i = 0; i < copies; i++) {
            if (tmp[i].fitness < 0) {
                tmp[i].fitness = -tmp[i].fitness;
            }
        }

        int populationCount = copies;
        while (true) {
            List<Snake> l = twoSnakesRouletteSelection();
            Snake s1 = new Snake(l.get(0).phenotype.clone());
            Snake s2 = new Snake(l.get(1).phenotype.clone());
            crossover(s1, s2);
            if (populationCount != populationSize) {
                mutation(s1);
                tmp[populationCount++] = s1;
            } else {
                break;
            }
            if (populationCount != populationSize) {
                mutation(s2);
                tmp[populationCount++] = s2;
            } else {
                break;
            }
        }
        snakes = tmp;
    }

    private void tournamentSelection() {
        int copies = (int) (populationSize * Connector.savedSnakes);
        copies = copies == 0 ? 1 : copies;
        Snake[] tmp = new Snake[populationSize];
        for (int i = 0; i < copies; i++) {
            int iter = findBestFromAll();
            snakes[iter].fitness = -snakes[iter].fitness;
            tmp[i] = snakes[iter];
        }
        for (int i = 0; i < copies; i++) {
            if (tmp[i].fitness < 0) {
                tmp[i].fitness = -tmp[i].fitness;
            }
        }

        int populationCount = copies;

        while (true) {
            List<Snake> l = twoSnakesTournamentSelection();
            Snake s1 = new Snake(l.get(0).phenotype.clone());
            Snake s2 = new Snake(l.get(1).phenotype.clone());
            crossover(s1, s2);
            if (populationCount != populationSize) {
                mutation(s1);
                tmp[populationCount++] = s1;
            } else {
                break;
            }
            if (populationCount != populationSize) {
                mutation(s2);
                tmp[populationCount++] = s2;
            } else {
                break;
            }
        }
        snakes = tmp;
    }

    private void mutationCoin(Snake snake) {
        if (random.nextFloat() > Connector.mutation_rate) return;
        for (int i = 0; i < snake.phenotype.length; i++) {
            if (random.nextBoolean()) {
                snake.phenotype[i] = GAHelper.getRandomDouble();
            }
        }
    }

    private void mutationChance(Snake snake) {
        for (int i = 0; i < snake.phenotype.length; i++) {
            if (random.nextFloat() < Connector.mutation_rate) {
                snake.phenotype[i] = GAHelper.getRandomDouble();
            }
        }
    }

    private void nCrossover(Snake s1, Snake s2) {
        int slices = random.nextInt(6) + 2;
        boolean first = random.nextBoolean();
        int part = s1.phenotype.length / slices;
        for (int i = 0; i < slices; i++) {
            if (first) {
                first = false;
                for (int j = i * part; j < i * part + part; j++) {
                    double tmp = s1.phenotype[j];
                    s1.phenotype[j] = s2.phenotype[j];
                    s2.phenotype[j] = tmp;
                }
            } else {
                first = true;
            }
        }
        if (first) {
            for (int i = slices * part; i < s1.phenotype.length; i++) {
                double tmp = s1.phenotype[i];
                s1.phenotype[i] = s2.phenotype[i];
                s2.phenotype[i] = tmp;
            }
        }
    }

    private void coinCrossover(Snake s1, Snake s2) {
        for (int i = 0; i < s1.phenotype.length; i++) {
            if (random.nextDouble() < 0.4) {
                double tmp = s1.phenotype[i];
                s1.phenotype[i] = s2.phenotype[i];
                s2.phenotype[i] = tmp;
            }
        }
    }

    private List<Snake> twoSnakesTournamentSelection() {
        List<Snake> AI = new LinkedList<>();
        List<Double> maxFitness = new LinkedList<>();
        int count = tournamentMinGroup + random.nextInt(tournamentMaxGroup - tournamentMinGroup);
        for (int i = 0; i < count; i++) {
            AI.add(null);
            maxFitness.add((double) -1);
        }
        for (int i = 0; i < populationSize; i++) {
            if (maxFitness.get(i % count) < snakes[i].fitness) {
                maxFitness.set((i % count), snakes[i].fitness);
                AI.set((i % count), snakes[i]);
            }
        }
        List<Snake> ret = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            ret.add(AI.get(i));
        }
        return ret;
    }

    private List<Snake> twoSnakesRouletteSelection() {
        double fullFitness = populationSize * avgFitness;

        List<Snake> v = new LinkedList<>();
        v.add(null);
        v.add(null);
        while (true) {
            Snake snake = snakes[random.nextInt(populationSize)];
            if (random.nextDouble() < snake.fitness / fullFitness) {
                v.set(0, snake);
                break;
            }
        }
        while (true) {
            Snake snake = snakes[random.nextInt(populationSize)];
            if (random.nextDouble() < snake.fitness / fullFitness) {
                v.set(1, snake);
                break;
            }
        }
        return v;
    }

    private int findBestFromAlive() {
        int bestI = -1;
        double bestF = -1;
        for (int i = 0; i < populationSize; i++) {
            if (snakes[i].isAlive()) {
                if (snakes[i].fitness > bestF) {
                    bestF = snakes[i].fitness;
                    bestI = i;
                }
            }
        }
        return bestI;
    }

    private int findBestFromAll() {
        int bestI = -1;
        double bestF = -1;
        for (int i = 0; i < populationSize; i++) {
            if (snakes[i].fitness > bestF) {
                bestF = snakes[i].fitness;
                bestI = i;
            }
        }
        return bestI;
    }

    private void drawAction() {
        if (Connector.triggered == -1) return;

        int x = (100 - Connector.geneticAlgorithm.population.x_size - 2) * 10 / 2;
        int y = (100 - Connector.geneticAlgorithm.population.y_size - 2) * 10 / 2;
        Connector.graphicsContext.clearRect(0, 0, 1000, 1000);
        for (int i = 0; i < Connector.geneticAlgorithm.population.y_size + 2; i++) {
            for (int j = 0; j < Connector.geneticAlgorithm.population.x_size + 2; j++) {
                if (Connector.geneticAlgorithm.population.snakes[Connector.triggered].snakeMap.map[i][j] == 2) {
                } else if (Connector.geneticAlgorithm.population.snakes[Connector.triggered].snakeMap.map[i][j] == 0) {
                    Connector.graphicsContext.setStroke(Color.BLACK);
                    Connector.graphicsContext.strokeRect(x + j * 10 + 1, y + i * 10 + 1, 8, 8);
                } else if (Connector.geneticAlgorithm.population.snakes[Connector.triggered].snakeMap.map[i][j] == 1) {
                    Connector.graphicsContext.setStroke(Color.RED);
                    Connector.graphicsContext.strokeRect(x + j * 10 + 1, y + i * 10 + 1, 8, 8);
                } else {
                    Connector.graphicsContext.setStroke(Color.GREEN);
                    Connector.graphicsContext.strokeRect(x + j * 10 + 1, y + i * 10 + 1, 8, 8);
                }
            }
        }
        Connector.graphicsContext.fillText("Health=" + Connector.geneticAlgorithm.population.snakes[Connector.triggered].health +
                " Food=" + Connector.geneticAlgorithm.population.snakes[Connector.triggered].foods +
                " Step=" + Connector.geneticAlgorithm.population.snakes[Connector.triggered].steps, 100, 995);
    }

    private void resize() {
        if (x_size == new_x_size && y_size == new_y_size) {needResize = false; return;}
        x_size = new_x_size;
        y_size = new_y_size;
        for (int i = 0; i < populationSize; i++) {
            maps[i] = new SnakeMap(x_size, y_size);
        }
        needResize = false;
        Connector.bestFitness = 0;
        Connector.bestFoods = 0;
        Connector.bestSteps = 0;
    }

    double avgFitness;
    private Snake[] snakes;
    private SnakeMap[] maps;
    private int x_size;
    private int y_size;
    public int new_x_size;
    public int new_y_size;
    int iteration;
    int iterationsCount;
    private int populationSize;
    public boolean needResize;
    private Random random;
    private int tournamentMaxGroup;
    private int tournamentMinGroup;
}
