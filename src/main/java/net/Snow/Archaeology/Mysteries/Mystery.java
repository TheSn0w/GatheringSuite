package net.Snow.Archaeology.Mysteries;

public class Mystery {
    private final String name;
    private final int minimumLevel;

    public Mystery(String name, int minimumLevel) {
        this.name = name;
        this.minimumLevel = minimumLevel;
    }

    public String getName() {
        return name;
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }
}