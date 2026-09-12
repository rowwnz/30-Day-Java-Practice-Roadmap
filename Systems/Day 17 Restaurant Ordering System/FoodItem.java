/**
 * FoodItem.java
 *
 * Concrete MenuItem representing a food dish (appetizer, main course,
 * or dessert). Demonstrates INHERITANCE: it extends MenuItem and reuses
 * all of its common fields/behavior while adding its own attribute (spicy).
 */
public class FoodItem extends MenuItem {

    private boolean spicy;

    public FoodItem(String name, double price, Category category, String description, boolean spicy) {
        super(name, price, category, description);
        this.spicy = spicy;
    }

    public boolean isSpicy() {
        return spicy;
    }

    @Override
    public String getSpecialInfo() {
        return spicy ? "Spicy" : "Not spicy";
    }
}
