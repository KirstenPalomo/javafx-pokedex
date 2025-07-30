
package pokedex.managers;

import pokedex.JsonManager;
import pokedex.models.Pokemon;
import java.util.ArrayList;
import java.util.List;
/**
 * Handles storage, retrieval, and searching of Pokemon objects in the Pokedex system.
 * Implements the singleton pattern to ensure a single shared instance.
 * Provides methods for adding new Pokemon, searching by name/type/number,
 * and determining evolution logic such as valid stone evolutions.
 *
 * Used by both CLI and GUI components to access and manage the Pokedex data.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
public class PokedexManager {
    private static final PokedexManager INSTANCE = new PokedexManager();

    // Singleton instance
    private static PokedexManager instance;

    /**
     * Private constructor. Loads Pokemon from JSON file or initializes an empty list.
     */
    private PokedexManager() {
        pokedex = JsonManager.loadPokemons(); // load from file
        if (pokedex == null) pokedex = new ArrayList<>(); // fallback if file doesn't exist
    }


    /**
     * Returns the singleton instance of the PokedexManager.
     *
     * @return Shared instance of PokedexManager
     */
    public static PokedexManager getInstance() {
        if (instance == null) {
            instance = new PokedexManager();
        }
        return instance;

    }

    // Internal list of pokemon
    private List<Pokemon> pokedex = new ArrayList<>();

    // Allowed Pokemon names for stone-based evolution
    private static final List<String> ALLOWED_STONE_EVOLUTION_NAMES = List.of(
            "pikachu", "vulpix", "growlithe", "togetic", "eevee"
    );

    /**
     * Returns a list of Pokemon names that are allowed to evolve via stones.
     *
     * @return List of names
     */
    public List<String> getAllowedStoneEvolutionNames() {
        return ALLOWED_STONE_EVOLUTION_NAMES;
    }

    /**
     * Checks if the stone used is correct for evolving the given Pokemon.
     *
     * @param pokemonName Name of the Pokemon
     * @param stoneUsed Name of the stone
     * @return True if the stone can evolve the Pokemon, false otherwise
     */
    public boolean isCorrectStoneForEvolution(String pokemonName, String stoneUsed) {
        String name = pokemonName.toLowerCase();
        String stone = stoneUsed.toLowerCase();

        return switch (name) {
            case "pikachu" -> stone.contains("thunder");
            case "vulpix" -> stone.contains("fire");
            case "growlithe" -> stone.contains("fire");
            case "togetic" -> stone.contains("shiny");
            case "eevee" -> stone.contains("water");
            default -> false;
        };
    }


    /**
     * Adds a Pokemon to the Pokedex if no duplicate by name or number exists.
     *
     * @param p Pokemon to add
     * @return True if added successfully, false if duplicate
     */
    public boolean addPokemon(Pokemon p) {
        for (Pokemon existing : pokedex) {
            if (existing.getPokedexNumber() == p.getPokedexNumber() ||
                    existing.getName().equalsIgnoreCase(p.getName())) {
                return false;
            }
        }
        pokedex.add(p);
        return true;
    }

    /**
     * Prints all Pokemon in the Pokedex to the console.
     * Displays a message if the list is empty.
     */
    public void viewAll() {
        if (pokedex.isEmpty()) {
            System.out.println("Pokedex is empty.");
            return;
        }
        for (Pokemon p : pokedex) {
            System.out.println(p);
        }
    }

    /**
     * Searches for Pokemon by name (case-insensitive, partial match).
     *
     * @param name Name or keyword
     * @return List of matching Pokemon
     */
    public List<Pokemon> searchByName(String name) {
        List<Pokemon> found = new ArrayList<>();
        String keyword = name.toLowerCase();

        for (Pokemon p : pokedex) {
            if (p.getName().toLowerCase().contains(keyword)) {
                found.add(p);
            }
        }
        return found;
    }

    /**
     * Searches for Pokemon by type (matches type1 or type2).
     *
     * @param type Type to search (e.g., Fire, Water)
     * @return List of matching Pokemon
     */
    public List<Pokemon> searchByType(String type) {
        List<Pokemon> found = new ArrayList<>();
        String keyword = type.toLowerCase();

        for (Pokemon p : pokedex) {
            if (p.getType1().toLowerCase().equals(keyword) ||
                    (p.getType2() != null && p.getType2().toLowerCase().equals(keyword))) {
                found.add(p);
            }
        }
        return found;
    }

    /**
     * Returns a Pokemon based on its Pokedex number.
     *
     * @param number Pokedex number
     * @return Pokemon if found, null otherwise
     */
    public Pokemon getByPokedexNumber(int number) {
        for (Pokemon p : pokedex) {
            if (p.getPokedexNumber() == number) {
                return p;
            }
        }
        return null;
    }

    /**
     * Searches and prints a Pokemon by number. Used for CLI display.
     *
     * @param number Pokedex number
     */
    public void searchByNumber(int number) {
        for (Pokemon p : pokedex) {
            if (p.getPokedexNumber() == number) {
                System.out.println("Pokemon found:\n\n" + p);
                return;
            }
        }
        System.out.println("No Pokemon found with Pokedex #" + number);
    }

    /**
     * Checks if a Pokemon with the given Pokedex number exists.
     *
     * @param number Pokedex number
     * @return True if exists, false otherwise
     */
    public boolean hasPokemonWithNumber(int number) {
        for (Pokemon p : pokedex) {
            if (p.getPokedexNumber() == number) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a Pokemon by its Pokedex number.
     *
     * @param number Pokedex number
     * @return Pokemon object or null
     */
    public Pokemon getPokemonByNumber(int number) {
        for (Pokemon p : pokedex) {
            if (p.getPokedexNumber() == number) {
                return p;
            }
        }
        return null;
    }

    /**
     * Checks if a Pokemon with the given name exists (case-insensitive).
     *
     * @param name Pokemon name
     * @return True if found, false otherwise
     */
    public boolean hasPokemonWithName(String name) {
        for (Pokemon p : pokedex) {
            if (p.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a Pokemon object by its name (case-insensitive).
     *
     * @param name Name of the Pokemon
     * @return Pokemon object or null
     */
    public Pokemon getPokemonByName(String name) {
        for (Pokemon p : pokedex) {
            if (p.getName().equalsIgnoreCase(name.trim())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Replaces the current Pokedex list with a new list (used during loading).
     *
     * @param loaded List of Pokemon to load
     */
    public void setAllPokemon(List<Pokemon> loaded) {
        pokedex.clear();
        pokedex.addAll(loaded);
    }

    /**
     * Returns a deduplicated list of all Pokemon in the Pokedex.
     *
     * @return List of unique Pokemon
     */
    public List<Pokemon> getAllPokemon() {
        List<Pokemon> uniqueList = new ArrayList<>();

        for (Pokemon p : pokedex) {
            boolean alreadyExists = false;

            for (Pokemon existing : uniqueList) {
                if (existing.getPokedexNumber() == p.getPokedexNumber()
                        && existing.getName().equalsIgnoreCase(p.getName())) {
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                uniqueList.add(p);
            }
        }

        return uniqueList;
    }
}