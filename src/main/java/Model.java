import similarity.JaccardSimilarity;
import util.FileReader;
import util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Model {
    private Map<String, Integer> diseasesIds; //map disease - id
    private Map<String, Integer> symptomIds; //map symptom - id
    private int[][] disSymptLinks; //dis - row, symptom - column, 1 - has link, 0 - doesn't

    private Model() {

    }

    public void setDiseasesIds(Map<String, Integer> diseasesIds) {
        this.diseasesIds = diseasesIds;
    }

    public void setSymptomIds(Map<String, Integer> symptomIds) {
        this.symptomIds = symptomIds;
    }

    public void setDisSymptLinks(int[][] disSymptLinks) {
        this.disSymptLinks = disSymptLinks;
    }

    public Map<String, Integer> getDiseasesIds() {
        return diseasesIds;
    }

    public Map<String, Integer> getSymptomIds() {
        return symptomIds;
    }

    //Logic

    //Testing

    // jaccard, correct rows, all features
    public double testSimpleJaccard() {
        Classifier classifier = new Classifier(new JaccardSimilarity());

        int[] features = new int[symptomIds.size()];
        for (int i = 0; i < features.length; i++) {
            features[i] = i;
        }

        //id of test - id of result
        double correct = 0;
        for (int i = 0; i < disSymptLinks.length; i++) {
            int[] row = disSymptLinks[i];
            int resultIndex = Util.getIndexOfLargest(
                    classifier.calculateSimilarities(
                            row,
                            disSymptLinks,
                            features
                    )
            );
            if (i == resultIndex) correct++;
        }
        return correct / diseasesIds.size();
    }

    // jaccard, rows with errors, customized features
    public double testRandomizedJaccard(int errorsAmnt) {
        Classifier classifier = new Classifier(new JaccardSimilarity());
        int[][] customizedFeatures = getCustomizedFeatures(disSymptLinks);
        //todo randomize rows symptom values

        //id of test - id of result
        double correct = 0;
        for (int i = 0; i < disSymptLinks.length; i++) {
            int[] row = disSymptLinks[i];
            int resultIndex = Util.getIndexOfLargest(
                    classifier.calculateSimilarities(
                            row,
                            disSymptLinks,
                            customizedFeatures[i]
                    )
            );
            if (i == resultIndex) correct++;
        }
        return correct / diseasesIds.size();
    }

    private int[][] getCustomizedFeatures(int[][] disSymptLinks) {
        int featureSize = 12;

        int[][] customizedFeatures = new int[disSymptLinks.length][featureSize];

        for (int i = 0; i < disSymptLinks.length; i++) {
            int[] features = new int[featureSize];
            int[] row = disSymptLinks[i];
            int k = 0;

            for (int j = 0; j < row.length && k < 7; j++) {
                if (row[j] == 1) {
                    features[k] = j;
                    k++;
                }
            }

            while (k < featureSize) {
                int j = Util.getRandomNumberInRange(0, featureSize - 1);

                if (Arrays.stream(features).noneMatch(f -> f == j)
                        && row[j] == 1) {
                    continue;
                }

                features[k] = j;
                k++;
            }

            customizedFeatures[i] = features;
        }

        return customizedFeatures;
    }

    public static class Builder {
        private static String diseasesRes = "diseases.txt";
        private static String symptomsRes = "symptoms.txt";
        private static String linksRes = "disease-symptom-TF_IDF-links [filtered].txt";

        public static Model build() throws IOException {
            Model model = new Model();

            DataParser dataParser = new DataParser();
            FileReader fileReader = new FileReader();


            model.setDiseasesIds(dataParser.linesToIdMap(fileReader.readLines(diseasesRes)));
            model.setSymptomIds(dataParser.linesToIdMap(fileReader.readLines(symptomsRes)));
            model.setDisSymptLinks(dataParser.linksToMatrix(
                    model.getDiseasesIds(),
                    model.getSymptomIds(),
                    fileReader.readLines(linksRes)
            ));

            return model;
        }
    }
}
