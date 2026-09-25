abstract class Person {
    protected String id;
    protected String name;
    protected int age;

    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public abstract String getRole();

    public void displayInfo() {
        System.out.println("Role: " + getRole());
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
    }
}

abstract class Staff extends Person {
    protected double baseSalary;

    public Staff(String id, String name, int age, double baseSalary) {
        super(id, name, age);
        this.baseSalary = baseSalary;
    }

    public abstract double computeSalary();

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.printf("Total Salary: %.2f%n", computeSalary());
    }
}

class Doctor extends Staff {
    private String specialization;
    private int patientsHandled;

    public Doctor(String id, String name, int age, double baseSalary, String specialization, int patientsHandled) {
        super(id, name, age, baseSalary);
        this.specialization = specialization;
        this.patientsHandled = patientsHandled;
    }

    @Override
    public String getRole() {
        return "Doctor";
    }

    @Override
    public double computeSalary() {
        return baseSalary + (patientsHandled * 500);
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Specialization: " + specialization);
        System.out.println("Patients Handled: " + patientsHandled);
    }
}

class Nurse extends Staff {
    private String ward;
    private int overtimeHours;

    public Nurse(String id, String name, int age, double baseSalary, String ward, int overtimeHours) {
        super(id, name, age, baseSalary);
        this.ward = ward;
        this.overtimeHours = overtimeHours;
    }

    @Override
    public String getRole() {
        return "Nurse";
    }

    @Override
    public double computeSalary() {
        return baseSalary + (overtimeHours * 150);
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Assigned Ward: " + ward);
        System.out.println("Overtime Hours: " + overtimeHours);
    }
}

abstract class Patient extends Person {
    protected String illness;
    protected int daysAdmitted;

    public Patient(String id, String name, int age, String illness, int daysAdmitted) {
        super(id, name, age);
        this.illness = illness;
        this.daysAdmitted = daysAdmitted;
    }

    public abstract double computeBill();

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Illness: " + illness);
        System.out.println("Days Admitted: " + daysAdmitted);
        System.out.printf("Total Bill: %.2f%n", computeBill());
    }
}

class InPatient extends Patient {
    private String roomType;

    public InPatient(String id, String name, int age, String illness, int daysAdmitted, String roomType) {
        super(id, name, age, illness, daysAdmitted);
        this.roomType = roomType;
    }

    @Override
    public String getRole() {
        return "In-Patient";
    }

    @Override
    public double computeBill() {
        double rate = roomType.equalsIgnoreCase("Private") ? 5000 : 2000;
        return daysAdmitted * rate;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Room Type: " + roomType);
    }
}

class OutPatient extends Patient {
    private double consultationFee;

    public OutPatient(String id, String name, int age, String illness, double consultationFee) {
        super(id, name, age, illness, 0);
        this.consultationFee = consultationFee;
    }

    @Override
    public String getRole() {
        return "Out-Patient";
    }

    @Override
    public double computeBill() {
        return consultationFee;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Consultation Fee: " + consultationFee);
    }
}

public class HospitalManagementSystem {
    public static void main(String[] args) {
        Person[] people = {
            new Doctor("D-001", "Dr. Ramon Cruz", 45, 60000, "Cardiology", 12),
            new Nurse("N-001", "Maria Santos", 29, 25000, "Ward B", 10),
            new InPatient("P-001", "Juan Dela Cruz", 52, "Pneumonia", 4, "Private"),
            new InPatient("P-002", "Ana Reyes", 34, "Appendicitis", 3, "Ward"),
            new OutPatient("P-003", "Luis Garcia", 21, "Flu", 800)
        };

        System.out.println("===== HOSPITAL MANAGEMENT SYSTEM =====");
        for (Person p : people) {
            System.out.println("--------------------------------------");
            p.displayInfo();
        }

        System.out.println("======================================");
        double totalSalaries = 0;
        double totalBills = 0;
        for (Person p : people) {
            if (p instanceof Staff) {
                totalSalaries += ((Staff) p).computeSalary();
            } else if (p instanceof Patient) {
                totalBills += ((Patient) p).computeBill();
            }
        }
        System.out.printf("Total Staff Salaries: %.2f%n", totalSalaries);
        System.out.printf("Total Patient Bills: %.2f%n", totalBills);
    }
}
