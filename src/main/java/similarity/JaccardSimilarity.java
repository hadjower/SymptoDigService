package similarity;

import java.util.Arrays;
import java.util.stream.IntStream;

public class JaccardSimilarity implements Similarity {
    /***
     * Jaccard similarity = p/p+q+r
     * p - number of features that positive for both objects
     * q - number of features that positive for object A and negative for object B
     * r - number of features that positive for object B and negative for object A
     *
     * @param inputA - features vector of object A
     * @param inputB - features vector of object B
     * @param features - indexes of features we need to calculate
     * @return similarity between inputA and inputB based on features
     */
    @Override
    public double calculate(int[] inputA, int[] inputB, int[] features) {
        double p = 0, q = 0, r = 0;
        for (int fIndex : features) {
            p += (inputA[fIndex] + inputB[fIndex] == 2) ? 1 : 0;
            q += (inputA[fIndex] > inputB[fIndex]) ? 1 : 0;
            r += (inputA[fIndex] < inputB[fIndex]) ? 1 : 0;
        }
        return p / (p + q + r);
    }
}
