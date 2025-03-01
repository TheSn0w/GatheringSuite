package net.Snow.Banking;

import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.regex.Pattern;

import static net.Snow.Banking.BankLocations.*;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.component;
import static net.Snow.Snow.random;


public class Porters {

    public static boolean usePorters = false;

    private static void setBankTabAndComponent() {
        // Set to first tab in the bank if not already set
        if (VarManager.getVarbitValue(45141) != 1) {
            logBlue("Setting to first tab in the bank.");
            component(1, -1, 33882277);
            Execution.delay(random.nextLong(850, 1250));
        }

        // Set the component to withdraw all porters
        if (VarManager.getVarbitValue(45189) != 7) {
            logBlue("Setting to the correct component to withdraw porters.");
            component(1, -1, 33882215);
            Execution.delay(random.nextLong(850, 1250));
        }
    }


    public static void handlePorterWithdrawals() {
        setBankTabAndComponent();

        boolean withdrew = false;
        String currentPorter = null;

        // Check and withdraw the first available porter using the pattern
        ResultSet<Item> porters = InventoryItemQuery.newQuery(95).name(PORTER_PATTERN).results();
        for (Item porter : porters) {
            int count = porter.getStackSize();
            if (count > 0) {
                logBlue("Available in bank: " + porter.getName() + " - Quantity: " + count);
                ResultSet<Item> inventoryPorters = InventoryItemQuery.newQuery(95).name(porter.getName()).results();
                if (!inventoryPorters.isEmpty()) {
                    Item inventoryPorter = inventoryPorters.first();
                    String itemName = inventoryPorter.getName();
                    int itemSlot = inventoryPorter.getSlot();

                    int GraceStatus = VarManager.getVarbitValue(52157);
                    if (GraceStatus == 1) {
                        logOrange("Grace of the Elves is upgraded to 2000 slots");
                        Bank.withdraw(PORTER_PATTERN, 4);
                        Execution.delay(random.nextLong(650, 1000)); // Small delay between interactions
                        Bank.withdraw(PORTER_PATTERN, 4);
                    } else {
                        logOrange("Grace of the Elves is normal with 500 slots");
                        Bank.withdraw(PORTER_PATTERN, 4);
                    }

                    logBlue("Withdrew: " + itemName + " from slot: " + itemSlot);
                    withdrew = true;
                    currentPorter = itemName;
                    Bank.close();
                    Execution.delay(random.nextLong(1250, 1500)); // Add a delay to ensure the bank count updates
                    handleGraceOfTheElves(currentPorter);
                    break;
                }
            }
        }

        // Log error if no porters were withdrawn
        if (!withdrew) {
            logError("Failed to withdraw any porters.");
        }
    }


    private static void handleGraceOfTheElves(String currentPorter) {
        if (Equipment.contains("Grace of the elves")) {
            int GraceStatus = VarManager.getVarbitValue(52157);
            // Check if Grace of the Elves is equipped and its charges
            if (GraceStatus == 1) {
                int currentGraceCharges = VarManager.getInvVarbit(94, 2, 30214);
                if (currentGraceCharges < 1500) {
                    logBlue("Porters have " + currentGraceCharges + " charges. Charging.");
                    boolean interacted = Equipment.interact(Equipment.Slot.NECK, "Charge all porters");
                    if (interacted) {
                        logSuccess("Interaction with Equipment was successful.");
                        Execution.delayUntil(1500, () -> VarManager.getInvVarbit(94, 2, 30214) >= 1500); // Wait until charges update

                        currentGraceCharges = VarManager.getInvVarbit(94, 2, 30214); // Check again
                        if (currentGraceCharges < 1500) {
                            logWarning("Insufficient charges in Backpack after interaction. Reopening bank to withdraw more porters.");
                            Banking selectedBanking = bankings.get(selectedBankIndex);
                            if (interactWithBank(selectedBanking, "Use") || interactWithBank(selectedBanking, "Bank") || interactWithBank(selectedBanking, "Deposit-all (into metal bank)")) {
                                handlePorterWithdrawals(); // Retry withdrawal
                            }
                        }
                    } else {
                        logError("Interaction with Equipment failed.");
                    }
                }
            } else if (GraceStatus == 0) {
                int currentGraceCharges = VarManager.getInvVarbit(94, 2, 30214);
                if (currentGraceCharges < 450) {
                    logBlue("Porters have " + currentGraceCharges + " charges. Charging.");
                    boolean interacted = Equipment.interact(Equipment.Slot.NECK, "Charge all porters");
                    if (interacted) {
                        logSuccess("Interaction with Equipment was successful.");
                        Execution.delayUntil(10000, () -> VarManager.getInvVarbit(94, 2, 30214) >= 450); // Wait until charges update

                        currentGraceCharges = VarManager.getInvVarbit(94, 2, 30214); // Check again
                        logBlue("Current Grace of the elves charges: " + currentGraceCharges);
                        if (net.botwithus.api.game.hud.inventories.Backpack.getCount(currentPorter) <= 10) {
                            logWarning("Insufficient charges in Backpack after interaction. Reopening bank to withdraw more porters.");
                            Banking selectedBanking = bankings.get(selectedBankIndex);
                            if (interactWithBank(selectedBanking, "Use") || interactWithBank(selectedBanking, "Bank") || interactWithBank(selectedBanking, "Deposit-all (into metal bank)")) {
                                handlePorterWithdrawals(); // Retry withdrawal
                            }
                        } else {
                            logPink("Backpack get Count: " + net.botwithus.api.game.hud.inventories.Backpack.getCount(currentPorter));
                        }
                    } else {
                        logError("Interaction with Equipment failed.");
                    }
                }
            }
        } else {
            // Grace of the Elves not equipped
            int currentPorterCharges = VarManager.getInvVarbit(94, 17, 18361);
            if (currentPorterCharges < 1) {
                boolean interacted = Backpack.interact(currentPorter, "Wear");
                if (interacted) {
                    boolean equipped = Execution.delayUntil(5000, () -> Equipment.contains(currentPorter));
                    if (equipped) {
                        logSuccess("Equipping the porter was successful.");
                    } else {
                        logError("Interacted but failed to see the porter count component.");
                    }
                } else {
                    logError("Failed to wear the item.");
                }
            }
        }
    }


    public static void quickPorterWithdraw() {
        setBankTabAndComponent();

        boolean withdrew = false;

        // Check and withdraw the first available porter using the pattern
        ResultSet<Item> porters = InventoryItemQuery.newQuery(95).name(PORTER_PATTERN).results();
        for (Item porter : porters) {
            int count = porter.getStackSize();
            if (count > 0) {
                logBlue("Available in bank: " + porter.getName() + " - Quantity: " + count);
                ResultSet<Item> inventoryPorters = InventoryItemQuery.newQuery(95).name(porter.getName()).results();
                if (!inventoryPorters.isEmpty()) {
                    Item inventoryPorter = inventoryPorters.first();
                    String itemName = inventoryPorter.getName();
                    int itemSlot = inventoryPorter.getSlot();
                    Bank.withdraw(PORTER_PATTERN, 4);
                    Execution.delay(random.nextLong(650, 1000)); // Small delay between interactions
                    logBlue("Withdrew: " + itemName + " from slot: " + itemSlot);
                    withdrew = true;
                    break;
                }
            }
        }

        // Log error if no porters were withdrawn
        if (!withdrew) {
            logError("Failed to withdraw any porters.");
        }
    }

    private static final Pattern PORTER_PATTERN = Pattern.compile(
            "Sign of the porter (VII|VI|V|IV|III|II|I)",
            Pattern.CASE_INSENSITIVE
    );


    public static void checkPorters() {
        // Check porters in backpack
        ResultSet<Item> portersInBackpack = InventoryItemQuery.newQuery(93).name(PORTER_PATTERN).results();
        boolean hasPortersInBackpack = !portersInBackpack.isEmpty();
        String porterName = hasPortersInBackpack ? portersInBackpack.first().getName() : "None";
        int porterCount = hasPortersInBackpack ? portersInBackpack.size() : 0;
        logGrey("Porters in backpack: " + hasPortersInBackpack + " (Count: " + porterCount + ", Name: " + porterName + ")");

        // Check if Grace of the Elves is equipped
        boolean hasGraceOfTheElves = Equipment.contains("Grace of the elves");
        logGrey("Grace of the elves in equipment: " + hasGraceOfTheElves);

        if (hasPortersInBackpack) {
            if (hasGraceOfTheElves) {
                int GraceStatus = VarManager.getVarbitValue(52157);
                int currentGraceCharges = VarManager.getInvVarbit(94, 2, 30214);
                logGrey("Current Grace of the elves charges: " + currentGraceCharges);
                if (GraceStatus == 1) {
                    int randomChargeThreshold = random.nextInt(1451) + 500; // Random between 500 and 1950
                    logGrey("Generated random charge threshold (upgraded): " + randomChargeThreshold);
                    if (currentGraceCharges < randomChargeThreshold) {
                        logGrey("Current charges (" + currentGraceCharges + ") are less than threshold (" + randomChargeThreshold + "). Charging all porters.");
                        //[Original]: DoAction(COMPONENT, 6, 2, 95944719)
                        boolean interacted = Equipment.interact(Equipment.Slot.NECK, "Charge all porters");
                        if (interacted) {
                            logSuccess("Successfully interacted with Equipment to charge all porters.");
                        } else {
                            logError("Failed to interact with Equipment to charge all porters.");
                        }
                    }
                } else {
                    int randomChargeThreshold = random.nextInt(450) + 1; // Random between 1 and 450
                    logGrey("Generated random charge threshold (non-upgraded): " + randomChargeThreshold);
                    if (currentGraceCharges < randomChargeThreshold) {
                        logGrey("Current charges (" + currentGraceCharges + ") are less than threshold (" + randomChargeThreshold + "). Charging all porters.");
                        boolean interacted = Equipment.interact(Equipment.Slot.NECK, "Charge all porters");
                        if (interacted) {
                            logSuccess("Successfully interacted with Equipment to charge all porters.");
                        } else {
                            logError("Failed to interact with Equipment to charge all porters.");
                        }
                    }
                }
            } else {
                ResultSet<Item> portersInEquipment = InventoryItemQuery.newQuery(94).name(PORTER_PATTERN).results();
                boolean portersWorn = !portersInEquipment.isEmpty();
                String wornPorterName = portersWorn ? portersInEquipment.first().getName() : "None";
                logGrey("Porters worn: " + portersWorn + " (Name: " + wornPorterName + ")");

                if (!portersWorn) {
                    logGrey("Wearing porter: " + porterName);
                    Backpack.interact(porterName, "Wear");
                } else {
                    int currentPorterCharges = VarManager.getInvVarbit(94, 17, 18361);
                    logGrey("Current porter charges: " + currentPorterCharges);
                }
            }
        } else {
            if (hasGraceOfTheElves) {
                int GraceStatus = VarManager.getVarbitValue(52157);
                int currentGraceCharges = VarManager.getInvVarbit(94, 2, 30214);
                logGrey("Current Grace of the elves charge: " + currentGraceCharges);
                logGrey("Grace of the elves Upgraded: " + (GraceStatus == 1));


                if (currentGraceCharges < 1) {
                    if (selectedBankIndex == 0) {
                        logError("No porters found in inventory, you must have a bank selected to withdraw porters.");
                    } else {
                        logWarning("No porters found in inventory. Going to bank to withdraw.");
                        goToBankToUseOrBank();
                    }
                }
            } else {
                ResultSet<Item> portersInEquipment = InventoryItemQuery.newQuery(94).name(PORTER_PATTERN).results();
                boolean portersWorn = !portersInEquipment.isEmpty();
                String wornPorterName = portersWorn ? portersInEquipment.first().getName() : "None";
                logGrey("Porters worn: " + portersWorn + " (Name: " + wornPorterName + ")");

                if (!portersWorn) {
                    if (selectedBankIndex == 0) {
                        logError("No porters found in inventory, you must have a bank selected to withdraw porters.");
                    } else {
                        logGrey("No porters found in inventory. Going to bank to withdraw.");
                        goToBankToUseOrBank();
                    }
                } else {
                    int currentPorterCharges = VarManager.getInvVarbit(94, 17, 18361);
                    logGrey("Current porter charges: " + currentPorterCharges);
                }
            }
        }
    }
}

