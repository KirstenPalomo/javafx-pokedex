/**
 * Represents a Pokémon in the Pokédex system.
 * Each Pokémon has a Pokédex number, name, types, stats, evolution details, moveset, and an optional held item.
 * This class supports logic for move management, stat modification, cloning, and profile generation.
 * Used  in trainer lineups, storage, and battle-related operations.
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.models;

import pokedex.models.Item;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
    // Core Pokémon attributes
    private int pokedexNumber;
    private String name;
    private String type1;
    private String type2;
    private int baseLevel;

    // Evolution properties
    private Integer evolvesFrom;
    private Integer evolvesTo;
    private Integer evolutionLevel;

    // Base stats
    private int hp;
    private int attack;
    private int defense;
    private int speed;

    //Moves and held item
    private List<String> moveSet = new ArrayList<>();
    private Item heldItem;

    /**
     * Constructs a new Pokémon with the given attributes.
     * Default moves "Tackle" and "Defend" are always added.
     * Extra moves are added only if space is available (max of 4 total).
     *
     * @param pokedexNumber Pokédex number of the Pokémon
     * @param name Name of the Pokémon
     * @param type1 Primary type
     * @param type2 Secondary type (can be null)
     * @param baseLevel Base level of the Pokémon
     * @param evolvesFrom Pokédex number of pre-evolution
     * @param evolvesTo Pokédex number of evolved form
     * @param evolutionLevel Level at which it evolves
     * @param hp Base HP stat
     * @param attack Base Attack stat
     * @param defense Base Defense stat
     * @param speed Base Speed stat
     * @param extraMoves List of additional move names (optional)
     * @param heldItem Item currently held by this Pokémon (can be null)
     */
    public Pokemon(int pokedexNumber, String name, String type1, String type2, int baseLevel,
                   Integer evolvesFrom, Integer evolvesTo, Integer evolutionLevel, int hp, int attack,
                   int defense, int speed, List<String> extraMoves, Item heldItem) {

        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.baseLevel = baseLevel;
        this.evolvesFrom = evolvesFrom;
        this.evolvesTo = evolvesTo;
        this.evolutionLevel = evolutionLevel;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.heldItem = heldItem;
        this.moveSet = new ArrayList<>();

        //Default moves
        this.moveSet.add("Tackle");
        this.moveSet.add("Defend");

        //Add valid moves if there is still space
        if (extraMoves != null) {
            for (String move : extraMoves) {
                String trimmedMove = move.trim();
                if (trimmedMove.isEmpty() || moveSet.contains(trimmedMove)) continue;
                if (moveSet.size() >= 4) {
                    System.out.println("⚠️ \"" + trimmedMove + "\" ignored. Move set is full.");
                    continue;
                }
                moveSet.add(trimmedMove);
            }
        }
    }

    /** Makes the pokemon cry. It prints its name in upper case with an "!" */
    public void cry() {
        System.out.println(name.toUpperCase() + "!");
    }

    //Getters for pokemon attributes
    public int getPokedexNumber() {
        return pokedexNumber;
    }

    public String getName() {
        return name;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public int getBaseLevel() {
        return baseLevel;
    }

    public Integer getEvolvesFrom() {
        return evolvesFrom;
    }

    public Integer getEvolvesTo() {
        return evolvesTo;
    }

    public Integer getEvolutionLevel() {
        return evolutionLevel;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public List<String> getMoveSet() {
        return moveSet;
    }

    public Item getHeldItem() {
        return heldItem;
    }

    //Setters for pokemon attributes
    public void setBaseLevel(int baseLevel) {
        this.baseLevel = baseLevel;
    }

    public void setEvolutionLevel(Integer evolutionLevel) {
        this.evolutionLevel = evolutionLevel;
    }

    public void setEvolvesTo(Integer evolvesTo) {
        this.evolvesTo = evolvesTo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPokedexNumber(int number) {
        this.pokedexNumber = number;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setHeldItem(Item heldItem) {
        this.heldItem = heldItem;
    }

    /**
     * Sets the move set to a new list of moves.
     * @param moveSet List of move names to set
     */
    public void setMoveSet(List<String> moveSet) {
        this.moveSet = new ArrayList<>(moveSet);
    }

    /**
     * Increases HP by a specified amount and logs the change.
     *
     * @param amount Amount to increase
     */
    public void increaseHp(int amount) {
        this.hp += amount;
        System.out.println(name + "'s HP increased by " + amount + ". New HP: " + hp);
    }

    /**
     * Increases Attack by a specified amount and logs the change.
     *
     * @param amount Amount to increase
     */
    public void increaseAttack(int amount) {
        this.attack += amount;
        System.out.println(name + "'s Attack increased by " + amount + ". New Attack: " + attack);
    }

    /**
     * Increases Defense by a specified amount and logs the change.
     *
     * @param amount Amount to increase
     */
    public void increaseDefense(int amount) {
        this.defense += amount;
        System.out.println(name + "'s Defense increased by " + amount + ". New Defense: " + defense);
    }

    /**
     * Increases Speed by a specified amount and logs the change.
     *
     * @param amount Amount to increase
     */
    public void increaseSpeed(int amount) {
        this.speed += amount;
        System.out.println(name + "'s Speed increased by " + amount + ". New Speed: " + speed);
    }

    /**
     * Returns a formatted brief profile string for this Pokémon.
     *
     * @return Brief description including type, moves, and held item
     */
    public String briefProfile() {
        String types = type1 + (type2 != null && !type2.isEmpty() ? "/" + type2 : "");
        String moves = moveSet.isEmpty() ? "(None)" : String.join(", ", moveSet);
        String held = (heldItem != null) ? heldItem.getName() : "None";

        return String.format("%s (#%03d) | Type: %s | Moves: %s\n   Held Item: %s",
                name, pokedexNumber, types, moves, held);
    }

    /**
     * Checks if two Pokémon are equal based on Pokédex number.
     *
     * @param o Other object
     * @return True if same Pokédex number, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return pokedexNumber == pokemon.pokedexNumber;
    }

    /** @return Hash code based on Pokedex number. */
    @Override
    public int hashCode() {
        return Integer.hashCode(pokedexNumber);
    }

    /** @return Formatted string version (e.g., "#025 – Pikachu"). */
    @Override
    public String toString() {
        return String.format("#%03d – %s", pokedexNumber, name);
    }

    // ✅ CLONE METHOD
    /**
     * Creates and returns a deep copy of the Pokemon.
     * Note: The held item is not deep-copied, only its reference is reused.
     * Deep copy: creates a completely independent duplicate of an object, including all its nested objects or arrays
     * @return Cloned Pokemon object
     */
    public Pokemon clone() {
        Pokemon copy = new Pokemon(
                this.pokedexNumber,
                this.name,
                this.type1,
                this.type2,
                this.baseLevel,
                this.evolvesFrom,
                this.evolvesTo,
                this.evolutionLevel,
                this.hp,
                this.attack,
                this.defense,
                this.speed,
                new ArrayList<>(this.moveSet),
                this.heldItem // NOTE: this shares the same Item reference
        );
        return copy;
    }
}