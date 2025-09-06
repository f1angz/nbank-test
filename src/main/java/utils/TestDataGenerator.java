package utils;

import java.util.Random;

public class TestDataGenerator {
    private static final Random RANDOM = new Random();

    public static double randomBalance() {
        return RANDOM.nextDouble(100.0, 1000.0);
    }
}
