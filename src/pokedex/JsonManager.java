package pokedex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import pokedex.models.Item;
import pokedex.models.Move;
import pokedex.models.Pokemon;
import pokedex.models.Trainer;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonManager {
    private static final String FILE_NAME = "trainers.json";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, type, ctx) ->
                    new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) -> {
                try {
                    return LocalDate.parse(json.getAsString());
                } catch (Exception e) {
                    throw new JsonParseException("Invalid date: " + json.getAsString(), e);
                }
            })
            .setPrettyPrinting()
            .create();

    public static void saveTrainers(List<Trainer> trainers) {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(trainers, writer);
            System.out.println("✅ Trainers saved to " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("❌ Error saving trainers: " + e.getMessage());
        }
    }

    public static List<Trainer> loadTrainers() {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Type listType = new TypeToken<List<Trainer>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.out.println("⚠️ No existing trainers file found.");
            return null;
        }
    }


    private static final String POKEMON_FILE = "pokemons.json";

    /** Save the full list of Pokémon to disk */
    public static void savePokemons(List<Pokemon> pokemons) {
        try (FileWriter writer = new FileWriter(POKEMON_FILE)) {
            gson.toJson(pokemons, writer);
            System.out.println("✅ Pokémon saved to " + POKEMON_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving pokemons: " + e.getMessage());
        }
    }

    /** Load your list of Pokémon back into memory */
    public static List<Pokemon> loadPokemons() {
        try (FileReader reader = new FileReader(POKEMON_FILE)) {
            Type listType = new TypeToken<List<Pokemon>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.out.println("⚠️ No existing pokemons file found.");
            return null;
        }
    }
    private static final String MOVES_FILE = "moves.json";

    /** Save all moves to disk */
    public static void saveMoves(List<Move> moves) {
        try (FileWriter writer = new FileWriter(MOVES_FILE)) {
            gson.toJson(moves, writer);
            System.out.println("✅ Moves saved to " + MOVES_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving moves: " + e.getMessage());
        }
    }

    /** Load all moves from disk */
    public static List<Move> loadMoves() {
        try (FileReader reader = new FileReader(MOVES_FILE)) {
            Type listType = new TypeToken<List<Move>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.out.println("⚠️ No existing moves file found.");
            return null;
        }
    }
    public static void saveItems(List<Item> items) {
        try (Writer writer = new FileWriter("items.json")) {
            gson.toJson(items, writer); // use the shared gson instance
            System.out.println("✅ Items saved to items.json");
        } catch (IOException e) {
            System.err.println("❌ Error saving items: " + e.getMessage());
        }
    }

    public static List<Item> loadItems() {
        try (Reader reader = new FileReader("items.json")) {
            Gson gson = new Gson();
            Item[] items = gson.fromJson(reader, Item[].class);
            return items != null ? new ArrayList<>(Arrays.asList(items)) : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>(); // return empty if file doesn't exist
        }
    }




}
