import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * RestaurantOrderingSystem.java
 *
 * Main driver class with the console menu loop. Holds the Menu and the
 * order history (an ArrayList<Order> - another COLLECTIONS usage) and
 * ties everything together.
 */
public class RestaurantOrderingSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Menu menu = new Menu();
    private static final List<Order> orderHistory = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  WELCOME TO THE RESTAURANT ORDERING SYSTEM");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1:
                    menu.displayFullMenu();
                    break;
                case 2:
                    viewByCategory();
                    break;
                case 3:
                    placeNewOrder();
                    break;
                case 4:
                    viewOrderHistory();
                    break;
                case 5:
                    running = false;
                    System.out.println("Thank you! Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n1. View Full Menu");
        System.out.println("2. View Menu by Category");
        System.out.println("3. Place New Order");
        System.out.println("4. View Order History");
        System.out.println("5. Exit");
    }

    private static void viewByCategory() {
        System.out.println("\nCategories:");
        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            System.out.println((i + 1) + ". " + cats[i].getLabel());
        }
        int choice = readInt("Choose category: ");
        if (choice < 1 || choice > cats.length) {
            System.out.println("Invalid category.");
            return;
        }
        Category selected = cats[choice - 1];
        List<MenuItem> items = menu.getItemsGroupedByCategory().get(selected);
        if (items.isEmpty()) {
            System.out.println("No items in this category.");
            return;
        }
        for (MenuItem item : items) {
            System.out.println(item.toString() + " | " + item.getSpecialInfo());
        }
    }

    private static void placeNewOrder() {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        int table = readInt("Enter table number: ");

        Order order = new Order(name, table);
        boolean ordering = true;
        while (ordering) {
            menu.displayFullMenu();
            int id = readInt("\nEnter item number to add (0 to finish): ");
            if (id == 0) {
                ordering = false;
                continue;
            }
            MenuItem item = menu.getItemById(id);
            if (item == null) {
                System.out.println("Item not found. Please try again.");
                continue;
            }
            int qty = readInt("Enter quantity: ");
            if (qty <= 0) {
                System.out.println("Quantity must be positive.");
                continue;
            }
            order.addItem(item, qty);
            System.out.println(qty + "x " + item.getName() + " added to order.");
        }

        if (order.getItems().isEmpty()) {
            System.out.println("Order cancelled - no items were selected.");
            return;
        }

        order.markCompleted();
        orderHistory.add(order);
        System.out.println("\n" + order.getReceipt());
    }

    private static void viewOrderHistory() {
        if (orderHistory.isEmpty()) {
            System.out.println("No orders placed yet.");
            return;
        }
        for (Order o : orderHistory) {
            System.out.println(String.format("Order #%d - %s (Table %d) - Total: P%.2f",
                    o.getOrderId(), o.getCustomerName(), o.getTableNumber(), o.getTotal()));
        }
        int id = readInt("\nEnter order number to view full receipt (0 to skip): ");
        if (id == 0) {
            return;
        }
        for (Order o : orderHistory) {
            if (o.getOrderId() == id) {
                System.out.println(o.getReceipt());
                return;
            }
        }
        System.out.println("Order not found.");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
