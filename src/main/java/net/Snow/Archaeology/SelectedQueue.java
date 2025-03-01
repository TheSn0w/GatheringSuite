package net.Snow.Archaeology;

import net.Snow.Archaeology.ArtifactQueue.ArtifactQueue;
import net.Snow.Archaeology.ArtifactQueue.ArtifactsObtained;
import net.Snow.Archaeology.Collections.Artifact;
import net.Snow.Navigation;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.Snow.Archaeology.Archaeology.ArchaeologyState.EXCAVATING;
import static net.Snow.Archaeology.Archaeology.setBotState;
import static net.Snow.Archaeology.MaterialTypes.*;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.Navigation.MoveTo;

public class SelectedQueue {

    public static Map<String, ArtifactsObtained> artifactInfoMap = new ConcurrentHashMap<>();
    private static final double MAX_DISTANCE_TO_DIGSITE = 25.0;

    public static void handleTask(ArtifactQueue.ArtifactTask currentTask, List<ArtifactQueue.ArtifactTask> allTasks) {
        logAllTasks(allTasks, currentTask);  // Log all tasks in the queue with the current task highlighted

        // Update inventory map and check completion status for all tasks
        for (ArtifactQueue.ArtifactTask task : allTasks) {
            String taskArtifactName = task.getArtifact().getDamagedName();
            ArtifactsObtained taskArtifactInfo = getOrCreateArtifactInfo(taskArtifactName, task.getAmount());
            int taskCurrentCount = inventoryMap.getOrDefault(taskArtifactName, 0);
            taskArtifactInfo.setObtainedAmount(taskCurrentCount);

            // Mark the task as complete if the required amount is obtained
            if (taskArtifactInfo.getObtainedAmount() >= taskArtifactInfo.getRequiredAmount()) {
                task.setCompleted(true);
            }
        }

        // Now handle the current task as usual
        Artifact artifact = currentTask.getArtifact();
        String artifactName = artifact.getDamagedName();
//        logError("Current Task being processed:");
//        logError(" - Task: " + artifactName + ", Required Level: " + currentTask.getArtifact().getLevelRequired()
//                + ", Amount: " + currentTask.getAmount() + ", Completed: " + currentTask.isCompleted());

        int requiredLevel = artifact.getLevelRequired();
        int currentLevel = Skills.ARCHAEOLOGY.getLevel();
        logArtifactLevelInfo(artifactName, requiredLevel, currentLevel);

        // Check if the player meets the level requirement for this artifact
        if (currentLevel < requiredLevel) {
            logWarning(String.format("Player level (%d) does not meet the required level (%d) for current task: %s. Will retry later.", currentLevel, requiredLevel, artifactName));
            currentTask.setCompleted(false);  // Ensure the current task is not marked as complete
            return; // Skip this artifact but do not mark it as complete
        }

        // Get updated information for the current task
        ArtifactsObtained artifactInfo = getOrCreateArtifactInfo(artifactName, currentTask.getAmount());
        logGrey("Current count of " + artifactName + " collected: " + artifactInfo.getObtainedAmount() + " / " + artifactInfo.getRequiredAmount());

        // If the required amount is obtained, move to the next task
        if (artifactInfo.getObtainedAmount() >= artifactInfo.getRequiredAmount()) {
            logGrey("Collected required amount of current task: " + artifactName);
            currentTask.setCompleted(true);  // Mark the task as completed

            // Iterate through the remaining tasks to find the next incomplete one
            ArtifactQueue.ArtifactTask nextTask = null;
            for (int i = allTasks.indexOf(currentTask) + 1; i < allTasks.size(); i++) {
                if (!allTasks.get(i).isCompleted()) {
                    nextTask = allTasks.get(i);
                    break;
                }
            }

            if (nextTask != null) {
                String nextArtifactName = nextTask.getArtifact().getDamagedName();
                int nextRequiredLevel = nextTask.getArtifact().getLevelRequired();

                logGrey("Next task to process: " + nextArtifactName + " with required level: " + nextRequiredLevel);

                if (currentLevel < nextRequiredLevel) {
                    logWarning("Player level (" + currentLevel + ") does not meet the required level (" + nextRequiredLevel + ") for the next task: " + nextArtifactName + ". Resetting current task: " + artifactName + " to incomplete.");
                    currentTask.setCompleted(false);  // Reset the current task to "not complete"
                    logWarning("Continuing to work on task: " + artifactName + " until the required level for the next task is reached.");
                    setBotState(EXCAVATING);  // Continue working on the current task
                } else {
                    logGrey("Player level (" + currentLevel + ") meets the requirement for the next task: " + nextArtifactName + ". Preparing to move on.");
                    moveToArtifact(nextTask.getArtifact());  // Move to the correct excavation site for the next task
                    handleTask(nextTask, allTasks);  // Recursively handle the next task
                }
            } else {
                logError("No further tasks in queue.");
            }

        } else {
            logGrey("Continuing to collect artifact: " + artifactName);
            moveToArtifact(artifact);  // Move to the correct excavation site
            setBotState(EXCAVATING);  // Set state to EXCAVATING to start collecting
        }
    }





    private static void logAllTasks(List<ArtifactQueue.ArtifactTask> allTasks, ArtifactQueue.ArtifactTask currentTask) {
        ArtifactQueue.ArtifactTask nextTask = null;
        boolean foundCurrentTask = false;
        int currentLevel = Skills.ARCHAEOLOGY.getLevel();

        for (ArtifactQueue.ArtifactTask task : allTasks) {
            String taskArtifactName = task.getArtifact().getDamagedName();
            int taskRequiredLevel = task.getArtifact().getLevelRequired();
            int taskAmount = task.getAmount();
            boolean isCompleted = task.isCompleted();
//            log(" - Task: " + taskArtifactName + ", Required Level: " + taskRequiredLevel + ", Amount: " + taskAmount + ", Completed: " + isCompleted);

            if (foundCurrentTask && nextTask == null) {
                nextTask = task;
            }

            if (task.equals(currentTask)) {
                foundCurrentTask = true;
            }
        }

        if (nextTask != null) {
            String nextTaskArtifactName = nextTask.getArtifact().getDamagedName();
            int nextTaskRequiredLevel = nextTask.getArtifact().getLevelRequired();
            int nextTaskAmount = nextTask.getAmount();
            boolean canCompleteNextTask = currentLevel >= nextTaskRequiredLevel;
            log("Next task in queue: " + nextTaskArtifactName + ", Required Level: " + nextTaskRequiredLevel + ", Amount: " + nextTaskAmount + ", Can Complete: " + canCompleteNextTask);
        } else {
            logGrey("No next task in queue.");
        }
    }


    private static void moveToArtifact(Artifact artifact) {
        if (artifact == null) {
            log("No artifact provided. Exiting moveToArtifact.");
            return;
        }

        /*log("Checking excavation site for artifact: " + artifact.getDamagedName());*/
        Navigation nextHotspot = getExcavationTypeByName(artifact.getHotspot());

        if (nextHotspot != null) {
            int requiredIndex = ExcavationTypes.indexOf(nextHotspot);
            if (selectedExcavationIndex != requiredIndex) {
                if (!traverseToArtifact(nextHotspot)) {
                    log("Already near excavation site: " + nextHotspot.getName());
                }
                setExcavationTypeByName(artifact.getHotspot(), nextHotspot);
                selectedExcavationIndex = requiredIndex;
                log("Setting excavation type to: " + nextHotspot.getName() + " with index: " + selectedExcavationIndex);
            }
        } else {
            logError("No matching hotspot found for artifact: " + artifact.getDamagedName());
        }
    }







    // Logs the player's current Archaeology level and the required level for the artifact
    static void logArtifactLevelInfo(String artifactName, int requiredLevel, int currentLevel) {
        logGrey(String.format("Required level for %s: %d, Current level: %d", artifactName, requiredLevel, currentLevel));
        if (currentLevel >= requiredLevel) {
            logGrey(String.format("Player level meets the requirement for %s", artifactName));
        }
    }

    // Retrieves or creates artifact information (required and obtained amounts)
    static ArtifactsObtained getOrCreateArtifactInfo(String artifactName, int requiredAmount) {
        return artifactInfoMap.computeIfAbsent(artifactName, name -> new ArtifactsObtained(name, requiredAmount, 0));
    }


    // Traverses the player to the specified excavation site if they are not already near it
    private static boolean traverseToArtifact(Navigation nextHotspot) {
        LocalPlayer player = Client.getLocalPlayer();
        SceneObject selectedDigsiteObject = SceneObjectQuery.newQuery()
                .name(nextHotspot.getName())
                .results()
                .nearestTo(player.getCoordinate());

        if (selectedDigsiteObject == null || player.getCoordinate().distanceTo(selectedDigsiteObject.getCoordinate()) > MAX_DISTANCE_TO_DIGSITE) {
            log("Traversing to: " + nextHotspot.getName());
            MoveTo(nextHotspot.getCoordinate());
            nextHotspot.executeAdditionalSteps();
            return true; // Player is traversing to the site
        }
        return false; // Player is already near the site
    }

    // Finds the correct excavation site by name
    private static Navigation getExcavationTypeByName(String name) {
        return ExcavationTypes.stream()
                .filter(excavationType -> excavationType.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    logError("No excavation type found for name: " + name);
                    return null;
                });
    }
}