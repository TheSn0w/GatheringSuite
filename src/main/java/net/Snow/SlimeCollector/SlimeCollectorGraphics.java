package net.Snow.SlimeCollector;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.Snow.ImGui.BankingImGui.bankingLocationsImGui;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTime;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.Snow.Runecrafting.Variables.worldHop;

public class SlimeCollectorGraphics {

    private static final Map<String, Integer> itemPrices = new ConcurrentHashMap<>(); // The item prices map


    public static void draw(SnowsTemplateGraphics sgc, Snow script) throws IOException {
        ImGui.SetItemWidth(285.0F);

        bankingLocationsImGui();

        booleanOptions();

        ImGui.SeparatorText("Botstate: " + SlimeCollector.getSlimeState());


        float windowWidth = 285.0F;

        String text1 = "Disable banking to logout";
        float textWidth1 = ImGui.CalcTextSize(text1).getX();
        float centerPosX1 = (windowWidth - textWidth1) / 2.0F;
        ImGui.SetCursorPosX(centerPosX1);
        ImGui.Text(text1);

        String text4 = "once out of notepaper";
        float textWidth4 = ImGui.CalcTextSize(text1).getX();
        float centerPosX4 = (windowWidth - textWidth4) / 2.0F;
        ImGui.SetCursorPosX(centerPosX4);
        ImGui.Text(text4);

        ImGui.Separator();

        String text2 = "enable Banking to withdraw preset 2";
        float textWidth2 = ImGui.CalcTextSize(text2).getX();
        float centerPosX2 = (windowWidth - textWidth2) / 2.0F;
        ImGui.SetCursorPosX(centerPosX2);
        ImGui.Text(text2);

        String text3 = "and head of to necro ritual site";
        float textWidth3 = ImGui.CalcTextSize(text3).getX();
        float centerPosX3 = (windowWidth - textWidth3) / 2.0F;
        ImGui.SetCursorPosX(centerPosX3);
        ImGui.Text(text3);

        ImGui.SeparatorText("Items Gathered");

        // Calculate time elapsed in hours
        double hoursElapsed = (System.currentTimeMillis() - startTime) / 3600000.0;

        inventoryMap.forEach((itemName, count) -> {
            int perHour = (int) (count / hoursElapsed);  // Calculate the per-hour rate
            ImGui.Text(itemName + ": " + count + " (" + perHour + " per hour)");
        });
    }

    private static void booleanOptions() {
        createCenteredButton("World hop", () -> worldHop = !worldHop, worldHop, 138.0f);
        showTooltip("Automatically world hop when a player is nearby.");
    }


}
