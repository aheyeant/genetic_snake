package Libs;

import GA.GeneticAlgorithm;
import GA.Visualizator;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;

public class Connector {
    public static void click_start() {
        if (started) return;
        if (phenotypeVisualization) return;
        reInit();
        geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.init();
        Thread thread = new Thread(geneticAlgorithm);
        thread.start();
    }

    public static void click_pause() {
        if (phenotypeVisualization) return;
        if (started && !stopped) paused = !paused;
    }

    public static void click_stop() {
        if (phenotypeVisualization) return;
        paused = false;
        stopped = true;
        started = false;
    }

    public static void click_resize() {
        if (geneticAlgorithm == null) return;
        geneticAlgorithm.population.needResize = true;
        geneticAlgorithm.population.new_x_size = map_size_x;
        geneticAlgorithm.population.new_y_size = map_size_y;
    }

    public static boolean click_visualization() {
        visualization = !visualization;
        return visualization;
    }

    public static boolean click_phenotype_visualization() {
        if (bestPhenotype == null) return false;
        if (phenotypeVisualization) {
            phenotypeVisualizationStarted = false;
            phenotypeVisualizationPaused = false;
            phenotypeVisualization = false;
            visualizator = null;
        } else {
            if (!paused) click_pause();
            phenotypeVisualization = true;
            visualizator = new Visualizator(bestPhenotype);
            phenotypeVisualizationStarted = false;
            phenotypeVisualizationPaused = false;
        }
        return phenotypeVisualization;
    }

    public static void click_phenotype_visualization_start() {
        if (!phenotypeVisualization) return;
        if (visualizator == null) return;
        if (phenotypeVisualizationStarted) return;
        Thread thread = new Thread(visualizator);
        phenotypeVisualizationStarted = true;
        thread.start();
    }

    public static void click_phenotype_visualization_next() {
        if (!phenotypeVisualization) return;
        if (visualizator != null) visualizator.next = true;
    }

    public static void click_phenotype_visualization_pause() {
        if (!phenotypeVisualization) return;
        phenotypeVisualizationPaused = !phenotypeVisualizationPaused;
    }

    private static void reInit() {
        triggered = 0;
        started = false;
        stopped = false;
        paused = false;
        bestFitness = 0;
        iteration = 0;
        bestFoods = 0;
        bestSteps = 0;
    }


    public static GeneticAlgorithm geneticAlgorithm = null;

    public static int triggered = 0;

    public static int population_count = 100;
    public static int iterations_count = 500;
    public static double mutation_rate = 0.2;
    public static int fps = 33;
    public static int map_size_x = 10;
    public static int map_size_y = 10;

    public static boolean started = false;
    public static boolean stopped = false;
    public static boolean paused = false;
    public static boolean visualization = true;
    public static boolean phenotypeVisualization = false;
    public static boolean phenotypeVisualizationStarted = false;
    public static boolean phenotypeVisualizationPaused = false;
    public static Visualizator visualizator = null;
    public static double saved_size_x = 0;
    public static double saved_size_y = 0;

    public static int selectionFunction = 1;
    public static int mutationFunction = 1;
    public static int crossoverFunction = 1;
    public static double savedSnakes = 0.5;

    public static GraphicsContext graphicsContext = null;
    public static Canvas canvas = null;

    public static double[] bestPhenotype = null;
    public static double bestFitness = 0;
    public static int bestSteps = 0;
    public static int bestFoods = 0;
    public static double lastFitness = 0;
    public static int lastSteps = 0;
    public static int lastFoods = 0;
    public static int iteration = 0;
}
