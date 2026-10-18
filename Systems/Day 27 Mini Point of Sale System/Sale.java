import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Sale implements Storable {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String saleId;
    private final LocalDateTime dateTime;
    private final List<CartItem> items;
    private final double total;
    private final double cash;

    public Sale(String saleId, List<CartItem> items, double total, double cash) {
        this.saleId = saleId;
        this.dateTime = LocalDateTime.now();
        this.items = new ArrayList<>(items);
        this.total = total;
        this.cash = cash;
    }

    public String getSaleId() { return saleId; }
    public double getTotal()  { return total; }
    public double getChange() { return cash - total; }

    @Override
    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("                 MINI POS RECEIPT\n");
        sb.append("Sale ID : ").append(saleId).append("\n");
        sb.append("Date    : ").append(dateTime.format(FMT)).append("\n");
        sb.append("--------------------------------------------------\n");
        for (CartItem item : items) sb.append(item).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-36s %12.2f%n", "TOTAL", total));
        sb.append(String.format("%-36s %12.2f%n", "CASH", cash));
        sb.append(String.format("%-36s %12.2f%n", "CHANGE", getChange()));
        sb.append("==================================================\n");
        return sb.toString();
    }
}
