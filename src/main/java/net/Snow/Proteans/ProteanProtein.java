package net.Snow.Proteans;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static net.Snow.ImGui.Experience.*;
import static net.Snow.Snow.shutdown;

public class ProteanProtein implements AbstractScript {

    @Override
    public void run(Snow script) {
        handleProteanProtein();
    }

    // another example of a method that could be used to interact with a different type of object depending on what is available
    // if multiple are available, it will interact with the closest one
    // Define a static regex pattern to match any string containing "protein" case-insensitively
    private static final Pattern PROTEAN_PATTERN = Pattern.compile("(?i).*protein.*");

    public static void handleProteanProtein() {
        // Query the inventory for items with ID 93 and filter them using the regex pattern
        ResultSet<Item> items = InventoryItemQuery.newQuery(93)
                .name(PROTEAN_PATTERN).results();

        // Check if the query returned any items
        if (items == null || items.isEmpty()) {
            ScriptConsole.println("No items matching 'protein' found in inventory.");
            return;
        }

        // Return when cooking interface is open
        if (Interfaces.isOpen(1251)) {
            ScriptConsole.println("Interface 1251 is open.");
            return;
        }

        // Query for cooking objects by options such as "Cook on", "Cook", or "Cook-at"
        List<SceneObject> cookOnObjects = SceneObjectQuery.newQuery()
                .option("Cook on")
                .results()
                .stream().toList();

        List<SceneObject> cookObjects = SceneObjectQuery.newQuery()
                .option("Cook")
                .results()
                .stream().toList();

        List<SceneObject> cookAtObjects = SceneObjectQuery.newQuery()
                .option("Cook-at")
                .results()
                .stream().toList();

        // Combine all results into a single list
        List<SceneObject> cookingObjects = new ArrayList<>();
        cookingObjects.addAll(cookOnObjects);
        cookingObjects.addAll(cookObjects);
        cookingObjects.addAll(cookAtObjects);

        // Find the closest cooking object
        SceneObject closestObject = cookingObjects.stream()
                .min(Comparator.comparingDouble(obj -> obj.getCoordinate().distanceTo(Client.getLocalPlayer().getCoordinate())))
                .orElse(null);

        if (closestObject != null) {
            // Determine the correct interaction action based on the available options
            String action = null;
            if (closestObject.getOptions().contains("Cook on")) {
                action = "Cook on";
            } else if (closestObject.getOptions().contains("Cook")) {
                action = "Cook";
            } else if (closestObject.getOptions().contains("Cook-at")) {
                action = "Cook-at";
            }

            if (action != null) {
                ScriptConsole.println(closestObject.getName() + " found. : Interacted: " + closestObject.interact(action));
                Execution.delayUntil(10000, () -> Interfaces.isOpen(1370));
            } else {
                ScriptConsole.println("No valid cooking options found for the closest object.");
            }
        } else {
            ScriptConsole.println("No cooking objects found.");
        }

        if (Interfaces.isOpen(1370)) {
            ScriptConsole.println("Interface 1370 is open.");
            selectInterface();
            Execution.delayUntil(10000, () -> Interfaces.isOpen(1251));
        }
    }


    private static void selectInterface() {
        // Query the interface component with ID 1370
        Component componentInterface = ComponentQuery.newQuery(1370).results().first();

        // Check if the component is found
        if (componentInterface == null) {
            ScriptConsole.println("[Error] Interface not found.");
            return;
        }

        // Retrieve the value of a player variable to determine the number of resources that can be made when the interface is open
        int canWeMakeAnything = VarManager.getVarValue(VarDomainType.PLAYER, 8847);

        // If the number of resources is zero, print a caution message and return
        if (canWeMakeAnything < 1) {
            shutdown();
        }

        // Attempt to interact with a dialog using the MiniMenu class
        boolean success = MiniMenu.interact(16, 0, -1, 89784350);

        // Print a success message if the interaction is successful, otherwise print an error message
        if (success) {
            ScriptConsole.println("Successfully interacted with Dialog!");
        } else {
            ScriptConsole.println("Failed to Interact with Dialog.");
        }
    }


    @Override
    public void initialize() {
        startingCookingXP.set(Skills.COOKING.getSkill().getExperience());
    }

    private static HashMap<String, Integer> inventoryMap = new HashMap<>();
    public AtomicInteger startingCookingXP = new AtomicInteger(0);

    @Override
    public void draw(SnowsTemplateGraphics sgc) {
        ImGui.SeparatorText("Cooking Stats");
        int currentLevel = Skills.COOKING.getSkill().getActualLevel();
        ImGui.Text("Current Level: " + currentLevel);
        int currentXP = Skills.COOKING.getSkill().getExperience();
        int xpForNextLevel = Skills.COOKING.getExperienceAt(currentLevel + 1);
        int xpTillNextLevel = xpForNextLevel - currentXP;
        ImGui.Text("XP remaining: " + xpTillNextLevel);
        displayXPGained(Skills.COOKING, startingCookingXP.get());
        displayXpPerHour(Skills.COOKING, startingCookingXP.get());
        ImGui.SeparatorText("Progress Bar");
        displayXpProgressBar(Skills.COOKING);
        ImGui.SeparatorText("Items Gathered");
        inventoryMap.forEach((itemName, count) -> {
            ImGui.Text(itemName + ": " + count);
        });
    }
}
