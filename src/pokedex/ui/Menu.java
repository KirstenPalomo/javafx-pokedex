/**
 * This class serves as the main menu system for the CLI-based Pokédex application.
 * It provides interactive options for the user to manage Pokémon, Moves, Items, and Trainers.
 * The menu allows adding, viewing, and searching various entities using input from the console.
 * This class utilizes manager classes to access and manipulate data.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.ui;

import pokedex.JsonManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.MoveManager;
import pokedex.managers.ItemManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Item;
import pokedex.models.Move;
import pokedex.models.Pokemon;
import pokedex.models.Trainer;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the CLI-based interaction menu for managing all Pokédex entities.
 * Provides options to add, view, search, and manage Trainers, Pokémon, Moves, and Items.
 */
public class Menu {
    private static final boolean ALLOW_EXTRA_MOVES = false;
    private static final List<String> VALID_TYPES = List.of(
            "Normal", "Fire", "Water", "Grass", "Electric", "Ice",
            "Fighting", "Poison", "Ground", "Flying", "Psychic",
            "Bug", "Rock", "Ghost", "Dark", "Dragon", "Steel", "Shiny");

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;
    private final Scanner scanner;

    /**
     * Constructs a new Menu instance with the given managers and scanner.
     *
     * @param pokedexManager   the manager for handling Pokémon-related logic
     * @param moveManager      the manager for handling move-related logic
     * @param itemManager      the manager for handling item-related logic
     * @param trainerManager   the manager for handling trainer-related logic
     * @param scanner          the Scanner object for user input
     */
    public Menu(PokedexManager pokedexManager, MoveManager moveManager, ItemManager itemManager, TrainerManager trainerManager, Scanner scanner) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
        this.scanner = scanner;
    }

    /**
     * Displays the main menu and processes user selections.
     * Includes submenus for managing trainers and saving data upon exit.
     */
    public void display(){
        int choice;
        do {
            System.out.println("\n=== POKEDEX MENU ===");
            System.out.println("1. Add Pokémon");
            System.out.println("2. View All Pokémon");
            System.out.println("3. Search Pokémon");
            System.out.println("4. Add Move");
            System.out.println("5. View All Moves");
            System.out.println("6. Search Move");
            System.out.println("7. View All Items");
            System.out.println("8. Search Item");
            System.out.println("9. Add Trainer");
            System.out.println("10. View All Trainers");
            System.out.println("11. Search Trainers");
            System.out.println("12. Exit");
            System.out.print("Enter choice: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a number: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addPokemonTask();
                    break;
                case 2:
                    viewAllPokemonTask();
                    break;
                case 3:
                    pokemonSearch();
                    break;
                case 4:
                    addMoveTask();
                    break;
                case 5:
                    viewAllMovesTask();
                    break;
                case 6:
                    System.out.print("Enter move name keyword: ");
                    String moveQuery = scanner.nextLine();
                    moveManager.searchMoveByName(moveQuery);
                    break;
                case 7:
                    viewAllItemsTask();
                    break;
                case 8:
                    itemSearch();
                    break;
                case 9:
                    addTrainer();
                    break;
                case 10:
                    List<Trainer> allTrainers = trainerManager.getAllTrainers();
                    if (allTrainers.isEmpty()) {
                        System.out.println("⚠️ No trainers found.");
                        break;
                    }

                    System.out.println("--- All Trainers ---");
                    for (int i = 0; i < allTrainers.size(); i++) {
                        System.out.printf("%d. %s%n", i + 1, allTrainers.get(i));
                    }

                    int selected = readInt("Select a trainer (0 to cancel): ");
                    if (selected < 1 || selected > allTrainers.size()) {
                        System.out.println("❌ Returning to main menu.");
                        break;
                    }

                    Trainer selectedTrainer = allTrainers.get(selected - 1);
                    clearScreen();
                    selectedTrainer.displayProfile();
                    pauseAndReturn();

                    boolean managingTrainer = true;
                    while (managingTrainer) {
                        System.out.println("\n--- Manage Trainer: " + selectedTrainer.getName() + " ---");
                        System.out.println("1. Buy Item");
                        System.out.println("2. Sell Item");
                        System.out.println("3. Use Item on Pokémon");
                        System.out.println("4. Give Item to Pokémon");
                        System.out.println("5. Remove Held Item");
                        System.out.println("6. Add Pokémon to Lineup");
                        System.out.println("7. Switch Pokémon from Storage");
                        System.out.println("8. Release Pokémon");
                        System.out.println("9. Teach Move");
                        System.out.println("10. View Trainer Profile");
                        System.out.println("0. Return to Main Menu");
                        System.out.print("Enter choice: ");

                        int trainerOption = readInt("");
                        switch (trainerOption) {
                            case 1:
                                selectedTrainer.buyItem(scanner, itemManager);
                                break;
                            case 2:
                                selectedTrainer.sellItem(scanner);
                                break;
                            case 3:
                                selectedTrainer.useItem(scanner, pokedexManager);
                                break;
                            case 4:
                                selectedTrainer.giveItemToPokemon(scanner, itemManager);
                                break;
                            case 5:
                                selectedTrainer.removeHeldItem(scanner);
                                break;
                            case 6:
                                selectedTrainer.addToLineup(scanner, pokedexManager);
                                break;
                            case 7:
                                selectedTrainer.switchPokemon(scanner);
                                break;
                            case 8:
                                selectedTrainer.releasePokemon(scanner);
                                break;
                            case 9: {
    //Ask Trainer to pick a Pokémon
    Pokemon target = selectedTrainer.promptSelectPokemon(scanner);
    if (target == null) break;

    //Prompt for move name and look it up
    System.out.print("Move to teach: ");
    String moveName = scanner.nextLine().trim();
    Move move = moveManager.getMoveByName(moveName);
    if (move == null) {
        System.out.println("⚠️ Move not found.");
        break;
    }

    //Attempt to teach
    String result = selectedTrainer.teachMove(target, move, moveManager);
    System.out.println(result);

    //If the move‐set was full, offer to forget one TM and learn the new one
    if (result.contains("full") && result.contains("TM")) {
        System.out.print("Would you like to forget a TM and learn “"
                         + move.getName() + "”? (Y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("y")) {
            // collect only the TM‐classified moves
            List<String> tms = target.getMoveSet().stream()
                .filter(mn -> {
                    Move mm = moveManager.getMoveByName(mn);
                    return mm != null && mm.getClassification().equalsIgnoreCase("TM");
                })
                .toList();

            if (tms.isEmpty()) {
                System.out.println("⚠️ You have no TM moves to forget.");
                break;
            }

            //Show the TM list
            System.out.println("Which TM would you like to forget?");
            for (int i = 0; i < tms.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, tms.get(i));
            }
            System.out.print("Enter number: ");
            int idx;
            try {
                idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid choice.");
                break;
            }

            if (idx < 0 || idx >= tms.size()) {
                System.out.println("⚠️ Invalid choice.");
                break;
            }

            // Perform the swap via forgetAndLearnMove
            boolean ok = selectedTrainer.forgetAndLearnMove(
                target, tms.get(idx), move, moveManager);
            if (ok) {
                System.out.printf("✅ %s forgot %s and learned %s!%n",
                    target.getName(), tms.get(idx), move.getName());
            } else {
                System.out.println("⚠️ Could not swap moves (HM moves are locked).");
            }
        }
    }
    break;
}
                            case 10:
                                clearScreen();
                                selectedTrainer.displayProfile();
                                pauseAndReturn();
                                break;
                            case 0:
                                managingTrainer = false;
                                break;
                            default:
                                System.out.println("⚠️ Invalid choice.");
                        }
                    }
                    break;
                case 11:
                    searchTrainer();
                    break;
                case 12:
                    //save before exit
                    System.out.println("Saving trainers to disk...");

                    JsonManager.saveTrainers(trainerManager.getAllTrainers());
                    JsonManager.savePokemons(pokedexManager.getAllPokemon());
                    JsonManager.saveMoves(moveManager.getAllMoves());
                    JsonManager.saveItems(itemManager.getAllItems());
                    System.out.println("Data saved. Exiting Pokédex. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        } while (choice != 12);
    }

    /**
     * Prompts the user to input information for a new Trainer.
     * Validates inputs for ID, name, birthdate, sex, hometown, and description.
     * Adds the new Trainer to the TrainerManager.
     */
    private void addTrainer() {
        System.out.println("--- Add Trainer ---");
        // Trainer ID (Numbers only, cannot be empty, no duplicates)
        String id;
        while (true) {
            System.out.print("Trainer ID (numbers only): ");
            id = scanner.nextLine().trim();
            if (id.isEmpty()) {
                System.out.println("⚠️ Trainer ID cannot be blank.");
                continue;
            }
            if (!id.matches("\\d+")) {
                System.out.println("⚠️ Trainer ID must be numbers only.");
                continue;
            }
            if (trainerManager.hasTrainerWithID(id)) {
                System.out.println("⚠️ Trainer ID already exists.");
                continue;
            }
            break;
        }
        // Name
        String name;
        while (true) {
            System.out.print("Name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("⚠️ Name cannot be blank.");
                continue;
            }
            break;
        }

        // Birthdate (must be a valid date)
        LocalDate birthdate;
        while (true) {
            System.out.print("Birthdate (yyyy-mm-dd): ");
            String bdayInput = scanner.nextLine().trim();
            try {
                birthdate = LocalDate.parse(bdayInput);
                break; // Valid date, exit loop
            } catch (Exception e) {
                System.out.println("⚠️ Invalid date format. Please use yyyy-mm-dd.");
            }
        }

        // Sex (M/F only)
        String sex;
        while (true) {
            System.out.print("Sex (M/F): ");
            sex = scanner.nextLine().trim().toUpperCase();
            if (!(sex.equals("M") || sex.equals("F"))) {
                System.out.println("⚠️ Invalid sex. Enter M or F only.");
                continue;
            }
            break;
        }
        // Hometown
        String town;
        while (true) {
            System.out.print("Hometown: ");
            town = scanner.nextLine().trim();
            if (town.isEmpty()) {
                System.out.println("⚠️ Hometown cannot be blank.");
                continue;
            }
            break;
        }

        // Description
        String desc;
        while (true) {
            System.out.print("Description: ");
            desc = scanner.nextLine().trim();
            if (desc.isEmpty()) {
                System.out.println("⚠️ Description cannot be blank.");
                continue;
            }
            break;
        }

        Trainer t = new Trainer(id, name, birthdate, sex, town, desc, 1_000_000);
        trainerManager.addTrainer(t);
    }

    /**
     * Prompts the user to enter a keyword and performs a trainer search using the TrainerManager.
     */
    private void searchTrainer() {
        System.out.print("Enter trainer keyword: ");
        String keyword = scanner.nextLine();
        trainerManager.searchTrainer(keyword);
    }

    /**
     * Formats a string to proper Pokémon type format (capitalized first letter).
     *
     * @param input the raw type input
     * @return a properly formatted type string or null if invalid
     */
    private String formatType(String input) {
        if (input == null || input.isBlank()) return null;
        input = input.trim().toLowerCase();
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Prompts the user to input all fields required to create and add a new Pokémon.
     * Includes validation, optional evolution details, and confirmation before adding.
     * Adds the Pokémon to the PokédexManager.
     */
    private void addPokemonTask() {
        System.out.println("--- Add New Pokémon ---");

        int pokedexNumber;
        while (true) {
            pokedexNumber = readInt("Pokedex Number: ");
            if (pokedexManager.hasPokemonWithNumber(pokedexNumber)) {
                System.out.println("⚠️ Pokémon with this Pokedex number already exists.");
            } else {
                break;
            }
        }

        String name;
        while (true) {
            System.out.print("Name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("⚠️ Name cannot be empty.");
                continue;
            }
            if (pokedexManager.hasPokemonWithName(name)) {
                System.out.println("⚠️ Pokémon with this name already exists.");
                continue;
            }
            break;
        }

        // TYPE 1
        String type1;
        while (true) {
            System.out.println("Options for Type 1 and Type 2: \n Normal, Fire, Water, Grass, Electric, Ice, Fighting, Poison, Ground,\n Flying, Psychic, Bug, Rock, Ghost, Dark, Dragon, Steel, Fairy");
            System.out.print("Type 1: ");
            type1 = formatType(scanner.nextLine());
            if (type1 == null || !VALID_TYPES.contains(type1)) {
                System.out.println("⚠️ Invalid Type 1. Please enter a valid Pokémon type.");
            } else {
                break;
            }
        }

        // TYPE 2 (Optional)
        String type2;
        while (true) {
            System.out.print("Type 2 (optional): ");
            type2 = formatType(scanner.nextLine());
            if (type2 == null) {
                type2 = null;
                break;
            } else if (!VALID_TYPES.contains(type2)) {
                System.out.println("⚠️ Invalid Type 2. Please enter a valid Pokémon type or leave blank.");
            } else {
                break;
            }
        }

        int baseLevel = readInt("Base Level: ");
        Integer evolvesFrom = readOptionalInt("Evolves From (Pokedex Number, -1 if none): ");
        Integer evolvesTo = readOptionalInt("Evolves To (Pokedex Number, -1 if none): ");
        Integer evolutionLevel = readOptionalInt("Evolution Level: ", true);

        int hp = readInt("HP: ");
        int attack = readInt("Attack: ");
        int defense = readInt("Defense: ");
        int speed = readInt("Speed: ");

        List<String> moveSet = new ArrayList<>();
        moveSet.add("Tackle");
        moveSet.add("Defend");

        if (ALLOW_EXTRA_MOVES) {
            System.out.print("Extra moves (comma-separated, or leave blank): ");
            String extra = scanner.nextLine().trim();
            if (!extra.isEmpty()) {
                for (String move : extra.split(",")) {
                    String trimmed = move.trim();
                    if (!trimmed.isEmpty()) moveSet.add(trimmed);
                }
            }
        } else {
            System.out.println("(Extra moves are not allowed since you don't have a trainer yet — only default moves applied.)");
        }

        //Confirmation before adding the Pokémon
        String confirm;
        while (true) {
            System.out.print("Confirm adding this Pokémon? (Y/N): ");
            confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("y") || confirm.equals("n")) {
                break;
            } else {
                System.out.println("⚠️ Invalid input. Please enter Y or N.");
            }
        }

        if (confirm.equals("n")) {
            System.out.println("❌ Pokémon was not added to the Pokédex.");
            return; // exit the method early
        }

        //Add to Pokédex only after confirmation
        Pokemon newPokemon = new Pokemon(pokedexNumber, name, type1, type2, baseLevel,
                evolvesFrom, evolvesTo, evolutionLevel, hp, attack, defense, speed, moveSet, null);

        pokedexManager.addPokemon(newPokemon);

        // Cry-out prompt after confirming add
        String response;
        while (true) {
            System.out.print("Would you like " + name + " to cry out? (Y/N): ");
            response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("y")) {
                System.out.print(name + " says: ");
                newPokemon.cry();
                break;
            } else if (response.equals("n")) {
                break;
            } else {
                System.out.println("⚠️ Please enter Y or N.");
            }
        }
    }

    /**
     * Handles the addition of a new Pokémon move through console input.
     * Validates name, description, classification, and types, and confirms with the user before adding.
     */
    public void addMoveTask() {
        System.out.println("--- Add New Move ---");

        // NAME
        String name;
        while (true) {
            System.out.print("Name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("⚠️ Move name cannot be empty.");
                continue;
            }
            if (moveManager.hasMoveWithName(name)) {
                System.out.println("⚠️ Move with this name already exists.");
                continue;
            }
            break;
        }

        // DESCRIPTION
        String description;
        while (true) {
            System.out.print("Description: ");
            description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                System.out.println("⚠️ Description cannot be empty.");
            } else {
                break;
            }
        }

        // CLASSIFICATION
        String classification;
        while (true) {
            System.out.print("Classification (HM/TM): ");
            classification = scanner.nextLine().trim().toUpperCase();

            if (classification.isEmpty()) {
                System.out.println("⚠️ Classification cannot be empty.");
            } else if (!classification.equals("HM") && !classification.equals("TM")) {
                System.out.println("⚠️ Invalid classification. Please enter HM or TM only.");
            } else {
                break;
            }
        }


        // TYPE 1 (Required + validated)
        String type1;
        while (true) {
            System.out.println("Options for Type 1 and Type 2: \n Normal, Fire, Water, Grass, Electric, Ice, Fighting, Poison, Ground,\n Flying, Psychic, Bug, Rock, Ghost, Dark, Dragon, Steel, Fairy");
            System.out.print("Type 1: ");
            type1 = formatType(scanner.nextLine());
            if (type1 == null || !VALID_TYPES.contains(type1)) {
                System.out.println("⚠️ Invalid Type 1. Must be a valid Pokémon type.");
            } else {
                break;
            }
        }

        // TYPE 2 (Optional, but validated if provided)
        String type2;
        while (true) {
            System.out.print("Type 2 (or leave blank if none): ");
            type2 = formatType(scanner.nextLine());
            if (type2 == null) {
                type2 = null;
                break;
            }
            if (!VALID_TYPES.contains(type2)) {
                System.out.println("⚠️ Invalid Type 2. Must be a valid Pokémon type or leave blank.");
            } else {
                break;
            }
        }

        // Confirmation before adding the move
        String confirm;
        while (true) {
            System.out.print("Confirm adding this move? (Y/N): ");
            confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("y") || confirm.equals("n")) {
                break;
            } else {
                System.out.println("⚠️ Invalid input. Please enter Y or N.");
            }
        }

        if (confirm.equals("n")) {
            System.out.println("❌ Move was not added.");
            return; // Exit without adding
        }

        // All valid — proceed
        Move newMove = new Move(name, description, classification, type1, type2);
        moveManager.addMove(newMove);
    }

    /**
     * Displays a submenu for searching Pokémon by name, type, or Pokédex number.
     * Calls corresponding methods from the PokedexManager.
     */
    public void pokemonSearch() {
        System.out.println("\n--- Search Pokémon ---");
        System.out.printf("%-5s %-30s%n", "1.", "By Name");
        System.out.printf("%-5s %-30s%n", "2.", "By Type");
        System.out.printf("%-5s %-30s%n", "3.", "By Pokedex Number");
        System.out.print("Enter choice: ");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1:
                System.out.print("Enter name keyword: ");
                String nameQuery = scanner.nextLine();
                pokedexManager.searchByName(nameQuery);
                break;
            case 2:
                System.out.print("Enter type: ");
                String typeQuery = scanner.nextLine();
                pokedexManager.searchByType(typeQuery);
                break;
            case 3:
                System.out.print("Enter Pokedex number: ");
                int numberQuery = scanner.nextInt();
                scanner.nextLine();
                pokedexManager.searchByNumber(numberQuery);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Displays a submenu for searching items by various filters such as name, effect, category, or price range.
     */
    public void itemSearch(){
        System.out.println("\n--- Search Pokémon Items ---");
        System.out.printf("%-5s %-30s%n", "1.", "By Name");
        System.out.printf("%-5s %-30s%n", "2.", "By Effect");
        System.out.printf("%-5s %-30s%n", "3.", "By Category");
        System.out.printf("%-5s %-30s%n", "4.", "By Buy Price Range");
        System.out.print("Enter choice: ");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1:
                System.out.print("Enter item name keyword: ");
                String nameQuery = scanner.nextLine();
                itemManager.searchItemByName(nameQuery);
                break;
            case 2:
                System.out.print("Enter effect keyword: ");
                String effectQuery = scanner.nextLine();
                itemManager.searchItemByEffect(effectQuery);
                break;
            case 3:
                System.out.print("Enter category: ");
                String category = scanner.nextLine();
                itemManager.searchItemByCategory(category);
                break;
            case 4:
                System.out.print("Enter minimum buy price: ");
                int minPrice = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter maximum buy price: ");
                int maxPrice = scanner.nextInt();
                scanner.nextLine();
                itemManager.searchByItemPriceRange(minPrice, maxPrice);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Reads a required integer input from the user with validation and retry logic.
     * @param prompt the prompt to display
     * @return the valid integer entered
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("⚠️ Input cannot be empty.");
                continue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid input. Enter a valid number.");
            }
        }
    }

    /**
     * Wrapper for readOptionalInt without zero validation.
     * @param prompt prompt to display
     * @return Integer value or null
     */
    private Integer readOptionalInt(String prompt) {
        return readOptionalInt(prompt, false);
    }

    /**
     * Reads an optional integer input; returns null if input is -1.
     * @param prompt the prompt to display
     * @param allowZero whether 0 is a valid input
     * @return the parsed Integer or null
     */
    private Integer readOptionalInt(String prompt, boolean allowZero) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("⚠️ Input cannot be empty.");
                continue;
            }
            try {
                int value = Integer.parseInt(input);
                if (value == -1) return null;
                if (!allowZero && value <= 0) {
                    System.out.println("⚠️ Please enter a positive number or -1.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid input. Enter a valid number.");
            }
        }
    }

    /**
     * Waits for the user to press Enter to continue.
     */
    private void pauseAndReturn() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine(); // Wait for Enter
    }

    /**
     * Clears the console screen (platform-dependent).
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Displays all Pokémon in the Pokédex using the manager.
     */
    private void viewAllPokemonTask() {
        clearScreen();
        pokedexManager.viewAll();
        pauseAndReturn();
    }

    /**
     * Displays all Pokémon moves using the MoveManager.
     */
    private void viewAllMovesTask() {
        clearScreen();
        moveManager.viewAllMoves();
        pauseAndReturn();
    }

    /**
     * Displays all items using the ItemManager.
     */
    private void viewAllItemsTask() {
        clearScreen();
        itemManager.viewAllItems();
        pauseAndReturn();
    }
}