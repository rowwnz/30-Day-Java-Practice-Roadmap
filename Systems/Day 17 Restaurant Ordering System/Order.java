import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Order.java
 *
 * Represents a single customer's order. Internally uses an ArrayList
 * (the COLLECTIONS requirement) to hold an arbitrary number of OrderItem
 * line entries, since the number of dishes ordered isn't known in advance.
 */
public class Order {

    private static int nextOrderId = 1001;
    public static final double TAX_RATE = 0.12; // 12% VAT

    private final int orderId;
    private final String customerName;
    private final int tableNumber;
    private final List<OrderItem> items;
    private final LocalDateTime orderTime;
    private boolean completed;

    public Order(String customerName, int tableNumber) {
        this.orderId = nextOrderId++;
        this.customerName = customerName;
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.completed = false;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted() {
        this.completed = true;
    }

    /**
     * Adds a menu item to the order. If the same item was already added,
     * its quantity is simply increased instead of creating a duplicate line.
     */
    public void addItem(MenuItem menuItem, int quantity) {
        for (OrderItem existing : items) {
            if (existing.getMenuItem().getId() == menuItem.getId()) {
                existing.addQuantity(quantity);
                return;
            }
        }
        items.add(new OrderItem(menuItem, quantity));
    }

    public boolean removeItem(int menuItemId) {
        return items.removeIf(oi -> oi.getMenuItem().getId() == menuItemId);
    }

    public double getSubtotal() {
        double sum = 0.0;
        for (OrderItem oi : items) {
            sum += oi.getSubtotal();
        }
        return sum;
    }

    public double getTax() {
        return getSubtotal() * TAX_RATE;
    }

    public double getTotal() {
        return getSubtotal() + getTax();
    }

    public String getReceipt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("             OFFICIAL RECEIPT\n");
        sb.append("========================================\n");
        sb.append("Order #: ").append(orderId).append("\n");
        sb.append("Customer: ").append(customerName).append("\n");
        sb.append("Table #: ").append(tableNumber).append("\n");
        sb.append("Date: ").append(orderTime.format(fmt)).append("\n");
        sb.append("----------------------------------------\n");
        for (OrderItem oi : items) {
            sb.append(oi.toString()).append("\n");
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal:       P%.2f%n", getSubtotal()));
        sb.append(String.format("VAT (12%%):      P%.2f%n", getTax()));
        sb.append(String.format("TOTAL:          P%.2f%n", getTotal()));
        sb.append("========================================\n");
        return sb.toString();
    }
}
