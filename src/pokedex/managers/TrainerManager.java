/**
 * This manager handles all trainers in the Enhanced Pokédex system.
 * It stores and retrieves trainer data, supports searching by ID, name, and hometown,
 * and provides methods to view trainer information and access specific trainer profiles.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.managers;

// Imports the Trainer model class, which represents a user/trainer profile in the system
import pokedex.models.Trainer;

// Java utility classes for dynamic lists and immutable copies
import java.util.ArrayList;
import java.util.List;

/**
 * TrainerManager handles the storage, retrieval, and searching of Trainer objects.
 * Provides methods for adding new trainers, listing all, and searching by various fields.
 * Used by menu systems and controller classes to manage trainer data.
 */
public class TrainerManager {
    // Internal list holding all trainers
    private final List<Trainer> trainers = new ArrayList<>();

    /**
     * Adds a new trainer to the list.
     *
     * @param trainer the Trainer object to be added
     */
    public void addTrainer(Trainer trainer) {
        trainers.add(trainer);
        System.out.println("✅ Trainer " + trainer.getName() + " added successfully.");
    }

    /**
     * Returns an unmodifiable list of all trainers.
     *
     * @return list of all Trainer objects
     */
    public List<Trainer> getAllTrainers() {
        return List.copyOf(trainers);
    }

    /**
     * Replaces the current list of trainers with a loaded list from file
     *
     * @param loaded list of trainers to load
     */
    public void setTrainers(List<Trainer> loaded) {
    trainers.clear();
    trainers.addAll(loaded);
}
    /**
     * Displays all trainers with their name, ID, and Pokémon lineup and storage.
     * Useful for debugging or viewing full trainer records.
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
     * Searches for trainers where ID, name, or hometown contains the keyword.
     * Case-insensitive matching.
     *
     * @param keyword the keyword to search for
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

    /**
     * Checks if a trainer with the specified ID exists.
     *
     * @param id the trainer ID to check
     * @return true if a match is found, false otherwise
     */
    public boolean hasTrainerWithID(String id) {
        for (Trainer t : trainers) {
            if (t.getTrainerID().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a trainer with the given ID.
     *
     * @param id the trainer ID to search
     * @return the Trainer object, or null if not found
     */
    public Trainer getTrainerWithID(String id) {
        for (Trainer t : trainers) {
            if (t.getTrainerID().equals(id)) {
                return t;
            }
        }
        return null;
    }


    /**
     * Checks whether a trainer exists with the given name (case-insensitive).
     *
     * @param name the trainer name to check
     * @return true if found, false otherwise
     */
    public boolean hasTrainerWithName(String name) {
        for (Trainer t : trainers) {
            if (t.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a trainer with the given name (case-insensitive, trimmed).
     *
     * @param name trainer name to find
     * @return the matching Trainer object or null if not found
     */
    public Trainer getTrainerWithName(String name) {
        for (Trainer t : trainers) {
            if (t.getName().equalsIgnoreCase(name.trim())) {
                return t;
            }
        }
        return null;
    }
}