# Inventory System v2

A console-based inventory management program written in Java. It stores items in a text file, so your data is kept between runs.

## Requirements

- Java JDK 8 or later (`java -version` to check)

## Files

| File | Purpose |
|---|---|
| `InventoryApp.java` | Main program (menu and user input) |
| `Item.java` | Abstract base class for all items |
| `StandardItem.java` | Regular, non-perishable item |
| `PerishableItem.java` | Item with an expiry date |
| `Inventory.java` | Stores and manages the items |
| `InventoryRepository.java` | Interface for saving and loading |
| `FileInventoryRepository.java` | Saves and loads items to `inventory.txt` |

## How to Run

1. Put all 7 `.java` files in the same folder.
2. Open a terminal or command prompt in that folder.
3. Compile:
   ```
   javac *.java
   ```
4. Run:
   ```
   java InventoryApp
   ```

On the first run, the inventory is empty. A file named `inventory.txt` is created in the same folder the first time you save.

## Main Menu

```
========== INVENTORY SYSTEM v2 ==========
 1. Add item
 2. View all items
 3. Search items
 4. Restock item (add quantity)
 5. Sell / issue item (deduct quantity)
 6. Edit item details
 7. Delete item
 8. Reports (total value, low stock)
 9. Save now
10. View expired items
 0. Exit
```

Type the number of an option and press **Enter**. If there are changes that have not been saved, `* unsaved changes` appears under the menu.

## Operations

### 1. Add Item
1. Choose the item type:
   - `1` = Standard item (e.g., ballpen, stapler)
   - `2` = Perishable item (e.g., milk, medicine)
2. Enter the name, category, quantity, and unit price.
3. For a perishable item, enter the expiry date in `yyyy-mm-dd` format (e.g., `2026-12-31`).

The system assigns an ID automatically (`ITM-0001`, `ITM-0002`, and so on).

### 2. View All Items
Shows every item in a table with its ID, type, name, category, quantity, unit price, total value, and expiry details.

### 3. Search Items
Enter a keyword. Items whose **name or category** contains that keyword are listed. The search is not case-sensitive.

### 4. Restock Item
1. Enter the item ID (e.g., `ITM-0001`; lowercase also works).
2. Enter the quantity to add.

### 5. Sell / Issue Item
1. Enter the item ID.
2. Enter the quantity to deduct.

The program shows the amount (quantity × unit price) and the remaining stock. You cannot deduct more than the available stock. A warning appears if the item is expired.

### 6. Edit Item Details
1. Enter the item ID.
2. For each field, type a new value, or press **Enter** to keep the current value shown in brackets.

### 7. Delete Item
1. Enter the item ID.
2. Confirm with `y` (yes) or `n` (no).

### 8. Reports
Shows:
- Number of items
- Total inventory value
- Number of expired items
- Low-stock list: enter a threshold, or press **Enter** to use the default of `5`. Items with a quantity at or below the threshold are listed.

### 9. Save Now
Saves all items to `inventory.txt`.

### 10. View Expired Items
Lists perishable items whose expiry date has already passed.

### 0. Exit
If there are unsaved changes, the program asks whether to save before exiting:
- `y` = save, then exit
- `n` = exit without saving

## Input Rules

| Field | Rule |
|---|---|
| Name / Category | Cannot be empty or contain the `\|` character |
| Quantity | Whole number, 0 or greater |
| Unit price | Number, 0 or greater (e.g., `12.50`) |
| Expiry date | Format `yyyy-mm-dd` |
| Yes/No prompts | `y`, `yes`, `n`, or `no` |

If an input is invalid, the program shows an error and asks again.

## Save File

Data is stored in `inventory.txt`, one item per line:

```
# TYPE|ID|NAME|CATEGORY|QUANTITY|UNIT_PRICE|EXTRA
STANDARD|ITM-0001|Ballpen|Supplies|3|12.5|-
PERISHABLE|ITM-0002|Milk|Dairy|6|95.0|2026-11-01
```

- The file is loaded automatically when the program starts.
- Lines that are damaged or invalid are skipped with a warning.
- To start with an empty inventory, delete or rename `inventory.txt`.
- Always run the program from the same folder, so it finds the same `inventory.txt`.
