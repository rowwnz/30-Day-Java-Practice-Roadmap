import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    public void addItem(Product product, int qty) {
        int alreadyInCart = getQuantityOf(product.getId());
        if (alreadyInCart + qty > product.getStock()) {
            throw new IllegalArgumentException("Only " + (product.getStock() - alreadyInCart)
                    + " more of " + product.getName() + " available.");
        }
        for (CartItem item : items) {
            if (item.getProduct().getId().equalsIgnoreCase(product.getId())) {
                item.addQuantity(qty);
                return;
            }
        }
        items.add(new CartItem(product, qty));
    }

    public boolean removeItem(String productId) {
        return items.removeIf(i -> i.getProduct().getId().equalsIgnoreCase(productId));
    }

    public int getQuantityOf(String productId) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equalsIgnoreCase(productId)) return item.getQuantity();
        }
        return 0;
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items) total += item.getSubtotal();
        return total;
    }

    public List<CartItem> getItems() { return items; }
    public boolean isEmpty()         { return items.isEmpty(); }
    public void clear()              { items.clear(); }
}
