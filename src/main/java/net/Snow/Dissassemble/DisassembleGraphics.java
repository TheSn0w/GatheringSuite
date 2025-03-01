package net.Snow.Dissassemble;

import net.Snow.ImGui.Experience;
import net.Snow.ImGui.ImGuiCol;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.ImGuiCol.renderCenteredText;

public class DisassembleGraphics {

    public static Map<String, Integer> materialsGained = new HashMap<>(); // Replace with your actual data structure

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingInventionLevel, AtomicInteger startingInventionXP) throws IOException {

        renderCenteredText("Will Disassemble ALL items in your inventory", 255, 0, 0, 255);

        ImGui.SeparatorText("Invention Stats");
        displaySkillStats(Skills.INVENTION, startingInventionLevel.get(), startingInventionXP.get(), inventoryMap);

        // Lists of material names to be displayed in specific colors
        List<String> orangeMaterials = Arrays.asList(
                "evasive components", "healthy components", "heavy components", "imbued components",
                "light components", "living components", "refined components", "sharp components",
                "variable components", "pious components", "protective components", "swift components",
                "dextrous components", "subtle components", "direct components", "precious components",
                "enhancing components", "powerful components", "ethereal components", "precise components",
                "stunning components", "strong components"
        );

        List<String> redMaterials = Arrays.asList(
                "corporeal components", "ancient components", "ascended components", "culinary components",
                "dragonfire components", "fungal components", "harnessed components", "noxious components",
                "oceanic components", "pestiferous components", "resilient components", "rumbling components",
                "seren components", "shifting components", "silent components", "zamorak components",
                "shadow components", "clockwork components", "explosive components", "faceted components",
                "armadyl components", "undead components", "zaros components", "fortunate components",
                "ilujankan components", "brassican components", "saradomin components", "bandos components",
                "knightly components", "cywir components", "avernic components"
        );

        // Time elapsed since the script started
        double elapsedSeconds = (System.currentTimeMillis() - Experience.startTime) / 1000.0;

        // Display the materials gained with appropriate coloring
        for (Map.Entry<String, Integer> entry : materialsGained.entrySet()) {
            String materialName = entry.getKey(); // Use the cleaned and normalized name
            float materialsPerHour = (float) entry.getValue() / (float) elapsedSeconds * 3600;

            // Set text color based on the material's categorization
            if (orangeMaterials.contains(materialName)) {
                ImGui.PushStyleColor(ImGuiCol.Text, 1.0f, 0.5f, 0.0f, 1.0f); // Orange color
            } else if (redMaterials.contains(materialName)) {
                ImGui.PushStyleColor(ImGuiCol.Text, 1.0f, 0.0f, 0.0f, 1.0f); // Red color
            }

            ImGui.Text(materialName + ": " + entry.getValue() + " (" + String.format("%.2f", materialsPerHour) + " per hour)");

            // Reset the text color to default
            if (orangeMaterials.contains(materialName) || redMaterials.contains(materialName)) {
                ImGui.PopStyleColor();
            }
        }
    }

}
