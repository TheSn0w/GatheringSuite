package net.Snow.Archaeology;

import net.Snow.Archaeology.Collections.Artifact;
import net.Snow.Archaeology.Collections.ArtifactRequirements;
import net.Snow.Navigation;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.WalkAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.util.*;

import static net.Snow.Archaeology.Archaeology.ArchaeologyState.EXCAVATING;
import static net.Snow.Archaeology.Archaeology.setBotState;
import static net.Snow.Archaeology.Collections.CheckArtifacts.artifactCounts;
import static net.Snow.Archaeology.Collections.CheckArtifacts.checkArtifacts;
import static net.Snow.Banking.BankLocations.lastPlayerCoordinate;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Snow.*;

public class MaterialTypes {

    public static int selectedExcavationIndex = 0;
    public static List<Navigation> ExcavationTypes = new ArrayList<>();

    static {
        ExcavationTypes.add(new Navigation("Disabled", null, null, 0)); // 0
        ExcavationTypes.add(new Navigation("Venator remains", new Coordinate(3371, 3192, 0), "Kharid Et", 1)); // 1
        ExcavationTypes.add(new Navigation("Lodge art storage", new Coordinate(3263, 3504, 0), "Infernal Source", 24, List.of(
                MaterialTypes::performLodgeArtStorageSteps
        ))); // 2
        ExcavationTypes.add(new Navigation("Lodge bar storage", new Coordinate(3263, 3504, 0), "Infernal Source", 20, List.of(
                MaterialTypes::performLodgeBarStorageSteps
        ))); // 3
        ExcavationTypes.add(new Navigation("Sacrificial altar", new Coordinate(3263, 3504, 0), "Infernal Source", 36, List.of(
                MaterialTypes::performSacrificialAltarSteps
        ))); // 4
        ExcavationTypes.add(new Navigation("Dis dungeon debris", new Coordinate(3263, 3504, 0), "Infernal Source", 45, List.of(
                MaterialTypes::performDisDungeonDebrisSteps
        ))); // 5
        ExcavationTypes.add(new Navigation("Cultist footlocker", new Coordinate(3263, 3504, 0), "Infernal Source", 29, List.of(
                MaterialTypes::performCultistFootlockerSteps
        ))); // 6
        ExcavationTypes.add(new Navigation("Legionary remains", new Coordinate(3374, 3182, 0), "Kharid Et", 12, List.of(
                MaterialTypes::performLegionaryRemainsSteps
        ))); // 7
        ExcavationTypes.add(new Navigation("Castra debris", new Coordinate(3374, 3182, 0), "Kharid Et", 17, List.of(
                MaterialTypes::performCastraDebrisSteps
        ))); // 8
        ExcavationTypes.add(new Navigation("Administratum debris", new Coordinate(3374, 3182, 0), "Kharid Et", 25, List.of(
                MaterialTypes::performAdministratumDebrisSteps
        ))); // 9
        ExcavationTypes.add(new Navigation("Prodromoi remains", new Coordinate(3735, 3225, 0), "Everlight", 42)); // 10
        ExcavationTypes.add(new Navigation("Monoceros remains", new Coordinate(3748, 3206, 0), "Everlight", 48)); // 11
        ExcavationTypes.add(new Navigation("Amphitheatre debris", new Coordinate(3750, 3292, 0), "Everlight", 51)); // 12
        ExcavationTypes.add(new Navigation("Ceramics studio debris", new Coordinate(3741, 3277, 0), "Everlight", 56)); // 13
        ExcavationTypes.add(new Navigation("Stadio debris", new Coordinate(3817, 3181, 0), "Everlight", 61)); // 14
        ExcavationTypes.add(new Navigation("Dominion Games podium", new Coordinate(3794, 3227, 0), "Everlight", 69)); // 15
        ExcavationTypes.add(new Navigation("Oikos studio debris", new Coordinate(3738, 3176, 0), "Everlight", 72)); // 16
        ExcavationTypes.add(new Navigation("Oikos fishing hut remnants", new Coordinate(3729, 3170, 0), "Everlight", 84)); // 17
        ExcavationTypes.add(new Navigation("Acropolis debris", new Coordinate(3752, 3149, 0), "Everlight", 92)); // 18
        ExcavationTypes.add(new Navigation("Icyene weapon rack", new Coordinate(3801, 3216, 0), "Everlight", 100)); // 19
        ExcavationTypes.add(new Navigation("Bibliotheke debris", new Coordinate(3772, 3149, 0), "Everlight", 109)); // 20


        ExcavationTypes.add(new Navigation("Praesidio remains", new Coordinate(3374, 3182, 0), "Kharid Et", 47, List.of(
                MaterialTypes::performPraesidioDebrisSteps
        )));

        ExcavationTypes.add(new Navigation("Carcerem debris", new Coordinate(3374, 3182, 0), "Kharid Et", 58, List.of(
                MaterialTypes::performCarceremDebrisSteps
        )));

        ExcavationTypes.add(new Navigation("Kharid-et chapel debris", new Coordinate(3374, 3182, 0), "Kharid Et", 74, List.of(
                MaterialTypes::performChapelDebrisSteps
        )));

        ExcavationTypes.add(new Navigation("Pontifex remains", new Coordinate(3374, 3182, 0), "Kharid Et", 81, List.of(
                MaterialTypes::performPontifixRemainsSteps
        ))); // 21

        ExcavationTypes.add(new Navigation("Orcus altar", new Coordinate(3374, 3182, 0), "Kharid Et", 86, List.of(
                MaterialTypes::performOrcusAltarRemainsSteps
        ))); // 22

        ExcavationTypes.add(new Navigation("Armarium debris", new Coordinate(3374, 3182, 0), "Kharid Et", 93, List.of(
                MaterialTypes::performArmariumDebrisSteps
        ))); // 23

        ExcavationTypes.add(new Navigation("Culinarum debris", new Coordinate(3374, 3182, 0), "Kharid Et", 100, List.of(
                MaterialTypes::performCulinarumDebrisSteps
        ))); // 24

        ExcavationTypes.add(new Navigation("Castle hall rubble", new Coordinate(3435, 3733, 0), "Daemonheim", 73)); // 25
        ExcavationTypes.add(new Navigation("Tunnelling equipment repository", new Coordinate(3451, 3742, 0), "Daemonheim", 77)); // 26
        ExcavationTypes.add(new Navigation("Botanical reserve", new Coordinate(3440, 3743, 1), "Daemonheim", 78)); // 27
        ExcavationTypes.add(new Navigation("Communal space", new Coordinate(3463, 3732, 1), "Daemonheim", 87)); // 28
        ExcavationTypes.add(new Navigation("Projection space", new Coordinate(3461, 3749, 0), "Daemonheim", 103, List.of(
                MaterialTypes::performProjectionSpaceSteps
        ))); // 29
        ExcavationTypes.add(new Navigation("Security booth", new Coordinate(3461, 3749, 0), "Daemonheim", 107, List.of(
                MaterialTypes::performSecurityBoothSteps
        ))); // 30
        ExcavationTypes.add(new Navigation("Traveller's station", new Coordinate(3461, 3749, 0), "Daemonheim", 113, List.of(
                MaterialTypes::performTravellersStationSteps
        ))); // 31

        ExcavationTypes.add(new Navigation("Ikovian memorial", new Coordinate(2677, 3401, 0), "Stormguard", 70)); // 33
        ExcavationTypes.add(new Navigation("Keshik ger", new Coordinate(2680, 3399, 0), "Stormguard", 76, List.of(
                MaterialTypes::performKeshikGer
        ))); // 34
        ExcavationTypes.add(new Navigation("Tailory debris", new Coordinate(2680, 3399, 0), "Stormguard", 81, List.of(
                MaterialTypes::performTailoryDebris
        ))); // 35
        ExcavationTypes.add(new Navigation("Weapons research debris", new Coordinate(2680, 3399, 0), "Stormguard", 85, List.of(
                MaterialTypes::performWeaponsResearchDebris
        ))); // 36
        ExcavationTypes.add(new Navigation("Gravitron research debris", new Coordinate(2680, 3399, 0), "Stormguard", 91, List.of(
                MaterialTypes::performGravitonResearchDebris
        ))); // 37
        ExcavationTypes.add(new Navigation("Keshik tower debris", new Coordinate(2680, 3399, 0), "Stormguard", 95, List.of(
                MaterialTypes::performKeshikTowerDebris
        ))); // 38
        ExcavationTypes.add(new Navigation("Destroyed golem", new Coordinate(2680, 3399, 0), "Stormguard", 98, List.of(
                MaterialTypes::performDestroyedGolem
        ))); // 39
        ExcavationTypes.add(new Navigation("Keshik weapon rack", new Coordinate(2680, 3399, 0), "Stormguard", 103, List.of(
                MaterialTypes::performKeshikWeaponRack
        ))); // 40
        ExcavationTypes.add(new Navigation("Flight research debris", new Coordinate(2680, 3399, 0), "Stormguard", 111, List.of(
                MaterialTypes::performFlightResearchDebris
        ))); // 41
        ExcavationTypes.add(new Navigation("Aetherium forge", new Coordinate(2680, 3399, 0), "Stormguard", 112, List.of(
                MaterialTypes::performAetheriumForge
        ))); // 42
        ExcavationTypes.add(new Navigation("Howl's workshop debris", new Coordinate(2680, 3399, 0), "Stormguard", 118, List.of(
                MaterialTypes::performHowlWorkshopDebris
        ))); // 43
        ExcavationTypes.add(new Navigation("Gladiatorial goblin remains", new Coordinate(2412, 2838, 0), "Warforge", 76, List.of(
                MaterialTypes::performGladiatorialGoblinRemains
        ))); // 44
        ExcavationTypes.add(new Navigation("Crucible stands debris", new Coordinate(2412, 2838, 0), "Warforge", 81, List.of(
                MaterialTypes::performCrucibleStandsDebris
        ))); // 45
        ExcavationTypes.add(new Navigation("Goblin dorm debris", new Coordinate(2412, 2838, 0), "Warforge", 83, List.of(
                MaterialTypes::performGoblinDormDebris
        ))); // 46

        ExcavationTypes.add(new Navigation("Big High War God shrine", new Coordinate(2412, 2838, 0), "Warforge", 89, List.of(
                MaterialTypes::performBigHighWarGodShrine
        ))); // 47

        ExcavationTypes.add(new Navigation("Yu'biusk animal pen", new Coordinate(2412, 2838, 0), "Warforge", 94, List.of(
                MaterialTypes::performYuBiuskAnimalPen
        ))); // 48

        ExcavationTypes.add(new Navigation("Goblin trainee remains", new Coordinate(2412, 2838, 0), "Warforge", 97, List.of(
                MaterialTypes::performGoblinTraineeRemains
        ))); // 49

        ExcavationTypes.add(new Navigation("Kyzaj champion's boudoir", new Coordinate(2412, 2838, 0), "Warforge", 100, List.of(
                MaterialTypes::performKyzajChampionsBoudoir
        ))); // 50

        ExcavationTypes.add(new Navigation("Warforge scrap pile", new Coordinate(2412, 2838, 0), "Warforge", 104, List.of(
                MaterialTypes::performWarforgeScrapPile
        ))); // 51

        ExcavationTypes.add(new Navigation("Warforge weapon rack", new Coordinate(2412, 2838, 0), "Warforge", 110, List.of(
                MaterialTypes::performWarforgeWeaponRack
        ))); // 52

        ExcavationTypes.add(new Navigation("Bandos's sanctum debris", new Coordinate(2412, 2838, 0), "Warforge", 115, List.of(
                MaterialTypes::performBandosSanctumDebris
        ))); // 53

        ExcavationTypes.add(new Navigation("Makeshift pie oven", new Coordinate(2412, 2838, 0), "Warforge", 119, List.of(
                MaterialTypes::performMakeshiftPieOven
        ))); // 54

        ExcavationTypes.add(new Navigation("Ancient magick munitions", new Coordinate(3374, 3182, 0), "Karid Et", 107, List.of(
                MaterialTypes::performAncientMagickMunitions
        ))); // 55

        ExcavationTypes.add(new Navigation("Praetorian remains", new Coordinate(3374, 3182, 0), "Karid Et", 114, List.of(
                MaterialTypes::performPraetorianRemains
        ))); // 56

        ExcavationTypes.add(new Navigation("War table debris", new Coordinate(3374, 3182, 0), "Karid Et", 118, List.of(
                MaterialTypes::performWarTableDebris
        ))); // 57

        ExcavationTypes.add(new Navigation("Varanusaur remains", new Coordinate(5328, 2415, 0), "Orthen", 90, List.of(
                MaterialTypes::performVaranusaurRemains
        ))); // 58

        ExcavationTypes.add(new Navigation("Dragonkin reliquary", new Coordinate(5328, 2415, 0), "Orthen", 96, List.of(
                MaterialTypes::performDragonkinReliquary
        ))); // 59

        ExcavationTypes.add(new Navigation("Dragonkin coffin", new Coordinate(5328, 2415, 0), "Orthen", 99, List.of(
                MaterialTypes::performDragonkinCoffin
        ))); // 60

        ExcavationTypes.add(new Navigation("Autopsy table", new Coordinate(5593, 2532, 0), "Orthen", 101, List.of(
                MaterialTypes::performAutopsyTable
        ))); // 56

        ExcavationTypes.add(new Navigation("Experiment workbench", new Coordinate(5593, 2532, 0), "Orthen", 102, List.of(
                MaterialTypes::performExperimentWorkbench
        ))); // 57

        ExcavationTypes.add(new Navigation("Aughra remains", new Coordinate(5447, 2339, 0), "Orthen", 106, List.of(
                MaterialTypes::performAughraRemains
        ))); // 58

        ExcavationTypes.add(new Navigation("Moksha device", new Coordinate(5447, 2339, 0), "Orthen", 108, List.of(
                MaterialTypes::performMokshaDevice
        ))); // 59

        ExcavationTypes.add(new Navigation("Xolo mine", new Coordinate(5676, 2157, 0), "Orthen", 113, List.of(
                MaterialTypes::performXoloMine
        ))); // 60

        ExcavationTypes.add(new Navigation("Xolo remains", new Coordinate(5676, 2157, 0), "Orthen", 119, List.of(
                MaterialTypes::performXoloRemains
        ))); // 61

        ExcavationTypes.add(new Navigation("Saurthen debris", new Coordinate(5676, 2157, 0), "Orthen", 120, List.of(
                MaterialTypes::performSaurthenDebris
        ))); // 62

        ExcavationTypes.add(new Navigation("Infernal art", new Coordinate(3263, 3504, 0), "Infernal Source", 65, List.of(
                MaterialTypes::performInfernalArt
        ))); // 63

        ExcavationTypes.add(new Navigation("Shakroth remains", new Coordinate(3263, 3504, 0), "Infernal Source", 68, List.of(
                MaterialTypes::performShakrothRemains
        ))); // 63

        ExcavationTypes.add(new Navigation("Animal trophies", new Coordinate(3263, 3504, 0), "Infernal Source", 81, List.of(
                MaterialTypes::performAnimalTrophies
        ))); // 63

        ExcavationTypes.add(new Navigation("Dis overspill", new Coordinate(3263, 3504, 0), "Infernal Source", 89, List.of(
                MaterialTypes::performDisOverspill
        ))); // 64

        ExcavationTypes.add(new Navigation("Byzroth remains", new Coordinate(3263, 3504, 0), "Infernal Source", 98, List.of(
                MaterialTypes::performByzrothRemains
        ))); // 65

        ExcavationTypes.add(new Navigation("Hellfire forge", new Coordinate(3263, 3504, 0), "Infernal Source", 104, List.of(
                MaterialTypes::performHellfireForge
        ))); // 66

        ExcavationTypes.add(new Navigation("Chthonian trophies", new Coordinate(3263, 3504, 0), "Infernal Source", 110, List.of(
                MaterialTypes::performChthonianTrophies
        ))); // 67

        ExcavationTypes.add(new Navigation("Tsutsaroth remains", new Coordinate(3263, 3504, 0), "Infernal Source", 116, List.of(
                MaterialTypes::performTsutsarothRemains
        ))); // 68

        ExcavationTypes.add(new Navigation("Stockpiled art", new Coordinate(3762, 3150, 0), "Everlight", 105, List.of(
                MaterialTypes::performStockpiledArt
        ))); // 69

        ExcavationTypes.add(new Navigation("Optimatoi remains", new Coordinate(3762, 3150, 0), "Everlight", 117, List.of(
                MaterialTypes::performOptimatoiRemains
        ))); // 70
        ExcavationTypes.add(new Navigation("Ministry remains", new Coordinate(1746, 1246, 0), "Senntisten", 60)); // 71
        ExcavationTypes.add(new Navigation("Cathedral debris", new Coordinate(1771, 1274, 0), "Senntisten", 62)); // 72
        ExcavationTypes.add(new Navigation("Marketplace debris", new Coordinate(1669, 1321, 0), "Senntisten", 63)); // 73
        ExcavationTypes.add(new Navigation("Inquisitor remains", new Coordinate(1695, 1344, 0), "Senntisten", 64)); // 74
        ExcavationTypes.add(new Navigation("Gladiator remains", new Coordinate(1666, 1310, 0), "Senntisten", 66, List.of(
                MaterialTypes::performGladiatorRemains
        ))); // 75
        ExcavationTypes.add(new Navigation("Citizen remains", new Coordinate(1792, 1312, 0), "Senntisten", 68, List.of(
                MaterialTypes::performCitizenRemains
        ))); // 76

        ExcavationTypes.add(new Navigation("Material cache (Third Age iron)", new Coordinate(3386, 3399, 0), "Archaeology Guild", 5)); // 71
        ExcavationTypes.add(new Navigation("Material cache (Zarosian insignia)", new Coordinate(2873, 12632, 2), "Archaeology Guild", 5)); // 72
        ExcavationTypes.add(new Navigation("Material cache (samite silk)", new Coordinate(3370, 3200, 0), "Karid Et", 12)); // 73
        ExcavationTypes.add(new Navigation("Material cache (Imperial steel)", new Coordinate(2856, 12650, 2), "Archaeology Guild", 12)); // 74
        ExcavationTypes.add(new Navigation("Material cache (White oak)", new Coordinate(3009, 3499, 0), "Ice Mountain", 17)); // 75
        ExcavationTypes.add(new Navigation("Material cache (goldrune)", new Coordinate(3011, 3498, 0), "Ice Mountain", 20, List.of(
                MaterialTypes::performGoldrune
        ))); // 76
        ExcavationTypes.add(new Navigation("Material cache (Orthenglass)", new Coordinate(5443, 2500, 0), "Anachronia", 20)); // 77
        ExcavationTypes.add(new Navigation("Material cache (Tyrion purple)", new Coordinate(2860, 12623, 2), "Archaeology Guild", 25)); // 78
        ExcavationTypes.add(new Navigation("Material cache (ancient vis)", new Coordinate(3400, 3556, 2), "Slayer tower", 25)); // 79
        ExcavationTypes.add(new Navigation("Material cache (leather scraps)", new Coordinate(3440, 3529, 0), "Morytania north", 29)); // 80
        ExcavationTypes.add(new Navigation("Material cache (chaotic brimstone)", new Coordinate(3411, 3655, 0), "Daemonheim", 29)); // 81
        ExcavationTypes.add(new Navigation("Material cache (demonhide)", new Coordinate(2887, 5336, 0), "God Wars Dungeon", 29, List.of(
                MaterialTypes::performDemonhide
        ))); // 82
        ExcavationTypes.add(new Navigation("Material cache (Eye of Dagon)", new Coordinate(3410, 3654, 0), "Daemonheim", 36)); // 83
        ExcavationTypes.add(new Navigation("Material cache (hellfire metal)", new Coordinate(2887, 5336, 0), "God Wars Dungeon", 36, List.of(
                MaterialTypes::performHellfireMetal

        ))); // 84
        ExcavationTypes.add(new Navigation("Material cache (Blood of Orcus)", new Coordinate(3400, 3558, 2), "Slayer tower", 58)); // 85
        ExcavationTypes.add(new Navigation("Material cache (Star of Saradomin)", new Coordinate(3765, 3140, 0), "Everlight", 51)); // 86
        ExcavationTypes.add(new Navigation("Material cache (orgone)", new Coordinate(5676, 2157, 0), "Orthen", 73, List.of(
                MaterialTypes::performOrgone
        ))); // 87
        ExcavationTypes.add(new Navigation("Material cache (white marble)", new Coordinate(1699, 6110, 0), "Wizards Tower", 42)); // 88
        ExcavationTypes.add(new Navigation("Material cache (cadmium red)", new Coordinate(1692, 6110, 0), "Wizards Tower", 24)); // 89
        ExcavationTypes.add(new Navigation("Material cache (vellum)", new Coordinate(1698, 6115, 0), "Wizards Tower", 24)); // 90
        ExcavationTypes.add(new Navigation("Material cache (malachite green)", new Coordinate(2412, 2838, 0), "Warforge", 76, List.of(
                MaterialTypes::performMalachiteGreen
        ))); // 91
        ExcavationTypes.add(new Navigation("Material cache (warforged bronze)", new Coordinate(2412, 2838, 0), "Warforge", 76, List.of(
                MaterialTypes::performWarforgedBronze
        ))); // 92
        ExcavationTypes.add(new Navigation("Material cache (Yu'biusk clay)", new Coordinate(2392, 2820, 0), "Wardroge", 83)); // 93
        ExcavationTypes.add(new Navigation("Material cache (vulcanised rubber)", new Coordinate(2388, 2824, 0), "Warforge", 76)); // 94
        ExcavationTypes.add(new Navigation("Material cache (Mark of the Kyzaj)", new Coordinate(2871, 5346, 0), "God Wars Dungeon", 76)); // 95
        ExcavationTypes.add(new Navigation("Fort debris", new Coordinate(3374, 3181, 0), "Karid et", 12)); // 87

    } //EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(39203).option("Enter").results();

    public static void performWarforgedBronze() {
        performSteps("Material cache (warforged bronze)", 117243, "Descend", 47185921, new Coordinate(2400, 7312, 0));
    }
    public static void performMalachiteGreen() {
        performSteps("Material cache (malachite green)", 117243, "Descend", 47185921, new Coordinate(2393, 7317, 0));
    }
    //performSteps("Gladiatorial goblin remains", 117243, "Descend", 47185921, new Coordinate(2402, 7319, 0));


    public static void performKeshikGer() {
        performSteps("Keshik ger", 117129, "Teleport", 47185940, new Coordinate(2471, 7199, 1));
    }

    public static void performTailoryDebris() {
        performSteps("Tailory debris", 117129, "Teleport", 47185940, new Coordinate(2504, 7185, 1));
    }

    public static void performHellfireMetal() {
        performSteps("Material cache (hellfire metal)", 26439, "Jump-over", 0, new Coordinate(2885, 5361, 0));
    }

    public static void performDemonhide() {
        performSteps("Material cache (demonhide)", 26439, "Jump-over", 0, new Coordinate(2926, 5358, 0));
    }

    public static void performGladiatorRemains() {
        LocalPlayer player = Client.getLocalPlayer();
        logBlue("Starting additional steps for " + "Gladiator remains" + ".");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(120046).option("Enter (Non combat)").results();

        if (!results.isEmpty()) {
            results.nearest().interact("Enter (Non combat)");
            Execution.delay(random.nextLong(4500, 6000));
        }

        Coordinate currentCoordinate = player.getCoordinate();

        log("Current Coordinate: " + currentCoordinate.toString());
        Movement.walkTo(currentCoordinate.getX() + 4, currentCoordinate.getY() - 9, true);

        Execution.delayUntil(5000, () -> player.isMoving());
    }

    public static void performCitizenRemains() {
        LocalPlayer player = Client.getLocalPlayer();
        logBlue("Starting additional steps for " + "Citizen remains" + ".");

        performSteps("Citizen remains", 121130, "Squeeze through", 0, new Coordinate(1798, 1288, 0));

    }

    public static int getSelectedExcavationIndexByName(String name) {
        for (int i = 0; i < ExcavationTypes.size(); i++) {
            if (ExcavationTypes.get(i).getName().equals(name)) {
                return i;
            }
        }
        logError("Excavation type not found for name: " + name);
        return -1; // or throw an exception
    }

    public static void setSelectedExcavationIndexByName(String name) {
        for (int i = 0; i < ExcavationTypes.size(); i++) {
            if (ExcavationTypes.get(i).getName().equals(name)) {
                selectedExcavationIndex = i;
                return;
            }
        }
        logError("Excavation type not found for name: " + name);
    }
    public static void performGoldrune() {
        performSteps("Material cache (goldrune)", 39203, "Enter", 0, new Coordinate(3035, 9883, 0));
    }

    private static void performStockpiledArt() {
        logBlue("Starting additional steps for " + "Stockpiled art" + ".");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116494).option("Enter").results();

        if (!results.isEmpty()) {
            results.nearest().interact("Enter");
            Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2355, 7499, 1)));
            Execution.delay(random.nextLong(2000, 3000));
        }

        Movement.walkTo(2338, 7499, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2338, 7499, 0)));

        EntityResultSet<SceneObject> cave = SceneObjectQuery.newQuery().id(116636).option("Traverse").results();

        if (!cave.isEmpty()) {
            cave.nearest().interact("Traverse");
            Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2338, 7502, 1)));
            Execution.delay(random.nextLong(2000, 3000));
        }


        logBlue("Moving to final coordinate");
        Movement.walkTo(2340, 7515, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2340, 7515, 1)));

        setBotState(EXCAVATING);
        logBlue("Completed additional steps for " + "Stockpiled art" + ".");
    }

    private static void performOptimatoiRemains() {
        logBlue("Starting additional steps for " + "Optimatoi remains" + ".");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116494).option("Enter").results();

        if (!results.isEmpty()) {
            results.nearest().interact("Enter");
            Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2355, 7499, 1)));
            Execution.delay(random.nextLong(2000, 3000));
        }

        Movement.walkTo(2338, 7499, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2338, 7499, 0)));

        EntityResultSet<SceneObject> cave = SceneObjectQuery.newQuery().id(116636).option("Traverse").results();

        if (!cave.isEmpty()) {
            cave.nearest().interact("Traverse");
            Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2338, 7502, 1)));
            Execution.delay(random.nextLong(2000, 3000));
        }


        logBlue("Moving to final coordinate");
        Movement.walkTo(2328, 7528, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2328, 7528, 1)));

        setBotState(EXCAVATING);
        logBlue("Completed additional steps for " + "Optimatoi remains" + ".");
    }

    //[Original]: DoAction(DIALOGUE, 0, -1, 47185943)
    private static void performInfernalArt() {
        performSteps("Lodge art storage", 116691, "Descend", 47185943, new Coordinate(2632, 7383, 3));
    }

    private static void performShakrothRemains() {
        performSteps("Shakroth remains", 116691, "Descend", 47185943, new Coordinate(2605, 7464, 3));
    }

    private static void performAnimalTrophies() {
        performSteps("Animal trophies", 116691, "Descend", 47185943, new Coordinate(2658, 7393, 3));
    }

    //[Original]: DoAction(DIALOGUE, 0, -1, 47185946)
    private static void performDisOverspill() {
        logBlue("Starting additional steps for " + "Dis Overspill" + ".");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116691).option("Descend").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Descend"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, 47185946);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }


        logBlue("Moving to final coordinate");
        Movement.walkTo(2642, 7401, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2642, 7401, 3)));

        setBotState(EXCAVATING);
        logBlue("Completed additional steps for " + "Dis Overspill" + ".");
    }

    private static void performByzrothRemains() {
        LocalPlayer player = Client.getLocalPlayer();
        logBlue("Starting additional steps for Byzroth remains.");

        logBlue("Querying SceneObject with id 116691 and option 'Descend'.");
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116691).option("Descend").results();

        logBlue("Waiting for the SceneObject to be interactable.");
        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Descend"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, 47185946);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        logBlue("Walking to coordinate (2642, 7452).");
        Movement.walkTo(2642, 7452, true);
        Execution.delayUntil(10000, () -> player.getCoordinate().equals(new Coordinate(2642, 7452, 2)));

        logBlue("Querying SceneObject with id 116736 and option 'Enter'.");
        EntityResultSet<SceneObject> gate = SceneObjectQuery.newQuery().id(116736).option("Enter").results();
        if (!gate.isEmpty()) {
            logBlue("Interacting with the gate to 'Enter'.");
            gate.nearest().interact("Enter");
            logBlue("Waiting until player reaches coordinate (2635, 7455, 2).");
            Execution.delayUntil(10000, () -> player.getCoordinate().equals(new Coordinate(2635, 7455, 2)));
            logBlue("Delaying for 2-3 seconds.");
            Execution.delay(random.nextLong(2000, 3000));
        }

        logBlue("Walking to coordinate (2613, 7455).");
        Movement.walkTo(2613, 7455, true);
        Execution.delayUntil(10000, () -> player.getCoordinate().equals(new Coordinate(2613, 7455, 2)));
        if (!gate.isEmpty()) {
            logBlue("Interacting with the gate to 'Enter' again.");
            gate.nearest().interact("Enter");
            logBlue("Waiting until player reaches coordinate (2606, 7453, 2).");
            Execution.delayUntil(10000, () -> player.getCoordinate().equals(new Coordinate(2606, 7453, 2)));
            Execution.delay(random.nextLong(2000, 3000));

        }

        logBlue("Moving to final coordinate (2604, 7448, 2).");
        Movement.walkTo(2604, 7448, true);
        Execution.delayUntil(10000, () -> player.getCoordinate().equals(new Coordinate(2604, 7448, 2)));

        logBlue("Setting bot state to EXCAVATING.");
        setBotState(EXCAVATING);
        logBlue("Completed additional steps for Byzroth remains.");
    }

    private static void performHellfireForge() {
        logBlue("Starting additional steps for " + "Hellfire forge" + ".");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116691).option("Descend").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Descend"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, 47185946);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }


        logBlue("Moving to final coordinate");
        Movement.walkTo(2646, 7445, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2646, 7445, 2)));

        setBotState(EXCAVATING);
        logBlue("Completed additional steps for " + "Hellfire forge" + ".");
    }

    private static void performChthonianTrophies() {
        LocalPlayer player = Client.getLocalPlayer();
        logBlue("Starting additional steps for " + "Chthonian trophies" + ".");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116691).option("Descend").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Descend"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, 47185949);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        Movement.walkTo(2641, 7434, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2641, 7434, 1)));
        logBlue("Querying SceneObject with id 116736 and option 'Enter'.");
        EntityResultSet<SceneObject> gate = SceneObjectQuery.newQuery().id(116736).option("Enter").results();
        if (!gate.isEmpty()) {
            logBlue("Interacting with the gate to 'Enter'.");
            gate.nearest().interact("Enter");
            logBlue("Waiting until player reaches coordinate (2639, 7439, 1).");
            Execution.delayUntil(10000, () -> player.getCoordinate().equals(new Coordinate(2639, 7439, 1)));
            logBlue("Delaying for 2-3 seconds.");
            Execution.delay(random.nextLong(2000, 3000));
        }


        logBlue("Moving to final coordinate");
        Movement.walkTo(2622, 7441, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2622, 7441, 1)));

        setBotState(EXCAVATING);
        logBlue("Completed additional steps for " + "Chthonian trophies" + ".");
    }

    private static void performTsutsarothRemains() {
        LocalPlayer player = Client.getLocalPlayer();
        logBlue("Starting additional steps for " + "Tsutsaroth remains" + ".");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116691).option("Descend").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Descend"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, 47185949);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        Movement.walkTo(2643, 7418, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2643, 7418, 1)));
        logBlue("Querying SceneObject with id 116736 and option 'Enter'.");
        EntityResultSet<SceneObject> gate = SceneObjectQuery.newQuery().id(116736).option("Enter").results();
        if (!gate.isEmpty()) {
            logBlue("Interacting with the gate to 'Enter'.");
            gate.nearest().interact("Enter");
            logBlue("Waiting until player reaches coordinate (2616, 7406, 1).");
            Execution.delayUntil(random.nextLong(4500, 6000), () -> player.getCoordinate().equals(new Coordinate(2616, 7406, 1)));
            logBlue("Delaying for 2-3 seconds.");
            Execution.delay(random.nextLong(2000, 3000));
        }


        logBlue("Moving to final coordinate");
        Movement.walkTo(2611, 7405, true);
        Execution.delayUntil(random.nextLong(7500, 10000), () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(2611, 7405, 1)));

        setBotState(EXCAVATING);
        logBlue("Completed additional steps for " + "Tsutsaroth remains" + ".");
    }

    private static void performDragonkinReliquary() {
        performSteps("Dragonkin reliquary", 119042, "Enter", 0, new Coordinate(1126, 120, 2));
    }

    private static void performDragonkinCoffin() {
        performSteps("Dragonkin coffin", 119042, "Enter", 0, new Coordinate(1124, 140, 2));
    }

    private static void performVaranusaurRemains() {
        performSteps("Varanusaur remains", 119042, "Enter", 0, new Coordinate(1114, 140, 2));
    }

    private static void performAutopsyTable() {
        performSteps("Autopsy table", 119049, "Enter", 0, new Coordinate(1249, 99, 0));
    }

    private static void performExperimentWorkbench() {
        performSteps("Experiment workbench", 119049, "Enter", 0, new Coordinate(1262, 101, 0));
    }

    private static void performAughraRemains() {
        logBlue("Starting additional steps for Aughra Remains.");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(119029).option("Teleport").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Teleport"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(71));

        if (Interfaces.isOpen(71)) {
            logBlue("Interface is open. Teleporting.");
            //[Original]: DoAction(COMPONENT, 1, 3, 4653065)
            component(1, 3, 4653065);
            Execution.delay(random.nextLong(800, 1000));
            //[Original]: DoAction(COMPONENT, 1, -1, 4653084)
            component(1, -1, 4653084);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(5000, 7000));
        }


        Movement.walkTo(1111, 205, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(1111, 205, 1)));

        setBotState(EXCAVATING);
        logBlue("Completed additional steps for Aughra Remains.");
    }

    private static void performMokshaDevice() {
        logBlue("Starting additional steps for Moksha Device.");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(119029).option("Teleport").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Teleport"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(71));

        if (Interfaces.isOpen(71)) {
            logBlue("Interface is open. Teleporting.");
            //[Original]: DoAction(COMPONENT, 1, 3, 4653065)
            component(1, 3, 4653065);
            Execution.delay(random.nextLong(800, 1000));
            //[Original]: DoAction(COMPONENT, 1, -1, 4653084)
            component(1, -1, 4653084);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        Movement.walkTo(1116, 219, true);
        Execution.delayUntil(10000, () -> Client.getLocalPlayer().getCoordinate().equals(new Coordinate(1116, 219, 2)));

        setBotState(EXCAVATING);
        logBlue("Completed additional steps for Moksha Device.");
    }

    private static void performXoloMine() {
        performSteps("Xolo mine", 119066, "Enter", 0, new Coordinate(1255, 255, 0));
    }

    private static void performXoloRemains() {
        performSteps("Xolo remains", 119066, "Enter", 0, new Coordinate(1244, 212, 0));
    }

    private static void performSaurthenDebris() {
        performSteps("Saurthen debris", 119066, "Enter", 0, new Coordinate(1251, 226, 0));
    }

    private static void performOrgone() {
        performSteps("Material cache (orgone)", 119066, "Enter", 0, new Coordinate(1257, 236, 0));
    }


    //[Original]: DoAction(DIALOGUE, 0, -1, 47185943)
    private static void performAncientMagickMunitions() {
        performSteps("Ancient magick munitions", 116926, "Enter", 47185943, new Coordinate(2657, 7586, 0));
    }

    private static void performPraetorianRemains() {
        performSteps("Praetorian remains", 116926, "Enter", 47185943, new Coordinate(2651, 7570, 0));
    }

    private static void performWarTableDebris() {
        performSteps("War table debris", 116926, "Enter", 47185943, new Coordinate(2656, 7579, 0));
    }

    //[Original]: DoAction(DIALOGUE, 0, -1, 47185921)
    private static void performGladiatorialGoblinRemains() {
        performSteps("Gladiatorial goblin remains", 117243, "Descend", 47185921, new Coordinate(2402, 7319, 0));
    }

    private static void performCrucibleStandsDebris() {
        performSteps("Crucible stands debris", 117243, "Descend", 47185921, new Coordinate(2416, 7319, 0));
    }

    //[Original]: DoAction(DIALOGUE, 0, -1, 47185940)
    private static void performGoblinDormDebris() {
        performSteps("Goblin dorm debris", 117243, "Descend", 47185940, new Coordinate(2315, 7328, 0));
    }

    private static void performBigHighWarGodShrine() {
        performSteps("Big High War God shrine", 117243, "Descend", 47185940, new Coordinate(2312, 7406, 0));
    }

    private static void performYuBiuskAnimalPen() {
        performSteps("Yu'biusk animal pen", 117243, "Descend", 47185940, new Coordinate(2295, 7332, 0));
    }

    private static void performGoblinTraineeRemains() {
        performSteps("Goblin trainee remains", 117243, "Descend", 47185940, new Coordinate(2299, 7408, 0));
    }

    private static void performKyzajChampionsBoudoir() {
        performSteps("Kyzaj champion's boudoir", 117243, "Descend", 47185940, new Coordinate(2310, 7319, 0));
    }


    private static void performCommonSteps(String stepName, int sceneObjectId, String sceneObjectOption, int dialogId, Coordinate moveToCoordinate, Coordinate finalCoordinate, int interactObjectId, String interactOption, long minDelay, long maxDelay) {
        LocalPlayer player = Client.getLocalPlayer();
        logBlue("Starting additional steps for " + stepName);

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(sceneObjectId).option(sceneObjectOption).results();
        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact(sceneObjectOption));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, dialogId);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(moveToCoordinate);

        // Interact with the scene object
        EntityResultSet<SceneObject> sceneObjects = SceneObjectQuery.newQuery().id(interactObjectId).option(interactOption).results();
        if (sceneObjects != null && sceneObjects.nearest().interact(interactOption)) {
            Execution.delay(random.nextLong(minDelay, maxDelay));
        }

        if (finalCoordinate != null) {
            MoveTo(finalCoordinate);
        }

        setBotState(EXCAVATING);
    }

    public static void performMakeshiftPieOven() {
        performCommonSteps("Makeshift Pie Oven", 117243, "Descend", 47185940, new Coordinate(2298, 7373, 0), null, 117254, "Pass through", 3500, 5000);
    }

    public static void performWarforgeWeaponRack() {
        performCommonSteps("Warforge Weapon Rack", 117243, "Descend", 47185940, new Coordinate(2298, 7373, 0), null, 117254, "Pass through", 3500, 5000);
    }

    public static void performWarforgeScrapPile() {
        performCommonSteps("Warforge Scrap Pile", 117243, "Descend", 47185940, new Coordinate(2296, 7392, 0), new Coordinate(2286, 7393, 0), 117330, "Traverse", 3500, 5000);
    }

    public static void performBandosSanctumDebris() {
        performCommonSteps("Bandos Sanctum Debris", 117243, "Descend", 47185921, new Coordinate(2404, 7337, 0), new Coordinate(2399, 7345, 0), 117270, "Climb through", 3500, 5000);
    }


    private static void performSteps(String stepName, int sceneObjectId, String sceneObjectOption, int dialogId, Coordinate finalCoordinate) {
        logBlue("Starting additional steps for " + stepName + ".");

        logBlue("Querying for SceneObject with id " + sceneObjectId + " and option '" + sceneObjectOption + "'.");
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(sceneObjectId).option(sceneObjectOption).results();

        logBlue("Waiting for '" + sceneObjectOption + "' option to be available.");
        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact(sceneObjectOption));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, dialogId);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        logBlue("Moving to final coordinate: " + finalCoordinate);
        MoveTo(finalCoordinate);

        setBotState(EXCAVATING);
        logBlue("Completed additional steps for " + stepName + ".");
    }

    private static void performProjectionSpaceSteps() {
        performSteps("Lodge art storage", 130291, "Enter", 0, new Coordinate(2197, 9242, 1));
    }

    private static void performSecurityBoothSteps() {
        performSteps("Lodge art storage", 130291, "Enter", 0, new Coordinate(2223, 9253, 1));
    }

    private static void performTravellersStationSteps() {
        performSteps("Lodge art storage", 130291, "Enter", 0, new Coordinate(2196, 9261, 1));
    }

    private static void performLodgeArtStorageSteps() {
        performSteps("Lodge art storage", 116691, "Descend", 47185921, new Coordinate(2592, 7335, 0));
    }

    private static void performLodgeBarStorageSteps() {
        performSteps("Lodge bar storage", 116691, "Descend", 47185921, new Coordinate(2591, 7326, 0));
    }

    private static void performSacrificialAltarSteps() {
        performSteps("Sacrificial altar", 116691, "Descend", 47185940, new Coordinate(2637, 7329, 0));
    }

    private static void performDisDungeonDebrisSteps() {
        performSteps("Dis dungeon debris", 116691, "Descend", 47185940, new Coordinate(2656, 7307, 0));
    }

    private static void performCultistFootlockerSteps() {
        performSteps("Cultist footlocker", 116691, "Descend", 47185940, new Coordinate(2675, 7309, 0));
    }

    public static void performLegionaryRemainsSteps() {
        performSteps("Legionary remains", 116926, "Enter", 47185921, new Coordinate(2436, 7596, 0));
    }

    public static void performCastraDebrisSteps() {
        performSteps("Castra debris", 116926, "Enter", 47185921, new Coordinate(2448, 7587, 0));
    }

    public static void performAdministratumDebrisSteps() {
        performSteps("Administratum debris", 116926, "Enter", 47185921, new Coordinate(2449, 7570, 0));
    }

    public static void performPraesidioDebrisSteps() {
        //[Original]: DoAction(DIALOGUE, 0, -1, 47185940)
        performSteps("Praesidio remains", 116926, "Enter", 47185940, new Coordinate(2255, 7600, 0));
    }

    public static void performChapelDebrisSteps() {
        performSteps("Kharid-et chapel debris", 116926, "Enter", 47185921, new Coordinate(2486, 7577, 0));
    }

    public static void performArmariumDebrisSteps() {
        performSteps("Armarium debris", 116926, "Enter", 47185921, new Coordinate(2488, 7588, 0));
    }

    public static void performCulinarumDebrisSteps() {
        performSteps("Culinarum debris", 116926, "Enter", 47185921, new Coordinate(2482, 7587, 0));
    }

    public static void performCarceremDebrisSteps() {
        LocalPlayer player = Client.getLocalPlayer();
        logBlue("Starting additional steps for Carcerum Debris");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116926).option("Enter").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Enter"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, 47185940);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        Movement.walkTo(2258, 7586, true);
        Execution.delayUntil(30000, () -> player.getCoordinate().equals(new Coordinate(2258, 7586, 0)));

        SceneObject legatusBarrier = SceneObjectQuery.newQuery().name("Legatus barrier").option("Pass").results().nearest();

        if (legatusBarrier != null && legatusBarrier.interact("Pass")) {
            Execution.delay(random.nextLong(3500, 5000));
        }

        if (lastPlayerCoordinate != null) {
            logBlue("Moving to last recorded coordinate: " + lastPlayerCoordinate);
            MoveTo(lastPlayerCoordinate);
        } else {
            MoveTo(new Coordinate(2266, 7585, 0));
        }
        setBotState(EXCAVATING);
    }

    public static void performPontifixRemainsSteps() {
        LocalPlayer player = Client.getLocalPlayer();
        logBlue("Starting additional steps for Pontifex Remains");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116926).option("Enter").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Enter"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, 47185921);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(new Coordinate(2476, 7570, 0));

        EntityResultSet<SceneObject> barrier = SceneObjectQuery.newQuery().id(116930).option("Pass").results();

        if (barrier != null && barrier.nearest().interact("Pass")) {
            Execution.delay(random.nextLong(3500, 5000));
        }

        if (lastPlayerCoordinate != null) {
            logBlue("Moving to last recorded coordinate: " + lastPlayerCoordinate);
            MoveTo(lastPlayerCoordinate);
        } else {
            MoveTo(new Coordinate(2483, 7570, 0));
        }
        setBotState(EXCAVATING);
    }


    public static void performOrcusAltarRemainsSteps() {
        LocalPlayer player = Client.getLocalPlayer();
        logBlue("Starting additional steps for Pontifex Remains");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(116926).option("Enter").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Enter"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");
            dialog(0, -1, 47185921);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(new Coordinate(2476, 7570, 0));

        EntityResultSet<SceneObject> barrier = SceneObjectQuery.newQuery().id(116930).option("Pass").results();

        if (barrier != null && barrier.nearest().interact("Pass")) {
            Execution.delay(random.nextLong(3500, 5000));
        }

        if (lastPlayerCoordinate != null) {
            logBlue("Moving to last recorded coordinate: " + lastPlayerCoordinate);
            MoveTo(lastPlayerCoordinate);
        } else {
            MoveTo(new Coordinate(2489, 7572, 0));
        }
        setBotState(EXCAVATING);
    }

    private static void removeWeaponsAndShields() {
        InventoryItemQuery.newQuery(94).slots(3).results().forEach(item -> {
            logBlue("Removing weapon: " + item.getName());
            Equipment.interact(Equipment.Slot.WEAPON, "Remove");
        });

        InventoryItemQuery.newQuery(94).slots(5).results().forEach(item -> {
            logBlue("Removing shield: " + item.getName());
            Equipment.interact(Equipment.Slot.SHIELD, "Remove");
        });
    }

    private static void interactWithSceneObject(int objectId, String option, long delay) {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(objectId).option(option).results();
        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact(option));
        Execution.delay(random.nextLong(delay, delay + 1000));
    }

    private static void moveToCoordinates(Coordinate... coordinates) {
        LocalPlayer player = Client.getLocalPlayer();
        for (Coordinate coordinate : coordinates) {
            MiniMenu.interact(WalkAction.WALK.getType(), 0, coordinate.getX(), coordinate.getY());
            Execution.delayUntil(10000, () -> player.getCoordinate().equals(coordinate));
        }
    }

    public static void performGravitonResearchDebris() {
        logBlue("Starting additional steps for Graviton Research Debris");
        removeWeaponsAndShields();
        interactWithSceneObject(117137, "Leap", 6000);
        moveToCoordinates(new Coordinate(2435, 7178, 1), new Coordinate(2416, 7169, 1), new Coordinate(2399, 7162, 1));
        interactWithSceneObject(117137, "Leap", 6000);
        moveToLastOrDefaultCoordinate(new Coordinate(2391, 7176, 1));
        equipWeaponsAndShields();
        setBotState(EXCAVATING);
    }

    public static void performDestroyedGolem() {
        logBlue("Starting additional steps for Graviton Research Debris");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(117129).option("Teleport").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Teleport"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");

            dialog(0, -1, 47185940);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(new Coordinate(2454, 7201, 1));
        removeWeaponsAndShields();
        interactWithSceneObject(117137, "Leap", 6000);
        moveToCoordinates(new Coordinate(2438, 7173, 1), new Coordinate(2420, 7149, 1), new Coordinate(2414, 7139, 1));
        interactWithSceneObject(117137, "Leap", 6000);
        moveToLastOrDefaultCoordinate(new Coordinate(2404, 7124, 1));
        equipWeaponsAndShields();
        setBotState(EXCAVATING);
    }

    private static void performWeaponsResearchDebris() {
        logBlue("Starting additional steps for " + "Weapon Research Debris" + ".");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(117129).option("Teleport").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Teleport"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");

            dialog(0, -1, 47185940);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(new Coordinate(2454, 7201, 1));
        removeWeaponsAndShields();
        interactWithSceneObject(117137, "Leap", 6000);
        moveToCoordinates(new Coordinate(2435, 7178, 1), new Coordinate(2416, 7169, 1), new Coordinate(2399, 7162, 1));
        interactWithSceneObject(117137, "Leap", 6000);
        moveToCoordinates(new Coordinate(2391, 7198, 1));
        interactWithSceneObject(117181, "Traverse", 6000);
        moveToLastOrDefaultCoordinate(new Coordinate(2391, 7176, 1));
        equipWeaponsAndShields();
        setBotState(EXCAVATING);
    }

    public static void performKeshikTowerDebris() {
        logBlue("Starting additional steps for Keshik Tower Debris");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(117129).option("Teleport").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Teleport"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");

            dialog(0, -1, 47185940);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(new Coordinate(2454, 7201, 1));
        removeWeaponsAndShields();
        interactWithSceneObject(117137, "Leap", 6000);
        moveToCoordinates(new Coordinate(2439, 7181, 1), new Coordinate(2440, 7149, 1), new Coordinate(2465, 7151, 1),
                new Coordinate(2490, 7144, 1), new Coordinate(2507, 7127, 1), new Coordinate(2519, 7122, 1));
        interactWithSceneObject(117137, "Leap", 6000);
        moveToLastOrDefaultCoordinate(new Coordinate(2540, 7134, 1));
        equipWeaponsAndShields();
        setBotState(EXCAVATING);
    }

    private static void performKeshikWeaponRack() {
        logBlue("Starting additional steps for Keshik Weapon Rack");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(117129).option("Teleport").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Teleport"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");

            dialog(0, -1, 47185940);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(new Coordinate(2454, 7201, 1));
        removeWeaponsAndShields();
        interactWithSceneObject(117137, "Leap", 6000);
        moveToCoordinates(new Coordinate(2418, 7233, 1), new Coordinate(2373, 7244, 1), new Coordinate(2350, 7235, 1));
        interactWithSceneObject(117137, "Leap", 6000);
        moveToLastOrDefaultCoordinate(new Coordinate(2336, 7245, 1));
        equipWeaponsAndShields();
        setBotState(EXCAVATING);
    }

    private static void performFlightResearchDebris() {
        logBlue("Starting additional steps for Fligh Research Debris");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(117129).option("Teleport").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Teleport"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");

            dialog(0, -1, 47185940);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(new Coordinate(2454, 7201, 1));
        removeWeaponsAndShields();
        interactWithSceneObject(117137, "Leap", 6000);
        moveToCoordinates(new Coordinate(2435, 7180, 1), new Coordinate(2442, 7149, 1), new Coordinate(2471, 7143, 1));
        interactWithSceneObject(117137, "Leap", 6000);
        moveToLastOrDefaultCoordinate(new Coordinate(2479, 7127, 1));
        equipWeaponsAndShields();
        setBotState(EXCAVATING);
    }

    private static void performAetheriumForge() {
        logBlue("Starting additional steps for Aetherium Forge");
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(117129).option("Teleport").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Teleport"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");

            dialog(0, -1, 47185940);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(new Coordinate(2454, 7201, 1));
        removeWeaponsAndShields();
        interactWithSceneObject(117137, "Leap", 6000);
        moveToCoordinates(new Coordinate(2437, 7182, 1), new Coordinate(2430, 7163, 1), new Coordinate(2417, 7154, 1));
        interactWithSceneObject(117175, "Interact", 6000);
        moveToLastOrDefaultCoordinate(new Coordinate(2409, 7148, 3));
        equipWeaponsAndShields();
        setBotState(EXCAVATING);
    }

    private static void performHowlWorkshopDebris() {
        logBlue("Starting additional steps for Howls Workshop Debris");

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(117129).option("Teleport").results();

        Execution.delayUntil(10000, () -> !results.isEmpty() && results.nearest().interact("Teleport"));

        logBlue("Small Delay.");
        Execution.delayUntil(random.nextLong(5000, 6000), () -> Interfaces.isOpen(720));

        if (Interfaces.isOpen(720)) {
            logBlue("Interface 720 is open. Interacting with dialog.");

            dialog(0, -1, 47185940);

            logBlue("Delaying for 4-5 seconds.");
            Execution.delay(random.nextLong(4000, 5000));
        }

        MoveTo(new Coordinate(2454, 7201, 1));
        removeWeaponsAndShields();
        interactWithSceneObject(117137, "Leap", 6000);
        moveToCoordinates(new Coordinate(2437, 7182, 1), new Coordinate(2430, 7163, 1), new Coordinate(2417, 7154, 1));
        interactWithSceneObject(117175, "Interact", 6000);
        moveToLastOrDefaultCoordinate(new Coordinate(2410, 7158, 3));
        equipWeaponsAndShields();
        setBotState(EXCAVATING);
    }

    private static void moveToLastOrDefaultCoordinate(Coordinate defaultCoordinate) {
        if (lastPlayerCoordinate != null) {
            logBlue("Moving to last recorded coordinate: " + lastPlayerCoordinate);
            MoveTo(lastPlayerCoordinate);
        } else {
            MoveTo(defaultCoordinate);
        }
    }

    private static void equipWeaponsAndShields() {
        InventoryItemQuery.newQuery(93)
                .option("Wield")
                .results()
                .forEach(item -> {
                    logBlue("Equipping item: " + item.getName());
                    Backpack.interact(item.getName(), "Wield");
                });
    }


    public static Navigation getExcavationTypeByName(String name) {
        for (Navigation excavationType : ExcavationTypes) {
            if (excavationType.getName().equals(name)) {
                return excavationType;
            }
        }
        return null; // or throw an exception if preferred
    }

    public static void setExcavationTypeByName(String name, Navigation newExcavationType) {
        for (int i = 0; i < ExcavationTypes.size(); i++) {
            if (ExcavationTypes.get(i).getName().equals(name)) {
                ExcavationTypes.set(i, newExcavationType);
                return;
            }
        }
        // Optionally handle the case where the name was not found
    }

    public static Queue<String> collectionQueue = new LinkedList<>();

    public static void addCollectionToQueue(String collectionName) {
        collectionQueue.add(collectionName);
    }

    public static void removeCollectionFromQueue(String collectionName) {
        collectionQueue.remove(collectionName);
    }


    public static void moveToNextHotspotBasedOnMissingArtifacts(List<Artifact> missingArtifacts) {
        logBlue("Starting moveToNextHotspotBasedOnMissingArtifacts.");

        if (missingArtifacts == null || missingArtifacts.isEmpty()) {
            logBlue("Collection is complete. Moving to the next collection.");
            checkArtifacts();
            return;
        }

        int playerArchaeologyLevel = Skills.ARCHAEOLOGY.getLevel();
        logBlue("Player Archaeology Level: " + playerArchaeologyLevel);

        for (Artifact artifact : missingArtifacts) {
            logBlue("Checking artifact: " + artifact.getName());

            int requiredLevel = artifact.getLevelRequired();
            logBlue("Artifact required level: " + requiredLevel);

            if (playerArchaeologyLevel < requiredLevel) {
                logWarning("Skipping artifact: " + artifact.getName() + " due to insufficient level.");
                continue;
            }

            String hotspot = artifact.getHotspot();
            Navigation nextHotspot = getExcavationTypeByName(hotspot);

            if (nextHotspot != null) {
                selectedExcavationIndex = ExcavationTypes.indexOf(nextHotspot);
                logBlue("Setting excavation type to: " + nextHotspot.getName() + " with index: " + selectedExcavationIndex);
                MoveTo(nextHotspot.getCoordinate());

                if (!nextHotspot.getAdditionalSteps().isEmpty()) {
                    logBlue("Executing additional steps for: " + nextHotspot.getName());
                    nextHotspot.executeAdditionalSteps();
                }

                setBotState(EXCAVATING);
                logBlue("Completed moveToNextHotspotBasedOnMissingArtifacts.");
                return;
            } else {
                logError("No matching hotspot found for artifact: " + artifact.getName());
            }
        }

        logBlue("Completed moveToNextHotspotBasedOnMissingArtifacts.");
    }

    public static boolean checkIfCollectionComplete(String collectionName) {
        Set<String> uniqueArtifacts = new HashSet<>();
        List<Artifact> requiredArtifacts = ArtifactRequirements.getAllArtifactRequirements().stream()
                .filter(artifact -> artifact.getCollectionNames().contains(collectionName))
                .filter(artifact -> uniqueArtifacts.add(artifact.getId())) // Ensure uniqueness
                .toList();

        for (Artifact artifact : requiredArtifacts) {
            long count = artifactCounts.getOrDefault(artifact.getId(), 0L);
            if (count == 0) {
                return false;
            }
        }
        return true;
    }
}