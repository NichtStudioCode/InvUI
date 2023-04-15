package xyz.xenondevs.invui.util;

import java.util.Random;

public class MathUtils {
    
    public static final Random RANDOM = new Random();
    
    /**
     * Generates a pseudorandom number between min and max.
     *
     * @param min The lower bound (inclusive)
     * @param max The upper bound (exclusive)
     * @return A pseudorandom number between min and max.
     */
    public static int randInt(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }
    
}
