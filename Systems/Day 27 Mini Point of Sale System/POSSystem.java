import java.util.Scanner;

public class POSSystem {
    private final Scanner sc = new Scanner(System.in);
    private final Inventory inventory = new Inventory("products.txt");
    private final SalesLogger logger = new SalesLogger("sales_log.txt");

    public static void main(String[] args) {
        new POSSystem().run();
    }

    private void run() {
        inventory.load();
        int choice;
        do {
            System.out.println("\n========== MINI POINT-OF-SALE SYSTEM ==========");
            System.out.println("[1] View Products");
            System.out.println("[2] Add Product");
            System.out.println("[3] Update Product");
            System.out.println("[4] Remove Product");
            System.out.println("[5] New Sale");
            System.out.println("[6] View Sales History");
            System.out.println("[0] Exit");
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> viewProducts();
                case 2 -> addProduct();
                case 3 -> updateProduct();
                case 4 -> removeProduct();
                case 5 -> newSale();
                case 6 -> logger.printHistory();
                case 0 -> System.out.println("Saving data... Goodbye!");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
        inventory.save();
    }

    private void viewProducts() {
        if (inventory.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        System.out.printf("%n%-6s %-22s %10s %7s%n", "ID", "NAME", "PRICE", "STOCK");
        System.out.println("-----------------------------------------------");
        for (Product p : inventory.getAll()) System.out.println(p);
    }

    private void addProduct() {
        String id = readText("Product ID: ");
        if (inventory.findById(id) != null) {
            System.out.println("A product with that ID already exists.");
            return;
        }
        String name = readText("Name: ");
        double price = readPositiveDouble("Price: ");
        int stock = readNonNegativeInt("Stock: ");
        inventory.addProduct(new Product(id, name, price, stock));
        inventory.save();
        System.out.println("Product added.");
    }

    private void updateProduct() {
        Product p = inventory.findById(readText("Product ID to update: "));
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }
        System.out.println("Current: " + p);
        System.out.println("[1] Name  [2] Price  [3] Stock");
        switch (readInt("Field to update: ")) {
            case 1 -> p.setName(readText("New name: "));
            case 2 -> p.setPrice(readPositiveDouble("New price: "));
            case 3 -> p.setStock(readNonNegativeInt("New stock: "));
            default -> { System.out.println("Invalid choice."); return; }
        }
        inventory.save();
        System.out.println("Product updated.");
    }

    private void removeProduct() {
        String id = readText("Product ID to remove: ");
        if (inventory.removeProduct(id)) {
            inventory.save();
            System.out.println("Product removed.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private void newSale() {
        if (inventory.isEmpty()) {
            System.out.println("No products to sell.");
            return;
        }
        Cart cart = new Cart();
        while (true) {
            System.out.println("\n--- CURRENT SALE ---  Total: " + String.format("%.2f", cart.getTotal()));
            System.out.println("[1] Add Item  [2] Remove Item  [3] View Cart  [4] Checkout  [0] Cancel Sale");
            int choice = readInt("Enter choice: ");

            if (choice == 1) {
                viewProducts();
                Product p = inventory.findById(readText("Product ID: "));
                if (p == null) { System.out.println("Product not found."); continue; }
                int qty = readInt("Quantity: ");
                if (qty <= 0) { System.out.println("Quantity must be at least 1."); continue; }
                try {
                    cart.addItem(p, qty);
                    System.out.println("Added " + qty + " x " + p.getName());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            } else if (choice == 2) {
                String id = readText("Product ID to remove from cart: ");
                System.out.println(cart.removeItem(id) ? "Item removed." : "Item not in cart.");
            } else if (choice == 3) {
                printCart(cart);
            } else if (choice == 4) {
                if (cart.isEmpty()) { System.out.println("Cart is empty."); continue; }
                if (checkout(cart)) return;
            } else if (choice == 0) {
                System.out.println("Sale cancelled.");
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void printCart(Cart cart) {
        if (cart.isEmpty()) { System.out.println("Cart is empty."); return; }
        for (CartItem item : cart.getItems()) System.out.println(item);
        System.out.printf("%-36s %12.2f%n", "TOTAL", cart.getTotal());
    }

    private boolean checkout(Cart cart) {
        printCart(cart);
        double total = cart.getTotal();
        double cash = readPositiveDouble("Cash received: ");
        if (cash < total) {
            System.out.printf("Insufficient cash. Need %.2f more.%n", total - cash);
            return false;
        }

        for (CartItem item : cart.getItems()) {
            item.getProduct().reduceStock(item.getQuantity());
        }

        String saleId = String.format("S%04d", logger.nextSaleNumber());
        Sale sale = new Sale(saleId, cart.getItems(), total, cash);
        logger.log(sale);
        inventory.save();

        System.out.println();
        System.out.print(sale.toFileString());
        cart.clear();
        return true;
    }

    private String nextLine() {
        if (!sc.hasNextLine()) {
            System.out.println("\nInput closed. Saving data and exiting.");
            inventory.save();
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = nextLine();
            if (s.isEmpty()) { System.out.println("Input cannot be empty."); continue; }
            if (s.contains("|")) { System.out.println("The '|' character is not allowed."); continue; }
            return s;
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private int readNonNegativeInt(String prompt) {
        while (true) {
            int n = readInt(prompt);
            if (n >= 0) return n;
            System.out.println("Value cannot be negative.");
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double d = Double.parseDouble(nextLine());
                if (d > 0) return d;
                System.out.println("Value must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
