package net.Snow.Smithing;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.ImGuiCol.renderCenteredText;

public class SmithingGraphics {


    // Variables to hold the selected indices and objects
    public static int selectedBarTypeIndex = 0;
    public static int selectedItemIndex = 0;
    public static SmithingTypes selectedBarType = SmithingTypes.values()[0];
    public static SmithingTypes.SmithingItem selectedItem = null;

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingSmithingLevel, AtomicInteger startingSmithingXP) throws IOException {

        renderCenteredText("Tested at Fort", 255, 0, 0, 255);
        renderCenteredText("Select Random item first from UI", 255, 0, 0, 255);
        ImGui.SetItemWidth(285.0F);

        // Combo box for selecting the bar type
        int barTypeResult = ImGui.Combo("##BarType", selectedBarTypeIndex,
                Arrays.stream(SmithingTypes.values()).map(SmithingTypes::getName).toArray(String[]::new));
        showTooltip("Select the bar type to smith.");

        if (barTypeResult != selectedBarTypeIndex) {
            selectedBarTypeIndex = barTypeResult;
            selectedBarType = SmithingTypes.values()[selectedBarTypeIndex];
            selectedItemIndex = 0; // Reset item selection when bar type changes
            selectedItem = null;
            logBlue("Bar type selected: " + selectedBarType.getName());
            script.saveConfiguration();
        }

        ImGui.SetItemWidth(285.0F);

        if (selectedBarType != null && selectedBarType.getItems().length > 0) {
            SmithingTypes.SmithingItem[] items = selectedBarType.getItems();
            int itemResult = ImGui.Combo("##SmithingItem", selectedItemIndex,
                    Arrays.stream(items).map(SmithingTypes.SmithingItem::getItemName).toArray(String[]::new));
            showTooltip("Select the item to smith.");

            if (itemResult != selectedItemIndex) {
                selectedItemIndex = itemResult;
                selectedItem = items[selectedItemIndex];
                logBlue("Item selected: " + selectedItem.getItemName());
                script.saveConfiguration();
            }
        }
        ImGui.SeparatorText("Smithing Stats");
        displaySkillStats(Skills.SMITHING, startingSmithingLevel.get(), startingSmithingXP.get(), inventoryMap);
    }
}
