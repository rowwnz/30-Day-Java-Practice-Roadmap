import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Day 29 - Console E-Commerce System (Full-system integration)
 *
 * Integrates: user accounts (login/register, roles), product catalog,
 * inventory/stock control, shopping cart, vouchers, VAT, checkout with
 * payment, receipts, order history, and an admin panel with sales reports.
 */
public class JavaMartECommerce {

    // ================================================================
    // MODELS
    // ================================================================

    enum Role { ADMIN, CUSTOMER }

    enum PaymentMethod { CASH, CARD, E_WALLET }

    static class Product {
        private final int id;
        private String name;
        private String category;
        private double price;
        private int stock;

        Product(int id, String name, String category, double price, int stock) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        int getId() { return id; }
        String getName() { return name; }
        String getCategory() { return category; }
        double getPrice() { return price; }
        int getStock() { return stock; }

        void setName(String name) { this.name = name; }
        void setCategory(String category) { this.category = category; }
        void setPrice(double price) { this.price = price; }
        void addStock(int qty) { this.stock += qty; }
        void reduceStock(int qty) { this.stock -= qty; }
    }

    static class CartItem {
        private final Product product;
        private int quantity;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        Product getProduct() { return product; }
        int getQuantity() { return quantity; }
        void setQuantity(int quantity) { this.quantity = quantity; }
        double getLineTotal() { return product.getPrice() * quantity; }
    }

    static class Cart {
        private final Map<Integer, CartItem> items = new LinkedHashMap<>();

        void add(Product p, int qty) {
            CartItem existing = items.get(p.getId());
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + qty);
            } else {
                items.put(p.getId(), new CartItem(p, qty));
            }
        }

        boolean update(int productId, int qty) {
            CartItem item = items.get(productId);
            if (item == null) return false;
            if (qty <= 0) items.remove(productId);
            else item.setQuantity(qty);
            return true;
        }

        boolean remove(int productId) { return items.remove(productId) != null; }

        int quantityOf(int productId) {
            CartItem item = items.get(productId);
            return item == null ? 0 : item.getQuantity();
        }

        Collection<CartItem> getItems() { return items.values(); }
        boolean isEmpty() { return items.isEmpty(); }
        void clear() { items.clear(); }

        double getSubtotal() {
            double total = 0;
            for (CartItem item : items.values()) total += item.getLineTotal();
            return total;
        }
    }

    static class OrderLine {
        final String productName;
        final double unitPrice;
        final int quantity;

        OrderLine(String productName, double unitPrice, int quantity) {
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        double total() { return unitPrice * quantity; }
    }

    static class Order {
        final String orderId;
        final String username;
        final List<OrderLine> lines;
        final double subtotal;
        final double discount;
        final double vat;
        final double total;
        final PaymentMethod paymentMethod;
        final double amountPaid;
        final LocalDateTime dateTime;
        final String voucherCode;

        Order(String orderId, String username, List<OrderLine> lines, double subtotal,
              double discount, double vat, double total, PaymentMethod paymentMethod,
              double amountPaid, String voucherCode) {
            this.orderId = orderId;
            this.username = username;
            this.lines = lines;
            this.subtotal = subtotal;
            this.discount = discount;
            this.vat = vat;
            this.total = total;
            this.paymentMethod = paymentMethod;
            this.amountPaid = amountPaid;
            this.voucherCode = voucherCode;
            this.dateTime = LocalDateTime.now();
        }
    }

    static class User {
        private final String username;
        private final String password;
        private final Role role;
        private final Cart cart = new Cart();
        private final List<Order> orders = new ArrayList<>();

        User(String username, String password, Role role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        String getUsername() { return username; }
        Role getRole() { return role; }
        Cart getCart() { return cart; }
        List<Order> getOrders() { return orders; }
        boolean checkPassword(String input) { return password.equals(input); }
    }

    // ================================================================
    // SERVICES
    // ================================================================

    static class Inventory {
        private final Map<Integer, Product> products = new LinkedHashMap<>();
        private int nextId = 101;
        static final int LOW_STOCK_LIMIT = 5;

        Product addProduct(String name, String category, double price, int stock) {
            Product p = new Product(nextId++, name, category, price, stock);
            products.put(p.getId(), p);
            return p;
        }

        Product find(int id) { return products.get(id); }
        boolean remove(int id) { return products.remove(id) != null; }
        Collection<Product> getAll() { return products.values(); }

        List<Product> search(String keyword) {
            List<Product> result = new ArrayList<>();
            String k = keyword.toLowerCase();
            for (Product p : products.values()) {
                if (p.getName().toLowerCase().contains(k)
                        || p.getCategory().toLowerCase().contains(k)) {
                    result.add(p);
                }
            }
            return result;
        }

        Set<String> getCategories() {
            Set<String> categories = new TreeSet<>();
            for (Product p : products.values()) categories.add(p.getCategory());
            return categories;
        }

        List<Product> byCategory(String category) {
            List<Product> result = new ArrayList<>();
            for (Product p : products.values()) {
                if (p.getCategory().equalsIgnoreCase(category)) result.add(p);
            }
            return result;
        }

        List<Product> lowStock() {
            List<Product> result = new ArrayList<>();
            for (Product p : products.values()) {
                if (p.getStock() <= LOW_STOCK_LIMIT) result.add(p);
            }
            return result;
        }
    }

    static class UserService {
        private final Map<String, User> users = new HashMap<>();

        boolean register(String username, String password, Role role) {
            String key = username.toLowerCase();
            if (users.containsKey(key)) return false;
            users.put(key, new User(username, password, role));
            return true;
        }

        User login(String username, String password) {
            User u = users.get(username.toLowerCase());
            return (u != null && u.checkPassword(password)) ? u : null;
        }
    }

    static class OrderService {
        static final double VAT_RATE = 0.12;
        private final List<Order> allOrders = new ArrayList<>();
        private final Map<String, Double> vouchers = new HashMap<>();
        private int orderCounter = 1;

        OrderService() {
            vouchers.put("SAVE10", 0.10);
            vouchers.put("STUDENT15", 0.15);
            vouchers.put("WELCOME5", 0.05);
        }

        Double getVoucherRate(String code) {
            return vouchers.get(code.toUpperCase());
        }

        /** Returns null if valid, otherwise an error message. */
        String validateStock(Cart cart) {
            for (CartItem item : cart.getItems()) {
                Product p = item.getProduct();
                if (item.getQuantity() > p.getStock()) {
                    return "Not enough stock for " + p.getName()
                            + " (requested " + item.getQuantity()
                            + ", available " + p.getStock() + ").";
                }
            }
            return null;
        }

        Order placeOrder(User user, double discountRate, String voucherCode,
                         PaymentMethod method, double amountPaid) {
            Cart cart = user.getCart();
            List<OrderLine> lines = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                Product p = item.getProduct();
                lines.add(new OrderLine(p.getName(), p.getPrice(), item.getQuantity()));
                p.reduceStock(item.getQuantity());   // inventory integration
            }

            double subtotal = cart.getSubtotal();
            double discount = round(subtotal * discountRate);
            double vat = round((subtotal - discount) * VAT_RATE);
            double total = round(subtotal - discount + vat);

            String orderId = String.format("ORD-%04d", orderCounter++);
            Order order = new Order(orderId, user.getUsername(), lines, subtotal,
                    discount, vat, total, method, amountPaid, voucherCode);

            user.getOrders().add(order);
            allOrders.add(order);
            cart.clear();
            return order;
        }

        List<Order> getAllOrders() { return allOrders; }

        static double computeTotal(double subtotal, double discountRate) {
            double discount = round(subtotal * discountRate);
            double vat = round((subtotal - discount) * VAT_RATE);
            return round(subtotal - discount + vat);
        }
    }

    // ================================================================
    // APPLICATION STATE
    // ================================================================

    static final Scanner sc = new Scanner(System.in);
    static final Inventory inventory = new Inventory();
    static final UserService userService = new UserService();
    static final OrderService orderService = new OrderService();
    static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        seedData();
        printBanner();

        boolean running = true;
        while (running) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("[1] Login");
            System.out.println("[2] Register");
            System.out.println("[3] Browse as Guest");
            System.out.println("[0] Exit");
            int choice = readInt("Choose: ", 0, 3);

            switch (choice) {
                case 1 -> {
                    User user = loginFlow();
                    if (user != null) {
                        if (user.getRole() == Role.ADMIN) adminMenu(user);
                        else customerMenu(user);
                    }
                }
                case 2 -> registerFlow();
                case 3 -> browseMenu(null);
                case 0 -> running = false;
            }
        }
        System.out.println("\nThank you for shopping with JavaMart. Goodbye!");
    }

    static void seedData() {
        userService.register("admin", "admin123", Role.ADMIN);
        userService.register("juan", "juan123", Role.CUSTOMER);

        inventory.addProduct("Wireless Mouse", "Electronics", 499.00, 25);
        inventory.addProduct("Mechanical Keyboard", "Electronics", 2499.00, 10);
        inventory.addProduct("USB-C Hub", "Electronics", 899.00, 4);
        inventory.addProduct("Laptop Stand", "Accessories", 750.00, 15);
        inventory.addProduct("Mouse Pad XL", "Accessories", 299.00, 30);
        inventory.addProduct("Java Programming Book", "Books", 1200.00, 8);
        inventory.addProduct("Clean Code Book", "Books", 1550.00, 3);
        inventory.addProduct("Water Bottle", "Lifestyle", 350.00, 40);
    }

    static void printBanner() {
        System.out.println("=========================================");
        System.out.println("     JAVAMART - Console E-Commerce       ");
        System.out.println("=========================================");
        System.out.println("Demo accounts: admin / admin123 (Admin)");
        System.out.println("               juan  / juan123  (Customer)");
    }

    // ================================================================
    // AUTH
    // ================================================================

    static User loginFlow() {
        System.out.println("\n--- LOGIN ---");
        String username = readLine("Username: ");
        String password = readLine("Password: ");
        User user = userService.login(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.");
            return null;
        }
        System.out.println("Welcome, " + user.getUsername() + "! (" + user.getRole() + ")");
        return user;
    }

    static void registerFlow() {
        System.out.println("\n--- REGISTER ---");
        String username = readLine("Choose a username: ");
        if (username.length() < 3 || username.contains(" ")) {
            System.out.println("Username must be at least 3 characters with no spaces.");
            return;
        }
        String password = readLine("Choose a password (min 6 chars): ");
        if (password.length() < 6) {
            System.out.println("Password too short.");
            return;
        }
        if (userService.register(username, password, Role.CUSTOMER)) {
            System.out.println("Account created. You can now log in.");
        } else {
            System.out.println("Username already taken.");
        }
    }

    // ================================================================
    // CUSTOMER
    // ================================================================

    static void customerMenu(User user) {
        while (true) {
            int cartCount = 0;
            for (CartItem item : user.getCart().getItems()) cartCount += item.getQuantity();

            System.out.println("\n======= CUSTOMER MENU (" + user.getUsername() + ") =======");
            System.out.println("[1] Browse / Search Products");
            System.out.println("[2] View Cart (" + cartCount + " item/s)");
            System.out.println("[3] Checkout");
            System.out.println("[4] Order History");
            System.out.println("[0] Logout");
            int choice = readInt("Choose: ", 0, 4);

            switch (choice) {
                case 1 -> browseMenu(user);
                case 2 -> cartMenu(user);
                case 3 -> checkout(user);
                case 4 -> showOrderHistory(user);
                case 0 -> {
                    System.out.println("Logged out.");
                    return;
                }
            }
        }
    }

    static void browseMenu(User user) {
        while (true) {
            System.out.println("\n--- BROWSE PRODUCTS ---");
            System.out.println("[1] View All");
            System.out.println("[2] Filter by Category");
            System.out.println("[3] Search");
            if (user != null) System.out.println("[4] Add to Cart");
            System.out.println("[0] Back");
            int max = (user != null) ? 4 : 3;
            int choice = readInt("Choose: ", 0, max);

            switch (choice) {
                case 1 -> printProducts(inventory.getAll());
                case 2 -> {
                    List<String> categories = new ArrayList<>(inventory.getCategories());
                    for (int i = 0; i < categories.size(); i++) {
                        System.out.println("[" + (i + 1) + "] " + categories.get(i));
                    }
                    int c = readInt("Category: ", 1, categories.size());
                    printProducts(inventory.byCategory(categories.get(c - 1)));
                }
                case 3 -> {
                    String keyword = readLine("Keyword: ");
                    printProducts(inventory.search(keyword));
                }
                case 4 -> addToCart(user);
                case 0 -> {
                    return;
                }
            }
        }
    }

    static void addToCart(User user) {
        int id = readInt("Enter Product ID: ", 0, Integer.MAX_VALUE);
        Product p = inventory.find(id);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }
        int available = p.getStock() - user.getCart().quantityOf(id);
        if (available <= 0) {
            System.out.println("No more stock available for " + p.getName() + ".");
            return;
        }
        int qty = readInt("Quantity (1-" + available + "): ", 1, available);
        user.getCart().add(p, qty);
        System.out.println(qty + " x " + p.getName() + " added to cart.");
    }

    static void cartMenu(User user) {
        Cart cart = user.getCart();
        while (true) {
            printCart(cart);
            if (cart.isEmpty()) return;

            System.out.println("[1] Update Quantity");
            System.out.println("[2] Remove Item");
            System.out.println("[3] Clear Cart");
            System.out.println("[0] Back");
            int choice = readInt("Choose: ", 0, 3);

            switch (choice) {
                case 1 -> {
                    int id = readInt("Product ID: ", 0, Integer.MAX_VALUE);
                    Product p = inventory.find(id);
                    if (p == null || cart.quantityOf(id) == 0) {
                        System.out.println("Item not in cart.");
                        break;
                    }
                    int qty = readInt("New quantity (0 to remove, max " + p.getStock() + "): ",
                            0, p.getStock());
                    cart.update(id, qty);
                    System.out.println("Cart updated.");
                }
                case 2 -> {
                    int id = readInt("Product ID: ", 0, Integer.MAX_VALUE);
                    System.out.println(cart.remove(id) ? "Item removed." : "Item not in cart.");
                }
                case 3 -> {
                    if (confirm("Clear the entire cart? (y/n): ")) {
                        cart.clear();
                        System.out.println("Cart cleared.");
                    }
                }
                case 0 -> {
                    return;
                }
            }
        }
    }

    static void checkout(User user) {
        Cart cart = user.getCart();
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        String stockError = orderService.validateStock(cart);
        if (stockError != null) {
            System.out.println("Checkout failed: " + stockError);
            return;
        }

        printCart(cart);

        // Voucher
        double discountRate = 0;
        String voucherCode = null;
        if (confirm("Apply a voucher code? (y/n): ")) {
            String code = readLine("Voucher code: ");
            Double rate = orderService.getVoucherRate(code);
            if (rate == null) {
                System.out.println("Invalid voucher. Continuing without discount.");
            } else {
                discountRate = rate;
                voucherCode = code.toUpperCase();
                System.out.printf("Voucher applied: %.0f%% off%n", rate * 100);
            }
        }

        double subtotal = cart.getSubtotal();
        double discount = round(subtotal * discountRate);
        double vat = round((subtotal - discount) * OrderService.VAT_RATE);
        double total = OrderService.computeTotal(subtotal, discountRate);

        System.out.println("\n--- ORDER SUMMARY ---");
        System.out.printf("Subtotal : %s%n", peso(subtotal));
        System.out.printf("Discount : -%s%n", peso(discount));
        System.out.printf("VAT (12%%): %s%n", peso(vat));
        System.out.printf("TOTAL    : %s%n", peso(total));

        // Payment
        System.out.println("\nPayment Method:");
        System.out.println("[1] Cash");
        System.out.println("[2] Card");
        System.out.println("[3] E-Wallet");
        System.out.println("[0] Cancel");
        int pm = readInt("Choose: ", 0, 3);
        if (pm == 0) {
            System.out.println("Checkout cancelled.");
            return;
        }
        PaymentMethod method = PaymentMethod.values()[pm - 1];

        double amountPaid = total;
        if (method == PaymentMethod.CASH) {
            amountPaid = readDouble("Cash amount: ", total);
        } else {
            String ref = readLine("Enter reference / last 4 digits: ");
            if (ref.isBlank()) {
                System.out.println("Payment reference required. Checkout cancelled.");
                return;
            }
        }

        if (!confirm("Confirm order? (y/n): ")) {
            System.out.println("Checkout cancelled.");
            return;
        }

        Order order = orderService.placeOrder(user, discountRate, voucherCode, method, amountPaid);
        System.out.println("\nOrder placed successfully!");
        printReceipt(order);
    }

    static void showOrderHistory(User user) {
        List<Order> orders = user.getOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }
        System.out.println("\n--- ORDER HISTORY ---");
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            System.out.printf("[%d] %s | %s | %s | %s%n", i + 1, o.orderId,
                    o.dateTime.format(DATE_FMT), o.paymentMethod, peso(o.total));
        }
        int choice = readInt("View receipt # (0 to go back): ", 0, orders.size());
        if (choice > 0) printReceipt(orders.get(choice - 1));
    }

    // ================================================================
    // ADMIN
    // ================================================================

    static void adminMenu(User admin) {
        while (true) {
            System.out.println("\n========== ADMIN PANEL ==========");
            System.out.println("[1] View Inventory");
            System.out.println("[2] Add Product");
            System.out.println("[3] Edit Product");
            System.out.println("[4] Restock Product");
            System.out.println("[5] Remove Product");
            System.out.println("[6] Low Stock Report");
            System.out.println("[7] View All Orders");
            System.out.println("[8] Sales Report");
            System.out.println("[0] Logout");
            int choice = readInt("Choose: ", 0, 8);

            switch (choice) {
                case 1 -> printProducts(inventory.getAll());
                case 2 -> adminAddProduct();
                case 3 -> adminEditProduct();
                case 4 -> adminRestock();
                case 5 -> adminRemoveProduct();
                case 6 -> {
                    List<Product> low = inventory.lowStock();
                    if (low.isEmpty()) System.out.println("All products are sufficiently stocked.");
                    else {
                        System.out.println("Products at or below " + Inventory.LOW_STOCK_LIMIT + " units:");
                        printProducts(low);
                    }
                }
                case 7 -> adminViewOrders();
                case 8 -> salesReport();
                case 0 -> {
                    System.out.println("Logged out.");
                    return;
                }
            }
        }
    }

    static void adminAddProduct() {
        String name = readLine("Product name: ");
        if (name.isBlank()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        String category = readLine("Category: ");
        double price = readDouble("Price: ", 0.01);
        int stock = readInt("Initial stock: ", 0, 100000);
        Product p = inventory.addProduct(name, category.isBlank() ? "General" : category, price, stock);
        System.out.println("Product added with ID " + p.getId() + ".");
    }

    static void adminEditProduct() {
        Product p = inventory.find(readInt("Product ID: ", 0, Integer.MAX_VALUE));
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }
        String name = readLine("New name (blank to keep \"" + p.getName() + "\"): ");
        if (!name.isBlank()) p.setName(name);
        String category = readLine("New category (blank to keep \"" + p.getCategory() + "\"): ");
        if (!category.isBlank()) p.setCategory(category);
        if (confirm("Change price (current " + peso(p.getPrice()) + ")? (y/n): ")) {
            p.setPrice(readDouble("New price: ", 0.01));
        }
        System.out.println("Product updated.");
    }

    static void adminRestock() {
        Product p = inventory.find(readInt("Product ID: ", 0, Integer.MAX_VALUE));
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }
        int qty = readInt("Quantity to add: ", 1, 100000);
        p.addStock(qty);
        System.out.println(p.getName() + " stock is now " + p.getStock() + ".");
    }

    static void adminRemoveProduct() {
        int id = readInt("Product ID: ", 0, Integer.MAX_VALUE);
        Product p = inventory.find(id);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }
        if (confirm("Remove \"" + p.getName() + "\"? (y/n): ")) {
            inventory.remove(id);
            System.out.println("Product removed.");
        }
    }

    static void adminViewOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders have been placed yet.");
            return;
        }
        System.out.println("\n--- ALL ORDERS ---");
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            System.out.printf("[%d] %s | %-10s | %s | %-8s | %s%n", i + 1, o.orderId, o.username,
                    o.dateTime.format(DATE_FMT), o.paymentMethod, peso(o.total));
        }
        int choice = readInt("View receipt # (0 to go back): ", 0, orders.size());
        if (choice > 0) printReceipt(orders.get(choice - 1));
    }

    static void salesReport() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No sales recorded yet.");
            return;
        }

        double gross = 0, discounts = 0, vat = 0, net = 0;
        int itemsSold = 0;
        Map<String, Integer> qtyByProduct = new HashMap<>();
        Map<PaymentMethod, Double> byPayment = new EnumMap<>(PaymentMethod.class);

        for (Order o : orders) {
            gross += o.subtotal;
            discounts += o.discount;
            vat += o.vat;
            net += o.total;
            byPayment.merge(o.paymentMethod, o.total, Double::sum);
            for (OrderLine line : o.lines) {
                itemsSold += line.quantity;
                qtyByProduct.merge(line.productName, line.quantity, Integer::sum);
            }
        }

        String bestSeller = Collections.max(qtyByProduct.entrySet(), Map.Entry.comparingByValue()).getKey();

        System.out.println("\n=========== SALES REPORT ===========");
        System.out.printf("Total orders     : %d%n", orders.size());
        System.out.printf("Items sold       : %d%n", itemsSold);
        System.out.printf("Gross sales      : %s%n", peso(gross));
        System.out.printf("Total discounts  : -%s%n", peso(discounts));
        System.out.printf("VAT collected    : %s%n", peso(vat));
        System.out.printf("Net revenue      : %s%n", peso(net));
        System.out.printf("Average order    : %s%n", peso(net / orders.size()));
        System.out.printf("Best seller      : %s (%d sold)%n", bestSeller, qtyByProduct.get(bestSeller));
        System.out.println("Revenue by payment method:");
        for (Map.Entry<PaymentMethod, Double> e : byPayment.entrySet()) {
            System.out.printf("  %-9s: %s%n", e.getKey(), peso(e.getValue()));
        }
        System.out.println("====================================");
    }

    // ================================================================
    // DISPLAY HELPERS
    // ================================================================

    static void printProducts(Collection<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        String line = "+-----+--------------------------+--------------+--------------+-------+";
        System.out.println(line);
        System.out.printf("| %-3s | %-24s | %-12s | %12s | %5s |%n", "ID", "Name", "Category", "Price", "Stock");
        System.out.println(line);
        for (Product p : products) {
            String stock = p.getStock() == 0 ? "OUT" : String.valueOf(p.getStock());
            System.out.printf("| %-3d | %-24s | %-12s | %12s | %5s |%n", p.getId(),
                    truncate(p.getName(), 24), truncate(p.getCategory(), 12), peso(p.getPrice()), stock);
        }
        System.out.println(line);
    }

    static void printCart(Cart cart) {
        System.out.println("\n--- YOUR CART ---");
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        String line = "+-----+--------------------------+--------------+-----+--------------+";
        System.out.println(line);
        System.out.printf("| %-3s | %-24s | %12s | %3s | %12s |%n", "ID", "Product", "Unit Price", "Qty", "Line Total");
        System.out.println(line);
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            System.out.printf("| %-3d | %-24s | %12s | %3d | %12s |%n", p.getId(),
                    truncate(p.getName(), 24), peso(p.getPrice()), item.getQuantity(), peso(item.getLineTotal()));
        }
        System.out.println(line);
        System.out.printf("Subtotal: %s%n", peso(cart.getSubtotal()));
    }

    static void printReceipt(Order o) {
        System.out.println("\n========================================");
        System.out.println("               JAVAMART                 ");
        System.out.println("            OFFICIAL RECEIPT            ");
        System.out.println("========================================");
        System.out.println("Order ID : " + o.orderId);
        System.out.println("Customer : " + o.username);
        System.out.println("Date     : " + o.dateTime.format(DATE_FMT));
        System.out.println("----------------------------------------");
        for (OrderLine line : o.lines) {
            System.out.printf("%-24s%n", truncate(line.productName, 24));
            System.out.printf("  %d x %-14s %18s%n", line.quantity, peso(line.unitPrice), peso(line.total()));
        }
        System.out.println("----------------------------------------");
        System.out.printf("%-20s %19s%n", "Subtotal", peso(o.subtotal));
        if (o.discount > 0) {
            System.out.printf("%-20s %19s%n", "Discount (" + o.voucherCode + ")", "-" + peso(o.discount));
        }
        System.out.printf("%-20s %19s%n", "VAT (12%)", peso(o.vat));
        System.out.printf("%-20s %19s%n", "TOTAL", peso(o.total));
        System.out.printf("%-20s %19s%n", "Paid via " + o.paymentMethod, peso(o.amountPaid));
        if (o.paymentMethod == PaymentMethod.CASH) {
            System.out.printf("%-20s %19s%n", "Change", peso(o.amountPaid - o.total));
        }
        System.out.println("========================================");
        System.out.println("     Thank you for your purchase!       ");
        System.out.println("========================================");
    }

    // ================================================================
    // INPUT & UTILITY HELPERS
    // ================================================================

    static String readLine(String prompt) {
        System.out.print(prompt);
        if (!sc.hasNextLine()) System.exit(0);
        return sc.nextLine().trim();
    }

    static int readInt(String prompt, int min, int max) {
        while (true) {
            String input = readLine(prompt);
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                System.out.println("Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    static double readDouble(String prompt, double min) {
        while (true) {
            String input = readLine(prompt);
            try {
                double value = Double.parseDouble(input);
                if (value >= min) return round(value);
                System.out.printf("Amount must be at least %s.%n", peso(min));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    static boolean confirm(String prompt) {
        while (true) {
            String input = readLine(prompt).toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Please enter y or n.");
        }
    }

    static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    static String peso(double amount) {
        return String.format("PHP %,.2f", amount);
    }

    static String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
