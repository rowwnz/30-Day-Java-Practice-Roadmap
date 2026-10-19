package sms.storage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saves and loads the DataStore using Java object serialization. */
public final class FileStorage {

    private FileStorage() {
    }

    public static void save(DataStore store, Path path) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(store);
        }
    }

    /** Returns the saved data, or null if there is no file or it cannot be read. */
    public static DataStore load(Path path) {
        if (!Files.exists(path)) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            return (DataStore) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.out.println("[WARN] Could not read " + path + " (" + e.getMessage() + "). Starting with sample data.");
            return null;
        }
    }
}
