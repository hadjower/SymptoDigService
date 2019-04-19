import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class DataParser {
    public Map<String, Integer> linesToIdMap(List<String> lines) {
        //map entity - id
        return lines.stream().skip(1).map(line -> line.split("\t"))
                .collect(toMap(arr -> arr[1], arr -> Integer.parseInt(arr[0])));
    }

    public int[][] linksToMatrix(Map<String, Integer> diseases, Map<String, Integer> symptoms, List<String> links) {
        int[][] disSymptLinks = new int[diseases.entrySet().size()][symptoms.entrySet().size()];
        links.stream().skip(1)
                .map(line -> line.split("\t"))
                .forEach(arr -> {
                    disSymptLinks[diseases.get(arr[0])][symptoms.get(arr[1])] = 1;
                });
        return disSymptLinks;
    }
}
