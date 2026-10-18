import java.io.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Inventory {
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final String fileName;

    public Inventory(String fileName) {
        this.fileName = fileName;
    }

    public void load() {
        File file = new File(fileName);
        if (!file.exists()) {
            addSampleProducts();
            save();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                try {
                    Product p = Product.fromFileString(line);
                    products.put(p.getId().toUpperCase(), p);
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipped bad line " + lineNo + " in " + fileName);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading " + fileName + ": " + e.getMessage());
        }
    }

    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Product p : products.values()) writer.println(p.toFileString());
        } catch (IOException e) {
            System.out.println("Error saving " + fileName + ": " + e.getMessage());
        }
    }

    private void addSampleProducts() {
        addProduct(new Product("P001", "Bottled Water 500ml", 20.00, 50));
        addProduct(new Product("P002", "Instant Noodles", 15.50, 40));
        addProduct(new Product("P003", "Canned Tuna", 38.75, 25));
        addProduct(new Product("P004", "Bread Loaf", 65.00, 15));
        addProduct(new Product("P005", "Coffee 3-in-1", 9.00, 100));
    }

    public boolean addProduct(Product p) {
        String key = p.getId().toUpperCase();
        if (products.containsKey(key)) return false;
        products.put(key, p);
        return true;
    }

    public Product findById(String id) {
        return products.get(id.toUpperCase());
    }

    public boolean removeProduct(String id) {
        return products.remove(id.toUpperCase()) != null;
    }

    public Collection<Product> getAll() {
        return products.values();
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }
}
