package net.Snow.Crafting;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Crafting.Crafting.*;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;

public class CraftingGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingCraftingLevel, AtomicInteger startingCraftingXP) throws IOException {

        createCenteredButton("Form Clay", () -> formClay = !formClay, formClay, 138.0f);
        showTooltip("Creates Urns.");

        ImGui.SameLine();

        createCenteredButton("Fire Urn", () -> fireClay = !fireClay, fireClay, 138.0f);
        showTooltip("Fires Urns");

        createCenteredButton("Fire Urn Lunar", () -> fireClayLunar = !fireClayLunar, fireClayLunar, 138.0f);
        showTooltip("Fires Urns with Lunar Spellbook");

        ImGui.SameLine();

        createCenteredButton("Add Runes", () -> addRunes = !addRunes, addRunes, 138.0f);
        showTooltip("Add Runes to Urns");

        createCenteredButton("Craft Gems", () -> craftGems = !craftGems, craftGems, 138.0f);
        showTooltip("Craft any gems");
        // Display the skill stats
        ImGui.SeparatorText("Crafting Stats");
        displaySkillStats(Skills.CRAFTING, startingCraftingLevel.get(), startingCraftingXP.get(), inventoryMap);
    }
}
