import java.util.Scanner;

public class ShoppingCart {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        //Product catalog stored in parallel arrays 
        int[] productIds = { 1, 2, 3, 4, 5, 6, 7, 8 };

        String[] productNames = {
            "Ballpen (black)",
            "Notebook (80 leaves)",
            "USB Flash Drive 32GB",
            "Scientific Calculator",
            "Bond Paper (short, ream)",
            "Correction Tape",
            "Mechanical Pencil",
            "Backpack"
        };

        double[] prices = { 15.00, 45.00, 350.00, 850.00, 220.00, 35.00, 60.00, 1250.00 };

        int totalProducts = productIds.length;

        int[] cartQty = new int[totalProducts];   

        int choice = 0;

        //Main menu loop 
        do {
            System.out.println();
            System.out.println("========================================");
            System.out.println("         SHOPPING CART SYSTEM");
            System.out.println("========================================");
            System.out.println("[1] View products");
            System.out.println("[2] Add item to cart");
            System.out.println("[3] Remove item from cart");
            System.out.println("[4] View cart and total");
            System.out.println("[5] Checkout");
            System.out.println("[0] Exit");
            System.out.println("========================================");
            System.out.print("Enter choice: ");

            // Read the choice as text so letters do not crash the program
            String entry = input.nextLine().trim();
            if (entry.length() == 1 && entry.charAt(0) >= '0' && entry.charAt(0) <= '5') {
                choice = entry.charAt(0) - '0';
            } else {
                System.out.println("Invalid choice. Please enter a number from 0 to 5.");
                continue;
            }

            //[1] VIEW PRODUCTS 
            if (choice == 1) {
                System.out.println();
                System.out.println("ID    PRODUCT                          PRICE");
                System.out.println("-------------------------------------------------");

                for (int i = 0; i < totalProducts; i++) {
                    System.out.printf("%-6d%-33s%10.2f%n",
                            productIds[i], productNames[i], prices[i]);
                }
            }

            //[2] ADD ITEM
            else if (choice == 2) {
                System.out.print("Enter product ID: ");
                String idText = input.nextLine().trim();

                // Check that every character is a digit
                boolean isNumber = idText.length() > 0;
                for (int i = 0; i < idText.length(); i++) {
                    if (idText.charAt(i) < '0' || idText.charAt(i) > '9') {
                        isNumber = false;
                    }
                }

                if (!isNumber) {
                    System.out.println("Invalid ID. Numbers only.");
                    continue;
                }

                int targetId = Integer.parseInt(idText);

                // Search the catalog for the matching product
                int position = -1;
                for (int i = 0; i < totalProducts; i++) {
                    if (productIds[i] == targetId) {
                        position = i;
                        break;
                    }
                }

                if (position == -1) {
                    System.out.println("Product ID " + targetId + " not found.");
                    continue;
                }

                System.out.print("Enter quantity: ");
                String qtyText = input.nextLine().trim();

                boolean isQty = qtyText.length() > 0;
                for (int i = 0; i < qtyText.length(); i++) {
                    if (qtyText.charAt(i) < '0' || qtyText.charAt(i) > '9') {
                        isQty = false;
                    }
                }

                if (!isQty) {
                    System.out.println("Invalid quantity. Numbers only.");
                    continue;
                }

                int quantity = Integer.parseInt(qtyText);

                if (quantity <= 0) {
                    System.out.println("Quantity must be at least 1.");
                } else {
                    cartQty[position] = cartQty[position] + quantity;
                    System.out.printf("Added: %d x %s = %.2f%n",
                            quantity, productNames[position], quantity * prices[position]);
                    System.out.println("In cart now: " + cartQty[position]);
                }
            }

            //[3] REMOVE ITEM 
            else if (choice == 3) {
                System.out.print("Enter product ID to remove: ");
                String idText = input.nextLine().trim();

                boolean isNumber = idText.length() > 0;
                for (int i = 0; i < idText.length(); i++) {
                    if (idText.charAt(i) < '0' || idText.charAt(i) > '9') {
                        isNumber = false;
                    }
                }

                if (!isNumber) {
                    System.out.println("Invalid ID. Numbers only.");
                    continue;
                }

                int targetId = Integer.parseInt(idText);

                int position = -1;
                for (int i = 0; i < totalProducts; i++) {
                    if (productIds[i] == targetId) {
                        position = i;
                        break;
                    }
                }

                if (position == -1) {
                    System.out.println("Product ID " + targetId + " not found.");
                } else if (cartQty[position] == 0) {
                    System.out.println(productNames[position] + " is not in your cart.");
                } else {
                    System.out.println("Removed all " + cartQty[position] + " of "
                            + productNames[position] + ".");
                    cartQty[position] = 0;
                }
            }

            //[4] VIEW CART AND TOTAL 
            else if (choice == 4) {
                double grandTotal = 0;
                int itemCount = 0;

                System.out.println();
                System.out.println("QTY   PRODUCT                          PRICE   SUBTOTAL");
                System.out.println("--------------------------------------------------------");

                // Loop through the cart, printing each line and adding to the total
                for (int i = 0; i < totalProducts; i++) {
                    if (cartQty[i] > 0) {
                        double subtotal = cartQty[i] * prices[i];

                        System.out.printf("%-6d%-33s%7.2f%10.2f%n",
                                cartQty[i], productNames[i], prices[i], subtotal);

                        grandTotal = grandTotal + subtotal;
                        itemCount = itemCount + cartQty[i];
                    }
                }

                if (itemCount == 0) {
                    System.out.println("Your cart is empty.");
                } else {
                    System.out.println("--------------------------------------------------------");
                    System.out.println("Total items : " + itemCount);
                    System.out.printf("GRAND TOTAL : %.2f%n", grandTotal);
                }
            }

            //[5] CHECKOUT
            else if (choice == 5) {
                double grandTotal = 0;
                int itemCount = 0;

                // Compute the total again before billing
                for (int i = 0; i < totalProducts; i++) {
                    if (cartQty[i] > 0) {
                        grandTotal = grandTotal + (cartQty[i] * prices[i]);
                        itemCount = itemCount + cartQty[i];
                    }
                }

                if (itemCount == 0) {
                    System.out.println("Your cart is empty. Nothing to check out.");
                    continue;
                }

                System.out.println();
                System.out.println("================ RECEIPT ================");

                for (int i = 0; i < totalProducts; i++) {
                    if (cartQty[i] > 0) {
                        System.out.printf("%-6d%-24s%10.2f%n",
                                cartQty[i], productNames[i], cartQty[i] * prices[i]);
                    }
                }

                System.out.println("-----------------------------------------");
                System.out.printf("Total items    : %d%n", itemCount);
                System.out.printf("Amount due     : %.2f%n", grandTotal);

                // Payment loop: keep asking until the cash is enough
                double payment = 0;
                boolean paid = false;

                while (!paid) {
                    System.out.print("Cash payment   : ");
                    String cashText = input.nextLine().trim();

                    boolean isCash = cashText.length() > 0;
                    int dotCount = 0;
                    for (int i = 0; i < cashText.length(); i++) {
                        char c = cashText.charAt(i);
                        if (c == '.') {
                            dotCount++;
                        } else if (c < '0' || c > '9') {
                            isCash = false;
                        }
                    }
                    if (dotCount > 1) {
                        isCash = false;
                    }

                    if (!isCash) {
                        System.out.println("Invalid amount. Numbers only.");
                    } else {
                        payment = Double.parseDouble(cashText);

                        if (payment < grandTotal) {
                            System.out.printf("Not enough. Short by %.2f%n", grandTotal - payment);
                        } else {
                            paid = true;
                        }
                    }
                }

                System.out.printf("Change         : %.2f%n", payment - grandTotal);
                System.out.println("=========================================");
                System.out.println("Thank you for shopping!");

                // Empty the cart after a successful checkout
                for (int i = 0; i < totalProducts; i++) {
                    cartQty[i] = 0;
                }
            }

            //[0] EXIT 
            else {
                System.out.println("Exiting. Goodbye!");
            }

        } while (choice != 0);

        input.close();
    }
}
