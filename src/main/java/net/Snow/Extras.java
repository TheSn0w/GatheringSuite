package net.Snow;

public class Extras {


/// PRAWNBROKER IN HOUSE TELEPORT
//        MiniMenu.interact(ObjectAction.OBJECT1.getType(), 96656, 11081, 2961);
//
//        Execution.delayUntil(5000, () -> Interfaces.isOpen(1306));
//
//        if (Interfaces.isOpen(1306)) {
//            completeBuilding();
//        }
//
//        Execution.delay(random.nextLong(600, 1000));
//
//
//        MiniMenu.interact(ObjectAction.OBJECT5.getType(), 96658, 11081, 2961);
//        Execution.delay(random.nextLong(600, 1000));
//
//        MiniMenu.interact(ObjectAction.OBJECT1.getType(), 96656, 11081, 2961);
//        Execution.delayUntil(5000, () -> Interfaces.isOpen(1306));
//
//        if (Interfaces.isOpen(1306)) {
//            completeBuilding();
//        }
//
//
//        Execution.delay(random.nextLong(600, 1000));
//
//
//        MiniMenu.interact(ObjectAction.OBJECT5.getType(), 96658, 11081, 2961);
//
//        Execution.delay(random.nextLong(600, 1000));
//
//        EntityResultSet<Npc> results = NpcQuery.newQuery().name("Demon butler").results();
//        if (!results.isEmpty()) {
//            results.nearest().interact("Fetch-from-bank");
//        } else {
//            logBlue("butler not found");
//        }
//
//        Execution.delayUntil(5000, () -> Interfaces.isOpen(1188));
//        if (Interfaces.isOpen(1188)) {
//            logOrange("Text: " + Dialog.getOptions());
//            Dialog.interact(0);
//        }
//
//
//
//
//        MiniMenu.interact(ObjectAction.OBJECT1.getType(), 96656, 11081, 2961);
//        Execution.delayUntil(5000, () -> Interfaces.isOpen(1306));
//
//        if (Interfaces.isOpen(1306)) {
//            completeBuilding();
//        }
//
//        Execution.delay(random.nextLong(600, 1000));
//
//        MiniMenu.interact(ObjectAction.OBJECT5.getType(), 96658, 11081, 2961);
//
//        Execution.delayUntil(5000, () -> Backpack.getCount("Mahogany plank") >= 16);
//
//    }

    ///  TELEPORTING FOR MAGIC XP
//        int magicLevel = Skills.MAGIC.getSkill().getActualLevel();
//        if (teleportIfPossible(magicLevel, "Camelot Teleport", 45)) return;
//        if (teleportIfPossible(magicLevel, "Falador Teleport", 37)) return;
//        if (teleportIfPossible(magicLevel, "Lumbridge Teleport", 31)) return;
//        if (teleportIfPossible(magicLevel, "Varrock Teleport", 25)) return;
//        if (teleportIfPossible(magicLevel, "Taverly Teleport", 20)) return;
//    }
//
//    private boolean teleportIfPossible(int magicLevel, String ability, int requiredLevel) {
//        if (magicLevel >= requiredLevel && ActionBar.containsAbility(ability) && ActionBar.getCooldownPrecise(ability) == 0) {
//            int lawRuneCount = Backpack.getQuantity("Law rune");
//            if (lawRuneCount > 0 && ActionBar.getCooldownPrecise(ability) == 0) {
//                logSuccess("Law runes available: " + lawRuneCount + " " + ActionBar.getCooldownPrecise(ability));
//                long startTime = System.currentTimeMillis();
//                ActionBar.useAbility(ability);
//                Execution.delay(random.nextLong(700, 1300));
//                long endTime = System.currentTimeMillis();
//                long duration = endTime - startTime;
//                logOrange("Magic Level: " + magicLevel + ", Teleporting to " + ability.replace(" Teleport", "") + ", Time to teleport: " + duration + " ms");
//
//                int xpGained = getXpForAbility(ability);
//                int xpToLevel59 = 247886 - Skills.MAGIC.getSkill().getExperience();
//                int castsLeft = xpToLevel59 / xpGained;
//                logBlue("XP gained: " + xpGained + ", Casts left until level 59: " + castsLeft);
//
//                return true;
//            } else {
//                logOrange("Not enough Law runes to cast " + ability);
//            }
//        }
//        return false;
//    }
//
//    private int getXpForAbility(String ability) {
//        switch (ability) {
//            case "Camelot Teleport":
//                return 56;
//            case "Falador Teleport":
//                return 48;
//            case "Lumbridge Teleport":
//                return 41;
//            case "Varrock Teleport":
//                return 35;
//            case "Taverly Teleport":
//                return 38;
//            default:
//                return 0;
//        }
//    }

//        component(1, -1, 93716700);
//        Execution.delay(random.nextLong(400, 600));

//        if (player.isMoving()) {
//            return;
//        }
//
//        switch (combatState) {
//            case COMBAT:
//                if (!player.hasTarget()) {
//                    handleCombat(player);
//                }
//                if (isHealthLow(player))
//                    eatFood(player);
//                return;
//            case BANKING:
//                if (selectedBankIndex == 0) {
//                    logError("No bank selected.");
//                    return;
//                } else {
//                    goToBankToUseOrBank();
//                }
//                return;
//            default:
//                break;
//        }
//
//    private void completeBuilding() {
//        logBlue("Attempting to complete building.");
//        if (Interfaces.isOpen(1306)) {
//            logBlue("Build interface is open. Completing building.");
//            dialog(0, 4, 85590029);
//            Execution.delayUntil(5000, () -> player.getAnimationId() == -1);
//            Execution.delay(random.nextLong(600, 800));
//        } else {
//            logError("Build interface is not open.");
//        }
//    }
}
