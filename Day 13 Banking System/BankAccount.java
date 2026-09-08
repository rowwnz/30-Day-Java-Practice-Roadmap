
public class BankAccount {

    //Private fields: cannot be accessed directly outside this class 
    private String accountNumber;
    private String accountHolder;
    private double balance;

    //Constructor
    public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = (initialBalance >= 0) ? initialBalance : 0;
    }

    //Getters (controlled read access) 
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    //Behavior methods (controlled write access, with validation)

    /**
   
     * @param amount must be positive, otherwise the deposit is rejected.
     * @return true if the deposit succeeded.
     */

    
    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit failed: amount must be greater than zero.");
            return false;
        }
        balance += amount;
        System.out.printf("Deposit successful. New balance: %.2f%n", balance);
        return true;
    }

    /**
     * Removes funds from the account.
     * @param amount must be positive and not exceed the current balance.
     * @return true if the withdrawal succeeded.
     */

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal failed: amount must be greater than zero.");
            return false;
        }
        if (amount > balance) {
            System.out.println("Withdrawal failed: insufficient balance.");
            return false;
        }
        balance -= amount;
        System.out.printf("Withdrawal successful. New balance: %.2f%n", balance);
        return true;
    }

    //Prints a formatted summary of the account.
    
    public void displayAccountInfo() {
        System.out.println("----------------------------------");
        System.out.println("Account Number : " + accountNumber);
        System.out.println("Account Holder : " + accountHolder);
        System.out.printf("Balance        : %.2f%n", balance);
        System.out.println("----------------------------------");
    }
}
