import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class SalesManagementSystem {

    static class Product {
        private String id;
        private String name;
        private double price;
        private int stock;

        public Product(String id, String name, double price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public void setName(String name) { this.name = name; }
        public void setPrice(double price) { this.price = price; }
        public void setStock(int stock) { this.stock = stock; }

        public String toFileString() {
            return id + "," + name + "," + price + "," + stock;
        }

        public static Product fromFileString(String line) {
            String[] p = line.split(",");
            return new Product(p[0], p[1], Double.parseDouble(p[2]), Integer.parseInt(p[3]));
        }
    }

    static class Sale {
        private int saleId;
        private String date;
        private String productId;
        private int quantity;
        private double total;

        public Sale(int saleId, String date, String productId, int quantity, double total) {
            this.saleId = saleId;
            this.date = date;
            this.productId = productId;
            this.quantity = quantity;
            this.total = total;
        }

        public int getSaleId() { return saleId; }
        public String getDate() { return date; }
        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public double getTotal() { return total; }

        public String toFileString() {
            return saleId + "," + date + "," + productId + "," + quantity + "," + total;
        }

        public static Sale fromFileString(String line) {
            String[] p = line.split(",");
            return new Sale(Integer.parseInt(p[0]), p[1], p[2],
                    Integer.parseInt(p[3]), Double.parseDouble(p[4]));
        }
    }

    private static final String PRODUCT_FILE = "products.txt";
    private static final String SALES_FILE = "sales.txt";

    private static Map<String, Product> products = new LinkedHashMap<>();
    private static List<Sale> sales = new ArrayList<>();

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadProducts();
        loadSales();

        int choice;
        do {
            System.out.println("\n========== SALES MANAGEMENT SYSTEM ==========");
            System.out.println("[1] Add Product");
            System.out.println("[2] View All Products");
            System.out.println("[3] Update Product");
            System.out.println("[4] Delete Product");
            System.out.println("[5] Record a Sale");
            System.out.println("[6] View Sales History");
            System.out.println("[7] Sales Report");
            System.out.println("[8] Search Product");
            System.out.println("[0] Save and Exit");
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1: addProduct(); break;
                case 2: viewProducts(); break;
                case 3: updateProduct(); break;
                case 4: deleteProduct(); break;
                case 5: recordSale(); break;
                case 6: viewSales(); break;
                case 7: salesReport(); break;
                case 8: searchProduct(); break;
                case 0:
                    saveProducts();
                    saveSales();
                    System.out.println("Data saved. Goodbye!");
                    break;
                default: System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    private static void addProduct() {
        String id = readString("Enter product ID: ").toUpperCase();
        if (products.containsKey(id)) {
            System.out.println("Product ID already exists.");
            return;
        }
        String name = readString("Enter product name: ");
        double price = readDouble("Enter price: ");
        int stock = readInt("Enter stock quantity: ");

        if (price < 0 || stock < 0) {
            System.out.println("Price and stock cannot be negative.");
            return;
        }
        products.put(id, new Product(id, name, price, stock));
        saveProducts();
        System.out.println("Product added successfully.");
    }

    private static void viewProducts() {
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        System.out.println("\n---------------------- PRODUCT LIST ----------------------");
        System.out.printf("%-8s %-22s %12s %8s%n", "ID", "Name", "Price", "Stock");
        System.out.println("----------------------------------------------------------");
        for (Product p : products.values()) {
            System.out.printf("%-8s %-22s %12.2f %8d%n",
                    p.getId(), p.getName(), p.getPrice(), p.getStock());
        }
    }

    private static void updateProduct() {
        String id = readString("Enter product ID to update: ").toUpperCase();
        Product p = products.get(id);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }
        System.out.println("Leave blank to keep the current value.");

        String name = readLine("New name (" + p.getName() + "): ");
        if (!name.isEmpty()) p.setName(name);

        String price = readLine("New price (" + p.getPrice() + "): ");
        if (!price.isEmpty()) {
            try { p.setPrice(Double.parseDouble(price)); }
            catch (NumberFormatException e) { System.out.println("Invalid price. Kept old value."); }
        }

        String stock = readLine("New stock (" + p.getStock() + "): ");
        if (!stock.isEmpty()) {
            try { p.setStock(Integer.parseInt(stock)); }
            catch (NumberFormatException e) { System.out.println("Invalid stock. Kept old value."); }
        }

        saveProducts();
        System.out.println("Product updated successfully.");
    }

    private static void deleteProduct() {
        String id = readString("Enter product ID to delete: ").toUpperCase();
        if (products.remove(id) != null) {
            saveProducts();
            System.out.println("Product deleted.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void searchProduct() {
        String keyword = readString("Enter product name or ID keyword: ").toLowerCase();
        boolean found = false;
        for (Product p : products.values()) {
            if (p.getName().toLowerCase().contains(keyword) || p.getId().toLowerCase().contains(keyword)) {
                if (!found) {
                    System.out.printf("%-8s %-22s %12s %8s%n", "ID", "Name", "Price", "Stock");
                }
                System.out.printf("%-8s %-22s %12.2f %8d%n",
                        p.getId(), p.getName(), p.getPrice(), p.getStock());
                found = true;
            }
        }
        if (!found) System.out.println("No matching products.");
    }

    private static void recordSale() {
        if (products.isEmpty()) {
            System.out.println("No products available. Add products first.");
            return;
        }
        viewProducts();
        String id = readString("Enter product ID: ").toUpperCase();
        Product p = products.get(id);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }
        int qty = readInt("Enter quantity: ");
        if (qty <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }
        if (qty > p.getStock()) {
            System.out.println("Insufficient stock. Available: " + p.getStock());
            return;
        }

        double total = qty * p.getPrice();
        int saleId = sales.isEmpty() ? 1 : sales.get(sales.size() - 1).getSaleId() + 1;
        Sale sale = new Sale(saleId, LocalDate.now().toString(), id, qty, total);

        sales.add(sale);
        p.setStock(p.getStock() - qty);

        saveSales();
        saveProducts();
        System.out.printf("Sale recorded. Sale #%d | %s x%d | Total: %.2f%n",
                saleId, p.getName(), qty, total);
    }

    private static void viewSales() {
        if (sales.isEmpty()) {
            System.out.println("No sales recorded yet.");
            return;
        }
        System.out.println("\n-------------------------- SALES HISTORY --------------------------");
        System.out.printf("%-6s %-12s %-8s %-20s %5s %12s%n", "Sale#", "Date", "ProdID", "Product", "Qty", "Total");
        System.out.println("-------------------------------------------------------------------");
        for (Sale s : sales) {
            Product p = products.get(s.getProductId());
            String name = (p != null) ? p.getName() : "(deleted)";
            System.out.printf("%-6d %-12s %-8s %-20s %5d %12.2f%n",
                    s.getSaleId(), s.getDate(), s.getProductId(), name, s.getQuantity(), s.getTotal());
        }
    }

    private static void salesReport() {
        if (sales.isEmpty()) {
            System.out.println("No sales recorded yet.");
            return;
        }

        double grandTotal = 0;
        int totalItems = 0;
        Map<String, Integer> qtyPerProduct = new HashMap<>();
        Map<String, Double> revenuePerProduct = new HashMap<>();

        for (Sale s : sales) {
            grandTotal += s.getTotal();
            totalItems += s.getQuantity();
            qtyPerProduct.put(s.getProductId(),
                    qtyPerProduct.getOrDefault(s.getProductId(), 0) + s.getQuantity());
            revenuePerProduct.put(s.getProductId(),
                    revenuePerProduct.getOrDefault(s.getProductId(), 0.0) + s.getTotal());
        }

        List<String> ranked = new ArrayList<>(revenuePerProduct.keySet());
        ranked.sort((a, b) -> Double.compare(revenuePerProduct.get(b), revenuePerProduct.get(a)));

        System.out.println("\n===================== SALES REPORT =====================");
        System.out.println("Total transactions : " + sales.size());
        System.out.println("Total items sold   : " + totalItems);
        System.out.printf("Total revenue      : %.2f%n", grandTotal);
        System.out.printf("Average per sale   : %.2f%n", grandTotal / sales.size());

        System.out.println("\n--- Revenue per Product (highest first) ---");
        System.out.printf("%-8s %-20s %8s %12s%n", "ID", "Product", "QtySold", "Revenue");
        for (String id : ranked) {
            Product p = products.get(id);
            String name = (p != null) ? p.getName() : "(deleted)";
            System.out.printf("%-8s %-20s %8d %12.2f%n",
                    id, name, qtyPerProduct.get(id), revenuePerProduct.get(id));
        }

        String top = ranked.get(0);
        Product tp = products.get(top);
        System.out.println("\nBest-selling product: " + (tp != null ? tp.getName() : top));

        System.out.println("\n--- Low Stock (5 or fewer) ---");
        boolean any = false;
        for (Product p : products.values()) {
            if (p.getStock() <= 5) {
                System.out.println(p.getId() + " - " + p.getName() + " (" + p.getStock() + " left)");
                any = true;
            }
        }
        if (!any) System.out.println("All products are sufficiently stocked.");
    }

    private static void loadProducts() {
        File file = new File(PRODUCT_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Product p = Product.fromFileString(line);
                products.put(p.getId(), p);
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    private static void saveProducts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCT_FILE))) {
            for (Product p : products.values()) {
                pw.println(p.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error saving products: " + e.getMessage());
        }
    }

    private static void loadSales() {
        File file = new File(SALES_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                sales.add(Sale.fromFileString(line));
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Error loading sales: " + e.getMessage());
        }
    }

    private static void saveSales() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SALES_FILE))) {
            for (Sale s : sales) {
                pw.println(s.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error saving sales: " + e.getMessage());
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static String readString(String prompt) {
        String input;
        do {
            input = readLine(prompt);
            if (input.isEmpty()) System.out.println("Input cannot be empty.");
            else if (input.contains(",")) { System.out.println("Commas are not allowed."); input = ""; }
        } while (input.isEmpty());
        return input;
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
