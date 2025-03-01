package net.Snow.Prayer;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.LevelLogout.CheckLevel.skillThresholdGraphics;

public class PrayerGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingPrayerLevel, AtomicInteger startingPrayerXP) {
        ImGui.SetItemWidth(285.0F);


        skillThresholdGraphics(Skills.PRAYER.getSkill());
        displaySkillStats(Skills.PRAYER, startingPrayerLevel.get(), startingPrayerXP.get(),  inventoryMap);
    }
}
