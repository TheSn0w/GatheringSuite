package net.Snow.Rituals;

import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.minimenu.actions.NPCAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.List;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;

public class Distrubances {

    public static void handleDisturbances() {
        Execution.delay(defile());
        Execution.delay(shamblingHorror());
        Execution.delay(glyths());
        Execution.delay(soulStorm());
        Execution.delay(sparklingglyth());
        Execution.delay(ghost());
    }

    public static long glyths() {
        LocalPlayer player = Client.getLocalPlayer();
        List<Integer> npcTypeIds = new ArrayList<>();
        npcTypeIds.add(30495); // Glyth 1
        npcTypeIds.add(30496); // Glyth 2
        npcTypeIds.add(30497); // Glyth 3

        boolean interactedWithAllGlyths = false;

        for (Integer npcTypeId : npcTypeIds) {
            EntityResultSet<Npc> entities = NpcQuery.newQuery().byParentType(npcTypeId).results();

            if (!entities.isEmpty()) {
                Npc glythNpc = entities.first();
                if (glythNpc == null) {
                    continue;
                }

                Execution.delay(random.nextLong(200, 400));
                logBlue("Interacting with Glyth");
                glythNpc.interact("Deactivate");

                while (!entities.isEmpty()) {
                    entities = NpcQuery.newQuery().byParentType(npcTypeId).results();
                    Execution.delay(random.nextLong(50, 100));
                }

                if (npcTypeId.equals(30497) && entities.isEmpty()) {
                    interactedWithAllGlyths = true;
                }
            }
        }

        if (interactedWithAllGlyths) {
            EntityResultSet<SceneObject> pedestalEntities = SceneObjectQuery.newQuery().id(127316).option("Continue ritual").results();
            if (!pedestalEntities.isEmpty()) {
                SceneObject pedestal = pedestalEntities.first();
                if (pedestal != null) {
                    logSuccess("Continuing Ritual");
                    pedestal.interact("Continue ritual");
                    Execution.delayUntil(random.nextLong(8000, 10000), () -> !player.isMoving() && player.getAnimationId() == 35520);
                }
            }
        }

        return 0;
    }

    public static long ghost() {
        LocalPlayer player = Client.getLocalPlayer();
        int npcTypeId = 30493; // Ghost

        while (true) {
            EntityResultSet<Npc> entities = NpcQuery.newQuery().byParentType(npcTypeId).results();
            if (entities.isEmpty()) {
                break;
            }

            Npc ghostNpc = entities.nearest();
            if (ghostNpc == null) {
                break;
            }
            Execution.delay(random.nextLong(1025, 1300));

            logSuccess("Interacting with Ghost");
            boolean interacted = ghostNpc.interact("Dismiss");

            if (interacted) {
                Execution.delay(random.nextLong(1250, 1500)); // Delay for 1 second after interaction
                Execution.delayUntil(random.nextLong(10000, 12000), () -> !player.isMoving()); // Delay until player stops moving
            }

            EntityResultSet<SceneObject> pedestal = SceneObjectQuery.newQuery().id(127316).option("Continue ritual").results();
            if (!pedestal.isEmpty()) {
                logSuccess("Continuing Ritual");
                pedestal.first().interact("Continue ritual");
                Execution.delayUntil(random.nextLong(8000, 10000), () -> !player.isMoving() && player.getAnimationId() == 35520);
                Execution.delay(random.nextLong(3000, 5000));
            }
        }

        return 0;
    }


    public static long sparklingglyth() {
        LocalPlayer player = Client.getLocalPlayer();
        List<Integer> npcTypeIds = new ArrayList<>();
        npcTypeIds.add(30492); // Sparkling Glyth

        for (Integer npcTypeId : npcTypeIds) {
            EntityResultSet<Npc> entities = NpcQuery.newQuery().byType(npcTypeId).results();
            if (!entities.isEmpty()) {
                Npc sparklingGlythNpc = entities.first();
                if (sparklingGlythNpc != null) {
                    Execution.delay(random.nextLong(605, 989));
                    logSuccess("Interacting with Sparkling Glyth");
                    boolean interacted = sparklingGlythNpc.interact("Restore");

                    if (interacted) {
                        Execution.delay(random.nextLong(1250, 1500));
                        Execution.delayUntil(random.nextLong(10000, 12000), () -> player.getAnimationId() == 29914 || !player.isMoving());

                        EntityResultSet<SceneObject> pedestal = SceneObjectQuery.newQuery().id(127316).option("Continue ritual").results();
                        if (!pedestal.isEmpty()) {
                            logSuccess("Continuing Ritual");
                            pedestal.first().interact("Continue ritual");
                            Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getAnimationId() == 35520);
                            Execution.delay(random.nextLong(3000, 5000));
                        }
                    }
                }
            }
        }

        return 0;
    }

    public static long soulStorm() {
        LocalPlayer player = Client.getLocalPlayer();
        List<Integer> npcTypeIds = new ArrayList<>();
        npcTypeIds.add(30498); // Soul Storm
        npcTypeIds.add(30499); // Soul Storm

        for (Integer npcTypeId : npcTypeIds) {
            EntityResultSet<Npc> entities = NpcQuery.newQuery().byType(npcTypeId).results();

            while (!entities.isEmpty() && !player.isMoving()) {
                logSuccess("Interacting with Soul Storm");
                entities.first().interact("Dissipate");
                Execution.delay(random.nextLong(1800, 2200));
                entities = NpcQuery.newQuery().byType(npcTypeId).results();

                if (entities.isEmpty()) {
                    break;
                }
            }
        }

        return 0;
    }

    public static Npc getSoulstorm() {
        List<Integer> spotAnimations = List.of(7915, 7916, 7917);
        Npc soulStorm = null;

        for (Integer spotAnimation : spotAnimations) {
            soulStorm = NpcQuery.newQuery()
                    .spotAnimation(spotAnimation)
                    .results()
                    .first();
            if (soulStorm != null) {
                break;
            }
        }

        return soulStorm;
    }

    public static long shamblingHorror() {
        LocalPlayer player = Client.getLocalPlayer();
        int npcTypeId = 30494; // Shambling Horror

        while (true) {
            EntityResultSet<Npc> entities = NpcQuery.newQuery().byParentType(npcTypeId).results();
            if (entities.isEmpty()) {
                break;
            }

            Execution.delay(random.nextLong(1025, 1300));
            Npc shambingHorrorNpc = entities.first();

            logSuccess("Interacting with Shambling Horror");
            shambingHorrorNpc.interact("Sever link");

            Execution.delay(random.nextLong(1000, 1500));
            Execution.delayUntil(random.nextLong(10000, 12000), () -> !player.isMoving());

            Npc glow = getGlow();
            if (glow == null) {
                logWarning("Glow not found. Interacting with Shambling Horror again.");
                continue;
            }

            logSuccess("Found Glow. Interacting with Glow");
            boolean action;
            String name = glow.getName();
            if (name != null && name.contains("depleted")) {
                action = glow.interact(NPCAction.NPC3);
            } else {
                action = glow.interact(NPCAction.NPC1);
            }
            if (action) {
                logSuccess("Interaction with Glow complete.");
                Execution.delay(random.nextLong(600, 1250));
                EntityResultSet<SceneObject> pedestal = SceneObjectQuery.newQuery().id(127316).option("Continue ritual").results();
                if (!pedestal.isEmpty()) {
                    logSuccess("Continuing Ritual");
                    pedestal.first().interact("Continue ritual");
                    Execution.delayUntil(random.nextLong(8000, 10000), () -> {
                        if (!player.isMoving()) {
                            player.getAnimationId();
                        }
                        return false;
                    });
                }
            } else {
                logError("Failed to interact with Glow.");
            }
        }

        return 0;
    }

    public static Npc getGlow() {
        Npc glow = NpcQuery.newQuery()
                .spotAnimation(7977)
                .results()
                .first();
        if (glow == null) {
            glow = NpcQuery.newQuery()
                    .spotAnimation(6861)
                    .results()
                    .first();
        }
        return glow;
    }

    public static long defile() {
        LocalPlayer player = Client.getLocalPlayer();
        EntityResultSet<Npc> results = NpcQuery.newQuery().byType(30500).results();
        if (!results.isEmpty()) {
            Npc defileNpc = results.first();
            if (defileNpc == null) {
                return 0;
            }

            Execution.delay(random.nextLong(500, 1000));
            logSuccess("Siphoning the Defile");
            defileNpc.interact("Siphon");
            Execution.delayUntil(random.nextLong(10000, 15000), () -> !player.isMoving());

            boolean hasInteracted = false;
            while (!results.isEmpty()) {
                Npc light = getLight();
                if (light != null && !hasInteracted) {
                    Execution.delay(random.nextLong(400, 600));
                    logSuccess("Purple Smoke appeared, interacting with Defile again");
                    defileNpc.interact("Siphon");
                    hasInteracted = true;
                    Execution.delayUntil(random.nextLong(10000, 15000), () -> player.getAnimationId() == -1);
                } else if (light == null) {
                    hasInteracted = false;
                }
                results = NpcQuery.newQuery().byType(30500).results();
            }
        }
        return 0;
    }

    public static Npc getLight() {
        return NpcQuery.newQuery()
                .spotAnimation(7930)
                .results()
                .first();
    }
}
