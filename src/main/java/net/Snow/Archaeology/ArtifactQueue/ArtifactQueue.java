package net.Snow.Archaeology.ArtifactQueue;

import net.Snow.Archaeology.Collections.Artifact;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static net.Snow.ImGui.CustomLogger.log;

public class ArtifactQueue {
    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise.
     */
    public static boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    /**
     * Removes the last artifact task in the queue.
     */
    public static void removeLastArtifactTask() {
        if (!queue.isEmpty()) {
            ArtifactTask removedTask = ((LinkedList<ArtifactTask>) queue).removeLast();
            log("Removed last artifact task: " + removedTask.getArtifact().getDamagedName());
        } else {
            log("Attempted to remove the last task, but the queue is empty.");
        }
    }

    /**
     * Removes a specific artifact task from the queue.
     *
     * @param task the ArtifactTask to remove.
     */
    public static void removeArtifactTask(ArtifactTask task) {
        if (queue.remove(task)) {
            log("Successfully removed artifact task: " + task.getArtifact().getDamagedName());
        } else {
            log("Failed to remove artifact task: " + task.getArtifact().getDamagedName() + ". It might not have been in the queue.");
        }
    }

    /**
     * Retrieves all artifact tasks currently in the queue.
     *
     * @return a list of all ArtifactTasks in the queue.
     */
    public static List<ArtifactTask> getAllArtifactTasks() {
        return new LinkedList<>(queue);
    }


    /**
     * Represents a single artifact task with an associated artifact and the required amount.
     */
    public static class ArtifactTask {
        private final Artifact artifact;
        private final int amount;
        private boolean completed; // New flag to indicate whether the task is completed

        public ArtifactTask(Artifact artifact, int amount) {
            this.artifact = artifact;
            this.amount = amount;
            this.completed = false;
        }

        public Artifact getArtifact() {
            return artifact;
        }

        public String getDamagedArtifactName() {
            return artifact.getDamagedName();
        }

        public int getAmount() {
            return amount;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
    private static Queue<ArtifactTask> queue = new LinkedList<>();

    public static ArtifactTask peekNextArtifactTask() {
        return queue.peek();
    }

    public static ArtifactTask getNextArtifactTask() {
        return queue.poll();
    }

    public static void addArtifactTask(ArtifactTask task) {
        queue.add(task);
    }

    public static void clearQueue() {
        queue.clear(); // Clears all items from the queue
    }

}
