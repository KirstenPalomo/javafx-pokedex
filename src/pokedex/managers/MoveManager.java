/**
 * Handles storage, retrieval, and searching of Move objects in the Pokedex system.
 * Implements the singleton pattern to ensure a single shared instance across the application.
 * Provides methods to add moves, view all, search by various attributes, and retrieve specific moves.
 *
 * Used by controllers and models to manage move-related functionality such as teaching Pokemon new moves.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.managers;

import pokedex.models.Move;
import java.util.ArrayList;
import java.util.List;

public class MoveManager {
    private static MoveManager instance; // 🔹 Singleton instance

    /**
     * Returns an unmodifiable copy of all stored moves.
     *
     * @return List of Move objects
     */
    private List<Move> moves = new ArrayList<>();
     public List<Move> getAllMoves() {
        return List.copyOf(moves);
    }

    /**
     * Replaces the entire move list with a new one.
     * Typically used when loading from file.
     *
     * @param loaded List of moves to load
     */
    public void setAllMoves(List<Move> loaded) {
        moves.clear();
        moves.addAll(loaded);
    }
    /**
     * Private constructor to enforce singleton pattern.
     * Prevents external instantiation.
     */
    private MoveManager() {} // 🔐 private constructor

    /**
     * Returns the singleton instance of MoveManager.
     *
     * @return Shared instance of MoveManager
     */
    public static MoveManager getInstance() {
        if (instance == null) {
            instance = new MoveManager();
        }
        return instance;
    }

    /**
     * Adds a new move to the move list.
     * Checks for duplicates by move name (case-insensitive).
     *
     * @param move Move object to add
     */
    public void addMove(Move move) {
        for (Move existing : moves) {
            if (existing.getName().equalsIgnoreCase(move.getName())) {
                System.out.println("Error: a move named \"" + move.getName() + "\" already exists.");
                return;
            }
        }
        moves.add(move);
        System.out.println("✅ " + move.getName() + " added to Moves.");
    }

    /**
     * Prints all stored moves to the console.
     * Displays a message if the list is empty.
     */
    public void viewAllMoves() {
        if (moves.isEmpty()) {
            System.out.println("No moves available.");
            return;
        }
        for (Move move : moves) {
            System.out.println(move);
        }
    }

    /**
     * Searches for moves by name, description, type1, type2, or classification.
     * Case-insensitive partial matches are supported.
     *
     * @param name Keyword to search
     */
    public void searchMoveByName(String name) {
        List<Move> found = new ArrayList<>();
        String keyword = name.toLowerCase();

        for (Move move : moves) {
            if ((move.getName() != null && move.getName().toLowerCase().contains(keyword))
                    || (move.getDescription() != null && move.getDescription().toLowerCase().contains(keyword))
                    || (move.getType1() != null && move.getType1().toLowerCase().contains(keyword))
                    || (move.getType2() != null && move.getType2().toLowerCase().contains(keyword))
                    || (move.getClassification() != null && move.getClassification().toLowerCase().contains(keyword))) {
                found.add(move);
            }
        }

        if (found.isEmpty()) {
            System.out.println("No moves found with keyword containing: " + name);
        } else {
            System.out.printf("Moves found:\n\n");
            for (Move move : found) {
                System.out.println(move);
            }
        }
    }

    /**
     * Checks if a move with the given name exists.
     *
     * @param name Name of the move
     * @return True if found, false otherwise
     */
    public boolean hasMoveWithName(String name) {
        for (Move move : moves) {
            if (move.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a move by its name (case-insensitive).
     *
     * @param name Name of the move
     * @return Move object if found, null otherwise
     */
    public Move getMoveByName(String name) {
        for (Move m : moves) {
            if (m.getName().equalsIgnoreCase(name.trim())) {
                return m;
            }
        }
        return null;
    }
}