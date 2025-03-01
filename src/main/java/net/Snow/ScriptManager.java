package net.Snow;

// ScriptManager.java
public class ScriptManager {
    private static Snow currentScript;

    public static Snow getCurrentScript() {
        return currentScript;
    }

    public static void setCurrentScript(Snow script) {
        currentScript = script;
    }
}