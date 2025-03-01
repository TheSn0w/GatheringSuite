package net.Snow;

import net.Snow.AbyssRunecrafting.AbyssRunecrafting;
import net.Snow.Archaeology.Archaeology;
import net.Snow.Combat.Combat;
import net.Snow.Combat.Graphics.NPCScanGraphics;
import net.Snow.Combat.Variables;
import net.Snow.Crafting.Crafting;
import net.Snow.Dissassemble.Disassemble;
import net.Snow.Divination.Divination;
import net.Snow.Firemaking.Firemaking;
import net.Snow.Fishing.Fishing;
import net.Snow.Fletching.Fletching;
import net.Snow.Herblore.Herblore;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Mining.Mining;
import net.Snow.Notepaper.Notepaper;
import net.Snow.Prayer.Prayer;
import net.Snow.Proteans.ProteanProtein;
import net.Snow.Rituals.Necromancy;
import net.Snow.Runecrafting.Runecrafting;
import net.Snow.SlimeCollector.SlimeCollector;
import net.Snow.Smelting.Smelting;
import net.Snow.Smithing.Smithing;
import net.Snow.Summoning.Summoning;
import net.Snow.Thieving.Thieving;
import net.Snow.Woodcutting.Woodcutting;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.EventBus;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.quest.Quest;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static net.Snow.Archaeology.Collections.CollectionTypes.selectedCollectionIndex;
import static net.Snow.Archaeology.DropSoil.dropSoil;
import static net.Snow.Archaeology.MaterialTypes.selectedExcavationIndex;
import static net.Snow.Banking.BankLocations.selectedBankIndex;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.Combat.Abilities.*;
import static net.Snow.Combat.Graphics.LootingGraphics.getTargetItemNames;
import static net.Snow.Combat.Health.Potions.*;
import static net.Snow.Combat.Health.Scrimshaws.*;
import static net.Snow.Combat.Looting.LootManager.*;
import static net.Snow.Combat.NPCLocation.NPCType.selectedNpcIndex;
import static net.Snow.Divination.DivinationGraphics.*;
import static net.Snow.Fishing.FishingTypes.selectedFishIndex;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.SnowsTemplateGraphics.selectedActivityIndex;
import static net.Snow.ImGui.SnowsTemplateGraphics.showLogs;
import static net.Snow.LevelLogout.CheckLevel.checkThreshold;
import static net.Snow.Mining.MiningVariables.*;
import static net.Snow.Mining.RockTypes.selectedRockIndex;
import static net.Snow.Runecrafting.RunecraftingGraphics.*;
import static net.Snow.Runecrafting.Variables.worldHop;
import static net.Snow.Spells.Lightform.useLightForm;
import static net.Snow.Woodcutting.BirdsNests.pickupNests;
import static net.Snow.Woodcutting.Crystallise.useCrystallise;
import static net.Snow.Woodcutting.Firemaking.doFiremaking;
import static net.Snow.Woodcutting.Fletching.*;
import static net.Snow.Woodcutting.IncenseSticks.doIncenseSticks;
import static net.Snow.Woodcutting.TreeTypes.selectedTreeIndex;
import static net.botwithus.api.game.hud.Hud.logout;


public class Snow extends LoopingScript {

    // this script type is called polymorphism
    // abstract classes
    // method overiding.//

    public static final Random random = new Random();

    public AbstractScript AbstractScript;

    public AbstractScript[] scripts = new AbstractScript[] {
            new Mining(), // 0
            new Woodcutting(), // 1
            new Archaeology(), // 2
            new Fishing(), // 3
            new Prayer(), // 4
            new SlimeCollector(), // 5
            new Necromancy(), // 6
            new Thieving(), // 7
            new Herblore(), // 8
            new Firemaking(), // 9
            new Summoning(), // 10
            new Fletching(), // 11
            new Disassemble(), // 12
            new Smelting(), // 13
            new Smithing(), // 14
            new ProteanProtein(), //
            new Divination(), //
            new Runecrafting(), //
            new AbyssRunecrafting(), //
            new Crafting(), //
//            new UnlockFishing(), //
            new Combat()

            // Add new scripts here
    };

    public void changeActivity(int newIndex) {
        if (newIndex >= 0 && newIndex < scripts.length) {
            selectedActivityIndex = newIndex;
            this.AbstractScript = scripts[selectedActivityIndex];
            this.AbstractScript.initialize();
            this.loopDelay = this.AbstractScript.getLoopDelay();
            logOrange("Activity changed to: " + this.AbstractScript.getClass().getSimpleName());
        } else {
            logError("Invalid activity index: " + newIndex);
        }
    }

    public Snow(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new SnowsTemplateGraphics(getConsole(), this);
        isBackgroundScript = false;
    }


    // SnowsTemplate.java
    @Override
    public boolean initialize() {
        boolean start = super.initialize(); // always call super.initialize() first
        EventBus.EVENT_BUS.subscribe(this, InventoryUpdateEvent.class, this::onInventoryUpdate);
        EventBus.EVENT_BUS.subscribe(this, ChatMessageEvent.class, this::onChatMessageEvent);
        loadConfiguration();
        this.AbstractScript = scripts[selectedActivityIndex];
        this.AbstractScript.initialize();
        this.loopDelay = this.AbstractScript.getLoopDelay(); // Set loop delay based on the current script
        ScriptManager.setCurrentScript(this); // Set the current script
        return start; // return the result of the super.initialize() call
    }



    public void onInventoryUpdate(InventoryUpdateEvent event) {
        if (!isActive()) {
            return;
        }
        if (AbstractScript != null) {
            AbstractScript.onInventoryUpdate(event);
        }

    }

    public void onChatMessageEvent(ChatMessageEvent event) {
        if (!isActive()) {
            return;
        }
        if (AbstractScript != null) {
            AbstractScript.onChatMessageEvent(event);
        }
    }

    @Override
    public void onLoop() {
        AbstractScript.run(this);
    }

    public static void capturestuff() {
        EntityResultSet<Npc> npcResults = NpcQuery.newQuery()
                .name("Seren spirit", "Divine blessing", "Catalyst of alteration", "Fire spirit", "Forge phoenix")
                .results();

        if (npcResults.isEmpty()) {
            return;
        }

        Npc npc = npcResults.nearest();
        if (npc != null) {
            String npcName = npc.getName();
            String interactionOption;

            switch (npcName) {
                case "Seren spirit":
                case "Divine blessing":
                case "Catalyst of alteration":
                    interactionOption = "Capture";
                    break;
                case "Fire spirit":
                case "Forge phoenix":
                    interactionOption = "Collect reward";
                    break;
                default:
                    logError("Unknown NPC found: " + npcName);
                    return;
            }
            npc.interact(interactionOption);
            logMagenta("Interacting with " + npcName + " using " + interactionOption + ".");
            Execution.delayUntil(10000, () -> !npc.validate());
        } else {
            logError("Failed to find nearest NPC.");
        }
    }

    public static boolean isAnimating() {
        int initialAnimationId = LocalPlayer.LOCAL_PLAYER.getAnimationId();

        if (initialAnimationId == -1) {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 2500) {
                int currentAnimationId = LocalPlayer.LOCAL_PLAYER.getAnimationId();
                if (currentAnimationId != -1) {
                    return true;
                }
                Execution.delay(300); // Small delay to avoid busy-waiting
            }
            return false;
        } else {
            return true;
        }
    }


    public static void shutdown() {
        logError("Shutting down...");
        selectedActivityIndex = 0;
        LoginManager.setAutoLogin(false);
        Execution.delay(random.nextLong(1000, 2000));
        logout();
        Execution.delay(random.nextLong(4000, 5000));
    }

    public static boolean component(int option1, int option2, int option3) {
        MiniMenu.interact(ComponentAction.COMPONENT.getType(), option1, option2, option3);
        return false;
    }

    public static void dialog(int option1, int option2, int option3) {
        MiniMenu.interact(ComponentAction.DIALOGUE.getType(), option1, option2, option3);
    }

    public static void selectInterface1370() {
        Component interfaceComponent = ComponentQuery.newQuery(1370).results().first();
        if (interfaceComponent == null) {
            logError("Interface has not appeared");
            return;
        }

        int resourcesCanMake = VarManager.getVarValue(VarDomainType.PLAYER, 8847);
        if (resourcesCanMake < 1) {
            logError("Cannot make anything.");
            Execution.delay(random.nextLong(500, 750));
            shutdown();
        }

        logGrey("We can make: " + resourcesCanMake + " items.");

        boolean interact = MiniMenu.interact(16, 0, -1, 89784350);
        if (interact) {
            logMagenta("Successfully interacted with button.");
            Execution.delayUntil(random.nextLong(1200, 2000), () -> Interfaces.isOpen(1251));
        } else {
            logError("Failed to interact with button.");
            Execution.delay(random.nextLong(500, 750));
        }
    }

    public static boolean isQuestComplete(int questId) {
        Optional<Quest> quest = Quest.byId(questId);
        return quest.isPresent() && quest.get().isComplete();
    }

    private String getQuestName(int questId) {
        Optional<Quest> quest = Quest.byId(questId);
        return quest.map(Quest::name).orElse("");
    }

    //String questName = getQuestName(390);
    //logMisc("Player has completed the quest: " + questName + " and has enough reputation to enter VIP area.");


    public void saveConfiguration() throws IOException {
        if (AbstractScript != null) {
            AbstractScript.saveConfiguration();
        }
        // Existing configuration properties
        this.configuration.addProperty("SelectedActivityIndex", String.valueOf(selectedActivityIndex));
        this.configuration.addProperty("selectedTreeIndex", String.valueOf(selectedTreeIndex));
        this.configuration.addProperty("selectedBankIndex", String.valueOf(selectedBankIndex));
        this.configuration.addProperty("selectedRockIndex", String.valueOf(selectedRockIndex));
        this.configuration.addProperty("selectedExcavationIndex", String.valueOf(selectedExcavationIndex));
        this.configuration.addProperty("showLogs", String.valueOf(showLogs));
        this.configuration.addProperty("usePorters", String.valueOf(usePorters));
        this.configuration.addProperty("doAutomaticLevelling", String.valueOf(doAutomaticLevelling));
        this.configuration.addProperty("useRockertunities", String.valueOf(useRockertunities));
        this.configuration.addProperty("useJujuPotion", String.valueOf(useJujuPotion));
        this.configuration.addProperty("useLightForm", String.valueOf(useLightForm));
        this.configuration.addProperty("useCrystallise", String.valueOf(useCrystallise));
        this.configuration.addProperty("pickupNests", String.valueOf(pickupNests));
        this.configuration.addProperty("doFiremaking", String.valueOf(doFiremaking));
        this.configuration.addProperty("doIncenseSticks", String.valueOf(doIncenseSticks));
        this.configuration.addProperty("doFletching", String.valueOf(doFletching));
        this.configuration.addProperty("arrowShafts", String.valueOf(arrowShafts));
        this.configuration.addProperty("shortBow", String.valueOf(shortBow));
        this.configuration.addProperty("woodenStock", String.valueOf(woodenStock));
        this.configuration.addProperty("shieldBow", String.valueOf(shieldBow));
        this.configuration.addProperty("selectedCollectionIndex", String.valueOf(selectedCollectionIndex));
        this.configuration.addProperty("selectedFishIndex", String.valueOf(selectedFishIndex));
        this.configuration.addProperty("selectedNpcIndex", String.valueOf(selectedNpcIndex));
        this.configuration.addProperty("onlyFarmChronicles", String.valueOf(onlyFarmChronicles));
        this.configuration.addProperty("harvestChronicles", String.valueOf(harvestChronicles));
        this.configuration.addProperty("depositChronicles", String.valueOf(depositChronicles));
        this.configuration.addProperty("dropSoil", String.valueOf(dropSoil));
        this.configuration.addProperty("useInvokeDeath", String.valueOf(useInvokeDeath));
        this.configuration.addProperty("useVolleyofSouls", String.valueOf(useVolleyofSouls));
        this.configuration.addProperty("useEssenceofFinality", String.valueOf(useEssenceofFinality));
        this.configuration.addProperty("useWeaponSpecialAttack", String.valueOf(useWeaponSpecialAttack));
        this.configuration.addProperty("useDefensives", String.valueOf(useDefensives));
        this.configuration.addProperty("useCustomLoot", String.valueOf(useCustomLoot));
        this.configuration.addProperty("useLootAllStackableItems", String.valueOf(useLootAllStackableItems));
        this.configuration.addProperty("useLootAllNotedItems", String.valueOf(useLootAllNotedItems));
        this.configuration.addProperty("useLootEverything", String.valueOf(useLootEverything));
        this.configuration.addProperty("targetItemNames", String.join(",", getTargetItemNames()));
        this.configuration.addProperty("selectedNpcNames", String.join(",", NPCScanGraphics.selectedNpcNames));
        this.configuration.addProperty("boneAltar", String.valueOf(boneAltar));
        this.configuration.addProperty("miasmaAltar", String.valueOf(miasmaAltar));
        this.configuration.addProperty("spiritAltar", String.valueOf(spiritAltar));
        this.configuration.addProperty("fleshAltar", String.valueOf(fleshAltar));
        this.configuration.addProperty("worldHop", String.valueOf(worldHop));
        this.configuration.addProperty("useWeaponPoison", String.valueOf(useWeaponPoison));
        this.configuration.addProperty("useOverloads", String.valueOf(useOverloads));
        this.configuration.addProperty("usePrayerPotions", String.valueOf(usePrayerPotions));
        this.configuration.addProperty("useAgressionPotions", String.valueOf(useAgressionPotions));
        this.configuration.addProperty("selectedNotepaperNames", String.join(",", Notepaper.getSelectedNotepaperNames()));
        this.configuration.addProperty("useNotepaper", String.valueOf(useNotepaper));
        this.configuration.addProperty("scriptureofJas", String.valueOf(scriptureofJas));
        this.configuration.addProperty("scriptureofWen", String.valueOf(scriptureofWen));
        this.configuration.addProperty("useScrimshaws", String.valueOf(useScrimshaws));
        this.configuration.addProperty("checkThreshold", String.valueOf(checkThreshold));
        this.configuration.addProperty("healthPointsThreshold", String.valueOf(Variables.getHealthPointsThreshold()));
        this.configuration.addProperty("prayerPointsThreshold", String.valueOf(Variables.getPrayerPointsThreshold()));

        this.configuration.save();
    }


    public void loadConfiguration() {
        if (AbstractScript != null) {
            AbstractScript.loadConfiguration();
        }
        // Load existing configuration properties
        dropSoil = Boolean.parseBoolean(this.configuration.getProperty("dropSoil"));
        onlyFarmChronicles = Boolean.parseBoolean(this.configuration.getProperty("onlyFarmChronicles"));
        harvestChronicles = Boolean.parseBoolean(this.configuration.getProperty("harvestChronicles"));
        depositChronicles = Boolean.parseBoolean(this.configuration.getProperty("depositChronicles"));
        selectedNpcIndex = Integer.parseInt(this.configuration.getProperty("selectedNpcIndex"));
        selectedFishIndex = Integer.parseInt(this.configuration.getProperty("selectedFishIndex"));
        selectedCollectionIndex = Integer.parseInt(this.configuration.getProperty("selectedCollectionIndex"));
        arrowShafts = Boolean.parseBoolean(this.configuration.getProperty("arrowShafts"));
        shortBow = Boolean.parseBoolean(this.configuration.getProperty("shortBow"));
        woodenStock = Boolean.parseBoolean(this.configuration.getProperty("woodenStock"));
        shieldBow = Boolean.parseBoolean(this.configuration.getProperty("shieldBow"));
        doFletching = Boolean.parseBoolean(this.configuration.getProperty("doFletching"));
        doIncenseSticks = Boolean.parseBoolean(this.configuration.getProperty("doIncenseSticks"));
        doFiremaking = Boolean.parseBoolean(this.configuration.getProperty("doFiremaking"));
        pickupNests = Boolean.parseBoolean(this.configuration.getProperty("pickupNests"));
        useCrystallise = Boolean.parseBoolean(this.configuration.getProperty("useCrystallise"));
        useLightForm = Boolean.parseBoolean(this.configuration.getProperty("useLightForm"));
        useJujuPotion = Boolean.parseBoolean(this.configuration.getProperty("useJujuPotion"));
        selectedActivityIndex = Integer.parseInt(this.configuration.getProperty("SelectedActivityIndex"));
        selectedTreeIndex = Integer.parseInt(this.configuration.getProperty("selectedTreeIndex"));
        selectedBankIndex = Integer.parseInt(this.configuration.getProperty("selectedBankIndex"));
        selectedRockIndex = Integer.parseInt(this.configuration.getProperty("selectedRockIndex"));
        selectedExcavationIndex = Integer.parseInt(this.configuration.getProperty("selectedExcavationIndex"));
        showLogs = Boolean.parseBoolean(this.configuration.getProperty("showLogs"));
        usePorters = Boolean.parseBoolean(this.configuration.getProperty("usePorters"));
        doAutomaticLevelling = Boolean.parseBoolean(this.configuration.getProperty("doAutomaticLevelling"));
        useRockertunities = Boolean.parseBoolean(this.configuration.getProperty("useRockertunities"));
        useInvokeDeath = Boolean.parseBoolean(this.configuration.getProperty("useInvokeDeath"));
        useVolleyofSouls = Boolean.parseBoolean(this.configuration.getProperty("useVolleyofSouls"));
        useEssenceofFinality = Boolean.parseBoolean(this.configuration.getProperty("useEssenceofFinality"));
        useWeaponSpecialAttack = Boolean.parseBoolean(this.configuration.getProperty("useWeaponSpecialAttack"));
        useDefensives = Boolean.parseBoolean(this.configuration.getProperty("useDefensives"));
        useCustomLoot = Boolean.parseBoolean(this.configuration.getProperty("useCustomLoot"));
        useLootAllStackableItems = Boolean.parseBoolean(this.configuration.getProperty("useLootAllStackableItems"));
        useLootAllNotedItems = Boolean.parseBoolean(this.configuration.getProperty("useLootAllNotedItems"));
        useLootEverything = Boolean.parseBoolean(this.configuration.getProperty("useLootEverything"));
        boneAltar = Boolean.parseBoolean(this.configuration.getProperty("boneAltar"));
        miasmaAltar = Boolean.parseBoolean(this.configuration.getProperty("miasmaAltar"));
        spiritAltar = Boolean.parseBoolean(this.configuration.getProperty("spiritAltar"));
        fleshAltar = Boolean.parseBoolean(this.configuration.getProperty("fleshAltar"));
        worldHop = Boolean.parseBoolean(this.configuration.getProperty("worldHop"));
        useWeaponPoison = Boolean.parseBoolean(this.configuration.getProperty("useWeaponPoison"));
        useOverloads = Boolean.parseBoolean(this.configuration.getProperty("useOverloads"));
        usePrayerPotions = Boolean.parseBoolean(this.configuration.getProperty("usePrayerPotions"));
        useAgressionPotions = Boolean.parseBoolean(this.configuration.getProperty("useAgressionPotions"));
        useNotepaper = Boolean.parseBoolean(this.configuration.getProperty("useNotepaper"));
        scriptureofJas = Boolean.parseBoolean(this.configuration.getProperty("scriptureofJas"));
        scriptureofWen = Boolean.parseBoolean(this.configuration.getProperty("scriptureofWen"));
        useScrimshaws = Boolean.parseBoolean(this.configuration.getProperty("useScrimshaws"));
        checkThreshold = Boolean.parseBoolean(this.configuration.getProperty("checkThreshold"));


        // Load Health and Prayer thresholds
        Variables.setHealthThreshold(Integer.parseInt(this.configuration.getProperty("healthPointsThreshold") != null ? this.configuration.getProperty("healthPointsThreshold") : "1"));
        Variables.setPrayerPointsThreshold(Integer.parseInt(this.configuration.getProperty("prayerPointsThreshold") != null ? this.configuration.getProperty("prayerPointsThreshold") : "100"));

        // Load selected NPC names
        String npcNames = this.configuration.getProperty("selectedNpcNames");
        if (npcNames != null && !npcNames.isEmpty()) {
            NPCScanGraphics.selectedNpcNames = new ArrayList<>(List.of(npcNames.split(",")));
        } else {
            NPCScanGraphics.selectedNpcNames = new ArrayList<>();
        }

        // Load target item names
        String itemNames = this.configuration.getProperty("targetItemNames");
        if (itemNames != null && !itemNames.isEmpty()) {
            targetItemNames = new ArrayList<>(List.of(itemNames.split(",")));
        } else {
            targetItemNames = new ArrayList<>();
        }

        // Load selected notepaper names
        String notepaperNames = this.configuration.getProperty("selectedNotepaperNames");
        if (notepaperNames != null && !notepaperNames.isEmpty()) {
            Notepaper.selectedNotepaperNames = new ArrayList<>(List.of(notepaperNames.split(",")));
        } else {
            Notepaper.selectedNotepaperNames = new ArrayList<>();
        }

    }
}