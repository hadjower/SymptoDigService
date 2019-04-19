import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Model model = Model.Builder.build();
        double accuracity = model.testSimpleJaccard();
        System.out.println("Accuracity for Jaccard[all input correct] is " + accuracity);
    }

    private static Path getResPath(String name) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(name).toURI());
    }

    private static void assignIds() {
        try (Stream<String> stream = Files.lines(
                Paths.get(ClassLoader.getSystemResource("disease-symptom-TF_IDF-links [filtered].txt").toURI()))) {
            List<String> disSymptLinks = stream.collect(toList());

            Set<String> diseases = disSymptLinks.stream().map(link -> link.split("\t")[0]).collect(toSet());
            Iterator<String> diseasesIterator = diseases.iterator();
            final StringBuilder diseasesOutput = new StringBuilder();
            diseasesOutput.append("Index\tDisease\n");
            IntStream.range(0, diseases.size())
                    .forEach(i -> diseasesOutput
                            .append(i)
                            .append("\t")
                            .append(diseasesIterator.next())
                            .append("\n"));

            System.out.println(diseasesOutput);

            saveFile(diseasesOutput.toString(), "diseases.txt");

            System.out.println("--------------------------");

            Set<String> symptoms = disSymptLinks.stream().map(link -> link.split("\t")[1]).collect(toSet());
            final Iterator<String> symptomsIterator = symptoms.iterator();
            final StringBuilder symptomsOutput = new StringBuilder();
            symptomsOutput.append("Index\tSymptom\n");
            IntStream.range(0, symptoms.size())
                    .forEach(i -> symptomsOutput
                            .append(i)
                            .append("\t")
                            .append(symptomsIterator.next())
                            .append("\n"));

            System.out.println(symptomsOutput);

            saveFile(symptomsOutput.toString(), "symptoms.txt");

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveFile(String string, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void test() {
        Set<String> set1 = new HashSet<>();
        set1.add("Key1");
        set1.add("Key2");

        Set<String> set2 = new HashSet<>();
        set2.add("Key1");
        set2.add("Key2");
        set2.add("Key3");

        Set<String> set3 = new HashSet<>();
        set3.add("Key1");
        set3.add("Key2");

        Map<String, Set<String>> map = new HashMap<>();
        map.put("Disease 1", set1);
        map.put("Disease 2", set2);
        map.put("Disease 3", set3);

        map.entrySet().forEach(System.out::println);

//        Map<String, Set<String>> mapNew = map.entrySet().stream()
//                .collect(groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
//                .entrySet().stream()
//                .filter(entry -> entry.getValue().size() == 1)
//                .collect(toMap(entry -> entry.getValue().get(0), Map.Entry::getKey));

        System.out.println("---------------------------");
//        mapNew.entrySet().forEach(System.out::println);

        String output = map.entrySet().stream()
                .map(e -> e.getValue().stream()
                        .map(sym -> e.getKey() + "\t" + sym)
                        .collect(joining("\n")))
                .collect(joining("\n"));

        output = "Disease" + "\t" + "Symptom\n" + output;
        System.out.println(output);

    }

    private static void perform() {
        try (Stream<String> streamSymDis = Files.lines(Paths.get(ClassLoader.getSystemResource("symptom-disease.txt").toURI()));
             Stream<String> streamSymDisFiltered = Files.lines(Paths.get(ClassLoader.getSystemResource("symptom-disease-filtered.txt").toURI()))) {

            List<String> dataFiltered = streamSymDisFiltered.collect(Collectors.toList());

            System.out.printf("Unfiltered: %d links; Filtered: %d links.\n",
                    streamSymDis.count(),
                    dataFiltered.size());

            Map<String, Set<String>> dataMap = dataFiltered.stream()
                    .map(line -> line.split("\t"))
                    .collect(
                            groupingBy(arr -> arr[1], Collectors.mapping(arr -> arr[0], Collectors.toSet()))
                    );

            System.out.printf("Diseases: %d.\n", dataMap.size());
            Map<String, Set<String>> oldDataMap = dataMap;

            //Diseases with symptoms amount > 3
            dataMap = dataMap.entrySet().stream()
                    .filter(entry -> entry.getValue().size() > 3)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

//            saveDataMap(dataMap);

            System.out.printf("Without diseases with symptoms amount <= 3: %d links.\n",
                    dataMap.values().stream().mapToInt(Set::size).sum()
            );

            System.out.printf("Diseases: %d.\n", dataMap.size());

//            Map<String, Set<String>> dataMapMoreFiltered =
//            dataMap.entrySet().stream()
//                    .collect(groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
//                    .entrySet().stream()
//                    .filter(entry -> entry.getValue().size() > 1)
//                    .forEach(System.out::println);
//                    .entrySet().stream()
//                    .filter(entry -> entry.getValue().size() == 1)
//                    .collect(toMap(entry -> entry.getValue().get(0), Map.Entry::getKey));

//            System.out.printf("Without diseases with duplicate symptoms: %d links.\n",
//                    dataMapMoreFiltered.values().stream().mapToInt(Set::size).sum()
//            );
//            System.out.printf("Diseases without duplicates: %d.\n", dataMapMoreFiltered.size());


            System.out.println();
            dataMap.entrySet().forEach(System.out::println);
            System.out.println();

            //Calculate TF-IDF for disease-symptom links

            Map<String, Long> symtomDiseasesCountMap = dataFiltered.stream()
                    .map(line -> line.split("\t"))
                    .collect(groupingBy(linkArr -> linkArr[0], counting()));
            Set<String[]> dataFilteredSplitted = dataFiltered.stream()
                    .map(line -> line.split("\t"))
                    .collect(toSet());

            String disSymTFIDFLinks = dataFilteredSplitted.parallelStream()
                    .map(linkArr -> {
                        String[] newLinkArr = new String[5];
                        newLinkArr[0] = linkArr[1]; //disease
                        newLinkArr[1] = linkArr[0]; //symptom

                        //tf
                        double tf = Double.parseDouble(linkArr[2]) / dataFiltered.stream()
                                .map(line -> line.split("\t"))
                                .filter(subLinkArr -> subLinkArr[1].equals(newLinkArr[0]))
                                .mapToInt(subLinkArr -> Integer.parseInt(subLinkArr[2]))
                                .sum();
                        newLinkArr[2] = String.valueOf(tf);
                        //idf
                        double idf = Math.log10(oldDataMap.size() / symtomDiseasesCountMap.get(newLinkArr[1]));
                        newLinkArr[3] = String.valueOf(idf);
                        //tf-idf
                        double tf_idf = tf * idf;
                        newLinkArr[4] = String.valueOf(tf_idf);
                        return newLinkArr;
                    })
                    .map(newLinkArr -> String.join("\t", newLinkArr))
                    .collect(joining("\n"));
            disSymTFIDFLinks = "Disease\tSymptom\tTF\tIDF\tTF-IDF\n" + disSymTFIDFLinks;
            saveFile(disSymTFIDFLinks, "disease-symptom-TF_IDF-links.txt");

            System.out.println(disSymTFIDFLinks);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveDataMap(Map<String, Set<String>> dataMap) {
        String output = dataMap.entrySet().stream()
                .map(e -> e.getValue().stream()
                        .map(sym -> e.getKey() + "\t" + sym)
                        .collect(joining("\n")))
                .collect(joining("\n"));

        output = "Disease\tSymptom\n" + output;

        try (FileOutputStream fos = new FileOutputStream("disease-symptom-links.txt")) {
            fos.write(output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Script {
        public void main() {
            //Filter links with tfidf >= 3.5
            try (Stream<String> streamSymDis = Files.lines(Paths.get(ClassLoader.getSystemResource("symptom-disease.txt").toURI()))) {

                String filteredData = streamSymDis.skip(1)
                        .filter(line -> Float.compare(Float.parseFloat(line.split("\t")[3]), 3.5f) >= 0)
                        .collect(Collectors.joining("\n"));

                try (FileOutputStream fos = new FileOutputStream("symptom-disease-filtered.txt")) {
                    fos.write(filteredData.getBytes());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
