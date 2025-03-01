package net.Snow.Archaeology.Collections;

import java.util.List;
import java.util.Map;



public class Artifact {
    private final String id; // Unique identifier
    private final String name;
    private final String damagedName;
    private Map<String, Integer> requiredMaterials = Map.of();
    private final int levelRequired;
    private final String hotspot;
    private final List<String> collectionNames;

    public Artifact(String id, String name, String damagedName, Map<String, Integer> requiredMaterials, int levelRequired, String hotspot, List<String> collectionNames) {
        this.id = id;
        this.name = name;
        this.damagedName = damagedName;
        this.requiredMaterials = requiredMaterials;
        this.levelRequired = levelRequired;
        this.hotspot = hotspot;
        this.collectionNames = collectionNames;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDamagedName() {
        return damagedName;
    }

    public Map<String, Integer> getRequiredMaterials() {
        return requiredMaterials;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public String getHotspot() {
        return hotspot;
    }

    public List<String> getCollectionNames() {
        return collectionNames;
    }
}