package net.Snow.Smelting;

import java.util.ArrayList;
import java.util.List;

public class SelectedType {

    public static int selectedSmithingIndex = 0; // Starts with the "Disabled" option selected

    // Define an ArrayList to hold the different types of skills
    public static List<String> smithingOptions = new ArrayList<>();

    static {
        // Initialize the ArrayList with the "Disabled" option as the first item
        smithingOptions.add("Disabled");
        smithingOptions.add("Enchanted gem");
        smithingOptions.add("Bars");
        smithingOptions.add("Jewellery");
    }


    public static String getSelectedSkill() {
        if (selectedSmithingIndex >= 0 && selectedSmithingIndex < smithingOptions.size()) {
            return smithingOptions.get(selectedSmithingIndex);
        } else {
            return "Disabled"; // Fallback option
        }
    }

    public static void setSelectedSmithingIndex(int index) {
        if (index >= 0 && index < smithingOptions.size()) {
            selectedSmithingIndex = index;
        } else {
            selectedSmithingIndex = 0; // Default to "Disabled"
        }
    }
}