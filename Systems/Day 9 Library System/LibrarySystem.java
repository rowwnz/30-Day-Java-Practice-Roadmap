import java.util.Scanner;


public class LibrarySystem {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        //Library data stored in parallel arrays 
        int[] bookIds = { 101, 102, 103, 104, 105, 106, 107, 108 };

        String[] titles = {
            "Java Programming Basics",
            "Data Structures and Algorithms",
            "Introduction to Computing",
            "Database Management Systems",
            "Computer Networks",
            "Operating System Concepts",
            "Web Development Essentials",
            "Software Engineering Principles"
        };

        String[] authors = {
            "James Gosling",
            "Robert Lafore",
            "Peter Norton",
            "Ramez Elmasri",
            "Andrew Tanenbaum",
            "Abraham Silberschatz",
            "Jon Duckett",
            "Ian Sommerville"
        };

        int[] copies = { 3, 2, 5, 1, 4, 2, 3, 0 };

        int totalBooks = bookIds.length;
        int choice = 0;

        //Main menu loop
        do {
            System.out.println();
            System.out.println("========================================");
            System.out.println("           LIBRARY SYSTEM");
            System.out.println("========================================");
            System.out.println("[1] Display all books");
            System.out.println("[2] Search by title       (linear search)");
            System.out.println("[3] Search by author      (linear search)");
            System.out.println("[4] Search by book ID     (binary search)");
            System.out.println("[5] Borrow a book");
            System.out.println("[6] Return a book");
            System.out.println("[0] Exit");
            System.out.println("========================================");
            System.out.print("Enter choice: ");

            // Read the choice as text so letters do not crash the program
            String entry = input.nextLine().trim();
            if (entry.length() == 1 && entry.charAt(0) >= '0' && entry.charAt(0) <= '6') {
                choice = entry.charAt(0) - '0';
            } else {
                System.out.println("Invalid choice. Please enter a number from 0 to 6.");
                continue;
            }

            //[1] DISPLAY ALL BOOKS 
            if (choice == 1) {
                System.out.println();
                System.out.println("ID    TITLE                             AUTHOR                  COPIES");
                System.out.println("---------------------------------------------------------------------");

                for (int i = 0; i < totalBooks; i++) {
                    System.out.printf("%-6d%-34s%-24s%d%n",
                            bookIds[i], titles[i], authors[i], copies[i]);
                }
            }

            // [2] SEARCH BY TITLE (LINEAR) 

            else if (choice == 2) {
                System.out.print("Enter title keyword: ");
                String keyword = input.nextLine().trim().toLowerCase();

                int found = 0;

                // Linear search: check every element from first to last
                for (int i = 0; i < totalBooks; i++) {
                    if (titles[i].toLowerCase().contains(keyword)) {
                        System.out.println();
                        System.out.println("ID     : " + bookIds[i]);
                        System.out.println("Title  : " + titles[i]);
                        System.out.println("Author : " + authors[i]);
                        System.out.println("Copies : " + copies[i]);
                        found++;
                    }
                }

                if (found == 0) {
                    System.out.println("No book found with that title keyword.");
                } else {
                    System.out.println();
                    System.out.println("Matches found: " + found);
                }
            }

            // [3] SEARCH BY AUTHOR (LINEAR) 

            else if (choice == 3) {
                System.out.print("Enter author keyword: ");
                String keyword = input.nextLine().trim().toLowerCase();

                int found = 0;

                for (int i = 0; i < totalBooks; i++) {
                    if (authors[i].toLowerCase().contains(keyword)) {
                        System.out.println();
                        System.out.println("ID     : " + bookIds[i]);
                        System.out.println("Title  : " + titles[i]);
                        System.out.println("Author : " + authors[i]);
                        System.out.println("Copies : " + copies[i]);
                        found++;
                    }
                }

                if (found == 0) {
                    System.out.println("No book found by that author.");
                } else {
                    System.out.println();
                    System.out.println("Matches found: " + found);
                }
            }

            //[4] SEARCH BY ID (BINARY)

            else if (choice == 4) {
                System.out.print("Enter book ID: ");
                String idText = input.nextLine().trim();
                int targetId = -1;

                // Simple check that every character is a digit
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

                targetId = Integer.parseInt(idText);

                // Binary search on the sorted bookIds array
                int low = 0;
                int high = totalBooks - 1;
                int position = -1;

                while (low <= high) {
                    int mid = (low + high) / 2;

                    if (bookIds[mid] == targetId) {
                        position = mid;
                        break;
                    } else if (bookIds[mid] < targetId) {
                        low = mid + 1;      // search the right half
                    } else {
                        high = mid - 1;     // search the left half
                    }
                }

                if (position == -1) {
                    System.out.println("Book ID " + targetId + " not found.");
                } else {
                    System.out.println();
                    System.out.println("ID     : " + bookIds[position]);
                    System.out.println("Title  : " + titles[position]);
                    System.out.println("Author : " + authors[position]);
                    System.out.println("Copies : " + copies[position]);
                }
            }

            // [5] BORROW / [6] RETURN 

            else if (choice == 5 || choice == 6) {
                System.out.print("Enter book ID: ");
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

                // Linear search to locate the index of the book
                int position = -1;
                for (int i = 0; i < totalBooks; i++) {
                    if (bookIds[i] == targetId) {
                        position = i;
                        break;
                    }
                }

                if (position == -1) {
                    System.out.println("Book ID " + targetId + " not found.");
                } else if (choice == 5) {
                    if (copies[position] > 0) {
                        copies[position]--;
                        System.out.println("Borrowed: " + titles[position]);
                        System.out.println("Copies left: " + copies[position]);
                    } else {
                        System.out.println("Sorry, no copies left of " + titles[position] + ".");
                    }
                } else {
                    copies[position]++;
                    System.out.println("Returned: " + titles[position]);
                    System.out.println("Copies available: " + copies[position]);
                }
            }

            //[0] EXIT 
            
            else {
                System.out.println("Thank you for using the Library System.");
            }

        } while (choice != 0);

        input.close();
    }
}
