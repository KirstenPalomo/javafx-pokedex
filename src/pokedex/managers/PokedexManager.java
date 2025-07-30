/**
 * Handles storage, retrieval, and searching of Pokémon objects in the Pokédex system.
 * Implements the singleton pattern to ensure a single shared instance.
 * Provides methods for adding new Pokémon, searching by name/type/number,
 * and determining evolution logic such as valid stone evolutions.
 *
 * Used by both CLI and GUI components to access and manage the Pokédex data.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.managers;

import pokedex.JsonManager;
import pokedex.models.Pokemon;
import java.util.ArrayList;
import java.util.List;

public class PokedexManager {
    private static final PokedexManager INSTANCE = new PokedexManager();

    // Singleton instance
    private static PokedexManager instance;

    /**
     * Private constructor. Loads Pokémon from JSON file or initializes an empty list.
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

    // Allowed Pokémon names for stone-based evolution
    private static final List<String> ALLOWED_STONE_EVOLUTION_NAMES = List.of(
            "pikachu", "vulpix", "growlithe", "togetic", "eevee"
    );

    /**
     * Returns a list of Pokémon names that are allowed to evolve via stones.
     *
     * @return List of names
     */
    public List<String> getAllowedStoneEvolutionNames() {
        return ALLOWED_STONE_EVOLUTION_NAMES;
    }

    /**
     * Checks if the stone used is correct for evolving the given Pokémon.
     *
     * @param pokemonName Name of the Pokémon
     * @param stoneUsed Name of the stone
     * @return True if the stone can evolve the Pokémon, false otherwise
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
     * Adds a Pokémon to the Pokédex if no duplicate by name or number exists.
     *
     * @param p Pokémon to add
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
     * Prints all Pokémon in the Pokédex to the console.
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
     * Searches for Pokémon by name (case-insensitive, partial match).
     *
     * @param name Name or keyword
     * @return List of matching Pokémon
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
     * Searches for Pokémon by type (matches type1 or type2).
     *
     * @param type Type to search (e.g., Fire, Water)
     * @return List of matching Pokémon
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
     * Returns a Pokémon based on its Pokédex number.
     *
     * @param number Pokédex number
     * @return Pokémon if found, null otherwise
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
     * Searches and prints a Pokémon by number. Used for CLI display.
     *
     * @param number Pokédex number
     */
    public void searchByNumber(int number) {
        for (Pokemon p : pokedex) {
            if (p.getPokedexNumber() == number) {
                System.out.println("Pokémon found:\n\n" + p);
                return;
            }
        }
        System.out.println("No Pokémon found with Pokédex #" + number);
    }

    /**
     * Checks if a Pokémon with the given Pokédex number exists.
     *
     * @param number Pokédex number
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
     * Retrieves a Pokémon by its Pokédex number.
     *
     * @param number Pokédex number
     * @return Pokémon object or null
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
     * Checks if a Pokémon with the given name exists (case-insensitive).
     *
     * @param name Pokémon name
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
     * Retrieves a Pokémon object by its name (case-insensitive).
     *
     * @param name Name of the Pokémon
     * @return Pokémon object or null
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
     * Replaces the current Pokédex list with a new list (used during loading).
     *
     * @param loaded List of Pokémon to load
     */
    public void setAllPokemon(List<Pokemon> loaded) {
        pokedex.clear();
        pokedex.addAll(loaded);
    }

    /**
     * Returns a deduplicated list of all Pokémon in the Pokédex.
     *
     * @return List of unique Pokémon
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