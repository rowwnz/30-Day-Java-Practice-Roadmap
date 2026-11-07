package gym.model;

/** Anything that can be saved as one line of a CSV file. */
public interface Storable {
    String toCsv();
}
