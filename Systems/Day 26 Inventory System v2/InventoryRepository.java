import java.io.IOException;

public interface InventoryRepository {
    Inventory load() throws IOException;
    void save(Inventory inventory) throws IOException;
}
