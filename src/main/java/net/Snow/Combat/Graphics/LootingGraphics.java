package net.Snow.Combat.Graphics;

import net.Snow.ImGui.ImGuiCol;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.util.ArrayList;
import java.util.List;

import static net.Snow.Combat.Graphics.CombatGraphics.showLootOptions;
import static net.Snow.Combat.Graphics.NotepaperGraphics.renderNotepaperWindow;
import static net.Snow.Combat.Looting.LootManager.*;
import static net.Snow.ImGui.CustomLogger.logOrange;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;

public class LootingGraphics {

    public static List<String> getTargetItemNames() {
        return targetItemNames;
    }

    public static String selectedItem = "";

    public static String getSelectedItem() {
        return selectedItem;
    }

    public static List<String> LootList = List.of("charm", "coins", "bones", "grimy", "rune"


    );

    public static void renderLootingWindow() {
        if (showLootOptions) {
            setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);
            if (ImGui.Begin("LootingWindow", ImGuiWindowFlag.NoDecoration.getValue())) {
                ImGui.SetWindowSize(300.0F, 300.0F);

                createCenteredButton("Walk to Loot", () -> walkToLoot = !walkToLoot, walkToLoot, 138.0f);
                showTooltip("Walking to loot.");

                createCenteredButton("Custom Loot", () -> useCustomLoot = !useCustomLoot, useCustomLoot, 138.0f);
                showTooltip("Custom Loot.");

                ImGui.SameLine();

                createCenteredButton("Loot Stackables", () -> useLootAllStackableItems = !useLootAllStackableItems, useLootAllStackableItems, 138.0f);
                showTooltip("Loot all stackable items.");


                createCenteredButton("Loot Noted", () -> useLootAllNotedItems = !useLootAllNotedItems, useLootAllNotedItems, 138.0f);
                showTooltip("Loot all noted items.");

                ImGui.SameLine();

                createCenteredButton("Loot All", () -> useLootEverything = !useLootEverything, useLootEverything, 138.0f);
                showTooltip("Loot all items.");

                createCenteredButton("Notepaper", () -> useNotepaper = !useNotepaper, useNotepaper, 138.0f);
                showTooltip("Use notepaper on items.");

                if (useCustomLoot) {

                    ImGui.SeparatorText("Loot Options");


                    ImGui.SetItemWidth(211.0F);
                    setSelectedItem(ImGui.InputTextWithHint("Add Items here", "##Itemname", getSelectedItem()));

                    ImGui.SameLine();


                    if (ImGui.Button("Add Item") && !getSelectedItem().isEmpty()) {
                        getTargetItemNames().add(getSelectedItem());
                        setSelectedItem("");
                    }

                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Enter the name of the item to add to your list. Case-insensitive.");
                    }

                    List<String> comboItemsList = new ArrayList<>(LootList);
                    comboItemsList.add(0, "                  Select Loot to Add");
                    String[] comboItems = comboItemsList.toArray(new String[0]);

                    int selectedItemIndex = 0;

                    ImGui.SetItemWidth(284.0F);
                    int comboResult = ImGui.Combo("##LootType", selectedItemIndex, comboItems);
                    if (comboResult != selectedItemIndex) {
                        selectedItemIndex = comboResult;
                        if (selectedItemIndex > 0 && selectedItemIndex < comboItems.length) {
                            String selectedName = comboItems[selectedItemIndex];
                            getTargetItemNames().add(selectedName);
                            logOrange("Predefined Loot added: " + selectedName);
                        } else {
                            logOrange("Please select a valid loot.");
                        }
                    }
                    if (!getTargetItemNames().isEmpty()) {
                        for (String itemName : new ArrayList<>(getTargetItemNames())) {
                            ImGui.PushStyleColor(ImGuiCol.Text, 0, 255, 0, 1.0f);
                            if (ImGui.Button(itemName + "##Remove")) {
                                getTargetItemNames().remove(itemName);
                            }
                            ImGui.PopStyleColor();

                            if (ImGui.IsItemHovered()) {
                                ImGui.SetTooltip("Click to remove this item");
                            }
                        }
                    }
                }

                if (useLootAllStackableItems || useLootAllNotedItems) {
                    ImGui.SeparatorText("Excluded Keywords Options");

                    ImGui.SetItemWidth(185.0F);

                    setExcludedKeyword(ImGui.InputTextWithHint("Add Excluded Name", "##ExcludedKeyword", getExcludedKeyword()));

                    ImGui.SameLine();

                    if (ImGui.Button("Add Keyword") && !getExcludedKeyword().isEmpty()) {
                        addExcludedKeyword(getExcludedKeyword());
                        setExcludedKeyword("");
                    }

                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Enter the name of the item to exclude from looting, or part of the name of items such as 'seed' to not loot any seeds");
                    }

                    if (!getExcludedKeywords().isEmpty()) {
                        for (String excludedKeyword : new ArrayList<>(getExcludedKeywords())) {
                            ImGui.PushStyleColor(ImGuiCol.Text, 255, 0, 0, 1.0f);
                            if (ImGui.Button(excludedKeyword + "##Remove")) {
                                removeExcludedKeyword(excludedKeyword);
                            }
                            ImGui.PopStyleColor();
                            if (ImGui.IsItemHovered()) {
                                ImGui.SetTooltip("Click to remove this excluded keyword");
                            }
                        }
                    }
                }
                if (useNotepaper) {
                    renderNotepaperWindow();
                }

            }
            ImGui.PopStyleColor();
            ImGui.End();
        }
    }

    public static void setSelectedItem(String selectedItem) {
        LootingGraphics.selectedItem = selectedItem;
    }
}
