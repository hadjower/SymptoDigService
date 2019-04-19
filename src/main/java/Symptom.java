public class Symptom {
    private int id;
    private String name;
    private double tf;
    private double idf;
    private double tf_idf;

    public Symptom(String name, double tf, double idf, double tf_idf) {
        this.name = name;
        this.tf = tf;
        this.idf = idf;
        this.tf_idf = tf_idf;
    }

    @Override
    public String toString() {
        return name + "\t" +
                tf + "\t" +
                idf + "\t" +
                tf_idf;
    }

    public static Symptom parse(String[] linkArray) {
        //linkArray[0] - disease
        return new Symptom(
                linkArray[1],
                Double.parseDouble(linkArray[2]),
                Double.parseDouble(linkArray[3]),
                Double.parseDouble(linkArray[4])
        );
    }

}
