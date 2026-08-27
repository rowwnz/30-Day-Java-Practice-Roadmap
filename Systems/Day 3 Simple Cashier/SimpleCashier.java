import java.util.Scanner;

public class SimpleCashier {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String peso = "\u20B1";

        // Item information
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();

        System.out.print("Enter item price: " + peso);
        double price = scanner.nextDouble();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        // Calculate total
        double total = price * quantity;

        // Display receipt
        System.out.println("\n----- RECEIPT -----");
        System.out.println("Item: " + itemName);
        System.out.println("Price: " + peso + price);
        System.out.println("Quantity: " + quantity);
        System.out.println("Total: " + peso + total);

        // Payment
        System.out.print("\nEnter payment: " + peso);
        double payment = scanner.nextDouble();

        // Check payment
        if (payment >= total) {

            double change = payment - total;

            System.out.println("\nPayment accepted!");
            System.out.println("Change: " + peso + change);
            System.out.println("Thank you for your purchase!");

        } else {

            double shortage = total - payment;

            System.out.println("\nInsufficient payment!");
            System.out.println("You still need: " + peso + shortage);
        }

        scanner.close();
    }
}