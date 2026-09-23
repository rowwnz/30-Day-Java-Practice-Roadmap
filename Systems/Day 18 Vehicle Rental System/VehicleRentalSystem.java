import java.util.Scanner;

class Vehicle {
    protected String plateNumber;
    protected String brand;
    protected String model;
    protected double dailyRate;
    protected boolean isRented;

    public Vehicle(String plateNumber, String brand, String model, double dailyRate) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.isRented = false;
    }

    public double calculateRentalCost(int days) {
        return dailyRate * days;
    }

    public String getType() {
        return "Vehicle";
    }

    public void displayInfo() {
        System.out.printf("%-12s %-10s %-10s %-12s P%,10.2f/day  %s%n",
                getType(), plateNumber, brand, model, dailyRate,
                isRented ? "[RENTED]" : "[AVAILABLE]");
    }
}

class Car extends Vehicle {
    private int seats;

    public Car(String plateNumber, String brand, String model, double dailyRate, int seats) {
        super(plateNumber, brand, model, dailyRate);
        this.seats = seats;
    }

    @Override
    public String getType() {
        return "Car";
    }

    @Override
    public double calculateRentalCost(int days) {
        double cost = super.calculateRentalCost(days);
        if (seats > 5) {
            cost += 500 * days;
        }
        return cost;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("             Seats: " + seats);
    }
}

class Motorcycle extends Vehicle {
    private int engineCC;

    public Motorcycle(String plateNumber, String brand, String model, double dailyRate, int engineCC) {
        super(plateNumber, brand, model, dailyRate);
        this.engineCC = engineCC;
    }

    @Override
    public String getType() {
        return "Motorcycle";
    }

    @Override
    public double calculateRentalCost(int days) {
        double cost = super.calculateRentalCost(days);
        if (days >= 7) {
            cost *= 0.90;
        }
        return cost;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("             Engine: " + engineCC + "cc");
    }
}

class Truck extends Vehicle {
    private double loadCapacityTons;

    public Truck(String plateNumber, String brand, String model, double dailyRate, double loadCapacityTons) {
        super(plateNumber, brand, model, dailyRate);
        this.loadCapacityTons = loadCapacityTons;
    }

    @Override
    public String getType() {
        return "Truck";
    }

    @Override
    public double calculateRentalCost(int days) {
        return super.calculateRentalCost(days) + (loadCapacityTons * 1000);
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("             Load Capacity: " + loadCapacityTons + " tons");
    }
}

public class VehicleRentalSystem {
    static Vehicle[] fleet = {
        new Car("ABC-1234", "Toyota", "Vios", 1500, 5),
        new Car("DEF-5678", "Toyota", "Innova", 2500, 7),
        new Motorcycle("MC-1111", "Honda", "Click", 500, 125),
        new Motorcycle("MC-2222", "Yamaha", "NMAX", 700, 155),
        new Truck("TRK-9999", "Isuzu", "Elf", 4000, 3)
    };

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n===== VEHICLE RENTAL SYSTEM =====");
            System.out.println("[1] View All Vehicles");
            System.out.println("[2] Rent a Vehicle");
            System.out.println("[3] Return a Vehicle");
            System.out.println("[4] Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1: viewVehicles(); break;
                case 2: rentVehicle(); break;
                case 3: returnVehicle(); break;
                case 4: System.out.println("Thank you for using the system!"); break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    static void viewVehicles() {
        System.out.println("\n#  Type         Plate      Brand      Model        Rate             Status");
        for (int i = 0; i < fleet.length; i++) {
            System.out.print((i + 1) + ". ");
            fleet[i].displayInfo();
        }
    }

    static void rentVehicle() {
        viewVehicles();
        System.out.print("\nSelect vehicle number: ");
        int index = sc.nextInt() - 1;

        if (index < 0 || index >= fleet.length) {
            System.out.println("Invalid vehicle number.");
            return;
        }
        if (fleet[index].isRented) {
            System.out.println("Sorry, that vehicle is already rented.");
            return;
        }

        System.out.print("Number of days: ");
        int days = sc.nextInt();
        if (days <= 0) {
            System.out.println("Invalid number of days.");
            return;
        }

        double total = fleet[index].calculateRentalCost(days);
        fleet[index].isRented = true;

        System.out.println("\n--- RENTAL RECEIPT ---");
        System.out.println("Vehicle : " + fleet[index].getType() + " - "
                + fleet[index].brand + " " + fleet[index].model);
        System.out.println("Plate   : " + fleet[index].plateNumber);
        System.out.println("Days    : " + days);
        System.out.printf("Total   : P%,.2f%n", total);
    }

    static void returnVehicle() {
        System.out.print("\nEnter plate number: ");
        String plate = sc.next();

        for (Vehicle v : fleet) {
            if (v.plateNumber.equalsIgnoreCase(plate)) {
                if (!v.isRented) {
                    System.out.println("That vehicle is not currently rented.");
                } else {
                    v.isRented = false;
                    System.out.println(v.brand + " " + v.model + " returned successfully.");
                }
                return;
            }
        }
        System.out.println("Vehicle not found.");
    }
}
