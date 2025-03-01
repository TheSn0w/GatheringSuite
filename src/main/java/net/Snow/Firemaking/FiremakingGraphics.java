package net.Snow.Firemaking;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.BankingImGui.bankingLocationsImGui;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;

public class FiremakingGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingFiremakingLevel, AtomicInteger startingFiremakingXP) throws IOException {
        ImGui.SetItemWidth(285.0F);
        bankingLocationsImGui();


        // Display the skill stats
        ImGui.SeparatorText("Firemaking Stats");
        displaySkillStats(Skills.FIREMAKING, startingFiremakingLevel.get(), startingFiremakingXP.get(),  inventoryMap);

    }
}
