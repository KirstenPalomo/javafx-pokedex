package pokedex.managers;

import pokedex.models.Move;
import java.util.ArrayList;
import java.util.List;

public class MoveManager {
    private static MoveManager instance; // 🔹 Singleton instance

    private List<Move> moves = new ArrayList<>();

    private MoveManager() {} // 🔐 private constructor

    public static MoveManager getInstance() {
        if (instance == null) {
            instance = new MoveManager();
        }
        return instance;
    }

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

    public void viewAllMoves() {
        if (moves.isEmpty()) {
            System.out.println("No moves available.");
            return;
        }
        for (Move move : moves) {
            System.out.println(move);
        }
    }

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

    public boolean hasMoveWithName(String name) {
        for (Move move : moves) {
            if (move.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Move getMoveByName(String name) {
        for (Move m : moves) {
            if (m.getName().equalsIgnoreCase(name.trim())) {
                return m;
            }
        }
        return null;
    }
}