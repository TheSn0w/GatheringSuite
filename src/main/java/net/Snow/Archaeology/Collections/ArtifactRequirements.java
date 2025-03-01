package net.Snow.Archaeology.Collections;

import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.Snow.ImGui.CustomLogger.*;

public class ArtifactRequirements {

    public static Map<String, Integer> getMaterialsByName(String artifactName) {
        List<Artifact> artifacts = getAllArtifactRequirements();

        for (Artifact artifact : artifacts) {
            if (artifact.getName().equals(artifactName)) {
                return artifact.getRequiredMaterials();
            }
        }

        return null; // Return null if the artifact isn't found
    }


    public static void checkMaterials(String artifactName) {
        // Retrieve the required materials for the given artifact name
        Map<String, Integer> requiredMaterials = ArtifactRequirements.getMaterialsByName(artifactName);

        if (requiredMaterials != null) {
            // Query both inventories (93 and 885)
            ResultSet<Item> inventoryItems93 = InventoryItemQuery.newQuery(93).results();
            ResultSet<Item> inventoryItems885 = InventoryItemQuery.newQuery(885).results();

            // Combine the results from both inventories
            List<Item> combinedInventoryItems = inventoryItems93.stream()
                    .collect(Collectors.toList());
            combinedInventoryItems.addAll(inventoryItems885.stream()
                    .toList());

            // Track missing materials and insufficiencies
            boolean hasAllMaterials = true;
            for (Map.Entry<String, Integer> entry : requiredMaterials.entrySet()) {
                String materialName = entry.getKey();
                int requiredAmount = entry.getValue();

                // Check if the material is in the combined inventory
                Item matchingItem = combinedInventoryItems.stream()
                        .filter(item -> materialName.equals(item.getName()))
                        .findFirst()
                        .orElse(null);

                if (matchingItem == null) {
                    hasAllMaterials = false;
                    logWarning("Missing material: " + materialName + ", required: " + requiredAmount);
                } else if (matchingItem.getStackSize() < requiredAmount) {
                    hasAllMaterials = false;
                    int missingAmount = requiredAmount - matchingItem.getStackSize();
                    logWarning("Insufficient " + materialName + ": need " + missingAmount + " more.");
                }
            }

            if (hasAllMaterials) {
                logSuccess("You have all the required materials for " + artifactName + ".");
            } else {
                logWarning("You are missing some materials for " + artifactName + ".");
            }
        } else {
            logError("Artifact '" + artifactName + "' not found.");
        }
    }



    public static List<Artifact> getAllArtifactRequirements() {
        Map<String, Integer> hookahPipeMaterials = new HashMap<>();
        hookahPipeMaterials.put("Third Age iron", 10);
        hookahPipeMaterials.put("Goldrune", 12);
        hookahPipeMaterials.put("Orthenglass", 8);

        Map<String, Integer> opulentWineGobletMaterials = new HashMap<>();
        opulentWineGobletMaterials.put("Third Age iron", 14);
        opulentWineGobletMaterials.put("Goldrune", 16);

        Map<String, Integer> crestOfDagonMaterials = new HashMap<>();
        crestOfDagonMaterials.put("Goldrune", 14);
        crestOfDagonMaterials.put("Orthenglass", 18);

        Map<String, Integer> disorderPaintingMaterials = new HashMap<>();
        disorderPaintingMaterials.put("Samite silk", 6);
        disorderPaintingMaterials.put("White oak", 6);
        disorderPaintingMaterials.put("Vellum", 6);
        disorderPaintingMaterials.put("Cadmium red", 14);

        Map<String, Integer> impMaskMaterials = new HashMap<>();
        impMaskMaterials.put("Leather scraps", 10);
        impMaskMaterials.put("Chaotic brimstone", 10);
        impMaskMaterials.put("Demonhide", 12);

        Map<String, Integer> lesserDemonMaskMaterials = new HashMap<>();
        lesserDemonMaskMaterials.put("Leather scraps", 6);
        lesserDemonMaskMaterials.put("Chaotic brimstone", 8);
        lesserDemonMaskMaterials.put("Demonhide", 12);
        lesserDemonMaskMaterials.put("Cadmium red", 6);

        Map<String, Integer> greaterDemonMaskMaterials = new HashMap<>();
        greaterDemonMaskMaterials.put("Third Age iron", 6);
        greaterDemonMaskMaterials.put("Leather scraps", 6);
        greaterDemonMaskMaterials.put("Chaotic brimstone", 8);
        greaterDemonMaskMaterials.put("Demonhide", 12);

        Map<String, Integer> orderOfDisRobesMaterials = new HashMap<>();
        orderOfDisRobesMaterials.put("Samite silk", 16);
        orderOfDisRobesMaterials.put("Cadmium red", 10);
        orderOfDisRobesMaterials.put("Eye of Dagon", 14);

        Map<String, Integer> ritualDaggerMaterials = new HashMap<>();
        ritualDaggerMaterials.put("Goldrune", 16);
        ritualDaggerMaterials.put("Hellfire metal", 24);
        ritualDaggerMaterials.put("Ruby", 1);

        Map<String, Integer> venatorDaggerMatierals = new HashMap<>();
        venatorDaggerMatierals.put("Third Age iron", 16);
        venatorDaggerMatierals.put("Zarosian insignia", 12);


        Map<String, Integer> venatorLightCrossbowMaterials = new HashMap<>();
        venatorLightCrossbowMaterials.put("Third Age iron", 12);
        venatorLightCrossbowMaterials.put("Zarosian insignia", 16);

        Map<String, Integer> primisElementisStandardMaterials = new HashMap<>();
        primisElementisStandardMaterials.put("Samite silk", 16);
        primisElementisStandardMaterials.put("Third Age iron", 12);

        Map<String, Integer> legionaryGladiusMaterials = new HashMap<>();
        legionaryGladiusMaterials.put("Third Age iron", 10);
        legionaryGladiusMaterials.put("Zarosian insignia", 6);
        legionaryGladiusMaterials.put("Imperial steel", 12);

        Map<String, Integer> legionarySquareShieldMaterials = new HashMap<>();
        legionarySquareShieldMaterials.put("Third Age iron", 8);
        legionarySquareShieldMaterials.put("Zarosian insignia", 8);
        legionarySquareShieldMaterials.put("Imperial steel", 12);

        Map<String, Integer> zarosEffigyMaterials = new HashMap<>();
        zarosEffigyMaterials.put("Samite silk", 8);
        zarosEffigyMaterials.put("White oak", 10);
        zarosEffigyMaterials.put("Zarosian insignia", 12);

        Map<String, Integer> zarosianTrainingDummyMaterials = new HashMap<>();
        zarosianTrainingDummyMaterials.put("Third Age iron", 16);
        zarosianTrainingDummyMaterials.put("White oak", 14);

        Map<String, Integer> fryingPanMaterials = new HashMap<>();
        fryingPanMaterials.put("Third Age iron", 20);
        fryingPanMaterials.put("White marble", 24);

        Map<String, Integer> hallowedLanternMaterials = new HashMap<>();
        hallowedLanternMaterials.put("Third Age iron", 20);
        hallowedLanternMaterials.put("Keramos", 24);
        hallowedLanternMaterials.put("White candle", 1);

        Map<String, Integer> brandingIronMaterials = new HashMap<>();
        brandingIronMaterials.put("Third Age iron", 14);
        brandingIronMaterials.put("Eye of Dagon", 12);
        brandingIronMaterials.put("Hellfire metal", 20);

        Map<String, Integer> manaclesMaterials = new HashMap<>();
        manaclesMaterials.put("Third Age iron", 14);
        manaclesMaterials.put("Chaotic brimstone", 18);
        manaclesMaterials.put("Eye of Dagon", 14);

        Map<String, Integer> ancientTimepieceMaterials = new HashMap<>();
        ancientTimepieceMaterials.put("Goldrune", 12);
        ancientTimepieceMaterials.put("Imperial steel", 16);
        ancientTimepieceMaterials.put("Ancient vis", 18);

        Map<String, Integer> legatusPendantMaterials = new HashMap<>();
        legatusPendantMaterials.put("Third Age iron", 16);
        legatusPendantMaterials.put("Goldrune", 18);
        legatusPendantMaterials.put("Ancient vis", 12);
        legatusPendantMaterials.put("Dragonstone", 1);

        Map<String, Integer> ceremonialUnicornOrnamentMaterials = new HashMap<>();
        ceremonialUnicornOrnamentMaterials.put("Keramos", 26);
        ceremonialUnicornOrnamentMaterials.put("Cobalt blue", 20);

        Map<String, Integer> ceremonialUnicornSaddleMaterials = new HashMap<>();
        ceremonialUnicornSaddleMaterials.put("Leather scraps", 24);
        ceremonialUnicornSaddleMaterials.put("Cobalt blue", 22);

        Map<String, Integer> everlightHarpMaterials = new HashMap<>();
        everlightHarpMaterials.put("Everlight silvthril", 30);
        everlightHarpMaterials.put("White oak", 22);

        Map<String, Integer> everlightTrumpetMaterials = new HashMap<>();
        everlightTrumpetMaterials.put("Everlight silvthril", 28);
        everlightTrumpetMaterials.put("Goldrune", 24);

        Map<String, Integer> everlightViolinMaterials = new HashMap<>();
        everlightViolinMaterials.put("Star of Saradomin", 16);
        everlightViolinMaterials.put("White oak", 20);
        everlightViolinMaterials.put("Samite silk", 16);

        Map<String, Integer> foldedArmFigurineFemaleMaterials = new HashMap<>();
        foldedArmFigurineFemaleMaterials.put("White marble", 30);
        foldedArmFigurineFemaleMaterials.put("Goldrune", 24);

        Map<String, Integer> foldedArmFigurineMaleMaterials = new HashMap<>();
        foldedArmFigurineMaleMaterials.put("White marble", 30);
        foldedArmFigurineMaleMaterials.put("Goldrune", 24);

        Map<String, Integer> inciteFearSpellScrollMaterials = new HashMap<>();
        inciteFearSpellScrollMaterials.put("Vellum", 20);
        inciteFearSpellScrollMaterials.put("Ancient vis", 18);
        inciteFearSpellScrollMaterials.put("Blood of Orcus", 18);

        Map<String, Integer> pontifexSignetRingMaterials = new HashMap<>();
        pontifexSignetRingMaterials.put("Third Age iron", 16);
        pontifexSignetRingMaterials.put("Goldrune", 18);
        pontifexSignetRingMaterials.put("Ancient vis", 22);
        pontifexSignetRingMaterials.put("Dragonstone", 1);

        Map<String, Integer> apexCapMaterials = new HashMap<>();
        apexCapMaterials.put("Samite silk", 28);
        apexCapMaterials.put("Leather scraps", 12);
        apexCapMaterials.put("Ancient vis", 20);

        Map<String, Integer> curseTabletMaterials = new HashMap<>();
        curseTabletMaterials.put("Imperial steel", 16);
        curseTabletMaterials.put("Zarosian insignia", 12);
        curseTabletMaterials.put("Soapstone", 20);
        curseTabletMaterials.put("Blood of Orcus", 12);

        Map<String, Integer> funeraryUrnOfShadowMaterials = new HashMap<>();
        funeraryUrnOfShadowMaterials.put("Soapstone", 26);
        funeraryUrnOfShadowMaterials.put("Tyrian purple", 14);
        funeraryUrnOfShadowMaterials.put("Ancient vis", 20);

        Map<String, Integer> dominionDiscusMaterials = new HashMap<>();
        dominionDiscusMaterials.put("Keramos", 34);
        dominionDiscusMaterials.put("Star of Saradomin", 28);

        Map<String, Integer> dominionJavelinMaterials = new HashMap<>();
        dominionJavelinMaterials.put("Keramos", 32);
        dominionJavelinMaterials.put("Third Age iron", 30);

        Map<String, Integer> dominionPelteShieldMaterials = new HashMap<>();
        dominionPelteShieldMaterials.put("Star of Saradomin", 34);
        dominionPelteShieldMaterials.put("Samite silk", 28);

        Map<String, Integer> infulaRobesMaterials = new HashMap<>();
        infulaRobesMaterials.put("Samite silk", 26);
        infulaRobesMaterials.put("Leather scraps", 12);
        infulaRobesMaterials.put("Goldrune", 12);
        infulaRobesMaterials.put("Tyrian purple", 12);

        Map<String, Integer> funeraryUrnOfSmokeMaterials = new HashMap<>();
        funeraryUrnOfSmokeMaterials.put("Soapstone", 28);
        funeraryUrnOfSmokeMaterials.put("Tyrian purple", 14);
        funeraryUrnOfSmokeMaterials.put("Ancient vis", 20);

        Map<String, Integer> handOfTheAncientsMaterials = new HashMap<>();
        handOfTheAncientsMaterials.put("Blood of Orcus", 12);
        handOfTheAncientsMaterials.put("White oak", 18);
        handOfTheAncientsMaterials.put("Ancient vis", 14);
        handOfTheAncientsMaterials.put("Goldrune", 18);

        Map<String, Integer> decorativeAmphoraMaterials = new HashMap<>();
        decorativeAmphoraMaterials.put("Tyrian purple", 16);
        decorativeAmphoraMaterials.put("Ancient vis", 18);
        decorativeAmphoraMaterials.put("Soapstone", 28);

        Map<String, Integer> funeraryUrnOfIceMaterials = new HashMap<>();
        funeraryUrnOfIceMaterials.put("Soapstone", 28);
        funeraryUrnOfIceMaterials.put("Tyrian purple", 14);
        funeraryUrnOfIceMaterials.put("Ancient vis", 20);

        Map<String, Integer> loarnabRodMaterials = new HashMap<>();
        loarnabRodMaterials.put("White oak", 28);
        loarnabRodMaterials.put("Blood of Orcus", 16);
        loarnabRodMaterials.put("Imperial steel", 18);

        Map<String, Integer> inquisitorsCeremonialArmourMaterials = new HashMap<>();
        inquisitorsCeremonialArmourMaterials.put("Leather scraps", 14);
        inquisitorsCeremonialArmourMaterials.put("Samite silk", 30);
        inquisitorsCeremonialArmourMaterials.put("Tyrian purple", 18);

        Map<String, Integer> inquisitorsCeremonialMaskMaterials = new HashMap<>();
        inquisitorsCeremonialMaskMaterials.put("Ancient vis", 14);
        inquisitorsCeremonialMaskMaterials.put("Leather scraps", 12);
        inquisitorsCeremonialMaskMaterials.put("Blood of Orcus", 14);
        inquisitorsCeremonialMaskMaterials.put("Samite silk", 22);

        Map<String, Integer> inquisitorsSealMaterials = new HashMap<>();
        inquisitorsSealMaterials.put("Tyrian purple", 14);
        inquisitorsSealMaterials.put("Zarosian insignia", 20);
        inquisitorsSealMaterials.put("Ancient vis", 14);
        inquisitorsSealMaterials.put("Goldrune", 14);

        Map<String, Integer> lakeOfFirePaintingMaterials = new HashMap<>();
        lakeOfFirePaintingMaterials.put("Samite silk", 10);
        lakeOfFirePaintingMaterials.put("White oak", 10);
        lakeOfFirePaintingMaterials.put("Vellum", 10);
        lakeOfFirePaintingMaterials.put("Cadmium red", 34);

        Map<String, Integer> lustMetalSculptureMaterials = new HashMap<>();
        lustMetalSculptureMaterials.put("Third Age iron", 16);
        lustMetalSculptureMaterials.put("Eye of Dagon", 24);
        lustMetalSculptureMaterials.put("Goldrune", 24);
        lustMetalSculptureMaterials.put("Ruby", 1);

        Map<String, Integer> gladiatorHelmetMaterials = new HashMap<>();
        gladiatorHelmetMaterials.put("Imperial steel", 30);
        gladiatorHelmetMaterials.put("Blood of Orcus", 16);
        gladiatorHelmetMaterials.put("Leather scraps", 18);

        Map<String, Integer> gladiatorSwordMaterials = new HashMap<>();
        gladiatorSwordMaterials.put("Imperial steel", 30);
        gladiatorSwordMaterials.put("Goldrune", 18);
        gladiatorSwordMaterials.put("Zarosian insignia", 16);

        Map<String, Integer> funeraryUrnOfBloodMaterials = new HashMap<>();
        funeraryUrnOfBloodMaterials.put("Soapstone", 30);
        funeraryUrnOfBloodMaterials.put("Tyrian purple", 14);
        funeraryUrnOfBloodMaterials.put("Blood of Orcus", 20);

        Map<String, Integer> funeraryUrnOfMiasmaMaterials = new HashMap<>();
        funeraryUrnOfMiasmaMaterials.put("Soapstone", 30);
        funeraryUrnOfMiasmaMaterials.put("Tyrian purple", 14);
        funeraryUrnOfMiasmaMaterials.put("Ancient vis", 20);

        Map<String, Integer> modelChariotMaterials = new HashMap<>();
        modelChariotMaterials.put("Vellum", 12);
        modelChariotMaterials.put("Imperial steel", 18);
        modelChariotMaterials.put("Goldrune", 20);
        modelChariotMaterials.put("Zarosian insignia", 14);

        Map<String, Integer> serpentsFallCarvingMaterials = new HashMap<>();
        serpentsFallCarvingMaterials.put("Vellum", 16);
        serpentsFallCarvingMaterials.put("Tyrian purple", 24);
        serpentsFallCarvingMaterials.put("Blood of Orcus", 12);
        serpentsFallCarvingMaterials.put("White oak", 12);

        Map<String, Integer> chaosStarMaterials = new HashMap<>();
        chaosStarMaterials.put("Chaotic brimstone", 28);
        chaosStarMaterials.put("Hellfire metal", 36);

        Map<String, Integer> spikedDogCollarMaterials = new HashMap<>();
        spikedDogCollarMaterials.put("Third Age iron", 24);
        spikedDogCollarMaterials.put("Leather scraps", 24);
        spikedDogCollarMaterials.put("Chaotic brimstone", 16);

        Map<String, Integer> bronzeDominionMedalMaterials = new HashMap<>();
        bronzeDominionMedalMaterials.put("Everlight silvthril", 36);
        bronzeDominionMedalMaterials.put("Star of Saradomin", 26);
        bronzeDominionMedalMaterials.put("Bronze bar", 1);

        Map<String, Integer> silverDominionMedalMaterials = new HashMap<>();
        silverDominionMedalMaterials.put("Everlight silvthril", 36);
        silverDominionMedalMaterials.put("Star of Saradomin", 26);
        silverDominionMedalMaterials.put("Silver bar", 1);

        Map<String, Integer> dominionTorchMaterials = new HashMap<>();
        dominionTorchMaterials.put("Goldrune", 12);
        dominionTorchMaterials.put("Orthenglass", 12);
        dominionTorchMaterials.put("Everlight silvthril", 20);
        dominionTorchMaterials.put("Star of Saradomin", 18);

        Map<String, Integer> ikovianGeregeMaterials = new HashMap<>();
        ikovianGeregeMaterials.put("Third Age iron", 36);
        ikovianGeregeMaterials.put("Wings of War", 30);

        Map<String, Integer> toyGliderMaterials = new HashMap<>();
        toyGliderMaterials.put("Stormguard steel", 36);
        toyGliderMaterials.put("White oak", 30);

        Map<String, Integer> toyWarGolemMaterials = new HashMap<>();
        toyWarGolemMaterials.put("Third Age iron", 36);
        toyWarGolemMaterials.put("White oak", 30);
        toyWarGolemMaterials.put("Clockwork", 1);

        Map<String, Integer> ceremonialDragonkinDeviceMaterials = new HashMap<>();
        ceremonialDragonkinDeviceMaterials.put("Orthenglass", 66);

        Map<String, Integer> decorativeVaseMaterials = new HashMap<>();
        decorativeVaseMaterials.put("White marble", 36);
        decorativeVaseMaterials.put("Cobalt blue", 30);

        Map<String, Integer> kantharosCupMaterials = new HashMap<>();
        kantharosCupMaterials.put("Everlight silvthril", 30);
        kantharosCupMaterials.put("Orthenglass", 36);
        kantharosCupMaterials.put("Sapphire", 1);

        Map<String, Integer> pateraBowlMaterials = new HashMap<>();
        pateraBowlMaterials.put("Keramos", 36);
        pateraBowlMaterials.put("Goldrune", 30);
        pateraBowlMaterials.put("Sapphire", 1);

        Map<String, Integer> castleGatestoneMaterials = new HashMap<>();
        castleGatestoneMaterials.put("Orgone", 36);
        castleGatestoneMaterials.put("Orthenglass", 32);

        Map<String, Integer> engravedRingOfKinshipMaterials = new HashMap<>();
        engravedRingOfKinshipMaterials.put("Goldrune", 28);
        engravedRingOfKinshipMaterials.put("Dragon metal", 40);

        Map<String, Integer> ceremonialMaceMaterials = new HashMap<>();
        ceremonialMaceMaterials.put("Imperial steel", 20);
        ceremonialMaceMaterials.put("Third Age iron", 20);
        ceremonialMaceMaterials.put("Goldrune", 28);

        Map<String, Integer> pontifexMaximusFigurineMaterials = new HashMap<>();
        pontifexMaximusFigurineMaterials.put("Zarosian insignia", 24);
        pontifexMaximusFigurineMaterials.put("Ancient vis", 16);
        pontifexMaximusFigurineMaterials.put("Goldrune", 28);
        pontifexMaximusFigurineMaterials.put("Dragonstone", 1);

        Map<String, Integer> consensusAdIdemPaintingMaterials = new HashMap<>();
        consensusAdIdemPaintingMaterials.put("White oak", 10);
        consensusAdIdemPaintingMaterials.put("Samite silk", 10);
        consensusAdIdemPaintingMaterials.put("Tyrian purple", 50);

        Map<String, Integer> wingsuitV1Materials = new HashMap<>();
        wingsuitV1Materials.put("Samite silk", 40);
        wingsuitV1Materials.put("Leather scraps", 20);
        wingsuitV1Materials.put("Stormguard steel", 9);
        wingsuitV1Materials.put("Armadylean yellow", 1);

        Map<String, Integer> avianSongEggPlayerMaterials = new HashMap<>();
        avianSongEggPlayerMaterials.put("Stormguard steel", 36);
        avianSongEggPlayerMaterials.put("Armadylean yellow", 32);
        avianSongEggPlayerMaterials.put("Diamond", 1);

        Map<String, Integer> keshikDrumMaterials = new HashMap<>();
        keshikDrumMaterials.put("Wings of War", 16);
        keshikDrumMaterials.put("Animal furs", 16);
        keshikDrumMaterials.put("White oak", 20);
        keshikDrumMaterials.put("Leather scraps", 16);

        Map<String, Integer> morinKhuurMaterials = new HashMap<>();
        morinKhuurMaterials.put("Armadylean yellow", 36);
        morinKhuurMaterials.put("White oak", 32);

        Map<String, Integer> ekeleshuunBlinderMaskMaterials = new HashMap<>();
        ekeleshuunBlinderMaskMaterials.put("Vulcanised rubber", 24);
        ekeleshuunBlinderMaskMaterials.put("Malachite green", 20);
        ekeleshuunBlinderMaskMaterials.put("Vellum", 24);

        Map<String, Integer> narogoshuunHobDaGobBallMaterials = new HashMap<>();
        narogoshuunHobDaGobBallMaterials.put("Vulcanised rubber", 36);
        narogoshuunHobDaGobBallMaterials.put("Mark of the Kyzaj", 32);

        Map<String, Integer> rekeshuunWarTetherMaterials = new HashMap<>();
        rekeshuunWarTetherMaterials.put("Warforged bronze", 20);
        rekeshuunWarTetherMaterials.put("Vulcanised rubber", 22);
        rekeshuunWarTetherMaterials.put("Leather scraps", 26);

        Map<String, Integer> exploratoryTotemMaterials = new HashMap<>();
        exploratoryTotemMaterials.put("Dragon metal", 34);
        exploratoryTotemMaterials.put("Compass rose", 36);

        Map<String, Integer> excavatorPortalMineMaterials = new HashMap<>();
        excavatorPortalMineMaterials.put("Orgone", 36);
        excavatorPortalMineMaterials.put("Orthenglass", 34);

        Map<String, Integer> storageTotemMaterials = new HashMap<>();
        storageTotemMaterials.put("Dragon metal", 36);
        storageTotemMaterials.put("Compass rose", 34);

        Map<String, Integer> plantSeedSatchelMaterials = new HashMap<>();
        plantSeedSatchelMaterials.put("Felt", 28);
        plantSeedSatchelMaterials.put("Carbon black", 28);
        plantSeedSatchelMaterials.put("Compass rose", 14);

        Map<String, Integer> snuffBoxMaterials = new HashMap<>();
        snuffBoxMaterials.put("Soapstone", 20);
        snuffBoxMaterials.put("Carbon black", 30);
        snuffBoxMaterials.put("Felt", 20);

        Map<String, Integer> funeraryUrnMaterials = new HashMap<>();
        funeraryUrnMaterials.put("Soapstone", 30);
        funeraryUrnMaterials.put("Tyrian purple", 20);
        funeraryUrnMaterials.put("Ancient vis", 24);

        Map<String, Integer> aviansieDreamcoatMaterials = new HashMap<>();
        aviansieDreamcoatMaterials.put("Armadylean yellow", 20);
        aviansieDreamcoatMaterials.put("Samite silk", 30);
        aviansieDreamcoatMaterials.put("Animal furs", 22);

        Map<String, Integer> ceremonialPlumeMaterials = new HashMap<>();
        ceremonialPlumeMaterials.put("Armadylean yellow", 38);
        ceremonialPlumeMaterials.put("Goldrune", 34);
        ceremonialPlumeMaterials.put("Phoenix feather", 1);

        Map<String, Integer> peacockingParasolMaterials = new HashMap<>();
        peacockingParasolMaterials.put("Armadylean yellow", 22);
        peacockingParasolMaterials.put("Samite silk", 30);
        peacockingParasolMaterials.put("White oak", 20);

        Map<String, Integer> ogreKyzajAxeMaterials = new HashMap<>();
        ogreKyzajAxeMaterials.put("Warforged bronze", 28);
        ogreKyzajAxeMaterials.put("Mark of the Kyzaj", 20);
        ogreKyzajAxeMaterials.put("Fossilised bone", 24);

        Map<String, Integer> orkCleaverSwordMaterials = new HashMap<>();
        orkCleaverSwordMaterials.put("Warforged bronze", 36);
        orkCleaverSwordMaterials.put("Fossilised bone", 36);

        Map<String, Integer> larupiaTrophyMaterials = new HashMap<>();
        larupiaTrophyMaterials.put("Cadmium red", 18);
        larupiaTrophyMaterials.put("Animal furs", 28);
        larupiaTrophyMaterials.put("Orthenglass", 26);

        Map<String, Integer> lionTrophyMaterials = new HashMap<>();
        lionTrophyMaterials.put("Cadmium red", 18);
        lionTrophyMaterials.put("Animal furs", 28);
        lionTrophyMaterials.put("White oak", 26);

        Map<String, Integer> sheWolfTrophyMaterials = new HashMap<>();
        sheWolfTrophyMaterials.put("Chaotic brimstone", 26);
        sheWolfTrophyMaterials.put("Cadmium red", 18);
        sheWolfTrophyMaterials.put("Animal furs", 28);

        Map<String, Integer> pontifexCenserMaterials = new HashMap<>();
        pontifexCenserMaterials.put("Third Age iron", 20);
        pontifexCenserMaterials.put("Ancient vis", 20);
        pontifexCenserMaterials.put("Goldrune", 32);
        pontifexCenserMaterials.put("Dragonstone", 1);

        Map<String, Integer> pontifexCrozierMaterials = new HashMap<>();
        pontifexCrozierMaterials.put("Imperial steel", 20);
        pontifexCrozierMaterials.put("Zarosian insignia", 20);
        pontifexCrozierMaterials.put("Goldrune", 32);

        Map<String, Integer> pontifexMitreMaterials = new HashMap<>();
        pontifexMitreMaterials.put("Samite silk", 32);
        pontifexMitreMaterials.put("Ancient vis", 20);
        pontifexMitreMaterials.put("Zarosian insignia", 20);

        Map<String, Integer> thorobshuunBattleStandardMaterials = new HashMap<>();
        thorobshuunBattleStandardMaterials.put("Mark of the Kyzaj", 16);
        thorobshuunBattleStandardMaterials.put("Malachite green", 22);
        thorobshuunBattleStandardMaterials.put("White oak", 16);
        thorobshuunBattleStandardMaterials.put("Samite silk", 20);

        Map<String, Integer> yurkolgokhStinkGrenadeMaterials = new HashMap<>();
        yurkolgokhStinkGrenadeMaterials.put("Yu'biusk clay", 38);
        yurkolgokhStinkGrenadeMaterials.put("Vulcanised rubber", 36);
        yurkolgokhStinkGrenadeMaterials.put("Weapon poison (3)", 1);

        Map<String, Integer> dominarianDeviceMaterials = new HashMap<>();
        dominarianDeviceMaterials.put("Everlight silvthril", 30);
        dominarianDeviceMaterials.put("Keramos", 22);
        dominarianDeviceMaterials.put("Third Age iron", 22);
        dominarianDeviceMaterials.put("Clockwork", 1);

        Map<String, Integer> fishingTridentMaterials = new HashMap<>();
        fishingTridentMaterials.put("Star of Saradomin", 22);
        fishingTridentMaterials.put("Third Age iron", 30);
        fishingTridentMaterials.put("Goldrune", 22);

        Map<String, Integer> hawkeyeLensMultiVisionScopeMaterials = new HashMap<>();
        hawkeyeLensMultiVisionScopeMaterials.put("Stormguard steel", 40);
        hawkeyeLensMultiVisionScopeMaterials.put("Orthenglass", 34);

        Map<String, Integer> talon3RazorWingMaterials = new HashMap<>();
        talon3RazorWingMaterials.put("Aetherium alloy", 40);
        talon3RazorWingMaterials.put("Wings of War", 34);
        talon3RazorWingMaterials.put("Rope", 1);

        Map<String, Integer> exsanguinateSpellScrollMaterials = new HashMap<>();
        exsanguinateSpellScrollMaterials.put("Vellum", 40);
        exsanguinateSpellScrollMaterials.put("Blood of Orcus", 36);

        Map<String, Integer> necromanticFocusMaterials = new HashMap<>();
        necromanticFocusMaterials.put("Imperial steel", 20);
        necromanticFocusMaterials.put("Blood of Orcus", 26);
        necromanticFocusMaterials.put("Ancient vis", 30);

        Map<String, Integer> spentSummoningCharmMaterials = new HashMap<>();
        spentSummoningCharmMaterials.put("Orgone", 46);
        spentSummoningCharmMaterials.put("Felt", 30);

        Map<String, Integer> friendshipBraceletMaterials = new HashMap<>();
        friendshipBraceletMaterials.put("Soapstone", 30);
        friendshipBraceletMaterials.put("Felt", 46);

        Map<String, Integer> homelyTotemMaterials = new HashMap<>();
        homelyTotemMaterials.put("Soapstone", 36);
        homelyTotemMaterials.put("Felt", 40);

        Map<String, Integer> highPriestCrozierMaterials = new HashMap<>();
        highPriestCrozierMaterials.put("Mark of the Kyzaj", 26);
        highPriestCrozierMaterials.put("Malachite green", 24);
        highPriestCrozierMaterials.put("Goldrune", 28);

        Map<String, Integer> highPriestMitreMaterials = new HashMap<>();
        highPriestMitreMaterials.put("Mark of the Kyzaj", 26);
        highPriestMitreMaterials.put("Malachite green", 24);
        highPriestMitreMaterials.put("Samite silk", 28);

        Map<String, Integer> highPriestOrbMaterials = new HashMap<>();
        highPriestOrbMaterials.put("Mark of the Kyzaj", 26);
        highPriestOrbMaterials.put("Malachite green", 24);
        highPriestOrbMaterials.put("Goldrune", 28);

        Map<String, Integer> tormentMetalSculptureMaterials = new HashMap<>();
        tormentMetalSculptureMaterials.put("Eye of Dagon", 20);
        tormentMetalSculptureMaterials.put("Third Age iron", 20);
        tormentMetalSculptureMaterials.put("Hellfire metal", 38);

        Map<String, Integer> pandemoniumTapestryMaterials = new HashMap<>();
        pandemoniumTapestryMaterials.put("White oak", 12);
        pandemoniumTapestryMaterials.put("Samite silk", 12);
        pandemoniumTapestryMaterials.put("Vellum", 12);
        pandemoniumTapestryMaterials.put("Cadmium red", 42);

        Map<String, Integer> ceremonialDragonkinTabletMaterials = new HashMap<>();
        ceremonialDragonkinTabletMaterials.put("Orthenglass", 79);

        Map<String, Integer> pasahaMaterials = new HashMap<>();
        pasahaMaterials.put("Felt", 40);
        pasahaMaterials.put("Goldrune", 38);

        Map<String, Integer> ritualBellMaterials = new HashMap<>();
        ritualBellMaterials.put("Goldrune", 40);
        ritualBellMaterials.put("Compass rose", 38);

        Map<String, Integer> prototypeGravimeterMaterials = new HashMap<>();
        prototypeGravimeterMaterials.put("Quintessence", 34);
        prototypeGravimeterMaterials.put("Leather scraps", 20);
        prototypeGravimeterMaterials.put("Third Age iron", 26);

        Map<String, Integer> songbirdRecorderMaterials = new HashMap<>();
        songbirdRecorderMaterials.put("Stormguard steel", 44);
        songbirdRecorderMaterials.put("Orthenglass", 36);
        songbirdRecorderMaterials.put("Diamond", 1);

        Map<String, Integer> amphoraMaterials = new HashMap<>();
        amphoraMaterials.put("Everlight silvthril", 34);
        amphoraMaterials.put("Keramos", 46);

        Map<String, Integer> rodOfAsclepiusMaterials = new HashMap<>();
        rodOfAsclepiusMaterials.put("White marble", 30);
        rodOfAsclepiusMaterials.put("Star of Saradomin", 24);
        rodOfAsclepiusMaterials.put("Goldrune", 26);

        Map<String, Integer> zarosianEwerMaterials = new HashMap<>();
        zarosianEwerMaterials.put("Third Age iron", 52);
        zarosianEwerMaterials.put("Zarosian insignia", 30);

        Map<String, Integer> zarosianSteinMaterials = new HashMap<>();
        zarosianSteinMaterials.put("Third Age iron", 16);
        zarosianSteinMaterials.put("Imperial steel", 36);
        zarosianSteinMaterials.put("Zarosian insignia", 30);

        Map<String, Integer> beastkeeperHelmMaterials = new HashMap<>();
        beastkeeperHelmMaterials.put("Warforged bronze", 16);
        beastkeeperHelmMaterials.put("Vulcanised rubber", 24);
        beastkeeperHelmMaterials.put("Animal furs", 20);
        beastkeeperHelmMaterials.put("Fossilised bone", 24);

        Map<String, Integer> idithuunHornRingMaterials = new HashMap<>();
        idithuunHornRingMaterials.put("Yu'biusk clay", 40);
        idithuunHornRingMaterials.put("Vulcanised rubber", 44);

        Map<String, Integer> nosorogSculptureMaterials = new HashMap<>();
        nosorogSculptureMaterials.put("Yu'biusk clay", 30);
        nosorogSculptureMaterials.put("Malachite green", 24);
        nosorogSculptureMaterials.put("Warforged bronze", 30);

        Map<String, Integer> dayguardShieldMaterials = new HashMap<>();
        dayguardShieldMaterials.put("Stormguard steel", 36);
        dayguardShieldMaterials.put("Wings of War", 28);
        dayguardShieldMaterials.put("White oak", 20);

        Map<String, Integer> stormguardGeregeMaterials = new HashMap<>();
        stormguardGeregeMaterials.put("Stormguard steel", 36);
        stormguardGeregeMaterials.put("Wings of War", 28);
        stormguardGeregeMaterials.put("Goldrune", 20);

        Map<String, Integer> kilayaMaterials = new HashMap<>();
        kilayaMaterials.put("Dragon metal", 46);
        kilayaMaterials.put("Compass rose", 40);

        Map<String, Integer> vazaraMaterials = new HashMap<>();
        vazaraMaterials.put("Dragon metal", 30);
        vazaraMaterials.put("Compass rose", 28);
        vazaraMaterials.put("Goldrune", 28);

        Map<String, Integer> ourgMegahitterMaterials = new HashMap<>();
        ourgMegahitterMaterials.put("White oak", 20);
        ourgMegahitterMaterials.put("Leather scraps", 20);
        ourgMegahitterMaterials.put("Orthenglass", 26);
        ourgMegahitterMaterials.put("Malachite green", 22);

        Map<String, Integer> ourgTowerShieldMaterials = new HashMap<>();
        ourgTowerShieldMaterials.put("Mark of the Kyzaj", 20);
        ourgTowerShieldMaterials.put("Third Age iron", 26);
        ourgTowerShieldMaterials.put("Leather scraps", 22);
        ourgTowerShieldMaterials.put("White oak", 20);

        Map<String, Integer> garagorshuunAnchorMaterials = new HashMap<>();
        garagorshuunAnchorMaterials.put("Warforged bronze", 32);
        garagorshuunAnchorMaterials.put("Mark of the Kyzaj", 26);
        garagorshuunAnchorMaterials.put("Third Age iron", 30);

        Map<String, Integer> golemHeartMaterials = new HashMap<>();
        golemHeartMaterials.put("Aetherium alloy", 34);
        golemHeartMaterials.put("Quintessence", 24);
        golemHeartMaterials.put("Orthenglass", 16);
        golemHeartMaterials.put("Soapstone", 16);

        Map<String, Integer> golemInstructionMaterials = new HashMap<>();
        golemInstructionMaterials.put("Quintessence", 46);
        golemInstructionMaterials.put("Vellum", 44);
        golemInstructionMaterials.put("Black mushroom ink", 1);

        Map<String, Integer> hellfireHaladieMaterials = new HashMap<>();
        hellfireHaladieMaterials.put("Hellfire metal", 44);
        hellfireHaladieMaterials.put("Third Age iron", 26);
        hellfireHaladieMaterials.put("Leather scraps", 20);

        Map<String, Integer> hellfireKatarMaterials = new HashMap<>();
        hellfireKatarMaterials.put("Hellfire metal", 50);
        hellfireKatarMaterials.put("Leather scraps", 40);

        Map<String, Integer> hellfireZaghnalMaterials = new HashMap<>();
        hellfireZaghnalMaterials.put("Hellfire metal", 38);
        hellfireZaghnalMaterials.put("White oak", 26);
        hellfireZaghnalMaterials.put("Orthenglass", 26);

        Map<String, Integer> deathMaskMaterials = new HashMap<>();
        deathMaskMaterials.put("Orgone", 56);
        deathMaskMaterials.put("Soapstone", 34);

        Map<String, Integer> dragonkinCalendarMaterials = new HashMap<>();
        dragonkinCalendarMaterials.put("Orgone", 34);
        dragonkinCalendarMaterials.put("Carbon black", 28);
        dragonkinCalendarMaterials.put("Compass rose", 28);

        Map<String, Integer> dragonkinStaffMaterials = new HashMap<>();
        dragonkinStaffMaterials.put("Orgone", 56);
        dragonkinStaffMaterials.put("Compass rose", 34);


        Map<String, Integer> dorgeshuunSpearMaterials = new HashMap<>();
        dorgeshuunSpearMaterials.put("Warforged bronze", 50);
        dorgeshuunSpearMaterials.put("White oak", 42);

        Map<String, Integer> forgedInWarSculptureMaterials = new HashMap<>();
        forgedInWarSculptureMaterials.put("Warforged bronze", 50);
        forgedInWarSculptureMaterials.put("Yu'biusk clay", 42);
        forgedInWarSculptureMaterials.put("Emerald", 1);

        Map<String, Integer> kopisDaggerMaterials = new HashMap<>();
        kopisDaggerMaterials.put("Everlight silvthril", 50);
        kopisDaggerMaterials.put("Leather scraps", 42);

        Map<String, Integer> xiphosShortSwordMaterials = new HashMap<>();
        xiphosShortSwordMaterials.put("Everlight silvthril", 46);
        xiphosShortSwordMaterials.put("Leather scraps", 46);

        Map<String, Integer> smokeCloudSpellScrollMaterials = new HashMap<>();
        smokeCloudSpellScrollMaterials.put("Vellum", 40);
        smokeCloudSpellScrollMaterials.put("Ancient vis", 20);
        smokeCloudSpellScrollMaterials.put("Blood of Orcus", 32);

        Map<String, Integer> vigoremVialMaterials = new HashMap<>();
        vigoremVialMaterials.put("Imperial steel", 54);
        vigoremVialMaterials.put("Ancient vis", 38);
        vigoremVialMaterials.put("Molten glass", 1);

        Map<String, Integer> dragonScalpelMaterials = new HashMap<>();
        dragonScalpelMaterials.put("Dragon metal", 52);
        dragonScalpelMaterials.put("Felt", 42);

        Map<String, Integer> protectiveGogglesMaterials = new HashMap<>();
        protectiveGogglesMaterials.put("Felt", 42);
        protectiveGogglesMaterials.put("Orthenglass", 52);

        Map<String, Integer> dragonBurnerMaterials = new HashMap<>();
        dragonBurnerMaterials.put("Dragon metal", 52);
        dragonBurnerMaterials.put("Orgone", 42);

        Map<String, Integer> orthenglassFlaskMaterials = new HashMap<>();
        orthenglassFlaskMaterials.put("Dragon metal", 34);
        orthenglassFlaskMaterials.put("Orthenglass", 60);

        Map<String, Integer> blackfireLanceMaterials = new HashMap<>();
        blackfireLanceMaterials.put("Aetherium alloy", 50);
        blackfireLanceMaterials.put("Quintessence", 46);

        Map<String, Integer> nightguardShieldMaterials = new HashMap<>();
        nightguardShieldMaterials.put("Stormguard steel", 30);
        nightguardShieldMaterials.put("Wings of War", 36);
        nightguardShieldMaterials.put("White oak", 30);

        Map<String, Integer> projectionAttunerMaterials = new HashMap<>();
        projectionAttunerMaterials.put("Goldrune", 28);
        projectionAttunerMaterials.put("Orthenglass", 28);
        projectionAttunerMaterials.put("Dragon metal", 40);

        Map<String, Integer> goldenProjectionNeedleMaterials = new HashMap<>();
        goldenProjectionNeedleMaterials.put("Goldrune", 36);
        goldenProjectionNeedleMaterials.put("Dragon metal", 60);

        Map<String, Integer> huzamogaarbChaosCrownMaterials = new HashMap<>();
        huzamogaarbChaosCrownMaterials.put("Warforged bronze", 44);
        huzamogaarbChaosCrownMaterials.put("Third Age iron", 34);
        huzamogaarbChaosCrownMaterials.put("Eye of Dagon", 20);

        Map<String, Integer> saragorgakStarCrownMaterials = new HashMap<>();
        saragorgakStarCrownMaterials.put("Warforged bronze", 44);
        saragorgakStarCrownMaterials.put("Third Age iron", 34);
        saragorgakStarCrownMaterials.put("Star of Saradomin", 20);

        Map<String, Integer> possessionMetalSculptureMaterials = new HashMap<>();
        possessionMetalSculptureMaterials.put("Eye of Dagon", 24);
        possessionMetalSculptureMaterials.put("Chaotic brimstone", 30);
        possessionMetalSculptureMaterials.put("Third Age iron", 44);

        Map<String, Integer> trishulaMaterials = new HashMap<>();
        trishulaMaterials.put("Hellfire metal", 48);
        trishulaMaterials.put("Eye of Dagon", 30);
        trishulaMaterials.put("Third Age iron", 20);

        Map<String, Integer> tsutsarothPiercingMaterials = new HashMap<>();
        tsutsarothPiercingMaterials.put("Hellfire metal", 44);
        tsutsarothPiercingMaterials.put("Chaotic brimstone", 30);
        tsutsarothPiercingMaterials.put("Cadmium red", 24);

        Map<String, Integer> hallowedBeTheEverlightPaintingMaterials = new HashMap<>();
        hallowedBeTheEverlightPaintingMaterials.put("Cobalt blue", 52);
        hallowedBeTheEverlightPaintingMaterials.put("White oak", 16);
        hallowedBeTheEverlightPaintingMaterials.put("Samite silk", 16);
        hallowedBeTheEverlightPaintingMaterials.put("Vellum", 16);

        Map<String, Integer> theLordOfLightPaintingMaterials = new HashMap<>();
        theLordOfLightPaintingMaterials.put("Cobalt blue", 52);
        theLordOfLightPaintingMaterials.put("White oak", 16);
        theLordOfLightPaintingMaterials.put("Samite silk", 16);
        theLordOfLightPaintingMaterials.put("Vellum", 16);

        Map<String, Integer> thePrideOfPadosanPaintingMaterials = new HashMap<>();
        thePrideOfPadosanPaintingMaterials.put("Cobalt blue", 52);
        thePrideOfPadosanPaintingMaterials.put("White oak", 16);
        thePrideOfPadosanPaintingMaterials.put("Samite silk", 16);
        thePrideOfPadosanPaintingMaterials.put("Vellum", 16);

        Map<String, Integer> meditationPipeMaterials = new HashMap<>();
        meditationPipeMaterials.put("Orgone", 60);
        meditationPipeMaterials.put("Dragon metal", 40);

        Map<String, Integer> personalTotemMaterials = new HashMap<>();
        personalTotemMaterials.put("Orgone", 48);
        personalTotemMaterials.put("Carbon black", 26);
        personalTotemMaterials.put("Compass rose", 26);

        Map<String, Integer> singingBowlMaterials = new HashMap<>();
        singingBowlMaterials.put("Orgone", 60);
        singingBowlMaterials.put("Dragon metal", 40);

        Map<String, Integer> ancientMagicTabletMaterials = new HashMap<>();
        ancientMagicTabletMaterials.put("Ancient vis", 40);
        ancientMagicTabletMaterials.put("Blood of Orcus", 64);

        Map<String, Integer> animateDeadSpellScrollMaterials = new HashMap<>();
        animateDeadSpellScrollMaterials.put("Vellum", 40);
        animateDeadSpellScrollMaterials.put("Ancient vis", 24);
        animateDeadSpellScrollMaterials.put("Blood of Orcus", 40);

        Map<String, Integer> portablePhylacteryMaterials = new HashMap<>();
        portablePhylacteryMaterials.put("Imperial steel", 48);
        portablePhylacteryMaterials.put("Blood of Orcus", 36);
        portablePhylacteryMaterials.put("Ancient vis", 20);

        Map<String, Integer> comfortGatestoneMaterials = new HashMap<>();
        comfortGatestoneMaterials.put("Orthenglass", 40);
        comfortGatestoneMaterials.put("Orgone", 34);
        comfortGatestoneMaterials.put("Compass rose", 30);

        Map<String, Integer> halaksCubeMaterials = new HashMap<>();
        halaksCubeMaterials.put("Compass rose", 36);
        halaksCubeMaterials.put("Carbon black", 40);
        halaksCubeMaterials.put("Orgone", 28);

        Map<String, Integer> lingamStoneMaterials = new HashMap<>();
        lingamStoneMaterials.put("Orgone", 44);
        lingamStoneMaterials.put("Carbon black", 30);
        lingamStoneMaterials.put("Compass rose", 32);

        Map<String, Integer> masterControlMaterials = new HashMap<>();
        masterControlMaterials.put("Orgone", 30);
        masterControlMaterials.put("Carbon black", 32);
        masterControlMaterials.put("Compass rose", 44);

        Map<String, Integer> theEnlightenedSoulScrollMaterials = new HashMap<>();
        theEnlightenedSoulScrollMaterials.put("Star of Saradomin", 50);
        theEnlightenedSoulScrollMaterials.put("Vellum", 60);

        Map<String, Integer> theEudoxianElementsTabletMaterials = new HashMap<>();
        theEudoxianElementsTabletMaterials.put("White marble", 60);
        theEudoxianElementsTabletMaterials.put("Goldrune", 50);

        Map<String, Integer> drogokishuunHookSwordMaterials = new HashMap<>();
        drogokishuunHookSwordMaterials.put("Warforged bronze", 44);
        drogokishuunHookSwordMaterials.put("Malachite green", 36);
        drogokishuunHookSwordMaterials.put("Fossilised bone", 32);

        Map<String, Integer> hobgoblinManstickerMaterials = new HashMap<>();
        hobgoblinManstickerMaterials.put("Warforged bronze", 66);
        hobgoblinManstickerMaterials.put("Fossilised bone", 46);

        Map<String, Integer> chaosElementalTrophyMaterials = new HashMap<>();
        chaosElementalTrophyMaterials.put("Chaotic brimstone", 52);
        chaosElementalTrophyMaterials.put("White oak", 30);
        chaosElementalTrophyMaterials.put("Hellfire metal", 30);

        Map<String, Integer> viriusTrophyMaterials = new HashMap<>();
        viriusTrophyMaterials.put("Demonhide", 44);
        viriusTrophyMaterials.put("White oak", 34);
        viriusTrophyMaterials.put("Orthenglass", 34);

        Map<String, Integer> flatCapMaterials = new HashMap<>();
        flatCapMaterials.put("Armadylean yellow", 60);
        flatCapMaterials.put("Samite silk", 54);

        Map<String, Integer> nightOwlFlightGogglesMaterials = new HashMap<>();
        nightOwlFlightGogglesMaterials.put("Armadylean yellow", 44);
        nightOwlFlightGogglesMaterials.put("Leather scraps", 40);
        nightOwlFlightGogglesMaterials.put("Orthenglass", 30);

        Map<String, Integer> prototypeGodbowMaterials = new HashMap<>();
        prototypeGodbowMaterials.put("Aetherium alloy", 50);
        prototypeGodbowMaterials.put("Quintessence", 34);
        prototypeGodbowMaterials.put("Wings of War", 34);

        Map<String, Integer> prototypeGodstaffMaterials = new HashMap<>();
        prototypeGodstaffMaterials.put("Aetherium alloy", 50);
        prototypeGodstaffMaterials.put("Quintessence", 34);
        prototypeGodstaffMaterials.put("Wings of War", 34);

        Map<String, Integer> prototypeGodswordMaterials = new HashMap<>();
        prototypeGodswordMaterials.put("Aetherium alloy", 50);
        prototypeGodswordMaterials.put("Wings of War", 34);
        prototypeGodswordMaterials.put("Goldrune", 34);

        Map<String, Integer> xoloHardHatMaterials = new HashMap<>();
        xoloHardHatMaterials.put("Goldrune", 54);
        xoloHardHatMaterials.put("Dragon metal", 66);

        Map<String, Integer> xoloPickaxeMaterials = new HashMap<>();
        xoloPickaxeMaterials.put("Goldrune", 36);
        xoloPickaxeMaterials.put("Dragon metal", 50);
        xoloPickaxeMaterials.put("Orgone", 34);

        Map<String, Integer> portablePortalGeneratorMaterials = new HashMap<>();
        portablePortalGeneratorMaterials.put("Dragon metal", 50);
        portablePortalGeneratorMaterials.put("Orthenglass", 36);
        portablePortalGeneratorMaterials.put("Goldrune", 34);

        Map<String, Integer> warpedTrinketMaterials = new HashMap<>();
        warpedTrinketMaterials.put("Dragon metal", 30);
        warpedTrinketMaterials.put("Orthenglass", 30);
        warpedTrinketMaterials.put("Soapstone", 30);
        warpedTrinketMaterials.put("Goldrune", 30);

        Map<String, Integer> praetorianHoodMaterials = new HashMap<>();
        praetorianHoodMaterials.put("Ancient vis", 36);
        praetorianHoodMaterials.put("Samite silk", 48);
        praetorianHoodMaterials.put("Zarosian insignia", 40);
        praetorianHoodMaterials.put("Death rune", 30);

        Map<String, Integer> praetorianRobesMaterials = new HashMap<>();
        praetorianRobesMaterials.put("Ancient vis", 30);
        praetorianRobesMaterials.put("Samite silk", 54);
        praetorianRobesMaterials.put("Zarosian insignia", 40);
        praetorianRobesMaterials.put("Death rune", 50);

        Map<String, Integer> praetorianStaffMaterials = new HashMap<>();
        praetorianStaffMaterials.put("Imperial steel", 36);
        praetorianStaffMaterials.put("Ancient vis", 58);
        praetorianStaffMaterials.put("Zarosian insignia", 30);
        praetorianStaffMaterials.put("Death rune", 100);

        Map<String, Integer> kalikraChieftainCrownMaterials = new HashMap<>();
        kalikraChieftainCrownMaterials.put("Yu'biusk clay", 66);
        kalikraChieftainCrownMaterials.put("Animal furs", 60);

        Map<String, Integer> kalikraMaceMaterials = new HashMap<>();
        kalikraMaceMaterials.put("Vulcanised rubber", 42);
        kalikraMaceMaterials.put("Third Age iron", 44);
        kalikraMaceMaterials.put("Fossilised bone", 40);

        Map<String, Integer> kalikraWarhornMaterials = new HashMap<>();
        kalikraWarhornMaterials.put("Vulcanised rubber", 44);
        kalikraWarhornMaterials.put("Fossilised bone", 42);
        kalikraWarhornMaterials.put("Animal furs", 40);

        Map<String, Integer> tsutsarothHelmMaterials = new HashMap<>();
        tsutsarothHelmMaterials.put("Hellfire metal", 50);
        tsutsarothHelmMaterials.put("Eye of Dagon", 40);
        tsutsarothHelmMaterials.put("Goldrune", 40);

        Map<String, Integer> tsutsarothPauldronMaterials = new HashMap<>();
        tsutsarothPauldronMaterials.put("Hellfire metal", 40);
        tsutsarothPauldronMaterials.put("Goldrune", 50);
        tsutsarothPauldronMaterials.put("Eye of Dagon", 40);

        Map<String, Integer> tsutsarothUrumiMaterials = new HashMap<>();
        tsutsarothUrumiMaterials.put("Hellfire metal", 50);
        tsutsarothUrumiMaterials.put("Eye of Dagon", 40);
        tsutsarothUrumiMaterials.put("Third Age iron", 40);

        Map<String, Integer> doruSpearMaterials = new HashMap<>();
        doruSpearMaterials.put("Everlight silvthril", 70);
        doruSpearMaterials.put("White oak", 62);

        Map<String, Integer> kontosLanceMaterials = new HashMap<>();
        kontosLanceMaterials.put("Everlight silvthril", 70);
        kontosLanceMaterials.put("Samite silk", 62);

        Map<String, Integer> chuluuStoneMaterials = new HashMap<>();
        chuluuStoneMaterials.put("Aetherium alloy", 40);
        chuluuStoneMaterials.put("Quintessence", 30);
        chuluuStoneMaterials.put("Soapstone", 40);
        chuluuStoneMaterials.put("Goldrune", 24);

        Map<String, Integer> quintessenceCounterMaterials = new HashMap<>();
        quintessenceCounterMaterials.put("Quintessence", 54);
        quintessenceCounterMaterials.put("Stormguard steel", 40);
        quintessenceCounterMaterials.put("White oak", 40);

        Map<String, Integer> sphericalAstrolabeMaterials = new HashMap<>();
        sphericalAstrolabeMaterials.put("Aetherium alloy", 46);
        sphericalAstrolabeMaterials.put("Armadylean yellow", 40);
        sphericalAstrolabeMaterials.put("Orthenglass", 48);

        Map<String, Integer> ancientGlobeMaterials = new HashMap<>();
        ancientGlobeMaterials.put("White oak", 20);
        ancientGlobeMaterials.put("Tyrian purple", 54);
        ancientGlobeMaterials.put("Ancient vis", 60);

        Map<String, Integer> battlePlansMaterials = new HashMap<>();
        battlePlansMaterials.put("Vellum", 40);
        battlePlansMaterials.put("Tyrian purple", 60);
        battlePlansMaterials.put("Ancient vis", 34);

        Map<String, Integer> primaLegioPaintingMaterials = new HashMap<>();
        primaLegioPaintingMaterials.put("White oak", 20);
        primaLegioPaintingMaterials.put("Samite silk", 20);
        primaLegioPaintingMaterials.put("Tyrian purple", 74);
        primaLegioPaintingMaterials.put("Zarosian insignia", 20);

        Map<String, Integer> daBossManSculptureMaterials = new HashMap<>();
        daBossManSculptureMaterials.put("Yu'biusk clay", 50);
        daBossManSculptureMaterials.put("Malachite green", 44);
        daBossManSculptureMaterials.put("Soapstone", 44);

        Map<String, Integer> horogothgarCookingPotMaterials = new HashMap<>();
        horogothgarCookingPotMaterials.put("Yu'biusk clay", 60);
        horogothgarCookingPotMaterials.put("Malachite green", 38);
        horogothgarCookingPotMaterials.put("Soapstone", 40);

        Map<String, Integer> xoloShieldMaterials = new HashMap<>();
        xoloShieldMaterials.put("Goldrune", 52);
        xoloShieldMaterials.put("Orgone", 44);
        xoloShieldMaterials.put("Felt", 42);

        Map<String, Integer> xoloSpearMaterials = new HashMap<>();
        xoloSpearMaterials.put("Dragon metal", 74);
        xoloSpearMaterials.put("Orgone", 64);

        Map<String, Integer> goldDishMaterials = new HashMap<>();
        goldDishMaterials.put("Goldrune", 86);
        goldDishMaterials.put("Dragon metal", 54);

        Map<String, Integer> rakshaIdolMaterials = new HashMap<>();
        rakshaIdolMaterials.put("Orgone", 56);
        rakshaIdolMaterials.put("Dragon metal", 44);
        rakshaIdolMaterials.put("Goldrune", 40);

        Map<String, Integer> legatusMaximusFigurineMaterials = new HashMap<>();
        legatusMaximusFigurineMaterials.put("Goldrune", 8);
        legatusMaximusFigurineMaterials.put("Zarosian insignia", 14);
        legatusMaximusFigurineMaterials.put("Ancient vis", 10);

        Map<String, Integer> solemInUmbraPaintingMaterials = new HashMap<>();
        solemInUmbraPaintingMaterials.put("Samite silk", 8);
        solemInUmbraPaintingMaterials.put("White oak", 10);
        solemInUmbraPaintingMaterials.put("Tyrian purple", 14);


        Artifact hookahPipe = new Artifact("artifact-000", "Hookah pipe", "Hookah pipe (damaged)", hookahPipeMaterials, 20, "Lodge bar storage", Arrays.asList("Zamorakian I", "Museum - Zamorakian I", "Smoky Fings"));
        Artifact opulentWineGoblet = new Artifact("artifact-001", "Opulent wine goblet", "Opulent wine goblet (damaged)", opulentWineGobletMaterials, 20, "Lodge bar storage", Arrays.asList("Zamorakian I", "Museum - Zamorakian I", "Smoky Fings"));
        Artifact crestOfDagon = new Artifact("artifact-003", "Crest of Dagon", "Crest of Dagon (damaged)", crestOfDagonMaterials, 24, "Lodge art storage", Arrays.asList("Zamorakian I", "Museum - Zamorakian I", "Showy Fings", "Knowledge is Power"));
        Artifact disorderPainting = new Artifact("artifact-004", "'Disorder' painting", "'Disorder' painting (damaged)", disorderPaintingMaterials, 24, "Lodge art storage", Arrays.asList("Zamorakian I", "Museum - Zamorakian I", "Anarchic Abstraction"));
        Artifact impMask = new Artifact("artifact-005", "Imp mask", "Imp mask (damaged)", impMaskMaterials, 29, "Cultist footlocker", Arrays.asList("Zamorakian I", "Museum - Zamorakian I"));
        Artifact lesserDemonMask = new Artifact("artifact-006", "Lesser demon mask", "Lesser demon mask (damaged)", lesserDemonMaskMaterials, 29, "Cultist footlocker", Arrays.asList("Zamorakian I", "Museum - Zamorakian I", "Hat Hoarder"));
        Artifact greaterDemonMask = new Artifact("artifact-007", "Greater demon mask", "Greater demon mask (damaged)", greaterDemonMaskMaterials, 29, "Cultist footlocker", Arrays.asList("Zamorakian I", "Museum - Zamorakian I", "Hat Hoarder"));
        Artifact orderOfDisRobes = new Artifact("artifact-008", "Order of Dis robes", "Order of Dis robes (damaged)", orderOfDisRobesMaterials, 36, "Sacrificial altar", Arrays.asList("Zamorakian I", "Museum - Zamorakian I"));
        Artifact ritualDagger = new Artifact("artifact-009", "Ritual dagger", "Ritual dagger (damaged)", ritualDaggerMaterials, 36, "Sacrificial altar", Arrays.asList("Zamorakian I", "Museum - Zamorakian I"));
        Artifact venatorDagger = new Artifact("artifact-010", "Venator dagger", "Venator dagger (damaged)", venatorDaggerMatierals, 1, "Venator remains", Arrays.asList("Zarosian I", "Museum - Zarosian I"));
        Artifact venatorLightCrossbow = new Artifact("artifact-011", "Venator light crossbow", "Venator light crossbow (damaged)", venatorLightCrossbowMaterials, 5, "Venator remains", Arrays.asList("Zarosian I", "Museum - Zarosian I"));
        Artifact primisElementisStandard = new Artifact("artifact-012", "Primis Elementis standard", "Primis Elementis standard (damaged)", primisElementisStandardMaterials, 12, "Legionary remains", Arrays.asList("Zarosian I", "Museum - Zarosian I"));
        Artifact legionaryGladius = new Artifact("artifact-013", "Legionary gladius", "Legionary gladius (damaged)", legionaryGladiusMaterials, 12, "Legionary remains", Arrays.asList("Zarosian I", "Museum - Zarosian I"));
        Artifact legionarySquareShield = new Artifact("artifact-014", "Legionary square shield", "Legionary square shield (damaged)", legionarySquareShieldMaterials, 12, "Legionary remains", Arrays.asList("Zarosian I", "Museum - Zarosian I"));
        Artifact zarosEffigy = new Artifact("artifact-015", "Zaros effigy", "Zaros effigy (damaged)", zarosEffigyMaterials, 17, "Castra debris", Arrays.asList("Zarosian I", "Museum - Zarosian I"));
        Artifact zarosianTrainingDummy = new Artifact("artifact-016", "Zarosian training dummy", "Zarosian training dummy (damaged)", zarosianTrainingDummyMaterials, 17, "Castra debris", Arrays.asList("Zarosian I", "Museum - Zarosian I"));
        Artifact legatusMaximusFigurine = new Artifact("artifact-017", "Legatus Maximus figurine", "Legatus Maximus figurine (damaged)", legatusMaximusFigurineMaterials, 25, "Administratum debris", Arrays.asList("Zarosian I", "Museum - Zarosian I", "Magic Man", "Showy Fings"));
        Artifact solemInUmbraPainting = new Artifact("artifact-018", "'Solem in Umbra' painting", "'Solem in Umbra' painting (damaged)", solemInUmbraPaintingMaterials, 25, "Administratum debris", Arrays.asList("Zarosian I", "Museum - Zarosian I", "Imperial Impressionism"));
        Artifact fryingPan = new Artifact("artifact-019", "'Frying pan'", "'Frying pan' (damaged)", fryingPanMaterials, 42, "Prodromoi remains", Arrays.asList("Saradominist I", "Museum - Saradominist I"));
        Artifact hallowedLantern = new Artifact("artifact-020", "Hallowed lantern", "Hallowed lantern (damaged)", hallowedLanternMaterials, 42, "Prodromoi remains", Arrays.asList("Saradominist I", "Museum - Saradominist I"));
        Artifact brandingIron = new Artifact("artifact-021", "Branding iron", "Branding iron (damaged)", brandingIronMaterials, 45, "Dis dungeon debris", Arrays.asList("Zamorakian II", "Museum - Zamorakian II"));
        Artifact manacles = new Artifact("artifact-022", "Manacles", "Manacles (damaged)", manaclesMaterials, 45, "Dis dungeon debris", Arrays.asList("Zamorakian II", "Museum - Zamorakian II"));
        Artifact ancientTimepiece = new Artifact("artifact-023",
                "Ancient timepiece", "Ancient timepiece (damaged)", ancientTimepieceMaterials, 47, "Praesidio remains", Arrays.asList("Zarosian II", "Museum - Zarosian II", "Blingy Fings", "Magic Man"));
        Artifact legatusPendant = new Artifact("artifact-024",
                "Legatus pendant", "Legatus pendant (damaged)", legatusPendantMaterials, 47, "Praesidio remains", Arrays.asList("Zarosian II", "Museum - Zarosian II", "Blingy Fings"));
        Artifact ceremonialUnicornOrnament = new Artifact("artifact-025",
                "Ceremonial unicorn ornament", "Ceremonial unicorn ornament (damaged)", ceremonialUnicornOrnamentMaterials, 48, "Monoceros remains", Arrays.asList("Saradominist I", "Museum - Saradominist I", "Hat Hoarder"));
        Artifact ceremonialUnicornSaddle = new Artifact("artifact-026",
                "Ceremonial unicorn saddle", "Ceremonial unicorn saddle (damaged)", ceremonialUnicornSaddleMaterials, 48, "Monoceros remains", Arrays.asList("Saradominist I", "Museum - Saradominist I"));
        Artifact everlightHarp = new Artifact("artifact-027",
                "Everlight harp", "Everlight harp (damaged)", everlightHarpMaterials, 51, "Amphitheatre debris", Arrays.asList("Saradominist I", "Museum - Saradominist I", "Wise Am the Music Man"));
        Artifact everlightTrumpet = new Artifact("artifact-028",
                "Everlight trumpet", "Everlight trumpet (damaged)", everlightTrumpetMaterials, 51, "Amphitheatre debris", Arrays.asList("Saradominist I", "Museum - Saradominist I", "Wise Am the Music Man", "Smoky Fings"));
        Artifact everlightViolin = new Artifact("artifact-029",
                "Everlight violin", "Everlight violin (damaged)", everlightViolinMaterials, 51, "Amphitheatre debris", Arrays.asList("Saradominist I", "Museum - Saradominist I", "Wise Am the Music Man"));
        Artifact foldedArmFigurineFemale = new Artifact("artifact-030",
                "Folded-arm figurine (female)", "Folded-arm figurine (female)(damaged)", foldedArmFigurineFemaleMaterials, 56, "Ceramics studio debris", Arrays.asList("Saradominist I", "Museum - Saradominist I"));
        Artifact foldedArmFigurineMale = new Artifact("artifact-031",
                "Folded-arm figurine (male)", "Folded-arm figurine (male)(damaged)", foldedArmFigurineMaleMaterials, 56, "Ceramics studio debris", Arrays.asList("Saradominist I", "Museum - Saradominist I"));
        Artifact inciteFearSpellScroll = new Artifact("artifact-032",
                "'Incite Fear' spell scroll", "'Incite Fear' spell scroll (damaged)", inciteFearSpellScrollMaterials, 58, "Carcerem debris", Arrays.asList("Zarosian II", "Museum - Zarosian II", "Magic Man", "Imperial Sorcery"));
        Artifact pontifexSignetRing = new Artifact("artifact-033",
                "Pontifex signet ring", "Pontifex signet ring (damaged)", pontifexSignetRingMaterials, 58, "Carcerem debris", Arrays.asList("Zarosian II", "Museum - Zarosian II", "Blingy Fings"));
        Artifact apexCap = new Artifact("artifact-034",
                "Apex cap", "Apex cap (damaged)", apexCapMaterials, 60, "Ministry remains", List.of("Museum - Zarosian V", "Religious Iconography"));
        Artifact curseTablet = new Artifact("artifact-035",
                "Curse tablet", "Curse tablet (damaged)", curseTabletMaterials, 60, "Ministry remains", List.of("Museum - Zarosian V", "Imperial Sorcery"));
        Artifact funeraryUrnOfShadow = new Artifact("artifact-036",
                "Funerary urn of shadow", "Funerary urn of shadow (damaged)", funeraryUrnOfShadowMaterials, 60, "Ministry remains", List.of("Museum - Zarosian V", "Urns of the Empire"));
        Artifact dominionDiscus = new Artifact("artifact-037",
                "Dominion discus", "Dominion discus (damaged)", dominionDiscusMaterials, 61, "Stadio debris", Arrays.asList("Saradominist II", "Museum - Saradominist II"));
        Artifact dominionJavelin = new Artifact("artifact-038",
                "Dominion javelin", "Dominion javelin (damaged)", dominionJavelinMaterials, 61, "Stadio debris", Arrays.asList("Saradominist II", "Museum - Saradominist II"));
        Artifact dominionPelteShield = new Artifact("artifact-039",
                "Dominion pelte shield", "Dominion pelte shield (damaged)", dominionPelteShieldMaterials, 61, "Stadio debris", Arrays.asList("Saradominist II", "Museum - Saradominist II"));
        Artifact infulaRobes = new Artifact("artifact-040",
                "Infula robes", "Infula robes (damaged)", infulaRobesMaterials, 62, "Cathedral debris", Arrays.asList("Museum - Zarosian V", "Religious Iconography"));
        Artifact funeraryUrnOfSmoke = new Artifact("artifact-041",
                "Funerary urn of smoke", "Funerary urn of smoke (damaged)", funeraryUrnOfSmokeMaterials, 62, "Cathedral debris", Arrays.asList("Museum - Zarosian V", "Urns of the Empire"));
        Artifact handOfTheAncients = new Artifact("artifact-042",
                "Hand of the Ancients", "Hand of the Ancients (damaged)", handOfTheAncientsMaterials, 62, "Cathedral debris", Arrays.asList("Museum - Zarosian V", "Imperial Sorcery"));
        Artifact decorativeAmphora = new Artifact("artifact-043",
                "Decorative amphora", "Decorative amphora (damaged)", decorativeAmphoraMaterials, 63, "Marketplace debris", Arrays.asList("Museum - Zarosian VI", "Entertaining the Masses"));
        Artifact funeraryUrnOfIce = new Artifact("artifact-044",
                "Funerary urn of ice", "Funerary urn of ice (damaged)", funeraryUrnOfIceMaterials, 63, "Marketplace debris", Arrays.asList("Museum - Zarosian VI", "Urns of the Empire"));
        Artifact loarnabRod = new Artifact("artifact-045",
                "Loarnab rod", "Loarnab rod (damaged)", loarnabRodMaterials, 63, "Marketplace debris", Arrays.asList("Museum - Zarosian VI", "Religious Iconography"));
        Artifact inquisitorsCeremonialArmour = new Artifact("artifact-046",
                "Inquisitor's ceremonial armour", "Inquisitor's ceremonial armour (damaged)", inquisitorsCeremonialArmourMaterials, 64, "Inquisitor remains", Arrays.asList("Finery of the Inquisition", "Museum - Zarosian VI"));
        Artifact inquisitorsCeremonialMask = new Artifact("artifact-047",
                "Inquisitor's ceremonial mask", "Inquisitor's ceremonial mask (damaged)", inquisitorsCeremonialMaskMaterials, 64, "Inquisitor remains", Arrays.asList("Finery of the Inquisition", "Museum - Zarosian VI"));
        Artifact inquisitorsSeal = new Artifact("artifact-048",
                "Inquisitor's seal", "Inquisitor's seal (damaged)", inquisitorsSealMaterials, 64, "Inquisitor remains", Arrays.asList("Finery of the Inquisition", "Museum - Zarosian VI"));
        Artifact lakeOfFirePainting = new Artifact("artifact-049",
                "'The Lake of Fire' painting", "'The Lake of Fire' painting (damaged)", lakeOfFirePaintingMaterials, 65, "Infernal art", Arrays.asList("Anarchic Abstraction", "Zamorakian II", "Museum - Zamorakian II"));
        Artifact lustMetalSculpture = new Artifact("artifact-050",
                "'Lust' metal sculpture", "'Lust' metal sculpture (damaged)", lustMetalSculptureMaterials, 65, "Infernal art", Arrays.asList("Showy Fings", "Zamorakian II", "Museum - Zamorakian II"));
        Artifact gladiatorHelmet = new Artifact("artifact-051",
                "Gladiator helmet", "Gladiator helmet (damaged)", gladiatorHelmetMaterials, 66, "Gladiator remains", Arrays.asList("Entertaining the Masses", "Museum - Zarosian VII"));
        Artifact gladiatorSword = new Artifact("artifact-052",
                "Gladiator sword", "Gladiator sword (damaged)", gladiatorSwordMaterials, 66, "Gladiator remains", Arrays.asList("Entertaining the Masses", "Museum - Zarosian VII"));
        Artifact funeraryUrnOfBlood = new Artifact("artifact-053",
                "Funerary urn of blood", "Funerary urn of blood (damaged)", funeraryUrnOfBloodMaterials, 66, "Gladiator remains", List.of("Museum - Zarosian VII"));
        Artifact funeraryUrnOfMiasma = new Artifact("artifact-054",
                "Funerary urn of miasma", "Funerary urn of miasma (damaged)", funeraryUrnOfMiasmaMaterials, 67, "Citizen remains", List.of("Museum - Zarosian VII"));
        Artifact modelChariot = new Artifact("artifact-055",
                "Model chariot", "Model chariot (damaged)", modelChariotMaterials, 67, "Citizen remains", List.of("Museum - Zarosian VII", "Entertaining the Masses"));
        Artifact serpentsFallCarving = new Artifact("artifact-056",
                "'The Serpent's Fall' carving", "'The Serpent's Fall' carving (damaged)", serpentsFallCarvingMaterials, 67, "Citizen remains", List.of("Museum - Zarosian VII", "Religious Iconography"));
        Artifact chaosStar = new Artifact("artifact-057",
                "Chaos star", "Chaos star (damaged)", chaosStarMaterials, 69, "Shakroth remains", List.of("Museum - Zamorakian II", "Zamorakian II"));
        Artifact spikedDogCollar = new Artifact("artifact-058",
                "Spiked dog collar", "Spiked dog collar (damaged)", spikedDogCollarMaterials, 69, "Shakroth remains", List.of("Museum - Zamorakian II", "Zamorakian II"));
        Artifact bronzeDominionMedal = new Artifact("artifact-059",
                "Bronze Dominion medal", "Bronze Dominion medal (damaged)", bronzeDominionMedalMaterials, 69, "Dominion Games podium", List.of("Museum - Saradominist II", "Blingy Fings", "Saradominist II"));
        Artifact silverDominionMedal = new Artifact("artifact-060",
                "Silver Dominion medal", "Silver Dominion medal (damaged)", silverDominionMedalMaterials, 69, "Dominion Games podium", List.of("Museum - Saradominist II", "Blingy Fings", "Saradominist II"));
        Artifact dominionTorch = new Artifact("artifact-061",
                "Dominion torch", "Dominion torch (damaged)", dominionTorchMaterials, 69, "Dominion Games podium", List.of("Museum - Saradominist II", "Smoky Fings", "Saradominist II"));
        Artifact ikovianGerege = new Artifact("artifact-062",
                "Ikovian gerege", "Ikovian gerege (damaged)", ikovianGeregeMaterials, 70, "Ikovian memorial", List.of("Museum - Armadylean I", "Armadylean I", "Knowledge is Power"));
        Artifact toyGlider = new Artifact("artifact-063",
                "Toy glider", "Toy glider (damaged)", toyGliderMaterials, 70, "Ikovian memorial", List.of("Museum - Armadylean I", "Armadylean I"));
        Artifact toyWarGolem = new Artifact("artifact-064",
                "Toy war golem", "Toy war golem (damaged)", toyWarGolemMaterials, 70, "Ikovian memorial", List.of("Museum - Armadylean I", "Armadylean I"));
        Artifact decorativeVase = new Artifact("artifact-065",
                "Decorative vase", "Decorative vase (damaged)", decorativeVaseMaterials, 72, "Oikos studio debris", List.of("Museum - Saradominist II", "Saradominist II"));
        Artifact kantharosCup = new Artifact("artifact-066",
                "Kantharos cup", "Kantharos cup (damaged)", kantharosCupMaterials, 72, "Oikos studio debris", List.of("Museum - Saradominist II", "Saradominist II"));
        Artifact pateraBowl = new Artifact("artifact-067",
                "Patera bowl", "Patera bowl (damaged)", pateraBowlMaterials, 72, "Oikos studio debris", List.of("Museum - Saradominist II", "Saradominist II"));
        Artifact castleGatestone = new Artifact("artifact-068",
                "Castle gatestone", "Castle gatestone (damaged)", castleGatestoneMaterials, 73, "Castle hall rubble", Arrays.asList("Dragonkin V", "Museum - Dragonkin V"));
        Artifact engravedRingOfKinship = new Artifact("artifact-069",
                "Engraved ring of kinship", "Engraved ring of kinship (damaged)", engravedRingOfKinshipMaterials, 73, "Castle hall rubble", Arrays.asList("Dragonkin V", "Museum - Dragonkin V"));
        Artifact ceremonialMace = new Artifact("artifact-070",
                "Ceremonial mace", "Ceremonial mace (damaged)", ceremonialMaceMaterials, 74, "Kharid-et chapel debris", Arrays.asList("Museum - Zarosian II", "Zarosian II", "Hitty Fings"));
        Artifact pontifexMaximusFigurine = new Artifact("artifact-071",
                "Pontifex Maximus figurine", "Pontifex Maximus figurine (damaged)", pontifexMaximusFigurineMaterials, 74, "Kharid-et chapel debris", Arrays.asList("Museum - Zarosian II", "Zarosian II", "Showy Fings"));
        Artifact consensusAdIdemPainting = new Artifact("artifact-072",
                "'Consensus ad Idem' painting", "'Consensus ad Idem' painting (damaged)", consensusAdIdemPaintingMaterials, 74, "Kharid-et chapel debris", Arrays.asList("Museum - Zarosian II", "Imperial Impressionism", "Zarosian II"));
        Artifact avianSongEggPlayer = new Artifact("artifact-206","Avian song-egg player", "Avian song-egg player (damaged)", avianSongEggPlayerMaterials, 76, "Keshik ger", Arrays.asList("Museum - Armadylean I", "Armadylean I", "Wise Am the Music Man"));
        Artifact keshikDrum = new Artifact("artifact-073",
                "Keshik drum", "Keshik drum (damaged)", keshikDrumMaterials, 76, "Keshik ger", Arrays.asList("Museum - Armadylean I", "Armadylean I", "Wise Am the Music Man"));
        Artifact morinKhuur = new Artifact("artifact-074",
                "Morin khuur", "Morin khuur (damaged)", morinKhuurMaterials, 76, "Keshik ger", Arrays.asList("Museum - Armadylean I", "Armadylean I", "Wise Am the Music Man"));
        Artifact ekeleshuunBlinderMask = new Artifact("artifact-075",
                "Ekeleshuun blinder mask", "Ekeleshuun blinder mask (damaged)", ekeleshuunBlinderMaskMaterials, 76, "Gladiatorial goblin remains", Arrays.asList("Museum - Bandosian I", "Hat Problem", "Green Gobbo Goodies I"));
        Artifact narogoshuunHobDaGobBall = new Artifact("artifact-076",
                "Narogoshuun 'Hob-da-Gob' ball", "Narogoshuun 'Hob-da-Gob' ball (damaged)", narogoshuunHobDaGobBallMaterials, 76, "Gladiatorial goblin remains", List.of("Museum - Bandosian I", "Green Gobbo Goodies I"));
        Artifact rekeshuunWarTether = new Artifact("artifact-077",
                "Rekeshuun war tether", "Rekeshuun war tether (damaged)", rekeshuunWarTetherMaterials, 76, "Gladiatorial goblin remains", List.of("Museum - Bandosian I", "Green Gobbo Goodies I"));
        Artifact exploratoryTotem = new Artifact("artifact-078",
                "Exploratory totem", "Exploratory totem (damaged)", exploratoryTotemMaterials, 77, "Tunnelling equipment repository", List.of("Dragonkin V", "Museum - Dragonkin V"));
        Artifact excavatorPortalMine = new Artifact("artifact-079",
                "Excavator portal mine", "Excavator portal mine (damaged)", excavatorPortalMineMaterials, 77, "Tunnelling equipment repository", List.of("Dragonkin V", "Museum - Dragonkin V"));
        Artifact storageTotem = new Artifact("artifact-080",
                "Storage totem", "Storage totem (damaged)", storageTotemMaterials, 78, "Botanical reserve", List.of("Dragonkin VI", "Museum - Dragonkin VI"));
        Artifact plantSeedSatchel = new Artifact("artifact-081",
                "Plant seed satchel", "Plant seed satchel (damaged)", plantSeedSatchelMaterials, 78, "Botanical reserve", List.of("Dragonkin VI", "Museum - Dragonkin VI"));
        Artifact snuffBox = new Artifact("artifact-082",
                "Snuff box", "Snuff box (damaged)", snuffBoxMaterials, 78, "Botanical reserve", List.of("Dragonkin VI", "Museum - Dragonkin VI"));
        Artifact aviansieDreamcoat = new Artifact("artifact-083",
                "Aviansie dreamcoat", "Aviansie dreamcoat (damaged)", aviansieDreamcoatMaterials, 81, "Tailory debris", Arrays.asList("Armadylean I", "Museum - Armadylean I"));
        Artifact ceremonialPlume = new Artifact("artifact-084",
                "Ceremonial plume", "Ceremonial plume (damaged)", ceremonialPlumeMaterials, 81, "Tailory debris", Arrays.asList("Showy Fings", "Armadylean I", "Museum - Armadylean I"));
        Artifact peacockingParasol = new Artifact("artifact-085",
                "Peacocking parasol", "Peacocking parasol (damaged)", peacockingParasolMaterials, 81, "Tailory debris", Arrays.asList("Armadylean I", "Museum - Armadylean I"));
        Artifact ogreKyzajAxe = new Artifact("artifact-086",
                "Ogre Kyzaj axe", "Ogre Kyzaj axe (damaged)", ogreKyzajAxeMaterials, 81, "Crucible stands debris", Arrays.asList("Red Rum Relics I", "Museum - Bandosian I"));
        Artifact orkCleaverSword = new Artifact("artifact-087",
                "Ork cleaver sword", "Ork cleaver sword (damaged)", orkCleaverSwordMaterials, 81, "Crucible stands debris", Arrays.asList("Red Rum Relics I", "Museum - Bandosian I"));
        Artifact larupiaTrophy = new Artifact("artifact-088",
                "Larupia trophy", "Larupia trophy (damaged)", larupiaTrophyMaterials, 81, "Animal trophies", Arrays.asList("Zamorakian II", "Museum - Zamorakian II"));
        Artifact lionTrophy = new Artifact("artifact-089",
                "Lion trophy", "Lion trophy (damaged)", lionTrophyMaterials, 81, "Animal trophies", Arrays.asList("Zamorakian II", "Museum - Zamorakian II"));
        Artifact sheWolfTrophy = new Artifact("artifact-090",
                "She-wolf trophy", "She-wolf trophy (damaged)", sheWolfTrophyMaterials, 81, "Animal trophies", Arrays.asList("Zamorakian II", "Museum - Zamorakian II"));
        Artifact pontifexCenser = new Artifact("artifact-091",
                "Pontifex censer", "Pontifex censer (damaged)", pontifexCenserMaterials, 81, "Pontifex remains", Arrays.asList("Smoky Fings", "Zarosian II", "Museum - Zarosian II"));
        Artifact pontifexCrozier = new Artifact("artifact-092",
                "Pontifex crozier", "Pontifex crozier (damaged)", pontifexCrozierMaterials, 81, "Pontifex remains", Arrays.asList("Hitty Fings", "Zarosian II", "Museum - Zarosian II"));
        Artifact pontifexMitre = new Artifact("artifact-093",
                "Pontifex mitre", "Pontifex mitre (damaged)", pontifexMitreMaterials, 81, "Pontifex remains", Arrays.asList("Zarosian II", "Museum - Zarosian II", "Hat Hoarder"));
        Artifact thorobshuunBattleStandard = new Artifact("artifact-094",
                "Thorobshuun battle standard", "Thorobshuun battle standard (damaged)", thorobshuunBattleStandardMaterials, 83, "Goblin dorm debris", Arrays.asList("Green Gobbo Goodies I", "Museum - Bandosian I"));
        Artifact yurkolgokhStinkGrenade = new Artifact("artifact-095",
                "Yurkolgokh stink grenade", "Yurkolgokh stink grenade (damaged)", yurkolgokhStinkGrenadeMaterials, 83, "Goblin dorm debris", Arrays.asList("Green Gobbo Goodies I", "Museum - Bandosian I"));
        Artifact dominarianDevice = new Artifact("artifact-096",
                "Dominarian device", "Dominarian device (damaged)", dominarianDeviceMaterials, 84, "Oikos fishing hut remnants", Arrays.asList("Saradominist III", "Museum - Saradominist III"));
        Artifact fishingTrident = new Artifact("artifact-097",
                "Fishing trident", "Fishing trident (damaged)", fishingTridentMaterials, 84, "Oikos fishing hut remnants", Arrays.asList("Hitty Fings", "Saradominist III", "Museum - Saradominist III"));
        Artifact hawkeyeLensMultiVisionScope = new Artifact("artifact-098",
                "Hawkeye lens multi-vision scope", "Hawkeye lens multi-vision scope (damaged)", hawkeyeLensMultiVisionScopeMaterials, 85, "Weapons research debris", Arrays.asList("Armadylean II", "Museum - Armadylean II"));
        Artifact talon3RazorWing = new Artifact("artifact-099",
                "Talon-3 razor wing", "Talon-3 razor wing (damaged)", talon3RazorWingMaterials, 85, "Weapons research debris", Arrays.asList("Armadylean II", "Museum - Armadylean II"));
        Artifact exsanguinateSpellScroll = new Artifact("artifact-100",
                "'Exsanguinate' spell scroll", "'Exsanguinate' spell scroll (damaged)", exsanguinateSpellScrollMaterials, 86, "Orcus altar", Arrays.asList("Zarosian III", "Museum - Zarosian III", "Magic Man", "Imperial Sorcery"));
        Artifact necromanticFocus = new Artifact("artifact-101",
                "Necromantic focus", "Necromantic focus (damaged)", necromanticFocusMaterials, 86, "Orcus altar", Arrays.asList("Zarosian III", "Museum - Zarosian III", "Knowledge is Power", "Imperial Sorcery"));
        Artifact spentSummoningCharm = new Artifact("artifact-102",
                "Spent summoning charm", "Spent summoning charm (damaged)", spentSummoningCharmMaterials, 87, "Communal space", Arrays.asList("Dragonkin VI", "Museum - Dragonkin VI"));
        Artifact friendshipBracelet = new Artifact("artifact-103",
                "'Friendship bracelet'", "'Friendship bracelet' (damaged)", friendshipBraceletMaterials, 87, "Communal space", Arrays.asList("Dragonkin VI", "Museum - Dragonkin VI"));
        Artifact homelyTotem = new Artifact("artifact-104",
                "Homely totem", "Homely totem (damaged)", homelyTotemMaterials, 87, "Communal space", Arrays.asList("Dragonkin VI", "Museum - Dragonkin VI"));
        Artifact highPriestCrozier = new Artifact("artifact-105",
                "High priest crozier", "High priest crozier (damaged)", highPriestCrozierMaterials, 89, "Big High War God shrine", Arrays.asList("Hitty Fings", "Green Gobbo Goodies II", "Museum - Bandosian I"));
        Artifact highPriestMitre = new Artifact("artifact-106",
                "High priest mitre", "High priest mitre (damaged)", highPriestMitreMaterials, 89, "Big High War God shrine", Arrays.asList("Green Gobbo Goodies II", "Museum - Bandosian I", "Hat Problem", "Magic Man"));
        Artifact highPriestOrb = new Artifact("artifact-107",
                "High priest orb", "High priest orb (damaged)", highPriestOrbMaterials, 89, "Big High War God shrine", Arrays.asList("Hitty Fings", "Green Gobbo Goodies II", "Museum - Bandosian I"));
        Artifact tormentMetalSculpture = new Artifact("artifact-108",
                "'Torment' metal sculpture", "'Torment' metal sculpture (damaged)", tormentMetalSculptureMaterials, 89, "Dis overspill", Arrays.asList("Zamorakian III", "Museum - Zamorakian III"));
        Artifact pandemoniumTapestry = new Artifact("artifact-109",
                "'Pandemonium' tapestry", "'Pandemonium' tapestry (damaged)", pandemoniumTapestryMaterials, 89, "Dis overspill", Arrays.asList("Anarchic Abstraction", "Zamorakian III", "Museum - Zamorakian III"));
        Artifact pasaha = new Artifact("artifact-110",
                "Pasaha", "Pasaha (damaged)", pasahaMaterials, 90, "Varanusaur remains", Arrays.asList("Dragonkin I", "Museum - Dragonkin I"));
        Artifact ritualBell = new Artifact("artifact-111",
                "Ritual bell", "Ritual bell (damaged)", ritualBellMaterials, 90, "Varanusaur remains", Arrays.asList("Dragonkin I", "Museum - Dragonkin I"));
        Artifact prototypeGravimeter = new Artifact("artifact-112",
                "Prototype gravimeter", "Prototype gravimeter (damaged)", prototypeGravimeterMaterials, 91, "Gravitron research debris", Arrays.asList("Armadylean II", "Museum - Armadylean II"));
        Artifact songbirdRecorder = new Artifact("artifact-113",
                "Songbird recorder", "Songbird recorder (damaged)", songbirdRecorderMaterials, 91, "Gravitron research debris", Arrays.asList("Armadylean II", "Museum - Armadylean II", "Wise Am the Music Man"));
        Artifact amphora = new Artifact("artifact-114",
                "Amphora", "Amphora (damaged)", amphoraMaterials, 92, "Acropolis debris", Arrays.asList("Saradominist III", "Museum - Saradominist III"));
        Artifact rodOfAsclepius = new Artifact("artifact-115",
                "Rod of Asclepius", "Rod of Asclepius (damaged)", rodOfAsclepiusMaterials, 92, "Acropolis debris", Arrays.asList("Showy Fings", "Saradominist III", "Museum - Saradominist III"));
        Artifact zarosianEwer = new Artifact("artifact-116",
                "Zarosian ewer", "Zarosian ewer (damaged)", zarosianEwerMaterials, 93, "Armarium debris", Arrays.asList("Zarosian III", "Museum - Zarosian III"));
        Artifact zarosianStein = new Artifact("artifact-117",
                "Zarosian stein", "Zarosian stein (damaged)", zarosianSteinMaterials, 93, "Armarium debris", Arrays.asList("Zarosian III", "Museum - Zarosian III"));
        Artifact beastkeeperHelm = new Artifact("artifact-118",
                "Beastkeeper helm", "Beastkeeper helm (damaged)", beastkeeperHelmMaterials, 94, "Yu'biusk animal pen", Arrays.asList("Red Rum Relics I", "Museum - Bandosian II", "Hat Problem"));
        Artifact idithuunHornRing = new Artifact("artifact-119",
                "Idithuun horn ring", "Idithuun horn ring (damaged)", idithuunHornRingMaterials, 94, "Yu'biusk animal pen", Arrays.asList("Green Gobbo Goodies II", "Museum - Bandosian II"));
        Artifact nosorogSculpture = new Artifact("artifact-120",
                "'Nosorog!' sculpture", "'Nosorog!' sculpture (damaged)", nosorogSculptureMaterials, 94, "Yu'biusk animal pen", Arrays.asList("Red Rum Relics I", "Museum - Bandosian II"));
        Artifact dayguardShield = new Artifact("artifact-121",
                "Dayguard shield", "Dayguard shield (damaged)", dayguardShieldMaterials, 95, "Keshik tower debris", Arrays.asList("Armadylean II", "Museum - Armadylean II"));
        Artifact stormguardGerege = new Artifact("artifact-122",
                "Stormguard gerege", "Stormguard gerege (damaged)", stormguardGeregeMaterials, 95, "Keshik tower debris", Arrays.asList("Armadylean II", "Museum - Armadylean II"));
        Artifact kilaya = new Artifact("artifact-123",
                "Kilaya", "Kilaya (damaged)", kilayaMaterials, 96, "Dragonkin reliquary", Arrays.asList("Dragonkin I", "Museum - Dragonkin I"));
        Artifact vazara = new Artifact("artifact-124",
                "Vazara", "Vazara (damaged)", vazaraMaterials, 96, "Dragonkin reliquary", Arrays.asList("Dragonkin I", "Museum - Dragonkin I"));
        Artifact ourgMegahitter = new Artifact("artifact-125",
                "Ourg megahitter", "Ourg megahitter (damaged)", ourgMegahitterMaterials, 97, "Goblin trainee remains", Arrays.asList("Red Rum Relics II", "Museum - Bandosian II"));
        Artifact ourgTowerShield = new Artifact("artifact-126",
                "Ourg tower/goblin cower shield", "Ourg tower/goblin cower shield (damaged)", ourgTowerShieldMaterials, 97, "Goblin trainee remains", Arrays.asList("Museum - Bandosian II", "Red Rum Relics II"));
        Artifact garagorshuunAnchor = new Artifact("artifact-127",
                "Garagorshuun anchor", "Garagorshuun anchor (damaged)", garagorshuunAnchorMaterials, 97, "Goblin trainee remains", Arrays.asList("Green Gobbo Goodies II", "Museum - Bandosian II"));
        Artifact golemHeart = new Artifact("artifact-128",
                "Golem heart", "Golem heart (damaged)", golemHeartMaterials, 98, "Destroyed golem", Arrays.asList("Armadylean II", "Museum - Armadylean II"));
        Artifact golemInstruction = new Artifact("artifact-129",
                "Golem instruction", "Golem instruction (damaged)", golemInstructionMaterials, 98, "Destroyed golem", Arrays.asList("Armadylean II", "Museum - Armadylean II", "Knowledge is Power"));
        Artifact hellfireHaladie = new Artifact("artifact-130",
                "Hellfire haladie", "Hellfire haladie (damaged)", hellfireHaladieMaterials, 98, "Byzroth remains", Arrays.asList("Museum - Zamorakian III", "Zamorakian III"));
        Artifact hellfireKatar = new Artifact("artifact-131",
                "Hellfire katar", "Hellfire katar (damaged)", hellfireKatarMaterials, 98, "Byzroth remains", Arrays.asList("Museum - Zamorakian III", "Zamorakian III"));
        Artifact hellfireZaghnal = new Artifact("artifact-132",
                "Hellfire zaghnal", "Hellfire zaghnal (damaged)", hellfireZaghnalMaterials, 98, "Byzroth remains", Arrays.asList("Museum - Zamorakian III", "Zamorakian III"));
        Artifact deathMask = new Artifact("artifact-133",
                "Death mask", "Death mask (damaged)", deathMaskMaterials, 99, "Dragonkin coffin", Arrays.asList("Dragonkin I", "Museum - Dragonkin I"));
        Artifact dragonkinCalendar = new Artifact("artifact-134",
                "Dragonkin calendar", "Dragonkin calendar (damaged)", dragonkinCalendarMaterials, 99, "Dragonkin coffin", Arrays.asList("Dragonkin I", "Museum - Dragonkin I"));
        Artifact dragonkinStaff = new Artifact("artifact-135",
                "Dragonkin staff", "Dragonkin staff (damaged)", dragonkinStaffMaterials, 99, "Dragonkin coffin", Arrays.asList("Dragonkin I", "Museum - Dragonkin I"));
        Artifact dorgeshuunSpear = new Artifact("artifact-136",
                "Dorgeshuun spear", "Dorgeshuun spear (damaged)", dorgeshuunSpearMaterials, 100, "Kyzaj champion's boudoir", Arrays.asList("Museum - Bandosian II", "Green Gobbo Goodies III"));
        Artifact forgedInWarSculpture = new Artifact("artifact-137",
                "'Forged in War' sculpture", "'Forged in War' sculpture (damaged)", forgedInWarSculptureMaterials, 100, "Kyzaj champion's boudoir", Arrays.asList("Museum - Bandosian II", "Red Rum Relics II"));
        Artifact kopisDagger = new Artifact("artifact-138",
                "Kopis dagger", "Kopis dagger (damaged)", kopisDaggerMaterials, 100, "Icyene weapon rack", Arrays.asList("Museum - Saradominist III", "Saradominist III"));
        Artifact xiphosShortSword = new Artifact("artifact-139",
                "Xiphos short sword", "Xiphos short sword (damaged)", xiphosShortSwordMaterials, 100, "Icyene weapon rack", Arrays.asList("Museum - Saradominist III", "Saradominist III"));
        Artifact smokeCloudSpellScroll = new Artifact("artifact-140",
                "'Smoke Cloud' spell scroll", "'Smoke Cloud' spell scroll (damaged)", smokeCloudSpellScrollMaterials, 100, "Culinarum debris", Arrays.asList("Magic Man", "Museum - Zarosian III", "Zarosian III", "Imperial Sorcery"));
        Artifact vigoremVial = new Artifact("artifact-141",
                "Vigorem vial", "Vigorem vial (damaged)", vigoremVialMaterials, 100, "Culinarum debris", Arrays.asList("Museum - Zarosian III", "Zarosian III"));
        Artifact dragonScalpel = new Artifact("artifact-142",
                "Dragon scalpel", "Dragon scalpel (damaged)", dragonScalpelMaterials, 101, "Autopsy table", Arrays.asList("Dragonkin II", "Museum - Dragonkin II"));
        Artifact protectiveGoggles = new Artifact("artifact-143",
                "Protective goggles", "Protective goggles (damaged)", protectiveGogglesMaterials, 101, "Autopsy table", Arrays.asList("Dragonkin II", "Museum - Dragonkin II"));
        Artifact dragonBurner = new Artifact("artifact-144",
                "Dragon burner", "Dragon burner (damaged)", dragonBurnerMaterials, 102, "Experiment workbench", Arrays.asList("Dragonkin II", "Museum - Dragonkin II"));
        Artifact orthenglassFlask = new Artifact("artifact-145",
                "Orthenglass flask", "Orthenglass flask (damaged)", orthenglassFlaskMaterials, 102, "Experiment workbench", Arrays.asList("Dragonkin II", "Museum - Dragonkin II"));
        Artifact blackfireLance = new Artifact("artifact-146",
                "Blackfire lance", "Blackfire lance (damaged)", blackfireLanceMaterials, 103, "Keshik weapon rack", Arrays.asList("Armadylean III", "Museum - Armadylean III"));
        Artifact nightguardShield = new Artifact("artifact-147",
                "Nightguard shield", "Nightguard shield (damaged)", nightguardShieldMaterials, 103, "Keshik weapon rack", Arrays.asList("Armadylean III", "Museum - Armadylean III"));
        Artifact projectionAttuner = new Artifact("artifact-148",
                "Projection attuner", "Projection attuner (damaged)", projectionAttunerMaterials, 103, "Projection space", Arrays.asList("Dragonkin VII", "Museum - Dragonkin VII"));
        Artifact goldenProjectionNeedle = new Artifact("artifact-149",
                "Golden projection 'needle'", "Golden projection 'needle' (damaged)", goldenProjectionNeedleMaterials, 103, "Projection space", Arrays.asList("Dragonkin VII", "Museum - Dragonkin VII"));
        Artifact huzamogaarbChaosCrown = new Artifact("artifact-150",
                "Huzamogaarb chaos crown", "Huzamogaarb chaos crown (damaged)", huzamogaarbChaosCrownMaterials, 104, "Warforge scrap pile", Arrays.asList("Green Gobbo Goodies III", "Hat Problem", "Museum - Bandosian III"));
        Artifact saragorgakStarCrown = new Artifact("artifact-151",
                "Saragorgak star crown", "Saragorgak star crown (damaged)", saragorgakStarCrownMaterials, 104, "Warforge scrap pile", Arrays.asList("Green Gobbo Goodies III", "Hat Hoarder", "Museum - Bandosian III"));
        Artifact possessionMetalSculpture = new Artifact("artifact-152",
                "'Possession' metal sculpture", "'Possession' metal sculpture (damaged)", possessionMetalSculptureMaterials, 104, "Hellfire forge", Arrays.asList("Museum - Zamorakian III", "Zamorakian III"));
        Artifact trishula = new Artifact("artifact-153",
                "Trishula", "Trishula (damaged)", trishulaMaterials, 104, "Hellfire forge", Arrays.asList("Museum - Zamorakian III", "Zamorakian III"));
        Artifact tsutsarothPiercing = new Artifact("artifact-154",
                "Tsutsaroth piercing", "Tsutsaroth piercing (damaged)", tsutsarothPiercingMaterials, 104, "Hellfire forge", Arrays.asList("Museum - Zamorakian III", "Zamorakian III"));
        Artifact hallowedBeTheEverlightPainting = new Artifact("artifact-155",
                "'Hallowed Be the Everlight' painting", "'Hallowed Be the Everlight' painting (damaged)", hallowedBeTheEverlightPaintingMaterials, 105, "Stockpiled art", Arrays.asList("Museum - Saradominist IV", "Radiant Renaissance", "Saradominist IV"));
        Artifact theLordOfLightPainting = new Artifact("artifact-156",
                "'The Lord of Light' painting", "'The Lord of Light' painting (damaged)", theLordOfLightPaintingMaterials, 105, "Stockpiled art", Arrays.asList("Museum - Saradominist IV", "Radiant Renaissance", "Saradominist IV"));
        Artifact thePrideOfPadosanPainting = new Artifact("artifact-157",
                "'The Pride of Padosan' painting", "'The Pride of Padosan' painting (damaged)", thePrideOfPadosanPaintingMaterials, 105, "Stockpiled art", Arrays.asList("Museum - Saradominist IV", "Radiant Renaissance", "Saradominist IV"));
        Artifact meditationPipe = new Artifact("artifact-158",
                "Meditation pipe", "Meditation pipe (damaged)", meditationPipeMaterials, 106, "Aughra remains", Arrays.asList("Dragonkin III", "Museum - Dragonkin III"));
        Artifact personalTotem = new Artifact("artifact-159",
                "Personal totem", "Personal totem (damaged)", personalTotemMaterials, 106, "Aughra remains", Arrays.asList("Dragonkin III", "Museum - Dragonkin III"));
        Artifact singingBowl = new Artifact("artifact-160",
                "Singing bowl", "Singing bowl (damaged)", singingBowlMaterials, 106, "Aughra remains", Arrays.asList("Dragonkin III", "Museum - Dragonkin III"));
        Artifact ancientMagicTablet = new Artifact("artifact-161",
                "Ancient magic tablet", "Ancient magic tablet (damaged)", ancientMagicTabletMaterials, 107, "Ancient magick munitions", Arrays.asList("Museum - Zarosian III", "Zarosian III", "Imperial Sorcery"));
        Artifact animateDeadSpellScroll = new Artifact("artifact-162",
                "'Animate Dead' spell scroll", "'Animate Dead' spell scroll (damaged)", animateDeadSpellScrollMaterials, 107, "Ancient magick munitions", Arrays.asList("Magic Man", "Museum - Zarosian III", "Zarosian III", "Imperial Sorcery"));
        Artifact portablePhylactery = new Artifact("artifact-163",
                "Portable phylactery", "Portable phylactery (damaged)", portablePhylacteryMaterials, 107, "Ancient magick munitions", Arrays.asList("Magic Man", "Museum - Zarosian III", "Zarosian III"));
        Artifact comfortGatestone = new Artifact("artifact-164",
                "Comfort gatestone", "Comfort gatestone (damaged)", comfortGatestoneMaterials, 107, "Security booth", Arrays.asList("Dragonkin VII", "Museum - Dragonkin VII"));
        Artifact halaksCube = new Artifact("artifact-165",
                "Halak's cube", "Halak's cube (damaged)", halaksCubeMaterials, 107, "Security booth", Arrays.asList("Dragonkin VII", "Museum - Dragonkin VII"));
        Artifact lingamStone = new Artifact("artifact-166",
                "Lingam stone", "Lingam stone (damaged)", lingamStoneMaterials, 108, "Moksha device", Arrays.asList("Dragonkin III", "Museum - Dragonkin III"));
        Artifact masterControl = new Artifact("artifact-167",
                "Master control", "Master control (damaged)", masterControlMaterials, 108, "Moksha device", Arrays.asList("Dragonkin III", "Museum - Dragonkin III"));
        Artifact theEnlightenedSoulScroll = new Artifact("artifact-168",
                "'The Enlightened Soul' scroll", "'The Enlightened Soul' scroll (damaged)", theEnlightenedSoulScrollMaterials, 109, "Bibliotheke debris", Arrays.asList("Knowledge is Power", "Museum - Saradominist IV", "Saradominist IV"));
        Artifact theEudoxianElementsTablet = new Artifact("artifact-205","'The Eudoxian Elements' tablet", "'The Eudoxian Elements' tablet (damaged)", theEudoxianElementsTabletMaterials, 109, "Bibliotheke debris", Arrays.asList("Knowledge is Power", "Museum - Saradominist IV", "Saradominist IV"));
        Artifact drogokishuunHookSword = new Artifact("artifact-169",
                "Drogokishuun hook sword", "Drogokishuun hook sword (damaged)", drogokishuunHookSwordMaterials, 110, "Warforge weapon rack", Arrays.asList("Green Gobbo Goodies III", "Museum - Bandosian III"));
        Artifact hobgoblinMansticker = new Artifact("artifact-170",
                "Hobgoblin mansticker", "Hobgoblin mansticker (damaged)", hobgoblinManstickerMaterials, 110, "Warforge weapon rack", Arrays.asList("Museum - Bandosian III", "Red Rum Relics II"));
        Artifact chaosElementalTrophy = new Artifact("artifact-171",
                "Chaos Elemental trophy", "Chaos Elemental trophy (damaged)", chaosElementalTrophyMaterials, 110, "Chthonian trophies", Arrays.asList("Museum - Zamorakian IV", "Zamorakian IV"));
        Artifact viriusTrophy = new Artifact("artifact-172",
                "Virius trophy", "Virius trophy (damaged)", viriusTrophyMaterials, 110, "Chthonian trophies", Arrays.asList("Museum - Zamorakian IV", "Zamorakian IV"));
        Artifact flatCap = new Artifact("artifact-173",
                "Flat cap", "Flat cap (damaged)", flatCapMaterials, 111, "Flight research debris", Arrays.asList("Armadylean III", "Hat Problem", "Museum - Armadylean III"));
        Artifact nightOwlFlightGoggles = new Artifact("artifact-174",
                "Night owl flight goggles", "Night owl flight goggles (damaged)", nightOwlFlightGogglesMaterials, 111, "Flight research debris", Arrays.asList("Armadylean III", "Museum - Armadylean III"));
        Artifact prototypeGodbow = new Artifact("artifact-175",
                "Prototype godbow", "Prototype godbow (damaged)", prototypeGodbowMaterials, 112, "Aetherium forge", Arrays.asList("Armadylean III", "Museum - Armadylean III"));
        Artifact prototypeGodstaff = new Artifact("artifact-176",
                "Prototype godstaff", "Prototype godstaff (damaged)", prototypeGodstaffMaterials, 112, "Aetherium forge", Arrays.asList("Armadylean III", "Museum - Armadylean III"));
        Artifact prototypeGodsword = new Artifact("artifact-177",
                "Prototype godsword", "Prototype godsword (damaged)", prototypeGodswordMaterials, 112, "Aetherium forge", Arrays.asList("Armadylean III", "Museum - Armadylean III"));
        Artifact xoloHardHat = new Artifact("artifact-178",
                "Xolo hard hat", "Xolo hard hat (damaged)", xoloHardHatMaterials, 113, "Xolo mine", Arrays.asList("Dragonkin IV", "Museum - Dragonkin IV"));
        Artifact xoloPickaxe = new Artifact("artifact-179",
                "Xolo pickaxe", "Xolo pickaxe (damaged)", xoloPickaxeMaterials, 113, "Xolo mine", Arrays.asList("Dragonkin IV", "Museum - Dragonkin IV"));
        Artifact portablePortalGenerator = new Artifact("artifact-180",
                "Portable portal generator", "Portable portal generator (damaged)", portablePortalGeneratorMaterials, 113, "Traveller's station", Arrays.asList("Dragonkin VII", "Museum - Dragonkin VII"));
        Artifact warpedTrinket = new Artifact("artifact-181",
                "Warped trinket", "Warped trinket (damaged)", warpedTrinketMaterials, 113, "Traveller's station", Arrays.asList("Dragonkin VII", "Museum - Dragonkin VII"));
        Artifact praetorianHood = new Artifact("artifact-182",
                "Praetorian hood", "Praetorian hood (damaged)", praetorianHoodMaterials, 114, "Praetorian remains", Arrays.asList("Hat Problem", "Museum - Zarosian IV", "Zarosian IV"));
        Artifact praetorianRobes = new Artifact("artifact-183",
                "Praetorian robes", "Praetorian robes (damaged)", praetorianRobesMaterials, 114, "Praetorian remains", Arrays.asList("Museum - Zarosian IV", "Zarosian IV"));
        Artifact praetorianStaff = new Artifact("artifact-184",
                "Praetorian staff", "Praetorian staff (damaged)", praetorianStaffMaterials, 114, "Praetorian remains", Arrays.asList("Museum - Zarosian IV", "Zarosian IV"));
        Artifact kalikraChieftainCrown = new Artifact("artifact-185",
                "Kal-i-kra chieftain crown", "Kal-i-kra chieftain crown (damaged)", kalikraChieftainCrownMaterials, 115, "Bandos's sanctum debris", Arrays.asList("Hat Hoarder", "Museum - Bandosian III", "Red Rum Relics III"));
        Artifact kalikraMace = new Artifact("artifact-186",
                "Kal-i-kra mace", "Kal-i-kra mace (damaged)", kalikraMaceMaterials, 115, "Bandos's sanctum debris", Arrays.asList("Museum - Bandosian III", "Red Rum Relics III"));
        Artifact kalikraWarhorn = new Artifact("artifact-187",
                "Kal-i-kra warhorn", "Kal-i-kra warhorn (damaged)", kalikraWarhornMaterials, 115, "Bandos's sanctum debris", Arrays.asList("Museum - Bandosian III", "Red Rum Relics III"));
        Artifact tsutsarothHelm = new Artifact("artifact-188",
                "Tsutsaroth helm", "Tsutsaroth helm (damaged)", tsutsarothHelmMaterials, 116, "Tsutsaroth remains", Arrays.asList("Hat Hoarder", "Museum - Zamorakian IV", "Zamorakian IV"));
        Artifact tsutsarothPauldron = new Artifact("artifact-189",
                "Tsutsaroth pauldron", "Tsutsaroth pauldron (damaged)", tsutsarothPauldronMaterials, 116, "Tsutsaroth remains", Arrays.asList("Museum - Zamorakian IV", "Zamorakian IV"));
        Artifact tsutsarothUrumi = new Artifact("artifact-190",
                "Tsutsaroth urumi", "Tsutsaroth urumi (damaged)", tsutsarothUrumiMaterials, 116, "Tsutsaroth remains", Arrays.asList("Museum - Zamorakian IV", "Zamorakian IV"));
        Artifact doruSpear = new Artifact("artifact-191",
                "Doru spear", "Doru spear (damaged)", doruSpearMaterials, 117, "Optimatoi remains", Arrays.asList("Museum - Saradominist IV", "Saradominist IV"));
        Artifact kontosLance = new Artifact("artifact-192",
                "Kontos lance", "Kontos lance (damaged)", kontosLanceMaterials, 117, "Optimatoi remains", Arrays.asList("Museum - Saradominist IV", "Saradominist IV"));
        Artifact chuluuStone = new Artifact("artifact-193",
                "Chuluu stone", "Chuluu stone (damaged)", chuluuStoneMaterials, 118, "Howl's workshop debris", Arrays.asList("Armadylean III", "Magic Man", "Museum - Armadylean III"));
        Artifact quintessenceCounter = new Artifact("artifact-194",
                "Quintessence counter", "Quintessence counter (damaged)", quintessenceCounterMaterials, 118, "Howl's workshop debris", Arrays.asList("Armadylean III", "Museum - Armadylean III"));
        Artifact sphericalAstrolabe = new Artifact("artifact-195",
                "Spherical astrolabe", "Spherical astrolabe (damaged)", sphericalAstrolabeMaterials, 118, "Howl's workshop debris", Arrays.asList("Armadylean III", "Museum - Armadylean III"));
        Artifact ancientGlobe = new Artifact("artifact-196",
                "Ancient globe", "Ancient globe (damaged)", ancientGlobeMaterials, 118, "War table debris", Arrays.asList("Museum - Zarosian IV", "Zarosian IV"));
        Artifact battlePlans = new Artifact("artifact-197",
                "Battle plans", "Battle plans (damaged)", battlePlansMaterials, 118, "War table debris", Arrays.asList("Museum - Zarosian IV", "Zarosian IV"));
        Artifact primaLegioPainting = new Artifact("artifact-198",
                "'Prima Legio' painting", "'Prima Legio' painting (damaged)", primaLegioPaintingMaterials, 118, "War table debris", Arrays.asList("Imperial Impressionism", "Museum - Zarosian IV", "Zarosian IV"));
        Artifact daBossManSculpture = new Artifact("artifact-199",
                "'Da Boss Man' sculpture", "'Da Boss Man' sculpture (damaged)", daBossManSculptureMaterials, 119, "Makeshift pie oven", Arrays.asList("Green Gobbo Goodies III", "Knowledge is Power", "Museum - Bandosian III", "Red Rum Relics III"));
        Artifact horogothgarCookingPot = new Artifact("artifact-200",
                "Horogothgar cooking pot", "Horogothgar cooking pot (damaged)", horogothgarCookingPotMaterials, 119, "Makeshift pie oven", Arrays.asList("Green Gobbo Goodies III", "Museum - Bandosian III"));
        Artifact xoloShield = new Artifact("artifact-201","Xolo shield", "Xolo shield (damaged)", xoloShieldMaterials, 119, "Xolo remains", Arrays.asList("Dragonkin IV", "Museum - Dragonkin IV"));
        Artifact xoloSpear = new Artifact("artifact-202","Xolo spear", "Xolo spear (damaged)", xoloSpearMaterials, 119, "Xolo remains", Arrays.asList("Dragonkin IV", "Museum - Dragonkin IV"));
        Artifact goldDish = new Artifact("artifact-203","Gold dish", "Gold dish (damaged)", goldDishMaterials, 120, "Saurthen debris", Arrays.asList("Dragonkin IV", "Museum - Dragonkin IV"));
        Artifact rakshaIdol = new Artifact("artifact-204","'Raksha' idol", "'Raksha' idol (damaged)", rakshaIdolMaterials, 120, "Saurthen debris", Arrays.asList("Dragonkin IV", "Museum - Dragonkin IV"));








        return Arrays.asList(
                hookahPipe, opulentWineGoblet, crestOfDagon, disorderPainting, impMask, lesserDemonMask, greaterDemonMask, orderOfDisRobes, ritualDagger, venatorDagger, venatorLightCrossbow, primisElementisStandard, legionaryGladius, legionarySquareShield, zarosEffigy, zarosianTrainingDummy, orderOfDisRobes, ritualDagger, fryingPan, hallowedLantern, brandingIron, manacles,
                ancientTimepiece, legatusPendant, ceremonialUnicornOrnament, ceremonialUnicornSaddle, everlightHarp, everlightTrumpet, everlightViolin, foldedArmFigurineFemale, foldedArmFigurineMale, inciteFearSpellScroll, pontifexSignetRing, apexCap, curseTablet, funeraryUrnOfShadow,
                dominionDiscus, dominionJavelin, dominionPelteShield, infulaRobes, funeraryUrnOfSmoke, handOfTheAncients, decorativeAmphora, funeraryUrnOfIce, loarnabRod, inquisitorsCeremonialArmour, inquisitorsCeremonialMask, inquisitorsSeal,
                lakeOfFirePainting, lustMetalSculpture, gladiatorHelmet, gladiatorSword, funeraryUrnOfBlood, funeraryUrnOfMiasma, modelChariot, serpentsFallCarving, chaosStar, spikedDogCollar, bronzeDominionMedal,
                silverDominionMedal, dominionTorch, ikovianGerege, toyGlider, toyWarGolem, decorativeVase, kantharosCup, pateraBowl, castleGatestone, engravedRingOfKinship, ceremonialMace, pontifexMaximusFigurine,
                consensusAdIdemPainting, avianSongEggPlayer, keshikDrum, morinKhuur, ekeleshuunBlinderMask, narogoshuunHobDaGobBall, rekeshuunWarTether, exploratoryTotem, excavatorPortalMine, storageTotem, plantSeedSatchel, snuffBox,
                aviansieDreamcoat, ceremonialPlume, peacockingParasol, ogreKyzajAxe, orkCleaverSword, larupiaTrophy, lionTrophy, sheWolfTrophy, pontifexCenser, pontifexCrozier, pontifexMitre, thorobshuunBattleStandard, yurkolgokhStinkGrenade,
                dominarianDevice, fishingTrident, hawkeyeLensMultiVisionScope, talon3RazorWing, exsanguinateSpellScroll, necromanticFocus, spentSummoningCharm, friendshipBracelet, homelyTotem, highPriestCrozier, highPriestMitre, highPriestOrb,
                tormentMetalSculpture, pandemoniumTapestry, pasaha, ritualBell, prototypeGravimeter, songbirdRecorder, amphora, rodOfAsclepius, zarosianEwer, zarosianStein, beastkeeperHelm, idithuunHornRing,
                nosorogSculpture, dayguardShield, stormguardGerege, kilaya, vazara, ourgMegahitter, ourgTowerShield, garagorshuunAnchor, golemHeart, golemInstruction, hellfireHaladie, hellfireKatar, hellfireZaghnal, deathMask, dragonkinCalendar, dragonkinStaff,
                dorgeshuunSpear, forgedInWarSculpture, kopisDagger, xiphosShortSword, smokeCloudSpellScroll, vigoremVial, dragonScalpel, protectiveGoggles, dragonBurner, orthenglassFlask, blackfireLance, nightguardShield, projectionAttuner, goldenProjectionNeedle,
                huzamogaarbChaosCrown, saragorgakStarCrown, possessionMetalSculpture, trishula, tsutsarothPiercing, hallowedBeTheEverlightPainting, theLordOfLightPainting, thePrideOfPadosanPainting, meditationPipe, personalTotem, singingBowl, ancientMagicTablet,
                animateDeadSpellScroll, portablePhylactery, comfortGatestone, halaksCube, lingamStone, masterControl, theEnlightenedSoulScroll, theEudoxianElementsTablet, drogokishuunHookSword, hobgoblinMansticker, chaosElementalTrophy, viriusTrophy,
                flatCap, nightOwlFlightGoggles, prototypeGodbow, prototypeGodstaff, prototypeGodsword, xoloHardHat, xoloPickaxe, portablePortalGenerator, warpedTrinket, praetorianHood, praetorianRobes, praetorianStaff, kalikraChieftainCrown, kalikraMace,
                kalikraWarhorn, tsutsarothHelm, tsutsarothPauldron, tsutsarothUrumi, doruSpear, kontosLance, chuluuStone, quintessenceCounter, sphericalAstrolabe, ancientGlobe, battlePlans, primaLegioPainting, daBossManSculpture, horogothgarCookingPot, xoloShield,
                xoloSpear, goldDish, rakshaIdol, legatusMaximusFigurine, solemInUmbraPainting




        );
    }
}