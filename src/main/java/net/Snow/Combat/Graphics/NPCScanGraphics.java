package net.Snow.Combat.Graphics;

import net.Snow.Combat.NPCScanner;
import net.Snow.ImGui.ImGuiCol;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.util.ArrayList;
import java.util.List;

import static net.Snow.Combat.Variables.selectedNpcName;
import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;

public class NPCScanGraphics {

    public static List<String> selectedNpcNames = new ArrayList<>();
    public static String newNpcName = "";

    static void showNearbyNPCs() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);
        if (ImGui.Begin("Nearby NPCs", ImGuiWindowFlag.NoDecoration.getValue())) {
            ImGui.SetWindowSize((float) 300, (float) 200);

//                // Display the list of selected NPC names at the top
//                if (!selectedNpcNames.isEmpty()) {
//                    ImGui.Text("Selected NPCs: " + String.join(", ", selectedNpcNames));
//                }


            if (!selectedNpcNames.isEmpty()) {
                ImGui.PushStyleColor(ImGuiCol.Text, 0, 255, 0, 1.0f);
                ImGui.Separator();
                for (String npcName : new ArrayList<>(selectedNpcNames)) {
                    if (ImGui.Button(npcName)) {
                        selectedNpcNames.remove(npcName);
                        logBlue("NPC removed: " + npcName);
                    }
                }
                ImGui.PopStyleColor();
            }

            // Input text field to add new NPC names
            ImGui.SetItemWidth(211);
            newNpcName = ImGui.InputTextWithHint("Add NPC name here", "##NewNpcName", newNpcName);
            ImGui.SameLine();
            if (ImGui.Button("Add NPC") && !newNpcName.isEmpty()) {
                selectedNpcNames.add(newNpcName);
                selectedNpcName = newNpcName; // Update selectedNpcName
                logBlue("NPC added: " + newNpcName);
                newNpcName = "";
            }

            List<List<String>> npcTableData = NPCScanner.getNpcTableData();
            ImGui.SetItemWidth(300);

            if (ImGui.ListBoxHeader("", 285, 160)) {
                ImGui.Columns(1, "NPC names", true);
                for (int i = 0; i < npcTableData.size(); i++) {
                    List<String> row = npcTableData.get(i);
                    String npcName = row.get(0);

                    String npcIdentifier = npcName + "##" + i;

                    boolean isSelected = selectedNpcNames.contains(npcName);
                    if (ImGui.Selectable(npcIdentifier, isSelected, 0)) {
                        if (isSelected) {
                            selectedNpcNames.remove(npcName);
                            logBlue("NPC removed: " + npcName);
                        } else {
                            selectedNpcNames.add(npcName);
                            selectedNpcName = npcName; // Update selectedNpcName
                            logBlue("NPC selected: " + npcName);
                        }
                    }

                    ImGui.NextColumn();
                }
                ImGui.Columns(1, "Column", false);
                ImGui.ListBoxFooter();
            }
        }
        ImGui.PopStyleColor();
        ImGui.End();
    }
}
