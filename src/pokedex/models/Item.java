/**
 * Represents an item that can be bought, sold, or used in the Pokédex system.
 * Each item has a name, category, description, effects, and price information.
 * It is used in various gameplay mechanics such as healing, boosting stats, and evolution.
 * This class also supports logic for displaying item prices in formatted form.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */

package pokedex.models;

/**
 * Represents an item that can be bought, sold, or used in the Pokédex system.
 * Each item has a name, category, description, effects, and price information.
 */
public class Item {
    private String name;
    private String category;
    private String description;
    private String effects;
    private Integer minBuyingPrice;
    private Integer maxBuyingPrice;
    private int sellingPrice;
    public static final String NOT_SOLD = "Not sold";

    /**
     * Constructs an Item with the given attributes.
     *
     * @param name the name of the item
     * @param category the item category (e.g., Vitamin, Feather, Evolution Stone)
     * @param description a brief description of the item
     * @param effects the effect(s) of the item (e.g., "hp+10")
     * @param minBuyingPrice minimum buying price, or null if not sold
     * @param maxBuyingPrice maximum buying price, or null if fixed price
     * @param sellingPrice the price at which the item can be sold
     */
    public Item(String name, String category, String description, String effects, Integer minBuyingPrice, Integer maxBuyingPrice, int sellingPrice){
        this.name = name;
        this.category = category;
        this.description = description;
        this.effects = effects;
        this.minBuyingPrice = minBuyingPrice;
        this.maxBuyingPrice = maxBuyingPrice;
        this.sellingPrice = sellingPrice;
    }

    /**
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * @return the category of the item
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return the item description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the effect(s) this item has
     */
    public String getEffects() {
        return effects;
    }

    /**
     * @return the minimum buying price, or null if not sold
     */
    public Integer getMinBuyingPrice() {
        return minBuyingPrice;
    }

    /**
     * @return the maximum buying price, or 0 if fixed
     */
    public int getMaxBuyingPrice() {
        return maxBuyingPrice;
    }

    /**
     * @return the selling price of the item
     */
    public int getSellingPrice() {
        return sellingPrice;
    }

    /**
     * Checks if the item is available for purchase.
     *
     * @return true if the item has a buying price, false if not sold
     */
    public boolean isSold() {
        return minBuyingPrice != null;
    }

    /**
     * Returns a formatted string representing the buying price.
     * Shows a range if both min and max are different.
     *
     * @return the formatted buying price (e.g., "₱200–300" or "Not sold")
     */
    public String getFormattedBuyingPrice() {
        if (!isSold()) {
            return NOT_SOLD;
        } else if (maxBuyingPrice != null && !minBuyingPrice.equals(maxBuyingPrice)) {
            return String.format("₱%,d–%,d", minBuyingPrice, maxBuyingPrice);
        } else {
            return String.format("₱%,d", minBuyingPrice);
        }
    }

    /**
     * @return the formatted selling price (e.g., "₱100")
     */
    public String getFormattedSellingPrice() {
        return String.format("₱%,d", sellingPrice);
    }

    /**
     * Shortcut method for getting the formatted buying price.
     *
     * @return same as getFormattedBuyingPrice()
     */
    public String getBuyingPrice() {
        return getFormattedBuyingPrice();
    }

    /**
     * Returns a string representation of the item.
     * Includes name and minimum price (if sold).
     *
     * @return formatted string (e.g., "Potion (₱200)")
     */
    @Override
    public String toString() {
        return name + " (₱" + (minBuyingPrice != null ? minBuyingPrice : "?") + ")";
    }

}
