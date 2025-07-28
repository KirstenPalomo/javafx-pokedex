package pokedex.models;

import pokedex.managers.ItemManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.MoveManager;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Represents a Pokémon Trainer with lineup, storage, item bag, and funds.
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

    public static class BagItem implements Serializable {
        final Item item;
        int quantity;

        public BagItem(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public Item getItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }
        public void decrement()
        {
            quantity--;
        }
    }

    /** Default constructor—₱1,000,000 starting money. */
    public Trainer(String trainerID,
                   String name,
                   LocalDate birthdate,
                   String sex,
                   String hometown,
                   String description) {
        this(trainerID, name, birthdate, sex, hometown, description, 1_000_000);
    }

    /** Full constructor with custom starting funds. */
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
    public String getTrainerID()    { return trainerID; }
    public String getName()         { return name; }
    public LocalDate getBirthdate() { return birthdate; }
    public String getSex()          { return sex; }
    public String getHometown()     { return hometown; }
    public String getDescription()  { return description; }
    public int getMoney()           { return money; }
    public List<Pokemon> getLineup()  { return Collections.unmodifiableList(lineup); }
    public List<Pokemon> getStorage() { return Collections.unmodifiableList(storage); }
    public Map<String, BagItem> getItemBag() { return itemBag; }


    // ── Core Item Logic ──────────────────────────────────────────────────────

    /** Buys an item, enforcing money and bag limits. Also gives 1 Rare Candy if within limits. */
    public String buyItem(Item item) {
        String nm = item.getName();
        Integer priceObj = item.getMinBuyingPrice();
        if (priceObj == null) {
            return "⚠️ Item is not sold and cannot be bought.";
        }
        int price = priceObj;

        if (money < price) {
            return "ERROR: Not enough money.";
        }

        int totalCount = itemBag.values().stream().mapToInt(b -> b.quantity).sum();

        // Check if adding the bought item would exceed unique or total limits
        boolean isNewItem = !itemBag.containsKey(nm);
        if (isNewItem && itemBag.size() >= 10) {
            return "⚠️ Max 10 unique items.";
        }

        if (totalCount >= 50) {
            return "⚠️ Max 50 items total.";
        }

        // Deduct money and add the bought item
        money -= price;
        itemBag.putIfAbsent(nm, new BagItem(item, 0));
        itemBag.get(nm).quantity++;

        // 🎁 Attempt to give 1 Rare Candy
        Item rareCandy = ItemManager.getInstance().getItemByName("Rare Candy");
        if (rareCandy != null) {
            boolean newCandy = !itemBag.containsKey("Rare Candy");
            if ((newCandy && itemBag.size() >= 10) || (totalCount + 1 > 50)) {
                return "SUCCESS: Bought " + nm + " for ₱" + price + "\n⚠️ Bonus Rare Candy could not be given (item limit reached).";
            }
            itemBag.putIfAbsent("Rare Candy", new BagItem(rareCandy, 0));
            itemBag.get("Rare Candy").quantity++;
            return "SUCCESS: Bought " + nm + " for ₱" + price + "\n🎁 Bonus: Received 1 Rare Candy!";
        }

        return "SUCCESS: Bought " + nm + " for ₱" + price;
    }


    /** Sells one item, refunding 50% of its base price. */
    public void sellItem(String itemName) {
        BagItem bag = itemBag.get(itemName);
        if (bag == null) {
            System.out.println("⚠️ You don’t have that item.");
            return;
        }

        Integer baseObj = bag.item.getMinBuyingPrice();
        if (baseObj == null) {
            System.out.println("⚠️ Item cannot be sold (Not sold in shops).");
            return;
        }
        int base = baseObj;

        int refund = base / 2;
        money += refund;
        bag.quantity--;
        if (bag.quantity == 0) itemBag.remove(itemName);
        System.out.printf("✅ Sold %s for ₱%,d%n", itemName, refund);
    }

    /** Displays the contents of the item bag. */
    public void viewBag() {
        System.out.println("=== Item Bag ===");
        if (itemBag.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        itemBag.values().forEach(b ->
                System.out.printf("- %s x%d%n", b.item.getName(), b.quantity)
        );
        int total = itemBag.values().stream().mapToInt(b->b.quantity).sum();
        System.out.printf("Total: %d/50 items, %d/10 unique%n",
                total, itemBag.size());
    }

    // ── Core Pokémon Logic ──────────────────────────────────────────────────

    /** Adds a Pokémon to lineup if <6, else to storage. */
    public boolean addPokemon(Pokemon p) {
        // Check for duplicate in lineup
        for (Pokemon existing : lineup) {
            if (existing.getName().equalsIgnoreCase(p.getName())) {
                System.out.println("⚠️ " + p.getName() + " is already in the lineup.");
                return true; // Still return true to avoid sending it to storage
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

    /** Switches a lineup Pokémon with a storage Pokémon by index. */
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

    /** Releases a Pokémon by name. */
    public void releasePokemon(String pokeName) {
        boolean removed = lineup.removeIf(p -> p.getName().equalsIgnoreCase(pokeName));
        if (!removed) removed = storage.removeIf(p -> p.getName().equalsIgnoreCase(pokeName));
        System.out.println(removed ? "✅ Released " + pokeName : "⚠️ Pokémon not found.");
    }

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
            return "PROMPT_FORGET";
        }

        // All good — add the move
        moves.add(mName);
        return target.getName() + " learned " + mName + (isHM ? " (HM)." : "!");
    }

    /**
 * Forgets `forgetMove` (if TM) and learns `newMove` in its place.
 * @return true if swap succeeded.
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

    /** Prompts for and buys an item via ItemManager. */
    public void buyItem(Scanner sc, ItemManager im) {
        while (true) {
            System.out.println("\n=== Buy Item ===");
            im.viewAllItems(); // Optional: Show list of items
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

    /** Prompts for and sells an item interactively. */
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


    /** Prompts for and uses an item on a chosen Pokémon. */
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


    /** Prompts for and adds a Poké from the Pokédex. */
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

    /** Prompts for and switches lineup↔storage. */
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


    /** Prompts for and releases a Pokémon by name. */
    public void releasePokemon(Scanner sc) {
        System.out.print("Name to release: ");
        releasePokemon(sc.nextLine().trim());
        boolean removed = lineup.removeIf(p -> p.getName().equalsIgnoreCase(name));
        if (!removed) {
            removed = storage.removeIf(p -> p.getName().equalsIgnoreCase(name));
        }
    }

    /** Prompts for and teaches a new move via MoveManager. */
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


    /** Helper to pick a Pokémon from lineup+storage. */
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

    public void useItem(Item item, Pokemon target, PokedexManager pokedexManager, Scanner scanner) {
        System.out.println(name + " used " + item.getName() + " on " + target.getName());
        String itemName = item.getName().toLowerCase();
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

        target.setHeldItem(null);
        return result;
    }


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

    public String applyRareCandyEffect(Pokemon target, PokedexManager pokedexManager) {
        if (!pokedexManager.getAllowedStoneEvolutionNames()
                .contains(target.getName().toLowerCase())) {
            return target.getName() + " is not allowed to evolve.";
        }

        int oldLevel = target.getBaseLevel();
        int newLevel = oldLevel + 1;
        target.setBaseLevel(newLevel);

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
            log.append("[EVOLUTION_PROMPT]");  // ← special flag
        }

        return log.toString();
    }


    private void applyEvolutionStoneEffect(Item item, Pokemon target, PokedexManager pokedexManager) {
        String stoneUsed = item.getName(); // e.g., "Fire Stone", "Water Stone"
        String targetNameLower = target.getName().toLowerCase();

        // ✅ Check if Pokémon is allowed to evolve via stone
        if (!pokedexManager.getAllowedStoneEvolutionNames().contains(targetNameLower)) {
            System.out.println("⚠️ " + target.getName() + " cannot evolve using an evolution stone.");
            return;
        }

        // ✅ Check if the stone used is the correct one
        if (!pokedexManager.isCorrectStoneForEvolution(target.getName(), stoneUsed)) {
            System.out.println("⚠️ " + stoneUsed + " cannot be used to evolve " + target.getName() + ".");
            return;
        }

        // ✅ Check for evolution target
        Integer evolvesTo = target.getEvolvesTo();
        if (evolvesTo == null) {
            System.out.println("⚠️ " + target.getName() + " has no evolution via stone.");
            return;
        }

        // ✅ Retrieve evolved form
        Pokemon evolvedForm = pokedexManager.getPokemonByNumber(evolvesTo);
        if (evolvedForm == null) {
            System.out.println("⚠️ Evolution data not found in Pokédex.");
            return;
        }

        // ✅ Apply evolution and stat updates
        System.out.println("🎉 " + target.getName() + " is evolving into " + evolvedForm.getName() + "!");
        target.setName(evolvedForm.getName());
        target.setPokedexNumber(evolvedForm.getPokedexNumber());
        target.setType1(evolvedForm.getType1());
        target.setType2(evolvedForm.getType2());
        target.setHp(Math.max(target.getHp(), evolvedForm.getHp()));
        target.setAttack(Math.max(target.getAttack(), evolvedForm.getAttack()));
        target.setDefense(Math.max(target.getDefense(), evolvedForm.getDefense()));
        target.setSpeed(Math.max(target.getSpeed(), evolvedForm.getSpeed()));
        target.setEvolvesTo(evolvedForm.getEvolvesTo()); // In case it can evolve again
        target.setEvolutionLevel(evolvedForm.getEvolutionLevel()); // Optional: support further Rare Candy evolutions

        System.out.println("✅ Evolution complete. Stats updated (higher values applied only)!");
    }


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

    @Override
    public String toString() {
        return String.format("%s | %s | %s | ₱%,d",
                trainerID, name, hometown, money);
    }


}