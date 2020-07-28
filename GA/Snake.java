package GA;

import Libs.Coordinate;

import java.util.LinkedList;
import java.util.List;

class Snake {
    Snake() {
        aroundInfo = new int[24];
        this.phenotype = generatePhenotype();
        snakeMap = null;
        positions = new LinkedList<>();
        head = null;
        direction = null;
        health = 0;
        steps = 0;
        foods = 0;
    }

    Snake(double[] phenotype) {
        aroundInfo = new int[24];
        this.phenotype = phenotype;
        snakeMap = null;
        positions = new LinkedList<>();
        head = null;
        direction = null;
        health = 0;
        steps = 0;
        foods = 0;
    }

    void init(SnakeMap snakeMap) {
        this.snakeMap = snakeMap;
        this.snakeMap.clear();
        positions.clear();
        head = this.snakeMap.setSnake();
        positions.add(head);
        health = this.snakeMap.healthInFood;
        direction = Coordinate.getRandomDir();
        steps = 0;
        foods = 0;
        fitness = 1;
    }

    boolean isAlive() {
        return health > 0;
    }

    void nextStep() {
        if (!isAlive()) {
            throw new IllegalCallerException("Snake is dead");
        }
        getAroundInfo();
        execute(getAction());
        head = new Coordinate(head.x + direction.x, head.y + direction.y);
        if (snakeMap.map[head.y][head.x] == 2) {
            health--;
            //fitness += 1;
            snakeMap.map[head.y][head.x] = 1;
            positions.add(0, head);
            Coordinate rm = positions.remove(positions.size() - 1);
            snakeMap.map[rm.y][rm.x] = 2;
            steps++;
            return;
        }

        if (snakeMap.map[head.y][head.x] == 3) {
            health = snakeMap.healthInFood + 10 * foods;
            //todo
            foods++;
            fitness += 100;
            snakeMap.map[head.y][head.x] = 1;
            positions.add(0, head);
            snakeMap.setFood();
            return;
        }
        if (snakeMap.map[head.y][head.x] == 0) {
            health = 0;
        }
        if (snakeMap.map[head.y][head.x] == 1) {
            health = 0;
            fitness -= 0.1 * fitness;
        }
    }

    private void execute(byte command) {
        if (command == 0) return;
        if (command == 1) {
            turnLeft();
            return;
        }
        if (command == 2) {
            turnRight();
            return;
        }

        throw new IllegalArgumentException("command=\"" + command + "\"");
    }

    /**
     * 0 - None
     * 1 - turn left
     * 2 - turn right
     */
    private byte getAction() {
        double[] chance = new double[]{0, 0, 0};
        for (int i = 0; i < 24; i++) {
            if (aroundInfo[i] != -1) {
                chance[0] += phenotype[i * 3] / aroundInfo[i];
                chance[1] += phenotype[i * 3 + 1] / aroundInfo[i];
                chance[2] += phenotype[i * 3 + 2] / aroundInfo[i];
            }
        }
        if (chance[0] >= chance[1]) {
            if (chance[0] >= chance[2]) return 0;
            return 2;
        } else {
            if (chance[1] >= chance[2]) return 1;
            return 2;
        }
    }

    private void turnLeft() {
        if (direction.x == 0) {
            if (direction.y == 1) {
                direction.x = 1;
            } else {
                direction.x = -1;
            }
            direction.y = 0;
            return;
        }
        if (direction.y == 0) {
            if (direction.x == 1) {
                direction.y = -1;
            } else {
                direction.y = 1;
            }
            direction.x = 0;
        }
    }

    private void turnRight() {
        if (direction.x == 0) {
            if (direction.y == 1) {
                direction.x = -1;
            } else {
                direction.x = 1;
            }
            direction.y = 0;
            return;
        }
        if (direction.y == 0) {
            if (direction.x == 1) {
                direction.y = 1;
            } else {
                direction.y = -1;
            }
            direction.x = 0;
        }
    }

    /**
     *          0
     *      7       1
     *   6      s ->    2
     *      5       3
     *          4
     */
    private void getAroundInfo() {
        if (direction.x == 0) {
            if (direction.y == 1) {
                getAroundInfoBottom();
            } else {
                getAroundInfoTop();
            }
            return;
        }
        if (direction.y == 0) {
            if (direction.x == 1) {
                getAroundInfoRight();
            } else {
                getAroundInfoLeft();
            }
        }
    }

    private void getAroundInfoLeft() {
        getLineInfoBottom(0);
        getLineInfoBottomLeft(1);
        getLineInfoLeft(2);
        getLineInfoTopLeft(3);
        getLineInfoTop(4);
        getLineInfoTopRight(5);
        getLineInfoRight(6);
        getLineInfoBottomRight(7);
    }

    private void getAroundInfoRight() {
        getLineInfoTop(0);
        getLineInfoTopRight(1);
        getLineInfoRight(2);
        getLineInfoBottomRight(3);
        getLineInfoBottom(4);
        getLineInfoBottomLeft(5);
        getLineInfoLeft(6);
        getLineInfoTopLeft(7);

    }

    private void getAroundInfoTop() {
        getLineInfoLeft(0);
        getLineInfoTopLeft(1);
        getLineInfoTop(2);
        getLineInfoTopRight(3);
        getLineInfoRight(4);
        getLineInfoBottomRight(5);
        getLineInfoBottom(6);
        getLineInfoBottomLeft(7);

    }

    private void getAroundInfoBottom() {
        getLineInfoRight(0);
        getLineInfoBottomRight(1);
        getLineInfoBottom(2);
        getLineInfoBottomLeft(3);
        getLineInfoLeft(4);
        getLineInfoTopLeft(5);
        getLineInfoTop(6);
        getLineInfoTopRight(7);

    }


    /**
     * 0 - WALL
     * 1 - SNAKE
     * 2 - FREE
     * 3 - FOOD
     */
    private void getLineInfoTop(int position) {
        aroundInfo[position * 3] = -1;
        aroundInfo[position * 3 + 1] = -1;
        aroundInfo[position * 3 + 2] = -1;
        Coordinate c = new Coordinate(head.x, head.y);
        for (int i = 1; i < 1000; i++) {
            c.y -= 1;
            if (snakeMap.map[c.y][c.x] == 2) {
                continue;
            }
            if (snakeMap.map[c.y][c.x] == 0) {
                aroundInfo[position * 3] = i;
                break;
            } else if (snakeMap.map[c.y][c.x] == 1) {
                if (aroundInfo[position * 3 + 1] == -1) {
                    aroundInfo[position * 3 + 1] = i;
                }
            } else {
                aroundInfo[position * 3 + 2] = i;
            }
        }
    }

    private void getLineInfoTopRight(int position) {
        aroundInfo[position * 3] = -1;
        aroundInfo[position * 3 + 1] = -1;
        aroundInfo[position * 3 + 2] = -1;
        Coordinate c = new Coordinate(head.x, head.y);
        for (int i = 1; i < 1000; i++) {
            c.x += 1;
            c.y -= 1;
            if (snakeMap.map[c.y][c.x] == 2) {
                continue;
            }
            if (snakeMap.map[c.y][c.x] == 0) {
                aroundInfo[position * 3] = i;
                break;
            } else if (snakeMap.map[c.y][c.x] == 1) {
                if (aroundInfo[position * 3 + 1] == -1) {
                    aroundInfo[position * 3 + 1] = i;
                }
            } else {
                aroundInfo[position * 3 + 2] = i;
            }
        }
    }

    private void getLineInfoRight(int position) {
        aroundInfo[position * 3] = -1;
        aroundInfo[position * 3 + 1] = -1;
        aroundInfo[position * 3 + 2] = -1;
        Coordinate c = new Coordinate(head.x, head.y);
        for (int i = 1; i < 1000; i++) {
            c.x += 1;
            if (snakeMap.map[c.y][c.x] == 2) {
                continue;
            }
            if (snakeMap.map[c.y][c.x] == 0) {
                aroundInfo[position * 3] = i;
                break;
            } else if (snakeMap.map[c.y][c.x] == 1) {
                if (aroundInfo[position * 3 + 1] == -1) {
                    aroundInfo[position * 3 + 1] = i;
                }
            } else {
                aroundInfo[position * 3 + 2] = i;
            }
        }
    }

    private void getLineInfoBottomRight(int position) {
        aroundInfo[position * 3] = -1;
        aroundInfo[position * 3 + 1] = -1;
        aroundInfo[position * 3 + 2] = -1;
        Coordinate c = new Coordinate(head.x, head.y);
        for (int i = 1; i < 1000; i++) {
            c.x += 1;
            c.y += 1;
            if (snakeMap.map[c.y][c.x] == 2) {
                continue;
            }
            if (snakeMap.map[c.y][c.x] == 0) {
                aroundInfo[position * 3] = i;
                break;
            } else if (snakeMap.map[c.y][c.x] == 1) {
                if (aroundInfo[position * 3 + 1] == -1) {
                    aroundInfo[position * 3 + 1] = i;
                }
            } else {
                aroundInfo[position * 3 + 2] = i;
            }
        }
    }

    private void getLineInfoBottom(int position) {
        aroundInfo[position * 3] = -1;
        aroundInfo[position * 3 + 1] = -1;
        aroundInfo[position * 3 + 2] = -1;
        Coordinate c = new Coordinate(head.x, head.y);
        for (int i = 1; i < 1000; i++) {
            c.y += 1;
            if (snakeMap.map[c.y][c.x] == 2) {
                continue;
            }
            if (snakeMap.map[c.y][c.x] == 0) {
                aroundInfo[position * 3] = i;
                break;
            } else if (snakeMap.map[c.y][c.x] == 1) {
                if (aroundInfo[position * 3 + 1] == -1) {
                    aroundInfo[position * 3 + 1] = i;
                }
            } else {
                aroundInfo[position * 3 + 2] = i;
            }
        }
    }

    private void getLineInfoBottomLeft(int position) {
        aroundInfo[position * 3] = -1;
        aroundInfo[position * 3 + 1] = -1;
        aroundInfo[position * 3 + 2] = -1;
        Coordinate c = new Coordinate(head.x, head.y);
        for (int i = 1; i < 1000; i++) {
            c.x -= 1;
            c.y += 1;
            if (snakeMap.map[c.y][c.x] == 2) {
                continue;
            }
            if (snakeMap.map[c.y][c.x] == 0) {
                aroundInfo[position * 3] = i;
                break;
            } else if (snakeMap.map[c.y][c.x] == 1) {
                if (aroundInfo[position * 3 + 1] == -1) {
                    aroundInfo[position * 3 + 1] = i;
                }
            } else {
                aroundInfo[position * 3 + 2] = i;
            }
        }
    }

    private void getLineInfoLeft(int position) {
        aroundInfo[position * 3] = -1;
        aroundInfo[position * 3 + 1] = -1;
        aroundInfo[position * 3 + 2] = -1;
        Coordinate c = new Coordinate(head.x, head.y);
        for (int i = 1; i < 1000; i++) {
            c.x -= 1;
            if (snakeMap.map[c.y][c.x] == 2) {
                continue;
            }
            if (snakeMap.map[c.y][c.x] == 0) {
                aroundInfo[position * 3] = i;
                break;
            } else if (snakeMap.map[c.y][c.x] == 1) {
                if (aroundInfo[position * 3 + 1] == -1) {
                    aroundInfo[position * 3 + 1] = i;
                }
            } else {
                aroundInfo[position * 3 + 2] = i;
            }
        }
    }

    private void getLineInfoTopLeft(int position) {
        aroundInfo[position * 3] = -1;
        aroundInfo[position * 3 + 1] = -1;
        aroundInfo[position * 3 + 2] = -1;
        Coordinate c = new Coordinate(head.x, head.y);
        for (int i = 1; i < 1000; i++) {
            c.x -= 1;
            c.y -= 1;
            if (snakeMap.map[c.y][c.x] == 2) {
                continue;
            }
            if (snakeMap.map[c.y][c.x] == 0) {
                aroundInfo[position * 3] = i;
                break;
            } else if (snakeMap.map[c.y][c.x] == 1) {
                if (aroundInfo[position * 3 + 1] == -1) {
                    aroundInfo[position * 3 + 1] = i;
                }
            } else {
                aroundInfo[position * 3 + 2] = i;
            }
        }
    }

    private double[] generatePhenotype() {
        double[] p = new double[72];
        for (int i = 0; i < 72; i++) {
            p[i] = GAHelper.getRandomDouble();
        }
        return p;
    }

    /**
     * size 8 * 15
     */
    double[] phenotype;
    SnakeMap snakeMap;
    private List<Coordinate> positions;
    double fitness;
    int steps;
    int foods;
    int health;
    private Coordinate head;
    private Coordinate direction;
    /**
     * 0 - wall info
     * 1 - snake info
     * 2 - food info
     */
    private int[] aroundInfo;
}

