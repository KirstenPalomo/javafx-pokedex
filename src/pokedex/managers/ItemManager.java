package pokedex.managers;

import pokedex.models.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    private List<Item> items = new ArrayList<>();

    public ItemManager() {
        //Initialize with some default items
        //Vitamins and candies
        items.add(new Item("HP Up", "Vitamin", "A nutritious drink for Pokémon.", "+10 HP EVs",10000, 10000, 5000));
        items.add(new Item("Protein", "Vitamin", "A nutritious drink for Pokémon.", "+10 Attack EVs", 10000, 10000, 5000));
        items.add(new Item("Iron", "Vitamin", "A nutritious drink for Pokémon.", "+10 Defense EVs", 10000, 10000, 5000));
        items.add(new Item("Carbos", "Vitamin", "A nutritious drink for Pokémon.", "+10 Speed EVs", 10000, 10000, 5000));
        items.add(new Item("Zinc", "Vitamin", "A nutritious drink for Pokémon.", "+10 Special Defense EVs", 10000, 10000, 5000));
        items.add(new Item("Rare Candy", "Leveling Item", "A candy that is packed with energy.", "Increases level by 1 (stat gain depends on Pokémon's base stats and EVs)", null, null, 2400));

        //Feather
        items.add(new Item("Health Feather", "Feather", "A feather that slightly increases HP.", "+1 HP EV", 300, 300, 150));
        items.add(new Item("Muscle Feather", "Feather", "A feather that slightly increases Attack.", "+1 Attack EV", 300, 300, 150));
        items.add(new Item("Resist Feather", "Feather", "A feather that slightly increases Defense.", "+1 Defense EV", 300, 300, 150));
        items.add(new Item("Swift Feather", "Feather", "A feather that slightly increases Speed.", "+1 Speed EV", 300, 300, 150));

        //Evolution stones
        items.add(new Item("Fire Stone", "Evolution Stone", "A stone that radiates heat.", "Evolves Pokémon like Vulpix, Growlithe, Eevee (into Flareon), etc.", 3000, 5000, 1500));
        // Fire Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
        items.add(new Item("Water Stone", "Evolution Stone", "A stone with a blue watery appearance.", "Evolves Pokémon like Poliwhirl, Shellder, Eevee (into Vaporeon), etc.", 3000, 5000, 1500));
        // Water Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
        items.add(new Item("Thunder Stone", "Evolution Stone", "A stone that sparkles with electricity.", "Evolves Pokémon like Pikachu, Eevee (into Jolteon), Eelektrik, etc.", 3000, 5000, 1500));
        // Thunder Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
        items.add(new Item("Leaf Stone", "Evolution Stone", "A stone with a leaf pattern", "Evolves Pokémon like Gloom, Weepinbell, Exeggcute, etc.", 3000, 5000, 1500));
        // Leaf Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
        items.add(new Item("Moon Stone", "Evolution Stone", "A stone that glows faintly in the moonlight.", "Evolves Pokémon like Nidorina, Clefairy, Jigglypuff, etc.", null, null, 1500));
        // Moon Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
        items.add(new Item("Sun Stone", "Evolution Stone", "A stone that glows like the sun.", "Evolves Pokémon like Gloom (into Bellossom), Sunkern, Cottonee, etc.", 3000, 5000, 1500));
        // Sun Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
        items.add(new Item("Shiny Stone", "Evolution Stone", "A stone that sparkles brightly.", "Evolves Pokémon like Togetic, Roselia, Minccino, etc.", 3000, 5000, 1500));
        // Shiny Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
        items.add(new Item("Dusk Stone", "Evolution Stone", "A stone that is ominous in appearance.", "Evolves Pokémon like Munkrow, Misdreavus, Doublade, etc.", 3000, 5000, 1500));
        // Dusk Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
        items.add(new Item("Dawn Stone", "Evolution Stone", "A stone that sparkles like the morning sky.", "Evolves male Kirlia into Gallade, female Snorunt into Froslass.", 3000, 5000, 1500));
        // Dawn Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
        items.add(new Item("Ice Stone", "Evolution Stone", "A stone that is cold to the touch.", "Evolves Pokémon like Alolan Vulpix, Galarian Darumaka, Eevee (into Glaceon).", 3000, 5000, 1500));
        // Ice Stone: Spec says ₱3000–₱5000, using max ₱5000 for standardization
    }

    public void viewAllItems() {
        if (items.isEmpty()) {
            System.out.println("No items available.");
            return;
        }
        for (Item item : items) {
            System.out.println(item);
        }
    }

    public void searchItemByName(String name) {
        List<Item> found = new ArrayList<>();
        String keyword = name.toLowerCase();

        for (Item item : items) {
            if (item.getName().toLowerCase().contains(keyword)) {
                found.add(item);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No items found with name matching: " + name);
        } else {
            System.out.printf("Item(s) found:\n\n");
            for (Item item : found) {
                System.out.println(item);
            }
        }
    }

    public List<Item> getAllItems() {
        return items;
    }
    public void searchItemByEffect(String effect) {
        List<Item> found = new ArrayList<>();
        String keyword = effect.toLowerCase();

        for (Item item : items) {
            if (item.getEffects().toLowerCase().contains(keyword)) {
                found.add(item);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No items found with effect matching: " + effect);
        } else {
            System.out.printf("Item(s) found:\n\n");
            for (Item item : found) {
                System.out.println(item);
            }
        }
    }

    public void searchItemByCategory(String category) {
        List<Item> found = new ArrayList<>();
        String keyword = category.toLowerCase();

        for (Item item : items) {
            if (item.getCategory().toLowerCase().contains(keyword)) {
                found.add(item);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No items found in category: " + category);
        } else {
            System.out.printf("Item(s) found:\n\n");
            for (Item item : found) {
                System.out.println(item);
            }
        }
    }

    public void searchByItemPriceRange(int minPrice, int maxPrice) {
        List<Item> found = new ArrayList<>();

        for (Item item : items) {
            if (!item.isSold()) {
                continue; // skip items not sold
            }

            Integer itemMin = item.getMinBuyingPrice();
            Integer itemMax = item.getMaxBuyingPrice();

            // If itemMin or itemMax is null, skip it (optional check)
            if (itemMin == null || itemMax == null) continue;

            // Check for any overlap between item range and search range
            boolean overlaps = itemMax >= minPrice && itemMin <= maxPrice;
            if (overlaps) {
                found.add(item);
            }
        }

        if (found.isEmpty()) {
            System.out.println("No items found in price range: ₱" + minPrice + " - ₱" + maxPrice);
        } else {
            System.out.println("Item(s) found:\n");
            for (Item item : found) {
                System.out.println(item);
            }
        }
    }


    public Item getItemByName(String name) {
        if (name == null) return null;
        String key = name.trim().toLowerCase();
        for (Item item : items) {
            if (item.getName().toLowerCase().equals(key)) {
                return item;
            }
        }
        return null;
    }

    public boolean hasItemWithName(String name) {
        if (name == null) return false;
        String key = name.trim().toLowerCase();
        for (Item item : items) {
            if (item.getName().toLowerCase().equals(key)) {
                return true;
            }
        }
        return false;
    }
}