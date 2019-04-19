package util;

public class Util {
    public static int getIndexOfLargest(double[] array) {
        int largestIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > array[largestIndex])
                largestIndex = i;
        }
        return largestIndex;
    }
}
