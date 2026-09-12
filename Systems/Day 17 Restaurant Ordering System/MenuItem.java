/**
 * MenuItem.java
 *
 * Abstract base class for every item that can appear on the restaurant's
 * menu. Demonstrates:
 *  - ENCAPSULATION: fields are private, accessed only through getters/setters.
 *  - ABSTRACTION: this class cannot be instantiated directly; it defines
 *    the common contract (getSpecialInfo) that every concrete item must
 *    implement in its own way.
 */
public abstract class MenuItem {

    // Shared counter used to auto-generate a unique id for every item created.
    private static int nextId = 1;

    private final int id;
    private String name;
    private double price;
    private final Category category;
    private String description;

    public MenuItem(String name, double price, Category category, String description) {
        this.id = nextId++;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    /**
     * POLYMORPHISM: each subclass returns something different here
     * (spiciness for food, size/temperature for beverages), but callers
     * can invoke it through a single MenuItem reference.
     */
    public abstract String getSpecialInfo();

    @Override
    public String toString() {
        return String.format("[%2d] %-22s P%-8.2f (%s)", id, name, price, category.getLabel());
    }
}
