package Libs;

import java.util.Random;

public class Coordinate {
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinate getRandomDir() {
        Random random = new Random();
        switch (random.nextInt(4)) {
            case 0:
                return new Coordinate(0, 1);
            case 1:
                return new Coordinate(0, -1);
            case 2:
                return new Coordinate(1, 0);
            case 3:
                return new Coordinate(-1, 0);
        }
        return new Coordinate(0, 1);
    }

    public int x;
    public int y;
}
