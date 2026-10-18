public class CartItem {
    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity()    { return quantity; }

    public void addQuantity(int qty) { quantity += qty; }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%-22s %4d x %9.2f = %10.2f",
                product.getName(), quantity, product.getPrice(), getSubtotal());
    }
}
