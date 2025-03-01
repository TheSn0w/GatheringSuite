package net.Snow.Combat.Graphics;

import net.Snow.Notepaper.Notepaper;
import net.botwithus.rs3.imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

import static net.Snow.ImGui.CustomLogger.log;

public class NotepaperGraphics {

    public static List<String> predefinedNotepaperNames = List.of(
            "Huge bladed rune salvage",
            "Huge spiky rune salvage",
            "Huge plated rune salvage",
            "Huge blunt rune salvage",
            "Large blunt rune salvage",
            "Large bladed rune salvage",
            "Large plated rune salvage",
            "Large spiky rune salvage",
            "Huge bladed orikalkum salvage",
            "Huge spiky orikalkum salvage",
            "Huge plated orikalkum salvage",
            "Huge blunt orikalkum salvage",
            "Large blunt orikalkum salvage",
            "Large bladed orikalkum salvage",
            "Large plated orikalkum salvage",
            "Large spiky orikalkum salvage",
            "Ascension grips",
            "Ascension Keystone Primus",
            "Ascension Keystone Secundus",
            "Ascension Keystone Tertius",
            "Ascension Keystone Quartus",
            "Ascension Keystone Quintus",
            "Ascension Keystone Sextus"

    );

    public static void renderNotepaperWindow() {
        ImGui.SeparatorText("Notepaper Options");

        ImGui.SetItemWidth(175.0F);
        Notepaper.setNotepaperName(ImGui.InputTextWithHint("Enter Notepaper Name", "##Notepapername", Notepaper.getNotepaperName()));

        ImGui.SameLine();

        if (ImGui.Button("Add Notepaper") && !Notepaper.getNotepaperName().isEmpty()) {
            Notepaper.addNotepaperName(Notepaper.getNotepaperName());
            predefinedNotepaperNames.add(Notepaper.getNotepaperName());
            Notepaper.setNotepaperName("");
        }

        if (ImGui.IsItemHovered()) {
            ImGui.SetTooltip("Enter the name of the item to add to your list. Case-sensitive.");
        }

        List<String> comboItemsList = new ArrayList<>();
        comboItemsList.add("Select an item...");
        for (String item : predefinedNotepaperNames) {
            if (item.toLowerCase().contains(Notepaper.getNotepaperName().toLowerCase())) {
                comboItemsList.add(item);
            }
        }
        String[] comboItems = comboItemsList.toArray(new String[0]);
        ImGui.SetItemWidth(285.0F);

        int notepaperResult = ImGui.Combo("##NotepaperType", Notepaper.selectedNotepaperIndex, comboItems);
        if (notepaperResult != Notepaper.selectedNotepaperIndex) {
            Notepaper.selectedNotepaperIndex = notepaperResult;
            if (notepaperResult > 0) {
                String selectedName = comboItems[notepaperResult];
                Notepaper.addNotepaperName(selectedName);
                log("Predefined notepaper added: " + selectedName);
            }
        }


        if (!Notepaper.getSelectedNotepaperNames().isEmpty()) {
            for (String notepaperName : new ArrayList<>(Notepaper.getSelectedNotepaperNames())) {
                if (ImGui.Button(notepaperName + "##Remove")) {
                    Notepaper.removeNotepaperName(notepaperName);
                }
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Click to remove this notepaper");
                }
            }
        }
    }
}
