public abstract class Item {
    private final String id;
    private String name;
    private String category;
    private int quantity;
    private double unitPrice;

    protected Item(String id, String name, String category, int quantity, double unitPrice) {
        this.id = id;
        setName(name);
        setCategory(category);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
    }

    public abstract String getType();

    public abstract String getExtraInfo();

    protected abstract String extraFileData();

    public double getTotalValue() {
        return quantity * unitPrice;
    }

    public void addStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be greater than 0.");
        }
        quantity += amount;
    }

    public void removeStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to remove must be greater than 0.");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException("Not enough stock. Available: " + quantity);
        }
        quantity -= amount;
    }

    public String toFileLine() {
        return String.join(FileInventoryRepository.DELIMITER,
                getType(), id, name, category,
                String.valueOf(quantity), String.valueOf(unitPrice),
                extraFileData());
    }

    @Override
    public String toString() {
        return String.format("%-9s %-10s %-20s %-14s %6d %12.2f %14.2f  %s",
                id, getType(), name, category, quantity, unitPrice, getTotalValue(), getExtraInfo());
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }

    public void setName(String name) {
        this.name = requireText(name, "Name");
    }

    public void setCategory(String category) {
        this.category = requireText(category, "Category");
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.unitPrice = unitPrice;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        if (value.contains(FileInventoryRepository.DELIMITER)) {
            throw new IllegalArgumentException(field + " cannot contain the '"
                    + FileInventoryRepository.DELIMITER + "' character.");
        }
        return value.trim();
    }
}
