package net.Snow.Herblore;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;

public class HerbloreGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingHerbloreLevel, AtomicInteger startingHerbloreXP) throws IOException {

        ImGui.SeparatorText("Herblore Stats");
        displaySkillStats(Skills.HERBLORE, startingHerbloreLevel.get(), startingHerbloreXP.get(),  inventoryMap);

    }
}
