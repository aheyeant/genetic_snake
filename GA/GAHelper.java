package GA;

import java.util.Random;

public class GAHelper {
    private static Random random = new Random();

    public static double getRandomDouble() {
        return random.nextInt() % 100;
    }

}
