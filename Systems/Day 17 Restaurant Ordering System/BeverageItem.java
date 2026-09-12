/**
 * BeverageItem.java
 *
 * Concrete MenuItem representing a drink. Its category is always
 * Category.BEVERAGE, so the constructor sets that automatically instead
 * of asking the caller for it. Also demonstrates INHERITANCE.
 */
public class BeverageItem extends MenuItem {

    private String size; // "Small", "Medium", "Large"
    private boolean cold;

    public BeverageItem(String name, double price, String description, String size, boolean cold) {
        super(name, price, Category.BEVERAGE, description);
        this.size = size;
        this.cold = cold;
    }

    public String getSize() {
        return size;
    }

    public boolean isCold() {
        return cold;
    }

    @Override
    public String getSpecialInfo() {
        return size + ", served " + (cold ? "cold" : "hot");
    }
}
