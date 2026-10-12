import java.time.LocalDate;

public class PerishableItem extends Item {
    private LocalDate expiryDate;

    public PerishableItem(String id, String name, String category, int quantity,
                          double unitPrice, LocalDate expiryDate) {
        super(id, name, category, quantity, unitPrice);
        setExpiryDate(expiryDate);
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date is required for perishable items.");
        }
        this.expiryDate = expiryDate;
    }

    @Override
    public String getType() {
        return "PERISHABLE";
    }

    @Override
    public String getExtraInfo() {
        return "Expires " + expiryDate + (isExpired() ? " (EXPIRED)" : "");
    }

    @Override
    protected String extraFileData() {
        return expiryDate.toString();
    }
}
