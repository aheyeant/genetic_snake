package GA;

import Libs.Coordinate;

import java.util.Random;

class SnakeMap {
    /**
     * 0 - WALL
     * 1 - SNAKE
     * 2 - FREE
     * 3 - FOOD
     */
    SnakeMap(int x, int y) {
        x_size = x;
        y_size = y;
        map = new byte[y_size + 2][x_size + 2];
        for (int i = 0; i < y_size + 2; i++) {
            map[i][0] = 0;
            map[i][x_size + 1] = 0;
        }
        for (int i = 1; i < x_size + 1; i++) {
            map[0][i] = 0;
            map[y_size + 1][i] = 0;
        }
        random = new Random();
        healthInFood = x_size * y_size / 5 * 2;
    }

    void clear() {
        for (int i = 1; i < y_size + 1; i++) {
            for (int j = 1; j < x_size + 1; j++) {
                map[i][j] = 2;
            }
        }
    }

    Coordinate setSnake() {
        Coordinate snake = new Coordinate(x_size / 2 + 1, y_size / 2 + 1);
        map[snake.y][snake.x] = 1;
        setFood();
        return snake;
    }

    void setFood() {
        while (true) {
            int xx = random.nextInt(x_size) + 1;
            int yy = random.nextInt(y_size) + 1;
            if (map[yy][xx] == 2) {
                map[yy][xx] = 3;
                return;
            }
        }
    }


    byte[][] map;
    int healthInFood;
    private final int x_size;
    private final int y_size;
    private Random random;

}
