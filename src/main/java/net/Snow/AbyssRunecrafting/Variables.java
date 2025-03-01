package net.Snow.AbyssRunecrafting;

public class Variables {

    public static boolean craftNatureRunes = false;
    public static boolean craftBloodRunes = false;
    public static boolean craftCosmicRunes = false;
    public static boolean craftFireRunes = false;
    public static boolean craftEarthRunes = false;
    public static boolean craftMindRunes = false;
    public static boolean craftAirRunes = false;
    public static boolean craftWaterRunes = false;
    public static boolean craftDeathRunes = false;
    public static boolean craftLawRunes = false;
    public static boolean craftChaosRunes = false;


    public static String getRiftName() {
        if (craftNatureRunes) return "Nature rift";
        if (craftBloodRunes) return "Blood rift";
        if (craftCosmicRunes) return "Cosmic rift";
        if (craftFireRunes) return "Fire rift";
        if (craftEarthRunes) return "Earth rift";
        if (craftMindRunes) return "Mind rift";
        if (craftAirRunes) return "Air rift";
        if (craftWaterRunes) return "Water rift";
        if (craftDeathRunes) return "Death rift";
        if (craftLawRunes) return "Law rift";
        if (craftChaosRunes) return "Chaos rift";
        return "";
    }

    public static String getAltarName() {
        if (craftNatureRunes) return "Nature altar";
        if (craftBloodRunes) return "Blood altar";
        if (craftCosmicRunes) return "Cosmic altar";
        if (craftFireRunes) return "Fire altar";
        if (craftEarthRunes) return "Earth altar";
        if (craftMindRunes) return "Mind altar";
        if (craftAirRunes) return "Air altar";
        if (craftWaterRunes) return "Water altar";
        if (craftDeathRunes) return "Death altar";
        if (craftLawRunes) return "Law altar";
        if (craftChaosRunes) return "Chaos altar";
        return "";
    }
}
