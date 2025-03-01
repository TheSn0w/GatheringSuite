package net.Snow.Archaeology;

import net.Snow.Archaeology.ArtifactQueue.ArtifactQueue;
import net.Snow.Archaeology.ArtifactQueue.ArtifactsObtained;
import net.Snow.Archaeology.Collections.Collection;
import net.Snow.Archaeology.Collections.*;
import net.Snow.Archaeology.Mysteries.Mysteries;
import net.Snow.Archaeology.Mysteries.Mystery;
import net.Snow.ImGui.ImGuiCol;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.Headbar;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.Snow.Archaeology.Archaeology.ArchaeologyState.COLLECTIONS;
import static net.Snow.Archaeology.Archaeology.ArchaeologyState.EXCAVATING;
import static net.Snow.Archaeology.Archaeology.getBotState;
import static net.Snow.Archaeology.Archaeology.setBotState;
import static net.Snow.Archaeology.Collections.CheckArtifacts.*;
import static net.Snow.Archaeology.DropSoil.dropSoil;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.BankingImGui.bankingLocationsImGui;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.*;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Runecrafting.Variables.worldHop;

public class ArchaeologyGraphics {

    public static boolean useMaterialBuffs = false;
    public static boolean showNearbyExcavations = false;

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingArchaeologyLevel, AtomicInteger startingArchaeologyXP) throws IOException {
        ImGui.SetItemWidth(285.0F);

        // Excavation Type Combo
        int excavationResult = ImGui.Combo("##ExcavationType", MaterialTypes.selectedExcavationIndex, MaterialTypes.ExcavationTypes.stream().map(excavation -> excavation.getName() + " (" + excavation.getLocationName() + ")").toArray(String[]::new));
        showTooltip("Select the excavation type to work on, will move to if not nearby.");
        if (excavationResult != MaterialTypes.selectedExcavationIndex) {
            MaterialTypes.selectedExcavationIndex = excavationResult;
            if (MaterialTypes.selectedExcavationIndex > 0) {
                String selectedExcavation = MaterialTypes.ExcavationTypes.get(MaterialTypes.selectedExcavationIndex).getName();
                logBlue("Excavation selected: " + selectedExcavation);
                script.saveConfiguration();
            } else {
                logBlue("Excavation selection disabled.");
            }
        }

        bankingLocationsImGui();


        if (Mysteries.doMysteries) {
            ImGui.SeparatorText("Mystery Selection");

            // Mystery Selection Combo
            ImGui.SetItemWidth(285.0F);
            String[] mysteryNames = Mysteries.mysteryList.stream().map(Mystery::getName).toArray(String[]::new);
            int mysteryResult = ImGui.Combo("##MysterySelection", Mysteries.selectedMysteryIndex, mysteryNames);
            showTooltip("Select the mystery to work on.");
            if (mysteryResult != Mysteries.selectedMysteryIndex) {
                Mysteries.selectedMysteryIndex = mysteryResult;
                if (mysteryResult >= 0) {
                    String selectedMystery = Mysteries.mysteryList.get(mysteryResult).getName();
                    logBlue("Mystery selected: " + selectedMystery);
                    script.saveConfiguration();
                } else {
                    logBlue("Mystery selection disabled.");
                }
            }
        }
        ImGui.Separator();
        ImGui.Text("Botstate: " + getBotState().name());

        booleanOptions();

        if (showNearbyExcavations) {
            if (ImGui.Begin("Nearby Rocks", ImGuiWindowFlag.NoDecoration.getValue())) {
                ImGui.SetWindowSize((float) 400, (float) 175);
                List<List<String>> tableData = Scanner.getSceneObjectTableData();

                ImGui.SetItemWidth(600);

                if (ImGui.ListBoxHeader("", 385, 160)) {
                    ImGui.Columns(1, "SceneObject names", true);
                    for (int i = 0; i < tableData.size(); i++) {
                        List<String> row = tableData.get(i);
                        String rockName = row.get(0);

                        String sceneObjectIdentifier = rockName + "##" + i;

                        ImGui.Selectable(sceneObjectIdentifier, false, 0);
                        if (ImGui.IsItemClicked(ImGui.MouseButton.LEFT_BUTTON)) {
                            // Find the index of the selected excavation
                            int selectedIndex = MaterialTypes.ExcavationTypes.stream()
                                    .map(excavation -> excavation.getName())
                                    .collect(Collectors.toList())
                                    .indexOf(rockName);

                            if (selectedIndex != -1) {
                                MaterialTypes.selectedExcavationIndex = selectedIndex;
                                String selectedExcavation = MaterialTypes.ExcavationTypes.get(selectedIndex).getName();
                                logBlue("Excavation selected: " + selectedExcavation);
                                script.saveConfiguration();
                            } else {
                                logError("Selected rock not found in ExcavationTypes.");
                            }
                        }

                        ImGui.NextColumn();
                    }
                    ImGui.Columns(1, "Column", false);
                    ImGui.ListBoxFooter();
                }
            }
            ImGui.End();
        }

        displayHeadbarProgressBar(Client.getLocalPlayer());
        displayArtifactCounts();
        displayQueueInfo();
        displaySkillStats(Skills.ARCHAEOLOGY, startingArchaeologyLevel.get(), startingArchaeologyXP.get(), inventoryMap);


    }

    public static void displayHeadbarProgressBar(LocalPlayer player) {
        Optional<Headbar> headbarOpt = player.getHeadbars().stream()
                .filter(bar -> bar.getId() == 13 && bar.getWidth() < 255)
                .findAny();

        if (headbarOpt.isPresent()) {
            ImGui.Separator();
            ImGui.Text("Relic Progression");
            Headbar headbar = headbarOpt.get();
            int currentWidth = headbar.getWidth();
            float progress = (float) currentWidth / 255;

            float[][] colors = {
                    {1.0f, 0.0f, 0.0f, 1.0f}, {1.0f, 0.4f, 0.4f, 1.0f}, {1.0f, 0.6f, 0.0f, 1.0f},
                    {1.0f, 0.7f, 0.4f, 1.0f}, {1.0f, 1.0f, 0.0f, 1.0f}, {0.8f, 1.0f, 0.4f, 1.0f},
                    {0.6f, 1.0f, 0.6f, 1.0f}, {0.4f, 1.0f, 0.4f, 1.0f}, {0.3f, 0.9f, 0.3f, 1.0f},
                    {0.2f, 0.8f, 0.2f, 1.0f}, {0.1f, 0.7f, 0.1f, 1.0f}
            };

            int index = (int) (progress * 10);
            float blend = (progress * 10) - index;
            if (index >= colors.length - 1) {
                index = colors.length - 2;
                blend = 1;
            }
            float[] startColor = colors[index];
            float[] endColor = colors[index + 1];
            float[] currentColor = {
                    startColor[0] + blend * (endColor[0] - startColor[0]),
                    startColor[1] + blend * (endColor[1] - startColor[1]),
                    startColor[2] + blend * (endColor[2] - startColor[2]),
                    1.0f
            };

            ImGui.PushStyleColor(42, currentColor[0], currentColor[1], currentColor[2], currentColor[3]);
            ImGui.PushStyleColor(0, RGBToFloat(0), RGBToFloat(0), RGBToFloat(0), 0.0f);
            ImGui.ProgressBar(String.format("%.2f%%", progress * 100), progress, 285, 15);
            ImGui.PopStyleColor(2);
        }
    }

    public static int selectedArtifactIndex = 0;
    public static int artifactAmount = 1;

 /*   private static void displayArtifactSelection() {
        if (!useQueue) {
            return;
        }
        List<Artifact> artifacts = ArtifactRequirements.getAllArtifactRequirements();

        ImGui.SeparatorText("Select Artifact and Amount");

        // Combo box for selecting artifact
        ImGui.SetItemWidth(285.0F);

        String[] artifactNames = artifacts.stream().map(Artifact::getName).toArray(String[]::new);
        int artifactResult = ImGui.Combo("##Artifact", selectedArtifactIndex, artifactNames);
        showTooltip("Select the artifact to add to the queue.");
        if (artifactResult != selectedArtifactIndex) {
            selectedArtifactIndex = artifactResult;
            String selectedArtifactName = artifacts.get(selectedArtifactIndex).getName();
            logBlue("Artifact selected: " + selectedArtifactName);
        }

        ImGui.SetItemWidth(285.0F);
        int newArtifactAmount = ImGui.Slider("Amount", artifactAmount, 1, 10, 0);
        if (newArtifactAmount != artifactAmount) {
            artifactAmount = newArtifactAmount;
            logBlue("Artifact amount set to: " + artifactAmount);
        }

        if (ImGui.Button("Add to Queue")) {
            Artifact selectedArtifact = artifacts.get(selectedArtifactIndex);
            ArtifactQueue.addArtifactTask(new ArtifactQueue.ArtifactTask(selectedArtifact, artifactAmount));
            logBlue("Added " + artifactAmount + " of " + selectedArtifact.getName() + " to the queue.");
            logBlue("Hotspot location: " + selectedArtifact.getHotspot());

            // Update artifactInfoMap with the new artifact amount
            ArtifactsObtained artifactInfo = SelectedQueue.artifactInfoMap.get(selectedArtifact.getDamagedName());
            if (artifactInfo == null) {
                artifactInfo = new ArtifactsObtained(selectedArtifact.getDamagedName(), artifactAmount, 0);
            } else {
                artifactInfo.setRequiredAmount(artifactAmount);
            }
            SelectedQueue.artifactInfoMap.put(selectedArtifact.getDamagedName(), artifactInfo);
        }

        ImGui.SameLine();
        if (ImGui.Button("Remove Last from Queue")) {
            ArtifactQueue.removeLastArtifactTask();
            logBlue("Removed last artifact from the queue.");
        }
    }*/

    public static boolean useQueue = false;

    private static void displayQueueInfo() {
        if (!useQueue) {
            return;
        }

        // Set style color for the window background
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);

        if (ImGui.Begin("Incense Sticks Settings", ImGuiWindowFlag.NoDecoration.getValue() | ImGuiWindowFlag.NoResize.getValue())) {

            List<Artifact> artifacts = ArtifactRequirements.getAllArtifactRequirements();

            ImGui.SeparatorText("Select Artifact and Amount");

            // Combo box for selecting artifact
            ImGui.SetItemWidth(485.0F);

            String[] artifactNames = artifacts.stream().map(Artifact::getName).toArray(String[]::new);
            int artifactResult = ImGui.Combo("##Artifact", selectedArtifactIndex, artifactNames);
            showTooltip("Select the artifact to add to the queue.");
            if (artifactResult != selectedArtifactIndex) {
                selectedArtifactIndex = artifactResult;
                String selectedArtifactName = artifacts.get(selectedArtifactIndex).getName();
                logBlue("Artifact selected: " + selectedArtifactName);
            }

            ImGui.SetItemWidth(485.0F);
            int newArtifactAmount = ImGui.Slider("Amount", artifactAmount, 1, 20, 1);
            if (newArtifactAmount != artifactAmount) {
                artifactAmount = newArtifactAmount;
                logBlue("Artifact amount set to: " + artifactAmount);
            }


            if (ImGui.Button("Add to Queue")) {
                ImGui.SetItemWidth(240.0F);
                Artifact selectedArtifact = artifacts.get(selectedArtifactIndex);
                ArtifactQueue.addArtifactTask(new ArtifactQueue.ArtifactTask(selectedArtifact, artifactAmount));
                logBlue("Added " + artifactAmount + " of " + selectedArtifact.getName() + " to the queue.");
                logBlue("Hotspot location: " + selectedArtifact.getHotspot());

                // Update artifactInfoMap with the new artifact amount
                ArtifactsObtained artifactInfo = SelectedQueue.artifactInfoMap.get(selectedArtifact.getDamagedName());
                if (artifactInfo == null) {
                    artifactInfo = new ArtifactsObtained(selectedArtifact.getDamagedName(), artifactAmount, 0);
                } else {
                    artifactInfo.setRequiredAmount(artifactAmount);
                }
                SelectedQueue.artifactInfoMap.put(selectedArtifact.getDamagedName(), artifactInfo);
            }

            // Begin a new ImGui window
            ImGui.SetWindowSize(500, 300); // Adjust window size for better visibility
            ImGui.SeparatorText("Artifact Queue");

            // Adjust the width for the items
            ImGui.SetItemWidth(485);

            // Correct the ListBoxHeader call with fixed height
            if (ImGui.ListBoxHeader("##ArtifactQueue", 485, 120)) { // Set width and height for ListBoxHeader
                // Begin column setup
                ImGui.Columns(3, "ArtifactQueue", true);
                ImGui.Text("Artifact Name"); // Header for the first column
                ImGui.NextColumn();
                ImGui.Text("Obtained"); // Header for the second column
                ImGui.NextColumn();
                ImGui.Text("Actions"); // Header for the third column
                ImGui.NextColumn();
                ImGui.Separator(); // Separator for column headers

                // Iterate over all artifact tasks in the queue
                for (ArtifactQueue.ArtifactTask task : ArtifactQueue.getAllArtifactTasks()) {
                    Artifact artifact = task.getArtifact();
                    ArtifactsObtained artifactInfo = SelectedQueue.artifactInfoMap.get(artifact.getDamagedName());

                    if (artifactInfo == null) {
                        artifactInfo = new ArtifactsObtained(artifact.getDamagedName(), task.getAmount(), 0);
                        SelectedQueue.artifactInfoMap.put(artifact.getDamagedName(), artifactInfo);
                    }

                    int requiredAmount = artifactInfo.getRequiredAmount();
                    int obtainedAmount = artifactInfo.getObtainedAmount();

                    // Display the correct artifact name under "Artifact Name"
                    ImGui.Text(artifact.getName());  // Use artifact.getName() to display the correct name
                    ImGui.NextColumn();

                    // Display obtained / required amount
                    ImGui.Text(obtainedAmount + " / " + requiredAmount);
                    ImGui.NextColumn();

                    // Display "Remove" button
                    if (ImGui.Button("Remove##" + artifact.getDamagedName())) {
                        ArtifactQueue.removeArtifactTask(task); // Remove the artifact from the queue
                        break;
                    }

                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Click to remove this artifact from the queue");
                    }

                    ImGui.NextColumn(); // Move to the next row
                    ImGui.Separator(); // Separator for each line
                }

                ImGui.Columns(1, "method", false); // Reset to a single column
                ImGui.ListBoxFooter(); // End list box
            }
            ImGui.PopStyleColor();
            ImGui.End(); // End the window
        }
    }

    private static final int REQUIRED_ARTIFACT_COUNT = 1; // Assuming required count is 1

    private static void displayArtifactCounts() {
        if (!doCollections) {
            CollectionTypes.selectedCollectionIndex = 0;
            return;
        }

        // Set style color for the window background
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);

        // Begin a new window for artifact counts
        if (ImGui.Begin("Artifact Collection Status", ImGuiWindowFlag.NoScrollbar.getValue() | ImGuiWindowFlag.NoResize.getValue())) {
            ImGui.SetWindowSize(500.f, 600.f);

            displayCollectionSelection();

            displayCurrentAndNextTargetArtifacts();

            displayArtifactCollectionQueue();

            displayCollectionQueueActions();
        }
        ImGui.PopStyleColor();
        ImGui.End(); // End the window
    }

    private static void displayCollectionSelection() {
        ImGui.SeparatorText("Collection Name");
        ImGui.SetItemWidth(485.0F);

        int playerArchaeologyLevel = Skills.ARCHAEOLOGY.getLevel();

        List<Collection> availableCollections = CollectionTypes.collections.stream()
                .filter(collection -> collection.getLevelRequired() <= playerArchaeologyLevel)
                .toList();

        String[] collectionNames = availableCollections.stream()
                .map(Collection::getName)
                .toArray(String[]::new);

        // Combo box for selecting collection
        int collectionResult = ImGui.Combo("##CollectionName", CollectionTypes.selectedCollectionIndex, collectionNames);
        showTooltip("Select the collection name to work on.");
        if (collectionResult != CollectionTypes.selectedCollectionIndex) {
            CollectionTypes.selectedCollectionIndex = collectionResult;
            if (CollectionTypes.selectedCollectionIndex >= 0 && CollectionTypes.selectedCollectionIndex < availableCollections.size()) {
                String selectedCollection = availableCollections.get(CollectionTypes.selectedCollectionIndex).getName();
                logBlue("Collection selected: " + selectedCollection);
                MaterialTypes.addCollectionToQueue(selectedCollection);
            } else {
                logBlue("Collection selection disabled.");
            }
        }
    }

    private static void displayCurrentAndNextTargetArtifacts() {
        ImGui.SeparatorText("Target Artifacts and Locations");
        if (!missingArtifacts.isEmpty()) {
            Artifact currentArtifact = missingArtifacts.get(0); // Get the current artifact to collect
            String currentHotspot = currentArtifact.getHotspot(); // Get the location of the current artifact

            ImGui.Text("Current Artifact: " + currentArtifact.getName());
            ImGui.Text("Current Location: " + currentHotspot);
            ImGui.Separator();

            // Check if there's a next artifact
            if (missingArtifacts.size() > 1) {
                Artifact nextArtifact = missingArtifacts.get(1); // Get the next artifact to collect
                String nextHotspot = nextArtifact.getHotspot(); // Get the location of the next artifact

                ImGui.Text("Next Artifact: " + nextArtifact.getName());
                ImGui.Text("Next Location: " + nextHotspot);
            } else {
                ImGui.Text("Next Artifact: None");
                ImGui.Text("Next Location: None");
            }
        } else {
            ImGui.Text("No artifacts currently missing.");
        }
    }

    private static List<Artifact> getRequiredArtifactsForCollection(String collectionName) {
        return ArtifactRequirements.getAllArtifactRequirements().stream()
                .filter(artifact -> artifact.getCollectionNames().contains(collectionName))
                .distinct()
                .collect(Collectors.toList());
    }

    private static void displayCurrentTargetArtifact() {
        if (!missingArtifacts.isEmpty()) {
            Artifact currentTarget = missingArtifacts.get(0);
            ImGui.Text("Currently Targeting: " + currentTarget.getName());
            ImGui.Separator();
        }
    }

    private static void displayArtifactCollectionQueue() {
        ImGui.SeparatorText("Artifact Collection Queue");
        if (ImGui.ListBoxHeader("##ArtifactCollectionQueue", 485, 200)) {
            for (String collectionName : MaterialTypes.collectionQueue) {
                ImGui.SeparatorText("Queued Collection: " + collectionName);

                List<Artifact> requiredArtifacts = getRequiredArtifactsForCollection(collectionName);

                // Display the current target artifact
                displayCurrentTargetArtifact();

                // Display the artifact grid
                displayArtifactGrid(requiredArtifacts);

                // Determine collection status
                boolean allComplete = isCollectionComplete(requiredArtifacts);
                String collectionStatus = allComplete ? "Complete" : "Incomplete";
                ImGui.Text("Collection Status: " + collectionStatus);
            }

            ImGui.ListBoxFooter();
        }
    }

    private static void displayArtifactGrid(List<Artifact> requiredArtifacts) {
        ImGui.Columns(4, "ArtifactGrid", false);
        ImGui.Text("Artifact Name");
        ImGui.NextColumn();
        ImGui.Text("Damaged Count");
        ImGui.NextColumn();
        ImGui.Text("Repaired Count");
        ImGui.NextColumn();
        ImGui.Text("Total Count");
        ImGui.NextColumn();
        ImGui.Separator();

        for (Artifact artifact : requiredArtifacts) {
            displayArtifactRow(artifact);
            ImGui.Separator();
        }

        ImGui.Columns(1, "Method", false);
    }

    private static void displayArtifactRow(Artifact artifact) {
        int damagedCount = damagedArtifactCounts.getOrDefault(artifact.getId(), 0);
        int undamagedCount = undamagedArtifactCounts.getOrDefault(artifact.getId(), 0);
        long totalCount = damagedCount + undamagedCount;

        boolean isMissing = missingArtifacts.contains(artifact);

        String status = isMissing ? " (Targeting)" : "";

        // Display artifact name
        ImGui.Text(artifact.getName());
        ImGui.NextColumn();

        // Display damaged count
        ImGui.Text(String.valueOf(damagedCount));
        ImGui.NextColumn();

        // Display repaired count
        ImGui.Text(String.valueOf(undamagedCount));
        ImGui.NextColumn();

        // Display total count with status
        setStyleColorForCount(totalCount);
        ImGui.Text(totalCount + status);
        ImGui.PopStyleColor();
        ImGui.NextColumn();
    }

    private static boolean isCollectionComplete(List<Artifact> requiredArtifacts) {
        return requiredArtifacts.stream()
                .allMatch(artifact -> {
                    int damagedCount = damagedArtifactCounts.getOrDefault(artifact.getId(), 0);
                    int undamagedCount = undamagedArtifactCounts.getOrDefault(artifact.getId(), 0);
                    long totalCount = damagedCount + undamagedCount;
                    return totalCount >= requiredCount;
                });
    }

    private static void setStyleColorForCount(long totalCount) {
        if (totalCount == 0) {
            setStyleColor(ImGuiCol.Text, 255, 0, 0, 255); // Red
        } else {
            setStyleColor(ImGuiCol.Text, 0, 128, 0, 255); // Green
        }
    }

    private static void displayCollectionQueueActions() {
        ImGui.SeparatorText("Collection Queue");
        if (ImGui.ListBoxHeader("##CollectionQueueList", 485, 205)) { // Set width and height for ListBoxHeader
            // Create a 3-column grid layout
            ImGui.Columns(3, "CollectionQueueGrid", true);

            // Headers for the grid
            ImGui.Text("Collection Name");
            ImGui.NextColumn();
            ImGui.Text("Status");
            ImGui.NextColumn();
            ImGui.Text("Actions");
            ImGui.NextColumn();
            ImGui.Separator(); // Separator between headers and content

            // Create a copy to avoid ConcurrentModificationException
            List<String> collectionQueueCopy = new ArrayList<>(MaterialTypes.collectionQueue);

            // Iterate over each collection in the queue
            for (String collectionNameInQueue : collectionQueueCopy) {
                displayCollectionQueueRow(collectionNameInQueue);
            }

            ImGui.Columns(1, "method", false); // Reset to a single column
            ImGui.ListBoxFooter(); // End list box for the collection queue
        }
    }

    private static void displayCollectionQueueRow(String collectionNameInQueue) {
        // Display the collection name
        ImGui.Text(collectionNameInQueue);
        ImGui.NextColumn();

        // Determine if the collection is complete and set the color accordingly
        boolean isComplete = checkIfCollectionComplete(collectionNameInQueue);
        if (isComplete) {
            setStyleColor(ImGuiCol.Text, 0, 128, 0, 255); // Green color for "Complete"
            ImGui.Text("Complete");
        } else {
            setStyleColor(ImGuiCol.Text, 255, 0, 0, 255); // Red color for "Incomplete"
            ImGui.Text("Incomplete");
        }
        ImGui.PopStyleColor(); // Reset to the default color after setting the status color
        ImGui.NextColumn();

        // "Remove" button for each collection
        if (ImGui.Button("Remove##" + collectionNameInQueue)) {
            MaterialTypes.removeCollectionFromQueue(collectionNameInQueue);
            // Since we're iterating over a copy, no need to break
        }

        if (ImGui.IsItemHovered()) {
            ImGui.SetTooltip("Click to remove this collection");
        }

        ImGui.NextColumn(); // Move to the next row
        ImGui.Separator(); // Separator for each line
    }

    private static boolean checkIfCollectionComplete(String collectionName) {
        Set<String> uniqueArtifacts = new HashSet<>();
        List<Artifact> requiredArtifacts = ArtifactRequirements.getAllArtifactRequirements().stream()
                .filter(artifact -> artifact.getCollectionNames().contains(collectionName))
                .filter(artifact -> uniqueArtifacts.add(artifact.getId())) // Ensure uniqueness
                .toList();

        for (Artifact artifact : requiredArtifacts) {
            int damagedCount = damagedArtifactCounts.getOrDefault(artifact.getId(), 0);
            int undamagedCount = undamagedArtifactCounts.getOrDefault(artifact.getId(), 0);
            long totalCount = damagedCount + undamagedCount;

            if (totalCount < REQUIRED_ARTIFACT_COUNT) {
                return false;
            }
        }
        return true;
    }



//    private static void displayArtifactCounts() {
//        if (!doCollections) {
//            CollectionTypes.selectedCollectionIndex = 0;
//            return;
//        }
//
//        // Set style color for the window background
//        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);
//
//        // Begin a new window for artifact counts
//        if (ImGui.Begin("Artifact Collection Status", ImGuiWindowFlag.NoScrollbar.getValue() | ImGuiWindowFlag.NoResize.getValue())) {
//            ImGui.SetWindowSize(500.f, 600.f);
//            ImGui.SeparatorText("Collection Name");
//
//            ImGui.SetItemWidth(485.0F);
//
//            int playerArchaeologyLevel = Skills.ARCHAEOLOGY.getLevel();
//
//            List<Collection> availableCollections = CollectionTypes.collections.stream()
//                    .filter(collection -> collection.getLevelRequired() <= playerArchaeologyLevel)
//                    .toList();
//
//            // Combo box for selecting collection
//            int collectionResult = ImGui.Combo("##CollectionName", CollectionTypes.selectedCollectionIndex, availableCollections.stream().map(Collection::getName).toArray(String[]::new));
//            showTooltip("Select the collection name to work on.");
//            if (collectionResult != CollectionTypes.selectedCollectionIndex) {
//                CollectionTypes.selectedCollectionIndex = collectionResult;
//                if (CollectionTypes.selectedCollectionIndex >= 0) {
//                    String selectedCollection = availableCollections.get(CollectionTypes.selectedCollectionIndex).getName();
//                    logBlue("Collection selected: " + selectedCollection);
//                    MaterialTypes.addCollectionToQueue(selectedCollection);
//                } else {
//                    logBlue("Collection selection disabled.");
//                }
//            }
//
//            // Display current and next target artifact and location
//            ImGui.SeparatorText("Target Artifacts and Locations");
//            if (!missingArtifacts.isEmpty()) {
//                Artifact currentArtifact = missingArtifacts.get(0); // Get the current artifact to collect
//                String currentHotspot = currentArtifact.getHotspot(); // Get the location of the current artifact
//
//                ImGui.Text("Current Artifact: " + currentArtifact.getName());
//                ImGui.Text("Current Location: " + currentHotspot);
//                ImGui.Separator();
//
//                // Check if there's a next artifact
//                if (missingArtifacts.size() > 1) {
//                    Artifact nextArtifact = missingArtifacts.get(1); // Get the next artifact to collect
//                    String nextHotspot = nextArtifact.getHotspot(); // Get the location of the next artifact
//
//                    ImGui.Text("Next Artifact: " + nextArtifact.getName());
//                    ImGui.Text("Next Location: " + nextHotspot);
//                } else {
//                    ImGui.Text("Next Artifact: None");
//                    ImGui.Text("Next Location: None");
//                }
//            } else {
//                ImGui.Text("No artifacts currently missing.");
//            }
//
//            // Display current collection queue in ListBoxHeader
//            ImGui.SeparatorText("Artifact Collection Queue");
//            if (ImGui.ListBoxHeader("##ArtifactCollectionQueue", 485, 200)) { // Set width and height for ListBoxHeader
//                // Iterate over the collection queue
//                for (String collectionName : MaterialTypes.collectionQueue) {
//                    ImGui.SeparatorText("Queued Collection: " + collectionName);
//
//                    Set<String> uniqueArtifacts = new HashSet<>();
//                    List<Artifact> requiredArtifacts = ArtifactRequirements.getAllArtifactRequirements().stream()
//                            .filter(artifact -> artifact.getCollectionNames().contains(collectionName))
//                            .filter(artifact -> uniqueArtifacts.add(artifact.getId())) // Ensure uniqueness
//                            .toList();
//
//                    // Display the current target artifact
//                    if (!missingArtifacts.isEmpty()) {
//                        Artifact currentTarget = missingArtifacts.get(0);
//                        ImGui.Text("Currently Targeting: " + currentTarget.getName());
//                        ImGui.Separator();
//                    }
//
//                    // Set up a grid for displaying artifact information
//                    ImGui.Columns(4, "ArtifactGrid", false); // Create a 4-column layout
//                    ImGui.Text("Artifact Name"); // Header for the first column
//                    ImGui.NextColumn();
//                    ImGui.Text("Damaged Count"); // Header for the second column
//                    ImGui.NextColumn();
//                    ImGui.Text("Repaired Count"); // Header for the third column
//                    ImGui.NextColumn();
//                    ImGui.Text("Total Count"); // Header for the fourth column
//                    ImGui.NextColumn();
//                    ImGui.Separator(); // Divider between headers and content
//
//                    boolean allComplete = true;
//                    for (Artifact artifact : requiredArtifacts) {
//                        // Retrieve the counts from the public maps
//                        int damagedCount = damagedArtifactCounts.getOrDefault(artifact.getId(), 0);
//                        int undamagedCount = undamagedArtifactCounts.getOrDefault(artifact.getId(), 0);
//                        long totalCount = damagedCount + undamagedCount;
//
//                        boolean isMissing = missingArtifacts.stream()
//                                .anyMatch(missingArtifact -> missingArtifact.getId().equals(artifact.getId()));
//
//                        // Determine the status for display next to the count
//                        String status = "";
//                        if (totalCount == 0 || isMissing) {
//                            status = " (Targeting)";
//                        }
//
//                        // Display artifact name
//                        ImGui.Text(artifact.getName());
//                        ImGui.NextColumn();
//
//                        // Display damaged count
//                        ImGui.Text(String.valueOf(damagedCount));
//                        ImGui.NextColumn();
//
//                        // Display non-damaged count
//                        ImGui.Text(String.valueOf(undamagedCount));
//                        ImGui.NextColumn();
//
//                        // Display total count with status
//                        if (totalCount == 0) {
//                            setStyleColor(ImGuiCol.Text, 255, 0, 0, 255); // Red color for count = 0
//                        } else {
//                            setStyleColor(ImGuiCol.Text, 0, 128, 0, 255); // Green color for count > 0
//                        }
//
//                        ImGui.Text(totalCount + status);
//                        ImGui.PopStyleColor(); // Reset color after displaying count
//                        ImGui.NextColumn();
//
//                        ImGui.Separator(); // Separator for each line
//
//                        if (totalCount < requiredCount) {
//                            allComplete = false;
//                        }
//                    }
//
//                    ImGui.Columns(1, "method", false); // Reset to a single column
//                    String collectionStatus = allComplete ? "Complete" : "Incomplete";
//                    ImGui.Text("Collection Status: " + collectionStatus);
//                }
//
//                ImGui.ListBoxFooter(); // End the ListBoxHeader
//            }
//
//            // Display Collection Queue actions
//            ImGui.SeparatorText("Collection Queue");
//            if (ImGui.ListBoxHeader("##CollectionQueueList", 485, 205)) { // Set width and height for ListBoxHeader
//                // Create a 3-column grid layout
//                ImGui.Columns(3, "CollectionQueueGrid", true);
//
//                // Headers for the grid
//                ImGui.Text("Collection Name");
//                ImGui.NextColumn();
//                ImGui.Text("Status");
//                ImGui.NextColumn();
//                ImGui.Text("Actions");
//                ImGui.NextColumn();
//                ImGui.Separator(); // Separator between headers and content
//
//                // Iterate over each collection in the queue
//                for (String collectionNameInQueue : MaterialTypes.collectionQueue) {
//                    // Display the collection name
//                    ImGui.Text(collectionNameInQueue);
//                    ImGui.NextColumn();
//
//                    // Determine if the collection is complete and set the color accordingly
//                    boolean isComplete = checkIfCollectionComplete(collectionNameInQueue);
//                    if (isComplete) {
//                        setStyleColor(ImGuiCol.Text, 0, 128, 0, 255); // Green color for "Complete"
//                        ImGui.Text("Complete");
//                    } else {
//                        setStyleColor(ImGuiCol.Text, 255, 0, 0, 255); // Red color for "Incomplete"
//                        ImGui.Text("Incomplete");
//                    }
//                    ImGui.PopStyleColor(); // Reset to the default color after setting the status color
//                    ImGui.NextColumn();
//
//                    // "Remove" button for each collection
//                    if (ImGui.Button("Remove##" + collectionNameInQueue)) {
//                        MaterialTypes.removeCollectionFromQueue(collectionNameInQueue);
//                        break;
//                    }
//
//                    if (ImGui.IsItemHovered()) {
//                        ImGui.SetTooltip("Click to remove this collection");
//                    }
//
//                    ImGui.NextColumn(); // Move to the next row
//                    ImGui.Separator(); // Separator for each line
//                }
//
//                ImGui.Columns(1, "method", false); // Reset to a single column
//                ImGui.ListBoxFooter(); // End list box for the collection queue
//            }
//
//        }
//        ImGui.PopStyleColor();
//        ImGui.End(); // End the window
//    }






    public static boolean doCollections = false;

    private static void booleanOptions() {
        createCenteredButton("Collections", () -> {
            doCollections = !doCollections;
            if (doCollections) {
                setBotState(COLLECTIONS);
            }
        }, doCollections, 138.0f);
        showTooltip("Will do Collections based on what you select.");

        ImGui.SameLine();

        createCenteredButton("Queue", () -> {
            useQueue = !useQueue;
            if (useQueue) {
                setBotState(EXCAVATING);
            }
        }, useQueue, 138.0f);
        showTooltip("Will work off a queue of Set Artifacts and theyre amounts.");


        createCenteredButton("Porters", () -> usePorters = !usePorters, usePorters, 138.0f);
        showTooltip("Enable or disable the use of porters/Grace of the Elves.");

        ImGui.SameLine();

        createCenteredButton("Drop Soil", () -> dropSoil = !dropSoil, dropSoil, 138.0f);
        showTooltip("Will drop Soil when backpack is full instead of going to bank or filling Archaeological box.");


        createCenteredButton("Show Collections", CollectionManager::logAllCollectionsStatus, false, 138.0f);
        showTooltip("Logs the completion status of all collections.");

        ImGui.SameLine();

        createCenteredButton("Hop Worlds", () -> worldHop = !worldHop, worldHop, 138.0f);
        showTooltip("Hops Worlds when Material Caches are depleted");

        createCenteredButton("Buffs", () -> useMaterialBuffs = !useMaterialBuffs, useMaterialBuffs, 138.0f);
        showTooltip("Will use the following if in Backpack, Tarpaulin sheet, Arch tea, Material Manual, Hi-spec monocle.");

        ImGui.SameLine();

        createCenteredButton("Familiar", () -> useFamiliar = !useFamiliar, useFamiliar, 138.0f);
        showTooltip("Enable or disable the use of a familiar (Waterfiend).");

        createCenteredButton("Nearby", () -> showNearbyExcavations = !showNearbyExcavations, showNearbyExcavations, 138.0f);
        showTooltip("Show nearby excavations in the area.");
    }

}
