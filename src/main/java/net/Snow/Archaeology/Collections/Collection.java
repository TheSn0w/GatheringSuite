package net.Snow.Archaeology.Collections;

import net.botwithus.rs3.game.achievemnt.Achievement;

import java.util.List;

public class Collection {
    private final String name;
    private final int levelRequired;
    private final List<Integer> achievementIds;

    public Collection(String name, int levelRequired, List<Integer> achievementIds) {
        this.name = name;
        this.levelRequired = levelRequired;
        this.achievementIds = achievementIds;
    }

    public String getName() {
        return name;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public List<Integer> getAchievementIds() {
        return achievementIds;
    }

    public boolean areAllAchievementsComplete() {
        if (achievementIds == null || achievementIds.isEmpty()) {
            return false;
        }
        for (Integer achievementId : achievementIds) {
            if (!Achievement.byId(achievementId).isCompleted()) {
                return false;
            }
        }
        return true;
    }
}