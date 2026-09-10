import java.util.ArrayList;
import java.util.Scanner;

public class ContactManagementSystem {

    private static ArrayList<Contact> contacts = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addContact();
                    break;
                case 2:
                    viewAllContacts();
                    break;
                case 3:
                    searchContact();
                    break;
                case 4:
                    updateContact();
                    break;
                case 5:
                    deleteContact();
                    break;
                case 6:
                    running = false;
                    System.out.println("Exiting Contact Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-6.");
            }
            System.out.println();
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("===== CONTACT MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Contact");
        System.out.println("2. View All Contacts");
        System.out.println("3. Search Contact");
        System.out.println("4. Update Contact");
        System.out.println("5. Delete Contact");
        System.out.println("6. Exit");
        System.out.println("======================================");
    }
 
    private static void addContact() {
        System.out.println("--- Add New Contact ---");
        String name = readLine("Name: ");
        String phone = readLine("Phone Number: ");
        String email = readLine("Email: ");
        String address = readLine("Address: ");

        Contact contact = new Contact(name, phone, email, address);
        contacts.add(contact);

        System.out.println("Contact added successfully! (Assigned ID: " + contact.getId() + ")");
    }

    private static void viewAllContacts() {
        System.out.println("--- All Contacts ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
            return;
        }
        for (Contact c : contacts) {
            System.out.println(c);
        }
        System.out.println("Total contacts: " + contacts.size());
    }

    private static void searchContact() {
        System.out.println("--- Search Contact ---");
        String query = readLine("Enter Contact ID or Name to search: ");

        boolean found = false;
        for (Contact c : contacts) {
            boolean idMatch = query.matches("\\d+") && c.getId() == Integer.parseInt(query);
            boolean nameMatch = c.getName().toLowerCase().contains(query.toLowerCase());

            if (idMatch || nameMatch) {
                System.out.println(c);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching contact found.");
        }
    }

    private static void updateContact() {
        System.out.println("--- Update Contact ---");
        int id = readInt("Enter Contact ID to update: ");
        Contact contact = findById(id);

        if (contact == null) {
            System.out.println("Contact with ID " + id + " not found.");
            return;
        }

        System.out.println("Current details: " + contact);
        System.out.println("Leave a field blank to keep its current value.");

        String name = readLine("New Name: ");
        if (!name.isBlank()) contact.setName(name);

        String phone = readLine("New Phone Number: ");
        if (!phone.isBlank()) contact.setPhoneNumber(phone);

        String email = readLine("New Email: ");
        if (!email.isBlank()) contact.setEmail(email);

        String address = readLine("New Address: ");
        if (!address.isBlank()) contact.setAddress(address);

        System.out.println("Contact updated successfully!");
        System.out.println("Updated details: " + contact);
    }

    private static void deleteContact() {
        System.out.println("--- Delete Contact ---");
        int id = readInt("Enter Contact ID to delete: ");
        Contact contact = findById(id);

        if (contact == null) {
            System.out.println("Contact with ID " + id + " not found.");
            return;
        }

        contacts.remove(contact);
        System.out.println("Contact deleted successfully.");
    }

    private static Contact findById(int id) {
        for (Contact c : contacts) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
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
