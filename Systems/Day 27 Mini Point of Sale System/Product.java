public class Product implements Storable {
    private final String id;
    private String name;
    private double price;
    private int stock;

    public Product(String id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        setPrice(price);
        setStock(stock);
    }

    public String getId()    { return id; }
    public String getName()  { return name; }
    public double getPrice() { return price; }
    public int getStock()    { return stock; }

    public void setName(String name) { this.name = name; }

    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        this.price = price;
    }

    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative.");
        this.stock = stock;
    }

    public void reduceStock(int qty) {
        if (qty > stock) throw new IllegalArgumentException("Not enough stock for " + name + ".");
        stock -= qty;
    }

    @Override
    public String toFileString() {
        return id + "|" + name + "|" + price + "|" + stock;
    }

    public static Product fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 4) throw new IllegalArgumentException("Invalid product line: " + line);
        return new Product(parts[0].trim(), parts[1].trim(),
                Double.parseDouble(parts[2].trim()), Integer.parseInt(parts[3].trim()));
    }

    @Override
    public String toString() {
        return String.format("%-6s %-22s %10.2f %7d", id, name, price, stock);
    }
}
