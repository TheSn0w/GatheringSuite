package net.Snow.ImGui;

import net.botwithus.rs3.imgui.ImGui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomLogger {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final List<String> logMessages = new ArrayList<>();

    private static final String RESET = "\u001B[0m";
    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String MELLOW_GREEN = "\u001B[32;1m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String ORANGE = "\u001B[38;5;208m";
    private static final String MAGENTA = "\u001B[38;5;201m";
    private static final String LIME = "\u001B[38;5;10m";
    private static final String PINK = "\u001B[38;5;219m";
    private static final String GRAY = "\u001B[37m";



    public static void logError(String message) {
        log(RED + message + RESET);
    }

    public static void logWarning(String message) {
        log(YELLOW + message + RESET);
    }


    public static void logBlue(String message) {
        log(BLUE + message + RESET);
    }

    public static void logSuccess(String message) {
        log(MELLOW_GREEN + message + RESET);
    }

    public static void logGrey(String message) {
        log(GRAY + message + RESET);
    }

    public static void logOrange(String message) {
        log(ORANGE + message + RESET);
    }

    public static void logBlack(String message) {
        log(BLACK + message + RESET);
    }

    public static void logGreen(String message) {
        log(GREEN + message + RESET);
    }

    public static void logPurple(String message) {
        log(PURPLE + message + RESET);
    }

    public static void logCyan(String message) {
        log(CYAN + message + RESET);
    }

    public static void logMagenta(String message) {
        log(MAGENTA + message + RESET);
    }

    public static void logLime(String message) {
        log(LIME + message + RESET);
    }

    public static void logPink(String message) {
        log(PINK + message + RESET);
    }

    public static void log(String message) {
        LocalDateTime now = LocalDateTime.now();
        logMessages.add(dtf.format(now) + " - " + message);
    }

    public static List<String> getLogMessages() {
        return logMessages;
    }

    public static List<String> getErrorAndWarningMessages() {
        return logMessages.stream()
                .filter(msg -> msg.contains(RED) || msg.contains(YELLOW))
                .collect(Collectors.toList());
    }

    public static List<String> getMiscMessages() {
        return logMessages.stream()
                .filter(msg -> msg.contains(GRAY) || msg.contains(PURPLE))
                .collect(Collectors.toList());
    }

    public static List<String> getAllMessages() {
        return logMessages.stream()
                .filter(msg -> !msg.contains(GRAY) && !msg.contains(PURPLE))
                .collect(Collectors.toList());
    }

    public static void showTooltip(String tooltipText) {
        if (ImGui.IsItemHovered()) {
            ImGui.SetTooltip(tooltipText);
        }
    }
}