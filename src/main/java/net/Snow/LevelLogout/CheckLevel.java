package net.Snow.LevelLogout;

import net.botwithus.rs3.game.skills.Skill;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.getLevelFromXP;
import static net.Snow.Snow.shutdown;
import static net.botwithus.rs3.imgui.ImGui.SetTooltip;


public class CheckLevel {

    public static boolean checkThreshold = false;
    public static Map<Skill, Integer> skillThresholds = new HashMap<>();
    private static final Map<Integer, String> skillNames = new HashMap<>();
    static {
        skillNames.put(0, "Attack");
        skillNames.put(1, "Defense");
        skillNames.put(2, "Strength");
        skillNames.put(3, "Hitpoints");
        skillNames.put(4, "Ranged");
        skillNames.put(5, "Prayer");
        skillNames.put(6, "Magic");
        skillNames.put(7, "Cooking");
        skillNames.put(8, "Woodcutting");
        skillNames.put(9, "Fletching");
        skillNames.put(10, "Fishing");
        skillNames.put(11, "Firemaking");
        skillNames.put(12, "Crafting");
        skillNames.put(13, "Smithing");
        skillNames.put(14, "Mining");
        skillNames.put(15, "Herblore");
        skillNames.put(16, "Agility");
        skillNames.put(17, "Thieving");
        skillNames.put(18, "Slayer");
        skillNames.put(19, "Farming");
        skillNames.put(20, "Runecrafting");
        skillNames.put(21, "Hunter");
        skillNames.put(22, "Construction");
        skillNames.put(23, "Summoning");
        skillNames.put(24, "Dungeoneering");
        skillNames.put(25, "Divination");
        skillNames.put(26, "Invention");
        skillNames.put(27, "Archaeology");
        skillNames.put(28, "Necromancy");
    }

    public static List<Skill> getAllSkills() {
        List<Skill> skills = new ArrayList<>();
        for (Integer id : skillNames.keySet()) {
            skills.add(new Skill(Skills.values()[id]));
        }
        return skills;
    }


   public static void checkLevelForSkill(Skill skill) {
        int currentXP = skill.getExperience();
        int currentLevel = getLevelFromXP(currentXP);
        int threshold = getSkillThreshold(skill);

        logPurple("Checking level for skill: " + getSkillName(skill));
        logPurple("Current XP: " + currentXP);
        logPurple("Current level: " + currentLevel);
        logPurple("Threshold: " + threshold);

        if (currentLevel >= threshold && checkThreshold) {
            logError("You have reached: " + threshold + " in " + getSkillName(skill) + ". logging off");
            shutdown();
        } else {
            logPurple("No action taken. Current level: " + currentLevel + ", Threshold: " + threshold);
        }
    }

   public static void skillThresholdGraphics(Skill skill) {
        if (checkThreshold) {
            ImGui.SeparatorText("Set " + getSkillName(skill) + " Level Threshold");
            ImGui.SetItemWidth(280);

            // Get the current skill threshold
            int displayedSkillThreshold = getSkillThreshold(skill);

            // Use the ImGui slider to adjust the skill threshold
            int newDisplayedSkillThreshold = ImGui.Slider("##" + getSkillName(skill) + "Threshold", displayedSkillThreshold, 1, 120, 1);

            // Check if the skill threshold has changed and set the new value
            if (newDisplayedSkillThreshold != displayedSkillThreshold) {
                setSkillThreshold(skill, newDisplayedSkillThreshold);
                int currentLevel = getLevelFromXP(skill.getExperience());
                logBlue("User set required " + getSkillName(skill) + " level to: " + newDisplayedSkillThreshold + "." + " current level: " + currentLevel + "." + " Current XP: " + skill.getExperience());
            }

            SetTooltip("Set the required " + getSkillName(skill) + " level within the range 1 to 120");
        }
    }

    // Getter for the skill threshold
    public static int getSkillThreshold(Skill skill) {
        return skillThresholds.getOrDefault(skill, 1);
    }

    // Setter for the skill threshold
    public static void setSkillThreshold(Skill skill, int threshold) {
        skillThresholds.put(skill, threshold);
        logBlue("Set skill threshold for " + getSkillName(skill) + " to: " + threshold);
    }

    public static String getSkillName(Skill skill) {
        return skillNames.getOrDefault(skill.getId(), "Unknown Skill");
    }


//    public static String getSkillName(Skill skill) {
//        switch (skill.getId()) {
//            case 0: return "Attack";
//            case 1: return "Defense";
//            case 2: return "Strength";
//            case 3: return "Hitpoints";
//            case 4: return "Ranged";
//            case 5: return "Prayer";
//            case 6: return "Magic";
//            case 7: return "Cooking";
//            case 8: return "Woodcutting";
//            case 9: return "Fletching";
//            case 10: return "Fishing";
//            case 11: return "Firemaking";
//            case 12: return "Crafting";
//            case 13: return "Smithing";
//            case 14: return "Mining";
//            case 15: return "Herblore";
//            case 16: return "Agility";
//            case 17: return "Thieving";
//            case 18: return "Slayer";
//            case 19: return "Farming";
//            case 20: return "Runecrafting";
//            case 21: return "Hunter";
//            case 22: return "Construction";
//            case 23: return "Summoning";
//            case 24: return "Dungeoneering";
//            case 25: return "Divination";
//            case 26: return "Invention";
//            case 27: return "Archaeology";
//            case 28: return "Necromancy";
//            default: return "Unknown Skill";
//        }
//    }

}
