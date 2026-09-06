import java.util.Scanner;

public class InventorySystem {

    static final int MAX_ITEMS = 100;
    static Item[] items = new Item[MAX_ITEMS];
    static int itemCount = 0; // number of slots currently in use

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        do {
            printMenu();
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> addItem();       // Create
                case 2 -> viewAllItems();  // Read (all)
                case 3 -> searchItem();    // Read (one)
                case 4 -> updateItem();    // Update
                case 5 -> deleteItem();    // Delete
                case 6 -> System.out.println("Exiting program. Goodbye!");
                default -> System.out.println("Invalid choice. Please select 1-6.");
            }
            System.out.println();

        } while (choice != 6);

        sc.close();
    }


    // Menu

    static void printMenu() {
        System.out.println("===== INVENTORY SYSTEM =====");
        System.out.println("1. Add Item (Create)");
        System.out.println("2. View All Items (Read)");
        System.out.println("3. Search Item by ID (Read)");
        System.out.println("4. Update Item (Update)");
        System.out.println("5. Delete Item (Delete)");
        System.out.println("6. Exit");
        System.out.println("=============================");
    }

    // CREATE

    static void addItem() {
        if (itemCount >= MAX_ITEMS) {
            System.out.println("Inventory is full. Cannot add more items.");
            return;
        }

        int id = readInt("Enter Item ID: ");

        if (findIndexById(id) != -1) {
            System.out.println("An item with ID " + id + " already exists.");
            return;
        }

        System.out.print("Enter Item Name: ");
        String name = sc.nextLine();

        int quantity = readInt("Enter Quantity: ");
        double price = readDouble("Enter Price: ");

        items[itemCount] = new Item(id, name, quantity, price);
        itemCount++;

        System.out.println("Item added successfully.");
    }


    // READ (all)

    static void viewAllItems() {
        if (itemCount == 0) {
            System.out.println("Inventory is empty.");
            return;
        }

        System.out.printf("%-6s %-20s %-10s %-10s%n", "ID", "Name", "Quantity", "Price");
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < itemCount; i++) {
            Item it = items[i];
            System.out.printf("%-6d %-20s %-10d %-10.2f%n",
                    it.id, it.name, it.quantity, it.price);
        }
    }


    // READ (single item)

    static void searchItem() {
        int id = readInt("Enter Item ID to search: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Item with ID " + id + " not found.");
            return;
        }

        Item it = items[index];
        System.out.println("Item Found:");
        System.out.println("ID       : " + it.id);
        System.out.println("Name     : " + it.name);
        System.out.println("Quantity : " + it.quantity);
        System.out.println("Price    : " + it.price);
    }

    // UPDATE

    static void updateItem() {
        int id = readInt("Enter Item ID to update: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Item with ID " + id + " not found.");
            return;
        }

        Item it = items[index];
        System.out.println("Leave a field blank to keep its current value.");

        System.out.print("Enter new Name (" + it.name + "): ");
        String name = sc.nextLine();
        if (!name.isBlank()) {
            it.name = name;
        }

        System.out.print("Enter new Quantity (" + it.quantity + "): ");
        String qtyInput = sc.nextLine();
        if (!qtyInput.isBlank()) {
            try {
                it.quantity = Integer.parseInt(qtyInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity entered. Quantity not changed.");
            }
        }

        System.out.print("Enter new Price (" + it.price + "): ");
        String priceInput = sc.nextLine();
        if (!priceInput.isBlank()) {
            try {
                it.price = Double.parseDouble(priceInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid price entered. Price not changed.");
            }
        }

        System.out.println("Item updated successfully.");
    }

    // DELETE

    static void deleteItem() {
        int id = readInt("Enter Item ID to delete: ");
        int index = findIndexById(id);

        if (index == -1) {
            System.out.println("Item with ID " + id + " not found.");
            return;
        }

        for (int i = index; i < itemCount - 1; i++) {
            items[i] = items[i + 1];
        }
        items[itemCount - 1] = null;
        itemCount--;

        System.out.println("Item deleted successfully.");
    }


    // Helper methods
  
    static int findIndexById(int id) {
        for (int i = 0; i < itemCount; i++) {
            if (items[i].id == id) {
                return i;
            }
        }
        return -1;
    }

    /** Reads an integer from the console, re-prompting on invalid input. */
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    /** Reads a double from the console, re-prompting on invalid input. */
    static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}


//Represents a single inventory item.

class Item {
    int id;
    String name;
    int quantity;
    double price;

    Item(int id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}
