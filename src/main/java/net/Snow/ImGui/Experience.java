package net.Snow.ImGui;

import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import static net.Snow.ImGui.SnowsTemplateGraphics.scriptStartTime;

public class Experience {

    public static float RGBToFloat(int value) {
        return value / 255.0f;
    }


    private static final Map<Integer, Integer> EXTENDED_LEVEL_XP = new HashMap<>();

    static {
        EXTENDED_LEVEL_XP.put(1, 1);
        EXTENDED_LEVEL_XP.put(2, 83);
        EXTENDED_LEVEL_XP.put(3, 174);
        EXTENDED_LEVEL_XP.put(4, 276);
        EXTENDED_LEVEL_XP.put(5, 388);
        EXTENDED_LEVEL_XP.put(6, 512);
        EXTENDED_LEVEL_XP.put(7, 650);
        EXTENDED_LEVEL_XP.put(8, 801);
        EXTENDED_LEVEL_XP.put(9, 969);
        EXTENDED_LEVEL_XP.put(10, 1154);
        EXTENDED_LEVEL_XP.put(11, 1358);
        EXTENDED_LEVEL_XP.put(12, 1584);
        EXTENDED_LEVEL_XP.put(13, 1833);
        EXTENDED_LEVEL_XP.put(14, 2107);
        EXTENDED_LEVEL_XP.put(15, 2411);
        EXTENDED_LEVEL_XP.put(16, 2746);
        EXTENDED_LEVEL_XP.put(17, 3115);
        EXTENDED_LEVEL_XP.put(18, 3523);
        EXTENDED_LEVEL_XP.put(19, 3973);
        EXTENDED_LEVEL_XP.put(20, 4470);
        EXTENDED_LEVEL_XP.put(21, 5018);
        EXTENDED_LEVEL_XP.put(22, 5624);
        EXTENDED_LEVEL_XP.put(23, 6291);
        EXTENDED_LEVEL_XP.put(24, 7028);
        EXTENDED_LEVEL_XP.put(25, 7842);
        EXTENDED_LEVEL_XP.put(26, 8740);
        EXTENDED_LEVEL_XP.put(27, 9730);
        EXTENDED_LEVEL_XP.put(28, 10824);
        EXTENDED_LEVEL_XP.put(29, 12031);
        EXTENDED_LEVEL_XP.put(30, 13363);
        EXTENDED_LEVEL_XP.put(31, 14833);
        EXTENDED_LEVEL_XP.put(32, 16456);
        EXTENDED_LEVEL_XP.put(33, 18247);
        EXTENDED_LEVEL_XP.put(34, 20224);
        EXTENDED_LEVEL_XP.put(35, 22406);
        EXTENDED_LEVEL_XP.put(36, 24815);
        EXTENDED_LEVEL_XP.put(37, 27473);
        EXTENDED_LEVEL_XP.put(38, 30408);
        EXTENDED_LEVEL_XP.put(39, 33648);
        EXTENDED_LEVEL_XP.put(40, 37224);
        EXTENDED_LEVEL_XP.put(41, 41171);
        EXTENDED_LEVEL_XP.put(42, 45529);
        EXTENDED_LEVEL_XP.put(43, 50339);
        EXTENDED_LEVEL_XP.put(44, 55649);
        EXTENDED_LEVEL_XP.put(45, 61512);
        EXTENDED_LEVEL_XP.put(46, 67983);
        EXTENDED_LEVEL_XP.put(47, 75127);
        EXTENDED_LEVEL_XP.put(48, 83014);
        EXTENDED_LEVEL_XP.put(49, 91721);
        EXTENDED_LEVEL_XP.put(50, 101333);
        EXTENDED_LEVEL_XP.put(51, 111945);
        EXTENDED_LEVEL_XP.put(52, 123660);
        EXTENDED_LEVEL_XP.put(53, 136594);
        EXTENDED_LEVEL_XP.put(54, 150872);
        EXTENDED_LEVEL_XP.put(55, 166636);
        EXTENDED_LEVEL_XP.put(56, 184040);
        EXTENDED_LEVEL_XP.put(57, 203254);
        EXTENDED_LEVEL_XP.put(58, 224466);
        EXTENDED_LEVEL_XP.put(59, 247886);
        EXTENDED_LEVEL_XP.put(60, 273742);
        EXTENDED_LEVEL_XP.put(61, 302288);
        EXTENDED_LEVEL_XP.put(62, 333804);
        EXTENDED_LEVEL_XP.put(63, 368599);
        EXTENDED_LEVEL_XP.put(64, 407015);
        EXTENDED_LEVEL_XP.put(65, 449428);
        EXTENDED_LEVEL_XP.put(66, 496254);
        EXTENDED_LEVEL_XP.put(67, 547953);
        EXTENDED_LEVEL_XP.put(68, 605032);
        EXTENDED_LEVEL_XP.put(69, 668051);
        EXTENDED_LEVEL_XP.put(70, 737627);
        EXTENDED_LEVEL_XP.put(71, 814445);
        EXTENDED_LEVEL_XP.put(72, 899257);
        EXTENDED_LEVEL_XP.put(73, 992895);
        EXTENDED_LEVEL_XP.put(74, 1096278);
        EXTENDED_LEVEL_XP.put(75, 1210421);
        EXTENDED_LEVEL_XP.put(76, 1336443);
        EXTENDED_LEVEL_XP.put(77, 1475581);
        EXTENDED_LEVEL_XP.put(78, 1629200);
        EXTENDED_LEVEL_XP.put(79, 1798808);
        EXTENDED_LEVEL_XP.put(80, 1986068);
        EXTENDED_LEVEL_XP.put(81, 2192818);
        EXTENDED_LEVEL_XP.put(82, 2421087);
        EXTENDED_LEVEL_XP.put(83, 2673114);
        EXTENDED_LEVEL_XP.put(84, 2951373);
        EXTENDED_LEVEL_XP.put(85, 3258594);
        EXTENDED_LEVEL_XP.put(86, 3597792);
        EXTENDED_LEVEL_XP.put(87, 3972294);
        EXTENDED_LEVEL_XP.put(88, 4385776);
        EXTENDED_LEVEL_XP.put(89, 4842295);
        EXTENDED_LEVEL_XP.put(90, 5346332);
        EXTENDED_LEVEL_XP.put(91, 5902831);
        EXTENDED_LEVEL_XP.put(92, 6517253);
        EXTENDED_LEVEL_XP.put(93, 7195629);
        EXTENDED_LEVEL_XP.put(94, 7944614);
        EXTENDED_LEVEL_XP.put(95, 8771558);
        EXTENDED_LEVEL_XP.put(96, 9684577);
        EXTENDED_LEVEL_XP.put(97, 10692629);
        EXTENDED_LEVEL_XP.put(98, 11805606);
        EXTENDED_LEVEL_XP.put(99, 13034431);
        EXTENDED_LEVEL_XP.put(100, 14391160);
        EXTENDED_LEVEL_XP.put(101, 15889109);
        EXTENDED_LEVEL_XP.put(102, 17542976);
        EXTENDED_LEVEL_XP.put(103, 19368992);
        EXTENDED_LEVEL_XP.put(104, 21385073);
        EXTENDED_LEVEL_XP.put(105, 23611006);
        EXTENDED_LEVEL_XP.put(106, 26068632);
        EXTENDED_LEVEL_XP.put(107, 28782069);
        EXTENDED_LEVEL_XP.put(108, 31777943);
        EXTENDED_LEVEL_XP.put(109, 35085654);
        EXTENDED_LEVEL_XP.put(110, 38737661);
        EXTENDED_LEVEL_XP.put(111, 42769801);
        EXTENDED_LEVEL_XP.put(112, 47221641);
        EXTENDED_LEVEL_XP.put(113, 52136869);
        EXTENDED_LEVEL_XP.put(114, 57563718);
        EXTENDED_LEVEL_XP.put(115, 63555443);
        EXTENDED_LEVEL_XP.put(116, 70170840);
        EXTENDED_LEVEL_XP.put(117, 77474828);
        EXTENDED_LEVEL_XP.put(118, 85539082);
        EXTENDED_LEVEL_XP.put(119, 94442737);
        EXTENDED_LEVEL_XP.put(120, 104273167);
        EXTENDED_LEVEL_XP.put(121, 115126838);
        EXTENDED_LEVEL_XP.put(122, 127110260);
        EXTENDED_LEVEL_XP.put(123, 140341028);
        EXTENDED_LEVEL_XP.put(124, 154948977);
        EXTENDED_LEVEL_XP.put(125, 171077457);
        EXTENDED_LEVEL_XP.put(126, 188884740);
        EXTENDED_LEVEL_XP.put(127, 200000000);
    }

    public static int getExperienceAt(int level) {
        return EXTENDED_LEVEL_XP.getOrDefault(level, EXTENDED_LEVEL_XP.get(127));
    }

    public static void displayXPGained(Skills skill, int startingXP) {
        int currentXP = skill.getSkill().getExperience();
        int xpGained = currentXP - startingXP;

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        String formattedXpGained = numberFormat.format(xpGained);

        ImGui.Text("XP Gained: " + formattedXpGained);
    }

    public static void displayXpPerHour(Skills skill, int startingXP) {
        long elapsedTime = System.currentTimeMillis() - scriptStartTime;
        double hoursElapsed = elapsedTime / (1000.0 * 60 * 60);
        int currentXP = skill.getSkill().getExperience();
        int xpGained = currentXP - startingXP;
        double xpPerHour = hoursElapsed > 0 ? xpGained / hoursElapsed : 0;

        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        String formattedXpPerHour = numberFormat.format(xpPerHour);

        ImGui.Text("XP Per Hour: " + formattedXpPerHour);
    }

    public static void displayTimeRunning() {
        long elapsedTimeMillis = System.currentTimeMillis() - scriptStartTime;
        long elapsedSeconds = elapsedTimeMillis / 1000;
        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;
        String timeRunningFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        ImGui.Text(timeRunningFormatted);
    }

    public static void displayXpProgressBar(Skills skill) {
        int currentXP = skill.getSkill().getExperience();
        int currentLevel = getLevelFromXP(currentXP);
        int xpForNextLevel = getExperienceAt(currentLevel + 1);
        int xpForCurrentLevel = getExperienceAt(currentLevel);
        int xpToNextLevel = xpForNextLevel - xpForCurrentLevel;
        int xpGainedTowardsNextLevel = currentXP - xpForCurrentLevel;
        float progress = (float) xpGainedTowardsNextLevel / xpToNextLevel;

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
        ImGui.ProgressBar(String.format("%.2f%%", progress * 100), progress, 284, 25);
        ImGui.PopStyleColor(2);
    }

    public static void displayTimeTillNextLevel(Skills skill, int startingXP) {
        int currentXP = skill.getSkill().getExperience();
        int currentLevel = getLevelFromXP(currentXP);
        int xpForNextLevel = getExperienceAt(currentLevel + 1);
        int xpForCurrentLevel = getExperienceAt(currentLevel);
        int xpGainedTowardsNextLevel = currentXP - xpForCurrentLevel;
        int xpRemaining = xpForNextLevel - currentXP;

        // Calculate total XP gained since the script started
        int totalXPGained = currentXP - startingXP;

        long timeElapsedMillis = System.currentTimeMillis() - scriptStartTime;
        double hoursElapsed = timeElapsedMillis / (1000.0 * 60 * 60);

        if (totalXPGained > 0 && hoursElapsed > 0) {
            double xpPerHour = totalXPGained / hoursElapsed;
            double hoursToLevel = xpRemaining / xpPerHour;

            long totalSecondsToLevel = (long) (hoursToLevel * 3600);
            long hours = totalSecondsToLevel / 3600;
            long minutes = (totalSecondsToLevel % 3600) / 60;
            long seconds = totalSecondsToLevel % 60;

            String timeToLevelFormatted = String.format("Time to level: %02d:%02d:%02d", hours, minutes, seconds);
            ImGui.Text(timeToLevelFormatted);
        } else {
            ImGui.Text("Time to level: calculating...");
        }
    }

    public static void displayCurrentLevelAndXP(Skills skill, int startingXP) {
        int currentXP = skill.getSkill().getExperience();
        int startingLevel = getLevelFromXP(startingXP); // Determine starting level based on starting XP
        int currentLevel = getLevelFromXP(currentXP);
        int levelsGained = currentLevel - startingLevel;

        ImGui.Text("Current Level: " + currentLevel + " (+" + levelsGained + " levels gained)");

        int xpForNextLevel = getExperienceAt(currentLevel + 1);
        int xpTillNextLevel = xpForNextLevel - currentXP;

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        String formattedXpTillNextLevel = numberFormat.format(xpTillNextLevel);

        ImGui.Text("XP remaining: " + formattedXpTillNextLevel);
    }

    public static int getLevelFromXP(int xp) {
        //logError("getLevelFromXP called with xp: " + xp);
        for (int level = 1; level <= 127; level++) {
            if (xp < EXTENDED_LEVEL_XP.get(level)) {
                //logError("Returning level: " + (level - 1));
                return level - 1;
            }
        }
        //logError("Returning max level: 120");
        return 127; // Max level
    }

    public static HashMap<String, Integer> inventoryMap = new HashMap<>();

    public static long startTime;  // Track when the skill tracking started

    public static void startTracking() {
        startTime = System.currentTimeMillis();  // Capture the start time when tracking begins
    }

    public static void displaySkillStats(Skills skill, int startingLevel, int startingXP, Map<String, Integer> inventoryMap) {
        displayCurrentLevelAndXP(skill, startingXP);

        displayXPGained(skill, startingXP);
        displayXpPerHour(skill, startingXP);
        displayTimeTillNextLevel(skill, startingXP);

        ImGui.SeparatorText("Progress Bar");
//        displayXpProgressBar(skill);

        ImGui.SeparatorText("Items Gathered");

        // Calculate time elapsed in hours
        double hoursElapsed = (System.currentTimeMillis() - startTime) / 3600000.0;

        inventoryMap.forEach((itemName, count) -> {
            int perHour = (int) (count / hoursElapsed);  // Calculate the per-hour rate
            ImGui.Text(itemName + ": " + count + " (" + perHour + " per hour)");
        });
    }
}
