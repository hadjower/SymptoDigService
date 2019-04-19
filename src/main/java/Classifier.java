import similarity.Similarity;

import java.util.stream.IntStream;

public class Classifier {
    private Similarity similarity;

    public Classifier(Similarity similarity) {
        this.similarity = similarity;
    }

    public double[] calculateSimilarities(int[] input, int[][] data, int[] features) {
        double[] similarities = new double[data.length];
        IntStream.range(0, data.length)
                .forEach(i -> similarities[i] = similarity.calculate(input, data[i], features));
        return similarities;
    }
}
