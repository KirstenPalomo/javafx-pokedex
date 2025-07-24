package pokedex.models;

import pokedex.models.Item;
import java.util.ArrayList;
import java.util.List;

public class Pokemon {

    private int pokedexNumber;
    private String name;
    private String type1;
    private String type2;
    private int baseLevel;

    // Evolution properties
    private Integer evolvesFrom;
    private Integer evolvesTo;
    private Integer evolutionLevel;

    //Base stats
    private int hp;
    private int attack;
    private int defense;
    private int speed;

    private List<String> moveSet = new ArrayList<>();
    private Item heldItem;

    public Pokemon(int pokedexNumber, String name, String type1, String type2, int baseLevel,
                   Integer evolvesFrom, Integer evolvesTo, Integer evolutionLevel, int hp, int attack,
                   int defense, int speed, List<String> extraMoves, Item heldItem){

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
        this.moveSet.add("Tackle"); // Default move
        this.moveSet.add("Defend"); // Default move

        if (extraMoves != null) {
            for (String move : extraMoves) {
                String trimmedMove = move.trim();
                if (trimmedMove.isEmpty() || moveSet.contains(trimmedMove)) {
                    continue;
                }
                // Limit to 4 moves
                // If moveSet already has 4 moves, ignore any additional ones
                // This prevents adding more than 4 moves
                if (moveSet.size() >= 4) {
                    System.out.println("⚠️ \"" + trimmedMove + "\" ignored. Move set is full.");
                    continue;
                }
                moveSet.add(trimmedMove);
            }
        }
    }

    public void cry(){
        System.out.println(name.toUpperCase() + "!");
    }

    public int getPokedexNumber(){
        return pokedexNumber;
    }
    public String getName(){
        return name;
    }
    public String getType1(){
        return type1;
    }
    public String getType2(){
        return type2;
    }
    public int getBaseLevel() {
        return baseLevel;
    }
    public Integer getEvolvesFrom(){
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

    public void setBaseLevel(int baseLevel) {
        this.baseLevel = baseLevel;
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
    public void setMoveSet(List<String> moveSet) { this.moveSet = new ArrayList<>(moveSet); // Deep copy to avoid shared reference
    }


    public void increaseHp(int amount) {
        this.hp += amount;
        System.out.println("" + name + "'s HP increased by " + amount + ". New HP: " + hp);
    }

    public void increaseAttack(int amount) {
        this.attack += amount;
        System.out.println("" + name + "'s Attack increased by " + amount + ". New Attack: " + attack);
    }

    public void increaseDefense(int amount) {
        this.defense += amount;
        System.out.println("" + name + "'s Defense increased by " + amount + ". New Defense: " + defense);
    }

    public void increaseSpeed(int amount) {
        this.speed += amount;
        System.out.println("" + name + "'s Speed increased by " + amount + ". New Speed: " + speed);
    }

    public String briefProfile() {
        String types = type1 + (type2 != null && !type2.isEmpty() ? "/" + type2 : "");
        String moves = moveSet.isEmpty() ? "(None)" : String.join(", ", moveSet);
        String held = (heldItem != null) ? heldItem.getName() : "None";

        return String.format("%s (#%03d) | Type: %s | Moves: %s\n   Held Item: %s",
                name, pokedexNumber, types, moves, held);
    }

    @Override
    public String toString() {
        return String.format("#%03d – %s", pokedexNumber, name);
    }
}