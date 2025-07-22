package pokedex.managers;

import pokedex.models.Trainer;
import java.util.ArrayList;
import java.util.List;

/**
 * TrainerManager handles storage and retrieval of Trainer objects.
 * It provides methods to add, view, and search trainers.
 */
public class TrainerManager {
    // Internal list holding all trainers
    private final List<Trainer> trainers = new ArrayList<>();

    /**
     * Adds a new trainer to the system.
     * @param trainer the Trainer to add
     */
    public void addTrainer(Trainer trainer) {
        trainers.add(trainer);
        System.out.println("✅ Trainer " + trainer.getName() + " added successfully.");
    }

    /**
     * Returns an unmodifiable list of all trainers.
     * @return list of all Trainer objects
     */
    public List<Trainer> getAllTrainers() {
        return List.copyOf(trainers);
    }

    /**
     * Displays all trainers along with their lineup and storage details.
     * This is a convenience method; for submenu-driven interaction, use getAllTrainers().
     */
    public void viewAllTrainers() {
        if (trainers.isEmpty()) {
            System.out.println("⚠️ No trainers found.");
            return;
        }
        for (Trainer t : trainers) {
            System.out.println("-------------------------------------------------");
            System.out.println(t);  // uses Trainer.toString()
            System.out.println("Lineup:");
            t.getLineup().forEach(p -> System.out.println(" - " + p.getName()));
            System.out.println("Storage:");
            t.getStorage().forEach(p -> System.out.println(" - " + p.getName()));
        }
    }

    /**
     * Searches for trainers whose ID, name, or hometown contains the given keyword (case-insensitive).
     * @param keyword the search term
     */
    public void searchTrainer(String keyword) {
        String lower = keyword.toLowerCase();
        List<Trainer> found = new ArrayList<>();
        for (Trainer t : trainers) {
            if (t.getName().toLowerCase().contains(lower)
                    || t.getTrainerID().toLowerCase().contains(lower)
                    || t.getHometown().toLowerCase().contains(lower)) {
                found.add(t);
            }
        }
        if (found.isEmpty()) {
            System.out.println("⚠️ No trainers match keyword: " + keyword);
        } else {
            System.out.println("Matching trainers:");
            for (Trainer t : found) {
                System.out.println(t);
            }
        }
    }

    public boolean hasTrainerWithID(String id) {
        for (Trainer t : trainers) {
            if (t.getTrainerID().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public Trainer getTrainerWithID(String id) {
        for (Trainer t : trainers) {
            if (t.getTrainerID().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public boolean hasTrainerWithName(String name) {
        for (Trainer t : trainers) {
            if (t.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Trainer getTrainerWithName(String name) {
        for (Trainer t : trainers) {
            if (t.getName().equalsIgnoreCase(name.trim())) {
                return t;
            }
        }
        return null;
    }
}