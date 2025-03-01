package net.Snow.Mining;

import net.Snow.AbstractScript;
import net.Snow.Banking.BankLocations;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Navigation;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.Headbar;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.Regex;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Banking.DepositBox.getNearestReachableDepositBox;
import static net.Snow.Banking.DepositBox.handleDepositBox;
import static net.Snow.Banking.Porters.checkPorters;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Mining.Crystallise.findRockForCrystallise;
import static net.Snow.Mining.DropOres.dropAllOres;
import static net.Snow.Mining.Familiar.summonFamiliar;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Mining.GemBag.fillGemBag;
import static net.Snow.Mining.GemBag.gemBag;
import static net.Snow.Mining.MiningVariables.*;
import static net.Snow.Mining.OreBox.ORE_BOXES_PATTERN;
import static net.Snow.Mining.OreBox.fillOreBox;
import static net.Snow.Mining.RockTypes.rockTypes;
import static net.Snow.Mining.Scanner.updateSceneObjectTableData;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Navigation.movePlayerAwayFromAction;
import static net.Snow.Snow.*;
import static net.Snow.Spells.Lightform.applyLightForm;
import static net.Snow.Spells.Lightform.useLightForm;
import static net.Snow.Woodcutting.Crystallise.useCrystallise;

public class Mining implements AbstractScript {

    Snow script;

    @Override
    public void initialize() {
        startingMiningXP.set(Skills.MINING.getSkill().getExperience());
        startingMiningLevel.set(Skills.MINING.getSkill().getLevel());
        startTracking();
    }

    public AtomicInteger startingMiningXP = new AtomicInteger(0);
    public AtomicInteger startingMiningLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        MiningGraphics.draw(sgc, script, startingMiningLevel, startingMiningXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        MiningUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        MiningUpdates.onChatMessageEvent(event, inventoryMap);
    }

//    @Override
//    public int getLoopDelay() {
//        return 100;
//    }

    @Override
    public void run(Snow script) {
        LocalPlayer player = Client.getLocalPlayer();
        updateSceneObjectTableData(player);
        capturestuff();

        if (Interfaces.isOpen(1189)) {
            dialog(0, -1, 77922323);
        }

        SpotAnimation crystallise = SpotAnimationQuery.newQuery().ids(5802, 5804).results().first();
        boolean crystalliseCast = false;
        if (crystallise == null && useCrystallise) {
            SceneObject rock = findRockForCrystallise(player);
            if (rock != null) {
                crystalliseCast = Crystallise.applyCrystallise(rock, player);
            }
        }

        if (!crystalliseCast) {
            handleMining();
        } else {
            // Ensure mining interaction after Crystallise
            SceneObject rock = findRockForCrystallise(player);
            if (rock != null) {
                interactWithNearestRock(SceneObjectQuery.newQuery().name(rock.getName()).option("Mine").results(), " - After Crystallise");
            }
        }

        if (useLightForm) {
            applyLightForm(player);
        }

        if (useJujuPotion) {
            int jujuPotionTimeRemaining = VarManager.getVarValue(VarDomainType.PLAYER, 4908);
            int jujuPotionTimeRemainingInMinutes = (jujuPotionTimeRemaining * 15) / 60;
            logBlue("Perfect Juju Mining Potion Time Remaining: " + jujuPotionTimeRemainingInMinutes + " minutes");
            if (!InventoryItemQuery.newQuery(93).name(PERFECT_JUJU_MINING_POTION_PATTERN).results().isEmpty() && VarManager.getVarValue(VarDomainType.PLAYER, 4908) <= 1) {
                Item jujuPotion = InventoryItemQuery.newQuery(93).name(PERFECT_JUJU_MINING_POTION_PATTERN).results().first();
                if (jujuPotion != null) {
                    logSuccess("Drinking: " + jujuPotion.getName());
                    Backpack.interact(jujuPotion.getName(), "Drink");
                    Execution.delay(random.nextLong(1000, 2000));
                }
                return;
            }
        }

        if (useFamiliar) {
            logBlue("Familiar time reminaing: " + VarManager.getVarbitValue(6055) + " minutes");
            if (VarManager.getVarbitValue(6055) <= 1) {
                summonFamiliar();
            }
        }

        if (usePorters) {
            checkPorters();
        }

        if (Backpack.isFull() && !Backpack.contains(Regex.getPatternForContainsString("Sign of the Porter"))) {
            if (fillGemBag() && !Backpack.isFull()) {
                return;
            }

            Item oreBox = InventoryItemQuery.newQuery().name(ORE_BOXES_PATTERN).results().first();

            if (oreBox == null && gemBag == null) {
                logBlue("Ore box nor Gem bag found, Checking bank index.");
                if (BankLocations.selectedBankIndex == 0) {
                    logBlue("Banking is disabled. Dropping all Ores.");
                    dropAllOres();
                } else {
                    logBlue("Banking is enabled. Going to bank to Load Last Preset.");
                    BankLocations.goToBankToLoadLastPresetfrom();
                }
                return;
            }

            if (fillOreBox()) {
                logBlue("Ore box filled. Rechecking backpack status.");
            }

            if (Backpack.isFull()) {
                if (BankLocations.selectedBankIndex == 0) {
                    logBlue("Banking is disabled. Dropping all Ores.");
                    dropAllOres();
                } else {
                    SceneObject nearestBox = getNearestReachableDepositBox();
                    if (nearestBox != null) {
                        if (Backpack.contains(ORE_BOXES_PATTERN)) {
                            logBlue("Ore box found in backpack. Depositing all ores nearby at: " + nearestBox.getName());
                            handleDepositBox(nearestBox);
                        } else {
                            nearestBox.interact("Deposit-All");
                            logBlue("Interacted with Deposit Box: " + nearestBox.getName());
                            Execution.delayUntil(random.nextLong(30000), () -> !Backpack.isFull());
                        }
                    } else {
                        BankLocations.goToBankToUseOrBank();
                    }
                }
            }
        } else {
            handleMining();
        }
    }

//    private void handleFullBackpack() {
//        if (BankLocations.selectedBankIndex == 0) {
//            logBlue("Banking is disabled. Dropping all Fish.");
//            dropAllFish();
//        } else {
//            SceneObject nearestBox = getNearestReachableDepositBox();
//            if (nearestBox != null) {
//                if (Backpack.contains("Fishing bait") || Backpack.contains("Feather")) {
//                    handleDepositBox(nearestBox);
//                } else {
//                    nearestBox.interact("Deposit-All");
//                    Execution.delayUntil(random.nextLong(30000), () -> !Backpack.isFull());
//                }
//            } else {
//                BankLocations.goToBankToUseOrBank();
//            }
//        }
//    }


    public static void handleMining() {
        LocalPlayer player = Client.getLocalPlayer();
        int playerMiningLevel = Skills.MINING.getSkill().getActualLevel();

        if (doAutomaticLevelling) {
            // Auto-levelling based on player level
            if (playerMiningLevel >= 89) {
                RockTypes.setSelectedRockByName("Seren stone", "Priffdinas");
            } else if (playerMiningLevel >= 70) {
                if (Skills.DUNGEONEERING.getActualLevel() >= 75) {
                    logGrey("Dungeoneering level is above 75, selecting Al Kharid Resource Dungeon location for Necrite Rock.");
                    RockTypes.setSelectedRockByName("Necrite rock", "Al Kharid Resource Dungeon");
                } else {
                    if (player.getCombatLevel() > 130) {
                        logGrey("Combat level is above 130, selecting Wilderness location for Necrite Rock.");
                        RockTypes.setSelectedRockByName("Necrite rock", "Wilderness");
                    } else {
                        logGrey("Dungeoneering level is below 75, and combat is below 130, Mining Drakolith in the Mining Guild.");
                        if (Skills.DUNGEONEERING.getActualLevel() >= 45) {
                            RockTypes.setSelectedRockByName("Drakolith rock", "Mining Guild");
                        } else {
                            RockTypes.setSelectedRockByName("Orichalcite rock", "Mining Guild");
                        }
                    }
                }
            } else if (playerMiningLevel >= 60) {
                RockTypes.setSelectedRockByName("Orichalcite rock", "Mining Guild");
            } else if (playerMiningLevel >= 50) {
                RockTypes.setSelectedRockByName("Runite rock", "Wilderness");
            } else if (playerMiningLevel >= 40) {
                RockTypes.setSelectedRockByName("Adamantite rock", "Varrock South-East Mine");
            } else if (playerMiningLevel >= 30) {
                RockTypes.setSelectedRockByName("Mithril rock", "Varrock South-West Mine");
            } else if (playerMiningLevel >= 10) {
                RockTypes.setSelectedRockByName("Iron rock", "Burthorpe");
            } else {
                RockTypes.setSelectedRockByName("Copper rock", "Burthorpe");
            }
        }

        Navigation selectedRock = rockTypes.get(RockTypes.getSelectedRockIndex());



        if (selectedRock.getName().equals("Disabled")) {
            logError("No rock type selected.");
            return;
        }

        if (!selectedRock.hasRequiredLevel(playerMiningLevel)) {
            logError("Player does not meet the minimum level requirement for: " + selectedRock.getName());
            return;
        }

        EntityResultSet<SceneObject> mineableRocks = SceneObjectQuery.newQuery().name(selectedRock.getName()).option("Mine").results();

        EntityResultSet<SpotAnimation> spotAnimations = SpotAnimationQuery.newQuery().ids(7164, 7165).results();

        if (player.isMoving()) {
            logWarning("Player is moving.");
            return;
        }

        SceneObject nearestRock = mineableRocks.nearest();
        if (nearestRock == null || nearestRock.distanceTo(player) > 5.0) {
            logBlue("No suitable rocks found within 5 tiles. Traversing to: " + selectedRock.getName() + " location.");
            movePlayerAwayFromAction(player);
            MoveTo(selectedRock.getCoordinate());
            return;
        }

        Optional<Headbar> headbar = player.getHeadbars().stream()
                .filter(bar -> bar.getId() == 5 && bar.getWidth() < MiningGraphics.headbarWidthThreshold)
                .findAny();

        if (headbar.isPresent()) {
            logOrange("Interacting with rock at stamina: " + headbar.get().getWidth());
            interactWithNearestRock(mineableRocks, " - Stamina is low");
            Execution.delay(random.nextLong(1000, 2000));
        }
//        if (headbar.isPresent()) {
//            logOrange("Interacting with rock at stamina: " + headbar.get().getWidth());
//            interactWithNearestRock(mineableRocks, " - Stamina is low");
//            Execution.delay(random.nextLong(1000, 2000));
//        }

        if (!spotAnimations.isEmpty() && useRockertunities) {
            interactWithSpotAnimations(spotAnimations, mineableRocks);
        } else if (player.getAnimationId() == -1) {
            interactWithNearestRock(mineableRocks, " - Player is not animating");
        }
    }

    public static void interactWithNearestRock(EntityResultSet<SceneObject> rocks, String reason) {
        // Find the nearest rock and interact with it
        SceneObject nearestRock = rocks.nearest();
        if (nearestRock != null) {
            logSuccess("Interacted with: " + nearestRock.getName() + reason + ": " + nearestRock.interact("Mine"));
            Execution.delay(random.nextLong(1000, 2000));
        }
    }

    private static void interactWithSpotAnimations(EntityResultSet<SpotAnimation> animations, EntityResultSet<SceneObject> mineableRocks) {
        // Find the first spot animation and interact with the corresponding rock
        SpotAnimation currentAnimation = animations.first();
        mineableRocks.stream().filter(rock -> rock.getCoordinate().equals(currentAnimation.getCoordinate())).findFirst().ifPresent(rock -> {
            logBlue("Interacted with: " + rock.getName() + " - Rockertunity Appeared: " + rock.interact("Mine"));
            Execution.delayUntil(random.nextLong(3000, 5000), () -> !currentAnimation.validate());
        });
    }
}
