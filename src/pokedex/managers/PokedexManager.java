package pokedex.managers;

import pokedex.JsonManager;
import pokedex.models.Pokemon;
import java.util.ArrayList;
import java.util.List;

public class PokedexManager {
    private static final PokedexManager INSTANCE = new PokedexManager();

    // 🔹 Singleton instance
    private static PokedexManager instance;

    // 🔹 Private constructor to prevent external instantiation
    private PokedexManager() {
        pokedex = JsonManager.loadPokemons(); // load from file
        if (pokedex == null) pokedex = new ArrayList<>(); // fallback if file doesn't exist
    }


    // 🔹 Public method to access the single instance
    public static PokedexManager getInstance() {
        if (instance == null) {
            instance = new PokedexManager();
        }
        return instance;

    }


    private List<Pokemon> pokedex = new ArrayList<>();

    private static final List<String> ALLOWED_STONE_EVOLUTION_NAMES = List.of(
            "pikachu", "vulpix", "growlithe", "togetic", "eevee"
    );

    public List<String> getAllowedStoneEvolutionNames() {
        return ALLOWED_STONE_EVOLUTION_NAMES;
    }

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

    public void viewAll() {
        if (pokedex.isEmpty()) {
            System.out.println("Pokedex is empty.");
            return;
        }
        for (Pokemon p : pokedex) {
            System.out.println(p);
        }
    }

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

    public Pokemon getByPokedexNumber(int number) {
        for (Pokemon p : pokedex) {
            if (p.getPokedexNumber() == number) {
                return p;
            }
        }
        return null;
    }

    public void searchByNumber(int number) {
        for (Pokemon p : pokedex) {
            if (p.getPokedexNumber() == number) {
                System.out.println("Pokémon found:\n\n" + p);
                return;
            }
        }
        System.out.println("No Pokémon found with Pokédex #" + number);
    }

    public boolean hasPokemonWithNumber(int number) {
        for (Pokemon p : pokedex) {
            if (p.getPokedexNumber() == number) {
                return true;
            }
        }
        return false;
    }

    public Pokemon getPokemonByNumber(int number) {
        for (Pokemon p : pokedex) {
            if (p.getPokedexNumber() == number) {
                return p;
            }
        }
        return null;
    }

    public boolean hasPokemonWithName(String name) {
        for (Pokemon p : pokedex) {
            if (p.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Pokemon getPokemonByName(String name) {
        for (Pokemon p : pokedex) {
            if (p.getName().equalsIgnoreCase(name.trim())) {
                return p;
            }
        }
        return null;
    }
    public void setAllPokemon(List<Pokemon> loaded) {
        pokedex.clear();
        pokedex.addAll(loaded);
    }

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