package net.Snow.ImGui;

import net.Snow.Snow;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

import java.io.IOException;
import java.util.Arrays;

import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.Snow.ImGui.LogsInterface.drawLogsWindow;

public class SnowsTemplateGraphics extends ScriptGraphicsContext {



    Snow script;

    public static long scriptStartTime;
    public static boolean showLogs = false;

    // Define the selectedActivityIndex variable for the combo box

    public static int selectedActivityIndex = 0;

    public SnowsTemplateGraphics(ScriptConsole console, Snow script) {
        super(console);
        this.script = script;
        scriptStartTime = System.currentTimeMillis();
    }


    @Override
    public void drawSettings() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);

        // Start the main settings window
        if (ImGui.Begin("Snows", ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue() | ImGuiWindowFlag.NoScrollbar.getValue())) {
            ImGui.SetWindowSize(300.f, 600.f);

            // Create a child window for the top buttons
            ImGui.BeginChild("Top Buttons", 0, 80, false, ImGuiWindowFlag.NoScrollbar.getValue());
            createCenteredButton(showLogs ? "Hide Logs" : "Show Logs", () -> {
                showLogs = !showLogs;
                try {
                    script.saveConfiguration();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, showLogs, 138.0f);
            showTooltip("Show or hide the logs window, useful for debugging.");

            ImGui.SameLine();

            createCenteredButton("Save Configuration", () -> {
                try {
                    script.saveConfiguration();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, false, 138.0f);
            showTooltip("Save the current configuration.");

            // Display time running as a centered button
            createCenteredButton(getTimeRunningFormatted(), () -> {
                // Button action can be defined here if needed
            }, false, 284.0f);
            showTooltip("Displays the time the script has been running.");
            ImGui.EndChild(); // End the Top Buttons child window

            // Create a child window for the main content
            ImGui.BeginChild("Main Content", 0, 0, false, ImGuiWindowFlag.NoScrollbar.getValue());

            ImGui.SeparatorText("Active Scripts");

            ImGui.SetItemWidth(285.0F);

            int result = ImGui.Combo("##ActivityType", selectedActivityIndex, Arrays.stream(script.scripts).map(script -> script.getClass().getSimpleName()).toArray(String[]::new));
            showTooltip("Select the activity you want to perform.");
            if (result != selectedActivityIndex) {
                selectedActivityIndex = result;

                script.AbstractScript = script.scripts[selectedActivityIndex];
                script.AbstractScript.initialize();

                // Save the configuration whenever the selected activity changes
                try {
                    script.saveConfiguration();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (script.AbstractScript != null) {
                try {
                    script.AbstractScript.draw(this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ImGui.Text("No script selected.");
            }

            ImGui.EndChild(); // End the Main Content child window

            ImGui.PopStyleColor(1); // Pop the WindowBg color
            ImGui.End(); // End the Snows Template window
        }

        // If showLogs is true, display the logs window
        if (showLogs) {
            drawLogsWindow();
        }
    }

    private String getTimeRunningFormatted() {
        long elapsedTimeMillis = System.currentTimeMillis() - SnowsTemplateGraphics.scriptStartTime;
        long elapsedSeconds = elapsedTimeMillis / 1000;
        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}