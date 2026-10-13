public class StandardItem extends Item {
    public StandardItem(String id, String name, String category, int quantity, double unitPrice) {
        super(id, name, category, quantity, unitPrice);
    }

    @Override
    public String getType() {
        return "STANDARD";
    }

    @Override
    public String getExtraInfo() {
        return "-";
    }

    @Override
    protected String extraFileData() {
        return "-";
    }
}
