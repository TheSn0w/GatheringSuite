package net.Snow.Fishing;

import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.quest.Quest;
import net.botwithus.rs3.game.skills.Skill;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;

import java.util.Optional;

import static net.Snow.Banking.BankLocations.getSelectedBank;
import static net.Snow.Banking.BankLocations.setBank;
import static net.Snow.Fishing.FishingTypes.setSelectedFishingSpotByNameLocationAndType;
import static net.Snow.ImGui.CustomLogger.*;

public class AutoChange {

    public static boolean selectFishingSpotBasedOnLevel() {
        int playerFishingLevel = getPlayerFishingLevel();
        Optional<Quest> quest = Quest.byId(390);

        logGrey("Checking fishing level and quest status.");

        if (playerFishingLevel < 20) {
            FishingSpot currentSpot = FishingTypes.getSelectedFishingSpot();
            if (!"Net".equals(currentSpot.getFishingMethod()) && !"Draynor Village".equals(currentSpot.getLocationName()) && !currentSpot.getTypeIDs().contains(327)) {
                logOrange("Fishing level is below 20, switching to Net at Draynor Village");
                setSelectedFishingSpotByNameLocationAndType("Fishing spot", "Draynor Village", 327);
                if (!getSelectedBank().equals("Draynor")) {
                    logWarning("Bank is not set to Draynor. Changing bank to Draynor.");
                    setBank("Draynor");
                }
                logGrey("Fishing spot changed to Net at Draynor Village");
                return true;
            } else {
                logGrey("Fishing spot is already set to Net at Draynor Village. No change needed.");
            }
        } else if (playerFishingLevel >= 20 && playerFishingLevel < 52) {
            FishingSpot currentSpot = FishingTypes.getSelectedFishingSpot();
            if (!"Lure".equals(currentSpot.getFishingMethod()) && !"Barbarian Village".equals(currentSpot.getLocationName()) && !currentSpot.getTypeIDs().contains(328)) {
                logOrange("Fishing level is between 20 and 52, switching to Lure at Barbarian Village");
                setSelectedFishingSpotByNameLocationAndType("Fishing spot", "Barbarian Village", 328);
                if (!getSelectedBank().equals("Edgeville")) {
                    logWarning("Bank is not set to Edgeville. Changing bank to Edgeville.");
                    setBank("Edgeville");
                }
                logGrey("Fishing spot changed to Lure at Barbarian Village");
                return true;
            } else {
                logGrey("Fishing spot is already set to Lure at Barbarian Village. No change needed.");
            }
        } else if (playerFishingLevel >= 52 && playerFishingLevel < 93) {
            if (quest.isPresent() && quest.get().isComplete()) {
                logGrey("Quest is present and complete.");
                if (VarManager.getVarValue(VarDomainType.PLAYER, 7002) > 1290000) {
                    FishingSpot currentSpot = FishingTypes.getSelectedFishingSpot();
                    if (!"Menaphos (Vip)".equals(currentSpot.getLocationName()) && !currentSpot.getTypeIDs().contains(24574)) {
                        logGrey("Player has completed the quest: " + quest.get().name() + " and has enough reputation to enter VIP area.");
                        setSelectedFishingSpotByNameLocationAndType("Fishing spot", "Menaphos (Vip)", 24574);
                        if (!getSelectedBank().equals("Menaphos VIP")) {
                            logWarning("Bank is not set to Menaphos VIP. Changing bank to Menaphos VIP.");
                            setBank("Menaphos VIP");
                        }
                        logGrey("Fishing spot changed to Menaphos (Vip)");
                        return true;
                    } else {
                        logGrey("Fishing spot is already set to Menaphos (Vip). No change needed.");
                    }
                } else {
                    FishingSpot currentSpot = FishingTypes.getSelectedFishingSpot();
                    if (!"Menaphos (Non Vip)".equals(currentSpot.getLocationName()) && !currentSpot.getTypeIDs().contains(24572)) {
                        logGrey("Player has completed the quest: " + quest.get().name() + " but does not have enough reputation to enter VIP area.");
                        setSelectedFishingSpotByNameLocationAndType("Fishing spot", "Menaphos (Non Vip)", 24572);
                        if (!getSelectedBank().equals("Menaphos Main")) {
                            logWarning("Bank is not set to Menaphos Main. Changing bank to Menaphos Main.");
                            setBank("Menaphos Main");
                        }
                        logGrey("Fishing spot changed to Menaphos (Non Vip)");
                        return true;
                    } else {
                        logGrey("Fishing spot is already set to Menaphos (Non Vip). No change needed.");
                    }
                }
            } else {
                logGrey("Quest is not present or not complete.");
                FishingSpot currentSpot = FishingTypes.getSelectedFishingSpot();
                if (!"Lure".equals(currentSpot.getFishingMethod()) && !"Barbarian Village".equals(currentSpot.getLocationName()) && !currentSpot.getTypeIDs().contains(328)) {
                    logOrange("Fishing level is 52 or above but quest is not complete, switching to Lure at Barbarian Village");
                    setSelectedFishingSpotByNameLocationAndType("Fishing spot", "Barbarian Village", 328);
                    if (!getSelectedBank().equals("Edgeville")) {
                        logWarning("Bank is not set to Edgeville. Changing bank to Edgeville.");
                        setBank("Edgeville");
                    }
                    logGrey("Fishing spot changed to Lure at Barbarian Village");
                    return true;
                } else {
                    logGrey("Fishing spot is already set to Lure at Barbarian Village. No change needed.");
                }
            }
        } else if (playerFishingLevel >= 93) {
            if (playerFishingLevel <= 97) {
                FishingSpot currentSpot = FishingTypes.getSelectedFishingSpot();
                if (!"Small/Medium Urchins".equals(currentSpot.getSpotNames()) && !"Prif".equals(currentSpot.getLocationName()) && !currentSpot.getTypeIDs().contains(21779)) {
                    logOrange("Fishing level is between 93 and 97, switching to the highest level fishing spot");
                    setSelectedFishingSpotByNameLocationAndType("Fishing spot", "Prif", 21779);
                    if (!getSelectedBank().equals("Prif Waterfall")) {
                        logWarning("Bank is not set to Prif Waterfall. Changing bank to Prif Waterfall.");
                        setBank("Prif Waterfall");
                    }
                    logGrey("Fishing spot changed to Prif (93-97)");
                    return true;
                } else {
                    logGrey("Fishing spot is already set to Prif (93-97). No change needed.");
                }
            } else {
                FishingSpot currentSpot = FishingTypes.getSelectedFishingSpot();
                if (!"Large Urchins".equals(currentSpot.getSpotNames()) && !"Prif".equals(currentSpot.getLocationName()) && !currentSpot.getTypeIDs().contains(21780)) {
                    logOrange("Fishing level is above 97, switching to the highest level fishing spot");
                    setSelectedFishingSpotByNameLocationAndType("Fishing spot", "Prif", 21780);
                    if (!getSelectedBank().equals("Prif Waterfall")) {
                        logWarning("Bank is not set to Prif Waterfall. Changing bank to Prif Waterfall.");
                        setBank("Prif Waterfall");
                    }
                    logGrey("Fishing spot changed to Prif (97+)");
                    return true;
                } else {
                    logGrey("Fishing spot is already set to Prif Large Urchins. No change needed.");
                }
            }
        }
        logGrey("No change in fishing spot required.");
        return false;
    }

    private static int getPlayerFishingLevel() {
        Skill fishingSkill = Skills.FISHING.getSkill();
        return fishingSkill != null ? fishingSkill.getLevel() : 1;
    }
}