import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;


class Guest {
    private String name;
    private String contactNumber;

    public Guest(String name, String contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    @Override
    public String toString() {
        return name + " (Contact: " + contactNumber + ")";
    }
}

class Room {
    private int roomNumber;
    private String roomType;
    private double pricePerNight;
    private boolean available;

    public Room(int roomNumber, String roomType, double pricePerNight, boolean available) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.available = available;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format("Room %-4d | %-10s | PHP %8.2f / night | %s",
                roomNumber, roomType, pricePerNight, available ? "Available" : "Occupied");
    }
}


class Reservation {
    private static int nextId = 1;

    private final int reservationId;
    private Guest guest;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int nights;
    private double totalCost;

    public Reservation(Guest guest, Room room, LocalDate checkIn, LocalDate checkOut, int nights) {
        this.reservationId = nextId++;
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.totalCost = nights * room.getPricePerNight();
    }

    public int getReservationId() {
        return reservationId;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getNights() {
        return nights;
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format(
                "Reservation #%d%n  Guest      : %s%n  Room       : %d (%s)%n  Check-in   : %s%n  Check-out  : %s%n  Nights     : %d%n  Total Cost : PHP %.2f",
                reservationId, guest.toString(), room.getRoomNumber(), room.getRoomType(),
                checkIn.format(fmt), checkOut.format(fmt), nights, totalCost);
    }
}


public class HotelReservationSystem {

    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public HotelReservationSystem() {
        initializeRooms();
    }

    private void initializeRooms() {
        rooms.add(new Room(101, "Standard", 1500.00, true));
        rooms.add(new Room(102, "Standard", 1500.00, true));
        rooms.add(new Room(103, "Standard", 1500.00, true));
        rooms.add(new Room(201, "Deluxe", 2500.00, true));
        rooms.add(new Room(202, "Deluxe", 2500.00, true));
        rooms.add(new Room(301, "Suite", 4500.00, true));
        rooms.add(new Room(302, "Suite", 4500.00, true));
    }

    public void run() {
        boolean running = true;
        System.out.println("=========================================");
        System.out.println("     WELCOME TO THE HOTEL RESERVATION     ");
        System.out.println("               MANAGEMENT SYSTEM          ");
        System.out.println("=========================================");

        while (running) {
            printMenu();
            int choice = readIntInRange("Enter choice: ", 1, 6);
            switch (choice) {
                case 1:
                    viewAvailableRooms();
                    break;
                case 2:
                    makeReservation();
                    break;
                case 3:
                    cancelReservation();
                    break;
                case 4:
                    viewAllReservations();
                    break;
                case 5:
                    searchByGuestName();
                    break;
                case 6:
                    running = false;
                    System.out.println("\nThank you for using the Hotel Reservation System. Goodbye!");
                    break;
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("1. View Available Rooms");
        System.out.println("2. Make a Reservation");
        System.out.println("3. Cancel a Reservation");
        System.out.println("4. View All Reservations");
        System.out.println("5. Search Reservation by Guest Name");
        System.out.println("6. Exit");
        System.out.println("----------------------------------------------");
    }

  

    private void viewAvailableRooms() {
        System.out.println("\n--- Available Rooms ---");
        boolean anyAvailable = false;
        for (Room room : rooms) {
            if (room.isAvailable()) {
                System.out.println(room);
                anyAvailable = true;
            }
        }
        if (!anyAvailable) {
            System.out.println("No rooms are currently available.");
        }
    }

    private void makeReservation() {
        viewAvailableRooms();

        boolean hasAvailable = false;
        for (Room room : rooms) {
            if (room.isAvailable()) {
                hasAvailable = true;
                break;
            }
        }
        if (!hasAvailable) {
            System.out.println("Cannot make a reservation: no rooms are available.");
            return;
        }

        Room selectedRoom;
        while (true) {
            int roomNumber = readPositiveInt("\nEnter room number to book: ");
            Room found = findRoomByNumber(roomNumber);
            if (found == null) {
                System.out.println("Room " + roomNumber + " does not exist. Please try again.");
                continue;
            }
            if (!found.isAvailable()) {
                System.out.println("Room " + roomNumber + " is not available. Please choose another.");
                continue;
            }
            selectedRoom = found;
            break;
        }

        String name = readValidName("Enter guest name: ");
        String contact = readValidContact("Enter contact number (7-15 digits): ");
        Guest guest = new Guest(name, contact);

        LocalDate checkIn = readValidDate("Enter check-in date (yyyy-MM-dd): ");
        int nights = readPositiveInt("Enter number of nights: ");
        LocalDate checkOut = checkIn.plusDays(nights);

        Reservation reservation = new Reservation(guest, selectedRoom, checkIn, checkOut, nights);
        reservations.add(reservation);
        selectedRoom.setAvailable(false);

        System.out.println("\nReservation confirmed!");
        System.out.println(reservation);
    }

    private void cancelReservation() {
        if (reservations.isEmpty()) {
            System.out.println("\nThere are no reservations to cancel.");
            return;
        }

        viewAllReservations();
        int id = readPositiveInt("\nEnter reservation ID to cancel: ");

        Reservation target = null;
        for (Reservation r : reservations) {
            if (r.getReservationId() == id) {
                target = r;
                break;
            }
        }

        if (target == null) {
            System.out.println("No reservation found with ID " + id + ".");
            return;
        }

        target.getRoom().setAvailable(true);
        reservations.remove(target);
        System.out.println("Reservation #" + id + " has been cancelled. Room "
                + target.getRoom().getRoomNumber() + " is now available.");
    }

    private void viewAllReservations() {
        System.out.println("\n--- All Reservations ---");
        if (reservations.isEmpty()) {
            System.out.println("No reservations on record.");
            return;
        }
        for (Reservation r : reservations) {
            System.out.println(r);
            System.out.println("-----------------------------------------");
        }
    }

    private void searchByGuestName() {
        String name = readNonEmptyString("\nEnter guest name to search: ");
        boolean found = false;
        for (Reservation r : reservations) {
            if (r.getGuest().getName().equalsIgnoreCase(name)) {
                System.out.println(r);
                System.out.println("-----------------------------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No reservations found for guest \"" + name + "\".");
        }
    }



    private Room findRoomByNumber(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }



    private int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value <= 0) {
                    System.out.println("Value must be greater than zero.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("This field cannot be empty.");
                continue;
            }
            return input;
        }
    }

    private String readValidName(String prompt) {
        while (true) {
            String input = readNonEmptyString(prompt);
            if (!input.matches("[A-Za-z .'-]+")) {
                System.out.println("Name may only contain letters, spaces, apostrophes, and hyphens.");
                continue;
            }
            return input;
        }
    }

    private String readValidContact(String prompt) {
        while (true) {
            String input = readNonEmptyString(prompt);
            if (!input.matches("\\d{7,15}")) {
                System.out.println("Contact number must contain 7 to 15 digits only.");
                continue;
            }
            return input;
        }
    }

    private LocalDate readValidDate(String prompt) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(input, fmt);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Check-in date cannot be in the past.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd (e.g., 2026-09-15).");
            }
        }
    }

    public static void main(String[] args) {
        HotelReservationSystem system = new HotelReservationSystem();
        system.run();
    }
}
