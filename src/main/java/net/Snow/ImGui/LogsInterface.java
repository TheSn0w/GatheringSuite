package net.Snow.ImGui;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.util.List;

import static net.Snow.ImGui.CustomLogger.logWarning;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;

public class LogsInterface {

    private static String currentLogFilter = "ALL"; // Define the variable

    private static boolean showErrorsSelected = false;
    private static boolean showMiscSelected = false;
    private static boolean showAllSelected = true; // Default to "Show All"
    private static boolean scrollToBottomSelected = true; // Track the state of the "Scroll to Bottom" button

    public static void drawLogsWindow() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);
        if (ImGui.Begin("Logs", ImGuiWindowFlag.NoTitleBar.getValue() | ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue() | ImGuiWindowFlag.NoCollapse.getValue())) {
            ImGui.SetWindowSize(500, 400); // Set a size for the logs window

            // Calculate button width and spacing
            float totalWidth = 500;
            int numButtons = 4; // Number of buttons
            float buttonWidth = totalWidth / numButtons;
            float totalButtonWidth = buttonWidth * numButtons;
            float spacing = (totalWidth - totalButtonWidth) / (numButtons + 1);

            // Set initial position for the first button
            float currentX = spacing;

            // Button inside the logs window to scroll to the bottom
            ImGui.SetCursorPosX(currentX);
            createCenteredButton("Scroll to Bottom", () -> scrollToBottomSelected = !scrollToBottomSelected, scrollToBottomSelected, buttonWidth);
            currentX += buttonWidth + spacing;

            ImGui.SameLine();

            // Buttons to filter log messages
            ImGui.SetCursorPosX(currentX);
            createCenteredButton("Show Errors", () -> {
                currentLogFilter = "ERROR";
                showErrorsSelected = true;
                showMiscSelected = false;
                showAllSelected = false;
            }, showErrorsSelected, buttonWidth);
            currentX += buttonWidth + spacing;

            ImGui.SameLine();

            ImGui.SetCursorPosX(currentX);
            createCenteredButton("Show Misc", () -> {
                currentLogFilter = "MISC";
                showErrorsSelected = false;
                showMiscSelected = true;
                showAllSelected = false;
            }, showMiscSelected, buttonWidth);
            currentX += buttonWidth + spacing;

            ImGui.SameLine();

            ImGui.SetCursorPosX(currentX);
            createCenteredButton("Show All", () -> {
                currentLogFilter = "ALL";
                showErrorsSelected = false;
                showMiscSelected = false;
                showAllSelected = true;
            }, showAllSelected, buttonWidth);


            // Begin a child region for the log messages
            ImGui.BeginChild("LogMessagesRegion", 0, 0, false, ImGuiWindowFlag.NoDecoration.getValue());

            // Display log messages based on the current filter
            filterLogs(currentLogFilter);

            // Scroll to bottom if the flag is set
            if (scrollToBottomSelected) {
                ImGui.SetScrollHereY(1.0f);
            }

            ImGui.EndChild(); // End the child region

            ImGui.PopStyleColor(1); // Pop the WindowBg color

            ImGui.End(); // End the Logs window
        }
    }

    public static void createCenteredButton(String buttonText, Runnable onClick, boolean isClicked, float buttonWidth) {
        setStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0); // when you hover over it, will turn this colour
        setStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0); // when you click it, will turn this colour momentarily
        ImGui.PushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f); // Set the frame rounding to 0
        ImGui.PushStyleVar(ImGuiStyleVar.FrameBorderSize, 0.1f); // Set the frame border size to 1
        ImGui.PushStyleColor(ImGuiCol.Border, 1.0f, 1.0f, 1.0f, 0.1f); // Set the border color to white
        ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0); // Set the border shadow color to black

        float[] normalColor = {0.0f, 0.0f, 0.0f, 0.0f}; // Default normal color

        if (isClicked) {
            ImGui.PushStyleColor(ImGuiCol.Button, 0.0f, 1.0f, 0.0f, 0.3f); // If selected turn Green
        } else {
            ImGui.PushStyleColor(ImGuiCol.Button, normalColor[0], normalColor[1], normalColor[2], normalColor[3]);
        }

        float textWidth = ImGui.CalcTextSize(buttonText).getX();
        float padding = (buttonWidth - textWidth) / 2;
        ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding, 10.0f);
        if (ImGui.Button(buttonText)) {
            onClick.run();
            logWarning(buttonText + " button clicked.");
        }
        ImGui.PopStyleVar(2); // Only pop one style var
        ImGui.PopStyleColor(6); // Pop the colours
    }


    private static void filterLogs(String filterType) {
        List<String> filteredMessages;
        switch (filterType) {
            case "ERROR":
                filteredMessages = CustomLogger.getErrorAndWarningMessages();
                break;
            case "MISC":
                filteredMessages = CustomLogger.getMiscMessages();
                break;
            case "ALL":
            default:
                filteredMessages = CustomLogger.getAllMessages();
                break;
        }
        displayLogMessages(filteredMessages);
    }

    private static void displayLogMessages(List<String> logMessages) {
        for (String message : logMessages) {
            // Default color
            float r = 1.0f, g = 1.0f, b = 1.0f;

            if (message.contains("\u001B[30m")) { // Black
                r = 0.0f;
                g = 0.0f;
                b = 0.0f;
                message = message.replace("\u001B[30m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[31m")) { // Red
                r = 1.0f;
                g = 0.0f;
                b = 0.0f;
                message = message.replace("\u001B[31m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[32m")) { // Green
                r = 0.0f;
                g = 1.0f;
                b = 0.0f;
                message = message.replace("\u001B[32m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[32;1m")) { // Mellow Green
                r = 0.0f;
                g = 0.5f;
                b = 0.0f;
                message = message.replace("\u001B[32;1m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[33m")) { // Yellow
                r = 1.0f;
                g = 1.0f;
                b = 0.0f;
                message = message.replace("\u001B[33m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[34m")) { // Blue
                r = 0.0f;
                g = 0.7f;
                b = 1.0f;
                message = message.replace("\u001B[34m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[35m")) { // Purple
                r = 0.5f;
                g = 0.0f;
                b = 0.5f;
                message = message.replace("\u001B[35m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[36m")) { // Cyan
                r = 0.0f;
                g = 1.0f;
                b = 1.0f;
                message = message.replace("\u001B[36m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[37m")) { // Gray
                r = 0.5f;
                g = 0.5f;
                b = 0.5f;
                message = message.replace("\u001B[37m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[38;5;208m")) { // Orange
                r = 1.0f;
                g = 0.65f;
                b = 0.0f;
                message = message.replace("\u001B[38;5;208m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[38;5;201m")) { // Magenta
                r = 1.0f;
                g = 0.0f;
                b = 1.0f;
                message = message.replace("\u001B[38;5;201m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[38;5;10m")) { // Lime
                r = 0.0f;
                g = 1.0f;
                b = 0.0f;
                message = message.replace("\u001B[38;5;10m", "").replace("\u001B[0m", "");
            } else if (message.contains("\u001B[38;5;219m")) { // Pink
                r = 1.0f;
                g = 0.75f;
                b = 0.8f;
                message = message.replace("\u001B[38;5;219m", "").replace("\u001B[0m", "");
            }

            ImGui.PushStyleColor(ImGuiCol.Text, r, g, b, 1.0f);
            ImGui.Text(message);
            ImGui.PopStyleColor(1);
        }
    }
}
