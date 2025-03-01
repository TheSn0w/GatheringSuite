package net.Snow.Woodcutting;

import net.botwithus.rs3.util.Regex;

import java.util.regex.Pattern;

public class WoodcuttingVariables {

    public static final Pattern woodboxPattern = Regex.getPatternForContainingOneOf("Wood box", "wood box");
    public static final Pattern logPattern = Regex.getPatternForContainingOneOf("Logs", "logs");
    public static final Pattern PERFECT_JUJU_WOODCUTTING_POTION_PATTERN = Pattern.compile("Perfect juju woodcutting potion \\((1|2|3|4)\\)");}
