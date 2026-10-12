import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private final Map<String, Item> items = new LinkedHashMap<>();
    private int nextIdNumber = 1;

    public String generateId() {
        String id;
        do {
            id = String.format("ITM-%04d", nextIdNumber++);
        } while (items.containsKey(id));
        return id;
    }

    public void addItem(Item item) {
        if (items.containsKey(item.getId())) {
            throw new IllegalArgumentException("An item with ID " + item.getId() + " already exists.");
        }
        items.put(item.getId(), item);
        syncIdCounter(item.getId());
    }

    public Item findById(String id) {
        return items.get(id.trim().toUpperCase());
    }

    public boolean removeItem(String id) {
        return items.remove(id.trim().toUpperCase()) != null;
    }

    public List<Item> searchByName(String keyword) {
        String key = keyword.trim().toLowerCase();
        List<Item> results = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(key)
                    || item.getCategory().toLowerCase().contains(key)) {
                results.add(item);
            }
        }
        return results;
    }

    public List<Item> getLowStock(int threshold) {
        List<Item> results = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getQuantity() <= threshold) {
                results.add(item);
            }
        }
        return results;
    }

    public List<PerishableItem> getExpiredItems() {
        List<PerishableItem> results = new ArrayList<>();
        for (Item item : items.values()) {
            if (item instanceof PerishableItem) {
                PerishableItem p = (PerishableItem) item;
                if (p.isExpired()) {
                    results.add(p);
                }
            }
        }
        return results;
    }

    public double getTotalValue() {
        double total = 0;
        for (Item item : items.values()) {
            total += item.getTotalValue();
        }
        return total;
    }

    public Collection<Item> getAllItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    private void syncIdCounter(String id) {
        if (id.startsWith("ITM-")) {
            try {
                int number = Integer.parseInt(id.substring(4));
                if (number >= nextIdNumber) {
                    nextIdNumber = number + 1;
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
