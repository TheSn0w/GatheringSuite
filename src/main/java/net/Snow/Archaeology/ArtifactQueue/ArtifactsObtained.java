package net.Snow.Archaeology.ArtifactQueue;

public class ArtifactsObtained {
    private String artifactName;
    private int requiredAmount;
    private int obtainedAmount;

    public ArtifactsObtained(String artifactName, int requiredAmount, int obtainedAmount) {
        this.artifactName = artifactName;
        this.requiredAmount = requiredAmount;
        this.obtainedAmount = obtainedAmount;
    }

    public void setRequiredAmount(int requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public void setObtainedAmount(int obtainedAmount) {
        this.obtainedAmount = obtainedAmount;
    }

    public int getObtainedAmount() {
        return obtainedAmount;
    }
}