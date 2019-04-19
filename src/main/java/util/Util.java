package util;

import java.util.Random;

public class Util {

    public static int getIndexOfLargest(double[] array) {
        int largestIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > array[largestIndex])
                largestIndex = i;
        }
        return largestIndex;
    }

    private static Random r = new Random();
    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return r.nextInt((max - min) + 1) + min;
    }
}
