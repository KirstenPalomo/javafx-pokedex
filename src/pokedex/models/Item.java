package pokedex.models;

public class Item {
    private String name;
    private String category;
    private String description;
    private String effects;
    private Integer minBuyingPrice;
    private Integer maxBuyingPrice;
    private int sellingPrice;
    public static final String NOT_SOLD = "Not sold";

    public Item(String name, String category, String description, String effects, Integer minBuyingPrice, Integer maxBuyingPrice, int sellingPrice){
        this.name = name;
        this.category = category;
        this.description = description;
        this.effects = effects;
        this.minBuyingPrice = minBuyingPrice;
        this.maxBuyingPrice = maxBuyingPrice;
        this.sellingPrice = sellingPrice;
    }

    public String getName(){
        return name;
    }
    public String getCategory(){
        return category;
    }
    public String getDescription(){
        return description;
    }
    public String getEffects(){
        return effects;
    }
    public Integer getMinBuyingPrice(){
        return minBuyingPrice;
    }
    public int getMaxBuyingPrice(){
        return maxBuyingPrice;
    }
    public int getSellingPrice(){
        return sellingPrice;
    }

    public boolean isSold() {
        return minBuyingPrice != null;
    }

    public String getFormattedBuyingPrice() {
        if (!isSold()) {
            return NOT_SOLD;
        } else if (maxBuyingPrice != null && !minBuyingPrice.equals(maxBuyingPrice)) {
            return String.format("₱%,d–%,d", minBuyingPrice, maxBuyingPrice);
        } else {
            return String.format("₱%,d", minBuyingPrice);
        }
    }

    public String getFormattedSellingPrice() {
        return String.format("₱%,d", sellingPrice);
    }

    public String getBuyingPrice() {
        return getFormattedBuyingPrice();
    }

    @Override
    public String toString() {
        return name + " (₱" + (minBuyingPrice != null ? minBuyingPrice : "?") + ")";
    }

}
