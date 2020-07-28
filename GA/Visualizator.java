package GA;

import Libs.Connector;
import javafx.scene.paint.Color;

public class Visualizator implements Runnable {
    public Visualizator(double[] phenotype) {
        snake = new Snake(phenotype);
        map = null;
        next = false;
    }

    @Override
    public void run() {
        while (Connector.phenotypeVisualizationStarted) {
            init();
            while (snake.isAlive()) {
                while (Connector.phenotypeVisualizationPaused && Connector.phenotypeVisualizationStarted && !next) {}
                if (!Connector.phenotypeVisualizationStarted) return;
                if (next) {
                    next = false;
                    break;
                }
                long current = System.currentTimeMillis();
                snake.nextStep();
                drawAction();
                while (System.currentTimeMillis() - current < Connector.fps);
            }
        }
    }

    private void init() {
        size_x = Connector.map_size_x;
        size_y = Connector.map_size_y;
        map = new SnakeMap(size_x, size_y);
        snake.init(map);
    }

    private void drawAction() {
        int x = (100 - this.size_x - 2) * 10 / 2;
        int y = (100 - this.size_x - 2) * 10 / 2;
        Connector.graphicsContext.clearRect(0, 0, 1000, 1000);
        for (int i = 0; i < size_y + 2; i++) {
            for (int j = 0; j < size_x + 2; j++) {
                if (snake.snakeMap.map[i][j] == 2) {
                } else if (snake.snakeMap.map[i][j] == 0) {
                    Connector.graphicsContext.setStroke(Color.BLACK);
                    Connector.graphicsContext.strokeRect(x + j * 10 + 1, y + i * 10 + 1, 8, 8);
                } else if (snake.snakeMap.map[i][j] == 1) {
                    Connector.graphicsContext.setStroke(Color.RED);
                    Connector.graphicsContext.strokeRect(x + j * 10 + 1, y + i * 10 + 1, 8, 8);
                } else {
                    Connector.graphicsContext.setStroke(Color.GREEN);
                    Connector.graphicsContext.strokeRect(x + j * 10 + 1, y + i * 10 + 1, 8, 8);
                }
            }
        }
        Connector.graphicsContext.fillText("Health=" + snake.health +
                " Food=" + snake.foods +
                " Step=" + snake.steps, 100, 995);
    }

    public boolean next;
    private int size_x;
    private int size_y;
    private Snake snake;
    private SnakeMap map;
}
