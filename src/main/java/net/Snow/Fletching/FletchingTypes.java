package net.Snow.Fletching;

import java.util.ArrayList;
import java.util.List;

public class FletchingTypes {

    // Static index to track the selected fletching type
    public static int selectedFletchingIndex = 0; // Default to "Disabled"

    // List to hold fletching options
    public static List<String> fletchingOptions = new ArrayList<>();

    // Static block to initialize the fletching options
    static {
        fletchingOptions.add("Disabled");
        fletchingOptions.add("Unstrung bows");
        fletchingOptions.add("String bows");
        fletchingOptions.add("Ammo");
    }

    // Method to get the currently selected fletching type
    public static String getSelectedFletching() {
        if (selectedFletchingIndex >= 0 && selectedFletchingIndex < fletchingOptions.size()) {
            return fletchingOptions.get(selectedFletchingIndex);
        } else {
            return "Disabled"; // Fallback option if the index is out of bounds
        }
    }

    // Method to set the selected fletching index
    public static void setSelectedFletchingIndex(int index) {
        if (index >= 0 && index < fletchingOptions.size()) {
            selectedFletchingIndex = index;
        } else {
            selectedFletchingIndex = 0; // Default to "Disabled"
        }
    }
}
