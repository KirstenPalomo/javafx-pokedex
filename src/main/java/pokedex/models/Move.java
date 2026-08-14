
package pokedex.models;
/**
 * Represents a move that a Pokemon can learn in the Pokedex system.
 * Each move has a name, description, classification (e.g., HM, TM), and one or two types.
 * Used in teaching Pokemon new abilities and displaying move data.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
public class Move {
    private String name;
    private String description;
    private String classification;
    private String type1;
    private String type2;
    /**
     * Constructs a new Move with the given properties.
     * If type1 or type2 is null, default values are used ("Unknown" or null).
     *
     * @param name Name of the move
     * @param description Description of what the move does
     * @param classification HM or TM (or other classification)
     * @param type1 Primary move type (e.g., Water, Fire)
     * @param type2 Secondary move type (can be null)
     */
    public Move(String name, String description, String classification,
                String type1, String type2){

        this.name = name.trim();
        this.description = description.trim();
        this.classification = classification.trim();
        this.type1 = (type1 != null && !type1.isBlank()) ? type1.trim() : "Unknown";
        this.type2 = (type2 != null && !type2.isBlank()) ? type2.trim() : null;
    }

    /** @return Name of the move */
    public String getName(){
        return name;
    }
    /** @return Description of the move */
    public String getDescription(){
        return description;
    }
    /** @return Classification (e.g., HM or TM) */
    public String getClassification(){
        return classification;
    }
    /** @return Primary type of the move */
    public String getType1(){
        return type1;
    }
    /** @return Secondary type of the move (can be null) */
    public String getType2(){
        return type2;
    }

    /**
     * Returns a formatted string representation of the move.
     *
     * @return A descriptive block showing name, classification, description, and types
     */
    @Override
    public String toString(){
        return String.format(
                "====================\n" +
                        "Move: %s [%s]\n" +
                        "Description: %s\n" +
                        "Type: %s%s\n" +
                        "====================\n",
                name, classification,
                description,
                type1, (type2 != null ? "/" + type2 : ""));
    }
}
