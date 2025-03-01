package net.Snow.Combat;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Combat.Combat.player;
import static net.Snow.Combat.Variables.healthPointsThreshold;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Abilities {

    public static boolean useWeaponSpecialAttack = false;
    public static boolean useEssenceofFinality = false;
    public static boolean useVolleyofSouls = false;
    public static boolean useInvokeDeath = false;


    private static Map<String, AbilityIndex> abilityIndices = new HashMap<>();

    private static class AbilityIndex {
        private final int interfaceIndex;
        private final int componentIndex;

        public AbilityIndex(int interfaceIndex, int componentIndex) {
            this.interfaceIndex = interfaceIndex;
            this.componentIndex = componentIndex;
        }

        public int interfaceIndex() {
            return interfaceIndex;
        }

        public int componentIndex() {
            return componentIndex;
        }
    }

    public static void manageCombatAbilities() {

        if (player.hasTarget() && player.inCombat()) {

                if (useInvokeDeath) {
                    setup("Invoke Death");
                    invokeDeath();
                }
                if (useVolleyofSouls) {
                    setup("Volley of Souls");
                    volleyOfSouls();
                }
                if (useEssenceofFinality) {
                    setup("Essence of Finality");
                    essenceOfFinality();
                }
                if (useWeaponSpecialAttack) {
                    setup("Weapon Special Attack");
                    DeathEssence();
                }
                if (useDefensives) {
                    useDefensives();
                }
            }
    }


    public static void setup(String abilityName) {
        int spriteID = ActionBar.getActionSprite(ActionBar.getActionStruct(abilityName).getParams());

        Component component = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673)
                .spriteId(spriteID)
                .option("Customise-keybind")
                .results()
                .first();

        if (component != null) {
            int interfaceIndex = component.getInterfaceIndex();
            int componentIndex = component.getComponentIndex();
            abilityIndices.put(abilityName, new AbilityIndex(interfaceIndex, componentIndex));
        }
    }

    public static void interactWithAbility(String abilityName) {
        AbilityIndex index = abilityIndices.get(abilityName);
        if (index != null) {
            logOrange("Interacting with ability: " + abilityName);
            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, (index.interfaceIndex() << 16 | index.componentIndex()));
        } else {
            logError("Ability not found in the map: " + abilityName);
        }
    }


    public static void invokeDeath() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (VarManager.getVarbitValue(53247) == 0
                && player.inCombat()
                && ComponentQuery.newQuery(284).spriteId(30100).results().isEmpty()
                && ActionBar.getCooldownPrecise("Invoke Death") == 0
                && player.hasTarget()
                && player.getFollowing() != null
                && player.getFollowing().getCurrentHealth() > 5000) {
            interactWithAbility("Invoke Death");
            boolean effectConfirmed = Execution.delayUntil(random.nextLong(4000, 5000), () -> VarManager.getVarbitValue(53247) == 1);
            if (effectConfirmed) {
                logSuccess("Invoke Death effect confirmed.");
            }
        }
    }

    public static int VolleyOfSoulsThreshold = 5;

    public static void volleyOfSouls() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        AtomicInteger currentResidualSouls = new AtomicInteger(VarManager.getVarValue(VarDomainType.PLAYER, 11035));

        if (currentResidualSouls.get() >= VolleyOfSoulsThreshold && player.hasTarget() && player.inCombat()) {
            currentResidualSouls.set(VarManager.getVarValue(VarDomainType.PLAYER, 11035));
            interactWithAbility("Volley of Souls");
            int finalCurrentResidualSouls = currentResidualSouls.get();
            boolean effectConfirmed = Execution.delayUntil(random.nextLong(4000, 5000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11035) < finalCurrentResidualSouls);
            if (effectConfirmed) {
                logSuccess("Volley of Souls effect confirmed with " + currentResidualSouls.get() + " residual souls.");
            }
        }
    }


    public static void manageThreadsOfFate() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.getCooldown("Threads of Fate") == 0 && player.hasTarget() && player.inCombat() && player.getFollowing() != null) {
            interactWithAbility("Threads of Fate");
            boolean effectConfirmed = Execution.delayUntil(random.nextLong(2000, 3000), () -> ActionBar.getCooldownPrecise("Threads of Fate") != 0);
            if (effectConfirmed) {
                log("[Success] Threads of Fate effect confirmed.");
            }
        }
    }

    public static void keepArmyUp() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (VarManager.getVarValue(VarDomainType.PLAYER, 11018) == 0) {
            interactWithAbility("Conjure Undead Army");
            boolean effectConfirmed = Execution.delayUntil(random.nextLong(2000, 3000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11018) != 0);
            if (effectConfirmed) {
                log("[Success] Conjure Undead Army effect confirmed.");
            }
        }
    }

    public static int NecrosisStacksThreshold = 12;

    public static void essenceOfFinality() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        AtomicInteger currentNecrosisStacks = new AtomicInteger(VarManager.getVarValue(VarDomainType.PLAYER, 10986));

        if (player.getAdrenaline() > 250
                && ComponentQuery.newQuery(291).spriteId(55524).results().isEmpty()
                && player.inCombat()
                && player.hasTarget()
                && currentNecrosisStacks.get() >= NecrosisStacksThreshold) {
            interactWithAbility("Essence of Finality");
            int finalCurrentNecrosisStacks = currentNecrosisStacks.get();
            boolean effectConfirmed = Execution.delayUntil(random.nextLong(2000, 3000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 10986) != finalCurrentNecrosisStacks);
            if (effectConfirmed) {
                logSuccess("Essence of Finality effect confirmed with " + currentNecrosisStacks.get() + " Necrosis stacks.");
            }
        }
    }

    public static void DeathEssence() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (player.getAdrenaline() > 300
                && player.getFollowing() != null
                && player.getFollowing().getCurrentHealth() > 500
                && ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty()
                && player.hasTarget()
                && player.inCombat()) {
            interactWithAbility("Weapon Special Attack");
            boolean abilityEffect = Execution.delayUntil(random.nextLong(2000, 3000), () -> !ComponentQuery.newQuery(291).spriteId(55480).results().isEmpty());
            if (abilityEffect) {
                log("[Success] Weapon Special Attack effect confirmed.");
            }
        }
    }


    public static void manageAnimateDead() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ComponentQuery.newQuery(284).spriteId(14764).results().isEmpty()) {
            interactWithAbility("Animate Dead");
            boolean abilityEffect = Execution.delayUntil(random.nextLong(2000, 3000), () -> !ComponentQuery.newQuery(284).spriteId(14764).results().isEmpty());
            if (abilityEffect) {
                log("[Success] Animate Dead effect confirmed.");
            }
        }
    }

    public static void manageDarkness() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (VarManager.getVarValue(VarDomainType.PLAYER, 11074) == 0) {
            interactWithAbility("Darkness");
            boolean effectConfirmed = Execution.delayUntil(random.nextLong(2000, 3000), () -> VarManager.getVarValue(VarDomainType.PLAYER, 11074) != 0);
            if (effectConfirmed) {
                log("[Success] Darkness effect confirmed.");
            }
        }
    }

    public static void activateUndeadSlayer() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.containsAbility("Undead Slayer") && ActionBar.getCooldownPrecise("Undead Slayer") == 0 && player.inCombat() && player.hasTarget()) {
            interactWithAbility("Undead Slayer");
            log("[Success] Activated Undead Slayer.");
            Execution.delay(random.nextLong(1900, 2000));
        }
    }

    public static void activateDragonSlayer() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.containsAbility("Dragon Slayer") && ActionBar.getCooldownPrecise("Dragon Slayer") == 0 && player.inCombat() && player.hasTarget()) {
            interactWithAbility("Dragon Slayer");
            log("[Success] Activated Dragon Slayer.");
            Execution.delay(random.nextLong(1900, 2000));
        }
    }

    public static void activateDemonSlayer() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.containsAbility("Demon Slayer") && ActionBar.getCooldownPrecise("Demon Slayer") == 0 && player.inCombat() && player.hasTarget()) {
            interactWithAbility("Demon Slayer");
            log("[Success] Activated Demon Slayer.");
            Execution.delay(random.nextLong(1900, 2000));
        }
    }

    public static void vulnerabilityBomb() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        int vulnDebuffVarbit = VarManager.getVarbitValue(1939);

        if (vulnDebuffVarbit == 0 && ActionBar.containsItem("Vulnerability bomb") && player.hasTarget() && player.inCombat()) {
            boolean success = ActionBar.useItem("Vulnerability bomb", "Throw");
            if (success) {
                log("[Success] Throwing Vulnerability bomb at " + player.getTarget().getName());
                Execution.delay(random.nextLong(1900, 2000));
            } else {
                log("[Caution] Failed to use Vulnerability bomb!");
            }
        }
    }

    public static void activateElvenRitual() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (Backpack.contains("Ancient elven ritual shard")) {
            if (ComponentQuery.newQuery(291).spriteId(43358).results().isEmpty()) {
                boolean success = Backpack.interact("Ancient elven ritual shard", "Activate");
                if (success) {
                    log("[Success] Activated Elven Ritual Shard.");
                    Execution.delay(random.nextLong(1900, 2000));
                } else {
                    log("[Caution] Failed to activate Elven Ritual Shard.");
                }
            }
        }
    }


    public static void activateExcalibur() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ComponentQuery.newQuery(291).spriteId(14632).results().isEmpty()) {
            if (player.getCurrentHealth() * 100 / player.getMaximumHealth() <= healthPointsThreshold) {
                ResultSet<Item> items = InventoryItemQuery.newQuery().results();
                Item excaliburItem = items.stream()
                        .filter(item -> item.getName() != null && item.getName().toLowerCase().contains("excalibur"))
                        .findFirst()
                        .orElse(null);

                if (excaliburItem == null) {
                    log("[Error] No Excalibur found!");
                } else {
                    boolean success = Backpack.interact(excaliburItem.getName(), "Activate");
                    if (success) {
                        log("Activating " + excaliburItem.getName());
                        Execution.delay(random.nextLong(1900, 2000));
                    } else {
                        log("[Caution] Failed to activate Excalibur.");
                    }
                }
            }
        }
    }

    private static long lastAbilityUseTime = 0;
    public static boolean useDefensives = false;


    public static void useDefensives() {
        LocalPlayer player = getLocalPlayer();
        long currentTime = System.currentTimeMillis();
        if (useDefensives && currentTime - lastAbilityUseTime >= random.nextLong(7500, 10000)) {
            if (ActionBar.containsAbility("Barricade") && ActionBar.getCooldownPrecise("Barricade") == 0 && player.inCombat() && player.hasTarget() && player.getAdrenaline() >= 1000) {
                useBarricade();
                lastAbilityUseTime = currentTime;
                return;
            }
            if (ActionBar.containsAbility("Devotion") && ActionBar.getCooldownPrecise("Devotion") == 0 && player.inCombat() && player.hasTarget() && player.getAdrenaline() > 500) {
                useDevotion();
                lastAbilityUseTime = currentTime;
                return;
            }
            if (ActionBar.containsAbility("Resonance") && ActionBar.getCooldownPrecise("Resonance") == 0 && player.inCombat() && player.hasTarget()) {
                useResonance();
                lastAbilityUseTime = currentTime;
                return;
            }
            if (ActionBar.containsAbility("Anticipation") && ActionBar.getCooldownPrecise("Anticipation") == 0 && player.inCombat() && player.hasTarget()) {
                useAnticipation();
                lastAbilityUseTime = currentTime;
                return;
            }
            if (ActionBar.containsAbility("Reflect") && ActionBar.getCooldownPrecise("Reflect") == 0 && player.inCombat() && player.hasTarget() && player.getAdrenaline() > 500) {
                useReflect();
                lastAbilityUseTime = currentTime;
                return;
            }
            if (ActionBar.containsAbility("Debilitate") && ActionBar.getCooldownPrecise("Debilitate") == 0 && player.inCombat() && player.hasTarget()) {
                useDebilitate();
                lastAbilityUseTime = currentTime;
            }
        }
    }

    public static void useReflect() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.containsAbility("Reflect") && ActionBar.getCooldownPrecise("Reflect") == 0 && player.inCombat() && player.hasTarget() && player.getAdrenaline() > 500) {
            ActionBar.useAbility("Reflect");
            log("[Success] Activated Reflect.");
            Execution.delay(random.nextLong(1900, 2000));
        }
    }
    public static void useResonance() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.containsAbility("Resonance") && ActionBar.getCooldownPrecise("Resonance") == 0 && player.inCombat() && player.hasTarget()) {
            ActionBar.useAbility("Resonance");
            log("[Success] Activated Resonance.");
            Execution.delay(random.nextLong(1900, 2000));
        }
    }

    public static void useDebilitate() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.containsAbility("Debilitate") && ActionBar.getCooldownPrecise("Debilitate") == 0 && player.inCombat() && player.hasTarget() && player.getAdrenaline() > 500) {
            ActionBar.useAbility("Debilitate");
            log("[Success] Activated Debilitate.");
            Execution.delay(random.nextLong(1900, 2000));
        }
    }

    public static void useDevotion() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.containsAbility("Devotion") && ActionBar.getCooldownPrecise("Devotion") == 0 && player.inCombat() && player.hasTarget() && player.getAdrenaline() > 500) {
            ActionBar.useAbility("Devotion");
            log("[Success] Activated Devotion.");
            Execution.delay(random.nextLong(1900, 2000));
        }
    }

    public static void useBarricade() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.containsAbility("Barricade") && ActionBar.getCooldownPrecise("Barricade") == 0 && player.inCombat() && player.hasTarget() && player.getAdrenaline() >= 1000) {
            ActionBar.useAbility("Barricade");
            log("[Success] Activated Barricade.");
            Execution.delay(random.nextLong(1900, 2000));
        }
    }

    public static void useAnticipation() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }
        if (ActionBar.containsAbility("Anticipation") && ActionBar.getCooldownPrecise("Anticipation") == 0 && player.inCombat() && player.hasTarget()) {
            ActionBar.useAbility("Anticipation");
            log("[Success] Activated Anticipation.");
            Execution.delay(random.nextLong(1900, 2000));
        }
    }
}
