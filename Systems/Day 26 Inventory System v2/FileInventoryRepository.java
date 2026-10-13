import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class FileInventoryRepository implements InventoryRepository {
    public static final String DELIMITER = "|";
    private static final String HEADER = "# TYPE|ID|NAME|CATEGORY|QUANTITY|UNIT_PRICE|EXTRA";

    private final Path filePath;

    public FileInventoryRepository(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    public Path getFilePath() {
        return filePath.toAbsolutePath();
    }

    @Override
    public Inventory load() throws IOException {
        Inventory inventory = new Inventory();
        if (!Files.exists(filePath)) {
            return inventory;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    inventory.addItem(parseLine(line));
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    System.out.println("Warning: skipped line " + lineNumber
                            + " in save file (" + e.getMessage() + ")");
                }
            }
        }
        return inventory;
    }

    @Override
    public void save(Inventory inventory) throws IOException {
        Path parent = filePath.toAbsolutePath().getParent();
        Path tempFile = Files.createTempFile(parent, "inventory", ".tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();
            for (Item item : inventory.getAllItems()) {
                writer.write(item.toFileLine());
                writer.newLine();
            }
        }
        Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private Item parseLine(String line) {
        String[] parts = line.split(java.util.regex.Pattern.quote(DELIMITER), -1);
        if (parts.length != 7) {
            throw new IllegalArgumentException("expected 7 fields, found " + parts.length);
        }

        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        String category = parts[3];
        int quantity;
        double price;
        try {
            quantity = Integer.parseInt(parts[4]);
            price = Double.parseDouble(parts[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid number");
        }

        switch (type) {
            case "STANDARD":
                return new StandardItem(id, name, category, quantity, price);
            case "PERISHABLE":
                return new PerishableItem(id, name, category, quantity, price, LocalDate.parse(parts[6]));
            default:
                throw new IllegalArgumentException("unknown item type '" + type + "'");
        }
    }
}
