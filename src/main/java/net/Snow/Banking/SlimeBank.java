package net.Snow.Banking;

import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.script.Execution;

import static net.Snow.Banking.BankLocations.checkBankTabs;
import static net.Snow.ImGui.CustomLogger.logOrange;
import static net.Snow.Snow.random;

public class SlimeBank {

    public static void withdrawPreset2() {
        if (Bank.isOpen()) {
            Execution.delay(random.nextLong(500, 1000));
            checkBankTabs();
            logOrange("Withdrawing Preset 2");
            //[Original]: DoAction(COMPONENT, 1, 2, 33882231)
            Bank.loadPreset(2);
            Execution.delay(random.nextLong(500, 1000));
            Bank.close();
        }
    }
}
