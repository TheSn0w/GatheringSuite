package net.Snow.Summoning;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.ImGuiCol.renderCenteredText;

public class SummoningGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingSummoningLevel, AtomicInteger startingSummoningXP) throws IOException {
        renderCenteredText("Tested at Taverly", 255, 0, 0, 255);
        renderCenteredText("Only for Binding Contracts", 255, 0, 0, 255);


        // Display the skill stats
        ImGui.SeparatorText("Summoning Stats");
        displaySkillStats(Skills.SUMMONING, startingSummoningLevel.get(), startingSummoningXP.get(),  inventoryMap);

    }
}

