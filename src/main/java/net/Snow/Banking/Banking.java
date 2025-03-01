package net.Snow.Banking;

import net.botwithus.rs3.game.Coordinate;

import java.util.List;

public class Banking {
    public enum Type {
        SCENE_OBJECT, NPC
    }

    private String name;
    private int id;
    private Type type;
    private Coordinate coordinate;
    private List<String> interactionOptions;

    public Banking(String name, int id, Type type, Coordinate coordinate, List<String> interactionOptions) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.coordinate = coordinate;
        this.interactionOptions = interactionOptions;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public List<String> getInteractionOptions() {
        return interactionOptions;
    }
}