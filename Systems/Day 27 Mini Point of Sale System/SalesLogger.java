import java.io.*;

public class SalesLogger {
    private final String fileName;

    public SalesLogger(String fileName) {
        this.fileName = fileName;
    }

    public void log(Sale sale) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.print(sale.toFileString());
            writer.println();
        } catch (IOException e) {
            System.out.println("Error writing " + fileName + ": " + e.getMessage());
        }
    }

    public int nextSaleNumber() {
        int count = 0;
        File file = new File(fileName);
        if (!file.exists()) return 1;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Sale ID")) count++;
            }
        } catch (IOException e) {
            System.out.println("Error reading " + fileName + ": " + e.getMessage());
        }
        return count + 1;
    }

    public void printHistory() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("No sales recorded yet.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            System.out.println("Error reading " + fileName + ": " + e.getMessage());
        }
    }
}
