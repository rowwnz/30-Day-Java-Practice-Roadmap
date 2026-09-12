/**
 * Category.java
 *
 * Enum representing the categories a MenuItem can belong to.
 * Using an enum here (instead of plain Strings) prevents invalid
 * category values and makes category-based grouping type-safe.
 */
public enum Category {
    APPETIZER("Appetizer"),
    MAIN_COURSE("Main Course"),
    DESSERT("Dessert"),
    BEVERAGE("Beverage");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
