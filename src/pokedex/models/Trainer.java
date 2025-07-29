/**
 * This class represents a Pokémon Trainer in the Pokédex system.
 * Trainers have personal profiles, a lineup and storage for Pokémon,
 * an inventory system for items, and methods to interact with both.
 *
 * Core functionality includes buying/selling items, using items on Pokémon,
 * teaching and forgetting moves, managing Pokémon lineup/storage,
 * and triggering Pokémon evolutions through level-up or stones.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */

package pokedex.models;

// Core model dependencies for item and pokedex logic
import pokedex.managers.ItemManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.MoveManager;

// Java built-in serialization for saving trainer data
import java.io.Serializable;
// Used to store and manage trainer birthdates
import java.time.LocalDate;
// Utility classes: lists, maps, and collections
import java.util.*;

/**
 * Represents a Pokémon Trainer with profile info, lineup, storage,
 * item bag, and interactive methods for gameplay mechanics.
 */
public class Trainer implements Serializable {
    private static final long serialVersionUID = 1L;

    // ── Profile ───────────────────────────────────────────────────────────────
    private final String trainerID;
    private final String name;
    private final LocalDate birthdate;
    private final String sex;
    private final String hometown;
    private final String description;

    // ── Funds ────────────────────────────────────────────────────────────────
    private int money;

    // ── Pokémon ─────────────────────────────────────────────────────────────
    private final List<Pokemon> lineup;   // up to 6
    private final List<Pokemon> storage;

    // ── Item bag ────────────────────────────────────────────────────────────
    private final Map<String, BagItem> itemBag;

    /**
     * Represents an item in the trainer's bag along with its quantity.
     */
    public static class BagItem implements Serializable {
        final Item item;
        int quantity;

        /**
         * Constructs a BagItem with an item and its quantity.
         *
         * @param item the item reference
         * @param quantity how many of this item the trainer has
         */
        public BagItem(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
        /** @return the item */
        public Item getItem() {
            return item;
        }
        /** @return the quantity of this item */
        public int getQuantity() {
            return quantity;
        }

        /**
         * Decreases the item quantity by 1.
         */
        public void decrement()
        {
            quantity--;
        }
    }

    /**
     * Creates a trainer with default starting money (₱1,000,000).
     *
     * @param trainerID the trainer's unique ID
     * @param name trainer's name
     * @param birthdate date of birth
     * @param sex sex of trainer
     * @param hometown home city/town
     * @param description description or bio
     */
    public Trainer(String trainerID,
                   String name,
                   LocalDate birthdate,
                   String sex,
                   String hometown,
                   String description) {
        this(trainerID, name, birthdate, sex, hometown, description, 1_000_000);
    }

    /**
     * Full constructor that allows setting custom starting funds.
     *
     * @param trainerID trainer's unique ID
     * @param name trainer's name
     * @param birthdate date of birth
     * @param sex trainer's sex
     * @param hometown trainer's hometown
     * @param description description or bio
     * @param startingMoney initial money
     */
    public Trainer(String trainerID,
                   String name,
                   LocalDate birthdate,
                   String sex,
                   String hometown,
                   String description,
                   int startingMoney) {
        this.trainerID   = trainerID;
        this.name        = name;
        this.birthdate   = birthdate;
        this.sex         = sex;
        this.hometown    = hometown;
        this.description = description;
        this.money       = startingMoney;
        this.lineup      = new ArrayList<>();
        this.storage     = new ArrayList<>();
        this.itemBag     = new HashMap<>();
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    /** @return the trainer’s ID */
    public String getTrainerID()    { return trainerID; }

    /** @return the trainer’s name */
    public String getName()         { return name; }

    /** @return the trainer’s birthdate */
    public LocalDate getBirthdate() { return birthdate; }

    /** @return the trainer’s sex */
    public String getSex()          { return sex; }

    /** @return the trainer’s hometown */
    public String getHometown()     { return hometown; }

    /** @return the trainer’s description or bio */
    public String getDescription()  { return description; }

    /** @return current amount of money the trainer has */
    public int getMoney()           { return money; }

    /** @return the trainer’s current Pokémon lineup (read-only) */
    public List<Pokemon> getLineup()  { return Collections.unmodifiableList(lineup); }

    /** @return the trainer’s storage Pokémon list (read-only) */
    public List<Pokemon> getStorage() { return Collections.unmodifiableList(storage); }

    /** @return the trainer’s item bag map (modifiable) */
    public Map<String, BagItem> getItemBag() { return itemBag; }


    // ── Core Item Logic ──────────────────────────────────────────────────────

    /**
     * Attempts to buy an item from the shop.
     * Enforces:
     * - Money availability
     * - 10 unique item limit
     * - 50 total item limit
     * Also gives a bonus Rare Candy if space allows.
     *
     * @param item the item to buy
     * @return status message indicating success, error, or bonus info
     */
    public String buyItem(Item item) {
        String nm = item.getName();
        Integer priceObj = item.getMinBuyingPrice();
        // Item is not sold
        if (priceObj == null) {
            return "⚠️ Item is not sold and cannot be bought.";
        }
        int price = priceObj;

        if (money < price) {
            return "ERROR: Not enough money.";
        }

        // Total quantity of all items in bag
        int totalCount = itemBag.values().stream().mapToInt(b -> b.quantity).sum();

        // Check if adding the bought item would exceed unique or total limits
        boolean isNewItem = !itemBag.containsKey(nm);
        // Max of 10 unique item types allowed
        if (isNewItem && itemBag.size() >= 10) {
            return "⚠️ Max 10 unique items.";
        }

        // Max of 50 items in total allowed
        if (totalCount >= 50) {
            return "⚠️ Max 50 items total.";
        }

        // Deduct money and add the bought item
        money -= price;
        itemBag.putIfAbsent(nm, new BagItem(item, 0));
        itemBag.get(nm).quantity++;

        // Attempt to give 1 Rare Candy
        Item rareCandy = ItemManager.getInstance().getItemByName("Rare Candy");
        if (rareCandy != null) {
            boolean newCandy = !itemBag.containsKey("Rare Candy");
            // If no space for bonus candy (either unique or total limit reached)
            if ((newCandy && itemBag.size() >= 10) || (totalCount + 1 > 50)) {
                return "SUCCESS: Bought " + nm + " for ₱" + price + "\n⚠️ Bonus Rare Candy could not be given (item limit reached).";
            }
            itemBag.putIfAbsent("Rare Candy", new BagItem(rareCandy, 0));
            itemBag.get("Rare Candy").quantity++;
            return "SUCCESS: Bought " + nm + " for ₱" + price + "\n🎁 Bonus: Received 1 Rare Candy!";
        }

        return "SUCCESS: Bought " + nm + " for ₱" + price;
    }


    /**
     * Sells one quantity of an item.
     * Refund is 50% of the buying price if available,
     * otherwise it uses the fixed selling price.
     *
     * @param itemName name of the item to sell
     */
    public void sellItem(String itemName) {
        BagItem bag = itemBag.get(itemName);
        if (bag == null) {
            System.out.println("⚠️ You don’t have that item.");
            return;
        }

        int refund = 0;


        Integer buyingPrice = bag.item.getMinBuyingPrice();
        if (buyingPrice != null && buyingPrice > 0) {
            // Standard refund: 50% of buying price
            refund = buyingPrice / 2;
        } else {
            // Fallback: use fixed selling price if defined
            int fixedSell = bag.item.getSellingPrice();
            if (fixedSell > 0) {
                refund = fixedSell;
            }
        }

        if (refund <= 0) {
            System.out.println("⚠️ Item cannot be sold.");
            return;
        }

        money += refund;
        bag.quantity--;
        if (bag.quantity == 0) itemBag.remove(itemName);

        System.out.printf("✅ Sold %s for ₱%,d%n", itemName, refund);
    }

    /**
     * Displays all items currently in the trainer's bag,
     * including quantity, total count, and unique item count.
     */
    public void viewBag() {
        System.out.println("=== Item Bag ===");
        if (itemBag.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        // Show each item with quantity
        itemBag.values().forEach(b ->
                System.out.printf("- %s x%d%n", b.item.getName(), b.quantity)
        );
        int total = itemBag.values().stream().mapToInt(b->b.quantity).sum();
        System.out.printf("Total: %d/50 items, %d/10 unique%n",
                total, itemBag.size());
    }

    // ── Core Pokémon Logic ──────────────────────────────────────────────────

    /**
     * Adds a Pokémon to the trainer's lineup if there is space (max 6),
     * otherwise adds it to the storage.
     * Prevents duplicates in the lineup.
     *
     * @param p the Pokémon to add
     * @return true if added to lineup, false if added to storage
     */
    public boolean addPokemon(Pokemon p) {
        // Prevent duplicate Pokémon in lineup by name
        for (Pokemon existing : lineup) {
            if (existing.getName().equalsIgnoreCase(p.getName())) {
                System.out.println("⚠️ " + p.getName() + " is already in the lineup.");
                return true; // Still return true so it's not sent to storage again
            }
        }

        if (lineup.size() < 6) {
            lineup.add(p);
            return true;
        } else {
            storage.add(p);
            return false;
        }
    }

    /**
     * Switches a Pokémon between the lineup and storage based on index.
     *
     * @param li index of Pokémon in lineup
     * @param si index of Pokémon in storage
     */
    public void switchPokemon(int li, int si) {
        if (li < 0 || li >= lineup.size() || si < 0 || si >= storage.size()) {
            System.out.println("⚠️ Invalid switch indexes.");
            return;
        }
        Pokemon temp = lineup.get(li);
        lineup.set(li, storage.get(si));
        storage.set(si, temp);
        System.out.println("✅ Pokémon switched.");
    }

    /**
     * Releases a Pokémon by name from either lineup or storage.
     *
     * @param pokeName name of the Pokémon to release
     */
    public void releasePokemon(String pokeName) {
        boolean removed = lineup.removeIf(p -> p.getName().equalsIgnoreCase(pokeName));
        if (!removed) removed = storage.removeIf(p -> p.getName().equalsIgnoreCase(pokeName));
        System.out.println(removed ? "✅ Released " + pokeName : "⚠️ Pokémon not found.");
    }

    /**
     * Attempts to teach a move to the target Pokémon.
     * Checks for compatibility and move limit (max 4), and supports HM restriction.
     *
     * @param target the Pokémon to learn the move
     * @param move the move to teach
     * @param moveManager the MoveManager instance used for lookup
     * @return result message (e.g., success, already knows, not compatible, or needs to forget)
     */
    public String teachMove(Pokemon target, Move move, MoveManager moveManager) {
        List<String> moves = target.getMoveSet();
        String mName = move.getName();

        // Skip if already known
        if (moves.contains(mName)) return "Already knows " + mName;

        // Compatibility check
        String t1 = target.getType1(), t2 = target.getType2();
        boolean compatible =
                move.getType1().equalsIgnoreCase(t1) ||
                        (t2 != null && move.getType1().equalsIgnoreCase(t2)) ||
                        (move.getType2() != null && (
                                move.getType2().equalsIgnoreCase(t1) ||
                                        (t2 != null && move.getType2().equalsIgnoreCase(t2))
                        ));
        if (!compatible) {
            return "⚠️ " + mName + " isn’t compatible with " + target.getName() + ".";
        }

        boolean isHM = move.getClassification().equalsIgnoreCase("HM");

        // Prevent learning if already has 4 moves
        if (moves.size() >= 4) {
            return "PROMPT_FORGET"; // Signal to prompt user for replacement
        }

        // All good — add the move
        moves.add(mName);
        return target.getName() + " learned " + mName + (isHM ? " (HM)." : "!");
    }

    /**
     * Replaces a TM move with a new move.
     * Does not allow HMs to be forgotten.
     *
     * @param target the Pokémon to modify
     * @param forgetMove the move to be forgotten
     * @param newMove the new move to learn
     * @param moveManager reference to MoveManager for move classification check
     * @return true if replacement was successful
     */
    public boolean forgetAndLearnMove(Pokemon target, String forgetMove, Move newMove, MoveManager moveManager) {
        List<String> moves = target.getMoveSet();
        int idx = moves.indexOf(forgetMove);
        if (idx < 0) return false;

        Move oldMoveObj = moveManager.getMoveByName(forgetMove);
        if (oldMoveObj != null && oldMoveObj.getClassification().equalsIgnoreCase("HM")) {
            return false; // HMs cannot be forgotten
        }

        // Do not allow more than 4 moves total
        if (moves.size() > 4) return false;

        // Compatibility check (optional, for safety)
        String t1 = target.getType1(), t2 = target.getType2();
        boolean compatible =
                newMove.getType1().equalsIgnoreCase(t1) ||
                        (t2 != null && newMove.getType1().equalsIgnoreCase(t2)) ||
                        (newMove.getType2() != null && (
                                newMove.getType2().equalsIgnoreCase(t1) ||
                                        (t2 != null && newMove.getType2().equalsIgnoreCase(t2))
                        ));
        if (!compatible) return false;

        // All good — replace move
        moves.set(idx, newMove.getName());
        return true;
    }


    // ── Interactive Wrappers ────────────────────────────────────────────────

    /**
     * Interactive menu for buying items using Scanner input.
     * Shows all items from ItemManager and allows the trainer to purchase them.
     *
     * @param sc Scanner for user input
     * @param im ItemManager instance to fetch item details
     */
    public void buyItem(Scanner sc, ItemManager im) {
        while (true) {
            System.out.println("\n=== Buy Item ===");
            im.viewAllItems(); // Show list of items
            System.out.print("Item to buy (or '0' to cancel): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) break;

            Item it = im.getItemByName(input);
            if (it == null) {
                System.out.println("⚠️ Item not found.");
                continue;
            }

            String result = buyItem(it);
            System.out.println(result);
        }
    }

    /**
     * Interactive menu for selling items using Scanner input.
     * Displays all sellable items in the bag and processes the selected item.
     *
     * @param sc Scanner for user input
     */
    public void sellItem(Scanner sc) {
        if (itemBag.isEmpty()) {
            System.out.println("⚠️ No items to sell.");
            return;
        }

        List<String> itemNames = new ArrayList<>(itemBag.keySet());

        System.out.println("\n=== Sell Item ===");
        for (int i = 0; i < itemNames.size(); i++) {
            String name = itemNames.get(i);
            BagItem bag = itemBag.get(name);
            System.out.printf("%d. %s ×%d%n", i + 1, name, bag.getQuantity());
        }

        System.out.print("Enter the number of the item to sell (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input.");
            return;
        }

        if (choice == 0) {
            System.out.println("❌ Cancelled.");
            return;
        }

        if (choice < 1 || choice > itemNames.size()) {
            System.out.println("❌ Invalid selection.");
            return;
        }

        String selectedName = itemNames.get(choice - 1);
        BagItem bag = itemBag.get(selectedName);

        if (bag == null || bag.quantity <= 0) {
            System.out.println("❌ You no longer have this item.");
            return;
        }

        Integer priceObj = bag.item.getMinBuyingPrice();
        if (priceObj == null) {
            System.out.println("⚠️ Item cannot be sold (Not sold in shops).");
            return;
        }

        int price = priceObj;
        int sellPrice = price / 2;

        money += sellPrice;
        bag.quantity--;
        if (bag.quantity == 0) itemBag.remove(selectedName);

        System.out.printf("✅ Sold 1 %s for ₱%,d. Current money: ₱%,d%n", selectedName, sellPrice, money);
    }


    /**
     * Prompts the user to choose an item and apply it to a selected Pokémon.
     *
     * @param sc Scanner for user input
     * @param pokedexManager reference to PokedexManager for evolution logic
     */
    public void useItem(Scanner sc, PokedexManager pokedexManager) {
        if (itemBag.isEmpty()) {
            System.out.println("⚠️ No items.");
            return;
        }

        System.out.println("Bag items:");
        itemBag.keySet().forEach(k -> System.out.println("- " + k));

        System.out.print("Select item to use: ");
        String itemName = sc.nextLine().trim();
        BagItem b = null;
        for (String key : itemBag.keySet()) {
            if (key.equalsIgnoreCase(itemName)) {
                b = itemBag.get(key);
                break;
            }
        }

        if (b == null || b.quantity == 0) {
            System.out.println("⚠️ You don’t have that item.");
            return;
        }

        if (lineup.isEmpty()) {
            System.out.println("⚠️ No Pokémon in your lineup to use this item on.");
            return;
        }

        Pokemon p = choosePokemon(sc);
        if (p == null) return;

        useItem(b.item, p, pokedexManager, sc);

        b.quantity--;
        if (b.quantity == 0) itemBag.remove(b.item.getName());
    }

    /**
     * Prompts the user to give an item to a Pokémon.
     * If the Pokémon is already holding something, it is replaced.
     *
     * @param sc Scanner for user input
     * @param im ItemManager to fetch item by name
     */
    public void giveItemToPokemon(Scanner sc, ItemManager im) {
        Pokemon p = choosePokemon(sc);
        if (p == null) return;

        // Select item to give
        System.out.print("Enter item name to give: ");
        String itemName = sc.nextLine().trim();
        Item item = im.getItemByName(itemName);

        if (item == null) {
            System.out.println("⚠️ Item not found.");
            return;
        }

        // If Pokémon already holds something, discard it
        if (p.getHeldItem() != null) {
            System.out.println("⚠️ " + p.getName() + " is currently holding: " + p.getHeldItem().getName());
            System.out.print("Giving a new item will discard the current one. Proceed? (Y/N): ");
            String confirm = sc.nextLine().trim().toLowerCase();

            if (!confirm.equals("y")) {
                System.out.println("❌ Action canceled.");
                return;
            }

            System.out.println("🗑️ Discarded " + p.getHeldItem().getName());
        }

        // Assign new item
        p.setHeldItem(item);
        System.out.println("✅ " + p.getName() + " is now holding " + item.getName());
    }

    /**
     * Prompts the user to remove a held item from a Pokémon.
     * The item is discarded after removal.
     *
     * @param sc Scanner for user input
     */
    public void removeHeldItem(Scanner sc) {
        Pokemon p = choosePokemon(sc);
        if (p == null) return;

        if (p.getHeldItem() == null) {
            System.out.println(p.getName() + " is not holding any item.");
            return;
        }

        System.out.println(p.getName() + " is currently holding: " + p.getHeldItem().getName());
        System.out.print("Are you sure you want to remove and discard it? (Y/N): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (!confirm.equals("y")) {
            System.out.println("❌ Action canceled.");
            return;
        }

        System.out.println("🗑️ " + p.getHeldItem().getName() + " was removed and discarded.");
        p.setHeldItem(null);
    }


    /**
     * Prompts for a Pokémon name and adds it to lineup or storage.
     *
     * @param sc Scanner for user input
     * @param pm PokedexManager for retrieving Pokémon by name
     */
    public void addToLineup(Scanner sc, PokedexManager pm) {
        System.out.print("Pokémon to add: ");
        Pokemon p = pm.getPokemonByName(sc.nextLine().trim());
        if (p==null) {
            System.out.println("⚠️ Not in Pokédex.");
            return;
        }
        System.out.println(addPokemon(p)
                ? "✅ Added to lineup."
                : "✅ Added to storage.");
    }

    /**
     * Prompts user to switch a Pokémon from lineup to storage and vice versa.
     *
     * @param sc Scanner for user input
     */
    public void switchPokemon(Scanner sc) {
        if (lineup.isEmpty() || storage.isEmpty()) {
            System.out.println("⚠️ Need at least one in both lists.");
            return;
        }
        System.out.println("Lineup:");
        for (int i=0;i<lineup.size();i++) {
            System.out.printf("%d. %s%n", i+1, lineup.get(i).getName());
        }
        System.out.println("Storage:");
        for (int i=0;i<storage.size();i++) {
            System.out.printf("%d. %s%n", i+1, storage.get(i).getName());
        }
        try {
            System.out.print("Lineup #: ");
            int li = Integer.parseInt(sc.nextLine())-1;
            System.out.print("Storage #: ");
            int si = Integer.parseInt(sc.nextLine())-1;
            switchPokemon(li, si);
        } catch (Exception e) {
            System.out.println("⚠️ Invalid input.");
        }
    }
    public Pokemon promptSelectPokemon(Scanner sc) {
        return choosePokemon(sc);
    }


    /**
     * Wrapper for prompting the user to select a Pokémon to release.
     *
     * @param sc Scanner for user input
     */
    public void releasePokemon(Scanner sc) {
        System.out.print("Name to release: ");
        releasePokemon(sc.nextLine().trim());
        boolean removed = lineup.removeIf(p -> p.getName().equalsIgnoreCase(name));
        if (!removed) {
            removed = storage.removeIf(p -> p.getName().equalsIgnoreCase(name));
        }
    }

    /**
     * Prompts for a Pokémon and move, checks compatibility and move limit,
     * and handles forgetting moves if needed.
     *
     * @param sc Scanner for user input
     * @param mm MoveManager to retrieve move objects
     */
    public void teachMove(Scanner sc, MoveManager mm) {
        Pokemon p = choosePokemon(sc);
        if (p == null) return;

        System.out.print("Move to teach: ");
        Move m = mm.getMoveByName(sc.nextLine().trim());

        if (m == null) {
            System.out.println("⚠️ Move not found.");
            return;
        }

        String result = teachMove(p, m, mm);

        if (result.equals("Needs to forget a move first")) {
            System.out.println("❗ " + result);
            System.out.println("Current moves:");
            List<String> moves = p.getMoveSet();
            for (int i = 0; i < moves.size(); i++) {
                System.out.printf("[%d] %s\n", i + 1, moves.get(i));
            }

            int choice = -1;
            while (choice < 0 || choice > moves.size()) {
                System.out.print("Choose a move to forget (0 to cancel, 1-" + moves.size() + "): ");
                try {
                    choice = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Invalid number.");
                }
            }

            if (choice == 0) {
                System.out.println("❌ Move teaching canceled.");
                return;
            }

            String moveToForget = moves.get(choice - 1);

            System.out.print("Are you sure you want " + p.getName() + " to forget " + moveToForget + " and learn " + m.getName() + "? (Y/N): ");
            String confirm = sc.nextLine().trim().toLowerCase();
            if (!confirm.equals("y")) {
                System.out.println("❌ Move teaching canceled.");
                return;
            }

            if (forgetAndLearnMove(p, moveToForget, m, mm)) {
                System.out.println("✅ " + p.getName() + " forgot " + moveToForget + " and learned " + m.getName());
            } else {
                System.out.println("❌ Failed to learn move. HM moves cannot be forgotten.");
            }

        } else {
            System.out.println(result); // Shows success or error from teachMove logic
        }
    }


    /**
     * Prompts the user to choose a Pokémon from lineup or storage.
     *
     * @param sc Scanner for input
     * @return selected Pokémon or null if cancelled/invalid
     */
    private Pokemon choosePokemon(Scanner sc) {
        List<Pokemon> all = new ArrayList<>();
        System.out.println("Select a Pokémon:");
        for (Pokemon p : lineup) {
            all.add(p);
            System.out.printf("%d. %s (Lineup)%n", all.size(), p.getName());
        }
        for (Pokemon p : storage) {
            all.add(p);
            System.out.printf("%d. %s (Storage)%n", all.size(), p.getName());
        }
        try {
            System.out.print("Enter #: ");
            int idx = Integer.parseInt(sc.nextLine())-1;
            return (idx>=0 && idx<all.size()) ? all.get(idx) : null;
        } catch (Exception e) {
            System.out.println("⚠️ Invalid number.");
            return null;
        }
    }

    /**
     * Applies the effect of an item (e.g., Rare Candy, Vitamin, Evolution Stone) on a Pokémon.
     * Determines the item category and delegates to the correct helper method.
     *
     * @param item the item to use
     * @param target the Pokémon to use the item on
     * @param pokedexManager reference for evolution rules and Pokédex data
     * @param scanner used for any additional input (unused)
     */
    public void useItem(Item item, Pokemon target, PokedexManager pokedexManager, Scanner scanner) {
        System.out.println(name + " used " + item.getName() + " on " + target.getName());
        String itemName = item.getName().toLowerCase();
        // Valid evolution stones that might not be labeled as "Evolution Stone" category
        List<String> evolutionStones = List.of(
                "ice stone", "fire stone", "water stone", "thunder stone",
                "leaf stone", "moon stone", "sun stone", "shiny stone",
                "dusk stone", "dawn stone"
        );

        if (item.getCategory().equalsIgnoreCase("Vitamin") || item.getCategory().equalsIgnoreCase("Feather")) {
            applyVitaminEffect(item, target);
        } else if (item.getName().equalsIgnoreCase("Rare Candy")) {
            applyRareCandyEffect(target, pokedexManager);
        } else if (item.getCategory().equalsIgnoreCase("Evolution Stone")) {
            System.out.println("✅ REACHED item.getCategory() == Evolution Stone");
            applyEvolutionStoneEffect(item, target, pokedexManager);
        } else if (evolutionStones.contains(itemName)) {
            System.out.println("✅ REACHED evolutionStones.contains(itemName)");
            applyEvolutionStoneEffect(item, target, pokedexManager);
        } else {
            System.out.println("⚠️ Item has no effect.");
        }
    }

    /**
     * Uses the Pokémon's held item (if any) and applies its effect.
     * Held item is removed after use.
     *
     * @param target the Pokémon using its held item
     * @param pokedexManager reference for evolution rules
     * @return status message (e.g., effect applied or no effect)
     */
    public String useHeldItem(Pokemon target, PokedexManager pokedexManager) {
        if (target == null || target.getHeldItem() == null) return "⚠️ No held item found.";

        Item heldItem = target.getHeldItem();
        String result;

        if (heldItem.getName().equalsIgnoreCase("Rare Candy")) {
            result = applyRareCandyEffect(target, pokedexManager);
        } else if (heldItem.getCategory().equalsIgnoreCase("Vitamin") || heldItem.getCategory().equalsIgnoreCase("Feather")) {
            applyVitaminEffect(heldItem, target);
            result = heldItem.getName() + " took effect! " + target.getName() + "'s stats improved.";
        } else {
            result = heldItem.getName() + " had no effect.";
        }

        target.setHeldItem(null); // Remove held item after use
        return result;
    }

    /**
     * Applies the effect of a vitamin or feather to a Pokémon.
     * Boosts the corresponding stat by +10 (vitamin) or +1 (feather).
     *
     * @param item the item used
     * @param target the Pokémon receiving the stat boost
     */
    public void applyVitaminEffect(Item item, Pokemon target) {
        String effect = item.getEffects().toLowerCase();
        int amount = item.getCategory().equalsIgnoreCase("Feather") ? 1 : 10; // ← Decide based on category

        if (effect.contains("hp")) {
            target.increaseHp(amount);
        } else if (effect.contains("attack")) {
            target.increaseAttack(amount);
        } else if (effect.contains("defense")) {
            target.increaseDefense(amount);
        } else if (effect.contains("speed")) {
            target.increaseSpeed(amount);
        } else {
            System.out.println("⚠️ Unknown vitamin or feather effect.");
        }
    }

    /**
     * Applies the effect of a Rare Candy to level up a Pokémon by 1.
     * Increases all stats by 10% and checks if the Pokémon can evolve.
     *
     * @param target the Pokémon to level up
     * @param pokedexManager reference for evolution data
     * @return evolution-ready message with updated stats
     */
    public String applyRareCandyEffect(Pokemon target, PokedexManager pokedexManager) {
        if (!pokedexManager.getAllowedStoneEvolutionNames()
                .contains(target.getName().toLowerCase())) {
            return target.getName() + " is not allowed to evolve.";
        }

        int oldLevel = target.getBaseLevel();
        int newLevel = oldLevel + 1;
        target.setBaseLevel(newLevel);

        // Apply 10% stat boost
        target.setHp((int) Math.round(target.getHp() * 1.1));
        target.setAttack((int) Math.round(target.getAttack() * 1.1));
        target.setDefense((int) Math.round(target.getDefense() * 1.1));
        target.setSpeed((int) Math.round(target.getSpeed() * 1.1));

        StringBuilder log = new StringBuilder();
        log.append(target.getName())
                .append(" leveled up from ").append(oldLevel)
                .append(" to ").append(newLevel).append(". Base stats increased by 10%.\n");

        Integer evoLevel = target.getEvolutionLevel();
        Integer evolvesTo = target.getEvolvesTo();

        if (evoLevel != null && newLevel >= evoLevel && evolvesTo != null) {
            log.append(target.getName())
                    .append(" can now evolve (Evolution Level: ")
                    .append(evoLevel).append(").\n");
            log.append("[EVOLUTION_PROMPT]");  // Signal for evolution popup
        }

        return log.toString();
    }

    /**
     * Evolves a Pokémon using the specified evolution stone,
     * if the stone is valid and the evolution is allowed.
     * Updates all applicable Pokémon properties.
     *
     * @param item the evolution stone being used
     * @param target the Pokémon to evolve
     * @param pokedexManager reference for allowed evolutions and evolution result
     */
    private void applyEvolutionStoneEffect(Item item, Pokemon target, PokedexManager pokedexManager) {
        String stoneUsed = item.getName(); // e.g., "Fire Stone", "Water Stone"
        String targetNameLower = target.getName().toLowerCase();

        // Check if Pokémon is allowed to evolve via stone
        if (!pokedexManager.getAllowedStoneEvolutionNames().contains(targetNameLower)) {
            System.out.println("⚠️ " + target.getName() + " cannot evolve using an evolution stone.");
            return;
        }

        // Check if the stone used is the correct one
        if (!pokedexManager.isCorrectStoneForEvolution(target.getName(), stoneUsed)) {
            System.out.println("⚠️ " + stoneUsed + " cannot be used to evolve " + target.getName() + ".");
            return;
        }

        // Check for evolution target
        Integer evolvesTo = target.getEvolvesTo();
        if (evolvesTo == null) {
            System.out.println("⚠️ " + target.getName() + " has no evolution via stone.");
            return;
        }

        // Retrieve evolved form
        Pokemon evolvedForm = pokedexManager.getPokemonByNumber(evolvesTo);
        if (evolvedForm == null) {
            System.out.println("⚠️ Evolution data not found in Pokédex.");
            return;
        }

        // Apply evolution and stat updates
        System.out.println("🎉 " + target.getName() + " is evolving into " + evolvedForm.getName() + "!");
        target.setName(evolvedForm.getName());
        target.setPokedexNumber(evolvedForm.getPokedexNumber());
        target.setType1(evolvedForm.getType1());
        target.setType2(evolvedForm.getType2());
        // Keep the higher value between current and evolved form stats
        target.setHp(Math.max(target.getHp(), evolvedForm.getHp()));
        target.setAttack(Math.max(target.getAttack(), evolvedForm.getAttack()));
        target.setDefense(Math.max(target.getDefense(), evolvedForm.getDefense()));
        target.setSpeed(Math.max(target.getSpeed(), evolvedForm.getSpeed()));
        // Support possible future evolutions
        target.setEvolvesTo(evolvedForm.getEvolvesTo());
        target.setEvolutionLevel(evolvedForm.getEvolutionLevel());

        System.out.println("✅ Evolution complete. Stats updated (higher values applied only)!");
    }


    /**
     * Displays all profile information for the trainer, including:
     * - Personal details
     * - Pokémon in lineup and storage
     * - Items held
     */
    public void displayProfile() {
        System.out.println("\n--- Trainer Profile: " + name + " ---");
        System.out.println("Sex        : " + sex);
        System.out.println("Hometown   : " + hometown);
        System.out.println("Description: " + description); // Optional but encouraged
        System.out.printf("Money      : ₱%,d%n", money);

        System.out.println("\n--- Lineup (" + lineup.size() + "/6) ---");
        if (lineup.isEmpty()) {
            System.out.println("  (None)");
        } else {
            int index = 1;
            for (Pokemon p : lineup) {
                System.out.printf("%d. %s%n", index++, p.briefProfile());
            }
        }

        System.out.println("\n--- Storage (" + storage.size() + " Pokémon) ---");
        if (storage.isEmpty()) {
            System.out.println("  (None)");
        } else {
            for (Pokemon p : storage) {
                System.out.println("- " + p.getName() + " (#" + String.format("%03d", p.getPokedexNumber()) + ")");
            }
        }

        System.out.println("\n--- Inventory ---");
        if (itemBag.isEmpty()) {
            System.out.println("  (Empty)");
        } else {
            for (BagItem bagItem : itemBag.values()) {
                Item item = bagItem.getItem(); // assuming BagItem holds the actual Item object
                int quantity = bagItem.getQuantity();

                System.out.println(item.getName() + " × " + quantity);
            }
        }
    }

    /**
     * @return A compact string representation of the trainer for list views.
     */
    @Override
    public String toString() {
        return String.format("%s | %s | %s | ₱%,d",
                trainerID, name, hometown, money);
    }


}