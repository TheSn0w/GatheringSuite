package net.Snow.ImGui;

import net.Snow.Banking.BankLocations;
import net.Snow.Banking.Banking;
import net.Snow.Snow;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.Comparator;

import static net.Snow.Banking.BankLocations.getSelectedBank;
import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.showTooltip;

public class BankingImGui {

    static Snow script;

    public static void bankingLocationsImGui() throws IOException {
        ImGui.SeparatorText("Banking Location");

        // Update "Disabled" bank coordinate to the player's current coordinate
        updateDisabledBankCoordinate();

        // Check if the selected bank is "Disabled" and then sort by distance
        if (getSelectedBank().getName().equals("Disabled")) {
            sortBankLocationsByDistance();
        }

        // Bank Location Combo
        ImGui.SetItemWidth(285.0F);
        int bankResult = ImGui.Combo("##BankLocation", BankLocations.selectedBankIndex,
                BankLocations.bankings.stream().map(Banking::getName).toArray(String[]::new));
        showTooltip("Select the bank location to use for banking.");

        // Check if the selected index has changed
        if (bankResult != BankLocations.selectedBankIndex) {
            BankLocations.selectedBankIndex = bankResult;
            if (bankResult >= 0) {
                String selectedBank = BankLocations.bankings.get(bankResult).getName();
                logBlue("Bank selected: " + selectedBank);
                script.saveConfiguration();
            }
        }
    }

    // Update the "Disabled" bank coordinate by recreating the entry
    private static void updateDisabledBankCoordinate() {
        Coordinate playerCoordinate = Client.getLocalPlayer().getCoordinate();
        if (playerCoordinate != null) {
            // Recreate the "Disabled" entry with the updated coordinate
            Banking disabledBank = new Banking("Disabled", -1, null, playerCoordinate, null);

            // Replace the old "Disabled" entry with the new one
            BankLocations.bankings.set(0, disabledBank);
        }
    }

    private static void sortBankLocationsByDistance() {
        // Get the player's coordinate
        Coordinate playerCoordinate = Client.getLocalPlayer().getCoordinate();

        if (playerCoordinate != null) {
            // Sort bank locations by distance, keeping "Disabled" at index 0
            BankLocations.bankings.subList(1, BankLocations.bankings.size())
                    .sort(Comparator.comparingDouble(bank -> getDistanceToPlayer(bank.getCoordinate(), playerCoordinate)));
        }
    }

    // Helper method to calculate the distance between two coordinates
    private static double getDistanceToPlayer(Coordinate bankCoordinate, Coordinate playerCoordinate) {
        if (bankCoordinate == null || playerCoordinate == null) {
            return Double.MAX_VALUE; // Return a large value to ensure banks without coordinates are sorted last
        }
        return Distance.between(bankCoordinate, playerCoordinate);
    }


}
