package pokedex.models;

public class Move {
    private String name;
    private String description;
    private String classification;
    private String type1;
    private String type2;

    public Move(String name, String description, String classification,
                String type1, String type2){

        this.name = name.trim();
        this.description = description.trim();
        this.classification = classification.trim();
        this.type1 = (type1 != null && !type1.isBlank()) ? type1.trim() : "Unknown";
        this.type2 = (type2 != null && !type2.isBlank()) ? type2.trim() : null;
    }

    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public String getClassification(){
        return classification;
    }
    public String getType1(){
        return type1;
    }
    public String getType2(){
        return type2;
    }

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
