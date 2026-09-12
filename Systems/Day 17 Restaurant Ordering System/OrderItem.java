/**
 * OrderItem.java
 *
 * Represents one line in a customer's order: a MenuItem plus how many
 * of it was ordered. This is a COMPOSITION relationship - an OrderItem
 * "has a" MenuItem.
 */
public class OrderItem {

    private final MenuItem menuItem;
    private int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%-22s x%-3d P%-8.2f = P%.2f",
                menuItem.getName(), quantity, menuItem.getPrice(), getSubtotal());
    }
}
