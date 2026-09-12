import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Menu.java
 *
 * Owns the full catalog of MenuItem objects. Uses two kinds of
 * COLLECTIONS:
 *  - a Map<Integer, MenuItem> for fast lookup of an item by its id
 *  - Lists for grouping/searching results
 * LinkedHashMap is used (instead of plain HashMap) so items keep the
 * order they were added in when the menu is displayed.
 */
public class Menu {

    private final Map<Integer, MenuItem> itemsById;

    public Menu() {
        itemsById = new LinkedHashMap<>();
        loadDefaultMenu();
    }

    private void loadDefaultMenu() {
        addItem(new FoodItem("Spring Rolls", 120.00, Category.APPETIZER, "Crispy vegetable rolls", false));
        addItem(new FoodItem("Buffalo Wings", 180.00, Category.APPETIZER, "Spicy chicken wings", true));
        addItem(new FoodItem("Grilled Chicken", 250.00, Category.MAIN_COURSE, "Grilled chicken breast with rice", false));
        addItem(new FoodItem("Beef Sisig", 220.00, Category.MAIN_COURSE, "Sizzling chopped beef", true));
        addItem(new FoodItem("Pork Adobo", 200.00, Category.MAIN_COURSE, "Classic braised pork in soy-vinegar sauce", false));
        addItem(new FoodItem("Chocolate Lava Cake", 130.00, Category.DESSERT, "Warm cake with a molten center", false));
        addItem(new FoodItem("Halo-Halo", 110.00, Category.DESSERT, "Mixed shaved ice dessert", false));
        addItem(new BeverageItem("Iced Tea", 60.00, "House blend iced tea", "Medium", true));
        addItem(new BeverageItem("Hot Coffee", 70.00, "Brewed coffee", "Small", false));
        addItem(new BeverageItem("Mango Shake", 90.00, "Fresh mango shake", "Large", true));
    }

    public void addItem(MenuItem item) {
        itemsById.put(item.getId(), item);
    }

    public boolean removeItem(int id) {
        return itemsById.remove(id) != null;
    }

    public MenuItem getItemById(int id) {
        return itemsById.get(id);
    }

    public List<MenuItem> getAllItems() {
        return new ArrayList<>(itemsById.values());
    }

    /**
     * Groups every menu item by its Category, e.g. all appetizers together,
     * all main courses together, etc. Returns a Map<Category, List<MenuItem>>.
     */
    public Map<Category, List<MenuItem>> getItemsGroupedByCategory() {
        Map<Category, List<MenuItem>> grouped = new LinkedHashMap<>();
        for (Category c : Category.values()) {
            grouped.put(c, new ArrayList<>());
        }
        for (MenuItem item : itemsById.values()) {
            grouped.get(item.getCategory()).add(item);
        }
        return grouped;
    }

    public List<MenuItem> searchByName(String keyword) {
        List<MenuItem> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (MenuItem item : itemsById.values()) {
            if (item.getName().toLowerCase().contains(lower)) {
                results.add(item);
            }
        }
        return results;
    }

    public void displayFullMenu() {
        Map<Category, List<MenuItem>> grouped = getItemsGroupedByCategory();
        for (Category c : Category.values()) {
            List<MenuItem> list = grouped.get(c);
            if (list.isEmpty()) {
                continue;
            }
            System.out.println("\n-- " + c.getLabel() + " --");
            for (MenuItem item : list) {
                System.out.println(item.toString() + " | " + item.getSpecialInfo());
            }
        }
    }
}
