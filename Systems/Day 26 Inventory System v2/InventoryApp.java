import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Scanner;

public class InventoryApp {
    private static final String SAVE_FILE = "inventory.txt";
    private static final int DEFAULT_LOW_STOCK = 5;

    private final Scanner scanner = new Scanner(System.in);
    private final InventoryRepository repository;
    private Inventory inventory;
    private boolean unsavedChanges = false;

    public InventoryApp(InventoryRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        new InventoryApp(new FileInventoryRepository(SAVE_FILE)).run();
    }

    public void run() {
        try {
            inventory = repository.load();
            System.out.println("Loaded " + inventory.size() + " item(s) from " + SAVE_FILE + ".");
        } catch (IOException e) {
            System.out.println("Could not read save file: " + e.getMessage());
            System.out.println("Starting with an empty inventory.");
            inventory = new Inventory();
        }

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ", 0, 10);
            System.out.println();
            switch (choice) {
                case 1:  addItem(); break;
                case 2:  listItems(inventory.getAllItems(), "ALL ITEMS"); break;
                case 3:  searchItems(); break;
                case 4:  restockItem(); break;
                case 5:  sellItem(); break;
                case 6:  editItem(); break;
                case 7:  deleteItem(); break;
                case 8:  showReports(); break;
                case 9:  save(); break;
                case 10: listItems(inventory.getExpiredItems(), "EXPIRED ITEMS"); break;
                case 0:  running = !confirmExit(); break;
                default: break;
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("========== INVENTORY SYSTEM v2 ==========");
        System.out.println(" 1. Add item");
        System.out.println(" 2. View all items");
        System.out.println(" 3. Search items");
        System.out.println(" 4. Restock item (add quantity)");
        System.out.println(" 5. Sell / issue item (deduct quantity)");
        System.out.println(" 6. Edit item details");
        System.out.println(" 7. Delete item");
        System.out.println(" 8. Reports (total value, low stock)");
        System.out.println(" 9. Save now");
        System.out.println("10. View expired items");
        System.out.println(" 0. Exit");
        System.out.println("=========================================" + (unsavedChanges ? "  * unsaved changes" : ""));
    }

    private void addItem() {
        System.out.println("--- Add Item ---");
        System.out.println("1. Standard item");
        System.out.println("2. Perishable item (has expiry date)");
        int type = readInt("Item type: ", 1, 2);

        String name = readText("Name: ");
        String category = readText("Category: ");
        int quantity = readInt("Quantity: ", 0, Integer.MAX_VALUE);
        double price = readDouble("Unit price: ");

        try {
            String id = inventory.generateId();
            Item item = (type == 1)
                    ? new StandardItem(id, name, category, quantity, price)
                    : new PerishableItem(id, name, category, quantity, price,
                                         readDate("Expiry date (yyyy-mm-dd): "));
            inventory.addItem(item);
            markChanged();
            System.out.println("Added " + item.getName() + " with ID " + item.getId() + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchItems() {
        String keyword = readText("Enter name or category keyword: ");
        listItems(inventory.searchByName(keyword), "SEARCH RESULTS FOR \"" + keyword + "\"");
    }

    private void restockItem() {
        Item item = promptForItem();
        if (item == null) return;
        int amount = readInt("Quantity to add: ", 1, Integer.MAX_VALUE);
        item.addStock(amount);
        markChanged();
        System.out.println("New quantity of " + item.getName() + ": " + item.getQuantity());
    }

    private void sellItem() {
        Item item = promptForItem();
        if (item == null) return;
        if (item instanceof PerishableItem && ((PerishableItem) item).isExpired()) {
            System.out.println("Warning: this item is expired.");
        }
        int amount = readInt("Quantity to deduct: ", 1, Integer.MAX_VALUE);
        try {
            item.removeStock(amount);
            markChanged();
            System.out.printf("Deducted %d. Amount: %.2f | Remaining: %d%n",
                    amount, amount * item.getUnitPrice(), item.getQuantity());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void editItem() {
        Item item = promptForItem();
        if (item == null) return;

        System.out.println("Press Enter to keep the current value.");
        try {
            String name = readOptional("Name [" + item.getName() + "]: ");
            if (!name.isEmpty()) item.setName(name);

            String category = readOptional("Category [" + item.getCategory() + "]: ");
            if (!category.isEmpty()) item.setCategory(category);

            String qty = readOptional("Quantity [" + item.getQuantity() + "]: ");
            if (!qty.isEmpty()) item.setQuantity(Integer.parseInt(qty));

            String price = readOptional("Unit price [" + item.getUnitPrice() + "]: ");
            if (!price.isEmpty()) item.setUnitPrice(Double.parseDouble(price));

            if (item instanceof PerishableItem) {
                PerishableItem p = (PerishableItem) item;
                String date = readOptional("Expiry date [" + p.getExpiryDate() + "]: ");
                if (!date.isEmpty()) p.setExpiryDate(LocalDate.parse(date));
            }
            markChanged();
            System.out.println("Item updated.");
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid number. Remaining fields were not changed.");
        } catch (DateTimeParseException e) {
            System.out.println("Error: invalid date. Use yyyy-mm-dd.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteItem() {
        Item item = promptForItem();
        if (item == null) return;
        if (readYesNo("Delete " + item.getName() + " (" + item.getId() + ")? (y/n): ")) {
            inventory.removeItem(item.getId());
            markChanged();
            System.out.println("Item deleted.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private void showReports() {
        System.out.println("--- Reports ---");
        System.out.println("Number of items     : " + inventory.size());
        System.out.printf ("Total inventory value: %,.2f%n", inventory.getTotalValue());
        System.out.println("Expired items        : " + inventory.getExpiredItems().size());

        String input = readOptional("Low-stock threshold [" + DEFAULT_LOW_STOCK + "]: ");
        int threshold = DEFAULT_LOW_STOCK;
        if (!input.isEmpty()) {
            try {
                threshold = Math.max(0, Integer.parseInt(input));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Using " + DEFAULT_LOW_STOCK + ".");
            }
        }
        listItems(inventory.getLowStock(threshold), "LOW STOCK (quantity <= " + threshold + ")");
    }

    private boolean save() {
        try {
            repository.save(inventory);
            unsavedChanges = false;
            System.out.println("Saved " + inventory.size() + " item(s) to " + SAVE_FILE + ".");
            return true;
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
            return false;
        }
    }

    private boolean confirmExit() {
        if (!unsavedChanges) return true;
        if (readYesNo("You have unsaved changes. Save before exiting? (y/n): ")) {
            return save() || readYesNo("Save failed. Exit anyway? (y/n): ");
        }
        return true;
    }

    private void listItems(Collection<? extends Item> list, String title) {
        System.out.println("--- " + title + " ---");
        if (list.isEmpty()) {
            System.out.println("No items found.");
            return;
        }
        System.out.printf("%-9s %-10s %-20s %-14s %6s %12s %14s  %s%n",
                "ID", "TYPE", "NAME", "CATEGORY", "QTY", "UNIT PRICE", "TOTAL VALUE", "DETAILS");
        for (Item item : list) {
            System.out.println(item);
        }
        System.out.println(list.size() + " item(s).");
    }

    private Item promptForItem() {
        if (inventory.isEmpty()) {
            System.out.println("The inventory is empty.");
            return null;
        }
        String id = readText("Item ID (e.g. ITM-0001): ");
        Item item = inventory.findById(id);
        if (item == null) {
            System.out.println("No item found with ID " + id.toUpperCase() + ".");
        }
        return item;
    }

    private void markChanged() {
        unsavedChanges = true;
    }

    private String readOptional(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private String readText(String prompt) {
        while (true) {
            String value = readOptional(prompt);
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readOptional(prompt);
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) return number;
                System.out.println("Enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            String value = readOptional(prompt);
            try {
                double number = Double.parseDouble(value);
                if (number >= 0) return number;
                System.out.println("Value cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String value = readOptional(prompt);
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use the format yyyy-mm-dd (e.g. 2026-12-31).");
            }
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            String value = readOptional(prompt).toLowerCase();
            if (value.equals("y") || value.equals("yes")) return true;
            if (value.equals("n") || value.equals("no")) return false;
            System.out.println("Please enter y or n.");
        }
    }
}
