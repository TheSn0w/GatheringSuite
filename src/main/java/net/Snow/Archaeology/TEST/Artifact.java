package net.Snow.Archaeology.TEST;

import java.util.ArrayList;
import java.util.List;

public class Artifact {
    int itemId;

    int damagedItemId;

    int level;

    List<Integer> materials;

    List<Integer> amounts;

    public Artifact(int itemId, int damagedItemId, int level, List<Integer> materials, List<Integer> amounts) {
        this.itemId = itemId;
        this.damagedItemId = damagedItemId;
        this.level = level;
        this.materials = materials;
        this.amounts = amounts;
    }

    public Artifact(int itemId, int damagedItemId, int level) {
        this.itemId = itemId;
        this.damagedItemId = damagedItemId;
        this.level = level;
        this.materials = new ArrayList<>();
        this.amounts = new ArrayList<>();
    }
}