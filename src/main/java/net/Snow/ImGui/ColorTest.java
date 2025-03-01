package net.Snow.ImGui;

public class ColorTest {

    private static final String RESET = "\u001B[0m";
    private static final String[] COLORS = {
            "\u001B[30m", // Black
            "\u001B[31m", // Red
            "\u001B[32m", // Green
            "\u001B[33m", // Yellow
            "\u001B[34m", // Blue
            "\u001B[35m", // Purple
            "\u001B[36m", // Cyan
            "\u001B[37m", // White
            "\u001B[38;5;208m", // Orange
            "\u001B[38;5;201m", // Magenta
            "\u001B[38;5;10m", // Lime
            "\u001B[38;5;219m"  // Pink
    };

    private static final String[] COLOR_NAMES = {
            "Black",
            "Red",
            "Green",
            "Yellow",
            "Blue",
            "Purple",
            "Cyan",
            "White",
            "Orange",
            "Magenta",
            "Lime",
    };

    public static void main(String[] args) {
        for (int i = 0; i < COLORS.length; i++) {
            CustomLogger.log(COLORS[i] + COLOR_NAMES[i] + RESET);
        }
    }
}