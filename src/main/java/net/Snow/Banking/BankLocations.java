package net.Snow.Banking;

import net.Snow.ScriptManager;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.Snow.Archaeology.Archaeology.ArchaeologyState.EXCAVATING;
import static net.Snow.Archaeology.Archaeology.setBotState;
import static net.Snow.Archaeology.MaterialTypes.getSelectedExcavationIndexByName;
import static net.Snow.Archaeology.MaterialTypes.selectedExcavationIndex;
import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.SnowsTemplateGraphics.selectedActivityIndex;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Navigation.movePlayerAwayFromAction;
import static net.Snow.Snow.*;
import static net.Snow.Spells.Lightform.disableLightForm;

public class BankLocations {

    public static int selectedBankIndex = 0; // Starts with the "Disabled" option selected

    public static List<Banking> bankings = new ArrayList<>();

    //    static {
//        bankings.add(new Banking("Disabled", -1, null, player.getCoordinate(), null));
//        bankings.add(new Banking("Seers Village", 25808, Banking.Type.SCENE_OBJECT, new Coordinate(2727, 3493, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Catherby", 24914, Banking.Type.SCENE_OBJECT, new Coordinate(2795, 3440, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Ardougne North", 34752, Banking.Type.SCENE_OBJECT, new Coordinate(2616, 3332, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Ardougne South", 34752, Banking.Type.SCENE_OBJECT, new Coordinate(2655, 3283, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Grand Exchange", 21059, Banking.Type.NPC, new Coordinate(3163, 3484, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Varrock West", 782, Banking.Type.SCENE_OBJECT, new Coordinate(3182, 3436, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Varrock East", 782, Banking.Type.SCENE_OBJECT, new Coordinate(3253, 3420, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Artisans Workshop", 85341, Banking.Type.SCENE_OBJECT, new Coordinate(3060, 3339, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Taverly", 66666, Banking.Type.SCENE_OBJECT, new Coordinate(2875, 3417, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Burthorpe", 25688, Banking.Type.SCENE_OBJECT, new Coordinate(2888, 3536, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Falador West", 11758, Banking.Type.SCENE_OBJECT, new Coordinate(2946, 3368, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Falador East", 11758, Banking.Type.SCENE_OBJECT, new Coordinate(3012, 3355, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Fort Forinthry", 125239, Banking.Type.SCENE_OBJECT, new Coordinate(3283, 3554, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Edgeville", 42377, Banking.Type.SCENE_OBJECT, new Coordinate(3097, 3496, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Al Kharid", 76274, Banking.Type.SCENE_OBJECT, new Coordinate(3270, 3168, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Draynor", 2012, Banking.Type.SCENE_OBJECT, new Coordinate(3092, 3245, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("War's Retreat", 114750, Banking.Type.SCENE_OBJECT, new Coordinate(3299, 10131, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Lumbridge", 79036, Banking.Type.SCENE_OBJECT, new Coordinate(3214, 3257, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Mining Guild Deposit Box", 25937, Banking.Type.SCENE_OBJECT, new Coordinate(1043, 4578, 0), Arrays.asList("Deposit-All")));
//        bankings.add(new Banking("Daemonheim", 25946, Banking.Type.NPC, new Coordinate(3449, 3719, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Anachronia", 113739, Banking.Type.SCENE_OBJECT, new Coordinate(5465, 2342, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Max Guild", 7691, Banking.Type.NPC, new Coordinate(2276, 3311, 1), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Priffdinas Center", 7084, Banking.Type.NPC, new Coordinate(2215, 3357, 1), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Priffdinas Traehorn Bank", 92692, Banking.Type.SCENE_OBJECT, new Coordinate(2232, 3310, 1), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Prif Waterfall", 96537, Banking.Type.SCENE_OBJECT, new Coordinate(2295, 3404, 2), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Menaphos VIP", 107737, Banking.Type.SCENE_OBJECT, new Coordinate(3182, 2742, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Menaphos Main", 108601, Banking.Type.SCENE_OBJECT, new Coordinate(3233, 2759, 0), Arrays.asList("Load Last Preset from", "Bank")));
//        bankings.add(new Banking("Archaeological Guild", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(3362, 3397, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Karid Et Bank Chest", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(3356, 3197, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Infernal Source", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(3254, 3499, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Everlight", 43807, Banking.Type.SCENE_OBJECT, new Coordinate(3700, 3214, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Stormguard Citadel", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(2675, 3405, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Warforge", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(2412, 2828, 0), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Burthorpe Metal Bank", 113259, Banking.Type.SCENE_OBJECT, new Coordinate(2887, 3506, 0), Arrays.asList("Deposit-all (into metal bank)")));
//        bankings.add(new Banking("Um Bank", 126506, Banking.Type.SCENE_OBJECT, new Coordinate(1110, 1739, 1), Arrays.asList("Load Last Preset from", "Use")));
//        bankings.add(new Banking("Port District", 107489, Banking.Type.SCENE_OBJECT, new Coordinate(3217, 2622, 0), Arrays.asList("Load Last Preset from", "Use")));
//
//    }
    static {
        bankings.add(new Banking("Disabled", -1, null, player.getCoordinate(), null));
        bankings.add(new Banking("Traehorn Bank", 92692, Banking.Type.SCENE_OBJECT, new Coordinate(2232, 3310, 1), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Al Kharid", 76274, Banking.Type.SCENE_OBJECT, new Coordinate(3270, 3168, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Anachronia", 113739, Banking.Type.SCENE_OBJECT, new Coordinate(5465, 2342, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Archaeological Guild", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(3362, 3397, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Ardougne North", 34752, Banking.Type.SCENE_OBJECT, new Coordinate(2616, 3332, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Ardougne South", 34752, Banking.Type.SCENE_OBJECT, new Coordinate(2655, 3283, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Artisans Workshop", 85341, Banking.Type.SCENE_OBJECT, new Coordinate(3060, 3339, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Burthorpe", 25688, Banking.Type.SCENE_OBJECT, new Coordinate(2888, 3536, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Burthorpe Metal Bank", 113259, Banking.Type.SCENE_OBJECT, new Coordinate(2887, 3506, 0), Arrays.asList("Deposit-all (into metal bank)")));
        bankings.add(new Banking("Crafting Guild Deposit Box", 89211, Banking.Type.SCENE_OBJECT, new Coordinate(2936, 3280, 0), Arrays.asList("Deposit-All")));
        bankings.add(new Banking("Catherby", 24914, Banking.Type.SCENE_OBJECT, new Coordinate(2795, 3440, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Daemonheim", 25946, Banking.Type.NPC, new Coordinate(3449, 3719, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Draynor", 2012, Banking.Type.SCENE_OBJECT, new Coordinate(3092, 3245, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Edgeville", 42377, Banking.Type.SCENE_OBJECT, new Coordinate(3097, 3496, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Everlight", 43807, Banking.Type.SCENE_OBJECT, new Coordinate(3700, 3214, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Falador East", 11758, Banking.Type.SCENE_OBJECT, new Coordinate(3012, 3355, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Falador West", 11758, Banking.Type.SCENE_OBJECT, new Coordinate(2946, 3368, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Fort Forinthry", 125239, Banking.Type.SCENE_OBJECT, new Coordinate(3283, 3554, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Grand Exchange", 21059, Banking.Type.NPC, new Coordinate(3163, 3484, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Infernal Source", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(3254, 3499, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Karid Et Bank Chest", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(3356, 3197, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Lumbridge", 79036, Banking.Type.SCENE_OBJECT, new Coordinate(3214, 3257, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Max Guild", 7691, Banking.Type.NPC, new Coordinate(2276, 3311, 1), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Menaphos Main", 108601, Banking.Type.SCENE_OBJECT, new Coordinate(3233, 2759, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Menaphos VIP", 107737, Banking.Type.SCENE_OBJECT, new Coordinate(3182, 2742, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Mining Guild Deposit Box", 25937, Banking.Type.SCENE_OBJECT, new Coordinate(1043, 4578, 0), Arrays.asList("Deposit-All")));
        bankings.add(new Banking("Menaphos Port District Chest", 107497, Banking.Type.SCENE_OBJECT, new Coordinate(3217, 2622, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Prif Waterfall", 96537, Banking.Type.SCENE_OBJECT, new Coordinate(2295, 3404, 2), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Prif Center", 7084, Banking.Type.NPC, new Coordinate(2215, 3357, 1), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Seers Village", 25808, Banking.Type.SCENE_OBJECT, new Coordinate(2727, 3493, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Stormguard Citadel", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(2675, 3405, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Taverly", 66666, Banking.Type.SCENE_OBJECT, new Coordinate(2875, 3417, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Um Bank", 126506, Banking.Type.SCENE_OBJECT, new Coordinate(1110, 1739, 1), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Varrock East", 782, Banking.Type.SCENE_OBJECT, new Coordinate(3253, 3420, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("Varrock West", 782, Banking.Type.SCENE_OBJECT, new Coordinate(3182, 3436, 0), Arrays.asList("Load Last Preset from", "Bank")));
        bankings.add(new Banking("War's Retreat", 114750, Banking.Type.SCENE_OBJECT, new Coordinate(3299, 10131, 0), Arrays.asList("Load Last Preset from", "Use")));
        bankings.add(new Banking("Warforge", 115427, Banking.Type.SCENE_OBJECT, new Coordinate(2412, 2828, 0), Arrays.asList("Load Last Preset from", "Use")));
    }

    public static Coordinate lastPlayerCoordinate = null;

    public static void goToBankToLoadLastPresetfrom() {
        LocalPlayer player = Client.getLocalPlayer();
        if (selectedBankIndex != 0) {
            lastPlayerCoordinate = player.getCoordinate();
            logBlue("Recording player coordinate: " + lastPlayerCoordinate);

            movePlayerAwayFromAction(player);

            Banking selectedBanking = bankings.get(selectedBankIndex);
            logBlue("Navigating to " + selectedBanking.getName() + " bank.");
            TraverseEvent.State state = Movement.traverse(NavPath.resolve(selectedBanking.getCoordinate()));
            logBlue("Traversal state: " + state);

            if (state == TraverseEvent.State.FINISHED) {
                boolean success = interactWithBank(selectedBanking, "Load Last Preset from") || interactWithBank(selectedBanking, "Deposit-All");
                if (success) {
                    if (selectedActivityIndex == 2) {
                        setBotState(EXCAVATING);
                    } else {
                        logBlue("Traversing back to recorded coordinate: " + lastPlayerCoordinate);
                        Movement.traverse(NavPath.resolve(lastPlayerCoordinate));
                    }
                }
            } else {
                logError("Failed to traverse to " + selectedBanking.getName() + " bank.");
            }
        }
    }

    public static void goToBankToUseOrBank() {
        LocalPlayer player = Client.getLocalPlayer();
        if (selectedBankIndex != 0) {
            disableLightForm();
            lastPlayerCoordinate = player.getCoordinate();
            logBlue("Recording player coordinate: " + lastPlayerCoordinate);

            movePlayerAwayFromAction(player);

            if (selectedActivityIndex == 2 && isSelectedExcavationGladiatorRemains()) {
                logOrange("Checking for Archaeology journal and outfit because we are at Gladiator remains excavation.");
                ResultSet<Item> results = InventoryItemQuery.newQuery(94).ids(49429).option("Remove").results();
                if (results.isEmpty()) {
                    logWarning("Archaeology journal not found in inventory.");
                    if (InventoryItemQuery.newQuery(94).ids(49941, 49942, 49943, 49945, 49944).results().isEmpty()) {
                        logError("No Archaeology outfit found equipped.");
                    } else {
                        //[Original]: DoAction(COMPONENT, 2, 0, 95944719)
                        component(2, 0, 95944719);
                        Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
                        if (Interfaces.isOpen(720)) {
                            //[Original]: DoAction(DIALOGUE, 0, -1, 47185921)
                            dialog(0, -1, 47185921);
                            Execution.delay(random.nextLong(4500, 6000));
                        }
                    }
                } else {
                    logWarning("Archaeology journal found in inventory.");
                    //[Original]: DoAction(COMPONENT, 2, 17, 95944719)
                    component(2, 17, 95944719);
                    Execution.delay(random.nextLong(4500, 6000));
                }
            }

            Banking selectedBanking = bankings.get(selectedBankIndex);
            logBlue("Navigating to " + selectedBanking.getName() + " bank.");
            TraverseEvent.State state = Movement.traverse(NavPath.resolve(selectedBanking.getCoordinate()));
            logBlue("Traversal state: " + state);

            if (state == TraverseEvent.State.FINISHED) {
                boolean success = interactWithBank(selectedBanking, "Use") || interactWithBank(selectedBanking, "Bank") || interactWithBank(selectedBanking, "Deposit-all (into metal bank)");
                if (success) {
                    if (selectedActivityIndex == 2) {
                        logWarning("Setting bot state to EXCAVATING");
                        setBotState(EXCAVATING);
                    } else {
                        logBlue("Traversing back to recorded coordinate: " + lastPlayerCoordinate);
                        MoveTo(lastPlayerCoordinate);
                    }
                }
            } else {
                logError("Failed to traverse to " + selectedBanking.getName() + " bank.");
            }
        }
    }


    static boolean interactWithBank(Banking banking, String interactionType) {
        // Check the type of the banking entity and call the appropriate interaction method
        if (banking.getType() == Banking.Type.SCENE_OBJECT) {
            return interactWithSceneObjectBank(banking, interactionType);
        } else if (banking.getType() == Banking.Type.NPC) {
            return interactWithNpcBank(banking, interactionType);
        }
        return false;
    }

    private static boolean interactWithSceneObjectBank(Banking banking, String interactionType) {
        // Query for scene objects with the specified banking ID
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(banking.getId()).results();
        logOrange("Querying for scene objects with ID: " + banking.getId());

        if (!results.isEmpty()) {
            logOrange("Found " + results.size() + " scene objects.");
            SceneObject sceneObject = results.nearest();
            if (sceneObject != null) {
                logOrange("Nearest scene object: " + sceneObject.getName() + " at " + sceneObject.getCoordinate());
                // Iterate through the interaction options and perform the specified interaction
                for (String option : banking.getInteractionOptions()) {
                    logOrange("Checking interaction option: " + option);
                    if (!option.equals(interactionType)) {
                        continue;
                    }
                    logOrange("Attempting to interact with: " + sceneObject.getName() + " using option: " + option);
                    if (sceneObject.interact(option)) {
                        logBlue("Interacted with: " + sceneObject.getName() + " using option: " + option);
                        switch (option) {
                            case "Load Last Preset from", "Deposit-all (into metal bank)" -> {
                                return Execution.delayUntil(5000, () -> !Backpack.isFull());
                            }
                            case "Use", "Bank" -> {
                                waitForBank();
                                return true;
                            }
                        }
                    } else {
                        logError("Failed to interact with: " + sceneObject.getName() + " using option: " + option);
                    }
                }
            } else {
                logError("No nearest scene object found.");
            }
        } else {
            logError("No suitable bank found.");
        }
        return false;
    }

    private static boolean interactWithNpcBank(Banking banking, String interactionType) {
        // Query for NPCs with the specified banking ID
        EntityResultSet<Npc> results = NpcQuery.newQuery().id(banking.getId()).results();
        if (!results.isEmpty()) {
            Npc npc = results.nearest();
            if (npc != null) {
                // Iterate through the interaction options and perform the specified interaction
                for (String option : banking.getInteractionOptions()) {
                    if (!option.equals(interactionType)) {
                        continue;
                    }
                    if (npc.interact(option)) {
                        logBlue("Interacted with: " + npc.getName() + " using option: " + option);
                        if (option.equals("Load Last Preset from")) {
                            return Execution.delayUntil(5000, () -> !Backpack.isFull());
                        } else if (option.equals("Use") || option.equals("Bank")) {
                            waitForBank();
                            return true;
                        }
                    }
                }
            }
        } else {
            logError("No suitable bank found.");
        }
        return false;
    }


    private static void waitForBank() {
        // Wait until the bank is open
        Execution.delayUntil(5000, Bank::isOpen);
        Execution.delay(random.nextLong(1250, 1500));
        logBlue("Bank is now open.");
        if (selectedActivityIndex == 0) { // Mining
            logBlue("Depositing all items for Mining.");
            MiningBank.depositAll();
        } else if (selectedActivityIndex == 1) { // Woodcutting
            logBlue("Depositing all items for Woodcutting.");
            WoodcuttingBank.depositAll();
        } else if (selectedActivityIndex == 2) { // Archaeology
            logBlue("Depositing all items for Archaeology.");
            ArchaeologyBank.depositAll();
        } else if (selectedActivityIndex == 3) {// Fishing
            logBlue("Depositing all items for Fishing.");
            FishingBank.depositAll();
        } else if (selectedActivityIndex == 100) {// Combat
            logBlue("Depositing all items for Combat.");
            CombatBank.depositAll();
        } else if (selectedActivityIndex == 5) {
            logBlue("Withdrawing preset 2 for Slime Collection to continue.");
            SlimeBank.withdrawPreset2();
        }
    }

    public static void goToBanktoLoadPreset2() {
        try {
            Banking selectedBanking = bankings.get(selectedBankIndex);
            logBlue("Navigating to " + selectedBanking.getName() + " bank.");
            TraverseEvent.State state = Movement.traverse(NavPath.resolve(selectedBanking.getCoordinate()));
            logBlue("Traversal state: " + state);

            if (state == TraverseEvent.State.FINISHED) {
                boolean success = interactWithBank(selectedBanking, "Use") || interactWithBank(selectedBanking, "Bank");
                if (success) {
                    Snow script = ScriptManager.getCurrentScript();
                    logError("Activity Index Before Change: " + selectedActivityIndex);
                    script.changeActivity(6); // Change to the desired activity index
                    logError("Activity Index after Change: " + selectedActivityIndex);
                }
            } else {
                logError("Failed to traverse to " + selectedBanking.getName() + " bank.");
            }
        } catch (Exception e) {
            logError("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void checkBankTabs() {
        int varbit45141 = VarManager.getVarbitValue(45141);
        int varbit45189 = VarManager.getVarbitValue(45189);
        int varbit45221 = VarManager.getVarbitValue(45139);

        if (varbit45141 != 1) {
            component(1, -1, 33882270);
            logBlue("Interacted with First Bank Tab");
            Execution.delay(random.nextLong(600, 800));
        } else {
            logGrey("First Bank tab is already selected, no interaction needed.");
        }

        if (varbit45189 != 2) {
            component(1, -1, 33882205);
            logBlue("Interacted with Transfer Option");
            Execution.delay(random.nextLong(600, 800));
        } else {
            logGrey("Transfer option is already selected, no interaction needed.");
        }

        if (varbit45221 != 0) {
            // [Original]: DoAction(COMPONENT, 1, -1, 33882168)
            component(1, -1, 33882168);
            logBlue("Interacted with Backpack Tab");
            Execution.delay(random.nextLong(600, 800));
        } else {
            logGrey("Backpack is already selected, no interaction needed.");
        }
    }

    public static void setBank(String bankName) {
        for (int i = 0; i < bankings.size(); i++) {
            if (bankings.get(i).getName().equalsIgnoreCase(bankName)) {
                selectedBankIndex = i;
                return;
            }
        }
        throw new IllegalArgumentException("Bank with name " + bankName + " not found.");
    }

    public static Banking getSelectedBank() {
        if (selectedBankIndex >= 0 && selectedBankIndex < bankings.size()) {
            return bankings.get(selectedBankIndex);
        }
        throw new IllegalStateException("No bank is currently selected.");
    }

    public static boolean isSelectedExcavationGladiatorRemains() {
        int gladiatorRemainsIndex = getSelectedExcavationIndexByName("Gladiator remains");
        return selectedExcavationIndex == gladiatorRemainsIndex;
    }
}