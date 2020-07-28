package GUI;

import Libs.Connector;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;


public class Controller{
    public Slider sld_population_count;
    public Label label_sld_population_count;

    public Slider sld_iterations_count;
    public Label label_sld_iterations_count;

    public Label label_sld_mutation_rate;
    public Slider sld_mutation_rate;

    public Slider sld_fps;
    public Label label_sld_fps;

    public Label label_map_size;
    public Button btn_resize_map;
    public Slider sld_x_size;
    public Slider sld_y_size;

    public Button btn_start;
    public Button btn_stop;
    public Button btn_pause;

    public ChoiceBox selected_function;
    public ChoiceBox selected_mutation;
    public ChoiceBox selected_crossover;

    public Label label_sld_saved_percents;
    public Slider sld_saved_percents;

    public Button btn_visualization;

    public Button btn_best_phenotype_visualization;
    public Button btn_best_phenotype_start;
    public Button btn_best_phenotype_next;
    public Button btn_best_phenotype_pause;

    public Canvas canvas;


    public void initialize() {
        Connector.graphicsContext = canvas.getGraphicsContext2D();

        Connector.canvas = canvas;

        sld_population_count.valueProperty().addListener((observableValue, o, n) -> {
            label_sld_population_count.setText("Population: " + n.intValue());
            Connector.population_count = n.intValue();
        });

        sld_iterations_count.valueProperty().addListener((observableValue, o, n) -> {
            label_sld_iterations_count.setText("Iterations: " + n.intValue());
            Connector.iterations_count = n.intValue();
        });

        sld_mutation_rate.valueProperty().addListener((observableValue, o, n) -> {
            label_sld_mutation_rate.setText("Mutation rate: " + n.intValue() + "%");
            Connector.mutation_rate = n.intValue() / 100.0;
        });

        sld_x_size.valueProperty().addListener((observableValue, o, n) -> {
            Connector.map_size_x = n.intValue();
            label_map_size.setText("Map size: " + Connector.map_size_x + " x " + Connector.map_size_y);
        });

        sld_y_size.valueProperty().addListener((observableValue, o, n) -> {
            Connector.map_size_y = n.intValue();
            label_map_size.setText("Map size: " + Connector.map_size_x + " x " + Connector.map_size_y);
        });

        sld_fps.valueProperty().addListener((observable, o, n) -> {
            Connector.fps = 1000 / n.intValue();
            label_sld_fps.setText("FPS: " + n.intValue());
        });

        sld_saved_percents.valueProperty().addListener((observable, o, n) -> {
            Connector.savedSnakes = n.intValue() / 100.0;
            label_sld_saved_percents.setText("Saved: " + n.intValue() + "%");
        });
    }

    public void changeSelectionFunction(ActionEvent event) {
        if(selected_function.getValue().equals("Roulette")) {
            Connector.selectionFunction = 1;
            return;
        }
        if(selected_function.getValue().equals("Tournament")) {
            Connector.selectionFunction = 2;
            return;
        }
        if(selected_function.getValue().equals("Bests")) {
            Connector.selectionFunction = 3;
            return;
        }
    }

    public void changeMutationFunction(ActionEvent event) {
        if(selected_mutation.getValue().equals("Coin")) {
            Connector.mutationFunction = 1;
            return;
        }
        if(selected_mutation.getValue().equals("Chance")) {
            Connector.mutationFunction = 2;
            return;
        }
    }

    public void changeCrossoverFunction(ActionEvent event) {
        if(selected_crossover.getValue().equals("N points")) {
            Connector.mutationFunction = 1;
            return;
        }
        if(selected_crossover.getValue().equals("Uniform")) {
            Connector.mutationFunction = 2;
            return;
        }
    }

    public void click_resize(ActionEvent event) {
        Connector.click_resize();
    }

    public void click_start(ActionEvent event) {
        Connector.click_start();
    }

    public void click_stop(ActionEvent event) {
        Connector.click_stop();
        btn_pause.setText("Pause");
    }

    public void click_pause(ActionEvent event) {
        Connector.click_pause();
        if (Connector.paused) {
            btn_pause.setText("Resume");
        } else {
            btn_pause.setText("Pause");
        }
    }

    public void click_visualization(ActionEvent event) {
        if (Connector.click_visualization()) {
            btn_visualization.setText("Visualization ON");
            btn_visualization.setStyle("");
        } else {
            btn_visualization.setText("Visualization OFF");
            btn_visualization.setStyle("-fx-background-color: rgba(255, 0, 0, 0.67)");
        }
    }

    public void click_phenotype_visualization(ActionEvent event) {
        if (Connector.click_phenotype_visualization()) {
            btn_best_phenotype_visualization.setText("Phenotype visualization mode ON");
            if (Connector.started) btn_pause.setText("Resume");
            Connector.saved_size_x = sld_x_size.getValue();
            Connector.saved_size_y = sld_y_size.getValue();
            btn_best_phenotype_start.setVisible(true);
            btn_best_phenotype_next.setVisible(true);
            btn_best_phenotype_pause.setVisible(true);
        } else {
            btn_best_phenotype_visualization.setText("Phenotype visualization mode OFF");
            sld_x_size.setValue(Connector.saved_size_x);
            sld_y_size.setValue(Connector.saved_size_y);
            btn_best_phenotype_start.setVisible(false);
            btn_best_phenotype_next.setVisible(false);
            btn_best_phenotype_pause.setVisible(false);
        }

    }

    public void click_phenotype_visualization_start(ActionEvent event) {
        Connector.click_phenotype_visualization_start();
    }

    public void click_phenotype_visualization_next(ActionEvent event) {
        Connector.click_phenotype_visualization_next();
    }

    public void click_phenotype_visualization_pause(ActionEvent event) {
        Connector.click_phenotype_visualization_pause();
    }
}
